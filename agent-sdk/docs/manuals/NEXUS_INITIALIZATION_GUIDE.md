# Nexus 初始化引导流程设计

## 📚 必读文档

> ⚠️ **Nexus Enterprise项目必读**: 在集成SDK时遇到Spring注入问题，请参考：
> - [SDK组件注入二次开发指南](../SDK_INJECTION_SECONDARY_DEVELOPMENT_GUIDE.md)
> - [SDK组件注入单元测试指南](../SDK_INJECTION_UNIT_TEST_GUIDE.md)

---

## 一、P2P模式初始化

### 1.1 架构设计

```
┌─────────────────────────────────────────────────────────────────┐
│                    P2P模式初始化流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐       │
│  │  启动Nexus  │────▶│  检查网络   │────▶│ 连接公网市场 │       │
│  └─────────────┘     └─────────────┘     └─────────────┘       │
│                                                │                │
│                                                ▼                │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐       │
│  │  启动完成   │◀────│ 下载并安装  │◀────│ 获取技能列表 │       │
│  └─────────────┘     └─────────────┘     └─────────────┘       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 公网依赖检查

| 组件 | 公网来源 | 本地缓存 | 离线可用 |
|------|---------|---------|---------|
| 场景索引 | GitHub/Gitee | ✅ | ✅ |
| 技能清单 | GitHub/Gitee | ✅ | ✅ |
| 技能包(JAR) | GitHub Release | ✅ | ✅ |
| 场景配置 | 本地配置 | ✅ | ✅ |

### 1.3 纯公网模式限制

```yaml
# 仅公网模式可完成的功能
capabilities:
  - 场景索引下载: ✅ 完全支持
  - 技能发现: ✅ 完全支持
  - 技能下载: ✅ 完全支持
  - 技能安装: ✅ 完全支持
  - 场景激活: ✅ 完全支持

# 需要额外配置的功能
requires_config:
  - LLM集成: 需配置API Key
  - 企业认证: 需配置App ID/Secret
  - 消息推送: 需配置渠道参数
```

---

## 二、企业版初始化流程

### 2.1 初始化引导步骤

```
┌─────────────────────────────────────────────────────────────────┐
│                   企业版初始化引导                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Step 1: 选择场景组                                              │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  ○ Enterprise Nexus Platform (推荐)                     │   │
│  │    - 包含: VFS, 认证, 消息, 工作流, UI生成               │   │
│  │    - 适合: 企业协作场景                                   │   │
│  │                                                         │   │
│  │  ○ Personal Nexus                                       │   │
│  │    - 包含: VFS, 认证, UI生成                             │   │
│  │    - 适合: 个人用户                                       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 2: 选择场景                                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  必选场景:                                               │   │
│  │  ☑ VFS - 虚拟文件系统 (必选)                            │   │
│  │  ☑ Auth - 认证授权 (必选)                                │   │
│  │                                                         │   │
│  │  可选场景:                                               │   │
│  │  ☑ Msg - 消息服务                                       │   │
│  │  ☐ Workflow - 工作流管理                                │   │
│  │  ☐ A2UI - UI生成                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 3: 配置认证提供者                                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  认证方式:                                               │   │
│  │  ○ 本地认证 (默认)                                       │   │
│  │  ○ 飞书认证                                             │   │
│  │    - App ID: [________________]                         │   │
│  │    - App Secret: [________________]                     │   │
│  │  ○ 钉钉认证                                             │   │
│  │    - App Key: [________________]                         │   │
│  │    - App Secret: [________________]                     │   │
│  │  ○ 企业微信认证                                          │   │
│  │    - Corp ID: [________________]                         │   │
│  │    - Agent ID: [________________]                       │   │
│  │    - Secret: [________________]                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 4: 下载技能                                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  正在下载技能...                                         │   │
│  │  [████████████░░░░░░░░░░░░] 50%                         │   │
│  │                                                         │   │
│  │  当前: skill-vfs                                        │   │
│  │  已完成: skill-user-auth                                │   │
│  │  待下载: skill-msg, skill-workflow, skill-a2ui          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 5: 配置LLM (可选)                                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  LLM配置:                                               │   │
│  │  ○ 使用云端LLM                                          │   │
│  │    - Provider: [OpenAI / Claude / DeepSeek]             │   │
│  │    - API Key: [________________]                         │   │
│  │    - Base URL: [________________] (可选)                │   │
│  │                                                         │   │
│  │  ○ 使用本地LLM                                          │   │
│  │    - 模型路径: [________________]                        │   │
│  │                                                         │   │
│  │  ○ 跳过配置 (稍后配置)                                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 6: 启动场景                                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  正在启动场景...                                         │   │
│  │                                                         │   │
│  │  ☑ VFS - 已启动                                         │   │
│  │  ☑ Auth - 已启动                                        │   │
│  │  ☑ Msg - 正在启动...                                    │   │
│  │  ○ Workflow - 等待中                                    │   │
│  │  ○ A2UI - 等待中                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 7: 完成                                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  ✅ 初始化完成!                                          │   │
│  │                                                         │   │
│  │  已安装技能: 5个                                         │   │
│  │  已激活场景: 3个                                         │   │
│  │  耗时: 2分30秒                                          │   │
│  │                                                         │   │
│  │  [进入控制台]  [查看文档]  [开始使用]                    │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 企业版初始化代码示例

