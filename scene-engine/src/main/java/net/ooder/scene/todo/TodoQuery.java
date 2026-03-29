package net.ooder.scene.todo;

import java.util.ArrayList;
import java.util.List;

/**
 * 待办查询条件
 * 
 * <p>用于查询待办列表时的过滤条件。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class TodoQuery {
    
    private List<TodoType> types;
    private List<TodoStatus> statuses;
    private String sceneGroupId;
    private String fromUserId;
    private String priority;
    private Boolean expired;
    private Long deadlineFrom;
    private Long deadlineTo;
    private String keyword;
    private int pageNum = 1;
    private int pageSize = 20;
    private String sortBy = "createTime";
    private String sortOrder = "desc";
    
    public TodoQuery() {}
    
    public List<TodoType> getTypes() {
        return types;
    }
    
    public void setTypes(List<TodoType> types) {
        this.types = types;
    }
    
    public TodoQuery addType(TodoType type) {
        if (this.types == null) {
            this.types = new ArrayList<>();
        }
        this.types.add(type);
        return this;
    }
    
    public List<TodoStatus> getStatuses() {
        return statuses;
    }
    
    public void setStatuses(List<TodoStatus> statuses) {
        this.statuses = statuses;
    }
    
    public TodoQuery addStatus(TodoStatus status) {
        if (this.statuses == null) {
            this.statuses = new ArrayList<>();
        }
        this.statuses.add(status);
        return this;
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
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Boolean getExpired() {
        return expired;
    }
    
    public void setExpired(Boolean expired) {
        this.expired = expired;
    }
    
    public Long getDeadlineFrom() {
        return deadlineFrom;
    }
    
    public void setDeadlineFrom(Long deadlineFrom) {
        this.deadlineFrom = deadlineFrom;
    }
    
    public Long getDeadlineTo() {
        return deadlineTo;
    }
    
    public void setDeadlineTo(Long deadlineTo) {
        this.deadlineTo = deadlineTo;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public int getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final TodoQuery query = new TodoQuery();
        
        public Builder types(List<TodoType> types) {
            query.setTypes(types);
            return this;
        }
        
        public Builder addType(TodoType type) {
            query.addType(type);
            return this;
        }
        
        public Builder statuses(List<TodoStatus> statuses) {
            query.setStatuses(statuses);
            return this;
        }
        
        public Builder addStatus(TodoStatus status) {
            query.addStatus(status);
            return this;
        }
        
        public Builder sceneGroupId(String sceneGroupId) {
            query.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder fromUserId(String fromUserId) {
            query.setFromUserId(fromUserId);
            return this;
        }
        
        public Builder priority(String priority) {
            query.setPriority(priority);
            return this;
        }
        
        public Builder expired(Boolean expired) {
            query.setExpired(expired);
            return this;
        }
        
        public Builder deadlineFrom(Long deadlineFrom) {
            query.setDeadlineFrom(deadlineFrom);
            return this;
        }
        
        public Builder deadlineTo(Long deadlineTo) {
            query.setDeadlineTo(deadlineTo);
            return this;
        }
        
        public Builder keyword(String keyword) {
            query.setKeyword(keyword);
            return this;
        }
        
        public Builder pageNum(int pageNum) {
            query.setPageNum(pageNum);
            return this;
        }
        
        public Builder pageSize(int pageSize) {
            query.setPageSize(pageSize);
            return this;
        }
        
        public Builder sortBy(String sortBy) {
            query.setSortBy(sortBy);
            return this;
        }
        
        public Builder sortOrder(String sortOrder) {
            query.setSortOrder(sortOrder);
            return this;
        }
        
        public TodoQuery build() {
            return query;
        }
    }
}
