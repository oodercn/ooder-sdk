package net.ooder.scene.group;

import net.ooder.scene.capability.CapabilityBinding;
import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.participant.Participant;
import net.ooder.scene.snapshot.SceneSnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 场景组
 * 
 * <p>SE原生的场景组模型，用于管理一个完整的业务场景，包含参与者、能力绑定、知识库等。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>场景组生命周期管理 - 创建、激活、停用、销毁</li>
 *   <li>参与者管理 - 加入、离开、角色变更</li>
 *   <li>能力绑定管理 - 绑定、解绑、更新</li>
 *   <li>知识库绑定管理 - 绑定、解绑</li>
 *   <li>快照管理 - 创建、恢复</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneGroup {
    
    /**
     * 场景组状态
     */
    public enum Status {
        CREATING,       // 创建中
        ACTIVE,         // 激活状态
        SUSPENDED,      // 暂停状态
        ARCHIVED,       // 已归档
        DESTROYING,     // 销毁中
        DESTROYED       // 已销毁
    }
    
    /**
     * 创建者类型
     */
    public enum CreatorType {
        USER,           // 用户创建
        AGENT,          // 代理创建
        SYSTEM          // 系统创建
    }
    
    // ========== 基础信息 ==========
    
    /** 场景组唯一标识 */
    private final String sceneGroupId;
    
    /** 模板ID */
    private final String templateId;
    
    /** 场景组名称 */
    private String name;
    
    /** 场景组描述 */
    private String description;
    
    /** 当前状态 */
    private final AtomicReference<Status> status = new AtomicReference<>(Status.CREATING);
    
    /** 创建者ID */
    private final String creatorId;
    
    /** 创建者类型 */
    private final CreatorType creatorType;
    
    // ========== 配置信息 ==========
    
    /** 配置属性 */
    private final Map<String, Object> config = new ConcurrentHashMap<>();
    
    /** LLM配置 */
    private final Map<String, Object> llmConfig = new ConcurrentHashMap<>();
    
    // ========== 关联数据 ==========
    
    /** 参与者列表 */
    private final List<Participant> participants = new CopyOnWriteArrayList<>();
    
    /** 参与者ID索引 */
    private final Map<String, Participant> participantIndex = new ConcurrentHashMap<>();
    
    /** 能力绑定列表 */
    private final List<CapabilityBinding> capabilityBindings = new CopyOnWriteArrayList<>();
    
    /** 能力绑定ID索引 */
    private final Map<String, CapabilityBinding> capabilityBindingIndex = new ConcurrentHashMap<>();
    
    /** 知识库绑定列表 */
    private final List<KnowledgeBinding> knowledgeBindings = new CopyOnWriteArrayList<>();
    
    /** 知识库绑定ID索引 */
    private final Map<String, KnowledgeBinding> knowledgeBindingIndex = new ConcurrentHashMap<>();
    
    /** 快照列表 */
    private final List<SceneSnapshot> snapshots = new CopyOnWriteArrayList<>();
    
    /** 快照ID索引 */
    private final Map<String, SceneSnapshot> snapshotIndex = new ConcurrentHashMap<>();
    
    /** 事件日志列表 */
    private final List<SceneGroupEvent> eventLog = new CopyOnWriteArrayList<>();
    
    /** 事件日志最大条数 */
    private static final int MAX_EVENT_LOG_SIZE = 1000;
    
    // ========== 统计信息 ==========
    
    /** 成员数量 */
    private final AtomicInteger memberCount = new AtomicInteger(0);
    
    /** 创建时间 */
    private final Instant createTime;
    
    /** 最后更新时间 */
    private volatile Instant lastUpdateTime;
    
    /**
     * 构造函数
     */
    public SceneGroup(String sceneGroupId, String templateId, String creatorId, CreatorType creatorType) {
        this.sceneGroupId = sceneGroupId;
        this.templateId = templateId;
        this.creatorId = creatorId;
        this.creatorType = creatorType;
        this.createTime = Instant.now();
        this.lastUpdateTime = this.createTime;
    }
    
    // ========== 基础信息 Getter ==========
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public String getTemplateId() {
        return templateId;
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
    
    public Status getStatus() {
        return status.get();
    }
    
    public String getCreatorId() {
        return creatorId;
    }
    
    public CreatorType getCreatorType() {
        return creatorType;
    }
    
    // ========== 状态管理 ==========
    
    /**
     * 激活场景组
     */
    public boolean activate() {
        boolean success = status.compareAndSet(Status.CREATING, Status.ACTIVE) ||
                         status.compareAndSet(Status.SUSPENDED, Status.ACTIVE);
        if (success) {
            updateTime();
        }
        return success;
    }
    
    /**
     * 暂停场景组
     */
    public boolean suspend() {
        boolean success = status.compareAndSet(Status.ACTIVE, Status.SUSPENDED);
        if (success) {
            updateTime();
        }
        return success;
    }
    
    /**
     * 归档场景组
     */
    public boolean archive() {
        boolean success = status.compareAndSet(Status.ACTIVE, Status.ARCHIVED) ||
                         status.compareAndSet(Status.SUSPENDED, Status.ARCHIVED);
        if (success) {
            updateTime();
        }
        return success;
    }
    
    /**
     * 从归档恢复
     */
    public boolean restoreFromArchive() {
        boolean success = status.compareAndSet(Status.ARCHIVED, Status.ACTIVE);
        if (success) {
            updateTime();
        }
        return success;
    }
    
    /**
     * 开始销毁场景组
     */
    public boolean startDestroying() {
        boolean success = status.compareAndSet(Status.ACTIVE, Status.DESTROYING) ||
                         status.compareAndSet(Status.SUSPENDED, Status.DESTROYING);
        if (success) {
            updateTime();
        }
        return success;
    }
    
    /**
     * 完成销毁
     */
    public boolean finishDestroying() {
        boolean success = status.compareAndSet(Status.DESTROYING, Status.DESTROYED);
        if (success) {
            updateTime();
            // 清理数据
            clearAllData();
        }
        return success;
    }
    
    /**
     * 检查是否可以激活
     */
    public boolean canActivate() {
        Status current = status.get();
        return current == Status.CREATING || current == Status.SUSPENDED;
    }
    
    /**
     * 检查是否已销毁
     */
    public boolean isDestroyed() {
        return status.get() == Status.DESTROYED;
    }
    
    // ========== 参与者管理 ==========
    
    /**
     * 添加参与者
     */
    public boolean addParticipant(Participant participant) {
        if (participant == null || participant.getParticipantId() == null) {
            return false;
        }
        
        String participantId = participant.getParticipantId();
        
        // 检查是否已存在
        if (participantIndex.containsKey(participantId)) {
            return false;
        }
        
        // 添加到列表和索引
        participants.add(participant);
        participantIndex.put(participantId, participant);
        memberCount.incrementAndGet();
        updateTime();
        
        return true;
    }
    
    /**
     * 移除参与者
     */
    public boolean removeParticipant(String participantId) {
        Participant participant = participantIndex.remove(participantId);
        if (participant != null) {
            participants.remove(participant);
            memberCount.decrementAndGet();
            updateTime();
            return true;
        }
        return false;
    }
    
    /**
     * 获取参与者
     */
    public Participant getParticipant(String participantId) {
        return participantIndex.get(participantId);
    }
    
    /**
     * 获取所有参与者
     */
    public List<Participant> getAllParticipants() {
        return new ArrayList<>(participants);
    }
    
    /**
     * 获取参与者数量
     */
    public int getParticipantCount() {
        return memberCount.get();
    }
    
    /**
     * 检查参与者是否存在
     */
    public boolean hasParticipant(String participantId) {
        return participantIndex.containsKey(participantId);
    }
    
    // ========== 能力绑定管理 ==========
    
    /**
     * 添加能力绑定
     */
    public boolean addCapabilityBinding(CapabilityBinding binding) {
        if (binding == null || binding.getBindingId() == null) {
            return false;
        }
        
        String bindingId = binding.getBindingId();
        
        if (capabilityBindingIndex.containsKey(bindingId)) {
            return false;
        }
        
        capabilityBindings.add(binding);
        capabilityBindingIndex.put(bindingId, binding);
        updateTime();
        
        return true;
    }
    
    /**
     * 移除能力绑定
     */
    public boolean removeCapabilityBinding(String bindingId) {
        CapabilityBinding binding = capabilityBindingIndex.remove(bindingId);
        if (binding != null) {
            capabilityBindings.remove(binding);
            updateTime();
            return true;
        }
        return false;
    }
    
    /**
     * 获取能力绑定
     */
    public CapabilityBinding getCapabilityBinding(String bindingId) {
        return capabilityBindingIndex.get(bindingId);
    }
    
    /**
     * 获取所有能力绑定
     */
    public List<CapabilityBinding> getAllCapabilityBindings() {
        return new ArrayList<>(capabilityBindings);
    }
    
    /**
     * 获取能力绑定数量
     */
    public int getCapabilityBindingCount() {
        return capabilityBindings.size();
    }
    
    // ========== 知识库绑定管理 ==========
    
    /**
     * 添加知识库绑定
     */
    public boolean addKnowledgeBinding(KnowledgeBinding binding) {
        if (binding == null || binding.getKbId() == null) {
            return false;
        }
        
        String kbId = binding.getKbId();
        
        if (knowledgeBindingIndex.containsKey(kbId)) {
            return false;
        }
        
        knowledgeBindings.add(binding);
        knowledgeBindingIndex.put(kbId, binding);
        updateTime();
        
        return true;
    }
    
    /**
     * 移除知识库绑定
     */
    public boolean removeKnowledgeBinding(String kbId) {
        KnowledgeBinding binding = knowledgeBindingIndex.remove(kbId);
        if (binding != null) {
            knowledgeBindings.remove(binding);
            updateTime();
            return true;
        }
        return false;
    }
    
    /**
     * 获取知识库绑定
     */
    public KnowledgeBinding getKnowledgeBinding(String kbId) {
        return knowledgeBindingIndex.get(kbId);
    }
    
    /**
     * 获取所有知识库绑定
     */
    public List<KnowledgeBinding> getAllKnowledgeBindings() {
        return new ArrayList<>(knowledgeBindings);
    }
    
    // ========== 快照管理 ==========
    
    /**
     * 添加快照
     */
    public boolean addSnapshot(SceneSnapshot snapshot) {
        if (snapshot == null || snapshot.getSnapshotId() == null) {
            return false;
        }
        
        String snapshotId = snapshot.getSnapshotId();
        
        if (snapshotIndex.containsKey(snapshotId)) {
            return false;
        }
        
        snapshots.add(snapshot);
        snapshotIndex.put(snapshotId, snapshot);
        updateTime();
        
        return true;
    }
    
    /**
     * 移除快照
     */
    public boolean removeSnapshot(String snapshotId) {
        SceneSnapshot snapshot = snapshotIndex.remove(snapshotId);
        if (snapshot != null) {
            snapshots.remove(snapshot);
            updateTime();
            return true;
        }
        return false;
    }
    
    /**
     * 获取快照
     */
    public SceneSnapshot getSnapshot(String snapshotId) {
        return snapshotIndex.get(snapshotId);
    }
    
    /**
     * 获取所有快照
     */
    public List<SceneSnapshot> getAllSnapshots() {
        return new ArrayList<>(snapshots);
    }
    
    // ========== 事件日志管理 ==========
    
    /**
     * 添加事件日志
     */
    public void addEvent(SceneGroupEvent event) {
        if (event == null) {
            return;
        }
        
        eventLog.add(0, event); // 最新事件放前面
        
        // 限制日志大小
        while (eventLog.size() > MAX_EVENT_LOG_SIZE) {
            eventLog.remove(eventLog.size() - 1);
        }
    }
    
    /**
     * 获取事件日志
     */
    public List<SceneGroupEvent> getEventLog(int limit) {
        int size = Math.min(limit, eventLog.size());
        return new ArrayList<>(eventLog.subList(0, size));
    }
    
    /**
     * 获取所有事件日志
     */
    public List<SceneGroupEvent> getAllEventLog() {
        return new ArrayList<>(eventLog);
    }
    
    /**
     * 记录参与者加入事件
     */
    public void logParticipantJoined(Participant participant) {
        SceneGroupEvent event = new SceneGroupEvent(
            sceneGroupId,
            SceneGroupEvent.Type.PARTICIPANT_JOINED,
            participant.getParticipantId(),
            "Participant joined: " + participant.getName()
        );
        addEvent(event);
    }
    
    /**
     * 记录参与者离开事件
     */
    public void logParticipantLeft(Participant participant) {
        SceneGroupEvent event = new SceneGroupEvent(
            sceneGroupId,
            SceneGroupEvent.Type.PARTICIPANT_LEFT,
            participant.getParticipantId(),
            "Participant left: " + participant.getName()
        );
        addEvent(event);
    }
    
    /**
     * 记录能力绑定事件
     */
    public void logCapabilityBound(CapabilityBinding binding) {
        SceneGroupEvent event = new SceneGroupEvent(
            sceneGroupId,
            SceneGroupEvent.Type.CAPABILITY_BOUND,
            binding.getBindingId(),
            "Capability bound: " + binding.getCapName()
        );
        addEvent(event);
    }
    
    /**
     * 记录状态变更事件
     */
    public void logStatusChanged(Status oldStatus, Status newStatus) {
        SceneGroupEvent event = new SceneGroupEvent(
            sceneGroupId,
            SceneGroupEvent.Type.STATUS_CHANGED,
            null,
            "Status changed: " + oldStatus + " -> " + newStatus
        );
        addEvent(event);
    }
    
    // ========== 配置管理 ==========
    
    /**
     * 设置配置属性
     */
    public void setConfig(String key, Object value) {
        config.put(key, value);
        updateTime();
    }
    
    /**
     * 获取配置属性
     */
    public Object getConfig(String key) {
        return config.get(key);
    }
    
    /**
     * 获取所有配置
     */
    public Map<String, Object> getAllConfig() {
        return new HashMap<>(config);
    }
    
    /**
     * 设置LLM配置
     */
    public void setLlmConfig(String key, Object value) {
        llmConfig.put(key, value);
        updateTime();
    }
    
    /**
     * 获取LLM配置
     */
    public Object getLlmConfig(String key) {
        return llmConfig.get(key);
    }
    
    /**
     * 获取所有LLM配置
     */
    public Map<String, Object> getAllLlmConfig() {
        return new HashMap<>(llmConfig);
    }
    
    // ========== 时间相关 ==========
    
    /**
     * 获取创建时间
     */
    public Instant getCreateTime() {
        return createTime;
    }
    
    /**
     * 获取最后更新时间
     */
    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    /**
     * 更新时间戳
     */
    private void updateTime() {
        this.lastUpdateTime = Instant.now();
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 清理所有数据（销毁时使用）
     */
    private void clearAllData() {
        participants.clear();
        participantIndex.clear();
        capabilityBindings.clear();
        capabilityBindingIndex.clear();
        knowledgeBindings.clear();
        knowledgeBindingIndex.clear();
        snapshots.clear();
        snapshotIndex.clear();
        config.clear();
        llmConfig.clear();
        memberCount.set(0);
    }
    
    @Override
    public String toString() {
        return "SceneGroup{" +
                "sceneGroupId='" + sceneGroupId + '\'' +
                ", templateId='" + templateId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status.get() +
                ", creatorId='" + creatorId + '\'' +
                ", participantCount=" + memberCount.get() +
                ", createTime=" + createTime +
                '}';
    }
}
