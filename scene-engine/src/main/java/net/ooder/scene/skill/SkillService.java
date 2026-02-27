package net.ooder.scene.skill;

import java.util.List;
import java.util.Map;

import net.ooder.sdk.api.capability.Capability;
import net.ooder.scene.core.InstalledSkillInfo;
import net.ooder.scene.core.SkillInfo;
import net.ooder.scene.core.SkillInstallProgress;
import net.ooder.scene.core.SkillInstallResult;
import net.ooder.scene.core.SkillQuery;
import net.ooder.scene.core.SkillUninstallResult;

/**
 * Skill 服务接口
 *
 * <p>管理 Skill 的生命周期，包括发现、安装、卸载、启动、停止和能力调用。</p>
 *
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>Skill 发现 - 从仓库或网络发现可用 Skill</li>
 *   <li>Skill 安装 - 安装 Skill 到本地环境</li>
 *   <li>Skill 卸载 - 从本地环境移除 Skill</li>
 *   <li>生命周期管理 - 启动、停止、重启 Skill</li>
 *   <li>能力调用 - 调用 Skill 提供的能力</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * // 发现 Skill
 * List<SkillInfo> skills = skillService.discoverSkills(new SkillQuery("messaging"));
 *
 * // 安装 Skill
 * SkillInstallResult result = skillService.installSkill("user123", "skill-001");
 *
 * // 调用能力
 * Object result = skillService.invokeCapability("user123", "skill-001", "sendMessage", params);
 *
 * // 卸载 Skill
 * skillService.uninstallSkill("user123", "skill-001");
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 0.7.0
 * @see SkillInfo
 * @see Capability
 */
public interface SkillService {

    /**
     * 查找 Skill
     *
     * @param skillId Skill ID
     * @return Skill 信息，不存在返回 null
     */
    SkillInfo findSkill(String skillId);

    /**
     * 搜索 Skill
     *
     * @param query 查询条件
     * @return Skill 列表
     */
    List<SkillInfo> searchSkills(SkillQuery query);

    /**
     * 发现 Skill
     *
     * @param query 查询条件
     * @return 发现的 Skill 列表
     */
    List<SkillInfo> discoverSkills(SkillQuery query);

    /**
     * 安装 Skill
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     * @return 安装结果
     */
    SkillInstallResult installSkill(String userId, String skillId);

    /**
     * 安装 Skill（带配置）
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     * @param config 安装配置
     * @return 安装结果
     */
    SkillInstallResult installSkill(String userId, String skillId, Map<String, Object> config);

    /**
     * 获取安装进度
     *
     * @param installId 安装ID
     * @return 安装进度
     */
    SkillInstallProgress getInstallProgress(String installId);

    /**
     * 卸载 Skill
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     * @return 卸载结果
     */
    SkillUninstallResult uninstallSkill(String userId, String skillId);

    /**
     * 列出已安装的 Skill
     *
     * @param userId 用户ID
     * @return 已安装的 Skill 列表
     */
    List<InstalledSkillInfo> listInstalledSkills(String userId);

    /**
     * 列出 Skill 的能力
     *
     * @param skillId Skill ID
     * @return 能力列表
     */
    List<Capability> listCapabilities(String skillId);

    /**
     * 调用能力
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     * @param capability 能力名称
     * @param params 调用参数
     * @return 调用结果
     */
    Object invokeCapability(String userId, String skillId, String capability, Map<String, Object> params);

    /**
     * 获取运行时状态
     *
     * @param skillId Skill ID
     * @return 运行时状态
     */
    SkillRuntimeStatus getRuntimeStatus(String skillId);

    /**
     * 启动 Skill
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     */
    void startSkill(String userId, String skillId);

    /**
     * 停止 Skill
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     */
    void stopSkill(String userId, String skillId);

    /**
     * 重启 Skill
     *
     * @param userId 用户ID
     * @param skillId Skill ID
     */
    void restartSkill(String userId, String skillId);
}
