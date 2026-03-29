# SE SDK 3.0.1 扩展协作说明书

> **文档版本**: v1.0  
> **创建日期**: 2026-03-28  
> **目标版本**: SE SDK 3.0.1  
> **状态**: 实现中  
> **协作方**: MVP Skill Scene Team ↔ SE SDK Team

---

## 一、协作背景

### 1.1 项目背景

MVP项目正在推进**场景组智能工作流系统**建设，需要SE SDK提供底层能力支撑。经过深入分析现有SE SDK 2.3.1/3.0.0实现，发现需要新增以下核心能力：

1. **企业规范流程数据模型** - 支持LLM生成和结构化存储
2. **融合工作流模板机制** - 支持企业规范与技能定义的智能融合
3. **完善度评估体系** - 支持流程完整性量化评估
4. **增强的激活步骤执行器** - 支持更复杂的激活流程
5. **可视化流程配置支持** - 提供流程可视化数据结构

### 1.2 协作目标

本文档明确SE SDK需要扩展的接口和数据模型，供MVP层调用，确保双方开发工作顺利对接。

---

## 二、SE SDK 需要提供的接口

### 2.1 企业规范流程服务接口 (P0)

#### 2.1.1 接口定义

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\EnterpriseProcedureService.java`

```java
package net.ooder.scene.procedure;

import java.util.List;
import java.util.Map;

/**
 * 企业规范流程服务接口
 * 
 * <p>提供企业规范流程的完整生命周期管理，包括CRUD、LLM辅助创建、完善度评估等。</p>
 * 
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface EnterpriseProcedureService {
    
    // ========== CRUD操作 ==========
    
    /**
     * 创建企业规范流程
     * 
     * @param request 创建请求
     * @return 创建的企业规范流程
     * @throws ProcedureValidationException 验证失败时抛出
     */
    EnterpriseProcedure create(EnterpriseProcedureCreateRequest request);
    
    /**
     * 获取企业规范流程
     * 
     * @param procedureId 流程ID
     * @return 企业规范流程，不存在时返回 null
     */
    EnterpriseProcedure get(String procedureId);
    
    /**
     * 更新企业规范流程
     * 
     * @param procedureId 流程ID
     * @param request 更新请求
     * @return 更新后的企业规范流程
     * @throws ProcedureNotFoundException 流程不存在时抛出
     */
    EnterpriseProcedure update(String procedureId, EnterpriseProcedureUpdateRequest request);
    
    /**
     * 删除企业规范流程
     * 
     * @param procedureId 流程ID
     * @throws ProcedureNotFoundException 流程不存在时抛出
     */
    void delete(String procedureId);
    
    /**
     * 列出企业规范流程
     * 
     * @param request 查询请求
     * @return 流程列表
     */
    List<EnterpriseProcedure> list(EnterpriseProcedureQueryRequest request);
    
    // ========== LLM辅助 ==========
    
    /**
     * LLM辅助创建企业规范流程
     * 
     * <p>根据提供的文档，使用LLM生成企业规范流程。</p>
     * 
     * @param documents 来源文档列表
     * @return 生成的企业规范流程
     * @throws LlmServiceException LLM服务异常时抛出
     */
    EnterpriseProcedure llmAssistCreate(List<Document> documents);
    
    /**
     * 获取LLM生成预览
     * 
     * <p>预览LLM生成的企业规范流程，不实际保存。</p>
     * 
     * @param documents 来源文档列表
     * @return 预览结果
     */
    EnterpriseProcedurePreview llmPreview(List<Document> documents);
    
    // ========== 完善度 ==========
    
    /**
     * 评估企业规范流程完善度
     * 
     * @param procedureId 流程ID
     * @return 完善度详情
     */
    CompletenessDetail evaluateCompleteness(String procedureId);
    
    /**
     * 获取完善度改进建议
     * 
     * @param procedureId 流程ID
     * @return 改进建议列表
     */
    List<CompletenessSuggestion> getCompletenessSuggestions(String procedureId);
    
    // ========== 验证 ==========
    
    /**
     * 验证企业规范流程
     * 
     * @param procedureId 流程ID
     * @return 验证结果
     */
    ValidationResult validate(String procedureId);
}
```

#### 2.1.2 请求/响应模型

**EnterpriseProcedureCreateRequest**:
```java
package net.ooder.scene.procedure;

