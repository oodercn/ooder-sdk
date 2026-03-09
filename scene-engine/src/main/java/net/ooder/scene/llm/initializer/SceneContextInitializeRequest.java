package net.ooder.scene.llm.initializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景上下文初始化请求
 * 
 * <p>封装初始化 LlmSceneContext 所需的所有参数。</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
public class SceneContextInitializeRequest {
    
    private String agentId;
    private String sandboxId;
    private String userId;
    private String userName;
    private String domainId;
    private List<String> userRoles;
    private List<String> userPermissions;
    
    // NLP相关
    private String componentType;
    private String moduleViewType;
    private Map<String, Object> nlpConfig;
    
    // 知识库相关
    private String knowledgeBaseId;
    private List<String> accessibleKnowledgeBases;
    private Map<String, Object> searchFilters;
    
    // 安全相关
    private String securityLevel;
    private String sessionId;
    private String traceId;
    private String ipAddress;
    private String userAgent;
    
    // 扩展属性
    private Map<String, Object> extendedAttributes;
    
    // 超时配置
    private Long ttlMillis;
    private Long idleTimeoutMillis;
    
    public SceneContextInitializeRequest() {
        this.extendedAttributes = new HashMap<>();
        this.nlpConfig = new HashMap<>();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public void addExtendedAttribute(String key, Object value) {
        if (extendedAttributes == null) {
            extendedAttributes = new HashMap<>();
        }
        extendedAttributes.put(key, value);
    }
    
    public void addNlpConfig(String key, Object value) {
        if (nlpConfig == null) {
            nlpConfig = new HashMap<>();
        }
        nlpConfig.put(key, value);
    }
    
    // Getters and Setters
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getSandboxId() { return sandboxId; }
    public void setSandboxId(String sandboxId) { this.sandboxId = sandboxId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getDomainId() { return domainId; }
    public void setDomainId(String domainId) { this.domainId = domainId; }
    
    public List<String> getUserRoles() { return userRoles; }
    public void setUserRoles(List<String> userRoles) { this.userRoles = userRoles; }
    
    public List<String> getUserPermissions() { return userPermissions; }
    public void setUserPermissions(List<String> userPermissions) { this.userPermissions = userPermissions; }
    
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
    
    public String getModuleViewType() { return moduleViewType; }
    public void setModuleViewType(String moduleViewType) { this.moduleViewType = moduleViewType; }
    
    public Map<String, Object> getNlpConfig() { return nlpConfig; }
    public void setNlpConfig(Map<String, Object> nlpConfig) { this.nlpConfig = nlpConfig; }
    
    public String getKnowledgeBaseId() { return knowledgeBaseId; }
    public void setKnowledgeBaseId(String knowledgeBaseId) { this.knowledgeBaseId = knowledgeBaseId; }
    
    public List<String> getAccessibleKnowledgeBases() { return accessibleKnowledgeBases; }
    public void setAccessibleKnowledgeBases(List<String> accessibleKnowledgeBases) { this.accessibleKnowledgeBases = accessibleKnowledgeBases; }
    
    public Map<String, Object> getSearchFilters() { return searchFilters; }
    public void setSearchFilters(Map<String, Object> searchFilters) { this.searchFilters = searchFilters; }
    
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public Map<String, Object> getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) { this.extendedAttributes = extendedAttributes; }
    
    public Long getTtlMillis() { return ttlMillis; }
    public void setTtlMillis(Long ttlMillis) { this.ttlMillis = ttlMillis; }
    
    public Long getIdleTimeoutMillis() { return idleTimeoutMillis; }
    public void setIdleTimeoutMillis(Long idleTimeoutMillis) { this.idleTimeoutMillis = idleTimeoutMillis; }
    
    public static class Builder {
        private SceneContextInitializeRequest request = new SceneContextInitializeRequest();
        
        public Builder agentId(String agentId) {
            request.setAgentId(agentId);
            return this;
        }
        
        public Builder sandboxId(String sandboxId) {
            request.setSandboxId(sandboxId);
            return this;
        }
        
        public Builder userId(String userId) {
            request.setUserId(userId);
            return this;
        }
        
        public Builder userName(String userName) {
            request.setUserName(userName);
            return this;
        }
        
        public Builder domainId(String domainId) {
            request.setDomainId(domainId);
            return this;
        }
        
        public Builder userRoles(List<String> roles) {
            request.setUserRoles(roles);
            return this;
        }
        
        public Builder userPermissions(List<String> permissions) {
            request.setUserPermissions(permissions);
            return this;
        }
        
        public Builder componentType(String componentType) {
            request.setComponentType(componentType);
            return this;
        }
        
        public Builder moduleViewType(String moduleViewType) {
            request.setModuleViewType(moduleViewType);
            return this;
        }
        
        public Builder nlpConfig(String key, Object value) {
            request.addNlpConfig(key, value);
            return this;
        }
        
        public Builder knowledgeBaseId(String knowledgeBaseId) {
            request.setKnowledgeBaseId(knowledgeBaseId);
            return this;
        }
        
        public Builder accessibleKnowledgeBases(List<String> kbIds) {
            request.setAccessibleKnowledgeBases(kbIds);
            return this;
        }
        
        public Builder securityLevel(String securityLevel) {
            request.setSecurityLevel(securityLevel);
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            request.setSessionId(sessionId);
            return this;
        }
        
        public Builder traceId(String traceId) {
            request.setTraceId(traceId);
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            request.setIpAddress(ipAddress);
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            request.setUserAgent(userAgent);
            return this;
        }
        
        public Builder extendedAttribute(String key, Object value) {
            request.addExtendedAttribute(key, value);
            return this;
        }
        
        public Builder ttlMillis(long ttlMillis) {
            request.setTtlMillis(ttlMillis);
            return this;
        }
        
        public Builder idleTimeoutMillis(long idleTimeoutMillis) {
            request.setIdleTimeoutMillis(idleTimeoutMillis);
            return this;
        }
        
        public SceneContextInitializeRequest build() {
            return request;
        }
    }
}
