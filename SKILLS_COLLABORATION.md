# Ooder SDK v2.3 与 Skills 协作文档

> **版本**: 2.3  
> **日期**: 2026-02-27  
> **状态**: 开发中

---

## 一、协作概述

本文档定义 Ooder SDK v2.3 与上层 Skills 的协作边界，明确哪些功能由 SDK 提供底层支撑，哪些由 Skills 实现业务逻辑。

---

## 二、功能分工矩阵

### 2.1 连接测试功能

| 功能 | SDK 底层支撑 | Skills 实现 | 协作方式 |
|------|-------------|------------|---------|
| **数据库连接测试** | `ooder-server` 提供基础连接测试 API | ❌ 无需 Skill | SDK 直接提供 |
| **MQTT 连接测试** | `ooder-msg-web` 提供 MQTT 客户端 | `skill-mqtt` 实现 Broker 测试 | SDK 提供客户端，Skill 提供 Broker |
| **能力端点连接测试** | `agent-sdk-api` 定义接口，`agent-sdk-core` 实现 | ❌ 无需 Skill | SDK 直接提供 |

### 2.2 性能监控功能

| 功能 | SDK 底层支撑 | Skills 实现 | 协作方式 |
|------|-------------|------------|---------|
| **性能指标采集** | `scene-engine` 提供运行时指标 API | `skill-monitor` 采集和存储 | SDK 暴露指标，Skill 采集分析 |
| **性能历史数据** | `scene-engine` 提供历史查询 API | `skill-monitor` 存储历史 | SDK 提供数据，Skill 管理存储 |

### 2.3 流程管理功能

| 功能 | SDK 底层支撑 | Skills 实现 | 协作方式 |
|------|-------------|------------|---------|
| **场景启动流程** | `scene-engine` 提供流程编排引擎 | ❌ 无需 Skill | SDK 直接提供 |
| **流程状态跟踪** | `scene-engine` 提供状态查询 API | ❌ 无需 Skill | SDK 直接提供 |
| **流程控制** | `scene-engine` 提供暂停/恢复/回滚 API | ❌ 无需 Skill | SDK 直接提供 |

### 2.4 配置管理功能

| 功能 | SDK 底层支撑 | Skills 实现 | 协作方式 |
|------|-------------|------------|---------|
| **配置历史** | `ooder-config` 提供版本管理 API | ❌ 无需 Skill | SDK 直接提供 |
| **配置回滚** | `ooder-config` 提供回滚 API | ❌ 无需 Skill | SDK 直接提供 |
| **配置导入导出** | `ooder-config` 提供序列化 API | ❌ 无需 Skill | SDK 直接提供 |

### 2.5 事件和日志功能

| 功能 | SDK 底层支撑 | Skills 实现 | 协作方式 |
|------|-------------|------------|---------|
| **运行时事件** | `scene-engine` 提供事件发布/订阅 API | ❌ 无需 Skill | SDK 直接提供 |
| **运行时日志** | `scene-engine` 提供日志记录 API | ❌ 无需 Skill | SDK 直接提供 |

### 2.6 服务健康功能

| 功能 | SDK 底层支撑 | Skills 实现 | 协作方式 |
|------|-------------|------------|---------|
| **服务健康检查** | `ooder-server` 提供健康检查 API | `skill-health` 实现检查逻辑 | SDK 提供检查框架，Skill 实现具体检查 |
| **健康报告生成** | `ooder-server` 提供报告 API | `skill-health` 生成报告 | SDK 提供数据，Skill 生成报告 |

### 2.7 能力状态功能

| 功能 | SDK 底层支撑 | Skills 实现 | 协作方式 |
|------|-------------|------------|---------|
| **能力运行状态** | `agent-sdk-core` 提供状态查询 API | ❌ 无需 Skill | SDK 直接提供 |
| **能力统计** | `agent-sdk-core` 提供统计数据 API | ❌ 无需 Skill | SDK 直接提供 |

---

## 三、已存在 Skills 清单

### 3.1 可直接使用的 Skills

| Skill | 版本 | 功能 | 覆盖需求 | 状态 |
|-------|------|------|---------|------|
| `skill-monitor` | 2.3.0 | 监控服务 | 性能监控 (2.1, 2.2) | ✅ 已存在 |
| `skill-health` | 0.7.3 | 健康检查 | 服务健康 (6.1) | ✅ 已存在 |
| `skill-mqtt` | 0.7.1 | MQTT 服务 | MQTT 连接测试 (1.2) | ✅ 已存在 |

