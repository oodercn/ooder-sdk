# Skills-LLM 体系架构设计

## 一、概述

本文档定义 ooder Skills-LLM 完整体系架构，解决以下核心问题：

1. **Function Calling 注入**：激活 Skill 时将函数定义注入上下文
2. **知识库多级加载**：skills.md 支持多级加载，不仅限于提示词
3. **页面上下文重组**：角色、本地知识库、Skill 信息如何重组上下文
4. **A2A 上下文传递**：Agent 间默认上下文传递规范

## 二、整体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Skills-LLM 体系架构                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        用户界面层 (UI Layer)                         │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐           │   │
│  │  │ 角色选择 │  │知识库选择│  │ Skill选择│  │ 会话管理 │           │   │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘           │   │
│  └───────┼─────────────┼─────────────┼─────────────┼──────────────────┘   │
│          │             │             │             │                       │
│          ▼             ▼             ▼             ▼                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    上下文重组层 (Context Assembly)                   │   │
│  │                                                                     │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │              SkillActivationContext                          │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │   │   │
│  │  │  │ RoleContext │  │KnowledgeCtx │  │ SkillContext│          │   │   │
│  │  │  │  (角色)     │  │ (知识库)    │  │ (技能)      │          │   │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘          │   │   │
│  │  │                       │                                     │   │   │
│  │  │                       ▼                                     │   │   │
│  │  │  ┌─────────────────────────────────────────────────────┐   │   │   │
│  │  │  │              LlmRuntimeContext                       │   │   │   │
│  │  │  │  - SystemPrompt (系统提示词)                         │   │   │   │
│  │  │  │  - Functions (函数定义)                              │   │   │   │
│  │  │  │  - Knowledge (知识内容)                              │   │   │   │
│  │  │  │  - ConversationMemory (对话记忆)                     │   │   │   │
│  │  │  └─────────────────────────────────────────────────────┘   │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                      │                                      │
│                                      ▼                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      A2A 传输层 (A2A Transport)                     │   │
│  │                                                                     │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │              ContextTransfer (上下文传递)                    │   │   │
│  │  │  TransferMode: FULL | REFERENCE | DELTA | SELECTIVE         │   │   │
│  │  │  DefaultParts: USER | KNOWLEDGE | FUNCTIONS | MEMORY        │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 三、核心组件设计

### 3.1 SkillActivationContext - Skill 激活上下文

**问题**：Function Calling 应该在激活 Skill 时注入到上下文

**设计**：

```java
/**
 * Skill 激活上下文
 * 
 * <p>在 Skill 激活时创建，包含完整的 LLM 运行时所需信息</p>
 */
public class SkillActivationContext {
    
    private String activationId;
    private String skillId;
    private String sceneId;
    private String userId;
    
    // 激活时注入的组件
    private RoleContext roleContext;           // 角色上下文
    private KnowledgeContext knowledgeContext; // 知识库上下文
    private FunctionContext functionContext;   // 函数定义上下文
    private MemoryContext memoryContext;       // 对话记忆上下文
    
    // 激活状态
    private ActivationState state;
    private long activatedAt;
    private long lastActiveAt;
    
    /**
     * 激活 Skill 时创建上下文
     */
    public static SkillActivationContext activate(ActivationRequest request) {
        SkillActivationContext context = new SkillActivationContext();
        context.activationId = generateId();
        context.skillId = request.getSkillId();
        context.sceneId = request.getSceneId();
        context.userId = request.getUserId();
        
        // 1. 加载角色上下文
        context.roleContext = loadRoleContext(request.getRoleId());
        
        // 2. 加载知识库上下文（多级加载）
        context.knowledgeContext = loadKnowledgeContext(
            request.getSkillId(),
            request.getKnowledgeBaseIds()
        );
        
        // 3. 注入函数定义
        context.functionContext = loadFunctionContext(request.getSkillId());
        
        // 4. 恢复对话记忆
        context.memoryContext = loadMemoryContext(request.getSessionId());
        
        context.state = ActivationState.ACTIVATED;
        context.activatedAt = System.currentTimeMillis();
        
        return context;
    }
}
```

### 3.2 FunctionContext - 函数定义上下文

