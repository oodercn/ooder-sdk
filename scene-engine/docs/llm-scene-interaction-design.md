# LLM 与场景技能交互设计方案

**版本**: v1.0  
**日期**: 2026-03-09  
**状态**: 详细设计  

---

## 一、设计目标

### 1.1 核心目标

| 目标 | 说明 | 优先级 |
|-----|------|--------|
| **场景上下文初始化** | Engine 完成 LLM 场景上下文的初始化工作 | P0 |
| **NLP 上下文管理** | 场景定义支持 NLP 管理上下文功能 | P0 |
| **A2A 上下文传递** | A2A 命令协议支持上下文传递 | P0 |
| **LLM 间交互** | LLM-A 与 LLM-B 能完成信息交互 | P1 |
| **智能安装引导** | LLM 驱动的场景技能智能安装 | P1 |

### 1.2 设计原则

1. **分层解耦**: LLM 层、场景层、技能层职责清晰分离
2. **上下文驱动**: 所有交互基于统一的场景上下文
3. **协议标准化**: A2A 协议支持上下文传递和 LLM 间通信
4. **可扩展性**: 支持多模型、多场景、多技能的灵活组合

---

## 二、分层架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        LLM 与场景技能交互架构                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    应用层 (Application Layer)                        │   │
│  │                                                                     │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │   │
│  │   │ Scene Skill │  │  Rich Skill │  │    Agent    │  │   User    │ │   │
│  │   │   (场景技能) │  │  (富技能)    │  │   (智能体)   │  │  (用户)    │ │   │
│  │   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └─────┬─────┘ │   │
│  └──────────┼────────────────┼────────────────┼───────────────┼───────┘   │
│             │                │                │               │           │
│             └────────────────┴────────────────┘               │           │
│                              │                                │           │
│                              ▼                                ▼           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    场景引擎层 (Scene Engine Layer)                    │   │
│  │                                                                     │   │
│  │   ┌─────────────────────────────────────────────────────────────┐  │   │
│  │   │              Scene Orchestrator (场景编排器)                  │  │   │
│  │   │                                                             │  │   │
│  │   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │  │   │
│  │   │  │   Scene     │  │   Skill     │  │   Context   │         │  │   │
│  │   │  │   Manager   │  │   Registry  │  │   Manager   │         │  │   │
│  │   │  └─────────────┘  └─────────────┘  └─────────────┘         │  │   │
│  │   └─────────────────────────────────────────────────────────────┘  │   │
│  │                              │                                      │   │
│  │   ┌──────────────────────────┼──────────────────────────┐          │   │
│  │   ▼                          ▼                          ▼          │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │   │
│  │   │ Activation  │  │    Menu     │  │ Dependency  │  │  Install  │ │   │
│  │   │   Engine    │  │   Engine    │  │   Engine    │  │   Engine  │ │   │
│  │   └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    LLM 集成层 (LLM Integration Layer)                 │   │
│  │                                                                     │   │
│  │   ┌─────────────────────────────────────────────────────────────┐  │   │
│  │   │              LLM Context Manager (LLM 上下文管理器)            │  │   │
│  │   │                                                             │  │   │
│  │   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │  │   │
│  │   │  │   Scene     │  │    NLP      │  │ Knowledge   │         │  │   │
│  │   │  │   Context   │  │   Context   │  │   Context   │         │  │   │
│  │   │  └─────────────┘  └─────────────┘  └─────────────┘         │  │   │
│  │   └─────────────────────────────────────────────────────────────┘  │   │
│  │                              │                                      │   │
│  │   ┌──────────────────────────┼──────────────────────────┐          │   │
│  │   ▼                          ▼                          ▼          │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │   │
│  │   │   LLM-A     │  │   LLM-B     │  │   LLM-C     │  │  LLM-Pool │ │   │
│  │   │  (场景LLM)   │  │  (技能LLM)   │  │  (知识LLM)   │  │ (LLM池)   │ │   │
│  │   └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    A2A 协议层 (A2A Protocol Layer)                    │   │
│  │                                                                     │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │   │
│  │   │   Command   │  │   Context   │  │   Message   │  │  Routing  │ │   │
│  │   │   Router    │  │  Transfer   │  │   Queue     │  │   Table   │ │   │
│  │   └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 层次职责

