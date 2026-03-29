package net.ooder.scene.todo.push;

import net.ooder.scene.todo.TodoDTO;

/**
 * Todo 推送服务接口
 *
 * <p>提供待办变更的实时推送能力，支持多种推送渠道：</p>
 * <ul>
 *   <li>WebSocket - 实时推送给在线用户</li>
 *   <li>站内信 - 系统通知</li>
 * </ul>
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface TodoPushService {

    /**
     * 推送给指定用户
     *
     * @param userId 用户ID
     * @param todo 待办数据
     * @param action 操作类型 (created/updated/deleted)
     */
    void pushToUser(String userId, TodoDTO todo, String action);

    /**
     * 推送给场景组参与者
     *
     * @param sceneGroupId 场景组ID
     * @param todo 待办数据
     * @param action 操作类型
     */
    void pushToSceneGroup(String sceneGroupId, TodoDTO todo, String action);

    /**
     * 推送待办创建通知
     *
     * @param todo 新创建的待办
     */
    default void pushTodoCreated(TodoDTO todo) {
        pushToUser(todo.getToUserId(), todo, "created");
    }

    /**
     * 推送待办状态变更通知
     *
     * @param todo 变更后的待办
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     */
    default void pushTodoStatusChanged(TodoDTO todo, String oldStatus, String newStatus) {
        pushToUser(todo.getToUserId(), todo, "status_changed");
    }

    /**
     * 推送待办删除通知
     *
     * @param todoId 被删除的待办ID
     * @param userId 相关用户ID
     */
    default void pushTodoDeleted(String todoId, String userId) {
        TodoDTO todo = new TodoDTO();
        todo.setId(todoId);
        todo.setToUserId(userId);
        pushToUser(userId, todo, "deleted");
    }

    /**
     * 推送待办过期通知
     *
     * @param todo 过期的待办
     */
    default void pushTodoExpired(TodoDTO todo) {
        pushToUser(todo.getToUserId(), todo, "expired");
    }
}
