# Engine 层协作需求文档

**版本**: v1.0  
**日期**: 2026-03-09  
**状态**: 需求规格  
**目标读者**: Engine 开发团队

---

## 一、背景与目标

### 1.1 背景

为了支持 LLM 与场景技能的深度交互，需要 Engine 层提供一系列基础能力，包括：
- 场景上下文的生命周期管理
- NLP 上下文的组件化管理
- 跨场景的上下文传递能力
- 与 LLM-SDK、Agent-SDK 的协作接口

### 1.2 目标

1. **上下文初始化**: Engine 完成 LLM 场景上下文的初始化工作
2. **NLP 上下文管理**: 场景定义支持 NLP 管理上下文功能
3. **A2A 上下文传递**: A2A 命令协议支持上下文传递
4. **LLM 间交互**: LLM-A 与 LLM-B 能完成信息交互

---

## 二、协作任务清单

### 2.1 P0 级任务 (核心必需)

#### TASK-001: 场景上下文初始化器

**任务描述**: 实现场景上下文初始化器，支持 LLM 场景上下文的创建和管理

**输入**:
- 场景ID (sceneId)
- 初始化请求 (SceneContextInitializeRequest)

**输出**:
- 初始化后的 LlmSceneContext

**接口定义**:
```java
public interface SceneContextInitializer {
    LlmSceneContext initialize(String sceneId, SceneContextInitializeRequest request);
    LlmSceneContext restore(String contextId);
    String serialize(LlmSceneContext context);
    LlmSceneContext deserialize(String serialized);
    void destroy(String contextId);
}
```

**验收标准**:
- [ ] 支持创建包含 SceneContext、NlpContext、KnowledgeContext、ToolContext、SecurityContext 的完整上下文
- [ ] 支持上下文的序列化和反序列化
- [ ] 支持从现有会话恢复上下文
- [ ] 支持上下文过期检测和自动清理

**依赖**: 无
**预计工时**: 5天

---

#### TASK-002: 上下文注册中心

**任务描述**: 实现上下文注册中心，管理所有活跃的 LLM 场景上下文

**输入**:
- LlmSceneContext

**输出**:
- 上下文存储和检索

**接口定义**:
```java
public interface LlmContextRegistry {
    void register(LlmSceneContext context);
    LlmSceneContext get(String contextId);
    void update(LlmSceneContext context);
    void remove(String contextId);
    List<LlmSceneContext> listByScene(String sceneId);
    List<LlmSceneContext> listByUser(String userId);
}
```

**验收标准**:
- [ ] 支持上下文的 CRUD 操作
- [ ] 支持按场景ID、用户ID查询
- [ ] 支持上下文过期自动清理
- [ ] 支持上下文状态变更监听

**依赖**: TASK-001
**预计工时**: 3天

---

#### TASK-003: NLP 上下文管理器

**任务描述**: 实现 NLP 上下文管理器，支持组件化上下文管理

**输入**:
- 组件类型 (componentType)
- 模块视图类型 (moduleViewType)
- 模块配置 (moduleConfig)

**输出**:
- NlpContext

**接口定义**:
```java
public interface NlpContextManager {
    NlpContext initializeNlpContext(String componentType, String moduleViewType, Object config);
    void registerComponentContext(String nlpContextId, NlpComponentContext componentContext);
    NlpComponentContext getComponentContext(String nlpContextId, String componentId);
    void setActiveComponent(String nlpContextId, String componentId);
    NlpComponentContext getActiveComponent(String nlpContextId);
    void setExpressionVariable(String nlpContextId, String name, Object value);
    Object evaluateExpression(String nlpContextId, String expression);
}
```

**验收标准**:
- [ ] 支持 9 种 ModuleViewType 的上下文初始化
- [ ] 支持组件上下文的注册和获取
- [ ] 支持活跃组件管理
- [ ] 支持表达式变量设置和求值

**依赖**: TASK-001
**预计工时**: 5天

---

#### TASK-004: 上下文序列化/反序列化

