# 场景组 2.3.1 澄清问题与答案确认

## 1. 概述

本文档按场景技能分类整理澄清问题，并给出建议答案供确认。

---

## 2. 场景技能分类体系

### 2.1 技能分类维度

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           技能分类体系（三维正交）                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   维度1: 形态 (SkillForm)          维度2: 场景类型 (SceneType)              │
│   ┌─────────────────────┐         ┌─────────────────────┐                  │
│   │ SCENE (场景技能)     │         │ AUTO (自主场景)      │                  │
│   │   - 容器型           │ ────────│ TRIGGER (触发场景)   │                  │
│   │   - 可嵌套           │  仅当    │ HYBRID (混合场景)    │                  │
│   │   - 有结构           │  SCENE   └─────────────────────┘                  │
│   │                     │                                                    │
│   │ STANDALONE (独立技能)│                                                    │
│   │   - 原子型           │                                                    │
│   │   - 不可再分         │                                                    │
│   └─────────────────────┘                                                    │
│                                                                             │
│   维度3: 分类 (SkillCategory)                                                │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │ KNOWLEDGE │ LLM │ TOOL │ WORKFLOW │ DATA │ SERVICE │ UI │ OTHER    │   │
│   │  知识类   │ AI  │ 工具 │  流程    │ 数据 │  服务   │界面│  其他    │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
```

### 2.2 分类组合示例

| 组合 | 形态 | 场景类型 | 分类 | 示例 |
|------|------|----------|------|------|
| 周报场景 | SCENE | AUTO | KNOWLEDGE | 自动收集、生成周报 |
| 审批流程 | SCENE | TRIGGER | WORKFLOW | 人工触发审批 |
| 智能客服 | SCENE | HYBRID | LLM | 主动推送+被动应答 |
| 计算器 | STANDALONE | - | TOOL | 独立工具 |

---

## 3. 澄清问题与建议答案

### 3.1 场景技能与场景组关系（按分类）

#### 3.1.1 AUTO 场景（自主场景）

| 问题 | 建议答案 | 说明 |
|------|----------|------|
| **Q-AUTO-001**: 场景技能激活时是否一定创建场景组？ | **是** | AUTO 场景激活后立即创建场景组并进入 ACTIVE 状态 |
| **Q-AUTO-002**: 一个场景技能可以创建多个场景组吗？ | **是** | 一个模板可创建多个实例，如多个团队的周报场景组 |
| **Q-AUTO-003**: 场景组的 templateId 是否等于场景技能的 skillId？ | **是** | templateId = skillId，用于关联模板 |
| **Q-AUTO-004**: 场景组初始状态是什么？ | **ACTIVE** | AUTO 场景立即激活，无需等待外部触发 |
| **Q-AUTO-005**: 是否立即启动心跳？ | **是** | 心跳由场景组自主维护 |
| **Q-AUTO-006**: 是否注册定时任务？ | **是** | 根据场景配置自动注册定时任务 |
| **Q-AUTO-007**: 是否注册事件监听？ | **是** | 根据场景配置自动注册事件监听 |
| **Q-AUTO-008**: 生命周期由谁控制？ | **自身控制** | 场景组自主管理生命周期 |

**AUTO 场景激活流程**:
```
场景技能(AUTO) → 安装 → 激活 → 创建场景组 → ACTIVE
                              ├── 启动心跳
                              ├── 注册定时任务
                              └── 注册事件监听
