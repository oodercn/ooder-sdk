# 知识库、LLM、应用开发规范分离设计

**版本**: v2.4.0  
**日期**: 2026-03-07  
**状态**: 架构规范

---

## 一、设计原则

### 1.1 核心原则

| 原则 | 说明 |
|------|------|
| **职责分离** | 知识库、LLM、应用开发三者独立，各自有独立规范 |
| **注解驱动** | 使用注解完成综合性表述，各自独立解析配置属性 |
| **异步优先** | 减少硬性拦截，必须时采用异步方式 |
| **允许失败** | 观察者允许失败，不影响正常业务逻辑 |

### 1.2 三层独立规范

```
┌─────────────────────────────────────────────────────────────┐
│  应用开发规范 (Application Development Spec)                 │
│  ├── @BusinessEntity - 业务实体注解                         │
│  ├── @BusinessField - 业务字段注解                          │
│  └── @BusinessObserver - 业务观察者配置                      │
├─────────────────────────────────────────────────────────────┤
│  知识库规范 (Knowledge Repository Spec)                      │
│  ├── @KnowledgeEntity - 知识实体注解                         │
│  ├── @KnowledgeField - 知识字段注解                          │
│  └── @KnowledgeObserver - 知识观察者配置                      │
├─────────────────────────────────────────────────────────────┤
│  LLM 规范 (LLM Integration Spec)                             │
│  ├── @LlmEntity - LLM 实体注解                               │
│  ├── @LlmField - LLM 字段注解                                │
│  └── @LlmIntervention - LLM 干预配置                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、知识库规范 (Knowledge Repository Spec)

### 2.1 知识库独立需求规格

**定位**：知识资料库是独立的存储和检索服务，不依赖业务系统。

**核心能力**：
- 向量存储与检索
- 三层知识架构（通用/专业/场景）
- 知识生命周期管理
- 知识质量评估

### 2.2 知识库注解定义

```java
/**
 * 知识实体注解 - 独立解析
 * 用于标记需要被知识库管理的实体
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KnowledgeEntity {
    
    /**
     * 知识库名称
     */
    String name();
    
    /**
     * 知识层级
     */
    KnowledgeLayer layer();
    
    /**
     * 向量维度
     */
    int vectorDimension() default 1536;
    
    /**
     * 相似度阈值
     */
    float similarityThreshold() default 0.7f;
    
    /**
     * 是否自动索引
     */
    boolean autoIndex() default true;
    
    /**
     * 知识过期时间（天）
     */
    int expireDays() default -1;
}

/**
 * 知识字段注解 - 独立解析
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KnowledgeField {
    
    /**
     * 是否向量化
     */
    boolean vectorize() default true;
    
    /**
     * 是否存储原文
     */
    boolean store() default true;
    
    /**
     * 是否可过滤
     */
    boolean filterable() default false;
    
    /**
     * 是否可搜索
     */
    boolean searchable() default true;
    
    /**
     * 字段权重
     */
    float weight() default 1.0f;
}

/**
 * 知识观察者配置 - 异步执行，允许失败
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KnowledgeObserverConfig {
    
    /**
     * 是否启用
     */
    boolean enabled() default true;
    
    /**
     * 是否异步执行
     */
    boolean async() default true;
    
    /**
     * 是否允许失败
     */
    boolean allowFailure() default true;
    
    /**
     * 重试次数
     */
    int retryCount() default 3;
    
    /**
     * 失败处理策略
     */
    FailureStrategy failureStrategy() default FailureStrategy.LOG;
    
    enum FailureStrategy {
        LOG,        // 仅记录日志
        RETRY,      // 重试
        IGNORE      // 忽略
    }
}
```

### 2.3 知识库配置解析器

```java
/**
 * 知识库配置解析器 - 独立解析知识库相关注解
 */
@Component
public class KnowledgeConfigResolver {
    
    private final KnowledgeRepository knowledgeRepository;
    
