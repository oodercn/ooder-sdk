package net.ooder.scene.provider.model.agent;

public class NetworkStatusData {
    
    private boolean online;
    private long latency;
    private long bandwidth;
    private String connectionType;
    private String ipAddress;
    private int activeConnections;
    
    public boolean isOnline() {
        return online;
    }
    
    public void setOnline(boolean online) {
        this.online = online;
    }
    
    public long getLatency() {
        return latency;
    }
    
    public void setLatency(long latency) {
        this.latency = latency;
    }
    
    public long getBandwidth() {
        return bandwidth;
    }
    
    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
    }
    
    public String getConnectionType() {
        return connectionType;
    }
    
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public int getActiveConnections() {
        return activeConnections;
    }
    
    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }
}
