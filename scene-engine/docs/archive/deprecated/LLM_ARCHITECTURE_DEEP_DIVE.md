# LLM 架构深度设计专题

**版本**: v1.0  
**日期**: 2026-03-09  
**状态**: 深度设计文档  

---

## 一、设计哲学

### 1.1 核心思想

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         LLM 设计哲学                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                  │
│   │   Context   │────▶│   Driven    │────▶│  Everything │                  │
│   │   (上下文)   │     │   (驱动)     │     │  (一切)      │                  │
│   └─────────────┘     └─────────────┘     └─────────────┘                  │
│                                                                             │
│   ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                  │
│   │   Layered   │────▶│  Separation │────▶│  Clear API  │                  │
│   │   (分层)     │     │   (分离)     │     │  (清晰接口)  │                  │
│   └─────────────┘     └─────────────┘     └─────────────┘                  │
│                                                                             │
│   ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                  │
│   │  Protocol   │────▶│   Based     │────▶│  A2A First  │                  │
│   │   (协议)     │     │   (基于)     │     │  (A2A优先)   │                  │
│   └─────────────┘     └─────────────┘     └─────────────┘                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 设计原则

| 原则 | 说明 | 实践 |
|-----|------|------|
| **上下文即状态** | 所有状态通过上下文传递 | LlmSceneContext 封装所有状态 |
| **分层解耦** | 职责清晰分离 | 应用层/引擎层/LLM层/A2A层 |
| **协议优先** | 基于标准协议通信 | A2A Command 协议 |
| **模型无关** | 支持多 LLM 切换 | LLM Pool 抽象 |
| **可观测性** | 全链路可追踪 | Context 链路追踪 |

---

## 二、架构全景

### 2.1 五层架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         LLM 五层架构全景                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 5: Application Layer (应用层)                                 │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │   │
│  │  │  Scene   │ │  Rich    │ │  Agent   │ │   User   │ │  Skill   │  │   │
│  │  │  Skill   │ │  Skill   │ │          │ │          │ │  Plugin  │  │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 4: Scene Engine Layer (场景引擎层)                            │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │   │
│  │  │  Scene   │ │  Skill   │ │ Context  │ │  Menu    │ │ Install  │  │   │
│  │  │  Manager │ │  Manager │ │  Manager │ │  Engine  │ │  Engine  │  │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 3: LLM Integration Layer (LLM集成层)                          │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │   │
│  │  │  Context │ │   NLP    │ │Knowledge │ │   Tool   │ │  LLM     │  │   │
│  │  │  Manager │ │  Manager │ │  Manager │ │  Manager │ │  Pool    │  │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 2: A2A Protocol Layer (A2A协议层)                             │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │   │
│  │  │ Command  │ │ Context  │ │  Message │ │  Routing │ │  Load    │  │   │
│  │  │  Router  │ │ Transfer │ │   Queue  │ │   Table  │ │ Balancer │  │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 1: Infrastructure Layer (基础设施层)                          │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │   │
│  │  │  Vector  │ │  VFS     │ │  Cache   │ │  Config  │ │  Monitor │  │   │
│  │  │   Store  │ │  Skills  │ │  Layer   │ │  Center  │ │          │  │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 数据流向

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         LLM 数据流向                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   User Input                                                                │
│       │                                                                     │
│       ▼                                                                     │
│   ┌─────────────┐                                                           │
│   │  Application│  ← 用户交互界面                                            │
│   └──────┬──────┘                                                           │
│          │ 1. 创建/获取 Context                                              │
│          ▼                                                                  │
│   ┌─────────────┐                                                           │
│   │Scene Engine │  ← 场景生命周期管理                                        │
│   └──────┬──────┘                                                           │
│          │ 2. 构建 LLM Context                                               │
│          │    - Scene Context                                                │
│          │    - NLP Context                                                  │
│          │    - Knowledge Context                                            │
│          │    - Tool Context                                                 │
│          │    - Security Context                                             │
│          ▼                                                                  │
│   ┌─────────────┐                                                           │
│   │ LLM Manager │  ← 上下文组装和增强                                        │
│   └──────┬──────┘                                                           │
│          │ 3. RAG 检索                                                       │
│          ▼                                                                  │
│   ┌─────────────┐     ┌─────────────┐                                       │
│   │ Vector Store│◄────│ Embedding   │                                       │
│   │   (Milvus)  │     │   Service   │                                       │
│   └──────┬──────┘     └─────────────┘                                       │
│          │                                                                  │
│          │ 4. 构建 Augmented Prompt                                         │
│          ▼                                                                  │
│   ┌─────────────┐                                                           │
│   │  LLM Pool   │  ← 多模型选择                                             │
│   └──────┬──────┘                                                           │
│          │ 5. LLM API Call                                                  │
│          ▼                                                                  │
│   ┌─────────────┐                                                           │
│   │ LLM Provider│  ← GPT-4/Claude/Local                                     │
│   └──────┬──────┘                                                           │
│          │                                                                  │
│          │ 6. Tool Call?                                                    │
│          ├── Yes ──▶ Tool Execution ──▶ Back to Step 4                      │
│          │                                                                  │
│          ▼ No                                                               │
│   ┌─────────────┐                                                           │
│   │   Response  │                                                           │
│   └──────┬──────┘                                                           │
│          │                                                                  │
│          ▼                                                                  │
│   User Output                                                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、核心上下文设计

