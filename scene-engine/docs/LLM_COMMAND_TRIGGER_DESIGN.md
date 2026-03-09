# LLM Agent 命令触发激活设计

**版本**: v2.4.0  
**日期**: 2026-03-07  
**状态**: 架构设计

---

## 一、设计概述

### 1.1 核心原则

| 原则 | 说明 |
|------|------|
| **命令触发** | LLM 通过命令激活，而非直接调用 |
| **沙箱隔离** | LLM 完全隔离在独立的 Agent 沙箱内 |
| **独立调度** | 内部独立完成任务调度和环境管理 |
| **统一协议** | 外部调用统一使用 Agent 命令协议 |

### 1.2 架构概览

```
┌─────────────────────────────────────────────────────────────┐
│  LLM Agent 命令触发架构                                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  外部调用层                                          │   │
│  │  ├── 业务系统                                        │   │
│  │  ├── 用户界面                                        │   │
│  │  └── 其他 Agent                                      │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  MCP Agent (主控智能体)                              │   │
│  │  ├── 命令路由                                        │   │
│  │  ├── 权限验证                                        │   │
│  │  └── 会话管理                                        │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼ Agent-Command 协议               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  End Agent (终端智能体)                              │   │
│  │  ┌─────────────────────────────────────────────┐   │   │
│  │  │  LLM Sandbox (LLM 沙箱)                      │   │   │
│  │  │  ├── LLM Runtime (运行时)                    │   │   │
│  │  │  ├── Task Scheduler (任务调度)               │   │   │
│  │  │  ├── Environment (环境管理)                  │   │   │
│  │  │  └── LLM-CAP (能力端点)                      │   │   │
│  │  └─────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、LLM Agent 沙箱设计

### 2.1 沙箱架构

```
┌─────────────────────────────────────────────────────────────┐
│  LLM Sandbox (LLM 沙箱)                                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  沙箱边界 (Sandbox Boundary)                         │   │
│  │  ├── 网络隔离：仅允许 Agent-Command 通道              │   │
│  │  ├── 文件隔离：独立文件系统空间                       │   │
│  │  ├── 内存隔离：独立内存空间                           │   │
│  │  └── 进程隔离：独立进程空间                           │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  LLM Runtime (运行时)                                │   │
│  │  ├── Model Loader (模型加载器)                       │   │
│  │  ├── Inference Engine (推理引擎)                     │   │
│  │  ├── Context Manager (上下文管理)                    │   │
│  │  └── Token Counter (Token 计数器)                    │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Task Scheduler (任务调度)                           │   │
│  │  ├── Task Queue (任务队列)                           │   │
│  │  ├── Priority Manager (优先级管理)                   │   │
│  │  ├── Timeout Handler (超时处理)                      │   │
│  │  └── Retry Manager (重试管理)                        │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Environment (环境管理)                              │   │
│  │  ├── Session State (会话状态)                        │   │
│  │  ├── User Context (用户上下文)                       │   │
│  │  ├── Knowledge Access (知识访问)                     │   │
│  │  └── Tool Registry (工具注册)                        │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  LLM-CAP Endpoints (能力端点)                        │   │
│  │  ├── /llm/chat (对话端点)                            │   │
│  │  ├── /llm/complete (补全端点)                        │   │
│  │  ├── /llm/embed (向量化端点)                         │   │
│  │  └── /llm/function (函数调用端点)                    │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 沙箱接口定义

```java
/**
 * LLM 沙箱接口
 */
public interface LlmSandbox {
    
    /**
     * 初始化沙箱
     */
    void initialize(SandboxConfig config);
    
    /**
     * 启动沙箱
     */
    void start();
    
    /**
     * 停止沙箱
     */
    void stop();
    
    /**
     * 获取沙箱状态
     */
    SandboxStatus getStatus();
    
    /**
     * 执行命令
     */
    SandboxResult execute(LlmCommand command);
    
    /**
     * 获取能力端点
     */
    LlmCapEndpoint getEndpoint(String capName);
    
    /**
     * 获取环境信息
     */
    EnvironmentInfo getEnvironment();
}

/**
 * 沙箱配置
 */
@Data
@Builder
public class SandboxConfig {
    
    private String sandboxId;
    private String sceneId;
    private String llmModelId;
    private long memoryLimit;
    private long tokenLimit;
    private int maxConcurrentTasks;
    private long taskTimeout;
    private List<String> allowedTools;
    private List<String> allowedKnowledgeBases;
    private SecurityPolicy securityPolicy;
}

/**
 * 沙箱状态
 */
@Data
@Builder
public class SandboxStatus {
    
    private String sandboxId;
    private SandboxState state;
    private int activeTasks;
    private int queuedTasks;
    private long memoryUsed;
    private long tokensUsed;
    private LocalDateTime lastActivity;
    
    public enum SandboxState {
        INITIALIZING,
        RUNNING,
        PAUSED,
        STOPPED,
        ERROR
    }
}
```

