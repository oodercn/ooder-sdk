package net.ooder.sdk.llm.memory.model;

import java.util.List;

public class ShareRequest {
    private String sourceAgentId;
    private List<String> targetAgentIds;
    private String memoryCategory;
    private Boolean readOnly;
    private Long expireDurationMs;

    public ShareRequest() {
    }

    public String getSourceAgentId() {
        return sourceAgentId;
    }

    public void setSourceAgentId(String sourceAgentId) {
        this.sourceAgentId = sourceAgentId;
    }

    public List<String> getTargetAgentIds() {
        return targetAgentIds;
    }

    public void setTargetAgentIds(List<String> targetAgentIds) {
        this.targetAgentIds = targetAgentIds;
    }

    public String getMemoryCategory() {
        return memoryCategory;
    }

    public void setMemoryCategory(String memoryCategory) {
        this.memoryCategory = memoryCategory;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Long getExpireDurationMs() {
        return expireDurationMs;
    }

    public void setExpireDurationMs(Long expireDurationMs) {
        this.expireDurationMs = expireDurationMs;
    }
}
