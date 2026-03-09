package net.ooder.sdk.agent.command;

import net.ooder.sdk.a2a.A2ACommand;
import net.ooder.sdk.a2a.A2ACommandResponse;

/**
 * 场景协作命令处理器
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface SceneCommandHandler {

    /**
     * 处理安装命令
     * @param command 命令
     * @return 响应
     */
    A2ACommandResponse handleInstall(A2ACommand command);

    /**
     * 处理激活命令
     * @param command 命令
     * @return 响应
     */
    A2ACommandResponse handleActivate(A2ACommand command);

    /**
     * 处理停用命令
     * @param command 命令
     * @return 响应
     */
    A2ACommandResponse handleDeactivate(A2ACommand command);

    /**
     * 处理卸载命令
     * @param command 命令
     * @return 响应
     */
    A2ACommandResponse handleUninstall(A2ACommand command);

    /**
     * 处理状态查询命令
     * @param command 命令
     * @return 响应
     */
    A2ACommandResponse handleStatus(A2ACommand command);

    /**
     * 处理同步命令
     * @param command 命令
     * @return 响应
     */
    A2ACommandResponse handleSync(A2ACommand command);
}
