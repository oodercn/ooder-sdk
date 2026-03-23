package net.ooder.scene.failover;

import net.ooder.scene.agent.AgentSessionManager;
import net.ooder.scene.agent.AgentStatus;
import net.ooder.scene.agent.AgentMessageBus;
import net.ooder.scene.agent.AgentMessage;
import net.ooder.scene.agent.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@Component
public class FailoverManagerImpl implements FailoverManager {

    private static final Logger log = LoggerFactory.getLogger(FailoverManagerImpl.class);

    private static final long DEFAULT_TIMEOUT = 60 * 1000L;
    private static final long CHECK_INTERVAL = 10 * 1000L;

    private final AgentSessionManager sessionManager;
    private final AgentMessageBus messageBus;

    private final Map<String, AgentInfo> agentRegistry = new ConcurrentHashMap<>();
    private final Map<String, List<String>> sceneGroupAgents = new ConcurrentHashMap<>();
    private final Map<String, List<TaskInfo>> agentTasks = new ConcurrentHashMap<>();
    private final List<FailoverListener> listeners = new CopyOnWriteArrayList<>();

    private ScheduledExecutorService scheduler;
    private long timeout = DEFAULT_TIMEOUT;
    private volatile boolean monitoring = false;

    public FailoverManagerImpl(AgentSessionManager sessionManager, AgentMessageBus messageBus) {
        this.sessionManager = sessionManager;
        this.messageBus = messageBus;
    }

    @Override
    public void registerAgent(String agentId, String sceneGroupId) {
        AgentInfo info = new AgentInfo(agentId, sceneGroupId);
        info.setLastHeartbeat(System.currentTimeMillis());
        agentRegistry.put(agentId, info);

        sceneGroupAgents.computeIfAbsent(sceneGroupId, k -> new CopyOnWriteArrayList<>()).add(agentId);

        log.info("Agent registered for failover: agentId={}, sceneGroupId={}", agentId, sceneGroupId);
    }

    @Override
    public void unregisterAgent(String agentId) {
        AgentInfo info = agentRegistry.remove(agentId);
        if (info != null) {
            List<String> agents = sceneGroupAgents.get(info.getSceneGroupId());
            if (agents != null) {
                agents.remove(agentId);
            }
            agentTasks.remove(agentId);
            log.info("Agent unregistered from failover: agentId={}", agentId);
        }
    }

    @Override
    public void updateHeartbeat(String agentId) {
        AgentInfo info = agentRegistry.get(agentId);
        if (info != null) {
            info.setLastHeartbeat(System.currentTimeMillis());
            info.setTimeout(false);

            if (info.wasTimedOut()) {
                info.setWasTimedOut(false);
                fireEvent(FailoverEventType.AGENT_RECOVERED, agentId, info.getSceneGroupId(), null);
                log.info("Agent recovered: agentId={}", agentId);
            }
        }
    }

    @Override
    public List<String> getTimedOutAgents() {
        List<String> timedOut = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (Map.Entry<String, AgentInfo> entry : agentRegistry.entrySet()) {
            AgentInfo info = entry.getValue();
            if (now - info.getLastHeartbeat() > timeout && !info.isTimeout()) {
                timedOut.add(entry.getKey());
            }
        }

        return timedOut;
    }

    @Override
    public List<String> getAgentsBySceneGroup(String sceneGroupId) {
        List<String> agents = sceneGroupAgents.get(sceneGroupId);
        return agents != null ? new ArrayList<>(agents) : new ArrayList<>();
    }

    @Override
    public void reassignTasks(String failedAgentId, String targetAgentId) {
        AgentInfo failedInfo = agentRegistry.get(failedAgentId);
        if (failedInfo == null) {
            log.warn("Cannot reassign tasks: agent not found: {}", failedAgentId);
            return;
        }

        List<TaskInfo> tasks = agentTasks.remove(failedAgentId);
        if (tasks == null || tasks.isEmpty()) {
            log.info("No tasks to reassign for agent: {}", failedAgentId);
            return;
        }

        fireEvent(FailoverEventType.FAILOVER_STARTED, failedAgentId, failedInfo.getSceneGroupId(), null);

        int reassigned = 0;
        for (TaskInfo task : tasks) {
            try {
                AgentMessage message = AgentMessage.builder()
                        .from("system-failover")
                        .to(targetAgentId)
                        .type(MessageType.TASK_DELEGATE)
                        .payloadItem("originalTaskId", task.getTaskId())
                        .payloadItem("originalAgentId", failedAgentId)
                        .payloadItem("taskData", task.getTaskData())
                        .priority(10)
                        .build();

                messageBus.send(message);

                List<TaskInfo> targetTasks = agentTasks.computeIfAbsent(targetAgentId, k -> new CopyOnWriteArrayList<>());
                targetTasks.add(task);

                reassigned++;

                fireEvent(FailoverEventType.TASK_REASSIGNED, failedAgentId, failedInfo.getSceneGroupId(), task.getTaskId());

            } catch (Exception e) {
                log.error("Failed to reassign task: taskId={}", task.getTaskId(), e);
                fireEvent(FailoverEventType.TASK_FAILED, failedAgentId, failedInfo.getSceneGroupId(), task.getTaskId());
            }
        }

        fireEvent(FailoverEventType.FAILOVER_COMPLETED, failedAgentId, failedInfo.getSceneGroupId(), null);

        log.info("Tasks reassigned: from={}, to={}, count={}", failedAgentId, targetAgentId, reassigned);
    }

