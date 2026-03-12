# SE 技能分类 - 用户视角设计

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **标准**: 以 SE (scene-engine) 为准  
> **目标**: 统一用户层和管理员层的分类展示

---

## 一、核心设计原则

### 1.1 以 SE 三维分类为基础

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     SE 三维分类体系 (标准)                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  维度1: skillForm (技能形态)                                                  │
│  ├── STANDALONE  - 独立技能 (单一功能，如驱动、提供者)                        │
│  └── SCENE       - 场景技能 (复杂业务场景)                                    │
│                                                                             │
│  维度2: sceneType (场景类型) - 仅 SCENE 有效                                   │
│  ├── AUTO        - 自驱场景 (自动运行)                                        │
│  └── TRIGGER     - 触发场景 (需用户触发)                                      │
│                                                                             │
│  维度3: visibility (可见性)                                                   │
│  ├── public      - 公开可见 (用户可发现安装)                                  │
│  └── internal    - 内部使用 (后台运行，用户不可见)                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 用户展示层分类

**用户看到的不是技术分类，而是业务分类**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     用户展示层分类设计                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  展示分类 = 业务领域 + 使用场景                                               │
│                                                                             │
│  示例:                                                                      │
│  ├── 办公协作 (日志汇报、会议管理、审批流程)                                  │
│  ├── 人力资源 (招聘管理、员工档案、绩效考核)                                  │
│  ├── 智能助手 (知识问答、AI对话、智能客服)                                    │
│  ├── 数据处理 (报表分析、数据同步、ETL工具)                                   │
│  └── 系统工具 (文件存储、消息通知、定时任务)                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、用户分类设计

### 2.1 用户可见分类 (public)

| 展示分类 | 业务领域 | 包含场景 | 典型技能 |
|----------|----------|----------|----------|
| **🏢 办公协作** | 团队协作 | TRIGGER | 日志汇报、会议管理、审批流程、任务分配 |
| **👥 人力资源** | HR管理 | TRIGGER | 招聘管理、员工档案、绩效考核、培训管理 |
| **🤖 智能助手** | AI服务 | AUTO | 知识问答、AI对话、智能客服、文档助手 |
| **📊 数据处理** | 数据分析 | AUTO/TRIGGER | 报表分析、数据同步、数据清洗、可视化 |
| **💼 项目管理** | 项目协作 | TRIGGER | 项目跟踪、敏捷看板、里程碑管理 |
| **📢 营销运营** | 市场推广 | AUTO/TRIGGER | 内容发布、社媒管理、活动运营 |
| **🔧 系统工具** | 基础服务 | AUTO | 文件存储、消息通知、定时任务、备份恢复 |

### 2.2 分类与 SE 三维分类映射

```yaml
# 办公协作分类
classification:
  id: office-collaboration
  name: 办公协作
  nameEn: Office Collaboration
  icon: ri-team-line
  description: 团队协作、日志汇报、会议管理等办公场景
  
  # 映射到 SE 三维分类
  seMapping:
    skillForm: SCENE
    sceneType: TRIGGER
    visibility: public
  
  # 典型技能
  exampleSkills:
    - skill-daily-report      # 日志汇报
    - skill-meeting-manager   # 会议管理
    - skill-approval-flow     # 审批流程
    - skill-task-assign       # 任务分配

# 人力资源分类
classification:
  id: human-resource
  name: 人力资源
  nameEn: Human Resource
  icon: ri-user-settings-line
  description: 招聘、员工管理、绩效考核等HR场景
  
  seMapping:
    skillForm: SCENE
    sceneType: TRIGGER
    visibility: public
  
  exampleSkills:
    - skill-recruitment       # 招聘管理
    - skill-employee-profile  # 员工档案
    - skill-performance       # 绩效考核
    - skill-training          # 培训管理
```

