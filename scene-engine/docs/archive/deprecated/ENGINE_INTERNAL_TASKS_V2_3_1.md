# Engine 内部任务清单 - 版本 2.3.1

**版本**: 2.3.1  
**代号**: Context-Core  
**目标日期**: 2026-04-30  
**状态**: 规划中  
**目标读者**: Engine 开发团队

---

## 一、任务总览

### 1.1 任务分布

| 模块 | 任务数 | 总工时 | 优先级 |
|-----|--------|--------|--------|
| 场景上下文管理 | 3 | 10天 | P0 |
| NLP 上下文管理 | 2 | 8天 | P0 |
| 上下文传递 | 2 | 6天 | P0 |
| RAG 集成 | 1 | 4天 | P1 |
| **总计** | **8** | **28天** | - |

### 1.2 依赖关系

```
ENGINE-001 (上下文初始化器)
    ├── ENGINE-002 (上下文注册中心) [依赖001]
    ├── ENGINE-003 (上下文序列化) [依赖001]
    └── ENGINE-004 (NLP上下文管理器) [依赖001]

ENGINE-004 (NLP上下文管理器)
    └── ENGINE-005 (组件上下文工厂) [依赖004]

ENGINE-003 (上下文序列化)
    └── ENGINE-006 (上下文传递处理器) [依赖003]

ENGINE-006 (上下文传递处理器)
    └── ENGINE-007 (A2A集成适配器) [依赖006]

ENGINE-008 (RAG集成) [依赖ENGINE-002]
```

---

## 二、P0 级任务

### ENGINE-001: 场景上下文初始化器

**优先级**: P0  
**预计工时**: 4天  
**负责人**: [待分配]  
**依赖**: 无

#### 任务描述

实现场景上下文初始化器，负责创建和管理 LlmSceneContext 的生命周期。

#### 核心接口

```java
/**
 * 场景上下文初始化器
 */
public interface SceneContextInitializer {
    
    /**
     * 初始化场景上下文
     */
    LlmSceneContext initialize(String sceneId, SceneContextInitializeRequest request);
    
    /**
     * 恢复上下文
     */
    LlmSceneContext restore(String contextId);
    
    /**
     * 序列化上下文
     */
    String serialize(LlmSceneContext context);
    
    /**
     * 反序列化上下文
     */
    LlmSceneContext deserialize(String serialized);
    
    /**
     * 销毁上下文
     */
    void destroy(String contextId);
}
```

#### 实现要点

1. **上下文构建**
   - 构建 SceneContext（场景信息、角色、配置）
   - 构建 NlpContext（NLP 组件上下文）
   - 构建 KnowledgeContext（知识库上下文）
   - 构建 ToolContext（工具上下文）
   - 构建 SecurityContext（安全上下文）

2. **生命周期管理**
   - 创建 → 初始化 → 活跃 → 销毁
   - 支持上下文过期检测
   - 支持上下文恢复

#### 验收标准

- [ ] 支持创建包含 5 种子上下文的完整上下文
- [ ] 支持上下文序列化和反序列化
- [ ] 支持上下文过期检测（默认30分钟）
- [ ] 支持从现有会话恢复上下文
- [ ] 单元测试覆盖率 > 80%

#### 交付物

1. `SceneContextInitializer` 接口及实现
2. `LlmSceneContext` 核心数据结构
3. 5 种子上下文构建器
4. 单元测试

---

### ENGINE-002: 上下文注册中心

**优先级**: P0  
**预计工时**: 3天  
**负责人**: [待分配]  
**依赖**: ENGINE-001

#### 任务描述

实现上下文注册中心，管理所有活跃的 LLM 场景上下文。

#### 核心接口

