package net.ooder.scene.core.instance;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 场景实例存储库接口
 * 
 * <p>提供场景实例的持久化操作</p>
 *
 * @author Ooder Team
 * @version 3.0.0
 * @since 2.3.1
 */
public interface SceneInstanceRepository {

    /**
     * 保存场景实例
     *
     * @param instance 场景实例
     * @return 保存后的实例
     */
    SceneInstance save(SceneInstance instance);

    /**
     * 根据实例ID查找
     *
     * @param instanceId 实例ID
     * @return 场景实例
     */
    Optional<SceneInstance> findById(String instanceId);

    /**
     * 根据场景ID查找所有实例
     *
     * @param sceneId 场景ID
     * @return 实例列表
     */
    List<SceneInstance> findBySceneId(String sceneId);

    /**
     * 根据模板ID查找所有实例
     *
     * @param templateId 模板ID
     * @return 实例列表
     */
    List<SceneInstance> findByTemplateId(String templateId);

    /**
     * 根据用户ID查找参与的实例
     *
     * @param userId 用户ID
     * @return 实例列表
     */
    List<SceneInstance> findByUserId(String userId);

    /**
     * 根据用户ID和角色ID查找实例
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 实例列表
     */
    List<SceneInstance> findByUserIdAndRoleId(String userId, String roleId);

    /**
     * 根据状态查找实例
     *
     * @param state 状态
     * @return 实例列表
     */
    List<SceneInstance> findByState(String state);

    /**
     * 查找所有实例
     *
     * @return 实例列表
     */
    List<SceneInstance> findAll();

    /**
     * 删除实例
     *
     * @param instanceId 实例ID
     * @return 是否成功
     */
    boolean deleteById(String instanceId);

    /**
     * 更新实例状态
     *
     * @param instanceId 实例ID
     * @param state 新状态
     * @return 是否成功
     */
    boolean updateState(String instanceId, String state);

    /**
     * 添加参与者
     *
     * @param instanceId 实例ID
     * @param participant 参与者信息
     * @return 是否成功
     */
    boolean addParticipant(String instanceId, SceneInstance.ParticipantInfo participant);

    /**
     * 移除参与者
     *
     * @param instanceId 实例ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeParticipant(String instanceId, String userId);

    /**
     * 更新实例配置
     *
     * @param instanceId 实例ID
     * @param config 配置数据
     * @return 是否成功
     */
    boolean updateConfig(String instanceId, Map<String, Object> config);

    /**
     * 添加激活记录
     *
     * @param instanceId 实例ID
     * @param record 激活记录
     * @return 是否成功
     */
    boolean addActivationRecord(String instanceId, SceneInstance.ActivationRecord record);

    /**
     * 统计实例数量
     *
     * @return 实例数量
     */
    long count();

    /**
     * 统计指定场景的实例数量
     *
     * @param sceneId 场景ID
     * @return 实例数量
     */
    long countBySceneId(String sceneId);

    /**
     * 检查实例是否存在
     *
     * @param instanceId 实例ID
     * @return 是否存在
     */
    boolean existsById(String instanceId);
}
