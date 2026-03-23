package net.ooder.scene.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExecutionContext {

    private String executionId;
    private String sceneGroupId;
    private String agentId;
    private String capabilityId;
    private long startTime;
    private Map<String, Object> parameters;
    private ExecutionState state;

    public ExecutionContext() {
        this.executionId = UUID.randomUUID().toString().replace("-", "");
        this.startTime = System.currentTimeMillis();
        this.parameters = new HashMap<>();
        this.state = ExecutionState.PENDING;
    }

    public ExecutionContext(String sceneGroupId, String agentId, String capabilityId) {
        this();
        this.sceneGroupId = sceneGroupId;
        this.agentId = agentId;
        this.capabilityId = capabilityId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) this.parameters.get(key);
    }

    public ExecutionState getState() {
        return state;
    }

    public void setState(ExecutionState state) {
        this.state = state;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public String toString() {
        return "ExecutionContext{" +
                "executionId='" + executionId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", capabilityId='" + capabilityId + '\'' +
                ", state=" + state +
                ", elapsedTime=" + getElapsedTime() + "ms" +
                '}';
    }
}
