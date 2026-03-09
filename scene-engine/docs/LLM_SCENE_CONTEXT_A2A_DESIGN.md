# LLM 场景上下文与 A2A 交互设计

**版本**: v2.4.0  
**日期**: 2026-03-07  
**状态**: 架构设计

---

## 一、设计概述

### 1.1 核心需求

| 需求 | 说明 |
|------|------|
| **场景上下文初始化** | Engine 完成 LLM 场景上下文的初始化工作 |
| **NLP 上下文管理** | 场景定义支持 NLP 管理上下文功能 |
| **A2A 上下文传递** | A2A 命令协议支持上下文传递 |
| **LLM 间交互** | LLM-A 与 LLM-B 能完成信息交互 |

### 1.2 架构概览

```
┌─────────────────────────────────────────────────────────────────────────┐
│  LLM 场景上下文与 A2A 交互架构                                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Scene Engine (场景引擎)                                         │   │
│  │  ├── SceneContextInitializer (场景上下文初始化器)                 │   │
│  │  ├── NlpContextManager (NLP 上下文管理器)                        │   │
│  │  └── LlmContextRegistry (LLM 上下文注册中心)                     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  LLM Sandbox A (LLM-A)                                          │   │
│  │  ├── LlmSceneContext (场景上下文)                                │   │
│  │  ├── NlpComponentContext (NLP 组件上下文)                        │   │
│  │  └── ConversationMemory (对话记忆)                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    │ A2A Command (Context Transfer)     │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  LLM Sandbox B (LLM-B)                                          │   │
│  │  ├── LlmSceneContext (场景上下文)                                │   │
│  │  ├── NlpComponentContext (NLP 组件上下文)                        │   │
│  │  └── ConversationMemory (对话记忆)                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 二、LLM 场景上下文初始化

### 2.1 上下文层次结构

```
┌─────────────────────────────────────────────────────────────────────────┐
│  LLM 场景上下文层次结构                                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  LlmSceneContext (LLM 场景上下文 - 顶层)                          │   │
│  │  ├── sceneId: String                                             │   │
│  │  ├── agentId: String                                             │   │
│  │  ├── userId: String                                              │   │
│  │  ├── sessionId: String                                           │   │
│  │  └── createdAt: long                                             │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│          ┌─────────────────────────┼─────────────────────────┐          │
│          ▼                         ▼                         ▼          │
│  ┌───────────────┐     ┌───────────────────┐     ┌───────────────┐     │
│  │ UserContext   │     │ NlpContext        │     │ KnowledgeCtx  │     │
│  │ (用户上下文)   │     │ (NLP 上下文)       │     │ (知识上下文)   │     │
│  └───────────────┘     └───────────────────┘     └───────────────┘     │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  NlpComponentContext (NLP 组件上下文)                            │   │
│  │  ├── moduleMeta: CustomModuleMeta                                │   │
│  │  ├── dataMeta: CustomDataMeta                                    │   │
│  │  ├── componentType: String                                       │   │
│  │  └── viewConfig: ModuleViewMeta                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心接口定义

```java
/**
 * LLM 场景上下文
 * 封装 LLM 在特定场景中所需的所有上下文信息
 */
@Data
@Builder
public class LlmSceneContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String contextId;
    private String sceneId;
    private String agentId;
    private String sandboxId;
    
    private UserContext userContext;
    private NlpContext nlpContext;
    private KnowledgeContext knowledgeContext;
    private SecurityContext securityContext;
    
    private Map<String, Object> extendedAttributes;
    private long createdAt;
    private long lastAccessedAt;
    
    public void touch() {
        this.lastAccessedAt = System.currentTimeMillis();
    }
    
    public Object getExtendedAttribute(String key) {
        return extendedAttributes != null ? extendedAttributes.get(key) : null;
    }
    
    public void setExtendedAttribute(String key, Object value) {
        if (extendedAttributes == null) {
            extendedAttributes = new HashMap<>();
        }
        extendedAttributes.put(key, value);
    }
}

/**
 * 用户上下文
 */
@Data
@Builder
public class UserContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String userName;
    private String domainId;
    private String token;
    private List<String> roles;
    private List<String> permissions;
    private boolean isLlmUser;
    private String llmUserId;
    private String llmToken;
}

/**
 * NLP 上下文
 */
@Data
@Builder
public class NlpContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String nlpContextId;
    private String componentType;
    private ModuleViewType moduleViewType;
    private PanelType panelType;
    
    private CustomModuleMeta moduleMeta;
    private CustomDataMeta dataMeta;
    private ModuleViewMeta viewConfig;
    
    private Map<String, NlpComponentContext> componentContexts;
    private List<String> activeComponentIds;
    
    private String currentExpression;
    private Map<String, Object> expressionVariables;
}

/**
 * 知识上下文
 */
@Data
@Builder
public class KnowledgeContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String knowledgeBaseId;
    private String knowledgeBaseType;
    private List<String> accessibleKnowledgeBases;
    private Map<String, Object> searchFilters;
    private int maxResults;
    private float similarityThreshold;
}

/**
 * 安全上下文
 */
@Data
@Builder
public class SecurityContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String securityLevel;
    private String sessionId;
    private String traceId;
    private boolean auditEnabled;
    private List<String> allowedOperations;
    private Map<String, String> securityLabels;
}
```

### 2.3 场景上下文初始化器