public class EnterpriseProcedureCreateRequest {
    private String name;                              // 必填：流程名称
    private String category;                          // 必填：流程分类
    private String description;                       // 可选：描述
    private List<String> tags;                        // 可选：标签
    private ProcedureSource source;                   // 必填：来源类型
    private String organizationId;                    // 必填：组织ID
    private List<String> departmentIds;               // 可选：部门ID列表
    private List<ProcedureRole> roles;                // 必填：角色定义
    private List<ProcedureStep> steps;                // 必填：流程步骤
    private List<ProcedureRule> rules;                // 可选：约束规则
    private List<String> requiredCapabilities;        // 可选：必需能力
    private List<String> knowledgeBaseIds;            // 可选：知识库ID列表
    private String author;                            // 必填：创建者
    private Map<String, Object> extensions;           // 可选：扩展属性
}
```

**EnterpriseProcedureQueryRequest**:
```java
package net.ooder.scene.procedure;

public class EnterpriseProcedureQueryRequest {
    private String organizationId;                    // 组织ID
    private String category;                          // 分类
    private ProcedureStatus status;                   // 状态
    private ProcedureSource source;                   // 来源类型
    private String keyword;                           // 关键词搜索
    private int minCompleteness;                      // 最低完善度
    private int page;                                 // 页码
    private int pageSize;                             // 每页数量
}
```

#### 2.1.3 核心数据模型

**EnterpriseProcedure**:
```java
package net.ooder.scene.procedure;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 企业规范流程实体
 */
public class EnterpriseProcedure implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 基础标识 ==========
    private String procedureId;                       // 流程ID（UUID）
    private String name;                              // 流程名称
    private String category;                          // 流程分类
    private String description;                       // 描述
    private List<String> tags;                        // 标签
    
    // ========== 来源信息 ==========
    private ProcedureSource source;                   // 来源类型
    private SourceMetadata sourceMetadata;            // 来源元数据
    
    // ========== 状态管理 ==========
    private ProcedureStatus status;                   // 状态
    private int completeness;                         // 完善度分数(0-100)
    private CompletenessDetail completenessDetail;    // 完善度详情
    
    // ========== 组织关联 ==========
    private String organizationId;                    // 组织ID
    private List<String> departmentIds;               // 部门ID列表
    
    // ========== 角色定义 ==========
    private List<ProcedureRole> roles;                // 角色列表
    
    // ========== 流程步骤 ==========
    private List<ProcedureStep> steps;                // 步骤列表
    
    // ========== 约束规则 ==========
    private List<ProcedureRule> rules;                // 规则列表
    
    // ========== 能力要求 ==========
    private List<String> requiredCapabilities;        // 必需能力
    private List<String> optionalCapabilities;        // 可选能力
    
    // ========== 知识库关联 ==========
    private List<String> knowledgeBaseIds;            // 知识库ID列表
    
    // ========== 元数据 ==========
    private Long createTime;                          // 创建时间
    private Long updateTime;                          // 更新时间
    private String author;                            // 创建者
    private String version;                           // 版本号
    private Map<String, Object> extensions;           // 扩展属性
    
    // Getters and Setters...
}
```

**ProcedureRole**:
```java
package net.ooder.scene.procedure;

/**
 * 规范角色定义
 */
public class ProcedureRole implements Serializable {
    
    private String roleId;                            // 角色ID
    private String name;                              // 角色名称
    private String description;                       // 角色描述
    private int priority;                             // 优先级
    private boolean required;                         // 是否必需
    private int minCount;                             // 最小人数
    private int maxCount;                             // 最大人数
    
    // 组织关联
    private List<String> positionIds;                 // 岗位ID列表
    private List<String> permissionIds;               // 权限ID列表
    
    // 能力要求
    private List<String> requiredCapabilities;        // 必需能力
    
    // 激活配置
    private List<ActivationStepRef> activationSteps;  // 激活步骤引用
    
    // 菜单权限
    private List<String> menuIds;                     // 菜单ID列表
    
    private Map<String, Object> extensions;           // 扩展属性
}
```

**ProcedureRule**:
```java
package net.ooder.scene.procedure;

/**
 * 规范约束规则
 */
public class ProcedureRule implements Serializable {
    
