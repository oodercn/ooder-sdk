# 应用团队集成指南 - scene-engine v2.3.1

## 一、版本信息

| 项目 | 值 |
|------|-----|
| 版本号 | 2.3.1 |
| 发布日期 | 2026-03-10 |
| Maven坐标 | `net.ooder:scene-engine:2.3.1` |
| Java版本 | Java 8+ |

## 二、Maven依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

## 三、v3.0 新模型快速入门

### 3.1 核心概念

**范式转变**：技能是唯一核心实体，场景是技能的形态属性。

```
类比文件系统：
├── 技能 (Skill)          → 文件/文件夹
├── 技能形态 (SkillForm)   → 文件类型 (file/folder)
├── 场景类型 (SceneType)   → 文件夹类型 (源码包/资源文件夹/普通文件夹)
└── 技能分类 (SkillCategory) → 文件扩展名 (.doc/.exe/.ai 等)
```

### 3.2 关键枚举

#### SkillForm（技能形态）

| 值 | 说明 | 类比 |
|----|------|------|
| `SCENE` | 场景技能（容器型） | 文件夹 |
| `STANDALONE` | 独立技能（原子型） | 文件 |

#### SceneType（场景类型）

| 值 | 说明 | 特性 |
|----|------|------|
| `AUTO` | 自主场景 | 可自驱动 |
| `TRIGGER` | 触发场景 | 被动响应 |
| `HYBRID` | 混合场景 | 主动+被动 |

#### SkillCategory（技能分类）

| 值 | 说明 | 类比 |
|----|------|------|
| `KNOWLEDGE` | 知识类 | .doc/.pdf |
| `LLM` | AI模型类 | .ai/.model |
| `TOOL` | 工具类 | .exe/.sh |
| `WORKFLOW` | 流程类 | .flow/.pipeline |
| `DATA` | 数据类 | .db/.json |
| `SERVICE` | 服务类 | .service/.api |
| `UI` | 界面类 | .ui/.html |
| `OTHER` | 其他 | - |

### 3.3 使用示例

```java
import net.ooder.scene.skill.model.*;

// 获取技能信息
Skill skill = richSkill;

// 判断技能形态
if (skill.getForm() == SkillForm.SCENE) {
    // 场景技能逻辑
    Optional<SceneType> sceneType = skill.getSceneType();
    if (sceneType.isPresent() && sceneType.get().canSelfDrive()) {
        // 自主场景，可自驱动
    }
} else {
    // 独立技能逻辑
}

// 获取分类
SkillCategory category = skill.getCategory();

// 获取服务目的
Set<ServicePurpose> purposes = skill.getPurposes();
```

## 四、迁移指南

### 4.1 字段映射

| 旧字段/概念 | 新字段/概念 | 说明 |
|-------------|-------------|------|
| `sceneSkill: true` | `form: SCENE` | 明确声明形态 |
| `sceneSkill: false` | `form: STANDALONE` | 明确声明形态 |
| `mainFirst: true` | `sceneType: AUTO` | 自主场景 |
| `mainFirst: false` | `sceneType: TRIGGER` | 触发场景 |
| `category: ABS/ASS` | `sceneType: AUTO` | 合并为自主场景 |
| `category: TBS` | `sceneType: TRIGGER` | 触发场景 |
| `NOT_SCENE_SKILL` | `form: STANDALONE` | 独立技能 |

### 4.2 废弃的API

| 废弃API | 替代方案 |
|---------|----------|
| `SceneSkillCategory` 枚举 | 使用 `SceneType` + `SkillForm` |
| `SceneSkillClassifier` | 直接读取 `form`/`sceneType` 字段 |
| `MetadataCompat` | 直接使用 v3.0 字段 |

### 4.3 兼容性保证

RichSkill 自动兼容旧数据：

```java
// 旧数据自动推断
// 如果 metadata 中没有 form 字段，会根据 sceneSkill 推断
SkillForm form = richSkill.getForm();

// 旧分类代码自动转换
// ABS/ASS → AUTO, TBS → TRIGGER
Optional<SceneType> sceneType = richSkill.getSceneType();
```

## 五、协同任务清单

### 5.1 必须完成（阻塞集成）

| 序号 | 任务 | 负责团队 | 状态 |
|------|------|----------|------|
| 1 | agent-sdk 创建 v3.0 枚举 | agent-sdk | ✅ 已完成 |
| 2 | agent-sdk 重写 SkillPackage | agent-sdk | ✅ 已完成 |
| 3 | agent-sdk 重写 SkillMetadata | agent-sdk | ✅ 已完成 |
| 4 | agent-sdk YAML解析器适配 | agent-sdk | ✅ 已完成 |
| 5 | scene-engine 新模型实现 | scene-engine | ✅ 已完成 |
| 6 | scene-engine 旧代码清理 | scene-engine | ✅ 已完成 |

### 5.2 建议完成（优化体验）

| 序号 | 任务 | 负责团队 | 优先级 |
|------|------|----------|--------|
| 1 | 数据库迁移脚本 | DBA | P1 |
| 2 | 单元测试覆盖 | 各团队 | P2 |
| 3 | 性能测试 | QA | P2 |
| 4 | 用户文档更新 | 文档团队 | P3 |

### 5.3 可选完成（长期优化）

| 序号 | 任务 | 说明 |
|------|------|------|
| 1 | 监控告警适配 | 新字段监控 |
| 2 | 日志格式统一 | v3.0 格式 |
| 3 | API文档更新 | Swagger/OpenAPI |

## 六、验证清单

集成前请确认：

- [ ] Maven 依赖正确引入
- [ ] 编译无错误
- [ ] 旧功能正常运行
- [ ] 新API可正常调用
- [ ] 日志输出正常
- [ ] 无 ClassNotFoundException

## 七、常见问题

### Q1: 旧代码是否会报错？

**A**: 不会。RichSkill 实现了向后兼容，旧字段会自动推断为新模型属性。

### Q2: 是否需要立即迁移？

**A**: 建议尽快迁移。旧API已标记废弃，将在下一大版本移除。

### Q3: 新旧模型如何共存？

**A**: RichSkill 同时支持新旧两种方式获取数据：
```java
// 旧方式（仍可用，但废弃）
String category = skillPackage.getMetadata().get("category");

// 新方式（推荐）
SkillCategory category = richSkill.getCategory();
```

### Q4: 数据库如何迁移？

**A**: 需要执行迁移脚本，将旧字段转换为新字段。具体脚本待DBA提供。

## 八、联系方式

| 角色 | 联系方式 |
|------|----------|
| scene-engine负责人 | [待填写] |
| agent-sdk负责人 | [待填写] |
| 技术支持 | [待填写] |

---

**文档版本**：v1.0
**更新日期**：2026-03-10
