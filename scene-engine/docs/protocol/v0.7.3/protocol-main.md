# Ooder AI Bridge 协议主文档 - v0.7.3

## 1. 协议概述

### 1.1 协议背景

随着人工智能技术的快速发展，多智能体系统之间的协同与通信变得日益重要。Ooder AI Bridge 协议旨在建立一个标准化的通信框架，实现不同智能体、技能服务和资源系统之间的无缝交互与协作。

v0.7.3 版本是 Ooder Agent 协议的重要升级版本，主要实现了场景管理的全面增强，包括节点发现、本地认证、场景组协作、离线支持和事件总线等核心特性。

### 1.2 协议目标

- **标准化通信**：定义统一的消息格式和通信接口，消除系统间的互操作障碍
- **安全可靠**：提供完善的认证、授权和加密机制，确保通信安全
- **广域网支持**：实现 SuperAgent 在广域网环境下的安全通信和认证
- **灵活扩展**：支持动态发现和注册，适应不断增长的服务和资源
- **高效协同**：优化通信协议，减少延迟，提高多智能体协同效率
- **离线支持**：支持网络断开时的离线运行和数据同步

### 1.3 应用场景

- 广域网内的 SuperAgent 节点通信
- 跨网络、跨设备的技能共享和调用
- 企业级多智能体系统的分布式部署
- 个人设备间的安全数据同步和协同
- 跨组织的 SuperAgent 网络协作
- 离线环境下的场景运行

## 2. 协议架构

### 2.1 分层架构

```
┌───────────────────────
│        应用层        │
└───────────────────────
        │
┌───────────────────────
│        协议层        │
│  - Discovery         │
│  - Login             │
│  - Collaboration     │
│  - Offline           │
└───────────────────────
        │
┌───────────────────────
│        传输层        │
└───────────────────────
        │
┌───────────────────────
│        安全层        │
└───────────────────────
```

### 2.2 核心组件

- **消息格式**：定义统一的请求/响应结构
- **命令系统**：标准化的操作指令集
- **安全机制**：认证、授权、加密和安全审计
- **错误处理**：统一的错误码和异常处理机制
- **扩展机制**：支持自定义命令和参数
- **广域网适配**：针对广域网环境的协议优化
- **离线支持**：离线模式和数据同步机制

## 3. 文档结构

本协议文档采用分册结构，包括以下部分：

### 3.1 主文档（本文件）

- 协议概述
- 架构设计
- 术语定义
- 版本说明
- 核心数据结构

### 3.2 Agent 协议分册

- [Agent 协议文档](./agent-protocol.md)
- MCP Agent 规范
- Route Agent 规范
- End Agent 规范
- Agent 通信机制
- DiscoveryProtocol 接口
- LoginProtocol 接口

### 3.3 Skill 分册

- [Skill 发现协议](./skill-discovery-protocol.md)
- Skill 需求规范
- Skill-Capability 关系
- Skill 接口定义
- Skill 数据模型

### 3.4 P2P 协议分册

- 广域网 P2P 网络架构
- 安全认证机制
- 节点发现与路由
- 数据传输与加密

### 3.5 场景组协议分册

- 场景组创建与管理
- 成员角色与权限
- 故障检测与切换
- VFS 资源访问

### 3.6 云托管协议分册

- 云托管提供商接口
- Kubernetes 集成
- 自动扩缩容
- 服务发现与负载均衡
- 成本估算

## 4. 核心数据结构

### 4.1 DiscoveryRequest（发现请求）

```java
public class DiscoveryRequest {
    private String discoveryType;     // 发现类型：UDP/DHT/SKILL_CENTER/MDNS
    private int timeout;              // 超时时间（毫秒）
    private int maxPeers;             // 最大返回节点数
    private List<String> peerTypes;   // 节点类型过滤
    private String filter;            // 自定义过滤条件
}
```

### 4.2 DiscoveryResult（发现结果）

```java
public class DiscoveryResult {
    private boolean success;          // 是否成功
    private String message;           // 结果消息
    private List<Peer> peers;         // 发现的节点列表
    private int totalFound;           // 总发现数量
    private long duration;            // 耗时（毫秒）
}
```

