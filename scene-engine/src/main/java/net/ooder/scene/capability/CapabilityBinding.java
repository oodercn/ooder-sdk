package net.ooder.scene.capability;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 能力绑定
 * 
 * <p>SE原生的能力绑定模型，表示将某个能力绑定到场景组。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>维护能力绑定的基本信息</li>
 *   <li>管理绑定的状态和优先级</li>
 *   <li>记录绑定的配置和参数</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class CapabilityBinding {
    
    /**
     * 提供者类型
     */
    public enum ProviderType {
        AGENT,          // 代理提供
        PLATFORM,       // 平台提供
        EXTERNAL,     // 外部提供
        HYBRID          // 混合提供
    }
    
    /**
     * 连接器类型
     */
    public enum ConnectorType {
        INTERNAL,       // 内部连接
        EXTERNAL,       // 外部连接
        HYBRID          // 混合连接
    }
    
    /**
     * 绑定状态
     */
    public enum Status {
        ACTIVE,         // 激活状态
        INACTIVE,       // 非激活状态
        ERROR,          // 错误状态
        PENDING,        // 待处理状态
        REMOVED         // 已移除
    }
    
    // ========== 基础信息 ==========
    
    /** 绑定ID */
    private final String bindingId;
    
    /** 场景组ID */
    private final String sceneGroupId;
    
    /** 能力ID */
    private final String capId;
    
    /** 能力名称 */
    private String capName;
    
    /** 能力地址 */
    private String capAddress;
    
    // ========== 类型信息 ==========
    
    /** 提供者类型 */
    private ProviderType providerType;
    
    /** 连接器类型 */
    private ConnectorType connectorType;
    
    // ========== 状态和配置 ==========
    
    /** 当前状态 */
    private volatile Status status = Status.PENDING;
    
    /** 优先级（数字越小优先级越高） */
    private int priority = 0;
    
    /** 是否允许降级 */
    private boolean fallback = false;
    
    /** 配置参数 */
    private final Map<String, Object> config = new ConcurrentHashMap<>();
    
    /** 元数据 */
    private final Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    // ========== 时间信息 ==========
    
    /** 绑定创建时间 */
    private final Instant createTime;
    
    /** 最后更新时间 */
    private volatile Instant lastUpdateTime;
    
    /** 激活时间 */
    private volatile Instant activateTime;
    
    /**
     * 构造函数
     */
    public CapabilityBinding(String bindingId, String sceneGroupId, String capId) {
        this.bindingId = bindingId;
        this.sceneGroupId = sceneGroupId;
        this.capId = capId;
        this.createTime = Instant.now();
        this.lastUpdateTime = this.createTime;
    }
    
    // ========== 基础信息 Getter/Setter ==========
    
    public String getBindingId() {
        return bindingId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public String getCapId() {
        return capId;
    }
    
    public String getCapName() {
        return capName;
    }
    
    public void setCapName(String capName) {
        this.capName = capName;
        updateTime();
    }
    
    public String getCapAddress() {
        return capAddress;
    }
    
    public void setCapAddress(String capAddress) {
        this.capAddress = capAddress;
        updateTime();
    }
    
    // ========== 类型信息 Getter/Setter ==========
    
    public ProviderType getProviderType() {
        return providerType;
    }
    
    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
        updateTime();
    }
    
    public ConnectorType getConnectorType() {
        return connectorType;
    }
    
    public void setConnectorType(ConnectorType connectorType) {
        this.connectorType = connectorType;
        updateTime();
    }
    
    // ========== 状态和配置 Getter/Setter ==========
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.ACTIVE && activateTime == null) {
            activateTime = Instant.now();
        }
        updateTime();
    }
    
    /**
     * 激活绑定
     */
    public boolean activate() {
        if (status == Status.ACTIVE) {
            return true;
        }
        if (status == Status.PENDING || status == Status.INACTIVE) {
            status = Status.ACTIVE;
            if (activateTime == null) {
                activateTime = Instant.now();
            }
            updateTime();
            return true;
        }
        return false;
    }
    
    /**
     * 停用绑定
     */
    public boolean deactivate() {
        if (status == Status.ACTIVE) {
            status = Status.INACTIVE;
            updateTime();
            return true;
        }
        return false;
    }
    
    /**
     * 标记为错误状态
     */
    public void markError() {
        status = Status.ERROR;
        updateTime();
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
        updateTime();
    }
    
    public boolean isFallback() {
        return fallback;
    }
    
    public void setFallback(boolean fallback) {
        this.fallback = fallback;
        updateTime();
    }
    
    // ========== 配置和元数据 ==========
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public Object getConfig(String key) {
        return config.get(key);
    }
    
    public void setConfig(String key, Object value) {
        config.put(key, value);
        updateTime();
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
        updateTime();
    }
    
    // ========== 时间信息 ==========
    
    public Instant getCreateTime() {
        return createTime;
    }
    
    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public Instant getActivateTime() {
        return activateTime;
    }
    
    private void updateTime() {
        this.lastUpdateTime = Instant.now();
    }
    
    @Override
    public String toString() {
        return "CapabilityBinding{" +
                "bindingId='" + bindingId + '\'' +
                ", capId='" + capId + '\'' +
                ", capName='" + capName + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", providerType=" + providerType +
                '}';  // 这里有个问题，我添加一个缺失的关闭花括号
    }
}
