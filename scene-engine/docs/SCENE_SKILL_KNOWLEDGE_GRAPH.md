# 场景技能系统 - 完整知识图谱

> 基于9个详细设计文档的综合分析

## 一、核心实体图谱 (Entity Graph)

### 1.1 主要实体定义

```yaml
# 场景技能 (SceneSkill) - 核心实体
SceneSkill:
  属性:
    - skillId: String                    # 技能唯一标识
    - name: String                       # 技能名称
    - version: String                    # 版本号
    - type: SceneType                    # 场景类型枚举
    - description: String                # 技能描述
    - icon: String                       # 图标URL
    - provider: String                   # 提供方
    - minEngineVersion: String           # 最低引擎版本要求
  状态:
    - DISCOVERED                        # 已发现
    - PREVIEWING                        # 预览中
    - CONFIGURING                       # 配置中
    - DEP_CHECKING                      # 依赖检查中
    - DEP_CONFIRMING                    # 依赖确认中
    - INSTALLING                        # 安装中
    - INSTALLED                         # 已安装
    - ACTIVATING                        # 激活中
    - ACTIVE                            # 已激活
    - FAILED                            # 失败
    - ROLLED_BACK                       # 已回滚

# 场景技能配置 (SceneSkillConfig) - 配置实体
SceneSkillConfig:
  属性:
    - templateId: String                 # 模板ID
    - version: String                    # 配置版本
  包含:
    - dependencies: DependencyConfig     # 依赖配置
    - roles: List<RoleConfig>            # 角色配置列表
    - activationSteps: Map<Role, List<ActivationStepConfig>>  # 激活步骤
    - menus: List<MenuConfig>            # 菜单配置
    - uiSkills: List<UISkillConfig>      # UI技能配置
    - privateCapabilities: List<CapabilityConfig>  # 私有能力配置

# 依赖配置 (DependencyConfig)
DependencyConfig:
  属性:
    required: List<SkillDependency>      # 必需依赖
    optional: List<SkillDependency>      # 可选依赖
  SkillDependency:
    - skillId: String                    # 依赖技能ID
    - version: String                    # 版本要求
    - autoInstall: boolean               # 是否自动安装
    - reason: String                     # 依赖原因说明

# 角色配置 (RoleConfig)
RoleConfig:
  属性:
    - roleId: String                     # 角色ID
    - roleName: String                   # 角色名称
    - description: String                # 角色描述
    - permissions: List<String>          # 权限列表
    - isMandatory: boolean               # 是否必须有人承担
    - minParticipants: int               # 最小参与者数
    - maxParticipants: int               # 最大参与者数

# 激活步骤配置 (ActivationStepConfig)
ActivationStepConfig:
  属性:
    - stepId: String                     # 步骤ID
    - name: String                       # 步骤名称
    - description: String                # 步骤描述
    - required: boolean                  # 是否必需
    - uiComponent: String                # UI组件标识
    - validationRules: List<Rule>        # 验证规则
    - defaultValue: Object               # 默认值

# 菜单配置 (MenuConfig)
MenuConfig:
  属性:
    - menuId: String                     # 菜单ID
    - name: String                       # 菜单名称
    - icon: String                       # 图标
    - path: String                       # 路由路径
    - component: String                  # 组件名
    - roles: List<String>                # 可见角色
    - order: int                         # 排序
    - parentId: String                   # 父菜单ID

# UI技能配置 (UISkillConfig)
UISkillConfig:
  属性:
    - skillId: String                    # UI技能ID
    - name: String                       # 名称
    - type: UISkillType                  # 类型 (WIDGET/TOOLBAR/PANEL)
    - entryPoint: String                 # 入口组件
    - props: Map<String, Object>         # 默认属性
    - roles: List<String>                # 适用角色

# 私有能力配置 (CapabilityConfig)
CapabilityConfig:
  属性:
    - capabilityId: String               # 能力ID
    - name: String                       # 能力名称
    - description: String                # 能力描述
    - configSchema: JSONSchema           # 配置Schema
    - defaultConfig: Map                 # 默认配置
    - roles: List<String>                # 适用角色

# 场景实例 (SceneInstance)
SceneInstance:
  属性:
    - sceneId: String                    # 场景实例ID
    - templateId: String                 # 模板ID
    - name: String                       # 场景名称
    - status: SceneStatus                # 场景状态
    - createdBy: String                  # 创建者
    - createdAt: DateTime                # 创建时间
    - activatedAt: DateTime              # 激活时间
  包含:
    - participants: List<Participant>    # 参与者列表
    - installedSkills: List<InstalledSkill>  # 已安装技能
    - runtimeConfig: Map                 # 运行时配置

# 参与者 (Participant)
Participant:
  属性:
    - userId: String                     # 用户ID
    - roleId: String                     # 角色ID
    - joinedAt: DateTime                 # 加入时间
    - status: ParticipantStatus          # 参与状态
    - privateCapabilities: Map           # 私有能力配置

# 安装技能 (InstalledSkill)
InstalledSkill:
  属性:
    - skillId: String                    # 技能ID
    - version: String                    # 版本
    - installedAt: DateTime              # 安装时间
    - status: SkillStatus                # 技能状态
    - config: Map                        # 技能配置

# 激活流程 (ActivationProcess)
ActivationProcess:
  属性:
    - processId: String                  # 流程ID
    - sceneId: String                    # 场景ID
    - userId: String                     # 用户ID
    - roleId: String                     # 角色ID
    - type: ActivationType               # 激活类型
    - status: ProcessStatus              # 流程状态
    - currentStep: String                # 当前步骤
    - completedSteps: List<String>       # 已完成步骤
    - stepData: Map                      # 步骤数据
    - startedAt: DateTime                # 开始时间
    - completedAt: DateTime              # 完成时间

# 菜单项 (MenuItem)
MenuItem:
  属性:
    - itemId: String                     # 菜单项ID
    - name: String                       # 名称
    - icon: String                       # 图标
    - path: String                       # 路径
    - component: String                  # 组件
    - children: List<MenuItem>           # 子菜单
    - metadata: Map                      # 元数据

# 用户菜单 (UserMenu)
UserMenu:
  属性:
    - userId: String                     # 用户ID
    - sceneId: String                    # 场景ID
    - items: List<MenuItem>              # 菜单项列表
    - generatedAt: DateTime              # 生成时间
    - version: String                    # 版本

# 推送目标 (PushTarget)
PushTarget:
  属性:
    - targetId: String                   # 目标ID
    - type: PushTargetType               # 类型 (USER/ROLE/GROUP)
    - targetRef: String                  # 目标引用
    - channel: PushChannel               # 推送渠道
    - status: PushStatus                 # 推送状态

# 提醒配置 (ReminderConfig)
ReminderConfig:
  属性:
    - reminderId: String                 # 提醒ID
    - name: String                       # 提醒名称
    - triggerCondition: TriggerCondition # 触发条件
    - notifyChannels: List<Channel>      # 通知渠道
    - template: String                   # 消息模板
    - enabled: boolean                   # 是否启用
```

