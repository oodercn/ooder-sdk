# Phase 2: 技能层拆分方案

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **执行团队**: Skills Team  
> **前提条件**: Phase 1 已完成 (config/ 目录已创建)  
> **SDK状态**: ✅ 已完成 (Visibility, SkillForm, CapabilityCategory 已实现)  
> **状态**: 待执行

---

## 一、当前状态确认

### 1.1 SDK 完成情况 ✅

| 枚举/模型 | 状态 | 文件路径 |
|-----------|:----:|----------|
| Visibility | ✅ | `skill-scene/.../model/Visibility.java` |
| SkillForm | ✅ | `skill-scene/.../model/SkillForm.java` |
| CapabilityCategory | ✅ | `skill-scene/.../model/CapabilityCategory.java` |

### 1.2 Phase 1 完成情况 ✅

| 文件 | 状态 | 路径 |
|------|:----:|------|
| schema.yaml | ✅ | `config/schema.yaml` |
| addresses.yaml | ✅ | `config/addresses.yaml` |
| categories.yaml | ✅ | `config/categories.yaml` |

---

## 二、Phase 2 目标

将 `skill-index.yaml` 中的技能配置拆分为独立的 `skill-index-entry.yaml` 文件。

### 2.1 拆分范围

**当前**: `skill-index.yaml` 中约 60+ 个技能定义

**目标**: 每个技能独立的 `skill-index-entry.yaml` 文件

```
当前结构:
skill-index.yaml (2000+ 行)
  └── skills: [...60+ 技能...]

目标结构:
skills/
  ├── skill-capability/
  │   └── skill-index-entry.yaml
  ├── skill-knowledge-qa/
  │   └── skill-index-entry.yaml
  ├── skill-daily-report/
  │   └── skill-index-entry.yaml
  └── ... (60+ 个技能)
```

---

## 三、技能索引条目模板

### 3.1 skill-index-entry.yaml 模板

```yaml
# skills/{skill-id}/skill-index-entry.yaml
# 技能索引配置 - SE标准 v1.1.0

apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: {skill-id}
  name: {技能名称}
  version: {版本号}
  description: {技能描述}

# SE三维分类
spec:
  skillForm: {SCENE|PROVIDER|DRIVER|INTERNAL}
  sceneType: {AUTO|TRIGGER}  # 仅SCENE时必需
  visibility: {public|developer|internal}
  
  # 业务分类
  businessCategory: {OFFICE_COLLABORATION|HUMAN_RESOURCE|AI_ASSISTANT|...}
  subCategory: {子分类}
  
  # 技术分类
  category: {KNOWLEDGE|LLM|TOOL|WORKFLOW|DATA|SERVICE|UI|OTHER}
  capabilityCategory: {sys|org|auth|vfs|db|llm|know|...}
  
  # 能力地址配置
  capabilityAddresses:
    required:
      - address: {0xXX}
        name: {地址名称}
        fallback: {0xXX|null}
        description: {描述}
    optional:
      - address: {0xXX}
        name: {地址名称}
        skipable: true
        description: {描述}
  
  # 标签
  tags:
    - {tag1}
    - {tag2}
  
  # 依赖
  dependencies:
    - {skill-id-1}
    - {skill-id-2}
  
  # 角色 (SCENE技能必需)
  roles:
    - name: MANAGER
      displayName: 场景管理员
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE, CONFIG, DELETE]
    - name: MEMBER
      displayName: 场景成员
      minCount: 0
      maxCount: 100
      permissions: [READ, WRITE]
```

---

## 四、技能拆分清单

### 4.1 _system/ 系统技能 (4个)

| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-capability | PROVIDER | SERVICE | P0 |
| skill-common | INTERNAL | SERVICE | P1 |
| skill-management | SCENE | SERVICE | P0 |
| skill-protocol | INTERNAL | SERVICE | P1 |

### 4.2 _drivers/llm/ LLM驱动 (5个)

| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-llm-ollama | PROVIDER | LLM | P0 |
| skill-llm-openai | PROVIDER | LLM | P0 |
| skill-llm-qianwen | PROVIDER | LLM | P0 |
| skill-llm-deepseek | PROVIDER | LLM | P0 |
| skill-llm-volcengine | PROVIDER | LLM | P1 |

### 4.3 _drivers/org/ 组织驱动 (5个)

| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-org-base | PROVIDER | SERVICE | P0 |
| skill-org-dingding | PROVIDER | SERVICE | P0 |
| skill-org-feishu | PROVIDER | SERVICE | P0 |
| skill-org-ldap | PROVIDER | SERVICE | P1 |
| skill-org-wecom | PROVIDER | SERVICE | P1 |

### 4.4 _drivers/vfs/ 存储驱动 (6个)

| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-vfs-base | PROVIDER | SERVICE | P0 |
| skill-vfs-database | PROVIDER | SERVICE | P1 |
| skill-vfs-local | PROVIDER | SERVICE | P0 |
| skill-vfs-minio | PROVIDER | SERVICE | P1 |
| skill-vfs-oss | PROVIDER | SERVICE | P1 |
| skill-vfs-s3 | PROVIDER | SERVICE | P1 |

### 4.5 _drivers/media/ 媒体驱动 (5个)

| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-media-toutiao | PROVIDER | MEDIA | P1 |
| skill-media-wechat | PROVIDER | MEDIA | P1 |
| skill-media-weibo | PROVIDER | MEDIA | P1 |
| skill-media-xiaohongshu | PROVIDER | MEDIA | P1 |
| skill-media-zhihu | PROVIDER | MEDIA | P1 |

### 4.6 _drivers/payment/ 支付驱动 (3个)

| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-payment-alipay | PROVIDER | SERVICE | P1 |
| skill-payment-unionpay | PROVIDER | SERVICE | P1 |
| skill-payment-wechat | PROVIDER | SERVICE | P1 |

### 4.7 capabilities/ 能力技能 (20+个)

#### auth/ 认证
| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-user-auth | PROVIDER | SERVICE | P0 |

#### communication/ 通讯
| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-email | PROVIDER | SERVICE | P0 |
| skill-group | PROVIDER | SERVICE | P1 |
| skill-im | PROVIDER | SERVICE | P1 |
| skill-mqtt | PROVIDER | SERVICE | P1 |
| skill-msg | PROVIDER | SERVICE | P0 |
| skill-notify | PROVIDER | SERVICE | P1 |

#### knowledge/ 知识库
| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-knowledge-base | PROVIDER | KNOWLEDGE | P0 |
| skill-local-knowledge | PROVIDER | KNOWLEDGE | P1 |
| skill-rag | PROVIDER | KNOWLEDGE | P0 |
| skill-vector-sqlite | PROVIDER | KNOWLEDGE | P1 |

#### llm/ LLM能力
| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-llm-config-manager | PROVIDER | LLM | P1 |
| skill-llm-context-builder | PROVIDER | LLM | P1 |
| skill-llm-conversation | PROVIDER | LLM | P0 |

#### monitor/ 监控
| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-agent | PROVIDER | SERVICE | P1 |
| skill-cmd-service | PROVIDER | SERVICE | P1 |
| skill-health | PROVIDER | SERVICE | P1 |
| skill-monitor | PROVIDER | SERVICE | P1 |
| skill-network | PROVIDER | SERVICE | P1 |
| skill-remote-terminal | PROVIDER | SERVICE | P1 |
| skill-res-service | PROVIDER | SERVICE | P1 |

#### scheduler/ 调度
| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-scheduler-quartz | PROVIDER | SERVICE | P1 |
| skill-task | PROVIDER | SERVICE | P1 |

#### search/ 搜索
| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-search | PROVIDER | SERVICE | P1 |

