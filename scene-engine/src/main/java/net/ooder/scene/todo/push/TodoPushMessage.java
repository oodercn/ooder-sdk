package net.ooder.scene.todo.push;

import net.ooder.scene.todo.TodoDTO;

/**
 * Todo 推送消息
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class TodoPushMessage {

    private String action;
    private TodoDTO todo;
    private String sceneGroupId;
    private long timestamp;

    public TodoPushMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    public TodoPushMessage(String action, TodoDTO todo) {
        this.action = action;
        this.todo = todo;
        this.timestamp = System.currentTimeMillis();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public TodoDTO getTodo() {
        return todo;
    }

    public void setTodo(TodoDTO todo) {
        this.todo = todo;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCreated() {
        return "created".equals(action);
    }

    public boolean isUpdated() {
        return "status_changed".equals(action) || "updated".equals(action);
    }

    public boolean isDeleted() {
        return "deleted".equals(action);
    }

    public boolean isExpired() {
        return "expired".equals(action);
    }

    @Override
    public String toString() {
        return "TodoPushMessage{" +
                "action='" + action + '\'' +
                ", todoId='" + (todo != null ? todo.getId() : null) + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
