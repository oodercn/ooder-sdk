# skill-common 3.0.0 循环依赖检查报告

**检查日期**: 2026-03-24  
**检查范围**: 所有 Spring 自动注入的 Bean

---

## 一、Bean 依赖关系图

```
SkillCommonAutoConfiguration
├── JsonStorageService (无依赖)
├── AuthService (无依赖)
├── OrgService → JsonStorageService
├── AuthApi → AuthService (@Autowired)
├── ConfigApi (无依赖)
├── SkillIndexLoader (无依赖)
└── DiscoveryOrchestrator → SkillIndexLoader (@Autowired)

SkillSdkAutoConfiguration
├── SkillsHealthIndicator (无依赖)
└── SkillsMetricsConfigurer (无依赖)

其他 @Service/@Component
├── UnifiedConfigurationService (无依赖)
├── ResourceManager (无依赖)
└── MenuController → AuthService (@Autowired(required=false))
```

---

## 二、检查结果

### ✅ 无循环依赖

经过分析，**未发现循环依赖问题**。所有 Bean 的依赖关系都是单向的，不存在 A→B→A 类型的循环。

---

## 三、发现的问题

### 问题 1: OrgService init() 被调用两次

**位置**: `SkillCommonAutoConfiguration.java:39-43`

```java
@Bean
@ConditionalOnMissingBean
public OrgService orgService(JsonStorageService storage) {
    OrgService service = new OrgService(storage);
    service.init();  // ❌ 手动调用 init()
    return service;
}
```

**问题**: `OrgService` 类上有 `@PostConstruct` 注解的 `init()` 方法，配置类中又手动调用了 `init()`，导致初始化逻辑执行两次。

**修复建议**: 移除配置类中的手动 `init()` 调用

```java
@Bean
@ConditionalOnMissingBean
public OrgService orgService(JsonStorageService storage) {
    return new OrgService(storage);  // @PostConstruct 会自动调用 init()
}
```

---

### 问题 2: DiscoveryOrchestrator 注入方式不一致

**位置**: `DiscoveryOrchestrator.java:25-26`

```java
@Autowired
private SkillIndexLoader skillIndexLoader;  // ❌ 字段注入
```

**位置**: `SkillCommonAutoConfiguration.java:65-67`

```java
@Bean
@ConditionalOnMissingBean
public DiscoveryOrchestrator discoveryOrchestrator(SkillIndexLoader skillIndexLoader) {
    DiscoveryOrchestrator orchestrator = new DiscoveryOrchestrator();
    // ❌ 构造器参数传入但未使用
    return orchestrator;
}
```

**问题**: 配置类传入 `SkillIndexLoader` 参数但未使用，类内部使用 `@Autowired` 字段注入，造成代码不一致。

**修复建议**: 使用构造器注入替代字段注入

```java
// DiscoveryOrchestrator.java
private final SkillIndexLoader skillIndexLoader;

public DiscoveryOrchestrator(SkillIndexLoader skillIndexLoader) {
    this.skillIndexLoader = skillIndexLoader;
}
```

```java
// SkillCommonAutoConfiguration.java
@Bean
@ConditionalOnMissingBean
public DiscoveryOrchestrator discoveryOrchestrator(SkillIndexLoader skillIndexLoader) {
    return new DiscoveryOrchestrator(skillIndexLoader);
}
```

---

### 问题 3: JsonStorageService 初始化方式不一致

**位置**: `SkillCommonAutoConfiguration.java:25-28`

```java
@Bean
@ConditionalOnMissingBean(JsonStorageService.class)
public JsonStorageService jsonStorageService() {
    JsonStorageService service = new JsonStorageService(storagePath);
    service.init();  // ❌ 手动调用 init()
    return service;
}
```

**问题**: `JsonStorageService` 类上有 `@PostConstruct` 注解的 `init()` 方法，手动调用会导致重复初始化。

**修复建议**: 移除手动 `init()` 调用

```java
@Bean
@ConditionalOnMissingBean(JsonStorageService.class)
public JsonStorageService jsonStorageService() {
    return new JsonStorageService(storagePath);
}
```

---

### 问题 4: AuthApi 使用字段注入

**位置**: `AuthApi.java:24-25`

```java
@Autowired
private AuthService authService;  // ❌ 字段注入
```

**建议**: 使用构造器注入，便于测试和解耦

```java
private final AuthService authService;

public AuthApi(AuthService authService) {
    this.authService = authService;
}
```

---

## 四、注入方式统计

| 注入方式 | 数量 | 建议 |
|---------|------|------|
| 构造器注入 | 1 (OrgService) | ✅ 推荐 |
| 字段注入 (@Autowired) | 4 | ⚠️ 建议改为构造器注入 |
| 方法注入 (@Bean) | 9 | ✅ 正常 |

---

## 五、修复优先级

| 优先级 | 问题 | 影响 |
|--------|------|------|
| **高** | OrgService init() 重复调用 | 可能导致数据重复初始化 |
| **高** | JsonStorageService init() 重复调用 | 可能导致资源重复加载 |
| **中** | DiscoveryOrchestrator 注入不一致 | 代码维护性问题 |
| **低** | AuthApi 字段注入 | 代码规范问题 |

---

## 六、修复代码

### 修复 SkillCommonAutoConfiguration.java

```java
package net.ooder.skill.common.config;

import net.ooder.skill.common.api.AuthApi;
import net.ooder.skill.common.api.ConfigApi;
import net.ooder.skill.common.discovery.DiscoveryOrchestrator;
import net.ooder.skill.common.discovery.SkillIndexLoader;
import net.ooder.skill.common.service.AuthService;
import net.ooder.skill.common.service.OrgService;
import net.ooder.skill.common.storage.JsonStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.common.enabled", havingValue = "true", matchIfMissing = true)
public class SkillCommonAutoConfiguration {

    @Value("${app.storage.path:./data}")
    private String storagePath;

    @Bean
    @ConditionalOnMissingBean(JsonStorageService.class)
    public JsonStorageService jsonStorageService() {
        return new JsonStorageService(storagePath);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService() {
        return new AuthService();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrgService orgService(JsonStorageService storage) {
        return new OrgService(storage);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthApi authApi(AuthService authService) {
        return new AuthApi(authService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigApi configApi() {
        return new ConfigApi();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SkillIndexLoader skillIndexLoader() {
        return new SkillIndexLoader();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public DiscoveryOrchestrator discoveryOrchestrator(SkillIndexLoader skillIndexLoader) {
        return new DiscoveryOrchestrator(skillIndexLoader);
    }
}
```

---

## 七、结论

**循环依赖状态**: ✅ 无循环依赖

**需要修复的问题**: 4 个（主要是重复初始化和注入方式不一致）

**建议**: 按优先级修复上述问题，统一使用构造器注入，移除手动 `init()` 调用。

---

*检查工具: Spring Bean Dependency Analyzer*