### 2.3 沙箱实现

```java
/**
 * LLM 沙箱实现
 */
@Component
public class LlmSandboxImpl implements LlmSandbox {
    
    private final SandboxConfig config;
    private final LlmRuntime runtime;
    private final TaskScheduler scheduler;
    private final EnvironmentManager environment;
    private final Map<String, LlmCapEndpoint> endpoints;
    
    private SandboxStatus status;
    
    @Override
    public void initialize(SandboxConfig config) {
        this.config = config;
        
        // 1. 初始化运行时
        runtime.initialize(config.getLlmModelId());
        
        // 2. 初始化任务调度器
        scheduler.initialize(config.getMaxConcurrentTasks(), config.getTaskTimeout());
        
        // 3. 初始化环境
        environment.initialize(config);
        
        // 4. 注册能力端点
        registerEndpoints();
        
        // 5. 更新状态
        status = SandboxStatus.builder()
            .sandboxId(config.getSandboxId())
            .state(SandboxState.INITIALIZING)
            .build();
    }
    
    @Override
    public void start() {
        runtime.start();
        scheduler.start();
        status.setState(SandboxState.RUNNING);
    }
    
    @Override
    public SandboxResult execute(LlmCommand command) {
        // 1. 验证命令
        validateCommand(command);
        
        // 2. 创建任务
        LlmTask task = createTask(command);
        
        // 3. 提交到调度器
        Future<SandboxResult> future = scheduler.submit(task);
        
        // 4. 等待结果
        try {
            return future.get(config.getTaskTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return SandboxResult.timeout(command.getCommandId());
        } catch (Exception e) {
            return SandboxResult.error(command.getCommandId(), e.getMessage());
        }
    }
    
    private void registerEndpoints() {
        endpoints.put("chat", new ChatEndpoint(runtime, environment));
        endpoints.put("complete", new CompleteEndpoint(runtime, environment));
        endpoints.put("embed", new EmbedEndpoint(runtime, environment));
        endpoints.put("function", new FunctionEndpoint(runtime, environment, config.getAllowedTools()));
    }
}
```

---

## 三、A2A 命令体系设计

### 3.1 命令类型定义

```java
/**
 * A2A 命令类型
 */
public enum A2ACommandType {
    
    // === LLM 激活命令 ===
    LLM_ACTIVATE("llm.activate", "激活 LLM 沙箱"),
    LLM_DEACTIVATE("llm.deactivate", "停用 LLM 沙箱"),
    LLM_CHAT("llm.chat", "LLM 对话命令"),
    LLM_COMPLETE("llm.complete", "LLM 补全命令"),
    LLM_EMBED("llm.embed", "LLM 向量化命令"),
    LLM_FUNCTION("llm.function", "LLM 函数调用命令"),
    
    // === 任务调度命令 ===
    TASK_SUBMIT("task.submit", "提交任务"),
    TASK_CANCEL("task.cancel", "取消任务"),
    TASK_STATUS("task.status", "查询任务状态"),
    TASK_RESULT("task.result", "获取任务结果"),
    
    // === 环境管理命令 ===
    ENV_SET("env.set", "设置环境变量"),
    ENV_GET("env.get", "获取环境变量"),
    ENV_CLEAR("env.clear", "清除环境变量"),
    CONTEXT_UPDATE("context.update", "更新上下文"),
    
    // === 知识访问命令 ===
    KB_SEARCH("kb.search", "知识库检索"),
    KB_INDEX("kb.index", "知识库索引"),
    KB_DELETE("kb.delete", "知识库删除"),
    
    // === 工具调用命令 ===
    TOOL_INVOKE("tool.invoke", "工具调用"),
    TOOL_REGISTER("tool.register", "工具注册"),
    TOOL_UNREGISTER("tool.unregister", "工具注销"),
    
    // === 场景交互命令 ===
    SCENE_ENTER("scene.enter", "进入场景"),
    SCENE_EXIT("scene.exit", "退出场景"),
    SCENE_TRANSFER("scene.transfer", "场景数据传输"),
    
    // === 会话管理命令 ===
    SESSION_CREATE("session.create", "创建会话"),
    SESSION_DESTROY("session.destroy", "销毁会话"),
    SESSION_SYNC("session.sync", "同步会话");
    
    private final String code;
    private final String description;
}
```

