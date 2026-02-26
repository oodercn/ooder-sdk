package net.ooder.sdk.llm.capability.model;

import net.ooder.sdk.llm.common.enums.ResourceType;

public class ResourceRequirement {
    private ResourceType resourceType;
    private Integer cpuCores;
    private Integer memoryMB;
    private Integer gpuCount;
    private Long durationMs;

    public ResourceRequirement() {
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Integer getMemoryMB() {
        return memoryMB;
    }

    public void setMemoryMB(Integer memoryMB) {
        this.memoryMB = memoryMB;
    }

    public Integer getGpuCount() {
        return gpuCount;
    }

    public void setGpuCount(Integer gpuCount) {
        this.gpuCount = gpuCount;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}