### 4.3 Peer（节点信息）

```java
public class Peer {
    private String peerId;            // 节点唯一标识
    private String peerName;          // 节点名称
    private String peerType;          // 节点类型：MCP/ROUTE/END
    private String address;           // 网络地址
    private int port;                 // 端口号
    private String status;            // 状态：ONLINE/OFFLINE
    private long lastSeen;            // 最后在线时间
    private long registeredAt;        // 注册时间
    private List<String> capabilities;// 能力列表
    private String version;           // 协议版本

    public boolean isOnline() {
        return "ONLINE".equals(status);
    }
}
```

### 4.4 LoginRequest（登录请求）

```java
public class LoginRequest {
    private String username;          // 用户名
    private String password;          // 密码
    private String loginType;         // 登录类型：ONLINE/LOCAL/DOMAIN
    private String deviceId;          // 设备ID
    private String clientIp;          // 客户端IP
    private String userAgent;         // 用户代理
}
```

### 4.5 LoginResult（登录结果）

```java
public class LoginResult {
    private boolean success;          // 是否成功
    private String message;           // 结果消息
    private Session session;          // 会话信息
    private String errorCode;         // 错误码
}
```

### 4.6 Session（会话信息）

```java
public class Session {
    private String sessionId;         // 会话唯一标识
    private String userId;            // 用户ID
    private String username;          // 用户名
    private String deviceId;          // 设备ID
    private String clientIp;          // 客户端IP
    private long createdAt;           // 创建时间
    private long expiresAt;           // 过期时间
    private long lastActiveAt;        // 最后活跃时间
    private String status;            // 状态：ACTIVE/EXPIRED/REVOKED

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) && !isExpired();
    }
}
```

### 4.7 Driver（驱动接口）

```java
public interface Driver {
    
    String getCategory();
    
    String getVersion();
    
    void initialize(DriverContext context);
    
    void shutdown();
    
    Object getSkill();
    
    Object getCapabilities();
    
    Object getFallback();
    
    boolean hasFallback();
    
    InterfaceDefinition getInterfaceDefinition();
    
    HealthStatus getHealthStatus();
}
```

### 4.8 InterfaceDefinition（接口定义）

```yaml
metadata:
  category: org
  version: "0.7.3"
  interfaceHash: sha256-hash

spec:
  capabilities:
    org-data-read:
      description: 组织数据读取能力
      methods:
        getOrgTree:
          description: 获取组织树
          input:
            type: object
            properties:
              rootId:
                type: string
          output:
            type: object
            properties:
              tree:
                type: array
```

## 5. 版本说明

### 5.1 当前版本

- 版本号：v0.7.3
- 发布日期：2026-02-20
- 主要更新：
  - **DiscoveryProtocol**：新增节点发现协议，支持局域网和广域网节点自动发现
  - **LoginProtocol**：新增本地认证协议，支持离线认证和会话管理
  - **CollaborationProtocol**：新增场景组协作协议，支持任务分配和状态同步
  - **OfflineService**：新增离线服务，支持网络断开时的场景运行和数据同步
  - **EventBus**：新增事件总线，统一事件管理和模块解耦
  - **Driver**：新增驱动代理组件，支持接口定义解析和动态代理
  - **CloudHostingProtocol**：新增云托管协议，支持 Kubernetes 集群部署和自动扩缩容

### 5.2 版本历史

| 版本号 | 发布日期 | 主要变化 |
|--------|----------|----------|
| v0.51  | 2025-12-15 | 初始版本发布 |
| v0.6.2 | 2026-01-18 | 术语统一与功能增强 |
| v0.6.3 | 2026-01-23 | 集成A2UI Skills，统一南向协议格式 |
| v0.6.5 | 2026-01-29 | P2P 网络架构，无状态技能分发 |
| v0.6.6 | 2026-02-04 | Maven Central 依赖管理，配置体系优化 |
| v0.7.0 | 2026-02-11 | 广域网内 P2P 安全认证设计 |
| v0.7.2 | 2026-02-15 | 场景组管理增强，密钥生成优化 |
| v0.7.3 | 2026-02-20 | 场景管理全面升级，新增发现/认证/协作/离线/事件总线/云托管/驱动代理 |

