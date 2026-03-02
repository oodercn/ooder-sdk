package net.ooder.scene.monitor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 能力状态监控接口
 * 提供场景内能力的运行状态监控
 *
 * @author ooder
 * @since 2.3
 */
public interface CapabilityStatusMonitor {
    
    /**
     * 获取能力状态
     * @param sceneId 场景ID
     * @return 能力状态映射
     */
    CompletableFuture<Map<String, CapabilityStatus>> getCapabilitiesStatus(String sceneId);
}

/**
 * 能力状态
 */
class CapabilityStatus {
    private String capabilityId;
    private String capabilityName;
    private String interfaceId;
    private String status;        // ACTIVE, INACTIVE, ERROR
    private long invokeCount;     // 调用次数
    private long errorCount;      // 错误次数
    private double successRate;   // 成功率(%)
    private double avgLatency;    // 平均延迟(ms)
    private long lastInvokeTime;  // 最后调用时间
    
    // Getters and Setters
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getCapabilityName() { return capabilityName; }
    public void setCapabilityName(String capabilityName) { this.capabilityName = capabilityName; }
    public String getInterfaceId() { return interfaceId; }
    public void setInterfaceId(String interfaceId) { this.interfaceId = interfaceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getInvokeCount() { return invokeCount; }
    public void setInvokeCount(long invokeCount) { this.invokeCount = invokeCount; }
    public long getErrorCount() { return errorCount; }
    public void setErrorCount(long errorCount) { this.errorCount = errorCount; }
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(double avgLatency) { this.avgLatency = avgLatency; }
    public long getLastInvokeTime() { return lastInvokeTime; }
    public void setLastInvokeTime(long lastInvokeTime) { this.lastInvokeTime = lastInvokeTime; }
}
