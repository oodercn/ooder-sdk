# 发现服务二次开发手册

**版本**: 3.0.1  
**日期**: 2026-04-01  
**作者**: SE 团队

---

## 一、概述

### 1.1 什么是发现服务

发现服务是 Ooder SDK 的核心组件，用于发现和获取技能包（Skill Package）。支持多种发现方式：

| 发现方式 | 说明 | 适用场景 | 实现状态 |
|----------|------|----------|----------|
| LOCAL | 本地文件系统发现 | 开发调试、离线环境 | ✅ 完整实现 |
| GITEE | Gitee 仓库发现 | 国内团队协作 | ✅ 完整实现 |
| GITHUB | GitHub 仓库发现 | 国际团队协作 | ✅ 完整实现 |
| P2P_UDP | UDP 广播发现 | 局域网发现 | 🚧 实验性 |
| P2P_MDNS | mDNS 服务发现 | 局域网服务发现 | 🚧 实验性 |
| SKILL_CENTER | 技能中心发现 | 企业级技能管理 | 🚧 实验性 |

### 1.2 架构设计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           应用层 (Your Application)                          │
│         @Autowired DiscoveryCoordinator  ← 推荐使用                          │
│         @Autowired DiscoveryService                                          │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           scene-engine (v3.0.2)                              │
│  DiscoveryCoordinator (发现协调器)                                            │
│  ├── 自动注册发现器                                                            │
│  ├── 缓存管理                                                                  │
│  ├── 结果聚合                                                                  │
│  └── 状态管理                                                                  │
│                                                                              │
│  SkillDiscovererAdapter (统一发现器接口)                                       │
│  ├── GiteeSkillDiscovererAdapter                                             │
│  ├── GitHubSkillDiscovererAdapter                                            │
│  └── LocalSkillDiscovererAdapter                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           skills-framework (v3.0.1)                          │
│  GitRepositoryDiscovererAdapter ← 完整实现                                    │
│  ├── Gitee API 集成                                                          │
│  ├── GitHub API 集成                                                          │
│  ├── 缓存机制                                                                  │
│  ├── 重试机制                                                                  │
│  └── 错误处理                                                                  │
│                                                                              │
│  LocalDiscoverer                                                             │
│  UdpDiscoverer                                                               │
│  SkillCenterDiscoverer                                                       │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、快速开始

### 2.1 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>3.0.2</version>
</dependency>
```

### 2.2 基础配置

```yaml
# application.yml
scene:
  engine:
    discovery:
      enabled: true
      
      # Gitee 配置
      gitee:
        enabled: true
        token: ${GITEE_TOKEN:}
        default-owner: ooderCN
        default-repo: skills
        default-branch: master
        skills-path: ""
        cache-ttl-ms: 3600000
      
      # GitHub 配置
      github:
        enabled: false
        token: ${GITHUB_TOKEN:}
        default-owner: ""
        default-repo: ""
        cache-ttl-ms: 3600000
      
      # 本地配置
      local:
        enabled: true
        skills-path: ./skills
        search-paths: ./skills,./.ooder/downloads,./.ooder/installed,./.ooder/dev
      
      # 缓存配置
      cache:
        enabled: true
        ttl-ms: 3600000
        dir: ./.ooder/cache/discovery
        max-entries: 100
```

### 2.3 使用发现服务

```java
@Service
public class MyDiscoveryService {
    
    @Autowired
    private DiscoveryCoordinator discoveryCoordinator;
    
    /**
     * 从 Gitee 发现技能
     */
    public List<DiscoveryService.SkillInfo> discoverFromGitee() throws Exception {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setSource("gitee");
        
        DiscoveryResult result = discoveryCoordinator.discover(request).get();
        return result.getSkills();
    }
    
    /**
     * 从所有来源发现技能
     */
    public List<DiscoveryService.SkillInfo> discoverFromAll() throws Exception {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setSource("all");
        
        DiscoveryResult result = discoveryCoordinator.discover(request).get();
        return result.getSkills();
    }
    
