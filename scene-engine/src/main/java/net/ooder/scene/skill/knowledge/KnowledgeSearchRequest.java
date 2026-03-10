package net.ooder.scene.skill.knowledge;

import java.util.List;
import java.util.Map;

/**
 * 知识库搜索请求
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeSearchRequest {

    /** 知识库ID */
    private String kbId;

    /** 知识库ID列表 */
    private List<String> kbIds;

    /** 搜索查询 */
    private String query;

    /** 搜索类型：KEYWORD, SEMANTIC, HYBRID */
    private String searchType;

    /** 返回结果数量 */
    private int topK;

    /** 相似度阈值 */
    private float threshold;

    /** 标签过滤 */
    private List<String> tags;

    /** 过滤条件 */
    private Map<String, Object> filters;

    /** 搜索参数 */
    private Map<String, Object> params;

    public KnowledgeSearchRequest() {
        this.topK = 10;
        this.searchType = "HYBRID";
        this.threshold = 0.7f;
    }

    // Getters and Setters
    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }

    public List<String> getKbIds() { return kbIds; }
    public void setKbIds(List<String> kbIds) { this.kbIds = kbIds; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }

    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }

    public float getThreshold() { return threshold; }
    public void setThreshold(float threshold) { this.threshold = threshold; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Map<String, Object> getFilters() { return filters; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
