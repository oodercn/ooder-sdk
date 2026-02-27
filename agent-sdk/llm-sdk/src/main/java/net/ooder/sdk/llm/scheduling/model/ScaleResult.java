package net.ooder.sdk.llm.scheduling.model;

import java.time.Instant;

public class ScaleResult {
    private String scaleId;
    private String status;
    private Integer previousCount;
    private Integer currentCount;
    private Instant completionTime;
    private String message;

    public ScaleResult() {
    }

    public String getScaleId() {
        return scaleId;
    }

    public void setScaleId(String scaleId) {
        this.scaleId = scaleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPreviousCount() {
        return previousCount;
    }

    public void setPreviousCount(Integer previousCount) {
        this.previousCount = previousCount;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public Instant getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Instant completionTime) {
        this.completionTime = completionTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
