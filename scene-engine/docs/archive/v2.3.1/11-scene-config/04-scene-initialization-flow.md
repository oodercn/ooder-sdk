# 场景激活数据初始化流程

## 概述

场景激活过程涉及多个核心组件的初始化，包括参与者、知识库绑定、能力绑定、菜单、配置、场景组和激活步骤执行。本文档详细描述每个初始化流程的执行逻辑。

---

## 一、整体流程概览

### 1.1 六步初始化流程

```
SceneGroupInitializer.initialize()
    │
    ├─▶ 1. loadScene()           场景加载
    │       └─▶ 加载场景定义和配置
    │
    ├─▶ 2. initializeAgents()    Agent 初始化
    │       └─▶ 创建 SceneAgent，分配角色
    │
    ├─▶ 3. parseCapabilities()   CAP 解析
    │       └─▶ 解析所需能力
    │
    ├─▶ 4. discoverSkills()      Skill 发现
    │       └─▶ 查询匹配的 Skill
    │
    ├─▶ 5. mountSkills()         Skill 挂载
    │       └─▶ 创建连接器并挂载
    │
    └─▶ 6. activate()            场景激活
            └─▶ 启动场景组
```

### 1.2 初始化上下文

```java
public class InitContext {
    private String initId;                    // 初始化ID
    private InitRequest request;              // 初始化请求
    private InitStatus status;                // 当前状态
    private List<SceneAgentCore> agents;      // Agent 列表
    private List<Capability> requiredCapabilities;  // 必需能力
    private List<Capability> optionalCapabilities;  // 可选能力
    private Map<String, List<SkillMatch>> skillMatches; // Skill 匹配
    private Map<String, SkillBinding> skillBindings;    // Skill 绑定
}
```

---

## 二、初始化参与者

### 2.1 Participant 实体

**包路径**: `net.ooder.scene.participant.Participant`

```java
public class Participant {
    // 参与者类型
    public enum Type {
        USER,           // 用户
        AGENT,          // 代理
        SUPER_AGENT     // 超级代理
    }
    
    // 参与者角色
    public enum Role {
        OWNER,          // 所有者
        MANAGER,        // 管理员
        COORDINATOR,    // 协调者
        EMPLOYEE,       // 员工
        OBSERVER,       // 观察者
        LLM_ASSISTANT   // LLM助手
    }
    
    // 参与者状态
    public enum Status {
        INVITED,        // 已邀请
        JOINED,         // 已加入
        ACTIVE,         // 活跃中
        LEFT,           // 已离开
        SUSPENDED,      // 已暂停
        REMOVED         // 已移除
    }
    
    private String participantId;     // 参与者ID
    private String userId;            // 用户ID
    private String displayName;       // 显示名称
    private Type type;                // 类型
    private Role role;                // 角色
    private Status status;            // 状态
    private long joinTime;            // 加入时间
    private long lastHeartbeat;       // 最后心跳
}
```

### 2.2 同步参与者流程

**方法**: `SceneGroupInitializer.syncParticipants()`

```
syncParticipants(context, seGroup)
    │
    ├─▶ 遍历 context.getAgents()
    │       │
    │       └─▶ createParticipantFromAgent(agent, sceneGroupId)
    │               │
    │               ├─▶ 生成 participantId
    │               ├─▶ 映射 MemberRole → Participant.Role
    │               │     PRIMARY → OWNER
    │               │     BACKUP → EMPLOYEE
    │               │     MEMBER → OBSERVER
    │               └─▶ 创建 Participant 对象
    │
    ├─▶ 创建创建者参与者
    │       │
    │       └─▶ role = OWNER
    │
    └─▶ seGroup.addParticipant(participant)
```

### 2.3 角色映射关系

| SDK MemberRole | SE Participant.Role | 说明 |
|---------------|---------------------|------|
| PRIMARY | OWNER | 主节点 → 所有者 |
| BACKUP | EMPLOYEE | 备节点 → 员工 |
| MEMBER | OBSERVER | 普通成员 → 观察者 |

---

## 三、初始化知识资料库

### 3.1 KnowledgeBinding 实体

**包路径**: `net.ooder.scene.skill.knowledge.KnowledgeBinding`

