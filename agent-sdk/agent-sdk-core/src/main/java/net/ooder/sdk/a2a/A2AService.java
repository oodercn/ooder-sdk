package net.ooder.sdk.a2a;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A2A 服务接口
 * 由 AGENT-SDK 实现，Engine 调用
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface A2AService {

    /**
     * 发送 Command
     *
     * @param command 命令
     * @return 响应
     */
    A2ACommandResponse sendCommand(A2ACommand command);

    /**
     * 异步发送 Command
     *
     * @param command 命令
     * @param callback 回调
     */
    void sendCommandAsync(A2ACommand command, CommandCallback callback);

    /**
     * 传递上下文
     *
     * @param transfer 上下文传递
     * @return 传递结果
     */
    TransferResult transferContext(ContextTransfer transfer);

    /**
     * 注册 Agent
     *
     * @param agentInfo Agent 信息
     * @return 注册结果
     */
    RegistrationResult registerAgent(AgentInfo agentInfo);

    /**
     * 发现 Agent
     *
     * @param criteria 发现条件
     * @return Agent 列表
     */
    List<AgentInfo> discoverAgents(DiscoveryCriteria criteria);

    /**
     * 命令回调
     */
    interface CommandCallback {
        void onResponse(A2ACommandResponse response);
        void onError(Exception error);
    }

    /**
     * 传递结果
     */
    class TransferResult {
        private boolean success;
        private String transferId;
        private String errorMessage;
        private long transferTime;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getTransferId() { return transferId; }
        public void setTransferId(String transferId) { this.transferId = transferId; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getTransferTime() { return transferTime; }
        public void setTransferTime(long transferTime) { this.transferTime = transferTime; }
    }

    /**
     * 注册结果
     */
    class RegistrationResult {
        private boolean success;
        private String agentId;
        private String token;
        private String errorMessage;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
