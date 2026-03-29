package net.ooder.scene.log;

import net.ooder.scene.core.PageResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LogPersistenceService {

    CompletableFuture<String> write(LogEntry entry);

    CompletableFuture<List<String>> batchWrite(List<LogEntry> entries);

    CompletableFuture<PageResult<LogEntry>> query(LogQueryCriteria criteria);

    CompletableFuture<LogEntry> getById(String logId);

    CompletableFuture<Boolean> delete(String logId);

    CompletableFuture<Long> clean(LogCleanCriteria criteria);

    CompletableFuture<byte[]> export(LogQueryCriteria criteria, String format);

    CompletableFuture<LogStatistics> statistics(LogStatsCriteria criteria);

    CompletableFuture<List<LogEntry>> getTraceChain(String traceId);

    CompletableFuture<List<LogEntry>> getCascadedLogs(String logId);
}
