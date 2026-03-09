package net.ooder.scene.service.push;

import net.ooder.sdk.common.enums.MemberRole;

/**
 * 推送反馈
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class PushFeedback {

    private String feedbackId;
    private String pushId;
    private String sceneId;
    private String userId;
    private String userName;
    private MemberRole assignedRole;
    private FeedbackStatus status;
    private String rejectReason;
    private long feedbackTime;
    private long pushTime;

    public PushFeedback() {}

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }

    public String getPushId() { return pushId; }
    public void setPushId(String pushId) { this.pushId = pushId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public MemberRole getAssignedRole() { return assignedRole; }
    public void setAssignedRole(MemberRole assignedRole) { this.assignedRole = assignedRole; }

    public FeedbackStatus getStatus() { return status; }
    public void setStatus(FeedbackStatus status) { this.status = status; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public long getFeedbackTime() { return feedbackTime; }
    public void setFeedbackTime(long feedbackTime) { this.feedbackTime = feedbackTime; }

    public long getPushTime() { return pushTime; }
    public void setPushTime(long pushTime) { this.pushTime = pushTime; }

    public boolean isConfirmed() {
        return status == FeedbackStatus.CONFIRMED;
    }

    public boolean isRejected() {
        return status == FeedbackStatus.REJECTED;
    }

    public boolean isPending() {
        return status == FeedbackStatus.PENDING;
    }

    /**
     * 反馈状态枚举
     */
    public enum FeedbackStatus {
        PENDING("待确认"),
        CONFIRMED("已确认"),
        REJECTED("已拒绝"),
        EXPIRED("已过期"),
        CANCELLED("已取消");

        private final String description;

        FeedbackStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