### 3.1 LlmSceneContext - 核心数据结构

```java
/**
 * LLM 场景上下文 - 核心数据结构
 * 这是整个 LLM 架构的核心，封装了 LLM 在特定场景中所需的所有上下文信息
 */
@Data
@Builder
public class LlmSceneContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 基础标识 ==========
    private String contextId;           // 上下文唯一标识 (ctx-xxx)
    private String sceneId;             // 场景ID
    private String skillId;             // 技能ID
    private String agentId;             // Agent ID
    private String userId;              // 用户ID
    private String sessionId;           // 会话ID
    private String parentContextId;     // 父上下文ID（用于上下文继承）
    
    // ========== 子上下文（五大核心上下文）==========
    private SceneContext sceneContext;              // 场景上下文
    private NlpContext nlpContext;                  // NLP 上下文
    private KnowledgeContext knowledgeContext;      // 知识上下文
    private ToolContext toolContext;                // 工具上下文
    private SecurityContext securityContext;        // 安全上下文
    
    // ========== 状态信息 ==========
    private ContextStatus status;       // 上下文状态
    private String currentStep;         // 当前步骤
    private Map<String, Object> stepData;   // 步骤数据
    private List<String> history;       // 对话历史
    
    // ========== 时间戳 ==========
    private long createdAt;             // 创建时间
    private long lastAccessedAt;        // 最后访问时间
    private long expiresAt;             // 过期时间
    
    // ========== 扩展属性 ==========
    private Map<String, Object> extendedAttributes;
    
    // ========== 方法 ==========
    public void touch() {
        this.lastAccessedAt = System.currentTimeMillis();
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > this.expiresAt;
    }
    
    public boolean canTransfer() {
        return this.status == ContextStatus.ACTIVE || 
               this.status == ContextStatus.SUSPENDED;
    }
}
```

### 3.2 五大子上下文详解

#### 3.2.1 SceneContext - 场景上下文

```java
/**
 * 场景上下文
 * 描述当前场景的基本信息、角色、配置和技能状态
 */
@Data
@Builder
public class SceneContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 场景基本信息
    private String sceneType;           // 场景类型 (daily-report, project-mgmt, etc.)
    private String sceneName;           // 场景名称
    private String sceneDescription;    // 场景描述
    private String sceneVersion;        // 场景版本
    
    // 角色信息
    private List<String> roles;         // 角色列表
    private String currentRole;         // 当前角色
    private Map<String, RolePermission> rolePermissions;  // 角色权限
    
    // 配置信息
    private Map<String, Object> sceneConfig;    // 场景配置
    private Map<String, Object> runtimeData;    // 运行时数据
    
    // 技能信息
    private List<String> activatedSkills;   // 已激活技能
    private List<String> availableSkills;   // 可用技能
    private Map<String, SkillConfig> skillConfigs;  // 技能配置
    
    // 激活状态
    private ActivationStatus activationStatus;  // 激活状态
    private String activationStep;              // 当前激活步骤
    private Map<String, Object> activationData; // 激活数据
}
```

#### 3.2.2 NlpContext - NLP 上下文

