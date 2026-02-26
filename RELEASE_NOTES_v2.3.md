# Ooder SDK v2.3 版本发布说明

**发布日期**: 2026-02-24  
**版本号**: 2.3  
**状态**: 正式发布

---

## 一、版本概述

Ooder SDK v2.3 是一个重要的架构升级版本,基于 v0.8.0 升级主计划进行全面重构。本版本主要聚焦于:

1. **代码精简**: 删除约70个冗余文件,减少维护成本
2. **架构统一**: 标准化API接口,提升代码一致性
3. **能力扩展**: 新增AI能力模块,支持AIGC/MCP/工作流
4. **版本统一**: 所有模块版本统一为2.3,简化依赖管理

---

## 二、主要变更

### 2.1 删除冗余代码

#### scene-engine 模块
- **删除** `drivers/` 目录(约50个文件)
  - `drivers/vfs/` - 与 `skill-vfs` 重复
  - `drivers/org/` - 与 `skill-org` 重复
  - `drivers/msg/` - 与 `skill-msg` 重复
  - `drivers/mqtt/` - 与 `skill-mqtt` 重复

#### agent-sdk 模块
- **删除** `net.ooder.sdk.cmd` 包(10个文件)
- **删除** `net.ooder.sdk.msg` 包(6个文件)
- **保留** `net.ooder.sdk.api.cmd` 和 `net.ooder.sdk.api.msg` 作为标准API

### 2.2 新增功能模块

#### skill-ai 模块(全新)
提供统一的AI能力接口:

| 能力 | 说明 |
|------|------|
| `aigc.text-generation` | 文本生成 |
| `aigc.chat` | 对话能力 |
| `mcp.client-management` | MCP客户端管理 |
| `mcp.tool-call` | MCP工具调用 |
| `workflow.execution` | 工作流执行 |
| `workflow.management` | 工作流管理 |

**核心类**:
- `AISkill` / `AISkillImpl` - AI能力接口和实现
- `AIGCResult` / `Message` / `ModelInfo` - AIGC相关模型
- `MCPConfig` / `MCPResult` / `MCPClientInfo` - MCP相关模型
- `WorkflowDefinition` / `WorkflowStep` / `WorkflowResult` - 工作流相关模型

### 2.3 版本统一

所有模块版本统一为 **2.3**:

| 模块 | 旧版本 | 新版本 |
|------|--------|--------|
| scene-engine-parent | 0.8.0 | 2.3 |
| scene-engine | 0.8.0 | 2.3 |
| skill-org | 0.8.0 | 2.3 |
| skill-vfs | 0.8.0 | 2.3 |
| skill-msg | 0.8.0 | 2.3 |
| skill-mqtt | 0.8.0 | 2.3 |
| skill-agent | 0.8.0 | 2.3 |
| skill-security | 0.8.0 | 2.3 |
| skill-business | 0.8.0 | 2.3 |
| skill-ai | - | 2.3 |
| agent-sdk | 0.8.0 | 2.3 |
| llm-sdk | 0.8.0 | 2.3 |

---

## 三、兼容性说明

### 3.1 向后兼容

- **API兼容**: 通过桥接层保持与旧版本API兼容
- **数据兼容**: 数据模型保持不变,自动迁移
- **配置兼容**: 提供配置迁移工具

### 3.2 迁移指南

#### 使用旧包的应用
```java
// 变更前
import net.ooder.sdk.cmd.CmdClientConfig;

// 变更后
import net.ooder.sdk.api.cmd.CmdClientConfig;
```

#### 使用drivers的应用
```java
// 变更前
import net.ooder.scene.drivers.vfs.VfsSkill;

// 变更后
import net.ooder.scene.skills.vfs.VfsSkill;
```

#### 使用AI能力(新增)
```java
// 通过skill-ai使用AIGC能力
AISkill aiSkill = new AISkillImpl();
aiSkill.setLlmProvider(llmProvider);

// 文本生成
AIGCResult result = aiSkill.generateText("gpt-4", "Hello", params).join();

// MCP工具调用
MCPConfig config = new MCPConfig();
config.setClientId("my-client");
config.setTransportType(MCPConfig.TransportType.SSE);
aiSkill.registerMCPClient("my-client", config);
MCPResult result = aiSkill.callMCPTool("my-client", "search", params).join();

// 工作流执行
WorkflowDefinition workflow = new WorkflowDefinition();
workflow.setWorkflowId("my-workflow");
// ... 配置步骤
aiSkill.registerWorkflow(workflow);
WorkflowResult result = aiSkill.executeWorkflow("my-workflow", params).join();
```

---

## 四、系统要求

- **Java版本**: Java 8+
- **Spring Boot**: 2.7.0+
- **Maven**: 3.6+

---

## 五、依赖引用

### Maven

```xml
<!-- scene-engine -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>

<!-- agent-sdk -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>2.3</version>
</dependency>

<!-- llm-sdk -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk</artifactId>
    <version>2.3</version>
</dependency>

<!-- skill-ai -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-ai</artifactId>
    <version>2.3</version>
</dependency>
```

---

## 六、已知问题

暂无

---

## 七、后续计划

### v2.4 计划(1个月内)
- [ ] 完善skill-ai的MCP协议实现
- [ ] 增强工作流引擎
- [ ] 优化性能

### v2.5 计划(3个月内)
- [ ] 完成CAP注册表实现
- [ ] SceneAgent完整实现
- [ ] CommandPacket LLM扩展

---

## 八、问题反馈

如有问题,请联系:
- **GitHub Issues**: https://github.com/oodercn/ooder-sdk/issues
- **邮箱**: team@ooder.net

---

## 九、致谢

感谢所有贡献者和用户的支持!

---

**发布日期**: 2026-02-24  
**文档版本**: 1.0
