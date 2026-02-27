package net.ooder.scene.asset;

import java.time.Instant;
import java.util.*;

public class DataAssetBuilder {
    
    String assetId;
    String dataSource;
    DataAssetManager.DataCategory category;
    DataAssetManager.DataFormat format;
    DataAssetManager.DataSensitivity sensitivity;
    long sizeInBytes;
    String schema;
    String storageLocation;
    DataAssetManager.RetentionPolicy retentionPolicy;
    DataAssetManager.DataClassification classification;
    Instant lastAccessedAt;
    long accessCount;
    String name;
    String description;
    DigitalAsset.AssetStatus status = DigitalAsset.AssetStatus.ACTIVE;
    Map<String, Object> metadata = new HashMap<>();
    Instant createdAt;
    Instant updatedAt;
    String ownerId;
    String location;
    List<String> tags = new ArrayList<>();
    
    public DataAssetBuilder assetId(String assetId) { this.assetId = assetId; return this; }
    public DataAssetBuilder dataSource(String dataSource) { this.dataSource = dataSource; return this; }
    public DataAssetBuilder category(DataAssetManager.DataCategory category) { this.category = category; return this; }
    public DataAssetBuilder format(DataAssetManager.DataFormat format) { this.format = format; return this; }
    public DataAssetBuilder sensitivity(DataAssetManager.DataSensitivity sensitivity) { this.sensitivity = sensitivity; return this; }
    public DataAssetBuilder sizeInBytes(long sizeInBytes) { this.sizeInBytes = sizeInBytes; return this; }
    public DataAssetBuilder schema(String schema) { this.schema = schema; return this; }
    public DataAssetBuilder storageLocation(String storageLocation) { this.storageLocation = storageLocation; return this; }
    public DataAssetBuilder retentionPolicy(DataAssetManager.RetentionPolicy retentionPolicy) { this.retentionPolicy = retentionPolicy; return this; }
    public DataAssetBuilder classification(DataAssetManager.DataClassification classification) { this.classification = classification; return this; }
    public DataAssetBuilder lastAccessedAt(Instant lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; return this; }
    public DataAssetBuilder accessCount(long accessCount) { this.accessCount = accessCount; return this; }
    public DataAssetBuilder name(String name) { this.name = name; return this; }
    public DataAssetBuilder description(String description) { this.description = description; return this; }
    public DataAssetBuilder status(DigitalAsset.AssetStatus status) { this.status = status; return this; }
    public DataAssetBuilder metadata(Map<String, Object> metadata) { this.metadata = metadata != null ? metadata : new HashMap<>(); return this; }
    public DataAssetBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
    public DataAssetBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
    public DataAssetBuilder ownerId(String ownerId) { this.ownerId = ownerId; return this; }
    public DataAssetBuilder location(String location) { this.location = location; return this; }
    public DataAssetBuilder tags(List<String> tags) { this.tags = tags != null ? tags : new ArrayList<>(); return this; }
    
    public DataAsset build() {
        if (assetId == null) assetId = "data-" + System.currentTimeMillis();
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        return new DataAsset(this);
    }
}
