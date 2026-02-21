package net.ooder.server.httpproxy.core;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSActionContext;
import net.ooder.server.context.MinServerActionContextImpl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.Properties;

public class ConnectionRunnable implements Runnable {
    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, ConnectionRunnable.class);

    protected Server server;
    protected Socket connection;
    protected Properties config;
    protected String scheme;

    public ConnectionRunnable(Server aServer, String aScheme, Socket aConnection, Properties aConnectionConfig) {
        this.scheme = aScheme;
        this.server = aServer;
        this.connection = aConnection;
        this.config = aConnectionConfig;

    }

    public void run() {
        try {
            boolean next = false;
            HttpRequest request = createRequest();
            JDSActionContext.getActionContext().remove();
            JDSActionContext.setContext(new MinServerActionContextImpl(request,request.getOgnlContext()));
            do {
                if (!connection.isClosed() && request.readRequest(connection.getInputStream())) {
                    HttpResponse response = new HttpResponse(request, connection.getOutputStream(), server.getResponseListeners());
                    if (!server.post(request, response)) {
                        response.sendError(HttpURLConnection.HTTP_NOT_FOUND, " was not found on this server.");
                    }
                    next = response.isKeepAlive();
                    if (!next) {
                        response.addHeader("Connection", "close");
                    }
                    response.commitResponse();

                } else {
                    //log.info("No request sent.  Closing connection.");
                    next = false;
                }
            } while (next);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Handler threw an exception.", e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected HttpRequest createRequest() throws IOException {
        return new HttpRequest(scheme, connection, config);
    }
}
