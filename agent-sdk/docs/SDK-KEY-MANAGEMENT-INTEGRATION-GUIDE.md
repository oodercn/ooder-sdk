# SDK 密钥管理集成开发说明

**版本**: v2.3.1  
**日期**: 2026-03-21  
**状态**: SDK 已完成实现

---

## 一、概述

### 1.1 文档目的

本文档面向 SE 团队，说明如何集成 SDK v2.3.1 中已实现的密钥管理与 Agent 组网功能。

### 1.2 SDK 已完成内容

SDK 团队已完成以下功能的实现：

| 模块 | 状态 | 说明 |
|------|------|------|
| 密钥实体模型 | ✅ 已完成 | `KeyEntity` 统一模型 |
| 入网请求模型 | ✅ 已完成 | `NetworkJoinRequest` |
| 密钥管理服务 | ✅ 已完成 | `KeyManagementService` |
| 入网审批服务 | ✅ 已完成 | `NetworkJoinService` |
| 密钥规则服务 | ✅ 已完成 | `KeyRuleService` |
| 审计日志服务 | ✅ 已完成 | `KeyUsageLogService` |
| NexusService 集成 | ✅ 已完成 | 组网接口与密钥结合 |

### 1.3 设计原则

1. **入网审批默认不需要** - 自动签发密钥
2. **勾选需要后启用手工审批** - 需要管理员批准
3. **安全 KEY 与组网接口结合** - 通过 NexusService 统一调用

### 1.4 v3.0.1 重命名变更

| 原名称 | 新名称 | 包路径 | 说明 |
|--------|--------|--------|------|
| `TokenManager` | `AuthTokenManager` | `net.ooder.sdk.service.security.auth` | 认证令牌管理器 |
| `SceneGroupManager` | `CollaborativeSceneGroupManager` | `net.ooder.skills.api` | 协作场景组管理器 |

**删除的重复类：**
- `api.security.impl.SecurityServiceImpl` → 保留 `service.security.SecurityServiceImpl`
- `api.network.impl.NetworkServiceImpl` → 保留 `service.network.NetworkServiceImpl`
- `service.llm.LlmService` (类) → 保留 `llm.service.LlmService` (接口)

---

## 二、包结构

SDK 已实现的包结构：

```
net.ooder.sdk.api.security
├── KeyType.java                 # 密钥类型枚举
├── KeyStatus.java               # 密钥状态枚举
├── OwnerType.java               # 所有者类型枚举
├── RequestType.java             # 入网请求类型枚举
├── RequestStatus.java           # 请求状态枚举
├── KeyEntity.java               # 密钥实体
├── NetworkJoinRequest.java      # 入网请求实体
├── KeyRule.java                 # 密钥规则实体
├── KeyUsageLog.java             # 密钥使用日志实体
├── KeyManagementService.java    # 密钥管理服务接口
├── NetworkJoinService.java      # 入网审批服务接口
├── KeyRuleService.java          # 密钥规则服务接口
├── KeyUsageLogService.java      # 审计日志服务接口
└── impl/
    ├── KeyManagementServiceImpl.java
    ├── NetworkJoinServiceImpl.java
    ├── KeyRuleServiceImpl.java
    └── KeyUsageLogServiceImpl.java
```

---

## 三、核心接口使用指南

### 3.1 密钥管理服务

#### 3.1.1 生成密钥

```java
import net.ooder.sdk.api.security.*;
import net.ooder.sdk.api.security.KeyManagementService.KeyGenerateRequest;

KeyManagementService keyService = new KeyManagementServiceImpl();

KeyGenerateRequest request = new KeyGenerateRequest();
request.setOwnerId("user-001");
request.setOwnerType(OwnerType.USER);
request.setKeyType(KeyType.SESSION_TOKEN);
request.setKeyName("用户会话密钥");
request.setExpiresInSeconds(86400);
request.setMaxUseCount(1000);
request.setSceneGroupId("scene-001");

KeyEntity key = keyService.generateKey(request);
System.out.println("密钥ID: " + key.getKeyId());
System.out.println("密钥值: " + key.getKeyValue());
```

