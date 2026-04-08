# Skill Hot Plug Starter

Ooder Skill 热插拔支持模块，提供无需重启的 Skill 动态安装、卸载、更新能力。

## 功能特性

- **动态安装**: 运行时安装新的 Skill，无需重启应用
- **动态卸载**: 安全卸载已安装的 Skill，释放资源
- **热更新**: 支持 Skill 版本更新，平滑过渡
- **类隔离**: 每个 Skill 拥有独立的 ClassLoader，避免类冲突
- **服务代理**: 动态服务注册与发现
- **路由动态注册**: Spring MVC 路由动态注册与注销

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-hotplug-starter</artifactId>
    <version>3.0.1</version>
</dependency>
```

### 2. 配置属性

```yaml
ooder:
  skill:
    hotplug:
      enabled: true              # 启用热插拔
      plugin-directory: ./plugins # 插件目录
      auto-load: true            # 自动加载插件目录中的插件
      class-loader-cache-size: 100 # 类加载器缓存大小
      isolation-enabled: true    # 启用插件隔离
      sources:                   # 多来源加载配置
        - type: system           # 来源类型：system、plugins、external
          path: ./skills/_system
          enabled: true
          priority: 10           # 加载优先级（数字越小优先级越高）
          excludes:              # 排除的 Skill ID 列表
            - skill-tenant       # 排除需要特殊处理的 Skill
            - skill-rag
        - type: plugins
          path: ./skills/_business
          enabled: true
          priority: 20
```

### 3. 使用 PluginManager

```java
@Autowired
private PluginManager pluginManager;

// 安装 Skill
SkillPackage skillPackage = SkillPackage.fromFile(new File("skill-xxx.jar"));
PluginInstallResult result = pluginManager.installSkill(skillPackage);

// 卸载 Skill
PluginUninstallResult result = pluginManager.uninstallSkill("skill-xxx");

// 更新 Skill
PluginUpdateResult result = pluginManager.updateSkill("skill-xxx", newPackage);

// 获取已安装的 Skill 列表
List<PluginInfo> plugins = pluginManager.getInstalledSkills();
```

## REST API

热插拔模块提供以下 REST API：

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/plugins` | GET | 获取所有已安装的插件 |
| `/api/plugins/{skillId}` | GET | 获取指定插件信息 |
| `/api/plugins/install` | POST | 上传并安装插件 |
| `/api/plugins/{skillId}/uninstall` | POST | 卸载插件 |
| `/api/plugins/{skillId}/update` | POST | 更新插件 |
| `/api/plugins/{skillId}/status` | GET | 获取插件状态 |
| `/api/plugins/{skillId}/check` | GET | 检查插件是否已安装 |

## Skill 包结构

支持热插拔的 Skill 包需要包含 `skill.yaml` 配置文件：

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-example
  name: Example Skill
  version: 1.0.0
  description: An example skill
  author: Ooder Team
  category: business
  
spec:
  # 设计时概念（SkillCard 新字段）
  skillForm: STANDALONE        # 技能形态：SCENE、STANDALONE、PROVIDER、DRIVER
  skillCategory: TOOL          # 技能分类：KNOWLEDGE、LLM、TOOL、WORKFLOW、DATA、SERVICE、UI
  sceneType: AUTO              # 场景类型：AUTO、TRIGGER、PRIMARY、COLLABORATIVE
  purposes:                    # 服务目的（可多选）
    - TEAM
    - PERSISTENT
  
  # 运行时配置
  dependencies:
    - dependency1.jar
    - dependency2.jar

  lifecycle:
    startup: com.example.ExampleSkillStartup
    shutdown: com.example.ExampleSkillShutdown

  routes:
    - path: /api/example/hello
      method: GET
      controller: com.example.ExampleController
      methodName: hello
      produces: application/json

  services:
    - name: exampleService
      interface: com.example.ExampleService
      implementation: com.example.ExampleServiceImpl
      singleton: true

  ui:
    type: html
  entry: index.html
  staticResources:
    - css/
    - js/
  cdnDependencies:
    - https://cdn.example.com/lib.js
```

## 架构说明

### 核心组件

1. **PluginManager**: 插件管理器，负责安装、卸载、更新
2. **PluginClassLoader**: 插件类加载器，实现类隔离
3. **ClassLoaderManager**: 类加载器管理器
4. **RouteRegistry**: 路由注册器，动态注册 Spring MVC 路由
5. **ServiceRegistry**: 服务注册器，管理服务代理

### 类加载机制

```
Bootstrap ClassLoader
       ↑
Extension ClassLoader
       ↑
Application ClassLoader (Parent)
       ↑
