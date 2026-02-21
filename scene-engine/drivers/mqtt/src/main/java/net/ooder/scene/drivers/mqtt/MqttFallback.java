package net.ooder.scene.drivers.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MqttFallback implements MqttSkill {
    
    private static final Logger logger = LoggerFactory.getLogger(MqttFallback.class);
    
    private static final String SKILL_ID = "skill-mqtt-fallback";
    private static final String SKILL_NAME = "MQTT Fallback Skill";
    private static final String SKILL_VERSION = "0.7.3";
    
    private MqttCapabilities capabilities;
    private boolean running = false;
    
    private Map<String, MessageHandler> subscriptionHandlers = new ConcurrentHashMap<String, MessageHandler>();
    private ExecutorService executorService;
    private Map<String, List<MqttMessage>> messageStore = new ConcurrentHashMap<String, List<MqttMessage>>();
    
    public MqttFallback() {
        this.capabilities = MqttCapabilities.forLightweight();
        this.executorService = Executors.newCachedThreadPool();
    }
    
    public void initialize() {
        logger.info("MQTT Fallback initialized with provider: {}", capabilities.getProviderType());
    }
    
    public void shutdown() {
        stop();
        logger.info("MQTT Fallback shutdown completed");
    }
    
    @Override
    public void initialize(MqttCapabilities capabilities) {
        this.capabilities = capabilities;
    }
    
    @Override
    public String getSkillId() {
        return SKILL_ID;
    }
    
    @Override
    public String getSkillName() {
        return SKILL_NAME;
    }
    
    @Override
    public String getSkillVersion() {
        return SKILL_VERSION;
    }
    
    @Override
    public List<String> getCapabilities() {
        List<String> caps = new ArrayList<String>();
        caps.add("mqtt.broker");
        caps.add("mqtt.publish");
        caps.add("mqtt.subscribe");
        caps.add("mqtt.p2p");
        caps.add("mqtt.topic");
        caps.add("mqtt.command");
        caps.add("mqtt.broadcast");
        return caps;
    }
    
    @Override
    public MqttCapabilities getMqttCapabilities() {
        return capabilities;
    }
    
    @Override
    public boolean isSupport(String capability) {
        switch (capability) {
            case "mqtt.broker": return capabilities.isSupportBroker();
            case "mqtt.publish": return capabilities.isSupportPublish();
            case "mqtt.subscribe": return capabilities.isSupportSubscribe();
            case "mqtt.p2p": return capabilities.isSupportP2P();
            case "mqtt.topic": return capabilities.isSupportTopic();
            case "mqtt.command": return capabilities.isSupportCommand();
            case "mqtt.broadcast": return capabilities.isSupportBroadcast();
            default: return false;
        }
    }
    
    @Override
    public void start() {
        if (running) {
            return;
        }
        running = true;
        logger.info("MQTT Fallback started");
    }
    
    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        subscriptionHandlers.clear();
        if (executorService != null) {
            executorService.shutdown();
        }
        logger.info("MQTT Fallback stopped");
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public boolean publish(String topic, String payload, int qos) {
        return publish(topic, payload != null ? payload.getBytes() : null, qos, false);
    }
    
    @Override
    public boolean publish(String topic, byte[] payload, int qos, boolean retained) {
        if (!running) {
            logger.warn("MQTT Fallback is not running");
            return false;
        }
        
        MqttMessage message = new MqttMessage(topic, payload);
        message.setQos(qos);
        message.setRetained(retained);
        
        storeMessage(topic, message);
        logger.debug("Published message to topic: {}", topic);
        return true;
    }
    
    @Override
    public boolean subscribe(String topic, int qos, MessageHandler handler) {
        if (!running) {
            logger.warn("MQTT Fallback is not running");
            return false;
        }
        
        subscriptionHandlers.put(topic, handler);
        logger.info("Subscribed to topic: {}", topic);
        return true;
    }
    
    @Override
    public boolean unsubscribe(String topic) {
        MessageHandler handler = subscriptionHandlers.remove(topic);
        return handler != null;
    }
    
    @Override
    public boolean sendP2P(String from, String to, String content) {
        if (!running) {
            return false;
        }
        
        String topic = "ooder/p2p/" + to + "/inbox";
        MqttMessage message = new MqttMessage(topic, content);
        message.setQos(1);
        
        storeMessage(topic, message);
        logger.debug("Sent P2P message from {} to {}", from, to);
        return true;
    }
    
    @Override
    public boolean sendBroadcast(String channel, String body, int qos) {
        if (!running) {
            return false;
        }
        
        String topic = "ooder/broadcast/" + channel;
        MqttMessage message = new MqttMessage(topic, body);
        message.setQos(qos);
        
        storeMessage(topic, message);
        logger.debug("Sent broadcast message to channel: {}", channel);
        return true;
    }
    
    @Override
    public boolean sendCommand(String deviceType, String deviceId, String command) {
        if (!running) {
            return false;
        }
        
        String topic = "ooder/command/" + deviceType + "/" + deviceId + "/request";
        MqttMessage message = new MqttMessage(topic, command);
        message.setQos(1);
        
        storeMessage(topic, message);
        logger.debug("Sent command to device: {}/{}", deviceType, deviceId);
        return true;
    }
    
    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        try {
            switch (capability) {
                case "mqtt.broker.start":
                    start();
                    return isRunning();
                case "mqtt.broker.stop":
                    stop();
                    return !isRunning();
                case "mqtt.broker.status":
                    return isRunning();
                case "mqtt.publish":
                    return publish(
                        (String) params.get("topic"),
                        (String) params.get("payload"),
                        params.get("qos") != null ? ((Number) params.get("qos")).intValue() : 1
                    );
                case "mqtt.subscribe":
                    return subscribe(
                        (String) params.get("topic"),
                        params.get("qos") != null ? ((Number) params.get("qos")).intValue() : 1,
                        null
                    );
                case "mqtt.unsubscribe":
                    return unsubscribe((String) params.get("topic"));
                case "mqtt.p2p":
                    return sendP2P(
                        (String) params.get("from"),
                        (String) params.get("to"),
                        (String) params.get("content")
                    );
                case "mqtt.broadcast":
                    return sendBroadcast(
                        (String) params.get("channel"),
                        (String) params.get("body"),
                        params.get("qos") != null ? ((Number) params.get("qos")).intValue() : 1
                    );
                case "mqtt.command":
                    return sendCommand(
                        (String) params.get("deviceType"),
                        (String) params.get("deviceId"),
                        (String) params.get("command")
                    );
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.error("Failed to invoke capability: {}", capability, e);
            return null;
        }
    }
    
    private void storeMessage(String topic, MqttMessage message) {
        if (!messageStore.containsKey(topic)) {
            messageStore.put(topic, new ArrayList<MqttMessage>());
        }
        messageStore.get(topic).add(message);
        
        notifyHandlers(topic, message);
    }
    
    private void notifyHandlers(final String topic, final MqttMessage message) {
        for (Map.Entry<String, MessageHandler> entry : subscriptionHandlers.entrySet()) {
            final String subTopic = entry.getKey();
            final MessageHandler handler = entry.getValue();
            
            if (topicMatches(subTopic, topic) && handler != null) {
                executorService.submit(new Runnable() {
                    public void run() {
                        try {
                            handler.onMessage(message);
                        } catch (Exception e) {
                            logger.error("Error in message handler", e);
                        }
                    }
                });
            }
        }
    }
    
    private boolean topicMatches(String subscription, String topic) {
        if (subscription.equals(topic)) {
            return true;
        }
        
        if (subscription.endsWith("#")) {
            String prefix = subscription.substring(0, subscription.length() - 1);
            return topic.startsWith(prefix);
        }
        
        if (subscription.contains("+")) {
            String[] subParts = subscription.split("/");
            String[] topicParts = topic.split("/");
            
            if (subParts.length != topicParts.length) {
                return false;
            }
            
            for (int i = 0; i < subParts.length; i++) {
                if (!subParts[i].equals("+") && !subParts[i].equals(topicParts[i])) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
}
