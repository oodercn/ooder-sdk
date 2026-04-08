package net.ooder.config.core;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class YamlConfigReader {
    
    private static final String[] CONFIG_FILE_NAMES = {
        "application.yml",
        "application.yaml",
        "ooder-config.yml",
        "ooder-config.yaml"
    };
    
    public OoderConfig read() {
        for (String fileName : CONFIG_FILE_NAMES) {
            URL resource = getClass().getClassLoader().getResource(fileName);
            if (resource != null) {
                return read(resource);
            }
        }
        return new OoderConfig();
    }
    
    public OoderConfig read(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return read(file);
        }
        return new OoderConfig();
    }
    
    public OoderConfig read(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return parseYaml(is);
        } catch (IOException e) {
            throw new ConfigException("Failed to read config file: " + file.getPath(), e);
        }
    }
    
    public OoderConfig read(URL url) {
        try (InputStream is = url.openStream()) {
            return parseYaml(is);
        } catch (IOException e) {
            throw new ConfigException("Failed to read config from URL: " + url, e);
        }
    }
    
    public OoderConfig read(InputStream inputStream) {
        try {
            return parseYaml(inputStream);
        } catch (Exception e) {
            throw new ConfigException("Failed to parse YAML config", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private OoderConfig parseYaml(InputStream is) throws IOException {
        Map<String, Object> yamlMap = loadYaml(is);
        return mapToConfig(yamlMap);
    }
    
    private Map<String, Object> loadYaml(InputStream is) throws IOException {
        try {
            Class<?> yamlClass = Class.forName("org.yaml.snakeyaml.Yaml");
            Object yaml = yamlClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method loadMethod = yamlClass.getMethod("load", InputStream.class);
            Object result = loadMethod.invoke(yaml, is);
            if (result instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result;
                return map;
            }
            return new HashMap<String, Object>();
        } catch (ClassNotFoundException e) {
            throw new ConfigException("SnakeYAML not found. Please add snakeyaml dependency.", e);
        } catch (Exception e) {
            throw new ConfigException("Failed to load YAML", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private OoderConfig mapToConfig(Map<String, Object> map) {
        OoderConfig config = new OoderConfig();
        
        Map<String, Object> ooderMap = getMap(map, "ooder");
        if (ooderMap.isEmpty()) {
            ooderMap = map;
        }
        
        Map<String, Object> jdsMap = getMap(ooderMap, "jds");
        if (!jdsMap.isEmpty()) {
            JdsConfig jds = config.getJds();
            jds.setHome(getString(jdsMap, "home"));
            jds.setConfigName(getString(jdsMap, "config-name"));
        }
        
        Map<String, Object> serverMap = getMap(ooderMap, "server");
        if (!serverMap.isEmpty()) {
            ServerConfig server = config.getServer();
            server.setPort(getInteger(serverMap, "port"));
            server.setUrl(getString(serverMap, "url"));
            
            Map<String, Object> adminMap = getMap(serverMap, "admin");
            if (!adminMap.isEmpty()) {
                ServerConfig.AdminConfig admin = server.getAdmin();
                admin.setEnabled(getBoolean(adminMap, "enabled", true));
                admin.setPort(getInteger(adminMap, "port"));
                admin.setKey(getString(adminMap, "key"));
                admin.setHost(getString(adminMap, "host"));
            }
        }
        
        Map<String, Object> clusterMap = getMap(ooderMap, "cluster");
        if (!clusterMap.isEmpty()) {
            ClusterConfig cluster = config.getCluster();
            cluster.setEnabled(getBoolean(clusterMap, "enabled", false));
            
            Map<String, Object> udpMap = getMap(clusterMap, "udp");
            if (!udpMap.isEmpty()) {
                ClusterConfig.UdpConfig udp = cluster.getUdp();
                udp.setEnabled(getBoolean(udpMap, "enabled", true));
                udp.setPort(getInteger(udpMap, "port"));
                udp.setCode(getString(udpMap, "code"));
                udp.setServerIP(getString(udpMap, "server-ip"));
                udp.setSelf(getBoolean(udpMap, "self", true));
            }
        }
        
        Map<String, Object> sessionMap = getMap(ooderMap, "session");
        if (!sessionMap.isEmpty()) {
            SessionConfig session = config.getSession();
            session.setEnabled(getBoolean(sessionMap, "enabled", true));
            session.setExpireTime(getLong(sessionMap, "expire-time", 30L));
            session.setCheckInterval(getLong(sessionMap, "check-interval", 5L));
            session.setSingleLogin(getBoolean(sessionMap, "single-login", true));
        }
        
        Map<String, Object> cacheMap = getMap(ooderMap, "cache");
        if (!cacheMap.isEmpty()) {
            CacheConfig cache = config.getCache();
            cache.setEnabled(getBoolean(cacheMap, "enabled", true));
            cache.setDumpCache(getBoolean(cacheMap, "dump-cache", true));
            cache.setDbUser(getString(cacheMap, "db-user"));
            cache.setDbPassword(getString(cacheMap, "db-password"));
            cache.setDbUrl(getString(cacheMap, "db-url"));
            cache.setDataPath(getString(cacheMap, "data-path"));
            cache.setDbName(getString(cacheMap, "db-name"));
        }
        
        Map<String, Object> userMap = getMap(ooderMap, "user");
        if (!userMap.isEmpty()) {
            UserConfig user = config.getUser();
            user.setServerUrl(getString(userMap, "server-url"));
            user.setSystemCode(getString(userMap, "system-code"));
            user.setConfigName(getString(userMap, "config-name"));
            user.setUsername(getString(userMap, "username"));
            user.setPassword(getString(userMap, "password"));
            user.setProxyHost(getString(userMap, "proxy-host"));
            user.setProxyPort(getInteger(userMap, "proxy-port"));
            user.setAutoLogin(getBoolean(userMap, "auto-login", false));
            user.setSavePassword(getBoolean(userMap, "save-password", false));
            user.setOffline(getBoolean(userMap, "offline", true));
            user.setMsgPort(getInteger(userMap, "msg-port"));
            user.setUdpUrl(getString(userMap, "udp-url"));
        }
        
        return config;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> parent, String key) {
        Object value = parent.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return new HashMap<String, Object>();
    }
    
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get(toCamelCase(key));
        }
        return value != null ? value.toString() : null;
    }
    
    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get(toCamelCase(key));
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private Long getLong(Map<String, Object> map, String key, long defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get(toCamelCase(key));
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    private boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            value = map.get(toCamelCase(key));
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }
    
    private String toCamelCase(String kebabCase) {
        StringBuilder result = new StringBuilder();
        String[] parts = kebabCase.split("-");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                result.append(parts[i]);
            } else {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    result.append(parts[i].substring(1));
                }
            }
        }
        return result.toString();
    }
}
