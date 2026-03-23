package net.ooder.scene.core.instance;

import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.SkillLifecycleState;
import net.ooder.scene.core.template.SceneTemplate;

import java.io.Serializable;
import java.util.*;

/**
 * 场景实例
 * 
 * <p>表示场景在运行时的实例，包含：</p>
 * <ul>
 *   <li>场景基本信息</li>
 *   <li>参与者信息</li>
 *   <li>运行状态</li>
 *   <li>配置数据</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    private String instanceId;
    private String sceneId;
    private String templateId;
    private String templateName;
    private String skillId;
    private String skillName;
    private SkillLifecycleState state;
    private String visibility;
    private String category;
    
    private Map<String, ParticipantInfo> participants;
    private Map<String, Object> config;
    private Map<String, Object> runtimeData;
    private Map<String, Object> context;
    
    private long createdAt;
    private long updatedAt;
    private long activatedAt;
    private long deactivatedAt;
    private String createdBy;
    private String updatedBy;
    
    private List<ActivationRecord> activationHistory;
    private List<String> tags;
    private Map<String, String> metadata;

    public SceneInstance() {
        this.participants = new HashMap<>();
        this.config = new HashMap<>();
        this.runtimeData = new HashMap<>();
        this.context = new HashMap<>();
        this.activationHistory = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.state = SkillLifecycleState.INSTALLED;
    }

    public static SceneInstance fromTemplate(SceneTemplate template, String instanceId) {
        SceneInstance instance = new SceneInstance();
        instance.setInstanceId(instanceId);
        instance.setTemplateId(template.getTemplateId());
        instance.setTemplateName(template.getTemplateName());
        instance.setVisibility(template.getVisibility());
        instance.setCategory(template.getCategory());
        return instance;
    }

    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public SkillLifecycleState getState() { return state; }
    public void setState(SkillLifecycleState state) { this.state = state; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Map<String, ParticipantInfo> getParticipants() { return participants; }
    public void setParticipants(Map<String, ParticipantInfo> participants) { this.participants = participants; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public Map<String, Object> getRuntimeData() { return runtimeData; }
    public void setRuntimeData(Map<String, Object> runtimeData) { this.runtimeData = runtimeData; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public long getActivatedAt() { return activatedAt; }
    public void setActivatedAt(long activatedAt) { this.activatedAt = activatedAt; }
    public long getDeactivatedAt() { return deactivatedAt; }
    public void setDeactivatedAt(long deactivatedAt) { this.deactivatedAt = deactivatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public List<ActivationRecord> getActivationHistory() { return activationHistory; }
    public void setActivationHistory(List<ActivationRecord> activationHistory) { this.activationHistory = activationHistory; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

    public boolean isActivated() {
        return state == SkillLifecycleState.ACTIVATED;
    }

    public void addParticipant(ParticipantInfo participant) {
        if (participant != null && participant.getUserId() != null) {
            participants.put(participant.getUserId(), participant);
        }
    }

    public void removeParticipant(String userId) {
        participants.remove(userId);
    }

    public ParticipantInfo getParticipant(String userId) {
        return participants.get(userId);
    }

    public List<ParticipantInfo> getParticipantsByRole(String roleId) {
        List<ParticipantInfo> result = new ArrayList<>();
        for (ParticipantInfo participant : participants.values()) {
            if (roleId.equals(participant.getRoleId())) {
                result.add(participant);
            }
        }
        return result;
    }

    public void addActivationRecord(ActivationRecord record) {
        if (record != null) {
            activationHistory.add(record);
        }
    }

    /**
     * 参与者信息
     */
    public static class ParticipantInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        private String userId;
        private String userName;
        private String roleId;
        private String roleName;
        private ParticipantStatus status;
        private long joinedAt;
        private long leftAt;
        private Map<String, Object> config;
        private List<String> permissions;

        public enum ParticipantStatus {
            INVITED, JOINED, ACTIVE, INACTIVE, LEFT
        }

        public ParticipantInfo() {
            this.status = ParticipantStatus.INVITED;
            this.config = new HashMap<>();
            this.permissions = new ArrayList<>();
        }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
        public ParticipantStatus getStatus() { return status; }
        public void setStatus(ParticipantStatus status) { this.status = status; }
        public long getJoinedAt() { return joinedAt; }
        public void setJoinedAt(long joinedAt) { this.joinedAt = joinedAt; }
        public long getLeftAt() { return leftAt; }
        public void setLeftAt(long leftAt) { this.leftAt = leftAt; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }

        public boolean isActive() {
            return status == ParticipantStatus.ACTIVE || status == ParticipantStatus.JOINED;
        }
    }

    /**
     * 激活记录
     */
    public static class ActivationRecord implements Serializable {

        private static final long serialVersionUID = 1L;

        private String recordId;
        private String userId;
        private String roleId;
        private String stepId;
        private String stepName;
        private String action;
        private boolean success;
        private String message;
        private Map<String, Object> input;
        private Map<String, Object> output;
        private long timestamp;

        public String getRecordId() { return recordId; }
        public void setRecordId(String recordId) { this.recordId = recordId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }
        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, Object> getInput() { return input; }
        public void setInput(Map<String, Object> input) { this.input = input; }
        public Map<String, Object> getOutput() { return output; }
        public void setOutput(Map<String, Object> output) { this.output = output; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
