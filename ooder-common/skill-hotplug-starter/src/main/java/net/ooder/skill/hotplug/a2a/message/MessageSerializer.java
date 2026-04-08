package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A2A消息序列化器
 */
public class MessageSerializer {

    private static final Logger logger = LoggerFactory.getLogger(MessageSerializer.class);

    private final ObjectMapper objectMapper;

    public MessageSerializer() {
        this.objectMapper = new ObjectMapper();
        // 配置序列化选项
        this.objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
    }

    public MessageSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 序列化消息为JSON字符串
     */
    public String serialize(A2AMessage message) throws MessageSerializationException {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize message: {}", message, e);
            throw new MessageSerializationException("Failed to serialize message", e);
        }
    }

    /**
     * 反序列化JSON字符串为消息对象
     */
    public A2AMessage deserialize(String json) throws MessageSerializationException {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode typeNode = rootNode.get("type");

            if (typeNode == null) {
                throw new MessageSerializationException("Message type is missing");
            }

            MessageType messageType = MessageType.fromCode(typeNode.asText());
            Class<? extends A2AMessage> messageClass = getMessageClass(messageType);

            return objectMapper.treeToValue(rootNode, messageClass);
        } catch (Exception e) {
            logger.error("Failed to deserialize message: {}", json, e);
            throw new MessageSerializationException("Failed to deserialize message", e);
        }
    }

    /**
     * 根据消息类型获取对应的类
     */
    private Class<? extends A2AMessage> getMessageClass(MessageType type) {
        switch (type) {
            case TASK_SEND:
                return TaskSendMessage.class;
            case TASK_GET:
                return TaskGetMessage.class;
            case TASK_CANCEL:
                return TaskCancelMessage.class;
            case STATE_CHANGE:
                return StateChangeMessage.class;
            case SKILL_CARD:
                return SkillCardMessage.class;
            case CONFIG_UPDATE:
                return ConfigUpdateMessage.class;
            case ERROR:
                return ErrorMessage.class;
            default:
                return GenericMessage.class;
        }
    }

    /**
     * 通用消息类（用于未知类型）
     */
    public static class GenericMessage extends A2AMessage {
        public GenericMessage() {
            super();
        }
    }
}