**设计**：激活时从 Skill 元数据加载函数定义

```java
/**
 * 函数定义上下文
 * 
 * <p>在 Skill 激活时注入，包含所有可用的函数定义</p>
 */
public class FunctionContext {
    
    private String skillId;
    private List<FunctionDefinition> functions;
    private Map<String, FunctionExecutor> executors;
    private Map<String, String> capabilityMappings;
    
    /**
     * 从 Skill 元数据加载函数定义
     */
    public static FunctionContext loadFromSkill(String skillId) {
        FunctionContext context = new FunctionContext();
        context.skillId = skillId;
        
        // 从 SkillPackage.metadata.llmConfig.functions 加载
        SkillPackage pkg = skillRegistry.getSkill(skillId);
        Map<String, Object> llmConfig = pkg.getMetadata().get("llmConfig");
        
        if (llmConfig != null && llmConfig.containsKey("functions")) {
            List<Map<String, Object>> funcs = (List<Map<String, Object>>) llmConfig.get("functions");
            
            for (Map<String, Object> func : funcs) {
                FunctionDefinition def = FunctionDefinition.fromMap(func);
                context.functions.add(def);
                
                // 建立函数到 Capability 的映射
                if (func.containsKey("capability")) {
                    context.capabilityMappings.put(def.getName(), (String) func.get("capability"));
                }
            }
        }
        
        return context;
    }
    
    /**
     * 转换为 LLM API 可用的 Tools 格式
     */
    public List<Map<String, Object>> toTools() {
        List<Map<String, Object>> tools = new ArrayList<>();
        
        for (FunctionDefinition def : functions) {
            Map<String, Object> tool = new LinkedHashMap<>();
            tool.put("type", "function");
            tool.put("function", def.toMap());
            tools.add(tool);
        }
        
        return tools;
    }
}
```

## 四、知识库多级加载设计

### 4.1 skills.md 规范

**问题**：知识库/RAG 在激活时读取 skills.md，支持多级加载

**设计**：skills.md 是 Skill 的知识描述文件，支持多级加载

```
skill-package/
├── skill.json              # Skill 元数据
├── skills.md               # 主知识文档
├── knowledge/              # 知识库目录
│   ├── basic.md            # 基础知识（Level 1）
│   ├── advanced.md         # 进阶知识（Level 2）
│   ├── expert.md           # 专家知识（Level 3）
│   └── references/         # 参考文档
│       ├── api.md
│       └── examples.md
└── rag/                    # RAG 向量数据
    ├── index.faiss
    └── chunks.json
```

### 4.2 skills.md 结构规范

```markdown
# Skill 知识文档

## 元信息
- skillId: recruitment-skill
- version: 1.0.0
- loadLevel: BASIC | ADVANCED | EXPERT | FULL

## 角色定义
你是招聘场景的智能助手，专门帮助HR处理招聘相关事务。

## 核心能力
1. 简历扫描与解析
2. 面试安排与协调
3. 候选人筛选与评估

## 知识内容

### Level 1: BASIC（基础）
基础使用说明，每次激活都加载...

### Level 2: ADVANCED（进阶）
进阶功能和配置，按需加载...

### Level 3: EXPERT（专家）
专家级知识和最佳实践，深度场景加载...

## 使用示例
[示例内容...]

## API 参考
[API 文档...]

## 常见问题
[FAQ 内容...]
```

### 4.3 多级加载机制