## 二、关系图谱 (Relationship Graph)

### 2.1 核心关系定义

```yaml
# 关系类型定义
关系:
  # 1. 配置关系
  SceneSkillConfig_contains_DependencyConfig:
    类型: 组合关系
    基数: 1:1
    说明: 场景技能配置包含依赖配置
    
  SceneSkillConfig_contains_RoleConfig:
    类型: 组合关系
    基数: 1:N
    说明: 场景技能配置包含多个角色配置
    
  SceneSkillConfig_contains_ActivationStepConfig:
    类型: 组合关系
    基数: 1:N (按角色分组)
    说明: 场景技能配置按角色包含激活步骤
    
  SceneSkillConfig_contains_MenuConfig:
    类型: 组合关系
    基数: 1:N
    说明: 场景技能配置包含菜单配置
    
  SceneSkillConfig_contains_UISkillConfig:
    类型: 组合关系
    基数: 1:N
    说明: 场景技能配置包含UI技能配置
    
  SceneSkillConfig_contains_CapabilityConfig:
    类型: 组合关系
    基数: 1:N
    说明: 场景技能配置包含私有能力配置

  # 2. 实例关系
  SceneInstance_has_Participant:
    类型: 聚合关系
    基数: 1:N
    说明: 场景实例包含多个参与者
    
  SceneInstance_has_InstalledSkill:
    类型: 聚合关系
    基数: 1:N
    说明: 场景实例包含多个已安装技能
    
  Participant_has_PrivateCapability:
    类型: 关联关系
    基数: 1:N
    说明: 参与者拥有私有能力配置

  # 3. 激活关系
  ActivationProcess_belongsTo_SceneInstance:
    类型: 关联关系
    基数: N:1
    说明: 激活流程属于场景实例
    
  ActivationProcess_belongsTo_Participant:
    类型: 关联关系
    基数: N:1
    说明: 激活流程属于参与者
    
  ActivationProcess_follows_ActivationStepConfig:
    类型: 遵循关系
    基数: 1:N
    说明: 激活流程遵循激活步骤配置

  # 4. 菜单关系
  UserMenu_belongsTo_Participant:
    类型: 关联关系
    基数: 1:1
    说明: 用户菜单属于参与者
    
  UserMenu_composedOf_MenuItem:
    类型: 组合关系
    基数: 1:N
    说明: 用户菜单由菜单项组成
    
  MenuItem_derivedFrom_MenuConfig:
    类型: 派生关系
    基数: N:1
    说明: 菜单项派生自菜单配置
    
  MenuItem_derivedFrom_UISkillConfig:
    类型: 派生关系
    基数: N:1
    说明: 菜单项派生自UI技能配置

  # 5. 推送关系
  PushTarget_targets_Participant:
    类型: 关联关系
    基数: N:1
    说明: 推送目标指向参与者
    
  PushTarget_derivedFrom_ActivationProcess:
    类型: 派生关系
    基数: N:1
    说明: 推送目标派生自激活流程

  # 6. 提醒关系
  ReminderConfig_belongsTo_SceneInstance:
    类型: 关联关系
    基数: N:1
    说明: 提醒配置属于场景实例
    
  ReminderConfig_triggers_by_Condition:
    类型: 触发关系
    基数: 1:1
    说明: 提醒配置由条件触发

  # 7. 依赖关系
  SkillDependency_dependsOn_SceneSkill:
    类型: 依赖关系
    基数: N:1
    说明: 技能依赖指向被依赖技能
    
  InstalledSkill_satisfies_SkillDependency:
    类型: 满足关系
    基数: 1:1
    说明: 已安装技能满足技能依赖

  # 8. 角色关系
  RoleConfig_defines_ParticipantRole:
    类型: 定义关系
    基数: 1:N
    说明: 角色配置定义参与者角色
    
  ActivationStepConfig_assignedTo_RoleConfig:
    类型: 分配关系
    基数: N:1
    说明: 激活步骤配置分配给角色配置
    
  MenuConfig_visibleTo_RoleConfig:
    类型: 可见关系
    基数: N:N
    说明: 菜单配置对角色可见
    
  UISkillConfig_availableTo_RoleConfig:
    类型: 可用关系
    基数: N:N
    说明: UI技能配置对角色可用
    
  CapabilityConfig_boundTo_RoleConfig:
    类型: 绑定关系
    基数: N:N
    说明: 私有能力配置绑定到角色
```

