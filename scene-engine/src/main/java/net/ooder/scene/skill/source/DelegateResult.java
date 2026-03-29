package net.ooder.scene.skill.source;

import java.util.List;

/**
 * 技能委派结果
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0.1
 */
public class DelegateResult {

    private String skillId;
    private String fromUserId;
    private List<String> toUserIds;
    private Long deadline;
    private Long delegateTime;
    private boolean success;
    private String message;

    public DelegateResult() {}

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public List<String> getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(List<String> toUserIds) {
        this.toUserIds = toUserIds;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public Long getDelegateTime() {
        return delegateTime;
    }

    public void setDelegateTime(Long delegateTime) {
        this.delegateTime = delegateTime;
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
