package net.ooder.scene.protocol;

import java.util.List;

public class Peer {
    private String peerId;
    private String peerName;
    private String peerType;
    private String address;
    private int port;
    private String status;
    private long lastSeen;
    private long registeredAt;
    private List<String> capabilities;
    private String version;

    public String getPeerId() { return peerId; }
    public void setPeerId(String peerId) { this.peerId = peerId; }
    public String getPeerName() { return peerName; }
    public void setPeerName(String peerName) { this.peerName = peerName; }
    public String getPeerType() { return peerType; }
    public void setPeerType(String peerType) { this.peerType = peerType; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getLastSeen() { return lastSeen; }
    public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }
    public long getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(long registeredAt) { this.registeredAt = registeredAt; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public boolean isOnline() {
        return "ONLINE".equals(status);
    }
}
