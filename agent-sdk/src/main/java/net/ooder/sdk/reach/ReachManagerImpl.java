package net.ooder.sdk.reach;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class ReachManagerImpl implements ReachManager {
    
    private static final Logger log = LoggerFactory.getLogger(ReachManagerImpl.class);
    
    private final Map<String, ReachExecutor> executors = new ConcurrentHashMap<>();
    private ReachAuthContext authContext = new ReachAuthContext();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public ReachResult execute(ReachProtocol protocol) {
        long startTime = System.currentTimeMillis();
        
        try {
            String deviceType = protocol.getDeviceType();
            String deviceId = protocol.getDeviceId();
            String action = protocol.getAction();
            
            if (authContext != null && !authContext.isAuthorized(deviceType, deviceId, action)) {
                ReachResult result = ReachResult.unauthorized("Not authorized for this operation");
                result.setExecutionTime(System.currentTimeMillis() - startTime);
                return result;
            }
            
            ReachExecutor executorImpl = executors.get(deviceType);
            if (executorImpl == null) {
                ReachResult result = ReachResult.failure("No executor found for device type: " + deviceType);
                result.setExecutionTime(System.currentTimeMillis() - startTime);
                return result;
            }
            
            ReachResult result = executorImpl.execute(protocol);
            result.setExecutionTime(System.currentTimeMillis() - startTime);
            
            log.info("Reach executed: {}://{}/{}/{} in {}ms", 
                protocol.getProtocol(), deviceType, deviceId, action, result.getExecutionTime());
            
            return result;
        } catch (Exception e) {
            log.error("Reach execution failed", e);
            ReachResult result = ReachResult.failure("Execution failed: " + e.getMessage());
            result.setExecutionTime(System.currentTimeMillis() - startTime);
            return result;
        }
    }
    
    @Override
    public void registerExecutor(String deviceType, ReachExecutor executor) {
        executors.put(deviceType, executor);
        log.info("Executor registered for device type: {}", deviceType);
    }
    
    @Override
    public void unregisterExecutor(String deviceType) {
        executors.remove(deviceType);
        log.info("Executor unregistered for device type: {}", deviceType);
    }
    
    @Override
    public void setAuthContext(ReachAuthContext authContext) {
        this.authContext = authContext;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
