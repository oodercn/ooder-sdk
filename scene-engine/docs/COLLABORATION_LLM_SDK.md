# LLM-SDK 协作文档

> **文档版本**: 1.0.0  
> **编写日期**: 2026-03-09  
> **协作方**: Scene-Engine Team ↔ LLM-SDK Team  
> **来源文档**: collaboration-spec-comparison.md

---

## 一、协作背景

Scene-Engine 需要扩展 LLM 能力以支持场景安装和激活流程。本文档定义 LLM-SDK 需要提供的接口扩展。

---

## 二、任务清单

### 2.1 P0 优先级（阻塞任务）

| 任务ID | 任务名称 | 工作量 | 说明 |
|--------|---------|--------|------|
| **LLM-SDK-001** | ToolCallingApi | 5天 | 工具注册+执行+LLM调用 |
| **LLM-SDK-002** | StructuredOutputApi | 3天 | Schema约束输出+验证 |
| **LLM-SDK-003** | ContextTemplateApi | 5天 | 上下文模板+管理 |

### 2.2 P1 优先级（重要任务）

| 任务ID | 任务名称 | 工作量 | 说明 |
|--------|---------|--------|------|
| **LLM-SDK-004** | DegradationApi | 3天 | 降级策略+恢复 |
| **LLM-SDK-005** | InstallationContextManager | 3天 | 安装专用上下文+检查点 |

---

## 三、接口详细设计

### 3.1 LLM-SDK-001: ToolCallingApi

**需求来源**: Scene-Engine 需要在安装流程中调用工具（如配置检查、服务探测等）

**接口设计**:

```java
package net.ooder.sdk.llm.tool;

/**
 * 工具调用 API
 */
public interface ToolCallingApi {

    /**
     * 注册工具
     * @param toolDef 工具定义
     * @return 注册结果
     */
    ToolRegistration registerTool(ToolDefinition toolDef);

    /**
     * 注销工具
     * @param toolId 工具ID
     */
    void unregisterTool(String toolId);

    /**
     * 执行工具调用
     * @param request 调用请求
     * @return 执行结果
     */
    ToolExecutionResult executeTool(ToolExecutionRequest request);

    /**
     * 获取工具列表
     * @return 工具列表
     */
    List<ToolDefinition> listTools();

    /**
     * LLM对话+工具调用
     * @param request 对话请求
     * @return 对话响应
     */
    ChatResponse chatWithTools(ChatRequest request);
}
```

**数据模型**:

```java
public class ToolDefinition {
    private String toolId;
    private String name;
    private String description;
    private Map<String, Object> parametersSchema;  // JSON Schema
    private String handlerClass;
}

public class ToolExecutionRequest {
    private String toolId;
    private Map<String, Object> parameters;
    private String sessionId;
}

public class ToolExecutionResult {
    private String toolId;
    private boolean success;
    private Object result;
    private String error;
}
```

**依赖方**: 
- Scene-Engine: ENGINE-006 工具调用注册中心
- Skills: SKILL-MOD-001 skill-llm-conversation扩展

---

### 3.2 LLM-SDK-002: StructuredOutputApi

**需求来源**: 安装流程需要结构化输出（如配置确认、步骤结果等）

**接口设计**:

```java
package net.ooder.sdk.llm.output;

/**
 * 结构化输出 API
 */
public interface StructuredOutputApi {

    /**
     * 注册输出 Schema
     * @param schemaId Schema ID
     * @param schema JSON Schema
     */
    void registerSchema(String schemaId, Map<String, Object> schema);

    /**
     * 结构化对话
     * @param request 对话请求
     * @param schemaId Schema ID
     * @return 结构化响应
     */
    <T> StructuredResponse<T> chatStructured(ChatRequest request, String schemaId, Class<T> type);

    /**
     * 验证输出
     * @param output 输出内容
     * @param schemaId Schema ID
     * @return 验证结果
     */
    ValidationResult validateOutput(Object output, String schemaId);
}
```

**数据模型**:

```java
public class StructuredResponse<T> {
    private T data;
    private boolean valid;
    private List<String> validationErrors;
    private String rawResponse;
}

public class ValidationResult {
    private boolean valid;
    private List<String> errors;
}
```

**依赖方**:
- Scene-Engine: ENGINE-003 结构化输出支持
- Skills: SKILL-MOD-001 skill-llm-conversation扩展

---

### 3.3 LLM-SDK-003: ContextTemplateApi

**需求来源**: 安装流程需要专用上下文模板（如安装引导、配置确认等）

**接口设计**:

```java
package net.ooder.sdk.llm.context;

/**
 * 上下文模板 API
 */
public interface ContextTemplateApi {

    /**
     * 注册上下文模板
     * @param templateId 模板ID
     * @param template 模板内容
     */
    void registerTemplate(String templateId, ContextTemplate template);

    /**
     * 获取上下文模板
     * @param templateId 模板ID
     * @return 模板内容
     */
    ContextTemplate getTemplate(String templateId);

    /**
     * 渲染上下文
     * @param templateId 模板ID
     * @param variables 变量
     * @return 渲染后的上下文
     */
    String renderContext(String templateId, Map<String, Object> variables);

    /**
     * 创建上下文实例
     * @param templateId 模板ID
     * @param sessionId 会话ID
     * @return 上下文实例
     */
    ContextInstance createContext(String templateId, String sessionId);
}
```

**数据模型**:

