# Ooder SDK 模块分工文档

> **版本**: 2.3  
> **日期**: 2026-02-27  
> **状态**: 正式发布

---

## 一、模块总览

```
ooder-sdk (父工程: ooder-sdk-parent 1.0.0)
├── agent-sdk (聚合工程: pom类型, v2.3)
│   ├── agent-sdk-api (jar类型, v2.3)
│   ├── llm-sdk-api (jar类型, v2.3)
│   ├── llm-sdk (jar类型, v2.3)
│   ├── skills-framework (jar类型, v2.3)
│   └── agent-sdk-core (jar类型, v2.3)
├── ooder-api (jar类型, v2.3)
├── ooder-util (jar类型, v2.3)
├── ooder-annotation (jar类型, v2.3)
├── ooder-common (聚合工程: pom类型, v2.3)
│   ├── ooder-config (jar类型, v2.3)
│   ├── ooder-database (jar类型, v2.3)
│   ├── ooder-common-client (jar类型, v2.3)
│   ├── ooder-server (jar类型, v2.3)
│   ├── ooder-vfs-web (jar类型, v2.3)
│   ├── ooder-org-web (jar类型, v2.3)
│   └── ooder-msg-web (jar类型, v2.3)
└── scene-engine (jar类型, v2.3)
```

---

## 二、模块详细分工

### 2.1 根父工程 (ooder-sdk-parent)

**路径**: `pom.xml`  
**类型**: pom  
**版本**: 1.0.0  
**职责**: 统一管理所有子模块的版本和依赖

**管理内容**:
- 版本属性: `ooder.version=2.3`, `agent-sdk.version=2.3`, `scene-engine.version=2.3`
- 依赖管理: 统一声明所有可用依赖的版本
- 插件管理: 编译、打包、发布插件配置
- 构建配置: local/release 两种构建模式

**子模块列表**:
```xml
<modules>
    <module>ooder-api</module>
    <module>ooder-util</module>
    <module>agent-sdk</module>
    <module>ooder-annotation</module>
    <module>ooder-common</module>
    <module>scene-engine</module>
</modules>
```

---

### 2.2 agent-sdk (聚合工程)

**路径**: `agent-sdk/pom.xml`  
**artifactId**: agent-sdk  
**类型**: pom  
**版本**: 2.3  
**职责**: 聚合所有 Agent SDK 相关子模块

**⚠️ 重要**: 此模块为 pom 类型，**不能直接作为依赖使用**

**子模块**:

| 子模块 | artifactId | 类型 | 版本 | 职责 |
|--------|-----------|------|------|------|
| agent-sdk-api | agent-sdk-api | jar | 2.3 | 定义核心API接口 |
| llm-sdk-api | llm-sdk-api | jar | 2.3 | 定义LLM轻量级API |
| llm-sdk | llm-sdk | jar | 2.3 | LLM完整实现 |
| skills-framework | skills-framework | jar | 2.3 | 技能框架实现 |
| agent-sdk-core | agent-sdk-core | jar | 2.3 | 核心实现层 |

**依赖关系**:
```
agent-sdk-api (无依赖)
    ↑
    ├── llm-sdk-api → agent-sdk-api
    ├── skills-framework → agent-sdk-api
    └── agent-sdk-core → api + llm-sdk-api + skills-framework + llm-sdk
```

---

#### 2.2.1 agent-sdk-api

**artifactId**: agent-sdk-api  
**类型**: jar  
**职责**: 定义 Agent SDK 的核心 API 接口

**核心接口**:
- `Agent` - Agent 接口
- `Capability` - 能力接口
- `SceneManager` - 场景管理器接口
- `SceneGroupManager` - 场景组管理器接口
- `Command` - 命令接口
- `Event` - 事件接口

**核心枚举**:
- `AgentType` - Agent 类型 (MCP, ROUTE, END, SCENE, WORKER)
- `CapabilityStatus` - 能力状态
- `CapabilityType` - 能力类型
- `MemberRole` - 成员角色 (PRIMARY, BACKUP, OBSERVER, MEMBER)
- `SceneType` - 场景类型 (PRIMARY, COLLABORATIVE)

**核心 Bean**:
- `SceneDefinition` - 场景定义
- `SceneSnapshot` - 场景快照
- `SceneGroup` - 场景组
- `SceneMember` - 场景成员

---

#### 2.2.2 llm-sdk-api

**artifactId**: llm-sdk-api  
**类型**: jar  
**职责**: 定义 LLM 轻量级 API 接口

**核心接口**:
- `LlmDriver` - LLM 驱动接口

**核心类**:
- `ChatRequest` - 聊天请求
- `ChatResponse` - 聊天响应
- `ChatMessage` - 聊天消息
- `FunctionDefinition` - 函数定义

---

#### 2.2.3 llm-sdk

**artifactId**: llm-sdk  
**类型**: jar  
**职责**: LLM 完整实现

**核心实现**:
- `BaiduWenxinDriver` - 百度文心驱动
- `SparkDriver` - 讯飞星火驱动
- `MockLlmDriver` - 模拟驱动

