# 技术方案：Scene-Engine-Core 功能迁移与Mock统一

## 一、项目背景

### 1.1 源项目分析

**agent-skillcenter** (版本 2.1)
- Spring Boot 2.7.0 + Java 8
- 依赖: scene-engine-core 0.7.3, ooder-common 2.2
- 包含30+个Mock实现类
- 约5000行Mock代码

### 1.2 目标项目

**scene-engine-core** (版本 0.7.3)
- 已实现: AuditService, PermissionService, SecurityInterceptor
- 待实现: SecurityService, NetworkService, HostingService, Protocol Adapters

---

## 二、技术架构

### 2.1 模块结构

```
scene-engine-core/
├── src/main/java/net/ooder/scene/
│   ├── core/
│   │   ├── security/          # 已实现
│   │   │   ├── AuditService
│   │   │   ├── PermissionService
│   │   │   ├── SecurityInterceptor
│   │   │   └── SecureSkillService
│   │   └── skill/             # 已实现
│   │       ├── storage/
│   │       ├── network/
│   │       ├── llm/
│   │       ├── scheduler/
│   │       └── security/
│   ├── service/               # 新增
│   │   ├── SecurityService
│   │   ├── NetworkService
│   │   └── HostingService
│   └── protocol/              # 新增
│       ├── LoginProtocolAdapter
│       ├── DiscoveryProtocolAdapter
│       ├── DomainManagementProtocolAdapter
│       ├── CollaborationProtocolAdapter
│       └── ObservationProtocolAdapter
```

### 2.2 依赖关系

```
agent-skillcenter
    └── scene-engine-core (0.7.3)
            ├── agent-sdk (0.7.3)
            ├── ooder-common-client (2.2)
            ├── ooder-server (2.2)
            └── ooder-vfs-web (2.2)
```

---

## 三、API设计

### 3.1 SecurityService

```java
package net.ooder.scene.service;

public interface SecurityService {
    SecurityStatus getStatus();
    SecurityStats getStats();
    List<SecurityPolicy> listPolicies();
    SecurityPolicy getPolicy(String policyId);
    SecurityPolicy createPolicy(SecurityPolicy policy);
    boolean enablePolicy(String policyId);
    boolean disablePolicy(String policyId);
    boolean deletePolicy(String policyId);
    PageResult<AccessControl> listAcls(int page, int size);
    AccessControl createAcl(AccessControl acl);
    boolean deleteAcl(String aclId);
    PageResult<ThreatInfo> listThreats(int page, int size);
    boolean resolveThreat(String threatId);
    boolean runSecurityScan();
    boolean toggleFirewall();
}
```

### 3.2 NetworkService

```java
package net.ooder.scene.service;

public interface NetworkService {
    NetworkStatus getStatus();
    NetworkStats getStats();
    PageResult<NetworkLink> listLinks(int page, int size);
    NetworkLink getLink(String linkId);
    boolean disconnectLink(String linkId);
    boolean reconnectLink(String linkId);
    PageResult<NetworkRoute> listRoutes(int page, int size);
    NetworkRoute getRoute(String routeId);
    NetworkRoute findRoute(String source, String target, String algorithm, int maxHops);
    NetworkTopology getTopology();
    NetworkQuality getQuality();
}
```

### 3.3 HostingService

```java
package net.ooder.scene.service;

public interface HostingService {
    List<HostingInstance> getAllInstances();
    PageResult<HostingInstance> getInstances(int page, int size);
    HostingInstance getInstance(String instanceId);
    HostingInstance createInstance(HostingInstance instance);
    boolean deleteInstance(String instanceId);
    boolean startInstance(String instanceId);
    boolean stopInstance(String instanceId);
    boolean scaleInstance(String instanceId, int replicas);
    InstanceHealth getHealth(String instanceId);
}
```

### 3.4 Protocol Adapters

```java
package net.ooder.scene.protocol;

public interface LoginProtocolAdapter {
    CompletableFuture<LoginResult> login(LoginRequest request);
    CompletableFuture<Void> logout(String sessionId);
    CompletableFuture<Session> getSession(String sessionId);
    CompletableFuture<Boolean> validateSession(String sessionId);
}

public interface DiscoveryProtocolAdapter {
    CompletableFuture<DiscoveryResult> discoverPeers(DiscoveryRequest request);
    CompletableFuture<List<Peer>> listDiscoveredPeers();
    CompletableFuture<Peer> discoverMcp();
}

public interface DomainManagementProtocolAdapter {
    CompletableFuture<List<Domain>> listDomains();
    CompletableFuture<Domain> getDomain(String domainId);
    CompletableFuture<Domain> createDomain(Domain domain);
}

public interface CollaborationProtocolAdapter {
    CompletableFuture<Void> joinChannel(String channelId);
    CompletableFuture<Void> leaveChannel(String channelId);
    CompletableFuture<Void> sendMessage(String channelId, Message message);
}

public interface ObservationProtocolAdapter {
    CompletableFuture<ObservationData> observe(String targetId);
    CompletableFuture<List<ObservationData>> getHistory(String targetId, long start, long end);
}
```

