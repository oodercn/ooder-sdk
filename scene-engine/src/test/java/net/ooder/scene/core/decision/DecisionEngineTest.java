package net.ooder.scene.core.decision;

import net.ooder.scene.core.decision.impl.DecisionEngineImpl;
import net.ooder.scene.skill.llm.LlmProvider;
import net.ooder.scene.skill.rule.MvelRuleEngine;
import net.ooder.scene.skill.rule.impl.MvelRuleEngineImpl;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Decision Engine 单元测试
 */
public class DecisionEngineTest {

    @Test
    public void testDecisionModeValues() {
        assertEquals(3, DecisionMode.values().length);
        assertNotNull(DecisionMode.ONLINE_ONLY);
        assertNotNull(DecisionMode.OFFLINE_ONLY);
        assertNotNull(DecisionMode.ONLINE_FIRST);
    }

    @Test
    public void testDecisionModeNeedsLlm() {
        assertTrue(DecisionMode.ONLINE_ONLY.needsLlm());
        assertFalse(DecisionMode.OFFLINE_ONLY.needsLlm());
        assertTrue(DecisionMode.ONLINE_FIRST.needsLlm());
    }

    @Test
    public void testDecisionModeNeedsRuleEngine() {
        assertFalse(DecisionMode.ONLINE_ONLY.needsRuleEngine());
        assertTrue(DecisionMode.OFFLINE_ONLY.needsRuleEngine());
        assertTrue(DecisionMode.ONLINE_FIRST.needsRuleEngine());
    }

    @Test
    public void testDecisionModeAllowsFallback() {
        assertFalse(DecisionMode.ONLINE_ONLY.allowsFallback());
        assertFalse(DecisionMode.OFFLINE_ONLY.allowsFallback());
        assertTrue(DecisionMode.ONLINE_FIRST.allowsFallback());
    }

    @Test
    public void testDecisionModeFromCode() {
        assertEquals(DecisionMode.ONLINE_ONLY, DecisionMode.fromCode("online_only"));
        assertEquals(DecisionMode.OFFLINE_ONLY, DecisionMode.fromCode("offline_only"));
        assertEquals(DecisionMode.ONLINE_FIRST, DecisionMode.fromCode("online_first"));
        assertEquals(DecisionMode.ONLINE_FIRST, DecisionMode.fromCode("invalid"));
        assertEquals(DecisionMode.ONLINE_FIRST, DecisionMode.fromCode(null));
    }

    @Test
    public void testDecisionModeCodeNameDescription() {
        assertEquals("online_only", DecisionMode.ONLINE_ONLY.getCode());
        assertEquals("仅在线", DecisionMode.ONLINE_ONLY.getName());
        assertNotNull(DecisionMode.ONLINE_ONLY.getDescription());
    }

    @Test
    public void testDecisionContextCreation() {
        DecisionContext context = new DecisionContext();
        assertNotNull(context);
        assertTrue(context.getTimestamp() > 0);
        assertNotNull(context.getConversationHistory());
        assertNotNull(context.getMetadata());
        assertNotNull(context.getParams());
    }

    @Test
    public void testDecisionContextSetters() {
        DecisionContext context = new DecisionContext()
            .setQuery("test query")
            .setUserId("user-1")
            .setSceneId("scene-1")
            .setGroupId("group-1")
            .setAgentId("agent-1")
            .setMode(DecisionMode.OFFLINE_ONLY);

        assertEquals("test query", context.getQuery());
        assertEquals("user-1", context.getUserId());
        assertEquals("scene-1", context.getSceneId());
        assertEquals("group-1", context.getGroupId());
        assertEquals("agent-1", context.getAgentId());
        assertEquals(DecisionMode.OFFLINE_ONLY, context.getMode());
    }

    @Test
    public void testDecisionContextStaticFactory() {
        DecisionContext context = DecisionContext.of("query", "user", "scene");
        assertEquals("query", context.getQuery());
        assertEquals("user", context.getUserId());
        assertEquals("scene", context.getSceneId());
        assertEquals(DecisionMode.ONLINE_FIRST, context.getMode());
    }

    @Test
    public void testDecisionContextAddMessage() {
        DecisionContext context = new DecisionContext();
        context.addMessage("user", "Hello");
        context.addMessage("assistant", "Hi there");

        assertEquals(2, context.getConversationHistory().size());
        assertTrue(context.hasConversationHistory());
    }

