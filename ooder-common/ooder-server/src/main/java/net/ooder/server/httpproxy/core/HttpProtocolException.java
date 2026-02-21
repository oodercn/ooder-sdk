package net.ooder.server.httpproxy.core;

import java.net.ProtocolException;

public class HttpProtocolException extends ProtocolException {
    int statusCode;

    public HttpProtocolException( int statusCode, String message ) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusPhrase() {
        return Http.getStatusPhrase( statusCode );
    }

    public String toString() {
        return getClass().getName() + ": " + statusCode + " " + getMessage();
    }
}
