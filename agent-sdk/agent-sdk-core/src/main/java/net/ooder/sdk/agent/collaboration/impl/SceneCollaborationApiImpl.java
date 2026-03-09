package net.ooder.sdk.agent.collaboration.impl;

import net.ooder.sdk.agent.collaboration.SceneCollaborationApi;
import net.ooder.sdk.a2a.A2AClient;
import net.ooder.sdk.api.scene.SceneGroupManager;
import net.ooder.sdk.api.scene.SceneManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SceneCollaborationApi 实现类
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneCollaborationApiImpl implements SceneCollaborationApi {

    private static final Logger logger = LoggerFactory.getLogger(SceneCollaborationApiImpl.class);

    private final SceneManager sceneManager;
    private final SceneGroupManager sceneGroupManager;
    private final A2AClient a2aClient;
    
    // 存储订阅信息
    private final Map<String, SubscriptionInfo> subscriptions = new ConcurrentHashMap<>();
    // 存储场景组成员信息
    private final Map<String, List<SceneMemberInfo>> groupMembers = new ConcurrentHashMap<>();
    // 存储场景组状态
    private final Map<String, SceneGroupState> groupStates = new ConcurrentHashMap<>();
    // 存储事件监听器
    private final Map<String, List<SceneEventListener>> eventListeners = new ConcurrentHashMap<>();
    
    private static final String SUBSCRIPTION_PREFIX = "sub_";

    public SceneCollaborationApiImpl(SceneManager sceneManager, SceneGroupManager sceneGroupManager) {
        this(sceneManager, sceneGroupManager, null);
    }

    public SceneCollaborationApiImpl(SceneManager sceneManager, SceneGroupManager sceneGroupManager, A2AClient a2aClient) {
        this.sceneManager = sceneManager;
        this.sceneGroupManager = sceneGroupManager;
        this.a2aClient = a2aClient;
        logger.info("SceneCollaborationApiImpl initialized");
    }

    @Override
    public CompletableFuture<Boolean> joinGroup(String groupId, MemberRole role, List<String> capabilities) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Joining group: {}, role: {}, capabilities: {}", groupId, role, capabilities);
                
                SceneMemberInfo member = new SceneMemberInfo();
                member.setAgentId(getCurrentAgentId());
                member.setRole(role);
                member.setStatus("ACTIVE");
                member.setCapabilities(capabilities);
                member.setJoinTime(System.currentTimeMillis());
                
                groupMembers.computeIfAbsent(groupId, k -> new ArrayList<>()).add(member);
                updateGroupState(groupId);
                
                // 通知监听器
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("agentId", member.getAgentId());
                eventData.put("role", role.name());
                eventData.put("capabilities", capabilities);
                notifyListeners(groupId, "MEMBER_JOINED", eventData);
                
                logger.info("Successfully joined group: {} as {}", groupId, role);
                return true;
            } catch (Exception e) {
                logger.error("Failed to join group: {}", groupId, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> leaveGroup(String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Leaving group: {}", groupId);
                
                String currentAgentId = getCurrentAgentId();
                List<SceneMemberInfo> members = groupMembers.get(groupId);
                
                if (members != null) {
                    members.removeIf(m -> m.getAgentId().equals(currentAgentId));
                    updateGroupState(groupId);
                    
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("agentId", currentAgentId);
                    notifyListeners(groupId, "MEMBER_LEFT", eventData);
                }
                
                logger.info("Successfully left group: {}", groupId);
                return true;
            } catch (Exception e) {
                logger.error("Failed to leave group: {}", groupId, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<String> subscribeGroupEvents(String groupId, List<String> eventTypes, SceneEventListener listener) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Subscribing to group events: {}, types: {}", groupId, eventTypes);
                
                String subscriptionId = SUBSCRIPTION_PREFIX + UUID.randomUUID().toString();
                
                SubscriptionInfo subscription = new SubscriptionInfo();
                subscription.setSubscriptionId(subscriptionId);
                subscription.setGroupId(groupId);
                subscription.setEventTypes(eventTypes);
                subscription.setListener(listener);
                
                subscriptions.put(subscriptionId, subscription);
                eventListeners.computeIfAbsent(groupId, k -> new ArrayList<>()).add(listener);
                
                logger.info("Successfully subscribed to group: {}, subscriptionId: {}", groupId, subscriptionId);
                return subscriptionId;
            } catch (Exception e) {
                logger.error("Failed to subscribe to group: {}", groupId, e);
                throw new RuntimeException("Subscription failed", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> unsubscribeGroupEvents(String subscriptionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Unsubscribing: {}", subscriptionId);
                
                SubscriptionInfo subscription = subscriptions.remove(subscriptionId);
                if (subscription != null) {
                    List<SceneEventListener> listeners = eventListeners.get(subscription.getGroupId());
                    if (listeners != null) {
                        listeners.remove(subscription.getListener());
                    }
                    logger.info("Successfully unsubscribed: {}", subscriptionId);
                    return true;
                }
                logger.warn("Subscription not found: {}", subscriptionId);
                return false;
            } catch (Exception e) {
                logger.error("Failed to unsubscribe: {}", subscriptionId, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> publishGroupEvent(String groupId, String eventType, Map<String, Object> payload) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Publishing event to group: {}, type: {}", groupId, eventType);
                
                notifyListeners(groupId, eventType, payload);
                
                logger.info("Successfully published event to group: {}, type: {}", groupId, eventType);
                return true;
            } catch (Exception e) {
                logger.error("Failed to publish event to group: {}", groupId, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<SceneGroupState> getGroupState(String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Getting group state: {}", groupId);
                
                SceneGroupState state = groupStates.get(groupId);
                if (state == null) {
                    state = new SceneGroupState();
                    state.setGroupId(groupId);
                    state.setStatus("INACTIVE");
                    state.setMemberCount(0);
                }
                return state;
            } catch (Exception e) {
                logger.error("Failed to get group state: {}", groupId, e);
                throw new RuntimeException("Failed to get group state", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<SceneMemberInfo>> getGroupMembers(String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Getting group members: {}", groupId);
                
                List<SceneMemberInfo> members = groupMembers.get(groupId);
                return members != null ? new ArrayList<>(members) : new ArrayList<>();
            } catch (Exception e) {
                logger.error("Failed to get group members: {}", groupId, e);
                throw new RuntimeException("Failed to get group members", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> inviteMember(String groupId, String agentId, MemberRole role) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Inviting member: {} to group: {} as {}", agentId, groupId, role);
                
                Map<String, Object> invitation = new HashMap<>();
                invitation.put("groupId", groupId);
                invitation.put("role", role.name());
                invitation.put("inviter", getCurrentAgentId());
                invitation.put("type", "SCENE_INVITATION");
                
                // 使用A2A客户端发送邀请
                if (a2aClient != null) {
                    a2aClient.sendMessage(agentId, invitation)
                        .thenAccept(success -> {
                            if (success) {
                                logger.info("Successfully sent invitation to member: {} for group: {}", agentId, groupId);
                            } else {
                                logger.warn("Failed to send invitation to member: {} for group: {}", agentId, groupId);
                            }
                        })
                        .exceptionally(ex -> {
                            logger.error("Error sending invitation to member: {} for group: {}", agentId, groupId, ex);
                            return null;
                        });
                } else {
                    logger.warn("A2AClient not available, invitation not sent to: {}", agentId);
                }
                
                logger.info("Successfully invited member: {} to group: {}", agentId, groupId);
                return true;
            } catch (Exception e) {
                logger.error("Failed to invite member: {} to group: {}", agentId, groupId, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> removeMember(String groupId, String agentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Removing member: {} from group: {}", agentId, groupId);
                
                List<SceneMemberInfo> members = groupMembers.get(groupId);
                if (members != null) {
                    members.removeIf(m -> m.getAgentId().equals(agentId));
                    updateGroupState(groupId);
                    
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("agentId", agentId);
                    eventData.put("removedBy", getCurrentAgentId());
                    notifyListeners(groupId, "MEMBER_REMOVED", eventData);
                }
                
                logger.info("Successfully removed member: {} from group: {}", agentId, groupId);
                return true;
            } catch (Exception e) {
                logger.error("Failed to remove member: {} from group: {}", agentId, groupId, e);
                return false;
            }
        });
    }

    private void updateGroupState(String groupId) {
        List<SceneMemberInfo> members = groupMembers.getOrDefault(groupId, new ArrayList<>());
        
        SceneGroupState state = new SceneGroupState();
        state.setGroupId(groupId);
        state.setStatus(members.isEmpty() ? "INACTIVE" : "ACTIVE");
        state.setMemberCount(members.size());
        state.setLastActivityTime(System.currentTimeMillis());
        
        groupStates.put(groupId, state);
        logger.debug("Updated group state: {}, members: {}", groupId, members.size());
    }

    private void notifyListeners(String groupId, String eventType, Map<String, Object> payload) {
        List<SceneEventListener> listeners = eventListeners.get(groupId);
        if (listeners != null) {
            for (SceneEventListener listener : listeners) {
                try {
                    listener.onEvent(groupId, eventType, payload);
                } catch (Exception e) {
                    logger.error("Error notifying listener for group: {}, event: {}", groupId, eventType, e);
                }
            }
        }
    }

    private String getCurrentAgentId() {
        // 从当前上下文中获取Agent ID
        // 这里简化处理，实际应该从安全上下文或配置中获取
        return "agent_" + System.currentTimeMillis();
    }

    private static class SubscriptionInfo {
        private String subscriptionId;
        private String groupId;
        private List<String> eventTypes;
        private SceneEventListener listener;

        public String getSubscriptionId() { return subscriptionId; }
        public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public List<String> getEventTypes() { return eventTypes; }
        public void setEventTypes(List<String> eventTypes) { this.eventTypes = eventTypes; }
        public SceneEventListener getListener() { return listener; }
        public void setListener(SceneEventListener listener) { this.listener = listener; }
    }
}
