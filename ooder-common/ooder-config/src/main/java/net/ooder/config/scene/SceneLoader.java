package net.ooder.config.scene;

import net.ooder.config.scene.enums.SceneEnvironment;
import org.yaml.snakeyaml.Yaml;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneLoader {
    
    private static final String DEFAULT_SCENE_FILE = "scene.yaml";
    private static final String SCENE_FILE_PATTERN = "scene-%s.yaml";
    
    public static SceneConfig load() {
        SceneEnvironment env = SceneEnvironment.fromSystemProperty();
        return loadByEnv(env);
    }
    
    public static SceneConfig load(String configFile) {
        try {
            Resource resource = new ClassPathResource(configFile);
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    return loadFromStream(is);
                }
            }
            
            File file = new File(configFile);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    return loadFromStream(fis);
                }
            }
            
            throw new SceneConfigException("Scene config file not found: " + configFile);
        } catch (SceneConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new SceneConfigException("Failed to load scene config: " + e.getMessage(), e);
        }
    }
    
    public static SceneConfig loadByEnv(SceneEnvironment env) {
        String configFile = env.getConfigFileName();
        return load(configFile);
    }
    
    public static SceneConfig loadByEnv(String env) {
        SceneEnvironment environment = SceneEnvironment.fromCode(env);
        return loadByEnv(environment);
    }
    
    public static SceneConfig loadFromYaml(String yamlContent) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(yamlContent);
        return parseSceneConfig(data);
    }
    
    public static SceneConfig loadFromStream(InputStream is) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(is);
        return parseSceneConfig(data);
    }
    
    @SuppressWarnings("unchecked")
    private static SceneConfig parseSceneConfig(Map<String, Object> data) {
        Map<String, Object> sceneData = (Map<String, Object>) data.get("scene");
        if (sceneData == null) {
            throw new SceneConfigException("Invalid scene config: missing 'scene' root");
        }
        
        SceneConfig.SceneConfigBuilder builder = SceneConfig.builder()
            .id(resolveString(sceneData.get("id")))
            .name(resolveString(sceneData.get("name")))
            .description(resolveString(sceneData.get("description")));
        
        if (sceneData.containsKey("org")) {
            builder.org(parseOrgConfig((Map<String, Object>) sceneData.get("org")));
        }
        
        if (sceneData.containsKey("vfs")) {
            builder.vfs(parseVfsConfig((Map<String, Object>) sceneData.get("vfs")));
        }
        
        if (sceneData.containsKey("msg")) {
            builder.msg(parseMsgConfig((Map<String, Object>) sceneData.get("msg")));
        }
        
        if (sceneData.containsKey("jds")) {
            builder.jds(parseJdsConfig((Map<String, Object>) sceneData.get("jds")));
        }
        
        return builder.build();
    }
    
    @SuppressWarnings("unchecked")
    private static OrgSceneConfig parseOrgConfig(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        
        OrgSceneConfig config = new OrgSceneConfig();
        config.setSceneId(resolveString(data.get("sceneId")));
        config.setConfigName(resolveString(data.get("configName")));
        config.setDbDriver(resolveString(data.get("dbDriver")));
        config.setDbUrl(resolveString(data.get("dbUrl")));
        config.setDbUser(resolveString(data.get("dbUser")));
        config.setDbPassword(resolveString(data.get("dbPassword")));
        
        if (data.containsKey("cacheEnabled")) {
            config.setCacheEnabled(Boolean.parseBoolean(data.get("cacheEnabled").toString()));
        }
        if (data.containsKey("cacheExpireTime")) {
            config.setCacheExpireTime(Long.parseLong(data.get("cacheExpireTime").toString()));
        }
        if (data.containsKey("cacheSize")) {
            config.setCacheSize(Integer.parseInt(data.get("cacheSize").toString()));
        }
        if (data.containsKey("connectTimeout")) {
            config.setConnectTimeout(Integer.parseInt(data.get("connectTimeout").toString()));
        }
        if (data.containsKey("readTimeout")) {
            config.setReadTimeout(Integer.parseInt(data.get("readTimeout").toString()));
        }
        
        if (data.containsKey("capabilities")) {
            config.setCapabilities(parseCapabilities((Map<String, Object>) data.get("capabilities")));
        }
        
        return config;
    }
    
    @SuppressWarnings("unchecked")
    private static JdsSceneConfig parseJdsConfig(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        
        JdsSceneConfig config = new JdsSceneConfig();
        config.setSceneId(resolveString(data.get("sceneId")));
        config.setConfigName(resolveString(data.get("configName")));
        
        if (data.containsKey("adminPort")) {
            config.setAdminPort(Integer.parseInt(data.get("adminPort").toString()));
        }
        config.setAdminKey(resolveString(data.get("adminKey")));
        
        if (data.containsKey("singleLogin")) {
            config.setSingleLogin(Boolean.parseBoolean(data.get("singleLogin").toString()));
        }
        if (data.containsKey("sessionEnabled")) {
            config.setSessionEnabled(Boolean.parseBoolean(data.get("sessionEnabled").toString()));
        }
        if (data.containsKey("sessionExpireTime")) {
            config.setSessionExpireTime(Long.parseLong(data.get("sessionExpireTime").toString()));
        }
        if (data.containsKey("sessionCheckInterval")) {
            config.setSessionCheckInterval(Long.parseLong(data.get("sessionCheckInterval").toString()));
        }
        if (data.containsKey("cacheMaxSize")) {
            config.setCacheMaxSize(Integer.parseInt(data.get("cacheMaxSize").toString()));
        }
        if (data.containsKey("cacheExpireTime")) {
            config.setCacheExpireTime(Long.parseLong(data.get("cacheExpireTime").toString()));
        }
        if (data.containsKey("clusterEnabled")) {
            config.setClusterEnabled(Boolean.parseBoolean(data.get("clusterEnabled").toString()));
        }
        if (data.containsKey("udpPort")) {
            config.setUdpPort(Integer.parseInt(data.get("udpPort").toString()));
        }
        
        if (data.containsKey("capabilities")) {
            config.setCapabilities(parseCapabilities((Map<String, Object>) data.get("capabilities")));
        }
        
        return config;
    }
    
    @SuppressWarnings("unchecked")
    private static VfsSceneConfig parseVfsConfig(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        
        VfsSceneConfig config = new VfsSceneConfig();
        config.setSceneId(resolveString(data.get("sceneId")));
        config.setConfigName(resolveString(data.get("configName")));
        config.setStorageType(resolveString(data.get("storageType")));
        config.setBasePath(resolveString(data.get("basePath")));
        
        if (data.containsKey("maxFileSize")) {
            config.setMaxFileSize(Long.parseLong(data.get("maxFileSize").toString()));
        }
        config.setAllowedTypes(resolveString(data.get("allowedTypes")));
        
        config.setOssEndpoint(resolveString(data.get("ossEndpoint")));
        config.setOssBucket(resolveString(data.get("ossBucket")));
        config.setOssAccessKey(resolveString(data.get("ossAccessKey")));
        config.setOssSecretKey(resolveString(data.get("ossSecretKey")));
        
        config.setLocalCachePath(resolveString(data.get("localCachePath")));
        if (data.containsKey("bigFileSize")) {
            config.setBigFileSize(Integer.parseInt(data.get("bigFileSize").toString()));
        }
        
        if (data.containsKey("cacheEnabled")) {
            config.setCacheEnabled(Boolean.parseBoolean(data.get("cacheEnabled").toString()));
        }
        if (data.containsKey("connectTimeout")) {
            config.setConnectTimeout(Integer.parseInt(data.get("connectTimeout").toString()));
        }
        if (data.containsKey("readTimeout")) {
            config.setReadTimeout(Integer.parseInt(data.get("readTimeout").toString()));
        }
        
        if (data.containsKey("capabilities")) {
            config.setCapabilities(parseCapabilities((Map<String, Object>) data.get("capabilities")));
        }
        
        return config;
    }
    
    @SuppressWarnings("unchecked")
    private static MsgSceneConfig parseMsgConfig(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        
        MsgSceneConfig config = new MsgSceneConfig();
        config.setSceneId(resolveString(data.get("sceneId")));
        config.setConfigName(resolveString(data.get("configName")));
        config.setPersonId(resolveString(data.get("personId")));
        config.setMsgClass(resolveString(data.get("msgClass")));
        
        config.setMqttBroker(resolveString(data.get("mqttBroker")));
        if (data.containsKey("mqttPort")) {
            config.setMqttPort(Integer.parseInt(data.get("mqttPort").toString()));
        }
        config.setMqttClientId(resolveString(data.get("mqttClientId")));
        config.setMqttUsername(resolveString(data.get("mqttUsername")));
        config.setMqttPassword(resolveString(data.get("mqttPassword")));
        
        if (data.containsKey("retainMessages")) {
            config.setRetainMessages(Boolean.parseBoolean(data.get("retainMessages").toString()));
        }
        if (data.containsKey("qos")) {
            config.setQos(Integer.parseInt(data.get("qos").toString()));
        }
        
        if (data.containsKey("cacheEnabled")) {
            config.setCacheEnabled(Boolean.parseBoolean(data.get("cacheEnabled").toString()));
        }
        if (data.containsKey("connectTimeout")) {
            config.setConnectTimeout(Integer.parseInt(data.get("connectTimeout").toString()));
        }
        if (data.containsKey("readTimeout")) {
            config.setReadTimeout(Integer.parseInt(data.get("readTimeout").toString()));
        }
        
        if (data.containsKey("capabilities")) {
            config.setCapabilities(parseCapabilities((Map<String, Object>) data.get("capabilities")));
        }
        
        return config;
    }
    
    @SuppressWarnings("unchecked")
    private static Map<String, CapabilityConfig> parseCapabilities(Map<String, Object> data) {
        Map<String, CapabilityConfig> capabilities = new HashMap<String, CapabilityConfig>();
        if (data == null) {
            return capabilities;
        }
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                Map<String, Object> capData = (Map<String, Object>) value;
                CapabilityConfig cap = parseCapabilityConfig(capData);
                capabilities.put(key, cap);
            }
        }
        
        return capabilities;
    }
    
    private static CapabilityConfig parseCapabilityConfig(Map<String, Object> data) {
        CapabilityConfig cap = new CapabilityConfig();
        cap.setCapabilityName(resolveString(data.get("capabilityName")));
        cap.setInterfaceId(resolveString(data.get("interfaceId")));
        cap.setEndpoint(resolveString(data.get("endpoint")));
        cap.setSystemCode(resolveString(data.get("systemCode")));
        
        if (data.containsKey("timeout")) {
            cap.setTimeout(Integer.parseInt(data.get("timeout").toString()));
        }
        if (data.containsKey("retryCount")) {
            cap.setRetryCount(Integer.parseInt(data.get("retryCount").toString()));
        }
        if (data.containsKey("enabled")) {
            cap.setEnabled(Boolean.parseBoolean(data.get("enabled").toString()));
        }
        
        return cap;
    }
    
    private static String resolveString(Object value) {
        if (value == null) {
            return null;
        }
        
        String str = value.toString();
        return resolveEnvVars(str);
    }
    
    private static String resolveEnvVars(String value) {
        if (value == null) {
            return null;
        }
        
        if (value.startsWith("${") && value.endsWith("}")) {
            String envKey = value.substring(2, value.length() - 1);
            String envValue = System.getenv(envKey);
            if (envValue == null) {
                envValue = System.getProperty(envKey);
            }
            return envValue != null ? envValue : value;
        }
        return value;
    }
}
