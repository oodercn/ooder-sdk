package net.ooder.sdk.api.execution;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskExecutionContext {

    private String executionId;
    private String sceneId;
    private String sceneGroupId;
    private String skillId;
    private String capabilityId;
    private String agentId;
    private String userId;
    private String sessionId;
    private ExecutionPhase phase;
    private ExecutionState state;
    private Map<String, Object> parameters;
    private Map<String, Object> variables;
    private Map<String, Object> context;
    private Instant startTime;
    private Instant endTime;
    private long timeout;
    private String parentExecutionId;
    private int retryCount;
    private int maxRetries;

    public enum ExecutionPhase {
        INITIALIZATION,
        VALIDATION,
        EXECUTION,
        POST_PROCESSING,
        CLEANUP
    }

    public enum ExecutionState {
        CREATED,
        PENDING,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED,
        TIMEOUT,
        CANCELLED
    }

    public TaskExecutionContext() {
        this.executionId = UUID.randomUUID().toString();
        this.phase = ExecutionPhase.INITIALIZATION;
        this.state = ExecutionState.CREATED;
        this.parameters = new HashMap<>();
        this.variables = new HashMap<>();
        this.context = new HashMap<>();
        this.startTime = Instant.now();
        this.retryCount = 0;
        this.maxRetries = 3;
    }

    public TaskExecutionContext(String sceneId, String skillId) {
        this();
        this.sceneId = sceneId;
        this.skillId = skillId;
    }

    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public ExecutionPhase getPhase() { return phase; }
    public void setPhase(ExecutionPhase phase) { this.phase = phase; }

    public ExecutionState getState() { return state; }
    public void setState(ExecutionState state) { this.state = state; }

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters != null ? parameters : new HashMap<>(); }

    public Map<String, Object> getVariables() { return variables; }
    public void setVariables(Map<String, Object> variables) { this.variables = variables != null ? variables : new HashMap<>(); }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context != null ? context : new HashMap<>(); }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }

    public String getParentExecutionId() { return parentExecutionId; }
    public void setParentExecutionId(String parentExecutionId) { this.parentExecutionId = parentExecutionId; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) this.parameters.get(key);
    }

    public void setVariable(String key, Object value) {
        this.variables.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getVariable(String key) {
        return (T) this.variables.get(key);
    }

    public void setContextValue(String key, Object value) {
        this.context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContextValue(String key) {
        return (T) this.context.get(key);
    }

    public long getDuration() {
        if (endTime != null && startTime != null) {
            return endTime.toEpochMilli() - startTime.toEpochMilli();
        }
        if (startTime != null) {
            return Instant.now().toEpochMilli() - startTime.toEpochMilli();
        }
        return 0;
    }

    public boolean isRunning() {
        return state == ExecutionState.RUNNING;
    }

    public boolean isCompleted() {
        return state == ExecutionState.COMPLETED;
    }

    public boolean isFailed() {
        return state == ExecutionState.FAILED;
    }

    public boolean isTerminal() {
        return state == ExecutionState.COMPLETED || 
               state == ExecutionState.FAILED || 
               state == ExecutionState.TIMEOUT || 
               state == ExecutionState.CANCELLED;
    }

    public boolean canRetry() {
        return retryCount < maxRetries && 
               (state == ExecutionState.FAILED || state == ExecutionState.TIMEOUT);
    }

    public void markRunning() {
        this.state = ExecutionState.RUNNING;
        this.phase = ExecutionPhase.EXECUTION;
    }

    public void markCompleted() {
        this.state = ExecutionState.COMPLETED;
        this.endTime = Instant.now();
    }

    public void markFailed() {
        this.state = ExecutionState.FAILED;
        this.endTime = Instant.now();
    }

    public void markTimeout() {
        this.state = ExecutionState.TIMEOUT;
        this.endTime = Instant.now();
    }

    public void markCancelled() {
        this.state = ExecutionState.CANCELLED;
        this.endTime = Instant.now();
    }

    public void incrementRetry() {
        this.retryCount++;
    }
}
