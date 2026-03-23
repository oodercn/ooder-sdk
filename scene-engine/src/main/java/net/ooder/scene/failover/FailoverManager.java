package net.ooder.scene.failover;

import java.util.List;
import java.util.Map;

public interface FailoverManager {

    void registerAgent(String agentId, String sceneGroupId);

    void unregisterAgent(String agentId);

    void updateHeartbeat(String agentId);

    List<String> getTimedOutAgents();

    List<String> getAgentsBySceneGroup(String sceneGroupId);

    void reassignTasks(String failedAgentId, String targetAgentId);

    void reassignTasksAuto(String failedAgentId);

    String selectReplacementAgent(String sceneGroupId, String failedAgentId);

    void addFailoverListener(FailoverListener listener);

    void removeFailoverListener(FailoverListener listener);

    FailoverStats getStats();

    void startMonitoring();

    void stopMonitoring();

    class FailoverStats {
        private int totalAgents;
        private int activeAgents;
        private int timedOutAgents;
        private int recoveredAgents;
        private int reassignedTasks;
        private long lastCheckTime;

        public int getTotalAgents() { return totalAgents; }
        public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }

        public int getActiveAgents() { return activeAgents; }
        public void setActiveAgents(int activeAgents) { this.activeAgents = activeAgents; }

        public int getTimedOutAgents() { return timedOutAgents; }
        public void setTimedOutAgents(int timedOutAgents) { this.timedOutAgents = timedOutAgents; }

        public int getRecoveredAgents() { return recoveredAgents; }
        public void setRecoveredAgents(int recoveredAgents) { this.recoveredAgents = recoveredAgents; }

        public int getReassignedTasks() { return reassignedTasks; }
        public void setReassignedTasks(int reassignedTasks) { this.reassignedTasks = reassignedTasks; }

        public long getLastCheckTime() { return lastCheckTime; }
        public void setLastCheckTime(long lastCheckTime) { this.lastCheckTime = lastCheckTime; }
    }
}
