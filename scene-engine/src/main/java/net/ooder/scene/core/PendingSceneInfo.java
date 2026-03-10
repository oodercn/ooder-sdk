package net.ooder.scene.core;

import net.ooder.scene.skill.model.SkillCategory;
import net.ooder.scene.skill.model.SkillForm;
import net.ooder.scene.skill.model.SceneType;
import net.ooder.sdk.common.enums.MemberRole;

/**
 * 待激活场景信息 (v3.0)
 *
 * <p>表示一个等待用户激活的场景，包含场景基本信息和推送信息。</p>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 2.3.1
 */
public class PendingSceneInfo {

    private String sceneId;
    private String sceneName;
    private String description;
    private SkillCategory category;
    private SkillForm form;
    private SceneType sceneType;
    private MemberRole requiredRole;
    private String pushFrom;
    private String pushFromName;
    private long pushTime;
    private long expireTime;
    private PendingSceneStatus status;
    private int memberCount;
    private int activatedCount;

    public PendingSceneInfo() {
        this.status = PendingSceneStatus.PENDING;
    }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }

    public SkillForm getForm() { return form; }
    public void setForm(SkillForm form) { this.form = form; }

    public SceneType getSceneType() { return sceneType; }
    public void setSceneType(SceneType sceneType) { this.sceneType = sceneType; }

    public MemberRole getRequiredRole() { return requiredRole; }
    public void setRequiredRole(MemberRole requiredRole) { this.requiredRole = requiredRole; }

    public String getPushFrom() { return pushFrom; }
    public void setPushFrom(String pushFrom) { this.pushFrom = pushFrom; }

    public String getPushFromName() { return pushFromName; }
    public void setPushFromName(String pushFromName) { this.pushFromName = pushFromName; }

    public long getPushTime() { return pushTime; }
    public void setPushTime(long pushTime) { this.pushTime = pushTime; }

    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }

    public PendingSceneStatus getStatus() { return status; }
    public void setStatus(PendingSceneStatus status) { this.status = status; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getActivatedCount() { return activatedCount; }
    public void setActivatedCount(int activatedCount) { this.activatedCount = activatedCount; }

    public boolean isExpired() {
        return expireTime > 0 && System.currentTimeMillis() > expireTime;
    }

    public boolean isPushed() {
        return pushFrom != null && !pushFrom.isEmpty();
    }

    public boolean isScene() {
        return form == SkillForm.SCENE;
    }

    public boolean canSelfDrive() {
        return sceneType != null && sceneType.canSelfDrive();
    }

    /**
     * 待激活场景状态枚举
     */
    public enum PendingSceneStatus {
        PENDING("待激活"),
        ACTIVATING("激活中"),
        ACTIVATED("已激活"),
        EXPIRED("已过期"),
        REJECTED("已拒绝");

        private final String description;

        PendingSceneStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
