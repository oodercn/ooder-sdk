# Skills 团队协作排查文档

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **发起团队**: Engine Team (scene-engine)  
> **目标团队**: Skills Team  
> **状态**: 待处理

---

## 一、排查概述

### 1.1 排查目的

验证 `E:\github\ooder-skills\skills` 目录下的技能配置是否符合 SE v3.0 规范，确保技能能够被正确发现、安装和激活。

### 1.2 排查范围

| 目录 | 技能数量 | 说明 |
|------|----------|------|
| `_drivers/llm/` | 5个 | LLM驱动技能 |
| `_drivers/media/` | 5个 | 媒体驱动技能 |
| `_drivers/org/` | 5个 | 组织驱动技能 |
| `_drivers/payment/` | 3个 | 支付驱动技能 |
| `_drivers/vfs/` | 6个 | 存储驱动技能 |
| `_system/` | 4个 | 系统技能 |
| `capabilities/` | 20+个 | 能力技能 |
| `scenes/` | 8个 | 场景技能 |
| `tools/` | 3个 | 工具技能 |
| **总计** | **约60个** | |

---

## 二、发现的问题

### 2.1 分类体系未更新 (高优先级)

**问题描述**: 大部分技能未使用新的三维分类体系

**影响**: 技能无法被正确分类和过滤

**需要修改**:

```yaml
# 旧配置 (当前状态)
metadata:
  type: scene-skill  # 旧分类

# 新配置 (目标状态)
metadata:
  skillForm: SCENE          # STANDALONE / SCENE
  sceneType: AUTO           # AUTO / TRIGGER (仅SCENE)
  visibility: public        # public / internal
```

**涉及技能**: 预估 90% 以上的技能需要更新

---

### 2.2 缺少 capabilityAddresses 配置 (高优先级)

**问题描述**: 技能未声明所需的能力地址

**影响**: SE 无法验证技能依赖，无法正确安装

**需要添加**:

```yaml
spec:
  capabilityAddresses:
    required:
      - address: 0x30         # KNOW_VECTOR
        name: "向量知识库"
        fallback: null
      - address: 0x28         # LLM_OLLAMA
        name: "LLM服务"
        fallback: 0x29        # 降级地址
    optional:
      - address: 0x4A         # COMM_EMAIL
        name: "邮件服务"
        skipable: true
```

**能力地址参考表**:

| 能力类型 | 地址 | 代码 | 说明 |
|----------|:----:|------|------|
| **知识库** | 0x30 | KNOW_VECTOR | 向量知识库 |
| | 0x31 | KNOW_DOCUMENT | 文档知识库 |
| | 0x32 | KNOW_GRAPH | 图谱知识库 |
| | 0x34 | KNOW_EMBEDDING | 嵌入服务 |
| **LLM** | 0x28 | LLM_OLLAMA | Ollama本地模型 |
| | 0x29 | LLM_OPENAI | OpenAI API |
| | 0x2A | LLM_QIANWEN | 通义千问 |
| **数据库** | 0x20 | DB_SQLITE | SQLite |
| | 0x21 | DB_MYSQL | MySQL |
| | 0x24 | DB_REDIS | Redis |
| **通讯** | 0x48 | COMM_MSG | 消息服务 |
| | 0x49 | COMM_MQTT | MQTT服务 |
| | 0x4A | COMM_EMAIL | 邮件服务 |
| **组织** | 0x08 | ORG_LOCAL | 本地组织 |
| | 0x09 | ORG_DINGDING | 钉钉组织 |

---

### 2.3 缺少 skills.md LLM 文档 (中优先级)

**问题描述**: 大部分技能缺少 `skills.md` 文件

**影响**: LLM 无法获取技能知识，影响智能问答和场景激活

**需要添加**: 每个技能目录下创建 `skills.md`

**文档结构**:

```markdown
# 技能名称

## 概述
技能的简要描述，用于 LLM 理解技能用途。

## 能力列表

### capability-id-1
- **名称**: 能力1
- **描述**: 能力1的详细描述
- **输入参数**:
  - `param1` (string, required): 参数1描述
  - `param2` (number, optional): 参数2描述
- **输出**: 输出描述

### capability-id-2
...

## 知识库

### 基础知识
基础使用说明...

### 高级知识
高级配置说明...

### 专家知识
专家级使用技巧...
```

---

### 2.4 skill-index.yaml 未更新 (高优先级)

**问题描述**: skill-index.yaml 中的技能条目缺少新分类字段

**影响**: 技能发现服务无法正确返回分类信息

**需要更新**:

```yaml
# 旧配置
- skillId: skill-knowledge-qa
  name: 知识问答
  category: knowledge
  type: abs

# 新配置
- skillId: skill-knowledge-qa
  name: 知识问答
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  addressRequired: [0x30, 0x28, 0x34]
  addressOptional: [0x4A]
```

---

## 三、分类建议

### 3.1 技能分类矩阵

| 技能 | skillForm | sceneType | visibility | 必需地址 |
|------|-----------|-----------|------------|----------|
| **知识问答** | SCENE | AUTO | public | 0x30, 0x28, 0x34 |
| **LLM对话** | SCENE | AUTO | public | 0x28 |
| **日志汇报** | SCENE | TRIGGER | public | 0x08, 0x20, 0x28, 0x48, 0x68 |
| **系统监控** | SCENE | AUTO | internal | 0x50, 0x06 |
| **LLM-Ollama** | STANDALONE | - | public | - |
| **存储-MinIO** | STANDALONE | - | public | - |
| **组织-钉钉** | STANDALONE | - | public | - |

### 3.2 分类规则说明

