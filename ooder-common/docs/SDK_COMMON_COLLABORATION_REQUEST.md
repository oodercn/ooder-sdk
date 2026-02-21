# SDK 场景配置协作请求

## 文档信息

| 项目 | 内容 |
|------|------|
| 发起方 | SDK Team / northbound-services |
| 接收方 | ooder-common (ooderAgent 企业版) |
| 版本 | v1.0 |
| 创建日期 | 2026-02-19 |
| 状态 | 待确认 |

---

## 一、协作概述

### 1.1 背景

SDK 7.2 版本正在实施南北向分层架构，需要 ooder-common 项目提供基础设施支持。本次协作主要涉及配置模板、VFS 服务端接口、以及核心组件的迁移适配。

### 1.2 协作目标

| 目标 | 说明 | 优先级 |
|------|------|--------|
| 配置标准化 | 提供统一的 SDK 配置模板 | P0 |
| VFS 服务端扩展 | 实现场景/组/Skill/Agent/链路 REST API | P0 |
| 组件迁移适配 | ConfigObserver 和 SceneGroupManager 迁移 | P1 |

### 1.3 协作项目关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           SDK 7.2 架构                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  northbound-services (北向服务)                                      │   │
│  │  - 场景配置管理  - 能力注册  - 数据源管理                             │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                      │                                      │
│                                      ▼                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  ooder-common (基础设施)                                             │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │ ooder-config│ │ ooder-vfs   │ │ ooder-msg   │ │ ooder-server│   │   │
│  │  │ 配置管理     │ │ 文件存储     │ │ 消息通信     │ │ 微服务支撑   │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                      │                                      │
│                                      ▼                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  guper-Agent/agent-sdk (SDK 核心)                                   │   │
│  │  - CoreMessage  - CoreTransport  - ProtocolHub  - CommandPacket     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、协作需求详情

### 2.1 需求一：配置文件模板

#### 2.1.1 需求描述

提供 `sdk-config.yaml` 标准模板，支持 SDK 场景配置的统一管理。

#### 2.1.2 模板规范

```yaml
# sdk-config.yaml - SDK 场景配置标准模板
# 版本: 1.0
# 维护: ooder-common / SDK Team

sdk:
  # ==================== 基础配置 ====================
  version: "7.2"
  sceneId: ${SDK_SCENE_ID:default-scene}
  sceneName: ${SDK_SCENE_NAME:Default Scene}
  environment: ${SDK_ENV:development}
  
  # ==================== 网络配置 ====================
  network:
    mode: ${SDK_NETWORK_MODE:southbound}
    endpoint: ${SDK_ENDPOINT:http://localhost:8080}
    timeout: ${SDK_TIMEOUT:30000}
    retryCount: ${SDK_RETRY_COUNT:3}
    
  # ==================== 北向配置 ====================
  northbound:
    enabled: ${SDK_NORTH_ENABLED:false}
    udpPort: ${SDK_NORTH_UDP_PORT:9001}
    p2pEnabled: ${SDK_NORTH_P2P:false}
    multicastGroup: ${SDK_NORTH_MULTICAST:239.255.255.250}
    
  # ==================== 南向配置 ====================
  southbound:
    httpEnabled: ${SDK_SOUTH_HTTP:true}
    mqttEnabled: ${SDK_SOUTH_MQTT:false}
    mqttBroker: ${SDK_MQTT_BROKER:tcp://localhost:1883}
    
  # ==================== 安全配置 ====================
  security:
    authEnabled: ${SDK_AUTH_ENABLED:true}
    tokenHeader: ${SDK_TOKEN_HEADER:Authorization}
    tokenPrefix: ${SDK_TOKEN_PREFIX:Bearer}
    encryptEnabled: ${SDK_ENCRYPT_ENABLED:false}
    encryptAlgorithm: ${SDK_ENCRYPT_ALG:AES-256}
    
  # ==================== 缓存配置 ====================
  cache:
    enabled: ${SDK_CACHE_ENABLED:true}
    type: ${SDK_CACHE_TYPE:memory}
    maxSize: ${SDK_CACHE_MAX_SIZE:10485760}
    expireTime: ${SDK_CACHE_EXPIRE:300000}
    
  # ==================== 日志配置 ====================
  logging:
    level: ${SDK_LOG_LEVEL:INFO}
    format: ${SDK_LOG_FORMAT:json}
    output: ${SDK_LOG_OUTPUT:console}
    
  # ==================== 监控配置 ====================
  monitoring:
    enabled: ${SDK_MONITORING_ENABLED:true}
    metricsInterval: ${SDK_METRICS_INTERVAL:60000}
    prometheusEnabled: ${SDK_PROMETHEUS:false}
    prometheusPort: ${SDK_PROMETHEUS_PORT:9090}
```

