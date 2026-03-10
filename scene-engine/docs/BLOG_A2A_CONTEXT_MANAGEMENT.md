# ooderAgent 深度揭秘：A2A 上下文管理的艺术

> 在多 Agent 协作的时代，如何让 Agent 之间高效、安全地共享上下文？本文深入解析 ooderAgent 的 A2A（Agent-to-Agent）上下文管理机制。

## 一、为什么需要 A2A 上下文管理？

在复杂的业务场景中，单个 Agent 往往难以完成所有任务。想象一下招聘场景：

- **简历筛选 Agent**：负责解析和评估简历
- **面试安排 Agent**：负责协调面试时间
- **候选人沟通 Agent**：负责与候选人保持联系

这些 Agent 需要协同工作，而协同的关键在于**上下文传递**。当简历筛选 Agent 发现合适的候选人时，它需要将所有相关信息传递给面试安排 Agent，包括：

- 候选人信息（用户上下文）
- 简历解析结果（知识上下文）
- 可用的面试时间段（函数上下文）
- 之前的沟通记录（记忆上下文）

**没有上下文管理，Agent 就成了"孤岛"；有了 A2A 上下文管理，Agent 形成了"协作网络"。**

## 二、ooderAgent A2A 架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                    A2A 上下文传递架构                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Agent A (源)                                      Agent B (目标)│
│        │                                                   │     │
│        ▼                                                   │     │
│   ┌─────────────────┐                                      │     │
│   │ 准备上下文传递   │                                      │     │
│   │                 │                                      │     │
│   │ • 选择传递模式   │                                      │     │
│   │ • 过滤上下文部分 │                                      │     │
│   │ • 序列化上下文   │                                      │     │
│   └────────┬────────┘                                      │     │
│            │                                               │     │
│            ▼                                               │     │
│   ┌─────────────────────────────────────────────────────┐  │     │
│   │                    A2ACommand                        │  │     │
│   │  {                                                   │  │     │
│   │    "header": {                                       │  │     │
│   │      "commandId": "cmd-xxx",                         │  │     │
│   │      "commandType": "LLM_CONTEXT_SHARE",             │  │     │
│   │      "sourceAgent": "agent-a",                       │  │     │
│   │      "targetAgent": "agent-b"                        │  │     │
│   │    },                                                │  │     │
│   │    "contextTransfer": {                              │  │     │
│   │      "transferMode": "SELECTIVE",                    │  │     │
│   │      "includedParts": ["USER", "KNOWLEDGE", "FUNCTIONS"],│  │     │
│   │      "contextReference": {                           │  │     │
│   │        "contextId": "ctx-xxx",                       │  │     │
│   │        "skillId": "recruitment-skill",               │  │     │
│   │        "sessionId": "sess-xxx"                       │  │     │
│   │      }                                               │  │     │
│   │    }                                                 │  │     │
│   │  }                                                   │  │     │
│   └─────────────────────────────────────────────────────┘  │     │
│            │                                               │     │
│            │  A2A Protocol (P2P/HTTP/WebSocket)            │     │
│            └───────────────────────────────────────────────┼─────┘
│                                                            │     │
│                                                            ▼     │
│                                                     ┌─────────────────┐
│                                                     │ 接收上下文传递   │
│                                                     │                 │
│                                                     │ • 验证权限      │
│                                                     │ • 反序列化上下文│
│                                                     │ • 合并到本地上下文│
│                                                     │ • 更新运行时状态│
│                                                     └─────────────────┘
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 三、四大传递模式：灵活应对不同场景

ooderAgent 提供了四种上下文传递模式，就像快递的四种配送方式：

### 3.1 FULL 模式 - "整车运输"

**适用场景**：Agent 首次协作，需要完整的上下文信息

```java
// 完整序列化所有上下文数据
transfer.setSerializedContext(serializeFull(sourceContext));
```

**特点**：
- 传递完整的上下文数据
- 数据量大，但信息最全
- 适合初次协作或上下文变化较大的场景

### 3.2 REFERENCE 模式 - "到付自提"

**适用场景**：目标 Agent 可以访问共享的上下文存储

