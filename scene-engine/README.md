# SceneEngine - Ooder 场景引擎服务

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Version](https://img.shields.io/badge/version-0.7.3-green.svg)]()
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://openjdk.org/)

## 项目概述

SceneEngine (原 northbound-services) 是 Ooder Agent 平台的场景引擎服务集群，提供专业技能服务和场景驱动的能力编排。基于 Skills 架构，支持组织管理、文件存储、消息通信、MQTT 服务等核心场景。

### 核心特性

- **场景驱动架构**：基于 YAML 配置的场景编排
- **技能模块化**：可插拔的 Skill 能力封装
- **多租户支持**：企业级多租户隔离
- **云原生部署**：支持 Kubernetes 集群部署
- **协议标准化**：统一的南向/北向协议

## 版本信息

| 版本 | 发布日期 | SDK 版本 | 说明 |
|------|----------|----------|------|
| **0.7.3** | 2026-02-20 | agent-sdk 0.7.3 | 场景引擎重构、协议增强 |
| 0.7.2 | 2026-02-17 | agent-sdk 0.7.2 | Skills 架构完善 |
| 0.7.1 | 2026-02-15 | agent-sdk 0.7.1 | 场景驱动架构 |

## 项目结构

```
sceneEngine/
├── northbound-core/           # 核心模块 - Skills 基础接口和实现
│   └── src/main/java/net/ooder/northbound/
│       ├── skill/             # 技能基础接口
│       ├── scene/             # 场景管理
│       └── protocol/          # 协议实现
│
├── scene-engine/              # 场景引擎核心
│   └── src/main/java/net/ooder/scene/
│       ├── config/            # 场景配置
│       ├── engine/            # 引擎实现
│       └── lifecycle/         # 生命周期管理
│
├── skill-org/                 # 组织服务技能
│   ├── skill-org-feishu/      # 飞书组织服务
│   ├── skill-org-dingding/    # 钉钉组织服务
│   ├── skill-org-wecom/       # 企业微信组织服务
│   └── skill-org-ldap/        # LDAP 组织服务
│
├── skill-vfs/                 # 文件服务技能
│   ├── skill-vfs-local/       # 本地存储
│   ├── skill-vfs-minio/       # MinIO 存储
│   ├── skill-vfs-oss/         # 阿里云 OSS
│   └── skill-vfs-s3/          # AWS S3
│
├── skill-mqtt/                # MQTT 服务技能
│   └── src/main/java/net/ooder/skill/mqtt/
│       ├── broker/            # MQTT Broker
│       └── client/            # MQTT 客户端
│
├── skill-msg/                 # 消息服务技能
│   └── src/main/java/net/ooder/skill/msg/
│       ├── p2p/               # P2P 消息
│       └── topic/             # Topic 订阅
│
├── skill-agent/               # Agent 服务技能
│   └── src/main/java/net/ooder/skill/agent/
│       ├── registry/          # Agent 注册
│       └── heartbeat/         # 心跳管理
│
├── northbound-gateway/        # 北向网关
│   └── src/main/java/net/ooder/gateway/
│       ├── http/              # HTTP API
│       └── comet/             # Comet 长连接
│
└── docs/                      # 设计文档
    ├── NORTHBOUND_REQUIREMENTS.md
    ├── NORTHBOUND_SKILLS_DESIGN.md
    └── SCENE_SKILLS_STANDARDIZATION.md
```

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 8+ | 运行环境 |
| Spring Boot | 2.7.0 | 应用框架 |
| agent-sdk | 0.7.3 | Ooder SDK |
| ooder-common | 2.2 | 公共库 |
| MQTT | 3.1.1/5.0 | 消息协议 |

## 场景分类

### 通讯类场景 (COMMUNICATION)

| 场景 | 技能 | 说明 |
|------|------|------|
| p2p | skill-msg | 点对点通信 |
| group | skill-msg | 群组通信 |
| broadcast | skill-msg | 广播通信 |
| mqtt-messaging | skill-mqtt | MQTT 消息 |

### 业务类场景 (BUSINESS)

| 场景 | 技能 | 说明 |
|------|------|------|
| auth | skill-org-* | 认证授权 |
| vfs | skill-vfs-* | 文件存储 |
| hr | skill-org-* | 人力资源 |
| crm | skill-org-* | 客户管理 |

### 协作类场景 (COLLABORATION)

| 场景 | 技能 | 说明 |
|------|------|------|
| meeting | skill-msg | 会议协作 |
| document | skill-vfs-* | 文档协作 |
| task | skill-agent | 任务管理 |

### 系统类场景 (SYSTEM)

| 场景 | 技能 | 说明 |
|------|------|------|
| sys | skill-agent | 系统管理 |
| monitor | skill-agent | 监控告警 |
| security | skill-org-* | 安全管理 |

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- Spring Boot 2.7.0

### 编译项目

```bash
# 克隆项目
git clone https://github.com/ooderCN/sceneEngine.git

# 编译
cd sceneEngine
mvn clean compile

# 打包
mvn clean package -DskipTests
```

### 启动服务

```bash
# 启动网关
java -jar northbound-gateway/target/northbound-gateway-0.7.3.jar

# 启动 MQTT 服务
java -jar skill-mqtt/target/skill-mqtt-0.7.3.jar

# 启动组织服务
java -jar skill-org/skill-org-feishu/target/skill-org-feishu-0.7.3.jar
```

### 场景配置示例

```yaml
# scene-config.yaml
scene:
  id: auth-scene
  name: 认证场景
  version: 0.7.3
  
  capabilities:
    - org-data-read
    - user-auth
    
  skills:
    - id: skill-org-feishu
      version: 0.7.3
      config:
        FEISHU_APP_ID: ${FEISHU_APP_ID}
        FEISHU_APP_SECRET: ${FEISHU_APP_SECRET}
        
  dependencies:
    - scene: vfs-scene
      capabilities:
        - file-read
        - file-write
```

## API 端点

### 网关 API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/info` | GET | 服务信息 |
| `/api/health` | GET | 健康检查 |
| `/api/scene/list` | GET | 场景列表 |
| `/api/scene/{id}` | GET | 场景详情 |
| `/api/skill/list` | GET | 技能列表 |
| `/api/skill/{id}/execute` | POST | 执行技能 |

### MQTT Topic 规范

| Topic | 说明 |
|-------|------|
| `ooder/p2p/{from}/{to}` | P2P 消息 |
| `ooder/group/{groupId}` | 群组消息 |
| `ooder/sensor/{deviceId}` | 设备数据 |
| `ooder/broadcast` | 广播消息 |

## 协议支持

### 南向协议

| 协议 | 说明 |
|------|------|
| DiscoveryProtocol | 节点发现 |
| LoginProtocol | 用户认证 |
| CollaborationProtocol | 场景协作 |

### 北向协议

| 协议 | 说明 |
|------|------|
| DomainManagementProtocol | 域管理 |
| ObservationProtocol | 可观测性 |

## 相关项目

| 项目 | 说明 | 地址 |
|------|------|------|
| **super-Agent** | 核心框架 | [github.com/ooderCN/super-Agent](https://github.com/ooderCN/super-Agent) |
| **ooder-Nexus** | 分发枢纽 | [github.com/ooderCN/ooder-Nexus](https://github.com/ooderCN/ooder-Nexus) |
| **skills** | 能力库 | [github.com/ooderCN/skills](https://github.com/ooderCN/skills) |
| **common** | 企业开发包 | [github.com/ooderCN/common](https://github.com/ooderCN/common) |

## 文档资源

| 文档 | 说明 |
|------|------|
| [总体需求](docs/NORTHBOUND_REQUIREMENTS.md) | 需求分析 |
| [Skills 架构设计](docs/NORTHBOUND_SKILLS_DESIGN.md) | 架构设计 |
| [Cap 设计规范](docs/SKILLS_CAP_SPECIFICATION.md) | 能力规范 |
| [场景标准化](docs/SCENE_SKILLS_STANDARDIZATION.md) | 场景规范 |

## 贡献指南

欢迎贡献代码和提出问题！

1. Fork 仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## 许可证

本项目采用 Apache License 2.0 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

- GitHub: [https://github.com/ooderCN/sceneEngine](https://github.com/ooderCN/sceneEngine)
- Gitee: [https://gitee.com/ooderCN/sceneEngine](https://gitee.com/ooderCN/sceneEngine)

---

**Made with ❤️ by Ooder Team**
