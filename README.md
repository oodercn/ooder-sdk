# Ooder SDK

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8+-green.svg)](https://openjdk.org/)

Ooder Agent 平台软件开发工具包。

## 项目结构

```
ooder-sdk/
├── agent-sdk/          # Agent SDK 核心模块
│   ├── src/            # 源代码
│   ├── docs/           # 文档
│   └── pom.xml         # Maven 配置
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

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>0.7.3</version>
</dependency>
```

### 构建项目

```bash
cd agent-sdk
mvn clean install
```

## 版本历史

| 版本 | 说明 |
|------|------|
| 0.7.3 | 协议增强、云托管、GitHub/Gitee 发现 |
| 0.7.2 | 能力中心、南北向协议完善 |
| 0.7.1 | 场景驱动架构 |
| 0.6.6 | 配置体系优化 |

## 相关项目

| 项目 | 说明 |
|------|------|
| [super-Agent](https://gitee.com/ooderCN/super-Agent) | 核心框架 |
| [ooder-Nexus](https://gitee.com/ooderCN/ooder-Nexus) | 分发枢纽 |
| [skills](https://gitee.com/ooderCN/skills) | 能力库 |
| [common](https://gitee.com/ooderCN/common) | 企业开发包 |

## 许可证

[MIT License](LICENSE)

## 贡献

欢迎提交 Issue 和 Pull Request。

---

**Ooder Team**
