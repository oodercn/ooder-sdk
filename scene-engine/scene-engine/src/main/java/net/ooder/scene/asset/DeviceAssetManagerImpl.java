package net.ooder.scene.asset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DeviceAssetManagerImpl implements DeviceAssetManager {
    
    private static final Logger log = LoggerFactory.getLogger(DeviceAssetManagerImpl.class);
    
    private final Map<String, DeviceAsset> devices = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> placeDevices = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> zoneDevices = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public DeviceAsset registerDevice(DeviceAsset device) {
        if (device == null || device.getDeviceId() == null) {
            throw new IllegalArgumentException("Device and device ID cannot be null");
        }
        
        devices.put(device.getDeviceId(), device);
        
        if (device.getPlaceId() != null) {
            placeDevices.computeIfAbsent(device.getPlaceId(), k -> ConcurrentHashMap.newKeySet())
                .add(device.getDeviceId());
        }
        
        if (device.getZoneId() != null) {
            zoneDevices.computeIfAbsent(device.getZoneId(), k -> ConcurrentHashMap.newKeySet())
                .add(device.getDeviceId());
        }
        
        log.info("Device registered: {} type={}", device.getDeviceId(), device.getDeviceType());
        return device;
    }
    
    @Override
    public void updateDevice(String deviceId, Map<String, Object> updates) {
        DeviceAsset existing = devices.get(deviceId);
        if (existing == null) {
            throw new IllegalArgumentException("Device not found: " + deviceId);
        }
        
        DeviceAssetBuilder builder = new DeviceAssetBuilder()
            .assetId(existing.getAssetId())
            .deviceId(existing.getDeviceId())
            .deviceType(existing.getDeviceType())
            .manufacturer(existing.getManufacturer())
            .model(existing.getModel())
            .serialNumber(existing.getSerialNumber())
            .firmwareVersion(existing.getFirmwareVersion())
            .deviceStatus(existing.getDeviceStatus())
            .placeId(existing.getPlaceId())
            .zoneId(existing.getZoneId())
            .ipAddress(existing.getIpAddress())
            .macAddress(existing.getMacAddress())
            .configuration(existing.getConfiguration())
            .capabilities(existing.getCapabilities())
            .lastSeenAt(existing.getLastSeenAt())
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
        
        if (updates.containsKey("name")) builder.name((String) updates.get("name"));
        if (updates.containsKey("ipAddress")) builder.ipAddress((String) updates.get("ipAddress"));
        if (updates.containsKey("deviceStatus")) builder.deviceStatus((DeviceAssetManager.DeviceStatus) updates.get("deviceStatus"));
        
        devices.put(deviceId, builder.build());
        log.info("Device updated: {}", deviceId);
    }
    
    @Override
    public void unregisterDevice(String deviceId) {
        DeviceAsset device = devices.remove(deviceId);
        if (device != null) {
            if (device.getPlaceId() != null) {
                Set<String> placeSet = placeDevices.get(device.getPlaceId());
                if (placeSet != null) placeSet.remove(deviceId);
            }
            if (device.getZoneId() != null) {
                Set<String> zoneSet = zoneDevices.get(device.getZoneId());
                if (zoneSet != null) zoneSet.remove(deviceId);
            }
            log.info("Device unregistered: {}", deviceId);
        }
    }
    
    @Override
    public Optional<DeviceAsset> getDevice(String deviceId) {
        return Optional.ofNullable(devices.get(deviceId));
    }
    
    @Override
    public List<DeviceAsset> getDevicesByPlace(String placeId) {
        Set<String> deviceIds = placeDevices.get(placeId);
        if (deviceIds == null) return Collections.emptyList();
        return deviceIds.stream()
            .map(devices::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceAsset> getDevicesByZone(String zoneId) {
        Set<String> deviceIds = zoneDevices.get(zoneId);
        if (deviceIds == null) return Collections.emptyList();
        return deviceIds.stream()
            .map(devices::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceAsset> getDevicesByType(String deviceType) {
        return devices.values().stream()
            .filter(d -> deviceType.equals(d.getDeviceType()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceAsset> getDevicesByStatus(DeviceAssetManager.DeviceStatus status) {
        return devices.values().stream()
            .filter(d -> d.getDeviceStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public CompletableFuture<DeviceHealth> checkDeviceHealth(String deviceId) {
        return CompletableFuture.supplyAsync(() -> {
            DeviceAsset device = devices.get(deviceId);
            DeviceHealth health = new DeviceHealth();
            health.setDeviceId(deviceId);
            health.setCheckedAt(System.currentTimeMillis());
            
            if (device == null) {
                health.setHealthy(false);
                health.setStatus("NOT_FOUND");
                health.setMessage("Device not found");
                return health;
            }
            
            boolean isOnline = device.getDeviceStatus() == DeviceAssetManager.DeviceStatus.ONLINE;
            health.setHealthy(isOnline);
            health.setStatus(device.getDeviceStatus().name());
            health.setMessage(isOnline ? "Device is online" : "Device is not online");
            health.setMetrics(new HashMap<>());
            health.setResponseTime(isOnline ? 10 : -1);
            
            return health;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> connectDevice(String deviceId) {
        return CompletableFuture.runAsync(() -> {
            DeviceAsset device = devices.get(deviceId);
            if (device == null) {
                throw new IllegalArgumentException("Device not found: " + deviceId);
            }
            Map<String, Object> connectUpdate = new HashMap<>();
            connectUpdate.put("deviceStatus", DeviceAssetManager.DeviceStatus.ONLINE);
            updateDevice(deviceId, connectUpdate);
            log.info("Device connected: {}", deviceId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> disconnectDevice(String deviceId) {
        return CompletableFuture.runAsync(() -> {
            DeviceAsset device = devices.get(deviceId);
            if (device == null) {
                throw new IllegalArgumentException("Device not found: " + deviceId);
            }
            Map<String, Object> disconnectUpdate = new HashMap<>();
            disconnectUpdate.put("deviceStatus", DeviceAssetManager.DeviceStatus.OFFLINE);
            updateDevice(deviceId, disconnectUpdate);
            log.info("Device disconnected: {}", deviceId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> restartDevice(String deviceId) {
        return CompletableFuture.runAsync(() -> {
            DeviceAsset device = devices.get(deviceId);
            if (device == null) {
                throw new IllegalArgumentException("Device not found: " + deviceId);
            }
            log.info("Device restart initiated: {}", deviceId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Map<String, Object>> getDeviceMetrics(String deviceId) {
        return CompletableFuture.supplyAsync(() -> {
            DeviceAsset device = devices.get(deviceId);
            if (device == null) {
                return Collections.emptyMap();
            }
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("deviceId", deviceId);
            metrics.put("status", device.getDeviceStatus().name());
            metrics.put("lastSeen", device.getLastSeenAt());
            metrics.put("uptime", System.currentTimeMillis());
            return metrics;
        }, executor);
    }
    
    @Override
    public void assignToPlace(String deviceId, String placeId) {
        DeviceAsset device = devices.get(deviceId);
        if (device == null) return;
        
        if (device.getPlaceId() != null) {
            Set<String> oldPlaceDevices = placeDevices.get(device.getPlaceId());
            if (oldPlaceDevices != null) oldPlaceDevices.remove(deviceId);
        }
        
        placeDevices.computeIfAbsent(placeId, k -> ConcurrentHashMap.newKeySet()).add(deviceId);
        Map<String, Object> placeUpdate = new HashMap<>();
        placeUpdate.put("placeId", placeId);
        updateDevice(deviceId, placeUpdate);
        log.info("Device {} assigned to place {}", deviceId, placeId);
    }
    
    @Override
    public void assignToZone(String deviceId, String zoneId) {
        DeviceAsset device = devices.get(deviceId);
        if (device == null) return;
        
        if (device.getZoneId() != null) {
            Set<String> oldZoneDevices = zoneDevices.get(device.getZoneId());
            if (oldZoneDevices != null) oldZoneDevices.remove(deviceId);
        }
        
        zoneDevices.computeIfAbsent(zoneId, k -> ConcurrentHashMap.newKeySet()).add(deviceId);
        Map<String, Object> zoneUpdate = new HashMap<>();
        zoneUpdate.put("zoneId", zoneId);
        updateDevice(deviceId, zoneUpdate);
        log.info("Device {} assigned to zone {}", deviceId, zoneId);
    }
    
    @Override
    public void moveToZone(String deviceId, String fromZoneId, String toZoneId) {
        Set<String> fromSet = zoneDevices.get(fromZoneId);
        if (fromSet != null) fromSet.remove(deviceId);
        
        zoneDevices.computeIfAbsent(toZoneId, k -> ConcurrentHashMap.newKeySet()).add(deviceId);
        Map<String, Object> moveUpdate = new HashMap<>();
        moveUpdate.put("zoneId", toZoneId);
        updateDevice(deviceId, moveUpdate);
        log.info("Device {} moved from zone {} to {}", deviceId, fromZoneId, toZoneId);
    }
}
