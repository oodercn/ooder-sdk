package net.ooder.scene.core.activation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 激活请求
 *
 * <p>启动激活流程时的请求参数</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ActivationRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneGroupId;
    private String templateId;
    private String activator;
    private String roleName;
    
    private String leaderId;
    private List<String> collaboratorIds;
    
    private boolean autoActivate;
    private List<String> enabledPrivateCapabilities;
    
    private Map<String, Object> config;
    private Map<String, Object> metadata;
    
    public ActivationRequest() {
        this.autoActivate = false;
        this.collaboratorIds = new ArrayList<>();
        this.enabledPrivateCapabilities = new ArrayList<>();
        this.config = new HashMap<>();
        this.metadata = new HashMap<>();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public String getActivator() {
        return activator;
    }
    
    public void setActivator(String activator) {
        this.activator = activator;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public String getLeaderId() {
        return leaderId;
    }
    
    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }
    
    public List<String> getCollaboratorIds() {
        return collaboratorIds != null ? collaboratorIds : new ArrayList<>();
    }
    
    public void setCollaboratorIds(List<String> collaboratorIds) {
        this.collaboratorIds = collaboratorIds != null ? collaboratorIds : new ArrayList<>();
    }
    
    public boolean isAutoActivate() {
        return autoActivate;
    }
    
    public void setAutoActivate(boolean autoActivate) {
        this.autoActivate = autoActivate;
    }
    
    public List<String> getEnabledPrivateCapabilities() {
        return enabledPrivateCapabilities != null ? enabledPrivateCapabilities : new ArrayList<>();
    }
    
    public void setEnabledPrivateCapabilities(List<String> enabledPrivateCapabilities) {
        this.enabledPrivateCapabilities = enabledPrivateCapabilities != null ? enabledPrivateCapabilities : new ArrayList<>();
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<>();
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
    
    @Override
    public String toString() {
        return "ActivationRequest{" +
                "templateId='" + templateId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", activator='" + activator + '\'' +
                ", roleName='" + roleName + '\'' +
                ", autoActivate=" + autoActivate +
                '}';
    }
    
    public static class Builder {
        private final ActivationRequest request = new ActivationRequest();
        
        public Builder sceneGroupId(String sceneGroupId) {
            request.setSceneGroupId(sceneGroupId);
            return this;
        }
        
        public Builder templateId(String templateId) {
            request.setTemplateId(templateId);
            return this;
        }
        
        public Builder activator(String activator) {
            request.setActivator(activator);
            return this;
        }
        
        public Builder roleName(String roleName) {
            request.setRoleName(roleName);
            return this;
        }
        
        public Builder leaderId(String leaderId) {
            request.setLeaderId(leaderId);
            return this;
        }
        
        public Builder collaboratorIds(List<String> collaboratorIds) {
            request.setCollaboratorIds(collaboratorIds);
            return this;
        }
        
        public Builder autoActivate(boolean autoActivate) {
            request.setAutoActivate(autoActivate);
            return this;
        }
        
        public Builder enabledPrivateCapabilities(List<String> capabilities) {
            request.setEnabledPrivateCapabilities(capabilities);
            return this;
        }
        
        public Builder config(Map<String, Object> config) {
            request.setConfig(config);
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            request.setMetadata(metadata);
            return this;
        }
        
        public ActivationRequest build() {
            return request;
        }
    }
}
