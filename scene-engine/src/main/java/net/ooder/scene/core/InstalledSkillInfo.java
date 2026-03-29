package net.ooder.scene.core;

import java.util.Map;

/**
 * 已安装技能信息
 */
public class InstalledSkillInfo {
    private String installId;
    private String skillId;
    private String name;
    private String version;
    private String status;
    private String installPath;
    private long installedAt;
    private long lastUsedAt;

    private String installSource;
    private String installedBy;
    private String sharedBy;
    private String delegatedBy;
    private Long pushTime;
    private String pushMessage;
    private Map<String, Object> sourceMetadata;

    public InstalledSkillInfo() {}

    public String getInstallId() { return installId; }
    public void setInstallId(String installId) { this.installId = installId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getInstallPath() { return installPath; }
    public void setInstallPath(String installPath) { this.installPath = installPath; }
    public long getInstalledAt() { return installedAt; }
    public void setInstalledAt(long installedAt) { this.installedAt = installedAt; }
    public long getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(long lastUsedAt) { this.lastUsedAt = lastUsedAt; }

    public String getInstallSource() { return installSource; }
    public void setInstallSource(String installSource) { this.installSource = installSource; }

    public String getInstalledBy() { return installedBy; }
    public void setInstalledBy(String installedBy) { this.installedBy = installedBy; }

    public String getSharedBy() { return sharedBy; }
    public void setSharedBy(String sharedBy) { this.sharedBy = sharedBy; }

    public String getDelegatedBy() { return delegatedBy; }
    public void setDelegatedBy(String delegatedBy) { this.delegatedBy = delegatedBy; }

    public Long getPushTime() { return pushTime; }
    public void setPushTime(Long pushTime) { this.pushTime = pushTime; }

    public String getPushMessage() { return pushMessage; }
    public void setPushMessage(String pushMessage) { this.pushMessage = pushMessage; }

    public Map<String, Object> getSourceMetadata() { return sourceMetadata; }
    public void setSourceMetadata(Map<String, Object> sourceMetadata) { this.sourceMetadata = sourceMetadata; }
}
