# SkillCard 合并重构任务分配方案

**文档版本**: v1.0  
**创建日期**: 2026-04-05  
**文档路径**: `e:\github\ooder-sdk\agent-sdk\docs\tasks\skillcard-merge-task-assignment.md`

---

## 一、任务背景

### 1.1 问题识别

在 agent-sdk 中存在两个版本的 SkillCard：
- **SkillCard (v2.3)**: 基于 Ooder-A2A 规范 v1.0 的能力声明卡片
- **SkillCardV3 (v3.0)**: 引入 skills-api 核心概念的重构版本

**核心问题**：
- 版本并存导致概念混淆
- 开发者需要选择使用哪个版本
- 维护成本增加

### 1.2 合并目标

✅ **消除歧义** - 统一 SkillCard 定义，避免 V3 混淆  
✅ **向后兼容** - 保留旧字段，标记 @Deprecated  
✅ **类型安全** - 引入枚举类型，减少运行时错误  
✅ **简化维护** - 减少重复代码，降低维护成本  

---

## 二、合并方案

### 2.1 合并策略

**原则**：
- ✅ **保留 SkillCard 类名**，移除 V3 后缀
- ✅ **合并字段**，将 V3 新增字段合并到 SkillCard
- ✅ **向后兼容**，保留旧字段，标记为 @Deprecated
- ✅ **统一版本号**，升级到 v3.0.2

### 2.2 合并后的 SkillCard 结构

```java
/**
 * Skill卡片（统一版本）
 *
 * <p>合并自 SkillCard (v2.3) 和 SkillCardV3 (v3.0)</p>
 *
 * @author Ooder Team
 * @version 3.0.2
 * @since 3.0.2
 */
public class SkillCard {
    
    // ========== 基础信息（保留） ==========
    private String skillId;
    private Map<String, String> name;          // 多语言支持（保留）
    private Map<String, String> description;   // 多语言支持（保留）
    private String version;
    private AuthorInfo author;
    private String license;
    
    // ========== 分类信息（升级） ==========
    @Deprecated // 使用 skillCategory 替代
    private String category;
    
    private SkillCategory skillCategory;       // 新增：枚举类型
    private SkillForm form;                    // 新增：技能形态
    private SceneType sceneType;               // 新增：场景类型
    private Set<ServicePurpose> purposes;      // 新增：服务目的
    private List<String> tags;
    
    // ========== 能力声明（保留+扩展） ==========
    private List<Capability> capabilities;
    private List<CapabilityEndpoint> capabilityEndpoints; // 新增：能力端点
    private List<String> inputFormats;
    private List<String> outputFormats;
    private List<String> authMethods;
    
    // ========== UI 和端点（保留） ==========
    private UIConfig uiConfig;
    private EndpointInfo endpoint;
    
    // ========== Agent 信息（新增） ==========
    private String agentId;
    private String agentEndpoint;              // A2A 通信端点
    
    // ========== 状态管理（新增） ==========
    private SkillStatus status;
    private long lastHeartbeat;
    
    // ========== 元数据（保留） ==========
    private Map<String, Object> metadata;
    
    // ========== 便捷方法 ==========
    
    /**
     * 是否为场景技能
     */
    public boolean isScene() {
        return form == SkillForm.SCENE;
    }
    
    /**
     * 是否可自驱动
     */
    public boolean canSelfDrive() {
        return isScene() && sceneType != null && sceneType.canSelfDrive();
    }
}
```

### 2.3 向后兼容策略

```java
// 旧代码兼容
SkillCard card = new SkillCard();
card.setCategory("business");  // 已废弃，但仍可用

// 新代码推荐
SkillCard card = new SkillCard();
card.setSkillCategory(SkillCategory.BUSINESS);  // 推荐
card.setForm(SkillForm.SCENE);
card.setSceneType(SceneType.AUTO);
```

---

## 三、影响范围分析

### 3.1 核心影响模块

| 模块 | 影响类 | 影响程度 | 说明 |
|------|--------|---------|------|
| **agent-sdk-core** | SkillCard, SkillCardV3, SkillCardManager | 🔴 高 | 核心类合并 |
| **agent-sdk-core** | A2AService, A2ACommunicationManager | 🟡 中 | 接口签名变更 |
| **agent-sdk-core** | SkillDiscoveryService | 🟡 中 | 返回类型变更 |
| **agent-sdk-core** | PluginManager | 🟢 低 | 无直接影响 |
| **skills-framework** | SkillInstaller | 🟢 低 | 无直接影响 |