### 2.2 关系图谱可视化

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           场景技能系统知识图谱                               │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐         contains         ┌─────────────────┐
│ SceneSkillConfig│◄────────────────────────►│ DependencyConfig│
└────────┬────────┘                          └─────────────────┘
         │
         │ contains      ┌──────────────────────────────────────────┐
         ├──────────────►│               RoleConfig                  │
         │               │  - roleId, roleName, description          │
         │               │  - permissions, isMandatory               │
         │               │  - minParticipants, maxParticipants       │
         │               └──────────────────────────────────────────┘
         │                               ▲
         │                               │ assignedTo
         │                               │
         │ contains               ┌──────┴──────┐
         ├───────────────────────►│ActivationStep│
         │                        │   Config     │
         │                        └─────────────┘
         │
         │ contains               ┌─────────────┐
         ├───────────────────────►│  MenuConfig  │
         │                        └──────┬──────┘
         │                               │
         │                               │ visibleTo
         │                               ▼
         │ contains               ┌─────────────┐      derives      ┌─────────┐
         ├───────────────────────►│ UISkillConfig│◄─────────────────│ MenuItem│
         │                        └─────────────┘                  └────┬────┘
         │                                                             │
         │ contains                                                     │ composes
         ├───────────────────────►┌─────────────────┐◄─────────────────┘
         │                        │ CapabilityConfig │
         │                        └─────────────────┘
         │
         │ instantiates
         ▼
┌─────────────────┐         has           ┌─────────────────┐
│  SceneInstance  │◄─────────────────────►│  Participant    │
│  - sceneId      │                       │  - userId       │
│  - status       │                       │  - roleId       │
│  - createdBy    │                       │  - status       │
└────────┬────────┘                       └────────┬────────┘
         │                                         │
         │ has                                       │ has
         │                                           │
         ▼                                           ▼
┌─────────────────┐                       ┌─────────────────┐
│ InstalledSkill  │                       │ PrivateCapability│
│  - skillId      │                       │  - capabilityId  │
│  - version      │                       │  - config       │
│  - status       │                       └─────────────────┘
└─────────────────┘
         ▲
         │ satisfies
         │
┌─────────────────┐
│ SkillDependency │
│  - skillId      │
│  - version      │
│  - autoInstall  │
└─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              激活流程子图                                    │
└─────────────────────────────────────────────────────────────────────────────┘

┌───────────────────┐
│ ActivationProcess │
│  - processId      │
│  - type           │
│  - status         │
│  - currentStep    │
└─────────┬─────────┘
          │
          ├──────────────►┌─────────────┐
          │ belongsTo     │SceneInstance│
          │               └─────────────┘
          │
          ├──────────────►┌─────────────┐
          │ belongsTo     │ Participant │
          │               └─────────────┘
          │
          ├──────────────►┌─────────────────┐
          │ generates     │   PushTarget    │
          │               │  - targetId     │
          │               │  - type         │
          │               │  - channel      │
          │               └─────────────────┘
          │
          └──────────────►┌─────────────────┐
            completes     │   UserMenu      │
                          │  - userId       │
                          │  - items[]      │
                          └─────────────────┘
