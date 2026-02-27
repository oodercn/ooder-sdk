package net.ooder.scene.core.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * LogProvider核心实现
 *
 * <p>SEC Engine内置实现，提供日志存储、查询、导出等功能</p>
 */
public class LogProviderImpl implements LogProvider {

    private static final String PROVIDER_NAME = "log-provider";
    private static final String VERSION = "1.0.0";
    private static final int MAX_LOGS = 10000;

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    
    private final List<LogEntry> logs = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong logIdGenerator = new AtomicLong(0);
    private final Map<String, AtomicLong> levelCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> sourceCounts = new ConcurrentHashMap<>();

    public LogProviderImpl() {
        levelCounts.put("ERROR", new AtomicLong(0));
        levelCounts.put("WARN", new AtomicLong(0));
        levelCounts.put("INFO", new AtomicLong(0));
        levelCounts.put("DEBUG", new AtomicLong(0));
        levelCounts.put("TRACE", new AtomicLong(0));
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
        writeLog("INFO", "LogProvider started", PROVIDER_NAME);
    }

    @Override
    public void stop() {
        writeLog("INFO", "LogProvider stopped", PROVIDER_NAME);
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public Result<Boolean> writeLog(String level, String message, String source) {
        return writeLog(level, message, source, null);
    }

    @Override
    public Result<Boolean> writeLog(String level, String message, String source, Map<String, Object> details) {
        try {
            LogEntry entry = new LogEntry();
            entry.setLogId("log-" + logIdGenerator.incrementAndGet());
            entry.setLevel(level != null ? level.toUpperCase() : "INFO");
            entry.setMessage(message);
            entry.setSource(source != null ? source : "unknown");
            entry.setTimestamp(System.currentTimeMillis());
            entry.setThreadName(Thread.currentThread().getName());
            entry.setDetails(details);
            
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 3) {
                entry.setLoggerName(stackTrace[3].getClassName());
            }
            
            synchronized (logs) {
                if (logs.size() >= MAX_LOGS) {
                    logs.remove(0);
                }
                logs.add(entry);
            }
            
            AtomicLong levelCount = levelCounts.get(entry.getLevel());
            if (levelCount != null) {
                levelCount.incrementAndGet();
            }
            
            sourceCounts.computeIfAbsent(source, k -> new AtomicLong(0)).incrementAndGet();
            
            return Result.success(true);
        } catch (Exception e) {
            return Result.error("Failed to write log: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResult<LogEntry>> queryLogs(LogQuery query) {
        try {
            List<LogEntry> filteredLogs = new ArrayList<>(logs);
            
            if (query.getLevel() != null && !query.getLevel().isEmpty()) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> query.getLevel().equalsIgnoreCase(log.getLevel()))
                    .collect(Collectors.toList());
            }
            
            if (query.getSource() != null && !query.getSource().isEmpty()) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> query.getSource().equals(log.getSource()))
                    .collect(Collectors.toList());
            }
            
            if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
                String keyword = query.getKeyword().toLowerCase();
                filteredLogs = filteredLogs.stream()
                    .filter(log -> log.getMessage().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
            }
            
