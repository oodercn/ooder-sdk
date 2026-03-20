package net.ooder.sdk.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skills.config.ConfigNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SystemDefaultsLoader {
    
    private static final Logger log = LoggerFactory.getLogger(SystemDefaultsLoader.class);
    private static final String SYSTEM_DEFAULTS_PATH = "/config/system-defaults.json";
    
    private final ObjectMapper objectMapper;
    private ConfigNode systemDefaults;
    private Map<String, ConfigNode> systemSkillConfigs;
    private Map<String, String> defaultInterfaceBindings;
    
    public SystemDefaultsLoader() {
        this.objectMapper = new ObjectMapper();
        this.systemSkillConfigs = new HashMap<>();
        this.defaultInterfaceBindings = new HashMap<>();
    }
    
    public void load() {
        log.info("Loading system defaults from: {}", SYSTEM_DEFAULTS_PATH);
        
        try (InputStream is = getClass().getResourceAsStream(SYSTEM_DEFAULTS_PATH)) {
            if (is == null) {
                log.warn("System defaults file not found: {}", SYSTEM_DEFAULTS_PATH);
                loadDefaults();
                return;
            }
            
            Map<String, Object> data = objectMapper.readValue(is, Map.class);
            parseSystemDefaults(data);
            
            log.info("System defaults loaded successfully");
        } catch (IOException e) {
            log.error("Failed to load system defaults", e);
            loadDefaults();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void parseSystemDefaults(Map<String, Object> data) {
        String version = (String) data.get("version");
        log.info("System defaults version: {}", version);
        
        Map<String, Object> systemSkills = (Map<String, Object>) data.get("systemSkills");
        if (systemSkills != null) {
            for (Map.Entry<String, Object> entry : systemSkills.entrySet()) {
                String skillId = entry.getKey();
                Map<String, Object> skillConfig = (Map<String, Object>) entry.getValue();
                systemSkillConfigs.put(skillId, new ConfigNode(skillConfig));
                log.debug("Loaded system skill config: {}", skillId);
            }
        }
        
        Map<String, String> bindings = (Map<String, String>) data.get("defaultInterfaceBindings");
        if (bindings != null) {
            defaultInterfaceBindings.putAll(bindings);
            log.debug("Loaded {} default interface bindings", bindings.size());
        }
        
        systemDefaults = new ConfigNode(data);
    }
    
    private void loadDefaults() {
        log.info("Loading hardcoded system defaults");
        
        Map<String, Object> sqliteConfig = new LinkedHashMap<>();
        sqliteConfig.put("enabled", true);
        sqliteConfig.put("autoStart", true);
        Map<String, Object> sqliteInner = new LinkedHashMap<>();
        sqliteInner.put("url", "jdbc:sqlite:./data/system.db");
        sqliteInner.put("poolSize", 5);
        sqliteInner.put("connectionTimeout", 30000);
        sqliteConfig.put("config", sqliteInner);
        systemSkillConfigs.put("skill-db-sqlite", new ConfigNode(sqliteConfig));
        
        Map<String, Object> loggingConfig = new LinkedHashMap<>();
        loggingConfig.put("enabled", true);
        loggingConfig.put("autoStart", true);
        Map<String, Object> loggingInner = new LinkedHashMap<>();
        loggingInner.put("level", "INFO");
        loggingInner.put("format", "json");
        loggingConfig.put("config", loggingInner);
        systemSkillConfigs.put("skill-logging", new ConfigNode(loggingConfig));
        
        Map<String, Object> metricsConfig = new LinkedHashMap<>();
        metricsConfig.put("enabled", true);
        metricsConfig.put("autoStart", true);
        Map<String, Object> metricsInner = new LinkedHashMap<>();
        metricsInner.put("enabled", true);
        metricsInner.put("interval", 60);
        metricsConfig.put("config", metricsInner);
        systemSkillConfigs.put("skill-metrics", new ConfigNode(metricsConfig));
        
        defaultInterfaceBindings.put("DatabaseDriver", "skill-db-sqlite");
        defaultInterfaceBindings.put("LoggingService", "skill-logging");
        defaultInterfaceBindings.put("MetricsService", "skill-metrics");
    }
    
    public ConfigNode getSystemDefaults() {
        if (systemDefaults == null) {
            load();
        }
        return systemDefaults;
    }
    
    public ConfigNode getSkillConfig(String skillId) {
        if (systemDefaults == null) {
            load();
        }
        return systemSkillConfigs.get(skillId);
    }
    
    public Map<String, ConfigNode> getAllSkillConfigs() {
        if (systemDefaults == null) {
            load();
        }
        return new HashMap<>(systemSkillConfigs);
    }
    
    public String getDefaultInterfaceBinding(String interfaceId) {
        if (defaultInterfaceBindings.isEmpty()) {
            load();
        }
        return defaultInterfaceBindings.get(interfaceId);
    }
    
    public Map<String, String> getDefaultInterfaceBindings() {
        if (defaultInterfaceBindings.isEmpty()) {
            load();
        }
        return new HashMap<>(defaultInterfaceBindings);
    }
    
    public boolean isAutoStart(String skillId) {
        ConfigNode config = getSkillConfig(skillId);
        if (config != null) {
            return config.getBoolean("autoStart", false);
        }
        return false;
    }
    
    public boolean isEnabled(String skillId) {
        ConfigNode config = getSkillConfig(skillId);
        if (config != null) {
            return config.getBoolean("enabled", false);
        }
        return false;
    }
    
    public java.util.List<String> getAutoStartSkills() {
        java.util.List<String> autoStartSkills = new java.util.ArrayList<>();
        for (Map.Entry<String, ConfigNode> entry : getAllSkillConfigs().entrySet()) {
            if (entry.getValue().getBoolean("autoStart", false)) {
                autoStartSkills.add(entry.getKey());
            }
        }
        return autoStartSkills;
    }
}