    /**
     * 获取单个技能详情
     */
    public CapabilityDTO getSkillDetail(String skillId) throws Exception {
        return discoveryCoordinator.discoverOne(skillId).get();
    }
}
```

---

## 三、GitRepositoryDiscovererAdapter 功能说明

### 3.1 完整功能列表

`GitRepositoryDiscovererAdapter` 在 3.0.1 版本中已完成完整实现：

| 方法 | 功能 | 状态 |
|------|------|------|
| `discover()` | 发现所有技能 | ✅ 完整实现 |
| `discover(String skillId)` | 发现单个技能 | ✅ 完整实现 |
| `discoverByScene(String sceneId)` | 按场景发现 | ✅ 完整实现 |
| `search(String query)` | 关键词搜索 | ✅ 完整实现 |
| `searchByCapability(String capabilityId)` | 按能力搜索 | ✅ 完整实现 |
| `discoverByCategory(String category)` | 按分类发现 | ✅ 完整实现 |
| `searchByTags(List<String> tags)` | 按标签搜索 | ✅ 完整实现 |

### 3.2 核心特性

#### 3.2.1 Gitee/GitHub API 集成

```java
// Gitee API
private static final String GITEE_API_BASE = "https://gitee.com/api/v5";

// GitHub API
private static final String GITHUB_API_BASE = "https://api.github.com";
```

**支持的 API 操作**：
- 获取仓库目录树：`/repos/{owner}/{repo}/git/trees/{branch}?recursive=1`
- 获取文件内容：`/repos/{owner}/{repo}/contents/{path}?ref={branch}`

#### 3.2.2 缓存机制

```java
// 默认缓存 5 分钟
private long cacheTtlMs = 300000;

// 清除缓存
adapter.clearCache();
```

#### 3.2.3 重试机制

```java
private static final int MAX_RETRIES = 3;
private static final long RETRY_DELAY_MS = 1000;
```

#### 3.2.4 错误处理

| 异常类型 | 触发条件 | 处理方式 |
|----------|----------|----------|
| `AuthenticationException` | Token 无效或过期 | 抛出异常，记录日志 |
| `RepositoryNotFoundException` | 仓库不存在 | 抛出异常，记录日志 |
| `ApiRateLimitException` | API 限流 | 抛出异常，包含重试时间 |
| `DiscoveryException` | 其他错误 | 重试 3 次后抛出 |

### 3.3 配置示例

```java
// 创建 Gitee 发现器
GitRepositoryDiscovererAdapter giteeAdapter = new GitRepositoryDiscovererAdapter("gitee");
giteeAdapter.setDefaultOwner("ooderCN");
giteeAdapter.setDefaultRepo("skills");
giteeAdapter.setDefaultBranch("master");
giteeAdapter.setGiteeToken("your-gitee-token");
giteeAdapter.setCacheTtlMs(3600000);  // 1 小时缓存

// 创建 GitHub 发现器
GitRepositoryDiscovererAdapter githubAdapter = new GitRepositoryDiscovererAdapter("github");
githubAdapter.setDefaultOwner("your-org");
githubAdapter.setDefaultRepo("skills");
githubAdapter.setDefaultBranch("main");
githubAdapter.setGithubToken("your-github-token");

// 发现技能
List<SkillPackage> skills = giteeAdapter.discover().join();
```

---

## 四、自定义发现器

### 4.1 实现 SkillDiscovererAdapter 接口

```java
package com.example.discovery;

import net.ooder.scene.discovery.CapabilityDTO;
import net.ooder.scene.discovery.adapter.DiscoveryMethod;
import net.ooder.scene.discovery.adapter.SkillDiscovererAdapter;
import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;
import net.ooder.scene.discovery.api.DiscoveryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 自定义发现器示例 - 从内部技能仓库发现
 */
@Component
public class InternalRepoDiscovererAdapter implements SkillDiscovererAdapter {

    private final InternalRepoClient repoClient;
    
    public InternalRepoDiscovererAdapter(InternalRepoClient repoClient) {
        this.repoClient = repoClient;
    }
    
    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            DiscoveryResult result = new DiscoveryResult();
            result.setSource(getMethod().getCode());
            result.setTimestamp(System.currentTimeMillis());
            
