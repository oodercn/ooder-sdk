# Ooder 协议主文档 - v2.3

> **版本**: 2.3  
> **发布日期**: 2026-02-23  
> **状态**: 稳定版

## 1. 协议体系架构

### 1.1 协议分层

```
┌─────────────────────────────────────────────────────────────────┐
│                        应用层 (Application)                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  Scene API  │  │  Skill API  │  │   Cap API   │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
├─────────────────────────────────────────────────────────────────┤
│                        服务层 (Service)                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │   Session   │  │   Skill     │  │  Discovery  │             │
│  │   Manager   │  │   Service   │  │   Service   │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
├─────────────────────────────────────────────────────────────────┤
│                        协议层 (Protocol)                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │    UDP      │  │    mDNS     │  │   HTTP      │             │
│  │  Broadcast  │  │   DNS-SD    │  │  REST API   │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
├─────────────────────────────────────────────────────────────────┤
│                        传输层 (Transport)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │    TCP      │  │    UDP      │  │    TLS      │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 核心协议

| 协议 | 版本 | 说明 |
|------|------|------|
| Agent Protocol | 2.3 | Agent 间通信协议 |
| Discovery Protocol | 2.3 | 能力发现协议 |
| Session Protocol | 2.3 | 会话管理协议 |
| Skill Protocol | 2.3 | 技能管理协议 |

## 2. v2.3 核心特性

### 2.1 架构简化

- 移除冗余模块：ooder-codegen, ooder-infra-core, ooder-infra-driver
- 统一版本号：所有模块统一使用 2.3 版本
- 简化依赖关系：减少第三方依赖

### 2.2 能力发现抽象

统一的能力发现服务接口：

```java
public interface CapabilityDiscoveryService {
    CompletableFuture<SyncResult> syncAllIndexes();
    CompletableFuture<List<DiscoveredItem>> listScenes(String category);
    CompletableFuture<List<DiscoveredItem>> searchScenes(String query);
    CompletableFuture<SceneDetail> getSceneDetail(String sceneId);
    CompletableFuture<List<DiscoveredItem>> listCapabilities(String category);
    CompletableFuture<List<DiscoveredItem>> searchCapabilities(String query);
    CompletableFuture<CapabilityDetail> getCapabilityDetail(String capId);
    void registerProvider(DiscoveryProvider provider);
    void setDiscoveryScope(DiscoveryScope scope);
}
```

### 2.3 代码规范

- 全面补充 JavaDoc 注释
- 统一代码格式
- 添加 Spring 注解

## 3. 通信协议

### 3.1 消息格式

```json
{
  "protocol_version": "2.3",
  "message_type": "request|response|event",
  "timestamp": "2026-02-23T12:00:00Z",
  "trace_id": "uuid",
  "source": {
    "id": "agent-id",
    "type": "mcp|route|end"
  },
  "destination": {
    "id": "agent-id",
    "type": "mcp|route|end"
  },
  "payload": {},
  "metadata": {},
  "signature": "base64"
}
```

### 3.2 操作类型

| 操作 | 说明 | 模块 |
|------|------|------|
| session.create | 创建会话 | Session |
| session.validate | 验证会话 | Session |
| session.destroy | 销毁会话 | Session |
| skill.discover | 发现技能 | Skill |
| skill.install | 安装技能 | Skill |
| skill.invoke | 调用技能 | Skill |
| cap.invoke | 调用能力 | CapRouter |
| discovery.sync | 同步索引 | Discovery |

## 4. 安全机制

### 4.1 认证方式

| 方式 | 说明 | 场景 |
|------|------|------|
| Token | JWT Token 认证 | 在线环境 |
| Local | 本地缓存认证 | 离线环境 |
| Certificate | 证书认证 | 企业环境 |

### 4.2 加密传输

- TLS 1.3 传输加密
- AES-256 端到端加密
- ECDH 密钥交换

## 5. 版本兼容性

| 版本 | 兼容性 | 说明 |
|------|--------|------|
| 2.3 | 向前兼容 0.8.0 | 推荐使用 |
| 0.8.0 | 向前兼容 0.7.3 | 支持 |
| 0.7.3 | 基础版本 | 支持 |

## 6. 参考资料

- [Agent 协议](./agent-protocol.md)
- [技能发现协议](./skill-discovery-protocol.md)
- [SDK 协作文档](../../SDK-COLLABORATION.md)

---

**Ooder Team | Version 2.3 | 2026-02-23**
