# scene-engine 3.0.0 注入方式变更适配检查报告

**检查日期**: 2026-03-24  
**变更来源**: scene-engine 3.0.0  
**影响范围**: skill-common 3.0.0

---

## 一、变更概述

scene-engine 3.0.0 已将以下 3 个类的字段注入改为构造函数注入：

| 文件 | 变更内容 |
|------|---------|
| `SkillSwitchHandlerImpl.java` | `@Autowired` → 构造函数注入 `ContextStorageService` |
| `SkillSDKAdapter.java` | 3个 `@Autowired` 字段 → 构造函数注入 `SkillRegistry`, `SkillInstaller`, `SkillDiscoverer` |
| `SkillInstanceFactory.java` | `@Autowired` → 构造函数注入 `SkillSDKAdapter` |

---

## 二、变更前后对比

### 2.1 SkillSwitchHandlerImpl

**变更前 (字段注入)**:
```java
@Service
public class SkillSwitchHandlerImpl implements SkillSwitchHandler {
    
    @Autowired
    private ContextStorageService contextStorageService;
    
    // ...
}
```

**变更后 (构造函数注入)**:
```java
@Service
public class SkillSwitchHandlerImpl implements SkillSwitchHandler {

    private final ContextStorageService contextStorageService;

    public SkillSwitchHandlerImpl(ContextStorageService contextStorageService) {
        this.contextStorageService = contextStorageService;
    }
    
    // ...
}
```

---

### 2.2 SkillSDKAdapter

**变更前 (字段注入)**:
```java
@Component
public class SkillSDKAdapter {

    @Autowired
    private SkillRegistry skillRegistry;
    
    @Autowired
    private SkillInstaller skillInstaller;
    
    @Autowired
    private SkillDiscoverer skillDiscoverer;
    
    // ...
}
```

**变更后 (构造函数注入)**:
```java
@Component
public class SkillSDKAdapter {

    private final SkillRegistry skillRegistry;
    private final SkillInstaller skillInstaller;
    private final SkillDiscoverer skillDiscoverer;

    public SkillSDKAdapter(SkillRegistry skillRegistry, 
                           SkillInstaller skillInstaller, 
                           SkillDiscoverer skillDiscoverer) {
        this.skillRegistry = skillRegistry;
        this.skillInstaller = skillInstaller;
        this.skillDiscoverer = skillDiscoverer;
    }
    
    // ...
}
```

---

### 2.3 SkillInstanceFactory

**变更前 (字段注入)**:
```java
@Component
public class SkillInstanceFactory {

    @Autowired
    private SkillSDKAdapter sdkAdapter;
    
    // ...
}
```

**变更后 (构造函数注入)**:
```java
@Component
public class SkillInstanceFactory {

    private final SkillSDKAdapter sdkAdapter;

    public SkillInstanceFactory(SkillSDKAdapter sdkAdapter) {
        this.sdkAdapter = sdkAdapter;
    }
    
    // ...
}
```

---

## 三、skill-common 适配检查

### 3.1 直接依赖检查

```
检查路径: skill-common/src/main/java
检查模式: import net.ooder.scene.skill.*
检查结果: ✅ 无直接依赖
```

skill-common 源码中 **没有直接使用** scene-engine 中这 3 个变更的类。

### 3.2 间接依赖检查

skill-common 通过 scene-engine 依赖传递获得这些类，但：
- 不直接实例化这些类
- 不直接注入这些类
- 不继承或扩展这些类

---

## 四、影响评估

| 评估项 | 结果 | 说明 |
|--------|------|------|
| 编译兼容性 | ✅ 兼容 | 无直接依赖，不影响编译 |
| 运行时兼容性 | ✅ 兼容 | Spring 自动处理构造函数注入 |
| API 兼容性 | ✅ 兼容 | 公共方法签名未变 |
| 行为兼容性 | ✅ 兼容 | 注入方式变更不影响行为 |

---

## 五、结论

### ✅ 无需适配

skill-common 3.0.0 **不需要进行任何适配修改**：

1. **无直接依赖**: skill-common 源码中没有直接使用变更的 3 个类
2. **Spring 自动处理**: 构造函数注入由 Spring 容器自动处理
3. **向后兼容**: 变更后的类对外接口保持不变

### 建议的后续行动

| 优先级 | 行动 | 说明 |
|--------|------|------|
| 低 | 更新 CHANGELOG | 记录 scene-engine 3.0.0 的变更 |
| 低 | 验证集成测试 | 确认 skill-common 与 scene-engine 3.0.0 集成正常 |

---

## 六、依赖关系图

```
scene-engine 3.0.0
├── SkillSwitchHandlerImpl
│   └── ContextStorageService (构造函数注入)
├── SkillSDKAdapter
│   ├── SkillRegistry (构造函数注入)
│   ├── SkillInstaller (构造函数注入)
│   └── SkillDiscoverer (构造函数注入)
└── SkillInstanceFactory
    └── SkillSDKAdapter (构造函数注入)

skill-common 3.0.0
├── 依赖 scene-engine 3.0.0
└── 无直接使用上述类
```

---

*检查工具: Spring Bean Dependency Analyzer*
