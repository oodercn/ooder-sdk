package net.ooder.scene.skill.tool;

import java.util.Map;

/**
 * 工具执行上下文
 *
 * @author ooder
 * @since 2.3
 */
public class ToolExecutionContext {
    
    private String userId;
    private String kbId;
    private String sessionId;
    private String conversationId;
    private Map<String, Object> metadata;
    
    public ToolExecutionContext() {
    }
    
    public ToolExecutionContext(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getKbId() {
        return kbId;
    }
    
    public void setKbId(String kbId) {
        this.kbId = kbId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public static ToolExecutionContext of(String userId) {
        return new ToolExecutionContext(userId);
    }
    
    public static ToolExecutionContext of(String userId, String kbId) {
        ToolExecutionContext ctx = new ToolExecutionContext(userId);
        ctx.setKbId(kbId);
        return ctx;
    }
}
