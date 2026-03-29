# Discovery 模块深入分析报告

**分析日期**: 2026-03-29  
**分析范围**: `UnifiedDiscoveryServiceImpl` + `DiscoveryServiceImpl`  
**目标**: 识别冗余、错误、不必要的兼容性设计

---

## 一、问题总览

### 1.1 架构问题

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              当前架构（问题）                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Controller (apex)                                                         │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────┐                                                   │
│   │  DiscoveryServiceImpl │  ← 问题1: source 与 URL 不匹配                   │
│   │  (高级 API)          │  ← 问题2: 重复调用同一 URL                         │
│   └──────────┬──────────┘                                                   │
│              │                                                              │
│              ▼                                                              │
│   ┌─────────────────────┐                                                   │
│   │ UnifiedDiscovery     │  ← 问题3: 配置方式过多                            │
│   │ ServiceImpl          │  ← 问题4: 缓存逻辑冗余                            │
│   │ (底层实现)           │  ← 问题5: URL 判断与配置冲突                       │
│   └─────────────────────┘                                                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 问题分类

| 类别 | 问题数 | 严重程度 | 说明 |
|------|--------|---------|------|
| **错误设计** | 3 | 🔴 高 | 导致功能异常 |
| **冗余代码** | 8 | 🟡 中 | 增加维护成本 |
| **不必要兼容** | 5 | 🟢 低 | 可移除 |

---

## 二、错误设计（必须修复）

### 2.1 🔴 错误1: `DiscoveryServiceImpl.discover()` source 与 URL 不匹配

**位置**: `DiscoveryServiceImpl.java:47-59`

```java
// ❌ 错误代码
if ("gitee".equalsIgnoreCase(source) || "all".equalsIgnoreCase(source)) {
    List<SkillPackage> giteeSkills = unifiedDiscoveryService
        .discoverSkills(repositoryUrl, request.getSkillsPath())  // ← 使用 repositoryUrl
        .get(...);
}

if ("github".equalsIgnoreCase(source) || "all".equalsIgnoreCase(source)) {
    List<SkillPackage> githubSkills = unifiedDiscoveryService
        .discoverSkills(repositoryUrl, request.getSkillsPath())  // ← 再次使用同一个 repositoryUrl！
        .get(...);
}
```

**问题**:
- `source = "github"` 但 `repositoryUrl = "https://gitee.com/..."` 时，URL 不匹配
- `source = "all"` 时，两次调用使用同一个 URL

**影响**: 日志显示 GitHub 但实际请求 Gitee

**修复方案**:
```java
// ✅ 方案A: 移除 source 参数，只依赖 URL 判断
if (repositoryUrl.contains("gitee.com")) {
    return unifiedDiscoveryService.discoverSkills(repositoryUrl, ...);
} else if (repositoryUrl.contains("github.com")) {
    return unifiedDiscoveryService.discoverSkills(repositoryUrl, ...);
}

// ✅ 方案B: source 与 URL 强绑定
if ("gitee".equalsIgnoreCase(source)) {
    String giteeUrl = repositoryUrl != null ? repositoryUrl : config.getGiteeDefaultUrl();
    return unifiedDiscoveryService.discoverSkills(giteeUrl, ...);
}
```

---

### 2.2 🔴 错误2: `discoverFromGitee()` 配置覆盖 URL 解析

**位置**: `UnifiedDiscoveryServiceImpl.java:282-286`

```java
// ❌ 问题代码
String owner = giteeConfig.getOwner() != null ? giteeConfig.getOwner() : extractOwner(repositoryUrl);
String repo = giteeConfig.getRepo() != null ? giteeConfig.getRepo() : extractRepo(repositoryUrl);
String branch = giteeConfig.getBranch() != null ? giteeConfig.getBranch() : "main";
```

**问题**:
- 如果 `giteeConfig` 预先配置了 `owner/repo`，会覆盖 URL 解析结果
- 导致 URL 中的 owner/repo 被忽略

**场景**:
```
1. DiscoveryAutoConfiguration 配置 giteeConfig.owner = "ooderCN"
2. 调用 discoverSkills("https://gitee.com/otherUser/skills")
3. 实际使用 owner = "ooderCN" (配置值)，而非 "otherUser" (URL 解析值)
```

