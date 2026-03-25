# Agent 协议文档 - v2.3

> **版本**: 2.3  
> **发布日期**: 2026-02-23  
> **状态**: 稳定版

## 1. 协议概述

Agent 协议是 Ooder 平台中 MCP Agent、Route Agent 和 End Agent 之间的通信协议，属于南向协议的具体实现。v2.3 版本统一了能力发现抽象层，简化了架构，提升了可维护性。

### 1.1 协议目标

- 实现 Agent 之间的高效、安全通信
- 支持广域网环境下的 Agent 协同工作
- 提供标准化的 Agent 接口和消息格式
- 确保 Agent 身份的真实性和通信的安全性
- 支持 Agent 动态发现和网络自组织
- 提供灵活的扩展机制，适应不同的应用场景
- **v2.3 新增**: 统一能力发现服务抽象
- **v2.3 新增**: 移除冗余模块，简化架构
- **v2.3 新增**: 标准化代码注释和文档

### 1.2 v2.3 核心变更

| 变更类型 | 说明 | 影响 |
|---------|------|------|
| 架构重构 | 移除 ooder-codegen, ooder-infra 等冗余模块 | 简化依赖 |
| 发现抽象 | 统一 CapabilityDiscoveryService 接口 | 提升可维护性 |
| 代码规范 | 全面补充 JavaDoc 注释 | 提升可读性 |
| 版本统一 | 所有模块统一使用 2.3 版本 | 消除版本混乱 |

### 1.3 协议适用范围

- 广域网内的 Agent 通信
- 跨网络、跨设备的 Agent 协同
- 企业级分布式 Agent 系统
- 个人设备间的安全 Agent 通信
- 跨组织的 Agent 网络协作
- 离线环境下的 Agent 运行

## 2. Agent 架构 (v2.3)

### 2.1 Agent 类型

SuperAgent 系统包含三种类型的 Agent：

| Agent 类型 | 描述 | 职责 |
|------------|------|------|
| MCP Agent | 主控智能体 | 资源管理、任务调度、安全认证 |
| Route Agent | 路由智能体 | 消息路由、负载均衡、网络管理 |
| End Agent | 终端智能体 | 与外部设备和系统交互、数据采集和执行 |

### 2.2 v2.3 架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                      Scene Engine v2.3                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Core       │  │  Discovery   │  │   Protocol   │          │
│  │   核心引擎    │  │   能力发现    │  │   协议实现    │          │
│  ├──────────────┤  ├──────────────┤  ├──────────────┤          │
│  │ - CapRouter  │  │ - Discovery  │  │ - UDP        │          │
│  │ - SceneEngine│  │   Service    │  │ - mDNS       │          │
│  │ - SkillHolder│  │ - Providers  │  │ - SkillCenter│          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │    Event     │  │   Session    │  │    Skill     │          │
│  │   事件系统    │  │   会话管理    │  │   技能管理    │          │
│  ├──────────────┤  ├──────────────┤  ├──────────────┤          │
│  │ - SceneEvent │  │ - Session    │  │ - Skill      │          │
│  │ - Publisher  │  │   Manager    │  │   Service    │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Northbound Core                              │
│                    北向协议核心                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.3 通信模式

Agent 之间的通信模式包括：

- **星型通信**: 所有 Agent 与 MCP Agent 直接通信
- **链式通信**: Agent 之间通过 Route Agent 进行链式通信
- **网状通信**: Agent 之间直接通信，形成网状网络
- **混合通信**: 结合多种通信模式，适应不同场景

## 3. 协议格式

### 3.1 消息格式

Agent 协议采用 JSON 格式，确保数据结构的一致性和可解析性：

