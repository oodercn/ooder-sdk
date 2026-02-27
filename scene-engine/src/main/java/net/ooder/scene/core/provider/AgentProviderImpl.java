package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.AgentProvider;
import net.ooder.scene.provider.model.agent.CommandStatsData;
import net.ooder.scene.provider.model.agent.EndAgent;
import net.ooder.scene.provider.model.agent.NetworkStatusData;
import net.ooder.scene.provider.model.agent.TestCommandResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AgentProviderImpl implements AgentProvider {

    private static final String PROVIDER_NAME = "agent-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;

    private final Map<String, EndAgent> agentRegistry = new ConcurrentHashMap<>();
    private final AtomicLong totalCommands = new AtomicLong(0);
    private final AtomicLong successCommands = new AtomicLong(0);
    private final AtomicLong totalLatency = new AtomicLong(0);
    private final AtomicLong maxLatency = new AtomicLong(0);
    private final AtomicLong minLatency = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong failedCount = new AtomicLong(0);
    private final AtomicLong timeoutCount = new AtomicLong(0);

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
    public Result<List<EndAgent>> getEndAgents() {
        List<EndAgent> agents = new ArrayList<>(agentRegistry.values());
        return Result.success(agents);
    }

    @Override
    public Result<EndAgent> addEndAgent(Map<String, Object> agentData) {
        if (agentData == null) {
            return Result.badRequest("Agent data is required");
        }

        EndAgent agent = new EndAgent();
        String agentId = UUID.randomUUID().toString();
        agent.setAgentId(agentId);

        if (agentData.containsKey("name")) {
            agent.setName((String) agentData.get("name"));
        }
        if (agentData.containsKey("type")) {
            agent.setType((String) agentData.get("type"));
        }
        if (agentData.containsKey("status")) {
            agent.setStatus((String) agentData.get("status"));
        } else {
            agent.setStatus("active");
        }
        if (agentData.containsKey("endpoint")) {
            agent.setEndpoint((String) agentData.get("endpoint"));
        }
        if (agentData.containsKey("description")) {
            agent.setDescription((String) agentData.get("description"));
        }
        if (agentData.containsKey("version")) {
            agent.setVersion((String) agentData.get("version"));
        }
        if (agentData.containsKey("metadata")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) agentData.get("metadata");
            agent.setMetadata(metadata);
        }

        agent.setLastActiveAt(System.currentTimeMillis());

        agentRegistry.put(agentId, agent);

        return Result.success(agent);
    }

    @Override
    public Result<EndAgent> editEndAgent(String agentId, Map<String, Object> agentData) {
        if (agentId == null || agentId.isEmpty()) {
            return Result.badRequest("Agent ID is required");
        }

        EndAgent existingAgent = agentRegistry.get(agentId);
        if (existingAgent == null) {
            return Result.notFound("Agent not found: " + agentId);
        }

        if (agentData == null) {
            return Result.badRequest("Agent data is required");
        }

        if (agentData.containsKey("name")) {
            existingAgent.setName((String) agentData.get("name"));
        }
        if (agentData.containsKey("type")) {
            existingAgent.setType((String) agentData.get("type"));
        }
        if (agentData.containsKey("status")) {
            existingAgent.setStatus((String) agentData.get("status"));
        }
        if (agentData.containsKey("endpoint")) {
            existingAgent.setEndpoint((String) agentData.get("endpoint"));
        }
        if (agentData.containsKey("description")) {
            existingAgent.setDescription((String) agentData.get("description"));
        }
        if (agentData.containsKey("version")) {
            existingAgent.setVersion((String) agentData.get("version"));
        }
        if (agentData.containsKey("metadata")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) agentData.get("metadata");
            existingAgent.setMetadata(metadata);
        }

        existingAgent.setLastActiveAt(System.currentTimeMillis());

        return Result.success(existingAgent);
    }

    @Override
    public Result<EndAgent> deleteEndAgent(String agentId) {
        if (agentId == null || agentId.isEmpty()) {
            return Result.badRequest("Agent ID is required");
        }

        EndAgent removedAgent = agentRegistry.remove(agentId);
        if (removedAgent == null) {
            return Result.notFound("Agent not found: " + agentId);
        }

        return Result.success(removedAgent);
    }

    @Override
    public Result<EndAgent> getEndAgentDetails(String agentId) {
        if (agentId == null || agentId.isEmpty()) {
            return Result.badRequest("Agent ID is required");
        }

        EndAgent agent = agentRegistry.get(agentId);
        if (agent == null) {
            return Result.notFound("Agent not found: " + agentId);
        }

        return Result.success(agent);
    }

    @Override
    public Result<NetworkStatusData> getNetworkStatus() {
        NetworkStatusData status = new NetworkStatusData();
        status.setOnline(running);
        status.setLatency(calculateAvgLatency());
        status.setBandwidth(1000000);
        status.setConnectionType("internal");
        status.setIpAddress("127.0.0.1");
        status.setActiveConnections(agentRegistry.size());

        return Result.success(status);
    }

    @Override
    public Result<CommandStatsData> getCommandStats() {
        CommandStatsData stats = new CommandStatsData();
        stats.setTotalCommands(totalCommands.get());

        long total = totalCommands.get();
        long success = successCommands.get();
        double successRate = total > 0 ? (double) success / total * 100 : 0;
        stats.setSuccessRate(successRate);

        stats.setAvgLatency(calculateAvgLatency());
        stats.setMaxLatency(maxLatency.get());
        stats.setMinLatency(minLatency.get() == Long.MAX_VALUE ? 0 : minLatency.get());
        stats.setFailedCount(failedCount.get());
        stats.setTimeoutCount(timeoutCount.get());

        return Result.success(stats);
    }

    @Override
    public Result<TestCommandResult> testCommand(Map<String, Object> commandData) {
        if (commandData == null) {
            return Result.badRequest("Command data is required");
        }

        TestCommandResult result = new TestCommandResult();
        long startTime = System.currentTimeMillis();

        String command = (String) commandData.get("command");
        if (command == null || command.isEmpty()) {
            result.setSuccess(false);
            result.setErrorCode("INVALID_COMMAND");
            result.setErrorMessage("Command is required");
            result.setDuration(System.currentTimeMillis() - startTime);
            recordCommand(false, result.getDuration(), false);
            return Result.success(result);
        }

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
            long duration = System.currentTimeMillis() - startTime;

            result.setSuccess(exitCode == 0);
            result.setOutput(output.toString());
            result.setExitCode(exitCode);
            result.setDuration(duration);

            if (exitCode != 0) {
                result.setErrorCode("EXIT_CODE_" + exitCode);
                result.setErrorMessage("Command exited with non-zero code");
            }

            recordCommand(exitCode == 0, duration, false);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            result.setSuccess(false);
            result.setOutput("");
            result.setErrorCode("EXECUTION_ERROR");
            result.setErrorMessage(e.getMessage());
            result.setDuration(duration);
            recordCommand(false, duration, false);
        }

        return Result.success(result);
    }

    private void recordCommand(boolean success, long latency, boolean timeout) {
        totalCommands.incrementAndGet();
        if (success) {
            successCommands.incrementAndGet();
        } else {
            failedCount.incrementAndGet();
        }
        if (timeout) {
            timeoutCount.incrementAndGet();
        }

        totalLatency.addAndGet(latency);
        updateMaxLatency(latency);
        updateMinLatency(latency);
    }

    private synchronized void updateMaxLatency(long latency) {
        if (latency > maxLatency.get()) {
            maxLatency.set(latency);
        }
    }

    private synchronized void updateMinLatency(long latency) {
        if (latency < minLatency.get()) {
            minLatency.set(latency);
        }
    }

    private long calculateAvgLatency() {
        long total = totalCommands.get();
        return total > 0 ? totalLatency.get() / total : 0;
    }
}
