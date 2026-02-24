package net.ooder.sdk.asset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AssetGovernanceImpl implements AssetGovernance {
    
    private static final Logger log = LoggerFactory.getLogger(AssetGovernanceImpl.class);
    
    private final Map<String, DigitalAsset> assets = new ConcurrentHashMap<>();
    private final Map<String, AssetHealth> healthStatus = new ConcurrentHashMap<>();
    private final List<AssetListener> listeners = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public void registerAsset(DigitalAsset asset) {
        if (asset == null || asset.getAssetId() == null) {
            throw new IllegalArgumentException("Asset and asset ID cannot be null");
        }
        
        assets.put(asset.getAssetId(), asset);
        
        AssetHealth health = new AssetHealth();
        health.setAssetId(asset.getAssetId());
        health.setHealthy(true);
        health.setStatus("REGISTERED");
        health.setMessage("Asset registered successfully");
        health.setCheckedAt(System.currentTimeMillis());
        healthStatus.put(asset.getAssetId(), health);
        
        notifyAssetRegistered(asset);
        
        log.info("Asset registered: {} type={} category={}", 
            asset.getAssetId(), asset.getType(), asset.getCategory());
    }
    
    @Override
    public void updateAsset(String assetId, Map<String, Object> updates) {
        DigitalAsset existing = assets.get(assetId);
        if (existing == null) {
            throw new IllegalArgumentException("Asset not found: " + assetId);
        }
        
        DigitalAssetBuilder builder = DigitalAsset.builder()
            .assetId(existing.getAssetId())
            .type(existing.getType())
            .category(existing.getCategory())
            .name(existing.getName())
            .description(existing.getDescription())
            .status(existing.getStatus())
            .metadata(existing.getMetadata())
            .createdAt(existing.getCreatedAt())
            .updatedAt(Instant.now())
            .ownerId(existing.getOwnerId())
            .location(existing.getLocation())
            .tags(existing.getTags());
        
        if (updates.containsKey("name")) {
            builder.name((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            builder.description((String) updates.get("description"));
        }
        if (updates.containsKey("location")) {
            builder.location((String) updates.get("location"));
        }
        if (updates.containsKey("tags")) {
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) updates.get("tags");
            builder.tags(tags);
        }
        
        DigitalAsset updated = builder.build();
        assets.put(assetId, updated);
        
        notifyAssetUpdated(updated, updates);
        
        log.info("Asset updated: {}", assetId);
    }
    
    @Override
    public void decommissionAsset(String assetId) {
        DigitalAsset asset = assets.get(assetId);
        if (asset == null) {
            return;
        }
        
        DigitalAsset decommissioned = DigitalAsset.builder()
            .assetId(asset.getAssetId())
            .type(asset.getType())
            .category(asset.getCategory())
            .name(asset.getName())
            .description(asset.getDescription())
            .status(DigitalAsset.AssetStatus.DECOMMISSIONED)
            .metadata(asset.getMetadata())
            .createdAt(asset.getCreatedAt())
            .updatedAt(Instant.now())
            .ownerId(asset.getOwnerId())
            .location(asset.getLocation())
            .tags(asset.getTags())
            .build();
        
        assets.put(assetId, decommissioned);
        
        AssetHealth health = healthStatus.get(assetId);
        if (health != null) {
            health.setHealthy(false);
            health.setStatus("DECOMMISSIONED");
        }
        
        notifyAssetDecommissioned(assetId);
        
        log.info("Asset decommissioned: {}", assetId);
    }
    
    @Override
    public Optional<DigitalAsset> getAsset(String assetId) {
        return Optional.ofNullable(assets.get(assetId));
    }
    
    @Override
    public List<DigitalAsset> queryAssets(AssetQuery query) {
        return assets.values().stream()
            .filter(asset -> matchesQuery(asset, query))
            .skip(query.getOffset())
            .limit(query.getLimit())
            .collect(Collectors.toList());
    }
    
    private boolean matchesQuery(DigitalAsset asset, AssetQuery query) {
        if (query.getType() != null && asset.getType() != query.getType()) {
            return false;
        }
        if (query.getCategory() != null && asset.getCategory() != query.getCategory()) {
            return false;
        }
        if (query.getStatus() != null && asset.getStatus() != query.getStatus()) {
            return false;
        }
        if (query.getOwnerId() != null && !query.getOwnerId().equals(asset.getOwnerId())) {
            return false;
        }
        if (query.getLocation() != null && !query.getLocation().equals(asset.getLocation())) {
            return false;
        }
        if (query.getKeyword() != null && !matchesKeyword(asset, query.getKeyword())) {
            return false;
        }
        return true;
    }
    
    private boolean matchesKeyword(DigitalAsset asset, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return (asset.getName() != null && asset.getName().toLowerCase().contains(lowerKeyword)) ||
               (asset.getDescription() != null && asset.getDescription().toLowerCase().contains(lowerKeyword)) ||
               (asset.getTags() != null && asset.getTags().stream().anyMatch(t -> t.toLowerCase().contains(lowerKeyword)));
    }
    
    @Override
    public List<DigitalAsset> getAssetsByType(DigitalAsset.AssetType type) {
        return assets.values().stream()
            .filter(a -> a.getType() == type)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DigitalAsset> getAssetsByCategory(DigitalAsset.AssetCategory category) {
        return assets.values().stream()
            .filter(a -> a.getCategory() == category)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DigitalAsset> getAssetsByStatus(DigitalAsset.AssetStatus status) {
        return assets.values().stream()
            .filter(a -> a.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DigitalAsset> getAssetsByOwner(String ownerId) {
        return assets.values().stream()
            .filter(a -> ownerId.equals(a.getOwnerId()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DigitalAsset> getAssetsByLocation(String location) {
        return assets.values().stream()
            .filter(a -> location.equals(a.getLocation()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DigitalAsset> searchAssets(String keyword) {
        return assets.values().stream()
            .filter(a -> matchesKeyword(a, keyword))
            .collect(Collectors.toList());
    }
    
    @Override
    public CompletableFuture<Void> transferOwnership(String assetId, String newOwnerId) {
        return CompletableFuture.runAsync(() -> {
            DigitalAsset asset = assets.get(assetId);
            if (asset == null) {
                throw new IllegalArgumentException("Asset not found: " + assetId);
            }
            
            String oldOwner = asset.getOwnerId();
            
            DigitalAsset updated = DigitalAsset.builder()
                .assetId(asset.getAssetId())
                .type(asset.getType())
                .category(asset.getCategory())
                .name(asset.getName())
                .description(asset.getDescription())
                .status(asset.getStatus())
                .metadata(asset.getMetadata())
                .createdAt(asset.getCreatedAt())
                .updatedAt(Instant.now())
                .ownerId(newOwnerId)
                .location(asset.getLocation())
                .tags(asset.getTags())
                .build();
            
            assets.put(assetId, updated);
            
            notifyAssetOwnershipTransferred(assetId, oldOwner, newOwnerId);
            
            log.info("Asset ownership transferred: {} from {} to {}", assetId, oldOwner, newOwnerId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> updateStatus(String assetId, DigitalAsset.AssetStatus status) {
        return CompletableFuture.runAsync(() -> {
            DigitalAsset asset = assets.get(assetId);
            if (asset == null) {
                throw new IllegalArgumentException("Asset not found: " + assetId);
            }
            
            DigitalAsset.AssetStatus oldStatus = asset.getStatus();
            
            DigitalAsset updated = DigitalAsset.builder()
                .assetId(asset.getAssetId())
                .type(asset.getType())
                .category(asset.getCategory())
                .name(asset.getName())
                .description(asset.getDescription())
                .status(status)
                .metadata(asset.getMetadata())
                .createdAt(asset.getCreatedAt())
                .updatedAt(Instant.now())
                .ownerId(asset.getOwnerId())
                .location(asset.getLocation())
                .tags(asset.getTags())
                .build();
            
            assets.put(assetId, updated);
            
            notifyAssetStatusChanged(assetId, oldStatus, status);
            
            log.info("Asset status updated: {} from {} to {}", assetId, oldStatus, status);
        }, executor);
    }
    
    @Override
    public CompletableFuture<AssetHealth> checkHealth(String assetId) {
        return CompletableFuture.supplyAsync(() -> {
            DigitalAsset asset = assets.get(assetId);
            if (asset == null) {
                AssetHealth health = new AssetHealth();
                health.setAssetId(assetId);
                health.setHealthy(false);
                health.setStatus("NOT_FOUND");
                health.setMessage("Asset not found");
                return health;
            }
            
            AssetHealth health = new AssetHealth();
            health.setAssetId(assetId);
            health.setHealthy(asset.getStatus() == DigitalAsset.AssetStatus.ACTIVE);
            health.setStatus(asset.getStatus().name());
            health.setMessage(health.isHealthy() ? "Asset is healthy" : "Asset is not active");
            health.setCheckedAt(System.currentTimeMillis());
            health.setMetrics(new HashMap<>());
            
            healthStatus.put(assetId, health);
            
            return health;
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<AssetHealth>> checkAllHealth() {
        return CompletableFuture.supplyAsync(() -> {
            List<AssetHealth> results = new ArrayList<>();
            for (String assetId : assets.keySet()) {
                results.add(checkHealth(assetId).join());
            }
            return results;
        }, executor);
    }
    
    @Override
    public AssetStatistics getStatistics() {
        AssetStatistics stats = new AssetStatistics();
        stats.setTotalAssets(assets.size());
        stats.setActiveAssets((int) assets.values().stream()
            .filter(a -> a.getStatus() == DigitalAsset.AssetStatus.ACTIVE)
            .count());
        stats.setInactiveAssets((int) assets.values().stream()
            .filter(a -> a.getStatus() != DigitalAsset.AssetStatus.ACTIVE)
            .count());
        
        Map<DigitalAsset.AssetType, Integer> byType = new EnumMap<>(DigitalAsset.AssetType.class);
        Map<DigitalAsset.AssetStatus, Integer> byStatus = new EnumMap<>(DigitalAsset.AssetStatus.class);
        
        for (DigitalAsset asset : assets.values()) {
            byType.merge(asset.getType(), 1, Integer::sum);
            byStatus.merge(asset.getStatus(), 1, Integer::sum);
        }
        
        stats.setAssetsByType(byType);
        stats.setAssetsByStatus(byStatus);
        
        return stats;
    }
    
    @Override
    public void addAssetListener(AssetListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void removeAssetListener(AssetListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public int getAssetCount() {
        return assets.size();
    }
    
    @Override
    public void clear() {
        assets.clear();
        healthStatus.clear();
        log.info("All assets cleared");
    }
    
    private void notifyAssetRegistered(DigitalAsset asset) {
        for (AssetListener listener : listeners) {
            try {
                listener.onAssetRegistered(asset);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyAssetUpdated(DigitalAsset asset, Map<String, Object> updates) {
        for (AssetListener listener : listeners) {
            try {
                listener.onAssetUpdated(asset, updates);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyAssetDecommissioned(String assetId) {
        for (AssetListener listener : listeners) {
            try {
                listener.onAssetDecommissioned(assetId);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyAssetStatusChanged(String assetId, DigitalAsset.AssetStatus oldStatus, DigitalAsset.AssetStatus newStatus) {
        for (AssetListener listener : listeners) {
            try {
                listener.onAssetStatusChanged(assetId, oldStatus, newStatus);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
    
    private void notifyAssetOwnershipTransferred(String assetId, String oldOwner, String newOwner) {
        for (AssetListener listener : listeners) {
            try {
                listener.onAssetOwnershipTransferred(assetId, oldOwner, newOwner);
            } catch (Exception e) {
                log.warn("Listener error", e);
            }
        }
    }
}
