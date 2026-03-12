
package net.ooder.skills.core.installer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ooder.skills.api.InstallRequest;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.context.EnvironmentContext;
import net.ooder.skills.api.context.OrganizationContext;
import net.ooder.skills.api.installer.InstallStep;
import net.ooder.skills.api.rag.RagConfig;

/**
 * 安装上下文
 *
 * <p>扩展支持 RAG 安装配置和环境扫描</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.3.0
 */
public class InstallContext {

    private String contextId;
    private InstallRequest request;
    private SkillPackage skillPackage;
    private String installPath;
    private Map<String, Object> properties;
    private List<String> installedFiles;
    private List<String> installedDependencies;
    private InstallStatus status;
    private String errorMessage;
    private long startTime;
    private long endTime;
    private Map<String, Object> rollbackData;

    // ========== v2.4.0 新增字段 ==========

    /**
     * 组织上下文
     */
    private OrganizationContext organizationContext;

    /**
     * 环境上下文
     */
    private EnvironmentContext environmentContext;

    /**
     * RAG 安装配置
     */
    private RagConfig ragConfig;

    /**
     * 安装步骤记录
     */
    private List<InstallStep> installSteps;

    /**
     * 扫描结果
     */
    private Map<String, Object> scanResults;
    
    public InstallContext() {
        this.contextId = java.util.UUID.randomUUID().toString();
        this.properties = new HashMap<>();
        this.installedFiles = new ArrayList<>();
        this.installedDependencies = new ArrayList<>();
        this.rollbackData = new HashMap<>();
        this.status = InstallStatus.INITIALIZED;
        // v2.4.0 初始化新增字段
        this.installSteps = new ArrayList<>();
        this.scanResults = new HashMap<>();
    }
    
    public String getContextId() { return contextId; }
    
    public InstallRequest getRequest() { return request; }
    public void setRequest(InstallRequest request) { this.request = request; }
    
    public String getSkillId() {
        return request != null ? request.getSkillId() : null;
    }
    public void setSkillId(String skillId) {
        if (request == null) {
            request = new InstallRequest();
        }
        request.setSkillId(skillId);
    }
    
    public SkillPackage getSkillPackage() { return skillPackage; }
    public void setSkillPackage(SkillPackage skillPackage) { this.skillPackage = skillPackage; }
    
    public String getInstallPath() { return installPath; }
    public void setInstallPath(String installPath) { this.installPath = installPath; }
    
    public Map<String, Object> getProperties() { return properties; }
    public void setProperty(String key, Object value) { properties.put(key, value); }
    public Object getProperty(String key) { return properties.get(key); }
    
    public List<String> getInstalledFiles() { return installedFiles; }
    public void addInstalledFile(String file) { installedFiles.add(file); }
    
    public List<String> getInstalledDependencies() { return installedDependencies; }
    public void addInstalledDependency(String dep) { installedDependencies.add(dep); }
    
    public InstallStatus getStatus() { return status; }
    public void setStatus(InstallStatus status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    
    public long getDuration() {
        if (startTime > 0 && endTime > 0) {
            return endTime - startTime;
        }
        return 0;
    }
    
    public Map<String, Object> getRollbackData() { return rollbackData; }
    public void setRollbackData(String key, Object value) { rollbackData.put(key, value); }
    public Object getRollbackData(String key) { return rollbackData.get(key); }

    // ========== v2.4.0 新增 Getter/Setter ==========

    public OrganizationContext getOrganizationContext() { return organizationContext; }
    public void setOrganizationContext(OrganizationContext organizationContext) { this.organizationContext = organizationContext; }

    public EnvironmentContext getEnvironmentContext() { return environmentContext; }
    public void setEnvironmentContext(EnvironmentContext environmentContext) { this.environmentContext = environmentContext; }

    public RagConfig getRagConfig() { return ragConfig; }
    public void setRagConfig(RagConfig ragConfig) { this.ragConfig = ragConfig; }

    public List<InstallStep> getInstallSteps() { return installSteps; }
    public void setInstallSteps(List<InstallStep> installSteps) { this.installSteps = installSteps; }
    public void addInstallStep(InstallStep step) { installSteps.add(step); }

    public Map<String, Object> getScanResults() { return scanResults; }
    public void setScanResults(Map<String, Object> scanResults) { this.scanResults = scanResults; }
    public void setScanResult(String key, Object value) { scanResults.put(key, value); }
}