| 层次 | 核心职责 | 关键组件 |
|-----|---------|---------|
| **应用层** | 提供用户交互界面和业务场景 | Scene Skill, Rich Skill, Agent |
| **场景引擎层** | 管理场景生命周期和技能编排 | Scene Orchestrator, Context Manager |
| **LLM 集成层** | 管理 LLM 上下文和模型调用 | LLM Context Manager, LLM Pool |
| **A2A 协议层** | 支持跨场景、跨 LLM 的通信 | Command Router, Context Transfer |

---

## 三、核心组件设计

### 3.1 LLM 场景上下文 (LlmSceneContext)

#### 3.1.1 上下文结构

```java
/**
 * LLM 场景上下文 - 核心数据结构
 * 封装 LLM 在特定场景中所需的所有上下文信息
 */
@Data
@Builder
public class LlmSceneContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 基础标识 ==========
    private String contextId;           // 上下文唯一标识
    private String sceneId;             // 场景ID
    private String skillId;             // 技能ID
    private String agentId;             // Agent ID
    private String userId;              // 用户ID
    private String sessionId;           // 会话ID
    
    // ========== 子上下文 ==========
    private SceneContext sceneContext;      // 场景上下文
    private NlpContext nlpContext;          // NLP 上下文
    private KnowledgeContext knowledgeContext;  // 知识上下文
    private ToolContext toolContext;        // 工具上下文
    private SecurityContext securityContext;    // 安全上下文
    
    // ========== 状态信息 ==========
    private ContextStatus status;       // 上下文状态
    private String currentStep;         // 当前步骤
    private Map<String, Object> stepData;   // 步骤数据
    
    // ========== 时间戳 ==========
    private long createdAt;             // 创建时间
    private long lastAccessedAt;        // 最后访问时间
    private long expiresAt;             // 过期时间
    
    // ========== 扩展属性 ==========
    private Map<String, Object> extendedAttributes;
    
    public void touch() {
        this.lastAccessedAt = System.currentTimeMillis();
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > this.expiresAt;
    }
}

/**
 * 场景上下文
 */
@Data
@Builder
public class SceneContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sceneType;           // 场景类型
    private String sceneName;           // 场景名称
    private String sceneDescription;    // 场景描述
    
    private List<String> roles;         // 角色列表
    private String currentRole;         // 当前角色
    
    private Map<String, Object> sceneConfig;    // 场景配置
    private Map<String, Object> runtimeData;    // 运行时数据
    
    private List<String> activatedSkills;   // 已激活技能
    private List<String> availableSkills;   // 可用技能
}

/**
 * NLP 上下文
 */
@Data
@Builder
public class NlpContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String nlpContextId;        // NLP 上下文ID
    private String componentType;       // 组件类型
    private String moduleViewType;      // 模块视图类型
    
    private CustomModuleMeta moduleMeta;    // 模块元数据
    private CustomDataMeta dataMeta;        // 数据元数据
    
    private Map<String, NlpComponentContext> componentContexts; // 组件上下文
    private List<String> activeComponentIds;    // 活跃组件ID列表
    
    private String currentExpression;   // 当前表达式
    private Map<String, Object> expressionVariables;    // 表达式变量
}

/**
 * 知识上下文
 */
@Data
@Builder
public class KnowledgeContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String knowledgeBaseId;     // 知识库ID
    private String knowledgeBaseType;   // 知识库类型
    
    private List<String> accessibleKnowledgeBases;  // 可访问知识库列表
    private Map<String, Object> searchFilters;      // 搜索过滤器
    
    private int maxResults;             // 最大返回结果数
    private float similarityThreshold;  // 相似度阈值
    
    private List<String> recentDocuments;   // 最近访问文档
    private Map<String, Object> documentCache;  // 文档缓存
}

/**
 * 工具上下文
 */
@Data
@Builder
public class ToolContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<ToolDefinition> availableTools;    // 可用工具列表
    private List<String> activeToolIds;             // 活跃工具ID
    
    private Map<String, ToolExecutionResult> toolResults;   // 工具执行结果
    private Map<String, Object> toolParameters;             // 工具参数
    
    private int maxToolCalls;         // 最大工具调用次数
    private int currentToolCallCount; // 当前工具调用次数
}
```