```java
public class KnowledgeBinding {
    private String sceneGroupId;    // 场景组ID
    private String kbId;            // 知识库ID
    private String kbName;          // 知识库名称
    private String layer;           // 层级 (skill/scene/org/global)
    private long bindTime;          // 绑定时间
}
```

### 3.2 KnowledgeBase 实体

**包路径**: `net.ooder.scene.skill.knowledge.KnowledgeBase`

```java
public class KnowledgeBase {
    private String kbId;            // 知识库ID
    private String name;            // 知识库名称
    private String ownerId;         // 所有者ID
    private String visibility;      // 可见性 (private/shared/public)
    private String embeddingModel;  // 嵌入模型
    private int chunkSize;          // 分块大小
    private int chunkOverlap;       // 分块重叠
    private long createTime;        // 创建时间
    private long documentCount;     // 文档数量
}
```

### 3.3 知识库绑定服务

**接口**: `KnowledgeBindingService`

```java
public interface KnowledgeBindingService {
    // 绑定知识库到场景
    void bindToScene(String sceneGroupId, String kbId, String layer);
    
    // 从场景解绑知识库
    void unbindFromScene(String sceneGroupId, String kbId);
    
    // 检索知识
    List<KnowledgeChunk> searchKnowledge(String sceneGroupId, String query, int topK);
    
    // 跨层检索知识
    List<KnowledgeChunk> crossLayerSearch(String sceneGroupId, String query, 
                                           List<String> layers, int topK);
    
    // 获取场景绑定的知识库列表
    List<KnowledgeBinding> getBindings(String sceneGroupId);
}
```

### 3.4 知识层级结构

```
知识检索优先级（从高到低）:
    │
    ├─▶ skill 层    - 技能私有知识
    │
    ├─▶ scene 层    - 场景共享知识
    │
    ├─▶ org 层      - 组织公共知识
    │
    └─▶ global 层   - 全局通用知识
```

### 3.5 配置加载

**方法**: `SkillInstallProcessorImpl.buildConfig()`

```java
// 从 skill.yaml spec.knowledge 加载配置
Object knowledge = specMap.get("knowledge");
if (knowledge instanceof Map) {
    config.setKnowledgeConfig((Map<String, Object>) knowledge);
}
```

**配置示例**:

```yaml
spec:
  knowledge:
    enabled: true
    autoCreate: true
    visibility: private
    embeddingModel: text-embedding-3-small
    chunkSize: 500
    chunkOverlap: 50
    layers:
      - skill
      - scene
```

---

## 四、初始化能力绑定

### 4.1 CapabilityBinding 实体

**包路径**: `net.ooder.scene.capability.CapabilityBinding`

```java
public class CapabilityBinding {
    // 提供者类型
    public enum ProviderType {
        AGENT,          // 代理提供
        PLATFORM,       // 平台提供
        EXTERNAL,       // 外部提供
        HYBRID          // 混合提供
    }
    
    // 绑定状态
    public enum Status {
        ACTIVE,         // 激活状态
        INACTIVE,       // 非激活状态
        ERROR,          // 错误状态
        PENDING,        // 待处理状态
        REMOVED         // 已移除
    }
    
    private final String bindingId;      // 绑定ID
    private final String sceneGroupId;   // 场景组ID
    private final String capId;          // 能力ID
    private ProviderType providerType;   // 提供者类型
    private volatile Status status;      // 当前状态
    private int priority;                // 优先级
    private long bindTime;               // 绑定时间
}
```

### 4.2 能力解析流程

**方法**: `SceneGroupInitializer.parseCapabilities()`

```
parseCapabilities(context)
    │
    ├─▶ 获取 requiredCapabilities
    │       │
    │       └─▶ 遍历查找 Capability
    │               │
    │               ├─▶ 找到 → context.addRequiredCapability(cap)
    │               └─▶ 未找到 → 抛出 InitException
    │
    └─▶ 获取 optionalCapabilities
            │
            └─▶ 遍历查找 Capability
                    │
                    ├─▶ 找到 → context.addOptionalCapability(cap)
                    └─▶ 未找到 → 跳过（可选能力不报错）
```

### 4.3 技能挂载流程

**方法**: `SceneGroupInitializer.mountSkills()`

