# SE SDK 3.0.1 MVP 协同开发任务清单

> **文档版本**: v1.0  
> **创建日期**: 2026-03-28  
> **SE SDK 版本**: 3.0.1  
> **状态**: 待协同

---

## 一、SE SDK 已完成工作

### 1.1 核心服务实现

| 服务 | 实现类 | 状态 |
|------|--------|------|
| 企业规范流程服务 | `EnterpriseProcedureServiceImpl` | ✅ 已完成 |
| 融合模板服务 | `FusionTemplateServiceImpl` | ✅ 已完成 |
| 完善度评估器 | `DefaultCompletenessEvaluator` | ✅ 已完成 |

### 1.2 内置执行器实现

| 执行器 | 类名 | 状态 |
|--------|------|------|
| 确认参与者 | `ConfirmParticipantsExecutor` | ✅ 已完成 |
| 选择推送目标 | `SelectPushTargetsExecutor` | ✅ 已完成 |
| 绑定知识库 | `BindKnowledgeExecutor` | ✅ 已完成 |
| 配置条件 | `ConfigConditionsExecutor` | ✅ 已完成 |
| 配置私有能力 | `ConfigPrivateCapabilitiesExecutor` | ✅ 已完成 |
| 配置工作流 | `ConfigWorkflowExecutor` | ✅ 已完成 |

### 1.3 Maven 仓库

```
D:\maven\.m2\repository\net\ooder\scene-engine\3.0.1\
├── scene-engine-3.0.1.jar
├── scene-engine-3.0.1-sources.jar
└── scene-engine-3.0.1.pom
```

---

## 二、需要 MVP 协同的开发任务

### 2.1 LLM 服务集成 (P0 - 高优先级)

**背景**: SE SDK 的 `EnterpriseProcedureServiceImpl.llmAssistCreate()` 和 `llmPreview()` 方法需要调用 LLM 服务。

**MVP 需要提供**:

```java
package net.ooder.scene.llm;

/**
 * LLM服务接口（MVP层提供）
 */
public interface LlmService {
    
    /**
     * 调用LLM生成内容
     */
    LlmResponse generate(String prompt, LlmConfig config);
    
    /**
     * 从文档提取结构化信息
     */
    Map<String, Object> extractFromDocuments(List<Document> documents, String schema);
}
```

**SE SDK 调用位置**:
- `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\EnterpriseProcedureServiceImpl.java`
  - `llmAssistCreate()` 方法
  - `llmPreview()` 方法

**协同方式**: MVP 通过 SPI 或依赖注入方式提供 `LlmService` 实现。

---

### 2.2 组织架构服务集成 (P1 - 中优先级)

**背景**: 企业规范流程需要关联组织和部门信息。

**MVP 需要提供**:

```java
package net.ooder.scene.org;

/**
 * 组织架构服务接口（MVP层提供）
 */
public interface OrganizationService {
    
    /**
     * 获取组织信息
     */
    OrganizationInfo getOrganization(String organizationId);
    
    /**
     * 获取部门信息
     */
    DepartmentInfo getDepartment(String departmentId);
    
    /**
     * 获取岗位信息
     */
    PositionInfo getPosition(String positionId);
    
    /**
     * 获取用户信息
     */
    UserInfo getUser(String userId);
}
```

**SE SDK 调用位置**:
- `EnterpriseProcedureServiceImpl` - 组织验证
- `ProcedureRoleEntity` - 岗位关联

---

### 2.3 文档存储服务集成 (P1 - 中优先级)

**背景**: 企业规范流程的来源文档需要存储和访问。

**MVP 需要提供**:

```java
package net.ooder.scene.document;

/**
 * 文档存储服务接口（MVP层提供）
 */
public interface DocumentStorageService {
    
    /**
     * 获取文档内容
     */
    Document getDocument(String documentId);
    
    /**
     * 列出文档
     */
    List<Document> listDocuments(DocumentQuery query);
    
    /**
     * 保存文档
     */
    String saveDocument(Document document);
}
```

---

### 2.4 执行器注册与发现 (P0 - 高优先级)

**背景**: SE SDK 提供了多个内置执行器，MVP 需要注册到执行器注册中心。

**MVP 需要做**:

1. 在应用启动时注册执行器:

```java
// 在 Spring Boot 应用启动时
@Bean
public void registerExecutors(ExtensionPointRegistry registry) {
    registry.register(EnhancedActivationStepExecutor.class, 
            new ConfirmParticipantsExecutor(), "confirm-participants");
    registry.register(EnhancedActivationStepExecutor.class, 
            new SelectPushTargetsExecutor(), "select-push-targets");
    registry.register(EnhancedActivationStepExecutor.class, 
            new BindKnowledgeExecutor(), "bind-knowledge");
    registry.register(EnhancedActivationStepExecutor.class, 
            new ConfigConditionsExecutor(), "config-conditions");
    registry.register(EnhancedActivationStepExecutor.class, 
            new ConfigPrivateCapabilitiesExecutor(), "config-private-capabilities");
    registry.register(EnhancedActivationStepExecutor.class, 
            new ConfigWorkflowExecutor(), "config-workflow");
}
```

2. 配置执行器类型映射:

```yaml
# application.yml
scene:
  executors:
    confirm-participants: net.ooder.scene.core.activation.executor.ConfirmParticipantsExecutor
    select-push-targets: net.ooder.scene.core.activation.executor.SelectPushTargetsExecutor
    bind-knowledge: net.ooder.scene.core.activation.executor.BindKnowledgeExecutor
    config-conditions: net.ooder.scene.core.activation.executor.ConfigConditionsExecutor
    config-private-capabilities: net.ooder.scene.core.activation.executor.ConfigPrivateCapabilitiesExecutor
    config-workflow: net.ooder.scene.core.activation.executor.ConfigWorkflowExecutor
```

