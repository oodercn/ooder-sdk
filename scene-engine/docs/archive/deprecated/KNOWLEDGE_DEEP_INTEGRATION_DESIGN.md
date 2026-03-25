# 知识资料库深度整合架构设计

**版本**: v2.4.0  
**日期**: 2026-03-07  
**状态**: 架构设计

---

## 一、核心理念转变

### 1.1 从被动存储到主动观察

**传统模式**：
```
业务系统 → 显式调用 → 知识库存储
```

**新模式**：
```
业务系统 → 正常CRUD → 业务数据库
              ↓
         观察者监听
              ↓
         知识库自动同步
```

### 1.2 知识资料库的三重角色

```
┌─────────────────────────────────────────────────────────────┐
│  知识资料库 (Knowledge Repository)                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  角色一：本地场景业务参与者                                   │
│  ├── 直接参与业务逻辑                                        │
│  ├── 本地知识检索与匹配                                      │
│  └── 离线场景支持                                            │
│                                                             │
│  角色二：知识传递者                                           │
│  ├── 向上层（综合库）提供数据源                               │
│  └── 为 RAG 提供高质量知识                                    │
│                                                             │
│  角色三：LLM 协作者                                           │
│  ├── 知识梳理与精简                                          │
│  ├── 归纳总结                                                │
│  └── 自主优化索引                                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、观察者模式：知识库参与业务CRUD

### 2.1 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│  业务应用层                                                  │
│  ├── 招聘管理 (CRUD: 职位/简历/面试)                         │
│  ├── 审批流程 (CRUD: 申请/审批/归档)                         │
│  └── 客户管理 (CRUD: 客户/跟进/合同)                         │
├─────────────────────────────────────────────────────────────┤
│                      │                                      │
│                      ▼                                      │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  KnowledgeObserver (知识观察者)                       │   │
│  │  ├── @OnCreate → 自动索引                             │   │
│  │  ├── @OnUpdate → 增量更新                             │   │
│  │  ├── @OnDelete → 级联删除                             │   │
│  │  └── @OnQuery → 知识增强                              │   │
│  └─────────────────────────────────────────────────────┘   │
│                      │                                      │
│          ┌──────────┴──────────┐                           │
│          ▼                     ▼                           │
│  ┌─────────────┐       ┌─────────────┐                    │
│  │ 业务数据库   │       │ 知识资料库   │                    │
│  │ (MySQL)     │       │ (Vector)    │                    │
│  └─────────────┘       └─────────────┘                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 注解驱动的观察者

```java
/**
 * 知识实体注解 - 标记需要被知识库观察的实体
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface KnowledgeEntity {
    
    String name();
    
    KnowledgeLayer layer();
    
    boolean autoIndex() default true;
    
    boolean autoOptimize() default true;
}

/**
 * 知识字段注解 - 标记需要向量化的字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KnowledgeField {
    
    boolean vectorize() default true;
    
    boolean summarize() default false;      // LLM 总结
    
    boolean extract() default false;        // LLM 提取关键信息
    
    String embeddingModel() default "default";
}

/**
 * 知识观察者配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface KnowledgeObserverConfig {
    
    boolean enableCreate() default true;
    
    boolean enableUpdate() default true;
    
    boolean enableDelete() default true;
    
    boolean enableQuery() default false;
}
```

### 2.3 实体示例

```java
@Entity
@Table(name = "job_requirement")
@KnowledgeEntity(
    name = "JobRequirement",
    layer = KnowledgeLayer.PROFESSIONAL,
    autoIndex = true,
    autoOptimize = true
)
@KnowledgeObserverConfig(
    enableCreate = true,
    enableUpdate = true,
    enableDelete = true,
    enableQuery = true
)
public class JobRequirement {
    
    @Id
    private String id;
    
    @KnowledgeField(vectorize = true, summarize = true)
    private String title;
    
    @KnowledgeField(vectorize = true, extract = true)
    private String description;
    
    @KnowledgeField(vectorize = false)
    private String department;
    
    @KnowledgeField(vectorize = false)
    private LocalDateTime createTime;
}

// 使用示例
@Service
public class JobRequirementService {
    
    @Autowired
    private JobRequirementRepository repository;
    
    // 正常 CRUD，知识库自动同步
    public JobRequirement create(JobRequirement req) {
        return repository.save(req);
        // 知识库观察者自动触发：
        // 1. 向量化 description
        // 2. LLM 提取关键信息
        // 3. 写入 Vector Store
    }
    
    public JobRequirement update(JobRequirement req) {
        return repository.save(req);
        // 知识库观察者自动触发：
        // 1. 增量更新向量
        // 2. 更新索引
    }
    
    public void delete(String id) {
        repository.deleteById(id);
        // 知识库观察者自动触发：
        // 1. 删除向量
        // 2. 清理索引
    }
}
```

### 2.4 观察者实现

```java
@Component
public class KnowledgeEntityObserver {
    
    @Autowired
    private KnowledgeIndexService indexService;
    
    @Autowired
    private LlmKnowledgeProcessor llmProcessor;
    
    @EventListener
    @Async
    public void onEntityCreate(EntityCreateEvent event) {
        Object entity = event.getEntity();
        KnowledgeEntity config = entity.getClass().getAnnotation(KnowledgeEntity.class);
        
        if (config == null || !config.autoIndex()) {
            return;
        }
        
        // 1. 提取向量字段
        Map<String, Object> vectorData = extractVectorFields(entity);
        
        // 2. LLM 处理（提取、总结）
        ProcessedKnowledge processed = llmProcessor.process(entity, config);
        
        // 3. 写入知识库
        KnowledgeDocument doc = KnowledgeDocument.builder()
            .id(getEntityId(entity))
            .layer(config.layer())
            .content(processed.getContent())
            .vector(processed.getVector())
            .metadata(buildMetadata(entity))
            .build();
        
        indexService.index(doc);
    }
    
    @EventListener
    @Async
    public void onEntityUpdate(EntityUpdateEvent event) {
        // 增量更新逻辑
    }
    
    @EventListener
    @Async
    public void onEntityDelete(EntityDeleteEvent event) {
        // 级联删除逻辑
    }
}
```

---

## 三、LLM 在知识库中的扩展作用

### 3.1 LLM 能力矩阵

```
┌─────────────────────────────────────────────────────────────┐
│  LLM 知识能力                                                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  知识处理层                                          │   │
│  │  ├── 内容梳理：去除冗余、结构化整理                   │   │
│  │  ├── 知识精简：提取核心、压缩存储                     │   │
│  │  ├── 归纳总结：生成摘要、关键词提取                   │   │
│  │  └── 关系抽取：实体识别、关系图谱                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  索引优化层                                          │   │
│  │  ├── 自主优化：分析查询日志、优化索引结构             │   │
│  │  ├── 质量评估：检测过期、重复、低质量知识             │   │
│  │  ├── 智能分类：自动归类、标签生成                     │   │
│  │  └── 预测预取：预测热点、预加载                       │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  知识传递层                                          │   │
│  │  ├── 知识蒸馏：将复杂知识简化传递                     │   │
│  │  ├── 跨层映射：通用→专业→场景的知识适配               │   │
│  │  └── 知识融合：多源知识合并去重                       │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 LLM 知识处理器

```java
public interface LlmKnowledgeProcessor {
    
    /**
     * 处理实体，生成知识文档
     */
    ProcessedKnowledge process(Object entity, KnowledgeEntity config);
    
    /**
     * 知识精简
     */
    String summarize(String content, SummarizeConfig config);
    
    /**
     * 关键信息提取
     */
    ExtractedInfo extract(String content, List<String> fields);
    
    /**
     * 知识质量评估
     */
    QualityScore evaluate(KnowledgeDocument doc);
    
    /**
     * 自主优化索引
     */
    OptimizationPlan optimize(List<QueryLog> logs);
}

