# Agent-SDK 协作文档

> **文档版本**: 1.0.0  
> **编写日期**: 2026-03-09  
> **协作方**: Scene-Engine Team ↔ Agent-SDK Team  
> **来源文档**: collaboration-spec-comparison.md

---

## 一、协作背景

Scene-Engine 需要扩展 Agent 协作能力以支持场景安装、激活和多Agent协作。本文档定义 Agent-SDK 需要提供的接口扩展。

---

## 二、任务清单

### 2.1 P0 优先级（阻塞任务）

| 任务ID | 任务名称 | 工作量 | 说明 |
|--------|---------|--------|------|
| **AGENT-SDK-001** | 场景协作命令 | 3天 | SCENE_INSTALL/ACTIVATE等 |

### 2.2 P1 优先级（重要任务）

| 任务ID | 任务名称 | 工作量 | 说明 |
|--------|---------|--------|------|
| **AGENT-SDK-002** | SceneCollaborationApi | 5天 | 加入/离开场景组+事件订阅 |
| **AGENT-SDK-003** | CapabilityInvocationApi | 3天 | 能力发现+调用+状态 |
| **AGENT-SDK-004** | LLMCollaborationApi | 5天 | LLM能力分配+对话 |
| **AGENT-SDK-005** | 安装状态同步 | 2天 | 状态广播+订阅+同步 |

### 2.3 P2 优先级（增强任务）

| 任务ID | 任务名称 | 工作量 | 说明 |
|--------|---------|--------|------|
| **AGENT-SDK-006** | LLM服务发现 | 2天 | Provider发现+端点选择 |

---

## 三、接口详细设计

### 3.1 AGENT-SDK-001: 场景协作命令

**需求来源**: Agent 间需要传递场景安装/激活等协作命令

**接口设计**:

```java
package net.ooder.sdk.agent.command;

import net.ooder.sdk.a2a.A2ACommand;
import net.ooder.sdk.a2a.A2ACommandResponse;

/**
 * 场景协作命令定义
 */
public class SceneCollaborationCommands {

    public static final String SCENE_INSTALL = "scene.install";
    public static final String SCENE_ACTIVATE = "scene.activate";
    public static final String SCENE_DEACTIVATE = "scene.deactivate";
    public static final String SCENE_UNINSTALL = "scene.uninstall";
    public static final String SCENE_STATUS = "scene.status";
    public static final String SCENE_SYNC = "scene.sync";

    /**
     * 创建场景安装命令
     */
    public static A2ACommand createInstallCommand(String sceneId, String targetAgentId, Map<String, Object> params) {
        return A2ACommand.builder()
            .commandType(SCENE_INSTALL)
            .targetAgent(targetAgentId)
            .body(Map.of(
                "sceneId", sceneId,
                "params", params
            ))
            .build();
    }

    /**
     * 创建场景激活命令
     */
    public static A2ACommand createActivateCommand(String sceneId, String targetAgentId, MemberRole role) {
        return A2ACommand.builder()
            .commandType(SCENE_ACTIVATE)
            .targetAgent(targetAgentId)
            .body(Map.of(
                "sceneId", sceneId,
                "role", role.name()
            ))
            .build();
    }

    /**
     * 创建场景状态同步命令
     */
    public static A2ACommand createSyncCommand(String sceneId, String targetAgentId, SceneStatus status) {
        return A2ACommand.builder()
            .commandType(SCENE_SYNC)
            .targetAgent(targetAgentId)
            .body(Map.of(
                "sceneId", sceneId,
                "status", status
            ))
            .build();
    }
}
```

**命令处理器**:

```java
/**
 * 场景协作命令处理器
 */
public interface SceneCommandHandler {

    /**
     * 处理安装命令
     */
    A2ACommandResponse handleInstall(A2ACommand command);

    /**
     * 处理激活命令
     */
    A2ACommandResponse handleActivate(A2ACommand command);

    /**
     * 处理停用命令
     */
    A2ACommandResponse handleDeactivate(A2ACommand command);

    /**
     * 处理卸载命令
     */
    A2ACommandResponse handleUninstall(A2ACommand command);

    /**
     * 处理状态查询命令
     */
    A2ACommandResponse handleStatus(A2ACommand command);

    /**
     * 处理同步命令
     */
    A2ACommandResponse handleSync(A2ACommand command);
}
```

**依赖方**:
- Scene-Engine: ENGINE-001 场景技能生命周期管理
- Scene-Engine: ENGINE-004 激活流程引擎

---

