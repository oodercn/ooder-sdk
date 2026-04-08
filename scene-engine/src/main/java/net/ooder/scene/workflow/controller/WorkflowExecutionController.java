package net.ooder.scene.workflow.controller;

import net.ooder.scene.core.Result;
import net.ooder.scene.workflow.dto.WorkflowExecutionDTO;
import net.ooder.scene.workflow.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工作流执行控制器
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@RestController
@RequestMapping("/api/v1/executions")
@CrossOrigin
public class WorkflowExecutionController {

    private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionController.class);

    private final WorkflowService workflowService;

    @Autowired
    public WorkflowExecutionController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/{executionId}")
    public Result<WorkflowExecutionDTO> getExecution(@PathVariable String executionId) {
        log.info("API: Get execution - executionId={}", executionId);
        try {
            WorkflowExecutionDTO result = workflowService.getExecution(executionId);
            if (result == null) {
                return Result.notFound("执行记录不存在");
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get execution", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{executionId}/cancel")
    public Result<Boolean> cancelExecution(@PathVariable String executionId) {
        log.info("API: Cancel execution - executionId={}", executionId);
        try {
            boolean result = workflowService.cancelExecution(executionId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to cancel execution", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{executionId}/retry")
    public Result<WorkflowExecutionDTO> retryExecution(@PathVariable String executionId) {
        log.info("API: Retry execution - executionId={}", executionId);
        try {
            WorkflowExecutionDTO result = workflowService.retryExecution(executionId);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.notFound(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage(), 400);
        } catch (Exception e) {
            log.error("Failed to retry execution", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{executionId}/status")
    public Result<String> getExecutionStatus(@PathVariable String executionId) {
        log.info("API: Get execution status - executionId={}", executionId);
        try {
            String status = workflowService.getExecutionStatus(executionId);
            if (status == null) {
                return Result.notFound("执行记录不存在");
            }
            return Result.success(status);
        } catch (Exception e) {
            log.error("Failed to get execution status", e);
            return Result.error(e.getMessage());
        }
    }
}