@Service
public class LlmKnowledgeProcessorImpl implements LlmKnowledgeProcessor {
    
    @Autowired
    private LlmProvider llmProvider;
    
    @Override
    public ProcessedKnowledge process(Object entity, KnowledgeEntity config) {
        // 1. 提取字段内容
        String rawContent = extractContent(entity);
        
        // 2. LLM 梳理精简
        String prompt = buildProcessPrompt(rawContent, config);
        String processedContent = llmProvider.chat("知识处理助手", prompt);
        
        // 3. 提取关键信息
        ExtractedInfo info = extract(rawContent, getExtractFields(entity));
        
        // 4. 生成向量
        float[] vector = embeddingService.embed(processedContent);
        
        return ProcessedKnowledge.builder()
            .content(processedContent)
            .vector(vector)
            .extractedInfo(info)
            .build();
    }
    
    @Override
    public String summarize(String content, SummarizeConfig config) {
        String prompt = String.format("""
            请对以下内容进行精简总结：
            
            原始内容：
            %s
            
            要求：
            1. 保留核心信息
            2. 去除冗余描述
            3. 字数控制在 %d 字以内
            4. 保留关键数据和数值
            """, content, config.getMaxWords());
        
        return llmProvider.chat("知识精简助手", prompt);
    }
    
