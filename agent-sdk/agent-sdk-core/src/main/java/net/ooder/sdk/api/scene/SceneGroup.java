package net.ooder.sdk.api.scene;

import java.util.List;
import java.util.Map;

import net.ooder.sdk.common.enums.MemberRole;
import net.ooder.skills.api.SceneType;
import net.ooder.skills.sync.UserSceneGroup;

/**
 * 场景组类
 * 
 * <p>场景组是场景内的高可用集群，包含多个成员（主成员和备份成员）。
 * 场景组提供故障转移、状态共享和密钥管理等功能。</p>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class SceneGroup {
    
    /** 场景组ID */
    private String sceneGroupId;
    
    /** 所属场景ID */
    private String sceneId;
    
    /** 成员列表 */
    private List<SceneMember> members;
    
    /** 场景组密钥 */
    private SceneGroupKey key;
    
    /** 状态 */
    private String status;
    
    /** 创建时间 */
    private long createTime;
    
    /** 最后更新时间 */
    private long lastUpdateTime;
    
    /** 属性 */
    private Map<String, Object> properties;
    
    /** 最大成员数 */
    private int maxMembers;
    
    /** 心跳间隔（毫秒） */
    private int heartbeatInterval;
    
    /** 心跳超时（毫秒） */
    private int heartbeatTimeout;
    
    /** 是否自动故障转移 */
    private boolean autoFailover;
    
    /** 前一主成员ID */
    private String previousPrimaryId;
    
    /** 成为主成员的时间 */
    private long primarySince;
    
    /** 故障转移次数 */
    private int failoverCount;
    
    /** 共享状态 */
    private Map<String, Object> sharedState;
    
    /** 最后状态更新时间 */
    private long lastStateUpdate;
    
    /** 待处理邀请列表 */
    private List<String> pendingInvitations;
    
    /**
     * 获取场景组ID
     * 
     * @return 场景组ID
     */
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    /**
     * 设置场景组ID
     * 
     * @param sceneGroupId 场景组ID
     */
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    /**
     * 获取场景ID
     * 
     * @return 场景ID
     */
    public String getSceneId() {
        return sceneId;
    }
    
    /**
     * 设置场景ID
     * 
     * @param sceneId 场景ID
     */
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    /**
     * 获取成员列表
     * 
     * @return 成员列表
     */
    public List<SceneMember> getMembers() {
        return members;
    }
    
    /**
     * 设置成员列表
     * 
     * @param members 成员列表
     */
    public void setMembers(List<SceneMember> members) {
        this.members = members;
    }
    
    /**
     * 获取场景组密钥
     * 
     * @return 场景组密钥
     */
    public SceneGroupKey getKey() {
        return key;
    }
    
    /**
     * 设置场景组密钥
     * 
     * @param key 场景组密钥
     */
    public void setKey(SceneGroupKey key) {
        this.key = key;
    }
    
    /**
     * 获取状态
     * 
     * @return 状态
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * 设置状态
     * 
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * 获取创建时间
     * 
     * @return 创建时间戳
     */
    public long getCreateTime() {
        return createTime;
    }
    
    /**
     * 设置创建时间
     * 
     * @param createTime 创建时间戳
     */
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    /**
     * 获取最后更新时间
     * 
     * @return 最后更新时间戳
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    /**
     * 设置最后更新时间
     * 
     * @param lastUpdateTime 最后更新时间戳
     */
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    /**
     * 获取属性
     * 
     * @return 属性映射
     */
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * 设置属性
     * 
     * @param properties 属性映射
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    /**
     * 获取最大成员数
     * 
     * @return 最大成员数
     */
    public int getMaxMembers() {
        return maxMembers;
    }
    
    /**
     * 设置最大成员数
     * 
     * @param maxMembers 最大成员数
     */
    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }
    
    /**
     * 获取心跳间隔
     * 
     * @return 心跳间隔（毫秒）
     */
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }
    
    /**
     * 设置心跳间隔
     * 
     * @param heartbeatInterval 心跳间隔（毫秒）
     */
    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
    
    /**
     * 获取心跳超时
     * 
     * @return 心跳超时（毫秒）
     */
    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }
    
    /**
     * 设置心跳超时
     * 
     * @param heartbeatTimeout 心跳超时（毫秒）
     */
    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }
    
    /**
     * 判断是否自动故障转移
     * 
     * @return true表示自动故障转移
     */
    public boolean isAutoFailover() {
        return autoFailover;
    }
    
    /**
     * 设置自动故障转移
     * 
     * @param autoFailover 是否自动故障转移
     */
    public void setAutoFailover(boolean autoFailover) {
        this.autoFailover = autoFailover;
    }
    
    /**
     * 获取前一主成员ID
     * 
     * @return 前一主成员ID
     */
    public String getPreviousPrimaryId() {
        return previousPrimaryId;
    }
    
    /**
     * 设置前一主成员ID
     * 
     * @param previousPrimaryId 前一主成员ID
     */
    public void setPreviousPrimaryId(String previousPrimaryId) {
        this.previousPrimaryId = previousPrimaryId;
    }
    
    /**
     * 获取成为主成员的时间
     * 
     * @return 时间戳
     */
    public long getPrimarySince() {
        return primarySince;
    }
    
    /**
     * 设置成为主成员的时间
     * 
     * @param primarySince 时间戳
     */
    public void setPrimarySince(long primarySince) {
        this.primarySince = primarySince;
    }
    
    /**
     * 获取故障转移次数
     * 
     * @return 故障转移次数
     */
    public int getFailoverCount() {
        return failoverCount;
    }
    
    /**
     * 设置故障转移次数
     * 
     * @param failoverCount 故障转移次数
     */
    public void setFailoverCount(int failoverCount) {
        this.failoverCount = failoverCount;
    }
    
    /**
     * 获取共享状态
     * 
     * @return 共享状态
     */
    public Map<String, Object> getSharedState() {
        return sharedState;
    }
    
    /**
     * 设置共享状态
     * 
     * @param sharedState 共享状态
     */
    public void setSharedState(Map<String, Object> sharedState) {
        this.sharedState = sharedState;
    }
    
    /**
     * 获取最后状态更新时间
     * 
     * @return 时间戳
     */
    public long getLastStateUpdate() {
        return lastStateUpdate;
    }
    
    /**
     * 设置最后状态更新时间
     * 
     * @param lastStateUpdate 时间戳
     */
    public void setLastStateUpdate(long lastStateUpdate) {
        this.lastStateUpdate = lastStateUpdate;
    }
    
    /**
     * 获取待处理邀请列表
     * 
     * @return 邀请列表
     */
    public List<String> getPendingInvitations() {
        return pendingInvitations;
    }
    
    /**
     * 设置待处理邀请列表
     * 
     * @param pendingInvitations 邀请列表
     */
    public void setPendingInvitations(List<String> pendingInvitations) {
        this.pendingInvitations = pendingInvitations;
    }
    
    /**
     * 获取主成员
     * 
     * @return 主成员，如果没有则返回null
     */
    public SceneMember getPrimary() {
        if (members == null) return null;
        return members.stream()
            .filter(m -> m.getRole() == MemberRole.PRIMARY)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 获取备份成员列表
     * 
     * @return 备份成员列表
     */
    public List<SceneMember> getBackups() {
        if (members == null) return java.util.Collections.emptyList();
        return members.stream()
            .filter(m -> m.getRole() == MemberRole.BACKUP)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取成员数量
     * 
     * @return 成员数量
     */
    public int getMemberCount() {
        return members != null ? members.size() : 0;
    }
    
    /**
     * 判断是否包含指定成员
     * 
     * @param agentId Agent ID
     * @return true表示包含该成员
     */
    public boolean hasMember(String agentId) {
        if (members == null) return false;
        return members.stream().anyMatch(m -> agentId.equals(m.getAgentId()));
    }
    
    /**
     * 获取指定成员
     * 
     * @param agentId Agent ID
     * @return 成员对象，如果不存在则返回null
     */
    public SceneMember getMember(String agentId) {
        if (members == null) return null;
        return members.stream()
            .filter(m -> agentId.equals(m.getAgentId()))
            .findFirst()
            .orElse(null);
    }
    
    private Map<String, UserSceneGroup> userSceneGroups;
    
    public Map<String, UserSceneGroup> getUserSceneGroups() {
        return userSceneGroups;
    }
    
    public void setUserSceneGroups(Map<String, UserSceneGroup> userSceneGroups) {
        this.userSceneGroups = userSceneGroups;
    }
    
    public UserSceneGroup getOrCreateUserSceneGroup(String userId) {
        if (userSceneGroups == null) {
            userSceneGroups = new java.util.concurrent.ConcurrentHashMap<>();
        }
        UserSceneGroup existing = userSceneGroups.get(userId);
        if (existing != null) {
            return existing;
        }
        return null;
    }
    
    @Deprecated
    public UserSceneGroup getOrCreateUserSceneGroup(String userId, 
            java.util.function.BiFunction<String, String, UserSceneGroup> factory) {
        if (userSceneGroups == null) {
            userSceneGroups = new java.util.concurrent.ConcurrentHashMap<>();
        }
        return userSceneGroups.computeIfAbsent(userId, 
            uid -> factory.apply(sceneGroupId, uid));
    }
    
    public void putUserSceneGroup(String userId, UserSceneGroup userSceneGroup) {
        if (userSceneGroups == null) {
            userSceneGroups = new java.util.concurrent.ConcurrentHashMap<>();
        }
        userSceneGroups.put(userId, userSceneGroup);
    }
    
    public List<UserSceneGroup> getAllUserSceneGroups() {
        if (userSceneGroups == null || userSceneGroups.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return new java.util.ArrayList<>(userSceneGroups.values());
    }
    
    public void removeUserSceneGroup(String userId) {
        if (userSceneGroups != null) {
            userSceneGroups.remove(userId);
        }
    }
    
    public UserSceneGroup getUserSceneGroup(String userId) {
        if (userSceneGroups == null) return null;
        return userSceneGroups.get(userId);
    }

    private String defaultAgentId;
    private String llmConfigId;
    private List<String> knowledgeBaseIds;
    private Map<String, Object> extensions;

    public String getDefaultAgentId() {
        return defaultAgentId;
    }

    public void setDefaultAgentId(String defaultAgentId) {
        this.defaultAgentId = defaultAgentId;
    }

    public String getLlmConfigId() {
        return llmConfigId;
    }

    public void setLlmConfigId(String llmConfigId) {
        this.llmConfigId = llmConfigId;
    }

    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }

    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }

    public void addKnowledgeBaseId(String knowledgeBaseId) {
        if (knowledgeBaseIds == null) {
            knowledgeBaseIds = new java.util.ArrayList<>();
        }
        if (!knowledgeBaseIds.contains(knowledgeBaseId)) {
            knowledgeBaseIds.add(knowledgeBaseId);
        }
    }

    public void removeKnowledgeBaseId(String knowledgeBaseId) {
        if (knowledgeBaseIds != null) {
            knowledgeBaseIds.remove(knowledgeBaseId);
        }
    }

    public boolean hasKnowledgeBase(String knowledgeBaseId) {
        return knowledgeBaseIds != null && knowledgeBaseIds.contains(knowledgeBaseId);
    }

    public void addExtension(String key, Object value) {
        if (extensions == null) {
            extensions = new java.util.HashMap<>();
        }
        extensions.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtension(String key) {
        if (extensions == null) return null;
        return (T) extensions.get(key);
    }
}
