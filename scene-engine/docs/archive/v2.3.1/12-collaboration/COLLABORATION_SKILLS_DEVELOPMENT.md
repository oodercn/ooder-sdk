# SE SDK v2.3.1 与 Skills 团队协同开发文档

**版本**: 2.3.1  
**发布日期**: 2026-03-22  
**目标读者**: Skills 开发团队  
**协作方**: SE SDK 团队  
**状态**: 🔴 待确认

---

## 一、协作背景

### 1.1 版本目标

SE SDK v2.3.1 版本需要 Skills 团队配合增强场景技能的 `skill.yaml` 配置，确保安装时能够通过配置完整性验证。

### 1.2 协作需求概述

| 需求 | 优先级 | 影响范围 |
|------|--------|----------|
| 场景配置增强 | P0 | 所有 SCENE 类型技能 |
| 激活步骤定义 | P0 | 所有 SCENE 类型技能 |
| 菜单配置完善 | P0 | 所有 SCENE 类型技能 |
| 角色权限定义 | P1 | 多角色场景技能 |

### 1.3 当前状态

```
SE SDK 配置验证已实现:
├── SceneConfigLoader ✅
├── SceneValidationException ✅
├── validateSceneConfig ✅
└── 需要技能配置配合: 5+ 技能待增强
```

---

## 二、验证规则说明

### 2.1 安装时验证流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          技能安装验证流程                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │
│  │ 技能包加载   │───▶│ 类型判断    │───▶│ 场景配置    │───▶│ 配置验证    │ │
│  │             │    │             │    │ 加载        │    │             │ │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘ │
│         │                  │                  │                  │          │
│         ▼                  ▼                  ▼                  ▼          │
│   读取 skill.yaml    type=SCENE?      解析 spec.*       验证完整性         │
│                                             │                  │          │
│                                             └──────────────────┘          │
│                                                      │                      │
│                                                      ▼                      │
│                                            ┌─────────────┐                │
│                                            │ 验证结果    │                │
│                                            └─────────────┘                │
│                                             │            │                 │
│                                             ▼            ▼                 │
│                                          ✅ 通过      ❌ 阻断              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 验证规则详情

| 验证项 | 验证类型 | 触发条件 | 错误级别 | 错误信息 |
|--------|----------|----------|----------|----------|
| 场景配置存在 | SCENE_CONFIG_MISSING | type=SCENE 但无 spec.roles | 🔴 阻断 | 技能包中未定义场景配置 |
| 角色定义存在 | ROLES_MISSING | spec.roles 为空 | 🔴 阻断 | 场景缺少角色定义 |
| 必需角色存在 | REQUIRED_ROLE_MISSING | 无 required=true 角色 | 🔴 阻断 | 场景缺少必需角色 |
| 激活步骤存在 | ACTIVATION_STEPS_MISSING | spec.activationSteps 为空 | 🔴 阻断 | 场景缺少激活步骤 |
| 角色激活步骤 | ROLE_ACTIVATION_STEPS_MISSING | 必需角色无激活步骤 | 🔴 阻断 | 必需角色缺少激活步骤 |
| 菜单配置存在 | MENUS_MISSING | spec.menus 为空 | 🔴 阻断 | 场景缺少菜单配置 |
| 角色菜单配置 | ROLE_MENUS_MISSING | 必需角色无菜单 | 🔴 阻断 | 必需角色缺少菜单 |

---

## 三、skill.yaml 配置规范

### 3.1 完整配置模板

