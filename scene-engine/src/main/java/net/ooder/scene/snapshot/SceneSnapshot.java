package net.ooder.scene.snapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景组快照
 *
 * <p>保存场景组在特定时间点的完整状态，包括参与者、能力、知识库绑定和LLM配置。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneSnapshot {

    private String snapshotId;
    private String sceneGroupId;
    private String name;
    private String description;
    private SnapshotStatus status;
    private LocalDateTime createTime;
    private String creatorId;
    private Long size;
    private SnapshotTrigger trigger;

    // 快照内容
    private Map<String, Object> metadata;
    private List<ParticipantSnapshot> participants;
    private List<CapabilitySnapshot> capabilities;
    private List<KnowledgeBindingSnapshot> knowledgeBases;
    private LlmConfigSnapshot llmConfig;
    private Map<String, Object> extendedConfig;

    public SceneSnapshot() {
        this.status = SnapshotStatus.CREATING;
        this.createTime = LocalDateTime.now();
        this.metadata = new HashMap<>();
        this.participants = new ArrayList<>();
        this.capabilities = new ArrayList<>();
        this.knowledgeBases = new ArrayList<>();
        this.extendedConfig = new HashMap<>();
    }

    // ===== Getters and Setters =====

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public String getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SnapshotStatus getStatus() {
        return status;
    }

    public void setStatus(SnapshotStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public SnapshotTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(SnapshotTrigger trigger) {
        this.trigger = trigger;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public List<ParticipantSnapshot> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantSnapshot> participants) {
        this.participants = participants;
    }

    public List<CapabilitySnapshot> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<CapabilitySnapshot> capabilities) {
        this.capabilities = capabilities;
    }

    public List<KnowledgeBindingSnapshot> getKnowledgeBases() {
        return knowledgeBases;
    }

    public void setKnowledgeBases(List<KnowledgeBindingSnapshot> knowledgeBases) {
        this.knowledgeBases = knowledgeBases;
    }

    public LlmConfigSnapshot getLlmConfig() {
        return llmConfig;
    }

    public void setLlmConfig(LlmConfigSnapshot llmConfig) {
        this.llmConfig = llmConfig;
    }

    public Map<String, Object> getExtendedConfig() {
        return extendedConfig;
    }

    public void setExtendedConfig(Map<String, Object> extendedConfig) {
        this.extendedConfig = extendedConfig;
    }

    // ===== 便捷方法 =====

    /**
     * 检查快照是否可用
     */
    public boolean isAvailable() {
        return status == SnapshotStatus.ACTIVE;
    }

    /**
     * 获取参与者数量
     */
    public int getParticipantCount() {
        return participants != null ? participants.size() : 0;
    }

    /**
     * 获取能力数量
     */
    public int getCapabilityCount() {
        return capabilities != null ? capabilities.size() : 0;
    }

    /**
     * 获取知识库绑定数量
     */
    public int getKnowledgeBaseCount() {
        return knowledgeBases != null ? knowledgeBases.size() : 0;
    }

    @Override
    public String toString() {
        return "SceneSnapshot{" +
            "snapshotId='" + snapshotId + '\'' +
            ", sceneGroupId='" + sceneGroupId + '\'' +
            ", name='" + name + '\'' +
            ", status=" + status +
            ", createTime=" + createTime +
            ", participants=" + getParticipantCount() +
            ", capabilities=" + getCapabilityCount() +
            ", knowledgeBases=" + getKnowledgeBaseCount() +
            '}';
    }
}