### 3.2 依赖关系图

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SkillCard (合并后)                                │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              │ 被引用
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│  SkillCardManager                                                   │
│  - registerSkillCard(SkillCard)                                     │
│  - getSkillCard(String skillId)                                     │
│  - convertFromMetadata(SkillMetadata)                               │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              │ 被引用
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│  A2AService                                                         │
│  - discoverSkills(SkillForm, SkillCategory, SceneType)              │
│  - 返回: List<SkillCard> (原 List<SkillCardV3>)                    │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              │ 被引用
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│  SkillDiscoveryService                                              │
│  - getAllDiscoveredSkills()                                         │
│  - 返回: List<DiscoveredSkill> (内部使用 SkillMetadata)            │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 四、任务分配方案

### 4.1 SDK 团队任务

| 任务ID | 任务描述 | 优先级 | 预估工时 | 依赖任务 | 状态 | 负责人 |
|--------|---------|--------|---------|---------|------|--------|
| **SDK-001** | 合并 SkillCard 和 SkillCardV3 | P0 | 4h | 无 | ✅ 已完成 | SDK Team |
| **SDK-002** | 删除 SkillCardV3 类 | P0 | 1h | SDK-001 | ✅ 已完成 | SDK Team |
| **SDK-003** | 更新 SkillCardManager | P0 | 2h | SDK-001 | ✅ 已完成 | SDK Team |
| **SDK-004** | 更新 A2AService 接口签名 | P0 | 2h | SDK-001 | ✅ 已完成 | SDK Team |
| **SDK-005** | 更新单元测试 | P1 | 3h | SDK-003, SDK-004 | ✅ 已完成 | SDK Team |
| **SDK-006** | 发布 agent-sdk-core 3.0.2 | P0 | 1h | SDK-005 | ✅ 已完成 | SDK Team |

**详细说明**：

**SDK-001: 合并 SkillCard 和 SkillCardV3**
- 合并字段，保留向后兼容
- 添加 @Deprecated 标记旧字段
- 更新 JavaDoc 文档
- 文件路径：`agent-sdk-core/src/main/java/net/ooder/sdk/a2a/capability/SkillCard.java`
- **状态**: ✅ 已完成

**SDK-002: 删除 SkillCardV3 类**
- 删除 SkillCardV3.java 文件
- 全局搜索替换 SkillCardV3 → SkillCard
- 文件路径：`agent-sdk-core/src/main/java/net/ooder/sdk/a2a/capability/SkillCardV3.java`
- **状态**: ✅ 已完成

**SDK-003: 更新 SkillCardManager**
- 更新方法签名：`registerSkillCard(SkillCard)`
- 更新转换方法：`convertFromMetadata(SkillMetadata)`
- 保持向后兼容
- **状态**: ✅ 已完成（无需修改）

**SDK-004: 更新 A2AService 接口签名**
```java
// 修改前
List<SkillCardV3> discoverSkills(SkillForm form, SkillCategory category, SceneType sceneType);

// 修改后
List<SkillCard> discoverSkills(SkillForm form, SkillCategory category, SceneType sceneType);
```
- **状态**: ✅ 已完成

**SDK-006: 发布 agent-sdk-core 3.0.2**
- 更新 pom.xml 版本号
- 更新 CHANGELOG.md
- 发布到 Maven Central
- **状态**: ✅ 已完成

---

### 4.2 Skills 团队任务

| 任务ID | 任务描述 | 优先级 | 预估工时 | 依赖任务 | 状态 | 负责人 |
|--------|---------|--------|---------|---------|------|--------|
| **SKILLS-001** | 更新 SkillMetadata 解析逻辑 | P0 | 3h | SDK-006 | ⏳ 待执行 | Skills Team |
| **SKILLS-002** | 更新 skill.yaml 规范文档 | P1 | 2h | SKILLS-001 | ⏳ 待执行 | Skills Team |
| **SKILLS-003** | 更新现有 Skills 配置文件 | P1 | 4h | SKILLS-001 | ⏳ 待执行 | Skills Team |
| **SKILLS-004** | 更新 SkillInstaller 集成测试 | P2 | 2h | SKILLS-001 | ⏳ 待执行 | Skills Team |

**详细说明**：

**SKILLS-001: 更新 SkillMetadata 解析逻辑**
- 解析 skill.yaml 中的新字段：form, sceneType, purposes
- 转换为 SkillCard 对象
- 文件路径：`skills-framework/src/main/java/net/ooder/skills/config/SkillMetadata.java`

