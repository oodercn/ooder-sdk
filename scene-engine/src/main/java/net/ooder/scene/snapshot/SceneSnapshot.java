package net.ooder.scene.snapshot;

import net.ooder.scene.capability.CapabilityBinding;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.participant.Participant;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 场景快照
 * 
 * <p>SE原生的场景快照模型，用于保存场景组在某一时刻的完整状态。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>保存场景组的完整状态</li>
 *   <li>支持从快照恢复场景组</li>
 *   <li>管理快照的元数据</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneSnapshot {
    
    /**
     * 快照类型
     */
    public enum Type {
        MANUAL,         // 手动创建
        AUTO,           // 自动创建
        SCHEDULED,      // 定时创建
        BEFORE_ACTION,  // 操作前创建
        SYSTEM          // 系统创建
    }
    
    /**
     * 快照状态
     */
    public enum Status {
        ACTIVE,         // 有效状态
        RESTORING,      // 恢复中
        RESTORED,       // 已恢复
        EXPIRED,        // 已过期
        DELETED         // 已删除
    }
    
    // ========== 基础信息 ==========
    
    /** 快照ID */
    private final String snapshotId;
    
    /** 场景组ID */
    private final String sceneGroupId;
    
    /** 快照名称 */
    private String name;
    
    /** 快照描述 */
    private String description;
    
    /** 快照类型 */
    private final Type type;
    
    // ========== 状态 ==========
    
    /** 当前状态 */
    private volatile Status status = Status.ACTIVE;
    
    // ========== 快照数据 ==========
    
    /** 场景组状态数据（JSON序列化） */
    private String sceneGroupData;
    
    /** 参与者列表 */
    private List<Participant> participants = new CopyOnWriteArrayList<>();
    
    /** 能力绑定列表 */
    private List<CapabilityBinding> capabilityBindings = new CopyOnWriteArrayList<>();
    
    /** 知识库绑定列表 */
    private List<KnowledgeBinding> knowledgeBindings = new CopyOnWriteArrayList<>();
    
    /** 配置数据 */
    private Map<String, Object> configData = new HashMap<>();
    
    /** 元数据 */
    private Map<String, Object> metadata = new HashMap<>();
    
    // ========== 统计 ==========
    
    /** 快照大小（字节） */
    private volatile long size = 0;
    
    /** 引用计数 */
    private volatile int refCount = 0;
    
    // ========== 时间信息 ==========
    
    /** 快照创建时间 */
    private final Instant createTime;
    
    /** 最后更新时间 */
    private volatile Instant lastUpdateTime;
    
    /** 过期时间 */
    private volatile Instant expireTime;
    
    /** 恢复时间 */
    private volatile Instant restoreTime;
    
    /**
     * 构造函数
     */
    public SceneSnapshot(String snapshotId, String sceneGroupId, Type type) {
        this.snapshotId = snapshotId;
        this.sceneGroupId = sceneGroupId;
        this.type = type;
        this.createTime = Instant.now();
        this.lastUpdateTime = this.createTime;
    }
    
    // ========== 基础信息 Getter/Setter ==========
    
    public String getSnapshotId() {
        return snapshotId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        updateTime();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        updateTime();
    }
    
    public Type getType() {
        return type;
    }
    
    // ========== 状态管理 ==========
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
        updateTime();
    }
    
    /**
     * 标记为恢复中
     */
    public void markRestoring() {
        this.status = Status.RESTORING;
        updateTime();
    }
    
    /**
     * 标记为已恢复
     */
    public void markRestored() {
        this.status = Status.RESTORED;
        this.restoreTime = Instant.now();
        updateTime();
    }
    
    /**
     * 标记为已过期
     */
    public void markExpired() {
        this.status = Status.EXPIRED;
        updateTime();
    }
    
    /**
     * 标记为已删除
     */
    public void markDeleted() {
        this.status = Status.DELETED;
        updateTime();
    }
    
    /**
     * 检查是否有效
     */
    public boolean isValid() {
        return status == Status.ACTIVE;
    }
    
    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        if (expireTime != null) {
            return Instant.now().isAfter(expireTime);
        }
        return false;
    }
    
    // ========== 快照数据 Getter/Setter ==========
    
    public String getSceneGroupData() {
        return sceneGroupData;
    }
    
    public void setSceneGroupData(String sceneGroupData) {
        this.sceneGroupData = sceneGroupData;
        updateTime();
    }
    
    public List<Participant> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
        updateTime();
    }
    
    public List<CapabilityBinding> getCapabilityBindings() {
        return capabilityBindings;
    }
    
    public void setCapabilityBindings(List<CapabilityBinding> capabilityBindings) {
        this.capabilityBindings = capabilityBindings;
        updateTime();
    }
    
    public List<KnowledgeBinding> getKnowledgeBindings() {
        return knowledgeBindings;
    }
    
    public void setKnowledgeBindings(List<KnowledgeBinding> knowledgeBindings) {
        this.knowledgeBindings = knowledgeBindings;
        updateTime();
    }
    
    public Map<String, Object> getConfigData() {
        return configData;
    }
    
    public void setConfigData(Map<String, Object> configData) {
        this.configData = configData;
        updateTime();
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        updateTime();
    }
    
    // ========== 统计 ==========
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    /**
     * 估算快照大小
     */
    public void estimateSize() {
        long total = 0;
        if (sceneGroupData != null) {
            total += sceneGroupData.getBytes().length;
        }
        total += participants.size() * 1024; // 估算每个参与者1KB
        total += capabilityBindings.size() * 512; // 估算每个绑定512B
        total += knowledgeBindings.size() * 512;
        this.size = total;
    }
    
    public int getRefCount() {
        return refCount;
    }
    
    public void incrementRef() {
        refCount++;
    }
    
    public void decrementRef() {
        if (refCount > 0) {
            refCount--;
        }
    }
    
    // ========== 时间信息 ==========
    
    public Instant getCreateTime() {
        return createTime;
    }
    
    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    private void updateTime() {
        this.lastUpdateTime = Instant.now();
    }
    
    public Instant getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(Instant expireTime) {
        this.expireTime = expireTime;
        updateTime();
    }
    
    public Instant getRestoreTime() {
        return restoreTime;
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 获取快照摘要信息
     */
    public SnapshotSummary getSummary() {
        SnapshotSummary summary = new SnapshotSummary();
        summary.setSnapshotId(snapshotId);
        summary.setSceneGroupId(sceneGroupId);
        summary.setName(name);
        summary.setType(type);
        summary.setStatus(status);
        summary.setCreateTime(createTime);
        summary.setSize(size);
        summary.setParticipantCount(participants.size());
        summary.setCapabilityBindingCount(capabilityBindings.size());
        summary.setKnowledgeBindingCount(knowledgeBindings.size());
        return summary;
    }
    
    @Override
    public String toString() {
        return "SceneSnapshot{" +
                "snapshotId='" + snapshotId + '\'' +
                ", sceneGroupId='" + sceneGroupId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", size=" + size +
                ", createTime=" + createTime +
                '}';
    }
    
    /**
     * 快照摘要
     */
    public static class SnapshotSummary {
        private String snapshotId;
        private String sceneGroupId;
        private String name;
        private Type type;
        private Status status;
        private Instant createTime;
        private long size;
        private int participantCount;
        private int capabilityBindingCount;
        private int knowledgeBindingCount;
        
        // Getters and Setters
        public String getSnapshotId() { return snapshotId; }
        public void setSnapshotId(String snapshotId) { this.snapshotId = snapshotId; }
        
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Type getType() { return type; }
        public void setType(Type type) { this.type = type; }
        
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
        
        public Instant getCreateTime() { return createTime; }
        public void setCreateTime(Instant createTime) { this.createTime = createTime; }
        
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        
        public int getParticipantCount() { return participantCount; }
        public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }
        
        public int getCapabilityBindingCount() { return capabilityBindingCount; }
        public void setCapabilityBindingCount(int capabilityBindingCount) { this.capabilityBindingCount = capabilityBindingCount; }
        
        public int getKnowledgeBindingCount() { return knowledgeBindingCount; }
        public void setKnowledgeBindingCount(int knowledgeBindingCount) { this.knowledgeBindingCount = knowledgeBindingCount; }
    }
}
