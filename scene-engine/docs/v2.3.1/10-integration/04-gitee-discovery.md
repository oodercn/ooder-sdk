# Gitee 技能发现器

> **版本**: v2.3.1  
> **最后更新**: 2026-03-20  
> **适用范围**: PUBLIC

## 1. 概述

Gitee 技能发现器是 Scene Engine 提供的一种远程技能发现机制，允许用户从 Gitee 仓库发现和安装公开或私有技能包。

### 1.1 核心特性

| 特性 | 说明 |
|------|------|
| 自动识别 | 自动识别 Gitee 仓库地址格式 |
| 智能缓存 | 本地 JSON 缓存，避免频繁 API 调用 |
| 限流保护 | 内置访问频率控制，避免触发平台限制 |
| 多仓库支持 | 支持配置多个 Gitee 仓库地址 |
| 私有仓库 | 支持通过 Token 访问私有仓库 |

### 1.2 适用场景

- 从 Gitee 公开仓库发现社区技能
- 企业内部 Gitee 私有仓库技能共享
- 技能包版本管理和发布
- CI/CD 集成自动发现

## 2. 快速开始

### 2.1 基本配置

在 `application.yaml` 中配置 Gitee 发现器：

```yaml
ooder:
  discovery:
    gitee:
      enabled: true
      default-owner: your-org
      default-repo: skills
      default-branch: main
      skills-path: skills
      token: ${GITEE_TOKEN:}
      cache-ttl-ms: 3600000  # 1小时缓存
```

### 2.2 发现技能

```java
@Autowired
private UnifiedDiscoveryService discoveryService;

public void discoverFromGitee() {
    String repoUrl = "https://gitee.com/ooder/skills";
    
    CompletableFuture<List<SkillPackage>> future = 
        discoveryService.discoverSkills(repoUrl);
    
    List<SkillPackage> skills = future.get(60, TimeUnit.SECONDS);
    
    for (SkillPackage skill : skills) {
        System.out.println("发现技能: " + skill.getName() + 
                          " v" + skill.getVersion());
    }
}
```

### 2.3 支持的地址格式

| 格式 | 示例 |
|------|------|
| HTTPS URL | `https://gitee.com/owner/repo` |
| 协议格式 | `gitee://owner/repo` |
| 带分支 | `gitee://owner/repo?branch=develop` |
| 带路径 | `gitee://owner/repo?path=skills` |

## 3. skill-index.yaml 配置

### 3.1 文件位置

在 Gitee 仓库中，`skill-index.yaml` 应放置在技能目录下：

```
your-repo/
├── skills/
│   ├── skill-index.yaml      # 技能索引文件
│   ├── skill-recruitment/     # 招聘技能
│   │   ├── SKILL.md
│   │   └── ...
│   └── skill-meeting/         # 会议技能
│       ├── SKILL.md
│       └── ...
└── README.md
```

### 3.2 索引文件结构

#### 格式1：直接 skills 列表（旧格式）

```yaml
skills:
  - id: skill-network
    name: 网络管理服务
    category: sys
    version: "2.3.1"
  - id: skill-security
    name: 安全管理服务
    category: sys
    version: "2.3.1"
```

#### 格式2：includes 引用（v2.3.1 标准格式）✅ 推荐

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: "2.3.1"

