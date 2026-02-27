package net.ooder.scene.asset;

import java.time.Instant;
import java.util.*;

public class DataAsset implements DigitalAsset {
    
    private final String assetId;
    private final String dataSource;
    private final DataAssetManager.DataCategory category;
    private final DataAssetManager.DataFormat format;
    private final DataAssetManager.DataSensitivity sensitivity;
    private final long sizeInBytes;
    private final String schema;
    private final String storageLocation;
    private final DataAssetManager.RetentionPolicy retentionPolicy;
    private final DataAssetManager.DataClassification classification;
    private final Instant lastAccessedAt;
    private final long accessCount;
    private final DigitalAsset.AssetType type = DigitalAsset.AssetType.DATA;
    private final String name;
    private final String description;
    private final DigitalAsset.AssetStatus status;
    private final Map<String, Object> metadata;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String ownerId;
    private final String location;
    private final List<String> tags;
    
    public DataAsset(DataAssetBuilder builder) {
        this.assetId = builder.assetId;
        this.dataSource = builder.dataSource;
        this.category = builder.category;
        this.format = builder.format;
        this.sensitivity = builder.sensitivity;
        this.sizeInBytes = builder.sizeInBytes;
        this.schema = builder.schema;
        this.storageLocation = builder.storageLocation;
        this.retentionPolicy = builder.retentionPolicy;
        this.classification = builder.classification;
        this.lastAccessedAt = builder.lastAccessedAt;
        this.accessCount = builder.accessCount;
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
        this.metadata = Collections.unmodifiableMap(builder.metadata);
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.ownerId = builder.ownerId;
        this.location = builder.location;
        this.tags = Collections.unmodifiableList(builder.tags);
    }
    
    @Override public String getAssetId() { return assetId; }
    @Override public DigitalAsset.AssetType getType() { return type; }
    @Override public DigitalAsset.AssetCategory getCategory() { 
        return category != null ? DigitalAsset.AssetCategory.valueOf(category.name()) : null; 
    }
    @Override public String getName() { return name; }
    @Override public String getDescription() { return description; }
    @Override public DigitalAsset.AssetStatus getStatus() { return status; }
    @Override public Map<String, Object> getMetadata() { return metadata; }
    @Override public Instant getCreatedAt() { return createdAt; }
    @Override public Instant getUpdatedAt() { return updatedAt; }
    @Override public String getOwnerId() { return ownerId; }
    @Override public String getLocation() { return location; }
    @Override public List<String> getTags() { return tags; }
    
    public String getDataSource() { return dataSource; }
    public DataAssetManager.DataCategory getDataCategory() { return category; }
    public DataAssetManager.DataFormat getFormat() { return format; }
    public DataAssetManager.DataSensitivity getSensitivity() { return sensitivity; }
    public long getSizeInBytes() { return sizeInBytes; }
    public String getSchema() { return schema; }
    public String getStorageLocation() { return storageLocation; }
    public DataAssetManager.RetentionPolicy getRetentionPolicy() { return retentionPolicy; }
    public DataAssetManager.DataClassification getClassification() { return classification; }
    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public long getAccessCount() { return accessCount; }
    
    public static DataAssetBuilder builder() { return new DataAssetBuilder(); }
}
