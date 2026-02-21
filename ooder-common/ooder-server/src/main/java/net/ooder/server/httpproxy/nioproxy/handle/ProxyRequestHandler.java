package net.ooder.server.httpproxy.nioproxy.handle;


import net.ooder.server.httpproxy.ServerProxyFactory;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.HttpContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ProxyRequestHandler implements HttpAsyncRequestHandler<ProxyHttpExchange> {


    private final HttpAsyncRequester executor;
    private final BasicNIOConnPool connPool;
    private final AtomicLong counter;
    Map<String, HttpHost> hostMap = new HashMap<String, HttpHost>();

    public ProxyRequestHandler(
            final HttpAsyncRequester executor,
            final BasicNIOConnPool connPool) {
        super();
        this.executor = executor;
        this.connPool = connPool;
        this.counter = new AtomicLong(1);
    }


    public HttpAsyncRequestConsumer<ProxyHttpExchange> processRequest(
            final HttpRequest request,
            final HttpContext context) {
        ProxyHttpExchange httpExchange = (ProxyHttpExchange) context.getAttribute("http-exchange");
        ServerProxyFactory factory = ServerProxyFactory.getInstance();
        HttpHost target = factory.getProxyHost(request);
        if (httpExchange == null || httpExchange.getException() != null) {
            httpExchange = new ProxyHttpExchange();
            context.setAttribute("http-exchange", httpExchange);
        } else {
            httpExchange.reset();
        }
        String id = String.format("%08X", this.counter.getAndIncrement());
        httpExchange.setId(id);
        httpExchange.setTarget(target);
        return new ProxyRequestConsumer(httpExchange, this.executor, this.connPool);
    }

    public void handle(
            final ProxyHttpExchange httpExchange,
            final HttpAsyncExchange responseTrigger,
            final HttpContext context) {
        synchronized (httpExchange) {
            Exception ex = httpExchange.getException();
            if (ex != null) {


                int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
                HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, status,
                        EnglishReasonPhraseCatalog.INSTANCE.getReason(status, Locale.CHINESE));
                String message = ex.getMessage();
                if (message == null || message.equals("")) {
                    message = "Unexpected error";
                }
                response.setEntity(new NStringEntity(message, ContentType.DEFAULT_TEXT));
                responseTrigger.submitResponse(new BasicAsyncResponseProducer(response));
                httpExchange.setResponse(response);
                httpExchange.setResponseTrigger(responseTrigger);
            } else {
                HttpResponse response = httpExchange.getResponse();
                if (response != null) {
                    responseTrigger.submitResponse(new ProxyResponseProducer(httpExchange));
                }
                httpExchange.setResponseTrigger(responseTrigger);
            }

        }
    }

}