#### 3.1.2 上下文状态机

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        上下文状态机                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                              ┌─────────┐                                    │
│                              │ CREATED │                                    │
│                              │ (创建)   │                                    │
│                              └────┬────┘                                    │
│                                   │ initialize()                            │
│                                   ▼                                         │
│                              ┌─────────┐                                    │
│                         ┌───▶│ INITIALIZING │                              │
│                         │    │ (初始化中) │                                  │
│                         │    └────┬────┘                                    │
│                         │         │ onInitialized()                         │
│                         │         ▼                                         │
│                         │    ┌─────────┐                                    │
│                         │    │ ACTIVE  │                                    │
│                    error│    │ (活跃)   │◄─────────────────┐               │
│                         │    └────┬────┘                  │               │
│                         │         │                        │ resume()      │
│                         │         │ suspend()              │               │
│                         │         ▼                        │               │
│                         │    ┌─────────┐                   │               │
│                         └───▶│SUSPENDED│───────────────────┘               │
│                              │ (挂起)   │                                   │
│                              └────┬────┘                                   │
│                                   │ transfer()                              │
│                                   ▼                                         │
│                              ┌─────────┐                                    │
│                              │TRANSFERRING│                                 │
│                              │(传输中)  │                                   │
│                              └────┬────┘                                    │
│                                   │ onTransferred()                         │
│                                   ▼                                         │
│                              ┌─────────┐                                    │
│                              │TRANSFERRED│                                  │
│                              │(已传输)  │                                   │
│                              └────┬────┘                                    │
│                                   │ destroy()                               │
│                                   ▼                                         │
│                              ┌─────────┐                                    │
│                              │ DESTROYED│                                   │
│                              │ (已销毁) │                                   │
│                              └─────────┘                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 场景上下文初始化器

