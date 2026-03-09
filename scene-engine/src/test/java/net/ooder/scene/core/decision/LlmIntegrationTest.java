package net.ooder.scene.core.decision;

import net.ooder.scene.core.decision.impl.DecisionEngineImpl;
import net.ooder.scene.skill.knowledge.*;
import net.ooder.scene.skill.knowledge.impl.KnowledgeCapabilityImpl;
import net.ooder.scene.skill.llm.*;
import net.ooder.scene.skill.rag.KnowledgeBaseConfig;
import net.ooder.scene.skill.rule.*;
import net.ooder.scene.skill.rule.impl.LlmRuleGeneratorImpl;
import net.ooder.scene.skill.rule.impl.MvelRuleEngineImpl;
import net.ooder.scene.skill.vector.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LLM集成测试
 */
public class LlmIntegrationTest {

    private DecisionEngineImpl decisionEngine;
    private MvelRuleEngineImpl ruleEngine;
    private LlmRuleGeneratorImpl ruleGenerator;
    private KnowledgeCapabilityImpl knowledgeCapability;
    private MockLlmProvider llmProvider;
    private MockEmbeddingService embeddingService;
    private MockVectorStore vectorStore;
    private MockKnowledgeBaseService kbService;

    @BeforeEach
    public void setUp() {
        llmProvider = new MockLlmProvider();
        embeddingService = new MockEmbeddingService();
        vectorStore = new MockVectorStore();
        kbService = new MockKnowledgeBaseService();

        ruleEngine = new MvelRuleEngineImpl();
        ruleGenerator = new LlmRuleGeneratorImpl(llmProvider, ruleEngine);
        knowledgeCapability = new KnowledgeCapabilityImpl(kbService, embeddingService, vectorStore);
        decisionEngine = new DecisionEngineImpl(llmProvider, ruleEngine);
    }

    @AfterEach
    public void tearDown() {
        ruleEngine.clearAllRules();
        knowledgeCapability.clearCache(null);
    }

    @Test
    public void testDecisionEngineOfflineMode() {
        RuleScript rule = new RuleScript()
            .setRuleId("intent_routing")
            .setSceneId("scene-weather")
            .setType(RuleType.DECISION)
            .setAction("['capability': 'weather_query', 'capId': 'weather-001', 'confidence': 0.9]")
            .setPriority(10);

        ruleEngine.registerRule(rule);

        DecisionContext context = DecisionContext.of("What is the weather today?", "user-1", "scene-weather")
            .setMode(DecisionMode.OFFLINE_ONLY);
        DecisionResult result = decisionEngine.decide(context);

        assertTrue(result.isSuccess());
        assertEquals(DecisionResult.DecisionSource.RULE_ENGINE, result.getSource());
    }

