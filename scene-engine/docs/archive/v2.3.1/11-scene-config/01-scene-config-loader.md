# 场景配置加载

## 概述

`SceneConfigLoader` 负责从技能包的 `skill.yaml` 文件中加载场景配置，包括角色定义、激活步骤、菜单配置等。

**包路径**: `net.ooder.scene.skill.install.SceneConfigLoader`

---

## 核心方法

### loadSceneConfig

从技能包加载场景配置。

```java
public SceneTemplate loadSceneConfig(String skillId, SkillPackage skillPackage)
```

**参数**:
- `skillId` - 技能ID
- `skillPackage` - 技能包对象

**返回**:
- `SceneTemplate` - 场景模板配置，如果不存在则返回 `null`

**示例**:

```java
SceneConfigLoader loader = new SceneConfigLoader();
SkillPackage skillPackage = skillPackageManager.getSkill(skillId).get();
SceneTemplate template = loader.loadSceneConfig(skillId, skillPackage);

if (template != null) {
    System.out.println("场景类型: " + template.getSceneType());
    System.out.println("角色数量: " + template.getRoles().size());
}
```

---

## 支持的配置字段

### skill.yaml 结构

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: skill-xxx
  name: 技能名称
  version: "2.3.1"
  category: biz
  description: 技能描述

spec:
  # 技能形式
  skillForm: SCENE
  
  # 能力配置
  capability:
    address: 0x08
    category: HR
    code: HR_RECRUITMENT
    operations: [recruit, interview, offer]
  
  # 场景配置
  scene:
    type: INTERACTIVE      # AUTO / TRIGGER / HYBRID
    visibility: public     # public / internal
    participantMode: multi-user
  
  # 角色定义
  roles:
    - name: MANAGER
      description: 管理员
      required: true
      minCount: 1
      maxCount: 1
      permissions: [manage, approve]
    - name: EMPLOYEE
      description: 员工
      required: true
      minCount: 1
      maxCount: 100
      permissions: [view, submit]
  
  # 激活步骤（按角色区分）
  activationSteps:
    MANAGER:
      - stepId: confirm-config
        name: 确认配置
        description: 确认场景配置
        required: true
        autoExecute: false
    EMPLOYEE:
      - stepId: confirm-join
        name: 确认加入
        description: 确认加入场景
        required: true
        autoExecute: false
  
  # 菜单配置（按角色区分）
  menus:
    MANAGER:
      - id: dashboard
        name: 管理概览
        icon: ri-dashboard-line
        url: /console/pages/dashboard.html
        order: 1
    EMPLOYEE:
      - id: my-tasks
        name: 我的任务
        icon: ri-task-line
        url: /console/pages/tasks.html
        order: 1
  
  # 私有能力
  privateCapabilities:
    - capId: internal-report
      name: 内部报表
      description: 仅内部使用的报表功能
```

---

## 加载的配置项

| 配置路径 | SceneTemplate 字段 | 说明 |
|----------|-------------------|------|
| `spec.capability` | `category`, `capabilityCode` | 能力分类配置 |
| `spec.scene` | `sceneType`, `visibility` | 场景类型配置 |
| `spec.roles` | `roles` | 角色定义列表 |
| `spec.activationSteps` | `activationSteps` | 激活步骤（按角色区分） |
| `spec.menus` | `menus` | 菜单配置（按角色区分） |
| `spec.privateCapabilities` | `privateCapabilities` | 私有能力列表 |

---

## SceneTemplate 结构

```java
public class SceneTemplate {
    // 基本信息
    private String templateId;
    private String templateName;
    private String description;
    private String version;
    
    // 场景配置
    private SceneType sceneType;      // AUTO / TRIGGER / HYBRID
    private String visibility;        // public / internal
    
    // 能力配置
    private String category;
    private String capabilityCode;
    
    // 角色配置
    private List<RoleConfig> roles;
    
    // 激活步骤（按角色ID索引）
    private Map<String, List<ActivationStepConfig>> activationSteps;
    
    // 菜单配置（按角色ID索引）
    private Map<String, List<MenuConfig>> menus;
    
    // 私有能力
    private List<PrivateCapabilityConfig> privateCapabilities;
    
    // 便捷方法
    public List<ActivationStepConfig> getActivationStepsForRole(String roleId);
    public List<MenuConfig> getMenusForRole(String roleId);
    public List<RoleConfig> getRequiredRoles();
}
```

---

## RoleConfig 结构

```java
public class RoleConfig {
    private String roleId;           // 角色ID
    private String roleName;         // 角色名称
    private String name;             // 兼容字段
    private String description;      // 角色描述
    private int priority;            // 优先级
    private boolean required;        // 是否必需
    private int minCount;            // 最小人数
    private int maxCount;            // 最大人数
    private List<String> permissions; // 权限列表
}
```

---

## ActivationStepConfig 结构

```java
public class ActivationStepConfig {
    private String stepId;           // 步骤ID
    private String stepName;         // 步骤名称
    private String name;             // 兼容字段
    private String description;      // 步骤描述
    private String stepType;         // 步骤类型
    private int order;               // 执行顺序
    private boolean skippable;       // 是否可跳过
    private boolean required;        // 是否必需
    private boolean autoExecute;     // 是否自动执行
    private String executorType;     // 执行器类型
    private List<String> privateCapabilities; // 私有能力
}
```

---

## 使用场景

### 1. 安装时加载配置

```java
public InstallResult install(InstallRequest request) {
    String skillId = request.getSkillId();
    SkillPackage skillPackage = getSkillPackage(skillId);
    
    // 加载场景配置
    SceneConfigLoader loader = new SceneConfigLoader();
    SceneTemplate template = loader.loadSceneConfig(skillId, skillPackage);
    
    if (template != null) {
        // 验证配置
        loader.validateSceneConfig(skillId, template);
        
        // 保存模板供后续使用
        sceneTemplateRegistry.register(skillId, template);
    }
    
    // 继续安装流程...
}
```

### 2. 激活时获取配置

```java
public void activateScene(String skillId, String userId, String roleId) {
    // 获取已加载的模板
    SceneTemplate template = sceneTemplateRegistry.get(skillId);
    
    // 获取角色的激活步骤
    List<ActivationStepConfig> steps = template.getActivationStepsForRole(roleId);
    
    // 执行激活步骤...
}
```

---

## 注意事项

1. **配置缺失处理**: 如果 `skill.yaml` 中没有场景配置，`loadSceneConfig` 返回 `null`
2. **字段兼容**: 同时支持 `name` 和 `roleName` 等兼容字段
3. **类型转换**: `sceneType` 字符串会自动转换为 `SceneType` 枚举
4. **空值安全**: 列表字段返回空列表而非 `null`

---

## 相关文档

- [场景验证](02-scene-validation.md)
- [场景激活服务](03-scene-activation.md)
