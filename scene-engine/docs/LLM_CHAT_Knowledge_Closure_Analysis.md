# LLM-CHAT 通用功能知识点闭环推导（v2.1）

## 一、简化设计核心思路

### 1.1 LLM 辅助配置生成

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    LLM 辅助配置生成流程（v2.1）                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  用户首次调用 Skill                                                          │
│       │                                                                     │
│       ▼                                                                     │
│  检测本地配置是否存在                                                         │
│       │                                                                     │
│       ├── 不存在 ──▶ LLM 分析 SKILLS.MD ──▶ 生成本地 .yaml 配置              │
│       │                                          │                         │
│       │                                          ▼                         │
│       └── 存在 ──▶ 检查版本是否变化                                            │
│                         │                                                   │
│                         ├── 无变化 ──▶ 直接使用现有配置                       │
│                         │                                                   │
│                         └── 有变化 ──▶ LLM 辅助二次修改配置                   │
│                                                                             │
│  ★ 新增：SKILLS.MD 作为主要输入源                                            │
│  ★ 新增：配置热加载机制                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 设计优势

| 优势 | 说明 |
|------|------|
| **零配置启动** | 用户无需手工编写配置，首次调用自动生成 |
| **智能适配** | LLM 根据 SKILLS.MD 生成最优配置 |
| **版本感知** | 程序版本变化时自动更新配置 |
| **渐进增强** | 用户可手动调整 LLM 生成的配置 |
| **热加载** | 配置修改后无需重启，自动生效 |

---

## 二、SKILLS.MD 说明书规范

### 2.1 SKILLS.MD 文件结构

```markdown
# 技能名称

> 版本: 2.3.1 | 作者: Ooder Team | 更新日期: 2026-03-10

## 概述

技能的简要描述，用于 LLM 理解技能用途和生成配置。

## 能力列表

### capability-1: 文档搜索

- **名称**: searchDocuments
- **描述**: 在文档库中搜索符合条件的文档
- **输入参数**:
  - `query` (string, required): 搜索关键词
  - `limit` (integer, optional): 返回数量限制，默认10
  - `category` (enum[report, memo, contract], optional): 文档分类
- **输出**: Document[] - 文档列表

### capability-2: 文档摘要

- **名称**: summarizeDocument
- **描述**: 生成文档的智能摘要
- **输入参数**:
  - `documentId` (string, required): 文档ID
  - `maxLength` (integer, optional): 摘要最大长度，默认500
- **输出**: string - 摘要文本

## 使用场景

### 场景1: 文档检索
用户需要查找特定文档时，调用 searchDocuments 能力。

### 场景2: 快速浏览
用户需要快速了解文档内容时，调用 summarizeDocument 能力。

## 知识库

### 基础知识 (knowledge/basic.md)
- 文档分类规范
- 搜索语法说明

### 高级知识 (knowledge/advanced.md)
- 复杂查询技巧
- 摘要生成算法

### 专家知识 (knowledge/expert.md)
- 自定义索引策略
- 性能优化指南

## 配置建议

### LLM Provider
- 推荐模型: deepseek-chat
- Temperature: 0.7
- MaxTokens: 4096

### Prompt 模板建议
- System Prompt: 你是一个文档管理助手...
- User Prompt: 用户请求：{{query}}

## 版本历史

### v2.3.1 (2026-03-10)
- 新增 category 参数支持枚举
- 优化搜索性能

### v2.3.0 (2026-03-01)
- 初始版本
```

### 2.2 SKILLS.MD 解析器

