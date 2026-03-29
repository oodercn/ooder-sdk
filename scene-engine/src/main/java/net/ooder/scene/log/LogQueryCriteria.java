package net.ooder.scene.log;

import java.util.List;

public class LogQueryCriteria {

    private List<LogCategory> categories;
    private String subType;
    private List<LogLevel> levels;
    private List<LogStatus> statuses;

    private Long startTime;
    private Long endTime;

    private String companyId;
    private String departmentId;
    private String userId;
    private String sessionId;
    private String traceId;
    private String parentLogId;

    private String source;
    private String action;
    private String keyword;

    private String resourceType;
    private String resourceId;

    private int pageNum = 1;
    private int pageSize = 20;
    private String sortOrder = "desc";
    private String sortField = "timestamp";

    public List<LogCategory> getCategories() { return categories; }
    public void setCategories(List<LogCategory> categories) { this.categories = categories; }

    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }

    public List<LogLevel> getLevels() { return levels; }
    public void setLevels(List<LogLevel> levels) { this.levels = levels; }

    public List<LogStatus> getStatuses() { return statuses; }
    public void setStatuses(List<LogStatus> statuses) { this.statuses = statuses; }

    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }

    public Long getEndTime() { return endTime; }
    public void setEndTime(Long endTime) { this.endTime = endTime; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public String getParentLogId() { return parentLogId; }
    public void setParentLogId(String parentLogId) { this.parentLogId = parentLogId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LogQueryCriteria criteria = new LogQueryCriteria();

        public Builder categories(List<LogCategory> categories) {
            criteria.categories = categories;
            return this;
        }

        public Builder category(LogCategory category) {
            criteria.categories = List.of(category);
            return this;
        }

        public Builder subType(String subType) {
            criteria.subType = subType;
            return this;
        }

        public Builder levels(List<LogLevel> levels) {
            criteria.levels = levels;
            return this;
        }

        public Builder level(LogLevel level) {
            criteria.levels = List.of(level);
            return this;
        }

        public Builder statuses(List<LogStatus> statuses) {
            criteria.statuses = statuses;
            return this;
        }

        public Builder status(LogStatus status) {
            criteria.statuses = List.of(status);
            return this;
        }

        public Builder timeRange(Long startTime, Long endTime) {
            criteria.startTime = startTime;
            criteria.endTime = endTime;
            return this;
        }

        public Builder companyId(String companyId) {
            criteria.companyId = companyId;
            return this;
        }

        public Builder departmentId(String departmentId) {
            criteria.departmentId = departmentId;
            return this;
        }

        public Builder userId(String userId) {
            criteria.userId = userId;
            return this;
        }

        public Builder sessionId(String sessionId) {
            criteria.sessionId = sessionId;
            return this;
        }

        public Builder traceId(String traceId) {
            criteria.traceId = traceId;
            return this;
        }

        public Builder parentLogId(String parentLogId) {
            criteria.parentLogId = parentLogId;
            return this;
        }

        public Builder source(String source) {
            criteria.source = source;
            return this;
        }

        public Builder action(String action) {
            criteria.action = action;
            return this;
        }

        public Builder keyword(String keyword) {
            criteria.keyword = keyword;
            return this;
        }

        public Builder resource(String resourceType, String resourceId) {
            criteria.resourceType = resourceType;
            criteria.resourceId = resourceId;
            return this;
        }

        public Builder page(int pageNum, int pageSize) {
            criteria.pageNum = pageNum;
            criteria.pageSize = pageSize;
            return this;
        }

        public Builder sort(String field, String order) {
            criteria.sortField = field;
            criteria.sortOrder = order;
            return this;
        }

        public LogQueryCriteria build() {
            return criteria;
        }
    }
}
