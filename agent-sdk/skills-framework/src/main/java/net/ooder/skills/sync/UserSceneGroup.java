package net.ooder.skills.sync;

import java.util.List;
import java.util.Map;

public interface UserSceneGroup {
    
    String getSceneGroupId();
    
    String getSceneId();
    
    default String getUserId() {
        throw new UnsupportedOperationException("getUserId() not implemented");
    }
    
    default Participant.Role getRole() {
        throw new UnsupportedOperationException("getRole() not implemented");
    }
    
    default void setRole(Participant.Role role) {
        throw new UnsupportedOperationException("setRole() not implemented");
    }
    
    Participant addCollaborator(String userId, Participant.Role role);
    
    void removeCollaborator(String userId);
    
    void changeCollaboratorRole(String userId, Participant.Role newRole);
    
    List<Participant> getCollaborators();
    
    SkillBinding addSkill(String skillId, Map<String, Object> config);
    
    void removeSkill(String skillId);
    
    void updateSkillConfig(String skillId, Map<String, Object> config);
    
    List<SkillBinding> getSkills();
    
    default SkillBinding getSkill(String skillId) {
        return getSkills().stream()
            .filter(b -> skillId.equals(b.getSkillId()))
            .findFirst()
            .orElse(null);
    }
    
    CollaborationSession startCollaboration(String collaborationType, List<String> participants);
    
    void endCollaboration(String sessionId);
    
    CollaborationSession getCollaborationSession(String sessionId);
    
    List<CollaborationSession> getActiveCollaborations();
    
    CapabilityBinding bindCapability(String capId, Map<String, Object> config);
    
    void unbindCapability(String bindingId);
    
    List<CapabilityBinding> getCapabilityBindings();
    
    KnowledgeBinding bindKnowledgeBase(String kbId, String layer);
    
    void unbindKnowledgeBase(String kbId);
    
    List<KnowledgeBinding> getKnowledgeBaseBindings();
    
    default Map<String, Object> getPersonalContext() {
        throw new UnsupportedOperationException("getPersonalContext() not implemented");
    }
    
    default void setPersonalContext(String key, Object value) {
        throw new UnsupportedOperationException("setPersonalContext() not implemented");
    }
    
    default void removePersonalContext(String key) {
        throw new UnsupportedOperationException("removePersonalContext() not implemented");
    }
    
    default void syncToSceneGroup() {
        throw new UnsupportedOperationException("syncToSceneGroup() not implemented");
    }
    
    default void syncFromSceneGroup() {
        throw new UnsupportedOperationException("syncFromSceneGroup() not implemented");
    }
    
    void activate();
    
    void deactivate();
    
    String getStatus();
}
