# 能力地址空间完整设计文档

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **维护团队**: Engine Team  
> **状态**: ✅ 已实现

---

## 一、设计原则

### 1.1 核心原则

| 原则 | 说明 |
|------|------|
| **底层减少不确定性** | 地址固定、枚举定义、无动态计算 |
| **需求固定** | 系统支持范围固定，扩充需大版本 |
| **对外输出最小化** | 每地址一个原子能力，复杂能力用参数区分 |
| **地址固定，配置动态** | 地址编译时确定，配置运行时管理 |
| **上下文区分实例** | 多实例通过上下文区分，不分配独立地址 |

### 1.2 设计约束

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  约束 1: 地址固定                                                           │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • CapabilityAddress (0x00-0x7F) 是静态固定的                               │
│  • 编译时确定，运行时不变                                                   │
│  • MCP 离线时可直接使用固定地址调用能力                                      │
│                                                                             │
│  约束 2: 配置动态                                                           │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • MCP 在线时动态评估链路质量                                               │
│  • 计算最优配置并通知下级 Agent                                             │
│  • 推荐完成后形成固定单一策略                                               │
│                                                                             │
│  约束 3: 上下文隔离                                                         │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • 同一地址可对应多个运行实例                                               │
│  • 实例通过 ContextReference 区分                                          │
│  • 不需要为实例分配独立地址                                                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、地址空间规划

### 2.1 固定地址区 (0x00-0x7F)

**共 128 个地址，16 个分类，每分类 8 个地址**

| 序号 | 分类代码 | 分类名称 | 地址范围 | 默认地址 | 说明 |
|:----:|----------|----------|----------|:--------:|------|
| 1 | SYS | 系统核心 | 0x00-0x07 | 0x00 | 系统核心服务 |
| 2 | ORG | 组织服务 | 0x08-0x0F | 0x08 | 组织架构、用户管理 |
| 3 | AUTH | 认证服务 | 0x10-0x17 | 0x10 | 认证、授权 |
| 4 | VFS | 文件存储 | 0x18-0x1F | 0x18 | 文件系统、对象存储 |
| 5 | DB | 数据库 | 0x20-0x27 | 0x20 | 数据库服务 |
| 6 | LLM | 大语言模型 | 0x28-0x2F | 0x28 | LLM、Embedding |
| 7 | KNOW | 知识库 | 0x30-0x37 | 0x30 | 知识库、向量存储 |
| 8 | PAY | 支付服务 | 0x38-0x3F | 0x38 | 支付、结算 |
| 9 | MEDIA | 媒体服务 | 0x40-0x47 | 0x40 | 媒体、社交 |
| 10 | COMM | 通讯服务 | 0x48-0x4F | 0x48 | 消息、通知 |
| 11 | MON | 监控服务 | 0x50-0x57 | 0x50 | 监控、日志 |
| 12 | IOT | 物联网 | 0x58-0x5F | 0x58 | IoT 设备 |
| 13 | SEARCH | 搜索服务 | 0x60-0x67 | 0x60 | 搜索、检索 |
| 14 | SCHED | 调度服务 | 0x68-0x6F | 0x68 | 任务调度 |
| 15 | SEC | 安全服务 | 0x70-0x77 | 0x70 | 安全、加密 |
| 16 | NET | 网络服务 | 0x78-0x7F | 0x78 | 网络、代理 |

### 2.2 扩展地址区 (0x80-0xFF)

**共 128 个地址，4 个区域**

| 区域 | 地址范围 | 地址数 | 用途 | 固定性 |
|------|----------|:------:|------|:------:|
| 系统预留 | 0x80-0x8F | 16 | 未来官方扩展 | 固定 |
| 用户绑定 | 0x90-0x9F | 16 | 用户动态绑定 | 动态分配 |
| 开发者扩展 | 0xA0-0xAF | 16 | 开发者自定义 | 动态分配 |
| 紧急预留 | 0xB0-0xFF | 80 | 应急使用 | 固定 |

