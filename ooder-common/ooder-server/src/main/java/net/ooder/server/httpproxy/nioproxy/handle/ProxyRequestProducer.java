package net.ooder.server.httpproxy.nioproxy.handle;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProxyRequestProducer implements HttpAsyncRequestProducer {

    private final ProxyHttpExchange httpExchange;

    public ProxyRequestProducer(final ProxyHttpExchange httpExchange) {
        super();
        this.httpExchange = httpExchange;
    }

    public void close() {
    }

    public HttpHost getTarget() {
        synchronized (this.httpExchange) {
            return this.httpExchange.getTarget();
        }
    }

    public HttpRequest generateRequest() {
        synchronized (this.httpExchange) {
            HttpRequest request = this.httpExchange.getRequest();
            if (request instanceof HttpEntityEnclosingRequest) {
                BasicHttpEntityEnclosingRequest closeRequest = new BasicHttpEntityEnclosingRequest(
                        request.getRequestLine());

                Header[] exHeaders = request.getAllHeaders();
                for (Header header : exHeaders) {
                    String name = header.getName().toLowerCase();
                    if (!name.equals("Transfer-encoding".toLowerCase())
                            && !name.equals("Content-Length".toLowerCase())) {
                        closeRequest.addHeader(header);
                    }
                }
                closeRequest.setEntity(((HttpEntityEnclosingRequest) request).getEntity());
                return closeRequest;
            } else {
                BasicHttpRequest basicHttpRequest = new BasicHttpRequest(request.getRequestLine());
                Header[] exHeaders = request.getAllHeaders();
                for (Header header : exHeaders) {
                    String name = header.getName().toLowerCase();
                    if (!name.equals("Transfer-encoding".toLowerCase())
                            && !name.equals("Content-Length".toLowerCase())) {
                        basicHttpRequest.addHeader(header);
                    }
                }
                return basicHttpRequest;
            }
        }
    }

    public void produceContent(
            final ContentEncoder encoder, final IOControl ioctrl) throws IOException {
        synchronized (this.httpExchange) {
            this.httpExchange.setOriginIOControl(ioctrl);
            ByteBuffer buf = this.httpExchange.getInBuffer();
            buf.flip();
            int n = encoder.write(buf);
            buf.compact();

            if (buf.hasRemaining() && !this.httpExchange.isRequestReceived()) {
                if (this.httpExchange.getClientIOControl() != null) {
                    this.httpExchange.getClientIOControl().requestInput();
                }
            }
            if (buf.position() == 0) {
                if (this.httpExchange.isRequestReceived()) {
                    encoder.complete();
                } else {
                    ioctrl.suspendOutput();
                }
            }
        }
    }

    public void requestCompleted(final HttpContext context) {
        synchronized (this.httpExchange) {

        }
    }

    public boolean isRepeatable() {
        return false;
    }

    public void resetRequest() {
    }

    public void failed(final Exception ex) {
    }

}