```

## 三、流程图谱 (Process Graph)

### 3.1 安装状态机

```
                    ┌─────────────┐
         ┌─────────►│ DISCOVERED  │◄────────┐
         │          │  (已发现)    │         │
         │          └──────┬──────┘         │
         │                 │ preview         │
         │                 ▼                 │
         │          ┌─────────────┐          │
         │          │ PREVIEWING  │          │
         │          │  (预览中)    │          │
         │          └──────┬──────┘          │
         │                 │ configure       │
         │                 ▼                 │
         │          ┌─────────────┐          │
         │          │ CONFIGURING │          │
         │          │  (配置中)    │          │
         │          └──────┬──────┘          │
         │                 │ check_deps      │
         │                 ▼                 │
         │          ┌─────────────┐          │
         │          │ DEP_CHECKING│          │
         │          │(依赖检查中)  │          │
         │          └──────┬──────┘          │
         │                 │ confirm         │
         │                 ▼                 │
         │          ┌─────────────┐          │
         │          │DEP_CONFIRMING│         │
         │          │(依赖确认中)  │          │
         │          └──────┬──────┘          │
         │                 │ install         │
         │                 ▼                 │
         │          ┌─────────────┐          │
         │          │ INSTALLING  │          │
         │          │  (安装中)    │          │
         │          └──────┬──────┘          │
         │                 │ complete        │
         │                 ▼                 │
         │          ┌─────────────┐          │
         │    ┌────►│  INSTALLED  │◄────┐    │
         │    │     │  (已安装)    │     │    │
         │    │     └──────┬──────┘     │    │
         │    │            │ activate   │    │
         │    │            ▼            │    │
         │    │     ┌─────────────┐     │    │
         │    │     │  ACTIVATING │     │    │
         │    │     │  (激活中)    │     │    │
         │    │     └──────┬──────┘     │    │
         │    │            │ complete   │    │
         │    │            ▼            │    │
         │    │     ┌─────────────┐     │    │
         └────┴────►│    ACTIVE   │◄────┴────┘
                    │  (已激活)    │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            ▼            ▼
        ┌─────────┐  ┌─────────┐  ┌───────────┐
        │  FAILED │  │ROLLED_BACK│ │ deactivate│
        └─────────┘  └─────────┘  └─────┬─────┘
                                        │
                                        ▼
                                   ┌─────────┐
                                   │ INACTIVE│
                                   └─────────┘
```

### 3.2 领导激活流程 (6步)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          领导激活流程 (MANAGER)                              │
└─────────────────────────────────────────────────────────────────────────────┘

  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
  │   START     │────►│  Step 1     │────►│  Step 2     │
  └─────────────┘     │confirm-     │     │select-push- │
                      │participants │     │targets      │
                      └──────┬──────┘     └──────┬──────┘
                             │                   │
                             ▼                   ▼
                      ┌─────────────┐     ┌─────────────┐
                      │ 选择参与者   │     │ 选择推送目标 │
                      │ - 查看角色   │     │ - 部门员工   │
                      │ - 邀请成员   │     │ - 邮件推送   │
                      │ - 确认名单   │     │ - IM推送     │
                      └─────────────┘     └─────────────┘

  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
  │  Step 3     │◄────│  Step 4     │◄────│  Step 5     │
  │config-      │     │   get-key   │     │confirm-     │
  │conditions   │     │  (可选)      │     │activation   │
  └──────┬──────┘     └──────┬──────┘     └──────┬──────┘
         │                   │                   │
         ▼                   ▼                   ▼
  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
  │ 配置驱动条件 │     │ 获取KEY     │     │ 确认激活     │
  │ - 触发条件   │     │ - 申请APIKey│     │ - 汇总信息   │
  │ - 执行规则   │     │ - 配置密钥   │     │ - 最终确认   │
  │ - 提醒设置   │     │ - 安全存储   │     │ - 激活场景   │
  └─────────────┘     └─────────────┘     └──────┬──────┘
                                                 │
                                                 ▼
                                          ┌─────────────┐
                                          │  Step 6     │
                                          │network-     │
                                          │actions      │
                                          └──────┬──────┘
                                                 │
                                                 ▼
                                          ┌─────────────┐
                                          │ 入网动作     │
                                          │ - 注册菜单   │
                                          │ - 绑定能力   │
                                          │ - 通知成员   │
                                          └──────┬──────┘
                                                 │
                                                 ▼
                                          ┌─────────────┐
                                          │    END      │
                                          │  (激活完成)  │
                                          └─────────────┘
```

### 3.3 员工激活流程 (3步)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          员工激活流程 (EMPLOYEE)                             │
└─────────────────────────────────────────────────────────────────────────────┘

  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
  │   START     │────►│  Step 1     │────►│  Step 2     │────►│  Step 3     │
  │  (接收邀请)  │     │confirm-join │     │config-      │     │confirm-     │
  └─────────────┘     │             │     │private-     │     │activation   │
                      └──────┬──────┘     │capabilities │     └──────┬──────┘
                             │            │  (可选)      │            │
                             ▼            └──────┬──────┘            ▼
                      ┌─────────────┐            │            ┌─────────────┐
                      │ 确认加入场景 │            │            │ 确认激活     │
                      │ - 查看场景   │            ▼            │ - 确认配置   │
                      │ - 了解角色   │     ┌─────────────┐     │ - 激活完成   │
                      │ - 接受邀请   │     │ 配置私有能力 │     │ - 获取菜单   │
                      └─────────────┘     │ - 邮件设置   │     └──────┬──────┘
                                          │ - GIT配置    │            │
                                          │ - 个性化     │            ▼
                                          └─────────────┘     ┌─────────────┐
                                                                │    END      │
                                                                │  (激活完成)  │
                                                                └─────────────┘
