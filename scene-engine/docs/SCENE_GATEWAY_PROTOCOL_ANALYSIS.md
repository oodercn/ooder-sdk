# Scene Gateway 北向协议分析与实现方案

## 一、现状分析

### 1.1 当前状态

**scene-gateway 模块现状：**
- 仅有 `pom.xml` 文件，无实际源代码
- 描述为："HTTP API, WebSocket, Comet long polling"
- 依赖已迁移的 skill 模块（skill-org, skill-vfs, skill-msg, skill-agent）

**结论：scene-gateway 是一个空模块，需要重新设计和实现**

### 1.2 北向协议需求

根据文档分析，北向协议需要提供：

| 协议类型 | 用途 | 优先级 |
|----------|------|--------|
| HTTP REST API | 外部系统调用 | P0 |
| WebSocket | 实时双向通信 | P0 |
| SSE (Server-Sent Events) | 服务器推送 | P1 |
| gRPC | 高性能内部通信 | P1 |
| GraphQL | 灵活查询 | P2 |

## 二、协议实现完成度评估

### 2.1 已实现的协议组件

| 组件 | 位置 | 完成度 | 说明 |
|------|------|--------|------|
| HTTP API | scene-engine | 70% | 基础 Controller 存在，需完善 |
| WebSocket | scene-engine | 50% | 基础配置存在，需实现业务逻辑 |
| UDP Discovery | scene-engine | 80% | `UdpDiscoveryService` 已实现 |
| mDNS Discovery | scene-engine | 60% | `MdnsDiscoveryService` 框架存在 |
| CAP Router | scene-engine | 90% | `CapRouter` 已实现 |
| EventBus | scene-engine | 85% | 事件总线已实现 |

### 2.2 缺失的协议组件

| 组件 | 优先级 | 说明 |
|------|--------|------|
| REST API 统一入口 | P0 | 需要统一的 API Gateway |
| 认证授权中间件 | P0 | JWT/OAuth2 集成 |
| 请求路由分发 | P0 | 根据 CAP 路由到对应 Skill |
| 负载均衡 | P1 | 多实例 Skill 负载均衡 |
| 限流熔断 | P1 | 保护系统稳定性 |
| 监控指标 | P1 | Prometheus 指标暴露 |
| OpenAPI 文档 | P2 | Swagger/OpenAPI 自动生成 |

## 三、技术方案

### 3.1 架构设计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Scene Gateway 架构设计                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        接入层 (Gateway Layer)                        │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   HTTP      │  │  WebSocket  │  │    SSE      │  │    gRPC     │ │   │
│  │  │   Server    │  │   Server    │  │   Server    │  │   Server    │ │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘ │   │
│  └─────────┼────────────────┼────────────────┼────────────────┼────────┘   │
│            │                │                │                │            │
│            └────────────────┴────────────────┴────────────────┘            │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      协议适配层 (Protocol Adapter)                    │   │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │   │
│  │  │  HTTP Adapter   │  │ WebSocket Adapter│  │  gRPC Adapter   │     │   │
│  │  │  - REST 转 CAP  │  │ - 双向流转换    │  │ - Proto 转 CAP  │     │   │
│  │  │  - 参数解析     │  │ - 心跳管理      │  │ - 流式处理      │     │   │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      核心处理层 (Core Layer)                         │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   Auth      │  │   Router    │  │   Load      │  │  Circuit    │ │   │
│  │  │ Middleware  │  │   Handler   │  │  Balancer   │  │   Breaker   │ │   │
│  │  │ - JWT验证   │  │ - CAP路由   │  │ - 负载均衡  │  │ - 熔断降级  │ │   │
│  │  │ - 权限检查  │  │ - 协议转换  │  │ - 健康检查  │  │ - 限流控制  │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                             │
│                              ▼                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      服务调用层 (Service Layer)                      │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │                    SceneEngine Core                         │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │   │   │
│  │  │  │ SceneAgent  │  │  CapRouter  │  │  Skill      │         │   │   │
│  │  │  │ Manager     │  │             │  │  Connector  │         │   │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘         │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 核心接口设计

#### 3.2.1 Gateway Server 接口