PluginClassLoader (Skill 专用)
```

- 系统类（java.*, javax.*, org.springframework.* 等）由父类加载器加载
- Skill 自己的类由 PluginClassLoader 加载
- 实现类隔离，避免版本冲突

### 生命周期

```
INSTALLING → INSTALLED → STARTING → ACTIVE
                                    ↓
UNINSTALLED ← STOPPING ← STOPPED ← ERROR
```

## 注意事项

1. **内存泄漏**: 卸载 Skill 后，建议触发一次 Full GC 以确保类加载器被回收
2. **线程安全**: 卸载 Skill 前确保没有正在执行的请求
3. **资源释放**: Skill 应在 `onStop` 回调中释放所有资源
4. **数据库连接**: Skill 使用数据库连接池时，卸载时需要关闭连接
5. **依赖注入**: 由于 ClassLoader 隔离，JAR 包中的 Controller 需要特殊处理依赖注入（见下文）
6. **JPA 配置**: JPA 相关配置（DataSource、EntityManagerFactory、TransactionManager）不通过热加载处理，由 Spring Boot 自动配置机制在应用启动时加载

## JPA 配置说明

### 热加载与 JPA 配置的关系

热加载模块**不会处理** JPA 相关的配置类，包括：
- `DataSource` 配置
- `EntityManagerFactory` 配置
- `TransactionManager` 配置
- `@EnableJpaRepositories` 注解
- Hibernate 相关配置

这些配置应该通过 `spring.factories` 注册，由 Spring Boot 在应用启动时自动加载。

### Skill JPA 配置规范

每个需要 JPA 的 Skill 应该：

1. **创建独立的 JPA 配置类**：

```java
@Configuration
@EnableJpaRepositories(
    basePackages = "net.ooder.skill.xxx.repository",
    entityManagerFactoryRef = "xxxEntityManagerFactory",
    transactionManagerRef = "xxxTransactionManager"
)
@ConditionalOnProperty(name = "skill.xxx.jpa.enabled", havingValue = "true", matchIfMissing = true)
public class XxxJpaConfiguration extends SkillJpaConfigurationSupport {

    public XxxJpaConfiguration() {
        super("xxx", "net.ooder.skill.xxx.entity");
    }
}
```

2. **在 spring.factories 中注册**：

```properties
# META-INF/spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
net.ooder.skill.xxx.XxxAutoConfiguration,\
net.ooder.skill.xxx.config.XxxJpaConfiguration
```

3. **配置文件**：

```yaml
# application.yml
skill:
  xxx:
    enabled: true
    jpa:
      enabled: true
    db:
      path: ./data/xxx.db
```

### 为什么热加载不处理 JPA 配置？

1. **生命周期问题**: JPA 配置需要在 Spring 上下文启动时处理，而热加载是在运行时
2. **EntityManager 冲突**: 多个 EntityManager 需要在启动时正确初始化
3. **事务管理**: 事务管理器需要在 Spring 容器中正确注册
4. **类加载器隔离**: JPA 实体类需要被正确的 ClassLoader 加载

### 验证 JPA 配置是否生效

启动应用后，检查日志：

```
INFO  HikariDataSource - HikariPool-1 - Starting...
INFO  Hibernate: create table if not exists xxx...
INFO  JpaTransactionManager - Creating new transaction...
```

如果看到这些日志，说明 JPA 配置已正确加载。

## 二次开发指南

### Controller 依赖注入问题

#### 问题描述

由于 ClassLoader 隔离机制，JAR 包中的 Controller 类无法通过 Spring 的 `AutowireCapableBeanFactory.createBean()` 正确创建实例。当 Spring 自动装配失败时，系统会回退到使用默认构造函数创建 Controller 实例，导致 `@Autowired` 和 `@Resource` 注入的字段为 `null`。

#### 解决方案

系统已实现**手动依赖注入**机制，在默认构造函数创建实例后，自动扫描并注入依赖。

#### 依赖解析优先级

1. Spring 容器按名称查找（`@Resource` 指定名称）
2. Spring 容器按类型查找
3. **ServiceRegistry 按接口类型查找**（当前 Skill 注册的服务）
4. **ServiceRegistry 按服务名称查找**（当前 Skill 注册的服务）
5. Spring 容器按字段名查找
6. Spring 容器按类名查找

### skill.yaml 配置规范

为确保 Controller 依赖注入正常工作，**必须在 `skill.yaml` 中声明 `services` 部分**：

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-example
  name: Example Skill
  version: 1.0.0

spec:
  services:
    - name: exampleService          # 服务名称，用于 Controller 中 @Autowired 字段名匹配
      interface: com.example.ExampleService
      implementation: com.example.ExampleServiceImpl
    - name: anotherService
      interface: com.example.AnotherService
      implementation: com.example.AnotherServiceImpl

  endpoints:
    - path: /api/example/list
      method: POST
      controllerClass: com.example.ExampleController
      methodName: listItems
```

