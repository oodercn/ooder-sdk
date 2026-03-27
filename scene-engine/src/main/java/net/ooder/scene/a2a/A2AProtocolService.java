package net.ooder.scene.a2a;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A2A 协议服务接口
 *
 * <p>提供 Agent 到 Agent 的通信能力。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>消息发送 - 支持 A2A 消息发送</li>
 *   <li>请求响应 - 支持请求-响应模式</li>
 *   <li>对话管理 - 支持多 Agent 对话</li>
 *   <li>消息路由 - 支持基于规则的消息路由</li>
 *   <li>协议转换 - 支持南向协议转换</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface A2AProtocolService {

    String sendMessage(A2AMessage message);

    CompletableFuture<A2AResponse> sendRequest(A2ARequest request);

    void registerHandler(String agentId, A2AMessageHandler handler);

    void unregisterHandler(String agentId);

    A2AConversation createConversation(String sceneGroupId, List<String> agentIds);

    A2AConversation getConversation(String conversationId);

    void addToConversation(String conversationId, String agentId);

    void removeFromConversation(String conversationId, String agentId);

    void sendToConversation(String conversationId, A2AMessage message);

    List<A2AMessage> getConversationHistory(String conversationId);

    void addRoutingRule(A2ARoutingRule rule);

    void removeRoutingRule(String ruleId);

    List<A2ARoutingRule> getRoutingRules();

    String route(A2AMessage message);

    void broadcast(String sceneGroupId, A2AMessage message);

    List<String> getActiveConversations(String sceneGroupId);

    void endConversation(String conversationId);

    A2AStats getStats();

    class A2AStats {
        private int totalMessages;
        private int activeConversations;
        private int registeredAgents;
        private int routingRules;
        
        public int getTotalMessages() { return totalMessages; }
        public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }
        
        public int getActiveConversations() { return activeConversations; }
        public void setActiveConversations(int activeConversations) { this.activeConversations = activeConversations; }
        
        public int getRegisteredAgents() { return registeredAgents; }
        public void setRegisteredAgents(int registeredAgents) { this.registeredAgents = registeredAgents; }
        
        public int getRoutingRules() { return routingRules; }
        public void setRoutingRules(int routingRules) { this.routingRules = routingRules; }
    }
}
