# Scene-Engine Driver 与 Common 统一重构方案

## 一、现状分析

### 1.1 当前架构问题

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              scene-engine                                    │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  Driver 体系 (scene-engine 内部)                                        │ │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐        │ │
│  │  │  Driver │ │BaseProv │ │SkillProv│ │Discovery│ │Protocol │        │ │
│  │  │  (核心) │ │ (业务)  │ │ (能力)  │ │ (发现)  │ │ (协议)  │        │ │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘        │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              agent-sdk                                       │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  与 scene-engine 的 Driver 存在概念重叠                                 │ │
│  │  - ProtocolProvider (agent-sdk) vs ProtocolProvider (scene-engine)    │ │
│  │  - SceneAgent (agent-sdk) vs SceneAgentCore (scene-engine)            │ │
│  │  - Capability (agent-sdk) vs SkillProvider (scene-engine)             │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            ooder-common                                      │
│  ┌─────────┬─────────┬─────────┬─────────┬─────────┬─────────┬─────────┐  │
│  │ config  │ client  │database │ server  │ vfs-web │ org-web │ msg-web │  │
│  │ (配置)   │ (核心)   │ (数据库)│ (服务)   │ (文件)   │ (组织)   │ (消息)   │  │
│  └─────────┴─────────┴─────────┴─────────┴─────────┴─────────┴─────────┘  │
│  问题：与 scene-engine 的 Provider 概念重复，但分散在不同模块                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 核心问题

| 问题 | 说明 | 影响 |
|-----|------|------|
| **概念重复** | Driver/Provider 与 agent-sdk 的 Capability/Agent 重叠 | 学习成本高 |
| **职责分散** | vfs/org/msg 能力分散在 common 各模块 | 难以统一管理 |
| **依赖混乱** | scene-engine 同时依赖 agent-sdk 和 common | 循环依赖风险 |
| **缺乏统一** | 没有统一的能力抽象层 | 扩展困难 |

---

## 二、统一重构方案

### 2.1 目标架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Layer 4: 应用层 (Applications)                                              │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  scene-engine (精简)                                                   │ │
│  │  - 移除 Driver 框架（下沉到 infra-driver）                              │ │
│  │  - 只保留业务编排逻辑                                                   │ │
│  │  - 通过标准接口使用能力                                                  │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Layer 3: 能力层 (Capability SDKs)                                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │  llm-sdk    │ │ agent-sdk   │ │  vfs-sdk    │ │  org-sdk    │          │
│  │  (AI能力)   │ │ (Agent能力) │ │ (文件能力)  │ │ (组织权限)   │          │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────────────────┐  │
│  │  msg-sdk    │ │ index-sdk   │ │  其他能力 SDK ...                    │  │
│  │ (消息通信)   │ │ (索引搜索)   │ │                                     │  │
│  └─────────────┘ └─────────────┘ └─────────────────────────────────────┘  │
│                                                                              │
│  统一接口：CapabilityProvider (替代 scene-engine 的 Provider)                │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  interface CapabilityProvider {                                       │ │
│  │      String getCapabilityName();                                      │ │
│  │      String getVersion();                                             │ │
│  │      HealthStatus getHealthStatus();                                  │ │
│  │      Object invoke(String method, Map<String, Object> params);        │ │
│  │  }                                                                    │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Layer 2: 驱动层 (Driver Framework) - 新建                                   │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  ooder-infra-driver (从 scene-engine 提取)                             │ │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐        │ │
│  │  │  Driver │ │ Driver  │ │ Driver  │ │ Skill   │ │Discovery│        │ │
│  │  │ Framework│ │Registry │ │Context  │ │Provider │ │Provider │        │ │
│  │  │ (驱动框架)│ │(注册中心)│ │(上下文)  │ │(能力包装)│ │(发现)   │        │ │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘        │ │
│  │                                                                      │ │
│  │  职责：统一的能力加载、生命周期管理、健康检查、服务发现                  │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  Layer 1: 基础设施层 (Infrastructure)                                        │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  ooder-infra-core (从 common-client 提取)                              │ │
│  │  - 异步执行、缓存、配置、事件、异常、工具                                │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  ooder-infra-data (从 database 提取)                                   │ │
│  │  - 数据库访问、连接池、DAO框架                                          │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  ooder-infra-server (从 server 提取)                                   │ │
│  │  - HTTP服务、UDP通信、服务注册                                          │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、关键改造点

### 3.1 统一能力接口

**现状：**
- scene-engine: `BaseProvider`, `SkillProvider`, `LlmProvider`
- agent-sdk: `Capability`, `ProtocolProvider`
- common: 各模块有自己的服务接口

