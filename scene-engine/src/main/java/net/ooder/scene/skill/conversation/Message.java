package net.ooder.scene.skill.conversation;

import java.util.List;

/**
 * 消息
 *
 * @author ooder
 * @since 2.3
 */
public class Message {
    
    private String messageId;
    private String conversationId;
    private String role;
    private String content;
    private List<ToolCallInfo> toolCalls;
    private List<ToolResultInfo> toolResults;
    private long createdAt;
    private int tokenCount;
    
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    public static final String ROLE_TOOL = "tool";
    
    public Message() {
    }
    
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
        this.createdAt = System.currentTimeMillis();
    }
    
    public static Message user(String content) {
        return new Message(ROLE_USER, content);
    }
    
    public static Message assistant(String content) {
        return new Message(ROLE_ASSISTANT, content);
    }
    
    public static Message system(String content) {
        return new Message(ROLE_SYSTEM, content);
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public List<ToolCallInfo> getToolCalls() {
        return toolCalls;
    }
    
    public void setToolCalls(List<ToolCallInfo> toolCalls) {
        this.toolCalls = toolCalls;
    }
    
    public List<ToolResultInfo> getToolResults() {
        return toolResults;
    }
    
    public void setToolResults(List<ToolResultInfo> toolResults) {
        this.toolResults = toolResults;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getTokenCount() {
        return tokenCount;
    }
    
    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }
    
    public static class ToolCallInfo {
        private String id;
        private String name;
        private String arguments;
        
        public ToolCallInfo() {
        }
        
        public ToolCallInfo(String id, String name, String arguments) {
            this.id = id;
            this.name = name;
            this.arguments = arguments;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getArguments() {
            return arguments;
        }
        
        public void setArguments(String arguments) {
            this.arguments = arguments;
        }
    }
    
    public static class ToolResultInfo {
        private String toolCallId;
        private String toolName;
        private String content;
        
        public ToolResultInfo() {
        }
        
        public ToolResultInfo(String toolCallId, String toolName, String content) {
            this.toolCallId = toolCallId;
            this.toolName = toolName;
            this.content = content;
        }
        
        public String getToolCallId() {
            return toolCallId;
        }
        
        public void setToolCallId(String toolCallId) {
            this.toolCallId = toolCallId;
        }
        
        public String getToolName() {
            return toolName;
        }
        
        public void setToolName(String toolName) {
            this.toolName = toolName;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
}