```json
{
  "protocol_version": "2.3",
  "command_id": "uuid",
  "timestamp": "2026-02-23T12:00:00Z",
  "source": {
    "component": "string",
    "id": "string",
    "type": "mcp|route|end"
  },
  "destination": {
    "component": "string",
    "id": "string",
    "type": "mcp|route|end"
  },
  "operation": "string",
  "payload": {},
  "metadata": {
    "priority": "high|medium|low",
    "timeout": "number",
    "retry_count": "number",
    "security_level": "high|medium|low",
    "trace_id": "string",
    "offline_mode": false
  },
  "signature": "digital_signature",
  "token": "session_token"
}
```

## 4. 核心服务接口 (v2.3)

### 4.1 CapabilityDiscoveryService

统一的能力发现服务接口：

```java
/**
 * 能力发现服务接口
 * @since 2.3
 */
public interface CapabilityDiscoveryService {
    
    /**
     * 同步所有索引
     */
    CompletableFuture<SyncResult> syncAllIndexes();
    
    /**
     * 列出场景
     */
    CompletableFuture<List<DiscoveredItem>> listScenes(String category);
    
    /**
     * 搜索场景
     */
    CompletableFuture<List<DiscoveredItem>> searchScenes(String query);
    
    /**
     * 获取场景详情
     */
    CompletableFuture<SceneDetail> getSceneDetail(String sceneId);
    
    /**
     * 列出能力
     */
    CompletableFuture<List<DiscoveredItem>> listCapabilities(String category);
    
    /**
     * 搜索能力
     */
    CompletableFuture<List<DiscoveredItem>> searchCapabilities(String query);
    
    /**
     * 获取能力详情
     */
    CompletableFuture<CapabilityDetail> getCapabilityDetail(String capId);
    
    /**
     * 注册发现提供者
     */
    void registerProvider(DiscoveryProvider provider);
    
    /**
     * 设置发现范围
     */
    void setDiscoveryScope(DiscoveryScope scope);
}
```

### 4.2 SessionManager

会话管理接口：

```java
/**
 * Session 管理器接口
 * @since 2.3
 */
public interface SessionManager {
    
    SessionInfo createSession(String userId, String username, 
                             String clientIp, String userAgent);
    
    SessionInfo getSession(String sessionId);
    
    boolean validateSession(String sessionId);
    
    SessionInfo refreshSession(String sessionId);
    
    void destroySession(String sessionId);
    
    List<SessionInfo> getActiveSessions(String userId);
}
```

### 4.3 SkillService

技能服务接口：

```java
/**
 * Skill 服务接口
 * @since 2.3
 */
public interface SkillService {
    
    SkillInfo findSkill(String skillId);
    
    List<SkillInfo> discoverSkills(SkillQuery query);
    
    SkillInstallResult installSkill(String userId, String skillId);
    
    SkillUninstallResult uninstallSkill(String userId, String skillId);
    
    List<InstalledSkillInfo> listInstalledSkills(String userId);
    
    Object invokeCapability(String userId, String skillId, 
                           String capability, Map<String, Object> params);
    
    void startSkill(String userId, String skillId);
    
    void stopSkill(String userId, String skillId);
}
```

## 5. 发现机制 (v2.3)

### 5.1 发现范围

| 范围 | 说明 | 发现方式 |
|------|------|---------|
| PERSONAL | 个人设备 | Local FS, UDP |
| DEPARTMENT | 部门分享 | UDP, mDNS, SkillCenter |
| COMPANY | 公司管理 | SkillCenter API |
| PUBLIC | 公共社区 | SkillCenter API |

### 5.2 发现提供者

```java
/**
 * 发现提供者接口
 * @since 2.3
 */
public interface DiscoveryProvider {
    
    String getProviderName();
    
    void initialize(DiscoveryConfig config);
    
    void start();
    
    void stop();
    
    CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query);
    
    int getPriority();
    
    boolean isApplicable(DiscoveryScope scope);
}
```

### 5.3 发现流程

