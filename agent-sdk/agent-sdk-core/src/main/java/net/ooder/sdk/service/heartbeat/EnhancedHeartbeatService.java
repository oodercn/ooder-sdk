package net.ooder.sdk.service.heartbeat;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.agent.AgentFactory;
import net.ooder.sdk.api.agent.EndAgent;
import net.ooder.sdk.api.agent.McpAgent;
import net.ooder.sdk.api.agent.RouteAgent;
import net.ooder.sdk.api.agent.SceneAgent;
import net.ooder.sdk.api.agent.WorkerAgent;

public class EnhancedHeartbeatService {
    
    private static final Logger log = LoggerFactory.getLogger(EnhancedHeartbeatService.class);
    
    private final AgentFactory agentFactory;
    private final ScheduledExecutorService scheduler;
    private final Map<String, HeartbeatContext> heartbeatContexts = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> heartbeatTasks = new ConcurrentHashMap<>();
    
    private final Map<String, DeviceStatusListener> statusListeners = new ConcurrentHashMap<>();
    
    public EnhancedHeartbeatService(AgentFactory agentFactory) {
        this.agentFactory = agentFactory;
        this.scheduler = Executors.newScheduledThreadPool(4);
    }
    
    public void registerAgent(String agentId, HeartbeatConfig config) {
        HeartbeatContext context = new HeartbeatContext(agentId, config);
        heartbeatContexts.put(agentId, context);
        log.info("Registered agent for heartbeat: {} with config: {}", agentId, config);
    }
    
    public void registerAgent(String agentId, HeartbeatConfig.DeviceType deviceType) {
        registerAgent(agentId, new HeartbeatConfig(deviceType));
    }
    
    public void unregisterAgent(String agentId) {
        stopHeartbeat(agentId);
        heartbeatContexts.remove(agentId);
        log.info("Unregistered agent from heartbeat: {}", agentId);
    }
    
    public void startHeartbeat(String agentId) {
        HeartbeatContext context = heartbeatContexts.get(agentId);
        if (context == null) {
            log.warn("Agent not registered for heartbeat: {}", agentId);
            return;
        }
        
        if (heartbeatTasks.containsKey(agentId)) {
            log.debug("Heartbeat already running for agent: {}", agentId);
            return;
        }
        
        scheduleHeartbeat(agentId, context);
        log.info("Started heartbeat for agent: {}", agentId);
    }
    
