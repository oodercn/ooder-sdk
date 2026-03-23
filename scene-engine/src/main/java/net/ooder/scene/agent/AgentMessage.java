package net.ooder.scene.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AgentMessage {

    private String messageId;
    private String fromAgent;
    private String toAgent;
    private String sceneGroupId;
    private MessageType type;
    private Map<String, Object> payload;
    private int priority;
    private long createTime;
    private long expireTime;
    private boolean acknowledged;

    public AgentMessage() {
        this.messageId = UUID.randomUUID().toString().replace("-", "");
        this.createTime = System.currentTimeMillis();
        this.payload = new HashMap<>();
        this.priority = 0;
        this.acknowledged = false;
    }

    public AgentMessage(String fromAgent, String toAgent, MessageType type) {
        this();
        this.fromAgent = fromAgent;
        this.toAgent = toAgent;
        this.type = type;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFromAgent() {
        return fromAgent;
    }

    public void setFromAgent(String fromAgent) {
        this.fromAgent = fromAgent;
    }

    public String getToAgent() {
        return toAgent;
    }

    public void setToAgent(String toAgent) {
        this.toAgent = toAgent;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public void setPayloadItem(String key, Object value) {
        this.payload.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayloadItem(String key) {
        return (T) this.payload.get(key);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public boolean isExpired() {
        return expireTime > 0 && System.currentTimeMillis() > expireTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AgentMessage message = new AgentMessage();

        public Builder from(String fromAgent) {
            message.setFromAgent(fromAgent);
            return this;
        }

        public Builder to(String toAgent) {
            message.setToAgent(toAgent);
            return this;
        }

        public Builder sceneGroup(String sceneGroupId) {
            message.setSceneGroupId(sceneGroupId);
            return this;
        }

        public Builder type(MessageType type) {
            message.setType(type);
            return this;
        }

        public Builder payload(Map<String, Object> payload) {
            message.setPayload(payload);
            return this;
        }

        public Builder payloadItem(String key, Object value) {
            message.setPayloadItem(key, value);
            return this;
        }

        public Builder priority(int priority) {
            message.setPriority(priority);
            return this;
        }

        public Builder expireIn(long millis) {
            message.setExpireTime(System.currentTimeMillis() + millis);
            return this;
        }

        public AgentMessage build() {
            return message;
        }
    }

    @Override
    public String toString() {
        return "AgentMessage{" +
                "messageId='" + messageId + '\'' +
                ", fromAgent='" + fromAgent + '\'' +
                ", toAgent='" + toAgent + '\'' +
                ", type=" + type +
                ", priority=" + priority +
                ", acknowledged=" + acknowledged +
                '}';
    }
}
