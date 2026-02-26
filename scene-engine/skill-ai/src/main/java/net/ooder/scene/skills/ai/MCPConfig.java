package net.ooder.scene.skills.ai;

import java.util.Map;

/**
 * MCP 客户端配置
 */
public class MCPConfig {

    public enum TransportType {
        STDIO, SSE, HTTP
    }

    private String clientId;
    private String name;
    private TransportType transportType;
    private String command;
    private String url;
    private Map<String, String> env;
    private Map<String, Object> parameters;
    private long timeout;

    // Getters and Setters
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TransportType getTransportType() { return transportType; }
    public void setTransportType(TransportType transportType) { this.transportType = transportType; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Map<String, String> getEnv() { return env; }
    public void setEnv(Map<String, String> env) { this.env = env; }

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }
}