#### 2.1.3 配置加载接口

```java
package net.ooder.common.config;

public interface SdkConfigLoader {
    
    SdkConfig load(String configPath);
    
    SdkConfig loadFromClasspath(String configName);
    
    SdkConfig loadFromEnvironment();
    
    void reload(String configPath);
    
    void addConfigChangeListener(ConfigChangeListener listener);
}

public class SdkConfig implements Serializable {
    
    private String version;
    private String sceneId;
    private String sceneName;
    private String environment;
    
    private NetworkConfig network;
    private NorthboundConfig northbound;
    private SouthboundConfig southbound;
    private SecurityConfig security;
    private CacheConfig cache;
    private LoggingConfig logging;
    private MonitoringConfig monitoring;
}
```

#### 2.1.4 交付要求

| 项目 | 要求 |
|------|------|
| 模板文件 | 放置于 `ooder-config/src/main/resources/templates/sdk-config.yaml` |
| 加载器实现 | 在 `ooder-config` 模块实现 `SdkConfigLoader` |
| 环境变量支持 | 支持 `${VAR:default}` 格式的环境变量替换 |
| 热更新支持 | 支持配置文件变更监听和自动重载 |

---

### 2.2 需求二：VFS 服务端接口扩展

#### 2.2.1 需求描述

在 `ooder-vfs-web` 模块中扩展 REST API，支持场景/组/Skill/Agent/链路的管理。

#### 2.2.2 API 端点设计

##### 场景管理 API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/vfs/scene` | GET | 获取场景列表 |
| `/api/vfs/scene/{sceneId}` | GET | 获取场景详情 |
| `/api/vfs/scene` | POST | 创建场景 |
| `/api/vfs/scene/{sceneId}` | PUT | 更新场景 |
| `/api/vfs/scene/{sceneId}` | DELETE | 删除场景 |
| `/api/vfs/scene/{sceneId}/activate` | POST | 激活场景 |
| `/api/vfs/scene/{sceneId}/deactivate` | POST | 停用场景 |

##### 组管理 API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/vfs/group` | GET | 获取组列表 |
| `/api/vfs/group/{groupId}` | GET | 获取组详情 |
| `/api/vfs/group` | POST | 创建组 |
| `/api/vfs/group/{groupId}` | PUT | 更新组 |
| `/api/vfs/group/{groupId}` | DELETE | 删除组 |
| `/api/vfs/group/{groupId}/scenes` | GET | 获取组内场景列表 |
| `/api/vfs/group/{groupId}/scene/{sceneId}` | POST | 添加场景到组 |

##### Skill 管理 API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/vfs/skill` | GET | 获取 Skill 列表 |
| `/api/vfs/skill/{skillId}` | GET | 获取 Skill 详情 |
| `/api/vfs/skill` | POST | 注册 Skill |
| `/api/vfs/skill/{skillId}` | PUT | 更新 Skill |
| `/api/vfs/skill/{skillId}` | DELETE | 注销 Skill |
| `/api/vfs/skill/{skillId}/capabilities` | GET | 获取 Skill 能力列表 |
| `/api/vfs/skill/{skillId}/status` | GET | 获取 Skill 状态 |

