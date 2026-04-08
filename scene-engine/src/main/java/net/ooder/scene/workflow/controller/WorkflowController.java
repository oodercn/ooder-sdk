package net.ooder.scene.workflow.controller;

import jakarta.validation.Valid;
import net.ooder.scene.core.Result;
import net.ooder.scene.workflow.dto.WorkflowDTO;
import net.ooder.scene.workflow.dto.WorkflowStepDTO;
import net.ooder.scene.workflow.dto.WorkflowExecutionDTO;
import net.ooder.scene.workflow.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工作流管理控制器
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@RestController
@RequestMapping("/api/v1/workflows")
@CrossOrigin
public class WorkflowController {

    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);

    private final WorkflowService workflowService;

    @Autowired
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public Result<WorkflowDTO> createWorkflow(@Valid @RequestBody WorkflowDTO dto) {
        log.info("API: Create workflow - sceneGroupId={}, name={}", dto.getSceneGroupId(), dto.getName());
        try {
            WorkflowDTO result = workflowService.createWorkflow(dto);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to create workflow", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<List<WorkflowDTO>> listWorkflows(@RequestParam String sceneGroupId) {
        log.info("API: List workflows - sceneGroupId={}", sceneGroupId);
        try {
            List<WorkflowDTO> result = workflowService.listWorkflows(sceneGroupId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to list workflows", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<WorkflowDTO> getWorkflow(@PathVariable("id") String workflowId) {
        log.info("API: Get workflow - workflowId={}", workflowId);
        try {
            WorkflowDTO result = workflowService.getWorkflow(workflowId);
            if (result == null) {
                return Result.notFound("工作流不存在");
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get workflow", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<WorkflowDTO> updateWorkflow(
            @PathVariable("id") String workflowId,
            @Valid @RequestBody WorkflowDTO dto) {
        log.info("API: Update workflow - workflowId={}", workflowId);
        try {
            WorkflowDTO result = workflowService.updateWorkflow(workflowId, dto);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to update workflow", e);
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteWorkflow(@PathVariable("id") String workflowId) {
        log.info("API: Delete workflow - workflowId={}", workflowId);
        try {
            boolean result = workflowService.deleteWorkflow(workflowId);
            return Result.success(result);
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage(), 400);
        } catch (Exception e) {
            log.error("Failed to delete workflow", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/start")
    public Result<WorkflowExecutionDTO> startWorkflow(
            @PathVariable("id") String workflowId,
            @RequestBody(required = false) Map<String, Object> inputData) {
        log.info("API: Start workflow - workflowId={}", workflowId);
        try {
            WorkflowExecutionDTO result = workflowService.startWorkflow(workflowId, inputData);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.notFound(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage(), 400);
        } catch (Exception e) {
            log.error("Failed to start workflow", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/steps")
    public Result<List<WorkflowStepDTO>> getWorkflowSteps(@PathVariable("id") String workflowId) {
        log.info("API: Get workflow steps - workflowId={}", workflowId);
        try {
            List<WorkflowStepDTO> result = workflowService.getWorkflowSteps(workflowId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get workflow steps", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/steps")
    public Result<Boolean> addWorkflowStep(
            @PathVariable("id") String workflowId,
            @Valid @RequestBody WorkflowStepDTO dto) {
        log.info("API: Add workflow step - workflowId={}, stepName={}", workflowId, dto.getName());
        try {
            boolean result = workflowService.addWorkflowStep(workflowId, dto);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to add workflow step", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/steps/{stepId}")
    public Result<Boolean> updateWorkflowStep(
            @PathVariable("id") String workflowId,
            @PathVariable("stepId") String stepId,
            @Valid @RequestBody WorkflowStepDTO dto) {
        log.info("API: Update workflow step - workflowId={}, stepId={}", workflowId, stepId);
        try {
            boolean result = workflowService.updateWorkflowStep(workflowId, stepId, dto);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to update workflow step", e);
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/steps/{stepId}")
    public Result<Boolean> deleteWorkflowStep(
            @PathVariable("id") String workflowId,
            @PathVariable("stepId") String stepId) {
        log.info("API: Delete workflow step - workflowId={}, stepId={}", workflowId, stepId);
        try {
            boolean result = workflowService.deleteWorkflowStep(workflowId, stepId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to delete workflow step", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/steps/reorder")
    public Result<Boolean> reorderSteps(
            @PathVariable("id") String workflowId,
            @RequestBody List<String> stepIds) {
        log.info("API: Reorder steps - workflowId={}, stepCount={}", workflowId, stepIds.size());
        try {
            boolean result = workflowService.reorderSteps(workflowId, stepIds);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to reorder steps", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/executions")
    public Result<List<WorkflowExecutionDTO>> getExecutionHistory(@PathVariable("id") String workflowId) {
        log.info("API: Get execution history - workflowId={}", workflowId);
        try {
            List<WorkflowExecutionDTO> result = workflowService.getExecutionHistory(workflowId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get execution history", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/trigger")
    public Result<Boolean> setTrigger(
            @PathVariable("id") String workflowId,
            @RequestBody Map<String, String> triggerConfig) {
        log.info("API: Set trigger - workflowId={}", workflowId);
        try {
            String triggerType = triggerConfig.get("triggerType");
            String config = triggerConfig.get("triggerConfig");
            boolean result = workflowService.setTrigger(workflowId, triggerType, config);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to set trigger", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/trigger/enable")
    public Result<Boolean> enableTrigger(@PathVariable("id") String workflowId) {
        log.info("API: Enable trigger - workflowId={}", workflowId);
        try {
            boolean result = workflowService.enableTrigger(workflowId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to enable trigger", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/trigger/disable")
    public Result<Boolean> disableTrigger(@PathVariable("id") String workflowId) {
        log.info("API: Disable trigger - workflowId={}", workflowId);
        try {
            boolean result = workflowService.disableTrigger(workflowId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to disable trigger", e);
            return Result.error(e.getMessage());
        }
    }
}
