package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.health.HealthCheckResult;
import net.ooder.scene.provider.model.health.HealthCheckSchedule;
import net.ooder.scene.provider.model.health.HealthReport;
import net.ooder.scene.provider.model.health.ServiceCheckResult;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HealthProviderTest {

    private HealthProviderImpl healthProvider;

    @BeforeEach
    public void setup() {
        healthProvider = new HealthProviderImpl();
        healthProvider.initialize(null);
        healthProvider.start();
    }

    @AfterEach
    public void teardown() {
        healthProvider.stop();
    }

    @Test
    public void testProviderInitialization() {
        assertTrue(healthProvider.isInitialized());
        assertTrue(healthProvider.isRunning());
        assertEquals("health-provider", healthProvider.getProviderName());
        assertEquals("1.0.0", healthProvider.getVersion());
        assertEquals(100, healthProvider.getPriority());
    }

    @Test
    public void testRunHealthCheck() {
        Result<HealthCheckResult> result = healthProvider.runHealthCheck(null);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        HealthCheckResult checkResult = result.getData();
        assertNotNull(checkResult.getCheckId());
        assertTrue(checkResult.getTimestamp() > 0);
        assertNotNull(checkResult.getComponents());
        assertFalse(checkResult.getComponents().isEmpty());
    }

    @Test
    public void testRunHealthCheckComponents() {
        Result<HealthCheckResult> result = healthProvider.runHealthCheck(null);
        
        assertTrue(result.isSuccess());
        HealthCheckResult checkResult = result.getData();
        
        List<HealthCheckResult.ComponentHealth> components = checkResult.getComponents();
        assertTrue(components.size() >= 4);
        
        boolean hasMemory = false;
        boolean hasCpu = false;
        boolean hasThreads = false;
        boolean hasDisk = false;
        
        for (HealthCheckResult.ComponentHealth component : components) {
            if ("memory".equals(component.getName())) hasMemory = true;
            if ("cpu".equals(component.getName())) hasCpu = true;
            if ("threads".equals(component.getName())) hasThreads = true;
            if ("disk".equals(component.getName())) hasDisk = true;
            
            assertNotNull(component.getStatus());
            assertNotNull(component.getMessage());
        }
        
        assertTrue(hasMemory);
        assertTrue(hasCpu);
        assertTrue(hasThreads);
        assertTrue(hasDisk);
    }

    @Test
    public void testRunHealthCheckWithParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("checkType", "full");
        params.put("includeDetails", true);
        
        Result<HealthCheckResult> result = healthProvider.runHealthCheck(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData().getDetails());
    }

    @Test
    public void testExportHealthReport() {
        Result<HealthReport> result = healthProvider.exportHealthReport();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        HealthReport report = result.getData();
        assertNotNull(report.getReportId());
        assertTrue(report.getGeneratedAt() > 0);
        assertNotNull(report.getSummary());
        assertNotNull(report.getServices());
    }

    @Test
    public void testExportHealthReportSummary() {
        Result<HealthReport> result = healthProvider.exportHealthReport();
        
        assertTrue(result.isSuccess());
        HealthReport report = result.getData();
        
        HealthReport.HealthSummary summary = report.getSummary();
        assertTrue(summary.getTotalServices() >= 0);
        assertTrue(summary.getHealthyServices() >= 0);
        assertTrue(summary.getUnhealthyServices() >= 0);
        assertTrue(summary.getAvailability() >= 0);
    }

    @Test
    public void testExportHealthReportServices() {
        Result<HealthReport> result = healthProvider.exportHealthReport();
        
        assertTrue(result.isSuccess());
        HealthReport report = result.getData();
        
        List<HealthReport.ServiceHealth> services = report.getServices();
        assertFalse(services.isEmpty());
        
        HealthReport.ServiceHealth engineService = services.get(0);
        assertEquals("scene-engine", engineService.getServiceName());
        assertNotNull(engineService.getStatus());
    }

    @Test
    public void testScheduleHealthCheck() {
        Map<String, Object> params = new HashMap<>();
        params.put("cron", "0 0 * * *");
        params.put("checkType", "daily");
        
        Result<HealthCheckSchedule> result = healthProvider.scheduleHealthCheck(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        HealthCheckSchedule schedule = result.getData();
        assertNotNull(schedule.getScheduleId());
        assertEquals("0 0 * * *", schedule.getCron());
        assertTrue(schedule.isEnabled());
        assertEquals("daily", schedule.getCheckType());
    }

    @Test
    public void testScheduleHealthCheckWithNullParams() {
        Result<HealthCheckSchedule> result = healthProvider.scheduleHealthCheck(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testScheduleHealthCheckWithoutCron() {
        Map<String, Object> params = new HashMap<>();
        params.put("checkType", "daily");
        
        Result<HealthCheckSchedule> result = healthProvider.scheduleHealthCheck(params);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testCheckServiceSceneEngine() {
        Result<ServiceCheckResult> result = healthProvider.checkService("scene-engine");
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        ServiceCheckResult serviceResult = result.getData();
        assertEquals("scene-engine", serviceResult.getServiceName());
        assertEquals("running", serviceResult.getStatus());
        assertTrue(serviceResult.isReachable());
    }

    @Test
    public void testCheckServiceNotFound() {
        Result<ServiceCheckResult> result = healthProvider.checkService("non-existent-service");
        
        assertTrue(result.isSuccess());
        ServiceCheckResult serviceResult = result.getData();
        assertEquals("unknown", serviceResult.getStatus());
        assertFalse(serviceResult.isReachable());
    }

    @Test
    public void testCheckServiceWithNullName() {
        Result<ServiceCheckResult> result = healthProvider.checkService(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testCheckServiceWithEmptyName() {
        Result<ServiceCheckResult> result = healthProvider.checkService("");
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testProviderLifecycle() {
        HealthProviderImpl provider = new HealthProviderImpl();
        
        assertFalse(provider.isInitialized());
        assertFalse(provider.isRunning());
        
        provider.initialize(null);
        assertTrue(provider.isInitialized());
        assertFalse(provider.isRunning());
        
        provider.start();
        assertTrue(provider.isRunning());
        
        provider.stop();
        assertFalse(provider.isRunning());
    }

    @Test
    public void testStartWithoutInitialize() {
        HealthProviderImpl provider = new HealthProviderImpl();
        
        assertThrows(IllegalStateException.class, () -> provider.start());
    }

    @Test
    public void testCheckHistory() {
        healthProvider.runHealthCheck(null);
        healthProvider.runHealthCheck(null);
        healthProvider.runHealthCheck(null);
        
        List<HealthCheckResult> history = healthProvider.getCheckHistory();
        
        assertTrue(history.size() >= 3);
    }

    @Test
    public void testSchedules() {
        Map<String, Object> params = new HashMap<>();
        params.put("cron", "0 0 * * *");
        
        healthProvider.scheduleHealthCheck(params);
        
        Map<String, HealthCheckSchedule> schedules = healthProvider.getSchedules();
        
        assertFalse(schedules.isEmpty());
    }
}