### 3.2 命令消息结构

```java
/**
 * A2A 命令消息
 */
@Data
@Builder
public class A2ACommand {
    
    /**
     * 命令头
     */
    private CommandHeader header;
    
    /**
     * 命令体
     */
    private CommandBody body;
    
    /**
     * 命令元数据
     */
    private CommandMetadata metadata;
    
    /**
     * 安全信息
     */
    private SecurityInfo security;
    
    @Data
    @Builder
    public static class CommandHeader {
        
        /**
         * 协议版本
         */
        private String protocolVersion;
        
        /**
         * 命令类型
         */
        private A2ACommandType commandType;
        
        /**
         * 命令ID
         */
        private String commandId;
        
        /**
         * 时间戳
         */
        private long timestamp;
        
        /**
         * 追踪ID
         */
        private String traceId;
    }
    
    @Data
    @Builder
    public static class CommandBody {
        
        /**
         * 来源 Agent
         */
        private AgentInfo source;
        
        /**
         * 目标 Agent
         */
        private AgentInfo target;
        
        /**
         * 目标端点 (LLM-CAP)
         */
        private String targetEndpoint;
        
        /**
         * 操作参数
         */
        private Map<String, Object> params;
        
        /**
         * 载荷数据
         */
        private Object payload;
    }
    
    @Data
    @Builder
    public static class CommandMetadata {
        
        /**
         * 优先级
         */
        private Priority priority;
        
        /**
         * 超时时间
         */
        private long timeoutMs;
        
        /**
         * 重试次数
         */
        private int retryCount;
        
        /**
         * 是否需要确认
         */
        private boolean requireAck;
        
        /**
         * 回调地址
         */
        private String callbackUrl;
        
        public enum Priority {
            LOW, MEDIUM, HIGH, CRITICAL
        }
    }
    
    @Data
    @Builder
    public static class SecurityInfo {
        
        /**
         * 用户令牌
         */
        private String userToken;
        
        /**
         * LLM 令牌
         */
        private String llmToken;
        
        /**
         * 会话ID
         */
        private String sessionId;
        
        /**
         * 安全级别
         */
        private String securityLevel;
        
        /**
         * 数字签名
         */
        private String signature;
    }
    
    @Data
    @Builder
    public static class AgentInfo {
        
        private String agentId;
        private String agentType;
        private String sceneId;
        private String endpoint;
    }
}
```

### 3.3 命令响应结构

```java
/**
 * A2A 命令响应
 */
@Data
@Builder
public class A2ACommandResponse {
    
    /**
     * 响应头
     */
    private ResponseHeader header;
    
    /**
     * 响应体
     */
    private ResponseBody body;
    
    @Data
    @Builder
    public static class ResponseHeader {
        
        /**
         * 原命令ID
         */
        private String commandId;
        
        /**
         * 响应ID
         */
        private String responseId;
        
        /**
         * 时间戳
         */
        private long timestamp;
        
        /**
         * 响应状态
         */
        private ResponseStatus status;
        
        /**
         * 错误信息
         */
        private String errorMessage;
        
        /**
         * 错误码
         */
        private String errorCode;
        
        public enum ResponseStatus {
            SUCCESS,
            FAILED,
            TIMEOUT,
            REJECTED,
            PENDING
        }
    }
    
    @Data
    @Builder
    public static class ResponseBody {
        
        /**
         * 结果数据
         */
        private Object result;
        
        /**
         * 元数据
         */
        private Map<String, Object> metadata;
        
        /**
         * Token 使用统计
         */
        private TokenUsage tokenUsage;
    }
    
    @Data
    @Builder
    public static class TokenUsage {
        
        private long promptTokens;
        private long completionTokens;
        private long totalTokens;
    }
}
```

---

## 四、命令触发流程

### 4.1 LLM 激活流程

