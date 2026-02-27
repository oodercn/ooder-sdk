package net.ooder.scene.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;

import java.util.List;
import java.util.Map;

/**
 * 日志Provider接口
 */
public interface LogProvider extends BaseProvider {
    Result<Boolean> writeLog(String level, String message, String source);
    Result<Boolean> writeLog(String level, String message, String source, Map<String, Object> details);
    Result<PageResult<LogEntry>> queryLogs(LogQuery query);
    Result<LogEntry> getLog(String logId);
    Result<Boolean> deleteLog(String logId);
    Result<Long> clearLogs(long beforeTime);
    Result<LogExportResult> exportLogs(LogQuery query, String format);
    Result<LogStatistics> getStatistics(long startTime, long endTime);
    Result<Map<String, Long>> getLevelStatistics(long startTime, long endTime);
    Result<Map<String, Long>> getSourceStatistics(long startTime, long endTime);
}
