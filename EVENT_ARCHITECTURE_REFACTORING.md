# SDK 2.3 事件架构重构方案

## 一、设计原则

### 1.1 分层事件模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Engine 层（业务层）                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  特点：                                                              │   │
│  │  • 可中断（cancel）                                                   │   │
│  │  • 有状态（调用者信息、审计上下文）                                     │   │
│  │  • Spring 集成（@EventListener）                                      │   │
│  │  • 自动审计日志                                                       │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │  事件类型：                                                           │   │
│  │  • SkillInvocationEvent（Skill 调用事件）                             │   │
│  │  • CapabilityAccessEvent（能力访问事件）                              │   │
│  │  • PermissionCheckEvent（权限检查事件）                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────────────────┤
│                           Core 层（内核层）                                  │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  特点：                                                              │   │
│  │  • 无状态（不可变对象）                                                │   │
│  │  • 只观察（不能中断流程）                                              │   │
│  │  • 高性能（EventBean 单例管理）                                        │   │
│  │  • 无依赖（不依赖 Spring）                                             │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │  事件类型：                                                           │   │
│  │  • SkillStateChangedEvent（Skill 状态变更）                           │   │
│  │  • AgentStateChangedEvent（Agent 状态变更）                           │   │
│  │  • LinkStateChangedEvent（Link 状态变更）                             │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 关键设计决策

| 决策项 | Engine 层 | Core 层 |
|-------|-----------|---------|
| **可中断性** | ✅ 支持 cancel() | ❌ 不支持 |
| **调用者信息** | ✅ CallerInfo | ❌ 无 |
| **审计日志** | ✅ 自动记录 | ❌ 不记录 |
| **Spring 集成** | ✅ @EventListener | ❌ EventBean |
| **状态可变** | ✅ 可修改 | ❌ 不可变 |
| **性能要求** | 一般 | 高（高频事件） |
| **使用场景** | 权限、审计、业务控制 | 状态监控、日志、Metrics |

---

## 二、Core 层实现

### 2.1 核心类

```java
// 事件基类 - 不可变
net.ooder.sdk.core.event.CoreEvent

// 事件监听器
net.ooder.sdk.core.event.CoreEventListener<T extends CoreEvent>

// 事件管理器（单例）
net.ooder.sdk.core.event.EventBean
```

### 2.2 具体事件

```java
// Skill 状态变更
net.ooder.sdk.core.event.skill.SkillStateChangedEvent

// Agent 状态变更
net.ooder.sdk.core.event.agent.AgentStateChangedEvent

// Link 状态变更
net.ooder.sdk.core.event.link.LinkStateChangedEvent
```

### 2.3 使用示例

```java
// 发布事件
EventBean.getInstance().publish(
    new SkillStateChangedEvent(skillId, LifecycleState.STARTED, LifecycleState.HEALTHY)
);

// 订阅事件
EventBean.getInstance().subscribe(SkillStateChangedEvent.class, event -> {
    log.info("Skill {} state changed: {} -> {}", 
        event.getSkillId(), event.getOldState(), event.getNewState());
});
```

---

## 三、Engine 层实现

### 3.1 核心类

```java
// 事件基类 - 可中断、有状态
net.ooder.sdk.engine.event.EngineEvent

// 事件发布器（Spring 组件）
net.ooder.sdk.engine.event.EngineEventPublisher
```

### 3.2 调用者信息

```java
// 创建调用者信息
CallerInfo caller = new CallerInfo(
    userId,      // 用户 ID
    username,    // 用户名
    ipAddress,   // IP 地址
    sessionToken // 会话 Token
);

// 系统调用
CallerInfo system = CallerInfo.SYSTEM;
```

### 3.3 使用示例

```java
@Service
public class SkillService {
    
    @Autowired
    private EngineEventPublisher eventPublisher;
    
    public Object invokeSkill(String skillId, String capabilityId, 
                             Map<String, Object> params, CallerInfo caller) {
        
        // 创建事件
        SkillInvocationEvent event = new SkillInvocationEvent(
            caller, skillId, capabilityId, params
        );
        
        // 发布事件（如果被取消会返回 false）
        boolean allowed = eventPublisher.publish(event, () -> {
            throw new AccessDeniedException("Skill invocation cancelled by listener");
        });
        
        if (!allowed) {
            return null;
        }
        
        // 执行调用
        long startTime = System.currentTimeMillis();
        try {
            Object result = doInvoke(skillId, capabilityId, params);
            event.setResult(result);
            return result;
        } catch (Exception e) {
            event.setError(e);
            throw e;
        } finally {
            event.setDurationMs(System.currentTimeMillis() - startTime);
        }
    }
}

// 监听事件
@Component
public class SkillInvocationListener {
    
    @EventListener
    public void onSkillInvocation(SkillInvocationEvent event) {
        // 权限检查
        if (!hasPermission(event.getCallerInfo(), event.getSkillId())) {
            event.cancel("Permission denied");
            return;
        }
        
        // 记录审计日志（自动）
        // 可以添加业务逻辑
    }
}
```

