# 场景配置验证

## 概述

`SceneConfigLoader.validateSceneConfig()` 方法用于验证场景配置的完整性，确保安装时配置符合要求。

**包路径**: `net.ooder.scene.skill.install.SceneConfigLoader`

---

## 验证方法

### validateSceneConfig

验证场景配置完整性。

```java
public void validateSceneConfig(String skillId, SceneTemplate template)
    throws SceneValidationException
```

**参数**:
- `skillId` - 技能ID
- `template` - 场景模板配置

**异常**:
- `SceneValidationException` - 验证失败时抛出

**示例**:

```java
SceneConfigLoader loader = new SceneConfigLoader();
SceneTemplate template = loader.loadSceneConfig(skillId, skillPackage);

try {
    loader.validateSceneConfig(skillId, template);
    System.out.println("验证通过");
} catch (SceneValidationException e) {
    System.err.println("验证失败: " + e.getMessage());
    System.err.println("验证类型: " + e.getValidationType());
}
```

---

## 验证规则

### 验证项列表

| 验证项 | 验证类型 | 触发条件 | 错误信息 |
|--------|----------|----------|----------|
| 场景配置存在 | `SCENE_CONFIG_MISSING` | template 为 null | 技能包中未定义场景配置 |
| 角色定义存在 | `ROLES_MISSING` | roles 为空 | 场景缺少角色定义 |
| 必需角色存在 | `REQUIRED_ROLE_MISSING` | 无必需角色 | 场景缺少必需角色 |
| 激活步骤存在 | `ACTIVATION_STEPS_MISSING` | activationSteps 为空 | 场景缺少激活步骤 |
| 角色激活步骤 | `ROLE_ACTIVATION_STEPS_MISSING` | 必需角色无激活步骤 | 必需角色缺少激活步骤 |
| 菜单配置存在 | `MENUS_MISSING` | menus 为空 | 场景缺少菜单配置 |
| 角色菜单配置 | `ROLE_MENUS_MISSING` | 必需角色无菜单 | 必需角色缺少菜单 |

---

## SceneValidationException

**包路径**: `net.ooder.scene.skill.exception.SceneValidationException`

### 构造方法

```java
// 基本构造
public SceneValidationException(String message)

// 带技能ID
public SceneValidationException(String skillId, String message)

// 带验证类型
public SceneValidationException(String skillId, String validationType, String message)

// 带原因异常
public SceneValidationException(String skillId, String message, Throwable cause)
```

### 验证类型常量

```java
public static final String SCENE_CONFIG_MISSING = "SCENE_CONFIG_MISSING";
public static final String ROLES_MISSING = "ROLES_MISSING";
public static final String REQUIRED_ROLE_MISSING = "REQUIRED_ROLE_MISSING";
public static final String ACTIVATION_STEPS_MISSING = "ACTIVATION_STEPS_MISSING";
public static final String ROLE_ACTIVATION_STEPS_MISSING = "ROLE_ACTIVATION_STEPS_MISSING";
public static final String MENUS_MISSING = "MENUS_MISSING";
public static final String ROLE_MENUS_MISSING = "ROLE_MENUS_MISSING";
```

### 方法

| 方法 | 返回类型 | 说明 |
|------|----------|------|
| `getValidationType()` | `String` | 获取验证类型 |
| `getSkillId()` | `String` | 获取技能ID（继承自父类） |
| `getMessage()` | `String` | 获取格式化的错误消息 |

---

## 使用示例

### 1. 安装时验证

```java
public InstallResult install(InstallRequest request) {
    String skillId = request.getSkillId();
    
    try {
        SkillPackage skillPackage = getSkillPackage(skillId);
        if (skillPackage == null) {
            return InstallResult.failure(skillId, "Skill not found");
        }
        
        // 加载场景配置
        SceneConfigLoader loader = new SceneConfigLoader();
        SceneTemplate template = loader.loadSceneConfig(skillId, skillPackage);
        
        // 验证配置（仅对场景类型技能）
        if ("SCENE".equals(determineSkillForm(skillPackage))) {
            if (template == null) {
                throw new SceneValidationException(skillId, "SCENE_CONFIG_MISSING",
                    "场景配置缺失: 技能包中未定义场景配置。" +
                    "请在 skill.yaml 中添加 spec.roles、spec.activationSteps、spec.menus 配置。");
            }
            loader.validateSceneConfig(skillId, template);
        }
        
        // 继续安装流程...
        
    } catch (SceneValidationException e) {
        log.error("Scene validation failed for skill: {}", skillId, e);
        return InstallResult.failure(skillId, "场景配置验证失败: " + e.getMessage());
    }
}
```

### 2. 自定义验证消息

```java
try {
    loader.validateSceneConfig(skillId, template);
} catch (SceneValidationException e) {
    // 根据验证类型提供不同的解决方案
    switch (e.getValidationType()) {
        case "ROLES_MISSING":
            log.error("请添加角色定义: spec.roles");
            break;
        case "ACTIVATION_STEPS_MISSING":
            log.error("请添加激活步骤: spec.activationSteps");
            break;
        case "MENUS_MISSING":
            log.error("请添加菜单配置: spec.menus");
            break;
        default:
            log.error("验证失败: {}", e.getMessage());
    }
}
```

---

## 错误消息格式

验证异常的消息格式如下：

```
[验证类型] 详细错误信息
```

示例：

```
[ROLES_MISSING] 场景缺少角色定义: 请在 skill.yaml 的 spec.roles 中定义场景角色
```

---

## 完整配置示例

以下是一个通过所有验证的完整配置示例：

```yaml
spec:
  skillForm: SCENE
  
  scene:
    type: HYBRID
    visibility: public
  
  roles:
    - name: MANAGER
      description: 管理员
      required: true
      minCount: 1
      maxCount: 1
    - name: EMPLOYEE
      description: 员工
      required: true
      minCount: 1
      maxCount: 100
  
  activationSteps:
    MANAGER:
      - stepId: confirm-config
        name: 确认配置
        required: true
    EMPLOYEE:
      - stepId: confirm-join
        name: 确认加入
        required: true
  
  menus:
    MANAGER:
      - id: dashboard
        name: 管理概览
        url: /console/pages/dashboard.html
        order: 1
    EMPLOYEE:
      - id: my-tasks
        name: 我的任务
        url: /console/pages/tasks.html
        order: 1
```

---

## 验证流程图

```
开始验证
    │
    ▼
template == null? ──是──▶ SCENE_CONFIG_MISSING
    │
    否
    ▼
roles 为空? ──是──▶ ROLES_MISSING
    │
    否
    ▼
无必需角色? ──是──▶ REQUIRED_ROLE_MISSING
    │
    否
    ▼
activationSteps 为空? ──是──▶ ACTIVATION_STEPS_MISSING
    │
    否
    ▼
必需角色无激活步骤? ──是──▶ ROLE_ACTIVATION_STEPS_MISSING
    │
    否
    ▼
menus 为空? ──是──▶ MENUS_MISSING
    │
    否
    ▼
必需角色无菜单? ──是──▶ ROLE_MENUS_MISSING
    │
    否
    ▼
验证通过 ✓
```

---

## 相关文档

- [场景配置加载](01-scene-config-loader.md)
- [场景激活服务](03-scene-activation.md)
