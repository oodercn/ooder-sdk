# Ooder SDK 文档导航

> **版本**: 2.3  
> **日期**: 2026-03-01

---

## 快速导航

### 新手上路
1. [README.md](README.md) - 项目概览
2. [ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md) - 架构指南
3. [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - 二次开发手册
4. [MODULE_DIVISION.md](MODULE_DIVISION.md) - 模块分工

### 开发参考
- [SKILLS_COLLABORATION.md](SKILLS_COLLABORATION.md) - Skills 协作
- [RELEASE_NOTES_v2.3.md](RELEASE_NOTES_v2.3.md) - 发布说明
- [CHANGELOG.md](CHANGELOG.md) - 变更日志

---

## 文档目录结构

### 根目录文档

| 文档 | 说明 | 目标读者 |
|------|------|----------|
| [README.md](README.md) | 项目说明、快速开始 | 所有用户 |
| [ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md) | 架构指南、依赖关系 | 架构师 |
| [MODULE_DIVISION.md](MODULE_DIVISION.md) | 模块详细分工 | 开发者 |
| [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) | 二次开发手册 | SDK 开发者 |
| [NAVIGATION.md](NAVIGATION.md) | 文档导航 | 所有用户 |
| [SKILLS_COLLABORATION.md](SKILLS_COLLABORATION.md) | Skills 协作规范 | Skills 开发者 |
| [RELEASE_NOTES_v2.3.md](RELEASE_NOTES_v2.3.md) | v2.3 发布说明 | 所有用户 |
| [CHANGELOG.md](CHANGELOG.md) | 变更日志 | 开发者 |
| [CONTRIBUTING.md](CONTRIBUTING.md) | 贡献指南 | 贡献者 |

### docs/ 目录文档

| 文档 | 说明 | 目标读者 |
|------|------|----------|
| [HTTP_CLIENT_BEST_PRACTICES.md](docs/HTTP_CLIENT_BEST_PRACTICES.md) | HTTP 客户端最佳实践 | Skills 开发者 |

### Agent SDK 文档

| 路径 | 说明 |
|------|------|
| [agent-sdk/README.md](agent-sdk/README.md) | Agent SDK 说明 |
| [agent-sdk/CHANGELOG.md](agent-sdk/CHANGELOG.md) | Agent SDK 变更日志 |
| [agent-sdk/docs/architecture/SDK_2.3_ARCHITECTURE.md](agent-sdk/docs/architecture/SDK_2.3_ARCHITECTURE.md) | SDK 2.3 架构 |
| [agent-sdk/docs/architecture/NORTHBOUND_SOUTHBOUND_ARCHITECTURE.md](agent-sdk/docs/architecture/NORTHBOUND_SOUTHBOUND_ARCHITECTURE.md) | 南北向架构 |
| [agent-sdk/docs/guides/QUICK_START.md](agent-sdk/docs/guides/QUICK_START.md) | 快速开始指南 |
| [agent-sdk/docs/manuals/NORTHBOUND_SERVICE_MANUAL.md](agent-sdk/docs/manuals/NORTHBOUND_SERVICE_MANUAL.md) | 北向服务手册 |
| [agent-sdk/docs/manuals/SOUTHBOUND_SERVICE_MANUAL.md](agent-sdk/docs/manuals/SOUTHBOUND_SERVICE_MANUAL.md) | 南向服务手册 |
| [agent-sdk/docs/manuals/SECURITY_MANUAL.md](agent-sdk/docs/manuals/SECURITY_MANUAL.md) | 安全手册 |

### Scene Engine 文档

| 路径 | 说明 |
|------|------|
| [scene-engine/README.md](scene-engine/README.md) | 场景引擎说明 |
| [scene-engine/docs/protocol/v2.3/protocol-main.md](scene-engine/docs/protocol/v2.3/protocol-main.md) | 协议主文档 |
| [scene-engine/docs/protocol/v2.3/skill-discovery-protocol.md](scene-engine/docs/protocol/v2.3/skill-discovery-protocol.md) | 技能发现协议 |
| [scene-engine/docs/protocol/v2.3/agent-protocol.md](scene-engine/docs/protocol/v2.3/agent-protocol.md) | Agent 协议 |
| [scene-engine/docs/SDK-COLLABORATION.md](scene-engine/docs/SDK-COLLABORATION.md) | SDK 协作 |

### Ooder Common 文档

| 路径 | 说明 |
|------|------|
| [ooder-common/README.md](ooder-common/README.md) | 通用组件说明 |
| [ooder-common/JDSSERVER-GUIDE.md](ooder-common/JDSSERVER-GUIDE.md) | JDSServer 指南 |
| [ooder-common/RELEASE_STATEMENT.md](ooder-common/RELEASE_STATEMENT.md) | 发布声明 |

---

## 按角色导航

### 如果你是架构师

推荐阅读顺序：
1. [ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md) - 整体架构
2. [MODULE_DIVISION.md](MODULE_DIVISION.md) - 模块分工
3. [agent-sdk/docs/architecture/SDK_2.3_ARCHITECTURE.md](agent-sdk/docs/architecture/SDK_2.3_ARCHITECTURE.md) - SDK 架构

### 如果你是 SDK 开发者

推荐阅读顺序：
1. [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) - 开发手册
2. [MODULE_DIVISION.md](MODULE_DIVISION.md) - 模块分工
3. [agent-sdk/docs/guides/QUICK_START.md](agent-sdk/docs/guides/QUICK_START.md) - 快速开始

### 如果你是 Skills 开发者

推荐阅读顺序：
1. [SKILLS_COLLABORATION.md](SKILLS_COLLABORATION.md) - 协作规范
2. [scene-engine/docs/SDK-COLLABORATION.md](scene-engine/docs/SDK-COLLABORATION.md) - SDK 协作
3. [agent-sdk/docs/manuals/NORTHBOUND_SERVICE_MANUAL.md](agent-sdk/docs/manuals/NORTHBOUND_SERVICE_MANUAL.md) - 北向服务

### 如果你是运维人员

推荐阅读顺序：
1. [README.md](README.md) - 项目说明
2. [RELEASE_NOTES_v2.3.md](RELEASE_NOTES_v2.3.md) - 发布说明
3. [ooder-common/JDSSERVER-GUIDE.md](ooder-common/JDSSERVER-GUIDE.md) - JDSServer 指南

---

## 接口文档导航

### 引擎层接口 (scene-engine)

| 包路径 | 接口 | 说明 |
|--------|------|------|
| `net.ooder.scene.skill.llm` | [LlmProvider](scene-engine/src/main/java/net/ooder/scene/skill/llm/LlmProvider.java) | LLM Provider |
| `net.ooder.scene.skill.llm` | [StreamHandler](scene-engine/src/main/java/net/ooder/scene/skill/llm/StreamHandler.java) | 流式处理 |
| `net.ooder.scene.skill.vector` | [VectorStore](scene-engine/src/main/java/net/ooder/scene/skill/vector/VectorStore.java) | 向量存储 |
| `net.ooder.scene.skill.vector` | [EmbeddingService](scene-engine/src/main/java/net/ooder/scene/skill/vector/EmbeddingService.java) | 嵌入服务 |
| `net.ooder.scene.skill.security` | [SecureResourceAccessor](scene-engine/src/main/java/net/ooder/scene/skill/security/SecureResourceAccessor.java) | 安全访问（scene-engine） |
| `net.ooder.scene.skill.audit` | [AuditLogger](scene-engine/src/main/java/net/ooder/scene/skill/audit/AuditLogger.java) | 审计日志（scene-engine） |
| `net.ooder.scene.skill.knowledge` | [KnowledgeBaseApi](scene-engine/src/main/java/net/ooder/scene/skill/knowledge/KnowledgeBaseApi.java) | 知识库接口（scene-engine） |
| `net.ooder.scene.skill.rag` | [RagApi](scene-engine/src/main/java/net/ooder/scene/skill/rag/RagApi.java) | RAG 接口（scene-engine） |
| `net.ooder.scene.ui` | [NexusUiRegistry](scene-engine/src/main/java/net/ooder/scene/ui/NexusUiRegistry.java) | UI 注册表（scene-engine） |
| `net.ooder.scene.ui` | [NexusUiLoader](scene-engine/src/main/java/net/ooder/scene/ui/NexusUiLoader.java) | UI 加载器（scene-engine） |
| `net.ooder.scene.ui` | [NexusUiController](scene-engine/src/main/java/net/ooder/scene/ui/NexusUiController.java) | UI 管理 API（scene-engine） |
| `net.ooder.scene.monitor` | [SceneMonitor](scene-engine/src/main/java/net/ooder/scene/monitor/SceneMonitor.java) | 场景监控 |

### SDK API 层接口 (agent-sdk-api)

| 包路径 | 接口 | 说明 |
|--------|------|------|
| `net.ooder.sdk.api` | [Agent](agent-sdk/agent-sdk-api/src/main/java/net/ooder/sdk/api/Agent.java) | Agent 接口 |
| `net.ooder.sdk.api.capability` | [Capability](agent-sdk/agent-sdk-api/src/main/java/net/ooder/sdk/api/capability/Capability.java) | 能力接口 |
| `net.ooder.sdk.api.scene` | [SceneManager](agent-sdk/agent-sdk-api/src/main/java/net/ooder/sdk/api/scene/SceneManager.java) | 场景管理 |
| `net.ooder.sdk.api.connection` | [ConnectionTestService](agent-sdk/agent-sdk-api/src/main/java/net/ooder/sdk/api/connection/ConnectionTestService.java) | 连接测试 |

### Skills Framework 接口

| 包路径 | 接口/类 | 说明 |
|--------|--------|------|
| `net.ooder.skills.api` | [SkillPackageManager](agent-sdk/skills-framework/src/main/java/net/ooder/skills/api/SkillPackageManager.java) | Skill 包管理器 |
| `net.ooder.skills.api` | [InstallResultWithDependencies](agent-sdk/skills-framework/src/main/java/net/ooder/skills/api/InstallResultWithDependencies.java) | 带依赖的安装结果 |

---

## 模块源码导航

### Agent SDK

```
agent-sdk/
├── agent-sdk-api/src/main/java/net/ooder/sdk/api/
│   ├── Agent.java
│   ├── capability/
│   ├── scene/
│   └── connection/
├── agent-sdk-core/src/main/java/net/ooder/sdk/core/
│   └── connection/
├── llm-sdk-api/src/main/java/net/ooder/sdk/llm/api/
├── llm-sdk/src/main/java/net/ooder/sdk/llm/
└── skills-framework/src/main/java/net/ooder/sdk/skill/
```

### Scene Engine

```
scene-engine/src/main/java/net/ooder/scene/
├── core/
├── discovery/
├── protocol/
├── event/
├── session/
├── workflow/
└── skill/              # 新增
    ├── llm/            # LLM 接口
    ├── vector/         # 向量接口
    ├── security/       # 安全接口
    └── audit/          # 审计接口
```

### Ooder Common

```
ooder-common/
├── ooder-config/src/main/java/net/ooder/config/
├── ooder-database/src/main/java/net/ooder/common/database/
├── ooder-common-client/src/main/java/net/ooder/common/client/
├── ooder-server/src/main/java/net/ooder/server/
│   └── connection/     # 连接测试
├── ooder-vfs-web/src/main/java/net/ooder/vfs/
├── ooder-org-web/src/main/java/net/ooder/org/
└── ooder-msg-web/src/main/java/net/ooder/msg/
```

---

## 相关链接

- **GitHub**: https://github.com/oodercn/ooder-sdk
- **Gitee**: https://gitee.com/ooderCN/ooder-sdk
- **Issues**: https://github.com/oodercn/ooder-sdk/issues
- **Maven Central**: https://central.sonatype.com/artifact/net.ooder

---

**Made with ❤️ by Ooder Team**
