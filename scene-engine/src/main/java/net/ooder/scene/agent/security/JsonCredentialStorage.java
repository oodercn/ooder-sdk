package net.ooder.scene.agent.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

public class JsonCredentialStorage implements AgentCredentialStorage {

    private static final Logger log = LoggerFactory.getLogger(JsonCredentialStorage.class);

    private final ObjectMapper objectMapper;
    private final Map<String, ReentrantReadWriteLock> locks;
    private final Map<String, AgentCredential> memoryCache;

    @Value("${scene.engine.agent.credential.storage.root:data/agent-credentials}")
    private String storageRoot;

    public JsonCredentialStorage() {
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
            loadAllToCache();
            log.info("JsonCredentialStorage initialized with root: {}", storageRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize credential storage", e);
        }
    }

    private void loadAllToCache() {
        Path storagePath = Paths.get(storageRoot);
        if (!Files.exists(storagePath)) {
            return;
        }

        try (Stream<Path> paths = Files.list(storagePath)) {
            paths.filter(path -> path.toString().endsWith(".json"))
                    .map(this::readFromFile)
                    .filter(Objects::nonNull)
                    .forEach(cred -> memoryCache.put(cred.getCredentialId(), cred));
        } catch (IOException e) {
            log.error("Failed to load credentials to cache", e);
        }
    }

    @Override
    public String saveCredential(AgentCredential credential) {
        if (credential == null || credential.getAgentId() == null) {
            throw new IllegalArgumentException("Credential and agentId are required");
        }

        String credentialId = credential.getCredentialId();
        if (credentialId == null || credentialId.isEmpty()) {
            credentialId = UUID.randomUUID().toString().replace("-", "");
            credential.setCredentialId(credentialId);
        }

        ReentrantReadWriteLock lock = locks.computeIfAbsent(credentialId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            writeToFile(credential);
            memoryCache.put(credentialId, credential);
            log.info("Credential saved: credentialId={}, agentId={}", credentialId, credential.getAgentId());
            return credentialId;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<AgentCredential> loadCredential(String credentialId) {
        if (credentialId == null) {
            return Optional.empty();
        }

        AgentCredential cached = memoryCache.get(credentialId);
        if (cached != null) {
            return Optional.of(cached);
        }

        ReentrantReadWriteLock lock = locks.computeIfAbsent(credentialId, k -> new ReentrantReadWriteLock());
        lock.readLock().lock();
        try {
            Path filePath = getFilePath(credentialId);
            AgentCredential credential = readFromFile(filePath);
            if (credential != null) {
                memoryCache.put(credentialId, credential);
            }
            return Optional.ofNullable(credential);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<AgentCredential> loadCredentialByAgent(String agentId, CredentialType type) {
        return memoryCache.values().stream()
                .filter(cred -> agentId.equals(cred.getAgentId()))
                .filter(cred -> type == null || type == cred.getType())
                .filter(AgentCredential::isUsable)
                .findFirst();
    }

    @Override
    public List<AgentCredential> loadCredentialsByAgent(String agentId) {
        List<AgentCredential> result = new ArrayList<>();
        for (AgentCredential cred : memoryCache.values()) {
            if (agentId.equals(cred.getAgentId())) {
                result.add(cred);
            }
        }
        return result;
    }

    @Override
    public void updateCredential(AgentCredential credential) {
        if (credential == null || credential.getCredentialId() == null) {
            return;
        }

        ReentrantReadWriteLock lock = locks.computeIfAbsent(credential.getCredentialId(), k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            writeToFile(credential);
            memoryCache.put(credential.getCredentialId(), credential);
            log.debug("Credential updated: credentialId={}", credential.getCredentialId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteCredential(String credentialId) {
        if (credentialId == null) {
            return;
        }

        ReentrantReadWriteLock lock = locks.computeIfAbsent(credentialId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            Path filePath = getFilePath(credentialId);
            Files.deleteIfExists(filePath);
            memoryCache.remove(credentialId);
            locks.remove(credentialId);
            log.info("Credential deleted: credentialId={}", credentialId);
        } catch (IOException e) {
            log.error("Failed to delete credential: {}", credentialId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteCredentialsByAgent(String agentId) {
        List<String> toDelete = new ArrayList<>();
        for (AgentCredential cred : memoryCache.values()) {
            if (agentId.equals(cred.getAgentId())) {
                toDelete.add(cred.getCredentialId());
            }
        }

        for (String credentialId : toDelete) {
            deleteCredential(credentialId);
        }
    }

    @Override
    public boolean validateCredential(String agentId, String plainValue, CredentialType type) {
        if (agentId == null || plainValue == null) {
            return false;
        }

        Optional<AgentCredential> credOpt = loadCredentialByAgent(agentId, type);
        if (!credOpt.isPresent()) {
            return false;
        }

        AgentCredential credential = credOpt.get();
        if (!credential.isUsable()) {
            return false;
        }

        boolean valid = CredentialHasher.verify(
                plainValue,
                credential.getHashedValue(),
                credential.getSalt(),
                credential.getHashAlgorithm()
        );

        if (valid) {
            recordUsage(credential.getCredentialId());
        }

        return valid;
    }

    @Override
    public void recordUsage(String credentialId) {
        loadCredential(credentialId).ifPresent(cred -> {
            cred.setLastUsedAt(Instant.now());
            cred.incrementUseCount();
            updateCredential(cred);
        });
    }

    @Override
    public int cleanupExpiredCredentials() {
        int count = 0;
        List<String> toDelete = new ArrayList<>();

        for (AgentCredential cred : memoryCache.values()) {
            if (cred.isExpired()) {
                toDelete.add(cred.getCredentialId());
            }
        }

        for (String credentialId : toDelete) {
            deleteCredential(credentialId);
            count++;
        }

        if (count > 0) {
            log.info("Cleaned up {} expired credentials", count);
        }

        return count;
    }

    private Path getFilePath(String credentialId) {
        return Paths.get(storageRoot, credentialId + ".json");
    }

    private void writeToFile(AgentCredential credential) {
        try {
            Path filePath = getFilePath(credential.getCredentialId());
            Files.createDirectories(filePath.getParent());
            objectMapper.writeValue(filePath.toFile(), credential);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write credential file", e);
        }
    }

    private AgentCredential readFromFile(Path filePath) {
        if (!Files.exists(filePath)) {
            return null;
        }

        try {
            return objectMapper.readValue(filePath.toFile(), AgentCredential.class);
        } catch (IOException e) {
            log.error("Failed to read credential file: {}", filePath, e);
            return null;
        }
    }

    public int getCredentialCount() {
        return memoryCache.size();
    }

    public String getStorageRoot() {
        return storageRoot;
    }

    public void setStorageRoot(String storageRoot) {
        this.storageRoot = storageRoot;
    }
}
