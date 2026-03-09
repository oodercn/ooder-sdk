# Scene-Engine 分层重构协作任务

> **文档版本**: v1.0.0  
> **编写日期**: 2026-03-08  
> **目标**: 根据分层原则，将 Skills 和通用工程功能从 scene-engine 中分离

---

## 一、分层原则

### 1.1 核心原则

| 原则 | 说明 |
|------|------|
| **单一职责** | scene-engine 只负责场景生命周期管理 |
| **依赖倒置** | 依赖接口，不依赖实现 |
| **模块独立** | Skills、LLM、通用工程独立模块 |

### 1.2 目标分层

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           目标分层架构                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    scene-engine (场景引擎)                           │   │
│  │  职责: 场景生命周期管理、场景组管理、CAP 路由                         │   │
│  │  保留: core/, service/, event/, ui/                                  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    skill-sdk (技能 SDK)                              │   │
│  │  职责: Skill 生命周期、Skill 发现、Skill 实例管理                     │   │
│  │  移入: skill/, discovery/, knowledge/                                │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    llm-sdk (LLM SDK)                                 │   │
│  │  职责: LLM 代理、上下文管理、Agent 会话                               │   │
│  │  移入: llm/                                                          │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    common-engine (通用工程)                          │   │
│  │  职责: Provider、Monitor、Protocol、Session、Asset、Audit            │   │
│  │  移入: provider/, monitor/, protocol/, session/, asset/, audit/      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、协作任务清单

### 2.1 TASK-1: 创建 skill-sdk 模块 (P0)

**目标**: 将 scene-engine 中的 Skills 功能分离到独立模块

**移出文件清单**:

| 源路径 | 目标模块 | 目标路径 |
|--------|----------|----------|
| `scene-engine/.../skill/*` | skill-sdk | `skill-sdk/.../skill/*` |
| `scene-engine/.../discovery/*` | skill-sdk | `skill-sdk/.../discovery/*` |
| `scene-engine/.../knowledge/*` | skill-sdk | `skill-sdk/.../knowledge/*` |

**详细文件列表**:

```
skill/
├── adapter/
│   └── SkillSDKAdapter.java
├── audit/
│   ├── AuditEntry.java
│   ├── AuditLogQueryResult.java
│   ├── AuditLogger.java
│   └── AuditStats.java
├── classification/
│   ├── CapabilitySubType.java
│   ├── MetadataCompat.java
│   ├── SceneSkillCategory.java
│   ├── SceneSkillClassificationException.java
│   ├── SceneSkillClassificationResult.java
│   ├── SceneSkillClassifier.java
│   ├── SceneSkillClassifierImpl.java
│   └── WaitingSubState.java
├── config/
│   └── VectorStoreAutoConfiguration.java
├── contribution/
│   ├── impl/UserContributionServiceImpl.java
│   ├── BatchImportResult.java
│   ├── ContributionStats.java
│   ├── FileUploadRequest.java
│   ├── TextKnowledgeRequest.java
│   ├── UrlImportRequest.java
│   └── UserContributionService.java
├── conversation/
│   ├── impl/ConversationServiceImpl.java
│   ├── Conversation.java
│   ├── ConversationCreateRequest.java
│   ├── ConversationService.java
│   ├── ConversationStats.java
│   ├── Message.java
│   ├── MessageRequest.java
│   ├── MessageResponse.java
│   └── StreamMessageHandler.java
├── coordinator/
│   ├── InstallCoordinator.java
│   └── InstallOptions.java
├── exception/
│   ├── SkillException.java
│   ├── SkillNotRunningException.java
│   └── SkillStateTransitionException.java
├── importer/
│   ├── impl/BatchImportServiceImpl.java
│   ├── ArchiveImportRequest.java
│   ├── ArchiveType.java
│   ├── BatchImportService.java
│   ├── ImportResult.java
│   └── ImportTask.java
├── instance/
│   ├── SkillInstance.java
│   ├── SkillInstanceFactory.java
│   └── SkillInstancePool.java
├── knowledge/
│   ├── impl/
│   │   ├── FixedSizeDocumentChunker.java
│   │   ├── KnowledgeBaseServiceImpl.java
│   │   └── KnowledgeCapabilityImpl.java
│   ├── Document.java
│   ├── DocumentChunk.java
│   ├── DocumentChunker.java
│   ├── DocumentCreateRequest.java
│   ├── IndexStatus.java
│   ├── KnowledgeBase.java
│   ├── KnowledgeBaseApi.java
│   ├── KnowledgeBaseCreateRequest.java
│   ├── KnowledgeBaseService.java
│   ├── KnowledgeBaseUpdateRequest.java
│   ├── KnowledgeCapability.java
│   ├── KnowledgeSearchRequest.java
│   └── KnowledgeSearchResult.java
├── llm/
│   ├── impl/AbstractLlmProvider.java
│   ├── EnhancedLlmProvider.java
│   ├── FunctionCall.java
│   ├── LlmProvider.java
│   └── StreamHandler.java
├── model/
│   └── RichSkill.java
├── permission/
│   ├── impl/PermissionServiceImpl.java
│   ├── GrantPermissionRequest.java
│   ├── KbPermission.java
│   ├── Permission.java
│   └── PermissionService.java
├── proxy/
│   └── SkillInstallProxy.java
├── HttpClientProvider.java
├── LlmProvider.java
├── MockHttpClientProvider.java
├── MockLlmProvider.java
├── MockSchedulerProvider.java
├── MockStorageProvider.java
├── SchedulerProvider.java
├── SkillClient.java
├── SkillProviderRegistry.java
├── SkillRuntimeStatus.java
├── SkillService.java
├── StorageProvider.java
├── StreamHandler.java
└── UserSkillClient.java

discovery/
├── api/
│   ├── DiscoveryRequest.java
│   ├── DiscoveryResult.java
│   └── DiscoveryService.java
├── cache/
│   ├── CacheManager.java
│   ├── JsonFileCacheManager.java
│   └── SimpleCacheManager.java
├── coordinator/
│   └── DiscoveryCoordinator.java
├── dependency/
│   └── DependencyManager.java
├── impl/
│   └── CapabilityDiscoveryServiceImpl.java
├── install/
│   └── InstallTaskManager.java
├── integrity/
│   └── IntegrityChecker.java
├── internal/
│   ├── InternalDiscoveryService.java
│   └── InternalDiscoveryServiceImpl.java
├── provider/
│   ├── MdnsDiscoveryProvider.java
│   ├── SkillCenterDiscoveryProvider.java
│   └── UdpDiscoveryProvider.java
├── storage/
│   ├── MultiRepoConfigManager.java
│   └── VfsPathStrategy.java
├── CapabilityDetail.java
├── CapabilityDiscoveryService.java
├── DiscoveredItem.java
├── DiscoveryConfig.java
├── DiscoveryProvider.java
├── DiscoveryQuery.java
├── DiscoveryScope.java
├── DiscoveryType.java
├── SceneDetail.java
├── SyncResult.java
├── UnifiedDiscoveryService.java
└── UnifiedSkillRegistry.java
```