**任务描述**: 实现上下文的序列化和反序列化，支持 A2A 传递

**输入**:
- LlmSceneContext

**输出**:
- 序列化字符串 / 反序列化后的上下文

**接口定义**:
```java
public interface ContextSerializer {
    String serialize(LlmSceneContext context);
    LlmSceneContext deserialize(String serialized);
    String serializePartial(LlmSceneContext context, Set<ContextPart> parts);
}
```

**验收标准**:
- [ ] 支持完整上下文的序列化/反序列化
- [ ] 支持部分上下文的序列化（选择性传递）
- [ ] 序列化格式兼容 JSON
- [ ] 支持版本兼容性（向后兼容）

**依赖**: TASK-001
**预计工时**: 3天

---

### 2.2 P1 级任务 (重要)

#### TASK-005: 上下文传递处理器

**任务描述**: 实现上下文传递处理器，支持 A2A 协议的上下文传递

**输入**:
- 源上下文 (sourceContext)
- 传递模式 (transferMode)
- 包含的部分 (includedParts)

**输出**:
- ContextTransfer

**接口定义**:
```java
public interface ContextTransferHandler {
    ContextTransfer prepareTransfer(LlmSceneContext sourceContext, TransferMode mode, Set<ContextPart> includedParts);
    LlmSceneContext receiveTransfer(ContextTransfer transfer, String targetSceneId);
    void mergeContext(LlmSceneContext target, LlmSceneContext source, MergeStrategy strategy);
}
```

**验收标准**:
- [ ] 支持 4 种传递模式：FULL、REFERENCE、DELTA、SELECTIVE
- [ ] 支持上下文接收和反序列化
- [ ] 支持 3 种合并策略：SOURCE_PRIORITY、TARGET_PRIORITY、DEEP_MERGE
- [ ] 集成 A2A Command 协议

**依赖**: TASK-001, TASK-004
**预计工时**: 5天

---

#### TASK-006: A2A 上下文传递协议集成

**任务描述**: 在 A2A Command 协议中集成上下文传递能力

**输入**:
- A2A Command

**输出**:
- 携带 ContextTransfer 的 Command

**接口定义**:
```java
public interface A2AContextTransferProtocol {
    Command buildTransferCommand(String sourceContextId, String targetSceneId, ContextTransfer transfer);
    ContextTransfer extractTransferFromCommand(Command command);
    void handleTransferCommand(Command command);
}
```

**验收标准**:
- [ ] A2A Command 支持携带 ContextTransfer
- [ ] 支持上下文传递命令的构建和解析
- [ ] 支持跨场景上下文传递处理
- [ ] 与现有 A2A 协议兼容

**依赖**: TASK-005
**预计工时**: 4天

---

#### TASK-007: 场景上下文状态机

**任务描述**: 实现场景上下文状态机，管理上下文生命周期

**状态定义**:
- CREATED -> INITIALIZING -> ACTIVE -> SUSPENDED -> TRANSFERRING -> TRANSFERRED -> DESTROYED

**接口定义**:
```java
public interface ContextStateMachine {
    void transition(String contextId, ContextStatus from, ContextStatus to);
    ContextStatus getStatus(String contextId);
    void registerTransitionListener(ContextStatus from, ContextStatus to, TransitionListener listener);
}
```

**验收标准**:
- [ ] 支持 7 种状态的正向流转
- [ ] 支持状态转换监听
- [ ] 支持错误状态处理
- [ ] 支持状态历史记录

**依赖**: TASK-001, TASK-002
**预计工时**: 3天

---

### 2.3 P2 级任务 (优化)

#### TASK-008: 上下文缓存优化

**任务描述**: 实现上下文多级缓存，提升性能

**接口定义**:
```java
public interface ContextCache {
    void put(String contextId, LlmSceneContext context);
    LlmSceneContext get(String contextId);
    void invalidate(String contextId);
    void invalidateByScene(String sceneId);
}
```

**验收标准**:
- [ ] 支持本地缓存（Caffeine）
- [ ] 支持分布式缓存（Redis）
- [ ] 支持缓存一致性保障
- [ ] 支持缓存命中率监控

