package net.ooder.scene.skill.rule;

import net.ooder.scene.skill.rule.impl.LlmRuleGeneratorImpl;
import net.ooder.scene.skill.rule.impl.MvelRuleEngineImpl;
import net.ooder.scene.skill.llm.LlmProvider;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LLM Rule Generator 单元测试
 */
public class LlmRuleGeneratorTest {

    private LlmRuleGeneratorImpl generator;
    private MvelRuleEngineImpl ruleEngine;

    @BeforeEach
    public void setUp() {
        ruleEngine = new MvelRuleEngineImpl();
    }

    @AfterEach
    public void tearDown() {
        ruleEngine.clearAllRules();
    }

    @Test
    public void testGeneratorWithNullLlmProvider() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = generator.generateRule("scene-1", "test conversation", null);
        assertNull(rule);
    }

    @Test
    public void testGeneratorBasicInfo() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        assertEquals("LlmRuleGenerator", generator.getName());
        assertEquals("2.3.1", generator.getVersion());
    }

    @Test
    public void testValidateNullRule() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        LlmRuleGenerator.RuleValidationResult result = generator.validateRule(null);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void testValidateRuleWithEmptyId() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript().setName("Test");
        LlmRuleGenerator.RuleValidationResult result = generator.validateRule(rule);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("ID"));
    }

    @Test
    public void testValidateValidRule() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setCondition("query != null")
            .setAction("['capability': 'test']")
            .setPriority(10);

        LlmRuleGenerator.RuleValidationResult result = generator.validateRule(rule);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateRuleWithInvalidCondition() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setCondition("invalid syntax {{{")
            .setAction("result = 1");

        LlmRuleGenerator.RuleValidationResult result = generator.validateRule(rule);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("condition"));
    }

    @Test
    public void testValidateRuleWithInvalidAction() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setCondition("true")
            .setAction("invalid syntax {{{");

        LlmRuleGenerator.RuleValidationResult result = generator.validateRule(rule);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("action"));
    }

    @Test
    public void testValidateRuleWithWarnings() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setPriority(-5);

        LlmRuleGenerator.RuleValidationResult result = generator.validateRule(rule);
        assertTrue(result.isValid());
        assertFalse(result.getWarnings().isEmpty());
    }

    @Test
    public void testTestRuleWithEmptyCases() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setAction("['result': 'ok']");

        ruleEngine.registerRule(rule);

        LlmRuleGenerator.RuleTestResult result = generator.testRule(rule, new ArrayList<>());
        assertEquals(0, result.getTotalCases());
        assertTrue(result.isPassed());
    }

    @Test
    public void testTestRuleWithPassingCases() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setAction("['capability': 'test', 'confidence': 0.9]");

        ruleEngine.registerRule(rule);

        List<Map<String, Object>> testCases = new ArrayList<>();
        Map<String, Object> testCase1 = new HashMap<>();
        testCase1.put("query", "hello");
        testCase1.put("expected", Collections.singletonMap("capability", "test"));
        testCases.add(testCase1);

        LlmRuleGenerator.RuleTestResult result = generator.testRule(rule, testCases);
        assertEquals(1, result.getTotalCases());
        assertEquals(1, result.getPassedCases());
        assertTrue(result.isPassed());
    }

    @Test
    public void testTestRuleWithFailingCases() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript()
            .setRuleId("rule-1")
            .setAction("['capability': 'actual']");

        ruleEngine.registerRule(rule);

        List<Map<String, Object>> testCases = new ArrayList<>();
        Map<String, Object> testCase1 = new HashMap<>();
        testCase1.put("query", "hello");
        testCase1.put("expected", Collections.singletonMap("capability", "expected"));
        testCases.add(testCase1);

        LlmRuleGenerator.RuleTestResult result = generator.testRule(rule, testCases);
        assertEquals(1, result.getTotalCases());
        assertEquals(0, result.getPassedCases());
        assertEquals(1, result.getFailedCases());
        assertFalse(result.isPassed());
    }

    @Test
    public void testGenerateRuleFromIntentWithNullLlm() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = generator.generateRuleFromIntent("scene-1", "greeting", Arrays.asList("hello", "hi"));
        assertNull(rule);
    }

    @Test
    public void testOptimizeRuleWithNullLlm() {
        generator = new LlmRuleGeneratorImpl(null, ruleEngine);
        
        RuleScript rule = new RuleScript().setRuleId("rule-1").setAction("result = 1");
        RuleScript optimized = generator.optimizeRule(rule, "make it better");
        
        assertEquals(rule.getRuleId(), optimized.getRuleId());
    }

    @Test
    public void testOptimizeNullRule() {
        LlmProvider mockLlm = createMockLlmProvider();
        generator = new LlmRuleGeneratorImpl(mockLlm, ruleEngine);
        
        RuleScript optimized = generator.optimizeRule(null, "feedback");
        assertNull(optimized);
    }

    @Test
    public void testValidationResultStaticMethods() {
        LlmRuleGenerator.RuleValidationResult valid = LlmRuleGenerator.RuleValidationResult.valid();
        assertTrue(valid.isValid());
        assertNull(valid.getErrorMessage());

        LlmRuleGenerator.RuleValidationResult invalid = LlmRuleGenerator.RuleValidationResult.invalid("error");
        assertFalse(invalid.isValid());
        assertEquals("error", invalid.getErrorMessage());
    }

    @Test
    public void testValidationResultWarnings() {
        LlmRuleGenerator.RuleValidationResult result = LlmRuleGenerator.RuleValidationResult.valid();
        result.addWarning("warning 1");
        result.addWarning("warning 2");

        assertEquals(2, result.getWarnings().size());
    }

    @Test
    public void testTestResultSummary() {
        LlmRuleGenerator.RuleTestResult result = new LlmRuleGenerator.RuleTestResult();
        result.setTotalCases(10);
        result.setPassedCases(8);
        result.setFailedCases(2);
        result.setSummary("Test completed");

        assertEquals(10, result.getTotalCases());
        assertEquals(8, result.getPassedCases());
        assertEquals(2, result.getFailedCases());
        assertEquals("Test completed", result.getSummary());
    }

    @Test
    public void testTestCaseResult() {
        LlmRuleGenerator.TestCaseResult caseResult = new LlmRuleGenerator.TestCaseResult();
        Map<String, Object> input = new HashMap<>();
        input.put("query", "test");
        
        caseResult.setInput(input);
        caseResult.setExpectedOutput("expected");
        caseResult.setActualOutput("actual");
        caseResult.setPassed(false);
        caseResult.setMessage("mismatch");

        assertEquals(input, caseResult.getInput());
        assertEquals("expected", caseResult.getExpectedOutput());
        assertEquals("actual", caseResult.getActualOutput());
        assertFalse(caseResult.isPassed());
        assertEquals("mismatch", caseResult.getMessage());
    }

    @Test
    public void testGenerateRuleWithMockLlm() {
        LlmProvider mockLlm = createMockLlmProvider();
        generator = new LlmRuleGeneratorImpl(mockLlm, ruleEngine);

        RuleScript rule = generator.generateRule("scene-1", "test conversation", null);
        assertNotNull(rule);
        assertEquals("scene-1", rule.getSceneId());
    }

    @Test
    public void testGenerateRuleFromIntentWithMockLlm() {
        LlmProvider mockLlm = createMockLlmProvider();
        generator = new LlmRuleGeneratorImpl(mockLlm, ruleEngine);

        RuleScript rule = generator.generateRuleFromIntent("scene-1", "greeting", Arrays.asList("hello", "hi"));
        assertNotNull(rule);
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
                message.put("content", "{\n" +
                    "  \"ruleId\": \"test_rule_001\",\n" +
                    "  \"name\": \"Test Rule\",\n" +
                    "  \"type\": \"DECISION\",\n" +
                    "  \"condition\": \"query != null\",\n" +
                    "  \"action\": \"['capability': 'test', 'confidence': 0.9]\",\n" +
                    "  \"priority\": 50,\n" +
                    "  \"description\": \"Generated test rule\"\n" +
                    "}");
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
}
