package net.ooder.scene.core.driver;

import net.ooder.sdk.core.InterfaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverRegistry.class);
    
    private final Map<String, Driver> drivers = new ConcurrentHashMap<String, Driver>();
    private final Map<String, InterfaceDefinition> interfaceDefinitions = new ConcurrentHashMap<String, InterfaceDefinition>();
    
    public void register(Driver driver) {
        String category = driver.getCategory();
        
        if (drivers.containsKey(category)) {
            logger.warn("Driver already registered for category: {}, replacing...", category);
        }
        
        drivers.put(category, driver);
        
        if (driver.getInterfaceDefinition() != null) {
            interfaceDefinitions.put(category, driver.getInterfaceDefinition());
        }
        
        logger.info("Registered driver: {} v{}", category, driver.getVersion());
    }
    
    public void unregister(String category) {
        Driver driver = drivers.remove(category);
        interfaceDefinitions.remove(category);
        
        if (driver != null) {
            driver.shutdown();
            logger.info("Unregistered driver: {}", category);
        }
    }
    
    public Driver getDriver(String category) {
        return drivers.get(category);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Driver> T getDriver(String category, Class<T> type) {
        Driver driver = drivers.get(category);
        if (driver != null && type.isInstance(driver)) {
            return (T) driver;
        }
        return null;
    }
    
    public InterfaceDefinition getInterfaceDefinition(String category) {
        return interfaceDefinitions.get(category);
    }
    
    public Collection<Driver> getAllDrivers() {
        return drivers.values();
    }
    
    public Collection<String> getCategories() {
        return drivers.keySet();
    }
    
    public boolean hasDriver(String category) {
        return drivers.containsKey(category);
    }
    
    public int size() {
        return drivers.size();
    }
    
    public void clear() {
        for (Driver driver : drivers.values()) {
            try {
                driver.shutdown();
            } catch (Exception e) {
                logger.error("Failed to shutdown driver: {}", driver.getCategory(), e);
            }
        }
        drivers.clear();
        interfaceDefinitions.clear();
        logger.info("All drivers cleared");
    }
    
    public net.ooder.sdk.core.driver.HealthStatus getHealthStatus(String category) {
        Driver driver = drivers.get(category);
        if (driver != null) {
            return driver.getHealthStatus();
        }
        return net.ooder.sdk.core.driver.HealthStatus.UNKNOWN;
    }
    
    public Map<String, net.ooder.sdk.core.driver.HealthStatus> getAllHealthStatus() {
        Map<String, net.ooder.sdk.core.driver.HealthStatus> status = new java.util.HashMap<String, net.ooder.sdk.core.driver.HealthStatus>();
        for (Map.Entry<String, Driver> entry : drivers.entrySet()) {
            status.put(entry.getKey(), entry.getValue().getHealthStatus());
        }
        return status;
    }
}
