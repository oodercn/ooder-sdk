package net.ooder.scene.core.activation.executor;

import net.ooder.scene.core.activation.model.ActivationProcess;
import net.ooder.scene.core.spi.ActivationStepExecutor;
import net.ooder.scene.core.template.ActivationStepConfig;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GetKeyExecutor 单元测试
 */
public class GetKeyExecutorTest {

    private GetKeyExecutor executor;

    @BeforeEach
    public void setUp() {
        executor = new GetKeyExecutor();
    }

    @Test
    public void testGetStepType() {
        assertEquals("GET_KEY", executor.getStepType());
    }

    @Test
    public void testCanExecuteWithUpperCase() {
        ActivationStepConfig config = new ActivationStepConfig();
        config.setStepType("GET_KEY");
        
        assertTrue(executor.canExecute(config));
    }

    @Test
    public void testCanExecuteWithLowerCase() {
        ActivationStepConfig config = new ActivationStepConfig();
        config.setStepType("get-key");
        
        assertTrue(executor.canExecute(config));
    }

    @Test
    public void testCannotExecuteOtherType() {
        ActivationStepConfig config = new ActivationStepConfig();
        config.setStepType("CONFIRM_JOIN");
        
        assertFalse(executor.canExecute(config));
    }

    @Test
    public void testExecuteWithDefaultConfig() {
        ActivationStepConfig stepConfig = createStepConfig("step-1", null);
        ActivationProcess process = createProcess("scene-1", "user-1", "role-1");
        Map<String, Object> context = new HashMap<>();
        
        ActivationStepExecutor.StepResult result = executor.execute(stepConfig, process, context);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertNotNull(result.getData().get("keyId"));
        assertNotNull(result.getData().get("key"));
        assertTrue(result.getData().get("key").toString().startsWith("SK_"));
        assertEquals("ACCESS", result.getData().get("keyType"));
    }

    @Test
    public void testExecuteWithCustomConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("keyType", "ADMIN");
        config.put("keyLength", 16);
        config.put("validityMs", 86400000L);
        
        ActivationStepConfig stepConfig = createStepConfig("step-1", config);
        ActivationProcess process = createProcess("scene-1", "user-1", "role-1");
        Map<String, Object> context = new HashMap<>();
        
        ActivationStepExecutor.StepResult result = executor.execute(stepConfig, process, context);
        
        assertTrue(result.isSuccess());
        assertEquals("ADMIN", result.getData().get("keyType"));
        
        String key = (String) result.getData().get("key");
        assertTrue(key.startsWith("SK_"));
        assertTrue(key.length() > 3);
    }

    @Test
    public void testExecuteWithPermissions() {
        Map<String, Object> config = new HashMap<>();
        config.put("keyType", "ACCESS");
        config.put("permissions", Arrays.asList("read", "write", "delete"));
        
        ActivationStepConfig stepConfig = createStepConfig("step-1", config);
        ActivationProcess process = createProcess("scene-1", "user-1", "role-1");
        Map<String, Object> context = new HashMap<>();
        
        ActivationStepExecutor.StepResult result = executor.execute(stepConfig, process, context);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData().get("keyId"));
        assertNotNull(result.getData().get("expiresAt"));
    }

    @Test
    public void testKeyUniqueness() {
        ActivationStepConfig stepConfig = createStepConfig("step-1", null);
        ActivationProcess process = createProcess("scene-1", "user-1", "role-1");
        Map<String, Object> context = new HashMap<>();
        
        ActivationStepExecutor.StepResult result1 = executor.execute(stepConfig, process, context);
        ActivationStepExecutor.StepResult result2 = executor.execute(stepConfig, process, context);
        
        assertNotEquals(result1.getData().get("key"), result2.getData().get("key"));
        assertNotEquals(result1.getData().get("keyId"), result2.getData().get("keyId"));
    }

    @Test
    public void testSceneKeyExpiry() {
        GetKeyExecutor.SceneKey sceneKey = new GetKeyExecutor.SceneKey();
        sceneKey.setExpiresAt(System.currentTimeMillis() - 1000);
        
        assertTrue(sceneKey.isExpired());
        
        sceneKey.setExpiresAt(System.currentTimeMillis() + 100000);
        assertFalse(sceneKey.isExpired());
    }

    @Test
    public void testSceneKeyProperties() {
        GetKeyExecutor.SceneKey sceneKey = new GetKeyExecutor.SceneKey();
        sceneKey.setKeyId("key-123");
        sceneKey.setKey("SK_test123");
        sceneKey.setKeyType("ACCESS");
        sceneKey.setSceneId("scene-1");
        sceneKey.setUserId("user-1");
        sceneKey.setRoleId("role-1");
        sceneKey.setCreatedAt(System.currentTimeMillis());
        sceneKey.setExpiresAt(System.currentTimeMillis() + 86400000);
        sceneKey.setActive(true);
        sceneKey.setPermissions(Arrays.asList("read", "write"));
        
        assertEquals("key-123", sceneKey.getKeyId());
        assertEquals("SK_test123", sceneKey.getKey());
        assertEquals("ACCESS", sceneKey.getKeyType());
        assertEquals("scene-1", sceneKey.getSceneId());
        assertEquals("user-1", sceneKey.getUserId());
        assertEquals("role-1", sceneKey.getRoleId());
        assertTrue(sceneKey.isActive());
        assertEquals(2, sceneKey.getPermissions().size());
    }

    private ActivationStepConfig createStepConfig(String stepId, Map<String, Object> config) {
        ActivationStepConfig stepConfig = new ActivationStepConfig();
        stepConfig.setStepId(stepId);
        stepConfig.setStepName("Get Key Step");
        stepConfig.setStepType("GET_KEY");
        stepConfig.setConfig(config);
        return stepConfig;
    }

    private ActivationProcess createProcess(String sceneId, String userId, String roleId) {
        ActivationProcess process = new ActivationProcess();
        process.setTemplateId(sceneId);
        process.setUserId(userId);
        process.setRoleId(roleId);
        return process;
    }
}