```
mountSkills(context)
    │
    └─▶ 遍历 context.getAgents()
            │
            └─▶ 遍历 context.getSkillMatches()
                    │
                    ├─▶ 选择最佳匹配 (matches.get(0))
                    │
                    ├─▶ 创建 SceneConfig
                    │       skillId, capId, connectorType
                    │       endpoint, priority
                    │
                    ├─▶ agent.mountSkill(skillId, config)
                    │
                    └─▶ 创建 SkillBinding
                            skillId, capId, priority
```

---

## 五、初始化菜单

### 5.1 MenuGenerationEngine 接口

**包路径**: `net.ooder.scene.core.menu.MenuGenerationEngine`

```java
public interface MenuGenerationEngine {
    // 生成场景菜单
    List<MenuItem> generateSceneMenu(String sceneId, String userId, String role);
    
    // 生成用户菜单
    UserMenu generateUserMenu(String userId);
    
    // 刷新/清除菜单缓存
    void refreshMenuCache(String sceneId);
    void clearMenuCache(String sceneId);
    
    // 注册/注销菜单提供者
    void registerMenuProvider(MenuProvider provider);
    void unregisterMenuProvider(String providerId);
    
    // 获取/更新菜单配置
    MenuConfig getMenuConfig(String sceneId);
    void updateMenuConfig(String sceneId, MenuConfig config);
}
```

### 5.2 菜单注册流程

**方法**: `SceneActivationServiceImpl.registerMenus()`

```
registerMenus(template, request)
    │
    ├─▶ 检查 MenuGenerationEngine 可用性
    │
    ├─▶ 获取模板菜单配置
    │       template.getMenus()
    │
    ├─▶ 确定用户角色
    │       request.getRole() 或 determineUserRole()
    │
    ├─▶ 获取角色菜单
    │       menus.get(roleId)
    │
    └─▶ 注册菜单配置
            convertToEngineMenuConfig()
            menuGenerationEngine.updateMenuConfig()
```

### 5.3 菜单配置加载

**方法**: `SceneConfigLoader.loadMenusConfig()`

```yaml
# skill.yaml 配置示例
spec:
  menus:
    MANAGER:
      - id: dashboard
        name: 管理概览
        icon: ri-dashboard-line
        url: /console/pages/dashboard.html
        order: 1
      - id: settings
        name: 系统设置
        icon: ri-settings-line
        url: /console/pages/settings.html
        order: 2
    EMPLOYEE:
      - id: my-tasks
        name: 我的任务
        icon: ri-task-line
        url: /console/pages/tasks.html
        order: 1
```

---

## 六、初始化配置

### 6.1 SceneConfig 实体

**包路径**: `net.ooder.scene.core.SceneConfig`

```java
public class SceneConfig {
    private String configId;
    private Map<String, Object> properties;
    
    public void setProperty(String key, Object value);
    public Object getProperty(String key);
    public void removeProperty(String key);
    public Map<String, Object> getProperties();
    public void merge(SceneConfig other);
}
```

### 6.2 SceneTemplate 模板

**包路径**: `net.ooder.scene.core.template.SceneTemplate`

```java
public class SceneTemplate {
    // 基本信息
    private String templateId;
    private String templateName;
    private String description;
    private String version;
    
    // 场景配置
    private SceneType sceneType;         // AUTO/TRIGGER/HYBRID
    private String visibility;           // public/internal
    
    // 能力配置
    private String category;
    private String capabilityCode;
    
    // 角色配置
    private List<RoleConfig> roles;
    
    // 激活步骤（按角色区分）
    private Map<String, List<ActivationStepConfig>> activationSteps;
    
    // 菜单配置（按角色区分）
    private Map<String, List<MenuConfig>> menus;
    
    // 依赖配置
    private DependenciesConfig dependencies;
    
    // 私有能力
    private List<PrivateCapabilityConfig> privateCapabilities;
}
```

### 6.3 配置加载流程

**方法**: `SceneConfigLoader.loadSceneConfig()`

```
loadSceneConfig(skillId, skillPackage)
    │
    ├─▶ 获取 metadata
    │
    ├─▶ 创建 SceneTemplate
    │       templateId, templateName, description, version
    │
    ├─▶ loadCapabilityConfig()
    │       spec.capability → category, capabilityCode
    │
    ├─▶ loadSceneConfig()
    │       spec.scene → sceneType, visibility
    │
    ├─▶ loadRolesConfig()
    │       spec.roles → List<RoleConfig>
    │
    ├─▶ loadActivationStepsConfig()
    │       spec.activationSteps → Map<roleId, List<Step>>
    │
    ├─▶ loadMenusConfig()
    │       spec.menus → Map<roleId, List<Menu>>
    │
    └─▶ loadPrivateCapabilitiesConfig()
            spec.privateCapabilities → List<PrivateCap>
```