    @Override
    public OptimizationPlan optimize(List<QueryLog> logs) {
        String prompt = buildOptimizePrompt(logs);
        String result = llmProvider.chat("索引优化助手", prompt);
        return parseOptimizePlan(result);
    }
}
```

### 3.3 自主优化索引

```java
@Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点
public void autoOptimizeIndex() {
    // 1. 收集查询日志
    List<QueryLog> logs = queryLogRepository.findRecentLogs(7);
    
    // 2. LLM 分析优化方案
    OptimizationPlan plan = llmProcessor.optimize(logs);
    
    // 3. 执行优化
    for (OptimizationAction action : plan.getActions()) {
        switch (action.getType()) {
            case MERGE_DUPLICATES:
                mergeDuplicateKnowledge(action.getTargetIds());
                break;
            case UPDATE_EMBEDDING:
                updateEmbedding(action.getTargetIds());
                break;
            case REINDEX:
                reindexKnowledge(action.getTargetId());
                break;
            case ARCHIVE_STALE:
                archiveStaleKnowledge(action.getTargetIds());
                break;
        }
    }
}
```

---

## 四、LLM 与知识库的工具关系

### 4.1 工具调用架构

```
┌─────────────────────────────────────────────────────────────┐
│  LLM 工具调用层                                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  方案一：MVEL 规则引擎                                │   │
│  │  ├── 优点：离线可用、执行快速                         │   │
│  │  ├── 缺点：灵活性有限、难以处理复杂逻辑               │   │
│  │  └── 适用：简单路由、条件判断                         │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  方案二：Function Call + 模板                        │   │
│  │  ├── 优点：标准化、可扩展、LLM 原生支持               │   │
│  │  ├── 缺点：依赖 LLM 在线                             │   │
│  │  └── 适用：复杂工具调用、多步骤任务                   │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  方案三：混合模式（推荐）                             │   │
│  │  ├── 简单操作 → MVEL 规则                            │   │
│  │  ├── 复杂操作 → Function Call                        │   │
│  │  └── 降级策略 → LLM 不可用时回退 MVEL                 │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 知识库工具定义