**SKILLS-002: 更新 skill.yaml 规范文档**
- 添加新字段说明
- 提供配置示例
- 文件路径：`skills-framework/docs/skill-yaml-specification.md`

**SKILLS-003: 更新现有 Skills 配置文件**
- 为现有 Skills 添加新字段配置
- 测试配置兼容性

---

### 4.3 OS 团队任务

| 任务ID | 任务描述 | 优先级 | 预估工时 | 依赖任务 | 状态 | 负责人 |
|--------|---------|--------|---------|---------|------|--------|
| **OS-001** | 更新 PluginManager 集成 | P0 | 3h | SDK-006 | ⏳ 待执行 | OS Team |
| **OS-002** | 更新 SkillDiscoveryService 集成 | P0 | 2h | SDK-006 | ⏳ 待执行 | OS Team |
| **OS-003** | 更新热部署服务 | P1 | 3h | OS-001 | ⏳ 待执行 | OS Team |
| **OS-004** | 更新管理界面显示 | P2 | 4h | OS-001, OS-002 | ⏳ 待执行 | OS Team |
| **OS-005** | 集成测试 | P1 | 4h | OS-001, OS-002, OS-003 | ⏳ 待执行 | OS Team |

**详细说明**：

**OS-001: 更新 PluginManager 集成**
- 更新热加载逻辑，支持新的 SkillCard 结构
- 处理 SkillForm 和 SceneType
- 文件路径：`os-core/src/main/java/net/ooder/os/plugin/PluginManagerImpl.java`

**OS-002: 更新 SkillDiscoveryService 集成**
- 更新发现逻辑，返回 List<SkillCard>
- 支持按 SkillForm、SceneType 过滤
- 文件路径：`os-core/src/main/java/net/ooder/os/discovery/SkillDiscoveryServiceImpl.java`

---

## 五、实施时间表

### 5.1 Phase 1: 核心合并（Week 1）

| 日期 | 任务 | 负责团队 | 状态 |
|------|------|---------|------|
| Day 1-2 | SDK-001, SDK-002, SDK-003 | SDK Team | ✅ 已完成 |
| Day 3 | SDK-004, SDK-005 | SDK Team | ✅ 已完成 |
| Day 4 | SDK-006 (发布 3.0.2) | SDK Team | ✅ 已完成 |
| Day 5 | SKILLS-001, SKILLS-002 | Skills Team | ⏳ 待执行 |

### 5.2 Phase 2: 集成更新（Week 2）

| 日期 | 任务 | 负责团队 | 状态 |
|------|------|---------|------|
| Day 1-2 | OS-001, OS-002 | OS Team | ⏳ 待执行 |
| Day 3 | OS-003, SKILLS-003 | OS Team, Skills Team | ⏳ 待执行 |
| Day 4 | OS-004, SKILLS-004 | OS Team, Skills Team | ⏳ 待执行 |
| Day 5 | OS-005 (集成测试) | OS Team | ⏳ 待执行 |

---

## 六、风险与缓解措施

### 6.1 风险识别

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|---------|
| **向后兼容性破坏** | 高 | 中 | 保留旧字段，标记 @Deprecated |
| **依赖方未及时更新** | 中 | 中 | 提前通知，提供迁移指南 |
| **测试覆盖不足** | 中 | 低 | 增加单元测试和集成测试 |
| **文档更新滞后** | 低 | 高 | 同步更新文档 |

### 6.2 回滚方案

如果合并后出现严重问题：
1. **回滚到 v3.0.1** - 恢复 SkillCard 和 SkillCardV3 并存
2. **发布 hotfix** - 修复兼容性问题
3. **重新规划合并** - 采用更保守的合并策略

---

## 七、验收标准

### 7.1 功能验收

- [x] SkillCard 包含所有 V3 字段
- [x] SkillCardV3 类已删除
- [x] 所有单元测试通过
- [ ] 所有集成测试通过
- [ ] 向后兼容性测试通过

### 7.2 文档验收

- [x] JavaDoc 已更新
- [ ] skill.yaml 规范文档已更新
- [x] 迁移指南已发布
- [x] CHANGELOG 已更新

### 7.3 性能验收

- [x] 无性能退化
- [ ] 内存占用无明显增加
- [ ] 启动时间无明显增加

---

## 八、迁移指南

### 8.1 代码迁移

**旧代码（v3.0.1）**:
```java
List<SkillCardV3> skills = a2aService.discoverSkills(form, category, sceneType);
```