```java
/**
 * NLP 上下文
 * 管理 NLP 组件的上下文，支持表达式求值和组件状态管理
 */
@Data
@Builder
public class NlpContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 基础信息
    private String nlpContextId;        // NLP 上下文ID
    private String componentType;       // 组件类型
    private ModuleViewType moduleViewType;  // 模块视图类型
    
    // 元数据
    private CustomModuleMeta moduleMeta;    // 模块元数据
    private CustomDataMeta dataMeta;        // 数据元数据
    
    // 组件上下文管理
    private Map<String, NlpComponentContext> componentContexts; // 组件上下文映射
    private List<String> activeComponentIds;    // 活跃组件ID列表（按优先级排序）
    
    // 表达式管理
    private String currentExpression;   // 当前表达式
    private Map<String, Object> expressionVariables;    // 表达式变量
    private List<ExpressionHistory> expressionHistory;  // 表达式历史
    
    // 方法
    public NlpComponentContext getActiveComponent() {
        if (activeComponentIds != null && !activeComponentIds.isEmpty()) {
            return componentContexts.get(activeComponentIds.get(0));
        }
        return null;
    }
    
    public void setActiveComponent(String componentId) {
        activeComponentIds.remove(componentId);
        activeComponentIds.add(0, componentId);
        
        // 更新组件激活状态
        componentContexts.values().forEach(ctx -> 
            ctx.setActive(ctx.getComponentId().equals(componentId))
        );
    }
}
```

#### 3.2.3 KnowledgeContext - 知识上下文

```java
/**
 * 知识上下文
 * 管理知识库的访问权限、搜索配置和检索结果
 */
@Data
@Builder
public class KnowledgeContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 知识库信息
    private String knowledgeBaseId;     // 当前知识库ID
    private String knowledgeBaseType;   // 知识库类型
    private List<String> accessibleKnowledgeBases;  // 可访问知识库列表
    
    // 搜索配置
    private Map<String, Object> searchFilters;      // 搜索过滤器
    private int maxResults;             // 最大返回结果数
    private float similarityThreshold;  // 相似度阈值 (0.0 - 1.0)
    private String searchStrategy;      // 搜索策略 (semantic/hybrid/keyword)
    
    // 检索结果
    private List<String> recentDocuments;   // 最近访问文档
    private Map<String, Object> documentCache;  // 文档缓存
    private List<RetrievalResult> lastRetrievalResults;  // 上次检索结果
    
    // RAG 配置
    private int maxContextTokens;       // 最大上下文Token数
    private String rerankModel;         // 重排序模型
    private boolean enableCitation;     // 是否启用引用
}
```

#### 3.2.4 ToolContext - 工具上下文

```java
/**
 * 工具上下文
 * 管理可用工具、工具调用状态和结果
 */
@Data
@Builder
public class ToolContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 工具定义
    private List<ToolDefinition> availableTools;    // 可用工具列表
    private List<String> activeToolIds;             // 活跃工具ID
    private Map<String, ToolConfig> toolConfigs;    // 工具配置
    
    // 调用状态
    private Map<String, ToolExecutionResult> toolResults;   // 工具执行结果
    private Map<String, Object> toolParameters;             // 工具参数
    private List<ToolCallHistory> toolCallHistory;          // 工具调用历史
    
    // 限制
    private int maxToolCalls;         // 最大工具调用次数
    private int currentToolCallCount; // 当前工具调用次数
    private long toolCallTimeout;     // 工具调用超时时间
    
    // 方法
    public boolean canCallTool() {
        return currentToolCallCount < maxToolCalls;
    }
    
    public void recordToolCall(String toolId, ToolExecutionResult result) {
        toolResults.put(toolId, result);
        toolCallHistory.add(new ToolCallHistory(toolId, result, System.currentTimeMillis()));
        currentToolCallCount++;
    }
}
```

#### 3.2.5 SecurityContext - 安全上下文

```java
/**
 * 安全上下文
 * 管理安全相关的信息，包括认证、授权和审计
 */
@Data
@Builder
public class SecurityContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 认证信息
    private String userId;
    private String userName;
    private String token;
    private String domainId;
    private List<String> roles;
    private List<String> permissions;
    
    // 安全级别
    private String securityLevel;       // 安全级别 (LOW/MEDIUM/HIGH)
    private boolean dataEncryption;     // 是否启用数据加密
    private boolean auditEnabled;       // 是否启用审计
    
    // 审计信息
    private String sessionId;
    private String clientIp;
    private String userAgent;
    private List<AuditRecord> auditRecords;
    
    // 数据脱敏
    private List<String> sensitiveFields;   // 敏感字段
    private Map<String, String> dataMaskingRules;  // 脱敏规则
}
```