### 3.2 AGENT-SDK-002: SceneCollaborationApi

**需求来源**: Agent 需要加入/离开场景组，订阅场景事件

**接口设计**:

```java
package net.ooder.sdk.agent.scene;

import java.util.List;
import java.util.function.Consumer;

/**
 * 场景协作 API
 */
public interface SceneCollaborationApi {

    /**
     * 加入场景组
     * @param sceneId 场景ID
     * @param role 成员角色
     * @return 加入结果
     */
    SceneJoinResult joinScene(String sceneId, MemberRole role);

    /**
     * 离开场景组
     * @param sceneId 场景ID
     * @return 离开结果
     */
    SceneLeaveResult leaveScene(String sceneId);

    /**
     * 获取场景组成员列表
     * @param sceneId 场景ID
     * @return 成员列表
     */
    List<SceneMember> getSceneMembers(String sceneId);

    /**
     * 订阅场景事件
     * @param sceneId 场景ID
     * @param eventType 事件类型
     * @param handler 事件处理器
     * @return 订阅ID
     */
    String subscribeSceneEvent(String sceneId, SceneEventType eventType, Consumer<SceneEvent> handler);

    /**
     * 取消订阅
     * @param subscriptionId 订阅ID
     */
    void unsubscribeSceneEvent(String subscriptionId);

    /**
     * 发布场景事件
     * @param sceneId 场景ID
     * @param event 事件
     */
    void publishSceneEvent(String sceneId, SceneEvent event);

    /**
     * 获取场景状态
     * @param sceneId 场景ID
     * @return 场景状态
     */
    SceneState getSceneState(String sceneId);
}
```

**数据模型**:

```java
public class SceneJoinResult {
    private boolean success;
    private String sceneId;
    private String memberId;
    private MemberRole role;
    private String errorMessage;
}

public class SceneMember {
    private String memberId;
    private String agentId;
    private String sceneId;
    private MemberRole role;
    private MemberStatus status;
    private long joinTime;
}

public enum SceneEventType {
    MEMBER_JOINED,
    MEMBER_LEFT,
    MEMBER_ACTIVATED,
    MEMBER_DEACTIVATED,
    SCENE_ACTIVATED,
    SCENE_DEACTIVATED,
    STATUS_CHANGED
}

public class SceneEvent {
    private String eventId;
    private String sceneId;
    private SceneEventType eventType;
    private String sourceMemberId;
    private Object payload;
    private long timestamp;
}
```

**依赖方**:
- Scene-Engine: ENGINE-004 激活流程引擎
- Scene-Engine: ENGINE-007 安装状态持久化

---

### 3.3 AGENT-SDK-003: CapabilityInvocationApi

**需求来源**: Agent 需要发现和调用其他 Agent 的能力

**接口设计**:

```java
package net.ooder.sdk.agent.capability;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 能力调用 API
 */
public interface CapabilityInvocationApi {

    /**
     * 发现能力
     * @param query 查询条件
     * @return 能力列表
     */
    List<CapabilityInfo> discoverCapabilities(CapabilityQuery query);

    /**
     * 获取能力详情
     * @param capabilityId 能力ID
     * @return 能力详情
     */
    CapabilityDetail getCapabilityDetail(String capabilityId);

    /**
     * 调用能力
     * @param request 调用请求
     * @return 调用结果
     */
    CompletableFuture<CapabilityResponse> invokeCapability(CapabilityRequest request);

    /**
     * 获取能力调用状态
     * @param invocationId 调用ID
     * @return 调用状态
     */
    CapabilityInvocationStatus getInvocationStatus(String invocationId);

    /**
     * 取消能力调用
     * @param invocationId 调用ID
     * @return 是否成功
     */
    boolean cancelInvocation(String invocationId);

    /**
     * 注册能力
     * @param capability 能力信息
     * @return 注册结果
     */
    CapabilityRegistration registerCapability(CapabilityInfo capability);

    /**
     * 注销能力
     * @param capabilityId 能力ID
     */
    void unregisterCapability(String capabilityId);
}
```

**数据模型**:

```java
public class CapabilityQuery {
    private String sceneId;
    private String skillId;
    private String capabilityType;
    private List<String> tags;
}

public class CapabilityInfo {
    private String capabilityId;
    private String skillId;
    private String name;
    private String description;
    private String capabilityType;
    private List<String> tags;
    private Map<String, Object> parametersSchema;
}

public class CapabilityRequest {
    private String capabilityId;
    private String sourceAgentId;
    private String targetAgentId;
    private Map<String, Object> parameters;
    private int timeout;
}

public class CapabilityResponse {
    private String invocationId;
    private boolean success;
    private Object result;
    private String error;
    private long duration;
}
```