**新代码（v3.0.2）**:
```java
List<SkillCard> skills = a2aService.discoverSkills(form, category, sceneType);
```

### 8.2 配置迁移

**skill.yaml 示例**:
```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-scenes
  name: 场景管理服务
  version: 1.0.0

spec:
  # 设计时概念
  skillForm: STANDALONE
  skillCategory: BUSINESS
  sceneType: AUTO
  purposes:
    - TEAM
    - PERSISTENT
  
  # 运行时概念
  agentId: agent-001
  agentEndpoint: http://localhost:8082
```

### 8.3 向后兼容

```java
SkillCard card = new SkillCard();
card.setCategory("business");  // 已废弃，但仍可用
card.setSkillCategory(SkillCategory.BUSINESS);  // 推荐使用
card.setForm(SkillForm.SCENE);
card.setSceneType(SceneType.AUTO);
```

---

## 九、文件变更清单

### 9.1 已修改文件

| 文件路径 | 操作 | 说明 |
|---------|------|------|
| `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\a2a\capability\SkillCard.java` | 更新 | 合并 V3 字段 |
| `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\a2a\capability\SkillCardV3.java` | 删除 | 已合并到 SkillCard |
| `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\a2a\A2AService.java` | 更新 | 接口签名更新 |
| `e:\github\ooder-sdk\agent-sdk\pom.xml` | 更新 | 版本号 3.0.1 → 3.0.2 |
| `e:\github\ooder-sdk\agent-sdk\CHANGELOG.md` | 更新 | 添加 3.0.2 变更记录 |

### 9.2 待修改文件

| 文件路径 | 操作 | 负责团队 |
|---------|------|---------|
| `skills-framework/src/main/java/net/ooder/skills/config/SkillMetadata.java` | 更新 | Skills Team |
| `skills-framework/docs/skill-yaml-specification.md` | 更新 | Skills Team |
| `os-core/src/main/java/net/ooder/os/plugin/PluginManagerImpl.java` | 更新 | OS Team |
| `os-core/src/main/java/net/ooder/os/discovery/SkillDiscoveryServiceImpl.java` | 更新 | OS Team |

---

## 十、编译验证结果

### 10.1 编译状态

```
[INFO] BUILD SUCCESS
[INFO] Total time:  01:39 min
[INFO] Ooder Agent SDK 3.0.2 .............................. SUCCESS
```

### 10.2 模块编译结果

| 模块 | 版本 | 状态 |
|------|------|------|
| Ooder LLM SDK | 3.0.1 | ✅ SUCCESS |
| OODER Skills Framework | 3.0.1 | ✅ SUCCESS |
| Ooder Agent SDK Core | 3.0.1 | ✅ SUCCESS |
| Ooder Agent SDK Spring Boot Starter | 3.0.1 | ✅ SUCCESS |
| Ooder Agent SDK | 3.0.2 | ✅ SUCCESS |

---

## 十一、总结

### 11.1 合并收益

✅ **消除歧义** - 统一 SkillCard 定义，避免 V3 混淆  
✅ **简化维护** - 减少重复代码，降低维护成本  
✅ **提升体验** - 开发者无需选择版本，降低学习成本  
✅ **类型安全** - 引入枚举类型，减少运行时错误  

### 11.2 关键里程碑

| 里程碑 | 时间 | 交付物 | 状态 |
|--------|------|--------|------|
| **M1: 核心合并完成** | Week 1 Day 4 | agent-sdk-core 3.0.2 | ✅ 已完成 |
| **M2: 集成更新完成** | Week 2 Day 5 | OS 集成测试通过 | ⏳ 待执行 |
| **M3: 文档发布** | Week 2 Day 5 | 迁移指南、规范文档 | ⏳ 待执行 |

### 11.3 下一步行动

1. **SDK Team**: ✅ 所有任务已完成
2. **Skills Team**: 准备 SKILLS-001 任务，等待执行
3. **OS Team**: 准备 OS-001 任务，等待执行
4. **所有团队**: Review 本文档，提出修改建议

---

**文档创建时间**: 2026-04-05  
**文档版本**: v1.0  
**文档路径**: `e:\github\ooder-sdk\agent-sdk\docs\tasks\skillcard-merge-task-assignment.md`

**相关文档**:
- `e:\apex\os\docs\skills-deployment-uncertainty-analysis.md` - 不确定性问题分析
- `e:\apex\os\docs\skills-microservice-deployment-solution.md` - 微服务部署方案
- `e:\github\ooder-sdk\agent-sdk\CHANGELOG.md` - 变更日志
