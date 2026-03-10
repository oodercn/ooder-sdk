# SceneEngine - Ooder 场景引擎服务

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Version](https://img.shields.io/badge/version-2.3.1-green.svg)]()
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://openjdk.org/)

## 项目概述

SceneEngine 是 Ooder Agent 平台的场景引擎服务，提供专业技能服务和场景驱动的能力编排。基于 Skills 架构，支持组织管理、文件存储、消息通信等核心场景。

### 核心特性

- **技能驱动架构**：v3.0 新模型，技能是核心，场景是形态
- **声明式分类**：开发时声明，运行时只读，消除模糊地带
- **技能模块化**：可插拔的 Skill 能力封装
- **CAP 能力路由**：统一的能力地址空间（00-FF）
- **多协议发现**：UDP、mDNS、SkillCenter 发现
- **云原生部署**：支持 Kubernetes 集群部署

## 版本信息

| 版本 | 发布日期 | SDK 版本 | 说明 |
|------|----------|----------|------|
| **2.3.1** | 2026-03-10 | agent-sdk 3.0 | v3.0 模型重构、技能形态分类 |
| 2.3 | 2026-02-23 | agent-sdk 2.3 | 架构重构、能力发现抽象 |
| 0.7.3 | 2026-02-20 | agent-sdk 0.7.3 | 场景引擎重构 |

## 项目结构

```
scene-engine/
├── src/main/java/net/ooder/scene/
│   ├── core/                    # 核心引擎
│   │   ├── driver/              # 驱动框架
│   │   ├── provider/            # 能力提供者
│   │   ├── security/            # 安全模块
│   │   └── skill/               # 技能管理
│   ├── discovery/               # 能力发现
│   │   ├── provider/            # 发现提供者
│   │   └── impl/                # 实现类
│   ├── protocol/                # 协议实现
│   │   └── impl/                # 协议适配器
│   ├── event/                   # 事件系统
│   ├── session/                 # 会话管理
│   ├── workflow/                # 工作流
│   └── skill/                   # 技能接口（v2.3新增）
│       ├── llm/                 # LLM Provider 接口
│       ├── vector/              # 向量存储接口
│       ├── security/            # 安全访问接口
│       └── audit/               # 审计日志接口
├── northbound-core/             # 北向协议核心
├── config/                      # 配置文件
└── docs/                        # 文档
```

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 8+ | 运行环境 |
| Maven | 3.6+ | 构建工具 |
| agent-sdk | 2.3 | Ooder SDK |

## 核心概念

### CAP（Capability Address Protocol）

能力地址协议，统一的能力寻址方案：

- **00-3F**：系统能力（System）
- **40-9F**：通用能力（Common）
- **A0-FF**：扩展能力（Extension）

### 能力发现

支持多种发现方式：

| 发现方式 | 范围 | 协议 |
|----------|------|------|
| UDP Broadcast | 局域网 | UDP |
| mDNS | 局域网 | DNS-SD |
| SkillCenter | 全局 | HTTP |
| Local FS | 本地 | 文件系统 |

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+

### 编译项目

```bash
# 克隆项目
git clone https://github.com/ooderCN/ooder-sdk.git

# 编译
cd ooder-sdk/scene-engine
mvn clean compile

# 打包
mvn clean package -DskipTests
```

## API 端点

### 场景管理

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/scenes` | GET | 场景列表 |
| `/api/v1/scenes/{id}` | GET | 场景详情 |
| `/api/v1/scenes` | POST | 创建场景 |

### 能力调用

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/cap/{capId}` | POST | 调用能力 |

## 相关项目

| 项目 | 说明 | 地址 |
|------|------|------|
| **agent-sdk** | 核心 SDK | [github.com/ooderCN/ooder-sdk](https://github.com/ooderCN/ooder-sdk) |
| **ooder-skills** | 技能库 | [github.com/ooderCN/ooder-skills](https://github.com/ooderCN/ooder-skills) |

## 文档资源

| 文档 | 说明 |
|------|------|
| [Agent 协议](docs/protocol/v2.3/agent-protocol.md) | v2.3 协议规范（推荐） |
| [协议主文档](docs/protocol/v2.3/protocol-main.md) | v2.3 协议体系架构 |
| [技能发现协议](docs/protocol/v2.3/skill-discovery-protocol.md) | v2.3 发现协议规范（含实现指南） |

## 新增接口（v2.3）

### 技能接口

| 接口 | 包路径 | 说明 |
|------|--------|------|
| `LlmProvider` | `net.ooder.scene.skill.llm` | LLM Provider 标准接口 |
| `StreamHandler` | `net.ooder.scene.skill.llm` | 流式处理回调接口 |
| `VectorStore` | `net.ooder.scene.skill.vector` | 向量存储接口 |
| `EmbeddingService` | `net.ooder.scene.skill.vector` | 嵌入服务接口 |
| `SecureResourceAccessor` | `net.ooder.scene.skill.security` | 安全资源访问器 |
| `AuditLogger` | `net.ooder.scene.skill.audit` | 审计日志接口 |

### 使用示例

```java
// 使用 LlmProvider
LlmProvider provider = ...;
provider.chat("gpt-4", messages, options);

// 使用 VectorStore
VectorStore store = ...;
store.insert("id", vector, metadata);
List<SearchResult> results = store.search(queryVector, 10, filters);
```

## 许可证

本项目采用 Apache License 2.0 许可证 - 详见 [LICENSE](LICENSE) 文件

---

**Made with ❤️ by Ooder Team**