```

### 3.4 独立技能激活流程 (4步)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        独立技能激活流程 (STANDALONE)                         │
└─────────────────────────────────────────────────────────────────────────────┘

  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
  │   START     │────►│  Step 1     │────►│  Step 2     │────►│  Step 3     │
  └─────────────┘     │config-skill │     │verify-config│     │confirm-     │
                      │             │     │             │     │activation   │
                      └──────┬──────┘     └──────┬──────┘     └──────┬──────┘
                             │                   │                   │
                             ▼                   ▼                   ▼
                      ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
                      │ 配置技能参数 │     │ 验证配置     │     │ 确认激活     │
                      │ - 基础设置   │     │ - 参数校验   │     │ - 最终确认   │
                      │ - 高级选项   │     │ - 连接测试   │     │ - 激活执行   │
                      └─────────────┘     └─────────────┘     └──────┬──────┘
                                                                     │
                                                                     ▼
                                                              ┌─────────────┐
                                                              │  Step 4     │
                                                              │enable-      │
                                                              │features     │
                                                              └──────┬──────┘
                                                                     │
                                                                     ▼
                                                              ┌─────────────┐
                                                              │ 启用功能     │
                                                              │ - 注册服务   │
                                                              │ - 启动任务   │
                                                              │ - 通知用户   │
                                                              └──────┬──────┘
                                                                     │
                                                                     ▼
                                                              ┌─────────────┐
                                                              │    END      │
                                                              │  (激活完成)  │
                                                              └─────────────┘
```

## 四、配置图谱 (Configuration Graph)

### 4.1 完整配置结构

```yaml
# 场景技能完整配置示例
sceneSkillConfig:
  # ========== 基础信息 ==========
  templateId: "daily-report-scene"
  version: "1.0.0"
  name: "日报场景"
  description: "团队日报收集与汇总场景"
  
  # ========== 依赖配置 ==========
  dependencies:
    required:
      - skillId: "mqtt-push"
        version: ">=1.0.0"
        autoInstall: true
        reason: "需要推送通知能力"
      - skillId: "email-service"
        version: ">=1.2.0"
        autoInstall: true
        reason: "需要邮件发送能力"
    optional:
      - skillId: "git-integration"
        version: ">=1.0.0"
        autoInstall: false
        reason: "可选代码提交汇总"
  
  # ========== 角色配置 ==========
  roles:
    - roleId: "MANAGER"
      roleName: "部门负责人"
      description: "负责场景激活和成员管理"
      permissions: ["ACTIVATE", "MANAGE_MEMBERS", "VIEW_REPORTS"]
      isMandatory: true
      minParticipants: 1
      maxParticipants: 1
      
    - roleId: "EMPLOYEE"
      roleName: "部门员工"
      description: "参与日报提交"
      permissions: ["SUBMIT_REPORT", "VIEW_OWN"]
      isMandatory: true
      minParticipants: 1
      maxParticipants: 100
      
    - roleId: "OBSERVER"
      roleName: "观察员"
      description: "只读访问"
      permissions: ["VIEW_REPORTS"]
      isMandatory: false
      minParticipants: 0
      maxParticipants: 10
  
  # ========== 激活步骤配置 ==========
  activationSteps:
    MANAGER:
      - stepId: "confirm-participants"
        name: "确认参与者"
        description: "确认场景参与者名单"
        required: true
        uiComponent: "ParticipantSelector"
        validationRules:
          - type: "MIN_COUNT"
            value: 2
            message: "至少需要2名参与者"
            
      - stepId: "select-push-targets"
        name: "选择推送目标"
        description: "选择提醒推送目标"
        required: true
        uiComponent: "PushTargetSelector"
        
      - stepId: "config-conditions"
        name: "配置驱动条件"
        description: "配置日报触发条件"
        required: true
        uiComponent: "ConditionConfigurator"
        defaultValue:
          triggerTime: "18:00"
          remindBefore: 30
          
      - stepId: "get-key"
        name: "获取KEY"
        description: "申请API密钥"
        required: false
        uiComponent: "KeyApplication"
        
      - stepId: "confirm-activation"
        name: "确认激活"
        description: "确认场景激活"
        required: true
        uiComponent: "ActivationConfirm"
        
      - stepId: "network-actions"
        name: "入网动作"
        description: "执行入网配置"
        required: true
        uiComponent: "NetworkActionExecutor"
        
    EMPLOYEE:
      - stepId: "confirm-join"
        name: "确认加入场景"
        description: "确认加入日报场景"
        required: true
        uiComponent: "JoinConfirmation"
        
      - stepId: "config-private-capabilities"
        name: "配置私有能力"
        description: "配置个人工具"
        required: false
        uiComponent: "CapabilityConfigurator"
        
      - stepId: "confirm-activation"
        name: "确认激活"
        description: "确认激活"
        required: true
        uiComponent: "ActivationConfirm"
  
  # ========== 菜单配置 ==========
  menus:
    - menuId: "daily-report-submit"
      name: "提交日报"
      icon: "EditOutlined"
      path: "/scene/{sceneId}/report/submit"
      component: "DailyReportSubmit"
      roles: ["EMPLOYEE"]
      order: 1
      
    - menuId: "daily-report-list"
      name: "日报列表"
      icon: "FileTextOutlined"
      path: "/scene/{sceneId}/report/list"
      component: "DailyReportList"
      roles: ["MANAGER", "EMPLOYEE"]
      order: 2
      
    - menuId: "daily-report-summary"
      name: "日报汇总"
      icon: "BarChartOutlined"
      path: "/scene/{sceneId}/report/summary"
      component: "DailyReportSummary"
      roles: ["MANAGER"]
      order: 3
      parentId: "daily-report-list"
      
    - menuId: "scene-settings"
      name: "场景设置"
      icon: "SettingOutlined"
      path: "/scene/{sceneId}/settings"
      component: "SceneSettings"
      roles: ["MANAGER"]
      order: 10
  
  # ========== UI技能配置 ==========
  uiSkills:
    - skillId: "report-assistant"
      name: "日报助手"
      type: "WIDGET"
      entryPoint: "ReportAssistantWidget"
      props:
        position: "right"
        width: 320
      roles: ["EMPLOYEE"]
      
    - skillId: "quick-submit-toolbar"
      name: "快速提交工具栏"
      type: "TOOLBAR"
      entryPoint: "QuickSubmitToolbar"
      props:
        position: "top"
      roles: ["EMPLOYEE"]
      
    - skillId: "summary-panel"
      name: "汇总面板"
      type: "PANEL"
      entryPoint: "SummaryPanel"
      props:
        collapsible: true
      roles: ["MANAGER"]
  
  # ========== 私有能力配置 ==========
  privateCapabilities:
    - capabilityId: "email-integration"
      name: "邮件集成"
      description: "个人邮件账户集成"
      configSchema:
        type: "object"
        properties:
          email:
            type: "string"
            format: "email"
          smtpServer:
            type: "string"
          smtpPort:
            type: "integer"
      defaultConfig:
        smtpPort: 587
      roles: ["EMPLOYEE"]
      
    - capabilityId: "git-integration"
      name: "Git集成"
      description: "Git仓库集成"
      configSchema:
        type: "object"
        properties:
          repository:
            type: "string"
          branch:
            type: "string"
      defaultConfig:
        branch: "main"
      roles: ["EMPLOYEE"]
```

