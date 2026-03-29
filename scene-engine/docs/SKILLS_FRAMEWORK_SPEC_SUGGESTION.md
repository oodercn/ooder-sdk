# Skills Framework 规范建议

## 问题概述

**发现时间**: 2026-03-29  
**问题级别**: P1 - 严重  
**影响范围**: skill-index.yaml 解析、技能类型识别、场景技能安装  

---

## 问题描述

### 现象
当 `skill-index.yaml` 或技能 YAML 文件中定义了 `skillForm: SCENE` 时，系统无法正确识别技能类型，导致：
- 场景技能被错误识别为原子技能 (`ATOMIC`)
- 场景配置验证被跳过
- 安装流程异常

### 根因分析

#### 1. 数据流断裂

```
skill-index.yaml                    UnifiedDiscoveryServiceImpl         SkillInstallProcessorImpl
     │                                      │                                      │
     │  spec:                               │                                      │
     │    skillForm: SCENE                  │                                      │
     │    roles: [...]                      │                                      │
     ├──────────────────────────────────────►                                      │
     │                                      │ createSkillPackage()                 │
     │                                      │   ❌ 未解析 spec.skillForm           │
     │                                      │   ❌ 未设置 metadata                 │
     │                                      │                                      │
     │                                      ├──────────────────────────────────────►
     │                                      │                                      │ determineSkillForm()
     │                                      │                                      │   metadata == null
     │                                      │                                      │   return "ATOMIC" ❌
```

#### 2. 代码层面问题

**位置**: `UnifiedDiscoveryServiceImpl.createSkillPackage()` (scene-engine 模块)

```java
// 问题代码 - 只解析了部分字段
private SkillPackage createSkillPackage(JSONObject skillData) {
    // ... 解析 skillId, name, version, description, category, tags ...
    
    // ❌ 遗漏: 未解析 skillForm
    // ❌ 遗漏: 未设置 metadata
    
    return skill;  // metadata 为 null
}
```

**下游影响**: `SkillInstallProcessorImpl.determineSkillForm()`

```java
private String determineSkillForm(SkillPackage skillPackage) {
    Map<String, Object> metadata = skillPackage.getMetadata();
    if (metadata == null) {
        return "ATOMIC";  // ← 由于上游未设置，永远返回默认值
    }
    // ... 后续逻辑无法执行 ...
}
```

---

## 修复方案

### 已修复内容

**文件**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\discovery\impl\UnifiedDiscoveryServiceImpl.java`

```java
private SkillPackage createSkillPackage(JSONObject skillData) {
    // ... 原有字段解析 ...
    
    // ✅ 正确修复: 从 spec 节点解析 skillForm
    JSONObject spec = skillData.getJSONObject("spec");
    if (spec != null) {
        Map<String, Object> metadata = new HashMap<>();
        
        String skillForm = spec.getString("skillForm");
        if (skillForm != null) {
            metadata.put("skillForm", skillForm);
            metadata.put("type", skillForm);  // 兼容 determineSkillForm() 的 type 检查
        }
        
        String sceneType = spec.getString("sceneType");
        if (sceneType != null) {
            metadata.put("sceneType", sceneType);
        }
        
        // 保留整个 spec 供下游使用
        metadata.put("spec", new HashMap<>(spec));
        
        skill.setMetadata(metadata);
    }
    
    return skill;
}
```

**关键点**: `skillForm` 字段位于 `spec` 节点内，而非根级别。

---

## 规范建议

### 建议 1: SkillPackage 元数据完整性规范

**原则**: `SkillPackage.metadata` 应包含技能定义的所有扩展信息

**必填字段**:

| 字段路径 | 类型 | 说明 | 用途 |
|---------|------|------|------|
| `metadata.skillForm` | String | 技能形态: `ATOMIC` / `SCENE` | 类型识别 |
| `metadata.type` | String | 类型别名 (与 skillForm 同值) | 兼容性检查 |
| `metadata.sceneType` | String | 场景类型 | 场景分类 |
| `metadata.spec` | Map | 技能规格定义 (完整副本) | 配置解析 |

**spec 子字段** (存储在 `metadata.spec` 中):

| 字段路径 | 类型 | 说明 |
|---------|------|------|
| `spec.skillForm` | String | 技能形态 (源数据位置) |
| `spec.sceneType` | String | 场景类型 |
| `spec.roles` | List | 场景角色定义 |
| `spec.activationSteps` | List | 激活步骤 |
| `spec.menus` | List | 菜单配置 |
| `spec.llmConfig` | Map | LLM 配置 |
| `spec.knowledge` | Map | 知识库配置 |

---

### 建议 2: YAML 文件规范

**skill-index.yaml 示例**:

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex
spec:
  includes:
    - skills/*.yaml
```

