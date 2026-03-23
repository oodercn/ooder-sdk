# 协作任务：MVP 适配 SE SDK 2.3.1

## 一、任务概述

**提交人**: SE SDK 团队  
**提交时间**: 2026-03-22  
**优先级**: 高  
**涉及团队**: MVP 团队  
**SE SDK 版本**: 2.3.1  
**依赖位置**: `D:\maven\.m2\repository\net\ooder\scene-engine\2.3.1\`

---

## 二、SE SDK 2.3.1 新增功能

### 2.1 场景配置相关

| 类名 | 包路径 | 功能 |
|------|--------|------|
| `SceneConfigLoader` | `net.ooder.scene.skill.install` | 从 skill.yaml 加载场景配置 |
| `SceneValidationException` | `net.ooder.scene.skill.exception` | 场景验证异常 |
| `SceneActivationServiceImpl` | `net.ooder.scene.core.activation` | 场景激活服务实现 |

### 2.2 配置模型扩展

| 类名 | 新增字段 |
|------|----------|
| `SceneTemplate` | `sceneType` (SceneType枚举), `visibility`, `category`, `capabilityCode` |
| `RoleConfig` | `name`, `permissions` |
| `ActivationStepConfig` | `name`, `autoExecute`, `privateCapabilities` |

### 2.3 审计服务适配

| 类名 | 包路径 | 功能 |
|------|--------|------|
| `AuditServiceAdapter` | `net.ooder.scene.core.security` | 将 audit.AuditService 适配为 security.AuditService |

### 2.4 SceneType 枚举统一

SE SDK 已统一使用 `net.ooder.scene.skill.model.SceneType`：
- `AUTO` - 自主场景
- `TRIGGER` - 触发场景
- `HYBRID` - 混合场景

---

## 三、MVP 当前状态分析

### 3.1 依赖配置

```xml
<!-- pom.xml -->
<ooder.sdk.version>2.3.1</ooder.sdk.version>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>${ooder.sdk.version}</version>
</dependency>
```

### 3.2 MVP 自有实现（可保留）

| 组件 | MVP 实现位置 | SE SDK 状态 | 建议 |
|------|-------------|-------------|------|
| 场景激活 | `SceneSkillLifecycleServiceImpl` | 已实现 | 可选迁移 |
| 审计服务 | `AuditService.java` | 有适配器 | 可选集成 |
| SceneType | 两个定义 | 已统一 | 需适配 |

---

## 四、MVP 适配任务清单

### 4.1 高优先级 (P0) - 必须完成

#### 4.1.1 版本升级

```bash
# 确保 Maven 本地仓库已更新
mvn clean install -DskipTests -q
# 位置：D:\maven\.m2\repository\net\ooder\scene-engine\2.3.1\
```

#### 4.1.2 安装服务集成 SceneConfigLoader

**文件**: `src/main/java/net/ooder/mvp/skill/scene/capability/install/InstallServiceImpl.java`

**需要添加的代码**:

```java
import net.ooder.scene.skill.install.SceneConfigLoader;
import net.ooder.scene.skill.exception.SceneValidationException;
import net.ooder.scene.core.template.SceneTemplate;

