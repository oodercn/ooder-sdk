package net.ooder.scene.todo;

import net.ooder.scene.core.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 待办服务接口
 * 
 * <p>SE SDK 提供的待办管理服务，支持多种待办类型的创建、查询和操作。</p>
 * 
 * <h3>支持的待办类型：</h3>
 * <ul>
 *   <li>INVITATION - 协作邀请</li>
 *   <li>DELEGATION - 领导委派</li>
 *   <li>REMINDER - 待办提醒</li>
 *   <li>APPROVAL - 审批请求</li>
 *   <li>ACTIVATION - 待激活能力</li>
 *   <li>SCENE_NOTIFICATION - 场景通知</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 创建邀请待办
 * InvitationTodoRequest request = new InvitationTodoRequest();
 * request.setSceneGroupId("sg-001");
 * request.setToUserId("user-001");
 * request.setRole("EMPLOYEE");
 * TodoDTO todo = todoService.createInvitationTodo(request);
 * 
 * // 接受待办
 * todoService.acceptTodo("user-001", todo.getId());
 * </pre>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface TodoService {
    
    // ========== 待办创建 ==========
    
    /**
     * 创建邀请待办
     * 
     * @param request 邀请待办请求
     * @return 创建的待办
     */
    TodoDTO createInvitationTodo(InvitationTodoRequest request);
    
    /**
     * 创建委派待办
     * 
     * @param request 委派待办请求
     * @return 创建的待办
     */
    TodoDTO createDelegationTodo(DelegationTodoRequest request);
    
    /**
     * 创建审批待办
     * 
     * @param request 审批待办请求
     * @return 创建的待办
     */
    TodoDTO createApprovalTodo(ApprovalTodoRequest request);
    
    /**
     * 创建提醒待办
     * 
     * @param request 提醒待办请求
     * @return 创建的待办
     */
    TodoDTO createReminderTodo(ReminderTodoRequest request);
    
    /**
     * 创建激活待办
     * 
     * @param request 激活待办请求
     * @return 创建的待办
     */
    TodoDTO createActivationTodo(ActivationTodoRequest request);
    
    /**
     * 创建场景通知待办
     * 
     * @param request 场景通知请求
     * @return 创建的待办
     */
    TodoDTO createSceneNotificationTodo(SceneNotificationRequest request);
    
    /**
     * 创建通用待办
     * 
     * @param todo 待办数据
     * @return 创建的待办
     */
    TodoDTO createTodo(TodoDTO todo);
    
    // ========== 待办查询 ==========
    
    /**
     * 获取用户的待办列表
     * 
     * @param userId 用户ID
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<TodoDTO> listUserTodos(String userId, TodoQuery query);
    
    /**
     * 获取待办详情
     * 
     * @param todoId 待办ID
     * @return 待办详情
     */
    TodoDTO getTodo(String todoId);
    
    /**
     * 按类型统计待办
     * 
     * @param userId 用户ID
     * @return 类型统计映射
     */
    Map<String, Integer> countByType(String userId);
    
    /**
     * 获取场景组的待办列表
     * 
     * @param sceneGroupId 场景组ID
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<TodoDTO> listSceneGroupTodos(String sceneGroupId, TodoQuery query);
    
    /**
     * 获取待处理待办数量
     * 
     * @param userId 用户ID
     * @return 待处理数量
     */
    int getPendingCount(String userId);
    
    // ========== 待办操作 ==========
    
    /**
     * 接受待办
     * 
     * @param userId 用户ID
     * @param todoId 待办ID
     * @return 是否成功
     */
    boolean acceptTodo(String userId, String todoId);
    
    /**
     * 拒绝待办
     * 
     * @param userId 用户ID
     * @param todoId 待办ID
     * @return 是否成功
     */
    boolean rejectTodo(String userId, String todoId);
    
    /**
     * 完成待办
     * 
     * @param userId 用户ID
     * @param todoId 待办ID
     * @return 是否成功
     */
    boolean completeTodo(String userId, String todoId);
    
    /**
     * 审批待办
     * 
     * @param userId 用户ID
     * @param todoId 待办ID
     * @param approved 是否批准
     * @param comment 审批意见
     * @return 是否成功
     */
    boolean approveTodo(String userId, String todoId, boolean approved, String comment);
    
    /**
     * 取消待办
     * 
     * @param todoId 待办ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelTodo(String todoId, String reason);
    
    /**
     * 删除待办
     * 
     * @param todoId 待办ID
     * @return 是否成功
     */
    boolean deleteTodo(String todoId);
    
    // ========== 待办订阅 ==========
    
    /**
     * 订阅待办变更
     * 
     * @param userId 用户ID
     * @param listener 监听器
     */
    void subscribe(String userId, TodoChangeListener listener);
    
    /**
     * 取消订阅
     * 
     * @param userId 用户ID
     * @param listener 监听器
     */
    void unsubscribe(String userId, TodoChangeListener listener);
    
    // ========== 过期处理 ==========
    
    /**
     * 检查并处理过期待办
     * 
     * @return 处理的过期待办数量
     */
    int processExpiredTodos();
    
    // ========== 批量操作 ==========
    
    /**
     * 批量创建待办
     * 
     * @param todos 待办列表
     * @return 创建成功的待办列表
     */
    default List<TodoDTO> batchCreateTodos(List<TodoDTO> todos) {
        return todos.stream()
                .map(this::createTodo)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 批量完成待办
     * 
     * @param userId 用户ID
     * @param todoIds 待办ID列表
     * @return 成功数量
     */
    default int batchCompleteTodos(String userId, List<String> todoIds) {
        int count = 0;
        for (String todoId : todoIds) {
            if (completeTodo(userId, todoId)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 批量取消待办
     * 
     * @param todoIds 待办ID列表
     * @param reason 取消原因
     * @return 成功数量
     */
    default int batchCancelTodos(List<String> todoIds, String reason) {
        int count = 0;
        for (String todoId : todoIds) {
            if (cancelTodo(todoId, reason)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 批量删除待办
     * 
     * @param todoIds 待办ID列表
     * @return 成功数量
     */
    default int batchDeleteTodos(List<String> todoIds) {
        int count = 0;
        for (String todoId : todoIds) {
            if (deleteTodo(todoId)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 批量获取待办
     * 
     * @param todoIds 待办ID列表
     * @return 待办列表（不存在的ID会被忽略）
     */
    default List<TodoDTO> batchGetTodos(List<String> todoIds) {
        return todoIds.stream()
                .map(this::getTodo)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }
}