##### Agent 管理 API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/vfs/agent` | GET | 获取 Agent 列表 |
| `/api/vfs/agent/{agentId}` | GET | 获取 Agent 详情 |
| `/api/vfs/agent` | POST | 注册 Agent |
| `/api/vfs/agent/{agentId}` | PUT | 更新 Agent |
| `/api/vfs/agent/{agentId}` | DELETE | 注销 Agent |
| `/api/vfs/agent/{agentId}/skills` | GET | 获取 Agent 关联的 Skill |
| `/api/vfs/agent/{agentId}/status` | GET | 获取 Agent 状态 |
| `/api/vfs/agent/{agentId}/heartbeat` | POST | Agent 心跳 |

##### 链路管理 API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/vfs/chain` | GET | 获取链路列表 |
| `/api/vfs/chain/{chainId}` | GET | 获取链路详情 |
| `/api/vfs/chain` | POST | 创建链路 |
| `/api/vfs/chain/{chainId}` | PUT | 更新链路 |
| `/api/vfs/chain/{chainId}` | DELETE | 删除链路 |
| `/api/vfs/chain/{chainId}/execute` | POST | 执行链路 |
| `/api/vfs/chain/{chainId}/status` | GET | 获取链路执行状态 |

#### 2.2.3 数据模型

```java
package net.ooder.vfs.model;

public class VfsScene implements Serializable {
    
    private String sceneId;
    private String sceneName;
    private String sceneVersion;
    private String description;
    
    private SceneStatus status;
    private String environment;
    
    private String groupId;
    private List<String> skillIds;
    private List<String> agentIds;
    
    private Map<String, Object> config;
    private SecurityPolicy securityPolicy;
    
    private long createTime;
    private long updateTime;
    private String createBy;
}

public class VfsGroup implements Serializable {
    
    private String groupId;
    private String groupName;
    private String description;
    
    private GroupType type;
    private GroupStatus status;
    
    private List<String> sceneIds;
    private List<String> agentIds;
    
    private Map<String, Object> metadata;
    
    private long createTime;
    private long updateTime;
}

public class VfsSkill implements Serializable {
    
    private String skillId;
    private String skillName;
    private String skillVersion;
    private String description;
    
    private SkillType type;
    private SkillStatus status;
    
    private List<String> capabilityIds;
    private List<String> dataSourceIds;
    
    private String endpoint;
    private int timeout;
    
    private Map<String, Object> config;
    private Map<String, Object> metadata;
}

public class VfsAgent implements Serializable {
    
    private String agentId;
    private String agentName;
    private String agentVersion;
    private String description;
    
    private AgentType type;
    private AgentStatus status;
    
    private String endpoint;
    private String groupId;
    private List<String> skillIds;
    
    private long lastHeartbeat;
    private Map<String, Object> metadata;
}

public class VfsChain implements Serializable {
    
    private String chainId;
    private String chainName;
    private String description;
    
    private ChainStatus status;
    private List<ChainStep> steps;
    
    private String triggerType;
    private String triggerConfig;
    
    private long createTime;
    private long updateTime;
}

public class ChainStep implements Serializable {
    
    private String stepId;
    private String stepName;
    private int order;
    
    private String skillId;
    private String capabilityId;
    
    private Map<String, Object> input;
    private Map<String, Object> output;
    
    private String condition;
    private String errorHandler;
}
```

#### 2.2.4 交付要求

| 项目 | 要求 |
|------|------|
| API 实现 | 在 `ooder-vfs-web` 模块实现所有 REST API |
| 数据模型 | 在 `net.ooder.vfs.model` 包下定义数据模型 |
| 服务接口 | 定义 `VfsSceneService`, `VfsGroupService`, `VfsSkillService`, `VfsAgentService`, `VfsChainService` |
| 权限控制 | 集成现有的权限控制机制 |
| API 文档 | 生成 OpenAPI/Swagger 文档 |

---