            if (query.getStartTime() != null) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> log.getTimestamp() >= query.getStartTime())
                    .collect(Collectors.toList());
            }
            
            if (query.getEndTime() != null) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> log.getTimestamp() <= query.getEndTime())
                    .collect(Collectors.toList());
            }
            
            if ("desc".equalsIgnoreCase(query.getSortOrder())) {
                Collections.reverse(filteredLogs);
            }
            
            int total = filteredLogs.size();
            int page = query.getPage();
            int size = query.getSize();
            int start = (page - 1) * size;
            int end = Math.min(start + size, total);
            
            List<LogEntry> pagedLogs = start < total ? 
                filteredLogs.subList(start, end) : new ArrayList<>();
            
            PageResult<LogEntry> result = new PageResult<>();
            result.setItems(pagedLogs);
            result.setTotal(total);
            result.setPageNum(page);
            result.setPageSize(size);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Failed to query logs: " + e.getMessage());
        }
    }

    @Override
    public Result<LogEntry> getLog(String logId) {
        synchronized (logs) {
            for (LogEntry entry : logs) {
                if (logId.equals(entry.getLogId())) {
                    return Result.success(entry);
                }
            }
        }
        return Result.notFound("Log not found: " + logId);
    }

    @Override
    public Result<Boolean> deleteLog(String logId) {
        synchronized (logs) {
            Iterator<LogEntry> iterator = logs.iterator();
            while (iterator.hasNext()) {
                LogEntry entry = iterator.next();
                if (logId.equals(entry.getLogId())) {
                    iterator.remove();
                    return Result.success(true);
                }
            }
        }
        return Result.success(false);
    }

    @Override
    public Result<Long> clearLogs(long beforeTime) {
        long count = 0;
        synchronized (logs) {
            Iterator<LogEntry> iterator = logs.iterator();
            while (iterator.hasNext()) {
                LogEntry entry = iterator.next();
                if (entry.getTimestamp() < beforeTime) {
                    iterator.remove();
                    count++;
                }
            }
        }
        return Result.success(count);
    }

    @Override
    public Result<LogExportResult> exportLogs(LogQuery query, String format) {
        try {
            Result<PageResult<LogEntry>> queryResult = queryLogs(query);
            if (!queryResult.isSuccess()) {
                return Result.error(queryResult.getError());
            }
            
            List<LogEntry> logsToExport = queryResult.getData().getItems();
            String content;
            String fileName;
            
            if ("csv".equalsIgnoreCase(format)) {
                content = exportAsCsv(logsToExport);
                fileName = "logs_" + System.currentTimeMillis() + ".csv";
            } else if ("txt".equalsIgnoreCase(format)) {
                content = exportAsTxt(logsToExport);
                fileName = "logs_" + System.currentTimeMillis() + ".txt";
            } else {
                content = exportAsJson(logsToExport);
                fileName = "logs_" + System.currentTimeMillis() + ".json";
            }
            
            LogExportResult result = new LogExportResult();
            result.setExportId("export-" + System.currentTimeMillis());
            result.setFileName(fileName);
            result.setFormat(format);
            result.setRecordCount(logsToExport.size());
            result.setFileSize(content.length());
            result.setTimestamp(System.currentTimeMillis());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Failed to export logs: " + e.getMessage());
        }
    }

    @Override
    public Result<LogStatistics> getStatistics(long startTime, long endTime) {
        LogStatistics stats = new LogStatistics();
        stats.setStartTime(startTime);
        stats.setEndTime(endTime);
        
        synchronized (logs) {
            for (LogEntry entry : logs) {
                if (entry.getTimestamp() >= startTime && entry.getTimestamp() <= endTime) {
                    stats.setTotalCount(stats.getTotalCount() + 1);
                    
                    switch (entry.getLevel()) {
                        case "ERROR":
                            stats.setErrorCount(stats.getErrorCount() + 1);
                            break;
                        case "WARN":
                            stats.setWarnCount(stats.getWarnCount() + 1);
                            break;
                        case "INFO":
                            stats.setInfoCount(stats.getInfoCount() + 1);
                            break;
                        case "DEBUG":
                            stats.setDebugCount(stats.getDebugCount() + 1);
                            break;
                        case "TRACE":
                            stats.setTraceCount(stats.getTraceCount() + 1);
                            break;
                    }
                }
            }
        }
        
        return Result.success(stats);
    }

    @Override
    public Result<Map<String, Long>> getLevelStatistics(long startTime, long endTime) {
        Map<String, Long> result = new HashMap<>();
        
        synchronized (logs) {
            for (LogEntry entry : logs) {
                if (entry.getTimestamp() >= startTime && entry.getTimestamp() <= endTime) {
                    result.merge(entry.getLevel(), 1L, Long::sum);
                }
            }
        }
        
        return Result.success(result);
    }

    @Override
    public Result<Map<String, Long>> getSourceStatistics(long startTime, long endTime) {
        Map<String, Long> result = new HashMap<>();
        
        synchronized (logs) {
            for (LogEntry entry : logs) {
                if (entry.getTimestamp() >= startTime && entry.getTimestamp() <= endTime) {
                    result.merge(entry.getSource(), 1L, Long::sum);
                }
            }
        }
        
        return Result.success(result);
    }

    private String exportAsJson(List<LogEntry> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < logs.size(); i++) {
            if (i > 0) sb.append(",");
            LogEntry entry = logs.get(i);
            sb.append("{\"logId\":\"").append(entry.getLogId()).append("\"");
            sb.append(",\"level\":\"").append(entry.getLevel()).append("\"");
            sb.append(",\"message\":\"").append(escapeJson(entry.getMessage())).append("\"");
            sb.append(",\"source\":\"").append(entry.getSource()).append("\"");
            sb.append(",\"timestamp\":").append(entry.getTimestamp());
            sb.append(",\"threadName\":\"").append(entry.getThreadName()).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String exportAsCsv(List<LogEntry> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("logId,level,message,source,timestamp,threadName\n");
        for (LogEntry entry : logs) {
            sb.append(entry.getLogId()).append(",");
            sb.append(entry.getLevel()).append(",");
            sb.append("\"").append(escapeCsv(entry.getMessage())).append("\",");
            sb.append(entry.getSource()).append(",");
            sb.append(entry.getTimestamp()).append(",");
            sb.append(entry.getThreadName()).append("\n");
        }
        return sb.toString();
    }

    private String exportAsTxt(List<LogEntry> logs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuilder sb = new StringBuilder();
        for (LogEntry entry : logs) {
            sb.append(sdf.format(new Date(entry.getTimestamp()))).append(" ");
            sb.append("[").append(entry.getLevel()).append("] ");
            sb.append("[").append(entry.getThreadName()).append("] ");
            sb.append(entry.getSource()).append(" - ");
            sb.append(entry.getMessage()).append("\n");
        }
        return sb.toString();
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
    }
}