```java
package net.ooder.scene.llm.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.*;

/**
 * SKILLS.MD 解析器
 *
 * <p>解析 SKILLS.MD 文件，提取：</p>
 * <ul>
 *   <li>技能元信息（名称、版本、描述）</li>
 *   <li>Capability 定义</li>
 *   <li>参数类型信息</li>
 *   <li>配置建议</li>
 *   <li>知识库路径</li>
 * </ul>
 */
public class SkillsMdParser {

    private static final Pattern VERSION_PATTERN = Pattern.compile("版本:\\s*([\\d.]+)");
    private static final Pattern CAPABILITY_PATTERN = Pattern.compile("###\\s*(\\S+):\\s*(.+)");
    private static final Pattern PARAM_PATTERN = Pattern.compile("`(\\w+)`\\s*\\(([^)]+)\\)(?:,\\s*(\\w+))?:\\s*(.+)");
    
    /**
     * 解析 SKILLS.MD 文件
     */
    public SkillsMdDocument parse(Path skillsMdPath) throws IOException {
        String content = Files.readString(skillsMdPath);
        return parse(content);
    }
    
    /**
     * 解析 SKILLS.MD 内容
     */
    public SkillsMdDocument parse(String content) {
        SkillsMdDocument doc = new SkillsMdDocument();
        
        // 1. 解析元信息
        parseMetadata(content, doc);
        
        // 2. 解析概述
        parseOverview(content, doc);
        
        // 3. 解析能力列表
        parseCapabilities(content, doc);
        
        // 4. 解析使用场景
        parseScenarios(content, doc);
        
        // 5. 解析知识库路径
        parseKnowledgePaths(content, doc);
        
        // 6. 解析配置建议
        parseConfigSuggestions(content, doc);
        
        return doc;
    }
    
    private void parseMetadata(String content, SkillsMdDocument doc) {
        // 解析版本
        Matcher versionMatcher = VERSION_PATTERN.matcher(content);
        if (versionMatcher.find()) {
            doc.setVersion(versionMatcher.group(1));
        }
        
        // 解析标题作为名称
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.startsWith("# ") && !line.startsWith("## ")) {
                doc.setName(line.substring(2).trim());
                break;
            }
        }
    }
    
    private void parseOverview(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 概述");
        if (start == -1) return;
        
        int end = content.indexOf("## ", start + 4);
        if (end == -1) end = content.length();
        
        String overview = content.substring(start + 4, end).trim();
        doc.setOverview(overview);
    }
    
    private void parseCapabilities(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 能力列表");
        if (start == -1) return;
        
        int end = content.indexOf("## ", start + 6);
        if (end == -1) end = content.length();
        
        String section = content.substring(start, end);
        String[] blocks = section.split("### ");
        
        for (String block : blocks) {
            if (block.trim().isEmpty()) continue;
            
            CapabilityDefinition cap = parseCapabilityBlock(block);
            if (cap != null) {
                doc.addCapability(cap);
            }
        }
    }
    
    private CapabilityDefinition parseCapabilityBlock(String block) {
        String[] lines = block.split("\n");
        if (lines.length < 2) return null;
        
        CapabilityDefinition cap = new CapabilityDefinition();
        
        // 解析第一行: capability-id: 名称
        String[] header = lines[0].split(":", 2);
        cap.setId(header[0].trim());
        if (header.length > 1) {
            cap.setName(header[1].trim());
        }
        
        // 解析属性
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            
            if (line.startsWith("- **名称**:")) {
                cap.setName(line.substring(7).trim());
            } else if (line.startsWith("- **描述**:")) {
                cap.setDescription(line.substring(7).trim());
            } else if (line.startsWith("- **输入参数**:")) {
                // 解析参数列表
                for (int j = i + 1; j < lines.length; j++) {
                    String paramLine = lines[j].trim();
                    if (!paramLine.startsWith("- `")) break;
                    
                    ParameterDefinition param = parseParameter(paramLine);
                    if (param != null) {
                        cap.addParameter(param);
                    }
                }
            } else if (line.startsWith("- **输出**:")) {
                cap.setOutput(line.substring(7).trim());
            }
        }
        
        return cap;
    }
    
    private ParameterDefinition parseParameter(String line) {
        // 格式: - `paramName` (type, required/optional): description
        Matcher matcher = PARAM_PATTERN.matcher(line);
        if (!matcher.find()) return null;
        
        ParameterDefinition param = new ParameterDefinition();
        param.setName(matcher.group(1));
        param.setType(matcher.group(2));
        param.setRequired("required".equals(matcher.group(3)));
        param.setDescription(matcher.group(4));
        
        // 解析枚举类型
        if (param.getType().startsWith("enum[")) {
            String enumStr = param.getType().substring(5, param.getType().length() - 1);
            param.setEnumValues(Arrays.asList(enumStr.split(",\\s*")));
            param.setType("string");
        }
        
        return param;
    }
    
    private void parseScenarios(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 使用场景");
        if (start == -1) return;
        
        int end = content.indexOf("## ", start + 6);
        if (end == -1) end = content.length();
        
        String section = content.substring(start, end);
        String[] blocks = section.split("### ");
        
        for (String block : blocks) {
            if (block.trim().isEmpty()) continue;
            
            String[] lines = block.split("\n", 2);
            if (lines.length >= 2) {
                ScenarioDefinition scenario = new ScenarioDefinition();
                scenario.setName(lines[0].trim());
                scenario.setDescription(lines[1].trim());
                doc.addScenario(scenario);
            }
        }
    }
    
    private void parseKnowledgePaths(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 知识库");
        if (start == -1) return;
        
        int end = content.indexOf("## ", start + 5);
        if (end == -1) end = content.length();
        
        String section = content.substring(start, end);
        
        // 解析知识库路径
        Pattern pathPattern = Pattern.compile("\\(([^)]+\\.md)\\)");
        Matcher matcher = pathPattern.matcher(section);
        
        while (matcher.find()) {
            doc.addKnowledgePath(matcher.group(1));
        }
    }
    
    private void parseConfigSuggestions(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 配置建议");
        if (start == -1) return;
        
        int end = content.indexOf("## ", start + 6);
        if (end == -1) end = content.length();
        
        String section = content.substring(start, end);
        
        // 解析推荐模型
        Pattern modelPattern = Pattern.compile("推荐模型:\\s*(\\S+)");
        Matcher modelMatcher = modelPattern.matcher(section);
        if (modelMatcher.find()) {
            doc.setSuggestedModel(modelMatcher.group(1));
        }
        
        // 解析 Temperature
        Pattern tempPattern = Pattern.compile("Temperature:\\s*([\\d.]+)");
        Matcher tempMatcher = tempPattern.matcher(section);
        if (tempMatcher.find()) {
            doc.setSuggestedTemperature(Double.parseDouble(tempMatcher.group(1)));
        }
    }
    
    /**
     * SKILLS.MD 文档模型
     */
    public static class SkillsMdDocument {
        private String name;
        private String version;
        private String overview;
        private List<CapabilityDefinition> capabilities = new ArrayList<>();
        private List<ScenarioDefinition> scenarios = new ArrayList<>();
        private List<String> knowledgePaths = new ArrayList<>();
        private String suggestedModel;
        private Double suggestedTemperature;
        
        // getters and setters
    }
    
    /**
     * Capability 定义
     */
    public static class CapabilityDefinition {
        private String id;
        private String name;
        private String description;
        private List<ParameterDefinition> parameters = new ArrayList<>();
        private String output;
        
        // getters and setters
    }
    
    /**
     * 参数定义
     */
    public static class ParameterDefinition {
        private String name;
        private String type;
        private boolean required;
        private String description;
        private List<String> enumValues;
        private String defaultValue;
        
        // getters and setters
    }
    
    /**
     * 场景定义
     */
    public static class ScenarioDefinition {
        private String name;
        private String description;
        
        // getters and setters
    }
}
```

---

## 三、配置热加载设计

### 3.1 热加载架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    配置热加载架构                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                   │
│  │ 文件监听器  │────▶│ 配置解析器  │────▶│ 配置校验器  │                   │
│  │ WatchService│     │ YamlParser  │     │ Validator   │                   │
│  └─────────────┘     └─────────────┘     └─────────────┘                   │
│         │                   │                   │                           │
│         │                   │                   ▼                           │
│         │                   │           ┌─────────────┐                     │
│         │                   │           │ 配置差异    │                     │
│         │                   │           │ 计算器      │                     │
│         │                   │           └─────────────┘                     │
│         │                   │                   │                           │
│         ▼                   ▼                   ▼                           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    配置热更新管理器                                   │   │
│  │                                                                     │   │
│  │  1. 检测配置文件变化                                                 │   │
│  │  2. 解析新配置                                                       │   │
│  │  3. 计算配置差异                                                     │   │
│  │  4. 应用增量更新                                                     │   │
│  │  5. 通知相关组件                                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                 │                                           │
│                                 ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    配置变更事件                                       │   │
│  │                                                                     │   │
│  │  - ConfigChangedEvent                                               │   │
│  │  - FunctionAddedEvent                                               │   │
│  │  - PromptUpdatedEvent                                               │   │
│  │  - RuleChangedEvent                                                 │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 配置热加载服务

```java
package net.ooder.scene.llm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 配置热加载服务
 *
 * <p>支持：</p>
 * <ul>
 *   <li>文件变化监听</li>
 *   <li>配置差异计算</li>
 *   <li>增量更新</li>
 *   <li>变更通知</li>
 * </ul>
 */
