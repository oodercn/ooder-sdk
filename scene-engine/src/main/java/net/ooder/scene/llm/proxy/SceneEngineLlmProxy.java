package net.ooder.scene.llm.proxy;

import net.ooder.sdk.service.llm.LlmConfig;
import net.ooder.scene.llm.proxy.agent.*;
import net.ooder.sdk.memory.ConversationMemory;
import net.ooder.scene.llm.proxy.common.LlmProxyException;
import net.ooder.sdk.drivers.llm.LlmDriver;
import net.ooder.scene.llm.proxy.connection.LlmConnection;
import net.ooder.scene.llm.proxy.connection.LlmConnectionManager;
import net.ooder.scene.llm.proxy.lifecycle.AgentLifecycleListener;
import net.ooder.scene.llm.proxy.user.UserLlmSessionContext;
import net.ooder.scene.llm.proxy.user.UserLlmSessionManager;
import net.ooder.scene.session.SessionInfo;
import net.ooder.scene.session.SessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SceneEngine LLM 代理层入口
 * 集成到 SceneEngine，提供用户-Agent-连接三层隔离的LLM服务
 */
public class SceneEngineLlmProxy {
    
    private static final Logger log = LoggerFactory.getLogger(SceneEngineLlmProxy.class);
    
    private SessionManager sessionManager;
    private UserLlmSessionManager userLlmSessionManager;
    private AgentSessionManager agentSessionManager;
    private LlmConnectionManager connectionManager;
    private LlmProxyMonitor monitor;
    
    public SceneEngineLlmProxy() {
        this.connectionManager = new LlmConnectionManager();
        this.agentSessionManager = new AgentSessionManager(connectionManager);
        this.userLlmSessionManager = new UserLlmSessionManager(agentSessionManager, connectionManager);
        this.monitor = new LlmProxyMonitor(userLlmSessionManager, agentSessionManager, connectionManager);
    }
    
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    
    /**
     * 启动代理层（包括监控）
     */
    public void start() {
        log.info("Starting SceneEngineLlmProxy");
        monitor.start();
    }
    
    /**
     * 停止代理层
     */
    public void stop() {
        log.info("Stopping SceneEngineLlmProxy");
        monitor.stop();
        userLlmSessionManager.shutdown();
    }
    
    // ==================== 用户会话管理 ====================
    
    /**
     * 用户登录时初始化LLM代理上下文
     */
    public void onUserLogin(String sessionId, String userId) {
        log.info("User login, initializing LLM proxy context: userId={}, sessionId={}", userId, sessionId);
        
        // 初始化用户LLM上下文
        userLlmSessionManager.initializeUserContext(userId, sessionId);
    }
    
    /**
     * 用户登出时清理
     */
    public void onUserLogout(String userId) {
        log.info("User logout, cleaning up LLM proxy context: userId={}", userId);
        
        // 清理用户所有Agent
        userLlmSessionManager.cleanupUserAgents(userId);
    }
    
    // ==================== Agent 管理 ====================
    
    /**
     * 为用户创建Agent
     */
    public AgentLlmSessionHandle createAgent(
            String userSessionId,
            String agentType,
            LlmConfig llmConfig) throws LlmProxyException {
        
        return createAgent(userSessionId, agentType, llmConfig, AgentCreationOptions.defaults());
    }
    
    /**
     * 为用户创建Agent（带选项）
     */
    public AgentLlmSessionHandle createAgent(
            String userSessionId,
            String agentType,
            LlmConfig llmConfig,
            AgentCreationOptions options) throws LlmProxyException {
        
        // 1. 验证用户会话
        String userId = validateAndGetUserId(userSessionId);
        
        // 2. 创建Agent会话
        return userLlmSessionManager.createAgentForUser(userId, agentType, llmConfig, options);
    }
    
    /**
     * 销毁Agent
     */
    public void destroyAgent(String userSessionId, String agentId) throws LlmProxyException {
        String userId = validateAndGetUserId(userSessionId);
        userLlmSessionManager.destroyUserAgent(userId, agentId);
    }
    
    /**
     * 获取用户的所有Agent
     */
    public List<AgentLlmSessionContext> getUserAgents(String userSessionId) throws LlmProxyException {
        String userId = validateAndGetUserId(userSessionId);
        return userLlmSessionManager.getUserAgents(userId);
    }
    
    // ==================== LLM 对话 ====================
    
    /**
     * 使用Agent进行同步对话
     */
    public String chatWithAgent(String agentId, String message) throws LlmProxyException {
        return chatWithAgent(agentId, message, null);
    }
    