---

## 七、初始化场景组

### 7.1 SceneGroup 实体

**包路径**: `net.ooder.scene.group.SceneGroup`

```java
public class SceneGroup {
    // 场景组状态
    public enum Status {
        CREATING,       // 创建中
        ACTIVE,         // 激活状态
        SUSPENDED,      // 暂停状态
        ARCHIVED,       // 已归档
        DESTROYING,     // 销毁中
        DESTROYED       // 已销毁
    }
    
    // 创建者类型
    public enum CreatorType {
        USER,           // 用户创建
        AGENT,          // 代理创建
        SYSTEM          // 系统创建
    }
    
    // 基础信息
    private final String sceneGroupId;
    private final String templateId;
    private String name;
    private String description;
    private final AtomicReference<Status> status;
    
    // 关联数据
    private final List<Participant> participants;
    private final List<CapabilityBinding> capabilityBindings;
    private final List<KnowledgeBinding> knowledgeBindings;
    private final List<SceneSnapshot> snapshots;
    private final Map<String, Object> config;
}
```

### 7.2 场景组创建流程

**方法**: `SceneGroupInitializer.syncCreateSeSceneGroup()`

```
syncCreateSeSceneGroup(context, sdkGroup)
    │
    ├─▶ 创建 SE SceneGroup
    │       seSceneGroupManager.createSceneGroup(
    │           sceneGroupId, templateId, creatorId, CreatorType.USER
    │       )
    │
    ├─▶ 设置名称
    │       seGroup.setName(request.getSceneName())
    │
    ├─▶ 同步参与者
    │       syncParticipants(context, seGroup)
    │
    ├─▶ 应用场景类型行为
    │       sceneTypeHandler.applyBehavior(seGroup, sceneType)
    │
    └─▶ 持久化
            sceneGroupPersistence.save(seGroup)
```

### 7.3 场景类型行为

**类**: `SceneTypeHandler`

| 场景类型 | 行为 | 说明 |
|----------|------|------|
| AUTO | `applyAutoBehavior()` | 立即激活，启动自驱动逻辑 |
| TRIGGER | `applyTriggerBehavior()` | 保持 CREATED 状态，等待外部触发 |
| HYBRID | `applyHybridBehavior()` | 根据配置决定初始行为 |

---

## 八、激活步骤执行

### 8.1 ActivationStepExecutor SPI

**包路径**: `net.ooder.scene.core.spi.ActivationStepExecutor`

```java
public interface ActivationStepExecutor {
    // 获取步骤类型
    String getStepType();
    
    // 检查是否可以执行
    boolean canExecute(ActivationStepConfig stepConfig);
    
    // 执行步骤
    StepResult execute(ActivationStepConfig stepConfig, 
                       ActivationProcess process, 
                       Map<String, Object> context);
}

// 执行结果
class StepResult {
    private boolean success;
    private String message;
    private Map<String, Object> data;
    
    public static StepResult success(String message);
    public static StepResult failure(String message);
}
```

### 8.2 激活步骤配置

**包路径**: `net.ooder.scene.core.template.ActivationStepConfig`

```java
public class ActivationStepConfig {
    private String stepId;           // 步骤ID
    private String stepName;         // 步骤名称
    private String stepType;         // 步骤类型
    private int order;               // 执行顺序
    private boolean skippable;       // 是否可跳过
    private boolean required;        // 是否必需
    private boolean autoExecute;     // 是否自动执行
    private String executorType;     // 执行器类型
    private List<String> privateCapabilities;  // 私有能力
    private Map<String, Object> config;  // 配置参数
}
```

### 8.3 激活步骤执行流程

**方法**: `SceneActivationServiceImpl.executeActivationSteps()`

