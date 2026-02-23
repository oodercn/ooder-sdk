# Skill Discovery Protocol - v0.7.3

## 1. Overview

The Skill Discovery Protocol defines how skills are discovered, registered, and made available to consumers. This protocol enables a decentralized, self-organizing network of skills that can be found and used without centralized configuration.

v0.7.3 adds offline support, event bus integration, and enhanced discovery methods.

### 1.1 Design Goals

- **Zero Configuration**: Skills can be discovered without manual configuration
- **Decentralized**: No single point of failure in skill discovery
- **Real-time**: Changes in skill availability are propagated quickly
- **Secure**: Skills are authenticated before being used
- **v0.7.3 New**: Offline mode support
- **v0.7.3 New**: Event-driven discovery

### 1.2 v0.7.3 Upgrades

| Feature | Description |
|---------|-------------|
| DiscoveryProtocol | Multi-path discovery (UDP/DHT/SkillCenter/mDNS) |
| OfflineService | Offline skill discovery support |
| EventBus | Event-driven discovery notifications |
| Cache Enhancement | Offline skill cache |

### 1.3 Discovery Methods

| Method | Code | Scope | Latency | Use Case |
|--------|------|-------|---------|----------|
| UDP Broadcast | `UDP_BROADCAST` | Local Network | Low | LAN discovery |
| DHT (Kademlia) | `DHT_KADEMLIA` | Global | Medium | P2P discovery |
| SkillCenter API | `SKILL_CENTER` | Global | Low | Centralized catalog |
| mDNS/DNS-SD | `MDNS_DNS_SD` | Local Network | Low | Service discovery |
| GitHub | `GITHUB` | Global | Medium | GitHub repository discovery |
| Gitee | `GITEE` | Global | Medium | Gitee repository discovery |
| Git Repository | `GIT_REPOSITORY` | Global | Medium | Generic Git repository discovery |
| Local Filesystem | `LOCAL_FS` | Local | Very Low | Local development |
| Auto Detect | `AUTO` | - | - | Auto detect discovery method |

## 2. Core Interfaces

### 2.1 DiscoveryProtocolAdapter

```java
public interface DiscoveryProtocolAdapter {

    CompletableFuture<DiscoveryResult> discoverPeers(DiscoveryRequest request);

    CompletableFuture<List<Peer>> listDiscoveredPeers();

    CompletableFuture<Peer> discoverMcp();

    CompletableFuture<Peer> getPeer(String peerId);

    CompletableFuture<Boolean> isPeerOnline(String peerId);

    void addDiscoveryListener(DiscoveryEventListener listener);

    void removeDiscoveryListener(DiscoveryEventListener listener);
}
```

### 2.2 SkillPackageManager

```java
public interface SkillPackageManager {
    
    CompletableFuture<SkillPackage> discover(String skillId, DiscoveryMethod method);
    
    CompletableFuture<List<SkillPackage>> discoverAll(DiscoveryMethod method);
    
    CompletableFuture<List<SkillPackage>> discoverByScene(String sceneId, DiscoveryMethod method);
    
    CompletableFuture<InstallResult> install(InstallRequest request);
    
    CompletableFuture<UninstallResult> uninstall(String skillId);
    
    CompletableFuture<UpdateResult> update(String skillId, String version);
    
    CompletableFuture<List<InstalledSkill>> listInstalled();
    
    CompletableFuture<InstalledSkill> getInstalled(String skillId);
    
    CompletableFuture<Boolean> isInstalled(String skillId);
    
    CompletableFuture<List<SkillPackage>> search(String query, DiscoveryMethod method);
    
    CompletableFuture<List<SkillPackage>> searchByCapability(String capabilityId, DiscoveryMethod method);
    
    String getSkillRootPath();
    
    void setSkillRootPath(String path);
}
```

### 2.3 OfflineDiscoveryService

```java
public interface OfflineDiscoveryService {
    
    List<SkillInfo> getCachedSkills();
    
    Optional<SkillInfo> getCachedSkill(String skillId);
    
    boolean isSkillCached(String skillId);
    
    CompletableFuture<SyncResult> syncNow();
}
```

## 3. Data Models

### 3.1 DiscoveryRequest

```java
public class DiscoveryRequest {
    private String requestId;        // 请求唯一标识
    private DiscoveryType type;      // 发现类型
    private int timeout;             // 超时时间（毫秒）
    private String targetNetwork;    // 目标网络
    private int maxPeers;            // 最大返回节点数
    private List<String> peerTypes;  // 节点类型过滤
    private String filter;           // 自定义过滤条件
}
```

### 3.2 DiscoveryResult

```java
public class DiscoveryResult {
    private boolean success;         // 是否成功
    private String message;          // 结果消息
    private List<Peer> peers;        // 发现的节点列表
    private int totalFound;          // 总发现数量
    private long duration;           // 耗时（毫秒）
}
```

### 3.3 Peer

