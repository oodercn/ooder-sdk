package net.ooder.scene.skill.knowledge;

import net.ooder.scene.skill.knowledge.impl.KnowledgeCapabilityImpl;
import net.ooder.scene.skill.rag.KnowledgeBaseConfig;
import net.ooder.scene.skill.vector.EmbeddingService;
import net.ooder.scene.skill.vector.SearchResult;
import net.ooder.scene.skill.vector.VectorData;
import net.ooder.scene.skill.vector.VectorStore;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Knowledge Capability 单元测试
 */
public class KnowledgeCapabilityTest {

    private KnowledgeCapabilityImpl capability;
    private MockKnowledgeBaseService kbService;
    private MockEmbeddingService embeddingService;
    private MockVectorStore vectorStore;

    @BeforeEach
    public void setUp() {
        kbService = new MockKnowledgeBaseService();
        embeddingService = new MockEmbeddingService();
        vectorStore = new MockVectorStore();
        capability = new KnowledgeCapabilityImpl(kbService, embeddingService, vectorStore);
    }

    @AfterEach
    public void tearDown() {
        capability.clearCache(null);
    }

    @Test
    public void testKnowledgeLayerValues() {
        assertEquals(3, KnowledgeCapability.KnowledgeLayer.values().length);
        assertNotNull(KnowledgeCapability.KnowledgeLayer.GENERAL);
        assertNotNull(KnowledgeCapability.KnowledgeLayer.PROFESSIONAL);
        assertNotNull(KnowledgeCapability.KnowledgeLayer.SCENE);
    }

    @Test
    public void testKnowledgeLayerPriority() {
        assertTrue(KnowledgeCapability.KnowledgeLayer.SCENE.getPriority() > 
                   KnowledgeCapability.KnowledgeLayer.PROFESSIONAL.getPriority());
        assertTrue(KnowledgeCapability.KnowledgeLayer.PROFESSIONAL.getPriority() > 
                   KnowledgeCapability.KnowledgeLayer.GENERAL.getPriority());
    }

    @Test
    public void testKnowledgeLayerFromCode() {
        assertEquals(KnowledgeCapability.KnowledgeLayer.GENERAL, 
                     KnowledgeCapability.KnowledgeLayer.fromCode("general"));
        assertEquals(KnowledgeCapability.KnowledgeLayer.PROFESSIONAL, 
                     KnowledgeCapability.KnowledgeLayer.fromCode("professional"));
        assertEquals(KnowledgeCapability.KnowledgeLayer.SCENE, 
                     KnowledgeCapability.KnowledgeLayer.fromCode("scene"));
        assertEquals(KnowledgeCapability.KnowledgeLayer.SCENE, 
                     KnowledgeCapability.KnowledgeLayer.fromCode("invalid"));
        assertEquals(KnowledgeCapability.KnowledgeLayer.SCENE, 
                     KnowledgeCapability.KnowledgeLayer.fromCode(null));
    }

    @Test
    public void testCapabilityBasicInfo() {
        assertEquals("KnowledgeCapability", capability.getName());
        assertEquals("2.3.1", capability.getVersion());
    }

    @Test
    public void testRegisterKnowledgeBase() {
        KnowledgeBaseConfig config = new KnowledgeBaseConfig();
        config.setKbId("kb-1");
        config.setName("Test KB");

        capability.registerKnowledgeBase("kb-1", KnowledgeCapability.KnowledgeLayer.SCENE, config);

        assertEquals(1, capability.getTotalKnowledgeBases());
        assertEquals(config, capability.getKnowledgeBaseConfig("kb-1"));
        assertTrue(capability.getLayerKnowledgeBases(KnowledgeCapability.KnowledgeLayer.SCENE).contains("kb-1"));
    }

    @Test
    public void testUnregisterKnowledgeBase() {
        KnowledgeBaseConfig config = new KnowledgeBaseConfig();
        config.setKbId("kb-1");

        capability.registerKnowledgeBase("kb-1", KnowledgeCapability.KnowledgeLayer.SCENE, config);
        assertEquals(1, capability.getTotalKnowledgeBases());

        capability.unregisterKnowledgeBase("kb-1");
        assertEquals(0, capability.getTotalKnowledgeBases());
        assertNull(capability.getKnowledgeBaseConfig("kb-1"));
    }

    @Test
    public void testUnregisterNullKbId() {
        int count = capability.getTotalKnowledgeBases();
        capability.unregisterKnowledgeBase(null);
        assertEquals(count, capability.getTotalKnowledgeBases());
    }

