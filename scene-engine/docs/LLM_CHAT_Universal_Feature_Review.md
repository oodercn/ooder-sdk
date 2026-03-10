# LLM-CHAT 通用功能建设方案 - 可行性评审报告

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 评审日期 | 2026-03-10 |
| 评审方 | SE (scene-engine) 团队 |

---

## 一、现有实现分析

### 1.1 已有接口

| 接口 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| `LlmProvider` | `net.ooder.scene.skill.llm` | ✅ 已有 | 核心 Provider 接口 |
| `LlmConfig` | `net.ooder.scene.llm.config` | ✅ 已有 | 基础配置类 |
| `StreamHandler` | `net.ooder.scene.skill.llm` | ✅ 已有 | 流式处理回调 |
| `FunctionCall` | `net.ooder.scene.skill.llm` | ✅ 已有 | 函数调用定义 |

### 1.2 已有实现

| 实现类 | 包路径 | 状态 | 说明 |
|--------|--------|------|------|
| `AbstractLlmProvider` | `net.ooder.scene.skill.llm.impl` | ✅ 已有 | 抽象基类 |
| `EnhancedLlmProvider` | `net.ooder.scene.skill.llm` | ✅ 已有 | 增强版 Provider |
| `MockLlmProvider` | `net.ooder.scene.skill` | ✅ 已有 | Mock 实现 |
| `SkillLlmDriver` | `net.ooder.scene.skill.llm.driver` | ✅ 已有 | 技能 LLM 驱动 |
| `SceneEngineLlmProxy` | `net.ooder.scene.llm.proxy` | ✅ 已有 | 场景引擎 LLM 代理 |

### 1.3 已有上下文管理

| 类 | 包路径 | 状态 | 说明 |
|-----|--------|------|------|
| `LlmRuntimeContext` | `net.ooder.scene.llm.context` | ✅ 已有 | 运行时上下文 |
| `LlmSceneContext` | `net.ooder.scene.llm.context` | ✅ 已有 | 场景上下文 |
| `LlmContextRegistry` | `net.ooder.scene.llm.context` | ✅ 已有 | 上下文注册表 |
| `SkillActivationContext` | `net.ooder.scene.llm.context` | ✅ 已有 | 技能激活上下文 |

### 1.4 已有连接管理

| 类 | 包路径 | 状态 | 说明 |
|-----|--------|------|------|
| `LlmConnection` | `net.ooder.scene.llm.proxy.connection` | ✅ 已有 | 连接封装 |
| `LlmConnectionPool` | `net.ooder.scene.llm.proxy.connection` | ✅ 已有 | 连接池 |
| `AgentLlmSessionContext` | `net.ooder.scene.llm.proxy.agent` | ✅ 已有 | Agent 会话上下文 |

---

## 二、可行性评估

### 2.1 总体评估

| 评估项 | 评分 | 说明 |
|--------|------|------|
| **技术可行性** | ⭐⭐⭐⭐⭐ | 已有完善的基础设施 |
| **工作量评估** | ⭐⭐⭐⭐ | 需要新增统一服务层 |
| **风险等级** | ⭐⭐ | 低风险，主要是封装和增强 |
| **优先级** | ⭐⭐⭐⭐⭐ | 高优先级，解决实际痛点 |

### 2.2 详细分析

#### ✅ 可复用现有代码

1. **LlmProvider 接口已完善**
   - 支持 chat、complete、embed、translate、summarize
   - 支持 streaming、function calling
   - 可直接作为底层实现

2. **上下文管理已存在**
   - `LlmContextRegistry` 可扩展为 `ContextService`
   - `LlmSceneContext` 可复用

3. **连接池已实现**
   - `LlmConnectionPool` 可复用
   - 支持多 Provider 连接管理

#### ⚠️ 需要新增功能

1. **LlmService 统一服务层** - 需要新建
2. **PromptService 模板管理** - 需要新建
3. **ToolService 自动注册** - 需要新建
4. **YAML 配置解析** - 需要新建
5. **重试/降级策略** - 需要新建

---

## 三、接口设计评审

### 3.1 LlmService 接口

| 方法 | 现有支持 | 评审意见 |
|------|----------|----------|
| `chat()` | ✅ LlmProvider.chat | 可行，封装现有接口 |
| `chatStream()` | ✅ LlmProvider.chatStream | 可行，封装现有接口 |
| `complete()` | ✅ LlmProvider.complete | 可行 |
| `translate()` | ✅ LlmProvider.translate | 可行 |
| `summarize()` | ✅ LlmProvider.summarize | 可行 |
| `embed()` | ✅ LlmProvider.embed | 可行 |
| `structuredOutput()` | ⚠️ 需新增 | 可行，基于 function calling |
| `getProviders()` | ⚠️ 需新增 | 可行，遍历 SPI |
| `getModels()` | ✅ LlmProvider.getSupportedModels | 可行 |
| `setActiveModel()` | ⚠️ 需新增 | 可行 |

