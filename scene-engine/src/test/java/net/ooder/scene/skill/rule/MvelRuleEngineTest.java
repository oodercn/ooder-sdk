package net.ooder.scene.skill.rule;

import net.ooder.scene.skill.rule.impl.MvelRuleEngineImpl;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MVEL Rule Engine 单元测试
 */
public class MvelRuleEngineTest {

    private MvelRuleEngineImpl engine;

    @BeforeEach
    public void setUp() {
        engine = new MvelRuleEngineImpl();
    }

    @AfterEach
    public void tearDown() {
        engine.clearAllRules();
    }

    @Test
    public void testRuleTypeValues() {
        assertEquals(5, RuleType.values().length);
        assertNotNull(RuleType.DECISION);
        assertNotNull(RuleType.TRANSFORM);
        assertNotNull(RuleType.VALIDATION);
        assertNotNull(RuleType.ROUTING);
        assertNotNull(RuleType.FALLBACK);
    }

    @Test
    public void testRuleTypeFromCode() {
        assertEquals(RuleType.DECISION, RuleType.fromCode("decision"));
        assertEquals(RuleType.TRANSFORM, RuleType.fromCode("transform"));
        assertEquals(RuleType.VALIDATION, RuleType.fromCode("validation"));
        assertEquals(RuleType.ROUTING, RuleType.fromCode("routing"));
        assertEquals(RuleType.FALLBACK, RuleType.fromCode("fallback"));
        assertEquals(RuleType.DECISION, RuleType.fromCode("invalid"));
        assertEquals(RuleType.DECISION, RuleType.fromCode(null));
    }

    @Test
    public void testRuleScriptCreation() {
        RuleScript rule = new RuleScript();
        assertNotNull(rule);
        assertTrue(rule.isEnabled());
        assertEquals(0, rule.getPriority());
        assertTrue(rule.getCreatedAt() > 0);
    }

    @Test
    public void testRuleScriptSetters() {
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setName("Test Rule")
            .setType(RuleType.DECISION)
            .setCondition("query != null")
            .setAction("result = 'matched'")
            .setPriority(10)
            .setEnabled(true)
            .setCreatedBy("user-1")
            .setDescription("Test rule description");

        assertEquals("rule-1", rule.getRuleId());
        assertEquals("scene-1", rule.getSceneId());
        assertEquals("Test Rule", rule.getName());
        assertEquals(RuleType.DECISION, rule.getType());
        assertEquals("query != null", rule.getCondition());
        assertEquals("result = 'matched'", rule.getAction());
        assertEquals(10, rule.getPriority());
        assertTrue(rule.isEnabled());
        assertEquals("user-1", rule.getCreatedBy());
        assertEquals("Test rule description", rule.getDescription());
    }

    @Test
    public void testRuleScriptHasMethods() {
        RuleScript rule = new RuleScript();
        assertFalse(rule.hasCondition());
        assertFalse(rule.hasAction());

        rule.setCondition("x > 0");
        assertTrue(rule.hasCondition());

        rule.setAction("y = 1");
        assertTrue(rule.hasAction());
    }

    @Test
    public void testEngineInitialStatus() {
        assertEquals(0, engine.getRuleCount());
        assertFalse(engine.hasRule("non-existent"));
    }

    @Test
    public void testRegisterRule() {
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setName("Test Rule")
            .setType(RuleType.DECISION)
            .setAction("result = 'success'");

        engine.registerRule(rule);

        assertEquals(1, engine.getRuleCount());
        assertTrue(engine.hasRule("rule-1"));
        assertNotNull(engine.getRule("rule-1"));
    }

    @Test
    public void testRemoveRule() {
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setAction("result = 'success'");

        engine.registerRule(rule);
        assertTrue(engine.hasRule("rule-1"));

        engine.removeRule("rule-1");
        assertFalse(engine.hasRule("rule-1"));
        assertEquals(0, engine.getRuleCount());
    }

    @Test
    public void testExecuteScript() {
        Map<String, Object> context = new HashMap<>();
        context.put("x", 10);
        context.put("y", 20);

        Object result = engine.executeScript("x + y", context);
        assertEquals(30, result);
    }

