# Ooder Agent SDK 2.3

[![Maven Central](https://img.shields.io/badge/Maven%20Central-v2.3-blue)](https://central.sonatype.com/artifact/net.ooder/agent-sdk)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.java.com/)

## 简介

Ooder Agent SDK 是一个面向南向协议实现的轻量级 Agent SDK，提供完整的 Agent 生命周期管理、能力编排、场景管理和协议适配功能。

## 架构特点

### 1. 模块化设计

```
agent-sdk (父工程)
├── agent-sdk-api          # API 接口和模型定义
├── llm-sdk-api           # LLM 轻量级 API
├── skills-framework      # 技能框架
└── agent-sdk-core        # 核心实现
    └── 依赖外部 llm-sdk (完整实现)
```

### 2. 依赖关系

```
agent-sdk-api (无依赖)
    ↑
    ├── llm-sdk-api → agent-sdk-api
    ├── skills-framework → agent-sdk-api
    └── agent-sdk-core → api + llm-sdk-api + skills-framework + 外部llm-sdk
```

### 3. 核心功能

- **Agent 管理**: 完整的 Agent 生命周期管理
- **能力编排**: Story/Will 编排引擎
- **场景管理**: Scene 和 SceneGroup 管理
- **协议适配**: A2A、REACH 南向协议支持
- **技能框架**: 技能加载、生成和运行时支持
- **LLM 集成**: 通过外部 llm-sdk 提供完整 LLM 能力

## 快速开始

### Maven 依赖

```xml
<!-- 完整 SDK -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>

<!-- 或仅使用 API -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

### 基础使用

```java
import net.ooder.sdk.api.OoderSDK;

public class AgentApplication {
    public static void main(String[] args) {
        // 创建 SDK 实例
        OoderSDK sdk = OoderSDK.builder()
            .agentId("my-agent")
            .agentName("My Agent")
            .build();
        
        // 启动 Agent
        sdk.start();
        
        // 使用能力
        CapabilityResult result = sdk.getCapabilityInvoker()
            .invoke("capability-id", params);
    }
}
```

## 模块说明

### agent-sdk-api

核心 API 接口和模型定义，包括：
- Agent 接口定义
- 能力 (Capability) 接口
- 场景 (Scene) 接口
- 命令 (Command) 接口
- 事件 (Event) 接口
- 连接测试 (ConnectionTestService) 接口（v2.3新增）

### llm-sdk-api

LLM 轻量级 API，提供：
- LLM 服务接口
- 聊天请求/响应模型
- 函数定义模型

### skills-framework

技能框架，提供：
- 技能加载机制
- 技能代码生成
- 运行时支持

### agent-sdk-core

核心实现，包括：
- Agent 实现
- 能力编排引擎
- 协议适配器 (A2A, REACH)
- 南向协议闭环实现

## 工程结构

### 内部模块

```
agent-sdk/
├── agent-sdk-api          # API 接口和模型定义
├── llm-sdk-api           # LLM 轻量级 API
├── llm-sdk               # LLM 完整实现 (已合并)
├── skills-framework      # 技能框架
└── agent-sdk-core        # 核心实现
```

### llm-sdk (内部模块)

位于 `agent-sdk/llm-sdk`，提供：
- 完整 LLM 驱动实现 (BaiduWenxin, Spark, Mock)
- Story/Will 完整实现
- Memory 管理
- NLP 处理
- 多 LLM 适配和调度

**依赖关系**: agent-sdk-core → llm-sdk

### scene-engine (外部工程)

位于 `E:\github\ooder-sdk\scene-engine`，提供：
- 完整 Scene 引擎实现
- 安全、审计功能
- 多种 Skill 实现

**依赖关系**: scene-engine → agent-sdk (单向)

## 版本历史

### 2.3.0 (当前版本)

- **泛型化改造** - 核心 API 全面支持泛型
  - 24 个核心类已泛型化
  - 提供类型安全的 API
  - 向后兼容
- 重构模块化结构
- 分离 llm-sdk-api (轻量级) 和 llm-sdk (完整版)
- 移除 scene-engine 模块 (移至外部工程)
- 统一版本号为 2.3
- 清理重复代码
- **新增连接测试接口** - ConnectionTestService
  - 支持能力端点连接测试
  - 支持 HTTP/TCP 端点测试

### 0.7.3 (历史版本)

- 初始版本
- 包含 scene-engine 和 llm-sdk 作为内部模块

## 泛型化 API

### 概述

从 2.3.0 版本开始，Ooder Agent SDK 的核心 API 已全面支持泛型，提供类型安全的编程体验。

### 已泛型化的核心类

| 类/接口 | 泛型参数 | 说明 |
|---------|----------|------|
| `StorageService<T>` | T - 存储类型 | 类型安全的存储服务 |
| `CapabilityRouter<P, D>` | P - 参数类型, D - 结果类型 | 泛型能力路由 |
| `SecurityService<C>` | C - 声明类型 | 泛型安全服务 |
| `OrchestrationResult<T>` | T - 结果类型 | 泛型编排结果 |
| `StepExecutionRecord<R>` | R - 记录类型 | 泛型执行记录 |
| `A2AMessage<T>` | T - 数据类型 | 泛型 A2A 消息 |
| `NlpInteractionApi<M>` | M - 元数据类型 | 泛型 NLP 接口 |
| `SceneConfig<P>` | P - 属性类型 | 泛型场景配置 |
| `LinkInfo<M>` | M - 元数据类型 | 泛型链接信息 |
| `TokenInfo<C>` | C - 声明类型 | 泛型 Token 信息 |
| `ConfigUpdateMessage<C>` | C - 配置类型 | 泛型配置更新消息 |
| `ChatRequest<P>` | P - 参数类型 | 泛型聊天请求 |
| `TaskSendMessage<P>` | P - 参数类型 | 泛型任务发送消息 |
| `CapabilityDeclaration<M, D>` | M - 元数据类型, D - 默认值类型 | 泛型能力声明 |
| `SceneStore<C>` | C - 配置类型 | 泛型场景存储 |

### 使用示例

```java
// StorageService 泛型使用
StorageService<User> storage = ...;
Optional<User> user = storage.load("user:001", User.class);
Map<String, User> users = storage.loadBatch(keys, User.class);

// OrchestrationResult 泛型使用
OrchestrationResult<Order> result = orchestrator.orchestrate(story);
Order order = result.getFinalResult(); // 无需强制类型转换

// A2AMessage 泛型使用
A2AMessage<TaskRequest> message = A2AMessage.taskSend("skill-001", taskRequest);
TaskRequest request = message.getData();

// 向后兼容
A2AMessage<Map<String, Object>> generic = A2AMessage.createGeneric();
```

### 迁移指南

1. **新代码**: 直接使用泛型版本
2. **现有代码**: 使用 `Object` 作为类型参数保持兼容
   ```java
   StorageService<Object> storage = ...;
   ChatRequest<Object> request = ChatRequest.createGeneric();
   ```

## 构建

```bash
# 编译
mvn clean compile

# 打包
mvn clean package

# 安装到本地仓库
mvn clean install

# 跳过测试
mvn clean install -DskipTests
```

## 文档

- [架构设计](docs/architecture/OVERALL_ARCHITECTURE.md)
- [核心抽象层](docs/architecture/CORE_ABSTRACTION_LAYER.md)
- [南北向架构](docs/architecture/NORTHBOUND_SOUTHBOUND_ARCHITECTURE.md)
- [快速开始](docs/guides/QUICK_START.md)

## 许可证

MIT License

## 作者

- IhyTdX (18683731@qq.com)

## 相关链接

- [GitHub](https://github.com/oodercn/super-Agent)
- [Maven Central](https://central.sonatype.com/artifact/net.ooder/agent-sdk)
