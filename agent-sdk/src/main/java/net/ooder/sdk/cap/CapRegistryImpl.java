package net.ooder.sdk.cap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CapRegistryImpl implements CapRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(CapRegistryImpl.class);
    
    private final Map<String, CapDefinition> definitionByAddress = new ConcurrentHashMap<>();
    private final Map<String, CapDefinition> definitionByName = new ConcurrentHashMap<>();
    private final Map<String, List<String>> versionHistory = new ConcurrentHashMap<>();
    
    private final CapYamlParser yamlParser;
    private final CapVersionManager versionManager;
    
    public CapRegistryImpl() {
        this.yamlParser = new CapYamlParser();
        this.versionManager = new CapVersionManager();
    }
    
    @Override
    public void register(CapDefinition definition) {
        if (definition == null || definition.getMetadata() == null) {
            throw new IllegalArgumentException("CAP definition cannot be null");
        }
        
        String capId = definition.getCapId();
        String name = definition.getName();
        String version = definition.getVersion();
        
        if (!CapAddress.isValidAddress(capId)) {
            throw new IllegalArgumentException("Invalid CAP address: " + capId);
        }
        
        definitionByAddress.put(capId, definition);
        definitionByName.put(name, definition);
        
        versionHistory.computeIfAbsent(capId, k -> new ArrayList<>()).add(version);
        versionManager.recordVersion(capId, version);
        
        log.info("Registered CAP: {} ({}) version {}", name, capId, version);
    }
    
    @Override
    public void unregister(String capId) {
        CapDefinition definition = definitionByAddress.remove(capId);
        if (definition != null) {
            definitionByName.remove(definition.getName());
            versionHistory.remove(capId);
            log.info("Unregistered CAP: {} ({})", definition.getName(), capId);
        }
    }
    
    @Override
    public Optional<CapDefinition> getDefinition(String capId) {
        return Optional.ofNullable(definitionByAddress.get(capId));
    }
    
    @Override
    public Optional<CapDefinition> getByName(String name) {
        return Optional.ofNullable(definitionByName.get(name));
    }
    
    @Override
    public List<CapDefinition> getAllDefinitions() {
        return new ArrayList<>(definitionByAddress.values());
    }
    
    @Override
    public List<CapDefinition> getByCategory(CapCategory category) {
        List<CapDefinition> result = new ArrayList<>();
        for (CapDefinition def : definitionByAddress.values()) {
            if (category.contains(def.getCapId())) {
                result.add(def);
            }
        }
        return result;
    }
    
    @Override
    public List<CapDefinition> getByStatus(String status) {
        List<CapDefinition> result = new ArrayList<>();
        for (CapDefinition def : definitionByAddress.values()) {
            if (status.equals(def.getMetadata().getStatus())) {
                result.add(def);
            }
        }
        return result;
    }
    
    @Override
    public List<CapDefinition> search(String query) {
        String lowerQuery = query.toLowerCase();
        List<CapDefinition> result = new ArrayList<>();
        
        for (CapDefinition def : definitionByAddress.values()) {
            if (def.getName().toLowerCase().contains(lowerQuery) ||
                def.getMetadata().getDescription().toLowerCase().contains(lowerQuery)) {
                result.add(def);
            }
        }
        return result;
    }
    
    @Override
    public boolean exists(String capId) {
        return definitionByAddress.containsKey(capId);
    }
    
    @Override
    public boolean isCompatible(String capId, String version) {
        CapDefinition definition = definitionByAddress.get(capId);
        if (definition == null) {
            return false;
        }
        return versionManager.isCompatible(definition.getVersion(), version);
    }
    
    @Override
    public String getLatestVersion(String capId) {
        return versionManager.getLatestVersion(capId);
    }
    
    @Override
    public List<String> getVersions(String capId) {
        return versionManager.getVersions(capId);
    }
    
    @Override
    public void loadFromDirectory(String directory) {
        Path dirPath = Paths.get(directory);
        if (!Files.isDirectory(dirPath)) {
            log.warn("CAP directory does not exist: {}", directory);
            return;
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    Path yamlPath = path.resolve("cap.yaml");
                    if (Files.exists(yamlPath)) {
                        try {
                            CapDefinition definition = yamlParser.parse(yamlPath);
                            register(definition);
                        } catch (Exception e) {
                            log.error("Failed to load CAP from: {}", yamlPath, e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to scan CAP directory: {}", directory, e);
        }
    }
    
    @Override
    public void loadFromClasspath(String path) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is != null) {
                CapDefinition definition = yamlParser.parse(is);
                register(definition);
            }
        } catch (Exception e) {
            log.error("Failed to load CAP from classpath: {}", path, e);
        }
    }
    
    @Override
    public void reload() {
        log.info("Reloading CAP registry...");
        clear();
    }
    
    @Override
    public int size() {
        return definitionByAddress.size();
    }
    
    @Override
    public void clear() {
        definitionByAddress.clear();
        definitionByName.clear();
        versionHistory.clear();
        log.info("CAP registry cleared");
    }
}
