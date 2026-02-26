package net.ooder.sdk.engine.audit;

import net.ooder.sdk.engine.event.EngineEvent;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 审计日志存储接口
 *
 * <p>定义审计日志的存储和查询接口</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public interface AuditLogStorage {

    /**
     * 存储审计日志
     *
     * @param logEntry 审计日志条目
     */
    void store(AuditLogEntry logEntry);

    /**
     * 批量存储审计日志
     *
     * @param logEntries 审计日志条目列表
     */
    void storeBatch(List<AuditLogEntry> logEntries);

    /**
     * 根据事件 ID 查询审计日志
     *
     * @param eventId 事件 ID
     * @return 审计日志条目
     */
    AuditLogEntry findByEventId(String eventId);

    /**
     * 根据调用者 ID 查询审计日志
     *
     * @param callerId 调用者 ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志条目列表
     */
    List<AuditLogEntry> findByCallerId(String callerId, Instant startTime, Instant endTime);

    /**
     * 根据 Skill ID 查询审计日志
     *
     * @param skillId Skill ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志条目列表
     */
    List<AuditLogEntry> findBySkillId(String skillId, Instant startTime, Instant endTime);

    /**
     * 审计日志条目
     */
    class AuditLogEntry {
        private String logId;
        private String eventId;
        private String eventType;
        private String callerId;
        private String callerName;
        private String callerIp;
        private String skillId;
        private String capabilityId;
        private String description;
        private String level;
        private long timestamp;
        private boolean cancelled;
        private String cancelReason;
        private Map<String, Object> attributes;

        // Getters and Setters
        public String getLogId() { return logId; }
        public void setLogId(String logId) { this.logId = logId; }

        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }

        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }

        public String getCallerId() { return callerId; }
        public void setCallerId(String callerId) { this.callerId = callerId; }

        public String getCallerName() { return callerName; }
        public void setCallerName(String callerName) { this.callerName = callerName; }

        public String getCallerIp() { return callerIp; }
        public void setCallerIp(String callerIp) { this.callerIp = callerIp; }

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

        public String getCancelReason() { return cancelReason; }
        public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

        public Map<String, Object> getAttributes() { return attributes; }
        public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    }
}
