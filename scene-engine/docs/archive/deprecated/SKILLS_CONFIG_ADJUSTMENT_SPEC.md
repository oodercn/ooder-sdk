# Skills 配置调整说明 - SE v3.0 协作

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **发起团队**: Engine Team (scene-engine)  
> **目标团队**: Skills Team  
> **状态**: 待确认

---

## 一、背景说明

### 1.1 SE 已实现的能力地址框架

SE (scene-engine) 已完成能力地址空间核心框架实现：

| 组件 | 位置 | 说明 |
|------|------|------|
| `CapabilityAddress` | SDK | 128个固定地址枚举 (0x00-0x7F) |
| `CapabilityCategory` | SDK | 16个分类枚举 |
| `CapabilityRouter` | SE | 能力路由、驱动绑定 |
| `CapabilityInstanceRegistry` | SE | 实例注册、上下文隔离 |
| `CapabilityMappingService` | SE | capability → address 映射 |
| `CapabilityInstanceSnapshot` | SE | 持久化快照 |
| `CapabilityInstanceRestorer` | SE | 离线恢复逻辑 |

### 1.2 配置调整目标

1. **统一能力地址**: 所有能力配置使用固定地址
2. **分层配置管理**: MCP层(预定义) + ROUTE层(动态)
3. **上下文隔离**: 同一地址多实例通过contextId区分
4. **安全标识**: userSessionId/agentSessionId作为唯一安全标识

---

## 二、Skills 配置调整清单

### 2.1 skill.yaml 配置调整

#### 2.1.1 新增 capabilityAddresses 字段

```yaml
# 旧配置 (废弃)
spec:
  capabilities:
    - id: kb-management
      category: service

# 新配置 (推荐)
spec:
  capabilityAddresses:
    required:
      - address: 0x30         # KNOW_VECTOR - 向量知识库
        name: "知识库服务"
        fallback: null        # 必需，无降级
      - address: 0x28         # LLM_OLLAMA - LLM服务
        name: "LLM服务"
        fallback: 0x29        # 降级到 LLM_OPENAI
    optional:
      - address: 0x4A         # COMM_EMAIL - 邮件服务
        name: "邮件通知"
        skipable: true
```

#### 2.1.2 能力地址映射表

| 能力类型 | 地址 | 代码 | 说明 |
|----------|:----:|------|------|
| **知识库** | 0x30 | KNOW_VECTOR | 向量知识库 |
| | 0x31 | KNOW_DOCUMENT | 文档知识库 |
| | 0x32 | KNOW_GRAPH | 图谱知识库 |
| | 0x34 | KNOW_EMBEDDING | 嵌入服务 |
| **LLM** | 0x28 | LLM_OLLAMA | Ollama本地模型 |
| | 0x29 | LLM_OPENAI | OpenAI API |
| | 0x2A | LLM_QIANWEN | 通义千问 |
| | 0x2B | LLM_DEEPSEEK | DeepSeek |
| **数据库** | 0x20 | DB_SQLITE | SQLite |
| | 0x21 | DB_MYSQL | MySQL |
| | 0x22 | DB_POSTGRESQL | PostgreSQL |
| | 0x24 | DB_REDIS | Redis |
| **通讯** | 0x48 | COMM_MSG | 消息服务 |
| | 0x49 | COMM_MQTT | MQTT服务 |
| | 0x4A | COMM_EMAIL | 邮件服务 |
| | 0x4B | COMM_DINGTALK | 钉钉通知 |
| **组织** | 0x08 | ORG_LOCAL | 本地组织 |
| | 0x09 | ORG_DINGDING | 钉钉组织 |
| | 0x0A | ORG_FEISHU | 飞书组织 |

### 2.2 skill-index.yaml 配置调整

#### 2.2.1 新增 addressRequired 字段

```yaml
# 旧配置
- id: skill-knowledge-qa
  name: 知识问答
  category: knowledge
  type: abs

# 新配置
- id: skill-knowledge-qa
  name: 知识问答
  skillForm: SCENE           # STANDALONE / SCENE
  sceneType: AUTO            # AUTO / TRIGGER (仅SCENE有效)
  visibility: public         # public / internal
  addressRequired:           # 必需的能力地址
    - 0x30                   # KNOW_VECTOR
    - 0x28                   # LLM_OLLAMA
  addressOptional:           # 可选的能力地址
    - 0x4A                   # COMM_EMAIL
```

