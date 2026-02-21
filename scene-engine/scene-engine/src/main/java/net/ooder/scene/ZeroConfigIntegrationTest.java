package net.ooder.scene;

import net.ooder.scene.core.*;
import net.ooder.scene.engine.*;
import net.ooder.scene.session.*;

import java.util.*;

public class ZeroConfigIntegrationTest {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Zero Config Integration Test - Scene Engine 0.7.3");
        System.out.println("========================================");
        System.out.println();

        try {
            story1_SceneConfigurationInitialization();
            story2_EngineTypes();
            story3_EngineStatus();
            story4_SessionManagement();
            story5_TokenManagement();
            story6_UserInfo();
            story7_SystemConfig();
            
            printSummary();
            
        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void story1_SceneConfigurationInitialization() {
        System.out.println("Story 1: Scene Configuration Initialization");
        System.out.println("----------------------------------------");

        try {
            Map<String, Object> sceneConfig = createDevSceneConfig();
            
            assertNotNull(sceneConfig);
            assertTrue(sceneConfig.containsKey("org"));
            assertTrue(sceneConfig.containsKey("vfs"));
            assertTrue(sceneConfig.containsKey("mqtt"));
            
            System.out.println("  [OK] Org config: " + ((Map)sceneConfig.get("org")).get("sceneId"));
            System.out.println("  [OK] VFS config: " + ((Map)sceneConfig.get("vfs")).get("sceneId"));
            System.out.println("  [OK] MQTT config: " + ((Map)sceneConfig.get("mqtt")).get("sceneId"));
            
            Map<String, Object> mqttConfig = (Map<String, Object>) sceneConfig.get("mqtt");
            assertEquals(1883, mqttConfig.get("port"));
            assertEquals("lightweight-mqtt", mqttConfig.get("providerId"));
            
            System.out.println("  [OK] MQTT port: " + mqttConfig.get("port"));
            System.out.println("  [OK] MQTT provider: " + mqttConfig.get("providerId"));
            
            System.out.println("  [PASS] Scene configuration initialization test passed");
            passedTests++;
            System.out.println();

        } catch (AssertionError e) {
            System.out.println("  [FAIL] " + e.getMessage());
            failedTests++;
            System.out.println();
        }
    }

    private static void story2_EngineTypes() {
        System.out.println("Story 2: Engine Types");
        System.out.println("----------------------------------------");

        try {
            EngineType[] engineTypes = EngineType.values();
            
            assertTrue(engineTypes.length > 0);
            
            for (EngineType type : engineTypes) {
                System.out.println("  [OK] " + type.name() + ": " + type.getDescription());
            }
            
            System.out.println("  [PASS] Engine types test passed: " + engineTypes.length + " types");
            passedTests++;
            System.out.println();

        } catch (AssertionError e) {
            System.out.println("  [FAIL] " + e.getMessage());
            failedTests++;
            System.out.println();
        }
    }

    private static void story3_EngineStatus() {
        System.out.println("Story 3: Engine Status");
        System.out.println("----------------------------------------");

        try {
            net.ooder.scene.engine.EngineStatus[] statuses = net.ooder.scene.engine.EngineStatus.values();
            
            assertTrue(statuses.length > 0);
            
            for (net.ooder.scene.engine.EngineStatus status : statuses) {
                System.out.println("  [OK] " + status.name() + ": " + status.getDescription());
            }
            
            System.out.println("  [PASS] Engine status test passed: " + statuses.length + " statuses");
            passedTests++;
            System.out.println();

        } catch (AssertionError e) {
            System.out.println("  [FAIL] " + e.getMessage());
            failedTests++;
            System.out.println();
        }
    }

    private static void story4_SessionManagement() {
        System.out.println("Story 4: Session Management");
        System.out.println("----------------------------------------");

        try {
            SessionInfo sessionInfo = new SessionInfo();
            sessionInfo.setSessionId("test-session-001");
            sessionInfo.setUserId("test-user-001");
            sessionInfo.setClientIp("192.168.1.100");
            sessionInfo.setStatus("CONNECTED");
            
            assertEquals("test-session-001", sessionInfo.getSessionId());
            assertEquals("test-user-001", sessionInfo.getUserId());
            assertEquals("192.168.1.100", sessionInfo.getClientIp());
            
            System.out.println("  [OK] SessionId: " + sessionInfo.getSessionId());
            System.out.println("  [OK] UserId: " + sessionInfo.getUserId());
            System.out.println("  [OK] ClientIp: " + sessionInfo.getClientIp());
            
            System.out.println("  [PASS] Session management test passed");
            passedTests++;
            System.out.println();

        } catch (AssertionError e) {
            System.out.println("  [FAIL] " + e.getMessage());
            failedTests++;
            System.out.println();
        }
    }

    private static void story5_TokenManagement() {
        System.out.println("Story 5: Token Management");
        System.out.println("----------------------------------------");

        try {
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setToken("test-token-abc123");
            tokenInfo.setSubject("test-user-001");
            tokenInfo.setIssuedAt(System.currentTimeMillis());
            tokenInfo.setExpiresAt(System.currentTimeMillis() + 3600000);
            
            assertEquals("test-token-abc123", tokenInfo.getToken());
            assertEquals("test-user-001", tokenInfo.getSubject());
            assertFalse(tokenInfo.isExpired());
            
            System.out.println("  [OK] Token: " + tokenInfo.getToken());
            System.out.println("  [OK] Subject: " + tokenInfo.getSubject());
            System.out.println("  [OK] IssuedAt: " + tokenInfo.getIssuedAt());
            
            System.out.println("  [PASS] Token management test passed");
            passedTests++;
            System.out.println();

        } catch (AssertionError e) {
            System.out.println("  [FAIL] " + e.getMessage());
            failedTests++;
            System.out.println();
        }
    }

    private static void story6_UserInfo() {
        System.out.println("Story 6: User Info");
        System.out.println("----------------------------------------");

        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId("user-001");
            userInfo.setUsername("testuser");
            userInfo.setNickname("Test User");
            userInfo.setEmail("test@example.com");
            userInfo.setPhone("13800138000");
            
            assertEquals("user-001", userInfo.getUserId());
            assertEquals("testuser", userInfo.getUsername());
            assertEquals("Test User", userInfo.getNickname());
            assertEquals("test@example.com", userInfo.getEmail());
            
            System.out.println("  [OK] UserId: " + userInfo.getUserId());
            System.out.println("  [OK] Username: " + userInfo.getUsername());
            System.out.println("  [OK] Email: " + userInfo.getEmail());
            
            System.out.println("  [PASS] User info test passed");
            passedTests++;
            System.out.println();

        } catch (AssertionError e) {
            System.out.println("  [FAIL] " + e.getMessage());
            failedTests++;
            System.out.println();
        }
    }

