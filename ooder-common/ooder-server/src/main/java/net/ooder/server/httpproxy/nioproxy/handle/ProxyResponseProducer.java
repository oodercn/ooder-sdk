package net.ooder.server.httpproxy.nioproxy.handle;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncResponseProducer;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProxyResponseProducer implements HttpAsyncResponseProducer {

    private final ProxyHttpExchange httpExchange;

    public ProxyResponseProducer(final ProxyHttpExchange httpExchange) {
        super();
        this.httpExchange = httpExchange;
    }

    public void close() {
        this.httpExchange.reset();
    }

    public HttpResponse generateResponse() {
        synchronized (this.httpExchange) {
            HttpResponse response = this.httpExchange.getResponse();
         
            BasicHttpResponse r = new BasicHttpResponse(response.getStatusLine());
            r.setEntity(response.getEntity());
            return r;
        }
    }

    public void produceContent(
            final ContentEncoder encoder, final IOControl ioctrl) throws IOException {
        synchronized (this.httpExchange) {
            this.httpExchange.setClientIOControl(ioctrl);
            // Send data to the client
            ByteBuffer buf = this.httpExchange.getOutBuffer();
            buf.flip();
            int n = encoder.write(buf);
            buf.compact();
          
            // transferred, make sure the origin is sending more data
            if (buf.hasRemaining() && !this.httpExchange.isResponseReceived()) {
                if (this.httpExchange.getOriginIOControl() != null) {
                    this.httpExchange.getOriginIOControl().requestInput();
                    
                }
            }
            if (buf.position() == 0) {
                if (this.httpExchange.isResponseReceived()) {
                    encoder.complete();
                   
                } else {
                    // Input buffer is empty. Wait until the origin fills up
                    // the buffer
                    ioctrl.suspendOutput();
                   
                }
            }
        }
    }

    public void responseCompleted(final HttpContext context) {
        synchronized (this.httpExchange) {
         
        }
    }

    public void failed(final Exception ex) {
      
    }

}