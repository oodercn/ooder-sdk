package net.ooder.scene.audit;

import net.ooder.scene.core.AuditLog;
import net.ooder.scene.core.AuditLogFilter;
import net.ooder.scene.core.PageRequest;
import net.ooder.scene.core.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 审计日志持久化服务
 * 
 * <p>提供审计日志的文件持久化存储能力</p>
 * 
 * <h3>特性：</h3>
 * <ul>
 *   <li>按日期分割日志文件</li>
 *   <li>异步写入提升性能</li>
 *   <li>自动清理过期日志</li>
 *   <li>支持日志导出</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Service
public class AuditLogPersistenceService implements AuditService {
    
    private static final Logger log = LoggerFactory.getLogger(AuditLogPersistenceService.class);
    
    private final ExecutorService executor = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "audit-log-writer");
        t.setDaemon(true);
        return t;
    });
    
    private final BlockingQueue<AuditLog> logQueue = new LinkedBlockingQueue<>(10000);
    private volatile boolean running = true;
    
    private Path logDirectory;
    private int maxHistoryDays = 90;
    private long maxFileSize = 100 * 1024 * 1024; // 100MB
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Map<String, BufferedWriter> writers = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() throws IOException {
        String logDir = System.getProperty("ooder.audit.log.dir", "./logs/audit");
        this.logDirectory = Paths.get(logDir);
        
        if (!Files.exists(logDirectory)) {
            Files.createDirectories(logDirectory);
        }
        
        String maxDays = System.getProperty("ooder.audit.max.history.days", "90");
        this.maxHistoryDays = Integer.parseInt(maxDays);
        
        String maxSize = System.getProperty("ooder.audit.max.file.size", "104857600"); // 100MB
        this.maxFileSize = Long.parseLong(maxSize);
        
        executor.submit(() -> processLogQueue());
        executor.submit(() -> cleanExpiredLogs());
        
        log.info("AuditLogPersistenceService initialized: dir={}, maxHistory={}days", 
            logDirectory, maxHistoryDays);
    }
    
    @PreDestroy
    public void destroy() {
        running = false;
        executor.shutdown();
        
        for (BufferedWriter writer : writers.values()) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                log.error("Failed to close writer", e);
            }
        }
        writers.clear();
        
        log.info("AuditLogPersistenceService destroyed");
    }
    
    @Override
    public void log(AuditLog auditLog) {
        if (!running) {
            return;
        }
        
        try {
            if (!logQueue.offer(auditLog, 1, TimeUnit.SECONDS)) {
                log.warn("Audit log queue is full, dropping log: {}", auditLog.getLogId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void log(String userId, String eventType, String action, String target, 
                    String result, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setLogId(UUID.randomUUID().toString());
        auditLog.setUserId(userId);
        auditLog.setEventType(eventType);
        auditLog.setAction(action);
        auditLog.setTarget(target);
        auditLog.setResult(result);
        auditLog.setDescription(description);
        auditLog.setTimestamp(System.currentTimeMillis());
        
        log(auditLog);
    }
    
    @Override
    public PageResult<AuditLog> query(PageRequest request) {
        return query(request, null);
    }
    
    @Override
    public PageResult<AuditLog> query(PageRequest request, AuditLogFilter filter) {
        List<AuditLog> results = new ArrayList<>();
        int total = 0;
        
        try {
            List<Path> logFiles = getLogFiles(request, filter);
            
            for (Path file : logFiles) {
                List<AuditLog> logs = readLogsFromFile(file, filter);
                results.addAll(logs);
                total += logs.size();
            }
            
            results.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
            
            int start = (request.getPageNum() - 1) * request.getPageSize();
            int end = Math.min(start + request.getPageSize(), results.size());
            
            if (start < results.size()) {
                results = results.subList(start, end);
            } else {
                results = new ArrayList<>();
            }
            
        } catch (IOException e) {
            log.error("Failed to query audit logs", e);
        }
        
        PageResult<AuditLog> pageResult = new PageResult<>();
        pageResult.setItems(results);
        pageResult.setPageNum(request.getPageNum());
        pageResult.setPageSize(request.getPageSize());
        pageResult.setTotal(total);
        pageResult.setTotalPages((total + request.getPageSize() - 1) / request.getPageSize());
        
        return pageResult;
    }
    
    @Override
    public AuditLog getLog(String logId) {
        if (logId == null) {
            return null;
        }
        
        try {
            List<Path> logFiles = getLogFiles(null, null);
            
            for (Path file : logFiles) {
                List<AuditLog> logs = readLogsFromFile(file, null);
                for (AuditLog log : logs) {
                    if (logId.equals(log.getLogId())) {
                        return log;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to get audit log: {}", logId, e);
        }
        
        return null;
    }
    
    @Override
    public byte[] export(AuditLogFilter filter, String format) {
        List<AuditLog> allLogs = new ArrayList<>();
        
        try {
            List<Path> logFiles = getLogFiles(null, filter);
            
            for (Path file : logFiles) {
                allLogs.addAll(readLogsFromFile(file, filter));
            }
            
            allLogs.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
            
            switch (format.toLowerCase()) {
                case "json":
                    return exportAsJson(allLogs);
                case "csv":
                    return exportAsCsv(allLogs);
                default:
                    return exportAsJson(allLogs);
            }
            
        } catch (IOException e) {
            log.error("Failed to export audit logs", e);
            return new byte[0];
        }
    }
    
    @Override
    public int cleanExpiredLogs(long beforeTime) {
        int cleaned = 0;
        
        try {
            LocalDate cutoffDate = LocalDate.now().minusDays(maxHistoryDays);
            
            List<Path> logFiles = Files.list(logDirectory)
                .filter(p -> p.toString().endsWith(".log"))
                .collect(Collectors.toList());
            
            for (Path file : logFiles) {
                String fileName = file.getFileName().toString();
                String dateStr = fileName.replace("audit-", "").replace(".log", "");
                
                try {
                    LocalDate fileDate = LocalDate.parse(dateStr, dateFormatter);
                    if (fileDate.isBefore(cutoffDate)) {
                        Files.delete(file);
                        cleaned++;
                        log.info("Deleted expired audit log file: {}", fileName);
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse date from file: {}", fileName);
                }
            }
            
        } catch (IOException e) {
            log.error("Failed to clean expired logs", e);
        }
        
        return cleaned;
    }
    
    @Override
    public AuditStats getStats(long startTime, long endTime) {
        AuditStats stats = new AuditStats();
        
        try {
            List<Path> logFiles = getLogFilesByTimeRange(startTime, endTime);
            
            long totalCount = 0;
            long successCount = 0;
            long failureCount = 0;
            Map<String, Long> eventTypeCounts = new HashMap<>();
            
            for (Path file : logFiles) {
                List<AuditLog> logs = readLogsFromFile(file, null);
                
                for (AuditLog auditLog : logs) {
                    if (auditLog.getTimestamp() >= startTime && auditLog.getTimestamp() <= endTime) {
                        totalCount++;
                        
                        if ("success".equalsIgnoreCase(auditLog.getResult())) {
                            successCount++;
                        } else {
                            failureCount++;
                        }
                        
                        String eventType = auditLog.getEventType();
                        eventTypeCounts.merge(eventType, 1L, Long::sum);
                    }
                }
            }
            
            stats.setTotalCount(totalCount);
            stats.setSuccessCount(successCount);
            stats.setFailureCount(failureCount);
            stats.setEventTypeCounts(eventTypeCounts);
            
        } catch (IOException e) {
            log.error("Failed to get audit stats", e);
        }
        
        return stats;
    }
    
    private void processLogQueue() {
        while (running || !logQueue.isEmpty()) {
            try {
                AuditLog auditLog = logQueue.poll(1, TimeUnit.SECONDS);
                if (auditLog != null) {
                    writeLog(auditLog);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error processing audit log queue", e);
            }
        }
    }
    
    private void writeLog(AuditLog auditLog) {
        try {
            String dateStr = LocalDate.now().format(dateFormatter);
            String fileName = "audit-" + dateStr + ".log";
            Path filePath = logDirectory.resolve(fileName);
            
            BufferedWriter writer = writers.computeIfAbsent(fileName, k -> {
                try {
                    return Files.newBufferedWriter(filePath, 
                        StandardOpenOption.CREATE, 
                        StandardOpenOption.APPEND,
                        StandardOpenOption.WRITE);
                } catch (IOException e) {
                    log.error("Failed to create writer for: {}", k, e);
                    return null;
                }
            });
            
            if (writer != null) {
                String json = toJson(auditLog);
                writer.write(json);
                writer.newLine();
                writer.flush();
                
                checkFileSize(filePath);
            }
            
        } catch (IOException e) {
            log.error("Failed to write audit log", e);
        }
    }
    
    private void checkFileSize(Path filePath) {
        try {
            long size = Files.size(filePath);
            if (size > maxFileSize) {
                String fileName = filePath.getFileName().toString();
                writers.remove(fileName);
                
                String rotatedName = fileName.replace(".log", 
                    "-" + System.currentTimeMillis() + ".log");
                Path rotatedPath = logDirectory.resolve(rotatedName);
                Files.move(filePath, rotatedPath);
                
                log.info("Rotated audit log file: {} -> {}", fileName, rotatedName);
            }
        } catch (IOException e) {
            log.error("Failed to check file size", e);
        }
    }
    
    private void cleanExpiredLogs() {
        while (running) {
            try {
                Thread.sleep(TimeUnit.HOURS.toMillis(1));
                int cleaned = cleanExpiredLogs(System.currentTimeMillis());
                if (cleaned > 0) {
                    log.info("Cleaned {} expired audit log files", cleaned);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private List<Path> getLogFiles(PageRequest request, AuditLogFilter filter) throws IOException {
        return Files.list(logDirectory)
            .filter(p -> p.toString().endsWith(".log"))
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
    }
    
    private List<Path> getLogFilesByTimeRange(long startTime, long endTime) throws IOException {
        return Files.list(logDirectory)
            .filter(p -> p.toString().endsWith(".log"))
            .collect(Collectors.toList());
    }
    
    private List<AuditLog> readLogsFromFile(Path file, AuditLogFilter filter) throws IOException {
        List<AuditLog> logs = new ArrayList<>();
        
        if (!Files.exists(file)) {
            return logs;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    AuditLog auditLog = fromJson(line);
                    if (filter == null || matchesFilter(auditLog, filter)) {
                        logs.add(auditLog);
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse audit log line: {}", line);
                }
            }
        }
        
        return logs;
    }
    
    private boolean matchesFilter(AuditLog log, AuditLogFilter filter) {
        if (filter.getUserId() != null && !filter.getUserId().equals(log.getUserId())) {
            return false;
        }
        if (filter.getEventType() != null && !filter.getEventType().equals(log.getEventType())) {
            return false;
        }
        if (filter.getStartTime() > 0 && log.getTimestamp() < filter.getStartTime()) {
            return false;
        }
        if (filter.getEndTime() > 0 && log.getTimestamp() > filter.getEndTime()) {
            return false;
        }
        return true;
    }
    
    private String toJson(AuditLog log) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"logId\":\"").append(escape(log.getLogId())).append("\",");
        sb.append("\"eventType\":\"").append(escape(log.getEventType())).append("\",");
        sb.append("\"severity\":\"").append(escape(log.getSeverity())).append("\",");
        sb.append("\"userId\":\"").append(escape(log.getUserId())).append("\",");
        sb.append("\"userName\":\"").append(escape(log.getUserName())).append("\",");
        sb.append("\"source\":\"").append(escape(log.getSource())).append("\",");
        sb.append("\"target\":\"").append(escape(log.getTarget())).append("\",");
        sb.append("\"action\":\"").append(escape(log.getAction())).append("\",");
        sb.append("\"description\":\"").append(escape(log.getDescription())).append("\",");
        sb.append("\"result\":\"").append(escape(log.getResult())).append("\",");
        sb.append("\"details\":\"").append(escape(log.getDetails())).append("\",");
        sb.append("\"ipAddress\":\"").append(escape(log.getIpAddress())).append("\",");
        sb.append("\"timestamp\":").append(log.getTimestamp());
        sb.append("}");
        return sb.toString();
    }
    
    private AuditLog fromJson(String json) {
        AuditLog log = new AuditLog();
        
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        String[] fields = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        
        for (String field : fields) {
            int colonIndex = field.indexOf(":");
            if (colonIndex > 0) {
                String key = field.substring(0, colonIndex).trim().replace("\"", "");
                String value = field.substring(colonIndex + 1).trim();
                
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                
                switch (key) {
                    case "logId": log.setLogId(value); break;
                    case "eventType": log.setEventType(value); break;
                    case "severity": log.setSeverity(value); break;
                    case "userId": log.setUserId(value); break;
                    case "userName": log.setUserName(value); break;
                    case "source": log.setSource(value); break;
                    case "target": log.setTarget(value); break;
                    case "action": log.setAction(value); break;
                    case "description": log.setDescription(value); break;
                    case "result": log.setResult(value); break;
                    case "details": log.setDetails(value); break;
                    case "ipAddress": log.setIpAddress(value); break;
                    case "timestamp": log.setTimestamp(Long.parseLong(value)); break;
                }
            }
        }
        
        return log;
    }
    
    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
    
    private byte[] exportAsJson(List<AuditLog> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < logs.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(logs.get(i)));
        }
        sb.append("]");
        return sb.toString().getBytes();
    }
    
    private byte[] exportAsCsv(List<AuditLog> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("logId,eventType,severity,userId,userName,source,target,action,description,result,details,ipAddress,timestamp\n");
        
        for (AuditLog log : logs) {
            sb.append(log.getLogId()).append(",");
            sb.append(log.getEventType()).append(",");
            sb.append(log.getSeverity()).append(",");
            sb.append(log.getUserId()).append(",");
            sb.append(log.getUserName()).append(",");
            sb.append(log.getSource()).append(",");
            sb.append(log.getTarget()).append(",");
            sb.append(log.getAction()).append(",");
            sb.append(log.getDescription()).append(",");
            sb.append(log.getResult()).append(",");
            sb.append(log.getDetails()).append(",");
            sb.append(log.getIpAddress()).append(",");
            sb.append(log.getTimestamp()).append("\n");
        }
        
        return sb.toString().getBytes();
    }
}
