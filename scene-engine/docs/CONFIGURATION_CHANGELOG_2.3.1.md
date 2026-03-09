# Scene Engine v2.3.1 配置修改说明

**版本**: v2.3.1  
**日期**: 2026-03-08  
**状态**: 正式发布

---

## 一、版本概述

v2.3.1 版本新增了以下功能：

- 决策引擎配置
- MVEL 规则引擎配置
- 知识能力配置
- LLM Provider 增强
- 向量存储自动配置
- **场景组初始化配置** (新增)

---

## 二、新增配置项

### 2.1 决策引擎配置

#### 2.1.1 决策模式配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.decision.mode` | `ONLINE_FIRST` | 决策模式：ONLINE_ONLY, OFFLINE_ONLY, ONLINE_FIRST |
| `scene.decision.cache.enabled` | `true` | 是否启用决策结果缓存 |
| `scene.decision.cache.ttl` | `300000` | 缓存过期时间（毫秒），默认5分钟 |
| `scene.decision.timeout` | `30000` | LLM决策超时时间（毫秒） |

#### 2.1.2 配置示例

```yaml
scene:
  decision:
    mode: ONLINE_FIRST
    cache:
      enabled: true
      ttl: 300000
    timeout: 30000
```

#### 2.1.3 决策模式说明

| 模式 | 配置值 | 说明 | 适用场景 |
|------|--------|------|----------|
| 仅在线 | `ONLINE_ONLY` | 仅使用 LLM 决策 | 对准确性要求高，网络稳定 |
| 仅离线 | `OFFLINE_ONLY` | 仅使用规则引擎 | 离线环境、性能敏感场景 |
| 优先在线 | `ONLINE_FIRST` | 优先 LLM，失败降级规则 | 默认模式，兼顾准确性和可用性 |

---

### 2.2 MVEL 规则引擎配置

#### 2.2.1 基本配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.rule.engine.cache-size` | `1000` | 规则编译缓存大小 |
| `scene.rule.engine.sandbox.enabled` | `true` | 是否启用安全沙箱 |
| `scene.rule.engine.timeout` | `5000` | 规则执行超时时间（毫秒） |

#### 2.2.2 安全沙箱配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.rule.sandbox.allowed-classes` | - | 允许访问的类列表 |
| `scene.rule.sandbox.allowed-packages` | `java.lang,java.util` | 允许访问的包列表 |
| `scene.rule.sandbox.denied-methods` | `exit,exec` | 禁止调用的方法 |

#### 2.2.3 配置示例

```yaml
scene:
  rule:
    engine:
      cache-size: 1000
      sandbox:
        enabled: true
      timeout: 5000
    sandbox:
      allowed-packages:
        - java.lang
        - java.util
        - java.text
      denied-methods:
        - exit
        - exec
        - getRuntime
```

---

### 2.3 知识能力配置

#### 2.3.1 三层架构配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.knowledge.layer.general.enabled` | `true` | 是否启用通用知识层 |
| `scene.knowledge.layer.professional.enabled` | `true` | 是否启用专业模块层 |
| `scene.knowledge.layer.scene.enabled` | `true` | 是否启用场景知识层 |

#### 2.3.2 检索策略配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.knowledge.search.default-top-k` | `5` | 默认返回结果数 |
| `scene.knowledge.search.default-threshold` | `0.7` | 默认相似度阈值 |
| `scene.knowledge.search.cross-layer.enabled` | `true` | 是否启用跨层检索 |

#### 2.3.3 配置示例

```yaml
scene:
  knowledge:
    layer:
      general:
        enabled: true
      professional:
        enabled: true
      scene:
        enabled: true
    search:
      default-top-k: 5
      default-threshold: 0.7
      cross-layer:
        enabled: true
```

---

### 2.4 LLM Provider 配置

#### 2.4.1 基本配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.llm.provider.default-model` | `gpt-3.5-turbo` | 默认模型 |
| `scene.llm.provider.timeout` | `60000` | 请求超时时间（毫秒） |
| `scene.llm.provider.max-retries` | `3` | 最大重试次数 |

