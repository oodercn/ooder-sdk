package net.ooder.scene.drivers.mqtt;

import java.util.List;
import java.util.Map;

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
