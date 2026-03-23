package net.ooder.scene.agent.persistence;

import net.ooder.scene.agent.AgentMessage;

import java.util.List;
import java.util.Optional;

public interface MessagePersistence {

    String persist(AgentMessage message);

    Optional<AgentMessage> load(String messageId);

    List<AgentMessage> loadPendingByAgent(String agentId);

    List<AgentMessage> loadBySceneGroup(String sceneGroupId, int limit);

    void markDelivered(String messageId);

    void markAcknowledged(String messageId);

    void delete(String messageId);

    void deleteByAgent(String agentId);

    int cleanupExpired();

    int cleanupByAge(int maxAgeHours);

    MessageStats getStats();

    class MessageStats {
        private int totalMessages;
        private int pendingMessages;
        private int deliveredMessages;
        private int acknowledgedMessages;
        private long oldestMessageTime;
        private long newestMessageTime;

        public int getTotalMessages() { return totalMessages; }
        public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }

        public int getPendingMessages() { return pendingMessages; }
        public void setPendingMessages(int pendingMessages) { this.pendingMessages = pendingMessages; }

        public int getDeliveredMessages() { return deliveredMessages; }
        public void setDeliveredMessages(int deliveredMessages) { this.deliveredMessages = deliveredMessages; }

        public int getAcknowledgedMessages() { return acknowledgedMessages; }
        public void setAcknowledgedMessages(int acknowledgedMessages) { this.acknowledgedMessages = acknowledgedMessages; }

        public long getOldestMessageTime() { return oldestMessageTime; }
        public void setOldestMessageTime(long oldestMessageTime) { this.oldestMessageTime = oldestMessageTime; }

        public long getNewestMessageTime() { return newestMessageTime; }
        public void setNewestMessageTime(long newestMessageTime) { this.newestMessageTime = newestMessageTime; }
    }
}
