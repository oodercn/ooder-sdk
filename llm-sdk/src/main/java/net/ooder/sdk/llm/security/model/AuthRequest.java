package net.ooder.sdk.llm.security.model;

import net.ooder.sdk.llm.common.enums.AuthType;

import java.util.Map;

public class AuthRequest {
    private String agentId;
    private AuthType authType;
    private String credential;
    private Map<String, Object> metadata;

    public AuthRequest() {
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
