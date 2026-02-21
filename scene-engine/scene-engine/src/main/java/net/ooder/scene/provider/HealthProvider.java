package net.ooder.scene.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.health.HealthCheckResult;
import net.ooder.scene.provider.model.health.HealthReport;
import net.ooder.scene.provider.model.health.HealthCheckSchedule;
import net.ooder.scene.provider.model.health.ServiceCheckResult;

import java.util.Map;

public interface HealthProvider extends BaseProvider {
    
    Result<HealthCheckResult> runHealthCheck(Map<String, Object> params);
    
    Result<HealthReport> exportHealthReport();
    
    Result<HealthCheckSchedule> scheduleHealthCheck(Map<String, Object> params);
    
    Result<ServiceCheckResult> checkService(String serviceName);
}
