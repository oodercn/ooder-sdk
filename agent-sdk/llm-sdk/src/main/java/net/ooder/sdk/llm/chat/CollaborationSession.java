package net.ooder.sdk.llm.chat;

import net.ooder.sdk.llm.model.AgentInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 协作会话接口
 */
public interface CollaborationSession {

    /**
     * 获取会话ID
     */
    String getSessionId();

    /**
     * 分配子任务给智能体
     * @param assignments 任务分配列表
     */
    void assignTasks(List<TaskAssignment> assignments);

    /**
     * 等待协作完成
     * @param timeoutMs 超时时间（毫秒）
     * @return 协作结果
     */
    CompletableFuture<CollaborationResult> waitForCompletion(long timeoutMs);

    /**
     * 结束协作会话
     */
    void end();

    /**
     * 任务分配
     */
    class TaskAssignment {
        private String agentId;
        private String subTask;
        private int priority;
        private long deadline;

        public TaskAssignment(String agentId, String subTask, int priority) {
            this.agentId = agentId;
            this.subTask = subTask;
            this.priority = priority;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getSubTask() {
            return subTask;
        }

        public void setSubTask(String subTask) {
            this.subTask = subTask;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public long getDeadline() {
            return deadline;
        }

        public void setDeadline(long deadline) {
            this.deadline = deadline;
        }
    }

    /**
     * 协作结果
     */
    class CollaborationResult {
        private String sessionId;
        private boolean success;
        private String summary;
        private List<AgentContribution> contributions;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<AgentContribution> getContributions() {
            return contributions;
        }

        public void setContributions(List<AgentContribution> contributions) {
            this.contributions = contributions;
        }
    }

    /**
     * 智能体贡献
     */
    class AgentContribution {
        private String agentId;
        private String agentName;
        private String content;
        private long contributionTime;

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getContributionTime() {
            return contributionTime;
        }

        public void setContributionTime(long contributionTime) {
            this.contributionTime = contributionTime;
        }
    }
}
