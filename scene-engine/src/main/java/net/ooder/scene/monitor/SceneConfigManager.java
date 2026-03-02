package net.ooder.scene.monitor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 场景配置管理接口
 * 提供场景配置的查询、历史、回滚等功能
 *
 * @author ooder
 * @since 2.3
 */
public interface SceneConfigManager {
    
    /**
     * 获取配置历史
     * @param sceneId 场景ID
     * @return 配置历史
     */
    CompletableFuture<ConfigHistory> getConfigHistory(String sceneId);
    
    /**
     * 回滚配置到指定版本
     * @param sceneId 场景ID
     * @param version 版本号
     * @return 是否成功
     */
    CompletableFuture<Boolean> rollbackConfig(String sceneId, int version);
    
    /**
     * 导出配置
     * @param sceneId 场景ID
     * @param format 格式 (json, yaml)
     * @return 配置内容
     */
    CompletableFuture<String> exportConfig(String sceneId, String format);
    
    /**
     * 导入配置
     * @param sceneId 场景ID
     * @param configContent 配置内容
     * @param format 格式 (json, yaml)
     * @return 是否成功
     */
    CompletableFuture<Boolean> importConfig(String sceneId, String configContent, String format);
}

/**
 * 配置历史
 */
class ConfigHistory {
    private String sceneId;
    private List<ConfigVersion> versions;
    
    // Getters and Setters
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public List<ConfigVersion> getVersions() { return versions; }
    public void setVersions(List<ConfigVersion> versions) { this.versions = versions; }
}

/**
 * 配置版本
 */
class ConfigVersion {
    private int version;
    private long createdAt;
    private String createdBy;
    private String description;
    private String changeType;    // CREATE, UPDATE, DELETE
    private String configContent; // 配置内容(JSON/YAML)
    
    // Getters and Setters
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getConfigContent() { return configContent; }
    public void setConfigContent(String configContent) { this.configContent = configContent; }
}