    /**
     * 解析实体上的知识库注解
     */
    public KnowledgeEntityConfig resolve(Class<?> clazz) {
        KnowledgeEntity entity = clazz.getAnnotation(KnowledgeEntity.class);
        if (entity == null) {
            return null;
        }
        
        KnowledgeEntityConfig config = new KnowledgeEntityConfig();
        config.setName(entity.name());
        config.setLayer(entity.layer());
        config.setVectorDimension(entity.vectorDimension());
        config.setSimilarityThreshold(entity.similarityThreshold());
        config.setAutoIndex(entity.autoIndex());
        config.setExpireDays(entity.expireDays());
        
        // 解析字段注解
        Map<String, KnowledgeFieldConfig> fields = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            KnowledgeField kf = field.getAnnotation(KnowledgeField.class);
            if (kf != null) {
                KnowledgeFieldConfig fieldConfig = new KnowledgeFieldConfig();
                fieldConfig.setVectorize(kf.vectorize());
                fieldConfig.setStore(kf.store());
                fieldConfig.setFilterable(kf.filterable());
                fieldConfig.setSearchable(kf.searchable());
                fieldConfig.setWeight(kf.weight());
                fields.put(field.getName(), fieldConfig);
            }
        }
        config.setFields(fields);
        
        // 解析观察者配置
        KnowledgeObserverConfig observer = clazz.getAnnotation(KnowledgeObserverConfig.class);
        if (observer != null) {
            config.setObserverEnabled(observer.enabled());
            config.setAsync(observer.async());
            config.setAllowFailure(observer.allowFailure());
            config.setRetryCount(observer.retryCount());
            config.setFailureStrategy(observer.failureStrategy());
        }
        
        return config;
    }
    
    /**
     * 注册知识实体
     */
    public void register(Class<?> clazz) {
        KnowledgeEntityConfig config = resolve(clazz);
        if (config != null) {
            knowledgeRepository.registerEntityConfig(config);
        }
    }
}
```

---

## 三、LLM 规范 (LLM Integration Spec)

### 3.1 LLM 独立需求规格

**定位**：LLM 是独立的能力提供者，支持在线/离线切换。

**核心能力**：
- 对话生成
- 文本向量化
- Function Calling
- 干预控制

### 3.2 LLM 注解定义

```java
/**
 * LLM 实体注解 - 独立解析
 * 用于标记需要 LLM 处理的实体
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmEntity {
    
    /**
     * LLM 处理器名称
     */
    String processor();
    
    /**
     * 默认模型
     */
    String defaultModel() default "gpt-3.5-turbo";
    
    /**
     * 最大 Token 数
     */
    int maxTokens() default 4000;
    
    /**
     * 温度参数
     */
    float temperature() default 0.7f;
}

/**
 * LLM 字段注解 - 独立解析
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmField {
    
    /**
     * 是否需要 LLM 处理
     */
    boolean process() default false;
    
    /**
     * 处理类型
     */
    LlmProcessType processType() default LlmProcessType.NONE;
    
    /**
     * 处理提示模板
     */
    String promptTemplate() default "";
    
    /**
     * 是否缓存结果
     */
    boolean cache() default true;
    
    enum LlmProcessType {
        NONE,           // 不处理
        SUMMARIZE,      // 总结
        EXTRACT,        // 提取
        TRANSLATE,      // 翻译
        CLASSIFY,       // 分类
        GENERATE        // 生成
    }
}

/**
 * LLM 干预配置 - 独立解析
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmIntervention {
    
    /**
     * 干预模式
     */
    InterventionMode mode() default InterventionMode.PROXY;
    
    /**
     * 触发条件（SpEL 表达式）
     */
    String condition() default "";
    
    /**
     * 是否需要用户确认
     */
    boolean requireConfirm() default false;
    
    /**
     * 超时时间（毫秒）
     */
    long timeout() default 30000;
    
    /**
     * 干预模式说明：
     * - FORCE: 强行干预，在 LLM CHAT 对话指令生命周期内强制执行
     * - PROXY: 代理执行，如"帮我从招聘网站查找..."这类任务
     * - DEFAULT: 默认规则，按预设规则执行
     */
    enum InterventionMode {
        FORCE,      // 强行干预（生命周期可控）
        PROXY,      // 代理执行
        DEFAULT     // 默认规则
    }
}

