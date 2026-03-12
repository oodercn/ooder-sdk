package net.ooder.skills.api.rag;

import java.util.List;
import java.util.Map;

/**
 * RAG 配置
 *
 * <p>定义技能知识库的 RAG 检索配置</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class RagConfig {

    private String indexId;
    private String indexType = "hnsw";
    private List<DataSource> dataSources;
    private ChunkConfig chunkConfig;
    private EmbeddingConfig embeddingConfig;
    private SearchParams searchParams;

    public String getIndexId() { return indexId; }
    public void setIndexId(String indexId) { this.indexId = indexId; }

    public String getIndexType() { return indexType; }
    public void setIndexType(String indexType) { this.indexType = indexType; }

    public List<DataSource> getDataSources() { return dataSources; }
    public void setDataSources(List<DataSource> dataSources) { this.dataSources = dataSources; }

    public ChunkConfig getChunkConfig() { return chunkConfig; }
    public void setChunkConfig(ChunkConfig chunkConfig) { this.chunkConfig = chunkConfig; }

    public EmbeddingConfig getEmbeddingConfig() { return embeddingConfig; }
    public void setEmbeddingConfig(EmbeddingConfig embeddingConfig) { this.embeddingConfig = embeddingConfig; }

    public SearchParams getSearchParams() { return searchParams; }
    public void setSearchParams(SearchParams searchParams) { this.searchParams = searchParams; }

    /**
     * 数据源配置
     */
    public static class DataSource {
        private String type;
        private String content;
        private String path;
        private String encoding = "utf-8";
        private String pattern;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public String getEncoding() { return encoding; }
        public void setEncoding(String encoding) { this.encoding = encoding; }

        public String getPattern() { return pattern; }
        public void setPattern(String pattern) { this.pattern = pattern; }
    }

    /**
     * 文档切分配置
     */
    public static class ChunkConfig {
        private String strategy = "recursive";
        private int chunkSize = 1000;
        private int chunkOverlap = 200;

        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }

        public int getChunkSize() { return chunkSize; }
        public void setChunkSize(int chunkSize) { this.chunkSize = chunkSize; }

        public int getChunkOverlap() { return chunkOverlap; }
        public void setChunkOverlap(int chunkOverlap) { this.chunkOverlap = chunkOverlap; }
    }

    /**
     * 嵌入模型配置
     */
    public static class EmbeddingConfig {
        private String model = "text-embedding-3-small";
        private int dimensions = 1536;

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public int getDimensions() { return dimensions; }
        public void setDimensions(int dimensions) { this.dimensions = dimensions; }
    }

    /**
     * 检索参数
     */
    public static class SearchParams {
        private int topK = 5;
        private double scoreThreshold = 0.7;

        public int getTopK() { return topK; }
        public void setTopK(int topK) { this.topK = topK; }

        public double getScoreThreshold() { return scoreThreshold; }
        public void setScoreThreshold(double scoreThreshold) { this.scoreThreshold = scoreThreshold; }
    }
}