```java
/**
 * 知识库加载级别
 */
public enum KnowledgeLoadLevel {
    BASIC(1, "基础知识", 2048),      // ~2K tokens
    ADVANCED(2, "进阶知识", 4096),    // ~4K tokens
    EXPERT(3, "专家知识", 8192),      // ~8K tokens
    FULL(4, "完整知识", -1);          // 无限制
    
    private final int level;
    private final String description;
    private final int maxTokens;
}

/**
 * 知识库上下文
 */
public class KnowledgeContext {
    
    private String skillId;
    private KnowledgeLoadLevel loadLevel;
    private List<KnowledgeChunk> loadedChunks;
    private String ragIndexId;
    
    /**
     * 多级加载知识
     */
    public static KnowledgeContext load(String skillId, KnowledgeLoadLevel level) {
        KnowledgeContext context = new KnowledgeContext();
        context.skillId = skillId;
        context.loadLevel = level;
        
        // 1. 加载 skills.md 主文档
        String skillsMd = loadSkillsMd(skillId);
        context.loadedChunks.add(parseMarkdown(skillsMd, level));
        
        // 2. 根据级别加载额外知识
        if (level.getLevel() >= KnowledgeLoadLevel.ADVANCED.getLevel()) {
            context.loadedChunks.add(loadKnowledgeFile(skillId, "knowledge/advanced.md"));
        }
        
        if (level.getLevel() >= KnowledgeLoadLevel.EXPERT.getLevel()) {
            context.loadedChunks.add(loadKnowledgeFile(skillId, "knowledge/expert.md"));
        }
        
        // 3. RAG 向量检索补充
        if (context.ragIndexId != null) {
            List<RagResult> ragResults = ragService.retrieve(
                context.ragIndexId, 
                "skill context", 
                level.getMaxTokens()
            );
            context.loadedChunks.addAll(ragResults);
        }
        
        return context;
    }
    
    /**
     * 构建知识提示词
     */
    public String buildKnowledgePrompt() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("## 知识库内容\n\n");
        
        for (KnowledgeChunk chunk : loadedChunks) {
            sb.append(chunk.getContent()).append("\n\n");
        }
        
        return sb.toString();
    }
}
```

## 五、页面上下文重组机制

### 5.1 上下文重组流程

**问题**：用户打开页面时，角色、本地知识库、Skill 信息如何重组上下文

**设计**：

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           页面上下文重组流程                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  用户打开页面                                                                │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    1. 收集上下文组件                                  │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐           │   │
│  │  │ 用户信息 │  │ 角色配置 │  │ 知识库ID │  │ Skill ID │           │   │
│  │  │ userId   │  │ roleId   │  │ kbIds[]  │  │ skillId  │           │   │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    2. 加载各组件内容                                  │   │
│  │                                                                     │   │
│  │  RoleContext ← RoleService.getRole(roleId)                         │   │
│  │  KnowledgeContext ← KnowledgeService.load(kbIds, level)            │   │
│  │  FunctionContext ← SkillService.loadFunctions(skillId)             │   │
│  │  MemoryContext ← SessionService.getMemory(sessionId)               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    3. 组装 LlmRuntimeContext                         │   │
│  │                                                                     │   │
│  │  SystemPrompt = RoleContext.prompt + KnowledgeContext.prompt       │   │
│  │  Functions = FunctionContext.toTools()                             │   │
│  │  Messages = MemoryContext.history + [current user message]         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    4. 注册到 ContextRegistry                         │   │
│  │                                                                     │   │
│  │  contextRegistry.register(contextId, context)                      │   │
│  │  返回 contextId 给前端                                              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 LlmRuntimeContext - 运行时上下文

```java
/**
 * LLM 运行时上下文
 * 
 * <p>组装完整的 LLM 调用所需上下文</p>
 */
public class LlmRuntimeContext {
    
    private String contextId;
    private String sessionId;
    
    // 组装后的 LLM 输入
    private String systemPrompt;              // 组装后的系统提示词
    private List<Map<String, Object>> tools;  // 函数定义
    private List<Map<String, Object>> messages; // 消息历史
    
    // 子上下文引用
    private RoleContext roleContext;
    private KnowledgeContext knowledgeContext;
    private FunctionContext functionContext;
    private MemoryContext memoryContext;
    
    /**
     * 重组上下文
     */
    public static LlmRuntimeContext assemble(AssemblyRequest request) {
        LlmRuntimeContext context = new LlmRuntimeContext();
        context.contextId = generateContextId();
        context.sessionId = request.getSessionId();
        
        // 1. 加载角色上下文
        context.roleContext = roleService.getRole(request.getRoleId());
        
        // 2. 加载知识库上下文
        context.knowledgeContext = knowledgeService.load(
            request.getKnowledgeBaseIds(),
            request.getLoadLevel()
        );
        
        // 3. 加载函数定义
        context.functionContext = skillService.loadFunctions(request.getSkillId());
        
        // 4. 加载对话记忆
        context.memoryContext = sessionService.getMemory(request.getSessionId());
        
        // 5. 组装系统提示词
        context.systemPrompt = assembleSystemPrompt(
            context.roleContext,
            context.knowledgeContext
        );
        
        // 6. 组装函数定义
        context.tools = context.functionContext.toTools();
        
        // 7. 组装消息历史
        context.messages = context.memoryContext.getHistory();
        
        return context;
    }
    
    /**
     * 组装系统提示词
     */
    private static String assembleSystemPrompt(
            RoleContext role, 
            KnowledgeContext knowledge) {
        
        StringBuilder prompt = new StringBuilder();
        
        // 角色定义
        prompt.append("# 角色定义\n\n");
        prompt.append(role.getDefinition()).append("\n\n");
        
        // 角色行为准则
        prompt.append("## 行为准则\n\n");
        for (String guideline : role.getGuidelines()) {
            prompt.append("- ").append(guideline).append("\n");
        }
        prompt.append("\n");
        
        // 知识库内容
        prompt.append(knowledge.buildKnowledgePrompt());
        
        return prompt.toString();
    }
}
```

