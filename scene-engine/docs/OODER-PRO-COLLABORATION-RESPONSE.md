# SE 协作需求回复 - ooder-pro

## 1. 协作概述

**发起方**: ooder-pro 团队  
**接收方**: SE 团队  
**主题**: SceneEngineAutoConfiguration 与 Spring Boot 集成  
**日期**: 2026-03-20  
**状态**: 🟢 已完成

---

## 2. 需求分析

### 2.1 ooder-pro 需求

| 需求 | 说明 |
|------|------|
| SceneEngineAutoConfiguration | Spring Boot 自动配置 |
| SceneEngine Bean 注入 | 提供 SceneEngine 核心接口 |
| 服务提供者接口 | 提供 SPI 服务访问 |
| 能力发现与注册 | 支持从 skill-index 发现能力 |

### 2.2 SE SDK 现状

| 功能 | 状态 | 文件 |
|------|------|------|
| SceneEngineAutoConfiguration | ✅ 已实现 | `SceneEngineAutoConfiguration.java` |
| SceneEngine 接口 | ✅ 已实现 | `SceneEngine.java` |
| SceneEngineServiceProvider | ✅ 已实现 | `SceneEngineServiceProvider.java` |
| SceneEngineIntegration | ✅ 已实现 | `SceneEngineIntegration.java` |

---

## 3. 接口说明

### 3.1 SceneEngine 核心接口

```java
public interface SceneEngine {
    // 用户登录
    SceneClient login(String username, String password);
    SceneClient login(String token);
    AdminClient adminLogin(String username, String password);
    
    // 会话管理
    void logout(String sessionId);
    SessionInfo getSession(String sessionId);
    boolean validateSession(String sessionId);
    SessionInfo refreshSession(String sessionId);
    
    // 引擎控制
    EngineStatus getStatus();
    void start();
    void stop();
    String getName();
    String getVersion();
    boolean isRunning();
    
    // 服务获取
    <T> T getService(Class<T> serviceType);
    <T> T getService(String serviceId, Class<T> serviceType);
    Object execute(String command, Object... args);
    
    // 能力管理
    void registerCapability(String capabilityId, Object capability);
    void unregisterCapability(String capabilityId);
    boolean hasCapability(String capabilityId);
    
    // 配置与服务
    SceneEngineConfig getConfig();
    SkillService getSkillService();
    SceneProvider getSceneProvider();
    UserSettingsProvider getUserSettingsProvider();
    HeartbeatProvider getHeartbeatProvider();
}
```

### 3.2 SceneEngineServiceProvider SPI 接口

```java
public interface SceneEngineServiceProvider {
    ConversationStorageService getStorageService();
    ConversationService getConversationService();
    KnowledgeBaseService getKnowledgeBaseService();
    TerminologyService getTerminologyService();
    InteractionFeedbackService getInteractionFeedbackService();
    ToolRegistry getToolRegistry();
    ToolOrchestrator getToolOrchestrator();
    String getProviderName();
    String getProviderVersion();
    boolean isServiceAvailable(Class<?> serviceType);
}
```

### 3.3 SceneEngineIntegration 发现服务

```java
@Component
public class SceneEngineIntegration {
    // 从索引目录发现能力
    List<CapabilityDTO> discoverFromIndex(File indexDir);
    
    // 从远程仓库发现能力
    List<CapabilityDTO> discoverFromRemote(String repoUrl, String branch, String basePath);
    
    // 批量注册能力
    BatchRegisterResult registerCapabilities(List<CapabilityDTO> capabilities);
    
    // 注册单个能力
    void registerCapability(CapabilityDTO capability);
    
    // 钩子管理
    void addDiscoveryHook(SceneEngineDiscoveryHook hook);
    void removeDiscoveryHook(SceneEngineDiscoveryHook hook);
}
```

---

## 4. 使用方式

### 4.1 Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 4.2 Spring Boot 自动配置

```java
// 自动注入 SceneEngine
@Autowired
private SceneEngine sceneEngine;

// 自动注入 SceneEngineIntegration
@Autowired
private SceneEngineIntegration sceneEngineIntegration;
```

### 4.3 使用 SceneEngine

```java
// 用户登录
SceneClient client = sceneEngine.login("username", "password");

// 获取服务
SkillService skillService = sceneEngine.getSkillService();
SceneProvider sceneProvider = sceneEngine.getSceneProvider();

// 注册能力
sceneEngine.registerCapability("my-capability", myCapabilityImpl);

// 执行命令
Object result = sceneEngine.execute("myCommand", arg1, arg2);
```

### 4.4 使用 SPI 服务

```java
// 通过 ServiceLoader 获取
ServiceLoader<SceneEngineServiceProvider> loader = 
    ServiceLoader.load(SceneEngineServiceProvider.class);

SceneEngineServiceProvider provider = loader.findFirst()
    .orElseThrow(() -> new IllegalStateException("Provider not found"));

// 获取服务
ConversationService conversationService = provider.getConversationService();
KnowledgeBaseService knowledgeBaseService = provider.getKnowledgeBaseService();
```

### 4.5 使用发现服务

```java
// 从索引目录发现能力
File indexDir = new File("./skill-index");
List<CapabilityDTO> capabilities = sceneEngineIntegration.discoverFromIndex(indexDir);

// 批量注册
BatchRegisterResult result = sceneEngineIntegration.registerCapabilities(capabilities);
System.out.println("成功: " + result.getSuccessCount());
System.out.println("失败: " + result.getFailedCount());
```

---

## 5. 配置项

### 5.1 application.yml

```yaml
scene:
  engine:
    name: ooder-scene-engine
    version: 2.3.1
    auto-start: true
    
  discovery:
    enabled: true
    index-path: ./skill-index
    auto-register: true
    
  session:
    timeout: 3600000
    max-sessions: 1000
```

---

## 6. 能力发现目录结构

```
skill-index/
├── categories.yaml          # 分类定义
├── scene-drivers.yaml       # 场景驱动定义
├── skills/
│   ├── skill1.yaml
│   └── skill2.yaml
└── scenes/
    ├── scene1.yaml
    └── scene2.yaml
```

---

## 7. 验收状态

| 序号 | 验收项 | 状态 |
|------|--------|------|
| 1 | SceneEngineAutoConfiguration 存在 | ✅ |
| 2 | SceneEngine Bean 可注入 | ✅ |
| 3 | SceneEngineServiceProvider SPI 可用 | ✅ |
| 4 | SceneEngineIntegration 发现服务可用 | ✅ |
| 5 | 能力注册功能正常 | ✅ |

---

## 8. 结论

**SE SDK 2.3.1 已完整支持 ooder-pro 的协作需求**：

- ✅ `SceneEngineAutoConfiguration` - Spring Boot 自动配置
- ✅ `SceneEngine` - 核心接口
- ✅ `SceneEngineServiceProvider` - SPI 服务提供者
- ✅ `SceneEngineIntegration` - 能力发现与注册

ooder-pro 可以直接使用 SE SDK 2.3.1 进行集成开发。

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**SE 团队**: SceneEngine Team
