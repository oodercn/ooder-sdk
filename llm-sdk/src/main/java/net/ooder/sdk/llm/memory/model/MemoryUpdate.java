package net.ooder.sdk.llm.memory.model;

import net.ooder.sdk.llm.common.enums.MemoryType;

import java.util.Map;

public class MemoryUpdate {
    private String memoryId;
    private String agentId;
    private MemoryType memoryType;
    private String content;
    private Map<String, Object> metadata;
    private String updateOperation;

    public MemoryUpdate() {
    }

    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public MemoryType getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(MemoryType memoryType) {
        this.memoryType = memoryType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getUpdateOperation() {
        return updateOperation;
    }

    public void setUpdateOperation(String updateOperation) {
        this.updateOperation = updateOperation;
    }
}
