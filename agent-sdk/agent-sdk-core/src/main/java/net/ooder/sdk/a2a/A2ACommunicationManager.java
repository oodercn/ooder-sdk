package net.ooder.sdk.a2a;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A2A (Agent-to-Agent) 通信管理器
 *
 * <p>管理 Agent 之间的直接通信</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public interface A2ACommunicationManager {

    /**
     * 发送消息到目标 Agent
     *
     * @param targetAgentId 目标 Agent ID
     * @param message 消息内容
     * @param context A2A 上下文
     * @return CompletableFuture<响应>
     */
    CompletableFuture<A2AMessage> sendMessage(String targetAgentId, A2AMessage message, A2AContext context);

    /**
     * 广播消息到场景中的所有 Agent
     *
     * @param sceneId 场景ID
     * @param message 消息内容
     * @param context A2A 上下文
     * @return CompletableFuture<响应列表>
     */
    CompletableFuture<Map<String, A2AMessage>> broadcast(String sceneId, A2AMessage message, A2AContext context);

    /**
     * 调用目标 Agent 的能力
     *
     * @param targetAgentId 目标 Agent ID
     * @param capId 能力ID
     * @param params 参数
     * @param context A2A 上下文
     * @return CompletableFuture<调用结果>
     */
    CompletableFuture<Object> invokeCapability(String targetAgentId, String capId, Map<String, Object> params, A2AContext context);

    /**
     * 注册消息处理器
     *
     * @param handler 消息处理器
     */
    void registerHandler(A2AMessageHandler handler);

    /**
     * 注销消息处理器
     *
     * @param handler 消息处理器
     */
    void unregisterHandler(A2AMessageHandler handler);

    /**
     * 启动通信管理器
     */
    void start();

    /**
     * 停止通信管理器
     */
    void stop();

    /**
     * A2A 消息
     */
    class A2AMessage {
        private String messageId;
        private String messageType;
        private String sourceAgentId;
        private String targetAgentId;
        private Object payload;
        private long timestamp;
        private Map<String, Object> headers;

        public A2AMessage() {
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }

        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }

        public String getSourceAgentId() { return sourceAgentId; }
        public void setSourceAgentId(String sourceAgentId) { this.sourceAgentId = sourceAgentId; }

        public String getTargetAgentId() { return targetAgentId; }
        public void setTargetAgentId(String targetAgentId) { this.targetAgentId = targetAgentId; }

        public Object getPayload() { return payload; }
        public void setPayload(Object payload) { this.payload = payload; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public Map<String, Object> getHeaders() { return headers; }
        public void setHeaders(Map<String, Object> headers) { this.headers = headers; }

        /**
         * 创建请求消息
         */
        public static A2AMessage request(String messageType, Object payload) {
            A2AMessage msg = new A2AMessage();
            msg.setMessageType(messageType);
            msg.setPayload(payload);
            return msg;
        }

        /**
         * 创建响应消息
         */
        public static A2AMessage response(String requestId, Object payload) {
            A2AMessage msg = new A2AMessage();
            msg.setMessageType("RESPONSE");
            msg.setPayload(payload);
            java.util.HashMap<String, Object> headers = new java.util.HashMap<>();
            headers.put("requestId", requestId);
            msg.setHeaders(headers);
            return msg;
        }
    }

    /**
     * A2A 消息处理器
     */
    interface A2AMessageHandler {
        /**
         * 处理消息
         *
         * @param message 消息
         * @param context 上下文
         * @return 响应消息
         */
        A2AMessage handle(A2AMessage message, A2AContext context);

        /**
         * 获取支持的消息类型
         *
         * @return 消息类型列表
         */
        String[] getSupportedMessageTypes();
    }
}