**依赖方**:
- Scene-Engine: ENGINE-006 工具调用注册中心
- Skills: SKILL-MOD-001 skill-llm-conversation扩展

---

### 3.4 AGENT-SDK-004: LLMCollaborationApi

**需求来源**: 多 Agent 需要共享 LLM 能力和对话

**接口设计**:

```java
package net.ooder.sdk.agent.llm;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * LLM协作 API
 */
public interface LLMCollaborationApi {

    /**
     * 分配LLM能力
     * @param sceneId 场景ID
     * @param agentId Agent ID
     * @param config LLM配置
     * @return 分配结果
     */
    LlmAllocationResult allocateLlm(String sceneId, String agentId, LlmConfig config);

    /**
     * 释放LLM能力
     * @param allocationId 分配ID
     */
    void releaseLlm(String allocationId);

    /**
     * 获取LLM分配状态
     * @param sceneId 场景ID
     * @return 分配状态
     */
    List<LlmAllocation> getLlmAllocations(String sceneId);

    /**
     * 创建协作对话
     * @param sceneId 场景ID
     * @param participants 参与者列表
     * @return 对话ID
     */
    String createCollaborativeConversation(String sceneId, List<String> participants);

    /**
     * 发送协作消息
     * @param conversationId 对话ID
     * @param message 消息
     * @return 响应
     */
    CompletableFuture<CollaborativeResponse> sendCollaborativeMessage(String conversationId, CollaborativeMessage message);

    /**
     * 订阅协作对话
     * @param conversationId 对话ID
     * @param handler 消息处理器
     * @return 订阅ID
     */
    String subscribeConversation(String conversationId, CollaborativeMessageHandler handler);

    /**
     * 取消订阅
     * @param subscriptionId 订阅ID
     */
    void unsubscribeConversation(String subscriptionId);
}
```

**数据模型**:

```java
public class LlmAllocation {
    private String allocationId;
    private String sceneId;
    private String agentId;
    private String provider;
    private String model;
    private LlmQuota quota;
    private LlmAllocationStatus status;
}

public class LlmQuota {
    private int maxTokens;
    private int maxRequests;
    private int usedTokens;
    private int usedRequests;
}

public class CollaborativeMessage {
    private String messageId;
    private String conversationId;
    private String senderId;
    private String content;
    private MessageType type;
    private long timestamp;
}

public class CollaborativeResponse {
    private String messageId;
    private String responderId;
    private String content;
    private List<ToolCall> toolCalls;
}
```

**依赖方**:
- Scene-Engine: ENGINE-002 LLM上下文隔离管理
- LLM-SDK: LLM-SDK-001 ToolCallingApi

---

### 3.5 AGENT-SDK-005: 安装状态同步

**需求来源**: 多 Agent 需要同步安装状态

**接口设计**:

```java
package net.ooder.sdk.agent.installation;

import java.util.List;
import java.util.function.Consumer;

/**
 * 安装状态同步 API
 */
public interface InstallationSyncApi {

    /**
     * 广播安装状态
     * @param installId 安装ID
     * @param status 状态
     */
    void broadcastInstallationStatus(String installId, InstallationStatus status);

    /**
     * 订阅安装状态
     * @param installId 安装ID
     * @param handler 状态处理器
     * @return 订阅ID
     */
    String subscribeInstallationStatus(String installId, Consumer<InstallationStatusUpdate> handler);

    /**
     * 取消订阅
     * @param subscriptionId 订阅ID
     */
    void unsubscribeInstallationStatus(String subscriptionId);

    /**
     * 获取安装状态
     * @param installId 安装ID
     * @return 安装状态
     */
    InstallationStatus getInstallationStatus(String installId);

    /**
     * 同步安装状态
     * @param installId 安装ID
     * @return 同步结果
     */
    InstallationSyncResult syncInstallationStatus(String installId);

    /**
     * 获取所有活跃安装
     * @param sceneId 场景ID（可选）
     * @return 安装列表
     */
    List<InstallationInfo> getActiveInstallations(String sceneId);
}
```

**数据模型**:

