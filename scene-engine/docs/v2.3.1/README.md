# Scene Engine v2.3.1 官方文档

**版本**: v2.3.1  
**发布日期**: 2026-03-22  
**状态**: 正式发布

---

## 文档目录

| 分册 | 内容 | 文件 |
|------|------|------|
| **01-概述** | 架构概览、快速开始、命名规范 | [01-overview/](01-overview/) |
| **02-核心API** | LLM服务、配置、适配器 | [02-core-api/](02-core-api/) |
| **03-对话服务** | 多轮对话、流式对话、工具调用 | [03-conversation/](03-conversation/) |
| **04-知识库** | 知识库管理、文档管理、检索 | [04-knowledge/](04-knowledge/) |
| **05-术语服务** | 术语解析、缩写扩展、同义词 | [05-terminology/](05-terminology/) |
| **06-交互反馈** | 反馈循环、QA提取、自动学习 | [06-feedback/](06-feedback/) |
| **07-RAG** | 向量存储、RAG Pipeline、自适应检索 | [07-rag/](07-rag/) |
| **08-工具调用** | 工具注册、编排、Function Calling | [08-tool/](08-tool/) |
| **09-SPI** | 服务暴露、插件开发 | [09-spi/](09-spi/) |
| **10-集成** | Spring Boot集成、配置参考、Skill集成、Gitee发现器 | [10-integration/](10-integration/) |
| **11-场景配置** | 场景配置加载、验证、激活 | [11-scene-config/](11-scene-config/) |
| **12-最佳实践** | 开发规范、性能优化、安全建议 | [12-best-practices/](12-best-practices/) |
| **13-参考** | API索引、错误码、版本历史 | [13-reference/](13-reference/) |

---

## 快速导航

### 新用户入门
1. [概述 - 快速开始](01-overview/02-quickstart.md)
2. [核心API - LLM服务](02-core-api/01-llm-service.md)
3. [对话服务 - 基础对话](03-conversation/01-basic.md)

### 核心功能
- [知识库管理](04-knowledge/01-knowledge-base.md)
- [术语解析](05-terminology/01-terminology-service.md)
- [交互反馈](06-feedback/01-feedback-loop.md)
- [RAG检索](07-rag/01-rag-pipeline.md)
- [工具调用](08-tool/01-tool-registry.md)

### 场景配置 (v2.3.1 新增)
- [场景配置加载](11-scene-config/01-scene-config-loader.md)
- [场景验证](11-scene-config/02-scene-validation.md)
- [场景激活服务](11-scene-config/03-scene-activation.md)
- [数据初始化流程](11-scene-config/04-scene-initialization-flow.md)

### 高级特性
- [SPI服务暴露](09-spi/01-service-provider.md)
- [Spring Boot集成](10-integration/01-spring-boot.md)
- [Skill控制器工厂](10-integration/03-skill-controller-factory.md)
- [Gitee技能发现器](10-integration/04-gitee-discovery.md)
- [性能优化](12-best-practices/02-performance.md)

---

## 版本特性

### v2.3.1 新特性

| 特性 | 说明 | 文档 |
|------|------|------|
| **场景配置加载** | 从 skill.yaml 加载场景配置 | [11-scene-config/01-scene-config-loader.md](11-scene-config/01-scene-config-loader.md) |
| **场景配置验证** | 验证场景配置完整性 | [11-scene-config/02-scene-validation.md](11-scene-config/02-scene-validation.md) |
| **场景激活服务** | 执行激活步骤、注册菜单 | [11-scene-config/03-scene-activation.md](11-scene-config/03-scene-activation.md) |
| **审计服务适配器** | 统一审计服务接口 | [10-integration/05-audit-adapter.md](10-integration/05-audit-adapter.md) |
| 术语服务 | 缩写扩展、同义词管理 | [05-terminology/](05-terminology/) |
| 交互反馈 | 自动学习、知识库更新 | [06-feedback/](06-feedback/) |
| SPI暴露 | Skill插件访问SE服务 | [09-spi/](09-spi/) |
| 简化API | chat/chatWithTools/chatStream | [03-conversation/](03-conversation/) |
| Skill控制器工厂 | 解决Skill非Spring Bean问题 | [10-integration/03-skill-controller-factory.md](10-integration/03-skill-controller-factory.md) |

---

## 新增枚举类型

### SceneType (场景类型)

**包路径**: `net.ooder.scene.skill.model.SceneType`

| 值 | 说明 | 特性 |
|----|------|------|
| `AUTO` | 自主场景 | 自驱动运行，无需外部触发 |
| `TRIGGER` | 触发场景 | 等待外部触发，被动响应 |
| `HYBRID` | 混合场景 | 既可主动也可被动 |

### SceneRunMode (场景运行模式)

**包路径**: `net.ooder.skills.api.SceneRunMode` (skills-framework)

| 值 | 说明 |
|----|------|
| `AUTO` | 自动运行模式 |
| `TRIGGER` | 触发运行模式 |
| `HYBRID` | 混合运行模式 |

### SceneRoleType (场景角色类型)

**包路径**: `net.ooder.skills.common.enums.SceneRoleType` (skills-framework)

| 值 | 说明 |
|----|------|
| `PRIMARY` | 主角色 |
| `COLLABORATIVE` | 协作角色 |

---

## 命名规范

| 层级 | 命名规则 | 示例 |
|------|---------|------|
| **SDK类** | 无特殊前缀 | `DriverChatRequest`, `LlmDriver.LlmConfig` |
| **SE类** | `Scene`前缀 | `SceneChatRequest`, `SceneLlmConfig` |

---

## 支持

- **GitHub**: https://github.com/ooder/scene-engine
- **文档**: https://docs.ooder.net/scene-engine/v2.3.1
- **问题反馈**: https://github.com/ooder/scene-engine/issues

---

**版权所有 © 2026 Ooder Team**