            try {
                List<InternalSkill> skills = repoClient.listSkills();
                
                List<DiscoveryService.SkillInfo> skillInfos = new ArrayList<>();
                for (InternalSkill skill : skills) {
                    skillInfos.add(convertToSkillInfo(skill));
                }
                
                result.setSkills(skillInfos);
                result.setTotalCount(skillInfos.size());
                
            } catch (Exception e) {
                result.setErrorMessage(e.getMessage());
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<CapabilityDTO> discoverOne(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InternalSkill skill = repoClient.getSkill(skillId);
                if (skill != null) {
                    return convertToCapabilityDTO(skill);
                }
            } catch (Exception e) {
                // log error
            }
            return null;
        });
    }
    
    @Override
    public DiscoveryMethod getMethod() {
        return DiscoveryMethod.SKILL_CENTER;
    }
    
    @Override
    public boolean isAvailable() {
        return repoClient.isConfigured();
    }
    
    @Override
    public int getPriority() {
        return 80;
    }
    
    @Override
    public String getName() {
        return "内部技能仓库发现器";
    }
    
    private DiscoveryService.SkillInfo convertToSkillInfo(InternalSkill skill) {
        DiscoveryService.SkillInfo info = new DiscoveryService.SkillInfo();
        info.setSkillId(skill.getId());
        info.setName(skill.getName());
        info.setVersion(skill.getVersion());
        info.setDescription(skill.getDescription());
        info.setCategory(skill.getCategory());
        info.setTags(skill.getTags());
        info.setSource("internal");
        return info;
    }
    
    private CapabilityDTO convertToCapabilityDTO(InternalSkill skill) {
        CapabilityDTO dto = new CapabilityDTO();
        dto.setId(skill.getId());
        dto.setSkillId(skill.getId());
        dto.setName(skill.getName());
        dto.setVersion(skill.getVersion());
        dto.setDescription(skill.getDescription());
        dto.setCategory(skill.getCategory());
        dto.setTags(skill.getTags());
        dto.setSource("internal");
        return dto;
    }
}
```

### 4.2 自动注册

Spring Boot 会自动扫描并注册所有 `SkillDiscovererAdapter` Bean：

```java
// 无需手动注册，DiscoveryAutoConfiguration 会自动处理
// 只需要确保：
// 1. 类上添加 @Component 注解
// 2. 实现 SkillDiscovererAdapter 接口
// 3. isAvailable() 返回 true（配置正确时）
```

---

## 五、高级用法

### 5.1 自定义缓存策略

```java
@Service
public class CachedDiscoveryService {
    
    @Autowired
    private DiscoveryCoordinator coordinator;
    
    /**
     * 带缓存的发现
     */
    public List<DiscoveryService.SkillInfo> discoverWithCache(String source) {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setSource(source);
        
        DiscoveryResult result = coordinator.discover(request).join();
        return result.getSkills();
    }
    
    /**
     * 强制刷新
     */
    public List<DiscoveryService.SkillInfo> forceRefresh(String source) {
        return coordinator.refresh(source).join()
            .stream()
            .map(this::convertToSkillInfo)
            .collect(Collectors.toList());
    }
}
```

### 5.2 多源聚合发现

```java
@Service
public class AggregatedDiscoveryService {
    
    @Autowired
    private DiscoveryCoordinator coordinator;
    
    /**
     * 从多个来源聚合发现
     */
    public Map<String, List<DiscoveryService.SkillInfo>> discoverFromMultiple(
            List<String> sources) {
        
        Map<String, List<DiscoveryService.SkillInfo>> results = new LinkedHashMap<>();
        
        List<CompletableFuture<Void>> futures = sources.stream()
            .map(source -> CompletableFuture.runAsync(() -> {
                try {
                    DiscoveryRequest request = new DiscoveryRequest();
                    request.setSource(source);
                    
                    DiscoveryResult result = coordinator.discover(request).get();
                    results.put(source, result.getSkills());
                } catch (Exception e) {
                    results.put(source, Collections.emptyList());
                }
            }))
            .collect(Collectors.toList());
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        return results;
    }
}
```

### 5.3 使用过滤条件

```java
// 使用 DiscoveryFilter 过滤
DiscoveryFilter filter = new DiscoveryFilter();
filter.setSceneId("recruitment");
filter.setCategory("hr");
filter.setTags(List.of("interview", "candidate"));

adapter.setFilter(filter);
List<SkillPackage> filtered = adapter.discover().join();
```

---

## 六、常见问题

### 6.1 发现器未注册

**问题**：调用发现时返回 "No discoverer available for method: GITEE"

**原因**：
1. Token 未配置
2. `enabled` 设置为 false
3. `isAvailable()` 返回 false

**解决方案**：

```yaml
scene:
  engine:
    discovery:
      gitee:
        enabled: true
        token: your-gitee-token  # 必须配置