#### 3.1.2 验证密钥

```java
KeyManagementService.KeyValidationResult result = 
    keyService.validateKeyByValue("your-key-value", "scene-001");

if (result.isValid()) {
    KeyEntity key = result.getKeyEntity();
    System.out.println("密钥有效，持有者: " + key.getOwnerId());
} else {
    System.out.println("密钥无效: " + result.getErrorMessage());
}
```

#### 3.1.3 密钥生命周期管理

```java
// 撤销密钥
keyService.revokeKey("key-001");

// 暂停密钥
keyService.suspendKey("key-001");

// 激活密钥
keyService.activateKey("key-001");

// 刷新密钥（重新生成密钥值）
KeyEntity newKey = keyService.refreshKey("key-001");
```

### 3.2 入网审批服务

#### 3.2.1 自动审批模式（默认）

```java
KeyManagementService keyService = new KeyManagementServiceImpl();
NetworkJoinService joinService = new NetworkJoinServiceImpl(keyService);

// 默认不需要审批
joinService.setApprovalRequired("scene-001", false);

NetworkJoinRequest request = NetworkJoinRequest.forAgent(
    "agent-001", "Agent One", "scene-001"
);

NetworkJoinRequest result = joinService.createRequest(request);

if (result.isApproved()) {
    KeyEntity key = result.getIssuedKey();
    System.out.println("自动审批通过，密钥: " + key.getKeyValue());
}
```

#### 3.2.2 手工审批模式

```java
// 设置需要审批
joinService.setApprovalRequired("scene-001", true);

NetworkJoinRequest request = NetworkJoinRequest.forUser(
    "user-001", "张三", "scene-001"
);

NetworkJoinRequest result = joinService.createRequest(request);

if (result.isPending()) {
    System.out.println("请求已提交，等待审批: " + result.getRequestId());
}

// 审批通过
KeyRule rule = new KeyRule();
rule.setDefaultExpiresInSeconds(86400);
rule.setDefaultMaxUseCount(1000);

NetworkJoinRequest approved = joinService.approve(
    "req-001", "admin-001", "审批通过", rule
);

// 获取签发的密钥
KeyEntity key = approved.getIssuedKey();

// 或拒绝申请
joinService.reject("req-001", "admin-001", "信息不完整");
```

### 3.3 NexusService 集成

#### 3.3.1 使用密钥加入场景组

```java
import net.ooder.sdk.nexus.*;
import net.ooder.sdk.nexus.impl.NexusServiceImpl;

NexusService nexusService = new NexusServiceImpl(provider);

// 方式1: 直接加入（无需密钥）
nexusService.joinSceneGroup("scene-001").join();

// 方式2: 使用密钥加入
nexusService.joinSceneGroupWithKey("scene-001", "your-key-value").join();

// 方式3: 请求加入（支持审批流程）
NetworkJoinRequest request = NetworkJoinRequest.forAgent(
    "agent-001", "Agent One", "scene-001"
);
NetworkJoinRequest result = nexusService.requestJoinSceneGroup(request).join();
```

#### 3.3.2 管理场景组审批设置

```java
// 设置场景组需要审批
nexusService.setSceneGroupApprovalRequired("scene-001", true).join();

// 验证访问权限
boolean hasAccess = nexusService.validateSceneGroupAccess(
    "scene-001", 
    "your-key-value"
).join();

// 获取场景组访问密钥
KeyEntity key = nexusService.getSceneGroupAccessKey("scene-001").join();
```

---

## 四、SE 集成任务

### 4.1 任务清单

| 任务 | 说明 | 优先级 |
|------|------|--------|
| 数据库持久化 | 实现 Repository 接口，替换内存存储 | P0 |
| Spring Boot 集成 | 创建 AutoConfiguration | P0 |
| 缓存层 | 添加 Redis 缓存支持 | P1 |
| API 接口 | 暴露 REST API | P1 |
| 前端页面 | 审批管理界面 | P2 |