### 4.2 配置依赖关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           配置依赖关系图                                     │
└─────────────────────────────────────────────────────────────────────────────┘

SceneSkillConfig
├── templateId, version, name, description (基础信息)
│
├── dependencies (依赖配置)
│   ├── required[] (必需依赖)
│   │   ├── skillId + version + autoInstall + reason
│   │   └── 影响: 安装流程、依赖检查、自动安装
│   └── optional[] (可选依赖)
│       ├── skillId + version + autoInstall + reason
│       └── 影响: 功能扩展、可选安装
│
├── roles[] (角色配置)
│   ├── roleId + roleName + description
│   ├── permissions[] (权限列表)
│   └── isMandatory + minParticipants + maxParticipants
│       └── 影响: 参与者验证、激活流程
│
├── activationSteps (激活步骤)
│   ├── Map<Role, List<ActivationStepConfig>>
│   │   ├── stepId + name + description
│   │   ├── required (是否必需)
│   │   ├── uiComponent (UI组件)
│   │   ├── validationRules[] (验证规则)
│   │   └── defaultValue (默认值)
│   └── 影响: 激活流程引擎、步骤验证、UI渲染
│
├── menus[] (菜单配置)
│   ├── menuId + name + icon + path + component
│   ├── roles[] (可见角色)
│   ├── order (排序)
│   └── parentId (父菜单)
│       └── 影响: 菜单生成、权限控制、导航结构
│
├── uiSkills[] (UI技能)
│   ├── skillId + name + type (WIDGET/TOOLBAR/PANEL)
│   ├── entryPoint (入口组件)
│   ├── props (属性)
│   └── roles[] (适用角色)
│       └── 影响: UI渲染、组件加载、角色适配
│
└── privateCapabilities[] (私有能力)
    ├── capabilityId + name + description
    ├── configSchema (配置Schema)
    ├── defaultConfig (默认配置)
    └── roles[] (适用角色)
        └── 影响: 个性化配置、能力绑定、运行时行为

配置影响链:
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  dependencies   │───►│  安装流程控制    │───►│  依赖检查服务    │
└─────────────────┘    └─────────────────┘    └─────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     roles       │───►│  参与者验证     │───►│  激活流程选择    │
└─────────────────┘    └─────────────────┘    └─────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ activationSteps │───►│  激活流程引擎    │───►│  步骤验证服务    │
└─────────────────┘    └─────────────────┘    └─────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     menus       │───►│  菜单生成引擎    │───►│  权限控制服务    │
└─────────────────┘    └─────────────────┘    └─────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   uiSkills      │───►│  UI渲染引擎     │───►│  组件加载服务    │
└─────────────────┘    └─────────────────┘    └─────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│privateCapabilities│──►│  个性化配置     │───►│  能力绑定服务    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 五、服务依赖图谱 (Service Dependency Graph)

