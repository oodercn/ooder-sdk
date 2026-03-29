# SE SDK Discovery 模块重构深入分析

**分析日期**: 2026-03-29  
**分析范围**: `UnifiedDiscoveryServiceImpl` 及相关模块  
**目标**: 确定重构范围和团队协作需求

---

## 一、问题深入分析

### 1.1 URL 判断问题分析

**现象**: 日志显示 `Discovering skills from: https://github.com/ooderCN/skills`，但实际传递的是 Gitee URL

**代码检查** (`UnifiedDiscoveryServiceImpl.java:191-209`):
```java
@Override
public CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl, String skillsPath) {
    return CompletableFuture.supplyAsync(() -> {
        try {
            logger.info("Discovering skills from: {}", repositoryUrl);
            
            if (repositoryUrl.contains("gitee.com")) {
                return discoverFromGitee(repositoryUrl, skillsPath);
            } else if (repositoryUrl.contains("github.com")) {
                return discoverFromGithub(repositoryUrl, skillsPath);
            }
            // ...
        }
    });
}
```

**结论**: 
- ✅ URL 判断逻辑本身是正确的
- ❌ 问题可能出在调用方或配置层面
- ❌ `discoverFromGitee` 方法内部使用了 `giteeConfig` Map，可能被错误配置覆盖

**根因定位**:
```java
// discoverFromGitee 方法 (第 277-284 行)
String owner = (String) giteeConfig.getOrDefault("owner", extractOwner(repositoryUrl));
String repo = (String) giteeConfig.getOrDefault("repo", extractRepo(repositoryUrl));
```

如果 `giteeConfig` 被其他地方配置了错误的值，会导致使用配置值而非 URL 解析值。

---

### 1.2 缓存机制问题分析

**当前架构**:
```
┌─────────────────────────────────────────────────────────────┐
│                    JsonFileCacheManager                      │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐     ┌─────────────────────────────┐   │
│  │  Memory Cache   │────►│  File Cache (.json files)   │   │
│  │  ConcurrentHashMap│     │  ./ooder/cache/discovery/   │   │
│  └─────────────────┘     └─────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

**问题清单**:

| 问题 | 位置 | 影响 | 严重程度 |
|------|------|------|---------|
| 缓存键缺少分支信息 | `buildCacheKey()` | 不同分支数据混淆 | 高 |
| 文件缓存数据不一致 | `JsonFileCacheManager` | 缓存污染 | 高 |
| 双层缓存增加复杂度 | 整体架构 | 维护困难 | 中 |
| 缓存过期检查不严格 | `exists()` | 可能返回过期数据 | 中 |

**缓存键问题详解**:
```java
// 当前实现 (第 498-500 行)
private String buildCacheKey(String platform, String owner, String repo, String basePath) {
    return String.format("%s:%s/%s/%s", platform, owner, repo, basePath != null ? basePath : "");
}
// 示例: gitee:ooderCN/skills/skill-index

// 问题: 分支信息缺失
// master 分支和 main 分支会使用相同的缓存键！
```

---

### 1.3 兼容性设计问题分析

**当前配置方式** (共 4 种):

```
┌──────────────────────────────────────────────────────────────────┐
│                        配置方式                                   │
├──────────────────────────────────────────────────────────────────┤
│  1. configureGitee(token, owner, repo, branch, skillsPath)       │
│     └── 旧版本 API，使用 Map<String, Object> 存储                 │
│                                                                   │
│  2. configureGitee(GiteeDiscoveryConfig)                         │
│     └── 新版本 API，使用强类型配置对象                             │
│                                                                   │
│  3. DiscoveryProperties (Spring Boot)                            │
│     └── 配置文件方式: scene.engine.discovery.gitee.*              │
│                                                                   │
│  4. 直接设置 giteeConfig/githubConfig Map                        │
│     └── 内部使用，无类型安全                                       │
└──────────────────────────────────────────────────────────────────┘
```

**问题**:
- 配置优先级不明确
- `Map<String, Object>` 缺乏类型安全
- 多种配置方式可能相互覆盖

---

### 1.4 架构问题分析

**当前类职责**:

```
UnifiedDiscoveryServiceImpl (1100+ 行)
├── 配置管理
│   ├── configureGitee() x2
│   ├── configureGithub()
│   ├── setCacheConfig()
│   └── giteeConfig/githubConfig Map
├── 缓存管理
│   ├── JsonFileCacheManager
│   ├── memoryCache
│   └── TTL 管理
├── URL 解析
│   ├── extractOwner()
│   ├── extractRepo()
│   └── normalizePath()
├── API 调用
│   ├── fetchUrlContent()
│   ├── fetchAndDecodeGiteeFile()
│   └── fetchSkillsFromGitee/Github()
├── YAML 解析
│   ├── parseSkillIndex()
│   ├── resolveIncludes()
│   └── createSkillPackage()
└── 业务逻辑
    ├── discoverSkills()
    ├── discoverSkill()
    └── getReleases()
