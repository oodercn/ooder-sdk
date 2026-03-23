package net.ooder.sdk.api.security.impl;

import net.ooder.sdk.api.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

public class KeyManagementServiceImpl implements KeyManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(KeyManagementServiceImpl.class);
    
    private final Map<String, KeyEntity> keys;
    private final Map<String, KeyUsageLog> usageLogs;
    private final SecureRandom random;
    private final ExecutorService executor;
    
    public KeyManagementServiceImpl() {
        this.keys = new ConcurrentHashMap<String, KeyEntity>();
        this.usageLogs = new ConcurrentHashMap<String, KeyUsageLog>();
        this.random = new SecureRandom();
        this.executor = Executors.newCachedThreadPool();
    }
    
    @Override
    public KeyEntity generateKey(KeyGenerateRequest request) {
        KeyEntity entity = new KeyEntity();
        entity.setKeyId("key-" + UUID.randomUUID().toString());
        entity.setKeyValue(generateSecureKeyValue());
        entity.setKeyName(request.getKeyName());
        entity.setKeyType(request.getKeyType() != null ? request.getKeyType() : KeyType.SESSION_TOKEN);
        entity.setStatus(KeyStatus.ACTIVE);
        entity.setIssuerId("system");
        entity.setIssuedAt(System.currentTimeMillis());
        entity.setOwnerId(request.getOwnerId());
        entity.setOwnerType(request.getOwnerType() != null ? request.getOwnerType() : OwnerType.USER);
        entity.setExpiresAt(request.getExpiresInSeconds() > 0 
            ? System.currentTimeMillis() + request.getExpiresInSeconds() * 1000 
            : System.currentTimeMillis() + 86400000);
        entity.setMaxUseCount(request.getMaxUseCount() > 0 ? request.getMaxUseCount() : 1000);
        entity.setAllowedScenes(request.getAllowedScenes());
        entity.setAllowedOperations(request.getAllowedOperations());
        entity.setSceneGroupId(request.getSceneGroupId());
        entity.setAgentId(request.getAgentId());
        entity.setDeviceId(request.getDeviceId());
        entity.setApprovalRequired(request.isApprovalRequired());
        entity.setCreatedAt(System.currentTimeMillis());
        entity.setUpdatedAt(System.currentTimeMillis());
        
        keys.put(entity.getKeyId(), entity);
        
        log.info("Generated key: id={}, type={}, owner={}", 
            entity.getKeyId(), entity.getKeyType(), entity.getOwnerId());
        
        return entity;
    }
    
    @Override
    public KeyEntity getKey(String keyId) {
        return keys.get(keyId);
    }
    
    @Override
    public KeyEntity getKeyByValue(String keyValue) {
        for (KeyEntity entity : keys.values()) {
            if (entity.getKeyValue().equals(keyValue)) {
                return entity;
            }
        }
        return null;
    }
    
    @Override
    public List<KeyEntity> getKeysByOwner(String ownerId, OwnerType ownerType) {
        List<KeyEntity> result = new ArrayList<KeyEntity>();
        for (KeyEntity entity : keys.values()) {
            if (entity.getOwnerId().equals(ownerId) 
                && (ownerType == null || entity.getOwnerType() == ownerType)) {
                result.add(entity);
            }
        }
        return result;
    }
    
    @Override
    public List<KeyEntity> getKeysByScene(String sceneGroupId) {
        List<KeyEntity> result = new ArrayList<KeyEntity>();
        for (KeyEntity entity : keys.values()) {
            if (sceneGroupId.equals(entity.getSceneGroupId())) {
                result.add(entity);
            }
        }
        return result;
    }
    
    @Override
    public List<KeyEntity> getAllKeys(KeyQueryRequest request) {
        List<KeyEntity> result = new ArrayList<KeyEntity>();
        for (KeyEntity entity : keys.values()) {
            if (matchQuery(entity, request)) {
                result.add(entity);
            }
        }
        return result;
    }
    
    @Override
    public KeyValidationResult validateKey(String keyId, String scope) {
        KeyValidationResult result = new KeyValidationResult();
        KeyEntity entity = keys.get(keyId);
        
        if (entity == null) {
            result.setValid(false);
            result.setErrorCode("KEY_NOT_FOUND");
            result.setErrorMessage("Key not found: " + keyId);
            return result;
        }
        
        if (!entity.isValid()) {
            result.setValid(false);
            result.setErrorCode("KEY_INVALID");
            result.setErrorMessage("Key is not valid. Status: " + entity.getStatus());
            return result;
        }
        
        if (scope != null && !entity.canAccessScene(scope)) {
            result.setValid(false);
            result.setErrorCode("SCOPE_DENIED");
            result.setErrorMessage("Key does not have access to scope: " + scope);
            return result;
        }
        
        entity.incrementUsage();
        result.setValid(true);
        result.setKeyEntity(entity);
        
        log.debug("Key validated: id={}, scope={}", keyId, scope);
        return result;
    }
    
    @Override
    public KeyValidationResult validateKeyByValue(String keyValue, String scope) {
        KeyEntity entity = getKeyByValue(keyValue);
        if (entity == null) {
            KeyValidationResult result = new KeyValidationResult();
            result.setValid(false);
            result.setErrorCode("KEY_NOT_FOUND");
            result.setErrorMessage("Key not found for value");
            return result;
        }
        return validateKey(entity.getKeyId(), scope);
    }
    
    @Override
    public boolean revokeKey(String keyId) {
        KeyEntity entity = keys.get(keyId);
        if (entity == null) {
            return false;
        }
        entity.setStatus(KeyStatus.REVOKED);
        entity.setUpdatedAt(System.currentTimeMillis());
        log.info("Key revoked: {}", keyId);
        return true;
    }
    
    @Override
    public boolean suspendKey(String keyId) {
        KeyEntity entity = keys.get(keyId);
        if (entity == null) {
            return false;
        }
        entity.setStatus(KeyStatus.SUSPENDED);
        entity.setUpdatedAt(System.currentTimeMillis());
        log.info("Key suspended: {}", keyId);
        return true;
    }
    
    @Override
    public boolean activateKey(String keyId) {
        KeyEntity entity = keys.get(keyId);
        if (entity == null) {
            return false;
        }
        entity.setStatus(KeyStatus.ACTIVE);
        entity.setUpdatedAt(System.currentTimeMillis());
        log.info("Key activated: {}", keyId);
        return true;
    }
    
    @Override
    public KeyEntity refreshKey(String keyId) {
        KeyEntity entity = keys.get(keyId);
        if (entity == null) {
            return null;
        }
        
        entity.setKeyValue(generateSecureKeyValue());
        entity.setExpiresAt(System.currentTimeMillis() + 86400000);
        entity.setUsedCount(0);
        entity.setStatus(KeyStatus.ACTIVE);
        entity.setUpdatedAt(System.currentTimeMillis());
        
        log.info("Key refreshed: {}", keyId);
        return entity;
    }
    
    @Override
    public KeyAccessResult accessResource(String keyId, String resource, String action) {
        KeyAccessResult result = new KeyAccessResult();
        KeyEntity entity = keys.get(keyId);
        
        if (entity == null) {
            result.setAllowed(false);
            result.setErrorCode("KEY_NOT_FOUND");
            result.setErrorMessage("Key not found");
            return result;
        }
        
        if (!entity.isValid()) {
            result.setAllowed(false);
            result.setErrorCode("KEY_INVALID");
            result.setErrorMessage("Key is not valid");
            return result;
        }
        
        if (!entity.canPerformOperation(action)) {
            result.setAllowed(false);
            result.setErrorCode("ACTION_DENIED");
            result.setErrorMessage("Action not allowed: " + action);
            return result;
        }
        
        entity.incrementUsage();
        result.setAllowed(true);
        result.setKeyEntity(entity);
        
        log.debug("Key access granted: keyId={}, resource={}, action={}", keyId, resource, action);
        return result;
    }
    
    @Override
    public void recordUsage(KeyUsageLog log) {
        log.setLogId("log-" + UUID.randomUUID().toString());
        usageLogs.put(log.getLogId(), log);
        this.log.debug("Usage recorded: keyId={}, operation={}", log.getKeyId(), log.getOperation());
    }
    
    @Override
    public KeyStats getKeyStats() {
        KeyStats stats = new KeyStats();
        stats.setTotalKeys(keys.size());
        
        int active = 0, expired = 0, revoked = 0, suspended = 0;
        long totalUsage = 0;
        
        for (KeyEntity entity : keys.values()) {
            switch (entity.getStatus()) {
                case ACTIVE: active++; break;
                case EXPIRED: expired++; break;
                case REVOKED: revoked++; break;
                case SUSPENDED: suspended++; break;
                default: break;
            }
            totalUsage += entity.getUsedCount();
        }
        
        stats.setActiveKeys(active);
        stats.setExpiredKeys(expired);
        stats.setRevokedKeys(revoked);
        stats.setSuspendedKeys(suspended);
        stats.setTotalUsageCount(totalUsage);
        
        return stats;
    }
    
    @Override
    public List<KeyUsageLog> getUsageLogs(String keyId, int limit) {
        List<KeyUsageLog> result = new ArrayList<KeyUsageLog>();
        for (KeyUsageLog log : usageLogs.values()) {
            if (keyId.equals(log.getKeyId())) {
                result.add(log);
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        return result;
    }
    
    @Override
    public CompletableFuture<KeyEntity> generateKeyAsync(KeyGenerateRequest request) {
        return CompletableFuture.supplyAsync(() -> generateKey(request), executor);
    }
    
    @Override
    public CompletableFuture<KeyValidationResult> validateKeyAsync(String keyId, String scope) {
        return CompletableFuture.supplyAsync(() -> validateKey(keyId, scope), executor);
    }
    
    public void shutdown() {
        log.info("Shutting down KeyManagementService");
        executor.shutdown();
        keys.clear();
        usageLogs.clear();
        log.info("KeyManagementService shutdown complete");
    }
    
    private String generateSecureKeyValue() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }
    
    private boolean matchQuery(KeyEntity entity, KeyQueryRequest request) {
        if (request.getKeyType() != null && entity.getKeyType() != request.getKeyType()) {
            return false;
        }
        if (request.getStatus() != null && entity.getStatus() != request.getStatus()) {
            return false;
        }
        if (request.getOwnerId() != null && !entity.getOwnerId().equals(request.getOwnerId())) {
            return false;
        }
        if (request.getOwnerType() != null && entity.getOwnerType() != request.getOwnerType()) {
            return false;
        }
        if (request.getSceneGroupId() != null && !entity.getSceneGroupId().equals(request.getSceneGroupId())) {
            return false;
        }
        return true;
    }
}
