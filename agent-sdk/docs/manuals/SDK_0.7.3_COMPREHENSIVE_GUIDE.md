# Ooder Agent SDK 0.7.3 综合指南

## 1. 概述

Ooder Agent SDK 0.7.3 版本是一个完整的Agent开发框架，为开发者提供了构建智能、协作、安全的Agent生态系统的能力。本指南涵盖了SDK的核心功能、升级指南、二次开发、消息与命令协作等方面的内容。

## 2. 版本特性

| 特性 | 描述 |
|------|------|
| **南北向分层架构** | 核心抽象层、南向服务层、北向服务层三层分离 |
| **驱动代理包支持** | 接口文件位置信息、驱动代理包加载、动态代理创建、离线降级实现 |
| **验证与开发工具链** | 四层验证机制、测试框架、代码生成器、命令行工具 |
| **三种安装模式** | DRIVER_ONLY、REMOTE_SKILL、FULL_INSTALL |
| **用户-组织-域模型** | 支持多租户、多组织、多域的复杂业务场景 |
| **增强场景组** | 自组网、LLM介入、离线运行、多点分支 |
| **增强北向协议** | 命令增强、异步处理、状态追踪、重试机制 |
| **ooder-common深度集成** | VFS、组织管理、消息服务、集群管理、MCP服务 |
| **YAML解析支持** | 使用SnakeYAML库解析接口定义文件 |
| **动态代理** | 使用Java反射创建远程调用代理 |

## 3. 升级指南

### 3.1 从 0.7.2 升级到 0.7.3

#### 3.1.1 依赖更新

在 `pom.xml` 中更新依赖：

```xml
<properties>
    <ooder.version>2.2</ooder.version>
</properties>

<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>0.7.3</version>
    </dependency>
    
    <!-- 新增SnakeYAML依赖 -->
    <dependency>
        <groupId>org.yaml</groupId>
        <artifactId>snakeyaml</artifactId>
        <version>1.33</version>
    </dependency>
</dependencies>
```

#### 3.1.2 配置迁移

```properties
# 驱动代理包配置
ooder.sdk.skill.driver.cache-enabled=true
ooder.sdk.skill.driver.cache-directory=./driver-cache
ooder.sdk.skill.driver.timeout=30000

# 验证工具配置
ooder.sdk.validator.level=4
ooder.sdk.validator.report-directory=./validation-reports

# 测试配置
ooder.sdk.test.type=UNIT
ooder.sdk.test.report-directory=./test-reports
```

#### 3.1.3 代码适配

```java
// 使用驱动代理包
import net.ooder.sdk.skill.driver.DriverLoader;
import net.ooder.sdk.skill.driver.ProxyFactory;
import net.ooder.sdk.api.skill.SkillInfo;
import net.ooder.sdk.api.skill.InstallRequest;
import net.ooder.sdk.api.skill.InstallMode;

// 加载驱动
DriverLoader driverLoader = new DriverLoaderImpl();
SkillInfo skillInfo = driverLoader.loadDriver("skill-001");

// 创建代理
ProxyFactory proxyFactory = new ProxyFactoryImpl();
Object proxy = proxyFactory.createRemoteProxy(
    skillInfo.getInterfaceLocation().getPath(),
    skillInfo.getRemoteEndpoint()
);

// 安装技能（指定安装模式）
InstallRequest request = InstallRequest.builder()
    .skillId("skill-001")
    .mode(InstallMode.DRIVER_ONLY)
    .build();

SkillInstaller installer = new SkillInstallerImpl();
installer.install(request);
```

### 3.2 从 0.6.6 升级到 0.7.3

请先参考 [SDK 0.7.2 升级指南](SDK_0.7.2_UPGRADE_GUIDE.md) 完成从 0.6.6 到 0.7.2 的升级，然后再按照上述步骤从 0.7.2 升级到 0.7.3。

## 4. 核心功能

### 4.1 驱动代理包支持

#### 4.1.1 核心组件

| 组件 | 说明 | 路径 |
|------|------|------|
| **SkillInfo** | 增强以支持接口文件位置和降级配置 | `src/main/java/net/ooder/sdk/api/skill/SkillInfo.java` |
| **DriverLoader** | 驱动加载接口，支持加载、缓存驱动代理包 | `src/main/java/net/ooder/sdk/skill/driver/DriverLoader.java` |
| **InterfaceParser** | 接口解析器，支持解析YAML和JSON格式的接口定义 | `src/main/java/net/ooder/sdk/skill/driver/InterfaceParser.java` |
| **ProxyFactory** | 代理工厂，支持动态创建远程调用代理和降级实现实例 | `src/main/java/net/ooder/sdk/skill/driver/ProxyFactory.java` |
| **SkillInstaller** | 增强以支持三种安装模式 | `src/main/java/net/ooder/sdk/core/skill/installer/SkillInstaller.java` |