    private String ruleId;                            // 规则ID
    private String name;                              // 规则名称
    private RuleType type;                            // 规则类型
    private String description;                       // 规则描述
    private String expression;                        // 规则表达式
    private int priority;                             // 优先级
    private String errorMessage;                      // 错误消息
    private ErrorAction errorAction;                  // 错误处理动作
    private Map<String, Object> extensions;           // 扩展属性
}

public enum RuleType {
    COMPLIANCE,    // 合规规则
    APPROVAL,      // 审批规则
    CONSTRAINT,    // 约束规则
    VALIDATION,    // 验证规则
    BUSINESS       // 业务规则
}

public enum ErrorAction {
    WARN,          // 警告
    BLOCK,         // 阻止
    CORRECT,       // 自动纠正
    ESCALATE       // 上报
}
```

---

### 2.2 融合模板服务接口 (P0)

#### 2.2.1 接口定义

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusionTemplateService.java`

```java
package net.ooder.scene.fusion;

import java.util.List;

/**
 * 融合模板服务接口
 * 
 * <p>提供企业规范流程与技能定义的智能融合能力。</p>
 * 
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface FusionTemplateService {
    
    // ========== 匹配 ==========
    
    /**
     * 匹配企业规范流程
     * 
     * <p>根据技能定义，查找匹配的企业规范流程。</p>
     * 
     * @param skillId 技能ID
     * @return 匹配结果列表（按匹配度降序）
     */
    List<ProcedureMatchResult> matchProcedures(String skillId);
    
    /**
     * 计算匹配度
     * 
     * @param procedureId 企业规范流程ID
     * @param skillId 技能ID
     * @return 匹配度分数(0-100)
     */
    int calculateMatchScore(String procedureId, String skillId);
    
    // ========== 融合 ==========
    
    /**
     * 执行融合
     * 
     * <p>将企业规范流程与技能定义融合，生成融合工作流模板。</p>
     * 
     * @param request 融合请求
     * @return 融合后的工作流模板
     * @throws FusionConflictException 存在未解决的冲突时抛出
     */
    FusedWorkflowTemplate fuse(FusionRequest request);
    
    /**
     * 预览融合结果
     * 
     * <p>预览融合结果，不实际保存。</p>
     * 
     * @param request 融合请求
     * @return 融合预览结果
     */
    FusionPreview preview(FusionRequest request);
    
    /**
     * 解决融合冲突
     * 
     * @param templateId 模板ID
     * @param request 冲突解决请求
     * @return 更新后的融合模板
     */
    FusedWorkflowTemplate resolveConflict(String templateId, ConflictResolutionRequest request);
    
    // ========== 模板管理 ==========
    
    /**
     * 获取融合模板
     * 
     * @param templateId 模板ID
     * @return 融合模板，不存在时返回 null
     */
    FusedWorkflowTemplate get(String templateId);
    
    /**
     * 列出融合模板
     * 
     * @param request 查询请求
     * @return 模板列表
     */
    List<FusedWorkflowTemplate> list(FusionTemplateQueryRequest request);
    
    /**
     * 删除融合模板
     * 
     * @param templateId 模板ID
     */
    void delete(String templateId);
    
    // ========== 版本管理 ==========
    
    /**
     * 获取模板版本历史
     * 
     * @param templateId 模板ID
     * @return 版本历史列表
     */
    List<TemplateVersion> getVersionHistory(String templateId);
    
    /**
     * 回滚到指定版本
     * 
     * @param templateId 模板ID
     * @param version 版本号
     * @return 回滚后的模板
     */
    FusedWorkflowTemplate rollback(String templateId, int version);
}
```

#### 2.2.2 请求/响应模型

**FusionRequest**:
```java
package net.ooder.scene.fusion;

public class FusionRequest {
    private String enterpriseProcedureId;             // 必填：企业规范流程ID
    private String skillId;                           // 必填：技能ID
    private String skillTemplateId;                   // 可选：技能模板ID
    private FusionStrategy fusionStrategy;            // 必填：融合策略
    private String name;                              // 可选：模板名称
    private String description;                       // 可选：模板描述
    private String fusedBy;                           // 必填：操作者
}
```

