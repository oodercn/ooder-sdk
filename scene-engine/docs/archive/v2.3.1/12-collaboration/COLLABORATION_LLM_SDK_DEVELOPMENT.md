# SE SDK v2.3.1 与 LLM-SDK 协同开发文档

**版本**: 2.3.1  
**发布日期**: 2026-03-22  
**目标读者**: LLM-SDK 开发团队  
**协作方**: SE SDK 团队  
**状态**: 🔴 待确认

---

## 一、协作背景

### 1.1 版本目标

SE SDK v2.3.1 版本需要 LLM-SDK 提供场景激活引导和智能对话支持，使场景激活流程更加智能化。

### 1.2 协作需求概述

| 需求 | 优先级 | SE SDK 依赖程度 |
|------|--------|----------------|
| 结构化输出 | P0 | 高 |
| 工具调用 | P0 | 高 |
| 激活引导 | P1 | 中 |
| Token 管理 | P2 | 低 |

### 1.3 当前状态

```
SE SDK 覆盖度: 81.2%
├── LLM Service 覆盖度: 62%
│   └── 需要 LLM-SDK 补齐: 38%
└── 激活引导场景: 0% (未实现)
```

---

## 二、协作边界

### 2.1 职责划分

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              SE SDK                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ • 场景上下文组装 (SceneContext)                                      │  │
│  │ • Prompt 构建和增强                                                 │  │
│  │ • 工具定义管理 (ToolDefinition)                                      │  │
│  │ • 激活步骤执行 (ActivationStepExecutor)                              │  │
│  │ • 响应结果处理                                                       │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼ 调用
┌─────────────────────────────────────────────────────────────────────────────┐
│                            LLM-SDK                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ • LLM API 调用封装 (chat/chatStream/chatWithTools)                  │  │
│  │ • 多模型 Provider 管理                                               │  │
│  │ • Token 计算和配额管理                                               │  │
│  │ • 流式响应处理                                                       │  │
│  │ • 结构化输出 (structuredOutput)                                      │  │
│  │ • 错误重试和降级                                                     │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 接口调用关系

| SE SDK 调用 | LLM-SDK 提供 | 说明 |
|-------------|--------------|------|
| `llmService.chat(request)` | LlmResponse | 普通对话 |
| `llmService.chatStream(request, handler)` | void | 流式对话 |
| `llmService.chatWithTools(request)` | LlmResponse | 工具调用对话 |
| `llmService.structuredOutput(request, type)` | T | 结构化输出 |

---

## 三、任务清单

### 3.1 P0 级任务 (关键)

#### 任务 LLM-001: 结构化输出支持

**优先级**: 🔴 P0  
**预计工时**: 3天  
**依赖**: 无

**任务描述**:
扩展 LLM-SDK 支持结构化输出能力，使 SE SDK 能够在场景激活时获取结构化的用户输入。

**接口定义**:

```java
/**
 * LLM 服务接口扩展
 */
public interface LlmService {
    
    /**
     * 结构化输出
     * 
     * <p>调用 LLM 并将响应解析为指定类型</p>
     * 
     * @param request 对话请求（需包含 responseSchema）
     * @param responseType 响应类型
     * @param <T> 响应类型泛型
     * @return 结构化响应对象
     */
    <T> T structuredOutput(StructuredChatRequest request, Class<T> responseType);
    
    /**
     * 带验证的结构化输出
     * 
     * @param request 对话请求
     * @param responseSchema 响应Schema定义
     * @param validator 验证器（可选）
     * @return 验证后的结构化响应
     */
    <T> T structuredOutputWithValidation(
        StructuredChatRequest request, 
        ResponseSchema responseSchema,
        OutputValidator<T> validator
    );
}

/**
 * 结构化对话请求
 */
@Builder
@Data
public class StructuredChatRequest {
    private String conversationId;
    private List<Message> messages;
    private String model;
    private ResponseSchema responseSchema;
    private Integer maxTokens;
    private Double temperature;
    private Map<String, Object> options;
}

/**
 * 响应Schema定义
 */
@Builder
@Data
public class ResponseSchema {
    private String type;              // object, array, string, number, boolean
    private List<SchemaProperty> properties;
    private List<String> required;
    private String description;
    
    @Builder
    @Data
    public static class SchemaProperty {
        private String name;
        private String type;
        private String description;
        private Object defaultValue;
        private List<String> enumValues;
    }
}

/**
 * 输出验证器
 */
public interface OutputValidator<T> {
    ValidationResult validate(T output);
    
    static <T> OutputValidator<T> noOp() {
        return output -> ValidationResult.valid();
    }
}
```

**使用场景**:

