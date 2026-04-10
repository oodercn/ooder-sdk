# skill-hotplug-starter 动态路由注册问题修复报告

## 问题反馈来源

- **反馈文档**: `E:\apex\os\docs\skill-hotplug-route-registration-issue.md`
- **反馈团队**: 应用团队
- **问题版本**: skill-hotplug-starter-3.0.1
- **修复版本**: skill-hotplug-starter-3.0.1 (已更新本地仓库)

---

## 问题分析

### 问题现象

应用团队在使用 `skill-hotplug-starter-3.0.1.jar` 进行动态加载 Skills JAR 时，Controller 的路由没有被正确注册到 Spring MVC，导致 API 请求返回 404 错误。

```
GET http://localhost:8085/api/v1/discovery/methods
返回: 404 No static resource api/v1/discovery/methods
```

### 根本原因分析

经过代码审查，发现 **三个核心问题**：

#### 问题 1: skill.yaml 配置字段不匹配

**应用团队的 skill.yaml 配置**：
```yaml
spec:
  endpoints:           # 使用了 endpoints
    - path: /api/v1/discovery/methods
      controllerClass: # 使用了 controllerClass
```

**代码实际解析的字段**：
```yaml
spec:
  routes:              # 代码解析的是 routes
    - path: /api/v1/discovery/methods
      controller:      # 代码解析的是 controller
```

**影响**: 配置无法被正确解析，`config.getRoutes()` 返回 null，导致路由注册被跳过。

#### 问题 2: RouteDefinition 字段名不匹配

`RouteDefinition.java` 中只解析 `controller` 字段，不支持 `controllerClass` 字段。

#### 问题 3: Controller 实例化问题（最关键）

`RouteRegistry.createControllerInstance()` 方法直接使用反射创建 Controller 实例，没有使用 Spring ApplicationContext：

```java
// 原有问题代码
private Object createControllerInstance(Class<?> controllerClass) throws Exception {
    try {
        return controllerClass.newInstance();  // 直接反射创建
    } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException("Failed to create controller instance", e);
    }
}
```

**影响**:
- Controller 不是 Spring Bean，无法进行依赖注入
- `@Autowired`、`@Value` 等注解失效
- 如果 Controller 没有无参构造器，直接抛出异常

---

## 修复方案

### 修复 1: SkillConfiguration 支持 endpoints 和 routes 双字段

**文件**: `net.ooder.skill.hotplug.config.SkillConfiguration`

```java
// 解析路由配置（支持 routes 和 endpoints 两种字段名）
List<Map<String, Object>> routesData = (List<Map<String, Object>>) spec.get("routes");
if (routesData == null) {
    routesData = (List<Map<String, Object>>) spec.get("endpoints");
}
if (routesData != null) {
    config.routes = RouteDefinition.fromList(routesData);
}
```

### 修复 2: RouteDefinition 支持 controllerClass 和 controller 双字段

**文件**: `net.ooder.skill.hotplug.config.RouteDefinition`

```java
route.controllerClass = (String) data.get("controller");
if (route.controllerClass == null) {
    route.controllerClass = (String) data.get("controllerClass");
}
```

### 修复 3: RouteRegistry 使用 ApplicationContext 创建 Controller

**文件**: `net.ooder.skill.hotplug.registry.RouteRegistry`

```java
private Object createControllerInstance(Class<?> controllerClass) throws Exception {
    String className = controllerClass.getName();
    
    // 1. 尝试获取已存在的 Spring Bean
    try {
        Object existingBean = applicationContext.getBean(className);
        if (existingBean != null) {
            logger.debug("Using existing Spring bean for controller: {}", className);
            return existingBean;
        }
    } catch (Exception e) {
        logger.debug("No existing bean found for controller: {}", className);
    }
    
    // 2. 使用 Spring AutowireCapableBeanFactory 创建并自动装配
    try {
        Object bean = applicationContext.getAutowireCapableBeanFactory()
                .createBean(controllerClass);
        logger.info("Created and autowired controller instance: {}", className);
        return bean;
    } catch (Exception e) {
        logger.warn("Failed to create controller with Spring autowiring, trying default constructor: {}", className);
        // 3. 降级使用默认构造器
        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create controller instance: " + className, ex);
        }
    }
}
```