    /**
     * 使用Agent进行同步对话（带系统提示）
     */
    public String chatWithAgent(String agentId, String message, String systemPrompt) throws LlmProxyException {
        // 1. 获取Agent上下文
        AgentLlmSessionContext agentContext = agentSessionManager.getAgentContext(agentId);
        if (agentContext == null) {
            throw new LlmProxyException("AGENT_NOT_FOUND", "Agent not found: " + agentId);
        }
        
        if (!agentContext.isActive()) {
            throw new LlmProxyException("AGENT_INACTIVE", "Agent is not active: " + agentId);
        }
        
        // 2. 检查配额
        if (!agentContext.getQuota().canCreateConversation()) {
            throw new LlmProxyException("AGENT_CONVERSATION_LIMIT", "Agent conversation limit exceeded");
        }
        
        // 3. 获取连接
        LlmConnection connection;
        try {
            connection = agentContext.getConnectionPool().acquireConnection();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmProxyException("CONNECTION_ACQUIRE_FAILED", "Failed to acquire LLM connection", e);
        }
        
        try {
            // 4. 构建请求
            LlmDriver.ChatRequest request = buildChatRequest(agentContext, message, systemPrompt);
            
            // 5. 调用LLM
            LlmDriver.ChatResponse response = connection.chat(request);
            
            // 6. 获取内容
            String content = "";
            if (response.getMessage() != null) {
                content = response.getMessage().getContent();
            }
            
            // 7. 更新对话内存
            updateConversationMemory(agentContext, message, content);
            
            // 8. 更新配额
            if (response.getUsage() != null) {
                long tokens = response.getUsage().getTotalTokens();
                agentContext.getQuota().consumeTokens(tokens);
                
                // 同时更新用户级配额
                UserLlmSessionContext userContext = userLlmSessionManager.getUserContext(agentContext.getUserId());
                if (userContext != null) {
                    userContext.consumeTokens(tokens);
                }
            }
            
            return content;
            
        } finally {
            connection.release();
        }
    }
    