---

## 四、上下文状态机

### 4.1 状态定义

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      上下文状态机                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                              ┌─────────┐                                    │
│                              │ CREATED │                                    │
│                              │ (创建)   │                                    │
│                              └────┬────┘                                    │
│                                   │ initialize()                            │
│                                   ▼                                         │
│                              ┌─────────┐                                    │
│                         ┌───▶│INITIALIZING│                                 │
│                         │    │(初始化中)│                                   │
│                         │    └────┬────┘                                    │
│                         │         │ onInitialized()                         │
│                         │         ▼                                         │
│                         │    ┌─────────┐                                    │
│                         │    │ ACTIVE  │◄────────────────────────┐         │
│                    error│    │ (活跃)  │                         │         │
│                         │    └────┬────┘                         │         │
│                         │         │                              │         │
│                         │         │ suspend()                    │resume() │
│                         │         ▼                              │         │
│                         │    ┌─────────┐                         │         │
│                         └───▶│SUSPENDED│─────────────────────────┘         │
│                              │ (挂起)  │                                   │
│                              └────┬────┘                                   │
│                                   │ transfer()                              │
│                                   ▼                                         │
│                              ┌─────────┐                                    │
│                              │TRANSFERRING│                                 │
│                              │(传输中) │                                   │
│                              └────┬────┘                                    │
│                                   │ onTransferred()                         │
│                                   ▼                                         │
│                              ┌─────────┐                                    │
│                              │TRANSFERRED│                                  │
│                              │(已传输) │                                   │
│                              └────┬────┘                                    │
│                                   │ destroy()                               │
│                                   ▼                                         │
│                              ┌─────────┐                                    │
│                              │DESTROYED│                                    │
│                              │(已销毁) │                                   │
│                              └─────────┘                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 状态转换表

| 当前状态 | 事件 | 目标状态 | 说明 |
|---------|------|---------|------|
| CREATED | initialize() | INITIALIZING | 开始初始化 |
| INITIALIZING | onInitialized() | ACTIVE | 初始化完成 |
| INITIALIZING | onError() | ERROR | 初始化失败 |
| ACTIVE | suspend() | SUSPENDED | 挂起上下文 |
| SUSPENDED | resume() | ACTIVE | 恢复上下文 |
| ACTIVE/SUSPENDED | transfer() | TRANSFERRING | 开始传递 |
| TRANSFERRING | onTransferred() | TRANSFERRED | 传递完成 |
| TRANSFERRING | onError() | ACTIVE | 传递失败，恢复原状态 |
| 任意状态 | destroy() | DESTROYED | 销毁上下文 |

---

## 五、上下文传递机制

### 5.1 传递模式

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       上下文传递模式                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Mode 1: FULL (完整传递)                                                     │
│  ┌─────────────┐                    ┌─────────────┐                        │
│  │   Source    │────────────────────▶│   Target    │                        │
│  │  Context    │   序列化所有数据    │  Context    │                        │
│  └─────────────┘                    └─────────────┘                        │
│       100% 数据                    重建完整上下文                           │
│                                                                             │
│  Mode 2: REFERENCE (引用传递)                                                │
│  ┌─────────────┐                    ┌─────────────┐                        │
│  │   Source    │────────────────────▶│   Target    │                        │
│  │  Context    │   传递引用ID        │  Context    │                        │
│  └─────────────┘                    └─────────────┘                        │
│       contextId                    从Registry恢复                          │
│                                                                             │
│  Mode 3: DELTA (增量传递)                                                    │
│  ┌─────────────┐                    ┌─────────────┐                        │
│  │   Source    │────────────────────▶│   Target    │                        │
│  │  Context    │   传递变更数据      │  Context    │                        │
│  └─────────────┘                    └─────────────┘                        │
│       changes only                 合并到目标上下文                         │
│                                                                             │
│  Mode 4: SELECTIVE (选择性传递)                                              │
│  ┌─────────────┐                    ┌─────────────┐                        │
│  │   Source    │────────────────────▶│   Target    │                        │
│  │  Context    │   选择部分传递      │  Context    │                        │
│  └─────────────┘                    └─────────────┘                        │
│       [Scene, NLP]                 仅重建指定部分                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 ContextTransfer 数据结构

