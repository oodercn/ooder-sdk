package net.ooder.sdk.cap;

import java.util.List;
import java.util.Optional;

public interface CapRegistry {
    
    void register(CapDefinition definition);
    
    void unregister(String capId);
    
    Optional<CapDefinition> getDefinition(String capId);
    
    Optional<CapDefinition> getByName(String name);
    
    List<CapDefinition> getAllDefinitions();
    
    List<CapDefinition> getByCategory(CapCategory category);
    
    List<CapDefinition> getByStatus(String status);
    
    List<CapDefinition> search(String query);
    
    boolean exists(String capId);
    
    boolean isCompatible(String capId, String version);
    
    String getLatestVersion(String capId);
    
    List<String> getVersions(String capId);
    
    void loadFromDirectory(String directory);
    
    void loadFromClasspath(String path);
    
    void reload();
    
    int size();
    
    void clear();
}
