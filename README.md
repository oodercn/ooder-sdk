# Ooder SDK

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8+-green.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)

Ooder Agent 平台软件开发工具包，包含 Agent SDK、通用组件和场景引擎。

## 项目结构

```
ooder-sdk/
├── agent-sdk/          # Agent SDK 核心模块 (v0.7.3)
├── ooder-annotation/   # 注解模块 (v2.2)
├── ooder-common/       # 通用组件模块 (v2.2)
├── scene-engine/       # 场景引擎 (v0.7.3)
├── pom.xml             # 父 POM
├── README.md
├── CHANGELOG.md
├── CONTRIBUTING.md
└── LICENSE
```

## 模块说明

### agent-sdk

Agent SDK 是 Ooder Agent 平台的核心开发工具包，提供：

- Agent 生命周期管理（End Agent、Route Agent、MCP Agent）
- 技能发现与安装（支持 GitHub/Gitee/本地文件系统）
- 场景配置与协作
- 网络通信（P2P、UDP、WebSocket）
- 安全认证

详细文档请参阅 [agent-sdk/README.md](agent-sdk/README.md)

### ooder-annotation

注解模块提供 Ooder 平台的核心注解定义：

| 注解类型 | 说明 |
|----------|------|
| UI 组件 | @FormAnnotation, @GridAnnotation, @TreeAnnotation 等 |
| 事件 | @APIEvent, @ButtonEvent, @FieldEvent 等 |
| 数据绑定 | @DBField, @DBTable, @DBPrimaryKey 等 |
| Agent | @Agent, @AgentCapability, @Skill 等 |

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
| drivers/mqtt | MQTT 协议驱动 |
| drivers/msg | 消息驱动 |
| drivers/org | 组织架构驱动 |
| drivers/vfs | VFS 驱动 |
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

### 代码示例

#### 创建 End Agent

```java
import net.ooder.sdk.OoderSDK;
import net.ooder.sdk.api.agent.EndAgent;
import net.ooder.sdk.infra.config.SDKConfiguration;

public class MyEndAgent {
    public static void main(String[] args) {
        SDKConfiguration config = new SDKConfiguration();
        config.setAgentId("my-end-agent-001");
        config.setAgentName("My End Agent");
        config.setAgentType(AgentType.END);
        
        OoderSDK sdk = OoderSDK.builder()
            .configuration(config)
            .build();
        
        sdk.initialize();
        sdk.start();
    }
}
```

#### 使用注解定义 Skill

```java
import net.ooder.annotation.Agent;
import net.ooder.annotation.AgentCapability;
import net.ooder.annotation.Skill;

@Agent(id = "my-agent", name = "My Agent")
@AgentCapability(name = "data-processing")
public class MyAgent {
    
    @Skill(id = "data-extract", name = "数据提取")
    public ExtractResult extractData(ExtractRequest request) {
        return new ExtractResult();
    }
}
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
│  │ Agent 管理 │ 技能发现 │ 场景配置 │ P2P 网络 │ 安全认证    │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  引擎层 (scene-engine)                                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ 场景引擎        │  │ 协议驱动        │  │ 技能模块        │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  基础层 (ooder-common + ooder-annotation)                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ 通用组件        │  │ 注解定义        │  │ 工具类          │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
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

详细变更请参阅 [CHANGELOG.md](CHANGELOG.md)

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