```

#### 3.1.2 TRIGGER 场景（触发场景）

| 问题 | 建议答案 | 说明 |
|------|----------|------|
| **Q-TRIGGER-001**: 场景技能激活时是否一定创建场景组？ | **是** | TRIGGER 场景激活后创建场景组，但状态为 CREATED |
| **Q-TRIGGER-002**: 一个场景技能可以创建多个场景组吗？ | **是** | 一个模板可创建多个实例 |
| **Q-TRIGGER-003**: 场景组的 templateId 是否等于场景技能的 skillId？ | **是** | templateId = skillId |
| **Q-TRIGGER-004**: 场景组初始状态是什么？ | **CREATED** | 等待外部触发才进入 ACTIVE |
| **Q-TRIGGER-005**: 是否立即启动心跳？ | **否** | 触发时才启动心跳 |
| **Q-TRIGGER-006**: 是否注册定时任务？ | **否** | 无自驱能力 |
| **Q-TRIGGER-007**: 是否注册事件监听？ | **否** | 无自驱能力 |
| **Q-TRIGGER-008**: 是否需要触发入口？ | **是** | 必须注册 API/事件触发入口 |
| **Q-TRIGGER-009**: 生命周期由谁控制？ | **调用方控制** | 由外部触发方控制 |

**TRIGGER 场景激活流程**:
```
场景技能(TRIGGER) → 安装 → 激活 → 创建场景组 → CREATED
                              ├── 注册触发入口
                              └── 等待外部触发
                                        ↓
                              外部触发 → ACTIVE → 执行 → SUSPENDED
```

#### 3.1.3 HYBRID 场景（混合场景）

| 问题 | 建议答案 | 说明 |
|------|----------|------|
| **Q-HYBRID-001**: 场景技能激活时是否一定创建场景组？ | **是** | HYBRID 场景激活后创建场景组 |
| **Q-HYBRID-002**: 一个场景技能可以创建多个场景组吗？ | **是** | 一个模板可创建多个实例 |
| **Q-HYBRID-003**: 场景组的 templateId 是否等于场景技能的 skillId？ | **是** | templateId = skillId |
| **Q-HYBRID-004**: 场景组初始状态是什么？ | **可配置** | 根据配置决定 ACTIVE 或 CREATED |
| **Q-HYBRID-005**: 是否立即启动心跳？ | **可选** | 根据配置决定 |
| **Q-HYBRID-006**: 是否注册定时任务？ | **可选** | 根据配置决定 |
| **Q-HYBRID-007**: 是否注册事件监听？ | **可选** | 根据配置决定 |
| **Q-HYBRID-008**: 是否需要触发入口？ | **是** | 必须注册触发入口，支持被动触发 |
| **Q-HYBRID-009**: 生命周期由谁控制？ | **灵活控制** | 可在自主和被动间切换 |

**HYBRID 场景激活流程**:
```
场景技能(HYBRID) → 安装 → 激活 → 创建场景组 → 根据配置
                              ├── [startAsAuto=true]
                              │   ├── ACTIVE
                              │   ├── 启动心跳
                              │   ├── 注册定时任务
                              │   └── 注册事件监听
                              └── [startAsAuto=false]
                                  ├── CREATED
                                  └── 注册触发入口
                              
                              同时支持外部触发
```

---

## 4. ID 规则（通用设定）

### 4.1 ID 生成规则

| ID 类型 | 格式 | 示例 | 说明 |
|---------|------|------|------|
| **skillId** | `{namespace}.{name}` | `com.ooder.weekly-report` | 技能唯一标识，命名空间+名称 |
| **sceneGroupId** | `{skillId}-{timestamp}-{random}` | `com.ooder.weekly-report-1710844800000-a1b2` | 场景组唯一标识 |
| **templateId** | = skillId | `com.ooder.weekly-report` | 模板ID等于技能ID |
| **participantId** | `{sceneGroupId}-p-{index}` | `com.ooder.weekly-report-...-p-001` | 参与者ID |
| **bindingId** | `{sceneGroupId}-b-{type}-{index}` | `...-b-cap-001` | 绑定ID |
| **snapshotId** | `{sceneGroupId}-s-{timestamp}` | `...-s-1710844800000` | 快照ID |

### 4.2 ID 生成时机

```
场景技能安装
    │
    ├── skillId ──────────────→ 开发时定义（skill.yaml）
    │
    └── 场景技能激活
            │
            ├── sceneGroupId ──→ 激活时生成（SDK SceneGroup 创建）
            │
            ├── templateId ────→ = skillId（直接赋值）
            │
            └── 参与者加入
                    │
                    ├── participantId → 加入时生成
                    └── bindingId ────→ 绑定时生成
