package net.ooder.server.httpproxy.nioproxy.handle;

import org.apache.http.HttpException;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;

import java.io.IOException;

public class ProxyClientProtocolHandler extends HttpAsyncRequestExecutor {

    public ProxyClientProtocolHandler() {
        super();
    }

    @Override
    protected void log(final Exception ex) {
        //ex.printStackTrace();
    }

    @Override
    public void connected(final NHttpClientConnection conn,
                          final Object attachment) throws IOException, HttpException {

        super.connected(conn, attachment);
    }

    @Override
    public void closed(final NHttpClientConnection conn) {

        super.closed(conn);
    }

}