    /**
     * 使用Agent进行异步对话
     */
    public CompletableFuture<String> chatWithAgentAsync(String agentId, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return chatWithAgent(agentId, message);
            } catch (LlmProxyException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 流式对话
     */
    public void chatWithAgentStream(String agentId, String message, ChatStreamHandler handler) throws LlmProxyException {
        AgentLlmSessionContext agentContext = agentSessionManager.getAgentContext(agentId);
        if (agentContext == null) {
            throw new LlmProxyException("AGENT_NOT_FOUND", "Agent not found: " + agentId);
        }
        
        LlmConnection connection;
        try {
            connection = agentContext.getConnectionPool().acquireConnection();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmProxyException("CONNECTION_ACQUIRE_FAILED", "Failed to acquire LLM connection", e);
        }
        
        try {
            LlmDriver.ChatRequest request = buildChatRequest(agentContext, message, null);
            
            connection.chatStream(request, new net.ooder.sdk.drivers.llm.LlmDriver.ChatStreamHandler() {
                @Override
                public void onToken(String token) {
                    handler.onMessage(token);
                }
                
                @Override
                public void onMessage(LlmDriver.ChatMessage message) {
                    if (message != null && message.getContent() != null) {
                        handler.onMessage(message.getContent());
                    }
                }
                
                @Override
                public void onComplete(LlmDriver.ChatResponse response) {
                    handler.onComplete();
                }
                
                @Override
                public void onError(Throwable error) {
                    handler.onError(error);
                }
            });
            
        } finally {
            connection.release();
        }
    }
    
    // ==================== 对话历史管理 ====================
    
    /**
     * 获取Agent的对话历史
     */
    public List<ConversationMemory.Message> getAgentConversationHistory(String agentId) {
        AgentLlmSessionContext agentContext = agentSessionManager.getAgentContext(agentId);
        if (agentContext == null || agentContext.getConversationMemory() == null) {
            return null;
        }
        return agentContext.getConversationMemory().getMessages(agentContext.getConversationMemoryId());
    }
    
    /**
     * 清空Agent的对话历史
     */
    public void clearAgentConversation(String agentId) {
        AgentLlmSessionContext agentContext = agentSessionManager.getAgentContext(agentId);
        if (agentContext != null && agentContext.getConversationMemory() != null) {
            agentContext.getConversationMemory().clearConversation(agentContext.getConversationMemoryId());
        }
    }
    
    // ==================== 生命周期监听 ====================
    
    public void registerAgentLifecycleListener(AgentLifecycleListener listener) {
        agentSessionManager.registerLifecycleListener(listener);
    }
    
    public void unregisterAgentLifecycleListener(AgentLifecycleListener listener) {
        agentSessionManager.unregisterLifecycleListener(listener);
    }
    
    // ==================== 统计信息 ====================
    
    /**
     * 获取统计信息
     */
    public ProxyStats getStats() {
        ProxyStats stats = new ProxyStats();
        stats.setUserStats(userLlmSessionManager.getStats());
        stats.setConnectionStats(connectionManager.getAllPoolStats());
        return stats;
    }
    
    // ==================== 私有方法 ====================
    
    /**
     * 验证会话并获取用户ID
     */
    private String validateAndGetUserId(String userSessionId) throws LlmProxyException {
        if (sessionManager == null) {
            // 如果没有配置SessionManager，直接返回sessionId作为userId
            return userSessionId;
        }
        
        // 先验证会话是否有效
        if (!sessionManager.validateSession(userSessionId)) {
            throw new LlmProxyException("INVALID_SESSION", "Invalid user session: " + userSessionId);
        }
        
        // 获取会话信息
        SessionInfo session = sessionManager.getSession(userSessionId);
        if (session == null) {
            throw new LlmProxyException("INVALID_SESSION", "Session not found: " + userSessionId);
        }
        return session.getUserId();
    }
    
    /**
     * 构建对话请求
     */
    private LlmDriver.ChatRequest buildChatRequest(AgentLlmSessionContext agentContext, String message, String systemPrompt) {
        LlmDriver.ChatRequest request = new LlmDriver.ChatRequest();
        request.setModel(agentContext.getLlmConfig().getDefaultModel());
        
        // 构建消息列表
        java.util.List<LlmDriver.ChatMessage> messages = new java.util.ArrayList<>();
        
        // 添加系统提示
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(LlmDriver.ChatMessage.system(systemPrompt));
        }
        
        // 添加历史消息
        java.util.List<ConversationMemory.Message> history = agentContext.getConversationMemory()
                .getMessages(agentContext.getConversationMemoryId());
        if (history != null) {
            for (ConversationMemory.Message msg : history) {
                if ("user".equals(msg.getRole())) {
                    messages.add(LlmDriver.ChatMessage.user(msg.getContent()));
                } else if ("assistant".equals(msg.getRole())) {
                    messages.add(LlmDriver.ChatMessage.assistant(msg.getContent()));
                } else if ("system".equals(msg.getRole())) {
                    messages.add(LlmDriver.ChatMessage.system(msg.getContent()));
                }
            }
        }
        
        // 添加用户消息
        messages.add(LlmDriver.ChatMessage.user(message));
        
        request.setMessages(messages);
        
        return request;
    }
    
    /**
     * 更新对话内存
     */
    private void updateConversationMemory(AgentLlmSessionContext agentContext, String userMessage, String assistantMessage) {
        ConversationMemory memory = agentContext.getConversationMemory();
        String memoryId = agentContext.getConversationMemoryId();
        
        // 添加用户消息
        memory.addMessage(memoryId, ConversationMemory.Message.user(userMessage));
        
        // 添加助手回复
        memory.addMessage(memoryId, ConversationMemory.Message.assistant(assistantMessage));
    }
    
    // ==================== Getter ====================
    
    public UserLlmSessionManager getUserLlmSessionManager() {
        return userLlmSessionManager;
    }
    
    public AgentSessionManager getAgentSessionManager() {
        return agentSessionManager;
    }
    
    public LlmConnectionManager getConnectionManager() {
        return connectionManager;
    }
    
    public LlmProxyMonitor getMonitor() {
        return monitor;
    }
    
    // ==================== 流式处理接口 ====================
    
    public interface ChatStreamHandler {
        void onMessage(String content);
        void onComplete();
        void onError(Throwable error);
    }
    
    // ==================== 统计类 ====================
    
    public static class ProxyStats {
        private UserLlmSessionManager.UserSessionStats userStats;
        private java.util.Map<String, net.ooder.scene.llm.proxy.connection.LlmConnectionPool.PoolStats> connectionStats;
        
        public UserLlmSessionManager.UserSessionStats getUserStats() { return userStats; }
        public void setUserStats(UserLlmSessionManager.UserSessionStats userStats) { this.userStats = userStats; }
        
        public java.util.Map<String, net.ooder.scene.llm.proxy.connection.LlmConnectionPool.PoolStats> getConnectionStats() { return connectionStats; }
        public void setConnectionStats(java.util.Map<String, net.ooder.scene.llm.proxy.connection.LlmConnectionPool.PoolStats> connectionStats) { 
            this.connectionStats = connectionStats; 
        }
    }
}
