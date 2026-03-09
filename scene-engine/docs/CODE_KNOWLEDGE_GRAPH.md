# Scene Engine 代码知识图谱

**版本**: v2.3.1  
**日期**: 2026-03-07  
**状态**: 完整梳理

---

## 一、包结构总览

```
net.ooder.scene
├── core/           # 核心层 - 引擎核心功能
├── skill/          # 技能层 - 业务技能
├── discovery/      # 发现层 - 能力发现
├── engine/         # 引擎层 - 引擎管理
├── event/          # 事件层 - 事件发布
├── llm/            # LLM层 - LLM代理
├── monitor/        # 监控层 - 场景监控
├── protocol/       # 协议层 - 通信协议
├── provider/       # 提供者层 - 能力提供
├── security/       # 安全层 - 安全服务
├── service/        # 服务层 - 统一服务
├── session/        # 会话层 - 会话管理
├── ui/             # UI层 - 用户界面
└── workflow/       # 工作流层 - 流程编排
```

---

## 二、核心层 (core)

### 2.1 引擎核心

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SceneEngine` | 场景引擎主接口 | createScene(), destroyScene(), getScene() |
| `SceneEngineImpl` | 引擎实现 | 实现所有引擎方法 |
| `SceneClient` | 场景客户端 | connect(), disconnect(), getScene() |
| `SceneClientImpl` | 客户端实现 | 实现所有客户端方法 |

### 2.2 Agent 核心

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SceneAgentCore` | Agent核心接口 | initialize(), mountSkill(), invokeCap() |
| `SceneAgentBridge` | Agent桥接实现 | 同时实现 SceneAgentCore + SceneAgent (SDK) |
| `SceneAgentState` | Agent状态枚举 | INITIALIZING, READY, RUNNING, STOPPED |

### 2.3 场景组

| 类名 | 职责 | 关键属性 |
|------|------|----------|
| `SceneGroupInfo` | 场景组信息 | groupId, sceneId, members, primaryMember |
| `SceneMemberInfo` | 成员信息 | memberId, userId, role, joinTime |

### 2.4 能力路由

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `CapRouter` | 能力路由器 | routeRequest(), registerHandler() |
| `CapAddress` | 能力地址 | toHex(), getSegment(), getCategory() |
| `CapRequest` | 能力请求 | getRequestId(), getCapId(), getParams() |
| `CapResponse` | 能力响应 | success(), failure(), getData() |

### 2.5 配置与上下文

| 类名 | 职责 | 关键属性 |
|------|------|----------|
| `SceneConfig` | 场景配置 | configId, properties |
| `SceneContext` | 场景上下文 | sceneId, agentId, skillConfigs |
| `SceneLifecycleManager` | 生命周期管理 | startSceneAgent(), stopSceneAgent() |

### 2.6 提供者注册

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `ProviderRegistry` | 提供者注册接口 | register(), getProvider() |
| `DefaultProviderRegistry` | 默认实现 | 实现所有注册方法 |
| `CapRegistryService` | 能力注册服务 | registerCapability(), getCapability() |
| `CapAddressAllocator` | 地址分配器 | allocate(), deallocate() |

---

## 三、技能层 (skill)

### 3.1 技能核心

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `RichSkill` | 充血技能模型 | getSceneCategory(), hasSelfDriveCapability() |
| `SkillInstance` | 技能实例 | start(), stop(), execute() |
| `SkillInstancePool` | 实例池 | acquire(), release() |
| `SkillService` | 技能服务 | install(), uninstall(), query() |

### 3.2 安装协调

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `InstallCoordinator` | 安装协调器 | install(), uninstall(), getProgress() |
| `InstallOptions` | 安装选项 | isForce(), isOffline() |
| `InstallSession` | 安装会话 | getSessionId(), getStatus() |

