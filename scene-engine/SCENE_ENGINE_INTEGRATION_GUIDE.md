# Scene Engine v2.3 集成指南

**版本**: 2.3  
**日期**: 2026-03-03  
**作者**: ENGINE团队 + SKILL团队  

---

## 1. 快速开始

### 1.1 引入依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>
```

### 1.2 开箱即用

Scene Engine v2.3 支持**零配置启动**，引入依赖后自动完成：

- ✅ JDSConfig 默认配置（JDSHome=./JDSHome, ConfigName=scene）
- ✅ 自动创建目录结构
- ✅ 自动生成默认 engine_config.xml
- ✅ UnifiedSceneService 自动注册
- ✅ JDSServer 自动注册 Scene 引擎

### 1.3 验证启动

```java
@SpringBootApplication
public class MyApplication {
    
    @Autowired
    private UnifiedSceneService unifiedSceneService;
    
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
    
    @PostConstruct
    public void init() {
        // 验证服务可用
        System.out.println("UnifiedSceneService: " + unifiedSceneService);
    }
}
```

**启动日志示例**：
```
[SceneEngineAutoConfiguration] JDSConfig initialized successfully
[SceneEngineAutoConfiguration] JDSHome: ./JDSHome
[SceneEngineAutoConfiguration] ConfigName: scene
[SceneEngineAutoConfiguration] Created directory: ./JDSHome/application/scene/config
[SceneEngineAutoConfiguration] Created default engine_config.xml: ./JDSHome/application/scene/config/engine_config.xml
Scene engine successfully registered to JDSServer
```

---

## 2. 核心架构

### 2.1 架构分层

```
┌─────────────────────────────────────────────────────────┐
│                    应用层 (Your Application)              │
│  @Autowired UnifiedSceneService  ← 推荐                  │
│  @Autowired SkillPackageManager                          │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                   scene-engine (v2.3)                    │
│  UnifiedSceneService                                     │
│  ├─ discoverSkills(owner, repo, options)                 │
│  ├─ installSkill(skillId)                                │
│  └─ getSkillDetail(skillId)                              │
│                                                          │
│  SecureSceneEngineProxy (通过 JDSServer 获取)            │
│  ├─ login(username, password)                            │
│  ├─ findSkill(skillId)                                   │
│  └─ installSkill(skillId)                                │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    ooder-sdk (v2.3)                      │
│  GiteeDiscoverer / GitHubDiscoverer                      │
│  SkillPackageManager                                     │
│  JDSServer                                               │
└─────────────────────────────────────────────────────────┘
```

### 2.2 核心组件

| 组件 | 说明 | 获取方式 |
|------|------|----------|
| **UnifiedSceneService** | 统一场景服务 | `@Autowired` |
| **SecureSceneEngineProxy** | 安全代理 | `JDSServer.getJDSClientService()` |
| **SceneEngine** | 核心引擎 | 通过代理访问 |
| **SceneClient** | 场景客户端 | `proxy.login()` |
| **AdminClient** | 管理客户端 | `proxy.adminLogin()` |

---

## 3. 使用指南

### 3.1 推荐方式：UnifiedSceneService

```java
@Service
public class DiscoveryService {
    
    @Autowired
    private UnifiedSceneService unifiedSceneService;
    
    /**
     * 发现 Gitee 仓库中的 Skills
     */
    public List<RichSkill> discoverGiteeSkills(String owner, String repo) {
        DiscoveryOptions options = new DiscoveryOptions();
        options.setSource("gitee");
        
        DiscoveryResult result = unifiedSceneService
            .discoverSkills(owner, repo, options)
            .join(); // 异步转同步
            
        return result.getSkills();
    }
    
    /**
     * 安装 Skill
     */
    public String installSkill(String skillId) {
        return unifiedSceneService.installSkill(skillId);
    }
    
    /**
     * 获取安装进度
     */
    public int getInstallProgress(String sessionId) {
        return unifiedSceneService.getInstallProgress(sessionId);
    }
}
```

### 3.2 高级方式：SecureSceneEngineProxy

```java
@Service
public class AdvancedSceneService {
    
    /**
     * 使用 JDSServer 获取代理（强制安全方式）
     */
    public void useSceneEngine() {
        // 1. 登录 JDSServer
        JDSSessionHandle sessionHandle = JDSServer.getInstance()
            .connect(clientService);
        
        // 2. 获取 Scene 引擎代理
        ConfigCode sceneCode = ConfigCode.fromType("scene");
        SecureSceneEngineProxy proxy = (SecureSceneEngineProxy) JDSServer.getInstance()
            .getJDSClientService(sessionHandle, sceneCode);
        
        // 3. 登录 SceneEngine
        SceneClient client = proxy.login("username", "password");
        
        // 4. 使用客户端
        List<RichSkill> skills = client.searchSkills(new SkillQuery());
        
        // 5. 安装 Skill
        SkillInstallResult result = client.installSkill("skill-id");
    }
}
```

### 3.3 场景引擎生命周期

```java
// 启动引擎
SceneEngine sceneEngine = SceneEngineHolder.getInstance().getSceneEngine();
sceneEngine.start();

// 获取状态
EngineStatus status = sceneEngine.getStatus();

// 停止引擎
sceneEngine.stop();
```

---

## 4. 配置说明

### 4.1 默认配置

Scene Engine v2.3 提供以下默认值：

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `JDSHome` | `./JDSHome` | JDS 服务器主目录 |
| `ConfigName` | `scene` | 当前系统配置代码 |
| `server.host` | `localhost` | 服务器主机 |
| `server.port` | `10523` | 服务器端口 |

### 4.2 自定义配置

**方式1：Spring Boot application.yml**
```yaml
# 可选配置，有默认值
ooder:
  jds:
    server:
      home: /custom/JDSHome
    config:
      name: myscene