### 5.3 RoleContext - 角色上下文

```java
/**
 * 角色上下文
 */
public class RoleContext {
    
    private String roleId;
    private String roleName;
    private String definition;           // 角色定义
    private List<String> guidelines;     // 行为准则
    private List<String> capabilities;   // 能力列表
    private Map<String, Object> config;  // 角色配置
    
    /**
     * 预定义角色
     */
    public static final Map<String, RoleContext> BUILTIN_ROLES = new HashMap<>();
    
    static {
        // 通用助手
        RoleContext assistant = new RoleContext();
        assistant.roleId = "assistant";
        assistant.roleName = "智能助手";
        assistant.definition = "你是一个智能助手，帮助用户完成各种任务。";
        assistant.guidelines = Arrays.asList(
            "保持专业和友好的态度",
            "提供准确和有帮助的回答",
            "在不确定时主动询问澄清"
        );
        BUILTIN_ROLES.put("assistant", assistant);
        
        // HR 助手
        RoleContext hrAssistant = new RoleContext();
        hrAssistant.roleId = "hr-assistant";
        hrAssistant.roleName = "HR 助手";
        hrAssistant.definition = "你是招聘场景的智能助手，专门帮助HR处理招聘相关事务。";
        hrAssistant.guidelines = Arrays.asList(
            "熟悉招聘流程和最佳实践",
            "保护候选人隐私信息",
            "提供客观公正的候选人评估"
        );
        BUILTIN_ROLES.put("hr-assistant", hrAssistant);
    }
}
```

## 六、A2A 默认上下文传递

### 6.1 传递规范

**问题**：A2A 中默认上下文的传递

**设计**：定义 A2A 通信中的默认上下文传递规则

```java
/**
 * A2A 上下文传递配置
 */
public class A2AContextTransferConfig {
    
    // 默认传递模式
    private TransferMode defaultMode = TransferMode.SELECTIVE;
    
    // 默认包含的上下文部分
    private Set<ContextPart> defaultIncludedParts = new HashSet<>(Arrays.asList(
        ContextPart.USER_CONTEXT,        // 用户身份
        ContextPart.KNOWLEDGE_CONTEXT,   // 知识库引用
        ContextPart.FUNCTION_CONTEXT,    // 函数定义
        ContextPart.MEMORY_CONTEXT       // 对话记忆
    ));
    
    // 默认排除的上下文部分
    private Set<ContextPart> defaultExcludedParts = new HashSet<>(Arrays.asList(
        ContextPart.SECURITY_CREDENTIALS, // 安全凭证
        ContextPart.INTERNAL_STATE        // 内部状态
    ));
    
    // 最大传递大小
    private int maxTransferSize = 65536;  // 64KB
}
```

