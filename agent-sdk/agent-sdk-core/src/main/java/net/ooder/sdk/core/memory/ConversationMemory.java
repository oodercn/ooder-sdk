package net.ooder.sdk.core.memory;

import java.util.List;
import java.util.Map;

public interface ConversationMemory {
    
    String addMessage(String conversationId, Message message);
    
    List<Message> getMessages(String conversationId);
    
    List<Message> getRecentMessages(String conversationId, int limit);
    
    void clearConversation(String conversationId);
    
    void setContext(String conversationId, String key, Object value);
    
    Object getContext(String conversationId, String key);
    
    Map<String, Object> getAllContext(String conversationId);
    
    String summarize(String conversationId);
    
    List<String> getActiveConversations();
    
    void setMaxMessages(String conversationId, int maxMessages);
    
    class Message {
        private String messageId;
        private String conversationId;
        private String role;
        private String content;
        private Map<String, Object> metadata;
        private long timestamp;
        
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public static Message user(String content) {
            Message msg = new Message();
            msg.setRole("user");
            msg.setContent(content);
            msg.setTimestamp(System.currentTimeMillis());
            return msg;
        }
        
        public static Message assistant(String content) {
            Message msg = new Message();
            msg.setRole("assistant");
            msg.setContent(content);
            msg.setTimestamp(System.currentTimeMillis());
            return msg;
        }
        
        public static Message system(String content) {
            Message msg = new Message();
            msg.setRole("system");
            msg.setContent(content);
            msg.setTimestamp(System.currentTimeMillis());
            return msg;
        }
    }
}
