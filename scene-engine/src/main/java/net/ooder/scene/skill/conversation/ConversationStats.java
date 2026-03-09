package net.ooder.scene.skill.conversation;

/**
 * 对话统计
 *
 * @author ooder
 * @since 2.3
 */
public class ConversationStats {
    
    private String conversationId;
    private int totalMessages;
    private int userMessages;
    private int assistantMessages;
    private int toolCalls;
    private int totalTokens;
    private int promptTokens;
    private int completionTokens;
    private long totalDuration;
    private long createdAt;
    private long lastMessageAt;
    
    public ConversationStats() {
    }
    
    public ConversationStats(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public int getTotalMessages() {
        return totalMessages;
    }
    
    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }
    
    public int getUserMessages() {
        return userMessages;
    }
    
    public void setUserMessages(int userMessages) {
        this.userMessages = userMessages;
    }
    
    public int getAssistantMessages() {
        return assistantMessages;
    }
    
    public void setAssistantMessages(int assistantMessages) {
        this.assistantMessages = assistantMessages;
    }
    
    public int getToolCalls() {
        return toolCalls;
    }
    
    public void setToolCalls(int toolCalls) {
        this.toolCalls = toolCalls;
    }
    
    public int getTotalTokens() {
        return totalTokens;
    }
    
    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }
    
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
    
    public long getTotalDuration() {
        return totalDuration;
    }
    
    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getLastMessageAt() {
        return lastMessageAt;
    }
    
    public void setLastMessageAt(long lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
    
    public void incrementUserMessages() {
        this.userMessages++;
        this.totalMessages++;
    }
    
    public void incrementAssistantMessages() {
        this.assistantMessages++;
        this.totalMessages++;
    }
    
    public void incrementToolCalls() {
        this.toolCalls++;
    }
    
    public void addTokens(int prompt, int completion) {
        this.promptTokens += prompt;
        this.completionTokens += completion;
        this.totalTokens += prompt + completion;
    }
    
    public void addDuration(long duration) {
        this.totalDuration += duration;
    }
}
