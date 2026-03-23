package net.ooder.sdk.a2a;

import net.ooder.sdk.a2a.capability.SkillCardV3;
import net.ooder.skills.api.SkillCategory;
import net.ooder.skills.api.SkillForm;
import net.ooder.skills.api.SceneType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A2A 服务接口
 * 由 AGENT-SDK 实现，Engine 调用
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
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
     * 批量传递上下文
     *
     * @param transfers 上下文传递列表
     * @return 批量传递结果
     */
    BatchTransferResult batchTransferContext(List<ContextTransfer> transfers);

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

    // ========== v3.0 技能发现方法 ==========

    /**
     * 发现技能（v3.0）
     *
     * @param form 技能形态过滤（可选）
     * @param category 技能分类过滤（可选）
     * @param sceneType 场景类型过滤（可选）
     * @return 匹配的技能列表
     */
    List<SkillCardV3> discoverSkills(SkillForm form, SkillCategory category, SceneType sceneType);

    /**
     * 发现场景技能
     */
    default List<SkillCardV3> discoverSceneSkills() {
        return discoverSkills(SkillForm.SCENE, null, null);
    }

    /**
     * 发现自主场景
     */
    default List<SkillCardV3> discoverAutoScenes() {
        return discoverSkills(SkillForm.SCENE, null, SceneType.AUTO);
    }

    /**
     * 发现触发场景
     */
    default List<SkillCardV3> discoverTriggerScenes() {
        return discoverSkills(SkillForm.SCENE, null, SceneType.TRIGGER);
    }

    /**
     * 发现独立技能
     */
    default List<SkillCardV3> discoverStandaloneSkills() {
        return discoverSkills(SkillForm.STANDALONE, null, null);
    }

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
     * 批量传递结果
     */
    class BatchTransferResult {
        private boolean success;
        private int totalCount;
        private int successCount;
        private int failedCount;
        private List<TransferResult> results;
        private String errorMessage;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailedCount() { return failedCount; }
        public void setFailedCount(int failedCount) { this.failedCount = failedCount; }
        public List<TransferResult> getResults() { return results; }
        public void setResults(List<TransferResult> results) { this.results = results; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
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
