# AGENT-SDK 协作任务文档 - 版本 2.3.1

**版本**: 2.3.1  
**代号**: Context-Core  
**目标日期**: 2026-04-15  
**状态**: 待确认  
**目标读者**: AGENT-SDK 开发团队

---

## 一、协作背景

### 1.1 协作目标

Engine 版本 2.3.1 需要 AGENT-SDK 提供以下核心能力：
1. **A2A 上下文传递协议** - 支持跨场景上下文传递
2. **Command 路由增强** - 支持上下文感知的命令路由
3. **消息队列支持** - 异步上下文传递
4. **Agent 注册与发现** - 支持 LLM 驱动的 Agent 协作

### 1.2 协作边界

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         协作边界定义                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Engine 负责:                                                               │
│  ├── 上下文序列化和反序列化                                                 │
│  ├── 上下文传递策略选择                                                     │
│  ├── 跨场景业务逻辑                                                         │
│  └── 上下文合并处理                                                         │
│                                                                             │
│  AGENT-SDK 负责:                                                            │
│  ├── A2A Command 协议实现                                                   │
│  ├── Command 路由和分发                                                     │
│  ├── 消息队列管理                                                           │
│  ├── Agent 生命周期管理                                                     │
│  └── 负载均衡和故障转移                                                     │
│                                                                             │
│  协作接口:                                                                  │
│  └── A2AService (Engine 调用 AGENT-SDK)                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、任务清单

### 2.1 AGENT-SDK-001: A2A 上下文传递协议

**优先级**: P0  
**预计工时**: 4天  
**依赖**: 无

#### 任务描述

扩展 A2A Command 协议，支持上下文传递能力。

#### 接口定义

```java
/**
 * A2A 服务接口
 * 由 AGENT-SDK 实现，Engine 调用
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
     * @param transfer 上下文传递
     * @return 传递结果
     */
    TransferResult transferContext(ContextTransfer transfer);
    
    /**
     * 注册 Agent
     * 
     * @param agentInfo Agent 信息
     * @return 注册结果
     */
    RegistrationResult registerAgent(AgentInfo agentInfo);
    
    /**
     * 发现 Agent
     * 
     * @param criteria 发现条件
     * @return Agent 列表
     */
    List<AgentInfo> discoverAgents(DiscoveryCriteria criteria);
}

/**
 * A2A Command (扩展支持上下文传递)
 */
@Data
@Builder
public class Command implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 基础信息
    private String commandId;
    private String commandType;
    private String version;
    
    // 路由信息
    private String sourceAgentId;
    private String targetAgentId;
    private String sourceSceneId;
    private String targetSceneId;
    
    // ========== 新增：上下文传递 ==========
    private ContextTransfer contextTransfer;    // 上下文传递数据
    private String contextReference;            // 上下文引用（引用传递模式）
    private TransferMode transferMode;          // 传递模式
    
    // 负载
    private Map<String, Object> payload;
    private Map<String, Object> headers;
    
    // 元数据
    private long timestamp;
    private long ttl;
    private Priority priority;
    
    // 追踪
    private String traceId;
    private List<String> spanIds;
}

/**
 * 上下文传递
 */
@Data
@Builder
public class ContextTransfer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String transferId;
    private String sourceContextId;
    private String targetContextId;
    private String sourceSceneId;
    private String targetSceneId;
    
    private TransferMode transferMode;
    private Set<ContextPart> includedParts;
    
    private String serializedContext;       // 序列化的上下文
    private ContextReference contextReference;
    private Map<String, Object> contextDelta;
    
    private long createdAt;
    private long expiresAt;
}

/**
 * 传递模式
 */
public enum TransferMode {
    FULL,       // 完整传递
    REFERENCE,  // 引用传递
    DELTA,      // 增量传递
    SELECTIVE   // 选择性传递
}

/**
 * 上下文部分
 */
public enum ContextPart {
    SCENE_CONTEXT,
    NLP_CONTEXT,
    KNOWLEDGE_CONTEXT,
    TOOL_CONTEXT,
    SECURITY_CONTEXT
}
```

#### 验收标准

- [ ] Command 支持携带 ContextTransfer
- [ ] 支持 4 种传递模式
- [ ] 支持传递超时和重试
- [ ] 支持传递状态追踪
- [ ] 与现有 A2A 协议兼容

#### 交付物

1. 扩展的 Command 结构
2. ContextTransfer 支持
3. 传递状态管理
4. 兼容性测试报告

