package net.ooder.scene.llm.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM 场景上下文
 * 
 * <p>封装 LLM 在特定场景中所需的所有上下文信息，支持 A2A 传递。</p>
 * 
 * <p>设计原则：</p>
 * <ul>
 *   <li>复用现有上下文：继承 DecisionContext 的设计理念</li>
 *   <li>支持序列化：实现 Serializable 用于 A2A 传递</li>
 *   <li>安全隔离：包含 SecurityContext 用于安全验证</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class LlmSceneContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String contextId;
    private String sceneId;
    private String agentId;
    private String sandboxId;
    
    private UserContext userContext;
    private NlpContext nlpContext;
    private KnowledgeContext knowledgeContext;
    private SecurityContext securityContext;
    
    private Map<String, Object> extendedAttributes;
    private long createdAt;
    private long lastAccessedAt;
    
    public LlmSceneContext() {
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = this.createdAt;
        this.extendedAttributes = new HashMap<>();
    }
    
    public LlmSceneContext(String sceneId, String agentId) {
        this();
        this.contextId = generateContextId();
        this.sceneId = sceneId;
        this.agentId = agentId;
    }
    
    public void touch() {
        this.lastAccessedAt = System.currentTimeMillis();
    }
    
    public Object getExtendedAttribute(String key) {
        return extendedAttributes != null ? extendedAttributes.get(key) : null;
    }
    
    public void setExtendedAttribute(String key, Object value) {
        if (extendedAttributes == null) {
            extendedAttributes = new HashMap<>();
        }
        extendedAttributes.put(key, value);
    }
    
    public void removeExtendedAttribute(String key) {
        if (extendedAttributes != null) {
            extendedAttributes.remove(key);
        }
    }
    
    private String generateContextId() {
        return "ctx-" + Long.toHexString(System.currentTimeMillis()) + "-" + 
               Integer.toHexString(hashCode());
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getContextId() { return contextId; }
    public void setContextId(String contextId) { this.contextId = contextId; }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getSandboxId() { return sandboxId; }
    public void setSandboxId(String sandboxId) { this.sandboxId = sandboxId; }
    
    public UserContext getUserContext() { return userContext; }
    public void setUserContext(UserContext userContext) { this.userContext = userContext; }
    
    public NlpContext getNlpContext() { return nlpContext; }
    public void setNlpContext(NlpContext nlpContext) { this.nlpContext = nlpContext; }
    
    public KnowledgeContext getKnowledgeContext() { return knowledgeContext; }
    public void setKnowledgeContext(KnowledgeContext knowledgeContext) { this.knowledgeContext = knowledgeContext; }
    
    public SecurityContext getSecurityContext() { return securityContext; }
    public void setSecurityContext(SecurityContext securityContext) { this.securityContext = securityContext; }
    
    public Map<String, Object> getExtendedAttributes() { return extendedAttributes; }
    public void setExtendedAttributes(Map<String, Object> extendedAttributes) { this.extendedAttributes = extendedAttributes; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(long lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    
    public static class Builder {
        private LlmSceneContext context = new LlmSceneContext();
        
        public Builder contextId(String contextId) {
            context.setContextId(contextId);
            return this;
        }
        
        public Builder sceneId(String sceneId) {
            context.setSceneId(sceneId);
            return this;
        }
        
        public Builder agentId(String agentId) {
            context.setAgentId(agentId);
            return this;
        }
        
        public Builder sandboxId(String sandboxId) {
            context.setSandboxId(sandboxId);
            return this;
        }
        
        public Builder userContext(UserContext userContext) {
            context.setUserContext(userContext);
            return this;
        }
        
        public Builder nlpContext(NlpContext nlpContext) {
            context.setNlpContext(nlpContext);
            return this;
        }
        
        public Builder knowledgeContext(KnowledgeContext knowledgeContext) {
            context.setKnowledgeContext(knowledgeContext);
            return this;
        }
        
        public Builder securityContext(SecurityContext securityContext) {
            context.setSecurityContext(securityContext);
            return this;
        }
        
        public Builder extendedAttribute(String key, Object value) {
            context.setExtendedAttribute(key, value);
            return this;
        }
        
        public LlmSceneContext build() {
            if (context.getContextId() == null) {
                context.setContextId(context.generateContextId());
            }
            return context;
        }
    }
}
