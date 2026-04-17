package net.ooder.sdk.cli.command.system;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统状态命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class StatusCommand implements CliCommand {

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Show system status";
    }

    @Override
    public String getUsage() {
        return "ooder status";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        Map<String, Object> status = new HashMap<>();
        status.put("version", "3.1.0");
        status.put("status", "running");
        status.put("skills", 3);
        status.put("scenes", 2);
        status.put("capabilities", 5);
        status.put("uptime", "2h 15m");

        return CommandResult.success("System is running", status);
    }

    @Override
    public String getCategory() {
        return "system";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"info", "stat"};
    }
}
