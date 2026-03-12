# Phase 2 执行协同任务清单

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **执行阶段**: Phase 2 - 技能层拆分  
> **状态**: 立即执行

---

## 一、执行概览

### 1.1 前置条件确认 ✅

| 条件 | 状态 | 说明 |
|------|:----:|------|
| Phase 1 完成 | ✅ | config/ 目录及基础配置文件已创建 |
| SDK枚举实现 | ✅ | Visibility, SkillForm, CapabilityCategory 已完成 |
| 协作文档 | ✅ | PHASE2_SKILL_LAYER_SPLIT.md 已生成 |

### 1.2 执行目标

将 **60+ 个技能** 从 `skill-index.yaml` 拆分为独立的 `skill-index-entry.yaml` 文件。

---

## 二、Skills Team 执行任务

### 2.1 Batch 1: P0 核心技能 (立即执行)

**工期**: 2天  
**负责人**: Skills Team  
**技能数量**: 20个

#### 执行步骤

**Step 1: 获取技能信息**

```bash
# 从现有 skill-index.yaml 提取以下技能信息
cd E:\github\ooder-skills\skills
```

**Step 2: 创建技能索引条目文件**

为每个技能创建 `skill-index-entry.yaml`:

| 序号 | 技能路径 | 技能ID | skillForm | 优先级 |
|:----:|----------|--------|-----------|:------:|
| 1 | `_system/skill-capability/` | skill-capability | PROVIDER | P0 |
| 2 | `_system/skill-management/` | skill-management | SCENE | P0 |
| 3 | `_drivers/llm/skill-llm-ollama/` | skill-llm-ollama | PROVIDER | P0 |
| 4 | `_drivers/llm/skill-llm-openai/` | skill-llm-openai | PROVIDER | P0 |
| 5 | `_drivers/llm/skill-llm-qianwen/` | skill-llm-qianwen | PROVIDER | P0 |
| 6 | `_drivers/llm/skill-llm-deepseek/` | skill-llm-deepseek | PROVIDER | P0 |
| 7 | `_drivers/org/skill-org-base/` | skill-org-base | PROVIDER | P0 |
| 8 | `_drivers/org/skill-org-dingding/` | skill-org-dingding | PROVIDER | P0 |
| 9 | `_drivers/org/skill-org-feishu/` | skill-org-feishu | PROVIDER | P0 |
| 10 | `_drivers/vfs/skill-vfs-base/` | skill-vfs-base | PROVIDER | P0 |
| 11 | `_drivers/vfs/skill-vfs-local/` | skill-vfs-local | PROVIDER | P0 |
| 12 | `capabilities/auth/skill-user-auth/` | skill-user-auth | PROVIDER | P0 |
| 13 | `capabilities/communication/skill-email/` | skill-email | PROVIDER | P0 |
| 14 | `capabilities/communication/skill-msg/` | skill-msg | PROVIDER | P0 |
| 15 | `capabilities/knowledge/skill-knowledge-base/` | skill-knowledge-base | PROVIDER | P0 |
| 16 | `capabilities/knowledge/skill-rag/` | skill-rag | PROVIDER | P0 |
| 17 | `capabilities/llm/skill-llm-conversation/` | skill-llm-conversation | PROVIDER | P0 |
| 18 | `scenes/skill-business/` | skill-business | SCENE | P0 |
| 19 | `scenes/skill-collaboration/` | skill-collaboration | SCENE | P0 |
| 20 | `scenes/skill-document-assistant/` | skill-document-assistant | SCENE | P0 |
| 21 | `scenes/skill-knowledge-qa/` | skill-knowledge-qa | SCENE | P0 |
| 22 | `scenes/skill-llm-chat/` | skill-llm-chat | SCENE | P0 |
| 23 | `tools/skill-market/` | skill-market | STANDALONE | P0 |

**Step 3: 文件创建命令示例**

```bash
# 示例: 创建 skill-knowledge-qa 的索引条目
cd E:\github\ooder-skills\skills\scenes\skill-knowledge-qa

# 创建文件 (使用以下内容)
```

**skill-index-entry.yaml 内容模板**:

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: {skill-id}
  name: {技能名称}
  version: {版本号}
  description: {技能描述}

spec:
  skillForm: {SCENE|PROVIDER|DRIVER|INTERNAL}
  sceneType: {AUTO|TRIGGER}  # 仅SCENE时
  visibility: {public|developer|internal}
  businessCategory: {OFFICE_COLLABORATION|HUMAN_RESOURCE|AI_ASSISTANT|...}
  subCategory: {子分类}
  category: {KNOWLEDGE|LLM|TOOL|WORKFLOW|DATA|SERVICE|UI|OTHER}
  capabilityCategory: {sys|org|auth|vfs|db|llm|know|...}
  
  capabilityAddresses:
    required:
      - address: {0xXX}
        name: {地址名称}
        fallback: {0xXX|null}
        description: {描述}
    optional: []
  
  tags: [{tag1}, {tag2}]
  dependencies: [{skill-id-1}]
  
  roles:  # SCENE技能必需
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

**Step 4: 提交代码**

```bash
# 每个技能单独提交
git add skill-index-entry.yaml
git commit -m "feat({skill-id}): add skill-index-entry.yaml

- Add SE v1.1.0 compliant skill index entry
- Define capability addresses
- Configure roles (if SCENE)

Refs: PHASE2_SKILL_LAYER_SPLIT.md"
git push
```

---

### 2.2 Batch 2: P1 重要技能 (第3-4天)

**工期**: 2天  
**技能数量**: 25个