    private void scheduleHeartbeat(String agentId, HeartbeatContext context) {
        int interval = context.getConfig().getCurrentInterval(context.isSleeping());
        
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                executeHeartbeat(agentId, context);
            } catch (Exception e) {
                log.error("Heartbeat error for agent: {}", agentId, e);
            }
        }, 0, interval, TimeUnit.MILLISECONDS);
        
        heartbeatTasks.put(agentId, future);
    }
    
    private void executeHeartbeat(String agentId, HeartbeatContext context) {
        Agent agent = agentFactory.getAgent(agentId);
        if (agent == null) {
            log.warn("Agent not found for heartbeat: {}", agentId);
            handleAgentOffline(agentId, context, "Agent not found");
            return;
        }
        
        try {
            boolean success = sendHeartbeat(agent);
            
            if (success) {
                context.recordHeartbeatSuccess();
                if (context.getStatus() != DeviceStatus.ONLINE) {
                    updateStatus(agentId, context, DeviceStatus.ONLINE);
                }
            } else {
                context.recordHeartbeatFailure();
                checkOfflineThreshold(agentId, context);
            }
            
        } catch (Exception e) {
            log.error("Failed to send heartbeat for agent: {}", agentId, e);
            context.recordHeartbeatFailure();
            checkOfflineThreshold(agentId, context);
        }
    }
    
    private boolean sendHeartbeat(Agent agent) {
        try {
            if (agent instanceof McpAgent) {
                ((McpAgent) agent).heartbeat().get(5, TimeUnit.SECONDS);
            } else if (agent instanceof RouteAgent) {
                ((RouteAgent) agent).heartbeat().get(5, TimeUnit.SECONDS);
            } else if (agent instanceof EndAgent) {
                ((EndAgent) agent).heartbeat().get(5, TimeUnit.SECONDS);
            } else if (agent instanceof SceneAgent) {
                return agent.isHealthy();
            } else if (agent instanceof WorkerAgent) {
                return agent.isHealthy();
            }
            return true;
        } catch (TimeoutException e) {
            log.warn("Heartbeat timeout for agent: {}", agent.getAgentId());
            return false;
        } catch (Exception e) {
            log.error("Heartbeat failed for agent: {}", agent.getAgentId(), e);
            return false;
        }
    }
    
    private void checkOfflineThreshold(String agentId, HeartbeatContext context) {
        int missed = context.getConsecutiveMisses();
        int threshold = context.getConfig().getOfflineThreshold();
        
        if (missed >= threshold) {
            handleAgentOffline(agentId, context, 
                "Missed " + missed + " consecutive heartbeats");
        } else if (missed > 0) {
            updateStatus(agentId, context, DeviceStatus.DEGRADED);
        }
    }
    
    private void handleAgentOffline(String agentId, HeartbeatContext context, String reason) {
        updateStatus(agentId, context, DeviceStatus.OFFLINE);
        log.warn("Agent {} marked as OFFLINE: {}", agentId, reason);
        
        stopHeartbeat(agentId);
        
        scheduler.schedule(() -> {
            if (heartbeatContexts.containsKey(agentId)) {
                log.info("Attempting to reconnect agent: {}", agentId);
                startHeartbeat(agentId);
            }
        }, 30, TimeUnit.SECONDS);
    }
    
    private void updateStatus(String agentId, HeartbeatContext context, DeviceStatus newStatus) {
        DeviceStatus oldStatus = context.getStatus();
        if (oldStatus != newStatus) {
            context.setStatus(newStatus);
            notifyStatusChange(agentId, oldStatus, newStatus);
        }
    }
    
    public void stopHeartbeat(String agentId) {
        ScheduledFuture<?> future = heartbeatTasks.remove(agentId);
        if (future != null) {
            future.cancel(false);
            log.info("Stopped heartbeat for agent: {}", agentId);
        }
    }
    
    public void stopAllHeartbeats() {
        for (String agentId : heartbeatTasks.keySet()) {
            stopHeartbeat(agentId);
        }
        log.info("Stopped all heartbeats");
    }
    
    public void setSleepMode(String agentId, boolean sleeping) {
        HeartbeatContext context = heartbeatContexts.get(agentId);
        if (context != null && context.isSleeping() != sleeping) {
            context.setSleeping(sleeping);
            
            stopHeartbeat(agentId);
            scheduleHeartbeat(agentId, context);
            
            log.info("Agent {} sleep mode: {}", agentId, sleeping ? "enabled" : "disabled");
        }
    }
    
    public DeviceStatus getDeviceStatus(String agentId) {
        HeartbeatContext context = heartbeatContexts.get(agentId);
        return context != null ? context.getStatus() : DeviceStatus.UNKNOWN;
    }
    
    public HeartbeatStats getHeartbeatStats(String agentId) {
        HeartbeatContext context = heartbeatContexts.get(agentId);
        if (context == null) {
            return null;
        }
        return new HeartbeatStats(
            context.getTotalHeartbeats(),
            context.getSuccessfulHeartbeats(),
            context.getFailedHeartbeats(),
            context.getConsecutiveMisses(),
            context.getLastHeartbeatTime(),
            context.getStatus()
        );
    }
    
    public void addStatusListener(String agentId, DeviceStatusListener listener) {
        statusListeners.put(agentId, listener);
    }
    
    public void removeStatusListener(String agentId) {
        statusListeners.remove(agentId);
    }
    
    private void notifyStatusChange(String agentId, DeviceStatus oldStatus, DeviceStatus newStatus) {
        DeviceStatusListener listener = statusListeners.get(agentId);
        if (listener != null) {
            try {
                listener.onStatusChange(agentId, oldStatus, newStatus);
            } catch (Exception e) {
                log.error("Error notifying status change for agent: {}", agentId, e);
            }
        }
    }
    
    public void shutdown() {
        stopAllHeartbeats();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("EnhancedHeartbeatService shutdown complete");
    }
    
    public enum DeviceStatus {
        ONLINE("online", "在线"),
        DEGRADED("degraded", "降级"),
        OFFLINE("offline", "离线"),
        FAULT("fault", "故障"),
        UNKNOWN("unknown", "未知");
        
        private final String code;
        private final String description;
        
        DeviceStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    public interface DeviceStatusListener {
        void onStatusChange(String agentId, DeviceStatus oldStatus, DeviceStatus newStatus);
    }
    
    private static class HeartbeatContext {
        private final String agentId;
        private final HeartbeatConfig config;
        private final AtomicInteger totalHeartbeats = new AtomicInteger(0);
        private final AtomicInteger successfulHeartbeats = new AtomicInteger(0);
        private final AtomicInteger failedHeartbeats = new AtomicInteger(0);
        private final AtomicInteger consecutiveMisses = new AtomicInteger(0);
        private final AtomicLong lastHeartbeatTime = new AtomicLong(0);
        private final AtomicReference<DeviceStatus> status = new AtomicReference<>(DeviceStatus.ONLINE);
        private volatile boolean sleeping = false;
        
        public HeartbeatContext(String agentId, HeartbeatConfig config) {
            this.agentId = agentId;
            this.config = config;
        }
        
        public HeartbeatConfig getConfig() { return config; }
        public DeviceStatus getStatus() { return status.get(); }
        public void setStatus(DeviceStatus status) { this.status.set(status); }
        public boolean isSleeping() { return sleeping; }
        public void setSleeping(boolean sleeping) { this.sleeping = sleeping; }
        
        public int getTotalHeartbeats() { return totalHeartbeats.get(); }
        public int getSuccessfulHeartbeats() { return successfulHeartbeats.get(); }
        public int getFailedHeartbeats() { return failedHeartbeats.get(); }
        public int getConsecutiveMisses() { return consecutiveMisses.get(); }
        public long getLastHeartbeatTime() { return lastHeartbeatTime.get(); }
        
        public void recordHeartbeatSuccess() {
            totalHeartbeats.incrementAndGet();
            successfulHeartbeats.incrementAndGet();
            consecutiveMisses.set(0);
            lastHeartbeatTime.set(System.currentTimeMillis());
        }
        
        public void recordHeartbeatFailure() {
            totalHeartbeats.incrementAndGet();
            failedHeartbeats.incrementAndGet();
            consecutiveMisses.incrementAndGet();
        }
    }
    
    public static class HeartbeatStats {
        private final int totalHeartbeats;
        private final int successfulHeartbeats;
        private final int failedHeartbeats;
        private final int consecutiveMisses;
        private final long lastHeartbeatTime;
        private final DeviceStatus status;
        
        public HeartbeatStats(int total, int success, int failed, 
                             int consecutive, long lastTime, DeviceStatus status) {
            this.totalHeartbeats = total;
            this.successfulHeartbeats = success;
            this.failedHeartbeats = failed;
            this.consecutiveMisses = consecutive;
            this.lastHeartbeatTime = lastTime;
            this.status = status;
        }
        
        public int getTotalHeartbeats() { return totalHeartbeats; }
        public int getSuccessfulHeartbeats() { return successfulHeartbeats; }
        public int getFailedHeartbeats() { return failedHeartbeats; }
        public int getConsecutiveMisses() { return consecutiveMisses; }
        public long getLastHeartbeatTime() { return lastHeartbeatTime; }
        public DeviceStatus getStatus() { return status; }
        
        public double getSuccessRate() {
            if (totalHeartbeats == 0) return 0;
            return (double) successfulHeartbeats / totalHeartbeats * 100;
        }
    }
}
