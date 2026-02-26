package net.ooder.sdk.engine.plugin;

import net.ooder.sdk.engine.core.EngineRuntime;

/**
 * 引擎插件接口
 */
public interface EnginePlugin {
    
    String getPluginId();
    
    String getName();
    
    String getVersion();
    
    void initialize(EngineRuntime runtime);
    
    void start();
    
    void stop();
    
    void destroy();
    
    default boolean isEnabled() {
        return true;
    }
}