#### 2.4.2 Function Calling 配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.llm.function-calling.enabled` | `true` | 是否启用 Function Calling |
| `scene.llm.function-calling.max-iterations` | `5` | 最大迭代次数 |

#### 2.4.3 配置示例

```yaml
scene:
  llm:
    provider:
      default-model: gpt-3.5-turbo
      timeout: 60000
      max-retries: 3
    function-calling:
      enabled: true
      max-iterations: 5
```

---

### 2.5 向量存储配置

#### 2.5.1 自动配置说明

v2.3.1 提供向量存储自动配置，遵循 **微（降级）→ 小 → 大** 架构原则：

| 层级 | 实现类 | 触发条件 | 适用场景 |
|------|--------|----------|----------|
| 微（降级） | `InMemoryVectorStore` | 无外部实现 | 开发测试、离线场景 |
| 小 | `SqliteVectorStore` | 配置 SQLite | 小团队、边缘部署 |
| 大 | `MilvusVectorStore` | 配置 Milvus | 大规模生产环境 |

#### 2.5.2 向量维度配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.vector.dimension` | `1536` | 向量维度（需与嵌入模型匹配） |
| `scene.vector.store.type` | `memory` | 存储类型：memory, sqlite, milvus |

#### 2.5.3 配置示例

```yaml
scene:
  vector:
    dimension: 1536
    store:
      type: memory
```

---

### 2.6 场景组初始化配置 (新增)

#### 2.6.1 基本配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.group.init.min-members` | `1` | 场景组最小成员数 |
| `scene.group.init.max-members` | `10` | 场景组最大成员数 |
| `scene.group.init.heartbeat-interval` | `5000` | 心跳间隔（毫秒） |
| `scene.group.init.heartbeat-timeout` | `15000` | 心跳超时（毫秒） |
| `scene.group.init.key-threshold` | `2` | 密钥门限值 |
| `scene.group.init.auto-failover` | `true` | 是否自动故障转移 |

#### 2.6.2 路由策略配置

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `scene.group.routing.strategy` | `priority` | 路由策略：priority, round-robin, random, least-load |

#### 2.6.3 配置示例

```yaml
scene:
  group:
    init:
      min-members: 1
      max-members: 10
      heartbeat-interval: 5000
      heartbeat-timeout: 15000
      key-threshold: 2
      auto-failover: true
    routing:
      strategy: priority
```

---

## 三、Spring Boot 自动配置

### 3.1 自动配置类

| 配置类 | 说明 |
|--------|------|
| `SceneEngineAutoConfiguration` | Scene Engine 核心自动配置 |
| `VectorStoreAutoConfiguration` | 向量存储自动配置 |
| `SceneGroupAutoConfiguration` | 场景组初始化自动配置 (新增) |

### 3.2 条件装配

使用 `@ConditionalOnMissingBean` 实现条件装配：

```java
@Bean
@ConditionalOnMissingBean(VectorStore.class)
public VectorStore vectorStore() {
    return new InMemoryVectorStore(DEFAULT_DIMENSION);
}
```

**说明**：当容器中没有其他 `VectorStore` 实现时，自动创建 `InMemoryVectorStore`。

### 3.3 覆盖默认配置

如需覆盖默认实现，只需在应用中定义同名 Bean：

```java
@Configuration
public class MyVectorStoreConfig {
    
    @Bean
    public VectorStore vectorStore() {
        return new MilvusVectorStore(milvusConfig);
    }
}
```

---

## 四、配置文件示例

### 4.1 完整配置示例

```yaml
scene:
  decision:
    mode: ONLINE_FIRST
    cache:
      enabled: true
      ttl: 300000
    timeout: 30000
  
  rule:
    engine:
      cache-size: 1000
      sandbox:
        enabled: true
      timeout: 5000
    sandbox:
      allowed-packages:
        - java.lang
        - java.util
        - java.text
      denied-methods:
        - exit
        - exec
  
  knowledge:
    layer:
      general:
        enabled: true
      professional:
        enabled: true
      scene:
        enabled: true
    search:
      default-top-k: 5
      default-threshold: 0.7
      cross-layer:
        enabled: true
  
  llm:
    provider:
      default-model: gpt-3.5-turbo
      timeout: 60000
      max-retries: 3
    function-calling:
      enabled: true
      max-iterations: 5
  
  vector:
    dimension: 1536
    store:
      type: memory
  
  group:
    init:
      min-members: 1
      max-members: 10
      heartbeat-interval: 5000
      heartbeat-timeout: 15000
      key-threshold: 2
      auto-failover: true
    routing:
      strategy: priority
```

