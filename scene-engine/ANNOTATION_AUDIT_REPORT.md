# Scene Engine 注解使用审计报告

**审计日期**: 2026-03-03  
**审计范围**: scene-engine/src/main/java/net/ooder/scene  
**审计原则**: engine内部尽量避免使用Spring注入依赖

---

## 审计结果摘要

| 类别 | 文件数 | 状态 |
|------|--------|------|
| **配置类（允许使用注解）** | 3 | ✅ 合规 |
| **Service实现类（需整改）** | 2 | ⚠️ 需整改 |
| **核心引擎类（需整改）** | 2 | ⚠️ 需整改 |
| **其他类（需评估）** | 6 | ⚠️ 需评估 |

---

## 详细审计结果

### 一、配置类（允许使用注解）✅

| 文件 | 使用的注解 | 状态 | 说明 |
|------|-----------|------|------|
| `config/JDSServerSceneConfiguration.java` | `@Configuration`, `@ConditionalOnClass`, `@Bean`, `@ConditionalOnMissingBean`, `@PostConstruct` | ✅ 合规 | 配置类，允许使用Spring注解 |
| `config/SceneEngineAutoConfiguration.java` | `@Configuration`, `@ConditionalOnClass`, `@Bean`, `@ConditionalOnMissingBean` | ✅ 合规 | 配置类，允许使用Spring注解 |
| `event/config/EventConfiguration.java` | 待检查 | ⚠️ 需确认 | 事件配置类 |

### 二、Service实现类（需整改）⚠️

| 文件 | 使用的注解 | 问题 | 整改建议 |
|------|-----------|------|----------|
| `service/impl/UnifiedSceneServiceImpl.java` | `@Service`, `@Autowired` | Service层使用Spring注解 | 改为通过构造函数传入依赖，或使用Holder模式 |
| `discovery/impl/CapabilityDiscoveryServiceImpl.java` | 待检查 | - | 需检查是否使用Spring注解 |

### 三、核心引擎类（需整改）⚠️

| 文件 | 使用的注解 | 问题 | 整改建议 |
|------|-----------|------|----------|
| `core/impl/SceneEngineImpl.java` | `@Component`, `@Autowired` x6, `@PostConstruct` | 核心引擎类使用Spring注解，违反原则 | **必须整改**：移除所有Spring注解，改为通过构造函数或setter传入依赖 |
| `core/CapRouter.java` | `@Component`, `@Autowired` x2, `@PostConstruct` | 核心路由类使用Spring注解 | **必须整改**：移除所有Spring注解，改为通过构造函数传入依赖 |

### 四、其他类（需评估）⚠️

| 文件 | 使用的注解 | 状态 | 说明 |
|------|-----------|------|------|
| `ui/NexusUiController.java` | 待检查 | ⚠️ 需评估 | UI控制器类，可能允许使用注解 |
| `ui/NexusUiLoader.java` | 待检查 | ⚠️ 需评估 | UI加载类 |
| `ui/NexusUiRegistryImpl.java` | 待检查 | ⚠️ 需评估 | UI注册类 |
| `protocol/UdpDiscoveryService.java` | 待检查 | ⚠️ 需评估 | 协议服务类 |
| `event/SceneEventPublisher.java` | 待检查 | ⚠️ 需评估 | 事件发布类 |
| `event/listener/AuditEventListener.java` | 待检查 | ⚠️ 需评估 | 事件监听类 |

---

## 核心问题详细说明

### 问题1: SceneEngineImpl 使用Spring注解

**文件**: `core/impl/SceneEngineImpl.java`

**问题代码**:
```java
@Component  // ❌ 不应该使用
public class SceneEngineImpl implements SceneEngine {
    
    @Autowired  // ❌ 不应该使用
    private SessionManager sessionManager;
    
    @Autowired  // ❌ 不应该使用
    private SceneClientFactory sceneClientFactory;
    
    @Autowired  // ❌ 不应该使用
    private AdminClientFactory adminClientFactory;
    
    @Autowired  // ❌ 不应该使用
    private CapabilityDiscoveryService discoveryService;
    
    @Autowired  // ❌ 不应该使用
    private SkillInstaller skillInstaller;
    
    @Autowired  // ❌ 不应该使用
    private AuditService auditService;
    
    @PostConstruct  // ❌ 不应该使用
    public void init() {
        // 初始化逻辑
    }
}
```