```
┌─────────────────────────────────────────────────────────────┐
│  LLM 激活命令流程                                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  业务系统                                                    │
│  │                                                          │
│  │ 1. 构建激活命令                                          │
│  ▼                                                          │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  A2ACommand: LLM_ACTIVATE                            │   │
│  │  {                                                   │   │
│  │    "header": {                                       │   │
│  │      "commandType": "llm.activate",                  │   │
│  │      "commandId": "cmd-001"                          │   │
│  │    },                                                │   │
│  │    "body": {                                         │   │
│  │      "target": {                                     │   │
│  │        "agentId": "agent-approval",                  │   │
│  │        "sceneId": "approval-scene",                  │   │
│  │        "endpoint": "llm-cap-approval"                │   │
│  │      },                                              │   │
│  │      "params": {                                     │   │
│  │        "modelId": "gpt-4",                           │   │
│  │        "taskType": "approval_analysis"               │   │
│  │      }                                               │   │
│  │    }                                                 │   │
│  │  }                                                   │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  MCP Agent (主控智能体)                              │   │
│  │  ├── 验证命令权限                                    │   │
│  │  ├── 解析目标 Agent                                  │   │
│  │  └── 路由命令                                        │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼ Agent-Command 协议               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  End Agent (审批场景)                                │   │
│  │  ┌─────────────────────────────────────────────┐   │   │
│  │  │  LLM Sandbox                                 │   │   │
│  │  │  ├── 接收命令                                │   │   │
│  │  │  ├── 激活 LLM Runtime                       │   │   │
│  │  │  ├── 加载审批场景环境                        │   │   │
│  │  │  └── 返回激活状态                            │   │   │
│  │  └─────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  A2ACommandResponse                                  │   │
│  │  {                                                   │   │
│  │    "header": {                                       │   │
│  │      "commandId": "cmd-001",                         │   │
│  │      "status": "SUCCESS"                             │   │
│  │    },                                                │   │
│  │    "body": {                                         │   │
│  │      "result": {                                     │   │
│  │        "sandboxId": "sandbox-approval-001",          │   │
│  │        "status": "RUNNING",                          │   │
│  │        "endpoint": "llm-cap-approval"                │   │
│  │      }                                               │   │
│  │    }                                                 │   │
│  │  }                                                   │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 命令路由实现

```java
/**
 * MCP Agent 命令路由器
 */
@Component
public class McpCommandRouter {
    
    private final Map<String, EndAgentProxy> agentProxies;
    private final CommandValidator validator;
    private final AuditLogService auditLogService;
    
    /**
     * 路由命令
     */
    public A2ACommandResponse route(A2ACommand command) {
        // 1. 验证命令
        ValidationResult validation = validator.validate(command);
        if (!validation.isValid()) {
            return A2ACommandResponse.builder()
                .header(A2ACommandResponse.ResponseHeader.builder()
                    .commandId(command.getHeader().getCommandId())
                    .status(A2ACommandResponse.ResponseStatus.REJECTED)
                    .errorMessage(validation.getErrorMessage())
                    .build())
                .build();
        }
        
        // 2. 获取目标 Agent
        String targetAgentId = command.getBody().getTarget().getAgentId();
        EndAgentProxy agentProxy = agentProxies.get(targetAgentId);
        
        if (agentProxy == null) {
            return createErrorResponse(command, "Target agent not found: " + targetAgentId);
        }
        
        // 3. 记录审计日志
        auditLogService.logCommand(command);
        
        // 4. 发送命令到目标 Agent
        try {
            A2ACommandResponse response = agentProxy.sendCommand(command);
            
            // 5. 记录响应日志
            auditLogService.logResponse(command, response);
            
            return response;
            
        } catch (Exception e) {
            return createErrorResponse(command, "Command execution failed: " + e.getMessage());
        }
    }
    
    /**
     * 广播命令到多个 Agent
     */
    public List<A2ACommandResponse> broadcast(
            A2ACommand command, 
            List<String> targetAgentIds) {
        
        return targetAgentIds.parallelStream()
            .map(agentId -> {
                A2ACommand clonedCommand = cloneCommand(command, agentId);
                return route(clonedCommand);
            })
            .collect(Collectors.toList());
    }
}
```

### 4.3 End Agent 命令处理器

```java
/**
 * End Agent 命令处理器
 */
@Component
public class EndAgentCommandHandler {
    
