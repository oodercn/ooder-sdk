package net.ooder.skill.common.spi.todo;

public class TodoInfo {
    
    private String todoId;
    private String title;
    private String description;
    private String assigneeId;
    private String creatorId;
    private TodoStatus status;
    private int priority;
    private long dueTime;
    private long createTime;
    private long completeTime;
    private String platformSource;
    
    public TodoInfo() {}
    
    public TodoInfo(String title, String assigneeId) {
        this.title = title;
        this.assigneeId = assigneeId;
        this.status = TodoStatus.PENDING;
        this.createTime = System.currentTimeMillis();
    }
    
    public String getTodoId() { return todoId; }
    public void setTodoId(String todoId) { this.todoId = todoId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public TodoStatus getStatus() { return status; }
    public void setStatus(TodoStatus status) { this.status = status; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public long getDueTime() { return dueTime; }
    public void setDueTime(long dueTime) { this.dueTime = dueTime; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getCompleteTime() { return completeTime; }
    public void setCompleteTime(long completeTime) { this.completeTime = completeTime; }
    public String getPlatformSource() { return platformSource; }
    public void setPlatformSource(String platformSource) { this.platformSource = platformSource; }
}
