# SDK 3.0.0 Agent 实现公共 API 暴露协作需求

## 协作状态：✅ SDK 已响应

**响应日期**: 2026-03-25  
**SDK 版本**: 3.0.0  
**状态**: 已完成

---

## 背景

MVP 项目在升级到 SDK 3.0.0 版本后，发现 Agent 相关接口（`SceneAgent`、`EndAgent`、`WorkerAgent`、`RouteAgent`、`McpAgent`）非常复杂，包含大量方法需要实现。

经 scene-engine 团队深入分析，**发现 SDK 已经提供了完整的 Agent 实现**，但这些实现类未作为公共 API 正式暴露，导致 MVP 项目不知道可以直接使用。

---

## SDK 团队响应结果

### ✅ 需求 1：公共 API 状态确认 - 已完成

SDK 团队采用 **方案 B**：添加 `@PublicAPI` 注解

| 类名 | 状态 | 说明 |
|------|------|------|
| `AgentFactoryImpl` | ✅ 已添加 `@PublicAPI` | 公共 API |
| `WorkerAgentImpl` | ✅ 已添加 `@PublicAPI` | 公共 API |
| `SceneAgentImpl` | ✅ 已添加 `@PublicAPI` | 公共 API |
| `EndAgentImpl` | ✅ 已添加 `@PublicAPI` | 公共 API |
| `RouteAgentImpl` | ✅ 已添加 `@PublicAPI` | 公共 API |
| `McpAgentImpl` | ✅ 已添加 `@PublicAPI` | 公共 API |