```

### 4.3 ID 共享规则

| 层级 | ID | 共享说明 |
|------|-----|----------|
| **SDK SceneGroup** | sceneGroupId | 生成后传递给 SE SceneGroup |
| **SE SceneGroup** | sceneGroupId | 复用 SDK 生成的 ID |
| **Agent** | agentId | 映射到 Participant.userId |
| **Participant** | userId | = agentId |

---

## 5. 同样场景（模板）概念梳理

### 5.1 核心概念定义

**模板 (Template)** = 场景技能的定义

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              模板 = 场景技能                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   模板定义（开发时）                        场景组实例（运行时）              │
│   ─────────────────────                   ─────────────────────            │
│                                                                             │
│   SkillPackage                            SceneGroup                        │
│   ├── skillId (模板ID)                    ├── sceneGroupId (实例ID)        │
│   ├── name                                ├── templateId (= skillId)       │
│   ├── form: SCENE                         ├── name (可自定义)              │
│   ├── sceneType: AUTO/TRIGGER/HYBRID      ├── status                       │
│   ├── category: KNOWLEDGE/LLM/...         ├── participants                 │
│   ├── sceneStructure:                     ├── capabilityBindings           │
│   │   ├── internalCapabilities            │   └── 来自模板定义              │
│   │   ├── childSkills                     ├── knowledgeBindings            │
│   │   └── orchestration                   └── config                        │
│   └── purposes: [TEAM, PERIODIC, ...]                                       │
│                                                                             │
│   类比：                                                                    │
│   模板 = 类定义 (Class)                                                     │
│   场景组 = 实例对象 (Instance)                                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 模板与实例的关系

```
模板 (skillId: com.ooder.weekly-report)
│
├── 实例1: sceneGroupId = com.ooder.weekly-report-1710844800000-a1b2
│   ├── name: "研发团队周报"
│   ├── templateId: com.ooder.weekly-report
│   ├── participants: [张三, 李四, 王五]
│   └── status: ACTIVE
│
├── 实例2: sceneGroupId = com.ooder.weekly-report-1710844900000-c3d4
│   ├── name: "产品团队周报"
│   ├── templateId: com.ooder.weekly-report
│   ├── participants: [赵六, 钱七]
│   └── status: ACTIVE
│
└── 实例3: sceneGroupId = com.ooder.weekly-report-1710845000000-e5f6
    ├── name: "运营团队周报"
    ├── templateId: com.ooder.weekly-report
    ├── participants: [孙八, 周九, 吴十]
    └── status: CREATED (等待触发)
```

### 5.3 模板索引机制

```java
// SceneGroupManager 中的模板索引
private final Map<String, List<SceneGroup>> templateIndex = new ConcurrentHashMap<>();

// 通过模板ID获取所有实例
public List<SceneGroup> getSceneGroupsByTemplate(String templateId) {
    return templateIndex.getOrDefault(templateId, Collections.emptyList());
}
```

**索引结构**:
```
templateIndex
│
├── "com.ooder.weekly-report" → [实例1, 实例2, 实例3]
├── "com.ooder.meeting-room"  → [实例A, 实例B]
└── "com.ooder.approval"      → [实例X]
```

### 5.4 模板能力继承

| 模板定义 | 场景组继承 | 可覆盖 |
|----------|------------|--------|
| sceneType | ✅ 继承 | ❌ 不可覆盖 |
| category | ✅ 继承 | ❌ 不可覆盖 |
| internalCapabilities | ✅ 继承 | ❌ 不可覆盖 |
| childSkills | ✅ 继承 | ❌ 不可覆盖 |
| orchestration | ✅ 继承 | ❌ 不可覆盖 |
| name | ✅ 继承 | ✅ 可覆盖 |
| config | ✅ 继承 | ✅ 可覆盖 |
| participants | ❌ 不继承 | ✅ 运行时添加 |

### 5.5 模板版本管理

```
模板版本演进：
com.ooder.weekly-report
├── v1.0.0 → 初始版本
├── v1.1.0 → 增加子技能
└── v2.0.0 → 重构编排逻辑

场景组实例版本：
sceneGroupId: com.ooder.weekly-report-1710844800000-a1b2
└── 创建时锁定模板版本: v1.0.0
    └── 即使模板升级到 v2.0.0，实例仍使用 v1.0.0
