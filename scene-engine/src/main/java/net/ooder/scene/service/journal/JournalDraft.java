package net.ooder.scene.service.journal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志草稿
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JournalDraft {

    private String draftId;
    private String sceneId;
    private String userId;
    private String title;
    private String content;
    private DraftStatus status;
    private Date createTime;
    private Date updateTime;
    private Date submitTime;
    private String templateId;
    private Map<String, Object> metadata = new HashMap<>();
    private AutoGenerateSource autoGenerateSource;

    public JournalDraft() {
        this.status = DraftStatus.DRAFT;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public JournalDraft(String sceneId, String userId) {
        this();
        this.sceneId = sceneId;
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

    public DraftStatus getStatus() { return status; }
    public void setStatus(DraftStatus status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Date getSubmitTime() { return submitTime; }
    public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; }

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public AutoGenerateSource getAutoGenerateSource() { return autoGenerateSource; }
    public void setAutoGenerateSource(AutoGenerateSource autoGenerateSource) { this.autoGenerateSource = autoGenerateSource; }

    public boolean isSubmitted() {
        return status == DraftStatus.SUBMITTED;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.updateTime = new Date();
    }

    /**
     * 草稿状态枚举
     */
    public enum DraftStatus {
        DRAFT("草稿"),
        SUBMITTED("已提交"),
        ARCHIVED("已归档");

        private final String description;

        DraftStatus(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    /**
     * 自动生成来源枚举
     */
    public enum AutoGenerateSource {
        MANUAL("手工撰写"),
        EMAIL_SUMMARY("邮件汇总"),
        GIT_SUMMARY("代码提交汇总"),
        AI_GENERATED("AI生成"),
        TEMPLATE("模板生成");

        private final String description;

        AutoGenerateSource(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }
}
