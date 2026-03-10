package net.ooder.skills.config;

import java.util.List;

/**
 * Skill MainFirst 配置
 * 从 SkillManifest.MainFirstConfig 抽取
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SkillMainFirstConfig {
    private List<SelfCheckConfiguration> selfChecks;
    private List<SelfStartConfiguration> selfStarts;
    private SelfDriveConfiguration selfDrive;
    private List<CollaborationStartConfiguration> collaborationStarts;

    public List<SelfCheckConfiguration> getSelfChecks() { return selfChecks; }
    public void setSelfChecks(List<SelfCheckConfiguration> selfChecks) { this.selfChecks = selfChecks; }

    public List<SelfStartConfiguration> getSelfStarts() { return selfStarts; }
    public void setSelfStarts(List<SelfStartConfiguration> selfStarts) { this.selfStarts = selfStarts; }

    public SelfDriveConfiguration getSelfDrive() { return selfDrive; }
    public void setSelfDrive(SelfDriveConfiguration selfDrive) { this.selfDrive = selfDrive; }

    public List<CollaborationStartConfiguration> getCollaborationStarts() { return collaborationStarts; }
    public void setCollaborationStarts(List<CollaborationStartConfiguration> collaborationStarts) {
        this.collaborationStarts = collaborationStarts;
    }
}
