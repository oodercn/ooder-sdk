# SceneEngine 0.7.3 开发计划

## 一、项目定位

### 1.1 核心定位

**SceneEngine 是 ooderAgent 工程的北向协议核心实现**

- 作为所有北向工程节点的支撑 SDK
- 以 JAR 包形式提供集成
- 实现场景驱动的企业级服务架构

### 1.2 核心概念

| 概念 | 说明 |
|------|------|
| **Scene（场景）** | 服务组织单元，定义一组相关能力和协作关系 |
| **SceneServer** | 场景服务总协调器，管理场景生命周期和能力编排 |
| **Engine（引擎）** | 技术能力分类管理器，负责特定领域能力的安装、初始化、运行监控 |
| **Skill（技能）** | 原子化服务能力，由 Engine 管理和提供 |
| **Capability（能力）** | Skill 提供的具体功能点 |

---

## 二、术语字典

### 2.1 核心术语

| 术语 | 英文 | 定义 |
|------|------|------|
| 场景 | Scene | 服务组织单元，定义一组相关能力和协作关系 |
| 场景服务 | SceneServer | 场景服务总协调器，管理场景生命周期和能力编排 |
| 场景客户端 | SceneClient | 用户接入场景的统一入口 |
| 场景定义 | SceneDefinition | 场景的元数据描述 |
| 场景注册中心 | SceneRegistry | 维护场景定义和元数据的注册中心 |

### 2.2 组织术语

| 术语 | 英文 | 定义 |
|------|------|------|
| 组 | Group | 场景内的服务节点集合 |
| 成员 | Member | 场景或组的参与者 |
| 角色 | Role | 场景内的角色定义 |
| 链路 | Link | 场景内成员之间的连接关系 |

### 2.3 能力术语

| 术语 | 英文 | 定义 |
|------|------|------|
| 引擎 | Engine | 技术能力分类管理器 |
| 技能 | Skill | 原子化服务能力 |
| 能力 | Capability | Skill 提供的具体功能点 |
| 操作 | Operation | 能力的具体操作方法 |

---

## 三、用户故事

### 3.1 故事一：零配置启动

**角色**：开发者  
**目标**：无需配置文件，快速启动场景服务

```
场景流程：
1. 添加 Maven 依赖
   └── <dependency>
           <groupId>net.ooder</groupId>
           <artifactId>scene-engine</artifactId>
           <version>0.7.3</version>
       </dependency>

2. 获取 SceneServer 实例
   └── SceneServer server = SceneServerFactory.getDefault();

3. 用户登录
   └── SceneClient client = server.login("user@example.com", "password");

4. 调用能力
   └── Object result = client.invoke("message", "send", params);
```

### 3.2 故事二：场景注册与管理

**角色**：系统管理员  
**目标**：注册自定义场景并管理生命周期

```
场景流程：
1. 定义场景
   └── SceneDefinition hrScene = new CustomSceneBuilder()
           .sceneId("hr-scene")
           .sceneName("人力资源场景")
           .addDriver("driver-org")
           .addDriver("driver-vfs")
           .addCapability("employee", "create", "update", "query")
           .build();

2. 注册场景
   └── server.registerScene(hrScene);

3. 启动场景
   └── scene.start();

4. 监控场景
   └── SceneStatus status = scene.getStatus();
```

### 3.3 故事三：驱动集成

**角色**：平台开发者  
**目标**：集成自定义驱动扩展能力

```
场景流程：
1. 实现 Driver 接口
   └── public class CustomDriver implements Driver { ... }

2. 注册驱动
   └── server.getDriverRegistry().register(new CustomDriver());

3. 启动驱动
   └── driver.initialize(context);

4. 获取技能
   └── Object skill = driver.getSkill();
```

### 3.4 故事四：Provider接口实现

**角色**：Skills团队开发者  
**目标**：实现Provider接口提供具体能力

```
场景流程：
1. 实现Provider接口
   └── public class OpenWrtProvider implements 
           NetworkConfigProvider, DeviceManagementProvider { ... }

2. 通过ServiceLoader注册
   └── META-INF/services/net.ooder.scene.provider.NetworkConfigProvider

3. 场景驱动调用Provider
   └── ProviderRegistry.getProvider(NetworkConfigProvider.class);

4. 执行具体能力
   └── Result<NetworkConfig> result = provider.getNetworkConfig();
```

---

## 四、目录结构规范

### 4.1 当前状态

