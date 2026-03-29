package net.ooder.scene.skill.source;

import java.util.List;

/**
 * 技能推送结果
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0.1
 */
public class PushResult {

    private String skillId;
    private List<String> toUserIds;
    private Long pushTime;
    private boolean success;
    private String message;

    public PushResult() {}

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public List<String> getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(List<String> toUserIds) {
        this.toUserIds = toUserIds;
    }

    public Long getPushTime() {
        return pushTime;
    }

    public void setPushTime(Long pushTime) {
        this.pushTime = pushTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