**修复方案**:
```java
// ✅ 方案: URL 参数优先
String owner = extractOwner(repositoryUrl);
String repo = extractRepo(repositoryUrl);
String branch = extractBranch(repositoryUrl) != null ? extractBranch(repositoryUrl) : "main";

// 配置仅作为 token 来源
String token = giteeConfig.getToken();
```

---

### 2.3 🔴 错误3: `discoverSkills()` 重载导致参数丢失

**位置**: `UnifiedDiscoveryServiceImpl.java:169-171`

```java
@Override
public CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl) {
    return discoverSkills(repositoryUrl, null);  // ← skillsPath 丢失
}
```

**问题**: 当调用单参数方法时，`skillsPath` 被设为 null，可能使用错误的配置值

---

## 三、冗余代码（建议移除）

### 3.1 🟡 冗余1: 双配置方式

**位置**: `UnifiedDiscoveryServiceImpl.java:89-143`

```java
// ❌ 冗余: 两种配置方式
@Deprecated
public void configureGitee(String token, String owner, String repo, String branch, String skillsPath) {
    // 旧版本
}

public void configureGitee(GiteeDiscoveryConfig config) {
    // 新版本
}
```

**保留原因**: 向后兼容  
**是否必要**: ❌ 否，可以移除旧版本

---

### 3.2 🟡 冗余2: 双缓存 TTL 设置

**位置**: `UnifiedDiscoveryServiceImpl.java:145-166`

```java
// ❌ 冗余: 三种设置 TTL 的方式
public void setGiteeCacheTtl(long ttlMs) { ... }
public void setGithubCacheTtl(long ttlMs) { ... }
public void setCacheConfig(String dir, long ttlMs, int maxEntries) { ... }
public void setCacheConfig(CacheConfig config) { ... }
```

**保留原因**: 灵活性  
**是否必要**: ❌ 否，统一使用一种方式

---

### 3.3 🟡 冗余3: 内存缓存 + 配置缓存

**位置**: `UnifiedDiscoveryServiceImpl.java:77-79`

```java
// ❌ 冗余: 两层缓存
private final Map<String, CacheEntry> memoryCache = new ConcurrentHashMap<>();
private long giteeCacheTtl = 3600000;
private long githubCacheTtl = 3600000;
```

**问题**: 
- 缓存逻辑分散在多处
- TTL 配置与缓存实现耦合

**建议**: 移除缓存，由调用方控制

---

### 3.4 🟡 冗余4: `DiscoveryServiceImpl` 空方法

**位置**: `DiscoveryServiceImpl.java:105-183`

```java
// ❌ 冗余: 空实现方法
@Override
public CompletableFuture<List<SkillInfo>> searchByCategory(String category) {
    return CompletableFuture.supplyAsync(() -> {
        return new ArrayList<>();  // ← 永远返回空
    });
}

@Override
public CompletableFuture<List<SkillInfo>> getInstalled() {
    return CompletableFuture.supplyAsync(() -> {
        return new ArrayList<>();  // ← 永远返回空
    });
}

@Override
public CompletableFuture<List<SkillInfo>> getCached() {
    return CompletableFuture.supplyAsync(() -> {
        return new ArrayList<>();  // ← 永远返回空
    });
}

@Override
public CompletableFuture<IntegrityCheckResult> checkIntegrity(String skillId) { ... }

@Override
public CompletableFuture<DependencyCheckResult> checkDependencies(String skillId) { ... }

@Override
public CompletableFuture<DependencyInstallResult> installDependencies(String skillId) { ... }
```

**保留原因**: 接口定义  
**是否必要**: ❌ 否，移除或抛出 UnsupportedOperationException

---

### 3.5 🟡 冗余5: `getSkillManifest()` 空实现

**位置**: `UnifiedDiscoveryServiceImpl.java:228-233`

```java
@Override
public CompletableFuture<String> getSkillManifest(String repositoryUrl, String skillName) {
    return CompletableFuture.supplyAsync(() -> {
        return "";  // ← 永远返回空字符串
    });
}
```