```
1. 设置发现范围 (PERSONAL/DEPARTMENT/COMPANY/PUBLIC)
                    │
                    ▼
2. 获取适用的发现提供者
                    │
                    ├── LocalFsProvider (优先级 50)
                    ├── UdpProvider (优先级 100)
                    ├── MdnsProvider (优先级 90)
                    └── SkillCenterProvider (优先级 80)
                    │
                    ▼
3. 并行执行发现查询
                    │
                    ▼
4. 聚合和去重结果
                    │
                    ▼
5. 返回发现的场景/能力
```

## 6. 安全机制

### 6.1 身份认证

- **身份生成**: 每个 Agent 使用 ECC 算法生成密钥对
- **证书管理**: Agent 可以向信任的 CA 申请身份证书
- **证书验证**: Agent 之间通信时验证对方证书
- **本地认证**: 离线环境下可使用本地缓存的认证信息

### 6.2 数据加密

- **传输加密**: 使用 TLS 1.3 加密所有 Agent 间通信
- **端到端加密**: 对敏感数据使用 AES-256 进行端到端加密
- **密钥管理**: 使用 ECDH 算法进行密钥交换

## 7. CAP 能力路由

### 7.1 能力地址空间

| 地址范围 | 分类 | 说明 |
|---------|------|------|
| 00-3F (0-63) | System | 系统能力，框架核心功能 |
| 40-9F (64-159) | Common | 通用能力，业务通用功能 |
| A0-FF (160-255) | Extension | 扩展能力，第三方扩展 |

### 7.2 CapRouter

```java
/**
 * CAP 能力路由器
 * @since 2.3
 */
@Component
public class CapRouter {
    
    private final CapRegistry registry;
    private final Map<String, CapHandler> handlers = new ConcurrentHashMap<>();
    
    public CapResponse routeRequest(String capId, CapRequest request) {
        // 路由逻辑
    }
    
    public void registerHandler(String capId, CapHandler handler) {
        handlers.put(capId, handler);
    }
}
```

## 8. 事件系统

### 8.1 核心事件

| 事件类型 | 说明 | 触发条件 |
|---------|------|---------|
| CapabilityInvokedEvent | 能力被调用 | 能力调用成功 |
| CapabilityFailedEvent | 能力调用失败 | 能力调用异常 |
| SessionCreatedEvent | 会话创建 | 用户登录成功 |
| SessionDestroyedEvent | 会话销毁 | 用户登出或过期 |
| SkillInstalledEvent | 技能安装 | 技能安装完成 |
| SkillStartedEvent | 技能启动 | 技能启动成功 |

### 8.2 事件发布

```java
/**
 * 场景事件发布器
 * @since 2.3
 */
@Component
public class SceneEventPublisher {
    
    public void publish(SceneEvent event) {
        publisher.publishEvent(event);
    }
    
    public void publishAsync(SceneEvent event) {
        // 异步发布
    }
}
```

## 9. 错误处理

### 9.1 错误码

| 错误码 | 错误描述 | 处理策略 |
|--------|----------|----------|
| 1000 | 参数错误 | 直接返回错误 |
| 1001 | 认证失败 | 引导重新认证 |
| 1002 | 权限不足 | 直接返回错误 |
| 1003 | 资源不存在 | 直接返回错误 |
| 1004 | 请求超时 | 指数退避重试 |
| 1005 | 网络错误 | 尝试其他路径 |
| 2000 | 安全验证失败 | 引导重新认证 |
| 3001 | 离线模式限制 | 提示用户 |

## 10. 版本历史

| 版本 | 发布日期 | 主要变更 |
|------|----------|----------|
| 2.3 | 2026-02-23 | 架构重构，统一发现抽象，代码规范，完整实现指南 |

## 11. 参考资料

- [协议主文档](./protocol-main.md)
- [技能发现协议](./skill-discovery-protocol.md)
- [场景引擎架构](../../architecture/scene-engine-architecture.md)
- [SDK 协作文档](../../SDK-COLLABORATION.md)

---

**Ooder Team | Version 2.3 | 2026-02-23**
