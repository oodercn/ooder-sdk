package net.ooder.sdk.a2a.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A2A消息序列化器
 *
 * <p>负责A2A消息的序列化和反序列化</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class MessageSerializer {

    private static final Logger log = LoggerFactory.getLogger(MessageSerializer.class);

    /**
     * 序列化消息为JSON字符串
     *
     * @param message 消息对象
     * @return JSON字符串
     * @throws MessageSerializationException 序列化失败时抛出
     */
    public String serialize(A2AMessage message) throws MessageSerializationException {
        if (message == null) {
            throw new MessageSerializationException("Message cannot be null");
        }

        try {
            String json = JSON.toJSONString(message, SerializerFeature.WriteMapNullValue);
            log.debug("Serialized message: {}", json);
            return json;
        } catch (Exception e) {
            log.error("Failed to serialize message: {}", message, e);
            throw new MessageSerializationException("Failed to serialize message: " + e.getMessage(), e);
        }
    }

    /**
     * 反序列化JSON字符串为消息对象
     *
     * @param json JSON字符串
     * @return 消息对象
     * @throws MessageSerializationException 反序列化失败时抛出
     */
    public A2AMessage deserialize(String json) throws MessageSerializationException {
        if (json == null || json.trim().isEmpty()) {
            throw new MessageSerializationException("JSON string cannot be null or empty");
        }

        try {
            // 先解析为JSONObject获取type字段
            JSONObject jsonObject = JSON.parseObject(json);
            String typeCode = jsonObject.getString("type");

            if (typeCode == null) {
                throw new MessageSerializationException("Message type is missing");
            }

            // 根据类型获取对应的类
            A2AMessageType messageType = A2AMessageType.fromCode(typeCode);
            if (messageType == null) {
                throw new MessageSerializationException("Unknown message type: " + typeCode);
            }

            Class<? extends A2AMessage> messageClass = getMessageClass(messageType);

            // 反序列化为具体类型
            A2AMessage message = JSON.parseObject(json, messageClass);
            log.debug("Deserialized message: type={}, class={}", typeCode, messageClass.getSimpleName());
            return message;

        } catch (MessageSerializationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to deserialize message: {}", json, e);
            throw new MessageSerializationException("Failed to deserialize message: " + e.getMessage(), e);
        }
    }

    /**
     * 根据消息类型获取对应的类
     *
     * @param type 消息类型
     * @return 消息类
     */
    private Class<? extends A2AMessage> getMessageClass(A2AMessageType type) {
        switch (type) {
            case TASK_SEND:
                return TaskSendMessage.class;
            case TASK_GET:
                return TaskGetMessage.class;
            case TASK_CANCEL:
                return TaskCancelMessage.class;
            case TASK_RESUBSCRIBE:
                return TaskResubscribeMessage.class;
            case STATE_CHANGE:
                return StateChangeMessage.class;
            case SKILL_CARD:
                return SkillCardMessage.class;
            case CONFIG_UPDATE:
                return ConfigUpdateMessage.class;
            case ERROR:
                return ErrorMessage.class;
            case HEARTBEAT:
                return HeartbeatMessage.class;
            case ACK:
                return AckMessage.class;
            default:
                return A2AMessage.class;
        }
    }

    /**
     * 序列化为格式化的JSON字符串（便于阅读）
     *
     * @param message 消息对象
     * @return 格式化的JSON字符串
     */
    public String serializePretty(A2AMessage message) {
        if (message == null) {
            return "{}";
        }
        return JSON.toJSONString(message, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 验证消息格式
     *
     * @param json JSON字符串
     * @return true如果格式有效
     */
    public boolean validate(String json) {
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            return jsonObject.containsKey("type") && jsonObject.containsKey("timestamp");
        } catch (Exception e) {
            return false;
        }
    }
}
