# Agent SDK Infra 层重构建议

## 一、现状分析

### 1.1 当前 Infra 层结构

```
net.ooder.sdk.infra
├── async          # 异步执行 (AsyncExecutor, AsyncTask)
├── config         # 配置管理 (SDKConfiguration, ConfigLoader)
├── exception      # 异常体系 (SDKException, ErrorCode, ErrorHandler)
├── lifecycle      # 生命周期 (LifecycleManager, LifecycleComponent, ShutdownHook)
├── observer       # 观察者模式 (ConfigObserver, ConfigChangeListener)
├── retry          # 重试机制 (RetryManager, RetryStrategy)
└── utils          # 工具类 (JsonUtils, FileUtils, NetUtils, ValidationUtils)
```

### 1.2 存在的问题

| 问题 | 说明 | 影响 |
|-----|------|------|
| **职责混杂** | Infra 层同时包含基础设施和业务逻辑 | 边界不清晰 |
| **与 Engine 层重叠** | LifecycleManager 与 Engine 层事件机制重复 | 功能冗余 |
| **缺乏分层** | 没有区分 Core 基础设施和 Engine 扩展 | 架构混乱 |
| **Spring 耦合** | 部分组件应该由 Spring 管理但未明确 | 依赖关系不清 |

---

## 二、重构方案

### 2.1 分层架构规划

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Engine 层（业务基础设施）                           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  net.ooder.sdk.engine.infra                                         │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │   Config    │ │   Async     │ │   Retry     │ │   Metrics   │   │   │
│  │  │   Engine    │ │   Engine    │ │   Engine    │ │   Engine    │   │   │
│  │  │  (配置中心)  │ │  (异步执行)  │ │  (重试策略)  │ │  (指标收集)  │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  │                                                                     │   │
│  │  特点：Spring 管理、可配置、支持事件、可审计                          │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────────────────┤
│                           Core 层（核心基础设施）                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  net.ooder.sdk.core.infra                                           │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │  Lifecycle  │ │   Async     │ │  Exception  │ │    Utils    │   │   │
│  │  │   Core      │ │   Core      │ │   Core      │ │   Core      │   │   │
│  │  │ (生命周期)   │ │ (基础异步)   │ │ (基础异常)   │ │ (基础工具)   │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  │                                                                     │   │
│  │  特点：无 Spring 依赖、高性能、单例模式、不可变                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、详细重构计划

### 3.1 Core 层（核心基础设施）

#### 位置：`net.ooder.sdk.core.infra`

**保留组件：**

| 组件 | 说明 | 修改建议 |
|-----|------|---------|
| `LifecycleManager` | 组件生命周期管理 | 简化为纯管理，移除事件发布 |
| `LifecycleComponent` | 生命周期组件接口 | 保持不变 |
| `AsyncExecutor` | 基础异步执行器 | 移除 Spring 依赖，保持纯 Java |
| `SDKException` | 基础异常类 | 保持不变 |
| `ErrorCode` | 错误码定义 | 保持不变 |
| `JsonUtils` | JSON 工具 | 保持不变 |
| `FileUtils` | 文件工具 | 保持不变 |

**移除/迁移：**
- `ConfigLoader` → Engine 层
- `ConfigObserver` → Engine 层
- `RetryManager` → Engine 层
- `ErrorHandler` → Engine 层

### 3.2 Engine 层（业务基础设施）

#### 位置：`net.ooder.sdk.engine.infra`

**新增/迁移组件：**

