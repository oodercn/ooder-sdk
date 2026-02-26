package net.ooder.sdk.engine.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Engine 层事件基类
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>可中断</b>：监听者可以调用 cancel() 中断后续流程</li>
 *   <li><b>有状态</b>：包含调用者信息、审计日志等上下文</li>
 *   <li><b>可审计</b>：所有事件自动记录审计日志</li>
 *   <li><b>Spring 集成</b>：支持 Spring 事件机制</li>
 * </ul>
 *
 * <p>使用场景：权限检查、业务审计、流程控制等</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public abstract class EngineEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件唯一标识
     */
    private final String eventId;

    /**
     * 事件类型
     */
    private final String eventType;

    /**
     * 事件发生时间戳
     */
    private final long timestamp;

    /**
     * 事件版本
     */
    private final int version;

    /**
     * 调用者信息
     */
    private final CallerInfo callerInfo;

    /**
     * 是否被取消
     */
    private volatile boolean cancelled = false;

    /**
     * 取消原因
     */
    private volatile String cancelReason;

    /**
     * 扩展属性（用于传递上下文）
     */
    private final Map<String, Object> attributes;

    protected EngineEvent(CallerInfo callerInfo) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = this.getClass().getSimpleName();
        this.timestamp = System.currentTimeMillis();
        this.version = 1;
        this.callerInfo = callerInfo != null ? callerInfo : CallerInfo.SYSTEM;
        this.attributes = new HashMap<>();
    }

    // ==================== 访问器 ====================

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Instant getInstant() {
        return Instant.ofEpochMilli(timestamp);
    }

    public int getVersion() {
        return version;
    }

    public CallerInfo getCallerInfo() {
        return callerInfo;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }

    // ==================== 可中断操作 ====================

    /**
     * 取消事件（中断后续流程）
     *
     * @param reason 取消原因
     */
    public void cancel(String reason) {
        this.cancelled = true;
        this.cancelReason = reason;
    }

    /**
     * 设置扩展属性
     */
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    /**
     * 获取扩展属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    // ==================== 抽象方法 ====================

    /**
     * 获取事件描述
     */
    public abstract String getDescription();

    /**
     * 是否需要审计
     */
    public boolean isAuditable() {
        return true;
    }

    /**
     * 获取审计级别
     */
    public AuditLevel getAuditLevel() {
        return AuditLevel.INFO;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, caller=%s, cancelled=%s, time=%s]",
            eventType, eventId, callerInfo.getUserId(), cancelled, getInstant());
    }

    // ==================== 内部类 ====================

    /**
     * 调用者信息
     */
    public static class CallerInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        public static final CallerInfo SYSTEM = new CallerInfo("system", "SYSTEM", "127.0.0.1", null);

        /**
         * 用户 ID
         */
        private final String userId;

        /**
         * 用户名
         */
        private final String username;

        /**
         * IP 地址
         */
        private final String ipAddress;

        /**
         * 会话 Token
         */
        private final String sessionToken;

        public CallerInfo(String userId, String username, String ipAddress, String sessionToken) {
            this.userId = userId;
            this.username = username;
            this.ipAddress = ipAddress;
            this.sessionToken = sessionToken;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        @Override
        public String toString() {
            return String.format("CallerInfo[user=%s, ip=%s]", username, ipAddress);
        }
    }

    /**
     * 审计级别
     */
    public enum AuditLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }
}
