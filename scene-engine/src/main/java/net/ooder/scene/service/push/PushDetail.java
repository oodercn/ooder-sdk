package net.ooder.scene.service.push;

import net.ooder.sdk.common.enums.MemberRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 推送详情
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class PushDetail {

    private String pushId;
    private String sceneId;
    private String sceneName;
    private String leaderId;
    private String leaderName;
    private String message;
    private long pushTime;
    private long expireTime;
    private int totalCount;
    private int confirmedCount;
    private int rejectedCount;
    private int pendingCount;
    private List<PushFeedback> feedbacks = new ArrayList<>();
    private Map<String, MemberRole> roleAssignments = new HashMap<>();
    private PushStatus status;

    public PushDetail() {
        this.status = PushStatus.ACTIVE;
    }

    public String getPushId() { return pushId; }
    public void setPushId(String pushId) { this.pushId = pushId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }

    public String getLeaderId() { return leaderId; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }

    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getPushTime() { return pushTime; }
    public void setPushTime(long pushTime) { this.pushTime = pushTime; }

    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getConfirmedCount() { return confirmedCount; }
    public void setConfirmedCount(int confirmedCount) { this.confirmedCount = confirmedCount; }

    public int getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(int rejectedCount) { this.rejectedCount = rejectedCount; }

    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }

    public List<PushFeedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<PushFeedback> feedbacks) { this.feedbacks = feedbacks; }

    public Map<String, MemberRole> getRoleAssignments() { return roleAssignments; }
    public void setRoleAssignments(Map<String, MemberRole> roleAssignments) { this.roleAssignments = roleAssignments; }

    public PushStatus getStatus() { return status; }
    public void setStatus(PushStatus status) { this.status = status; }

    public boolean isExpired() {
        return expireTime > 0 && System.currentTimeMillis() > expireTime;
    }

    public boolean isAllConfirmed() {
        return pendingCount == 0 && rejectedCount == 0 && confirmedCount == totalCount;
    }

    /**
     * 推送状态枚举
     */
    public enum PushStatus {
        ACTIVE("进行中"),
        COMPLETED("已完成"),
        EXPIRED("已过期"),
        CANCELLED("已取消");

        private final String description;

        PushStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
