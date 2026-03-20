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
        initDefaultConfigs();
    }
    
    private void loadConfigs() {
        File file = new File(dataDir, "system-skill-configs.json");
        if (file.exists()) {
            try {
                SystemSkillConfig[] array = objectMapper.readValue(file, SystemSkillConfig[].class);
                for (SystemSkillConfig config : array) {
                    skillConfigs.put(config.getSkillId(), config);
                }
                log.info("Loaded {} system skill configs", skillConfigs.size());
            } catch (IOException e) {
                log.warn("Failed to load system skill configs: {}", e.getMessage());
            }
        }
        
        loadHistory();
    }
    
    private void loadHistory() {
        File file = new File(dataDir, "config-history.json");
        if (file.exists()) {
            try {
                ConfigHistory[] array = objectMapper.readValue(file, ConfigHistory[].class);
                for (ConfigHistory history : array) {
                    configHistory.add(history);
                }
                log.info("Loaded {} config history records", configHistory.size());
            } catch (IOException e) {
                log.warn("Failed to load config history: {}", e.getMessage());
            }
        }
    }
    
    private void saveConfigs() {
        try {
            File file = new File(dataDir, "system-skill-configs.json");
            objectMapper.writeValue(file, skillConfigs.values());
        } catch (IOException e) {
            log.error("Failed to save system skill configs: {}", e.getMessage());
        }
    }
    
    private void saveHistory() {
        try {
            File file = new File(dataDir, "config-history.json");
            objectMapper.writeValue(file, configHistory);
        } catch (IOException e) {
            log.error("Failed to save config history: {}", e.getMessage());
        }
    }
    
    private void initDefaultConfigs() {
        if (skillConfigs.isEmpty()) {
            initDefaultSkillConfigs();
        }
    }
    
    private void initDefaultSkillConfigs() {
        long now = System.currentTimeMillis();
        
        addDefaultSkill("skill-db-sqlite", "SQLite 数据库", "database", 
            "轻量级嵌入式数据库，适用于开发和小型部署", true, true, "running", Map.of(
                "url", "jdbc:sqlite:./data/system.db",
                "poolSize", 5,
                "connectionTimeout", 30000
            ), now);
        
        addDefaultSkill("skill-db-mysql", "MySQL 数据库", "database",
            "企业级关系型数据库，适用于生产环境", false, false, "stopped", Map.of(
                "host", "localhost",
                "port", 3306,
                "database", "ooder",
                "username", "root",
                "password", ""
            ), now);
        
        addDefaultSkill("skill-llm-openai", "OpenAI LLM", "llm",
            "OpenAI 大语言模型服务", false, false, "stopped", Map.of(
                "apiKey", "",
                "model", "gpt-4",
                "temperature", 0.7
            ), now);
        
        addDefaultSkill("skill-llm-deepseek", "DeepSeek LLM", "llm",
            "DeepSeek 大语言模型服务", false, false, "stopped", Map.of(
                "apiKey", "",
                "model", "deepseek-chat",
                "baseUrl", "https://api.deepseek.com"
            ), now);
        
        addDefaultSkill("skill-llm-anthropic", "Anthropic LLM", "llm",
            "Anthropic Claude 大语言模型服务", false, false, "stopped", Map.of(
                "apiKey", "",
                "model", "claude-3-opus"
            ), now);
        
        addDefaultSkill("skill-llm-azure", "Azure OpenAI", "llm",
            "Azure OpenAI 服务", false, false, "stopped", Map.of(
                "endpoint", "",
                "apiKey", "",
                "deploymentName", ""
            ), now);
        
        addDefaultSkill("skill-embedding-local", "本地向量嵌入", "embedding",
            "本地向量嵌入服务，支持中英文文本向量化", true, true, "running", Map.of(
                "modelPath", "./models/embedding",
                "modelType", "bge-small-zh",
                "dimension", 512,
                "batchSize", 32
            ), now);
        
        addDefaultSkill("skill-rerank", "重排序服务", "embedding",
            "搜索结果重排序服务", false, false, "stopped", Map.of(
                "modelPath", "./models/rerank",
                "modelType", "bge-reranker"
            ), now);
        
        addDefaultSkill("skill-knowledge-base", "知识库服务", "embedding",
            "知识库管理和检索服务", false, false, "stopped", Map.of(
                "storagePath", "./data/knowledge"
            ), now);
        
        addDefaultSkill("skill-vector-store", "向量存储", "embedding",
            "向量数据库存储服务", false, false, "stopped", Map.of(
                "type", "milvus",
                "host", "localhost",
                "port", 19530
            ), now);
        
        addDefaultSkill("skill-cache-redis", "Redis 缓存", "storage",
            "Redis 缓存服务", false, false, "stopped", Map.of(
                "host", "localhost",
                "port", 6379,
                "database", 0
            ), now);
        
        addDefaultSkill("skill-storage-oss", "OSS 存储", "storage",
            "阿里云 OSS 对象存储服务", false, false, "stopped", Map.of(
                "endpoint", "",
                "accessKeyId", "",
                "accessKeySecret", "",
                "bucketName", ""
            ), now);
        
        addDefaultSkill("skill-storage-minio", "MinIO 存储", "storage",
            "MinIO 对象存储服务", false, false, "stopped", Map.of(
                "endpoint", "http://localhost:9000",
                "accessKey", "minioadmin",
                "secretKey", "minioadmin",
                "bucketName", "ooder"
            ), now);
        
        addDefaultSkill("skill-notification", "通知服务", "other",
            "消息通知服务，支持邮件、短信、推送", false, false, "stopped", Map.of(
                "emailEnabled", false,
                "smsEnabled", false,
                "pushEnabled", false
            ), now);
        
        addDefaultSkill("skill-logging", "日志服务", "monitoring",
            "系统日志收集和管理服务", true, true, "running", Map.of(
                "level", "INFO",
                "format", "json",
                "filePath", "./logs/sdk.log"
            ), now);
        
        addDefaultSkill("skill-metrics", "指标服务", "monitoring",
            "系统指标收集和监控服务", true, true, "running", Map.of(
                "enabled", true,
                "interval", 60,
                "port", 9090
            ), now);
        
        addDefaultSkill("skill-tracing", "链路追踪", "monitoring",
            "分布式链路追踪服务", false, false, "stopped", Map.of(
                "enabled", false,
                "sampleRate", 0.1
            ), now);
        
        saveConfigs();
        log.info("Initialized {} default system skill configs", skillConfigs.size());
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
            
            addHistory(skillId, "update_config", Map.of("oldConfig", oldConfig, "newConfig", config));
            
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
            
            skill.setStatus("running");
            skill.setUpdateTime(System.currentTimeMillis());
            
            SkillRuntimeStatus runtime = new SkillRuntimeStatus();
            runtime.setSkillId(skillId);
            runtime.setStatus("running");
            runtime.setStartTime(System.currentTimeMillis());
            runtime.setUptime(0);
            runtime.setMetrics(new HashMap<>());
            runtimeStatuses.put(skillId, runtime);
            
            addHistory(skillId, "start", Map.of());
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
            
            addHistory(skillId, "stop", Map.of());
            saveConfigs();
            
            log.info("Stopped skill: {}", skillId);
        });
    }
    
    @Override
    public CompletableFuture<List<ConfigHistory>> getConfigHistory(String skillId, int limit) {
        List<ConfigHistory> result = configHistory.stream()
            .filter(h -> skillId == null || skillId.equals(h.getSkillId()))
            .limit(limit > 0 ? limit : 100)
            .collect(Collectors.toList());
        return CompletableFuture.completedFuture(result);
    }
    
    @Override
    public CompletableFuture<SkillRuntimeStatus> getSkillRuntimeStatus(String skillId) {
        SkillRuntimeStatus status = runtimeStatuses.get(skillId);
        if (status != null) {
            status.setUptime(System.currentTimeMillis() - status.getStartTime());
        }
        return CompletableFuture.completedFuture(status);
    }
    
    @Override
    public CompletableFuture<List<SkillCategory>> getCategories() {
        return CompletableFuture.completedFuture(new ArrayList<>(DEFAULT_CATEGORIES));
    }
    
    @Override
    public CompletableFuture<Void> resetSkillConfig(String skillId) {
        return CompletableFuture.runAsync(() -> {
            skillConfigs.clear();
            runtimeStatuses.clear();
            initDefaultSkillConfigs();
            addHistory(skillId, "reset", Map.of());
            log.info("Reset skill config to defaults: {}", skillId);
        });
    }
    
    private void addHistory(String skillId, String action, Map<String, Object> changes) {
        ConfigHistory history = new ConfigHistory();
        history.setId("hist-" + System.currentTimeMillis());
        history.setSkillId(skillId);
        history.setAction(action);
        history.setChanges(changes);
        history.setOperator("system");
        history.setTimestamp(System.currentTimeMillis());
        
        configHistory.add(0, history);
        
        while (configHistory.size() > 1000) {
            configHistory.remove(configHistory.size() - 1);
        }
        
        saveHistory();
    }
}
