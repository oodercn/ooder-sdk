# Skills-LLM 体系需求覆盖度报告

## 一、需求概述

根据 Skills-LLM 体系架构设计，核心需求包括：

1. **Function Calling 注入**：激活 Skill 时将函数定义注入到上下文
2. **知识库多级加载**：skills.md 支持 BASIC/ADVANCED/EXPERT/FULL 四级加载
3. **页面上下文重组**：角色 + 知识库 + Skill + 记忆 自动组装运行时上下文
4. **A2A 上下文传递**：定义默认上下文传递规则

## 二、实现状态检查

### 2.1 SkillActivationContext - Skill 激活上下文

| 检查项 | 状态 | 文件 |
|--------|------|------|
| 类定义 | ✅ 已实现 | [SkillActivationContext.java](../src/main/java/net/ooder/scene/llm/context/SkillActivationContext.java) |
| activate() 方法 | ✅ 已实现 | 静态工厂方法 |
| buildSystemPrompt() 方法 | ✅ 已实现 | 组装角色+知识提示词 |
| getTools() 方法 | ✅ 已实现 | 获取函数定义 |
| executeFunction() 方法 | ✅ 已实现 | 执行函数调用 |
| 消息历史管理 | ✅ 已实现 | addUserMessage/addAssistantMessage |

**实现详情**：
```java
// 激活 Skill
SkillActivationContext context = SkillActivationContext.activate(
    ActivationRequest.builder()
        .skillId("recruitment-skill")
        .userId("user-xxx")
        .roleId("hr-assistant")
        .build()
);

// 获取函数定义
List<Map<String, Object>> tools = context.getTools();

// 获取系统提示词
String prompt = context.buildSystemPrompt();
```

---

### 2.2 FunctionContext - 函数定义上下文

| 检查项 | 状态 | 文件 |
|--------|------|------|
| 类定义 | ✅ 已实现 | [FunctionContext.java](../src/main/java/net/ooder/scene/llm/context/FunctionContext.java) |
| loadFromSkill() 方法 | ✅ 已实现 | 从 Skill 元数据加载 |
| toTools() 方法 | ✅ 已实现 | 转换为 LLM Tools 格式 |
| execute() 方法 | ✅ 已实现 | 执行函数调用 |
| Capability 映射 | ✅ 已实现 | capabilityMappings |
| FunctionDefinition 内部类 | ✅ 已实现 | 函数定义结构 |
| ParameterDefinition 内部类 | ✅ 已实现 | 参数定义结构 |
| FunctionExecutor 接口 | ✅ 已实现 | 函数执行器接口 |

**实现详情**：
```java
// 从 Skill 加载函数定义
FunctionContext context = FunctionContext.loadFromSkill("recruitment-skill");

// 获取 LLM Tools 格式
List<Map<String, Object>> tools = context.toTools();

// 注册执行器
context.registerExecutor("scan_resume", (args, ctx) -> {
    return skillService.invokeCapability(ctx.getSkillId(), "resume_scan", args);
});

// 执行函数
Object result = context.execute("scan_resume", args, activationContext);
```

---

### 2.3 KnowledgeContext - 知识库上下文

| 检查项 | 状态 | 文件 |
|--------|------|------|
| 类定义 | ✅ 已实现 | [KnowledgeContext.java](../src/main/java/net/ooder/scene/llm/context/KnowledgeContext.java) |
| KnowledgeLoadLevel 枚举 | ✅ 已实现 | BASIC/ADVANCED/EXPERT/FULL |
| KnowledgeChunk 内部类 | ✅ 已实现 | 知识块结构 |
| buildPromptSection() 方法 | ✅ 已实现 | 构建知识提示词 |
| addChunk() 方法 | ✅ 已实现 | 添加知识块 |
| RAG 支持 | ✅ 已实现 | ragIndexId 字段 |
| Builder 模式 | ✅ 已实现 | builder() 方法 |

**实现详情**：
```java
// 四级加载
public enum KnowledgeLoadLevel {
    BASIC(1, "基础知识", 2048),      // ~2K tokens
    ADVANCED(2, "进阶知识", 4096),    // ~4K tokens
    EXPERT(3, "专家知识", 8192),      // ~8K tokens
    FULL(4, "完整知识", -1);          // 无限制
}

// 构建知识提示词
String prompt = knowledgeContext.buildPromptSection();
```

