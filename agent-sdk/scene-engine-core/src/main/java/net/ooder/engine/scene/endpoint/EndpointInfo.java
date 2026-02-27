package net.ooder.engine.scene.endpoint;

public class EndpointInfo {
    private String endpointId;
    private String endpointType;
    private String host;
    private int port;
    private String protocol;
    private String status;

    public String getEndpointId() { return endpointId; }
    public void setEndpointId(String endpointId) { this.endpointId = endpointId; }
    public String getEndpointType() { return endpointType; }
    public void setEndpointType(String endpointType) { this.endpointType = endpointType; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
