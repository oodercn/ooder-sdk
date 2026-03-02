package net.ooder.skills.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板安装结果
 * 包含场景模板安装的完整结果
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class TemplateInstallResult {

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 场景ID（如果创建了场景）
     */
    private String sceneId;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 状态：pending, installing, installed, failed, partial
     */
    private String status;

    /**
     * 成功安装的 Skills
     */
    private List<String> installedSkills;

    /**
     * 跳过的 Skills（已安装）
     */
    private List<String> skippedSkills;

    /**
     * 安装失败的 Skills
     */
    private List<String> failedSkills;

    /**
     * 每个 Skill 的安装结果详情
     */
    private Map<String, SkillInstallDetail> skillDetails;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 安装时间戳
     */
    private long timestamp;

    /**
     * 安装耗时（毫秒）
     */
    private long duration;

    /**
     * 安装进度（0-100）
     */
    private int progress;

    public TemplateInstallResult() {
        this.installedSkills = new ArrayList<>();
        this.skippedSkills = new ArrayList<>();
        this.failedSkills = new ArrayList<>();
        this.skillDetails = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
        this.status = "pending";
        this.progress = 0;
    }

    // Getters and Setters
    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getInstalledSkills() {
        return installedSkills;
    }

    public void setInstalledSkills(List<String> installedSkills) {
        this.installedSkills = installedSkills;
    }

    public List<String> getSkippedSkills() {
        return skippedSkills;
    }

    public void setSkippedSkills(List<String> skippedSkills) {
        this.skippedSkills = skippedSkills;
    }

    public List<String> getFailedSkills() {
        return failedSkills;
    }

    public void setFailedSkills(List<String> failedSkills) {
        this.failedSkills = failedSkills;
    }

    public Map<String, SkillInstallDetail> getSkillDetails() {
        return skillDetails;
    }

    public void setSkillDetails(Map<String, SkillInstallDetail> skillDetails) {
        this.skillDetails = skillDetails;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    /**
     * 添加成功安装的 Skill
     */
    public void addInstalledSkill(String skillId) {
        this.installedSkills.add(skillId);
    }

    /**
     * 添加跳过的 Skill
     */
    public void addSkippedSkill(String skillId) {
        this.skippedSkills.add(skillId);
    }

    /**
     * 添加安装失败的 Skill
     */
    public void addFailedSkill(String skillId) {
        this.failedSkills.add(skillId);
    }

    /**
     * 添加 Skill 安装详情
     */
    public void addSkillDetail(String skillId, SkillInstallDetail detail) {
        this.skillDetails.put(skillId, detail);
    }

    /**
     * 更新进度
     */
    public void updateProgress(int progress) {
        this.progress = Math.min(100, Math.max(0, progress));
    }

    /**
     * Skill 安装详情
     */
    public static class SkillInstallDetail {
        private String skillId;
        private String status; // installed, skipped, failed
        private String version;
        private String error;
        private long duration;

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    @Override
    public String toString() {
        return "TemplateInstallResult{" +
                "templateId='" + templateId + '\'' +
                ", sceneId='" + sceneId + '\'' +
                ", success=" + success +
                ", status='" + status + '\'' +
                ", installedSkills=" + installedSkills.size() +
                ", skippedSkills=" + skippedSkills.size() +
                ", failedSkills=" + failedSkills.size() +
                ", progress=" + progress +
                '}';
    }
}
