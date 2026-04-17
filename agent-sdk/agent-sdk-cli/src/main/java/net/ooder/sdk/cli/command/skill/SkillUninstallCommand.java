package net.ooder.sdk.cli.command.skill;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.UninstallResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Skill卸载命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillUninstallCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(SkillUninstallCommand.class);

    private final SkillInstaller skillInstaller;
    private final SkillRegistry skillRegistry;

    public SkillUninstallCommand(SkillInstaller skillInstaller, SkillRegistry skillRegistry) {
        this.skillInstaller = skillInstaller;
        this.skillRegistry = skillRegistry;
    }

    @Override
    public String getName() {
        return "skill:uninstall";
    }

    @Override
    public String getDescription() {
        return "Uninstall a skill";
    }

    @Override
    public String getUsage() {
        return "ooder skill:uninstall --skill-id <id> [--force]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String skillId = context.getAttribute("skill-id");
        boolean force = Boolean.parseBoolean(context.getAttribute("force"));

        if (skillId == null || skillId.isEmpty()) {
            return CommandResult.invalidArgs("Skill ID is required (--skill-id)");
        }

        try {
            log.info("Uninstalling skill: {}", skillId);

            // 检查Skill是否存在
            InstalledSkill installedSkill = skillRegistry.getInstalledSkill(skillId);
            if (installedSkill == null) {
                return CommandResult.notFound("Skill not found: " + skillId);
            }

            // 执行卸载 - 使用异步 API
            CompletableFuture<UninstallResult> future = skillInstaller.uninstall(skillId, force);
            UninstallResult result = future.get();

            if (!result.isSuccess()) {
                return CommandResult.error("Failed to uninstall skill: " + skillId + " - " + result.getError());
            }

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("skillId", skillId);
            resultMap.put("name", installedSkill.getName());
            resultMap.put("version", installedSkill.getVersion());
            resultMap.put("status", "UNINSTALLED");
            resultMap.put("dataRemoved", result.isDataRemoved());

            log.info("Skill uninstalled successfully: {}", skillId);

            return CommandResult.success(
                    String.format("Skill uninstalled: %s v%s", installedSkill.getName(), installedSkill.getVersion()),
                    resultMap
            );

        } catch (Exception e) {
            log.error("Failed to uninstall skill: {}", skillId, e);
            return CommandResult.error("Uninstallation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCategory() {
        return "skill";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"uninstall-skill", "remove-skill"};
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
