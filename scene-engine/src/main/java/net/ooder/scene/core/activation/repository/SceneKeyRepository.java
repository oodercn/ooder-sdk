package net.ooder.scene.core.activation.repository;

import net.ooder.scene.core.activation.model.SceneKey;

import java.util.List;
import java.util.Optional;

/**
 * 场景密钥存储库接口
 * 
 * <p>提供场景密钥的持久化操作</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface SceneKeyRepository {

    /**
     * 保存密钥
     *
     * @param key 密钥
     * @return 保存后的密钥
     */
    SceneKey save(SceneKey key);

    /**
     * 根据密钥ID查找
     *
     * @param keyId 密钥ID
     * @return 密钥
     */
    Optional<SceneKey> findById(String keyId);

    /**
     * 根据密钥值查找
     *
     * @param key 密钥值
     * @return 密钥
     */
    Optional<SceneKey> findByKey(String key);

    /**
     * 根据场景ID查找所有密钥
     *
     * @param sceneId 场景ID
     * @return 密钥列表
     */
    List<SceneKey> findBySceneId(String sceneId);

    /**
     * 根据实例ID查找密钥
     *
     * @param instanceId 实例ID
     * @return 密钥列表
     */
    List<SceneKey> findByInstanceId(String instanceId);

    /**
     * 根据用户ID查找密钥
     *
     * @param userId 用户ID
     * @return 密钥列表
     */
    List<SceneKey> findByUserId(String userId);

    /**
     * 根据密钥类型查找
     *
     * @param keyType 密钥类型
     * @return 密钥列表
     */
    List<SceneKey> findByKeyType(String keyType);

    /**
     * 查找有效的密钥
     *
     * @param sceneId 场景ID
     * @return 有效密钥列表
     */
    List<SceneKey> findValidKeys(String sceneId);

    /**
     * 查找所有密钥
     *
     * @return 密钥列表
     */
    List<SceneKey> findAll();

    /**
     * 删除密钥
     *
     * @param keyId 密钥ID
     * @return 是否成功
     */
    boolean deleteById(String keyId);

    /**
     * 撤销密钥
     *
     * @param keyId 密钥ID
     * @return 是否成功
     */
    boolean revokeKey(String keyId);

    /**
     * 更新密钥使用次数
     *
     * @param keyId 密钥ID
     * @return 是否成功
     */
    boolean incrementUsage(String keyId);

    /**
     * 统计密钥数量
     *
     * @return 密钥数量
     */
    long count();

    /**
     * 统计场景的密钥数量
     *
     * @param sceneId 场景ID
     * @return 密钥数量
     */
    long countBySceneId(String sceneId);

    /**
     * 检查密钥是否存在
     *
     * @param keyId 密钥ID
     * @return 是否存在
     */
    boolean existsById(String keyId);

    /**
     * 清理过期密钥
     *
     * @return 清理数量
     */
    int cleanupExpired();

    /**
     * 验证密钥
     *
     * @param key 密钥值
     * @return 密钥信息，如果无效则返回空
     */
    Optional<SceneKey> validateKey(String key);
}