```java
/**
 * 上下文注册中心
 */
public interface LlmContextRegistry {
    
    /**
     * 注册上下文
     */
    void register(LlmSceneContext context);
    
    /**
     * 获取上下文
     */
    LlmSceneContext get(String contextId);
    
    /**
     * 更新上下文
     */
    void update(LlmSceneContext context);
    
    /**
     * 移除上下文
     */
    void remove(String contextId);
    
    /**
     * 按场景查询
     */
    List<LlmSceneContext> listByScene(String sceneId);
    
    /**
     * 按用户查询
     */
    List<LlmSceneContext> listByUser(String userId);
    
    /**
     * 注册状态监听器
     */
    void addListener(ContextStatusListener listener);
}
```

#### 实现要点

1. **存储策略**
   - 内存存储（ConcurrentHashMap）
   - 支持可选的 Redis 持久化
   - 支持多级缓存

2. **过期清理**
   - 定时任务清理过期上下文
   - 支持手动清理
   - 清理事件通知

3. **状态监听**
   - 状态变更事件
   - 监听器注册机制

#### 验收标准

- [ ] 支持上下文的 CRUD 操作
- [ ] 支持按场景ID、用户ID查询
- [ ] 支持上下文过期自动清理
- [ ] 支持状态变更监听
- [ ] 支持 10000+ 并发上下文

#### 交付物

1. `LlmContextRegistry` 接口及实现
2. 内存存储实现
3. 过期清理任务
4. 状态监听机制

---

### ENGINE-003: 上下文序列化/反序列化

**优先级**: P0  
**预计工时**: 3天  
**负责人**: [待分配]  
**依赖**: ENGINE-001

#### 任务描述

实现上下文的序列化和反序列化，支持 A2A 传递。

#### 核心接口

```java
/**
 * 上下文序列化器
 */
public interface ContextSerializer {
    
    /**
     * 序列化完整上下文
     */
    String serialize(LlmSceneContext context);
    
    /**
     * 反序列化上下文
     */
    LlmSceneContext deserialize(String serialized);
    
    /**
     * 序列化部分上下文
     */
    String serializePartial(LlmSceneContext context, Set<ContextPart> parts);
    
    /**
     * 获取序列化大小
     */
    int getSerializedSize(LlmSceneContext context);
}

/**
 * 序列化格式
 */
public enum SerializationFormat {
    JSON,       // JSON 格式（默认）
    PROTOBUF,   // Protobuf 格式（高性能）
    KRYO        // Kryo 格式（Java专用）
}
```

#### 实现要点

1. **JSON 序列化**
   - 使用 Jackson 进行序列化
   - 支持自定义序列化器
   - 支持压缩（GZIP）

2. **部分序列化**
   - 支持选择性序列化子上下文
   - 支持排除敏感字段

3. **版本兼容**
   - 支持向后兼容
   - 版本号管理

#### 验收标准

- [ ] 支持完整上下文的序列化/反序列化
- [ ] 支持部分上下文的序列化
- [ ] 支持 JSON 和 Protobuf 格式
- [ ] 支持版本兼容性
- [ ] 序列化大小 < 1MB（典型场景）

#### 交付物

1. `ContextSerializer` 接口及实现
2. JSON 序列化器
3. Protobuf 序列化器（可选）
4. 版本兼容性测试

---

### ENGINE-004: NLP 上下文管理器

**优先级**: P0  
**预计工时**: 5天  
**负责人**: [待分配]  
**依赖**: ENGINE-001

#### 任务描述

实现 NLP 上下文管理器，支持组件化上下文管理。

#### 核心接口

```java
/**
 * NLP 上下文管理器
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
     * 设置活跃组件
     */
    void setActiveComponent(String nlpContextId, String componentId);
    
    /**
     * 获取活跃组件
     */
    NlpComponentContext getActiveComponent(String nlpContextId);
    
    /**
     * 设置表达式变量
     */
    void setExpressionVariable(String nlpContextId, String name, Object value);
    
    /**
     * 求值表达式
     */
    Object evaluateExpression(String nlpContextId, String expression);
}
```

#### 支持的 ModuleViewType

