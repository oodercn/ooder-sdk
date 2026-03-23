package net.ooder.sdk.api.scene;

import java.util.List;
import java.util.Optional;

public interface KnowledgeBindingManager {

    SceneKnowledgeBinding bind(String sceneGroupId, String knowledgeBaseId, KnowledgeBindingConfig config);

    void unbind(String sceneGroupId, String knowledgeBaseId);

    Optional<SceneKnowledgeBinding> getBinding(String sceneGroupId, String knowledgeBaseId);

    List<SceneKnowledgeBinding> getBindingsBySceneGroup(String sceneGroupId);

    List<SceneKnowledgeBinding> getBindingsByKnowledgeBase(String knowledgeBaseId);

    void updateBindingConfig(String sceneGroupId, String knowledgeBaseId, KnowledgeBindingConfig config);

    boolean isBound(String sceneGroupId, String knowledgeBaseId);

    void enableBinding(String sceneGroupId, String knowledgeBaseId);

    void disableBinding(String sceneGroupId, String knowledgeBaseId);

    List<SceneKnowledgeBinding> getActiveBindings(String sceneGroupId);

    void setBindingPriority(String sceneGroupId, String knowledgeBaseId, int priority);

    int getBindingPriority(String sceneGroupId, String knowledgeBaseId);

    class SceneKnowledgeBinding {
        private String bindingId;
        private String sceneGroupId;
        private String knowledgeBaseId;
        private KnowledgeBindingConfig config;
        private BindingStatus status;
        private int priority;
        private long createTime;
        private long updateTime;

        public enum BindingStatus {
            ACTIVE,
            INACTIVE,
            PENDING,
            ERROR
        }

        public SceneKnowledgeBinding() {
            this.createTime = System.currentTimeMillis();
            this.updateTime = this.createTime;
            this.status = BindingStatus.ACTIVE;
            this.priority = 0;
        }

        public SceneKnowledgeBinding(String sceneGroupId, String knowledgeBaseId) {
            this();
            this.sceneGroupId = sceneGroupId;
            this.knowledgeBaseId = knowledgeBaseId;
        }

        public String getBindingId() { return bindingId; }
        public void setBindingId(String bindingId) { this.bindingId = bindingId; }

        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

        public String getKnowledgeBaseId() { return knowledgeBaseId; }
        public void setKnowledgeBaseId(String knowledgeBaseId) { this.knowledgeBaseId = knowledgeBaseId; }

        public KnowledgeBindingConfig getConfig() { return config; }
        public void setConfig(KnowledgeBindingConfig config) { this.config = config; }

        public BindingStatus getStatus() { return status; }
        public void setStatus(BindingStatus status) { this.status = status; }

        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }

        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }

        public long getUpdateTime() { return updateTime; }
        public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }

        public boolean isActive() {
            return status == BindingStatus.ACTIVE;
        }
    }

    class KnowledgeBindingConfig {
        private boolean enabled;
        private String accessMode;
        private int maxResults;
        private float minScore;
        private List<String> includedCollections;
        private List<String> excludedCollections;
        private java.util.Map<String, Object> extraConfig;

        public KnowledgeBindingConfig() {
            this.enabled = true;
            this.accessMode = "read";
            this.maxResults = 10;
            this.minScore = 0.5f;
            this.includedCollections = new java.util.ArrayList<>();
            this.excludedCollections = new java.util.ArrayList<>();
            this.extraConfig = new java.util.HashMap<>();
        }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getAccessMode() { return accessMode; }
        public void setAccessMode(String accessMode) { this.accessMode = accessMode; }

        public int getMaxResults() { return maxResults; }
        public void setMaxResults(int maxResults) { this.maxResults = maxResults; }

        public float getMinScore() { return minScore; }
        public void setMinScore(float minScore) { this.minScore = minScore; }

        public List<String> getIncludedCollections() { return includedCollections; }
        public void setIncludedCollections(List<String> includedCollections) { this.includedCollections = includedCollections; }

        public List<String> getExcludedCollections() { return excludedCollections; }
        public void setExcludedCollections(List<String> excludedCollections) { this.excludedCollections = excludedCollections; }

        public java.util.Map<String, Object> getExtraConfig() { return extraConfig; }
        public void setExtraConfig(java.util.Map<String, Object> extraConfig) { this.extraConfig = extraConfig; }
    }
}
