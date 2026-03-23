package net.ooder.scene.skill.engine.context.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.scene.skill.engine.context.ContextStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JSON 文件存储服务实现
 * 基于文件系统的上下文持久化实现，支持并发访问控制
 *
 * <p>变更说明: 新增实现类，提供基于JSON文件的上下文存储能力</p>
 *
 * @author ooder
 * @since 2.3.1
 */
@Service
public class JsonStorageService implements ContextStorageService {

    private static final Logger logger = LoggerFactory.getLogger(JsonStorageService.class);

    private final ObjectMapper objectMapper;
    private final Map<String, ReentrantReadWriteLock> locks;
    private final ScheduledExecutorService cleanupExecutor;

    @Value("${scene.engine.context.storage.root:data}")
    private String storageRoot;

    @Value("${scene.engine.context.lock.cleanup.interval:3600}")
    private int lockCleanupIntervalSeconds;

    @Value("${scene.engine.context.lock.expire:300}")
    private int lockExpireSeconds;

    public JsonStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.locks = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "context-lock-cleanup");
            t.setDaemon(true);
            return t;
        });
    }

    @PostConstruct
    public void init() {
        // 初始化存储目录
        initStorageDirectories();
        // 启动锁清理定时任务
        startLockCleanupTask();
        logger.info("JsonStorageService initialized with root: {}", getStorageRoot());
    }

    /**
     * 初始化存储目录结构
     */
    private void initStorageDirectories() {
        try {
            Files.createDirectories(Paths.get(getStorageRoot(), "users"));
            Files.createDirectories(Paths.get(getStorageRoot(), "sessions"));
            Files.createDirectories(Paths.get(getStorageRoot(), "skills"));
            Files.createDirectories(Paths.get(getStorageRoot(), "knowledge"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage directories", e);
        }
    }

    /**
     * 启动锁清理定时任务
     */
    private void startLockCleanupTask() {
        cleanupExecutor.scheduleAtFixedRate(
            this::cleanupExpiredLocks,
            lockCleanupIntervalSeconds,
            lockCleanupIntervalSeconds,
            TimeUnit.SECONDS
        );
    }

    // ========== 用户上下文 ==========

    @Override
    public void saveUserContext(String userId, Map<String, Object> context) {
        String lockKey = "user:" + userId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "users", userId + ".json");
            writeJsonFile(filePath, context);
            logger.debug("Saved user context for userId: {}", userId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Map<String, Object> loadUserContext(String userId) {
        String lockKey = "user:" + userId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.readLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "users", userId + ".json");
            return readJsonFile(filePath);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void deleteUserContext(String userId) {
        String lockKey = "user:" + userId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "users", userId + ".json");
            deleteFile(filePath);
            locks.remove(lockKey);
            logger.debug("Deleted user context for userId: {}", userId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== 会话上下文 ==========

    @Override
    public void saveSessionContext(String sessionId, Map<String, Object> context) {
        String lockKey = "session:" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path sessionDir = Paths.get(getStorageRoot(), "sessions", sessionId);
            Files.createDirectories(sessionDir);
            Path filePath = sessionDir.resolve("context.json");
            writeJsonFile(filePath, context);
            logger.debug("Saved session context for sessionId: {}", sessionId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save session context", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Map<String, Object> loadSessionContext(String sessionId) {
        String lockKey = "session:" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.readLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "sessions", sessionId, "context.json");
            return readJsonFile(filePath);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean sessionExists(String sessionId) {
        Path sessionDir = Paths.get(getStorageRoot(), "sessions", sessionId);
        return Files.exists(sessionDir);
    }

    @Override
    public void deleteSession(String sessionId) {
        String lockKey = "session:" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path sessionDir = Paths.get(getStorageRoot(), "sessions", sessionId);
            deleteDirectory(sessionDir);
            locks.remove(lockKey);
            logger.debug("Deleted session for sessionId: {}", sessionId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== Skill 上下文 ==========

    @Override
    public void saveSkillContext(String skillId, String sessionId, Map<String, Object> context) {
        String lockKey = "skill:" + skillId + ":" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path skillDir = Paths.get(getStorageRoot(), "skills", skillId, sessionId);
            Files.createDirectories(skillDir);
            Path filePath = skillDir.resolve("context.json");
            writeJsonFile(filePath, context);
            logger.debug("Saved skill context for skillId: {}, sessionId: {}", skillId, sessionId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skill context", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Map<String, Object> loadSkillContext(String skillId, String sessionId) {
        String lockKey = "skill:" + skillId + ":" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.readLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "skills", skillId, sessionId, "context.json");
            return readJsonFile(filePath);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void deleteSkillContext(String skillId, String sessionId) {
        String lockKey = "skill:" + skillId + ":" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path skillDir = Paths.get(getStorageRoot(), "skills", skillId, sessionId);
            deleteDirectory(skillDir);
            locks.remove(lockKey);
            logger.debug("Deleted skill context for skillId: {}, sessionId: {}", skillId, sessionId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== 对话历史 ==========

    @Override
    public void saveChatMessage(String sessionId, Map<String, Object> message) {
        String lockKey = "chat:" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path sessionDir = Paths.get(getStorageRoot(), "sessions", sessionId);
            Files.createDirectories(sessionDir);
            Path filePath = sessionDir.resolve("chat-history.json");

            List<Map<String, Object>> history = loadChatHistory(sessionId, 0);
            history.add(message);

            // 按时间戳排序
            history.sort(Comparator.comparingLong(m -> {
                Object ts = m.get("timestamp");
                return ts instanceof Number ? ((Number) ts).longValue() : 0L;
            }));

            writeJsonFile(filePath, history);
            logger.debug("Saved chat message for sessionId: {}", sessionId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save chat message", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void saveChatMessages(String sessionId, List<Map<String, Object>> messages) {
        for (Map<String, Object> message : messages) {
            saveChatMessage(sessionId, message);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> loadChatHistory(String sessionId, int limit) {
        String lockKey = "chat:" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.readLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "sessions", sessionId, "chat-history.json");
            Map<String, Object> data = readJsonFile(filePath);

            if (data.isEmpty()) {
                return new ArrayList<>();
            }

            Object historyObj = data.get("history");
            if (historyObj instanceof List) {
                List<Map<String, Object>> history = (List<Map<String, Object>>) historyObj;
                if (limit > 0 && history.size() > limit) {
                    return history.subList(history.size() - limit, history.size());
                }
                return history;
            }

            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Map<String, Object>> loadChatHistory(String sessionId, int offset, int limit) {
        List<Map<String, Object>> history = loadChatHistory(sessionId, 0);

        if (offset >= history.size()) {
            return new ArrayList<>();
        }

        int endIndex = Math.min(offset + limit, history.size());
        return history.subList(offset, endIndex);
    }

    @Override
    public void clearChatHistory(String sessionId) {
        String lockKey = "chat:" + sessionId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "sessions", sessionId, "chat-history.json");
            deleteFile(filePath);
            logger.debug("Cleared chat history for sessionId: {}", sessionId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ========== 页面状态 ==========

    @Override
    public void savePageState(String sessionId, String pageId, Map<String, Object> state) {
        String lockKey = "page:" + sessionId + ":" + pageId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path pagesDir = Paths.get(getStorageRoot(), "sessions", sessionId, "pages");
            Files.createDirectories(pagesDir);
            Path filePath = pagesDir.resolve(pageId + ".json");
            writeJsonFile(filePath, state);
            logger.debug("Saved page state for sessionId: {}, pageId: {}", sessionId, pageId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save page state", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Map<String, Object> loadPageState(String sessionId, String pageId) {
        String lockKey = "page:" + sessionId + ":" + pageId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.readLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "sessions", sessionId, "pages", pageId + ".json");
            return readJsonFile(filePath);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void deletePageState(String sessionId, String pageId) {
        String lockKey = "page:" + sessionId + ":" + pageId;
        ReentrantReadWriteLock lock = getLock(lockKey);

        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(getStorageRoot(), "sessions", sessionId, "pages", pageId + ".json");
            deleteFile(filePath);
            locks.remove(lockKey);
            logger.debug("Deleted page state for sessionId: {}, pageId: {}", sessionId, pageId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Map<String, Map<String, Object>> loadAllPageStates(String sessionId) {
        Map<String, Map<String, Object>> result = new HashMap<>();
        Path pagesDir = Paths.get(getStorageRoot(), "sessions", sessionId, "pages");

        if (!Files.exists(pagesDir)) {
            return result;
        }

        try (Stream<Path> paths = Files.list(pagesDir)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(filePath -> {
                    String pageId = filePath.getFileName().toString().replace(".json", "");
                    Map<String, Object> state = loadPageState(sessionId, pageId);
                    if (!state.isEmpty()) {
                        result.put(pageId, state);
                    }
                });
        } catch (IOException e) {
            logger.error("Failed to load page states for sessionId: {}", sessionId, e);
        }

        return result;
    }

    // ========== 工具方法 ==========

    @Override
    public String getStorageRoot() {
        return storageRoot;
    }

    @Override
    public int cleanupExpiredData(int maxAgeDays) {
        int cleanedCount = 0;
        long maxAgeMillis = maxAgeDays * 24 * 60 * 60 * 1000L;
        long cutoffTime = System.currentTimeMillis() - maxAgeMillis;

        // 清理过期的会话数据
        Path sessionsDir = Paths.get(getStorageRoot(), "sessions");
        if (Files.exists(sessionsDir)) {
            try (Stream<Path> paths = Files.list(sessionsDir)) {
                cleanedCount += paths.filter(Files::isDirectory)
                    .filter(dir -> isExpired(dir, cutoffTime))
                    .peek(dir -> {
                        deleteDirectory(dir);
                        logger.info("Cleaned expired session: {}", dir.getFileName());
                    })
                    .count();
            } catch (IOException e) {
                logger.error("Failed to cleanup expired sessions", e);
            }
        }

        return cleanedCount;
    }

    // ========== 私有辅助方法 ==========

    /**
     * 获取或创建锁
     */
    private ReentrantReadWriteLock getLock(String key) {
        return locks.computeIfAbsent(key, k -> new ReentrantReadWriteLock());
    }

    /**
     * 清理过期的锁
     */
    private void cleanupExpiredLocks() {
        locks.entrySet().removeIf(entry -> {
            ReentrantReadWriteLock lock = entry.getValue();
            // 清理未被持有且读锁计数为0的锁
            return !lock.isWriteLocked() && lock.getReadLockCount() == 0;
        });
        logger.debug("Cleaned up expired locks, remaining: {}", locks.size());
    }

    /**
     * 写入 JSON 文件
     */
    private void writeJsonFile(Path filePath, Object data) {
        try {
            Files.createDirectories(filePath.getParent());
            objectMapper.writeValue(filePath.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }

    /**
     * 读取 JSON 文件
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> readJsonFile(Path filePath) {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(filePath.toFile(), new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            return new HashMap<>();
        }
    }

    /**
     * 删除文件
     */
    private void deleteFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", filePath, e);
        }
    }

    /**
     * 递归删除目录
     */
    private void deleteDirectory(Path dir) {
        if (!Files.exists(dir)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        logger.error("Failed to delete: {}", path, e);
                    }
                });
        } catch (IOException e) {
            logger.error("Failed to delete directory: {}", dir, e);
        }
    }

    /**
     * 检查目录是否过期
     */
    private boolean isExpired(Path dir, long cutoffTime) {
        try {
            return Files.getLastModifiedTime(dir).toMillis() < cutoffTime;
        } catch (IOException e) {
            return false;
        }
    }
}
