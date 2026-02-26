package net.ooder.sdk.llm.scheduling.model;

public class ResourceAllocation {
    private Integer allocatedCpuCores;
    private Integer allocatedMemoryMB;
    private Integer allocatedGpuCount;
    private Long allocatedStorageMB;

    public ResourceAllocation() {
    }

    public Integer getAllocatedCpuCores() {
        return allocatedCpuCores;
    }

    public void setAllocatedCpuCores(Integer allocatedCpuCores) {
        this.allocatedCpuCores = allocatedCpuCores;
    }

    public Integer getAllocatedMemoryMB() {
        return allocatedMemoryMB;
    }

    public void setAllocatedMemoryMB(Integer allocatedMemoryMB) {
        this.allocatedMemoryMB = allocatedMemoryMB;
    }

    public Integer getAllocatedGpuCount() {
        return allocatedGpuCount;
    }

    public void setAllocatedGpuCount(Integer allocatedGpuCount) {
        this.allocatedGpuCount = allocatedGpuCount;
    }

    public Long getAllocatedStorageMB() {
        return allocatedStorageMB;
    }

    public void setAllocatedStorageMB(Long allocatedStorageMB) {
        this.allocatedStorageMB = allocatedStorageMB;
    }
}