```java
/**
 * 知识库工具注册表
 */
@Component
public class KnowledgeToolRegistry {
    
    private final Map<String, KnowledgeTool> tools = new HashMap<>();
    
    @PostConstruct
    public void init() {
        // 注册内置工具
        registerTool(new SearchKnowledgeTool());
        registerTool(new IndexKnowledgeTool());
        registerTool(new SummarizeKnowledgeTool());
        registerTool(new ExtractKnowledgeTool());
        registerTool(new OptimizeIndexTool());
    }
    
    public void registerTool(KnowledgeTool tool) {
        tools.put(tool.getName(), tool);
    }
    
    public List<FunctionDef> getFunctionDefs() {
        return tools.values().stream()
            .map(this::toFunctionDef)
            .collect(Collectors.toList());
    }
    
    public Object executeTool(String name, Map<String, Object> params) {
        KnowledgeTool tool = tools.get(name);
        if (tool == null) {
            throw new ToolNotFoundException(name);
        }
        return tool.execute(params);
    }
}

/**
 * 知识检索工具
 */
public class SearchKnowledgeTool implements KnowledgeTool {
    
    @Override
    public String getName() {
        return "search_knowledge";
    }
    
    @Override
    public String getDescription() {
        return "从知识库中检索相关知识";
    }
    
    @Override
    public List<FunctionParam> getParams() {
        return Arrays.asList(
            new FunctionParam("query", "string", "检索关键词"),
            new FunctionParam("layer", "string", "知识层级：GENERAL/PROFESSIONAL/SCENE"),
            new FunctionParam("top_k", "integer", "返回结果数量")
        );
    }
    
    @Override
    public Object execute(Map<String, Object> params) {
        String query = (String) params.get("query");
        String layer = (String) params.get("layer");
        int topK = (int) params.getOrDefault("top_k", 5);
        
        return knowledgeCapability.retrieve(
            query, 
            KnowledgeLayer.valueOf(layer), 
            Map.of("topK", topK)
        );
    }
}

/**
 * 知识索引工具
 */
public class IndexKnowledgeTool implements KnowledgeTool {
    
    @Override
    public String getName() {
        return "index_knowledge";
    }
    
    @Override
    public String getDescription() {
        return "将新知识索引到知识库";
    }
    
    @Override
    public List<FunctionParam> getParams() {
        return Arrays.asList(
            new FunctionParam("content", "string", "知识内容"),
            new FunctionParam("title", "string", "知识标题"),
            new FunctionParam("layer", "string", "知识层级"),
            new FunctionParam("metadata", "object", "元数据")
        );
    }
    
    @Override
    public Object execute(Map<String, Object> params) {
        // 索引逻辑
    }
}
```

### 4.3 IDE 扩展式 Skills 架构

```java
/**
 * Skill 定义接口
 */
public interface Skill {
    
    String getId();
    
    String getName();
    
    String getDescription();
    
    List<String> getRequiredTools();
    
    String getPromptTemplate();
    
    SkillResult execute(SkillContext context);
}

/**
 * 知识管理 Skill
 */
@SkillMeta(
    id = "knowledge-management",
    name = "知识管理助手",
    description = "帮助用户管理知识库，包括检索、索引、优化等操作",
    requiredTools = {"search_knowledge", "index_knowledge", "summarize_knowledge"}
)
public class KnowledgeManagementSkill implements Skill {
    
    @Override
    public SkillResult execute(SkillContext context) {
        String userQuery = context.getUserQuery();
        
        // 1. LLM 理解意图
        Intent intent = llmProvider.analyzeIntent(userQuery);
        
        // 2. 选择工具
        List<String> tools = selectTools(intent);
        
        // 3. 执行工具链
        List<ToolResult> results = executeToolChain(tools, context);
        
        // 4. 生成回复
        String response = llmProvider.generateResponse(results);
        
        return SkillResult.success(response);
    }
}

/**
 * Skill 注册表
 */
@Component
public class SkillRegistry {
    
    private final Map<String, Skill> skills = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 扫描并注册 Skills
        scanAndRegisterSkills();
    }
    
    public void registerSkill(Skill skill) {
        skills.put(skill.getId(), skill);
    }
    
    public Skill getSkill(String id) {
        return skills.get(id);
    }
    
    public List<Skill> findMatchingSkills(String query) {
        // 根据查询匹配相关 Skills
    }
}
```

---

## 五、LLM 上下文支持模式

### 5.1 模式一：页面悬浮窗实时感知

```
┌─────────────────────────────────────────────────────────────┐
│  业务页面                                                    │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  页面内容                                            │   │
│  │  ├── 数据：当前表单/列表数据                         │   │
│  │  ├── 动作：用户操作行为                              │   │
│  │  └── 元素：页面DOM结构                               │   │
│  └─────────────────────────────────────────────────────┘   │
│                                    │                        │
│                                    ▼                        │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  LLM Chat 悬浮窗                                     │   │
│  │  ┌─────────────────────────────────────────────┐   │   │
│  │  │  实时感知上下文                               │   │   │
│  │  │  ├── 当前页面类型                            │   │   │
│  │  │  ├── 当前数据状态                            │   │   │
│  │  │  ├── 用户操作历史                            │   │   │
│  │  │  └── 可用操作列表                            │   │   │
│  │  └─────────────────────────────────────────────┘   │   │
│  │                                                      │   │
│  │  用户: "帮我筛选符合条件的简历"                       │   │
│  │  LLM: "检测到您在简历列表页面，当前有50份简历。        │   │
│  │        请问筛选条件是什么？"                          │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**实现代码**：

```java
/**
 * 页面上下文感知服务
 */
