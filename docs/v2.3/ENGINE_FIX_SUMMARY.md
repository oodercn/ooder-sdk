# Engine修复完成总结

> **版本**: 1.0  
> **日期**: 2026-03-03  
> **修复团队**: Engine团队  
> **目标用户**: KG团队

---

## 修复概览

本次修复解决了 `skill-knowledge-qa` 依赖配置问题中提出的所有核心问题。

| 问题 | 优先级 | 状态 | 修复文件 |
|------|--------|------|----------|
| 依赖自动安装机制 | P0 | ✅ 已修复 | `DependencyInstaller.java` |
| 场景能力协作机制 | P0 | ✅ 已修复 | `CollaborativeCapabilityManager.java` |
| LLM配置与Skill依赖集成 | P1 | ✅ 已修复 | `SkillDependency.java` |
| 能力提供者注册 | P2 | ✅ 已修复 | `DependencyResolver.java` |

---

## 详细修复内容

### 1. 依赖管理机制增强

#### 1.1 SkillDependency类增强

**文件**: `agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/plugin/SkillDependency.java`

**新增属性**:
```java
// 自动安装相关
private boolean autoInstall;           // 是否自动安装
private String installSource;          // 安装来源
private String fallback;               // fallback策略
private long timeout = 30000;          // 安装超时

// 配置映射和健康检查
private Map<String, String> configMapping;           // 配置映射
private HealthCheckConfig healthCheck;               // 健康检查配置
```

**新增方法**:
- `shouldAutoInstall()` - 检查是否应该尝试自动安装
- `hasFallback()` - 检查是否有fallback策略

#### 1.2 SkillYamlParser解析增强

**文件**: `agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/discovery/SkillYamlParser.java`

**新增解析字段**:
- `required` - 是否必需
- `autoInstall` - 是否自动安装
- `installSource` - 安装来源
- `fallback` - fallback策略
- `timeout` - 安装超时
- `configMapping` - 配置映射
- `healthCheck` - 健康检查配置

#### 1.3 DependencyInstaller自动安装器（新增）

**文件**: `agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/dependency/DependencyInstaller.java`

**核心功能**:
- 自动安装依赖Skill
- Fallback策略处理（embedded/optional/fail）
- 批量安装支持
- 安装状态追踪
- 超时控制

**使用示例**:
```java
DependencyInstaller installer = new DependencyInstaller(discoveryService, skillInstaller);

// 安装单个依赖
InstallResult result = installer.installDependency(dependency);

// 批量安装
BatchInstallResult batchResult = installer.installDependencies(dependencies);

// 解析并安装
BatchInstallResult result = installer.resolveAndInstall(metadata);
```

#### 1.4 DependencyResolver增强

**文件**: `agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/dependency/DependencyResolver.java`

**新增功能**:
- 集成DependencyInstaller
- 自动安装缺失依赖
- Fallback策略处理

**使用示例**:
```java
DependencyResolver resolver = new DependencyResolver(discoveryService, versionManager);
resolver.setDependencyInstaller(installer);

ResolutionResult result = resolver.resolve(metadata);
```

---

### 2. 场景能力协作机制

#### 2.1 SelfCheckConfig配置类（新增）

**文件**: `agent-sdk/skills-framework/src/main/java/net/ooder/skills/api/SelfCheckConfig.java`

**新增检查项**:
```java
private List<String> checkDependencies;        // 检查依赖Skill
private List<String> checkConfig;              // 检查配置项
private String healthCheckEndpoint;            // 健康检查端点
private OnCheckFailed onCheckFailed;           // 失败处理策略
private RetryConfig retry;                     // 重试配置
```

**失败处理策略**:
- `degrade` - 降级运行
- `fail` - 启动失败
- `continue` - 继续启动

#### 2.2 CollaborativeCapabilityManager协作管理器（新增）

**文件**: `agent-sdk/skills-framework/src/main/java/net/ooder/skills/core/collaboration/CollaborativeCapabilityManager.java`

