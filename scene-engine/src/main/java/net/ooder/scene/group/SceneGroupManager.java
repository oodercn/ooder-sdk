package net.ooder.scene.group;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.SceneEventType;
import net.ooder.scene.event.scenegroup.SceneGroupAuditEvent;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 场景组管理器
 * 
 * <p>SE原生的场景组管理器，负责管理所有场景组的生命周期和状态。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>场景组的创建、销毁</li>
 *   <li>场景组的激活、暂停</li>
 *   <li>参与者心跳检测</li>
 *   <li>场景组状态维护</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Component
public class SceneGroupManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SceneGroupManager.class);
    
    /** 场景组存储 */
    private final Map<String, SceneGroup> sceneGroups = new ConcurrentHashMap<>();
    
    /** 模板ID索引 */
    private final Map<String, List<SceneGroup>> templateIndex = new ConcurrentHashMap<>();
    
    /** 调度器 */
    private ScheduledExecutorService scheduler;
    
    /** 事件发布器 */
    private final SceneEventPublisher eventPublisher;
    
    /** 心跳检查间隔（秒） */
    private int heartbeatCheckInterval = 60;
    
    /** 心跳超时时间（秒） */
    private int heartbeatTimeout = 300;
    
    public SceneGroupManager(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @PostConstruct
    public void init() {
        logger.info("初始化 SceneGroupManager");
        scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "SceneGroup-Scheduler");
            t.setDaemon(true);
            return t;
        });
        
        // 启动心跳检测任务
        scheduler.scheduleAtFixedRate(
            this::checkHeartbeats,
            heartbeatCheckInterval,
            heartbeatCheckInterval,
            TimeUnit.SECONDS
        );
        
        // 启动状态维护任务
        scheduler.scheduleAtFixedRate(
            this::maintainStatus,
            30,
            30,
            TimeUnit.SECONDS
        );
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("销毁 SceneGroupManager");
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
        
        // 清理所有场景组
        sceneGroups.clear();
        templateIndex.clear();
    }
    
    // ========== 场景组管理 ==========
    
    /**
     * 创建场景组
     */
    public SceneGroup createSceneGroup(String sceneGroupId, String templateId, String creatorId, 
                                       SceneGroup.CreatorType creatorType) {
        if (sceneGroups.containsKey(sceneGroupId)) {
            throw new IllegalArgumentException("SceneGroup already exists: " + sceneGroupId);
        }
        
        SceneGroup sceneGroup = new SceneGroup(sceneGroupId, templateId, creatorId, creatorType);
        sceneGroups.put(sceneGroupId, sceneGroup);
        
        templateIndex.computeIfAbsent(templateId, k -> new CopyOnWriteArrayList<>()).add(sceneGroup);
        
        logger.info("Created SceneGroup: {}", sceneGroupId);
        
        publishAuditEvent(SceneEventType.SCENE_GROUP_CREATED, sceneGroupId, null, creatorId, null, true);
        
        return sceneGroup;
    }
    
    /**
     * 获取场景组
     */
    public SceneGroup getSceneGroup(String sceneGroupId) {
        return sceneGroups.get(sceneGroupId);
    }
    
    /**
     * 获取所有场景组
     */
    public List<SceneGroup> getAllSceneGroups() {
        return Collections.unmodifiableList(new ArrayList<>(sceneGroups.values()));
    }
    
    /**
     * 获取模板下的所有场景组
     */
    public List<SceneGroup> getSceneGroupsByTemplate(String templateId) {
        return templateIndex.getOrDefault(templateId, Collections.emptyList());
    }
    
    /**
     * 激活场景组
     */
    public boolean activateSceneGroup(String sceneGroupId) {
        SceneGroup sceneGroup = sceneGroups.get(sceneGroupId);
        if (sceneGroup == null) {
            return false;
        }
        
        boolean success = sceneGroup.activate();
        if (success) {
            logger.info("Activated SceneGroup: {}", sceneGroupId);
            publishAuditEvent(SceneEventType.SCENE_GROUP_ACTIVATED, sceneGroupId, null, null, null, true);
        }
        return success;
    }
    
    /**
     * 暂停场景组
     */
    public boolean suspendSceneGroup(String sceneGroupId) {
        SceneGroup sceneGroup = sceneGroups.get(sceneGroupId);
        if (sceneGroup == null) {
            return false;
        }
        
        boolean success = sceneGroup.suspend();
        if (success) {
            logger.info("Suspended SceneGroup: {}", sceneGroupId);
            publishAuditEvent(SceneEventType.SCENE_GROUP_SUSPENDED, sceneGroupId, null, null, null, true);
        }
        return success;
    }
    
    /**
     * 销毁场景组
     */
    public boolean destroySceneGroup(String sceneGroupId) {
        SceneGroup sceneGroup = sceneGroups.get(sceneGroupId);
        if (sceneGroup == null) {
            return false;
        }
        
        boolean success = sceneGroup.startDestroying();
        if (success) {
            sceneGroup.finishDestroying();
            sceneGroups.remove(sceneGroupId);
            
            List<SceneGroup> groups = templateIndex.get(sceneGroup.getTemplateId());
            if (groups != null) {
                groups.remove(sceneGroup);
            }
            
            logger.info("Destroyed SceneGroup: {}", sceneGroupId);
            publishAuditEvent(SceneEventType.SCENE_GROUP_DESTROYED, sceneGroupId, null, null, null, true);
        }
        return success;
    }
    
    // ========== 参与者管理 ==========
    
    /**
     * 添加参与者
     */
    public boolean addParticipant(String sceneGroupId, Participant participant) {
        SceneGroup sceneGroup = sceneGroups.get(sceneGroupId);
        if (sceneGroup == null) {
            return false;
        }
        
        boolean success = sceneGroup.addParticipant(participant);
        if (success) {
            logger.info("Added participant {} to SceneGroup {}", 
                participant.getParticipantId(), sceneGroupId);
            publishAuditEvent(SceneEventType.SCENE_GROUP_PARTICIPANT_ADDED, 
                sceneGroupId, null, null, participant.getParticipantId(), true);
        }
        return success;
    }
    
    /**
     * 移除参与者
     */
    public boolean removeParticipant(String sceneGroupId, String participantId) {
        SceneGroup sceneGroup = sceneGroups.get(sceneGroupId);
        if (sceneGroup == null) {
            return false;
        }
        
        boolean success = sceneGroup.removeParticipant(participantId);
        if (success) {
            logger.info("Removed participant {} from SceneGroup {}", 
                participantId, sceneGroupId);
            publishAuditEvent(SceneEventType.SCENE_GROUP_PARTICIPANT_REMOVED, 
                sceneGroupId, null, null, participantId, true);
        }
        return success;
    }
    
    /**
     * 获取参与者
     */
    public Participant getParticipant(String sceneGroupId, String participantId) {
        SceneGroup sceneGroup = sceneGroups.get(sceneGroupId);
        if (sceneGroup == null) {
            return null;
        }
        return sceneGroup.getParticipant(participantId);
    }
    
    /**
     * 获取场景组事件日志
     * 
     * @param sceneGroupId 场景组ID
     * @param limit 限制条数
     * @return 事件日志列表
     */
    public List<SceneGroupEvent> getEventLog(String sceneGroupId, int limit) {
        SceneGroup sceneGroup = sceneGroups.get(sceneGroupId);
        if (sceneGroup == null) {
            return new ArrayList<>();
        }
        return sceneGroup.getEventLog(limit);
    }
    
    /**
     * 获取用户参与的所有场景组
     * 
     * @param userId 用户ID
     * @return 场景组列表
     */
    public List<SceneGroup> getUserSceneGroups(String userId) {
        List<SceneGroup> result = new ArrayList<>();
        for (SceneGroup group : sceneGroups.values()) {
            Participant participant = group.getParticipant(userId);
            if (participant != null) {
                result.add(group);
            }
        }
        return result;
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 检查心跳
     */
    private void checkHeartbeats() {
        long now = System.currentTimeMillis() / 1000;
        
        for (SceneGroup sceneGroup : sceneGroups.values()) {
            for (Participant participant : sceneGroup.getAllParticipants()) {
                if (participant.getStatus() == Participant.Status.ACTIVE) {
                    // 检查是否超时
                    if (!participant.isOnline()) {
                        // 心跳超时，标记为离线
                        logger.warn("Participant {} in SceneGroup {} heartbeat timeout",
                            participant.getParticipantId(), sceneGroup.getSceneGroupId());
                        // 可以在这里触发离线事件
                    }
                }
            }
        }
    }
    
    /**
     * 维护状态
     */
    private void maintainStatus() {
        for (SceneGroup sceneGroup : sceneGroups.values()) {
            // 检查场景组是否需要清理
            if (sceneGroup.getStatus() == SceneGroup.Status.DESTROYED) {
                // 已经销毁的场景组，从内存中移除
                if (sceneGroup.getLastUpdateTime().getEpochSecond() < 
                    System.currentTimeMillis() / 1000 - 3600) { // 1小时后清理
                    sceneGroups.remove(sceneGroup.getSceneGroupId());
                    logger.info("Cleaned up destroyed SceneGroup: {}", sceneGroup.getSceneGroupId());
                }
            }
        }
    }
    
    // ========== SeSceneGroup 管理 ==========
    
    private final Map<String, SeSceneGroup> seSceneGroups = new ConcurrentHashMap<>();
    
    /**
     * 获取 SE 简化版 SceneGroup
     */
    public SeSceneGroup getSeSceneGroupRef(String sceneGroupId) {
        return seSceneGroups.get(sceneGroupId);
    }
    
    /**
     * 获取或创建 SE 简化版 SceneGroup
     */
    public SeSceneGroup getOrCreateSeSceneGroup(String sdkSceneGroupId, String sceneId) {
        return seSceneGroups.computeIfAbsent(sdkSceneGroupId, 
            id -> new SeSceneGroup(id, sceneId));
    }
    
    /**
     * 移除 SE 简化版 SceneGroup
     */
    public void removeSeSceneGroup(String sceneGroupId) {
        seSceneGroups.remove(sceneGroupId);
    }
    
    private void publishAuditEvent(SceneEventType eventType, String groupId, String groupName, 
                                   String operatorId, String participantId, boolean success) {
        if (eventPublisher != null) {
            SceneGroupAuditEvent event = SceneGroupAuditEvent.builder()
                .source(this)
                .eventType(eventType)
                .groupId(groupId)
                .groupName(groupName)
                .operatorId(operatorId)
                .participantId(participantId)
                .success(success)
                .build();
            eventPublisher.publish(event);
        }
    }
}