### 2.3 地址分配规则

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     地址分配规则                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  固定地址 (编译时确定):                                                      │
│  ─────────────────────────────────────────────────────────────────────────  │
│  ✅ 16个分类 × 8地址 = 128个 (0x00-0x7F)                                    │
│  ✅ 系统预留区 = 16个 (0x80-0x8F)                                           │
│  ✅ 紧急预留区 = 80个 (0xB0-0xFF)                                           │
│                                                                             │
│  固定区域 (运行时动态分配):                                                  │
│  ─────────────────────────────────────────────────────────────────────────  │
│  ✅ 用户绑定区 = 16个 (0x90-0x9F)                                           │
│  ✅ 开发者扩展区 = 16个 (0xA0-0xAF)                                         │
│                                                                             │
│  不需要地址 (通过上下文区分):                                                │
│  ─────────────────────────────────────────────────────────────────────────  │
│  ✅ 能力实例 - 通过 contextId 区分                                          │
│  ✅ 多租户 - 通过 contextId 区分                                            │
│  ✅ 能力操作 (CRUD) - 通过 operation 参数区分                               │
│  ✅ Agent 实例 - 通过 agentId 区分                                          │
│  ✅ 场景级能力 - 通过 sceneId 区分                                           │
│  ✅ 协作链路 - 通过 ContextTransfer 区分                                    │
│  ✅ 版本 - 通过上下文指定版本                                                │
│  ✅ 负载均衡 - 通过上下文选择实例                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、原子能力接口

### 3.1 接口定义

```java
/**
 * 原子能力接口 - 最小化输出
 */
public interface AtomicCapability {
    
    /**
     * 获取能力地址
     */
    CapabilityAddress getAddress();
    
    /**
     * 获取支持的操作列表
     */
    Set<String> getSupportedOperations();
    
    /**
     * 执行能力操作
     * @param operation 操作类型 (create/read/update/delete/list/...)
     * @param params 参数
     * @param contextRef 上下文引用
     * @return 执行结果
     */
    Result execute(String operation, Map<String, Object> params, ContextReference contextRef);
}
```

### 3.2 操作类型约定

| 操作类型 | 说明 | 适用场景 |
|----------|------|----------|
| create | 创建 | 新建资源 |
| read | 读取 | 查询单个资源 |
| update | 更新 | 修改资源 |
| delete | 删除 | 删除资源 |
| list | 列表 | 批量查询 |
| sync | 同步 | 数据同步 |
| export | 导出 | 数据导出 |
| import | 导入 | 数据导入 |
| validate | 验证 | 数据校验 |
| chat | 对话 | LLM 对话 |
| embed | 嵌入 | 向量嵌入 |
| search | 搜索 | 向量搜索 |

---

## 四、上下文体系

### 4.1 Session 层次结构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Session 层次结构                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  UserSession (用户会话)                                                      │
│  ├── 产生时机: 用户登录/安装时                                               │
│  ├── 统一标识: userSessionId                                               │
│  └── 关联: 1:N 个 token                                                    │
│                                                                             │
│  AgentSession (Agent会话)                                                   │
│  ├── 产生时机: 安装/声明 agent 时                                           │
│  ├── 统一标识: agentSessionId (安装时产生)                                  │
│  ├── 启动时产生: contextId                                                 │
│  └── 关联: 属于某个 UserSession                                             │
│                                                                             │
│  ContextId (上下文ID)                                                       │
│  ├── 产生时机: 启动 agent 时                                                │
│  ├── 关系: 可以推导出 userSessionId                                         │
│  └── 用途: 能力调用的上下文标识                                              │
│                                                                             │
│  安全标识 (只有这两个):                                                      │
│  ├── userSessionId (用户会话标识)                                           │
│  └── agentSessionId (Agent会话标识)                                         │
│                                                                             │
│  其他都是协议层 KEY 关系                                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 上下文关联模型

```java
/**
 * 能力会话上下文
 */
public class CapSessionContext {
    
    // 层次关系
    private String userSessionId;           // 用户会话ID (安全标识1)
    private String agentSessionId;          // Agent会话ID (安全标识2)
    private String contextId;               // 上下文ID (能力调用标识)
    
    // 关联信息
    private String userId;
    private String agentId;
    private String sceneId;
    
    // 安全上下文
    private SecurityContext securityContext;
    
    // 能力绑定
    private Map<Integer, String> capabilityBindings;  // address → providerId
}
```

### 4.3 反向索引