    private final Map<String, LlmSandbox> sandboxes;
    private final CommandDispatcher dispatcher;
    
    /**
     * 处理命令
     */
    public A2ACommandResponse handle(A2ACommand command) {
        A2ACommandType commandType = command.getHeader().getCommandType();
        
        // 根据命令类型分发
        switch (commandType) {
            case LLM_ACTIVATE:
                return handleActivate(command);
            case LLM_DEACTIVATE:
                return handleDeactivate(command);
            case LLM_CHAT:
                return handleChat(command);
            case LLM_COMPLETE:
                return handleComplete(command);
            case LLM_EMBED:
                return handleEmbed(command);
            case LLM_FUNCTION:
                return handleFunction(command);
            case KB_SEARCH:
                return handleKnowledgeSearch(command);
            case TOOL_INVOKE:
                return handleToolInvoke(command);
            default:
                return createErrorResponse(command, "Unknown command type: " + commandType);
        }
    }
    
    /**
     * 处理 LLM 激活命令
     */
    private A2ACommandResponse handleActivate(A2ACommand command) {
        Map<String, Object> params = command.getBody().getParams();
        
        String modelId = (String) params.get("modelId");
        String taskType = (String) params.get("taskType");
        
        // 创建沙箱配置
        SandboxConfig config = SandboxConfig.builder()
            .sandboxId("sandbox-" + UUID.randomUUID().toString())
            .sceneId(command.getBody().getTarget().getSceneId())
            .llmModelId(modelId)
            .memoryLimit(1024 * 1024 * 512)  // 512MB
            .tokenLimit(100000)
            .maxConcurrentTasks(5)
            .taskTimeout(60000)
            .build();
        
        // 创建并启动沙箱
        LlmSandbox sandbox = new LlmSandboxImpl();
        sandbox.initialize(config);
        sandbox.start();
        
        // 注册沙箱
        sandboxes.put(config.getSandboxId(), sandbox);
        
        return A2ACommandResponse.builder()
            .header(A2ACommandResponse.ResponseHeader.builder()
                .commandId(command.getHeader().getCommandId())
                .responseId(UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis())
                .status(A2ACommandResponse.ResponseStatus.SUCCESS)
                .build())
            .body(A2ACommandResponse.ResponseBody.builder()
                .result(Map.of(
                    "sandboxId", config.getSandboxId(),
                    "status", "RUNNING",
                    "endpoint", command.getBody().getTargetEndpoint()
                ))
                .build())
            .build();
    }
    
    /**
     * 处理 LLM 对话命令
     */
    private A2ACommandResponse handleChat(A2ACommand command) {
        String sandboxId = (String) command.getBody().getParams().get("sandboxId");
        LlmSandbox sandbox = sandboxes.get(sandboxId);
        
        if (sandbox == null) {
            return createErrorResponse(command, "Sandbox not found: " + sandboxId);
        }
        
        // 创建 LLM 命令
        LlmCommand llmCommand = LlmCommand.builder()
            .commandId(command.getHeader().getCommandId())
            .type(LlmCommandType.CHAT)
            .params(command.getBody().getParams())
            .build();
        
        // 执行命令
        SandboxResult result = sandbox.execute(llmCommand);
        
        return createResponse(command, result);
    }
}
```

---

## 五、场景示例：简历审批 LLM 激活

### 5.1 场景描述

```
用户在招聘场景发起简历审批请求
    │
    ▼
招聘场景 Agent 发送 LLM_ACTIVATE 命令
    │
    ▼
MCP Agent 路由命令到审批场景 End Agent
    │
    ▼
审批场景 End Agent 激活 LLM 沙箱
    │
    ▼
LLM 沙箱加载审批分析环境
    │
    ▼
LLM 执行审批分析任务
    │
    ▼
返回审批建议
```

### 5.2 代码实现

```java
/**
 * 简历审批服务
 */
@Service
public class ResumeApprovalService {
    
    @Autowired
    private McpCommandRouter commandRouter;
    