### 修复 4: 增加诊断日志

在 `PluginManager` 和 `RouteRegistry` 中添加了详细的诊断日志，便于问题排查。

---

## 修复后的 skill.yaml 配置示例

现在支持两种配置格式：

### 格式一（推荐）

```yaml
metadata:
  id: skill-discovery
  name: 能力发现服务
  version: 1.0.0

spec:
  routes:
    - path: /api/v1/discovery/methods
      method: GET
      controller: net.ooder.skill.discovery.controller.DiscoveryController
      methodName: getDiscoveryMethods
      description: 获取发现方法列表
```

### 格式二（兼容应用团队原有配置）

```yaml
metadata:
  id: skill-discovery
  name: 能力发现服务
  version: 1.0.0

spec:
  endpoints:
    - path: /api/v1/discovery/methods
      method: GET
      controllerClass: net.ooder.skill.discovery.controller.DiscoveryController
      methodName: getDiscoveryMethods
      description: 获取发现方法列表
```

---

## 验证方法

### 1. 更新依赖

确保应用项目使用最新的本地安装版本：

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-hotplug-starter</artifactId>
    <version>3.0.1</version>
</dependency>
```

### 2. 启动应用后检查日志

成功注册路由后，日志应显示：

```
[PluginManager] Registering 1 routes for skill: skill-discovery
[RouteRegistry] Registering route: GET /api/v1/discovery/methods -> DiscoveryController.getDiscoveryMethods
[RouteRegistry] Created and autowired controller instance: net.ooder.skill.discovery.controller.DiscoveryController
[RouteRegistry] Successfully registered route: GET /api/v1/discovery/methods to Spring MVC
[PluginManager] Skill installed successfully: skill-discovery
```

### 3. 测试 API 请求

```bash
curl http://localhost:8085/api/v1/discovery/methods
```

应返回正确的响应，而非 404 错误。

### 4. 故障排查

如果看到以下日志，说明配置解析有问题：

```
[PluginManager] No routes defined in skill configuration for: skill-discovery
```

请检查 skill.yaml 文件格式是否正确。

---

## 修改文件清单

| 文件路径 | 修改内容 |
|---------|---------|
| `net/ooder/skill/hotplug/config/SkillConfiguration.java` | 支持 `endpoints` 和 `routes` 双字段解析 |
| `net/ooder/skill/hotplug/config/RouteDefinition.java` | 支持 `controllerClass` 和 `controller` 双字段 |
| `net/ooder/skill/hotplug/registry/RouteRegistry.java` | 使用 ApplicationContext 创建 Controller，添加诊断日志 |
| `net/ooder/skill/hotplug/PluginManager.java` | 添加路由注册诊断日志 |

---

## 本地仓库安装位置

| 文件类型 | 本地仓库路径 |
|---------|-------------|
| POM | `D:\maven\.m2\repository\net\ooder\skill-hotplug-starter\3.0.1\skill-hotplug-starter-3.0.1.pom` |
| JAR | `D:\maven\.m2\repository\net\ooder\skill-hotplug-starter\3.0.1\skill-hotplug-starter-3.0.1.jar` |
| Sources | `D:\maven\.m2\repository\net\ooder\skill-hotplug-starter\3.0.1\skill-hotplug-starter-3.0.1-sources.jar` |
| Javadoc | `D:\maven\.m2\repository\net\ooder\skill-hotplug-starter\3.0.1\skill-hotplug-starter-3.0.1-javadoc.jar` |

---

## 后续建议

1. **统一配置规范**: 建议应用团队使用 `routes` 和 `controller` 字段，与框架保持一致
2. **Controller 设计**: 动态加载的 Controller 应提供无参构造器，或确保依赖注入的 Bean 已在主应用中注册
3. **版本管理**: 后续发布新版本时，建议更新版本号以区分修复版本

---

**文档创建时间**: 2026-03-31  
**文档版本**: 1.0  
**修复版本**: skill-hotplug-starter-3.0.1  
**联系人**: ooder 团队