### 2.3 需求三：ConfigObserver 迁移

#### 2.3.1 需求描述

将 `ConfigObserver` 组件迁移到 ooder-common，实现 `SyncListener` 接口以支持配置同步。

#### 2.3.2 接口定义

```java
package net.ooder.common.config.sync;

public interface SyncListener {
    
    void onSyncStart(SyncEvent event);
    
    void onSyncProgress(SyncEvent event, int progress);
    
    void onSyncComplete(SyncEvent event, SyncResult result);
    
    void onSyncError(SyncEvent event, Exception error);
    
    void onSyncCancel(SyncEvent event);
}

public class SyncEvent implements Serializable {
    
    private String syncId;
    private String sourceId;
    private String targetId;
    private SyncType syncType;
    private SyncDirection direction;
    
    private long startTime;
    private long endTime;
    private int totalItems;
    private int processedItems;
    
    private Map<String, Object> metadata;
    
    public enum SyncType {
        FULL,       // 全量同步
        INCREMENTAL, // 增量同步
        DELTA       // 差异同步
    }
    
    public enum SyncDirection {
        PUSH,       // 推送
        PULL,       // 拉取
        BIDIRECTIONAL // 双向
    }
}

public class SyncResult implements Serializable {
    
    private boolean success;
    private int createdCount;
    private int updatedCount;
    private int deletedCount;
    private int skippedCount;
    private int errorCount;
    
    private List<SyncError> errors;
    private Map<String, Object> statistics;
}
```

#### 2.3.3 ConfigObserver 实现

```java
package net.ooder.common.config.observer;

public class ConfigObserver implements SyncListener {
    
    private ConfigManager configManager;
    private SyncCoordinator syncCoordinator;
    
    @Override
    public void onSyncStart(SyncEvent event) {
        log.info("Config sync started: {}", event.getSyncId());
    }
    
    @Override
    public void onSyncProgress(SyncEvent event, int progress) {
        log.debug("Config sync progress: {}%", progress);
    }
    
    @Override
    public void onSyncComplete(SyncEvent event, SyncResult result) {
        log.info("Config sync completed: created={}, updated={}, deleted={}",
            result.getCreatedCount(), result.getUpdatedCount(), result.getDeletedCount());
        
        configManager.refreshCache();
        notifyConfigChange(event);
    }
    
    @Override
    public void onSyncError(SyncEvent event, Exception error) {
        log.error("Config sync error: {}", event.getSyncId(), error);
        handleSyncError(event, error);
    }
    
    @Override
    public void onSyncCancel(SyncEvent event) {
        log.warn("Config sync cancelled: {}", event.getSyncId());
    }
    
    private void notifyConfigChange(SyncEvent event) {
        List<ConfigChangeListener> listeners = configManager.getListeners();
        for (ConfigChangeListener listener : listeners) {
            listener.onConfigChanged(event);
        }
    }
}
```

#### 2.3.4 交付要求

| 项目 | 要求 |
|------|------|
| 接口定义 | 在 `ooder-config` 模块定义 `SyncListener` 接口 |
| 实现迁移 | 将 `ConfigObserver` 迁移到 `ooder-config` 模块 |
| 事件机制 | 实现配置变更事件发布/订阅机制 |
| 线程安全 | 确保同步过程中的线程安全 |

---

### 2.4 需求四：SceneGroupManager 迁移

#### 2.4.1 需求描述

将 `SceneGroupManager` 迁移到 ooder-common，使用 SDK 的持久化实现。

#### 2.4.2 接口定义

