package net.ooder.skills.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 兼容性检查结果
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class CompatibilityResult {
    
    /**
     * 兼容性状态
     */
    public enum Status {
        /** 完全兼容 */
        COMPATIBLE,
        /** 部分兼容（有警告） */
        PARTIAL,
        /** 不兼容 */
        INCOMPATIBLE,
        /** 未知（Skill不存在） */
        UNKNOWN
    }
    
    private String skillId;
    private String requiredVersion;
    private String actualVersion;
    private Status status;
    private boolean compatible;
    private List<String> conflicts;
    private String suggestedVersion;
    private String message;
    private List<CompatibilityIssue> issues;
    
    public CompatibilityResult() {
        this.conflicts = new ArrayList<>();
        this.issues = new ArrayList<>();
    }
    
    public CompatibilityResult(String skillId, String requiredVersion) {
        this();
        this.skillId = skillId;
        this.requiredVersion = requiredVersion;
    }
    
    // Getters and Setters
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getRequiredVersion() {
        return requiredVersion;
    }
    
    public void setRequiredVersion(String requiredVersion) {
        this.requiredVersion = requiredVersion;
    }
    
    public String getActualVersion() {
        return actualVersion;
    }
    
    public void setActualVersion(String actualVersion) {
        this.actualVersion = actualVersion;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public boolean isCompatible() {
        return compatible;
    }
    
    public void setCompatible(boolean compatible) {
        this.compatible = compatible;
    }
    
    public List<String> getConflicts() {
        return conflicts;
    }
    
    public void setConflicts(List<String> conflicts) {
        this.conflicts = conflicts;
    }
    
    public void addConflict(String conflict) {
        if (this.conflicts == null) {
            this.conflicts = new ArrayList<>();
        }
        this.conflicts.add(conflict);
    }
    
    public String getSuggestedVersion() {
        return suggestedVersion;
    }
    
    public void setSuggestedVersion(String suggestedVersion) {
        this.suggestedVersion = suggestedVersion;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<CompatibilityIssue> getIssues() {
        return issues;
    }
    
    public void setIssues(List<CompatibilityIssue> issues) {
        this.issues = issues;
    }
    
    public void addIssue(CompatibilityIssue issue) {
        if (this.issues == null) {
            this.issues = new ArrayList<>();
        }
        this.issues.add(issue);
    }
    
    /**
     * 创建兼容的结果
     */
    public static CompatibilityResult compatible(String skillId, String requiredVersion, String actualVersion) {
        CompatibilityResult result = new CompatibilityResult(skillId, requiredVersion);
        result.setActualVersion(actualVersion);
        result.setStatus(Status.COMPATIBLE);
        result.setCompatible(true);
        result.setMessage("版本兼容");
        return result;
    }
    
    /**
     * 创建不兼容的结果
     */
    public static CompatibilityResult incompatible(String skillId, String requiredVersion, String actualVersion, String message) {
        CompatibilityResult result = new CompatibilityResult(skillId, requiredVersion);
        result.setActualVersion(actualVersion);
        result.setStatus(Status.INCOMPATIBLE);
        result.setCompatible(false);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 创建未知的结果（Skill不存在）
     */
    public static CompatibilityResult unknown(String skillId, String requiredVersion) {
        CompatibilityResult result = new CompatibilityResult(skillId, requiredVersion);
        result.setStatus(Status.UNKNOWN);
        result.setCompatible(false);
        result.setMessage("Skill不存在: " + skillId);
        return result;
    }
    
    @Override
    public String toString() {
        return "CompatibilityResult{" +
            "skillId='" + skillId + '\'' +
            ", requiredVersion='" + requiredVersion + '\'' +
            ", actualVersion='" + actualVersion + '\'' +
            ", status=" + status +
            ", compatible=" + compatible +
            ", message='" + message + '\'' +
            '}';
    }
}
