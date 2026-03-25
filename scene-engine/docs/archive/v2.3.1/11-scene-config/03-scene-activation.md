# 场景激活服务

## 概述

`SceneActivationServiceImpl` 实现了 `ActivationFlowEngine` 接口，负责场景技能的完整激活流程，包括执行激活步骤、注册菜单、应用角色配置等。

**包路径**: `net.ooder.scene.core.activation.SceneActivationServiceImpl`

**实现接口**: `ActivationFlowEngine`

---

## 核心功能

| 功能 | 方法 | 说明 |
|------|------|------|
| 启动激活流程 | `startActivation()` | 执行完整激活流程 |
| 执行激活步骤 | `executeStep()` | 执行单个激活步骤 |
| 跳过步骤 | `skipStep()` | 跳过可跳过的步骤 |
| 重试步骤 | `retryStep()` | 重试失败的步骤 |
| 获取激活状态 | `getActivationStatus()` | 查询激活进度 |
| 注册场景模板 | `registerSceneTemplate()` | 注册场景模板供激活使用 |

---

## 使用方式

### 1. 创建服务实例

```java
// 通过构造函数创建
SceneConfigLoader configLoader = new SceneConfigLoader();
ExtensionPointRegistry extensionRegistry = getExtensionPointRegistry();
MenuGenerationEngine menuEngine = getMenuGenerationEngine();

SceneActivationServiceImpl activationService = new SceneActivationServiceImpl(
    configLoader,
    extensionRegistry,
    menuEngine
);
```

### 2. 注册场景模板

```java
// 方式1: 直接注册模板
activationService.registerSceneTemplate(sceneId, sceneTemplate);

// 方式2: 从技能包加载并注册
activationService.registerSceneTemplate(sceneId, skillPackage);
```

### 3. 启动激活流程

```java
ActivationRequest request = new ActivationRequest();
request.setSceneId("scene-001");
request.setUserId("user-123");
request.setRole("MANAGER");

CompletableFuture<ActivationResult> future = activationService.startActivation(request);

ActivationResult result = future.get();
if (result.isSuccess()) {
    System.out.println("激活成功: " + result.getActivationId());
} else {
    System.err.println("激活失败: " + result.getErrorMessage());
}
```

### 4. 执行单个步骤

```java
Map<String, Object> input = new HashMap<>();
input.put("confirmed", true);

CompletableFuture<StepResult> stepFuture = activationService.executeStep(
    activationId,
    "confirm-config",
    input
);

StepResult stepResult = stepFuture.get();
if (stepResult.isSuccess()) {
    System.out.println("步骤执行成功: " + stepResult.getStepName());
}
```

---

## ActivationRequest 结构

```java
public class ActivationRequest {
    private String sceneId;           // 场景ID（必需）
    private String userId;            // 用户ID（必需）
    private String role;              // 角色ID（可选，自动推断）
    private Map<String, Object> properties; // 扩展属性
}
```

---

## ActivationResult 结构

```java
public class ActivationResult {
    private boolean success;          // 是否成功
    private String activationId;      // 激活流程ID
    private String sceneId;           // 场景ID
    private String userId;            // 用户ID
    private ActivationPhase completedPhase; // 完成阶段
    private String errorMessage;      // 错误信息
}
```

---

## 激活流程

```
startActivation()
    │
    ├─▶ 创建 ActivationProcess
    │
    ├─▶ 获取 SceneTemplate
    │       │
    │       └─▶ 失败 → 返回错误
    │
    ├─▶ 执行激活步骤 executeActivationSteps()
    │       │
    │       ├─▶ 获取角色激活步骤
    │       │
    │       └─▶ 遍历执行每个步骤
    │               │
    │               ├─▶ autoExecute=true → 自动执行
    │               │
    │               ├─▶ skippable=true → 标记为待处理
    │               │
    │               └─▶ required=true → 必须执行
    │
    ├─▶ 注册菜单 registerMenus()
    │       │
    │       └─▶ 调用 MenuGenerationEngine
    │
    └─▶ 返回 ActivationResult
```

---

## 激活步骤执行

### 步骤类型