**依赖**: TASK-002
**预计工时**: 3天

---

#### TASK-009: 上下文监控与告警

**任务描述**: 实现上下文监控和告警能力

**监控指标**:
- 活跃上下文数量
- 上下文创建/销毁速率
- 上下文过期率
- 上下文传递延迟

**验收标准**:
- [ ] 支持上下文指标收集
- [ ] 支持 Prometheus 集成
- [ ] 支持告警规则配置
- [ ] 支持上下文链路追踪

**依赖**: TASK-002
**预计工时**: 3天

---

## 三、数据模型

### 3.1 LlmSceneContext

```java
@Data
@Builder
public class LlmSceneContext implements Serializable {
    private String contextId;
    private String sceneId;
    private String skillId;
    private String agentId;
    private String userId;
    private String sessionId;
    
    private SceneContext sceneContext;
    private NlpContext nlpContext;
    private KnowledgeContext knowledgeContext;
    private ToolContext toolContext;
    private SecurityContext securityContext;
    
    private ContextStatus status;
    private String currentStep;
    private Map<String, Object> stepData;
    
    private long createdAt;
    private long lastAccessedAt;
    private long expiresAt;
    private Map<String, Object> extendedAttributes;
}
```

### 3.2 NlpContext

```java
@Data
@Builder
public class NlpContext implements Serializable {
    private String nlpContextId;
    private String componentType;
    private ModuleViewType moduleViewType;
    private CustomModuleMeta moduleMeta;
    private CustomDataMeta dataMeta;
    private Map<String, NlpComponentContext> componentContexts;
    private List<String> activeComponentIds;
    private String currentExpression;
    private Map<String, Object> expressionVariables;
}
```

### 3.3 ContextTransfer

```java
@Data
@Builder
public class ContextTransfer implements Serializable {
    private String sourceContextId;
    private String targetContextId;
    private TransferMode transferMode;
    private String serializedContext;
    private ContextReference contextReference;
    private Map<String, Object> contextDelta;
    private Set<ContextPart> includedParts;
    private Set<ContextPart> excludedParts;
    private long createdAt;
    private long expiresAt;
}
```

---

## 四、接口汇总

### 4.1 核心接口

| 接口 | 方法数 | 优先级 | 依赖 |
|-----|--------|--------|------|
| SceneContextInitializer | 5 | P0 | 无 |
| LlmContextRegistry | 6 | P0 | TASK-001 |
| NlpContextManager | 8 | P0 | TASK-001 |
| ContextSerializer | 3 | P0 | TASK-001 |
| ContextTransferHandler | 3 | P1 | TASK-001, TASK-004 |
| A2AContextTransferProtocol | 3 | P1 | TASK-005 |
| ContextStateMachine | 3 | P1 | TASK-001, TASK-002 |
| ContextCache | 4 | P2 | TASK-002 |

**总计: 35个接口方法**

### 4.2 枚举定义

```java
public enum ContextStatus {
    CREATED, INITIALIZING, ACTIVE, SUSPENDED, 
    TRANSFERRING, TRANSFERRED, DESTROYED
}

public enum TransferMode {
    FULL, REFERENCE, DELTA, SELECTIVE
}

public enum ContextPart {
    SCENE_CONTEXT, NLP_CONTEXT, KNOWLEDGE_CONTEXT, 
    TOOL_CONTEXT, SECURITY_CONTEXT, EXTENDED_ATTRIBUTES
}

public enum MergeStrategy {
    SOURCE_PRIORITY, TARGET_PRIORITY, DEEP_MERGE
}

public enum ModuleViewType {
    LAYOUTCONFIG, FORMCONFIG, GRIDCONFIG, TREECONFIG,
    GALLERYCONFIG, BLOCKCONFIG, DIVCONFIG, GROUPCONFIG, PANELCONFIG
}
```

---

## 五、协作边界

### 5.1 Engine 负责

