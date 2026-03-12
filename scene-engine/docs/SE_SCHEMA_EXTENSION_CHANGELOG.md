# SE标准扩展变更日志

## 版本: v2.4.0 / SE v1.2.0

**更新日期**: 2026-03-12  
**任务ID**: SE-EXT-2026-001

---

## 变更摘要

本次扩展为 SE (Skill Index) 标准新增了两个重要字段，以支持能力发现页面的安装向导功能：

1. **`participants`** - 参与者配置
2. **`driverConditions`** - 驱动条件配置

---

## 详细变更

### 1. 新增字段: participants (参与者配置)

**适用条件**: `skillForm == SCENE`

**用途**: 定义场景安装时的参与者配置规则，支持安装向导步骤3（配置参与者）

**Schema结构**:

```yaml
participants:
  type: object
  required: false
  properties:
    leader:           # 主导者配置 (必需)
      required: boolean           # 是否必须指定主导者 (默认: true)
      defaultToCurrentUser: boolean  # 默认为当前用户 (默认: true)
      permissions: string[]       # 主导者权限列表
    
    collaborators:    # 协作者配置 (可选)
      minCount: integer           # 最小协作者数量 (默认: 0)
      maxCount: integer           # 最大协作者数量 (默认: 10)
      selectionType: enum         # 选择类型 (默认: USER)
        values: [USER, ROLE, GROUP]
    
    pushType:         # 推送类型 (可选)
      type: enum
      values: [SHARE, INVITE, DELEGATE]
      default: SHARE
```

**示例**:

```yaml
participants:
  leader:
    required: true
    defaultToCurrentUser: true
    permissions: [ACTIVATE, CONFIGURE, INVITE, DELEGATE, DELETE]
  collaborators:
    minCount: 0
    maxCount: 10
    selectionType: USER
  pushType: INVITE
```

---

### 2. 新增字段: driverConditions (驱动条件配置)

**适用条件**: `skillForm == SCENE || skillForm == DRIVER`

**用途**: 定义场景的触发条件配置规则，支持安装向导步骤4（驱动条件）

**Schema结构**:

```yaml
driverConditions:
  type: object
  required: false
  properties:
    supportedTypes:   # 支持的触发类型 (必需)
      type: array
      itemType: enum
      values: [MANUAL, SCHEDULE, EVENT, WEBHOOK]
    
    defaultType:      # 默认触发类型 (可选)
      type: enum
      values: [MANUAL, SCHEDULE, EVENT, WEBHOOK]
      default: MANUAL
    
    scheduleConfig:   # 定时触发配置 (可选)
      cronExpression: string      # Cron表达式
      timezone: string            # 时区 (默认: "Asia/Shanghai")
    
    eventConfig:      # 事件触发配置 (可选)
      eventTypes: string[]        # 监听的事件类型
      eventSource: string         # 事件来源
```

**示例**:

```yaml
driverConditions:
  supportedTypes: [MANUAL, SCHEDULE, EVENT]
  defaultType: MANUAL
  scheduleConfig:
    cronExpression: "0 9 * * MON"
    timezone: "Asia/Shanghai"
  eventConfig:
    eventTypes: [USER_LOGIN, DATA_UPDATE]
    eventSource: "system"
```

---

## 验证规则

新增以下验证规则：

| 规则ID | 说明 | 严重程度 |
|--------|------|----------|
| `participantsLeaderRequired` | 如果配置了participants，则必须包含leader配置 | ERROR |
| `driverConditionsSupportedTypesRequired` | 如果配置了driverConditions，则必须指定supportedTypes | ERROR |
| `driverConditionsDefaultTypeValid` | defaultType必须在supportedTypes中 | ERROR |
| `participantsCollaboratorsCountValid` | 协作者最小数量不能大于最大数量 | ERROR |

---

## 兼容性说明

- **向后兼容**: 新字段均为可选字段，不影响现有技能
- **默认值处理**: 后端需要处理字段缺失时的默认值逻辑
- **前端适配**: 前端需要兼容旧版本技能（无新字段时使用默认配置）

---

## 版本更新

| 项目 | 旧版本 | 新版本 |
|------|--------|--------|
| Schema版本 | 2.3.1 | 2.4.0 |
| SE标准版本 | v1.1.0 | v1.2.0 |
| 最后更新 | 2026-03-11 | 2026-03-12 |

---

## 相关文件

1. **Schema定义**: `docs/examples/schema-extended-v2.4.0.yaml`
2. **使用示例**: `docs/examples/skill-index-with-new-fields.yaml`
3. **协作任务**: `E:\github\ooder-skills\skills\skill-scene\docs\COLLABORATION_TASK_SE_EXTENSION.md`

---

## 后续工作

根据协作任务分工，后续需要完成：

| 任务 | 负责方 | 状态 |
|------|--------|------|
| SE标准Schema扩展 | 架构组 | ✅ 已完成 |
| 后端模型更新 | 后端组 | ⏳ 待开始 |
| 前端适配 | 前端组 | ⏳ 待开始 |
| 测试验证 | 测试组 | ⏳ 待开始 |