```java
/**
 * 场景上下文初始化器接口
 */
public interface SceneContextInitializer {
    
    /**
     * 初始化 LLM 场景上下文
     * 
     * @param sceneId 场景ID
     * @param request 初始化请求
     * @return 初始化后的上下文
     */
    LlmSceneContext initialize(String sceneId, SceneContextInitializeRequest request);
    
    /**
     * 从现有会话恢复上下文
     * 
     * @param contextId 上下文ID
     * @return 恢复的上下文
     */
    LlmSceneContext restore(String contextId);
    
    /**
     * 序列化上下文（用于 A2A 传递）
     * 
     * @param context 上下文
     * @return 序列化后的字符串
     */
    String serialize(LlmSceneContext context);
    
    /**
     * 反序列化上下文
     * 
     * @param serialized 序列化字符串
     * @return 反序列化后的上下文
     */
    LlmSceneContext deserialize(String serialized);
    
    /**
     * 销毁上下文
     * 
     * @param contextId 上下文ID
     */
    void destroy(String contextId);
}

/**
 * 场景上下文初始化请求
 */
@Data
@Builder
public class SceneContextInitializeRequest {
    
    // 用户信息
    private String userId;
    private String userName;
    private String domainId;
    private String token;
    private String sessionId;
    private List<String> roles;
    
    // 场景信息
    private String sceneType;
    private String componentType;
    private String moduleViewType;
    private Object moduleConfig;
    
    // 知识库信息
    private String knowledgeBaseId;
    private List<String> accessibleKnowledgeBases;
    
    // 安全信息
    private String securityLevel;
    private boolean auditEnabled;
    
    // 扩展参数
    private Map<String, Object> extraParams;
}

/**
 * 场景上下文初始化器实现
 */
@Component
public class SceneContextInitializerImpl implements SceneContextInitializer {
    
    @Autowired
    private LlmContextRegistry contextRegistry;
    
    @Autowired
    private NlpContextManager nlpContextManager;
    
    @Autowired
    private KnowledgeContextBuilder knowledgeContextBuilder;
    
    @Autowired
    private ToolContextBuilder toolContextBuilder;
    
    @Autowired
    private SecurityContextBuilder securityContextBuilder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public LlmSceneContext initialize(String sceneId, SceneContextInitializeRequest request) {
        String contextId = generateContextId();
        long now = System.currentTimeMillis();
        
        LlmSceneContext.LlmSceneContextBuilder builder = LlmSceneContext.builder()
            .contextId(contextId)
            .sceneId(sceneId)
            .userId(request.getUserId())
            .sessionId(request.getSessionId())
            .status(ContextStatus.CREATED)
            .createdAt(now)
            .lastAccessedAt(now)
            .expiresAt(now + DEFAULT_TTL)
            .extendedAttributes(new HashMap<>());
        
        // 构建场景上下文
        builder.sceneContext(buildSceneContext(sceneId, request));
        
        // 构建 NLP 上下文
        builder.nlpContext(nlpContextManager.initializeNlpContext(
            request.getComponentType(),
            request.getModuleViewType(),
            request.getModuleConfig()
        ));
        
        // 构建知识上下文
        builder.knowledgeContext(knowledgeContextBuilder.build(request));
        
        // 构建工具上下文
        builder.toolContext(toolContextBuilder.build(sceneId, request.getRoles()));
        
        // 构建安全上下文
        builder.securityContext(securityContextBuilder.build(request));
        
        LlmSceneContext context = builder.build();
        
        // 注册上下文
        contextRegistry.register(context);
        
        // 更新状态
        context.setStatus(ContextStatus.ACTIVE);
        contextRegistry.update(context);
        
        return context;
    }
    
    @Override
    public LlmSceneContext restore(String contextId) {
        LlmSceneContext context = contextRegistry.get(contextId);
        if (context == null) {
            throw new ContextNotFoundException("Context not found: " + contextId);
        }
        
        if (context.isExpired()) {
            throw new ContextExpiredException("Context expired: " + contextId);
        }
        
        context.touch();
        contextRegistry.update(context);
        
        return context;
    }
    
    @Override
    public String serialize(LlmSceneContext context) {
        try {
            return objectMapper.writeValueAsString(context);
        } catch (Exception e) {
            throw new ContextSerializationException("Failed to serialize context", e);
        }
    }
    
    @Override
    public LlmSceneContext deserialize(String serialized) {
        try {
            return objectMapper.readValue(serialized, LlmSceneContext.class);
        } catch (Exception e) {
            throw new ContextDeserializationException("Failed to deserialize context", e);
        }
    }
    
    @Override
    public void destroy(String contextId) {
        LlmSceneContext context = contextRegistry.get(contextId);
        if (context != null) {
            context.setStatus(ContextStatus.DESTROYED);
            contextRegistry.remove(contextId);
        }
    }
    
    private SceneContext buildSceneContext(String sceneId, SceneContextInitializeRequest request) {
        return SceneContext.builder()
            .sceneType(request.getSceneType())
            .roles(request.getRoles())
            .sceneConfig(request.getExtraParams())
            .runtimeData(new HashMap<>())
            .activatedSkills(new ArrayList<>())
            .availableSkills(new ArrayList<>())
            .build();
    }
    
    private String generateContextId() {
        return "ctx-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private static final long DEFAULT_TTL = 30 * 60 * 1000; // 30分钟
}
```

### 3.3 NLP 上下文管理器

