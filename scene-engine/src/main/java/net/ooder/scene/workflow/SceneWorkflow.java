package net.ooder.scene.workflow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景组工作流
 *
 * <p>表示场景组的工作流定义，包含工作流步骤、触发条件和执行状态。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneWorkflow {

    private String workflowId;
    private String sceneGroupId;
    private String name;
    private String description;
    private WorkflowStatus status;
    private WorkflowTriggerType triggerType;
    private String triggerConfig;
    private List<WorkflowStep> steps;
    private Map<String, Object> variables;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String creatorId;
    private int version;
    private boolean autoStart;
    private boolean triggerEnabled;

    public SceneWorkflow() {
        this.status = WorkflowStatus.DRAFT;
        this.steps = new ArrayList<>();
        this.variables = new HashMap<>();
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.version = 1;
    }

    // ===== Getters and Setters =====

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
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

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public WorkflowTriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(WorkflowTriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerConfig() {
        return triggerConfig;
    }

    public void setTriggerConfig(String triggerConfig) {
        this.triggerConfig = triggerConfig;
    }

    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean isTriggerEnabled() {
        return triggerEnabled;
    }

    public void setTriggerEnabled(boolean triggerEnabled) {
        this.triggerEnabled = triggerEnabled;
    }

    // ===== 便捷方法 =====

    /**
     * 添加步骤
     */
    public void addStep(WorkflowStep step) {
        if (this.steps == null) {
            this.steps = new ArrayList<>();
        }
        step.setSequence(this.steps.size() + 1);
        this.steps.add(step);
    }

    /**
     * 获取步骤数量
     */
    public int getStepCount() {
        return steps != null ? steps.size() : 0;
    }

    /**
     * 检查是否可执行
     */
    public boolean isExecutable() {
        return status == WorkflowStatus.ACTIVE && steps != null && !steps.isEmpty();
    }

    /**
     * 获取当前执行中的步骤
     */
    public WorkflowStep getCurrentStep() {
        if (steps == null) {
            return null;
        }
        for (WorkflowStep step : steps) {
            if (step.getStatus() == WorkflowStepStatus.RUNNING) {
                return step;
            }
        }
        // 返回第一个等待中的步骤
        for (WorkflowStep step : steps) {
            if (step.getStatus() == WorkflowStepStatus.PENDING) {
                return step;
            }
        }
        return null;
    }

    /**
     * 获取已完成步骤数
     */
    public int getCompletedStepCount() {
        if (steps == null) {
            return 0;
        }
        int count = 0;
        for (WorkflowStep step : steps) {
            if (step.getStatus() == WorkflowStepStatus.COMPLETED) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取执行进度（0-100）
     */
    public int getProgress() {
        if (steps == null || steps.isEmpty()) {
            return 0;
        }
        return (getCompletedStepCount() * 100) / steps.size();
    }

    @Override
    public String toString() {
        return "SceneWorkflow{" +
            "workflowId='" + workflowId + '\'' +
            ", sceneGroupId='" + sceneGroupId + '\'' +
            ", name='" + name + '\'' +
            ", status=" + status +
            ", steps=" + getStepCount() +
            ", progress=" + getProgress() + "%" +
            '}';
    }
}
