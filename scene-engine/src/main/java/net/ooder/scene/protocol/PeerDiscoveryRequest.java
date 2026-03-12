package net.ooder.scene.protocol;

import java.util.List;

/**
 * P2P 网络发现请求
 *
 * <p>原 DiscoveryRequest 重命名，避免与 discovery/api/DiscoveryRequest 冲突</p>
 *
 * @author Ooder Team
 * @since 2.3
 */
public class PeerDiscoveryRequest {
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
