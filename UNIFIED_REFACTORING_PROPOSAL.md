# Ooder SDK 统一重构方案

## 一、现状分析

### 1.1 当前架构问题

```
┌─────────────────────────────────────────────────────────────────┐
│                     agent-sdk (2.3)                              │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  infra (基础设施) - 需要拆分                               │   │
│  │  llm (AI能力) - 需要独立                                  │   │
│  │  agent (核心) - 保持                                       │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   scene-engine (2.3)                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  依赖 agent-sdk 的全部                                    │   │
│  │  有自己的 LlmProvider 接口                                │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   ooder-common (2.3)                             │
│  ┌─────────┬─────────┬─────────┬─────────┬─────────┬─────────┐  │
│  │ config  │ client  │database │ server  │ vfs-web │ org-web │  │
│  │ (配置)   │ (核心)   │ (数据库)│ (服务)   │ (文件)   │ (组织)   │  │
│  └─────────┴─────────┴─────────┴─────────┴─────────┴─────────┘  │
│  ┌─────────┬─────────┐                                          │
│  │index-web│ msg-web │                                          │
│  │ (索引)   │ (消息)   │                                          │
│  └─────────┴─────────┘                                          │
└─────────────────────────────────────────────────────────────────┘
```

**问题：**
1. agent-sdk 包含 infra + llm + agent，职责混杂
2. scene-engine 依赖整个 agent-sdk，耦合严重
3. llm-sdk 独立但功能重复
4. ooder-common 模块众多，层级不清

---

## 二、目标架构

### 2.1 四层架构设计

