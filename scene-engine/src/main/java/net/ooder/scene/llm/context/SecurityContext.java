package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全上下文
 * 
 * <p>封装安全相关信息，集成现有 SecurityInterceptor 机制。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class SecurityContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String securityLevel;
    private String sessionId;
    private String traceId;
    private boolean auditEnabled;
    private List<String> allowedOperations;
    private Map<String, String> securityLabels;
    
    private String ipAddress;
    private String userAgent;

    public SecurityContext() {
        this.allowedOperations = new ArrayList<>();
        this.securityLabels = new HashMap<>();
        this.auditEnabled = true;
    }
    
    public SecurityContext(String sessionId, String traceId) {
        this();
        this.sessionId = sessionId;
        this.traceId = traceId;
    }
    
    public boolean isOperationAllowed(String operation) {
        return allowedOperations == null || allowedOperations.isEmpty() || 
               allowedOperations.contains(operation);
    }
    
    public void allowOperation(String operation) {
        if (allowedOperations == null) {
            allowedOperations = new ArrayList<>();
        }
        if (!allowedOperations.contains(operation)) {
            allowedOperations.add(operation);
        }
    }
    
    public void denyOperation(String operation) {
        if (allowedOperations != null) {
            allowedOperations.remove(operation);
        }
    }
    
    public void setSecurityLabel(String key, String value) {
        if (securityLabels == null) {
            securityLabels = new HashMap<>();
        }
        securityLabels.put(key, value);
    }
    
    public String getSecurityLabel(String key) {
        return securityLabels != null ? securityLabels.get(key) : null;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public boolean isAuditEnabled() { return auditEnabled; }
    public void setAuditEnabled(boolean auditEnabled) { this.auditEnabled = auditEnabled; }
    
    public List<String> getAllowedOperations() { return allowedOperations; }
    public void setAllowedOperations(List<String> allowedOperations) { this.allowedOperations = allowedOperations; }
    
    public Map<String, String> getSecurityLabels() { return securityLabels; }
    public void setSecurityLabels(Map<String, String> securityLabels) { this.securityLabels = securityLabels; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public static class Builder {
        private SecurityContext context = new SecurityContext();
        
        public Builder securityLevel(String securityLevel) {
            context.setSecurityLevel(securityLevel);
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            context.setSessionId(sessionId);
            return this;
        }
        
        public Builder traceId(String traceId) {
            context.setTraceId(traceId);
            return this;
        }
        
        public Builder auditEnabled(boolean auditEnabled) {
            context.setAuditEnabled(auditEnabled);
            return this;
        }
        
        public Builder allowedOperation(String operation) {
            context.allowOperation(operation);
            return this;
        }
        
        public Builder securityLabel(String key, String value) {
            context.setSecurityLabel(key, value);
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            context.setIpAddress(ipAddress);
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            context.setUserAgent(userAgent);
            return this;
        }
        
        public SecurityContext build() {
            return context;
        }
    }
}
