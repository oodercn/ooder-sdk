package net.ooder.scene.skill.knowledge;

import java.util.HashMap;
import java.util.Map;

/**
 * 知识库配置类
 *
 * <p>用于配置知识库检索参数，包括 TopK、相似度阈值、跨层检索等。</p>
 *
 * <h3>配置项说明：</h3>
 * <ul>
 *   <li><b>topK</b>: 返回的最相关文档数量，默认 5</li>
 *   <li><b>threshold</b>: 相似度阈值 (0.0-1.0)，默认 0.7</li>
 *   <li><b>crossLayerSearch</b>: 是否启用跨层检索，默认 false</li>
 *   <li><b>rerankEnabled</b>: 是否启用重排序，默认 true</li>
 *   <li><b>maxTokens</b>: 最大返回令牌数，默认 2000</li>
 * </ul>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class KnowledgeConfig {

    /**
     * 默认 TopK 值
     */
    public static final int DEFAULT_TOP_K = 5;

    /**
     * 默认相似度阈值
     */
    public static final double DEFAULT_THRESHOLD = 0.7;

    /**
     * 默认最大令牌数
     */
    public static final int DEFAULT_MAX_TOKENS = 2000;

    private Integer topK;
    private Double threshold;
    private Boolean crossLayerSearch;
    private Boolean rerankEnabled;
    private Integer maxTokens;
    private Map<String, Object> extendedConfig;

    public KnowledgeConfig() {
        this.topK = DEFAULT_TOP_K;
        this.threshold = DEFAULT_THRESHOLD;
        this.crossLayerSearch = false;
        this.rerankEnabled = true;
        this.maxTokens = DEFAULT_MAX_TOKENS;
        this.extendedConfig = new HashMap<>();
    }

    /**
     * 获取 TopK 值
     *
     * @return TopK 值，未设置返回默认值
     */
    public Integer getTopK() {
        return topK != null ? topK : DEFAULT_TOP_K;
    }

    /**
     * 设置 TopK 值
     *
     * @param topK TopK 值，范围 1-100
     */
    public void setTopK(Integer topK) {
        if (topK != null && (topK < 1 || topK > 100)) {
            throw new IllegalArgumentException("topK must be between 1 and 100");
        }
        this.topK = topK;
    }

    /**
     * 获取相似度阈值
     *
     * @return 相似度阈值，未设置返回默认值
     */
    public Double getThreshold() {
        return threshold != null ? threshold : DEFAULT_THRESHOLD;
    }

    /**
     * 设置相似度阈值
     *
     * @param threshold 相似度阈值，范围 0.0-1.0
     */
    public void setThreshold(Double threshold) {
        if (threshold != null && (threshold < 0.0 || threshold > 1.0)) {
            throw new IllegalArgumentException("threshold must be between 0.0 and 1.0");
        }
        this.threshold = threshold;
    }

    /**
     * 是否启用跨层检索
     *
     * @return true 表示启用跨层检索
     */
    public Boolean getCrossLayerSearch() {
        return crossLayerSearch != null ? crossLayerSearch : false;
    }

    /**
     * 设置跨层检索开关
     *
     * @param crossLayerSearch 是否启用跨层检索
     */
    public void setCrossLayerSearch(Boolean crossLayerSearch) {
        this.crossLayerSearch = crossLayerSearch;
    }

    /**
     * 是否启用重排序
     *
     * @return true 表示启用重排序
     */
    public Boolean getRerankEnabled() {
        return rerankEnabled != null ? rerankEnabled : true;
    }

    /**
     * 设置重排序开关
     *
     * @param rerankEnabled 是否启用重排序
     */
    public void setRerankEnabled(Boolean rerankEnabled) {
        this.rerankEnabled = rerankEnabled;
    }

    /**
     * 获取最大令牌数
     *
     * @return 最大令牌数，未设置返回默认值
     */
    public Integer getMaxTokens() {
        return maxTokens != null ? maxTokens : DEFAULT_MAX_TOKENS;
    }

    /**
     * 设置最大令牌数
     *
     * @param maxTokens 最大令牌数，范围 100-8000
     */
    public void setMaxTokens(Integer maxTokens) {
        if (maxTokens != null && (maxTokens < 100 || maxTokens > 8000)) {
            throw new IllegalArgumentException("maxTokens must be between 100 and 8000");
        }
        this.maxTokens = maxTokens;
    }

    /**
     * 获取扩展配置
     *
     * @return 扩展配置映射
     */
    public Map<String, Object> getExtendedConfig() {
        return extendedConfig;
    }

    /**
     * 设置扩展配置
     *
     * @param extendedConfig 扩展配置映射
     */
    public void setExtendedConfig(Map<String, Object> extendedConfig) {
        this.extendedConfig = extendedConfig != null ? extendedConfig : new HashMap<>();
    }

    /**
     * 添加扩展配置项
     *
     * @param key 配置键
     * @param value 配置值
     */
    public void addExtendedConfig(String key, Object value) {
        if (this.extendedConfig == null) {
            this.extendedConfig = new HashMap<>();
        }
        this.extendedConfig.put(key, value);
    }

    /**
     * 获取扩展配置项
     *
     * @param key 配置键
     * @return 配置值
     */
    public Object getExtendedConfig(String key) {
        return this.extendedConfig != null ? this.extendedConfig.get(key) : null;
    }

    /**
     * 创建默认配置
     *
     * @return 默认配置实例
     */
    public static KnowledgeConfig defaultConfig() {
        return new KnowledgeConfig();
    }

    /**
     * 创建宽松配置（低阈值，高召回）
     *
     * @return 宽松配置实例
     */
    public static KnowledgeConfig lenientConfig() {
        KnowledgeConfig config = new KnowledgeConfig();
        config.setThreshold(0.5);
        config.setTopK(10);
        return config;
    }

    /**
     * 创建严格配置（高阈值，高精度）
     *
     * @return 严格配置实例
     */
    public static KnowledgeConfig strictConfig() {
        KnowledgeConfig config = new KnowledgeConfig();
        config.setThreshold(0.85);
        config.setTopK(3);
        return config;
    }

    @Override
    public String toString() {
        return "KnowledgeConfig{" +
            "topK=" + getTopK() +
            ", threshold=" + getThreshold() +
            ", crossLayerSearch=" + getCrossLayerSearch() +
            ", rerankEnabled=" + getRerankEnabled() +
            ", maxTokens=" + getMaxTokens() +
            ", extendedConfig=" + extendedConfig +
            '}';
    }
}
