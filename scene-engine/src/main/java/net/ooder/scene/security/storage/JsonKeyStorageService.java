package net.ooder.scene.security.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.sdk.api.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

/**
 * JSON文件存储实现 - 密钥管理
 *
 * <p>通过 {@link net.ooder.scene.security.config.KeyManagementAutoConfiguration} 注册为Bean</p>
 *
 * @author ooder
 * @since 2.3.1
 */
public class JsonKeyStorageService {

    private static final Logger logger = LoggerFactory.getLogger(JsonKeyStorageService.class);

    private final ObjectMapper objectMapper;
    private final Map<String, ReentrantReadWriteLock> locks;
    private final ScheduledExecutorService cleanupExecutor;

    @Value("${scene.engine.key.storage.root:data/keys}")
    private String storageRoot;

    @Value("${scene.engine.key.lock.cleanup.interval:3600}")
    private int lockCleanupIntervalSeconds;

    public JsonKeyStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.locks = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "key-lock-cleanup");
            t.setDaemon(true);
            return t;
        });
    }

    @PostConstruct
    public void init() {
        initStorageDirectories();
        startLockCleanupTask();
        logger.info("JsonKeyStorageService initialized with root: {}", storageRoot);
    }

    private void initStorageDirectories() {
        try {
            Files.createDirectories(Paths.get(storageRoot, "keys"));
            Files.createDirectories(Paths.get(storageRoot, "requests"));
            Files.createDirectories(Paths.get(storageRoot, "rules"));
            Files.createDirectories(Paths.get(storageRoot, "logs"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage directories", e);
        }
    }

    private void startLockCleanupTask() {
        cleanupExecutor.scheduleAtFixedRate(
            this::cleanupExpiredLocks,
            lockCleanupIntervalSeconds,
            lockCleanupIntervalSeconds,
            TimeUnit.SECONDS
        );
    }

    public void saveKey(KeyEntity key) {
        String lockKey = "key:" + key.getKeyId();
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "keys", key.getKeyId() + ".json");
            writeJsonFile(filePath, key);
            logger.debug("Saved key: {}", key.getKeyId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public KeyEntity loadKey(String keyId) {
        String lockKey = "key:" + keyId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.readLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "keys", keyId + ".json");
            return readJsonFile(filePath, KeyEntity.class);
        } finally {
            lock.readLock().unlock();
        }
    }

    public KeyEntity loadKeyByValue(String keyValue) {
        Path keysDir = Paths.get(storageRoot, "keys");
        if (!Files.exists(keysDir)) {
            return null;
        }
        
        try (Stream<Path> paths = Files.list(keysDir)) {
            return paths
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> readJsonFile(path, KeyEntity.class))
                .filter(Objects::nonNull)
                .filter(key -> keyValue.equals(key.getKeyValue()))
                .findFirst()
                .orElse(null);
        } catch (IOException e) {
            logger.error("Failed to load key by value", e);
            return null;
        }
    }

    public void deleteKey(String keyId) {
        String lockKey = "key:" + keyId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "keys", keyId + ".json");
            Files.deleteIfExists(filePath);
            locks.remove(lockKey);
            logger.debug("Deleted key: {}", keyId);
        } catch (IOException e) {
            logger.error("Failed to delete key: {}", keyId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<KeyEntity> loadKeysByOwner(String ownerId, OwnerType ownerType) {
        Path keysDir = Paths.get(storageRoot, "keys");
        if (!Files.exists(keysDir)) {
            return new ArrayList<>();
        }
        
        try (Stream<Path> paths = Files.list(keysDir)) {
            return paths
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> readJsonFile(path, KeyEntity.class))
                .filter(Objects::nonNull)
                .filter(key -> ownerId.equals(key.getOwnerId()) && ownerType == key.getOwnerType())
                .collect(java.util.stream.Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to load keys by owner: {}", ownerId, e);
            return new ArrayList<>();
        }
    }

    public List<KeyEntity> loadKeysBySceneGroup(String sceneGroupId) {
        Path keysDir = Paths.get(storageRoot, "keys");
        if (!Files.exists(keysDir)) {
            return new ArrayList<>();
        }
        
        try (Stream<Path> paths = Files.list(keysDir)) {
            return paths
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> readJsonFile(path, KeyEntity.class))
                .filter(Objects::nonNull)
                .filter(key -> sceneGroupId.equals(key.getSceneGroupId()))
                .collect(java.util.stream.Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to load keys by scene group: {}", sceneGroupId, e);
            return new ArrayList<>();
        }
    }

    public void saveRequest(NetworkJoinRequest request) {
        String lockKey = "request:" + request.getRequestId();
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "requests", request.getRequestId() + ".json");
            writeJsonFile(filePath, request);
            logger.debug("Saved request: {}", request.getRequestId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public NetworkJoinRequest loadRequest(String requestId) {
        String lockKey = "request:" + requestId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.readLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "requests", requestId + ".json");
            return readJsonFile(filePath, NetworkJoinRequest.class);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<NetworkJoinRequest> loadPendingRequests() {
        Path requestsDir = Paths.get(storageRoot, "requests");
        if (!Files.exists(requestsDir)) {
            return new ArrayList<>();
        }
        
        try (Stream<Path> paths = Files.list(requestsDir)) {
            return paths
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> readJsonFile(path, NetworkJoinRequest.class))
                .filter(Objects::nonNull)
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to load pending requests", e);
            return new ArrayList<>();
        }
    }

    public void deleteRequest(String requestId) {
        String lockKey = "request:" + requestId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "requests", requestId + ".json");
            Files.deleteIfExists(filePath);
            locks.remove(lockKey);
            logger.debug("Deleted request: {}", requestId);
        } catch (IOException e) {
            logger.error("Failed to delete request: {}", requestId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveRule(KeyRule rule) {
        String lockKey = "rule:" + rule.getRuleId();
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "rules", rule.getRuleId() + ".json");
            writeJsonFile(filePath, rule);
            logger.debug("Saved rule: {}", rule.getRuleId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public KeyRule loadRule(String ruleId) {
        String lockKey = "rule:" + ruleId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.readLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "rules", ruleId + ".json");
            return readJsonFile(filePath, KeyRule.class);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<KeyRule> loadAllRules() {
        Path rulesDir = Paths.get(storageRoot, "rules");
        if (!Files.exists(rulesDir)) {
            return new ArrayList<>();
        }
        
        try (Stream<Path> paths = Files.list(rulesDir)) {
            return paths
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> readJsonFile(path, KeyRule.class))
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to load all rules", e);
            return new ArrayList<>();
        }
    }

    public void deleteRule(String ruleId) {
        String lockKey = "rule:" + ruleId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "rules", ruleId + ".json");
            Files.deleteIfExists(filePath);
            locks.remove(lockKey);
            logger.debug("Deleted rule: {}", ruleId);
        } catch (IOException e) {
            logger.error("Failed to delete rule: {}", ruleId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveUsageLog(KeyUsageLog log) {
        String lockKey = "log:" + log.getLogId();
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(log.getTimestamp()));
            Path filePath = Paths.get(storageRoot, "logs", date, log.getLogId() + ".json");
            writeJsonFile(filePath, log);
            logger.debug("Saved usage log: {}", log.getLogId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<KeyUsageLog> loadUsageLogs(String keyId, int limit) {
        Path logsDir = Paths.get(storageRoot, "logs");
        if (!Files.exists(logsDir)) {
            return new ArrayList<>();
        }
        
        try {
            List<KeyUsageLog> allLogs = new ArrayList<>();
            
            Files.list(logsDir)
                .filter(Files::isDirectory)
                .forEach(dateDir -> {
                    try (Stream<Path> paths = Files.list(dateDir)) {
                        paths
                            .filter(path -> path.toString().endsWith(".json"))
                            .map(path -> readJsonFile(path, KeyUsageLog.class))
                            .filter(Objects::nonNull)
                            .filter(log -> keyId.equals(log.getKeyId()))
                            .forEach(allLogs::add);
                    } catch (IOException e) {
                        logger.error("Failed to load logs from: {}", dateDir, e);
                    }
                });
            
            allLogs.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
            
            if (limit > 0 && allLogs.size() > limit) {
                return allLogs.subList(0, limit);
            }
            
            return allLogs;
        } catch (IOException e) {
            logger.error("Failed to load usage logs for key: {}", keyId, e);
            return new ArrayList<>();
        }
    }

    public String getStorageRoot() {
        return storageRoot;
    }

    private void cleanupExpiredLocks() {
        locks.entrySet().removeIf(entry -> {
            ReentrantReadWriteLock lock = entry.getValue();
            return !lock.isWriteLocked() && lock.getReadLockCount() == 0;
        });
        logger.debug("Cleaned up expired locks, remaining: {}", locks.size());
    }

    private void writeJsonFile(Path filePath, Object data) {
        try {
            Files.createDirectories(filePath.getParent());
            objectMapper.writeValue(filePath.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }

    private <T> T readJsonFile(Path filePath, Class<T> clazz) {
        if (!Files.exists(filePath)) {
            return null;
        }
        
        try {
            return objectMapper.readValue(filePath.toFile(), clazz);
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            return null;
        }
    }

    public int cleanupExpiredData(int maxAgeDays) {
        int cleanedCount = 0;
        long maxAgeMillis = maxAgeDays * 24 * 60 * 60 * 1000L;
        long cutoffTime = System.currentTimeMillis() - maxAgeMillis;

        Path keysDir = Paths.get(storageRoot, "keys");
        if (Files.exists(keysDir)) {
            try (Stream<Path> paths = Files.list(keysDir)) {
                cleanedCount += paths
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> readJsonFile(path, KeyEntity.class))
                    .filter(Objects::nonNull)
                    .filter(key -> key.getExpiresAt() > 0 && key.getExpiresAt() < cutoffTime)
                    .peek(key -> {
                        deleteKey(key.getKeyId());
                        logger.info("Cleaned expired key: {}", key.getKeyId());
                    })
                    .count();
            } catch (IOException e) {
                logger.error("Failed to cleanup expired keys", e);
            }
        }

        return cleanedCount;
    }
}