| 问题 | 当前状态 | 说明 |
|------|---------|------|
| 项目根目录 | scene-engine | ✅ 已正确命名 |
| 核心模块 | scene-engine | ✅ 已正确命名 |
| 驱动模块 | drivers/org, drivers/vfs, drivers/msg, drivers/mqtt | ✅ 已创建 |
| 网关模块 | scene-gateway | ✅ 已正确命名 |
| 技能模块 | skill-org, skill-vfs 等 | ✅ 已正确命名 |
| Java 包名 | net.ooder.scene | ✅ 已正确命名 |

### 4.2 目标目录结构

```
scene-engine/                              # 项目根目录
├── pom.xml                                # 父 POM
├── README.md
├── config/
│   ├── scene-dev.yaml
│   └── scene-prod.yaml
├── docs/
│   └── *.md
│
├── scene-engine/                          # 核心模块
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/net/ooder/scene/
│       │   │   ├── SceneEngine.java       # 场景引擎接口
│       │   │   ├── SceneClient.java       # 场景客户端接口
│       │   │   ├── AdminClient.java       # 管理客户端接口
│       │   │   │
│       │   │   ├── core/                  # 核心组件
│       │   │   │   ├── driver/            # 驱动层
│       │   │   │   │   ├── Driver.java
│       │   │   │   │   ├── DriverContext.java
│       │   │   │   │   ├── DriverRegistry.java
│       │   │   │   │   └── HealthStatus.java
│       │   │   │   ├── provider/          # Provider实现
│       │   │   │   │   ├── AgentProviderImpl.java
│       │   │   │   │   ├── HealthProviderImpl.java
│       │   │   │   │   └── ...
│       │   │   │   └── security/          # 安全组件
│       │   │   │       ├── AuditService.java
│       │   │   │       ├── PermissionService.java
│       │   │   │       └── SecurityInterceptor.java
│       │   │   │
│       │   │   ├── provider/              # Provider接口定义
│       │   │   │   ├── AgentProvider.java
│       │   │   │   ├── HealthProvider.java
│       │   │   │   ├── ProtocolProvider.java
│       │   │   │   ├── SkillShareProvider.java
│       │   │   │   ├── NetworkConfigProvider.java
│       │   │   │   └── DeviceManagementProvider.java
│       │   │   │
│       │   │   ├── protocol/              # 协议适配器
│       │   │   │   ├── LoginProtocolAdapter.java
│       │   │   │   └── DiscoveryProtocolAdapter.java
│       │   │   │
│       │   │   └── session/               # 会话管理
│       │   │       ├── SessionManager.java
│       │   │       └── TokenManager.java
│       │   │
│       │   └── resources/
│       │       └── META-INF/
│       │           └── services/          # ServiceLoader配置
│       └── test/
│
├── drivers/                               # 场景驱动模块
│   ├── pom.xml
│   ├── org/                               # ORG场景驱动
│   │   ├── META-INF/scene/
│   │   │   ├── scene.yaml
│   │   │   └── interface.yaml
│   │   └── src/main/java/net/ooder/scene/drivers/org/
│   │       ├── OrgDriver.java
│   │       ├── OrgFallback.java
│   │       ├── OrgSkillImpl.java
│   │       └── ...
│   ├── vfs/                               # VFS场景驱动
│   │   └── ...
│   ├── msg/                               # MSG场景驱动
│   │   └── ...
│   └── mqtt/                              # MQTT场景驱动
│       └── ...
│
├── skill-org/                             # 组织技能（保留兼容）
│   ├── pom.xml
│   └── src/main/java/net/ooder/scene/skills/org/
│
├── skill-vfs/                             # 文件技能（保留兼容）
│   ├── pom.xml
│   └── src/main/java/net/ooder/scene/skills/vfs/
│
├── skill-msg/                             # 消息技能（保留兼容）
│   ├── pom.xml
│   └── src/main/java/net/ooder/scene/skills/msg/
│
├── skill-mqtt/                            # MQTT 技能
│   ├── pom.xml
│   └── src/main/java/net/ooder/scene/skills/mqtt/
│
├── skill-agent/                           # 代理技能
│   ├── pom.xml
│   └── src/main/java/net/ooder/scene/skills/agent/
│
└── scene-gateway/                         # 场景网关
    ├── pom.xml
    └── src/main/java/net/ooder/scene/gateway/
```

---

## 五、开发阶段

### 阶段一：基础设施搭建（优先级：高）

| 任务 | 说明 | 状态 |
|------|------|------|
| 统一目录命名 | 确保所有目录符合规范 | ✅ 已完成 |
| 更新 POM 配置 | groupId, artifactId 统一为 net.ooder | ✅ 已完成 |
| 创建包结构 | 创建 net.ooder.scene 包结构 | ✅ 已完成 |

