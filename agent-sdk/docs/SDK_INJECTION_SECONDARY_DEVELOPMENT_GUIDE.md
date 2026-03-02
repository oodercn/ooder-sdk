# SDK组件注入二次开发指南

## 文档信息

| 属性 | 值 |
|------|-----|
| **文档版本** | 1.3 |
| **编写日期** | 2026-03-02 |
| **适用版本** | SDK v2.3 |
| **目标项目** | ooder-Nexus-Enterprise |

---

## 更新记录

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.3 | 2026-03-02 | 添加v2.3分层架构设计，新增DiscoveryCoordinator和InstallCoordinator配置 |
| 1.2 | 2026-02-28 | 初始版本，解决SDK组件注入问题 |

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
| **DiscoveryCoordinator** | `net.ooder.scene.discovery.coordinator.DiscoveryCoordinator` | **v2.3新增，需要CacheManager** |
| **InstallCoordinator** | `net.ooder.scene.skill.coordinator.InstallCoordinator` | **v2.3新增，需要SkillInstaller** |

### 1.2 v2.3分层架构说明

SDK v2.3引入了分层架构设计：

```
┌─────────────────────────────────────────┐
│  SceneEngine层（充血模型+状态控制）      │
│  - DiscoveryCoordinator（发现协调器）    │
│  - InstallCoordinator（安装协调器）      │
│  - RichSkill（充血模型）                 │
│  - InstallSession（状态机）              │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│  SDK层（贫血模型+无状态）                │
│  - SkillDiscoverer（发现接口）           │
│  - SkillInstaller（安装接口）            │
│  - SkillPackage（贫血模型）              │
└─────────────────────────────────────────┘
```

**设计原则**：
- **SDK层**：贫血模型，无状态，单一功能实现
- **SceneEngine层**：充血模型，有状态，聚合控制

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

#### 2.1.2 创建SDK配置类（scene-engine组件 - v2.3更新）

```java
package net.ooder.nexus.config;

import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.driver.DriverRegistry;
import net.ooder.scene.core.impl.SceneEngineImpl;
import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import net.ooder.scene.provider.HeartbeatProvider;
import net.ooder.scene.provider.SceneProvider;
import net.ooder.scene.provider.UserSettingsProvider;
import net.ooder.scene.session.SessionManager;
import net.ooder.scene.skill.SkillService;
import net.ooder.scene.skill.coordinator.InstallCoordinator;
import net.ooder.skills.api.SkillInstaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SDK组件配置类（scene-engine）
 * 
 * 解决scene-engine组件无法通过@Autowired注入的问题
 * 注意：需要添加scene-engine依赖
 * 
 * v2.3更新：新增DiscoveryCoordinator和InstallCoordinator配置
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
        // 需要实现HeartbeatProvider接口
        return new CustomUserSettingsProvider();
    }

    /**
     * 注册CacheManager
     * 
     * v2.3新增：DiscoveryCoordinator的依赖项
     */
    @Bean
    public CacheManager cacheManager() {
        // 使用默认实现或自定义实现
        return new DefaultCacheManager();
    }

    /**
     * 注册DiscoveryCoordinator
     * 
     * v2.3新增：发现协调器，负责缓存策略和发现编排
     */
    @Bean
    public DiscoveryCoordinator discoveryCoordinator(CacheManager cacheManager) {
        DiscoveryCoordinator coordinator = new DiscoveryCoordinator(cacheManager);
        
        // 注册SDK发现器
        coordinator.registerDiscoverer("local", new LocalDiscoverer());
        coordinator.registerDiscoverer("github", new GitHubDiscoverer());
        coordinator.registerDiscoverer("gitee", new GiteeDiscoverer());
        coordinator.registerDiscoverer("udp", new UdpDiscoverer());
        
        return coordinator;
    }

    /**
     * 注册SkillInstaller
     * 
     * v2.3新增：InstallCoordinator的依赖项
     */
    @Bean
    public SkillInstaller skillInstaller() {
        // 使用默认实现或自定义实现
        return new DefaultSkillInstaller();
    }

    /**
     * 注册InstallCoordinator
     * 
     * v2.3新增：安装协调器，负责安装状态管理
     */
    @Bean
    public InstallCoordinator installCoordinator(SkillInstaller skillInstaller) {
        return new InstallCoordinator(skillInstaller);
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
            UserSettingsProvider userSettingsProvider,
            net.ooder.scene.skill.SkillService skillService) {
        
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
import net.ooder.scene.discovery.cache.CacheManager;
import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import net.ooder.scene.skill.coordinator.InstallCoordinator;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillPackageManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SDK自动配置类
 * 
 * 使用Spring Boot自动配置机制，只在Bean不存在时创建
 * 
 * v2.3更新：新增DiscoveryCoordinator和InstallCoordinator自动配置
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
    @ConditionalOnClass(DriverRegistry.class)
    public DriverRegistry driverRegistry() {
        return new DriverRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(CacheManager.class)
    public CacheManager cacheManager() {
        return new DefaultCacheManager();
    }

    /**
     * v2.3新增：DiscoveryCoordinator自动配置
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DiscoveryCoordinator.class)
    public DiscoveryCoordinator discoveryCoordinator(CacheManager cacheManager) {
        DiscoveryCoordinator coordinator = new DiscoveryCoordinator(cacheManager);
        
        // 注册默认发现器
        coordinator.registerDiscoverer("local", new LocalDiscoverer());
        
        return coordinator;
    }

    /**
     * v2.3新增：InstallCoordinator自动配置
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(InstallCoordinator.class)
    public InstallCoordinator installCoordinator(SkillInstaller skillInstaller) {
        return new InstallCoordinator(skillInstaller);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(SceneEngine.class)
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

---

## 三、v2.3分层架构使用指南

### 3.1 发现功能使用（DiscoveryCoordinator）

```java
package net.ooder.nexus.service;

