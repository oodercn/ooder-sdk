package net.ooder.sdk.llm.capability.model;

import java.time.Instant;
import java.util.Map;

public class CapabilityResponse {
    private String capabilityId;
    private String accessToken;
    private Instant expireTime;
    private LlmEndpoint endpoint;
    private Map<String, Object> metadata;

    public CapabilityResponse() {
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Instant expireTime) {
        this.expireTime = expireTime;
    }

    public LlmEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(LlmEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
