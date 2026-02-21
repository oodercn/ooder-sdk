package net.ooder.northbound.scene;

import net.ooder.northbound.client.OrgWebClient;
import net.ooder.northbound.client.VfsWebClient;
import net.ooder.northbound.client.MsgWebClient;
import net.ooder.northbound.client.specialized.GwClient;
import net.ooder.northbound.client.specialized.GwClient.MqttEvent;
import net.ooder.northbound.client.specialized.GwClient.MqttEventListener;
import net.ooder.northbound.client.impl.UserClientImpl;
import net.ooder.northbound.client.impl.GwClientImpl;
import net.ooder.northbound.engine.ServiceEngine;
import net.ooder.northbound.engine.impl.ServiceEngineImpl;
import net.ooder.northbound.mqtt.context.MqttSessionManager;
import net.ooder.northbound.mqtt.provider.MqttProviderFactory;
import net.ooder.northbound.mqtt.spec.MqttTopicSpec;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用户故事测试 - 验证场景配置
 * 
 * 用户故事:
 * 1. 作为用户，我希望能够通过场景配置初始化所有服�? * 2. 作为用户，我希望能够使用MQTT服务进行消息通信
 * 3. 作为用户，我希望能够订阅和发布MQTT消息
 * 4. 作为用户，我希望能够通过GwClient接收MQTT事件
 */
public class UserStoryTest {
    
    private ServiceEngine engine;
    private UserClientImpl userClient;
    private GwClientImpl gwClient;
    
    @Before
    public void setUp() {
        System.out.println("========================================");
        System.out.println("用户故事测试 - 场景配置验证");
        System.out.println("版本: 0.7.3");
        System.out.println("========================================");
        
        engine = new ServiceEngineImpl();
        userClient = new UserClientImpl("story-user", "story-session", "story-token", engine);
        gwClient = new GwClientImpl(userClient);
        
        MqttSessionManager.getInstance().clearAll();
    }
    
    @After
    public void tearDown() {
        MqttSessionManager.getInstance().clearAll();
    }
    