#### 4.1.2 安装模式

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| **DRIVER_ONLY** | 仅安装驱动代理包 | 本地已有技能实现，仅需要代理层 |
| **REMOTE_SKILL** | 安装远程技能 | 技能实现部署在远程服务器 |
| **FULL_INSTALL** | 完整安装（驱动+技能） | 标准安装方式 |

### 4.2 验证与开发工具链

#### 4.2.1 核心组件

| 组件 | 说明 | 路径 |
|------|------|------|
| **SceneValidator** | 验证框架，支持四层验证 | `src/main/java/net/ooder/sdk/validator/SceneValidator.java` |
| **TestRunner** | 测试运行器，支持三种测试类型 | `src/main/java/net/ooder/sdk/test/TestRunner.java` |
| **CodeGenerator** | 代码生成器，支持生成Driver、Skill接口和Fallback代码 | `src/main/java/net/ooder/sdk/generator/CodeGenerator.java` |
| **SceneCli** | 命令行工具，支持init、generate、validate等命令 | `src/main/java/net/ooder/sdk/cli/SceneCli.java` |

#### 4.2.2 四层验证机制

| 层级 | 说明 | 验证内容 |
|------|------|----------|
| **Level 1** | 基础验证 | 目录结构、配置文件、依赖检查 |
| **Level 2** | 接口验证 | 接口定义、参数校验、返回值校验 |
| **Level 3** | 逻辑验证 | 业务逻辑、状态管理、错误处理 |
| **Level 4** | 集成验证 | 多Agent协作、网络通信、安全验证 |

### 4.3 消息与命令协作

#### 4.3.1 消息相关能力

| SDK 接口 | 说明 |
|---------|------|
| **CoreMessage** | 核心消息接口，支持消息标题、状态等字段 |
| **CoreTransport** | 传输层接口，支持消息确认机制 |
| **MessageType** | 消息类型枚举，支持6种类型 |
| **TransportMessage** | 传输消息实体 |
| **TransportResult** | 传输结果 |

#### 4.3.2 命令相关能力

| SDK 接口 | 说明 |
|---------|------|
| **ProtocolHub** | 协议中心，支持命令路由和批量处理 |
| **CommandPacket** | 命令包，支持方向字段 |
| **CommandResult** | 命令结果 |
| **ProtocolHandler** | 协议处理器 |

#### 4.3.3 消息确认机制

```java
public interface CoreTransport {
    // 现有方法...
    
    // 新增方法
    void acknowledge(String messageId);
    void setAckListener(AckListener listener);
}

public interface AckListener {
    void onAckReceived(String messageId, AckStatus status);
}

public enum AckStatus {
    DELIVERED,
    READ,
    FAILED
}
```

## 5. 二次开发指南

### 5.1 开发环境配置

| 工具 | 版本要求 |
|------|----------|
| JDK | 8+ |
| Maven | 3.6+ |
| IDE | IntelliJ IDEA / Eclipse |

### 5.2 项目结构

```
agent-sdk/
├── src/main/java/net/ooder/sdk/
│   ├── api/                    # 公开API层
│   │   ├── llm/               # LLM服务API
│   │   ├── metadata/          # 元数据服务API
│   │   ├── network/           # 网络服务API
│   │   ├── security/          # 安全服务API
│   │   └── storage/           # 存储服务API
│   ├── capability/            # 能力中心
│   │   ├── impl/              # 能力中心实现
│   │   └── model/             # 能力中心数据模型
│   ├── core/                  # 核心抽象层
│   │   ├── collaboration/     # 协作抽象
│   │   ├── network/           # 网络抽象
│   │   ├── protocol/          # 协议抽象
│   │   └── security/          # 安全抽象
│   ├── northbound/            # 北向服务层
│   │   └── protocol/          # 北向协议
│   │       ├── impl/          # 协议实现
│   │       └── model/         # 协议数据模型
│   ├── southbound/            # 南向服务层
│   │   └── protocol/          # 南向协议
│   │       ├── impl/          # 协议实现
│   │       └── model/         # 协议数据模型
│   ├── nexus/                 # Nexus连接服务
│   │   ├── impl/              # 实现类
│   │   ├── model/             # 数据模型
│   │   ├── offline/           # 离线服务
│   │   └── resource/          # 资源服务
│   ├── skill/                 # 技能相关
│   │   └── driver/            # 驱动代理包
│   ├── validator/             # 验证工具
│   ├── test/                  # 测试框架
│   ├── generator/             # 代码生成器
│   ├── cli/                   # 命令行工具
│   └── service/               # 内部服务层
│       ├── agent/             # Agent服务
│       ├── discovery/         # 发现服务
│       ├── heartbeat/         # 心跳服务
│       ├── llm/               # LLM服务
│       ├── metadata/          # 元数据服务
│       ├── network/           # 网络服务
│       ├── scene/             # 场景服务
│       ├── security/          # 安全服务
│       ├── skill/             # 技能服务
│       ├── skillcenter/       # 技能中心
│       └── storage/           # 存储服务
└── src/test/java/             # 测试代码
```

