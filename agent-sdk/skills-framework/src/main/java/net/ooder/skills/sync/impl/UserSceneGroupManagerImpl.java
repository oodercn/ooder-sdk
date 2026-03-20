package net.ooder.skills.sync.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.ooder.skills.api.SceneGroupManager;
import net.ooder.skills.sync.Participant;
import net.ooder.skills.sync.SkillBinding;
import net.ooder.skills.sync.UserSceneGroup;
import net.ooder.skills.sync.UserSceneGroupManager;
import net.ooder.skills.sync.event.SyncEventPublisher;

public class UserSceneGroupManagerImpl implements UserSceneGroupManager {
    
    private final Map<String, UserSceneGroup> sceneGroups;
    private final List<UserSceneGroupListener> listeners;
    private final SceneGroupManager sdkSceneGroupManager;
    private final SyncEventPublisher eventPublisher;
    
    public UserSceneGroupManagerImpl(SceneGroupManager sdkSceneGroupManager) {
        this.sceneGroups = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
        this.sdkSceneGroupManager = sdkSceneGroupManager;
        this.eventPublisher = new SyncEventPublisher();
    }
    
    @Override
    public UserSceneGroup createSceneGroup(String sceneGroupId, String sceneId, String creatorId) {
        return createSceneGroup(sceneGroupId, sceneId, creatorId, new HashMap<>());
    }
    
    @Override
    public UserSceneGroup createSceneGroup(String sceneGroupId, String sceneId, String creatorId, Map<String, Object> config) {
        UserSceneGroupImpl group = new UserSceneGroupImpl(
            sceneGroupId, 
            sceneId, 
            sdkSceneGroupManager, 
            eventPublisher
        );
        
        if (creatorId != null) {
            group.addCollaborator(creatorId, Participant.Role.OWNER);
        }
        
        sceneGroups.put(sceneGroupId, group);
        
        notifyGroupCreated(group);
        
        return group;
    }
    
    @Override
    public UserSceneGroup getUserSceneGroup(String sceneGroupId) {
        return sceneGroups.get(sceneGroupId);
    }
    
    @Override
    public void removeSceneGroup(String sceneGroupId) {
        UserSceneGroup removed = sceneGroups.remove(sceneGroupId);
        
        if (removed != null) {
            notifyGroupRemoved(sceneGroupId);
        }
    }
    
    @Override
    public List<UserSceneGroup> getAllSceneGroups() {
        return new ArrayList<>(sceneGroups.values());
    }
    
    @Override
    public List<UserSceneGroup> getSceneGroupsByUser(String userId) {
        List<UserSceneGroup> result = new ArrayList<>();
        
        for (UserSceneGroup group : sceneGroups.values()) {
            for (Participant p : group.getCollaborators()) {
                if (userId.equals(p.getUserId())) {
                    result.add(group);
                    break;
                }
            }
        }
        
        return result;
    }
    
    @Override
    public boolean exists(String sceneGroupId) {
        return sceneGroups.containsKey(sceneGroupId);
    }
    
    @Override
    public void addGroupListener(UserSceneGroupListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void removeGroupListener(UserSceneGroupListener listener) {
        listeners.remove(listener);
    }
    
    public SyncEventPublisher getEventPublisher() {
        return eventPublisher;
    }
    
    private void notifyGroupCreated(UserSceneGroup group) {
        for (UserSceneGroupListener listener : listeners) {
            try {
                listener.onSceneGroupCreated(group);
            } catch (Exception e) {
                System.err.println("Error notifying listener on group created: " + e.getMessage());
            }
        }
    }
    
    private void notifyGroupRemoved(String sceneGroupId) {
        for (UserSceneGroupListener listener : listeners) {
            try {
                listener.onSceneGroupRemoved(sceneGroupId);
            } catch (Exception e) {
                System.err.println("Error notifying listener on group removed: " + e.getMessage());
            }
        }
    }
}
