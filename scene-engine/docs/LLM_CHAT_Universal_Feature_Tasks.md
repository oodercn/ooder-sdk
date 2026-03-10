# LLM-CHAT 通用功能开发任务清单

## 项目信息

| 项目 | 内容 |
|------|------|
| 项目名称 | LLM-CHAT 通用功能建设 |
| 版本 | v2.4.0 |
| 开始日期 | 2026-03-11 |
| 预计完成 | 2026-04-30 |

---

## 一、P0 阶段任务（高优先级）

### 1.1 LlmService 核心接口（2周）

| 任务ID | 任务描述 | 负责人 | 状态 | 预计完成 |
|--------|----------|--------|------|----------|
| P0-001 | 创建 `LlmService` 接口定义 | SE | ⏳ 待开始 | 2026-03-12 |
| P0-002 | 创建 `ChatRequest` 请求类 | SE | ⏳ 待开始 | 2026-03-12 |
| P0-003 | 创建 `ChatResponse` 响应类 | SE | ⏳ 待开始 | 2026-03-12 |
| P0-004 | 实现 `DefaultLlmServiceImpl` | SE | ⏳ 待开始 | 2026-03-14 |
| P0-005 | 实现 Provider 自动发现机制 | SE | ⏳ 待开始 | 2026-03-16 |
| P0-006 | 实现 `getProviders()` 方法 | SE | ⏳ 待开始 | 2026-03-16 |
| P0-007 | 实现 `getModels()` 方法 | SE | ⏳ 待开始 | 2026-03-17 |
| P0-008 | 实现 `setActiveModel()` 方法 | SE | ⏳ 待开始 | 2026-03-17 |
| P0-009 | 单元测试 | SE | ⏳ 待开始 | 2026-03-18 |
| P0-010 | 集成测试 | SE | ⏳ 待开始 | 2026-03-20 |

**交付物**:
- `net.ooder.scene.llm.LlmService`
- `net.ooder.scene.llm.ChatRequest`
- `net.ooder.scene.llm.ChatResponse`
- `net.ooder.scene.llm.impl.DefaultLlmServiceImpl`

### 1.2 LLM 配置文件解析（1周）

| 任务ID | 任务描述 | 负责人 | 状态 | 预计完成 |
|--------|----------|--------|------|----------|
| P0-011 | 定义 `llm-config.yaml` Schema | SE | ⏳ 待开始 | 2026-03-21 |
| P0-012 | 创建 `LlmConfigProperties` 配置类 | SE | ⏳ 待开始 | 2026-03-22 |
| P0-013 | 实现 YAML 配置解析器 | SE | ⏳ 待开始 | 2026-03-23 |
| P0-014 | 实现环境变量注入 | SE | ⏳ 待开始 | 2026-03-24 |
| P0-015 | 实现配置热更新 | SE | ⏳ 待开始 | 2026-03-25 |
| P0-016 | 单元测试 | SE | ⏳ 待开始 | 2026-03-26 |

**交付物**:
- `net.ooder.scene.llm.config.LlmConfigProperties`
- `net.ooder.scene.llm.config.LlmConfigParser`
- `llm-config.yaml` 示例文件

---

## 二、P1 阶段任务（高/中优先级）

### 2.1 PromptService 模板管理（1周）

| 任务ID | 任务描述 | 负责人 | 状态 | 预计完成 |
|--------|----------|--------|------|----------|
| P1-001 | 创建 `PromptService` 接口 | SE | ⏳ 待开始 | 2026-03-27 |
| P1-002 | 创建 `PromptTemplate` 模板类 | SE | ⏳ 待开始 | 2026-03-27 |
| P1-003 | 定义 `prompts.yaml` Schema | SE | ⏳ 待开始 | 2026-03-28 |
| P1-004 | 实现模板渲染引擎 (Mustache) | SE | ⏳ 待开始 | 2026-03-29 |
| P1-005 | 实现 `DefaultPromptServiceImpl` | SE | ⏳ 待开始 | 2026-03-30 |
| P1-006 | 单元测试 | SE | ⏳ 待开始 | 2026-03-31 |

**交付物**:
- `net.ooder.scene.llm.prompt.PromptService`
- `net.ooder.scene.llm.prompt.PromptTemplate`
- `net.ooder.scene.llm.prompt.impl.DefaultPromptServiceImpl`
- `prompts.yaml` 示例文件

### 2.2 ToolService 自动注册（1周）