**FusionStrategy**:
```java
package net.ooder.scene.fusion;

/**
 * 融合策略配置
 */
public class FusionStrategy implements Serializable {
    
    private FusionPriority rolePriority;              // 角色定义优先级
    private FusionPriority activationStepPriority;    // 激活步骤优先级
    private FusionPriority menuPriority;              // 菜单配置优先级
    private FusionPriority capabilityPriority;        // 能力配置优先级
    private FusionPriority rulePriority;              // 约束规则优先级
    private boolean autoResolveConflict;              // 是否自动解决冲突
    private Map<String, Object> customRules;          // 自定义融合规则
}

public enum FusionPriority {
    ENTERPRISE_FIRST,    // 企业规范优先
    SKILL_FIRST,         // 技能定义优先
    MERGE,               // 合并
    USER_DECIDE          // 用户决定
}
```

**FusionConflict**:
```java
package net.ooder.scene.fusion;

/**
 * 融合冲突记录
 */
public class FusionConflict implements Serializable {
    
    private String conflictId;                        // 冲突ID
    private String field;                             // 冲突字段
    private ConflictType type;                        // 冲突类型
    private Object enterpriseValue;                   // 企业规范值
    private Object skillValue;                        // 技能定义值
    private ConflictResolution resolution;            // 解决方式
    private Object resolvedValue;                     // 解决后的值
    private String resolvedBy;                        // 解决者
    private Long resolvedAt;                          // 解决时间
    private String comment;                           // 备注
}

public enum ConflictType {
    VALUE_MISMATCH,          // 值不匹配
    TYPE_MISMATCH,           // 类型不匹配
    MISSING_IN_ENTERPRISE,   // 企业规范缺失
    MISSING_IN_SKILL,        // 技能定义缺失
    STRUCTURE_MISMATCH       // 结构不匹配
}

public enum ConflictResolution {
    USE_ENTERPRISE,    // 使用企业规范
    USE_SKILL,         // 使用技能定义
    MERGE,             // 合并
    CUSTOM,            // 自定义
    SKIP               // 跳过
}
```

#### 2.2.3 核心数据模型

**FusedWorkflowTemplate**:
```java
package net.ooder.scene.fusion;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 融合工作流模板
 */
public class FusedWorkflowTemplate implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== 基础标识 ==========
    private String templateId;                        // 模板ID
    private String name;                              // 模板名称
    private String description;                       // 描述
    
    // ========== 来源信息 ==========
    private String enterpriseProcedureId;             // 企业规范流程ID
    private String skillId;                           // 技能ID
    private String skillTemplateId;                   // 技能模板ID
    
    // ========== 融合信息 ==========
    private int matchScore;                           // 匹配度分数
    private FusionStrategy fusionStrategy;            // 融合策略
    private List<FusionConflict> fusionConflicts;     // 融合冲突列表
    private Long fusionTime;                          // 融合时间
    private String fusedBy;                           // 融合操作者
    
    // ========== 融合结果 ==========
    private List<FusedRole> roles;                    // 融合后角色
    private Map<String, List<ActivationStepConfig>> activationSteps; // 激活步骤
    private Map<String, List<MenuConfig>> menus;      // 菜单配置
    private List<ProcedureRule> rules;                // 约束规则
    private List<CapabilityBindingDef> capabilities;  // 能力绑定
    
    // ========== 知识库配置 ==========
    private List<KnowledgeBindingDef> knowledgeBindings; // 知识库绑定
    
    // ========== 可视化配置 ==========
    private VisualizationConfig visualization;        // 可视化配置
    
    // ========== 元数据 ==========
    private Long createTime;                          // 创建时间
    private Long updateTime;                          // 更新时间
    private String version;                           // 版本号
    private TemplateStatus status;                    // 状态
    private Map<String, Object> extensions;           // 扩展属性
}
```

---

### 2.3 完善度评估器接口 (P1)

#### 2.3.1 接口定义

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\procedure\completeness\CompletenessEvaluator.java`

```java
package net.ooder.scene.procedure.completeness;

import net.ooder.scene.procedure.EnterpriseProcedure;
import java.util.List;

/**
 * 完善度评估器接口
 * 
 * <p>评估企业规范流程的完整性。</p>
 * 
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface CompletenessEvaluator {
    
    /**
     * 评估企业规范流程完善度
     * 
     * @param procedure 企业规范流程
     * @return 完善度详情
     */
    CompletenessDetail evaluate(EnterpriseProcedure procedure);
    
    /**
     * 获取评估维度配置
     * 
     * @return 维度配置列表
     */
    List<CompletenessDimensionConfig> getDimensionConfigs();
    
    /**
     * 设置评估维度配置
     * 
     * @param configs 维度配置列表
     */
    void setDimensionConfigs(List<CompletenessDimensionConfig> configs);
}
```

#### 2.3.2 数据模型

**CompletenessDetail**:
```java
package net.ooder.scene.procedure.completeness;