## 6. 术语定义

| 术语 | 解释 |
|------|------|
| Ooder | 智能体系统的统一品牌名称 |
| Skill | 提供特定功能的服务单元 |
| Capability | Skill提供的具体能力 |
| Space | 资源组织的基本单元 |
| Zone | Space内的子区域，用于更细粒度的资源管理 |
| Endpoint | 服务的网络访问点 |
| Endpoints | 服务的多个网络访问点列表 |
| AI Bridge | 智能体间通信的桥梁协议，属于北向协议 |
| MCP Agent | 主控智能体，负责资源管理和调度 |
| Route Agent | 路由智能体，负责消息路由和转发 |
| End Agent | 终端智能体，负责与外部设备和系统交互 |
| 北向协议 | 以mcpAgent为中心，向云服务（包括公云、私有云、混合云）通信的协议统称 |
| 南向协议 | mcpAgent向下与其他组件通信的协议统称 |
| Agent协议 | 南向协议的具体实现，用于mcpAgent、routeAgent、endAgent及skills之间的通信协议 |
| A2UI | AI to UI，从图片生成UI代码的技术 |
| VFS | Virtual File System，虚拟文件系统 |
| LLM | Large Language Model，大语言模型 |
| P2P | 点对点（Peer-to-Peer），一种去中心化的网络架构 |
| 广域网 | 覆盖范围较大的网络，如互联网 |
| 局域网 | 覆盖范围较小的网络，如家庭或办公室网络 |
| SceneGroup | 场景组，共享KEY和VFS资源的Agent集合 |
| DiscoveryProtocol | 节点发现协议，用于发现网络中的节点和技能 |
| LoginProtocol | 登录协议，用于本地认证和会话管理 |
| CollaborationProtocol | 协作协议，用于场景组任务协作 |
| OfflineService | 离线服务，支持离线运行和数据同步 |
| EventBus | 事件总线，统一事件管理和模块解耦 |
| Driver | 驱动代理，加载和缓存技能接口定义 |
| InterfaceParser | 接口解析器，解析 YAML/JSON 接口定义文件 |

## 7. 协议规范

### 7.1 消息格式

```json
{
  "version": "0.7.3",
  "id": "uuid-1234-5678-90ab-cdef",
  "timestamp": 1737000000000,
  "type": "request",
  "command": "skill.discover",
  "params": {
    "space_id": "space-123",
    "capability": "weather"
  },
  "metadata": {
    "sender_id": "agent-123",
    "trace_id": "trace-456",
    "security_level": "high",
    "offline_mode": false
  },
  "signature": "digital_signature"
}
```

### 7.2 错误处理

| 错误码 | 描述 | HTTP状态码 |
|--------|------|------------|
| 1001 | 参数错误 | 400 |
| 1002 | 认证失败 | 401 |
| 1003 | 权限不足 | 403 |
| 1004 | 资源不存在 | 404 |
| 1005 | 内部错误 | 500 |
| 1006 | 服务不可用 | 503 |
| 2001 | 网络错误 | 502 |
| 2002 | 安全验证失败 | 401 |
| 2003 | 广域网连接失败 | 504 |
| 2004 | 节点认证失败 | 401 |
| 3001 | 离线模式限制 | 409 |
| 3002 | 同步冲突 | 409 |
| 3003 | 离线数据过期 | 410 |

## 8. 适用范围

本协议适用于以下场景：

- 企业级智能体系统
- 跨平台技能服务集成
- 资源管理与监控系统
- 多智能体协同应用
- A2UI 图生代码场景
- LLM 集成与优化场景
- 广域网 P2P 安全通信场景
- 离线环境场景运行

## 9. 版权声明

MIT License

Copyright (c) 2026 Ooder Technology Co., Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this document and associated documentation files, to deal
in the document without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the document, and to permit persons to whom the document is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the document.

THE DOCUMENT IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE DOCUMENT OR THE USE OR OTHER DEALINGS IN THE
DOCUMENT.
