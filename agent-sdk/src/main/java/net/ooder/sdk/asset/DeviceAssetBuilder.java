package net.ooder.sdk.asset;

import java.time.Instant;
import java.util.*;

public class DeviceAssetBuilder {
    
    String assetId;
    String deviceId;
    String deviceType;
    String manufacturer;
    String model;
    String serialNumber;
    String firmwareVersion;
    DeviceAssetManager.DeviceStatus deviceStatus = DeviceAssetManager.DeviceStatus.UNKNOWN;
    String placeId;
    String zoneId;
    String ipAddress;
    String macAddress;
    Map<String, Object> configuration = new HashMap<>();
    Map<String, Object> capabilities = new HashMap<>();
    Instant lastSeenAt;
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
    
    public DeviceAssetBuilder assetId(String assetId) { this.assetId = assetId; return this; }
    public DeviceAssetBuilder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
    public DeviceAssetBuilder deviceType(String deviceType) { this.deviceType = deviceType; return this; }
    public DeviceAssetBuilder manufacturer(String manufacturer) { this.manufacturer = manufacturer; return this; }
    public DeviceAssetBuilder model(String model) { this.model = model; return this; }
    public DeviceAssetBuilder serialNumber(String serialNumber) { this.serialNumber = serialNumber; return this; }
    public DeviceAssetBuilder firmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; return this; }
    public DeviceAssetBuilder deviceStatus(DeviceAssetManager.DeviceStatus deviceStatus) { this.deviceStatus = deviceStatus; return this; }
    public DeviceAssetBuilder placeId(String placeId) { this.placeId = placeId; return this; }
    public DeviceAssetBuilder zoneId(String zoneId) { this.zoneId = zoneId; return this; }
    public DeviceAssetBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
    public DeviceAssetBuilder macAddress(String macAddress) { this.macAddress = macAddress; return this; }
    public DeviceAssetBuilder configuration(Map<String, Object> configuration) { this.configuration = configuration; return this; }
    public DeviceAssetBuilder capabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; return this; }
    public DeviceAssetBuilder lastSeenAt(Instant lastSeenAt) { this.lastSeenAt = lastSeenAt; return this; }
    public DeviceAssetBuilder category(DigitalAsset.AssetCategory category) { this.category = category; return this; }
    public DeviceAssetBuilder name(String name) { this.name = name; return this; }
    public DeviceAssetBuilder description(String description) { this.description = description; return this; }
    public DeviceAssetBuilder status(DigitalAsset.AssetStatus status) { this.status = status; return this; }
    public DeviceAssetBuilder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
    public DeviceAssetBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
    public DeviceAssetBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
    public DeviceAssetBuilder ownerId(String ownerId) { this.ownerId = ownerId; return this; }
    public DeviceAssetBuilder location(String location) { this.location = location; return this; }
    public DeviceAssetBuilder tags(List<String> tags) { this.tags = tags; return this; }
    
    public DeviceAssetManager.DeviceAsset build() {
        if (assetId == null) assetId = "device-" + System.currentTimeMillis();
        if (deviceId == null) deviceId = assetId;
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (category == null) category = DigitalAsset.AssetCategory.PRODUCTION_DEVICE;
        return new DeviceAssetManager.DeviceAsset(this);
    }
}