### 5.1 核心服务关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          服务依赖关系图                                      │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        表现层 (Presentation)                     │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  Activation │  │    Menu     │  │   Scene     │             │
│  │    API      │  │    API      │  │    API      │             │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘             │
└─────────┼────────────────┼────────────────┼─────────────────────┘
          │                │                │
          ▼                ▼                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        应用层 (Application)                      │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ ActivationFlow  │  │ MenuGeneration  │  │  SceneSkill     │  │
│  │     Engine      │  │     Engine      │  │    Lifecycle    │  │
│  │                 │  │                 │  │                 │  │
│  │ - startActivation│  │ - generateMenu  │  │ - install       │  │
│  │ - executeStep   │  │ - registerMenu  │  │ - activate      │  │
│  │ - getStatus     │  │ - getUserMenu   │  │ - deactivate    │  │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  │
└───────────┼────────────────────┼────────────────────┼───────────┘
            │                    │                    │
            ▼                    ▼                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                        领域层 (Domain)                           │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │ Activation  │  │    Menu     │  │   Scene     │             │
│  │   Service   │  │   Service   │  │   Service   │             │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘             │
│         │                │                │                     │
│  ┌──────┴──────┐  ┌──────┴──────┐  ┌──────┴──────┐             │
│  │  Push       │  │  Reminder   │  │  Journal    │             │
│  │  Service    │  │  Service    │  │  Service    │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────┼────────────────┼────────────────┼─────────────────────┘
          │                │                │
          ▼                ▼                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      基础设施层 (Infrastructure)                  │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  Template   │  │   Scene     │  │   Skill     │             │
│  │  Repository │  │  Repository │  │  Repository │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
│                                                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │   Config    │  │    Menu     │  │  Activation │             │
│  │   Storage   │  │   Storage   │  │   Storage   │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘

外部依赖:
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   LLM Service   │  │  Agent Service  │  │  Push Service   │
│   (llm-sdk)     │  │  (agent-sdk)    │  │  (external)     │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

### 5.2 服务调用链

```
激活流程调用链:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Client     │───►│ Activation  │───►│ Activation  │───►│  Template   │
│   Request   │    │    API      │    │Flow Engine  │    │ Repository  │
└─────────────┘    └─────────────┘    └──────┬──────┘    └─────────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
             ┌─────────────┐          ┌─────────────┐          ┌─────────────┐
             │    Push     │          │    Menu     │          │   Scene     │
             │   Service   │          │GenerationEngine          │  Service   │
             └─────────────┘          └─────────────┘          └─────────────┘

菜单生成调用链:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  User       │───►│    Menu     │───►│ MenuGeneration│──►│  Template   │
│  Request    │    │    API      │    │   Engine     │    │ Repository  │
└─────────────┘    └─────────────┘    └──────┬──────┘    └─────────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
             ┌─────────────┐          ┌─────────────┐          ┌─────────────┐
             │  Role-based │          │  UISkill    │          │  Permission │
             │   Filter    │          │   Loader    │          │   Check     │
             └─────────────┘          └─────────────┘          └─────────────┘
```

## 六、数据流图谱 (Data Flow Graph)

### 6.1 安装激活数据流

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        安装激活数据流                                        │
└─────────────────────────────────────────────────────────────────────────────┘

阶段1: 发现与预览
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  Skill  │────►│  Skill  │────►│  Skill  │────►│  User   │
│ Registry│     │Discovery│     │ Preview │     │ Preview │
└─────────┘     └────┬────┘     └────┬────┘     └─────────┘
                     │               │
                     ▼               ▼
              ┌─────────┐     ┌─────────┐
              │Template │     │Metadata │
              │  Load   │     │ Display │
              └─────────┘     └─────────┘

阶段2: 配置与依赖检查
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  User   │────►│ Config  │────►│   Dep   │────►│  Dep    │
│ Config  │     │  Save   │     │  Check  │     │ Display │
└─────────┘     └─────────┘     └────┬────┘     └─────────┘
                                     │
                    ┌────────────────┼────────────────┐
                    ▼                ▼                ▼
             ┌─────────┐      ┌─────────┐      ┌─────────┐
             │  Check  │      │  Check  │      │  Check  │
             │  Skill  │      │  Service│      │  Config │
             │  Exists │      │  Health │      │  Valid  │
             └─────────┘      └─────────┘      └─────────┘

阶段3: 安装与激活
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  User   │────►│ Install │────►│ Activate│────►│  Menu   │
│ Confirm │     │  Skill  │     │  Skill  │     │ Register│
└─────────┘     └────┬────┘     └────┬────┘     └─────────┘
                     │               │
                     ▼               ▼
              ┌─────────┐     ┌─────────┐
              │  State  │     │Capability│
              │  Change │     │  Bind   │
              │INSTALLED│     │         │
              └─────────┘     └─────────┘