/**
 * LLM 工具定义注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmTool {
    
    /**
     * 工具名称
     */
    String name();
    
    /**
     * 工具描述
     */
    String description();
    
    /**
     * 工具分类
     */
    String category() default "general";
    
    /**
     * 是否需要权限
     */
    boolean requirePermission() default false;
}
```

### 3.3 LLM 配置解析器

```java
/**
 * LLM 配置解析器 - 独立解析 LLM 相关注解
 */
@Component
public class LlmConfigResolver {
    
    private final LlmToolRegistry toolRegistry;
    
    /**
     * 解析实体上的 LLM 注解
     */
    public LlmEntityConfig resolve(Class<?> clazz) {
        LlmEntity entity = clazz.getAnnotation(LlmEntity.class);
        if (entity == null) {
            return null;
        }
        
        LlmEntityConfig config = new LlmEntityConfig();
        config.setProcessor(entity.processor());
        config.setDefaultModel(entity.defaultModel());
        config.setMaxTokens(entity.maxTokens());
        config.setTemperature(entity.temperature());
        
        // 解析字段注解
        Map<String, LlmFieldConfig> fields = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            LlmField lf = field.getAnnotation(LlmField.class);
            if (lf != null) {
                LlmFieldConfig fieldConfig = new LlmFieldConfig();
                fieldConfig.setProcess(lf.process());
                fieldConfig.setProcessType(lf.processType());
                fieldConfig.setPromptTemplate(lf.promptTemplate());
                fieldConfig.setCache(lf.cache());
                fields.put(field.getName(), fieldConfig);
            }
        }
        config.setFields(fields);
        
        // 解析干预配置
        LlmIntervention intervention = clazz.getAnnotation(LlmIntervention.class);
        if (intervention != null) {
            config.setInterventionMode(intervention.mode());
            config.setCondition(intervention.condition());
            config.setRequireConfirm(intervention.requireConfirm());
            config.setTimeout(intervention.timeout());
        }
        
        return config;
    }
    
    /**
     * 解析工具定义
     */
    public LlmToolConfig resolveTool(Class<?> clazz) {
        LlmTool tool = clazz.getAnnotation(LlmTool.class);
        if (tool == null) {
            return null;
        }
        
        LlmToolConfig config = new LlmToolConfig();
        config.setName(tool.name());
        config.setDescription(tool.description());
        config.setCategory(tool.category());
        config.setRequirePermission(tool.requirePermission());
        
        return config;
    }
}
```

---

## 四、应用开发规范 (Application Development Spec)

### 4.1 应用开发独立需求规格

**定位**：业务应用独立开发，通过注解声明式集成知识库和 LLM。

**核心能力**：
- 业务实体定义
- 业务流程编排
- 基础能力封装（文件、浏览器等）

### 4.2 应用开发注解定义

```java
/**
 * 业务实体注解 - 独立解析
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessEntity {
    
    /**
     * 业务名称
     */
    String name();
    
    /**
     * 业务模块
     */
    String module();
    
    /**
     * 业务描述
     */
    String description() default "";
}

/**
 * 业务字段注解 - 独立解析
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessField {
    
    /**
     * 字段名称
     */
    String label() default "";
    
    /**
     * 是否必填
     */
    boolean required() default false;
    
    /**
     * 验证规则
     */
    String validation() default "";
    
    /**
     * 默认值
     */
    String defaultValue() default "";
}

/**
 * 业务观察者配置 - 异步执行
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessObserverConfig {
    
    /**
     * 是否启用
     */
    boolean enabled() default true;
    
    /**
     * 异步执行
     */
    boolean async() default true;
    
    /**
     * 事务性
     */
    boolean transactional() default false;
    
    /**
     * 执行顺序
     */
    int order() default 0;
}
```

### 4.3 基础能力规范

```java
/**
 * 文件能力 - MVEL 中可用的文件操作
 */
@Component("fileAbility")
public class FileAbility {
    
