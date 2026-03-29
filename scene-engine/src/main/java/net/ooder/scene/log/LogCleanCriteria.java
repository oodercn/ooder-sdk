package net.ooder.scene.log;

import java.util.List;

public class LogCleanCriteria {

    private List<LogCategory> categories;
    private Long beforeTime;
    private LogLevel maxLevel;
    private String companyId;
    private int retentionDays;

    public List<LogCategory> getCategories() { return categories; }
    public void setCategories(List<LogCategory> categories) { this.categories = categories; }

    public Long getBeforeTime() { return beforeTime; }
    public void setBeforeTime(Long beforeTime) { this.beforeTime = beforeTime; }

    public LogLevel getMaxLevel() { return maxLevel; }
    public void setMaxLevel(LogLevel maxLevel) { this.maxLevel = maxLevel; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public int getRetentionDays() { return retentionDays; }
    public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LogCleanCriteria criteria = new LogCleanCriteria();

        public Builder categories(List<LogCategory> categories) {
            criteria.categories = categories;
            return this;
        }

        public Builder beforeTime(Long beforeTime) {
            criteria.beforeTime = beforeTime;
            return this;
        }

        public Builder maxLevel(LogLevel maxLevel) {
            criteria.maxLevel = maxLevel;
            return this;
        }

        public Builder companyId(String companyId) {
            criteria.companyId = companyId;
            return this;
        }

        public Builder retentionDays(int retentionDays) {
            criteria.retentionDays = retentionDays;
            return this;
        }

        public LogCleanCriteria build() {
            return criteria;
        }
    }
}