```java
/**
 * 场景上下文初始化器接口
 */
public interface SceneContextInitializer {
    
    /**
     * 初始化 LLM 场景上下文
     */
    LlmSceneContext initialize(String sceneId, InitializeRequest request);
    
    /**
     * 从现有会话恢复上下文
     */
    LlmSceneContext restore(String contextId);
    
    /**
     * 序列化上下文（用于 A2A 传递）
     */
    String serialize(LlmSceneContext context);
    
    /**
     * 反序列化上下文
     */
    LlmSceneContext deserialize(String serialized);
}

/**
 * 初始化请求
 */
@Data
@Builder
public class InitializeRequest {
    
    private String userId;
    private String userName;
    private String domainId;
    private String token;
    private String sessionId;
    
    private String componentType;
    private String moduleViewType;
    private Object moduleConfig;
    
    private String knowledgeBaseId;
    private List<String> accessibleKnowledgeBases;
    
    private String securityLevel;
    private boolean auditEnabled;
    
    private Map<String, Object> extraParams;
}

/**
 * 场景上下文初始化器实现
 */
@Component
public class SceneContextInitializerImpl implements SceneContextInitializer {
    
    private final LlmContextRegistry contextRegistry;
    private final NlpContextManager nlpContextManager;
    private final KnowledgeContextBuilder knowledgeContextBuilder;
    private final SecurityContextBuilder securityContextBuilder;
    
    @Override
    public LlmSceneContext initialize(String sceneId, InitializeRequest request) {
        String contextId = generateContextId();
        
        LlmSceneContext.LlmSceneContextBuilder builder = LlmSceneContext.builder()
            .contextId(contextId)
            .sceneId(sceneId)
            .createdAt(System.currentTimeMillis())
            .lastAccessedAt(System.currentTimeMillis());
        
        builder.userContext(buildUserContext(request));
        
        builder.nlpContext(nlpContextManager.initializeNlpContext(
            request.getComponentType(),
            request.getModuleViewType(),
            request.getModuleConfig()
        ));
        
        builder.knowledgeContext(knowledgeContextBuilder.build(request));
        
        builder.securityContext(securityContextBuilder.build(request));
        
        builder.extendedAttributes(new HashMap<>());
        
        LlmSceneContext context = builder.build();
        
        contextRegistry.register(context);
        
        return context;
    }
    
    @Override
    public LlmSceneContext restore(String contextId) {
        return contextRegistry.get(contextId);
    }
    
    @Override
    public String serialize(LlmSceneContext context) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(context);
        } catch (Exception e) {
            throw new ContextSerializationException("Failed to serialize context", e);
        }
    }
    
    @Override
    public LlmSceneContext deserialize(String serialized) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(serialized, LlmSceneContext.class);
        } catch (Exception e) {
            throw new ContextDeserializationException("Failed to deserialize context", e);
        }
    }
    
    private UserContext buildUserContext(InitializeRequest request) {
        return UserContext.builder()
            .userId(request.getUserId())
            .userName(request.getUserName())
            .domainId(request.getDomainId())
            .token(request.getToken())
            .sessionId(request.getSessionId())
            .build();
    }
    
    private String generateContextId() {
        return "ctx-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
```

### 2.4 LLM 上下文注册中心

```java
/**
 * LLM 上下文注册中心
 */
@Component
public class LlmContextRegistry {
    
    private final ConcurrentHashMap<String, LlmSceneContext> contexts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sceneToContext = new ConcurrentHashMap<>();
    
    private final ContextEvictionPolicy evictionPolicy;
    
    public void register(LlmSceneContext context) {
        contexts.put(context.getContextId(), context);
        sceneToContext.put(context.getSceneId(), context.getContextId());
    }
    
    public LlmSceneContext get(String contextId) {
        LlmSceneContext context = contexts.get(contextId);
        if (context != null) {
            context.touch();
        }
        return context;
    }
    
    public LlmSceneContext getBySceneId(String sceneId) {
        String contextId = sceneToContext.get(sceneId);
        return contextId != null ? get(contextId) : null;
    }
    
    public void remove(String contextId) {
        LlmSceneContext context = contexts.remove(contextId);
        if (context != null) {
            sceneToContext.remove(context.getSceneId());
        }
    }
    
    public void update(LlmSceneContext context) {
        contexts.put(context.getContextId(), context);
    }
    
    public List<LlmSceneContext> getAllActive() {
        return contexts.values().stream()
            .filter(c -> !evictionPolicy.isExpired(c))
            .collect(Collectors.toList());
    }
    
    public void evictExpired() {
        contexts.entrySet().removeIf(entry -> {
            if (evictionPolicy.isExpired(entry.getValue())) {
                sceneToContext.remove(entry.getValue().getSceneId());
                return true;
            }
            return false;
        });
    }
}

/**
 * 上下文过期策略
 */
@Component
public class ContextEvictionPolicy {
    
    private static final long DEFAULT_TTL = 30 * 60 * 1000;
    private static final long DEFAULT_IDLE_TIMEOUT = 10 * 60 * 1000;
    
    public boolean isExpired(LlmSceneContext context) {
        long now = System.currentTimeMillis();
        long age = now - context.getCreatedAt();
        long idle = now - context.getLastAccessedAt();
        
        return age > DEFAULT_TTL || idle > DEFAULT_IDLE_TIMEOUT;
    }
}
```

---

## 三、NLP 上下文管理功能

### 3.1 NLP 上下文管理器

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
}

/**
 * NLP 组件上下文
 */
