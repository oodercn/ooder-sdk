# Agent SDK Core

## 简介

Agent SDK Core 是 Ooder Agent SDK 的核心实现模块，提供完整的 Agent 生命周期管理、能力编排、协议适配等功能。

## 模块定位

作为 Agent SDK 的核心实现层，本模块：
- 实现 Agent 生命周期管理
- 提供能力编排引擎
- 实现南向协议适配（A2A、REACH）
- 依赖 agent-sdk-api、llm-sdk-api、llm-sdk、skills-framework

## 核心功能

### Agent 管理
- `OoderSDK` - SDK 入口类
- `AgentImpl` - Agent 实现
- `AgentManager` - Agent 管理器
- `AgentLifecycleManager` - 生命周期管理

### 能力编排
- `StoryOrchestrator` - Story 编排器
- `CapabilityRouter` - 能力路由器
- `WillTransformer` - Will 转换器

### 协议适配
- `A2AProtocolAdapter` - A2A 协议适配器
- `ReachProtocolAdapter` - REACH 协议适配器
- `ProtocolHandler` - 协议处理器

### 南向协议闭环
- `SouthboundProtocolManager` - 南向协议管理器
- `ProtocolExecutor` - 协议执行器
- `ProtocolValidator` - 协议验证器

## 依赖关系

```
agent-sdk-core
    ├── agent-sdk-api (API 接口)
    ├── llm-sdk-api (LLM 轻量级 API)
    ├── llm-sdk (LLM 完整实现)
    └── skills-framework (技能框架)
```

## 使用方式

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>
```

## 快速开始

```java
import net.ooder.sdk.api.OoderSDK;

public class AgentExample {
    public static void main(String[] args) {
        // 创建 SDK 实例
        OoderSDK sdk = OoderSDK.builder()
            .agentId("my-agent")
            .agentName("My Agent")
            .build();
        
        // 启动 Agent
        sdk.start();
        
        // 使用能力编排
        StoryOrchestrator orchestrator = sdk.getStoryOrchestrator();
        orchestrator.orchestrate(story);
        
        // 调用能力
        CapabilityResult result = sdk.getCapabilityInvoker()
            .invoke("capability-id", params);
    }
}
```

## 版本

- 当前版本: 2.3
- 兼容版本: Java 8+

## 许可证

MIT License
