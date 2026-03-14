# Ooder SDK

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8+-green.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)

Ooder Agent 平台软件开发工具包，包含 Agent SDK、通用组件和场景引擎。

## 项目结构

```
ooder-sdk/
├── agent-sdk/              # Agent SDK 核心模块 (v2.3.1)
│   ├── agent-sdk-api/      # API 接口层
│   ├── agent-sdk-core/     # 核心实现层
│   ├── skills-framework/   # Skills 框架
│   ├── llm-sdk-api/        # LLM SDK API
│   └── llm-sdk/            # LLM SDK 实现
├── ooder-api/              # 基础 API 接口
├── ooder-util/             # 工具类模块
├── ooder-annotation/       # 注解模块 (v2.3.1)
├── ooder-common/                 # 通用组件模块 (v2.3.1)
│   ├── ooder-config/       # 场景配置管理
│   ├── ooder-database/     # 数据库访问层
│   ├── ooder-common-client/# 客户端核心组件
│   ├── ooder-server/       # 服务器核心
│   ├── ooder-vfs-web/      # VFS Web 服务
│   ├── ooder-org-web/      # 组织人员服务
│   └── ooder-msg-web/      # 消息服务
├── scene-engine/           # 场景引擎 (v2.3.1)
├── pom.xml                 # 父 POM
├── README.md                     # 项目说明
├── ARCHITECTURE_GUIDE.md         # 架构指南
├── MODULE_DIVISION.md            # 模块分工
├── DEVELOPMENT_GUIDE.md          # 二次开发手册
├── NAVIGATION.md                 # 文档导航
├── SKILLS_COLLABORATION.md       # Skills 协作
├── RELEASE_NOTES_v2.3.md         # 发布说明
├── CHANGELOG.md                  # 变更日志
├── CONTRIBUTING.md               # 贡献指南
└── LICENSE                       # 许可证
```

## 模块说明

### agent-sdk

Agent SDK 是 Ooder Agent 平台的核心开发工具包，采用分层架构设计：

| 子模块 | 说明 |
|--------|------|
| agent-sdk-api | API 接口层 - 定义 Agent、Capability、Skill 等核心接口 |
| agent-sdk-core | 核心实现层 - 实现 API 接口，依赖 skills-framework |
| skills-framework | Skills 框架 - 提供 Skill 生命周期管理、发现、安装等功能 |
| llm-sdk-api | LLM SDK API - 定义 LLM 驱动接口 |
| llm-sdk | LLM SDK 实现 - 实现各种 LLM 驱动（百度文心等） |

**特性：**
- Agent 生命周期管理（End Agent、Route Agent、MCP Agent）
- 技能发现与安装（支持 GitHub/Gitee/本地文件系统）
- 场景配置与协作
- LLM 集成（支持百度文心等）
- 网络通信（P2P、UDP、WebSocket）
- 安全认证

详细文档请参阅 [agent-sdk/README.md](agent-sdk/README.md)

### ooder-common

通用组件模块提供企业级开发组件：

| 子模块 | 说明 | 依赖关系 |
|--------|------|----------|
| ooder-config | 场景配置管理 - 提供场景配置、能力配置等 | 独立 |
| ooder-database | 数据库访问层 - 连接池、DAO框架、事务管理 | 依赖 ooder-config |
| ooder-common-client | 客户端核心组件 - 集群、缓存、VFS、组织、服务器等 | 依赖 ooder-database |
| ooder-server | 服务器核心 - Session、JDSServer、集群管理 | 依赖 ooder-common-client |
| ooder-vfs-web | VFS Web 服务 - 虚拟文件系统服务 | 依赖 ooder-common-client, ooder-server |
| ooder-org-web | 组织人员服务 - 组织架构和人员管理 | 依赖 ooder-common-client, ooder-server |
| ooder-msg-web | 消息服务 - 消息发送和接收 | 依赖 ooder-common-client, ooder-org-web, ooder-vfs-web |

### scene-engine

场景引擎提供场景驱动的业务编排能力：

| 功能模块 | 说明 |
|----------|------|
| 核心引擎 | SceneEngine、SceneManager、WorkflowEngine |
| 协议层 | EngineProtocolProvider、协议适配器 |
| 技能集成 | SkillProviderRegistry、SkillService |
| 会话管理 | SessionManager、TokenManager |
| 事件系统 | EventEngine、事件监听和处理 |