---

### 2.2 AGENT-SDK-002: Command 路由增强

**优先级**: P0  
**预计工时**: 3天  
**依赖**: AGENT-SDK-001

#### 任务描述

增强 Command Router，支持基于上下文的智能路由。

#### 接口定义

```java
/**
 * Command 路由器
 */
public interface CommandRouter {
    
    /**
     * 路由 Command
     * 
     * @param command 命令
     * @return 目标 Agent
     */
    String route(Command command);
    
    /**
     * 注册路由策略
     * 
     * @param strategy 策略
     */
    void registerStrategy(RoutingStrategy strategy);
}

/**
 * 路由策略
 */
public interface RoutingStrategy {
    
    /**
     * 选择目标
     * 
     * @param command 命令
     * @param candidates 候选 Agent
     * @return 选中的 Agent
     */
    String select(Command command, List<AgentInfo> candidates);
}

/**
 * 基于上下文的路由策略
 */
@Component
public class ContextBasedRoutingStrategy implements RoutingStrategy {
    
    @Override
    public String select(Command command, List<AgentInfo> candidates) {
        ContextTransfer transfer = command.getContextTransfer();
        if (transfer == null) {
            // 无上下文，使用默认策略
            return candidates.get(0).getAgentId();
        }
        
        // 根据上下文中的场景类型选择 Agent
        String sceneType = extractSceneType(transfer);
        return candidates.stream()
            .filter(agent -> agent.getCapabilities().contains(sceneType))
            .findFirst()
            .map(AgentInfo::getAgentId)
            .orElse(candidates.get(0).getAgentId());
    }
    
    private String extractSceneType(ContextTransfer transfer) {
        // 从上下文中提取场景类型
        if (transfer.getSerializedContext() != null) {
            // 解析序列化的上下文
            return parseSceneType(transfer.getSerializedContext());
        }
        return null;
    }
}

/**
 * 基于负载的路由策略
 */
@Component
public class LoadBasedRoutingStrategy implements RoutingStrategy {
    
    @Autowired
    private AgentLoadMonitor loadMonitor;
    
    @Override
    public String select(Command command, List<AgentInfo> candidates) {
        // 选择负载最低的 Agent
        return candidates.stream()
            .min(Comparator.comparingInt(agent -> 
                loadMonitor.getLoad(agent.getAgentId())))
            .map(AgentInfo::getAgentId)
            .orElse(candidates.get(0).getAgentId());
    }
}
```

#### 内置策略

| 策略 | 说明 |
|-----|------|
| `ContextBasedStrategy` | 基于上下文场景类型路由 |
| `LoadBasedStrategy` | 基于负载均衡路由 |
| `AffinityStrategy` | 基于会话亲和性路由 |
| `PriorityStrategy` | 基于优先级路由 |

#### 验收标准

- [ ] 支持基于上下文的路由
- [ ] 支持 4 种内置路由策略
- [ ] 支持策略组合
- [ ] 支持动态路由表更新
- [ ] 支持路由失败降级

#### 交付物

1. 增强的 CommandRouter
2. 路由策略实现
3. 路由表管理
4. 性能监控

---

### 2.3 AGENT-SDK-003: 消息队列支持

**优先级**: P1  
**预计工时**: 3天  
**依赖**: AGENT-SDK-001

#### 任务描述

提供消息队列支持，实现异步上下文传递。

#### 接口定义

```java
/**
 * 消息队列服务
 */
public interface MessageQueueService {
    
    /**
     * 发送消息
     * 
     * @param topic 主题
     * @param message 消息
     */
    void send(String topic, QueueMessage message);
    
    /**
     * 订阅消息
     * 
     * @param topic 主题
     * @param handler 处理器
     * @return 订阅ID
     */
    String subscribe(String topic, MessageHandler handler);
    
    /**
     * 取消订阅
     * 
     * @param subscriptionId 订阅ID
     */
    void unsubscribe(String subscriptionId);
    
    /**
     * 发送上下文传递消息
     * 
     * @param transfer 上下文传递
     */
    void sendContextTransfer(ContextTransfer transfer);
}

/**
 * 队列消息
 */
@Data
@Builder
public class QueueMessage implements Serializable {
    private String messageId;
    private String topic;
    private String type;
    private byte[] payload;
    private Map<String, String> headers;
    private long timestamp;
    private int priority;
}

/**
 * 消息处理器
 */
public interface MessageHandler {
    void onMessage(QueueMessage message);
    void onError(Exception error);
}

/**
 * 主题定义
 */
public class Topics {
    public static final String CONTEXT_TRANSFER = "a2a.context.transfer";
    public static final String COMMAND_ASYNC = "a2a.command.async";
    public static final String AGENT_EVENTS = "a2a.agent.events";
}
```

