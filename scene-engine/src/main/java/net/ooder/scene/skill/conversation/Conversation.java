package net.ooder.scene.skill.conversation;

import java.util.List;
import java.util.Map;

/**
 * 对话
 *
 * @author ooder
 * @since 2.3
 */
public class Conversation {
    
    private String conversationId;
    private String userId;
    private String title;
    private String kbId;
    private List<String> enabledTools;
    private Map<String, Object> settings;
    private long createdAt;
    private long updatedAt;
    private int messageCount;
    private String status;
    
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_ARCHIVED = "archived";
    
    public Conversation() {
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getKbId() {
        return kbId;
    }
    
    public void setKbId(String kbId) {
        this.kbId = kbId;
    }
    
    public List<String> getEnabledTools() {
        return enabledTools;
    }
    
    public void setEnabledTools(List<String> enabledTools) {
        this.enabledTools = enabledTools;
    }
    
    public Map<String, Object> getSettings() {
        return settings;
    }
    
    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getMessageCount() {
        return messageCount;
    }
    
    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void incrementMessageCount() {
        this.messageCount++;
        this.updatedAt = System.currentTimeMillis();
    }
}
