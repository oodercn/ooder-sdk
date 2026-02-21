package net.ooder.scene.skills.mqtt;

import java.util.List;
import java.util.Map;

/**
 * MqttSkill MQTT技能接口
 * 
 * <p>提供MQTT消息通信能力，包括消息发布、订阅、P2P消息等。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface MqttSkill {

    String getSkillId();
    
    String getSkillName();
    
    String getSkillVersion();
    
    List<String> getCapabilities();
    
    void initialize(MqttCapabilities capabilities);
    
    void start();
    
    void stop();
    
    boolean isRunning();
    
    boolean publish(String topic, String payload, int qos);
    
    boolean publish(String topic, byte[] payload, int qos, boolean retained);
    
    boolean subscribe(String topic, int qos, MessageHandler handler);
    
    boolean unsubscribe(String topic);
    
    boolean sendP2P(String from, String to, String content);
    
    boolean sendBroadcast(String channel, String body, int qos);
    
    boolean sendCommand(String deviceType, String deviceId, String command);
    
    MqttCapabilities getMqttCapabilities();
    
    boolean isSupport(String capability);
    
    Object invoke(String capability, Map<String, Object> params);
    
    interface MessageHandler {
        void onMessage(MqttMessage message);
    }
}
