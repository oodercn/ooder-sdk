# ooderAgent 企业版 2.1 开发套包

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/version-2.1-green.svg)](#)
[![Java](https://img.shields.io/badge/java-8+-orange.svg)](#)
[![Maven Central](https://img.shields.io/badge/maven%20central-v2.1-brightgreen.svg)](https://central.sonatype.com/artifact/net.ooder)

## 介绍

**ooderAgent 企业版 2.0** 是 ooder 企业级基础应用框架，为企业数字化转型提供完整的技术底座。本套包采用模块化设计，涵盖数据存储、缓存管理、文件存储、组织架构、消息通信、向量检索、物联网等核心能力，助力企业快速构建稳定、高效的业务系统。

本项目采用 **MIT 开源协议**，旨在为企业开发者提供开箱即用的企业级开发工具集。

> 📢 **发布声明**: 详见 [RELEASE_STATEMENT.md](RELEASE_STATEMENT.md)

---

## 🎯 版本说明

### 为什么从 2.0 开始

自本版本起，**ooderAgent 企业版与内部版本号完全统一**，不再区分内外部版本。这一决策标志着 ooderAgent 正式进入全开源阶段，内部与外部版本完全一致，实现真正的透明治理和生态共建。

当前版本 **2.1** 是在 2.0 基础上的稳定维护版本。

### 企业版 vs 个人版

| 特性 | 企业版 2.1 | 个人版 |
|------|-----------|--------|
| **JDK版本** | Java 8+ | Java 17+ |
| **依赖策略** | 适度依赖成熟开源组件 | 尽可能减少外部依赖 |
| **发布节奏** | 稳定迭代，严格测试 | 快速发版，敏捷迭代 |
| **开源规范** | 100%符合Maven Central规范 | - |
| **发展方向** | 以实用企业开发插件为主线横向扩展 | 开箱即用、精简核心 |

- **企业级应用 / 大型项目**: 推荐使用 **企业版 2.0**，注重稳定性和兼容性
- **个人学习 / 小型项目**: 推荐使用 **个人版**，享受最新技术特性

---

## 软件架构

ooderAgent 采用分层模块化设计，是一个独立的 Maven 多模块工程，包含以下核心模块：

### 🗄️ 数据存储层

| 模块 | 说明 | 功能概述 |
|------|------|----------|
| **ooder-database** | 数据库配置管理 | JDBC 数据库操作封装、连接池管理、DAO 工厂模式、SQL 工具及元数据管理 |
| **ooder-index-web** | 向量数据库配置 | 非结构化数据存储及检索服务，支持向量数据库配置 |

### ⚡ 缓存管理层

| 模块 | 说明 | 功能概述 |
|------|------|----------|
| **ooder-common-client** | REDIS 缓存管理 | Redis 连接池管理、缓存操作封装、分布式缓存支持 |

### 📁 文件存储层

| 模块 | 说明 | 功能概述 |
|------|------|----------|
| **ooder-vfs-web** | VFS 存储管理接口 | 分布式文件存储访问、文件版本控制、多人协作支持、发布管理基础支撑 |

### 🏢 组织管理层

| 模块 | 说明 | 功能概述 |
|------|------|----------|
| **ooder-org-web** | 内部组织机构接口 | 组织机构接口抽象、人员管理、部门管理、权限基础支撑 |

### 💬 消息通信层

| 模块 | 说明 | 功能概述 |
|------|------|----------|
| **ooder-msg-web** | 通用消息管理 | 消息通信服务、MQTT 协议支持、IoT 消息处理、消息队列管理 |

### ⚙️ 基础服务层

| 模块 | 说明 | 功能概述 |
|------|------|----------|
| **ooder-config** | 基础配置包 | Spring Boot 配置管理、配置属性自动绑定、配置处理器 |
| **ooder-server** | 微服务支撑 | 系统用户认证、集群管理、服务注册与发现、HTTP 代理服务 |

### 🌐 物联网层

| 模块 | 说明 | 功能概述 |
|------|------|----------|
| **ooder-iot-webclient** | IOT 北向协议转换基础包 | IoT 客户端 API、北向协议转换、设备管理、数据采集与上报 |

---

## 快速开始

### 环境要求

- Java 8+
- Maven 3.6+
- Redis 5.0+（可选，用于缓存功能）
- MySQL 5.7+ / PostgreSQL 10+（可选，用于数据库功能）

### 安装教程

#### Maven 依赖

在您的 `pom.xml` 文件中添加所需模块的依赖：

```xml
<!-- 基础配置包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-config</artifactId>
    <version>2.1</version>
</dependency>

<!-- 核心客户端包（包含 REDIS 管理） -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>2.1</version>
</dependency>

<!-- 数据库配置包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-database</artifactId>
    <version>2.1</version>
</dependency>

<!-- VFS 存储管理包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-vfs-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- 组织机构管理包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-org-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- 通用消息管理包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-msg-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- 向量数据库配置包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-index-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- 微服务支撑包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-server</artifactId>
    <version>2.1</version>
</dependency>

<!-- IoT 北向协议转换包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-iot-webclient</artifactId>
    <version>2.1</version>
</dependency>
```

**Maven Central**: https://search.maven.org/search?q=g:net.ooder

#### 源码编译

```bash
# 克隆项目
git clone https://github.com/ooder-net/ooder-common.git
cd ooder-common

# 编译安装
mvn clean install
```

---

## 模块详细说明

### 1. 🗄️ ooder-database - 数据库配置管理

数据库操作封装模块，提供轻量级、企业级的数据库操作工具。

**核心功能：**
- ✅ 多数据源配置管理
- ✅ 连接池自动管理（支持 HikariCP、C3P0）
- ✅ DAO 工厂模式，简化数据访问层开发
- ✅ SQL 解析与元数据管理
- ✅ 事务管理与连接池监控

**使用示例：**
```java
import net.ooder.common.database.DBAgent;
import net.ooder.common.database.dao.DAO;

// 获取数据库代理
DBAgent dbAgent = new DBAgent("configKey");

// 执行 SQL
int result = dbAgent.execute("SELECT * FROM users WHERE status = ?", 1);

// 使用 DAO 工厂
DAO<User> userDao = DAOFactory.getDAO(User.class);
List<User> users = userDao.findAll();
```

---

### 2. ⚡ ooder-common-client - REDIS 缓存管理

企业级缓存管理模块，提供 Redis 连接池管理和缓存操作封装。

**核心功能：**
- ✅ Redis 连接池自动管理
- ✅ 分布式缓存支持
- ✅ 缓存序列化与反序列化
- ✅ 缓存过期策略配置
- ✅ 集群模式支持

**使用示例：**
```java
import net.ooder.common.cache.redis.RedisPoolUtil;
import redis.clients.jedis.Jedis;

// 获取 Redis 连接
RedisPoolUtil redisPool = RedisPoolUtil.getInstance("redisKey");
try (Jedis jedis = redisPool.getResource()) {
    // 设置缓存
    jedis.set("user:1001", userJson);
    jedis.expire("user:1001", 3600);
    
    // 获取缓存
    String userData = jedis.get("user:1001");
}
```

---

### 3. 📁 ooder-vfs-web - VFS 存储管理接口

分布式文件存储管理模块，提供企业级文件存储解决方案。

**核心功能：**
- ✅ 分布式文件存储访问
- ✅ 文件版本控制与管理
- ✅ 多人协作文件锁定机制
- ✅ 文件发布与审批流程
- ✅ 大文件分片上传/下载

**使用示例：**
```java
import net.ooder.vfs.VFSManager;
import net.ooder.vfs.model.VFSFile;

// 获取 VFS 管理器
VFSManager vfsManager = VFSManager.getInstance();

// 上传文件
VFSFile file = vfsManager.upload("/docs/report.pdf", inputStream);

// 下载文件
InputStream downloadStream = vfsManager.download(file.getId());

// 版本管理
List<VFSFile> versions = vfsManager.getVersions(file.getId());
```

---

### 4. 🏢 ooder-org-web - 内部组织机构接口

企业组织机构管理模块，提供灵活的组织架构管理能力。

**核心功能：**
- ✅ 组织机构树形管理
- ✅ 人员信息管理
- ✅ 部门岗位配置
- ✅ 权限基础数据支撑
- ✅ 组织架构导入/导出

**使用示例：**
```java
import net.ooder.common.org.OrgManager;
import net.ooder.common.org.model.Department;
import net.ooder.common.org.model.Person;

// 获取组织管理器
OrgManager orgManager = OrgManager.getInstance();

// 创建部门
Department dept = new Department();
dept.setName("技术部");
dept.setCode("TECH");
orgManager.createDepartment(dept);

// 添加人员
Person person = new Person();
person.setName("张三");
person.setDepartmentId(dept.getId());
orgManager.createPerson(person);
```

---

### 5. 💬 ooder-msg-web - 通用消息管理

企业级消息通信模块，支持多种消息协议和 IoT 场景。

**核心功能：**
- ✅ MQTT 协议支持
- ✅ 消息队列管理
- ✅ 实时消息推送
- ✅ 消息持久化与重试
- ✅ IoT 设备消息处理

**使用示例：**
```java
import net.ooder.msg.MsgFactory;
import net.ooder.msg.model.Message;

// 获取消息工厂
MsgFactory msgFactory = MsgFactory.getInstance();

// 发送消息
Message message = new Message();
message.setTopic("device/data");
message.setPayload(dataJson);
msgFactory.publish(message);

// 订阅消息
msgFactory.subscribe("device/+/status", (msg) -> {
    System.out.println("收到设备状态: " + msg.getPayload());
});
```

---

### 6. 🔍 ooder-index-web - 向量数据库配置

非结构化数据存储与检索模块，支持向量数据库配置。

**核心功能：**
- ✅ 向量数据存储
- ✅ 相似度检索
- ✅ 全文检索支持
- ✅ 索引管理
- ✅ 高性能查询优化

**使用示例：**
```java
import net.ooder.index.IndexFactory;
import net.ooder.index.model.IndexDocument;

// 获取索引工厂
IndexFactory indexFactory = IndexFactory.getInstance();

// 创建索引文档
IndexDocument doc = new IndexDocument();
doc.setId("doc001");
doc.setContent("文档内容...");
doc.setVector(vectorData);

// 存储索引
indexFactory.index(doc);

// 向量检索
List<IndexDocument> results = indexFactory.search(vectorQuery, 10);
```

---

### 7. ⚙️ ooder-config - 基础配置包

Spring Boot 基础配置管理模块，提供配置属性自动绑定。

**核心功能：**
- ✅ 配置属性自动绑定
- ✅ 配置处理器
- ✅ 多环境配置支持
- ✅ 配置热更新

**使用示例：**
```java
import net.ooder.config.AppConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// 使用配置
@Autowired
private AppConfig appConfig;
```

---

### 8. 🖥️ ooder-server - 微服务支撑

微服务基础设施模块，提供服务注册、认证、集群管理等功能。

**核心功能：**
- ✅ 系统用户认证与授权
- ✅ 服务注册与发现
- ✅ 集群管理与负载均衡
- ✅ HTTP 代理与网关
- ✅ 服务健康检查

**使用示例：**
```java
import net.ooder.server.auth.AuthManager;
import net.ooder.server.cluster.ClusterManager;

// 用户认证
AuthManager authManager = AuthManager.getInstance();
boolean isValid = authManager.authenticate(username, password);

// 集群管理
ClusterManager clusterManager = ClusterManager.getInstance();
List<ServiceNode> nodes = clusterManager.getActiveNodes();
```

---

### 9. 🌐 ooder-iot-webclient - IOT 北向协议转换基础包

物联网北向协议转换模块，提供 IoT 设备管理和数据采集能力。

**核心功能：**
- ✅ 北向协议转换
- ✅ 设备管理
- ✅ 数据采集与上报
- ✅ 设备状态监控
- ✅ 远程指令下发

**使用示例：**
```java
import net.ooder.agent.client.IoTClient;
import net.ooder.agent.client.model.DeviceData;

// 获取 IoT 客户端
IoTClient iotClient = IoTClient.getInstance();

// 上报设备数据
DeviceData data = new DeviceData();
data.setDeviceId("DEV001");
data.setTemperature(25.5);
data.setHumidity(60.0);
iotClient.reportData(data);

// 监听指令
iotClient.onCommand((cmd) -> {
    System.out.println("收到指令: " + cmd.getAction());
});
```

---

## 企业级特性

### 🔒 安全性
- 完善的权限控制体系
- 数据加密传输与存储
- 安全审计日志

### 📈 高性能
- 连接池优化管理
- 缓存加速机制
- 异步消息处理

### 🏗️ 可扩展性
- 模块化架构设计
- 插件化扩展机制
- 多租户支持

### 🔧 可维护性
- 完善的日志体系
- 监控与告警
- 配置热更新

---

## 贡献指南

我们欢迎社区贡献！请遵循以下步骤：

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

完整发布声明详见 [RELEASE_STATEMENT.md](RELEASE_STATEMENT.md)

---

## 版本历史

- **v2.1** (2026-02-15) - 企业版开发套包维护版本
  - 修复ooder-config模块parent配置问题
  - 修复ooder-annotation依赖版本问题
  - 优化模块依赖管理
  - 发布到 Maven Central 中央仓库

- **v2.0** (2026-02-08) - 企业版开发套包发布
  - 版本统一升级到 2.0，与 ooderAgent 内部版本号一致
  - 完善依赖管理配置
  - 修复编译问题
  - 优化模块结构
  - 发布到 Maven Central 中央仓库
  - MIT 开源协议

- **v1.0** (2025-08-25) - 首个 MIT 开源版本发布
  - 包名从 `com.ds` 迁移到 `net.ooder`
  - 完整的模块化架构
  - MIT 开源协议

---

## 联系我们

- **官网**: [https://ooder.net](https://ooder.net)
- **GitHub**: [https://github.com/ooder-net/ooder-common](https://github.com/ooder-net/ooder-common)
- **问题反馈**: [GitHub Issues](https://github.com/ooder-net/ooder-common/issues)
- **邮箱**: team@ooder.net

---

## 致谢

感谢所有为 ooder 项目做出贡献的开发者！

---

**ooderAgent 企业版 2.0** - 让企业级开发更简单、更可控 🚀