#### security/ 安全
| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-access-control | PROVIDER | SERVICE | P1 |
| skill-audit | PROVIDER | SERVICE | P1 |
| skill-security | PROVIDER | SERVICE | P1 |

### 4.8 scenes/ 场景技能 (8个)

| 技能ID | skillForm | sceneType | category | 优先级 |
|--------|-----------|-----------|----------|:------:|
| skill-business | SCENE | TRIGGER | WORKFLOW | P0 |
| skill-collaboration | SCENE | TRIGGER | OFFICE_COLLABORATION | P0 |
| skill-document-assistant | SCENE | AUTO | KNOWLEDGE | P0 |
| skill-knowledge-qa | SCENE | AUTO | AI_ASSISTANT | P0 |
| skill-knowledge-share | SCENE | TRIGGER | OFFICE_COLLABORATION | P1 |
| skill-llm-chat | SCENE | AUTO | AI_ASSISTANT | P0 |
| skill-meeting-minutes | SCENE | TRIGGER | OFFICE_COLLABORATION | P1 |
| skill-onboarding-assistant | SCENE | TRIGGER | HUMAN_RESOURCE | P1 |
| skill-project-knowledge | SCENE | TRIGGER | PROJECT_MANAGEMENT | P1 |

### 4.9 tools/ 工具技能 (3个)

| 技能ID | skillForm | category | 优先级 |
|--------|-----------|----------|:------:|
| skill-document-processor | STANDALONE | TOOL | P1 |
| skill-market | STANDALONE | SERVICE | P0 |
| skill-report | STANDALONE | TOOL | P1 |
| skill-share | STANDALONE | SERVICE | P1 |

---

## 五、执行计划

### 5.1 分批执行

| 批次 | 技能范围 | 数量 | 工期 | 负责人 |
|------|----------|:----:|:----:|--------|
| Batch 1 | P0 核心技能 | 20个 | 2天 | Skills Team |
| Batch 2 | P1 重要技能 | 25个 | 2天 | Skills Team |
| Batch 3 | P2 其他技能 | 15个 | 1天 | Skills Team |

### 5.2 执行步骤

#### Step 1: 创建技能索引条目文件

```bash
# 示例: skill-knowledge-qa
cd E:\github\ooder-skills\skills\scenes\skill-knowledge-qa

# 创建 skill-index-entry.yaml
cat > skill-index-entry.yaml << 'EOF'
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: skill-knowledge-qa
  name: 知识问答场景
  version: 3.0.0
  description: "基于知识库的智能问答场景"

spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  
  businessCategory: AI_ASSISTANT
  subCategory: 知识问答
  
  category: KNOWLEDGE
  capabilityCategory: know
  
  capabilityAddresses:
    required:
      - address: 0x30
        name: KNOW_VECTOR
        description: "向量知识库"
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
        description: "LLM服务"
    optional:
      - address: 0x4A
        name: COMM_EMAIL
        skipable: true
        description: "邮件通知"
  
  tags:
    - AI
    - 知识库
    - 问答
    - LLM
  
  dependencies:
    - skill-llm-ollama
    - skill-vector-chroma
  
  roles:
    - name: MANAGER
      displayName: 场景管理员
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE, CONFIG, DELETE]
    - name: MEMBER
      displayName: 场景成员
      minCount: 0
      maxCount: 100
      permissions: [READ, WRITE]
EOF
```

#### Step 2: 验证文件格式

```bash
# 使用 yamllint 验证
yamllint skill-index-entry.yaml

# 或使用 Python
python -c "import yaml; yaml.safe_load(open('skill-index-entry.yaml'))"
```

#### Step 3: 提交代码

```bash
git add skill-index-entry.yaml
git commit -m "feat(skill-knowledge-qa): add skill-index-entry.yaml

- Add SE v1.1.0 compliant skill index entry
- Define capability addresses (0x30, 0x28)
- Configure roles (MANAGER, MEMBER)

Refs: SKILL_INDEX_REFACTORING_PROPOSAL.md"
git push
```

