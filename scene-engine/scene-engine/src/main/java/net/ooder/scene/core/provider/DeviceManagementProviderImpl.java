package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.DeviceManagementProvider;
import net.ooder.scene.provider.model.network.CommandResult;
import net.ooder.scene.provider.model.network.ConnectionStatus;
import net.ooder.scene.provider.model.network.SystemStatus;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

public class DeviceManagementProviderImpl implements DeviceManagementProvider {

    private static final String PROVIDER_NAME = "device-management-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;

    private ConnectionStatus connectionStatus;
    private long connectedAt = 0;

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
        this.connectionStatus = new ConnectionStatus();
        this.connectionStatus.setConnected(false);
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
    public Result<Boolean> connect(Map<String, Object> connectionData) {
        if (connectionData == null) {
            return Result.badRequest("Connection data is required");
        }

        String endpoint = (String) connectionData.get("endpoint");
        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = "local://localhost";
        }

        String connectionType = (String) connectionData.get("connectionType");
        if (connectionType == null || connectionType.isEmpty()) {
            connectionType = "local";
        }

        connectionStatus.setConnected(true);
        connectionStatus.setEndpoint(endpoint);
        connectionStatus.setConnectionType(connectionType);
        connectionStatus.setLatency(0);
        connectedAt = System.currentTimeMillis();
        connectionStatus.setConnectedAt(connectedAt);
        connectionStatus.setLastActivityAt(connectedAt);
        connectionStatus.setReconnectCount(0);

        return Result.success(true);
    }

    @Override
    public Result<Boolean> disconnect() {
        if (!connectionStatus.isConnected()) {
            return Result.success(false);
        }

        connectionStatus.setConnected(false);
        connectionStatus.setEndpoint(null);
        connectionStatus.setLatency(0);
        connectedAt = 0;

        return Result.success(true);
    }

    @Override
    public Result<ConnectionStatus> getConnectionStatus() {
        if (connectionStatus.isConnected()) {
            connectionStatus.setLastActivityAt(System.currentTimeMillis());
            connectionStatus.setLatency(calculateLatency());
        }
        return Result.success(connectionStatus);
    }

    private long calculateLatency() {
        return 1;
    }

    @Override
    public Result<CommandResult> executeCommand(String command) {
        return executeCommand(command, null);
    }

    @Override
    public Result<CommandResult> executeCommand(String command, Map<String, Object> params) {
        if (command == null || command.isEmpty()) {
            return Result.badRequest("Command is required");
        }

        if (!connectionStatus.isConnected()) {
            CommandResult result = new CommandResult();
            result.setSuccess(false);
            result.setError("Not connected to device");
            result.setExitCode(-1);
            result.setTimestamp(System.currentTimeMillis());
            return Result.success(result);
        }

        CommandResult result = new CommandResult();
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
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            result.setSuccess(exitCode == 0);
            result.setOutput(output.toString());
            result.setExitCode(exitCode);

            if (exitCode != 0) {
                result.setError("Command exited with non-zero code: " + exitCode);
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setOutput("");
            result.setError(e.getMessage());
            result.setExitCode(-1);
        }

        result.setDuration(System.currentTimeMillis() - startTime);
        result.setTimestamp(System.currentTimeMillis());

        connectionStatus.setLastActivityAt(System.currentTimeMillis());

        return Result.success(result);
    }

    @Override
    public Result<SystemStatus> getSystemStatus() {
        SystemStatus status = new SystemStatus();

        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        double loadAverage = osMXBean.getSystemLoadAverage();
        int processors = osMXBean.getAvailableProcessors();
        status.setCpuUsage(loadAverage > 0 ? (loadAverage / processors) * 100 : 0);

        long memoryUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long memoryMax = memoryMXBean.getHeapMemoryUsage().getMax();
        status.setMemoryUsed(memoryUsed / (1024 * 1024));
        status.setMemoryTotal(memoryMax / (1024 * 1024));
        status.setMemoryUsage(memoryMax > 0 ? (double) memoryUsed / memoryMax * 100 : 0);

        status.setUptime(runtimeMXBean.getUptime() / 1000);

        status.setHostname(getHostname());
        status.setOsVersion(System.getProperty("os.name") + " " + System.getProperty("os.version"));
        status.setKernelVersion(System.getProperty("os.version"));

        status.setLoadAverage1(loadAverage);
        status.setLoadAverage5(loadAverage);
        status.setLoadAverage15(loadAverage);

        return Result.success(status);
    }

    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    @Override
    public Result<Boolean> restart() {
        if (!connectionStatus.isConnected()) {
            return Result.error("Not connected to device");
        }

        return Result.success(true);
    }

    @Override
    public Result<Boolean> shutdown() {
        if (!connectionStatus.isConnected()) {
            return Result.error("Not connected to device");
        }

        connectionStatus.setConnected(false);
        return Result.success(true);
    }
}
