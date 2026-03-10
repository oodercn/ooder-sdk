package net.ooder.scene.skill.knowledge;

import java.util.List;

/**
 * 知识库绑定服务接口
 *
 * <p>提供场景与知识库的绑定能力，支持：</p>
 * <ul>
 *   <li>知识库绑定/解绑</li>
 *   <li>知识检索</li>
 *   <li>跨层检索</li>
 * </ul>
 *
 * @author ooder
 * @since 2.3.2
 */
public interface KnowledgeBindingService {

    /**
     * 绑定知识库到场景
     *
     * @param sceneGroupId 场景组ID
     * @param kbId 知识库ID
     * @param layer 层级（如：global, team, personal）
     */
    void bindToScene(String sceneGroupId, String kbId, String layer);

    /**
     * 从场景解绑知识库
     *
     * @param sceneGroupId 场景组ID
     * @param kbId 知识库ID
     */
    void unbindFromScene(String sceneGroupId, String kbId);

    /**
     * 检索知识
     *
     * @param sceneGroupId 场景组ID
     * @param query 查询文本
     * @param topK 返回数量
     * @return 知识片段列表
     */
    List<KnowledgeChunk> searchKnowledge(String sceneGroupId, String query, int topK);

    /**
     * 跨层检索知识
     *
     * @param sceneGroupId 场景组ID
     * @param query 查询文本
     * @param layers 层级列表
     * @param topK 返回数量
     * @return 知识片段列表
     */
    List<KnowledgeChunk> crossLayerSearch(String sceneGroupId, String query, 
                                           List<String> layers, int topK);

    /**
     * 获取场景绑定的知识库列表
     *
     * @param sceneGroupId 场景组ID
     * @return 知识库绑定信息列表
     */
    List<KnowledgeBinding> getBindings(String sceneGroupId);
}
