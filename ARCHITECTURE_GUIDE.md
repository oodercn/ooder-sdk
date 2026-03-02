# Ooder SDK 架构指南

> **版本**: 2.3  
> **日期**: 2026-02-27  
> **目标读者**: 上层应用开发者、架构师

---

## 目录

1. [工程结构](#1-工程结构)
2. [模块详解](#2-模块详解)
3. [依赖关系](#3-依赖关系)
4. [快速开始](#4-快速开始)
5. [版本历史](#5-版本历史)

---

## 1. 工程结构

```
ooder-sdk/
├── agent-sdk/                    # Agent SDK (v2.3)
│   ├── agent-sdk-api/            # API接口和模型定义
│   ├── agent-sdk-core/           # 核心实现
│   ├── skills-framework/         # 技能框架
│   ├── llm-sdk-api/              # LLM轻量级API
│   ├── llm-sdk/                  # LLM完整实现
│   └── README.md
├── ooder-api/                    # 基础API接口
├── ooder-util/                   # 工具类
├── ooder-annotation/             # 注解定义 (v2.3)
├── ooder-common/                 # 通用组件 (v2.3)
│   ├── ooder-config/             # 基础配置包
│   ├── ooder-common-client/      # 客户端核心组件
│   ├── ooder-server/             # 服务器核心
│   ├── ooder-vfs-web/            # VFS存储管理
│   ├── ooder-org-web/            # 组织机构接口
│   └── ooder-msg-web/            # 消息管理
├── scene-engine/                 # 场景引擎 (v2.3)
├── README.md
├── ARCHITECTURE_GUIDE.md         # 本文件
└── pom.xml
```

---

## 2. 模块详解

### 2.1 agent-sdk (v2.3)

**路径**: `agent-sdk/`

**定位**: 面向南向协议实现的轻量级Agent SDK

**子模块**:

| 子模块 | 说明 | 核心功能 |
|--------|------|----------|
| agent-sdk-api | API接口层 | Agent接口、能力接口、场景接口、命令接口、事件接口 |
| agent-sdk-core | 核心实现层 | Agent实现、能力编排引擎、协议适配器(A2A/REACH) |
| skills-framework | 技能框架 | 技能加载机制、技能代码生成、运行时支持 |
| llm-sdk-api | LLM轻量级API | LLM服务接口、聊天请求/响应模型、函数定义模型 |
| llm-sdk | LLM完整实现 | 完整LLM驱动(BaiduWenxin/Spark/Mock)、Story/Will实现、Memory管理 |

**依赖关系**:
```
agent-sdk-api (无依赖)
    ↑
    ├── llm-sdk-api → agent-sdk-api
    ├── skills-framework → agent-sdk-api
    └── agent-sdk-core → api + llm-sdk-api + skills-framework + llm-sdk
```

**使用示例**:
```java
import net.ooder.sdk.api.OoderSDK;

// 创建SDK实例
OoderSDK sdk = OoderSDK.builder()
    .agentId("my-agent")
    .agentName("My Agent")
    .build();

// 启动Agent
sdk.start();

// 使用能力
CapabilityResult result = sdk.getCapabilityInvoker()
    .invoke("capability-id", params);
```

---

### 2.2 scene-engine (v2.3)

**路径**: `scene-engine/`

**定位**: Ooder Agent平台的场景引擎服务，提供专业技能服务和场景驱动的能力编排

**核心特性**:
- 场景驱动架构：基于YAML配置的场景编排
- 技能模块化：可插拔的Skill能力封装
- CAP能力路由：统一的能力地址空间（00-FF）
- 多协议发现：UDP、mDNS、SkillCenter发现

**项目结构**:
```
scene-engine/
├── src/main/java/net/ooder/scene/
│   ├── core/                    # 核心引擎
│   │   ├── driver/              # 驱动框架
│   │   ├── provider/            # 能力提供者
│   │   ├── security/            # 安全模块
│   │   └── skill/               # 技能管理
│   ├── discovery/               # 能力发现
│   ├── protocol/                # 协议实现
│   ├── event/                   # 事件系统
│   ├── session/                 # 会话管理
│   └── workflow/                # 工作流
└── config/                      # 配置文件
```

**CAP能力地址空间**:
- 00-3F：系统能力（System）
- 40-9F：通用能力（Common）
- A0-FF：扩展能力（Extension）

**依赖关系**: scene-engine → agent-sdk (单向依赖)

---

### 2.3 ooder-common (v2.3)

**路径**: `ooder-common/`

**定位**: ooderAgent企业级开发套包，为企业数字化转型提供技术底座

**子模块**:

| 子模块 | 层级 | 功能概述 |
|--------|------|----------|
| ooder-config | 基础服务层 | Spring Boot配置管理、配置属性自动绑定 |
| ooder-common-client | 缓存管理层 | Redis连接池管理、分布式缓存支持 |
| ooder-server | 基础服务层 | 系统用户认证、集群管理、服务注册与发现 |
| ooder-vfs-web | 文件存储层 | 分布式文件存储、文件版本控制、多人协作 |
| ooder-org-web | 组织管理层 | 组织机构接口、人员管理、部门管理 |
| ooder-msg-web | 消息通信层 | MQTT协议支持、消息队列管理、IoT消息处理 |

**使用示例**:
```java
// ooder-config
@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class Application { }

// ooder-vfs-web
VFSManager vfsManager = VFSManager.getInstance();
VFSFile file = vfsManager.upload("/docs/report.pdf", inputStream);

// ooder-org-web
OrgManager orgManager = OrgManager.getInstance();
Department dept = new Department();
dept.setName("技术部");
orgManager.createDepartment(dept);

// ooder-msg-web
MsgFactory msgFactory = MsgFactory.getInstance();
Message message = new Message();
message.setTopic("device/data");
msgFactory.publish(message);
```

---

### 2.4 ooder-annotation (v2.3)

**路径**: `ooder-annotation/`

**定位**: 注解定义模块

**核心注解**:
- UI组件注解：@Agent, @AgentAction, @AgentDomain
- 数据绑定注解：@DBField, @DBTable
- 事件注解：@EventEnums
- 其他注解：@AIGCModel, @AIGCPrompt, @AIGCTask等

---

### 2.5 ooder-api

**路径**: `ooder-api/`

**定位**: 基础API接口

**核心接口**:
- `Identifiable` - 可标识接口
- `Named` - 可命名接口
- `Versioned` - 可版本化接口
- `OoderException` - 基础异常

---

### 2.6 ooder-util

**路径**: `ooder-util/`

**定位**: 工具类模块

**核心工具**:
- `StringUtils` - 字符串工具

---

## 3. 依赖关系

### 3.1 整体依赖图

```
上层应用
    ↓ 依赖
scene-engine
    ↓ 依赖
agent-sdk-core
    ↓ 依赖
agent-sdk-api + skills-framework + llm-sdk-api + llm-sdk
    ↓ 依赖
ooder-common-* (ooder-config, ooder-common-client, ooder-server等)
    ↓ 依赖
ooder-api + ooder-util + ooder-annotation
```

### 3.2 Maven依赖示例

**场景引擎**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>
```

**Agent SDK完整版**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>
```

**Agent SDK API**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

**ooder-common配置**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-config</artifactId>
    <version>2.3</version>
</dependency>
```

---

## 4. 快速开始

### 4.1 环境要求

- JDK 1.8+
- Maven 3.6+

### 4.2 构建项目

```bash
# 克隆仓库
git clone https://github.com/oodercn/ooder-sdk.git
cd ooder-sdk

# 构建所有模块
mvn clean install

# 构建指定模块
cd agent-sdk
mvn clean install
```

### 4.3 使用示例

**创建场景**:
```java
import net.ooder.sdk.api.scene.SceneManager;
import net.ooder.sdk.api.scene.SceneDefinition;
import net.ooder.sdk.common.enums.SceneType;

SceneDefinition scene = new SceneDefinition();
scene.setSceneId("my-scene");
scene.setName("My Scene");
scene.setType(SceneType.PRIMARY);

SceneManager sceneManager = ...;
sceneManager.create(scene);
```

**发现技能**:
```java
import net.ooder.sdk.service.skill.SkillService;

SkillService skillService = ...;
CompletableFuture<SkillPackage> future = skillService.discoverSkill("skill-id");
```

---

## 5. 版本历史

### 2.3 (当前版本)

**主要变更**:
- agent-sdk: 泛型化改造、模块化重构、统一版本2.3
- scene-engine: 架构重构、能力发现抽象、版本2.3
- ooder-common: 版本统一为2.3
- ooder-annotation: 版本统一为2.3

### 2.2

- ooder-common和ooder-annotation版本升级

### 1.0.0

- 统一SDK发布

---

## 附录

### 相关资源

| 资源 | 链接 |
|------|------|
| 主仓库 | https://github.com/oodercn/ooder-sdk |
| 技能仓库 | https://github.com/oodercn/ooder-skills |
| Maven Central | https://central.sonatype.com/artifact/net.ooder |

### 联系方式

- **GitHub Issues**: https://github.com/oodercn/ooder-sdk/issues

---

**Made with ❤️ by Ooder Team**
