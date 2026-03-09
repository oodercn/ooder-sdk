# Agent SDK 2.3.1 架构重构报告

## 执行摘要

**重构时间**: 2026-03-08  
**版本**: 2.3.1  
**状态**: ✅ **已完成**  

本次架构重构主要解决了模块重复定义、接口混乱、包名不一致等严重架构问题，将模块数量从 5 个减少到 3 个，建立了清晰的依赖关系。

---

## 一、重构前的问题

### 1.1 严重问题

| 问题 | 影响 | 数量 |
|------|------|------|
| **Capability 重复定义** | 同名不同类型，导致类型冲突 | 2 个 |
| **llm-sdk-api 模块冗余** | 代码重复，维护困难 | 1 个模块 |
| **SceneGroupManager 重复** | 同名不同功能，理解困难 | 2 个 |
| **LlmService 重复** | 接口和实现重复定义 | 2 个 |

### 1.2 中等问题

| 问题 | 影响 | 数量 |
|------|------|------|
| **枚举类重复** | AgentType, SceneType 等在多个模块重复 | 6+ 个 |
| **API 和实现混合** | agent-sdk-core 同时包含接口和实现 | - |
| **返回 null 的实现** | 可能导致 NPE | 20+ 处 |
| **包名不一致** | skills-framework 使用 net.ooder.skills | 1 个模块 |

---

## 二、重构内容

### 2.1 模块合并

#### ✅ 合并 llm-sdk-api → llm-sdk
- **操作**: 删除 llm-sdk-api 模块
- **原因**: 代码重复，根据 pom.xml 注释应已合并
- **影响**: 消除 LlmService 等接口的重复定义

#### ✅ 合并 agent-sdk-api → agent-sdk-core
- **操作**: 将 agent-sdk-api 的代码合并到 agent-sdk-core
- **原因**: 简化模块结构，API 和实现统一管理
- **影响**: 模块数量减少，依赖关系简化

### 2.2 重复定义清理

#### ✅ Capability 定义统一
- **创建**: `SkillCapability` 类（skills-framework）
- **保留**: `Capability` 类（标记 @Deprecated，继承 SkillCapability）
- **说明**: 
  - agent-sdk-core: `Capability`（接口）- 通用能力定义
  - skills-framework: `SkillCapability`（类）- 技能框架能力定义

#### ✅ 删除重复接口
- `CapabilityRequestApi`（llm-sdk 版本）
- `NlpInteractionApi`（llm-sdk 版本）
- `SchedulingApi`（llm-sdk 版本）
- `MemoryBridgeApi`（llm-sdk 版本）
- `SecurityApi`（llm-sdk 版本）
- `MonitoringApi`（llm-sdk 版本）

#### ✅ 删除重复 model 类
- 删除 llm-sdk 中 6 个 model 包下的重复类（约 50+ 个文件）

### 2.3 架构简化

#### ✅ 删除 LlmSdkFactory
- **原因**: 占位符实现，所有方法抛出 UnsupportedOperationException
- **替代**: 直接使用 agent-sdk-core 中的服务实现

#### ✅ 简化 LlmSdk 接口
- 移除对已删除接口的依赖
- 添加 @Deprecated 注解和迁移说明

---

## 三、重构后的架构

### 3.1 模块结构

```
agent-sdk (Parent POM) 2.3.1
├── llm-sdk              # LLM SDK（已合并 llm-sdk-api）
│   ├── MultiLlmAdapterApi（LLM 特有）
│   ├── Story/Will 编排引擎
│   ├── LLM 驱动实现
│   └── 记忆管理
├── skills-framework     # Skills 框架
│   ├── SkillCapability（新类）
│   ├── Capability（@Deprecated）
│   └── Skill 生命周期管理
└── agent-sdk-core       # Agent SDK Core（已合并 agent-sdk-api）
    ├── API 接口定义
    │   - Capability（接口）
    │   - SceneManager
    │   - SceneGroupManager
    │   - NlpInteractionApi
    │   - SchedulingApi
    │   - SecurityService
    │   - MonitoringApi
    │   - OfflineManager
    │   - CapRoutingStrategy
    │   - SkillConnector
    ├── 核心实现
    │   - StorageServiceImpl
    │   - SecurityServiceImpl
    │   - StoryOrchestrator
    │   - CapabilityRouter
    └── 依赖: llm-sdk, skills-framework
```

### 3.2 依赖关系

```
┌─────────────────────────────────────────┐
│      agent-sdk-core (API + 实现)        │
│  - 所有通用 API 定义和实现                │
└─────────────────────────────────────────┘
           ▲                    ▲
           │                    │
           │         ┌─────────┴─────────┐
           │         │                   │
           │         ▼                   ▼
           │  ┌─────────────┐    ┌─────────────┐
           │  │   llm-sdk   │    │ skills-     │
           │  │  (LLM 特有) │    │ framework   │
           │  └─────────────┘    └─────────────┘
           │                            │
           └────────────────────────────┘
```

### 3.3 模块数量变化

| 版本 | 模块数量 | 模块列表 |
|------|---------|---------|
| 2.3 | 5 个 | agent-sdk-api, llm-sdk-api, llm-sdk, skills-framework, agent-sdk-core |
| 2.3.1 | 3 个 | llm-sdk, skills-framework, agent-sdk-core |

---

## 四、关键变更

### 4.1 API 变更

#### 废弃的类/接口
| 类/接口 | 替代方案 | 移除版本 |
|---------|---------|---------|
| `net.ooder.skills.api.Capability` | `SkillCapability` | v2.4 |
| `net.ooder.skills.api.SceneGroupManager` | `CollaborativeSceneGroupManager` | v2.4 |
| `net.ooder.sdk.llm.LlmSdk` | 直接使用 agent-sdk-core 服务 | v2.4 |

