package net.ooder.scene.monitor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务健康监控接口
 * 提供场景内服务的健康状态监控
 *
 * @author ooder
 * @since 2.3
 */
public interface ServiceHealthMonitor {
    
    /**
     * 获取服务健康状态
     * @param sceneId 场景ID
     * @return 服务健康状态映射
     */
    CompletableFuture<Map<String, ServiceHealth>> getServicesHealth(String sceneId);
}

/**
 * 服务健康状态
 */
class ServiceHealth {
    private String serviceId;
    private String serviceName;
    private String type;        // ORG, VFS, MSG, JDS
    private String status;      // ACTIVE, WARNING, ERROR, STOPPED
    private long uptime;
    private int errorCount;
    private String lastError;
    private HealthCheckResult lastCheck;
    
    // Getters and Setters
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
    public HealthCheckResult getLastCheck() { return lastCheck; }
    public void setLastCheck(HealthCheckResult lastCheck) { this.lastCheck = lastCheck; }
}

/**
 * 健康检查结果
 */
class HealthCheckResult {
    private long checkTime;
    private boolean healthy;
    private String message;
    private Map<String, Object> details;
    
    // Getters and Setters
    public long getCheckTime() { return checkTime; }
    public void setCheckTime(long checkTime) { this.checkTime = checkTime; }
    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