```java
public class Peer {
    private String peerId;           // 节点唯一标识
    private String peerName;         // 节点名称
    private String peerType;         // 节点类型：MCP/ROUTE/END
    private String address;          // 网络地址
    private int port;                // 端口号
    private String status;           // 状态：ONLINE/OFFLINE
    private long lastSeen;           // 最后在线时间
    private long registeredAt;       // 注册时间
    private List<String> capabilities; // 能力列表
    private String version;          // 协议版本

    public boolean isOnline() {
        return "ONLINE".equals(status);
    }
}
```

### 3.4 SkillInfo

```java
public class SkillInfo {
    private String skillId;          // 技能唯一标识
    private String name;             // 技能名称
    private String version;          // 版本号
    private String description;      // 描述
    private String type;             // 类型：enterprise-skill/tool-skill/integration-skill
    private List<String> capabilities; // 能力列表
    private List<String> scenes;     // 场景列表
    private String endpoint;         // 服务端点
    private boolean offlineSupport;  // 是否支持离线
    private long registeredAt;       // 注册时间
    private long lastHeartbeat;      // 最后心跳时间
}
```

## 4. Skill Registration

### 4.1 Registration Message

When a skill starts, it broadcasts a registration message:

```
SKILL_REGISTER:{agentId};{skillId};{version};{skillType};{endpoint};{capabilities};{scenes};{timestamp};{signature}
```

**Field Description:**

| Field | Type | Description |
|-------|------|-------------|
| agentId | string | Unique agent identifier |
| skillId | string | Skill identifier (e.g., skill-org-feishu) |
| version | string | Skill version (semantic versioning) |
| skillType | string | enterprise-skill, tool-skill, integration-skill |
| endpoint | string | Service endpoint (host:port) |
| capabilities | string | Comma-separated capability IDs |
| scenes | string | Comma-separated scene names |
| timestamp | long | Registration timestamp (epoch milliseconds) |
| signature | string | HMAC signature for verification |

**Example:**

```
SKILL_REGISTER:agent-001;skill-org-feishu;0.7.3;enterprise-skill;192.168.1.100:8080;org-data-read,user-auth;auth;1707868800000;a1b2c3d4e5f6...
```

### 4.2 Registration Flow

```
┌─────────────────┐                              ┌─────────────────┐
│    Skill        │                              │   MCPAgent      │
│  (RouteAgent)   │                              │                 │
└─────────────────┘                              └─────────────────┘
         │                                                │
         │  1. SKILL_REGISTER                             │
         │───────────────────────────────────────────────▶│
         │                                                │
         │  2. SKILL_REGISTER_ACK                         │
         │◀───────────────────────────────────────────────│
         │                                                │
         │  3. JOIN_SCENE (scene: auth)                   │
         │───────────────────────────────────────────────▶│
         │                                                │
         │  4. JOIN_SCENE_RESPONSE                        │
         │◀───────────────────────────────────────────────│
         │     (sceneId, groupId, members)                │
         │                                                │
         │  5. HEARTBEAT (periodic)                       │
         │───────────────────────────────────────────────▶│
         │                                                │
         │  6. SkillRegisteredEvent (v0.7.3)              │
         │◀───────────────────────────────────────────────│
         │                                                │
```

## 5. Skill Discovery

### 5.1 Discovery Request

A consumer can request skill discovery:

```
SKILL_DISCOVER:{requesterId};{capabilityFilter};{sceneFilter};{typeFilter};{timestamp}
```

### 5.2 Discovery Response

```
SKILL_DISCOVER_RESPONSE:{requesterId};{skills};{timestamp}
```

**Skills Format:**

```
skillId1|version1|endpoint1|capabilities1|scenes1;skillId2|version2|endpoint2|capabilities2|scenes2
```

## 6. Scene-Based Discovery

### 6.1 Scene Request

Instead of discovering individual skills, consumers can request a scene:

```java
SceneJoinResult result = sdk.requestScene("auth", Arrays.asList("org-data-read", "user-auth"));

if (result.isJoined()) {
    String endpoint = result.getConnectionInfo().get("endpoint");
    String apiKey = result.getConnectionInfo().get("apiKey");
}
```

### 6.2 Connection Info Resolution

```json
{
  "sceneId": "auth-001",
  "groupId": "group-auth-001",
  "connectionInfo": {
    "protocol": "http",
    "host": "192.168.1.100",
    "port": 8080,
    "basePath": "/api",
    "authType": "api-key",
    "authHeader": "X-API-Key",
    "apiKey": "sk-xxxxx"
  },
  "skillInfo": {
    "skillId": "skill-org-feishu",
    "version": "0.7.3",
    "capabilities": ["org-data-read", "user-auth"]
  },
  "offline": false
}
```

## 7. Offline Discovery (v0.7.3 New)

### 7.1 Offline Cache

```yaml
discovery:
  offline:
    enabled: true
    cachePath: ./data/skill-cache
    maxAge: 86400000
    maxSkills: 100
```

