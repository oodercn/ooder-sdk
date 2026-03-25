# agent-sdk v3.0 协同任务清单

## 一、背景说明

scene-engine 已完成 v3.0 新模型重构，需要 agent-sdk 和 skills 包同步适配。

**核心变化**：
- 技能是唯一核心实体，场景是技能的形态属性
- 分类从"运行时计算"改为"开发时声明"
- 消除模糊地带（无 PENDING/INVALID）

## 二、新模型核心枚举

### 2.1 SkillForm（技能形态）

```java
package net.ooder.skills.api;

public enum SkillForm {
    SCENE,      // 场景技能（容器型，可包含子技能）
    STANDALONE  // 独立技能（原子型，单一能力）
}
```

### 2.2 SceneType（场景类型）

```java
package net.ooder.skills.api;

public enum SceneType {
    AUTO,    // 自主场景（自驱动，类比：源码包）
    TRIGGER, // 触发场景（被动响应，类比：资源文件夹）
    HYBRID   // 混合场景（既可主动也可被动，类比：普通文件夹）
}
```

### 2.3 SkillCategory（技能分类）

```java
package net.ooder.skills.api;

public enum SkillCategory {
    KNOWLEDGE,  // 知识类（类比：.doc/.pdf）
    LLM,        // AI模型类（类比：.ai/.model）
    TOOL,       // 工具类（类比：.exe/.sh）
    WORKFLOW,   // 流程类（类比：.flow/.pipeline）
    DATA,       // 数据类（类比：.db/.json）
    SERVICE,    // 服务类（类比：.service/.api）
    UI,         // 界面类（类比：.ui/.html）
    OTHER       // 其他
}
```

### 2.4 ServicePurpose（服务目的）

```java
package net.ooder.skills.api;

public enum ServicePurpose {
    // 范围维度
    PERSONAL, TEAM, ORGANIZATION, PUBLIC,
    // 时效维度
    INSTANT, PERSISTENT, SCHEDULED,
    // 主动性维度
    PROACTIVE, REACTIVE
}
```

## 三、任务清单

### 阶段一：skills-framework 核心枚举（优先级：P0）

| 序号 | 任务 | 文件路径 | 说明 |
|------|------|----------|------|
| 1.1 | 创建 SkillForm 枚举 | `skills-framework/src/main/java/net/ooder/skills/api/SkillForm.java` | 技能形态 |
| 1.2 | 创建 SceneType 枚举 | `skills-framework/src/main/java/net/ooder/skills/api/SceneType.java` | 场景类型 |
| 1.3 | 创建 SkillCategory 枚举 | `skills-framework/src/main/java/net/ooder/skills/api/SkillCategory.java` | 技能分类 |
| 1.4 | 创建 ServicePurpose 枚举 | `skills-framework/src/main/java/net/ooder/skills/api/ServicePurpose.java` | 服务目的 |

### 阶段二：核心模型类重写（优先级：P0）

| 序号 | 任务 | 文件路径 | 修改内容 |
|------|------|----------|----------|
| 2.1 | 重写 SkillPackage | `skills-framework/src/main/java/net/ooder/skills/api/SkillPackage.java` | 新增 form/category/purposes/sceneType，删除 sceneId/mainFirst |
| 2.2 | 重写 SkillDefinition | `skills-framework/src/main/java/net/ooder/skills/api/SkillDefinition.java` | 同上 |
| 2.3 | 重写 SkillManifest | `skills-framework/src/main/java/net/ooder/skills/api/SkillManifest.java` | 同上 |

### 阶段三：agent-sdk-core 适配（优先级：P1）

| 序号 | 任务 | 文件路径 | 修改内容 |
|------|------|----------|----------|
| 3.1 | 重写 SkillMetadata | `agent-sdk-core/src/main/java/net/ooder/sdk/plugin/SkillMetadata.java` | 适配 v3.0 字段 |
| 3.2 | 重写 SkillYamlParser | `agent-sdk-core/src/main/java/net/ooder/sdk/discovery/SkillYamlParser.java` | 解析 v3.0 YAML |
| 3.3 | 重写 DynamicSceneConfigManager | `agent-sdk-core/src/main/java/net/ooder/sdk/infra/config/scene/DynamicSceneConfigManager.java` | 适配新模型 |
| 3.4 | 修改 A2AService | `agent-sdk-core/src/main/java/net/ooder/sdk/a2a/A2AService.java` | Agent 发现条件适配 |
| 3.5 | 重写 SkillCard | `agent-sdk-core/src/main/java/net/ooder/sdk/a2a/capability/SkillCard.java` | 技能卡片结构适配 |

