# LLM-SDK 协作任务文档 - 版本 2.3.1

**版本**: 2.3.1  
**代号**: Context-Core  
**目标日期**: 2026-04-15  
**状态**: 待确认  
**目标读者**: LLM-SDK 开发团队

---

## 一、协作背景

### 1.1 协作目标

Engine 版本 2.3.1 需要 LLM-SDK 提供以下核心能力：
1. **LLM 调用抽象** - 统一的 LLM 调用接口
2. **工具调用支持** - Function Calling 能力
3. **文本向量化** - Embedding 服务
4. **模型路由策略** - 多模型选择和负载均衡
5. **Prompt 管理** - 模板管理和动态构建

### 1.2 协作边界

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         协作边界定义                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Engine 负责:                                                               │
│  ├── 场景上下文组装 (LlmSceneContext)                                       │
│  ├── Prompt 构建和增强                                                      │
│  ├── 工具定义管理                                                           │
│  └── 响应结果处理                                                           │
│                                                                             │
│  LLM-SDK 负责:                                                              │
│  ├── LLM API 调用封装                                                       │
│  ├── 多模型 Provider 管理                                                   │
│  ├── Token 计算和管理                                                       │
│  ├── 流式响应处理                                                           │
│  └── 错误重试和降级                                                         │
│                                                                             │
│  协作接口:                                                                  │
│  └── LlmService (Engine 调用 LLM-SDK)                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、任务清单

### 2.1 LLM-SDK-001: LLM 调用抽象接口

**优先级**: P0  
**预计工时**: 4天  
**依赖**: 无

#### 任务描述

提供统一的 LLM 调用接口，屏蔽底层不同 LLM Provider 的差异。

#### 接口定义

```java
/**
 * LLM 服务接口
 * 由 LLM-SDK 实现，Engine 调用
 */
public interface LlmService {
    
    /**
     * 普通对话
     * 
     * @param request 对话请求
     * @return 对话响应
     */
    LlmResponse chat(ChatRequest request);
    
    /**
     * 流式对话
     * 
     * @param request 对话请求
     * @param handler 流式响应处理器
     */
    void chatStream(ChatRequest request, StreamResponseHandler handler);
    
    /**
     * 带工具调用的对话
     * 
     * @param request 对话请求
     * @return 对话响应（可能包含工具调用）
     */
    LlmResponse chatWithTools(ChatRequest request);
}

/**
 * 对话请求
 */
@Data
@Builder
public class ChatRequest {
    private String requestId;           // 请求ID
    private String model;               // 模型名称 (gpt-4, claude-3, etc.)
    private List<Message> messages;     // 消息列表
    private List<ToolDefinition> tools; // 可用工具定义
    private Float temperature;          // 温度
    private Integer maxTokens;          // 最大Token数
    private Map<String, Object> extraParams;  // 额外参数
}

/**
 * 对话响应
 */
@Data
@Builder
public class LlmResponse {
    private String responseId;          // 响应ID
    private String model;               // 实际使用的模型
    private String content;             // 响应内容
    private List<ToolCall> toolCalls;   // 工具调用
    private FinishReason finishReason;  // 结束原因
    private TokenUsage tokenUsage;      // Token使用量
    private long latency;               // 延迟(ms)
}

/**
 * 消息
 */
@Data
@Builder
public class Message {
    private MessageRole role;           // 角色 (system/user/assistant/tool)
    private String content;             // 内容
    private String name;                // 名称（用于tool消息）
    private List<ToolCall> toolCalls;   // 工具调用（assistant消息）
}
```

#### 验收标准

- [ ] 支持 GPT-4/GPT-3.5 系列模型
- [ ] 支持 Claude 3 系列模型
- [ ] 支持本地模型（通过统一接口）
- [ ] 支持同步和流式调用
- [ ] 支持超时配置和重试机制
- [ ] 提供完整的错误码定义

#### 交付物

1. `LlmService` 接口实现
2. `OpenAiProvider` 实现
3. `ClaudeProvider` 实现
4. 单元测试覆盖率 > 80%

---

### 2.2 LLM-SDK-002: 工具调用支持

**优先级**: P0  
**预计工时**: 3天  
**依赖**: LLM-SDK-001

#### 任务描述

支持 Function Calling / Tool Use 能力，允许 LLM 调用外部工具。

#### 接口定义

```java
/**
 * 工具定义
 */
@Data
@Builder
public class ToolDefinition {
    private String type;                // 类型 (function)
    private FunctionDefinition function; // 函数定义
}

/**
 * 函数定义
 */
@Data
@Builder
public class FunctionDefinition {
    private String name;                // 函数名
    private String description;         // 函数描述
    private JsonSchema parameters;      // 参数Schema
}

/**
 * 工具调用
 */
@Data
@Builder
public class ToolCall {
    private String id;                  // 调用ID
    private String type;                // 类型 (function)
    private FunctionCall function;      // 函数调用
}

/**
 * 函数调用
 */
@Data
@Builder
public class FunctionCall {
    private String name;                // 函数名
    private String arguments;           // 参数JSON字符串
}

/**
 * 工具执行结果
 */
@Data
@Builder
public class ToolExecutionResult {
    private String toolCallId;          // 调用ID
    private String role;                // 角色 (tool)
    private String name;                // 工具名
    private String content;             // 执行结果
}
```

