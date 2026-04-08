# Ooder SDK v3.0.2 发布说明

**发布日期**: 2026-04-08  
**版本号**: 3.0.2  
**状态**: 正式发布

---

## 概述

Ooder SDK v3.0.2 是一个版本统一和维护版本，主要包含版本号统一、文档整理和归档清理工作。

---

## 主要变更

### 版本统一

所有 ooder 模块版本统一为 **3.0.2**：

| 模块 | 旧版本 | 新版本 |
|------|--------|--------|
| ooder-sdk-parent | 3.0.1 | 3.0.2 |
| agent-sdk | 3.0.1 | 3.0.2 |
| agent-sdk-core | 3.0.1 | 3.0.2 |
| llm-sdk | 3.0.1 | 3.0.2 |
| skills-framework | 3.0.1 | 3.0.2 |
| scene-engine | 3.0.1 | 3.0.2 |
| ooder-annotation | 3.0.1 | 3.0.2 |
| ooder-common-all | 3.0.1 | 3.0.2 |
| ooder-api | 3.0.1 | 3.0.2 |
| ooder-util | 3.0.1 | 3.0.2 |
| ooder-config | 3.0.1 | 3.0.2 |
| ooder-common-client | 3.0.1 | 3.0.2 |

### 清理工作

#### 移除的文件
- `agent-sdk/META-INF/maven/net.ooder/agent-sdk-core/pom.xml` (旧版本归档)
- `agent-sdk/META-INF/maven/net.ooder/llm-sdk/pom.xml` (旧版本归档)
- `agent-sdk/META-INF/maven/net.ooder/skills-framework/pom.xml` (旧版本归档)
- `agent-sdk/agent-sdk-core/META-INF/maven/` 目录
- `agent-sdk/llm-sdk/META-INF/maven/` 目录
- `agent-sdk/skills-framework/META-INF/maven/` 目录

#### 归档的文档
- `RELEASE_NOTES_v2.3.md` → `docs/archive/v2.3.x/`
- `RELEASE_NOTES_v2.3.1.md` → `docs/archive/v2.3.x/`

---

## 依赖引用

### Maven

```xml
<!-- 父 POM -->
<parent>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-sdk-parent</artifactId>
    <version>3.0.2</version>
</parent>

<!-- scene-engine -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- agent-sdk-core -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- llm-sdk -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- skills-framework -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skills-framework</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- ooder-annotation -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-annotation</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- ooder-config -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-config</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- ooder-common-client -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>3.0.2</version>
</dependency>
```

---

## 模块依赖关系

```
上层应用
    │
    ▼
scene-engine (3.0.2)
    │
    ▼
agent-sdk-core (3.0.2)
    │
    ├─► skills-framework (3.0.2)
    │
    └─► llm-sdk (3.0.2)
        │
        ▼
ooder-common-* (3.0.2)
    │
    ├─► ooder-annotation (3.0.2)
    │
    ├─► ooder-api (3.0.2)
    │
    └─► ooder-util (3.0.2)
```

---

## 系统要求

- **Java版本**: Java 21+
- **Spring Boot**: 3.2.5+
- **Maven**: 3.6+

---

## 升级指南

### 从 v3.0.1 升级

只需更新版本号即可，无 API 变更：

```xml
<properties>
    <ooder.version>3.0.2</ooder.version>
    <agent-sdk.version>3.0.2</agent-sdk.version>
    <scene-engine.version>3.0.2</scene-engine.version>
    <llm-sdk.version>3.0.2</llm-sdk.version>
</properties>
```

### 从 v2.3.x 升级

请参考 [RELEASE_NOTES_v3.0.0.md](./docs/archive/v3.0.0/RELEASE_NOTES_v3.0.0.md) 进行升级。

---

## 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 变更日志 | `scene-engine/docs/CHANGELOG.md` | 详细变更历史 |
| 架构指南 | `ARCHITECTURE_GUIDE.md` | 整体架构说明 |
| 开发指南 | `DEVELOPMENT_GUIDE.md` | 二次开发指南 |
| Skills 协作 | `SKILLS_COLLABORATION.md` | Skills 开发协作规范 |

---

## 反馈与支持

- **GitHub Issues**: https://github.com/oodercn/ooder-sdk/issues
- **邮箱**: team@ooder.net

---

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

---

**Ooder Team**  
2026-04-08
