package net.ooder.scene.core.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.*;
import java.net.InetAddress;
import java.util.*;

/**
 * SystemProvider核心实现
 *
 * <p>SEC Engine内置实现，提供系统信息、服务状态、资源使用等功能</p>
 */
public class SystemProviderImpl implements SystemProvider {

    private static final String PROVIDER_NAME = "system-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    private long startTime;

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
        this.startTime = System.currentTimeMillis();
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
        return 1000;
    }

    @Override
    public Result<SystemInfo> getSystemInfo() {
        try {
            SystemInfo info = new SystemInfo();
            
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
            
            info.setVersion(VERSION);
            info.setName("SEC Engine");
            info.setDescription("Scene Engine Core System Provider");
            info.setStartTime(startTime);
            info.setEnvironment(System.getProperty("environment", "development"));
            
            info.setJavaVersion(System.getProperty("java.version"));
            info.setJavaVendor(System.getProperty("java.vendor"));
            info.setJavaHome(System.getProperty("java.home"));
            
            info.setOsName(osMXBean.getName());
            info.setOsVersion(osMXBean.getVersion());
            info.setOsArch(System.getProperty("os.arch"));
            
            try {
                InetAddress localhost = InetAddress.getLocalHost();
                info.setHostname(localhost.getHostName());
                info.setIpAddress(localhost.getHostAddress());
            } catch (Exception e) {
                info.setHostname("unknown");
                info.setIpAddress("unknown");
            }
            
            info.setUptime(System.currentTimeMillis() - startTime);
            info.setAvailableProcessors(osMXBean.getAvailableProcessors());
            
            return Result.success(info);
        } catch (Exception e) {
            return Result.error("Failed to get system info: " + e.getMessage());
        }
    }

    @Override
    public Result<SystemStatus> getSystemStatus() {
        try {
            SystemStatus status = new SystemStatus();
            
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
            
            status.setStatus("running");
            status.setMessage("System is running normally");
            status.setTimestamp(System.currentTimeMillis());
            
            double cpuLoad = osMXBean.getSystemLoadAverage();
            if (cpuLoad < 0) cpuLoad = 0;
            status.setCpuUsage(Math.min(cpuLoad / osMXBean.getAvailableProcessors() * 100, 100));
            
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
            long usedMemory = heapUsage.getUsed();
            long maxMemory = heapUsage.getMax();
            status.setMemoryUsage(maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0);
            status.setTotalMemory(heapUsage.getCommitted());
            status.setFreeMemory(heapUsage.getCommitted() - usedMemory);
            status.setMaxMemory(maxMemory);
            
            status.setDiskUsage(getDiskUsage());
            
            status.setThreadCount(threadMXBean.getThreadCount());
            status.setActiveThreads(threadMXBean.getPeakThreadCount());
            
            return Result.success(status);
        } catch (Exception e) {
            return Result.error("Failed to get system status: " + e.getMessage());
        }
    }

    @Override
    public Result<SystemLoad> getSystemLoad() {
        try {
            SystemLoad load = new SystemLoad();
            
            OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            
            double cpuLoad = osMXBean.getSystemLoadAverage();
            if (cpuLoad < 0) cpuLoad = 0;
            load.setCpuLoad(cpuLoad);
            
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
            long usedMemory = heapUsage.getUsed();
            long maxMemory = heapUsage.getMax();
            load.setMemoryUsage(maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0);
            
            load.setDiskUsage(getDiskUsage());
            load.setNetworkUsage(0);
            
            load.setThreadCount(threadMXBean.getThreadCount());
            load.setProcessCount(0);
            load.setTimestamp(System.currentTimeMillis());
            
            return Result.success(load);
        } catch (Exception e) {
            return Result.error("Failed to get system load: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResult<ServiceInfo>> listServices(int page, int size) {
        List<ServiceInfo> services = new ArrayList<>();
        
        ServiceInfo engineService = new ServiceInfo();
        engineService.setServiceName("scene-engine");
        engineService.setDisplayName("Scene Engine Core");
        engineService.setStatus(running ? "running" : "stopped");
        engineService.setMessage(running ? "Service is running" : "Service is stopped");
        engineService.setLastUpdated(System.currentTimeMillis());
        engineService.setRestartCount(0);
        engineService.setServiceType("core");
        engineService.setEnabled(true);
        services.add(engineService);
        
        int total = services.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<ServiceInfo> pagedServices = start < total ? services.subList(start, end) : new ArrayList<>();
        
        PageResult<ServiceInfo> result = new PageResult<>();
        result.setItems(pagedServices);
        result.setTotal(total);
        result.setPageNum(page);
        result.setPageSize(size);
        
        return Result.success(result);
    }

    @Override
    public Result<ServiceInfo> getService(String serviceName) {
        if ("scene-engine".equals(serviceName)) {
            ServiceInfo service = new ServiceInfo();
            service.setServiceName("scene-engine");
            service.setDisplayName("Scene Engine Core");
            service.setStatus(running ? "running" : "stopped");
            service.setMessage(running ? "Service is running" : "Service is stopped");
            service.setLastUpdated(System.currentTimeMillis());
            service.setRestartCount(0);
            service.setServiceType("core");
            service.setEnabled(true);
            return Result.success(service);
        }
        return Result.notFound("Service not found: " + serviceName);
    }

    @Override
    public Result<Boolean> startService(String serviceName) {
        if ("scene-engine".equals(serviceName)) {
            if (!running) {
                start();
                return Result.success(true);
            }
            return Result.success(false);
        }
        return Result.notFound("Service not found: " + serviceName);
    }

    @Override
    public Result<Boolean> stopService(String serviceName) {
        if ("scene-engine".equals(serviceName)) {
            if (running) {
                stop();
                return Result.success(true);
            }
            return Result.success(false);
        }
        return Result.notFound("Service not found: " + serviceName);
    }

    @Override
    public Result<Boolean> restartService(String serviceName) {
        if ("scene-engine".equals(serviceName)) {
            stop();
            start();
            return Result.success(true);
        }
        return Result.notFound("Service not found: " + serviceName);
    }

    @Override
    public Result<List<ResourceUsage>> getResourceUsage() {
        List<ResourceUsage> resources = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        
        ResourceUsage memoryUsage = new ResourceUsage();
        memoryUsage.setName("Heap Memory");
        memoryUsage.setType("memory");
        memoryUsage.setUnit("bytes");
        memoryUsage.setUsed(heapUsage.getUsed());
        memoryUsage.setTotal(heapUsage.getMax());
        memoryUsage.setPercentage(heapUsage.getMax() > 0 ? (double) heapUsage.getUsed() / heapUsage.getMax() * 100 : 0);
        memoryUsage.setTimestamp(timestamp);
        resources.add(memoryUsage);
        
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ResourceUsage threadUsage = new ResourceUsage();
        threadUsage.setName("Threads");
        threadUsage.setType("thread");
        threadUsage.setUnit("count");
        threadUsage.setUsed(threadMXBean.getThreadCount());
        threadUsage.setTotal(threadMXBean.getPeakThreadCount());
        threadUsage.setPercentage(0);
        threadUsage.setTimestamp(timestamp);
        resources.add(threadUsage);
        
        return Result.success(resources);
    }

    @Override
    public Result<Map<String, String>> getEnvironmentVariables() {
        return Result.success(System.getenv());
    }

    @Override
    public Result<Map<String, String>> getSystemProperties() {
        Properties props = System.getProperties();
        Map<String, String> result = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            result.put(key, props.getProperty(key));
        }
        return Result.success(result);
    }

    @Override
    public Result<SystemCommandResult> executeCommand(String command) {
        SystemCommandResult result = new SystemCommandResult();
        result.setCommand(command);
        result.setTimestamp(System.currentTimeMillis());
        
        long startTime = System.currentTimeMillis();
        
        try {
            ProcessBuilder pb;
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                pb = new ProcessBuilder("sh", "-c", command);
            }
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            result.setExitCode(exitCode);
            result.setOutput(output.toString());
            result.setError("");
            result.setDuration(System.currentTimeMillis() - startTime);
            
            return Result.success(result);
        } catch (Exception e) {
            result.setExitCode(-1);
            result.setOutput("");
            result.setError(e.getMessage());
            result.setDuration(System.currentTimeMillis() - startTime);
            return Result.success(result);
        }
    }

    private double getDiskUsage() {
        try {
            java.io.File root = new java.io.File("/");
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            if (totalSpace > 0) {
                return (double) (totalSpace - freeSpace) / totalSpace * 100;
            }
        } catch (Exception e) {
            // Ignore
        }
        return 0;
    }
}