**skills/xxx-skill.yaml 示例**:

```yaml
apiVersion: ooder.io/v1
kind: Skill
metadata:
  skillId: scene-example
  name: 示例场景技能
  version: 1.0.0
  description: 场景技能示例
  category: productivity
  tags:
    - scene
    - example
spec:                       # ✅ spec 节点包含 skillForm
  skillForm: SCENE          # ✅ 必须在 spec 内声明
  sceneType: default        # 可选: 场景类型
  roles:
    - roleId: assistant
      name: 助手
  activationSteps:
    - step: 1
      action: initialize
  menus:
    - menuId: main
      name: 主菜单
  llmConfig:
    model: gpt-4
    systemPromptFile: prompts/system.md
  knowledge:
    enabled: true
    bases:
      - baseId: default
```

**重要**: `skillForm` 和 `sceneType` 字段必须放在 `spec` 节点内，而非根级别或 `metadata` 节点。

---

### 建议 3: 数据传递链路规范

**原则**: 任何涉及 `SkillPackage` 创建的代码路径，必须保证 `metadata` 完整性

**检查点**:

| 位置 | 方法 | 检查项 |
|------|------|--------|
| `UnifiedDiscoveryServiceImpl` | `createSkillPackage()` | ✅ 已修复 |
| `InternalDiscoveryServiceImpl` | `createSkillPackage()` | 需检查 |
| `UnifiedSkillRegistryImpl` | `registerSkill()` | 需检查 |
| `fetchAndParseYamlFile()` | YAML 解析 | 需检查 |

**代码审查清单**:

- [ ] 所有 `new SkillPackage()` 创建点是否设置了 `metadata`
- [ ] 所有 YAML 解析点是否提取了 `skillForm` 和 `spec`
- [ ] 所有 `skillPackage.getMetadata()` 调用是否处理了 null 情况

---

### 建议 4: 单元测试规范

**必须覆盖的测试场景**:

```java
@Test
void testCreateSkillPackage_WithSkillForm() {
    JSONObject skillData = new JSONObject();
    skillData.put("skillId", "test-skill");
    skillData.put("name", "Test Skill");
    
    // skillForm 在 spec 节点内
    JSONObject spec = new JSONObject();
    spec.put("skillForm", "SCENE");
    spec.put("sceneType", "default");
    spec.put("roles", Arrays.asList("assistant"));
    skillData.put("spec", spec);
    
    SkillPackage skill = createSkillPackage(skillData);
    
    assertNotNull(skill.getMetadata());
    assertEquals("SCENE", skill.getMetadata().get("skillForm"));
    assertEquals("default", skill.getMetadata().get("sceneType"));
    assertNotNull(skill.getMetadata().get("spec"));
}

@Test
void testDetermineSkillForm_FromMetadata() {
    SkillPackage skill = new SkillPackage();
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("skillForm", "SCENE");
    skill.setMetadata(metadata);
    
    String form = determineSkillForm(skill);
    assertEquals("SCENE", form);
}
```

---

### 建议 5: 防御性编程规范

**原则**: 下游代码不应假设上游数据完整性

**改进示例** (`SkillInstallProcessorImpl.determineSkillForm()`):

```java
private String determineSkillForm(SkillPackage skillPackage) {
    // ✅ 防御性检查: 同时检查 skillPackage 和 metadata
    if (skillPackage == null) {
        return "ATOMIC";
    }
    
    Map<String, Object> metadata = skillPackage.getMetadata();
    if (metadata == null) {
        // ✅ 建议: 添加日志，便于排查
        log.debug("Skill {} has no metadata, defaulting to ATOMIC", 
            skillPackage.getSkillId());
        return "ATOMIC";
    }
    
    // ... 原有逻辑 ...
}
```

---

## 影响评估

### 修复前
- 场景技能无法正确识别
- `skillForm` 字段被忽略
- `spec` 配置丢失

### 修复后
- ✅ `skillForm` 正确解析并存储到 `metadata`
- ✅ `spec` 对象完整保留
- ✅ `determineSkillForm()` 可正确识别场景技能
- ✅ 场景配置验证流程正常执行

---

## 相关文件

| 文件 | 路径 |
|------|------|
| 修复文件 | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\discovery\impl\UnifiedDiscoveryServiceImpl.java` |
| 下游依赖 | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\skill\install\impl\SkillInstallProcessorImpl.java` |
| SkillPackage 定义 | `net.ooder.skills.api.SkillPackage` (skills-framework 模块) |

---

## 版本信息

- **scene-engine**: 3.0.1
- **skills-framework**: 3.0.1
- **修复日期**: 2026-03-29
