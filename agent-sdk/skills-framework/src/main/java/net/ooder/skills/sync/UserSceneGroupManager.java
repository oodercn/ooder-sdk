package net.ooder.skills.sync;

import java.util.List;
import java.util.Map;

public interface UserSceneGroupManager {
    
    UserSceneGroup createSceneGroup(String sceneGroupId, String sceneId, String creatorId);
    
    UserSceneGroup createSceneGroup(String sceneGroupId, String sceneId, String creatorId, Map<String, Object> config);
    
    UserSceneGroup getUserSceneGroup(String sceneGroupId);
    
    void removeSceneGroup(String sceneGroupId);
    
    List<UserSceneGroup> getAllSceneGroups();
    
    List<UserSceneGroup> getSceneGroupsByUser(String userId);
    
    boolean exists(String sceneGroupId);
    
    void addGroupListener(UserSceneGroupListener listener);
    
    void removeGroupListener(UserSceneGroupListener listener);
    
    interface UserSceneGroupListener {
        
        default void onSceneGroupCreated(UserSceneGroup group) {}
        
        default void onSceneGroupRemoved(String sceneGroupId) {}
        
        default void onSceneGroupActivated(String sceneGroupId) {}
        
        default void onSceneGroupDeactivated(String sceneGroupId) {}
        
        default void onParticipantAdded(String sceneGroupId, Participant participant) {}
        
        default void onParticipantRemoved(String sceneGroupId, String userId) {}
        
        default void onSkillAdded(String sceneGroupId, SkillBinding binding) {}
        
        default void onSkillRemoved(String sceneGroupId, String skillId) {}
    }
}