public class ConfigHotReloadService {

    private static final Logger log = LoggerFactory.getLogger(ConfigHotReloadService.class);

    private final WatchService watchService;
    private final Map<Path, ConfigWatcher> watchers = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<ConfigChangeListener> listeners = new CopyOnWriteArrayList<>();

    private final YamlConfigParser parser;
    private final ConfigValidator validator;
    private final ConfigDiffCalculator diffCalculator;

    public ConfigHotReloadService() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.parser = new YamlConfigParser();
        this.validator = new ConfigValidator();
        this.diffCalculator = new ConfigDiffCalculator();
    }

    /**
     * 注册配置目录监听
     */
    public void registerWatch(Path configDir, String skillId) {
        Path watchDir = configDir.resolve(skillId);
        
        if (!Files.exists(watchDir)) {
            return;
        }

        try {
            WatchKey key = watchDir.register(watchService,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE);

            ConfigWatcher watcher = new ConfigWatcher(skillId, watchDir, key);
            watchers.put(watchDir, watcher);

            log.info("Registered config watch for skill: {}", skillId);
        } catch (IOException e) {
            log.error("Failed to register watch for: {}", watchDir, e);
        }
    }

    /**
     * 启动监听
     */
    public void start() {
        executor.submit(this::watchLoop);
        log.info("Config hot reload service started");
    }

    /**
     * 停止监听
     */
    public void stop() {
        executor.shutdown();
        try {
            watchService.close();
        } catch (IOException e) {
            // ignore
        }
        log.info("Config hot reload service stopped");
    }

    /**
     * 添加配置变更监听器
     */
    public void addListener(ConfigChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * 监听循环
     */
    private void watchLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WatchKey key = watchService.take();
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    handleWatchEvent(key, event);
                }
                
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in watch loop", e);
            }
        }
    }

    /**
     * 处理文件变化事件
     */
    private void handleWatchEvent(WatchKey key, WatchEvent<?> event) {
        Path watchDir = (Path) key.watchable();
        Path changedFile = watchDir.resolve((Path) event.context());
        
        // 只处理 .yaml 文件
        if (!changedFile.toString().endsWith(".yaml") && 
            !changedFile.toString().endsWith(".yml")) {
            return;
        }

        ConfigWatcher watcher = watchers.get(watchDir);
        if (watcher == null) return;

        log.info("Config file changed: {} ({})", changedFile, event.kind());

        // 防抖处理
        watcher.debounce(changedFile, () -> {
            handleConfigChange(watcher.getSkillId(), changedFile, event.kind());
        });
    }

    /**
     * 处理配置变化
     */
    private void handleConfigChange(String skillId, Path configFile, WatchEvent.Kind<?> kind) {
        try {
            if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                notifyConfigDeleted(skillId, configFile);
                return;
            }

            // 解析新配置
            SkillConfig newConfig = parser.parse(configFile);
            
            // 校验配置
            ValidationResult validation = validator.validate(newConfig);
            if (!validation.isValid()) {
                log.warn("Invalid config: {}", validation.getErrors());
                notifyConfigInvalid(skillId, configFile, validation);
                return;
            }

            // 获取旧配置
            SkillConfig oldConfig = getCurrentConfig(skillId);
            
            // 计算差异
            ConfigDiff diff = diffCalculator.calculate(oldConfig, newConfig);
            
            // 应用更新
            applyConfig(skillId, newConfig, diff);
            
            // 通知变更
            notifyConfigChanged(skillId, configFile, diff);
            
        } catch (Exception e) {
            log.error("Failed to handle config change: {}", configFile, e);
        }
    }

    /**
     * 应用配置更新
     */
    private void applyConfig(String skillId, SkillConfig newConfig, ConfigDiff diff) {
        // 1. 更新 LLM Provider 配置
        if (diff.hasLlmChanges()) {
            updateLlmProvider(skillId, newConfig.getLlmConfig());
        }

        // 2. 更新 Function 定义
        if (diff.hasFunctionChanges()) {
            updateFunctions(skillId, newConfig.getFunctions(), diff);
        }

        // 3. 更新 Prompt 模板
        if (diff.hasPromptChanges()) {
            updatePrompts(skillId, newConfig.getPrompts());
        }

        // 4. 更新规则
        if (diff.hasRuleChanges()) {
            updateRules(skillId, newConfig.getRules(), diff);
        }
    }

    /**
     * 通知配置变更
     */
    private void notifyConfigChanged(String skillId, Path configFile, ConfigDiff diff) {
        ConfigChangedEvent event = new ConfigChangedEvent(
            skillId, configFile, diff, System.currentTimeMillis()
        );

        for (ConfigChangeListener listener : listeners) {
            try {
                listener.onConfigChanged(event);
            } catch (Exception e) {
                log.error("Listener error", e);
            }
        }
    }

    private void notifyConfigDeleted(String skillId, Path configFile) {
        for (ConfigChangeListener listener : listeners) {
            listener.onConfigDeleted(skillId, configFile);
        }
    }

    private void notifyConfigInvalid(String skillId, Path configFile, ValidationResult validation) {
        for (ConfigChangeListener listener : listeners) {
            listener.onConfigInvalid(skillId, configFile, validation);
        }
    }

    private SkillConfig getCurrentConfig(String skillId) {
        // 从配置缓存获取当前配置
        return ConfigCache.get(skillId);
    }

    private void updateLlmProvider(String skillId, LlmConfig config) {
        // 更新 LLM Provider
    }

    private void updateFunctions(String skillId, List<FunctionDef> functions, ConfigDiff diff) {
        // 增量更新 Function
    }

    private void updatePrompts(String skillId, PromptConfig prompts) {
        // 更新 Prompt 模板
    }

    private void updateRules(String skillId, List<RuleDef> rules, ConfigDiff diff) {
        // 增量更新规则
    }

    /**
     * 配置监听器
     */
    private static class ConfigWatcher {
        private final String skillId;
        private final Path watchDir;
        private final WatchKey watchKey;
        private final Map<Path, ScheduledFuture<?>> debounceTasks = new ConcurrentHashMap<>();
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public ConfigWatcher(String skillId, Path watchDir, WatchKey watchKey) {
            this.skillId = skillId;
            this.watchDir = watchDir;
            this.watchKey = watchKey;
        }

        public void debounce(Path file, Runnable action) {
            // 取消之前的任务
            ScheduledFuture<?> existing = debounceTasks.get(file);
            if (existing != null) {
                existing.cancel(false);
            }

            // 延迟执行（防抖）
            ScheduledFuture<?> future = scheduler.schedule(() -> {
                debounceTasks.remove(file);
                action.run();
            }, 500, TimeUnit.MILLISECONDS);

            debounceTasks.put(file, future);
        }

        public String getSkillId() { return skillId; }
        public Path getWatchDir() { return watchDir; }
        public WatchKey getWatchKey() { return watchKey; }
    }
}
```

### 3.3 配置差异计算

```java
package net.ooder.scene.llm.config;