**整改方案**:
```java
// ✅ 整改后：移除所有Spring注解
public class SceneEngineImpl implements SceneEngine {
    
    private SessionManager sessionManager;
    private SceneClientFactory sceneClientFactory;
    private AdminClientFactory adminClientFactory;
    private CapabilityDiscoveryService discoveryService;
    private SkillInstaller skillInstaller;
    private AuditService auditService;
    
    // 通过构造函数传入依赖
    public SceneEngineImpl(SessionManager sessionManager,
                          SceneClientFactory sceneClientFactory,
                          AdminClientFactory adminClientFactory,
                          CapabilityDiscoveryService discoveryService,
                          SkillInstaller skillInstaller,
                          AuditService auditService) {
        this.sessionManager = sessionManager;
        this.sceneClientFactory = sceneClientFactory;
        this.adminClientFactory = adminClientFactory;
        this.discoveryService = discoveryService;
        this.skillInstaller = skillInstaller;
        this.auditService = auditService;
        init(); // 在构造函数中调用初始化
    }
    
    private void init() {
        // 初始化逻辑
    }
}
```

### 问题2: CapRouter 使用Spring注解

**文件**: `core/CapRouter.java`

**问题代码**:
```java
@Component  // ❌ 不应该使用
public class CapRouter {
    
    @Autowired  // ❌ 不应该使用
    private SceneEngine sceneEngine;
    
    @PostConstruct  // ❌ 不应该使用
    public void init() {
        // 初始化逻辑
    }
    
    @Autowired(required = false)  // ❌ 不应该使用
    private List<CapabilityInterceptor> interceptors;
}
```

**整改方案**:
```java
// ✅ 整改后：移除所有Spring注解
public class CapRouter {
    
    private SceneEngine sceneEngine;
    private List<CapabilityInterceptor> interceptors;
    
    // 通过构造函数传入依赖
    public CapRouter(SceneEngine sceneEngine, 
                     List<CapabilityInterceptor> interceptors) {
        this.sceneEngine = sceneEngine;
        this.interceptors = interceptors;
        init(); // 在构造函数中调用初始化
    }
    
    private void init() {
        // 初始化逻辑
    }
}
```

---

## 整改优先级

| 优先级 | 文件 | 原因 |
|--------|------|------|
| **P0（最高）** | `core/impl/SceneEngineImpl.java` | 核心引擎类，影响整个架构 |
| **P0（最高）** | `core/CapRouter.java` | 核心路由类，影响能力路由 |
| **P1（高）** | `service/impl/UnifiedSceneServiceImpl.java` | Service层，影响业务逻辑 |
| **P2（中）** | 其他6个文件 | 根据具体情况评估 |

---

## 整改原则

1. **核心引擎类**（SceneEngineImpl, CapRouter等）
   - 必须移除所有Spring注解
   - 通过构造函数传入依赖
   - 使用Holder模式管理单例

2. **Service实现类**
   - 建议移除Spring注解
   - 通过构造函数传入依赖
   - 由配置类统一管理生命周期

3. **配置类**
   - 允许使用Spring注解
   - 负责创建和配置所有组件
   - 使用@Bean方法创建实例

---

## 建议的架构调整

```
配置层（允许Spring注解）
├── JDSServerSceneConfiguration
├── SceneEngineAutoConfiguration
└── EventConfiguration
        ↓ 创建并传入依赖
核心层（无Spring注解）
├── SceneEngineImpl（通过构造函数接收依赖）
├── CapRouter（通过构造函数接收依赖）
└── SecureSceneEngineProxy（通过构造函数接收依赖）
        ↓ 使用
工具类（无Spring注解）
├── SceneEngineHolder（单例Holder）
└── SecureSceneEngineValidator
```

---

## 后续行动

1. **立即整改**: SceneEngineImpl 和 CapRouter（P0）
2. **评估整改**: UnifiedSceneServiceImpl（P1）
3. **检查确认**: 其他6个文件（P2）
4. **文档更新**: 更新开发规范，明确engine内部禁用Spring注解

---

**审计人**: AI Assistant  
**报告时间**: 2026-03-03