```java
// 只传递引用信息
transfer.setContextReference(buildReference(sourceContext));
```

**特点**：
- 只传递上下文的引用（ID、位置等）
- 数据量极小，延迟低
- 需要目标 Agent 能够从共享存储加载完整数据

### 3.3 DELTA 模式 - "增量更新"

**适用场景**：Agent 之间频繁协作，只需要传递变化的部分

```java
// 只传递增量数据
transfer.setContextDelta(buildDelta(sourceContext));
```

**特点**：
- 只传递自上次同步以来的变化
- 数据量适中，效率高
- 适合持续协作的场景

### 3.4 SELECTIVE 模式 - "精准配送"（默认）

**适用场景**：只需要传递特定的上下文部分

```java
// 选择性传递指定部分
transfer.setSerializedContext(
    serializePartial(sourceContext, includedParts)
);
transfer.setContextReference(buildReference(sourceContext));
```

**特点**：
- 灵活选择需要传递的上下文部分
- 默认传递：USER、KNOWLEDGE、FUNCTIONS、MEMORY
- 默认排除：SECURITY_CREDENTIALS、INTERNAL_STATE
- 平衡了数据量和信息完整性

## 四、上下文组成：四大核心部分

ooderAgent 的上下文由四个核心部分组成：

### 4.1 User Context - 用户上下文

包含用户身份信息、权限、偏好设置等。

```java
public class UserContext {
    private String userId;
    private String userName;
    private List<String> roles;
    private Map<String, Object> preferences;
    private Map<String, Object> permissions;
}
```

**传递策略**：
- 默认传递用户身份和权限
- 敏感信息（如密码、Token）默认排除

### 4.2 Knowledge Context - 知识上下文

包含知识库引用、加载的知识内容、RAG 检索结果等。

```java
public class KnowledgeContext {
    private String knowledgeBaseId;
    private KnowledgeLoadLevel loadLevel;
    private List<KnowledgeChunk> loadedKnowledge;
    private String ragIndexId;
    private Map<String, Object> metadata;
}
```

**传递策略**：
- 默认传递知识库引用和加载的知识
- 大量知识内容可以通过引用传递，目标 Agent 按需加载

### 4.3 Function Context - 函数上下文

包含 Skill 激活时注入的函数定义、Capability 映射等。

```java
public class FunctionContext {
    private String skillId;
    private List<FunctionDefinition> functions;
    private Map<String, String> capabilityMappings;
    
    public List<Map<String, Object>> toTools() {
        // 转换为 LLM API 可用的 Tools 格式
    }
}
```

**传递策略**：
- 默认传递函数定义和 Capability 映射
- 确保目标 Agent 可以调用相同的 Capability

### 4.4 Memory Context - 记忆上下文

包含对话历史、上下文变量、临时状态等。

```java
public class MemoryContext {
    private String sessionId;
    private List<Message> messages;
    private Map<String, Object> contextVariables;
    private Map<String, Object> tempState;
}
```

**传递策略**：
- 默认传递对话历史和关键上下文变量
- 临时状态默认排除，避免污染目标 Agent

## 五、安全机制：上下文传递的"安检门"

### 5.1 权限验证

在接收上下文之前，目标 Agent 会验证：

1. **源 Agent 身份**：是否来自可信的 Agent
2. **操作权限**：源 Agent 是否有权限传递此类上下文
3. **数据完整性**：验证上下文数据的签名和哈希

```java
private void validateTransferPermission(ContextTransfer transfer) {
    // 1. 验证源 Agent 身份
    if (!isTrustedAgent(transfer.getSourceAgentId())) {
        throw new SecurityException("Untrusted agent: " + transfer.getSourceAgentId());
    }
    
    // 2. 验证操作权限
    if (!hasPermission(transfer.getSourceAgentId(), "CONTEXT_TRANSFER")) {
        throw new SecurityException("No permission to transfer context");
    }
    
    // 3. 验证数据完整性
    if (!verifySignature(transfer)) {
        throw new SecurityException("Context transfer signature verification failed");
    }
}
```

### 5.2 敏感信息过滤

默认排除的敏感信息：

