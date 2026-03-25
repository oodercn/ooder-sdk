# Ooder Agent SDK 3.0.0

[![Maven Central](https://img.shields.io/badge/Maven%20Central-v3.0.0-blue)](https://central.sonatype.com/artifact/net.ooder/agent-sdk)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)](https://spring.io/projects/spring-boot)

> **重要变更**: 3.0.0 版本已升级到 JDK 21 和 Spring Boot 3.x，并新增 Spring Boot Starter 支持。

## 简介

Ooder Agent SDK 是一个面向南向协议实现的轻量级 Agent SDK，提供完整的 Agent 生命周期管理、能力编排、场景管理和协议适配功能。

## 架构特点

### 1. 模块化设计

```
agent-sdk (父工程) 3.0.0
├── llm-sdk                              # LLM SDK
├── skills-framework                     # 技能框架
├── agent-sdk-core                       # 核心实现
└── agent-sdk-spring-boot-starter        # Spring Boot Starter (新增)
```

### 2. 核心功能

- **Agent 管理**: 完整的 Agent 生命周期管理
- **能力编排**: Story/Will 编排引擎
- **场景管理**: Scene 和 SceneGroup 管理
- **协议适配**: A2A、REACH 南向协议支持
- **技能框架**: 技能加载、生成和运行时支持
- **LLM 集成**: 结构化输出、工具调用增强、激活引导、Token配额管理
- **负载均衡**: 内置负载均衡和故障转移服务

### 3. 公共 API

所有带有 `@PublicAPI` 注解的类都是 SDK 的公共 API，保证向后兼容：

- `OoderSDK` - SDK 入口类
- `AgentFactoryImpl` - Agent 工厂实现
- `WorkerAgentImpl` - Worker Agent 实现
- `SceneAgentImpl` - Scene Agent 实现
- `EndAgentImpl` - End Agent 实现
- `RouteAgentImpl` - Route Agent 实现
- `McpAgentImpl` - MCP Agent 实现

## 快速开始

### Spring Boot 集成 (推荐)

#### 1. 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-spring-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

#### 2. 配置 application.yml

```yaml
ooder:
  sdk:
    enabled: true
    agent-id: my-agent
    agent-name: My Agent
    agent-type: WORKER
    endpoint: http://localhost:8080
    discovery-enabled: true
    heartbeat-interval: 30000
```

#### 3. 使用 SDK

```java
@Autowired
private OoderSDK ooderSDK;

public void executeTask() {
    // 创建 Agent
    WorkerAgent worker = ooderSDK.getAgentFactory()
        .createWorkerAgent("scene-001", "worker-1", "skill-001");
    
    // 执行任务
    worker.execute("capability-id", params);
}
```

### 直接使用 SDK

#### Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>3.0.0</version>
</dependency>
```

#### 基础使用

```java
import net.ooder.sdk.api.OoderSDK;
import net.ooder.sdk.api.agent.WorkerAgent;

public class AgentApplication {
    public static void main(String[] args) {
        // 创建 SDK 实例
        OoderSDK sdk = OoderSDK.builder()
            .agentId("my-agent")
            .agentName("My Agent")
            .build();
        
        // 创建 Worker Agent
        WorkerAgent worker = sdk.getAgentFactory()
            .createWorkerAgent("scene-001", "worker-1", "skill-001");
        
        // 执行能力
        worker.execute("capability-id", params);
    }
}
```

## 模块说明

### llm-sdk

LLM SDK 模块，提供：
- 结构化输出支持 (`StructuredOutputApi`)
- 工具调用增强 (`ToolCallingApi`)
- 激活引导能力 (`ActivationGuidanceService`)
- Token 配额管理 (`TokenQuotaService`)
- 多 LLM 驱动适配

### skills-framework

技能框架，提供：
- 技能加载机制
- 技能代码生成
- 运行时支持

### agent-sdk-core

核心实现，包括：
- Agent 实现 (Worker, Scene, End, Route, Mcp)
- 能力编排引擎
- 协议适配器 (A2A, REACH)
- 负载均衡和故障转移
- 消息队列服务

### agent-sdk-spring-boot-starter

Spring Boot Starter，提供：
- 自动配置
- 属性绑定
- 开箱即用

## 抽象基类

SDK 提供以下抽象基类，方便扩展：

| 基类 | 包路径 | 用途 |
|------|--------|------|
| `AbstractAgent` | `net.ooder.sdk.api.agent.support` | Agent 基础实现 |
| `AbstractWorkerAgent` | `net.ooder.sdk.api.agent.support` | Worker Agent 基础实现 |
| `AbstractSceneAgent` | `net.ooder.sdk.api.agent.support` | Scene Agent 基础实现 |

## 技术栈

| 组件 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.4.1 |
| fastjson2 | 2.0.52 |
| Jackson | 2.18.2 |
| Lombok | 1.18.36 |
| JUnit | 5.11.4 |

## 构建

```bash
# 编译
mvn clean compile

# 打包
mvn clean package

# 安装到本地仓库
mvn clean install -DskipTests

# 发布到 Maven Central
mvn clean deploy -DskipTests
```

## 文档

- [架构设计](docs/architecture/OVERALL_ARCHITECTURE.md)
- [核心抽象层](docs/architecture/CORE_ABSTRACTION_LAYER.md)
- [南北向架构](docs/architecture/NORTHBOUND_SOUTHBOUND_ARCHITECTURE.md)
- [快速开始](docs/guides/QUICK_START.md)

## 版本历史

### 3.0.0 (当前版本)

- **JDK 21 升级** - 支持 JDK 21 新特性
- **Spring Boot 3.x 升级** - 兼容 Spring Boot 3.4.1
- **fastjson2 升级** - 从 fastjson 1.x 升级到 fastjson2
- **新增 Spring Boot Starter** - 开箱即用的 Spring Boot 集成
- **公共 API 标识** - 添加 `@PublicAPI` 注解
- **抽象基类** - 提供 `AbstractAgent`、`AbstractWorkerAgent`、`AbstractSceneAgent`
- **Agent 协作增强** - 批量上下文传递、路由增强、消息队列完善
- **负载均衡** - 内置负载均衡和故障转移服务

### 2.3.1

- 泛型化改造
- 模块化重构
- 分离 llm-sdk-api

## 许可证

MIT License

## 作者

- IhyTdX (18683731@qq.com)

## 相关链接

- [GitHub](https://github.com/oodercn/ooder-sdk)
- [Maven Central](https://central.sonatype.com/artifact/net.ooder/agent-sdk)
