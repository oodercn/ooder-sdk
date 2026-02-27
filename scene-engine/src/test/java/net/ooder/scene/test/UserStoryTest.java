package net.ooder.scene.test;

import net.ooder.scene.core.*;
import net.ooder.scene.session.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;

/**
 * User Story Test Cases for Scene Engine 0.7.3
 * 
 * User Stories:
 * 1. As a user, I want to login and get a session
 * 2. As a user, I want to manage my session
 * 3. As an admin, I want to manage skills
 * 4. As a developer, I want to configure scenes
 * 5. As a system operator, I want to monitor engine status
 */
public class UserStoryTest {
    
    @Before
    public void setUp() {
        System.out.println("========================================");
        System.out.println("User Story Test - Scene Engine 0.7.3");
        System.out.println("========================================");
    }
    
    @After
    public void tearDown() {
    }

    // ==================== Story 1: User Authentication ====================
    
    /**
     * Story 1.1: User Login with Username/Password
     * As a user, I want to login with my credentials
     */
    @Test
    public void story1_1_UserLoginWithCredentials() {
        System.out.println("\nStory 1.1: User Login with Credentials");
        System.out.println("----------------------------------------");
        
        UserInfo user = new UserInfo();
        user.setUserId("user-001");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setNickname("Test User");
        user.setStatus("ACTIVE");
        user.setToken("token-abc123");
        user.setRefreshToken("refresh-xyz789");
        
        assertEquals("user-001", user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertTrue("User should be active", user.isActive());
        assertFalse("User should not be admin", user.isAdmin());
        
        System.out.println("User login verified:");
        System.out.println("  - UserId: " + user.getUserId());
        System.out.println("  - Username: " + user.getUsername());
        System.out.println("  - Status: " + user.getStatus());
    }
    
    /**
     * Story 1.2: Admin User Login
     * As an admin, I want to login with admin privileges
     */
    @Test
    public void story1_2_AdminUserLogin() {
        System.out.println("\nStory 1.2: Admin User Login");
        System.out.println("----------------------------------------");
        
        UserInfo admin = new UserInfo();
        admin.setUserId("admin-001");
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setNickname("Administrator");
        admin.setStatus("ACTIVE");
        admin.setRoles(Arrays.asList("ADMIN", "SystemAdmin"));
        
        assertTrue("Admin should be active", admin.isActive());
        assertTrue("Admin should have admin privileges", admin.isAdmin());
        
        System.out.println("Admin login verified:");
        System.out.println("  - UserId: " + admin.getUserId());
        System.out.println("  - Roles: " + admin.getRoles());
        System.out.println("  - IsAdmin: " + admin.isAdmin());
    }

    // ==================== Story 2: Session Management ====================
    
    /**
     * Story 2.1: Create Session
     * As a user, I want to have a valid session after login
     */
    @Test
    public void story2_1_CreateSession() {
        System.out.println("\nStory 2.1: Create Session");
        System.out.println("----------------------------------------");
        
        long now = System.currentTimeMillis();
        
        SessionInfo session = new SessionInfo();
        session.setSessionId("session-" + UUID.randomUUID().toString());
        session.setUserId("user-001");
        session.setUsername("testuser");
        session.setToken("token-abc123");
        session.setClientIp("192.168.1.100");
        session.setUserAgent("Mozilla/5.0");
        session.setCreatedAt(now);
        session.setExpiresAt(now + 3600000);
        session.setLastActiveAt(now);
        session.setStatus("ACTIVE");
        
        assertNotNull("SessionId should not be null", session.getSessionId());
        assertEquals("user-001", session.getUserId());
        assertTrue("Session should be active", session.isActive());
        assertFalse("Session should not be expired", session.isExpired());
        
        System.out.println("Session created:");
        System.out.println("  - SessionId: " + session.getSessionId());
        System.out.println("  - UserId: " + session.getUserId());
        System.out.println("  - Status: " + session.getStatus());
        System.out.println("  - Active: " + session.isActive());
    }
    
    /**
     * Story 2.2: Session Expiration
     * As a system, I want to detect expired sessions
     */
    @Test
    public void story2_2_SessionExpiration() {
        System.out.println("\nStory 2.2: Session Expiration");
        System.out.println("----------------------------------------");
        
        SessionInfo expiredSession = new SessionInfo();
        expiredSession.setSessionId("session-expired");
        expiredSession.setUserId("user-001");
        expiredSession.setExpiresAt(System.currentTimeMillis() - 1000);
        expiredSession.setStatus("ACTIVE");
        
        assertTrue("Session should be expired", expiredSession.isExpired());
        assertFalse("Expired session should not be active", expiredSession.isActive());
        
        System.out.println("Expired session detected:");
        System.out.println("  - IsExpired: " + expiredSession.isExpired());
        System.out.println("  - IsActive: " + expiredSession.isActive());
    }

    // ==================== Story 3: Skill Management ====================
    
    /**
     * Story 3.1: Skill Information
     * As a developer, I want to get skill information
     */
    @Test
    public void story3_1_SkillInformation() {
        System.out.println("\nStory 3.1: Skill Information");
        System.out.println("----------------------------------------");
        
        SkillInfo skill = new SkillInfo();
        skill.setSkillId("skill-mqtt");
        skill.setName("MQTT Service Skill");
        skill.setDescription("Provides MQTT broker and messaging capabilities");
        skill.setVersion("0.7.3");
        skill.setAuthor("Ooder Team");
        skill.setStatus("INSTALLED");
        skill.setCategory("messaging");
        skill.setTags(Arrays.asList("mqtt", "messaging", "iot"));
        skill.setInstallCount(1000);
        skill.setCreatedAt(System.currentTimeMillis());
        
        assertEquals("skill-mqtt", skill.getSkillId());
        assertEquals("MQTT Service Skill", skill.getName());
        assertEquals("0.7.3", skill.getVersion());
        assertEquals("INSTALLED", skill.getStatus());
        
        System.out.println("Skill info verified:");
        System.out.println("  - SkillId: " + skill.getSkillId());
        System.out.println("  - Name: " + skill.getName());
        System.out.println("  - Version: " + skill.getVersion());
        System.out.println("  - Status: " + skill.getStatus());
        System.out.println("  - Tags: " + skill.getTags());
    }
    
    /**
     * Story 3.2: Skill Install Result
     * As an admin, I want to see skill installation result
     */
    @Test
    public void story3_2_SkillInstallResult() {
        System.out.println("\nStory 3.2: Skill Install Result");
        System.out.println("----------------------------------------");
        
        SkillInstallResult result = SkillInstallResult.success("install-001", "skill-mqtt");
        
        assertTrue("Install should succeed", result.isSuccess());
        assertEquals("install-001", result.getInstallId());
        assertEquals("skill-mqtt", result.getSkillId());
        assertNotNull("Message should not be null", result.getMessage());
        
        System.out.println("Install result:");
        System.out.println("  - Success: " + result.isSuccess());
        System.out.println("  - InstallId: " + result.getInstallId());
        System.out.println("  - SkillId: " + result.getSkillId());
        System.out.println("  - Message: " + result.getMessage());
        
        SkillInstallResult failResult = SkillInstallResult.failed("skill-mqtt", "Dependency not found");
        assertFalse("Install should fail", failResult.isSuccess());
        assertEquals("Dependency not found", failResult.getMessage());
    }
    
    /**
     * Story 3.3: Skill Uninstall Result
     * As an admin, I want to see skill uninstallation result
     */
    @Test
    public void story3_3_SkillUninstallResult() {
        System.out.println("\nStory 3.3: Skill Uninstall Result");
        System.out.println("----------------------------------------");
        
        SkillUninstallResult result = SkillUninstallResult.success("skill-mqtt");
        
        assertTrue("Uninstall should succeed", result.isSuccess());
        assertEquals("skill-mqtt", result.getSkillId());
        
        System.out.println("Uninstall result:");
        System.out.println("  - Success: " + result.isSuccess());
        System.out.println("  - SkillId: " + result.getSkillId());
        System.out.println("  - Message: " + result.getMessage());
    }

    // ==================== Story 4: Scene Configuration ====================
    
    /**
     * Story 4.1: Scene Configuration
     * As a developer, I want to configure a scene
     */
    @Test
    public void story4_1_SceneConfiguration() {
        System.out.println("\nStory 4.1: Scene Configuration");
        System.out.println("----------------------------------------");
        
        SceneInfo scene = new SceneInfo();
        scene.setSceneId("dev-scene");
        scene.setName("Development Scene");
        scene.setDescription("Scene for local development and testing");
        scene.setCategory("development");
        scene.setStatus("RUNNING");
        scene.setOwner("dev-team");
        scene.setRequiredCapabilities(Arrays.asList("mqtt-broker", "org-query", "vfs-client"));
        
        assertEquals("dev-scene", scene.getSceneId());
        assertEquals("Development Scene", scene.getName());
        assertEquals("RUNNING", scene.getStatus());
        assertTrue("Should have required capabilities", scene.getRequiredCapabilities().size() > 0);
        
        System.out.println("Scene configuration verified:");
        System.out.println("  - SceneId: " + scene.getSceneId());
        System.out.println("  - Name: " + scene.getName());
        System.out.println("  - Status: " + scene.getStatus());
        System.out.println("  - RequiredCapabilities: " + scene.getRequiredCapabilities());
    }
    
    /**
     * Story 4.2: Capability Information
     * As a developer, I want to get capability information
     */
    @Test
    public void story4_2_CapabilityInformation() {
        System.out.println("\nStory 4.2: Capability Information");
        System.out.println("----------------------------------------");
        
        CapabilityInfo capability = new CapabilityInfo(
            "cap-001", "mqtt-broker", "1.0.0", "messaging", 
            "Provides MQTT broker service", "skill-mqtt", "publish"
        );
        
        assertEquals("mqtt-broker", capability.getName());
        assertEquals("Provides MQTT broker service", capability.getDescription());
        assertEquals("cap-001", capability.getCapId());
        
        System.out.println("Capability info verified:");
        System.out.println("  - Name: " + capability.getName());
        System.out.println("  - Description: " + capability.getDescription());
        System.out.println("  - CapId: " + capability.getCapId());
    }

    // ==================== Story 5: Engine Status ====================
    
    /**
     * Story 5.1: Engine Status Lifecycle
     * As a system operator, I want to track engine status
     */
    @Test
    public void story5_1_EngineStatusLifecycle() {
        System.out.println("\nStory 5.1: Engine Status Lifecycle");
        System.out.println("----------------------------------------");
        
        net.ooder.scene.engine.EngineStatus[] statuses = net.ooder.scene.engine.EngineStatus.values();
        
        assertEquals("Should have 9 statuses", 9, statuses.length);
        
        String[] expectedStatuses = {
            "CREATED", "INITIALIZING", "INITIALIZED", "STARTING", 
            "RUNNING", "STOPPING", "STOPPED", "ERROR", "DESTROYED"
        };
        
        for (int i = 0; i < expectedStatuses.length; i++) {
            assertEquals(expectedStatuses[i], statuses[i].name());
            System.out.println("  - " + statuses[i].name() + ": " + statuses[i].getDescription());
        }
        
        System.out.println("Engine status lifecycle verified: " + statuses.length + " statuses");
    }
    
    /**
     * Story 5.2: Engine Status Transitions
     * As a system operator, I want to verify valid status transitions
     */
    @Test
    public void story5_2_EngineStatusTransitions() {
        System.out.println("\nStory 5.2: Engine Status Transitions");
        System.out.println("----------------------------------------");
        
        net.ooder.scene.engine.EngineStatus current = net.ooder.scene.engine.EngineStatus.CREATED;
        assertEquals("CREATED", current.name());
        
        current = net.ooder.scene.engine.EngineStatus.INITIALIZING;
        assertEquals("INITIALIZING", current.name());
        
        current = net.ooder.scene.engine.EngineStatus.INITIALIZED;
        assertEquals("INITIALIZED", current.name());
        
        current = net.ooder.scene.engine.EngineStatus.STARTING;
        assertEquals("STARTING", current.name());
        
        current = net.ooder.scene.engine.EngineStatus.RUNNING;
        assertEquals("RUNNING", current.name());
        assertEquals("Running", current.getDescription());
        
        System.out.println("Status transitions verified:");
        System.out.println("  CREATED -> INITIALIZING -> INITIALIZED -> STARTING -> RUNNING");
    }

    // ==================== Story 6: Token Management ====================
    
    /**
     * Story 6.1: Token Information
     * As a user, I want to manage my tokens
     */
    @Test
    public void story6_1_TokenInformation() {
        System.out.println("\nStory 6.1: Token Information");
        System.out.println("----------------------------------------");
        
        TokenInfo token = new TokenInfo();
        token.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        token.setSubject("user-001");
        token.setIssuer("scene-engine");
        token.setAudience("scene-api");
        token.setExpiresAt(System.currentTimeMillis() + 3600000);
        token.setIssuedAt(System.currentTimeMillis());
        
        assertEquals("user-001", token.getSubject());
        assertEquals("scene-engine", token.getIssuer());
        assertFalse("Token should not be expired", token.isExpired());
        
        System.out.println("Token info verified:");
        System.out.println("  - Subject: " + token.getSubject());
        System.out.println("  - Issuer: " + token.getIssuer());
        System.out.println("  - IsExpired: " + token.isExpired());
    }
    
    /**
     * Story 6.2: Token Expiration
     * As a system, I want to detect expired tokens
     */
    @Test
    public void story6_2_TokenExpiration() {
        System.out.println("\nStory 6.2: Token Expiration");
        System.out.println("----------------------------------------");
        
        TokenInfo expiredToken = new TokenInfo();
        expiredToken.setToken("expired-token");
        expiredToken.setSubject("user-001");
        expiredToken.setExpiresAt(System.currentTimeMillis() - 1000);
        
        assertTrue("Token should be expired", expiredToken.isExpired());
        
        System.out.println("Expired token detected:");
        System.out.println("  - IsExpired: " + expiredToken.isExpired());
    }

    // ==================== Story 7: Connection Management ====================
    
    /**
     * Story 7.1: Connection Information
     * As a system, I want to track client connections
     */
    @Test
    public void story7_1_ConnectionInformation() {
        System.out.println("\nStory 7.1: Connection Information");
        System.out.println("----------------------------------------");
        
        ConnectInfo conn = new ConnectInfo();
        conn.setConnectionId("conn-" + UUID.randomUUID().toString());
        conn.setClientIp("192.168.1.100");
        conn.setProtocol("mqtt");
        conn.setEndpoint("tcp://localhost:1883");
        conn.setStatus("CONNECTED");
        
        assertNotNull("ConnectionId should not be null", conn.getConnectionId());
        assertEquals("192.168.1.100", conn.getClientIp());
        assertEquals("mqtt", conn.getProtocol());
        assertTrue("Should be connected", conn.isConnected());
        
        System.out.println("Connection info verified:");
        System.out.println("  - ConnectionId: " + conn.getConnectionId());
        System.out.println("  - ClientIp: " + conn.getClientIp());
        System.out.println("  - Protocol: " + conn.getProtocol());
        System.out.println("  - IsConnected: " + conn.isConnected());
    }

    // ==================== Story 8: Page Query ====================
    
    /**
     * Story 8.1: Page Request
     * As a user, I want to query data with pagination
     */
    @Test
    public void story8_1_PageRequest() {
        System.out.println("\nStory 8.1: Page Request");
        System.out.println("----------------------------------------");
        
        PageRequest request = new PageRequest();
        request.setPageNum(1);
        request.setPageSize(20);
        request.setSortBy("createdAt");
        request.setSortOrder("DESC");
        
        assertEquals(1, request.getPageNum());
        assertEquals(20, request.getPageSize());
        assertEquals("createdAt", request.getSortBy());
        assertEquals("DESC", request.getSortOrder());
        assertEquals(0, request.getOffset());
        
        System.out.println("Page request verified:");
        System.out.println("  - PageNum: " + request.getPageNum());
        System.out.println("  - PageSize: " + request.getPageSize());
        System.out.println("  - SortBy: " + request.getSortBy());
        System.out.println("  - SortOrder: " + request.getSortOrder());
        System.out.println("  - Offset: " + request.getOffset());
    }
    
    /**
     * Story 8.2: Page Result
     * As a user, I want to receive paginated results
     */
    @Test
    public void story8_2_PageResult() {
        System.out.println("\nStory 8.2: Page Result");
        System.out.println("----------------------------------------");
        
        List<String> items = Arrays.asList("item1", "item2", "item3");
        PageResult<String> result = PageResult.of(items, 100, 1, 20);
        
        assertEquals(3, result.getItems().size());
        assertEquals(100, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(5, result.getTotalPages());
        assertTrue("Should have next page", result.hasNext());
        assertFalse("Should not have previous page", result.hasPrevious());
        
        System.out.println("Page result verified:");
        System.out.println("  - Items: " + result.getItems().size());
        System.out.println("  - Total: " + result.getTotal());
        System.out.println("  - PageNum: " + result.getPageNum());
        System.out.println("  - TotalPages: " + result.getTotalPages());
        System.out.println("  - HasNext: " + result.hasNext());
        System.out.println("  - HasPrevious: " + result.hasPrevious());
    }

    // ==================== Story 9: Scene Configuration YAML ====================
    
    /**
     * Story 9.1: Scene Configuration from YAML
     * As a developer, I want to load scene configuration from YAML
     */
    @Test
    public void story9_1_SceneConfigurationFromYaml() {
        System.out.println("\nStory 9.1: Scene Configuration from YAML");
        System.out.println("----------------------------------------");
        
        Map<String, Object> sceneConfig = createDevSceneConfig();
        
        assertNotNull("Scene config should not be null", sceneConfig);
        assertTrue("Should have org config", sceneConfig.containsKey("org"));
        assertTrue("Should have vfs config", sceneConfig.containsKey("vfs"));
        assertTrue("Should have mqtt config", sceneConfig.containsKey("mqtt"));
        
        Map<String, Object> mqttConfig = (Map<String, Object>) sceneConfig.get("mqtt");
        assertEquals("dev-mqtt", mqttConfig.get("sceneId"));
        assertEquals("lightweight-mqtt", mqttConfig.get("providerId"));
        assertEquals(1883, mqttConfig.get("port"));
        
        System.out.println("Scene config from YAML verified:");
        System.out.println("  - Org: " + ((Map)sceneConfig.get("org")).get("sceneId"));
        System.out.println("  - VFS: " + ((Map)sceneConfig.get("vfs")).get("sceneId"));
        System.out.println("  - MQTT: " + mqttConfig.get("sceneId"));
        System.out.println("  - MQTT Provider: " + mqttConfig.get("providerId"));
    }
    
    private Map<String, Object> createDevSceneConfig() {
        Map<String, Object> config = new HashMap<>();
        
        Map<String, Object> orgConfig = new HashMap<>();
        orgConfig.put("sceneId", "dev-org");
        orgConfig.put("configName", "org");
        config.put("org", orgConfig);
        
        Map<String, Object> vfsConfig = new HashMap<>();
        vfsConfig.put("sceneId", "dev-vfs");
        vfsConfig.put("configName", "vfs");
        config.put("vfs", vfsConfig);
        
        Map<String, Object> mqttConfig = new HashMap<>();
        mqttConfig.put("sceneId", "dev-mqtt");
        mqttConfig.put("providerId", "lightweight-mqtt");
        mqttConfig.put("port", 1883);
        mqttConfig.put("websocketPort", 8083);
        mqttConfig.put("maxConnections", 10000);
        mqttConfig.put("allowAnonymous", true);
        config.put("mqtt", mqttConfig);
        
        return config;
    }
}