/**
 * 完善度详情
 */
public class CompletenessDetail implements Serializable {
    
    private int overallScore;                         // 总分(0-100)
    private List<CompletenessDimension> dimensions;   // 维度详情
    private List<CompletenessIssue> issues;           // 待完善项
    private List<String> suggestions;                 // 改进建议
}

/**
 * 完善度维度
 */
public class CompletenessDimension implements Serializable {
    
    private String name;                              // 维度名称
    private int weight;                               // 权重(百分比)
    private int score;                                // 得分(0-100)
    private String status;                            // 状态(COMPLETE, PARTIAL, MISSING)
    private List<String> checkedItems;                // 已检查项
    private List<String> missingItems;                // 缺失项
}

/**
 * 完善度问题
 */
public class CompletenessIssue implements Serializable {
    
    private String dimension;                         // 所属维度
    private String description;                       // 问题描述
    private IssueSeverity severity;                   // 严重程度
    private String suggestion;                        // 改进建议
    private String actionUrl;                         // 操作链接
}

public enum IssueSeverity {
    CRITICAL,    // 严重 - 必须修复
    WARNING,     // 警告 - 建议修复
    INFO         // 信息 - 可选修复
}
```

---

### 2.4 增强激活步骤执行器接口 (P2)

#### 2.4.1 接口定义

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\spi\EnhancedActivationStepExecutor.java`

```java
package net.ooder.scene.core.spi;

import net.ooder.scene.core.activation.model.ActivationProcess;
import net.ooder.scene.core.template.ActivationStepConfig;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 增强的激活步骤执行器接口
 * 
 * <p>扩展自 ActivationStepExecutor，提供更丰富的执行控制能力。</p>
 * 
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface EnhancedActivationStepExecutor extends ActivationStepExecutor {
    
    /**
     * 获取步骤依赖
     * 
     * <p>定义当前步骤依赖的其他步骤ID列表，依赖步骤完成后才会执行当前步骤。</p>
     * 
     * @return 依赖的步骤ID列表
     */
    default List<String> getDependencies() {
        return Collections.emptyList();
    }
    
    /**
     * 前置处理
     * 
     * <p>在步骤执行前调用，可用于准备工作或验证。</p>
     * 
     * @param stepConfig 步骤配置
     * @param process 激活流程
     * @param context 执行上下文
     */
    default void beforeExecute(ActivationStepConfig stepConfig, 
                               ActivationProcess process, 
                               Map<String, Object> context) {
        // 默认空实现
    }
    
    /**
     * 后置处理
     * 
     * <p>在步骤执行后调用，可用于清理工作或记录日志。</p>
     * 
     * @param stepConfig 步骤配置
     * @param process 激活流程
     * @param context 执行上下文
     * @param result 执行结果
     */
    default void afterExecute(ActivationStepConfig stepConfig, 
                              ActivationProcess process, 
                              Map<String, Object> context,
                              StepResult result) {
        // 默认空实现
    }
    
    /**
     * 验证步骤输入
     * 
     * @param stepConfig 步骤配置
     * @param input 输入参数
     * @return 验证结果
     */
    default ValidationResult validateInput(ActivationStepConfig stepConfig,
                                           Map<String, Object> input) {
        return ValidationResult.success();
    }
    
    /**
     * 获取步骤输出Schema
     * 
     * <p>定义步骤输出的数据结构，用于后续步骤引用。</p>
     * 
     * @return 输出字段名到类型的映射
     */
    default Map<String, Class<?>> getOutputSchema() {
        return Collections.emptyMap();
    }
    
    /**
     * 是否支持回滚
     * 
     * @return true 如果支持回滚
     */
    default boolean supportsRollback() {
        return false;
    }
    
    /**
     * 回滚步骤
     * 
     * <p>当后续步骤失败时，回滚当前步骤的变更。</p>
     * 
     * @param stepConfig 步骤配置
     * @param process 激活流程
     * @param context 执行上下文
     */
    default void rollback(ActivationStepConfig stepConfig, 
                          ActivationProcess process, 
                          Map<String, Object> context) {
        // 默认不支持
    }
}
```

