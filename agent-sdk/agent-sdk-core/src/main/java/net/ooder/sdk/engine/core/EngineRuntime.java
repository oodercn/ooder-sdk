package net.ooder.sdk.engine.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 引擎运行时环境
 */
public interface EngineRuntime {
    
    void initialize();
    
    void start();
    
    void stop();
    
    void shutdown();
    
    ExecutionContext createContext();
    
    ExecutionContext createContext(Map<String, Object> properties);
    
    ExecutionEngine getExecutionEngine();
    
    EngineStatus getStatus();
    
    boolean isRunning();
    
    String getRuntimeId();
    
    String getVersion();
    
    Map<String, Object> getMetrics();
    
    enum EngineStatus {
        CREATED,
        INITIALIZING,
        RUNNING,
        STOPPING,
        STOPPED,
        ERROR
    }
}
