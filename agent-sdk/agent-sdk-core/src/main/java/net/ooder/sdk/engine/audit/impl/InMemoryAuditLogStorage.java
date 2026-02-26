package net.ooder.sdk.engine.audit.impl;

import net.ooder.sdk.engine.audit.AuditLogStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 内存审计日志存储实现
 *
 * <p>用于开发和测试环境，生产环境应使用数据库存储</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
@Component
public class InMemoryAuditLogStorage implements AuditLogStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryAuditLogStorage.class);

    /**
     * 日志存储：eventId -> AuditLogEntry
     */
    private final Map<String, AuditLogEntry> logStore = new ConcurrentHashMap<>();

    /**
     * 调用者索引：callerId -> eventId 列表
     */
    private final Map<String, List<String>> callerIndex = new ConcurrentHashMap<>();

    /**
     * Skill 索引：skillId -> eventId 列表
     */
    private final Map<String, List<String>> skillIndex = new ConcurrentHashMap<>();

    /**
     * 最大存储条目数
     */
    private static final int MAX_ENTRIES = 10000;

    @PostConstruct
    public void init() {
        log.info("InMemoryAuditLogStorage initialized (max entries: {})", MAX_ENTRIES);
    }

    @Override
    public void store(AuditLogEntry logEntry) {
        if (logEntry == null) {
            return;
        }

        // 清理旧数据
        if (logStore.size() >= MAX_ENTRIES) {
            cleanupOldEntries();
        }

        // 存储日志
        logStore.put(logEntry.getEventId(), logEntry);

        // 更新索引
        callerIndex.computeIfAbsent(logEntry.getCallerId(), k -> new CopyOnWriteArrayList<>())
                   .add(logEntry.getEventId());

        if (logEntry.getSkillId() != null) {
            skillIndex.computeIfAbsent(logEntry.getSkillId(), k -> new CopyOnWriteArrayList<>())
                      .add(logEntry.getEventId());
        }

        log.debug("Audit log stored: eventId={}, caller={}, skill={}",
            logEntry.getEventId(), logEntry.getCallerId(), logEntry.getSkillId());
    }

    @Override
    public void storeBatch(List<AuditLogEntry> logEntries) {
        if (logEntries == null || logEntries.isEmpty()) {
            return;
        }

        for (AuditLogEntry entry : logEntries) {
            store(entry);
        }

        log.debug("Batch stored {} audit logs", logEntries.size());
    }

    @Override
    public AuditLogEntry findByEventId(String eventId) {
        return logStore.get(eventId);
    }

    @Override
    public List<AuditLogEntry> findByCallerId(String callerId, Instant startTime, Instant endTime) {
        List<String> eventIds = callerIndex.get(callerId);
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyList();
        }

        return eventIds.stream()
                       .map(logStore::get)
                       .filter(Objects::nonNull)
                       .filter(entry -> isInTimeRange(entry, startTime, endTime))
                       .sorted(Comparator.comparingLong(AuditLogEntry::getTimestamp).reversed())
                       .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogEntry> findBySkillId(String skillId, Instant startTime, Instant endTime) {
        List<String> eventIds = skillIndex.get(skillId);
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyList();
        }

        return eventIds.stream()
                       .map(logStore::get)
                       .filter(Objects::nonNull)
                       .filter(entry -> isInTimeRange(entry, startTime, endTime))
                       .sorted(Comparator.comparingLong(AuditLogEntry::getTimestamp).reversed())
                       .collect(Collectors.toList());
    }

    /**
     * 检查时间范围
     */
    private boolean isInTimeRange(AuditLogEntry entry, Instant startTime, Instant endTime) {
        Instant entryTime = Instant.ofEpochMilli(entry.getTimestamp());

        if (startTime != null && entryTime.isBefore(startTime)) {
            return false;
        }

        if (endTime != null && entryTime.isAfter(endTime)) {
            return false;
        }

        return true;
    }

    /**
     * 清理旧条目
     */
    private void cleanupOldEntries() {
        // 删除最旧的 20% 条目
        int toRemove = MAX_ENTRIES / 5;

        List<String> sortedEventIds = logStore.values().stream()
            .sorted(Comparator.comparingLong(AuditLogEntry::getTimestamp))
            .limit(toRemove)
            .map(AuditLogEntry::getEventId)
            .collect(Collectors.toList());

        for (String eventId : sortedEventIds) {
            AuditLogEntry entry = logStore.remove(eventId);
            if (entry != null) {
                // 从索引中移除
                List<String> callerEvents = callerIndex.get(entry.getCallerId());
                if (callerEvents != null) {
                    callerEvents.remove(eventId);
                }

                if (entry.getSkillId() != null) {
                    List<String> skillEvents = skillIndex.get(entry.getSkillId());
                    if (skillEvents != null) {
                        skillEvents.remove(eventId);
                    }
                }
            }
        }

        log.info("Cleaned up {} old audit log entries", toRemove);
    }

    /**
     * 获取当前存储的日志数量
     */
    public int getEntryCount() {
        return logStore.size();
    }

    /**
     * 清空所有日志
     */
    public void clear() {
        logStore.clear();
        callerIndex.clear();
        skillIndex.clear();
        log.info("All audit logs cleared");
    }
}
