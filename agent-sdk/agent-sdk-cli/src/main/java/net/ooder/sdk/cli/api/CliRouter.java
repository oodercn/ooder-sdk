package net.ooder.sdk.cli.api;

import java.util.List;
import java.util.Optional;

/**
 * CLI路由器接口
 *
 * <p>负责路由命令到对应的处理器</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface CliRouter {

    /**
     * 注册命令
     *
     * @param command 命令
     */
    void register(CliCommand command);

    /**
     * 注册命令
     *
     * @param name 命令名称
     * @param command 命令
     */
    void register(String name, CliCommand command);

    /**
     * 注销命令
     *
     * @param name 命令名称
     */
    void unregister(String name);

    /**
     * 路由命令
     *
     * @param commandName 命令名称
     * @return 命令实例
     */
    Optional<CliCommand> route(String commandName);

    /**
     * 执行命令
     *
     * @param commandName 命令名称
     * @param context 命令上下文
     * @return 执行结果
     */
    CommandResult execute(String commandName, CommandContext context);

    /**
     * 获取所有命令
     *
     * @return 命令列表
     */
    List<CliCommand> getAllCommands();

    /**
     * 获取分类命令
     *
     * @param category 分类
     * @return 命令列表
     */
    List<CliCommand> getCommandsByCategory(String category);

    /**
     * 检查命令是否存在
     *
     * @param name 命令名称
     * @return 是否存在
     */
    boolean hasCommand(String name);

    /**
     * 获取命令建议
     *
     * @param partial 部分命令名
     * @return 建议列表
     */
    List<String> getSuggestions(String partial);
}
