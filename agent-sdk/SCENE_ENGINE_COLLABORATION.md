# Scene-Engine 协作任务说明文档

## 文档信息
- **版本**: 2.3.1
- **日期**: 2026-03-08
- **状态**: ✅ **已打包完成，可交付使用**
- **打包时间**: 2026-03-08 15:54
- **Maven 状态**: 已安装到本地仓库

---

## 一、已完成工作（Agent-SDK 团队）

### 1.1 架构重构

#### 模块合并
- ✅ **llm-sdk-api → llm-sdk** 合并完成
- ✅ **agent-sdk-api → agent-sdk-core** 合并完成
- ✅ 模块数量从 5 个减少到 3 个

#### 重复定义清理
- ✅ 删除 LLM-SDK 中重复的 6 个接口定义：
  - `CapabilityRequestApi`
  - `NlpInteractionApi`
  - `SchedulingApi`
  - `MemoryBridgeApi`
  - `SecurityApi`
  - `MonitoringApi`
- ✅ 删除对应的 model 类（约 50+ 个文件）
- ✅ 删除 `LlmSdkFactory` 占位符工厂类

#### 接口标准化
- ✅ 所有通用 API 统一在 `agent-sdk-core` 中定义
- ✅ LLM 特有功能保留在 `llm-sdk` 中
- ✅ `LlmSdk` 接口简化，添加迁移说明

### 1.2 编译验证与打包
- ✅ **BUILD SUCCESS** - 所有模块编译通过
- ✅ 版本 2.3.1 已安装到本地 Maven 仓库
- ✅ Maven 打包完成（JAR + Sources + Javadoc）

### 1.3 Maven 本地仓库坐标

| 模块 | GroupId | ArtifactId | 版本 | 本地仓库路径 |
|------|---------|------------|------|-------------|
| Agent SDK Parent | `net.ooder` | `agent-sdk` | `2.3.1` | `~/.m2/repository/net/ooder/agent-sdk/2.3.1/` |
| LLM SDK | `net.ooder` | `llm-sdk` | `2.3.1` | `~/.m2/repository/net/ooder/llm-sdk/2.3.1/` |
| Skills Framework | `net.ooder` | `skills-framework` | `2.3.1` | `~/.m2/repository/net/ooder/skills-framework/2.3.1/` |
| Agent SDK Core | `net.ooder` | `agent-sdk-core` | `2.3.1` | `~/.m2/repository/net/ooder/agent-sdk-core/2.3.1/` |

---

## 二、新的模块架构

### 2.1 模块结构

```
agent-sdk (Parent POM) 2.3.1
├── llm-sdk              # LLM SDK（已合并 llm-sdk-api）
│   ├── MultiLlmAdapterApi（LLM 特有）
│   ├── Story/Will 编排
│   ├── LLM 驱动实现
│   └── 记忆管理
├── skills-framework     # Skills 框架（独立）
└── agent-sdk-core       # Agent SDK Core（已合并 agent-sdk-api）
    ├── API 接口定义
    ├── 核心实现
    └── 服务实现
```

### 2.2 依赖关系

```
┌─────────────────────────────────────────┐
│      agent-sdk-core (API + 实现)        │
│  - CapabilityRequestApi                  │
│  - NlpInteractionApi                     │
│  - SchedulingApi                         │
│  - SecurityService                       │
│  - MonitoringApi                         │
│  - SceneManager                          │
│  - SceneGroupManager                     │
│  - SkillConnector                        │
│  - CapRoutingStrategy                    │
│  - OfflineManager                        │
└─────────────────────────────────────────┘
                    ▲
                    │ 依赖
                    │
┌─────────────────────────────────────────┐
│           llm-sdk (LLM 特有)            │
│  - MultiLlmAdapterApi                    │
│  - Story/Will 编排                       │
│  - LLM 驱动实现                          │
└─────────────────────────────────────────┘
```

---

## 三、Scene-Engine 团队后续工作

### 3.1 依赖配置

Scene-Engine 项目需要添加以下依赖：

```xml
<dependencies>
    <!-- Agent SDK Core - 包含所有 API 定义和实现 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-core</artifactId>
        <version>2.3.1</version>
    </dependency>
    
    <!-- LLM SDK - LLM 特有功能 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>2.3.1</version>
    </dependency>
    
    <!-- Skills Framework - Skill 管理 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skills-framework</artifactId>
        <version>2.3.1</version>
    </dependency>
</dependencies>
```

### 3.2 API 使用指南

#### 通用 API（从 agent-sdk-core 导入）

```java
// 场景管理
import net.ooder.sdk.api.scene.SceneManager;
import net.ooder.sdk.api.scene.SceneGroupManager;

// 能力管理
import net.ooder.sdk.api.capability.CapabilityRequestApi;

// NLP 交互
import net.ooder.sdk.api.nlp.NlpInteractionApi;

// 调度
import net.ooder.sdk.api.scheduling.SchedulingApi;

// 安全
import net.ooder.sdk.api.security.SecurityService;

// 监控
import net.ooder.sdk.api.monitoring.MonitoringApi;

// 离线管理
import net.ooder.sdk.api.offline.OfflineManager;

// 路由策略
import net.ooder.sdk.api.routing.CapRoutingStrategy;

// Skill 连接器
import net.ooder.sdk.api.skill.connector.SkillConnector;
```

#### LLM 特有 API（从 llm-sdk 导入）

