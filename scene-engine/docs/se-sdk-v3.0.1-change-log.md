# SE SDK v3.0.1 变更说明与二次开发指南

## 📋 版本信息

| 项目 | 内容 |
|------|------|
| 版本号 | 3.0.1 |
| 发布日期 | 2025-03-27 |
| Maven 坐标 | `net.ooder:scene-engine:3.0.1` |
| 本地仓库路径 | `D:\maven\.m2\repository\net\ooder\scene-engine\3.0.1\` |

---

## 🆕 新增功能

### 1. 统一会话管理 (UnifiedSessionManager)

**位置**: `net.ooder.scene.session.unified`

**功能**:
- 支持多种会话类型：USER、AGENT、SCENE、CONVERSATION
- 在线状态管理：ONLINE、OFFLINE、IDLE、BUSY、AWAY
- JSON 文件持久化存储（可扩展 Redis）
- 会话生命周期管理

**核心接口**:
```java
public interface UnifiedSessionManager {
    UnifiedSession createSession(SessionType type, String ownerId, Map<String, Object> metadata);
    UnifiedSession getSession(String sessionId);
    void invalidateSession(String sessionId);
    OnlineStatus getOnlineStatus(String ownerId);
    void heartbeat(String sessionId);
    List<UnifiedSession> getActiveSessionsByScene(String sceneGroupId);
}
```

### 2. Agent 上下文管理 (AgentContextManager)

**位置**: `net.ooder.scene.agent.context`

**功能**:
- 虚拟 Agent (LLM驱动) 与物理 Agent (外部服务) 区分
- 多级上下文管理：system → scene → session → conversation
- 心跳管理（物理 Agent 必需，虚拟 Agent 可选）
- Agent 档案与状态管理

**核心接口**:
```java
public interface AgentContextManager {
    AgentProfile registerVirtualAgent(VirtualAgentConfig config);
    AgentProfile registerPhysicalAgent(PhysicalAgentConfig config);
    AgentContext getAgentContext(String agentId);
    void heartbeat(String agentId);
    List<AgentProfile> getOnlineAgents(String sceneGroupId);
}
```

### 3. 消息队列服务 (MessageQueueService)

**位置**: `net.ooder.scene.message.queue`

**功能**:
- 统一消息格式 (MessageEnvelope)
- 离线消息存储
- 消息确认机制
- 支持多种投递保证：AT_MOST_ONCE、AT_LEAST_ONCE、EXACTLY_ONCE
- 复用现有 AgentMessageBus 和 MessagePersistence

**核心接口**:
```java
public interface MessageQueueService {
    String sendMessage(MessageEnvelope message);
    MessageReceipt sendMessageSync(MessageEnvelope message, long timeoutMs);
    List<MessageEnvelope> getOfflineMessages(String recipientId);
    void acknowledgeMessage(String messageId, String recipientId);
    void subscribe(String recipientId, MessageHandler handler);
}
```

### 4. A2A 协议服务 (A2AProtocolService)

**位置**: `net.ooder.scene.a2a`

**功能**:
- Agent 到 Agent 通信
- MCP 协议支持 (Model Context Protocol)
- 内部消息路由 (RouteAgent)
- 请求-响应模式
- 多 Agent 对话管理

**核心接口**:
```java
public interface A2AProtocolService {
    String sendMessage(A2AMessage message);
    CompletableFuture<A2AResponse> sendRequest(A2ARequest request);
    A2AConversation createConversation(String sceneGroupId, List<String> agentIds);
    void registerHandler(String agentId, A2AMessageHandler handler);
    void broadcast(String sceneGroupId, A2AMessage message);
}
```

### 5. 北向消息队列 (NorthboundMessageQueue)

**位置**: `net.ooder.scene.message.northbound`

**功能**:
- P2A (用户到 Agent) 通信
- P2P (用户到用户) 通信
- 统一北向接口

**核心接口**:
```java
public interface NorthboundMessageQueue {
    String sendToAgent(String userId, String agentId, Object content);
    String sendToUser(String fromUserId, String toUserId, Object content);
    List<MessageEnvelope> getMessagesForUser(String userId);
    void subscribeUser(String userId, NorthboundMessageHandler handler);
}
```

---

## 🔄 变更影响

### 兼容性

| 变更类型 | 说明 |
|---------|------|
| ✅ 向后兼容 | 现有接口保持不变，新增接口独立 |
| ✅ 无破坏性变更 | 所有新增组件使用 `@ConditionalOnMissingBean` |
| ✅ 复用现有组件 | MessageQueueService 复用 AgentMessageBus 和 MessagePersistence |

### 废弃接口

无废弃接口。以下接口建议迁移到新接口：

| 旧接口 | 新接口 | 说明 |
|-------|-------|------|
| `SessionManager` | `UnifiedSessionManager` | 建议使用统一会话管理 |
| `AgentSessionManager` | `AgentContextManager` | 建议使用 Agent 上下文管理 |

---

## 🛠️ 二次开发指南

### 1. 快速开始

#### 1.1 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>3.0.1</version>
</dependency>
```