```

**违反的设计原则**:
- ❌ 单一职责原则 (SRP)
- ❌ 开闭原则 (OCP) - 添加新平台需要修改类
- ❌ 依赖倒置原则 (DIP) - 直接依赖具体实现

---

## 二、任务拆解

### 2.1 任务分类

| 类别 | 任务 | SE 独立 | 需 SDK 协作 | 需 Skills 协作 |
|------|------|---------|-------------|----------------|
| **P0 - 紧急修复** | 修复缓存键缺少分支 | ✅ | - | - |
| | 修复 URL 判断日志问题 | ✅ | - | - |
| | 移除文件缓存 | ✅ | - | - |
| **P1 - 架构优化** | 统一配置方式 | ✅ | - | - |
| | 拆分 DiscoveryService | ✅ | ⚠️ 可能 | - |
| | 添加详细日志 | ✅ | - | - |
| **P2 - 规范统一** | YAML 格式规范 | - | - | ✅ |
| | SkillPackage 接口优化 | ⚠️ 需确认 | ✅ | - |

### 2.2 详细任务清单

#### P0 - 紧急修复 (SE 独立完成)

**任务 1: 修复缓存键缺少分支信息**
```java
// 修改位置: UnifiedDiscoveryServiceImpl.java

// 修改前
private String buildCacheKey(String platform, String owner, String repo, String basePath) {
    return String.format("%s:%s/%s/%s", platform, owner, repo, basePath != null ? basePath : "");
}

// 修改后
private String buildCacheKey(String platform, String owner, String repo, String branch, String basePath) {
    return String.format("%s:%s/%s:%s:%s", platform, owner, repo, branch, basePath != null ? basePath : "");
}
```

**任务 2: 移除文件缓存**
```java
// 修改位置: UnifiedDiscoveryServiceImpl.java

// 移除 JsonFileCacheManager
// - private JsonFileCacheManager cacheManager;

// 使用简单内存缓存
private final Map<String, CacheEntry> memoryCache = new ConcurrentHashMap<>();

private static class CacheEntry {
    List<SkillPackage> skills;
    long timestamp;
    long ttlMs;
    
    boolean isExpired() {
        return System.currentTimeMillis() > timestamp + ttlMs;
    }
}
```

**任务 3: 添加 URL 判断日志**
```java
// 修改位置: UnifiedDiscoveryServiceImpl.java

