package net.ooder.scene.workflow.service;

import net.ooder.scene.workflow.*;
import net.ooder.scene.workflow.dto.WorkflowDTO;
import net.ooder.scene.workflow.dto.WorkflowStepDTO;
import net.ooder.scene.workflow.dto.WorkflowExecutionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 工作流服务实现
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    private final SqlSceneWorkflowManager workflowManager;

    @Autowired
    public WorkflowServiceImpl(SqlSceneWorkflowManager workflowManager) {
        this.workflowManager = workflowManager;
    }

    @Override
    public WorkflowDTO createWorkflow(WorkflowDTO dto) {
        log.info("Creating workflow: sceneGroupId={}, name={}", dto.getSceneGroupId(), dto.getName());
        SceneWorkflow workflow = workflowManager.createWorkflow(
            dto.getSceneGroupId(),
            dto.getName(),
            dto.getDescription()
        );
        if (dto.getVariables() != null) {
            workflow.setVariables(dto.getVariables());
            workflowManager.updateWorkflow(workflow);
        }
        return toDTO(workflow);
    }

    @Override
    public WorkflowDTO getWorkflow(String workflowId) {
        log.debug("Getting workflow: workflowId={}", workflowId);
        SceneWorkflow workflow = workflowManager.getWorkflow(workflowId);
        if (workflow == null) {
            return null;
        }
        return toDTO(workflow);
    }

    @Override
    public List<WorkflowDTO> listWorkflows(String sceneGroupId) {
        log.debug("Listing workflows: sceneGroupId={}", sceneGroupId);
        List<SceneWorkflow> workflows = workflowManager.listWorkflows(sceneGroupId);
        return workflows.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public WorkflowDTO updateWorkflow(String workflowId, WorkflowDTO dto) {
        log.info("Updating workflow: workflowId={}", workflowId);
        SceneWorkflow workflow = workflowManager.getWorkflow(workflowId);
        if (workflow == null) {
            throw new IllegalArgumentException("Workflow not found: " + workflowId);
        }
        if (dto.getName() != null) {
            workflow.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            workflow.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            workflow.setStatus(dto.getStatus());
        }
        if (dto.getTriggerType() != null) {
            workflow.setTriggerType(dto.getTriggerType());
        }
        if (dto.getTriggerConfig() != null) {
            workflow.setTriggerConfig(dto.getTriggerConfig());
        }
        if (dto.getVariables() != null) {
            workflow.setVariables(dto.getVariables());
        }
        if (dto.getAutoStart() != null) {
            workflow.setAutoStart(dto.getAutoStart());
        }
        workflowManager.updateWorkflow(workflow);
        return toDTO(workflow);
    }

    @Override
    public boolean deleteWorkflow(String workflowId) {
        log.info("Deleting workflow: workflowId={}", workflowId);
        return workflowManager.deleteWorkflow(workflowId);
    }

    @Override
    public boolean activateWorkflow(String workflowId) {
        log.info("Activating workflow: workflowId={}", workflowId);
        return workflowManager.activateWorkflow(workflowId);
    }

    @Override
    public boolean pauseWorkflow(String workflowId) {
        log.info("Pausing workflow: workflowId={}", workflowId);
        return workflowManager.pauseWorkflow(workflowId);
    }

    @Override
    public WorkflowExecutionDTO startWorkflow(String workflowId, Map<String, Object> inputData) {
        log.info("Starting workflow: workflowId={}", workflowId);
        WorkflowExecution execution = workflowManager.executeWorkflow(workflowId, inputData);
        return toExecutionDTO(execution);
    }

    @Override
    public List<WorkflowStepDTO> getWorkflowSteps(String workflowId) {
        log.debug("Getting workflow steps: workflowId={}", workflowId);
        SceneWorkflow workflow = workflowManager.getWorkflow(workflowId);
        if (workflow == null || workflow.getSteps() == null) {
            return new ArrayList<>();
        }
        return workflow.getSteps().stream()
            .map(this::toStepDTO)
            .collect(Collectors.toList());
    }

    @Override
    public boolean addWorkflowStep(String workflowId, WorkflowStepDTO dto) {
        log.info("Adding workflow step: workflowId={}, stepName={}", workflowId, dto.getName());
        WorkflowStep step = toStepEntity(dto);
        if (step.getStepId() == null || step.getStepId().isEmpty()) {
            step.setStepId("step-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));
        }
        return workflowManager.addWorkflowStep(workflowId, step);
    }

    @Override
    public boolean updateWorkflowStep(String workflowId, String stepId, WorkflowStepDTO dto) {
        log.info("Updating workflow step: workflowId={}, stepId={}", workflowId, stepId);
        WorkflowStep step = toStepEntity(dto);
        step.setStepId(stepId);
        return workflowManager.updateWorkflowStep(workflowId, step);
    }

    @Override
    public boolean deleteWorkflowStep(String workflowId, String stepId) {
        log.info("Deleting workflow step: workflowId={}, stepId={}", workflowId, stepId);
        return workflowManager.deleteWorkflowStep(workflowId, stepId);
    }

    @Override
    public boolean reorderSteps(String workflowId, List<String> stepIds) {
        log.info("Reordering steps: workflowId={}, stepCount={}", workflowId, stepIds.size());
        return workflowManager.reorderSteps(workflowId, stepIds);
    }

    @Override
    public List<WorkflowExecutionDTO> getExecutionHistory(String workflowId) {
        log.debug("Getting execution history: workflowId={}", workflowId);
        List<WorkflowExecution> executions = workflowManager.listExecutions(workflowId);
        return executions.stream()
            .map(this::toExecutionDTO)
            .collect(Collectors.toList());
    }

    @Override
    public boolean setTrigger(String workflowId, String triggerType, String triggerConfig) {
        log.info("Setting trigger: workflowId={}, triggerType={}", workflowId, triggerType);
        WorkflowTriggerType type = WorkflowTriggerType.valueOf(triggerType);
        return workflowManager.setTrigger(workflowId, type, triggerConfig);
    }

    @Override
    public boolean enableTrigger(String workflowId) {
        log.info("Enabling trigger: workflowId={}", workflowId);
        return workflowManager.setTriggerEnabled(workflowId, true);
    }

    @Override
    public boolean disableTrigger(String workflowId) {
        log.info("Disabling trigger: workflowId={}", workflowId);
        return workflowManager.setTriggerEnabled(workflowId, false);
    }

    @Override
    public WorkflowExecutionDTO getExecution(String executionId) {
        log.debug("Getting execution: executionId={}", executionId);
        WorkflowExecution execution = workflowManager.getExecution(executionId);
        if (execution == null) {
            return null;
        }
        return toExecutionDTO(execution);
    }

    @Override
    public boolean cancelExecution(String executionId) {
        log.info("Cancelling execution: executionId={}", executionId);
        return workflowManager.cancelExecution(executionId);
    }

    @Override
    public WorkflowExecutionDTO retryExecution(String executionId) {
        log.info("Retrying execution: executionId={}", executionId);
        WorkflowExecution execution = workflowManager.retryExecution(executionId);
        return toExecutionDTO(execution);
    }

    @Override
    public String getExecutionStatus(String executionId) {
        log.debug("Getting execution status: executionId={}", executionId);
        WorkflowExecution execution = workflowManager.getExecution(executionId);
        if (execution == null) {
            return null;
        }
        return execution.getStatus().name();
    }

    private WorkflowDTO toDTO(SceneWorkflow workflow) {
        WorkflowDTO dto = new WorkflowDTO();
        dto.setWorkflowId(workflow.getWorkflowId());
        dto.setSceneGroupId(workflow.getSceneGroupId());
        dto.setName(workflow.getName());
        dto.setDescription(workflow.getDescription());
        dto.setStatus(workflow.getStatus());
        dto.setTriggerType(workflow.getTriggerType());
        dto.setTriggerConfig(workflow.getTriggerConfig());
        dto.setVariables(workflow.getVariables());
        dto.setCreateTime(workflow.getCreateTime());
        dto.setUpdateTime(workflow.getUpdateTime());
        dto.setCreatorId(workflow.getCreatorId());
        dto.setVersion(workflow.getVersion());
        dto.setAutoStart(workflow.isAutoStart());
        dto.setTriggerEnabled(workflow.isTriggerEnabled());
        if (workflow.getSteps() != null) {
            dto.setSteps(workflow.getSteps().stream()
                .map(this::toStepDTO)
                .collect(Collectors.toList()));
        }
        return dto;
    }

    private WorkflowStepDTO toStepDTO(WorkflowStep step) {
        WorkflowStepDTO dto = new WorkflowStepDTO();
        dto.setStepId(step.getStepId());
        dto.setName(step.getName());
        dto.setDescription(step.getDescription());
        dto.setSequence(step.getSequence());
        dto.setStepType(step.getStepType());
        dto.setConfig(step.getConfig());
        dto.setStatus(step.getStatus());
        dto.setResult(step.getResult());
        dto.setStartTime(step.getStartTime());
        dto.setEndTime(step.getEndTime());
        dto.setErrorMessage(step.getErrorMessage());
        dto.setDependsOn(step.getDependsOn());
        dto.setOutput(step.getOutput());
        dto.setAgentId(step.getAgentId());
        dto.setCapId(step.getCapId());
        dto.setInputs(step.getInputs());
        dto.setCondition(step.getCondition());
        return dto;
    }

    private WorkflowStep toStepEntity(WorkflowStepDTO dto) {
        WorkflowStep step = new WorkflowStep();
        step.setStepId(dto.getStepId());
        step.setName(dto.getName());
        step.setDescription(dto.getDescription());
        step.setSequence(dto.getSequence() != null ? dto.getSequence() : 0);
        step.setStepType(dto.getStepType());
        step.setConfig(dto.getConfig());
        step.setStatus(dto.getStatus());
        step.setResult(dto.getResult());
        step.setStartTime(dto.getStartTime() != null ? dto.getStartTime() : 0);
        step.setEndTime(dto.getEndTime() != null ? dto.getEndTime() : 0);
        step.setErrorMessage(dto.getErrorMessage());
        step.setDependsOn(dto.getDependsOn());
        step.setOutput(dto.getOutput());
        step.setAgentId(dto.getAgentId());
        step.setCapId(dto.getCapId());
        step.setInputs(dto.getInputs());
        step.setCondition(dto.getCondition());
        return step;
    }

    private WorkflowExecutionDTO toExecutionDTO(WorkflowExecution execution) {
        WorkflowExecutionDTO dto = new WorkflowExecutionDTO();
        dto.setExecutionId(execution.getExecutionId());
        dto.setWorkflowId(execution.getWorkflowId());
        dto.setSceneGroupId(execution.getSceneGroupId());
        dto.setStatus(execution.getStatus());
        dto.setStartTime(execution.getStartTime());
        dto.setEndTime(execution.getEndTime());
        dto.setTriggerType(execution.getTriggerType());
        dto.setTriggerSource(execution.getTriggerSource());
        dto.setInputData(execution.getInputData());
        dto.setOutputData(execution.getOutputData());
        dto.setResult(execution.getResult());
        dto.setErrorMessage(execution.getErrorMessage());
        dto.setCurrentStepIndex(execution.getCurrentStepIndex());
        dto.setExecutorId(execution.getExecutorId());
        dto.setDuration(execution.getDuration());
        return dto;
    }
}
