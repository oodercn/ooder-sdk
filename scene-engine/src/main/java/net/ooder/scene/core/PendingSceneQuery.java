package net.ooder.scene.core;

import net.ooder.sdk.common.enums.MemberRole;

import java.util.List;

/**
 * 待激活场景查询接口
 *
 * <p>提供待激活场景的查询能力，支持按用户和角色过滤。</p>
 *
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>领导查看待激活场景列表</li>
 *   <li>员工查看被推送的场景列表</li>
 *   <li>统计待激活场景数量</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface PendingSceneQuery {

    /**
     * 获取用户待激活场景列表
     *
     * @param userId 用户ID
     * @return 待激活场景列表
     */
    List<PendingSceneInfo> getPendingScenes(String userId);

    /**
     * 按角色获取用户待激活场景列表
     *
     * @param userId 用户ID
     * @param role 成员角色（PRIMARY/BACKUP/MEMBER）
     * @return 待激活场景列表
     */
    List<PendingSceneInfo> getPendingScenes(String userId, MemberRole role);

    /**
     * 获取用户待激活场景数量
     *
     * @param userId 用户ID
     * @return 待激活场景数量
     */
    int getPendingSceneCount(String userId);

    /**
     * 获取场景推送来源
     *
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @return 推送来源用户ID，如果没有被推送则返回null
     */
    String getPushSource(String sceneId, String userId);

    /**
     * 检查场景是否待激活
     *
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @return true 表示待激活
     */
    boolean isPending(String sceneId, String userId);
}