```java
/**
 * NLP 上下文管理器接口
 */
public interface NlpContextManager {
    
    /**
     * 初始化 NLP 上下文
     */
    NlpContext initializeNlpContext(String componentType, String moduleViewType, Object config);
    
    /**
     * 注册组件上下文
     */
    void registerComponentContext(String nlpContextId, NlpComponentContext componentContext);
    
    /**
     * 获取组件上下文
     */
    NlpComponentContext getComponentContext(String nlpContextId, String componentId);
    
    /**
     * 更新活跃组件
     */
    void setActiveComponent(String nlpContextId, String componentId);
    
    /**
     * 获取当前活跃组件
     */
    NlpComponentContext getActiveComponent(String nlpContextId);
    
    /**
     * 设置表达式变量
     */
    void setExpressionVariable(String nlpContextId, String name, Object value);
    
    /**
     * 解析表达式
     */
    Object evaluateExpression(String nlpContextId, String expression);
    
    /**
     * 获取 NLP 上下文
     */
    NlpContext getNlpContext(String nlpContextId);
    
    /**
     * 更新 NLP 上下文
     */
    void updateNlpContext(String nlpContextId, NlpContext nlpContext);
}

/**
 * NLP 上下文管理器实现
 */
@Component
public class NlpContextManagerImpl implements NlpContextManager {
    
    private final ConcurrentHashMap<String, NlpContext> nlpContexts = new ConcurrentHashMap<>();
    
    @Autowired
    private NlpComponentFactory componentFactory;
    
    @Autowired
    private ExpressionEvaluator expressionEvaluator;
    
    @Override
    public NlpContext initializeNlpContext(String componentType, String moduleViewType, Object config) {
        String nlpContextId = generateNlpContextId();
        
        ModuleViewType viewType = moduleViewType != null 
            ? ModuleViewType.valueOf(moduleViewType) 
            : inferModuleViewType(componentType);
        
        NlpContext.NlpContextBuilder builder = NlpContext.builder()
            .nlpContextId(nlpContextId)
            .componentType(componentType)
            .moduleViewType(viewType)
            .componentContexts(new HashMap<>())
            .activeComponentIds(new ArrayList<>())
            .expressionVariables(new HashMap<>());
        
        if (config != null) {
            NlpComponentContext componentContext = componentFactory.createFromConfig(config, viewType);
            builder.moduleMeta(componentContext.getModuleMeta())
                   .dataMeta(componentContext.getDataMeta());
            
            NlpContext nlpContext = builder.build();
            nlpContexts.put(nlpContextId, nlpContext);
            
            registerComponentContext(nlpContextId, componentContext);
            setActiveComponent(nlpContextId, componentContext.getComponentId());
            
            return nlpContext;
        }
        
        NlpContext nlpContext = builder.build();
        nlpContexts.put(nlpContextId, nlpContext);
        return nlpContext;
    }
    
    @Override
    public void registerComponentContext(String nlpContextId, NlpComponentContext componentContext) {
        NlpContext nlpContext = nlpContexts.get(nlpContextId);
        if (nlpContext != null) {
            nlpContext.getComponentContexts().put(
                componentContext.getComponentId(), 
                componentContext
            );
        }
    }
    
    @Override
    public NlpComponentContext getComponentContext(String nlpContextId, String componentId) {
        NlpContext nlpContext = nlpContexts.get(nlpContextId);
        if (nlpContext != null) {
            return nlpContext.getComponentContexts().get(componentId);
        }
        return null;
    }
    
    @Override
    public void setActiveComponent(String nlpContextId, String componentId) {
        NlpContext nlpContext = nlpContexts.get(nlpContextId);
        if (nlpContext != null) {
            List<String> activeIds = nlpContext.getActiveComponentIds();
            activeIds.remove(componentId);
            activeIds.add(0, componentId);
            
            Map<String, NlpComponentContext> contexts = nlpContext.getComponentContexts();
            for (NlpComponentContext ctx : contexts.values()) {
                ctx.setActive(ctx.getComponentId().equals(componentId));
            }
        }
    }
    
    @Override
    public NlpComponentContext getActiveComponent(String nlpContextId) {
        NlpContext nlpContext = nlpContexts.get(nlpContextId);
        if (nlpContext != null && !nlpContext.getActiveComponentIds().isEmpty()) {
            String activeId = nlpContext.getActiveComponentIds().get(0);
            return nlpContext.getComponentContexts().get(activeId);
        }
        return null;
    }
    
    @Override
    public void setExpressionVariable(String nlpContextId, String name, Object value) {
        NlpContext nlpContext = nlpContexts.get(nlpContextId);
        if (nlpContext != null) {
            nlpContext.getExpressionVariables().put(name, value);
        }
    }
    
    @Override
    public Object evaluateExpression(String nlpContextId, String expression) {
        NlpContext nlpContext = nlpContexts.get(nlpContextId);
        if (nlpContext != null) {
            return expressionEvaluator.evaluate(
                expression, 
                nlpContext.getExpressionVariables()
            );
        }
        return null;
    }
    
    @Override
    public NlpContext getNlpContext(String nlpContextId) {
        return nlpContexts.get(nlpContextId);
    }
    
    @Override
    public void updateNlpContext(String nlpContextId, NlpContext nlpContext) {
        nlpContexts.put(nlpContextId, nlpContext);
    }
    
    private String generateNlpContextId() {
        return "nlp-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private ModuleViewType inferModuleViewType(String componentType) {
        if (componentType == null) {
            return ModuleViewType.LAYOUTCONFIG;
        }
        
        String type = componentType.toUpperCase();
        if (type.contains("FORM")) {
            return ModuleViewType.FORMCONFIG;
        } else if (type.contains("GRID")) {
            return ModuleViewType.GRIDCONFIG;
        } else if (type.contains("TREE")) {
            return ModuleViewType.TREECONFIG;
        } else if (type.contains("GALLERY")) {
            return ModuleViewType.GALLERYCONFIG;
        } else if (type.contains("BLOCK")) {
            return ModuleViewType.BLOCKCONFIG;
        } else if (type.contains("DIV")) {
            return ModuleViewType.DIVCONFIG;
        } else if (type.contains("GROUP")) {
            return ModuleViewType.GROUPCONFIG;
        } else if (type.contains("PANEL")) {
            return ModuleViewType.PANELCONFIG;
        }
        return ModuleViewType.LAYOUTCONFIG;
    }
}
```

