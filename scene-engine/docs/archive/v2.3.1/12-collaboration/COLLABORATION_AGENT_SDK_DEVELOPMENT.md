# SE SDK v2.3.1 与 AGENT-SDK 协同开发文档

**版本**: 2.3.1  
**发布日期**: 2026-03-22  
**目标读者**: AGENT-SDK 开发团队  
**协作方**: SE SDK 团队  
**状态**: 🔴 待确认

---

## 一、协作背景

### 1.1 版本目标

SE SDK v2.3.1 版本需要 AGENT-SDK 提供场景间协作和 Agent 通信支持，实现跨场景上下文传递和任务委托。

### 1.2 协作需求概述

| 需求 | 优先级 | SE SDK 依赖程度 |
|------|--------|----------------|
| A2A 上下文传递 | P0 | 高 |
| Command 路由 | P0 | 高 |
| Agent 注册发现 | P1 | 中 |
| 消息队列支持 | P1 | 中 |

### 1.3 当前状态

```
SE SDK 覆盖度: 81.2%
├── Agent Service 覆盖度: 58%
│   └── 需要 AGENT-SDK 补齐: 42%
└── 场景间协作: 0% (未实现)
```

---

## 二、协作边界

### 2.1 职责划分

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              SE SDK                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ • 上下文序列化和反序列化 (ContextSerializer)                         │  │
│  │ • 上下文传递策略选择                                                 │  │
│  │ • 跨场景业务逻辑                                                     │  │
│  │ • 上下文合并处理                                                     │  │
│  │ • 场景 Agent 生命周期管理                                            │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼ 调用
┌─────────────────────────────────────────────────────────────────────────────┐
│                           AGENT-SDK                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ • A2A Command 协议实现                                               │  │
│  │ • Command 路由和分发                                                 │  │
│  │ • 消息队列管理                                                       │  │
│  │ • Agent 生命周期管理                                                 │  │
│  │ • 负载均衡和故障转移                                                 │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 接口调用关系

| SE SDK 调用 | AGENT-SDK 提供 | 说明 |
|-------------|----------------|------|
| `a2aService.sendCommand(command)` | CommandResponse | 同步发送命令 |
| `a2aService.sendCommandAsync(command, callback)` | void | 异步发送命令 |
| `a2aService.transferContext(transfer)` | TransferResult | 传递上下文 |
| `a2aService.registerAgent(agentInfo)` | RegistrationResult | 注册 Agent |

---

## 三、任务清单

### 3.1 P0 级任务 (关键)

#### 任务 AGENT-001: A2A 上下文传递协议

**优先级**: 🔴 P0  
**预计工时**: 4天  
**依赖**: 无

**任务描述**:
扩展 A2A Command 协议，支持场景间上下文传递能力。

**接口定义**:

```java
/**
 * A2A 服务接口
 */
public interface A2AService {
    
    /**
     * 发送 Command
     * 
     * @param command 命令
     * @return 响应
     */
    CommandResponse sendCommand(Command command);
    
    /**
     * 异步发送 Command
     * 
     * @param command 命令
     * @param callback 回调
     */
    void sendCommandAsync(Command command, CommandCallback callback);
    
    /**
     * 传递上下文
     * 
     * @param transfer 上下文传递请求
     * @return 传递结果
     */
    TransferResult transferContext(ContextTransfer transfer);
    
    /**
     * 批量传递上下文
     * 
     * @param transfers 上下文传递请求列表
     * @return 批量传递结果
     */
    BatchTransferResult batchTransferContext(List<ContextTransfer> transfers);
}

/**
 * Command 定义
 */
@Data
@Builder
public class Command {
    private String commandId;           // 命令唯一标识
    private String commandType;         // 命令类型
    private String sourceAgentId;       // 源 Agent ID
    private String targetAgentId;       // 目标 Agent ID
    private String sourceSceneId;       // 源场景 ID
    private String targetSceneId;       // 目标场景 ID
    private Map<String, Object> payload; // 命令负载
    private CommandOptions options;     // 命令选项
    private long timestamp;             // 时间戳
    private int ttl;                    // 生存时间（秒）
    
    @Data
    @Builder
    public static class CommandOptions {
        private boolean requireAck;         // 是否需要确认
        private long timeoutMs;             // 超时时间
        private int retryCount;             // 重试次数
        private Priority priority;          // 优先级
    }
    
    enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
}

/**
 * Command 响应
 */
@Data
public class CommandResponse {
    private String commandId;
    private ResponseStatus status;
    private Map<String, Object> result;
    private String errorMessage;
    private long processingTimeMs;
    
    enum ResponseStatus {
        SUCCESS, FAILED, TIMEOUT, REJECTED
    }
}

/**
 * 上下文传递请求
 */
@Data
@Builder
public class ContextTransfer {
    private String transferId;          // 传递ID
    private String sourceSceneId;       // 源场景ID
    private String targetSceneId;       // 目标场景ID
    private TransferType transferType;  // 传递类型
    private Map<String, Object> context; // 上下文数据
    private TransferOptions options;    // 传递选项
    
    enum TransferType {
        FULL,           // 完整上下文
        PARTIAL,        // 部分上下文
        DELTA           // 增量上下文
    }
    
    @Data
    @Builder
    public static class TransferOptions {
        private boolean mergeExisting;      // 是否合并已有上下文
        private boolean notifyUser;         // 是否通知用户
        private long expireAfterMs;         // 过期时间
        private String mergeStrategy;       // 合并策略: REPLACE, MERGE, APPEND
    }
}

/**
 * 传递结果
 */
@Data
public class TransferResult {
    private String transferId;
    private TransferStatus status;
    private String targetSceneId;
    private Map<String, Object> mergedContext; // 合并后的上下文
    private String errorMessage;
    
    enum TransferStatus {
        SUCCESS, PARTIAL, FAILED, REJECTED
    }
}
```

**使用场景**:

```java
// SE SDK 中场景间协作使用示例
public class SceneCollaborationService {
    
    private final A2AService a2aService;
    
    public void delegateTaskToScene(String sourceSceneId, 
                                     String targetSceneId, 
                                     Map<String, Object> taskData) {
        
        ContextTransfer transfer = ContextTransfer.builder()
            .transferId(UUID.randomUUID().toString())
            .sourceSceneId(sourceSceneId)
            .targetSceneId(targetSceneId)
            .transferType(TransferType.PARTIAL)
            .context(taskData)
            .options(ContextTransfer.TransferOptions.builder()
                .mergeExisting(true)
                .notifyUser(true)
                .mergeStrategy("MERGE")
                .build())
            .build();
        
        TransferResult result = a2aService.transferContext(transfer);
        
        if (result.getStatus() == TransferStatus.SUCCESS) {
            log.info("Task delegated successfully to scene: {}", targetSceneId);
        } else {
            log.error("Failed to delegate task: {}", result.getErrorMessage());
        }
    }
}
```

**验收标准**:
- [ ] `transferContext()` 正确传递上下文
- [ ] 支持完整/部分/增量三种传递类型
- [ ] 支持上下文合并策略
- [ ] 超时和重试机制正常工作

---

#### 任务 AGENT-002: Command 路由增强

**优先级**: 🔴 P0  
**预计工时**: 3天  
**依赖**: AGENT-001

**任务描述**:
增强 Command 路由能力，支持场景级别的命令路由和负载均衡。

**接口定义**:

