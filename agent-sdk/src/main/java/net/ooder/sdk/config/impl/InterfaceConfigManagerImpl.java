package net.ooder.sdk.config.impl;

import net.ooder.sdk.config.InterfaceConfigManager;
import net.ooder.sdk.config.InterfaceConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InterfaceConfigManagerImpl implements InterfaceConfigManager {
    
    private static final Logger log = LoggerFactory.getLogger(InterfaceConfigManagerImpl.class);
    
    private final Map<String, InterfaceConfig> configs = new ConcurrentHashMap<>();
    private final List<InterfaceConfigListener> listeners = new CopyOnWriteArrayList<>();
    
    @Override
    public void register(String interfaceId) {
        configs.computeIfAbsent(interfaceId, InterfaceConfig::new);
        notifyConfigAdded(interfaceId);
        log.info("Registered config for interface: {}", interfaceId);
    }
    
    @Override
    public InterfaceConfig getConfig(String interfaceId) {
        return configs.get(interfaceId);
    }
    
    @Override
    public void setConfig(String interfaceId, InterfaceConfig config) {
        configs.put(interfaceId, config);
        log.info("Set config for interface: {}", interfaceId);
    }
    
    @Override
    public void setProperty(String interfaceId, String key, Object value) {
        InterfaceConfig config = getOrCreate(interfaceId);
        Object oldValue = config.getProperty(key);
        config.setProperty(key, value);
        notifyConfigChanged(interfaceId, key, oldValue, value);
    }
    
    @Override
    public Object getProperty(String interfaceId, String key) {
        InterfaceConfig config = configs.get(interfaceId);
        return config != null ? config.getProperty(key) : null;
    }
    
    @Override
    public void removeProperty(String interfaceId, String key) {
        InterfaceConfig config = configs.get(interfaceId);
        if (config != null) {
            Object oldValue = config.getProperties().remove(key);
            notifyConfigChanged(interfaceId, key, oldValue, null);
        }
    }
    
    @Override
    public boolean hasConfig(String interfaceId) {
        return configs.containsKey(interfaceId);
    }
    
    @Override
    public List<String> getConfiguredInterfaces() {
        return new ArrayList<>(configs.keySet());
    }
    
    @Override
    public void loadConfigs(String configPath) {
        try (InputStream is = new FileInputStream(configPath)) {
            Properties props = new Properties();
            props.load(is);
            
            for (String key : props.stringPropertyNames()) {
                int dotIndex = key.indexOf('.');
                if (dotIndex > 0) {
                    String interfaceId = key.substring(0, dotIndex);
                    String propKey = key.substring(dotIndex + 1);
                    String value = props.getProperty(key);
                    
                    InterfaceConfig config = getOrCreate(interfaceId);
                    
                    switch (propKey) {
                        case "preferredImplementation":
                            config.setPreferredImplementation(value);
                            break;
                        case "enabled":
                            config.setEnabled(Boolean.parseBoolean(value));
                            break;
                        case "timeout":
                            config.setTimeout(Integer.parseInt(value));
                            break;
                        case "retryCount":
                            config.setRetryCount(Integer.parseInt(value));
                            break;
                        default:
                            config.setProperty(propKey, value);
                    }
                }
            }
            
            log.info("Loaded {} configs from {}", configs.size(), configPath);
            
        } catch (IOException e) {
            log.error("Failed to load configs from: {}", configPath, e);
        }
    }
    
    @Override
    public void saveConfigs(String configPath) {
        try (OutputStream os = new FileOutputStream(configPath)) {
            Properties props = new Properties();
            
            for (Map.Entry<String, InterfaceConfig> entry : configs.entrySet()) {
                String interfaceId = entry.getKey();
                InterfaceConfig config = entry.getValue();
                
                if (config.getPreferredImplementation() != null) {
                    props.setProperty(interfaceId + ".preferredImplementation", config.getPreferredImplementation());
                }
                props.setProperty(interfaceId + ".enabled", String.valueOf(config.isEnabled()));
                props.setProperty(interfaceId + ".timeout", String.valueOf(config.getTimeout()));
                props.setProperty(interfaceId + ".retryCount", String.valueOf(config.getRetryCount()));
                
                for (Map.Entry<String, Object> prop : config.getProperties().entrySet()) {
                    props.setProperty(interfaceId + "." + prop.getKey(), String.valueOf(prop.getValue()));
                }
            }
            
            props.store(os, "Interface Configurations");
            log.info("Saved {} configs to {}", configs.size(), configPath);
            
        } catch (IOException e) {
            log.error("Failed to save configs to: {}", configPath, e);
        }
    }
    
    @Override
    public void addConfigListener(InterfaceConfigListener listener) {
        if (listener != null) listeners.add(listener);
    }
    
    @Override
    public void removeConfigListener(InterfaceConfigListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public void reset(String interfaceId) {
        InterfaceConfig config = new InterfaceConfig(interfaceId);
        configs.put(interfaceId, config);
        notifyConfigReset(interfaceId);
        log.info("Config reset for interface: {}", interfaceId);
    }
    
    @Override
    public void resetAll() {
        configs.clear();
        log.info("All configs reset");
    }
    
    private InterfaceConfig getOrCreate(String interfaceId) {
        return configs.computeIfAbsent(interfaceId, InterfaceConfig::new);
    }
    
    private void notifyConfigChanged(String interfaceId, String key, Object oldValue, Object newValue) {
        for (InterfaceConfigListener listener : listeners) {
            try { listener.onConfigChanged(interfaceId, key, oldValue, newValue); } 
            catch (Exception e) { log.warn("Listener error", e); }
        }
    }
    
    private void notifyConfigAdded(String interfaceId) {
        for (InterfaceConfigListener listener : listeners) {
            try { listener.onConfigAdded(interfaceId); } 
            catch (Exception e) { log.warn("Listener error", e); }
        }
    }
    
    private void notifyConfigReset(String interfaceId) {
        for (InterfaceConfigListener listener : listeners) {
            try { listener.onConfigReset(interfaceId); } 
            catch (Exception e) { log.warn("Listener error", e); }
        }
    }
}