**依赖：** agent-sdk-api、agent-sdk-core、ooder-common 各子模块

### ooder-annotation

注解模块提供 Ooder 平台的核心注解定义：

| 注解类型 | 说明 |
|----------|------|
| UI 组件 | @FormAnnotation, @GridAnnotation, @TreeAnnotation 等 |
| 事件 | @APIEvent, @ButtonEvent, @FieldEvent 等 |
| 数据绑定 | @DBField, @DBTable, @DBPrimaryKey 等 |
| Agent | @Agent, @AgentCapability, @Skill 等 |

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+

### 添加依赖

**Agent SDK API:**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

**Agent SDK Core:**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>
```

**Scene Engine:**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>
```

**Ooder Annotation:**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-annotation</artifactId>
    <version>2.3</version>
</dependency>
```

**ooder-database:**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-database</artifactId>
    <version>2.3</version>
</dependency>
```

### 构建项目

```bash
mvn clean install
```

或单独构建模块：

```bash
cd agent-sdk
mvn clean install
```

## 架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                    Ooder SDK 架构                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  应用层                                                          │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐   │
│  │ 用户应用   │  │ 组织管理   │  │ 技能市场   │  │ 协作平台   │   │
│  └───────────┘  └───────────┘  └───────────┘  └───────────┘   │
│                                                                 │
│  SDK 层 (agent-sdk)                                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ agent-sdk-api: Agent、Capability、Skill 接口定义          │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ agent-sdk-core: 核心实现，依赖 skills-framework          │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ skills-framework: Skill 生命周期、发现、安装             │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ llm-sdk-api / llm-sdk: LLM 驱动接口和实现                │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  引擎层 (scene-engine)                                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ 场景引擎        │  │ 协议驱动        │  │ 技能集成        │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  基础层 (ooder-common + ooder-annotation)                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ ooder-config    │  │ ooder-common-*  │  │ ooder-server    │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 版本历史

| 版本 | 说明 |
|------|------|
| 2.3 | 架构重构：agent-sdk 拆分为 api/core/skills-framework/llm-sdk 模块 |
| 2.2 | ooder-common 和 ooder-annotation 版本升级 |
| 1.0.0 | 统一 SDK 发布 |

### 子模块版本

| 模块 | 版本 |
|------|------|
| agent-sdk-api | 2.3 |
| agent-sdk-core | 2.3 |
| skills-framework | 2.3 |
| llm-sdk-api | 2.3 |
| llm-sdk | 2.3 |
| ooder-annotation | 2.3 |
| ooder-common-all | 2.3 |
| scene-engine | 2.3 |

详细变更请参阅 [CHANGELOG.md](CHANGELOG.md)

## 文档导航

| 文档 | 说明 | 目标读者 |
|------|------|----------|
| [ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md) | 架构指南、依赖关系 | 架构师 |
| [MODULE_DIVISION.md](MODULE_DIVISION.md) | 模块详细分工、接口列表 | 开发者 |
| [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) | 二次开发手册 | SDK 开发者 |
| [NAVIGATION.md](NAVIGATION.md) | 完整文档导航 | 所有用户 |
| [SKILLS_COLLABORATION.md](SKILLS_COLLABORATION.md) | Skills 协作规范 | Skills 开发者 |
| [RELEASE_NOTES_v2.3.md](RELEASE_NOTES_v2.3.md) | v2.3 发布说明 | 所有用户 |

## 相关项目

| 项目 | 说明 | 地址 |
|------|------|------|
| super-Agent | 核心框架 | [GitHub](https://github.com/oodercn/super-Agent) / [Gitee](https://gitee.com/ooderCN/super-Agent) |
| ooder-skills | 能力库 | [GitHub](https://github.com/oodercn/ooder-skills) / [Gitee](https://gitee.com/ooderCN/skills) |

## 贡献指南

欢迎贡献代码和提出问题！请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

- GitHub: [https://github.com/oodercn/ooder-sdk](https://github.com/oodercn/ooder-sdk)
- Gitee: [https://gitee.com/ooderCN/ooder-sdk](https://gitee.com/ooderCN/ooder-sdk)

---

**Made with ❤️ by Ooder Team**
