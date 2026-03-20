package net.ooder.skills.sync.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import net.ooder.skills.api.SceneGroupManager;
import net.ooder.skills.common.enums.MemberRole;
import net.ooder.skills.sync.CapabilityBinding;
import net.ooder.skills.sync.CollaborationSession;
import net.ooder.skills.sync.KnowledgeBinding;
import net.ooder.skills.sync.Participant;
import net.ooder.skills.sync.SkillBinding;
import net.ooder.skills.sync.UserSceneGroup;
import net.ooder.skills.sync.UserSceneGroupAgentProxy;
import net.ooder.skills.sync.event.SyncEventPublisher;
import net.ooder.skills.sync.model.AgentStatusInfo;
import net.ooder.skills.sync.model.CommunicationLinkInfo;
import net.ooder.skills.sync.model.FailoverStatusInfo;
import net.ooder.skills.sync.model.HeartbeatInfo;

public class UserSceneGroupImpl implements UserSceneGroup, UserSceneGroupAgentProxy {
    
    private final String sceneGroupId;
    private final String sceneId;
    private final String userId;
    private Participant.Role role;
    private String status;
    
    private final List<Participant> participants;
    private final Map<String, SkillBinding> skillBindings;
    private final Map<String, CapabilityBinding> capabilityBindings;
    private final Map<String, KnowledgeBinding> knowledgeBindings;
    private final Map<String, CollaborationSession> collaborationSessions;
    private final Map<String, Object> personalContext;
    
    private final SceneGroupManager sdkSceneGroupManager;
    private final SyncEventPublisher eventPublisher;
    private final BidirectionalSyncCoordinator syncCoordinator;
    
    private final Map<String, Object> sharedState;
    private FailoverStatusInfo failoverStatus;
    
    public UserSceneGroupImpl(String sceneGroupId, String sceneId, 
                              SceneGroupManager sdkSceneGroupManager,
                              SyncEventPublisher eventPublisher) {
        this(sceneGroupId, sceneId, null, Participant.Role.EMPLOYEE, sdkSceneGroupManager, eventPublisher);
    }
    
    public UserSceneGroupImpl(String sceneGroupId, String sceneId, String userId, Participant.Role role,
                              SceneGroupManager sdkSceneGroupManager,
                              SyncEventPublisher eventPublisher) {
        this.sceneGroupId = sceneGroupId;
        this.sceneId = sceneId;
        this.userId = userId;
        this.role = role != null ? role : Participant.Role.EMPLOYEE;
        this.status = "created";
        
        this.participants = new CopyOnWriteArrayList<>();
        this.skillBindings = new ConcurrentHashMap<>();
        this.capabilityBindings = new ConcurrentHashMap<>();
        this.knowledgeBindings = new ConcurrentHashMap<>();
        this.collaborationSessions = new ConcurrentHashMap<>();
        this.personalContext = new ConcurrentHashMap<>();
        
        this.sdkSceneGroupManager = sdkSceneGroupManager;
        this.eventPublisher = eventPublisher;
        this.syncCoordinator = new BidirectionalSyncCoordinator(this, sdkSceneGroupManager, eventPublisher);
        
        this.sharedState = new ConcurrentHashMap<>();
        this.failoverStatus = new FailoverStatusInfo();
        this.failoverStatus.setSceneGroupId(sceneGroupId);
    }
    
    @Override
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    @Override
    public String getSceneId() {
        return sceneId;
    }
    
    @Override
    public String getUserId() {
        return userId;
    }
    
    @Override
    public Participant.Role getRole() {
        return role;
    }
    
    @Override
    public void setRole(Participant.Role role) {
        this.role = role;
    }
    
    @Override
    public String getStatus() {
        return status;
    }
    
    @Override
    public void activate() {
        this.status = "active";
        syncCoordinator.syncActivationToSdk();
        eventPublisher.publishSceneGroupActivated(sceneGroupId);
    }
    
    @Override
    public void deactivate() {
        this.status = "inactive";
        syncCoordinator.syncDeactivationToSdk();
        eventPublisher.publishSceneGroupDeactivated(sceneGroupId);
    }
    
    @Override
    public Participant addCollaborator(String userId, Participant.Role role) {
        Participant participant = new Participant();
        participant.setUserId(userId);
        participant.setDisplayName(userId);
        participant.setType(Participant.Type.USER);
        participant.setRole(role);
        
        participants.add(participant);
        
        MemberRole sdkRole = mapToSdkRole(role);
        syncCoordinator.syncParticipantJoinToSdk(userId, sdkRole);
        
        eventPublisher.publishCollaboratorAdded(sceneGroupId, participant);
        
        return participant;
    }
    
