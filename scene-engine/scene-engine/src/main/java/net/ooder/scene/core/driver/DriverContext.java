package net.ooder.scene.core.driver;

import net.ooder.scene.core.InterfaceDefinition;

public class DriverContext {
    
    private String category;
    private String version;
    private InterfaceDefinition interfaceDefinition;
    private Object config;
    private Object remoteManager;
    
    public DriverContext() {
    }
    
    public DriverContext(String category, String version) {
        this.category = category;
        this.version = version;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public InterfaceDefinition getInterfaceDefinition() {
        return interfaceDefinition;
    }
    
    public void setInterfaceDefinition(InterfaceDefinition interfaceDefinition) {
        this.interfaceDefinition = interfaceDefinition;
    }
    
    public Object getConfig() {
        return config;
    }
    
    public void setConfig(Object config) {
        this.config = config;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getRemoteManager(Class<T> type) {
        if (remoteManager != null && type.isInstance(remoteManager)) {
            return (T) remoteManager;
        }
        return null;
    }
    
    public void setRemoteManager(Object remoteManager) {
        this.remoteManager = remoteManager;
    }
}