- **SECURITY_CREDENTIALS**：密码、Token、API Key 等
- **INTERNAL_STATE**：内部状态、缓存数据、临时变量等
- **PERSONAL_DATA**：个人隐私数据（根据配置）

```java
// 默认排除的上下文部分
private Set<ContextPart> defaultExcludedParts = new HashSet<>(Arrays.asList(
    ContextPart.SECURITY_CREDENTIALS,  // 安全凭证
    ContextPart.INTERNAL_STATE         // 内部状态
));
```

### 5.3 大小限制

防止上下文传递成为 DDoS 攻击的载体：

```java
// 最大传递大小：64KB
private int maxTransferSize = 65536;

// 超过限制时，自动切换到 REFERENCE 模式
if (serializedContext.length() > maxTransferSize) {
    transfer.setTransferMode(TransferMode.REFERENCE);
    transfer.setSerializedContext(null);
    transfer.setContextReference(buildReference(sourceContext));
}
```

## 六、实战案例：招聘场景的 A2A 协作

让我们通过一个完整的招聘场景，看看 A2A 上下文管理如何工作：

### 场景描述

1. **简历筛选 Agent** 发现合适的候选人
2. 需要传递给 **面试安排 Agent** 进行面试调度
3. **面试安排 Agent** 完成后，传递给 **候选人沟通 Agent** 发送通知

### 上下文传递流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    招聘场景 A2A 协作流程                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Step 1: 简历筛选 Agent 发现候选人                               │
│  ─────────────────────────────────────                          │
│                                                                 │
│  • 解析简历，提取关键信息                                          │
│  • 评估候选人匹配度                                                │
│  • 决定推荐给面试安排 Agent                                        │
│                                                                 │
│  ContextTransfer:                                                │
│  {                                                               │
│    "transferMode": "SELECTIVE",                                  │
│    "includedParts": ["USER", "KNOWLEDGE", "FUNCTIONS"],          │
│    "context": {                                                  │
│      "user": {                                                   │
│        "candidateId": "CAND-001",                                │
│        "candidateName": "张三",                                   │
│        "resumeId": "RESUME-001"                                  │
│      },                                                          │
│      "knowledge": {                                              │
│        "resumeAnalysis": {                                       │
│          "skills": ["Java", "Spring", "MySQL"],                  │
│          "experience": "5年",                                     │
│          "matchScore": 0.85                                      │
│        }                                                         │
│      },                                                          │
│      "functions": ["schedule_interview", "send_notification"]    │
│    }                                                             │
│  }                                                               │
│                                                                 │
│  Step 2: 面试安排 Agent 接收上下文                                │
│  ─────────────────────────────────────                          │
│                                                                 │
│  • 验证权限和完整性                                                │
│  • 合并到本地上下文                                                │
│  • 查询面试官可用时间                                              │
│  • 安排面试时间                                                    │
│                                                                 │
│  ContextTransfer:                                                │
│  {                                                               │
│    "transferMode": "SELECTIVE",                                  │
│    "includedParts": ["USER", "KNOWLEDGE", "MEMORY"],             │
│    "context": {                                                  │
│      "user": {                                                   │
│        "candidateId": "CAND-001",                                │
│        "interviewTime": "2026-03-15 14:00",                      │
│        "interviewer": "李四",                                     │
│        "interviewType": "技术面试"                                │
│      },                                                          │
│      "knowledge": {                                              │
│        "interviewInfo": {                                        │
│          "location": "会议室A",                                   │
│          "duration": "60分钟",                                    │
│          "meetingLink": "https://meet.example.com/xxx"           │
│        }                                                         │
│      },                                                          │
│      "memory": {                                                 │
│        "previousCommunication": [                                │
│          {"time": "2026-03-10", "content": "初步沟通"}            │
│        ]                                                         │
│      }                                                           │
│    }                                                             │
│  }                                                               │
│                                                                 │
│  Step 3: 候选人沟通 Agent 发送通知                                │
│  ─────────────────────────────────────                          │
│                                                                 │
│  • 接收上下文                                                      │
│  • 生成面试通知邮件                                                │
│  • 发送给候选人                                                    │
│  • 记录沟通历史                                                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 七、最佳实践：如何用好 A2A 上下文管理

