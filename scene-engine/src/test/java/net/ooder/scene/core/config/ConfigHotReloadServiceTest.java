package net.ooder.scene.core.config;

import net.ooder.scene.core.config.ConfigHotReloadService.*;
import net.ooder.scene.core.lifecycle.SkillStateMachine;
import net.ooder.scene.core.template.SceneTemplate;
import net.ooder.scene.skill.install.SceneConfigLoader;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfigHotReloadService 单元测试
 */
public class ConfigHotReloadServiceTest {

    private ConfigHotReloadService service;
    private SceneTemplate template1;
    private SceneTemplate template2;

    @BeforeEach
    public void setUp() {
        SceneConfigLoader configLoader = new MockConfigLoader();
        SkillStateMachine stateMachine = new SkillStateMachine();
        service = new ConfigHotReloadService(configLoader, stateMachine);
        
        template1 = createTemplate("template-1", "Template 1");
        template2 = createTemplate("template-2", "Template 2");
    }

    @Test
    public void testRegisterConfig() {
        service.registerConfig("scene-1", "skill-1", template1);
        
        SceneTemplate current = service.getCurrentConfig("scene-1", "skill-1");
        
        assertNotNull(current);
        assertEquals("template-1", current.getTemplateId());
    }

    @Test
    public void testUpdateConfig() {
        service.registerConfig("scene-1", "skill-1", template1);
        
        SceneTemplate newTemplate = createTemplate("template-1", "Template 1 Updated");
        newTemplate.setDescription("Updated description");
        
        ConfigUpdateResult result = service.updateConfig("scene-1", "skill-1", newTemplate);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getOldVersion());
        assertNotNull(result.getNewVersion());
        assertFalse(result.getChanges().isEmpty());
    }

    @Test
    public void testUpdateConfigNotRegistered() {
        SceneTemplate newTemplate = createTemplate("template-1", "Template 1");
        
        ConfigUpdateResult result = service.updateConfig("scene-1", "skill-1", newTemplate);
        
        assertFalse(result.isSuccess());
        assertEquals("配置未注册", result.getMessage());
    }

    @Test
    public void testUpdateConfigNoChanges() {
        service.registerConfig("scene-1", "skill-1", template1);
        
        ConfigUpdateResult result = service.updateConfig("scene-1", "skill-1", template1);
        
        assertTrue(result.isSuccess());
        assertEquals("无配置变更", result.getMessage());
    }

    @Test
    public void testRollbackConfig() {
        service.registerConfig("scene-1", "skill-1", template1);
        
        ConfigUpdateResult updateResult = service.updateConfig("scene-1", "skill-1", template2);
        assertTrue(updateResult.isSuccess());
        
        String targetVersion = updateResult.getOldVersion();
        ConfigUpdateResult rollbackResult = service.rollbackConfig(
            "scene-1", "skill-1", targetVersion);
        
        assertTrue(rollbackResult.isSuccess());
        assertEquals("配置回滚成功", rollbackResult.getMessage());
    }

    @Test
    public void testRollbackConfigVersionNotFound() {
        service.registerConfig("scene-1", "skill-1", template1);
        
        ConfigUpdateResult result = service.rollbackConfig(
            "scene-1", "skill-1", "non-existent-version");
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("目标版本不存在"));
    }

    @Test
    public void testGetVersionHistory() {
        service.registerConfig("scene-1", "skill-1", template1);
        
        service.updateConfig("scene-1", "skill-1", template2);
        service.updateConfig("scene-1", "skill-1", template1);
        
        List<ConfigVersion> history = service.getVersionHistory("scene-1", "skill-1");
        
        assertNotNull(history);
        assertTrue(history.size() >= 1);
    }

    @Test
    public void testConfigChangeListener() {
        final boolean[] listenerCalled = {false};
        
        service.addListener(event -> {
            listenerCalled[0] = true;
            assertEquals("scene-1", event.getSceneId());
            assertEquals("skill-1", event.getSkillId());
            assertNotNull(event.getOldTemplate());
            assertNotNull(event.getNewTemplate());
        });
        
        service.registerConfig("scene-1", "skill-1", template1);
        
        SceneTemplate newTemplate = createTemplate("template-1", "Template 1 Updated");
        newTemplate.setDescription("Updated description");
        service.updateConfig("scene-1", "skill-1", newTemplate);
        
        assertTrue(listenerCalled[0]);
    }

    @Test
    public void testRemoveListener() {
        final int[] callCount = {0};
        
        ConfigChangeListener listener = event -> callCount[0]++;
        service.addListener(listener);
        
        service.registerConfig("scene-1", "skill-1", template1);
        
        service.removeListener(listener);
        
        SceneTemplate newTemplate = createTemplate("template-1", "Template 1 Updated");
        newTemplate.setDescription("Updated");
        service.updateConfig("scene-1", "skill-1", newTemplate);
        
        assertEquals(1, callCount[0]);
    }

    @Test
    public void testConfigChangeDetection() {
        ConfigChangeDetector detector = new ConfigChangeDetector();
        
        SceneTemplate oldTemplate = createTemplate("template-1", "Old Name");
        SceneTemplate newTemplate = createTemplate("template-1", "New Name");
        
        List<ConfigChange> changes = detector.detectChanges(oldTemplate, newTemplate);
        
        assertNotNull(changes);
    }

    @Test
    public void testConfigChangeSeverity() {
        ConfigChange change = new ConfigChange();
        change.setField("test");
        change.setOldValue("old");
        change.setNewValue("new");
        change.setType(ChangeType.MODIFY);
        change.setSeverity(ChangeSeverity.MINOR);
        
        assertEquals("test", change.getField());
        assertEquals(ChangeType.MODIFY, change.getType());
        assertEquals(ChangeSeverity.MINOR, change.getSeverity());
    }

    @Test
    public void testConfigVersion() {
        ConfigVersion version = new ConfigVersion();
        version.setVersionId("v1");
        version.setTemplate(template1);
        version.setTimestamp(System.currentTimeMillis());
        version.setPreviousVersion("v0");
        version.setSource("update");
        
        assertEquals("v1", version.getVersionId());
        assertEquals("update", version.getSource());
    }

    @Test
    public void testConfigUpdateResult() {
        ConfigUpdateResult result = new ConfigUpdateResult();
        result.setSceneId("scene-1");
        result.setSkillId("skill-1");
        result.setSuccess(true);
        result.setMessage("更新成功");
        result.setOldVersion("v1");
        result.setNewVersion("v2");
        
        assertEquals("scene-1", result.getSceneId());
        assertTrue(result.isSuccess());
        assertEquals("v1", result.getOldVersion());
        assertEquals("v2", result.getNewVersion());
    }

    private SceneTemplate createTemplate(String templateId, String templateName) {
        SceneTemplate template = new SceneTemplate();
        template.setTemplateId(templateId);
        template.setTemplateName(templateName);
        return template;
    }

    /**
     * Mock Config Loader
     */
    private static class MockConfigLoader extends SceneConfigLoader {
    }
}