```java
// Engine 层配置管理
@Component
public class ConfigEngine {
    @Autowired
    private ConfigLoader configLoader;
    
    @EventListener
    public void onConfigChange(ConfigChangeEvent event) {
        // 处理配置变更
    }
}

// Engine 层异步执行器
@Component
public class AsyncEngine {
    @Autowired
    private AsyncExecutor coreExecutor;
    
    @EventListener
    public void onTaskComplete(TaskCompleteEvent event) {
        // 发布任务完成事件
    }
}

// Engine 层重试管理器
@Component
public class RetryEngine {
    @Autowired
    private RetryStrategy retryStrategy;
    
    public <T> T executeWithRetry(Callable<T> task, RetryContext context) {
        // 执行重试，记录审计日志
    }
}

// Engine 层错误处理器
@Component
public class ErrorHandlerEngine {
    @EventListener
    public void onError(ErrorEvent event) {
        // 处理错误，记录日志，发送告警
    }
}
```

---

## 四、具体代码重构

### 4.1 Core 层 LifecycleManager 简化

```java
// net.ooder.sdk.core.infra.lifecycle.LifecycleManager
public final class LifecycleManager {
    
    private static final LifecycleManager INSTANCE = new LifecycleManager();
    private final List<LifecycleComponent> components = new CopyOnWriteArrayList<>();
    private final AtomicInteger state = new AtomicInteger(State.CREATED.ordinal());
    
    // 纯管理，不发布事件
    public void register(LifecycleComponent component) {
        components.add(component);
    }
    
    public synchronized void initialize() throws Exception {
        // 仅初始化组件，不发布事件
        for (LifecycleComponent component : components) {
            component.initialize();
        }
        state.set(State.INITIALIZED.ordinal());
    }
    
    // ... 其他方法保持简洁
}
```

### 4.2 Engine 层 LifecycleEngine 增强

```java
// net.ooder.sdk.engine.infra.lifecycle.LifecycleEngine
@Component
public class LifecycleEngine {
    
    @Autowired
    private LifecycleManager coreManager;
    
    @Autowired
    private EngineEventPublisher eventPublisher;
    
    public void initialize() throws Exception {
        // 1. 发布初始化开始事件
        LifecycleInitializingEvent event = new LifecycleInitializingEvent();
        eventPublisher.publish(event);
        
        if (event.isCancelled()) {
            throw new InitializationCancelledException(event.getCancelReason());
        }
        
        // 2. 调用 Core 层初始化
        try {
            coreManager.initialize();
            
            // 3. 发布初始化完成事件
            eventPublisher.publish(new LifecycleInitializedEvent());
        } catch (Exception e) {
            // 4. 发布初始化失败事件
            eventPublisher.publish(new LifecycleInitializationFailedEvent(e));
            throw e;
        }
    }
}
```

### 4.3 Core 层 AsyncExecutor 优化

```java
// net.ooder.sdk.core.infra.async.AsyncExecutor
public final class AsyncExecutor {
    
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;
    
    // 纯执行，不处理事件
    public <T> CompletableFuture<T> submit(Callable<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executorService.submit(() -> {
            try {
                T result = task.call();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
    
    // 添加回调支持，但不依赖 Spring
    public <T> CompletableFuture<T> submit(Callable<T> task, 
                                           Consumer<T> onSuccess, 
                                           Consumer<Throwable> onError) {
        CompletableFuture<T> future = submit(task);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                onError.accept(ex);
            } else {
                onSuccess.accept(result);
            }
        });
        return future;
    }
}
```

### 4.4 Engine 层 AsyncEngine 包装

```java
// net.ooder.sdk.engine.infra.async.AsyncEngine
@Component
public class AsyncEngine {
    
    @Autowired
    private AsyncExecutor coreExecutor;
    
    @Autowired
    private EngineEventPublisher eventPublisher;
    
    public <T> CompletableFuture<T> submit(Callable<T> task, String taskName) {
        // 1. 发布任务开始事件
        TaskStartedEvent startEvent = new TaskStartedEvent(taskName);
        eventPublisher.publish(startEvent);
        
        long startTime = System.currentTimeMillis();
        
        // 2. 使用 Core 层执行
        return coreExecutor.submit(task, 
            result -> {
                // 3. 发布任务成功事件
                long duration = System.currentTimeMillis() - startTime;
                eventPublisher.publish(new TaskCompletedEvent(taskName, duration, result));
            },
            error -> {
                // 4. 发布任务失败事件
                long duration = System.currentTimeMillis() - startTime;
                eventPublisher.publish(new TaskFailedEvent(taskName, duration, error));
            }
        );
    }
}
```

