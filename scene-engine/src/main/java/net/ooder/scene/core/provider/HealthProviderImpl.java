package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.HealthProvider;
import net.ooder.scene.provider.model.health.HealthCheckResult;
import net.ooder.scene.provider.model.health.HealthCheckSchedule;
import net.ooder.scene.provider.model.health.HealthReport;
import net.ooder.scene.provider.model.health.ServiceCheckResult;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HealthProviderImpl implements HealthProvider {

    private static final String PROVIDER_NAME = "health-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;

    private final Map<String, HealthCheckSchedule> schedules = new ConcurrentHashMap<>();
    private final List<HealthCheckResult> checkHistory = new ArrayList<>();
    private final int maxHistorySize = 100;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Result<HealthCheckResult> runHealthCheck(Map<String, Object> params) {
        HealthCheckResult result = new HealthCheckResult();
        result.setCheckId(UUID.randomUUID().toString());
        result.setTimestamp(System.currentTimeMillis());

        List<HealthCheckResult.ComponentHealth> components = new ArrayList<>();
        List<String> issues = new ArrayList<>();
        boolean overallHealthy = true;

        HealthCheckResult.ComponentHealth memoryHealth = checkMemory();
        components.add(memoryHealth);
        if (!"healthy".equals(memoryHealth.getStatus())) {
            overallHealthy = false;
            issues.add("Memory: " + memoryHealth.getMessage());
        }

        HealthCheckResult.ComponentHealth cpuHealth = checkCpu();
        components.add(cpuHealth);
        if (!"healthy".equals(cpuHealth.getStatus())) {
            overallHealthy = false;
            issues.add("CPU: " + cpuHealth.getMessage());
        }

        HealthCheckResult.ComponentHealth threadHealth = checkThreads();
        components.add(threadHealth);
        if (!"healthy".equals(threadHealth.getStatus())) {
            overallHealthy = false;
            issues.add("Threads: " + threadHealth.getMessage());
        }

        HealthCheckResult.ComponentHealth diskHealth = checkDisk();
        components.add(diskHealth);
        if (!"healthy".equals(diskHealth.getStatus())) {
            overallHealthy = false;
            issues.add("Disk: " + diskHealth.getMessage());
        }

        result.setComponents(components);
        result.setIssues(issues);
        result.setHealthy(overallHealthy);
        result.setStatus(overallHealthy ? "healthy" : "unhealthy");

        Map<String, Object> details = new HashMap<>();
        details.put("componentCount", components.size());
        details.put("issueCount", issues.size());
        details.put("checkDuration", System.currentTimeMillis() - result.getTimestamp());
        result.setDetails(details);

        addToHistory(result);

        return Result.success(result);
    }

    private HealthCheckResult.ComponentHealth checkMemory() {
        HealthCheckResult.ComponentHealth health = new HealthCheckResult.ComponentHealth();
        health.setName("memory");

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long used = memoryMXBean.getHeapMemoryUsage().getUsed();
        long max = memoryMXBean.getHeapMemoryUsage().getMax();
        double usagePercent = max > 0 ? (double) used / max * 100 : 0;

        Map<String, Object> details = new HashMap<>();
        details.put("usedBytes", used);
        details.put("maxBytes", max);
        details.put("usagePercent", String.format("%.2f", usagePercent));
        health.setDetails(details);

        if (usagePercent > 90) {
            health.setStatus("critical");
            health.setMessage("Memory usage is critical: " + String.format("%.1f%%", usagePercent));
        } else if (usagePercent > 75) {
            health.setStatus("warning");
            health.setMessage("Memory usage is high: " + String.format("%.1f%%", usagePercent));
        } else {
            health.setStatus("healthy");
            health.setMessage("Memory usage is normal: " + String.format("%.1f%%", usagePercent));
        }

        return health;
    }

    private HealthCheckResult.ComponentHealth checkCpu() {
        HealthCheckResult.ComponentHealth health = new HealthCheckResult.ComponentHealth();
        health.setName("cpu");

        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        double loadAverage = osMXBean.getSystemLoadAverage();
        int processors = osMXBean.getAvailableProcessors();
        double loadPercent = loadAverage > 0 ? (loadAverage / processors) * 100 : 0;

        Map<String, Object> details = new HashMap<>();
        details.put("loadAverage", loadAverage);
        details.put("processors", processors);
        details.put("loadPercent", String.format("%.2f", loadPercent));
        health.setDetails(details);

        if (loadPercent > 90) {
            health.setStatus("critical");
            health.setMessage("CPU load is critical: " + String.format("%.1f%%", loadPercent));
        } else if (loadPercent > 75) {
            health.setStatus("warning");
            health.setMessage("CPU load is high: " + String.format("%.1f%%", loadPercent));
        } else {
            health.setStatus("healthy");
            health.setMessage("CPU load is normal: " + String.format("%.1f%%", loadPercent));
        }

        return health;
    }

    private HealthCheckResult.ComponentHealth checkThreads() {
        HealthCheckResult.ComponentHealth health = new HealthCheckResult.ComponentHealth();
        health.setName("threads");

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadMXBean.getThreadCount();
        int peakThreadCount = threadMXBean.getPeakThreadCount();
        int daemonThreadCount = threadMXBean.getDaemonThreadCount();

        Map<String, Object> details = new HashMap<>();
        details.put("threadCount", threadCount);
        details.put("peakThreadCount", peakThreadCount);
        details.put("daemonThreadCount", daemonThreadCount);
        health.setDetails(details);

        if (threadCount > 500) {
            health.setStatus("warning");
            health.setMessage("Thread count is high: " + threadCount);
        } else {
            health.setStatus("healthy");
            health.setMessage("Thread count is normal: " + threadCount);
        }

        return health;
    }

    private HealthCheckResult.ComponentHealth checkDisk() {
        HealthCheckResult.ComponentHealth health = new HealthCheckResult.ComponentHealth();
        health.setName("disk");

        java.io.File root = new java.io.File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usableSpace = root.getUsableSpace();
        double usagePercent = totalSpace > 0 ? (double) (totalSpace - freeSpace) / totalSpace * 100 : 0;

        Map<String, Object> details = new HashMap<>();
        details.put("totalSpace", totalSpace);
        details.put("freeSpace", freeSpace);
        details.put("usableSpace", usableSpace);
        details.put("usagePercent", String.format("%.2f", usagePercent));
        health.setDetails(details);

        if (usagePercent > 90) {
            health.setStatus("critical");
            health.setMessage("Disk usage is critical: " + String.format("%.1f%%", usagePercent));
        } else if (usagePercent > 80) {
            health.setStatus("warning");
            health.setMessage("Disk usage is high: " + String.format("%.1f%%", usagePercent));
        } else {
            health.setStatus("healthy");
            health.setMessage("Disk usage is normal: " + String.format("%.1f%%", usagePercent));
        }

        return health;
    }

    @Override
    public Result<HealthReport> exportHealthReport() {
        HealthReport report = new HealthReport();
        report.setReportId(UUID.randomUUID().toString());
        report.setGeneratedAt(System.currentTimeMillis());
        report.setPeriod("current");

        HealthReport.HealthSummary summary = new HealthReport.HealthSummary();
        int totalServices = 1;
        int healthyServices = running ? 1 : 0;
        summary.setTotalServices(totalServices);
        summary.setHealthyServices(healthyServices);
        summary.setUnhealthyServices(totalServices - healthyServices);
        summary.setAvailability(totalServices > 0 ? (double) healthyServices / totalServices * 100 : 0);
        report.setSummary(summary);

        List<HealthReport.ServiceHealth> services = new ArrayList<>();
        HealthReport.ServiceHealth engineHealth = new HealthReport.ServiceHealth();
        engineHealth.setServiceName("scene-engine");
        engineHealth.setStatus(running ? "healthy" : "stopped");
        engineHealth.setUptime(running ? 99.9 : 0);
        engineHealth.setAvgLatency(0);
        engineHealth.setErrorCount(0);
        services.add(engineHealth);
        report.setServices(services);

        List<HealthReport.Recommendation> recommendations = new ArrayList<>();
        if (!running) {
            HealthReport.Recommendation rec = new HealthReport.Recommendation();
            rec.setSeverity("high");
            rec.setMessage("Scene Engine is not running");
            rec.setAction("Start the Scene Engine service");
            recommendations.add(rec);
        }

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long used = memoryMXBean.getHeapMemoryUsage().getUsed();
        long max = memoryMXBean.getHeapMemoryUsage().getMax();
        double memoryUsage = max > 0 ? (double) used / max * 100 : 0;
        if (memoryUsage > 80) {
            HealthReport.Recommendation rec = new HealthReport.Recommendation();
            rec.setSeverity("medium");
            rec.setMessage("Memory usage is high: " + String.format("%.1f%%", memoryUsage));
            rec.setAction("Consider increasing heap size or optimizing memory usage");
            recommendations.add(rec);
        }
        report.setRecommendations(recommendations);

        return Result.success(report);
    }

    @Override
    public Result<HealthCheckSchedule> scheduleHealthCheck(Map<String, Object> params) {
        if (params == null) {
            return Result.badRequest("Schedule parameters are required");
        }

        String cron = (String) params.get("cron");
        if (cron == null || cron.isEmpty()) {
            return Result.badRequest("Cron expression is required");
        }

        HealthCheckSchedule schedule = new HealthCheckSchedule();
        schedule.setScheduleId(UUID.randomUUID().toString());
        schedule.setCron(cron);
        schedule.setEnabled(true);

        if (params.containsKey("checkType")) {
            schedule.setCheckType((String) params.get("checkType"));
        } else {
            schedule.setCheckType("full");
        }

        schedule.setNextRunAt(calculateNextRun(cron));
        schedule.setLastRunAt(0);

        schedules.put(schedule.getScheduleId(), schedule);

        return Result.success(schedule);
    }

    private long calculateNextRun(String cron) {
        return System.currentTimeMillis() + 60000;
    }

    @Override
    public Result<ServiceCheckResult> checkService(String serviceName) {
        if (serviceName == null || serviceName.isEmpty()) {
            return Result.badRequest("Service name is required");
        }

        ServiceCheckResult result = new ServiceCheckResult();
        result.setServiceName(serviceName);
        result.setTimestamp(System.currentTimeMillis());

        long startTime = System.currentTimeMillis();

        if ("scene-engine".equals(serviceName)) {
            result.setStatus(running ? "running" : "stopped");
            result.setReachable(running);
            result.setMessage(running ? "Service is running normally" : "Service is not running");
        } else {
            result.setStatus("unknown");
            result.setReachable(false);
            result.setMessage("Service not found: " + serviceName);
        }

        result.setLatency(System.currentTimeMillis() - startTime);

        return Result.success(result);
    }

    private synchronized void addToHistory(HealthCheckResult result) {
        checkHistory.add(result);
        while (checkHistory.size() > maxHistorySize) {
            checkHistory.remove(0);
        }
    }

    public List<HealthCheckResult> getCheckHistory() {
        return new ArrayList<>(checkHistory);
    }

    public Map<String, HealthCheckSchedule> getSchedules() {
        return new HashMap<>(schedules);
    }
}