```java
// 多 LLM 适配器
import net.ooder.sdk.llm.adapter.MultiLlmAdapterApi;

// Story/Will 编排
import net.ooder.sdk.story.StoryManager;
import net.ooder.sdk.story.UserStory;
import net.ooder.sdk.will.WillManager;
import net.ooder.sdk.will.WillExpression;

// 记忆管理
import net.ooder.sdk.memory.MemoryBridge;
```

### 3.3 待实现功能（Scene-Engine 层）

#### P0 - 核心功能

1. **Scene-Engine 核心启动器**
   - 整合 agent-sdk-core 的初始化流程
   - 整合 llm-sdk 的初始化流程
   - 统一配置管理

2. **Scene 生命周期管理**
   - 使用 `SceneManager` 管理场景生命周期
   - 使用 `SceneGroupManager` 管理场景组
   - 实现场景的创建、激活、停用、销毁

3. **Skill 编排引擎**
   - 使用 `StoryManager` 和 `WillManager` 编排 Skill 执行
   - 实现场景内的 Skill 协作

#### P1 - 高级功能

4. **LLM 集成层**
   - 使用 `MultiLlmAdapterApi` 接入多种 LLM 提供商
   - 实现 LLM 能力的路由和负载均衡

5. **离线模式支持**
   - 使用 `OfflineManager` 实现离线场景支持
   - 实现本地缓存和同步机制

6. **监控和可观测性**
   - 使用 `MonitoringApi` 收集指标
   - 实现场景执行的可视化监控

#### P2 - 扩展功能

7. **安全集成**
   - 使用 `SecurityService` 实现场景级别的安全控制
   - 实现 Agent 间的安全通信

8. **能力发现与注册**
   - 使用 `CapabilityRequestApi` 实现动态能力发现
   - 实现场景内的能力共享

---

## 四、重要注意事项

### 4.1 已废弃的接口

以下接口已标记为 `@Deprecated`，请勿在新代码中使用：

- `net.ooder.sdk.llm.LlmSdk` - 已简化，建议直接使用 agent-sdk-core 中的服务
- `net.ooder.skills.api.SceneGroupManager` - 请使用 `net.ooder.sdk.api.scene.SceneGroupManager`

### 4.2 包名变更

以下包名已变更，请更新 import 语句：

| 旧包名 | 新包名 |
|--------|--------|
| `net.ooder.llm.api.*` | `net.ooder.sdk.llm.*` |
| `net.ooder.sdk.api.*` (agent-sdk-api) | `net.ooder.sdk.api.*` (agent-sdk-core) |

### 4.3 版本兼容性

- 当前版本：**2.3.1**
- 所有模块版本统一，避免版本不一致问题
- 建议 scene-engine 使用相同版本

---

## 五、协作流程

### 5.1 问题反馈

如在使用 agent-sdk 过程中遇到问题，请通过以下方式反馈：

1. 在 agent-sdk 项目中创建 Issue
2. 描述问题现象和复现步骤
3. 提供相关代码片段和错误日志

### 5.2 需求提出

如需新增 API 或功能：

1. 在 agent-sdk 项目中创建 Feature Request
2. 描述使用场景和需求
3. 说明期望的接口定义

### 5.3 代码贡献

如需贡献代码：

1. Fork agent-sdk 项目
2. 创建特性分支
3. 提交 Pull Request
4. 等待 Code Review

---

## 六、参考文档

### 6.1 核心接口文档

- `SceneManager` - 场景生命周期管理
- `SceneGroupManager` - 场景组管理（高可用集群）
- `SkillConnector` - Skill 连接抽象
- `CapRoutingStrategy` - CAP 路由策略
- `OfflineManager` - 离线模式管理

### 6.2 示例代码

请参考 `agent-sdk-core/src/test/java` 目录下的示例代码。

---

## 七、联系信息

- **Agent-SDK 团队**: agent-sdk-team@ooder.net
- **Scene-Engine 团队**: scene-engine-team@ooder.net
- **项目负责人**: [待填写]

---

## 附录：变更日志

### v2.3.1 (2026-03-08)

#### 架构变更
- 合并 llm-sdk-api 到 llm-sdk
- 合并 agent-sdk-api 到 agent-sdk-core
- 删除重复的接口定义
- 简化模块结构

#### API 变更
- 统一 API 定义位置
- 标记废弃接口
- 更新包结构

#### 依赖变更
- 模块数量：5个 → 3个
- 依赖关系简化
- 无循环依赖

#### 打包信息
- Maven 构建成功
- 已安装到本地仓库：`~/.m2/repository/net/ooder/`
- 包含文件：JAR + Sources + Javadoc

---

## 快速开始

### 1. 确认本地仓库

检查以下目录是否存在：
```bash
ls ~/.m2/repository/net/ooder/agent-sdk-core/2.3.1/
ls ~/.m2/repository/net/ooder/llm-sdk/2.3.1/
ls ~/.m2/repository/net/ooder/skills-framework/2.3.1/
```

### 2. 在 Scene-Engine 项目中添加依赖

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

### 3. 开始使用

```java
// 导入 Scene Manager
import net.ooder.sdk.api.scene.SceneManager;

// 导入 Scene Group Manager  
import net.ooder.sdk.api.scene.SceneGroupManager;

// 导入 Skill Connector
import net.ooder.sdk.api.skill.connector.SkillConnector;
```

---

**文档结束**

*本文档由 Agent-SDK 团队编制，供 Scene-Engine 团队参考使用。*
*最后更新：2026-03-08 15:54*

*本文档由 Agent-SDK 团队编制，供 Scene-Engine 团队参考使用。*
