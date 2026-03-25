# Phase 1: 基础层配置文档

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **执行团队**: Skills Team (有权限者)  
> **参考标准**: SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md v1.1.0  
> **状态**: 待执行

---

## 一、任务概述

创建分层设计的基础层配置文件，位于 `E:\github\ooder-skills\skills\config\` 目录。

**注意**: Engine Team 无权限直接修改此目录，需要 Skills Team 协助执行。

---

## 二、创建目录结构

```bash
# 在 E:\github\ooder-skills\skills\ 目录下执行
mkdir config
```

---

## 三、配置文件清单

### 3.1 config/schema.yaml

**文件路径**: `E:\github\ooder-skills\skills\config\schema.yaml`

**文件内容**:

```yaml
# config/schema.yaml
# SE标准 v1.1.0 - 配置规范定义
# 由 Engine Team 维护

apiVersion: config.ooder.net/v1
kind: SkillIndexSchema

metadata:
  version: "1.1.0"
  lastUpdated: "2026-03-11"
  description: "Skill Index 配置规范定义"

spec:
  fields:
    # 基础字段
    - name: id
      type: string
      required: true
      pattern: "^skill-[a-z0-9-]+$"
      description: "技能唯一标识"
      
    - name: name
      type: string
      required: true
      maxLength: 100
      description: "技能显示名称"
      
    - name: version
      type: string
      required: true
      pattern: "^\\d+\\.\\d+\\.\\d+$"
      description: "语义化版本号"
      
    - name: description
      type: string
      required: true
      maxLength: 500
      description: "技能描述"
    
    # SE三维分类字段
    - name: skillForm
      type: enum
      required: true
      values: [SCENE, PROVIDER, DRIVER, INTERNAL]
      description: "技能形态"
      
    - name: sceneType
      type: enum
      required: false
      values: [AUTO, TRIGGER]
      condition: "skillForm == SCENE"
      description: "场景类型 (仅SCENE时必需)"
      
    - name: visibility
      type: enum
      required: true
      values: [public, developer, internal]
      description: "可见性"
    
    # 业务分类字段
    - name: businessCategory
      type: enum
      required: true
      ref: "categories.yaml#/businessCategories"
      description: "业务分类"
      
    - name: subCategory
      type: string
      required: false
      maxLength: 50
      description: "子分类"
    
    # 技术分类字段
    - name: category
      type: enum
      required: true
      ref: "categories.yaml#/skillCategories"
      description: "SE标准技术分类"
      
    - name: capabilityCategory
      type: enum
      required: true
      ref: "categories.yaml#/capabilityCategories"
      description: "能力地址分类"
    
    # 标签字段
    - name: tags
      type: array
      required: false
      itemType: string
      description: "技能标签"
    
    # 能力地址字段
    - name: capabilityAddresses
      type: object
      required: true
      description: "能力地址配置"
      
    # 角色字段 (SCENE时必需)
    - name: roles
      type: array
      required: false
      condition: "skillForm == SCENE"
      description: "场景角色配置"
  
  validation:
    - rule: sceneTypeRequired
      condition: "skillForm == 'SCENE' && !sceneType"
      message: "SCENE技能必须指定sceneType (AUTO或TRIGGER)"
      severity: ERROR
      
    - rule: versionFormat
      condition: "!version || !version.matches('^\\d+\\.\\d+\\.\\d+$')"
      message: "版本号格式必须为 x.y.z"
      severity: ERROR
      
    - rule: idFormat
      condition: "!id || !id.matches('^skill-[a-z0-9-]+$')"
      message: "ID格式必须为 skill-{name}"
      severity: ERROR
      
    - rule: capabilityAddressesRequired
      condition: "!capabilityAddresses || !capabilityAddresses.required"
      message: "必须声明必需的能力地址"
      severity: WARNING
      
    - rule: rolesRequiredForScene
      condition: "skillForm == 'SCENE' && (!roles || roles.isEmpty())"
      message: "SCENE技能建议配置角色"
      severity: WARNING
```

---

### 3.2 config/addresses.yaml

**文件路径**: `E:\github\ooder-skills\skills\config\addresses.yaml`

**文件内容**: (完整内容见文档附件，约400行)

**核心内容**:
- 16个能力地址分类 (sys, org, auth, vfs, db, llm, know, payment, media, comm, mon, iot, search, sched, sec, net)
- 128个固定地址定义 (0x00-0x7F)
- 降级规则配置

**关键地址示例**:
```yaml
addresses:
  - address: 0x28
    name: LLM_OLLAMA
    category: llm
    fallback: 0x29
    
  - address: 0x30
    name: KNOW_VECTOR
    category: know
```

---

### 3.3 config/categories.yaml

**文件路径**: `E:\github\ooder-skills\skills\config\categories.yaml`

**文件内容**: (完整内容见文档附件，约300行)

**核心内容**:
- 8个SE标准技术分类 (KNOWLEDGE, LLM, TOOL, WORKFLOW, DATA, SERVICE, UI, OTHER)
- 17个能力地址分类
- 10个业务分类
- 4个skillForm枚举 (SCENE, PROVIDER, DRIVER, INTERNAL)
- 2个sceneType枚举 (AUTO, TRIGGER)
- 3个visibility枚举 (public, developer, internal)

---

## 四、执行步骤

### Step 1: 创建目录

```bash
cd E:\github\ooder-skills\skills
mkdir config
```

### Step 2: 创建 schema.yaml

1. 复制上述 schema.yaml 内容
2. 保存到 `E:\github\ooder-skills\skills\config\schema.yaml`
3. 验证文件格式: `yamllint config/schema.yaml`

### Step 3: 创建 addresses.yaml

1. 复制完整 addresses.yaml 内容 (见附件)
2. 保存到 `E:\github\ooder-skills\skills\config\addresses.yaml`
3. 验证文件格式

### Step 4: 创建 categories.yaml

1. 复制完整 categories.yaml 内容 (见附件)
2. 保存到 `E:\github\ooder-skills\skills\config\categories.yaml`
3. 验证文件格式

### Step 5: 提交代码

```bash
git add config/
git commit -m "feat: add Phase 1 base layer config files for skill index refactoring

- Add schema.yaml: configuration specification
- Add addresses.yaml: capability address space (128 addresses)
- Add categories.yaml: classification enums (skill/capability/business)

Refs: SKILL_INDEX_REFACTORING_PROPOSAL.md"
git push
```

---

## 五、验证清单

- [ ] config/ 目录已创建
- [ ] schema.yaml 已创建且格式正确
- [ ] addresses.yaml 已创建且格式正确
- [ ] categories.yaml 已创建且格式正确
- [ ] 所有文件已提交到 git
- [ ] Engine Team 已确认收到

---

## 六、附件

由于文档长度限制，完整文件内容请查看:

1. `addresses.yaml` 完整内容 - 见附件 A
2. `categories.yaml` 完整内容 - 见附件 B

---

## 七、联系方式

| 事项 | 联系人 |
|------|--------|
| 技术问题 | Engine Team |
| 权限问题 | Skills Team Admin |
| 紧急问题 | 双方会议 |

---

**执行优先级**: P0 (立即执行)  
**预计耗时**: 30 分钟  
**阻塞后续**: Phase 2 技能层拆分