### 3.3 场景技能分类

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SceneSkillClassifier` | 分类器接口 | detectCategory(), calculateBusinessSemanticsScore() |
| `SceneSkillClassifierImpl` | 分类器实现 | 实现所有分类方法 |
| `SceneSkillCategory` | 分类枚举 | ABS, ASS, TBS, NOT_SCENE_SKILL |
| `SceneSkillClassificationResult` | 分类结果 | getCategory(), getBusinessSemanticsScore() |
| `MetadataCompat` | 元数据兼容层 | isSceneSkill(), getBusinessTags() |

### 3.4 工具系统

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `Tool` | 工具接口 | getName(), execute() |
| `ToolRegistry` | 工具注册表 | register(), execute() |
| `ToolOrchestrator` | 工具编排器 | orchestrate() |
| `SearchKnowledgeTool` | 知识检索工具 | execute() |
| `ListDocumentsTool` | 文档列表工具 | execute() |

### 3.5 知识库

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `KnowledgeBase` | 知识库模型 | getId(), getName(), getStatus() |
| `KnowledgeBaseService` | 知识库服务 | create(), search(), addDocument() |
| `Document` | 文档模型 | getId(), getContent(), getChunks() |
| `DocumentChunker` | 文档分块器 | chunk() |
| `VectorStore` | 向量存储 | store(), search() |
| `EmbeddingService` | 向量化服务 | embed() |

### 3.6 LLM 集成

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `LlmProvider` | LLM提供者接口 | chat(), embed() |
| `MockLlmProvider` | Mock实现 | 返回固定响应 |
| `FunctionCall` | 函数调用模型 | getName(), getArguments() |
| `StreamHandler` | 流处理器 | onNext(), onComplete(), onError() |

### 3.7 RAG 系统

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `RagPipeline` | RAG管道 | execute() |
| `RagContext` | RAG上下文 | getQuery(), getKnowledgeBase() |
| `RagResult` | RAG结果 | getAnswer(), getSources() |
| `LlmGenerator` | LLM生成器 | generate() |

### 3.8 对话服务

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `Conversation` | 对话模型 | getId(), getMessages() |
| `ConversationService` | 对话服务 | create(), sendMessage() |
| `Message` | 消息模型 | getRole(), getContent() |
| `StreamMessageHandler` | 流消息处理 | handle() |

### 3.9 权限服务

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `Permission` | 权限模型 | getType(), getResource() |
| `PermissionService` | 权限服务 | grant(), revoke(), check() |
| `KbPermission` | 知识库权限 | getKbId(), getAccessLevel() |

---

## 四、发现层 (discovery)

### 4.1 发现服务

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `DiscoveryService` | 发现服务接口 | discover(), register() |
| `UnifiedDiscoveryService` | 统一发现服务 | discover() |
| `CapabilityDiscoveryService` | 能力发现服务 | discoverCapabilities() |
| `DiscoveredItem` | 发现项 | getId(), getType(), getSource() |

### 4.2 发现提供者

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `MdnsDiscoveryProvider` | mDNS发现 | discover() |
| `UdpDiscoveryProvider` | UDP发现 | discover() |
| `SkillCenterDiscoveryProvider` | 技能中心发现 | discover() |

---

## 五、事件层 (event)

### 5.1 事件核心

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SceneEvent` | 场景事件基类 | getType(), getTimestamp() |
| `SceneEventPublisher` | 事件发布器 | publish() |
| `SceneEventType` | 事件类型枚举 | SCENE_CREATED, SKILL_INSTALLED, etc. |

### 5.2 具体事件

| 类名 | 职责 |
|------|------|
| `CapabilityEvent` | 能力事件 |
| `SkillEvent` | 技能事件 |
| `SceneAgentEvent` | Agent事件 |
| `PeerEvent` | 对等节点事件 |
| `LoginEvent` | 登录事件 |
| `SessionEvent` | 会话事件 |

---

## 六、LLM层 (llm)

### 6.1 LLM代理

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SceneEngineLlmProxy` | LLM代理 | createSession(), chat() |
| `LlmConnectionPool` | 连接池 | acquire(), release() |
| `LlmConnectionManager` | 连接管理 | createConnection() |
| `AgentSessionManager` | Agent会话管理 | createSession() |
| `UserLlmSessionManager` | 用户会话管理 | createSession() |

---

## 七、监控层 (monitor)

### 7.1 监控服务

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SceneMonitor` | 场景监控 | getStats(), getHealth() |
| `PerformanceMonitor` | 性能监控 | recordLatency() |
| `ServiceHealthMonitor` | 服务健康监控 | checkHealth() |
| `CapabilityStatusMonitor` | 能力状态监控 | getStatus() |

---

## 八、协议层 (protocol)

### 8.1 协议服务

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `DiscoveryProtocolAdapter` | 发现协议适配 | handleDiscovery() |
| `LoginProtocolAdapter` | 登录协议适配 | handleLogin() |
| `MdnsDiscoveryService` | mDNS服务 | start(), stop() |
| `UdpDiscoveryService` | UDP服务 | start(), stop() |
| `Peer` | 对等节点 | getId(), getAddress() |
| `Session` | 会话 | getId(), getState() |

---

## 九、提供者层 (provider)

### 9.1 核心提供者

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SystemProvider` | 系统提供者 | getSystemInfo(), getStats() |
| `NetworkProvider` | 网络提供者 | getNetworkStatus() |
| `SecurityProvider` | 安全提供者 | checkPermission() |
| `UserProvider` | 用户提供者 | getUserInfo() |
| `ConfigProvider` | 配置提供者 | getConfig() |
| `HealthProvider` | 健康提供者 | checkHealth() |

### 9.2 提供者实现

| 类名 | 职责 |
|------|------|
| `SystemProviderImpl` | 系统提供者实现 |
| `NetworkConfigProviderImpl` | 网络配置实现 |
| `UserProviderImpl` | 用户提供者实现 |
| `ConfigProviderImpl` | 配置提供者实现 |
| `HealthProviderImpl` | 健康提供者实现 |

---

## 十、会话层 (session)

### 10.1 会话管理

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SessionManager` | 会话管理接口 | create(), get(), invalidate() |
| `SessionManagerImpl` | 会话管理实现 | 实现所有方法 |
| `TokenManager` | Token管理接口 | createToken(), validateToken() |
| `TokenManagerImpl` | Token管理实现 | 实现所有方法 |
| `SessionContext` | 会话上下文 | getUserId(), getSceneId() |
| `SessionInfo` | 会话信息 | getSessionId(), getCreateTime() |

