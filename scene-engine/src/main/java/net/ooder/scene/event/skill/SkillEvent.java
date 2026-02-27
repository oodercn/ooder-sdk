package net.ooder.scene.event.skill;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.SceneEventType;

public class SkillEvent extends SceneEvent {
    
    private final String skillId;
    private final String skillName;
    private final String version;
    private final String userId;
    private final boolean success;
    private final String errorMessage;
    
    private SkillEvent(Object source, SceneEventType eventType, String skillId, String skillName) {
        super(source, eventType);
        this.skillId = skillId;
        this.skillName = skillName;
        this.version = null;
        this.userId = null;
        this.success = true;
        this.errorMessage = null;
    }
    
    private SkillEvent(Object source, SceneEventType eventType, String skillId, String skillName,
                       String version, String userId, boolean success, String errorMessage) {
        super(source, eventType);
        this.skillId = skillId;
        this.skillName = skillName;
        this.version = version;
        this.userId = userId;
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    public static SkillEvent installed(Object source, String skillId, String skillName, String version) {
        return new SkillEvent(source, SceneEventType.SKILL_INSTALLED, skillId, skillName, 
                version, null, true, null);
    }
    
    public static SkillEvent installFailed(Object source, String skillName, String error) {
        return new SkillEvent(source, SceneEventType.SKILL_INSTALL_FAILED, null, skillName,
                null, null, false, error);
    }
    
    public static SkillEvent uninstalled(Object source, String skillId, String skillName) {
        return new SkillEvent(source, SceneEventType.SKILL_UNINSTALLED, skillId, skillName);
    }
    
    public static SkillEvent uninstallFailed(Object source, String skillId, String skillName, String error) {
        return new SkillEvent(source, SceneEventType.SKILL_UNINSTALL_FAILED, skillId, skillName,
                null, null, false, error);
    }
    
    public static SkillEvent started(Object source, String skillId, String skillName) {
        return new SkillEvent(source, SceneEventType.SKILL_STARTED, skillId, skillName);
    }
    
    public static SkillEvent stopped(Object source, String skillId, String skillName) {
        return new SkillEvent(source, SceneEventType.SKILL_STOPPED, skillId, skillName);
    }
    
    public static SkillEvent restarted(Object source, String skillId, String skillName) {
        return new SkillEvent(source, SceneEventType.SKILL_RESTARTED, skillId, skillName);
    }
    
    public static SkillEvent executionError(Object source, String skillId, String skillName, String error) {
        return new SkillEvent(source, SceneEventType.SKILL_EXECUTION_ERROR, skillId, skillName,
                null, null, false, error);
    }
    
    public static SkillEvent shared(Object source, String skillId, String skillName, String userId) {
        return new SkillEvent(source, SceneEventType.SKILL_SHARED, skillId, skillName, null, userId, true, null);
    }
    
    public static SkillEvent shareAccepted(Object source, String skillId, String skillName, String userId) {
        return new SkillEvent(source, SceneEventType.SKILL_SHARE_ACCEPTED, skillId, skillName, null, userId, true, null);
    }
    
    public static SkillEvent shareRejected(Object source, String skillId, String skillName, String userId) {
        return new SkillEvent(source, SceneEventType.SKILL_SHARE_REJECTED, skillId, skillName, null, userId, true, null);
    }
    
    public static SkillEvent shareCancelled(Object source, String skillId, String skillName) {
        return new SkillEvent(source, SceneEventType.SKILL_SHARE_CANCELLED, skillId, skillName);
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public String getSkillName() {
        return skillName;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Override
    public String toString() {
        return "SkillEvent{" +
                "eventType=" + getEventType() +
                ", skillId='" + skillId + '\'' +
                ", skillName='" + skillName + '\'' +
                ", version='" + version + '\'' +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", traceId='" + getTraceId() + '\'' +
                '}';
    }
}