#### 2.2.2 分类体系变更

```
旧分类 (废弃):
├── ABS (Agent-Based Scene)
├── ASS (Agent-Supported Scene)
└── TBS (Team-Based Scene)

新分类 (二维):
├── SkillForm (技能形态)
│   ├── STANDALONE  - 独立技能
│   └── SCENE       - 场景技能
├── SceneType (场景类型)
│   ├── AUTO        - 自驱场景 (hasSelfDrive=true)
│   └── TRIGGER     - 触发场景 (hasSelfDrive=false)
└── visibility (可见性)
    ├── public      - 公开可见
    └── internal    - 内部使用
```

### 2.3 skill.json 元数据调整

#### 2.3.1 新增 capabilityAddress 字段

```json
{
  "skillId": "skill-knowledge-qa",
  "name": "知识问答",
  "version": "3.0.0",
  "metadata": {
    "skillForm": "SCENE",
    "sceneType": "AUTO",
    "visibility": "public",
    
    "capabilityAddresses": {
      "required": [
        {
          "address": "0x30",
          "name": "知识库服务",
          "fallback": null
        },
        {
          "address": "0x28",
          "name": "LLM服务",
          "fallback": "0x29"
        }
      ],
      "optional": [
        {
          "address": "0x4A",
          "name": "邮件通知",
          "skipable": true
        }
      ]
    },
    
    "sceneCapabilities": [...],
    "llmConfig": {...}
  }
}
```

---

## 三、配置示例

### 3.1 知识问答场景 (knowledge-qa)

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-knowledge-qa
  name: 知识问答场景
  version: 3.0.0
  skillForm: SCENE
  sceneType: AUTO
  visibility: public

spec:
  # 能力地址依赖
  capabilityAddresses:
    required:
      - address: 0x30         # KNOW_VECTOR
        name: "向量知识库"
        fallback: null
      - address: 0x28         # LLM_OLLAMA
        name: "LLM服务"
        fallback: 0x29        # 降级到 OpenAI
      - address: 0x34         # KNOW_EMBEDDING
        name: "嵌入服务"
        fallback: null
    optional:
      - address: 0x4A         # COMM_EMAIL
        name: "邮件通知"
        skipable: true

  # 角色配置
  roles:
    - name: USER
      displayName: "用户"
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE]

  # 激活流程
  activationSteps:
    USER:
      - stepId: create-knowledge-base
        name: 创建知识库
        type: AUTO
        required: true
      - stepId: select-llm-provider
        name: 选择LLM服务
        type: USER_SELECT
        required: true
      - stepId: confirm-activation
        name: 确认激活
        type: CONFIRM
        required: true

  # 菜单配置
  menus:
    USER:
      - id: kb-management
        name: 知识库管理
        icon: ri-book-3-line
        url: /console/pages/kb-management.html
        order: 1
      - id: qa-chat
        name: 智能问答
        icon: ri-chat-3-line
        url: /console/pages/qa-chat.html
        order: 2
