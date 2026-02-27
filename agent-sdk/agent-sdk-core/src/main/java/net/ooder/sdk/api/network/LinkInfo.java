package net.ooder.sdk.api.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Link Information（泛型版本）
 *
 * @param <M> 元数据类型
 * @author ooder Team
 * @since 2.3
 */
public class LinkInfo<M> {

    private String linkId;
    private String sourceId;
    private String targetId;
    private LinkType type;
    private LinkStatus status;
    private long createTime;
    private long establishedTime;
    private long lastActiveTime;
    private int reconnectCount;
    private long totalBytesSent;
    private long totalBytesReceived;
    private double avgLatency;
    private double packetLossRate;
    private String qualityLevel;
    private LinkQualityInfo quality;
    /** 链接元数据 */
    private Map<String, M> metadata;
    
    /**
     * 创建通用链接信息（向后兼容）
     */
    public static LinkInfo<Object> createGeneric() {
        return new LinkInfo<>();
    }

    public LinkInfo() {
        this.createTime = System.currentTimeMillis();
        this.establishedTime = System.currentTimeMillis();
        this.lastActiveTime = System.currentTimeMillis();
        this.status = LinkStatus.ACTIVE;
        this.quality = new LinkQualityInfo();
        this.metadata = new ConcurrentHashMap<>();
        this.reconnectCount = 0;
        this.totalBytesSent = 0;
        this.totalBytesReceived = 0;
        this.avgLatency = 0.0;
        this.packetLossRate = 0.0;
        this.qualityLevel = "GOOD";
    }

    public enum LinkType {
        DIRECT,
        ROUTED,
        TUNNEL,
        P2P,
        RELAY
    }

    public enum LinkStatus {
        ACTIVE,
        INACTIVE,
        DEGRADED,
        FAILED,
        PENDING
    }

    public String getLinkId() { return linkId; }
    public void setLinkId(String linkId) { this.linkId = linkId; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public LinkType getType() { return type; }
    public void setType(LinkType type) { this.type = type; }

    public LinkStatus getStatus() { return status; }
    public void setStatus(LinkStatus status) { this.status = status; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }

    public LinkQualityInfo getQuality() { return quality; }
    public void setQuality(LinkQualityInfo quality) { this.quality = quality; }

    public long getEstablishedTime() { return establishedTime; }
    public void setEstablishedTime(long establishedTime) { this.establishedTime = establishedTime; }

    public int getReconnectCount() { return reconnectCount; }
    public void setReconnectCount(int reconnectCount) { this.reconnectCount = reconnectCount; }
    public void incrementReconnectCount() { this.reconnectCount++; }

    public long getTotalBytesSent() { return totalBytesSent; }
    public void setTotalBytesSent(long totalBytesSent) { this.totalBytesSent = totalBytesSent; }
    public void addBytesSent(long bytes) { this.totalBytesSent += bytes; }

    public long getTotalBytesReceived() { return totalBytesReceived; }
    public void setTotalBytesReceived(long totalBytesReceived) { this.totalBytesReceived = totalBytesReceived; }
    public void addBytesReceived(long bytes) { this.totalBytesReceived += bytes; }

    public double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(double avgLatency) { this.avgLatency = avgLatency; }

    public double getPacketLossRate() { return packetLossRate; }
    public void setPacketLossRate(double packetLossRate) { this.packetLossRate = packetLossRate; }

    public String getQualityLevel() { return qualityLevel; }
    public void setQualityLevel(String qualityLevel) { this.qualityLevel = qualityLevel; }

    /**
     * 获取链接元数据
     * @return 元数据映射
     */
    public Map<String, M> getMetadata() { return metadata; }
    /**
     * 设置链接元数据
     * @param metadata 元数据映射
     */
    public void setMetadata(Map<String, M> metadata) { this.metadata = metadata; }
    public void addMetadata(String key, M value) { this.metadata.put(key, value); }
    /**
     * 获取指定 key 的元数据值
     * @param key 元数据键
     * @return 元数据值
     */
    public M getMetadata(String key) { return this.metadata.get(key); }

    @Override
    public String toString() {
        return "LinkInfo{" +
                "linkId='" + linkId + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", avgLatency=" + avgLatency +
                ", qualityLevel='" + qualityLevel + '\'' +
                '}';
    }
}