```java
public class InstallationStatusUpdate {
    private String installId;
    private String sceneId;
    private InstallationStatus status;
    private String currentStep;
    private int progress;
    private String message;
    private long timestamp;
    private String sourceAgentId;
}

public class InstallationStatus {
    private String installId;
    private String sceneId;
    private InstallationPhase phase;
    private String currentStep;
    private int totalSteps;
    private int completedSteps;
    private List<StepResult> stepResults;
    private Map<String, Object> context;
}

public enum InstallationPhase {
    INITIALIZING,
    INSTALLING,
    CONFIGURING,
    ACTIVATING,
    COMPLETED,
    FAILED,
    CANCELLED
}
```

**依赖方**:
- Scene-Engine: ENGINE-007 安装状态持久化
- Skills: SKILL-NEW-001 skill-scene-installer

---

### 3.6 AGENT-SDK-006: LLM服务发现

**需求来源**: Agent 需要发现可用的 LLM Provider

**接口设计**:

```java
package net.ooder.sdk.agent.discovery;

import java.util.List;

/**
 * LLM服务发现 API
 */
public interface LlmDiscoveryApi {

    /**
     * 发现LLM Provider
     * @param query 查询条件
     * @return Provider列表
     */
    List<LlmProviderInfo> discoverProviders(LlmProviderQuery query);

    /**
     * 获取Provider详情
     * @param providerId Provider ID
     * @return Provider详情
     */
    LlmProviderDetail getProviderDetail(String providerId);

    /**
     * 选择最佳端点
     * @param providerId Provider ID
     * @param criteria 选择条件
     * @return 端点
     */
    LlmEndpoint selectEndpoint(String providerId, EndpointSelectionCriteria criteria);

    /**
     * 健康检查
     * @param endpointId 端点ID
     * @return 健康状态
     */
    EndpointHealth checkHealth(String endpointId);

    /**
     * 注册Provider
     * @param provider Provider信息
     * @return 注册结果
     */
    ProviderRegistration registerProvider(LlmProviderInfo provider);

    /**
     * 注销Provider
     * @param providerId Provider ID
     */
    void unregisterProvider(String providerId);
}
```

**数据模型**:

```java
public class LlmProviderInfo {
    private String providerId;
    private String name;
    private String providerType;
    private List<LlmEndpoint> endpoints;
    private List<String> supportedModels;
    private ProviderStatus status;
}

public class LlmEndpoint {
    private String endpointId;
    private String url;
    private String region;
    private int priority;
    private int load;
    private EndpointStatus status;
}

public class EndpointSelectionCriteria {
    private String preferredRegion;
    private int maxLatency;
    private int minAvailability;
    private List<String> requiredModels;
}
```

**依赖方**:
- Scene-Engine: ENGINE-002 LLM上下文隔离管理
- LLM-SDK: LLM-SDK-004 DegradationApi

---

## 四、协作时间线

| 阶段 | 时间 | Agent-SDK 任务 | Scene-Engine 依赖 |
|------|------|-----------------|-------------------|
| **Phase 1** | Week 1-2 | 场景协作命令 | ENGINE-001 |
| **Phase 2** | Week 3-4 | SceneCollaborationApi, CapabilityInvocationApi | ENGINE-004,006 |
| **Phase 3** | Week 5-6 | LLMCollaborationApi, 安装状态同步, LLM服务发现 | ENGINE-002,007 |

---

## 五、验收标准

### 5.1 场景协作命令

- [ ] 命令创建正确
- [ ] 命令处理正常
- [ ] 响应格式正确
- [ ] 单元测试覆盖 > 80%

### 5.2 SceneCollaborationApi

- [ ] 加入/离开场景组正常
- [ ] 事件订阅/发布正常
- [ ] 状态获取正确
- [ ] 单元测试覆盖 > 80%

### 5.3 CapabilityInvocationApi

- [ ] 能力发现正常
- [ ] 能力调用正常
- [ ] 状态获取正确
- [ ] 单元测试覆盖 > 80%

### 5.4 LLMCollaborationApi

- [ ] LLM分配/释放正常
- [ ] 协作对话正常
- [ ] 消息订阅正常
- [ ] 单元测试覆盖 > 80%

### 5.5 安装状态同步

- [ ] 状态广播正常
- [ ] 订阅/取消订阅正常
- [ ] 状态同步正确
- [ ] 单元测试覆盖 > 80%

### 5.6 LLM服务发现

- [ ] Provider发现正常
- [ ] 端点选择正确
- [ ] 健康检查正常
- [ ] 单元测试覆盖 > 80%

---

## 六、联系方式

- **Scene-Engine Team**: scene-engine@ooder.cn
- **Agent-SDK Team**: agent-sdk@ooder.cn

---

**文档状态**: 待确认  
**下一步**: Agent-SDK Team 确认接口设计后启动开发
