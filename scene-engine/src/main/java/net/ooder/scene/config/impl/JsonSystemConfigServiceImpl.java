package net.ooder.scene.config.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.scene.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 系统配置服务 - JSON 文件实现
 * 
 * <p>基于 JSON 文件存储系统配置。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JsonSystemConfigServiceImpl implements SystemConfigService {
    
    private static final Logger log = LoggerFactory.getLogger(JsonSystemConfigServiceImpl.class);
    
    private final ObjectMapper objectMapper;
    private final File dataDir;
    private final Map<String, SystemSkillConfig> skillConfigs = new ConcurrentHashMap<>();
    private final List<ConfigHistory> configHistory = new ArrayList<>();
    private final Map<String, SkillRuntimeStatus> runtimeStatuses = new ConcurrentHashMap<>();
    
    private static final List<SkillCategory> DEFAULT_CATEGORIES = Arrays.asList(
        new SkillCategory("database", "数据库服务", 2),
        new SkillCategory("llm", "LLM 服务", 4),
        new SkillCategory("embedding", "向量服务", 3),
        new SkillCategory("storage", "存储服务", 3),
        new SkillCategory("monitoring", "监控服务", 3),
        new SkillCategory("other", "其他服务", 2)
    );
    
    public JsonSystemConfigServiceImpl(String dataPath) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.dataDir = new File(dataPath);
        
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        loadConfigs();
        
        if (skillConfigs.isEmpty()) {
            initDefaultSkills();
        }
    }
    
    private void initDefaultSkills() {
        log.info("Initializing default system skill configs...");
        long now = System.currentTimeMillis();
        
        addDefaultSkill("skill-db-sqlite", "SQLite 数据库", "database", 
            "轻量级嵌入式数据库，适用于开发和小型部署", true, true, "running", 
            createMap("url", "jdbc:sqlite:./data/system.db", 
                     "poolSize", 5,
                     "connectionTimeout", 30000), now);
        
        addDefaultSkill("skill-db-mysql", "MySQL 数据库", "database",
            "企业级关系型数据库，适用于生产环境", false, false, "stopped", 
            createMap("host", "localhost",
                     "port", 3306,
                     "database", "ooder",
                     "username", "root",
                     "password", ""), now);
        
        addDefaultSkill("skill-llm-openai", "OpenAI LLM", "llm",
            "OpenAI 大语言模型服务", false, false, "stopped", 
            createMap("apiKey", "",
                     "model", "gpt-4",
                     "temperature", 0.7), now);
        
        addDefaultSkill("skill-llm-deepseek", "DeepSeek LLM", "llm",
            "DeepSeek 大语言模型服务", false, false, "stopped", 
            createMap("apiKey", "",
                     "model", "deepseek-chat",
                     "baseUrl", "https://api.deepseek.com"), now);
        
        addDefaultSkill("skill-llm-anthropic", "Anthropic LLM", "llm",
            "Anthropic Claude 大语言模型服务", false, false, "stopped", 
            createMap("apiKey", "",
                     "model", "claude-3-opus"), now);
        
        addDefaultSkill("skill-llm-azure", "Azure OpenAI", "llm",
            "Azure OpenAI 服务", false, false, "stopped", 
            createMap("endpoint", "",
                     "apiKey", "",
                     "deploymentName", ""), now);
        
        addDefaultSkill("skill-embedding-local", "本地向量嵌入", "embedding",
            "本地向量嵌入服务，支持中英文文本向量化", true, true, "running", 
            createMap("modelPath", "./models/embedding",
                     "modelType", "bge-small-zh",
                     "dimension", 512,
                     "batchSize", 32), now);
        
        addDefaultSkill("skill-rerank", "重排序服务", "embedding",
            "搜索结果重排序服务", false, false, "stopped", 
            createMap("modelPath", "./models/rerank",
                     "modelType", "bge-reranker"), now);
        
        addDefaultSkill("skill-knowledge-base", "知识库服务", "embedding",
            "知识库管理和检索服务", false, false, "stopped", 
            createMap("storagePath", "./data/knowledge"), now);
        
        addDefaultSkill("skill-vector-store", "向量存储", "embedding",
            "向量数据库存储服务", false, false, "stopped", 
            createMap("type", "milvus",
                     "host", "localhost",
                     "port", 19530), now);
        
        addDefaultSkill("skill-cache-redis", "Redis 缓存", "storage",
            "Redis 缓存服务", false, false, "stopped", 
            createMap("host", "localhost",
                     "port", 6379,
                     "database", 0), now);
        
        addDefaultSkill("skill-storage-oss", "OSS 存储", "storage",
            "阿里云 OSS 对象存储服务", false, false, "stopped", 
            createMap("endpoint", "",
                     "accessKeyId", "",
                     "accessKeySecret", "",
                     "bucketName", ""), now);
        
        addDefaultSkill("skill-storage-minio", "MinIO 存储", "storage",
            "MinIO 对象存储服务", false, false, "stopped", 
            createMap("endpoint", "http://localhost:9000",
                     "accessKey", "minioadmin",
                     "secretKey", "minioadmin",
                     "bucketName", "ooder"), now);
        
        addDefaultSkill("skill-notification", "通知服务", "other",
            "消息通知服务，支持邮件、短信、推送", false, false, "stopped", 
            createMap("emailEnabled", false,
                     "smsEnabled", false,
                     "pushEnabled", false), now);
        
        addDefaultSkill("skill-logging", "日志服务", "monitoring",
            "系统日志收集和管理服务", true, true, "running", 
            createMap("level", "INFO",
                     "format", "json",
                     "filePath", "./logs/sdk.log"), now);
        
        addDefaultSkill("skill-metrics", "指标服务", "monitoring",
            "系统指标收集和监控服务", true, true, "running", 
            createMap("enabled", true,
                     "interval", 60,
                     "port", 9090), now);
        
        addDefaultSkill("skill-tracing", "链路追踪", "monitoring",
            "分布式链路追踪服务", false, false, "stopped", 
            createMap("enabled", false,
                     "sampleRate", 0.1), now);
        
        saveConfigs();
        log.info("Initialized {} default system skill configs", skillConfigs.size());
    }
    
    private Map<String, Object> createMap(Object... keyValues) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
    
    private void addDefaultSkill(String skillId, String name, String category, 
            String description, boolean enabled, boolean autoStart, String status,
            Map<String, Object> config, long now) {
        SystemSkillConfig skill = new SystemSkillConfig(skillId, name);
        skill.setCategory(category);
        skill.setDescription(description);
        skill.setEnabled(enabled);
        skill.setAutoStart(autoStart);
        skill.setStatus(status);
        skill.setConfig(new HashMap<>(config));
        skill.setCreateTime(now);
        skill.setUpdateTime(now);
        skillConfigs.put(skillId, skill);
        
        if ("running".equals(status)) {
            SkillRuntimeStatus runtime = new SkillRuntimeStatus();
            runtime.setSkillId(skillId);
            runtime.setStatus("running");
            runtime.setStartTime(now);
            runtime.setUptime(0);
            runtime.setMetrics(new HashMap<>());
            runtimeStatuses.put(skillId, runtime);
        }
    }
    
    @Override
    public CompletableFuture<List<SystemSkillConfig>> listSystemSkills() {
        return CompletableFuture.completedFuture(new ArrayList<>(skillConfigs.values()));
    }
    
    @Override
    public CompletableFuture<List<SystemSkillConfig>> listSystemSkillsByCategory(String category) {
        List<SystemSkillConfig> result = skillConfigs.values().stream()
            .filter(c -> category.equals(c.getCategory()))
            .collect(Collectors.toList());
        return CompletableFuture.completedFuture(result);
    }
    
    @Override
    public CompletableFuture<SystemSkillConfig> getSkillConfig(String skillId) {
        return CompletableFuture.completedFuture(skillConfigs.get(skillId));
    }
    
    @Override
    public CompletableFuture<Void> updateSkillConfig(String skillId, Map<String, Object> config, boolean restart) {
        return CompletableFuture.runAsync(() -> {
            SystemSkillConfig skill = skillConfigs.get(skillId);
            if (skill == null) {
                throw new RuntimeException("Skill not found: " + skillId);
            }
            
            Map<String, Object> oldConfig = new HashMap<>(skill.getConfig());
            skill.setConfig(new HashMap<>(config));
            skill.setUpdateTime(System.currentTimeMillis());
            
            Map<String, Object> historyData = new HashMap<>();
            historyData.put("oldConfig", oldConfig);
            historyData.put("newConfig", config);
            addHistory(skillId, "update_config", historyData);
            
            saveConfigs();
            
            if (restart && "running".equals(skill.getStatus())) {
                log.info("Restarting skill: {}", skillId);
            }
            
            log.info("Updated skill config: {}", skillId);
        });
    }
    
    @Override
    public CompletableFuture<Void> startSkill(String skillId) {
        return CompletableFuture.runAsync(() -> {
            SystemSkillConfig skill = skillConfigs.get(skillId);
            if (skill == null) {
                throw new RuntimeException("Skill not found: " + skillId);
            }
            
            skill.setEnabled(true);
            skill.setStatus("running");
            skill.setUpdateTime(System.currentTimeMillis());
            
            SkillRuntimeStatus runtime = new SkillRuntimeStatus();
            runtime.setSkillId(skillId);
            runtime.setStatus("running");
            runtime.setStartTime(System.currentTimeMillis());
            runtime.setUptime(0);
            runtime.setMetrics(new HashMap<>());
            runtimeStatuses.put(skillId, runtime);
            
            addHistory(skillId, "start", new HashMap<>());
            saveConfigs();
            
            log.info("Started skill: {}", skillId);
        });
    }
    
    @Override
    public CompletableFuture<Void> stopSkill(String skillId) {
        return CompletableFuture.runAsync(() -> {
            SystemSkillConfig skill = skillConfigs.get(skillId);
            if (skill == null) {
                throw new RuntimeException("Skill not found: " + skillId);
            }
            
            skill.setStatus("stopped");
            skill.setUpdateTime(System.currentTimeMillis());
            
            SkillRuntimeStatus runtime = runtimeStatuses.get(skillId);
            if (runtime != null) {
                runtime.setStatus("stopped");
            }
            
            addHistory(skillId, "stop", new HashMap<>());
            saveConfigs();
            
            log.info("Stopped skill: {}", skillId);
        });
    }
    
    @Override
    public CompletableFuture<Void> resetSkillConfig(String skillId) {
        return CompletableFuture.runAsync(() -> {
            SystemSkillConfig skill = skillConfigs.get(skillId);
            if (skill == null) {
                throw new RuntimeException("Skill not found: " + skillId);
            }
            
            skill.getConfig().clear();
            skill.setUpdateTime(System.currentTimeMillis());
            
            addHistory(skillId, "reset", new HashMap<>());
            saveConfigs();
            
            log.info("Reset skill config: {}", skillId);
        });
    }
    
    @Override
    public CompletableFuture<SkillRuntimeStatus> getSkillRuntimeStatus(String skillId) {
        return CompletableFuture.completedFuture(runtimeStatuses.get(skillId));
    }
    
    @Override
    public CompletableFuture<List<ConfigHistory>> getConfigHistory(String skillId, int limit) {
        List<ConfigHistory> result = configHistory.stream()
            .filter(h -> skillId.equals(h.getSkillId()))
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .limit(limit)
            .collect(Collectors.toList());
        return CompletableFuture.completedFuture(result);
    }
    
    @Override
    public CompletableFuture<List<SkillCategory>> getCategories() {
        return CompletableFuture.completedFuture(DEFAULT_CATEGORIES);
    }
    
    private void addHistory(String skillId, String action, Map<String, Object> data) {
        ConfigHistory history = new ConfigHistory();
        history.setId(UUID.randomUUID().toString());
        history.setSkillId(skillId);
        history.setAction(action);
        Map<String, Map<String, Object>> changes = new HashMap<>();
        changes.put("data", data);
        history.setChanges(changes);
        history.setTimestamp(System.currentTimeMillis());
        configHistory.add(history);
    }
    
    private void loadConfigs() {
        File configFile = new File(dataDir, "system-skills.json");
        if (configFile.exists()) {
            try {
                Map<String, Object> data = objectMapper.readValue(configFile, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> configs = (Map<String, Object>) data.get("skillConfigs");
                if (configs != null) {
                    for (Map.Entry<String, Object> entry : configs.entrySet()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> configMap = (Map<String, Object>) entry.getValue();
                        SystemSkillConfig config = objectMapper.convertValue(configMap, SystemSkillConfig.class);
                        skillConfigs.put(entry.getKey(), config);
                    }
                }
                log.info("Loaded {} skill configs from {}", skillConfigs.size(), configFile);
            } catch (IOException e) {
                log.error("Failed to load configs from " + configFile, e);
            }
        }
    }
    
    private void saveConfigs() {
        File configFile = new File(dataDir, "system-skills.json");
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("skillConfigs", skillConfigs);
            data.put("lastUpdate", System.currentTimeMillis());
            objectMapper.writeValue(configFile, data);
            log.debug("Saved {} skill configs to {}", skillConfigs.size(), configFile);
        } catch (IOException e) {
            log.error("Failed to save configs to " + configFile, e);
        }
    }
}
