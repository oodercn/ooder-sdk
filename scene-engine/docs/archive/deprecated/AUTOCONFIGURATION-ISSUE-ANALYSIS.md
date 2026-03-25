# SE SDK 自动配置设计问题分析与解决方案

## 1. 问题描述

### 1.1 问题现象

ooder-pro 集成 SE SDK 时遇到以下问题：

1. **SE 库的 spring.factories 注册了自动配置类**
2. **这些自动配置类需要各种 bean，但没有提供默认实现**
3. **SE 库的类（如 SceneEngineIntegration, SkillSDKAdapter）被包扫描发现，需要更多 bean**

### 1.2 问题根源

SE SDK 的 `spring.factories` 文件：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
net.ooder.scene.autoconfigure.SceneEngineAutoConfiguration,\
net.ooder.scene.bridge.SdkSceneGroupAutoConfiguration,\
net.ooder.scene.skill.config.KnowledgePersistenceAutoConfiguration
```

这些自动配置类会自动加载，但它们依赖的 Bean 可能不存在：

- `LlmService` - 需要 skill-llm 插件提供
- `SceneEngine` - 需要具体实现
- 其他服务接口

---

## 2. 临时解决方案

### 2.1 排除自动配置

```java
@SpringBootApplication(exclude = {
    net.ooder.scene.autoconfigure.SceneEngineAutoConfiguration.class,
    net.ooder.scene.bridge.SdkSceneGroupAutoConfiguration.class,
    net.ooder.scene.skill.config.KnowledgePersistenceAutoConfiguration.class
})
@ComponentScan(
    basePackages = {"view", "net.ooder"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "net\\.ooder\\.scene\\..*"
    )
)
```

### 2.2 问题

- 需要手动排除每个自动配置类
- 需要手动排除包扫描
- 不够优雅

---

## 3. 推荐解决方案

### 3.1 方案一：条件化自动配置

**改进 `SceneEngineAutoConfiguration`**：

```java
@Configuration
@ConditionalOnProperty(name = "scene.engine.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(SceneEngineProperties.class)
public class SceneEngineAutoConfiguration {
    // ...
}
```

**使用方式**：

```yaml
scene:
  engine:
    enabled: true  # 显式启用
```

### 3.2 方案二：提供默认实现

**为所有必需的 Bean 提供默认实现**：

```java
@Bean
@ConditionalOnMissingBean(LlmService.class)
public LlmService defaultLlmService() {
    return new NoOpLlmService();  // 空实现
}

@Bean
@ConditionalOnMissingBean(SceneEngine.class)
public SceneEngine defaultSceneEngine() {
    return new NoOpSceneEngine();  // 空实现
}
```

### 3.3 方案三：模块化自动配置

**按模块拆分自动配置**：

```properties
# 核心模块（可选）
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
net.ooder.scene.autoconfigure.SceneEngineCoreAutoConfiguration

# LLM 模块（需要显式启用）
net.ooder.scene.autoconfigure.SceneEngineLlmAutoConfiguration

# 桥接模块（需要显式启用）
net.ooder.scene.autoconfigure.SceneEngineBridgeAutoConfiguration
```

---

## 4. 改进计划

### 4.1 Phase 1：条件化自动配置

- [ ] 为所有自动配置类添加 `@ConditionalOnProperty`
- [ ] 默认禁用自动配置
- [ ] 添加配置开关

### 4.2 Phase 2：提供默认实现

- [ ] 为 `LlmService` 提供 `NoOpLlmService`
- [ ] 为 `SceneEngine` 提供 `NoOpSceneEngine`
- [ ] 为其他必需 Bean 提供空实现

### 4.3 Phase 3：模块化拆分

- [ ] 拆分自动配置为核心、LLM、桥接模块
- [ ] 每个模块独立开关
- [ ] 更新文档

---

## 5. 配置示例

### 5.1 改进后的配置

```yaml
scene:
  engine:
    enabled: true
    auto-configure:
      core: true
      llm: false
      bridge: false
```

### 5.2 使用方式

**完全启用**：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**部分启用**：

```java
@SpringBootApplication(exclude = {
    SceneEngineLlmAutoConfiguration.class,
    SceneEngineBridgeAutoConfiguration.class
})
public class Application {
    // ...
}
```

---

## 6. 状态

- [x] 问题分析完成
- [ ] 改进方案确认
- [ ] 开始实施

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**SE 团队**: SceneEngine Team
