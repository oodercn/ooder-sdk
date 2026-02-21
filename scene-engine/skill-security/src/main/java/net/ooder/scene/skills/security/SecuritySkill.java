package net.ooder.scene.skills.security;

import net.ooder.scene.core.SkillInfo;
import java.util.Arrays;
import java.util.List;

/**
 * Security Skill Definition
 *
 * <p>安全管理技能 - 策略管理、访问控制、威胁检测</p>
 * 
 * <p>分类: sys (系统管理)</p>
 * <p>子分类: security</p>
 */
public class SecuritySkill extends SkillInfo {

    private static final String SKILL_ID = "skill-security";
    private static final String SKILL_NAME = "Security Skill";
    private static final String SKILL_VERSION = "0.7.3";
    private static final String SKILL_DESCRIPTION = "安全管理技能 - 策略管理、访问控制、威胁检测";
    private static final String SKILL_CATEGORY = "sys";
    private static final String SKILL_SUB_CATEGORY = "security";

    public SecuritySkill() {
        setSkillId(SKILL_ID);
        setName(SKILL_NAME);
        setVersion(SKILL_VERSION);
        setDescription(SKILL_DESCRIPTION);
        setAuthor("Ooder Team");
        setStatus("INSTALLED");
        setCategory(SKILL_CATEGORY);
        setTags(Arrays.asList("security", "policy", "acl", "threat", "firewall", "sys"));
        setInstallCount(0);
        setCreatedAt(System.currentTimeMillis());
    }

    public String getSubCategory() {
        return SKILL_SUB_CATEGORY;
    }

    public List<String> getRequiredCapabilities() {
        return Arrays.asList(
            "security-policy-manage",
            "security-acl-manage",
            "security-threat-detect",
            "security-firewall-manage"
        );
    }
}
