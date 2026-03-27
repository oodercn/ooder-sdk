package net.ooder.scene.a2a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A2A 对话
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class A2AConversation {
    
    private String conversationId;
    private String sceneGroupId;
    private List<String> participantIds = new ArrayList<>();
    
    private ConversationStatus status;
    private List<A2AMessage> history = new ArrayList<>();
    
    private Map<String, Object> sharedContext = new HashMap<>();
    
    private long createdAt;
    private long lastActiveAt;
    
    public A2AConversation() {
        this.status = ConversationStatus.ACTIVE;
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = this.createdAt;
    }
    
    public A2AConversation(String conversationId) {
        this();
        this.conversationId = conversationId;
    }
    
    public A2AConversation(String conversationId, String sceneGroupId) {
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
    
    public void addParticipant(String agentId) {
        if (!participantIds.contains(agentId)) {
            participantIds.add(agentId);
        }
    }
    
    public void removeParticipant(String agentId) {
        participantIds.remove(agentId);
    }
    
    public ConversationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ConversationStatus status) {
        this.status = status;
    }
    
    public List<A2AMessage> getHistory() {
        return history;
    }
    
    public void setHistory(List<A2AMessage> history) {
        this.history = history;
    }
    
    public void addMessage(A2AMessage message) {
        history.add(message);
        touch();
    }
    
    public Map<String, Object> getSharedContext() {
        return sharedContext;
    }
    
    public void setSharedContext(Map<String, Object> sharedContext) {
        this.sharedContext = sharedContext;
    }
    
    public void updateSharedContext(String key, Object value) {
        sharedContext.put(key, value);
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
    
    public boolean isActive() {
        return status == ConversationStatus.ACTIVE;
    }
    
    public void pause() {
        this.status = ConversationStatus.PAUSED;
    }
    
    public void resume() {
        this.status = ConversationStatus.ACTIVE;
    }
    
    public void end() {
        this.status = ConversationStatus.ENDED;
    }
    
    public void touch() {
        this.lastActiveAt = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return "A2AConversation{" +
                "conversationId='" + conversationId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", participants=" + participantIds +
                ", status=" + status +
                ", messageCount=" + history.size() +
                '}';
    }
}
