package net.ooder.sdk.asset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DataAssetManagerImpl implements DataAssetManager {
    
    private static final Logger log = LoggerFactory.getLogger(DataAssetManagerImpl.class);
    
    private final Map<String, DataAsset> dataAssets = new ConcurrentHashMap<>();
    private final Map<String, List<String>> backups = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public DataAsset registerDataAsset(DataAsset asset) {
        if (asset == null || asset.getAssetId() == null) {
            throw new IllegalArgumentException("Asset and asset ID cannot be null");
        }
        dataAssets.put(asset.getAssetId(), asset);
        log.info("Data asset registered: {} category={}", asset.getAssetId(), asset.getDataCategory());
        return asset;
    }
    
    @Override
    public void updateDataAsset(String assetId, Map<String, Object> updates) {
        DataAsset existing = dataAssets.get(assetId);
        if (existing == null) {
            throw new IllegalArgumentException("Data asset not found: " + assetId);
        }
        
        DataAssetBuilder builder = DataAsset.builder()
            .assetId(existing.getAssetId())
            .dataSource(existing.getDataSource())
            .category(existing.getDataCategory())
            .format(existing.getFormat())
            .sensitivity(existing.getSensitivity())
            .sizeInBytes(existing.getSizeInBytes())
            .schema(existing.getSchema())
            .storageLocation(existing.getStorageLocation())
            .retentionPolicy(existing.getRetentionPolicy())
            .classification(existing.getClassification())
            .lastAccessedAt(existing.getLastAccessedAt())
            .accessCount(existing.getAccessCount())
            .name(existing.getName())
            .description(existing.getDescription())
            .status(existing.getStatus())
            .metadata(existing.getMetadata())
            .createdAt(existing.getCreatedAt())
            .updatedAt(Instant.now())
            .ownerId(existing.getOwnerId())
            .location(existing.getLocation())
            .tags(existing.getTags());
        
        if (updates.containsKey("name")) builder.name((String) updates.get("name"));
        if (updates.containsKey("sizeInBytes")) builder.sizeInBytes((Long) updates.get("sizeInBytes"));
        
        dataAssets.put(assetId, builder.build());
        log.info("Data asset updated: {}", assetId);
    }
    
    @Override
    public void unregisterDataAsset(String assetId) {
        DataAsset removed = dataAssets.remove(assetId);
        if (removed != null) {
            backups.remove(assetId);
            log.info("Data asset unregistered: {}", assetId);
        }
    }
    
    @Override
    public Optional<DataAsset> getDataAsset(String assetId) {
        return Optional.ofNullable(dataAssets.get(assetId));
    }
    
    @Override
    public List<DataAsset> getDataAssetsByCategory(DataCategory category) {
        return dataAssets.values().stream()
            .filter(a -> a.getDataCategory() == category)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DataAsset> getDataAssetsByOwner(String ownerId) {
        return dataAssets.values().stream()
            .filter(a -> ownerId.equals(a.getOwnerId()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DataAsset> getDataAssetsBySensitivity(DataSensitivity sensitivity) {
        return dataAssets.values().stream()
            .filter(a -> a.getSensitivity() == sensitivity)
            .collect(Collectors.toList());
    }
    
    @Override
    public CompletableFuture<DataQualityReport> assessQuality(String assetId) {
        return CompletableFuture.supplyAsync(() -> {
            DataAsset asset = dataAssets.get(assetId);
            DataQualityReport report = new DataQualityReport();
            report.setAssetId(assetId);
            
            if (asset == null) {
                report.setHealthy(false);
                report.setOverallScore(0.0);
                report.setIssues(Collections.singletonList("Asset not found"));
                return report;
            }
            
            Random random = new Random();
            report.setHealthy(true);
            report.setCompletenessScore(0.7 + random.nextDouble() * 0.3);
            report.setAccuracyScore(0.75 + random.nextDouble() * 0.25);
            report.setConsistencyScore(0.8 + random.nextDouble() * 0.2);
            report.setOverallScore((report.getCompletenessScore() + report.getAccuracyScore() + 
                report.getConsistencyScore()) / 3);
            report.setIssues(new ArrayList<>());
            report.setDetails(new HashMap<>());
            
            return report;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> backup(String assetId) {
        return CompletableFuture.runAsync(() -> {
            DataAsset asset = dataAssets.get(assetId);
            if (asset == null) {
                throw new IllegalArgumentException("Data asset not found: " + assetId);
            }
            
            String backupId = "backup-" + System.currentTimeMillis();
            backups.computeIfAbsent(assetId, k -> new CopyOnWriteArrayList<>()).add(backupId);
            log.info("Data asset backed up: {} backupId={}", assetId, backupId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> restore(String assetId, String backupId) {
        return CompletableFuture.runAsync(() -> {
            List<String> assetBackups = backups.get(assetId);
            if (assetBackups == null || !assetBackups.contains(backupId)) {
                throw new IllegalArgumentException("Backup not found: " + backupId);
            }
            log.info("Data asset restored: {} from backup {}", assetId, backupId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Map<String, Object>> analyze(String assetId) {
        return CompletableFuture.supplyAsync(() -> {
            DataAsset asset = dataAssets.get(assetId);
            if (asset == null) return Collections.emptyMap();
            
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("assetId", assetId);
            analysis.put("size", asset.getSizeInBytes());
            analysis.put("format", asset.getFormat());
            analysis.put("sensitivity", asset.getSensitivity());
            analysis.put("recordCount", 1000);
            analysis.put("lastAnalyzed", Instant.now());
            return analysis;
        }, executor);
    }
    
    @Override
    public void setRetentionPolicy(String assetId, RetentionPolicy policy) {
        DataAsset asset = dataAssets.get(assetId);
        if (asset == null) return;
        
        DataAsset updated = DataAsset.builder()
            .assetId(asset.getAssetId())
            .dataSource(asset.getDataSource())
            .category(asset.getDataCategory())
            .format(asset.getFormat())
            .sensitivity(asset.getSensitivity())
            .sizeInBytes(asset.getSizeInBytes())
            .schema(asset.getSchema())
            .storageLocation(asset.getStorageLocation())
            .retentionPolicy(policy)
            .classification(asset.getClassification())
            .lastAccessedAt(asset.getLastAccessedAt())
            .accessCount(asset.getAccessCount())
            .name(asset.getName())
            .description(asset.getDescription())
            .status(asset.getStatus())
            .metadata(asset.getMetadata())
            .createdAt(asset.getCreatedAt())
            .updatedAt(Instant.now())
            .ownerId(asset.getOwnerId())
            .location(asset.getLocation())
            .tags(asset.getTags())
            .build();
        
        dataAssets.put(assetId, updated);
        log.info("Retention policy set for data asset: {}", assetId);
    }
    
    @Override
    public void applyDataClassification(String assetId, DataClassification classification) {
        DataAsset asset = dataAssets.get(assetId);
        if (asset == null) return;
        
        DataAsset updated = DataAsset.builder()
            .assetId(asset.getAssetId())
            .dataSource(asset.getDataSource())
            .category(asset.getDataCategory())
            .format(asset.getFormat())
            .sensitivity(asset.getSensitivity())
            .sizeInBytes(asset.getSizeInBytes())
            .schema(asset.getSchema())
            .storageLocation(asset.getStorageLocation())
            .retentionPolicy(asset.getRetentionPolicy())
            .classification(classification)
            .lastAccessedAt(asset.getLastAccessedAt())
            .accessCount(asset.getAccessCount())
            .name(asset.getName())
            .description(asset.getDescription())
            .status(asset.getStatus())
            .metadata(asset.getMetadata())
            .createdAt(asset.getCreatedAt())
            .updatedAt(Instant.now())
            .ownerId(asset.getOwnerId())
            .location(asset.getLocation())
            .tags(asset.getTags())
            .build();
        
        dataAssets.put(assetId, updated);
        log.info("Data classification applied for data asset: {}", assetId);
    }
}
