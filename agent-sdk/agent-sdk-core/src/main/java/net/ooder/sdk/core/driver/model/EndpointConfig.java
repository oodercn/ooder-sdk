package net.ooder.sdk.core.driver.model;

import java.util.Map;

public class EndpointConfig {
    
    private String endpoint;
    private String protocol;
    private int timeout = 30000;
    private int retryCount = 3;
    private Map<String, String> headers;
    private Map<String, Object> options;
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    
    public Map<String, Object> getOptions() { return options; }
    public void setOptions(Map<String, Object> options) { this.options = options; }
    
    public static EndpointConfig of(String endpoint) {
        EndpointConfig config = new EndpointConfig();
        config.setEndpoint(endpoint);
        return config;
    }
    
    public static EndpointConfig of(String endpoint, String protocol) {
        EndpointConfig config = of(endpoint);
        config.setProtocol(protocol);
        return config;
    }
}