### 2.3 用户界面展示

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     用户技能市场界面                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【顶部导航】                                                                │
│  全部 | 办公协作 | 人力资源 | 智能助手 | 数据处理 | 项目管理 | 营销运营 | 系统工具 │
│                                                                             │
│  【筛选条件】                                                                │
│  运行模式: [全部] [自动运行] [手动触发]                                      │
│  排序: [热门] [最新] [评分]                                                  │
│                                                                             │
│  【技能卡片列表】                                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ [图标] 日志汇报场景          [办公协作] [手动触发]  ⭐4.8  [安装]    │   │
│  │        团队协作的日志汇报系统，支持日报/周报/月报...                  │   │
│  │        参与者: 管理者、员工  |  能力: 组织、LLM、调度                 │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ [图标] 知识问答助手          [智能助手] [自动运行]  ⭐4.9  [安装]    │   │
│  │        基于知识库的智能问答，支持多轮对话...                          │   │
│  │        参与者: 用户  |  能力: 知识库、LLM                            │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ [图标] 招聘管理系统          [人力资源] [手动触发]  ⭐4.6  [安装]    │   │
│  │        全流程招聘管理，从职位发布到入职办理...                        │   │
│  │        参与者: HR、面试官、候选人  |  能力: 组织、LLM、邮件          │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、管理员分类设计

### 3.1 管理员视角分类

管理员需要看到**技术分类 + 业务分类 + 系统分类**

| 分类维度 | 分类项 | 说明 |
|----------|--------|------|
| **业务分类** | 办公协作、人力资源、智能助手... | 同用户视角 |
| **技术分类** | LLM驱动、存储驱动、组织驱动... | 按能力类型 |
| **系统分类** | 系统监控、安全审计、调度服务... | internal 技能 |
| **形态分类** | SCENE场景、STANDALONE独立 | 按技能形态 |
| **状态分类** | 已发布、待审核、已禁用 | 按发布状态 |

### 3.2 管理员界面展示

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     管理员技能管理界面                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【顶部标签】                                                                │
│  [业务分类] [技术分类] [系统分类] [形态分类] [状态分类]                      │
│                                                                             │
│  === 业务分类标签 ===                                                        │
│  全部 | 办公协作 | 人力资源 | 智能助手 | 数据处理 | 项目管理 | 营销运营 | 系统工具 │
│                                                                             │
│  === 技术分类标签 ===                                                        │
│  全部 | LLM服务 | 知识库 | 存储服务 | 组织服务 | 消息服务 | 支付服务 | 媒体服务 │
│                                                                             │
│  === 系统分类标签 (仅管理员可见) ===                                         │
│  全部 | 系统监控 | 安全审计 | 调度服务 | 认证服务 | 网络服务                 │
│                                                                             │
│  【高级筛选】                                                                │
│  形态: [全部] [SCENE场景] [STANDALONE独立]                                   │
│  可见性: [全部] [public公开] [internal内部]                                  │
│  状态: [全部] [已发布] [待审核] [已禁用]                                     │
│  运行模式: [全部] [AUTO自动] [TRIGGER触发]                                   │
│                                                                             │
│  【技能列表 - 管理员视图】                                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ [图标] 系统监控服务    [系统监控] [internal] [系统内置] [已启用] [管理]│   │
│  │        系统监控和告警服务，后台自动运行...                            │   │
│  │        能力地址: 0x50 MON_METRICS, 0x06 SYS_CONFIG                   │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ [图标] LLM-Ollama驱动  [LLM服务] [STANDALONE] [public] [已发布] [管理]│   │
│  │        Ollama本地LLM服务驱动...                                       │   │
│  │        能力地址: 0x28 LLM_OLLAMA                                     │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ [图标] 日志汇报场景    [办公协作] [SCENE] [public] [已发布] [管理]    │   │
│  │        团队协作的日志汇报系统...                                      │   │
│  │        能力地址: 0x08 ORG_LOCAL, 0x28 LLM_OLLAMA, 0x68 SCHED_QUARTZ  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、具体场景分类示例

### 4.1 日志汇报场景