```java
// SE SDK 中激活引导使用示例
public class ActivationGuidanceService {
    
    private final LlmService llmService;
    
    public ParticipantSelectionGuidance guideParticipantSelection(
            SceneTemplate template, 
            String userId) {
        
        StructuredChatRequest request = StructuredChatRequest.builder()
            .conversationId(UUID.randomUUID().toString())
            .messages(List.of(
                Message.system("你是一个场景激活助手，帮助用户选择参与者"),
                Message.user("请帮助选择" + template.getTemplateName() + "的参与者")
            ))
            .responseSchema(ResponseSchema.builder()
                .type("object")
                .properties(List.of(
                    ResponseSchema.SchemaProperty.builder()
                        .name("recommendedParticipants")
                        .type("array")
                        .description("推荐的参与者列表")
                        .build(),
                    ResponseSchema.SchemaProperty.builder()
                        .name("selectionCriteria")
                        .type("string")
                        .description("选择标准说明")
                        .build()
                ))
                .required(List.of("recommendedParticipants"))
                .build())
            .build();
        
        return llmService.structuredOutput(request, ParticipantSelectionGuidance.class);
    }
}
```

**验收标准**:
- [ ] `structuredOutput()` 方法正确返回指定类型对象
- [ ] 支持复杂嵌套对象解析
- [ ] 支持数组类型响应
- [ ] 错误时抛出明确异常

---

#### 任务 LLM-002: 工具调用增强

**优先级**: 🔴 P0  
**预计工时**: 4天  
**依赖**: LLM-001

**任务描述**:
增强 LLM-SDK 的工具调用能力，支持 SE SDK 在场景激活时调用外部服务。

**接口定义**:

```java
/**
 * 工具调用对话请求
 */
@Builder
@Data
public class ToolCallingRequest {
    private String conversationId;
    private List<Message> messages;
    private String model;
    private List<ToolDefinition> tools;
    private ToolChoiceStrategy toolChoice;  // auto, specific, none
    private Integer maxToolCalls;           // 最多工具调用次数
    
    public enum ToolChoiceStrategy {
        AUTO,       // 自动选择
        SPECIFIC,   // 指定工具
        NONE        // 不调用工具
    }
}

/**
 * 工具定义
 */
@Builder
@Data
public class ToolDefinition {
    private String toolId;
    private String name;
    private String description;
    private Map<String, ParameterDefinition> parameters;
    
    @Builder
    @Data
    public static class ParameterDefinition {
        private String type;
        private String description;
        private boolean required;
        private Object defaultValue;
        private Map<String, Object> constraints;
    }
}

/**
 * 工具调用结果
 */
@Data
public class ToolCallResult {
    private String toolId;
    private String toolName;
    private Map<String, Object> arguments;
    private Object result;
    private boolean success;
    private String errorMessage;
    private long executionTimeMs;
}
```

**验收标准**:
- [ ] 支持 Function Calling 协议
- [ ] 支持多工具并行调用
- [ ] 工具执行结果正确返回
- [ ] 支持嵌套工具调用

---

### 3.2 P1 级任务 (重要)

#### 任务 LLM-003: 激活引导能力

**优先级**: 🟡 P1  
**预计工时**: 5天  
**依赖**: LLM-001, LLM-002

**任务描述**:
为 SE SDK 提供场景激活引导能力，支持在激活流程中通过 LLM 引导用户完成配置。

**接口定义**:

```java
/**
 * 激活引导服务接口
 */
public interface ActivationGuidanceService {
    
    /**
     * 获取下一步操作建议
     * 
     * @param context 激活上下文
     * @return 操作建议
     */
    GuidanceResult getNextStepGuidance(ActivationContext context);
    
    /**
     * 验证用户输入
     * 
     * @param stepId 步骤ID
     * @param userInput 用户输入
     * @return 验证结果
     */
    ValidationResult validateUserInput(String stepId, Object userInput);
    
    /**
     * 生成智能填充建议
     * 
     * @param fieldPath 字段路径
     * @param context 上下文
     * @return 填充建议
     */
    List<Suggestion> generateSuggestions(String fieldPath, ActivationContext context);
}

/**
 * 激活上下文
 */
@Data
public class ActivationContext {
    private String sceneId;
    private String templateId;
    private String userId;
    private String roleId;
    private String currentStepId;
    private Map<String, Object> stepData;
    private List<String> completedSteps;
    private Map<String, Object> userProfile;
}

/**
 * 引导结果
 */
@Data
public class GuidanceResult {
    private String guidanceText;           // 引导文本
    private List<Option> availableOptions;  // 可选项
    private Map<String, Object> suggestions; // 填充建议
    private boolean requiresConfirmation;   // 是否需要确认
    
    @Data
    public static class Option {
        private String id;
        private String label;
        private String description;
        private Map<String, Object> metadata;
    }
}
```

**验收标准**:
- [ ] 根据当前步骤生成引导文本
- [ ] 提供可选项列表
- [ ] 支持用户输入验证
- [ ] 生成智能填充建议

---

### 3.3 P2 级任务 (优化)