```java
// 企业版初始化
NexusInitializer initializer = ooderSDK.getNexusInitializer();

// Step 1: 获取可用场景组
List<SceneGroupInfo> groups = initializer.getAvailableSceneGroups().join();

// Step 2: 创建初始化请求
InitRequest request = new InitRequest();
request.setSceneGroupId("enterprise-nexus");
request.setSelectedScenes(Arrays.asList("vfs", "auth", "msg", "workflow"));

// Step 3: 配置认证提供者
AuthProviderConfig authConfig = new AuthProviderConfig();
authConfig.setType("feishu");
authConfig.setAppId("cli_xxx");
authConfig.setAppSecret("xxx");
request.setAuthProvider(authConfig);

// Step 4: 配置LLM
LLMConfig llmConfig = new LLMConfig();
llmConfig.setProvider("deepseek");
llmConfig.setApiKey("sk-xxx");
request.setLlmConfig(llmConfig);

// Step 5: 设置下载源
request.setDiscoverySource("gitee"); // 国内使用Gitee

// Step 6: 执行初始化
CompletableFuture<InitResult> future = initializer.initialize(request);

// 监控进度
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    InitProgress progress = initializer.getProgress(request.getInitId()).join();
    System.out.println(progress.getPhase() + " - " + progress.getPercentage() + "%");
}, 0, 1, TimeUnit.SECONDS);

// 等待完成
InitResult result = future.join();
if (result.isSuccess()) {
    System.out.println("初始化成功!");
    System.out.println("已安装技能: " + result.getInstalledSkills().size());
    System.out.println("已激活场景: " + result.getActivatedScenes().size());
}
```

---

## 三、个人端初始化流程

### 3.1 初始化引导步骤

```
┌─────────────────────────────────────────────────────────────────┐
│                   个人端初始化引导                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Step 1: 欢迎界面                                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                                                         │   │
│  │     欢迎使用 Nexus 个人版                                │   │
│  │                                                         │   │
│  │     Nexus 是一个智能协作平台，帮助您                     │   │
│  │     高效完成日常工作和开发任务。                          │   │
│  │                                                         │   │
│  │     [开始使用]                                          │   │
│  │                                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 2: 选择场景                                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  选择您需要的功能:                                       │   │
│  │                                                         │   │
│  │  ☑ 文件管理 (必选)                                       │   │
│  │    - 文件存储、管理、共享                                │   │
│  │                                                         │   │
│  │  ☑ 用户认证 (必选)                                       │   │
│  │    - 登录认证、权限管理                                  │   │
│  │                                                         │   │
│  │  ☐ UI生成 (可选)                                        │   │
│  │    - 设计图转代码、组件生成                              │   │
│  │                                                         │   │
│  │  [下一步]                                               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 3: 配置LLM (可选)                                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  配置AI助手 (可选):                                      │   │
│  │                                                         │   │
│  │  ○ 使用云端AI                                           │   │
│  │    Provider: [DeepSeek / OpenAI / Claude]               │   │
│  │    API Key: [________________]                           │   │
│  │                                                         │   │
│  │  ○ 使用本地IDE集成                                       │   │
│  │    检测到: Trae IDE / Cursor / VS Code                  │   │
│  │    [自动配置]                                           │   │
│  │                                                         │   │
│  │  ○ 跳过 (稍后配置)                                       │   │
│  │                                                         │   │
│  │  [跳过] [下一步]                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 4: 下载并安装                                              │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                                                         │   │
│  │     正在初始化...                                        │   │
│  │     [████████████████████] 100%                         │   │
│  │                                                         │   │
│  │     ✅ 下载技能包                                        │   │
│  │     ✅ 安装技能                                          │   │
│  │     ✅ 激活场景                                          │   │
│  │                                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 5: 登录/注册                                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                                                         │   │
│  │     创建账户或登录                                       │   │
│  │                                                         │   │
│  │     用户名: [________________]                           │   │
│  │     密码: [________________]                            │   │
│  │                                                         │   │
│  │     [注册新账户]  [登录]                                 │   │
│  │                                                         │   │
│  │     或使用第三方登录:                                    │   │
│  │     [飞书登录] [钉钉登录] [微信登录]                     │   │
│  │                                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                         │                                       │
│                         ▼                                       │
│  Step 6: 完成                                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                                                         │   │
│  │     🎉 初始化完成!                                       │   │
│  │                                                         │   │
│  │     您的Nexus已准备就绪。                                │   │
│  │                                                         │   │
│  │     [进入主界面]                                        │   │
│  │                                                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 个人端初始化代码示例

```java
// 个人端初始化
NexusInitializer initializer = ooderSDK.getNexusInitializer();

