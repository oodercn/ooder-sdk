package net.ooder.scene.skill.source;

import net.ooder.scene.core.InstalledSkillInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 技能来源服务接口
 *
 * <p>提供技能安装来源管理能力，包括：</p>
 * <ul>
 *   <li>记录和查询技能来源</li>
 *   <li>按来源类型查询技能</li>
 *   <li>技能分享、委派、推送功能</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0.1
 */
public interface SkillSourceService {

    /**
     * 按来源类型查询已安装技能
     *
     * @param source 来源类型 (download/share/delegate/push/registry/dev)
     * @return 该来源的所有已安装技能
     */
    List<InstalledSkillInfo> getSkillsBySource(String source);

    /**
     * 按安装人查询已安装技能
     *
     * @param userId 用户ID
     * @return 该用户安装的所有技能
     */
    List<InstalledSkillInfo> getSkillsByInstaller(String userId);

    /**
     * 按分享人查询已安装技能
     *
     * @param userId 分享人ID
     * @return 该用户分享的所有技能
     */
    List<InstalledSkillInfo> getSkillsBySharer(String userId);

    /**
     * 按委派人查询已安装技能
     *
     * @param userId 委派人ID
     * @return 该用户委派的所有技能
     */
    List<InstalledSkillInfo> getSkillsByDelegator(String userId);

    /**
     * 记录技能安装来源
     *
     * @param skillId 技能ID
     * @param source 来源类型
     * @param metadata 来源元数据
     */
    void recordInstallSource(String skillId, String source, Map<String, Object> metadata);

    /**
     * 更新技能来源信息
     *
     * @param skillId 技能ID
     * @param source 新的来源类型
     * @param fromUserId 来源用户ID（分享人/委派人）
     */
    void updateSource(String skillId, String source, String fromUserId);

    /**
     * 带来源信息的安装
     *
     * @param skillId 技能ID
     * @param source 来源类型
     * @param installedBy 安装人ID
     * @return 安装结果
     */
    CompletableFuture<Boolean> installWithSource(String skillId, String source, String installedBy);

    /**
     * 分享技能给其他用户
     *
     * @param skillId 技能ID
     * @param fromUserId 分享人ID
     * @param toUserIds 目标用户ID列表
     * @param message 分享消息
     * @return 分享结果
     */
    CompletableFuture<ShareResult> shareSkill(String skillId, String fromUserId,
            List<String> toUserIds, String message);

    /**
     * 委派技能给其他用户
     *
     * @param skillId 技能ID
     * @param fromUserId 委派人ID
     * @param toUserIds 目标用户ID列表
     * @param deadline 截止时间
     * @param message 委派消息
     * @return 委派结果
     */
    CompletableFuture<DelegateResult> delegateSkill(String skillId, String fromUserId,
            List<String> toUserIds, Long deadline, String message);

    /**
     * 系统推送技能给用户
     *
     * @param skillId 技能ID
     * @param toUserIds 目标用户ID列表
     * @param message 推送消息
     * @return 推送结果
     */
    CompletableFuture<PushResult> pushSkill(String skillId, List<String> toUserIds, String message);

    /**
     * 获取技能来源信息
     *
     * @param skillId 技能ID
     * @return 技能来源信息
     */
    InstalledSkillInfo getSourceInfo(String skillId);
}