    /**
     * 读取文件
     */
    public String read(String path) {
        // 安全检查
        if (!isPathAllowed(path)) {
            throw new SecurityException("Path not allowed: " + path);
        }
        return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    }
    
    /**
     * 写入文件
     */
    public void write(String path, String content) {
        if (!isPathAllowed(path)) {
            throw new SecurityException("Path not allowed: " + path);
        }
        FileUtils.writeStringToFile(new File(path), content, StandardCharsets.UTF_8);
    }
    
    /**
     * 列出目录
     */
    public List<String> list(String path) {
        // 实现
    }
    
    private boolean isPathAllowed(String path) {
        // 检查路径是否在允许范围内
        return path.startsWith("/data/") || path.startsWith("/tmp/");
    }
}

/**
 * 浏览器能力 - MVEL 中可用的浏览器操作
 */
@Component("browserAbility")
public class BrowserAbility {
    
    /**
     * 打开 URL
     */
    public String open(String url) {
        // 安全检查
        if (!isUrlAllowed(url)) {
            throw new SecurityException("URL not allowed: " + url);
        }
        // 返回页面内容或操作结果
    }
    
    /**
     * 点击元素
     */
    public void click(String selector) {
        // 实现
    }
    
    /**
     * 输入文本
     */
    public void input(String selector, String text) {
        // 实现
    }
    
    private boolean isUrlAllowed(String url) {
        // 检查 URL 是否在允许范围内
        return true;
    }
}

/**
 * 数据库能力 - MVEL 中可用的数据库操作
 */
@Component("dbAbility")
public class DatabaseAbility {
    
    /**
     * 查询
     */
    public List<Map<String, Object>> query(String sql) {
        // 安全检查：防止 SQL 注入
        if (isSqlDangerous(sql)) {
            throw new SecurityException("Dangerous SQL: " + sql);
        }
        // 执行查询
    }
    
    /**
     * 更新
     */
    public int update(String sql) {
        // 安全检查
    }
    
    private boolean isSqlDangerous(String sql) {
        String upper = sql.toUpperCase();
        return upper.contains("DROP") || upper.contains("TRUNCATE") || upper.contains("DELETE FROM");
    }
}
```

---

## 五、注解组合使用示例

### 5.1 完整实体定义

```java
/**
 * 简历实体 - 展示三套注解独立解析
 */
@Entity
@Table(name = "resume")

// === 业务开发规范注解 ===
@BusinessEntity(name = "简历", module = "recruitment", description = "候选人简历信息")
@BusinessObserverConfig(enabled = true, async = true, order = 1)

// === 知识库规范注解 ===
@KnowledgeEntity(
    name = "ResumeKnowledge",
    layer = KnowledgeLayer.SCENE,
    vectorDimension = 1536,
    autoIndex = true
)
@KnowledgeObserverConfig(
    enabled = true,
    async = true,
    allowFailure = true,
    failureStrategy = FailureStrategy.LOG
)

// === LLM 规范注解 ===
@LlmEntity(processor = "resumeProcessor", defaultModel = "gpt-3.5-turbo")
@LlmIntervention(
    mode = InterventionMode.PROXY,
    condition = "#action == 'analyze'",
    requireConfirm = true
)
public class Resume {
    
    @Id
    private String id;
    
    // === 业务字段 ===
    @BusinessField(label = "姓名", required = true)
    
    // === 知识字段 ===
    @KnowledgeField(vectorize = true, store = true, weight = 1.0f)
    
    // === LLM 字段 ===
    @LlmField(process = false)
    private String name;
    
    // === 业务字段 ===
    @BusinessField(label = "简历内容", required = true)
    
    // === 知识字段 ===
    @KnowledgeField(vectorize = true, store = true, weight = 2.0f)
    
    // === LLM 字段 ===
    @LlmField(
        process = true,
        processType = LlmProcessType.EXTRACT,
        promptTemplate = "请从以下简历中提取关键技能：${content}",
        cache = true
    )
    private String content;
    
    // === 业务字段 ===
    @BusinessField(label = "技能标签")
    
    // === 知识字段 ===
    @KnowledgeField(vectorize = true, filterable = true)
    
