# ooder-common 模块拆分协作需求说明

## 需求概述

**提出方**: Scene-Engine 团队  
**协作方**: ooder-common 团队  
**优先级**: P1 (高优先级)  
**预期完成时间**: 1-2周  

---

## 背景与动机

### 当前问题

在 Scene-Engine MVP 版本精简过程中，发现 `ooder-common` 模块存在以下问题：

1. **模块过重**: `ooder-common-client` 单模块包含过多功能（HTTP客户端、Redis缓存、Freemarker模板、拼音处理等）
2. **依赖冗余**: Scene-Engine 引用了 `ooder-common-client`，但实际只使用了其中一小部分功能
3. **传递依赖过多**: 导致 Scene-Engine 打包体积过大，包含大量未使用的依赖

### 影响分析

| 指标 | 当前状况 | 影响 |
|------|----------|------|
| ooder-common-client 传递依赖 | 20+ 个 | 打包体积增加 ~20MB |
| 未使用功能 | Freemarker、Jedis、MVEL等 | 启动时间延长、内存占用增加 |
| 维护成本 | 高耦合 | 难以按需升级和替换 |

---

## 拆分方案

### 目标架构

建议将 `ooder-common` 拆分为更细粒度的模块：

```
ooder-common/
├── ooder-common-core/          (新增 - 核心接口与基础类)
├── ooder-common-http/          (新增 - HTTP客户端)
├── ooder-common-cache/         (新增 - Redis/Jedis缓存)
├── ooder-common-template/      (新增 - Freemarker模板)
├── ooder-common-text/          (新增 - 拼音、文本处理)
├── ooder-common-expression/    (新增 - MVEL表达式)
├── ooder-common-search/        (新增 - Lucene全文搜索)
├── ooder-index-web/            (现有 - 保持不变)
├── ooder-vfs-web/              (现有 - 保持不变)
├── ooder-server/               (现有 - 保持不变)
├── ooder-org-web/              (现有 - 保持不变)
├── ooder-msg-web/              (现有 - 保持不变)
├── ooder-config/               (现有 - 保持不变)
├── ooder-config-core/          (现有 - 保持不变)
├── ooder-database/             (现有 - 保持不变)
└── ooder-annotation/           (现有 - 保持不变)
```

### 模块职责定义

#### 1. ooder-common-core (核心模块)
**职责**: 提供基础接口和通用工具类
**依赖**: 无外部依赖（仅JDK）
**体积目标**: < 100KB

```java
// 包含内容示例
- Result<T> 通用返回结果
- Filter 过滤器接口
- Identifiable 可标识接口
- Named 可命名接口
- Versioned 可版本化接口
- 基础异常类
- 工具类（StringUtils, DateUtils等）
```

#### 2. ooder-common-http (HTTP客户端模块)
**职责**: HTTP请求客户端
**依赖**: Apache HttpClient
**体积目标**: ~500KB

```java
// 包含内容示例
- HttpClient 封装
- HttpRequest/HttpResponse
- 连接池配置
- 重试机制
```

#### 3. ooder-common-cache (缓存模块)
**职责**: Redis缓存操作
**依赖**: Jedis, ooder-common-core
**体积目标**: ~300KB

```java
// 包含内容示例
- RedisTemplate
- CacheManager
- 序列化工具
```

#### 4. ooder-common-template (模板模块)
**职责**: 模板引擎（Freemarker）
**依赖**: Freemarker, ooder-common-core
**体积目标**: ~2MB (含Freemarker)

```java
// 包含内容示例
- TemplateEngine
- TemplateLoader
- 模板工具类
```

#### 5. ooder-common-text (文本处理模块)
**职责**: 拼音、文本处理
**依赖**: pinyin4j, ooder-common-core
**体积目标**: ~200KB

```java
// 包含内容示例
- PinyinUtils
- TextUtils
- 字符处理工具
```

#### 6. ooder-common-expression (表达式模块)
**职责**: MVEL表达式引擎
**依赖**: MVEL2, ooder-common-core
**体积目标**: ~1MB (含MVEL)

```java
// 包含内容示例
- ExpressionEvaluator
- ExpressionContext
```

#### 7. ooder-common-search (搜索模块)
**职责**: Lucene全文搜索
**依赖**: Lucene, ooder-common-core
**体积目标**: ~4MB (含Lucene)

```java
// 包含内容示例
- IndexManager
- Searcher
- DocumentBuilder
```

---

## Scene-Engine 使用需求

### 当前引用情况

Scene-Engine 当前在 `pom.xml` 中引用了以下 ooder-common 模块：

```xml
<!-- 当前引用 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-server</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-vfs-web</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-org-web</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-msg-web</artifactId>
    <version>3.0.2</version>
</dependency>
```

### 期望引用方式

拆分后，Scene-Engine 希望按需引用：