```java
/**
 * 上下文传递
 */
@Data
@Builder
public class ContextTransfer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 标识信息
    private String transferId;              // 传递ID
    private String sourceContextId;         // 源上下文ID
    private String targetContextId;         // 目标上下文ID
    private String sourceSceneId;           // 源场景ID
    private String targetSceneId;           // 目标场景ID
    
    // 传递配置
    private TransferMode transferMode;      // 传递模式
    private Set<ContextPart> includedParts; // 包含的部分
    private Set<ContextPart> excludedParts; // 排除的部分
    
    // 传递数据
    private String serializedContext;       // 序列化的上下文数据
    private ContextReference contextReference;  // 上下文引用
    private Map<String, Object> contextDelta;   // 上下文增量
    
    // 元数据
    private long createdAt;
    private long expiresAt;
    private String checksum;                // 数据校验和
    
    // 方法
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    public boolean isValid() {
        return !isExpired() && verifyChecksum();
    }
    
    private boolean verifyChecksum() {
        // 校验数据完整性
        return true;
    }
}
```

---

## 六、RAG 集成设计

### 6.1 RAG Pipeline

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         RAG Pipeline                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐                                                           │
│  │ User Query  │                                                           │
│  └──────┬──────┘                                                           │
│         │                                                                  │
│         ▼                                                                  │
│  ┌─────────────┐     ┌─────────────┐                                       │
│  │   Query     │────▶│  Embedding  │                                       │
│  │Understanding│     │   Service   │                                       │
│  └─────────────┘     └──────┬──────┘                                       │
│                             │                                              │
│                             ▼                                              │
│  ┌─────────────┐     ┌─────────────┐                                       │
│  │   Vector    │◄────│ Query Vector│                                       │
│  │   Search    │     │  [0.1,...]  │                                       │
│  └──────┬──────┘     └─────────────┘                                       │
│         │                                                                  │
│         ▼                                                                  │
│  ┌─────────────────────────────────────────┐                               │
│  │           Retrieved Chunks              │                               │
│  │  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐      │                               │
│  │  │ 0.95│ │ 0.89│ │ 0.85│ │ 0.82│ ...   │                               │
│  │  └─────┘ └─────┘ └─────┘ └─────┘      │                               │
│  └─────────────────────────────────────────┘                               │
│         │                                                                  │
│         ▼                                                                  │
│  ┌─────────────┐     ┌─────────────┐                                       │
│  │  Reranking  │────▶│   Top-K     │                                       │
│  │   (可选)    │     │  Selection  │                                       │
│  └─────────────┘     └──────┬──────┘                                       │
│                             │                                              │
│                             ▼                                              │
│  ┌─────────────────────────────────────────┐                               │
│  │         Context Construction            │                               │
│  │  ┌─────────────────────────────────┐   │                               │
│  │  │ System: 你是一个专业的助手...    │   │                               │
│  │  │ Context: [Chunk1, Chunk2, ...]  │   │                               │
│  │  │ User: {original_query}          │   │                               │
│  │  └─────────────────────────────────┘   │                               │
│  └─────────────────────────────────────────┘                               │
│         │                                                                  │
│         ▼                                                                  │
│  ┌─────────────┐                                                           │
│  │  LLM Call   │                                                           │
│  └──────┬──────┘                                                           │
│         │                                                                  │
│         ▼                                                                  │
│  ┌─────────────┐                                                           │
│  │   Answer    │                                                           │
│  └─────────────┘                                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 知识库与向量库关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    知识库与向量库关系                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    OODER-VFS Skills (文档存储)                        │   │
│  │                                                                     │   │
│  │   Document (原始文档)                                               │   │
│  │       ├── Content (内容)                                            │   │
│  │       └── Metadata (元数据)                                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    │ Sync                                   │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Knowledge Base (知识资料库)                        │   │
│  │                                                                     │   │
│  │   Document Entity                                                   │   │
│  │       ├── id, title, author, tags                                   │   │
│  │       ├── content (引用VFS)                                         │   │
│  │       └── permissions                                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    │ Chunking & Embedding                   │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Vector Store (向量库)                              │   │
│  │                                                                     │   │
│  │   Vector Collection                                                 │   │
│  │       ├── vector: [0.1, 0.2, ...] (1536d)                           │   │
│  │       ├── metadata: {docId, chunkId, kbId}                          │   │
│  │       └── content: "text chunk"                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    │ Similarity Search                      │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    RAG Engine (检索增强)                              │   │
│  │                                                                     │   │
│  │   Retrieved Results ──▶ Prompt Augmentation ──▶ LLM                 │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、A2A 协议集成

