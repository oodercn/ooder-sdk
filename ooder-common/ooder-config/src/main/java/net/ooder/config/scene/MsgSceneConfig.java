package net.ooder.config.scene;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MsgSceneConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneId;
    private String configName;
    
    private String personId;
    private String msgClass = "net.ooder.msg.ct.CtMsg";
    
    private String mqttBroker;
    private Integer mqttPort = 1883;
    private String mqttClientId;
    private String mqttUsername;
    private String mqttPassword;
    private boolean retainMessages = false;
    private Integer qos = 1;
    
    private boolean cacheEnabled = true;
    private long cacheExpireTime = 86400000L;
    private int cacheSize = 10485760;
    
    private Integer connectTimeout = 5000;
    private Integer readTimeout = 30000;
    
    private Map<String, CapabilityConfig> capabilities = new HashMap<String, CapabilityConfig>();
    
    public MsgSceneConfig() {
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    
    public String getPersonId() {
        return personId;
    }
    
    public void setPersonId(String personId) {
        this.personId = personId;
    }
    
    public String getMsgClass() {
        return msgClass;
    }
    
    public void setMsgClass(String msgClass) {
        this.msgClass = msgClass;
    }
    
    public String getMqttBroker() {
        return mqttBroker;
    }
    
    public void setMqttBroker(String mqttBroker) {
        this.mqttBroker = mqttBroker;
    }
    
    public Integer getMqttPort() {
        return mqttPort;
    }
    
    public void setMqttPort(Integer mqttPort) {
        this.mqttPort = mqttPort;
    }
    
    public String getMqttClientId() {
        return mqttClientId;
    }
    
    public void setMqttClientId(String mqttClientId) {
        this.mqttClientId = mqttClientId;
    }
    
    public String getMqttUsername() {
        return mqttUsername;
    }
    
    public void setMqttUsername(String mqttUsername) {
        this.mqttUsername = mqttUsername;
    }
    
    public String getMqttPassword() {
        return mqttPassword;
    }
    
    public void setMqttPassword(String mqttPassword) {
        this.mqttPassword = mqttPassword;
    }
    
    public boolean isRetainMessages() {
        return retainMessages;
    }
    
    public void setRetainMessages(boolean retainMessages) {
        this.retainMessages = retainMessages;
    }
    
    public Integer getQos() {
        return qos;
    }
    
    public void setQos(Integer qos) {
        this.qos = qos;
    }
    
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
    
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
    
    public long getCacheExpireTime() {
        return cacheExpireTime;
    }
    
    public void setCacheExpireTime(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
    }
    
    public int getCacheSize() {
        return cacheSize;
    }
    
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public Integer getConnectTimeout() {
        return connectTimeout;
    }
    
    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public Integer getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public Map<String, CapabilityConfig> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, CapabilityConfig> capabilities) {
        this.capabilities = capabilities;
    }
    
    @Override
    public String toString() {
        return "MsgSceneConfig{" +
                "sceneId='" + sceneId + '\'' +
                ", configName='" + configName + '\'' +
                ", personId='" + personId + '\'' +
                '}';
    }
}
