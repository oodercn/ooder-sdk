package net.ooder.server.httpproxy.nioproxy.handle;

import org.apache.http.HttpRequest;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProxyRequestConsumer implements HttpAsyncRequestConsumer<ProxyHttpExchange> {

    private final ProxyHttpExchange httpExchange;
    private final HttpAsyncRequester executor;
    private final BasicNIOConnPool connPool;

    private volatile boolean completed;

    public ProxyRequestConsumer(
            final ProxyHttpExchange httpExchange,
            final HttpAsyncRequester executor,
            final BasicNIOConnPool connPool) {
        super();
        this.httpExchange = httpExchange;
        this.executor = executor;
        this.connPool = connPool;
    }

    public void close() {
    }

    public void requestReceived(final HttpRequest request) {
        synchronized (this.httpExchange) {
            this.httpExchange.setRequest(request);
            this.executor.execute(
                    new ProxyRequestProducer(this.httpExchange),
                    new ProxyResponseConsumer(this.httpExchange),
                    this.connPool);
        }
    }

    public void consumeContent(
            final ContentDecoder decoder, final IOControl ioctrl) throws IOException {
        synchronized (this.httpExchange) {
            this.httpExchange.setClientIOControl(ioctrl);

            ByteBuffer buf = this.httpExchange.getInBuffer();
            int n = decoder.read(buf);

            if (decoder.isCompleted()) {
            }

            if (!buf.hasRemaining()) {
                ioctrl.suspendInput();

            }
            if (buf.position() > 0) {
                if (this.httpExchange.getOriginIOControl() != null) {
                    this.httpExchange.getOriginIOControl().requestOutput();

                }
            }
        }
    }

    public void requestCompleted(final HttpContext context) {
        synchronized (this.httpExchange) {
            this.completed = true;
            this.httpExchange.setRequestReceived();
            if (this.httpExchange.getOriginIOControl() != null) {
                this.httpExchange.getOriginIOControl().requestOutput();
            }
        }
    }

    public Exception getException() {
        return null;
    }

    public ProxyHttpExchange getResult() {
        return this.httpExchange;
    }

    public boolean isDone() {
        return this.completed;
    }

    public void failed(final Exception ex) {
        ex.printStackTrace();
    }

}