# SDK密钥管理集成开发说明

**版本**: v2.3.1  
**日期**: 2026-03-20  
**状态**: SDK已完成实现  

---

## 一、概述

### 1.1 文档目的

本文档面向 SE 团队，说明如何集成 SDK v2.3.1 中已实现的密钥管理与 Agent 组网功能。

### 1.2 SDK已完成内容

| 模块 | 状态 | 包路径 | 说明 |
|------|------|--------|------|
| 密钥实体模型 | ✅ 已完成 | `net.ooder.sdk.api.security` | `KeyEntity` 统一模型 |
| 入网请求模型 | ✅ 已完成 | `net.ooder.sdk.api.security` | `NetworkJoinRequest` |
| 密钥管理服务 | ✅ 已完成 | `net.ooder.sdk.api.security` | `KeyManagementService` |
| 入网审批服务 | ✅ 已完成 | `net.ooder.sdk.api.security` | `NetworkJoinService` |
| 密钥规则服务 | ✅ 已完成 | `net.ooder.sdk.api.security` | `KeyRuleService` |
| 审计日志服务 | ✅ 已完成 | `net.ooder.sdk.api.security` | `KeyUsageLogService` |
| NexusService 集成 | ✅ 已完成 | `net.ooder.sdk.nexus` | 组网接口与密钥结合 |

### 1.3 设计原则

1. **入网审批默认不需要** - 自动签发密钥
2. **勾选需要后启用手工审批** - 需要管理员批准
3. **安全 KEY 与组网接口结合** - 通过 NexusService 统一调用
4. **持久化优先使用JSON方案** - 参考现有 `JsonStorageService` 实现

### 1.4 SE团队职责

| 职责 | SE负责 | 优先级 | 说明 |
|------|--------|--------|------|
| JSON持久化集成 | ✅ | P0 | 集成SDK的JSON存储方案 |
| Spring Boot 集成 | ✅ | P0 | 创建 AutoConfiguration |
| API 接口 | ✅ | P1 | 暴露 REST API |
| 前端页面 | ✅ | P2 | 审批管理界面 |
| 单元测试 | ✅ | P1 | 集成测试 |

---

## 二、SDK包结构

### 2.1 核心包结构

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

### 2.2 NexusService包结构

```
net.ooder.sdk.nexus
├── NexusService.java            # 组网服务接口
├── NexusServiceFactory.java     # 组网服务工厂
└── model/
    ├── NexusConfig.java         # 组网配置
    ├── NexusState.java          # 组网状态
    ├── NexusStatus.java         # 状态枚举
    └── UserSession.java         # 用户会话
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

## 四、SE集成任务（简化版）

### 4.1 任务清单

| 任务 | 说明 | 优先级 | 预计时间 |
|------|------|--------|---------|
| JSON持久化集成 | 集成SDK的JSON存储方案 | P0 | 1天 |
| Spring Boot 集成 | 创建 AutoConfiguration | P0 | 0.5天 |
| API 接口 | 暴露 REST API | P1 | 1天 |
| 前端页面 | 审批管理界面 | P2 | 2天 |
| 单元测试 | 集成测试 | P1 | 1天 |

**总计**: 5.5人天

---

### 4.2 JSON持久化集成 (P0)

#### 任务4.2.1: 创建JSON存储实现
**负责人**: SE团队  
**预计时间**: 1天  
**优先级**: P0  

**任务内容**:
1. 参考 `JsonStorageService` 实现密钥存储
2. 创建 `JsonKeyStorageService` 类
3. 集成到SDK的 `KeyManagementServiceImpl`

**技术方案**:

```java
package net.ooder.scene.security.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.sdk.api.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * JSON文件存储实现 - 密钥管理
 * 
 * <p>参考现有的JsonStorageService实现</p>
 */
