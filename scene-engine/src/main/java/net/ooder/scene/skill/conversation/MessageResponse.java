package net.ooder.scene.skill.conversation;

import java.util.List;
import java.util.Map;

/**
 * 消息响应
 *
 * @author ooder
 * @since 2.3
 */
public class MessageResponse {
    
    private String messageId;
    private String conversationId;
    private String content;
    private List<SourceReference> sources;
    private List<ToolExecution> toolExecutions;
    private Map<String, Object> metadata;
    private Usage usage;
    
    public MessageResponse() {
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public List<SourceReference> getSources() {
        return sources;
    }
    
    public void setSources(List<SourceReference> sources) {
        this.sources = sources;
    }
    
    public List<ToolExecution> getToolExecutions() {
        return toolExecutions;
    }
    
    public void setToolExecutions(List<ToolExecution> toolExecutions) {
        this.toolExecutions = toolExecutions;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public Usage getUsage() {
        return usage;
    }
    
    public void setUsage(Usage usage) {
        this.usage = usage;
    }
    
    public static class SourceReference {
        private String docId;
        private String title;
        private String content;
        private float score;
        
        public String getDocId() {
            return docId;
        }
        
        public void setDocId(String docId) {
            this.docId = docId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public float getScore() {
            return score;
        }
        
        public void setScore(float score) {
            this.score = score;
        }
    }
    
    public static class ToolExecution {
        private String toolName;
        private Map<String, Object> arguments;
        private Object result;
        private boolean success;
        
        public String getToolName() {
            return toolName;
        }
        
        public void setToolName(String toolName) {
            this.toolName = toolName;
        }
        
        public Map<String, Object> getArguments() {
            return arguments;
        }
        
        public void setArguments(Map<String, Object> arguments) {
            this.arguments = arguments;
        }
        
        public Object getResult() {
            return result;
        }
        
        public void setResult(Object result) {
            this.result = result;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
    
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
        
        public int getPromptTokens() {
            return promptTokens;
        }
        
        public void setPromptTokens(int promptTokens) {
            this.promptTokens = promptTokens;
        }
        
        public int getCompletionTokens() {
            return completionTokens;
        }
        
        public void setCompletionTokens(int completionTokens) {
            this.completionTokens = completionTokens;
        }
        
        public int getTotalTokens() {
            return totalTokens;
        }
        
        public void setTotalTokens(int totalTokens) {
            this.totalTokens = totalTokens;
        }
    }
}
