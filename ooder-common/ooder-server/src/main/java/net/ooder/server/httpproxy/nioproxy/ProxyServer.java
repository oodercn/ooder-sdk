package net.ooder.server.httpproxy.nioproxy;

import net.ooder.config.UserBean;
import net.ooder.server.JDSServer;
import net.ooder.server.httpproxy.ServerProxyFactory;
import net.ooder.server.httpproxy.nioproxy.handle.HeadReponse;
import net.ooder.server.httpproxy.nioproxy.handle.ProxyClientProtocolHandler;
import net.ooder.server.httpproxy.nioproxy.handle.ProxyRequestHandler;
import net.ooder.server.httpproxy.nioproxy.handle.ProxyServiceHandler;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.DefaultHttpClientIODispatch;
import org.apache.http.impl.nio.DefaultHttpServerIODispatch;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;


public class ProxyServer {

    private DefaultConnectingIOReactor connectingIOReactor;

    private DefaultHttpClientIODispatch connectingEventDispatch;

    private DefaultListeningIOReactor listeningIOReactor;

    private DefaultHttpServerIODispatch listeningEventDispatch;

    private int port;

    public int sotimeout = 120000;//

    public int connectiontimeout = 60000;

    public int maxTol = 1000;//

    public int maxPerRoute = 500;


    private UriHttpAsyncRequestHandlerMapper handlerRegistry;


    public ProxyServer(int port) throws IOReactorException {
        this.port = port;
        HttpProcessor inhttpproc = new ImmutableHttpProcessor(
                new ResponseDate(),
                new ResponseServer("JDS-ESD Server"), new ResponseContent(),
                new ResponseConnControl(), new HeadReponse(), new ResponseProcessCookies());
        //
        HttpProcessor outhttpproc = new ImmutableHttpProcessor(
                new RequestContent(), new RequestClientConnControl(),
                new RequestTargetHost(), new RequestConnControl(), new RequestAddCookies(),

                //new RequestUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36"),
                new RequestExpectContinue());

        IOReactorConfig config = IOReactorConfig.custom().setSoTimeout(sotimeout).setConnectTimeout(connectiontimeout).build();
        this.connectingIOReactor = new DefaultConnectingIOReactor(config);
        this.listeningIOReactor = new DefaultListeningIOReactor(config);
        ProxyConnPool connPool = new ProxyConnPool(connectingIOReactor, ConnectionConfig.DEFAULT);
        connPool.setMaxTotal(maxTol);
        connPool.setDefaultMaxPerRoute(maxPerRoute);
        ProxyClientProtocolHandler clientHandler = new ProxyClientProtocolHandler();
        HttpAsyncRequester executor = new HttpAsyncRequester(outhttpproc,
                new ProxyOutgoingConnectionReuseStrategy());
        this.handlerRegistry = new UriHttpAsyncRequestHandlerMapper();
        handlerRegistry.register("*", new ProxyRequestHandler(executor, connPool));

        ProxyServiceHandler serviceHandler = new ProxyServiceHandler(
                inhttpproc, new ProxyIncomingConnectionReuseStrategy(),
                handlerRegistry);

        this.connectingEventDispatch = new DefaultHttpClientIODispatch(
                clientHandler, ConnectionConfig.DEFAULT);

        this.listeningEventDispatch = new DefaultHttpServerIODispatch(
                serviceHandler, ConnectionConfig.DEFAULT);


    }

    public void start() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    connectingIOReactor.execute(connectingEventDispatch);
                } catch (InterruptedIOException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        listeningIOReactor.shutdown();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }

        });
        t.start();

        try {
            listeningIOReactor.listen(new InetSocketAddress(port));
            listeningIOReactor.execute(listeningEventDispatch);
        } catch (InterruptedIOException ex) {

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                connectingIOReactor.shutdown();
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public void stop() {
        try {
            connectingIOReactor.shutdown();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }


    public DefaultHttpClientIODispatch getConnectingEventDispatch() {
        return connectingEventDispatch;
    }

    public void setConnectingEventDispatch(
            DefaultHttpClientIODispatch connectingEventDispatch) {
        this.connectingEventDispatch = connectingEventDispatch;
    }

    public DefaultConnectingIOReactor getConnectingIOReactor() {
        return connectingIOReactor;
    }

    public void setConnectingIOReactor(
            DefaultConnectingIOReactor connectingIOReactor) {
        this.connectingIOReactor = connectingIOReactor;
    }

    public int getConnectiontimeout() {
        return connectiontimeout;
    }

    public void setConnectiontimeout(int connectiontimeout) {
        this.connectiontimeout = connectiontimeout;
    }

    public DefaultHttpServerIODispatch getListeningEventDispatch() {
        return listeningEventDispatch;
    }

    public void setListeningEventDispatch(
            DefaultHttpServerIODispatch listeningEventDispatch) {
        this.listeningEventDispatch = listeningEventDispatch;
    }

    public DefaultListeningIOReactor getListeningIOReactor() {
        return listeningIOReactor;
    }

    public void setListeningIOReactor(DefaultListeningIOReactor listeningIOReactor) {
        this.listeningIOReactor = listeningIOReactor;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSotimeout() {
        return sotimeout;
    }

    public void setSotimeout(int sotimeout) {
        this.sotimeout = sotimeout;
    }

    public UriHttpAsyncRequestHandlerMapper getHandlerRegistry() {
        return handlerRegistry;
    }

    public void setHandlerRegistry(UriHttpAsyncRequestHandlerMapper handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public int getMaxTol() {
        return maxTol;
    }

    public void setMaxTol(int maxTol) {
        this.maxTol = maxTol;
    }

    public static void main(String[] args) throws Exception {
        JDSServer.getInstance();
        ServerProxyFactory.getInstance();
        Integer port = UserBean.getInstance().getProxyPort();
        new ProxyServer(port).start();
    }

}