### 3.2 Skills 依赖的 SDK 接口

#### skill-monitor 依赖

```java
// scene-engine 提供
public interface PerformanceMonitor {
    CompletableFuture<CurrentMetrics> getCurrentMetrics(String sceneId);
    CompletableFuture<PerformanceHistory> getPerformanceHistory(String sceneId, long startTime, long endTime, int interval);
}
```

#### skill-health 依赖

```java
// ooder-server 提供
public interface ServiceHealthMonitor {
    CompletableFuture<Map<String, ServiceHealth>> getServicesHealth(String sceneId);
}

// agent-sdk-core 提供
public interface CapabilityStatusMonitor {
    CompletableFuture<Map<String, CapabilityStatus>> getCapabilitiesStatus(String sceneId);
}
```

#### skill-mqtt 依赖

```java
// ooder-msg-web 提供
public interface MqttConnectionTestService {
    CompletableFuture<ConnectionTestResult> testMqttConnection(MqttConfig config);
}
```

---

## 四、SDK 需要新增的功能

### 4.1 agent-sdk-api (接口定义)

#### 新增 ConnectionTestService 接口

```java
package net.ooder.sdk.api.connection;

import java.util.concurrent.CompletableFuture;

/**
 * 连接测试服务接口
 * 提供能力端点连接测试功能
 *
 * @author ooder
 * @since 2.3
 */
public interface ConnectionTestService {
    
    /**
     * 测试能力端点连接
     * @param endpoint 能力端点配置
     * @return 连接测试结果
     */
    CompletableFuture<ConnectionTestResult> testCapabilityEndpoint(CapabilityEndpoint endpoint);
}

/**
 * 能力端点配置
 */
public class CapabilityEndpoint {
    private String capabilityId;
    private String interfaceId;
    private String endpoint;
    private int timeout;
    // getters/setters
}

/**
 * 连接测试结果
 */
public class ConnectionTestResult {
    private boolean success;
    private int latency;
    private String message;
    private String errorMessage;
    // getters/setters
}
```

### 4.2 agent-sdk-core (接口实现)

#### 实现 ConnectionTestService

```java
package net.ooder.sdk.core.connection;

import net.ooder.sdk.api.connection.ConnectionTestService;
import net.ooder.sdk.api.connection.CapabilityEndpoint;
import net.ooder.sdk.api.connection.ConnectionTestResult;

/**
 * 连接测试服务实现
 *
 * @author ooder
 * @since 2.3
 */
public class ConnectionTestServiceImpl implements ConnectionTestService {
    
    @Override
    public CompletableFuture<ConnectionTestResult> testCapabilityEndpoint(CapabilityEndpoint endpoint) {
        // 实现能力端点连接测试
    }
}
```

### 4.3 scene-engine (场景监控)

#### 新增 SceneMonitor 接口

```java
package net.ooder.scene.monitor;

import java.util.concurrent.CompletableFuture;

/**
 * 场景监控接口
 * 统一暴露场景监控相关功能
 *
 * @author ooder
 * @since 2.3
 */
public interface SceneMonitor {
    
    // 连接测试
    ConnectionTestService getConnectionTestService();
    
    // 性能监控
    PerformanceMonitor getPerformanceMonitor();
    
    // 流程管理
    SceneFlowManager getFlowManager();
    
    // 配置管理
    SceneConfigManager getConfigManager();
    
    // 事件管理
    SceneEventManager getEventManager();
    
    // 日志管理
    SceneLogManager getLogManager();
    
    // 服务健康
    ServiceHealthMonitor getServiceHealthMonitor();
    
    // 能力状态
    CapabilityStatusMonitor getCapabilityStatusMonitor();
}
```

### 4.4 ooder-server (服务健康)

#### 新增 ServiceHealthMonitor 接口

```java
package net.ooder.server.health;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务健康监控接口
 *
 * @author ooder
 * @since 2.3
 */
public interface ServiceHealthMonitor {
    
    /**
     * 获取服务健康状态
     * @param sceneId 场景ID
     * @return 服务健康状态映射
     */
    CompletableFuture<Map<String, ServiceHealth>> getServicesHealth(String sceneId);
    
    /**
     * 测试数据库连接
     * @param config 数据库配置
     * @return 连接测试结果
     */
    CompletableFuture<ConnectionTestResult> testDatabaseConnection(DatabaseConfig config);
}
```

