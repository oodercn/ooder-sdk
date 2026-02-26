package net.ooder.sdk.core.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Core 层事件基类
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>无状态</b>：事件对象不可变，发布后不能被修改</li>
 *   <li><b>只观察</b>：监听者只能观察，不能中断流程</li>
 *   <li><b>原生调用</b>：对应程序内部的原生方法调用</li>
 *   <li><b>高性能</b>：最小化内存分配，支持高频触发</li>
 * </ul>
 *
 * <p>使用场景：Skill 内部状态变更、Agent 生命周期、Link 连接状态等核心事件</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public abstract class CoreEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件唯一标识（不可变）
     */
    private final String eventId;

    /**
     * 事件类型（不可变）
     */
    private final String eventType;

    /**
     * 事件源标识（不可变）
     */
    private final String source;

    /**
     * 事件发生时间戳（不可变）
     */
    private final long timestamp;

    /**
     * 事件版本（用于兼容性）
     */
    private final int version;

    protected CoreEvent(String source) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = this.getClass().getSimpleName();
        this.source = source;
        this.timestamp = System.currentTimeMillis();
        this.version = 1;
    }

    protected CoreEvent(String source, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = this.getClass().getSimpleName();
        this.source = source;
        this.timestamp = System.currentTimeMillis();
        this.version = version;
    }

    // ==================== 只读访问器 ====================

    public final String getEventId() {
        return eventId;
    }

    public final String getEventType() {
        return eventType;
    }

    public final String getSource() {
        return source;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public final int getVersion() {
        return version;
    }

    /**
     * 获取事件发生时间（Instant 格式）
     */
    public final Instant getInstant() {
        return Instant.ofEpochMilli(timestamp);
    }

    // ==================== 抽象方法 ====================

    /**
     * 获取事件描述（用于日志）
     */
    public abstract String getDescription();

    /**
     * 获取事件优先级（用于过滤和排序）
     */
    public EventPriority getPriority() {
        return EventPriority.NORMAL;
    }

    // ==================== 禁止修改 ====================

    /**
     * Core 事件不允许被取消
     */
    public final boolean isCancellable() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, source=%s, time=%s]",
            eventType, eventId, source, getInstant());
    }

    /**
     * 事件优先级枚举
     */
    public enum EventPriority {
        LOW(0),
        NORMAL(1),
        HIGH(2),
        CRITICAL(3);

        private final int level;

        EventPriority(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }
}