```

**版本锁定规则**:
- 场景组创建时锁定模板版本
- 模板升级不影响已创建的场景组
- 如需升级，需手动触发"升级实例"操作

---

## 6. 确认清单

### 6.1 请确认以下理解

| 序号 | 确认项 | 建议答案 | 您的确认 |
|------|--------|----------|----------|
| 1 | AUTO 场景激活后立即进入 ACTIVE 状态 | ✅ 是 | ☐ 确认 / ☐ 否 |
| 2 | TRIGGER 场景激活后保持 CREATED 状态 | ✅ 是 | ☐ 确认 / ☐ 否 |
| 3 | HYBRID 场景初始状态可配置 | ✅ 是 | ☐ 确认 / ☐ 否 |
| 4 | templateId = skillId | ✅ 是 | ☐ 确认 / ☐ 否 |
| 5 | 一个模板可创建多个场景组实例 | ✅ 是 | ☐ 确认 / ☐ 否 |
| 6 | sceneGroupId 由 SDK 生成，SE 复用 | ✅ 是 | ☐ 确认 / ☐ 否 |
| 7 | 场景组创建时锁定模板版本 | ✅ 是 | ☐ 确认 / ☐ 否 |
| 8 | Agent.agentId = Participant.userId | ✅ 是 | ☐ 确认 / ☐ 否 |
| 9 | PRIMARY → OWNER/MANAGER | ✅ 是 | ☐ 确认 / ☐ 否 |
| 10 | BACKUP → EMPLOYEE/OBSERVER | ✅ 是 | ☐ 确认 / ☐ 否 |

### 6.2 已确认项

| 序号 | 确认项 | 确认答案 | 说明 |
|------|--------|----------|------|
| 1 | 场景组是否需要持久化？ | ✅ **是，必须持久化** | P0 优先级，核心功能 |
| 2 | 模板升级是否自动同步到实例？ | ❌ 否，需手动触发 | 保持实例稳定性 |
| 3 | 场景组参与者上限？ | 无硬性限制 | 由业务层控制 |
| 4 | 快照恢复是否支持回滚？ | ✅ 支持 | 支持归档恢复 |

---

## 7. 场景组持久化与归档

### 7.1 持久化方案

**存储方式**: 文件存储（JSON/YAML）

```
存储目录结构：
~/.ooder/
├── scene-groups/
│   ├── {sceneGroupId}/
│   │   ├── metadata.yaml          # 场景组元数据
│   │   ├── participants.yaml      # 参与者列表
│   │   ├── bindings/
│   │   │   ├── capabilities.yaml  # 能力绑定
│   │   │   └── knowledge.yaml     # 知识库绑定
│   │   ├── config.yaml            # 场景组配置
│   │   └── archives/              # 归档目录
│   │       ├── archive-20260319-001.yaml
│   │       └── archive-20260320-001.yaml
│   └── index.yaml                 # 场景组索引
```

### 7.2 持久化数据结构

**metadata.yaml**:
```yaml
sceneGroupId: com.ooder.weekly-report-1710844800000-a1b2
templateId: com.ooder.weekly-report
templateVersion: 1.0.0
name: 研发团队周报
description: 研发团队周报场景组
status: ACTIVE
creatorId: user-001
creatorType: USER
createTime: 2026-03-19T10:00:00Z
lastUpdateTime: 2026-03-19T15:30:00Z
sceneType: AUTO
category: KNOWLEDGE
```

**participants.yaml**:
```yaml
participants:
  - participantId: com.ooder.weekly-report-...-p-001
    userId: user-001
    name: 张三
    type: USER
    role: OWNER
    status: ACTIVE
    joinTime: 2026-03-19T10:00:00Z
  - participantId: com.ooder.weekly-report-...-p-002
    userId: user-002
    name: 李四
    type: USER
    role: EMPLOYEE
    status: ACTIVE
    joinTime: 2026-03-19T10:05:00Z
