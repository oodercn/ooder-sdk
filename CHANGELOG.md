# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-02-22

### Added

- 统一 SDK 发布，整合以下模块：
  - agent-sdk v0.7.3
  - ooder-annotation v2.2
  - ooder-common v2.2
  - scene-engine v0.7.3
- 创建父 POM 统一依赖管理
- 添加完整的 README 文档
- 添加 CHANGELOG.md
- 添加 CONTRIBUTING.md

### agent-sdk v0.7.3

#### Added

- 9 种技能发现方法 (UDP/DHT/mDNS/GitHub/Gitee/...)
- GitHub/Gitee 仓库技能发现
- 云托管协议 (K8s 部署支持)
- 场景组协作协议
- 离线服务支持
- 事件总线 (EventBus)

### ooder-annotation v2.2

#### Added

- UI 组件注解 (@FormAnnotation, @GridAnnotation, @TreeAnnotation)
- 事件注解 (@APIEvent, @ButtonEvent, @FieldEvent)
- 数据绑定注解 (@DBField, @DBTable, @DBPrimaryKey)
- Agent 注解 (@Agent, @AgentCapability, @Skill)

### ooder-common v2.2

#### Added

- ooder-common-client: 客户端通用组件
- ooder-config: 配置管理
- ooder-database: 数据库组件
- ooder-msg-web: 消息服务
- ooder-org-web: 组织架构服务
- ooder-vfs-web: 虚拟文件系统
- ooder-server: 服务器组件

### scene-engine v0.7.3

#### Added

- 场景引擎核心
- 场景网关
- 协议驱动 (MQTT, MSG, ORG, VFS)
- 技能模块 (skill-org, skill-vfs, skill-msg, skill-mqtt)

---

## 版本说明

### 版本号格式

- **主版本号**: 重大架构变更
- **次版本号**: 新功能添加
- **修订号**: Bug 修复和小改进

### 子模块版本

各子模块保持独立版本号，父 POM 版本表示整体 SDK 发布版本。

| 模块 | 当前版本 | 说明 |
|------|----------|------|
| agent-sdk | 0.7.3 | Agent SDK 核心 |
| ooder-annotation | 2.2 | 注解模块 |
| ooder-common | 2.2 | 通用组件 |
| scene-engine | 0.7.3 | 场景引擎 |
