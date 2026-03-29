package net.ooder.scene.log.spi;

import net.ooder.scene.log.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.ooder.scene.core.PageResult;

public interface LogStorageProvider {

    String getStorageType();

    List<LogCategory> getSupportedCategories();

    void initialize(LogStorageConfig config);

    void shutdown();

    CompletableFuture<String> store(LogEntry entry);

    CompletableFuture<List<String>> batchStore(List<LogEntry> entries);

    CompletableFuture<PageResult<LogEntry>> query(LogQueryCriteria criteria);

    CompletableFuture<LogEntry> getById(String logId);

    CompletableFuture<Boolean> delete(String logId);

    CompletableFuture<Long> clean(LogCleanCriteria criteria);

    CompletableFuture<Boolean> healthCheck();
}