#### 2.4.2 验证结果模型

**ValidationResult**:
```java
package net.ooder.scene.core.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果
 */
public class ValidationResult {
    
    private boolean valid;
    private List<String> errors;
    private List<String> warnings;
    
    public static ValidationResult success() {
        ValidationResult result = new ValidationResult();
        result.valid = true;
        return result;
    }
    
    public static ValidationResult failure(String error) {
        ValidationResult result = new ValidationResult();
        result.valid = false;
        result.errors = List.of(error);
        return result;
    }
    
    public static ValidationResult failure(List<String> errors) {
        ValidationResult result = new ValidationResult();
        result.valid = false;
        result.errors = new ArrayList<>(errors);
        return result;
    }
    
    // Getters...
}
```

---

### 2.5 可视化服务接口 (P2)

#### 2.5.1 接口定义

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\visualization\FlowVisualizationService.java`

```java
package net.ooder.scene.visualization;

/**
 * 流程可视化服务接口
 * 
 * <p>提供流程可视化的数据结构支持和BPMN导入导出能力。</p>
 * 
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface FlowVisualizationService {
    
    /**
     * 生成可视化配置
     * 
     * @param templateId 模板ID（可以是融合模板ID或场景模板ID）
     * @return 可视化配置
     */
    VisualizationConfig generateVisualization(String templateId);
    
    /**
     * 从可视化配置更新模板
     * 
     * @param templateId 模板ID
     * @param config 可视化配置
     */
    void updateFromVisualization(String templateId, VisualizationConfig config);
    
    /**
     * 导出为BPMN格式
     * 
     * @param templateId 模板ID
     * @return BPMN XML字符串
     */
    String exportToBpmn(String templateId);
    
    /**
     * 从BPMN导入
     * 
     * @param bpmnXml BPMN XML字符串
     * @return 生成的模板ID
     */
    String importFromBpmn(String bpmnXml);
}
```

#### 2.5.2 数据模型

**VisualizationConfig**:
```java
package net.ooder.scene.visualization;

/**
 * 可视化配置
 */
public class VisualizationConfig implements Serializable {
    
    private String layout;                            // 布局类型(horizontal, vertical, radial)
    private List<FlowNode> nodes;                     // 节点列表
    private List<FlowEdge> edges;                     // 边列表
    private Map<String, Object> config;               // 配置参数
}

/**
 * 流程节点
 */
public class FlowNode implements Serializable {
    
    private String nodeId;                            // 节点ID
    private String type;                              // 节点类型(start, end, step, gateway, subprocess)
    private String name;                              // 节点名称
    private String description;                       // 节点描述
    private int x;                                    // X坐标
    private int y;                                    // Y坐标
    private int width;                                // 宽度
    private int height;                               // 高度
    private String style;                             // 样式配置
    private Map<String, Object> data;                 // 节点数据
}

/**
 * 流程边
 */
public class FlowEdge implements Serializable {
    
    private String edgeId;                            // 边ID
    private String sourceId;                          // 源节点ID
    private String targetId;                          // 目标节点ID
    private String label;                             // 边标签
    private String condition;                         // 条件表达式
    private String style;                             // 样式配置
    private Map<String, Object> data;                 // 边数据
}
```

---

## 三、SE SDK 需要的依赖（由MVP层提供）

### 3.1 LLM服务接口 (P0)

MVP层需要提供LLM调用服务，供SE SDK调用。

```java
package net.ooder.scene.llm;

/**
 * LLM服务接口（MVP层提供）
 */
public interface LlmService {
    
    /**
     * 调用LLM生成内容
     * 
     * @param prompt 提示词
     * @param config 配置参数
     * @return 生成结果
     */
    LlmResponse generate(String prompt, LlmConfig config);
    
    /**
     * 从文档提取结构化信息
     * 
     * @param documents 文档列表
     * @param schema 输出Schema
     * @return 结构化结果
     */
    Map<String, Object> extractFromDocuments(List<Document> documents, String schema);
}
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\llm\LlmService.java`

### 3.2 组织架构服务接口 (P1)

MVP层需要提供组织架构服务，供SE SDK查询组织信息。

```java
package net.ooder.scene.org;

