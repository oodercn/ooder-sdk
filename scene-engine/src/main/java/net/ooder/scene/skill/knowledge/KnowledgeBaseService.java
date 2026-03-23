package net.ooder.scene.skill.knowledge;

import java.util.List;

/**
 * 知识库管理服务接口
 *
 * <p>提供知识库的完整生命周期管理，包括：</p>
 * <ul>
 *   <li>知识库 CRUD 操作</li>
 *   <li>文档管理</li>
 *   <li>索引管理</li>
 *   <li>权限管理</li>
 * </ul>
 *
 * <p>架构层次：知识增强层 - 知识库管理</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface KnowledgeBaseService {
    
    // ========== 知识库管理 ==========
    
    /**
     * 创建知识库
     *
     * @param request 创建请求
     * @return 创建的知识库
     */
    KnowledgeBase create(KnowledgeBaseCreateRequest request);
    
    /**
     * 列出用户的知识库
     *
     * @param ownerId 所有者ID
     * @return 知识库列表
     */
    List<KnowledgeBase> listByOwner(String ownerId);
    
    /**
     * 列出所有公开知识库
     *
     * @return 知识库列表
     */
    List<KnowledgeBase> listPublic();
    
    /**
     * 检查知识库是否存在
     *
     * @param kbId 知识库ID
     * @return 是否存在
     */
    boolean exists(String kbId);
    
    /**
     * 获取知识库
     *
     * @param kbId 知识库ID
     * @return 知识库信息
     */
    KnowledgeBase get(String kbId);
    
    /**
     * 更新知识库
     *
     * @param kbId 知识库ID
     * @param request 更新请求
     * @return 更新后的知识库
     */
    KnowledgeBase update(String kbId, KnowledgeBaseUpdateRequest request);
    
    /**
     * 删除知识库
     *
     * @param kbId 知识库ID
     */
    void delete(String kbId);
    
    // ========== 文档管理 ==========
    
    /**
     * 添加文档
     *
     * @param kbId 知识库ID
     * @param request 文档创建请求
     * @return 创建的文档
     */
    Document addDocument(String kbId, DocumentCreateRequest request);
    
    /**
     * 批量添加文档
     *
     * @param kbId 知识库ID
     * @param requests 文档创建请求列表
     * @return 创建的文档列表
     */
    List<Document> addDocuments(String kbId, List<DocumentCreateRequest> requests);
    
    /**
     * 获取文档
     *
     * @param kbId 知识库ID
     * @param docId 文档ID
     * @return 文档信息
     */
    Document getDocument(String kbId, String docId);
    
    /**
     * 删除文档
     *
     * @param kbId 知识库ID
     * @param docId 文档ID
     */
    void deleteDocument(String kbId, String docId);
    
    /**
     * 列出知识库中的所有文档
     *
     * @param kbId 知识库ID
     * @return 文档列表
     */
    List<Document> listDocuments(String kbId);
    
    /**
     * 搜索知识库
     *
     * @param kbId 知识库ID
     * @param request 搜索请求
     * @return 搜索结果
     */
    List<KnowledgeSearchResult> search(String kbId, KnowledgeSearchRequest request);
    
    // ========== 索引管理 ==========
    
    /**
     * 重建索引
     *
     * @param kbId 知识库ID
     */
    void rebuildIndex(String kbId);
    
    /**
     * 获取索引状态
     *
     * @param kbId 知识库ID
     * @return 索引状态
     */
    IndexStatus getIndexStatus(String kbId);
    
    // ========== 权限管理 ==========
    
    /**
     * 检查用户是否有权限访问知识库
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @param permission 权限类型 (read/write/admin)
     * @return 是否有权限
     */
    boolean hasPermission(String kbId, String userId, String permission);
    
    /**
     * 授予用户权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @param permission 权限类型
     */
    void grantPermission(String kbId, String userId, String permission);
    
    /**
     * 撤销用户权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     */
    void revokePermission(String kbId, String userId);
    
    // ========== 统计聚合 ==========
    
    /**
     * 获取知识库统计数据
     *
     * @return 统计数据
     */
    KnowledgeBaseStats getStats();
    
    /**
     * 获取所有知识库
     *
     * @return 知识库列表
     */
    List<KnowledgeBase> listAll();
    
    /**
     * 获取场景绑定数量
     *
     * @param kbId 知识库ID
     * @return 绑定数量
     */
    int getBindingCount(String kbId);
}
