# SDK组件注入二次开发指南

## 文档信息

| 属性 | 值 |
|------|-----|
| **文档版本** | 1.2 |
| **编写日期** | 2026-02-28 |
| **适用版本** | SDK v2.3 |
| **目标项目** | ooder-Nexus-Enterprise |

---

## 一、问题背景

### 1.1 问题描述

Nexus Enterprise项目在集成SDK v2.3时，发现以下组件无法通过Spring `@Autowired`注入：

| 组件 | 包路径 | 问题原因 |
|------|--------|----------|
| **CapabilityRegistry** | `net.ooder.sdk.a2a.capability.CapabilityRegistry` | **无Spring注解** |
| **SkillService** | `net.ooder.sdk.service.skill.SkillService` | **无Spring注解，需要构造参数** |
| **SceneEngine** | `net.ooder.scene.core.SceneEngine` | **scene-engine工程，依赖项未注入** |
| **DriverRegistry** | `net.ooder.scene.core.driver.DriverRegistry` | **scene-engine工程，无Spring注解** |

### 1.2 根本原因分析

#### CapabilityRegistry 注入失败原因

```java
// CapabilityRegistry.java
public class CapabilityRegistry {
    // ❌ 无任何Spring注解
    private final Map<String, List<CapabilityDeclaration>> skillCapabilities;
}
```

**根本原因**：
- 普通Java类，未使用Spring管理
- 需要手动创建实例或添加Spring注解

#### SkillService 注入失败原因

```java
// SkillService.java
public class SkillService {
    private final SkillPackageManager packageManager;
    
    // ❌ 需要构造参数，无法直接实例化
    public SkillService(SkillPackageManager packageManager) {
        this.packageManager = packageManager;
    }
}
```

**根本原因**：
- 需要`SkillPackageManager`构造参数
- 无默认构造函数
- 未使用Spring管理

#### SceneEngine/DriverRegistry 注入失败原因

**注意**：这两个组件属于**scene-engine工程**，不在agent-sdk-core中。

- `net.ooder.scene.core.SceneEngine` - scene-engine工程
- `net.ooder.scene.core.driver.DriverRegistry` - scene-engine工程

**SceneEngine依赖项分析**：

| 依赖项 | 状态 | 说明 |
|--------|------|------|
| `SceneEventPublisher` | ✅ 可自动注入 | 有 `@Component` 注解 |
| `SessionManager` | ❌ 需手动注册 | 有实现类，无Spring注解 |
| `SkillService` | ❌ 需实现 | 抽象类，需具体实现 |
| `SceneProvider` | ❌ 需自行实现 | 无实现类 |
| `HeartbeatProvider` | ❌ 需自行实现 | 无实现类 |
| `UserSettingsProvider` | ❌ 需自行实现 | 无实现类 |

**根本原因**：
- scene-engine工程未添加Spring Boot支持
- 组件未使用Spring注解
- SceneEngine依赖多个Provider接口，需要具体实现
- Nexus项目需要单独配置scene-engine的Bean

---

## 二、解决方案

### 方案1：Nexus端配置类（推荐 - 快速解决）

在Nexus Enterprise项目中创建配置类，手动注册SDK组件为Spring Bean。

#### 2.1.1 创建SDK配置类（agent-sdk组件）

```java
package net.ooder.nexus.config;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.local.LocalSkillPackageManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SDK组件配置类（agent-sdk）
 * 
 * 解决agent-sdk组件无法通过@Autowired注入的问题
 */
@Configuration
public class SdkComponentConfiguration {

    @Value("${ooder.skill.root-path:./skills}")
    private String skillRootPath;

    /**
     * 注册SkillPackageManager
     * 
     * SkillService的依赖项
     */
    @Bean
    public SkillPackageManager skillPackageManager() {
        LocalSkillPackageManager manager = new LocalSkillPackageManager();
        manager.setSkillRootPath(skillRootPath);
        return manager;
    }

    /**
     * 注册SkillService
     * 
     * 原因为：需要SkillPackageManager构造参数
     */
    @Bean
    public SkillService skillService(SkillPackageManager packageManager) {
        return new SkillService(packageManager);
    }

    /**
     * 注册CapabilityRegistry
     * 
     * 原因为：SDK中的CapabilityRegistry未使用Spring注解
     */
    @Bean
    public CapabilityRegistry capabilityRegistry() {
        return new CapabilityRegistry();
    }
}
```