/**
 * 组织架构服务接口（MVP层提供）
 */
public interface OrganizationService {
    
    /**
     * 获取组织信息
     * 
     * @param organizationId 组织ID
     * @return 组织信息
     */
    OrganizationInfo getOrganization(String organizationId);
    
    /**
     * 获取部门信息
     * 
     * @param departmentId 部门ID
     * @return 部门信息
     */
    DepartmentInfo getDepartment(String departmentId);
    
    /**
     * 获取岗位信息
     * 
     * @param positionId 岗位ID
     * @return 岗位信息
     */
    PositionInfo getPosition(String positionId);
}
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\org\OrganizationService.java`

### 3.3 文档存储服务接口 (P1)

MVP层需要提供文档存储服务，供SE SDK访问企业文档。

```java
package net.ooder.scene.document;

/**
 * 文档存储服务接口（MVP层提供）
 */
public interface DocumentStorageService {
    
    /**
     * 获取文档内容
     * 
     * @param documentId 文档ID
     * @return 文档内容
     */
    Document getDocument(String documentId);
    
    /**
     * 列出文档
     * 
     * @param query 查询条件
     * @return 文档列表
     */
    List<Document> listDocuments(DocumentQuery query);
}
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\document\DocumentStorageService.java`

---

## 四、SPI扩展点

### 4.1 新增扩展点

| 扩展点 | 接口 | 说明 | 优先级 |
|--------|------|------|--------|
| 完善度评估器 | `CompletenessEvaluator` | 自定义完善度评估逻辑 | P1 |
| 融合冲突解决器 | `FusionConflictResolver` | 自定义冲突解决策略 | P1 |
| 流程匹配器 | `ProcedureMatcher` | 自定义企业规范匹配逻辑 | P1 |

### 4.2 扩展点注册方式

```java
// 注册自定义完善度评估器
ExtensionPointRegistry registry = ExtensionPointRegistry.getInstance();
registry.register(CompletenessEvaluator.class, new CustomCompletenessEvaluator(), 10);

// 注册自定义冲突解决器
registry.register(FusionConflictResolver.class, new CustomConflictResolver(), "custom-resolver");
```

---

## 五、数据存储规范

### 5.1 目录结构

```
.ooder/
├── procedures/                    # 企业规范流程
│   ├── {procedureId}/
│   │   ├── metadata.yaml         # 元数据
│   │   ├── roles.yaml            # 角色定义
│   │   ├── steps.yaml            # 流程步骤
│   │   ├── rules.yaml            # 约束规则
│   │   └── completeness.yaml     # 完善度评估
│   └── index.yaml                # 索引文件
│
├── fused-templates/               # 融合模板
│   ├── {templateId}/
│   │   ├── metadata.yaml         # 元数据
│   │   ├── fusion.yaml           # 融合信息
│   │   ├── conflicts.yaml        # 冲突记录
│   │   ├── roles.yaml            # 融合后角色
│   │   ├── activation.yaml       # 激活步骤
│   │   ├── menus.yaml            # 菜单配置
│   │   └── visualization.yaml    # 可视化配置
│   └── index.yaml
│
└── scene-groups/                  # 场景组（现有）
    └── ...
```

### 5.2 YAML格式示例

**procedures/{procedureId}/metadata.yaml**:
```yaml
procedureId: proc-001
name: 采购审批流程
category: PROCUREMENT
description: 企业采购审批标准流程
tags:
  - 采购
  - 审批
source: KNOWLEDGE_BASE
status: ACTIVE
completeness: 85
organizationId: org-001
departmentIds:
  - dept-001
  - dept-002
requiredCapabilities:
  - cap-approval
  - cap-notification
knowledgeBaseIds:
  - kb-001
createTime: 2026-03-28T10:00:00Z
updateTime: 2026-03-28T15:30:00Z
author: user-001
version: "1.0"
```

---

## 六、接口调用示例

### 6.1 创建企业规范流程

```java
// MVP层调用示例
EnterpriseProcedureService procedureService = getProcedureService();

EnterpriseProcedureCreateRequest request = new EnterpriseProcedureCreateRequest();
request.setName("采购审批流程");
request.setCategory("PROCUREMENT");
request.setSource(ProcedureSource.MANUAL);
request.setOrganizationId("org-001");
request.setAuthor("user-001");

