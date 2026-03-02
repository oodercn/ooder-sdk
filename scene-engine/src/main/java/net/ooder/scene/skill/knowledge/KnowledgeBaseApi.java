package net.ooder.scene.skill.knowledge;

import java.util.List;

/**
 * 知识库接口
 * 提供知识库管理、文档管理、搜索等功能
 *
 * <p>实现类需要提供知识库的完整生命周期管理</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface KnowledgeBaseApi {
    
    /**
     * 创建知识库
     * @param request 创建请求
     * @return 创建的知识库
     */
    KnowledgeBase create(KnowledgeBaseCreateRequest request);
    
    /**
     * 获取知识库
     * @param kbId 知识库ID
     * @return 知识库信息
     */
    KnowledgeBase get(String kbId);
    
    /**
     * 更新知识库
     * @param kbId 知识库ID
     * @param request 更新请求
     * @return 更新后的知识库
     */
    KnowledgeBase update(String kbId, KnowledgeBaseUpdateRequest request);
    
    /**
     * 删除知识库
     * @param kbId 知识库ID
     */
    void delete(String kbId);
    
    /**
     * 列出知识库
     * @param ownerId 所有者ID
     * @return 知识库列表
     */
    List<KnowledgeBase> list(String ownerId);
    
    /**
     * 添加文档
     * @param kbId 知识库ID
     * @param request 文档创建请求
     * @return 创建的文档
     */
    Document addDocument(String kbId, DocumentCreateRequest request);
    
    /**
     * 删除文档
     * @param kbId 知识库ID
     * @param docId 文档ID
     */
    void deleteDocument(String kbId, String docId);
    
    /**
     * 获取文档
     * @param kbId 知识库ID
     * @param docId 文档ID
     * @return 文档信息
     */
    Document getDocument(String kbId, String docId);
    
    /**
     * 列出知识库中的所有文档
     * @param kbId 知识库ID
     * @return 文档列表
     */
    List<Document> listDocuments(String kbId);
    
    /**
     * 搜索知识库
     * @param request 搜索请求
     * @return 搜索结果
     */
    List<KnowledgeSearchResult> search(KnowledgeSearchRequest request);
    
    /**
     * 重建索引
     * @param kbId 知识库ID
     */
    void rebuildIndex(String kbId);
    
    /**
     * 获取索引状态
     * @param kbId 知识库ID
     * @return 索引状态
     */
    IndexStatus getIndexStatus(String kbId);
}