// 在 executeInstall 方法中添加验证
private void validateSceneConfig(String skillId, SkillPackage skillPackage) {
    SceneConfigLoader loader = new SceneConfigLoader();
    SceneTemplate template = loader.loadSceneConfig(skillId, skillPackage);
    
    if (template != null) {
        loader.validateSceneConfig(skillId, template);
    }
}
```

#### 4.1.3 SceneType 枚举适配

**问题**: MVP 有两个 SceneType 定义
- `net.ooder.mvp.skill.scene.capability.model.SceneType` - 场景运行类型
- `net.ooder.mvp.skill.scene.dto.scene.SceneType` - 场景分类

**解决方案**: 使用 SDK 的枚举或在 MVP 中添加映射

```java
// 建议在 MVP 中添加转换方法
public static net.ooder.scene.skill.model.SceneType toSdkSceneType(MvpSceneType type) {
    switch (type) {
        case AUTO: return net.ooder.scene.skill.model.SceneType.AUTO;
        case TRIGGER: return net.ooder.scene.skill.model.SceneType.TRIGGER;
        case HYBRID: return net.ooder.scene.skill.model.SceneType.HYBRID;
        default: return net.ooder.scene.skill.model.SceneType.HYBRID;
    }
}
```

---

### 4.2 中优先级 (P1) - 建议完成

#### 4.2.1 集成 SceneActivationService（可选）

**文件**: `src/main/java/net/ooder/mvp/skill/scene/capability/service/impl/SceneSkillLifecycleServiceImpl.java`

**说明**: MVP 已自己实现场景激活逻辑，可以：
- 保持现状，继续使用 MVP 实现
- 或迁移到 SE SDK 的 `SceneActivationServiceImpl`

#### 4.2.2 集成 AuditService 适配器（可选）

**文件**: `src/main/java/net/ooder/mvp/skill/scene/audit/AuditAspect.java`

**说明**: MVP 有自己的审计实现，可以：
- 保持现状，使用 MVP 的 `AuditService`
- 或集成 SE SDK 的 `AuditServiceAdapter`

---

## 五、具体代码修改示例

### 5.1 SeCapabilityServiceImpl.java 修改

**文件**: `src/main/java/net/ooder/mvp/skill/scene/capability/service/impl/SeCapabilityServiceImpl.java`

```java
// 添加导入
import net.ooder.scene.skill.install.SceneConfigLoader;
import net.ooder.scene.skill.exception.SceneValidationException;
import net.ooder.scene.core.template.SceneTemplate;

// 在类中添加 SceneConfigLoader 字段
private final SceneConfigLoader sceneConfigLoader;

// 修改构造函数
public SeCapabilityServiceImpl(SkillRegistry skillRegistry, 
                                SkillDiscoverer skillDiscoverer,
                                SkillPackageManager packageManager,
                                CapabilityStateService stateService) {
    this.skillRegistry = skillRegistry;
    this.skillDiscoverer = skillDiscoverer;
    this.packageManager = packageManager;
    this.stateService = stateService;
    this.localRegistry = new CapabilityRegistry();
    this.sceneConfigLoader = new SceneConfigLoader();  // 新增
    log.info("[SeCapabilityServiceImpl] Created with SE SDK components");
}

// 修改 convertSkillPackageToCapability 方法，添加场景配置加载
private Capability convertSkillPackageToCapability(SkillPackage skill) {
    if (skill == null || skill.getSkillId() == null) {
        return null;
    }
    
    Capability cap = new Capability();
    cap.setCapabilityId(skill.getSkillId());
    cap.setName(skill.getName());
    cap.setDescription(skill.getDescription());
    cap.setVersion(skill.getVersion());
    cap.setSkillId(skill.getSkillId());
    
    // 新增：加载场景配置
    try {
        SceneTemplate template = sceneConfigLoader.loadSceneConfig(skill.getSkillId(), skill);
        if (template != null) {
            // 从场景模板填充 Capability 字段
            if (template.getSceneType() != null) {
                cap.setSceneType(template.getSceneType().name());
            }
            if (template.getVisibility() != null) {
                cap.setVisibility(Visibility.valueOf(template.getVisibility().toUpperCase()));
            }
            if (template.getCategory() != null) {
                cap.setCapabilityCategory(CapabilityCategory.fromSeSdkCategory(template.getCategory()));
            }
            if (template.getRoles() != null && !template.getRoles().isEmpty()) {
                cap.setParticipants(convertRolesToParticipants(template.getRoles()));
            }
            log.info("[convertSkillPackageToCapability] Loaded scene config for: {}", skill.getSkillId());
        }
    } catch (SceneValidationException e) {
        log.warn("[convertSkillPackageToCapability] Scene config validation failed for {}: {}", 
            skill.getSkillId(), e.getMessage());
    } catch (Exception e) {
        log.debug("[convertSkillPackageToCapability] No scene config for {}: {}", 
            skill.getSkillId(), e.getMessage());
    }
    
    // 原有逻辑保持不变
    String category = skill.getCategory();
    if (category != null) {
        cap.setCapabilityCategory(CapabilityCategory.fromSeSdkCategory(category));
    }
    
    List<String> tags = skill.getTags();
    if (tags != null) {
        cap.setTags(tags);
    }
    
    List<String> dependencies = skill.getDependencies();
    if (dependencies != null) {
        cap.setDependencies(dependencies);
    }
    
    cap.setCreateTime(System.currentTimeMillis());
    cap.setUpdateTime(System.currentTimeMillis());
    cap.setStatus(CapabilityStatus.REGISTERED);
    cap.setInstalled(false);
    
    return cap;
}

