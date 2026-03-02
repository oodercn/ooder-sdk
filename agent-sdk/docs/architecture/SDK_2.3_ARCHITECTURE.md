# Agent SDK 2.3 架构设计文档

## 📚 必读文档

> ⚠️ **二次开发必读**: 在集成SDK到Spring Boot项目时，如果遇到组件注入问题，请先阅读以下文档：

| 文档 | 路径 | 说明 |
|------|------|------|
| **SDK组件注入二次开发指南** | `../SDK_INJECTION_SECONDARY_DEVELOPMENT_GUIDE.md` | Spring集成问题完整解决方案 |
| **SDK组件注入单元测试指南** | `../SDK_INJECTION_UNIT_TEST_GUIDE.md` | 单元测试实现与验证指南 |

**常见问题**:
- `DriverRegistry`无法通过`@Autowired`注入
- `CapabilityRegistry`无法通过`@Autowired`注入
- `SceneEngine`依赖项注入失败

---

## 1. 概述

Agent SDK 2.3 是一个经过重构的轻量级 Agent 开发框架，采用模块化设计，将核心功能与扩展功能分离，实现清晰的依赖关系。

## 2. 架构目标

- **模块化**: 清晰的模块边界，可独立使用
- **可扩展**: 支持外部扩展（llm-sdk, scene-engine）
- **轻量级**: 核心模块最小依赖
- **兼容性**: 保持与外部工程的兼容

## 3. 模块结构

```
agent-sdk (父工程, pom)
├── agent-sdk-api (jar)
│   └── 无外部依赖
├── llm-sdk-api (jar)
│   └── 依赖: agent-sdk-api
├── llm-sdk (jar)              # 已合并到内部
│   └── 依赖: agent-sdk-api
├── skills-framework (jar)
│   └── 依赖: agent-sdk-api
└── agent-sdk-core (jar)
    ├── 依赖: agent-sdk-api
    ├── 依赖: llm-sdk-api
    ├── 依赖: llm-sdk
    └── 依赖: skills-framework
```

## 4. 模块职责

### 4.1 agent-sdk-api

**定位**: API 接口和模型定义层

**包含内容**:
- Agent 接口定义
- Capability 接口定义
- Scene 接口定义
- Command 接口定义
- Event 接口定义
- 公共枚举和常量

**设计原则**:
- 无外部依赖
- 纯接口和模型
- 作为所有模块的基础

### 4.2 llm-sdk-api

**定位**: LLM 轻量级 API

**包含内容**:
- LLM 服务接口
- 聊天请求/响应模型
- 函数定义模型

**设计原则**:
- 最小化 LLM 抽象
- 不依赖具体 LLM 实现

### 4.3 skills-framework

**定位**: 技能框架支持

**包含内容**:
- 技能加载机制
- 技能代码生成
- 运行时支持

**设计原则**:
- 与具体技能实现解耦
- 提供扩展点

### 4.4 agent-sdk-core

**定位**: 核心实现

**包含内容**:
- Agent 实现
- 能力编排引擎 (StoryOrchestrator)
- 协议适配器 (A2A, REACH)
- 南向协议闭环实现

**依赖外部**:
- llm-sdk: 提供完整的 Story/Will/Memory 实现

## 5. 依赖关系

### 5.1 内部依赖

```
agent-sdk-api
    ↑
    ├── llm-sdk-api
    ├── llm-sdk
    ├── skills-framework
    └── agent-sdk-core
        ├── llm-sdk-api
        ├── llm-sdk
        └── skills-framework
```

### 5.2 工程间依赖

```
外部 scene-engine → agent-sdk (单向)
```

## 6. 命名规范

### 6.1 Artifact 命名

| 模块 | GroupId | ArtifactId | 说明 |
|------|---------|-----------|------|
| 父工程 | net.ooder | agent-sdk | 聚合模块 |
| API | net.ooder | agent-sdk-api | 核心 API |
| LLM API | net.ooder | llm-sdk-api | 轻量级 LLM API |
| 技能框架 | net.ooder | skills-framework | 技能支持 |
| 核心实现 | net.ooder | agent-sdk-core | 核心实现 |

### 6.2 内部模块命名

| 模块 | GroupId | ArtifactId | 说明 |
|------|---------|-----------|------|
| LLM SDK | net.ooder | llm-sdk | 完整 LLM 实现 (已合并到内部) |

### 6.3 外部工程命名

| 工程 | GroupId | ArtifactId | 说明 |
|------|---------|-----------|------|
| Scene Engine | net.ooder | scene-engine | 完整 Scene 实现 (外部工程) |

## 7. 版本策略

### 7.1 版本号规则

- **主版本**: 重大架构变更
- **次版本**: 功能增强，保持兼容
- **修订版本**: Bug 修复

### 7.2 当前版本

- **agent-sdk**: 2.3
- **llm-sdk**: 2.3
- **scene-engine**: 0.7.3 (外部)

## 8. 使用场景

### 8.1 轻量级使用

仅使用 API 层：

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

### 8.2 标准使用

使用核心 SDK：

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>
```

### 8.3 完整使用

核心 SDK + 外部扩展：

```xml
<!-- 核心 SDK -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>

<!-- 外部扩展 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>0.7.3</version>
</dependency>
```

## 9. 扩展机制

### 9.1 接口扩展

通过 agent-sdk-api 定义接口，外部工程实现：

```java
// agent-sdk-api 定义
public interface SceneManager {
    Scene create(SceneDefinition definition);
}

// scene-engine 实现
public class SceneManagerImpl implements SceneManager {
    // 实现逻辑
}
```

### 9.2 SPI 扩展

通过 SPI 机制加载扩展：

```java
// META-INF/services/net.ooder.sdk.api.scene.SceneManager
net.ooder.engine.scene.core.SceneManagerImpl
```

## 10. 迁移指南

### 10.1 从 0.7.3 迁移到 2.3

#### 变更点

1. **scene-engine 移除**: 从内部模块移至外部工程
2. **llm-sdk 分离**: 分为 llm-sdk-api (轻量) 和 llm-sdk (完整)
3. **版本统一**: 统一为 2.3

#### 迁移步骤

1. 更新依赖版本号到 2.3
2. 如需 scene-engine 功能，添加外部依赖
3. 更新 import 路径

### 10.2 代码调整

**Before (0.7.3)**:
```java
import net.ooder.engine.scene.core.SceneManagerImpl;
```

**After (2.3)**:
```java
// 方式1: 使用接口
import net.ooder.sdk.api.scene.SceneManager;

// 方式2: 使用外部工程实现
import net.ooder.engine.scene.core.SceneManagerImpl;
```

## 11. 未来规划

### 11.1 短期目标

- 完善核心功能测试
- 优化性能
- 完善文档

### 11.2 长期目标

- 支持更多南向协议
- 提供更丰富的编排能力
- 云原生支持

## 12. 附录

### 12.1 相关文档

- [整体架构](OVERALL_ARCHITECTURE.md)
- [核心抽象层](CORE_ABSTRACTION_LAYER.md)
- [南北向架构](NORTHBOUND_SOUTHBOUND_ARCHITECTURE.md)

### 12.2 外部工程

- [llm-sdk](../../llm-sdk/)
- [scene-engine](../../scene-engine/)