```java
/**
 * Command 路由服务
 */
public interface CommandRouter {
    
    /**
     * 路由 Command
     * 
     * @param command 命令
     * @return 路由结果
     */
    RouteResult route(Command command);
    
    /**
     * 注册路由规则
     * 
     * @param rule 路由规则
     */
    void registerRouteRule(RouteRule rule);
    
    /**
     * 获取目标 Agent 列表
     * 
     * @param sceneId 场景ID
     * @param commandType 命令类型
     * @return Agent 列表
     */
    List<AgentInfo> getTargetAgents(String sceneId, String commandType);
}

/**
 * 路由规则
 */
@Data
@Builder
public class RouteRule {
    private String ruleId;
    private String sceneId;             // 场景ID (可选，为空表示全局规则)
    private String commandType;         // 命令类型 (支持通配符)
    private RouteStrategy strategy;     // 路由策略
    private List<String> targetAgents;  // 目标 Agent 列表
    private int priority;               // 规则优先级
    private boolean enabled;
    
    enum RouteStrategy {
        ROUND_ROBIN,    // 轮询
        RANDOM,         // 随机
        LEAST_LOAD,     // 最小负载
        STICKY,         // 粘性路由
        BROADCAST       // 广播
    }
}

/**
 * 路由结果
 */
@Data
public class RouteResult {
    private String routeId;
    private List<AgentInfo> selectedAgents;
    private RouteStrategy usedStrategy;
    private Map<String, Object> routingMetadata;
}
```

**验收标准**:
- [ ] 支持多种路由策略
- [ ] 支持场景级别路由规则
- [ ] 支持动态规则注册
- [ ] 负载均衡正常工作

---

### 3.2 P1 级任务 (重要)

#### 任务 AGENT-003: Agent 注册与发现

**优先级**: 🟡 P1  
**预计工时**: 3天  
**依赖**: AGENT-001

**任务描述**:
为 SE SDK 提供 Agent 注册和发现能力，支持场景 Agent 的动态管理。

**接口定义**:

```java
/**
 * Agent 注册服务
 */
public interface AgentRegistry {
    
    /**
     * 注册 Agent
     * 
     * @param agentInfo Agent 信息
     * @return 注册结果
     */
    RegistrationResult registerAgent(AgentInfo agentInfo);
    
    /**
     * 注销 Agent
     * 
     * @param agentId Agent ID
     * @return 注销结果
     */
    DeregistrationResult deregisterAgent(String agentId);
    
    /**
     * 发现 Agent
     * 
     * @param query 查询条件
     * @return Agent 列表
     */
    List<AgentInfo> discoverAgents(AgentQuery query);
    
    /**
     * 获取 Agent 状态
     * 
     * @param agentId Agent ID
     * @return Agent 状态
     */
    AgentStatus getAgentStatus(String agentId);
    
    /**
     * 心跳
     * 
     * @param agentId Agent ID
     * @return 心跳结果
     */
    HeartbeatResult heartbeat(String agentId);
}

/**
 * Agent 信息
 */
@Data
@Builder
public class AgentInfo {
    private String agentId;             // Agent ID
    private String agentName;           // Agent 名称
    private String agentType;           // Agent 类型
    private String sceneId;             // 所属场景
    private String endpoint;            // 端点地址
    private AgentStatus status;         // 状态
    private Map<String, Object> capabilities; // 能力列表
    private Map<String, String> metadata;     // 元数据
    private long registeredAt;          // 注册时间
    private long lastHeartbeat;         // 最后心跳时间
    
    enum AgentStatus {
        ONLINE, OFFLINE, BUSY, ERROR
    }
}

/**
 * Agent 查询条件
 */
@Data
@Builder
public class AgentQuery {
    private String sceneId;
    private String agentType;
    private AgentStatus status;
    private List<String> requiredCapabilities;
    private Map<String, String> metadataFilter;
}

/**
 * 注册结果
 */
@Data
public class RegistrationResult {
    private String agentId;
    private boolean success;
    private String errorMessage;
    private String registrationToken;   // 注册令牌
}
```

**验收标准**:
- [ ] Agent 注册成功返回令牌
- [ ] 支持按场景、类型、能力查询
- [ ] 心跳机制正常工作
- [ ] Agent 状态正确更新

---

#### 任务 AGENT-004: 消息队列支持

**优先级**: 🟡 P1  
**预计工时**: 4天  
**依赖**: AGENT-001

**任务描述**:
为 SE SDK 提供异步消息队列支持，实现场景间的异步通信。