// 新增：角色转换方法
private List<Participant> convertRolesToParticipants(List<net.ooder.scene.core.template.RoleConfig> roles) {
    List<Participant> participants = new ArrayList<>();
    for (net.ooder.scene.core.template.RoleConfig role : roles) {
        Participant participant = new Participant();
        participant.setRoleId(role.getRoleId());
        participant.setRoleName(role.getName());
        participant.setRequired(role.isRequired());
        participant.setPermissions(role.getPermissions());
        participants.add(participant);
    }
    return participants;
}
```

### 5.2 InstallServiceImpl.java 修改

**文件**: `src/main/java/net/ooder/mvp/skill/scene/capability/install/InstallServiceImpl.java`

```java
// 添加导入
import net.ooder.scene.skill.install.SceneConfigLoader;
import net.ooder.scene.skill.exception.SceneValidationException;
import net.ooder.scene.core.template.SceneTemplate;

// 在类中添加字段
@Autowired
private SkillPackageManager skillPackageManager;

private final SceneConfigLoader sceneConfigLoader = new SceneConfigLoader();

// 在 executeInstall 方法中，技能验证后添加
@Override
@Auditable(eventType = AuditEventType.CAPABILITY_INVOKE, action = "执行能力安装", resourceType = "install")
public CompletableFuture<InstallConfig> executeInstall(String installId) {
    log.info("[executeInstall] Executing install: {}", installId);
    
    return CompletableFuture.supplyAsync(() -> {
        InstallConfig config = installs.get(installId);
        // ... 原有代码 ...
        
        try {
            // 新增：验证场景配置
            if ("SCENE".equals(config.getSkillForm())) {
                validateSceneConfig(config.getCapabilityId());
            }
            
            // 原有安装逻辑
            // ...
            
        } catch (SceneValidationException e) {
            log.error("[executeInstall] Scene config validation failed: {}", e.getMessage());
            config.setStatus(InstallConfig.InstallStatus.FAILED);
            if (progress != null) {
                progress.setStatus(InstallConfig.InstallStatus.FAILED);
                progress.setMessage("场景配置验证失败: " + e.getMessage());
            }
            return config;
        } catch (Exception e) {
            // 原有异常处理
        }
        
        return config;
    });
}

// 新增验证方法
private void validateSceneConfig(String capabilityId) {
    if (skillPackageManager == null) {
        log.warn("[validateSceneConfig] SkillPackageManager not available, skipping validation");
        return;
    }
    
    try {
        SkillPackage skillPackage = skillPackageManager.getSkill(capabilityId).get(5, TimeUnit.SECONDS);
        if (skillPackage != null) {
            SceneTemplate template = sceneConfigLoader.loadSceneConfig(capabilityId, skillPackage);
            if (template != null) {
                sceneConfigLoader.validateSceneConfig(capabilityId, template);
                log.info("[validateSceneConfig] Scene config validated for: {}", capabilityId);
            }
        }
    } catch (SceneValidationException e) {
        throw e;
    } catch (Exception e) {
        log.warn("[validateSceneConfig] Could not validate scene config: {}", e.getMessage());
    }
}
```

### 5.2 SceneType 枚举映射

```java
// 在 capability/model/SceneType.java 或工具类中添加
public enum SceneTypeConverter {
    ;

