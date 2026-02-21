package net.ooder.sdk.skill.driver;

public class EndpointConfig {
    private String url;
    private String protocol;
    private int timeout;
    private String apiKey;
    private String secret;
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
}