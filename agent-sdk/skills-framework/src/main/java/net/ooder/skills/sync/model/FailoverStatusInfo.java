package net.ooder.skills.sync.model;

public class FailoverStatusInfo {
    
    private String sceneGroupId;
    private boolean inProgress;
    private String failedAgentId;
    private String newPrimaryId;
    private long startTime;
    private String phase;
    
    public static final String PHASE_DETECTING = "detecting";
    public static final String PHASE_ELECTING = "electing";
    public static final String PHASE_SWITCHING = "switching";
    public static final String PHASE_COMPLETED = "completed";
    
    public FailoverStatusInfo() {
        this.inProgress = false;
    }
    
    private FailoverStatusInfo(Builder builder) {
        this.sceneGroupId = builder.sceneGroupId;
        this.inProgress = builder.inProgress;
        this.failedAgentId = builder.failedAgentId;
        this.newPrimaryId = builder.newPrimaryId;
        this.startTime = builder.startTime;
        this.phase = builder.phase;
    }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public boolean isInProgress() { return inProgress; }
    public void setInProgress(boolean inProgress) { this.inProgress = inProgress; }
    
    public String getFailedAgentId() { return failedAgentId; }
    public void setFailedAgentId(String failedAgentId) { this.failedAgentId = failedAgentId; }
    
    public String getNewPrimaryId() { return newPrimaryId; }
    public void setNewPrimaryId(String newPrimaryId) { this.newPrimaryId = newPrimaryId; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    
    public long getDuration() {
        if (startTime > 0) {
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }
    
    public boolean isCompleted() {
        return PHASE_COMPLETED.equals(phase);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String sceneGroupId;
        private boolean inProgress;
        private String failedAgentId;
        private String newPrimaryId;
        private long startTime;
        private String phase;
        
        public Builder sceneGroupId(String sceneGroupId) {
            this.sceneGroupId = sceneGroupId;
            return this;
        }
        
        public Builder inProgress(boolean inProgress) {
            this.inProgress = inProgress;
            return this;
        }
        
        public Builder failedAgentId(String failedAgentId) {
            this.failedAgentId = failedAgentId;
            return this;
        }
        
        public Builder newPrimaryId(String newPrimaryId) {
            this.newPrimaryId = newPrimaryId;
            return this;
        }
        
        public Builder startTime(long startTime) {
            this.startTime = startTime;
            return this;
        }
        
        public Builder phase(String phase) {
            this.phase = phase;
            return this;
        }
        
        public FailoverStatusInfo build() {
            return new FailoverStatusInfo(this);
        }
    }
}
