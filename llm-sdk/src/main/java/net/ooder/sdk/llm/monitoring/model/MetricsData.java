package net.ooder.sdk.llm.monitoring.model;

import java.time.Instant;
import java.util.Map;

public class MetricsData {
    private String metricsId;
    private String metricsType;
    private Instant timestamp;
    private Map<String, Object> metrics;
    private String source;

    public MetricsData() {
    }

    public String getMetricsId() {
        return metricsId;
    }

    public void setMetricsId(String metricsId) {
        this.metricsId = metricsId;
    }

    public String getMetricsType() {
        return metricsType;
    }

    public void setMetricsType(String metricsType) {
        this.metricsType = metricsType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
