package net.ooder.scene.event.scene;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class SceneAgentEvent extends SceneEvent {
    
    private final String sceneId;
    private final String sceneName;
    private final String agentId;
    private final String userId;
    private final String groupId;
    private final String status;
    private final String reason;
    
    private SceneAgentEvent(Object source, SceneEventType eventType, String sceneId, String sceneName) {
        super(source, eventType);
        this.sceneId = sceneId;
        this.sceneName = sceneName;
        this.agentId = null;
        this.userId = null;
        this.groupId = null;
        this.status = null;
        this.reason = null;
    }
    
    private SceneAgentEvent(Object source, SceneEventType eventType, String sceneId, String sceneName,
                            String agentId, String userId, String groupId, String status, String reason) {
        super(source, eventType);
        this.sceneId = sceneId;
        this.sceneName = sceneName;
        this.agentId = agentId;
        this.userId = userId;
        this.groupId = groupId;
        this.status = status;
        this.reason = reason;
    }
    
    public static SceneAgentEvent created(Object source, String sceneId, String sceneName, String userId) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_AGENT_CREATED, sceneId, sceneName,
                null, userId, null, null, null);
    }
    
    public static SceneAgentEvent started(Object source, String sceneId, String sceneName, String agentId) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_AGENT_STARTED, sceneId, sceneName,
                agentId, null, null, null, null);
    }
    
    public static SceneAgentEvent paused(Object source, String sceneId, String agentId, String reason) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_AGENT_PAUSED, sceneId, null,
                agentId, null, null, null, reason);
    }
    
    public static SceneAgentEvent resumed(Object source, String sceneId, String agentId) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_AGENT_RESUMED, sceneId, null,
                agentId, null, null, null, null);
    }
    
    public static SceneAgentEvent stopped(Object source, String sceneId, String agentId, String reason) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_AGENT_STOPPED, sceneId, null,
                agentId, null, null, null, reason);
    }
    
    public static SceneAgentEvent stateSaved(Object source, String sceneId) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_STATE_SAVED, sceneId, null);
    }
    
    public static SceneAgentEvent stateLoaded(Object source, String sceneId) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_STATE_LOADED, sceneId, null);
    }
    
    public static SceneAgentEvent activated(Object source, String sceneId, String sceneName, String userId) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_ACTIVATED, sceneId, sceneName,
                null, userId, null, null, null);
    }
    
    public static SceneAgentEvent deactivated(Object source, String sceneId, String userId, String reason) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_DEACTIVATED, sceneId, null,
                null, userId, null, null, reason);
    }
    
    public static SceneAgentEvent groupJoined(Object source, String sceneId, String groupId, String userId) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_GROUP_JOINED, sceneId, null,
                null, userId, groupId, null, null);
    }
    
    public static SceneAgentEvent groupLeft(Object source, String sceneId, String groupId, String userId) {
        return new SceneAgentEvent(source, SceneEventType.SCENE_GROUP_LEFT, sceneId, null,
                null, userId, groupId, null, null);
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public String getSceneName() {
        return sceneName;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return "SceneAgentEvent{" +
                "eventType=" + getEventType() +
                ", sceneId='" + sceneId + '\'' +
                ", sceneName='" + sceneName + '\'' +
                ", agentId='" + agentId + '\'' +
                ", userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
