package net.ooder.sdk.driver;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DriverLoader {
    
    <T> Optional<T> load(String interfaceId, Class<T> interfaceType);
    
    <T> Optional<T> load(String interfaceId, String skillId, Class<T> interfaceType);
    
    <T> Optional<T> loadFallback(String interfaceId, Class<T> interfaceType);
    
    void registerDriver(String interfaceId, String skillId, Object driver);
    
    void unregisterDriver(String interfaceId, String skillId);
    
    List<String> getAvailableDrivers(String interfaceId);
    
    boolean hasDriver(String interfaceId);
    
    boolean hasDriver(String interfaceId, String skillId);
    
    void setPreferredDriver(String interfaceId, String skillId);
    
    String getPreferredDriver(String interfaceId);
    
    void setAutoDiscovery(boolean enabled);
    
    boolean isAutoDiscoveryEnabled();
    
    void discoverDrivers();
    
    void discoverDrivers(String basePackage);
    
    int getDriverCount();
    
    void clear();
    
    DriverLoaderStats getStats();
    
    class DriverLoaderStats {
        private int totalDrivers;
        private int interfacesWithDrivers;
        private int fallbackDrivers;
        private int autoDiscovered;
        private long lastDiscoveryTime;
        
        public int getTotalDrivers() { return totalDrivers; }
        public void setTotalDrivers(int totalDrivers) { this.totalDrivers = totalDrivers; }
        
        public int getInterfacesWithDrivers() { return interfacesWithDrivers; }
        public void setInterfacesWithDrivers(int interfacesWithDrivers) { this.interfacesWithDrivers = interfacesWithDrivers; }
        
        public int getFallbackDrivers() { return fallbackDrivers; }
        public void setFallbackDrivers(int fallbackDrivers) { this.fallbackDrivers = fallbackDrivers; }
        
        public int getAutoDiscovered() { return autoDiscovered; }
        public void setAutoDiscovered(int autoDiscovered) { this.autoDiscovered = autoDiscovered; }
        
        public long getLastDiscoveryTime() { return lastDiscoveryTime; }
        public void setLastDiscoveryTime(long lastDiscoveryTime) { this.lastDiscoveryTime = lastDiscoveryTime; }
    }
}