### 6.2 A2A 上下文传递流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        A2A 上下文传递流程                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Agent A (源)                                              Agent B (目标)   │
│       │                                                           │         │
│       ▼                                                           │         │
│  ┌─────────────────┐                                              │         │
│  │ 准备上下文传递   │                                              │         │
│  │                 │                                              │         │
│  │ 1. 选择传递模式  │                                              │         │
│  │ 2. 过滤上下文部分│                                              │         │
│  │ 3. 序列化上下文  │                                              │         │
│  └────────┬────────┘                                              │         │
│           │                                                       │         │
│           ▼                                                       │         │
│  ┌─────────────────────────────────────────────────────────────┐  │         │
│  │                    A2ACommand                                │  │         │
│  │  {                                                           │  │         │
│  │    "header": {                                               │  │         │
│  │      "commandId": "cmd-xxx",                                 │  │         │
│  │      "commandType": "LLM_CONTEXT_SHARE",                     │  │         │
│  │      "sourceAgent": "agent-a",                               │  │         │
│  │      "targetAgent": "agent-b"                                │  │         │
│  │    },                                                        │  │         │
│  │    "contextTransfer": {                                      │  │         │
│  │      "transferMode": "SELECTIVE",                            │  │         │
│  │      "includedParts": ["USER", "KNOWLEDGE", "FUNCTIONS"],    │  │         │
│  │      "contextReference": {                                   │  │         │
│  │        "contextId": "ctx-xxx",                               │  │         │
│  │        "skillId": "recruitment-skill",                       │  │         │
│  │        "sessionId": "sess-xxx"                               │  │         │
│  │      },                                                      │  │         │
│  │      "contextDelta": {                                       │  │         │
│  │        "additionalKnowledge": ["kb-xxx"]                     │  │         │
│  │      }                                                       │  │         │
│  │    }                                                         │  │         │
│  │  }                                                           │  │         │
│  └─────────────────────────────────────────────────────────────┘  │         │
│           │                                                       │         │
│           │  A2A Protocol (P2P/HTTP/WebSocket)                    │         │
│           │                                                       │         │
│           └───────────────────────────────────────────────────────┼─────────┤
│                                                                     │         │
│                                                                     ▼         │
│                                                              ┌─────────────────┤
│                                                              │ 接收上下文传递   │
│                                                              │                 │
│                                                              │ 1. 验证权限      │
│                                                              │ 2. 反序列化上下文│
│                                                              │ 3. 合并到本地上下文│
│                                                              │ 4. 更新运行时状态│
│                                                              └─────────────────┘
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.3 ContextTransferHandler 实现

```java
/**
 * 上下文传递处理器
 */
public class ContextTransferHandler {
    
    private final LlmContextRegistry contextRegistry;
    private final A2AContextTransferConfig config;
    
    /**
     * 准备上下文传递（发送端）
     */
    public ContextTransfer prepareTransfer(
            String sourceContextId,
            TransferMode mode,
            Set<ContextPart> additionalParts) {
        
        LlmSceneContext sourceContext = contextRegistry.get(sourceContextId);
        if (sourceContext == null) {
            throw new ContextNotFoundException(sourceContextId);
        }
        
        // 合并默认部分和额外部分
        Set<ContextPart> includedParts = new HashSet<>(config.getDefaultIncludedParts());
        if (additionalParts != null) {
            includedParts.addAll(additionalParts);
        }
        includedParts.removeAll(config.getDefaultExcludedParts());
        
        ContextTransfer transfer = new ContextTransfer();
        transfer.setSourceContextId(sourceContextId);
        transfer.setTransferMode(mode != null ? mode : config.getDefaultMode());
        transfer.setIncludedParts(includedParts);
        
        switch (transfer.getTransferMode()) {
            case FULL:
                // 完整序列化
                transfer.setSerializedContext(serializeFull(sourceContext));
                break;
                
            case REFERENCE:
                // 只传递引用
                transfer.setContextReference(buildReference(sourceContext));
                break;
                
            case DELTA:
                // 传递增量
                transfer.setContextDelta(buildDelta(sourceContext));
                break;
                
            case SELECTIVE:
                // 选择性传递
                transfer.setSerializedContext(
                    serializePartial(sourceContext, includedParts)
                );
                transfer.setContextReference(buildReference(sourceContext));
                break;
        }
        
        return transfer;
    }
    
    /**
     * 接收上下文传递（接收端）
     */
    public LlmSceneContext receiveTransfer(
            ContextTransfer transfer,
            String targetSceneId) {
        
        // 1. 验证权限
        validateTransferPermission(transfer);
        
        // 2. 创建或获取目标上下文
        LlmSceneContext targetContext = contextRegistry.getBySceneId(targetSceneId);
        if (targetContext == null) {
            targetContext = new LlmSceneContext();
            targetContext.setSceneId(targetSceneId);
        }
        
        // 3. 根据传递模式处理
        switch (transfer.getTransferMode()) {
            case FULL:
                // 完整恢复
                targetContext = deserializeFull(transfer.getSerializedContext());
                break;
                
            case REFERENCE:
                // 从引用加载
                targetContext = loadFromReference(transfer.getContextReference());
                break;
                
            case DELTA:
                // 应用增量
                applyDelta(targetContext, transfer.getContextDelta());
                break;
                
            case SELECTIVE:
                // 选择性合并
                mergePartial(targetContext, transfer.getSerializedContext(), 
                    transfer.getIncludedParts());
                break;
        }
        
        // 4. 注册上下文
        contextRegistry.register(targetContext);
        
        return targetContext;
    }
    
    /**
     * 合并上下文
     */
    public void mergeContext(
            LlmSceneContext target,
            LlmSceneContext source,
            MergeStrategy strategy) {
        
        switch (strategy) {
            case SOURCE_PRIORITY:
                // 源优先：源上下文覆盖目标
                mergeUserContext(target, source.getUserContext(), true);
                mergeKnowledgeContext(target, source.getKnowledgeContext(), true);
                mergeFunctionContext(target, source.getFunctionContext(), true);
                break;
                
            case TARGET_PRIORITY:
                // 目标优先：保留目标上下文
                mergeUserContext(target, source.getUserContext(), false);
                mergeKnowledgeContext(target, source.getKnowledgeContext(), false);
                mergeFunctionContext(target, source.getFunctionContext(), false);
                break;
                
            case DEEP_MERGE:
                // 深度合并：智能合并
                deepMerge(target, source);
                break;
        }
    }
}
```

