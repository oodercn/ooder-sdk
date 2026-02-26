package net.ooder.sdk.api.driver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DriverConfig {
    
    protected String driverName;
    protected String driverVersion;
    protected int connectionTimeout = 30000;
    protected int maxRetries = 3;
    protected boolean simulationMode = false;
    protected Map<String, Object> properties = new ConcurrentHashMap<>();
    
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    
    public String getDriverVersion() { return driverVersion; }
    public void setDriverVersion(String driverVersion) { this.driverVersion = driverVersion; }
    
    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
    
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    
    public boolean isSimulationMode() { return simulationMode; }
    public void setSimulationMode(boolean simulationMode) { this.simulationMode = simulationMode; }
    
    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { 
        this.properties = properties != null ? properties : new ConcurrentHashMap<>(); 
    }
    
    public Object getProperty(String key) { return properties.get(key); }
    public void setProperty(String key, Object value) { properties.put(key, value); }
}