**接口定义**:

```java
/**
 * 消息队列服务
 */
public interface MessageQueueService {
    
    /**
     * 发送消息
     * 
     * @param message 消息
     * @return 发送结果
     */
    SendResult sendMessage(SceneMessage message);
    
    /**
     * 订阅消息
     * 
     * @param subscription 订阅配置
     * @return 订阅ID
     */
    String subscribe(MessageSubscription subscription);
    
    /**
     * 取消订阅
     * 
     * @param subscriptionId 订阅ID
     */
    void unsubscribe(String subscriptionId);
    
    /**
     * 确认消息
     * 
     * @param messageId 消息ID
     */
    void acknowledge(String messageId);
}

/**
 * 场景消息
 */
@Data
@Builder
public class SceneMessage {
    private String messageId;
    private String sourceSceneId;
    private String targetSceneId;
    private String messageType;
    private Map<String, Object> payload;
    private MessagePriority priority;
    private long ttl;
    private Map<String, String> headers;
    
    enum MessagePriority {
        LOW, NORMAL, HIGH
    }
}

/**
 * 消息订阅
 */
@Data
@Builder
public class MessageSubscription {
    private String sceneId;
    private List<String> messageTypes;
    private MessageHandler handler;
    private SubscriptionOptions options;
    
    @Data
    @Builder
    public static class SubscriptionOptions {
        private boolean autoAck;
        private int prefetchCount;
        private int maxRetries;
    }
}

/**
 * 消息处理器
 */
public interface MessageHandler {
    void handle(SceneMessage message);
    void onError(SceneMessage message, Exception error);
}
```

**验收标准**:
- [ ] 消息发送成功返回消息ID
- [ ] 订阅后正确接收消息
- [ ] 支持消息确认机制
- [ ] 支持消息重试

---

### 3.3 P2 级任务 (优化)

#### 任务 AGENT-005: 负载均衡和故障转移

**优先级**: 🟢 P2  
**预计工时**: 3天  
**依赖**: AGENT-002, AGENT-003

**任务描述**:
为 SE SDK 提供负载均衡和故障转移能力，提高场景协作的可靠性。

**接口定义**:

```java
/**
 * 负载均衡服务
 */
public interface LoadBalancer {
    
    /**
     * 选择最优 Agent
     * 
     * @param agents Agent 列表
     * @param context 选择上下文
     * @return 选中的 Agent
     */
    AgentInfo selectAgent(List<AgentInfo> agents, SelectionContext context);
    
    /**
     * 更新 Agent 负载
     * 
     * @param agentId Agent ID
     * @param loadDelta 负载变化
     */
    void updateLoad(String agentId, int loadDelta);
    
    /**
     * 获取 Agent 负载信息
     * 
     * @param agentId Agent ID
     * @return 负载信息
     */
    LoadInfo getLoadInfo(String agentId);
}

/**
 * 故障转移服务
 */
public interface FailoverService {
    
    /**
     * 执行带故障转移的操作
     * 
     * @param operation 操作
     * @param failoverOptions 故障转移选项
     * @return 操作结果
     */
    <T> T executeWithFailover(Operation<T> operation, FailoverOptions failoverOptions);
    
    /**
     * 记录失败
     * 
     * @param agentId Agent ID
     * @param error 错误信息
     */
    void recordFailure(String agentId, Exception error);
    
    /**
     * 检查 Agent 是否可用
     * 
     * @param agentId Agent ID
     * @return 是否可用
     */
    boolean isAvailable(String agentId);
}

/**
 * 故障转移选项
 */
@Data
@Builder
public class FailoverOptions {
    private int maxRetries;
    private long retryDelayMs;
    private List<String> fallbackAgents;
    private CircuitBreakerConfig circuitBreaker;
    
    @Data
    @Builder
    public static class CircuitBreakerConfig {
        private int failureThreshold;
        private long openDurationMs;
        private int halfOpenRequests;
    }
}
```

---

## 四、集成规范

### 4.1 Maven 依赖