```

**config.yaml**:
```yaml
config:
  llm:
    provider: openai
    model: gpt-4
    temperature: 0.7
    maxTokens: 2048
  scheduler:
    cronExpression: "0 0 17 ? * FRI"
    enabled: true
  reminder:
    enabled: true
    advanceMinutes: 30
```

### 7.3 归档机制

**归档定义**: 用户可自行归档场景实例，归档时仅归档运行数据，保留参与者和技能绑定。

```
归档前：
┌─────────────────────────────────────────────────────────────┐
│  SceneGroup                                                 │
│  ├── metadata (元数据)                                      │
│  ├── participants (参与者) ←─────────────────┐ 保持         │
│  ├── capabilityBindings (能力绑定) ←─────────┤ 保持         │
│  ├── knowledgeBindings (知识库绑定) ←────────┤ 保持         │
│  ├── config (配置)                          │              │
│  ├── runtimeData (运行数据) ─────────────────┼──→ 归档     │
│  │   ├── 执行历史                           │              │
│  │   ├── 中间状态                           │              │
│  │   ├── 临时变量                           │              │
│  │   └── 会话上下文                         │              │
│  └── status: ACTIVE → ARCHIVED              │              │
└─────────────────────────────────────────────────────────────┘
```

**归档操作流程**:
```
用户触发归档
    │
    ├── 1. 检查场景组状态（必须是 ACTIVE 或 SUSPENDED）
    │
    ├── 2. 创建归档文件
    │       ├── archive-{timestamp}.yaml
    │       └── 包含运行数据快照
    │
    ├── 3. 清理运行数据
    │       ├── 清空执行历史
    │       ├── 清空中间状态
    │       └── 清空临时变量
    │
    ├── 4. 保持结构数据
    │       ├── participants 保持不变
    │       ├── capabilityBindings 保持不变
    │       └── knowledgeBindings 保持不变
    │
    └── 5. 更新状态
            └── status: ACTIVE → ARCHIVED
```

**归档文件格式**:
```yaml
archiveId: archive-20260319-001
sceneGroupId: com.ooder.weekly-report-1710844800000-a1b2
archiveTime: 2026-03-19T18:00:00Z
archiveType: USER_INITIATED
runtimeData:
  executionHistory:
    - stepId: step-001
      action: collect-reports
      status: completed
      timestamp: 2026-03-19T17:00:00Z
    - stepId: step-002
      action: generate-summary
      status: completed
      timestamp: 2026-03-19T17:30:00Z
  sessionContext:
    lastPrompt: "生成本周周报"
    responseTokens: 1500
  temporaryState:
    collectedData: [...]
    processedCount: 5
```

### 7.4 归档恢复

```
归档恢复流程：
用户选择归档文件
    │
    ├── 1. 验证归档文件完整性
    │
    ├── 2. 检查场景组当前状态
    │       └── 必须是 ARCHIVED 状态
    │
    ├── 3. 恢复运行数据
    │       ├── 恢复执行历史
    │       ├── 恢复会话上下文
    │       └── 恢复临时状态
    │
    └── 4. 更新状态
            └── status: ARCHIVED → ACTIVE
