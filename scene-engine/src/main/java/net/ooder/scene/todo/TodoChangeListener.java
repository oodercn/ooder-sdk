package net.ooder.scene.todo;

/**
 * 待办变更监听器
 * 
 * <p>用于监听待办的生命周期事件。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface TodoChangeListener {
    
    /**
     * 待办创建时调用
     * 
     * @param todo 新创建的待办
     */
    default void onTodoCreated(TodoDTO todo) {}
    
    /**
     * 待办状态变更时调用
     * 
     * @param todo 变更后的待办
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     */
    default void onTodoStatusChanged(TodoDTO todo, TodoStatus oldStatus, TodoStatus newStatus) {}
    
    /**
     * 待办删除时调用
     * 
     * @param todoId 被删除的待办ID
     */
    default void onTodoDeleted(String todoId) {}
    
    /**
     * 待办过期时调用
     * 
     * @param todo 过期的待办
     */
    default void onTodoExpired(TodoDTO todo) {}
}