1. **上下文生命周期管理**
   - 创建、存储、更新、销毁
   - 状态机管理
   - 过期清理

2. **NLP 上下文管理**
   - 组件上下文注册
   - 表达式求值
   - 活跃组件管理

3. **上下文传递**
   - 序列化/反序列化
   - 传递协议实现
   - 上下文合并

### 5.2 LLM-SDK 负责

1. **LLM 调用**
   - 模型选择
   - Prompt 构建
   - 响应解析

2. **工具调用**
   - Tool Definition 管理
   - Function Calling
   - 结果处理

3. **RAG 集成**
   - 知识检索
   - Prompt 增强
   - 引用管理

### 5.3 Agent-SDK 负责

1. **A2A 协议**
   - Command 路由
   - 消息队列
   - 负载均衡

2. **Agent 管理**
   - Agent 注册
   - 能力发现
   - 协作调度

---

## 六、实施计划

### 6.1 阶段划分

```
Week 1-2: P0 核心任务
├── TASK-001: 场景上下文初始化器 (5天)
├── TASK-002: 上下文注册中心 (3天)
├── TASK-004: 上下文序列化/反序列化 (3天)
└── 集成测试 (3天)

Week 3-4: P0 NLP 任务
├── TASK-003: NLP 上下文管理器 (5天)
└── 集成测试 (5天)

Week 5-6: P1 传递任务
├── TASK-005: 上下文传递处理器 (5天)
├── TASK-006: A2A 上下文传递协议集成 (4天)
└── 集成测试 (3天)

Week 7-8: P1/P2 优化任务
├── TASK-007: 场景上下文状态机 (3天)
├── TASK-008: 上下文缓存优化 (3天)
├── TASK-009: 上下文监控与告警 (3天)
└── 性能测试 (5天)
```

### 6.2 里程碑

| 里程碑 | 时间 | 交付物 |
|-------|------|--------|
| M1 | Week 2 | 核心上下文管理能力 |
| M2 | Week 4 | NLP 上下文管理能力 |
| M3 | Week 6 | 跨场景上下文传递能力 |
| M4 | Week 8 | 完整功能 + 性能优化 |

---

## 七、验收标准

### 7.1 功能验收

- [ ] 支持创建包含 5 种子上下文的完整场景上下文
- [ ] 支持上下文的序列化和反序列化
- [ ] 支持 NLP 组件上下文管理
- [ ] 支持表达式求值
- [ ] 支持 4 种上下文传递模式
- [ ] 支持 3 种上下文合并策略
- [ ] 支持 A2A 协议集成

### 7.2 性能验收

- [ ] 上下文创建延迟 < 100ms
- [ ] 上下文序列化延迟 < 50ms
- [ ] 上下文查询延迟 < 10ms
- [ ] 支持 10000+ 并发上下文
- [ ] 上下文传递延迟 < 200ms

### 7.3 稳定性验收

- [ ] 上下文过期自动清理
- [ ] 内存泄漏检测通过
- [ ] 故障恢复能力
- [ ] 数据一致性保障

---

## 八、风险与应对

| 风险 | 可能性 | 影响 | 应对措施 |
|-----|--------|------|---------|
| 上下文数据过大 | 中 | 高 | 实现分页加载和增量传递 |
| 序列化性能瓶颈 | 中 | 中 | 使用 Protobuf 替代 JSON |
| 缓存一致性问题 | 低 | 高 | 实现分布式锁和版本控制 |
| A2A 协议不兼容 | 低 | 高 | 提前与 Agent-SDK 团队对接 |

---

## 九、附录

### 9.1 参考文档

- [LLM 与场景技能交互设计方案](llm-scene-interaction-design.md)
- [A2A 命令协议规范](a2a-command-protocol.md)
- [NLP 上下文规范](nlp-context-specification.md)

### 9.2 联系方式

- 技术负责人: [待填写]
- LLM-SDK 对接人: [待填写]
- Agent-SDK 对接人: [待填写]

---

**文档维护**: Engine 开发团队  
**最后更新**: 2026-03-09