// 创建初始化请求
InitRequest request = new InitRequest();
request.setSceneGroupId("personal-nexus");
request.setSelectedScenes(Arrays.asList("vfs", "auth")); // 基础场景

// 配置本地IDE集成 (可选)
LLMConfig llmConfig = new LLMConfig();
llmConfig.setLocalMode(true);
llmConfig.setProvider("trae-ide"); // 自动检测本地IDE
request.setLlmConfig(llmConfig);

// 设置下载源 (国内用户)
request.setDiscoverySource("gitee");

// 执行初始化
InitResult result = initializer.initialize(request).join();

if (result.isSuccess()) {
    // 进入登录界面
    showLoginScreen();
}
```

---

## 四、初始化配置文件

### 4.1 企业版配置示例

```yaml
# enterprise-init.yaml
apiVersion: nexus.ooder.net/v1
kind: NexusInitConfig

metadata:
  name: enterprise-nexus-init
  version: "1.0.0"

spec:
  sceneGroup: enterprise-nexus
  
  scenes:
    - sceneId: vfs
      enabled: true
    - sceneId: auth
      enabled: true
    - sceneId: msg
      enabled: true
    - sceneId: workflow
      enabled: false
    - sceneId: a2ui
      enabled: false

  discovery:
    source: gitee
    fallback: github
    cacheEnabled: true

  auth:
    provider: feishu
    appId: ${FEISHU_APP_ID}
    appSecret: ${FEISHU_APP_SECRET}

  llm:
    provider: deepseek
    apiKey: ${DEEPSEEK_API_KEY}
    model: deepseek-chat

  storage:
    rootPath: ${user.home}/.ooder/nexus
    vfsPath: ${user.home}/.ooder/nexus/vfs
```

### 4.2 个人端配置示例

```yaml
# personal-init.yaml
apiVersion: nexus.ooder.net/v1
kind: NexusInitConfig

metadata:
  name: personal-nexus-init
  version: "1.0.0"

spec:
  sceneGroup: personal-nexus
  
  scenes:
    - sceneId: vfs
      enabled: true
    - sceneId: auth
      enabled: true
    - sceneId: a2ui
      enabled: true

  discovery:
    source: gitee
    cacheEnabled: true

  llm:
    localMode: true
    provider: trae-ide

  storage:
    rootPath: ${user.home}/.ooder/nexus
```

---

## 五、初始化流程对比

| 步骤 | P2P模式 | 企业版 | 个人端 |
|------|---------|--------|--------|
| 场景组选择 | 自动 | 手动选择 | 自动(个人版) |
| 场景选择 | 默认必选 | 可选择 | 可选择 |
| 认证配置 | 无 | 必须配置 | 可选 |
| LLM配置 | 可选 | 推荐 | 可选 |
| 技能下载 | 自动 | 自动 | 自动 |
| 登录 | 可选 | 必须 | 必须 |

---

## 六、API接口

### 6.1 初始化接口

```java
public interface NexusInitializer {
    // 获取可用场景组
    CompletableFuture<List<SceneGroupInfo>> getAvailableSceneGroups();
    
    // 获取场景组详情
    CompletableFuture<SceneGroupInfo> getSceneGroupInfo(String sceneGroupId);
    
    // 开始初始化
    CompletableFuture<InitResult> initialize(InitRequest request);
    
    // 获取进度
    CompletableFuture<InitProgress> getProgress(String initId);
    
    // 取消初始化
    CompletableFuture<Void> cancel(String initId);
    
    // 验证配置
    CompletableFuture<ConfigValidationResult> validateConfig(Map<String, Object> config);
}
```

### 6.2 初始化阶段

```java
public enum InitPhase {
    PREPARING,              // 准备中
    DOWNLOADING_SCENE_INDEX,// 下载场景索引
    SELECTING_SCENES,       // 选择场景
    CONFIGURING_AUTH,       // 配置认证
    DOWNLOADING_SKILLS,     // 下载技能
    INSTALLING_SKILLS,      // 安装技能
    CONFIGURING_LLM,        // 配置LLM
    ACTIVATING_SCENES,      // 激活场景
    COMPLETED,              // 完成
    FAILED                  // 失败
}
```