    @Override
    public void reassignTasksAuto(String failedAgentId) {
        AgentInfo info = agentRegistry.get(failedAgentId);
        if (info == null) {
            return;
        }

        String replacement = selectReplacementAgent(info.getSceneGroupId(), failedAgentId);
        if (replacement != null) {
            reassignTasks(failedAgentId, replacement);
        } else {
            log.warn("No replacement agent available for: {}", failedAgentId);
        }
    }

    @Override
    public String selectReplacementAgent(String sceneGroupId, String failedAgentId) {
        List<String> agents = getAgentsBySceneGroup(sceneGroupId);

        for (String agentId : agents) {
            if (!agentId.equals(failedAgentId)) {
                AgentInfo info = agentRegistry.get(agentId);
                if (info != null && !info.isTimeout()) {
                    return agentId;
                }
            }
        }

        return null;
    }

    @Override
    public void addFailoverListener(FailoverListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeFailoverListener(FailoverListener listener) {
        listeners.remove(listener);
    }

    @Override
    public FailoverStats getStats() {
        FailoverStats stats = new FailoverStats();
        stats.setTotalAgents(agentRegistry.size());

        int active = 0;
        int timedOut = 0;
        long now = System.currentTimeMillis();

        for (AgentInfo info : agentRegistry.values()) {
            if (now - info.getLastHeartbeat() <= timeout) {
                active++;
            } else {
                timedOut++;
            }
        }

        stats.setActiveAgents(active);
        stats.setTimedOutAgents(timedOut);
        stats.setLastCheckTime(now);

        return stats;
    }

    @Override
    public void startMonitoring() {
        if (monitoring) {
            return;
        }

        monitoring = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkAgents, CHECK_INTERVAL, CHECK_INTERVAL, TimeUnit.MILLISECONDS);

        log.info("Failover monitoring started: timeout={}ms, interval={}ms", timeout, CHECK_INTERVAL);
    }

    @Override
    public void stopMonitoring() {
        monitoring = false;
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Failover monitoring stopped");
    }

    public void assignTask(String agentId, String taskId, Object taskData) {
        TaskInfo task = new TaskInfo(taskId, taskData);
        agentTasks.computeIfAbsent(agentId, k -> new CopyOnWriteArrayList<>()).add(task);
        log.debug("Task assigned: agentId={}, taskId={}", agentId, taskId);
    }

    public void completeTask(String agentId, String taskId) {
        List<TaskInfo> tasks = agentTasks.get(agentId);
        if (tasks != null) {
            tasks.removeIf(t -> t.getTaskId().equals(taskId));
        }
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    private void checkAgents() {
        try {
            List<String> timedOut = getTimedOutAgents();

            for (String agentId : timedOut) {
                AgentInfo info = agentRegistry.get(agentId);
                if (info != null && !info.isTimeout()) {
                    info.setTimeout(true);
                    info.setWasTimedOut(true);

                    fireEvent(FailoverEventType.AGENT_TIMEOUT, agentId, info.getSceneGroupId(), null);

                    log.warn("Agent timeout detected: agentId={}, lastHeartbeat={}ms ago",
                            agentId, System.currentTimeMillis() - info.getLastHeartbeat());

                    reassignTasksAuto(agentId);
                }
            }
        } catch (Exception e) {
            log.error("Error during agent check", e);
        }
    }

    private void fireEvent(FailoverEventType type, String agentId, String sceneGroupId, String taskId) {
        FailoverEvent event = new FailoverEvent(type, agentId);
        event.setSceneGroupId(sceneGroupId);
        event.setTaskId(taskId);

        for (FailoverListener listener : listeners) {
            try {
                if (listener.supports(type)) {
                    listener.onFailoverEvent(event);
                }
            } catch (Exception e) {
                log.warn("FailoverListener error: {}", e.getMessage());
            }
        }
    }

    private static class AgentInfo {
        private final String agentId;
        private final String sceneGroupId;
        private volatile long lastHeartbeat;
        private volatile boolean timeout;
        private volatile boolean wasTimedOut;

        AgentInfo(String agentId, String sceneGroupId) {
            this.agentId = agentId;
            this.sceneGroupId = sceneGroupId;
        }

        String getAgentId() { return agentId; }
        String getSceneGroupId() { return sceneGroupId; }
        long getLastHeartbeat() { return lastHeartbeat; }
        void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
        boolean isTimeout() { return timeout; }
        void setTimeout(boolean timeout) { this.timeout = timeout; }
        boolean wasTimedOut() { return wasTimedOut; }
        void setWasTimedOut(boolean wasTimedOut) { this.wasTimedOut = wasTimedOut; }
    }

    private static class TaskInfo {
        private final String taskId;
        private final Object taskData;
        private final Instant createTime;

        TaskInfo(String taskId, Object taskData) {
            this.taskId = taskId;
            this.taskData = taskData;
            this.createTime = Instant.now();
        }

        String getTaskId() { return taskId; }
        Object getTaskData() { return taskData; }
        Instant getCreateTime() { return createTime; }
    }
}