    @Test
    public void testGetLayerKnowledgeBasesEmpty() {
        List<String> kbs = capability.getLayerKnowledgeBases(KnowledgeCapability.KnowledgeLayer.GENERAL);
        assertNotNull(kbs);
        assertTrue(kbs.isEmpty());
    }

    @Test
    public void testClearCache() {
        capability.registerKnowledgeBase("kb-1", KnowledgeCapability.KnowledgeLayer.SCENE, new KnowledgeBaseConfig());
        
        capability.retrieve("test query", KnowledgeCapability.KnowledgeLayer.SCENE, null);
        assertTrue(capability.getCacheSize() > 0);

        capability.clearCache(null);
        assertEquals(0, capability.getCacheSize());
    }

    @Test
    public void testClearAllCache() {
        capability.registerKnowledgeBase("kb-1", KnowledgeCapability.KnowledgeLayer.SCENE, new KnowledgeBaseConfig());
        capability.registerKnowledgeBase("kb-2", KnowledgeCapability.KnowledgeLayer.PROFESSIONAL, new KnowledgeBaseConfig());
        
        capability.retrieve("query1", KnowledgeCapability.KnowledgeLayer.SCENE, null);
        capability.retrieve("query2", KnowledgeCapability.KnowledgeLayer.PROFESSIONAL, null);

        capability.clearCache(null);
        assertEquals(0, capability.getCacheSize());
    }

    @Test
    public void testKnowledgeResultSuccess() {
        List<KnowledgeCapability.RetrievedItem> items = new ArrayList<>();
        KnowledgeCapability.RetrievedItem item = new KnowledgeCapability.RetrievedItem();
        item.setKbId("kb-1");
        item.setScore(0.9f);
        items.add(item);

        KnowledgeCapability.KnowledgeResult result = KnowledgeCapability.KnowledgeResult.success("query", items);
        
        assertTrue(result.isSuccess());
        assertEquals("query", result.getQuery());
        assertEquals(1, result.getTotalCount());
        assertEquals(0.9f, result.getMaxScore(), 0.001);
        assertTrue(result.hasResults());
    }

    @Test
    public void testKnowledgeResultFailure() {
        KnowledgeCapability.KnowledgeResult result = KnowledgeCapability.KnowledgeResult.failure("Error occurred");
        
        assertFalse(result.isSuccess());
        assertEquals("Error occurred", result.getErrorMessage());
        assertFalse(result.hasResults());
    }

    @Test
    public void testRetrievedItem() {
        KnowledgeCapability.RetrievedItem item = new KnowledgeCapability.RetrievedItem();
        item.setKbId("kb-1");
        item.setDocId("doc-1");
        item.setChunkId("chunk-1");
        item.setTitle("Test Document");
        item.setContent("Test content");
        item.setScore(0.85f);
        item.setLayer(KnowledgeCapability.KnowledgeLayer.SCENE);

        assertEquals("kb-1", item.getKbId());
        assertEquals("doc-1", item.getDocId());
        assertEquals("chunk-1", item.getChunkId());
        assertEquals("Test Document", item.getTitle());
        assertEquals("Test content", item.getContent());
        assertEquals(0.85f, item.getScore(), 0.001);
        assertEquals(KnowledgeCapability.KnowledgeLayer.SCENE, item.getLayer());
    }

    @Test
    public void testRetrieveWithNoKnowledgeBases() {
        KnowledgeCapability.KnowledgeResult result = 
            capability.retrieve("test query", KnowledgeCapability.KnowledgeLayer.SCENE, null);
        
        assertTrue(result.isSuccess());
        assertFalse(result.hasResults());
    }

