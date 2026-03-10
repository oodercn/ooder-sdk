# SE SDK 协同开发回复报告

## 一、协同背景

skill-scene 团队于 2026-03-10 发起协同开发请求，SE SDK (scene-engine) 团队现回复如下。

---

## 二、协同需求回复

### 2.1 v3.0 模型集成 ✅ 已完成

| 需求项 | SE SDK 状态 | 说明 |
|--------|-------------|------|
| SkillForm 枚举 | ✅ 已实现 | `net.ooder.scene.skill.model.SkillForm` |
| SceneType 枚举 | ✅ 已实现 | `net.ooder.scene.skill.model.SceneType` |
| SkillCategory 枚举 | ✅ 已实现 | `net.ooder.scene.skill.model.SkillCategory` |
| ServicePurpose 枚举 | ✅ 已实现 | `net.ooder.scene.skill.model.ServicePurpose` |
| Skill 接口 | ✅ 已实现 | `net.ooder.scene.skill.model.Skill` |
| RichSkill 实现 | ✅ 已实现 | 实现 Skill 接口，兼容旧数据 |

### 2.2 分类 API 清理 ✅ 已完成

| 清理项 | 状态 | 说明 |
|--------|------|------|
| 删除 core.SkillCategory | ✅ 已删除 | 移除旧的静态常量分类 |
| SceneInfo.category | ✅ 已更新 | String → SkillCategory 枚举 |
| PendingSceneInfo.category | ✅ 已更新 | String → SkillCategory 枚举 |
| SceneDetail.category | ✅ 已更新 | String → SkillCategory 枚举 |
| CapabilityDetail.category | ✅ 已更新 | String → SkillCategory 枚举 |
| DiscoveryRequest.category | ✅ 已更新 | String → SkillCategory 枚举 |
| SkillQuery.category | ✅ 已更新 | String → SkillCategory 枚举 |

### 2.3 新增字段支持

| 字段 | 类型 | 说明 |
|------|------|------|
| `form` | `SkillForm` | 技能形态 (SCENE/STANDALONE) |
| `sceneType` | `SceneType` | 场景类型 (AUTO/TRIGGER/HYBRID) |

---

## 三、争议点回复

### 3.1 分类体系不一致 → 方案C：两层分类

**SE SDK 立场**：
- Engine 保持 8 个功能分类 (SkillCategory)
- skill-index 可定义业务领域 (domain) 字段
- 两者独立，可组合查询

### 3.2 ASS 归属问题 → 方案A：合并到 AUTO

**SE SDK 立场**：
- ASS = AUTO + INTERNAL visibility
- 建议增加 `visibility` 字段区分可见性
- 不需要单独的场景类型

### 3.3 错误分类修复 → 必须修复

**SE SDK 立场**：
- `abs/tbs/ass` 是场景类型，不是分类
- 必须修复，不能保留错误用法

### 3.4 nexus-ui 分类 → 方案A：合并到 UI

**SE SDK 立场**：
- nexus-ui 本质是 UI 类技能
- 通过其他字段区分加载方式

---

## 四、待协同事项

### 4.1 SE SDK 需要 skill-scene 支持

| 事项 | 说明 | 优先级 |
|------|------|--------|
| 数据迁移脚本 | 旧分类字段迁移到 v3.0 字段 | P1 |
| 前端适配 | 更新分类筛选 UI | P1 |
| 测试验证 | 端到端集成测试 | P2 |

### 4.2 skill-scene 需要的 SE SDK 支持

| 事项 | SE SDK 状态 | 说明 |
|------|-------------|------|
| v3.0 枚举类 | ✅ 已完成 | 可直接使用 |
| Skill 接口 | ✅ 已完成 | 可直接实现 |
| 查询 API | ✅ 已完成 | 支持按 form/category/sceneType 过滤 |
| Maven 依赖 | ✅ 已发布 | `net.ooder:scene-engine:2.3.1` |

---

## 五、版本信息

| 项目 | 版本 |
|------|------|
| scene-engine | 2.3.1 |
| agent-sdk | 2.3.1 |
| Java | 8+ |

### Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

---

## 六、下一步计划

### 6.1 SE SDK 待办

| 任务 | 状态 | 预计完成 |
|------|------|----------|
| Git 提交 v2.3.1 代码 | ⏳ 待执行 | 今日 |
| 更新 CHANGELOG | ✅ 已完成 | - |
| 创建集成指南 | ✅ 已完成 | - |

### 6.2 建议协同流程

```
1. skill-scene 更新依赖到 2.3.1
2. skill-scene 执行数据迁移脚本
3. skill-scene 更新前端 UI
4. 双方进行端到端测试
5. 正式发布
```

---

## 七、联系方式

| 角色 | 联系方式 |
|------|----------|
| SE SDK 负责人 | [待填写] |
| 技术支持 | [待填写] |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: SE SDK Team (scene-engine)
