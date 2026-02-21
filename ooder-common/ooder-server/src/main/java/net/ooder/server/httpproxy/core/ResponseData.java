package net.ooder.server.httpproxy.core;

import java.io.IOException;
import java.io.OutputStream;

public interface ResponseData {
    public long getLength();
    public void send(OutputStream os) throws IOException;
}
