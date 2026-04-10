package net.ooder.skill.common.spi;

import java.util.Map;

public interface ConfigService {
    
    Object getConfig(String key);
    
    <T> T getConfig(String key, Class<T> type);
    
    Map<String, Object> getConfigs(String prefix);
    
    void setConfig(String key, Object value);
    
    void deleteConfig(String key);
    
    boolean hasConfig(String key);
}
