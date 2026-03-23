package net.ooder.sdk.api.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String keyId;
    private String keyValue;
    private String keyName;
    private KeyType keyType;
    private KeyStatus status;
    
    private String issuerId;
    private long issuedAt;
    
    private String ownerId;
    private OwnerType ownerType;
    
    private long expiresAt;
    private int maxUseCount;
    private int usedCount;
    
    private List<String> allowedScenes;
    private List<String> allowedOperations;
    
    private String sceneGroupId;
    private String agentId;
    private String deviceId;
    
    private long lastUsedAt;
    private long createdAt;
    private long updatedAt;
    
    private boolean approvalRequired;
    private String approvalStatus;
    private String approvedBy;
    private long approvedAt;
    private String approvalComment;
    
    private Map<String, Object> metadata;
    
    public KeyEntity() {
        this.allowedScenes = new ArrayList<String>();
        this.allowedOperations = new ArrayList<String>();
        this.metadata = new HashMap<String, Object>();
        this.status = KeyStatus.ACTIVE;
        this.usedCount = 0;
        this.approvalRequired = false;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
    
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    
    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
    
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    
    public KeyType getKeyType() { return keyType; }
    public void setKeyType(KeyType keyType) { this.keyType = keyType; }
    
    public KeyStatus getStatus() { return status; }
    public void setStatus(KeyStatus status) { this.status = status; }
    
    public String getIssuerId() { return issuerId; }
    public void setIssuerId(String issuerId) { this.issuerId = issuerId; }
    
    public long getIssuedAt() { return issuedAt; }
    public void setIssuedAt(long issuedAt) { this.issuedAt = issuedAt; }
    
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    
    public OwnerType getOwnerType() { return ownerType; }
    public void setOwnerType(OwnerType ownerType) { this.ownerType = ownerType; }
    
    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
    
    public int getMaxUseCount() { return maxUseCount; }
    public void setMaxUseCount(int maxUseCount) { this.maxUseCount = maxUseCount; }
    
    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
    
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
    
    public long getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(long lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isApprovalRequired() { return approvalRequired; }
    public void setApprovalRequired(boolean approvalRequired) { this.approvalRequired = approvalRequired; }
    
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    
    public long getApprovedAt() { return approvedAt; }
    public void setApprovedAt(long approvedAt) { this.approvedAt = approvedAt; }
    
    public String getApprovalComment() { return approvalComment; }
    public void setApprovalComment(String approvalComment) { this.approvalComment = approvalComment; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public boolean isExpired() {
        return expiresAt > 0 && System.currentTimeMillis() > expiresAt;
    }
    
    public boolean isUsageLimitReached() {
        return maxUseCount > 0 && usedCount >= maxUseCount;
    }
    
    public boolean isValid() {
        if (status != KeyStatus.ACTIVE) {
            return false;
        }
        if (isExpired()) {
            return false;
        }
        if (isUsageLimitReached()) {
            return false;
        }
        if (approvalRequired && !"APPROVED".equals(approvalStatus)) {
            return false;
        }
        return true;
    }
    
    public boolean canAccessScene(String sceneId) {
        if (!isValid()) {
            return false;
        }
        if (allowedScenes == null || allowedScenes.isEmpty()) {
            return true;
        }
        return allowedScenes.contains(sceneId);
    }
    
    public boolean canPerformOperation(String operation) {
        if (!isValid()) {
            return false;
        }
        if (allowedOperations == null || allowedOperations.isEmpty()) {
            return true;
        }
        return allowedOperations.contains(operation);
    }
    
    public void incrementUsage() {
        this.usedCount++;
        this.lastUsedAt = System.currentTimeMillis();
        this.updatedAt = this.lastUsedAt;
    }
    
    public TokenInfo<Object> toTokenInfo() {
        TokenInfo<Object> info = new TokenInfo<Object>();
        info.setSubject(ownerId);
        info.setIssuer(issuerId);
        info.setIssuedAt(issuedAt);
        info.setExpiresAt(expiresAt);
        info.setValid(isValid());
        info.setClaims(metadata);
        return info;
    }
    
    public static KeyEntity fromTokenInfo(TokenInfo<?> tokenInfo) {
        KeyEntity entity = new KeyEntity();
        entity.setOwnerId(tokenInfo.getSubject());
        entity.setIssuerId(tokenInfo.getIssuer());
        entity.setIssuedAt(tokenInfo.getIssuedAt());
        entity.setExpiresAt(tokenInfo.getExpiresAt());
        entity.setKeyType(KeyType.SESSION_TOKEN);
        entity.setStatus(tokenInfo.isValid() ? KeyStatus.ACTIVE : KeyStatus.INACTIVE);
        if (tokenInfo.getClaims() != null) {
            entity.setMetadata(new HashMap<String, Object>(tokenInfo.getClaims()));
        }
        return entity;
    }
}
