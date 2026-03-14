# Scene Engine v2.3.1 官方文档

**版本**: v2.3.1  
**发布日期**: 2026-03-14  
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
| **10-集成** | Spring Boot集成、配置参考 | [10-integration/](10-integration/) |
| **11-最佳实践** | 开发规范、性能优化、安全建议 | [11-best-practices/](11-best-practices/) |
| **12-参考** | API索引、错误码、版本历史 | [12-reference/](12-reference/) |

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

### 高级特性
- [SPI服务暴露](09-spi/01-service-provider.md)
- [Spring Boot集成](10-integration/01-spring-boot.md)
- [性能优化](11-best-practices/02-performance.md)

---

## 版本特性

### v2.3.1 新特性

| 特性 | 说明 | 文档 |
|------|------|------|
| 术语服务 | 缩写扩展、同义词管理 | [05-terminology/](05-terminology/) |
| 交互反馈 | 自动学习、知识库更新 | [06-feedback/](06-feedback/) |
| SPI暴露 | Skill插件访问SE服务 | [09-spi/](09-spi/) |
| 简化API | chat/chatWithTools/chatStream | [03-conversation/](03-conversation/) |

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