```
┌─────────────────────────────────────────────────────────────────┐
│  Layer 4: 应用层 (Applications)                                  │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐               │
│  │scene-engine │ │  skill-ai   │ │ 其他应用     │               │
│  │  (场景引擎)  │ │  (AI技能)   │ │             │               │
│  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘               │
└─────────┼───────────────┼───────────────┼───────────────────────┘
          │               │               │
          ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────────┐
│  Layer 3: 能力层 (Capability SDKs)                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │   llm-sdk       │  │   agent-sdk     │  │   vfs-sdk       │  │
│  │  (AI 能力)       │  │  (Agent 能力)    │  │  (文件能力)      │  │
│  │  - 模型调用      │  │  - 生命周期      │  │  - 文件存储      │  │
│  │  - Embedding    │  │  - 网络协议      │  │  - 文件同步      │  │
│  │  - Prompt管理   │  │  - 场景管理      │  │  - 大文件        │  │
│  └────────┬────────┘  └────────┬────────┘  └─────────────────┘  │
│           │                    │                                │
│  ┌─────────────────┐  ┌─────────────────┐                      │
│  │   org-sdk       │  │   msg-sdk       │  ... 其他能力 SDK     │
│  │  (组织权限)      │  │  (消息通信)      │                      │
│  └─────────────────┘  └─────────────────┘                      │
└─────────────────────────────────────────────────────────────────┘
          │               │               │
          └───────────────┼───────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  Layer 2: 基础设施层 (Infrastructure)                            │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              ooder-infra-core (新)                       │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐       │   │
│  │  │  异步执行 │ │  配置管理 │ │  异常体系 │ │  工具类   │       │   │
│  │  │  生命周期 │ │  事件机制 │ │  日志记录 │ │  网络工具 │       │   │
│  │  │  缓存抽象 │ │  线程管理 │ │  集群通信 │ │  表达式   │       │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘       │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              ooder-infra-data (新)                       │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐                   │   │
│  │  │ 数据库访问│ │ 连接池   │ │ DAO框架  │                   │   │
│  │  └─────────┘ └─────────┘ └─────────┘                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              ooder-infra-server (新)                     │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐                   │   │
│  │  │ HTTP服务 │ │ UDP通信  │ │ 服务注册 │                   │   │
│  │  └─────────┘ └─────────┘ └─────────┘                   │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
          │               │               │
          └───────────────┼───────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  Layer 1: 基础层 (Foundation)                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              ooder-annotation (保留)                     │   │
│  │              ooder-config (保留)                         │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 三、详细重构方案

### 3.1 Layer 1: 基础层（保留）

#### ooder-annotation（不变）
- 位置：`ooder-common/ooder-annotation`
- 职责：注解定义
- 版本：2.3

#### ooder-config（增强）
- 位置：`ooder-common/ooder-config`
- 职责：配置管理基础
- 增强：支持更多配置源（Apollo、Nacos）

### 3.2 Layer 2: 基础设施层（新建）

#### ooder-infra-core（从 common-client 提取）

**来源：** `ooder-common/ooder-common-client`

**包含：**
```
net.ooder.infra.core
├── async          # AsyncExecutor, AsyncTask
├── cache          # CacheManager, RedisPoolUtil
├── config         # ConfigLoader (简化版)
├── event          # EventBus (轻量级)
├── exception      # BaseException, ErrorCode
├── lifecycle      # LifecycleManager (简化版)
├── thread         # 线程管理
├── util           # JsonUtils, FileUtils, NetUtils
└── expression     # JEP (可选)
```

**特点：**
- 纯 Java，无 Spring 依赖
- Java 8 兼容
- 零外部依赖

#### ooder-infra-data（从 database 改造）

**来源：** `ooder-common/ooder-database`

**包含：**
```
net.ooder.infra.data
├── jdbc           # JDBC封装
├── pool           # 连接池管理
├── dao            # DAO框架
└── orm            # 轻量级ORM (可选)
```

#### ooder-infra-server（从 server 改造）

**来源：** `ooder-common/ooder-server`

**包含：**
```
net.ooder.infra.server
├── http           # HTTP服务器
├── udp            # UDP通信
├── proxy          # 代理服务器
└── registry       # 服务注册
```

### 3.3 Layer 3: 能力层（改造）

#### llm-sdk（独立，依赖 infra-core）

**现状：** 已独立，但功能不完整

**改造：**
```
net.ooder.sdk.llm
├── api            # LLM API接口
├── model          # 模型定义
├── driver         # Driver实现 (从 agent-sdk 迁移)
├── embedding      # Embedding服务
├── prompt         # Prompt管理
└── memory         # 内存/上下文管理
```

**依赖：** `ooder-infra-core`

#### agent-sdk（精简，依赖 infra-core + infra-server）

**改造前：** 包含 infra + llm + agent

**改造后：** 只保留 agent 核心
```
net.ooder.sdk.agent
├── api            # Agent API
├── core           # Agent核心实现
├── lifecycle      # Agent生命周期
├── protocol       # 南向协议
└── scene          # 场景管理
```

**移除：**
- `infra` -> 迁移到 `ooder-infra-core`
- `llm` -> 迁移到 `llm-sdk`
- `drivers/llm` -> 迁移到 `llm-sdk`
- `nlp` -> 迁移到 `llm-sdk` (可选)
- `will` -> 保留，但使用 `llm-sdk` 的接口

**依赖：** `ooder-infra-core`, `ooder-infra-server`

#### vfs-sdk（从 vfs-web 改造）

**来源：** `ooder-common/ooder-vfs-web`

**包含：**
```
net.ooder.sdk.vfs
├── api            # VFS API
├── core           # 核心实现
├── store          # 存储实现
├── sync           # 同步机制
└── bigfile        # 大文件支持
```

#### org-sdk（从 org-web 改造）

**来源：** `ooder-common/ooder-org-web`

**包含：**
```
net.ooder.sdk.org
├── api            # Org API
├── core           # 核心实现
├── user           # 用户管理
├── role           # 角色权限
└── sync           # 组织同步
```

#### msg-sdk（从 msg-web 改造）

**来源：** `ooder-common/ooder-msg-web`

**包含：**
```
net.ooder.sdk.msg
├── api            # Msg API
├── core           # 核心实现
├── mqtt           # MQTT支持
├── jmq            # JMQ支持
└── command        # 命令消息
```

#### index-sdk（从 index-web 改造）

**来源：** `ooder-common/ooder-index-web`

**包含：**
```
net.ooder.sdk.index
├── api            # Index API
├── core           # Lucene封装
├── vfs            # VFS索引集成
└── search         # 搜索服务
```

### 3.4 Layer 4: 应用层（适配）

#### scene-engine（依赖多个 SDK）

**改造前：** 依赖整个 agent-sdk

**改造后：** 按需依赖
```xml
<dependencies>
    <!-- 基础设施 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-infra-core</artifactId>
    </dependency>
    
    <!-- 能力 SDK -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>vfs-sdk</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>org-sdk</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>msg-sdk</artifactId>
    </dependency>
