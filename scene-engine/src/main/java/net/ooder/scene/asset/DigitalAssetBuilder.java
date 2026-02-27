package net.ooder.scene.asset;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DigitalAssetBuilder {
    
    String assetId;
    DigitalAsset.AssetType type;
    DigitalAsset.AssetCategory category;
    String name;
    String description;
    DigitalAsset.AssetStatus status = DigitalAsset.AssetStatus.ACTIVE;
    Map<String, Object> metadata = new HashMap<>();
    Instant createdAt;
    Instant updatedAt;
    String ownerId;
    String location;
    List<String> tags = new ArrayList<>();
    
    public DigitalAssetBuilder assetId(String assetId) {
        this.assetId = assetId;
        return this;
    }
    
    public DigitalAssetBuilder type(DigitalAsset.AssetType type) {
        this.type = type;
        return this;
    }
    
    public DigitalAssetBuilder category(DigitalAsset.AssetCategory category) {
        this.category = category;
        return this;
    }
    
    public DigitalAssetBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public DigitalAssetBuilder description(String description) {
        this.description = description;
        return this;
    }
    
    public DigitalAssetBuilder status(DigitalAsset.AssetStatus status) {
        this.status = status;
        return this;
    }
    
    public DigitalAssetBuilder metadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
        return this;
    }
    
    public DigitalAssetBuilder addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
    
    public DigitalAssetBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    public DigitalAssetBuilder updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
    
    public DigitalAssetBuilder ownerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }
    
    public DigitalAssetBuilder location(String location) {
        this.location = location;
        return this;
    }
    
    public DigitalAssetBuilder tags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
        return this;
    }
    
    public DigitalAssetBuilder addTag(String tag) {
        this.tags.add(tag);
        return this;
    }
    
    public DigitalAsset build() {
        if (assetId == null) {
            assetId = "asset-" + System.currentTimeMillis();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
        return new DigitalAssetImpl(this);
    }
}
