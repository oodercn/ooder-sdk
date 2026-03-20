package net.ooder.skills.sync.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.ooder.skills.api.SceneGroupManager;
import net.ooder.skills.common.enums.MemberRole;
import net.ooder.skills.sync.CapabilityBinding;
import net.ooder.skills.sync.CollaborationSession;
import net.ooder.skills.sync.KnowledgeBinding;
import net.ooder.skills.sync.SkillBinding;
import net.ooder.skills.sync.event.SyncEventPublisher;
import net.ooder.skills.sync.model.AgentStatusInfo;
import net.ooder.skills.sync.model.CommunicationLinkInfo;
import net.ooder.skills.sync.model.FailoverStatusInfo;
import net.ooder.skills.sync.model.HeartbeatInfo;

public class BidirectionalSyncCoordinator {
    
    private final UserSceneGroupImpl userSceneGroup;
    private final SceneGroupManager sdkSceneGroupManager;
    private final SyncEventPublisher eventPublisher;
    
    public BidirectionalSyncCoordinator(UserSceneGroupImpl userSceneGroup,
                                        SceneGroupManager sdkSceneGroupManager,
                                        SyncEventPublisher eventPublisher) {
        this.userSceneGroup = userSceneGroup;
        this.sdkSceneGroupManager = sdkSceneGroupManager;
        this.eventPublisher = eventPublisher;
    }
    