@Data
@Builder
public class NlpComponentContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String componentId;
    private String componentType;
    private ModuleViewType moduleViewType;
    
    private CustomModuleMeta moduleMeta;
    private CustomDataMeta dataMeta;
    
    private Map<String, Object> properties;
    private Map<String, Object> bindings;
    private List<String> eventHandlers;
    
    private boolean active;
    private long lastModified;
    
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }
    
    public void setProperty(String key, Object value) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(key, value);
        this.lastModified = System.currentTimeMillis();
    }
}

/**
 * NLP 上下文管理器实现
 */
@Component
public class NlpContextManagerImpl implements NlpContextManager {
    
    private final ConcurrentHashMap<String, NlpContext> nlpContexts = new ConcurrentHashMap<>();
    private final NlpComponentFactory componentFactory;
    private final ExpressionEvaluator expressionEvaluator;
    
    @Override
    public NlpContext initializeNlpContext(String componentType, String moduleViewType, Object config) {
        String nlpContextId = "nlp-" + UUID.randomUUID().toString().substring(0, 8);
        
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

### 3.2 场景定义注解支持

```java
/**
 * NLP 上下文配置注解
 * 用于场景定义中配置 NLP 上下文管理
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NlpContextConfig {
    
    /**
     * 组件类型
     */
    String componentType() default "";
    
    /**
     * 模块视图类型
     */
    ModuleViewType moduleViewType() default ModuleViewType.LAYOUTCONFIG;
    
    /**
     * 是否启用表达式求值
     */
    boolean enableExpression() default true;
    
    /**
     * 是否启用组件追踪
     */
    boolean enableComponentTracking() default true;
    
    /**
     * 最大组件数量
     */
    int maxComponents() default 100;
    
    /**
     * 上下文变量定义
     */
    ContextVariable[] variables() default {};
}

/**
 * 上下文变量定义
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextVariable {
    
    String name();
    
    String type() default "java.lang.String";
    
    String defaultValue() default "";
    
    String description() default "";
}

/**
 * NLP 组件绑定注解
 * 用于将组件绑定到 NLP 上下文
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NlpComponentBinding {
    
    /**
     * 组件ID
     */
    String componentId();
    
    /**
     * 组件类型
     */
    String componentType();
    
    /**
     * 是否为主组件
     */
    boolean primary() default false;
    
    /**
     * 数据绑定表达式
     */
    String dataBinding() default "";
}

/**
 * 使用示例
 */
@NlpContextConfig(
    componentType = "RecruitmentForm",
    moduleViewType = ModuleViewType.FORMCONFIG,
    enableExpression = true,
    enableComponentTracking = true,
    variables = {
        @ContextVariable(name = "candidateId", type = "java.lang.String", description = "候选人ID"),
        @ContextVariable(name = "positionId", type = "java.lang.String", description = "职位ID"),
        @ContextVariable(name = "approvalStatus", type = "java.lang.String", defaultValue = "PENDING")
    }
)
public class RecruitmentScene {
    
    @NlpComponentBinding(
        componentId = "candidate-form",
        componentType = "ClassForm",
        primary = true,
        dataBinding = "#{candidateService.getCandidate(candidateId)}"
    )
    public NlpComponentContext initCandidateForm(NlpContext nlpContext) {
        return NlpComponentContext.builder()
            .componentId("candidate-form")
            .componentType("ClassForm")
            .moduleViewType(ModuleViewType.FORMCONFIG)
            .build();
    }
}
```

---

## 四、A2A 命令协议上下文传递扩展

### 4.1 扩展命令消息结构

```java
/**
 * A2A 命令消息 (扩展版)
 */
@Data
@Builder
public class A2ACommand implements Serializable {
    
    private static final long serialVersionUID = 2L;
    
    private CommandHeader header;
    private CommandBody body;
    private CommandMetadata metadata;
    private SecurityInfo security;
    
    /**
     * 新增：上下文传递
     */
    private ContextTransfer contextTransfer;
    
    @Data
    @Builder
    public static class ContextTransfer implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 源上下文ID
         */
        private String sourceContextId;
        
        /**
         * 目标上下文ID（可选，用于指定接收方使用的上下文）
         */
        private String targetContextId;
        
        /**
         * 上下文传递模式
         */
        private TransferMode transferMode;
        
        /**
         * 序列化的上下文数据（完整传递时使用）
         */
        private String serializedContext;
        
        /**
         * 上下文引用（引用传递时使用）
         */
        private ContextReference contextReference;
        
        /**
         * 上下文增量更新
         */
        private Map<String, Object> contextDelta;
        
        /**
         * 需要传递的上下文部分
         */
        private Set<ContextPart> includedParts;
        
        /**
         * 需要排除的上下文部分
         */
        private Set<ContextPart> excludedParts;
        
        public enum TransferMode {
            FULL,
            REFERENCE,
            DELTA,
            SELECTIVE
        }
        
        public enum ContextPart {
            USER_CONTEXT,
            NLP_CONTEXT,
            KNOWLEDGE_CONTEXT,
            SECURITY_CONTEXT,
            EXTENDED_ATTRIBUTES,
            CONVERSATION_MEMORY
        }
    }
    
    @Data
    @Builder
    public static class ContextReference implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private String contextId;
        private String sceneId;
        private String agentId;
        private long createdAt;
        private String checksum;
    }
}
```

### 4.2 上下文传递处理器

```java
/**
 * 上下文传递处理器接口
 */
public interface ContextTransferHandler {
    
    /**
     * 准备上下文传递
     */
    A2ACommand.ContextTransfer prepareTransfer(
        LlmSceneContext sourceContext, 
        A2ACommand.ContextTransfer.TransferMode mode,
        Set<A2ACommand.ContextTransfer.ContextPart> includedParts
    );
    
    /**
     * 接收上下文传递
     */
    LlmSceneContext receiveTransfer(
        A2ACommand.ContextTransfer transfer, 
        String targetSceneId
    );
    
    /**
     * 合并上下文
     */
    void mergeContext(
        LlmSceneContext target, 
        LlmSceneContext source,
        MergeStrategy strategy
    );
}

/**
 * 上下文传递处理器实现
 */
@Component
public class ContextTransferHandlerImpl implements ContextTransferHandler {
    
    private final SceneContextInitializer contextInitializer;
    private final LlmContextRegistry contextRegistry;
    private final ObjectMapper objectMapper;
    
    @Override
    public A2ACommand.ContextTransfer prepareTransfer(
            LlmSceneContext sourceContext, 
            A2ACommand.ContextTransfer.TransferMode mode,
            Set<A2ACommand.ContextTransfer.ContextPart> includedParts) {
        
        A2ACommand.ContextTransfer.ContextTransferBuilder builder = 
            A2ACommand.ContextTransfer.builder()
                .sourceContextId(sourceContext.getContextId())
                .transferMode(mode);
        
        switch (mode) {
            case FULL:
                builder.serializedContext(serializeContext(sourceContext, includedParts));
                break;
                
            case REFERENCE:
                builder.contextReference(createReference(sourceContext));
                break;
                
            case DELTA:
                builder.contextDelta(extractDelta(sourceContext));
                break;
                
            case SELECTIVE:
                builder.serializedContext(serializeContext(sourceContext, includedParts))
                       .includedParts(includedParts);
                break;
        }
        
        return builder.build();
    }
    
    @Override
    public LlmSceneContext receiveTransfer(
            A2ACommand.ContextTransfer transfer, 
            String targetSceneId) {
        
        switch (transfer.getTransferMode()) {
            case FULL:
            case SELECTIVE:
                return deserializeAndRegister(transfer.getSerializedContext(), targetSceneId);
                
            case REFERENCE:
                return resolveReference(transfer.getContextReference());
                
            case DELTA:
                return applyDelta(transfer, targetSceneId);
                
            default:
                throw new IllegalArgumentException("Unknown transfer mode: " + transfer.getTransferMode());
        }
    }
    
    @Override
    public void mergeContext(
            LlmSceneContext target, 
            LlmSceneContext source,
            MergeStrategy strategy) {
        
        switch (strategy) {
            case SOURCE_PRIORITY:
                mergeWithSourcePriority(target, source);
                break;
            case TARGET_PRIORITY:
                mergeWithTargetPriority(target, source);
                break;
            case DEEP_MERGE:
                deepMerge(target, source);
                break;
        }
        
        contextRegistry.update(target);
    }
    
    private String serializeContext(LlmSceneContext context, Set<A2ACommand.ContextTransfer.ContextPart> includedParts) {
        try {
            LlmSceneContext filteredContext = filterContext(context, includedParts);
            return objectMapper.writeValueAsString(filteredContext);
        } catch (Exception e) {
            throw new ContextTransferException("Failed to serialize context", e);
        }
    }
    
    private LlmSceneContext filterContext(LlmSceneContext context, Set<A2ACommand.ContextTransfer.ContextPart> includedParts) {
        if (includedParts == null || includedParts.isEmpty()) {
            return context;
        }
        
        LlmSceneContext.LlmSceneContextBuilder builder = LlmSceneContext.builder()
            .contextId(context.getContextId())
            .sceneId(context.getSceneId())
            .agentId(context.getAgentId())
            .sandboxId(context.getSandboxId())
            .createdAt(context.getCreatedAt())
            .lastAccessedAt(System.currentTimeMillis());
        
        if (includedParts.contains(A2ACommand.ContextTransfer.ContextPart.USER_CONTEXT)) {
            builder.userContext(context.getUserContext());
        }
        if (includedParts.contains(A2ACommand.ContextTransfer.ContextPart.NLP_CONTEXT)) {
            builder.nlpContext(context.getNlpContext());
        }
        if (includedParts.contains(A2ACommand.ContextTransfer.ContextPart.KNOWLEDGE_CONTEXT)) {
            builder.knowledgeContext(context.getKnowledgeContext());
        }
        if (includedParts.contains(A2ACommand.ContextTransfer.ContextPart.SECURITY_CONTEXT)) {
            builder.securityContext(context.getSecurityContext());
        }
        if (includedParts.contains(A2ACommand.ContextTransfer.ContextPart.EXTENDED_ATTRIBUTES)) {
            builder.extendedAttributes(context.getExtendedAttributes());
        }
        
        return builder.build();
    }
    
    private A2ACommand.ContextReference createReference(LlmSceneContext context) {
        return A2ACommand.ContextReference.builder()
            .contextId(context.getContextId())
            .sceneId(context.getSceneId())
            .agentId(context.getAgentId())
            .createdAt(context.getCreatedAt())
            .checksum(computeChecksum(context))
            .build();
    }
    
    private LlmSceneContext resolveReference(A2ACommand.ContextReference reference) {
        LlmSceneContext context = contextRegistry.get(reference.getContextId());
        if (context == null) {
            throw new ContextNotFoundException("Context not found: " + reference.getContextId());
        }
        return context;
    }
    
    private Map<String, Object> extractDelta(LlmSceneContext context) {
        Map<String, Object> delta = new HashMap<>();
        if (context.getExtendedAttributes() != null) {
            delta.putAll(context.getExtendedAttributes());
        }
        return delta;
    }
    
    private LlmSceneContext deserializeAndRegister(String serialized, String targetSceneId) {
        try {
            LlmSceneContext context = objectMapper.readValue(serialized, LlmSceneContext.class);
            context.setSceneId(targetSceneId);
            context.setContextId(generateNewContextId());
            contextRegistry.register(context);
            return context;
        } catch (Exception e) {
            throw new ContextTransferException("Failed to deserialize context", e);
        }
    }
    
    private LlmSceneContext applyDelta(A2ACommand.ContextTransfer transfer, String targetSceneId) {
        LlmSceneContext targetContext = contextRegistry.getBySceneId(targetSceneId);
        if (targetContext == null) {
            throw new ContextNotFoundException("Target context not found for scene: " + targetSceneId);
        }
        
        Map<String, Object> delta = transfer.getContextDelta();
        if (delta != null) {
            delta.forEach((key, value) -> targetContext.setExtendedAttribute(key, value));
        }
        
        contextRegistry.update(targetContext);
        return targetContext;
    }
    
    private String computeChecksum(LlmSceneContext context) {
        return DigestUtils.md5Hex(context.getContextId() + context.getCreatedAt());
    }
    
    private String generateNewContextId() {
        return "ctx-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    public enum MergeStrategy {
        SOURCE_PRIORITY,
        TARGET_PRIORITY,
        DEEP_MERGE
    }
}
```

---

## 五、LLM-A 与 LLM-B 交互机制

### 5.1 LLM 间交互架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│  LLM-A 与 LLM-B 交互架构                                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌───────────────────────────────────────────────────────────────────┐ │
│  │  Scene A (招聘场景)                                                │ │
│  │  ┌─────────────────────────────────────────────────────────────┐ │ │
│  │  │  LLM Sandbox A (LLM-A)                                      │ │ │
│  │  │  ├── LlmSceneContext (场景上下文)                            │ │ │
│  │  │  │   ├── UserContext (用户: 张三)                           │ │ │
│  │  │  │   ├── NlpContext (表单组件)                              │ │ │
│  │  │  │   └── KnowledgeContext (招聘知识库)                      │ │ │
│  │  │  ├── ConversationMemory                                     │ │ │
│  │  │  └── LlmRuntime                                             │ │ │
│  │  └─────────────────────────────────────────────────────────────┘ │ │
│  └───────────────────────────────────────────────────────────────────┘ │
│                                    │                                    │
│                                    │ A2A Command                        │
│                                    │ (Context Transfer)                 │
│                                    ▼                                    │
│  ┌───────────────────────────────────────────────────────────────────┐ │
│  │  MCP Agent (主控智能体)                                            │ │
│  │  ├── 命令路由                                                      │ │
│  │  ├── 上下文验证                                                    │ │
│  │  ├── 安全检查                                                      │ │
│  │  └── 审计日志                                                      │ │
│  └───────────────────────────────────────────────────────────────────┘ │
│                                    │                                    │
│                                    ▼                                    │
│  ┌───────────────────────────────────────────────────────────────────┐ │
│  │  Scene B (审批场景)                                                │ │
│  │  ┌─────────────────────────────────────────────────────────────┐ │ │
│  │  │  LLM Sandbox B (LLM-B)                                      │ │ │
│  │  │  ├── LlmSceneContext (场景上下文)                            │ │ │
│  │  │  │   ├── UserContext (用户: 张三 + LLM-USER)                │ │ │
│  │  │  │   ├── NlpContext (审批组件)                              │ │ │
│  │  │  │   ├── KnowledgeContext (审批知识库)                      │ │ │
│  │  │  │   └── TransferredContext (来自 LLM-A)                    │ │ │
│  │  │  ├── ConversationMemory                                     │ │ │
│  │  │  └── LlmRuntime                                             │ │ │
│  │  └─────────────────────────────────────────────────────────────┘ │ │
│  └───────────────────────────────────────────────────────────────────┘ │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.2 LLM 间交互命令

```java
/**
 * LLM 间交互命令类型
 */
public enum LlmInteractionCommandType {
    
    LLM_HANDSHAKE("llm.handshake", "LLM 握手"),
    LLM_CONTEXT_SHARE("llm.context.share", "LLM 上下文共享"),
    LLM_CONTEXT_REQUEST("llm.context.request", "LLM 上下文请求"),
    LLM_DATA_EXCHANGE("llm.data.exchange", "LLM 数据交换"),
    LLM_TASK_DELEGATE("llm.task.delegate", "LLM 任务委托"),
    LLM_RESULT_RETURN("llm.result.return", "LLM 结果返回"),
    LLM_CONVERSATION_SYNC("llm.conversation.sync", "LLM 对话同步");
    
    private final String code;
    private final String description;
    
    LlmInteractionCommandType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

/**
 * LLM 间交互服务
 */
@Service
public class LlmInteractionService {
    
    private final McpCommandRouter commandRouter;
    private final ContextTransferHandler contextTransferHandler;
    private final LlmContextRegistry contextRegistry;
    
    /**
     * LLM-A 发起与 LLM-B 的交互
     */
    public InteractionResult initiateInteraction(
            String sourceAgentId,
            String targetAgentId,
            InteractionRequest request) {
        
        LlmSceneContext sourceContext = contextRegistry.getBySceneId(request.getSourceSceneId());
        
        A2ACommand.ContextTransfer contextTransfer = contextTransferHandler.prepareTransfer(
            sourceContext,
            request.getTransferMode(),
            request.getIncludedParts()
        );
        
        A2ACommand command = A2ACommand.builder()
            .header(A2ACommand.CommandHeader.builder()
                .protocolVersion("2.4")
                .commandType(A2ACommandType.LLM_CHAT)
                .commandId("cmd-" + UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis())
                .traceId(generateTraceId())
                .build())
            .body(A2ACommand.CommandBody.builder()
                .source(A2ACommand.AgentInfo.builder()
                    .agentId(sourceAgentId)
                    .sceneId(request.getSourceSceneId())
                    .build())
                .target(A2ACommand.AgentInfo.builder()
                    .agentId(targetAgentId)
                    .sceneId(request.getTargetSceneId())
                    .build())
                .params(request.getParams())
                .payload(request.getPayload())
                .build())
            .contextTransfer(contextTransfer)
            .security(buildSecurityInfo(sourceContext))
            .build();
        
        A2ACommandResponse response = commandRouter.route(command);
        
        return InteractionResult.builder()
            .success(response.getHeader().getStatus() == A2ACommandResponse.ResponseStatus.SUCCESS)
            .responseId(response.getHeader().getResponseId())
            .result(response.getBody().getResult())
            .tokenUsage(response.getBody().getTokenUsage())
            .build();
    }
    
    /**
     * LLM-B 接收来自 LLM-A 的交互
     */
    public InteractionContext receiveInteraction(A2ACommand command) {
        A2ACommand.ContextTransfer transfer = command.getContextTransfer();
        
        LlmSceneContext receivedContext = contextTransferHandler.receiveTransfer(
            transfer,
            command.getBody().getTarget().getSceneId()
        );
        
        LlmSceneContext targetContext = contextRegistry.getBySceneId(
            command.getBody().getTarget().getSceneId()
        );
        
        if (targetContext != null) {
            contextTransferHandler.mergeContext(
                targetContext, 
                receivedContext,
                ContextTransferHandlerImpl.MergeStrategy.DEEP_MERGE
            );
        } else {
            contextRegistry.register(receivedContext);
            targetContext = receivedContext;
        }
        
        return InteractionContext.builder()
            .sourceAgentId(command.getBody().getSource().getAgentId())
            .targetAgentId(command.getBody().getTarget().getAgentId())
            .sceneContext(targetContext)
            .originalCommand(command)
            .build();
    }
    
    /**
     * LLM-B 返回结果给 LLM-A
     */
    public void returnResult(
            String sourceAgentId,
            String targetAgentId,
            InteractionResult result) {
        
        A2ACommand resultCommand = A2ACommand.builder()
            .header(A2ACommand.CommandHeader.builder()
                .protocolVersion("2.4")
                .commandType(A2ACommandType.LLM_RESULT_RETURN)
                .commandId("cmd-" + UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis())
                .build())
            .body(A2ACommand.CommandBody.builder()
                .source(A2ACommand.AgentInfo.builder()
                    .agentId(sourceAgentId)
                    .build())
                .target(A2ACommand.AgentInfo.builder()
                    .agentId(targetAgentId)
                    .build())
                .payload(result)
                .build())
            .build();
        
        commandRouter.route(resultCommand);
    }
}

/**
 * 交互请求
 */
@Data
@Builder
public class InteractionRequest {
    
    private String sourceSceneId;
    private String targetSceneId;
    private A2ACommand.ContextTransfer.TransferMode transferMode;
    private Set<A2ACommand.ContextTransfer.ContextPart> includedParts;
    private Map<String, Object> params;
    private Object payload;
    private long timeout;
}

/**
 * 交互结果
 */
@Data
@Builder
public class InteractionResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String responseId;
    private Object result;
    private A2ACommandResponse.TokenUsage tokenUsage;
    private String errorMessage;
}

/**
 * 交互上下文
 */
@Data
@Builder
public class InteractionContext {
    
    private String sourceAgentId;
    private String targetAgentId;
    private LlmSceneContext sceneContext;
    private A2ACommand originalCommand;
}
```

### 5.3 场景示例：招聘到审批的 LLM 交互

```java
/**
 * 招聘场景服务
 */
@Service
public class RecruitmentSceneService {
    
    @Autowired
    private LlmInteractionService llmInteractionService;
    
    @Autowired
    private LlmContextRegistry contextRegistry;
    
    /**
     * 发起简历审批请求
     * LLM-A (招聘场景) -> LLM-B (审批场景)
     */
    public ApprovalResult requestResumeApproval(
            String candidateId,
            String positionId,
            String userId) {
        
        LlmSceneContext recruitmentContext = contextRegistry.getBySceneId("recruitment-scene");
        
        Map<String, Object> candidateData = getCandidateData(candidateId);
        Map<String, Object> positionData = getPositionData(positionId);
        
        InteractionRequest request = InteractionRequest.builder()
            .sourceSceneId("recruitment-scene")
            .targetSceneId("approval-scene")
            .transferMode(A2ACommand.ContextTransfer.TransferMode.SELECTIVE)
            .includedParts(Set.of(
                A2ACommand.ContextTransfer.ContextPart.USER_CONTEXT,
                A2ACommand.ContextTransfer.ContextPart.KNOWLEDGE_CONTEXT
            ))
            .params(Map.of(
                "candidateId", candidateId,
                "positionId", positionId,
                "taskType", "RESUME_APPROVAL"
            ))
            .payload(Map.of(
                "candidate", candidateData,
                "position", positionData
            ))
            .timeout(60000)
            .build();
        
        InteractionResult result = llmInteractionService.initiateInteraction(
            "agent-recruitment",
            "agent-approval",
            request
        );
        
        if (result.isSuccess()) {
            return parseApprovalResult(result.getResult());
        } else {
            throw new ApprovalException("Approval request failed: " + result.getErrorMessage());
        }
    }
}

/**
 * 审批场景服务
 */
@Service
public class ApprovalSceneService {
    
    @Autowired
    private LlmInteractionService llmInteractionService;
    
    @Autowired
    private LlmSandboxManager sandboxManager;
    
    /**
     * 处理来自招聘场景的审批请求
     * LLM-B (审批场景) 接收 LLM-A (招聘场景) 的请求
     */
    @EventHandler(commandType = A2ACommandType.LLM_CHAT)
    public A2ACommandResponse handleApprovalRequest(A2ACommand command) {
        
        InteractionContext interactionContext = llmInteractionService.receiveInteraction(command);
        
        LlmSceneContext approvalContext = interactionContext.getSceneContext();
        
        String sandboxId = activateLlmSandbox(approvalContext);
        
        Map<String, Object> payload = (Map<String, Object>) command.getBody().getPayload();
        Map<String, Object> candidate = (Map<String, Object>) payload.get("candidate");
        Map<String, Object> position = (Map<String, Object>) payload.get("position");
        
        ApprovalAnalysis analysis = analyzeResume(sandboxId, candidate, position, approvalContext);
        
        return A2ACommandResponse.builder()
            .header(A2ACommandResponse.ResponseHeader.builder()
                .commandId(command.getHeader().getCommandId())
                .responseId(UUID.randomUUID().toString())
                .status(A2ACommandResponse.ResponseStatus.SUCCESS)
                .timestamp(System.currentTimeMillis())
                .build())
            .body(A2ACommandResponse.ResponseBody.builder()
                .result(analysis)
                .build())
            .build();
    }
    
    private String activateLlmSandbox(LlmSceneContext context) {
        SandboxConfig config = SandboxConfig.builder()
            .sandboxId("sandbox-approval-" + UUID.randomUUID().toString().substring(0, 8))
            .sceneId(context.getSceneId())
            .llmModelId("gpt-4")
            .memoryLimit(512 * 1024 * 1024)
            .tokenLimit(100000)
            .build();
        
        LlmSandbox sandbox = sandboxManager.createSandbox(config);
        sandbox.initialize(config);
        sandbox.start();
        
        return config.getSandboxId();
    }
    
    private ApprovalAnalysis analyzeResume(
            String sandboxId,
            Map<String, Object> candidate,
            Map<String, Object> position,
            LlmSceneContext context) {
        
        String systemPrompt = buildApprovalPrompt(context);
        String userPrompt = buildCandidatePrompt(candidate, position);
        
        LlmCommand chatCommand = LlmCommand.builder()
            .commandId(UUID.randomUUID().toString())
            .type(LlmCommandType.CHAT)
            .params(Map.of(
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
                )
            ))
            .build();
        
        LlmSandbox sandbox = sandboxManager.getSandbox(sandboxId);
        SandboxResult result = sandbox.execute(chatCommand);
        
        return parseAnalysisResult(result);
    }
}
```

### 5.4 交互序列图

```
┌─────────────┐    ┌─────────────┐    ┌──────────┐    ┌─────────────┐    ┌─────────────┐
│ 招聘场景     │    │ LLM-A       │    │MCP Agent │    │ LLM-B       │    │ 审批场景     │
│ Service     │    │ Sandbox     │    │          │    │ Sandbox     │    │ Service     │
└──────┬──────┘    └──────┬──────┘    └────┬─────┘    └──────┬──────┘    └──────┬──────┘
       │                  │                │                 │                  │
       │ requestResumeApproval()           │                 │                  │
       │─────────────────────────────────────────────────────────────────────▶│
       │                  │                │                 │                  │
       │                  │                │                 │  prepare context │
       │                  │                │                 │◀─────────────────│
       │                  │                │                 │                  │
       │                  │ A2A Command    │                 │                  │
       │                  │ (Context Transfer)               │                  │
       │                  │───────────────▶│                 │                  │
       │                  │                │                 │                  │
       │                  │                │ validate & route│                  │
       │                  │                │────────────────▶│                  │
       │                  │                │                 │                  │
       │                  │                │                 │ receive context  │
       │                  │                │                 │ activate sandbox │
       │                  │                │                 │                  │
       │                  │                │                 │ execute analysis │
       │                  │                │                 │                  │
       │                  │                │◀────────────────│                  │
       │                  │                │ response        │                  │
       │                  │                │                 │                  │
       │                  │◀───────────────│                 │                  │
       │                  │ response       │                 │                  │
       │                  │                │                 │                  │
       │◀─────────────────────────────────────────────────────────────────────│
       │ ApprovalResult   │                │                 │                  │
       │                  │                │                 │                  │
```

---

## 六、Engine 集成

### 6.1 Scene Engine 集成

```java
/**
 * Scene Engine 扩展接口
 */
public interface SceneEngine {
    
    /**
     * 创建场景并初始化上下文
     */
    SceneHandle createScene(SceneCreateRequest request);
    
    /**
     * 获取场景上下文
     */
    LlmSceneContext getSceneContext(String sceneId);
    
    /**
     * 跨场景交互
     */
    InteractionResult interactWithScene(
        String sourceSceneId,
        String targetSceneId,
        InteractionRequest request
    );
}

/**
 * Scene Engine 实现
 */
@Component
public class SceneEngineImpl implements SceneEngine {
    
    private final SceneContextInitializer contextInitializer;
    private final LlmContextRegistry contextRegistry;
    private final LlmInteractionService interactionService;
    private final SceneLifecycleManager lifecycleManager;
    
    @Override
    public SceneHandle createScene(SceneCreateRequest request) {
        String sceneId = generateSceneId();
        
        InitializeRequest initRequest = InitializeRequest.builder()
            .userId(request.getUserId())
            .userName(request.getUserName())
            .domainId(request.getDomainId())
            .token(request.getToken())
            .sessionId(request.getSessionId())
            .componentType(request.getComponentType())
            .moduleViewType(request.getModuleViewType())
            .moduleConfig(request.getModuleConfig())
            .knowledgeBaseId(request.getKnowledgeBaseId())
            .accessibleKnowledgeBases(request.getAccessibleKnowledgeBases())
            .securityLevel(request.getSecurityLevel())
            .auditEnabled(request.isAuditEnabled())
            .extraParams(request.getExtraParams())
            .build();
        
        LlmSceneContext context = contextInitializer.initialize(sceneId, initRequest);
        
        lifecycleManager.initialize(sceneId);
        
        return SceneHandle.builder()
            .sceneId(sceneId)
            .contextId(context.getContextId())
            .createdAt(System.currentTimeMillis())
            .build();
    }
    
    @Override
    public LlmSceneContext getSceneContext(String sceneId) {
        return contextRegistry.getBySceneId(sceneId);
    }
    
    @Override
    public InteractionResult interactWithScene(
            String sourceSceneId,
            String targetSceneId,
            InteractionRequest request) {
        
        request.setSourceSceneId(sourceSceneId);
        request.setTargetSceneId(targetSceneId);
        
        return interactionService.initiateInteraction(
            getAgentId(sourceSceneId),
            getAgentId(targetSceneId),
            request
        );
    }
    
    private String generateSceneId() {
        return "scene-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String getAgentId(String sceneId) {
        return "agent-" + sceneId;
    }
}

/**
 * 场景创建请求
 */
@Data
@Builder
public class SceneCreateRequest {
    
    private String userId;
    private String userName;
    private String domainId;
    private String token;
    private String sessionId;
    
    private String componentType;
    private String moduleViewType;
    private Object moduleConfig;
    
    private String knowledgeBaseId;
    private List<String> accessibleKnowledgeBases;
    
    private String securityLevel;
    private boolean auditEnabled;
    
    private Map<String, Object> extraParams;
}

/**
 * 场景句柄
 */
@Data
@Builder
public class SceneHandle {
    
    private String sceneId;
    private String contextId;
    private long createdAt;
}
```

### 6.2 自动配置

```java
/**
 * LLM 上下文自动配置
 */
@Configuration
@ConditionalOnProperty(prefix = "ooder.llm.context", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LlmContextAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public LlmContextRegistry llmContextRegistry() {
        return new LlmContextRegistry();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ContextEvictionPolicy contextEvictionPolicy() {
        return new ContextEvictionPolicy();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public NlpContextManager nlpContextManager(NlpComponentFactory componentFactory) {
        return new NlpContextManagerImpl(componentFactory, new OgnlExpressionEvaluator());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SceneContextInitializer sceneContextInitializer(
            LlmContextRegistry contextRegistry,
            NlpContextManager nlpContextManager,
            KnowledgeContextBuilder knowledgeContextBuilder,
            SecurityContextBuilder securityContextBuilder) {
        return new SceneContextInitializerImpl(
            contextRegistry,
            nlpContextManager,
            knowledgeContextBuilder,
            securityContextBuilder
        );
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ContextTransferHandler contextTransferHandler(
            SceneContextInitializer contextInitializer,
            LlmContextRegistry contextRegistry) {
        return new ContextTransferHandlerImpl(contextInitializer, contextRegistry, new ObjectMapper());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public LlmInteractionService llmInteractionService(
            McpCommandRouter commandRouter,
            ContextTransferHandler contextTransferHandler,
            LlmContextRegistry contextRegistry) {
        return new LlmInteractionService(commandRouter, contextTransferHandler, contextRegistry);
    }
}
```

---

## 七、总结

### 7.1 核心组件

| 组件 | 职责 |
|------|------|
| **LlmSceneContext** | LLM 场景上下文，封装所有上下文信息 |
| **SceneContextInitializer** | 场景上下文初始化器 |
| **NlpContextManager** | NLP 上下文管理器 |
| **LlmContextRegistry** | LLM 上下文注册中心 |
| **ContextTransferHandler** | 上下文传递处理器 |
| **LlmInteractionService** | LLM 间交互服务 |

### 7.2 上下文传递模式

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| **FULL** | 完整传递 | 需要完整上下文的场景 |
| **REFERENCE** | 引用传递 | 上下文较大，仅需引用 |
| **DELTA** | 增量传递 | 仅传递变更部分 |
| **SELECTIVE** | 选择性传递 | 按需传递特定部分 |

### 7.3 LLM 间交互流程

```
LLM-A (源场景)
    │
    │ 1. 准备上下文
    │ 2. 构建 A2A 命令
    │ 3. 设置传递模式
    ▼
MCP Agent (路由)
    │
    │ 4. 验证权限
    │ 5. 路由命令
    ▼
LLM-B (目标场景)
    │
    │ 6. 接收上下文
    │ 7. 合并上下文
    │ 8. 执行任务
    │ 9. 返回结果
    ▼
LLM-A (源场景)
    │
    │ 10. 接收结果
```

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
