package net.ooder.sdk.story;

import java.util.*;
import java.util.concurrent.*;

class StoryContextImpl implements StoryContext {
    
    private final String contextId;
    private final String storyId;
    private final Map<String, Object> variables = new ConcurrentHashMap<>();
    private final Map<String, Object> environment = new ConcurrentHashMap<>();
    private final List<String> availableCapabilities = new CopyOnWriteArrayList<>();
    private final Map<String, Object> assetSummary = new ConcurrentHashMap<>();
    private final List<ExecutionRecord> executionHistory = new CopyOnWriteArrayList<>();
    private String currentStepId;
    
    StoryContextImpl(String storyId) {
        this.contextId = "ctx-" + System.currentTimeMillis();
        this.storyId = storyId;
    }
    
    @Override public String getContextId() { return contextId; }
    @Override public String getStoryId() { return storyId; }
    @Override public Map<String, Object> getVariables() { return variables; }
    @Override public void setVariable(String key, Object value) { variables.put(key, value); }
    @Override public Object getVariable(String key) { return variables.get(key); }
    @Override public Map<String, Object> getEnvironment() { return environment; }
    @Override public List<String> getAvailableCapabilities() { return availableCapabilities; }
    @Override public Map<String, Object> getAssetSummary() { return assetSummary; }
    @Override public List<ExecutionRecord> getExecutionHistory() { return executionHistory; }
    @Override public void addExecutionRecord(ExecutionRecord record) { executionHistory.add(record); }
    @Override public String getCurrentStepId() { return currentStepId; }
    @Override public void setCurrentStepId(String stepId) { this.currentStepId = stepId; }
}