### 7.1 A2A Command 结构

```java
/**
 * A2A Command - 支持上下文传递
 */
@Data
@Builder
public class Command implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 基础信息
    private String commandId;
    private String commandType;
    private String version;
    
    // 路由信息
    private String sourceAgentId;
    private String targetAgentId;
    private String sourceSceneId;
    private String targetSceneId;
    
    // 上下文传递
    private ContextTransfer contextTransfer;    // 上下文传递数据
    private String contextReference;            // 上下文引用
    
    // 负载
    private Map<String, Object> payload;
    private Map<String, Object> headers;
    
    // 元数据
    private long timestamp;
    private long ttl;
    private Priority priority;
    
    // 追踪
    private String traceId;
    private List<String> spanIds;
}
```

### 7.2 上下文传递流程

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Scene A   │────▶│   LLM-A     │────▶│    A2A      │────▶│   Scene B   │
│   (Source)  │     │   (Agent)   │     │   Router    │     │   (Target)  │
└─────────────┘     └──────┬──────┘     └──────┬──────┘     └──────┬──────┘
                           │                    │                    │
                           │ 1. prepareTransfer │                    │
                           │    (serialize)     │                    │
                           │                    │                    │
                           │ 2. build Command   │                    │
                           │    with Context    │                    │
                           │───────────────────▶│                    │
                           │                    │                    │
                           │                    │ 3. route Command   │
                           │                    │───────────────────▶│
                           │                    │                    │
                           │                    │                    │ 4. receiveTransfer
                           │                    │                    │    (deserialize)
                           │                    │                    │
                           │                    │                    │ 5. mergeContext
                           │                    │                    │
                           │                    │                    │ 6. execute
                           │                    │                    │
                           │                    │ 7. return Result   │
                           │◀───────────────────│                    │
                           │                    │                    │
                           │ 8. return Response │                    │
                           │◀───────────────────│                    │
                           │                    │                    │
┌─────────────┐     ┌──────┴──────┐     ┌──────┴──────┐     ┌─────────────┐
│   Scene A   │◀────│   LLM-A     │◀────│    A2A      │     │   Scene B   │
│   (Source)  │     │   (Agent)   │     │   Router    │     │   (Target)  │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
```

---

## 八、LLM Pool 设计

### 8.1 多模型支持

```java
/**
 * LLM Pool - 多模型管理
 */
@Component
public class LlmPool {
    
    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
    
    @Autowired
    private LlmProviderFactory providerFactory;
    
    @Autowired
    private LlmRoutingStrategy routingStrategy;
    
    /**
     * 注册 LLM Provider
     */
    public void registerProvider(String name, LlmProvider provider) {
        providers.put(name, provider);
    }
    
    /**
     * 获取 Provider
     */
    public LlmProvider getProvider(String name) {
        return providers.get(name);
    }
    
    /**
     * 智能路由选择
     */
    public LlmProvider route(LlmSceneContext context, String taskType) {
        return routingStrategy.select(providers.values(), context, taskType);
    }
    
    /**
     * 调用 LLM
     */
    public LlmResponse chat(LlmSceneContext context, String message) {
        // 1. 选择 Provider
        LlmProvider provider = route(context, "chat");
        
        // 2. 构建 Prompt
        String prompt = buildPrompt(context, message);
        
        // 3. 调用 LLM
        return provider.chat(prompt, context.getToolContext().getAvailableTools());
    }
    
    private String buildPrompt(LlmSceneContext context, String message) {
        StringBuilder prompt = new StringBuilder();
        
        // System Message
        prompt.append("System: ").append(buildSystemMessage(context)).append("\n\n");
        
        // Context (RAG)
        if (context.getKnowledgeContext() != null) {
            prompt.append("Context: ").append(buildContextMessage(context)).append("\n\n");
        }
        
        // History
        if (context.getHistory() != null) {
            for (String h : context.getHistory()) {
                prompt.append(h).append("\n");
            }
        }
        
        // User Message
        prompt.append("User: ").append(message);
        
        return prompt.toString();
    }
}
```

### 8.2 路由策略

```java
/**
 * LLM 路由策略
 */