---

### 2.4 RoleContext - 角色上下文

| 检查项 | 状态 | 文件 |
|--------|------|------|
| 类定义 | ✅ 已实现 | [RoleContext.java](../src/main/java/net/ooder/scene/llm/context/RoleContext.java) |
| 预定义角色 | ✅ 已实现 | assistant, hr-assistant |
| buildPromptSection() 方法 | ✅ 已实现 | 构建角色提示词 |
| load() 方法 | ✅ 已实现 | 加载角色 |
| defaultRole() 方法 | ✅ 已实现 | 获取默认角色 |

**实现详情**：
```java
// 加载角色
RoleContext role = RoleContext.load("hr-assistant");

// 构建提示词
String prompt = role.buildPromptSection();
// 输出:
// # 角色定义
// 你是招聘场景的智能助手...
// ## 行为准则
// - 熟悉招聘流程和最佳实践
// - 保护候选人隐私信息
```

---

### 2.5 MemoryContext - 记忆上下文

| 检查项 | 状态 | 文件 |
|--------|------|------|
| 类定义 | ✅ 已实现 | [MemoryContext.java](../src/main/java/net/ooder/scene/llm/context/MemoryContext.java) |
| addMessage() 方法 | ✅ 已实现 | 添加消息 |
| getHistory() 方法 | ✅ 已实现 | 获取历史 |
| getRecentHistory() 方法 | ✅ 已实现 | 获取最近 N 条 |
| 工具调用支持 | ✅ 已实现 | addAssistantMessageWithTools |
| 工具结果支持 | ✅ 已实现 | addToolResultMessage |
| 历史长度限制 | ✅ 已实现 | maxHistoryLength |

**实现详情**：
```java
MemoryContext memory = new MemoryContext("session-xxx");

// 添加消息
memory.addMessage("user", "请帮我扫描简历");
memory.addMessage("assistant", "好的，我来处理");

// 获取历史
List<Map<String, Object>> history = memory.getHistory();
```

---

### 2.6 A2A ContextTransfer - 上下文传递

| 检查项 | 状态 | 文件 |
|--------|------|------|
| ContextTransfer 类 | ✅ 已存在 | [ContextTransfer.java](../src/main/java/net/ooder/scene/llm/command/ContextTransfer.java) |
| ContextTransferHandler 类 | ✅ 已存在 | [ContextTransferHandler.java](../src/main/java/net/ooder/scene/llm/context/ContextTransferHandler.java) |
| TransferMode 枚举 | ✅ 已存在 | FULL/REFERENCE/DELTA/SELECTIVE |
| ContextPart 枚举 | ✅ 已存在 | USER/NLP/KNOWLEDGE/SECURITY/... |
| prepareTransfer() 方法 | ✅ 已存在 | 准备传递 |
| receiveTransfer() 方法 | ✅ 已存在 | 接收传递 |
| mergeContext() 方法 | ✅ 已存在 | 合并上下文 |
| MergeStrategy 枚举 | ✅ 已存在 | SOURCE_PRIORITY/TARGET_PRIORITY/DEEP_MERGE |

**实现详情**：
```java
// 准备传递
ContextTransfer transfer = transferHandler.prepareTransfer(
    sourceContext,
    TransferMode.SELECTIVE,
    Set.of(ContextPart.USER_CONTEXT, ContextPart.KNOWLEDGE_CONTEXT)
);

// 接收传递
LlmSceneContext targetContext = transferHandler.receiveTransfer(
    transfer,
    targetSceneId
);

// 合并上下文
transferHandler.mergeContext(targetContext, sourceContext, MergeStrategy.DEEP_MERGE);
```

---

## 三、需求覆盖度汇总