import java.util.*;

/**
 * 配置差异计算器
 */
public class ConfigDiffCalculator {

    /**
     * 计算配置差异
     */
    public ConfigDiff calculate(SkillConfig oldConfig, SkillConfig newConfig) {
        ConfigDiff diff = new ConfigDiff();

        // 1. LLM 配置差异
        diff.setLlmChanges(calculateLlmDiff(
            oldConfig != null ? oldConfig.getLlmConfig() : null,
            newConfig.getLlmConfig()
        ));

        // 2. Function 差异
        diff.setFunctionChanges(calculateFunctionDiff(
            oldConfig != null ? oldConfig.getFunctions() : Collections.emptyList(),
            newConfig.getFunctions()
        ));

        // 3. Prompt 差异
        diff.setPromptChanges(calculatePromptDiff(
            oldConfig != null ? oldConfig.getPrompts() : null,
            newConfig.getPrompts()
        ));

        // 4. 规则差异
        diff.setRuleChanges(calculateRuleDiff(
            oldConfig != null ? oldConfig.getRules() : Collections.emptyList(),
            newConfig.getRules()
        ));

        return diff;
    }

    private LlmDiff calculateLlmDiff(LlmConfig old, LlmConfig new_) {
        if (old == null && new_ == null) return null;
        if (old == null) return new LlmDiff(null, new_, DiffType.ADDED);
        if (new_ == null) return new LlmDiff(old, null, DiffType.REMOVED);

        LlmDiff diff = new LlmDiff(old, new_, DiffType.MODIFIED);
        
        if (!Objects.equals(old.getProvider(), new_.getProvider())) {
            diff.addFieldChange("provider", old.getProvider(), new_.getProvider());
        }
        if (!Objects.equals(old.getModel(), new_.getModel())) {
            diff.addFieldChange("model", old.getModel(), new_.getModel());
        }
        if (!Objects.equals(old.getTemperature(), new_.getTemperature())) {
            diff.addFieldChange("temperature", old.getTemperature(), new_.getTemperature());
        }

        return diff.hasChanges() ? diff : null;
    }

