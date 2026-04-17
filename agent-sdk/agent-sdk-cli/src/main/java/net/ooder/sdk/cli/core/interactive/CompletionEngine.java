package net.ooder.sdk.cli.core.interactive;

import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.sdk.cli.api.InteractiveCli;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动补全引擎
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CompletionEngine implements InteractiveCli.Completer {

    private final CliRouter router;

    public CompletionEngine(CliRouter router) {
        this.router = router;
    }

    @Override
    public List<String> complete(String buffer, int cursor) {
        List<String> suggestions = new ArrayList<>();

        if (buffer == null || buffer.isEmpty()) {
            // 返回所有命令
            router.getAllCommands().forEach(cmd -> suggestions.add(cmd.getName()));
            return suggestions;
        }

        // 解析当前输入
        String[] parts = buffer.substring(0, cursor).split("\\s+");

        if (parts.length == 1) {
            // 补全命令名
            String partial = parts[0];
            suggestions.addAll(router.getSuggestions(partial));
        } else if (parts.length >= 2) {
            // 补全命令选项
            String command = parts[0];
            String current = parts[parts.length - 1];

            if (current.startsWith("--")) {
                // 补全长选项
                suggestions.addAll(getLongOptions(command, current));
            } else if (current.startsWith("-")) {
                // 补全短选项
                suggestions.addAll(getShortOptions(command, current));
            } else {
                // 补全值
                suggestions.addAll(getValueSuggestions(command, parts[parts.length - 2], current));
            }
        }

        return suggestions;
    }

    /**
     * 获取长选项补全
     *
     * @param command 命令
     * @param partial 部分输入
     * @return 建议列表
     */
    private List<String> getLongOptions(String command, String partial) {
        List<String> options = new ArrayList<>();

        // 通用选项
        if ("--skill-id".startsWith(partial)) options.add("--skill-id");
        if ("--group-id".startsWith(partial)) options.add("--group-id");
        if ("--output".startsWith(partial)) options.add("--output");
        if ("--verbose".startsWith(partial)) options.add("--verbose");
        if ("--quiet".startsWith(partial)) options.add("--quiet");
        if ("--help".startsWith(partial)) options.add("--help");

        return options;
    }

    /**
     * 获取短选项补全
     *
     * @param command 命令
     * @param partial 部分输入
     * @return 建议列表
     */
    private List<String> getShortOptions(String command, String partial) {
        List<String> options = new ArrayList<>();

        if ("-v".startsWith(partial)) options.add("-v");
        if ("-q".startsWith(partial)) options.add("-q");
        if ("-o".startsWith(partial)) options.add("-o");
        if ("-h".startsWith(partial)) options.add("-h");

        return options;
    }

    /**
     * 获取值补全建议
     *
     * @param command 命令
     * @param option 选项
     * @param partial 部分输入
     * @return 建议列表
     */
    private List<String> getValueSuggestions(String command, String option, String partial) {
        List<String> suggestions = new ArrayList<>();

        if ("--output".equals(option) || "-o".equals(option)) {
            if ("text".startsWith(partial)) suggestions.add("text");
            if ("json".startsWith(partial)) suggestions.add("json");
            if ("table".startsWith(partial)) suggestions.add("table");
        }

        return suggestions;
    }
}