| 类型 | 说明 |
|-----|------|
| LAYOUTCONFIG | 布局配置 |
| FORMCONFIG | 表单配置 |
| GRIDCONFIG | 表格配置 |
| TREECONFIG | 树形配置 |
| GALLERYCONFIG | 画廊配置 |
| BLOCKCONFIG | 块配置 |
| DIVCONFIG | 容器配置 |
| GROUPCONFIG | 分组配置 |
| PANELCONFIG | 面板配置 |

#### 实现要点

1. **组件上下文管理**
   - 组件注册和获取
   - 活跃组件管理
   - 组件状态同步

2. **表达式求值**
   - 支持 SpEL 表达式
   - 变量作用域管理
   - 表达式缓存

#### 验收标准

- [ ] 支持 9 种 ModuleViewType
- [ ] 支持组件上下文注册和获取
- [ ] 支持活跃组件管理
- [ ] 支持表达式求值
- [ ] 支持表达式变量设置

#### 交付物

1. `NlpContextManager` 接口及实现
2. 组件上下文管理
3. 表达式求值器
4. 单元测试

---

### ENGINE-005: 组件上下文工厂

**优先级**: P0  
**预计工时**: 3天  
**负责人**: [待分配]  
**依赖**: ENGINE-004

#### 任务描述

实现组件上下文工厂，根据配置创建不同类型的组件上下文。

#### 核心接口

```java
/**
 * 组件上下文工厂
 */
public interface NlpComponentFactory {
    
    /**
     * 从配置创建组件上下文
     */
    NlpComponentContext createFromConfig(Object config, ModuleViewType viewType);
    
    /**
     * 注册组件构建器
     */
    void registerBuilder(String componentType, ComponentContextBuilder builder);
    
    /**
     * 获取支持的组件类型
     */
    List<String> getSupportedTypes();
}

/**
 * 组件上下文构建器
 */
public interface ComponentContextBuilder {
    NlpComponentContext build(Object config);
}
```

#### 实现要点

1. **组件构建器注册**
   - 支持动态注册
   - 内置常用组件构建器

2. **配置解析**
   - 支持 JSON/YAML 配置
   - 配置验证

#### 验收标准

- [ ] 支持 9 种 ModuleViewType 的组件创建
- [ ] 支持动态注册组件构建器
- [ ] 支持配置验证
- [ ] 支持组件缓存

#### 交付物

1. `NlpComponentFactory` 接口及实现
2. 内置组件构建器
3. 配置解析器

---

### ENGINE-006: 上下文传递处理器

**优先级**: P0  
**预计工时**: 4天  
**负责人**: [待分配]  
**依赖**: ENGINE-003

#### 任务描述

实现上下文传递处理器，支持 A2A 协议的上下文传递。

#### 核心接口

```java
/**
 * 上下文传递处理器
 */
public interface ContextTransferHandler {
    
    /**
     * 准备传递
     */
    ContextTransfer prepareTransfer(
        LlmSceneContext sourceContext,
        TransferMode mode,
        Set<ContextPart> includedParts
    );
    
    /**
     * 接收传递
     */
    LlmSceneContext receiveTransfer(ContextTransfer transfer, String targetSceneId);
    
    /**
     * 合并上下文
     */
    void mergeContext(LlmSceneContext target, LlmSceneContext source, MergeStrategy strategy);
    
    /**
     * 验证传递
     */
    boolean validateTransfer(ContextTransfer transfer);
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

#### 传递模式实现

| 模式 | 实现方式 |
|-----|---------|
| FULL | 序列化完整上下文 |
| REFERENCE | 传递 contextId，目标端从 Registry 获取 |
| DELTA | 计算上下文差异，传递变更部分 |
| SELECTIVE | 根据 includedParts 选择性序列化 |

#### 验收标准

- [ ] 支持 4 种传递模式
- [ ] 支持 3 种合并策略
- [ ] 支持传递验证
- [ ] 支持传递失败处理
- [ ] 支持传递日志记录

#### 交付物

1. `ContextTransferHandler` 接口及实现
2. 4 种传递模式实现
3. 3 种合并策略实现
4. 传递验证器

---

### ENGINE-007: A2A 集成适配器

**优先级**: P0  
**预计工时**: 2天  
**负责人**: [待分配]  
**依赖**: ENGINE-006

#### 任务描述

实现 A2A 集成适配器，桥接 Engine 和 AGENT-SDK。

#### 核心接口

```java
/**
 * A2A 集成适配器
 */