## 七、完整调用流程

### 7.1 用户打开页面流程

```
1. 用户打开页面
   │
   ├── 前端发送请求
   │   POST /api/context/assemble
   │   {
   │     "userId": "user-xxx",
   │     "roleId": "hr-assistant",
   │     "knowledgeBaseIds": ["kb-xxx"],
   │     "skillId": "recruitment-skill",
   │     "sessionId": "sess-xxx"
   │   }
   │
   ├── 后端处理
   │   │
   │   ├── 1. 加载角色上下文
   │   │   RoleContext role = roleService.getRole("hr-assistant")
   │   │
   │   ├── 2. 加载知识库上下文（多级）
   │   │   KnowledgeContext knowledge = knowledgeService.load(
   │   │     ["kb-xxx"], 
   │   │     KnowledgeLoadLevel.ADVANCED
   │   │   )
   │   │
   │   ├── 3. 加载函数定义（激活时注入）
   │   │   FunctionContext functions = skillService.loadFunctions("recruitment-skill")
   │   │
   │   ├── 4. 加载对话记忆
   │   │   MemoryContext memory = sessionService.getMemory("sess-xxx")
   │   │
   │   ├── 5. 组装运行时上下文
   │   │   LlmRuntimeContext context = LlmRuntimeContext.assemble(...)
   │   │
   │   └── 6. 注册上下文
   │       contextRegistry.register(context)
   │
   └── 返回 contextId 给前端
       {
         "contextId": "ctx-xxx",
         "systemPromptLength": 2048,
         "functionCount": 5,
         "messageCount": 10
       }
```

### 7.2 用户发送消息流程

