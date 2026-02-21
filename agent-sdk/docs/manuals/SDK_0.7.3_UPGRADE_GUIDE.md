# Ooder Agent SDK 0.7.3 升级指南

## 1. 升级概览

### 1.1 版本特性

Ooder Agent SDK 0.7.3 版本在 0.7.2 版本的基础上，新增了驱动代理包支持和验证与开发工具链，为开发者提供了更完整的Agent开发生态系统。

| 特性 | 描述 |
|------|------|
| **驱动代理包支持** | 接口文件位置信息、驱动代理包加载、动态代理创建、离线降级实现 |
| **验证与开发工具链** | 四层验证机制、测试框架、代码生成器、命令行工具 |
| **三种安装模式** | DRIVER_ONLY、REMOTE_SKILL、FULL_INSTALL |
| **YAML解析支持** | 使用SnakeYAML库解析接口定义文件 |
| **动态代理** | 使用Java反射创建远程调用代理 |

### 1.2 升级影响

| 影响范围 | 影响程度 | 说明 |
|----------|----------|------|
| **架构变更** | 🟡 中 | 新增驱动代理包和验证工具链模块 |
| **API变更** | 🟡 中 | 新增接口，保持向后兼容 |
| **配置变更** | 🟢 低 | 新增配置项，旧配置兼容 |
| **依赖变更** | 🟢 低 | 新增SnakeYAML依赖 |

## 2. 新增功能

### 2.1 驱动代理包支持

#### 2.1.1 核心组件

| 组件 | 说明 | 路径 |
|------|------|------|
| **SkillInfo** | 增强以支持接口文件位置和降级配置 | `src/main/java/net/ooder/sdk/api/skill/SkillInfo.java` |
| **DriverLoader** | 驱动加载接口，支持加载、缓存驱动代理包 | `src/main/java/net/ooder/sdk/skill/driver/DriverLoader.java` |
| **InterfaceParser** | 接口解析器，支持解析YAML和JSON格式的接口定义 | `src/main/java/net/ooder/sdk/skill/driver/InterfaceParser.java` |
| **ProxyFactory** | 代理工厂，支持动态创建远程调用代理和降级实现实例 | `src/main/java/net/ooder/sdk/skill/driver/ProxyFactory.java` |
| **SkillInstaller** | 增强以支持三种安装模式 | `src/main/java/net/ooder/sdk/core/skill/installer/SkillInstaller.java` |

#### 2.1.2 安装模式

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| **DRIVER_ONLY** | 仅安装驱动代理包 | 本地已有技能实现，仅需要代理层 |
| **REMOTE_SKILL** | 安装远程技能 | 技能实现部署在远程服务器 |
| **FULL_INSTALL** | 完整安装（驱动+技能） | 标准安装方式 |

#### 2.1.3 使用示例

```java
import net.ooder.sdk.skill.driver.DriverLoader;
import net.ooder.sdk.skill.driver.InterfaceParser;
import net.ooder.sdk.skill.driver.ProxyFactory;
import net.ooder.sdk.api.skill.SkillInfo;

// 加载驱动代理包
DriverLoader driverLoader = new DriverLoaderImpl();
SkillInfo skillInfo = driverLoader.loadDriver("skill-001");

// 解析接口定义
InterfaceParser parser = new InterfaceParserImpl();
Map<String, Object> interfaceDef = parser.parse(skillInfo.getInterfaceLocation().getPath());

// 创建动态代理
ProxyFactory proxyFactory = new ProxyFactoryImpl();
Object proxy = proxyFactory.createRemoteProxy(interfaceDef, skillInfo.getRemoteEndpoint());

// 创建降级实现
Object fallback = proxyFactory.createFallback(interfaceDef, skillInfo.getFallbackConfig());
```

### 2.2 验证与开发工具链

#### 2.2.1 核心组件

| 组件 | 说明 | 路径 |
|------|------|------|
| **SceneValidator** | 验证框架，支持四层验证 | `src/main/java/net/ooder/sdk/validator/SceneValidator.java` |
| **TestRunner** | 测试运行器，支持三种测试类型 | `src/main/java/net/ooder/sdk/test/TestRunner.java` |
| **CodeGenerator** | 代码生成器，支持生成Driver、Skill接口和Fallback代码 | `src/main/java/net/ooder/sdk/generator/CodeGenerator.java` |
| **SceneCli** | 命令行工具，支持init、generate、validate等命令 | `src/main/java/net/ooder/sdk/cli/SceneCli.java` |

#### 2.2.2 四层验证机制

| 层级 | 说明 | 验证内容 |
|------|------|----------|
| **Level 1** | 基础验证 | 目录结构、配置文件、依赖检查 |
| **Level 2** | 接口验证 | 接口定义、参数校验、返回值校验 |
| **Level 3** | 逻辑验证 | 业务逻辑、状态管理、错误处理 |
| **Level 4** | 集成验证 | 多Agent协作、网络通信、安全验证 |

#### 2.2.3 使用示例

