package net.ooder.sdk.api.agent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AgentMessage {

    private String messageId;
    private String fromAgentId;
    private String toAgentId;
    private MessageType type;
    private String subject;
    private Object payload;
    private Map<String, String> headers;
    private MessagePriority priority;
    private MessageState state;
    private Instant createdAt;
    private Instant deliveredAt;
    private Instant readAt;
    private Instant expiresAt;
    private String correlationId;
    private String replyTo;

    public enum MessageType {
        TASK_REQUEST,
        TASK_RESPONSE,
        NOTIFICATION,
        QUERY,
        QUERY_RESPONSE,
        COMMAND,
        COMMAND_RESPONSE,
        EVENT,
        HEARTBEAT,
        ACK,
        ERROR
    }

    public enum MessagePriority {
        LOW(1),
        NORMAL(5),
        HIGH(10),
        URGENT(20);

        private final int value;

        MessagePriority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum MessageState {
        CREATED,
        SENT,
        DELIVERED,
        READ,
        ACKNOWLEDGED,
        FAILED,
        EXPIRED
    }

    public AgentMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.headers = new HashMap<>();
        this.priority = MessagePriority.NORMAL;
        this.state = MessageState.CREATED;
        this.createdAt = Instant.now();
    }

    public AgentMessage(String fromAgentId, String toAgentId, MessageType type) {
        this();
        this.fromAgentId = fromAgentId;
        this.toAgentId = toAgentId;
        this.type = type;
    }

    public AgentMessage(String fromAgentId, String toAgentId, MessageType type, Object payload) {
        this(fromAgentId, toAgentId, type);
        this.payload = payload;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getFromAgentId() { return fromAgentId; }
    public void setFromAgentId(String fromAgentId) { this.fromAgentId = fromAgentId; }

    public String getToAgentId() { return toAgentId; }
    public void setToAgentId(String toAgentId) { this.toAgentId = toAgentId; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers != null ? headers : new HashMap<>(); }

    public MessagePriority getPriority() { return priority; }
    public void setPriority(MessagePriority priority) { this.priority = priority; }

    public MessageState getState() { return state; }
    public void setState(MessageState state) { this.state = state; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    public AgentMessage addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isBroadcast() {
        return toAgentId == null || toAgentId.isEmpty() || "*".equals(toAgentId);
    }

    public AgentMessage createReply(Object replyPayload) {
        AgentMessage reply = new AgentMessage(this.toAgentId, this.fromAgentId, MessageType.TASK_RESPONSE);
        reply.setCorrelationId(this.messageId);
        reply.setPayload(replyPayload);
        return reply;
    }

    public static AgentMessageBuilder builder() {
        return new AgentMessageBuilder();
    }

    public static class AgentMessageBuilder {
        private AgentMessage message = new AgentMessage();

        public AgentMessageBuilder from(String agentId) {
            message.setFromAgentId(agentId);
            return this;
        }

        public AgentMessageBuilder to(String agentId) {
            message.setToAgentId(agentId);
            return this;
        }

        public AgentMessageBuilder type(MessageType type) {
            message.setType(type);
            return this;
        }

        public AgentMessageBuilder subject(String subject) {
            message.setSubject(subject);
            return this;
        }

        public AgentMessageBuilder payload(Object payload) {
            message.setPayload(payload);
            return this;
        }

        public AgentMessageBuilder priority(MessagePriority priority) {
            message.setPriority(priority);
            return this;
        }

        public AgentMessageBuilder header(String key, String value) {
            message.addHeader(key, value);
            return this;
        }

        public AgentMessageBuilder correlationId(String correlationId) {
            message.setCorrelationId(correlationId);
            return this;
        }

        public AgentMessageBuilder expiresAt(Instant expiresAt) {
            message.setExpiresAt(expiresAt);
            return this;
        }

        public AgentMessage build() {
            return message;
        }
    }
}