    // === LLM 字段 ===
    @LlmField(process = true, processType = LlmProcessType.CLASSIFY)
    private List<String> skills;
    
    // getters and setters
}
```

### 5.2 注解独立解析流程

```java
/**
 * 实体配置注册器 - 分别解析三套注解
 */
@Component
public class EntityConfigRegistry {
    
    @Autowired
    private KnowledgeConfigResolver knowledgeResolver;
    
    @Autowired
    private LlmConfigResolver llmResolver;
    
    @Autowired
    private BusinessConfigResolver businessResolver;
    
    /**
     * 注册实体 - 分别解析三套注解
     */
    public void registerEntity(Class<?> clazz) {
        // 1. 解析业务注解（独立）
        BusinessEntityConfig businessConfig = businessResolver.resolve(clazz);
        if (businessConfig != null) {
            businessRegistry.register(businessConfig);
        }
        
        // 2. 解析知识库注解（独立）
        KnowledgeEntityConfig knowledgeConfig = knowledgeResolver.resolve(clazz);
        if (knowledgeConfig != null) {
            knowledgeRepository.registerEntityConfig(knowledgeConfig);
        }
        
        // 3. 解析 LLM 注解（独立）
        LlmEntityConfig llmConfig = llmResolver.resolve(clazz);
        if (llmConfig != null) {
            llmRegistry.register(llmConfig);
        }
    }
}
```

---

## 六、异步观察者实现

### 6.1 统一异步事件总线

```java
/**
 * 异步事件总线 - 所有观察者通过事件总线异步执行
 */
@Component
public class AsyncEventBus {
    
    private final ExecutorService executor;
    private final Map<Class<?>, List<EventHandler<?>>> handlers = new ConcurrentHashMap<>();
    
    public AsyncEventBus() {
        this.executor = Executors.newFixedThreadPool(10);
    }
    
    /**
     * 发布事件 - 异步执行
     */
    public <T> void publish(T event) {
        List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers == null || eventHandlers.isEmpty()) {
            return;
        }
        
        for (EventHandler<?> handler : eventHandlers) {
            executor.submit(() -> {
                try {
                    ((EventHandler<T>) handler).handle(event);
                } catch (Exception e) {
                    // 允许失败，记录日志
                    log.error("Event handler failed: {}", handler.getClass().getName(), e);
                }
            });
        }
    }
    
    /**
     * 注册处理器
     */
    public <T> void registerHandler(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
}

/**
 * 事件处理器接口
 */
public interface EventHandler<T> {
    
    void handle(T event);
    
    int getOrder();
    
    boolean isEnabled();
}
```

### 6.2 知识库观察者（异步，允许失败）

```java
/**
 * 知识库观察者 - 异步执行，允许失败
 */
@Component
public class KnowledgeIndexObserver implements EventHandler<EntityChangeEvent> {
    
    @Autowired
    private KnowledgeIndexService indexService;
    
    @Autowired
    private KnowledgeConfigResolver configResolver;
    
    @Override
    public void handle(EntityChangeEvent event) {
        Object entity = event.getEntity();
        Class<?> clazz = entity.getClass();
        
        // 解析知识库配置
        KnowledgeEntityConfig config = configResolver.resolve(clazz);
        if (config == null || !config.isAutoIndex()) {
            return;
        }
        
        // 检查观察者配置
        if (!config.isObserverEnabled()) {
            return;
        }
        
        try {
            switch (event.getType()) {
                case CREATE:
                case UPDATE:
                    indexService.index(entity, config);
                    break;
                case DELETE:
                    indexService.delete(getEntityId(entity), config);
                    break;
            }
        } catch (Exception e) {
            // 允许失败，根据配置处理
            if (config.getFailureStrategy() == FailureStrategy.LOG) {
                log.error("Knowledge index failed for entity: {}", getEntityId(entity), e);
            } else if (config.getFailureStrategy() == FailureStrategy.RETRY) {
                // 重试逻辑
                retryIndex(entity, config, config.getRetryCount());
            }
            // IGNORE: 直接忽略
        }
    }
    