```yaml
skill: skill-daily-report
name: 日志汇报场景

# SE 三维分类 (技术层面)
seClassification:
  skillForm: SCENE           # 场景技能
  sceneType: TRIGGER         # 触发场景 (需用户填写日志)
  visibility: public         # 公开可见

# 用户展示分类 (业务层面)
userClassification:
  primary: 办公协作          # 主分类
  secondary: 团队协作        # 次级分类
  tags: [日志, 汇报, 日报, 周报, 团队]

# 管理员技术分类
adminClassification:
  business: 办公协作         # 业务分类
  technical: 组织服务        # 技术分类 (主要能力)
  capabilities:              # 能力地址
    - 0x08 ORG_LOCAL         # 组织服务
    - 0x20 DB_SQLITE         # 数据存储
    - 0x28 LLM_OLLAMA        # LLM服务
    - 0x48 COMM_MSG          # 消息服务
    - 0x68 SCHED_QUARTZ      # 调度服务

# 用户界面展示
uiDisplay:
  icon: ri-file-list-3-line
  color: blue
  description: 团队协作的日志汇报系统，支持日报/周报/月报，自动汇总分析
  features:
    - 多角色协作 (管理者、员工)
    - 定时提醒 (调度服务)
    - 智能汇总 (LLM分析)
    - 消息通知 (邮件/钉钉)
```

### 4.2 招聘管理场景

```yaml
skill: skill-recruitment
name: 招聘管理场景

# SE 三维分类
seClassification:
  skillForm: SCENE
  sceneType: TRIGGER
  visibility: public

# 用户展示分类
userClassification:
  primary: 人力资源
  secondary: 招聘管理
  tags: [招聘, 面试, 简历, 入职, HR]

# 管理员技术分类
adminClassification:
  business: 人力资源
  technical: 组织服务
  capabilities:
    - 0x08 ORG_LOCAL         # 组织服务
    - 0x20 DB_SQLITE         # 数据存储
    - 0x28 LLM_OLLAMA        # LLM服务 (简历分析)
    - 0x4A COMM_EMAIL        # 邮件服务 (面试通知)

# 用户界面展示
uiDisplay:
  icon: ri-user-add-line
  color: green
  description: 全流程招聘管理，从职位发布、简历筛选到入职办理
  features:
    - 多角色协作 (HR、面试官、候选人)
    - AI简历分析
    - 面试安排
    - 邮件通知
```

### 4.3 知识问答场景

```yaml
skill: skill-knowledge-qa
name: 知识问答助手

# SE 三维分类
seClassification:
  skillForm: SCENE
  sceneType: AUTO            # 自驱场景 (用户随时提问)
  visibility: public

# 用户展示分类
userClassification:
  primary: 智能助手
  secondary: 知识问答
  tags: [AI, 问答, 知识库, 智能客服]

# 管理员技术分类
adminClassification:
  business: 智能助手
  technical: 知识服务
  capabilities:
    - 0x30 KNOW_VECTOR       # 向量知识库
    - 0x28 LLM_OLLAMA        # LLM服务
    - 0x34 KNOW_EMBEDDING    # 嵌入服务

# 用户界面展示
uiDisplay:
  icon: ri-question-answer-line
  color: purple
  description: 基于知识库的智能问答，支持多轮对话和上下文理解
  features:
    - 7×24小时服务
    - 多轮对话
    - 知识库自动更新
    - 上下文记忆
```

---

## 五、分类映射表

### 5.1 业务分类 → SE 三维分类

| 业务分类 | skillForm | sceneType | visibility | 典型场景 |
|----------|-----------|-----------|------------|----------|
| **办公协作** | SCENE | TRIGGER | public | 日志汇报、会议管理、审批流程 |
| **人力资源** | SCENE | TRIGGER | public | 招聘管理、绩效考核、培训管理 |
| **智能助手** | SCENE | AUTO | public | 知识问答、AI对话、智能客服 |
| **数据处理** | SCENE | AUTO/TRIGGER | public | 报表分析、数据同步、可视化 |
| **项目管理** | SCENE | TRIGGER | public | 项目跟踪、敏捷看板 |
| **营销运营** | SCENE | AUTO/TRIGGER | public | 内容发布、社媒管理 |
| **系统工具** | STANDALONE | - | public | 文件存储、消息通知 |
| **系统监控** | SCENE/STANDALONE | AUTO | internal | 监控告警、日志收集 |

### 5.2 技术分类 → 能力地址

