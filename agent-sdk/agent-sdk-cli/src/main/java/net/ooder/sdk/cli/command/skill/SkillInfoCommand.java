package net.ooder.sdk.cli.command.skill;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.SkillManifest;
import net.ooder.skills.api.SkillRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Skill信息命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillInfoCommand implements CliCommand {

    private final SkillRegistry skillRegistry;

    public SkillInfoCommand(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }

    @Override
    public String getName() {
        return "skill:info";
    }

    @Override
    public String getDescription() {
        return "Show skill information";
    }

    @Override
    public String getUsage() {
        return "ooder skill:info --skill-id <id>";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String skillId = context.getAttribute("skill-id");

        if (skillId == null || skillId.isEmpty()) {
            return CommandResult.invalidArgs("Skill ID is required");
        }

        // 复用 SkillRegistry 获取Skill信息
        SkillManifest manifest = skillRegistry.getSkill(skillId);

        if (manifest == null) {
            return CommandResult.notFound("Skill not found: " + skillId);
        }

        Map<String, Object> info = new HashMap<>();
        info.put("skillId", manifest.getSkillId());
        info.put("name", manifest.getName());
        info.put("version", manifest.getVersion());
        info.put("description", manifest.getDescription());
        info.put("author", manifest.getAuthor());
        info.put("type", manifest.getSkillType());
        info.put("sceneId", manifest.getSceneId());
        info.put("capabilities", manifest.getCapabilities());
        info.put("dependencies", manifest.getDependencies());

        return CommandResult.success("Skill information retrieved", info);
    }

    @Override
    public String getCategory() {
        return "skill";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"skill-info", "show-skill"};
    }

    @Override
    public boolean validate(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--skill-id") && i + 1 < args.length) {
                return true;
            }
        }
        return false;
    }
}
