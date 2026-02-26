package net.ooder.sdk.engine.core;

import java.util.concurrent.CompletableFuture;

/**
 * 执行引擎
 */
public interface ExecutionEngine {
    
    ExecutionResult execute(ExecutionRequest request);
    
    CompletableFuture<ExecutionResult> executeAsync(ExecutionRequest request);
    
    void cancel(String executionId);
    
    ExecutionStatus getStatus(String executionId);
    
    ExecutionResult getResult(String executionId);
    
    enum ExecutionStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED,
        TIMEOUT
    }
}