---

## 四、接口设计

### 4.1 LLM 场景服务接口

```java
/**
 * LLM 场景服务接口
 * 提供 LLM 与场景技能的交互能力
 */
public interface LlmSceneService {
    
    // ========== 场景上下文管理 ==========
    
    /**
     * 创建场景上下文
     */
    LlmSceneContext createSceneContext(String sceneId, SceneContextInitializeRequest request);
    
    /**
     * 获取场景上下文
     */
    LlmSceneContext getSceneContext(String contextId);
    
    /**
     * 更新场景上下文
     */
    LlmSceneContext updateSceneContext(String contextId, Map<String, Object> updates);
    
    /**
     * 销毁场景上下文
     */
    void destroySceneContext(String contextId);
    
    // ========== LLM 交互 ==========
    
    /**
     * 发送消息到 LLM
     */
    LlmResponse chat(String contextId, String message);
    
    /**
     * 流式对话
     */
    void chatStream(String contextId, String message, StreamHandler handler);
    
    /**
     * 带工具调用的对话
     */
    LlmResponse chatWithTools(String contextId, String message, List<ToolDefinition> tools);
    
    /**
     * 执行技能
     */
    SkillExecutionResult executeSkill(String contextId, String skillId, Map<String, Object> params);
    
    // ========== 跨场景交互 ==========
    
    /**
     * 跨场景调用
     */
    CrossSceneResult interactWithScene(String sourceContextId, String targetSceneId, CrossSceneRequest request);
    
    /**
     * 传递上下文到另一个场景
     */
    String transferContext(String sourceContextId, String targetSceneId, ContextTransferRequest request);
}

/**
 * LLM 响应
 */
@Data
@Builder
public class LlmResponse {
    
    private String responseId;
    private String contextId;
    
    private String content;                 // 响应内容
    private List<ToolCall> toolCalls;       // 工具调用
    private FinishReason finishReason;      // 结束原因
    
    private TokenUsage tokenUsage;          // Token 使用量
    private long latency;                   // 响应延迟
    
    private Map<String, Object> metadata;   // 元数据
}

/**
 * 技能执行结果
 */
@Data
@Builder
public class SkillExecutionResult {
    
    private String resultId;
    private String skillId;
    private String contextId;
    
    private boolean success;
    private Object result;
    private String errorMessage;
    
    private long executionTime;
    private List<String> executedSteps;
}

/**
 * 跨场景结果
 */
@Data
@Builder
public class CrossSceneResult {
    
    private String resultId;
    private String sourceContextId;
    private String targetContextId;
    
    private boolean success;
    private Object result;
    private String errorMessage;
    
    private ContextTransferInfo contextTransferInfo;
}
```

### 4.2 A2A 上下文传递接口

