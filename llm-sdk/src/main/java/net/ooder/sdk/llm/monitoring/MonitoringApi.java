package net.ooder.sdk.llm.monitoring;

import net.ooder.sdk.llm.common.enums.MetricsType;
import net.ooder.sdk.llm.monitoring.model.AlertConfig;
import net.ooder.sdk.llm.monitoring.model.AlertConfigResult;
import net.ooder.sdk.llm.monitoring.model.HealthStatus;
import net.ooder.sdk.llm.monitoring.model.MetricsData;
import net.ooder.sdk.llm.monitoring.model.ReportFile;
import net.ooder.sdk.llm.monitoring.model.ReportRequest;
import net.ooder.sdk.llm.monitoring.model.StatisticsQuery;
import net.ooder.sdk.llm.monitoring.model.StatisticsResult;

public interface MonitoringApi {
    
    MetricsData collectMetrics(MetricsType type);
    
    StatisticsResult getStatistics(StatisticsQuery query);
    
    AlertConfigResult setAlert(AlertConfig config);
    
    HealthStatus getHealthStatus();
    
    ReportFile exportReport(ReportRequest request);
}