### 阶段二：核心接口定义（优先级：高）

| 任务 | 说明 | 状态 |
|------|------|------|
| Driver 接口 | 驱动核心接口 | ✅ 已完成 |
| DriverContext | 驱动上下文 | ✅ 已完成 |
| DriverRegistry | 驱动注册器 | ✅ 已完成 |
| Provider 接口 | AgentProvider, HealthProvider 等 | ✅ 已完成 |

### 阶段三：驱动层实现（优先级：高）

| 任务 | 说明 | 状态 |
|------|------|------|
| OrgDriver | 组织驱动实现 | ✅ 已完成 |
| VfsDriver | 文件驱动实现 | ✅ 已完成 |
| MsgDriver | 消息驱动实现 | ✅ 已完成 |
| MqttDriver | MQTT驱动实现 | ✅ 已完成 |

### 阶段四：Provider实现（优先级：高）

| 任务 | 说明 | 状态 |
|------|------|------|
| AgentProviderImpl | Agent管理实现 | ✅ 已完成 |
| HealthProviderImpl | 健康检查实现 | ✅ 已完成 |
| NetworkConfigProviderImpl | 网络配置实现 | ✅ 已完成 |
| UserProviderImpl | 用户管理实现 | ✅ 已完成 |

### 阶段五：技能服务实现（优先级：中）

| 任务 | 说明 | 状态 |
|------|------|------|
| OrgSkillImpl | 组织技能服务 | ✅ 已完成 |
| VfsSkillImpl | 文件技能服务 | ✅ 已完成 |
| MsgSkillImpl | 消息技能服务 | ✅ 已完成 |
| MqttSkillImpl | MQTT 技能服务 | ✅ 已完成 |

### 阶段六：测试与发布（优先级：高）

| 任务 | 说明 | 状态 |
|------|------|------|
| 单元测试 | 核心功能单元测试 | 待开发 |
| 集成测试 | 场景集成测试 | 待开发 |
| Maven 发布 | 推送到本地 Maven 仓库 | ✅ 已完成 |

---

## 六、技术规范

### 6.1 Java 版本

- **目标版本**：Java 8
- **编译版本**：1.8
- **Spring Boot**：2.7.0

### 6.2 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 包名 | net.ooder.scene | net.ooder.scene.core.driver |
| 接口 | 名词 | Driver, Provider |
| 实现类 | 接口名 + Impl | OrgDriver, AgentProviderImpl |
| 枚举 | 类型名 + Type/Status/Category | HealthStatus, MqttQos |
| Fallback | 场景名 + Fallback | OrgFallback, VfsFallback |

### 6.3 代码规范

- 使用 Lombok 简化代码
- 接口优先设计原则
- 依赖注入使用构造器注入
- 异常使用自定义业务异常
- Driver 模块自包含，不依赖 skill-* 模块

---

## 七、依赖关系

```
scene-engine (核心)
    ├── slf4j-api
    └── fastjson

drivers/org (ORG场景驱动)
    └── scene-engine

drivers/vfs (VFS场景驱动)
    └── scene-engine

drivers/msg (MSG场景驱动)
    └── scene-engine

drivers/mqtt (MQTT场景驱动)
    ├── scene-engine
    └── slf4j-api

skill-org (组织技能 - 兼容保留)
    └── scene-engine

skill-vfs (文件技能 - 兼容保留)
    └── scene-engine

skill-msg (消息技能 - 兼容保留)
    └── scene-engine

skill-mqtt (MQTT 技能)
    ├── scene-engine
    └── paho-client (1.2.5)

skill-agent (代理技能)
    └── scene-engine

scene-gateway (场景网关)
    ├── scene-engine
    ├── drivers/org
    ├── drivers/vfs
    ├── drivers/msg
    └── drivers/mqtt
```

---

## 八、验收标准

### 8.1 功能验收

- [x] Maven 可以零配置编译
- [x] Driver 可以初始化、启动、停止
- [x] Provider 接口可以通过 ServiceLoader 发现
- [x] 场景驱动可以独立发布
- [x] 技能可以安装、调用

### 8.2 质量验收

- [x] 所有核心接口有 Javadoc 注释
- [ ] 单元测试覆盖率 > 60%
- [x] 无编译错误和警告
- [x] Maven 构建成功

### 8.3 发布验收

- [x] 可以成功推送到本地 Maven 仓库
- [x] 依赖可以正确解析
- [x] 版本号正确 (0.7.3)

---

**文档版本**：2.0  
**更新日期**：2026-02-21  
**维护团队**：Ooder 技术团队