**核心组件**:
- `Story` - 故事管理
- `Will` - 意图管理
- `Memory` - 记忆管理

---

#### 2.2.4 skills-framework

**artifactId**: skills-framework  
**类型**: jar  
**职责**: 技能框架实现

**核心接口**:
- `Skill` - 技能接口
- `SkillPackageManager` - 技能包管理器
- `SkillRegistry` - 技能注册表
- `SkillInstaller` - 技能安装器

**核心类**:
- `SkillPackage` - 技能包
- `SkillManifest` - 技能清单
- `InstalledSkill` - 已安装技能

---

#### 2.2.5 agent-sdk-core

**artifactId**: agent-sdk-core  
**类型**: jar  
**职责**: Agent SDK 核心实现层

**核心实现**:
- `AgentImpl` - Agent 实现
- `CapabilityInvoker` - 能力调用器
- `CapabilityOrchestrator` - 能力编排器
- `A2AProtocolAdapter` - A2A 协议适配器
- `REACHProtocolAdapter` - REACH 协议适配器

**核心服务**:
- `SkillService` - 技能服务
- `SceneService` - 场景服务
- `NetworkService` - 网络服务

---

### 2.3 ooder-api

**路径**: `ooder-api/`  
**artifactId**: ooder-api  
**类型**: jar  
**版本**: 2.3  
**职责**: 基础 API 接口定义

**核心接口**:
- `Identifiable` - 可标识接口
- `Named` - 可命名接口
- `Versioned` - 可版本化接口

**核心异常**:
- `OoderException` - 基础异常

---

### 2.4 ooder-util

**路径**: `ooder-util/`  
**artifactId**: ooder-util  
**类型**: jar  
**版本**: 2.3  
**职责**: 工具类

**核心工具**:
- `StringUtils` - 字符串工具

---

### 2.5 ooder-annotation

**路径**: `ooder-annotation/`  
**artifactId**: ooder-annotation  
**类型**: jar  
**版本**: 2.3  
**职责**: 注解定义

**核心注解**:
- `@Agent` - Agent 注解
- `@AgentAction` - Agent 动作注解
- `@AgentDomain` - Agent 域注解
- `@DBField` - 数据库字段注解
- `@DBTable` - 数据库表注解
- `@AIGCModel` - AIGC 模型注解
- `@AIGCPrompt` - AIGC 提示词注解

---

### 2.6 ooder-common (聚合工程)

**路径**: `ooder-common/pom.xml`  
**artifactId**: ooder-common-all  
**类型**: pom  
**版本**: 2.3  
**职责**: 聚合所有 ooder-common 子模块

**⚠️ 重要**: 此模块为 pom 类型，**不能直接作为依赖使用**

**子模块**:

| 子模块 | artifactId | 类型 | 版本 | 职责 |
|--------|-----------|------|------|------|
| ooder-config | ooder-config | jar | 2.3 | 配置管理 |
| ooder-database | ooder-database | jar | 2.3 | 数据访问层 |
| ooder-common-client | ooder-common-client | jar | 2.3 | 客户端核心 |
| ooder-server | ooder-server | jar | 2.3 | 服务器核心 |
| ooder-vfs-web | ooder-vfs-web | jar | 2.3 | VFS 存储服务 |
| ooder-org-web | ooder-org-web | jar | 2.3 | 组织机构服务 |
| ooder-msg-web | ooder-msg-web | jar | 2.3 | 消息服务 |

**依赖关系**:
```
ooder-config (无依赖)
    ↑
ooder-database → ooder-config
    ↑
ooder-common-client → ooder-database
    ↑
ooder-server → ooder-common-client
    ↑
ooder-vfs-web → ooder-server
ooder-org-web → ooder-server
    ↑
ooder-msg-web → ooder-vfs-web + ooder-org-web
```

---

#### 2.6.1 ooder-config

**artifactId**: ooder-config  
**类型**: jar  
**职责**: 配置管理

**核心功能**:
- Spring Boot 配置属性自动绑定
- 场景配置管理
- 能力配置管理

---

#### 2.6.2 ooder-database

**artifactId**: ooder-database  
**类型**: jar  
**职责**: 数据库访问层，提供数据库连接池管理和DAO框架

**核心功能**:
- 数据库连接池管理（HikariCP、MiniConnectionPool）
- DAO框架（支持动态SQL、事务管理）
- 数据库元数据管理
- 多数据库类型支持（MySQL、PostgreSQL、HSQLDB等）

**核心类**:
- `ConnectionManager` - 连接管理器
- `ConnectionPool` - 连接池
- `DAO` - 数据访问对象接口
- `DBBeanBase` - 数据库实体基类
- `DBResult` - 数据库结果封装

**使用示例**:
```java
// 获取连接
ConnectionManager cm = ConnectionManagerFactory.getConnectionManager("default");
Connection conn = cm.getConnection();

// 使用DAO
DAO<User> userDao = DAOFactory.createDAO(User.class);
User user = userDao.findById(1L);
List<User> users = userDao.findAll();
```

