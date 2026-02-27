package net.ooder.sdk.llm.security.model;

import java.util.List;

public class AuthorizeResult {
    private String agentId;
    private boolean authorized;
    private List<String> grantedPermissions;
    private List<String> deniedPermissions;
    private String message;

    public AuthorizeResult() {
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public List<String> getGrantedPermissions() {
        return grantedPermissions;
    }

    public void setGrantedPermissions(List<String> grantedPermissions) {
        this.grantedPermissions = grantedPermissions;
    }

    public List<String> getDeniedPermissions() {
        return deniedPermissions;
    }

    public void setDeniedPermissions(List<String> deniedPermissions) {
        this.deniedPermissions = deniedPermissions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