```

### 3.2 日志汇报场景 (daily-report)

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-daily-report
  name: 日志汇报场景
  version: 3.0.0
  skillForm: SCENE
  sceneType: TRIGGER
  visibility: public

spec:
  # 能力地址依赖
  capabilityAddresses:
    required:
      - address: 0x08         # ORG_LOCAL
        name: "组织服务"
        fallback: null
      - address: 0x20         # DB_SQLITE
        name: "数据存储"
        fallback: 0x21        # 降级到 MySQL
      - address: 0x28         # LLM_OLLAMA
        name: "LLM服务"
        fallback: 0x29
      - address: 0x49         # COMM_MQTT
        name: "消息推送"
        fallback: null
      - address: 0x68         # SCHED_QUARTZ
        name: "调度服务"
        fallback: null
    optional:
      - address: 0x4A         # COMM_EMAIL
        name: "邮件服务"
        skipable: true
      - address: 0x4B         # COMM_DINGTALK
        name: "钉钉通知"
        skipable: true

  # 角色配置
  roles:
    - name: MANAGER
      displayName: "管理者"
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE, CONFIG, DELETE]
    - name: EMPLOYEE
      displayName: "员工"
      minCount: 1
      maxCount: 100
      permissions: [READ, WRITE]

  # 激活流程
  activationSteps:
    MANAGER:
      - stepId: select-participants
        name: 选择参与者
        type: USER_SELECT
        required: true
      - stepId: config-schedule
        name: 配置调度
        type: CONFIG_FORM
        required: true
      - stepId: config-notification
        name: 配置通知
        type: CONFIG_FORM
        required: false
        skipable: true
      - stepId: confirm-activation
        name: 确认激活
        type: CONFIRM
        required: true
    EMPLOYEE:
      - stepId: accept-invite
        name: 接受邀请
        type: CONFIRM
        required: true

  # 菜单配置
  menus:
    MANAGER:
      - id: team-logs
        name: 团队日志
        icon: ri-team-line
        url: /console/pages/team-logs.html
        order: 1
      - id: report-summary
        name: 汇总分析
        icon: ri-bar-chart-line
        url: /console/pages/report-summary.html
        order: 2
    EMPLOYEE:
      - id: write-log
        name: 填写日志
        icon: ri-edit-line
        url: /console/pages/write-log.html
        order: 1
      - id: my-history
        name: 我的记录
        icon: ri-history-line
        url: /console/pages/my-history.html
        order: 2
```

---

## 四、配置检验标准

### 4.1 必需检验项

| 检验项 | 检验方法 | 预期结果 |
|--------|----------|----------|
| 地址有效性 | `CapabilityAddress.fromAddress(addr)` | 返回有效枚举 |
| 必需地址完整 | 检查required字段 | 所有地址已配置 |
| 角色定义完整 | 检查roles字段 | 至少一个角色 |
| 激活步骤完整 | 检查activationSteps | 每角色至少一个步骤 |

### 4.2 推荐检验项

| 检验项 | 检验方法 | 预期结果 |
|--------|----------|----------|
| 降级地址有效 | 检查fallback字段 | 降级地址已定义 |
| 菜单配置完整 | 检查menus字段 | 每角色有菜单 |
| 可选地址标记 | 检查skipable字段 | 可选项已标记 |

---

## 五、迁移指南

### 5.1 从旧配置迁移

```
旧配置 → 新配置映射:

capabilities[].id          → capabilityAddresses.required[].address
capabilities[].category    → (废弃，使用地址分类)
type (abs/ass/tbs)         → skillForm + sceneType
```

### 5.2 迁移步骤

1. **更新 skill-index.yaml**
   - 添加 skillForm、sceneType、visibility 字段
   - 添加 addressRequired、addressOptional 字段
   - 移除 type 字段

2. **更新 skill.yaml**
   - 将 capabilities 转换为 capabilityAddresses
   - 添加 roles 配置
   - 添加 activationSteps 配置
   - 添加 menus 配置

3. **更新 skill.json**
   - 添加 capabilityAddresses 元数据
   - 更新分类字段

---

## 六、协作确认

### 6.1 Skills Team 需确认

| 序号 | 确认项 | 状态 |
|:----:|--------|:----:|
| 1 | 能力地址分配是否合理 | ⬜ |
| 2 | 分类体系变更是否接受 | ⬜ |
| 3 | 配置格式是否可行 | ⬜ |
| 4 | 迁移工作量评估 | ⬜ |
| 5 | 完成时间预估 | ⬜ |

### 6.2 SE Team 已完成

| 序号 | 完成项 | 状态 |
|:----:|--------|:----:|
| 1 | CapabilityAddress 枚举 | ✅ |
| 2 | CapabilityCategory 枚举 | ✅ |
| 3 | CapabilityRouter 路由器 | ✅ |
| 4 | CapabilityInstanceRegistry 注册表 | ✅ |
| 5 | CapabilityMappingService 映射服务 | ✅ |
| 6 | 配置检验接口 | ✅ |

---

## 七、联系方式

如有问题，请在项目仓库提交 Issue 或联系项目维护者。

---

**文档状态**: 待确认  
**下一步**: Skills Team 确认配置调整方案