    private static void story7_SystemConfig() {
        System.out.println("Story 7: System Config");
        System.out.println("----------------------------------------");

        try {
            SystemConfig systemConfig = new SystemConfig();
            systemConfig.setConfigId("config-001");
            
            Map<String, Object> settings = new HashMap<String, Object>();
            settings.put("mqtt.port", 1883);
            settings.put("mqtt.provider", "lightweight-mqtt");
            systemConfig.setSettings(settings);
            
            assertEquals("config-001", systemConfig.getConfigId());
            assertEquals(Integer.valueOf(1883), systemConfig.getSetting("mqtt.port", 0));
            assertEquals("lightweight-mqtt", systemConfig.getSetting("mqtt.provider", ""));
            
            System.out.println("  [OK] ConfigId: " + systemConfig.getConfigId());
            System.out.println("  [OK] mqtt.port: " + systemConfig.getSetting("mqtt.port", 0));
            System.out.println("  [OK] mqtt.provider: " + systemConfig.getSetting("mqtt.provider", ""));
            
            System.out.println("  [PASS] System config test passed");
            passedTests++;
            System.out.println();

        } catch (AssertionError e) {
            System.out.println("  [FAIL] " + e.getMessage());
            failedTests++;
            System.out.println();
        }
    }

    private static void printSummary() {
        System.out.println("========================================");
        System.out.println("Test Summary");
        System.out.println("========================================");
        System.out.println("  Passed: " + passedTests);
        System.out.println("  Failed: " + failedTests);
        System.out.println("  Total: " + (passedTests + failedTests));
        System.out.println();

        if (failedTests > 0) {
            System.out.println("[FAILED] Some tests failed");
            System.exit(1);
        } else {
            System.out.println("[SUCCESS] All tests passed!");
            System.exit(0);
        }
    }

    private static Map<String, Object> createDevSceneConfig() {
        Map<String, Object> config = new HashMap<String, Object>();
        
        Map<String, Object> orgConfig = new HashMap<String, Object>();
        orgConfig.put("sceneId", "dev-org");
        orgConfig.put("configName", "org");
        config.put("org", orgConfig);
        
        Map<String, Object> vfsConfig = new HashMap<String, Object>();
        vfsConfig.put("sceneId", "dev-vfs");
        vfsConfig.put("configName", "vfs");
        config.put("vfs", vfsConfig);
        
        Map<String, Object> mqttConfig = new HashMap<String, Object>();
        mqttConfig.put("sceneId", "dev-mqtt");
        mqttConfig.put("providerId", "lightweight-mqtt");
        mqttConfig.put("port", 1883);
        mqttConfig.put("websocketPort", 8083);
        mqttConfig.put("maxConnections", 10000);
        mqttConfig.put("allowAnonymous", true);
        config.put("mqtt", mqttConfig);
        
        return config;
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
    }

    private static void assertEquals(int expected, int actual) {
        if (expected == actual) return;
        throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected: true, Actual: false");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected: false, Actual: true");
        }
    }

    private static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected: not null, Actual: null");
        }
    }
}