```
2. 用户发送消息
   │
   ├── 前端发送请求
   │   POST /api/llm/chat
   │   {
   │     "contextId": "ctx-xxx",
   │     "message": "请帮我扫描简历 RESUME-001"
   │   }
   │
   ├── 后端处理
   │   │
   │   ├── 1. 获取运行时上下文
   │   │   LlmRuntimeContext context = contextRegistry.get("ctx-xxx")
   │   │
   │   ├── 2. 添加用户消息
   │   │   context.addMessage("user", "请帮我扫描简历 RESUME-001")
   │   │
   │   ├── 3. 调用 LLM
   │   │   response = llmProvider.chatWithFunctions(
   │   │     context.getSystemPrompt(),
   │   │     context.getMessages(),
   │   │     context.getTools()
   │   │   )
   │   │
   │   ├── 4. 处理 Function Calling
   │   │   if (response.hasToolCalls()) {
   │   │     for (toolCall in response.toolCalls) {
   │   │       // 自动映射到 Capability
   │   │       result = skillService.invokeCapability(
   │   │         context.skillId,
   │   │         toolCall.function.capability,
   │   │         toolCall.function.arguments
   │   │       )
   │   │     }
   │   │   // 继续对话获取最终响应
   │   │     response = llmProvider.chat(...)
   │   │   }
   │   │
   │   └── 5. 更新对话记忆
   │       context.addMessage("assistant", response.content)
   │       sessionService.saveMemory(context)
   │
   └── 返回响应给前端
       {
         "content": "好的，我已经启动简历扫描...",
         "actions": [{"action": "scanResume", "resumeId": "RESUME-001"}]
       }
```

### 7.3 A2A 跨 Agent 调用流程

```
3. A2A 跨 Agent 调用
   │
   ├── Agent A 发起调用
   │   │
   │   ├── 1. 准备上下文传递
   │   │   ContextTransfer transfer = transferHandler.prepareTransfer(
   │   │     sourceContextId,
   │   │     TransferMode.SELECTIVE,
   │   │     [ContextPart.USER, ContextPart.KNOWLEDGE]
   │   │   )
   │   │
   │   └── 2. 发送 A2A 命令
   │       a2aClient.send(A2ACommand.builder()
   │         .type(LLM_CONTEXT_SHARE)
   │         .targetAgent("agent-b")
   │         .contextTransfer(transfer)
   │         .build())
   │
   ├── Agent B 接收调用
   │   │
   │   ├── 1. 接收上下文传递
   │   │   context = transferHandler.receiveTransfer(
   │   │     transfer,
   │   │     targetSceneId
   │   │   )
   │   │
   │   ├── 2. 合并上下文
   │   │   transferHandler.mergeContext(
   │   │     localContext,
   │   │     receivedContext,
   │   │     MergeStrategy.DEEP_MERGE
   │   │   )
   │   │
   │   └── 3. 执行请求
   │       result = processRequest(request, mergedContext)
   │
   └── 返回结果给 Agent A
```

## 八、配置示例

### 8.1 Skill 元数据配置 (skill.json)

```json
{
  "skillId": "recruitment-skill",
  "name": "招聘助手",
  "version": "1.0.0",
  "metadata": {
    "llmConfig": {
      "systemPrompt": "你是招聘场景的智能助手...",
      "temperature": 0.7,
      "maxTokens": 2000,
      "functions": [
        {
          "name": "scan_resume",
          "description": "扫描并解析简历",
          "parameters": {
            "resumeId": {"type": "string", "description": "简历ID"}
          },
          "required": ["resumeId"],
          "capability": "resume_scan"
        }
      ]
    },
    "knowledgeConfig": {
      "defaultLevel": "ADVANCED",
      "ragEnabled": true,
      "ragIndexId": "rag-recruitment-v1",
      "knowledgeFiles": [
        "skills.md",
        "knowledge/basic.md",
        "knowledge/advanced.md"
      ]
    },
    "a2aConfig": {
      "defaultTransferMode": "SELECTIVE",
      "defaultIncludedParts": ["USER", "KNOWLEDGE", "FUNCTIONS"],
      "allowedAgents": ["agent-*"]
    }
  }
}
```

### 8.2 角色配置 (role.json)

```json
{
  "roleId": "hr-assistant",
  "roleName": "HR 助手",
  "definition": "你是招聘场景的智能助手，专门帮助HR处理招聘相关事务。",
  "guidelines": [
    "熟悉招聘流程和最佳实践",
    "保护候选人隐私信息",
    "提供客观公正的候选人评估"
  ],
  "capabilities": [
    "resume_scan",
    "interview_schedule",
    "candidate_query"
  ],
  "defaultKnowledgeLevel": "ADVANCED",
  "defaultSkillId": "recruitment-skill"
}
```

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-09
