package net.ooder.scene.core.provider;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.config.ConfigEvent;
import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.*;
import net.ooder.scene.provider.model.config.AdvancedConfig;
import net.ooder.scene.provider.model.config.BasicConfig;
import net.ooder.scene.provider.model.config.NetworkConfig;
import net.ooder.scene.provider.model.config.SecurityConfig;
import net.ooder.scene.provider.model.config.ServiceConfig;
import net.ooder.scene.provider.model.config.SystemConfig;
import net.ooder.scene.provider.model.config.TerminalConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ConfigProvider核心实现
 *
 * <p>SEC Engine内置实现，提供配置读写、导入导出、版本管理等功能</p>
 */
public class ConfigProviderImpl implements ConfigProvider {

    private static final String PROVIDER_NAME = "config-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    private SceneEventPublisher eventPublisher;
    
    private final Map<String, String> configs = new ConcurrentHashMap<>();
    private final Map<String, List<ConfigHistory>> configHistory = new ConcurrentHashMap<>();
    private final AtomicLong historyIdGenerator = new AtomicLong(0);
    
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        initDefaultConfigs();
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    private void initDefaultConfigs() {
        configs.put("engine.name", "SEC Engine");
        configs.put("engine.version", "0.8.0");
        configs.put("engine.environment", "development");
        configs.put("engine.logLevel", "INFO");
        configs.put("engine.maxThreads", "100");
        configs.put("engine.timeout", "30000");
        configs.put("security.enabled", "true");
        configs.put("security.authRequired", "true");
        configs.put("security.sessionTimeout", "1800000");
        configs.put("network.scanInterval", "300000");
        configs.put("network.timeout", "60000");
        configs.put("health.checkInterval", "60000");
    }

    @Override
    public Result<String> getConfig(String key) {
        if (key == null || key.isEmpty()) {
            return Result.badRequest("Config key cannot be null or empty");
        }
        String value = configs.get(key);
        return Result.success(value);
    }

