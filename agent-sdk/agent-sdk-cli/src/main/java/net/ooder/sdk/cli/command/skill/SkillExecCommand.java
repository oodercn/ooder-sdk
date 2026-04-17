package net.ooder.sdk.cli.command.skill;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.api.SkillCliExtension;
import net.ooder.sdk.cli.core.registry.SkillExtensionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Skill 执行命令 (统一命令规范)
 *
 * <p>统一命令格式: skill exec &lt;skill-id&gt; &lt;command&gt; [args...]</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillExecCommand implements CliCommand {

    private static final Logger logger = LoggerFactory.getLogger(SkillExecCommand.class);

    private final SkillExtensionRegistry extensionRegistry;

    public SkillExecCommand(SkillExtensionRegistry extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    @Override
    public String getName() {
        return "exec";
    }

    @Override
    public String getDescription() {
        return "Execute a command in a skill";
    }

    @Override
    public String getUsage() {
        return "skill exec <skill-id> <command> [args...]";
    }

    @Override
    public String getCategory() {
        return "skill";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"execute", "run"};
    }

    @Override
    public boolean validate(String[] args) {
        if (args == null || args.length < 2) {
            System.err.println("Error: Missing required arguments");
            System.err.println("Usage: " + getUsage());
            return false;
        }
        return true;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String[] args = context.getArgs();

        if (!validate(args)) {
            return CommandResult.error("Invalid arguments");
        }

        String skillId = args[0];
        String command = args[1];
        String[] commandArgs = args.length > 2 ? Arrays.copyOfRange(args, 2, args.length) : new String[0];

        logger.info("Executing command '{}' in skill '{}'", command, skillId);

        SkillCliExtension extension = extensionRegistry.getExtensionBySkillId(skillId);
        if (extension == null) {
            return CommandResult.error("Skill not found: " + skillId);
        }

        if (!extension.getCommand().equals(command)) {
            return CommandResult.error("Command '" + command + "' not found in skill '" + skillId + "'");
        }

        Map<String, Object> sceneContext = extractSceneContext(context);

        try {
            SkillCliExtension.CliResult result = extension.execute(commandArgs, sceneContext);
            return adaptResult(result);
        } catch (Exception e) {
            logger.error("Failed to execute command '{}' in skill '{}'", command, skillId, e);
            return CommandResult.error("Command execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * 从上下文提取场景上下文
     */
    private Map<String, Object> extractSceneContext(CommandContext context) {
        Map<String, Object> sceneContext = new HashMap<>();
        sceneContext.put("sceneId", context.getString("sceneId", "default"));
        sceneContext.put("sessionId", context.getString("sessionId", ""));
        return sceneContext;
    }

    /**
     * 适配执行结果
     */
    private CommandResult adaptResult(SkillCliExtension.CliResult result) {
        if (result.isSuccess()) {
            if (result.getData() != null) {
                return CommandResult.success(result.getMessage(), result.getData());
            }
            return CommandResult.success(result.getMessage());
        } else {
            return CommandResult.error(result.getMessage(), result.getExitCode());
        }
    }
}
