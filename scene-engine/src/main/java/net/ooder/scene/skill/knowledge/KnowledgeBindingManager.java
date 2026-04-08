package net.ooder.scene.skill.knowledge;

import java.util.List;

/**
 * 知识库绑定管理器接口
 *
 * <p><b>版本历史：</b></p>
 * <ul>
 *   <li>3.2.0 - 合并自 KnowledgeBindingManager 和 KnowledgeBindingService</li>
 *   <li>新增方法：searchKnowledge, crossLayerSearch</li>
 *   <li>统一使用 KnowledgeBinding 实体类</li>
 * </ul>
 *
 * <p>此接口是知识库绑定管理的核心接口，提供以下能力：</p>
 * <ul>
 *   <li>绑定管理：场景组与知识库的绑定/解绑</li>
 *   <li>查询管理：获取绑定列表、单个绑定、存在性检查</li>
 *   <li>优先级管理：设置和调整绑定优先级</li>
 *   <li>知识检索：支持同层检索和跨层检索</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 绑定知识库
 * String bindingId = bindingManager.bind(sceneGroupId, kbId, "global", userId);
 *
 * // 获取绑定列表
 * List<KnowledgeBinding> bindings = bindingManager.getBindings(sceneGroupId);
 *
 * // 搜索知识
 * List<KnowledgeChunk> results = bindingManager.searchKnowledge(sceneGroupId, "查询内容", 10);
 *
 * // 跨层搜索
 * List<KnowledgeChunk> results = bindingManager.crossLayerSearch(
 *     sceneGroupId, "查询内容", Arrays.asList("global", "domain"), 10);
 * }</pre>
 *
 * @author ooder
 * @version 3.2.0
 * @since 2.3.2
 */
public interface KnowledgeBindingManager {

    // ===== 绑定管理（核心 CRUD）=====

    /**
     * 绑定知识库到场景组
     *
     * @param sceneGroupId 场景组ID
     * @param binding 绑定信息（包含 kbId、layer、boundBy 等）
     * @return 绑定ID
     * @throws IllegalArgumentException 参数无效时抛出
     */
    String bind(String sceneGroupId, KnowledgeBinding binding);

    /**
     * 绑定知识库到场景组（简化版本）
     *
     * @param sceneGroupId 场景组ID
     * @param kbId 知识库ID
     * @param layer 层级
     * @param boundBy 绑定者
     * @return 绑定ID
     */
    String bind(String sceneGroupId, String kbId, String layer, String boundBy);

    /**
     * 解除知识库与场景组的绑定
     *
     * @param sceneGroupId 场景组ID
     * @param kbId 知识库ID
     * @return 是否成功解除绑定
     */
    boolean unbind(String sceneGroupId, String kbId);

    // ===== 查询管理 =====

    /**
     * 获取场景组的所有知识库绑定
     *
     * @param sceneGroupId 场景组ID
     * @return 绑定列表，按优先级降序排列
     */
    List<KnowledgeBinding> getBindings(String sceneGroupId);

    /**
     * 获取指定的绑定信息
     *
     * @param sceneGroupId 场景组ID
     * @param kbId 知识库ID
     * @return 绑定信息，不存在返回 null
     */
    KnowledgeBinding getBinding(String sceneGroupId, String kbId);

    /**
     * 检查知识库是否已绑定到场景组
     *
     * @param sceneGroupId 场景组ID
     * @param kbId 知识库ID
     * @return 是否存在绑定
     */
    boolean hasBinding(String sceneGroupId, String kbId);

    // ===== 优先级管理 =====

    /**
     * 设置绑定优先级
     *
     * @param sceneGroupId 场景组ID
     * @param kbId 知识库ID
     * @param priority 优先级（-100 到 100）
     * @return 是否成功设置
     */
    boolean setPriority(String sceneGroupId, String kbId, int priority);

    /**
     * 清除场景组的所有绑定
     *
     * @param sceneGroupId 场景组ID
     * @return 清除的绑定数量
     */
    int clearAllBindings(String sceneGroupId);

    // ===== 知识检索（来自 KnowledgeBindingService） =====

    /**
     * 在场景组绑定的知识库中搜索
     *
     * <p>使用嵌入向量进行语义搜索，返回最相关的知识块。</p>
     *
     * @param sceneGroupId 场景组ID
     * @param query 查询内容
     * @param topK 返回结果数量
     * @return 知识块列表，按相关性降序排列
     * @throws IllegalStateException 知识检索服务未就绪时抛出
     */
    List<KnowledgeChunk> searchKnowledge(String sceneGroupId, String query, int topK);

    /**
     * 在指定层级中跨层搜索
     *
     * <p>在多个层级中进行语义搜索，适用于需要跨多个知识层检索的场景。</p>
     *
     * @param sceneGroupId 场景组ID
     * @param query 查询内容
     * @param layers 要搜索的层级列表
     * @param topK 返回结果数量
     * @return 知识块列表，按相关性降序排列
     * @throws IllegalStateException 知识检索服务未就绪时抛出
     */
    List<KnowledgeChunk> crossLayerSearch(String sceneGroupId, String query, List<String> layers, int topK);

    // ===== 统计方法 =====

    /**
     * 获取场景组的绑定数量
     *
     * @param sceneGroupId 场景组ID
     * @return 绑定数量
     */
    int getBindingCount(String sceneGroupId);

    /**
     * 获取总绑定数量
     *
     * @return 总绑定数量
     */
    long getTotalBindingCount();
}