---

### 3.6 🟡 冗余6: `getReleases()` 空实现

**位置**: `UnifiedDiscoveryServiceImpl.java:235-247`

```java
@Override
public CompletableFuture<List<ReleaseInfo>> getReleases(String repositoryUrl) {
    return CompletableFuture.supplyAsync(() -> {
        return new ArrayList<>();  // ← 永远返回空
    });
}

@Override
public CompletableFuture<ReleaseInfo> getLatestRelease(String repositoryUrl) {
    return CompletableFuture.supplyAsync(() -> {
        return null;  // ← 永远返回 null
    });
}
```

---

### 3.7 🟡 冗余7: `CacheStatus` 半实现

**位置**: `UnifiedDiscoveryServiceImpl.java:264-271`

```java
@Override
public CacheStatus getCacheStatus(String repositoryUrl) {
    CacheStatus status = new CacheStatus();
    boolean cached = memoryCache.entrySet().stream()
        .anyMatch(e -> e.getKey().contains(repositoryUrl) && !e.getValue().isExpired());
    status.setCached(cached);
    return status;  // ← 只设置 cached 字段，其他字段未设置
}
```

---

### 3.8 🟡 冗余8: `DiscoveryListener` 机制

**位置**: `DiscoveryServiceImpl.java:185-241`

```java
private final List<DiscoveryListener> listeners = new ArrayList<>();

private void notifyDiscoveryStarted(DiscoveryRequest request) { ... }
private void notifyDiscoveryCompleted(DiscoveryResult result) { ... }
private void notifyDiscoveryFailed(String error) { ... }
```

**保留原因**: 事件通知  
**是否必要**: ❓ 需确认是否有实际使用

---

## 四、不必要兼容性设计

### 4.1 🟢 兼容1: `source` 参数

**位置**: `DiscoveryServiceImpl.java:43-59`

```java
String source = request.getSource();  // "gitee" | "github" | "all"
```

**保留原因**: 支持多平台  
**是否必要**: ❌ 否，URL 本身已包含平台信息

**建议**: 移除 `source` 参数，仅依赖 URL 判断

---

### 4.2 🟢 兼容2: 多索引文件名支持

**位置**: `UnifiedDiscoveryServiceImpl.java:329-338`

```java
for (String fallbackFile : fallbackIndexFiles) {
    String fallbackPath = buildIndexPath(basePath, fallbackFile);
    yamlContent = fetchAndDecodeGiteeFile(owner, repo, branch, fallbackPath, token);
    if (yamlContent != null) {
        usedIndexFile = fallbackPath;
        break;
    }
}
```

**保留原因**: 兼容不同命名习惯  
**是否必要**: ❓ 建议统一为 `skill-index.yaml`

---

### 4.3 🟢 兼容3: `skillsPath` 参数

**位置**: `UnifiedDiscoveryServiceImpl.java:286`

```java
String basePath = skillsPath != null ? normalizePath(skillsPath) : giteeConfig.getSkillsPath();
```

**保留原因**: 支持不同目录结构  
**是否必要**: ✅ 必要，但应简化逻辑

---

### 4.4 🟢 兼容4: `configureXxx()` 方法

**位置**: `UnifiedDiscoveryServiceImpl.java:89-143`

```java
public void configureGitee(String token, String owner, String repo, String branch, String skillsPath) { ... }
public void configureGitee(GiteeDiscoveryConfig config) { ... }
public void configureGithub(String token, String owner, String repo) { ... }
public void configureGithub(GithubDiscoveryConfig config) { ... }
```

**保留原因**: 灵活配置  
**是否必要**: ❌ 否，统一使用构造函数注入

---

### 4.5 🟢 兼容5: `CompletableFuture` 返回类型

**位置**: 所有公开方法

```java
public CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl) { ... }
```

**保留原因**: 异步支持  
**是否必要**: ❓ 需确认是否有异步调用场景

---

## 五、重构建议

