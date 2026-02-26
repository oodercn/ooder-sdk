package net.ooder.sdk.api.security;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SecurityApi {
    
    AuthenticationResult authenticate(AuthenticationRequest request);
    
    CompletableFuture<AuthenticationResult> authenticateAsync(AuthenticationRequest request);
    
    AuthorizationResult authorize(AuthorizationRequest request);
    
    boolean hasPermission(String userId, String permission);
    
    boolean hasRole(String userId, String role);
    
    void grantPermission(String userId, String permission);
    
    void revokePermission(String userId, String permission);
    
    void grantRole(String userId, String role);
    
    void revokeRole(String userId, String role);
    
    String createSession(String userId, Map<String, Object> claims);
    
    Optional<SessionInfo> getSession(String sessionId);
    
    void invalidateSession(String sessionId);
    
    void refreshSession(String sessionId);
    
    String generateToken(String userId, TokenType type, long expirySeconds);
    
    TokenInfo validateToken(String token);
    
    void invalidateToken(String token);
    
    AuditLog getAuditLog(String userId, int limit);
    
    void logSecurityEvent(SecurityEvent event);
    
    List<SecurityPolicy> getPolicies();
    
    void createPolicy(SecurityPolicy policy);
    
    void updatePolicy(String policyId, SecurityPolicy policy);
    
    void deletePolicy(String policyId);
    
    class AuthenticationRequest {
        private String userId;
        private String password;
        private String authType;
        private Map<String, Object> credentials;
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getAuthType() { return authType; }
        public void setAuthType(String authType) { this.authType = authType; }
        
        public Map<String, Object> getCredentials() { return credentials; }
        public void setCredentials(Map<String, Object> credentials) { this.credentials = credentials; }
    }
    
    class AuthenticationResult {
        private boolean success;
        private String userId;
        private String sessionId;
        private String token;
        private String message;
        private Instant expiresAt;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    }
    
    class AuthorizationRequest {
        private String userId;
        private String resource;
        private String action;
        private Map<String, Object> context;
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }
    
    class AuthorizationResult {
        private boolean allowed;
        private String reason;
        private List<String> matchedPolicies;
        
        public boolean isAllowed() { return allowed; }
        public void setAllowed(boolean allowed) { this.allowed = allowed; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public List<String> getMatchedPolicies() { return matchedPolicies; }
        public void setMatchedPolicies(List<String> matchedPolicies) { this.matchedPolicies = matchedPolicies; }
    }
    
    class SessionInfo {
        private String sessionId;
        private String userId;
        private Instant createdAt;
        private Instant expiresAt;
        private Map<String, Object> claims;
        private boolean valid;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        
        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
        
        public Map<String, Object> getClaims() { return claims; }
        public void setClaims(Map<String, Object> claims) { this.claims = claims; }
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
    }
    
    enum TokenType {
        ACCESS,
        REFRESH,
        API_KEY
    }
    
    class SecurityEvent {
        private String eventId;
        private String userId;
        private String eventType;
        private String resource;
        private String action;
        private String result;
        private String ipAddress;
        private Instant timestamp;
        private Map<String, Object> metadata;
        
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    class AuditLog {
        private List<SecurityEvent> events;
        private int totalCount;
        private Instant startTime;
        private Instant endTime;
        
        public List<SecurityEvent> getEvents() { return events; }
        public void setEvents(List<SecurityEvent> events) { this.events = events; }
        
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
    }
    
    class SecurityPolicy {
        private String policyId;
        private String name;
        private String description;
        private String resource;
        private String action;
        private List<String> allowedRoles;
        private Map<String, Object> conditions;
        private boolean enabled;
        
        public String getPolicyId() { return policyId; }
        public void setPolicyId(String policyId) { this.policyId = policyId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public List<String> getAllowedRoles() { return allowedRoles; }
        public void setAllowedRoles(List<String> allowedRoles) { this.allowedRoles = allowedRoles; }
        
        public Map<String, Object> getConditions() { return conditions; }
        public void setConditions(Map<String, Object> conditions) { this.conditions = conditions; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
}