**统一后：**

```java
// ooder-infra-driver 中定义
package net.ooder.infra.driver;

public interface CapabilityProvider {
    // 元数据
    String getProviderName();
    String getVersion();
    String getCategory();  // llm, vfs, org, msg, etc.
    
    // 生命周期
    void initialize(DriverContext context);
    void start();
    void stop();
    void destroy();
    
    // 状态
    boolean isInitialized();
    boolean isRunning();
    HealthStatus getHealthStatus();
    
    // 能力调用
    Object invoke(String capabilityId, Map<String, Object> params);
    
    // 接口定义（用于生成文档和校验）
    InterfaceDefinition getInterfaceDefinition();
}

// 统一的健康状态
public enum HealthStatus {
    HEALTHY,      // 健康
    DEGRADED,     // 降级（部分功能可用）
    UNHEALTHY,    // 不健康
    UNKNOWN       // 未知
}
```

### 3.2 Driver 框架下沉

**从 scene-engine 迁移到 ooder-infra-driver：**

| 组件 | 原位置 | 新位置 | 改造说明 |
|-----|--------|--------|---------|
| `Driver` | scene-engine | ooder-infra-driver | 保持不变 |
| `DriverRegistry` | scene-engine | ooder-infra-driver | 增强：支持更多发现机制 |
| `DriverContext` | scene-engine | ooder-infra-driver | 保持不变 |
| `BaseProvider` | scene-engine | ooder-infra-driver | 改名为 `CapabilityProvider` |
| `SkillProvider` | scene-engine | ooder-infra-driver | 作为 `CapabilityProvider` 子接口 |
| `DiscoveryProvider` | scene-engine | ooder-infra-driver | 保持不变 |

### 3.3 各能力 SDK 改造

#### vfs-sdk（从 vfs-web 改造）

```java
// 改造前：VfsStoreService (ooder-vfs-web)
public interface VfsStoreService {
    CtFile storeFile(InputStream input, String path);
    InputStream readFile(String fileId);
}

// 改造后：VfsCapabilityProvider (vfs-sdk)
@Component
public class VfsCapabilityProvider implements CapabilityProvider {
    
    @Autowired
    private VfsStoreService vfsService;  // 内部使用原服务
    
    @Override
    public String getProviderName() { return "vfs"; }
    
    @Override
    public String getCategory() { return "storage"; }
    
    @Override
    public Object invoke(String capabilityId, Map<String, Object> params) {
        switch (capabilityId) {
            case "storeFile":
                return vfsService.storeFile(
                    (InputStream) params.get("input"),
                    (String) params.get("path")
                );
            case "readFile":
                return vfsService.readFile((String) params.get("fileId"));
            // ...
        }
    }
}
```

#### org-sdk（从 org-web 改造）

```java
@Component
public class OrgCapabilityProvider implements CapabilityProvider {
    
    @Autowired
    private OrgWebManager orgManager;
    
    @Override
    public String getProviderName() { return "org"; }
    
    @Override
    public String getCategory() { return "identity"; }
    
    @Override
    public Object invoke(String capabilityId, Map<String, Object> params) {
        // 统一封装 org 能力
    }
}
```

#### msg-sdk（从 msg-web 改造）

```java
@Component
public class MsgCapabilityProvider implements CapabilityProvider {
    
    @Autowired
    private JmqService jmqService;
    
    @Override
    public String getProviderName() { return "msg"; }
    
    @Override
    public String getCategory() { return "messaging"; }
    
    @Override
    public Object invoke(String capabilityId, Map<String, Object> params) {
        // 统一封装消息能力
    }
}
```

### 3.4 agent-sdk 适配

```java
// agent-sdk 中的 Capability 适配到 CapabilityProvider
@Component
public class AgentCapabilityProvider implements CapabilityProvider {
    
    @Autowired
    private CapRegistry capRegistry;
    
    @Override
    public String getProviderName() { return "agent"; }
    
    @Override
    public String getCategory() { return "agent"; }
    
    @Override
    public Object invoke(String capabilityId, Map<String, Object> params) {
        // 通过 CapRegistry 调用 Agent 能力
        Capability cap = capRegistry.findById(capabilityId);
        return cap.invoke(params);
    }
}
```

### 3.5 llm-sdk 适配

