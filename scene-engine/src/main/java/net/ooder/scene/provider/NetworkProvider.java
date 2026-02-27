package net.ooder.scene.provider;

import net.ooder.scene.core.PageResult;
import java.util.List;

/**
 * 网络Provider接口
 *
 * <p>定义网络管理相关的操作接口，由skill-network实现</p>
 */
public interface NetworkProvider extends BaseProvider {

    /**
     * 获取网络状态
     */
    NetworkStatus getStatus();

    /**
     * 获取网络统计
     */
    NetworkStats getStats();

    /**
     * 列出网络链路
     */
    PageResult<NetworkLink> listLinks(int page, int size);

    /**
     * 获取网络链路
     */
    NetworkLink getLink(String linkId);

    /**
     * 断开链路
     */
    boolean disconnectLink(String linkId);

    /**
     * 重连链路
     */
    boolean reconnectLink(String linkId);

    /**
     * 列出路由
     */
    PageResult<NetworkRoute> listRoutes(int page, int size);

    /**
     * 获取路由
     */
    NetworkRoute getRoute(String routeId);

    /**
     * 查找路由
     */
    NetworkRoute findRoute(String source, String target, String algorithm, int maxHops);

    /**
     * 获取网络拓扑
     */
    NetworkTopology getTopology();

    /**
     * 获取网络质量
     */
    NetworkQuality getQuality();
}

/**
 * 网络状态
 */
class NetworkStatus {
    private String status;
    private String nodeId;
    private String nodeType;
    private boolean online;
    private int connectedPeers;
    private String localAddress;
    private int localPort;
    private long uptime;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
    public int getConnectedPeers() { return connectedPeers; }
    public void setConnectedPeers(int connectedPeers) { this.connectedPeers = connectedPeers; }
    public String getLocalAddress() { return localAddress; }
    public void setLocalAddress(String localAddress) { this.localAddress = localAddress; }
    public int getLocalPort() { return localPort; }
    public void setLocalPort(int localPort) { this.localPort = localPort; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
}

/**
 * 网络统计
 */
class NetworkStats {
    private long totalLinks;
    private long activeLinks;
    private long totalRoutes;
    private long totalBytes;
    private long totalPackets;
    private double avgLatency;
    private double packetLoss;

    public long getTotalLinks() { return totalLinks; }
    public void setTotalLinks(long totalLinks) { this.totalLinks = totalLinks; }
    public long getActiveLinks() { return activeLinks; }
    public void setActiveLinks(long activeLinks) { this.activeLinks = activeLinks; }
    public long getTotalRoutes() { return totalRoutes; }
    public void setTotalRoutes(long totalRoutes) { this.totalRoutes = totalRoutes; }
    public long getTotalBytes() { return totalBytes; }
    public void setTotalBytes(long totalBytes) { this.totalBytes = totalBytes; }
    public long getTotalPackets() { return totalPackets; }
    public void setTotalPackets(long totalPackets) { this.totalPackets = totalPackets; }
    public double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(double avgLatency) { this.avgLatency = avgLatency; }
    public double getPacketLoss() { return packetLoss; }
    public void setPacketLoss(double packetLoss) { this.packetLoss = packetLoss; }
}

/**
 * 网络链路
 */
class NetworkLink {
    private String linkId;
    private String sourceNode;
    private String targetNode;
    private String linkType;
    private String status;
    private int latency;
    private int bandwidth;
    private long establishedAt;
    private long lastActive;

    public String getLinkId() { return linkId; }
    public void setLinkId(String linkId) { this.linkId = linkId; }
    public String getSourceNode() { return sourceNode; }
    public void setSourceNode(String sourceNode) { this.sourceNode = sourceNode; }
    public String getTargetNode() { return targetNode; }
    public void setTargetNode(String targetNode) { this.targetNode = targetNode; }
    public String getLinkType() { return linkType; }
    public void setLinkType(String linkType) { this.linkType = linkType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getLatency() { return latency; }
    public void setLatency(int latency) { this.latency = latency; }
    public int getBandwidth() { return bandwidth; }
    public void setBandwidth(int bandwidth) { this.bandwidth = bandwidth; }
    public long getEstablishedAt() { return establishedAt; }
    public void setEstablishedAt(long establishedAt) { this.establishedAt = establishedAt; }
    public long getLastActive() { return lastActive; }
    public void setLastActive(long lastActive) { this.lastActive = lastActive; }
}

/**
 * 网络路由
 */
class NetworkRoute {
    private String routeId;
    private String sourceNode;
    private String targetNode;
    private List<String> hops;
    private int totalLatency;
    private int hopCount;
    private String status;
    private String routeType;
    private long createdAt;

    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    public String getSourceNode() { return sourceNode; }
    public void setSourceNode(String sourceNode) { this.sourceNode = sourceNode; }
    public String getTargetNode() { return targetNode; }
    public void setTargetNode(String targetNode) { this.targetNode = targetNode; }
    public List<String> getHops() { return hops; }
    public void setHops(List<String> hops) { this.hops = hops; }
    public int getTotalLatency() { return totalLatency; }
    public void setTotalLatency(int totalLatency) { this.totalLatency = totalLatency; }
    public int getHopCount() { return hopCount; }
    public void setHopCount(int hopCount) { this.hopCount = hopCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

/**
 * 网络拓扑
 */
class NetworkTopology {
    private String topologyId;
    private List<TopologyNode> nodes;
    private List<TopologyEdge> edges;
    private long updatedAt;

    public String getTopologyId() { return topologyId; }
    public void setTopologyId(String topologyId) { this.topologyId = topologyId; }
    public List<TopologyNode> getNodes() { return nodes; }
    public void setNodes(List<TopologyNode> nodes) { this.nodes = nodes; }
    public List<TopologyEdge> getEdges() { return edges; }
    public void setEdges(List<TopologyEdge> edges) { this.edges = edges; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}

/**
 * 拓扑节点
 */
class TopologyNode {
    private String nodeId;
    private String nodeType;
    private String label;
    private int x;
    private int y;

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}

/**
 * 拓扑边
 */
class TopologyEdge {
    private String source;
    private String target;
    private String label;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}

/**
 * 网络质量
 */
class NetworkQuality {
    private double latency;
    private double jitter;
    private double packetLoss;
    private int bandwidth;
    private String quality;

    public double getLatency() { return latency; }
    public void setLatency(double latency) { this.latency = latency; }
    public double getJitter() { return jitter; }
    public void setJitter(double jitter) { this.jitter = jitter; }
    public double getPacketLoss() { return packetLoss; }
    public void setPacketLoss(double packetLoss) { this.packetLoss = packetLoss; }
    public int getBandwidth() { return bandwidth; }
    public void setBandwidth(int bandwidth) { this.bandwidth = bandwidth; }
    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }
}