**核心功能**:
- 协作能力注册
- 协作启动（支持可选协作）
- 接口绑定
- 超时控制
- 批量协作启动

**使用示例**:
```java
CollaborativeCapabilityManager manager = new CollaborativeCapabilityManager();
manager.setMainFirstService(mainFirstService);

// 注册协作能力
manager.registerFromManifest(manifest);

// 启动协作
CollaborationStartResult result = manager.startCollaboration(
    "scene-knowledge-qa", "scene-indexing", true, 10000);

// 绑定接口
manager.bindInterface("scene-knowledge-qa", "indexing-service", "scene-indexing");
```

---

### 3. 配置示例

#### 3.1 增强的skill.yaml配置

```yaml
spec:
  type: scene-skill
  
  dependencies:
    - id: skill-knowledge-base
      version: ">=1.0.0"
      required: true
      autoInstall: true
      installSource: https://gitee.com/ooderCN/ooder-skills/releases
      fallback: embedded
      timeout: 30s
      
    - id: skill-llm-assistant
      version: ">=1.0.0"
      required: false
      autoInstall: false
      configMapping:
        apiKey: "${ooder.llm.api-key}"
        baseUrl: "${ooder.llm.base-url}"
        model: "${ooder.llm.model}"
      healthCheck:
        enabled: true
        endpoint: /health
        interval: 30s

  sceneCapabilities:
    - id: scene-knowledge-qa
      name: 知识问答场景能力
      type: SCENE
      mainFirst: true
      
      mainFirstConfig:
        selfCheck:
          checkCapabilities: [kb-management, document-management, kb-search]
          checkDriverCapabilities: [intent-receiver, event-listener]
          checkDependencies: [skill-knowledge-base, skill-indexing]
          checkConfig: [ooder.llm.api-key]
          
          onCheckFailed:
            action: degrade
            degradedCapabilities: [rag-retrieval]
            retry:
              maxAttempts: 3
              delay: 5s
              
        selfStart:
          - installDependencies: auto
          - initDriverCapabilities: [intent-receiver, event-listener]
          - initCapabilities: [kb-management, document-management, kb-search]
          - bindAddresses: auto
          
        startCollaboration:
          - startScene: scene-indexing
            optional: true
            timeout: 10s
            bindInterface: indexing-service

  collaborativeCapabilities:
    - capabilityId: scene-indexing
      role: PROVIDER
      interface: indexing-service
      autoStart: true
      optional: true
      timeout: 10000
```

---

## 迁移指南

### 从临时方案迁移到正式方案

#### 步骤1: 更新skill.yaml

将依赖从 `required: false` 改为 `required: true`，并添加 `autoInstall: true`:

```yaml
dependencies:
  - id: skill-knowledge-base
    version: ">=1.0.0"
    required: true        # 改为true
    autoInstall: true     # 启用自动安装
    fallback: embedded    # 设置fallback策略
```

#### 步骤2: 移除内置实现（可选）

如果依赖Skill已可用，可以移除内置实现:

```java
// 移除或注释掉内置实现
// @Service
// public class DocumentIndexService { ... }
```

#### 步骤3: 恢复协作配置

```yaml
selfCheck:
  - checkCollaborative: [scene-indexing]
  - checkDependencies: [skill-knowledge-base]
  
startCollaboration:
  - startScene: scene-indexing
    optional: true
    bindInterface: indexing-service
```

---

## API使用示例

### 自动安装依赖

```java
@Autowired
private DependencyInstaller dependencyInstaller;

public void installSkillDependencies(SkillMetadata metadata) {
    BatchInstallResult result = dependencyInstaller.resolveAndInstall(metadata);
    
    if (result.isAllSuccess()) {
        log.info("All dependencies installed successfully");
    } else {
        log.warn("Some dependencies failed: {}", result.getFailures());
    }
}
```

### 启动协作场景

