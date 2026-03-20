package net.ooder.scene.participant;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 参与者
 * 
 * <p>SE原生的参与者模型，表示场景组中的一个参与者（用户、代理等）。</p>
 * 
 * <h3>核心职责：</h3>
 * <ul>
 *   <li>维护参与者基本信息</li>
 *   <li>管理参与者在场景组中的状态</li>
 *   <li>记录参与者的心跳信息</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class Participant {
    
    /**
     * 参与者类型
     */
    public enum Type {
        USER,           // 用户
        AGENT,          // 代理
        SUPER_AGENT     // 超级代理
    }
    
    /**
     * 参与者角色
     * 
     * <p>与 SDK Participant.Role 保持一致</p>
     */
    public enum Role {
        OWNER,          // 所有者
        MANAGER,        // 管理员
        COORDINATOR,    // 协调者
        EMPLOYEE,       // 员工
        OBSERVER,       // 观察者
        LLM_ASSISTANT   // LLM助手
    }
    
    /**
     * 参与者状态
     */
    public enum Status {
        INVITED,        // 已邀请
        JOINED,         // 已加入
        ACTIVE,         // 活跃中
        LEFT,           // 已离开
        SUSPENDED,      // 已暂停
        REMOVED         // 已移除
    }
    
    // ========== 基础信息 ==========
    
    /** 参与者ID */
    private final String participantId;
    
    /** 用户ID */
    private final String userId;
    
    /** 参与者名称 */
    private String name;
    
    /** 参与者类型 */
    private final Type type;
    
    /** 参与者角色 */
    private final AtomicReference<Role> role = new AtomicReference<>(Role.EMPLOYEE);
    
    /** 当前状态 */
    private final AtomicReference<Status> status = new AtomicReference<>(Status.INVITED);
    
    // ========== 时间信息 ==========
    
    /** 加入时间 */
    private volatile Instant joinTime;
    
    /** 最后心跳时间 */
    private volatile Instant lastHeartbeat;
    
    /** 离开时间 */
    private volatile Instant leaveTime;
    
    // ========== 统计信息 ==========
    
    /** 心跳次数 */
    private volatile long heartbeatCount = 0;
    
    /** 在线时长（秒） */
    private volatile long onlineDuration = 0;
    
    /**
     * 构造函数
     */
    public Participant(String participantId, String userId, String name, Type type) {
        this.participantId = participantId;
        this.userId = userId;
        this.name = name;
        this.type = type;
    }
    
    // ========== Getter ==========
    
    public String getParticipantId() {
        return participantId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Type getType() {
        return type;
    }
    
    public Role getRole() {
        return role.get();
    }
    
    public boolean setRole(Role newRole) {
        return role.compareAndSet(getRole(), newRole) || role.getAndSet(newRole) != newRole;
    }
    
    public Status getStatus() {
        return status.get();
    }
    
    // ========== 状态转换 ==========
    
    /**
     * 接受邀请加入
     */
    public boolean join() {
        boolean success = status.compareAndSet(Status.INVITED, Status.JOINED);
        if (success) {
            joinTime = Instant.now();
            updateOnlineDuration();
        }
        return success;
    }
    
    /**
     * 标记为活跃状态
     */
    public boolean activate() {
        boolean success = status.compareAndSet(Status.JOINED, Status.ACTIVE) ||
                         status.compareAndSet(Status.SUSPENDED, Status.ACTIVE);
        if (success && lastHeartbeat == null) {
            lastHeartbeat = Instant.now();
        }
        return success;
    }
    
    /**
     * 暂停参与者
     */
    public boolean suspend() {
        boolean success = status.compareAndSet(Status.ACTIVE, Status.SUSPENDED);
        if (success) {
            updateOnlineDuration();
        }
        return success;
    }
    
    /**
     * 离开场景组
     */
    public boolean leave() {
        Status current = status.get();
        boolean success = current == Status.JOINED || current == Status.ACTIVE || current == Status.SUSPENDED;
        
        if (success && status.compareAndSet(current, Status.LEFT)) {
            leaveTime = Instant.now();
            updateOnlineDuration();
            return true;
        }
        return false;
    }
    
    /**
     * 被移除
     */
    public boolean remove() {
        Status current = status.get();
        boolean success = current != Status.LEFT && current != Status.REMOVED;
        
        if (success && status.compareAndSet(current, Status.REMOVED)) {
            if (leaveTime == null) {
                leaveTime = Instant.now();
            }
            updateOnlineDuration();
            return true;
        }
        return false;
    }
    
    // ========== 心跳管理 ==========
    
    /**
     * 更新心跳
     */
    public void heartbeat() {
        Instant now = Instant.now();
        
        // 更新在线时长
        if (lastHeartbeat != null && status.get() == Status.ACTIVE) {
            long duration = now.getEpochSecond() - lastHeartbeat.getEpochSecond();
            onlineDuration += duration;
        }
        
        lastHeartbeat = now;
        heartbeatCount++;
    }
    
    /**
     * 检查是否在线
     */
    public boolean isOnline() {
        if (lastHeartbeat == null) {
            return false;
        }
        
        // 5分钟内有心跳认为在线
        long lastHeartbeatTime = lastHeartbeat.getEpochSecond();
        long now = Instant.now().getEpochSecond();
        return (now - lastHeartbeatTime) < 300;
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 更新在线时长
     */
    private void updateOnlineDuration() {
        if (lastHeartbeat != null && status.get() == Status.ACTIVE) {
            long now = Instant.now().getEpochSecond();
            long duration = now - lastHeartbeat.getEpochSecond();
            onlineDuration += duration;
        }
    }
    
    // ========== Getter for time and stats ==========
    
    public Instant getJoinTime() {
        return joinTime;
    }
    
    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public Instant getLeaveTime() {
        return leaveTime;
    }
    
    public long getHeartbeatCount() {
        return heartbeatCount;
    }
    
    public long getOnlineDuration() {
        // 如果当前活跃，实时计算
        if (status.get() == Status.ACTIVE && lastHeartbeat != null) {
            long now = Instant.now().getEpochSecond();
            long currentSession = now - lastHeartbeat.getEpochSecond();
            return onlineDuration + currentSession;
        }
        return onlineDuration;
    }
    
    @Override
    public String toString() {
        return "Participant{" +
                "participantId='" + participantId + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", role=" + role.get() +
                ", status=" + status.get() +
                ", heartbeatCount=" + heartbeatCount +
                '}';
    }
}
