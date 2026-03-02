# 术语变更实施文档

## 文档信息

| 项目 | 说明 |
|------|------|
| 版本 | v1.0 |
| 日期 | 2026-03-02 |
| 状态 | 已实施 |
| 依据 | TERMINOLOGY_MAPPING.md |

---

## 一、变更概述

根据 `TERMINOLOGY_MAPPING.md` 的术语映射，已完成以下代码变更：

### 1.1 核心术语变更

| 旧术语 | 新术语 | 状态 |
|--------|--------|------|
| `SceneDefinition` | `SceneCapability` | ✅ 已创建新类，旧类标记@Deprecated |
| `primaryScene` | `mainFirst` | ✅ 已添加新字段，旧字段标记@Deprecated |
| `collaborativeScenes` | `collaborativeCapabilities` | ✅ 已添加新字段，旧字段标记@Deprecated |
| `WorkflowDefinition` | `capabilityChains` | ✅ 已添加新字段，旧字段标记@Deprecated |
| `SceneTemplate` | `SceneCapability` (概念统一) | ✅ SceneTemplate保留作为配置载体 |

### 1.2 CapabilityType枚举扩展

新增以下枚举值：

```java
public enum CapabilityType {
    // 原有类型...
    
    /** 原子能力 - 单一功能，不可分解 */
    ATOMIC,
    
    /** 组合能力 - 组合多个原子能力 */
    COMPOSITE,
    
    /** 协作能力 - 跨场景协作能力 */
    COLLABORATIVE
}
```

---

## 二、文件变更清单

### 2.1 新增文件

| 文件路径 | 说明 |
|----------|------|
| `agent-sdk/agent-sdk-api/src/main/java/net/ooder/sdk/api/scene/SceneCapability.java` | 场景能力类（新术语） |
| `docs/v2.3/TERMINOLOGY_IMPLEMENTATION.md` | 本实施文档 |

### 2.2 修改文件

| 文件路径 | 变更内容 |
|----------|----------|
| `agent-sdk/agent-sdk-api/src/main/java/net/ooder/sdk/api/capability/CapabilityType.java` | 新增ATOMIC/COMPOSITE/COLLABORATIVE枚举值 |
| `agent-sdk/skills-framework/src/main/java/net/ooder/skills/api/Capability.java` | 新增mainFirst、capabilities、capabilityChains等字段 |
| `agent-sdk/skills-framework/src/main/java/net/ooder/skills/api/SkillManifest.java` | 新增mainFirstScene、collaborativeCapabilities、sceneCapabilities等字段 |
| `agent-sdk/skills-framework/src/main/java/net/ooder/skills/api/SceneTemplate.java` | 更新术语，新增mainFirstConfig、capabilityChains等字段 |

---

## 三、向后兼容性

### 3.1 兼容策略

所有旧术语都保留了**向后兼容**：

1. **旧类保留**：`SceneDefinition` 类保留并标记 `@Deprecated`
2. **旧方法保留**：所有旧getter/setter方法保留并标记 `@Deprecated`
3. **数据兼容**：新方法自动回退到旧字段（如 `getMainFirstScene()` 优先返回 `mainFirstScene`，否则返回 `primaryScene`）

### 3.2 迁移示例

#### 旧代码（仍然可用）

```java
SkillManifest manifest = new SkillManifest();
manifest.setPrimaryScene(sceneConfig);  // @Deprecated 但仍可用
manifest.setCollaborativeScenes(Arrays.asList("scene1", "scene2"));  // @Deprecated 但仍可用
```

#### 新代码（推荐）

```java
SkillManifest manifest = new SkillManifest();
manifest.setMainFirstScene(sceneConfig);  // 新术语
manifest.setCollaborativeCapabilities(Arrays.asList("cap1", "cap2"));  // 新术语

// 设置场景能力定义
SkillManifest.SceneCapabilityDef sceneCap = new SkillManifest.SceneCapabilityDef();
sceneCap.setCapabilityId("my-scene-cap");
sceneCap.setMainFirst(true);

SkillManifest.MainFirstConfig mainFirstConfig = new SkillManifest.MainFirstConfig();
mainFirstConfig.setSelfChecks(Arrays.asList(...));
sceneCap.setMainFirstConfig(mainFirstConfig);

manifest.setSceneCapabilities(Arrays.asList(sceneCap));
```

---

## 四、新增数据结构

### 4.1 Capability类新增

