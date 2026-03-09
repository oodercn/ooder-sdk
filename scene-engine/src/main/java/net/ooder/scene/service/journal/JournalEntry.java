package net.ooder.scene.service.journal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志条目
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JournalEntry {

    private String entryId;
    private String sceneId;
    private String userId;
    private String userName;
    private String title;
    private String content;
    private JournalStatus status;
    private Date createTime;
    private Date submitTime;
    private Date reviewTime;
    private String reviewerId;
    private String reviewComment;
    private int version;
    private Map<String, Object> attachments = new HashMap<>();
    private Map<String, Object> metadata = new HashMap<>();

    public JournalEntry() {
        this.status = JournalStatus.SUBMITTED;
        this.version = 1;
        this.createTime = new Date();
    }

    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public JournalStatus getStatus() { return status; }
    public void setStatus(JournalStatus status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getSubmitTime() { return submitTime; }
    public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; }

    public Date getReviewTime() { return reviewTime; }
    public void setReviewTime(Date reviewTime) { this.reviewTime = reviewTime; }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public Map<String, Object> getAttachments() { return attachments; }
    public void setAttachments(Map<String, Object> attachments) { this.attachments = attachments; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public boolean isReviewed() {
        return status == JournalStatus.APPROVED || status == JournalStatus.REJECTED;
    }

    public boolean isApproved() {
        return status == JournalStatus.APPROVED;
    }

    /**
     * 日志状态枚举
     */
    public enum JournalStatus {
        DRAFT("草稿"),
        SUBMITTED("已提交"),
        UNDER_REVIEW("审核中"),
        APPROVED("已通过"),
        REJECTED("已驳回"),
        ARCHIVED("已归档");

        private final String description;

        JournalStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
