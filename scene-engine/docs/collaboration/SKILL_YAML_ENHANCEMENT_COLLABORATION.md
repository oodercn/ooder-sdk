# 协作任务：增强场景技能 skill.yaml 配置

## 一、任务概述

**提交人**: SE SDK 团队  
**提交时间**: 2026-03-21  
**优先级**: 高  
**涉及团队**: Skills 团队  
**关联任务**: SE-SDK-COLLAB-2026-003  
**参考文档**: [SKILL_INSTALLATION_SCHEME.md](file:///E:/github/ooder-skills/mvp/docs/collaboration/SKILL_INSTALLATION_SCHEME.md)

---

## 二、背景说明

SE SDK 团队已完成场景配置验证功能的开发，现需要 Skills 团队配合增强场景技能的 `skill.yaml` 配置文件，以确保安装时能够通过配置完整性验证。

### 2.1 SE SDK 已实现功能

1. **SceneConfigLoader** - 从 skill.yaml 加载场景配置
2. **SceneValidationException** - 场景验证异常
3. **validateSceneConfig** - 配置完整性验证

### 2.2 验证规则

安装场景类型技能时会进行以下验证：

| 验证项 | 验证类型 | 错误信息 |
|--------|----------|----------|
| 场景配置存在 | SCENE_CONFIG_MISSING | 技能包中未定义场景配置 |
| 角色定义存在 | ROLES_MISSING | 场景缺少角色定义 |
| 必需角色存在 | REQUIRED_ROLE_MISSING | 场景缺少必需角色 |
| 激活步骤存在 | ACTIVATION_STEPS_MISSING | 场景缺少激活步骤 |
| 角色激活步骤 | ROLE_ACTIVATION_STEPS_MISSING | 必需角色缺少激活步骤 |
| 菜单配置存在 | MENUS_MISSING | 场景缺少菜单配置 |
| 角色菜单配置 | ROLE_MENUS_MISSING | 必需角色缺少菜单 |

---

## 三、技能分类与场景模板需求

### 3.1 技能分类体系

根据 `skill-index/categories.yaml`，技能分为以下类型：

| 分类ID | 名称 | 安装类型 | 需要场景模板 | 用户可见 |
|--------|------|----------|:------------:|:--------:|
| **org** | 组织服务 | PROVIDER | ❌ | ❌ |
| **vfs** | 存储服务 | PROVIDER | ❌ | ❌ |
| **llm** | LLM服务 | PROVIDER/SCENE | ✅ | ✅ |
| **knowledge** | 知识服务 | SCENE | ✅ | ✅ |
| **biz** | 业务场景 | SCENE | ✅ | ✅ |
| **sys** | 系统管理 | PROVIDER/SCENE | ⚠️ 部分 | ❌ |
| **msg** | 消息通讯 | PROVIDER | ❌ | ❌ |
| **ui** | UI生成 | PROVIDER | ❌ | ❌ |
| **payment** | 支付服务 | PROVIDER | ❌ | ❌ |
| **media** | 媒体发布 | PROVIDER | ⚠️ 部分 | ✅ |
| **util** | 工具服务 | PROVIDER/SCENE | ⚠️ 部分 | ✅ |
| **nexus-ui** | Nexus界面 | PROVIDER | ❌ | ❌ |

### 3.2 安装类型定义

| 安装类型 | 说明 | 安装后状态 | 入口位置 |
|----------|------|------------|----------|
| **PROVIDER** | 独立能力提供者，被其他技能依赖 | INSTALLED | 不可见，作为依赖 |
| **SCENE** | 场景能力，需要激活 | PENDING_ACTIVATION | 场景管理/菜单 |
| **STANDALONE** | 独立运行，无需激活 | INSTALLED | 工具列表 |

---

## 四、Skills 团队任务

### 4.1 需要增强的技能列表

| 技能ID | 分类 | 安装类型 | 优先级 |
|--------|------|----------|--------|
| skill-recruitment-management | biz | SCENE | 高 |
| skill-approval-form | biz | SCENE | 高 |
| skill-collaboration | biz | SCENE | 高 |
| skill-business | biz | SCENE | 中 |
| skill-knowledge-qa | knowledge | SCENE | 中 |
| skill-knowledge-share | knowledge | SCENE | 中 |
| skill-meeting-minutes | biz | SCENE | 中 |
| skill-project-knowledge | knowledge | SCENE | 中 |

### 4.2 完整配置模板

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: {skill-id}
  name: {技能名称}
  version: "2.3.1"
  category: {分类}              # biz, knowledge, llm, util 等
  subCategory: {子分类}         # 可选
  description: {描述}
  author: {作者}
  icon: {图标}                  # Remix Icon 格式
  tags:
    - tag1
    - tag2

spec:
  # 能力定义
  capabilities:
    - id: {cap-id}
      name: {能力名称}
      description: {描述}
      category: service | ai | data | trigger
      
  # 依赖技能
  dependencies:
    - skillId: {dep-skill-id}
      version: ">=x.x.x"
      required: true/false
      description: "依赖说明"
      
  # 技能形式（必需）
  skillForm: PROVIDER | SCENE | STANDALONE
  
  # 场景配置（SCENE类型必需）
  scene:
    type: AUTO | TRIGGER | INTERACTIVE
    visibility: public | internal
    driver: {scene-driver-id}    # 可选
    
  # 角色配置（SCENE类型必需）
  roles:
    - id: {role-id}              # 角色ID，如：admin, hr-manager
      name: {角色显示名称}
      description: {角色描述}
      permissions:
        - perm1
        - perm2
        
  # 激活步骤（SCENE类型必需，按角色区分）
  activationSteps:
    {role-id}:
      - step: 1
        action: {动作标识}
        title: {步骤标题}
        description: {步骤描述}
        required: true
      - step: 2
        action: {动作标识}
        title: {步骤标题}
        description: {步骤描述}
        required: false
        
  # 菜单配置（SCENE类型必需，按角色区分）
  menus:
    {role-id}:
      - id: {menu-id}
        name: {菜单名称}
        icon: ri-icon-name
        path: /{scene-id}/page
        order: 1
        
  # 配置模式（可选）
  configSchema:
    type: object
    properties:
      configKey:
        type: string
        enum: [option1, option2]
        default: option1
        title: 配置项标题
        
  # 安装顺序（可选）
  installOrder:
    - dep-skill-1
    - dep-skill-2
    - {skill-id}
    
  # 资源估算（可选）
  estimatedResources:
    cpu: "500m"
    memory: "512Mi"
    storage: "1Gi"
    
  estimatedDuration: "5-10分钟"
```

### 4.3 配置示例：招聘管理系统

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: skill-recruitment-management
  name: 招聘管理系统
  version: "2.3.1"
  category: biz
  subCategory: hr
  description: 企业招聘全流程管理系统，支持职位发布、简历收集、面试安排、录用审批
  author: Ooder Team
  icon: ri-user-add-line
  tags:
    - recruitment
    - hr
    - hiring
    - interview

spec:
  capabilities:
    - id: job-posting
      name: 职位发布
      description: 创建、编辑、发布招聘职位
      category: service
    - id: resume-collection
      name: 简历收集
      description: 接收、解析、存储候选人简历
      category: service
    - id: interview-scheduling
      name: 面试安排
      description: 安排面试时间、地点、面试官
      category: service
    - id: offer-approval
      name: 录用审批
      description: 发起录用审批流程
      category: service
      
  dependencies:
    - skillId: skill-vfs-base
      version: ">=2.3.1"
      required: true
      description: 文件存储服务（简历存储）
    - skillId: skill-approval-form
      version: ">=2.3.1"
      required: true
      description: 审批表单服务（录用审批）
    - skillId: skill-msg
      version: ">=2.3.1"
      required: false
      description: 消息通知服务（面试提醒）
      
  skillForm: SCENE
  
  scene:
    type: INTERACTIVE
    visibility: public
    driver: null
    
  roles:
    - id: hr-manager
      name: HR管理员
      description: 管理招聘流程和职位
      permissions:
        - manage-jobs
        - view-resumes
        - schedule-interviews
        - approve-offers
    - id: interviewer
      name: 面试官
      description: 参与面试评估
      permissions:
        - view-assigned-resumes
        - submit-feedback
    - id: candidate
      name: 候选人
      description: 查看职位和提交简历
      permissions:
        - view-jobs
        - submit-resume
        
  activationSteps:
    hr-manager:
      - step: 1
        action: configure-departments
        title: 配置部门
        description: 设置招聘部门信息
        required: true
      - step: 2
        action: configure-workflow
        title: 配置流程
        description: 设置招聘审批流程
        required: true
      - step: 3
        action: invite-interviewers
        title: 邀请面试官
        description: 添加面试官账号
        required: false
    interviewer:
      - step: 1
        action: accept-invitation
        title: 接受邀请
        description: 加入招聘团队
        required: true
    candidate:
      - step: 1
        action: register
        title: 注册账号
        description: 创建候选人账号
        required: true
        
  menus:
    hr-manager:
      - id: dashboard
        name: 招聘概览
        icon: ri-dashboard-line
        path: /recruitment/dashboard
        order: 1
      - id: jobs
        name: 职位管理
        icon: ri-briefcase-line
        path: /recruitment/jobs
        order: 2
      - id: resumes
        name: 简历管理
        icon: ri-file-user-line
        path: /recruitment/resumes
        order: 3
      - id: interviews
        name: 面试安排
        icon: ri-calendar-line
        path: /recruitment/interviews
        order: 4
      - id: offers
        name: 录用管理
        icon: ri-user-follow-line
        path: /recruitment/offers
        order: 5
    interviewer:
      - id: my-interviews
        name: 我的面试
        icon: ri-calendar-check-line
        path: /recruitment/my-interviews
        order: 1
      - id: feedback
        name: 面试反馈
        icon: ri-chat-check-line
        path: /recruitment/feedback
        order: 2
    candidate:
      - id: jobs
        name: 浏览职位
        icon: ri-search-line
        path: /recruitment/jobs
        order: 1
      - id: my-application
        name: 我的申请
        icon: ri-file-list-line
        path: /recruitment/my-application
        order: 2
        
  configSchema:
    type: object
    properties:
      resumeStorage:
        type: string
        enum: [local, database, oss]
        default: database
        title: 简历存储位置
      maxResumeSize:
        type: integer
        default: 10
        title: 最大简历大小(MB)
      interviewReminder:
        type: boolean
        default: true
        title: 面试提醒
      approvalWorkflow:
        type: string
        default: default
        title: 审批流程
        
  installOrder:
    - skill-vfs-base
    - skill-approval-form
    - skill-msg
    - skill-recruitment-management
    
  estimatedResources:
    cpu: "500m"
    memory: "512Mi"
    storage: "1Gi"
    
  estimatedDuration: "5-10分钟"
```

---

## 五、验证方法

### 5.1 本地验证

更新配置后，可通过以下方式验证：

1. **YAML 格式验证**
   ```bash
   yamllint skill.yaml
   ```

2. **安装测试**
   - 将更新后的技能包部署到 MVP 环境
   - 尝试安装技能
   - 验证是否通过配置完整性检查

### 5.2 验证清单

- [ ] `spec.skillForm` 设置为 `SCENE`
- [ ] `spec.scene` 配置完整（type, visibility）
- [ ] `spec.roles` 至少定义一个角色
- [ ] `spec.activationSteps` 为每个角色定义激活步骤
- [ ] `spec.menus` 为每个角色定义菜单
- [ ] 所有角色 ID 在 `roles`、`activationSteps`、`menus` 中保持一致

---

## 六、SE SDK 新增类说明

### 6.1 SceneConfigLoader

位置：`net.ooder.scene.skill.install.SceneConfigLoader`

负责从技能包的 skill.yaml 读取场景配置，支持解析：
- `spec.capability` - 能力分类配置
- `spec.scene` - 场景类型配置
- `spec.roles` - 角色定义
- `spec.activationSteps` - 激活步骤
- `spec.menus` - 菜单配置
- `spec.privateCapabilities` - 私有能力

### 6.2 SceneValidationException

位置：`net.ooder.scene.skill.exception.SceneValidationException`

场景配置验证失败时抛出，包含：
- `skillId` - 技能ID
- `validationType` - 验证类型
- 详细错误信息

### 6.3 扩展的配置类

以下类已扩展以支持新字段：

| 类名 | 新增字段 |
|------|----------|
| `SceneTemplate` | `sceneType`, `visibility`, `category`, `capabilityCode` |
| `RoleConfig` | `name`, `permissions` |
| `ActivationStepConfig` | `name`, `autoExecute`, `privateCapabilities` |

---

## 七、时间计划

| 阶段 | 任务 | 预计时间 |
|------|------|----------|
| 阶段1 | 更新高优先级技能配置（招聘、审批、协作） | 2 天 |
| 阶段2 | 更新中优先级技能配置 | 2 天 |
| 阶段3 | 集成测试 | 1 天 |

---

## 八、联系方式

- **SE SDK 团队**: 已完成验证功能开发
- **Skills 团队**: 负责技能配置增强

---

**创建时间**: 2026-03-21  
**状态**: 待处理  
**文档版本**: 2.0
