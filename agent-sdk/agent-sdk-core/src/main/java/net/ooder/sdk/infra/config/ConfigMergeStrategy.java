package net.ooder.sdk.infra.config;

import net.ooder.skills.config.ConfigNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigMergeStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigMergeStrategy.class);
    
    public enum Priority {
        PUSH_CONFIG(100),
        SCENE_CONFIG(80),
        PROFILE_CONFIG(60),
        SYSTEM_DEFAULT(40);
        
        private final int level;
        
        Priority(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
    }
    
    public ConfigNode merge(ConfigNode base, ConfigNode override) {
        if (base == null) {
            return override != null ? override : new ConfigNode();
        }
        if (override == null) {
            return new ConfigNode(base.toMap());
        }
        
        return base.merge(override);
    }
    
    @SuppressWarnings("unchecked")
    private void deepMerge(Map<String, Object> target, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Map && target.get(key) instanceof Map) {
                deepMerge(
                    (Map<String, Object>) target.get(key),
                    (Map<String, Object>) value
                );
            } else {
                target.put(key, value);
            }
        }
    }
    
    public ConfigNode mergeWithPriority(ConfigNode systemDefault, 
                                         ConfigNode profileConfig,
                                         ConfigNode sceneConfig,
                                         ConfigNode pushConfig) {
        ConfigNode result = new ConfigNode();
        
        if (systemDefault != null) {
            result = result.merge(systemDefault);
            log.debug("Applied system default config");
        }
        
        if (profileConfig != null) {
            result = result.merge(profileConfig);
            log.debug("Applied profile config");
        }
        
        if (sceneConfig != null) {
            result = result.merge(sceneConfig);
            log.debug("Applied scene config");
        }
        
        if (pushConfig != null) {
            result = result.merge(pushConfig);
            log.debug("Applied push config");
        }
        
        return result;
    }
    
    public ConfigNode mergeSkillConfig(String skillId,
                                        ConfigNode systemDefault,
                                        ConfigNode profileConfig,
                                        ConfigNode sceneConfig,
                                        ConfigNode pushConfig) {
        log.debug("Merging config for skill: {}", skillId);
        return mergeWithPriority(systemDefault, profileConfig, sceneConfig, pushConfig);
    }
    
    public Map<String, Object> mergeMaps(Map<String, Object> base, Map<String, Object> override) {
        if (base == null) {
            return override != null ? new LinkedHashMap<>(override) : new LinkedHashMap<>();
        }
        if (override == null) {
            return new LinkedHashMap<>(base);
        }
        
        Map<String, Object> result = new LinkedHashMap<>(base);
        deepMerge(result, override);
        return result;
    }
}