**工作量**: 5人天

---

### 2.2 TASK-2: 创建 llm-sdk 模块 (P0)

**目标**: 将 scene-engine 中的 LLM 功能分离到独立模块

**移出文件清单**:

| 源路径 | 目标模块 | 目标路径 |
|--------|----------|----------|
| `scene-engine/.../llm/*` | llm-sdk | `llm-sdk/.../llm/*` |

**详细文件列表**:

```
llm/
├── command/
│   ├── A2ACommand.java
│   ├── A2ACommandResponse.java
│   ├── A2ACommandType.java
│   ├── AgentInfo.java
│   ├── CommandBody.java
│   ├── CommandHeader.java
│   ├── CommandMetadata.java
│   ├── ContextReference.java
│   ├── ContextTransfer.java
│   └── SecurityInfo.java
├── context/
│   ├── ContextTransferException.java
│   ├── ContextTransferHandler.java
│   ├── KnowledgeContext.java
│   ├── LlmContextRegistry.java
│   ├── LlmSceneContext.java
│   ├── NlpComponentContext.java
│   ├── NlpContext.java
│   ├── SecurityContext.java
│   └── UserContext.java
└── proxy/
    ├── agent/
    │   ├── AgentCreationOptions.java
    │   ├── AgentLlmQuota.java
    │   ├── AgentLlmSessionContext.java
    │   ├── AgentLlmSessionHandle.java
    │   └── AgentSessionManager.java
    ├── common/
    │   ├── AgentState.java
    │   ├── LlmProxyException.java
    │   └── PoolState.java
    ├── connection/
    │   ├── LlmConnection.java
    │   ├── LlmConnectionManager.java
    │   ├── LlmConnectionPool.java
    │   └── LlmConnectionPoolKey.java
    ├── lifecycle/
    │   ├── AgentLifecycleListener.java
    │   └── LoggingAgentLifecycleListener.java
    ├── user/
    │   ├── UserLlmQuota.java
    │   ├── UserLlmSessionContext.java
    │   └── UserLlmSessionManager.java
    ├── LlmProxyMonitor.java
    └── SceneEngineLlmProxy.java
```

