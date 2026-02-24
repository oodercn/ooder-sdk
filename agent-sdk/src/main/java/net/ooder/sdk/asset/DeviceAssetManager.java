package net.ooder.sdk.asset;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DeviceAssetManager {
    
    DeviceAsset registerDevice(DeviceAsset device);
    
    void updateDevice(String deviceId, Map<String, Object> updates);
    
    void unregisterDevice(String deviceId);
    
    Optional<DeviceAsset> getDevice(String deviceId);
    
    List<DeviceAsset> getDevicesByPlace(String placeId);
    
    List<DeviceAsset> getDevicesByZone(String zoneId);
    
    List<DeviceAsset> getDevicesByType(String deviceType);
    
    List<DeviceAsset> getDevicesByStatus(DeviceStatus status);
    
    CompletableFuture<DeviceHealth> checkDeviceHealth(String deviceId);
    
    CompletableFuture<Void> connectDevice(String deviceId);
    
    CompletableFuture<Void> disconnectDevice(String deviceId);
    
    CompletableFuture<Void> restartDevice(String deviceId);
    
    CompletableFuture<Map<String, Object>> getDeviceMetrics(String deviceId);
    
    void assignToPlace(String deviceId, String placeId);
    
    void assignToZone(String deviceId, String zoneId);
    
    void moveToZone(String deviceId, String fromZoneId, String toZoneId);
    
    class DeviceAsset implements DigitalAsset {
        private final String assetId;
        private final String deviceId;
        private final String deviceType;
        private final String manufacturer;
        private final String model;
        private final String serialNumber;
        private final String firmwareVersion;
        private final DeviceStatus deviceStatus;
        private final String placeId;
        private final String zoneId;
        private final String ipAddress;
        private final String macAddress;
        private final Map<String, Object> configuration;
        private final Map<String, Object> capabilities;
        private final Instant lastSeenAt;
        private final DigitalAsset.AssetType type = DigitalAsset.AssetType.DEVICE;
        private final DigitalAsset.AssetCategory category;
        private final String name;
        private final String description;
        private final DigitalAsset.AssetStatus status;
        private final Map<String, Object> metadata;
        private final Instant createdAt;
        private final Instant updatedAt;
        private final String ownerId;
        private final String location;
        private final List<String> tags;
        
        public DeviceAsset(DeviceAssetBuilder builder) {
            this.assetId = builder.assetId;
            this.deviceId = builder.deviceId;
            this.deviceType = builder.deviceType;
            this.manufacturer = builder.manufacturer;
            this.model = builder.model;
            this.serialNumber = builder.serialNumber;
            this.firmwareVersion = builder.firmwareVersion;
            this.deviceStatus = builder.deviceStatus;
            this.placeId = builder.placeId;
            this.zoneId = builder.zoneId;
            this.ipAddress = builder.ipAddress;
            this.macAddress = builder.macAddress;
            this.configuration = builder.configuration;
            this.capabilities = builder.capabilities;
            this.lastSeenAt = builder.lastSeenAt;
            this.category = builder.category;
            this.name = builder.name;
            this.description = builder.description;
            this.status = builder.status;
            this.metadata = builder.metadata;
            this.createdAt = builder.createdAt;
            this.updatedAt = builder.updatedAt;
            this.ownerId = builder.ownerId;
            this.location = builder.location;
            this.tags = builder.tags;
        }
        
        @Override public String getAssetId() { return assetId; }
        @Override public DigitalAsset.AssetType getType() { return type; }
        @Override public DigitalAsset.AssetCategory getCategory() { return category; }
        @Override public String getName() { return name; }
        @Override public String getDescription() { return description; }
        @Override public DigitalAsset.AssetStatus getStatus() { return status; }
        @Override public Map<String, Object> getMetadata() { return metadata; }
        @Override public Instant getCreatedAt() { return createdAt; }
        @Override public Instant getUpdatedAt() { return updatedAt; }
        @Override public String getOwnerId() { return ownerId; }
        @Override public String getLocation() { return location; }
        @Override public List<String> getTags() { return tags; }
        
        public String getDeviceId() { return deviceId; }
        public String getDeviceType() { return deviceType; }
        public String getManufacturer() { return manufacturer; }
        public String getModel() { return model; }
        public String getSerialNumber() { return serialNumber; }
        public String getFirmwareVersion() { return firmwareVersion; }
        public DeviceStatus getDeviceStatus() { return deviceStatus; }
        public String getPlaceId() { return placeId; }
        public String getZoneId() { return zoneId; }
        public String getIpAddress() { return ipAddress; }
        public String getMacAddress() { return macAddress; }
        public Map<String, Object> getConfiguration() { return configuration; }
        public Map<String, Object> getCapabilities() { return capabilities; }
        public Instant getLastSeenAt() { return lastSeenAt; }
        
        public static DeviceAssetBuilder builder() {
            return new DeviceAssetBuilder();
        }
    }
    
    enum DeviceStatus {
        ONLINE,
        OFFLINE,
        MAINTENANCE,
        ERROR,
        UNKNOWN
    }
    
    class DeviceHealth {
        private String deviceId;
        private boolean healthy;
        private String status;
        private String message;
        private Map<String, Object> metrics;
        private long responseTime;
        private long checkedAt;
        
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
        
        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
        
        public long getCheckedAt() { return checkedAt; }
        public void setCheckedAt(long checkedAt) { this.checkedAt = checkedAt; }
    }
}