**评审结论**: ✅ 通过

### 3.2 PromptService 接口

| 方法 | 现有支持 | 评审意见 |
|------|----------|----------|
| `getTemplate()` | ❌ 需新建 | 可行 |
| `render()` | ❌ 需新建 | 可行，使用模板引擎 |
| `registerTemplate()` | ❌ 需新建 | 可行 |
| `listTemplates()` | ❌ 需新建 | 可行 |

**评审结论**: ✅ 通过，建议使用 Mustache 或 Handlebars 模板引擎

### 3.3 ToolService 接口

| 方法 | 现有支持 | 评审意见 |
|------|----------|----------|
| `getToolDefinitions()` | ⚠️ 部分支持 | 可行，扩展 ToolOrchestrator |
| `executeTool()` | ✅ ToolOrchestrator | 可行 |
| `registerTool()` | ⚠️ 部分支持 | 可行 |

**评审结论**: ✅ 通过，与现有 ToolOrchestrator 整合

### 3.4 ContextService 接口

| 方法 | 现有支持 | 评审意见 |
|------|----------|----------|
| `createContext()` | ⚠️ 部分支持 | 可行，扩展 LlmContextRegistry |
| `getContext()` | ✅ LlmContextRegistry | 可行 |
| `updateContext()` | ⚠️ 部分支持 | 可行 |
| `destroyContext()` | ⚠️ 部分支持 | 可行 |
| `addMessage()` | ⚠️ 部分支持 | 可行 |
| `getHistory()` | ⚠️ 部分支持 | 可行 |

**评审结论**: ✅ 通过，基于 LlmContextRegistry 扩展

---

## 四、配置文件格式评审

### 4.1 llm-config.yaml

**评审意见**:
- ✅ 格式合理，符合 Kubernetes 风格
- ✅ 支持多 Provider 配置
- ✅ 支持环境变量注入
- ⚠️ 建议增加 `healthCheck` 配置

### 4.2 prompts.yaml

**评审意见**:
- ✅ 格式合理
- ✅ 支持模板变量
- ⚠️ 建议增加 `version` 和 `i18n` 支持

### 4.3 tools.yaml

**评审意见**:
- ✅ 格式合理
- ✅ 支持 Spring Bean 方式绑定
- ⚠️ 建议增加 `security` 配置（权限控制）

---

## 五、实施计划确认

### 5.1 阶段划分（确认）

| 阶段 | 内容 | 工作量 | 优先级 | SE 确认 |
|------|------|--------|--------|---------|
| **P0** | LlmService 核心接口 | 2周 | 高 | ✅ 确认 |
| **P0** | LLM 配置文件解析 | 1周 | 高 | ✅ 确认 |
| **P1** | PromptService 模板管理 | 1周 | 高 | ✅ 确认 |
| **P1** | ToolService 自动注册 | 1周 | 中 | ✅ 确认 |
| **P2** | ContextService 上下文管理 | 1周 | 中 | ✅ 确认 |
| **P2** | Provider SPI 扩展机制 | 1周 | 低 | ✅ 确认 |

### 5.2 接口清单（确认）

| 接口 | 包名 | 状态 | 说明 |
|------|------|------|------|
| `LlmService` | `net.ooder.scene.llm` | 🆕 新建 | LLM 统一服务 |
| `PromptService` | `net.ooder.scene.llm.prompt` | 🆕 新建 | Prompt 模板管理 |
| `ToolService` | `net.ooder.scene.llm.tool` | 🆕 新建 | Tool 自动注册 |
| `ContextService` | `net.ooder.scene.llm.context` | 🔄 扩展 | 基于 LlmContextRegistry |
| `LlmProvider` | `net.ooder.scene.skill.llm` | ✅ 已有 | Provider SPI |

---

## 六、风险与建议

### 6.1 风险点

| 风险 | 等级 | 缓解措施 |
|------|------|----------|
| 现有代码兼容性 | 低 | 保持向后兼容，新增接口 |
| 配置文件解析性能 | 低 | 使用缓存，延迟加载 |
| 模板引擎选择 | 低 | 使用成熟的 Mustache |

### 6.2 建议

1. **分阶段实施**
   - P0 阶段先实现核心功能
   - P1/P2 阶段逐步完善

2. **保持兼容性**
   - 现有 `LlmProvider` 接口不变
   - 新增 `LlmService` 作为统一入口

3. **配置热更新**
   - 支持配置文件热更新
   - 无需重启应用

---

## 七、评审结论

### 7.1 总体结论

| 项目 | 结论 |
|------|------|
| **可行性** | ✅ 完全可行 |
| **工作量** | 约 7 周 |
| **风险** | 低风险 |
| **建议** | 立即启动 P0 阶段 |

### 7.2 下一步行动

1. ✅ 评审通过
2. 📋 创建开发任务清单
3. 🚀 启动 P0 阶段开发

---

**评审状态**: ✅ 通过  
**评审人**: SE Team  
**评审日期**: 2026-03-10