---

## 五、目录结构调整

### 5.1 重构前

```
agent-sdk/src/main/java/net/ooder/sdk/
├── infra/
│   ├── async/
│   ├── config/
│   ├── exception/
│   ├── lifecycle/
│   ├── observer/
│   ├── retry/
│   └── utils/
└── engine/
    ├── event/
    ├── audit/
    └── security/
```

### 5.2 重构后

```
agent-sdk/src/main/java/net/ooder/sdk/
├── core/
│   └── infra/
│       ├── async/          # AsyncExecutor (简化版)
│       ├── exception/      # SDKException, ErrorCode
│       ├── lifecycle/      # LifecycleManager (简化版)
│       └── utils/          # JsonUtils, FileUtils
└── engine/
    ├── infra/
    │   ├── async/          # AsyncEngine (Spring 包装)
    │   ├── config/         # ConfigEngine (配置中心)
    │   ├── lifecycle/      # LifecycleEngine (事件驱动)
    │   ├── retry/          # RetryEngine (可审计)
    │   └── error/          # ErrorHandlerEngine
    ├── event/
    ├── audit/
    └── security/
```

---

## 六、实施步骤

### Phase 1: Core 层提取（1 周）
- [ ] 创建 `net.ooder.sdk.core.infra` 包
- [ ] 迁移 `LifecycleManager`（简化版）
- [ ] 迁移 `AsyncExecutor`（简化版）
- [ ] 迁移基础异常和工具类
- [ ] 保持向后兼容（保留原包，标记 @Deprecated）

### Phase 2: Engine 层增强（1 周）
- [ ] 创建 `net.ooder.sdk.engine.infra` 包
- [ ] 实现 `LifecycleEngine`
- [ ] 实现 `AsyncEngine`
- [ ] 实现 `ConfigEngine`
- [ ] 实现 `RetryEngine`

### Phase 3: 迁移和测试（1 周）
- [ ] 更新引用（使用 Engine 层组件）
- [ ] 编写单元测试
- [ ] 验证事件机制
- [ ] 性能测试

### Phase 4: 清理（3 天）
- [ ] 移除废弃的 Infra 类
- [ ] 更新文档
- [ ] 发布迁移指南

---

## 七、收益分析

| 方面 | 现状 | 重构后 | 收益 |
|-----|------|-------|------|
| **架构清晰度** | 职责混杂 | 分层明确 | 易于维护 |
| **可测试性** | 依赖复杂 | 分层测试 | 测试简单 |
| **可扩展性** | 难以扩展 | 插件化 | 易于扩展 |
| **性能** | 有 Spring 开销 | Core 层高性能 | 性能提升 |
| **事件支持** | 无统一事件 | 完整事件链 | 可观测性增强 |

---

## 八、风险评估

| 风险 | 等级 | 缓解措施 |
|-----|------|---------|
| 向后兼容性 | 中 | 保留原包，标记 @Deprecated |
| 迁移成本 | 中 | 分阶段实施，提供迁移指南 |
| 性能回归 | 低 | Core 层保持高性能 |
| 测试覆盖 | 中 | 增加单元测试和集成测试 |

---

## 九、总结

**核心思想：**
1. **分层清晰**：Core 层（无 Spring）+ Engine 层（Spring 管理）
2. **职责分离**：基础设施 vs 业务逻辑
3. **事件驱动**：Engine 层通过事件机制增强可观测性
4. **向后兼容**：平滑迁移，逐步替换

**预期效果：**
- 架构更清晰，职责更明确
- Core 层高性能，Engine 层高可观测
- 易于扩展和维护