```java
package net.ooder.common.scene;

public interface SceneGroupManager {
    
    SceneGroup createGroup(SceneGroupCreateRequest request);
    
    SceneGroup updateGroup(String groupId, SceneGroupUpdateRequest request);
    
    void deleteGroup(String groupId);
    
    SceneGroup getGroup(String groupId);
    
    List<SceneGroup> listGroups(GroupQueryRequest request);
    
    void addSceneToGroup(String groupId, String sceneId);
    
    void removeSceneFromGroup(String groupId, String sceneId);
    
    List<Scene> getGroupScenes(String groupId);
    
    void activateGroup(String groupId);
    
    void deactivateGroup(String groupId);
    
    GroupStatus getGroupStatus(String groupId);
    
    List<GroupEvent> getGroupEvents(String groupId);
}

public class SceneGroup implements Serializable {
    
    private String groupId;
    private String groupName;
    private String description;
    
    private GroupType type;
    private GroupStatus status;
    
    private List<String> sceneIds;
    private List<String> agentIds;
    
    private SceneGroupConfig config;
    private Map<String, Object> metadata;
    
    private long createTime;
    private long updateTime;
    private String createBy;
}

public class SceneGroupConfig implements Serializable {
    
    private boolean autoActivate;
    private int maxScenes;
    private int maxAgents;
    
    private FailoverPolicy failoverPolicy;
    private LoadBalancePolicy loadBalancePolicy;
    
    private Map<String, Object> customConfig;
}
```

#### 2.4.3 持久化实现

```java
package net.ooder.common.scene.persistence;

public interface SceneGroupPersistence {
    
    void save(SceneGroup group);
    
    void update(SceneGroup group);
    
    void delete(String groupId);
    
    SceneGroup load(String groupId);
    
    List<SceneGroup> loadAll();
    
    void addScene(String groupId, String sceneId);
    
    void removeScene(String groupId, String sceneId);
    
    List<String> loadScenes(String groupId);
}

public class DatabaseSceneGroupPersistence implements SceneGroupPersistence {
    
    private DBAgent dbAgent;
    
    @Override
    public void save(SceneGroup group) {
        String sql = "INSERT INTO scene_group (group_id, group_name, description, type, status, config, metadata, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        dbAgent.execute(sql, 
            group.getGroupId(),
            group.getGroupName(),
            group.getDescription(),
            group.getType().name(),
            group.getStatus().name(),
            toJson(group.getConfig()),
            toJson(group.getMetadata()),
            group.getCreateTime());
    }
    
    @Override
    public SceneGroup load(String groupId) {
        String sql = "SELECT * FROM scene_group WHERE group_id = ?";
        return dbAgent.queryForObject(sql, SceneGroup.class, groupId);
    }
}
```

#### 2.4.4 数据库表结构

```sql
CREATE TABLE scene_group (
    group_id VARCHAR(64) PRIMARY KEY,
    group_name VARCHAR(256) NOT NULL,
    description VARCHAR(512),
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    config TEXT,
    metadata TEXT,
    create_time BIGINT NOT NULL,
    update_time BIGINT,
    create_by VARCHAR(64)
);

CREATE TABLE scene_group_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id VARCHAR(64) NOT NULL,
    scene_id VARCHAR(64) NOT NULL,
    join_time BIGINT NOT NULL,
    UNIQUE KEY uk_group_scene (group_id, scene_id)
);

CREATE TABLE scene_group_event (
    event_id VARCHAR(64) PRIMARY KEY,
    group_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    event_data TEXT,
    event_time BIGINT NOT NULL,
    INDEX idx_group_id (group_id)
);
```

#### 2.4.5 交付要求

| 项目 | 要求 |
|------|------|
| 接口定义 | 在 `ooder-config` 模块定义 `SceneGroupManager` 接口 |
| 持久化实现 | 支持数据库持久化，使用 `ooder-database` 模块 |
| 缓存支持 | 集成 `ooder-common-client` 的 Redis 缓存 |
| 事件发布 | 实现组变更事件发布机制 |

---

## 三、协作任务分解

### 3.1 任务清单

