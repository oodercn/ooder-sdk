package net.ooder.sdk.cli.api;

import java.util.List;
import java.util.Map;

/**
 * CLI解析器接口
 *
 * <p>负责解析命令行参数</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface CliParser {

    /**
     * 解析命令行参数
     *
     * @param args 命令行参数
     * @return 解析结果
     */
    ParseResult parse(String[] args);

    /**
     * 获取命令名称
     *
     * @param args 命令行参数
     * @return 命令名称
     */
    String getCommandName(String[] args);

    /**
     * 获取命令选项
     *
     * @param args 命令行参数
     * @return 选项映射
     */
    Map<String, String> getOptions(String[] args);

    /**
     * 获取位置参数
     *
     * @param args 命令行参数
     * @return 位置参数列表
     */
    List<String> getPositionalArgs(String[] args);

    /**
     * 打印帮助信息
     *
     * @param commandName 命令名称
     */
    void printHelp(String commandName);

    /**
     * 打印全局帮助信息
     */
    void printGlobalHelp();

    /**
     * 解析结果
     */
    class ParseResult {
        private final String command;
        private final Map<String, String> options;
        private final List<String> positionalArgs;
        private final boolean helpRequested;
        private final String errorMessage;

        public ParseResult(String command, Map<String, String> options,
                          List<String> positionalArgs, boolean helpRequested, String errorMessage) {
            this.command = command;
            this.options = options;
            this.positionalArgs = positionalArgs;
            this.helpRequested = helpRequested;
            this.errorMessage = errorMessage;
        }

        public String getCommand() {
            return command;
        }

        public Map<String, String> getOptions() {
            return options;
        }

        public List<String> getPositionalArgs() {
            return positionalArgs;
        }

        public boolean isHelpRequested() {
            return helpRequested;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean hasError() {
            return errorMessage != null;
        }
    }
}