### Controller 开发规范

```java
@RestController
@RequestMapping("/api/example")
public class ExampleController {

    @Autowired
    private ExampleService exampleService;  // 字段名与 skill.yaml 中服务名称一致
    
    @Autowired
    private AnotherService anotherService;  // 或按接口类型匹配

    @PostMapping("/list")
    public Result listItems(@RequestBody Map<String, Object> request) {
        // exampleService 已被自动注入，可直接使用
        return Result.success(exampleService.list());
    }
}
```

### Service 开发规范

Service 实现类应使用**无参构造函数**或确保依赖通过 Spring 容器获取：

```java
@Service
public class ExampleServiceImpl implements ExampleService {

    // 推荐：无参构造函数 + 内部数据存储
    private final Map<String, Item> dataStore = new ConcurrentHashMap<>();
    
    // 如果需要 Spring Bean 依赖
    @Autowired
    private SomeOtherService otherService;  // 会通过手动依赖注入

    @Override
    public List<Item> list() {
        return new ArrayList<>(dataStore.values());
    }
}
```

### 核心代码修改

#### RouteRegistry.java

```java
private Object createNewBean(Class<?> controllerClass, String className) throws Exception {
    try {
        // 优先尝试 Spring 自动装配
        return applicationContext.getAutowireCapableBeanFactory().createBean(controllerClass);
    } catch (Exception e) {
        // 回退到默认构造函数 + 手动依赖注入
        Object bean = controllerClass.getDeclaredConstructor().newInstance();
        manualInjectDependencies(bean, controllerClass);
        return bean;
    }
}

private void manualInjectDependencies(Object bean, Class<?> clazz) {
    // 扫描 @Autowired 和 @Resource 字段
    // 从 Spring 容器或 ServiceRegistry 获取依赖并注入
}
```

#### ServiceRegistry.java

```java
private Object createServiceInstance(Class<?> implClass) throws Exception {
    try {
        // 优先尝试 Spring 自动装配
        return applicationContext.getAutowireCapableBeanFactory().createBean(implClass);
    } catch (Exception e) {
        // 回退到默认构造函数 + 手动依赖注入
        Object instance = implClass.getDeclaredConstructor().newInstance();
        manualInjectDependencies(instance, implClass);
        return instance;
    }
}
```

### 调试技巧

1. **查看日志**：启动时观察以下日志
   ```
   INFO  RouteRegistry - Manually injected dependency: sceneService -> SceneServiceImpl in SceneController
   INFO  ServiceRegistry - Created service instance with default constructor: SceneServiceImpl
   ```

2. **检查 skill.yaml**：确保 `services` 部分正确声明
   ```bash
   # 解压 JAR 包检查
   jar -tf skill-scenes-1.0.0.jar | grep skill.yaml
   unzip -p skill-scenes-1.0.0.jar skill.yaml
   ```

3. **验证依赖注入**：调用 API 检查是否返回 NullPointerException
   ```bash
   curl -X POST http://localhost:8080/api/v1/scenes/list \
        -H "Content-Type: application/json" \
        -d '{"pageNum":1,"pageSize":10}'
   ```

### 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| NullPointerException | Controller 依赖未注入 | 检查 skill.yaml 中 services 声明 |
| ClassNotFoundException | 类加载失败 | 检查 JAR 包依赖是否完整 |
| BeanCreationException | Spring 创建 Bean 失败 | 查看日志，确保无参构造函数存在 |
| Service not found | ServiceRegistry 中无此服务 | 确保 services 配置正确且加载成功 |

## 示例代码

### 实现生命周期接口

```java
public class MySkillLifecycle implements SkillLifecycle {
    
    @Override
    public void onStart(PluginContext context) {
        System.out.println("Skill started: " + context.getSkillId());
        // 初始化资源
    }
    
    @Override
    public void onStop(PluginContext context) {
        System.out.println("Skill stopped: " + context.getSkillId());
        // 释放资源
    }
}
```

### 监听状态变更

```java
pluginManager.addStateListener((state, context) -> {
    System.out.println("Skill " + context.getSkillId() + " state changed to: " + state);
});
```

## 版本历史

- **3.0.2**: 新增 JPA 配置排除机制，新增 excludes 配置支持，新增有参数 @Bean 方法支持，新增 SkillCard 字段解析（skillForm, skillCategory, sceneType, purposes）
- **3.0.1**: 新增手动依赖注入机制，解决 ClassLoader 隔离导致的 Controller 依赖注入问题
- **0.7.3**: 初始版本，支持基本的安装/卸载/更新功能
