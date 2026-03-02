package net.ooder.skills.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 场景组管理器接口
 * 
 * 管理场景组的生命周期，包括创建、添加/移除协作场景、状态管理等
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SceneGroupManager {
    
    /**
     * 创建场景组
     *
     * @param request 场景组创建请求
     * @return 场景组信息
     */
    CompletableFuture<SceneGroupInfo> createGroup(SceneGroupRequest request);
    
    /**
     * 解散场景组
     *
     * @param groupId 场景组ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> disbandGroup(String groupId);
    
    /**
     * 添加协作场景（新术语：协作能力）
     *
     * @param groupId 场景组ID
     * @param capabilityId 协作能力ID（原协作场景ID）
     * @return 是否成功
     */
    CompletableFuture<Boolean> addCollaborativeCapability(String groupId, String capabilityId);
    
    /**
     * 添加协作场景（带配置）
     *
     * @param groupId 场景组ID
     * @param capabilityId 协作能力ID
     * @param config 协作配置
     * @return 是否成功
     */
    CompletableFuture<Boolean> addCollaborativeCapability(String groupId, String capabilityId, CollaborativeConfig config);
    
    /**
     * 移除协作场景
     *
     * @param groupId 场景组ID
     * @param capabilityId 协作能力ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> removeCollaborativeCapability(String groupId, String capabilityId);
    
    /**
     * 获取场景组信息
     *
     * @param groupId 场景组ID
     * @return 场景组信息
     */
    CompletableFuture<SceneGroupInfo> getGroupInfo(String groupId);
    
    /**
     * 列出所有场景组
     *
     * @return 场景组列表
     */
    CompletableFuture<List<SceneGroupInfo>> listGroups();
    
    /**
     * 列出指定主场景的场景组
     *
     * @param mainCapabilityId 主场景能力ID
     * @return 场景组列表
     */
    CompletableFuture<List<SceneGroupInfo>> listGroupsByMainCapability(String mainCapabilityId);
    
    /**
     * 更新场景组状态
     *
     * @param groupId 场景组ID
     * @param status 新状态
     * @return 是否成功
     */
    CompletableFuture<Boolean> updateGroupStatus(String groupId, SceneGroupStatus status);
    
    /**
     * 同步场景组状态
     *
     * @param groupId 场景组ID
     * @param state 共享状态
     * @return 是否成功
     */
    CompletableFuture<Boolean> syncGroupState(String groupId, Map<String, Object> state);
    
    /**
     * 获取场景组共享状态
     *
     * @param groupId 场景组ID
     * @return 共享状态
     */
    CompletableFuture<Map<String, Object>> getGroupState(String groupId);
    
    /**
     * 检查场景组是否存在
     *
     * @param groupId 场景组ID
     * @return 是否存在
     */
    boolean exists(String groupId);
    
    /**
     * 添加场景组监听器
     *
     * @param listener 监听器
     */
    void addGroupListener(SceneGroupListener listener);
    
    /**
     * 移除场景组监听器
     *
     * @param listener 监听器
     */
    void removeGroupListener(SceneGroupListener listener);
    
    // ========== 数据类定义 ==========
    
    /**
     * 场景组创建请求
     */
    class SceneGroupRequest {
        private String groupId;
        private String mainCapabilityId;  // 主场景能力ID（原主场景ID）
        private List<String> collaborativeCapabilityIds;  // 协作能力ID列表
        private Map<String, Object> sharedState;  // 初始共享状态
        private Map<String, Object> config;  // 场景组配置
        
        // Getters and Setters
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public String getMainCapabilityId() { return mainCapabilityId; }
        public void setMainCapabilityId(String mainCapabilityId) { this.mainCapabilityId = mainCapabilityId; }
        public List<String> getCollaborativeCapabilityIds() { return collaborativeCapabilityIds; }
        public void setCollaborativeCapabilityIds(List<String> collaborativeCapabilityIds) { this.collaborativeCapabilityIds = collaborativeCapabilityIds; }
        public Map<String, Object> getSharedState() { return sharedState; }
        public void setSharedState(Map<String, Object> sharedState) { this.sharedState = sharedState; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }
    
    /**
     * 场景组信息
     */
    class SceneGroupInfo {
        private String groupId;
        private String mainCapabilityId;
        private List<CollaborativeCapabilityInfo> collaborativeCapabilities;
        private SceneGroupStatus status;
        private Map<String, Object> sharedState;
        private long createTime;
        private long lastUpdateTime;
        private String description;
        
        // Getters and Setters
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public String getMainCapabilityId() { return mainCapabilityId; }
        public void setMainCapabilityId(String mainCapabilityId) { this.mainCapabilityId = mainCapabilityId; }
        public List<CollaborativeCapabilityInfo> getCollaborativeCapabilities() { return collaborativeCapabilities; }
        public void setCollaborativeCapabilities(List<CollaborativeCapabilityInfo> collaborativeCapabilities) { this.collaborativeCapabilities = collaborativeCapabilities; }
        public SceneGroupStatus getStatus() { return status; }
        public void setStatus(SceneGroupStatus status) { this.status = status; }
        public Map<String, Object> getSharedState() { return sharedState; }
        public void setSharedState(Map<String, Object> sharedState) { this.sharedState = sharedState; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 协作能力信息
     */
    class CollaborativeCapabilityInfo {
        private String capabilityId;
        private String role;  // 角色：primary, secondary, observer等
        private CollaborativeStatus status;
        private Map<String, Object> config;
        private long joinTime;
        
        public enum CollaborativeStatus {
            PENDING,      // 待加入
            JOINING,      // 加入中
            ACTIVE,       // 活跃
            PAUSED,       // 暂停
            DISCONNECTED, // 断开
            LEFT          // 已离开
        }
        
        // Getters and Setters
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public CollaborativeStatus getStatus() { return status; }
        public void setStatus(CollaborativeStatus status) { this.status = status; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public long getJoinTime() { return joinTime; }
        public void setJoinTime(long joinTime) { this.joinTime = joinTime; }
    }
    
    /**
     * 协作配置
     */
    class CollaborativeConfig {
        private String role;
        private Map<String, Object> initParams;
        private boolean autoSync;
        private List<String> permissions;
        
        // Getters and Setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Map<String, Object> getInitParams() { return initParams; }
        public void setInitParams(Map<String, Object> initParams) { this.initParams = initParams; }
        public boolean isAutoSync() { return autoSync; }
        public void setAutoSync(boolean autoSync) { this.autoSync = autoSync; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    }
    
    /**
     * 场景组状态
     */
    enum SceneGroupStatus {
        CREATING,     // 创建中
        ACTIVE,       // 活跃
        PAUSED,       // 暂停
        DISBANDING,   // 解散中
        DISBANDED     // 已解散
    }
    
    /**
     * 场景组监听器
     */
    interface SceneGroupListener {
        void onGroupCreated(SceneGroupInfo group);
        void onGroupDisbanded(String groupId);
        void onCapabilityAdded(String groupId, String capabilityId);
        void onCapabilityRemoved(String groupId, String capabilityId);
        void onGroupStatusChanged(String groupId, SceneGroupStatus oldStatus, SceneGroupStatus newStatus);
        void onGroupStateSynced(String groupId, Map<String, Object> state);
    }
}
