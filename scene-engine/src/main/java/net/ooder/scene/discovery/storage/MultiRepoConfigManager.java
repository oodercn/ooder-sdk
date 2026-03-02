package net.ooder.scene.discovery.storage;

import net.ooder.engine.ConnectInfo;
import net.ooder.sdk.service.storage.persistence.StorageManager;
import net.ooder.sdk.service.storage.vfs.VfsManager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多仓库配置管理器
 * 
 * 支持单仓库/多仓库模式，管理多个GitHub/Gitee地址配置
 * 支持用户级别的ConnectInfo存储
 * 
 * @author ooder Team
 * @since 2.3
 */
public class MultiRepoConfigManager {

    private final VfsManager vfsManager;
    private final StorageManager storageManager;
    private final Map<String, List<RepositoryConfig>> userRepoConfigs;
    private final Map<String, ConnectInfo> userConnectInfos;

    public MultiRepoConfigManager(VfsManager vfsManager, StorageManager storageManager) {
        this.vfsManager = vfsManager;
        this.storageManager = storageManager;
        this.userRepoConfigs = new ConcurrentHashMap<>();
        this.userConnectInfos = new ConcurrentHashMap<>();
    }

    /**
     * 添加仓库配置
     * 
     * @param userId 用户ID（null表示全局配置）
     * @param config 仓库配置
     */
    public void addRepositoryConfig(String userId, RepositoryConfig config) {
        String key = userId != null ? userId : "global";
        userRepoConfigs.computeIfAbsent(key, k -> new ArrayList<>()).add(config);
        saveConfigToStorage(userId, config);
    }

    /**
     * 获取用户的所有仓库配置
     * 
     * @param userId 用户ID
     * @return 仓库配置列表
     */
    public List<RepositoryConfig> getRepositoryConfigs(String userId) {
        String key = userId != null ? userId : "global";
        
        // 先从内存获取
        List<RepositoryConfig> configs = userRepoConfigs.get(key);
        if (configs != null && !configs.isEmpty()) {
            return new ArrayList<>(configs);
        }
        
        // 从存储加载
        configs = loadConfigsFromStorage(userId);
        if (!configs.isEmpty()) {
            userRepoConfigs.put(key, configs);
        }
        
        return configs;
    }