    /**
     * 发起审批分析
     */
    public ApprovalAnalysisResult analyzeResume(
            String resumeId,
            DualUserContext userContext) {
        
        // 1. 构建 LLM 激活命令
        A2ACommand activateCommand = A2ACommand.builder()
            .header(A2ACommand.CommandHeader.builder()
                .protocolVersion("2.4")
                .commandType(A2ACommandType.LLM_ACTIVATE)
                .commandId("cmd-" + UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis())
                .traceId(generateTraceId())
                .build())
            .body(A2ACommand.CommandBody.builder()
                .source(A2ACommand.AgentInfo.builder()
                    .agentId("agent-recruitment")
                    .agentType("end")
                    .sceneId("recruitment-scene")
                    .build())
                .target(A2ACommand.AgentInfo.builder()
                    .agentId("agent-approval")
                    .agentType("end")
                    .sceneId("approval-scene")
                    .endpoint("llm-cap-approval")
                    .build())
                .params(Map.of(
                    "modelId", "gpt-4",
                    "taskType", "approval_analysis",
                    "resumeId", resumeId
                ))
                .build())
            .metadata(A2ACommand.CommandMetadata.builder()
                .priority(A2ACommand.CommandMetadata.Priority.HIGH)
                .timeoutMs(60000)
                .requireAck(true)
                .build())
            .security(A2ACommand.SecurityInfo.builder()
                .userToken(userContext.getUser().getToken())
                .llmToken(userContext.getLlmUser().getToken())
                .sessionId(userContext.getSession().getSessionId())
                .securityLevel("HIGH")
                .build())
            .build();
        
        // 2. 发送激活命令
        A2ACommandResponse activateResponse = commandRouter.route(activateCommand);
        
        if (activateResponse.getHeader().getStatus() != ResponseStatus.SUCCESS) {
            throw new ApprovalException("Failed to activate LLM: " + 
                activateResponse.getHeader().getErrorMessage());
        }
        
        // 3. 获取沙箱信息
        Map<String, Object> sandboxInfo = (Map<String, Object>) activateResponse.getBody().getResult();
        String sandboxId = (String) sandboxInfo.get("sandboxId");
        
        // 4. 构建 LLM 对话命令
        A2ACommand chatCommand = A2ACommand.builder()
            .header(A2ACommand.CommandHeader.builder()
                .protocolVersion("2.4")
                .commandType(A2ACommandType.LLM_CHAT)
                .commandId("cmd-" + UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis())
                .traceId(activateCommand.getHeader().getTraceId())
                .build())
            .body(A2ACommand.CommandBody.builder()
                .source(activateCommand.getBody().getSource())
                .target(activateCommand.getBody().getTarget())
                .params(Map.of(
                    "sandboxId", sandboxId,
                    "messages", List.of(
                        Map.of("role", "system", "content", buildSystemPrompt()),
                        Map.of("role", "user", "content", buildUserPrompt(resumeId))
                    )
                ))
                .build())
            .metadata(A2ACommand.CommandMetadata.builder()
                .priority(A2ACommand.CommandMetadata.Priority.HIGH)
                .timeoutMs(30000)
                .build())
            .security(activateCommand.getSecurity())
            .build();
        
        // 5. 发送对话命令
        A2ACommandResponse chatResponse = commandRouter.route(chatCommand);
        
        // 6. 解析结果
        return parseApprovalResult(chatResponse);
    }
    
    /**
     * 构建系统提示
     */
    private String buildSystemPrompt() {
        return """
            你是一个专业的简历审批助手。
            
            你的任务是分析候选人简历，评估其是否符合岗位要求，并给出审批建议。
            
            请从以下维度进行分析：
            1. 技能匹配度
            2. 经验相关性
            3. 教育背景
            4. 综合评价
            
            返回格式：
            {
                "matchScore": 0-100,
                "analysis": "分析说明",
                "recommendation": "APPROVE/REJECT/NEED_MORE_INFO",
                "reasons": ["原因1", "原因2"]
            }
            """;
    }
}
```

### 5.3 命令序列图

```
┌─────────┐    ┌─────────────┐    ┌──────────┐    ┌──────────────┐    ┌────────────┐
│ 业务系统 │    │ MCP Agent   │    │End Agent │    │ LLM Sandbox  │    │ LLM Runtime│
└────┬────┘    └──────┬──────┘    └────┬─────┘    └──────┬───────┘    └──────┬─────┘
     │                │                │                 │                   │
     │ LLM_ACTIVATE   │                │                 │                   │
     │───────────────▶│                │                 │                   │
     │                │                │                 │                   │
     │                │ route command  │                 │                   │
     │                │───────────────▶│                 │                   │
     │                │                │                 │                   │
     │                │                │ create sandbox  │                   │
     │                │                │────────────────▶│                   │
     │                │                │                 │                   │
     │                │                │                 │ initialize        │
     │                │                │                 │──────────────────▶│
     │                │                │                 │                   │
     │                │                │                 │◄──────────────────│
     │                │                │                 │                   │
     │                │                │◄────────────────│                   │
     │                │                │ sandboxId       │                   │
     │                │                │                 │                   │
     │                │◄───────────────│                 │                   │
     │                │ response       │                 │                   │
     │                │                │                 │                   │
     │◄───────────────│                │                 │                   │
     │ sandboxId      │                │                 │                   │
     │                │                │                 │                   │
     │ LLM_CHAT       │                │                 │                   │
     │───────────────▶│                │                 │                   │
     │                │                │                 │                   │
     │                │───────────────▶│                 │                   │
     │                │                │                 │                   │
     │                │                │────────────────▶│                   │
     │                │                │                 │                   │
     │                │                │                 │──────────────────▶│
     │                │                │                 │                   │
     │                │                │                 │◄──────────────────│
     │                │                │                 │ analysis result   │
     │                │                │                 │                   │
     │                │                │◄────────────────│                   │
     │                │                │                 │                   │
     │                │◄───────────────│                 │                   │
     │                │                │                 │                   │
     │◄───────────────│                │                 │                   │
     │ approval result│                │                 │                   │
     │                │                │                 │                   │