```java
public interface GatewayServer {
    void start(GatewayConfig config);
    void stop();
    boolean isRunning();
    String getProtocol();
}

public interface HttpGatewayServer extends GatewayServer {
    void registerHandler(String path, HttpHandler handler);
    void registerFilter(HttpFilter filter);
}

public interface WebSocketGatewayServer extends GatewayServer {
    void registerHandler(String path, WebSocketHandler handler);
    void broadcast(String message);
}
```

#### 3.2.2 Protocol Adapter 接口

```java
public interface ProtocolAdapter<Req, Resp> {
    CapRequest adapt(Req request);
    Resp adapt(CapResponse response);
    String getProtocol();
}

public class HttpProtocolAdapter implements ProtocolAdapter<HttpRequest, HttpResponse> {
    @Override
    public CapRequest adapt(HttpRequest request) {
        // HTTP 请求转 CAP 请求
        String capId = extractCapId(request.getPath());
        CapRequest capRequest = new CapRequest(UUID.randomUUID().toString(), capId);
        capRequest.setParameters(request.getParameters());
        return capRequest;
    }
    
    @Override
    public HttpResponse adapt(CapResponse response) {
        // CAP 响应转 HTTP 响应
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(response.isSuccess() ? 200 : 500);
        httpResponse.setBody(response.getResult());
        return httpResponse;
    }
}
```

#### 3.2.3 中间件接口

```java
public interface GatewayMiddleware {
    CapResponse process(CapRequest request, MiddlewareChain chain);
    int getOrder();
}

public class AuthMiddleware implements GatewayMiddleware {
    @Override
    public CapResponse process(CapRequest request, MiddlewareChain chain) {
        // 认证检查
        String token = request.getMetadata("token");
        if (!validateToken(token)) {
            return CapResponse.failure(request.getRequestId(), request.getCapId(), "Unauthorized");
        }
        return chain.next(request);
    }
}

public class RateLimitMiddleware implements GatewayMiddleware {
    @Override
    public CapResponse process(CapRequest request, MiddlewareChain chain) {
        // 限流检查
        if (!rateLimiter.tryAcquire()) {
            return CapResponse.failure(request.getRequestId(), request.getCapId(), "Rate limit exceeded");
        }
        return chain.next(request);
    }
}
```

### 3.3 API 设计

#### 3.3.1 REST API 端点

```yaml
# OpenAPI 规范
openapi: 3.0.0
info:
  title: Scene Gateway API
  version: 2.3.0

paths:
  /api/v1/scenes:
    get:
      summary: 获取场景列表
      operationId: listScenes
      responses:
        '200':
          description: 场景列表
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Scene'
    
    post:
      summary: 创建场景
      operationId: createScene
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/SceneConfig'
      responses:
        '201':
          description: 创建成功

  /api/v1/scenes/{sceneId}:
    get:
      summary: 获取场景详情
      operationId: getScene
      parameters:
        - name: sceneId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 场景详情

  /api/v1/cap/{capId}:
    post:
      summary: 调用能力
      operationId: invokeCapability
      parameters:
        - name: capId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 调用结果

  /api/v1/skills:
    get:
      summary: 获取技能列表
      operationId: listSkills
      responses:
        '200':
          description: 技能列表

  /ws/v1/events:
    get:
      summary: WebSocket 事件流
      operationId: eventStream
      responses:
        '101':
          description: 切换协议到 WebSocket

components:
  schemas:
    Scene:
      type: object
      properties:
        sceneId:
          type: string
        name:
          type: string
        status:
          type: string
          enum: [INITIALIZING, ACTIVE, SUSPENDED, STOPPED]
    
    SceneConfig:
      type: object
      properties:
        name:
          type: string
        capabilities:
          type: array
          items:
            type: string
```

## 四、实现计划

### 4.1 第一阶段：基础 HTTP Gateway（2周）

**目标：** 实现基础的 HTTP REST API 入口

**任务：**
1. 创建 `HttpGatewayServer` 实现
2. 集成 Spring Boot Web
3. 实现基础 Controller（Scene, CAP, Skill）
4. 实现 `HttpProtocolAdapter`
5. 集成 `CapRouter` 进行请求路由

**交付物：**
- 可运行的 HTTP Gateway
- 基础 REST API（场景管理、能力调用）
- 单元测试

### 4.2 第二阶段：认证与中间件（1周）