---

## 六、验证清单

### 6.1 单个技能验证

- [ ] skill-index-entry.yaml 文件已创建
- [ ] YAML 格式正确
- [ ] 所有必需字段已填写
- [ ] skillForm 值有效 (SCENE/PROVIDER/DRIVER/INTERNAL)
- [ ] visibility 值有效 (public/developer/internal)
- [ ] category 值有效 (KNOWLEDGE/LLM/TOOL/...)
- [ ] capabilityCategory 值有效 (sys/org/auth/...)
- [ ] capabilityAddresses 配置正确
- [ ] SCENE技能配置了 sceneType 和 roles
- [ ] 文件已提交到 git

### 6.2 批量验证

```bash
# 使用验证脚本
./scripts/validate-skill-index-entries.sh

# 或使用 Java 验证程序
mvn exec:java -Dexec.mainClass="net.ooder.skill.validation.SkillIndexValidator"
```

---

## 七、与 Engine Team 协作

### 7.1 需要 Engine Team 支持

| 任务 | 说明 | 状态 |
|------|------|:----:|
| 验证程序更新 | 更新 SkillValidationRunner 支持新格式 | 待开始 |
| 聚合工具开发 | 开发 skill-index-aggregator | 待开始 |
| CI/CD 配置 | 配置自动生成 skill-index.yaml | 待开始 |

### 7.2 协作流程

```
Skills Team                          Engine Team
     |                                    |
     |-- 1. 创建 skill-index-entry.yaml   |
     |                                    |
     |-- 2. 提交 PR -------------------->|
     |                                    |
     |<-- 3. 验证通过 --------------------|
     |                                    |
     |-- 4. 合并到 main                   |
     |                                    |
     |<-- 5. 触发聚合生成 ----------------|
     |                                    |
```

---

## 八、参考文档

| 文档 | 路径 |
|------|------|
| Phase 1 基础层配置 | `PHASE1_BASE_LAYER_CONFIG.md` |
| Skill Index 重构方案 | `SKILL_INDEX_REFACTORING_PROPOSAL.md` |
| SE强制执行标准 v1.1.0 | `SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md` |

---

## 九、附件

### 附件 A: 技能配置示例

#### 示例 1: 场景技能 (skill-knowledge-qa)

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: skill-knowledge-qa
  name: 知识问答场景
  version: 3.0.0
  description: "基于知识库的智能问答场景"

spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  businessCategory: AI_ASSISTANT
  subCategory: 知识问答
  category: KNOWLEDGE
  capabilityCategory: know
  
  capabilityAddresses:
    required:
      - address: 0x30
        name: KNOW_VECTOR
        description: "向量知识库"
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
        description: "LLM服务"
  
  tags: [AI, 知识库, 问答, LLM]
  dependencies: [skill-llm-ollama, skill-vector-chroma]
  
  roles:
    - name: MANAGER
      displayName: 场景管理员
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE, CONFIG, DELETE]
    - name: MEMBER
      displayName: 场景成员
      minCount: 0
      maxCount: 100
      permissions: [READ, WRITE]
```

#### 示例 2: 提供者技能 (skill-llm-ollama)

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: skill-llm-ollama
  name: Ollama LLM服务
  version: 2.0.0
  description: "基于Ollama的本地LLM服务"

spec:
  skillForm: PROVIDER
  visibility: public
  businessCategory: INFRASTRUCTURE
  category: LLM
  capabilityCategory: llm
  
  capabilityAddresses:
    required:
      - address: 0x28
        name: LLM_OLLAMA
        description: "Ollama本地模型"
  
  tags: [LLM, Ollama, AI, 本地模型]
  dependencies: []
```

---

**执行优先级**: P0 (立即执行)  
**预计总工期**: 5天 (分批执行)  
**阻塞后续**: Phase 3 聚合层实现
