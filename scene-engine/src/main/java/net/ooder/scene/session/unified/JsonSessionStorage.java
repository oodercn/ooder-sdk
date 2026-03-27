package net.ooder.scene.session.unified;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JSON 文件会话存储
 *
 * <p>默认的会话存储实现，使用 JSON 文件持久化。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class JsonSessionStorage implements SessionStorage {

    private static final Logger log = LoggerFactory.getLogger(JsonSessionStorage.class);

    private final Map<String, UnifiedSession> memoryCache = new ConcurrentHashMap<>();
    private final Map<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
    
    private String storageRoot = "data/sessions";
    private boolean enabled = true;

    public JsonSessionStorage() {
    }

    public JsonSessionStorage(String storageRoot) {
        this.storageRoot = storageRoot;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(storageRoot));
            loadAllToCache();
            log.info("JsonSessionStorage initialized: root={}, sessions={}", storageRoot, memoryCache.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize session storage", e);
        }
    }

    private void loadAllToCache() {
        Path root = Paths.get(storageRoot);
        if (!Files.exists(root)) {
            return;
        }
        
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(path -> path.toString().endsWith(".json"))
                    .map(this::readFromFile)
                    .filter(Objects::nonNull)
                    .forEach(session -> memoryCache.put(session.getSessionId(), session));
        } catch (IOException e) {
            log.error("Failed to load sessions from storage", e);
        }
    }

    @Override
    public String save(UnifiedSession session) {
        if (!enabled || session == null || session.getSessionId() == null) {
            return null;
        }

        String sessionId = session.getSessionId();
        ReentrantReadWriteLock lock = locks.computeIfAbsent(sessionId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            writeToFile(session);
            memoryCache.put(sessionId, session);
            log.debug("Session saved: sessionId={}, type={}", sessionId, session.getType());
            return sessionId;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<UnifiedSession> load(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }

        UnifiedSession cached = memoryCache.get(sessionId);
        if (cached != null) {
            return Optional.of(cached);
        }

        ReentrantReadWriteLock lock = locks.computeIfAbsent(sessionId, k -> new ReentrantReadWriteLock());
        lock.readLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, sessionId + ".json");
            if (Files.exists(filePath)) {
                UnifiedSession session = readFromFile(filePath);
                if (session != null) {
                    memoryCache.put(sessionId, session);
                    return Optional.of(session);
                }
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void update(UnifiedSession session) {
        save(session);
    }

    @Override
    public void delete(String sessionId) {
        if (sessionId == null) {
            return;
        }

        ReentrantReadWriteLock lock = locks.computeIfAbsent(sessionId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, sessionId + ".json");
            Files.deleteIfExists(filePath);
            memoryCache.remove(sessionId);
            locks.remove(sessionId);
            log.debug("Session deleted: sessionId={}", sessionId);
        } catch (IOException e) {
            log.error("Failed to delete session: {}", sessionId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<UnifiedSession> findByOwner(String ownerId) {
        if (ownerId == null) {
            return new ArrayList<>();
        }
        
        return memoryCache.values().stream()
                .filter(s -> ownerId.equals(s.getOwnerId()))
                .filter(s -> !s.isExpired())
                .collect(Collectors.toList());
    }

    @Override
    public List<UnifiedSession> findBySceneGroup(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }
        
        return memoryCache.values().stream()
                .filter(s -> sceneGroupId.equals(s.getSceneGroupId()))
                .filter(s -> !s.isExpired())
                .collect(Collectors.toList());
    }

    @Override
    public List<UnifiedSession> findByType(SessionType type) {
        if (type == null) {
            return new ArrayList<>();
        }
        
        return memoryCache.values().stream()
                .filter(s -> type == s.getType())
                .filter(s -> !s.isExpired())
                .collect(Collectors.toList());
    }

    @Override
    public List<UnifiedSession> findActiveSessions() {
        return memoryCache.values().stream()
                .filter(UnifiedSession::isValid)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return memoryCache.size();
    }

    @Override
    public void cleanupExpired() {
        List<String> expiredIds = memoryCache.values().stream()
                .filter(UnifiedSession::isExpired)
                .map(UnifiedSession::getSessionId)
                .collect(Collectors.toList());
        
        for (String sessionId : expiredIds) {
            delete(sessionId);
        }
        
        if (!expiredIds.isEmpty()) {
            log.info("Cleaned up {} expired sessions", expiredIds.size());
        }
    }

    @Override
    public void clear() {
        memoryCache.clear();
        try {
            Files.createDirectories(Paths.get(storageRoot));
            try (Stream<Path> paths = Files.list(Paths.get(storageRoot))) {
                paths.filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                log.error("Failed to delete session file: {}", path, e);
                            }
                        });
            }
        } catch (IOException e) {
            log.error("Failed to clear session storage", e);
        }
        log.info("Session storage cleared");
    }

    private void writeToFile(UnifiedSession session) {
        try {
            Path filePath = Paths.get(storageRoot, session.getSessionId() + ".json");
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, JSON.toJSONString(session, JSONWriter.Feature.PrettyFormat).getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write session file", e);
        }
    }

    private UnifiedSession readFromFile(Path filePath) {
        if (!Files.exists(filePath)) {
            return null;
        }
        
        try {
            return JSON.parseObject(new String(Files.readAllBytes(filePath), "UTF-8"), UnifiedSession.class);
        } catch (IOException e) {
            log.error("Failed to read session file: {}", filePath, e);
            return null;
        }
    }

    public void setStorageRoot(String storageRoot) {
        this.storageRoot = storageRoot;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
