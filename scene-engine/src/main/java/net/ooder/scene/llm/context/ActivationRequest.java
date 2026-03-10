package net.ooder.scene.llm.context;

import java.util.List;

/**
 * Skill 激活请求
 * 
 * <p>封装 Skill 激活所需的所有参数</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ActivationRequest {
    
    private String skillId;
    private String sceneId;
    private String userId;
    private String roleId;
    private List<String> knowledgeBaseIds;
    private String sessionId;
    private KnowledgeContext.KnowledgeLoadLevel knowledgeLevel;
    
    public ActivationRequest() {
        this.knowledgeLevel = KnowledgeContext.KnowledgeLoadLevel.ADVANCED;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getRoleId() {
        return roleId;
    }
    
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    
    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }
    
    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public KnowledgeContext.KnowledgeLoadLevel getKnowledgeLevel() {
        return knowledgeLevel;
    }
    
    public void setKnowledgeLevel(KnowledgeContext.KnowledgeLoadLevel knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }
    
    public static class Builder {
        private ActivationRequest request = new ActivationRequest();
        
        public Builder skillId(String skillId) {
            request.setSkillId(skillId);
            return this;
        }
        
        public Builder sceneId(String sceneId) {
            request.setSceneId(sceneId);
            return this;
        }
        
        public Builder userId(String userId) {
            request.setUserId(userId);
            return this;
        }
        
        public Builder roleId(String roleId) {
            request.setRoleId(roleId);
            return this;
        }
        
        public Builder knowledgeBaseIds(List<String> knowledgeBaseIds) {
            request.setKnowledgeBaseIds(knowledgeBaseIds);
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            request.setSessionId(sessionId);
            return this;
        }
        
        public Builder knowledgeLevel(KnowledgeContext.KnowledgeLoadLevel level) {
            request.setKnowledgeLevel(level);
            return this;
        }
        
        public ActivationRequest build() {
            return request;
        }
    }
}