@Service
public class PageContextAwareService {
    
    /**
     * 获取当前页面上下文
     */
    public PageContext getCurrentContext(String sessionId) {
        return PageContext.builder()
            .pageType(getPageType(sessionId))
            .currentData(getCurrentData(sessionId))
            .userActions(getRecentActions(sessionId))
            .availableActions(getAvailableActions(sessionId))
            .build();
    }
    
    /**
     * 构建增强提示
     */
    public String buildEnhancedPrompt(String userQuery, PageContext context) {
        return String.format("""
            当前页面上下文：
            - 页面类型：%s
            - 当前数据：%s
            - 用户操作历史：%s
            - 可用操作：%s
            
            用户问题：%s
            
            请根据上下文回答用户问题，或执行相应操作。
            """,
            context.getPageType(),
            toJson(context.getCurrentData()),
            context.getUserActions(),
            context.getAvailableActions(),
            userQuery
        );
    }
}

/**
 * 页面上下文
 */
@Data
@Builder
public class PageContext {
    
    private String pageType;              // 页面类型：list/form/detail
    
    private Map<String, Object> currentData;  // 当前数据
    
    private List<UserAction> userActions;     // 用户操作历史
    
    private List<String> availableActions;    // 可用操作列表
    
    private Map<String, Object> pageElements; // 页面元素
}

/**
 * 悬浮窗 Chat 控制器
 */
@RestController
@RequestMapping("/api/llm/chat")
public class FloatingChatController {
    
    @Autowired
    private PageContextAwareService contextService;
    
    @Autowired
    private LlmProvider llmProvider;
    
    @PostMapping("/ask")
    public ChatResponse ask(@RequestBody ChatRequest request) {
        // 1. 获取页面上下文
        PageContext context = contextService.getCurrentContext(request.getSessionId());
        
        // 2. 构建增强提示
        String enhancedPrompt = contextService.buildEnhancedPrompt(
            request.getQuery(), 
            context
        );
        
        // 3. LLM 处理
        String response = llmProvider.chat("智能助手", enhancedPrompt);
        
        // 4. 检测是否需要执行操作
        if (needsAction(response)) {
            ActionPlan plan = parseActionPlan(response);
            return ChatResponse.builder()
                .message(response)
                .actionPlan(plan)
                .build();
        }
        
        return ChatResponse.builder()
            .message(response)
            .build();
    }
}
```

### 5.2 模式二：运行期动态干预

```
┌─────────────────────────────────────────────────────────────┐
│  业务流程                                                    │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐  │
│  │ 请求    │───▶│ 拦截器  │───▶│ 处理器  │───▶│ 响应    │  │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘  │
│                      │                                      │
│                      ▼                                      │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  LLM 动态干预                                        │   │
│  │  ├── 接口数据干预：修改请求/响应数据                  │   │
│  │  ├── 脚本执行干预：动态执行脚本                       │   │
│  │  └── 流程拦截干预：中断/重定向流程                    │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**实现代码**：

