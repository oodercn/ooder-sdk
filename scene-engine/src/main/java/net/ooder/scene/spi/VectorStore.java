package net.ooder.scene.spi;

import java.util.List;
import java.util.Map;

/**
 * 向量存储 SPI
 *
 * <p>向量数据库接口，用于 RAG 检索</p>
 *
 * <p>实现要求：</p>
 * <ul>
 *   <li>Tiny: 内存向量</li>
 *   <li>Small: Milvus Lite / Chroma</li>
 *   <li>Enterprise: 分布式向量库</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
public interface VectorStore {

    /**
     * 添加向量
     *
     * @param id 文档 ID
     * @param embedding 向量
     * @param metadata 元数据
     */
    void addVector(String id, float[] embedding, Map<String, Object> metadata);

    /**
     * 批量添加向量
     *
     * @param vectors 向量列表
     */
    void addVectors(List<VectorData> vectors);

    /**
     * 相似度搜索
     *
     * @param embedding 查询向量
     * @param topK 返回数量
     * @return 搜索结果
     */
    List<SearchResult> search(float[] embedding, int topK);

    /**
     * 带过滤条件的搜索
     *
     * @param embedding 查询向量
     * @param topK 返回数量
     * @param filter 过滤条件
     * @return 搜索结果
     */
    List<SearchResult> search(float[] embedding, int topK, Map<String, Object> filter);

    /**
     * 删除向量
     *
     * @param id 文档 ID
     */
    void deleteVector(String id);

    /**
     * 清空所有向量
     */
    void clear();

    /**
     * 获取向量数量
     *
     * @return 数量
     */
    int size();

    /**
     * 获取提供者类型
     *
     * @return 类型: tiny, small, enterprise
     */
    String getProviderType();

    /**
     * 向量数据
     */
    record VectorData(String id, float[] embedding, Map<String, Object> metadata) {}

    /**
     * 搜索结果
     */
    record SearchResult(String id, float score, Map<String, Object> metadata) {}
}
