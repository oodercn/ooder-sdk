package net.ooder.scene.skill.knowledge;

import net.ooder.scene.skill.rag.KnowledgeBaseConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识能力接口
 * 
 * <p>作为独立Capability接入场景的知识检索服务</p>
 * <p>支持三层知识架构：</p>
 * <ul>
 *   <li>GENERAL - 通用知识层</li>
 *   <li>PROFESSIONAL - 专业知识层</li>
 *   <li>SCENE - 场景知识层</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface KnowledgeCapability {

    KnowledgeResult retrieve(String query, KnowledgeLayer layer, Map<String, Object> context);

    KnowledgeResult crossLayerRetrieve(String query, List<KnowledgeLayer> layers, Map<String, Object> context);

    void registerKnowledgeBase(String kbId, KnowledgeLayer layer, KnowledgeBaseConfig config);

    void unregisterKnowledgeBase(String kbId);

    KnowledgeBaseConfig getKnowledgeBaseConfig(String kbId);

    List<String> getLayerKnowledgeBases(KnowledgeLayer layer);

    void clearCache(String kbId);

    String getName();

    String getVersion();

    enum KnowledgeLayer {
        GENERAL("general", "通用知识层", 0),
        PROFESSIONAL("professional", "专业知识层", 1),
        SCENE("scene", "场景知识层", 2);

        private final String code;
        private final String name;
        private final int priority;

        KnowledgeLayer(String code, String name, int priority) {
            this.code = code;
            this.name = name;
            this.priority = priority;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public int getPriority() {
            return priority;
        }

        public static KnowledgeLayer fromCode(String code) {
            if (code == null) {
                return SCENE;
            }
            for (KnowledgeLayer layer : values()) {
                if (layer.code.equals(code)) {
                    return layer;
                }
            }
            return SCENE;
        }
    }

    class KnowledgeResult {
        private boolean success;
        private String query;
        private List<RetrievedItem> items;
        private KnowledgeLayer sourceLayer;
        private float maxScore;
        private float avgScore;
        private int totalCount;
        private long latencyMs;
        private String errorMessage;
        private Map<String, Object> metadata;

        public KnowledgeResult() {
            this.items = new ArrayList<>();
            this.metadata = new HashMap<>();
        }

        public static KnowledgeResult success(String query, List<RetrievedItem> items) {
            KnowledgeResult result = new KnowledgeResult();
            result.success = true;
            result.query = query;
            result.items = items != null ? items : new ArrayList<>();
            result.totalCount = result.items.size();
            if (!result.items.isEmpty()) {
                float max = 0;
                float sum = 0;
                for (RetrievedItem item : result.items) {
                    max = Math.max(max, item.getScore());
                    sum += item.getScore();
                }
                result.maxScore = max;
                result.avgScore = sum / result.items.size();
            }
            return result;
        }

        public static KnowledgeResult failure(String errorMessage) {
            KnowledgeResult result = new KnowledgeResult();
            result.success = false;
            result.errorMessage = errorMessage;
            return result;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public List<RetrievedItem> getItems() {
            return items;
        }

        public void setItems(List<RetrievedItem> items) {
            this.items = items != null ? items : new ArrayList<>();
            this.totalCount = this.items.size();
        }

        public KnowledgeLayer getSourceLayer() {
            return sourceLayer;
        }

        public void setSourceLayer(KnowledgeLayer sourceLayer) {
            this.sourceLayer = sourceLayer;
        }

        public float getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(float maxScore) {
            this.maxScore = maxScore;
        }

        public float getAvgScore() {
            return avgScore;
        }

        public void setAvgScore(float avgScore) {
            this.avgScore = avgScore;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public long getLatencyMs() {
            return latencyMs;
        }

        public void setLatencyMs(long latencyMs) {
            this.latencyMs = latencyMs;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }

        public boolean hasResults() {
            return items != null && !items.isEmpty();
        }
    }

    class RetrievedItem {
        private String kbId;
        private String docId;
        private String chunkId;
        private String title;
        private String content;
        private float score;
        private KnowledgeLayer layer;
        private Map<String, Object> metadata;

        public RetrievedItem() {
            this.metadata = new HashMap<>();
        }

        public String getKbId() {
            return kbId;
        }

        public void setKbId(String kbId) {
            this.kbId = kbId;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getChunkId() {
            return chunkId;
        }

        public void setChunkId(String chunkId) {
            this.chunkId = chunkId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }

        public KnowledgeLayer getLayer() {
            return layer;
        }

        public void setLayer(KnowledgeLayer layer) {
            this.layer = layer;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }
    }
}
