package net.ooder.server.httpproxy.nioproxy.handle;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.Locale;

public class ProxyResponseConsumer implements HttpAsyncResponseConsumer<ProxyHttpExchange> {

    private final ProxyHttpExchange httpExchange;

    private volatile boolean completed;

    public ProxyResponseConsumer(final ProxyHttpExchange httpExchange) {
        super();
        this.httpExchange = httpExchange;
    }

    public void close() {
    }

    public void responseReceived(final HttpResponse response) {
        synchronized (this.httpExchange) {
 
            this.httpExchange.setResponse(response);
            HttpAsyncExchange responseTrigger = this.httpExchange.getResponseTrigger();
            if (responseTrigger != null && !responseTrigger.isCompleted()) {
               
                responseTrigger.submitResponse(new ProxyResponseProducer(this.httpExchange));
            }
        }
    }

    public void consumeContent(
            final ContentDecoder decoder, final IOControl ioctrl) throws IOException {
        synchronized (this.httpExchange) {
            this.httpExchange.setOriginIOControl(ioctrl);
         
            ByteBuffer buf = this.httpExchange.getOutBuffer();
            int n = decoder.read(buf);
    
            if (decoder.isCompleted()) {
               
            }
             if (!buf.hasRemaining()) {
                ioctrl.suspendInput();
            }
              if (buf.position() > 0) {
                if (this.httpExchange.getClientIOControl() != null) {
                    this.httpExchange.getClientIOControl().requestOutput();
                    
                }
            }
        }
    }

    public void responseCompleted(final HttpContext context) {
        synchronized (this.httpExchange) {
            if (this.completed) {
                return;
            }
            this.completed = true;

            this.httpExchange.setResponseReceived();
            if (this.httpExchange.getClientIOControl() != null) {
                this.httpExchange.getClientIOControl().requestOutput();
              
            }
        }
    }

    public void failed(final Exception ex) {
        synchronized (this.httpExchange) {
            if (this.completed) {
                return;
            }
            this.completed = true;
            this.httpExchange.setException(ex);
            HttpAsyncExchange responseTrigger = this.httpExchange.getResponseTrigger();
            if (responseTrigger != null && !responseTrigger.isCompleted()) {
               
                int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
                HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_0, status,
                        EnglishReasonPhraseCatalog.INSTANCE.getReason(status, Locale.US));
                String message = ex.getMessage();
                if (message == null) {
                    message = "Unexpected error";
                }
                response.setEntity(new NStringEntity(message, ContentType.DEFAULT_TEXT));
                responseTrigger.submitResponse(new BasicAsyncResponseProducer(response));
            }
        }
    }

    public boolean cancel() {
        synchronized (this.httpExchange) {
            if (this.completed) {
                return false;
            }
            failed(new InterruptedIOException("Cancelled"));
            return true;
        }
    }

    public ProxyHttpExchange getResult() {
        return this.httpExchange;
    }

    public Exception getException() {
        return null;
    }

    public boolean isDone() {
        return this.completed;
    }

}