| 技术分类 | 能力地址范围 | 典型驱动 |
|----------|-------------|----------|
| **LLM服务** | 0x28-0x2F | skill-llm-ollama, skill-llm-openai |
| **知识服务** | 0x30-0x37 | skill-knowledge-base, skill-rag |
| **存储服务** | 0x20-0x27 | skill-vfs-minio, skill-vfs-oss |
| **组织服务** | 0x08-0x0F | skill-org-dingding, skill-org-feishu |
| **消息服务** | 0x48-0x4F | skill-mqtt, skill-email |
| **支付服务** | 0x40-0x47 | skill-payment-alipay, skill-payment-wechat |
| **媒体服务** | 0x48-0x4F | skill-media-wechat, skill-media-weibo |
| **监控服务** | 0x50-0x57 | skill-monitor, skill-health |

---

## 六、前端实现建议

### 6.1 分类数据接口

```typescript
// 获取用户可见的业务分类
GET /api/categories?role=user
Response: [
  { id: 'office-collaboration', name: '办公协作', icon: '...', count: 12 },
  { id: 'human-resource', name: '人力资源', icon: '...', count: 8 },
  { id: 'ai-assistant', name: '智能助手', icon: '...', count: 15 },
  ...
]

// 获取管理员可见的分类 (包含技术分类)
GET /api/categories?role=admin&view=all
Response: [
  // 业务分类
  { id: 'office-collaboration', name: '办公协作', type: 'business', ... },
  // 技术分类
  { id: 'llm-service', name: 'LLM服务', type: 'technical', addressRange: '0x28-0x2F', ... },
  // 系统分类
  { id: 'system-monitor', name: '系统监控', type: 'system', visibility: 'internal', ... }
]

// 获取分类下的技能
GET /api/skills?category=office-collaboration&role=user
Response: {
  total: 12,
  items: [
    { skillId: 'skill-daily-report', name: '日志汇报', sceneType: 'TRIGGER', ... },
    { skillId: 'skill-meeting-manager', name: '会议管理', sceneType: 'TRIGGER', ... }
  ]
}
```

### 6.2 分类过滤逻辑

```typescript
// 用户视角过滤
function filterSkillsForUser(skills: Skill[], categoryId: string): Skill[] {
  return skills.filter(skill => {
    // 1. 必须是 public
    if (skill.visibility !== 'public') return false;
    
    // 2. 匹配业务分类
    if (skill.userClassification?.primary !== categoryId) return false;
    
    // 3. 状态必须是已发布
    if (skill.status !== 'PUBLISHED') return false;
    
    return true;
  });
}

// 管理员视角过滤
function filterSkillsForAdmin(
  skills: Skill[], 
  filters: {
    categoryType?: 'business' | 'technical' | 'system';
    categoryId?: string;
    skillForm?: 'SCENE' | 'STANDALONE';
    visibility?: 'public' | 'internal';
    status?: 'PUBLISHED' | 'PENDING' | 'DISABLED';
  }
): Skill[] {
  return skills.filter(skill => {
    if (filters.skillForm && skill.skillForm !== filters.skillForm) return false;
    if (filters.visibility && skill.visibility !== filters.visibility) return false;
    if (filters.status && skill.status !== filters.status) return false;
    
    // 根据分类类型匹配
    if (filters.categoryType === 'business') {
      return skill.userClassification?.primary === filters.categoryId;
    } else if (filters.categoryType === 'technical') {
      return skill.adminClassification?.technical === filters.categoryId;
    }
    
    return true;
  });
}
```

---

## 七、总结

### 7.1 核心要点

1. **以 SE 三维分类为标准** (skillForm + sceneType + visibility)
2. **用户展示业务分类** (办公协作、人力资源、智能助手...)
3. **管理员展示多维度** (业务 + 技术 + 系统)
4. **分类之间建立映射** (业务分类 ↔ SE三维分类)

### 7.2 实施步骤

1. **定义业务分类枚举** (7个主要分类)
2. **建立映射关系** (业务分类 → SE三维分类)
3. **更新 skill.yaml** (添加 userClassification 字段)
4. **实现分类接口** (前端获取分类和技能列表)
5. **验证分类展示** (用户视角和管理员视角)

---

**文档状态**: 设计规范  
**下一步**: 更新 skill.yaml 规范，添加 userClassification 字段
