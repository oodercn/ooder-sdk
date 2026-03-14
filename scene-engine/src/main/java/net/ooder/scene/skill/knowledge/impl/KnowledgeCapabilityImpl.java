package net.ooder.scene.skill.knowledge.impl;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.KnowledgeCapability;
import net.ooder.scene.skill.rag.KnowledgeBaseConfig;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.SearchResult;
import net.ooder.scene.skill.vector.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 知识能力实现
 * 
 * <p>支持三层知识架构的知识检索服务</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class KnowledgeCapabilityImpl implements KnowledgeCapability {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeCapabilityImpl.class);
    private static final String VERSION = "2.3.1";

    private final KnowledgeBaseService kbService;
    private final SceneEmbeddingService embeddingService;
    private final VectorStore vectorStore;
    
    private final Map<String, KnowledgeBaseConfig> kbConfigs = new ConcurrentHashMap<>();
    private final Map<KnowledgeLayer, List<String>> layerKbs = new ConcurrentHashMap<>();
    private final Map<String, KnowledgeLayer> kbLayers = new ConcurrentHashMap<>();
    private final Map<String, List<RetrievedItem>> cache = new ConcurrentHashMap<>();

    public KnowledgeCapabilityImpl(KnowledgeBaseService kbService,
                                   SceneEmbeddingService embeddingService,
                                   VectorStore vectorStore) {
        this.kbService = kbService;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        
        for (KnowledgeLayer layer : KnowledgeLayer.values()) {
            layerKbs.put(layer, new ArrayList<>());
        }
    }

    @Override
    public KnowledgeResult retrieve(String query, KnowledgeLayer layer, Map<String, Object> context) {
        long startTime = System.currentTimeMillis();
        
        try {
            String cacheKey = buildCacheKey(query, layer, context);
            List<RetrievedItem> cached = cache.get(cacheKey);
            if (cached != null) {
                log.debug("Cache hit for query: {}", query);
                KnowledgeResult result = KnowledgeResult.success(query, cached);
                result.setSourceLayer(layer);
                result.setLatencyMs(System.currentTimeMillis() - startTime);
                return result;
            }

            List<String> kbIds = layerKbs.get(layer);
            if (kbIds == null || kbIds.isEmpty()) {
                log.debug("No knowledge bases registered for layer: {}", layer);
                return KnowledgeResult.success(query, new ArrayList<>());
            }

            float[] queryVector = embeddingService != null ? embeddingService.embed(query) : null;
            if (queryVector == null || queryVector.length == 0) {
                log.warn("Failed to embed query or embedding service is null");
                return KnowledgeResult.failure("Failed to embed query");
            }
            
            List<RetrievedItem> allItems = new ArrayList<>();
            
            for (String kbId : kbIds) {
                KnowledgeBaseConfig config = kbConfigs.get(kbId);
                if (config == null) {
                    continue;
                }

                Map<String, Object> filters = new HashMap<>();
                filters.put("kbId", kbId);
                if (context != null) {
                    filters.putAll(context);
                }

                List<SearchResult> results = vectorStore != null 
                    ? vectorStore.search(queryVector, config.getMaxResults(), filters) 
                    : null;
                if (results == null) {
                    results = Collections.emptyList();
                }

                for (SearchResult sr : results) {
                    if (sr.getScore() < config.getSimilarityThreshold()) {
                        continue;
                    }

                    Map<String, Object> metadata = sr.getMetadata();
                    if (metadata == null) {
                        metadata = new HashMap<>();
                    }

                    RetrievedItem item = new RetrievedItem();
                    item.setKbId(kbId);
                    item.setLayer(layer);
                    item.setDocId((String) metadata.get("docId"));
                    item.setChunkId((String) metadata.get("chunkId"));
                    item.setTitle((String) metadata.get("title"));
                    item.setContent(sr.getContent());
                    item.setScore(sr.getScore() * config.getWeight());
                    item.setMetadata(metadata);
                    
                    allItems.add(item);
                }
            }

            allItems.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));

            if (allItems.size() > 10) {
                allItems = allItems.subList(0, 10);
            }

            cache.put(cacheKey, allItems);

            KnowledgeResult result = KnowledgeResult.success(query, allItems);
            result.setSourceLayer(layer);
            result.setLatencyMs(System.currentTimeMillis() - startTime);
            
            log.info("Retrieved {} items from layer {} in {}ms", 
                allItems.size(), layer, result.getLatencyMs());
            
            return result;
        } catch (Exception e) {
            log.error("Failed to retrieve from layer: {}", layer, e);
            return KnowledgeResult.failure("Retrieve error: " + e.getMessage());
        }
    }

    @Override
    public KnowledgeResult crossLayerRetrieve(String query, List<KnowledgeLayer> layers, Map<String, Object> context) {
        long startTime = System.currentTimeMillis();
        
        if (layers == null || layers.isEmpty()) {
            layers = Arrays.asList(
                KnowledgeLayer.SCENE,
                KnowledgeLayer.PROFESSIONAL,
                KnowledgeLayer.GENERAL
            );
        }

        List<RetrievedItem> allItems = new ArrayList<>();
        KnowledgeResult bestResult = null;

        for (KnowledgeLayer layer : layers) {
            KnowledgeResult result = retrieve(query, layer, context);
            if (result.isSuccess() && result.hasResults()) {
                allItems.addAll(result.getItems());
                
                if (bestResult == null || result.getMaxScore() > bestResult.getMaxScore()) {
                    bestResult = result;
                }
            }
        }

        allItems.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));

        if (allItems.size() > 20) {
            allItems = allItems.subList(0, 20);
        }

        KnowledgeResult result = KnowledgeResult.success(query, allItems);
        result.setLatencyMs(System.currentTimeMillis() - startTime);
        
        if (bestResult != null) {
            result.setSourceLayer(bestResult.getSourceLayer());
        }
        
        log.info("Cross-layer retrieved {} items from {} layers in {}ms", 
            allItems.size(), layers.size(), result.getLatencyMs());
        
        return result;
    }

    @Override
    public void registerKnowledgeBase(String kbId, KnowledgeLayer layer, KnowledgeBaseConfig config) {
        if (kbId == null || layer == null) {
            log.warn("Invalid registration: kbId={}, layer={}", kbId, layer);
            return;
        }

        if (config == null) {
            config = new KnowledgeBaseConfig();
            config.setKbId(kbId);
        }

        kbConfigs.put(kbId, config);
        kbLayers.put(kbId, layer);
        
        List<String> layerKbList = layerKbs.computeIfAbsent(layer, k -> new ArrayList<>());
        if (!layerKbList.contains(kbId)) {
            layerKbList.add(kbId);
        }
        
        log.info("Registered knowledge base {} to layer {}", kbId, layer);
    }

    @Override
    public void unregisterKnowledgeBase(String kbId) {
        if (kbId == null) {
            return;
        }

        kbConfigs.remove(kbId);
        
        KnowledgeLayer layer = kbLayers.remove(kbId);
        if (layer != null) {
            List<String> layerKbList = layerKbs.get(layer);
            if (layerKbList != null) {
                layerKbList.remove(kbId);
            }
        }
        
        clearCache(kbId);
        
        log.info("Unregistered knowledge base: {}", kbId);
    }

    @Override
    public KnowledgeBaseConfig getKnowledgeBaseConfig(String kbId) {
        return kbConfigs.get(kbId);
    }

    @Override
    public List<String> getLayerKnowledgeBases(KnowledgeLayer layer) {
        List<String> kbIds = layerKbs.get(layer);
        return kbIds != null ? new ArrayList<>(kbIds) : new ArrayList<>();
    }

    @Override
    public void clearCache(String kbId) {
        if (kbId == null) {
            cache.clear();
            log.info("Cleared all cache");
        } else {
            cache.keySet().removeIf(key -> key.contains(kbId));
            log.info("Cleared cache for kb: {}", kbId);
        }
    }

    @Override
    public String getName() {
        return "KnowledgeCapability";
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    public int getCacheSize() {
        return cache.size();
    }

    public int getTotalKnowledgeBases() {
        return kbConfigs.size();
    }

    private String buildCacheKey(String query, KnowledgeLayer layer, Map<String, Object> context) {
        StringBuilder key = new StringBuilder();
        key.append(layer.getCode()).append(":").append(query.hashCode());
        if (context != null) {
            key.append(":").append(context.hashCode());
        }
        return key.toString();
    }
}
