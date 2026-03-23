package net.ooder.scene.core.activation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 场景密钥
 * 
 * <p>表示场景激活过程中生成的密钥，用于：</p>
 * <ul>
 *   <li>场景访问控制</li>
 *   <li>API鉴权</li>
 *   <li>数据加密</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneKey implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String keyId;              // 密钥ID
    private String key;                // 密钥值
    private String keyType;            // 密钥类型（ACCESS, ADMIN, API, ENCRYPTION）
    private String sceneId;            // 场景ID
    private String instanceId;         // 实例ID
    private String userId;             // 用户ID
    private String roleId;             // 角色ID
    private long createdAt;            // 创建时间
    private long expiresAt;            // 过期时间
    private long lastUsedAt;           // 最后使用时间
    private boolean active;            // 是否激活
    private List<String> permissions;  // 权限列表
    private int usageCount;            // 使用次数
    private int maxUsageCount;         // 最大使用次数（0表示无限制）
    private String description;        // 描述
    
    public SceneKey() {
        this.keyId = generateKeyId();
        this.createdAt = System.currentTimeMillis();
        this.active = true;
        this.permissions = new ArrayList<>();
        this.usageCount = 0;
        this.maxUsageCount = 0;
    }
    
    private static String generateKeyId() {
        return "key-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 检查密钥是否过期
     */
    public boolean isExpired() {
        if (expiresAt <= 0) {
            return false;
        }
        return System.currentTimeMillis() > expiresAt;
    }
    
    /**
     * 检查密钥是否有效
     */
    public boolean isValid() {
        if (!active) {
            return false;
        }
        if (isExpired()) {
            return false;
        }
        if (maxUsageCount > 0 && usageCount >= maxUsageCount) {
            return false;
        }
        return true;
    }
    
    /**
     * 使用密钥
     */
    public boolean use() {
        if (!isValid()) {
            return false;
        }
        usageCount++;
        lastUsedAt = System.currentTimeMillis();
        return true;
    }
    
    /**
     * 撤销密钥
     */
    public void revoke() {
        this.active = false;
    }
    
    /**
     * 检查是否有权限
     */
    public boolean hasPermission(String permission) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        return permissions.contains(permission) || permissions.contains("*");
    }
    
    /**
     * 添加权限
     */
    public void addPermission(String permission) {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }
    
    /**
     * 移除权限
     */
    public void removePermission(String permission) {
        if (permissions != null) {
            permissions.remove(permission);
        }
    }
    
    /**
     * 获取剩余有效时间（毫秒）
     */
    public long getRemainingTime() {
        if (expiresAt <= 0) {
            return Long.MAX_VALUE;
        }
        long remaining = expiresAt - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }
    
    /**
     * 获取剩余使用次数
     */
    public int getRemainingUsage() {
        if (maxUsageCount <= 0) {
            return Integer.MAX_VALUE;
        }
        int remaining = maxUsageCount - usageCount;
        return remaining > 0 ? remaining : 0;
    }
    
    // Getters and Setters
    
    public String getKeyId() {
        return keyId;
    }
    
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getKeyType() {
        return keyType;
    }
    
    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getRoleId() {
        return roleId;
    }
    
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public long getLastUsedAt() {
        return lastUsedAt;
    }
    
    public void setLastUsedAt(long lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    public int getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }
    
    public int getMaxUsageCount() {
        return maxUsageCount;
    }
    
    public void setMaxUsageCount(int maxUsageCount) {
        this.maxUsageCount = maxUsageCount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "SceneKey{" +
                "keyId='" + keyId + '\'' +
                ", keyType='" + keyType + '\'' +
                ", sceneId='" + sceneId + '\'' +
                ", active=" + active +
                ", usageCount=" + usageCount +
                '}';
    }
    
    /**
     * 密钥类型
     */
    public enum KeyType {
        ACCESS("访问密钥", 365L * 24 * 60 * 60 * 1000),      // 默认1年有效期
        ADMIN("管理密钥", 30L * 24 * 60 * 60 * 1000),       // 默认30天有效期
        API("API密钥", 365L * 24 * 60 * 60 * 1000),         // 默认1年有效期
        ENCRYPTION("加密密钥", 365L * 24 * 60 * 60 * 1000),  // 默认1年有效期
        TEMPORARY("临时密钥", 24L * 60 * 60 * 1000);        // 默认24小时有效期
        
        private final String description;
        private final long defaultValidity;
        
        KeyType(String description, long defaultValidity) {
            this.description = description;
            this.defaultValidity = defaultValidity;
        }
        
        public String getDescription() {
            return description;
        }
        
        public long getDefaultValidity() {
            return defaultValidity;
        }
    }
}