### 4.5 ooder-config (配置管理)

#### 扩展 SceneConfigManager 接口

```java
package net.ooder.config;

import java.util.concurrent.CompletableFuture;

/**
 * 场景配置管理器扩展
 *
 * @author ooder
 * @since 2.3
 */
public interface SceneConfigManager {
    
    // 现有方法...
    
    /**
     * 获取配置历史
     * @param sceneId 场景ID
     * @return 配置历史
     */
    CompletableFuture<ConfigHistory> getConfigHistory(String sceneId);
    
    /**
     * 回滚配置到指定版本
     * @param sceneId 场景ID
     * @param version 版本号
     * @return 是否成功
     */
    CompletableFuture<Boolean> rollbackConfig(String sceneId, int version);
    
    /**
     * 导出配置
     * @param sceneId 场景ID
     * @param format 格式 (json, yaml)
     * @return 配置内容
     */
    CompletableFuture<String> exportConfig(String sceneId, String format);
    
    /**
     * 导入配置
     * @param sceneId 场景ID
     * @param configContent 配置内容
     * @param format 格式 (json, yaml)
     * @return 是否成功
     */
    CompletableFuture<Boolean> importConfig(String sceneId, String configContent, String format);
}
```

---

## 五、协作接口定义

### 5.1 SDK 暴露给 Skills 的接口

```java
// scene-engine 暴露
public interface SceneMonitor {
    PerformanceMonitor getPerformanceMonitor();
    ServiceHealthMonitor getServiceHealthMonitor();
    CapabilityStatusMonitor getCapabilityStatusMonitor();
}

// ooder-server 暴露
public interface ServiceHealthMonitor {
    CompletableFuture<Map<String, ServiceHealth>> getServicesHealth(String sceneId);
}

// agent-sdk-core 暴露
public interface CapabilityStatusMonitor {
    CompletableFuture<Map<String, CapabilityStatus>> getCapabilitiesStatus(String sceneId);
}
```

### 5.2 Skills 需要实现的接口

```java
// skill-monitor 实现
@Component
public class SkillPerformanceMonitor implements PerformanceMonitor {
    // 实现性能监控逻辑
}

// skill-health 实现
@Component
public class SkillHealthChecker implements HealthChecker {
    // 实现健康检查逻辑
}
```

---

## 六、开发顺序建议

### 第一阶段：SDK 基础功能（高优先级）

1. **agent-sdk-api**: 新增 `ConnectionTestService` 接口
2. **agent-sdk-core**: 实现 `ConnectionTestService`
3. **ooder-server**: 新增 `ServiceHealthMonitor` 和数据库连接测试
4. **scene-engine**: 新增 `SceneMonitor` 接口

### 第二阶段：SDK 扩展功能（中优先级）

1. **scene-engine**: 实现 `PerformanceMonitor`, `SceneFlowManager`, `SceneEventManager`, `SceneLogManager`
2. **agent-sdk-core**: 实现 `CapabilityStatusMonitor`
3. **ooder-config**: 扩展配置管理功能

### 第三阶段：Skills 适配（低优先级）

1. **skill-monitor**: 适配新的 `PerformanceMonitor` 接口
2. **skill-health**: 适配新的 `ServiceHealthMonitor` 接口
3. **skill-mqtt**: 适配新的连接测试接口

---

## 七、版本兼容性

### SDK v2.3 兼容性

- 所有新增接口都标记 `@since 2.3`
- 保持向后兼容，不破坏现有接口
- Skills 可以选择性使用新接口

### Skills 适配要求

| Skill | 最低 SDK 版本 | 适配工作 |
|-------|--------------|---------|
| skill-monitor | 2.3 | 适配新的 PerformanceMonitor 接口 |
| skill-health | 2.3 | 适配新的 ServiceHealthMonitor 接口 |
| skill-mqtt | 2.3 | 使用新的连接测试接口 |

---

## 八、联系方式

如有协作问题，请联系：
- **SDK 团队**: sdk-team@ooder.net
- **Skills 团队**: skills-team@ooder.net
- **GitHub Issues**: https://github.com/oodercn/ooder-sdk/issues

---

**Made with ❤️ by Ooder Team**