---

## 四、与现有代码集成

### 4.1 LifecycleManagerImpl 集成

```java
public class LifecycleManagerImpl implements LifecycleManager {
    
    private void notifyStateChanged(String skillId, LifecycleState oldState, 
                                    LifecycleState newState, String source) {
        // 发布 Core 层事件（无状态、只观察）
        EventBean.getInstance().publish(
            new SkillStateChangedEvent(skillId, oldState, newState, source)
        );
        
        // 原有的监听器机制保留
        for (LifecycleListener listener : listeners) {
            listener.onStateChanged(new LifecycleEvent(skillId, oldState, newState));
        }
    }
}
```

### 4.2 SceneAgentImpl 集成

```java
public class SceneAgentImpl implements SceneAgent {
    
    @Autowired(required = false)
    private EngineEventPublisher eventPublisher;
    
    private Object invokeCapabilityInternal(Capability capability, Map<String, Object> params) {
        String skillId = capability.getSkillId();
        String capId = capability.getCapId();
        
        // Engine 层事件（权限检查、审计）
        if (eventPublisher != null) {
            CallerInfo caller = getCurrentCaller(); // 从上下文获取
            SkillInvocationEvent event = new SkillInvocationEvent(
                caller, skillId, capId, params
            );
            
            boolean allowed = eventPublisher.publish(event);
            if (!allowed) {
                throw new RuntimeException("Skill invocation cancelled: " + event.getCancelReason());
            }
        }
        
        // Core 层事件（状态变更）
        EventBean.getInstance().publish(
            new SkillInvocationStartedEvent(skillId, capId)
        );
        
        // 执行调用
        Object result = skillInvoker.invoke(skillId, capId, params);
        
        // 发布完成事件
        EventBean.getInstance().publish(
            new SkillInvocationCompletedEvent(skillId, capId, result)
        );
        
        return result;
    }
}
```

---

## 五、审计日志格式

### 5.1 日志格式

```
[AUDIT] 时间|事件ID|调用者ID|事件类型|描述|级别
```

### 5.2 示例

```
[AUDIT] 2026-02-25T10:30:15.123Z|evt-abc123|user-001|SkillInvocationEvent|Skill skill-001 capability cap-001 invoked by admin|INFO
[AUDIT] 2026-02-25T10:30:16.456Z|evt-def456|user-002|SkillInvocationEvent|Skill skill-002 capability cap-002 invoked by guest|WARNING
```

---

## 六、实施计划

### Phase 1: Core 层事件（1 周）
- [x] CoreEvent 基类
- [x] CoreEventListener 接口
- [x] EventBean 管理器
- [x] SkillStateChangedEvent
- [x] AgentStateChangedEvent
- [x] LinkStateChangedEvent
- [ ] 集成到 LifecycleManagerImpl
- [ ] 集成到 SceneAgentImpl

### Phase 2: Engine 层事件（1 周）
- [x] EngineEvent 基类
- [x] EngineEventPublisher
- [x] SkillInvocationEvent
- [ ] 权限检查监听器
- [ ] 审计日志存储

### Phase 3: 文档和测试（1 周）
- [ ] 编写集成文档
- [ ] 编写示例代码
- [ ] 单元测试
- [ ] 性能测试

---

## 七、与配置方案的整合

新的分层事件架构与之前的配置方案（Spring Boot Starter）可以无缝整合：

```yaml
ooder:
  sdk:
    events:
      core:
        enabled: true                    # 启用 Core 层事件
        async-thread-pool-size: 4        # 异步线程池大小
      engine:
        enabled: true                    # 启用 Engine 层事件
        audit-log-enabled: true          # 启用审计日志
        audit-log-storage: database      # 审计日志存储方式
```

---

## 八、总结

新的分层事件架构解决了以下问题：

1. **分层清晰**：Core 层（内核）和 Engine 层（业务）职责分离
2. **可观测性**：Core 层事件支持 Metrics 和监控
3. **可审计性**：Engine 层事件自动记录审计日志
4. **权限控制**：Engine 层事件支持中断（cancel）
5. **Spring 集成**：Engine 层支持 @EventListener
6. **高性能**：Core 层使用 EventBean 单例，最小化开销