```java
/**
 * 上下文传递处理器接口
 */
public interface ContextTransferHandler {
    
    /**
     * 准备上下文传递
     */
    ContextTransfer prepareTransfer(
        LlmSceneContext sourceContext, 
        TransferMode mode,
        Set<ContextPart> includedParts
    );
    
    /**
     * 接收上下文传递
     */
    LlmSceneContext receiveTransfer(ContextTransfer transfer, String targetSceneId);
    
    /**
     * 合并上下文
     */
    void mergeContext(LlmSceneContext target, LlmSceneContext source, MergeStrategy strategy);
}

/**
 * 上下文传递
 */
@Data
@Builder
public class ContextTransfer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sourceContextId;
    private String targetContextId;
    private TransferMode transferMode;
    
    private String serializedContext;       // 序列化的上下文数据
    private ContextReference contextReference;  // 上下文引用
    private Map<String, Object> contextDelta;   // 上下文增量
    
    private Set<ContextPart> includedParts; // 包含的部分
    private Set<ContextPart> excludedParts; // 排除的部分
    
    private long createdAt;
    private long expiresAt;
}

/**
 * 上下文引用
 */
@Data
@Builder
public class ContextReference implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String contextId;
    private String sceneId;
    private String agentId;
    private long createdAt;
    private String checksum;
}

/**
 * 传递模式
 */
public enum TransferMode {
    FULL,       // 完整传递
    REFERENCE,  // 引用传递
    DELTA,      // 增量传递
    SELECTIVE   // 选择性传递
}

/**
 * 上下文部分
 */
public enum ContextPart {
    SCENE_CONTEXT,      // 场景上下文
    NLP_CONTEXT,        // NLP 上下文
    KNOWLEDGE_CONTEXT,  // 知识上下文
    TOOL_CONTEXT,       // 工具上下文
    SECURITY_CONTEXT,   // 安全上下文
    EXTENDED_ATTRIBUTES // 扩展属性
}

/**
 * 合并策略
 */
public enum MergeStrategy {
    SOURCE_PRIORITY,    // 源优先
    TARGET_PRIORITY,    // 目标优先
    DEEP_MERGE          // 深度合并
}
```

---

## 五、交互流程设计

### 5.1 场景初始化流程

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    User     │────▶│   Scene     │────▶│    LLM      │────▶│   Context   │
│   Request   │     │   Engine    │     │   Service   │     │   Registry  │
└─────────────┘     └──────┬──────┘     └──────┬──────┘     └─────────────┘
                           │                    │
                           │ 1. createScene()   │
                           │───────────────────▶│
                           │                    │
                           │ 2. initializeContext()
                           │───────────────────▶│
                           │                    │
                           │ 3. buildSceneContext
                           │ 4. buildNlpContext
                           │ 5. buildKnowledgeContext
                           │ 6. buildToolContext
                           │ 7. buildSecurityContext
                           │                    │
                           │ 8. register(context)
                           │───────────────────▶│
                           │                    │
                           │ 9. return contextId
                           │◀───────────────────│
                           │                    │
                           │ 10. return SceneHandle
                           │◀───────────────────│
                           │                    │
┌─────────────┐     ┌──────┴──────┐     ┌──────┴──────┐     ┌─────────────┐
│    User     │◀────│   Scene     │◀────│    LLM      │     │   Context   │
│  Response   │     │   Engine    │     │   Service   │     │   Registry  │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
```

### 5.2 LLM 对话流程

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    User     │────▶│    LLM      │────▶│   Context   │────▶│   LLM       │
│   Message   │     │   Service   │     │   Manager   │     │   Provider  │
└─────────────┘     └──────┬──────┘     └──────┬──────┘     └──────┬──────┘
                           │                    │                    │
                           │ 1. chat(contextId, message)
                           │───────────────────▶│
                           │                    │
                           │ 2. getContext(contextId)
                           │───────────────────▶│
                           │                    │
                           │ 3. return context
                           │◀───────────────────│
                           │                    │
                           │ 4. augmentPrompt(context, message)
                           │                    │
                           │ 5. call LLM API
                           │───────────────────────────────────────▶│
                           │                    │                    │
                           │ 6. return response
                           │◀───────────────────────────────────────│
                           │                    │
                           │ 7. updateContext(context)
                           │───────────────────▶│
                           │                    │
                           │ 8. return LlmResponse
                           │◀───────────────────│
                           │                    │
┌─────────────┐     ┌──────┴──────┐     ┌──────┴──────┐     ┌─────────────┐
│    User     │◀────│    LLM      │     │   Context   │     │   LLM       │
│  Response   │     │   Service   │     │   Manager   │     │   Provider  │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
```