    @Test
    public void testDecisionContextAddParam() {
        DecisionContext context = new DecisionContext();
        context.addParam("key1", "value1");
        context.addParam("key2", 123);

        assertEquals("value1", context.getParams().get("key1"));
        assertEquals(123, context.getParams().get("key2"));
    }

    @Test
    public void testDecisionContextHasQuery() {
        DecisionContext context = new DecisionContext();
        assertFalse(context.hasQuery());

        context.setQuery("");
        assertFalse(context.hasQuery());

        context.setQuery("test");
        assertTrue(context.hasQuery());
    }

    @Test
    public void testDecisionResultSuccess() {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        DecisionResult result = DecisionResult.success("capability-1", params);
        assertTrue(result.isSuccess());
        assertEquals("capability-1", result.getCapability());
        assertEquals("value", result.getParams().get("key"));
    }

    @Test
    public void testDecisionResultSuccessWithCapId() {
        DecisionResult result = DecisionResult.success("capability-1", "cap-123", null);
        assertTrue(result.isSuccess());
        assertEquals("capability-1", result.getCapability());
        assertEquals("cap-123", result.getCapId());
    }

    @Test
    public void testDecisionResultFailure() {
        DecisionResult result = DecisionResult.failure("Something went wrong");
        assertFalse(result.isSuccess());
        assertEquals("Something went wrong", result.getErrorMessage());
    }

    @Test
    public void testDecisionResultConfidence() {
        DecisionResult result = new DecisionResult();
        result.setConfidence(0.9f);
        assertTrue(result.isHighConfidence());

        result.setConfidence(0.3f);
        assertTrue(result.isLowConfidence());

        result.setConfidence(0.6f);
        assertFalse(result.isHighConfidence());
        assertFalse(result.isLowConfidence());
    }

    @Test
    public void testDecisionResultFromLlm() {
        DecisionResult result = new DecisionResult();
        result.setFromLlm(true);
        assertTrue(result.isFromLlm());
        assertEquals(DecisionResult.DecisionSource.LLM, result.getSource());

        result.setFromLlm(false);
        assertFalse(result.isFromLlm());
        assertEquals(DecisionResult.DecisionSource.RULE_ENGINE, result.getSource());
    }

    @Test
    public void testDecisionResultHasMethods() {
        DecisionResult result = new DecisionResult();
        assertFalse(result.hasCapability());
        assertFalse(result.hasCapId());
        assertFalse(result.hasParams());

        result.setCapability("test");
        assertTrue(result.hasCapability());

        result.setCapId("cap-1");
        assertTrue(result.hasCapId());

        result.addParam("k", "v");
        assertTrue(result.hasParams());
    }

    @Test
    public void testDecisionResultMetadata() {
        DecisionResult result = new DecisionResult();
        result.addMetadata("meta1", "value1");
        result.addMetadata("meta2", 123);

        assertEquals("value1", result.getMetadata().get("meta1"));
        assertEquals(123, result.getMetadata().get("meta2"));
    }

    @Test
    public void testDecisionEngineStats() {
        DecisionEngine.DecisionStats stats = new DecisionEngine.DecisionStats();
        stats.setTotalDecisions(100);
        stats.setSuccessfulDecisions(80);
        stats.setFailedDecisions(20);
        stats.setLlmDecisions(50);
        stats.setRuleDecisions(30);
        stats.setCacheHits(20);
        stats.setAverageLatencyMs(15.5);

        assertEquals(100, stats.getTotalDecisions());
        assertEquals(80, stats.getSuccessfulDecisions());
        assertEquals(20, stats.getFailedDecisions());
        assertEquals(50, stats.getLlmDecisions());
        assertEquals(30, stats.getRuleDecisions());
        assertEquals(20, stats.getCacheHits());
        assertEquals(15.5, stats.getAverageLatencyMs(), 0.001);
        assertEquals(0.8, stats.getSuccessRate(), 0.001);
        assertEquals(0.5, stats.getLlmUsageRate(), 0.001);
        assertEquals(0.2, stats.getCacheHitRate(), 0.001);
    }

    @Test
    public void testDecisionEngineImplBasic() {
        DecisionEngine engine = new DecisionEngineImpl();
        assertNotNull(engine);
        assertEquals("SceneDecisionEngine", engine.getName());
        assertEquals("2.3.1", engine.getVersion());
        assertEquals(DecisionMode.ONLINE_FIRST, engine.getMode());
    }

