package net.ooder.scene.message.queue;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息参与者
 *
 * <p>表示消息的发送方或接收方。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MessageParticipant {
    
    private String id;
    private String name;
    private ParticipantType type;
    private String sceneGroupId;
    private Map<String, Object> attributes = new HashMap<>();
    
    public MessageParticipant() {
    }
    
    public MessageParticipant(String id, ParticipantType type) {
        this.id = id;
        this.type = type;
    }
    
    public MessageParticipant(String id, String name, ParticipantType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ParticipantType getType() {
        return type;
    }
    
    public void setType(ParticipantType type) {
        this.type = type;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) this.attributes.get(key);
    }
    
    public boolean isUser() {
        return type == ParticipantType.USER;
    }
    
    public boolean isAgent() {
        return type != null && type.isAgent();
    }
    
    public boolean isVirtualAgent() {
        return type == ParticipantType.VIRTUAL_AGENT;
    }
    
    public boolean isPhysicalAgent() {
        return type == ParticipantType.PHYSICAL_AGENT;
    }
    
    public static MessageParticipant user(String userId) {
        return new MessageParticipant(userId, ParticipantType.USER);
    }
    
    public static MessageParticipant user(String userId, String name) {
        return new MessageParticipant(userId, name, ParticipantType.USER);
    }
    
    public static MessageParticipant virtualAgent(String agentId) {
        return new MessageParticipant(agentId, ParticipantType.VIRTUAL_AGENT);
    }
    
    public static MessageParticipant virtualAgent(String agentId, String name) {
        return new MessageParticipant(agentId, name, ParticipantType.VIRTUAL_AGENT);
    }
    
    public static MessageParticipant physicalAgent(String agentId) {
        return new MessageParticipant(agentId, ParticipantType.PHYSICAL_AGENT);
    }
    
    public static MessageParticipant physicalAgent(String agentId, String name) {
        return new MessageParticipant(agentId, name, ParticipantType.PHYSICAL_AGENT);
    }
    
    public static MessageParticipant system() {
        return new MessageParticipant("system", ParticipantType.SYSTEM);
    }
    
    @Override
    public String toString() {
        return "MessageParticipant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