#### 1.2 基本配置

```yaml
scene:
  session:
    enabled: true
    storage-root: data/sessions
    default-ttl: 86400000
    
  agent:
    enabled: true
    default-heartbeat-interval: 30000
    
  message:
    enabled: true
    
  a2a:
    enabled: true
```

### 2. 使用示例

#### 2.1 创建会话

```java
@Autowired
private UnifiedSessionManager sessionManager;

public void createUserSession(String userId) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("clientIp", "192.168.1.1");
    metadata.put("userAgent", "Chrome/120.0");
    
    UnifiedSession session = sessionManager.createSession(
        SessionType.USER, 
        userId, 
        metadata
    );
    
    String sessionId = session.getSessionId();
}
```

#### 2.2 注册虚拟 Agent

```java
@Autowired
private AgentContextManager agentContextManager;

public void registerVirtualAgent() {
    VirtualAgentConfig config = VirtualAgentConfig.builder()
        .agentId("assistant-001")
        .name("智能助手")
        .sceneGroupId("scene-default")
        .role("assistant")
        .llmProvider("openai")
        .llmModel("gpt-4")
        .systemPrompt("你是一个有帮助的助手")
        .capability("chat")
        .capability("qa")
        .maxHistoryLength(20)
        .temperature(0.7)
        .build();
    
    AgentProfile profile = agentContextManager.registerVirtualAgent(config);
}
```

#### 2.3 注册物理 Agent

```java
public void registerPhysicalAgent() {
    PhysicalAgentConfig config = PhysicalAgentConfig.builder()
        .agentId("device-001")
        .name("IoT设备")
        .sceneGroupId("scene-iot")
        .endpoint("http://192.168.1.100:8080/api")
        .secretKey("your-secret-key")
        .heartbeatInterval(30000)
        .heartbeatTimeout(60000)
        .maxRetries(3)
        .build();
    
    AgentProfile profile = agentContextManager.registerPhysicalAgent(config);
}
```

#### 2.4 发送消息

```java
@Autowired
private MessageQueueService messageQueueService;

public void sendMessage() {
    MessageEnvelope envelope = MessageEnvelope.builder()
        .from(MessageParticipant.user("user-001"))
        .to(MessageParticipant.virtualAgent("assistant-001"))
        .sceneGroupId("scene-default")
        .messageType("chat")
        .content("你好，请帮我分析一下这段代码")
        .priority(MessagePriority.NORMAL)
        .build();
    
    String messageId = messageQueueService.sendMessage(envelope);
}
```

#### 2.5 A2A 通信

```java
@Autowired
private A2AProtocolService a2aProtocolService;

public void sendA2AMessage() {
    A2AMessage message = A2AMessage.builder()
        .from("agent-001")
        .to("agent-002")
        .sceneGroupId("scene-default")
        .type(A2AMessageType.TASK_REQUEST)
        .payload(Map.of("task", "analyze", "data", "some data"))
        .build();
    
    a2aProtocolService.sendMessage(message);
}

public void requestA2A() {
    A2ARequest request = new A2ARequest("agent-001", "agent-002", "execute");
    request.setParameter("command", "start");
    request.setTimeout(30000);
    
    CompletableFuture<A2AResponse> future = a2aProtocolService.sendRequest(request);
    
    future.thenAccept(response -> {
        if (response.isSuccess()) {
            System.out.println("Result: " + response.getResult());
        } else {
            System.err.println("Error: " + response.getErrorMessage());
        }
    });
}
```

#### 2.6 北向消息队列

```java
@Autowired
private NorthboundMessageQueue northboundMQ;

public void sendToAgent() {
    String messageId = northboundMQ.sendToAgent(
        "user-001", 
        "assistant-001", 
        "请帮我写一段代码"
    );
}

public void subscribeUser() {
    northboundMQ.subscribeUser("user-001", message -> {
        System.out.println("Received: " + message.getContent());
        northboundMQ.acknowledgeUserMessage("user-001", message.getMessageId());
    });
}
```

### 3. 自定义扩展

#### 3.1 自定义会话存储

```java
public class RedisSessionStorage implements SessionStorage {
    
    private final RedisTemplate<String, UnifiedSession> redisTemplate;
    
    @Override
    public String save(UnifiedSession session) {
        redisTemplate.opsForValue().set(
            "session:" + session.getSessionId(), 
            session, 
            session.getExpireAt() - System.currentTimeMillis(),
            TimeUnit.MILLISECONDS
        );
        return session.getSessionId();
    }
    
    @Override
    public Optional<UnifiedSession> load(String sessionId) {
        return Optional.ofNullable(
            redisTemplate.opsForValue().get("session:" + sessionId)
        );
    }
    
    // ... 其他方法实现
}

@Configuration
public class CustomSessionConfig {
    
    @Bean
    @Primary
    public SessionStorage sessionStorage(RedisTemplate<String, UnifiedSession> redisTemplate) {
        return new RedisSessionStorage(redisTemplate);
    }
}
```

