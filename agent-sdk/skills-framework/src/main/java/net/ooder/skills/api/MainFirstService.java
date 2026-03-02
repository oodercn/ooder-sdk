package net.ooder.skills.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * MainFirst自驱服务接口
 * 
 * 提供场景能力的自驱入口功能，包括：
 * - selfCheck(): 自检 - 检查子能力就绪状态
 * - selfStart(): 自启 - 初始化子能力
 * - selfDrive(): 自驱 - 驱动场景运行
 * - startCollaboration(): 启动协作 - 启动协作场景
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface MainFirstService {
    
    /**
     * 自检 - 检查子能力就绪状态
     * 
     * 在场景能力启动前执行，检查所有依赖的子能力是否已就绪
     *
     * @param capabilityId 场景能力ID
     * @return 自检结果
     */
    CompletableFuture<SelfCheckResult> selfCheck(String capabilityId);
    
    /**
     * 自检 - 带配置
     *
     * @param capabilityId 场景能力ID
     * @param config 自检配置
     * @return 自检结果
     */
    CompletableFuture<SelfCheckResult> selfCheck(String capabilityId, Map<String, Object> config);
    
    /**
     * 自启 - 初始化子能力
     * 
     * 初始化场景能力及其子能力，准备运行环境
     *
     * @param capabilityId 场景能力ID
     * @return 自启结果
     */
    CompletableFuture<SelfStartResult> selfStart(String capabilityId);
    
    /**
     * 自启 - 带配置
     *
     * @param capabilityId 场景能力ID
     * @param config 自启配置
     * @return 自启结果
     */
    CompletableFuture<SelfStartResult> selfStart(String capabilityId, Map<String, Object> config);
    
    /**
     * 自驱 - 驱动场景运行
     * 
     * 启动场景能力的主驱动循环，使场景进入自驱运行状态
     *
     * @param capabilityId 场景能力ID
     * @return 自驱结果
     */
    CompletableFuture<SelfDriveResult> selfDrive(String capabilityId);
    
    /**
     * 自驱 - 带配置
     *
     * @param capabilityId 场景能力ID
     * @param config 自驱配置
     * @return 自驱结果
     */
    CompletableFuture<SelfDriveResult> selfDrive(String capabilityId, Map<String, Object> config);
    
    /**
     * 启动协作 - 启动协作场景
     * 
     * 启动与当前场景能力协作的其他场景能力
     *
     * @param capabilityId 场景能力ID
     * @param config 协作配置
     * @return 协作启动结果
     */
    CompletableFuture<CollaborationResult> startCollaboration(String capabilityId, CollaborativeConfig config);
    
    /**
     * 停止协作
     *
     * @param capabilityId 场景能力ID
     * @param collaborativeCapabilityId 协作能力ID
     * @return 停止结果
     */
    CompletableFuture<Void> stopCollaboration(String capabilityId, String collaborativeCapabilityId);
    
    /**
     * 健康检查
     *
     * @param capabilityId 场景能力ID
     * @return 健康状态
     */
    CompletableFuture<HealthStatus> healthCheck(String capabilityId);
    
    /**
     * 故障恢复
     *
     * @param capabilityId 场景能力ID
     * @param recoveryConfig 恢复配置
     * @return 恢复结果
     */
    CompletableFuture<RecoveryResult> recover(String capabilityId, RecoveryConfig recoveryConfig);
    
    /**
     * 优雅关闭
     *
     * @param capabilityId 场景能力ID
     * @return 关闭结果
     */
    CompletableFuture<Void> gracefulShutdown(String capabilityId);
    
    /**
     * 获取场景能力状态
     *
     * @param capabilityId 场景能力ID
     * @return 能力状态
     */
    CompletableFuture<CapabilityStatus> getStatus(String capabilityId);
    
    /**
     * 添加MainFirst监听器
     *
     * @param listener 监听器
     */
    void addMainFirstListener(MainFirstListener listener);
    
    /**
     * 移除MainFirst监听器
     *
     * @param listener 监听器
     */
    void removeMainFirstListener(MainFirstListener listener);
    
    // ========== 结果类定义 ==========
    
    /**
     * 自检结果
     */
    class SelfCheckResult {
        private boolean passed;
        private String capabilityId;
        private List<CheckItem> checkItems;
        private List<String> failedItems;
        private String message;
        private long checkTime;
        
        // Getters and Setters
        public boolean isPassed() { return passed; }
        public void setPassed(boolean passed) { this.passed = passed; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public List<CheckItem> getCheckItems() { return checkItems; }
        public void setCheckItems(List<CheckItem> checkItems) { this.checkItems = checkItems; }
        public List<String> getFailedItems() { return failedItems; }
        public void setFailedItems(List<String> failedItems) { this.failedItems = failedItems; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getCheckTime() { return checkTime; }
        public void setCheckTime(long checkTime) { this.checkTime = checkTime; }
    }
    
    /**
     * 检查项
     */
    class CheckItem {
        private String name;
        private boolean passed;
        private String message;
        private long duration;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public boolean isPassed() { return passed; }
        public void setPassed(boolean passed) { this.passed = passed; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
    }
    
    /**
     * 自启结果
     */
    class SelfStartResult {
        private boolean success;
        private String capabilityId;
        private List<String> startedCapabilities;
        private List<String> failedCapabilities;
        private String message;
        private long startTime;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public List<String> getStartedCapabilities() { return startedCapabilities; }
        public void setStartedCapabilities(List<String> startedCapabilities) { this.startedCapabilities = startedCapabilities; }
        public List<String> getFailedCapabilities() { return failedCapabilities; }
        public void setFailedCapabilities(List<String> failedCapabilities) { this.failedCapabilities = failedCapabilities; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
    }
    
    /**
     * 自驱结果
     */
    class SelfDriveResult {
        private boolean success;
        private String capabilityId;
        private String driveMode;
        private String message;
        private long timestamp;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getDriveMode() { return driveMode; }
        public void setDriveMode(String driveMode) { this.driveMode = driveMode; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 协作启动结果
     */
    class CollaborationResult {
        private boolean success;
        private String capabilityId;
        private List<String> startedCollaborativeCapabilities;
        private List<String> failedCollaborativeCapabilities;
        private String message;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public List<String> getStartedCollaborativeCapabilities() { return startedCollaborativeCapabilities; }
        public void setStartedCollaborativeCapabilities(List<String> startedCollaborativeCapabilities) { this.startedCollaborativeCapabilities = startedCollaborativeCapabilities; }
        public List<String> getFailedCollaborativeCapabilities() { return failedCollaborativeCapabilities; }
        public void setFailedCollaborativeCapabilities(List<String> failedCollaborativeCapabilities) { this.failedCollaborativeCapabilities = failedCollaborativeCapabilities; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 健康状态
     */
    class HealthStatus {
        public enum Status {
            HEALTHY,    // 健康
            DEGRADED,   // 降级
            UNHEALTHY   // 不健康
        }
        
        private Status status;
        private String capabilityId;
        private List<HealthCheckItem> checks;
        private String message;
        private long timestamp;
        
        // Getters and Setters
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public List<HealthCheckItem> getChecks() { return checks; }
        public void setChecks(List<HealthCheckItem> checks) { this.checks = checks; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 健康检查项
     */
    class HealthCheckItem {
        private String name;
        private boolean healthy;
        private String message;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 恢复结果
     */
    class RecoveryResult {
        private boolean success;
        private String capabilityId;
        private String recoveryAction;
        private String message;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getRecoveryAction() { return recoveryAction; }
        public void setRecoveryAction(String recoveryAction) { this.recoveryAction = recoveryAction; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 能力状态
     */
    class CapabilityStatus {
        public enum State {
            PENDING,        // 待启动
            CHECKING,       // 自检中
            STARTING,       // 启动中
            RUNNING,        // 运行中
            DRIVING,        // 自驱中
            COLLABORATING,  // 协作中
            STOPPING,       // 停止中
            STOPPED,        // 已停止
            ERROR           // 错误
        }
        
        private State state;
        private String capabilityId;
        private String message;
        private long timestamp;
        
        // Getters and Setters
        public State getState() { return state; }
        public void setState(State state) { this.state = state; }
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 协作配置
     */
    class CollaborativeConfig {
        private List<String> collaborativeCapabilityIds;
        private Map<String, Object> initParams;
        private boolean autoSyncState;
        
        // Getters and Setters
        public List<String> getCollaborativeCapabilityIds() { return collaborativeCapabilityIds; }
        public void setCollaborativeCapabilityIds(List<String> collaborativeCapabilityIds) { this.collaborativeCapabilityIds = collaborativeCapabilityIds; }
        public Map<String, Object> getInitParams() { return initParams; }
        public void setInitParams(Map<String, Object> initParams) { this.initParams = initParams; }
        public boolean isAutoSyncState() { return autoSyncState; }
        public void setAutoSyncState(boolean autoSyncState) { this.autoSyncState = autoSyncState; }
    }
    
    /**
     * 恢复配置
     */
    class RecoveryConfig {
        private String recoveryType;
        private int maxRetries;
        private long retryInterval;
        private Map<String, Object> params;
        
        // Getters and Setters
        public String getRecoveryType() { return recoveryType; }
        public void setRecoveryType(String recoveryType) { this.recoveryType = recoveryType; }
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
        public long getRetryInterval() { return retryInterval; }
        public void setRetryInterval(long retryInterval) { this.retryInterval = retryInterval; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * MainFirst监听器
     */
    interface MainFirstListener {
        void onSelfCheckCompleted(SelfCheckResult result);
        void onSelfStartCompleted(SelfStartResult result);
        void onSelfDriveStarted(SelfDriveResult result);
        void onCollaborationStarted(CollaborationResult result);
        void onHealthStatusChanged(HealthStatus status);
        void onStateChanged(CapabilityStatus.State oldState, CapabilityStatus.State newState);
    }
}
