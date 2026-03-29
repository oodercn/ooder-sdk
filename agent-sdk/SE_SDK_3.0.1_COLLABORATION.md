# Agent SDK 3.0.1 协作需求说明书

> **文档版本**: v1.0  
> **创建日期**: 2026-03-28  
> **目标版本**: Agent SDK 3.0.1 / SE SDK 3.0.1  
> **状态**: 待评审  
> **协作方**: Agent SDK Team ↔ SE SDK Team

---

## 一、协作背景

Agent SDK 3.0.1 已完成以下接口定义，需要 SE SDK 团队实现具体功能：

1. **企业规范流程接口** (`api/procedure`)
2. **融合模板接口** (`api/fusion`)
3. **完善度评估接口** (`api/completeness`)
4. **增强激活步骤执行器接口** (`api/agent/EnhancedActivationStepExecutor`)

---

## 二、Agent SDK 已完成工作

### 2.1 新增接口文件清单

#### api/procedure 包 (企业规范流程)

| 文件 | 绝对路径 |
|------|---------|
| EnterpriseProcedure.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\EnterpriseProcedure.java` |
| EnterpriseProcedureService.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\EnterpriseProcedureService.java` |
| ProcedureSource.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ProcedureSource.java` |
| ProcedureStatus.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ProcedureStatus.java` |
| ProcedureRole.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ProcedureRole.java` |
| ProcedureStep.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ProcedureStep.java` |
| ProcedureRule.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ProcedureRule.java` |
| ProcedureRuleType.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ProcedureRuleType.java` |
| ErrorAction.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ErrorAction.java` |
| SourceMetadata.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\SourceMetadata.java` |
| ActivationStepRef.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ActivationStepRef.java` |
| Document.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\Document.java` |
| ValidationResult.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\ValidationResult.java` |
| CompletenessSuggestion.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\CompletenessSuggestion.java` |
| EnterpriseProcedureCreateRequest.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\EnterpriseProcedureCreateRequest.java` |
| EnterpriseProcedureUpdateRequest.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\EnterpriseProcedureUpdateRequest.java` |
| EnterpriseProcedureQueryRequest.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\EnterpriseProcedureQueryRequest.java` |
| EnterpriseProcedurePreview.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\procedure\EnterpriseProcedurePreview.java` |

#### api/completeness 包 (完善度评估)

| 文件 | 绝对路径 |
|------|---------|
| CompletenessEvaluator.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\completeness\CompletenessEvaluator.java` |
| CompletenessDetail.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\completeness\CompletenessDetail.java` |
| CompletenessDimension.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\completeness\CompletenessDimension.java` |
| CompletenessIssue.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\completeness\CompletenessIssue.java` |
| IssueSeverity.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\completeness\IssueSeverity.java` |
| CompletenessDimensionConfig.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\completeness\CompletenessDimensionConfig.java` |
| CompletenessCheckItem.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\completeness\CompletenessCheckItem.java` |

#### api/fusion 包 (融合模板)

| 文件 | 绝对路径 |
|------|---------|
| FusionTemplateService.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusionTemplateService.java` |
| FusionRequest.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusionRequest.java` |
| FusionStrategy.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusionStrategy.java` |
| FusionPriority.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusionPriority.java` |
| FusedWorkflowTemplate.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusedWorkflowTemplate.java` |
| FusedRole.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusedRole.java` |
| FusionConflict.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusionConflict.java` |
| ConflictType.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\ConflictType.java` |
| ConflictResolution.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\ConflictResolution.java` |
| ProcedureMatchResult.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\ProcedureMatchResult.java` |
| FusionPreview.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusionPreview.java` |
| ConflictResolutionRequest.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\ConflictResolutionRequest.java` |
| ConflictResolutionItem.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\ConflictResolutionItem.java` |
| TemplateVersion.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\TemplateVersion.java` |
| TemplateStatus.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\TemplateStatus.java` |
| FusionTemplateQueryRequest.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\FusionTemplateQueryRequest.java` |
| CapabilityBindingDef.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\fusion\CapabilityBindingDef.java` |

#### api/agent 包 (增强执行器)

