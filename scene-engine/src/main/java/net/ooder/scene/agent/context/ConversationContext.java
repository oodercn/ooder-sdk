package net.ooder.scene.agent.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对话上下文
 *
 * <p>管理多参与者对话的上下文信息。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ConversationContext {
    
    private String conversationId;
    private String sceneGroupId;
    
    private List<String> participantIds = new ArrayList<>();
    private List<Map<String, Object>> history = new ArrayList<>();
    
    private Map<String, Object> sharedState = new HashMap<>();
    private Map<String, Map<String, Object>> privateStates = new HashMap<>();
    
    private long createdAt;
    private long lastActiveAt;
    private String status;
    
    public ConversationContext() {
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = this.createdAt;
        this.status = "ACTIVE";
    }
    
    public ConversationContext(String conversationId) {
        this();
        this.conversationId = conversationId;
    }
    
    public ConversationContext(String conversationId, String sceneGroupId) {
        this(conversationId);
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public List<String> getParticipantIds() {
        return participantIds;
    }
    
    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }
    
    public void addParticipant(String participantId) {
        if (!participantIds.contains(participantId)) {
            participantIds.add(participantId);
            privateStates.put(participantId, new HashMap<>());
        }
    }
    
    public void removeParticipant(String participantId) {
        participantIds.remove(participantId);
        privateStates.remove(participantId);
    }
    
    public List<Map<String, Object>> getHistory() {
        return history;
    }
    
    public void setHistory(List<Map<String, Object>> history) {
        this.history = history;
    }
    
    public void addMessage(Map<String, Object> message) {
        history.add(message);
        touch();
    }
    
    public Map<String, Object> getSharedState() {
        return sharedState;
    }
    
    public void setSharedState(Map<String, Object> sharedState) {
        this.sharedState = sharedState;
        touch();
    }
    
    public void updateSharedState(String key, Object value) {
        sharedState.put(key, value);
        touch();
    }
    
    public Map<String, Map<String, Object>> getPrivateStates() {
        return privateStates;
    }
    
    public void setPrivateStates(Map<String, Map<String, Object>> privateStates) {
        this.privateStates = privateStates;
    }
    
    public Map<String, Object> getPrivateState(String participantId) {
        return privateStates.computeIfAbsent(participantId, k -> new HashMap<>());
    }
    
    public void updatePrivateState(String participantId, String key, Object value) {
        getPrivateState(participantId).put(key, value);
        touch();
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getLastActiveAt() {
        return lastActiveAt;
    }
    
    public void setLastActiveAt(long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public void pause() {
        this.status = "PAUSED";
    }
    
    public void resume() {
        this.status = "ACTIVE";
    }
    
    public void end() {
        this.status = "ENDED";
    }
    
    public void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return "ConversationContext{" +
                "conversationId='" + conversationId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", participants=" + participantIds.size() +
                ", historySize=" + history.size() +
                ", status='" + status + '\'' +
                '}';
    }
}