```

---

## 8. 场景组配置初始化（集成附属 Skill）

### 8.1 附属 Skill 默认配置集成

**关键说明**: 场景组初始化时，需要集成附属 Skill 的默认配置，特别是 `skill-llm-conversation`（第17项）的默认设定。

**附属 Skill 列表**:
| 序号 | Skill ID | 类型 | 说明 | 配置集成 |
|------|----------|------|------|----------|
| 1 | skill-org-base | PROVIDER | 组织基础 | 组织架构配置 |
| 2 | skill-org-dingding | PROVIDER | 钉钉组织 | 钉钉API配置 |
| 3 | skill-org-feishu | PROVIDER | 飞书组织 | 飞书API配置 |
| 4 | skill-vfs-base | PROVIDER | 虚拟文件系统基础 | VFS配置 |
| 5 | skill-vfs-local | PROVIDER | 本地文件系统 | 本地路径配置 |
| 6 | skill-user-auth | PROVIDER | 用户认证 | 认证配置 |
| 7 | skill-email | PROVIDER | 邮件服务 | SMTP配置 |
| 8 | skill-msg | PROVIDER | 消息服务 | 消息配置 |
| 9 | skill-knowledge-base | PROVIDER | 知识库服务 | 知识库配置 |
| 10 | skill-rag | PROVIDER | RAG服务 | RAG配置 |
| 11-16 | ... | PROVIDER | 其他能力 | ... |
| **17** | **skill-llm-conversation** | **PROVIDER** | **LLM对话服务** | **LLM配置（重点）** |

### 8.2 skill-llm-conversation 默认配置

**配置优先级**（从低到高）:
```
Level 1: 系统默认配置 (classpath:skills/skill-llm-conversation/)
Level 2: 环境配置 (config/env/{env}/skills/skill-llm-conversation/)
Level 3: 应用配置 (config/app/skills/skill-llm-conversation/)
Level 4: 用户配置 (~/.ooder/skills/skill-llm-conversation/)
Level 5: 场景组配置 (场景组实例级别)
```

**默认配置项**:
```yaml
llm:
  provider: openai
  model: gpt-4
  temperature: 0.7
  maxTokens: 2048
  topP: 1.0
  frequencyPenalty: 0.0
  presencePenalty: 0.0
  
conversation:
  maxHistoryLength: 20
  contextWindow: 4096
  systemPrompt: ""
  responseFormat: text
  
streaming:
  enabled: true
  chunkSize: 100
  
retry:
  maxAttempts: 3
  backoffMs: 1000
  
rateLimit:
  requestsPerMinute: 60
  tokensPerMinute: 100000
```

### 8.3 场景组初始化配置集成流程

```
场景组初始化
│
├── 1. 加载模板定义
│       └── 获取 sceneStructure.childSkills
│
├── 2. 加载附属 Skill 默认配置
│       │
│       ├── 遍历 childSkills
│       │       │
│       │       ├── [如果是 skill-llm-conversation]
│       │       │       │
│       │       │       ├── 加载系统默认配置
│       │       │       ├── 加载环境配置
│       │       │       ├── 加载应用配置
│       │       │       └── 合并为场景组默认配置
│       │       │
│       │       └── [其他附属 Skill]
│       │               └── 同样流程加载配置
│       │
│       └── 合并所有附属 Skill 配置
│
├── 3. 应用场景组级别配置覆盖
│       └── 用户自定义配置覆盖默认值
│
└── 4. 持久化配置
        └── 保存到 config.yaml
```

### 8.4 配置集成代码示例

```java
public class SceneGroupConfigInitializer {
    
    private final LayeredConfigLoader configLoader;
    
