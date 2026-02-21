package net.ooder.server.httpproxy.core;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSocketEndPoint implements EndPoint, Runnable {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, ServerSocketEndPoint.class);

    private static final ConfigOption PORT_OPTION = new ConfigOption("port", "8082", "HTTP server port.");
    private static final ConfigOption RESOLVE_HOSTNAME_OPTION = new ConfigOption("resolveHostName", "false", "Resolve host names");

    protected ServerSocketFactory factory;
    protected ServerSocket socket;
    protected Server server;
    protected String endpointName;
    protected boolean resolveHostName;

    public ServerSocketEndPoint() {
        factory = ServerSocketFactory.getDefault();
    }

    public void initialize(String name, Server server) throws IOException {
        this.endpointName = name;
        this.server = server;
        resolveHostName = RESOLVE_HOSTNAME_OPTION.getBoolean(server, endpointName).booleanValue();
    }

    public String getName() {
        return endpointName;
    }

    protected ServerSocket createSocket(int port) throws IOException {
        ServerSocket socket = factory.createServerSocket(port);
        return socket;
    }


    public void start() throws IOException {

        this.socket = createSocket(PORT_OPTION.getInteger(server, endpointName).intValue());
        log.info( "Socket listening on port " + socket.getLocalPort());
        Thread thread = new Thread(this, endpointName + "[" + socket.getLocalPort() + "] ServerSocketEndPoint");
        thread.setDaemon(true);
        thread.start();
    }

    public void run() {
        try {
            while (true) {
                Socket client = socket.accept();
                Properties config = new ChainableProperties(server.getConfig());
                Runnable runnable = createRunnable(client, config);
                if (resolveHostName) {
                    // after resolving, the host name appears Socket.toString.
                    InetAddress clientAddress = client.getInetAddress();
                    clientAddress.getHostName();
                }

                    log.debug("Connection from: " + client.toString());


                server.post(runnable);

            }
        } catch (IOException e) {
            log.warn( "IOException ignored", e);
        }
    }

    private String getHost(Socket socket) {
        String host = server.getProperty("host");
        if (host != null) return host;
        return socket.getLocalAddress().getHostName();
    }

    protected String getProtocol() {
        return "http";
    }

    //    protected synchronized Runnable createRunnable(Socket client, Properties config) throws IOException {
//        ConnectionRunnable runnable = new ConnectionRunnable(server, getProtocol(), client, config);
//        return runnable;
//    }
    protected Runnable createRunnable(Socket client, Properties config) throws IOException {
        ConnectionRunnable runnable = new ConnectionRunnable(server, getProtocol(), client, config);
        return runnable;
    }

    public void shutdown(Server server) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