    private FunctionDiff calculateFunctionDiff(List<FunctionDef> old, List<FunctionDef> new_) {
        FunctionDiff diff = new FunctionDiff();

        Map<String, FunctionDef> oldMap = toMap(old, FunctionDef::getName);
        Map<String, FunctionDef> newMap = toMap(new_, FunctionDef::getName);

        // 新增的 Function
        for (String name : newMap.keySet()) {
            if (!oldMap.containsKey(name)) {
                diff.addAdded(newMap.get(name));
            }
        }

        // 删除的 Function
        for (String name : oldMap.keySet()) {
            if (!newMap.containsKey(name)) {
                diff.addRemoved(oldMap.get(name));
            }
        }

        // 修改的 Function
        for (String name : newMap.keySet()) {
            if (oldMap.containsKey(name)) {
                FunctionDef oldFunc = oldMap.get(name);
                FunctionDef newFunc = newMap.get(name);
                
                if (!oldFunc.equals(newFunc)) {
                    diff.addModified(new FunctionChange(oldFunc, newFunc));
                }
            }
        }

        return diff;
    }

    private PromptDiff calculatePromptDiff(PromptConfig old, PromptConfig new_) {
        // 类似实现
        return new PromptDiff();
    }

    private RuleDiff calculateRuleDiff(List<RuleDef> old, List<RuleDef> new_) {
        // 类似实现
        return new RuleDiff();
    }