```java
public class ContextTemplate {
    private String templateId;
    private String name;
    private String description;
    private String systemPrompt;
    private List<String> requiredVariables;
    private Map<String, Object> defaultValues;
}

public class ContextInstance {
    private String instanceId;
    private String templateId;
    private String sessionId;
    private List<Message> messages;
    private Map<String, Object> variables;
}
```

**依赖方**:
- Scene-Engine: ENGINE-002 LLM上下文隔离管理
- Skills: SKILL-NEW-001 skill-scene-installer

---

### 3.4 LLM-SDK-004: DegradationApi

**需求来源**: LLM 不可用时需要降级到手动配置

**接口设计**:

```java
package net.ooder.sdk.llm.degradation;

/**
 * 降级策略 API
 */
public interface DegradationApi {

    /**
     * 注册降级策略
     * @param strategyId 策略ID
     * @param strategy 降级策略
     */
    void registerStrategy(String strategyId, DegradationStrategy strategy);

    /**
     * 检查是否需要降级
     * @param context 上下文
     * @return 是否需要降级
     */
    boolean shouldDegrade(DegradationContext context);

    /**
     * 执行降级
     * @param context 上下文
     * @return 降级结果
     */
    DegradationResult degrade(DegradationContext context);

    /**
     * 恢复服务
     * @param context 上下文
     */
    void recover(DegradationContext context);

    /**
     * 获取降级状态
     * @param sessionId 会话ID
     * @return 降级状态
     */
    DegradationStatus getStatus(String sessionId);
}
```

**数据模型**:

```java
public class DegradationStrategy {
    private String strategyId;
    private String name;
    private List<DegradationCondition> conditions;
    private DegradationAction action;
}

public class DegradationResult {
    private boolean degraded;
    private String fallbackMethod;
    private Object fallbackData;
}
```

**依赖方**:
- Scene-Engine: ENGINE-004 激活流程引擎
- Skills: SKILL-NEW-001 skill-scene-installer

---

### 3.5 LLM-SDK-005: InstallationContextManager

**需求来源**: 安装流程需要专用上下文管理和检查点

**接口设计**:

```java
package net.ooder.sdk.llm.installation;

/**
 * 安装上下文管理器
 */
public interface InstallationContextManager {

    /**
     * 创建安装上下文
     * @param installId 安装ID
     * @param sceneId 场景ID
     * @return 安装上下文
     */
    InstallationContext createInstallationContext(String installId, String sceneId);

    /**
     * 获取安装上下文
     * @param installId 安装ID
     * @return 安装上下文
     */
    InstallationContext getInstallationContext(String installId);

    /**
     * 保存检查点
     * @param installId 安装ID
     * @param stepId 步骤ID
     * @param state 状态
     */
    void saveCheckpoint(String installId, String stepId, Map<String, Object> state);

    /**
     * 恢复检查点
     * @param installId 安装ID
     * @param stepId 步骤ID
     * @return 状态
     */
    Map<String, Object> restoreCheckpoint(String installId, String stepId);

    /**
     * 完成安装
     * @param installId 安装ID
     */
    void completeInstallation(String installId);

    /**
     * 取消安装
     * @param installId 安装ID
     */
    void cancelInstallation(String installId);
}
```

**数据模型**:

```java
public class InstallationContext {
    private String installId;
    private String sceneId;
    private String userId;
    private InstallationStatus status;
    private List<InstallationStep> steps;
    private Map<String, Object> variables;
    private List<Checkpoint> checkpoints;
}

public class Checkpoint {
    private String stepId;
    private long timestamp;
    private Map<String, Object> state;
}
```

**依赖方**:
- Scene-Engine: ENGINE-001 场景技能生命周期管理
- Skills: SKILL-NEW-001 skill-scene-installer

---

## 四、协作时间线

| 阶段 | 时间 | LLM-SDK 任务 | Scene-Engine 依赖 |
|------|------|--------------|-------------------|
| **Phase 1** | Week 1-2 | ToolCallingApi, StructuredOutputApi, ContextTemplateApi | ENGINE-001,002,003 |
| **Phase 2** | Week 3-4 | DegradationApi, InstallationContextManager | ENGINE-004,006 |

---

## 五、验收标准

### 5.1 ToolCallingApi

- [ ] 工具注册/注销正常
- [ ] 工具执行返回正确结果
- [ ] LLM可正确调用工具
- [ ] 单元测试覆盖 > 80%

### 5.2 StructuredOutputApi

- [ ] Schema注册正常
- [ ] 结构化输出符合Schema
- [ ] 验证功能正常
- [ ] 单元测试覆盖 > 80%

### 5.3 ContextTemplateApi

- [ ] 模板注册/获取正常
- [ ] 变量渲染正确
- [ ] 上下文实例创建正常
- [ ] 单元测试覆盖 > 80%

### 5.4 DegradationApi

- [ ] 降级条件检测正确
- [ ] 降级执行正常
- [ ] 恢复功能正常
- [ ] 单元测试覆盖 > 80%

### 5.5 InstallationContextManager

- [ ] 上下文创建/获取正常
- [ ] 检查点保存/恢复正常
- [ ] 完成/取消功能正常
- [ ] 单元测试覆盖 > 80%

---

## 六、联系方式

- **Scene-Engine Team**: scene-engine@ooder.cn
- **LLM-SDK Team**: llm-sdk@ooder.cn

---

**文档状态**: 待确认  
**下一步**: LLM-SDK Team 确认接口设计后启动开发
