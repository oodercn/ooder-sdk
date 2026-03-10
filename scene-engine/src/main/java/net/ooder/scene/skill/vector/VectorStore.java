package net.ooder.scene.skill.vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 向量存储接口
 * 定义向量数据的存储和检索能力
 *
 * <p>实现类需要提供向量数据的插入、检索、删除等功能</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface VectorStore {

    /**
     * 插入单个向量
     * @param id 向量唯一标识
     * @param vector 向量数据
     * @param metadata 元数据信息
     */
    void insert(String id, float[] vector, Map<String, Object> metadata);

    /**
     * 批量插入向量
     * @param vectors 向量数据列表
     */
    void batchInsert(List<VectorData> vectors);

    /**
     * 向量相似度搜索
     * @param queryVector 查询向量
     * @param topK 返回结果数量
     * @param filters 过滤条件
     * @return 搜索结果列表
     */
    List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters);

    /**
     * 删除向量
     * @param id 向量标识
     */
    void delete(String id);

    /**
     * 根据元数据删除向量
     * @param filters 过滤条件
     */
    void deleteByMetadata(Map<String, Object> filters);

    /**
     * 根据单个元数据键值对删除向量
     * @param key 元数据键
     * @param value 元数据值
     */
    default void deleteByMetadata(String key, Object value) {
        Map<String, Object> filters = new HashMap<>();
        filters.put(key, value);
        deleteByMetadata(filters);
    }

    /**
     * 获取向量维度
     * @return 向量维度
     */
    int getDimension();

    /**
     * 获取存储的向量数量
     * @return 向量数量
     */
    long count();

    /**
     * 清空所有向量
     */
    void clear();
}
