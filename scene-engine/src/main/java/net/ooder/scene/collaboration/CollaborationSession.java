package net.ooder.scene.collaboration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 协作会话
 * 
 * <p>与 SDK CollaborationSession 保持一致</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class CollaborationSession {
    
    private String sessionId;
    private String sceneGroupId;
    private String collaborationType;
    private List<String> participantIds;
    private String status;
    private Map<String, Object> context;
    private long startTime;
    private long endTime;
    
    public CollaborationSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.participantIds = new ArrayList<>();
        this.context = new HashMap<>();
        this.status = "active";
        this.startTime = System.currentTimeMillis();
    }
    
    public CollaborationSession(String sceneGroupId, String collaborationType, List<String> participantIds) {
        this();
        this.sceneGroupId = sceneGroupId;
        this.collaborationType = collaborationType;
        this.participantIds = participantIds != null ? new ArrayList<>(participantIds) : new ArrayList<>();
    }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public String getCollaborationType() { return collaborationType; }
    public void setCollaborationType(String collaborationType) { this.collaborationType = collaborationType; }
    
    public List<String> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<String> participantIds) { this.participantIds = participantIds; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    
    public void addParticipant(String participantId) {
        if (!participantIds.contains(participantId)) {
            participantIds.add(participantId);
        }
    }
    
    public void removeParticipant(String participantId) {
        participantIds.remove(participantId);
    }
    
    public void end() {
        this.status = "ended";
        this.endTime = System.currentTimeMillis();
    }
    
    public boolean isActive() {
        return "active".equals(status);
    }
    
    public long getDuration() {
        if (endTime > 0) {
            return endTime - startTime;
        }
        return System.currentTimeMillis() - startTime;
    }
}
