# 依赖冲突协作请求汇总

## 概述

**日期**: 2026-03-24  
**来源报告**: `e:\github\ooder-skills\mvp\DEPENDENCY_CONFLICT_REPORT.md`  
**状态**: 需要各团队协作解决

---

## 已完成的修改 (scene-engine 3.0.0)

### @Configuration proxyBeanMethods 修复

所有 `@Configuration` 类已添加 `proxyBeanMethods = false`，解决 CGLIB 增强失败问题：

| 文件 | 状态 |
|------|------|
| `SceneEngineAutoConfiguration.java` (autoconfigure包) | ✅ 已修改 |
| `SceneEngineAutoConfiguration.java` (config包) | ✅ 已修改 |
| `SdkSceneGroupAutoConfiguration.java` | ✅ 已修改 |
| `DiscoveryAutoConfiguration.java` | ✅ 已修改 |
| `KnowledgePersistenceAutoConfiguration.java` | ✅ 已修改 |
| `VectorStoreAutoConfiguration.java` | ✅ 已修改 |
| `KeyManagementAutoConfiguration.java` | ✅ 已修改 |
| `LlmAuditAutoConfiguration.java` | ✅ 已修改 |
| `RequestMappingConfig.java` | ✅ 已修改 |
| `WebMvcConfig.java` | ✅ 已修改 |
| `EventConfiguration.java` | ✅ 已修改 |

---

## 各团队待处理事项

### skill-common 团队

| 序号 | 任务 | 优先级 | 预计完成时间 |
|------|------|--------|--------------|
| 1 | 确认 `AuthApi` 接口规范 | 高 | 2026-03-25 |
| 2 | 为 `JsonStorageService` 添加 `@ConditionalOnMissingBean` | 高 | 2026-03-25 |
| 3 | 添加接口文档说明 | 中 | 2026-03-26 |

**协作文档路径**: 请复制本文档到 skill-common 仓库

---

### scene-engine 团队

| 序号 | 任务 | 优先级 | 预计完成时间 |
|------|------|--------|--------------|
| 1 | 重命名 `JsonStorageService` 或添加 `@ConditionalOnMissingBean` | 高 | 2026-03-25 |
| 2 | 提供自动配置排除选项 | 中 | 2026-03-26 |
| 3 | 检查并减少 `@Primary` 注解使用 | 中 | 2026-03-26 |

**协作文档路径**: `e:\github\ooder-sdk\scene-engine\docs\COLLABORATION_REQUEST_MVP_DEPENDENCY.md`

---

### mvp-core 团队

| 序号 | 任务 | 优先级 | 预计完成时间 |
|------|------|--------|--------------|
| 1 | 删除重复的 `AuthController` | 高 | 2026-03-25 |
| 2 | 修改 `CapabilityConfig` 使用 `@Autowired` | 高 | 2026-03-25 |
| 3 | 检查并移除不必要的 `@Primary` 注解 | 中 | 2026-03-26 |
| 4 | 确认 `@Lazy` 对 Native 打包的影响 | 中 | 2026-03-26 |

**协作文档路径**: 请复制本文档到 `e:\github\ooder-skills\mvp\docs\`

---

## 文件分发指南

请将以下内容复制到各团队仓库：

### 1. skill-common 团队

复制以下内容到 skill-common 仓库的 `docs/` 目录：
- 本文档中 "skill-common 团队" 部分
- 原始报告: `e:\github\ooder-skills\mvp\DEPENDENCY_CONFLICT_REPORT.md`

### 2. mvp-core 团队

复制以下内容到 `e:\github\ooder-skills\mvp\docs/` 目录：
- 本文档中 "mvp-core 团队" 部分
- 原始报告: `e:\github\ooder-skills\mvp\DEPENDENCY_CONFLICT_REPORT.md`

### 3. scene-engine 团队

已创建协作文档：
- `e:\github\ooder-sdk\scene-engine\docs\COLLABORATION_REQUEST_MVP_DEPENDENCY.md`

---

## 联系方式

| 团队 | 仓库路径 |
|------|----------|
| scene-engine | `e:\github\ooder-sdk\scene-engine` |
| skill-common | 待确认 |
| mvp-core | `e:\github\ooder-skills\mvp` |

---

*文档生成时间: 2026-03-24 19:25*
