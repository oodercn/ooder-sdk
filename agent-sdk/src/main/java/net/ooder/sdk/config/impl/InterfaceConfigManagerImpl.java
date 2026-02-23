package net.ooder.sdk.config.impl;

import net.ooder.sdk.config.InterfaceConfigManager;
import net.ooder.sdk.config.InterfaceConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InterfaceConfigManagerImpl implements InterfaceConfigManager {
    
    private static final Logger log = LoggerFactory.getLogger(InterfaceConfigManagerImpl.class);
    
    private final Map<String, InterfaceConfig> configs = new ConcurrentHashMap<>();
    private final List<InterfaceConfigListener> listeners = new CopyOnWriteArrayList<>();
    
    @Override
    public void registerConfig(String interfaceId, InterfaceConfig config) {
        if (interfaceId == null || config == null) {
            throw new IllegalArgumentException("Interface ID and config cannot be null");
        }
        config.setInterfaceId(interfaceId);
        config.setLastUpdateTime(System.currentTimeMillis());
        configs.put(interfaceId, config);
        notifyConfigAdded(interfaceId);
        log.info("Config registered for interface: {}", interfaceId);
    }
    
    @Override
    public void unregisterConfig(String interfaceId) {
        if (interfaceId == null) return;
        InterfaceConfig removed = configs.remove(interfaceId);
        if (removed != null) {
            notifyConfigRemoved(interfaceId);
            log.info("Config unregistered for interface: {}", interfaceId);
        }
    }
    
    @Override
    public Optional<InterfaceConfig> getConfig(String interfaceId) {
        if (interfaceId == null) return Optional.empty();
        return Optional.ofNullable(configs.get(interfaceId));
    }
    
    @Override
    public Map<String, InterfaceConfig> getAllConfigs() {
        return new ConcurrentHashMap<>(configs);
    }
    
    @Override
    public void updateConfig(String interfaceId, Map<String, Object> updates) {
        InterfaceConfig config = configs.computeIfAbsent(interfaceId, InterfaceConfig::new);
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            Object oldValue = config.getProperty(entry.getKey());
            config.setProperty(entry.getKey(), entry.getValue());
            notifyConfigChanged(interfaceId, entry.getKey(), oldValue, entry.getValue());
        }
        log.debug("Config updated for interface: {}", interfaceId);
    }
    
    @Override
    public void setProperty(String interfaceId, String key, Object value) {
        InterfaceConfig config = configs.computeIfAbsent(interfaceId, InterfaceConfig::new);
        Object oldValue = config.getProperty(key);
        config.setProperty(key, value);
        notifyConfigChanged(interfaceId, key, oldValue, value);
    }
    
    @Override
    public Optional<Object> getProperty(String interfaceId, String key) {
        InterfaceConfig config = configs.get(interfaceId);
        if (config == null) return Optional.empty();
        return Optional.ofNullable(config.getProperty(key));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getProperty(String interfaceId, String key, Class<T> type) {
        InterfaceConfig config = configs.get(interfaceId);
        if (config == null) return Optional.empty();
        Object value = config.getProperty(key);
        if (value == null) return Optional.empty();
        if (type.isInstance(value)) {
            return Optional.of((T) value);
        }
        try {
            if (type == String.class) {
                return Optional.of((T) value.toString());
            } else if (type == Integer.class || type == int.class) {
                return Optional.of((T) Integer.valueOf(value.toString()));
            } else if (type == Long.class || type == long.class) {
                return Optional.of((T) Long.valueOf(value.toString()));
            } else if (type == Boolean.class || type == boolean.class) {
                return Optional.of((T) Boolean.valueOf(value.toString()));
            }
        } catch (Exception e) {
            log.debug("Failed to convert property {} to type {}", key, type.getName());
        }
        return Optional.empty();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String interfaceId, String key, T defaultValue) {
        Optional<T> value = getProperty(interfaceId, key, (Class<T>) defaultValue.getClass());
        return value.orElse(defaultValue);
    }
    
    @Override
    public void removeProperty(String interfaceId, String key) {
        InterfaceConfig config = configs.get(interfaceId);
        if (config != null) {
            Object oldValue = config.getProperties().remove(key);
            if (oldValue != null) {
                notifyConfigChanged(interfaceId, key, oldValue, null);
            }
        }
    }
    
    @Override
    public boolean hasConfig(String interfaceId) {
        return interfaceId != null && configs.containsKey(interfaceId);
    }
    
    @Override
    public List<String> getConfiguredInterfaces() {
        return new ArrayList<>(configs.keySet());
    }
    
    @Override
    public void loadConfigs(String configPath) {
        File file = new File(configPath);
        if (!file.exists()) {
            log.info("Config file not found: {}", configPath);
            return;
        }
        try (InputStream is = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(is);
            for (String key : props.stringPropertyNames()) {
                if (key.contains(".")) {
                    int dotIndex = key.indexOf('.');
                    String interfaceId = key.substring(0, dotIndex);
                    String propKey = key.substring(dotIndex + 1);
                    String value = props.getProperty(key);
                    setProperty(interfaceId, propKey, value);
                }
            }
            log.info("Loaded {} configs from {}", configs.size(), configPath);
        } catch (IOException e) {
            log.error("Failed to load configs from: {}", configPath, e);
        }
    }
    
    @Override
    public void saveConfigs(String configPath) {
        File file = new File(configPath);
        file.getParentFile().mkdirs();
        try (OutputStream os = new FileOutputStream(file)) {
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
    
    private void notifyConfigRemoved(String interfaceId) {
        for (InterfaceConfigListener listener : listeners) {
            try { listener.onConfigRemoved(interfaceId); }
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
