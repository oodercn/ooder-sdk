# Scene Engine v3.0.0 文档

## 概述

Scene Engine 是一个企业级场景引擎，提供场景管理、能力发现、知识库集成等功能。

## 核心特性

- **SPI 驱动架构**: 支持 Tiny/Small/Enterprise 三种规模部署
- **条件装配**: 基于 Spring Boot 的灵活配置
- **RAD 集成**: 低代码平台适配器
- **多模型支持**: LLM 多模型路由

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>3.0.0</version>
</dependency>
```

### 配置驱动

```yaml
scene:
  engine:
    driver: tiny  # tiny | small | enterprise
```

## 核心文档

| 文档 | 说明 |
|------|------|
| [二次开发指南](SECONDARY_DEVELOPMENT_GUIDE.md) | 完整的二次开发文档 |
| [API 参考](API_REFERENCE.md) | API 接口文档 |
| [应用集成指南](APPLICATION_INTEGRATION_GUIDE.md) | 应用层集成说明 |

## SPI 接口

| 接口 | 说明 |
|------|------|
| `StorageProvider` | 存储提供者 |
| `LlmProvider` | LLM 提供者 |
| `VectorStore` | 向量存储 |

## 驱动实现

| 驱动 | 存储实现 | LLM 实现 | 向量实现 | 适用场景 |
|------|----------|----------|----------|----------|
| Tiny | 文件存储 | Ollama | 内存 | 开发测试 |
| Small | JDBC | 远程 API | Milvus Lite | 小团队 |
| Enterprise | 分布式 | 多模型路由 | 分布式向量库 | 企业级 |

## 配置开关

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `scene.engine.driver` | 驱动类型 | 无 (降级) |
| `scene.engine.fallback.enabled` | 启用降级实现 | true |
| `scene.engine.rad.enabled` | 启用 RAD 集成 | false |

## 归档文档

- [v2.3.1 文档](archive/v2.3.1/)
- [协作文档](archive/collaboration/)
- [废弃文档](archive/deprecated/)

## 版本历史

- [CHANGELOG.md](CHANGELOG.md) - 版本变更记录

---

**维护团队**: Ooder Team  
**最后更新**: 2026-03-25