| 需求 | 子需求 | 实现状态 | 覆盖度 |
|------|--------|----------|--------|
| **1. Function Calling 注入** | | | **100%** |
| | SkillActivationContext | ✅ 已实现 | 100% |
| | FunctionContext | ✅ 已实现 | 100% |
| | toTools() 方法 | ✅ 已实现 | 100% |
| | execute() 方法 | ✅ 已实现 | 100% |
| **2. 知识库多级加载** | | | **100%** |
| | KnowledgeContext | ✅ 已实现 | 100% |
| | KnowledgeLoadLevel 枚举 | ✅ 已实现 | 100% |
| | KnowledgeChunk 内部类 | ✅ 已实现 | 100% |
| | buildPromptSection() | ✅ 已实现 | 100% |
| **3. 页面上下文重组** | | | **100%** |
| | RoleContext | ✅ 已实现 | 100% |
| | MemoryContext | ✅ 已实现 | 100% |
| | buildSystemPrompt() | ✅ 已实现 | 100% |
| | getTools() | ✅ 已实现 | 100% |
| **4. A2A 上下文传递** | | | **100%** |
| | ContextTransfer | ✅ 已存在 | 100% |
| | ContextTransferHandler | ✅ 已存在 | 100% |
| | TransferMode 枚举 | ✅ 已存在 | 100% |
| | mergeContext() | ✅ 已存在 | 100% |

---

## 四、新增实现文件

| 文件 | 说明 | 状态 |
|------|------|------|
| [SkillActivationContext.java](../src/main/java/net/ooder/scene/llm/context/SkillActivationContext.java) | Skill 激活上下文 | ✅ 新增 |
| [FunctionContext.java](../src/main/java/net/ooder/scene/llm/context/FunctionContext.java) | 函数定义上下文 | ✅ 新增 |
| [RoleContext.java](../src/main/java/net/ooder/scene/llm/context/RoleContext.java) | 角色上下文 | ✅ 新增 |
| [MemoryContext.java](../src/main/java/net/ooder/scene/llm/context/MemoryContext.java) | 记忆上下文 | ✅ 新增 |
| [KnowledgeContext.java](../src/main/java/net/ooder/scene/llm/context/KnowledgeContext.java) | 知识库上下文（增强） | ✅ 更新 |

---

## 五、闭环验证

### 5.1 激活 Skill 流程验证

```
用户请求激活 Skill
    │
    ├── SkillActivationContext.activate(request)
    │   │
    │   ├── loadRoleContext(roleId) → RoleContext
    │   ├── loadKnowledgeContext(skillId, kbIds) → KnowledgeContext
    │   ├── loadFunctionContext(skillId) → FunctionContext
    │   └── loadMemoryContext(sessionId) → MemoryContext
    │
    └── 返回 SkillActivationContext
        │
        ├── buildSystemPrompt() → 角色定义 + 知识内容
        ├── getTools() → 函数定义列表
        └── getMessages() → 对话历史
```

### 5.2 函数调用流程验证

```
LLM 返回 function_call
    │
    ├── SkillActivationContext.executeFunction(name, args)
    │   │
    │   └── FunctionContext.execute(name, args, context)
    │       │
    │       ├── 查找 executor
    │       ├── 查找 capability 映射
    │       └── 执行 executor.execute(args, context)
    │
    └── 返回执行结果
```

### 5.3 A2A 上下文传递流程验证

```
Agent A 发起传递
    │
    ├── ContextTransferHandler.prepareTransfer(source, mode, parts)
    │   │
    │   ├── 选择传递模式
    │   ├── 过滤上下文部分
    │   └── 序列化上下文
    │
    └── 返回 ContextTransfer
        │
        └── A2A 网络传输
            │
            └── Agent B 接收
                │
                ├── ContextTransferHandler.receiveTransfer(transfer, sceneId)
                │   │
                │   ├── 验证权限
                │   ├── 反序列化
                │   └── 注册上下文
                │
                └── 返回 LlmSceneContext
```

---

## 六、结论

**需求覆盖度：100%**

所有核心需求均已实现：

1. ✅ Function Calling 注入机制 - SkillActivationContext + FunctionContext
2. ✅ 知识库多级加载 - KnowledgeContext + KnowledgeLoadLevel
3. ✅ 页面上下文重组 - RoleContext + MemoryContext + buildSystemPrompt()
4. ✅ A2A 上下文传递 - ContextTransfer + ContextTransferHandler

---

**报告生成时间**: 2026-03-09  
**报告维护**: Ooder Team
