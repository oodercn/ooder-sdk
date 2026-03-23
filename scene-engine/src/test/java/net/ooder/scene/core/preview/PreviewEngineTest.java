package net.ooder.scene.core.preview;

import net.ooder.scene.core.dependency.DependencyCheckEngine;
import net.ooder.scene.core.preview.PreviewEngine.*;
import net.ooder.scene.core.template.*;
import net.ooder.scene.skill.install.SceneConfigLoader;
import net.ooder.scene.skill.model.SceneType;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PreviewEngine 单元测试
 */
public class PreviewEngineTest {

    private PreviewEngine engine;

    @BeforeEach
    public void setUp() {
        SceneConfigLoader configLoader = new MockSceneConfigLoader();
        DependencyCheckEngine depEngine = new DependencyCheckEngine();
        engine = new PreviewEngine(configLoader, depEngine);
    }

    @Test
    public void testPreviewBasicInfo() {
        MockSkillPackage skillPackage = new MockSkillPackage();
        skillPackage.setSkillId("test-skill");
        skillPackage.setMetadata(createBasicMetadata());
        
        PreviewResult result = engine.preview(skillPackage).join();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getBasicInfo());
        assertEquals("test-skill", result.getBasicInfo().getSkillId());
        assertEquals("Test Skill", result.getBasicInfo().getName());
        assertEquals("1.0.0", result.getBasicInfo().getVersion());
    }

    @Test
    public void testPreviewConfigInfo() {
        MockSkillPackage skillPackage = new MockSkillPackage();
        skillPackage.setSkillId("test-skill");
        skillPackage.setMetadata(createBasicMetadata());
        
        PreviewResult result = engine.preview(skillPackage).join();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getConfigInfo());
        assertTrue(result.getConfigInfo().isHasValidConfig());
        assertEquals("test-template", result.getConfigInfo().getTemplateId());
    }

    @Test
    public void testPreviewRoles() {
        MockSkillPackage skillPackage = new MockSkillPackage();
        skillPackage.setSkillId("test-skill");
        skillPackage.setMetadata(createBasicMetadata());
        
        PreviewResult result = engine.preview(skillPackage).join();
        
        List<RolePreview> roles = result.getConfigInfo().getRoles();
        assertNotNull(roles);
        assertEquals(2, roles.size());
        
        RolePreview managerRole = roles.stream()
            .filter(r -> "MANAGER".equals(r.getRoleId()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(managerRole);
        assertTrue(managerRole.isRequired());
        assertEquals(1, managerRole.getMinCount());
        assertEquals(1, managerRole.getMaxCount());
    }

    @Test
    public void testPreviewActivationSteps() {
        MockSkillPackage skillPackage = new MockSkillPackage();
        skillPackage.setSkillId("test-skill");
        skillPackage.setMetadata(createBasicMetadata());
        
        PreviewResult result = engine.preview(skillPackage).join();
        
        Map<String, List<ActivationStepPreview>> steps = 
            result.getConfigInfo().getActivationSteps();
        assertNotNull(steps);
        assertTrue(steps.containsKey("MANAGER"));
        assertTrue(steps.containsKey("EMPLOYEE"));
    }

    @Test
    public void testPreviewMenus() {
        MockSkillPackage skillPackage = new MockSkillPackage();
        skillPackage.setSkillId("test-skill");
        skillPackage.setMetadata(createBasicMetadata());
        
        PreviewResult result = engine.preview(skillPackage).join();
        
        Map<String, List<MenuPreview>> menus = result.getConfigInfo().getMenus();
        assertNotNull(menus);
        assertTrue(menus.containsKey("MANAGER"));
    }

    @Test
    public void testPreviewDependencyInfo() {
        MockSkillPackage skillPackage = new MockSkillPackage();
        skillPackage.setSkillId("test-skill");
        skillPackage.setMetadata(createBasicMetadata());
        
        PreviewResult result = engine.preview(skillPackage).join();
        
        assertNotNull(result.getDependencyInfo());
        assertFalse(result.getDependencyInfo().isHasDependencies());
    }

    @Test
    public void testPreviewWithNullSkillPackage() {
        PreviewResult result = engine.preview(null).join();
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    private Map<String, Object> createBasicMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", "Test Skill");
        metadata.put("displayName", "Test Skill Display");
        metadata.put("description", "A test skill for unit testing");
        metadata.put("version", "1.0.0");
        metadata.put("type", "SCENE");
        metadata.put("category", "biz");
        metadata.put("author", "Test Author");
        metadata.put("tags", Arrays.asList("test", "unit-test"));
        return metadata;
    }

    /**
     * Mock Skill Package
     */
    private static class MockSkillPackage {
        private String skillId;
        private Map<String, Object> metadata;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    /**
     * Mock Scene Config Loader
     */
    private static class MockSceneConfigLoader extends SceneConfigLoader {
        
        @Override
        public SceneTemplate loadSceneConfig(String skillId, Object skillPackage) {
            SceneTemplate template = new SceneTemplate();
            template.setTemplateId("test-template");
            template.setTemplateName("Test Template");
            template.setSceneType(SceneType.TRIGGER);
            template.setVisibility("internal");
            template.setCategory("biz");
            
            List<RoleConfig> roles = new ArrayList<>();
            
            RoleConfig manager = new RoleConfig();
            manager.setRoleId("MANAGER");
            manager.setRoleName("管理员");
            manager.setDescription("场景管理员");
            manager.setRequired(true);
            manager.setMinCount(1);
            manager.setMaxCount(1);
            manager.setPermissions(Arrays.asList("scene:manage", "report:view"));
            roles.add(manager);
            
            RoleConfig employee = new RoleConfig();
            employee.setRoleId("EMPLOYEE");
            employee.setRoleName("员工");
            employee.setDescription("普通员工");
            employee.setRequired(true);
            employee.setMinCount(1);
            employee.setMaxCount(100);
            employee.setPermissions(Arrays.asList("report:submit"));
            roles.add(employee);
            
            template.setRoles(roles);
            
            Map<String, List<ActivationStepConfig>> activationSteps = new HashMap<>();
            
            List<ActivationStepConfig> managerSteps = new ArrayList<>();
            ActivationStepConfig step1 = new ActivationStepConfig();
            step1.setStepId("confirm-participants");
            step1.setStepName("确认参与者");
            step1.setStepType("CONFIRM_PARTICIPANTS");
            step1.setRequired(true);
            step1.setOrder(1);
            managerSteps.add(step1);
            activationSteps.put("MANAGER", managerSteps);
            
            List<ActivationStepConfig> employeeSteps = new ArrayList<>();
            ActivationStepConfig empStep1 = new ActivationStepConfig();
            empStep1.setStepId("confirm-join");
            empStep1.setStepName("确认加入");
            empStep1.setStepType("CONFIRM_JOIN");
            empStep1.setRequired(true);
            empStep1.setOrder(1);
            employeeSteps.add(empStep1);
            activationSteps.put("EMPLOYEE", employeeSteps);
            
            template.setActivationSteps(activationSteps);
            
            Map<String, List<net.ooder.scene.ui.MenuConfig>> menus = new HashMap<>();
            List<net.ooder.scene.ui.MenuConfig> managerMenus = new ArrayList<>();
            net.ooder.scene.ui.MenuConfig menu1 = new net.ooder.scene.ui.MenuConfig();
            menu1.setMenuId("dashboard");
            menu1.setTitle("仪表盘");
            menu1.setPath("/dashboard");
            menu1.setOrder(1);
            managerMenus.add(menu1);
            menus.put("MANAGER", managerMenus);
            
            template.setMenus(menus);
            
            return template;
        }
    }
}