```java
/**
 * LLM 拦截器
 */
@Component
public class LlmInterceptor implements HandlerInterceptor {
    
    @Autowired
    private LlmInterventionService interventionService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        // 1. 构建拦截上下文
        InterceptContext context = InterceptContext.builder()
            .requestPath(request.getRequestURI())
            .requestMethod(request.getMethod())
            .requestParams(extractParams(request))
            .requestBody(extractBody(request))
            .build();
        
        // 2. LLM 判断是否需要干预
        InterventionDecision decision = interventionService.shouldIntervene(context);
        
        if (decision.isIntervene()) {
            // 3. 执行干预
            InterventionResult result = interventionService.executeIntervention(
                context, 
                decision
            );
            
            // 4. 处理干预结果
            switch (result.getType()) {
                case MODIFY_REQUEST:
                    modifyRequest(request, result.getModifiedData());
                    return true;
                    
                case MODIFY_RESPONSE:
                    writeResponse(response, result.getResponseData());
                    return false;
                    
                case REDIRECT:
                    response.sendRedirect(result.getRedirectUrl());
                    return false;
                    
                case EXECUTE_SCRIPT:
                    Object scriptResult = executeScript(result.getScript(), context);
                    request.setAttribute("scriptResult", scriptResult);
                    return true;
                    
                case BLOCK:
                    writeErrorResponse(response, result.getErrorMessage());
                    return false;
            }
        }
        
        return true;
    }
}

/**
 * LLM 干预服务
 */
@Service
public class LlmInterventionService {
    
    @Autowired
    private LlmProvider llmProvider;
    
    @Autowired
    private ScriptExecutor scriptExecutor;
    
    /**
     * 判断是否需要干预
     */
    public InterventionDecision shouldIntervene(InterceptContext context) {
        String prompt = buildInterventionPrompt(context);
        String response = llmProvider.chat("干预决策助手", prompt);
        return parseInterventionDecision(response);
    }
    
    /**
     * 执行干预
     */
    public InterventionResult executeIntervention(
            InterceptContext context, 
            InterventionDecision decision) {
        
        switch (decision.getActionType()) {
            case MODIFY_DATA:
                return modifyData(context, decision);
                
            case EXECUTE_SCRIPT:
                return executeScript(context, decision);
                
            case QUERY_KNOWLEDGE:
                return queryKnowledge(context, decision);
                
            default:
                return InterventionResult.noOp();
        }
    }
    
    /**
     * 动态脚本执行
     */
    private InterventionResult executeScript(
            InterceptContext context, 
            InterventionDecision decision) {
        
        // 1. LLM 生成脚本
        String scriptPrompt = buildScriptPrompt(context, decision);
        String script = llmProvider.chat("脚本生成助手", scriptPrompt);
        
        // 2. 安全执行脚本
        Object result = scriptExecutor.execute(script, context);
        
        return InterventionResult.builder()
            .type(InterventionType.SCRIPT_RESULT)
            .scriptResult(result)
            .build();
    }
}

/**
 * 脚本执行器
 */
@Component
public class ScriptExecutor {
    
    @Autowired
    private MvelRuleEngine ruleEngine;
    
    /**
     * 安全执行脚本
     */
    public Object execute(String script, InterceptContext context) {
        // 1. 脚本安全检查
        if (!isScriptSafe(script)) {
            throw new ScriptSecurityException("Script not safe");
        }
        
        // 2. 构建执行上下文
        Map<String, Object> executionContext = new HashMap<>();
        executionContext.put("request", context);
        executionContext.put("knowledge", knowledgeCapability);
        executionContext.put("tools", toolRegistry);
        
        // 3. 执行脚本
        return ruleEngine.execute(script, executionContext);
    }
    
    private boolean isScriptSafe(String script) {
        // 检查危险操作
        List<String> dangerousPatterns = Arrays.asList(
            "Runtime.getRuntime",
            "ProcessBuilder",
            "System.exit",
            "File.delete",
            "Class.forName"
        );
        
        for (String pattern : dangerousPatterns) {
            if (script.contains(pattern)) {
                return false;
            }
        }
        
        return true;
    }
}
```

### 5.3 干预场景示例