    /**
     * 获取特定来源的仓库配置
     * 
     * @param userId 用户ID
     * @param source 来源（github/gitee）
     * @return 仓库配置列表
     */
    public List<RepositoryConfig> getRepositoryConfigsBySource(String userId, String source) {
        return getRepositoryConfigs(userId).stream()
                .filter(config -> source.equalsIgnoreCase(config.getSource()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * 保存用户ConnectInfo
     * 
     * @param userId 用户ID
     * @param source 来源（github/gitee）
     * @param connectInfo 连接信息
     */
    public CompletableFuture<Boolean> saveConnectInfo(String userId, String source, ConnectInfo connectInfo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String path = VfsPathStrategy.getConnectInfoPath(userId, source);
                
                Map<String, Object> data = new HashMap<>();
                data.put("userId", connectInfo.getUserID());
                data.put("loginName", connectInfo.getLoginName());
                // 注意：密码应该加密存储，这里简化处理
                data.put("password", connectInfo.getPassword());
                data.put("source", source);
                data.put("timestamp", System.currentTimeMillis());
                
                // 保存到VFS
                byte[] content = mapToJson(data).getBytes();
                vfsManager.writeFile(path + "/connect.json", content).get();
                
                // 缓存到内存
                String key = userId + ":" + source;
                userConnectInfos.put(key, connectInfo);
                
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * 获取用户ConnectInfo
     * 
     * @param userId 用户ID
     * @param source 来源
     * @return ConnectInfo
     */
    public CompletableFuture<ConnectInfo> getConnectInfo(String userId, String source) {
        return CompletableFuture.supplyAsync(() -> {
            String key = userId + ":" + source;
            
            // 先从内存获取
            ConnectInfo cached = userConnectInfos.get(key);
            if (cached != null) {
                return cached;
            }
            
            try {
                // 从VFS加载
                String path = VfsPathStrategy.getConnectInfoPath(userId, source) + "/connect.json";
                byte[] content = vfsManager.readFile(path).get();
                
                if (content != null) {
                    Map<String, Object> data = jsonToMap(new String(content));
                    ConnectInfo connectInfo = new ConnectInfo(
                            (String) data.get("userId"),
                            (String) data.get("loginName"),
                            (String) data.get("password")
                    );
                    userConnectInfos.put(key, connectInfo);
                    return connectInfo;
                }
            } catch (Exception e) {
                // 忽略错误
            }
            
            return null;
        });
    }

    /**
     * 删除仓库配置
     * 
     * @param userId 用户ID
     * @param repoName 仓库名称
     */
    public void removeRepositoryConfig(String userId, String repoName) {
        String key = userId != null ? userId : "global";
        List<RepositoryConfig> configs = userRepoConfigs.get(key);
        if (configs != null) {
            configs.removeIf(config -> repoName.equals(config.getName()));
            saveAllConfigsToStorage(userId, configs);
        }
    }

    /**
     * 更新仓库配置
     * 
     * @param userId 用户ID
     * @param config 仓库配置
     */
    public void updateRepositoryConfig(String userId, RepositoryConfig config) {
        String key = userId != null ? userId : "global";
        List<RepositoryConfig> configs = userRepoConfigs.computeIfAbsent(key, k -> new ArrayList<>());
        
        // 移除旧配置
        configs.removeIf(c -> config.getName().equals(c.getName()));
        // 添加新配置
        configs.add(config);
        
        saveAllConfigsToStorage(userId, configs);
    }

    /**
     * 设置默认仓库
     * 
     * @param userId 用户ID
     * @param source 来源
     * @param repoName 仓库名称
     */
    public void setDefaultRepository(String userId, String source, String repoName) {
        List<RepositoryConfig> configs = getRepositoryConfigs(userId);
        for (RepositoryConfig config : configs) {
            if (source.equalsIgnoreCase(config.getSource())) {
                config.setDefault(repoName.equals(config.getName()));
            }
        }
        saveAllConfigsToStorage(userId, configs);
    }

    /**
     * 获取默认仓库配置
     * 
     * @param userId 用户ID
     * @param source 来源
     * @return 默认仓库配置
     */
    public RepositoryConfig getDefaultRepository(String userId, String source) {
        return getRepositoryConfigs(userId).stream()
                .filter(config -> source.equalsIgnoreCase(config.getSource()) && config.isDefault())
                .findFirst()
                .orElseGet(() -> getRepositoryConfigsBySource(userId, source).stream()
                        .findFirst()
                        .orElse(null));
    }

    /**
     * 验证仓库配置是否有效
     * 
     * @param config 仓库配置
     * @return 是否有效
     */
    public CompletableFuture<Boolean> validateRepositoryConfig(RepositoryConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            // 基本验证
            if (config.getOwner() == null || config.getOwner().isEmpty()) {
                return false;
            }
            
            if (config.isSingleRepoMode()) {
                return config.getSkillsRepo() != null && !config.getSkillsRepo().isEmpty();
            }
            
            return true;
        });
    }

    /**
     * 获取所有用户的仓库配置（管理员功能）
     * 
     * @return 用户仓库配置映射
     */
    public Map<String, List<RepositoryConfig>> getAllUserConfigs() {
        return new HashMap<>(userRepoConfigs);
    }

    /**
     * 保存配置到存储
     */
    private void saveConfigToStorage(String userId, RepositoryConfig config) {
        try {
            String namespace = userId != null ? "user_" + userId : "global";
            String key = "repo_" + config.getName();
            
            Map<String, Object> data = new HashMap<>();
            data.put("name", config.getName());
            data.put("source", config.getSource());
            data.put("owner", config.getOwner());
            data.put("skillsRepo", config.getSkillsRepo());
            data.put("skillsPath", config.getSkillsPath());
            data.put("singleRepoMode", config.isSingleRepoMode());
            data.put("token", config.getToken());
            data.put("apiBaseUrl", config.getApiBaseUrl());
            data.put("webBaseUrl", config.getWebBaseUrl());
            data.put("isDefault", config.isDefault());
            data.put("cacheTtlMs", config.getCacheTtlMs());
            
            storageManager.save(namespace, key, data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save repository config", e);
        }
    }

    /**
     * 保存所有配置到存储
     */
    private void saveAllConfigsToStorage(String userId, List<RepositoryConfig> configs) {
        try {
            String namespace = userId != null ? "user_" + userId : "global";
            
            List<Map<String, Object>> configList = new ArrayList<>();
            for (RepositoryConfig config : configs) {
                Map<String, Object> data = new HashMap<>();
                data.put("name", config.getName());
                data.put("source", config.getSource());
                data.put("owner", config.getOwner());
                data.put("skillsRepo", config.getSkillsRepo());
                data.put("skillsPath", config.getSkillsPath());
                data.put("singleRepoMode", config.isSingleRepoMode());
                data.put("token", config.getToken());
                data.put("apiBaseUrl", config.getApiBaseUrl());
                data.put("webBaseUrl", config.getWebBaseUrl());
                data.put("isDefault", config.isDefault());
                data.put("cacheTtlMs", config.getCacheTtlMs());
                configList.add(data);
            }
            
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("configs", configList);
            storageManager.save(namespace, "repositories", wrapper);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save repository configs", e);
        }
    }

    /**
     * 从存储加载配置
     */
    private List<RepositoryConfig> loadConfigsFromStorage(String userId) {
        List<RepositoryConfig> configs = new ArrayList<>();
        
        try {
            String namespace = userId != null ? "user_" + userId : "global";
            Map<String, Object> data = storageManager.load(namespace, "repositories");
            
            if (data != null && data.containsKey("configs")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> configList = (List<Map<String, Object>>) data.get("configs");
                
                for (Map<String, Object> configData : configList) {
                    RepositoryConfig config = new RepositoryConfig();
                    config.setName((String) configData.get("name"));
                    config.setSource((String) configData.get("source"));
                    config.setOwner((String) configData.get("owner"));
                    config.setSkillsRepo((String) configData.get("skillsRepo"));
                    config.setSkillsPath((String) configData.get("skillsPath"));
                    config.setSingleRepoMode((Boolean) configData.get("singleRepoMode"));
                    config.setToken((String) configData.get("token"));
                    config.setApiBaseUrl((String) configData.get("apiBaseUrl"));
                    config.setWebBaseUrl((String) configData.get("webBaseUrl"));
                    config.setDefault((Boolean) configData.get("isDefault"));
                    
                    Object cacheTtl = configData.get("cacheTtlMs");
                    if (cacheTtl != null) {
                        config.setCacheTtlMs(((Number) cacheTtl).longValue());
                    }
                    
                    configs.add(config);
                }
            }
        } catch (IOException e) {
            // 忽略错误
        }
        
        return configs;
    }

    private String mapToJson(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                sb.append("\"").append(entry.getValue()).append("\"");
            } else {
                sb.append(entry.getValue());
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = new HashMap<>();
        // 简化实现，实际应该使用JSON库
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":");
                if (kv.length == 2) {
                    String key = kv[0].trim().replace("\"", "");
                    String value = kv[1].trim().replace("\"", "");
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    /**
     * 仓库配置类
     */
    public static class RepositoryConfig {
        private String name;
        private String source; // github/gitee
        private String owner;
        private String skillsRepo;
        private String skillsPath = "skills";
        private boolean singleRepoMode = true;
        private String token;
        private String apiBaseUrl;
        private String webBaseUrl;
        private boolean isDefault = false;
        private long cacheTtlMs = 3600000; // 默认1小时

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        public String getSkillsRepo() { return skillsRepo; }
        public void setSkillsRepo(String skillsRepo) { this.skillsRepo = skillsRepo; }
        public String getSkillsPath() { return skillsPath; }
        public void setSkillsPath(String skillsPath) { this.skillsPath = skillsPath; }
        public boolean isSingleRepoMode() { return singleRepoMode; }
        public void setSingleRepoMode(boolean singleRepoMode) { this.singleRepoMode = singleRepoMode; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getApiBaseUrl() { return apiBaseUrl; }
        public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }
        public String getWebBaseUrl() { return webBaseUrl; }
        public void setWebBaseUrl(String webBaseUrl) { this.webBaseUrl = webBaseUrl; }
        public boolean isDefault() { return isDefault; }
        public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
        public long getCacheTtlMs() { return cacheTtlMs; }
        public void setCacheTtlMs(long cacheTtlMs) { this.cacheTtlMs = cacheTtlMs; }
    }
}
