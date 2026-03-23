package net.ooder.sdk.api.security;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface KeyManagementService {
    
    KeyEntity generateKey(KeyGenerateRequest request);
    
    KeyEntity getKey(String keyId);
    
    KeyEntity getKeyByValue(String keyValue);
    
    List<KeyEntity> getKeysByOwner(String ownerId, OwnerType ownerType);
    
    List<KeyEntity> getKeysByScene(String sceneGroupId);
    
    List<KeyEntity> getAllKeys(KeyQueryRequest request);
    
    KeyValidationResult validateKey(String keyId, String scope);
    
    KeyValidationResult validateKeyByValue(String keyValue, String scope);
    
    boolean revokeKey(String keyId);
    
    boolean suspendKey(String keyId);
    
    boolean activateKey(String keyId);
    
    KeyEntity refreshKey(String keyId);
    
    KeyAccessResult accessResource(String keyId, String resource, String action);
    
    void recordUsage(KeyUsageLog log);
    
    KeyStats getKeyStats();
    
    List<KeyUsageLog> getUsageLogs(String keyId, int limit);
    
    CompletableFuture<KeyEntity> generateKeyAsync(KeyGenerateRequest request);
    
    CompletableFuture<KeyValidationResult> validateKeyAsync(String keyId, String scope);
    
    class KeyGenerateRequest {
        private String ownerId;
        private OwnerType ownerType;
        private KeyType keyType;
        private String keyName;
        private long expiresInSeconds;
        private int maxUseCount;
        private List<String> allowedScenes;
        private List<String> allowedOperations;
        private String sceneGroupId;
        private String agentId;
        private String deviceId;
        private boolean approvalRequired;
        private Map<String, Object> metadata;
        
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        
        public OwnerType getOwnerType() { return ownerType; }
        public void setOwnerType(OwnerType ownerType) { this.ownerType = ownerType; }
        
        public KeyType getKeyType() { return keyType; }
        public void setKeyType(KeyType keyType) { this.keyType = keyType; }
        
        public String getKeyName() { return keyName; }
        public void setKeyName(String keyName) { this.keyName = keyName; }
        
        public long getExpiresInSeconds() { return expiresInSeconds; }
        public void setExpiresInSeconds(long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }
        
        public int getMaxUseCount() { return maxUseCount; }
        public void setMaxUseCount(int maxUseCount) { this.maxUseCount = maxUseCount; }
        
        public List<String> getAllowedScenes() { return allowedScenes; }
        public void setAllowedScenes(List<String> allowedScenes) { this.allowedScenes = allowedScenes; }
        
        public List<String> getAllowedOperations() { return allowedOperations; }
        public void setAllowedOperations(List<String> allowedOperations) { this.allowedOperations = allowedOperations; }
        
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        
        public boolean isApprovalRequired() { return approvalRequired; }
        public void setApprovalRequired(boolean approvalRequired) { this.approvalRequired = approvalRequired; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    class KeyQueryRequest {
        private KeyType keyType;
        private KeyStatus status;
        private String ownerId;
        private OwnerType ownerType;
        private String sceneGroupId;
        private int pageNum;
        private int pageSize;
        
        public KeyType getKeyType() { return keyType; }
        public void setKeyType(KeyType keyType) { this.keyType = keyType; }
        
        public KeyStatus getStatus() { return status; }
        public void setStatus(KeyStatus status) { this.status = status; }
        
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        
        public OwnerType getOwnerType() { return ownerType; }
        public void setOwnerType(OwnerType ownerType) { this.ownerType = ownerType; }
        
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        
        public int getPageNum() { return pageNum; }
        public void setPageNum(int pageNum) { this.pageNum = pageNum; }
        
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    }
    
    class KeyValidationResult {
        private boolean valid;
        private KeyEntity keyEntity;
        private String errorCode;
        private String errorMessage;
        private Map<String, Object> context;
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public KeyEntity getKeyEntity() { return keyEntity; }
        public void setKeyEntity(KeyEntity keyEntity) { this.keyEntity = keyEntity; }
        
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }
    
    class KeyAccessResult {
        private boolean allowed;
        private KeyEntity keyEntity;
        private String errorCode;
        private String errorMessage;
        
        public boolean isAllowed() { return allowed; }
        public void setAllowed(boolean allowed) { this.allowed = allowed; }
        
        public KeyEntity getKeyEntity() { return keyEntity; }
        public void setKeyEntity(KeyEntity keyEntity) { this.keyEntity = keyEntity; }
        
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    class KeyStats {
        private int totalKeys;
        private int activeKeys;
        private int expiredKeys;
        private int revokedKeys;
        private int suspendedKeys;
        private long totalUsageCount;
        private long todayUsageCount;
        
        public int getTotalKeys() { return totalKeys; }
        public void setTotalKeys(int totalKeys) { this.totalKeys = totalKeys; }
        
        public int getActiveKeys() { return activeKeys; }
        public void setActiveKeys(int activeKeys) { this.activeKeys = activeKeys; }
        
        public int getExpiredKeys() { return expiredKeys; }
        public void setExpiredKeys(int expiredKeys) { this.expiredKeys = expiredKeys; }
        
        public int getRevokedKeys() { return revokedKeys; }
        public void setRevokedKeys(int revokedKeys) { this.revokedKeys = revokedKeys; }
        
        public int getSuspendedKeys() { return suspendedKeys; }
        public void setSuspendedKeys(int suspendedKeys) { this.suspendedKeys = suspendedKeys; }
        
        public long getTotalUsageCount() { return totalUsageCount; }
        public void setTotalUsageCount(long totalUsageCount) { this.totalUsageCount = totalUsageCount; }
        
        public long getTodayUsageCount() { return todayUsageCount; }
        public void setTodayUsageCount(long todayUsageCount) { this.todayUsageCount = todayUsageCount; }
    }
}
