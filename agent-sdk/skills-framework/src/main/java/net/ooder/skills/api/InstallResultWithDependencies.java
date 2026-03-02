package net.ooder.skills.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 带依赖的安装结果
 * 
 * @author ooder
 * @since 2.3
 */
public class InstallResultWithDependencies {
    
    /** Skill ID */
    private String skillId;
    
    /** 是否成功 */
    private boolean success;
    
    /** 状态：installed, failed, partial */
    private String status;
    
    /** 成功安装的依赖列表 */
    private List<String> installedDependencies;
    
    /** 安装失败的依赖列表 */
    private List<String> failedDependencies;
    
    /** 已安装的依赖（之前已存在） */
    private List<String> existingDependencies;
    
    /** 错误信息 */
    private String error;
    
    /** 安装时间戳 */
    private long timestamp;
    
    /** 安装耗时（毫秒） */
    private long duration;
    
    public InstallResultWithDependencies() {
        this.installedDependencies = new ArrayList<>();
        this.failedDependencies = new ArrayList<>();
        this.existingDependencies = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<String> getInstalledDependencies() {
        return installedDependencies;
    }
    
    public void setInstalledDependencies(List<String> installedDependencies) {
        this.installedDependencies = installedDependencies;
    }
    
    public List<String> getFailedDependencies() {
        return failedDependencies;
    }
    
    public void setFailedDependencies(List<String> failedDependencies) {
        this.failedDependencies = failedDependencies;
    }
    
    public List<String> getExistingDependencies() {
        return existingDependencies;
    }
    
    public void setExistingDependencies(List<String> existingDependencies) {
        this.existingDependencies = existingDependencies;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    /**
     * 添加成功安装的依赖
     */
    public void addInstalledDependency(String skillId) {
        this.installedDependencies.add(skillId);
    }
    
    /**
     * 添加安装失败的依赖
     */
    public void addFailedDependency(String skillId) {
        this.failedDependencies.add(skillId);
    }
    
    /**
     * 添加已存在的依赖
     */
    public void addExistingDependency(String skillId) {
        this.existingDependencies.add(skillId);
    }
    
    @Override
    public String toString() {
        return "InstallResultWithDependencies{" +
                "skillId='" + skillId + '\'' +
                ", success=" + success +
                ", status='" + status + '\'' +
                ", installedDependencies=" + installedDependencies +
                ", failedDependencies=" + failedDependencies +
                ", error='" + error + '\'' +
                '}';
    }
}
