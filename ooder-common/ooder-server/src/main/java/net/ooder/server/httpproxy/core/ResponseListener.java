package net.ooder.server.httpproxy.core;

import java.io.IOException;

public interface ResponseListener {

    public void startTransfer(HttpRequest request);

    public void notify(HttpRequest request, int bytesSent, int totalLength) throws IOException;

    public void endTransfer(HttpRequest request, Exception e);

}
