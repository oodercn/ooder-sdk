package net.ooder.scene.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Scene Engine 配置属性
 * <p>统一的配置入口，替代分散的 Properties 类</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
@ConfigurationProperties(prefix = "se")
public class SceneEngineProperties {

    /**
     * 对话服务配置
     */
    private ConversationProperties conversation = new ConversationProperties();

    /**
     * LLM 配置
     */
    private LlmProperties llm = new LlmProperties();

    /**
     * 知识库配置
     */
    private KnowledgeProperties knowledge = new KnowledgeProperties();

    /**
     * Skill 配置
     */
    private SkillProperties skill = new SkillProperties();

    // Getters and Setters
    public ConversationProperties getConversation() {
        return conversation;
    }

    public void setConversation(ConversationProperties conversation) {
        this.conversation = conversation;
    }

    public LlmProperties getLlm() {
        return llm;
    }

    public void setLlm(LlmProperties llm) {
        this.llm = llm;
    }

    public KnowledgeProperties getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(KnowledgeProperties knowledge) {
        this.knowledge = knowledge;
    }

    public SkillProperties getSkill() {
        return skill;
    }

    public void setSkill(SkillProperties skill) {
        this.skill = skill;
    }

    /**
     * 对话服务配置
     */
    public static class ConversationProperties {
        private boolean enabled = true;
        private StorageProperties storage = new StorageProperties();
        private boolean autoLearn = false;
        private int maxHistory = 100;
        private AuditProperties audit = new AuditProperties();
        private KnowledgeUpdateProperties knowledge = new KnowledgeUpdateProperties();

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public StorageProperties getStorage() { return storage; }
        public void setStorage(StorageProperties storage) { this.storage = storage; }

        public boolean isAutoLearn() { return autoLearn; }
        public void setAutoLearn(boolean autoLearn) { this.autoLearn = autoLearn; }

        public int getMaxHistory() { return maxHistory; }
        public void setMaxHistory(int maxHistory) { this.maxHistory = maxHistory; }

        public AuditProperties getAudit() { return audit; }
        public void setAudit(AuditProperties audit) { this.audit = audit; }

        public KnowledgeUpdateProperties getKnowledge() { return knowledge; }
        public void setKnowledge(KnowledgeUpdateProperties knowledge) { this.knowledge = knowledge; }
    }

    /**
     * 存储配置
     */
    public static class StorageProperties {
        private String type = "file";
        private String path = System.getProperty("user.home") + "/.ooder/data/conversations";

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }

    /**
     * 审计配置
     */
    public static class AuditProperties {
        private boolean enabled = true;
        private boolean includeToolCalls = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public boolean isIncludeToolCalls() { return includeToolCalls; }
        public void setIncludeToolCalls(boolean includeToolCalls) { this.includeToolCalls = includeToolCalls; }
    }

    /**
     * 知识更新配置
     */
    public static class KnowledgeUpdateProperties {
        private boolean autoUpdate = false;
        private int minContentLength = 50;

        public boolean isAutoUpdate() { return autoUpdate; }
        public void setAutoUpdate(boolean autoUpdate) { this.autoUpdate = autoUpdate; }

        public int getMinContentLength() { return minContentLength; }
        public void setMinContentLength(int minContentLength) { this.minContentLength = minContentLength; }
    }

    /**
     * LLM 配置
     */
    public static class LlmProperties {
        private String provider = "openai";
        private String apiKey;
        private String model = "gpt-4";
        private double temperature = 0.7;
        private int maxTokens = 2000;

        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    }

    /**
     * 知识库配置
     */
    public static class KnowledgeProperties {
        private VectorStoreProperties vectorStore = new VectorStoreProperties();
        private ChunkProperties chunk = new ChunkProperties();
        private PersistenceProperties persistence = new PersistenceProperties();

        public VectorStoreProperties getVectorStore() { return vectorStore; }
        public void setVectorStore(VectorStoreProperties vectorStore) { this.vectorStore = vectorStore; }

        public ChunkProperties getChunk() { return chunk; }
        public void setChunk(ChunkProperties chunk) { this.chunk = chunk; }

        public PersistenceProperties getPersistence() { return persistence; }
        public void setPersistence(PersistenceProperties persistence) { this.persistence = persistence; }
    }

    /**
     * 持久化配置
     */
    public static class PersistenceProperties {
        private String type = "json";
        private String basePath = System.getProperty("user.home") + "/.ooder/data/knowledge";
        private boolean autoSave = true;
        private long saveIntervalMs = 5000;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getBasePath() { return basePath; }
        public void setBasePath(String basePath) { this.basePath = basePath; }

        public boolean isAutoSave() { return autoSave; }
        public void setAutoSave(boolean autoSave) { this.autoSave = autoSave; }

        public long getSaveIntervalMs() { return saveIntervalMs; }
        public void setSaveIntervalMs(long saveIntervalMs) { this.saveIntervalMs = saveIntervalMs; }
    }

    /**
     * 向量存储配置
     */
    public static class VectorStoreProperties {
        private String type = "memory";
        private int dimension = 1536;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public int getDimension() { return dimension; }
        public void setDimension(int dimension) { this.dimension = dimension; }
    }

    /**
     * 分块配置
     */
    public static class ChunkProperties {
        private int size = 500;
        private int overlap = 50;

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public int getOverlap() { return overlap; }
        public void setOverlap(int overlap) { this.overlap = overlap; }
    }

    /**
     * Skill 配置
     */
    public static class SkillProperties {
        private boolean enabled = true;
        private boolean autoRegister = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public boolean isAutoRegister() { return autoRegister; }
        public void setAutoRegister(boolean autoRegister) { this.autoRegister = autoRegister; }
    }
}