    /**
     * 用户故事1: 场景配置初始�?     * 
     * 作为系统管理�?     * 我希望能够通过场景配置初始化所有服�?     * 以便快速部署和配置系统
     */
    @Test
    public void story1_SceneConfigurationInitialization() {
        System.out.println("\n用户故事1: 场景配置初始�?);
        System.out.println("----------------------------------------");
        
        Map<String, Object> sceneConfig = createDevSceneConfig();
        
        assertNotNull("场景配置不应为空", sceneConfig);
        assertTrue("场景配置应包含org", sceneConfig.containsKey("org"));
        assertTrue("场景配置应包含vfs", sceneConfig.containsKey("vfs"));
        assertTrue("场景配置应包含msg", sceneConfig.containsKey("msg"));
        assertTrue("场景配置应包含mqtt", sceneConfig.containsKey("mqtt"));
        assertTrue("场景配置应包含protocols", sceneConfig.containsKey("protocols"));
        
        System.out.println("场景配置验证通过:");
        System.out.println("  - org配置: " + ((Map)sceneConfig.get("org")).get("sceneId"));
        System.out.println("  - vfs配置: " + ((Map)sceneConfig.get("vfs")).get("sceneId"));
        System.out.println("  - msg配置: " + ((Map)sceneConfig.get("msg")).get("sceneId"));
        System.out.println("  - mqtt配置: " + ((Map)sceneConfig.get("mqtt")).get("sceneId"));
        
        Map<String, Object> mqttConfig = (Map<String, Object>) sceneConfig.get("mqtt");
        assertEquals("MQTT端口应为1883", 1883, mqttConfig.get("port"));
        assertEquals("MQTT提供者应为lightweight-mqtt", "lightweight-mqtt", mqttConfig.get("providerId"));
        assertTrue("应允许匿名连�?, (Boolean) mqttConfig.get("allowAnonymous"));
        
        System.out.println("MQTT配置验证通过:");
        System.out.println("  - 端口: " + mqttConfig.get("port"));
        System.out.println("  - WebSocket端口: " + mqttConfig.get("websocketPort"));
        System.out.println("  - 提供�? " + mqttConfig.get("providerId"));
        System.out.println("  - 最大连接数: " + mqttConfig.get("maxConnections"));
    }
    
    /**
     * 用户故事2: MQTT服务启动和停�?     * 
     * 作为IoT设备开发�?     * 我希望能够启动和停止MQTT Broker服务
     * 以便控制消息服务的生命周�?     */
    @Test
    public void story2_MqttServiceStartAndStop() {
        System.out.println("\n用户故事2: MQTT服务启动和停�?);
        System.out.println("----------------------------------------");
        
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("providerId", "lightweight-mqtt");
        config.put("port", 1883);
        config.put("allowAnonymous", true);
        
        boolean registered = gwClient.registerMqttService("story-mqtt-service", "tcp://localhost:1883", config);
        assertTrue("MQTT服务注册应成�?, registered);
        System.out.println("MQTT服务注册成功: story-mqtt-service");
        
        Map<String, Object> status = gwClient.getMqttServiceStatus();
        assertTrue("服务应已启用", (Boolean) status.get("enabled"));
        assertTrue("服务应正在运�?, (Boolean) status.get("running"));
        System.out.println("MQTT服务状�? " + status.get("status"));
        
        assertNotNull("统计信息不应为空", status.get("statistics"));
        System.out.println("连接�? " + status.get("connectedCount"));
    }
    
    /**
     * 用户故事3: MQTT消息订阅和发�?     * 
     * 作为消息应用开发�?     * 我希望能够订阅和发布MQTT消息
     * 以便实现设备间的消息通信
     */
    @Test
    public void story3_MqttSubscribeAndPublish() throws InterruptedException {
        System.out.println("\n用户故事3: MQTT消息订阅和发�?);
        System.out.println("----------------------------------------");
        
        final List<MqttEvent> events = new ArrayList<MqttEvent>();
        final CountDownLatch subscribeLatch = new CountDownLatch(1);
        final CountDownLatch publishLatch = new CountDownLatch(2);
        
        gwClient.addMqttEventListener(new MqttEventListener() {
            @Override
            public void onMqttEvent(MqttEvent event) {
                events.add(event);
                if (MqttEvent.TYPE_SUBSCRIBE.equals(event.getEventType())) {
                    subscribeLatch.countDown();
                }
                if (MqttEvent.TYPE_PUBLISH.equals(event.getEventType())) {
                    publishLatch.countDown();
                }
            }
        });
        
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("providerId", "lightweight-mqtt");
        gwClient.registerMqttService("story-service", "tcp://localhost:1883", config);
        
        String topic = "ooder/topic/story-test/data";
        boolean subscribed = gwClient.subscribeMqttTopic(topic, 1);
        assertTrue("订阅应成�?, subscribed);
        System.out.println("订阅主题成功: " + topic);
        
        subscribeLatch.await(2, TimeUnit.SECONDS);
        
        String payload = "Hello from user story test!";
        boolean published = gwClient.publishMqttMessage(topic, payload, 1);
        assertTrue("发布应成�?, published);
        System.out.println("发布消息成功: " + payload);
        
        publishLatch.await(2, TimeUnit.SECONDS);
        
        boolean foundSubscribe = false;
        boolean foundPublish = false;
        
        for (MqttEvent event : events) {
            if (MqttEvent.TYPE_SUBSCRIBE.equals(event.getEventType())) {
                foundSubscribe = true;
                assertEquals("订阅主题应匹�?, topic, event.getTopic());
            }
            if (MqttEvent.TYPE_PUBLISH.equals(event.getEventType())) {
                foundPublish = true;
                assertEquals("发布主题应匹�?, topic, event.getTopic());
                assertEquals("发布内容应匹�?, payload, event.getPayload());
            }
        }
        
        assertTrue("应收到订阅事�?, foundSubscribe);
        assertTrue("应收到发布事�?, foundPublish);
        System.out.println("事件验证通过: 收到" + events.size() + "个事�?);
    }
    
    /**
     * 用户故事4: MQTT Topic规范
     * 
     * 作为系统架构�?     * 我希望使用标准的Topic规范
     * 以便统一消息路由规则
     */
    @Test
    public void story4_MqttTopicSpecification() {
        System.out.println("\n用户故事4: MQTT Topic规范");
        System.out.println("----------------------------------------");
        
        String p2pTopic = MqttTopicSpec.p2pTopic("user-001");
        assertEquals("P2P Topic应符合规�?, "ooder/p2p/user-001/inbox", p2pTopic);
        System.out.println("P2P Topic: " + p2pTopic);
        
        String groupTopic = MqttTopicSpec.groupTopic("group-001");
        assertEquals("Group Topic应符合规�?, "ooder/group/group-001/broadcast", groupTopic);
        System.out.println("Group Topic: " + groupTopic);
        
        String topicPath = MqttTopicSpec.topicPath("news");
        assertEquals("Topic Path应符合规�?, "ooder/topic/news/data", topicPath);
        System.out.println("Topic Path: " + topicPath);
        
        String sensorTopic = MqttTopicSpec.sensorTopic("temperature", "sensor-001");
        assertEquals("Sensor Topic应符合规�?, "ooder/sensor/temperature/sensor-001/data", sensorTopic);
        System.out.println("Sensor Topic: " + sensorTopic);
        
        String cmdReqTopic = MqttTopicSpec.commandRequestTopic("device", "dev-001");
        assertEquals("Command Request Topic应符合规�?, "ooder/command/device/dev-001/request", cmdReqTopic);
        System.out.println("Command Request Topic: " + cmdReqTopic);
        
        String cmdRespTopic = MqttTopicSpec.commandResponseTopic("device", "dev-001");
        assertEquals("Command Response Topic应符合规�?, "ooder/command/device/dev-001/response", cmdRespTopic);
        System.out.println("Command Response Topic: " + cmdRespTopic);
        
        assertTrue("应识别P2P Topic", MqttTopicSpec.isP2PTopic(p2pTopic));
        assertTrue("应识别Group Topic", MqttTopicSpec.isGroupTopic(groupTopic));
        assertTrue("应识别Sensor Topic", MqttTopicSpec.isSensorTopic(sensorTopic));
        assertTrue("应识别Command Topic", MqttTopicSpec.isCommandTopic(cmdReqTopic));
        
        System.out.println("Topic规范验证通过");
    }
    
    /**
     * 用户故事5: 多服务提供商支持
     * 
     * 作为运维工程�?     * 我希望能够选择不同的MQTT服务提供�?     * 以便根据业务需求选择合适的方案
     */
    @Test
    public void story5_MultiProviderSupport() {
        System.out.println("\n用户故事5: 多服务提供商支持");
        System.out.println("----------------------------------------");
        
        MqttProviderFactory factory = MqttProviderFactory.getInstance();
        factory.initialize();
        
        List<Map<String, Object>> providers = gwClient.getAvailableProviders();
        
        assertNotNull("提供者列表不应为�?, providers);
        assertFalse("提供者列表不应为�?, providers.isEmpty());
        System.out.println("可用提供者数�? " + providers.size());
        
        Map<String, String> expectedProviders = new HashMap<String, String>();
        expectedProviders.put("lightweight-mqtt", "LIGHTWEIGHT");
        expectedProviders.put("emqx-enterprise", "ENTERPRISE_SELF_HOSTED");
        expectedProviders.put("mosquitto-enterprise", "ENTERPRISE_SELF_HOSTED");
        expectedProviders.put("aliyun-iot", "CLOUD_MANAGED");
        expectedProviders.put("tencent-iot", "CLOUD_MANAGED");
        
        for (Map<String, Object> provider : providers) {
            String providerId = (String) provider.get("providerId");
            String providerType = (String) provider.get("providerType");
            
            if (expectedProviders.containsKey(providerId)) {
                assertEquals("提供者类型应匹配", expectedProviders.get(providerId), providerType);
                System.out.println("  - " + providerId + ": " + provider.get("providerName") + " (优先�? " + provider.get("priority") + ")");
            }
        }
        
        System.out.println("多提供者支持验证通过");
    }
    
    /**
     * 用户故事6: GwClient事件监听
     * 
     * 作为应用开发�?     * 我希望能够监听MQTT事件
     * 以便在消息到达时执行业务逻辑
     */
    @Test
    public void story6_GwClientEventListening() throws InterruptedException {
        System.out.println("\n用户故事6: GwClient事件监听");
        System.out.println("----------------------------------------");
        
        final List<MqttEvent> receivedEvents = new ArrayList<MqttEvent>();
        final CountDownLatch latch = new CountDownLatch(3);
        
        MqttEventListener listener = new MqttEventListener() {
            @Override
            public void onMqttEvent(MqttEvent event) {
                receivedEvents.add(event);
                System.out.println("收到事件: " + event.getEventType() + " - " + event.getTopic());
                latch.countDown();
            }
        };
        
        gwClient.addMqttEventListener(listener);
        System.out.println("添加事件监听�?);
        
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("providerId", "lightweight-mqtt");
        gwClient.registerMqttService("story-listener-service", "tcp://localhost:1883", config);
        
        gwClient.subscribeMqttTopic("ooder/test/story", 1);
        gwClient.publishMqttMessage("ooder/test/story", "test message", 1);
        
        latch.await(3, TimeUnit.SECONDS);
        
        assertTrue("应收到至�?个事�?, receivedEvents.size() >= 3);
        
        Set<String> eventTypes = new HashSet<String>();
        for (MqttEvent event : receivedEvents) {
            eventTypes.add(event.getEventType());
        }
        
        assertTrue("应包含connect事件", eventTypes.contains(MqttEvent.TYPE_CONNECT));
        assertTrue("应包含subscribe事件", eventTypes.contains(MqttEvent.TYPE_SUBSCRIBE));
        assertTrue("应包含publish事件", eventTypes.contains(MqttEvent.TYPE_PUBLISH));
        
        System.out.println("事件监听验证通过: 收到" + receivedEvents.size() + "个事�?);
        
        gwClient.removeMqttEventListener(listener);
        System.out.println("移除事件监听�?);
    }
    
    private Map<String, Object> createDevSceneConfig() {
        Map<String, Object> config = new HashMap<String, Object>();
        
        Map<String, Object> orgConfig = new HashMap<String, Object>();
        orgConfig.put("sceneId", "dev-org");
        orgConfig.put("configName", "org");
        orgConfig.put("dbDriver", "org.hsqldb.jdbcDriver");
        orgConfig.put("dbUrl", "jdbc:hsqldb:mem:devdb");
        orgConfig.put("dbUser", "sa");
        orgConfig.put("dbPassword", "");
        config.put("org", orgConfig);
        
        Map<String, Object> vfsConfig = new HashMap<String, Object>();
        vfsConfig.put("sceneId", "dev-vfs");
        vfsConfig.put("configName", "vfs");
        vfsConfig.put("storageType", "local");
        vfsConfig.put("basePath", "./data/vfs");
        config.put("vfs", vfsConfig);
        
        Map<String, Object> msgConfig = new HashMap<String, Object>();
        msgConfig.put("sceneId", "dev-msg");
        msgConfig.put("configName", "msg");
        msgConfig.put("personId", "dev-user");
        msgConfig.put("mqttBroker", "tcp://localhost:1883");
        config.put("msg", msgConfig);
        
        Map<String, Object> mqttConfig = new HashMap<String, Object>();
        mqttConfig.put("sceneId", "dev-mqtt");
        mqttConfig.put("configName", "mqtt");
        mqttConfig.put("providerId", "lightweight-mqtt");
        mqttConfig.put("port", 1883);
        mqttConfig.put("websocketPort", 8083);
        mqttConfig.put("maxConnections", 10000);
        mqttConfig.put("allowAnonymous", true);
        config.put("mqtt", mqttConfig);
        
        Map<String, Object> protocolsConfig = new HashMap<String, Object>();
        Map<String, Object> mqttProtocolConfig = new HashMap<String, Object>();
        mqttProtocolConfig.put("enabled", true);
        mqttProtocolConfig.put("defaultProvider", "lightweight-mqtt");
        protocolsConfig.put("mqtt", mqttProtocolConfig);
        config.put("protocols", protocolsConfig);
        
        return config;
    }
}
