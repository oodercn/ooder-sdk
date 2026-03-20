package net.ooder.scene.llm.audit;

import java.util.Map;

/**
 * LLM 调用上下文
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class LlmCallContext {
    
    private String companyId;
    private String companyName;
    private String departmentId;
    private String departmentName;
    private String userId;
    private String userName;
    private String sceneId;
    private String sceneName;
    private String capabilityId;
    private String capabilityName;
    private String moduleId;
    private String moduleName;
    private String businessType;
    private String clientIp;
    private String sessionId;
    private String requestId;
    private Map<String, Object> metadata;
    
    public LlmCallContext() {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getCapabilityName() { return capabilityName; }
    public void setCapabilityName(String capabilityName) { this.capabilityName = capabilityName; }
    public String getModuleId() { return moduleId; }
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public static class Builder {
        private final LlmCallContext context = new LlmCallContext();
        
        public Builder companyId(String companyId) { context.setCompanyId(companyId); return this; }
        public Builder companyName(String companyName) { context.setCompanyName(companyName); return this; }
        public Builder departmentId(String departmentId) { context.setDepartmentId(departmentId); return this; }
        public Builder departmentName(String departmentName) { context.setDepartmentName(departmentName); return this; }
        public Builder userId(String userId) { context.setUserId(userId); return this; }
        public Builder userName(String userName) { context.setUserName(userName); return this; }
        public Builder sceneId(String sceneId) { context.setSceneId(sceneId); return this; }
        public Builder sceneName(String sceneName) { context.setSceneName(sceneName); return this; }
        public Builder capabilityId(String capabilityId) { context.setCapabilityId(capabilityId); return this; }
        public Builder capabilityName(String capabilityName) { context.setCapabilityName(capabilityName); return this; }
        public Builder moduleId(String moduleId) { context.setModuleId(moduleId); return this; }
        public Builder moduleName(String moduleName) { context.setModuleName(moduleName); return this; }
        public Builder businessType(String businessType) { context.setBusinessType(businessType); return this; }
        public Builder clientIp(String clientIp) { context.setClientIp(clientIp); return this; }
        public Builder sessionId(String sessionId) { context.setSessionId(sessionId); return this; }
        public Builder requestId(String requestId) { context.setRequestId(requestId); return this; }
        public Builder metadata(Map<String, Object> metadata) { context.setMetadata(metadata); return this; }
        
        public LlmCallContext build() { return context; }
    }
}