    @Test
    public void testRetrieveWithRegisteredKb() {
        KnowledgeBaseConfig config = new KnowledgeBaseConfig();
        config.setKbId("kb-1");
        config.setSimilarityThreshold(0.5f);
        config.setMaxResults(5);

        capability.registerKnowledgeBase("kb-1", KnowledgeCapability.KnowledgeLayer.SCENE, config);
        
        vectorStore.addMockResult("kb-1", createMockSearchResult("doc-1", "Test content", 0.9f));

        KnowledgeCapability.KnowledgeResult result = 
            capability.retrieve("test query", KnowledgeCapability.KnowledgeLayer.SCENE, null);
        
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCrossLayerRetrieve() {
        capability.registerKnowledgeBase("kb-1", KnowledgeCapability.KnowledgeLayer.SCENE, new KnowledgeBaseConfig());
        capability.registerKnowledgeBase("kb-2", KnowledgeCapability.KnowledgeLayer.PROFESSIONAL, new KnowledgeBaseConfig());

        List<KnowledgeCapability.KnowledgeLayer> layers = Arrays.asList(
            KnowledgeCapability.KnowledgeLayer.SCENE,
            KnowledgeCapability.KnowledgeLayer.PROFESSIONAL
        );

        KnowledgeCapability.KnowledgeResult result = 
            capability.crossLayerRetrieve("test query", layers, null);
        
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCrossLayerRetrieveWithEmptyLayers() {
        KnowledgeCapability.KnowledgeResult result = 
            capability.crossLayerRetrieve("test query", null, null);
        
        assertTrue(result.isSuccess());
    }

    @Test
    public void testKnowledgeBaseConfigDefaults() {
        KnowledgeBaseConfig config = new KnowledgeBaseConfig();
        
        assertEquals(1.0f, config.getWeight(), 0.001);
        assertEquals(0.7f, config.getSimilarityThreshold(), 0.001);
        assertEquals(5, config.getMaxResults());
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

    private static class MockKnowledgeBaseService implements KnowledgeBaseService {
        private final Map<String, KnowledgeBase> kbs = new HashMap<>();

        @Override
        public KnowledgeBase create(KnowledgeBaseCreateRequest request) {
            String kbId = "kb-" + System.currentTimeMillis();
            KnowledgeBase kb = new KnowledgeBase(kbId, request.getName(), request.getOwnerId());
            kbs.put(kb.getKbId(), kb);
            return kb;
        }

        @Override
        public List<KnowledgeBase> listByOwner(String ownerId) {
            return new ArrayList<>();
        }

        @Override
        public List<KnowledgeBase> listPublic() {
            return new ArrayList<>();
        }

        @Override
        public boolean exists(String kbId) {
            return kbs.containsKey(kbId);
        }

        @Override
        public KnowledgeBase get(String kbId) {
            return kbs.get(kbId);
        }

        @Override
        public KnowledgeBase update(String kbId, KnowledgeBaseUpdateRequest request) {
            return kbs.get(kbId);
        }

        @Override
        public void delete(String kbId) {
            kbs.remove(kbId);
        }

        @Override
        public Document addDocument(String kbId, DocumentCreateRequest request) {
            return null;
        }

        @Override
        public List<Document> addDocuments(String kbId, List<DocumentCreateRequest> requests) {
            return new ArrayList<>();
        }

        @Override
        public Document getDocument(String kbId, String docId) {
            return null;
        }

        @Override
        public void deleteDocument(String kbId, String docId) {
        }

        @Override
        public List<Document> listDocuments(String kbId) {
            return new ArrayList<>();
        }

        @Override
        public List<KnowledgeSearchResult> search(String kbId, KnowledgeSearchRequest request) {
            return new ArrayList<>();
        }

        @Override
        public void rebuildIndex(String kbId) {
        }

        @Override
        public IndexStatus getIndexStatus(String kbId) {
            return new IndexStatus();
        }

        @Override
        public boolean hasPermission(String kbId, String userId, String permission) {
            return true;
        }

        @Override
        public void grantPermission(String kbId, String userId, String permission) {
        }

        @Override
        public void revokePermission(String kbId, String userId) {
        }
    }

    private static class MockEmbeddingService implements EmbeddingService {
        @Override
        public float[] embed(String text) {
            return new float[384];
        }

        @Override
        public List<float[]> embedBatch(List<String> texts) {
            List<float[]> result = new ArrayList<>();
            for (int i = 0; i < texts.size(); i++) {
                result.add(new float[384]);
            }
            return result;
        }

        @Override
        public int getDimension() {
            return 384;
        }

        @Override
        public String getModel() {
            return "mock-embedding";
        }
    }

    private static class MockVectorStore implements VectorStore {
        private final Map<String, List<SearchResult>> mockResults = new HashMap<>();

        void addMockResult(String kbId, SearchResult result) {
            mockResults.computeIfAbsent(kbId, k -> new ArrayList<>()).add(result);
        }

        @Override
        public void insert(String id, float[] vector, Map<String, Object> metadata) {
        }

        @Override
        public void batchInsert(List<VectorData> vectors) {
        }

        @Override
        public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
            String kbId = filters != null ? (String) filters.get("kbId") : null;
            if (kbId != null && mockResults.containsKey(kbId)) {
                return mockResults.get(kbId);
            }
            return new ArrayList<>();
        }

        @Override
        public void delete(String id) {
        }

        @Override
        public void deleteByMetadata(Map<String, Object> filters) {
        }

        @Override
        public int getDimension() {
            return 384;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void clear() {
        }
    }
}
