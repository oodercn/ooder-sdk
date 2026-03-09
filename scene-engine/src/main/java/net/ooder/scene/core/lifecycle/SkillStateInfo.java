package net.ooder.scene.core.lifecycle;

import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.SkillLifecycleState;

import java.util.Map;

/**
 * 技能状态信息
 */
public class SkillStateInfo {
    private String sceneId;
    private String skillId;
    private String skillName;
    private SkillLifecycleState state;
    private String role;
    private long installTime;
    private long activateTime;
    private long lastActiveTime;
    private Map<String, Object> config;
    private String errorMessage;

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public SkillLifecycleState getState() { return state; }
    public void setState(SkillLifecycleState state) { this.state = state; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public long getInstallTime() { return installTime; }
    public void setInstallTime(long installTime) { this.installTime = installTime; }
    public long getActivateTime() { return activateTime; }
    public void setActivateTime(long activateTime) { this.activateTime = activateTime; }
    public long getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public boolean isActivated() {
        return state == SkillLifecycleState.ACTIVATED;
    }

    public boolean isInstalled() {
        return state == SkillLifecycleState.INSTALLED || 
               state == SkillLifecycleState.ACTIVATED ||
               state == SkillLifecycleState.DEACTIVATED;
    }
}