| 类型 | 说明 | 执行方式 |
|------|------|----------|
| `autoExecute=true` | 自动执行步骤 | 系统自动执行，无需用户干预 |
| `skippable=true` | 可跳过步骤 | 用户可选择跳过 |
| `required=true` | 必需步骤 | 必须执行，否则激活失败 |

### 步骤执行器 SPI

通过 `ExtensionPointRegistry` 获取步骤执行器：

```java
public interface ActivationStepExecutor {
    boolean canExecute(ActivationStepConfig stepConfig);
    StepResult execute(ActivationStepConfig stepConfig, 
                       ActivationProcess process, 
                       Map<String, Object> context);
}
```

### 注册自定义执行器

```java
@Component
public class CustomActivationStepExecutor implements ActivationStepExecutor {
    
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

## 菜单注册

激活流程会自动调用 `MenuGenerationEngine` 注册菜单：

```java
private void registerMenus(SceneTemplate template, ActivationRequest request) {
    // 1. 获取角色的菜单配置
    List<MenuConfig> roleMenus = template.getMenusForRole(request.getRole());
    
    // 2. 转换为引擎格式
    MenuGenerationEngine.MenuConfig engineConfig = convertToEngineMenuConfig(
        request.getSceneId(), 
        roleMenus
    );
    
    // 3. 注册到菜单引擎
    menuGenerationEngine.updateMenuConfig(request.getSceneId(), engineConfig);
}
```

---

## 事件监听

### 订阅激活事件

```java
String subscriptionId = activationService.subscribeActivationEvent(
    activationId,
    new ActivationEventListener() {
        @Override
        public void onActivationEvent(ActivationEvent event) {
            System.out.println("事件: " + event.getEventType());
            System.out.println("消息: " + event.getMessage());
        }
    }
);
```

### 事件类型

| 事件类型 | 说明 |
|----------|------|
| `COMPLETED` | 激活完成 |
| `STEP_COMPLETED` | 步骤完成 |
| `STEP_FAILED` | 步骤失败 |
| `CANCELLED` | 激活取消 |

---

## 状态查询

### 获取激活状态

```java
ActivationStatus status = activationService.getActivationStatus(activationId);

System.out.println("阶段: " + status.getPhase());
System.out.println("进度: " + status.getProgress());
System.out.println("总步骤: " + status.getTotalSteps());
System.out.println("已完成: " + status.getCompletedSteps());
```

### 获取激活进度

```java
ActivationProgress progress = activationService.getProgress(activationId);

System.out.println("当前步骤索引: " + progress.getCurrentStepIndex());
for (StepProgress step : progress.getSteps()) {
    System.out.println(step.getStepName() + ": " + step.getStatus());
}
```

---

## 集成示例

### Spring Boot 集成

```java
@Configuration
public class SceneActivationConfig {
    
    @Bean
    public SceneActivationServiceImpl sceneActivationService(
            SceneConfigLoader sceneConfigLoader,
            ExtensionPointRegistry extensionPointRegistry,
            MenuGenerationEngine menuGenerationEngine) {
        return new SceneActivationServiceImpl(
            sceneConfigLoader,
            extensionPointRegistry,
            menuGenerationEngine
        );
    }
    
    @Bean
    public SceneConfigLoader sceneConfigLoader() {
        return new SceneConfigLoader();
    }
}
```

### MVP 集成

```java
@Service
public class SeCapabilityServiceImpl {
    
    private final SceneActivationServiceImpl activationService;
    
    public void activateScene(String capabilityId, String userId, String roleId) {
        // 1. 获取场景模板
        SceneTemplate template = sceneTemplateRegistry.get(capabilityId);
        
        // 2. 注册模板
        activationService.registerSceneTemplate(capabilityId, template);
        
        // 3. 启动激活
        ActivationRequest request = new ActivationRequest();
        request.setSceneId(capabilityId);
        request.setUserId(userId);
        request.setRole(roleId);
        
        ActivationResult result = activationService.startActivation(request).join();
        
        if (!result.isSuccess()) {
            throw new RuntimeException("激活失败: " + result.getErrorMessage());
        }
    }
}
```

---

## 相关文档

- [场景配置加载](01-scene-config-loader.md)
- [场景验证](02-scene-validation.md)
- [SPI服务暴露](../09-spi/01-service-provider.md)
