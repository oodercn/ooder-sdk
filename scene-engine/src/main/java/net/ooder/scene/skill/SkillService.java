package net.ooder.scene.skill;

import net.ooder.scene.core.InstalledSkillInfo;
import net.ooder.scene.core.SkillQuery;
import net.ooder.scene.skill.source.ShareResult;
import net.ooder.scene.skill.source.DelegateResult;
import net.ooder.scene.skill.source.PushResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Skill 服务接口
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0.1
 */
public interface SkillService {

    /**
     * 查找 Skill
     * @param skillId Skill ID
     * @return Skill 信息
     */
    Object findSkill(String skillId);

    /**
     * 搜索 Skills
     * @param query 查询条件
     * @return Skill 列表
     */
    List<Object> searchSkills(SkillQuery query);

    /**
     * 列出已安装的 Skills
     * @param userId 用户ID
     * @return Skill 列表
     */
    List<Object> listInstalledSkills(String userId);

    /**
     * 安装 Skill
     * @param skillId Skill ID
     * @param version 版本
     * @param options 选项
     * @return 安装结果
     */
    boolean installSkill(String skillId, String version, Map<String, Object> options);

    /**
     * 安装 Skill（简化版本）
     * @param skillId Skill ID
     * @param version 版本
     * @return 安装结果
     */
    default boolean installSkill(String skillId, String version) {
        return installSkill(skillId, version, null);
    }

    /**
     * 卸载 Skill
     * @param skillId Skill ID
     * @param userId 用户ID
     * @return 卸载结果
     */
    boolean uninstallSkill(String skillId, String userId);

    /**
     * 获取安装进度
     * @param sessionId 会话ID
     * @return 进度百分比
     */
    int getInstallProgress(String sessionId);

    /**
     * 带来源信息的安装
     *
     * @param skillId 技能ID
     * @param version 版本
     * @param source 来源类型
     * @param installedBy 安装人ID
     * @return 安装结果
     */
    default boolean installSkillWithSource(String skillId, String version, String source, String installedBy) {
        Map<String, Object> options = new java.util.HashMap<>();
        options.put("source", source);
        options.put("installedBy", installedBy);
        return installSkill(skillId, version, options);
    }

    /**
     * 按来源类型查询已安装技能
     *
     * @param source 来源类型
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