**目标：** 添加安全性和稳定性保障

**任务：**
1. 实现 `AuthMiddleware`（JWT 验证）
2. 实现 `RateLimitMiddleware`（限流）
3. 实现 `CircuitBreakerMiddleware`（熔断）
4. 集成到 Gateway 流程

**交付物：**
- 完整的中间件链
- 安全配置
- 压力测试报告

### 4.3 第三阶段：WebSocket 支持（1周）

**目标：** 实现实时双向通信

**任务：**
1. 创建 `WebSocketGatewayServer` 实现
2. 实现 `WebSocketProtocolAdapter`
3. 集成 EventBus 进行事件推送
4. 实现心跳和重连机制

**交付物：**
- WebSocket 服务
- 实时事件推送
- 客户端示例

### 4.4 第四阶段：高级特性（1周）

**目标：** 完善监控和文档

**任务：**
1. 集成 Prometheus 监控
2. 自动生成 OpenAPI 文档
3. 实现负载均衡
4. 性能优化

**交付物：**
- 监控仪表盘
- API 文档
- 性能测试报告

## 五、技术选型

| 组件 | 选型 | 理由 |
|------|------|------|
| Web 框架 | Spring Boot Web | 成熟稳定，生态丰富 |
| WebSocket | Spring WebSocket | 与 Spring Boot 集成好 |
| 认证 | JWT + Spring Security | 标准方案，易于集成 |
| 限流 | Bucket4j | 轻量级，性能好 |
| 熔断 | Resilience4j | 功能完善，与 Spring 集成好 |
| 监控 | Micrometer + Prometheus | 云原生标准 |
| 文档 | SpringDoc OpenAPI | 自动生成，维护成本低 |

## 六、与现有组件集成

### 6.1 与 SceneEngine 集成

```java
@Configuration
public class GatewayConfig {
    
    @Autowired
    private SceneEngine sceneEngine;
    
    @Bean
    public GatewayServer httpGatewayServer() {
        HttpGatewayServer server = new HttpGatewayServer();
        
        // 注册 Scene 管理 Handler
        server.registerHandler("/api/v1/scenes", new SceneHandler(sceneEngine));
        
        // 注册 CAP 调用 Handler
        server.registerHandler("/api/v1/cap/*", new CapHandler(sceneEngine.getCapRouter()));
        
        return server;
    }
}
```

### 6.2 与 CapRouter 集成

```java
public class CapHandler implements HttpHandler {
    
    private final CapRouter capRouter;
    
    @Override
    public HttpResponse handle(HttpRequest request) {
        // 协议转换
        HttpProtocolAdapter adapter = new HttpProtocolAdapter();
        CapRequest capRequest = adapter.adapt(request);
        
        // 路由到对应 Skill
        CapResponse capResponse = capRouter.routeRequest(
            capRequest.getCapId(), 
            capRequest
        );
        
        // 转换响应
        return adapter.adapt(capResponse);
    }
}
```

### 6.3 与 EventBus 集成

```java
public class WebSocketEventHandler implements WebSocketHandler {
    
    @Autowired
    private EventBus eventBus;
    
    @Override
    public void onOpen(WebSocketSession session) {
        // 订阅事件
        eventBus.subscribe("scene.*", event -> {
            session.sendMessage(event.toJson());
        });
    }
}
```

## 七、总结

### 当前完成度：20%

**已完成：**
- Gateway 模块框架（pom.xml）
- 部分底层组件（Discovery, CapRouter, EventBus）

**待实现：**
- HTTP Server 实现
- WebSocket Server 实现
- 协议适配器
- 中间件链
- REST API 端点

### 建议

1. **立即开始**：scene-gateway 是北向协议的核心入口，需要尽快实现
2. **分阶段实施**：按照四阶段计划逐步完成
3. **优先 HTTP**：先完成基础的 HTTP REST API，再扩展 WebSocket
4. **复用现有组件**：充分利用已实现的 CapRouter、EventBus 等组件

### 预期收益

- **统一入口**：提供统一的北向协议入口
- **协议转换**：自动转换 HTTP/WebSocket 到内部 CAP 协议
- **安全保障**：统一的认证、限流、熔断机制
- **可观测性**：完整的监控和日志