### 4.2 开发环境配置

```yaml
scene:
  decision:
    mode: OFFLINE_ONLY
  rule:
    engine:
      sandbox:
        enabled: false
  vector:
    store:
      type: memory
  group:
    init:
      min-members: 1
      max-members: 3
```

### 4.3 生产环境配置

```yaml
scene:
  decision:
    mode: ONLINE_FIRST
    cache:
      enabled: true
  rule:
    engine:
      sandbox:
        enabled: true
  llm:
    provider:
      default-model: gpt-4
      timeout: 120000
  vector:
    store:
      type: milvus
  group:
    init:
      min-members: 3
      max-members: 20
      auto-failover: true
```

---

## 五、配置迁移指南

### 5.1 从 v2.3 升级

v2.3.1 向后兼容 v2.3，无需修改现有配置。新增配置项均有默认值。

### 5.2 新增依赖

```xml
<!-- MVEL 表达式引擎 -->
<dependency>
    <groupId>org.mvel</groupId>
    <artifactId>mvel2</artifactId>
    <version>2.5.0.Final</version>
</dependency>

<!-- Agent SDK Core 2.3.1 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 5.3 配置检查清单

| 检查项 | 说明 |
|--------|------|
| 决策模式 | 根据业务需求选择合适的决策模式 |
| 安全沙箱 | 生产环境必须启用规则沙箱 |
| 向量维度 | 确保与嵌入模型维度匹配 |
| 超时配置 | 根据网络环境调整超时时间 |
| 场景组成员数 | 根据业务需求调整 min/max members |
| 路由策略 | 根据负载情况选择合适的路由策略 |

---

## 六、常见问题

### Q1: 如何切换决策模式？

```java
// 方式1: 配置文件
scene.decision.mode=OFFLINE_ONLY

// 方式2: 代码设置
decisionEngine.setMode(DecisionMode.OFFLINE_ONLY);

// 方式3: 单次请求指定
DecisionContext context = DecisionContext.builder()
    .query("测试查询")
    .mode(DecisionMode.OFFLINE_ONLY)
    .build();
```

### Q2: 如何自定义规则沙箱？

```java
@Configuration
public class RuleSandboxConfig {
    
    @Bean
    public MvelRuleEngine mvelRuleEngine() {
        MvelRuleEngineImpl engine = new MvelRuleEngineImpl();
        engine.addAllowedClass("com.example.MyClass");
        engine.addDeniedMethod("dangerousMethod");
        return engine;
    }
}
```

### Q3: 如何配置多模型支持？

```java
@Bean
public EnhancedLlmProvider enhancedLlmProvider(LlmService llmService) {
    AbstractLlmProvider provider = new AbstractLlmProviderImpl(llmService);
    
    provider.registerModel("gpt-4", ModelConfig.builder()
        .modelId("gpt-4")
        .maxTokens(8000)
        .build());
    
    provider.registerModel("gpt-3.5-turbo", ModelConfig.builder()
        .modelId("gpt-3.5-turbo")
        .maxTokens(4000)
        .build());
    
    provider.setDefaultModel("gpt-3.5-turbo");
    return provider;
}
```

### Q4: 如何配置场景组初始化？

```java
@Bean
public SceneGroupInitializer sceneGroupInitializer(
        SceneGroupManager sceneGroupManager,
        CapRegistry capRegistry,
        SceneEventPublisher eventPublisher,
        UnifiedSkillRegistry skillRegistry) {
    return new SceneGroupInitializer(
        sceneGroupManager,
        capRegistry,
        eventPublisher,
        skillRegistry
    );
}
```

---

## 七、版本历史

| 版本 | 日期 | 变更 |
|------|------|------|
| 2.3.1 | 2026-03-08 | 新增决策引擎、MVEL规则引擎、知识能力、LLM Provider增强、场景组初始化相关配置 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-08
