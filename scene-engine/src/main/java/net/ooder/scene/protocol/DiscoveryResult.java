package net.ooder.scene.protocol;

import java.util.List;

public class DiscoveryResult {
    private boolean success;
    private String message;
    private List<Peer> peers;
    private int totalFound;
    private long duration;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<Peer> getPeers() { return peers; }
    public void setPeers(List<Peer> peers) { this.peers = peers; }
    public int getTotalFound() { return totalFound; }
    public void setTotalFound(int totalFound) { this.totalFound = totalFound; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}
