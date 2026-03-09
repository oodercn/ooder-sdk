package net.ooder.scene.llm.command;

import java.io.Serializable;

/**
 * 上下文引用
 * 
 * <p>用于引用传递模式，避免完整上下文传输。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class ContextReference implements Serializable {

    private static final long serialVersionUID = 1L;

    private String contextId;
    private String sceneId;
    private String agentId;
    private long createdAt;
    private String checksum;

    public ContextReference() {}
    
    public ContextReference(String contextId, String sceneId) {
        this.contextId = contextId;
        this.sceneId = sceneId;
        this.createdAt = System.currentTimeMillis();
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
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public static class Builder {
        private ContextReference reference = new ContextReference();
        
        public Builder contextId(String contextId) {
            reference.setContextId(contextId);
            return this;
        }
        
        public Builder sceneId(String sceneId) {
            reference.setSceneId(sceneId);
            return this;
        }
        
        public Builder agentId(String agentId) {
            reference.setAgentId(agentId);
            return this;
        }
        
        public Builder createdAt(long createdAt) {
            reference.setCreatedAt(createdAt);
            return this;
        }
        
        public Builder checksum(String checksum) {
            reference.setChecksum(checksum);
            return this;
        }
        
        public ContextReference build() {
            return reference;
        }
    }
}
