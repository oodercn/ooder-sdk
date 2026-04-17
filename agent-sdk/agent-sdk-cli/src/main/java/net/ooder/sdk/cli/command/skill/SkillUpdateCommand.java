package net.ooder.sdk.cli.command.skill;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.InstallResult;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.SkillRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Skill更新命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillUpdateCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(SkillUpdateCommand.class);

    private final SkillInstaller skillInstaller;
    private final SkillRegistry skillRegistry;

    public SkillUpdateCommand(SkillInstaller skillInstaller, SkillRegistry skillRegistry) {
        this.skillInstaller = skillInstaller;
        this.skillRegistry = skillRegistry;
    }

    @Override
    public String getName() {
        return "skill:update";
    }

    @Override
    public String getDescription() {
        return "Update a skill to the latest version";
    }

    @Override
    public String getUsage() {
        return "ooder skill:update --skill-id <id> [--source <path|url>] [--check-only]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String skillId = context.getAttribute("skill-id");
        String source = context.getAttribute("source");
        boolean checkOnly = Boolean.parseBoolean(context.getAttribute("check-only"));

        if (skillId == null || skillId.isEmpty()) {
            return CommandResult.invalidArgs("Skill ID is required (--skill-id)");
        }

        try {
            log.info("Updating skill: {}", skillId);

            // 检查Skill是否存在
            InstalledSkill currentSkill = skillRegistry.getInstalledSkill(skillId);
            if (currentSkill == null) {
                return CommandResult.notFound("Skill not found: " + skillId);
            }

            String currentVersion = currentSkill.getVersion();

            // 仅检查更新
            if (checkOnly) {
                return checkForUpdate(skillId, currentVersion);
            }

            // 执行更新
            return performUpdate(skillId, source, currentVersion);

        } catch (Exception e) {
            log.error("Failed to update skill: {}", skillId, e);
            return CommandResult.error("Update failed: " + e.getMessage(), e);
        }
    }

    /**
     * 检查更新
     */
    private CommandResult checkForUpdate(String skillId, String currentVersion) {
        log.info("Checking for updates: {} (current: {})", skillId, currentVersion);

        // 获取最新版本信息
        String latestVersion = skillRegistry.getLatestVersion(skillId);

        if (latestVersion == null) {
            return CommandResult.error("Failed to check for updates");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("skillId", skillId);
        result.put("currentVersion", currentVersion);
        result.put("latestVersion", latestVersion);
        result.put("updateAvailable", !currentVersion.equals(latestVersion));

        if (currentVersion.equals(latestVersion)) {
            return CommandResult.success("Skill is up to date (v" + latestVersion + ")", result);
        } else {
            return CommandResult.success(
                    String.format("Update available: %s -> %s", currentVersion, latestVersion),
                    result
            );
        }
    }

    /**
     * 执行更新
     */
    private CommandResult performUpdate(String skillId, String source, String currentVersion) {
        log.info("Performing update for skill: {} from v{}", skillId, currentVersion);

        try {
            SkillPackage newPackage = new SkillPackage();
            newPackage.setSkillId(skillId);

            if (source != null && !source.isEmpty()) {
                // 从指定源更新
                if (source.startsWith("http://") || source.startsWith("https://")) {
                    newPackage.setDownloadUrl(source);
                } else {
                    newPackage.setSource(source);
                }
            }

            // 执行安装/更新 - 使用 UPGRADE 模式
            CompletableFuture<InstallResult> future = skillInstaller.install(newPackage,
                    SkillInstaller.InstallMode.UPGRADE);

            InstallResult installResult = future.get();

            if (!installResult.isSuccess()) {
                return CommandResult.error("Failed to update skill: " + installResult.getError());
            }

            // 获取更新后的信息
            InstalledSkill updatedSkill = skillRegistry.getInstalledSkill(skillId);
            String newVersion = updatedSkill != null ? updatedSkill.getVersion() : installResult.getVersion();

            Map<String, Object> result = new HashMap<>();
            result.put("skillId", skillId);
            result.put("previousVersion", currentVersion);
            result.put("newVersion", newVersion);
            result.put("status", "UPDATED");

            log.info("Skill updated successfully: {} v{} -> v{}",
                    skillId, currentVersion, newVersion);

            return CommandResult.success(
                    String.format("Skill updated: %s v%s -> v%s",
                            updatedSkill != null ? updatedSkill.getName() : skillId,
                            currentVersion, newVersion),
                    result
            );
        } catch (Exception e) {
            log.error("Failed to update skill: {}", skillId, e);
            return CommandResult.error("Update failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCategory() {
        return "skill";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"update-skill", "upgrade-skill"};
    }

    @Override
    public boolean validate(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--skill-id".equals(args[i]) && i + 1 < args.length) {
                return true;
            }
        }
        return false;
    }
}