spec:
  includes:
    - categories.yaml
    - scene-drivers.yaml
    - skills/*.yaml
    - scenes/*.yaml
  
  statistics:
    totalSkills: 63
    totalScenes: 50
```

**includes 格式优势**：
- 模块化：每个技能独立文件，易于维护
- 通配符支持：`skills/*.yaml` 自动匹配目录下所有技能
- 可扩展：支持引用其他配置文件

**支持的通配符模式**：

| 模式 | 说明 | 示例 |
|------|------|------|
| `*` | 匹配所有文件 | `skills/*` |
| `*.yaml` | 匹配指定扩展名 | `skills/*.yaml` |
| `skill-*` | 匹配前缀 | `skills/skill-*.yaml` |

#### 完整技能定义示例

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillIndex

metadata:
  id: skill-example-scene
  name: 示例场景技能
  version: 1.0.0
  description: 技能描述
  author: your-name
  license: Apache-2.0
  homepage: https://gitee.com/your-org/skills
  repository: https://gitee.com/your-org/skills.git

spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  businessCategory: PRODUCTIVITY
  category: TASK_MANAGEMENT
  capabilityCategory: BASIC_SERVICE

  tags:
    - 标签1
    - 标签2

  capabilityAddresses:
    required:
      - name: llm-service
        address: cap://llm/default
        description: LLM服务

  roles:
    - name: admin
      displayName: 管理员
      minCount: 1
      maxCount: 1
      permissions: [MANAGE, CONFIGURE, VIEW]

  participants:
    leader:
      required: true
      defaultToCurrentUser: true
      permissions: [ACTIVATE, CONFIGURE, DELETE]

  driverConditions:
    supportedTypes: [MANUAL, SCHEDULE]
    defaultType: MANUAL
```

### 3.3 必需字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `metadata.id` | string | 技能唯一标识，格式：`skill-{name}` |
| `metadata.name` | string | 技能显示名称 |
| `metadata.version` | string | 语义化版本号，格式：`x.y.z` |
| `metadata.description` | string | 技能描述 |
| `spec.skillForm` | enum | 技能形态：SCENE/PROVIDER/DRIVER/INTERNAL |
| `spec.visibility` | enum | 可见性：public/developer/internal |
| `spec.businessCategory` | enum | 业务分类 |
| `spec.category` | enum | SE标准技术分类 |
| `spec.capabilityCategory` | enum | 能力地址分类 |
| `spec.capabilityAddresses` | object | 能力地址配置 |

### 3.4 includes 格式解析流程

```
┌─────────────────────────────────────────────────────────────┐
│  获取 skill-index.yaml                                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  检测格式类型                                                 │
│  - spec.includes 存在？ → includes 格式                      │
│  - skills 存在？ → 直接列表格式                               │
└─────────────────────────────────────────────────────────────┘
                              │
           ┌──────────────────┴──────────────────┐
           │ includes 格式                        │ 直接列表格式
           ▼                                      ▼
┌─────────────────────┐              ┌─────────────────────┐
│  解析 includes 列表  │              │  直接解析 skills    │
└─────────────────────┘              └─────────────────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────────┐
│  遍历每个 include 条目                                       │
│  ├── 通配符模式 (skills/*.yaml)                              │
│  │   ├── 调用 API 获取目录文件列表                           │
│  │   ├── 匹配文件名模式                                      │
│  │   └── 逐个获取并解析 YAML 文件                            │
│  └── 具体文件 (categories.yaml)                              │
│      └── 直接获取并解析文件                                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  合并所有技能定义                                            │
│  返回完整的 SkillPackage 列表                                │
└─────────────────────────────────────────────────────────────┘
```

### 3.5 推荐的仓库目录结构

```
your-skills-repo/
├── skill-index.yaml           # 主索引文件（includes 格式）
├── categories.yaml            # 分类定义
├── scene-drivers.yaml         # 场景驱动配置
├── skills/                    # 技能定义目录
│   ├── skill-network.yaml
│   ├── skill-security.yaml
│   ├── skill-database.yaml
│   └── ...
├── scenes/                    # 场景定义目录
│   ├── scene-llm-chat.yaml
│   ├── scene-recruitment.yaml
│   └── ...
└── README.md
```

## 4. 多仓库配置

### 4.1 配置多个仓库

```java
@Autowired
private MultiRepoConfigManager repoConfigManager;

public void configureMultipleRepos() {
    RepositoryConfig config1 = new RepositoryConfig();
    config1.setName("官方技能库");
    config1.setSource("gitee");
    config1.setOwner("ooder");
    config1.setSkillsRepo("skills");
    config1.setSkillsPath("skills");
    config1.setDefault(true);
    
    repoConfigManager.addRepositoryConfig(null, config1);
    
    RepositoryConfig config2 = new RepositoryConfig();
    config2.setName("企业技能库");
    config2.setSource("gitee");
    config2.setOwner("my-company");
    config2.setSkillsRepo("internal-skills");
    config2.setToken("your-private-token");
    
    repoConfigManager.addRepositoryConfig("user-123", config2);
}
```

### 4.2 仓库配置属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `name` | string | 配置名称 |
| `source` | string | 来源：github/gitee |
| `owner` | string | 仓库所有者 |
| `skillsRepo` | string | 技能仓库名称 |
| `skillsPath` | string | 技能目录路径，默认 `skills` |
| `singleRepoMode` | boolean | 是否单仓库模式，默认 true |
| `token` | string | 访问令牌（私有仓库必需） |
| `apiBaseUrl` | string | API 基础地址（私有部署） |
| `webBaseUrl` | string | Web 基础地址 |
| `isDefault` | boolean | 是否默认仓库 |
| `cacheTtlMs` | long | 缓存有效期，默认 1 小时 |

## 5. 缓存机制

### 5.1 缓存策略

```
┌─────────────────────────────────────────────────────────────┐
│                      发现请求                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  检查本地缓存                                                │
│  - 缓存存在？                                                │
│  - 缓存未过期？                                              │
└─────────────────────────────────────────────────────────────┘
           │                                    │
           │ 是                                  │ 否
           ▼                                    ▼
┌─────────────────────┐              ┌─────────────────────┐
│  返回缓存结果        │              │  调用 Gitee API      │
└─────────────────────┘              └─────────────────────┘
                                                  │
                                                  ▼
                                     ┌─────────────────────┐
                                     │  更新本地缓存        │
                                     └─────────────────────┘
                                                  │
                                                  ▼
                                     ┌─────────────────────┐
                                     │  返回结果           │
                                     └─────────────────────┘
```

### 5.2 缓存配置

```java
UnifiedDiscoveryService.CacheConfig cacheConfig = 
    new UnifiedDiscoveryService.CacheConfig();
cacheConfig.setCacheTtlMs(7200000);  // 2小时
cacheConfig.setCacheDir("./.ooder/cache/discovery");
cacheConfig.setMaxCacheEntries(200);
cacheConfig.setEnableMemoryCache(true);
cacheConfig.setEnableFileCache(true);

discoveryService.setCacheConfig(cacheConfig);
```

### 5.3 缓存状态查询

```java
CacheStatus status = discoveryService.getCacheStatus(repoUrl);

System.out.println("缓存状态: " + status.isCached());
System.out.println("缓存时间: " + new Date(status.getCacheTime()));
System.out.println("过期时间: " + new Date(status.getExpireTime()));
System.out.println("缓存大小: " + status.getSize() + " bytes");
```

### 5.4 强制刷新缓存

```java
discoveryService.refreshCache(repoUrl).get();
```

## 6. API 限流

### 6.1 Gitee API 限制

| 类型 | 限制 |
|------|------|
| 未认证请求 | 60 次/小时 |
| 认证请求 | 5000 次/小时 |

### 6.2 内置限流保护

发现器内置限流机制，自动控制 API 调用频率：

```java
// 内部实现：限流器
private final RateLimiter rateLimiter = RateLimiter.create(1.0); // 1次/秒

public CompletableFuture<List<SkillPackage>> discoverSkills(String url) {
    return CompletableFuture.supplyAsync(() -> {
        rateLimiter.acquire();  // 获取许可
        return doDiscover(url);
    });
}
```

## 7. 认证配置

### 7.1 获取 Gitee Token

1. 登录 Gitee
2. 进入 设置 -> 私人令牌
3. 生成新令牌，选择权限：
   - `projects` - 访问仓库
   - `pull_requests` - 读取 PR（可选）

### 7.2 配置 Token

**方式一：环境变量**

```bash
export GITEE_TOKEN=your_token_here
```

**方式二：配置文件**

```yaml
ooder:
  discovery:
    gitee:
      token: your_token_here
```

**方式三：代码注入**

```java
@Autowired
private MultiRepoConfigManager repoConfigManager;

public void setToken() {
    RepositoryConfig config = repoConfigManager
        .getDefaultRepository("user-123", "gitee");
    config.setToken("your_token_here");
    repoConfigManager.updateRepositoryConfig("user-123", config);
}
```

## 8. VFS 存储路径

### 8.1 路径规划

| 类型 | 路径 |
|------|------|
| 本地缓存 | `/discovery/cache/gitee/{skillId}/{version}/` |
| 用户存储 | `/discovery/user/{userId}/gitee/{skillId}/{version}/` |
| 安装包 | `/discovery/packages/{skillId}/{version}/` |
| 元数据 | `/discovery/metadata/skills/{skillId}` |
| 连接信息 | `/discovery/user/{userId}/connect/gitee/` |

### 8.2 路径策略类

```java
String cachePath = VfsPathStrategy.getCachePath("gitee", "skill-123", "1.0.0");
String userPath = VfsPathStrategy.getUserPath("user-123", "gitee", "skill-123", "1.0.0");
String manifestPath = VfsPathStrategy.getSkillManifestPath("gitee", "skill-123", "1.0.0");
```

## 9. 错误处理

### 9.1 常见错误码

| 错误码 | 说明 | 处理建议 |
|--------|------|---------|
| 4001 | 发现范围不支持 | 检查 scope 设置 |
| 4003 | 发现超时 | 增加超时时间或重试 |
| 4006 | 认证失败 | 检查 Token 有效性 |
| 4007 | 提供者初始化失败 | 检查配置参数 |
| 4008 | 网络不可达 | 检查网络连接 |
| 403 | API 访问被拒绝 | 检查 Token 权限 |
| 404 | 仓库不存在 | 检查仓库地址 |
| 429 | 请求频率超限 | 等待后重试，启用缓存 |

### 9.2 异常处理示例

```java
try {
    List<SkillPackage> skills = discoveryService
        .discoverSkills(repoUrl)
        .get(60, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    log.error("发现超时: {}", repoUrl);
} catch (ExecutionException e) {
    if (e.getCause() instanceof AuthenticationException) {
        log.error("认证失败，请检查 Token");
    } else if (e.getCause() instanceof RateLimitException) {
        log.error("API 限流，请稍后重试");
    } else {
        log.error("发现失败: {}", e.getMessage());
    }
}
```

## 10. 最佳实践

### 10.1 仓库组织建议

```
recommended-structure/
├── skills/
│   ├── skill-index.yaml          # 总索引
│   ├── skill-hr-recruitment/     # HR招聘技能
│   │   ├── SKILL.md
│   │   ├── skill-manifest.yaml
│   │   └── src/
│   ├── skill-meeting-assistant/  # 会议助手技能
│   │   ├── SKILL.md
│   │   └── ...
│   └── templates/                # 技能模板
│       └── skill-template.yaml
├── docs/
│   └── README.md
└── releases/                     # 发布包（可选）
```

### 10.2 版本管理建议

- 使用语义化版本号：`major.minor.patch`
- 为每个版本创建 Git Tag
- 在 `skill-index.yaml` 中更新版本号
- 使用 Gitee Release 发布稳定版本

### 10.3 安全建议

- 私有仓库必须使用 Token
- Token 应通过环境变量注入，不要硬编码
- 定期轮换 Token
- 使用最小权限原则配置 Token

## 11. 与 GitHub 发现器的差异

| 特性 | Gitee | GitHub |
|------|-------|--------|
| API 地址 | `gitee.com/api/v5` | `api.github.com` |
| 认证方式 | Token | Token / OAuth |
| 速率限制 | 5000次/小时（认证） | 5000次/小时（认证） |
| 私有部署 | 支持 Gitee 企业版 | 支持 GitHub Enterprise |
| 国内访问 | 更快 | 可能较慢 |

## 12. 故障排查

### 12.1 发现返回空列表

**可能原因**：
1. 仓库地址错误
2. `skill-index.yaml` 文件不存在或格式错误
3. Token 无权限访问私有仓库
4. API 限流

**排查步骤**：
```bash
# 1. 检查仓库访问
curl -H "Authorization: token YOUR_TOKEN" \
  https://gitee.com/api/v5/repos/owner/repo

# 2. 检查索引文件
curl -H "Authorization: token YOUR_TOKEN" \
  https://gitee.com/api/v5/repos/owner/repo/contents/skills/skill-index.yaml

# 3. 查看日志
tail -f logs/scene-engine.log | grep -i discovery
```

### 12.2 缓存问题

```java
// 清除所有缓存
discoveryService.clearAllCache();

// 强制刷新指定仓库
discoveryService.refreshCache(repoUrl).get();
```

## 13. 相关文档

- [技能发现协议 v2.3](../protocol/v2.3/skill-discovery-protocol.md)
- [技能集成指南](./02-skill-integration.md)
- [配置参考](./01-configuration.md)
- [skill-index.yaml 示例](../../examples/skill-index.yaml)

---

**Ooder Team | Version 2.3.1 | 2026-03-20**