```java
/**
 * 上下文注册中心 (扩展)
 */
public class LlmContextRegistry {
    
    // 现有
    private Map<String, LlmSceneContext> contextCache;
    
    // 新增: 反向索引
    private Map<String, String> contextToUserSession;    // contextId → userSessionId
    private Map<String, String> contextToAgentSession;   // contextId → agentSessionId
    
    /**
     * 从 contextId 获取 userSessionId
     */
    public String getUserSessionId(String contextId) {
        return contextToUserSession.get(contextId);
    }
    
    /**
     * 从 contextId 获取 agentSessionId
     */
    public String getAgentSessionId(String contextId) {
        return contextToAgentSession.get(contextId);
    }
}
```

---

## 五、MCP/ROUTE 分层配置

### 5.1 MCP 层配置策略

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     MCP 层配置策略                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  配置来源: 开发者/安装者预定义                                                │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • 初始化时预定义能力配置                                                   │
│  • 会有冗余但固定                                                           │
│  • 手动优先，故障时自动降级                                                 │
│                                                                             │
│  策略规则:                                                                  │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • 手动优先: 使用 priority=1 的提供者                                       │
│  • 故障降级: 主提供者故障时，自动切换到 fallback                            │
│  • 冗余设计: 多个提供者保证高可用                                           │
│                                                                             │
│  触发时机: 安装/启动时                                                      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 ROUTE 层配置策略

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     ROUTE 层配置策略                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  职责: 链路管理、动态场景添加                                                │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • 用户添加协作对象时，自动计算最优链路                                     │
│  • 自动原则，不允许用户手动指定具体提供者                                   │
│  • 根据链路质量自动选择                                                     │
│                                                                             │
│  用户添加协作对象流程:                                                      │
│  ─────────────────────────────────────────────────────────────────────────  │
│  Step 1: 用户发起场景协作请求                                               │
│  Step 2: ROUTE 自动发现可用能力                                             │
│  Step 3: 评估链路质量、负载、可用性                                         │
│  Step 4: 自动选择最优提供者                                                 │
│  Step 5: 建立链路绑定                                                      │
│                                                                             │
│  触发时机: 场景激活/协作添加时                                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.3 MCP vs ROUTE 对比

| 维度 | MCP 层 | ROUTE 层 |
|------|--------|----------|
| **配置来源** | 开发者/安装者预定义 | 运行期动态计算 |
| **冗余设计** | 有冗余，固定配置 | 无冗余，按需选择 |
| **手动指定** | ✅ 允许，优先级最高 | ❌ 不允许 |
| **故障降级** | ✅ 自动降级 | ✅ 自动降级 |
| **用户干预** | 可手动调整 | 完全自动 |
| **触发时机** | 安装/启动时 | 场景激活/协作添加时 |

---

## 六、链路质量评估

### 6.1 评估指标

| 评估维度 | 指标 | 权重 |
|----------|------|:----:|
| 负载评估 | CPU使用率、内存使用率、连接池使用率、请求队列长度 | w1 |
| 可用性评估 | 响应时间(RTT)、成功率、错误率、健康检查状态 | w2 |
| 能力范畴评估 | 支持的操作列表、支持的模型/版本、功能完整性 | w3 |
| 降级评估 | 主能力可用性、备用能力可用性、降级阈值 | w4 |

### 6.2 综合评分

```
linkQualityScore = w1 * load + w2 * availability + w3 * scope + w4 * fallback
```

---

## 七、持久化与恢复

### 7.1 持久化责任划分

| 层级 | 负责人 | 持久化内容 | 存储位置 |
|------|--------|------------|----------|
| MCP Agent | 主控层 | 安全标识、策略配置、全局索引 | MCP Agent 本地 |
| Route Agent | 路由层 | 路由表、连接状态、消息队列 | Route Agent 本地 |
| End Agent | 终端层 | 能力实例状态、上下文快照、对话历史 | End Agent 本地 + VFS |
| VFS | 存储层 | 快照文件、配置文件、知识库 | VFS (本地/MinIO/S3) |

### 7.2 持久化数据结构

```java
/**
 * 能力实例持久化数据
 */
public class CapabilityInstanceSnapshot implements Serializable {
    
    // 安全标识
    private String userSessionId;
    private String agentSessionId;
    private String contextId;
    private String userId;
    private String agentId;
    private String sceneId;
    
    // 安全标签
    private Map<String, String> securityLabels;
    
    // 能力绑定
    private List<CapabilityBindingSnapshot> capabilityBindings;
    
    // 能力配置引用
    private Map<String, String> configReferences;
    
    // 知识库引用
    private List<KnowledgeBaseRef> knowledgeBaseRefs;
    
    // 上下文快照
    private LlmSceneContext contextSnapshot;
    
    // 对话历史
    private List<ConversationMessage> conversationHistory;
    
    // 函数上下文
    private FunctionContext functionContext;
    
    // 时间戳
    private long createdAt;
    private long updatedAt;
    private String checksum;
}
```

