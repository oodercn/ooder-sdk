package net.ooder.scene.skills.mqtt;

import java.util.HashSet;
import java.util.Set;

/**
 * MqttCapabilities MQTT能力配置
 * 
 * <p>定义MQTT技能支持的能力集合，用于判断哪些功能由外部 skills 提供，
 * 哪些需要本地降级实现。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class MqttCapabilities {

    private boolean supportBroker = true;
    private boolean supportPublish = true;
    private boolean supportSubscribe = true;
    private boolean supportP2P = true;
    private boolean supportTopic = true;
    private boolean supportCommand = true;
    private boolean supportBroadcast = true;
    
    private boolean supportQos0 = true;
    private boolean supportQos1 = true;
    private boolean supportQos2 = false;
    
    private boolean supportRetained = false;
    private boolean supportWill = false;
    private boolean supportTls = false;
    private boolean supportAuth = true;
    
    private boolean supportCluster = false;
    private boolean supportBridge = false;
    private boolean supportRuleEngine = false;

    private String providerType = "lightweight";
    private String brokerUrl;
    private String skillEndpoint;
    private String skillId;
    private int port = 1883;
    private int websocketPort = 8083;

    public boolean isSupportBroker() {
        return supportBroker;
    }

    public void setSupportBroker(boolean supportBroker) {
        this.supportBroker = supportBroker;
    }

    public boolean isSupportPublish() {
        return supportPublish;
    }

    public void setSupportPublish(boolean supportPublish) {
        this.supportPublish = supportPublish;
    }

    public boolean isSupportSubscribe() {
        return supportSubscribe;
    }

    public void setSupportSubscribe(boolean supportSubscribe) {
        this.supportSubscribe = supportSubscribe;
    }

    public boolean isSupportP2P() {
        return supportP2P;
    }

    public void setSupportP2P(boolean supportP2P) {
        this.supportP2P = supportP2P;
    }

    public boolean isSupportTopic() {
        return supportTopic;
    }

    public void setSupportTopic(boolean supportTopic) {
        this.supportTopic = supportTopic;
    }

    public boolean isSupportCommand() {
        return supportCommand;
    }

    public void setSupportCommand(boolean supportCommand) {
        this.supportCommand = supportCommand;
    }

    public boolean isSupportBroadcast() {
        return supportBroadcast;
    }

    public void setSupportBroadcast(boolean supportBroadcast) {
        this.supportBroadcast = supportBroadcast;
    }

    public boolean isSupportQos0() {
        return supportQos0;
    }

    public void setSupportQos0(boolean supportQos0) {
        this.supportQos0 = supportQos0;
    }

    public boolean isSupportQos1() {
        return supportQos1;
    }

    public void setSupportQos1(boolean supportQos1) {
        this.supportQos1 = supportQos1;
    }

    public boolean isSupportQos2() {
        return supportQos2;
    }

    public void setSupportQos2(boolean supportQos2) {
        this.supportQos2 = supportQos2;
    }

    public boolean isSupportRetained() {
        return supportRetained;
    }

    public void setSupportRetained(boolean supportRetained) {
        this.supportRetained = supportRetained;
    }

    public boolean isSupportWill() {
        return supportWill;
    }

    public void setSupportWill(boolean supportWill) {
        this.supportWill = supportWill;
    }

    public boolean isSupportTls() {
        return supportTls;
    }

    public void setSupportTls(boolean supportTls) {
        this.supportTls = supportTls;
    }

    public boolean isSupportAuth() {
        return supportAuth;
    }

    public void setSupportAuth(boolean supportAuth) {
        this.supportAuth = supportAuth;
    }

    public boolean isSupportCluster() {
        return supportCluster;
    }

    public void setSupportCluster(boolean supportCluster) {
        this.supportCluster = supportCluster;
    }

    public boolean isSupportBridge() {
        return supportBridge;
    }

    public void setSupportBridge(boolean supportBridge) {
        this.supportBridge = supportBridge;
    }

    public boolean isSupportRuleEngine() {
        return supportRuleEngine;
    }

    public void setSupportRuleEngine(boolean supportRuleEngine) {
        this.supportRuleEngine = supportRuleEngine;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getSkillEndpoint() {
        return skillEndpoint;
    }

    public void setSkillEndpoint(String skillEndpoint) {
        this.skillEndpoint = skillEndpoint;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWebsocketPort() {
        return websocketPort;
    }

    public void setWebsocketPort(int websocketPort) {
        this.websocketPort = websocketPort;
    }

    public Set<String> getUnsupportedCapabilities() {
        Set<String> unsupported = new HashSet<String>();
        if (!supportQos2) unsupported.add("qos2");
        if (!supportRetained) unsupported.add("retained");
        if (!supportWill) unsupported.add("will");
        if (!supportTls) unsupported.add("tls");
        if (!supportCluster) unsupported.add("cluster");
        if (!supportBridge) unsupported.add("bridge");
        if (!supportRuleEngine) unsupported.add("rule-engine");
        return unsupported;
    }

    public boolean requiresFallback() {
        return !supportQos2 || !supportRetained || !supportWill 
            || !supportTls || !supportCluster || !supportBridge || !supportRuleEngine;
    }

    public static MqttCapabilities forLightweight() {
        MqttCapabilities caps = new MqttCapabilities();
        caps.setProviderType("lightweight");
        caps.setSkillId("skill-mqtt-lightweight");
        caps.setSupportBroker(true);
        caps.setSupportPublish(true);
        caps.setSupportSubscribe(true);
        caps.setSupportP2P(true);
        caps.setSupportTopic(true);
        caps.setSupportCommand(true);
        caps.setSupportBroadcast(true);
        caps.setSupportQos0(true);
        caps.setSupportQos1(true);
        caps.setSupportQos2(true);
        caps.setSupportAuth(true);
        caps.setSupportCluster(false);
        caps.setSupportTls(false);
        caps.setSupportRuleEngine(false);
        return caps;
    }

    public static MqttCapabilities forEMQX() {
        MqttCapabilities caps = new MqttCapabilities();
        caps.setProviderType("emqx");
        caps.setSkillId("skill-mqtt-emqx");
        caps.setSupportBroker(true);
        caps.setSupportPublish(true);
        caps.setSupportSubscribe(true);
        caps.setSupportP2P(true);
        caps.setSupportTopic(true);
        caps.setSupportCommand(true);
        caps.setSupportBroadcast(true);
        caps.setSupportQos0(true);
        caps.setSupportQos1(true);
        caps.setSupportQos2(true);
        caps.setSupportRetained(true);
        caps.setSupportWill(true);
        caps.setSupportTls(true);
        caps.setSupportAuth(true);
        caps.setSupportCluster(true);
        caps.setSupportBridge(true);
        caps.setSupportRuleEngine(true);
        return caps;
    }

    public static MqttCapabilities forMosquitto() {
        MqttCapabilities caps = new MqttCapabilities();
        caps.setProviderType("mosquitto");
        caps.setSkillId("skill-mqtt-mosquitto");
        caps.setSupportBroker(true);
        caps.setSupportPublish(true);
        caps.setSupportSubscribe(true);
        caps.setSupportP2P(true);
        caps.setSupportTopic(true);
        caps.setSupportCommand(true);
        caps.setSupportBroadcast(true);
        caps.setSupportQos0(true);
        caps.setSupportQos1(true);
        caps.setSupportQos2(true);
        caps.setSupportRetained(true);
        caps.setSupportWill(true);
        caps.setSupportTls(true);
        caps.setSupportAuth(true);
        caps.setSupportCluster(false);
        caps.setSupportBridge(true);
        caps.setSupportRuleEngine(false);
        return caps;
    }

    public static MqttCapabilities forAliyunIoT() {
        MqttCapabilities caps = new MqttCapabilities();
        caps.setProviderType("aliyun-iot");
        caps.setSkillId("skill-mqtt-aliyun");
        caps.setSupportBroker(false);
        caps.setSupportPublish(true);
        caps.setSupportSubscribe(true);
        caps.setSupportP2P(true);
        caps.setSupportTopic(true);
        caps.setSupportCommand(true);
        caps.setSupportBroadcast(true);
        caps.setSupportQos0(true);
        caps.setSupportQos1(true);
        caps.setSupportQos2(false);
        caps.setSupportRetained(false);
        caps.setSupportWill(false);
        caps.setSupportTls(true);
        caps.setSupportAuth(true);
        caps.setSupportCluster(true);
        caps.setSupportBridge(false);
        caps.setSupportRuleEngine(true);
        return caps;
    }

    public static MqttCapabilities forTencentIoT() {
        MqttCapabilities caps = new MqttCapabilities();
        caps.setProviderType("tencent-iot");
        caps.setSkillId("skill-mqtt-tencent");
        caps.setSupportBroker(false);
        caps.setSupportPublish(true);
        caps.setSupportSubscribe(true);
        caps.setSupportP2P(true);
        caps.setSupportTopic(true);
        caps.setSupportCommand(true);
        caps.setSupportBroadcast(true);
        caps.setSupportQos0(true);
        caps.setSupportQos1(true);
        caps.setSupportQos2(false);
        caps.setSupportRetained(false);
        caps.setSupportWill(false);
        caps.setSupportTls(true);
        caps.setSupportAuth(true);
        caps.setSupportCluster(true);
        caps.setSupportBridge(false);
        caps.setSupportRuleEngine(true);
        return caps;
    }
}