import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import net.ooder.scene.skill.model.RichSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 技能发现服务
 * 
 * 使用v2.3的DiscoveryCoordinator进行技能发现
 */
@Service
public class SkillDiscoveryService {

    @Autowired
    private DiscoveryCoordinator discoveryCoordinator;

    /**
     * 发现所有技能
     */
    public CompletableFuture<List<RichSkill>> discoverAllSkills() {
        // 从所有来源发现
        return discoveryCoordinator.discover("all");
    }

    /**
     * 从GitHub发现技能
     */
    public CompletableFuture<List<RichSkill>> discoverFromGitHub() {
        return discoveryCoordinator.discover("github");
    }

    /**
     * 搜索技能
     */
    public CompletableFuture<List<RichSkill>> searchSkills(String keyword) {
        return discoveryCoordinator.search(keyword);
    }

    /**
     * 刷新缓存
     */
    public CompletableFuture<List<RichSkill>> refreshSkills() {
        return discoveryCoordinator.refresh("all");
    }
}
```

### 3.2 安装功能使用（InstallCoordinator）

```java
package net.ooder.nexus.service;

import net.ooder.scene.skill.coordinator.InstallCoordinator;
import net.ooder.scene.skill.model.RichSkill;
import net.ooder.scene.skill.session.InstallSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 技能安装服务
 * 
 * 使用v2.3的InstallCoordinator进行技能安装
 */
@Service
public class SkillInstallService {

    @Autowired
    private InstallCoordinator installCoordinator;

    /**
     * 安装技能
     */
    public String installSkill(RichSkill skill) {
        String sessionId = installCoordinator.install(skill);
        return sessionId;
    }

    /**
     * 获取安装进度
     */
    public int getInstallProgress(String sessionId) {
        return installCoordinator.getProgress(sessionId);
    }

    /**
     * 暂停安装
     */
    public boolean pauseInstall(String sessionId) {
        return installCoordinator.pause(sessionId);
    }

    /**
     * 恢复安装
     */
    public boolean resumeInstall(String sessionId) {
        return installCoordinator.resume(sessionId);
    }

    /**
     * 取消安装
     */
    public boolean cancelInstall(String sessionId) {
        return installCoordinator.cancel(sessionId);
    }

    /**
     * 获取安装报告
     */
    public InstallSession.InstallReport getInstallReport(String sessionId) {
        return installCoordinator.getReport(sessionId);
    }
}
```

### 3.3 RichSkill充血模型使用

```java
package net.ooder.nexus.controller;

import net.ooder.scene.skill.model.RichSkill;
import net.ooder.nexus.service.SkillDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 技能管理控制器
 */
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillDiscoveryService discoveryService;

    /**
     * 获取所有技能
     */
    @GetMapping
    public CompletableFuture<List<RichSkill>> getAllSkills() {
        return discoveryService.discoverAllSkills();
    }

    /**
     * 检查技能是否可安装
     */
    @GetMapping("/{skillId}/installable")
    public boolean isInstallable(@PathVariable String skillId) {
        // 使用充血模型的业务方法
        return discoveryService.getSkillDetail(skillId)
            .thenApply(RichSkill::isInstallable)
            .join();
    }

    /**
     * 获取技能依赖
     */
    @GetMapping("/{skillId}/dependencies")
    public List<RichSkill> getDependencies(@PathVariable String skillId) {
        RichSkill skill = discoveryService.getSkillDetail(skillId).join();
        // 使用充血模型的业务方法
        return skill.getDependencies();
    }
}
```

---

## 四、详细实现步骤

### 4.1 Nexus端实施步骤

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
├── SdkComponentConfiguration.java      # agent-sdk配置
├── SceneEngineConfiguration.java       # scene-engine配置（v2.3更新）
├── SdkAutoConfiguration.java           # 自动配置
└── SdkIntegrationService.java          # 集成服务
```

