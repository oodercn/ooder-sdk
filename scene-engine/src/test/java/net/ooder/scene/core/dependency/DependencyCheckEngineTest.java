package net.ooder.scene.core.dependency;

import net.ooder.scene.core.dependency.DependencyCheckEngine.*;
import net.ooder.scene.core.spi.DependencyChecker;
import net.ooder.scene.core.template.DependenciesConfig;
import net.ooder.scene.core.template.DependenciesConfig.DependencyItem;
import net.ooder.scene.core.template.SceneTemplate;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DependencyCheckEngine 单元测试
 */
public class DependencyCheckEngineTest {

    private DependencyCheckEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new DependencyCheckEngine();
        engine.registerChecker(new MockSkillDependencyChecker());
        engine.registerChecker(new MockServiceDependencyChecker());
    }

    @Test
    public void testRegisterChecker() {
        assertNotNull(engine);
    }

    @Test
    public void testCheckDependenciesNoDependencies() {
        SceneTemplate template = new SceneTemplate();
        template.setTemplateId("test-template");
        
        DependencyCheckResult result = engine.checkDependencies(template).join();
        
        assertTrue(result.isAllSatisfied());
        assertEquals("无依赖配置", result.getMessage());
    }

    @Test
    public void testCheckDependenciesAllSatisfied() {
        SceneTemplate template = createTemplateWithDependencies(
            Arrays.asList(
                createDependencyItem("skill-1", "Skill 1", "SKILL", true),
                createDependencyItem("service-1", "Service 1", "SERVICE", true)
            ),
            null
        );
        
        DependencyCheckResult result = engine.checkDependencies(template).join();
        
        assertTrue(result.isAllSatisfied());
        assertEquals(2, result.getRequiredCount());
        assertEquals(2, result.getSatisfiedCount());
    }

    @Test
    public void testCheckDependenciesUnsatisfied() {
        SceneTemplate template = createTemplateWithDependencies(
            Arrays.asList(
                createDependencyItem("skill-1", "Skill 1", "SKILL", true),
                createDependencyItem("skill-not-exist", "Skill Not Exist", "SKILL", true)
            ),
            null
        );
        
        DependencyCheckResult result = engine.checkDependencies(template).join();
        
        assertFalse(result.isAllSatisfied());
        assertEquals(1, result.getUnsatisfiedRequired().size());
    }

    @Test
    public void testCheckDependenciesOptional() {
        SceneTemplate template = createTemplateWithDependencies(
            Arrays.asList(
                createDependencyItem("skill-1", "Skill 1", "SKILL", true)
            ),
            Arrays.asList(
                createDependencyItem("skill-optional", "Optional Skill", "SKILL", false)
            )
        );
        
        DependencyCheckResult result = engine.checkDependencies(template).join();
        
        assertTrue(result.isAllSatisfied());
        assertNotNull(result.getOptionalChecks());
    }

    @Test
    public void testGetSolutions() {
        SceneTemplate template = createTemplateWithDependencies(
            Arrays.asList(
                createDependencyItem("skill-not-exist", "Skill Not Exist", "SKILL", true)
            ),
            null
        );
        
        List<DependencySolution> solutions = engine.getSolutions(template);
        
        assertEquals(1, solutions.size());
        assertEquals("skill-not-exist", solutions.get(0).getDependencyId());
    }

    @Test
    public void testCachedResult() {
        SceneTemplate template = new SceneTemplate();
        template.setTemplateId("test-template");
        
        engine.checkDependencies(template).join();
        
        DependencyCheckResult cached = engine.getCachedResult("test-template");
        assertNotNull(cached);
        
        engine.clearCache("test-template");
        assertNull(engine.getCachedResult("test-template"));
    }

    @Test
    public void testDependencyCheckListener() {
        final boolean[] listenerCalled = {false};
        
        engine.addListener((templateId, result) -> {
            listenerCalled[0] = true;
            assertEquals("test-template", templateId);
        });
        
        SceneTemplate template = new SceneTemplate();
        template.setTemplateId("test-template");
        engine.checkDependencies(template).join();
        
        assertTrue(listenerCalled[0]);
    }

    @Test
    public void testDependencyCheckItem() {
        DependencyCheckItem item = new DependencyCheckItem();
        item.setDependencyId("test-dep");
        item.setDependencyName("Test Dependency");
        item.setDependencyType("SKILL");
        item.setRequired(true);
        item.setSatisfied(true);
        item.setMessage("已满足");
        item.setStatus(DependencyCheckStatus.SATISFIED);
        
        assertEquals("test-dep", item.getDependencyId());
        assertTrue(item.isSatisfied());
        assertEquals(DependencyCheckStatus.SATISFIED, item.getStatus());
    }

    private SceneTemplate createTemplateWithDependencies(
            List<DependencyItem> required, 
            List<DependencyItem> optional) {
        SceneTemplate template = new SceneTemplate();
        template.setTemplateId("test-template-" + UUID.randomUUID().toString());
        
        DependenciesConfig config = new DependenciesConfig();
        config.setRequired(required);
        config.setOptional(optional);
        template.setDependencies(config);
        
        return template;
    }

    private DependencyItem createDependencyItem(
            String id, String name, String type, boolean autoInstall) {
        DependencyItem item = new DependencyItem();
        item.setDependencyId(id);
        item.setDependencyName(name);
        item.setDependencyType(type);
        item.setAutoInstall(autoInstall);
        return item;
    }

    /**
     * Mock Skill Dependency Checker
     */
    private static class MockSkillDependencyChecker implements DependencyChecker {
        @Override
        public String getDependencyType() {
            return "SKILL";
        }

        @Override
        public CheckResult check(DependencyItem dependency) {
            if (dependency.getDependencyId().contains("not-exist")) {
                return CheckResult.unsatisfied("技能未安装");
            }
            return CheckResult.satisfied("技能已安装");
        }

        @Override
        public HealthStatus healthCheck(String dependencyId) {
            if (dependencyId.contains("not-exist")) {
                return HealthStatus.UNHEALTHY;
            }
            return HealthStatus.HEALTHY;
        }
    }

    /**
     * Mock Service Dependency Checker
     */
    private static class MockServiceDependencyChecker implements DependencyChecker {
        @Override
        public String getDependencyType() {
            return "SERVICE";
        }

        @Override
        public CheckResult check(DependencyItem dependency) {
            return CheckResult.satisfied("服务可用");
        }

        @Override
        public HealthStatus healthCheck(String dependencyId) {
            return HealthStatus.HEALTHY;
        }
    }
}