```yaml
# ========================================
# 技能基本信息
# ========================================
name: daily-report                    # 技能ID (必填)
version: 1.0.0                        # 版本号 (必填)
type: SCENE                           # 类型: SCENE 表示场景技能 (必填)
displayName: 日报场景                 # 显示名称
description: 员工日报提交与管理场景    # 描述

# ========================================
# 规格配置 (场景技能必填)
# ========================================
spec:
  # --------------------------------------
  # 能力配置
  # --------------------------------------
  capability:
    category: biz                     # 分类: biz, knowledge, sys, util
    code: daily-report                # 能力代码
  
  # --------------------------------------
  # 场景配置
  # --------------------------------------
  scene:
    type: TRIGGER                     # 场景类型: AUTO, TRIGGER, HYBRID
    visibility: internal              # 可见性: public, internal
    participantMode: multi-role       # 参与者模式: single-user, multi-role
  
  # --------------------------------------
  # 角色配置 (必填)
  # --------------------------------------
  roles:
    - name: MANAGER                   # 角色名称 (必填)
      description: 场景管理员         # 角色描述
      required: true                  # 是否必需角色 (必填)
      minCount: 1                     # 最小人数
      maxCount: 1                     # 最大人数 (0表示无限制)
      permissions:                    # 角色权限
        - scene:manage
        - report:view
        - report:export
    
    - name: EMPLOYEE                  # 角色名称
      description: 普通员工
      required: true
      minCount: 1
      maxCount: 100
      permissions:
        - report:submit
        - report:view.own
  
  # --------------------------------------
  # 激活步骤配置 (必填)
  # --------------------------------------
  activationSteps:
    # 管理员激活步骤
    MANAGER:
      - stepId: confirm-participants  # 步骤ID (必填)
        name: 确认参与者               # 步骤名称 (必填)
        description: 选择参与日报的员工
        type: CONFIRM_PARTICIPANTS    # 步骤类型
        required: true                # 是否必需
        autoExecute: false            # 是否自动执行
        skippable: false              # 是否可跳过
      
      - stepId: select-push-targets
        name: 选择推送目标
        description: 选择接收日报提醒的人员
        type: SELECT_PUSH_TARGETS
        required: true
        autoExecute: false
      
      - stepId: config-conditions
        name: 配置提醒条件
        description: 设置日报提交提醒的时间和条件
        type: CONFIG_CONDITIONS
        required: false
        skippable: true               # 可跳过
      
      - stepId: confirm-activation
        name: 确认激活
        description: 确认场景激活
        type: CONFIRM_ACTIVATION
        required: true
    
    # 员工激活步骤
    EMPLOYEE:
      - stepId: confirm-join
        name: 确认加入
        description: 确认加入日报场景
        type: CONFIRM_JOIN
        required: true
      
      - stepId: config-private-capabilities
        name: 配置私有能力
        description: 配置个人提醒等私有能力
        type: CONFIG_PRIVATE_CAPABILITIES
        required: false
        privateCapabilities:          # 私有能力列表
          - personal-reminder
      
      - stepId: confirm-activation
        name: 确认激活
        type: CONFIRM_ACTIVATION
        required: true
  
  # --------------------------------------
  # 菜单配置 (必填)
  # --------------------------------------
  menus:
    # 管理员菜单
    MANAGER:
      - id: daily-report-dashboard    # 菜单ID (必填)
        name: 日报管理                 # 菜单名称 (必填)
        icon: report                   # 图标
        url: /daily-report/manager     # 路由地址
        order: 1                       # 排序
      
      - id: daily-report-summary
        name: 日报汇总
        icon: summary
        url: /daily-report/summary
        order: 2
      
      - id: daily-report-settings
        name: 场景设置
        icon: settings
        url: /daily-report/settings
        order: 3
    
    # 员工菜单
    EMPLOYEE:
      - id: daily-report-submit
        name: 提交日报
        icon: edit
        url: /daily-report/submit
        order: 1
      
      - id: daily-report-history
        name: 我的日报
        icon: history
        url: /daily-report/history
        order: 2
  
  # --------------------------------------
  # 私有能力配置 (可选)
  # --------------------------------------
  privateCapabilities:
    - capId: personal-reminder
      name: 个人提醒
      description: 自定义提醒时间和方式
      configSchema:
        type: object
        properties:
          reminderTime:
            type: string
            description: 提醒时间
          reminderMethod:
            type: string
            enum: [email, sms, app]
            description: 提醒方式

  # --------------------------------------
  # 依赖配置 (可选)
  # --------------------------------------
  dependencies:
    skills:
      - skillId: skill-notification
        version: ">=1.0.0"
        required: true
    services:
      - serviceId: storage-service
        required: true

  # --------------------------------------
  # UI 技能配置 (可选)
  # --------------------------------------
  uiSkills:
    - skillId: daily-report-ui
      entryPoint: pages/index.html
      config:
        theme: default
```

### 3.2 字段说明

#### 3.2.1 基本信息字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | ✅ | 技能唯一标识 |
| version | String | ✅ | 版本号，遵循语义化版本 |
| type | String | ✅ | 类型，场景技能必须为 SCENE |
| displayName | String | ❌ | 显示名称 |
| description | String | ❌ | 描述 |

#### 3.2.2 角色配置字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | ✅ | 角色名称，大写字母和下划线 |
| description | String | ❌ | 角色描述 |
| required | Boolean | ✅ | 是否必需角色 |
| minCount | Integer | ❌ | 最小人数，默认 1 |
| maxCount | Integer | ❌ | 最大人数，0 表示无限制 |
| permissions | List | ❌ | 权限列表 |

#### 3.2.3 激活步骤字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| stepId | String | ✅ | 步骤唯一标识 |
| name | String | ✅ | 步骤名称 |
| description | String | ❌ | 步骤描述 |
| type | String | ❌ | 步骤类型 |
| required | Boolean | ❌ | 是否必需，默认 true |
| autoExecute | Boolean | ❌ | 是否自动执行，默认 false |
| skippable | Boolean | ❌ | 是否可跳过，默认 false |
| privateCapabilities | List | ❌ | 私有能力列表 |

