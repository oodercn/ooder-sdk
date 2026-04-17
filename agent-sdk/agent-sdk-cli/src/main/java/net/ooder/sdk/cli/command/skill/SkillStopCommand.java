package net.ooder.sdk.cli.command.skill;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillService;

/**
 * Skill停止命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillStopCommand implements CliCommand {

    private final SkillRegistry skillRegistry;

    public SkillStopCommand(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }

    @Override
    public String getName() {
        return "skill:stop";
    }

    @Override
    public String getDescription() {
        return "Stop a skill";
    }

    @Override
    public String getUsage() {
        return "ooder skill:stop --skill-id <id>";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String skillId = context.getAttribute("skill-id");

        if (skillId == null || skillId.isEmpty()) {
            return CommandResult.invalidArgs("Skill ID is required");
        }

        // 复用 SkillRegistry 获取Skill服务
        SkillService service = skillRegistry.getService(skillId);

        if (service == null) {
            return CommandResult.notFound("Skill not found: " + skillId);
        }

        if (!service.isRunning()) {
            return CommandResult.success("Skill is already stopped: " + skillId);
        }

        try {
            service.stop();
            return CommandResult.success("Skill stopped: " + skillId);
        } catch (Exception e) {
            return CommandResult.error("Failed to stop skill: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCategory() {
        return "skill";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"stop-skill"};
    }
}
