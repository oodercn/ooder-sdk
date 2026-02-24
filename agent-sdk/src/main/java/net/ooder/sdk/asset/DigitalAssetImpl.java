package net.ooder.sdk.asset;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class DigitalAssetImpl implements DigitalAsset {
    
    private final String assetId;
    private final AssetType type;
    private final AssetCategory category;
    private final String name;
    private final String description;
    private final AssetStatus status;
    private final Map<String, Object> metadata;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String ownerId;
    private final String location;
    private final List<String> tags;
    
    DigitalAssetImpl(DigitalAssetBuilder builder) {
        this.assetId = builder.assetId;
        this.type = builder.type;
        this.category = builder.category;
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
    
    @Override
    public String getAssetId() { return assetId; }
    
    @Override
    public AssetType getType() { return type; }
    
    @Override
    public AssetCategory getCategory() { return category; }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public String getDescription() { return description; }
    
    @Override
    public AssetStatus getStatus() { return status; }
    
    @Override
    public Map<String, Object> getMetadata() { return metadata; }
    
    @Override
    public Instant getCreatedAt() { return createdAt; }
    
    @Override
    public Instant getUpdatedAt() { return updatedAt; }
    
    @Override
    public String getOwnerId() { return ownerId; }
    
    @Override
    public String getLocation() { return location; }
    
    @Override
    public List<String> getTags() { return tags; }
    
    @Override
    public String toString() {
        return "DigitalAsset{" +
            "assetId='" + assetId + '\'' +
            ", type=" + type +
            ", category=" + category +
            ", name='" + name + '\'' +
            ", status=" + status +
            '}';
    }
}
