# Scene Engine 迁移到 Agent SDK 2.3.1 指南

## 概述

Agent SDK 2.3.1 进行了重大重构，移除了大量旧 API。本文档提供迁移指南。

## 主要变化

### 1. 移除的 API

以下包和类在 agent-sdk 2.3.1 中已移除：

- `net.ooder.sdk.memory.*` - 内存管理 API
- `net.ooder.sdk.api.capability.*` - 能力注册 API
- `net.ooder.sdk.api.scene.*` - 场景组 API
- `net.ooder.sdk.api.agent.*` - Agent API
- `net.ooder.sdk.core.capability.*` - 核心能力 API
- `net.ooder.sdk.service.storage.*` - 存储服务 API
- `net.ooder.sdk.discovery.git.*` - Git 发现 API
- `net.ooder.sdk.nexus.resource.*` - Nexus 资源 API
- `net.ooder.sdk.api.connection.*` - 连接测试 API
- `net.ooder.sdk.southbound.protocol.*` - 协议 API

### 2. 新 API 结构

Agent SDK 2.3.1 提供了新的 API 结构：

```
net.ooder.sdk.llm.*              - LLM 核心 API
net.ooder.sdk.drivers.llm.*      - LLM 驱动 API
net.ooder.sdk.llm.pool.*         - 连接池 API
net.ooder.sdk.llm.tool.*         - 工具调用 API
net.ooder.sdk.llm.scene.*        - 场景上下文 API
net.ooder.sdk.llm.model.*        - 模型类
net.ooder.sdk.llm.service.*      - 服务实现
```

### 3. 主要 API 映射

| 旧 API | 新 API | 说明 |
|--------|--------|------|
| `net.ooder.sdk.service.llm.LlmConfig` | `net.ooder.sdk.drivers.llm.LlmDriver.LlmConfig` | 配置类 |
| `net.ooder.sdk.memory.ConversationMemory` | `net.ooder.sdk.llm.scene.SceneContext` | 会话上下文 |
| `net.ooder.sdk.api.capability.Capability` | `net.ooder.sdk.llm.tool.ToolDefinition` | 能力/工具定义 |
| `net.ooder.sdk.api.scene.SceneGroup` | `net.ooder.sdk.llm.scene.SceneContext` | 场景组 |
| `net.ooder.sdk.api.agent.Agent` | `net.ooder.sdk.llm.model.AgentInfo` | Agent 信息 |

## 迁移步骤

### 步骤 1: 更新依赖

在 pom.xml 中确保使用 agent-sdk 2.3.1：

```xml
<properties>
    <agent-sdk.version>2.3.1</agent-sdk.version>
</properties>

<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-core</artifactId>
        <version>${agent-sdk.version}</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>${agent-sdk.version}</version>
    </dependency>
</dependencies>
```

### 步骤 2: 替换导入语句

批量替换导入语句：

```bash
# 替换 LlmConfig
find . -name "*.java" -exec sed -i 's/import net\.ooder\.sdk\.service\.llm\.LlmConfig;/import net.ooder.sdk.drivers.llm.LlmDriver.LlmConfig;/g' {} \;

# 替换其他类...
```

### 步骤 3: 创建适配类

对于已移除的 API，在 scene-engine 中创建适配类：

1. **ConversationMemory** → 使用 `SceneContext`
2. **Capability** → 使用 `ToolDefinition`
3. **SceneGroup** → 使用 `SceneContext`

### 步骤 4: 修改业务逻辑

根据新 API 修改业务逻辑：

#### LLM 调用

旧代码：
```java
LlmConfig config = new LlmConfig();
config.setEndpoint("https://api.openai.com/v1");
config.setModel("gpt-4");
```

新代码：
```java
LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
config.setProvider("openai");
config.setDefaultModel("gpt-4");
```

#### 工具调用

旧代码：
```java
Capability cap = new Capability();
cap.setId("search");
cap.setName("搜索");
```

新代码：
```java
ToolDefinition tool = new ToolDefinition();
tool.setName("search");
tool.setDescription("搜索工具");
```

## 已完成的修改

### 已修改的文件

1. **pom.xml** - 版本更新为 2.3.1
2. **SceneEngine.java** - 添加缺失的方法
3. **SceneClient.java** - 简化接口
4. **SceneClientImpl.java** - 使用新 API
5. **LlmConfig.java** - 创建 scene-engine 自己的配置类
6. **LlmConnectionManager.java** - 修改导入
7. **LlmConnectionPoolKey.java** - 修改导入
8. **LlmConnectionPool.java** - 修改导入
9. **UserLlmSessionManager.java** - 修改导入
10. **AgentSessionManager.java** - 修改导入
11. **AgentLlmSessionContext.java** - 修改导入

### 已创建的类

1. `net.ooder.scene.llm.config.LlmConfig`
2. `net.ooder.scene.core.SceneEngineConfig`
3. `net.ooder.scene.core.SkillQuery`
4. `net.ooder.scene.provider.SceneProvider`
5. `net.ooder.scene.provider.UserSettingsProvider`
6. `net.ooder.scene.provider.HeartbeatProvider`
7. `net.ooder.scene.skill.SkillService`
8. `net.ooder.scene.skill.tool.Tool`
9. `net.ooder.scene.skill.tool.ToolContext`
10. `net.ooder.scene.skill.tool.ToolResult`
11. `net.ooder.scene.skill.llm.FunctionCall`
12. `net.ooder.scene.skill.contribution.Contribution`
13. `net.ooder.scene.skill.contribution.ContributionStats`

## 待完成的修改

### 需要重构的模块

1. **LLM 代理模块** (`net.ooder.scene.ll