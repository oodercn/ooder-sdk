package net.ooder.scene.workflow.service;

import net.ooder.scene.workflow.dto.WorkflowDTO;
import net.ooder.scene.workflow.dto.WorkflowStepDTO;
import net.ooder.scene.workflow.dto.WorkflowExecutionDTO;

import java.util.List;
import java.util.Map;

/**
 * 工作流服务接口
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface WorkflowService {

    /**
     * 创建工作流
     *
     * @param dto 工作流DTO
     * @return 创建的工作流
     */
    WorkflowDTO createWorkflow(WorkflowDTO dto);

    /**
     * 获取工作流
     *
     * @param workflowId 工作流ID
     * @return 工作流DTO
     */
    WorkflowDTO getWorkflow(String workflowId);

    /**
     * 获取场景组的工作流列表
     *
     * @param sceneGroupId 场景组ID
     * @return 工作流列表
     */
    List<WorkflowDTO> listWorkflows(String sceneGroupId);

    /**
     * 更新工作流
     *
     * @param workflowId 工作流ID
     * @param dto 工作流DTO
     * @return 更新后的工作流
     */
    WorkflowDTO updateWorkflow(String workflowId, WorkflowDTO dto);

    /**
     * 删除工作流
     *
     * @param workflowId 工作流ID
     * @return 是否成功
     */
    boolean deleteWorkflow(String workflowId);

    /**
     * 激活工作流
     *
     * @param workflowId 工作流ID
     * @return 是否成功
     */
    boolean activateWorkflow(String workflowId);

    /**
     * 暂停工作流
     *
     * @param workflowId 工作流ID
     * @return 是否成功
     */
    boolean pauseWorkflow(String workflowId);

    /**
     * 启动工作流执行
     *
     * @param workflowId 工作流ID
     * @param inputData 输入数据
     * @return 执行记录
     */
    WorkflowExecutionDTO startWorkflow(String workflowId, Map<String, Object> inputData);

    /**
     * 获取工作流步骤列表
     *
     * @param workflowId 工作流ID
     * @return 步骤列表
     */
    List<WorkflowStepDTO> getWorkflowSteps(String workflowId);

    /**
     * 添加工作流步骤
     *
     * @param workflowId 工作流ID
     * @param dto 步骤DTO
     * @return 是否成功
     */
    boolean addWorkflowStep(String workflowId, WorkflowStepDTO dto);

    /**
     * 更新工作流步骤
     *
     * @param workflowId 工作流ID
     * @param stepId 步骤ID
     * @param dto 步骤DTO
     * @return 是否成功
     */
    boolean updateWorkflowStep(String workflowId, String stepId, WorkflowStepDTO dto);

    /**
     * 删除工作流步骤
     *
     * @param workflowId 工作流ID
     * @param stepId 步骤ID
     * @return 是否成功
     */
    boolean deleteWorkflowStep(String workflowId, String stepId);

    /**
     * 重排序步骤
     *
     * @param workflowId 工作流ID
     * @param stepIds 步骤ID列表（按新顺序）
     * @return 是否成功
     */
    boolean reorderSteps(String workflowId, List<String> stepIds);

    /**
     * 获取执行历史
     *
     * @param workflowId 工作流ID
     * @return 执行记录列表
     */
    List<WorkflowExecutionDTO> getExecutionHistory(String workflowId);

    /**
     * 设置触发器
     *
     * @param workflowId 工作流ID
     * @param triggerType 触发类型
     * @param triggerConfig 触发配置
     * @return 是否成功
     */
    boolean setTrigger(String workflowId, String triggerType, String triggerConfig);

    /**
     * 启用触发器
     *
     * @param workflowId 工作流ID
     * @return 是否成功
     */
    boolean enableTrigger(String workflowId);

    /**
     * 禁用触发器
     *
     * @param workflowId 工作流ID
     * @return 是否成功
     */
    boolean disableTrigger(String workflowId);

    /**
     * 获取执行详情
     *
     * @param executionId 执行ID
     * @return 执行详情
     */
    WorkflowExecutionDTO getExecution(String executionId);

    /**
     * 取消执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    boolean cancelExecution(String executionId);

    /**
     * 重试执行
     *
     * @param executionId 执行ID
     * @return 新的执行记录
     */
    WorkflowExecutionDTO retryExecution(String executionId);

    /**
     * 获取执行状态
     *
     * @param executionId 执行ID
     * @return 执行状态
     */
    String getExecutionStatus(String executionId);
}