#### 3.2.4 菜单配置字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | ✅ | 菜单唯一标识 |
| name | String | ✅ | 菜单名称 |
| icon | String | ❌ | 图标名称 |
| url | String | ❌ | 路由地址 |
| order | Integer | ❌ | 排序，默认 0 |
| visible | Boolean | ❌ | 是否可见，默认 true |

---

## 四、任务清单

### 4.1 P0 级任务 (关键)

#### 任务 SKILLS-001: 日报场景配置增强

**优先级**: 🔴 P0  
**预计工时**: 1天  
**技能ID**: daily-report

**任务描述**:
为日报场景技能添加完整的 skill.yaml 配置。

**验收标准**:
- [ ] 包含 spec.roles 配置
- [ ] 包含 spec.activationSteps 配置
- [ ] 包含 spec.menus 配置
- [ ] 通过 SE SDK 安装验证

---

#### 任务 SKILLS-002: 审批场景配置增强

**优先级**: 🔴 P0  
**预计工时**: 1天  
**技能ID**: skill-approval-form

**任务描述**:
为审批场景技能添加完整的 skill.yaml 配置。

**验收标准**:
- [ ] 包含 spec.roles 配置
- [ ] 包含 spec.activationSteps 配置
- [ ] 包含 spec.menus 配置
- [ ] 通过 SE SDK 安装验证

---

#### 任务 SKILLS-003: 协作场景配置增强

**优先级**: 🔴 P0  
**预计工时**: 1天  
**技能ID**: skill-collaboration

**任务描述**:
为协作场景技能添加完整的 skill.yaml 配置。

**验收标准**:
- [ ] 包含 spec.roles 配置
- [ ] 包含 spec.activationSteps 配置
- [ ] 包含 spec.menus 配置
- [ ] 通过 SE SDK 安装验证

---

### 4.2 P1 级任务 (重要)

#### 任务 SKILLS-004: 招聘场景配置增强

**优先级**: 🟡 P1  
**预计工时**: 1天  
**技能ID**: skill-recruitment-management

**任务描述**:
为招聘场景技能添加完整的 skill.yaml 配置。

---

#### 任务 SKILLS-005: 知识管理场景配置增强

**优先级**: 🟡 P1  
**预计工时**: 1天  
**技能ID**: skill-knowledge-management

**任务描述**:
为知识管理场景技能添加完整的 skill.yaml 配置。

---

### 4.3 需要增强的技能汇总

| 技能ID | 分类 | 安装类型 | 优先级 | 当前状态 |
|--------|------|----------|--------|----------|
| daily-report | biz | SCENE | 🔴 P0 | 待增强 |
| skill-approval-form | biz | SCENE | 🔴 P0 | 待增强 |
| skill-collaboration | biz | SCENE | 🔴 P0 | 待增强 |
| skill-recruitment-management | biz | SCENE | 🟡 P1 | 待增强 |
| skill-knowledge-management | knowledge | SCENE | 🟡 P1 | 待增强 |

---

## 五、场景类型说明

### 5.1 SceneType 枚举

| 类型 | 说明 | 自驱动 | 可触发 | 适用场景 |
|------|------|--------|--------|----------|
| AUTO | 自主场景 | ✅ | ❌ | 智能助手、监控告警、自动化流程 |
| TRIGGER | 触发场景 | ❌ | ✅ | 审批流程、报表生成、工具服务 |
| HYBRID | 混合场景 | ✅ | ✅ | 智能客服、工作流引擎 |

### 5.2 类型选择指南

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          场景类型选择决策树                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                        ┌─────────────────┐                                 │
│                        │ 是否需要定时执行？│                                 │
│                        └────────┬────────┘                                 │
│                                 │                                           │
│                    ┌────────────┴────────────┐                             │
│                    │                         │                             │
│                    ▼                         ▼                             │
│                 ┌──────┐                 ┌──────┐                         │
│                 │ 是   │                 │ 否   │                         │
│                 └──┬───┘                 └──┬───┘                         │
│                    │                         │                             │
│                    ▼                         ▼                             │
│            ┌───────────────┐        ┌───────────────┐                    │
│            │ 是否需要人工   │        │ 是否需要人工   │                    │
│            │ 触发？        │        │ 触发？        │                    │
│            └───────┬───────┘        └───────┬───────┘                    │
│                    │                         │                             │
│         ┌──────────┴──────────┐   ┌──────────┴──────────┐               │
│         │                     │   │                     │               │
│         ▼                     ▼   ▼                     ▼               │
│      ┌──────┐            ┌──────┐ ┌──────┐          ┌──────┐           │
│      │HYBRID│            │ AUTO │ │TRIGGER│          │TRIGGER│           │
│      └──────┘            └──────┘ └──────┘          └──────┘           │
│                                                                             │
│  示例:                      示例:       示例:            示例:            │
│  • 智能客服                • 监控告警  • 审批流程      • 报表生成         │
│  • 工作流引擎              • 自动备份  • 表单提交      • 数据导出         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、激活步骤类型参考

