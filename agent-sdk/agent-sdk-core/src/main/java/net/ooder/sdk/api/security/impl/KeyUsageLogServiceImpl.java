package net.ooder.sdk.api.security.impl;

import net.ooder.sdk.api.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class KeyUsageLogServiceImpl implements KeyUsageLogService {
    
    private static final Logger log = LoggerFactory.getLogger(KeyUsageLogServiceImpl.class);
    
    private final Map<String, KeyUsageLog> logs;
    private final Map<String, List<String>> keyLogsIndex;
    private final Map<String, List<String>> sceneLogsIndex;
    private final Map<String, List<String>> operatorLogsIndex;
    
    public KeyUsageLogServiceImpl() {
        this.logs = new ConcurrentHashMap<String, KeyUsageLog>();
        this.keyLogsIndex = new ConcurrentHashMap<String, List<String>>();
        this.sceneLogsIndex = new ConcurrentHashMap<String, List<String>>();
        this.operatorLogsIndex = new ConcurrentHashMap<String, List<String>>();
    }
    
    @Override
    public KeyUsageLog recordLog(KeyUsageLog logEntry) {
        if (logEntry.getLogId() == null || logEntry.getLogId().isEmpty()) {
            logEntry.setLogId("log-" + UUID.randomUUID().toString());
        }
        
        logs.put(logEntry.getLogId(), logEntry);
        
        if (logEntry.getKeyId() != null) {
            keyLogsIndex.computeIfAbsent(logEntry.getKeyId(), k -> new ArrayList<String>()).add(logEntry.getLogId());
        }
        if (logEntry.getSceneGroupId() != null) {
            sceneLogsIndex.computeIfAbsent(logEntry.getSceneGroupId(), k -> new ArrayList<String>()).add(logEntry.getLogId());
        }
        if (logEntry.getOperatorId() != null) {
            operatorLogsIndex.computeIfAbsent(logEntry.getOperatorId(), k -> new ArrayList<String>()).add(logEntry.getLogId());
        }
        
        log.debug("Recorded usage log: id={}, keyId={}, operation={}", 
            logEntry.getLogId(), logEntry.getKeyId(), logEntry.getOperation());
        
        return logEntry;
    }
    
    @Override
    public KeyUsageLog getLog(String logId) {
        return logs.get(logId);
    }
    
    @Override
    public List<KeyUsageLog> getLogsByKey(String keyId) {
        List<String> logIds = keyLogsIndex.get(keyId);
        if (logIds == null) {
            return Collections.emptyList();
        }
        return logIds.stream()
            .map(logs::get)
            .filter(Objects::nonNull)
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<KeyUsageLog> getLogsByScene(String sceneGroupId) {
        List<String> logIds = sceneLogsIndex.get(sceneGroupId);
        if (logIds == null) {
            return Collections.emptyList();
        }
        return logIds.stream()
            .map(logs::get)
            .filter(Objects::nonNull)
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<KeyUsageLog> getLogsByOperator(String operatorId) {
        List<String> logIds = operatorLogsIndex.get(operatorId);
        if (logIds == null) {
            return Collections.emptyList();
        }
        return logIds.stream()
            .map(logs::get)
            .filter(Objects::nonNull)
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<KeyUsageLog> getRecentLogs(int limit) {
        return logs.values().stream()
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<KeyUsageLog> queryLogs(LogQueryRequest request) {
        return logs.values().stream()
            .filter(l -> matchQuery(l, request))
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .skip((long) request.getPageNum() * request.getPageSize())
            .limit(request.getPageSize())
            .collect(Collectors.toList());
    }
    
    @Override
    public void clearOldLogs(long beforeTimestamp) {
        List<String> toRemove = logs.values().stream()
            .filter(l -> l.getTimestamp() < beforeTimestamp)
            .map(KeyUsageLog::getLogId)
            .collect(Collectors.toList());
        
        for (String logId : toRemove) {
            KeyUsageLog removed = logs.remove(logId);
            if (removed != null) {
                if (removed.getKeyId() != null) {
                    List<String> keyLogs = keyLogsIndex.get(removed.getKeyId());
                    if (keyLogs != null) {
                        keyLogs.remove(logId);
                    }
                }
                if (removed.getSceneGroupId() != null) {
                    List<String> sceneLogs = sceneLogsIndex.get(removed.getSceneGroupId());
                    if (sceneLogs != null) {
                        sceneLogs.remove(logId);
                    }
                }
                if (removed.getOperatorId() != null) {
                    List<String> operatorLogs = operatorLogsIndex.get(removed.getOperatorId());
                    if (operatorLogs != null) {
                        operatorLogs.remove(logId);
                    }
                }
            }
        }
        
        log.info("Cleared {} old logs before timestamp {}", toRemove.size(), beforeTimestamp);
    }
    
    @Override
    public KeyUsageStats getStatsByKey(String keyId) {
        List<KeyUsageLog> keyLogs = getLogsByKey(keyId);
        
        KeyUsageStats stats = new KeyUsageStats();
        stats.setKeyId(keyId);
        stats.setTotalUsage(keyLogs.size());
        
        long successCount = keyLogs.stream().filter(KeyUsageLog::isSuccess).count();
        stats.setSuccessCount(successCount);
        stats.setFailureCount(keyLogs.size() - successCount);
        
        long todayStart = getTodayStart();
        long todayUsage = keyLogs.stream()
            .filter(l -> l.getTimestamp() >= todayStart)
            .count();
        stats.setTodayUsage(todayUsage);
        
        double avgDurationDouble = keyLogs.stream()
            .mapToLong(KeyUsageLog::getDuration)
            .filter(d -> d > 0)
            .average()
            .orElse(0.0);
        stats.setAvgDuration((long) avgDurationDouble);
        
        Optional<KeyUsageLog> lastLog = keyLogs.stream().findFirst();
        stats.setLastUsedAt(lastLog.map(l -> new Date(l.getTimestamp()).toString()).orElse(null));
        
        return stats;
    }
    
    @Override
    public KeyUsageStats getOverallStats() {
        KeyUsageStats stats = new KeyUsageStats();
        stats.setKeyId("OVERALL");
        stats.setTotalUsage(logs.size());
        
        long successCount = logs.values().stream().filter(KeyUsageLog::isSuccess).count();
        stats.setSuccessCount(successCount);
        stats.setFailureCount(logs.size() - successCount);
        
        long todayStart = getTodayStart();
        long todayUsage = logs.values().stream()
            .filter(l -> l.getTimestamp() >= todayStart)
            .count();
        stats.setTodayUsage(todayUsage);
        
        double avgDurationDoubleOverall = logs.values().stream()
            .mapToLong(KeyUsageLog::getDuration)
            .filter(d -> d > 0)
            .average()
            .orElse(0.0);
        stats.setAvgDuration((long) avgDurationDoubleOverall);
        
        Optional<KeyUsageLog> lastLog = logs.values().stream()
            .max((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
        stats.setLastUsedAt(lastLog.map(l -> new Date(l.getTimestamp()).toString()).orElse(null));
        
        return stats;
    }
    
    private boolean matchQuery(KeyUsageLog l, LogQueryRequest request) {
        if (request.getKeyId() != null && !request.getKeyId().equals(l.getKeyId())) {
            return false;
        }
        if (request.getOperatorId() != null && !request.getOperatorId().equals(l.getOperatorId())) {
            return false;
        }
        if (request.getSceneGroupId() != null && !request.getSceneGroupId().equals(l.getSceneGroupId())) {
            return false;
        }
        if (request.getStartTime() != null && l.getTimestamp() < request.getStartTime()) {
            return false;
        }
        if (request.getEndTime() != null && l.getTimestamp() > request.getEndTime()) {
            return false;
        }
        if (request.getSuccess() != null && request.getSuccess() != l.isSuccess()) {
            return false;
        }
        if (request.getOperation() != null && !request.getOperation().equals(l.getOperation())) {
            return false;
        }
        return true;
    }
    
    private long getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