@Component
public class A2AIntegrationAdapter {
    
    @Autowired
    private A2AService a2aService;
    
    @Autowired
    private ContextTransferHandler transferHandler;
    
    @Autowired
    private ContextSerializer serializer;
    
    /**
     * 跨场景调用
     */
    public CrossSceneResult callCrossScene(
        String sourceContextId,
        String targetSceneId,
        CrossSceneRequest request
    ) {
        // 1. 获取源上下文
        LlmSceneContext sourceContext = contextRegistry.get(sourceContextId);
        
        // 2. 准备传递
        ContextTransfer transfer = transferHandler.prepareTransfer(
            sourceContext,
            request.getTransferMode(),
            request.getIncludedParts()
        );
        
        // 3. 构建 Command
        Command command = Command.builder()
            .commandId(generateCommandId())
            .commandType("CROSS_SCENE_CALL")
            .sourceSceneId(sourceContext.getSceneId())
            .targetSceneId(targetSceneId)
            .contextTransfer(transfer)
            .transferMode(request.getTransferMode())
            .payload(request.getPayload())
            .build();
        
        // 4. 发送 Command
        CommandResponse response = a2aService.sendCommand(command);
        
        // 5. 处理响应
        return processResponse(response);
    }
    
    /**
     * 处理接收到的传递
     */
    public LlmSceneContext handleReceivedTransfer(ContextTransfer transfer) {
        // 1. 验证传递
        if (!transferHandler.validateTransfer(transfer)) {
            throw new InvalidTransferException("Transfer validation failed");
        }
        
        // 2. 接收传递
        return transferHandler.receiveTransfer(transfer, transfer.getTargetSceneId());
    }
}
```

#### 验收标准

- [ ] 支持跨场景调用
- [ ] 支持上下文传递
- [ ] 支持错误处理
- [ ] 支持超时控制
- [ ] 支持重试机制

#### 交付物

1. `A2AIntegrationAdapter` 实现
2. 跨场景调用封装
3. 错误处理机制
4. 集成测试

---

## 三、P1 级任务

### ENGINE-008: RAG 基础集成

**优先级**: P1  
**预计工时**: 4天  
**负责人**: [待分配]  
**依赖**: ENGINE-002

#### 任务描述

实现 RAG 基础集成，支持知识检索和 Prompt 增强。

#### 核心接口

```java
/**
 * RAG 服务
 */
public interface RagService {
    
    /**
     * 检索相关知识
     */
    List<RetrievalResult> retrieve(KnowledgeContext context, String query);
    
    /**
     * 构建增强 Prompt
     */
    String buildAugmentedPrompt(String originalPrompt, List<RetrievalResult> results);
    
    /**
     * RAG 对话
     */
    LlmResponse chatWithRag(LlmSceneContext context, String message);
}

/**
 * 检索结果
 */