---

## 四、数据模型

### 4.1 Security DTOs

```java
public class SecurityStatus {
    private String status;
    private String securityLevel;
    private int activePolicies;
    private int totalPolicies;
    private int recentAlerts;
    private int blockedAttempts;
    private double threatScore;
    private boolean firewallEnabled;
    private boolean encryptionEnabled;
    private boolean auditEnabled;
    private long lastScanTime;
}

public class SecurityPolicy {
    private String policyId;
    private String policyName;
    private String policyType;
    private String description;
    private String status;
    private int priority;
    private String action;
    private long createdAt;
    private long updatedAt;
}

public class AccessControl {
    private String aclId;
    private String resourceType;
    private String resourceId;
    private String principalType;
    private String principalId;
    private String permission;
    private String status;
    private long grantedAt;
    private String grantedBy;
}

public class ThreatInfo {
    private String threatId;
    private String threatType;
    private String severity;
    private String source;
    private String description;
    private String status;
    private String recommendation;
    private long detectedAt;
    private long resolvedAt;
}
```

### 4.2 Network DTOs

```java
public class NetworkStatus {
    private String status;
    private String nodeId;
    private String nodeType;
    private boolean online;
    private int connectedPeers;
    private String localAddress;
    private int localPort;
    private long uptime;
}

public class NetworkLink {
    private String linkId;
    private String sourceNode;
    private String targetNode;
    private String linkType;
    private String status;
    private int latency;
    private int bandwidth;
    private long establishedAt;
    private long lastActive;
}

public class NetworkRoute {
    private String routeId;
    private String sourceNode;
    private String targetNode;
    private List<String> hops;
    private int totalLatency;
    private int hopCount;
    private String status;
    private String routeType;
    private long createdAt;
}
```

---

## 五、实施步骤

### Phase 1: 核心API (第1周)

**Day 1-3: SecurityService**
1. 创建 `net.ooder.scene.service` 包
2. 定义 SecurityService 接口
3. 实现 SecurityServiceImpl
4. 创建 SecurityStatus, SecurityPolicy, AccessControl, ThreatInfo DTO
5. 编写单元测试

**Day 4-5: NetworkService**
1. 定义 NetworkService 接口
2. 实现 NetworkServiceImpl
3. 创建 NetworkStatus, NetworkLink, NetworkRoute DTO
4. 编写单元测试

**Day 6-7: Protocol Adapters**
1. 实现 LoginProtocolAdapter
2. 实现 DiscoveryProtocolAdapter
3. 编写单元测试

### Phase 2: 托管服务 (第2周)

**Day 1-2: HostingService**
1. 定义 HostingService 接口
2. 实现 HostingServiceImpl
3. 创建 HostingInstance, InstanceHealth DTO

**Day 3-5: Cloud Providers**
1. 实现 KubernetesProvider
2. 实现 AliyunProvider
3. 实现 TencentProvider
4. 编写单元测试

### Phase 3: 协议适配器 (第3周)

**Day 1-2: Domain & Collaboration**
1. 实现 DomainManagementProtocolAdapter
2. 实现 CollaborationProtocolAdapter

**Day 3: Observation**
1. 实现 ObservationProtocolAdapter
2. 编写单元测试

### Phase 4: 迁移验证 (第4周)

**Day 1-2: Mock移除**
1. agent-skillcenter 移除 Mock 实现
2. 更新依赖到 scene-engine-core API

**Day 3-4: 集成测试**
1. 编写集成测试
2. 验证功能正确性

**Day 5: 性能测试**
1. 性能基准测试
2. 优化瓶颈

---

## 六、风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| API不兼容 | 高 | 保持向后兼容，提供适配层 |
| 性能下降 | 中 | 性能测试，优化关键路径 |
| 测试覆盖不足 | 中 | 编写完整单元测试和集成测试 |

---

## 七、验收标准

1. ✅ 所有Mock实现被真实API替换
2. ✅ 单元测试覆盖率 > 80%
3. ✅ 集成测试全部通过
4. ✅ 性能不低于原Mock实现
5. ✅ agent-skillcenter正常编译运行

---

## 八、文档索引

- [COLLABORATION_TASK_SUMMARY.md](./COLLABORATION_TASK_SUMMARY.md) - 任务汇总
- [COLLABORATION_TASK_COMMON_FEATURES.md](./COLLABORATION_TASK_COMMON_FEATURES.md) - 通用功能迁移
- [COLLABORATION_TASK_MOCK_MIGRATION.md](./COLLABORATION_TASK_MOCK_MIGRATION.md) - Mock迁移详情
