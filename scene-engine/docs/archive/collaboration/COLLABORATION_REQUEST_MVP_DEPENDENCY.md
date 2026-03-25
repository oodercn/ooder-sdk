# 协作请求: 依赖冲突解决

## 概述

**来源团队**: mvp-core 团队  
**目标团队**: scene-engine 团队  
**日期**: 2026-03-24  
**优先级**: 高  
**关联报告**: `e:\github\ooder-skills\mvp\DEPENDENCY_CONFLICT_REPORT.md`

---

## 问题描述

### 1. JsonStorageService Bean 冲突

**冲突类**:
- `net.ooder.scene.skill.engine.context.impl.JsonStorageService`
- `net.ooder.skill.common.storage.JsonStorageService`

**问题描述**: 两个不同包路径的 `JsonStorageService` 类都带有 `@Service` 注解，导致 Spring 容器中存在多个同名 Bean。

**建议解决方案**:
```java
@Service
@ConditionalOnMissingBean(name = "jsonStorageService")
public class JsonStorageService implements ContextStorageService {
    // ...
}
```

或者重命名类:
```java
@Service("contextJsonStorageService")
public class JsonStorageService implements ContextStorageService {
    // ...
}
```

---

### 2. 自动配置排除选项

**问题描述**: `spring.factories` 中定义了多个自动配置类，部分项目可能不需要全部加载。

**当前配置** (`META-INF/spring.factories`):
```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  net.ooder.scene.autoconfigure.SceneEngineAutoConfiguration,\
  net.ooder.scene.config.SceneEngineAutoConfiguration,\
  net.ooder.scene.bridge.SdkSceneGroupAutoConfiguration,\
  net.ooder.scene.discovery.config.DiscoveryAutoConfiguration,\
  net.ooder.scene.llm.config.LlmAuditAutoConfiguration,\
  net.ooder.scene.security.config.KeyManagementAutoConfiguration,\
  net.ooder.scene.skill.config.KnowledgePersistenceAutoConfiguration,\
  net.ooder.scene.skill.config.VectorStoreAutoConfiguration
```

**建议**: 添加配置开关，允许用户排除不需要的自动配置:

```yaml
scene:
  engine:
    enabled: true
    exclude:
      - net.ooder.scene.bridge.SdkSceneGroupAutoConfiguration
      - net.ooder.scene.discovery.config.DiscoveryAutoConfiguration
```

---

### 3. @Primary 注解冲突

**问题描述**: scene-engine 中多个 Bean 使用了 `@Primary` 注解，与其他模块冲突。

**建议**: 
- 减少使用 `@Primary` 注解
- 使用 `@Qualifier` 明确指定 Bean
- 或提供配置选项禁用默认实现

---

## 已完成的修改 (scene-engine 3.0.0)

1. ✅ 所有 `@Configuration` 类已添加 `proxyBeanMethods = false`
2. ✅ 自动配置类已添加 `@ConditionalOnProperty` 开关
3. ✅ `JsonStorageService` 已重命名为 `SceneContextStorageService`
4. ✅ `RequestMappingConfig` 中的 `@Primary` 注解已替换为 `@ConditionalOnMissingBean`
5. ✅ `VectorStoreAutoConfiguration` 已添加 `@ConditionalOnProperty` 开关
6. ✅ `SceneEngineAutoConfiguration` 已添加 `@ConditionalOnProperty` 开关
7. ✅ 新增 SPI 接口定义: `StorageProvider`, `LlmProvider`, `VectorStore`
8. ✅ 新增驱动实现: Tiny (文件存储), Small (JDBC), Enterprise (分布式)
9. ✅ 新增降级实现: `InMemoryStorageProvider`, `MockLlmProvider`, `NoOpVectorStore`
10. ✅ 新增 RAD 适配器: `RadAdapter`, `FormRadAdapter`
11. ✅ 核心组件添加 `@ConditionalOnBean` 条件装配

---

## 配置开关汇总

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `scene.engine.enabled` | 启用 Scene Engine 核心服务 | false |
| `scene.engine.auto-config.enabled` | 启用 Scene Engine 自动配置 | true |
| `scene.engine.vector.enabled` | 启用向量存储自动配置 | true |
| `scene.engine.discovery.enabled` | 启用发现服务 | true |
| `scene.engine.llm.audit.enabled` | 启用 LLM 审计服务 | true |
| `scene.engine.key.management.enabled` | 启用密钥管理服务 | true |
| `scene.bridge.sdk.enabled` | 启用 SDK SceneGroup 桥接 | false |
| `scene.knowledge.persistence.enabled` | 启用知识库持久化 | false |

---

## 请求事项

| 序号 | 请求内容 | 优先级 | 状态 | 完成时间 |
|------|----------|--------|------|----------|
| 1 | 重命名 `JsonStorageService` 或添加 `@ConditionalOnMissingBean` | 高 | ✅ 已完成 | 2026-03-24 |
| 2 | 提供自动配置排除选项 | 中 | ✅ 已完成 | 2026-03-24 |
| 3 | 检查并减少 `@Primary` 注解使用 | 中 | ✅ 已完成 | 2026-03-24 |

---

## 联系方式

- **mvp-core 团队**: [提交 Issue](https://github.com/oodercn/ooder-skills/issues)
- **scene-engine 仓库**: `e:\github\ooder-sdk\scene-engine`

---

*协作请求生成时间: 2026-03-24 19:15*