    public void syncActivationToSdk() {
        try {
            sdkSceneGroupManager.updateGroupStatus(
                userSceneGroup.getSceneGroupId(),
                SceneGroupManager.SceneGroupStatus.ACTIVE
            );
            eventPublisher.publishSyncSuccess("activation", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("activation", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncDeactivationToSdk() {
        try {
            sdkSceneGroupManager.updateGroupStatus(
                userSceneGroup.getSceneGroupId(),
                SceneGroupManager.SceneGroupStatus.PAUSED
            );
            eventPublisher.publishSyncSuccess("deactivation", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("deactivation", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncParticipantJoinToSdk(String userId, MemberRole role) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "join");
            state.put("userId", userId);
            state.put("role", role.getCode());
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("participant_join", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("participant_join", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncParticipantLeaveToSdk(String userId) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "leave");
            state.put("userId", userId);
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("participant_leave", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("participant_leave", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncParticipantRoleChangeToSdk(String userId, MemberRole newRole) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "role_change");
            state.put("userId", userId);
            state.put("newRole", newRole.getCode());
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("participant_role_change", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("participant_role_change", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncSkillBindingToSdk(SkillBinding binding) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "skill_bind");
            state.put("skillId", binding.getSkillId());
            state.put("config", binding.getConfig());
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("skill_binding", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("skill_binding", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncSkillUnbindingToSdk(String skillId) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "skill_unbind");
            state.put("skillId", skillId);
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("skill_unbinding", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("skill_unbinding", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncSkillConfigUpdateToSdk(String skillId, Map<String, Object> config) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "skill_config_update");
            state.put("skillId", skillId);
            state.put("config", config);
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("skill_config_update", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("skill_config_update", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncCollaborationStartToSdk(CollaborationSession session) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "collaboration_start");
            state.put("sessionId", session.getSessionId());
            state.put("collaborationType", session.getCollaborationType());
            state.put("participants", session.getParticipantIds());
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("collaboration_start", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("collaboration_start", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncCollaborationEndToSdk(CollaborationSession session) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "collaboration_end");
            state.put("sessionId", session.getSessionId());
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("collaboration_end", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("collaboration_end", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncCapabilityBindingToSdk(CapabilityBinding binding) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "capability_bind");
            state.put("capabilityId", binding.getCapabilityId());
            state.put("config", binding.getConfig());
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("capability_binding", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("capability_binding", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncCapabilityUnbindingToSdk(String bindingId) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "capability_unbind");
            state.put("bindingId", bindingId);
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("capability_unbinding", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("capability_unbinding", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncKnowledgeBindingToSdk(KnowledgeBinding binding) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "knowledge_bind");
            state.put("kbId", binding.getKnowledgeBaseId());
            state.put("layer", binding.getLayer());
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("knowledge_binding", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("knowledge_binding", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncKnowledgeUnbindingToSdk(String kbId) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "knowledge_unbind");
            state.put("kbId", kbId);
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("knowledge_unbinding", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("knowledge_unbinding", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncSharedStateToSdk(Map<String, Object> sharedState) {
        try {
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), sharedState);
            eventPublisher.publishSyncSuccess("shared_state", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("shared_state", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncAllStateToSdk() {
        try {
            Map<String, Object> fullState = new HashMap<>();
            fullState.put("sceneGroupId", userSceneGroup.getSceneGroupId());
            fullState.put("status", userSceneGroup.getStatus());
            fullState.put("sharedState", userSceneGroup.getSharedState());
            fullState.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), fullState);
            eventPublisher.publishSyncSuccess("full_state", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("full_state", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public void syncAllStateFromSdk() {
        try {
            CompletableFuture<Map<String, Object>> future = 
                sdkSceneGroupManager.getGroupState(userSceneGroup.getSceneGroupId());
            
            future.thenAccept(state -> {
                if (state != null) {
                    eventPublisher.publishSyncSuccess("state_from_sdk", userSceneGroup.getSceneGroupId());
                }
            }).exceptionally(e -> {
                eventPublisher.publishSyncFailure("state_from_sdk", userSceneGroup.getSceneGroupId(), e);
                return null;
            });
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("state_from_sdk", userSceneGroup.getSceneGroupId(), e);
        }
    }
    
    public AgentStatusInfo getAgentStatusFromSdk(String agentId) {
        try {
            CompletableFuture<SceneGroupManager.SceneGroupInfo> future = 
                sdkSceneGroupManager.getGroupInfo(userSceneGroup.getSceneGroupId());
            
            SceneGroupManager.SceneGroupInfo info = future.join();
            if (info != null && info.getSharedState() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> members = 
                    (List<Map<String, Object>>) info.getSharedState().get("members");
                
                if (members != null) {
                    for (Map<String, Object> member : members) {
                        if (agentId.equals(member.get("agentId"))) {
                            return AgentStatusInfo.builder()
                                .agentId((String) member.get("agentId"))
                                .agentName((String) member.get("agentName"))
                                .status((String) member.get("status"))
                                .role((String) member.get("role"))
                                .lastHeartbeat(member.get("lastHeartbeat") != null 
                                    ? ((Number) member.get("lastHeartbeat")).longValue() : 0)
                                .endpoint((String) member.get("endpoint"))
                                .heartbeatMissed(member.get("heartbeatMissed") != null 
                                    ? ((Number) member.get("heartbeatMissed")).intValue() : 0)
                                .build();
                        }
                    }
                }
            }
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("agent_status", userSceneGroup.getSceneGroupId(), e);
        }
        return null;
    }
    
    public List<AgentStatusInfo> getAllAgentStatusesFromSdk() {
        List<AgentStatusInfo> statuses = new ArrayList<>();
        try {
            CompletableFuture<SceneGroupManager.SceneGroupInfo> future = 
                sdkSceneGroupManager.getGroupInfo(userSceneGroup.getSceneGroupId());
            
            SceneGroupManager.SceneGroupInfo info = future.join();
            if (info != null && info.getSharedState() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> members = 
                    (List<Map<String, Object>>) info.getSharedState().get("members");
                
                if (members != null) {
                    for (Map<String, Object> member : members) {
                        statuses.add(AgentStatusInfo.builder()
                            .agentId((String) member.get("agentId"))
                            .agentName((String) member.get("agentName"))
                            .status((String) member.get("status"))
                            .role((String) member.get("role"))
                            .lastHeartbeat(member.get("lastHeartbeat") != null 
                                ? ((Number) member.get("lastHeartbeat")).longValue() : 0)
                            .endpoint((String) member.get("endpoint"))
                            .heartbeatMissed(member.get("heartbeatMissed") != null 
                                ? ((Number) member.get("heartbeatMissed")).intValue() : 0)
                            .build());
                    }
                }
            }
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("all_agent_statuses", userSceneGroup.getSceneGroupId(), e);
        }
        return statuses;
    }
    
    public AgentStatusInfo getPrimaryAgentFromSdk() {
        List<AgentStatusInfo> all = getAllAgentStatusesFromSdk();
        return all.stream()
            .filter(a -> "PRIMARY".equals(a.getRole()) || "primary".equals(a.getRole()))
            .findFirst()
            .orElse(null);
    }
    
    public List<AgentStatusInfo> getBackupAgentsFromSdk() {
        List<AgentStatusInfo> all = getAllAgentStatusesFromSdk();
        List<AgentStatusInfo> backups = new ArrayList<>();
        for (AgentStatusInfo a : all) {
            if ("BACKUP".equals(a.getRole()) || "backup".equals(a.getRole())) {
                backups.add(a);
            }
        }
        return backups;
    }
    
    public CommunicationLinkInfo getCommunicationLinksFromSdk() {
        try {
            AgentStatusInfo primary = getPrimaryAgentFromSdk();
            List<AgentStatusInfo> backups = getBackupAgentsFromSdk();
            
            CommunicationLinkInfo.Builder builder = CommunicationLinkInfo.builder()
                .sceneGroupId(userSceneGroup.getSceneGroupId());
            
            if (primary != null) {
                builder.primaryAgentId(primary.getAgentId())
                       .primaryEndpoint(primary.getEndpoint());
            }
            
            List<CommunicationLinkInfo.AgentInfo> backupInfos = new ArrayList<>();
            for (AgentStatusInfo backup : backups) {
                backupInfos.add(CommunicationLinkInfo.AgentInfo.builder()
                    .agentId(backup.getAgentId())
                    .endpoint(backup.getEndpoint())
                    .build());
            }
            builder.backupAgents(backupInfos);
            
            return builder.build();
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("communication_links", userSceneGroup.getSceneGroupId(), e);
            return new CommunicationLinkInfo();
        }
    }
    
    public String getAgentEndpointFromSdk(String agentId) {
        AgentStatusInfo status = getAgentStatusFromSdk(agentId);
        return status != null ? status.getEndpoint() : null;
    }
    
    public HeartbeatInfo getHeartbeatInfoFromSdk(String agentId) {
        AgentStatusInfo status = getAgentStatusFromSdk(agentId);
        if (status != null) {
            return HeartbeatInfo.builder()
                .agentId(agentId)
                .lastHeartbeat(status.getLastHeartbeat())
                .missedCount(status.getHeartbeatMissed())
                .status(status.isHealthy() ? HeartbeatInfo.STATUS_HEALTHY : HeartbeatInfo.STATUS_CRITICAL)
                .build();
        }
        return null;
    }
    
    public void triggerFailoverOnSdk(String failedAgentId) {
        try {
            Map<String, Object> state = new HashMap<>();
            state.put("action", "trigger_failover");
            state.put("failedAgentId", failedAgentId);
            state.put("timestamp", System.currentTimeMillis());
            
            sdkSceneGroupManager.syncGroupState(userSceneGroup.getSceneGroupId(), state);
            eventPublisher.publishSyncSuccess("trigger_failover", userSceneGroup.getSceneGroupId());
        } catch (Exception e) {
            eventPublisher.publishSyncFailure("trigger_failover", userSceneGroup.getSceneGroupId(), e);
        }
    }
}
