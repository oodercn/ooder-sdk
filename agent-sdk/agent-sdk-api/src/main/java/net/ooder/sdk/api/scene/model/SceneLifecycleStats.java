package net.ooder.sdk.api.scene.model;

public class SceneLifecycleStats {
    private String sceneId;
    private SceneState state;
    private long createdTime;
    private long startedTime;
    private long lastStateChange;
    private long totalUptime;
    private int restartCount;
    private int errorCount;
    private String lastError;
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    
    public SceneState getState() { return state; }
    public void setState(SceneState state) { this.state = state; }
    
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    
    public long getStartedTime() { return startedTime; }
    public void setStartedTime(long startedTime) { this.startedTime = startedTime; }
    
    public long getLastStateChange() { return lastStateChange; }
    public void setLastStateChange(long lastStateChange) { this.lastStateChange = lastStateChange; }
    
    public long getTotalUptime() { return totalUptime; }
    public void setTotalUptime(long totalUptime) { this.totalUptime = totalUptime; }
    
    public int getRestartCount() { return restartCount; }
    public void setRestartCount(int restartCount) { this.restartCount = restartCount; }
    
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
}