| 任务ID | 任务描述 | 负责人 | 状态 | 预计完成 |
|--------|----------|--------|------|----------|
| P1-007 | 创建 `ToolService` 接口 | SE | ⏳ 待开始 | 2026-04-01 |
| P1-008 | 创建 `ToolDefinition` 定义类 | SE | ⏳ 待开始 | 2026-04-01 |
| P1-009 | 定义 `tools.yaml` Schema | SE | ⏳ 待开始 | 2026-04-02 |
| P1-010 | 实现 YAML 工具定义解析 | SE | ⏳ 待开始 | 2026-04-03 |
| P1-011 | 实现 Spring Bean 绑定机制 | SE | ⏳ 待开始 | 2026-04-04 |
| P1-012 | 整合 `ToolOrchestrator` | SE | ⏳ 待开始 | 2026-04-05 |
| P1-013 | 单元测试 | SE | ⏳ 待开始 | 2026-04-06 |

**交付物**:
- `net.ooder.scene.llm.tool.ToolService`
- `net.ooder.scene.llm.tool.ToolDefinition`
- `net.ooder.scene.llm.tool.impl.DefaultToolServiceImpl`
- `tools.yaml` 示例文件

---

## 三、P2 阶段任务（中/低优先级）

### 3.1 ContextService 上下文管理（1周）

| 任务ID | 任务描述 | 负责人 | 状态 | 预计完成 |
|--------|----------|--------|------|----------|
| P2-001 | 创建 `ContextService` 接口 | SE | ⏳ 待开始 | 2026-04-07 |
| P2-002 | 扩展 `LlmContextRegistry` | SE | ⏳ 待开始 | 2026-04-08 |
| P2-003 | 实现上下文生命周期管理 | SE | ⏳ 待开始 | 2026-04-09 |
| P2-004 | 实现消息历史管理 | SE | ⏳ 待开始 | 2026-04-10 |
| P2-005 | 单元测试 | SE | ⏳ 待开始 | 2026-04-11 |

**交付物**:
- `net.ooder.scene.llm.context.ContextService`
- `net.ooder.scene.llm.context.LlmContext` (增强版)

### 3.2 Provider SPI 扩展机制（1周）

| 任务ID | 任务描述 | 负责人 | 状态 | 预计完成 |
|--------|----------|--------|------|----------|
| P2-006 | 定义 Provider SPI 接口 | SE | ⏳ 待开始 | 2026-04-12 |
| P2-007 | 实现 Provider 自动注册 | SE | ⏳ 待开始 | 2026-04-13 |
| P2-008 | 实现重试策略 | SE | ⏳ 待开始 | 2026-04-14 |
| P2-009 | 实现降级策略 | SE | ⏳ 待开始 | 2026-04-15 |
| P2-010 | 单元测试 | SE | ⏳ 待开始 | 2026-04-16 |

**交付物**:
- `net.ooder.scene.llm.spi.LlmProviderSpi`
- `net.ooder.scene.llm.retry.RetryStrategy`
- `net.ooder.scene.llm.fallback.FallbackStrategy`

---

## 四、依赖关系

```
P0-001 ──┬── P0-002 ──┬── P0-004 ──┬── P0-009
         │            │            │
         │            └── P0-005 ──┴── P0-010
         │
         └── P0-003

P0-011 ──┬── P0-012 ──┬── P0-014
         │            │
         └── P0-013 ──┴── P0-015 ── P0-016

P1-001 ──┬── P1-002 ──┬── P1-004 ──┬── P1-006
         │            │            │
         └── P1-003 ──┴── P1-005 ──┘

P1-007 ──┬── P1-008 ──┬── P1-010 ──┬── P1-012 ── P1-013
         │            │            │
         └── P1-009 ──┴── P1-011 ──┘
```

---

## 五、里程碑

| 里程碑 | 日期 | 交付物 |
|--------|------|--------|
| **M1** | 2026-03-20 | LlmService 核心接口完成 |
| **M2** | 2026-03-26 | LLM 配置文件解析完成 |
| **M3** | 2026-03-31 | PromptService 模板管理完成 |
| **M4** | 2026-04-06 | ToolService 自动注册完成 |
| **M5** | 2026-04-11 | ContextService 上下文管理完成 |
| **M6** | 2026-04-16 | Provider SPI 扩展机制完成 |
| **M7** | 2026-04-20 | 集成测试完成 |
| **M8** | 2026-04-30 | v2.4.0 发布 |

---

## 六、资源需求

| 资源 | 需求 |
|------|------|
| 开发人员 | 2人 |
| 测试人员 | 1人 |
| 开发周期 | 7周 |
| 测试周期 | 2周 |

---

**文档状态**: ✅ 已确认  
**创建日期**: 2026-03-10  
**创建人**: SE Team