### 阶段四：配置文件更新（优先级：P1）

| 序号 | 任务 | 文件路径 | 修改内容 |
|------|------|----------|----------|
| 4.1 | 重写 cmd-scene.yaml | `agent-sdk-core/src/main/resources/config/cmd-scene.yaml` | v3.0 格式 |
| 4.2 | 重写 msg-scene.yaml | `agent-sdk-core/src/main/resources/config/msg-scene.yaml` | v3.0 格式 |

### 阶段五：删除旧代码（优先级：P2）

| 序号 | 任务 | 文件路径 | 说明 |
|------|------|----------|------|
| 5.1 | 删除 SceneConfiguration | `skills-framework/src/main/java/net/ooder/skills/config/SceneConfiguration.java` | 被新模型替代 |
| 5.2 | 删除 SkillMainFirstConfig | `skills-framework/src/main/java/net/ooder/skills/config/SkillMainFirstConfig.java` | mainFirst 概念废弃 |
| 5.3 | 删除 MainFirstConfiguration | `agent-sdk-core/src/main/java/net/ooder/sdk/infra/config/scene/MainFirstConfiguration.java` | 同上 |

### 阶段六：scene-engine 清理（优先级：P2）

| 序号 | 任务 | 文件路径 | 说明 |
|------|------|----------|------|
| 6.1 | 删除 SceneSkillCategory | `scene-engine/.../classification/SceneSkillCategory.java` | 旧分类枚举 |
| 6.2 | 删除 SceneSkillClassifier | `scene-engine/.../classification/SceneSkillClassifier.java` | 旧分类器 |
| 6.3 | 删除 SceneSkillClassifierImpl | `scene-engine/.../classification/SceneSkillClassifierImpl.java` | 旧分类器实现 |
| 6.4 | 删除 SceneSkillClassificationResult | `scene-engine/.../classification/SceneSkillClassificationResult.java` | 旧分类结果 |
| 6.5 | 修改 RichSkill | `scene-engine/.../model/RichSkill.java` | 适配新 SkillPackage |

## 四、YAML 格式对比

### 4.1 旧格式（v2.x）

```yaml
id: doc-assistant
name: 文档助手
version: 1.0.0
type: scene-skill
sceneSkill: true
mainFirst: true
mainFirstConfig:
  driverConditions:
    - type: timer
      cron: "0 0 * * *"
sceneCapabilities:
  - capabilityId: analyze
    mainFirst: true
category: ABS
```

### 4.2 新格式（v3.0）

```yaml
id: doc-assistant
name: 文档助手
version: 1.0.0
form: SCENE
sceneType: AUTO
category: knowledge
purposes:
  - TEAM
  - PERSISTENT
  - PROACTIVE
capabilities:
  - id: analyze
    type: internal
description: 智能文档分析助手
```

## 五、字段映射表

| 旧字段 | 新字段 | 说明 |
|--------|--------|------|
| `sceneSkill: true` | `form: SCENE` | 明确声明形态 |
| `sceneSkill: false` | `form: STANDALONE` | 明确声明形态 |
| `mainFirst: true` | `sceneType: AUTO` | 自主场景 |
| `mainFirst: false` | `sceneType: TRIGGER` | 触发场景 |
| `category: ABS/ASS` | `sceneType: AUTO` | 合并为自主场景 |
| `category: TBS` | `sceneType: TRIGGER` | 触发场景 |
| `category: NOT_SCENE_SKILL` | `form: STANDALONE` | 独立技能 |
| `type: scene-skill` | `form: SCENE` | 形态声明 |
| `sceneCapabilities` | `capabilities` | 能力列表简化 |

## 六、数据库变更

| 表名 | 删除字段 | 新增字段 |
|------|---------|---------|
| `skill_package` | `scene_id`, `main_first`, `scene_skill` | `form`, `scene_type`, `category`, `purposes` |
| `skill_manifest` | `scene_capabilities`, `main_first_config` | `entry_capability`, `orchestration_type` |
| `scene_skill_classification` | **整表删除** | - |

## 七、验证清单

- [ ] skills-framework 编译通过
- [ ] agent-sdk-core 编译通过
- [ ] scene-engine 编译通过
- [ ] YAML 解析正确
- [ ] A2A 协议兼容
- [ ] 技能发现正常
- [ ] 场景激活正常

## 八、联系信息

- scene-engine 负责人：[待填写]
- agent-sdk 负责人：[待填写]
- 协调人：[待填写]

---

**创建时间**：2026-03-10
**版本**：v3.0
**状态**：待执行
