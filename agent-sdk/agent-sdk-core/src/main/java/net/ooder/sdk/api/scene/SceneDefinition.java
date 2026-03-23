package net.ooder.sdk.api.scene;

import java.util.List;
import java.util.Map;

import net.ooder.skills.api.SceneType;
import net.ooder.sdk.api.capability.Capability;

/**
 * 场景定义类
 * 定义场景的基本信息、能力列表、协作场景和配置信息
 *
 * @author ooder
 * @since 2.3
 */
public class SceneDefinition {
    
    /** 场景ID */
    private String sceneId;
    /** 场景名称 */
    private String name;
    /** 场景描述 */
    private String description;
    /** 场景版本 */
    private String version;
    /** 场景类型 */
    private SceneType type;
    /** 场景前缀 */
    private String scenePrefix;
    /** 能力列表 */
    private List<Capability> capabilities;
    /** 协作场景ID列表 */
    private List<String> collaborativeScenes;
    /** 场景配置 */
    private Map<String, Object> config;
    /** VFS配置 */
    private Map<String, Object> vfsConfig;
    /** 认证配置 */
    private Map<String, Object> authConfig;
    
    /**
     * 获取场景ID
     * @return 场景ID
     */
    public String getSceneId() {
        return sceneId;
    }
    
    /**
     * 设置场景ID
     * @param sceneId 场景ID
     */
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    /**
     * 获取场景名称
     * @return 场景名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 设置场景名称
     * @param name 场景名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 获取场景描述
     * @return 场景描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 设置场景描述
     * @param description 场景描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 获取场景版本
     * @return 场景版本
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * 设置场景版本
     * @param version 场景版本
     */
    public void setVersion(String version) {
        this.version = version;
    }
    
    /**
     * 获取场景类型
     * @return 场景类型
     */
    public SceneType getType() {
        return type;
    }
    
    /**
     * 设置场景类型
     * @param type 场景类型
     */
    public void setType(SceneType type) {
        this.type = type;
    }
    
    /**
     * 获取场景前缀
     * @return 场景前缀
     */
    public String getScenePrefix() {
        return scenePrefix;
    }
    
    /**
     * 设置场景前缀
     * @param scenePrefix 场景前缀
     */
    public void setScenePrefix(String scenePrefix) {
        this.scenePrefix = scenePrefix;
    }
    
    /**
     * 获取能力列表
     * @return 能力列表
     */
    public List<Capability> getCapabilities() {
        return capabilities;
    }
    
    /**
     * 设置能力列表
     * @param capabilities 能力列表
     */
    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }
    
    /**
     * 获取协作场景列表
     * @return 协作场景ID列表
     */
    public List<String> getCollaborativeScenes() {
        return collaborativeScenes;
    }
    
    /**
     * 设置协作场景列表
     * @param collaborativeScenes 协作场景ID列表
     */
    public void setCollaborativeScenes(List<String> collaborativeScenes) {
        this.collaborativeScenes = collaborativeScenes;
    }
    
    /**
     * 获取场景配置
     * @return 场景配置
     */
    public Map<String, Object> getConfig() {
        return config;
    }
    
    /**
     * 设置场景配置
     * @param config 场景配置
     */
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    /**
     * 获取VFS配置
     * @return VFS配置
     */
    public Map<String, Object> getVfsConfig() {
        return vfsConfig;
    }
    
    /**
     * 设置VFS配置
     * @param vfsConfig VFS配置
     */
    public void setVfsConfig(Map<String, Object> vfsConfig) {
        this.vfsConfig = vfsConfig;
    }
    
    /**
     * 获取认证配置
     * @return 认证配置
     */
    public Map<String, Object> getAuthConfig() {
        return authConfig;
    }
    
    /**
     * 设置认证配置
     * @param authConfig 认证配置
     */
    public void setAuthConfig(Map<String, Object> authConfig) {
        this.authConfig = authConfig;
    }
    
    /**
     * 判断是否为主场景
     * @return true表示主场景
     */
    public boolean isPrimary() {
        return type == SceneType.PRIMARY;
    }
    
    /**
     * 判断是否为协作场景
     * @return true表示协作场景
     */
    public boolean isCollaborative() {
        return type == SceneType.COLLABORATIVE;
    }
    
    /**
     * 生成能力ID
     * @param functionName 功能名称
     * @return 生成的能力ID
     */
    public String generateCapId(String functionName) {
        if (scenePrefix == null || scenePrefix.isEmpty()) {
            return functionName;
        }
        return scenePrefix + "-" + functionName;
    }
}