### 7.2 Offline Discovery Flow

```
Network Disconnected
    │
    ▼
Enable Offline Mode
    │
    ├── Use cached skill registry
    ├── Return cached skills
    └── Publish OfflineModeEnabledEvent
    │
    ▼
Network Connected
    │
    ▼
Sync Skill Registry
    │
    ├── Fetch updated skills
    ├── Update local cache
    └── Publish SyncCompletedEvent
```

## 8. Event-Driven Discovery (v0.7.3 New)

### 8.1 Discovery Events

| Event Type | Description |
|------------|-------------|
| SkillRegisteredEvent | Skill registered successfully |
| SkillUnregisteredEvent | Skill unregistered |
| SkillDiscoveredEvent | New skill discovered |
| SkillAvailableEvent | Skill becomes available |
| SkillUnavailableEvent | Skill becomes unavailable |
| DiscoveryCompletedEvent | Discovery process completed |

### 8.2 Event Subscription

```java
@PostConstruct
public void init() {
    eventBus.subscribe(SkillRegisteredEvent.class, this::onSkillRegistered);
    eventBus.subscribe(SkillDiscoveredEvent.class, this::onSkillDiscovered);
    eventBus.subscribe(SkillAvailableEvent.class, this::onSkillAvailable);
}
```

## 9. Heartbeat and Health

### 9.1 Heartbeat Message

```
SKILL_HEARTBEAT:{agentId};{skillId};{status};{timestamp};{signature}
```

**Status Values:**

| Status | Description |
|--------|-------------|
| HEALTHY | Skill is healthy and available |
| DEGRADED | Skill is running but degraded |
| UNHEALTHY | Skill is unhealthy |
| MAINTENANCE | Skill is under maintenance |
| OFFLINE | Skill is in offline mode (v0.7.3 new) |

### 9.2 Health Check Configuration

```yaml
healthCheck:
  heartbeatInterval: 5000
  timeout: 30000
  retryCount: 3
  unhealthyThreshold: 3
  offlineThreshold: 5
```

## 10. Security

### 10.1 Message Signing

All discovery messages must be signed using SHA256withECDSA.

### 10.2 Certificate Chain

```
Root CA (ooder.net)
    │
    └── SkillCenter CA
            │
            ├── Skill Certificate (skill-org-feishu)
            └── Agent Certificate (agent-001)
```

## 11. Caching and Refresh

### 11.1 Local Cache

```yaml
cache:
  enabled: true
  ttl: 300000
  maxSize: 1000
  refreshBeforeExpiry: 60000
```

### 11.2 Cache Invalidation

Cache is invalidated when:
- TTL expires
- Skill unregisters
- Skill health status changes
- Manual refresh requested
- Offline mode changes (v0.7.3 new)

## 12. Error Handling

| Code | Description | Recovery Action |
|------|-------------|-----------------|
| DISC_001 | Discovery timeout | Retry with exponential backoff |
| DISC_002 | No skills found | Query SkillCenter |
| DISC_003 | Registration failed | Check credentials and retry |
| DISC_004 | Verification failed | Check certificate chain |
| DISC_005 | Network error | Use cached data if available |
| DISC_006 | Skill unavailable | Find alternative skill |
| DISC_007 | Offline mode limit | Use cached skills |

## 13. Configuration Reference

```yaml
discovery:
  udp:
    enabled: true
    multicastGroup: 224.0.0.1
    port: 54321
    broadcastInterval: 5000
    timeout: 30000
  
  dht:
    enabled: true
    bootstrapNodes:
      - host: dht1.ooder.net
        port: 6881
    replicationFactor: 3
  
  skillCenter:
    enabled: true
    url: https://skillcenter.ooder.net
    timeout: 10000
    cache:
      enabled: true
      ttl: 300000
  
  mdns:
    enabled: true
    serviceName: _ooder-skill._tcp
  
  github:
    enabled: true
    defaultOwner: ooderCN
    defaultRepo: skills
    defaultBranch: main
    token: ${GITHUB_TOKEN}
    baseUrl: https://api.github.com
    timeout: 60000
  
  gitee:
    enabled: true
    defaultOwner: ooderCN
    defaultRepo: skills
    defaultBranch: main
    token: ${GITEE_TOKEN}
    baseUrl: https://gitee.com/api/v5
    timeout: 60000
  
  offline:
    enabled: true
    cachePath: ./data/skill-cache
    maxAge: 86400000
    maxSkills: 100
  
  healthCheck:
    heartbeatInterval: 5000
    timeout: 30000
    retryCount: 3
    unhealthyThreshold: 3
    offlineThreshold: 5
```

## 14. Version History

| Version | Date | Changes |
|---------|------|---------|
| v0.7.0 | 2026-02-11 | Initial version |
| v0.7.3 | 2026-02-20 | Added offline support, event bus, mDNS discovery, GitHub/Gitee repository discovery |