</dependencies>
```

---

## 四、迁移策略

### 4.1 迁移原则

1. **保持功能不变** - 只移动代码，不修改逻辑
2. **保持包名不变** - 避免破坏现有引用
3. **分阶段实施** - 降低风险
4. **向后兼容** - 提供适配层

### 4.2 分阶段实施

#### Phase 1: 基础设施层（2周）

**Week 1: 创建 ooder-infra-core**
- 从 `ooder-common-client` 提取核心类
- 从 `agent-sdk/infra` 提取基础设施
- 发布 1.0.0

**Week 2: 创建 ooder-infra-data 和 ooder-infra-server**
- 从 `ooder-database` 迁移
- 从 `ooder-server` 迁移
- 发布 1.0.0

#### Phase 2: 能力 SDK 改造（2周）

**Week 3: llm-sdk 完善**
- 从 `agent-sdk` 迁移 LLM 实现
- 依赖 `ooder-infra-core`
- 发布 2.3.1

**Week 4: agent-sdk 精简**
- 移除 infra 和 llm
- 依赖 `ooder-infra-core` + `ooder-infra-server`
- 发布 2.3.1

#### Phase 3: Common 改造（2周）

**Week 5-6: 创建 vfs-sdk, org-sdk, msg-sdk, index-sdk**
- 从 `ooder-common` 各模块迁移
- 依赖 `ooder-infra-core`
- 发布 2.3.1

#### Phase 4: 应用层适配（1周）

**Week 7: scene-engine 适配**
- 更新依赖
- 测试验证

#### Phase 5: 清理（1周）

**Week 8: 废弃旧模块**
- 标记 `ooder-common-client` 为 @Deprecated
- 更新文档
- 发布迁移指南

---

## 五、依赖关系图（重构后）

```
ooder-annotation (2.3)
    │
    ▼
ooder-config (2.3)
    │
    ▼
ooder-infra-core (1.0.0) ◄────────────────────────┐
    │                                              │
    ├──► ooder-infra-data (1.0.0)                 │
    │                                              │
    ├──► ooder-infra-server (1.0.0) ◄─────────────┤
    │                   │                          │
    │                   ▼                          │
    │              agent-sdk (2.3.1)               │
    │                   │                          │
    │                   ▼                          │
    │              scene-engine (2.3.1)            │
    │                                              │
    ├──► llm-sdk (2.3.1) ◄────────────────────────┤
    │       │                                      │
    │       ▼                                      │
    │   skill-ai (2.3.1)                           │
    │                                              │
    ├──► vfs-sdk (2.3.1) ◄────────────────────────┤
    │                                              │
    ├──► org-sdk (2.3.1) ◄────────────────────────┤
    │                                              │
    ├──► msg-sdk (2.3.1) ◄────────────────────────┤
    │                                              │
    └──► index-sdk (2.3.1) ◄──────────────────────┘
```

---

## 六、风险与缓解

| 风险 | 等级 | 缓解措施 |
|-----|------|---------|
| 功能回归 | 高 | 全面单元测试 + 集成测试 |
| 依赖冲突 | 中 | 使用 Maven Enforcer 插件 |
| 版本混乱 | 中 | 统一版本管理，使用 BOM |
| 迁移成本 | 高 | 分阶段实施，保持向后兼容 |
| 团队适应 | 中 | 提供详细文档和培训 |

---

## 七、收益

| 方面 | 现状 | 重构后 |
|-----|------|-------|
| **架构清晰度** | 模块混杂，层级不清 | 四层架构，职责明确 |
| **依赖关系** | 循环依赖，耦合严重 | 单向依赖，清晰简单 |
| **可维护性** | 改动影响范围广 | 模块化，影响范围小 |
| **可扩展性** | 难以添加新能力 | 易于添加新 SDK |
| **团队分工** | 耦合开发 | 独立开发，并行效率高 |
| **技术栈** | 统一 Java 8 | 基础设施 Java 8，LLM 可升级 |

---

## 八、总结

**核心思想：**
1. **分层架构**：Foundation -> Infrastructure -> Capability -> Application
2. **职责分离**：每个 SDK 只负责一个领域
3. **依赖倒置**：上层依赖下层，下层不依赖上层
4. **渐进式重构**：分 5 个阶段，8 周完成

**预期效果：**
- 架构清晰，易于理解和维护
- 模块独立，团队可并行开发
- 技术栈灵活，LLM 可独立演进
- 向后兼容，平滑迁移
