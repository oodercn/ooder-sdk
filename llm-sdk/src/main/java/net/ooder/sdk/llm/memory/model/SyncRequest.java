package net.ooder.sdk.llm.memory.model;

import net.ooder.sdk.llm.common.enums.MemoryType;
import net.ooder.sdk.llm.common.enums.SyncMode;

import java.time.Duration;
import java.util.List;

public class SyncRequest {
    private String sourceAgentId;
    private String targetAgentId;
    private List<MemoryType> memoryTypes;
    private SyncMode syncMode;
    private Duration syncInterval;
    private Boolean includeContext;

    public SyncRequest() {
    }

    public String getSourceAgentId() {
        return sourceAgentId;
    }

    public void setSourceAgentId(String sourceAgentId) {
        this.sourceAgentId = sourceAgentId;
    }

    public String getTargetAgentId() {
        return targetAgentId;
    }

    public void setTargetAgentId(String targetAgentId) {
        this.targetAgentId = targetAgentId;
    }

    public List<MemoryType> getMemoryTypes() {
        return memoryTypes;
    }

    public void setMemoryTypes(List<MemoryType> memoryTypes) {
        this.memoryTypes = memoryTypes;
    }

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
    }

    public Duration getSyncInterval() {
        return syncInterval;
    }

    public void setSyncInterval(Duration syncInterval) {
        this.syncInterval = syncInterval;
    }

    public Boolean getIncludeContext() {
        return includeContext;
    }

    public void setIncludeContext(Boolean includeContext) {
        this.includeContext = includeContext;
    }
}
