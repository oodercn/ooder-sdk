package net.ooder.skills.sync.event;

import net.ooder.skills.sync.CapabilityBinding;
import net.ooder.skills.sync.CollaborationSession;
import net.ooder.skills.sync.KnowledgeBinding;
import net.ooder.skills.sync.Participant;
import net.ooder.skills.sync.SkillBinding;

public class SyncEventPublisher {
    
    private final EventListenerRegistry listenerRegistry;
    
    public SyncEventPublisher() {
        this.listenerRegistry = new EventListenerRegistry();
    }
    
    public void addListener(SyncEventListener listener) {
        listenerRegistry.addListener(listener);
    }
    
    public void removeListener(SyncEventListener listener) {
        listenerRegistry.removeListener(listener);
    }
    
    public void publishCollaboratorAdded(String sceneGroupId, Participant participant) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.COLLABORATOR_ADDED, 
            participant
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishCollaboratorRemoved(String sceneGroupId, String userId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.COLLABORATOR_REMOVED, 
            userId
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishCollaboratorRoleChanged(String sceneGroupId, String userId, Participant.Role newRole) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.COLLABORATOR_ROLE_CHANGED,
            new CollaboratorRoleChange(userId, newRole)
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishSkillAdded(String sceneGroupId, SkillBinding binding) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.SKILL_ADDED, 
            binding
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishSkillRemoved(String sceneGroupId, String skillId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.SKILL_REMOVED, 
            skillId
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishCollaborationStarted(String sceneGroupId, CollaborationSession session) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.COLLABORATION_STARTED, 
            session
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishCollaborationEnded(String sceneGroupId, String sessionId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.COLLABORATION_ENDED, 
            sessionId
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishCapabilityBound(String sceneGroupId, CapabilityBinding binding) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.CAPABILITY_BOUND, 
            binding
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishCapabilityUnbound(String sceneGroupId, String bindingId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.CAPABILITY_UNBOUND, 
            bindingId
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishKnowledgeBaseBound(String sceneGroupId, KnowledgeBinding binding) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.KNOWLEDGE_BASE_BOUND, 
            binding
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishKnowledgeBaseUnbound(String sceneGroupId, String kbId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.KNOWLEDGE_BASE_UNBOUND, 
            kbId
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishSceneGroupActivated(String sceneGroupId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.SCENE_GROUP_ACTIVATED, 
            null
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishSceneGroupDeactivated(String sceneGroupId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.SCENE_GROUP_DEACTIVATED, 
            null
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishFailoverCompleted(String sceneGroupId, String failedAgentId, String newPrimaryId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.FAILOVER_COMPLETED,
            new FailoverData(failedAgentId, newPrimaryId)
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishSyncSuccess(String operation, String sceneGroupId) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.SYNC_SUCCESS, 
            operation
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishSyncFailure(String operation, String sceneGroupId, Throwable error) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.SYNC_FAILURE, 
            new SyncError(operation, error)
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public void publishMemberStatusChanged(String sceneGroupId, String agentId, String oldStatus, String newStatus) {
        SyncEvent event = new SyncEvent(
            sceneGroupId, 
            SyncEvent.Type.MEMBER_STATUS_CHANGED,
            new MemberStatusChange(agentId, oldStatus, newStatus)
        );
        listenerRegistry.notifyListeners(event);
    }
    
    public static class CollaboratorRoleChange {
        private final String userId;
        private final Participant.Role newRole;
        
        public CollaboratorRoleChange(String userId, Participant.Role newRole) {
            this.userId = userId;
            this.newRole = newRole;
        }
        
        public String getUserId() { return userId; }
        public Participant.Role getNewRole() { return newRole; }
    }
    
    public static class FailoverData {
        private final String failedAgentId;
        private final String newPrimaryId;
        
        public FailoverData(String failedAgentId, String newPrimaryId) {
            this.failedAgentId = failedAgentId;
            this.newPrimaryId = newPrimaryId;
        }
        
        public String getFailedAgentId() { return failedAgentId; }
        public String getNewPrimaryId() { return newPrimaryId; }
    }
    
    public static class SyncError {
        private final String operation;
        private final Throwable error;
        
        public SyncError(String operation, Throwable error) {
            this.operation = operation;
            this.error = error;
        }
        
        public String getOperation() { return operation; }
        public Throwable getError() { return error; }
    }
    
    public static class MemberStatusChange {
        private final String agentId;
        private final String oldStatus;
        private final String newStatus;
        
        public MemberStatusChange(String agentId, String oldStatus, String newStatus) {
            this.agentId = agentId;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
        }
        
        public String getAgentId() { return agentId; }
        public String getOldStatus() { return oldStatus; }
        public String getNewStatus() { return newStatus; }
    }
}