#### 任务 LLM-004: Token 配额管理

**优先级**: 🟢 P2  
**预计工时**: 3天  
**依赖**: 无

**任务描述**:
为 SE SDK 提供 Token 配额管理能力，支持按场景、用户进行配额控制。

**接口定义**:

```java
/**
 * Token 配额服务
 */
public interface TokenQuotaService {
    
    /**
     * 检查配额
     * 
     * @param quotaRequest 配额请求
     * @return 配额检查结果
     */
    QuotaCheckResult checkQuota(QuotaRequest quotaRequest);
    
    /**
     * 消耗配额
     * 
     * @param consumption 消耗详情
     * @return 实际消耗量
     */
    int consumeQuota(TokenConsumption consumption);
    
    /**
     * 获取配额使用统计
     * 
     * @param scope 统计范围
     * @return 使用统计
     */
    QuotaUsageStats getUsageStats(QuotaScope scope);
}

/**
 * 配额范围
 */
@Data
public class QuotaScope {
    private String sceneId;      // 场景级别
    private String userId;        // 用户级别
    private String departmentId;  // 部门级别
    private String companyId;     // 公司级别
}
```

---

## 四、集成规范

### 4.1 Maven 依赖

```xml
<!-- SE SDK 添加 LLM-SDK 依赖 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 4.2 Spring Boot 自动配置

```java
@Configuration
@ConditionalOnClass(LlmService.class)
public class LlmSdkAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public LlmService llmService() {
        return new LlmServiceImpl();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ActivationGuidanceService activationGuidanceService(
            LlmService llmService,
            SceneTemplateRepository templateRepository) {
        return new ActivationGuidanceServiceImpl(llmService, templateRepository);
    }
}
```

### 4.3 配置属性

```yaml
# application.yml
ooder:
  llm:
    default-model: qwen-plus
    timeout: 30000
    max-retries: 3
    structured-output:
      enabled: true
      max-depth: 5
    tool-calling:
      enabled: true
      max-parallel-calls: 3
    quota:
      enabled: true
      default-quota: 10000
```

---

## 五、验收标准

### 5.1 功能验收

| 任务ID | 验收标准 | 测试用例 |
|--------|----------|----------|
| LLM-001 | 结构化输出正确解析 | 测试嵌套对象、数组、枚举 |
| LLM-002 | 工具调用正常工作 | 测试单工具、多工具、嵌套调用 |
| LLM-003 | 激活引导生成正确 | 测试各步骤引导文本 |
| LLM-004 | 配额计算准确 | 测试多场景、多用户配额 |

### 5.2 性能验收

| 指标 | 目标 | 说明 |
|------|------|------|
| 结构化输出响应时间 | < 2s | 不含 LLM 调用 |
| 工具调用响应时间 | < 500ms | 不含工具执行 |
| 引导生成响应时间 | < 1s | 不含 LLM 调用 |
| 并发支持 | ≥ 100 QPS | 正常负载 |

### 5.3 兼容性验收

- [ ] 支持 OpenAI 兼容接口
- [ ] 支持阿里云 DashScope
- [ ] 支持本地部署模型
- [ ] 兼容现有 LLM-SDK 1.x 版本

---

## 六、里程碑

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           开发里程碑                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  M1 (Week 1)    M2 (Week 2)    M3 (Week 3)    M4 (Week 4)                 │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐                 │
│  │LLM-001 │    │LLM-002 │    │LLM-003 │    │LLM-004 │                 │
│  │结构化   │────▶│工具调用  │────▶│激活引导 │────▶│Token    │                 │
│  │输出     │    │        │    │        │    │配额     │                 │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘                 │
│       │              │              │              │                        │
│       ▼              ▼              ▼              ▼                        │
│  接口定义       接口实现        场景集成       性能优化                      │
│                                                                             │
│  目标: 25%      目标: 50%      目标: 75%      目标: 100%                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| LLM 响应不稳定 | 高 | 实现重试和降级机制 |
| 结构化输出失败 | 中 | 提供 fallback 到普通文本 |
| Token 计算不准确 | 中 | 定期校准算法 |
| 并发性能不足 | 高 | 提前进行压测 |

---

## 八、联系人

| 角色 | 联系人 | 联系方式 |
|------|--------|----------|
| SE SDK 负责人 | - | - |
| LLM-SDK 负责人 | - | - |
| 接口评审 | - | - |

---

## 九、参考文档

- [SE SDK 覆盖度报告](./SCENE_LIFECYCLE_COVERAGE_V4.md)
- [LLM-SDK 现有接口文档](../COLLABORATION_LLM_SDK_V2_3_1.md)
- [ActivationFlowEngine 接口定义](../core/activation/ActivationFlowEngine.java)

---

*文档版本: 1.0*  
*创建日期: 2026-03-22*  
*SE SDK 团队*
