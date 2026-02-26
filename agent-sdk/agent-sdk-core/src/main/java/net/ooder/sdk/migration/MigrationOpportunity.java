package net.ooder.sdk.migration;

import java.time.LocalDateTime;

/**
 * 迁移机会
 */
public class MigrationOpportunity {
    private String skillId;
    private String currentVersion;
    private String targetVersion;
    private OpportunityType type;
    private MigrationAssessment assessment;
    private LocalDateTime detectedAt;
    private String reason;

    public enum OpportunityType {
        UPGRADE, DOWNGRADE, ROLLBACK
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public OpportunityType getType() {
        return type;
    }

    public void setType(OpportunityType type) {
        this.type = type;
    }

    public MigrationAssessment getAssessment() {
        return assessment;
    }

    public void setAssessment(MigrationAssessment assessment) {
        this.assessment = assessment;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