```xml
<!-- SE SDK 添加 AGENT-SDK 依赖 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 4.2 Spring Boot 自动配置

```java
@Configuration
@ConditionalOnClass(A2AService.class)
public class AgentSdkAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public A2AService a2aService() {
        return new A2AServiceImpl();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public AgentRegistry agentRegistry() {
        return new AgentRegistryImpl();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SceneCollaborationService sceneCollaborationService(
            A2AService a2aService,
            AgentRegistry agentRegistry) {
        return new SceneCollaborationServiceImpl(a2aService, agentRegistry);
    }
}
```

### 4.3 配置属性

```yaml
# application.yml
ooder:
  agent:
    a2a:
      enabled: true
      default-timeout: 30000
      max-retries: 3
    registry:
      enabled: true
      heartbeat-interval: 30000
      offline-threshold: 90000
    message-queue:
      enabled: true
      provider: redis  # redis, kafka, rabbitmq
      prefetch-count: 10
    load-balancer:
      strategy: least-load
      health-check-interval: 60000
```

---

## 五、验收标准

### 5.1 功能验收

| 任务ID | 验收标准 | 测试用例 |
|--------|----------|----------|
| AGENT-001 | 上下文传递正确 | 测试完整/部分/增量传递 |
| AGENT-002 | 路由策略正确 | 测试各种路由策略 |
| AGENT-003 | Agent 注册发现正常 | 测试注册、发现、心跳 |
| AGENT-004 | 消息队列正常 | 测试发送、订阅、确认 |
| AGENT-005 | 故障转移正常 | 测试重试、熔断 |

### 5.2 性能验收

| 指标 | 目标 | 说明 |
|------|------|------|
| Command 响应时间 | < 100ms | 同步调用 |
| 上下文传递时间 | < 200ms | 小于 1KB 数据 |
| 消息吞吐量 | ≥ 1000 msg/s | 正常负载 |
| Agent 发现时间 | < 50ms | 本地缓存命中 |

### 5.3 可靠性验收

- [ ] 支持消息持久化
- [ ] 支持消息重试
- [ ] 支持熔断机制
- [ ] 支持优雅降级

---

## 六、里程碑

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           开发里程碑                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  M1 (Week 1)    M2 (Week 2)    M3 (Week 3)    M4 (Week 4)                 │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐                 │
│  │AGENT-001│    │AGENT-002│    │AGENT-003│    │AGENT-005│                 │
│  │A2A协议  │────▶│Command  │────▶│Agent    │────▶│负载均衡 │                 │
│  │         │    │路由     │    │注册发现 │    │故障转移 │                 │
│  └─────────┘    └─────────┘    └─────────┘    └─────────┘                 │
│       │              │              │              │                        │
│       ▼              ▼              ▼              ▼                        │
│  协议实现        路由增强        服务集成       可靠性优化                    │
│                                                                             │
│  目标: 25%      目标: 50%      目标: 75%      目标: 100%                    │
│                                                                             │
│  并行任务: AGENT-004 (消息队列) 在 Week 2-3 完成                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| 网络不稳定 | 高 | 实现重试和本地缓存 |
| Agent 频繁上下线 | 中 | 实现心跳和健康检查 |
| 消息丢失 | 高 | 实现消息持久化和确认 |
| 负载不均衡 | 中 | 实现动态负载感知 |

---

## 八、联系人

| 角色 | 联系人 | 联系方式 |
|------|--------|----------|
| SE SDK 负责人 | - | - |
| AGENT-SDK 负责人 | - | - |
| 接口评审 | - | - |

---

## 九、参考文档

- [SE SDK 覆盖度报告](./SCENE_LIFECYCLE_COVERAGE_V4.md)
- [AGENT-SDK 现有接口文档](../COLLABORATION_AGENT_SDK_V2_3_1.md)
- [A2A Command 协议规范](../llm/command/A2ACommand.java)

---

*文档版本: 1.0*  
*创建日期: 2026-03-22*  
*SE SDK 团队*