### 5.3 扩展能力中心

```java
package com.example.sdk.extension;

import net.ooder.sdk.capability.CapabilitySpecService;
import net.ooder.sdk.capability.model.*;

public class CustomCapabilitySpecService implements CapabilitySpecService {
    
    private final CapabilitySpecService delegate;
    
    public CustomCapabilitySpecService(CapabilitySpecService delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public CompletableFuture<CapabilitySpec> registerSpec(SpecDefinition definition) {
        if (definition.getType() == CapabilityType.CUSTOM_TYPE) {
            validateCustomSpec(definition);
        }
        return delegate.registerSpec(definition);
    }
    
    private void validateCustomSpec(SpecDefinition definition) {
        // 自定义验证逻辑
    }
    
    // 其他方法委托给delegate...
}
```

### 5.4 扩展协议

#### 5.4.1 扩展南向协议

```java
package com.example.sdk.extension;

import net.ooder.sdk.southbound.protocol.DiscoveryProtocol;
import net.ooder.sdk.southbound.protocol.model.*;

public class CustomDiscoveryProtocol implements DiscoveryProtocol {
    
    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            DiscoveryResult result = new DiscoveryResult();
            List<PeerInfo> peers = customDiscoveryLogic();
            result.setPeers(peers);
            return result;
        });
    }
    
    private List<PeerInfo> customDiscoveryLogic() {
        // 实现自定义发现逻辑
        return new ArrayList<>();
    }
    
    // 其他方法实现...
}
```

#### 5.4.2 扩展北向协议

```java
package com.example.sdk.extension;

import net.ooder.sdk.northbound.protocol.DomainManagementProtocol;
import net.ooder.sdk.northbound.protocol.model.*;

public class CustomDomainManagement implements DomainManagementProtocol {
    
    @Override
    public CompletableFuture<DomainInfo> createDomain(CreateDomainRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            // 前置检查
            validateDomainRequest(request);
            
            // 创建域
            DomainInfo domain = doCreateDomain(request);
            
            // 后置处理
            initializeDomainResources(domain);
            
            return domain;
        });
    }
    
    private void validateDomainRequest(CreateDomainRequest request) {
        // 验证逻辑
    }
    
    private DomainInfo doCreateDomain(CreateDomainRequest request) {
        // 创建逻辑
        return new DomainInfo();
    }
    
    private void initializeDomainResources(DomainInfo domain) {
        // 资源初始化
    }
}
```

## 6. 命令行工具

### 6.1 基本命令

| 命令 | 说明 | 示例 |
|------|------|------|
| **init** | 初始化场景 | `java -jar agent-sdk-0.7.3.jar init --name my-scene --path ./my-scene` |
| **generate** | 生成代码 | `java -jar agent-sdk-0.7.3.jar generate --type driver --interface ./my-scene/interface.yaml --output ./my-scene/driver` |
| **validate** | 验证场景 | `java -jar agent-sdk-0.7.3.jar validate --scene ./my-scene --level 4` |
| **test** | 运行测试 | `java -jar agent-sdk-0.7.3.jar test --scene ./my-scene --type unit` |
| **package** | 打包场景 | `java -jar agent-sdk-0.7.3.jar package --scene ./my-scene --output ./my-scene.zip` |
| **publish** | 发布场景 | `java -jar agent-sdk-0.7.3.jar publish --scene ./my-scene --registry http://registry.example.com` |
| **docs** | 生成文档 | `java -jar agent-sdk-0.7.3.jar docs --scene ./my-scene --output ./docs` |
| **help** | 查看帮助 | `java -jar agent-sdk-0.7.3.jar help` |

### 6.2 使用示例