```xml
<!-- 期望引用 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-core</artifactId>
    <version>3.0.3</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-http</artifactId>
    <version>3.0.3</version>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-server</artifactId>
    <version>3.0.3</version>
</dependency>
<!-- 其他模块保持不变 -->
```

### 实际使用功能分析

通过代码分析，Scene-Engine 实际使用了 `ooder-common-client` 中的以下功能：

| 功能 | 使用频率 | 建议模块 |
|------|----------|----------|
| Result 类 | 高 | ooder-common-core |
| HTTP客户端 | 中 | ooder-common-http |
| Redis缓存 | 低 | ooder-common-cache (optional) |
| Freemarker | 低 | ooder-common-template (optional) |
| 拼音处理 | 低 | ooder-common-text (optional) |
| MVEL表达式 | 低 | ooder-common-expression (optional) |

---

## 实施建议

### 阶段1: 创建 ooder-common-core (第1周)

**任务**:
1. 从 `ooder-common-client` 中提取核心接口和工具类
2. 创建新的 `ooder-common-core` 模块
3. 确保无外部依赖
4. 发布 3.0.3 版本

**交付物**:
- `ooder-common-core` 模块
- 更新后的 `ooder-common-client` (依赖 core)

### 阶段2: 拆分其他模块 (第2周)

**任务**:
1. 创建 `ooder-common-http`
2. 创建 `ooder-common-cache`
3. 创建 `ooder-common-template`
4. 创建 `ooder-common-text`
5. 创建 `ooder-common-expression`
6. 重构 `ooder-common-client` 为聚合模块（可选）

**交付物**:
- 所有新模块
- 更新后的 `ooder-common-client`
- 迁移指南文档

### 阶段3: 兼容性处理

**向后兼容方案**:

方案A: 保持 `ooder-common-client` 作为聚合模块
```xml
<!-- ooder-common-client/pom.xml -->
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-core</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-http</artifactId>
    </dependency>
    <!-- 其他模块 -->
</dependencies>
```

方案B: 标记 `ooder-common-client` 为 deprecated
- 保留一个版本周期
- 引导用户迁移到新模块

---

## 协作事项

### 需要 ooder-common 团队提供

1. **模块拆分实施**
   - [ ] 创建新的子模块
   - [ ] 迁移代码
   - [ ] 更新依赖关系

2. **版本发布**
   - [ ] 发布 3.0.3-SNAPSHOT 供测试
   - [ ] 发布 3.0.3 正式版

3. **文档更新**
   - [ ] 更新 README.md
   - [ ] 提供迁移指南
   - [ ] 更新版本说明

4. **兼容性保证**
   - [ ] 确保现有功能不受影响
   - [ ] 提供向后兼容方案

### Scene-Engine 团队配合

1. **需求确认**
   - [ ] 确认模块拆分方案
   - [ ] 确认功能边界

2. **测试验证**
   - [ ] 测试新模块集成
   - [ ] 验证功能正常

3. **文档更新**
   - [ ] 更新 Scene-Engine 依赖说明

---

## 预期收益

### 对 Scene-Engine

| 指标 | 当前 | 预期 | 改善 |
|------|------|------|------|
| 依赖体积 | ~20MB | ~2MB | 减少 90% |
| 启动时间 | 较长 | 缩短 | 提升 20% |
| 内存占用 | 较高 | 降低 | 减少 15% |

### 对 ooder-common

1. **模块化更清晰**: 职责单一，易于维护
2. **复用性更高**: 可按需引用，减少冗余
3. **升级更灵活**: 各模块可独立版本演进

---

## 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 拆分导致兼容性问题 | 高 | 保持 `ooder-common-client` 作为聚合模块过渡 |
| 迁移成本 | 中 | 提供详细的迁移指南和工具 |
| 版本管理复杂 | 低 | 统一版本号管理，保持同步发布 |

---

## 时间计划

```
Week 1
├── Day 1-2: 创建 ooder-common-core
├── Day 3-4: 提取核心代码
└── Day 5: 发布 SNAPSHOT 版本

Week 2
├── Day 1-2: 创建其他子模块
├── Day 3-4: 重构 ooder-common-client
└── Day 5: 发布正式版 + 文档更新
```

---

## 联系方式

**需求提出**: Scene-Engine 团队  
**技术对接**: TBD  
**协调沟通**: 建议建立专项协作群

---

## 参考文档

- [Scene-Engine 依赖优化分析](DEPENDENCY_OPTIMIZATION_ANALYSIS.md)
- [Scene-Engine 依赖优化任务列表](DEPENDENCY_OPTIMIZATION_TASKS.md)
- [ooder-common 当前架构](e:\github\ooder-sdk\ooder-common\pom.xml)

---

**文档版本**: 1.0  
**创建日期**: 2026-04-10  
**最后更新**: 2026-04-10