@Override
public CompletableFuture<List<SkillPackage>> discoverSkills(String repositoryUrl, String skillsPath) {
    return CompletableFuture.supplyAsync(() -> {
        try {
            logger.info("Discovering skills from URL: {}", repositoryUrl);
            
            if (repositoryUrl == null || repositoryUrl.isEmpty()) {
                logger.warn("Repository URL is null or empty");
                return new ArrayList<>();
            }
            
            String normalizedUrl = repositoryUrl.toLowerCase();
            
            if (normalizedUrl.contains("gitee.com")) {
                logger.debug("Detected Gitee platform for URL: {}", repositoryUrl);
                return discoverFromGitee(repositoryUrl, skillsPath);
            } else if (normalizedUrl.contains("github.com")) {
                logger.debug("Detected GitHub platform for URL: {}", repositoryUrl);
                return discoverFromGithub(repositoryUrl, skillsPath);
            } else {
                logger.warn("Unsupported repository URL: {}", repositoryUrl);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error("Failed to discover skills from: " + repositoryUrl, e);
            return new ArrayList<>();
        }
    });
}
```

---

#### P1 - 架构优化 (SE 主导，可能需 SDK 协作)

**任务 4: 统一配置方式**

**修改内容**:
1. 移除 `configureGitee(String...)` 旧版本方法
2. 统一使用 `GiteeDiscoveryConfig` 和 `DiscoveryProperties`
3. 移除 `Map<String, Object> giteeConfig/githubConfig`

**影响范围**:
- `UnifiedDiscoveryServiceImpl.java` - 主要修改
- `DiscoveryAutoConfiguration.java` - 配置注入调整
- 调用方代码 - 需要迁移到新 API

**任务 5: 拆分 DiscoveryService**

**建议架构**:
```
┌─────────────────────────────────────────────────────────────┐
│                   DiscoveryService (接口)                    │
├─────────────────────────────────────────────────────────────┤
│  + discoverSkills(url, path): List<SkillPackage>            │
│  + discoverSkill(url, name): SkillPackage                   │
│  + refreshCache(url): boolean                               │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ GiteeDiscovery  │ │ GithubDiscovery │ │ LocalDiscovery  │
│    Service      │ │    Service      │ │    Service      │
└─────────────────┘ └─────────────────┘ └─────────────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            ▼
              ┌─────────────────────────┐
              │   DiscoveryCacheManager │
              │   (内存缓存，含分支信息)  │
              └─────────────────────────┘
```

**需要 SDK 协作的情况**:
- 如果 `SkillPackage` 接口需要扩展
- 如果需要修改 `skills-framework` 中的接口定义

---

#### P2 - 规范统一 (需 Skills 团队协作)

**任务 6: YAML 格式规范统一**

**当前问题**:
- `skillForm` 字段位置不统一（根级别 vs spec 节点）
- 索引文件名不统一（skill-index.yaml vs index.yaml）

**建议规范**:
```yaml
apiVersion: ooder.io/v1
kind: Skill
metadata:
  skillId: xxx
  name: xxx
  version: x.x.x
spec:
  skillForm: SCENE        # 统一在 spec 内
  sceneType: default
  roles: [...]
  # ...
```

**协作方**: Skills 团队负责 YAML 文件格式规范

---

**任务 7: SkillPackage 接口优化**

**当前问题**:
- `metadata` 字段类型为 `Map<String, Object>`，缺乏类型安全
- `skillForm` 等字段需要从 metadata 中提取

**建议优化**:
```java
public class SkillPackage {
    // 现有字段...
    
    // 新增强类型字段
    private SkillForm skillForm;  // enum: ATOMIC, SCENE, PROVIDER, DRIVER
    private String sceneType;
    
    // 保留 metadata 用于扩展
    private Map<String, Object> metadata;
}

public enum SkillForm {
    ATOMIC, SCENE, PROVIDER, DRIVER
}
```

**协作方**: SDK 团队负责 `skills-framework` 模块修改

---

## 三、团队协作矩阵

### 3.1 责任分配矩阵 (RACI)

| 任务 | SE 团队 | SDK 团队 | Skills 团队 |
|------|---------|----------|-------------|
| P0-1 缓存键修复 | **R/A** | I | I |
| P0-2 移除文件缓存 | **R/A** | I | I |
| P0-3 URL 日志优化 | **R/A** | I | I |
| P1-4 统一配置方式 | **R/A** | C | I |
| P1-5 拆分服务架构 | **R** | **A/C** | I |
| P2-6 YAML 规范 | C | I | **R/A** |
| P2-7 SkillPackage 优化 | C | **R/A** | I |

**图例**: R=负责执行, A=批准, C=咨询, I=知情

### 3.2 协作接口定义

#### SE ↔ SDK 协作接口

```java
// SDK 团队需提供的接口变更（如需要）
public interface SkillPackage {
    // 现有方法...
    
    // 建议新增
    SkillForm getSkillForm();
    void setSkillForm(SkillForm form);
    
    String getSceneType();
    void setSceneType(String type);
}
```

#### SE ↔ Skills 协作接口

```yaml
# Skills 团队需遵循的 YAML 规范
spec:
  skillForm: SCENE | ATOMIC | PROVIDER | DRIVER  # 必填
  sceneType: string                               # 可选
```

---

## 四、风险评估

### 4.1 风险矩阵

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| 缓存变更导致数据丢失 | 中 | 高 | 保留内存缓存，提供缓存预热接口 |
| 配置方式变更导致兼容性问题 | 高 | 中 | 提供迁移指南，保留过渡期兼容 |
| 服务拆分影响现有调用方 | 中 | 高 | 使用适配器模式，保持接口不变 |
| YAML 规范变更影响现有技能 | 高 | 中 | Skills 团队统一更新，提供转换脚本 |

### 4.2 回滚策略

每个任务完成后：
1. 保留旧代码注释，标注 `@Deprecated`
2. 版本号递增（3.0.1 → 3.0.2 → 3.1.0）
3. 提供回滚配置开关

---

## 五、结论与建议

### 5.1 总结

| 类别 | 结论 |
|------|------|
| **SE 独立完成** | P0 全部任务 + P1 部分任务（约 80%） |
| **需 SDK 协作** | P1-5 服务拆分（如涉及接口变更）、P2-7 SkillPackage 优化 |
| **需 Skills 协作** | P2-6 YAML 格式规范统一 |

### 5.2 建议执行顺序

```
第一阶段 (SE 独立)          第二阶段 (SE 主导)           第三阶段 (跨团队协作)
─────────────────────────────────────────────────────────────────────────────
P0-1 缓存键修复    ──────►  P1-4 统一配置方式   ──────►  P2-6 YAML 规范
P0-2 移除文件缓存           P1-5 服务拆分               P2-7 SkillPackage 优化
P0-3 URL 日志优化
     (1 天)                    (1-2 天)                    (2-3 天)
```

### 5.3 协作建议

1. **SE 团队**: 优先完成 P0 任务，为后续重构奠定基础
2. **SDK 团队**: 评估 `SkillPackage` 接口优化需求，确认是否需要修改
3. **Skills 团队**: 统一 YAML 文件格式，确保 `skillForm` 在 `spec` 节点内

---

**文档版本**: 1.0  
**分析者**: AI Assistant  
**审核者**: 待定
