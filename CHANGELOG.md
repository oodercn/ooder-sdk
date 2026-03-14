# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.3.1] - 2026-03-10

### Added

- SceneEngine LLM 代理层 (Beta)
  - 用户-Agent-连接三层隔离架构
  - LlmConnectionManager 连接池管理
  - AgentSessionManager 四级缓存设计
  - UserLlmSessionManager 配额管理
  - 生命周期监听机制
  - 监控和统计功能
- Skills 规范配置文档
  - 三闭环检查要求
  - 字典表规范
  - API 响应格式规范
  - 命名规范和开发流程

### Fixed

- JDSConfig 配置问题修复
  - 增加 JDSHome 默认值 `./JDSHome`
  - 自动创建目录结构
  - 避免 NullPointerException
- JDSInit 类加载异常处理优化
  - 改为警告日志输出
  - 避免打印完整堆栈
- LLM 连接池稳定性改进
  - 优化连接池共享机制
  - 改进引用计数管理

### Changed

- Maven 编译速度优化
  - 在 local profile 中禁用 Javadoc 插件
  - 编译时间从 7分56秒减少到 3分35秒
- 统一版本号到 2.3.1
  - 根 pom.xml
  - 所有子模块 pom.xml

## [2.3.0] - 2026-03-01

### Added

- 统一 SDK 发布 v2.3
- 版本号统一规范
- 完整的文档体系

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
