package net.ooder.sdk.cli.core.router;

import net.ooder.sdk.cli.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 默认CLI路由器实现
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class DefaultCliRouter implements CliRouter {

    private static final Logger log = LoggerFactory.getLogger(DefaultCliRouter.class);

    private final Map<String, CliCommand> commands = new ConcurrentHashMap<>();
    private final Map<String, String> aliases = new ConcurrentHashMap<>();

    @Override
    public void register(CliCommand command) {
        String name = command.getName();
        commands.put(name, command);

        // 注册别名
        for (String alias : command.getAliases()) {
            aliases.put(alias, name);
        }

        log.debug("Registered command: {}", name);
    }

    @Override
    public void register(String name, CliCommand command) {
        commands.put(name, command);
        log.debug("Registered command: {} -> {}", name, command.getClass().getSimpleName());
    }

    @Override
    public void unregister(String name) {
        CliCommand command = commands.remove(name);
        if (command != null) {
            // 移除别名
            for (String alias : command.getAliases()) {
                aliases.remove(alias);
            }
            log.debug("Unregistered command: {}", name);
        }
    }

    @Override
    public Optional<CliCommand> route(String commandName) {
        if (commandName == null || commandName.isEmpty()) {
            return Optional.empty();
        }

        // 直接查找
        CliCommand command = commands.get(commandName);
        if (command != null) {
            return Optional.of(command);
        }

        // 通过别名查找
        String actualName = aliases.get(commandName);
        if (actualName != null) {
            return Optional.ofNullable(commands.get(actualName));
        }

        return Optional.empty();
    }

    @Override
    public CommandResult execute(String commandName, CommandContext context) {
        Optional<CliCommand> commandOpt = route(commandName);

        if (commandOpt.isEmpty()) {
            return CommandResult.notFound("Unknown command: " + commandName);
        }

        CliCommand command = commandOpt.get();

        try {
            log.debug("Executing command: {}", commandName);
            return command.execute(context);
        } catch (Exception e) {
            log.error("Error executing command: {}", commandName, e);
            return CommandResult.error("Command execution failed", e);
        }
    }

    @Override
    public List<CliCommand> getAllCommands() {
        return new ArrayList<>(commands.values());
    }

    @Override
    public List<CliCommand> getCommandsByCategory(String category) {
        return commands.values().stream()
                .filter(cmd -> category.equals(cmd.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasCommand(String name) {
        return commands.containsKey(name) || aliases.containsKey(name);
    }

    @Override
    public List<String> getSuggestions(String partial) {
        if (partial == null || partial.isEmpty()) {
            return new ArrayList<>(commands.keySet());
        }

        String lowerPartial = partial.toLowerCase();
        List<String> suggestions = new ArrayList<>();

        // 匹配命令名
        for (String name : commands.keySet()) {
            if (name.toLowerCase().startsWith(lowerPartial)) {
                suggestions.add(name);
            }
        }

        // 匹配别名
        for (String alias : aliases.keySet()) {
            if (alias.toLowerCase().startsWith(lowerPartial) && !suggestions.contains(alias)) {
                suggestions.add(alias);
            }
        }

        return suggestions;
    }
}