#### 2.1.2 创建SDK配置类（scene-engine组件）

```java
package net.ooder.nexus.config;

import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.driver.DriverRegistry;
import net.ooder.scene.core.impl.SceneEngineImpl;
import net.ooder.scene.provider.HeartbeatProvider;
import net.ooder.scene.provider.SceneProvider;
import net.ooder.scene.provider.UserSettingsProvider;
import net.ooder.scene.session.SessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SDK组件配置类（scene-engine）
 * 
 * 解决scene-engine组件无法通过@Autowired注入的问题
 * 注意：需要添加scene-engine依赖
 */
@Configuration
public class SceneEngineConfiguration {

    /**
     * 注册DriverRegistry
     * 
     * 原因为：scene-engine中的DriverRegistry未使用Spring注解
     */
    @Bean
    public DriverRegistry driverRegistry() {
        return new DriverRegistry();
    }

    /**
     * 注册SessionManager
     * 
     * SceneEngine的依赖项，有实现类但无Spring注解
     */
    @Bean
    public SessionManager sessionManager() {
        // 使用默认实现或自定义实现
        return new DefaultSessionManager();
    }

    /**
     * 注册SceneProvider
     * 
     * SceneEngine的依赖项，需自行实现
     */
    @Bean
    public SceneProvider sceneProvider() {
        // 需要实现SceneProvider接口
        return new CustomSceneProvider();
    }

    /**
     * 注册HeartbeatProvider
     * 
     * SceneEngine的依赖项，需自行实现
     */
    @Bean
    public HeartbeatProvider heartbeatProvider() {
        // 需要实现HeartbeatProvider接口
        return new CustomHeartbeatProvider();
    }

    /**
     * 注册UserSettingsProvider
     * 
     * SceneEngine的依赖项，需自行实现
     */
    @Bean
    public UserSettingsProvider userSettingsProvider() {
        // 需要实现UserSettingsProvider接口
        return new CustomUserSettingsProvider();
    }

    /**
     * 注册SceneEngine
     * 
     * 注意：SceneEngineImpl需要多个依赖项
     */
    @Bean
    public SceneEngine sceneEngine(
            SessionManager sessionManager,
            SceneProvider sceneProvider,
            HeartbeatProvider heartbeatProvider,
            UserSettingsProvider userSettingsProvider) {
        
        SceneEngineImpl sceneEngine = new SceneEngineImpl();
        
        // 手动注入依赖（因为SceneEngineImpl使用@Autowired）
        // 方式1：通过setter注入（如果有setter方法）
        // sceneEngine.setSessionManager(sessionManager);
        
        // 方式2：通过反射注入
        // ReflectionUtils.setField(field, sceneEngine, sessionManager);
        
        return sceneEngine;
    }
}
```

#### 2.1.3 创建自动配置类（更优雅的方案）

```java
package net.ooder.nexus.config;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.driver.DriverRegistry;
import net.ooder.skills.api.SkillPackageManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SDK自动配置类
 * 
 * 使用Spring Boot自动配置机制，只在Bean不存在时创建
 */
@Configuration
public class SdkAutoConfiguration {

    // ==================== agent-sdk组件 ====================

    @Bean
    @ConditionalOnMissingBean
    public CapabilityRegistry capabilityRegistry() {
        return new CapabilityRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillService skillService(SkillPackageManager packageManager) {
        return new SkillService(packageManager);
    }

    // ==================== scene-engine组件 ====================

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DriverRegistry.class)  // 只有存在scene-engine时才创建
    public DriverRegistry driverRegistry() {
        return new DriverRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(SceneEngine.class)  // 只有存在scene-engine时才创建
    public SceneEngine sceneEngine() {
        return new SceneEngineImpl();
    }
}
```

#### 2.1.4 创建spring.factories文件

