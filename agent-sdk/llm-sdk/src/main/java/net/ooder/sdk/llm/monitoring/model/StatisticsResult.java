package net.ooder.sdk.llm.monitoring.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class StatisticsResult {
    private String queryId;
    private String metricsType;
    private Instant queryTime;
    private List<Map<String, Object>> dataPoints;
    private Map<String, Object> summary;
    private Long totalCount;

    public StatisticsResult() {
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getMetricsType() {
        return metricsType;
    }

    public void setMetricsType(String metricsType) {
        this.metricsType = metricsType;
    }

    public Instant getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Instant queryTime) {
        this.queryTime = queryTime;
    }

    public List<Map<String, Object>> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<Map<String, Object>> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
