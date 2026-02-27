package net.ooder.sdk.llm.scheduling.model;

public class ScaleRequest {
    private String resourcePoolId;
    private String scaleType;
    private Integer targetCount;
    private Integer targetCpuCores;
    private Integer targetMemoryMB;
    private Integer targetGpuCount;

    public ScaleRequest() {
    }

    public String getResourcePoolId() {
        return resourcePoolId;
    }

    public void setResourcePoolId(String resourcePoolId) {
        this.resourcePoolId = resourcePoolId;
    }

    public String getScaleType() {
        return scaleType;
    }

    public void setScaleType(String scaleType) {
        this.scaleType = scaleType;
    }

    public Integer getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(Integer targetCount) {
        this.targetCount = targetCount;
    }

    public Integer getTargetCpuCores() {
        return targetCpuCores;
    }

    public void setTargetCpuCores(Integer targetCpuCores) {
        this.targetCpuCores = targetCpuCores;
    }

    public Integer getTargetMemoryMB() {
        return targetMemoryMB;
    }

    public void setTargetMemoryMB(Integer targetMemoryMB) {
        this.targetMemoryMB = targetMemoryMB;
    }

    public Integer getTargetGpuCount() {
        return targetGpuCount;
    }

    public void setTargetGpuCount(Integer targetGpuCount) {
        this.targetGpuCount = targetGpuCount;
    }
}