public interface LlmRoutingStrategy {
    
    /**
     * 选择最佳 Provider
     */
    LlmProvider select(Collection<LlmProvider> providers, LlmSceneContext context, String taskType);
}

/**
 * 基于场景的路由策略
 */
@Component
public class SceneBasedRoutingStrategy implements LlmRoutingStrategy {
    
    @Override
    public LlmProvider select(Collection<LlmProvider> providers, LlmSceneContext context, String taskType) {
        String sceneType = context.getSceneContext().getSceneType();
        
        // 根据场景类型选择模型
        switch (sceneType) {
            case "code-generation":
                return findProvider(providers, "claude-3-opus");
            case "knowledge-qa":
                return findProvider(providers, "gpt-4");
            case "simple-chat":
                return findProvider(providers, "gpt-3.5");
            default:
                return findProvider(providers, "gpt-4");
        }
    }
    
    private LlmProvider findProvider(Collection<LlmProvider> providers, String modelName) {
        return providers.stream()
            .filter(p -> p.getModelName().equals(modelName))
            .findFirst()
            .orElse(providers.iterator().next());
    }
}
```

---

## 九、监控与可观测性

### 9.1 监控指标

| 指标 | 类型 | 说明 |
|-----|------|------|
| llm_context_created_total | Counter | 上下文创建总数 |
| llm_context_active | Gauge | 活跃上下文数 |
| llm_context_duration | Histogram | 上下文生命周期时长 |
| llm_chat_latency | Histogram | LLM 调用延迟 |
| llm_token_usage | Counter | Token 使用量 |
| llm_tool_call_total | Counter | 工具调用次数 |
| llm_rag_retrieval_latency | Histogram | RAG 检索延迟 |
| llm_context_transfer_latency | Histogram | 上下文传递延迟 |

### 9.2 链路追踪

```
Trace: scene-interaction-xxx
├── Span: context-initialization (50ms)
│   └── Span: nlp-context-init (20ms)
├── Span: rag-retrieval (150ms)
│   ├── Span: vector-search (80ms)
│   └── Span: reranking (70ms)
├── Span: llm-chat (2000ms)
│   ├── Span: prompt-building (10ms)
│   ├── Span: llm-api-call (1980ms)
│   └── Span: response-parsing (10ms)
└── Span: context-update (20ms)
```

---

## 十、总结

### 10.1 核心设计要点

1. **上下文即一切**: LlmSceneContext 是核心，封装所有状态
2. **五大子上下文**: Scene、NLP、Knowledge、Tool、Security 各司其职
3. **状态机管理**: 7 种状态，清晰的流转规则
4. **四种传递模式**: FULL、REFERENCE、DELTA、SELECTIVE 灵活选择
5. **RAG 深度集成**: VFS -> Knowledge Base -> Vector Store -> RAG Pipeline
6. **A2A 协议**: 支持跨场景、跨 LLM 的上下文传递
7. **LLM Pool**: 多模型支持，智能路由

### 10.2 关键接口数

| 层级 | 核心接口 | 方法数 |
|-----|---------|--------|
| Context | SceneContextInitializer | 5 |
| Context | LlmContextRegistry | 6 |
| Context | NlpContextManager | 8 |
| Context | ContextTransferHandler | 3 |
| RAG | KnowledgeContextManager | 6 |
| RAG | VectorStore | 8 |
| A2A | A2AContextTransferProtocol | 3 |
| LLM | LlmPool | 4 |
| **总计** | | **43** |

### 10.3 实施路线图

```
Phase 1 (Week 1-2): 核心上下文
├── LlmSceneContext 数据结构
├── SceneContextInitializer
└── LlmContextRegistry

Phase 2 (Week 3-4): NLP 上下文
├── NlpContextManager
├── 组件上下文管理
└── 表达式求值

Phase 3 (Week 5-6): RAG 集成
├── KnowledgeContext
├── Vector Store 集成
└── RAG Pipeline

Phase 4 (Week 7-8): A2A 与 LLM Pool
├── ContextTransferHandler
├── A2A 协议集成
└── LLM Pool
```

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-09
