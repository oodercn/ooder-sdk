package net.ooder.skill.capability.service;

import java.util.List;
import net.ooder.skill.capability.model.*;

public interface CapabilityBindingService {
    
    CapabilityBinding bind(String sceneGroupId, CapabilityBindingRequest request);
    
    void unbind(String bindingId);
    
    CapabilityBinding findById(String bindingId);
    
    List<CapabilityBinding> listAll();
    
    List<CapabilityBinding> listByCapability(String capabilityId);
    
    List<CapabilityBinding> listByAgent(String agentId);
    
    List<CapabilityBinding> listByLink(String linkId);
    
    void updateStatus(String bindingId, String status);
    
    public static class CapabilityBindingRequest {
        private String capabilityId;
        private String capDefId;
        private String agentId;
        private String linkId;
        private String providerType;
        private String providerId;
        private String connectorType;
        private int priority;
        private boolean fallback;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getCapDefId() { return capDefId; }
        public void setCapDefId(String capDefId) { this.capDefId = capDefId; }
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getLinkId() { return linkId; }
        public void setLinkId(String linkId) { this.linkId = linkId; }
        public String getProviderType() { return providerType; }
        public void setProviderType(String providerType) { this.providerType = providerType; }
        public String getProviderId() { return providerId; }
        public void setProviderId(String providerId) { this.providerId = providerId; }
        public String getConnectorType() { return connectorType; }
        public void setConnectorType(String connectorType) { this.connectorType = connectorType; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public boolean isFallback() { return fallback; }
        public void setFallback(boolean fallback) { this.fallback = fallback; }
    }
}
