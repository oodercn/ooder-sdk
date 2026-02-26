package net.ooder.scene.skills.ai;

import java.util.List;

/**
 * MCP 客户端信息
 */
public class MCPClientInfo {

    private String clientId;
    private String name;
    private MCPConfig.TransportType transportType;
    private boolean connected;
    private List<String> availableTools;
    private long lastPingTime;

    // Getters and Setters
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MCPConfig.TransportType getTransportType() { return transportType; }
    public void setTransportType(MCPConfig.TransportType transportType) { this.transportType = transportType; }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public List<String> getAvailableTools() { return availableTools; }
    public void setAvailableTools(List<String> availableTools) { this.availableTools = availableTools; }

    public long getLastPingTime() { return lastPingTime; }
    public void setLastPingTime(long lastPingTime) { this.lastPingTime = lastPingTime; }
}
