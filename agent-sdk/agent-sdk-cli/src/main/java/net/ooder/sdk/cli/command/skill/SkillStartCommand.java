package net.ooder.sdk.cli.command.skill;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillService;

/**
 * Skill启动命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillStartCommand implements CliCommand {

    private final SkillRegistry skillRegistry;

    public SkillStartCommand(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }

    @Override
    public String getName() {
        return "skill:start";
    }

    @Override
    public String getDescription() {
        return "Start a skill";
    }

    @Override
    public String getUsage() {
        return "ooder skill:start --skill-id <id>";
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

        if (service.isRunning()) {
            return CommandResult.success("Skill is already running: " + skillId);
        }

        try {
            service.start();
            return CommandResult.success("Skill started: " + skillId);
        } catch (Exception e) {
            return CommandResult.error("Failed to start skill: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCategory() {
        return "skill";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"start-skill"};
    }
}
