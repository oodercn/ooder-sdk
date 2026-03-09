package net.ooder.scene.service.journal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志提交请求
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JournalSubmitRequest {

    private String draftId;
    private String sceneId;
    private String userId;
    private String title;
    private String content;
    private List<String> attachmentIds = new ArrayList<>();
    private Map<String, Object> extraData = new HashMap<>();
    private boolean requireReview = false;
    private String reviewerId;

    public JournalSubmitRequest() {}

    public JournalSubmitRequest(String draftId, String userId) {
        this.draftId = draftId;
        this.userId = userId;
    }

    public String getDraftId() { return draftId; }
    public void setDraftId(String draftId) { this.draftId = draftId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getAttachmentIds() { return attachmentIds; }
    public void setAttachmentIds(List<String> attachmentIds) { this.attachmentIds = attachmentIds; }

    public Map<String, Object> getExtraData() { return extraData; }
    public void setExtraData(Map<String, Object> extraData) { this.extraData = extraData; }

    public boolean isRequireReview() { return requireReview; }
    public void setRequireReview(boolean requireReview) { this.requireReview = requireReview; }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }

    public void addAttachment(String attachmentId) {
        attachmentIds.add(attachmentId);
    }
}