    @Test
    public void testDecisionEngineOnlineFirstFallback() {
        RuleScript rule = new RuleScript()
            .setRuleId("intent_routing")
            .setSceneId("scene-test")
            .setType(RuleType.FALLBACK)
            .setAction("['capability': 'fallback', 'confidence': 0.5]")
            .setPriority(1);

        ruleEngine.registerRule(rule);
        decisionEngine.setMode(DecisionMode.ONLINE_FIRST);

        DecisionContext context = DecisionContext.of("test query", "user-1", "scene-test");
        DecisionResult result = decisionEngine.decide(context);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testRuleGenerationAndExecution() {
        RuleScript generatedRule = ruleGenerator.generateRule("scene-test", "User wants to check weather", null);
        
        if (generatedRule != null) {
            assertTrue(ruleEngine.hasRule(generatedRule.getRuleId()));
            LlmRuleGenerator.RuleValidationResult validation = ruleGenerator.validateRule(generatedRule);
            assertTrue(validation.isValid());
        }
    }

    @Test
    public void testKnowledgeCapabilityIntegration() {
        KnowledgeBaseConfig config = new KnowledgeBaseConfig();
        config.setKbId("kb-test");
        config.setSimilarityThreshold(0.5f);
        config.setMaxResults(5);

        knowledgeCapability.registerKnowledgeBase("kb-test", KnowledgeCapability.KnowledgeLayer.SCENE, config);
        vectorStore.addMockResult("kb-test", createMockSearchResult("doc-1", "Weather information", 0.9f));

        KnowledgeCapability.KnowledgeResult result = knowledgeCapability.retrieve(
            "weather query", KnowledgeCapability.KnowledgeLayer.SCENE, null);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testCrossLayerKnowledgeRetrieval() {
        knowledgeCapability.registerKnowledgeBase("kb-general", KnowledgeCapability.KnowledgeLayer.GENERAL, new KnowledgeBaseConfig());
        knowledgeCapability.registerKnowledgeBase("kb-pro", KnowledgeCapability.KnowledgeLayer.PROFESSIONAL, new KnowledgeBaseConfig());
        knowledgeCapability.registerKnowledgeBase("kb-scene", KnowledgeCapability.KnowledgeLayer.SCENE, new KnowledgeBaseConfig());

        vectorStore.addMockResult("kb-scene", createMockSearchResult("doc-1", "Scene doc", 0.95f));
        vectorStore.addMockResult("kb-pro", createMockSearchResult("doc-2", "Pro doc", 0.85f));

        KnowledgeCapability.KnowledgeResult result = knowledgeCapability.crossLayerRetrieve("test query", null, null);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testDecisionEngineStats() {
        decisionEngine.setMode(DecisionMode.OFFLINE_ONLY);

        RuleScript rule = new RuleScript()
            .setRuleId("intent_routing")
            .setSceneId("scene-stats")
            .setAction("['capability': 'test']");

        ruleEngine.registerRule(rule);

        for (int i = 0; i < 5; i++) {
            DecisionContext context = DecisionContext.of("query " + i, "user-1", "scene-stats");
            decisionEngine.decide(context);
        }

        DecisionEngine.DecisionStats stats = decisionEngine.getStats();
        assertEquals(5, stats.getTotalDecisions());
        assertTrue(stats.getAverageLatencyMs() >= 0);

        decisionEngine.reset();
        assertEquals(0, decisionEngine.getStats().getTotalDecisions());
    }

    @Test
    public void testKnowledgeBaseManagement() {
        knowledgeCapability.registerKnowledgeBase("kb-1", KnowledgeCapability.KnowledgeLayer.SCENE, new KnowledgeBaseConfig());
        knowledgeCapability.registerKnowledgeBase("kb-2", KnowledgeCapability.KnowledgeLayer.PROFESSIONAL, new KnowledgeBaseConfig());

        assertEquals(2, knowledgeCapability.getTotalKnowledgeBases());
        assertEquals(1, knowledgeCapability.getLayerKnowledgeBases(KnowledgeCapability.KnowledgeLayer.SCENE).size());
        assertEquals(1, knowledgeCapability.getLayerKnowledgeBases(KnowledgeCapability.KnowledgeLayer.PROFESSIONAL).size());

        knowledgeCapability.unregisterKnowledgeBase("kb-1");
        assertEquals(1, knowledgeCapability.getTotalKnowledgeBases());
    }

    private SearchResult createMockSearchResult(String docId, String content, float score) {
        SearchResult result = new SearchResult();
        result.setContent(content);
        result.setScore(score);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("docId", docId);
        metadata.put("chunkId", "chunk-1");
        result.setMetadata(metadata);
        return result;
    }

    private static class MockLlmProvider implements LlmProvider {
        @Override
        public String getProviderType() { return "mock"; }

        @Override
        public List<String> getSupportedModels() { return Arrays.asList("default"); }

        @Override
        public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
            Map<String, Object> response = new HashMap<>();
            List<Map<String, Object>> choices = new ArrayList<>();
            Map<String, Object> choice = new HashMap<>();
            Map<String, Object> message = new HashMap<>();
            message.put("content", "{\"capability\": \"llm_response\", \"confidence\": 0.95}");
            choice.put("message", message);
            choices.add(choice);
            response.put("choices", choices);
            return response;
        }

        @Override
        public String complete(String model, String prompt, Map<String, Object> options) { return "mock completion"; }

        @Override
        public List<double[]> embed(String model, List<String> texts) { return new ArrayList<>(); }

        @Override
        public String translate(String model, String text, String targetLanguage, String sourceLanguage) { return text; }

        @Override
        public String summarize(String model, String text, int maxLength) { return text; }

        @Override
        public boolean supportsStreaming() { return false; }

        @Override
        public boolean supportsFunctionCalling() { return false; }

        @Override
        public void chatStream(String model, List<Map<String, Object>> messages, Map<String, Object> options, StreamHandler handler) {}
    }

    private static class MockEmbeddingService implements EmbeddingService {
        @Override
        public float[] embed(String text) { return new float[384]; }

        @Override
        public List<float[]> embedBatch(List<String> texts) {
            List<float[]> result = new ArrayList<>();
            for (int i = 0; i < texts.size(); i++) {
                result.add(new float[384]);
            }
            return result;
        }

        @Override
        public int getDimension() { return 384; }

        @Override
        public String getModel() { return "mock-embedding"; }
    }

    private static class MockVectorStore implements VectorStore {
        private final Map<String, List<SearchResult>> mockResults = new HashMap<>();

        void addMockResult(String kbId, SearchResult result) {
            mockResults.computeIfAbsent(kbId, k -> new ArrayList<>()).add(result);
        }

        @Override
        public void insert(String id, float[] vector, Map<String, Object> metadata) {}

        @Override
        public void batchInsert(List<VectorData> vectors) {}

        @Override
        public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
            String kbId = filters != null ? (String) filters.get("kbId") : null;
            if (kbId != null && mockResults.containsKey(kbId)) {
                return mockResults.get(kbId);
            }
            return new ArrayList<>();
        }

        @Override
        public void delete(String id) {}

        @Override
        public void deleteByMetadata(Map<String, Object> filters) {}

        @Override
        public int getDimension() { return 384; }

        @Override
        public long count() { return 0; }

        @Override
        public void clear() {}
    }

    private static class MockKnowledgeBaseService implements KnowledgeBaseService {
        @Override
        public KnowledgeBase create(KnowledgeBaseCreateRequest request) { return null; }

        @Override
        public List<KnowledgeBase> listByOwner(String ownerId) { return new ArrayList<>(); }

        @Override
        public List<KnowledgeBase> listPublic() { return new ArrayList<>(); }

        @Override
        public boolean exists(String kbId) { return true; }

        @Override
        public KnowledgeBase get(String kbId) { return null; }

        @Override
        public KnowledgeBase update(String kbId, KnowledgeBaseUpdateRequest request) { return null; }

        @Override
        public void delete(String kbId) {}

        @Override
        public Document addDocument(String kbId, DocumentCreateRequest request) { return null; }

        @Override
        public List<Document> addDocuments(String kbId, List<DocumentCreateRequest> requests) { return new ArrayList<>(); }

        @Override
        public Document getDocument(String kbId, String docId) { return null; }

        @Override
        public void deleteDocument(String kbId, String docId) {}

        @Override
        public List<Document> listDocuments(String kbId) { return new ArrayList<>(); }

        @Override
        public List<KnowledgeSearchResult> search(String kbId, KnowledgeSearchRequest request) { return new ArrayList<>(); }

        @Override
        public void rebuildIndex(String kbId) {}

        @Override
        public IndexStatus getIndexStatus(String kbId) { return new IndexStatus(); }

        @Override
        public boolean hasPermission(String kbId, String userId, String permission) { return true; }

        @Override
        public void grantPermission(String kbId, String userId, String permission) {}

        @Override
        public void revokePermission(String kbId, String userId) {}
    }
}