### 5.1 简化架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              建议架构                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Controller (apex)                                                         │
│        │                                                                    │
│        │  discover(DiscoveryRequest)                                        │
│        ▼                                                                    │
│   ┌─────────────────────┐                                                   │
│   │  DiscoveryService   │  ← 单一入口                                       │
│   │  (简化版)           │  ← 无 source 参数，URL 判断平台                    │
│   └──────────┬──────────┘  ← 无缓存，由调用方控制                            │
│              │                                                              │
│              ▼                                                              │
│   ┌─────────────────────┐                                                   │
│   │ PlatformDiscovery   │  ← 接口                                           │
│   │    Service          │                                                   │
│   └──────────┬──────────┘                                                   │
│              │                                                              │
│       ┌──────┴──────┐                                                       │
│       ▼             ▼                                                       │
│   ┌─────────┐   ┌─────────┐                                                 │
│   │ Gitee   │   │ GitHub  │  ← 独立实现                                     │
│   │Discovery│   │Discovery│  ← 无状态                                       │
│   │Service  │   │Service  │  ← 无缓存                                       │
│   └─────────┘   └─────────┘                                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 简化接口

```java
// ✅ 简化后的接口
public interface DiscoveryService {
    
    /**
     * 发现技能
     * @param request 发现请求（仅包含 URL 和 skillsPath）
     * @return 发现结果
     */
    DiscoveryResult discover(DiscoveryRequest request);
    
    /**
     * 刷新缓存（可选，由调用方控制）
     */
    void refreshCache(String repositoryUrl);
}

public class DiscoveryRequest {
    private String repositoryUrl;  // 必填，包含平台信息
    private String skillsPath;     // 可选
    private String token;          // 可选，覆盖默认配置
    private String branch;         // 可选，默认 main/master
}
```

### 5.3 移除清单

| 移除项 | 位置 | 原因 |
|--------|------|------|
| `source` 参数 | `DiscoveryRequest` | URL 已包含平台信息 |
| `configureGitee(String...)` | `UnifiedDiscoveryServiceImpl` | 使用强类型配置 |
| `configureGithub(String...)` | `UnifiedDiscoveryServiceImpl` | 使用强类型配置 |
| `setGiteeCacheTtl()` | `UnifiedDiscoveryServiceImpl` | 统一配置 |
| `setGithubCacheTtl()` | `UnifiedDiscoveryServiceImpl` | 统一配置 |
| `setCacheConfig(String, long, int)` | `UnifiedDiscoveryServiceImpl` | 统一配置 |
| 内存缓存 | `UnifiedDiscoveryServiceImpl` | 由调用方控制 |
| `getReleases()` | `UnifiedDiscoveryServiceImpl` | 未实现 |
| `getLatestRelease()` | `UnifiedDiscoveryServiceImpl` | 未实现 |
| `getSkillManifest()` | `UnifiedDiscoveryServiceImpl` | 未实现 |
| `searchByCategory()` | `DiscoveryServiceImpl` | 未实现 |
| `getInstalled()` | `DiscoveryServiceImpl` | 未实现 |
| `getCached()` | `DiscoveryServiceImpl` | 未实现 |
| `checkIntegrity()` | `DiscoveryServiceImpl` | 未实现 |
| `checkDependencies()` | `DiscoveryServiceImpl` | 未实现 |
| `installDependencies()` | `DiscoveryServiceImpl` | 未实现 |
| `DiscoveryListener` | `DiscoveryServiceImpl` | 需确认使用情况 |

---

## 六、风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 移除缓存导致性能下降 | 中 | 调用方实现缓存 |
| 移除兼容方法导致编译错误 | 低 | 提供迁移指南 |
| 移除 `source` 参数导致调用方修改 | 中 | 自动从 URL 判断平台 |

---

## 七、结论

### 必须修复

1. **`DiscoveryServiceImpl.discover()`** - source 与 URL 不匹配
2. **`discoverFromGitee()`** - 配置覆盖 URL 解析

### 建议移除

1. 所有空实现方法
2. 旧版本配置方法（`@Deprecated`）
3. 内存缓存逻辑
4. `source` 参数

### 保留

1. `skillsPath` 参数（必要）
2. `CompletableFuture` 返回类型（需确认异步需求）

---

**文档版本**: 1.0  
**分析者**: AI Assistant  
**审核者**: 待定
