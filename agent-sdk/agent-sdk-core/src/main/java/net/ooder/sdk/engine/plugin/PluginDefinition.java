package net.ooder.sdk.engine.plugin;

import java.util.Map;
import java.util.Set;

/**
 * 插件定义
 */
public class PluginDefinition {
    
    private String pluginId;
    private String name;
    private String version;
    private String description;
    private String author;
    private Set<String> dependencies;
    private Map<String, Object> config;
    private String mainClass;
    private boolean enabled;
    private PluginStatus status;
    private long loadTime;
    
    public enum PluginStatus {
        UNLOADED,
        LOADED,
        ENABLED,
        DISABLED,
        ERROR
    }
    
    public String getPluginId() {
        return pluginId;
    }
    
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Set<String> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    public String getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public PluginStatus getStatus() {
        return status;
    }
    
    public void setStatus(PluginStatus status) {
        this.status = status;
    }
    
    public long getLoadTime() {
        return loadTime;
    }
    
    public void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }
}
