package net.ooder.scene.snapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * 快照差异
 *
 * <p>表示两个快照之间的差异，包括新增、删除和修改的内容。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SnapshotDiff {

    private String snapshotId1;
    private String snapshotId2;

    // 参与者差异
    private List<ParticipantSnapshot> addedParticipants;
    private List<ParticipantSnapshot> removedParticipants;
    private List<ParticipantSnapshot> modifiedParticipants;

    // 能力差异
    private List<CapabilitySnapshot> addedCapabilities;
    private List<CapabilitySnapshot> removedCapabilities;
    private List<CapabilitySnapshot> modifiedCapabilities;

    // 知识库差异
    private List<KnowledgeBindingSnapshot> addedKnowledgeBases;
    private List<KnowledgeBindingSnapshot> removedKnowledgeBases;
    private List<KnowledgeBindingSnapshot> modifiedKnowledgeBases;

    // LLM配置差异
    private boolean llmConfigChanged;
    private LlmConfigSnapshot llmConfig1;
    private LlmConfigSnapshot llmConfig2;

    public SnapshotDiff() {
        this.addedParticipants = new ArrayList<>();
        this.removedParticipants = new ArrayList<>();
        this.modifiedParticipants = new ArrayList<>();
        this.addedCapabilities = new ArrayList<>();
        this.removedCapabilities = new ArrayList<>();
        this.modifiedCapabilities = new ArrayList<>();
        this.addedKnowledgeBases = new ArrayList<>();
        this.removedKnowledgeBases = new ArrayList<>();
        this.modifiedKnowledgeBases = new ArrayList<>();
    }

    // ===== Getters and Setters =====

    public String getSnapshotId1() {
        return snapshotId1;
    }

    public void setSnapshotId1(String snapshotId1) {
        this.snapshotId1 = snapshotId1;
    }

    public String getSnapshotId2() {
        return snapshotId2;
    }

    public void setSnapshotId2(String snapshotId2) {
        this.snapshotId2 = snapshotId2;
    }

    public List<ParticipantSnapshot> getAddedParticipants() {
        return addedParticipants;
    }

    public void setAddedParticipants(List<ParticipantSnapshot> addedParticipants) {
        this.addedParticipants = addedParticipants;
    }

    public List<ParticipantSnapshot> getRemovedParticipants() {
        return removedParticipants;
    }

    public void setRemovedParticipants(List<ParticipantSnapshot> removedParticipants) {
        this.removedParticipants = removedParticipants;
    }

    public List<ParticipantSnapshot> getModifiedParticipants() {
        return modifiedParticipants;
    }

    public void setModifiedParticipants(List<ParticipantSnapshot> modifiedParticipants) {
        this.modifiedParticipants = modifiedParticipants;
    }

    public List<CapabilitySnapshot> getAddedCapabilities() {
        return addedCapabilities;
    }

    public void setAddedCapabilities(List<CapabilitySnapshot> addedCapabilities) {
        this.addedCapabilities = addedCapabilities;
    }

    public List<CapabilitySnapshot> getRemovedCapabilities() {
        return removedCapabilities;
    }

    public void setRemovedCapabilities(List<CapabilitySnapshot> removedCapabilities) {
        this.removedCapabilities = removedCapabilities;
    }

    public List<CapabilitySnapshot> getModifiedCapabilities() {
        return modifiedCapabilities;
    }

    public void setModifiedCapabilities(List<CapabilitySnapshot> modifiedCapabilities) {
        this.modifiedCapabilities = modifiedCapabilities;
    }

    public List<KnowledgeBindingSnapshot> getAddedKnowledgeBases() {
        return addedKnowledgeBases;
    }

    public void setAddedKnowledgeBases(List<KnowledgeBindingSnapshot> addedKnowledgeBases) {
        this.addedKnowledgeBases = addedKnowledgeBases;
    }

    public List<KnowledgeBindingSnapshot> getRemovedKnowledgeBases() {
        return removedKnowledgeBases;
    }

    public void setRemovedKnowledgeBases(List<KnowledgeBindingSnapshot> removedKnowledgeBases) {
        this.removedKnowledgeBases = removedKnowledgeBases;
    }

    public List<KnowledgeBindingSnapshot> getModifiedKnowledgeBases() {
        return modifiedKnowledgeBases;
    }

    public void setModifiedKnowledgeBases(List<KnowledgeBindingSnapshot> modifiedKnowledgeBases) {
        this.modifiedKnowledgeBases = modifiedKnowledgeBases;
    }

    public boolean isLlmConfigChanged() {
        return llmConfigChanged;
    }

    public void setLlmConfigChanged(boolean llmConfigChanged) {
        this.llmConfigChanged = llmConfigChanged;
    }

    public LlmConfigSnapshot getLlmConfig1() {
        return llmConfig1;
    }

    public void setLlmConfig1(LlmConfigSnapshot llmConfig1) {
        this.llmConfig1 = llmConfig1;
    }

    public LlmConfigSnapshot getLlmConfig2() {
        return llmConfig2;
    }

    public void setLlmConfig2(LlmConfigSnapshot llmConfig2) {
        this.llmConfig2 = llmConfig2;
    }

    // ===== 便捷方法 =====

    /**
     * 检查是否有差异
     */
    public boolean hasDifferences() {
        return !addedParticipants.isEmpty()
            || !removedParticipants.isEmpty()
            || !modifiedParticipants.isEmpty()
            || !addedCapabilities.isEmpty()
            || !removedCapabilities.isEmpty()
            || !modifiedCapabilities.isEmpty()
            || !addedKnowledgeBases.isEmpty()
            || !removedKnowledgeBases.isEmpty()
            || !modifiedKnowledgeBases.isEmpty()
            || llmConfigChanged;
    }

    /**
     * 获取总变更数
     */
    public int getTotalChanges() {
        return addedParticipants.size()
            + removedParticipants.size()
            + modifiedParticipants.size()
            + addedCapabilities.size()
            + removedCapabilities.size()
            + modifiedCapabilities.size()
            + addedKnowledgeBases.size()
            + removedKnowledgeBases.size()
            + modifiedKnowledgeBases.size()
            + (llmConfigChanged ? 1 : 0);
    }

    @Override
    public String toString() {
        return "SnapshotDiff{" +
            "snapshotId1='" + snapshotId1 + '\'' +
            ", snapshotId2='" + snapshotId2 + '\'' +
            ", totalChanges=" + getTotalChanges() +
            ", hasDifferences=" + hasDifferences() +
            '}';
    }
}