@Service
public class JsonKeyStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonKeyStorageService.class);
    
    private final ObjectMapper objectMapper;
    private final Map<String, ReentrantReadWriteLock> locks;
    
    @Value("${scene.engine.key.storage.root:data/keys}")
    private String storageRoot;
    
    public JsonKeyStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.locks = new ConcurrentHashMap<>();
    }
    
    @PostConstruct
    public void init() {
        initStorageDirectories();
        logger.info("JsonKeyStorageService initialized with root: {}", storageRoot);
    }
    
    private void initStorageDirectories() {
        try {
            Files.createDirectories(Paths.get(storageRoot, "keys"));
            Files.createDirectories(Paths.get(storageRoot, "requests"));
            Files.createDirectories(Paths.get(storageRoot, "rules"));
            Files.createDirectories(Paths.get(storageRoot, "logs"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage directories", e);
        }
    }
    
    // ========== 密钥存储 ==========
    
    public void saveKey(KeyEntity key) {
        String lockKey = "key:" + key.getKeyId();
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "keys", key.getKeyId() + ".json");
            writeJsonFile(filePath, key);
            logger.debug("Saved key: {}", key.getKeyId());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public KeyEntity loadKey(String keyId) {
        String lockKey = "key:" + keyId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.readLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "keys", keyId + ".json");
            return readJsonFile(filePath, KeyEntity.class);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public KeyEntity loadKeyByValue(String keyValue) {
        // 简化实现：遍历所有密钥文件查找
        Path keysDir = Paths.get(storageRoot, "keys");
        if (!Files.exists(keysDir)) {
            return null;
        }
        
        try {
            return Files.list(keysDir)
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> readJsonFile(path, KeyEntity.class))
                .filter(Objects::nonNull)
                .filter(key -> keyValue.equals(key.getKeyValue()))
                .findFirst()
                .orElse(null);
        } catch (IOException e) {
            logger.error("Failed to load key by value", e);
            return null;
        }
    }
    
    public void deleteKey(String keyId) {
        String lockKey = "key:" + keyId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "keys", keyId + ".json");
            Files.deleteIfExists(filePath);
            locks.remove(lockKey);
            logger.debug("Deleted key: {}", keyId);
        } catch (IOException e) {
            logger.error("Failed to delete key: {}", keyId, e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // ========== 入网请求存储 ==========
    
    public void saveRequest(NetworkJoinRequest request) {
        String lockKey = "request:" + request.getRequestId();
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "requests", request.getRequestId() + ".json");
            writeJsonFile(filePath, request);
            logger.debug("Saved request: {}", request.getRequestId());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public NetworkJoinRequest loadRequest(String requestId) {
        String lockKey = "request:" + requestId;
        ReentrantReadWriteLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantReadWriteLock());
        
        lock.readLock().lock();
        try {
            Path filePath = Paths.get(storageRoot, "requests", requestId + ".json");
            return readJsonFile(filePath, NetworkJoinRequest.class);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // ========== 工具方法 ==========
    
    private void writeJsonFile(Path filePath, Object data) {
        try {
            Files.createDirectories(filePath.getParent());
            objectMapper.writeValue(filePath.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }
    
    private <T> T readJsonFile(Path filePath, Class<T> clazz) {
        if (!Files.exists(filePath)) {
            return null;
        }
        
        try {
            return objectMapper.readValue(filePath.toFile(), clazz);
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            return null;
        }
    }
}
```

**验收标准**:
- [ ] JSON存储功能正常
- [ ] 并发安全
- [ ] 单元测试通过

**交付物**:
- JsonKeyStorageService实现
- 单元测试代码

---

### 4.3 Spring Boot集成 (P0)

#### 任务4.3.1: 创建AutoConfiguration
**负责人**: SE团队  
**预计时间**: 0.5天  
**优先级**: P0  

**技术方案**:

```java
package net.ooder.scene.security.config;

import net.ooder.sdk.api.security.*;
import net.ooder.scene.security.storage.JsonKeyStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeyManagementProperties.class)
public class KeyManagementAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public JsonKeyStorageService jsonKeyStorageService() {
        return new JsonKeyStorageService();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public KeyManagementService keyManagementService(JsonKeyStorageService storageService) {
        // 使用SDK的实现，注入JSON存储
        KeyManagementServiceImpl impl = new KeyManagementServiceImpl();
        impl.setStorageService(storageService);
        return impl;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public NetworkJoinService networkJoinService(KeyManagementService keyManagementService) {
        NetworkJoinServiceImpl impl = new NetworkJoinServiceImpl(keyManagementService);
        return impl;
    }
}
```

**验收标准**:
- [ ] 自动配置生效
- [ ] Bean正确装配

**交付物**:
- AutoConfiguration类
- spring.factories文件

---

### 4.4 API接口实现 (P1)

#### 任务4.4.1: 创建REST API Controller
**负责人**: SE团队  
**预计时间**: 1天  
**优先级**: P1  

**技术方案**:

```java
package net.ooder.scene.security.controller;

import net.ooder.sdk.api.security.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/keys")
@Tag(name = "密钥管理", description = "密钥管理相关接口")
public class KeyManagementController {
    
    @Autowired
    private KeyManagementService keyManagementService;
    
    @PostMapping
    @Operation(summary = "生成密钥")
    public Result<KeyEntity> generateKey(@RequestBody KeyManagementService.KeyGenerateRequest request) {
        KeyEntity key = keyManagementService.generateKey(request);
        return Result.success(key);
    }
    
    @GetMapping("/{keyId}")
    @Operation(summary = "获取密钥详情")
    public Result<KeyEntity> getKey(@PathVariable String keyId) {
        KeyEntity key = keyManagementService.getKey(keyId);
        return Result.success(key);
    }
    
    @PostMapping("/{keyId}/validate")
    @Operation(summary = "验证密钥")
    public Result<KeyManagementService.KeyValidationResult> validateKey(
            @PathVariable String keyId,
            @RequestParam String scope) {
        KeyManagementService.KeyValidationResult result = keyManagementService.validateKey(keyId, scope);
        return Result.success(result);
    }
    
    @PostMapping("/{keyId}/revoke")
    @Operation(summary = "撤销密钥")
    public Result<Boolean> revokeKey(@PathVariable String keyId) {
        boolean success = keyManagementService.revokeKey(keyId);
        return Result.success(success);
    }
}

@RestController
@RequestMapping("/api/v1/join-requests")
@Tag(name = "入网审批", description = "入网审批相关接口")
public class NetworkJoinController {
    
    @Autowired
    private NetworkJoinService networkJoinService;
    
    @PostMapping
    @Operation(summary = "创建入网请求")
    public Result<NetworkJoinRequest> createRequest(@RequestBody NetworkJoinRequest request) {
        NetworkJoinRequest result = networkJoinService.createRequest(request);
        return Result.success(result);
    }
    
    @GetMapping
    @Operation(summary = "获取请求列表")
    public Result<List<NetworkJoinRequest>> getPendingRequests() {
        List<NetworkJoinRequest> requests = networkJoinService.getPendingRequests();
        return Result.success(requests);
    }
    
    @PostMapping("/{requestId}/approve")
    @Operation(summary = "审批通过")
    public Result<NetworkJoinRequest> approve(
            @PathVariable String requestId,
            @RequestBody ApproveRequest approveRequest) {
        NetworkJoinRequest result = networkJoinService.approve(
            requestId, 
            approveRequest.getReviewerId(),
            approveRequest.getComment(),
            approveRequest.getRule()
        );
        return Result.success(result);
    }
}
```

**验收标准**:
- [ ] 所有API接口实现
- [ ] API文档完整
- [ ] 接口测试通过

**交付物**:
- Controller实现
- API文档

---

### 4.5 前端页面实现 (P2)

#### 任务4.5.1: 密钥管理页面
**负责人**: SE团队  
**预计时间**: 1天  
**优先级**: P2  

**任务内容**:
1. 创建密钥列表页面
2. 创建密钥详情弹窗
3. 实现密钥操作按钮

**验收标准**:
- [ ] 页面功能完整
- [ ] 交互流畅

**交付物**:
- 前端页面代码

---

#### 任务4.5.2: 入网审批页面
**负责人**: SE团队  
**预计时间**: 1天  
**优先级**: P2  

**任务内容**:
1. 创建审批列表页面
2. 创建审批详情弹窗
3. 实现审批操作

**验收标准**:
- [ ] 审批流程完整
- [ ] 操作便捷

**交付物**:
- 审批页面代码

---

## 五、配置说明

### 5.1 Maven依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 5.2 Spring Boot配置

```yaml
scene:
  engine:
    key:
      storage:
        root: data/keys  # JSON存储根目录
        
ooder:
  security:
    key-management:
      enabled: true
      default-expires-in-seconds: 86400
      default-max-use-count: 1000
```

---

## 六、最佳实践

### 6.1 密钥安全

1. **密钥值加密存储** - 使用 AES 加密
2. **传输使用 HTTPS** - 禁止明文传输
3. **定期轮换** - 设置合理的过期时间
4. **最小权限原则** - 只授予必要的场景访问权限

### 6.2 性能优化

1. **缓存热点密钥** - 可选添加Redis缓存
2. **批量查询** - 减少文件访问
3. **异步日志** - 使用消息队列记录审计日志

### 6.3 监控告警

1. **密钥过期告警** - 提前通知
2. **异常使用告警** - 频繁验证失败
3. **审批超时告警** - 长时间未处理

---

## 七、开发计划

### 7.1 时间表

| 阶段 | 任务 | 预计时间 | 开始日期 | 结束日期 |
|------|------|---------|---------|---------|
| Phase 1 | JSON持久化集成 | 1天 | Day 1 | Day 1 |
| Phase 2 | Spring Boot集成 | 0.5天 | Day 2 | Day 2 |
| Phase 3 | API接口实现 | 1天 | Day 3 | Day 3 |
| Phase 4 | 前端页面实现 | 2天 | Day 4 | Day 5 |
| Phase 5 | 测试与文档 | 1天 | Day 6 | Day 6 |

**总计**: 5.5人天

### 7.2 里程碑

- **M1 (Day 1)**: JSON持久化完成
- **M2 (Day 2)**: Spring Boot集成完成
- **M3 (Day 3)**: API接口完成
- **M4 (Day 5)**: 前端页面完成
- **M5 (Day 6)**: 测试文档完成

---

## 八、交付标准

### 8.1 功能完整性

- [ ] JSON持久化功能正常
- [ ] Spring Boot自动配置生效
- [ ] REST API接口完整
- [ ] 前端页面功能完整

### 8.2 质量标准

- [ ] 单元测试覆盖率>70%
- [ ] 所有测试通过
- [ ] 无严重Bug
- [ ] 代码符合规范

---

## 九、协作方式

### 9.1 代码管理

- **代码仓库**: `ooder-sdk/scene-engine`
- **分支策略**: Git Flow
- **开发分支**: `feature/key-management-integration`
- **提交规范**: Conventional Commits

### 9.2 沟通渠道

- **日常沟通**: 企业微信群
- **问题跟踪**: GitHub Issues
- **文档协作**: 语雀文档
- **定期会议**: 每日站会 (15分钟)

### 9.3 进度汇报

- **日报**: 每日下班前发送进度邮件
- **周报**: 每周五发送周报
- **里程碑汇报**: 每个里程碑完成时汇报

---

## 十、风险与应对

### 10.1 技术风险

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|---------|
| SDK接口变更 | 高 | 低 | 保持接口兼容性，版本管理 |
| JSON性能问题 | 中 | 低 | 可选添加Redis缓存 |
| 并发安全问题 | 中 | 低 | 使用读写锁保证安全 |

### 10.2 进度风险

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|---------|
| 需求变更 | 高 | 中 | 预留缓冲时间，快速迭代 |
| 技术难点 | 中 | 低 | 提前技术预研，寻求支持 |
| 人员变动 | 高 | 低 | 文档完善，知识共享 |

---

## 十一、联系方式

- **SE团队负责人**: [待定]
- **SDK团队对接人**: [待定]
- **项目经理**: [待定]

---

## 附录

### A. 参考文档

- [SDK密钥管理集成指南](E:\github\ooder-sdk\agent-sdk\docs\SDK-KEY-MANAGEMENT-INTEGRATION-GUIDE.md)
- [JsonStorageService实现](file:///e:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/skill/engine/context/impl/JsonStorageService.java)
- [FileConversationStorageService实现](file:///e:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/skill/conversation/storage/impl/FileConversationStorageService.java)

### B. 相关代码

- [TokenManager](file:///e:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/session/TokenManager.java)
- [SessionManager](file:///e:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/session/SessionManager.java)
- [PermissionService](file:///e:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/core/security/PermissionService.java)

---

**文档版本**: v2.3.1  
**创建日期**: 2026-03-20  
**最后更新**: 2026-03-20