```java
@Autowired
private CollaborativeCapabilityManager collaborationManager;

public void startCollaboration(String mainSceneId) {
    // 启动协作（可选协作）
    CollaborationStartResult result = collaborationManager.startCollaboration(
        mainSceneId, 
        "scene-indexing",
        true,    // optional
        10000    // timeout
    );
    
    if (result.isSuccess()) {
        log.info("Collaboration started successfully");
    } else if (result.isSkipped()) {
        log.warn("Collaboration skipped: {}", result.getMessage());
    } else {
        log.error("Collaboration failed: {}", result.getMessage());
    }
}
```

### 自检配置

```java
@Autowired
private MainFirstService mainFirstService;

public void performSelfCheck(String capabilityId) {
    SelfCheckConfig config = new SelfCheckConfig();
    config.setCheckCapabilities(Arrays.asList("kb-management", "document-management"));
    config.setCheckDependencies(Arrays.asList("skill-knowledge-base"));
    config.setCheckConfig(Arrays.asList("ooder.llm.api-key"));
    
    SelfCheckConfig.OnCheckFailed onCheckFailed = new SelfCheckConfig.OnCheckFailed();
    onCheckFailed.setAction("degrade");
    onCheckFailed.setDegradedCapabilities(Arrays.asList("rag-retrieval"));
    config.setOnCheckFailed(onCheckFailed);
    
    Map<String, Object> checkConfig = new HashMap<>();
    checkConfig.put("selfCheckConfig", config);
    
    CompletableFuture<MainFirstService.SelfCheckResult> future = 
        mainFirstService.selfCheck(capabilityId, checkConfig);
    
    SelfCheckResult result = future.join();
    if (result.isPassed()) {
        log.info("Self check passed");
    } else {
        log.warn("Self check failed: {}", result.getFailedItems());
    }
}
```

---

## 测试验证

### 单元测试

```java
@Test
public void testAutoInstall() {
    SkillDependency dependency = new SkillDependency();
    dependency.setId("skill-knowledge-base");
    dependency.setVersion(">=1.0.0");
    dependency.setRequired(true);
    dependency.setAutoInstall(true);
    dependency.setInstallSource("https://gitee.com/ooderCN/ooder-skills/releases");
    
    InstallResult result = installer.installDependency(dependency);
    assertTrue(result.isSuccess());
}

@Test
public void testFallback() {
    SkillDependency dependency = new SkillDependency();
    dependency.setId("skill-indexing");
    dependency.setRequired(true);
    dependency.setFallback("embedded");
    
    // 当依赖不存在时，应该使用embedded fallback
    InstallResult result = installer.installDependency(dependency);
    assertTrue(result.isSuccess());
    assertEquals("Using embedded implementation", result.getMessage());
}
```

---

## 联系方式

- **Engine团队**: 负责依赖管理机制实现
- **KG团队**: 负责skill-knowledge-qa开发
- **文档位置**: `docs/v2.3/ENGINE_FIX_SUMMARY.md`

---

## 附录

### 新增文件列表

| 文件 | 路径 | 说明 |
|------|------|------|
| `DependencyInstaller.java` | `agent-sdk/agent-sdk-core/.../dependency/` | 依赖自动安装器 |
| `SelfCheckConfig.java` | `agent-sdk/skills-framework/.../api/` | 增强自检配置 |
| `CollaborativeCapabilityManager.java` | `agent-sdk/skills-framework/.../collaboration/` | 协作能力管理器 |

### 修改文件列表

| 文件 | 路径 | 修改内容 |
|------|------|----------|
| `SkillDependency.java` | `agent-sdk/agent-sdk-core/.../plugin/` | 新增autoInstall等属性 |
| `SkillYamlParser.java` | `agent-sdk/agent-sdk-core/.../discovery/` | 解析新增字段 |
| `DependencyResolver.java` | `agent-sdk/agent-sdk-core/.../dependency/` | 集成自动安装和fallback |