**工作量**: 3人天

---

### 2.3 TASK-3: 创建 common-engine 模块 (P1)

**目标**: 将 scene-engine 中的通用工程功能分离到独立模块

**移出文件清单**:

| 源路径 | 目标模块 | 目标路径 |
|--------|----------|----------|
| `scene-engine/.../provider/*` | common-engine | `common-engine/.../provider/*` |
| `scene-engine/.../monitor/*` | common-engine | `common-engine/.../monitor/*` |
| `scene-engine/.../protocol/*` | common-engine | `common-engine/.../protocol/*` |
| `scene-engine/.../session/*` | common-engine | `common-engine/.../session/*` |
| `scene-engine/.../asset/*` | common-engine | `common-engine/.../asset/*` |
| `scene-engine/.../audit/*` | common-engine | `common-engine/.../audit/*` |
| `scene-engine/.../engine/*` | common-engine | `common-engine/.../engine/*` |

**详细文件列表**:

```
provider/
├── model/
│   ├── agent/
│   │   ├── CommandStatsData.java
│   │   ├── EndAgent.java
│   │   ├── NetworkStatusData.java
│   │   └── TestCommandResult.java
│   ├── config/
│   │   ├── AdvancedConfig.java
│   │   ├── BasicConfig.java
│   │   ├── NetworkConfig.java
│   │   ├── SecurityConfig.java
│   │   ├── ServiceConfig.java
│   │   ├── SystemConfig.java
│   │   └── TerminalConfig.java
│   ├── health/
│   │   ├── HealthCheckResult.java
│   │   ├── HealthCheckSchedule.java
│   │   ├── HealthReport.java
│   │   └── ServiceCheckResult.java
│   ├── network/
│   │   ├── CommandResult.java
│   │   ├── ConnectionStatus.java
│   │   ├── IPAddress.java
│   │   ├── IPBlacklist.java
│   │   ├── NetworkSetting.java
│   │   └── SystemStatus.java
│   ├── protocol/
│   │   ├── ProtocolCommandResult.java
│   │   └── ProtocolHandler.java
│   ├── share/
│   │   ├── ReceivedSkill.java
│   │   └── SharedSkill.java
│   └── user/
│       ├── Permission.java
│       ├── SecurityLog.java
│       ├── UserInfo.java
│       └── UserStatus.java
├── AccessControl.java
├── AgentProvider.java
├── AuditExportResult.java
├── AutoScalePolicy.java
├── BaseProvider.java
├── ConfigExportResult.java
├── ConfigGroup.java
├── ConfigHistory.java
├── ConfigItem.java
├── ConfigProvider.java
├── DeviceManagementProvider.java
├── HealthProvider.java
├── HeartbeatProvider.java
├── HostingExtensionProvider.java
├── HostingProvider.java
├── LogEntry.java
├── LogExportResult.java
├── LogProvider.java
├── LogQuery.java
├── LogStatistics.java
├── NetworkConfigProvider.java
├── NetworkProvider.java
├── ProtocolProvider.java
├── ResourceUsage.java
├── SceneProvider.java
├── SecurityPolicy.java
├── SecurityProvider.java
├── SecurityStats.java
├── SecurityStatus.java
├── ServiceEndpoint.java
├── ServiceInfo.java
├── SkillShareProvider.java
├── SystemCommandResult.java
├── SystemInfo.java
├── SystemLoad.java
├── SystemProvider.java
├── SystemStatus.java
├── ThreatInfo.java
├── UserProvider.java
├── UserSettingsProvider.java
└── Volume.java

monitor/
├── CapabilityStatusMonitor.java
├── PerformanceMonitor.java
├── SceneConfigManager.java
├── SceneEventManager.java
├── SceneFlowManager.java
├── SceneLogManager.java
├── SceneMonitor.java
└── ServiceHealthMonitor.java

protocol/
├── impl/
│   ├── DiscoveryProtocolAdapterImpl.java
│   └── LoginProtocolAdapterImpl.java
├── CompanyCenterConnector.java
├── DepartmentShareManager.java
├── DiscoveryCoordinator.java
├── DiscoveryEventListener.java
├── DiscoveryMessageCodec.java
├── DiscoveryProtocolAdapter.java
├── DiscoveryRequest.java
├── DiscoveryResult.java
├── EngineProtocolProvider.java
├── LoginProtocolAdapter.java
├── LoginRequest.java
├── LoginResult.java
├── MdnsDiscoveryService.java
├── OoderServiceRegistrar.java
├── Peer.java
├── PersonalNetworkManager.java
├── Session.java
└── UdpDiscoveryService.java

session/
├── impl/
│   ├── SessionManagerImpl.java
│   └── TokenManagerImpl.java
├── SessionContext.java
├── SessionInfo.java
├── SessionManager.java
├── TokenInfo.java
└── TokenManager.java

asset/
├── AssetGovernance.java
├── AssetGovernanceImpl.java
├── DataAsset.java
├── DataAssetBuilder.java
├── DataAssetManager.java
├── DataAssetManagerImpl.java
├── DeviceAssetBuilder.java
├── DeviceAssetManager.java
├── DeviceAssetManagerImpl.java
├── DigitalAsset.java
├── DigitalAssetBuilder.java
└── DigitalAssetImpl.java

audit/
├── AuditService.java
└── AuditStats.java

engine/
├── Engine.java
├── EngineManager.java
├── EngineStats.java
├── EngineStatus.java
└── EngineType.java
```

