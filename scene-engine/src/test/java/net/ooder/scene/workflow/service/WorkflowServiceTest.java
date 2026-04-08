package net.ooder.scene.workflow.service;

import net.ooder.scene.workflow.*;
import net.ooder.scene.workflow.dto.WorkflowDTO;
import net.ooder.scene.workflow.dto.WorkflowStepDTO;
import net.ooder.scene.workflow.dto.WorkflowExecutionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private SqlSceneWorkflowManager workflowManager;

    private WorkflowServiceImpl workflowService;

    @BeforeEach
    void setUp() {
        workflowService = new WorkflowServiceImpl(workflowManager);
    }

    @Test
    void testCreateWorkflow() {
        WorkflowDTO dto = new WorkflowDTO();
        dto.setSceneGroupId("scene-001");
        dto.setName("Test Workflow");
        dto.setDescription("Test Description");

        SceneWorkflow workflow = createTestWorkflow("wf-001", "scene-001", "Test Workflow");
        when(workflowManager.createWorkflow("scene-001", "Test Workflow", "Test Description"))
            .thenReturn(workflow);

        WorkflowDTO result = workflowService.createWorkflow(dto);

        assertNotNull(result);
        assertEquals("wf-001", result.getWorkflowId());
        assertEquals("Test Workflow", result.getName());
        verify(workflowManager).createWorkflow("scene-001", "Test Workflow", "Test Description");
    }

    @Test
    void testCreateWorkflowWithVariables() {
        WorkflowDTO dto = new WorkflowDTO();
        dto.setSceneGroupId("scene-001");
        dto.setName("Test Workflow");
        dto.setDescription("Test Description");
        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", "value1");
        dto.setVariables(variables);

        SceneWorkflow workflow = createTestWorkflow("wf-001", "scene-001", "Test Workflow");
        when(workflowManager.createWorkflow("scene-001", "Test Workflow", "Test Description"))
            .thenReturn(workflow);
        when(workflowManager.updateWorkflow(any(SceneWorkflow.class))).thenReturn(true);

        WorkflowDTO result = workflowService.createWorkflow(dto);

        assertNotNull(result);
        verify(workflowManager).updateWorkflow(any(SceneWorkflow.class));
    }

    @Test
    void testGetWorkflow() {
        SceneWorkflow workflow = createTestWorkflow("wf-001", "scene-001", "Test Workflow");
        when(workflowManager.getWorkflow("wf-001")).thenReturn(workflow);

        WorkflowDTO result = workflowService.getWorkflow("wf-001");

        assertNotNull(result);
        assertEquals("wf-001", result.getWorkflowId());
        assertEquals("Test Workflow", result.getName());
    }

    @Test
    void testGetWorkflowNotFound() {
        when(workflowManager.getWorkflow("wf-999")).thenReturn(null);

        WorkflowDTO result = workflowService.getWorkflow("wf-999");

        assertNull(result);
    }

    @Test
    void testListWorkflows() {
        List<SceneWorkflow> workflows = Arrays.asList(
            createTestWorkflow("wf-001", "scene-001", "Workflow 1"),
            createTestWorkflow("wf-002", "scene-001", "Workflow 2")
        );
        when(workflowManager.listWorkflows("scene-001")).thenReturn(workflows);

        List<WorkflowDTO> result = workflowService.listWorkflows("scene-001");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("wf-001", result.get(0).getWorkflowId());
        assertEquals("wf-002", result.get(1).getWorkflowId());
    }

    @Test
    void testUpdateWorkflow() {
        SceneWorkflow existingWorkflow = createTestWorkflow("wf-001", "scene-001", "Old Name");
        when(workflowManager.getWorkflow("wf-001")).thenReturn(existingWorkflow);
        when(workflowManager.updateWorkflow(any(SceneWorkflow.class))).thenReturn(true);

        WorkflowDTO dto = new WorkflowDTO();
        dto.setName("New Name");
        dto.setDescription("New Description");

        WorkflowDTO result = workflowService.updateWorkflow("wf-001", dto);

        assertNotNull(result);
        verify(workflowManager).updateWorkflow(any(SceneWorkflow.class));
    }

    @Test
    void testUpdateWorkflowNotFound() {
        when(workflowManager.getWorkflow("wf-999")).thenReturn(null);

        WorkflowDTO dto = new WorkflowDTO();
        dto.setName("New Name");

        assertThrows(IllegalArgumentException.class, () -> {
            workflowService.updateWorkflow("wf-999", dto);
        });
    }

    @Test
    void testDeleteWorkflow() {
        when(workflowManager.deleteWorkflow("wf-001")).thenReturn(true);

        boolean result = workflowService.deleteWorkflow("wf-001");

        assertTrue(result);
        verify(workflowManager).deleteWorkflow("wf-001");
    }

    @Test
    void testActivateWorkflow() {
        when(workflowManager.activateWorkflow("wf-001")).thenReturn(true);

        boolean result = workflowService.activateWorkflow("wf-001");

        assertTrue(result);
        verify(workflowManager).activateWorkflow("wf-001");
    }

    @Test
    void testPauseWorkflow() {
        when(workflowManager.pauseWorkflow("wf-001")).thenReturn(true);

        boolean result = workflowService.pauseWorkflow("wf-001");

        assertTrue(result);
        verify(workflowManager).pauseWorkflow("wf-001");
    }

    @Test
    void testStartWorkflow() {
        WorkflowExecution execution = createTestExecution("exec-001", "wf-001");
        when(workflowManager.executeWorkflow(eq("wf-001"), anyMap())).thenReturn(execution);

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("key", "value");

        WorkflowExecutionDTO result = workflowService.startWorkflow("wf-001", inputData);

        assertNotNull(result);
        assertEquals("exec-001", result.getExecutionId());
        assertEquals("wf-001", result.getWorkflowId());
    }

    @Test
    void testGetWorkflowSteps() {
        SceneWorkflow workflow = createTestWorkflow("wf-001", "scene-001", "Test");
        WorkflowStep step1 = createTestStep("step-001", "Step 1", 1);
        WorkflowStep step2 = createTestStep("step-002", "Step 2", 2);
        workflow.setSteps(Arrays.asList(step1, step2));
        when(workflowManager.getWorkflow("wf-001")).thenReturn(workflow);

        List<WorkflowStepDTO> result = workflowService.getWorkflowSteps("wf-001");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("step-001", result.get(0).getStepId());
        assertEquals("step-002", result.get(1).getStepId());
    }

    @Test
    void testGetWorkflowStepsEmpty() {
        when(workflowManager.getWorkflow("wf-001")).thenReturn(null);

        List<WorkflowStepDTO> result = workflowService.getWorkflowSteps("wf-001");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddWorkflowStep() {
        WorkflowStepDTO dto = new WorkflowStepDTO();
        dto.setName("New Step");
        dto.setStepType("task");
        dto.setSequence(1);

        when(workflowManager.addWorkflowStep(eq("wf-001"), any(WorkflowStep.class))).thenReturn(true);

        boolean result = workflowService.addWorkflowStep("wf-001", dto);

        assertTrue(result);
        verify(workflowManager).addWorkflowStep(eq("wf-001"), any(WorkflowStep.class));
    }

    @Test
    void testAddWorkflowStepWithGeneratedId() {
        WorkflowStepDTO dto = new WorkflowStepDTO();
        dto.setName("New Step");
        dto.setStepType("task");
        dto.setSequence(1);

        when(workflowManager.addWorkflowStep(eq("wf-001"), any(WorkflowStep.class))).thenReturn(true);

        boolean result = workflowService.addWorkflowStep("wf-001", dto);

        assertTrue(result);
        verify(workflowManager).addWorkflowStep(eq("wf-001"), argThat(step -> 
            step.getStepId() != null && step.getStepId().startsWith("step-")
        ));
    }

    @Test
    void testUpdateWorkflowStep() {
        WorkflowStepDTO dto = new WorkflowStepDTO();
        dto.setName("Updated Step");
        dto.setStepType("task");
        dto.setSequence(1);

        when(workflowManager.updateWorkflowStep(eq("wf-001"), any(WorkflowStep.class))).thenReturn(true);

        boolean result = workflowService.updateWorkflowStep("wf-001", "step-001", dto);

        assertTrue(result);
        verify(workflowManager).updateWorkflowStep(eq("wf-001"), argThat(step -> 
            "step-001".equals(step.getStepId())
        ));
    }

    @Test
    void testDeleteWorkflowStep() {
        when(workflowManager.deleteWorkflowStep("wf-001", "step-001")).thenReturn(true);

        boolean result = workflowService.deleteWorkflowStep("wf-001", "step-001");

        assertTrue(result);
        verify(workflowManager).deleteWorkflowStep("wf-001", "step-001");
    }

    @Test
    void testReorderSteps() {
        List<String> stepIds = Arrays.asList("step-002", "step-001");
        when(workflowManager.reorderSteps("wf-001", stepIds)).thenReturn(true);

        boolean result = workflowService.reorderSteps("wf-001", stepIds);

        assertTrue(result);
        verify(workflowManager).reorderSteps("wf-001", stepIds);
    }

    @Test
    void testGetExecutionHistory() {
        List<WorkflowExecution> executions = Arrays.asList(
            createTestExecution("exec-001", "wf-001"),
            createTestExecution("exec-002", "wf-001")
        );
        when(workflowManager.listExecutions("wf-001")).thenReturn(executions);

        List<WorkflowExecutionDTO> result = workflowService.getExecutionHistory("wf-001");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSetTrigger() {
        when(workflowManager.setTrigger(eq("wf-001"), eq(WorkflowTriggerType.SCHEDULED), anyString()))
            .thenReturn(true);

        boolean result = workflowService.setTrigger("wf-001", "SCHEDULED", "{\"cron\":\"0 0 * * *\"}");

        assertTrue(result);
        verify(workflowManager).setTrigger(eq("wf-001"), eq(WorkflowTriggerType.SCHEDULED), anyString());
    }

    @Test
    void testEnableTrigger() {
        when(workflowManager.setTriggerEnabled("wf-001", true)).thenReturn(true);

        boolean result = workflowService.enableTrigger("wf-001");

        assertTrue(result);
        verify(workflowManager).setTriggerEnabled("wf-001", true);
    }

    @Test
    void testDisableTrigger() {
        when(workflowManager.setTriggerEnabled("wf-001", false)).thenReturn(true);

        boolean result = workflowService.disableTrigger("wf-001");

        assertTrue(result);
        verify(workflowManager).setTriggerEnabled("wf-001", false);
    }

    @Test
    void testGetExecution() {
        WorkflowExecution execution = createTestExecution("exec-001", "wf-001");
        when(workflowManager.getExecution("exec-001")).thenReturn(execution);

        WorkflowExecutionDTO result = workflowService.getExecution("exec-001");

        assertNotNull(result);
        assertEquals("exec-001", result.getExecutionId());
    }

    @Test
    void testGetExecutionNotFound() {
        when(workflowManager.getExecution("exec-999")).thenReturn(null);

        WorkflowExecutionDTO result = workflowService.getExecution("exec-999");

        assertNull(result);
    }

    @Test
    void testCancelExecution() {
        when(workflowManager.cancelExecution("exec-001")).thenReturn(true);

        boolean result = workflowService.cancelExecution("exec-001");

        assertTrue(result);
        verify(workflowManager).cancelExecution("exec-001");
    }

    @Test
    void testRetryExecution() {
        WorkflowExecution newExecution = createTestExecution("exec-002", "wf-001");
        when(workflowManager.retryExecution("exec-001")).thenReturn(newExecution);

        WorkflowExecutionDTO result = workflowService.retryExecution("exec-001");

        assertNotNull(result);
        assertEquals("exec-002", result.getExecutionId());
    }

    @Test
    void testGetExecutionStatus() {
        WorkflowExecution execution = createTestExecution("exec-001", "wf-001");
        execution.setStatus(WorkflowStatus.RUNNING);
        when(workflowManager.getExecution("exec-001")).thenReturn(execution);

        String status = workflowService.getExecutionStatus("exec-001");

        assertEquals("RUNNING", status);
    }

    @Test
    void testGetExecutionStatusNotFound() {
        when(workflowManager.getExecution("exec-999")).thenReturn(null);

        String status = workflowService.getExecutionStatus("exec-999");

        assertNull(status);
    }

    private SceneWorkflow createTestWorkflow(String workflowId, String sceneGroupId, String name) {
        SceneWorkflow workflow = new SceneWorkflow();
        workflow.setWorkflowId(workflowId);
        workflow.setSceneGroupId(sceneGroupId);
        workflow.setName(name);
        workflow.setDescription("Test Description");
        workflow.setStatus(WorkflowStatus.DRAFT);
        workflow.setCreateTime(LocalDateTime.now());
        workflow.setUpdateTime(LocalDateTime.now());
        workflow.setVersion(1);
        workflow.setAutoStart(false);
        workflow.setTriggerEnabled(false);
        workflow.setSteps(new ArrayList<>());
        workflow.setVariables(new HashMap<>());
        return workflow;
    }

    private WorkflowStep createTestStep(String stepId, String name, int sequence) {
        WorkflowStep step = new WorkflowStep();
        step.setStepId(stepId);
        step.setName(name);
        step.setSequence(sequence);
        step.setStepType("task");
        step.setStatus(WorkflowStepStatus.PENDING);
        step.setConfig(new HashMap<>());
        return step;
    }

    private WorkflowExecution createTestExecution(String executionId, String workflowId) {
        WorkflowExecution execution = new WorkflowExecution();
        execution.setExecutionId(executionId);
        execution.setWorkflowId(workflowId);
        execution.setSceneGroupId("scene-001");
        execution.setStatus(WorkflowStatus.RUNNING);
        execution.setStartTime(LocalDateTime.now());
        execution.setTriggerType("MANUAL");
        execution.setInputData(new HashMap<>());
        execution.setOutputData(new HashMap<>());
        execution.setCurrentStepIndex(0);
        return execution;
    }
}
