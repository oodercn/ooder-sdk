package net.ooder.scene.skill.vector;

import java.util.Map;

/**
 * 向量搜索结果
 * 包含相似度分数和元数据信息
 *
 * @author ooder
 * @since 2.3
 */
public class SearchResult {

    /** 向量标识 */
    private String id;

    /** 相似度分数 (0-1) */
    private float score;

    /** 元数据信息 */
    private Map<String, Object> metadata;

    /** 向量数据（可选） */
    private float[] vector;

    /** 内容 */
    private String content;

    public SearchResult() {}

    public SearchResult(String id, float score, Map<String, Object> metadata) {
        this.id = id;
        this.score = score;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