### 7.3 恢复流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     恢复流程 (MCP Agent 协调)                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Phase 1: 安全恢复 (MCP Agent)                                              │
│  ├── 1.1 加载安全标识 (userSessionId, agentSessionId)                       │
│  ├── 1.2 验证 token 有效性                                                 │
│  ├── 1.3 重建 SecurityContext                                              │
│  └── 1.4 通知 Route Agent 恢复连接                                         │
│                                                                             │
│  Phase 2: 网络恢复 (Route Agent)                                            │
│  ├── 2.1 重建 Agent 路由表                                                 │
│  ├── 2.2 恢复 Agent 在线状态                                               │
│  └── 2.3 投递离线消息队列                                                  │
│                                                                             │
│  Phase 3: 能力恢复 (End Agent)                                              │
│  ├── 3.1 从 VFS 加载快照文件                                               │
│  ├── 3.2 恢复能力绑定                                                      │
│  ├── 3.3 恢复上下文                                                        │
│  └── 3.4 恢复函数上下文                                                    │
│                                                                             │
│  Phase 4: 知识库恢复 (MCP Agent 协调)                                        │
│  ├── 4.1 检查知识库版本                                                    │
│  ├── 4.2 执行增量同步 (如果需要)                                           │
│  └── 4.3 验证知识库可用性                                                  │
│                                                                             │
│  Phase 5: LLM 恢复 (MCP Agent 协调)                                          │
│  ├── 5.1 恢复对话历史                                                      │
│  ├── 5.2 重建连接池                                                        │
│  └── 5.3 验证 LLM 可用性                                                   │
│                                                                             │
│  Phase 6: 能力验证 (End Agent)                                              │
│  ├── 6.1 测试所有绑定能力                                                  │
│  └── 6.2 上报恢复结果给 MCP Agent                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.4 离线/在线场景

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     离线/在线场景对比                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  场景 1: MCP 离线                                                           │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • 使用固定地址 (0x00-0x7F)                                                │
│  • 使用本地缓存配置                                                        │
│  • 使用默认降级策略                                                        │
│  • 能力调用正常进行                                                        │
│                                                                             │
│  场景 2: MCP 在线                                                           │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • 使用固定地址 (0x00-0x7F)                                                │
│  • 使用 MCP 动态最优配置                                                   │
│  • 接收 MCP 配置更新通知                                                   │
│  • 能力调用使用最优路径                                                    │
│                                                                             │
│  场景 3: MCP 从离线恢复到在线                                               │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • MCP 上线后重新评估链路质量                                               │
│  • 计算最优配置                                                            │
│  • 通知下级 Agent 更新配置                                                 │
│  • 下级 Agent 应用新配置                                                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 八、能力输出与多层配置

### 8.1 capability 字符串 → CapabilityAddress 映射

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     能力输出映射                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Layer 1: Skill 元数据 (skill.json)                                         │
│  ─────────────────────────────────────────────────────────────────────────  │
│  {                                                                          │
│    "sceneCapabilities": [                                                   │
│      {                                                                      │
│        "id": "user.create",           // 能力ID (字符串)                   │
│        "name": "创建用户",                                                  │
│        "category": "ORG",             // 分类                               │
│        "operations": ["create"]       // 操作                               │
│      }                                                                      │
│    ]                                                                        │
│  }                                                                          │
│                                                                             │
│  Layer 2: MCP 配置 (capability-config.yaml)                                 │
│  ─────────────────────────────────────────────────────────────────────────  │
│  capabilityMappings:                                                        │
│    "user.create":                                                           │
│      address: 0x08                    # ORG_LOCAL                           │
│      operation: "create"                                                    │
│    "user.read":                                                             │
│      address: 0x08                    # ORG_LOCAL                           │
│      operation: "read"                                                      │
│    "llm.chat":                                                              │
│      address: 0x28                    # LLM                                 │
│      operation: "chat"                                                      │
│                                                                             │
│  Layer 3: FunctionContext 初始化                                            │
│  ─────────────────────────────────────────────────────────────────────────  │
│  • 加载 Skill 元数据                                                        │
│  • 查找 capability 对应的 address 映射                                      │
│  • 设置 capabilityAddress 和 operation                                      │
│  • 如果没有映射，保持 capability 字符串 (兼容模式)                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 8.2 FunctionDefinition 扩展

