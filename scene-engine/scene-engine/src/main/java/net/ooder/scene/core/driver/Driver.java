package net.ooder.scene.core.driver;

import net.ooder.scene.core.InterfaceDefinition;

public interface Driver {
    
    String getCategory();
    
    String getVersion();
    
    void initialize(DriverContext context);
    
    void shutdown();
    
    Object getSkill();
    
    Object getCapabilities();
    
    Object getFallback();
    
    boolean hasFallback();
    
    InterfaceDefinition getInterfaceDefinition();
    
    HealthStatus getHealthStatus();
}