阶段4: 运行时
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  User   │────►│  Menu   │────►│  Load   │────►│  Render │
│  Access │     │  Load   │     │ UISkill │     │   UI    │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
```

### 6.2 激活步骤数据流

```
领导激活数据流:
┌─────────────────────────────────────────────────────────────────────────────┐
│  Step 1: confirm-participants                                               │
│  Input:  { sceneId, userId, role }                                          │
│  Output: { participants: [{userId, roleId, status}], confirmed: boolean }   │
│  ─────────────────────────────────────────────────────────────────────────  │
│  1. Load template roles → 2. Get current participants → 3. Validate min/max │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Step 2: select-push-targets                                                │
│  Input:  { sceneId, participants }                                          │
│  Output: { targets: [{type, targetRef, channel}], selected: boolean }       │
│  ─────────────────────────────────────────────────────────────────────────  │
│  1. Get employee list → 2. Select targets → 3. Choose channels              │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Step 3: config-conditions                                                  │
│  Input:  { sceneId, triggerConfig }                                         │
│  Output: { conditions: {triggerTime, remindBefore, rules}, configured: true}│
│  ─────────────────────────────────────────────────────────────────────────  │
│  1. Load default config → 2. User customize → 3. Validate rules             │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Step 4: get-key (Optional)                                                 │
│  Input:  { sceneId, needKey: boolean }                                      │
│  Output: { keyInfo: {keyId, expiresAt}, obtained: boolean }                 │
│  ─────────────────────────────────────────────────────────────────────────  │
│  1. Check if key needed → 2. Apply for key → 3. Store securely              │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Step 5: confirm-activation                                                 │
│  Input:  { sceneId, allStepData }                                           │
│  Output: { confirmed: boolean, summary: Object }                            │
│  ─────────────────────────────────────────────────────────────────────────  │
│  1. Aggregate all data → 2. Display summary → 3. User confirm               │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Step 6: network-actions                                                    │
│  Input:  { sceneId, confirmed: true }                                       │
│  Output: { success: boolean, menus: [], capabilities: [] }                  │
│  ─────────────────────────────────────────────────────────────────────────  │
│  1. Register menus → 2. Bind capabilities → 3. Send notifications           │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 七、完整知识图谱总结

### 7.1 实体统计

| 实体类型 | 数量 | 核心属性数 | 关系数 |
|---------|------|-----------|--------|
| SceneSkill | 1 | 8 | 2 |
| SceneSkillConfig | 1 | 4 | 6 |
| DependencyConfig | 1 | 2 | 2 |
| RoleConfig | 1 | 6 | 5 |
| ActivationStepConfig | 1 | 6 | 2 |
| MenuConfig | 1 | 7 | 3 |
| UISkillConfig | 1 | 6 | 2 |
| CapabilityConfig | 1 | 5 | 2 |
| SceneInstance | 1 | 6 | 3 |
| Participant | 1 | 4 | 4 |
| InstalledSkill | 1 | 4 | 2 |
| ActivationProcess | 1 | 8 | 4 |
| MenuItem | 1 | 6 | 2 |
| UserMenu | 1 | 4 | 2 |
| PushTarget | 1 | 4 | 2 |
| ReminderConfig | 1 | 5 | 2 |
| **总计** | **16** | **82** | **43** |

### 7.2 流程统计

| 流程类型 | 步骤数 | 关键决策点 | 数据交换次数 |
|---------|--------|-----------|-------------|
| 安装状态机 | 11个状态 | 4个 | 8次 |
| 领导激活 | 6步 | 2个 | 12次 |
| 员工激活 | 3步 | 1个 | 6次 |
| 独立技能激活 | 4步 | 1个 | 8次 |
| 菜单生成 | 4阶段 | 2个 | 6次 |

### 7.3 配置统计

| 配置类别 | 配置项数 | 必填项 | 可选项 | 影响范围 |
|---------|---------|--------|--------|---------|
| 基础信息 | 4 | 4 | 0 | 全局 |
| 依赖配置 | 2 | 1 | 1 | 安装流程 |
| 角色配置 | 6 | 4 | 2 | 激活流程 |
| 激活步骤 | 6 | 4 | 2 | 激活流程 |
| 菜单配置 | 7 | 5 | 2 | 运行时 |
| UI技能 | 6 | 4 | 2 | 运行时 |
| 私有能力 | 5 | 3 | 2 | 个性化 |

### 7.4 服务统计

| 服务层 | 服务数 | 接口数 | 依赖服务 |
|--------|--------|--------|---------|
| 表现层 | 3 | 12 | 3 |
| 应用层 | 3 | 15 | 6 |
| 领域层 | 6 | 24 | 4 |
| 基础设施层 | 6 | 18 | 0 |
| **总计** | **18** | **69** | **13** |

---

*文档生成时间: 2026-03-09*
*基于9个详细设计文档综合分析*
