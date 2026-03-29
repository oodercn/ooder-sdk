package net.ooder.scene.log;

import java.util.List;

public class LogStatsCriteria {

    private List<LogCategory> categories;
    private Long startTime;
    private Long endTime;
    private String companyId;
    private String departmentId;
    private String userId;
    private String groupBy;

    public List<LogCategory> getCategories() { return categories; }
    public void setCategories(List<LogCategory> categories) { this.categories = categories; }

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

    public String getGroupBy() { return groupBy; }
    public void setGroupBy(String groupBy) { this.groupBy = groupBy; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LogStatsCriteria criteria = new LogStatsCriteria();

        public Builder categories(List<LogCategory> categories) {
            criteria.categories = categories;
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

        public Builder groupBy(String groupBy) {
            criteria.groupBy = groupBy;
            return this;
        }

        public LogStatsCriteria build() {
            return criteria;
        }
    }
}