```java
public class Capability {
    // 原有字段...
    
    // 新增字段
    private String capabilityType;
    private List<String> capabilities;  // 子能力ID列表
    private boolean mainFirst;  // 自驱入口标识
    private MainFirstConfig mainFirstConfig;
    private List<CollaborativeCapabilityRef> collaborativeCapabilities;
    private Map<String, CapabilityChain> capabilityChains;
    
    // 新增内部类
    public static class MainFirstConfig { ... }
    public static class SelfCheck { ... }
    public static class SelfStart { ... }
    public static class SelfDriveConfig { ... }
    public static class CollaborationStart { ... }
    public static class CollaborativeCapabilityRef { ... }
    public static class CapabilityChain { ... }
}
```

### 4.2 SkillManifest类新增

```java
public class SkillManifest {
    // 原有字段...
    
    // 新增字段
    private SceneConfig mainFirstScene;  // 自驱入口场景配置
    private List<String> collaborativeCapabilities;  // 协作能力ID列表
    private List<SceneDependency> collaborativeCapabilityDependencies;
    private List<SceneCapabilityDef> sceneCapabilities;  // 场景能力定义列表
    
    // 新增内部类
    public static class SceneCapabilityDef { ... }
    public static class MainFirstConfig { ... }
    public static class SelfCheck { ... }
    public static class SelfStart { ... }
    public static class SelfDriveConfig { ... }
    public static class CollaborationStart { ... }
    public static class CollaborativeCapabilityRef { ... }
}
```

### 4.3 SceneTemplate类新增

```java
public class SceneTemplate {
    // 原有字段...
    
    // 新增字段
    private List<CollaborativeCapabilityRef> collaborativeCapabilities;  // 协作能力配置
    private MainFirstConfig mainFirstConfig;  // 自驱入口配置
    private List<CapabilityChainDef> capabilityChains;  // 能力链定义
    
    // 新增内部类
    public static class CollaborativeCapabilityRef { ... }
    public static class MainFirstConfig { ... }
    public static class SelfCheckDef { ... }
    public static class SelfStartDef { ... }
    public static class SelfDriveDef { ... }
    public static class CollaborationStartDef { ... }
    public static class CapabilityChainDef { ... }
}
```

---

## 五、开发任务更新

根据术语变更，SDK_TEAM_TASKS.md 中的任务需要相应调整：

### 5.1 任务状态更新

| 任务ID | 任务名称 | 状态 | 备注 |
|--------|----------|------|------|
| SDK-001 | SceneDependencyResolver增强 | ✅ 已完成 | 已适配新术语 |
| SDK-002 | InstallWithDependencies增强 | ✅ 已完成 | 已适配新术语 |
| SDK-003 | 版本兼容性检查 | 🔄 进行中 | 需要更新术语引用 |
| ENG-001 | SceneGroupManager实现 | ⏳ 待开始 | 使用新术语 |
| ENG-002 | 场景激活流程 | ⏳ 待开始 | 改为"能力自驱流程" |
| ENG-003 | 场景间通信 | ⏳ 待开始 | 改为"能力间通信" |
| SCN-001 | 场景模板解析增强 | ⏳ 待开始 | 使用新术语 |
| SCN-002 | 能力动态绑定 | ⏳ 待开始 | 使用新术语 |
| SCN-003 | 部署流程整合 | ⏳ 待开始 | 使用新术语 |

### 5.2 新增任务

| 任务ID | 任务名称 | 优先级 | 说明 |
|--------|----------|--------|------|
| SDK-004 | MainFirstService接口定义 | P0 | 自驱服务核心接口 |
| SDK-005 | 驱动能力接口定义 | P1 | IntentReceiver/Scheduler/EventListener |
| SDK-006 | 能力链服务实现 | P1 | CapabilityChainService |

---

## 六、下一步工作

1. **继续实施SDK_TEAM_TASKS.md中的剩余任务**
   - 使用新术语进行开发
   - 避免使用已标记@Deprecated的旧API

2. **创建新的服务接口**
   - `MainFirstService` - 自驱服务
   - `IntentReceiver` - 意图接收
   - `SchedulerCapability` - 时间驱动
   - `EventListenerCapability` - 事件监听
   - `CapabilityChainService` - 能力链服务

3. **更新文档**
   - 更新API文档中的术语
   - 更新开发指南
   - 添加迁移指南

---

## 七、验证清单

- [x] CapabilityType枚举扩展
- [x] SceneCapability类创建
- [x] Capability类扩展
- [x] SkillManifest类扩展
- [x] SceneTemplate类更新
- [x] 向后兼容性保证（@Deprecated标记）
- [ ] 编译通过
- [ ] 单元测试通过

---

*作者: Ooder Team*  
*更新时间: 2026-03-02*
