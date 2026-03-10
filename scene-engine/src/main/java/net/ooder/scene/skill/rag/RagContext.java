package net.ooder.scene.skill.rag;

import java.util.List;
import java.util.Map;

/**
 * RAG 上下文
 *
 * @author ooder
 * @since 2.3
 */
public class RagContext {

    /** 用户查询 */
    private String query;

    /** 知识库ID */
    private String kbId;

    /** 返回结果数量 */
    private int topK;

    /** 检索类型：KEYWORD, SEMANTIC, HYBRID */
    private String retrievalType;

    /** 相似度阈值 */
    private float threshold;

    /** 过滤条件 */
    private Map<String, Object> filters;

    /** 扩展参数 */
    private Map<String, Object> params;

    public RagContext() {
        this.topK = 5;
        this.retrievalType = "HYBRID";
        this.threshold = 0.7f;
    }

    public RagContext(String query, String kbId) {
        this();
        this.query = query;
        this.kbId = kbId;
    }

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }

    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }

    public String getRetrievalType() { return retrievalType; }
    public void setRetrievalType(String retrievalType) { this.retrievalType = retrievalType; }

    public float getThreshold() { return threshold; }
    public void setThreshold(float threshold) { this.threshold = threshold; }

    public Map<String, Object> getFilters() { return filters; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
