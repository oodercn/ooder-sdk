package net.ooder.scene.skill;

import net.ooder.scene.core.SkillQuery;

import java.util.List;
import java.util.Map;

/**
 * Skill 服务接口
 *
 * @author Ooder Team
 * @version 2.3.1
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
}
