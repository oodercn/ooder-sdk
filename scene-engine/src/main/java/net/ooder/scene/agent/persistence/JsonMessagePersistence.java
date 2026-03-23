package net.ooder.scene.agent.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.ooder.scene.agent.AgentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

public class JsonMessagePersistence implements MessagePersistence {

    private static final Logger log = LoggerFactory.getLogger(JsonMessagePersistence.class);

    private final ObjectMapper objectMapper;
    private final Map<String, ReentrantReadWriteLock> locks;
    private final Map<String, PersistedMessage> memoryCache;

    @Value("${scene.engine.message.persistence.root:data/messages}")
    private String storageRoot;

    @Value("${scene.engine.message.persistence.enabled:true}")
    private boolean enabled;

    public JsonMessagePersistence() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.locks = new ConcurrentHashMap<>();
        this.memoryCache = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(storageRoot));
            Files.createDirectories(Paths.get(storageRoot, "pending"));
            Files.createDirectories(Paths.get(storageRoot, "delivered"));
            Files.createDirectories(Paths.get(storageRoot, "acknowledged"));
            loadAllToCache();
            log.info("JsonMessagePersistence initialized: root={}, enabled={}", storageRoot, enabled);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize message persistence", e);
        }
    }

    private void loadAllToCache() {
        Path pendingDir = Paths.get(storageRoot, "pending");
        if (Files.exists(pendingDir)) {
            try (Stream<Path> paths = Files.list(pendingDir)) {
                paths.filter(path -> path.toString().endsWith(".json"))
                        .map(this::readFromFile)
                        .filter(Objects::nonNull)
                        .forEach(msg -> memoryCache.put(msg.getStorageId(), msg));
            } catch (IOException e) {
                log.error("Failed to load pending messages", e);
            }
        }
    }

    @Override
    public String persist(AgentMessage message) {
        if (!enabled || message == null) {
            return null;
        }

        PersistedMessage persisted = new PersistedMessage(message);
        String storageId = message.getMessageId();
        persisted.setStorageId(storageId);

        ReentrantReadWriteLock lock = locks.computeIfAbsent(storageId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            writeToFile(persisted);
            memoryCache.put(storageId, persisted);
            log.debug("Message persisted: storageId={}, from={}, to={}", storageId, message.getFromAgent(), message.getToAgent());
            return storageId;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<AgentMessage> load(String messageId) {
        if (messageId == null) {
            return Optional.empty();
        }

        PersistedMessage cached = memoryCache.get(messageId);
        if (cached != null) {
            return Optional.ofNullable(cached.getMessage());
        }

        ReentrantReadWriteLock lock = locks.computeIfAbsent(messageId, k -> new ReentrantReadWriteLock());
        lock.readLock().lock();
        try {
            Path filePath = findMessageFile(messageId);
            if (filePath != null) {
                PersistedMessage persisted = readFromFile(filePath);
                if (persisted != null) {
                    memoryCache.put(messageId, persisted);
                    return Optional.ofNullable(persisted.getMessage());
                }
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<AgentMessage> loadPendingByAgent(String agentId) {
        if (agentId == null) {
            return new ArrayList<>();
        }

        List<AgentMessage> result = new ArrayList<>();
        for (PersistedMessage persisted : memoryCache.values()) {
            if (persisted.isPending() && persisted.getMessage() != null) {
                AgentMessage msg = persisted.getMessage();
                if (agentId.equals(msg.getToAgent()) && !persisted.isExpired()) {
                    result.add(msg);
                }
            }
        }

        result.sort(Comparator.comparingLong(AgentMessage::getCreateTime).reversed());
        return result;
    }

    @Override
    public List<AgentMessage> loadBySceneGroup(String sceneGroupId, int limit) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }

        List<AgentMessage> result = new ArrayList<>();
        for (PersistedMessage persisted : memoryCache.values()) {
            if (persisted.getMessage() != null && sceneGroupId.equals(persisted.getMessage().getSceneGroupId())) {
                result.add(persisted.getMessage());
            }
        }

        result.sort(Comparator.comparingLong(AgentMessage::getCreateTime).reversed());

        if (limit > 0 && result.size() > limit) {
            return result.subList(0, limit);
        }

        return result;
    }

    @Override
    public void markDelivered(String messageId) {
        if (messageId == null) return;

        PersistedMessage persisted = memoryCache.get(messageId);
        if (persisted == null) return;

        ReentrantReadWriteLock lock = locks.computeIfAbsent(messageId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            persisted.markDelivered();
            moveMessageFile(persisted, "delivered");
            writeToFile(persisted);
            log.debug("Message marked as delivered: {}", messageId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void markAcknowledged(String messageId) {
        if (messageId == null) return;

        PersistedMessage persisted = memoryCache.get(messageId);
        if (persisted == null) return;

        ReentrantReadWriteLock lock = locks.computeIfAbsent(messageId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            persisted.markAcknowledged();
            moveMessageFile(persisted, "acknowledged");
            writeToFile(persisted);
            memoryCache.remove(messageId);
            log.debug("Message marked as acknowledged: {}", messageId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(String messageId) {
        if (messageId == null) return;

        ReentrantReadWriteLock lock = locks.computeIfAbsent(messageId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            Path filePath = findMessageFile(messageId);
            if (filePath != null) {
                Files.deleteIfExists(filePath);
            }
            memoryCache.remove(messageId);
            locks.remove(messageId);
            log.debug("Message deleted: {}", messageId);
        } catch (IOException e) {
            log.error("Failed to delete message: {}", messageId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteByAgent(String agentId) {
        if (agentId == null) return;

        List<String> toDelete = new ArrayList<>();
        for (PersistedMessage persisted : memoryCache.values()) {
            if (persisted.getMessage() != null) {
                AgentMessage msg = persisted.getMessage();
                if (agentId.equals(msg.getToAgent()) || agentId.equals(msg.getFromAgent())) {
                    toDelete.add(persisted.getStorageId());
                }
            }
        }

        for (String messageId : toDelete) {
            delete(messageId);
        }
    }

    @Override
    public int cleanupExpired() {
        int count = 0;
        List<String> toDelete = new ArrayList<>();

        for (PersistedMessage persisted : memoryCache.values()) {
            if (persisted.isExpired()) {
                toDelete.add(persisted.getStorageId());
            }
        }

        for (String messageId : toDelete) {
            delete(messageId);
            count++;
        }

        if (count > 0) {
            log.info("Cleaned up {} expired messages", count);
        }

        return count;
    }

    @Override
    public int cleanupByAge(int maxAgeHours) {
        int count = 0;
        Instant cutoff = Instant.now().minus(maxAgeHours, ChronoUnit.HOURS);
        List<String> toDelete = new ArrayList<>();

        for (PersistedMessage persisted : memoryCache.values()) {
            if (persisted.getCreatedAt().isBefore(cutoff)) {
                toDelete.add(persisted.getStorageId());
            }
        }

        for (String messageId : toDelete) {
            delete(messageId);
            count++;
        }

        if (count > 0) {
            log.info("Cleaned up {} messages older than {} hours", count, maxAgeHours);
        }

        return count;
    }

    @Override
    public MessageStats getStats() {
        MessageStats stats = new MessageStats();

        int total = 0;
        int pending = 0;
        int delivered = 0;
        int acknowledged = 0;
        long oldest = Long.MAX_VALUE;
        long newest = 0;

        for (PersistedMessage persisted : memoryCache.values()) {
            total++;
            switch (persisted.getStatus()) {
                case PENDING: pending++; break;
                case DELIVERED: delivered++; break;
                case ACKNOWLEDGED: acknowledged++; break;
                default: break;
            }

            if (persisted.getMessage() != null) {
                long createTime = persisted.getMessage().getCreateTime();
                if (createTime < oldest) oldest = createTime;
                if (createTime > newest) newest = createTime;
            }
        }

        stats.setTotalMessages(total);
        stats.setPendingMessages(pending);
        stats.setDeliveredMessages(delivered);
        stats.setAcknowledgedMessages(acknowledged);
        stats.setOldestMessageTime(oldest == Long.MAX_VALUE ? 0 : oldest);
        stats.setNewestMessageTime(newest);

        return stats;
    }

    private Path findMessageFile(String messageId) {
        String[] dirs = {"pending", "delivered", "acknowledged"};
        for (String dir : dirs) {
            Path filePath = Paths.get(storageRoot, dir, messageId + ".json");
            if (Files.exists(filePath)) {
                return filePath;
            }
        }
        return null;
    }

    private void writeToFile(PersistedMessage persisted) {
        try {
            String dir = getStatusDirectory(persisted.getStatus());
            Path filePath = Paths.get(storageRoot, dir, persisted.getStorageId() + ".json");
            Files.createDirectories(filePath.getParent());
            objectMapper.writeValue(filePath.toFile(), persisted);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write message file", e);
        }
    }

    private void moveMessageFile(PersistedMessage persisted, String targetDir) {
        try {
            Path oldPath = findMessageFile(persisted.getStorageId());
            if (oldPath != null) {
                Path newPath = Paths.get(storageRoot, targetDir, persisted.getStorageId() + ".json");
                Files.createDirectories(newPath.getParent());
                Files.move(oldPath, newPath);
            }
        } catch (IOException e) {
            log.error("Failed to move message file", e);
        }
    }

    private PersistedMessage readFromFile(Path filePath) {
        if (!Files.exists(filePath)) {
            return null;
        }

        try {
            return objectMapper.readValue(filePath.toFile(), PersistedMessage.class);
        } catch (IOException e) {
            log.error("Failed to read message file: {}", filePath, e);
            return null;
        }
    }

    private String getStatusDirectory(MessageStatus status) {
        switch (status) {
            case PENDING: return "pending";
            case DELIVERED: return "delivered";
            case ACKNOWLEDGED: return "acknowledged";
            default: return "pending";
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMessageCount() {
        return memoryCache.size();
    }

    public String getStorageRoot() {
        return storageRoot;
    }

    public void setStorageRoot(String storageRoot) {
        this.storageRoot = storageRoot;
    }
}