```

### 6.2 认证失败

**问题**：`AuthenticationException: Authentication failed. Check your token for gitee`

**解决方案**：
1. 检查 Token 是否正确
2. 检查 Token 是否过期
3. 检查 Token 权限（需要 repo 或 public_repo 权限）

### 6.3 API 限流

**问题**：`ApiRateLimitException: API rate limit exceeded`

**解决方案**：
1. 等待限流时间后重试
2. 使用缓存减少 API 调用
3. GitHub 使用认证 Token 可提高限流阈值

### 6.4 Skill 模块无法访问主项目服务

**问题**：Skill 模块中 `@Autowired DiscoveryCoordinator` 为 null

**原因**：Skill 模块使用独立的 ClassLoader

**解决方案**：

```java
// 方案 A：通过静态工具类
DiscoveryCoordinator coordinator = ServiceHolder.get(DiscoveryCoordinator.class);

// 方案 B：通过 REST API
String url = "http://localhost:8080/api/internal/discovery/gitee";
DiscoveryResult result = restTemplate.postForObject(url, null, DiscoveryResult.class);

// 方案 C：通过 SkillContext（如果支持）
DiscoveryCoordinator coordinator = SkillContext.getService(DiscoveryCoordinator.class);
```

---

## 七、API 参考

### 7.1 DiscoveryCoordinator

| 方法 | 说明 |
|------|------|
| `discover(String source)` | 从指定来源发现技能 |
| `discover(DiscoveryRequest request)` | 使用请求对象发现技能 |
| `discoverOne(String skillId)` | 发现单个技能 |
| `refresh(String source)` | 刷新指定来源的缓存 |
| `getAvailableMethods()` | 获取所有可用的发现方法 |
| `registerAdapter(SkillDiscovererAdapter adapter)` | 注册发现器适配器 |

### 7.2 GitRepositoryDiscovererAdapter

| 方法 | 说明 |
|------|------|
| `discover()` | 发现所有技能 |
| `discover(String skillId)` | 发现单个技能 |
| `discoverByScene(String sceneId)` | 按场景发现 |
| `search(String query)` | 关键词搜索 |
| `searchByCapability(String capabilityId)` | 按能力搜索 |
| `discoverByCategory(String category)` | 按分类发现 |
| `searchByTags(List<String> tags)` | 按标签搜索 |
| `clearCache()` | 清除缓存 |
| `setCacheTtlMs(long ttlMs)` | 设置缓存时间 |

### 7.3 DiscoveryRequest

| 字段 | 类型 | 说明 |
|------|------|------|
| `source` | String | 来源：local/gitee/github/all |
| `category` | SkillCategory | 分类过滤 |
| `form` | SkillForm | 形态过滤 |
| `tags` | List<String> | 标签过滤 |
| `keyword` | String | 关键词搜索 |

### 7.4 DiscoveryResult

| 字段 | 类型 | 说明 |
|------|------|------|
| `skills` | List<SkillInfo> | 技能列表 |
| `totalCount` | int | 总数 |
| `source` | String | 来源 |
| `timestamp` | long | 时间戳 |
| `errorMessage` | String | 错误信息 |

---

## 八、版本历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 3.0.1 | 2026-04-01 | GitRepositoryDiscovererAdapter 完整实现、缓存机制、错误处理 |
| 3.0.1 | 2026-04-01 | 统一发现器接口、自动注册机制 |
| 3.0.0 | 2026-03-15 | 初始版本 |

---

## 九、相关文档

| 文档 | 路径 |
|------|------|
| skills-framework 协作声明 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SKILLS_FRAMEWORK_COLLABORATION_STATEMENT.md` |
| 问题分析与解决方案 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_SDK_3.0.1_DISCOVERY_ISSUES_RESPONSE.md` |
| SE 协作完成报告 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_DISCOVERY_COLLABORATION_COMPLETED.md` |

---

**文档路径**: `e:\github\ooder-sdk\scene-engine\docs\v3.0.1\discovery-secondary-development-guide.md`

**文档更新时间**: 2026-04-01
