package net.ooder.server.httpproxy.nioproxy;

import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

public class ProxyOutgoingConnectionReuseStrategy extends DefaultConnectionReuseStrategy {

    @Override
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        NHttpConnection conn = (NHttpConnection) context.getAttribute(
                HttpCoreContext.HTTP_CONNECTION);
        if (conn == null || !conn.isOpen()) {
            return false;
        }
        boolean keepAlive = super.keepAlive(response, context);
        return keepAlive;
    }

}