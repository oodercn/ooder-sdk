package net.ooder.sdk.interfaceRegistry;

import net.ooder.sdk.api.skill.InterfaceDefinition;
import net.ooder.sdk.api.skill.InterfaceDependency;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InterfaceRegistry {
    
    void register(InterfaceDefinition definition);
    
    void unregister(String interfaceId);
    
    Optional<InterfaceDefinition> getInterface(String interfaceId);
    
    List<InterfaceDefinition> getAllInterfaces();
    
    List<InterfaceDefinition> getInterfacesByVersion(String version);
    
    boolean hasInterface(String interfaceId);
    
    boolean hasInterface(String interfaceId, String version);
    
    void registerImplementation(String interfaceId, String skillId);
    
    void unregisterImplementation(String interfaceId, String skillId);
    
    List<String> getImplementations(String interfaceId);
    
    String getPreferredImplementation(String interfaceId);
    
    void setPreferredImplementation(String interfaceId, String skillId);
    
    List<InterfaceDefinition> resolveDependencies(List<InterfaceDependency> dependencies);
    
    boolean validateDependencies(List<InterfaceDependency> dependencies);
    
    int getInterfaceCount();
    
    void clear();
    
    InterfaceRegistryStats getStats();
    
    class InterfaceRegistryStats {
        private int totalInterfaces;
        private int totalImplementations;
        private int interfacesWithImplementations;
        private int interfacesWithoutImplementations;
        private long lastUpdateTime;
        
        public int getTotalInterfaces() { return totalInterfaces; }
        public void setTotalInterfaces(int totalInterfaces) { this.totalInterfaces = totalInterfaces; }
        
        public int getTotalImplementations() { return totalImplementations; }
        public void setTotalImplementations(int totalImplementations) { this.totalImplementations = totalImplementations; }
        
        public int getInterfacesWithImplementations() { return interfacesWithImplementations; }
        public void setInterfacesWithImplementations(int interfacesWithImplementations) { this.interfacesWithImplementations = interfacesWithImplementations; }
        
        public int getInterfacesWithoutImplementations() { return interfacesWithoutImplementations; }
        public void setInterfacesWithoutImplementations(int interfacesWithoutImplementations) { this.interfacesWithoutImplementations = interfacesWithoutImplementations; }
        
        public long getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    }
}
