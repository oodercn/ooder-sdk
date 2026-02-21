package net.ooder.scene.provider.model.network;

public class ConnectionStatus {
    
    private boolean connected;
    private String endpoint;
    private long latency;
    private String connectionType;
    private long connectedAt;
    private long lastActivityAt;
    private int reconnectCount;
    
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public long getLatency() {
        return latency;
    }
    
    public void setLatency(long latency) {
        this.latency = latency;
    }
    
    public String getConnectionType() {
        return connectionType;
    }
    
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
    
    public long getConnectedAt() {
        return connectedAt;
    }
    
    public void setConnectedAt(long connectedAt) {
        this.connectedAt = connectedAt;
    }
    
    public long getLastActivityAt() {
        return lastActivityAt;
    }
    
    public void setLastActivityAt(long lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }
    
    public int getReconnectCount() {
        return reconnectCount;
    }
    
    public void setReconnectCount(int reconnectCount) {
        this.reconnectCount = reconnectCount;
    }
}
