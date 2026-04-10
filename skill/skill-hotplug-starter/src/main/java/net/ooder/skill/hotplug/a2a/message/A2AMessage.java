package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * A2A消息基类
 * 对应Ooder-A2A规范v1.0消息格式定义
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TaskSendMessage.class, name = "task_send"),
    @JsonSubTypes.Type(value = TaskGetMessage.class, name = "task_get"),
    @JsonSubTypes.Type(value = TaskCancelMessage.class, name = "task_cancel"),
    @JsonSubTypes.Type(value = StateChangeMessage.class, name = "state_change"),
    @JsonSubTypes.Type(value = SkillCardMessage.class, name = "skill_card"),
    @JsonSubTypes.Type(value = ConfigUpdateMessage.class, name = "config_update"),
    @JsonSubTypes.Type(value = ErrorMessage.class, name = "error")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class A2AMessage {

    /**
     * 消息类型
     */
    @JsonProperty("type")
    private MessageType type;

    /**
     * Skill唯一标识
     */
    @JsonProperty("skillId")
    private String skillId;

    /**
     * 会话ID
     */
    @JsonProperty("sessionId")
    private String sessionId;

    /**
     * 消息时间戳
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * 消息元数据
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    protected A2AMessage() {
        this.timestamp = Instant.now().toEpochMilli();
        this.metadata = new HashMap<>();
    }

    protected A2AMessage(MessageType type) {
        this();
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
}