    @Override
    public void removeCollaborator(String userId) {
        participants.removeIf(p -> userId.equals(p.getUserId()));
        
        syncCoordinator.syncParticipantLeaveToSdk(userId);
        
        eventPublisher.publishCollaboratorRemoved(sceneGroupId, userId);
    }
    
    @Override
    public void changeCollaboratorRole(String userId, Participant.Role newRole) {
        participants.stream()
            .filter(p -> userId.equals(p.getUserId()))
            .findFirst()
            .ifPresent(p -> p.setRole(newRole));
        
        MemberRole sdkRole = mapToSdkRole(newRole);
        syncCoordinator.syncParticipantRoleChangeToSdk(userId, sdkRole);
        
        eventPublisher.publishCollaboratorRoleChanged(sceneGroupId, userId, newRole);
    }
    
    @Override
    public List<Participant> getCollaborators() {
        return new ArrayList<>(participants);
    }
    
    @Override
    public SkillBinding addSkill(String skillId, Map<String, Object> config) {
        SkillBinding binding = new SkillBinding(skillId, sceneGroupId);
        if (config != null) {
            binding.setConfig(new HashMap<>(config));
        }
        
        skillBindings.put(skillId, binding);
        
        syncCoordinator.syncSkillBindingToSdk(binding);
        
        eventPublisher.publishSkillAdded(sceneGroupId, binding);
        
        return binding;
    }
    
    @Override
    public void removeSkill(String skillId) {
        SkillBinding removed = skillBindings.remove(skillId);
        
        if (removed != null) {
            syncCoordinator.syncSkillUnbindingToSdk(skillId);
            eventPublisher.publishSkillRemoved(sceneGroupId, skillId);
        }
    }
    
    @Override
    public void updateSkillConfig(String skillId, Map<String, Object> config) {
        SkillBinding binding = skillBindings.get(skillId);
        if (binding != null && config != null) {
            binding.setConfig(new HashMap<>(config));
            syncCoordinator.syncSkillConfigUpdateToSdk(skillId, config);
        }
    }
    
    @Override
    public List<SkillBinding> getSkills() {
        return new ArrayList<>(skillBindings.values());
    }
    
    @Override
    public CollaborationSession startCollaboration(String collaborationType, List<String> participantIds) {
        CollaborationSession session = new CollaborationSession(sceneGroupId, collaborationType, participantIds);
        
        collaborationSessions.put(session.getSessionId(), session);
        
        syncCoordinator.syncCollaborationStartToSdk(session);
        
        eventPublisher.publishCollaborationStarted(sceneGroupId, session);
        
        return session;
    }
    
    @Override
    public void endCollaboration(String sessionId) {
        CollaborationSession session = collaborationSessions.get(sessionId);
        if (session != null) {
            session.end();
            
            syncCoordinator.syncCollaborationEndToSdk(session);
            
            eventPublisher.publishCollaborationEnded(sceneGroupId, sessionId);
        }
    }
    
    @Override
    public CollaborationSession getCollaborationSession(String sessionId) {
        return collaborationSessions.get(sessionId);
    }
    
    @Override
    public List<CollaborationSession> getActiveCollaborations() {
        return collaborationSessions.values().stream()
            .filter(CollaborationSession::isActive)
            .collect(Collectors.toList());
    }
    
    @Override
    public CapabilityBinding bindCapability(String capId, Map<String, Object> config) {
        CapabilityBinding binding = new CapabilityBinding(capId, sceneGroupId);
        if (config != null) {
            binding.setConfig(new HashMap<>(config));
        }
        
        capabilityBindings.put(capId, binding);
        
        syncCoordinator.syncCapabilityBindingToSdk(binding);
        
        eventPublisher.publishCapabilityBound(sceneGroupId, binding);
        
        return binding;
    }
    
    @Override
    public void unbindCapability(String bindingId) {
        CapabilityBinding removed = capabilityBindings.remove(bindingId);
        
        if (removed != null) {
            syncCoordinator.syncCapabilityUnbindingToSdk(bindingId);
            eventPublisher.publishCapabilityUnbound(sceneGroupId, bindingId);
        }
    }
    
    @Override
    public List<CapabilityBinding> getCapabilityBindings() {
        return new ArrayList<>(capabilityBindings.values());
    }
    
    @Override
    public KnowledgeBinding bindKnowledgeBase(String kbId, String layer) {
        KnowledgeBinding binding = new KnowledgeBinding(kbId, sceneGroupId, layer);
        
        knowledgeBindings.put(kbId, binding);
        
        syncCoordinator.syncKnowledgeBindingToSdk(binding);
        
        eventPublisher.publishKnowledgeBaseBound(sceneGroupId, binding);
        
        return binding;
    }
    
