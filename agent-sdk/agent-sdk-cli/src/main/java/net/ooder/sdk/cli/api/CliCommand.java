package net.ooder.sdk.cli.api;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * CLI命令接口
 *
 * <p>所有CLI命令必须实现此接口</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface CliCommand extends Callable<Integer> {

    /**
     * 获取命令名称
     *
     * @return 命令名称
     */
    String getName();

    /**
     * 获取命令描述
     *
     * @return 命令描述
     */
    String getDescription();

    /**
     * 获取命令用法
     *
     * @return 命令用法
     */
    String getUsage();

    /**
     * 执行命令
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    CommandResult execute(CommandContext context);

    /**
     * 检查是否需要交互式模式
     *
     * @return 是否需要交互式模式
     */
    default boolean isInteractive() {
        return false;
    }

    /**
     * 获取命令分类
     *
     * @return 命令分类
     */
    default String getCategory() {
        return "general";
    }

    /**
     * 获取命令别名
     *
     * @return 命令别名数组
     */
    default String[] getAliases() {
        return new String[0];
    }

    /**
     * 验证参数
     *
     * @param args 参数
     * @return 验证结果
     */
    default boolean validate(String[] args) {
        return true;
    }

    @Override
    default Integer call() throws Exception {
        CommandContext context = new CommandContext();
        CommandResult result = execute(context);
        return result.getExitCode();
    }
}