```
executeActivationSteps(process, template, request)
    │
    ├─▶ 确定用户角色
    │       roleId = request.getRole() 或 determineUserRole()
    │
    ├─▶ 获取角色激活步骤
    │       steps = template.getActivationStepsForRole(roleId)
    │
    ├─▶ 构建执行上下文
    │       context = {sceneId, userId, roleId}
    │
    └─▶ 遍历执行步骤
            │
            ├─▶ autoExecute=true
            │       └─▶ executeStep() → complete()
            │
            ├─▶ skippable=true
            │       └─▶ skip()
            │
            └─▶ required=true
                    │
                    ├─▶ executeStep()
                    │       │
                    │       ├─▶ findExecutor(stepType)
                    │       │
                    │       └─▶ executor.execute()
                    │
                    └─▶ 成功 → complete()
                        失败 → fail() + 抛出异常
```

### 8.4 步骤类型示例

| 步骤类型 | 说明 | 执行器 |
|----------|------|--------|
| CONFIRM_JOIN | 确认加入 | ConfirmJoinExecutor |
| CONFIG_CAPABILITY | 配置能力 | ConfigCapabilityExecutor |
| CONFIRM_CONDITION | 确认条件 | ConfirmConditionExecutor |
| INVITE_PARTICIPANTS | 邀请参与者 | InviteParticipantsExecutor |
| SELECT_TEMPLATE | 选择模板 | SelectTemplateExecutor |

---

## 九、扩展点注册中心

### 9.1 ExtensionPointRegistry

**包路径**: `net.ooder.scene.core.spi.ExtensionPointRegistry`

```java
public class ExtensionPointRegistry {
    // 注册扩展实现
    public <T> void register(Class<T> extensionPoint, T implementation);
    public <T> void register(Class<T> extensionPoint, T implementation, int priority);
    public <T> void register(Class<T> extensionPoint, T implementation, String name);
    
    // 获取扩展实现
    public <T> List<T> getExtensions(Class<T> extensionPoint);
    public <T> T getFirstExtension(Class<T> extensionPoint);
    public <T> T getExtensionByName(Class<T> extensionPoint, String name);
    
    // 注销扩展实现
    public <T> boolean unregister(Class<T> extensionPoint, T implementation);
    public <T> boolean unregisterByName(Class<T> extensionPoint, String name);
}
```

### 9.2 注册自定义执行器

```java
@Component
public class CustomActivationStepExecutor implements ActivationStepExecutor {
    
    @Override
    public String getStepType() {
        return "CUSTOM_TYPE";
    }
    
    @Override
    public boolean canExecute(ActivationStepConfig stepConfig) {
        return "CUSTOM_TYPE".equals(stepConfig.getStepType());
    }
    
    @Override
    public StepResult execute(ActivationStepConfig stepConfig,
                              ActivationProcess process,
                              Map<String, Object> context) {
        // 自定义执行逻辑
        return StepResult.success("执行成功");
    }
}
```

---

## 十、完整流程图

```
场景激活请求
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│              SceneGroupInitializer.initialize()          │
├─────────────────────────────────────────────────────────┤
│  1. loadScene()          → 加载场景定义                   │
│  2. initializeAgents()   → 创建 Agent，分配角色           │
│  3. parseCapabilities()  → 解析必需/可选能力              │
│  4. discoverSkills()     → 发现匹配的 Skill               │
│  5. mountSkills()        → 挂载 Skill 到 Agent            │
│  6. activate()           → 激活场景组                     │
└─────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│              SceneActivationServiceImpl                  │
├─────────────────────────────────────────────────────────┤
│  • 加载 SceneTemplate                                    │
│  • 执行激活步骤 (按角色)                                  │
│  • 注册菜单                                              │
│  • 应用角色配置                                          │
└─────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│                    初始化结果                            │
├─────────────────────────────────────────────────────────┤
│  • SceneGroup (ACTIVE)                                   │
│  • Participants (已加入)                                 │
│  • CapabilityBindings (ACTIVE)                           │
│  • KnowledgeBindings (已绑定)                            │
│  • Menus (已注册)                                        │
│  • Config (已加载)                                       │
└─────────────────────────────────────────────────────────┘
```

---

## 十一、相关文档

- [场景配置加载](01-scene-config-loader.md)
- [场景验证](02-scene-validation.md)
- [场景激活服务](03-scene-activation.md)
- [SPI服务暴露](../09-spi/01-service-provider.md)
- [能力与场景模型对比](../13-reference/CAPABILITY_SCENE_MODEL_COMPARISON.md)

---

**创建时间**: 2026-03-22  
**版本**: v2.3.1