### 6.1 标准步骤类型

| 步骤类型 | 说明 | 适用角色 |
|---------|------|----------|
| CONFIRM_PARTICIPANTS | 确认参与者 | 管理员 |
| SELECT_PUSH_TARGETS | 选择推送目标 | 管理员 |
| CONFIG_CONDITIONS | 配置条件 | 管理员 |
| CONFIRM_ACTIVATION | 确认激活 | 所有角色 |
| CONFIRM_JOIN | 确认加入 | 员工 |
| CONFIG_PRIVATE_CAPABILITIES | 配置私有能力 | 员工 |
| CONFIG_SKILL | 配置技能 | 独立用户 |
| VERIFY_CONFIG | 验证配置 | 独立用户 |
| ENABLE_FEATURES | 启用功能 | 独立用户 |

### 6.2 自定义步骤类型

Skills 团队可以定义自定义步骤类型，但需要：

1. 在 skill.yaml 中明确定义
2. 提供对应的 ActivationStepExecutor 实现
3. 注册到 ExtensionPointRegistry

```java
/**
 * 自定义激活步骤执行器示例
 */
@Component
public class CustomReportConfigExecutor implements ActivationStepExecutor {
    
    @Override
    public boolean canExecute(ActivationStepConfig stepConfig) {
        return "CUSTOM_REPORT_CONFIG".equals(stepConfig.getStepType());
    }
    
    @Override
    public StepResult execute(ActivationStepConfig stepConfig, 
                               ActivationProcess process, 
                               Map<String, Object> context) {
        StepResult result = new StepResult();
        result.setStepId(stepConfig.getStepId());
        
        // 自定义逻辑
        Map<String, Object> config = stepConfig.getConfig();
        // ...
        
        result.setSuccess(true);
        return result;
    }
}
```

---

## 七、验证与测试

### 7.1 本地验证方法

```bash
# 使用 SE SDK 提供的验证工具
java -jar scene-engine-tools.jar validate-skill \
  --skill-path /path/to/skill \
  --skill-id daily-report
```

### 7.2 验证输出示例

**成功输出**:
```
[INFO] Validating skill: daily-report
[INFO] Loading skill.yaml...
[INFO] Parsing spec configuration...
[INFO] Validating roles...
[INFO]   ✓ Found 2 roles
[INFO]   ✓ Required roles defined: MANAGER, EMPLOYEE
[INFO] Validating activation steps...
[INFO]   ✓ Found activation steps for MANAGER (4 steps)
[INFO]   ✓ Found activation steps for EMPLOYEE (3 steps)
[INFO] Validating menus...
[INFO]   ✓ Found menus for MANAGER (3 items)
[INFO]   ✓ Found menus for EMPLOYEE (2 items)
[SUCCESS] Skill validation passed: daily-report
```

**失败输出**:
```
[ERROR] Validating skill: daily-report
[ERROR] Loading skill.yaml...
[ERROR] Parsing spec configuration...
[ERROR] Validating roles...
[ERROR]   ✗ No roles defined
[FAILED] [ROLES_MISSING] 场景缺少角色定义: 请在 skill.yaml 的 spec.roles 中定义场景角色
```

---

## 八、里程碑

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           开发里程碑                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Week 1                          Week 2                                    │
│  ┌─────────────────────────┐    ┌─────────────────────────┐               │
│  │ P0 技能配置增强          │    │ P1 技能配置增强          │               │
│  │                         │    │                         │               │
│  │ • daily-report          │    │ • skill-recruitment     │               │
│  │ • skill-approval-form   │    │ • skill-knowledge       │               │
│  │ • skill-collaboration   │    │                         │               │
│  └─────────────────────────┘    └─────────────────────────┘               │
│             │                              │                                │
│             ▼                              ▼                                │
│        验证通过                        验证通过                              │
│                                                                             │
│  目标: 60% (3/5)                 目标: 100% (5/5)                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 九、联系人

| 角色 | 联系人 | 联系方式 |
|------|--------|----------|
| SE SDK 负责人 | - | - |
| Skills 团队负责人 | - | - |
| 配置评审 | - | - |

---

## 十、参考文档

- [SE SDK 覆盖度报告](./SCENE_LIFECYCLE_COVERAGE_V4.md)
- [SDK 协作说明](./SDK_COLLABORATION_GUIDE.md)
- [skill.yaml 配置规范](./skill-yaml-spec.md)

---

*文档版本: 1.0*  
*创建日期: 2026-03-22*  
*SE SDK 团队*
