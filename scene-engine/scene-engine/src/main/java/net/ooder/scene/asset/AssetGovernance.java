package net.ooder.scene.asset;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AssetGovernance {
    
    void registerAsset(DigitalAsset asset);
    
    void updateAsset(String assetId, Map<String, Object> updates);
    
    void decommissionAsset(String assetId);
    
    Optional<DigitalAsset> getAsset(String assetId);
    
    List<DigitalAsset> queryAssets(AssetQuery query);
    
    List<DigitalAsset> getAssetsByType(DigitalAsset.AssetType type);
    
    List<DigitalAsset> getAssetsByCategory(DigitalAsset.AssetCategory category);
    
    List<DigitalAsset> getAssetsByStatus(DigitalAsset.AssetStatus status);
    
    List<DigitalAsset> getAssetsByOwner(String ownerId);
    
    List<DigitalAsset> getAssetsByLocation(String location);
    
    List<DigitalAsset> searchAssets(String keyword);
    
    CompletableFuture<Void> transferOwnership(String assetId, String newOwnerId);
    
    CompletableFuture<Void> updateStatus(String assetId, DigitalAsset.AssetStatus status);
    
    CompletableFuture<AssetHealth> checkHealth(String assetId);
    
    CompletableFuture<List<AssetHealth>> checkAllHealth();
    
    AssetStatistics getStatistics();
    
    void addAssetListener(AssetListener listener);
    
    void removeAssetListener(AssetListener listener);
    
    int getAssetCount();
    
    void clear();
    
    class AssetQuery {
        private DigitalAsset.AssetType type;
        private DigitalAsset.AssetCategory category;
        private DigitalAsset.AssetStatus status;
        private String ownerId;
        private String location;
        private String keyword;
        private List<String> tags;
        private Map<String, Object> metadataFilters;
        private int limit = 100;
        private int offset = 0;
        
        public DigitalAsset.AssetType getType() { return type; }
        public void setType(DigitalAsset.AssetType type) { this.type = type; }
        
        public DigitalAsset.AssetCategory getCategory() { return category; }
        public void setCategory(DigitalAsset.AssetCategory category) { this.category = category; }
        
        public DigitalAsset.AssetStatus getStatus() { return status; }
        public void setStatus(DigitalAsset.AssetStatus status) { this.status = status; }
        
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public Map<String, Object> getMetadataFilters() { return metadataFilters; }
        public void setMetadataFilters(Map<String, Object> metadataFilters) { this.metadataFilters = metadataFilters; }
        
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
        
        public int getOffset() { return offset; }
        public void setOffset(int offset) { this.offset = offset; }
        
        public static AssetQuery create() {
            return new AssetQuery();
        }
        
        public AssetQuery withType(DigitalAsset.AssetType type) {
            this.type = type;
            return this;
        }
        
        public AssetQuery withCategory(DigitalAsset.AssetCategory category) {
            this.category = category;
            return this;
        }
        
        public AssetQuery withStatus(DigitalAsset.AssetStatus status) {
            this.status = status;
            return this;
        }
        
        public AssetQuery withOwner(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }
        
        public AssetQuery withLocation(String location) {
            this.location = location;
            return this;
        }
        
        public AssetQuery withKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }
    }
    
    class AssetHealth {
        private String assetId;
        private boolean healthy;
        private String status;
        private String message;
        private Map<String, Object> metrics;
        private long checkedAt;
        
        public String getAssetId() { return assetId; }
        public void setAssetId(String assetId) { this.assetId = assetId; }
        
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
        
        public long getCheckedAt() { return checkedAt; }
        public void setCheckedAt(long checkedAt) { this.checkedAt = checkedAt; }
    }
    
    class AssetStatistics {
        private int totalAssets;
        private int activeAssets;
        private int inactiveAssets;
        private Map<DigitalAsset.AssetType, Integer> assetsByType;
        private Map<DigitalAsset.AssetStatus, Integer> assetsByStatus;
        
        public int getTotalAssets() { return totalAssets; }
        public void setTotalAssets(int totalAssets) { this.totalAssets = totalAssets; }
        
        public int getActiveAssets() { return activeAssets; }
        public void setActiveAssets(int activeAssets) { this.activeAssets = activeAssets; }
        
        public int getInactiveAssets() { return inactiveAssets; }
        public void setInactiveAssets(int inactiveAssets) { this.inactiveAssets = inactiveAssets; }
        
        public Map<DigitalAsset.AssetType, Integer> getAssetsByType() { return assetsByType; }
        public void setAssetsByType(Map<DigitalAsset.AssetType, Integer> assetsByType) { this.assetsByType = assetsByType; }
        
        public Map<DigitalAsset.AssetStatus, Integer> getAssetsByStatus() { return assetsByStatus; }
        public void setAssetsByStatus(Map<DigitalAsset.AssetStatus, Integer> assetsByStatus) { this.assetsByStatus = assetsByStatus; }
    }
    
    interface AssetListener {
        
        void onAssetRegistered(DigitalAsset asset);
        
        void onAssetUpdated(DigitalAsset asset, Map<String, Object> updates);
        
        void onAssetDecommissioned(String assetId);
        
        void onAssetStatusChanged(String assetId, DigitalAsset.AssetStatus oldStatus, DigitalAsset.AssetStatus newStatus);
        
        void onAssetOwnershipTransferred(String assetId, String oldOwner, String newOwner);
    }
}
