package net.ooder.server.httpproxy.nioproxy;

import org.apache.http.HttpHost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.impl.nio.pool.BasicNIOPoolEntry;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.pool.NIOConnFactory;
import org.apache.http.nio.reactor.ConnectingIOReactor;

public class ProxyConnPool extends BasicNIOConnPool {

    public ProxyConnPool(
            final ConnectingIOReactor ioreactor,
            final ConnectionConfig config) {
        super(ioreactor, config);
    }

    public ProxyConnPool(
            final ConnectingIOReactor ioreactor,
            final NIOConnFactory<HttpHost, NHttpClientConnection> connFactory,
            final int connectTimeout) {
        super(ioreactor, connFactory, connectTimeout);
    }

    @Override
    public void release(final BasicNIOPoolEntry entry, boolean reusable) {
      
        super.release(entry, reusable);
       
    }

}