    @Override
    public int getOrder() {
        return 100;  // 知识库观察者优先级较低
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

### 6.3 LLM 干预处理器

```java
/**
 * LLM 干预处理器 - 根据干预模式执行
 */
@Component
public class LlmInterventionHandler implements EventHandler<InterventionEvent> {
    
    @Autowired
    private LlmProvider llmProvider;
    
    @Autowired
    private LlmConfigResolver configResolver;
    
    @Autowired
    private MvelRuleEngine ruleEngine;
    
    @Override
    public void handle(InterventionEvent event) {
        Object entity = event.getEntity();
        Class<?> clazz = entity.getClass();
        
        // 解析 LLM 配置
        LlmEntityConfig config = configResolver.resolve(clazz);
        if (config == null) {
            return;
        }
        
        InterventionMode mode = config.getInterventionMode();
        
        switch (mode) {
            case FORCE:
                // 强行干预 - 在 LLM CHAT 生命周期内强制执行
                handleForceIntervention(event, config);
                break;
                
            case PROXY:
                // 代理执行 - 如"帮我从招聘网站查找..."
                handleProxyIntervention(event, config);
                break;
                
            case DEFAULT:
            default:
                // 默认规则 - 按预设规则执行
                handleDefaultIntervention(event, config);
                break;
        }
    }
    
    /**
     * 强行干预 - 生命周期可控
     */
    private void handleForceIntervention(InterventionEvent event, LlmEntityConfig config) {
        // 检查是否在 LLM CHAT 对话生命周期内
        if (!isInLlmChatSession(event.getSessionId())) {
            log.warn("Force intervention ignored: not in LLM CHAT session");
            return;
        }
        
        // 执行强行干预
        String instruction = event.getInstruction();
        Object result = executeWithLlm(instruction, event.getContext());
        
        // 返回结果
        event.setResult(result);
    }
    
    /**
     * 代理执行
     */
    private void handleProxyIntervention(InterventionEvent event, LlmEntityConfig config) {
        String instruction = event.getInstruction();
        
        // 检查是否需要用户确认
        if (config.isRequireConfirm()) {
            boolean confirmed = requestUserConfirm(event.getUserId(), instruction);
            if (!confirmed) {
                event.setResult("用户取消操作");
                return;
            }
        }
        
        // LLM 理解指令并执行
        String actionPlan = llmProvider.chat("任务规划助手", 
            "请分析以下任务并生成执行计划：" + instruction);
        
        // 执行计划
        Object result = executeActionPlan(actionPlan, event.getContext());
        event.setResult(result);
    }
    
    /**
     * 默认规则
     */
    private void handleDefaultIntervention(InterventionEvent event, LlmEntityConfig config) {
        // 使用 MVEL 规则引擎执行
        String condition = config.getCondition();
        if (condition != null && !condition.isEmpty()) {
            boolean shouldIntervene = ruleEngine.execute(condition, event.getContext());
            if (!shouldIntervene) {
                return;
            }
        }
        
        // 执行默认逻辑
        Object result = ruleEngine.execute(config.getDefaultScript(), event.getContext());
        event.setResult(result);
    }
}
```

---

## 七、MVEL 能力扩展规范

### 7.1 能力注册表

```java
/**
 * MVEL 能力注册表 - 提供基础能力给脚本使用
 */
@Component
public class MvelAbilityRegistry {
    
    @Autowired
    private FileAbility fileAbility;
    
    @Autowired
    private BrowserAbility browserAbility;
    
    @Autowired
    private DatabaseAbility dbAbility;
    
    @Autowired
    private HttpAbility httpAbility;
    
    @Autowired
    private KnowledgeAbility knowledgeAbility;
    
    /**
     * 获取所有可用能力
     */
    public Map<String, Object> getAbilities() {
        Map<String, Object> abilities = new HashMap<>();
        abilities.put("file", fileAbility);
        abilities.put("browser", browserAbility);
        abilities.put("db", dbAbility);
        abilities.put("http", httpAbility);
        abilities.put("knowledge", knowledgeAbility);
        return abilities;
    }
    
    /**
     * 注入到 MVEL 上下文
     */
    public void injectToContext(Map<String, Object> context) {
        context.putAll(getAbilities());
    }
}
```

### 7.2 能力使用示例

```java
// MVEL 脚本中使用能力

// 文件能力
String content = file.read("/data/resume/001.pdf");
file.write("/data/output/result.txt", "处理结果");

// 浏览器能力
browser.open("https://recruitment.example.com");
browser.input("#search", "Java开发");
browser.click("#search-btn");
String results = browser.getText(".result-list");

// 数据库能力
List<Map<String, Object>> resumes = db.query(
    "SELECT * FROM resume WHERE status = 'pending'"
);

// HTTP 能力
String response = http.get("https://api.example.com/resumes");
Map<String, Object> data = http.post("https://api.example.com/analyze", requestBody);

// 知识能力
List<SearchResult> knowledge = knowledge.search("Java岗位要求", 5);
knowledge.index("新知识内容", Map.of("source", "manual"));
```

---

## 八、页面感知最小实现（参考 A2UI）

### 8.1 页面上下文接口

```java
/**
 * 页面上下文 - 最小实现
 */
public interface PageContextProvider {
    
    /**
     * 获取当前页面类型
     */
    String getPageType();
    
    /**
     * 获取当前页面数据
     */
    Map<String, Object> getPageData();
    
    /**
     * 获取当前用户操作
     */
    List<String> getRecentActions();
    
    /**
     * 获取可用操作列表
     */
    List<String> getAvailableActions();
}

/**
 * 页面上下文实现 - 基础实现
 */
@Component
public class BasicPageContextProvider implements PageContextProvider {
    
    @Override
    public String getPageType() {
        // 从请求上下文获取页面类型
        return RequestContextHolder.getCurrentRequest()
            .getAttribute("pageType", String.class);
    }
    
    @Override
    public Map<String, Object> getPageData() {
        // 从请求上下文获取页面数据
        return RequestContextHolder.getCurrentRequest()
            .getAttribute("pageData", Map.class);
    }
    
    @Override
    public List<String> getRecentActions() {
        // 从会话获取最近操作
        return SessionContextHolder.getCurrentSession()
            .getAttribute("recentActions", List.class);
    }
    
    @Override
    public List<String> getAvailableActions() {
        // 根据页面类型和用户权限返回可用操作
        return ActionRegistry.getAvailableActions(getPageType(), getCurrentUserId());
    }
}
```

---

## 九、总结

### 9.1 三套规范独立关系

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │ 业务开发规范     │  │ 知识库规范       │  │ LLM 规范    │ │
│  │                 │  │                 │  │             │ │
│  │ @BusinessEntity │  │ @KnowledgeEntity│  │ @LlmEntity  │ │
│  │ @BusinessField  │  │ @KnowledgeField │  │ @LlmField   │ │
│  │ @BusinessObserver│ │ @KnowledgeObserver│ │@LlmIntervention│
│  │                 │  │                 │  │             │ │
│  └────────┬────────┘  └────────┬────────┘  └──────┬──────┘ │
│           │                    │                   │        │
│           ▼                    ▼                   ▼        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │ BusinessConfig  │  │ KnowledgeConfig │  │ LlmConfig   │ │
│  │ Resolver        │  │ Resolver        │  │ Resolver    │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
│                                                             │
│           │                    │                   │        │
│           └────────────────────┼───────────────────┘        │
│                                ▼                            │
│                    ┌─────────────────────┐                  │
│                    │  AsyncEventBus      │                  │
│                    │  (异步事件总线)      │                  │
│                    └─────────────────────┘                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 9.2 核心设计要点

| 要点 | 说明 |
|------|------|
| **独立解析** | 三套注解各自独立解析，互不依赖 |
| **异步执行** | 所有观察者通过事件总线异步执行 |
| **允许失败** | 知识库观察者允许失败，不影响业务 |
| **干预模式** | FORCE/PROXY/DEFAULT 三种模式 |
| **能力扩展** | MVEL 中提供文件、浏览器等基础能力 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
