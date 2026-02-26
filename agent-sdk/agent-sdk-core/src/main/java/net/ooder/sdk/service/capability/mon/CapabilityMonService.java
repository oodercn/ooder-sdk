package net.ooder.sdk.service.capability.mon;

import net.ooder.sdk.api.capability.Capability;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CapabilityMonService {
    
    CompletableFuture<Void> startMonitoring(String capabilityId, Map<String, Object> config);
    
    CompletableFuture<Void> stopMonitoring(String capabilityId);
    
    CompletableFuture<Map<String, Object>> getMonitorStatus(String capabilityId);
    
    CompletableFuture<List<Map<String, Object>>> getExecutionLogs(String capabilityId, Map<String, Object> query);
    
    CompletableFuture<List<Map<String, Object>>> getMetrics(String capabilityId, Map<String, Object> query);
    
    CompletableFuture<Map<String, Object>> getTrace(String traceId);
    
    CompletableFuture<List<Map<String, Object>>> getAlerts(String capabilityId);
    
    CompletableFuture<Void> acknowledgeAlert(String alertId);
    
    void addMonitorListener(MonitorListener listener);
    
    void removeMonitorListener(MonitorListener listener);
    
    interface MonitorListener {
        void onExecutionStart(String capabilityId, String traceId);
        void onExecutionEnd(String capabilityId, String traceId, boolean success);
        void onAlert(String capabilityId, Map<String, Object> alert);
    }
}