#### 验收标准

- [ ] 支持 OpenAI Function Calling 格式
- [ ] 支持 Claude Tool Use 格式
- [ ] 支持工具定义 Schema 验证
- [ ] 支持工具调用结果回传
- [ ] 支持多轮工具调用

#### 交付物

1. 工具定义相关类
2. 工具调用解析器
3. 工具调用示例代码

---

### 2.3 LLM-SDK-003: 文本向量化服务

**优先级**: P0  
**预计工时**: 3天  
**依赖**: 无

#### 任务描述

提供文本向量化（Embedding）服务，支持 RAG 检索。

#### 接口定义

```java
/**
 * 向量化服务
 */
public interface EmbeddingService {
    
    /**
     * 单文本向量化
     * 
     * @param text 文本
     * @param model 模型名称
     * @return 向量
     */
    Embedding embed(String text, String model);
    
    /**
     * 批量向量化
     * 
     * @param texts 文本列表
     * @param model 模型名称
     * @return 向量列表
     */
    List<Embedding> embedBatch(List<String> texts, String model);
    
    /**
     * 获取模型维度
     * 
     * @param model 模型名称
     * @return 维度
     */
    int getDimension(String model);
}

/**
 * 向量
 */
@Data
@Builder
public class Embedding {
    private String model;               // 模型
    private float[] vector;             // 向量数据
    private int dimension;              // 维度
    private int tokenCount;             // Token数
}
```

#### 支持的模型

| 模型 | 维度 | 提供商 |
|-----|------|--------|
| text-embedding-3-small | 1536 | OpenAI |
| text-embedding-3-large | 3072 | OpenAI |
| text-embedding-ada-002 | 1536 | OpenAI |
| embed-multilingual-v3.0 | 1024 | Cohere |

#### 验收标准

- [ ] 支持 OpenAI Embedding 模型
- [ ] 支持批量向量化
- [ ] 支持异步向量化
- [ ] 支持缓存机制
- [ ] 支持维度自动检测

#### 交付物

1. `EmbeddingService` 实现
2. 批量处理优化
3. 缓存机制实现

---

### 2.4 LLM-SDK-004: 模型路由策略

**优先级**: P1  
**预计工时**: 3天  
**依赖**: LLM-SDK-001

#### 任务描述

实现模型路由策略，支持根据场景类型、成本、性能等因素选择最优模型。

#### 接口定义

```java
/**
 * 模型路由策略
 */
public interface ModelRoutingStrategy {
    
    /**
     * 选择模型
     * 
     * @param context 路由上下文
     * @return 选择的模型
     */
    String selectModel(RoutingContext context);
}

/**
 * 路由上下文
 */
@Data
@Builder
public class RoutingContext {
    private String sceneType;           // 场景类型
    private String taskType;            // 任务类型 (chat/completion/embedding)
    private Integer priority;           // 优先级
    private Long maxLatency;            // 最大延迟要求
    private BigDecimal maxCost;         // 最大成本预算
    private Map<String, Object> extraParams;
}

/**
 * 模型信息
 */
@Data
@Builder
public class ModelInfo {
    private String name;                // 模型名称
    private String provider;            // 提供商
    private int contextWindow;          // 上下文窗口
    private BigDecimal inputPrice;      // 输入价格 (per 1K tokens)
    private BigDecimal outputPrice;     // 输出价格 (per 1K tokens)
    private Map<String, Object> capabilities;  // 能力列表
}
```

#### 内置策略

| 策略 | 说明 |
|-----|------|
| `CostBasedStrategy` | 基于成本选择最便宜的模型 |
| `QualityBasedStrategy` | 基于质量选择最好的模型 |
| `LatencyBasedStrategy` | 基于延迟选择最快的模型 |
| `SceneBasedStrategy` | 基于场景类型选择模型 |
| `FallbackStrategy` | 主模型失败时降级到备用模型 |

#### 验收标准

- [ ] 支持 5 种内置路由策略
- [ ] 支持自定义路由策略
- [ ] 支持策略组合
- [ ] 支持模型健康检查
- [ ] 支持自动降级

#### 交付物

1. 路由策略接口和实现
2. 模型注册中心
3. 健康检查机制

---

### 2.5 LLM-SDK-005: Token 管理

**优先级**: P1  
**预计工时**: 2天  
**依赖**: 无

#### 任务描述

提供 Token 计算和管理能力，支持成本控制和上下文窗口管理。

#### 接口定义

