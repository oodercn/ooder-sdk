package net.ooder.scene.llm.proxy.agent;

/**
 * Agent创建选项
 */
public class AgentCreationOptions {
    
    private long idleTimeout = 30 * 60 * 1000; // 默认30分钟
    private long dailyTokenLimit = 100000L; // 默认10万token/天
    private int maxConversations = 10; // 默认10个对话
    private boolean enableQuotaCheck = true;
    
    public static AgentCreationOptions defaults() {
        return new AgentCreationOptions();
    }
    
    public AgentCreationOptions idleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }
    
    public AgentCreationOptions dailyTokenLimit(long dailyTokenLimit) {
        this.dailyTokenLimit = dailyTokenLimit;
        return this;
    }
    
    public AgentCreationOptions maxConversations(int maxConversations) {
        this.maxConversations = maxConversations;
        return this;
    }
    
    public AgentCreationOptions enableQuotaCheck(boolean enableQuotaCheck) {
        this.enableQuotaCheck = enableQuotaCheck;
        return this;
    }
    
    public long getIdleTimeout() {
        return idleTimeout;
    }
    
    public long getDailyTokenLimit() {
        return dailyTokenLimit;
    }
    
    public int getMaxConversations() {
        return maxConversations;
    }
    
    public boolean isEnableQuotaCheck() {
        return enableQuotaCheck;
    }
}
