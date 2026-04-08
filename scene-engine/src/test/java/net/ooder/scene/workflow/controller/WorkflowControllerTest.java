package net.ooder.scene.workflow.controller;

import net.ooder.scene.core.Result;
import net.ooder.scene.workflow.WorkflowStatus;
import net.ooder.scene.workflow.WorkflowTriggerType;
import net.ooder.scene.workflow.dto.WorkflowDTO;
import net.ooder.scene.workflow.dto.WorkflowStepDTO;
import net.ooder.scene.workflow.dto.WorkflowExecutionDTO;
import net.ooder.scene.workflow.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowControllerTest {

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private WorkflowController workflowController;

    private WorkflowDTO testWorkflowDTO;

    @BeforeEach
    void setUp() {
        testWorkflowDTO = createTestWorkflowDTO("wf-001", "scene-001", "Test Workflow");
    }

    @Test
    void testCreateWorkflow() {
        when(workflowService.createWorkflow(any(WorkflowDTO.class))).thenReturn(testWorkflowDTO);

        Result<WorkflowDTO> result = workflowController.createWorkflow(testWorkflowDTO);

        assertTrue(result.isSuccess());
        assertEquals("wf-001", result.getData().getWorkflowId());
        verify(workflowService).createWorkflow(any(WorkflowDTO.class));
    }

    @Test
    void testCreateWorkflowError() {
        when(workflowService.createWorkflow(any(WorkflowDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        Result<WorkflowDTO> result = workflowController.createWorkflow(testWorkflowDTO);

        assertFalse(result.isSuccess());
        assertEquals(500, result.getCode());
    }

    @Test
    void testListWorkflows() {
        List<WorkflowDTO> workflows = Arrays.asList(
            createTestWorkflowDTO("wf-001", "scene-001", "Workflow 1"),
            createTestWorkflowDTO("wf-002", "scene-001", "Workflow 2")
        );
        when(workflowService.listWorkflows("scene-001")).thenReturn(workflows);

        Result<List<WorkflowDTO>> result = workflowController.listWorkflows("scene-001");

        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    void testGetWorkflow() {
        when(workflowService.getWorkflow("wf-001")).thenReturn(testWorkflowDTO);

        Result<WorkflowDTO> result = workflowController.getWorkflow("wf-001");

        assertTrue(result.isSuccess());
        assertEquals("wf-001", result.getData().getWorkflowId());
    }

    @Test
    void testGetWorkflowNotFound() {
        when(workflowService.getWorkflow("wf-999")).thenReturn(null);

        Result<WorkflowDTO> result = workflowController.getWorkflow("wf-999");

        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    void testUpdateWorkflow() {
        when(workflowService.updateWorkflow(eq("wf-001"), any(WorkflowDTO.class))).thenReturn(testWorkflowDTO);

        Result<WorkflowDTO> result = workflowController.updateWorkflow("wf-001", testWorkflowDTO);

        assertTrue(result.isSuccess());
        verify(workflowService).updateWorkflow(eq("wf-001"), any(WorkflowDTO.class));
    }

    @Test
    void testUpdateWorkflowNotFound() {
        when(workflowService.updateWorkflow(eq("wf-999"), any(WorkflowDTO.class)))
            .thenThrow(new IllegalArgumentException("Workflow not found"));

        Result<WorkflowDTO> result = workflowController.updateWorkflow("wf-999", testWorkflowDTO);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    void testDeleteWorkflow() {
        when(workflowService.deleteWorkflow("wf-001")).thenReturn(true);

        Result<Boolean> result = workflowController.deleteWorkflow("wf-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    void testDeleteWorkflowWithRunningExecutions() {
        when(workflowService.deleteWorkflow("wf-001"))
            .thenThrow(new IllegalStateException("Cannot delete workflow with running executions"));

        Result<Boolean> result = workflowController.deleteWorkflow("wf-001");

        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    void testStartWorkflow() {
        WorkflowExecutionDTO execution = createTestExecutionDTO("exec-001", "wf-001");
        when(workflowService.startWorkflow(eq("wf-001"), anyMap())).thenReturn(execution);

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("key", "value");

        Result<WorkflowExecutionDTO> result = workflowController.startWorkflow("wf-001", inputData);

        assertTrue(result.isSuccess());
        assertEquals("exec-001", result.getData().getExecutionId());
    }

    @Test
    void testStartWorkflowNotFound() {
        when(workflowService.startWorkflow(eq("wf-999"), anyMap()))
            .thenThrow(new IllegalArgumentException("Workflow not found"));

        Result<WorkflowExecutionDTO> result = workflowController.startWorkflow("wf-999", new HashMap<>());

        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    void testGetWorkflowSteps() {
        List<WorkflowStepDTO> steps = Arrays.asList(
            createTestStepDTO("step-001", "Step 1", 1),
            createTestStepDTO("step-002", "Step 2", 2)
        );
        when(workflowService.getWorkflowSteps("wf-001")).thenReturn(steps);

        Result<List<WorkflowStepDTO>> result = workflowController.getWorkflowSteps("wf-001");

        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    void testAddWorkflowStep() {
        WorkflowStepDTO stepDTO = createTestStepDTO("step-001", "New Step", 1);
        when(workflowService.addWorkflowStep(eq("wf-001"), any(WorkflowStepDTO.class))).thenReturn(true);

        Result<Boolean> result = workflowController.addWorkflowStep("wf-001", stepDTO);

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    void testUpdateWorkflowStep() {
        WorkflowStepDTO stepDTO = createTestStepDTO("step-001", "Updated Step", 1);
        when(workflowService.updateWorkflowStep(eq("wf-001"), eq("step-001"), any(WorkflowStepDTO.class))).thenReturn(true);

        Result<Boolean> result = workflowController.updateWorkflowStep("wf-001", "step-001", stepDTO);

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    void testDeleteWorkflowStep() {
        when(workflowService.deleteWorkflowStep("wf-001", "step-001")).thenReturn(true);

        Result<Boolean> result = workflowController.deleteWorkflowStep("wf-001", "step-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    void testReorderSteps() {
        List<String> stepIds = Arrays.asList("step-002", "step-001");
        when(workflowService.reorderSteps("wf-001", stepIds)).thenReturn(true);

        Result<Boolean> result = workflowController.reorderSteps("wf-001", stepIds);

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    void testGetExecutionHistory() {
        List<WorkflowExecutionDTO> executions = Arrays.asList(
            createTestExecutionDTO("exec-001", "wf-001"),
            createTestExecutionDTO("exec-002", "wf-001")
        );
        when(workflowService.getExecutionHistory("wf-001")).thenReturn(executions);

        Result<List<WorkflowExecutionDTO>> result = workflowController.getExecutionHistory("wf-001");

        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    void testSetTrigger() {
        when(workflowService.setTrigger(eq("wf-001"), eq("SCHEDULED"), anyString())).thenReturn(true);

        Map<String, String> triggerConfig = new HashMap<>();
        triggerConfig.put("triggerType", "SCHEDULED");
        triggerConfig.put("triggerConfig", "{\"cron\":\"0 0 * * *\"}");

        Result<Boolean> result = workflowController.setTrigger("wf-001", triggerConfig);

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    void testEnableTrigger() {
        when(workflowService.enableTrigger("wf-001")).thenReturn(true);

        Result<Boolean> result = workflowController.enableTrigger("wf-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    void testDisableTrigger() {
        when(workflowService.disableTrigger("wf-001")).thenReturn(true);

        Result<Boolean> result = workflowController.disableTrigger("wf-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    private WorkflowDTO createTestWorkflowDTO(String workflowId, String sceneGroupId, String name) {
        WorkflowDTO dto = new WorkflowDTO();
        dto.setWorkflowId(workflowId);
        dto.setSceneGroupId(sceneGroupId);
        dto.setName(name);
        dto.setDescription("Test Description");
        dto.setStatus(WorkflowStatus.DRAFT);
        dto.setCreateTime(LocalDateTime.now());
        dto.setUpdateTime(LocalDateTime.now());
        dto.setVersion(1);
        dto.setAutoStart(false);
        dto.setTriggerEnabled(false);
        return dto;
    }

    private WorkflowStepDTO createTestStepDTO(String stepId, String name, int sequence) {
        WorkflowStepDTO dto = new WorkflowStepDTO();
        dto.setStepId(stepId);
        dto.setName(name);
        dto.setSequence(sequence);
        dto.setStepType("task");
        return dto;
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