    private <T> Map<String, T> toMap(List<T> list, java.util.function.Function<T, String> keyExtractor) {
        Map<String, T> map = new LinkedHashMap<>();
        for (T item : list) {
            map.put(keyExtractor.apply(item), item);
        }
        return map;
    }
}

/**
 * 配置差异
 */
public class ConfigDiff {
    private LlmDiff llmChanges;
    private FunctionDiff functionChanges;
    private PromptDiff promptChanges;
    private RuleDiff ruleChanges;

    public boolean hasLlmChanges() { return llmChanges != null; }
    public boolean hasFunctionChanges() { return functionChanges != null && !functionChanges.isEmpty(); }
    public boolean hasPromptChanges() { return promptChanges != null && !promptChanges.isEmpty(); }
    public boolean hasRuleChanges() { return ruleChanges != null && !ruleChanges.isEmpty(); }
    public boolean hasAnyChanges() { return hasLlmChanges() || hasFunctionChanges() || hasPromptChanges() || hasRuleChanges(); }

    // getters and setters
}

/**
 * 差异类型
 */
public enum DiffType {
    ADDED, MODIFIED, REMOVED
}

/**
 * Function 差异
 */
public class FunctionDiff {
    private List<FunctionDef> added = new ArrayList<>();
    private List<FunctionDef> removed = new ArrayList<>();
    private List<FunctionChange> modified = new ArrayList<>();

