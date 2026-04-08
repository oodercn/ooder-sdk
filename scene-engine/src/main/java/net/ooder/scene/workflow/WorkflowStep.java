package net.ooder.scene.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流步骤
 *
 * <p>表示工作流中的一个执行步骤，包含步骤类型、配置和执行状态。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class WorkflowStep {

    private String stepId;
    private String name;
    private String description;
    private int sequence;
    private String stepType;
    private Map<String, Object> config;
    private WorkflowStepStatus status;
    private String result;
    private long startTime;
    private long endTime;
    private String errorMessage;

    // ===== 扩展字段（兼容现有工作流引擎）=====
    private List<String> dependsOn;
    private String output;
    private String agentId;
    private String capId;
    private Duration timeout;
    private Map<String, Object> inputs;
    private String condition;

    public WorkflowStep() {
        this.status = WorkflowStepStatus.PENDING;
        this.config = new HashMap<>();
        this.dependsOn = new ArrayList<>();
        this.inputs = new HashMap<>();
    }

    // ===== Getters and Setters =====

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getStepType() {
        return stepType;
    }

    public void setStepType(String stepType) {
        this.stepType = stepType;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public WorkflowStepStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStepStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // ===== 扩展字段 Getters and Setters =====

    public List<String> getDependsOn() {
        return dependsOn != null ? dependsOn : new ArrayList<>();
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCapId() {
        return capId;
    }

    public void setCapId(String capId) {
        this.capId = capId;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Map<String, Object> getInputs() {
        return inputs != null ? inputs : new HashMap<>();
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    // ===== 便捷方法 =====

    /**
     * 获取执行时长（毫秒）
     */
    public long getDuration() {
        if (startTime > 0 && endTime > 0) {
            return endTime - startTime;
        }
        return 0;
    }

    /**
     * 检查是否已完成
     */
    public boolean isCompleted() {
        return status == WorkflowStepStatus.COMPLETED;
    }

    /**
     * 检查是否失败
     */
    public boolean isFailed() {
        return status == WorkflowStepStatus.FAILED;
    }

    /**
     * 检查是否有依赖
     */
    public boolean hasDependencies() {
        return dependsOn != null && !dependsOn.isEmpty();
    }

    /**
     * 添加依赖
     */
    public void addDependency(String stepId) {
        if (this.dependsOn == null) {
            this.dependsOn = new ArrayList<>();
        }
        this.dependsOn.add(stepId);
    }

    /**
     * 获取配置参数
     */
    public Map<String, Object> getParams() {
        return config != null ? config : new HashMap<>();
    }

    @Override
    public String toString() {
        return "WorkflowStep{" +
            "stepId='" + stepId + '\'' +
            ", name='" + name + '\'' +
            ", sequence=" + sequence +
            ", status=" + status +
            '}';
    }

    /**
     * 时长配置类
     */
    public static class Duration {
        private long duration;
        private java.util.concurrent.TimeUnit unit;

        public Duration() {
            this.duration = 30000;
            this.unit = java.util.concurrent.TimeUnit.MILLISECONDS;
        }

        public Duration(long duration, java.util.concurrent.TimeUnit unit) {
            this.duration = duration;
            this.unit = unit;
        }

        public long getDuration() {
            return unit.toMillis(duration);
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public java.util.concurrent.TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(java.util.concurrent.TimeUnit unit) {
            this.unit = unit;
        }

        public static Duration ofMillis(long millis) {
            return new Duration(millis, java.util.concurrent.TimeUnit.MILLISECONDS);
        }

        public static Duration ofSeconds(long seconds) {
            return new Duration(seconds, java.util.concurrent.TimeUnit.SECONDS);
        }

        public static Duration ofMinutes(long minutes) {
            return new Duration(minutes, java.util.concurrent.TimeUnit.MINUTES);
        }
    }
}