### 4.2 持久化实现示例

#### 4.2.1 创建 Repository 接口

```java
package net.ooder.scene.security.repository;

import net.ooder.sdk.api.security.KeyEntity;
import net.ooder.sdk.api.security.KeyStatus;
import net.ooder.sdk.api.security.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeyEntityRepository extends JpaRepository<KeyEntity, String> {
    
    Optional<KeyEntity> findByKeyValue(String keyValue);
    
    List<KeyEntity> findByOwnerIdAndOwnerType(String ownerId, OwnerType ownerType);
    
    List<KeyEntity> findBySceneGroupId(String sceneGroupId);
    
    List<KeyEntity> findByStatus(KeyStatus status);
}
```

#### 4.2.2 创建持久化服务实现

```java
package net.ooder.scene.security.service;

import net.ooder.sdk.api.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersistentKeyManagementService implements KeyManagementService {
    
    @Autowired
    private KeyEntityRepository keyRepository;
    
    @Override
    @Transactional
    public KeyEntity generateKey(KeyGenerateRequest request) {
        KeyEntity key = new KeyEntity();
        // ... 设置属性
        keyRepository.save(key);
        return key;
    }
    
    @Override
    @Cacheable(value = "keys", key = "#keyId")
    public KeyEntity getKey(String keyId) {
        return keyRepository.findById(keyId).orElse(null);
    }
    
    @Override
    @Cacheable(value = "keysByValue", key = "#keyValue")
    public KeyEntity getKeyByValue(String keyValue) {
        return keyRepository.findByKeyValue(keyValue).orElse(null);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "keys", key = "#keyId")
    public boolean revokeKey(String keyId) {
        KeyEntity key = keyRepository.findById(keyId).orElse(null);
        if (key != null) {
            key.setStatus(KeyStatus.REVOKED);
            keyRepository.save(key);
            return true;
        }
        return false;
    }
}
```

### 4.3 Spring Boot 自动配置

```java
package net.ooder.scene.security.config;

import net.ooder.sdk.api.security.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyManagementAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public KeyManagementService keyManagementService() {
        return new PersistentKeyManagementService();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public NetworkJoinService networkJoinService(KeyManagementService keyManagementService) {
        return new PersistentNetworkJoinService(keyManagementService);
    }
}
```

---

## 五、数据库表结构

### 5.1 密钥实体表

```sql
CREATE TABLE key_entity (
    key_id VARCHAR(64) PRIMARY KEY,
    key_value VARCHAR(256) NOT NULL UNIQUE,
    key_name VARCHAR(128),
    key_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    
    issuer_id VARCHAR(64),
    issued_at BIGINT NOT NULL,
    
    owner_id VARCHAR(64) NOT NULL,
    owner_type VARCHAR(32) NOT NULL,
    
    expires_at BIGINT,
    max_use_count INT DEFAULT 0,
    used_count INT DEFAULT 0,
    
    scene_group_id VARCHAR(64),
    agent_id VARCHAR(64),
    device_id VARCHAR(64),
    
    last_used_at BIGINT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    
    approval_required BOOLEAN DEFAULT FALSE,
    approval_status VARCHAR(32),
    approved_by VARCHAR(64),
    approved_at BIGINT,
    
    allowed_scenes TEXT,
    allowed_operations TEXT,
    metadata TEXT,
    
    INDEX idx_key_value (key_value),
    INDEX idx_owner (owner_id, owner_type),
    INDEX idx_scene (scene_group_id),
    INDEX idx_status (status)
);
```

### 5.2 入网请求表

```sql
CREATE TABLE network_join_request (
    request_id VARCHAR(64) PRIMARY KEY,
    request_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    
    applicant_id VARCHAR(64) NOT NULL,
    applicant_name VARCHAR(128),
    applicant_type VARCHAR(32) NOT NULL,
    
    scene_group_id VARCHAR(64) NOT NULL,
    invite_code VARCHAR(128),
    capabilities TEXT,
    
    reviewer_id VARCHAR(64),
    review_comment TEXT,
    reviewed_at BIGINT,
    
    issued_key_id VARCHAR(64),
    
    manual_approval_required BOOLEAN DEFAULT FALSE,
    
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    
    INDEX idx_applicant (applicant_id),
    INDEX idx_scene (scene_group_id),
    INDEX idx_status (status)
);
```