```java
/**
 * Token 管理器
 */
public interface TokenManager {
    
    /**
     * 计算文本 Token 数
     * 
     * @param text 文本
     * @param model 模型
     * @return Token 数
     */
    int countTokens(String text, String model);
    
    /**
     * 计算消息列表 Token 数
     * 
     * @param messages 消息列表
     * @param model 模型
     * @return Token 数
     */
    int countTokens(List<Message> messages, String model);
    
    /**
     * 截断文本到指定 Token 数
     * 
     * @param text 文本
     * @param maxTokens 最大 Token 数
     * @param model 模型
     * @return 截断后的文本
     */
    String truncate(String text, int maxTokens, String model);
    
    /**
     * 获取模型上下文窗口
     * 
     * @param model 模型
     * @return 上下文窗口大小
     */
    int getContextWindow(String model);
}

/**
 * Token 使用量
 */
@Data
@Builder
public class TokenUsage {
    private int promptTokens;       // Prompt Token 数
    private int completionTokens;   // 补全 Token 数
    private int totalTokens;        // 总 Token 数
}
```

#### 验收标准

- [ ] 支持主流模型的 Token 计算
- [ ] 支持 tiktoken 编码
- [ ] 支持文本截断
- [ ] 提供 Token 使用统计

#### 交付物

1. `TokenManager` 实现
2. 各模型 Token 计算器
3. Token 使用监控

---

## 三、协作接口汇总

### 3.1 Engine 调用 LLM-SDK 接口

| 接口 | 方法 | 输入 | 输出 | 说明 |
|-----|------|------|------|------|
| LlmService | chat | ChatRequest | LlmResponse | 普通对话 |
| LlmService | chatStream | ChatRequest, StreamResponseHandler | void | 流式对话 |
| LlmService | chatWithTools | ChatRequest | LlmResponse | 工具调用对话 |
| EmbeddingService | embed | String, String | Embedding | 单文本向量化 |
| EmbeddingService | embedBatch | List<String>, String | List<Embedding> | 批量向量化 |
| TokenManager | countTokens | String, String | int | Token 计算 |
| ModelRoutingStrategy | selectModel | RoutingContext | String | 模型选择 |

### 3.2 数据模型

| 模型 | 说明 | 字段数 |
|-----|------|--------|
| ChatRequest | 对话请求 | 7 |
| LlmResponse | 对话响应 | 7 |
| Message | 消息 | 4 |
| ToolDefinition | 工具定义 | 2 |
| ToolCall | 工具调用 | 3 |
| Embedding | 向量 | 4 |
| TokenUsage | Token使用 | 3 |

---

## 四、实施计划

### 4.1 时间线

```
Week 1: 基础能力
├── Day 1-2: LLM-SDK-001 LLM调用抽象接口
├── Day 3-4: LLM-SDK-003 文本向量化服务
└── Day 5: 代码审查和单元测试

Week 2: 高级能力
├── Day 1-2: LLM-SDK-002 工具调用支持
├── Day 3-4: LLM-SDK-004 模型路由策略
└── Day 5: 代码审查和单元测试

Week 3: 完善和集成
├── Day 1-2: LLM-SDK-005 Token管理
├── Day 3-4: 与 Engine 集成测试
└── Day 5: 文档完善和发布
```

### 4.2 依赖关系

```
LLM-SDK-001 (LLM调用抽象)
    ├── LLM-SDK-002 (工具调用) [依赖001]
    └── LLM-SDK-004 (模型路由) [依赖001]

LLM-SDK-003 (向量化服务) [无依赖]

LLM-SDK-005 (Token管理) [无依赖]
```

---

## 五、验收标准

### 5.1 功能验收

- [ ] 所有 5 个任务完成开发
- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试用例全部通过
- [ ] API 文档完整

### 5.2 性能验收

| 指标 | 目标值 |
|-----|--------|
| LLM 调用延迟 (P99) | < 3s |
| 向量化延迟 (单文本) | < 500ms |
| 向量化延迟 (批量100条) | < 5s |
| Token 计算延迟 | < 10ms |

### 5.3 稳定性验收

- [ ] 支持 99.9% 可用性
- [ ] 支持自动重试和降级
- [ ] 支持熔断机制
- [ ] 支持限流保护

---

## 六、沟通机制

### 6.1 协作沟通

| 事项 | 频率 | 参与方 |
|-----|------|--------|
| 进度同步 | 每周 | Engine + LLM-SDK |
| 接口评审 | 按需 | Engine + LLM-SDK |
| 集成测试 | 每两周 | Engine + LLM-SDK |

### 6.2 联系方式

- **Engine 负责人**: [待填写]
- **LLM-SDK 负责人**: [待填写]
- **技术交流群**: [待填写]

---

## 七、附录

### 7.1 参考文档

- [LLM 与场景技能交互设计方案](llm-scene-interaction-design.md)
- [Engine 层协作需求](engine-collaboration-request.md)
- [版本 2.3.1 实施路线图](VERSION_2_3_1_ROADMAP.md)

### 7.2 变更记录

| 版本 | 日期 | 变更内容 |
|-----|------|---------|
| 1.0 | 2026-03-09 | 初始版本 |

---

**文档维护**: Engine Team  
**最后更新**: 2026-03-09
