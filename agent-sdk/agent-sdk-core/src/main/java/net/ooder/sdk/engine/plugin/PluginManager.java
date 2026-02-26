package net.ooder.sdk.engine.plugin;

import java.util.List;

/**
 * 插件管理器
 */
public interface PluginManager {
    
    void loadPlugin(PluginDefinition plugin);
    
    void unloadPlugin(String pluginId);
    
    void enablePlugin(String pluginId);
    
    void disablePlugin(String pluginId);
    
    PluginDefinition getPlugin(String pluginId);
    
    List<PluginDefinition> getAllPlugins();
    
    List<PluginDefinition> getEnabledPlugins();
    
    boolean isPluginLoaded(String pluginId);
    
    boolean isPluginEnabled(String pluginId);
    
    void registerPluginListener(PluginListener listener);
    
    void unregisterPluginListener(PluginListener listener);
    
    interface PluginListener {
        void onPluginLoaded(PluginDefinition plugin);
        void onPluginUnloaded(PluginDefinition plugin);
        void onPluginEnabled(PluginDefinition plugin);
        void onPluginDisabled(PluginDefinition plugin);
    }
}