@Data
@Builder
public class RetrievalResult {
    private String documentId;
    private String chunkId;
    private String content;
    private float score;
    private Map<String, Object> metadata;
}
```

#### 实现要点

1. **知识检索**
   - 调用 EmbeddingService 向量化查询
   - 调用 VectorStore 检索相似文档
   - 结果排序和过滤

2. **Prompt 增强**
   - 构建 System Message
   - 注入检索结果作为 Context
   - 保持原始查询

#### 验收标准

- [ ] 支持知识检索
- [ ] 支持 Prompt 增强
- [ ] 支持 RAG 对话
- [ ] 支持检索结果缓存
- [ ] 支持引用追踪（基础版）

#### 交付物

1. `RagService` 接口及实现
2. 知识检索封装
3. Prompt 构建器
4. 缓存机制

---

## 四、实施计划

### 4.1 时间线

```
Week 1: 上下文基础
├── Day 1-2: ENGINE-001 场景上下文初始化器
├── Day 3-4: ENGINE-002 上下文注册中心
└── Day 5: 代码审查

Week 2: NLP上下文
├── Day 1-2: ENGINE-003 上下文序列化
├── Day 3-4: ENGINE-004 NLP上下文管理器
└── Day 5: 代码审查

Week 3: 组件和传递
├── Day 1-2: ENGINE-005 组件上下文工厂
├── Day 3-4: ENGINE-006 上下文传递处理器
└── Day 5: 代码审查

Week 4: 集成和RAG
├── Day 1-2: ENGINE-007 A2A集成适配器
├── Day 3-4: ENGINE-008 RAG基础集成
└── Day 5: 集成测试

Week 5: 测试和优化
├── Day 1-3: 端到端测试
├── Day 4: 性能优化
└── Day 5: 文档完善
```

### 4.2 里程碑

| 里程碑 | 时间 | 交付物 |
|-------|------|--------|
| M1 | Week 1 | 上下文管理能力 |
| M2 | Week 2 | NLP上下文管理能力 |
| M3 | Week 3 | 上下文传递能力 |
| M4 | Week 4 | A2A集成和RAG能力 |
| M5 | Week 5 | 完整功能和测试 |

---

## 五、验收标准

### 5.1 功能验收

- [ ] 所有 8 个任务完成开发
- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试用例全部通过
- [ ] API 文档完整

### 5.2 性能验收

| 指标 | 目标值 |
|-----|--------|
| 上下文创建延迟 | < 100ms |
| 上下文查询延迟 | < 10ms |
| 上下文序列化延迟 | < 50ms |
| 上下文传递延迟（同进程） | < 20ms |
| RAG 检索延迟 | < 200ms |
| 并发上下文数 | > 10000 |

### 5.3 稳定性验收

- [ ] 内存泄漏检测通过
- [ ] 长时间运行测试通过（72小时）
- [ ] 故障恢复测试通过
- [ ] 压力测试通过

---

## 六、外部依赖

### 6.1 LLM-SDK 依赖

| 接口 | 用途 | 预计可用时间 |
|-----|------|-------------|
| LlmService.chat | LLM 对话 | Week 2 |
| LlmService.chatWithTools | 工具调用 | Week 2 |
| EmbeddingService.embed | 文本向量化 | Week 2 |
| TokenManager.countTokens | Token 计算 | Week 3 |

### 6.2 AGENT-SDK 依赖

| 接口 | 用途 | 预计可用时间 |
|-----|------|-------------|
| A2AService.sendCommand | 发送命令 | Week 3 |
| A2AService.transferContext | 传递上下文 | Week 3 |
| CommandRouter.route | 命令路由 | Week 3 |

---

## 七、附录

### 7.1 参考文档

- [LLM 与场景技能交互设计方案](llm-scene-interaction-design.md)
- [Engine 层协作需求](engine-collaboration-request.md)
- [版本 2.3.1 实施路线图](VERSION_2_3_1_ROADMAP.md)
- [LLM-SDK 协作文档](COLLABORATION_LLM_SDK_V2_3_1.md)
- [AGENT-SDK 协作文档](COLLABORATION_AGENT_SDK_V2_3_1.md)

### 7.2 变更记录

| 版本 | 日期 | 变更内容 |
|-----|------|---------|
| 1.0 | 2026-03-09 | 初始版本 |

---

**文档维护**: Engine Team  
**最后更新**: 2026-03-09