    public boolean isEmpty() { return added.isEmpty() && removed.isEmpty() && modified.isEmpty(); }
    // getters and setters
}

/**
 * Function 变更
 */
public class FunctionChange {
    private FunctionDef oldFunc;
    private FunctionDef newFunc;
    private List<ParamChange> paramChanges;

    // getters and setters
}
```

### 3.4 配置变更事件

```java
package net.ooder.scene.llm.config;

import java.nio.file.Path;
import java.util.List;

/**
 * 配置变更事件
 */
public class ConfigChangedEvent {

    private final String skillId;
    private final Path configFile;
    private final ConfigDiff diff;
    private final long timestamp;

    public ConfigChangedEvent(String skillId, Path configFile, ConfigDiff diff, long timestamp) {
        this.skillId = skillId;
        this.configFile = configFile;
        this.diff = diff;
        this.timestamp = timestamp;
    }

    public String getSkillId() { return skillId; }
    public Path getConfigFile() { return configFile; }
    public ConfigDiff getDiff() { return diff; }
    public long getTimestamp() { return timestamp; }

    public boolean hasFunctionAdded() {
        return diff.hasFunctionChanges() && !diff.getFunctionChanges().getAdded().isEmpty();
    }

    public boolean hasFunctionRemoved() {
        return diff.hasFunctionChanges() && !diff.getFunctionChanges().getRemoved().isEmpty();
    }
}

/**
 * 配置变更监听器
 */
public interface ConfigChangeListener {

    /**
     * 配置变更
     */
    void onConfigChanged(ConfigChangedEvent event);

    /**
     * 配置删除
     */
    default void onConfigDeleted(String skillId, Path configFile) {}

