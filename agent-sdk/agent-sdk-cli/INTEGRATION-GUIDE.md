# Agent SDK CLI 设计和集成开发指南

## 目录

1. [概述](#1-概述)
2. [架构设计](#2-架构设计)
3. [Skill团队集成指南](#3-skill团队集成指南)
4. [应用团队集成指南](#4-应用团队集成指南)
5. [命令开发指南](#5-命令开发指南)
6. [最佳实践](#6-最佳实践)
7. [故障排除](#7-故障排除)

---

## 1. 概述

### 1.1 什么是 Agent SDK CLI

Agent SDK CLI 是 Ooder Agent SDK 的命令行接口，提供：

- **Skill 管理**: 安装、卸载、更新、启动、停止 Skill
- **场景管理**: 创建、管理、调用场景组
- **任务监控**: 异步任务状态查询和管理
- **扩展机制**: 支持自定义命令扩展

### 1.2 适用对象

- **Skill 开发团队**: 需要为 Skill 提供 CLI 管理能力的团队
- **应用开发团队**: 需要集成 CLI 到应用中的团队
- **运维团队**: 需要通过命令行管理 Agent 的团队

### 1.3 版本信息

- **版本**: 3.1.0
- **Java 要求**: JDK 11+
- **Spring Boot**: 2.7.x / 3.x

---

## 2. 架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLI 用户界面层                            │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ 交互式 CLI    │ │  命令行参数   │ │   结果输出    │            │
│  │ (JLine3)     │ │  (Picocli)   │ │ (Text/JSON)  │            │
│  └──────────────┘ └──────────────┘ └──────────────┘            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        命令路由层                                │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  CliRouter (DefaultCliRouter)                            │  │
│  │  - 命令注册/注销                                          │  │
│  │  - 命令路由分发                                           │  │
│  │  - 别名管理                                               │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        命令实现层                                │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │  Skill 命令   │ │  Scene 命令   │ │  Task 命令   │            │
│  │              │ │              │ │              │            │
│  │ - list       │ │ - list       │ │ - list       │            │
│  │ - info       │ │ - create     │ │ - status     │            │
│  │ - install    │ │ - invoke     │ │              │            │
│  │ - start      │ │ - event      │ │              │            │
│  └──────────────┘ └──────────────┘ └──────────────┘            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        适配器层                                  │
│  ┌──────────────────┐ ┌──────────────────┐ ┌────────────────┐  │
│  │ CliCommandAdapter │ │SceneManagerAdapter│ │TaskStatusMonitor│  │
│  │                  │ │                  │ │                │  │
│  │ - invokeSkill()  │ │ - createScene()  │ │ - submitTask() │  │
│  │ - getSkillInfo() │ │ - invokeCapability│ │ - getStatus()  │  │
│  └──────────────────┘ └──────────────────┘ └────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Agent SDK 层                              │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ SkillRegistry │ │ Collaborative │ │ SkillService │            │
│  │              │ │ SceneGroupMgr │ │              │            │
│  └──────────────┘ └──────────────┘ └──────────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 核心组件说明

| 组件 | 职责 | 关键接口 |
|------|------|----------|
| `CliRouter` | 命令路由和分发 | `route()`, `execute()` |
| `CliParser` | 命令行参数解析 | `parse()`, `getOptions()` |
| `CliCommand` | 命令接口 | `execute()`, `getName()` |
| `CliCommandAdapter` | Skill 调用适配 | `invokeSkill()` |
| `SceneManagerAdapter` | 场景管理适配 | `createSceneGroup()`, `invokeCapability()` |
| `TaskStatusMonitor` | 任务状态监控 | `submitTask()`, `getTaskStatus()` |
| `SecureCommandProxy` | 安全代理 | 权限检查、注入检测 |

---

## 3. Skill 团队集成指南

### 3.1 为 Skill 添加 CLI 支持

#### 3.1.1 实现 Skill 生命周期接口

```java
@Component
public class MySkillService implements SkillService {
    
    @Override
    public void start() {
        // Skill 启动逻辑
    }
    
    @Override
    public void stop() {
        // Skill 停止逻辑
    }
    
    @Override
    public boolean isRunning() {
        // 返回运行状态
        return true;
    }
    
    @Override
    public void executeAsync(SkillRequest request, SkillCallback callback) {
        // 异步执行逻辑
    }
}
```

#### 3.1.2 注册 Skill 到 Registry

```java
@Configuration
public class MySkillConfiguration {
    
    @Bean
    public SkillManifest mySkillManifest() {
        return SkillManifest.builder()
            .skillId("my-skill")
            .name("My Skill")
            .version("1.0.0")
            .description("My skill description")
            .skillType("UTILITY")
            .capabilities(Arrays.asList("process", "analyze"))
            .build();
    }
    
    @Bean
    public CommandLineRunner registerSkill(
            SkillRegistry registry,
            SkillManifest manifest,
            MySkillService service) {
        return args -> {
            registry.register(manifest, service);
        };
    }
}
```

#### 3.1.3 CLI 自动识别

一旦 Skill 注册到 `SkillRegistry`，CLI 会自动识别并提供以下命令：

```bash
# 查看 Skill 信息
ooder skill:info --skill-id my-skill

# 启动 Skill
ooder skill:start --skill-id my-skill

# 停止 Skill
ooder skill:stop --skill-id my-skill

# 调用 Skill 能力
ooder skill:exec --skill-id my-skill --capability process --param input="data"
```

### 3.2 自定义 Skill 命令

如果 Skill 需要自定义 CLI 命令，可以实现 `CliExtension` 接口：

```java
@Component
public class MySkillCliExtension implements CliExtension {
    
    @Override
    public String getId() {
        return "my-skill-cli";
    }
    
    @Override
    public String getName() {
        return "My Skill CLI Extension";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public List<CliCommand> getCommands() {
        return Arrays.asList(
            new MyCustomCommand()
        );
    }
    
    @Override
    public void initialize() {
        // 初始化逻辑
    }
    
    @Override
    public void destroy() {
        // 清理逻辑
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}

public class MyCustomCommand implements CliCommand {
    
    @Override
    public String getName() {
        return "my-skill:custom";
    }
    
    @Override
    public String getDescription() {
        return "My custom command";
    }
    
    @Override
    public CommandResult execute(CommandContext context) {
        // 命令逻辑
        return CommandResult.success("Executed");
    }
}
```

### 3.3 Skill 打包和安装

#### 3.3.1 创建 extension.properties

在 Skill 项目的 `src/main/resources` 下创建：

```properties
# extension.properties
extension.class=com.example.MySkillCliExtension
extension.id=my-skill-cli
extension.version=1.0.0
```

#### 3.3.2 打包为 JAR

```bash
mvn clean package
```

#### 3.3.3 通过 CLI 安装

```bash
# 安装 Skill
ooder skill:install --source /path/to/my-skill-1.0.0.jar

# 验证安装
ooder skill:list

# 查看信息
ooder skill:info --skill-id my-skill
```

---

## 4. 应用团队集成指南

### 4.1 Spring Boot 集成

#### 4.1.1 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-cli</artifactId>
    <version>3.1.0</version>
</dependency>
```

#### 4.1.2 配置 CLI

```yaml
# application.yml
ooder:
  cli:
    enabled: true
    security:
      enabled: true
      whitelist:
        - skill:list
        - skill:info
        - scene:list
      sensitive-keys:
        - password
        - secret
        - token
    interactive:
      enabled: true
      history-size: 1000
```

#### 4.1.3 自动配置（推荐方式）

创建 Spring Boot 自动配置类：

```java
@Configuration
@ConditionalOnProperty(prefix = "ooder.cli", name = "enabled", havingValue = "true")
public class OoderCliConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public OoderCli ooderCli(
            SkillRegistry skillRegistry,
            SkillInstaller skillInstaller,
            SkillInvoker skillInvoker,
            CollaborativeSceneGroupManager sceneGroupManager,
            @Autowired(required = false) PermissionEngine permissionEngine) {
        
        OoderCli cli = new OoderCli();
        
        // 注入依赖
        cli.setSkillRegistry(skillRegistry);
        cli.setSkillInstaller(skillInstaller);
        cli.setSkillInvoker(skillInvoker);
        cli.setSceneGroupManager(sceneGroupManager);
        
        if (permissionEngine != null) {
            cli.setPermissionEngine(permissionEngine);
        }
        
        // 初始化适配器和注册命令
        cli.initializeAdapters();
        
        return cli;
    }
}
```

#### 4.1.4 使用 CLI

```java
@Service
public class MyApplicationService {
    
    @Autowired
    private OoderCli cli;
    
    public void executeCommand(String[] args) {
        int exitCode = cli.run(args);
        // 处理结果
    }
}
```

### 4.2 非 Spring 集成

#### 4.2.1 手动配置

```java
public class MyApplication {
    
    public static void main(String[] args) {
        // 创建 CLI 实例
        OoderCli cli = new OoderCli();
        
        // 获取 SDK 依赖（通过你的方式）
        SkillRegistry skillRegistry = ...;
        SkillInstaller skillInstaller = ...;
        SkillInvoker skillInvoker = ...;
        CollaborativeSceneGroupManager sceneGroupManager = ...;
        
        // 注入依赖
        cli.setSkillRegistry(skillRegistry);
        cli.setSkillInstaller(skillInstaller);
        cli.setSkillInvoker(skillInvoker);
        cli.setSceneGroupManager(sceneGroupManager);
        
        // 初始化
        cli.initializeAdapters();
        
        // 运行
        int exitCode = cli.run(args);
        System.exit(exitCode);
    }
}
```

### 4.3 嵌入式使用

#### 4.3.1 程序化调用

```java
@Service
public class SkillManagementService {
    
    @Autowired
    private OoderCli cli;
    
    public SkillInfo getSkillInfo(String skillId) {
        String[] args = {"skill:info", "--skill-id", skillId, "--output", "json"};
        
        // 捕获输出
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));
        
        int exitCode = cli.run(args);
        
        System.setOut(originalOut);
        
        // 解析结果
        String output = baos.toString();
        return parseSkillInfo(output);
    }
    
    public boolean installSkill(String jarPath) {
        String[] args = {"skill:install", "--source", jarPath};
        int exitCode = cli.run(args);
        return exitCode == 0;
    }
}
```

#### 4.3.2 异步任务调用

```java
@Service
public class AsyncTaskService {
    
    @Autowired
    private CliCommandAdapter commandAdapter;
    
    public String submitAsyncTask(String skillId, String operation, Map<String, Object> params) {
        // 获取 Skill 服务
        SkillService service = ...;
        
        // 创建请求
        SkillRequest request = SkillRequest.create()
            .setSkillId(skillId)
            .setOperation(operation)
            .setParams(params);
        
        // 提交任务
        TaskStatusMonitor taskMonitor = new TaskStatusMonitor();
        String taskId = taskMonitor.submitTask(service, request);
        
        return taskId;
    }
    
    public TaskStatus getTaskStatus(String taskId) {
        TaskStatusMonitor taskMonitor = new TaskStatusMonitor();
        return taskMonitor.getTaskStatus(taskId);
    }
}
```

---

## 5. 命令开发指南

### 5.1 创建自定义命令

#### 5.1.1 实现 CliCommand 接口

```java
public class MyCustomCommand implements CliCommand {
    
    private final MyService service;
    
    // 通过构造函数注入依赖
    public MyCustomCommand(MyService service) {
        this.service = service;
    }
    
    @Override
    public String getName() {
        return "my:command";
    }
    
    @Override
    public String getDescription() {
        return "My custom command description";
    }
    
    @Override
    public String getUsage() {
        return "ooder my:command --param1 <value> [--optional <value>]";
    }
    
    @Override
    public CommandResult execute(CommandContext context) {
        try {
            // 获取参数
            String param1 = context.getAttribute("param1");
            String optional = context.getAttribute("optional");
            
            // 验证参数
            if (param1 == null || param1.isEmpty()) {
                return CommandResult.invalidArgs("param1 is required");
            }
            
            // 执行业务逻辑
            Object result = service.doSomething(param1, optional);
            
            // 返回成功
            return CommandResult.success("Operation completed", result);
            
        } catch (Exception e) {
            // 返回错误
            return CommandResult.error("Operation failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getCategory() {
        return "custom";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"my-cmd"};
    }
    
    @Override
    public boolean validate(String[] args) {
        // 参数预验证
        for (int i = 0; i < args.length; i++) {
            if ("--param1".equals(args[i]) && i + 1 < args.length) {
                return true;
            }
        }
        return false;
    }
}
```

#### 5.1.2 注册命令

```java
@Configuration
public class MyCommandConfiguration {
    
    @Bean
    public CommandLineRunner registerCustomCommand(
            OoderCli cli,
            MyService service) {
        return args -> {
            MyCustomCommand command = new MyCustomCommand(service);
            cli.getRouter().register(command);
        };
    }
}
```

### 5.2 命令安全

#### 5.2.1 使用 SecureCommandProxy

```java
// 包装命令以添加安全检查
SecureCommandProxy secureCommand = new SecureCommandProxy(
    command,
    permissionEngine,
    auditor,
    Set.of("my:command")  // 白名单
);

cli.getRouter().register(secureCommand);
```

#### 5.2.2 权限声明

```java
public class MySecureCommand implements CliCommand {
    
    @Override
    public List<String> getRequiredPermissions() {
        return Arrays.asList("my:command:execute");
    }
    
    @Override
    public CommandResult execute(CommandContext context) {
        // 权限检查会自动进行
        // ...
    }
}
```

---

## 6. 最佳实践

### 6.1 依赖注入

✅ **推荐**: 通过构造函数注入依赖
```java
public class MyCommand implements CliCommand {
    private final MyService service;
    
    public MyCommand(MyService service) {
        this.service = service;
    }
}
```

❌ **避免**: 使用静态工厂或全局状态
```java
public class MyCommand implements CliCommand {
    private MyService service = ServiceFactory.getInstance();  // 不要这样做
}
```

### 6.2 错误处理

✅ **推荐**: 详细的错误信息和异常链
```java
try {
    // 业务逻辑
} catch (SpecificException e) {
    log.error("Specific error occurred", e);
    return CommandResult.error("Specific error: " + e.getMessage(), e);
}
```

❌ **避免**: 吞掉异常或返回模糊错误
```java
try {
    // 业务逻辑
} catch (Exception e) {
    return CommandResult.error("Error");  // 不要这样做
}
```

### 6.3 日志记录

✅ **推荐**: 使用 SLF4J 记录关键操作
```java
private static final Logger log = LoggerFactory.getLogger(MyCommand.class);

public CommandResult execute(CommandContext context) {
    log.info("Executing command: {} with params: {}", getName(), context.getAttributes());
    // ...
}
```

### 6.4 参数验证

✅ **推荐**: 在 execute 方法中进行验证
```java
@Override
public CommandResult execute(CommandContext context) {
    String requiredParam = context.getAttribute("required");
    if (requiredParam == null || requiredParam.isEmpty()) {
        return CommandResult.invalidArgs("required parameter is missing");
    }
    // ...
}
```

### 6.5 结果格式化

✅ **推荐**: 支持多种输出格式
```java
@Override
public CommandResult execute(CommandContext context) {
    Object data = getData();
    
    // 根据输出格式返回不同结构
    String outputFormat = context.getOutputFormat();
    if ("json".equals(outputFormat)) {
        return CommandResult.success("Success", toJson(data));
    } else {
        return CommandResult.success("Success", toText(data));
    }
}
```

---

## 7. 故障排除

### 7.1 常见问题

#### Q1: 命令未找到

**症状**: `Unknown command: xxx`

**解决方案**:
1. 检查命令是否正确注册
2. 检查命令名称是否正确
3. 查看日志确认注册是否成功

```java
// 调试：列出所有注册命令
cli.getRouter().getAllCommands().forEach(cmd -> 
    System.out.println(cmd.getName())
);
```

#### Q2: 依赖注入失败

**症状**: `NullPointerException` 在命令执行时

**解决方案**:
1. 确保通过构造函数注入依赖
2. 检查 `initializeAdapters()` 是否在设置依赖后调用
3. 验证依赖 Bean 是否正确创建

```java
// 检查依赖
if (service == null) {
    log.error("Service not injected");
    return CommandResult.error("Service not available");
}
```

#### Q3: 权限被拒绝

**症状**: `Permission denied for command: xxx`

**解决方案**:
1. 检查命令是否在白名单中
2. 验证用户权限
3. 检查 `PermissionEngine` 配置

```java
// 调试：检查权限
boolean hasPermission = permissionEngine.hasPermission(userId, "cli", commandName);
System.out.println("Has permission: " + hasPermission);
```

#### Q4: 安全验证失败

**症状**: `Security validation failed`

**解决方案**:
1. 检查参数是否包含危险字符
2. 验证 SQL 注入检测规则
3. 检查敏感信息过滤配置

```java
// 临时禁用安全检查（仅用于调试）
cli.setCommandWhitelist(Set.of("*"));  // 允许所有命令
```

### 7.2 调试技巧

#### 启用详细日志

```yaml
# application.yml
logging:
  level:
    net.ooder.sdk.cli: DEBUG
```

#### 交互式调试

```bash
# 启动交互式模式
ooder

# 在交互式模式中测试命令
ooder> skill:list --verbose
```

#### 单元测试

```java
@Test
public void testMyCommand() {
    MyService mockService = mock(MyService.class);
    when(mockService.doSomething(any())).thenReturn("result");
    
    MyCommand command = new MyCommand(mockService);
    CommandContext context = new CommandContext();
    context.setAttribute("param1", "value");
    
    CommandResult result = command.execute(context);
    
    assertTrue(result.isSuccess());
    assertEquals("result", result.getData());
}
```

---

## 附录

### A. 完整命令列表

| 命令 | 描述 | 示例 |
|------|------|------|
| `skill:list` | 列出所有 Skills | `ooder skill:list` |
| `skill:info` | 查看 Skill 详情 | `ooder skill:info --skill-id my-skill` |
| `skill:install` | 安装 Skill | `ooder skill:install --source /path/to/skill.jar` |
| `skill:uninstall` | 卸载 Skill | `ooder skill:uninstall --skill-id my-skill` |
| `skill:update` | 更新 Skill | `ooder skill:update --skill-id my-skill` |
| `skill:start` | 启动 Skill | `ooder skill:start --skill-id my-skill` |
| `skill:stop` | 停止 Skill | `ooder skill:stop --skill-id my-skill` |
| `scene:list` | 列出所有场景 | `ooder scene:list` |
| `scene:create` | 创建场景 | `ooder scene:create --group-id g1 --main cap1` |
| `scene:info` | 查看场景详情 | `ooder scene:info --group-id g1` |
| `scene:invoke` | 调用场景能力 | `ooder scene:invoke --group-id g1 --capability cap1` |
| `scene:event` | 发布场景事件 | `ooder scene:event --group-id g1 --type event1` |
| `task:list` | 列出所有任务 | `ooder task:list` |
| `task:status` | 查询任务状态 | `ooder task:status --task-id task-xxx` |
| `status` | 系统状态 | `ooder status` |

### B. 配置参考

```yaml
ooder:
  cli:
    enabled: true
    output:
      default-format: text  # text, json, table
      color-enabled: true
    security:
      enabled: true
      whitelist-enabled: true
      injection-check-enabled: true
      dangerous-chars-check-enabled: true
      audit-enabled: true
      whitelist:
        - skill:list
        - skill:info
        - scene:list
      sensitive-keys:
        - password
        - secret
        - token
    interactive:
      enabled: true
      history-size: 1000
      history-file: "${user.home}/.ooder/cli-history"
      prompt: "ooder> "
    task:
      default-timeout: 60
      cleanup-interval: 3600
      max-concurrent: 10
```

### C. 相关文档

- [Agent SDK Core 文档](../agent-sdk-core/README.md)
- [Skills Framework 文档](../skills-framework/README.md)
- [API JavaDoc](../apidocs/index.html)

---

**文档版本**: 3.1.0  
**最后更新**: 2026-04-16  
**维护团队**: Agent SDK Team
