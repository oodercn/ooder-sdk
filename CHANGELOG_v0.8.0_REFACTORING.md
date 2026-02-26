# Ooder SDK v0.8.0 重构变更说明

**版本**: 0.8.0  
**日期**: 2026-02-24  
**状态**: 已完成  
**影响范围**: scene-engine, agent-sdk

---

## 一、变更概述

本次重构基于 v0.8.0 升级主计划,旨在:
1. **去除冗余代码**: 删除重复实现,减少维护成本
2. **统一API标准**: 规范接口定义,提升代码一致性
3. **新增AI能力**: 引入 skill-ai 模块,支持 AIGC/MCP/工作流

---

## 二、关联工程

### 2.1 直接修改的工程

| 工程 | 变更类型 | 影响程度 |
|------|----------|----------|
| **scene-engine** | 删除 + 新增 | 高 |
| **agent-sdk** | 删除 | 中 |

### 2.2 依赖关系图

```
┌─────────────────────────────────────────────────────────────┐
│                      scene-engine-parent                     │
│                         (pom管理)                            │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  scene-engine │    │   skill-*     │    │ scene-gateway │
│   (核心引擎)   │◄───│ (能力模块)    │    │   (网关)      │
└───────────────┘    └───────────────┘    └───────────────┘
        ▲                     │
        │                     │
        │    ┌────────────────┼────────────────┐
        │    │                │                │
        │    ▼                ▼                ▼
        │ ┌────────┐    ┌────────┐    ┌──────────────┐
        └─│skill-ai│    │skill-  │    │skill-vfs/msg │
          │(新增)  │    │org/mqtt│    │(已存在)      │
          └────────┘    └────────┘    └──────────────┘
                │
                ▼
        ┌───────────────┐
        │   llm-sdk     │
        │  (LLM能力)    │
        └───────────────┘

┌─────────────────────────────────────────────────────────────┐
│                         agent-sdk                            │
│                     (API标准化)                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、详细变更说明

### 3.1 scene-engine 工程

#### 3.1.1 删除内容 ⚠️

**删除目录**: `scene-engine/drivers/`

| 子模块 | 删除文件数 | 说明 |
|--------|-----------|------|
| `drivers/vfs/` | ~12 | 与 `skill-vfs` 完全重复 |
| `drivers/org/` | ~15 | 与 `skill-org` 完全重复 |
| `drivers/msg/` | ~10 | 与 `skill-msg` 功能重复 |
| `drivers/mqtt/` | ~8 | 与 `skill-mqtt` 完全重复 |
| **合计** | **~50** | 无功能损失 |

**pom.xml 变更**:
```xml
<!-- 删除前 -->
<modules>
    <module>scene-engine</module>
    <module>drivers</module>        <!-- 已删除 -->
    <module>skill-org</module>
    ...
</modules>

<!-- 删除后 -->
<modules>
    <module>scene-engine</module>
    <module>skill-org</module>
    ...
    <module>skill-ai</module>       <!-- 新增 -->
</modules>
```

#### 3.1.2 新增内容 ✨

**新增模块**: `scene-engine/skill-ai/`

| 文件 | 功能 | 说明 |
|------|------|------|
| `AISkill.java` | AI能力接口 | 定义AIGC/MCP/工作流能力 |
| `AISkillImpl.java` | AI能力实现 | 集成LlmProvider,实现SkillService |
| `AIGCResult.java` | AIGC结果 | 文本生成/对话返回结果 |
| `Message.java` | 消息模型 | 对话消息结构 |
| `ModelInfo.java` | 模型信息 | 可用模型元数据 |
| `MCPConfig.java` | MCP配置 | 客户端连接配置 |
| `MCPResult.java` | MCP结果 | 工具调用返回 |
| `MCPClientInfo.java` | MCP客户端信息 | 客户端状态 |
| `WorkflowDefinition.java` | 工作流定义 | 工作流结构 |
| `WorkflowStep.java` | 工作流步骤 | 步骤定义 |
| `WorkflowResult.java` | 工作流结果 | 执行结果 |

**提供的能力列表**:
```java
- aigc.text-generation    // 文本生成
- aigc.chat               // 对话能力
- mcp.client-management   // MCP客户端管理
- mcp.tool-call          // MCP工具调用
- workflow.execution     // 工作流执行
- workflow.management    // 工作流管理
```

---

### 3.2 agent-sdk 工程

#### 3.2.1 删除内容 ⚠️

**删除包**: `net.ooder.sdk.cmd` 和 `net.ooder.sdk.msg`

| 包路径 | 删除类 | 替代方案 |
|--------|--------|----------|
| `net.ooder.sdk.cmd` | CmdClientConfig | 使用 `net.ooder.sdk.api.cmd.CmdClientConfig` |
| `net.ooder.sdk.cmd` | CmdClientProxy | 使用 `net.ooder.sdk.api.cmd.CmdClientProxy` |
| `net.ooder.sdk.cmd` | Command/CommandBuilder等 | 使用 `net.ooder.sdk.api.protocol.*` |
| `net.ooder.sdk.msg` | MsgClientConfig | 使用 `net.ooder.sdk.api.msg.MsgClientConfig` |
| `net.ooder.sdk.msg` | MsgClientProxy | 使用 `net.ooder.sdk.api.msg.MsgClientProxy` |
| `net.ooder.sdk.msg` | MsgListener等 | 使用 `net.ooder.sdk.api.msg.*` |

**删除文件数**: 16 个 Java 文件

---

## 四、对协同团队的影响

### 4.1 需要关注的团队

| 团队 | 影响 | 建议操作 |
|------|------|----------|
| **AI/LLM团队** | 高 | 使用新的 `skill-ai` 模块,迁移现有AIGC调用 |
| **MCP团队** | 高 | 通过 `AISkill.registerMCPClient()` 注册客户端 |
| **工作流团队** | 高 | 使用 `AISkill.executeWorkflow()` 执行工作流 |
| **Org/VFS团队** | 中 | 确认不再使用 `drivers/` 下的类 |
| **Msg团队** | 中 | 确认不再使用 `net.ooder.sdk.msg.*` 旧包 |
| **Cmd团队** | 中 | 确认不再使用 `net.ooder.sdk.cmd.*` 旧包 |

### 4.2 代码迁移指南

#### 场景1: 使用 drivers/ 的代码

**变更前**:
```java
import net.ooder.scene.drivers.vfs.VfsSkill;
import net.ooder.scene.drivers.org.OrgSkill;

