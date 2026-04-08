package net.ooder.scene.workflow;

import java.util.List;
import java.util.Map;

/**
 * 场景组工作流管理器接口
 *
 * <p>管理场景组工作流的定义、执行和监控。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface SceneWorkflowManager {

    // ========== 工作流定义管理 ==========

    /**
     * 创建工作流
     *
     * @param sceneGroupId 场景组ID
     * @param name 工作流名称
     * @param description 工作流描述
     * @return 创建的工作流
     */
    SceneWorkflow createWorkflow(String sceneGroupId, String name, String description);

    /**
     * 获取工作流
     *
     * @param workflowId 工作流ID
     * @return 工作流，不存在返回null
     */
    SceneWorkflow getWorkflow(String workflowId);

    /**
     * 获取场景组的所有工作流
     *
     * @param sceneGroupId 场景组ID
     * @return 工作流列表
     */
    List<SceneWorkflow> listWorkflows(String sceneGroupId);

    /**
     * 获取场景组的活跃工作流
     *
     * @param sceneGroupId 场景组ID
     * @return 活跃工作流列表
     */
    List<SceneWorkflow> listActiveWorkflows(String sceneGroupId);

    /**
     * 更新工作流
     *
     * @param workflow 工作流
     * @return 是否成功
     */
    boolean updateWorkflow(SceneWorkflow workflow);

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
     * 归档工作流
     *
     * @param workflowId 工作流ID
     * @return 是否成功
     */
    boolean archiveWorkflow(String workflowId);

    // ========== 工作流步骤管理 ==========

    /**
     * 添加工作流步骤
     *
     * @param workflowId 工作流ID
     * @param step 步骤
     * @return 是否成功
     */
    boolean addWorkflowStep(String workflowId, WorkflowStep step);

    /**
     * 更新工作流步骤
     *
     * @param workflowId 工作流ID
     * @param step 步骤
     * @return 是否成功
     */
    boolean updateWorkflowStep(String workflowId, WorkflowStep step);

    /**
     * 删除工作流步骤
     *
     * @param workflowId 工作流ID
     * @param stepId 步骤ID
     * @return 是否成功
     */
    boolean deleteWorkflowStep(String workflowId, String stepId);

    /**
     * 重新排序步骤
     *
     * @param workflowId 工作流ID
     * @param stepIds 步骤ID列表（按新顺序）
     * @return 是否成功
     */
    boolean reorderSteps(String workflowId, List<String> stepIds);

    // ========== 工作流执行 ==========

    /**
     * 执行工作流
     *
     * @param workflowId 工作流ID
     * @param inputData 输入数据
     * @return 执行记录
     */
    WorkflowExecution executeWorkflow(String workflowId, Map<String, Object> inputData);

    /**
     * 执行工作流（指定执行者）
     *
     * @param workflowId 工作流ID
     * @param inputData 输入数据
     * @param executorId 执行者ID
     * @return 执行记录
     */
    WorkflowExecution executeWorkflow(String workflowId, Map<String, Object> inputData, String executorId);

    /**
     * 获取执行记录
     *
     * @param executionId 执行ID
     * @return 执行记录
     */
    WorkflowExecution getExecution(String executionId);

    /**
     * 获取工作流的执行记录
     *
     * @param workflowId 工作流ID
     * @return 执行记录列表
     */
    List<WorkflowExecution> listExecutions(String workflowId);

    /**
     * 获取场景组的执行记录
     *
     * @param sceneGroupId 场景组ID
     * @return 执行记录列表
     */
    List<WorkflowExecution> listExecutionsBySceneGroup(String sceneGroupId);

    /**
     * 取消执行
     *
     * @param executionId 执行ID
     * @return 是否成功
     */
    boolean cancelExecution(String executionId);

    /**
     * 重试失败的工作流
     *
     * @param executionId 执行ID
     * @return 新的执行记录
     */
    WorkflowExecution retryExecution(String executionId);

    // ========== 触发器管理 ==========

    /**
     * 设置触发器
     *
     * @param workflowId 工作流ID
     * @param triggerType 触发类型
     * @param triggerConfig 触发配置（JSON格式）
     * @return 是否成功
     */
    boolean setTrigger(String workflowId, WorkflowTriggerType triggerType, String triggerConfig);

    /**
     * 启用/禁用触发器
     *
     * @param workflowId 工作流ID
     * @param enabled 是否启用
     * @return 是否成功
     */
    boolean setTriggerEnabled(String workflowId, boolean enabled);

    // ========== 查询统计 ==========

    /**
     * 获取工作流数量
     *
     * @param sceneGroupId 场景组ID
     * @return 工作流数量
     */
    int getWorkflowCount(String sceneGroupId);

    /**
     * 获取执行次数
     *
     * @param workflowId 工作流ID
     * @return 执行次数
     */
    int getExecutionCount(String workflowId);

    /**
     * 获取成功执行次数
     *
     * @param workflowId 工作流ID
     * @return 成功执行次数
     */
    int getSuccessExecutionCount(String workflowId);

    /**
     * 获取平均执行时长（毫秒）
     *
     * @param workflowId 工作流ID
     * @return 平均执行时长
     */
    long getAverageExecutionDuration(String workflowId);

    /**
     * 检查工作流是否存在
     *
     * @param workflowId 工作流ID
     * @return 是否存在
     */
    boolean exists(String workflowId);
}