在 `src/main/resources/META-INF/spring.factories` 中添加：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
net.ooder.nexus.config.SdkAutoConfiguration
```

### 方案2：SDK端修改（推荐 - 长期解决）

修改SDK源码，添加Spring注解支持。

#### 2.2.1 修改CapabilityRegistry

```java
package net.ooder.sdk.a2a.capability;

import org.springframework.stereotype.Component;

/**
 * 能力注册表
 * 
 * 管理所有Skill的能力声明
 */
@Component  // ✅ 添加Spring注解
public class CapabilityRegistry {
    
    private final Map<String, List<CapabilityDeclaration>> skillCapabilities;
    private final Map<String, Map<String, CapabilityDeclaration>> capabilityIndex;
    
    public CapabilityRegistry() {
        this.skillCapabilities = new ConcurrentHashMap<>();
        this.capabilityIndex = new ConcurrentHashMap<>();
    }
    
    // ... 原有方法
}
```

#### 2.2.2 修改SkillService

```java
package net.ooder.sdk.service.skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 技能服务类
 */
@Service  // ✅ 添加Spring注解
public class SkillService {
    
    private final SkillPackageManager packageManager;
    
    @Autowired  // ✅ 添加自动注入
    public SkillService(SkillPackageManager packageManager) {
        this.packageManager = packageManager;
    }
    
    // ... 原有方法
}
```

#### 2.2.3 修改DriverRegistry（scene-engine）

```java
package net.ooder.scene.core.driver;

import org.springframework.stereotype.Component;

/**
 * 驱动注册表
 * 
 * 管理所有设备驱动
 */
@Component  // ✅ 添加Spring注解
public class DriverRegistry {
    
    private final Map<String, Driver> drivers = new ConcurrentHashMap<>();
    private final Map<String, InterfaceDefinition> interfaceDefinitions = new ConcurrentHashMap<>();
    
    // ... 原有方法
}
```

#### 2.2.4 创建SDK自动配置类

在SDK中创建自动配置类：

```java
package net.ooder.sdk.config;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.skills.api.SkillPackageManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * SDK自动配置类
 * 
 * 自动扫描并注册SDK组件
 */
@Configuration
@ComponentScan(basePackages = {
    "net.ooder.sdk.a2a.capability",
    "net.ooder.sdk.service.skill"
})
public class OoderSdkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SkillPackageManager skillPackageManager() {
        return new LocalSkillPackageManager();
    }
}
```

### 方案3：混合方案（平衡方案）

结合方案1和方案2的优点：

1. **短期**：使用方案1在Nexus端快速解决
2. **长期**：推动SDK团队实施方案2
3. **过渡**：方案1添加`@ConditionalOnMissingBean`，SDK更新后自动切换

---

## 三、详细实现步骤

### 3.1 Nexus端实施步骤

#### 步骤1：添加依赖

```xml
<!-- pom.xml -->
<dependencies>
    <!-- agent-sdk -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-core</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- scene-engine（如果需要） -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine</artifactId>
        <version>2.3</version>
    </dependency>
</dependencies>
```

#### 步骤2：创建配置目录

```
src/main/java/net/ooder/nexus/config/
├── SdkComponentConfiguration.java    # agent-sdk配置
├── SceneEngineConfiguration.java      # scene-engine配置
├── SdkAutoConfiguration.java          # 自动配置
└── SdkIntegrationService.java         # 集成服务
```

#### 步骤3：创建集成服务

```java
package net.ooder.nexus.service;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.driver.DriverRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * SDK集成服务
 * 
 * 验证SDK组件注入状态
 */
@Service
public class SdkIntegrationService {
    
    private static final Logger log = LoggerFactory.getLogger(SdkIntegrationService.class);
    
    @Autowired(required = false)
    private CapabilityRegistry capabilityRegistry;
    
    @Autowired(required = false)
    private SkillService skillService;
    
    @Autowired(required = false)
    private DriverRegistry driverRegistry;
    
    @Autowired(required = false)
    private SceneEngine sceneEngine;
    
