package net.ooder.server.httpproxy.nioproxy.handle;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;

public class HeadReponse implements HttpResponseInterceptor {
    @Override
    public void process(HttpResponse httpResponse, HttpContext httpContext) {

        HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
        ProxyHttpExchange exchange = clientContext.getAttribute("http-exchange", ProxyHttpExchange.class);
        BasicHttpRequest request = clientContext.getAttribute("http.request", BasicHttpRequest.class);

        if (exchange != null && exchange.getResponse() != null) {
            Header[] exHeaders = exchange.getResponse().getAllHeaders();
            for (Header header : exHeaders) {
                String name = header.getName().toLowerCase();
                if (!name.equals("Transfer-encoding".toLowerCase())
                        && !name.equals("Content-Length".toLowerCase())) {
                    httpResponse.setHeader(header);
                }

            }
        }


    }
}
