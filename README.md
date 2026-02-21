# Ooder SDK

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8+-green.svg)](https://openjdk.org/)

Ooder Agent 平台软件开发工具包，包含 Agent SDK、通用组件和场景引擎。

## 项目结构

```
ooder-sdk/
├── agent-sdk/          # Agent SDK 核心模块 (v0.7.3)
├── ooder-annotation/   # 注解模块 (v2.2)
├── ooder-common/       # 通用组件模块 (v2.2)
├── scene-engine/       # 场景引擎 (v0.7.3)
├── pom.xml             # 父 POM
└── README.md
```

## 模块说明

### agent-sdk

Agent SDK 是 Ooder Agent 平台的核心开发工具包，提供：

- Agent 生命周期管理（End Agent、Route Agent、MCP Agent）
- 技能发现与安装
- 场景配置与协作
- 网络通信（P2P、UDP、WebSocket）
- 安全认证

详细文档请参阅 [agent-sdk/README.md](agent-sdk/README.md)

### ooder-annotation

注解模块提供 Ooder 平台的核心注解定义：

- UI 组件注解（@FormAnnotation, @GridAnnotation 等）
- 事件注解（@APIEvent, @ButtonEvent 等）
- 数据绑定注解（@DBField, @DBTable 等）
- Agent 注解（@Agent, @AgentCapability 等）

### ooder-common

通用组件模块提供企业级开发组件：

| 子模块 | 说明 |
|--------|------|
| ooder-common-client | 客户端通用组件 |
| ooder-config | 配置管理 |
| ooder-database | 数据库组件 |
| ooder-msg-web | 消息服务 |
| ooder-org-web | 组织架构服务 |
| ooder-vfs-web | 虚拟文件系统 |
| ooder-server | 服务器组件 |

### scene-engine

场景引擎提供场景驱动的业务编排能力：

| 子模块 | 说明 |
|--------|------|
| scene-engine | 核心引擎 |
| scene-gateway | 场景网关 |
| drivers/ | 协议驱动（MQTT、MSG、ORG、VFS） |
| skill-* | 技能模块 |

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+

### 添加依赖

**Agent SDK:**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>0.7.3</version>
</dependency>
```

**Ooder Annotation:**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-annotation</artifactId>
    <version>2.2</version>
</dependency>
```

**Scene Engine:**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>0.7.3</version>
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

## 版本历史

| 版本 | 说明 |
|------|------|
| 1.0.0 | 统一 SDK 发布，包含 agent-sdk、ooder-annotation、ooder-common、scene-engine |

### 子模块版本

| 模块 | 版本 |
|------|------|
| agent-sdk | 0.7.3 |
| ooder-annotation | 2.2 |
| ooder-common | 2.2 |
| scene-engine | 0.7.3 |

## 相关项目

| 项目 | 说明 |
|------|------|
| [super-Agent](https://gitee.com/ooderCN/super-Agent) | 核心框架 |
| [ooder-Nexus](https://gitee.com/ooderCN/ooder-Nexus) | 分发枢纽 |
| [skills](https://gitee.com/ooderCN/skills) | 能力库 |

## 许可证

[MIT License](LICENSE)

## 贡献

欢迎提交 Issue 和 Pull Request。

---

**Ooder Team**