---

#### 2.6.3 ooder-common-client

**artifactId**: ooder-common-client  
**类型**: jar  
**职责**: 客户端核心组件

**核心功能**:
- Redis 连接池管理
- 分布式缓存支持
- 集群管理

---

#### 2.6.3 ooder-server

**artifactId**: ooder-server  
**类型**: jar  
**职责**: 服务器核心

**核心功能**:
- 系统用户认证
- 集群管理
- 服务注册与发现
- Session 管理

---

#### 2.6.4 ooder-vfs-web

**artifactId**: ooder-vfs-web  
**类型**: jar  
**职责**: VFS 存储服务

**核心功能**:
- 分布式文件存储
- 文件版本控制
- 多人协作

---

#### 2.6.5 ooder-org-web

**artifactId**: ooder-org-web  
**类型**: jar  
**职责**: 组织机构服务

**核心功能**:
- 组织机构接口
- 人员管理
- 部门管理

---

#### 2.6.6 ooder-msg-web

**artifactId**: ooder-msg-web  
**类型**: jar  
**职责**: 消息服务

**核心功能**:
- MQTT 协议支持
- 消息队列管理
- IoT 消息处理

---

### 2.7 scene-engine

**路径**: `scene-engine/`  
**artifactId**: scene-engine  
**类型**: jar  
**版本**: 2.3  
**职责**: 场景引擎服务

**核心功能**:
- 场景驱动架构
- 技能模块化
- CAP 能力路由
- 多协议发现

**核心组件**:
- `SceneEngine` - 场景引擎
- `SceneManager` - 场景管理器
- `WorkflowEngine` - 工作流引擎
- `EventEngine` - 事件引擎
- `SessionManager` - 会话管理器

**依赖关系**:
```
scene-engine → agent-sdk-api
scene-engine → agent-sdk-core
scene-engine → ooder-annotation
scene-engine → ooder-config
scene-engine → ooder-common-client
scene-engine → ooder-server
scene-engine → ooder-vfs-web
scene-engine → ooder-org-web
scene-engine → ooder-msg-web
```

---

## 三、依赖关系总图

```
上层应用
    ↓ 依赖
scene-engine
    ↓ 依赖
agent-sdk-core + ooder-common-*
    ↓ 依赖
agent-sdk-api + skills-framework + llm-sdk-api + llm-sdk
    ↓ 依赖
ooder-api + ooder-util + ooder-annotation
```

---

## 四、可用依赖列表

### 4.1 可直接依赖的模块

| artifactId | 版本 | 说明 |
|-----------|------|------|
| scene-engine | 2.3 | 场景引擎 |
| agent-sdk-core | 2.3 | Agent SDK 完整版 |
| agent-sdk-api | 2.3 | Agent SDK API |
| llm-sdk | 2.3 | LLM 完整实现 |
| llm-sdk-api | 2.3 | LLM API |
| skills-framework | 2.3 | 技能框架 |
| ooder-annotation | 2.3 | 注解定义 |
| ooder-config | 2.3 | 配置管理 |
| ooder-database | 2.3 | 数据库访问层 |
| ooder-common-client | 2.3 | 客户端核心 |
| ooder-server | 2.3 | 服务器核心 |
| ooder-vfs-web | 2.3 | VFS 服务 |
| ooder-org-web | 2.3 | 组织机构服务 |
| ooder-msg-web | 2.3 | 消息服务 |
| ooder-api | 2.3 | 基础 API |
| ooder-util | 2.3 | 工具类 |

### 4.2 不可直接依赖的模块

| artifactId | 类型 | 说明 |
|-----------|------|------|
| agent-sdk | pom | 聚合工程，使用子模块替代 |
| ooder-common-all | pom | 聚合工程，使用子模块替代 |

---

## 五、使用建议

### 5.1 场景引擎用户

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>
```

### 5.2 Agent 开发者

```xml
<!-- 完整 SDK -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>

<!-- 或仅 API -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

### 5.3 LLM 开发者

```xml
<!-- 完整 LLM -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk</artifactId>
    <version>2.3</version>
</dependency>

<!-- 或仅 API -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

### 5.4 基础服务用户

```xml
<!-- 配置管理 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-config</artifactId>
    <version>2.3</version>
</dependency>

<!-- 缓存服务 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>2.3</version>
</dependency>
```

---

## 六、版本管理

### 6.1 版本属性

在根 `pom.xml` 中定义:

```xml
<properties>
    <ooder.version>2.3</ooder.version>
    <agent-sdk.version>2.3</agent-sdk.version>
    <scene-engine.version>2.3</scene-engine.version>
    <llm-sdk.version>2.3</llm-sdk.version>
</properties>
```

### 6.2 版本统一策略

- 所有模块统一使用版本 **2.3**
- 通过 `${ooder.version}` 和 `${agent-sdk.version}` 属性管理
- 避免硬编码版本号

---

**Made with ❤️ by Ooder Team**