```bash
# 初始化场景
java -jar agent-sdk-0.7.3.jar init --name my-scene --path ./my-scene

# 生成驱动代码
java -jar agent-sdk-0.7.3.jar generate --type driver --interface ./my-scene/interface.yaml --output ./my-scene/driver

# 生成技能接口代码
java -jar agent-sdk-0.7.3.jar generate --type skill --interface ./my-scene/interface.yaml --output ./my-scene/skill

# 生成降级实现代码
java -jar agent-sdk-0.7.3.jar generate --type fallback --interface ./my-scene/interface.yaml --output ./my-scene/fallback

# 验证场景
java -jar agent-sdk-0.7.3.jar validate --scene ./my-scene --level 4

# 运行测试
java -jar agent-sdk-0.7.3.jar test --scene ./my-scene --type unit

# 打包场景
java -jar agent-sdk-0.7.3.jar package --scene ./my-scene --output ./my-scene.zip
```

## 7. 部署与集成

### 7.1 部署方式

| 部署方式 | 说明 | 适用场景 |
|----------|------|----------|
| **本地Maven仓库** | 使用`mvn install`命令安装到本地Maven仓库 | 开发和测试环境 |
| **远程Maven仓库** | 使用`mvn deploy`命令部署到远程Maven仓库 | 生产环境 |
| **直接依赖** | 将JAR文件添加到项目的lib目录 | 快速集成 |

### 7.2 集成方式

| 集成方式 | 说明 | 示例 |
|----------|------|------|
| **Maven依赖** | 在pom.xml中添加依赖 | `<dependency><groupId>net.ooder</groupId><artifactId>agent-sdk</artifactId><version>0.7.3</version></dependency>` |
| **Gradle依赖** | 在build.gradle中添加依赖 | `implementation 'net.ooder:agent-sdk:0.7.3'` |
| **直接引用** | 直接引用JAR文件 | 将agent-sdk-0.7.3.jar添加到项目的classpath |

## 8. 测试指南

### 8.1 测试类型

| 测试类型 | 目标 | 工具 |
|----------|------|------|
| **单元测试** | 验证单个类和方法的功能正确性 | JUnit 4 |
| **集成测试** | 验证模块之间的协作正确性 | Spring Boot Test |
| **系统测试** | 验证整个SDK的功能正确性 | 手动测试 |
| **兼容性测试** | 验证与旧版本的兼容性 | 手动测试 |

### 8.2 测试示例

#### 8.2.1 单元测试

```java
package com.example.sdk.test;

import org.junit.Test;
import static org.junit.Assert.*;

public class CustomCapabilitySpecServiceTest {
    
    @Test
    public void testRegisterCustomSpec() throws Exception {
        CustomCapabilitySpecService service = new CustomCapabilitySpecService(delegate);
        
        SpecDefinition definition = new SpecDefinition();
        definition.setName("CustomCapability");
        definition.setType(CapabilityType.CUSTOM_TYPE);
        definition.setVersion("1.0.0");
        
        CapabilitySpec spec = service.registerSpec(definition).get(10, TimeUnit.SECONDS);
        
        assertNotNull(spec);
        assertEquals("CustomCapability", spec.getSpecName());
    }
}
```

#### 8.2.2 集成测试

```java
package com.example.sdk.test;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class CustomIntegrationTest {
    
    private CapabilityCenter capabilityCenter;
    
    @Before
    public void setUp() {
        capabilityCenter = new CapabilityCenterImpl();
        capabilityCenter.initialize();
    }
    
    @After
    public void tearDown() {
        capabilityCenter.shutdown();
    }
    
    @Test
    public void testEndToEndFlow() throws Exception {
        // 测试完整流程
    }
}
```

## 9. 最佳实践

### 9.1 代码规范

- 遵循Java命名规范
- 使用接口进行抽象
- 保持方法职责单一
- 添加必要的日志记录

### 9.2 性能优化

- 使用异步编程模型
- 合理使用缓存
- 避免阻塞操作
- 控制并发度

### 9.3 安全考虑

- 输入验证
- 权限检查
- 敏感数据加密
- 审计日志

### 9.4 故障排除

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| **驱动加载失败** | 接口定义文件格式错误 | 检查YAML/JSON格式是否正确 |
| **代理创建失败** | 接口方法签名不匹配 | 检查接口定义与实现是否一致 |
| **验证失败** | 场景配置不完整 | 按照验证报告修复问题 |
| **测试失败** | 测试用例配置错误 | 检查测试用例配置文件 |
| **消息发送失败** | 网络连接问题 | 检查网络配置和连接状态 |
| **命令执行失败** | 协议处理器未注册 | 注册相应的协议处理器 |

## 10. 总结

Ooder Agent SDK 0.7.3 版本是一个功能完整、架构清晰的Agent开发框架，为开发者提供了构建智能、协作、安全的Agent生态系统的能力。通过本指南，开发者可以快速了解SDK的核心功能、升级步骤、二次开发方法以及最佳实践，从而更高效地开发和部署Agent应用。

---

**Ooder Agent SDK 0.7.3** - 构建智能、协作、安全的Agent生态系统！