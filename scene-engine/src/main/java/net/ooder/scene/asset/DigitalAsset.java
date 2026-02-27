package net.ooder.scene.asset;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface DigitalAsset {
    
    String getAssetId();
    
    AssetType getType();
    
    AssetCategory getCategory();
    
    String getName();
    
    String getDescription();
    
    AssetStatus getStatus();
    
    Map<String, Object> getMetadata();
    
    Instant getCreatedAt();
    
    Instant getUpdatedAt();
    
    String getOwnerId();
    
    String getLocation();
    
    List<String> getTags();
    
    enum AssetType {
        DEVICE,
        DATA,
        AGENT,
        RESOURCE
    }
    
    enum AssetCategory {
        PRODUCTION_DEVICE,
        NETWORK_DEVICE,
        STORAGE_DEVICE,
        TERMINAL_DEVICE,
        SENSOR_DEVICE,
        BUSINESS_DATA,
        OPERATIONAL_DATA,
        FINANCIAL_DATA,
        CUSTOMER_DATA,
        KNOWLEDGE_DATA,
        AI_AGENT,
        AUTOMATION_AGENT,
        MONITORING_AGENT,
        COORDINATION_AGENT,
        COMPUTE_RESOURCE,
        STORAGE_RESOURCE,
        NETWORK_RESOURCE,
        SERVICE_RESOURCE
    }
    
    enum AssetStatus {
        ACTIVE,
        INACTIVE,
        MAINTENANCE,
        DECOMMISSIONED,
        ERROR,
        UNKNOWN
    }
    
    static DigitalAssetBuilder builder() {
        return new DigitalAssetBuilder();
    }
}