```
┌─────────────────────────────────────────────────────────────────┐
│                        分类选择指南                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. SkillForm 选择                                              │
│  ├── STANDALONE: 单一功能技能，如 LLM驱动、存储驱动              │
│  └── SCENE: 复杂业务场景，如知识问答、日志汇报                   │
│                                                                 │
│  2. SceneType 选择 (仅 SCENE)                                   │
│  ├── AUTO: 自动运行，无需用户触发，如知识问答                    │
│  └── TRIGGER: 需要用户触发，如日志汇报、会议记录                 │
│                                                                 │
│  3. Visibility 选择                                             │
│  ├── public: 用户可见，可安装使用                                │
│  └── internal: 后台运行，用户不可见，如系统监控                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、修复任务清单

### 4.1 P0 - 必须修复 (阻塞安装)

| 序号 | 任务 | 涉及技能 | 预计工时 |
|:----:|------|----------|:--------:|
| 1 | 添加 skillForm/sceneType/visibility | 所有技能 | 2天 |
| 2 | 添加 capabilityAddresses | 所有场景技能 | 2天 |
| 3 | 更新 skill-index.yaml | 索引文件 | 0.5天 |

### 4.2 P1 - 重要修复 (影响功能)

| 序号 | 任务 | 涉及技能 | 预计工时 |
|:----:|------|----------|:--------:|
| 4 | 创建 skills.md | 所有技能 | 3天 |
| 5 | 验证依赖配置 | 所有技能 | 1天 |

### 4.3 P2 - 优化改进 (提升体验)

| 序号 | 任务 | 涉及技能 | 预计工时 |
|:----:|------|----------|:--------:|
| 6 | 添加 fallback 配置 | 关键技能 | 1天 |
| 7 | 完善资源声明 | 所有技能 | 0.5天 |

---

## 五、验证工具

### 5.1 测试程序

Engine Team 提供了技能验证测试程序：

**文件位置**: 
- `scene-engine/src/test/java/net/ooder/scene/skill/validation/SkillValidationRunner.java`

**运行方式**:
```bash
cd E:\github\ooder-sdk\scene-engine
mvn compile test-compile exec:java \
  -Dexec.mainClass="net.ooder.scene.skill.validation.SkillValidationRunner"
```

**验证内容**:
1. 扫描所有 skill.yaml
2. 检查新分类字段
3. 验证 capabilityAddresses
4. 检查 skills.md 存在性
5. 生成分类统计报告

### 5.2 验证报告示例

```
========================================
      Skills 配置验证测试 v3.0
========================================
扫描路径: E:ithuboder-skillskills

【步骤1】扫描技能目录...
发现 60 个技能配置文件

【步骤2】验证 skill-index.yaml...
  skill-index.yaml 包含 50 个技能
  - 包含新分类字段: 5 个
  - 未包含新分类字段: 45 个 (需要更新)

【步骤3】验证技能配置...
  ...

【步骤4】生成分类列表...
分类统计:
  STANDALONE (独立技能): 25 个
  SCENE_AUTO (自驱场景): 3 个
  SCENE_TRIGGER (触发场景): 2 个
  INTERNAL (内部技能): 0 个
  UNKNOWN (未分类): 30 个

========================================
          验证报告
========================================
总技能数: 60
有效配置: 5 个
有警告: 25 个
有错误: 30 个

【改进建议】
1. 为所有技能添加新分类字段
2. 添加 capabilityAddresses 配置
3. 完善 LLM 文档 (skills.md)
```

---

## 六、协作流程

### 6.1 修复流程

```
┌─────────────────────────────────────────────────────────────────┐
│                        修复协作流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. Skills Team 拉取最新代码                                     │
│     git pull origin main                                        │
│                                                                 │
│  2. 创建修复分支                                                 │
│     git checkout -b fix/skill-config-v3                         │
│                                                                 │
│  3. 按优先级修复配置                                             │
│     - P0: 添加分类字段和 capabilityAddresses                     │
│     - P1: 创建 skills.md                                         │
│                                                                 │
│  4. 本地验证                                                     │
│     运行 SkillValidationRunner 验证                              │
│                                                                 │
│  5. 提交 PR                                                      │
│     git push origin fix/skill-config-v3                         │
│                                                                 │
│  6. Engine Team 审核                                             │
│     - 检查配置规范性                                             │
│     - 验证地址分配合理性                                         │
│                                                                 │
│  7. 合并并更新索引                                               │
│     git merge fix/skill-config-v3                               │
│     更新 skill-index.yaml                                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 沟通渠道

| 事项 | 联系方式 |
|------|----------|
| **技术问题** | 在 scene-engine 仓库提交 Issue |
| **规范确认** | 参考 CAPABILITY_ADDRESS_SPACE_DESIGN.md |
| **紧急问题** | 联系 Engine Team 负责人 |

---

## 七、参考文档

| 文档 | 路径 |
|------|------|
| 能力地址设计 | `scene-engine/docs/CAPABILITY_ADDRESS_SPACE_DESIGN.md` |
| 分类过滤指南 | `scene-engine/docs/SKILL_CLASSIFICATION_AND_FILTER_GUIDE.md` |
| 配置调整说明 | `scene-engine/docs/SKILLS_CONFIG_ADJUSTMENT_SPEC.md` |
| 协作声明 | `scene-engine/docs/CAPABILITY_ADDRESS_COLLABORATION.md` |

---

## 八、时间节点

| 里程碑 | 日期 | 交付物 |
|--------|------|--------|
| **P0 完成** | 2026-03-18 | 所有技能添加新分类字段 |
| **P1 完成** | 2026-03-25 | 所有技能创建 skills.md |
| **联合测试** | 2026-03-28 | 通过 SkillValidationRunner 验证 |
| **正式发布** | 2026-04-01 | 技能市场 v3.0 上线 |

---

**文档状态**: 已发布  
**下一步**: Skills Team 开始 P0 任务修复
