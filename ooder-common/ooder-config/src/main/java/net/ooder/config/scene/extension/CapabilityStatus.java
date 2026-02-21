package net.ooder.config.scene.extension;

import java.io.Serializable;

public class CapabilityStatus implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_INITIALIZING = "INITIALIZING";
    
    private String capabilityCode;
    private String status = STATUS_INACTIVE;
    private long lastInvokeTime;
    private int invokeCount;
    private int errorCount;
    private String errorMessage;
    private long createdTime;
    private long updatedTime;
    
    public CapabilityStatus() {
        this.createdTime = System.currentTimeMillis();
        this.updatedTime = this.createdTime;
    }
    
    public CapabilityStatus(String capabilityCode) {
        this();
        this.capabilityCode = capabilityCode;
    }
    
    public String getCapabilityCode() {
        return capabilityCode;
    }
    
    public void setCapabilityCode(String capabilityCode) {
        this.capabilityCode = capabilityCode;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updatedTime = System.currentTimeMillis();
    }
    
    public long getLastInvokeTime() {
        return lastInvokeTime;
    }
    
    public void setLastInvokeTime(long lastInvokeTime) {
        this.lastInvokeTime = lastInvokeTime;
    }
    
    public int getInvokeCount() {
        return invokeCount;
    }
    
    public void setInvokeCount(int invokeCount) {
        this.invokeCount = invokeCount;
    }
    
    public void incrementInvokeCount() {
        this.invokeCount++;
        this.lastInvokeTime = System.currentTimeMillis();
        this.updatedTime = this.lastInvokeTime;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
    
    public void incrementErrorCount() {
        this.errorCount++;
        this.updatedTime = System.currentTimeMillis();
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.updatedTime = System.currentTimeMillis();
    }
    
    public long getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
    
    public long getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
    
    public boolean isError() {
        return STATUS_ERROR.equals(status);
    }
    
    public void markActive() {
        setStatus(STATUS_ACTIVE);
        this.errorMessage = null;
    }
    
    public void markError(String errorMessage) {
        setStatus(STATUS_ERROR);
        this.errorMessage = errorMessage;
        incrementErrorCount();
    }
    
    public void markInactive() {
        setStatus(STATUS_INACTIVE);
    }
    
    public void markInitializing() {
        setStatus(STATUS_INITIALIZING);
    }
    
    public void reset() {
        this.status = STATUS_INACTIVE;
        this.lastInvokeTime = 0;
        this.invokeCount = 0;
        this.errorCount = 0;
        this.errorMessage = null;
        this.updatedTime = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return "CapabilityStatus{" +
            "capabilityCode='" + capabilityCode + '\'' +
            ", status='" + status + '\'' +
            ", invokeCount=" + invokeCount +
            ", errorCount=" + errorCount +
            '}';
    }
}