#### 3.2 自定义消息处理器

```java
@Component
public class CustomMessageHandler implements MessageHandler {
    
    @Override
    public void onMessage(MessageEnvelope message) {
        String type = message.getMessageType();
        
        switch (type) {
            case "chat":
                handleChat(message);
                break;
            case "command":
                handleCommand(message);
                break;
            default:
                log.warn("Unknown message type: {}", type);
        }
    }
    
    private void handleChat(MessageEnvelope message) {
        // 处理聊天消息
    }
    
    private void handleCommand(MessageEnvelope message) {
        // 处理命令消息
    }
}
```

#### 3.3 自定义 A2A 消息处理器

```java
@Component
public class CustomA2AHandler implements A2AMessageHandler {
    
    @Override
    public void handle(A2AMessage message) {
        A2AMessageType type = message.getMessageType();
        
        if (type == A2AMessageType.TASK_REQUEST) {
            handleTaskRequest(message);
        } else if (type == A2AMessageType.COLLABORATION_INVITE) {
            handleCollaborationInvite(message);
        }
    }
    
    private void handleTaskRequest(A2AMessage message) {
        // 处理任务请求
    }
    
    private void handleCollaborationInvite(A2AMessage message) {
        // 处理协作邀请
    }
}

@Configuration
public class A2AConfig {
    
    @Autowired
    private A2AProtocolService a2aProtocolService;
    
    @Autowired
    private CustomA2AHandler customA2AHandler;
    
    @PostConstruct
    public void registerHandler() {
        a2aProtocolService.registerHandler("agent-custom", customA2AHandler);
    }
}
```

### 4. MCP 协议扩展

#### 4.1 使用 MCPAgent

```java
public class MCPAgentExample {
    
    private final MCPAgent mcpAgent;
    
    public MCPAgentExample() {
        this.mcpAgent = new MCPAgent("mcp-agent-001");
        
        // 注册 MCP 方法处理器
        mcpAgent.registerHandler("task.execute", message -> {
            String action = message.getParam("action");
            Map<String, Object> params = message.getParam("parameters");
            
            // 执行任务
            Object result = executeTask(action, params);
            
            return MCPMessage.response(message.getId(), Map.of("result", result));
        });
    }
    
    // 注册到 A2A 服务
    public void registerToA2A(A2AProtocolService a2aService) {
        a2aService.registerHandler("mcp-agent-001", mcpAgent);
    }
}
```

### 5. 路由规则配置

```java
@Configuration
public class RoutingConfig {
    
    @Autowired
    private A2AProtocolService a2aProtocolService;
    
    @PostConstruct
    public void configureRouting() {
        // 添加路由规则
        A2ARoutingRule chatRule = new A2ARoutingRule(
            "rule-chat", 
            "chat", 
            "assistant-001"
        );
        chatRule.setPriority(10);
        a2aProtocolService.addRoutingRule(chatRule);
        
        A2ARoutingRule commandRule = new A2ARoutingRule(
            "rule-command",
            "command",
            "executor-001"
        );
        commandRule.setPriority(8);
        a2aProtocolService.addRoutingRule(commandRule);
    }
}
```

---

## 📦 模块依赖关系

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         应用层 (Application Layer)                       │
├─────────────────────────────────────────────────────────────────────────┤
│  NorthboundMessageQueue (P2A/P2P)                                        │
├─────────────────────────────────────────────────────────────────────────┤
│  UnifiedSessionManager │ AgentContextManager │ MessageQueueService      │
├─────────────────────────────────────────────────────────────────────────┤
│  A2AProtocolService                                                      │
│  ├── MCPAgent (MCP协议)                                                  │
│  └── RouteAgent (内部路由)                                               │
├─────────────────────────────────────────────────────────────────────────┤
│                         基础设施层 (Infrastructure)                       │
│  AgentMessageBus │ MessagePersistence │ JsonSessionStorage              │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🔧 配置参考

### 完整配置示例

```yaml
scene:
  # 会话配置
  session:
    enabled: true
    storage-root: data/sessions
    default-ttl: 86400000        # 24小时
    cleanup-interval: 3600000    # 1小时清理一次
    
  # Agent 配置
  agent:
    enabled: true
    default-heartbeat-interval: 30000   # 30秒
    default-heartbeat-timeout: 60000    # 60秒
    
  # 消息配置
  message:
    enabled: true
    storage-root: data/messages
    persistence-enabled: true
    max-queue-size: 10000
    default-message-ttl: 86400000
    default-max-retries: 3
    
  # A2A 配置
  a2a:
    enabled: true
    protocol-version: "1.0"
    default-request-timeout: 30000
```

---

## 📞 技术支持

如有问题，请联系 SE SDK 开发团队。

---

**文档版本**: 1.0  
**最后更新**: 2025-03-27