#### 包名变更
| 旧包名 | 新包名 | 说明 |
|--------|--------|------|
| `net.ooder.llm.api.*` | `net.ooder.sdk.llm.*` | LLM API 统一 |

### 4.2 Maven 坐标

```xml
<!-- 核心 SDK -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3.1</version>
</dependency>

<!-- LLM SDK -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk</artifactId>
    <version>2.3.1</version>
</dependency>

<!-- Skills Framework -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skills-framework</artifactId>
    <version>2.3.1</version>
</dependency>
```

> **注意**: 不再提供 `agent-sdk-api` 和 `llm-sdk-api` 依赖

---

## 五、编译验证

### 5.1 构建结果

```
[INFO] Reactor Summary for Ooder Agent SDK 2.3.1:
[INFO]
[INFO] Ooder Agent SDK .................................... SUCCESS [  4.762 s]
[INFO] Ooder LLM SDK ...................................... SUCCESS [ 49.173 s]
[INFO] OODER Skills Framework ............................. SUCCESS [ 13.569 s]
[INFO] Ooder Agent SDK Core ............................... SUCCESS [ 28.381 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:37 min
```

### 5.2 安装结果

所有模块已成功安装到本地 Maven 仓库：
- `~/.m2/repository/net/ooder/agent-sdk/2.3.1/`
- `~/.m2/repository/net/ooder/llm-sdk/2.3.1/`
- `~/.m2/repository/net/ooder/skills-framework/2.3.1/`
- `~/.m2/repository/net/ooder/agent-sdk-core/2.3.1/`

---

## 六、遗留问题（后续版本处理）

### 6.1 中优先级

| 问题 | 说明 | 建议版本 |
|------|------|---------|
| **枚举类重复** | AgentType, SceneType 等在多个模块定义 | v2.4 |
| **API 和实现混合** | agent-sdk-core 同时包含接口和实现 | v2.4 |
| **返回 null** | 20+ 处返回 null 而非 Optional | v2.4 |

### 6.2 低优先级

| 问题 | 说明 | 建议版本 |
|------|------|---------|
| **包名统一** | skills-framework 使用 net.ooder.skills | v2.5 |
| **@Deprecated 代码清理** | 移除已废弃的类和方法 | v2.5 |
| **空值处理改进** | 全面使用 Optional | v2.5 |

---

## 七、Scene-Engine 团队协作指南

### 7.1 依赖配置

Scene-Engine 项目添加以下依赖：

```xml
<dependencies>
    <!-- Agent SDK Core -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-core</artifactId>
        <version>2.3.1</version>
    </dependency>
    
    <!-- LLM SDK -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>2.3.1</version>
    </dependency>
    
    <!-- Skills Framework -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skills-framework</artifactId>
        <version>2.3.1</version>
    </dependency>
</dependencies>
```

### 7.2 API 使用指南

```java
// 从 agent-sdk-core 导入通用 API
import net.ooder.sdk.api.scene.SceneManager;
import net.ooder.sdk.api.scene.SceneGroupManager;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.nlp.NlpInteractionApi;

// 从 llm-sdk 导入 LLM 特有 API
import net.ooder.sdk.llm.adapter.MultiLlmAdapterApi;
import net.ooder.sdk.story.StoryManager;
import net.ooder.sdk.will.WillManager;

// 从 skills-framework 导入 Skill API
import net.ooder.skills.api.SkillCapability;
```

### 7.3 后续工作任务

1. **P0 - 核心功能**
   - Scene-Engine 核心启动器
   - Scene 生命周期管理
   - Skill 编排引擎

2. **P1 - 高级功能**
   - LLM 集成层
   - 离线模式支持
   - 监控和可观测性

3. **P2 - 扩展功能**
   - 安全集成
   - 能力发现与注册

---

## 八、文档清单

### 8.1 已更新文档

| 文档 | 说明 |
|------|------|
| `CHANGELOG.md` | 添加 2.3.1 版本变更日志 |
| `README.md` | 更新版本号和模块结构 |
| `SDK_2.3_ARCHITECTURE.md` | 更新架构设计文档 |
| `SCENE_ENGINE_COLLABORATION.md` | 协作任务说明文档 |
| `ARCHITECTURE_REFACTORING_REPORT.md` | 本报告 |

### 8.2 参考文档

- [CHANGELOG.md](./CHANGELOG.md) - 变更日志
- [README.md](./README.md) - 项目说明
- [SCENE_ENGINE_COLLABORATION.md](./SCENE_ENGINE_COLLABORATION.md) - 协作指南

---

## 九、总结

### 9.1 重构成果

✅ **模块简化**: 5个 → 3个  
✅ **重复清理**: 删除 6 个重复接口 + 50+ model 类  
✅ **架构清晰**: 建立明确的依赖关系  
✅ **编译成功**: BUILD SUCCESS  
✅ **文档完善**: 更新所有相关文档  

### 9.2 架构原则

1. **单一职责**: 每个模块职责清晰
2. **依赖清晰**: 单向依赖，无循环依赖
3. **向后兼容**: 保留废弃类，提供迁移路径
4. **文档完善**: 所有变更都有文档说明

### 9.3 后续计划

- **v2.4**: 清理遗留问题（枚举重复、空值处理）
- **v2.5**: 包名统一、废弃代码清理
- **v2.6**: 性能优化和功能增强

---

**报告结束**

*本报告由 Agent-SDK 团队编制*  
*日期: 2026-03-08*  
*版本: 2.3.1*