```java
// llm-sdk 中的 LlmDriver 适配到 CapabilityProvider
@Component
public class LlmCapabilityProvider implements CapabilityProvider {
    
    @Autowired
    private LlmDriver llmDriver;
    
    @Override
    public String getProviderName() { return "llm"; }
    
    @Override
    public String getCategory() { return "ai"; }
    
    @Override
    public Object invoke(String capabilityId, Map<String, Object> params) {
        // 统一封装 LLM 能力
        switch (capabilityId) {
            case "chat":
                return llmDriver.chat(buildChatRequest(params));
            case "embedding":
                return llmDriver.embedding(buildEmbeddingRequest(params));
            // ...
        }
    }
}
```

---

## 四、scene-engine 简化

### 4.1 改造前

```java
// scene-engine 需要管理各种 Provider
@Service
public class SceneEngine {
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ConfigProvider configProvider;
    @Autowired
    private HealthProvider healthProvider;
    // ... 大量 Provider 注入
}
```

### 4.2 改造后

```java
// scene-engine 只依赖 DriverRegistry
@Service
public class SceneEngine {
    
    @Autowired
    private DriverRegistry driverRegistry;
    
    // 通过统一接口获取能力
    public Object invokeCapability(String category, String capabilityId, 
                                   Map<String, Object> params) {
        CapabilityProvider provider = driverRegistry
            .findProviderByCategory(category);
        return provider.invoke(capabilityId, params);
    }
    
    // 获取健康状态
    public Map<String, HealthStatus> getAllHealthStatus() {
        return driverRegistry.getAllProviders().stream()
            .collect(Collectors.toMap(
                CapabilityProvider::getProviderName,
                CapabilityProvider::getHealthStatus
            ));
    }
}
```

---

## 五、实施步骤

### Phase 1: 创建 ooder-infra-driver（2周）

**Week 1:**
- 从 scene-engine 提取 Driver 框架
- 定义 `CapabilityProvider` 统一接口
- 创建 `DriverRegistry` 注册中心

**Week 2:**
- 实现健康检查机制
- 实现服务发现机制
- 发布 1.0.0

### Phase 2: 改造能力 SDK（3周）

**Week 3:** vfs-sdk
- 创建 `VfsCapabilityProvider`
- 适配原有 `VfsStoreService`

**Week 4:** org-sdk
- 创建 `OrgCapabilityProvider`
- 适配原有 `OrgWebManager`

**Week 5:** msg-sdk
- 创建 `MsgCapabilityProvider`
- 适配原有 `JmqService`

### Phase 3: 改造 agent-sdk 和 llm-sdk（2周）

**Week 6:**
- agent-sdk: 创建 `AgentCapabilityProvider`
- llm-sdk: 创建 `LlmCapabilityProvider`

**Week 7:**
- 测试各 Provider 集成
- 修复兼容性问题

### Phase 4: 简化 scene-engine（1周）

**Week 8:**
- 移除 scene-engine 中的 Provider 实现
- 改为使用 `DriverRegistry`
- 测试验证

### Phase 5: 清理（1周）

**Week 9:**
- 废弃 scene-engine 中的旧 Provider
- 更新文档
- 发布迁移指南

---

## 六、收益分析

| 方面 | 现状 | 重构后 |
|-----|------|-------|
| **概念统一** | Driver/Provider/Capability 多个概念 | 统一 `CapabilityProvider` |
| **架构清晰** | scene-engine 臃肿 | scene-engine 精简，只保留编排 |
| **能力复用** | vfs/org/msg 分散 | 统一通过 Driver 框架管理 |
| **扩展性** | 添加新能力需要修改多处 | 只需实现 `CapabilityProvider` |
| **测试性** | 依赖复杂，难以测试 | 可 Mock Provider 进行测试 |
| **团队分工** | 耦合严重 | 各 SDK 团队独立开发 Provider |

---

## 七、风险与缓解

| 风险 | 等级 | 缓解措施 |
|-----|------|---------|
| 接口不兼容 | 高 | 提供适配器，保持向后兼容 |
| 性能下降 | 中 | 添加缓存，异步初始化 |
| 迁移成本高 | 高 | 分阶段实施，每阶段可独立发布 |
| 团队学习成本 | 中 | 提供详细文档和示例代码 |

---

## 八、总结

**核心思想：**
1. **统一接口**：所有能力通过 `CapabilityProvider` 暴露
2. **下沉框架**：Driver 框架下沉到 `ooder-infra-driver`
3. **简化引擎**：scene-engine 只保留业务编排逻辑
4. **能力自治**：各 SDK 管理自己的 Provider 实现

**预期效果：**
- 架构清晰，概念统一
- scene-engine 精简，易于维护
- 能力扩展简单，只需实现接口
- 团队分工明确，并行开发