### 5.3 密钥使用日志表

```sql
CREATE TABLE key_usage_log (
    log_id VARCHAR(64) PRIMARY KEY,
    key_id VARCHAR(64) NOT NULL,
    key_value VARCHAR(256),
    
    operation VARCHAR(64),
    resource VARCHAR(256),
    action VARCHAR(64),
    
    operator_id VARCHAR(64),
    operator_type VARCHAR(32),
    
    scene_group_id VARCHAR(64),
    agent_id VARCHAR(64),
    
    success BOOLEAN NOT NULL,
    error_code VARCHAR(32),
    error_message TEXT,
    
    client_ip VARCHAR(64),
    user_agent VARCHAR(256),
    
    timestamp BIGINT NOT NULL,
    duration BIGINT,
    
    context TEXT,
    
    INDEX idx_key (key_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_operator (operator_id)
);
```

---

## 六、API 接口设计

### 6.1 REST API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/keys` | POST | 生成密钥 |
| `/api/keys/{keyId}` | GET | 获取密钥 |
| `/api/keys/{keyId}/validate` | POST | 验证密钥 |
| `/api/keys/{keyId}/revoke` | POST | 撤销密钥 |
| `/api/join-requests` | POST | 创建入网请求 |
| `/api/join-requests` | GET | 获取请求列表 |
| `/api/join-requests/{requestId}/approve` | POST | 审批通过 |
| `/api/join-requests/{requestId}/reject` | POST | 拒绝申请 |

### 6.2 API 示例

#### 创建入网请求

```http
POST /api/join-requests
Content-Type: application/json

{
    "applicantId": "agent-001",
    "applicantName": "Agent One",
    "applicantType": "AGENT",
    "sceneGroupId": "scene-001"
}
```

#### 审批通过

```http
POST /api/join-requests/req-001/approve
Content-Type: application/json

{
    "reviewerId": "admin-001",
    "comment": "审批通过",
    "rule": {
        "defaultExpiresInSeconds": 86400,
        "defaultMaxUseCount": 1000
    }
}
```

---

## 七、依赖配置

### 7.1 Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 7.2 Spring Boot 配置

```yaml
ooder:
  security:
    key-management:
      enabled: true
      default-expires-in-seconds: 86400
      default-max-use-count: 1000
      cache:
        enabled: true
        ttl: 3600
```

---

## 八、最佳实践

### 8.1 密钥安全

1. **密钥值加密存储** - 使用 AES 加密
2. **传输使用 HTTPS** - 禁止明文传输
3. **定期轮换** - 设置合理的过期时间
4. **最小权限原则** - 只授予必要的场景访问权限

### 8.2 性能优化

1. **缓存热点密钥** - 使用 Redis 缓存
2. **批量查询** - 减少数据库访问
3. **异步日志** - 使用消息队列记录审计日志

### 8.3 监控告警

1. **密钥过期告警** - 提前通知
2. **异常使用告警** - 频繁验证失败
3. **审批超时告警** - 长时间未处理

---

## 九、常见问题

### Q1: 如何判断入网是否需要审批？

```java
boolean needsApproval = joinService.isApprovalRequired("scene-001");
```

### Q2: 密钥过期后如何续期？

```java
KeyEntity newKey = keyService.refreshKey("key-001");
```

### Q3: 如何查看密钥使用历史？

```java
List<KeyUsageLog> logs = keyService.getUsageLogs("key-001", 100);
```

---

## 十、版本历史

| 版本 | 日期 | 变更说明 |
|------|------|---------|
| v2.3.1 | 2026-03-20 | SDK 完成密钥管理实现 |

---

**文档维护**: SDK 团队  
**联系方式**: sdk-team@ooder.net
