package net.ooder.sdk.llm.capability.model;

import java.time.Instant;

public class CapabilityStatus {
    private String capabilityId;
    private String status;
    private Instant createTime;
    private Instant lastUsedTime;
    private Long usageCount;
    private Long remainingQuota;

    public CapabilityStatus() {
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(Instant lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public Long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Long usageCount) {
        this.usageCount = usageCount;
    }

    public Long getRemainingQuota() {
        return remainingQuota;
    }

    public void setRemainingQuota(Long remainingQuota) {
        this.remainingQuota = remainingQuota;
    }
}
