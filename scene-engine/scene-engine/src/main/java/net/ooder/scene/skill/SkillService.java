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

public interface SkillService {

    SkillInfo findSkill(String skillId);

    List<SkillInfo> searchSkills(SkillQuery query);

    List<SkillInfo> discoverSkills(SkillQuery query);

    SkillInstallResult installSkill(String userId, String skillId);

    SkillInstallResult installSkill(String userId, String skillId, Map<String, Object> config);

    SkillInstallProgress getInstallProgress(String installId);

    SkillUninstallResult uninstallSkill(String userId, String skillId);

    List<InstalledSkillInfo> listInstalledSkills(String userId);

    List<Capability> listCapabilities(String skillId);

    Object invokeCapability(String userId, String skillId, String capability, Map<String, Object> params);

    SkillRuntimeStatus getRuntimeStatus(String skillId);

    void startSkill(String userId, String skillId);

    void stopSkill(String userId, String skillId);

    void restartSkill(String userId, String skillId);
}