    @Override
    public void unbindKnowledgeBase(String kbId) {
        KnowledgeBinding removed = knowledgeBindings.remove(kbId);
        
        if (removed != null) {
            syncCoordinator.syncKnowledgeUnbindingToSdk(kbId);
            eventPublisher.publishKnowledgeBaseUnbound(sceneGroupId, kbId);
        }
    }
    
    @Override
    public List<KnowledgeBinding> getKnowledgeBaseBindings() {
        return new ArrayList<>(knowledgeBindings.values());
    }
    
    private MemberRole mapToSdkRole(Participant.Role role) {
        switch (role) {
            case OWNER:
            case MANAGER:
                return MemberRole.PRIMARY;
            case COORDINATOR:
            case EMPLOYEE:
            case OBSERVER:
            case LLM_ASSISTANT:
            default:
                return MemberRole.BACKUP;
        }
    }
    
    @Override
    public AgentStatusInfo getAgentStatus(String agentId) {
        return syncCoordinator.getAgentStatusFromSdk(agentId);
    }
    
    @Override
    public List<AgentStatusInfo> getAllAgentStatuses() {
        return syncCoordinator.getAllAgentStatusesFromSdk();
    }
    
    @Override
    public AgentStatusInfo getPrimaryAgent() {
        return syncCoordinator.getPrimaryAgentFromSdk();
    }
    
    @Override
    public List<AgentStatusInfo> getBackupAgents() {
        return syncCoordinator.getBackupAgentsFromSdk();
    }
    
    @Override
    public CommunicationLinkInfo getCommunicationLinks() {
        return syncCoordinator.getCommunicationLinksFromSdk();
    }
    
    @Override
    public String getAgentEndpoint(String agentId) {
        return syncCoordinator.getAgentEndpointFromSdk(agentId);
    }
    
    @Override
    public HeartbeatInfo getHeartbeatInfo(String agentId) {
        return syncCoordinator.getHeartbeatInfoFromSdk(agentId);
    }
    
    @Override
    public FailoverStatusInfo getFailoverStatus() {
        return failoverStatus;
    }
    
    @Override
    public void triggerFailover(String failedAgentId) {
        syncCoordinator.triggerFailoverOnSdk(failedAgentId);
    }
    
    @Override
    public Map<String, Object> getSharedState() {
        return new HashMap<>(sharedState);
    }
    
    @Override
    public void updateSharedState(String key, Object value) {
        sharedState.put(key, value);
        syncCoordinator.syncSharedStateToSdk(sharedState);
    }
    
    @Override
    public void syncStateToSdk() {
        syncCoordinator.syncAllStateToSdk();
    }
    
    @Override
    public void syncStateFromSdk() {
        syncCoordinator.syncAllStateFromSdk();
    }
    
    @Override
    public Map<String, Object> getPersonalContext() {
        return new HashMap<>(personalContext);
    }
    
    @Override
    public void setPersonalContext(String key, Object value) {
        personalContext.put(key, value);
    }
    
    @Override
    public void removePersonalContext(String key) {
        personalContext.remove(key);
    }
    
    @Override
    public void syncToSceneGroup() {
        syncCoordinator.syncAllStateToSdk();
    }
    
    @Override
    public void syncFromSceneGroup() {
        syncCoordinator.syncAllStateFromSdk();
    }
    
    public void updateParticipantStatus(String agentId, String newStatus) {
        participants.stream()
            .filter(p -> agentId.equals(p.getUserId()))
            .findFirst()
            .ifPresent(p -> {
                if ("online".equals(newStatus)) {
                    p.activate();
                } else if ("offline".equals(newStatus)) {
                    p.suspend();
                }
            });
    }
    
    public void handleFailoverEvent(String failedAgentId, String newPrimaryId) {
        failoverStatus.setInProgress(false);
        failoverStatus.setFailedAgentId(failedAgentId);
        failoverStatus.setNewPrimaryId(newPrimaryId);
        failoverStatus.setPhase(FailoverStatusInfo.PHASE_COMPLETED);
        
        participants.stream()
            .filter(p -> failedAgentId.equals(p.getUserId()))
            .findFirst()
            .ifPresent(p -> p.setRole(Participant.Role.EMPLOYEE));
        
        participants.stream()
            .filter(p -> newPrimaryId.equals(p.getUserId()))
            .findFirst()
            .ifPresent(p -> p.setRole(Participant.Role.OWNER));
        
        eventPublisher.publishFailoverCompleted(sceneGroupId, failedAgentId, newPrimaryId);
    }
    
    public BidirectionalSyncCoordinator getSyncCoordinator() {
        return syncCoordinator;
    }
}