#### 消息队列特性

| 特性 | 说明 |
|-----|------|
| 持久化 | 支持消息持久化，防止丢失 |
| 优先级 | 支持消息优先级 |
| 延迟投递 | 支持延迟消息 |
| 死信队列 | 支持失败消息处理 |
| 消息追踪 | 支持消息链路追踪 |

#### 验收标准

- [ ] 支持同步/异步发送
- [ ] 支持消息持久化
- [ ] 支持优先级队列
- [ ] 支持死信队列
- [ ] 支持消息追踪

#### 交付物

1. MessageQueueService 实现
2. 上下文传递消息支持
3. 消息追踪实现
4. 监控指标

---

### 2.4 AGENT-SDK-004: Agent 注册与发现

**优先级**: P1  
**预计工时**: 2天  
**依赖**: 无

#### 任务描述

实现 Agent 注册与发现机制，支持 LLM 驱动的 Agent 协作。

#### 接口定义

```java
/**
 * Agent 注册中心
 */
public interface AgentRegistry {
    
    /**
     * 注册 Agent
     * 
     * @param agentInfo Agent 信息
     * @return 注册结果
     */
    RegistrationResult register(AgentInfo agentInfo);
    
    /**
     * 注销 Agent
     * 
     * @param agentId Agent ID
     */
    void unregister(String agentId);
    
    /**
     * 发现 Agent
     * 
     * @param criteria 发现条件
     * @return Agent 列表
     */
    List<AgentInfo> discover(DiscoveryCriteria criteria);
    
    /**
     * 获取 Agent 信息
     * 
     * @param agentId Agent ID
     * @return Agent 信息
     */
    AgentInfo getAgent(String agentId);
    
    /**
     * 更新 Agent 状态
     * 
     * @param agentId Agent ID
     * @param status 状态
     */
    void updateStatus(String agentId, AgentStatus status);
}

/**
 * Agent 信息
 */
@Data
@Builder
public class AgentInfo implements Serializable {
    private String agentId;
    private String agentName;
    private String agentType;
    private String version;
    private AgentStatus status;
    private List<String> capabilities;
    private Map<String, Object> metadata;
    private String endpoint;
    private long registeredAt;
    private long lastHeartbeat;
}

/**
 * Agent 状态
 */
public enum AgentStatus {
    ONLINE,     // 在线
    OFFLINE,    // 离线
    BUSY,       // 忙碌
    MAINTENANCE // 维护中
}

/**
 * 发现条件
 */
@Data
@Builder
public class DiscoveryCriteria {
    private String agentType;
    private List<String> capabilities;
    private AgentStatus status;
    private Map<String, Object> filters;
}

/**
 * 注册结果
 */
@Data
@Builder
public class RegistrationResult {
    private boolean success;
    private String agentId;
    private String token;
    private String errorMessage;
}
```

#### 心跳机制

```java
/**
 * 心跳管理器
 */
@Component
public class HeartbeatManager {
    
    @Autowired
    private AgentRegistry agentRegistry;
    
    private final ScheduledExecutorService scheduler = 
        Executors.newScheduledThreadPool(1);
    
    /**
     * 启动心跳检查
     */
    public void start() {
        scheduler.scheduleAtFixedRate(
            this::checkHeartbeats,
            0,
            30,
            TimeUnit.SECONDS
        );
    }
    
    private void checkHeartbeats() {
        long now = System.currentTimeMillis();
        long timeout = 60 * 1000; // 60秒超时
        
        // 获取所有 Agent
        List<AgentInfo> agents = agentRegistry.discover(
            DiscoveryCriteria.builder().build()
        );
        
        for (AgentInfo agent : agents) {
            if (now - agent.getLastHeartbeat() > timeout) {
                // 心跳超时，标记为离线
                agentRegistry.updateStatus(agent.getAgentId(), AgentStatus.OFFLINE);
            }
        }
    }
}
```

#### 验收标准

- [ ] 支持 Agent 注册/注销
- [ ] 支持 Agent 发现
- [ ] 支持心跳检测
- [ ] 支持健康检查
- [ ] 支持负载信息上报

#### 交付物

