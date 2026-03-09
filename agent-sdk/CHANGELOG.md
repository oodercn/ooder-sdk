# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.3.1] - 2026-03-08

### Changed

- **架构重构** - 简化模块结构
  - 删除 `llm-sdk-api` 模块（已合并到 `llm-sdk`）
  - 删除 `agent-sdk-api` 模块（已合并到 `agent-sdk-core`）
  - 模块数量从 5 个减少到 3 个

### Removed

- `llm-sdk-api` 模块
- `agent-sdk-api` 模块
- `LlmServiceImpl` 类（依赖已删除的 llm-sdk-api）

---

## [2.3.0] - 2026-02-27

### Added

- **泛型化改造** - 核心 API 全面支持泛型
  - `StorageService<T>` - 类型安全的存储服务
  - `CapabilityRouter<P, D>` - 泛型能力路由
  - `SecurityService<C>` - 泛型安全服务
  - `OrchestrationResult<T>` - 泛型编排结果
  - `StepExecutionRecord<R>` - 泛型执行记录
  - `A2AMessage<T>` - 泛型 A2A 消息
  - `NlpInteractionApi<M>` - 泛型 NLP 接口
  - `SceneConfig<P>` - 泛型场景配置
  - `LinkInfo<M>` - 泛型链接信息
  - `TokenInfo<C>` - 泛型 Token 信息
  - `ConfigUpdateMessage<C>` - 泛型配置更新消息
  - `ChatRequest<P>` - 泛型聊天请求
  - `TaskSendMessage<P>` - 泛型任务发送消息
  - `CapabilityDeclaration<M, D>` - 泛型能力声明
  - `SceneStore<C>` - 泛型场景存储

### Changed

- 所有消息类统一继承泛型化的 `A2AMessage<T>`
  - `ErrorMessage<T>`
  - `TaskGetMessage<T>`
  - `TaskCancelMessage<T>`
  - `HeartbeatMessage<T>`
  - `AckMessage<T>`
  - `StateChangeMessage<T>`

### Removed

- 删除 agent-sdk-core 中与 skills-framework 重复的 Skills 相关类
  - `net.ooder.sdk.skill.SkillExecutionEngine`
  - `net.ooder.sdk.skill.SkillMdRegistry`
  - `net.ooder.sdk.skill.SkillMdParser`
  - `net.ooder.sdk.skill.SkillMdDocument`
  - `net.ooder.sdk.skill.SkillMdSection`
  - `net.ooder.sdk.skill.SkillMdParameter`
  - `net.ooder.sdk.skill.SkillMdExample`
  - 以及对应的实现类

### Fixed

- 清理代码重复问题
- 统一泛型命名规范

## [2.3] - 2025-02-27

### Changed

- 重构模块化结构
- 分离 llm-sdk-api (轻量级) 和 llm-sdk (完整版)
- 移除 scene-engine 模块 (移至外部工程)
- 统一版本号为 2.3
- 清理重复代码

## [0.7.3] - 2024

### Added

- 初始版本
- 包含 scene-engine 和 llm-sdk 作为内部模块

---

## 泛型化使用示例

### StorageService

```java
// 类型安全的存储
StorageService<User> storage = ...;
Optional<User> user = storage.load("user:001", User.class);
storage.save("user:001", user);

// 批量操作
Map<String, User> users = storage.loadBatch(keys, User.class);
```

### A2AMessage

```java
// 泛型消息
A2AMessage<TaskRequest> message = A2AMessage.taskSend("skill-001", taskRequest);
TaskRequest request = message.getData();

// 向后兼容
A2AMessage<Map<String, Object>> generic = A2AMessage.createGeneric();
```

### OrchestrationResult

```java
// 泛型结果
OrchestrationResult<Order> result = orchestrator.orchestrate(story);
Order order = result.getFinalResult(); // 无需强制类型转换
```

### ChatRequest

```java
// 泛型参数
ChatRequest<String> request = new ChatRequest<>();
request.addParameter("model", "gpt-4");
```