    /**
     * 配置无效
     */
    default void onConfigInvalid(String skillId, Path configFile, ValidationResult validation) {}
}
```

---

## 四、完整流程（更新版）

### 4.1 知识点全景图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    LLM-CHAT 通用功能知识点闭环（v2.1）                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌───────────┐ │
│  │ 1.SKILLS.MD │────▶│ 2.LLM辅助   │────▶│ 3.本地配置  │────▶│ 4.热加载  │ │
│  │ 说明书      │     │ 配置生成    │     │ .yaml文件   │     │ 运行时    │ │
│  └─────────────┘     └─────────────┘     └─────────────┘     └───────────┘ │
│         │                   │                   │                   │       │
│         ▼                   ▼                   ▼                   ▼       │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌───────────┐ │
│  │ 5.类型映射  │     │ 6.规则引擎  │     │ 7.前端支持  │     │ 8.知识库  │ │
│  │ Java→JSON   │     │ MVEL/SpEL   │     │ JS/TS类型   │     │ 多级加载  │ │
│  └─────────────┘     └─────────────┘     └─────────────┘     └───────────┘ │
│                                                                             │
│  ★ 新增：SKILLS.MD 作为主要输入源                                            │
│  ★ 新增：配置热加载机制                                                       │
│  ★ 新增：配置差异计算                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 完整流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    完整闭环流程（v2.1）                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. Skill 开发阶段                                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 开发者编写 SKILLS.MD 说明书                                          │   │
│  │ - 描述技能用途                                                       │   │
│  │ - 定义 Capability 和参数                                             │   │
│  │ - 提供使用场景                                                       │   │
│  │ - 建议配置参数                                                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│  2. 首次调用阶段                                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ LLM 分析 SKILLS.MD ──▶ 生成 .yaml 配置                               │   │
│  │ - 解析 Capability 定义                                               │   │
│  │ - 生成 Function 映射                                                 │   │
│  │ - 生成 Prompt 模板                                                   │   │
│  │ - 生成路由规则                                                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│  3. 运行阶段                                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 配置热加载服务监听文件变化                                            │   │
│  │ - 检测 .yaml 文件修改                                                │   │
│  │ - 计算配置差异                                                       │   │
│  │ - 增量更新配置                                                       │   │
│  │ - 通知相关组件                                                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│  4. 版本更新阶段                                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 检测版本变化 ──▶ LLM 辅助更新配置                                     │   │
│  │ - 保留用户修改                                                       │   │
│  │ - 添加新 Capability                                                  │   │
│  │ - 更新参数定义                                                       │   │
│  │ - 移除废弃功能                                                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 五、新增组件汇总

### 5.1 SKILLS.MD 相关

| 组件 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| `SkillsMdParser` | P0 | 2天 | SKILLS.MD 解析器 |
| `SkillsMdDocument` | P0 | 1天 | 文档模型 |
| `SkillsMdToConfigConverter` | P0 | 1天 | 转换为配置 |

### 5.2 热加载相关

| 组件 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| `ConfigHotReloadService` | P0 | 2天 | 热加载服务 |
| `ConfigDiffCalculator` | P1 | 1天 | 差异计算器 |
| `ConfigChangeListener` | P1 | 1天 | 变更监听器 |
| `ConfigValidator` | P1 | 1天 | 配置校验器 |

---

## 六、实施路径（更新版）

### 6.1 Phase 1: 核心能力（P0）

```
Week 1-2:
├── SkillsMdParser 解析器
├── LlmConfigGeneratorService 配置生成服务
├── ConfigHotReloadService 热加载服务
├── ConfigVersionManager 版本管理
└── LlmService 统一服务接口
```

### 6.2 Phase 2: 增强能力（P1）

```
Week 3-4:
├── ConfigDiffCalculator 差异计算
├── ConfigValidator 配置校验
├── 多级配置加载器
├── 规则引擎集成（复用 MvelRuleEngine）
└── 函数执行集成（复用 SkillFunctionExecutor）
```

### 6.3 Phase 3: 前端支持（P2）

```
Week 5-6:
├── LLM 生成 TypeScript 类型
├── LLM 生成 JavaScript SDK
└── 前端集成测试
```

---

**文档版本**: 2.1.0  
**更新日期**: 2026-03-10  
**作者**: SE Team
