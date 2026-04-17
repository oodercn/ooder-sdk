package net.ooder.sdk.cli.core.parser;

import net.ooder.sdk.cli.api.CliParser;
import picocli.CommandLine;

import java.util.*;

/**
 * 基于picocli的命令解析器
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class PicocliParser implements CliParser {

    private final Map<String, CommandLine> commandLines = new HashMap<>();
    private CommandLine mainCommandLine;

    public PicocliParser() {
        // 初始化主命令
        this.mainCommandLine = new CommandLine(new MainCommand());
    }

    @Override
    public ParseResult parse(String[] args) {
        if (args == null || args.length == 0) {
            return new ParseResult(null, Collections.emptyMap(),
                    Collections.emptyList(), true, null);
        }

        try {
            String command = args[0];
            if (command.startsWith("-")) {
                // 全局选项
                CommandLine.ParseResult parseResult = mainCommandLine.parseArgs(args);
                return new ParseResult(
                        null,
                        extractOptions(parseResult),
                        extractPositionalArgs(parseResult),
                        mainCommandLine.isUsageHelpRequested(),
                        null
                );
            }

            // 子命令
            CommandLine subCommandLine = commandLines.get(command);
            if (subCommandLine != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                CommandLine.ParseResult parseResult = subCommandLine.parseArgs(subArgs);

                return new ParseResult(
                        command,
                        extractOptions(parseResult),
                        extractPositionalArgs(parseResult),
                        subCommandLine.isUsageHelpRequested(),
                        null
                );
            }

            return new ParseResult(command, Collections.emptyMap(),
                    Collections.emptyList(), false, "Unknown command: " + command);

        } catch (CommandLine.ParameterException e) {
            return new ParseResult(null, Collections.emptyMap(),
                    Collections.emptyList(), false, e.getMessage());
        }
    }

    @Override
    public String getCommandName(String[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        String first = args[0];
        return first.startsWith("-") ? null : first;
    }

    @Override
    public Map<String, String> getOptions(String[] args) {
        ParseResult result = parse(args);
        return result.getOptions();
    }

    @Override
    public List<String> getPositionalArgs(String[] args) {
        ParseResult result = parse(args);
        return result.getPositionalArgs();
    }

    @Override
    public void printHelp(String commandName) {
        CommandLine cmd = commandLines.get(commandName);
        if (cmd != null) {
            cmd.usage(System.out);
        } else {
            System.out.println("Unknown command: " + commandName);
        }
    }

    @Override
    public void printGlobalHelp() {
        mainCommandLine.usage(System.out);
    }

    /**
     * 注册命令
     *
     * @param name 命令名称
     * @param command 命令对象
     */
    public void registerCommand(String name, Object command) {
        CommandLine cmd = new CommandLine(command);
        commandLines.put(name, cmd);
        mainCommandLine.addSubcommand(name, cmd);
    }

    /**
     * 从picocli解析结果提取选项
     */
    private Map<String, String> extractOptions(CommandLine.ParseResult parseResult) {
        Map<String, String> options = new HashMap<>();

        if (parseResult == null) {
            return options;
        }

        // 提取匹配的选项
        List<CommandLine.Model.OptionSpec> matchedOptions = parseResult.matchedOptions();
        for (CommandLine.Model.OptionSpec option : matchedOptions) {
            String key = option.longestName().replaceFirst("^-+", "");
            Object value = option.getValue();
            options.put(key, value != null ? value.toString() : "true");
        }

        return options;
    }

    /**
     * 从picocli解析结果提取位置参数
     */
    private List<String> extractPositionalArgs(CommandLine.ParseResult parseResult) {
        List<String> args = new ArrayList<>();

        if (parseResult == null) {
            return args;
        }

        // 提取位置参数
        List<CommandLine.Model.PositionalParamSpec> matchedPositionals = parseResult.matchedPositionals();
        for (CommandLine.Model.PositionalParamSpec positional : matchedPositionals) {
            Object value = positional.getValue();
            if (value instanceof List) {
                for (Object v : (List<?>) value) {
                    args.add(v != null ? v.toString() : "");
                }
            } else if (value != null) {
                args.add(value.toString());
            }
        }

        return args;
    }

    /**
     * 主命令定义
     */
    @CommandLine.Command(
            name = "ooder",
            description = "Ooder Agent SDK CLI",
            mixinStandardHelpOptions = true,
            version = "3.1.0"
    )
    public static class MainCommand {
        @CommandLine.Option(names = {"-v", "--verbose"}, description = "Verbose mode")
        private boolean verbose;

        @CommandLine.Option(names = {"-q", "--quiet"}, description = "Quiet mode")
        private boolean quiet;

        @CommandLine.Option(names = {"-o", "--output"}, description = "Output format (text, json, table)")
        private String outputFormat = "text";
    }
}