    @Override
    public Result<String> getConfig(String key, String defaultValue) {
        Result<String> result = getConfig(key);
        if (result.isSuccess() && result.getData() == null) {
            return Result.success(defaultValue);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Result<T> getConfig(String key, Class<T> type) {
        Result<String> result = getConfig(key);
        if (!result.isSuccess()) {
            return Result.error(result.getError());
        }
        
        String value = result.getData();
        if (value == null) {
            return Result.success(null);
        }
        
        try {
            if (type == String.class) {
                return (Result<T>) result;
            } else if (type == Integer.class || type == int.class) {
                return (Result<T>) Result.success(Integer.parseInt(value));
            } else if (type == Long.class || type == long.class) {
                return (Result<T>) Result.success(Long.parseLong(value));
            } else if (type == Boolean.class || type == boolean.class) {
                return (Result<T>) Result.success(Boolean.parseBoolean(value));
            } else if (type == Double.class || type == double.class) {
                return (Result<T>) Result.success(Double.parseDouble(value));
            } else {
                return Result.error("Unsupported type: " + type.getName());
            }
        } catch (NumberFormatException e) {
            return Result.error("Failed to parse config value: " + e.getMessage());
        }
    }

    @Override
    public <T> Result<T> getConfig(String key, Class<T> type, T defaultValue) {
        Result<T> result = getConfig(key, type);
        if (result.isSuccess() && result.getData() == null) {
            return Result.success(defaultValue);
        }
        return result;
    }

    @Override
    public Result<Boolean> setConfig(String key, String value) {
        if (key == null || key.isEmpty()) {
            return Result.badRequest("Config key cannot be null or empty");
        }
        
        String oldValue = configs.put(key, value);
        
        addConfigHistory(key, oldValue, value, "system", "Config updated");
        
        publishConfigEvent(ConfigEvent.set(this, key, extractGroup(key), oldValue, value, "system"));
        
        return Result.success(true);
    }

    @Override
    public Result<Boolean> setConfig(String key, Object value) {
        if (value == null) {
            return setConfig(key, (String) null);
        }
        return setConfig(key, String.valueOf(value));
    }

    @Override
    public Result<Boolean> setConfigs(Map<String, String> configs) {
        if (configs == null) {
            return Result.badRequest("Configs cannot be null");
        }
        
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            Result<Boolean> result = setConfig(entry.getKey(), entry.getValue());
            if (!result.isSuccess()) {
                return result;
            }
        }
        
        publishConfigEvent(ConfigEvent.batchSet(this, "batch", configs.size(), "system"));
        
        return Result.success(true);
    }

    @Override
    public Result<Boolean> deleteConfig(String key) {
        if (key == null || key.isEmpty()) {
            return Result.badRequest("Config key cannot be null or empty");
        }
        
        String oldValue = configs.remove(key);
        if (oldValue != null) {
            addConfigHistory(key, oldValue, null, "system", "Config deleted");
            publishConfigEvent(ConfigEvent.deleted(this, key, extractGroup(key), "system"));
            return Result.success(true);
        }
        
        return Result.success(false);
    }

    @Override
    public Result<Boolean> hasConfig(String key) {
        return Result.success(configs.containsKey(key));
    }

    @Override
    public Result<Map<String, String>> getAllConfigs() {
        return Result.success(new HashMap<>(configs));
    }

    @Override
    public Result<Map<String, String>> getConfigsByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return getAllConfigs();
        }
        
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        
        return Result.success(result);
    }

    @Override
    public Result<PageResult<ConfigHistory>> getConfigHistory(String key, int page, int size) {
        List<ConfigHistory> history = configHistory.getOrDefault(key, new ArrayList<>());
        
        int total = history.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<ConfigHistory> pagedHistory = start < total ? 
            new ArrayList<>(history.subList(start, end)) : new ArrayList<>();
        
        PageResult<ConfigHistory> result = new PageResult<>();
        result.setItems(pagedHistory);
        result.setTotal(total);
        result.setPageNum(page);
        result.setPageSize(size);
        
        return Result.success(result);
    }

    @Override
    public Result<Boolean> rollbackConfig(String key, String version) {
        List<ConfigHistory> history = configHistory.get(key);
        if (history == null || history.isEmpty()) {
            return Result.notFound("No history found for config: " + key);
        }
        
        for (ConfigHistory h : history) {
            if (version.equals(h.getHistoryId())) {
                String currentValue = configs.get(key);
                configs.put(key, h.getOldValue());
                addConfigHistory(key, currentValue, h.getOldValue(), "system", "Rollback to version: " + version);
                return Result.success(true);
            }
        }
        
        return Result.notFound("Version not found: " + version);
    }

    @Override
    public Result<ConfigExportResult> exportConfig(String format) {
        try {
            String content;
            String fileName;
            
            if ("yaml".equalsIgnoreCase(format)) {
                content = exportAsYaml();
                fileName = "config_" + System.currentTimeMillis() + ".yaml";
            } else if ("properties".equalsIgnoreCase(format)) {
                content = exportAsProperties();
                fileName = "config_" + System.currentTimeMillis() + ".properties";
            } else {
                content = exportAsJson();
                fileName = "config_" + System.currentTimeMillis() + ".json";
            }
            
            ConfigExportResult result = new ConfigExportResult();
            result.setExportId("export-" + System.currentTimeMillis());
            result.setFileName(fileName);
            result.setFormat(format);
            result.setConfigCount(configs.size());
            result.setFileSize(content.length());
            result.setContent(content);
            result.setTimestamp(System.currentTimeMillis());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Failed to export config: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> importConfig(String content, String format) {
        try {
            Map<String, String> importedConfigs;
            
            if ("yaml".equalsIgnoreCase(format)) {
                importedConfigs = importFromYaml(content);
            } else if ("properties".equalsIgnoreCase(format)) {
                importedConfigs = importFromProperties(content);
            } else {
                importedConfigs = importFromJson(content);
            }
            
            int count = 0;
            for (Map.Entry<String, String> entry : importedConfigs.entrySet()) {
                setConfig(entry.getKey(), entry.getValue());
                count++;
            }
            
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("Failed to import config: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> refreshConfig() {
        return Result.success(true);
    }

    @Override
    public Result<List<ConfigGroup>> getConfigGroups() {
        Map<String, List<ConfigItem>> groups = new HashMap<>();
        
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            String key = entry.getKey();
            int dotIndex = key.indexOf('.');
            String groupName = dotIndex > 0 ? key.substring(0, dotIndex) : "default";
            
            ConfigItem item = new ConfigItem();
            item.setKey(key);
            item.setValue(entry.getValue());
            item.setLastUpdated(System.currentTimeMillis());
            
            groups.computeIfAbsent(groupName, k -> new ArrayList<>()).add(item);
        }
        
        List<ConfigGroup> result = new ArrayList<>();
        for (Map.Entry<String, List<ConfigItem>> entry : groups.entrySet()) {
            ConfigGroup group = new ConfigGroup();
            group.setGroupName(entry.getKey());
            group.setConfigCount(entry.getValue().size());
            group.setItems(entry.getValue());
            group.setLastUpdated(System.currentTimeMillis());
            result.add(group);
        }
        
        return Result.success(result);
    }

    @Override
    public Result<ConfigGroup> getConfigGroup(String groupName) {
        List<ConfigItem> items = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            if (entry.getKey().startsWith(groupName + ".")) {
                ConfigItem item = new ConfigItem();
                item.setKey(entry.getKey());
                item.setValue(entry.getValue());
                item.setLastUpdated(System.currentTimeMillis());
                items.add(item);
            }
        }
        
        if (items.isEmpty()) {
            return Result.notFound("Config group not found: " + groupName);
        }
        
        ConfigGroup group = new ConfigGroup();
        group.setGroupName(groupName);
        group.setConfigCount(items.size());
        group.setItems(items);
        group.setLastUpdated(System.currentTimeMillis());
        
        return Result.success(group);
    }

    private void addConfigHistory(String key, String oldValue, String newValue, String changedBy, String reason) {
        ConfigHistory history = new ConfigHistory();
        history.setHistoryId("hist-" + historyIdGenerator.incrementAndGet());
        history.setKey(key);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedBy(changedBy);
        history.setChangedAt(System.currentTimeMillis());
        history.setReason(reason);
        
        configHistory.computeIfAbsent(key, k -> new ArrayList<>()).add(0, history);
    }

    private String exportAsJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        int i = 0;
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            if (i++ > 0) sb.append(",\n");
            sb.append("  \"").append(entry.getKey()).append("\": \"")
              .append(escapeJson(entry.getValue())).append("\"");
        }
        sb.append("\n}");
        return sb.toString();
    }

    private String exportAsYaml() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            sb.append(entry.getKey()).append(": \"")
              .append(escapeYaml(entry.getValue())).append("\"\n");
        }
        return sb.toString();
    }

    private String exportAsProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append("# SEC Engine Configuration\n");
        sb.append("# Generated: ").append(new Date()).append("\n\n");
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    private Map<String, String> importFromJson(String content) {
        Map<String, String> result = new HashMap<>();
        content = content.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            content = content.substring(1, content.length() - 1);
            String[] pairs = content.split(",\\s*");
            for (String pair : pairs) {
                String[] kv = pair.split(":\\s*", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().replace("\"", "");
                    String value = kv[1].trim().replace("\"", "");
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    private Map<String, String> importFromYaml(String content) {
        Map<String, String> result = new HashMap<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                String[] kv = line.split(":\\s*", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim();
                    String value = kv[1].trim().replace("\"", "");
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    private Map<String, String> importFromProperties(String content) {
        Map<String, String> result = new HashMap<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                String[] kv = line.split("=", 2);
                if (kv.length == 2) {
                    result.put(kv[0].trim(), kv[1].trim());
                }
            }
        }
        return result;
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String escapeYaml(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n");
    }

    @Override
    public Result<BasicConfig> getBasicConfig() {
        BasicConfig config = new BasicConfig();
        config.setHostname(getConfigValue("basic.hostname", "scene-engine"));
        config.setTimezone(getConfigValue("basic.timezone", "UTC"));
        config.setLanguage(getConfigValue("basic.language", "en"));
        config.setLogLevel(getConfigValue("basic.logLevel", "INFO"));
        return Result.success(config);
    }

    @Override
    public Result<AdvancedConfig> getAdvancedConfig() {
        AdvancedConfig config = new AdvancedConfig();
        config.setMaxThreads(getConfigIntValue("advanced.maxThreads", 100));
        config.setConnectionTimeout(getConfigIntValue("advanced.connectionTimeout", 30000));
        config.setReadTimeout(getConfigIntValue("advanced.readTimeout", 60000));
        config.setMaxConnections(getConfigIntValue("advanced.maxConnections", 1000));
        config.setDebugMode(getConfigBoolValue("advanced.debugMode", false));
        return Result.success(config);
    }

    @Override
    public Result<SecurityConfig> getSecurityConfig() {
        SecurityConfig config = new SecurityConfig();
        config.setEnabled(getConfigBoolValue("security.enabled", true));
        config.setAuthRequired(getConfigBoolValue("security.authRequired", true));
        config.setSessionTimeout(getConfigIntValue("security.sessionTimeout", 1800000));
        config.setMaxLoginAttempts(getConfigIntValue("security.maxLoginAttempts", 5));
        config.setPasswordPolicy(getConfigValue("security.passwordPolicy", "medium"));
        config.setTwoFactorEnabled(getConfigBoolValue("security.twoFactorEnabled", false));
        return Result.success(config);
    }

    @Override
    public Result<TerminalConfig> getTerminalConfig() {
        TerminalConfig config = new TerminalConfig();
        config.setMaxSessions(getConfigIntValue("terminal.maxSessions", 10));
        config.setSessionTimeout(getConfigIntValue("terminal.sessionTimeout", 3600000));
        config.setDefaultShell(getConfigValue("terminal.defaultShell", "/bin/bash"));
        config.setScrollbackLines(getConfigIntValue("terminal.scrollbackLines", 10000));
        config.setEnableColors(getConfigBoolValue("terminal.enableColors", true));
        return Result.success(config);
    }

    @Override
    public Result<ServiceConfig> getServiceConfig() {
        ServiceConfig config = new ServiceConfig();
        config.setAutoStart(getConfigBoolValue("service.autoStart", true));
        config.setHealthCheckInterval(getConfigIntValue("service.healthCheckInterval", 60000));
        config.setRestartDelay(getConfigIntValue("service.restartDelay", 5000));
        config.setMaxRestarts(getConfigIntValue("service.maxRestarts", 3));
        config.setLogPath(getConfigValue("service.logPath", "/var/log/scene-engine"));
        return Result.success(config);
    }

    @Override
    public Result<SystemConfig> getSystemConfig() {
        SystemConfig config = new SystemConfig();
        config.setEnvironment(getConfigValue("system.environment", "development"));
        config.setDataPath(getConfigValue("system.dataPath", "/var/lib/scene-engine"));
        config.setTempPath(getConfigValue("system.tempPath", "/tmp/scene-engine"));
        config.setMaxMemoryMB(getConfigLongValue("system.maxMemoryMB", 1024));
        config.setCpuCores(getConfigIntValue("system.cpuCores", Runtime.getRuntime().availableProcessors()));
        return Result.success(config);
    }

    @Override
    public Result<NetworkConfig> getNetworkConfig() {
        NetworkConfig config = new NetworkConfig();
        config.setHostname(getConfigValue("network.hostname", "scene-engine"));
        config.setGateway(getConfigValue("network.gateway", "192.168.1.1"));
        config.setSubnet(getConfigValue("network.subnet", "255.255.255.0"));
        config.setDhcpEnabled(getConfigBoolValue("network.dhcpEnabled", true));
        config.setIpAddress(getConfigValue("network.ipAddress", ""));
        
        String dnsStr = getConfigValue("network.dnsServers", "8.8.8.8,8.8.4.4");
        List<String> dnsServers = Arrays.asList(dnsStr.split(","));
        config.setDnsServers(dnsServers);
        
        return Result.success(config);
    }

    @Override
    public Result<Boolean> saveBasicConfig(BasicConfig config) {
        if (config == null) {
            return Result.badRequest("Config cannot be null");
        }
        setConfig("basic.hostname", config.getHostname());
        setConfig("basic.timezone", config.getTimezone());
        setConfig("basic.language", config.getLanguage());
        setConfig("basic.logLevel", config.getLogLevel());
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveAdvancedConfig(AdvancedConfig config) {
        if (config == null) {
            return Result.badRequest("Config cannot be null");
        }
        setConfig("advanced.maxThreads", config.getMaxThreads());
        setConfig("advanced.connectionTimeout", config.getConnectionTimeout());
        setConfig("advanced.readTimeout", config.getReadTimeout());
        setConfig("advanced.maxConnections", config.getMaxConnections());
        setConfig("advanced.debugMode", config.isDebugMode());
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveSecurityConfig(SecurityConfig config) {
        if (config == null) {
            return Result.badRequest("Config cannot be null");
        }
        setConfig("security.enabled", config.isEnabled());
        setConfig("security.authRequired", config.isAuthRequired());
        setConfig("security.sessionTimeout", config.getSessionTimeout());
        setConfig("security.maxLoginAttempts", config.getMaxLoginAttempts());
        setConfig("security.passwordPolicy", config.getPasswordPolicy());
        setConfig("security.twoFactorEnabled", config.isTwoFactorEnabled());
        
        publishConfigEvent(ConfigEvent.securityConfigChanged(this, "system"));
        
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveTerminalConfig(TerminalConfig config) {
        if (config == null) {
            return Result.badRequest("Config cannot be null");
        }
        setConfig("terminal.maxSessions", config.getMaxSessions());
        setConfig("terminal.sessionTimeout", config.getSessionTimeout());
        setConfig("terminal.defaultShell", config.getDefaultShell());
        setConfig("terminal.scrollbackLines", config.getScrollbackLines());
        setConfig("terminal.enableColors", config.isEnableColors());
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveServiceConfig(ServiceConfig config) {
        if (config == null) {
            return Result.badRequest("Config cannot be null");
        }
        setConfig("service.autoStart", config.isAutoStart());
        setConfig("service.healthCheckInterval", config.getHealthCheckInterval());
        setConfig("service.restartDelay", config.getRestartDelay());
        setConfig("service.maxRestarts", config.getMaxRestarts());
        setConfig("service.logPath", config.getLogPath());
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveSystemConfig(SystemConfig config) {
        if (config == null) {
            return Result.badRequest("Config cannot be null");
        }
        setConfig("system.environment", config.getEnvironment());
        setConfig("system.dataPath", config.getDataPath());
        setConfig("system.tempPath", config.getTempPath());
        setConfig("system.maxMemoryMB", config.getMaxMemoryMB());
        setConfig("system.cpuCores", config.getCpuCores());
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveNetworkConfig(NetworkConfig config) {
        if (config == null) {
            return Result.badRequest("Config cannot be null");
        }
        setConfig("network.hostname", config.getHostname());
        setConfig("network.gateway", config.getGateway());
        setConfig("network.subnet", config.getSubnet());
        setConfig("network.dhcpEnabled", config.isDhcpEnabled());
        setConfig("network.ipAddress", config.getIpAddress());
        
        if (config.getDnsServers() != null) {
            setConfig("network.dnsServers", String.join(",", config.getDnsServers()));
        }
        
        return Result.success(true);
    }

    private String getConfigValue(String key, String defaultValue) {
        String value = configs.get(key);
        return value != null ? value : defaultValue;
    }

    private int getConfigIntValue(String key, int defaultValue) {
        String value = configs.get(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private long getConfigLongValue(String key, long defaultValue) {
        String value = configs.get(key);
        if (value == null) return defaultValue;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean getConfigBoolValue(String key, boolean defaultValue) {
        String value = configs.get(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }
    
    private String extractGroup(String key) {
        if (key == null) return null;
        int dotIndex = key.indexOf('.');
        return dotIndex > 0 ? key.substring(0, dotIndex) : "default";
    }
    
    private void publishConfigEvent(ConfigEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }
}
