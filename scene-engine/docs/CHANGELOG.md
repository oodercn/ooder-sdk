# Scene Engine 变更日志

所有重要的变更都将记录在此文件中。

## [3.0.2] - 2026-04-08

### 变更

#### 版本统一
- 所有 ooder 依赖版本统一到 3.0.2
- ooder-sdk-parent 版本更新到 3.0.2
- agent-sdk 版本更新到 3.0.2
- scene-engine 版本更新到 3.0.2
- llm-sdk 版本更新到 3.0.2
- skills-framework 版本更新到 3.0.2
- ooder-annotation 版本更新到 3.0.2
- ooder-common-all 版本更新到 3.0.2
- ooder-api 版本更新到 3.0.2
- ooder-util 版本更新到 3.0.2

#### 清理
- 移除 META-INF/maven 下的旧版本 pom.xml 归档文件
- 归档 v2.3.x 版本文档到 docs/archive 目录

---

## [3.0.0] - 2026-03-25

### 新增

#### SPI 驱动架构
- 新增 `StorageProvider` SPI 接口
- 新增 `LlmProvider` SPI 接口
- 新增 `VectorStore` SPI 接口

#### 驱动实现
- **Tiny 驱动**: 文件存储 + Ollama LLM + 内存向量
- **Small 驱动**: JDBC 存储 + 远程 LLM API + Milvus Lite
- **Enterprise 驱动**: 分布式存储 + 多模型路由 + 分布式向量库

#### 降级实现
- `InMemoryStorageProvider`: 内存存储 (重启丢失)
- `MockLlmProvider`: Mock LLM (返回模拟响应)
- `NoOpVectorStore`: 空向量存储 (不支持检索)

#### RAD 集成
- 新增 `RadAdapter` 接口
- 新增 `FormRadAdapter` 表单适配器

### 变更

#### 依赖冲突解决
- `JsonStorageService` 重命名为 `SceneContextStorageService`
- `RequestMappingConfig` 中 `@Primary` 替换为 `@ConditionalOnMissingBean`
- `AgentMessageBusImpl` 添加 `@Primary` 和条件装配
- `SecureAgentMessageBus` 添加条件装配

#### 条件装配
- `SceneEngineIntegration`: 添加 `@ConditionalOnBean(SceneEngine.class)`
- `SkillSDKAdapter`: 添加 `@ConditionalOnBean({SkillRegistry.class, SkillInstaller.class, SkillDiscoverer.class})`
- `CapRouter`: 添加 `@ConditionalOnBean(CapRegistry.class)`
- `PermissionInterceptor`: 添加 `@ConditionalOnBean(PermissionService.class)`
- `FailoverManagerImpl`: 添加 `@ConditionalOnBean({AgentSessionManager.class, AgentMessageBus.class})`
- `SceneGroupManager`: 添加 `@ConditionalOnProperty`
- `AuditServiceAdapter`: 添加 `@ConditionalOnBean`

#### 依赖注入优化
- `SkillSwitchHandlerImpl`: 字段注入 → 构造函数注入
- `SkillSDKAdapter`: 字段注入 → 构造函数注入
- `SkillInstanceFactory`: 字段注入 → 构造函数注入

### 配置开关

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `scene.engine.driver` | 驱动类型 | 无 |
| `scene.engine.fallback.enabled` | 启用降级实现 | true |
| `scene.engine.rad.enabled` | 启用 RAD 集成 | false |
| `scene.engine.group.enabled` | 启用场景组管理 | true |
| `scene.engine.message.secure` | 启用安全消息总线 | false |
| `scene.engine.vector.enabled` | 启用向量存储 | true |
| `scene.engine.auto-config.enabled` | 启用自动配置 | true |

### 移除

- 移除 `JsonStorageService` (重命名为 `SceneContextStorageService`)

---

## [2.3.1] - 2026-03-14

### 新增
- Spring Boot Starter 支持
- Agent 实现公共 API 暴露

### 变更
- 升级到 JDK 21
- 升级到 Spring Boot 3.2.5

---

## [2.3.0] - 2026-02-28

### 新增
- 场景组管理
- 能力发现协议
- 知识库集成

---

**格式说明**: 基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)
