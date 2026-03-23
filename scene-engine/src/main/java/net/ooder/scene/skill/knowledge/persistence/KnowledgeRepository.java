package net.ooder.scene.skill.knowledge.persistence;

import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.IndexStatus;
import net.ooder.scene.skill.knowledge.KnowledgeBase;

import java.util.List;
import java.util.Map;

/**
 * 知识库持久化接口
 *
 * <p>提供知识库数据的持久化能力，支持多种存储后端：</p>
 * <ul>
 *   <li>json - JSON文件存储（默认）</li>
 *   <li>memory - 内存存储（开发测试）</li>
 *   <li>sql - SQL数据库存储（生产环境）</li>
 * </ul>
 *
 * <p>架构层次：基础设施层 - 持久化</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface KnowledgeRepository {

    /**
     * 初始化仓库
     */
    void initialize();

    /**
     * 关闭仓库，释放资源
     */
    void close();

    /**
     * 获取存储类型
     *
     * @return 存储类型标识
     */
    String getStorageType();

    // ========== 知识库操作 ==========

    /**
     * 保存知识库
     *
     * @param kb 知识库实体
     */
    void saveKnowledgeBase(KnowledgeBase kb);

    /**
     * 根据ID查找知识库
     *
     * @param kbId 知识库ID
     * @return 知识库实体，不存在返回null
     */
    KnowledgeBase findKnowledgeBaseById(String kbId);

    /**
     * 查找所有知识库
     *
     * @return 知识库列表
     */
    List<KnowledgeBase> findAllKnowledgeBases();

    /**
     * 根据所有者查找知识库
     *
     * @param ownerId 所有者ID
     * @return 知识库列表
     */
    List<KnowledgeBase> findKnowledgeBasesByOwner(String ownerId);

    /**
     * 检查知识库是否存在
     *
     * @param kbId 知识库ID
     * @return 是否存在
     */
    boolean existsKnowledgeBase(String kbId);

    /**
     * 删除知识库
     *
     * @param kbId 知识库ID
     */
    void deleteKnowledgeBase(String kbId);

    // ========== 文档操作 ==========

    /**
     * 保存文档
     *
     * @param document 文档实体
     */
    void saveDocument(Document document);

    /**
     * 批量保存文档
     *
     * @param documents 文档列表
     */
    void saveDocuments(List<Document> documents);

    /**
     * 根据ID查找文档
     *
     * @param kbId 知识库ID
     * @param docId 文档ID
     * @return 文档实体，不存在返回null
     */
    Document findDocumentById(String kbId, String docId);

    /**
     * 查找知识库下的所有文档
     *
     * @param kbId 知识库ID
     * @return 文档列表
     */
    List<Document> findDocumentsByKnowledgeBase(String kbId);

    /**
     * 删除文档
     *
     * @param kbId 知识库ID
     * @param docId 文档ID
     */
    void deleteDocument(String kbId, String docId);

    /**
     * 删除知识库下的所有文档
     *
     * @param kbId 知识库ID
     */
    void deleteDocumentsByKnowledgeBase(String kbId);

    // ========== 索引状态操作 ==========

    /**
     * 保存索引状态
     *
     * @param status 索引状态
     */
    void saveIndexStatus(IndexStatus status);

    /**
     * 查找索引状态
     *
     * @param kbId 知识库ID
     * @return 索引状态，不存在返回null
     */
    IndexStatus findIndexStatus(String kbId);

    // ========== 权限操作 ==========

    /**
     * 保存权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @param permission 权限级别
     */
    void savePermission(String kbId, String userId, String permission);

    /**
     * 查找权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @return 权限级别，不存在返回null
     */
    String findPermission(String kbId, String userId);

    /**
     * 查找知识库的所有权限
     *
     * @param kbId 知识库ID
     * @return 用户ID到权限的映射
     */
    Map<String, String> findPermissionsByKnowledgeBase(String kbId);

    /**
     * 删除权限
     *
     * @param kbId 知识库ID
     * @param userId 用户ID
     */
    void deletePermission(String kbId, String userId);

    /**
     * 删除知识库的所有权限
     *
     * @param kbId 知识库ID
     */
    void deletePermissionsByKnowledgeBase(String kbId);

    // ========== 场景绑定操作 ==========

    /**
     * 统计知识库被场景绑定的数量
     *
     * @param kbId 知识库ID
     * @return 绑定数量
     */
    int countSceneBindings(String kbId);

    /**
     * 查找知识库绑定的场景列表
     *
     * @param kbId 知识库ID
     * @return 场景ID列表
     */
    List<String> findBoundScenes(String kbId);
}