| 类别 | 技能数量 | 路径示例 |
|------|:--------:|----------|
| _drivers/media/ | 5个 | skill-media-toutiao, skill-media-wechat... |
| _drivers/payment/ | 3个 | skill-payment-alipay, skill-payment-wechat... |
| _drivers/vfs/ | 4个 | skill-vfs-database, skill-vfs-minio... |
| capabilities/communication/ | 4个 | skill-group, skill-im, skill-mqtt, skill-notify |
| capabilities/knowledge/ | 2个 | skill-local-knowledge, skill-vector-sqlite |
| capabilities/monitor/ | 7个 | skill-agent, skill-health, skill-monitor... |
| scenes/ | 3个 | skill-knowledge-share, skill-meeting-minutes, skill-project-knowledge |
| tools/ | 3个 | skill-document-processor, skill-report, skill-share |

---

### 2.3 Batch 3: P2 其他技能 (第5天)

**工期**: 1天  
**技能数量**: 15个

| 类别 | 技能数量 | 路径示例 |
|------|:--------:|----------|
| _system/ | 2个 | skill-common, skill-protocol |
| _drivers/org/ | 2个 | skill-org-ldap, skill-org-wecom |
| capabilities/llm/ | 2个 | skill-llm-config-manager, skill-llm-context-builder |
| capabilities/scheduler/ | 2个 | skill-scheduler-quartz, skill-task |
| capabilities/security/ | 3个 | skill-access-control, skill-audit, skill-security |
| scenes/ | 1个 | skill-onboarding-assistant |
| 其他 | 3个 | ... |

---

## 三、Engine Team 执行任务

### 3.1 并行任务 (与 Batch 1 同步进行)

| 任务 | 工期 | 说明 | 优先级 |
|------|:----:|------|:------:|
| 更新验证程序 | 1天 | 更新 SkillValidationRunner 支持新格式 | P0 |
| 开发聚合工具 | 2天 | 开发 skill-index-aggregator | P0 |
| CI/CD配置 | 1天 | 配置自动生成 skill-index.yaml | P1 |

### 3.2 验证程序更新

**文件**: `scene-engine/src/test/.../SkillValidationRunner.java`

**需要更新**:
1. 支持读取 `skill-index-entry.yaml`
2. 验证 SE v1.1.0 字段
3. 验证 capabilityAddresses 格式
4. 验证 roles 配置 (SCENE技能)

### 3.3 聚合工具开发

**工具名称**: `skill-index-aggregator`

**功能**:
```bash
skill-index-aggregator \
  --schema config/schema.yaml \
  --addresses config/addresses.yaml \
  --categories config/categories.yaml \
  --skills-dir skills/ \
  --output skill-index.yaml
```

**输入**:
- config/schema.yaml
- config/addresses.yaml
- config/categories.yaml
- skills/*/skill-index-entry.yaml

**输出**:
- skill-index.yaml (聚合后的完整索引)

---

## 四、执行时间表

```
Day 1-2: Batch 1 (P0核心技能) + Engine Team 验证程序更新
Day 3-4: Batch 2 (P1重要技能) + Engine Team 聚合工具开发
Day 5:   Batch 3 (P2其他技能) + Engine Team CI/CD配置
Day 6:   集成测试 + 问题修复
```

---

## 五、验证检查点

### 5.1 Skills Team 自查清单

每个技能提交前检查:
- [ ] skill-index-entry.yaml 文件已创建
- [ ] YAML 格式正确 (无语法错误)
- [ ] id 格式正确 (skill-{name})
- [ ] version 格式正确 (x.y.z)
- [ ] skillForm 值有效
- [ ] visibility 值有效
- [ ] category 值有效
- [ ] capabilityCategory 值有效
- [ ] capabilityAddresses 配置正确
- [ ] SCENE技能配置了 sceneType
- [ ] SCENE技能配置了 roles
- [ ] 文件已提交到 git

### 5.2 Engine Team 验证清单

- [ ] 验证程序支持新格式
- [ ] 聚合工具运行正常
- [ ] 生成的 skill-index.yaml 格式正确
- [ ] CI/CD 流程正常
- [ ] 全量技能验证通过

---

## 六、问题处理

### 6.1 常见问题

| 问题 | 解决方案 | 联系人 |
|------|----------|--------|
| 字段值不确定 | 参考 config/categories.yaml | Engine Team |
| capabilityAddresses 不确定 | 参考 config/addresses.yaml | Engine Team |
| YAML格式错误 | 使用 yamllint 检查 | Skills Team |
| 验证失败 | 查看验证程序输出 | Engine Team |

### 6.2 紧急联系

| 情况 | 联系人 | 响应时间 |
|------|--------|:--------:|
| 技术问题 | Engine Team | 4小时 |
| 配置问题 | Skills Team Lead | 2小时 |
| 阻塞问题 | 双方会议 | 当天 |

---

## 七、交付物

### 7.1 Skills Team 交付

- [ ] 60+ 个 skill-index-entry.yaml 文件
- [ ] 所有文件已提交到 git
- [ ] 验证通过

### 7.2 Engine Team 交付

- [ ] 更新的验证程序
- [ ] skill-index-aggregator 工具
- [ ] CI/CD 配置
- [ ] 自动生成的 skill-index.yaml

---

## 八、参考文档

| 文档 | 路径 | 说明 |
|------|------|------|
| Phase 2 拆分方案 | `PHASE2_SKILL_LAYER_SPLIT.md` | 详细设计方案 |
| Phase 1 基础配置 | `PHASE1_BASE_LAYER_CONFIG.md` | 基础层配置 |
| SE强制执行标准 | `SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md` | 标准规范 |

---

**开始执行时间**: 立即  
**预计完成时间**: 6天后  
**状态**: 🚀 立即开始