```

**方式2：JVM 参数**
```bash
java -DJDSHome=/custom/JDSHome -DConfigName=myscene -jar app.jar
```

**方式3：环境变量**
```bash
export JDSHome=/custom/JDSHome
export ConfigName=myscene
```

### 4.3 目录结构

Scene Engine 自动创建以下目录结构：

```
./JDSHome/                          # JDSHome 目录
├── application/
│   └── scene/                      # 场景引擎应用（ConfigName）
│       ├── config/
│       │   └── engine_config.xml   # 引擎配置文件
│       ├── lib/                    # 库文件
│       ├── classes/                # 类文件
│       ├── data/                   # 数据
│       └── temp/                   # 临时文件
└── config/
    └── engine_config.xml           # 公共配置
```

---

## 5. 核心概念

### 5.1 SceneAgent

场景是特殊的智能体，具备 Agent 属性：

```java
public class SceneAgent {
    private String agentId;              // scene-{sceneName}-{uuid}
    private String sceneId;              // 场景ID
    private AgentType type;              // PRIMARY | BACKUP | COLLABORATIVE
    private AgentStatus status;          // INITIALIZING | ACTIVE | SUSPENDED | STOPPED
    
    private SceneConfig config;          // 场景配置
    private List<CapBinding> capBindings;// CAP 绑定列表
    private Map<String, Skill> skills;   // 已挂载的 Skills
}
```

### 5.2 Agent 类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| PRIMARY | 主 Agent | 场景的主要执行者 |
| BACKUP | 备份 Agent | 高可用场景，故障时接管 |
| COLLABORATIVE | 协作 Agent | 多 Agent 协作场景 |

### 5.3 Skill 挂载机制

```yaml
# skill-manifest.yaml
spec:
  implements:
    - capId: "40"
      version: "1.0"
      connector:
        type: http
        config:
          baseUrl: https://msg.ooder.net
          timeout: 30000
```

**调用类型**：
- `http` - HTTP/HTTPS 远程调用
- `local-jar` - 本地 JAR 接口调用
- `grpc` - gRPC 远程调用
- `websocket` - WebSocket 双向通信
- `udp` - UDP 数据报

---

## 6. 开发规范

### 6.1 依赖管理

**推荐**：应用层只依赖 `scene-engine`
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>
```

**不推荐**：直接依赖 SDK 底层
```xml
<!-- 不推荐 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-sdk</artifactId>
    <version>2.3</version>
</dependency>
```

### 6.2 注解使用规范

| 层级 | 注解使用 | 说明 |
|------|----------|------|
| **配置层** | ✅ 允许 | SceneEngineAutoConfiguration, JDSServerSceneConfiguration |
| **核心引擎层** | ❌ 禁止 | SceneEngineImpl, CapRouter 等核心类 |
| **Service层** | ⚠️ 建议避免 | 通过配置层管理生命周期 |

### 6.3 安全规范

**强制使用 JDSServer 方式获取代理**：
```java
// ✅ 正确
SecureSceneEngineProxy proxy = (SecureSceneEngineProxy) JDSServer.getInstance()
    .getJDSClientService(sessionHandle, sceneCode);

// ❌ 错误
SceneEngine sceneEngine = new SceneEngineImpl(); // 禁止直接创建
```

---

## 7. 故障排查

### 7.1 常见问题

**问题1：NullPointerException at JDSConfig.currServerHome()**

**原因**：JDSConfig 未初始化

**解决**：
- 确保引入 scene-engine 依赖
- 检查启动日志是否有 `[SceneEngineAutoConfiguration] JDSConfig initialized successfully`

**问题2：JDSServerSceneConfiguration 加载顺序错误**

**原因**：Spring Bean 加载顺序问题

**解决**：
- 已修复：SceneEngineAutoConfiguration 先于 JDSServerSceneConfiguration 初始化
- 升级至 scene-engine 2.3 最新版本

**问题3：无法发现 Skills**

**原因**：Discoverer 未注册

**解决**：
```java
// 注册发现器
discoveryCoordinator.registerDiscoverer("gitee", new GiteeDiscoverer());
discoveryCoordinator.registerDiscoverer("github", new GitHubDiscoverer());
```

### 7.2 调试技巧

**启用调试日志**：
```yaml
# application.yml
logging:
  level:
    net.ooder.scene: DEBUG
```

**检查 JDSConfig**：
```java
System.out.println("JDSHome: " + System.getProperty("JDSHome"));
System.out.println("ConfigName: " + System.getProperty("ConfigName"));
```

---

## 8. 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 2.3 | 2026-03-03 | 开箱即用，自动配置 JDSConfig，移除核心类 Spring 注解 |
| 2.2 | 2026-02-28 | 初始版本 |

---

## 9. 相关文档

- [SDK 注入二次开发指南](./SDK_INJECTION_SECONDARY_DEVELOPMENT_GUIDE.md)
- [场景引擎规范](./SCENE-ENGINE-SPEC.md)
- [JDS 配置解决方案](./JDS_CONFIG_SOLUTION.md)
- [集成测试反馈](./INTEGRATION_TEST_FEEDBACK.md)

---

**文档版本**: v1.0  
**最后更新**: 2026-03-03  
**维护团队**: ENGINE团队 + SKILL团队
