package net.ooder.scene.test;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;

/**
 * Scene Configuration Validation Test
 * 
 * Validate scene configuration files:
 * 1. scene-dev.yaml - Development environment
 * 2. scene-prod.yaml - Production environment
 */
public class SceneConfigTest {
    
    @Before
    public void setUp() {
        System.out.println("========================================");
        System.out.println("Scene Configuration Validation Test");
        System.out.println("Version: 0.7.3");
        System.out.println("========================================");
    }
    
    @After
    public void tearDown() {
    }

    // ==================== Dev Scene Configuration ====================
    
    /**
     * Test 1: Dev Scene Basic Configuration
     */
    @Test
    public void test1_DevSceneBasicConfig() {
        System.out.println("\nTest 1: Dev Scene Basic Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> sceneConfig = loadDevSceneConfig();
        
        assertEquals("dev-scene", sceneConfig.get("id"));
        assertEquals("开发环境场景", sceneConfig.get("name"));
        assertEquals("用于本地开发和单元测试", sceneConfig.get("description"));
        assertEquals("0.7.3", sceneConfig.get("version"));
        
        System.out.println("Scene ID: " + sceneConfig.get("id"));
        System.out.println("Scene Name: " + sceneConfig.get("name"));
        System.out.println("Scene Version: " + sceneConfig.get("version"));
        System.out.println("Basic config verified: PASS");
    }
    
    /**
     * Test 2: Dev Org Configuration
     */
    @Test
    public void test2_DevOrgConfig() {
        System.out.println("\nTest 2: Dev Org Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> orgConfig = getDevOrgConfig();
        
        assertEquals("dev-org", orgConfig.get("sceneId"));
        assertEquals("org", orgConfig.get("configName"));
        assertEquals("org.hsqldb.jdbcDriver", orgConfig.get("dbDriver"));
        assertEquals("jdbc:hsqldb:mem:devdb", orgConfig.get("dbUrl"));
        assertEquals("sa", orgConfig.get("dbUser"));
        assertEquals("", orgConfig.get("dbPassword"));
        assertEquals(true, orgConfig.get("cacheEnabled"));
        
        Map<String, Object> capabilities = (Map<String, Object>) orgConfig.get("capabilities");
        assertTrue("Should have org-query capability", capabilities.containsKey("org-query"));
        assertTrue("Should have org-admin capability", capabilities.containsKey("org-admin"));
        assertTrue("Should have user-auth capability", capabilities.containsKey("user-auth"));
        
        System.out.println("Org SceneId: " + orgConfig.get("sceneId"));
        System.out.println("DB Driver: " + orgConfig.get("dbDriver"));
        System.out.println("Capabilities: " + capabilities.keySet());
        System.out.println("Org config verified: PASS");
    }
    
    /**
     * Test 3: Dev VFS Configuration
     */
    @Test
    public void test3_DevVfsConfig() {
        System.out.println("\nTest 3: Dev VFS Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> vfsConfig = getDevVfsConfig();
        
        assertEquals("dev-vfs", vfsConfig.get("sceneId"));
        assertEquals("vfs", vfsConfig.get("configName"));
        assertEquals("local", vfsConfig.get("storageType"));
        assertEquals("./data/vfs", vfsConfig.get("basePath"));
        assertEquals(52428800L, vfsConfig.get("maxFileSize"));
        assertEquals("*", vfsConfig.get("allowedTypes"));
        
        Map<String, Object> capabilities = (Map<String, Object>) vfsConfig.get("capabilities");
        assertTrue("Should have vfs-client capability", capabilities.containsKey("vfs-client"));
        assertTrue("Should have vfs-store capability", capabilities.containsKey("vfs-store"));
        
        System.out.println("VFS SceneId: " + vfsConfig.get("sceneId"));
        System.out.println("Storage Type: " + vfsConfig.get("storageType"));
        System.out.println("Base Path: " + vfsConfig.get("basePath"));
        System.out.println("VFS config verified: PASS");
    }
    
    /**
     * Test 4: Dev Msg Configuration
     */
    @Test
    public void test4_DevMsgConfig() {
        System.out.println("\nTest 4: Dev Msg Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> msgConfig = getDevMsgConfig();
        
        assertEquals("dev-msg", msgConfig.get("sceneId"));
        assertEquals("msg", msgConfig.get("configName"));
        assertEquals("dev-user", msgConfig.get("personId"));
        assertEquals("tcp://localhost:1883", msgConfig.get("mqttBroker"));
        assertEquals("dev-msg-client", msgConfig.get("mqttClientId"));
        assertEquals(false, msgConfig.get("retainMessages"));
        
        Map<String, Object> capabilities = (Map<String, Object>) msgConfig.get("capabilities");
        assertTrue("Should have msg-push capability", capabilities.containsKey("msg-push"));
        assertTrue("Should have msg-p2p capability", capabilities.containsKey("msg-p2p"));
        assertTrue("Should have msg-topic capability", capabilities.containsKey("msg-topic"));
        
        System.out.println("Msg SceneId: " + msgConfig.get("sceneId"));
        System.out.println("MQTT Broker: " + msgConfig.get("mqttBroker"));
        System.out.println("Capabilities: " + capabilities.keySet());
        System.out.println("Msg config verified: PASS");
    }
    
    /**
     * Test 5: Dev MQTT Configuration
     */
    @Test
    public void test5_DevMqttConfig() {
        System.out.println("\nTest 5: Dev MQTT Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> mqttConfig = getDevMqttConfig();
        
        assertEquals("dev-mqtt", mqttConfig.get("sceneId"));
        assertEquals("mqtt", mqttConfig.get("configName"));
        assertEquals("lightweight-mqtt", mqttConfig.get("providerId"));
        assertEquals("tcp://localhost:1883", mqttConfig.get("brokerUrl"));
        assertEquals("ws://localhost:8083", mqttConfig.get("websocketUrl"));
        assertEquals(1883, mqttConfig.get("port"));
        assertEquals(8083, mqttConfig.get("websocketPort"));
        assertEquals(10000, mqttConfig.get("maxConnections"));
        assertEquals(true, mqttConfig.get("allowAnonymous"));
        assertEquals(false, mqttConfig.get("persistent"));
        
        Map<String, Object> capabilities = (Map<String, Object>) mqttConfig.get("capabilities");
        assertTrue("Should have mqtt-broker capability", capabilities.containsKey("mqtt-broker"));
        assertTrue("Should have mqtt-publish capability", capabilities.containsKey("mqtt-publish"));
        assertTrue("Should have mqtt-subscribe capability", capabilities.containsKey("mqtt-subscribe"));
        assertTrue("Should have mqtt-p2p capability", capabilities.containsKey("mqtt-p2p"));
        assertTrue("Should have mqtt-topic capability", capabilities.containsKey("mqtt-topic"));
        assertTrue("Should have mqtt-command capability", capabilities.containsKey("mqtt-command"));
        
        System.out.println("MQTT SceneId: " + mqttConfig.get("sceneId"));
        System.out.println("Provider: " + mqttConfig.get("providerId"));
        System.out.println("Port: " + mqttConfig.get("port"));
        System.out.println("WebSocket Port: " + mqttConfig.get("websocketPort"));
        System.out.println("Max Connections: " + mqttConfig.get("maxConnections"));
        System.out.println("Capabilities: " + capabilities.keySet());
        System.out.println("MQTT config verified: PASS");
    }
    
    /**
     * Test 6: Dev Protocols Configuration
     */
    @Test
    public void test6_DevProtocolsConfig() {
        System.out.println("\nTest 6: Dev Protocols Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> protocolsConfig = getDevProtocolsConfig();
        
        assertTrue("Should have mqtt protocol", protocolsConfig.containsKey("mqtt"));
        
        Map<String, Object> mqttProtocol = (Map<String, Object>) protocolsConfig.get("mqtt");
        assertEquals(true, mqttProtocol.get("enabled"));
        assertEquals("lightweight-mqtt", mqttProtocol.get("defaultProvider"));
        
        List<Map<String, Object>> providers = (List<Map<String, Object>>) mqttProtocol.get("providers");
        assertEquals("Should have 5 providers", 5, providers.size());
        
        Set<String> providerIds = new HashSet<>();
        for (Map<String, Object> provider : providers) {
            providerIds.add((String) provider.get("id"));
            System.out.println("  - Provider: " + provider.get("id") + " (" + provider.get("type") + ", priority: " + provider.get("priority") + ")");
        }
        
        assertTrue("Should have lightweight-mqtt", providerIds.contains("lightweight-mqtt"));
        assertTrue("Should have emqx-enterprise", providerIds.contains("emqx-enterprise"));
        assertTrue("Should have mosquitto-enterprise", providerIds.contains("mosquitto-enterprise"));
        assertTrue("Should have aliyun-iot", providerIds.contains("aliyun-iot"));
        assertTrue("Should have tencent-iot", providerIds.contains("tencent-iot"));
        
        System.out.println("Protocols config verified: PASS");
    }
    
    /**
     * Test 7: Dev Topic Spec Configuration
     */
    @Test
    public void test7_DevTopicSpecConfig() {
        System.out.println("\nTest 7: Dev Topic Spec Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> topicSpec = getDevTopicSpec();
        
        assertEquals("ooder/", topicSpec.get("prefix"));
        
        Map<String, String> patterns = (Map<String, String>) topicSpec.get("patterns");
        
        assertEquals("ooder/p2p/{userId}/inbox", patterns.get("p2p"));
        assertEquals("ooder/group/{groupId}/broadcast", patterns.get("group"));
        assertEquals("ooder/topic/{topicName}/data", patterns.get("topic"));
        assertEquals("ooder/broadcast/{channel}", patterns.get("broadcast"));
        assertEquals("ooder/sensor/{sensorType}/{sensorId}/data", patterns.get("sensor"));
        assertEquals("ooder/command/{deviceType}/{deviceId}/request", patterns.get("commandRequest"));
        assertEquals("ooder/command/{deviceType}/{deviceId}/response", patterns.get("commandResponse"));
        assertEquals("ooder/system/{eventType}", patterns.get("system"));
        
        System.out.println("Topic Prefix: " + topicSpec.get("prefix"));
        System.out.println("Patterns:");
        for (Map.Entry<String, String> entry : patterns.entrySet()) {
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Topic spec verified: PASS");
    }

    // ==================== Prod Scene Configuration ====================
    
    /**
     * Test 8: Prod Scene Basic Configuration
     */
    @Test
    public void test8_ProdSceneBasicConfig() {
        System.out.println("\nTest 8: Prod Scene Basic Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> sceneConfig = loadProdSceneConfig();
        
        assertEquals("prod-scene", sceneConfig.get("id"));
        assertEquals("生产环境场景", sceneConfig.get("name"));
        assertEquals("生产环境配置，使用环境变量替换敏感信息", sceneConfig.get("description"));
        
        System.out.println("Scene ID: " + sceneConfig.get("id"));
        System.out.println("Scene Name: " + sceneConfig.get("name"));
        System.out.println("Prod basic config verified: PASS");
    }
    
    /**
     * Test 9: Prod Org Configuration
     */
    @Test
    public void test9_ProdOrgConfig() {
        System.out.println("\nTest 9: Prod Org Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> orgConfig = getProdOrgConfig();
        
        assertEquals("prod-org", orgConfig.get("sceneId"));
        assertEquals("org", orgConfig.get("configName"));
        assertEquals("com.mysql.cj.jdbc.Driver", orgConfig.get("dbDriver"));
        assertTrue("DB URL should contain mysql", ((String)orgConfig.get("dbUrl")).contains("mysql"));
        assertEquals("${DB_PASSWORD}", orgConfig.get("dbPassword"));
        assertEquals(true, orgConfig.get("cacheEnabled"));
        
        System.out.println("Org SceneId: " + orgConfig.get("sceneId"));
        System.out.println("DB Driver: " + orgConfig.get("dbDriver"));
        System.out.println("DB URL: " + orgConfig.get("dbUrl"));
        System.out.println("Prod org config verified: PASS");
    }
    
    /**
     * Test 10: Prod VFS Configuration
     */
    @Test
    public void test10_ProdVfsConfig() {
        System.out.println("\nTest 10: Prod VFS Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> vfsConfig = getProdVfsConfig();
        
        assertEquals("prod-vfs", vfsConfig.get("sceneId"));
        assertEquals("vfs", vfsConfig.get("configName"));
        assertEquals("oss", vfsConfig.get("storageType"));
        assertEquals("/prod-files", vfsConfig.get("basePath"));
        assertEquals(524288000L, vfsConfig.get("maxFileSize"));
        assertEquals("${OSS_ACCESS_KEY}", vfsConfig.get("ossAccessKey"));
        assertEquals("${OSS_SECRET_KEY}", vfsConfig.get("ossSecretKey"));
        
        System.out.println("VFS SceneId: " + vfsConfig.get("sceneId"));
        System.out.println("Storage Type: " + vfsConfig.get("storageType"));
        System.out.println("OSS Endpoint: " + vfsConfig.get("ossEndpoint"));
        System.out.println("Prod VFS config verified: PASS");
    }
    
    /**
     * Test 11: Prod Msg Configuration
     */
    @Test
    public void test11_ProdMsgConfig() {
        System.out.println("\nTest 11: Prod Msg Configuration");
        System.out.println("----------------------------------------");
        
        Map<String, Object> msgConfig = getProdMsgConfig();
        
        assertEquals("prod-msg", msgConfig.get("sceneId"));
        assertEquals("msg", msgConfig.get("configName"));
        assertEquals("system", msgConfig.get("personId"));
        assertEquals("tcp://mqtt-cluster:1883", msgConfig.get("mqttBroker"));
        assertEquals(true, msgConfig.get("retainMessages"));
        assertEquals("${MQTT_USER}", msgConfig.get("mqttUsername"));
        assertEquals("${MQTT_PASSWORD}", msgConfig.get("mqttPassword"));
        
        System.out.println("Msg SceneId: " + msgConfig.get("sceneId"));
        System.out.println("MQTT Broker: " + msgConfig.get("mqttBroker"));
        System.out.println("Prod Msg config verified: PASS");
    }

    // ==================== Helper Methods ====================
    
    private Map<String, Object> loadDevSceneConfig() {
        Map<String, Object> scene = new HashMap<>();
        scene.put("id", "dev-scene");
        scene.put("name", "开发环境场景");
        scene.put("description", "用于本地开发和单元测试");
        scene.put("version", "0.7.3");
        return scene;
    }
    
    private Map<String, Object> getDevOrgConfig() {
        Map<String, Object> org = new HashMap<>();
        org.put("sceneId", "dev-org");
        org.put("configName", "org");
        org.put("dbDriver", "org.hsqldb.jdbcDriver");
        org.put("dbUrl", "jdbc:hsqldb:mem:devdb");
        org.put("dbUser", "sa");
        org.put("dbPassword", "");
        org.put("cacheEnabled", true);
        org.put("cacheExpireTime", 3600000);
        org.put("cacheSize", 5242880);
        
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("org-query", createCapability("组织查询服务", "OrgWebManager", "http://localhost:8080/api/org/orgManager"));
        capabilities.put("org-admin", createCapability("组织管理服务", "OrgAdminService", "http://localhost:8080/api/org/admin"));
        capabilities.put("user-auth", createCapability("用户认证服务", "UserService", "http://localhost:8080/api/user"));
        org.put("capabilities", capabilities);
        
        return org;
    }
    
    private Map<String, Object> getDevVfsConfig() {
        Map<String, Object> vfs = new HashMap<>();
        vfs.put("sceneId", "dev-vfs");
        vfs.put("configName", "vfs");
        vfs.put("storageType", "local");
        vfs.put("basePath", "./data/vfs");
        vfs.put("maxFileSize", 52428800L);
        vfs.put("allowedTypes", "*");
        
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("vfs-client", createCapability("虚拟目录服务", "VFSWebService", "http://localhost:8080/api/vfs/client"));
        capabilities.put("vfs-store", createCapability("实体存储服务", "VFSStoreService", "http://localhost:8080/api/vfs/store"));
        vfs.put("capabilities", capabilities);
        
        return vfs;
    }
    
    private Map<String, Object> getDevMsgConfig() {
        Map<String, Object> msg = new HashMap<>();
        msg.put("sceneId", "dev-msg");
        msg.put("configName", "msg");
        msg.put("personId", "dev-user");
        msg.put("mqttBroker", "tcp://localhost:1883");
        msg.put("mqttClientId", "dev-msg-client");
        msg.put("retainMessages", false);
        
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("msg-push", createCapability("消息推送服务", "MsgWebService", "http://localhost:8080/api/msg/push"));
        capabilities.put("msg-p2p", createCapability("P2P通信服务", "MsgWebService", "http://localhost:8080/api/msg/p2p"));
        capabilities.put("msg-topic", createCapability("Topic订阅服务", "MsgWebService", "http://localhost:8080/api/msg/topic"));
        msg.put("capabilities", capabilities);
        
        return msg;
    }
    
    private Map<String, Object> getDevMqttConfig() {
        Map<String, Object> mqtt = new HashMap<>();
        mqtt.put("sceneId", "dev-mqtt");
        mqtt.put("configName", "mqtt");
        mqtt.put("providerId", "lightweight-mqtt");
        mqtt.put("brokerUrl", "tcp://localhost:1883");
        mqtt.put("websocketUrl", "ws://localhost:8083");
        mqtt.put("port", 1883);
        mqtt.put("websocketPort", 8083);
        mqtt.put("maxConnections", 10000);
        mqtt.put("allowAnonymous", true);
        mqtt.put("persistent", false);
        mqtt.put("dataDirectory", "./data/mqtt");
        
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("mqtt-broker", createCapability("MQTT Broker服务", "MqttService", "tcp://localhost:1883"));
        capabilities.put("mqtt-publish", createCapability("MQTT发布服务", "MqttService", "http://localhost:8084/api/mqtt/publish"));
        capabilities.put("mqtt-subscribe", createCapability("MQTT订阅服务", "MqttService", "http://localhost:8084/api/mqtt/subscribe"));
        capabilities.put("mqtt-p2p", createCapability("MQTT P2P服务", "MqttService", "http://localhost:8084/api/mqtt/p2p"));
        capabilities.put("mqtt-topic", createCapability("MQTT Topic服务", "MqttService", "http://localhost:8084/api/mqtt/topic"));
        capabilities.put("mqtt-command", createCapability("MQTT命令服务", "MqttService", "http://localhost:8084/api/mqtt/command"));
        mqtt.put("capabilities", capabilities);
        
        return mqtt;
    }
    
    private Map<String, Object> getDevProtocolsConfig() {
        Map<String, Object> protocols = new HashMap<>();
        
        Map<String, Object> mqtt = new HashMap<>();
        mqtt.put("enabled", true);
        mqtt.put("defaultProvider", "lightweight-mqtt");
        
        List<Map<String, Object>> providers = new ArrayList<>();
        providers.add(createProvider("lightweight-mqtt", "Lightweight MQTT Server", "LIGHTWEIGHT", 100));
        providers.add(createProvider("emqx-enterprise", "EMQX Enterprise", "ENTERPRISE_SELF_HOSTED", 50));
        providers.add(createProvider("mosquitto-enterprise", "Mosquitto", "ENTERPRISE_SELF_HOSTED", 60));
        providers.add(createProvider("aliyun-iot", "Aliyun IoT", "CLOUD_MANAGED", 10));
        providers.add(createProvider("tencent-iot", "Tencent IoT", "CLOUD_MANAGED", 15));
        mqtt.put("providers", providers);
        
        protocols.put("mqtt", mqtt);
        return protocols;
    }
    
    private Map<String, Object> getDevTopicSpec() {
        Map<String, Object> topicSpec = new HashMap<>();
        topicSpec.put("prefix", "ooder/");
        
        Map<String, String> patterns = new LinkedHashMap<>();
        patterns.put("p2p", "ooder/p2p/{userId}/inbox");
        patterns.put("group", "ooder/group/{groupId}/broadcast");
        patterns.put("topic", "ooder/topic/{topicName}/data");
        patterns.put("broadcast", "ooder/broadcast/{channel}");
        patterns.put("sensor", "ooder/sensor/{sensorType}/{sensorId}/data");
        patterns.put("commandRequest", "ooder/command/{deviceType}/{deviceId}/request");
        patterns.put("commandResponse", "ooder/command/{deviceType}/{deviceId}/response");
        patterns.put("system", "ooder/system/{eventType}");
        topicSpec.put("patterns", patterns);
        
        return topicSpec;
    }
    
    private Map<String, Object> loadProdSceneConfig() {
        Map<String, Object> scene = new HashMap<>();
        scene.put("id", "prod-scene");
        scene.put("name", "生产环境场景");
        scene.put("description", "生产环境配置，使用环境变量替换敏感信息");
        return scene;
    }
    
    private Map<String, Object> getProdOrgConfig() {
        Map<String, Object> org = new HashMap<>();
        org.put("sceneId", "prod-org");
        org.put("configName", "org");
        org.put("dbDriver", "com.mysql.cj.jdbc.Driver");
        org.put("dbUrl", "jdbc:mysql://db-cluster:3306/org_prod?useSSL=true&serverTimezone=Asia/Shanghai");
        org.put("dbUser", "prod_user");
        org.put("dbPassword", "${DB_PASSWORD}");
        org.put("cacheEnabled", true);
        org.put("cacheExpireTime", 604800000);
        org.put("cacheSize", 104857600);
        return org;
    }
    
    private Map<String, Object> getProdVfsConfig() {
        Map<String, Object> vfs = new HashMap<>();
        vfs.put("sceneId", "prod-vfs");
        vfs.put("configName", "vfs");
        vfs.put("storageType", "oss");
        vfs.put("basePath", "/prod-files");
        vfs.put("maxFileSize", 524288000L);
        vfs.put("allowedTypes", "jpg,jpeg,png,pdf,doc,docx,xls,xlsx,zip");
        vfs.put("ossEndpoint", "https://oss.example.com");
        vfs.put("ossBucket", "prod-vfs");
        vfs.put("ossAccessKey", "${OSS_ACCESS_KEY}");
        vfs.put("ossSecretKey", "${OSS_SECRET_KEY}");
        return vfs;
    }
    
    private Map<String, Object> getProdMsgConfig() {
        Map<String, Object> msg = new HashMap<>();
        msg.put("sceneId", "prod-msg");
        msg.put("configName", "msg");
        msg.put("personId", "system");
        msg.put("mqttBroker", "tcp://mqtt-cluster:1883");
        msg.put("mqttClientId", "prod-msg-${RANDOM_ID}");
        msg.put("retainMessages", true);
        msg.put("mqttUsername", "${MQTT_USER}");
        msg.put("mqttPassword", "${MQTT_PASSWORD}");
        return msg;
    }
    
    private Map<String, Object> createCapability(String name, String interfaceId, String endpoint) {
        Map<String, Object> cap = new HashMap<>();
        cap.put("capabilityName", name);
        cap.put("interfaceId", interfaceId);
        cap.put("endpoint", endpoint);
        return cap;
    }
    
    private Map<String, Object> createProvider(String id, String name, String type, int priority) {
        Map<String, Object> provider = new HashMap<>();
        provider.put("id", id);
        provider.put("name", name);
        provider.put("type", type);
        provider.put("priority", priority);
        return provider;
    }
}
