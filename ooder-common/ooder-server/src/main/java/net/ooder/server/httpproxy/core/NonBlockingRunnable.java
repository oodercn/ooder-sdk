package net.ooder.server.httpproxy.core;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NonBlockingRunnable implements Runnable {
    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, NonBlockingRunnable.class);
    Server server;
    Socket socket;
    InputStream is;
    OutputStream os;

    public NonBlockingRunnable(Server server, Socket aSocket, InputStream anIn, OutputStream anOut) {
        this.server = server;
        socket = aSocket;
        is = anIn;
        os = anOut;
    }

    public void run() {
        try {
            boolean next = false;
            do {
                HttpRequest request = new HttpRequest("http", socket, server.getConfig() );
                next = request.readRequest( is );
                if( next ) {
                    HttpResponse response = new HttpResponse( request, os, server.getResponseListeners() );
                    if( !server.post( request, response ) ) {
                        response.sendError( HttpURLConnection.HTTP_NOT_FOUND, " was not found on this server." );
                    }
                    next = response.isKeepAlive();
                    if( !next ) {
                        response.addHeader("Connection", "close" );
                    }
                    response.commitResponse();
                }
            } while( next );
        } catch( EOFException eof ) {
            log.error( "Closing connection" );
            // do nothing
        } catch( IOException e ) {
            log.error(  "IOException", e );
        } catch( Exception e ) {
            log.error("Handler threw an exception.", e );
        } finally {
            try {
                is.close();
            } catch( IOException e ) {
            }

            try {
                os.close();
            } catch( IOException e ) {
            }
        }
    }
}
