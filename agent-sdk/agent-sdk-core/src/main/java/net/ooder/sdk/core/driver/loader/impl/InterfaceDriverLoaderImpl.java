package net.ooder.sdk.core.driver.loader.impl;

import net.ooder.sdk.core.driver.loader.InterfaceDriverLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InterfaceDriverLoaderImpl implements InterfaceDriverLoader {
    
    private static final Logger log = LoggerFactory.getLogger(InterfaceDriverLoaderImpl.class);
    
    private final Map<String, Map<String, Object>> drivers = new ConcurrentHashMap<>();
    private final Map<String, String> preferredDrivers = new ConcurrentHashMap<>();
    private volatile boolean autoDiscovery = true;
    private volatile int autoDiscoveredCount = 0;
    
    @Override
    public <T> Optional<T> load(String interfaceId, Class<T> interfaceType) {
        if (interfaceId == null || interfaceType == null) {
            return Optional.empty();
        }
        
        String preferredSkillId = getPreferredDriver(interfaceId);
        if (preferredSkillId != null) {
            Optional<T> driver = load(interfaceId, preferredSkillId, interfaceType);
            if (driver.isPresent()) {
                return driver;
            }
        }
        
        Map<String, Object> interfaceDrivers = drivers.get(interfaceId);
        if (interfaceDrivers != null && !interfaceDrivers.isEmpty()) {
            for (Map.Entry<String, Object> entry : interfaceDrivers.entrySet()) {
                try {
                    T driver = interfaceType.cast(entry.getValue());
                    log.debug("Loaded driver for interface {}: {}", interfaceId, entry.getKey());
                    return Optional.of(driver);
                } catch (ClassCastException e) {
                    log.debug("Driver {} is not compatible with type {}", entry.getKey(), interfaceType.getName());
                }
            }
        }
        
        return loadFallback(interfaceId, interfaceType);
    }
    
    @Override
    public <T> Optional<T> load(String interfaceId, String skillId, Class<T> interfaceType) {
        if (interfaceId == null || skillId == null || interfaceType == null) {
            return Optional.empty();
        }
        
        Map<String, Object> interfaceDrivers = drivers.get(interfaceId);
        if (interfaceDrivers == null) {
            return Optional.empty();
        }
        
        Object driver = interfaceDrivers.get(skillId);
        if (driver == null) {
            return Optional.empty();
        }
        
        try {
            T typedDriver = interfaceType.cast(driver);
            log.debug("Loaded specific driver: {} -> {}", interfaceId, skillId);
            return Optional.of(typedDriver);
        } catch (ClassCastException e) {
            log.warn("Driver {} is not compatible with type {}", skillId, interfaceType.getName());
            return Optional.empty();
        }
    }
    
    @Override
    public <T> Optional<T> loadFallback(String interfaceId, Class<T> interfaceType) {
        log.debug("No fallback driver found for interface: {}", interfaceId);
        return Optional.empty();
    }
    
    @Override
    public void registerDriver(String interfaceId, String skillId, Object driver) {
        if (interfaceId == null || skillId == null || driver == null) {
            throw new IllegalArgumentException("Interface ID, Skill ID, and driver cannot be null");
        }
        
        Map<String, Object> interfaceDrivers = drivers.computeIfAbsent(interfaceId, k -> new ConcurrentHashMap<>());
        interfaceDrivers.put(skillId, driver);
        
        log.info("Driver registered: {} -> {}", interfaceId, skillId);
    }
    
    @Override
    public void unregisterDriver(String interfaceId, String skillId) {
        if (interfaceId == null || skillId == null) {
            return;
        }
        
        Map<String, Object> interfaceDrivers = drivers.get(interfaceId);
        if (interfaceDrivers != null) {
            Object removed = interfaceDrivers.remove(skillId);
            if (removed != null) {
                log.info("Driver unregistered: {} -> {}", interfaceId, skillId);
            }
        }
        
        if (skillId.equals(preferredDrivers.get(interfaceId))) {
            preferredDrivers.remove(interfaceId);
        }
    }
    
    @Override
    public List<String> getAvailableDrivers(String interfaceId) {
        if (interfaceId == null) {
            return Collections.emptyList();
        }
        
        Map<String, Object> interfaceDrivers = drivers.get(interfaceId);
        return interfaceDrivers != null ? new ArrayList<>(interfaceDrivers.keySet()) : Collections.emptyList();
    }
    
    @Override
    public boolean hasDriver(String interfaceId) {
        if (interfaceId == null) {
            return false;
        }
        
        Map<String, Object> interfaceDrivers = drivers.get(interfaceId);
        return interfaceDrivers != null && !interfaceDrivers.isEmpty();
    }
    
    @Override
    public boolean hasDriver(String interfaceId, String skillId) {
        if (interfaceId == null || skillId == null) {
            return false;
        }
        
        Map<String, Object> interfaceDrivers = drivers.get(interfaceId);
        return interfaceDrivers != null && interfaceDrivers.containsKey(skillId);
    }
    
    @Override
    public void setPreferredDriver(String interfaceId, String skillId) {
        if (interfaceId == null || skillId == null) {
            throw new IllegalArgumentException("Interface ID and Skill ID cannot be null");
        }
        
        preferredDrivers.put(interfaceId, skillId);
        log.info("Preferred driver set: {} -> {}", interfaceId, skillId);
    }
    
    @Override
    public String getPreferredDriver(String interfaceId) {
        if (interfaceId == null) {
            return null;
        }
        return preferredDrivers.get(interfaceId);
    }
    
    @Override
    public void setAutoDiscovery(boolean enabled) {
        this.autoDiscovery = enabled;
        log.info("Auto discovery {}", enabled ? "enabled" : "disabled");
    }
    
    @Override
    public boolean isAutoDiscoveryEnabled() {
        return autoDiscovery;
    }
    
    @Override
    public void discoverDrivers() {
        discoverDrivers("net.ooder.sdk.drivers");
    }
    
    @Override
    public void discoverDrivers(String basePackage) {
        if (!autoDiscovery) {
            log.debug("Auto discovery is disabled");
            return;
        }
        
        log.info("Discovering drivers in package: {}", basePackage);
        autoDiscoveredCount = 0;
        log.info("Driver discovery completed. Found {} drivers", autoDiscoveredCount);
    }
    
    @Override
    public int getDriverCount() {
        int count = 0;
        for (Map<String, Object> interfaceDrivers : drivers.values()) {
            count += interfaceDrivers.size();
        }
        return count;
    }
    
    @Override
    public void clear() {
        drivers.clear();
        preferredDrivers.clear();
        log.info("Driver loader cleared");
    }
    
    @Override
    public DriverLoaderStats getStats() {
        DriverLoaderStats stats = new DriverLoaderStats();
        stats.setTotalDrivers(getDriverCount());
        stats.setInterfacesWithDrivers(drivers.size());
        stats.setAutoDiscovered(autoDiscoveredCount);
        stats.setLastDiscoveryTime(System.currentTimeMillis());
        return stats;
    }
}