### 5.3 跨场景交互流程

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Scene A   │────▶│    LLM      │────▶│    A2A      │────▶│   Scene B   │
│   (Source)  │     │   Service   │     │   Router    │     │   (Target)  │
└─────────────┘     └──────┬──────┘     └──────┬──────┘     └──────┬──────┘
                           │                    │                    │
                           │ 1. interactWithScene()
                           │                    │
                           │ 2. prepareTransfer()
                           │ (serialize context)
                           │                    │
                           │ 3. build A2A Command
                           │    with ContextTransfer
                           │───────────────────▶│
                           │                    │
                           │ 4. route command
                           │───────────────────────────────────────▶│
                           │                    │                    │
                           │                    │ 5. receiveTransfer()
                           │                    │    (deserialize context)
                           │                    │                    │
                           │                    │ 6. mergeContext()
                           │                    │                    │
                           │                    │ 7. execute in Scene B
                           │                    │                    │
                           │                    │ 8. return result
                           │◀───────────────────────────────────────│
                           │                    │                    │
                           │ 9. return CrossSceneResult
                           │◀───────────────────│
                           │                    │
┌─────────────┐     ┌──────┴──────┐     ┌──────┴──────┐     ┌─────────────┐
│   Scene A   │◀────│    LLM      │◀────│    A2A      │     │   Scene B   │
│   (Source)  │     │   Service   │     │   Router    │     │   (Target)  │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
```

---

## 六、Engine 层协作需求

### 6.1 需要 Engine 提供的接口

| 接口 | 说明 | 优先级 |
|-----|------|--------|
| `SceneContextInitializer.initialize()` | 初始化场景上下文 | P0 |
| `SceneContextInitializer.serialize()` | 序列化上下文用于传递 | P0 |
| `NlpContextManager.initializeNlpContext()` | 初始化 NLP 上下文 | P0 |
| `NlpContextManager.registerComponentContext()` | 注册组件上下文 | P0 |
| `LlmContextRegistry.register()` | 注册 LLM 上下文 | P0 |
| `LlmContextRegistry.get()` | 获取 LLM 上下文 | P0 |
| `ContextTransferHandler.prepareTransfer()` | 准备上下文传递 | P1 |
| `ContextTransferHandler.receiveTransfer()` | 接收上下文传递 | P1 |

### 6.2 需要 Engine 实现的功能

| 功能 | 说明 | 优先级 |
|-----|------|--------|
| **场景上下文管理** | 创建、存储、更新、销毁场景上下文 | P0 |
| **NLP 上下文管理** | 管理 NLP 组件上下文和表达式求值 | P0 |
| **上下文序列化** | 支持上下文的序列化和反序列化 | P0 |
| **上下文传递** | 支持 A2A 协议的上下文传递 | P1 |
| **上下文合并** | 支持跨场景上下文合并 | P1 |
| **上下文过期管理** | 自动清理过期上下文 | P2 |

---

## 七、总结

### 7.1 核心设计要点

1. **分层架构**: 应用层、场景引擎层、LLM 集成层、A2A 协议层职责清晰
2. **上下文驱动**: 所有交互基于统一的 `LlmSceneContext` 上下文
3. **模块化设计**: 场景上下文、NLP 上下文、知识上下文、工具上下文独立管理
4. **协议支持**: A2A 协议支持上下文传递和跨场景交互

### 7.2 实施建议

**Phase 1 (Week 1-2): 核心上下文**
- 实现 `LlmSceneContext` 数据结构
- 实现 `SceneContextInitializer`
- 实现 `LlmContextRegistry`

**Phase 2 (Week 3-4): NLP 上下文**
- 实现 `NlpContextManager`
- 实现组件上下文管理
- 实现表达式求值

**Phase 3 (Week 5-6): 上下文传递**
- 实现 `ContextTransferHandler`
- 集成 A2A 协议
- 实现跨场景交互

**Phase 4 (Week 7-8): 优化完善**
- 性能优化
- 监控告警
- 文档完善

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-09
