package net.ooder.sdk.a2a.message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ooder-A2A 消息基类
 *
 * <p>根据Ooder-A2A规范v1.0定义的消息格式</p>
 *
 * <p>标准消息格式:</p>
 * <pre>
 * {
 *   "id": "uuid-string",
 *   "type": "task_send",
 *   "timestamp": 1700000000000,
 *   "skillId": "com.ooder.skills.example",
 *   "sessionId": "session-123",
 *   "data": { ... },
 *   "metadata": { ... }
 * }
 * </pre>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class A2AMessage {

    /**
     * 消息唯一标识
     */
    private String id;

    /**
     * 消息类型
     */
    private A2AMessageType type;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * Skill标识
     */
    private String skillId;

    /**
     * 会话标识
     */
    private String sessionId;

    /**
     * 消息数据
     */
    private Map<String, Object> data;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 扩展字段（保留向后兼容）
     */
    private Map<String, Object> extensions;

    public A2AMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.data = new HashMap<>();
        this.metadata = new HashMap<>();
        this.extensions = new HashMap<>();
    }

    public A2AMessage(A2AMessageType type) {
        this();
        this.type = type;
    }

    public A2AMessage(A2AMessageType type, String skillId) {
        this();
        this.type = type;
        this.skillId = skillId;
    }

    // ==================== 便捷构造方法 ====================

    /**
     * 创建任务发送消息
     */
    public static A2AMessage taskSend(String skillId, String input, Map<String, Object> parameters) {
        A2AMessage message = new A2AMessage(A2AMessageType.TASK_SEND, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("input", input);
        if (parameters != null) {
            data.put("parameters", parameters);
        }
        message.setData(data);
        return message;
    }

    /**
     * 创建任务获取消息
     */
    public static A2AMessage taskGet(String skillId, String taskId) {
        A2AMessage message = new A2AMessage(A2AMessageType.TASK_GET, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        message.setData(data);
        return message;
    }

    /**
     * 创建任务取消消息
     */
    public static A2AMessage taskCancel(String skillId, String taskId) {
        A2AMessage message = new A2AMessage(A2AMessageType.TASK_CANCEL, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        message.setData(data);
        return message;
    }

    /**
     * 创建状态变更消息
     */
    public static A2AMessage stateChange(String skillId, String fromState, String toState, String reason) {
        A2AMessage message = new A2AMessage(A2AMessageType.STATE_CHANGE, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("from", fromState);
        data.put("to", toState);
        if (reason != null) {
            data.put("reason", reason);
        }
        message.setData(data);
        return message;
    }

    /**
     * 创建错误消息
     */
    public static A2AMessage error(String skillId, int errorCode, String errorMessage) {
        A2AMessage message = new A2AMessage(A2AMessageType.ERROR, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("errorCode", errorCode);
        data.put("errorMessage", errorMessage);
        message.setData(data);
        return message;
    }

    /**
     * 创建心跳消息
     */
    public static A2AMessage heartbeat(String skillId) {
        return new A2AMessage(A2AMessageType.HEARTBEAT, skillId);
    }

    /**
     * 创建确认消息
     */
    public static A2AMessage ack(String skillId, String originalMessageId) {
        A2AMessage message = new A2AMessage(A2AMessageType.ACK, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("originalMessageId", originalMessageId);
        message.setData(data);
        return message;
    }

    // ==================== Getters and Setters ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public A2AMessageType getType() {
        return type;
    }

    public void setType(A2AMessageType type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data != null ? data : new HashMap<>();
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    // ==================== 便捷方法 ====================

    /**
     * 添加数据项
     */
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    /**
     * 获取数据项
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) this.data.get(key);
    }

    /**
     * 添加元数据
     */
    public A2AMessage addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }

    /**
     * 获取元数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key) {
        if (this.metadata == null) {
            return null;
        }
        return (T) this.metadata.get(key);
    }

    /**
     * 添加扩展字段（向后兼容）
     */
    public void addExtension(String key, Object value) {
        this.extensions.put(key, value);
    }

    /**
     * 获取扩展字段（向后兼容）
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtension(String key) {
        return (T) this.extensions.get(key);
    }

    @Override
    public String toString() {
        return "A2AMessage{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", skillId='" + skillId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", data=" + data +
                ", metadata=" + metadata +
                '}';
    }
}
