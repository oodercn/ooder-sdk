package net.ooder.server.httpproxy.core;

import java.io.IOException;

public interface EndPoint {

    public void initialize(String name, Server server) throws IOException;

    public String getName();

    public void start() throws IOException;

    public void shutdown(Server server);
}