| 文件 | 绝对路径 |
|------|---------|
| EnhancedActivationStepExecutor.java | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\agent\EnhancedActivationStepExecutor.java` |

---

## 三、SE SDK 需要实现的任务

### 3.1 企业规范流程服务实现 (P0)

**任务清单**:

| 任务ID | 任务描述 | 预估工时 |
|--------|----------|----------|
| S1.1 | 实现 `EnterpriseProcedureEntity` 实体类 | 0.5d |
| S1.2 | 实现 `ProcedureRoleEntity` 实体类 | 0.25d |
| S1.3 | 实现 `ProcedureStepEntity` 实体类 | 0.25d |
| S1.4 | 实现 `ProcedureRuleEntity` 实体类 | 0.25d |
| S1.5 | 实现 `SourceMetadataEntity` 实体类 | 0.25d |
| S1.6 | 实现 `EnterpriseProcedureServiceImpl` | 1.5d |
| S1.7 | 实现 `YamlProcedurePersistence` | 1d |
| S1.8 | 实现请求/响应模型实体类 | 0.5d |
| S1.9 | 编写单元测试 | 0.5d |

**包路径**: `net.ooder.scene.procedure`

### 3.2 融合模板服务实现 (P0)

**任务清单**:

| 任务ID | 任务描述 | 预估工时 |
|--------|----------|----------|
| S2.1 | 实现 `FusedWorkflowTemplateEntity` 实体类 | 0.5d |
| S2.2 | 实现 `FusedRoleEntity` 实体类 | 0.25d |
| S2.3 | 实现 `FusionConflictEntity` 实体类 | 0.25d |
| S2.4 | 实现 `DefaultProcedureMatcher` | 1.5d |
| S2.5 | 实现 `DefaultConflictResolver` | 1.5d |
| S2.6 | 实现 `FusionTemplateServiceImpl` | 1.5d |
| S2.7 | 扩展 `SkillInstallProcessor` | 0.5d |
| S2.8 | 实现 `YamlFusedTemplatePersistence` | 0.5d |
| S2.9 | 编写单元测试 | 0.5d |

**包路径**: `net.ooder.scene.fusion`

### 3.3 完善度评估器实现 (P1)

**任务清单**:

| 任务ID | 任务描述 | 预估工时 |
|--------|----------|----------|
| S3.1 | 实现各实体类 | 0.5d |
| S3.2 | 实现 `DefaultCompletenessEvaluator` | 1.5d |
| S3.3 | 实现评估维度配置 | 0.5d |
| S3.4 | 编写单元测试 | 0.5d |

**包路径**: `net.ooder.scene.procedure.completeness`

### 3.4 增强激活步骤执行器实现 (P2)

**任务清单**:

| 任务ID | 任务描述 | 预估工时 |
|--------|----------|----------|
| S4.1 | 扩展 `ActivationFlowEngine` 实现 | 1d |
| S4.2 | 实现具体执行器 | 1d |
| S4.3 | 编写单元测试 | 0.5d |

---

## 四、接口使用示例

### 4.1 创建企业规范流程

```java
// 引入 Agent SDK 接口
import net.ooder.sdk.api.procedure.*;

// SE SDK 提供实现
EnterpriseProcedureService procedureService = getProcedureService();

EnterpriseProcedureCreateRequest request = new EnterpriseProcedureCreateRequestEntity();
request.setName("采购审批流程");
request.setCategory("PROCUREMENT");
request.setSource(ProcedureSource.MANUAL);
request.setOrganizationId("org-001");
request.setAuthor("user-001");

EnterpriseProcedure procedure = procedureService.create(request);
```

### 4.2 执行融合

```java
import net.ooder.sdk.api.fusion.*;

FusionTemplateService fusionService = getFusionService();

List<ProcedureMatchResult> matches = fusionService.matchProcedures("skill-001");
if (!matches.isEmpty()) {
    FusionRequest request = new FusionRequestEntity();
    request.setEnterpriseProcedureId(matches.get(0).getProcedureId());
    request.setSkillId("skill-001");
    request.setFusedBy("user-001");
    
    FusedWorkflowTemplate template = fusionService.fuse(request);
}
```

---

## 五、依赖配置

SE SDK 需要添加 Agent SDK 依赖：

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>3.0.1</version>
</dependency>
```

---

## 六、验收标准

### 6.1 接口验收

- [ ] `EnterpriseProcedureService` 所有方法可正常调用
- [ ] `FusionTemplateService` 所有方法可正常调用
- [ ] `CompletenessEvaluator` 评估结果正确
- [ ] `EnhancedActivationStepExecutor` 扩展方法可正常调用

### 6.2 性能验收

- [ ] 企业规范流程CRUD操作 < 100ms
- [ ] 完善度评估 < 500ms
- [ ] 融合操作 < 1s
- [ ] 匹配计算 < 200ms

---

## 七、版本信息

| 项目 | 版本 |
|------|------|
| Agent SDK | 3.0.1 |
| SE SDK | 3.0.1 |
| Maven 本地仓库 | `D:\maven\.m2\repository\net\ooder\` |

---

**文档维护者**: Agent SDK Team  
**最后更新**: 2026-03-28
