package net.ooder.scene.discovery;

import java.util.List;
import java.util.Map;

/**
 * 能力数据传输对象
 * 
 * <p>用于发现流程中传递能力/场景信息的统一数据结构。</p>
 * 
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class CapabilityDTO {
    
    /** 能力/场景唯一标识 */
    private String id;
    
    /** 名称 */
    private String name;
    
    /** 描述 */
    private String description;
    
    /** 版本 */
    private String version;
    
    /** 类型: capability, scene, skill */
    private String type;
    
    /** 分类 */
    private String category;
    
    /** 标签列表 */
    private List<String> tags;
    
    /** 来源: local, github, gitee, git */
    private String source;
    
    /** 位置信息（文件路径或URL） */
    private String location;
    
    /** 驱动条件 */
    private List<DriverCondition> driverConditions;
    
    /** 依赖的能力ID列表 */
    private List<String> dependencies;
    
    /** 元数据 */
    private Map<String, Object> metadata;
    
    /** 是否已安装 */
    private boolean installed;
    
    /** 是否已注册到 SceneEngine */
    private boolean registered;
    
    /** 发现时间戳 */
    private long discoveredAt;
    
    /** 能力地址 */
    private String capabilityAddress;
    
    /**
     * 驱动条件
     */
    public static class DriverCondition {
        private String type;
        private String expression;
        private String description;
        
        public DriverCondition() {}
        
        public DriverCondition(String type, String expression, String description) {
            this.type = type;
            this.expression = expression;
            this.description = description;
        }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public List<DriverCondition> getDriverConditions() { return driverConditions; }
    public void setDriverConditions(List<DriverCondition> driverConditions) { this.driverConditions = driverConditions; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public boolean isInstalled() { return installed; }
    public void setInstalled(boolean installed) { this.installed = installed; }
    
    public boolean isRegistered() { return registered; }
    public void setRegistered(boolean registered) { this.registered = registered; }
    
    public long getDiscoveredAt() { return discoveredAt; }
    public void setDiscoveredAt(long discoveredAt) { this.discoveredAt = discoveredAt; }
    
    public String getCapabilityAddress() { return capabilityAddress; }
    public void setCapabilityAddress(String capabilityAddress) { this.capabilityAddress = capabilityAddress; }
    
    /**
     * 检查是否为场景类型
     */
    public boolean isScene() {
        return "scene".equalsIgnoreCase(type);
    }
    
    /**
     * 检查是否为能力类型
     */
    public boolean isCapability() {
        return "capability".equalsIgnoreCase(type);
    }
    
    /**
     * 检查是否为技能类型
     */
    public boolean isSkill() {
        return "skill".equalsIgnoreCase(type);
    }
    
    @Override
    public String toString() {
        return "CapabilityDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", source='" + source + '\'' +
                ", installed=" + installed +
                ", registered=" + registered +
                '}';
    }
}