    public SceneGroupConfig initializeConfig(SkillPackage template, 
                                              Map<String, Object> userConfig) {
        SceneGroupConfig config = new SceneGroupConfig();
        
        // 1. 加载附属 Skill 配置
        Optional<SceneStructure> structure = template.getSceneStructure();
        if (structure.isPresent()) {
            for (Skill childSkill : structure.get().getChildSkills()) {
                String skillId = childSkill.getSkillId();
                
                // 特别处理 skill-llm-conversation
                if ("skill-llm-conversation".equals(skillId)) {
                    LlmConfigProperties llmConfig = configLoader.load(skillId);
                    config.setLlmConfig(llmConfig);
                }
                
                // 其他附属 Skill 配置加载...
            }
        }
        
        // 2. 应用用户配置覆盖
        if (userConfig != null) {
            config.merge(userConfig);
        }
        
        return config;
    }
}
```

### 8.5 配置集成注意事项

**⚠️ 重要警告**:
1. **严格初始化顺序**: 必须按照 Level 1 → Level 5 的顺序加载配置
2. **配置覆盖规则**: 高优先级配置覆盖低优先级配置
3. **类型安全**: 配置值必须符合预期类型，否则使用默认值
4. **必填项检查**: 某些配置项（如 apiKey）可能需要运行时提供

**配置验证**:
```java
public void validateConfig(SceneGroupConfig config) {
    // 必填项检查
    if (config.getLlmConfig() != null) {
        LlmConfigProperties llm = config.getLlmConfig();
        if (llm.getProvider() == null) {
            throw new ConfigException("LLM provider is required");
        }
        if (llm.getModel() == null) {
            throw new ConfigException("LLM model is required");
        }
    }
    
    // 类型检查
    if (config.get("temperature") != null) {
        if (!(config.get("temperature") instanceof Number)) {
            throw new ConfigException("temperature must be a number");
        }
    }
}
```

---

## 9. 附录：完整映射关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           完整映射关系图                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   模板层 (Template Layer)                                                   │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  SkillPackage (场景技能定义)                                        │   │
│   │  ├── skillId ──────────────────────────────────────────────────────┐  │   │
│   │  ├── name                                                         │  │   │
│   │  ├── form: SCENE                                                 │  │   │
│   │  ├── sceneType: AUTO/TRIGGER/HYBRID                               │  │   │
│   │  ├── category: KNOWLEDGE/LLM/TOOL/WORKFLOW/...                   │  │   │
│   │  ├── sceneStructure:                                              │  │   │
│   │  │   ├── internalCapabilities ──────────────────────────────────────┼──┼──┐
│   │  │   ├── childSkills                                             │  │  │
│   │  │   └── orchestration                                           │  │  │
│   │  └── purposes: [TEAM/PERSONAL, PERIODIC/INSTANT, PROACTIVE/REACTIVE]  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                              │                              │
│                                              │ 激活                         │
│                                              ▼                              │
│   实例层 (Instance Layer)                                                   │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │  SE SceneGroup (业务场景组)                                         │  │
│   │  ├── sceneGroupId ←──────────────────────────────────────────────┐ │  │
│   │  ├── templateId ←────────────────────────────────────────────────┼─┘  │
│   │  ├── name (可自定义)                                               │  │   │
│   │  ├── status (根据 sceneType 决定初始状态)                          │  │   │
│   │  ├── capabilityBindings ←──────────────────────────────────────────┼────┘
│   │  ├── knowledgeBindings                                             │     │
│   │  ├── participants                                                  │     │
│   │  │   └── Participant                                               │     │
│   │  │       ├── participantId                                         │     │
│   │  │       ├── userId ←── Agent.agentId                              │     │
│   │  │       ├── type: USER/AGENT/SUPER_AGENT                          │     │
│   │  │       └── role: OWNER/MANAGER/EMPLOYEE/OBSERVER                 │     │
│   │  └── snapshots                                                    │     │
│   └─────────────────────────────────────────────────────────────────────┘     │
│                                              │                              │
│                                              │ 关联                         │
│                                              ▼                              │
│   基础设施层 (Infrastructure Layer)                                         │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  SDK SceneGroup (高可用集群)                                        │   │
│   │  ├── sceneGroupId ───────────────────────────────────────────────┐ │   │
│   │  ├── sceneId (= templateId)                                       │ │   │
│   │  ├── members                                                      │ │   │
│   │  │   └── SceneMember                                              │ │   │
│   │  │       ├── agentId ─────────────────────────────────────────────┼─┼───┐
│   │  │       ├── role: PRIMARY/BACKUP                                 │ │   │
│   │  │       └── endpoint                                             │ │   │
│   │  ├── key                                                          │ │   │
│   │  └── sharedState                                                  │ │   │
│   └─────────────────────────────────────────────────────────────────────┘ │   │
│                                              │                              │   │
│                                              │ 关联                         │   │
│                                              ▼                              │   │
│   Agent层 (Agent Layer)                                                     │   │
│   ┌─────────────────────────────────────────────────────────────────────┐   │   │
│   │  Agent                                                              │   │   │
│   │  ├── agentId ───────────────────────────────────────────────────────┼───┘   │
│   │  ├── agentType: USER/AGENT/AI                                      │       │
│   │  ├── capabilities                                                  │       │
│   │  └── endpoint                                                      │       │
│   └─────────────────────────────────────────────────────────────────────┘       │
│                                                                                │
└────────────────────────────────────────────────────────────────────────────────┘
```

---

**文档状态**: ✅ **确认完毕**  
**最后更新**: 2026-03-19  
**版本**: 1.1  
**确认人**: 用户确认
