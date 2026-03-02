
package net.ooder.skills.api;

import java.util.List;
import java.util.Map;

/**
 * 能力定义类
 * 支持能力驱动架构的新特性：自驱入口、子能力嵌套、能力链
 *
 * @author ooder
 * @since 2.3
 */
public class Capability {
    
    private String capId;
    private String name;
    private String description;
    private List<Parameter> parameters;
    private String returnType;
    private boolean async;
    
    // ========== 能力驱动架构新增字段 ==========
    
    /** 能力类型 */
    private String capabilityType;
    
    /** 子能力ID列表 - 支持能力嵌套 */
    private List<String> capabilities;
    
    /** 自驱入口标识 - mainFirst */
    private boolean mainFirst;
    
    /** 自驱配置 */
    private MainFirstConfig mainFirstConfig;
    
    /** 协作能力引用列表 */
    private List<CollaborativeCapabilityRef> collaborativeCapabilities;
    
    /** 能力链定义 */
    private Map<String, CapabilityChain> capabilityChains;
    
    public String getCapId() {
        return capId;
    }
    
    public void setCapId(String capId) {
        this.capId = capId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
    
    public String getReturnType() {
        return returnType;
    }
    
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    
    public boolean isAsync() {
        return async;
    }
    
    public void setAsync(boolean async) {
        this.async = async;
    }
    
    // ========== 能力驱动架构新增方法 ==========
    
    public String getCapabilityType() {
        return capabilityType;
    }
    
    public void setCapabilityType(String capabilityType) {
        this.capabilityType = capabilityType;
    }
    
    /**
     * 获取子能力ID列表
     * @return 子能力ID列表
     */
    public List<String> getCapabilities() {
        return capabilities;
    }
    
    /**
     * 设置子能力ID列表
     * @param capabilities 子能力ID列表
     */
    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }
    
    /**
     * 判断是否为自驱入口（mainFirst）
     * @return true表示自驱入口
     */
    public boolean isMainFirst() {
        return mainFirst;
    }
    
    /**
     * 设置自驱入口标识
     * @param mainFirst 自驱入口标识
     */
    public void setMainFirst(boolean mainFirst) {
        this.mainFirst = mainFirst;
    }
    
    /**
     * 获取自驱配置
     * @return 自驱配置
     */
    public MainFirstConfig getMainFirstConfig() {
        return mainFirstConfig;
    }
    
    /**
     * 设置自驱配置
     * @param mainFirstConfig 自驱配置
     */
    public void setMainFirstConfig(MainFirstConfig mainFirstConfig) {
        this.mainFirstConfig = mainFirstConfig;
    }
    
    /**
     * 获取协作能力引用列表
     * @return 协作能力引用列表
     */
    public List<CollaborativeCapabilityRef> getCollaborativeCapabilities() {
        return collaborativeCapabilities;
    }
    
    /**
     * 设置协作能力引用列表
     * @param collaborativeCapabilities 协作能力引用列表
     */
    public void setCollaborativeCapabilities(List<CollaborativeCapabilityRef> collaborativeCapabilities) {
        this.collaborativeCapabilities = collaborativeCapabilities;
    }
    
    /**
     * 获取能力链定义
     * @return 能力链定义
     */
    public Map<String, CapabilityChain> getCapabilityChains() {
        return capabilityChains;
    }
    
    /**
     * 设置能力链定义
     * @param capabilityChains 能力链定义
     */
    public void setCapabilityChains(Map<String, CapabilityChain> capabilityChains) {
        this.capabilityChains = capabilityChains;
    }
    
    // ========== 内部类定义 ==========
    
    /**
     * 自驱入口配置
     */
    public static class MainFirstConfig {
        private List<SelfCheck> selfChecks;
        private List<SelfStart> selfStarts;
        private SelfDriveConfig selfDrive;
        private List<CollaborationStart> collaborationStarts;
        
        public List<SelfCheck> getSelfChecks() { return selfChecks; }
        public void setSelfChecks(List<SelfCheck> selfChecks) { this.selfChecks = selfChecks; }
        public List<SelfStart> getSelfStarts() { return selfStarts; }
        public void setSelfStarts(List<SelfStart> selfStarts) { this.selfStarts = selfStarts; }
        public SelfDriveConfig getSelfDrive() { return selfDrive; }
        public void setSelfDrive(SelfDriveConfig selfDrive) { this.selfDrive = selfDrive; }
        public List<CollaborationStart> getCollaborationStarts() { return collaborationStarts; }
        public void setCollaborationStarts(List<CollaborationStart> collaborationStarts) { 
            this.collaborationStarts = collaborationStarts; 
        }
    }
    
    /**
     * 自检配置
     */
    public static class SelfCheck {
        private String checkType;
        private java.util.Map<String, Object> params;
        
        public String getCheckType() { return checkType; }
        public void setCheckType(String checkType) { this.checkType = checkType; }
        public java.util.Map<String, Object> getParams() { return params; }
        public void setParams(java.util.Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * 自启配置
     */
    public static class SelfStart {
        private String startType;
        private java.util.Map<String, Object> params;
        
        public String getStartType() { return startType; }
        public void setStartType(String startType) { this.startType = startType; }
        public java.util.Map<String, Object> getParams() { return params; }
        public void setParams(java.util.Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * 自驱配置
     */
    public static class SelfDriveConfig {
        private String driveMode;
        private long interval;
        private java.util.Map<String, Object> params;
        
        public String getDriveMode() { return driveMode; }
        public void setDriveMode(String driveMode) { this.driveMode = driveMode; }
        public long getInterval() { return interval; }
        public void setInterval(long interval) { this.interval = interval; }
        public java.util.Map<String, Object> getParams() { return params; }
        public void setParams(java.util.Map<String, Object> params) { this.params = params; }
    }
    
    /**
     * 协作启动配置
     */
    public static class CollaborationStart {
        private String collaborativeCapabilityId;
        private java.util.Map<String, Object> initParams;
        
        public String getCollaborativeCapabilityId() { return collaborativeCapabilityId; }
        public void setCollaborativeCapabilityId(String collaborativeCapabilityId) { 
            this.collaborativeCapabilityId = collaborativeCapabilityId; 
        }
        public java.util.Map<String, Object> getInitParams() { return initParams; }
        public void setInitParams(java.util.Map<String, Object> initParams) { this.initParams = initParams; }
    }
    
    /**
     * 协作能力引用
     */
    public static class CollaborativeCapabilityRef {
        private String capabilityId;
        private String role;
        private java.util.Map<String, Object> config;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public java.util.Map<String, Object> getConfig() { return config; }
        public void setConfig(java.util.Map<String, Object> config) { this.config = config; }
    }
    
    /**
     * 能力链定义
     */
    public static class CapabilityChain {
        private String chainId;
        private String name;
        private List<String> capabilityIds;
        private java.util.Map<String, Object> config;
        
        public String getChainId() { return chainId; }
        public void setChainId(String chainId) { this.chainId = chainId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<String> getCapabilityIds() { return capabilityIds; }
        public void setCapabilityIds(List<String> capabilityIds) { this.capabilityIds = capabilityIds; }
        public java.util.Map<String, Object> getConfig() { return config; }
        public void setConfig(java.util.Map<String, Object> config) { this.config = config; }
    }
}
