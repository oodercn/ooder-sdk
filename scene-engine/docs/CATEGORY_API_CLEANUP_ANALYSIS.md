# 场景技能分类API分析报告

## 一、问题概述

当前存在多套分类体系并存，导致前端调用混乱：

| 分类体系 | 位置 | 状态 |
|---------|------|------|
| 旧版分类 (ABS/ASS/TBS) | 已删除 | ❌ 废弃 |
| 业务分类 (system/productivity/...) | core.SkillCategory | ❌ 已删除 |
| v3.0分类 (knowledge/llm/tool/...) | skill.model.SkillCategory | ✅ 正确 |

## 二、API冗余分析

### 2.1 查询参数冗余

#### SkillQuery (✅ 已更新为v3.0)
```java
// 旧版 (String类型)
private String category;

// 新版 (枚举类型)
private SkillForm form;           // 新增
private SkillCategory category;   // 改为枚举
private SceneType sceneType;      // 新增
```

#### PendingSceneInfo (✅ 已更新为v3.0)
```java
// 已更新
private SkillCategory category;   // ✅ 已改为枚举
private SkillForm form;           // ✅ 新增
private SceneType sceneType;      // ✅ 新增
```

#### SceneInfo (✅ 已更新为v3.0)
```java
// 已更新
private SkillCategory category;   // ✅ 已改为枚举
private SkillForm form;           // ✅ 新增
private SceneType sceneType;      // ✅ 新增
```

#### SceneDetail (✅ 已更新为v3.0)
```java
// 已更新
private SkillCategory category;   // ✅ 已改为枚举
private SkillForm form;           // ✅ 新增
private SceneType sceneType;      // ✅ 新增
```

#### CapabilityDetail (✅ 已更新为v3.0)
```java
// 已更新
private SkillCategory category;   // ✅ 已改为枚举
private SkillForm form;           // ✅ 新增
```

#### DiscoveryRequest (✅ 已更新为v3.0)
```java
// 已更新
private SkillCategory category;   // ✅ 已改为枚举
private SkillForm form;           // ✅ 新增
private SceneType sceneType;      // ✅ 新增
```

### 2.2 SDK SkillPackage 冗余字段

```java
// agent-sdk SkillPackage 当前状态
private String sceneId;           // ❌ 废弃，应使用 form + sceneType
private String category;          // ⚠️ String类型，应改为枚举
private String subCategory;       // ❌ 废弃，v3.0不再使用子分类

// 建议修改
private SkillForm form;           // 新增
private SkillCategory category;   // 改为枚举
private SceneType sceneType;      // 新增（仅SCENE时有效）
```

### 2.3 冗余字段清单

| 字段 | 位置 | 状态 | 说明 |
|------|------|------|------|
| `sceneId` | SkillPackage | ❌ 废弃 | 改用 form + sceneType |
| `sceneSkill` | 多处 | ❌ 废弃 | 改用 form |
| `mainFirst` | 多处 | ❌ 废弃 | 改用 sceneType |
| `subCategory` | SkillPackage | ❌ 废弃 | v3.0不再使用 |
| `category` (String) | scene-engine | ✅ 已清理 | 已改为 SkillCategory 枚举 |

## 三、影响范围

### 3.1 scene-engine 模块 (✅ 全部完成)

| 文件 | 冗余字段 | 状态 |
|------|---------|------|
| SceneInfo.java | category (String) | ✅ 已更新为枚举 |
| PendingSceneInfo.java | category (String) | ✅ 已更新为枚举 |
| SceneDetail.java | category (String) | ✅ 已更新为枚举 |
| CapabilityDetail.java | category (String) | ✅ 已更新为枚举 |
| DiscoveryRequest.java | category (String) | ✅ 已更新为枚举 |
| SkillQuery.java | category (String) | ✅ 已更新为枚举 |
| PendingSceneQuery.java | 无 | ✅ 保持 |

### 3.2 agent-sdk 模块 (✅ v3.0重构已完成)

| 文件 | 冗余字段 | 状态 |
|------|---------|------|
| SkillPackage.java | sceneId, category, subCategory | ✅ 已重构为v3.0 |
| SkillManifest.java | sceneSkill, mainFirst | ✅ 已重构为v3.0 |
| SceneConfig.java | sceneSkill, mainFirst | ✅ 已重构为v3.0 |
| SkillDefinition.java | sceneSkill, mainFirst | ✅ 已重构为v3.0 |

## 四、v3.0 完成状态

### 4.1 scene-engine 新增枚举

| 枚举 | 包路径 | 说明 |
|------|--------|------|
| SkillForm | net.ooder.scene.skill.model | 技能形态 (SCENE/STANDALONE) |
| SceneType | net.ooder.scene.skill.model | 场景类型 (AUTO/TRIGGER/HYBRID) |
| SkillCategory | net.ooder.scene.skill.model | 技能分类 (8种) |
| ServicePurpose | net.ooder.scene.skill.model | 服务目的 (多维属性) |

### 4.2 agent-sdk 新增枚举

| 枚举 | 包路径 | 说明 |
|------|--------|------|
| SkillForm | net.ooder.skills.api | 技能形态 |
| SceneType | net.ooder.skills.api | 场景类型 |
| SkillCategory | net.ooder.skills.api | 技能分类 |
| ServicePurpose | net.ooder.skills.api | 服务目的 |

## 五、执行计划

### scene-engine 模块 (✅ 全部完成)

1. ✅ 删除 core.SkillCategory
2. ✅ 更新 SkillQuery
3. ✅ 更新 SceneInfo
4. ✅ 更新 PendingSceneInfo
5. ✅ 更新 SceneDetail
6. ✅ 更新 CapabilityDetail
7. ✅ 更新 DiscoveryRequest

### agent-sdk 模块 (✅ 全部完成)

1. ✅ 创建 v3.0 枚举类
2. ✅ SkillPackage 重构
3. ✅ SkillManifest 重构
4. ✅ SceneConfig 重构

---

**文档版本**: 1.1.0
**更新日期**: 2026-03-10
**作者**: Engine Team
