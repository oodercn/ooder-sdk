package net.ooder.spi.knowledge;

import java.util.List;

/**
 * 知识库服务 SPI
 * 提供文档上传、检索、管理等功能
 */
public interface KnowledgeService {

    /**
     * 上传文档到知识库
     *
     * @param userId   用户ID
     * @param title    文档标题
     * @param content  文档内容
     * @param dataType 数据类型
     * @return 知识文档
     */
    KnowledgeDocument uploadDocument(String userId, String title, String content, String dataType);

    /**
     * 根据ID获取文档
     *
     * @param docId 文档ID
     * @return 知识文档
     */
    KnowledgeDocument getDocument(String docId);

    /**
     * 搜索文档
     *
     * @param query 查询关键词
     * @param limit 返回数量限制
     * @return 文档ID列表
     */
    List<String> search(String query, int limit);

    /**
     * 删除文档
     *
     * @param docId 文档ID
     * @return 是否成功
     */
    boolean deleteDocument(String docId);

    /**
     * 更新文档
     *
     * @param docId   文档ID
     * @param title   新标题
     * @param content 新内容
     * @return 更新后的文档
     */
    KnowledgeDocument updateDocument(String docId, String title, String content);
}
