package net.ooder.scene.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import net.ooder.scene.workflow.WorkflowStatus;
import net.ooder.scene.workflow.WorkflowTriggerType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 工作流数据传输对象
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class WorkflowDTO {

    private String workflowId;

    @NotBlank(message = "场景组ID不能为空")
    private String sceneGroupId;

    @NotBlank(message = "工作流名称不能为空")
    @Size(max = 500, message = "工作流名称不能超过500个字符")
    private String name;

    @Size(max = 2000, message = "描述不能超过2000个字符")
    private String description;

    private WorkflowStatus status;

    private WorkflowTriggerType triggerType;

    private String triggerConfig;

    private List<WorkflowStepDTO> steps;

    private Map<String, Object> variables;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creatorId;

    private Integer version;

    private Boolean autoStart;

    private Boolean triggerEnabled;

    public WorkflowDTO() {
    }

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

    public List<WorkflowStepDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStepDTO> steps) {
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(Boolean autoStart) {
        this.autoStart = autoStart;
    }

    public Boolean getTriggerEnabled() {
        return triggerEnabled;
    }

    public void setTriggerEnabled(Boolean triggerEnabled) {
        this.triggerEnabled = triggerEnabled;
    }
}
