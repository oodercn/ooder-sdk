package net.ooder.sdk.api.monitoring;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MonitoringApi {
    
    void recordMetric(MetricData metric);
    
    void recordMetrics(List<MetricData> metrics);
    
    List<MetricData> queryMetrics(MetricQuery query);
    
    Optional<MetricData> getLatestMetric(String name);
    
    double getMetricAverage(String name, long durationMillis);
    
    double getMetricSum(String name, long durationMillis);
    
    void createAlert(AlertDefinition alert);
    
    void updateAlert(String alertId, AlertDefinition alert);
    
    void deleteAlert(String alertId);
    
    List<AlertDefinition> getAlerts();
    
    List<AlertInstance> getActiveAlerts();
    
    List<AlertInstance> getAlertHistory(String alertId, int limit);
    
    void acknowledgeAlert(String instanceId);
    
    void resolveAlert(String instanceId);
    
    void addAlertListener(AlertListener listener);
    
    void removeAlertListener(AlertListener listener);
    
    HealthCheckResult checkHealth(String componentId);
    
    List<HealthCheckResult> checkAllHealth();
    
    SystemStatus getSystemStatus();
    
    void startProfiling(String componentId);
    
    void stopProfiling(String componentId);
    
    ProfilingResult getProfilingResult(String componentId);
    
    List<LogEntry> queryLogs(LogQuery query);
    
    void addLogEntry(LogEntry entry);
    
    class MetricData {
        private String name;
        private double value;
        private Map<String, String> tags;
        private Instant timestamp;
        private String unit;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
        
        public Map<String, String> getTags() { return tags; }
        public void setTags(Map<String, String> tags) { this.tags = tags; }
        
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
    
    class MetricQuery {
        private String name;
        private Map<String, String> tags;
        private Instant startTime;
        private Instant endTime;
        private int limit;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Map<String, String> getTags() { return tags; }
        public void setTags(Map<String, String> tags) { this.tags = tags; }
        
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
    }
    
    class AlertDefinition {
        private String alertId;
        private String name;
        private String description;
        private String metricName;
        private AlertCondition condition;
        private double threshold;
        private int durationSeconds;
        private List<String> notificationChannels;
        private boolean enabled;
        
        public String getAlertId() { return alertId; }
        public void setAlertId(String alertId) { this.alertId = alertId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getMetricName() { return metricName; }
        public void setMetricName(String metricName) { this.metricName = metricName; }
        
        public AlertCondition getCondition() { return condition; }
        public void setCondition(AlertCondition condition) { this.condition = condition; }
        
        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }
        
        public int getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
        
        public List<String> getNotificationChannels() { return notificationChannels; }
        public void setNotificationChannels(List<String> notificationChannels) { this.notificationChannels = notificationChannels; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    enum AlertCondition {
        GREATER_THAN,
        LESS_THAN,
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN_OR_EQUAL
    }
    
    class AlertInstance {
        private String instanceId;
        private String alertId;
        private String alertName;
        private AlertSeverity severity;
        private String status;
        private double value;
        private String message;
        private Instant triggeredAt;
        private Instant acknowledgedAt;
        private Instant resolvedAt;
        private String acknowledgedBy;
        
        public String getInstanceId() { return instanceId; }
        public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
        
        public String getAlertId() { return alertId; }
        public void setAlertId(String alertId) { this.alertId = alertId; }
        
        public String getAlertName() { return alertName; }
        public void setAlertName(String alertName) { this.alertName = alertName; }
        
        public AlertSeverity getSeverity() { return severity; }
        public void setSeverity(AlertSeverity severity) { this.severity = severity; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Instant getTriggeredAt() { return triggeredAt; }
        public void setTriggeredAt(Instant triggeredAt) { this.triggeredAt = triggeredAt; }
        
        public Instant getAcknowledgedAt() { return acknowledgedAt; }
        public void setAcknowledgedAt(Instant acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
        
        public Instant getResolvedAt() { return resolvedAt; }
        public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
        
        public String getAcknowledgedBy() { return acknowledgedBy; }
        public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }
    }
    
    enum AlertSeverity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }
    
    interface AlertListener {
        void onAlertTriggered(AlertInstance alert);
        void onAlertAcknowledged(AlertInstance alert);
        void onAlertResolved(AlertInstance alert);
    }
    
    class HealthCheckResult {
        private String componentId;
        private boolean healthy;
        private String status;
        private String message;
        private Map<String, Object> details;
        private Instant checkedAt;
        
        public String getComponentId() { return componentId; }
        public void setComponentId(String componentId) { this.componentId = componentId; }
        
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
        
        public Instant getCheckedAt() { return checkedAt; }
        public void setCheckedAt(Instant checkedAt) { this.checkedAt = checkedAt; }
    }
    
    class SystemStatus {
        private String status;
        private int activeComponents;
        private int healthyComponents;
        private int unhealthyComponents;
        private double cpuUsage;
        private double memoryUsage;
        private long uptime;
        private Instant lastUpdated;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getActiveComponents() { return activeComponents; }
        public void setActiveComponents(int activeComponents) { this.activeComponents = activeComponents; }
        
        public int getHealthyComponents() { return healthyComponents; }
        public void setHealthyComponents(int healthyComponents) { this.healthyComponents = healthyComponents; }
        
        public int getUnhealthyComponents() { return unhealthyComponents; }
        public void setUnhealthyComponents(int unhealthyComponents) { this.unhealthyComponents = unhealthyComponents; }
        
        public double getCpuUsage() { return cpuUsage; }
        public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
        
        public double getMemoryUsage() { return memoryUsage; }
        public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
        
        public long getUptime() { return uptime; }
        public void setUptime(long uptime) { this.uptime = uptime; }
        
        public Instant getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
    }
    
    class ProfilingResult {
        private String componentId;
        private long duration;
        private long totalCalls;
        private long totalTime;
        private long maxTime;
        private long minTime;
        private double avgTime;
        private Map<String, Long> methodTimes;
        
        public String getComponentId() { return componentId; }
        public void setComponentId(String componentId) { this.componentId = componentId; }
        
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
        
        public long getTotalCalls() { return totalCalls; }
        public void setTotalCalls(long totalCalls) { this.totalCalls = totalCalls; }
        
        public long getTotalTime() { return totalTime; }
        public void setTotalTime(long totalTime) { this.totalTime = totalTime; }
        
        public long getMaxTime() { return maxTime; }
        public void setMaxTime(long maxTime) { this.maxTime = maxTime; }
        
        public long getMinTime() { return minTime; }
        public void setMinTime(long minTime) { this.minTime = minTime; }
        
        public double getAvgTime() { return avgTime; }
        public void setAvgTime(double avgTime) { this.avgTime = avgTime; }
        
        public Map<String, Long> getMethodTimes() { return methodTimes; }
        public void setMethodTimes(Map<String, Long> methodTimes) { this.methodTimes = methodTimes; }
    }
    
    class LogQuery {
        private String componentId;
        private String level;
        private String message;
        private Instant startTime;
        private Instant endTime;
        private int limit;
        
        public String getComponentId() { return componentId; }
        public void setComponentId(String componentId) { this.componentId = componentId; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
    }
    
    class LogEntry {
        private String entryId;
        private String componentId;
        private String level;
        private String message;
        private Map<String, Object> context;
        private Instant timestamp;
        private String threadName;
        private String exception;
        
        public String getEntryId() { return entryId; }
        public void setEntryId(String entryId) { this.entryId = entryId; }
        
        public String getComponentId() { return componentId; }
        public void setComponentId(String componentId) { this.componentId = componentId; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        
        public String getThreadName() { return threadName; }
        public void setThreadName(String threadName) { this.threadName = threadName; }
        
        public String getException() { return exception; }
        public void setException(String exception) { this.exception = exception; }
    }
}