VfsSkill vfs = new VfsSkill();
OrgSkill org = new OrgSkill();
```

**变更后**:
```java
import net.ooder.scene.skills.vfs.VfsSkill;
import net.ooder.scene.skills.org.OrgSkill;

VfsSkill vfs = new VfsSkill();
OrgSkill org = new OrgSkill();
```

#### 场景2: 使用旧 cmd/msg 包的代码

**变更前**:
```java
import net.ooder.sdk.cmd.CmdClientConfig;
import net.ooder.sdk.msg.MsgClientConfig;
```

**变更后**:
```java
import net.ooder.sdk.api.cmd.CmdClientConfig;
import net.ooder.sdk.api.msg.MsgClientConfig;
```

#### 场景3: 使用 AIGC 能力的代码

**变更前**:
```java
// 直接调用 llm-sdk
LlmProvider provider = ...;
String result = provider.complete(modelId, prompt, params);
```

**变更后**:
```java
// 通过 skill-ai 调用
AISkill aiSkill = new AISkillImpl();
aiSkill.setLlmProvider(provider);

AIGCResult result = aiSkill.generateText(modelId, prompt, params).join();
String content = result.getContent();
```

#### 场景4: 使用 MCP 的代码

**新功能**:
```java
AISkill aiSkill = ...;

// 注册 MCP 客户端
MCPConfig config = new MCPConfig();
config.setClientId("my-client");
config.setTransportType(MCPConfig.TransportType.SSE);
config.setUrl("http://localhost:3000/sse");

aiSkill.registerMCPClient("my-client", config);

// 调用 MCP 工具
Map<String, Object> params = new HashMap<>();
params.put("query", "Hello");
MCPResult result = aiSkill.callMCPTool("my-client", "search", params).join();
```

#### 场景5: 使用工作流的代码

**新功能**:
```java
AISkill aiSkill = ...;

// 定义工作流
WorkflowDefinition workflow = new WorkflowDefinition();
workflow.setWorkflowId("data-processing");
workflow.setName("数据处理工作流");

List<WorkflowStep> steps = new ArrayList<>();
WorkflowStep step1 = new WorkflowStep();
step1.setStepId("step1");
step1.setType(WorkflowStep.StepType.AI_GENERATION);
step1.setRef("gpt-4");
steps.add(step1);

workflow.setSteps(steps);

// 注册并执行
aiSkill.registerWorkflow(workflow);
WorkflowResult result = aiSkill.executeWorkflow("data-processing", params).join();
```

---

## 五、编译与验证

### 5.1 编译命令

```bash
# 编译整个 scene-engine
cd scene-engine
mvn clean compile -DskipTests

# 仅编译 skill-ai
mvn clean compile -DskipTests -pl skill-ai -am
```

### 5.2 验证清单

- [ ] scene-engine 编译通过
- [ ] agent-sdk 编译通过
- [ ] skill-ai 编译通过
- [ ] 无引用 drivers/ 的代码
- [ ] 无引用 net.ooder.sdk.cmd/msg 的代码
- [ ] 集成测试通过

---

## 六、后续计划

### 6.1 短期(1-2周)
- [ ] 各团队完成代码迁移
- [ ] 更新开发文档
- [ ] 补充单元测试

### 6.2 中期(1个月)
- [ ] ooder-common 业务逻辑迁移到 skill-* 模块
- [ ] 完善 skill-ai 的 MCP 协议实现
- [ ] 工作流引擎增强

### 6.3 长期(3个月)
- [ ] 完成 CAP 注册表实现
- [ ] SceneAgent 完整实现
- [ ] CommandPacket LLM 扩展

---

## 七、问题反馈

如有问题,请联系:
- **架构组**: 负责整体架构设计
- **SDK组**: 负责 SDK 开发和维护
- **AI组**: 负责 skill-ai 模块

---

**文档版本**: 1.0  
**最后更新**: 2026-02-24
