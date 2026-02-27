# LLM SDK API

## 简介

LLM SDK API 是 Ooder Agent SDK 的轻量级 LLM 接口模块，提供 LLM 服务的基础抽象和模型定义。

## 模块定位

作为 LLM 功能的轻量级接口层，本模块：
- 定义 LLM 服务接口
- 提供聊天请求/响应模型
- 支持函数定义模型
- 依赖 agent-sdk-api

## 核心接口

### LLM 服务
- `LlmService` - LLM 服务接口
- `LlmConfig` - LLM 配置模型

### 聊天模型
- `ChatRequest` - 聊天请求
- `ChatResponse` - 聊天响应
- `FunctionDef` - 函数定义
- `TokenUsage` - Token 使用量

## 使用方式

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

## 与 llm-sdk 的关系

```
llm-sdk-api (轻量级接口)
    ↑
llm-sdk (完整实现) - 提供具体的 LLM 驱动实现
```

## 版本

- 当前版本: 2.3
- 兼容版本: Java 8+

## 许可证

MIT License