**工作量**: 5人天

---

### 2.4 TASK-4: 重构 scene-engine 依赖 (P0)

**目标**: 移除上述模块后，重构 scene-engine 的依赖关系

**保留模块**:

```
scene-engine/
├── core/                    # 核心层
│   ├── decision/            # 决策引擎
│   ├── driver/              # 驱动
│   ├── impl/                # 核心实现
│   ├── init/                # 初始化
│   ├── provider/            # 场景相关 Provider
│   ├── secure/              # 安全
│   ├── security/            # 安全服务
│   ├── service/             # 核心服务
│   └── skill/               # 场景 Skill 服务入口
├── service/                 # 业务服务层
│   ├── push/                # 推送服务
│   ├── reminder/            # 提醒服务
│   └── journal/             # 日志服务
├── event/                   # 事件层
├── ui/                      # UI 层
├── config/                  # 配置
└── security/                # 安全客户端
```

**新增依赖**:

```xml
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-sdk</artifactId>
        <version>2.4.0</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>2.4.0</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>common-engine</artifactId>
        <version>2.4.0</version>
    </dependency>
</dependencies>
```

**工作量**: 3人天

---

## 三、依赖关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           模块依赖关系                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  scene-engine (场景引擎)                                                     │
│       │                                                                     │
│       ├──▶ skill-sdk (技能 SDK)                                             │
│       │        │                                                            │
│       │        └──▶ agent-sdk-core                                          │
│       │                                                                     │
│       ├──▶ llm-sdk (LLM SDK)                                                │
│       │        │                                                            │
│       │        └──▶ agent-sdk-core                                          │
│       │                                                                     │
│       └──▶ common-engine (通用工程)                                          │
│                │                                                            │
│                └──▶ agent-sdk-core                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、执行计划

### 4.1 阶段一：模块创建 (Week 1)

| 任务 | 工作量 | 负责团队 |
|------|--------|----------|
| TASK-1: 创建 skill-sdk | 5人天 | SDK Team |
| TASK-2: 创建 llm-sdk | 3人天 | SDK Team |

### 4.2 阶段二：模块迁移 (Week 2)

| 任务 | 工作量 | 负责团队 |
|------|--------|----------|
| TASK-3: 创建 common-engine | 5人天 | Common Team |
| TASK-4: 重构 scene-engine | 3人天 | Scene Team |

### 4.3 阶段三：集成验证 (Week 3)

| 任务 | 工作量 | 负责团队 |
|------|--------|----------|
| 集成测试 | 3人天 | QA Team |
| 文档更新 | 2人天 | All Teams |

---

## 五、风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 循环依赖 | 高 | 提前规划依赖关系，使用接口隔离 |
| API 不兼容 | 中 | 提供适配器层，保持向后兼容 |
| 测试覆盖不足 | 中 | 迁移前补充单元测试 |

---

## 六、验收标准

### 6.1 skill-sdk

- [ ] 所有 skill 相关类已迁移
- [ ] 独立编译通过
- [ ] 单元测试覆盖 > 80%

### 6.2 llm-sdk

- [ ] 所有 llm 相关类已迁移
- [ ] 独立编译通过
- [ ] 单元测试覆盖 > 80%

### 6.3 common-engine

- [ ] 所有通用工程类已迁移
- [ ] 独立编译通过
- [ ] 单元测试覆盖 > 80%

### 6.4 scene-engine

- [ ] 依赖新模块编译通过
- [ ] 功能测试通过
- [ ] 无循环依赖

---

## 七、联系方式

- **Scene-Engine Team**: scene-engine@ooder.cn
- **SDK Team**: sdk@ooder.cn
- **Common Team**: common@ooder.cn

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-08