```java
/**
 * 干预场景配置
 */
@Configuration
public class InterventionConfig {
    
    @Bean
    public InterventionRule resumeUploadIntervention() {
        return InterventionRule.builder()
            .name("简历上传干预")
            .pathPattern("/api/resume/upload")
            .trigger(TriggerType.PRE_PROCESS)
            .condition("request.contentType.contains('pdf')")
            .intervention(InterventionType.EXECUTE_SCRIPT)
            .script("""
                // 1. 提取简历内容
                String content = tools.execute('extract_pdf', request.file);
                
                // 2. LLM 分析简历
                Map analysis = tools.execute('analyze_resume', content);
                
                // 3. 检索匹配岗位
                List jobs = knowledge.search('岗位要求', analysis.skills);
                
                // 4. 返回匹配结果
                return Map.of(
                    'resume', content,
                    'analysis', analysis,
                    'matchedJobs', jobs
                );
                """)
            .build();
    }
    
    @Bean
    public InterventionRule interviewScheduleIntervention() {
        return InterventionRule.builder()
            .name("面试安排干预")
            .pathPattern("/api/interview/schedule")
            .trigger(TriggerType.PRE_PROCESS)
            .condition("request.action == 'create'")
            .intervention(InterventionType.QUERY_KNOWLEDGE)
            .knowledgeQuery("""
                查询条件：
                - 面试官可用时间
                - 会议室资源
                - 候选人偏好时间
                """)
            .build();
    }
}
```

---

## 六、全文检索替代方案

### 6.1 方案对比

| 方案 | 优势 | 劣势 | 适用场景 |
|------|------|------|----------|
| **Lucene** | 成熟稳定、功能丰富 | 部署复杂、资源消耗 | 大规模全文检索 |
| **Elasticsearch** | 分布式、实时搜索 | 运维成本高 | 企业级搜索 |
| **Meilisearch** | 轻量、易用 | 功能有限 | 中小规模应用 |
| **Typesense** | 快速、开源 | 社区较小 | 实时搜索 |
| **纯向量检索** | 语义理解强 | 精确匹配弱 | 语义搜索场景 |

### 6.2 推荐方案：Vector + BM25 混合

```java
/**
 * 混合检索服务
 */
@Service
public class HybridSearchService {
    
    @Autowired
    private VectorStore vectorStore;
    
    @Autowired
    private Bm25Indexer bm25Indexer;  // 轻量级 BM25 实现
    
    /**
     * 混合检索
     */
    public List<SearchResult> hybridSearch(HybridSearchRequest request) {
        // 1. 向量检索
        List<SearchResult> vectorResults = vectorStore.search(
            request.getQueryVector(),
            request.getTopK() * 2
        );
        
        // 2. BM25 检索
        List<SearchResult> bm25Results = bm25Indexer.search(
            request.getQuery(),
            request.getTopK() * 2
        );
        
        // 3. 融合排序 (RRF)
        return reciprocalRankFusion(vectorResults, bm25Results, request.getTopK());
    }
    
    /**
     * 倒数排名融合
     */
    private List<SearchResult> reciprocalRankFusion(
            List<SearchResult> vectorResults,
            List<SearchResult> bm25Results,
            int topK) {
        
        Map<String, Double> scores = new HashMap<>();
        int k = 60;  // RRF 参数
        
        // 向量结果打分
        for (int i = 0; i < vectorResults.size(); i++) {
            String docId = vectorResults.get(i).getDocId();
            scores.merge(docId, 1.0 / (k + i + 1), Double::sum);
        }
        
        // BM25 结果打分
        for (int i = 0; i < bm25Results.size(); i++) {
            String docId = bm25Results.get(i).getDocId();
            scores.merge(docId, 1.0 / (k + i + 1), Double::sum);
        }
        
        // 排序返回
        return scores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(topK)
            .map(e -> getSearchResult(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }
}
```

---

## 七、总结

### 7.1 核心设计原则

1. **观察者模式**：知识库被动感知业务变化，自动同步
2. **LLM 深度参与**：知识处理、优化、传递全流程
3. **工具标准化**：Function Call + 模板，支持 IDE 扩展
4. **双模式上下文**：悬浮窗感知 + 运行期干预

### 7.2 技术选型

| 组件 | 选择 | 理由 |
|------|------|------|
| 向量存储 | Milvus/Qdrant | 专业、高性能 |
| 全文检索 | BM25（可选） | 轻量、够用 |
| 工具调用 | Function Call + MVEL | 标准化 + 降级 |
| 脚本执行 | MVEL 沙箱 | 安全、可控 |

### 7.3 下一步

1. 实现观察者模式基础设施
2. 实现 LLM 知识处理器
3. 实现工具注册表和 Skills 架构
4. 实现页面上下文感知服务
5. 实现动态干预机制

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
