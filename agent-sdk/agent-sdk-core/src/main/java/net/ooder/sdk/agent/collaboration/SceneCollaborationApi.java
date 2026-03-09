package net.ooder.sdk.agent.collaboration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 场景协作 API
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface SceneCollaborationApi {

    /**
     * 加入场景组
     * @param groupId 场景组ID
     * @param role 角色
     * @param capabilities 能力列表
     * @return 是否成功
     */
    CompletableFuture<Boolean> joinGroup(String groupId, MemberRole role, List<String> capabilities);

    /**
     * 离开场景组
     * @param groupId 场景组ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> leaveGroup(String groupId);

    /**
     * 订阅场景组事件
     * @param groupId 场景组ID
     * @param eventTypes 事件类型列表
     * @param listener 事件监听器
     * @return 订阅ID
     */
    CompletableFuture<String> subscribeGroupEvents(String groupId, List<String> eventTypes, SceneEventListener listener);

    /**
     * 取消订阅
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> unsubscribeGroupEvents(String subscriptionId);

    /**
     * 发布场景组事件
     * @param groupId 场景组ID
     * @param eventType 事件类型
     * @param payload 事件数据
     * @return 是否成功
     */
    CompletableFuture<Boolean> publishGroupEvent(String groupId, String eventType, Map<String, Object> payload);

    /**
     * 获取场景组状态
     * @param groupId 场景组ID
     * @return 场景组状态
     */
    CompletableFuture<SceneGroupState> getGroupState(String groupId);

    /**
     * 获取成员列表
     * @param groupId 场景组ID
     * @return 成员列表
     */
    CompletableFuture<List<SceneMemberInfo>> getGroupMembers(String groupId);

    /**
     * 邀请成员加入
     * @param groupId 场景组ID
     * @param agentId Agent ID
     * @param role 角色
     * @return 是否成功
     */
    CompletableFuture<Boolean> inviteMember(String groupId, String agentId, MemberRole role);

    /**
     * 移除成员
     * @param groupId 场景组ID
     * @param agentId Agent ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> removeMember(String groupId, String agentId);

    /**
     * 角色枚举
     */
    enum MemberRole {
        HOST,
        PARTICIPANT,
        OBSERVER
    }

    /**
     * 场景组状态
     */
    class SceneGroupState {
        private String groupId;
        private String status;
        private int memberCount;
        private long lastActivityTime;
        private Map<String, Object> metadata;

        // Getters and Setters
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getMemberCount() { return memberCount; }
        public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
        public long getLastActivityTime() { return lastActivityTime; }
        public void setLastActivityTime(long lastActivityTime) { this.lastActivityTime = lastActivityTime; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * 场景成员信息
     */
    class SceneMemberInfo {
        private String agentId;
        private MemberRole role;
        private String status;
        private List<String> capabilities;
        private long joinTime;

        // Getters and Setters
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public MemberRole getRole() { return role; }
        public void setRole(MemberRole role) { this.role = role; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<String> getCapabilities() { return capabilities; }
        public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
        public long getJoinTime() { return joinTime; }
        public void setJoinTime(long joinTime) { this.joinTime = joinTime; }
    }

    /**
     * 场景事件监听器
     */
    interface SceneEventListener {
        void onEvent(String groupId, String eventType, Map<String, Object> payload);
    }
}
