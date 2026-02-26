package net.ooder.sdk.llm.monitoring.model;

import java.time.Instant;
import java.util.List;

public class StatisticsQuery {
    private String queryId;
    private String metricsType;
    private Instant startTime;
    private Instant endTime;
    private List<String> groupBy;
    private String aggregation;
    private Integer limit;

    public StatisticsQuery() {
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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public List<String> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(List<String> groupBy) {
        this.groupBy = groupBy;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
