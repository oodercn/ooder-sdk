package net.ooder.sdk.a2a;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A2A 客户端接口
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface A2AClient {

    /**
     * 发送命令到目标Agent
     * @param targetAgentId 目标Agent ID
     * @param command 命令
     * @return 响应
     */
    CompletableFuture<A2ACommandResponse> sendCommand(String targetAgentId, A2ACommand command);

    /**
     * 发送消息到目标Agent
     * @param targetAgentId 目标Agent ID
     * @param message 消息内容
     * @return 是否成功
     */
    CompletableFuture<Boolean> sendMessage(String targetAgentId, Map<String, Object> message);

    /**
     * 广播消息到多个Agent
     * @param agentIds Agent ID列表
     * @param message 消息内容
     * @return 发送结果
     */
    CompletableFuture<Map<String, Boolean>> broadcastMessage(java.util.List<String> agentIds, Map<String, Object> message);
}