**新增注解**:
- [PublicAPI.java](file:///e:/github/ooder-sdk/agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/api/PublicAPI.java) - 公共 API 标识注解

### ✅ 需求 2：抽象基类 - 已完成

SDK 团队在 `net.ooder.sdk.api.agent.support` 包中创建了抽象基类：

| 抽象基类 | 路径 | 状态 |
|----------|------|------|
| `AbstractAgent` | [AbstractAgent.java](file:///e:/github/ooder-sdk/agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/api/agent/support/AbstractAgent.java) | ✅ 已创建 |
| `AbstractWorkerAgent` | [AbstractWorkerAgent.java](file:///e:/github/ooder-sdk/agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/api/agent/support/AbstractWorkerAgent.java) | ✅ 已创建 |
| `AbstractSceneAgent` | [AbstractSceneAgent.java](file:///e:/github/ooder-sdk/agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/api/agent/support/AbstractSceneAgent.java) | ✅ 已创建 |

**注意**: `AbstractEndAgent`、`AbstractRouteAgent`、`AbstractMcpAgent` 尚未创建，但可直接使用实现类。

### ⏳ 需求 3：Spring Boot Starter - 待完成（SDK 团队）

此需求优先级为 P2，**SDK 团队** 尚未实现。

### ⏳ 需求 4：文档更新 - 待完成（SDK 团队）

此需求优先级为 P1，**SDK 团队** 尚未实现。

---

## MVP 下一步行动

### 立即可执行的操作

MVP 项目现在可以：

1. **直接使用实现类**：
```java
import net.ooder.sdk.core.agent.factory.AgentFactoryImpl;
import net.ooder.sdk.core.agent.impl.SceneAgentImpl;

AgentFactory factory = new AgentFactoryImpl();
SceneAgent agent = factory.createSceneAgent("mvp-scene", "mvp-agent");
```

2. **继承抽象基类扩展**：
```java
import net.ooder.sdk.api.agent.support.AbstractWorkerAgent;

public class MyWorkerAgent extends AbstractWorkerAgent {
    public MyWorkerAgent(String sceneId, String workerName, String skillId) {
        super(sceneId, workerName, skillId, Arrays.asList("cap1", "cap2"));
    }
    
    @Override
    public CompletableFuture<Object> execute(String capId, Map<String, Object> params) {
        // 自定义实现
    }
}
```

3. **使用 OoderSDK 入口类**：
```java
OoderSDK sdk = OoderSDK.builder()
    .agentId("mvp-agent")
    .agentName("MVP Agent")
    .build();

EndAgent endAgent = sdk.createEndAgent();
```

### 迁移检查清单

- [ ] 移除 `AgentHeartbeatConfig.java` 中的临时实现
- [ ] 使用 SDK 提供的 `AgentFactoryImpl`
- [ ] 按需继承 `AbstractWorkerAgent` 或 `AbstractSceneAgent`
- [ ] 测试 Agent 功能正常

---

## 发现的问题

### 1. 实现类存在但未公开

SDK 在 `net.ooder.sdk.core.agent.impl` 包中已有完整实现：

| 实现类 | 路径 | 方法覆盖 |
|--------|------|----------|
| `WorkerAgentImpl` | `net.ooder.sdk.core.agent.impl.WorkerAgentImpl` | 20+ 方法 ✅ |
| `SceneAgentImpl` | `net.ooder.sdk.core.agent.impl.SceneAgentImpl` | 12+ 方法 ✅ |
| `EndAgentImpl` | `net.ooder.sdk.core.agent.impl.EndAgentImpl` | 25+ 方法 ✅ |
| `RouteAgentImpl` | `net.ooder.sdk.core.agent.impl.RouteAgentImpl` | 15+ 方法 ✅ |
| `McpAgentImpl` | `net.ooder.sdk.core.agent.impl.McpAgentImpl` | 15+ 方法 ✅ |
| `AgentFactoryImpl` | `net.ooder.sdk.core.agent.factory.AgentFactoryImpl` | 10+ 方法 ✅ |

### 2. 包路径问题

- **接口定义**: `net.ooder.sdk.api.agent.*` (公共 API)
- **实现类**: `net.ooder.sdk.core.agent.impl.*` (内部包)

按照 Java 包命名惯例，`core` 包通常表示内部实现，用户可能认为不应直接使用。

### 3. 缺少抽象基类

对于需要扩展的场景，缺少 `AbstractAgent`、`AbstractWorkerAgent` 等抽象基类。

### 4. 文档缺失

没有明确的使用指南说明如何使用这些实现类。

---

## 协作需求

### 需求 1：确认公共 API 状态（优先级：P0）

请 SDK 团队确认以下实现类是否为公共 API：

| 类名 | 当前状态 | 期望状态 |
|------|----------|----------|
| `AgentFactoryImpl` | 内部包 | 公共 API |
| `WorkerAgentImpl` | 内部包 | 公共 API |
| `SceneAgentImpl` | 内部包 | 公共 API |
| `EndAgentImpl` | 内部包 | 公共 API |
| `RouteAgentImpl` | 内部包 | 公共 API |
| `McpAgentImpl` | 内部包 | 公共 API |

**建议方案**：
- 方案 A：将实现类移动到 `net.ooder.sdk.api.agent.impl` 包
- 方案 B：在现有位置添加 `@PublicAPI` 注解，并在文档中声明为公共 API
- 方案 C：提供公共工厂方法，隐藏实现类

### 需求 2：提供抽象基类（优先级：P1）

建议在 `net.ooder.sdk.api.agent.support` 包中提供抽象基类：

```java
package net.ooder.sdk.api.agent.support;

/**
 * Agent 抽象基类
 * 提供生命周期管理、状态转换等通用实现
 */
public abstract class AbstractAgent implements Agent {
    
    protected final String agentId;
    protected final String agentName;
    protected final AgentType agentType;
    protected volatile AgentState state = AgentState.CREATED;
    
    public AbstractAgent(String agentId, String agentName, AgentType agentType) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.agentType = agentType;
    }
    
    @Override
    public String getAgentId() { return agentId; }
    
    @Override
    public String getAgentName() { return agentName; }
    
    @Override
    public AgentType getAgentType() { return agentType; }
    
    @Override
    public boolean isHealthy() {
        return state == AgentState.RUNNING;
    }
    
    @Override
    public AgentState getState() { return state; }
    
    protected boolean transitionState(AgentState from, AgentState to) {
        if (state == from) {
            state = to;
            onStateChanged(from, to);
            return true;
        }
        return false;
    }
    
    protected void onStateChanged(AgentState from, AgentState to) {
        // 子类可覆盖
    }
}

/**
 * WorkerAgent 抽象基类
 */
public abstract class AbstractWorkerAgent extends AbstractAgent implements WorkerAgent {
    
    protected final String sceneId;
    protected final String skillId;
    protected final List<String> capabilities = new CopyOnWriteArrayList<>();
    
    protected volatile WorkerAgentStatus workerStatus = WorkerAgentStatus.IDLE;
    protected volatile String currentTaskId;
    protected volatile SkillService skill;
    
    public AbstractWorkerAgent(String sceneId, String workerName, String skillId, List<String> capabilities) {
        super(generateWorkerId(sceneId, workerName), workerName, AgentType.WORKER);
        this.sceneId = sceneId;
        this.skillId = skillId;
        if (capabilities != null) {
            this.capabilities.addAll(capabilities);
        }
    }
    
    private static String generateWorkerId(String sceneId, String name) {
        return "worker-" + sceneId + "-" + name + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public String getSceneId() { return sceneId; }
    
    @Override
    public String getSkillId() { return skillId; }
    
    @Override
    public List<String> getCapabilities() { return new ArrayList<>(capabilities); }
    
    @Override
    public WorkerAgentStatus getWorkerStatus() { return workerStatus; }
    
    @Override
    public boolean isIdle() { return workerStatus == WorkerAgentStatus.IDLE; }
    
    @Override
    public boolean isBusy() { return workerStatus == WorkerAgentStatus.BUSY; }
    
    @Override
    public boolean hasError() { return workerStatus == WorkerAgentStatus.ERROR; }
    
    @Override
    public void setIdle() {
        workerStatus = WorkerAgentStatus.IDLE;
        currentTaskId = null;
    }
    
    @Override
    public void setBusy() {
        workerStatus = WorkerAgentStatus.BUSY;
    }
    
    @Override
    public void setError(String errorMessage) {
        workerStatus = WorkerAgentStatus.ERROR;
    }
    
    @Override
    public String getCurrentTaskId() { return currentTaskId; }
    
    @Override
    public void setCurrentTaskId(String taskId) { this.currentTaskId = taskId; }
    
    @Override
    public SkillService getSkill() { return skill; }
    
    @Override
    public void setSkill(SkillService skill) { this.skill = skill; }
    
    // 子类需要实现的方法
    @Override
    public abstract CompletableFuture<Object> execute(String capId, Map<String, Object> params);
}

/**
 * SceneAgent 抽象基类
 */
public abstract class AbstractSceneAgent extends AbstractAgent implements SceneAgent {
    
    protected final String sceneId;
    protected final String domainId;
    protected final CapRegistry capRegistry;
    protected volatile AgentStatus agentStatus = AgentStatus.CREATED;
    
    public AbstractSceneAgent(String sceneId, String agentName, String domainId) {
        super(generateSceneAgentId(sceneId, agentName), agentName, AgentType.SCENE);
        this.sceneId = sceneId;
        this.domainId = domainId;
        this.capRegistry = new InMemoryCapRegistry();
    }
    
    private static String generateSceneAgentId(String sceneId, String agentName) {
        return "scene-" + sceneId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public String getSceneId() { return sceneId; }
    
    @Override
    public String getDomainId() { return domainId; }
    
    @Override
    public CapRegistry getCapRegistry() { return capRegistry; }
    
    @Override
    public boolean isRunning() {
        return getState() == AgentState.RUNNING && agentStatus == AgentStatus.RUNNING;
    }
    
    @Override
    public AgentStatus getAgentStatus() { return agentStatus; }
    
    @Override
    public void registerCapability(Capability capability) {
        capRegistry.register(capability);
    }
    
    @Override
    public void unregisterCapability(String capId) {
        capRegistry.unregister(capId);
    }
    
    // 子类需要实现的方法
    @Override
    public abstract Object invokeCapability(String capId, Map<String, Object> params);
}
```

### 需求 3：提供 Spring Boot Starter（优先级：P2）

建议提供自动配置，简化 Spring 项目集成：

```java
package net.ooder.sdk.spring;

@Configuration
@ConditionalOnClass(AgentFactory.class)
@EnableConfigurationProperties(SDKProperties.class)
public class SDKAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public AgentFactory agentFactory() {
        return new AgentFactoryImpl();
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "ooder.sdk", name = "auto-create-scene-agent", havingValue = "true")
    public SceneAgent sceneAgent(AgentFactory factory, SDKProperties properties) {
        return factory.createSceneAgent(
            properties.getSceneId(), 
            properties.getAgentName()
        );
    }
}

@ConfigurationProperties(prefix = "ooder.sdk")
public class SDKProperties {
    private String agentId;
    private String agentName;
    private String sceneId;
    private String endpoint;
    private boolean autoCreateSceneAgent = false;
    // getters/setters
}
```

使用方式：
```yaml
# application.yml
ooder:
  sdk:
    agent-id: mvp-agent-001
    agent-name: MVP Agent
    scene-id: mvp-scene
    auto-create-scene-agent: true
```

### 需求 4：更新文档（优先级：P1）

建议在 SDK 中添加以下文档：

1. **Agent 使用指南** (`docs/guides/AGENT_USAGE_GUIDE.md`)
   - 如何使用 AgentFactory 创建 Agent
   - 如何继承抽象基类扩展 Agent
   - Agent 生命周期管理

2. **API 参考文档** 更新
   - 明确标注公共 API
   - 添加实现类的使用示例

---

## MVP 迁移计划

### 当前状态

MVP 项目已临时实现了 Agent 接口：
- 文件：`AgentHeartbeatConfig.java`
- 实现类：`AgentAdapter`、`WorkerAgentAdapter`、`SceneAgentAdapter` 等
- 问题：大部分方法返回空值或默认值，不建议在生产环境使用

### 迁移步骤

| 步骤 | 任务 | 依赖 |
|------|------|------|
| 1 | SDK 确认实现类为公共 API | 本文档 |
| 2 | MVP 移除临时实现，使用 SDK 实现类 | 步骤 1 |
| 3 | SDK 发布包含抽象基类的版本 | 需求 2 |
| 4 | MVP 按需继承抽象基类扩展功能 | 步骤 3 |

### MVP 迁移代码示例

**迁移前**（临时实现）：
```java
public class AgentAdapter implements Agent {
    @Override
    public String getAgentId() { return "temp"; }
    @Override
    public String getAgentName() { return "temp"; }
    // ... 大量空实现
}
```

**迁移后**（使用 SDK 实现）：
```java
import net.ooder.sdk.core.agent.factory.AgentFactoryImpl;
import net.ooder.sdk.core.agent.impl.SceneAgentImpl;

@Configuration
public class AgentConfig {
    
    @Bean
    public AgentFactory agentFactory() {
        return new AgentFactoryImpl();
    }
    
    @Bean
    public SceneAgent sceneAgent(AgentFactory factory) {
        return factory.createSceneAgent("mvp-scene", "mvp-agent");
    }
}
```

---

## 时间计划

| 阶段 | 内容 | 时间 | 负责方 |
|------|------|------|--------|
| 短期 | SDK 确认公共 API 状态 | 1 天 | SDK 团队 |
| 短期 | MVP 移除临时实现 | 1 天 | MVP 团队 |
| 中期 | SDK 添加抽象基类 | 1 周 | SDK 团队 |
| 中期 | SDK 更新文档 | 1 周 | SDK 团队 |
| 长期 | SDK 发布 3.0.1 版本 | 下个版本 | SDK 团队 |

---

## 联系方式

- **发起方**: scene-engine 团队
- **协作方**: MVP 团队、SDK 团队
- **日期**: 2026-03-25
- **SDK 版本**: 3.0.0

---

## 附录

### A. 现有实现类完整列表

| 类名 | 包路径 | 实现接口 |
|------|--------|----------|
| `AgentFactoryImpl` | `net.ooder.sdk.core.agent.factory` | `AgentFactory` |
| `WorkerAgentImpl` | `net.ooder.sdk.core.agent.impl` | `WorkerAgent` |
| `SceneAgentImpl` | `net.ooder.sdk.core.agent.impl` | `SceneAgent` |
| `EndAgentImpl` | `net.ooder.sdk.core.agent.impl` | `EndAgent` |
| `RouteAgentImpl` | `net.ooder.sdk.core.agent.impl` | `RouteAgent` |
| `McpAgentImpl` | `net.ooder.sdk.core.agent.impl` | `McpAgent` |

### B. OoderSDK 入口类

SDK 提供了 `OoderSDK` 入口类，已集成 `AgentFactory`：

```java
// 使用方式
OoderSDK sdk = OoderSDK.builder()
    .agentId("my-agent")
    .agentName("My Agent")
    .build();

// 创建 Agent
EndAgent endAgent = sdk.createEndAgent();
RouteAgent routeAgent = sdk.createRouteAgent();
McpAgent mcpAgent = sdk.createMcpAgent();

// 获取 AgentFactory
AgentFactory factory = sdk.getAgentFactory();
SceneAgent sceneAgent = factory.createSceneAgent("scene-001", "my-scene");
```

### C. 相关文档

- [SDK_3.0.0_INTERFACE_COMPATIBILITY_REQUEST.md](./SDK_3.0.0_INTERFACE_COMPATIBILITY_REQUEST.md) - 接口兼容性问题
- [AGENT_SDK_V3_COLLABORATION_TASKS.md](./AGENT_SDK_V3_COLLABORATION_TASKS.md) - v3.0 协同任务
- [AGENT_SDK_V3_REQUIREMENTS.md](./AGENT_SDK_V3_REQUIREMENTS.md) - v3.0 需求说明
