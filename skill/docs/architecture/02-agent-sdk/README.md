# 第二册：Agent SDK 深度解析

## 目录

1. [Agent SDK 架构定位](#1-agent-sdk-架构定位)
2. [Command 体系详解](#2-command-体系详解)
3. [南向协议实现](#3-南向协议实现)
4. [多任务与异步机制](#4-多任务与异步机制)
5. [与 CLI 的集成](#5-与-cli-的集成)
6. [调用链路图](#6-调用链路图)

---

## 1. Agent SDK 架构定位

### 1.1 在整体架构中的位置

```
┌─────────────────────────────────────────┐
│           用户交互层                      │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │   CLI   │ │llm-chat │ │ Web UI  │   │
│  └────┬────┘ └────┬────┘ └────┬────┘   │
└───────┼───────────┼───────────┼────────┘
        │           │           │
        └───────────┴───────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           Agent SDK 协议层               │
│  ┌─────────────────────────────────┐   │
│  │  - 无状态原子操作                 │   │
│  │  - 命令通道 (Command Channel)    │   │
│  │  - 异步任务队列                   │   │
│  │  - 多活支持                       │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           SceneEngine 场景层             │
│  ┌─────────────────────────────────┐   │
│  │  - 有状态场景管理                 │   │
│  │  - 场景编排                       │   │
│  │  - 事件驱动                       │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### 1.2 核心职责

| 职责 | 说明 | 实现方式 |
|------|------|----------|
| 协议封装 | 将上层请求转换为标准协议 | Command 接口 |
| 原子操作 | 保证操作的原子性 | 无状态设计 |
| 异步支持 | 支持长时间运行的任务 | Task Queue |
| 多活部署 | 支持多节点部署 | 无状态 + 共享存储 |

---

## 2. Command 体系详解

### 2.1 Command 核心接口

基于代码分析，Agent SDK 的 Command 体系设计如下：

```java
/**
 * Command 接口 - Agent SDK 的核心抽象
 * 所有可执行操作都实现此接口
 */
public interface Command<R> {
    
    /**
     * 命令唯一标识
     */
    String getCommandId();
    
    /**
     * 命令类型
     */
    CommandType getType();
    
    /**
     * 执行命令
     * @param context 执行上下文
     * @return 执行结果
     */
    R execute(CommandContext context);
    
    /**
     * 是否支持异步执行
     */
    default boolean isAsync() {
        return false;
    }
    
    /**
     * 获取超时时间（毫秒）
     */
    default long getTimeout() {
        return 30000L;
    }
    
    /**
     * 获取重试策略
     */
    default RetryPolicy getRetryPolicy() {
        return RetryPolicy.NONE;
    }
}

/**
 * 命令类型枚举
 */
public enum CommandType {
    SYNC,       // 同步执行
    ASYNC,      // 异步执行
    DEFERRED,   // 延迟执行
    PIPELINE,   // 管道执行（多个命令串联）
    SAGA        // Saga 事务（分布式事务）
}

/**
 * 命令上下文
 */
public interface CommandContext {
    
    /**
     * 获取调用者信息
     */
    CallerInfo getCaller();
    
    /**
     * 获取场景上下文（如果有）
     */
    SceneContext getSceneContext();
    
    /**
     * 获取参数
     */
    Map<String, Object> getParameters();
    
    /**
     * 获取任务存储（用于异步任务）
     */
    TaskStorage getTaskStorage();
    
    /**
     * 获取消息服务
     */
    MessagingService getMessagingService();
}
```

### 2.2 Command 注册机制

```java
/**
 * Command 注册中心
 */
public interface CommandRegistry {
    
    /**
     * 注册命令
     */
    void register(Command<?> command);
    
    /**
     * 注册命令（带权限）
     */
    void register(Command<?> command, List<String> requiredPermissions);
    
    /**
     * 根据 ID 获取命令
     */
    Command<?> get(String commandId);
    
    /**
     * 获取所有命令
     */
    List<CommandInfo> list();
    
    /**
     * 注销命令
     */
    void unregister(String commandId);
}

/**
 * 命令信息
 */
public interface CommandInfo {
    String getCommandId();
    String getDescription();
    List<String> getRequiredPermissions();
    CommandType getType();
    Map<String, ParamInfo> getParameters();
}
```

### 2.3 Command 执行器

```java
/**
 * Command 执行器
 */
public interface CommandExecutor {
    
    /**
     * 执行命令
     */
    <R> CommandResult<R> execute(Command<R> command, CommandContext context);
    
    /**
     * 异步执行命令
     */
    <R> TaskId executeAsync(Command<R> command, CommandContext context);
    
    /**
     * 批量执行命令（管道）
     */
    <R> CommandResult<R> executePipeline(List<Command<?>> commands, CommandContext context);
    
    /**
     * 执行 Saga 事务
     */
    <R> CommandResult<R> executeSaga(SagaDefinition saga, CommandContext context);
}

/**
 * 命令结果
 */
public interface CommandResult<R> {
    boolean isSuccess();
    R getData();
    String getErrorCode();
    String getErrorMessage();
    
    /**
     * 如果是异步任务，返回任务 ID
     */
    String getTaskId();
    
    /**
     * 执行耗时（毫秒）
     */
    long getExecutionTime();
}
```

---

## 3. 南向协议实现

### 3.1 协议分层

```
┌─────────────────────────────────────────┐
│           应用层 (Application)           │
│  - Command 定义                          │
│  - 业务逻辑                              │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           消息层 (Messaging)             │
│  - UnifiedMessage                        │
│  - 消息序列化/反序列化                    │
│  - 消息路由                              │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           传输层 (Transport)             │
│  - WebSocket                             │
│  - HTTP/REST                             │
│  - 消息队列 (可选)                        │
└─────────────────────────────────────────┘
```

### 3.2 消息格式

```java
/**
 * 统一消息格式
 * 基于 skill-spi-messaging 的 UnifiedMessage
 */
public interface UnifiedMessage {
    
    /**
     * 消息唯一标识
     */
    String getMessageId();
    
    /**
     * 消息类型
     */
    MessageType getType();
    
    /**
     * 发送者
     */
    Participant getSender();
    
    /**
     * 接收者
     */
    List<Participant> getRecipients();
    
    /**
     * 消息内容
     */
    MessageContent getContent();
    
    /**
     * 消息状态
     */
    MessageStatus getStatus();
    
    /**
     * 时间戳
     */
    long getTimestamp();
}

/**
 * 命令消息内容
 */
public interface CommandMessageContent extends MessageContent {
    
    /**
     * 命令 ID
     */
    String getCommandId();
    
    /**
     * 命令参数
     */
    Map<String, Object> getParameters();
    
    /**
     * 执行上下文
     */
    Map<String, Object> getContext();
}
```

### 3.3 协议特性

| 特性 | 说明 | 实现 |
|------|------|------|
| 无状态 | 协议层不保存业务状态 | 每个请求独立处理 |
| 幂等性 | 支持重试不导致副作用 | 命令 ID 去重 |
| 异步化 | 支持长时间运行任务 | Task Queue |
| 可观测 | 支持追踪和监控 | 消息 ID 全链路传递 |

---

## 4. 多任务与异步机制

### 4.1 任务队列设计

```java
/**
 * 任务队列接口
 */
public interface TaskQueue {
    
    /**
     * 提交任务
     */
    TaskId submit(Task task);
    
    /**
     * 提交延迟任务
     */
    TaskId submitDelayed(Task task, Duration delay);
    
    /**
     * 提交定时任务
     */
    TaskId submitScheduled(Task task, CronExpression cron);
    
    /**
     * 取消任务
     */
    boolean cancel(TaskId taskId);
    
    /**
     * 获取任务状态
     */
    TaskStatus getStatus(TaskId taskId);
    
    /**
     * 获取任务结果
     */
    TaskResult getResult(TaskId taskId);
    
    /**
     * 列出任务
     */
    List<TaskInfo> list(TaskFilter filter);
}

/**
 * 任务定义
 */
public interface Task {
    
    TaskId getTaskId();
    
    String getCommandId();
    
    CommandContext getContext();
    
    /**
     * 优先级（0-100，数字越大优先级越高）
     */
    int getPriority();
    
    /**
     * 重试次数
     */
    int getMaxRetries();
    
    /**
     * 超时时间
     */
    Duration getTimeout();
    
    /**
     * 任务状态变更回调
     */
    TaskStatusListener getStatusListener();
}
```

### 4.2 异步执行流程

```
┌─────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  调用方  │────▶│ Command     │────▶│   Task      │────▶│   Task      │
│         │     │ Executor    │     │   Queue     │     │   Worker    │
└─────────┘     └─────────────┘     └─────────────┘     └──────┬──────┘
     │                                                           │
     │                                                           ▼
     │                                                    ┌─────────────┐
     │                                                    │   执行      │
     │                                                    │  Command    │
     │                                                    └──────┬──────┘
     │                                                           │
     │                                                           ▼
     │                                                    ┌─────────────┐
     │                                                    │  更新状态    │
     │                                                    │  (回调/轮询) │
     │                                                    └──────┬──────┘
     │                                                           │
     │◀──────────────────────────────────────────────────────────┘
     │                    返回结果
     ▼
┌─────────┐
│  获取   │
│  结果   │
└─────────┘
```

### 4.3 多活支持

```java
/**
 * 多活配置
 */
public interface MultiActiveConfig {
    
    /**
     * 节点标识
     */
    String getNodeId();
    
    /**
     * 是否主节点
     */
    boolean isPrimary();
    
    /**
     * 任务分配策略
     */
    TaskAllocationStrategy getAllocationStrategy();
    
    /**
     * 故障转移策略
     */
    FailoverStrategy getFailoverStrategy();
}

/**
 * 任务分配策略
 */
public enum TaskAllocationStrategy {
    ROUND_ROBIN,    // 轮询
    RANDOM,         // 随机
    HASH,           // 哈希（根据任务 ID）
    LEAST_LOADED,   // 最少负载
    CAPABILITY_BASED // 基于能力匹配
}
```

---

## 5. 与 CLI 的集成

### 5.1 CLI 命令到 Agent SDK Command 的转换

```java
/**
 * CLI 命令适配器
 */
public class CliCommandAdapter {
    
    @Autowired
    private CommandRegistry commandRegistry;
    
    @Autowired
    private CommandExecutor commandExecutor;
    
    /**
     * 将 CLI 输入转换为 Command 并执行
     */
    public CliResult execute(String[] args, UserSession user) {
        // 1. 解析 CLI 输入
        CliCommand cliCommand = parseCliInput(args);
        
        // 2. 查找对应的 Agent SDK Command
        Command<?> command = commandRegistry.get(cliCommand.getCommandId());
        if (command == null) {
            return CliResult.error("Unknown command: " + cliCommand.getCommandId());
        }
        
        // 3. 构建 CommandContext
        CommandContext context = CommandContext.builder()
            .caller(CallerInfo.from(user))
            .parameters(cliCommand.getParameters())
            .build();
        
        // 4. 权限校验
        if (!hasPermission(user, command)) {
            return CliResult.error("Permission denied");
        }
        
        // 5. 执行命令
        if (command.isAsync()) {
            // 异步执行
            TaskId taskId = commandExecutor.executeAsync(command, context);
            return CliResult.async(taskId);
        } else {
            // 同步执行
            CommandResult<?> result = commandExecutor.execute(command, context);
            return CliResult.from(result);
        }
    }
}
```

### 5.2 命令透传安全

```java
/**
 * 安全命令代理
 */
public class SecureCommandProxy {
    
    /**
     * 白名单机制 - 只允许特定的命令透传
     */
    private final Set<String> commandWhitelist = Set.of(
        "rag-skill:reindex",
        "rag-skill:search",
        "chart-skill:refresh",
        "db-skill:migrate"
    );
    
    /**
     * 参数过滤器 - 过滤危险参数
     */
    private final Map<String, ParamFilter> paramFilters = Map.of(
        "command", new DangerousCharFilter(),
        "script", new ScriptInjectionFilter(),
        "sql", new SqlInjectionFilter()
    );
    
    public CommandResult<?> executeProxy(String commandId, 
                                          Map<String, Object> params,
                                          UserSession user) {
        // 1. 白名单校验
        if (!commandWhitelist.contains(commandId)) {
            throw new SecurityException("Command not in whitelist: " + commandId);
        }
        
        // 2. 参数过滤
        Map<String, Object> filteredParams = filterParams(params);
        
        // 3. 权限映射
        if (!hasProxyPermission(user, commandId)) {
            throw new SecurityException("No proxy permission for: " + commandId);
        }
        
        // 4. 审计记录
        auditLog.record(user, commandId, filteredParams);
        
        // 5. 执行代理
        return execute(commandId, filteredParams);
    }
}
```

---

## 6. 调用链路图

### 6.1 完整调用链路

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLI 调用链路                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 用户输入                                                                 │
│     ┌─────────────────────────────────────────────────────────────────┐    │
│     │ $ skill exec rag-skill reindex --knowledgeBase=docs             │    │
│     └─────────────────────────────────────────────────────────────────┘    │
│                              │                                              │
│                              ▼                                              │
│  2. CLI 解析                                                                 │
│     ┌─────────────────────────────────────────────────────────────────┐    │
│     │ Command: exec                                                   │    │
│     │ Target: rag-skill                                               │    │
│     │ SubCommand: reindex                                             │    │
│     │ Params: {knowledgeBase=docs}                                    │    │
│     └─────────────────────────────────────────────────────────────────┘    │
│                              │                                              │
│                              ▼                                              │
│  3. 权限校验 (Layer 1)                                                       │
│     ┌─────────────────────────────────────────────────────────────────┐    │
│     │ User: admin                                                     │    │
│     │ Permissions: [skill:exec, rag-skill:reindex]                    │    │
│     │ Check: ✅ PASSED                                               │    │
│     └─────────────────────────────────────────────────────────────────┘    │
│                              │                                              │
│                              ▼                                              │
│  4. 命令转换                                                                 │
│     ┌─────────────────────────────────────────────────────────────────┐    │
│     │ CLI Command ──▶ Agent SDK Command                              │    │
│     │ exec rag-skill:reindex {knowledgeBase=docs}                     │    │
│     └─────────────────────────────────────────────────────────────────┘    │
│                              │                                              │
│                              ▼                                              │
│  5. Agent SDK 处理                                                           │
│     ┌─────────────────────────────────────────────────────────────────┐    │
│     │ CommandRegistry.get("rag-skill:reindex")                        │    │
│     │ CommandExecutor.execute()                                       │    │
│     │ Type: ASYNC                                                    │    │
│     └─────────────────────────────────────────────────────────────────┘    │
│                              │                                              │
│                              ▼                                              │
│  6. 任务队列                                                                 │
│     ┌─────────────────────────────────────────────────────────────────┐    │
│     │ TaskQueue.submit(task)                                          │    │
│     │ Task ID: task-xxx-xxx                                           │    │
│     │ Status: PENDING                                                │    │
│     └─────────────────────────────────────────────────────────────────┘    │
│                              │                                              │
│                              ▼                                              │
│  7. 异步执行                                                                 │
│     ┌─────────────────────────────────────────────────────────────────┐    │
│     │ TaskWorker.execute()                                            │    │
│     │ Status: RUNNING                                                │    │
│     │ ......                                                         │    │
│     │ Status: COMPLETED                                              │    │
│     └─────────────────────────────────────────────────────────────────┘    │
│                              │                                              │
│                              ▼                                              │
│  8. 结果返回                                                                 │
│     ┌─────────────────────────────────────────────────────────────────┐    │
│     │ CLI Result:                                                     │    │
│     │ {                                                               │    │
│     │   "taskId": "task-xxx-xxx",                                     │    │
│     │   "status": "COMPLETED",                                        │    │
│     │   "message": "Index rebuilt successfully"                       │    │
│     │ }                                                               │    │
│     └─────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 关键扩展点

| 扩展点 | 接口 | 说明 |
|--------|------|------|
| 自定义 Command | `Command<R>` | 实现业务逻辑 |
| 自定义过滤器 | `ParamFilter` | 参数安全检查 |
| 任务监听器 | `TaskStatusListener` | 监控任务状态 |
| 分配策略 | `TaskAllocationStrategy` | 多活任务分配 |

---

## 下一册预告

**第三册：SceneEngine 场景引擎**

将深入探讨：
- 场景生命周期状态机
- 场景编排与协调能力
- 事件驱动架构实现
- 与 Agent SDK 的集成

请继续阅读第三册了解场景层的设计实现。
