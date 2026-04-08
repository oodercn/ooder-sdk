# SE SDK 3.0.1 发现服务问题分析与解决方案

**文档路径**: `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_SDK_3.0.1_DISCOVERY_ISSUES_RESPONSE.md`

**创建日期**: 2026-04-01

**响应团队**: SE 团队

---

## 一、问题分析

### 1.1 问题一：GitRepositoryDiscovererAdapter 返回 0 个技能

**根本原因**：`GitRepositoryDiscovererAdapter` 是**占位实现**，没有真正调用 Gitee/GitHub API！

**代码证据**：

```java
// e:\github\ooder-sdk\agent-sdk\skills-framework\src\main\java\net\ooder\skills\core\discovery\GitRepositoryDiscovererAdapter.java
// 第 40-46 行

@Override
public CompletableFuture<List<SkillPackage>> discover() {
    return CompletableFuture.supplyAsync(() -> {
        List<SkillPackage> packages = new ArrayList<SkillPackage>();  // 创建空列表
        log.info("Discovering skills from Git repository: {}/{}", defaultOwner, defaultRepo);
        return packages;  // 直接返回空列表！没有实际 API 调用！
    });
}
```

**问题总结**：

| 方法 | 实现状态 | 问题 |
|------|----------|------|
| `discover()` | ❌ 占位实现 | 返回空列表，无 API 调用 |
| `discover(String skillId)` | ⚠️ 模拟实现 | 返回模拟数据，无实际查询 |
| `discoverByScene()` | ❌ 占位实现 | 返回空列表 |
| `search()` | ❌ 占位实现 | 返回空列表 |

---

### 1.2 问题二：DiscoveryCoordinator 未注入到 Skill 模块

**根本原因**：Skill 模块使用独立的 ClassLoader，无法访问主项目的 Spring Bean。

**架构分析**：

```
┌─────────────────────────────────────────────────────────────────┐
│                    主项目 ApplicationContext                      │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  DiscoveryCoordinator Bean                                   │ │
│  │  UnifiedDiscoveryService Bean                                │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ ClassLoader 隔离
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Skill 模块 ClassLoader                        │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  @Autowired DiscoveryCoordinator → null                      │ │
│  │  (无法访问主项目的 Bean)                                       │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

### 1.3 问题三：DiscoveryOrchestrator 只支持本地发现

**根本原因**：这是**设计决策**，不是 Bug。

**设计意图**：

| 层级 | 类 | 职责 | 支持的发现方式 |
|------|-----|------|----------------|
| skill-common | `DiscoveryOrchestrator` | 基础框架 | 仅 LOCAL（无需配置） |
| scene-engine | `DiscoveryCoordinator` | 完整实现 | LOCAL, GITEE, GITHUB, P2P 等 |

**原因**：
- `skill-common` 是基础模块，不应该依赖远程服务配置
- 远程发现器需要 Token、Owner、Repo 等配置，由应用层决定
- `scene-engine` 的 `DiscoveryCoordinator` 负责自动注册所有配置好的发现器

---

## 二、解决方案

### 2.1 解决问题一：实现 GitRepositoryDiscovererAdapter

**方案 A：在 skills-framework 中完整实现**（推荐）

```java
// 完整实现 GitRepositoryDiscovererAdapter
@Override
public CompletableFuture<List<SkillPackage>> discover() {
    return CompletableFuture.supplyAsync(() -> {
        List<SkillPackage> packages = new ArrayList<>();
        
        try {
            // 1. 获取仓库目录树
            String apiUrl = String.format(
                "https://gitee.com/api/v5/repos/%s/%s/git/trees/%s?recursive=1",
                defaultOwner, defaultRepo, defaultBranch
            );
            
            // 2. 调用 API
            String response = httpClient.get(apiUrl, Map.of(
                "Authorization", "token " + giteeToken
            ));
            
            // 3. 解析目录树，查找 skill.yaml 文件
            JsonNode tree = objectMapper.readTree(response).get("tree");
            for (JsonNode node : tree) {
                String path = node.get("path").asText();
                if (path.endsWith("skill.yaml") || path.endsWith("skill.yml")) {
                    // 4. 获取 skill.yaml 内容
                    SkillPackage pkg = loadSkillFromGit(path);
                    if (pkg != null) {
                        packages.add(pkg);
                    }
                }
            }
            
            log.info("Discovered {} skills from {}/{}", 
                packages.size(), defaultOwner, defaultRepo);
                
        } catch (Exception e) {
            log.error("Failed to discover skills from Git repository", e);
        }
        
        return packages;
    });
}
```

**方案 B：使用 scene-engine 的 GiteeSkillDiscovererAdapter**（临时方案）

os 工程可以直接使用 scene-engine 提供的 `GiteeSkillDiscovererAdapter`：

```java
// os 工程中使用
@Autowired
private DiscoveryCoordinator discoveryCoordinator;

