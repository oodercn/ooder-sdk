package net.ooder.scene.llm.proxy.agent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Agent级配额管理
 */
public class AgentLlmQuota {
    
    private final long dailyTokenLimit;
    private final AtomicLong dailyTokenUsed;
    private final int maxConversations;
    private final AtomicLong currentConversations;
    
    public AgentLlmQuota() {
        this(100000L, 10); // 默认10万token/天，10个对话
    }
    
    public AgentLlmQuota(long dailyTokenLimit, int maxConversations) {
        this.dailyTokenLimit = dailyTokenLimit;
        this.dailyTokenUsed = new AtomicLong(0);
        this.maxConversations = maxConversations;
        this.currentConversations = new AtomicLong(0);
    }
    
    /**
     * 检查并消耗Token配额
     */
    public synchronized boolean consumeTokens(long tokens) {
        long used = dailyTokenUsed.get();
        if (used + tokens > dailyTokenLimit) {
            return false;
        }
        dailyTokenUsed.addAndGet(tokens);
        return true;
    }
    
    /**
     * 检查是否可以创建新对话
     */
    public boolean canCreateConversation() {
        return currentConversations.get() < maxConversations;
    }
    
    /**
     * 增加对话计数
     */
    public void incrementConversation() {
        currentConversations.incrementAndGet();
    }
    
    /**
     * 减少对话计数
     */
    public void decrementConversation() {
        currentConversations.decrementAndGet();
    }
    
    /**
     * 获取剩余Token配额
     */
    public long getRemainingTokens() {
        return dailyTokenLimit - dailyTokenUsed.get();
    }
    
    /**
     * 重置每日Token使用量
     */
    public void resetDailyUsage() {
        dailyTokenUsed.set(0);
    }
    
    public long getDailyTokenLimit() {
        return dailyTokenLimit;
    }
    
    public long getDailyTokenUsed() {
        return dailyTokenUsed.get();
    }
    
    public int getMaxConversations() {
        return maxConversations;
    }
    
    public long getCurrentConversations() {
        return currentConversations.get();
    }
}
