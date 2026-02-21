package net.ooder.scene.skills.mqtt;

/**
 * MqttMessage MQTT消息
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class MqttMessage {

    private String topic;
    private byte[] payload;
    private int qos = 1;
    private boolean retained = false;
    private long timestamp;
    private String messageId;
    
    public MqttMessage() {
        this.timestamp = System.currentTimeMillis();
        this.messageId = "msg-" + timestamp + "-" + (int)(Math.random() * 10000);
    }
    
    public MqttMessage(String topic, byte[] payload) {
        this();
        this.topic = topic;
        this.payload = payload;
    }
    
    public MqttMessage(String topic, String payload) {
        this(topic, payload != null ? payload.getBytes() : null);
    }
    
    public static MqttMessage create(String topic, String payload) {
        return new MqttMessage(topic, payload);
    }
    
    public static MqttMessage create(String topic, byte[] payload) {
        return new MqttMessage(topic, payload);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
    
    public String getPayloadAsString() {
        return payload != null ? new String(payload) : null;
    }
    
    public void setPayload(String payload) {
        this.payload = payload != null ? payload.getBytes() : null;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
