package net.ooder.server.httpproxy.core;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SingleThreadedHttpEndPoint implements EndPoint, Runnable {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, SingleThreadedHttpEndPoint.class);
    private static final ConfigOption PORT_OPTION = new ConfigOption( "port", "8082", "HTTP server port" );
    private static final ConfigOption BUFFER_SIZE_OPTION = new ConfigOption( "buffersize", "1024", "Read buffer size." );

    private String endpointName;
    private Server server;
    private ByteBuffer byteBuffer;
    private Thread mainThread;
    private int socketPort = 80;

    public void initialize(String name, Server server) throws IOException {
        this.endpointName = name;
        this.server = server;
        try {
            socketPort = PORT_OPTION.getInteger( server, endpointName ).intValue();
        } catch( NumberFormatException e ) {
        }
        int size = 1024;
        try {
            size = BUFFER_SIZE_OPTION.getInteger( server, endpointName ).intValue();
        } catch( NumberFormatException e ) {
        }
        byteBuffer = ByteBuffer.allocateDirect( size );
    }

    public String getName() {
        return endpointName;
    }

    public void start() {
        mainThread = new Thread( this, endpointName + "[" + socketPort + "] ServerSocketEndPoint" );
        mainThread.setDaemon( true );
        mainThread.start();
    }

    public void run() {
        Selector selector = null;
        try {
            selector = createSelector( socketPort );
            boolean keepProcessing = true;
            while( keepProcessing ) {
                keepProcessing = processIncomingConnections(selector);
            }
        } catch (IOException e) {
            logException( Level.SEVERE, e );
        } finally {
            if( selector != null ) {
                try {
                    selector.close();
                } catch (IOException ignore) {
                }
            }
            mainThread = null;
        }
    }

    private boolean processIncomingConnections(Selector selector) {
        try {
            selector.select();
            if( Thread.currentThread().isInterrupted() ) {
                return false;
            }
            Iterator it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                try {
                    handleKey(selector,key);
                } catch( IOException ioe ) {
                    logException( Level.WARNING, ioe );
                    ((DirectionalTransfer)key.attachment()).closeClient();
                } finally {
                    it.remove();
                }
            }
        } catch( Exception e ) {
            logException( Level.SEVERE, e );
        }
        return true;
    }

    private void logException(Level logLevel, Throwable e) {
        LogRecord record = new LogRecord( logLevel, e.getMessage() );
        record.setThrown( e );
        log.error( record );
    }

    public void shutdown(Server server) {
        if( mainThread != null ) {
            mainThread.interrupt();
        }
    }

    private Selector createSelector( int port ) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();
        serverChannel.socket().bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        return selector;
    }

    private void handleKey(Selector selector, SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            acceptNewClient(selector, key);
        } else if (key.isReadable()) {
            readDataFromSocket(key);
        }
    }

    private void acceptNewClient(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Client client = new Client( channel );
        client.out.source().register( selector, SelectionKey.OP_READ, client.getTransferToSocket() );
        channel.register(selector, SelectionKey.OP_READ, client.getTransferToWorker() );
        server.post( new NonBlockingRunnable( server, channel.socket(), client.getTransferToWorker(), client.getTransferToSocket() ) );
    }

    private void readDataFromSocket(SelectionKey key) throws IOException {
        try {
            int count = ((ReadableByteChannel)key.channel()).read(byteBuffer);
            if ( count > 0) {
                byteBuffer.flip();
                DirectionalTransfer direction = (DirectionalTransfer) key.attachment();
                direction.transfer( byteBuffer );
            } else if ( count < 0) {
                ((DirectionalTransfer)key.attachment()).closeClient();
            }
        } finally {
            byteBuffer.clear();
        }
    }

    public interface DirectionalTransfer {
        public void transfer(ByteBuffer data) throws IOException;
        public void closeClient() throws IOException;
    }

    public static class Client {
        SocketChannel channel;
        Pipe in;
        Pipe out;
        TransferToSocket socketTransfer;
        TransferToWorker workerTransfer;

        public Client(SocketChannel aChannel) throws IOException {
            this.channel = aChannel;
            in = Pipe.open();
            out = Pipe.open();
            in.sink().configureBlocking( false );
            out.source().configureBlocking( false );
            socketTransfer = new TransferToSocket( this );
            workerTransfer = new TransferToWorker( this );
        }

        public TransferToSocket getTransferToSocket() {
            return socketTransfer;
        }

        public TransferToWorker getTransferToWorker() {
            return workerTransfer;
        }
    }

    public static class TransferToWorker extends InputStream implements DirectionalTransfer {
        Client client;

        public TransferToWorker(Client client) {
            this.client = client;
        }

        public int read(byte b[]) throws IOException {
            return read( b, 0, b.length );
        }

        public int read(byte b[], int off, int len) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap( b, off, len );
            return client.in.source().read( buffer );
        }

        public int read() throws IOException {
            byte[] byte1 = new byte[1];
            int count = 0;
            while( count == 0 ) {
                count = read( byte1 );
            }

            if( count > 0 ) {
                return (int)byte1[0];
            } else {
                return count;
            }
        }

        public void close() throws IOException {
//            client.in.sink().close();
        }

        public void transfer(ByteBuffer data) throws IOException {
            int count = client.in.sink().write( data );
            if( count == 0 || data.hasRemaining() ) {
                System.out.println("Count: " + count + " remaing: " + data.hasRemaining() );
            }
        }

        public void closeClient() throws IOException {
            client.channel.close();
            client.in.sink().close();
            client.out.source().close();
        }
    }

    public static class TransferToSocket extends OutputStream implements DirectionalTransfer {
        Client client;
        byte[] byte1;

        public TransferToSocket(Client client) {
            this.client = client;
        }

        public void transfer(ByteBuffer data) throws IOException {
            int written = client.channel.write( data );
            if( written == 0 ) {
                System.out.println("Written to socket: " + written );
            }
        }

        public void closeClient() throws IOException {
            client.in.source().close();
            client.out.sink().close();
        }

        public void write(int b) throws IOException {
            if( byte1 == null ) {
                byte1 = new byte[1];
            }
            byte1[0] = (byte)b;
            client.out.sink().write( ByteBuffer.wrap(byte1) );
        }

        public void write(byte b[]) throws IOException {
            write( b, 0, b.length );
        }

        public void write(byte b[], int off, int len) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
            while( buffer.hasRemaining() ) {
                int written = client.out.sink().write( buffer );
                if( written == 0 ) {
                    Thread.yield();
                }
            }
        }

        public void close() throws IOException {
//            client.out.sink().close();
        }
    }
}