    @Test
    public void testDecisionEngineImplSetMode() {
        DecisionEngine engine = new DecisionEngineImpl();
        engine.setMode(DecisionMode.OFFLINE_ONLY);
        assertEquals(DecisionMode.OFFLINE_ONLY, engine.getMode());

        engine.setMode(null);
        assertEquals(DecisionMode.ONLINE_FIRST, engine.getMode());
    }

    @Test
    public void testDecisionEngineImplNullContext() {
        DecisionEngine engine = new DecisionEngineImpl();
        DecisionResult result = engine.decide(null);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("null"));
    }

    @Test
    public void testDecisionEngineImplOfflineOnlyNoRuleEngine() {
        DecisionEngine engine = new DecisionEngineImpl(null, null);
        engine.setMode(DecisionMode.OFFLINE_ONLY);

        DecisionContext context = DecisionContext.of("test query", "user-1", "scene-1");
        DecisionResult result = engine.decide(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void testDecisionEngineImplOnlineOnlyNoLlm() {
        DecisionEngine engine = new DecisionEngineImpl(null, null);
        engine.setMode(DecisionMode.ONLINE_ONLY);

        DecisionContext context = DecisionContext.of("test query", "user-1", "scene-1");
        DecisionResult result = engine.decide(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void testDecisionEngineImplReset() {
        DecisionEngine engine = new DecisionEngineImpl();
        DecisionContext context = DecisionContext.of("test query", "user-1", "scene-1");
        engine.decide(context);

        assertTrue(engine.getStats().getTotalDecisions() > 0);

        engine.reset();
        assertEquals(0, engine.getStats().getTotalDecisions());
    }

    @Test
    public void testDecisionEngineImplWithMockLlm() {
        LlmProvider mockLlm = createMockLlmProvider();
        DecisionEngine engine = new DecisionEngineImpl(mockLlm, null);
        engine.setMode(DecisionMode.ONLINE_ONLY);

        DecisionContext context = DecisionContext.of("查询天气", "user-1", "scene-1");
        DecisionResult result = engine.decide(context);

        assertNotNull(result);
        assertTrue(engine.isLlmAvailable());
    }

    @Test
    public void testDecisionEngineImplWithMockRuleEngine() {
        MvelRuleEngine mockRuleEngine = createMockRuleEngine();
        DecisionEngine engine = new DecisionEngineImpl(null, mockRuleEngine);
        engine.setMode(DecisionMode.OFFLINE_ONLY);

        DecisionContext context = DecisionContext.of("查询天气", "user-1", "scene-1");
        DecisionResult result = engine.decide(context);

        assertTrue(result.isSuccess());
        assertFalse(result.isFromLlm());
        assertEquals(DecisionResult.DecisionSource.RULE_ENGINE, result.getSource());
    }

    @Test
    public void testDecisionEngineImplOnlineFirstFallback() {
        MvelRuleEngine mockRuleEngine = createMockRuleEngine();
        DecisionEngine engine = new DecisionEngineImpl(null, mockRuleEngine);
        engine.setMode(DecisionMode.ONLINE_FIRST);

        DecisionContext context = DecisionContext.of("查询天气", "user-1", "scene-1");
        DecisionResult result = engine.decide(context);

        assertTrue(result.isSuccess());
        assertEquals(DecisionResult.DecisionSource.RULE_ENGINE, result.getSource());
    }

    @Test
    public void testDecisionEngineImplStatsTracking() {
        MvelRuleEngine mockRuleEngine = createMockRuleEngine();
        DecisionEngine engine = new DecisionEngineImpl(null, mockRuleEngine);
        engine.setMode(DecisionMode.OFFLINE_ONLY);

        DecisionContext context = DecisionContext.of("test query", "user-1", "scene-1");
        engine.decide(context);
        engine.decide(context);
        engine.decide(context);

        DecisionEngine.DecisionStats stats = engine.getStats();
        assertEquals(3, stats.getTotalDecisions());
        assertEquals(3, stats.getSuccessfulDecisions());
        assertEquals(3, stats.getRuleDecisions());
        assertTrue(stats.getAverageLatencyMs() >= 0);
    }

    @Test
    public void testDecisionEngineImplContextModeOverride() {
        MvelRuleEngine mockRuleEngine = createMockRuleEngine();
        DecisionEngine engine = new DecisionEngineImpl(null, mockRuleEngine);
        engine.setMode(DecisionMode.ONLINE_FIRST);

        DecisionContext context = DecisionContext.of("test query", "user-1", "scene-1")
            .setMode(DecisionMode.OFFLINE_ONLY);

        DecisionResult result = engine.decide(context);
        assertTrue(result.isSuccess());
        assertEquals(DecisionResult.DecisionSource.RULE_ENGINE, result.getSource());
    }

    @Test
    public void testDecisionResultDecisionSourceValues() {
        DecisionResult.DecisionSource[] sources = DecisionResult.DecisionSource.values();
        assertEquals(4, sources.length);
        assertEquals(DecisionResult.DecisionSource.LLM, DecisionResult.DecisionSource.valueOf("LLM"));
        assertEquals(DecisionResult.DecisionSource.RULE_ENGINE, DecisionResult.DecisionSource.valueOf("RULE_ENGINE"));
        assertEquals(DecisionResult.DecisionSource.CACHE, DecisionResult.DecisionSource.valueOf("CACHE"));
        assertEquals(DecisionResult.DecisionSource.DEFAULT, DecisionResult.DecisionSource.valueOf("DEFAULT"));
    }

    @Test
    public void testDecisionResultToString() {
        DecisionResult result = DecisionResult.success("test-cap", "cap-1", null)
            .setConfidence(0.85f)
            .setFromLlm(true);

        String str = result.toString();
        assertTrue(str.contains("test-cap"));
        assertTrue(str.contains("cap-1"));
        assertTrue(str.contains("true"));
        assertTrue(str.contains("0.85"));
    }

    private LlmProvider createMockLlmProvider() {
        return new LlmProvider() {
            @Override
            public String getProviderType() {
                return "mock";
            }

            @Override
            public List<String> getSupportedModels() {
                return Arrays.asList("default");
            }

            @Override
            public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
                Map<String, Object> response = new HashMap<>();
                List<Map<String, Object>> choices = new ArrayList<>();
                Map<String, Object> choice = new HashMap<>();
                Map<String, Object> message = new HashMap<>();
                message.put("content", "{\"capability\": \"weather_query\", \"capId\": \"weather-001\", \"params\": {}, \"confidence\": 0.95}");
                choice.put("message", message);
                choices.add(choice);
                response.put("choices", choices);
                return response;
            }

            @Override
            public String complete(String model, String prompt, Map<String, Object> options) {
                return "mock completion";
            }

            @Override
            public List<double[]> embed(String model, List<String> texts) {
                return new ArrayList<>();
            }

            @Override
            public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
                return text;
            }

            @Override
            public String summarize(String model, String text, int maxLength) {
                return text;
            }

            @Override
            public boolean supportsStreaming() {
                return false;
            }

            @Override
            public boolean supportsFunctionCalling() {
                return false;
            }

            @Override
            public void chatStream(String model, List<Map<String, Object>> messages, Map<String, Object> options, net.ooder.scene.skill.llm.StreamHandler handler) {
            }
        };
    }

    private MvelRuleEngine createMockRuleEngine() {
        return new MvelRuleEngine() {
            @Override
            public Map<String, Object> execute(String ruleId, Map<String, Object> context) {
                Map<String, Object> result = new HashMap<>();
                result.put("capability", "weather_query");
                result.put("capId", "weather-001");
                result.put("params", new HashMap<>());
                result.put("confidence", 0.85);
                return result;
            }

            @Override
            public Object executeScript(String script, Map<String, Object> context) {
                return execute(script, context);
            }

            @Override
            public void registerRule(net.ooder.scene.skill.rule.RuleScript rule) {
            }

            @Override
            public void persistRule(net.ooder.scene.skill.rule.RuleScript rule) {
            }

            @Override
            public net.ooder.scene.skill.rule.RuleScript getRule(String ruleId) {
                return null;
            }

            @Override
            public void removeRule(String ruleId) {
            }

            @Override
            public List<net.ooder.scene.skill.rule.RuleScript> getSceneRules(String sceneId) {
                return new ArrayList<>();
            }

            @Override
            public boolean hasRule(String ruleId) {
                return false;
            }

            @Override
            public int getRuleCount() {
                return 0;
            }
        };
    }
}