    public static net.ooder.scene.skill.model.SceneType toSdk(MvpSceneType type) {
        if (type == null) {
            return net.ooder.scene.skill.model.SceneType.HYBRID;
        }
        switch (type) {
            case AUTO:
                return net.ooder.scene.skill.model.SceneType.AUTO;
            case TRIGGER:
                return net.ooder.scene.skill.model.SceneType.TRIGGER;
            case HYBRID:
                return net.ooder.scene.skill.model.SceneType.HYBRID;
            default:
                return net.ooder.scene.skill.model.SceneType.HYBRID;
        }
    }

    public static MvpSceneType fromSdk(net.ooder.scene.skill.model.SceneType type) {
        if (type == null) {
            return MvpSceneType.HYBRID;
        }
        switch (type) {
            case AUTO:
                return MvpSceneType.AUTO;
            case TRIGGER:
                return MvpSceneType.TRIGGER;
            case HYBRID:
                return MvpSceneType.HYBRID;
            default:
                return MvpSceneType.HYBRID;
        }
    }
}
```

---

## 六、验证方法

### 6.1 编译验证

```bash
cd E:\github\ooder-skills\mvp
mvn clean compile
```

### 6.2 集成测试

1. 部署 MVP 到测试环境
2. 安装一个场景类型技能（如 skill-recruitment-management）
3. 验证安装流程是否正常
4. 验证场景配置验证是否生效

### 6.3 验证清单

- [ ] scene-engine 2.3.1 依赖已更新
- [ ] InstallServiceImpl 能加载场景配置
- [ ] SceneValidationException 正确抛出
- [ ] SceneType 枚举映射正确
- [ ] 场景技能安装测试通过

---

## 七、时间计划

| 阶段 | 任务 | 预计时间 |
|------|------|----------|
| 阶段1 | 版本升级和编译验证 | 0.5 天 |
| 阶段2 | 集成 SceneConfigLoader | 1 天 |
| 阶段3 | SceneType 枚举适配 | 0.5 天 |
| 阶段4 | 集成测试 | 1 天 |

---

## 八、SE SDK 新增类说明

### 8.1 SceneConfigLoader

位置：`net.ooder.scene.skill.install.SceneConfigLoader`

```java
public class SceneConfigLoader {
    // 从技能包加载场景配置
    public SceneTemplate loadSceneConfig(String skillId, SkillPackage skillPackage)
    
    // 验证场景配置完整性
    public void validateSceneConfig(String skillId, SceneTemplate template)
}
```

### 8.2 SceneValidationException

位置：`net.ooder.scene.skill.exception.SceneValidationException`

```java
public class SceneValidationException extends SkillException {
    private final String validationType;
    
    // 验证类型常量
    public static final String SCENE_CONFIG_MISSING = "SCENE_CONFIG_MISSING";
    public static final String ROLES_MISSING = "ROLES_MISSING";
    public static final String REQUIRED_ROLE_MISSING = "REQUIRED_ROLE_MISSING";
    public static final String ACTIVATION_STEPS_MISSING = "ACTIVATION_STEPS_MISSING";
    public static final String ROLE_ACTIVATION_STEPS_MISSING = "ROLE_ACTIVATION_STEPS_MISSING";
    public static final String MENUS_MISSING = "MENUS_MISSING";
    public static final String ROLE_MENUS_MISSING = "ROLE_MENUS_MISSING";
}
```

### 8.3 SceneActivationServiceImpl

位置：`net.ooder.scene.core.activation.SceneActivationServiceImpl`

```java
public class SceneActivationServiceImpl implements ActivationFlowEngine {
    // 启动激活流程
    public CompletableFuture<ActivationResult> startActivation(ActivationRequest request)
    
    // 注册场景模板
    public void registerSceneTemplate(String sceneId, SkillPackage skillPackage)
    
    // 获取场景模板
    public SceneTemplate getSceneTemplate(String sceneId)
}
```

---

## 九、联系方式

- **SE SDK 团队**: 已完成验证功能开发
- **MVP 团队**: 负责适配集成

---

**创建时间**: 2026-03-22  
**状态**: 待处理  
**文档版本**: 1.0