    @Test
    public void testExecuteScriptWithString() {
        Map<String, Object> context = new HashMap<>();
        context.put("name", "test");

        Object result = engine.executeScript("name + '_suffix'", context);
        assertEquals("test_suffix", result);
    }

    @Test
    public void testExecuteScriptWithNull() {
        Object result = engine.executeScript(null, new HashMap<>());
        assertNotNull(result);
        assertTrue(result instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> errorResult = (Map<String, Object>) result;
        assertFalse((Boolean) errorResult.get("success"));
        assertNotNull(errorResult.get("error"));

        result = engine.executeScript("", new HashMap<>());
        assertNotNull(result);
        assertTrue(result instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> errorResult2 = (Map<String, Object>) result;
        assertFalse((Boolean) errorResult2.get("success"));
    }

    @Test
    public void testExecuteRuleWithAction() {
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setAction("['capability': 'weather', 'confidence': 0.9]");

        engine.registerRule(rule);

        Map<String, Object> context = new HashMap<>();
        Map<String, Object> result = engine.execute("rule-1", context);

        assertNotNull(result);
        assertEquals("weather", result.get("capability"));
        assertEquals(0.9, result.get("confidence"));
    }

    @Test
    public void testExecuteRuleWithCondition() {
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setCondition("query != null && query.contains('weather')")
            .setAction("['capability': 'weather_query']");

        engine.registerRule(rule);

        Map<String, Object> context1 = new HashMap<>();
        context1.put("query", "what is the weather today");
        Map<String, Object> result1 = engine.execute("rule-1", context1);
        assertEquals("weather_query", result1.get("capability"));

        Map<String, Object> context2 = new HashMap<>();
        context2.put("query", "hello world");
        Map<String, Object> result2 = engine.execute("rule-1", context2);
        assertNotNull(result2);
    }

    @Test
    public void testExecuteNonExistentRule() {
        Map<String, Object> result = engine.execute("non-existent", new HashMap<>());
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    public void testExecuteDisabledRule() {
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setAction("result = 'success'")
            .setEnabled(false);

        engine.registerRule(rule);

        Map<String, Object> result = engine.execute("rule-1", new HashMap<>());
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    public void testGetSceneRules() {
        RuleScript rule1 = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setPriority(1)
            .setAction("result = 1");

        RuleScript rule2 = new RuleScript()
            .setRuleId("rule-2")
            .setSceneId("scene-1")
            .setPriority(2)
            .setAction("result = 2");

        RuleScript rule3 = new RuleScript()
            .setRuleId("rule-3")
            .setSceneId("scene-2")
            .setAction("result = 3");

        engine.registerRule(rule1);
        engine.registerRule(rule2);
        engine.registerRule(rule3);

        List<RuleScript> scene1Rules = engine.getSceneRules("scene-1");
        assertEquals(2, scene1Rules.size());
        assertEquals("rule-2", scene1Rules.get(0).getRuleId());
        assertEquals("rule-1", scene1Rules.get(1).getRuleId());

        List<RuleScript> scene2Rules = engine.getSceneRules("scene-2");
        assertEquals(1, scene2Rules.size());

        List<RuleScript> emptyRules = engine.getSceneRules("non-existent");
        assertTrue(emptyRules.isEmpty());
    }

    @Test
    public void testClearSceneRules() {
        RuleScript rule1 = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setAction("result = 1");

        RuleScript rule2 = new RuleScript()
            .setRuleId("rule-2")
            .setSceneId("scene-2")
            .setAction("result = 2");

        engine.registerRule(rule1);
        engine.registerRule(rule2);

        assertEquals(2, engine.getRuleCount());

        engine.clearSceneRules("scene-1");
        assertEquals(1, engine.getRuleCount());
        assertFalse(engine.hasRule("rule-1"));
        assertTrue(engine.hasRule("rule-2"));
    }

    @Test
    public void testClearAllRules() {
        RuleScript rule1 = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setAction("result = 1");

        RuleScript rule2 = new RuleScript()
            .setRuleId("rule-2")
            .setSceneId("scene-2")
            .setAction("result = 2");

        engine.registerRule(rule1);
        engine.registerRule(rule2);

        assertEquals(2, engine.getRuleCount());

        engine.clearAllRules();
        assertEquals(0, engine.getRuleCount());
    }

    @Test
    public void testExecuteSceneRules() {
        RuleScript rule1 = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setPriority(1)
            .setCondition("query == 'hello'")
            .setAction("['capability': 'greeting']");

        RuleScript rule2 = new RuleScript()
            .setRuleId("rule-2")
            .setSceneId("scene-1")
            .setPriority(2)
            .setCondition("query == 'weather'")
            .setAction("['capability': 'weather']");

        engine.registerRule(rule1);
        engine.registerRule(rule2);

        Map<String, Object> context1 = new HashMap<>();
        context1.put("query", "weather");
        Map<String, Object> result1 = engine.executeSceneRules("scene-1", context1);
        assertEquals("weather", result1.get("capability"));

        Map<String, Object> context2 = new HashMap<>();
        context2.put("query", "hello");
        Map<String, Object> result2 = engine.executeSceneRules("scene-1", context2);
        assertEquals("greeting", result2.get("capability"));
    }

    @Test
    public void testExecuteRulesByType() {
        RuleScript decisionRule = new RuleScript()
            .setRuleId("decision-1")
            .setSceneId("scene-1")
            .setType(RuleType.DECISION)
            .setAction("['capability': 'decision_result']");

        RuleScript transformRule = new RuleScript()
            .setRuleId("transform-1")
            .setSceneId("scene-1")
            .setType(RuleType.TRANSFORM)
            .setAction("['data': 'transformed']");

        engine.registerRule(decisionRule);
        engine.registerRule(transformRule);

        Map<String, Object> context = new HashMap<>();
        Map<String, Object> result = engine.executeRulesByType("scene-1", RuleType.DECISION, context);
        assertEquals("decision_result", result.get("capability"));
    }

    @Test
    public void testGetSceneIds() {
        engine.registerRule(new RuleScript().setRuleId("r1").setSceneId("scene-1"));
        engine.registerRule(new RuleScript().setRuleId("r2").setSceneId("scene-2"));

        List<String> sceneIds = engine.getSceneIds();
        assertEquals(2, sceneIds.size());
        assertTrue(sceneIds.contains("scene-1"));
        assertTrue(sceneIds.contains("scene-2"));
    }

    @Test
    public void testGetSceneRuleCount() {
        engine.registerRule(new RuleScript().setRuleId("r1").setSceneId("scene-1"));
        engine.registerRule(new RuleScript().setRuleId("r2").setSceneId("scene-1"));
        engine.registerRule(new RuleScript().setRuleId("r3").setSceneId("scene-2"));

        assertEquals(2, engine.getSceneRuleCount("scene-1"));
        assertEquals(1, engine.getSceneRuleCount("scene-2"));
        assertEquals(0, engine.getSceneRuleCount("non-existent"));
    }

    @Test
    public void testComplexScriptExecution() {
        Map<String, Object> context = new HashMap<>();
        context.put("a", 10);
        context.put("b", 20);

        Object result = engine.executeScript("a * b + 5", context);
        assertEquals(205, result);
    }

    @Test
    public void testScriptWithMapOperations() {
        Map<String, Object> context = new HashMap<>();
        context.put("data", new HashMap<String, Object>());

        engine.executeScript("data.put('key', 'value')", context);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) context.get("data");
        assertEquals("value", data.get("key"));
    }

    @Test
    public void testRuleScriptToString() {
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setName("Test Rule")
            .setType(RuleType.DECISION)
            .setPriority(5);

        String str = rule.toString();
        assertTrue(str.contains("rule-1"));
        assertTrue(str.contains("Test Rule"));
        assertTrue(str.contains("DECISION"));
        assertTrue(str.contains("5"));
    }

    @Test
    public void testRegisterNullRule() {
        int countBefore = engine.getRuleCount();
        engine.registerRule(null);
        assertEquals(countBefore, engine.getRuleCount());
    }

    @Test
    public void testRegisterRuleWithNullId() {
        RuleScript rule = new RuleScript().setName("Test");
        int countBefore = engine.getRuleCount();
        engine.registerRule(rule);
        assertEquals(countBefore, engine.getRuleCount());
    }

    @Test
    public void testPersistRule() {
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setSceneId("scene-1")
            .setAction("result = 'persisted'");

        engine.persistRule(rule);

        assertTrue(engine.hasRule("rule-1"));
    }
}
