package net.ooder.scene.llm.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * LLM会话消息实体
 * 
 * @author ooder Team
 * @since 3.0.1
 */
public class LlmSessionMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    private String sessionId;
    private MessageRole role;
    private String content;
    private long timestamp;
    private Map<String, Object> metadata;
    
    public enum MessageRole {
        USER, ASSISTANT, SYSTEM
    }
    
    public LlmSessionMessage() {
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }
    
    public static LlmSessionMessage of(String sessionId, String role, String content) {
        LlmSessionMessage message = new LlmSessionMessage();
        message.setSessionId(sessionId);
        message.setRole(MessageRole.valueOf(role.toUpperCase()));
        message.setContent(content);
        return message;
    }
    
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public MessageRole getRole() { return role; }
    public void setRole(MessageRole role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new HashMap<>(); 
    }
}
