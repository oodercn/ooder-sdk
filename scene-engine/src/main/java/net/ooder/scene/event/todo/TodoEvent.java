package net.ooder.scene.event.todo;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;
import net.ooder.scene.todo.TodoStatus;
import net.ooder.scene.todo.TodoType;

import java.util.HashMap;
import java.util.Map;

/**
 * 待办事件
 * 
 * <p>待办状态变更时发布的事件。</p>
 * 
 * <h3>事件类型：</h3>
 * <ul>
 *   <li>TODO_CREATED - 待办创建</li>
 *   <li>TODO_ACCEPTED - 待办接受</li>
 *   <li>TODO_REJECTED - 待办拒绝</li>
 *   <li>TODO_COMPLETED - 待办完成</li>
 *   <li>TODO_APPROVED - 待办审批</li>
 *   <li>TODO_EXPIRED - 待办过期</li>
 *   <li>TODO_CANCELLED - 待办取消</li>
 *   <li>TODO_DELETED - 待办删除</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class TodoEvent extends SceneEvent {
    
    private String todoId;
    private TodoType todoType;
    private TodoStatus oldStatus;
    private TodoStatus newStatus;
    private String sceneGroupId;
    private String fromUserId;
    private String toUserId;
    private Map<String, Object> payload;
    
    public TodoEvent(Object source, SceneEventType eventType) {
        super(source, eventType);
    }
    
    public String getTodoId() {
        return todoId;
    }
    
    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }
    
    public TodoType getTodoType() {
        return todoType;
    }
    
    public void setTodoType(TodoType todoType) {
        this.todoType = todoType;
    }
    
    public TodoStatus getOldStatus() {
        return oldStatus;
    }
    
    public void setOldStatus(TodoStatus oldStatus) {
        this.oldStatus = oldStatus;
    }
    
    public TodoStatus getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(TodoStatus newStatus) {
        this.newStatus = newStatus;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    public String getToUserId() {
        return toUserId;
    }
    
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
    
    public Map<String, Object> getPayload() {
        return payload;
    }
    
    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
    
    public TodoEvent withTodoId(String todoId) {
        this.todoId = todoId;
        return this;
    }
    
    public TodoEvent withTodoType(TodoType todoType) {
        this.todoType = todoType;
        return this;
    }
    
    public TodoEvent withOldStatus(TodoStatus oldStatus) {
        this.oldStatus = oldStatus;
        return this;
    }
    
    public TodoEvent withNewStatus(TodoStatus newStatus) {
        this.newStatus = newStatus;
        return this;
    }
    
    public TodoEvent withSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
        return this;
    }
    
    public TodoEvent withFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
        return this;
    }
    
    public TodoEvent withToUserId(String toUserId) {
        this.toUserId = toUserId;
        return this;
    }
    
    public TodoEvent withPayload(String key, Object value) {
        if (this.payload == null) {
            this.payload = new HashMap<>();
        }
        this.payload.put(key, value);
        return this;
    }
    
    public static TodoEvent created(Object source, String todoId, TodoType todoType, 
                                    String sceneGroupId, String fromUserId, String toUserId) {
        TodoEvent event = new TodoEvent(source, SceneEventType.TODO_CREATED);
        event.setTodoId(todoId);
        event.setTodoType(todoType);
        event.setSceneGroupId(sceneGroupId);
        event.setFromUserId(fromUserId);
        event.setToUserId(toUserId);
        event.setNewStatus(TodoStatus.PENDING);
        return event;
    }
    
    public static TodoEvent statusChanged(Object source, String todoId, TodoType todoType,
                                           TodoStatus oldStatus, TodoStatus newStatus,
                                           String sceneGroupId, String toUserId) {
        TodoEvent event = new TodoEvent(source, mapStatusToEventType(newStatus));
        event.setTodoId(todoId);
        event.setTodoType(todoType);
        event.setOldStatus(oldStatus);
        event.setNewStatus(newStatus);
        event.setSceneGroupId(sceneGroupId);
        event.setToUserId(toUserId);
        return event;
    }
    
    public static TodoEvent deleted(Object source, String todoId, String sceneGroupId, String toUserId) {
        TodoEvent event = new TodoEvent(source, SceneEventType.TODO_DELETED);
        event.setTodoId(todoId);
        event.setSceneGroupId(sceneGroupId);
        event.setToUserId(toUserId);
        return event;
    }
    
    private static SceneEventType mapStatusToEventType(TodoStatus status) {
        if (status == null) {
            return SceneEventType.TODO_CREATED;
        }
        switch (status) {
            case ACCEPTED:
                return SceneEventType.TODO_ACCEPTED;
            case REJECTED:
                return SceneEventType.TODO_REJECTED;
            case COMPLETED:
                return SceneEventType.TODO_COMPLETED;
            case APPROVED:
                return SceneEventType.TODO_APPROVED;
            case EXPIRED:
                return SceneEventType.TODO_EXPIRED;
            case CANCELLED:
                return SceneEventType.TODO_CANCELLED;
            default:
                return SceneEventType.TODO_CREATED;
        }
    }
    
    @Override
    public String toString() {
        return "TodoEvent{" +
                "todoId='" + todoId + '\'' +
                ", todoType=" + todoType +
                ", oldStatus=" + oldStatus +
                ", newStatus=" + newStatus +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", eventType=" + getEventType() +
                '}';
    }
}