// 添加角色
List<ProcedureRole> roles = new ArrayList<>();
ProcedureRole applicant = new ProcedureRole();
applicant.setRoleId("APPLICANT");
applicant.setName("申请人");
applicant.setRequired(true);
applicant.setMinCount(1);
applicant.setMaxCount(1);
roles.add(applicant);
request.setRoles(roles);

// 创建
EnterpriseProcedure procedure = procedureService.create(request);
System.out.println("Created procedure: " + procedure.getProcedureId());
```

### 6.2 LLM辅助创建

```java
// MVP层调用示例
List<Document> documents = documentStorageService.listDocuments(
    new DocumentQuery().setCategory("procedure-docs")
);

EnterpriseProcedure procedure = procedureService.llmAssistCreate(documents);
System.out.println("LLM generated procedure: " + procedure.getName());
```

### 6.3 执行融合

```java
// MVP层调用示例
FusionTemplateService fusionService = getFusionTemplateService();

// 匹配企业规范
List<ProcedureMatchResult> matches = fusionService.matchProcedures("skill-001");
if (!matches.isEmpty()) {
    ProcedureMatchResult bestMatch = matches.get(0);
    
    // 创建融合请求
    FusionRequest request = new FusionRequest();
    request.setEnterpriseProcedureId(bestMatch.getProcedureId());
    request.setSkillId("skill-001");
    request.setFusedBy("user-001");
    
    // 设置融合策略
    FusionStrategy strategy = new FusionStrategy();
    strategy.setRolePriority(FusionPriority.ENTERPRISE_FIRST);
    strategy.setActivationStepPriority(FusionPriority.MERGE);
    strategy.setAutoResolveConflict(true);
    request.setFusionStrategy(strategy);
    
    // 执行融合
    FusedWorkflowTemplate template = fusionService.fuse(request);
    System.out.println("Created fused template: " + template.getTemplateId());
}
```

### 6.4 评估完善度

```java
// MVP层调用示例
CompletenessDetail detail = procedureService.evaluateCompleteness("proc-001");
System.out.println("Completeness score: " + detail.getOverallScore());

for (CompletenessIssue issue : detail.getIssues()) {
    System.out.println("Issue: " + issue.getDescription());
    System.out.println("Suggestion: " + issue.getSuggestion());
}
```

---

## 七、验收标准

### 7.1 接口验收

- [ ] `EnterpriseProcedureService` 所有方法可正常调用
- [ ] `FusionTemplateService` 所有方法可正常调用
- [ ] `CompletenessEvaluator` 评估结果正确
- [ ] `EnhancedActivationStepExecutor` 扩展方法可正常调用
- [ ] `FlowVisualizationService` 可生成可视化配置

### 7.2 性能验收

- [ ] 企业规范流程CRUD操作 < 100ms
- [ ] 完善度评估 < 500ms
- [ ] 融合操作 < 1s
- [ ] 匹配计算 < 200ms

### 7.3 兼容性验收

- [ ] 与SE SDK 3.0.0数据模型兼容
- [ ] 现有 `ActivationStepExecutor` 实现可继续使用
- [ ] 现有 `SceneTemplate` 可迁移到新模型

---

## 八、协作流程

### 8.1 开发协作

1. **需求确认**: MVP Team 提出需求，SDK Team 确认技术方案
2. **接口评审**: 双方共同评审接口设计
3. **并行开发**: SDK Team 开发核心功能，MVP Team 开发上层应用
4. **集成测试**: 双方进行集成测试
5. **发布验收**: 共同验收发布

### 8.2 沟通机制

- **周会**: 每周一同步进度，讨论问题
- **文档**: 通过协作文档同步设计变更
- **代码评审**: 关键接口变更需要双方评审

---

## 九、附录

### 9.1 相关文件路径

| 文件 | 绝对路径 |
|------|---------|
| 本协作文档 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_SDK_3.1.0_EXTENSION_COLLABORATION.md` |
| 重构规划文档 | `e:\github\ooder-sdk\scene-engine\docs\refactoring\SE_SDK_3.1.0_REFACTORING_PLAN.md` |
| 需求文档 | `e:\apex\app\docs\requirements\se-sdk-upgrade-proposal.md` |

### 9.2 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-28 | 初始版本 |

---

**文档维护者**: SE SDK Team & MVP Skill Scene Team  
**最后更新**: 2026-03-28