```java
/**
 * 函数定义 (扩展)
 */
public static class FunctionDefinition implements Serializable {
    private String name;
    private String description;
    
    // 原有字段 (保持兼容)
    private String capability;              // 能力ID (字符串)
    
    // 新增字段
    private Integer capabilityAddress;      // CapabilityAddress (0x08)
    private String operation;               // 操作类型
    
    private Map<String, ParameterDefinition> parameters;
    private List<String> required;
    
    /**
     * 是否已映射到地址
     */
    public boolean hasAddressMapping() {
        return capabilityAddress != null;
    }
    
    /**
     * 获取 CapabilityAddress 枚举
     */
    public CapabilityAddress getCapabilityAddressEnum() {
        if (capabilityAddress == null) {
            return null;
        }
        return CapabilityAddress.fromCode(capabilityAddress);
    }
}
```

---

## 九、VFS 同步支持

### 9.1 适用范围

| 内容 | 是否适合 VFS 同步 | 说明 |
|------|:-----------------:|------|
| 快照文件 | ✅ | CapabilityInstanceSnapshot |
| 配置文件 | ✅ | capability-config.yaml |
| 对话历史 | ✅ | conversationHistory |
| 知识库 | ⚠️ | 需要增量同步 |
| LLM 连接池 | ❌ | 需要重建 |

### 9.2 VFS 能力评估

| VFS 类型 | 地址 | 同步能力 | 适用场景 |
|----------|------|----------|----------|
| VFS_LOCAL | 0x18 | 无 (单机) | 微型服务部署 |
| VFS_MINIO | 0x1A | 分布式对象存储 | 小型/大型服务部署 |
| VFS_S3 | 0x1C | 云端对象存储 | 大型服务部署 |

---

## 十、安全层设计

### 10.1 安全标识

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     安全标识                                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  安全标识 (只有这两个):                                                      │
│  ├── userSessionId (用户会话标识)                                           │
│  └── agentSessionId (Agent会话标识)                                         │
│                                                                             │
│  其他都是协议层 KEY 关系                                                     │
│                                                                             │
│  验证流程:                                                                  │
│  ├── 验证 userSessionId 有效性                                              │
│  ├── 验证 agentSessionId 有效性                                             │
│  ├── 验证 checksum 完整性                                                   │
│  └── 重建安全上下文 SecurityContext                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 10.2 SecurityContext 扩展

```java
/**
 * 安全上下文 (扩展)
 */
public class SecurityContext {
    
    // 现有字段
    private String sessionId;
    private String traceId;
    private String ipAddress;
    private String userAgent;
    private String securityLevel;
    
    // 新增字段
    private Set<Integer> allowedCapabilities;                    // 允许的能力地址
    private Map<Integer, Set<String>> allowedOperations;         // 允许的操作
    
    /**
     * 验证能力是否允许
     */
    public boolean isCapabilityAllowed(Integer address) {
        return allowedCapabilities != null && allowedCapabilities.contains(address);
    }
    
    /**
     * 验证操作是否允许
     */
    public boolean isOperationAllowed(Integer address, String operation) {
        if (allowedOperations == null) {
            return false;
        }
        Set<String> ops = allowedOperations.get(address);
        return ops != null && ops.contains(operation);
    }
}
```

---

## 十一、数据结构定义

### 11.1 CapabilityCategory 枚举

```java
public enum CapabilityCategory {
    SYS(0x00, "SYS", "系统核心"),
    ORG(0x08, "ORG", "组织服务"),
    AUTH(0x10, "AUTH", "认证服务"),
    VFS(0x18, "VFS", "文件存储"),
    DB(0x20, "DB", "数据库"),
    LLM(0x28, "LLM", "大语言模型"),
    KNOW(0x30, "KNOW", "知识库"),
    PAY(0x38, "PAY", "支付服务"),
    MEDIA(0x40, "MEDIA", "媒体服务"),
    COMM(0x48, "COMM", "通讯服务"),
    MON(0x50, "MON", "监控服务"),
    IOT(0x58, "IOT", "物联网"),
    SEARCH(0x60, "SEARCH", "搜索服务"),
    SCHED(0x68, "SCHED", "调度服务"),
    SEC(0x70, "SEC", "安全服务"),
    NET(0x78, "NET", "网络服务");
    
    private final int baseAddress;
    private final String code;
    private final String name;
    
    // ... 方法实现
}
```