1. AgentRegistry 实现
2. 心跳管理机制
3. 健康检查接口
4. 服务发现客户端

---

## 三、协作接口汇总

### 3.1 Engine 调用 AGENT-SDK 接口

| 接口 | 方法 | 输入 | 输出 | 说明 |
|-----|------|------|------|------|
| A2AService | sendCommand | Command | CommandResponse | 同步发送命令 |
| A2AService | sendCommandAsync | Command, CommandCallback | void | 异步发送命令 |
| A2AService | transferContext | ContextTransfer | TransferResult | 传递上下文 |
| A2AService | registerAgent | AgentInfo | RegistrationResult | 注册 Agent |
| A2AService | discoverAgents | DiscoveryCriteria | List<AgentInfo> | 发现 Agent |
| CommandRouter | route | Command | String | 路由命令 |
| MessageQueueService | send | String, QueueMessage | void | 发送消息 |
| MessageQueueService | subscribe | String, MessageHandler | String | 订阅消息 |
| AgentRegistry | register | AgentInfo | RegistrationResult | 注册 Agent |
| AgentRegistry | discover | DiscoveryCriteria | List<AgentInfo> | 发现 Agent |

### 3.2 数据模型

| 模型 | 说明 | 字段数 |
|-----|------|--------|
| Command | A2A命令 | 14 |
| ContextTransfer | 上下文传递 | 10 |
| QueueMessage | 队列消息 | 7 |
| AgentInfo | Agent信息 | 10 |
| DiscoveryCriteria | 发现条件 | 4 |
| RegistrationResult | 注册结果 | 4 |

---

## 四、实施计划

### 4.1 时间线

```
Week 1: 基础协议
├── Day 1-2: AGENT-SDK-001 A2A上下文传递协议
├── Day 3-4: AGENT-SDK-002 Command路由增强
└── Day 5: 代码审查和单元测试

Week 2: 高级功能
├── Day 1-2: AGENT-SDK-003 消息队列支持
├── Day 3-4: AGENT-SDK-004 Agent注册与发现
└── Day 5: 代码审查和单元测试

Week 3: 集成测试
├── Day 1-2: 与 Engine 集成测试
├── Day 3-4: 与 LLM-SDK 集成测试
└── Day 5: 文档完善和发布
```

### 4.2 依赖关系

```
AGENT-SDK-001 (A2A上下文传递协议)
    ├── AGENT-SDK-002 (Command路由) [依赖001]
    └── AGENT-SDK-003 (消息队列) [依赖001]

AGENT-SDK-004 (Agent注册与发现) [无依赖]
```

---

## 五、验收标准

### 5.1 功能验收

- [ ] 所有 4 个任务完成开发
- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试用例全部通过
- [ ] API 文档完整

### 5.2 性能验收

| 指标 | 目标值 |
|-----|--------|
| Command 路由延迟 (P99) | < 10ms |
| 上下文传递延迟 (同机房) | < 50ms |
| 上下文传递延迟 (跨机房) | < 200ms |
| 消息队列吞吐量 | > 10000 msg/s |
| Agent 发现延迟 | < 100ms |

### 5.3 稳定性验收

- [ ] 支持 99.9% 可用性
- [ ] 支持故障自动转移
- [ ] 支持网络分区恢复
- [ ] 支持消息不丢失

---

## 六、沟通机制

### 6.1 协作沟通

| 事项 | 频率 | 参与方 |
|-----|------|--------|
| 进度同步 | 每周 | Engine + AGENT-SDK |
| 接口评审 | 按需 | Engine + AGENT-SDK |
| 集成测试 | 每两周 | Engine + AGENT-SDK + LLM-SDK |

### 6.2 联系方式

- **Engine 负责人**: [待填写]
- **AGENT-SDK 负责人**: [待填写]
- **技术交流群**: [待填写]

---

## 七、附录

### 7.1 参考文档

- [LLM 与场景技能交互设计方案](llm-scene-interaction-design.md)
- [Engine 层协作需求](engine-collaboration-request.md)
- [版本 2.3.1 实施路线图](VERSION_2_3_1_ROADMAP.md)
- [LLM-SDK 协作文档](COLLABORATION_LLM_SDK_V2_3_1.md)

### 7.2 变更记录

| 版本 | 日期 | 变更内容 |
|-----|------|---------|
| 1.0 | 2026-03-09 | 初始版本 |

---

**文档维护**: Engine Team  
**最后更新**: 2026-03-09
