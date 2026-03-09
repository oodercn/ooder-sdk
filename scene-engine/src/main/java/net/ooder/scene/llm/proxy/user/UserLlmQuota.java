package net.ooder.scene.llm.proxy.user;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户级配额管理
 */
public class UserLlmQuota {
    
    private final long dailyTokenLimit;
    private final AtomicLong dailyTokenUsed;
    private final int maxAgentsPerUser;
    private final AtomicInteger currentAgents;
    private final int maxConversationsPerUser;
    private final AtomicInteger currentConversations;
    
    public UserLlmQuota() {
        this(1000000L, 10, 50); // 默认100万token/天，10个Agent，50个对话
    }
    
    public UserLlmQuota(long dailyTokenLimit, int maxAgentsPerUser, int maxConversationsPerUser) {
        this.dailyTokenLimit = dailyTokenLimit;
        this.dailyTokenUsed = new AtomicLong(0);
        this.maxAgentsPerUser = maxAgentsPerUser;
        this.currentAgents = new AtomicInteger(0);
        this.maxConversationsPerUser = maxConversationsPerUser;
        this.currentConversations = new AtomicInteger(0);
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
     * 检查是否可以创建Agent
     */
    public boolean canCreateAgent() {
        return currentAgents.get() < maxAgentsPerUser;
    }
    
    /**
     * 增加Agent计数
     */
    public void incrementAgent() {
        currentAgents.incrementAndGet();
    }
    
    /**
     * 减少Agent计数
     */
    public void decrementAgent() {
        currentAgents.decrementAndGet();
    }
    
    /**
     * 检查是否可以创建对话
     */
    public boolean canCreateConversation() {
        return currentConversations.get() < maxConversationsPerUser;
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
    
    public int getMaxAgentsPerUser() {
        return maxAgentsPerUser;
    }
    
    public int getCurrentAgents() {
        return currentAgents.get();
    }
    
    public int getMaxConversationsPerUser() {
        return maxConversationsPerUser;
    }
    
    public int getCurrentConversations() {
        return currentConversations.get();
    }
}