```java
import net.ooder.sdk.validator.SceneValidator;
import net.ooder.sdk.validator.ScenePackage;
import net.ooder.sdk.test.TestRunner;
import net.ooder.sdk.test.TestType;

// 验证场景
SceneValidator validator = new SceneValidatorImpl();
ScenePackage scene = ScenePackage.load("path/to/scene");
ValidationResult result = validator.validateLevel(scene, 4);

// 运行测试
TestRunner testRunner = new TestRunnerImpl();
TestReport report = testRunner.runTests(scene, TestType.UNIT);

// 生成代码
CodeGenerator generator = new CodeGeneratorImpl();
generator.generateDriver("path/to/interface.yaml", "output/driver");
generator.generateSkillInterface("path/to/interface.yaml", "output/skill");
generator.generateFallback("path/to/interface.yaml", "output/fallback");
```

## 3. 升级步骤

### 3.1 依赖更新

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

### 3.2 配置迁移

#### 3.2.1 新增配置项

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

### 3.3 代码适配

#### 3.3.1 使用驱动代理包

```java
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

#### 3.3.2 使用验证工具

```java
import net.ooder.sdk.validator.SceneValidator;
import net.ooder.sdk.validator.ScenePackage;
import net.ooder.sdk.validator.ValidationResult;

// 验证场景
SceneValidator validator = new SceneValidatorImpl();
ScenePackage scene = ScenePackage.load("path/to/scene");
ValidationResult result = validator.validate(scene);

// 查看验证结果
System.out.println("Validation score: " + result.getScore());
System.out.println("Total checks: " + result.getTotalChecks());
System.out.println("Passed: " + result.getPassed());
System.out.println("Failed: " + result.getFailed());
System.out.println("Warnings: " + result.getWarnings());
```

#### 3.3.3 使用命令行工具

```bash
# 初始化场景
java -jar agent-sdk-0.7.3.jar init --name my-scene --path ./my-scene

# 生成代码
java -jar agent-sdk-0.7.3.jar generate --type driver --interface ./my-scene/interface.yaml --output ./my-scene/driver

# 验证场景
java -jar agent-sdk-0.7.3.jar validate --scene ./my-scene --level 4

# 运行测试
java -jar agent-sdk-0.7.3.jar test --scene ./my-scene --type unit
```

## 4. 兼容性说明

### 4.1 API兼容性

| API | 0.7.2 | 0.7.3 | 兼容性 | 说明 |
|-----|-------|-------|--------|------|
| `AgentFactory.createEndAgent()` | ✅ | ✅ | 完全兼容 | 无需修改 |
| `AgentFactory.createRouteAgent()` | ✅ | ✅ | 完全兼容 | 无需修改 |
| `AgentFactory.createMcpAgent()` | ✅ | ✅ | 完全兼容 | 无需修改 |
| `SkillInstaller.install()` | ✅ | ✅ | 扩展兼容 | 新增安装模式参数 |
| `SceneGroupManager.create()` | ✅ | ✅ | 完全兼容 | 无需修改 |

### 4.2 配置兼容性

| 配置项 | 0.7.2 | 0.7.3 | 兼容性 |
|--------|-------|-------|--------|
| `ooder.sdk.network.*` | ✅ | ✅ | 完全兼容 |
| `ooder.sdk.security.*` | ✅ | ✅ | 完全兼容 |
| `ooder.sdk.monitoring.*` | ✅ | ✅ | 完全兼容 |

## 5. 故障排除

### 5.1 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| **驱动加载失败** | 接口定义文件格式错误 | 检查YAML/JSON格式是否正确 |
| **代理创建失败** | 接口方法签名不匹配 | 检查接口定义与实现是否一致 |
| **验证失败** | 场景配置不完整 | 按照验证报告修复问题 |
| **测试失败** | 测试用例配置错误 | 检查测试用例配置文件 |

### 5.2 调试建议

1. **启用调试日志**：
```properties
logging.level.net.ooder.sdk=DEBUG
logging.level.net.ooder.sdk.skill.driver=TRACE
logging.level.net.ooder.sdk.validator=TRACE
```

2. **检查驱动状态**：
```java
DriverLoader driverLoader = new DriverLoaderImpl();
boolean isLoaded = driverLoader.isDriverLoaded("skill-001");
System.out.println("Driver loaded: " + isLoaded);
```

3. **查看验证报告**：
```java
ValidationResult result = validator.validate(scene);
String report = ValidationReportGenerator.generate(result);
System.out.println(report);
```

## 6. 总结

Ooder Agent SDK 0.7.3 版本通过新增驱动代理包支持和验证与开发工具链，为开发者提供了更完整的Agent开发生态系统：

1. **驱动代理包支持**：简化了远程技能的集成和管理
2. **验证与开发工具链**：提高了开发效率和代码质量
3. **三种安装模式**：满足不同场景的安装需求
4. **向后兼容**：保持了与0.7.2版本的兼容性

---

**Ooder Agent SDK 0.7.3** - 构建智能、协作、安全的Agent生态系统！