---

## 十一、工作流层 (workflow)

### 11.1 工作流核心

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `WorkflowEngine` | 工作流引擎 | execute(), pause(), resume() |
| `WorkflowEngineImpl` | 工作流引擎实现 | 实现所有方法 |
| `WorkflowDefinition` | 工作流定义 | getSteps(), getConditions() |
| `WorkflowContext` | 工作流上下文 | getVariables(), setVariable() |
| `WorkflowResult` | 工作流结果 | getStatus(), getData() |
| `WorkflowStep` | 工作流步骤 | getName(), execute() |

---

## 十二、安全层 (security)

### 12.1 安全服务

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `SecurityInterceptor` | 安全拦截器 | intercept() |
| `PermissionService` | 权限服务 | checkPermission() |
| `AuditService` | 审计服务 | log(), query() |
| `SecureSkillService` | 安全技能服务 | executeSecure() |

---

## 十三、UI层 (ui)

### 13.1 UI服务

| 类名 | 职责 | 关键方法 |
|------|------|----------|
| `NexusUiRegistry` | UI注册表 | register(), getRoute() |
| `NexusUiController` | UI控制器 | handleRequest() |
| `NexusUiConfig` | UI配置 | getMenus(), getRoutes() |
| `MenuConfig` | 菜单配置 | getItems() |
| `RouteConfig` | 路由配置 | getPath(), getHandler() |

---

## 十四、组件关系图

```
┌─────────────────────────────────────────────────────────────┐
│                    组件关系总览                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  SceneEngine                                                │
│      │                                                      │
│      ├──▶ SceneClient                                       │
│      │       └──▶ SceneGroupInfo                           │
│      │               └──▶ SceneMemberInfo                  │
│      │                                                      │
│      ├──▶ SceneAgentBridge                                  │
│      │       ├──▶ SceneAgentCore                           │
│      │       ├──▶ CapRegistryService                       │
│      │       └──▶ CapRouter                                │
│      │               └──▶ CapHandler                       │
│      │                                                      │
│      ├──▶ SkillService                                      │
│      │       ├──▶ InstallCoordinator                       │
│      │       │       └──▶ InstallStrategy                  │
│      │       ├──▶ RichSkill                                │
│      │       │       └──▶ SkillInstance                    │
│      │       └──▶ SceneSkillClassifier                     │
│      │                                                      │
│      ├──▶ KnowledgeBaseService                              │
│      │       ├──▶ VectorStore                              │
│      │       └──▶ EmbeddingService                         │
│      │                                                      │
│      ├──▶ LlmProvider                                       │
│      │       └──▶ SceneEngineLlmProxy                      │
│      │                                                      │
│      ├──▶ ToolRegistry                                      │
│      │       └──▶ ToolOrchestrator                         │
│      │                                                      │
│      ├──▶ WorkflowEngine                                    │
│      │       └──▶ WorkflowDefinition                       │
│      │                                                      │
│      ├──▶ SessionManager                                    │
│      │       └──▶ TokenManager                             │
│      │                                                      │
│      ├──▶ SecurityInterceptor                               │
│      │       └──▶ PermissionService                        │
│      │                                                      │
│      └──▶ DiscoveryService                                  │
│              └──▶ DiscoveryProvider                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 十五、缺失组件清单

### 15.1 决策层（完全缺失）

| 需要新增 | 包路径 | 职责 |
|----------|--------|------|
| DecisionEngine | core.decision | 决策引擎接口 |
| DecisionContext | core.decision | 决策上下文 |
| DecisionResult | core.decision | 决策结果 |
| DecisionMode | core.decision | 决策模式枚举 |
| MvelRuleEngine | skill.rule | MVEL规则引擎 |
| RuleScript | skill.rule | 规则脚本模型 |
| RuleRepository | skill.rule | 规则仓库 |
| LlmRuleGenerator | skill.rule | LLM规则生成器 |

### 15.2 需要增强的组件

| 组件 | 增强内容 |
|------|----------|
| SceneGroupInfo | 添加与 SceneAgent 的关联 |
| CapRouter | 支持决策引擎介入 |
| SceneAgentBridge | 支持决策模式选择 |
| ToolRegistry | 与 CapRegistry 关联 |
| KnowledgeBaseService | 支持知识分层 |

---

## 十六、版本历史

| 版本 | 日期 | 变更 |
|------|------|------|
| 2.3.1 | 2026-03-07 | 完整梳理代码结构，建立知识图谱 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
