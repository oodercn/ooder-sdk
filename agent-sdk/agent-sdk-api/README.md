# Agent SDK API

## 简介

Agent SDK API 是 Ooder Agent SDK 的核心 API 接口和模型定义模块，提供所有子模块共享的接口和领域模型。

## 模块定位

作为 Agent SDK 的基础层，本模块：
- 定义核心接口（Agent、Capability、Scene、Command、Event）
- 提供共享的领域模型
- 定义公共枚举和常量
- 无外部依赖，可被所有其他模块引用

## 核心接口

### Agent 接口
- `Agent` - Agent 基础接口
- `AgentFactory` - Agent 工厂接口

### Capability 接口
- `Capability` - 能力定义接口
- `CapabilityInvoker` - 能力调用接口

### Scene 接口
- `SceneManager` - 场景管理接口
- `SceneGroupManager` - 场景组管理接口
- `SceneDefinition` - 场景定义模型

### Command 接口
- `Command` - 命令接口
- `CommandResult` - 命令结果模型

### Event 接口
- `Event` - 事件基础接口
- `EventBus` - 事件总线接口
- `EventHandler` - 事件处理器接口

## 使用方式

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

## 版本

- 当前版本: 2.3
- 兼容版本: Java 8+

## 许可证

MIT License