#### 步骤3：创建集成服务（v2.3更新）

```java
package net.ooder.nexus.service;

import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import net.ooder.sdk.service.skill.SkillService;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.driver.DriverRegistry;
import net.ooder.scene.discovery.coordinator.DiscoveryCoordinator;
import net.ooder.scene.skill.coordinator.InstallCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * SDK集成服务
 * 
 * 验证SDK组件注入状态
 * 
 * v2.3更新：新增DiscoveryCoordinator和InstallCoordinator检查
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
    
    @Autowired(required = false)
    private DiscoveryCoordinator discoveryCoordinator;
    
    @Autowired(required = false)
    private InstallCoordinator installCoordinator;
    
    @PostConstruct
    public void init() {
        log.info("[SdkIntegration] SDK组件注入状态检查:");
        log.info("  - CapabilityRegistry: {}", capabilityRegistry != null ? "✅ 可用" : "❌ 不可用");
        log.info("  - SkillService: {}", skillService != null ? "✅ 可用" : "❌ 不可用");
        log.info("  - DriverRegistry: {}", driverRegistry != null ? "✅ 可用" : "❌ 不可用");
        log.info("  - SceneEngine: {}", sceneEngine != null ? "✅ 可用" : "❌ 不可用");
        log.info("  - DiscoveryCoordinator (v2.3): {}", discoveryCoordinator != null ? "✅ 可用" : "❌ 不可用");
        log.info("  - InstallCoordinator (v2.3): {}", installCoordinator != null ? "✅ 可用" : "❌ 不可用");
        
        if (capabilityRegistry == null || skillService == null) {
            log.warn("[SdkIntegration] 部分agent-sdk组件未注入，功能可能受限");
        }
        
        if (driverRegistry == null || sceneEngine == null) {
            log.warn("[SdkIntegration] 部分scene-engine组件未注入，功能可能受限");
        }
        
        if (discoveryCoordinator == null || installCoordinator == null) {
            log.warn("[SdkIntegration] 部分v2.3协调器组件未注入，发现/安装功能可能受限");
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
    
    public DiscoveryCoordinator getDiscoveryCoordinator() {
        return discoveryCoordinator;
    }
    
    public InstallCoordinator getInstallCoordinator() {
        return installCoordinator;
    }
}
```

---

## 五、常见问题

### Q1: DiscoveryCoordinator和InstallCoordinator是什么？

**A**: 
- **DiscoveryCoordinator**: v2.3新增的发现协调器，负责：
  - 控制缓存策略（何时使用缓存、何时刷新）
  - 聚合多个SDK发现器的结果
  - 将贫血模型（SkillPackage）转换为充血模型（RichSkill）

- **InstallCoordinator**: v2.3新增的安装协调器，负责：
  - 管理安装会话（InstallSession）
  - 协调SDK Installer执行安装
  - 处理安装状态机转换（暂停、恢复、重试）

### Q2: 为什么需要分层架构？

**A**: 
- **SDK层**：保持简单，只做单一功能，无状态判断
- **SceneEngine层**：负责复杂的业务逻辑、状态管理、策略控制
- **好处**：职责清晰，SDK可复用，SceneEngine可灵活控制

### Q3: RichSkill和SkillPackage有什么区别？

**A**: 
- **SkillPackage**（SDK层）：贫血模型，只包含数据（skillId, name, version等）
- **RichSkill**（SceneEngine层）：充血模型，包含数据+行为（isInstallable(), getDependencies()等）

### Q4: 如何扩展DiscoveryCoordinator？

**A**: 
```java
// 注册自定义发现器
discoveryCoordinator.registerDiscoverer("custom", new CustomDiscoverer());

// 自定义发现器实现
public class CustomDiscoverer implements SkillDiscoverer {
    @Override
    public CompletableFuture<List<SkillPackage>> discover() {
        // 实现发现逻辑
    }
}
```

---

## 六、参考文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 分层架构设计 | `docs/v2.3/LAYERED_ARCHITECTURE_DESIGN.md` | v2.3分层架构详细设计 |
| 发现设计 | `docs/v2.3/DISCOVERY_DESIGN.md` | 发现模块设计文档 |
| 术语映射 | `docs/v2.3/TERMINOLOGY_IMPLEMENTATION.md` | 术语变更说明 |
