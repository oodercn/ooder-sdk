package net.ooder.sdk.cli.adapter;

import net.ooder.sdk.cli.api.*;

import java.util.List;
import java.util.Map;

/**
 * 旧版 CliExtension 到 SkillCliExtension 的适配器
 *
 * <p>提供向后兼容支持，将旧版 CliExtension 接口适配为新的 SkillCliExtension 接口</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class LegacyCliExtensionAdapter implements SkillCliExtension {

    private final ExtensionRegistry.CliExtension legacyExtension;

    public LegacyCliExtensionAdapter(ExtensionRegistry.CliExtension legacyExtension) {
        this.legacyExtension = legacyExtension;
    }

    @Override
    public String getSkillId() {
        return legacyExtension.getId();
    }

    @Override
    public String getCommand() {
        List<CliCommand> commands = legacyExtension.getCommands();
        if (commands != null && !commands.isEmpty()) {
            return commands.get(0).getName();
        }
        return legacyExtension.getName();
    }

    @Override
    public String getDescription() {
        List<CliCommand> commands = legacyExtension.getCommands();
        if (commands != null && !commands.isEmpty()) {
            return commands.get(0).getDescription();
        }
        return legacyExtension.getName();
    }

    @Override
    public String getUsage() {
        List<CliCommand> commands = legacyExtension.getCommands();
        if (commands != null && !commands.isEmpty()) {
            return commands.get(0).getUsage();
        }
        return getCommand();
    }

    @Override
    public CliResult execute(String[] args, Map<String, Object> context) {
        List<CliCommand> commands = legacyExtension.getCommands();
        if (commands == null || commands.isEmpty()) {
            return CliResult.error("No commands available in extension");
        }

        CliCommand command = commands.get(0);
        CommandContext ctx = new CommandContext();
        ctx.setArgs(args);

        try {
            CommandResult result = command.execute(ctx);
            if (result.isSuccess()) {
                return CliResult.success(result.getMessage(), result.getData());
            } else {
                return CliResult.error(result.getExitCode(), result.getMessage());
            }
        } catch (Exception e) {
            return CliResult.error("Command execution failed: " + e.getMessage());
        }
    }

    @Override
    public String getCategory() {
        List<CliCommand> commands = legacyExtension.getCommands();
        if (commands != null && !commands.isEmpty()) {
            return commands.get(0).getCategory();
        }
        return "general";
    }

    @Override
    public String[] getAliases() {
        List<CliCommand> commands = legacyExtension.getCommands();
        if (commands != null && !commands.isEmpty()) {
            return commands.get(0).getAliases();
        }
        return new String[0];
    }

    @Override
    public boolean validate(String[] args) {
        List<CliCommand> commands = legacyExtension.getCommands();
        if (commands != null && !commands.isEmpty()) {
            return commands.get(0).validate(args);
        }
        return true;
    }

    @Override
    public boolean isInteractive() {
        List<CliCommand> commands = legacyExtension.getCommands();
        if (commands != null && !commands.isEmpty()) {
            return commands.get(0).isInteractive();
        }
        return false;
    }

    @Override
    public void initialize() {
        legacyExtension.initialize();
    }

    @Override
    public void destroy() {
        legacyExtension.destroy();
    }

    /**
     * 获取原始扩展
     */
    public ExtensionRegistry.CliExtension getLegacyExtension() {
        return legacyExtension;
    }

    /**
     * 获取扩展版本
     */
    public String getVersion() {
        return legacyExtension.getVersion();
    }

    /**
     * 获取所有命令
     */
    public List<CliCommand> getCommands() {
        return legacyExtension.getCommands();
    }

    /**
     * 是否已启用
     */
    public boolean isEnabled() {
        return legacyExtension.isEnabled();
    }
}