    @PostConstruct
    public void init() {
        log.info("[SdkIntegration] SDK组件注入状态检查:");
        log.info("  - CapabilityRegistry: {}", capabilityRegistry != null ? "✅ 可用" : "❌ 不可用");
        log.info("  - SkillService: {}", skillService != null ? "✅ 可用" : "❌ 不可用");
        log.info("  - DriverRegistry: {}", driverRegistry != null ? "✅ 可用" : "❌ 不可用");
        log.info("  - SceneEngine: {}", sceneEngine != null ? "✅ 可用" : "❌ 不可用");
        
        if (capabilityRegistry == null || skillService == null) {
            log.warn("[SdkIntegration] 部分agent-sdk组件未注入，功能可能受限");
        }
        
        if (driverRegistry == null || sceneEngine == null) {
            log.warn("[SdkIntegration] 部分scene-engine组件未注入，功能可能受限");
        }
    }
    
    // Getter方法
    public CapabilityRegistry getCapabilityRegistry() {
        return capabilityRegistry;
    }
    
    public SkillService getSkillService() {
        return skillService;
    }
    
    public DriverRegistry getDriverRegistry() {
        return driverRegistry;
    }
    
    public SceneEngine getSceneEngine() {
        return sceneEngine;
    }
}
```

#### 步骤4：实现Provider接口

SceneEngine依赖多个Provider接口，需要实现这些接口：

```java
package net.ooder.nexus.provider;

import net.ooder.scene.provider.SceneProvider;
import net.ooder.scene.model.Scene;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 场景提供者实现
 */
@Component
public class CustomSceneProvider implements SceneProvider {
    
    @Override
    public List<Scene> getScenes() {
        // 实现获取场景列表逻辑
        return List.of();
    }
    
    @Override
    public Scene getScene(String sceneId) {
        // 实现获取单个场景逻辑
        return null;
    }
}
```

```java
package net.ooder.nexus.provider;

import net.ooder.scene.provider.HeartbeatProvider;
import org.springframework.stereotype.Component;

/**
 * 心跳提供者实现
 */
@Component
public class CustomHeartbeatProvider implements HeartbeatProvider {
    
    @Override
    public void sendHeartbeat() {
        // 实现发送心跳逻辑
    }
    
    @Override
    public boolean isAlive() {
        // 实现存活检查逻辑
        return true;
    }
}
```

```java
package net.ooder.nexus.provider;

import net.ooder.scene.provider.UserSettingsProvider;
import net.ooder.scene.model.UserSettings;
import org.springframework.stereotype.Component;

/**
 * 用户设置提供者实现
 */
@Component
public class CustomUserSettingsProvider implements UserSettingsProvider {
    
    @Override
    public UserSettings getUserSettings(String userId) {
        // 实现获取用户设置逻辑
        return new UserSettings();
    }
    
    @Override
    public void saveUserSettings(String userId, UserSettings settings) {
        // 实现保存用户设置逻辑
    }
}
```

#### 步骤5：更新包扫描配置

```java
@SpringBootApplication(scanBasePackages = {
    "net.ooder.nexus",
    "net.ooder.sdk",        // SDK agent-sdk
    "net.ooder.scene"       // SDK scene-engine（如果需要）
})
public class NexusSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusSpringApplication.class, args);
    }
}
```

### 3.2 SDK端实施步骤

#### 步骤1：添加Spring依赖

在SDK的pom.xml中添加：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.7.0</version>
    <scope>provided</scope>  <!-- 由使用方提供 -->
</dependency>
```

#### 步骤2：添加Spring注解

修改以下类，添加`@Component`或`@Service`注解：

1. `net.ooder.sdk.a2a.capability.CapabilityRegistry` - `@Component`
2. `net.ooder.sdk.service.skill.SkillService` - `@Service`
3. `net.ooder.scene.core.driver.DriverRegistry` - `@Component`（scene-engine）

#### 步骤3：创建自动配置

创建 `net.ooder.sdk.config.OoderSdkAutoConfiguration`

#### 步骤4：创建spring.factories

在 `META-INF/spring.factories` 中添加自动配置。

---

## 四、验证测试

### 4.1 单元测试