---

### 2.5 服务实例化与注入 (P0 - 高优先级)

**背景**: SE SDK 的服务需要被 MVP 层实例化和注入。

**MVP 需要做**:

1. 创建 Spring Bean 配置:

```java
@Configuration
public class SceneEngineConfig {
    
    @Bean
    public EnterpriseProcedureService enterpriseProcedureService() {
        return new EnterpriseProcedureServiceImpl();
    }
    
    @Bean
    public FusionTemplateService fusionTemplateService() {
        return new FusionTemplateServiceImpl();
    }
    
    @Bean
    public CompletenessEvaluator completenessEvaluator() {
        return new DefaultCompletenessEvaluator();
    }
}
```

2. 注入 LLM 服务:

```java
@Configuration
public class LlmServiceConfig {
    
    @Bean
    @ConditionalOnMissingBean
    public LlmService llmService() {
        // MVP 提供 LLM 服务实现
        return new MvpLlmServiceImpl();
    }
}
```

---

### 2.6 REST API 暴露 (P2 - 低优先级)

**背景**: MVP 需要暴露 SE SDK 服务的 REST API。

**MVP 需要做**:

```java
@RestController
@RequestMapping("/api/v1/procedures")
public class EnterpriseProcedureController {
    
    @Autowired
    private EnterpriseProcedureService procedureService;
    
    @PostMapping
    public ResponseEntity<EnterpriseProcedure> create(
            @RequestBody EnterpriseProcedureCreateRequest request) {
        return ResponseEntity.ok(procedureService.create(request));
    }
    
    @GetMapping("/{procedureId}")
    public ResponseEntity<EnterpriseProcedure> get(
            @PathVariable String procedureId) {
        return ResponseEntity.ok(procedureService.get(procedureId));
    }
    
    // ... 其他 API
}
```

---

## 三、协同开发时间线

| 阶段 | 任务 | 负责方 | 预计完成 |
|------|------|--------|----------|
| Phase 1 | LLM 服务接口定义与实现 | MVP | Week 1 |
| Phase 1 | 执行器注册与发现 | MVP | Week 1 |
| Phase 2 | 服务实例化与注入 | MVP | Week 2 |
| Phase 2 | 组织架构服务集成 | MVP | Week 2 |
| Phase 3 | 文档存储服务集成 | MVP | Week 3 |
| Phase 3 | REST API 暴露 | MVP | Week 3 |

---

## 四、接口定义文件位置

| 接口 | Agent SDK 路径 |
|------|----------------|
| `EnterpriseProcedureService` | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\EnterpriseProcedureService.java` |
| `FusionTemplateService` | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusionTemplateService.java` |
| `CompletenessEvaluator` | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\completeness\CompletenessEvaluator.java` |
| `EnhancedActivationStepExecutor` | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\agent\EnhancedActivationStepExecutor.java` |

---

## 五、SE SDK 实现文件位置

### 5.1 核心服务

| 实现类 | SE SDK 路径 |
|--------|-------------|
| `EnterpriseProcedureServiceImpl` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\EnterpriseProcedureServiceImpl.java` |
| `FusionTemplateServiceImpl` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusionTemplateServiceImpl.java` |
| `DefaultCompletenessEvaluator` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\completeness\DefaultCompletenessEvaluator.java` |

### 5.2 内置执行器

| 执行器 | SE SDK 路径 |
|--------|-------------|
| `ConfirmParticipantsExecutor` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\activation\executor\ConfirmParticipantsExecutor.java` |
| `SelectPushTargetsExecutor` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\activation\executor\SelectPushTargetsExecutor.java` |
| `BindKnowledgeExecutor` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\activation\executor\BindKnowledgeExecutor.java` |
| `ConfigConditionsExecutor` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\activation\executor\ConfigConditionsExecutor.java` |
| `ConfigPrivateCapabilitiesExecutor` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\activation\executor\ConfigPrivateCapabilitiesExecutor.java` |
| `ConfigWorkflowExecutor` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\activation\executor\ConfigWorkflowExecutor.java` |

### 5.3 实体类

| 实体类 | SE SDK 路径 |
|--------|-------------|
| `EnterpriseProcedureEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\EnterpriseProcedureEntity.java` |
| `ProcedureRoleEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\ProcedureRoleEntity.java` |
| `ProcedureStepEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\ProcedureStepEntity.java` |
| `ProcedureRuleEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\ProcedureRuleEntity.java` |
| `FusedWorkflowTemplateEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusedWorkflowTemplateEntity.java` |
| `FusionConflictEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusionConflictEntity.java` |

---

## 六、验收标准

### 6.1 LLM 服务集成验收

- [ ] `EnterpriseProcedureService.llmAssistCreate()` 可正常调用
- [ ] `EnterpriseProcedureService.llmPreview()` 可正常调用
- [ ] LLM 生成的企业规范流程数据结构正确

### 6.2 执行器注册验收

- [ ] 所有 6 个执行器已注册到 `ExtensionPointRegistry`
- [ ] 执行器可通过 `getStepType()` 正确查找
- [ ] 执行器 `execute()` 方法可正常执行

### 6.3 服务注入验收

- [ ] `EnterpriseProcedureService` Bean 可正常注入
- [ ] `FusionTemplateService` Bean 可正常注入
- [ ] `CompletenessEvaluator` Bean 可正常注入

---

## 七、联系方式

| 项目 | 负责团队 |
|------|----------|
| SE SDK | SE SDK Team |
| MVP | MVP Skill Scene Team |

---

**文档维护者**: SE SDK Team  
**最后更新**: 2026-03-28
