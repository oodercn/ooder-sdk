package net.ooder.scene.provider;

import net.ooder.scene.core.PageResult;
import java.util.List;
import java.util.Map;

/**
 * 托管Provider接口
 *
 * <p>定义托管服务相关的操作接口，由skill-hosting实现</p>
 */
public interface HostingProvider extends BaseProvider {

    /**
     * 获取所有实例
     */
    List<HostingInstance> getAllInstances();

    /**
     * 分页获取实例
     */
    PageResult<HostingInstance> getInstances(int page, int size);

    /**
     * 获取实例
     */
    HostingInstance getInstance(String instanceId);

    /**
     * 创建实例
     */
    HostingInstance createInstance(HostingInstance instance);

    /**
     * 更新实例
     */
    boolean updateInstance(HostingInstance instance);

    /**
     * 删除实例
     */
    boolean deleteInstance(String instanceId);

    /**
     * 启动实例
     */
    boolean startInstance(String instanceId);

    /**
     * 停止实例
     */
    boolean stopInstance(String instanceId);

    /**
     * 重启实例
     */
    boolean restartInstance(String instanceId);

    /**
     * 扩缩容实例
     */
    boolean scaleInstance(String instanceId, int replicas);

    /**
     * 获取实例健康状态
     */
    InstanceHealth getHealth(String instanceId);

    /**
     * 获取实例指标
     */
    List<InstanceMetric> getMetrics(String instanceId, long startTime, long endTime);

    /**
     * 获取实例日志
     */
    List<HostingLogEntry> getLogs(String instanceId, int lines);

    /**
     * 执行命令
     */
    HostingCommandResult executeCommand(String instanceId, String command);

    /**
     * 获取支持的托管类型
     */
    List<String> getSupportedTypes();

    /**
     * 获取配额
     */
    HostingQuota getQuota();
}

/**
 * 托管实例
 */
class HostingInstance {
    private String instanceId;
    private String instanceName;
    private String instanceType;
    private String provider;
    private String status;
    private int replicas;
    private int desiredReplicas;
    private String image;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private Map<String, Object> config;
    private long createdAt;
    private long updatedAt;

    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    public String getInstanceName() { return instanceName; }
    public void setInstanceName(String instanceName) { this.instanceName = instanceName; }
    public String getInstanceType() { return instanceType; }
    public void setInstanceType(String instanceType) { this.instanceType = instanceType; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getReplicas() { return replicas; }
    public void setReplicas(int replicas) { this.replicas = replicas; }
    public int getDesiredReplicas() { return desiredReplicas; }
    public void setDesiredReplicas(int desiredReplicas) { this.desiredReplicas = desiredReplicas; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Map<String, String> getLabels() { return labels; }
    public void setLabels(Map<String, String> labels) { this.labels = labels; }
    public Map<String, String> getAnnotations() { return annotations; }
    public void setAnnotations(Map<String, String> annotations) { this.annotations = annotations; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}

/**
 * 实例健康状态
 */
class InstanceHealth {
    private String instanceId;
    private String status;
    private boolean healthy;
    private String message;
    private long lastCheckTime;
    private Map<String, HealthCheck> checks;

    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getLastCheckTime() { return lastCheckTime; }
    public void setLastCheckTime(long lastCheckTime) { this.lastCheckTime = lastCheckTime; }
    public Map<String, HealthCheck> getChecks() { return checks; }
    public void setChecks(Map<String, HealthCheck> checks) { this.checks = checks; }
}

/**
 * 健康检查
 */
class HealthCheck {
    private String name;
    private String status;
    private String message;
    private long duration;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}

/**
 * 实例指标
 */
class InstanceMetric {
    private String name;
    private String unit;
    private double value;
    private long timestamp;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * 托管日志条目
 */
class HostingLogEntry {
    private String instanceId;
    private String level;
    private String message;
    private String source;
    private long timestamp;

    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

/**
 * 托管命令执行结果
 */
class HostingCommandResult {
    private String instanceId;
    private String command;
    private int exitCode;
    private String stdout;
    private String stderr;
    private long duration;

    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }
    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}

/**
 * 托管配额
 */
class HostingQuota {
    private int maxInstances;
    private int currentInstances;
    private int maxCpuCores;
    private int currentCpuCores;
    private long maxMemoryMB;
    private long currentMemoryMB;
    private long maxStorageGB;
    private long currentStorageGB;

    public int getMaxInstances() { return maxInstances; }
    public void setMaxInstances(int maxInstances) { this.maxInstances = maxInstances; }
    public int getCurrentInstances() { return currentInstances; }
    public void setCurrentInstances(int currentInstances) { this.currentInstances = currentInstances; }
    public int getMaxCpuCores() { return maxCpuCores; }
    public void setMaxCpuCores(int maxCpuCores) { this.maxCpuCores = maxCpuCores; }
    public int getCurrentCpuCores() { return currentCpuCores; }
    public void setCurrentCpuCores(int currentCpuCores) { this.currentCpuCores = currentCpuCores; }
    public long getMaxMemoryMB() { return maxMemoryMB; }
    public void setMaxMemoryMB(long maxMemoryMB) { this.maxMemoryMB = maxMemoryMB; }
    public long getCurrentMemoryMB() { return currentMemoryMB; }
    public void setCurrentMemoryMB(long currentMemoryMB) { this.currentMemoryMB = currentMemoryMB; }
    public long getMaxStorageGB() { return maxStorageGB; }
    public void setMaxStorageGB(long maxStorageGB) { this.maxStorageGB = maxStorageGB; }
    public long getCurrentStorageGB() { return currentStorageGB; }
    public void setCurrentStorageGB(long currentStorageGB) { this.currentStorageGB = currentStorageGB; }
}
