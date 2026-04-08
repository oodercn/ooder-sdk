package net.ooder.scene.workflow.controller;

import net.ooder.scene.core.Result;
import net.ooder.scene.workflow.WorkflowStatus;
import net.ooder.scene.workflow.dto.WorkflowExecutionDTO;
import net.ooder.scene.workflow.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowExecutionControllerTest {

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private WorkflowExecutionController executionController;

    private WorkflowExecutionDTO testExecutionDTO;

    @BeforeEach
    void setUp() {
        testExecutionDTO = createTestExecutionDTO("exec-001", "wf-001");
    }

    @Test
    void testGetExecution() {
        when(workflowService.getExecution("exec-001")).thenReturn(testExecutionDTO);

        Result<WorkflowExecutionDTO> result = executionController.getExecution("exec-001");

        assertTrue(result.isSuccess());
        assertEquals("exec-001", result.getData().getExecutionId());
    }

    @Test
    void testGetExecutionNotFound() {
        when(workflowService.getExecution("exec-999")).thenReturn(null);

        Result<WorkflowExecutionDTO> result = executionController.getExecution("exec-999");

        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    void testCancelExecution() {
        when(workflowService.cancelExecution("exec-001")).thenReturn(true);

        Result<Boolean> result = executionController.cancelExecution("exec-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    void testRetryExecution() {
        WorkflowExecutionDTO newExecution = createTestExecutionDTO("exec-002", "wf-001");
        when(workflowService.retryExecution("exec-001")).thenReturn(newExecution);

        Result<WorkflowExecutionDTO> result = executionController.retryExecution("exec-001");

        assertTrue(result.isSuccess());
        assertEquals("exec-002", result.getData().getExecutionId());
    }

    @Test
    void testRetryExecutionNotFound() {
        when(workflowService.retryExecution("exec-999"))
            .thenThrow(new IllegalArgumentException("Execution not found"));

        Result<WorkflowExecutionDTO> result = executionController.retryExecution("exec-999");

        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    void testRetryExecutionInvalidState() {
        when(workflowService.retryExecution("exec-001"))
            .thenThrow(new IllegalStateException("Only failed or cancelled executions can be retried"));

        Result<WorkflowExecutionDTO> result = executionController.retryExecution("exec-001");

        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    void testGetExecutionStatus() {
        when(workflowService.getExecutionStatus("exec-001")).thenReturn("RUNNING");

        Result<String> result = executionController.getExecutionStatus("exec-001");

        assertTrue(result.isSuccess());
        assertEquals("RUNNING", result.getData());
    }

    @Test
    void testGetExecutionStatusNotFound() {
        when(workflowService.getExecutionStatus("exec-999")).thenReturn(null);

        Result<String> result = executionController.getExecutionStatus("exec-999");

        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    private WorkflowExecutionDTO createTestExecutionDTO(String executionId, String workflowId) {
        WorkflowExecutionDTO dto = new WorkflowExecutionDTO();
        dto.setExecutionId(executionId);
        dto.setWorkflowId(workflowId);
        dto.setSceneGroupId("scene-001");
        dto.setStatus(WorkflowStatus.RUNNING);
        dto.setStartTime(LocalDateTime.now());
        dto.setTriggerType("MANUAL");
        dto.setCurrentStepIndex(0);
        return dto;
    }
}