```java
package net.ooder.nexus.config;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SDK组件注入测试
 */
@SpringBootTest
public class SdkComponentInjectionTest {
    
    @Autowired
    private CapabilityRegistry capabilityRegistry;
    
    @Autowired
    private SkillService skillService;
    
    @Test
    public void testCapabilityRegistryInjection() {
        assertNotNull(capabilityRegistry, "CapabilityRegistry should be injected");
    }
    
    @Test
    public void testSkillServiceInjection() {
        assertNotNull(skillService, "SkillService should be injected");
    }
}
```

### 4.2 集成测试

```java
package net.ooder.nexus.service;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SDK集成测试
 */
@SpringBootTest
public class SdkIntegrationTest {
    
    @Autowired
    private SdkIntegrationService sdkIntegrationService;
    
    @Test
    public void testCapabilityRegistryFunctionality() {
        CapabilityRegistry registry = sdkIntegrationService.getCapabilityRegistry();
        assertNotNull(registry);
        assertNotNull(registry.getAllCapabilities());
    }
    
    @Test
    public void testSkillServiceFunctionality() {
        SkillService service = sdkIntegrationService.getSkillService();
        assertNotNull(service);
        assertNotNull(service.getSkillRootPath());
    }
}
```

---

## 五、常见问题

### Q1: SceneEngine和DriverRegistry在哪个工程？

**A**: 
- `SceneEngine` 和 `DriverRegistry` 属于 **scene-engine工程**
- `CapabilityRegistry` 和 `SkillService` 属于 **agent-sdk-core工程**
- 需要分别添加依赖和配置

### Q2: SceneEngine需要哪些依赖项？

**A**: SceneEngine依赖以下组件：

| 依赖项 | 状态 | 处理方式 |
|--------|------|----------|
| `SceneEventPublisher` | ✅ 可自动注入 | 已有`@Component`注解 |
| `SessionManager` | ❌ 需手动注册 | 配置类中创建Bean |
| `SkillService` | ❌ 需实现 | 抽象类，需具体实现 |
| `SceneProvider` | ❌ 需自行实现 | 实现接口并提供Bean |
| `HeartbeatProvider` | ❌ 需自行实现 | 实现接口并提供Bean |
| `UserSettingsProvider` | ❌ 需自行实现 | 实现接口并提供Bean |

### Q3: SkillService为什么需要SkillPackageManager？

**A**: `SkillService` 的构造函数需要 `SkillPackageManager` 参数：
```java
public SkillService(SkillPackageManager packageManager)
```
需要在创建 `SkillService` Bean 时先创建 `SkillPackageManager`。

### Q4: 方案1和方案2哪个更好？

**A**: 
- **短期**：方案1（Nexus端配置）更快，无需修改SDK
- **长期**：方案2（SDK端修改）更好，所有使用方受益
- **推荐**：同时使用，方案1添加`@ConditionalOnMissingBean`，SDK更新后自动切换

### Q5: 如何验证注入是否成功？

**A**: 
1. 启动时查看日志：`SdkIntegrationService`会输出注入状态
2. 运行单元测试：`SdkComponentInjectionTest`
3. 检查功能：调用组件方法验证功能正常

### Q6: 如果SDK更新了怎么办？

**A**: 
- 如果SDK添加了Spring支持，方案1的`@ConditionalOnMissingBean`会自动跳过
- 如果SDK未添加Spring支持，方案1继续生效
- 建议定期评估SDK更新，适时移除Nexus端的配置

---

## 六、附录

### 6.1 相关文件路径

| 文件 | 路径 | 所属工程 |
|------|------|---------|
| CapabilityRegistry | `agent-sdk-core/.../a2a/capability/CapabilityRegistry.java` | agent-sdk |
| SkillService | `agent-sdk-core/.../service/skill/SkillService.java` | agent-sdk |
| DriverRegistry | `scene-engine/.../core/driver/DriverRegistry.java` | scene-engine |
| SceneEngine | `scene-engine/.../core/SceneEngine.java` | scene-engine |

### 6.2 依赖配置

```xml
<!-- Nexus项目pom.xml -->
<dependencies>
    <!-- agent-sdk -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-core</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- scene-engine（可选） -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine</artifactId>
        <version>2.3</version>
    </dependency>
</dependencies>
```

### 6.3 联系信息

如有问题，请联系：
- **Nexus Enterprise Team**
- **SDK开发团队**

---

**文档结束**
