package net.ooder.scene.protocol;

import java.util.List;

public class DiscoveryRequest {
    private String discoveryType;
    private int timeout;
    private int maxPeers;
    private List<String> peerTypes;
    private String filter;

    public String getDiscoveryType() { return discoveryType; }
    public void setDiscoveryType(String discoveryType) { this.discoveryType = discoveryType; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public int getMaxPeers() { return maxPeers; }
    public void setMaxPeers(int maxPeers) { this.maxPeers = maxPeers; }
    public List<String> getPeerTypes() { return peerTypes; }
    public void setPeerTypes(List<String> peerTypes) { this.peerTypes = peerTypes; }
    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }
}
