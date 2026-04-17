# 第四册：CLI 设计实现

## 目录

1. [CLI 架构定位](#1-cli-架构定位)
2. [命令体系设计](#2-命令体系设计)
3. [与 Agent SDK 集成](#3-与-agent-sdk-集成)
4. [与 SceneEngine 集成](#4-与-sceneengine-集成)
5. [命令透传安全](#5-命令透传安全)
6. [实现示例](#6-实现示例)

---

## 1. CLI 架构定位

### 1.1 在整体架构中的位置

```
┌─────────────────────────────────────────┐
│  ┌─────────────────────────────────┐   │
│  │         CLI 工具层               │   │
│  │  ┌─────────────────────────┐   │   │
│  │  │  - 命令解析              │   │   │
│  │  │  - 权限校验              │   │   │
│  │  │  - 结果展示              │   │   │
│  │  │  - 交互模式              │   │   │
│  │  └─────────────────────────┘   │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           Agent SDK 协议层               │
│  ┌─────────────────────────────────┐   │
│  │  - Command 通道                  │   │
│  │  - 异步任务                      │   │
│  │  - 消息队列                      │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           SceneEngine 场景层             │
│  ┌─────────────────────────────────┐   │
│  │  - 场景编排                      │   │
│  │  - 事件驱动                      │   │
│  │  - 状态管理                      │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### 1.2 核心职责

| 职责 | 说明 | 实现方式 |
|------|------|----------|
| 命令解析 | 解析用户输入 | picocli 框架 |
| 权限校验 | 验证用户权限 | AuthService 集成 |
| 命令路由 | 路由到对应处理器 | Command Router |
| 结果展示 | 格式化输出 | 表格/JSON/交互式 |

---

## 2. 命令体系设计

### 2.1 命令结构

```
skill [全局选项] <命令> [子命令] [参数] [选项]

示例：
skill --output=json install rag-skill --version=1.2.0
skill exec rag-skill reindex --knowledgeBase=docs
skill scene create --type=meeting --participants=user1,user2
```

### 2.2 命令分类

```
skill
├── Core Commands (核心命令)
│   ├── list          # 列出所有 Skills
│   ├── info          # 查看 Skill 详情
│   ├── install       # 安装 Skill
│   ├── uninstall     # 卸载 Skill
│   ├── start         # 启动 Skill
│   ├── stop          # 停止 Skill
│   └── update        # 更新 Skill
│
├── NLP Commands (NLP 命令)
│   ├── nlp convert   # 自然语言转组件
│   ├── nlp skills    # 列出 NLP Skills
│   └── nlp execute   # 执行 NLP 指令
│
├── LLM Commands (LLM 命令)
│   ├── llm generate  # LLM 生成内容
│   ├── llm intent    # 意图识别
│   └── llm chat      # 启动对话模式
│
├── Scene Commands (场景命令)
│   ├── scene create  # 创建场景
│   ├── scene list    # 列出场景
│   ├── scene info    # 查看场景详情
│   ├── scene invoke  # 调用场景能力
│   └── scene event   # 发布场景事件
│
└── Extension Commands (扩展命令)
    └── exec <skill-id> <command>  # 执行 Skill 扩展命令
```

### 2.3 命令接口设计

```java
/**
 * CLI 命令接口
 */
public interface CliCommand {
    
    /**
     * 命令名称
     */
    String getName();
    
    /**
     * 命令描述
     */
    String getDescription();
    
    /**
     * 命令用法
     */
    String getUsage();
    
    /**
     * 执行命令
     */
    CliResult execute(CliContext context);
    
    /**
     * 获取子命令
     */
    default List<CliCommand> getSubCommands() {
        return Collections.emptyList();
    }
    
    /**
     * 获取参数定义
     */
    default List<ParamDefinition> getParameters() {
        return Collections.emptyList();
    }
    
    /**
     * 获取所需权限
     */
    default List<String> getRequiredPermissions() {
        return Collections.emptyList();
    }
}

/**
 * CLI 上下文
 */
public interface CliContext {
    
    /**
     * 获取用户会话
     */
    UserSession getUserSession();
    
    /**
     * 获取参数值
     */
    String getParameter(String name);
    
    /**
     * 获取选项值
     */
    String getOption(String name);
    
    /**
     * 获取原始参数
     */
    String[] getRawArgs();
    
    /**
     * 获取输出格式
     */
    OutputFormat getOutputFormat();
    
    /**
     * 是否交互模式
     */
    boolean isInteractive();
}

/**
 * CLI 结果
 */
public interface CliResult {
    
    int getExitCode();
    
    String getOutput();
    
    String getErrorOutput();
    
    boolean isSuccess();
    
    /**
     * 如果是异步任务，返回任务 ID
     */
    String getTaskId();
}
```

---

## 3. 与 Agent SDK 集成

### 3.1 集成架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CLI 与 Agent SDK 集成                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                          CLI 层                                      │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐│   │
│  │  │   Parser    │  │   Router    │  │  Validator  │  │  Renderer   ││   │
│  │  │  (命令解析)  │  │  (路由分发)  │  │  (参数校验)  │  │  (结果渲染)  ││   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘│   │
│  └─────────┼────────────────┼────────────────┼────────────────┼────────┘   │
│            │                │                │                │            │
│            └────────────────┴────────────────┴────────────────┘            │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    CLI Command Adapter                               │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │  - 将 CLI 命令转换为 Agent SDK Command                       │   │   │
│  │  │  - 处理异步任务状态                                          │   │   │
│  │  │  - 结果格式转换                                              │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                       Agent SDK 层                                   │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │  - Command Registry                                          │   │   │
│  │  │  - Command Executor                                          │   │   │
│  │  │  - Task Queue                                                │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 集成实现

```java
/**
 * CLI Command 适配器
 */
@Component
public class CliCommandAdapter {
    
    @Autowired
    private CommandRegistry commandRegistry;
    
    @Autowired
    private CommandExecutor commandExecutor;
    
    @Autowired
    private TaskQueue taskQueue;
    
    /**
     * 执行 CLI 命令
     */
    public CliResult execute(CliCommand cliCommand, CliContext context) {
        // 1. 权限校验
        if (!hasPermission(context.getUserSession(), cliCommand.getRequiredPermissions())) {
            return CliResult.error("Permission denied");
        }
        
        // 2. 构建 Agent SDK Command
        Command<?> agentCommand = convertToAgentCommand(cliCommand, context);
        
        // 3. 构建 CommandContext
        CommandContext commandContext = CommandContext.builder()
            .caller(CallerInfo.from(context.getUserSession()))
            .parameters(extractParameters(context))
            .build();
        
        // 4. 执行
        if (agentCommand.isAsync()) {
            // 异步执行
            TaskId taskId = commandExecutor.executeAsync(agentCommand, commandContext);
            return CliResult.async(taskId, "Task submitted: " + taskId);
        } else {
            // 同步执行
            CommandResult<?> result = commandExecutor.execute(agentCommand, commandContext);
            return convertToCliResult(result, context.getOutputFormat());
        }
    }
    
    /**
     * 查询异步任务状态
     */
    public CliResult queryTaskStatus(String taskId, OutputFormat format) {
        TaskStatus status = taskQueue.getStatus(TaskId.of(taskId));
        TaskResult result = taskQueue.getResult(TaskId.of(taskId));
        
        return formatTaskStatus(status, result, format);
    }
    
    /**
     * 等待异步任务完成
     */
    public CliResult waitForTask(String taskId, Duration timeout, OutputFormat format) {
        TaskId id = TaskId.of(taskId);
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < timeout.toMillis()) {
            TaskStatus status = taskQueue.getStatus(id);
            
            if (status == TaskStatus.COMPLETED || status == TaskStatus.FAILED) {
                TaskResult result = taskQueue.getResult(id);
                return formatTaskResult(result, format);
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return CliResult.error("Interrupted");
            }
        }
        
        return CliResult.error("Timeout waiting for task: " + taskId);
    }
}
```

---

## 4. 与 SceneEngine 集成

### 4.1 集成架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CLI 与 SceneEngine 集成                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                          CLI 层                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │  scene create --type=meeting --participants=user1,user2     │   │   │
│  │  │  scene invoke <scene-id> rag-skill:search --query=xxx       │   │   │
│  │  │  scene event <scene-id> participant.joined --user=user3     │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Scene CLI Adapter                                 │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │  - 场景命令解析                                                │   │   │
│  │  │  - 场景上下文构建                                              │   │   │
│  │  │  - 能力调用封装                                                │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                       SceneEngine 层                                 │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │  - SceneContextApi                                            │   │   │
│  │  │  - CapabilityBindingService                                   │   │   │
│  │  │  - SceneEventBus                                              │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 场景命令实现

```java
/**
 * 场景命令处理器
 */
@Component
public class SceneCommandHandler {
    
    @Autowired
    private SceneContextApi sceneContextApi;
    
    @Autowired
    private CapabilityBindingService capabilityBindingService;
    
    @Autowired
    private SceneEventBus eventBus;
    
    /**
     * 创建场景
     */
    public CliResult createScene(CliContext context) {
        String sceneType = context.getOption("type");
        String participants = context.getOption("participants");
        
        // 1. 创建场景
        SceneContext scene = sceneContextApi.createScene(
            SceneCreateRequest.builder()
                .sceneType(sceneType)
                .participants(parseParticipants(participants))
                .build()
        );
        
        // 2. 根据场景类型自动绑定能力
        List<String> requiredCapabilities = getRequiredCapabilities(sceneType);
        for (String capabilityId : requiredCapabilities) {
            capabilityBindingService.bindCapability(
                scene.getSceneGroupId(),
                capabilityId,
                BindConfiguration.builder()
                    .bindRole(BindRole.PRIMARY)
                    .build()
            );
        }
        
        return CliResult.success(Map.of(
            "sceneGroupId", scene.getSceneGroupId(),
            "sceneType", sceneType,
            "status", "CREATED"
        ));
    }
    
    /**
     * 调用场景能力
     */
    public CliResult invokeCapability(CliContext context) {
        String sceneId = context.getParameter("scene-id");
        String capabilityId = context.getParameter("capability-id");
        Map<String, Object> params = extractParams(context);
        
        // 1. 获取场景上下文
        SceneContext scene = sceneContextApi.getScene(sceneId);
        
        // 2. 检查用户是否是场景参与者
        if (!isParticipant(scene, context.getUserSession())) {
            return CliResult.error("Not a participant of this scene");
        }
        
        // 3. 调用能力
        Object result = scene.invokeCapability(capabilityId, params);
        
        return CliResult.success(result);
    }
    
    /**
     * 发布场景事件
     */
    public CliResult publishEvent(CliContext context) {
        String sceneId = context.getParameter("scene-id");
        String eventType = context.getParameter("event-type");
        Map<String, Object> data = extractParams(context);
        
        // 1. 获取场景上下文
        SceneContext scene = sceneContextApi.getScene(sceneId);
        
        // 2. 发布事件
        scene.publishEvent(eventType, data);
        
        return CliResult.success("Event published: " + eventType);
    }
}
```

---

## 5. 命令透传安全

### 5.1 安全风险分析

```
风险场景：
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  攻击者  │────▶│  CLI    │────▶│ 透传给  │────▶│ 被操控的 │
│         │     │  入口   │     │ Skill   │     │  Skill  │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
     │                              │
     │                              ▼
     │                        权限提升攻击
     │                        命令注入攻击
     │
     └─▶ 输入：skill exec rag-skill "rm -rf /"
```

### 5.2 安全防护机制

```java
/**
 * 安全命令代理
 */
@Component
public class SecureCommandProxy {
    
    /**
     * 命令白名单
     */
    private static final Set<String> COMMAND_WHITELIST = Set.of(
        // rag-skill 允许的命令
        "rag-skill:reindex",
        "rag-skill:search",
        "rag-skill:upload",
        
        // chart-skill 允许的命令
        "chart-skill:refresh",
        "chart-skill:export",
        
        // db-skill 允许的命令
        "db-skill:migrate",
        "db-skill:backup"
    );
    
    /**
     * 危险字符过滤器
     */
    private static final Pattern DANGEROUS_CHARS = Pattern.compile(
        "[;|&$`\\{}\\[\\]\\(\\)\\*\\?<>]"
    );
    
    /**
     * SQL 注入检测
     */
    private static final Pattern SQL_INJECTION = Pattern.compile(
        "(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION)\\b)|(--|#|/\\*)",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 脚本注入检测
     */
    private static final Pattern SCRIPT_INJECTION = Pattern.compile(
        "<script|javascript:|on\\w+\\s*=",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 执行代理命令
     */
    public CommandResult<?> executeProxy(String commandId, 
                                          Map<String, Object> params,
                                          UserSession user) {
        // 1. 白名单校验
        if (!COMMAND_WHITELIST.contains(commandId)) {
            logSecurityEvent(user, "COMMAND_NOT_IN_WHITELIST", commandId);
            throw new SecurityException("Command not in whitelist: " + commandId);
        }
        
        // 2. 参数过滤
        Map<String, Object> filteredParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // 过滤键名
            if (DANGEROUS_CHARS.matcher(key).find()) {
                logSecurityEvent(user, "DANGEROUS_PARAM_KEY", key);
                throw new SecurityException("Dangerous parameter key: " + key);
            }
            
            // 过滤值
            if (value instanceof String) {
                String strValue = (String) value;
                
                if (SQL_INJECTION.matcher(strValue).find()) {
                    logSecurityEvent(user, "SQL_INJECTION_DETECTED", strValue);
                    throw new SecurityException("SQL injection detected");
                }
                
                if (SCRIPT_INJECTION.matcher(strValue).find()) {
                    logSecurityEvent(user, "SCRIPT_INJECTION_DETECTED", strValue);
                    throw new SecurityException("Script injection detected");
                }
                
                filteredParams.put(key, strValue);
            } else {
                filteredParams.put(key, value);
            }
        }
        
        // 3. 权限映射校验
        if (!hasProxyPermission(user, commandId)) {
            logSecurityEvent(user, "PERMISSION_DENIED", commandId);
            throw new SecurityException("No proxy permission for: " + commandId);
        }
        
        // 4. 审计记录
        auditLog.record(AuditEvent.builder()
            .userId(user.getUserId())
            .action("PROXY_COMMAND")
            .resource(commandId)
            .parameters(filteredParams)
            .timestamp(System.currentTimeMillis())
            .build()
        );
        
        // 5. 执行代理
        return execute(commandId, filteredParams);
    }
    
    /**
     * 检查代理权限
     */
    private boolean hasProxyPermission(UserSession user, String commandId) {
        // 解析 Skill ID
        String skillId = commandId.split(":")[0];
        
        // 检查用户是否有该 Skill 的执行权限
        String permission = skillId + ":execute";
        return user.getPermissions().contains(permission) ||
               user.getPermissions().contains("skill:execute:*");
    }
    
    /**
     * 记录安全事件
     */
    private void logSecurityEvent(UserSession user, String eventType, String detail) {
        log.warn("[SECURITY] User: {}, Event: {}, Detail: {}", 
            user != null ? user.getUserId() : "anonymous",
            eventType, 
            detail
        );
    }
}
```

### 5.3 权限控制矩阵

| 角色 | 核心命令 | NLP 命令 | LLM 命令 | 场景命令 | 扩展命令 |
|------|----------|----------|----------|----------|----------|
| installer | ✅ 全部 | ❌ | ❌ | ❌ | ❌ |
| admin | ✅ 全部 | ✅ 全部 | ✅ 全部 | ✅ 全部 | ✅ 白名单内 |
| leader | ✅ view | ✅ 查询 | ✅ 生成 | ✅ 全部 | ✅ 白名单内 |
| collaborator | ✅ view | ✅ 查询 | ✅ 生成 | ✅ view/invoke | ❌ |

---

## 6. 实现示例

### 6.1 完整 CLI 命令示例

```java
/**
 * Skill 安装命令
 */
@Command(name = "install", description = "Install a skill")
public class InstallCommand implements CliCommand {
    
    @Parameters(paramLabel = "<skill-id>", description = "Skill ID or path")
    private String skillId;
    
    @Option(names = {"--version"}, description = "Skill version")
    private String version;
    
    @Option(names = {"--source"}, description = "Source: central, local, git")
    private String source = "central";
    
    @Autowired
    private PluginManager pluginManager;
    
    @Autowired
    private CommandExecutor commandExecutor;
    
    @Override
    public String getName() {
        return "install";
    }
    
    @Override
    public String getDescription() {
        return "Install a skill from various sources";
    }
    
    @Override
    public String getUsage() {
        return "skill install <skill-id> [--version=<version>] [--source=<source>]";
    }
    
    @Override
    public List<String> getRequiredPermissions() {
        return List.of("skill:install");
    }
    
    @Override
    public CliResult execute(CliContext context) {
        try {
            // 1. 构建安装命令
            InstallCommandRequest request = InstallCommandRequest.builder()
                .skillId(skillId)
                .version(version)
                .source(source)
                .build();
            
            // 2. 转换为 Agent SDK Command
            Command<InstallResult> command = new InstallSkillCommand(request);
            
            // 3. 构建上下文
            CommandContext commandContext = CommandContext.builder()
                .caller(CallerInfo.from(context.getUserSession()))
                .parameters(Map.of("request", request))
                .build();
            
            // 4. 执行（安装通常是异步的）
            TaskId taskId = commandExecutor.executeAsync(command, commandContext);
            
            // 5. 返回结果
            return CliResult.builder()
                .exitCode(0)
                .output(formatOutput("Installation started", Map.of(
                    "taskId", taskId,
                    "skillId", skillId,
                    "status", "INSTALLING"
                ), context.getOutputFormat()))
                .taskId(taskId.toString())
                .build();
                
        } catch (Exception e) {
            return CliResult.error("Installation failed: " + e.getMessage());
        }
    }
}
```

### 6.2 交互式模式示例

```java
/**
 * 交互式 CLI
 */
@Component
public class InteractiveCli {
    
    @Autowired
    private CliCommandRegistry commandRegistry;
    
    @Autowired
    private LineReader lineReader;
    
    public void start() {
        printWelcome();
        
        while (true) {
            try {
                String input = lineReader.readLine("skill> ");
                
                if (input == null || input.trim().isEmpty()) {
                    continue;
                }
                
                if ("exit".equalsIgnoreCase(input.trim()) || 
                    "quit".equalsIgnoreCase(input.trim())) {
                    break;
                }
                
                if ("help".equalsIgnoreCase(input.trim())) {
                    printHelp();
                    continue;
                }
                
                // 解析并执行命令
                String[] args = parseArgs(input);
                CliResult result = executeCommand(args);
                
                // 显示结果
                if (result.isSuccess()) {
                    System.out.println(result.getOutput());
                } else {
                    System.err.println(result.getErrorOutput());
                }
                
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        
        System.out.println("Goodbye!");
    }
    
    private void printWelcome() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     Ooder Skills CLI v3.0.5          ║");
        System.out.println("║     Type 'help' for commands         ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
```

---

## 下一册预告

**第五册：安全与权限**

将深入探讨：
- 完整的权限模型
- 命令透传安全机制
- 审计与监控
- 安全最佳实践

请继续阅读第五册了解安全设计。
