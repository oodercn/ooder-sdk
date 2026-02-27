package net.ooder.sdk.llm.memory.model;

import java.time.Instant;
import java.util.List;

public class ShareResult {
    private String shareId;
    private String status;
    private List<String> sharedAgentIds;
    private Instant expireTime;
    private String message;

    public ShareResult() {
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSharedAgentIds() {
        return sharedAgentIds;
    }

    public void setSharedAgentIds(List<String> sharedAgentIds) {
        this.sharedAgentIds = sharedAgentIds;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Instant expireTime) {
        this.expireTime = expireTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