```

---

## 六、命令配置规范

### 6.1 命令配置注解

```java
/**
 * LLM 命令端点配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LlmCapEndpoint {
    
    /**
     * 端点名称
     */
    String name();
    
    /**
     * 端点描述
     */
    String description();
    
    /**
     * 支持的命令类型
     */
    A2ACommandType[] supportedCommands();
    
    /**
     * 默认模型
     */
    String defaultModel() default "gpt-3.5-turbo";
    
    /**
     * 超时时间
     */
    long timeoutMs() default 30000;
    
    /**
     * 最大并发数
     */
    int maxConcurrency() default 5;
    
    /**
     * 是否需要激活
     */
    boolean requireActivation() default true;
}

/**
 * 审批分析端点
 */
@Component
@LlmCapEndpoint(
    name = "llm-cap-approval",
    description = "审批分析能力端点",
    supportedCommands = {
        A2ACommandType.LLM_ACTIVATE,
        A2ACommandType.LLM_CHAT,
        A2ACommandType.KB_SEARCH
    },
    defaultModel = "gpt-4",
    timeoutMs = 60000,
    maxConcurrency = 3,
    requireActivation = true
)
public class ApprovalAnalysisEndpoint implements LlmEndpoint {
    
    @Override
    public Object execute(LlmCommand command) {
        // 实现审批分析逻辑
    }
}
```

### 6.2 命令路由配置

```java
/**
 * 命令路由配置
 */
@Configuration
public class CommandRoutingConfig {
    
    @Bean
    public CommandRouteRegistry commandRouteRegistry() {
        CommandRouteRegistry registry = new CommandRouteRegistry();
        
        // 注册场景到 Agent 的映射
        registry.registerSceneAgent("recruitment-scene", "agent-recruitment");
        registry.registerSceneAgent("approval-scene", "agent-approval");
        registry.registerSceneAgent("training-scene", "agent-training");
        
        // 注册 Agent 到端点的映射
        registry.registerAgentEndpoint("agent-approval", "llm-cap-approval");
        registry.registerAgentEndpoint("agent-training", "llm-cap-training");
        
        return registry;
    }
}
```

---

## 七、总结

### 7.1 核心设计要点

| 要点 | 说明 |
|------|------|
| **命令触发** | LLM 通过 A2A 命令激活，而非直接调用 |
| **沙箱隔离** | LLM 完全隔离在独立沙箱内 |
| **统一协议** | 使用 Agent-Command 协议进行通信 |
| **MCP 路由** | MCP Agent 负责命令路由和权限验证 |

### 7.2 命令类型总结

| 命令类型 | 说明 | 场景 |
|----------|------|------|
| LLM_ACTIVATE | 激活 LLM 沙箱 | 启动 LLM 任务 |
| LLM_CHAT | LLM 对话 | 交互式对话 |
| LLM_FUNCTION | 函数调用 | 工具调用 |
| KB_SEARCH | 知识检索 | RAG 增强 |
| SCENE_TRANSFER | 场景传输 | 跨场景交互 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
