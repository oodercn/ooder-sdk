package net.ooder.skills.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本冲突检测结果
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class ConflictDetectionResult {
    
    private boolean hasConflict;
    private int conflictCount;
    private List<VersionConflict> conflicts;
    private List<String> suggestions;
    
    public ConflictDetectionResult() {
        this.conflicts = new ArrayList<>();
        this.suggestions = new ArrayList<>();
    }
    
    // Getters and Setters
    
    public boolean isHasConflict() {
        return hasConflict;
    }
    
    public void setHasConflict(boolean hasConflict) {
        this.hasConflict = hasConflict;
    }
    
    public int getConflictCount() {
        return conflictCount;
    }
    
    public void setConflictCount(int conflictCount) {
        this.conflictCount = conflictCount;
    }
    
    public List<VersionConflict> getConflicts() {
        return conflicts;
    }
    
    public void setConflicts(List<VersionConflict> conflicts) {
        this.conflicts = conflicts;
        this.conflictCount = conflicts != null ? conflicts.size() : 0;
        this.hasConflict = this.conflictCount > 0;
    }
    
    public void addConflict(VersionConflict conflict) {
        if (this.conflicts == null) {
            this.conflicts = new ArrayList<>();
        }
        this.conflicts.add(conflict);
        this.conflictCount = this.conflicts.size();
        this.hasConflict = true;
    }
    
    public List<String> getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
    
    public void addSuggestion(String suggestion) {
        if (this.suggestions == null) {
            this.suggestions = new ArrayList<>();
        }
        this.suggestions.add(suggestion);
    }
    
    /**
     * 创建无冲突的结果
     */
    public static ConflictDetectionResult noConflict() {
        ConflictDetectionResult result = new ConflictDetectionResult();
        result.setHasConflict(false);
        result.setConflictCount(0);
        return result;
    }
    
    @Override
    public String toString() {
        return "ConflictDetectionResult{" +
            "hasConflict=" + hasConflict +
            ", conflictCount=" + conflictCount +
            ", conflicts=" + conflicts +
            '}';
    }
    
    /**
     * 版本冲突详情
     */
    public static class VersionConflict {
        
        private String skillId;
        private String conflictType;
        private List<String> requiredVersions;
        private String message;
        private String resolution;
        
        public VersionConflict() {
            this.requiredVersions = new ArrayList<>();
        }
        
        public VersionConflict(String skillId, String conflictType, String message) {
            this();
            this.skillId = skillId;
            this.conflictType = conflictType;
            this.message = message;
        }
        
        // Getters and Setters
        
        public String getSkillId() {
            return skillId;
        }
        
        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }
        
        public String getConflictType() {
            return conflictType;
        }
        
        public void setConflictType(String conflictType) {
            this.conflictType = conflictType;
        }
        
        public List<String> getRequiredVersions() {
            return requiredVersions;
        }
        
        public void setRequiredVersions(List<String> requiredVersions) {
            this.requiredVersions = requiredVersions;
        }
        
        public void addRequiredVersion(String version) {
            if (this.requiredVersions == null) {
                this.requiredVersions = new ArrayList<>();
            }
            this.requiredVersions.add(version);
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getResolution() {
            return resolution;
        }
        
        public void setResolution(String resolution) {
            this.resolution = resolution;
        }
        
        @Override
        public String toString() {
            return "VersionConflict{" +
                "skillId='" + skillId + '\'' +
                ", conflictType='" + conflictType + '\'' +
                ", message='" + message + '\'' +
                '}';
        }
    }
}
