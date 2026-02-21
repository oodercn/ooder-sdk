package net.ooder.server.httpproxy.core;

import java.net.Socket;
import java.util.Properties;

public abstract class Request {

    private Properties requestProperties;
    private boolean isInternal = false;
    protected Socket connection;

    public Request(Socket connection, Properties serverConfig) {
        this.connection = connection;
        this.requestProperties = new ChainableProperties( serverConfig );
    }

    public String getProperty(String key, String defaultValue ) {
        return requestProperties.getProperty( key, defaultValue );
    }

    public void putProperty(String key, String value) {
        requestProperties.put( key, value );
    }

    public boolean isInternal() {
        return isInternal;
    }

    protected void setIsInternal( boolean isItInternal ) {
        isInternal = isItInternal; 
    }

    public String getLocalAddr() {
        return connection.getLocalAddress().getHostAddress();
    }

    public int getLocalPort() {
        return connection.getLocalPort();
    }

    public String getRemoteAddr() {
        return connection.getInetAddress().getHostAddress();
    }

    public int getRemotePort() {
        return connection.getPort();
    }
}