### 11.2 CapabilityAddress 枚举

```java
public enum CapabilityAddress {
    // SYS (0x00-0x07)
    SYS_CORE(0x00, "SYS_CORE", "系统核心", CapabilityCategory.SYS),
    SYS_CONFIG(0x01, "SYS_CONFIG", "系统配置", CapabilityCategory.SYS),
    
    // ORG (0x08-0x0F)
    ORG_LOCAL(0x08, "ORG_LOCAL", "本地组织", CapabilityCategory.ORG),
    ORG_DINGDING(0x09, "ORG_DINGDING", "钉钉组织", CapabilityCategory.ORG),
    ORG_FEISHU(0x0A, "ORG_FEISHU", "飞书组织", CapabilityCategory.ORG),
    ORG_WECOM(0x0B, "ORG_WECOM", "企业微信", CapabilityCategory.ORG),
    ORG_LDAP(0x0C, "ORG_LDAP", "LDAP", CapabilityCategory.ORG),
    
    // VFS (0x18-0x1F)
    VFS_LOCAL(0x18, "VFS_LOCAL", "本地存储", CapabilityCategory.VFS),
    VFS_DATABASE(0x19, "VFS_DATABASE", "数据库存储", CapabilityCategory.VFS),
    VFS_MINIO(0x1A, "VFS_MINIO", "MinIO存储", CapabilityCategory.VFS),
    VFS_OSS(0x1B, "VFS_OSS", "阿里云OSS", CapabilityCategory.VFS),
    VFS_S3(0x1C, "VFS_S3", "AWS S3", CapabilityCategory.VFS),
    
    // LLM (0x28-0x2F)
    LLM_OLLAMA(0x28, "LLM_OLLAMA", "Ollama本地模型", CapabilityCategory.LLM),
    LLM_OPENAI(0x29, "LLM_OPENAI", "OpenAI", CapabilityCategory.LLM),
    LLM_QIANWEN(0x2A, "LLM_QIANWEN", "通义千问", CapabilityCategory.LLM),
    LLM_DEEPSEEK(0x2B, "LLM_DEEPSEEK", "DeepSeek", CapabilityCategory.LLM),
    LLM_VOLCENGINE(0x2C, "LLM_VOLCENGINE", "火山引擎", CapabilityCategory.LLM),
    
    // ... 其他分类的地址定义
    
    // 扩展区
    EXTENSION_START(0x80, "EXTENSION_START", "扩展区开始", null),
    USER_BINDING_START(0x90, "USER_BINDING_START", "用户绑定区开始", null),
    DEVELOPER_START(0xA0, "DEVELOPER_START", "开发者扩展区开始", null),
    RESERVED_START(0xB0, "RESERVED_START", "预留区开始", null);
    
    private final int address;
    private final String code;
    private final String name;
    private final CapabilityCategory category;
    
    // ... 方法实现
}
```

---

## 十二、待实现任务

### 12.1 P0 任务 ✅ 已完成

| 任务 | 说明 | 状态 |
|------|------|:----:|
| CapabilityCategory 枚举 | 16 分类枚举 (SDK) | ✅ |
| CapabilityAddress 枚举 | 128 地址枚举 (SDK) | ✅ |
| CapabilityRouter 路由器 | 能力路由 (SE) | ✅ |
| CapabilityInstanceRegistry | 实例注册管理 (SE) | ✅ |

### 12.2 P1 任务 ✅ 已完成

| 任务 | 说明 | 状态 |
|------|------|:----:|
| CapabilityMappingService | capability → address 映射 | ✅ |
| FunctionDefinition 扩展 | 添加 capabilityAddress 字段 | ✅ |
| LlmContextRegistry 扩展 | 添加反向索引 | ✅ |
| SecurityContext 扩展 | 添加能力地址验证 | ✅ |
| CapabilityInstanceSnapshot | 持久化数据结构 | ✅ |
| CapabilityInstanceRestorer | 恢复逻辑 | ✅ |

---

## 十三、版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| 1.0.0 | 2026-03-11 | 初始版本 |

---

**文档状态**: ✅ 已实现  
**完成日期**: 2026-03-11