| 任务ID | 任务名称 | 负责方 | 优先级 | 预计工时 |
|--------|---------|--------|--------|---------|
| CO-001 | sdk-config.yaml 模板 | ooder-common | P0 | 1天 |
| CO-002 | SdkConfigLoader 实现 | ooder-common | P0 | 2天 |
| CO-003 | 场景管理 API | ooder-common | P0 | 3天 |
| CO-004 | 组管理 API | ooder-common | P0 | 2天 |
| CO-005 | Skill 管理 API | ooder-common | P1 | 2天 |
| CO-006 | Agent 管理 API | ooder-common | P1 | 2天 |
| CO-007 | 链路管理 API | ooder-common | P1 | 3天 |
| CO-008 | SyncListener 接口 | ooder-common | P1 | 1天 |
| CO-009 | ConfigObserver 迁移 | ooder-common | P1 | 2天 |
| CO-010 | SceneGroupManager 迁移 | ooder-common | P1 | 3天 |
| CO-011 | 数据库表结构 | ooder-common | P1 | 1天 |
| CO-012 | API 文档 | ooder-common | P2 | 1天 |

### 3.2 依赖关系

```
CO-001 (模板) ──▶ CO-002 (加载器)
                        │
                        ▼
CO-003 (场景API) ──▶ CO-004 (组API) ──▶ CO-005 (Skill API)
        │                   │                   │
        │                   ▼                   ▼
        │           CO-006 (Agent API) ──▶ CO-007 (链路API)
        │
        ▼
CO-008 (SyncListener) ──▶ CO-009 (ConfigObserver)
                                    │
                                    ▼
                            CO-010 (SceneGroupManager) ──▶ CO-011 (数据库)
```

---

## 四、接口对接规范

### 4.1 响应格式

```java
public class ApiResponse<T> implements Serializable {
    
    private int code;
    private String message;
    private T data;
    private long timestamp;
    private String traceId;
    
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
```

### 4.2 错误码定义

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 500 | 服务器内部错误 |
| 503 | 服务不可用 |

### 4.3 分页格式

```java
public class PageResponse<T> implements Serializable {
    
    private List<T> items;
    private int pageNum;
    private int pageSize;
    private long total;
    private int totalPages;
}
```

---

## 五、验收标准

### 5.1 功能验收

| 验收项 | 验收标准 |
|--------|---------|
| 配置模板 | 模板文件存在，支持环境变量替换 |
| 配置加载 | 加载器能正确加载配置，支持热更新 |
| API 功能 | 所有 API 端点可正常访问，返回正确响应 |
| 数据持久化 | 数据能正确存储和读取 |
| 事件机制 | 配置变更事件能正确触发和监听 |

### 5.2 性能验收

| 指标 | 目标值 |
|------|--------|
| API 响应时间 | < 200ms |
| 配置加载时间 | < 100ms |
| 批量操作吞吐量 | > 1000 TPS |

### 5.3 文档验收

- [ ] API 文档完整
- [ ] 配置说明文档完整
- [ ] 使用示例完整

---

## 六、时间计划

### 6.1 里程碑

| 里程碑 | 目标 | 预计完成 |
|--------|------|---------|
| M1 | 配置模板和加载器完成 | Week 1 |
| M2 | 场景/组 API 完成 | Week 2 |
| M3 | Skill/Agent/链路 API 完成 | Week 3 |
| M4 | 组件迁移完成 | Week 4 |

### 6.2 交付物

| 交付物 | 位置 |
|--------|------|
| sdk-config.yaml | ooder-config/src/main/resources/templates/ |
| SdkConfigLoader | ooder-config/src/main/java/net/ooder/common/config/ |
| VFS API | ooder-vfs-web/src/main/java/net/ooder/vfs/api/ |
| SyncListener | ooder-config/src/main/java/net/ooder/common/config/sync/ |
| SceneGroupManager | ooder-config/src/main/java/net/ooder/common/scene/ |

---

## 七、联系方式

| 项目 | 联系人 |
|------|--------|
| SDK Team | - |
| ooder-common | - |
| northbound-services | - |

---

## 八、变更记录

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| v1.0 | 2026-02-19 | 初始版本 |

---

**文档状态**: 待确认  
**下一步**: ooder-common 团队确认需求和交付时间
