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
 * @version 2.3
 * @since 2.3
 */
/**
 * Ooder-A2A 消息基类（泛型版本）
 *
 * <p>根据Ooder-A2A规范v1.0定义的消息格式</p>
 *
 * @param <T> 消息数据类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class A2AMessage<T> {

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
     * 消息数据负载
     */
    private T data;

    /**
     * 消息元数据
     */
    private Map<String, String> metadata;

    /**
     * 扩展字段（保留向后兼容）
     */
    private Map<String, Object> extensions;

    public A2AMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
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
     * 创建任务发送消息（泛型版本）
     */
    public static <T> A2AMessage<T> taskSend(String skillId, T data) {
        A2AMessage<T> message = new A2AMessage<>(A2AMessageType.TASK_SEND, skillId);
        message.setData(data);
        return message;
    }

    /**
     * 创建任务发送消息（向后兼容）
     */
    @SuppressWarnings("unchecked")
    public static A2AMessage<Map<String, Object>> taskSend(String skillId, String input, Map<String, Object> parameters) {
        A2AMessage<Map<String, Object>> message = new A2AMessage<>(A2AMessageType.TASK_SEND, skillId);
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
    public static A2AMessage<Map<String, Object>> taskGet(String skillId, String taskId) {
        A2AMessage<Map<String, Object>> message = new A2AMessage<>(A2AMessageType.TASK_GET, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        message.setData(data);
        return message;
    }

    /**
     * 创建任务取消消息
     */
    public static A2AMessage<Map<String, Object>> taskCancel(String skillId, String taskId) {
        A2AMessage<Map<String, Object>> message = new A2AMessage<>(A2AMessageType.TASK_CANCEL, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        message.setData(data);
        return message;
    }

    /**
     * 创建状态变更消息
     */
    public static A2AMessage<Map<String, Object>> stateChange(String skillId, String fromState, String toState, String reason) {
        A2AMessage<Map<String, Object>> message = new A2AMessage<>(A2AMessageType.STATE_CHANGE, skillId);
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
    public static A2AMessage<Map<String, Object>> error(String skillId, int errorCode, String errorMessage) {
        A2AMessage<Map<String, Object>> message = new A2AMessage<>(A2AMessageType.ERROR, skillId);
        Map<String, Object> data = new HashMap<>();
        data.put("errorCode", errorCode);
        data.put("errorMessage", errorMessage);
        message.setData(data);
        return message;
    }

    /**
     * 创建心跳消息
     */
    public static A2AMessage<Void> heartbeat(String skillId) {
        return new A2AMessage<>(A2AMessageType.HEARTBEAT, skillId);
    }

    /**
     * 创建确认消息
     */
    public static A2AMessage<Map<String, Object>> ack(String skillId, String originalMessageId) {
        A2AMessage<Map<String, Object>> message = new A2AMessage<>(A2AMessageType.ACK, skillId);
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

    /**
     * 获取消息数据
     * @return 数据
     */
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取消息元数据
     * @return 元数据映射
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * 获取扩展字段
     * @return 扩展字段映射
     */
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    // ==================== 便捷方法 ====================

    /**
     * 添加数据项（仅当 data 为 Map 时可用）
     */
    @SuppressWarnings("unchecked")
    public void addData(String key, Object value) {
        if (this.data instanceof Map) {
            ((Map<String, Object>) this.data).put(key, value);
        } else {
            throw new UnsupportedOperationException("addData only supported when data is a Map");
        }
    }

    /**
     * 获取数据项（仅当 data 为 Map 时可用）
     */
    @SuppressWarnings("unchecked")
    public <V> V getData(String key) {
        if (this.data instanceof Map) {
            return (V) ((Map<String, Object>) this.data).get(key);
        }
        throw new UnsupportedOperationException("getData(String) only supported when data is a Map");
    }

    /**
     * 添加元数据
     */
    public A2AMessage<T> addMetadata(String key, String value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }

    /**
     * 获取元数据
     */
    public String getMetadata(String key) {
        if (this.metadata == null) {
            return null;
        }
        return this.metadata.get(key);
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