public List<DiscoveryService.SkillInfo> discoverFromGitee() {
    DiscoveryRequest request = new DiscoveryRequest();
    request.setSource("gitee");
    
    DiscoveryResult result = discoveryCoordinator.discover(request).join();
    return result.getSkills();
}
```

---

### 2.2 解决问题二：Skill 模块访问主项目服务

**方案 A：通过 SkillContext 获取服务**（推荐）

```java
// 在 Skill 模块中
public class DiscoveryController {
    
    public Object discover() {
        // 通过 SkillContext 获取主项目服务
        DiscoveryCoordinator coordinator = SkillContext.getService(DiscoveryCoordinator.class);
        if (coordinator == null) {
            throw new IllegalStateException("DiscoveryCoordinator not available");
        }
        
        DiscoveryResult result = coordinator.discover("gitee").join();
        return result;
    }
}
```

**方案 B：通过静态工具类访问**

```java
// 主项目中注册
@Service
public class ServiceRegistry {
    
    @PostConstruct
    public void init() {
        ServiceHolder.register(DiscoveryCoordinator.class, discoveryCoordinator);
    }
}

// Skill 模块中使用
public class DiscoveryController {
    
    public Object discover() {
        DiscoveryCoordinator coordinator = ServiceHolder.get(DiscoveryCoordinator.class);
        // ...
    }
}
```

**方案 C：通过 REST API 访问**

```java
// Skill 模块中通过 HTTP 调用主项目 API
public class DiscoveryController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Object discover() {
        String url = "http://localhost:8080/api/internal/discovery/gitee";
        return restTemplate.postForObject(url, null, DiscoveryResult.class);
    }
}
```

---

### 2.3 解决问题三：正确使用 DiscoveryCoordinator

**推荐用法**：

```java
// ✅ 正确：使用 DiscoveryCoordinator（scene-engine 层）
@Autowired
private DiscoveryCoordinator discoveryCoordinator;

// ❌ 错误：使用 DiscoveryOrchestrator（skill-common 层，只支持本地）
@Autowired
private DiscoveryOrchestrator discoveryOrchestrator;
```

**自动注册机制**：

```yaml
# application.yml 配置
scene:
  engine:
    discovery:
      gitee:
        enabled: true
        token: ${GITEE_TOKEN}  # 配置了 token 才会自动注册
        default-owner: ooderCN
        default-repo: skills
```

---

## 三、短期修复计划

### 3.1 SE 团队任务

| 任务 | 优先级 | 状态 |
|------|--------|------|
| 实现 GitRepositoryDiscovererAdapter 完整逻辑 | P0 | 待开发 |
| 添加 SkillContext 服务访问机制 | P1 | 待开发 |
| 更新文档说明两套发现体系的使用场景 | P1 | 已完成 |

### 3.2 os 工程临时方案

1. **使用 DiscoveryCoordinator 替代 DiscoveryOrchestrator**
2. **通过 REST API 或静态工具类访问主项目服务**
3. **等待 SE SDK 3.0.1 版本修复**

---

## 四、长期改进计划

### 4.1 统一发现服务接口

```java
// 统一接口设计
public interface UnifiedDiscoveryService {
    
    // 发现技能
    CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);
    
    // 发现单个技能
    CompletableFuture<CapabilityDTO> discoverOne(String skillId);
    
    // 获取可用的发现方法
    List<DiscoveryMethod> getAvailableMethods();
}
```

### 4.2 Skill 模块服务访问机制

```java
// Skill 服务访问接口
public interface SkillServiceAccessor {
    
    <T> T getService(Class<T> serviceClass);
    
    <T> Optional<T> getOptionalService(Class<T> serviceClass);
}
```

---

## 五、文档更新

已创建/更新的文档：

| 文档 | 路径 |
|------|------|
| 发现服务二次开发手册 | `e:\github\ooder-sdk\scene-engine\docs\v3.0.1\discovery-secondary-development-guide.md` |
| SE 协作完成报告 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_DISCOVERY_COLLABORATION_COMPLETED.md` |
| 问题分析与解决方案 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_SDK_3.0.1_DISCOVERY_ISSUES_RESPONSE.md` |

---

## 六、联系与反馈

如有问题或需要进一步沟通：

- **SE 团队项目路径**: `e:\github\ooder-sdk\scene-engine`
- **os 工程问题反馈**: `e:\apex\os\docs\se-sdk-discovery-issues.md`

---

**文档创建时间**: 2026-04-01