### 7.1 选择合适的传递模式

| 场景 | 推荐模式 | 理由 |
|------|---------|------|
| Agent 首次协作 | FULL | 需要完整的上下文信息 |
| 共享存储可用 | REFERENCE | 数据量小，延迟低 |
| 频繁协作 | DELTA | 只传递变化，效率高 |
| 只需特定信息 | SELECTIVE | 灵活控制，默认推荐 |

### 7.2 合理划分上下文部分

```java
// 好的做法：明确指定需要传递的部分
ContextTransfer transfer = transferHandler.prepareTransfer(
    sourceContextId,
    TransferMode.SELECTIVE,
    Set.of(ContextPart.USER, ContextPart.KNOWLEDGE)  // 明确指定
);

// 避免：传递不必要的敏感信息
// ❌ 不要传递 SECURITY_CREDENTIALS
// ❌ 不要传递 INTERNAL_STATE
```

### 7.3 处理上下文冲突

当源上下文和目标上下文有冲突时，选择合适的合并策略：

```java
// SOURCE_PRIORITY：源优先（适合源 Agent 权威的场景）
transferHandler.mergeContext(target, source, MergeStrategy.SOURCE_PRIORITY);

// TARGET_PRIORITY：目标优先（适合目标 Agent 权威的场景）
transferHandler.mergeContext(target, source, MergeStrategy.TARGET_PRIORITY);

// DEEP_MERGE：深度合并（智能合并，适合协作场景）
transferHandler.mergeContext(target, source, MergeStrategy.DEEP_MERGE);
```

### 7.4 监控和日志

```java
// 记录上下文传递日志
log.info("Context transfer: {} -> {}, mode: {}, parts: {}, size: {}KB",
    transfer.getSourceAgentId(),
    transfer.getTargetAgentId(),
    transfer.getTransferMode(),
    transfer.getIncludedParts(),
    transfer.getSerializedContext().length() / 1024
);
```

## 八、未来展望：A2A 的演进方向

### 8.1 智能上下文压缩

使用 LLM 对上下文进行智能摘要，减少传递数据量：

```java
// 智能压缩对话历史
List<Message> compressedHistory = llmService.compressHistory(
    originalHistory,
    CompressionLevel.HIGH  // 高压缩比
);
```

### 8.2 上下文版本管理

支持上下文的版本控制和回滚：

```java
// 保存上下文快照
String snapshotId = contextService.saveSnapshot(context);

// 回滚到指定版本
contextService.rollback(contextId, snapshotId);
```

### 8.3 跨域上下文传递

支持不同组织、不同平台的 Agent 之间的上下文传递：

```java
// 跨域上下文传递
ContextTransfer transfer = transferHandler.prepareCrossDomainTransfer(
    sourceContextId,
    targetDomain,
    TransferMode.SELECTIVE
);
```

## 九、总结

ooderAgent 的 A2A 上下文管理机制，让 Agent 从"孤岛"变成了"协作网络"。通过：

1. **四大传递模式**：FULL、REFERENCE、DELTA、SELECTIVE，灵活应对不同场景
2. **四大上下文部分**：User、Knowledge、Function、Memory，完整覆盖协作所需信息
3. **三层安全机制**：权限验证、敏感信息过滤、大小限制，确保传递安全可靠
4. **智能合并策略**：SOURCE_PRIORITY、TARGET_PRIORITY、DEEP_MERGE，优雅处理冲突

**A2A 上下文管理，让 Agent 协作像人类团队协作一样自然、高效、安全。**

---

**相关文档**：
- [Skills-LLM 体系架构设计](./SKILLS_LLM_ARCHITECTURE.md)
- [A2A 集成适配器](../src/main/java/net/ooder/scene/llm/a2a/A2AIntegrationAdapter.java)
- [上下文传递处理器](../src/main/java/net/ooder/scene/llm/context/ContextTransferHandler.java)

**作者**：Ooder Team  
**发布日期**：2026-03-09  
**标签**：#ooderAgent #A2A #上下文管理 #多Agent协作 #LLM
