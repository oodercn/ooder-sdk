# Scene Engine 发现模块设计文档

## 版本信息
- **版本**: v2.3
- **日期**: 2026-03-02
- **状态**: 设计完成，接口定义完成

---

## 1. 设计目标

### 1.1 核心目标
1. **统一发现入口**：封装GitHub/Gitee/本地发现，提供统一API
2. **智能缓存机制**：多级缓存避免频繁访问远程API（每小时60次限制）
3. **完整性保障**：文件完整性检查、依赖检查、自动修复
4. **版本管理**：支持多版本共存、版本冲突解决
5. **离线支持**：完全离线工作模式

### 1.2 设计原则
- **分层清晰**：接口层→业务层→数据层
- **单一职责**：每个组件只做一件事
- **失败隔离**：局部失败不影响整体
- **可观测性**：全链路日志+监控

---

## 2. 架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    发现架构设计 v2.3                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  【接口层】                                                      │
│  ├── DiscoveryService           统一发现接口（对外）              │
│  │   ├── discover()             统一发现入口                     │
│  │   ├── refresh()              强制刷新                         │
│  │   ├── search()               搜索                            │
│  │   ├── checkIntegrity()       完整性检查                      │
│  │   ├── checkDependencies()    依赖检查                        │
│  │   └── installDependencies()  依赖安装                        │
│  │                                                              │
│  【业务层】                                                      │
│  ├── CacheManager               缓存管理器                       │
│  │   ├── Memory Cache           内存缓存（LRU）                  │
│  │   └── File Cache             文件缓存（JSON）                 │
│  │                                                              │
│  ├── IntegrityChecker           完整性检查器                     │
│  │   ├── 文件哈希校验                                           │
│  │   ├── 元数据校验                                             │
│  │   └── 自动修复                                               │
│  │                                                              │
│  ├── DependencyManager          依赖管理器                       │
│  │   ├── 依赖检查                                               │
│  │   ├── 依赖树解析                                             │
│  │   ├── 安装顺序计算                                           │
│  │   └── 循环依赖检测                                           │
│  │                                                              │
│  ├── UnifiedDiscoveryService    统一发现服务                     │
│  │   ├── GitHub发现                                            │
│  │   ├── Gitee发现                                             │
│  │   └── 本地发现                                              │
│  │                                                              │
│  【数据层】                                                      │
│  ├── VFS Storage                VFS存储                         │
│  │   ├── /skills/registry/      注册表                          │
│  │   ├── /skills/cache/         缓存                           │
│  │   ├── /skills/installed/     已安装                         │
│  │   └── /skills/temp/          临时文件                        │
│  │                                                              │
│  └── Local File System          本地文件系统                     │
│      └── .ooder/cache/           本地缓存目录                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 VFS地址规划

```
vfs://
├── skills/
│   ├── registry/              # 技能注册表
│   │   ├── index.json         # 全局索引
│   │   ├── channels.json      # 渠道配置
│   │   └── history/           # 发现历史
│   │       └── {channelId}/
│   │           └── {timestamp}.json
│   ├── cache/                 # 发现缓存
│   │   ├── github/            # GitHub缓存
│   │   │   └── {hash}.json
│   │   ├── gitee/             # Gitee缓存
│   │   │   └── {hash}.json
│   │   └── local/             # 本地缓存
│   │       └── {hash}.json
│   ├── installed/             # 已安装技能
│   │   └── {skillId}/
│   │       ├── {version}/
│   │       └── manifest.json
│   └── temp/                  # 临时下载
│       └── {uuid}/
└── .ooder/
    └── cache/                 # 本地缓存目录
        ├── skills/            # 技能缓存
        └── discovery/         # 发现缓存
```

---

## 3. 核心组件

### 3.1 DiscoveryService（统一发现接口）

**位置**: `net.ooder.scene.discovery.api.DiscoveryService`

**职责**: 对外提供统一的发现入口，封装所有发现细节

**核心方法**:
```java
// 统一发现入口（自动处理缓存/网络/分批）
CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);

// 强制刷新（忽略缓存）
CompletableFuture<DiscoveryResult> refresh(DiscoveryRequest request);

// 搜索（跨渠道）
CompletableFuture<List<SkillInfo>> search(String keyword);

// 完整性检查
CompletableFuture<IntegrityCheckResult> checkIntegrity(String skillId);

// 依赖检查
CompletableFuture<DependencyCheckResult> checkDependencies(String skillId);

// 依赖安装
CompletableFuture<DependencyInstallResult> installDependencies(String skillId);
```

### 3.2 CacheManager（缓存管理器）

**位置**: `net.ooder.scene.discovery.cache.CacheManager`

**职责**: 统一管理多级缓存

**缓存策略**:
| 层级 | 类型 | TTL | 容量限制 | 淘汰策略 |
|------|------|-----|----------|----------|
| L1 | 内存缓存 | 1小时 | 100条目 | LRU |
| L2 | 文件缓存 | 24小时 | 1000条目 | TTL到期删除 |

**核心方法**:
```java
// 获取缓存
SkillInfo getSkill(String skillId);

// 写入缓存
void putSkill(SkillInfo skill);

// 检查有效性
boolean exists(String skillId);

// 清除过期缓存
int clearExpired();
```

### 3.3 IntegrityChecker（完整性检查器）

**位置**: `net.ooder.scene.discovery.integrity.IntegrityChecker`

**职责**: 检查Skill包的完整性

**检查项**:
1. 文件存在性检查
2. 文件哈希校验（SHA-256）
3. 元数据完整性
4. 依赖完整性

**核心方法**:
```java
// 检查完整性
CompletableFuture<IntegrityCheckResult> check(String skillId);

// 修复问题
CompletableFuture<RepairResult> repair(String skillId);
```

### 3.4 DependencyManager（依赖管理器）

**位置**: `net.ooder.scene.discovery.dependency.DependencyManager`

**职责**: 管理Skill依赖关系

**核心方法**:
```java
// 检查依赖
CompletableFuture<DependencyCheckResult> checkDependencies(String skillId);

// 解析依赖树
CompletableFuture<DependencyTree> resolveDependencyTree(String skillId);

// 获取安装顺序（拓扑排序）
CompletableFuture<List<String>> getInstallOrder(String skillId);

// 安装依赖
CompletableFuture<DependencyInstallResult> installDependencies(String skillId);

// 检测循环依赖
CompletableFuture<List<List<String>>> detectCircularDependencies(String skillId);
```

---

## 4. 缓存策略

### 4.1 缓存流程

```
发现请求
    │
    ▼
检查内存缓存 ──命中──→ 返回结果
    │ 未命中
    ▼
检查文件缓存 ──命中──→ 更新内存缓存 → 返回结果
    │ 未命中
    ▼
分批网络请求 ──完成──→ 更新缓存 → 返回结果
```

### 4.2 分批策略

```java
// 分批配置
class BatchConfig {
    int batchSize = 10;           // 每批10个请求
    long batchIntervalMs = 1000;  // 批次间隔1秒
    int maxConcurrent = 5;        // 最大并发5个
    long timeoutMs = 30000;       // 超时30秒
}
```

### 4.3 缓存一致性

1. **写入策略**: 先写内存，再异步写文件
2. **过期策略**: TTL到期自动删除
3. **同步策略**: 启动时从文件加载到内存
4. **清理策略**: 定期清理过期缓存

---

## 5. 完整性检查

### 5.1 检查流程

```
开始检查
    │
    ▼
检查文件存在性 ──缺失──→ 记录缺失文件
    │
    ▼
计算文件哈希 ──不匹配──→ 记录损坏文件
    │
    ▼
检查元数据 ──无效──→ 记录元数据错误
    │
    ▼
检查依赖 ──缺失──→ 记录缺失依赖
    │
    ▼
生成检查报告
```

### 5.2 修复策略

1. **文件缺失**: 从缓存/远程重新下载
2. **文件损坏**: 重新下载并替换
3. **元数据错误**: 尝试从备份恢复
4. **依赖缺失**: 触发依赖安装流程

---

## 6. 依赖管理

### 6.1 依赖解析

```java
// 依赖树示例
skill-a:1.0.0
├── skill-b:1.0.0
│   └── skill-c:1.0.0
└── skill-d:2.0.0
    └── skill-c:1.5.0  // 版本冲突！
```

### 6.2 冲突解决

1. **版本冲突**: 使用最新兼容版本
2. **循环依赖**: 检测并报错
3. **可选依赖**: 默认不安装，可配置

### 6.3 安装顺序

使用拓扑排序计算安装顺序：
```
依赖树: A -> B -> C
        A -> D -> C

安装顺序: C -> B -> D -> A
```

---

## 7. 版本管理

### 7.1 版本号规范

遵循语义化版本（SemVer）:
```
主版本号.次版本号.修订号-预发布标识
 1.0.0-beta
```

### 7.2 版本策略

1. **latest**: 最新稳定版本
2. **stable**: 推荐稳定版本
3. **具体版本**: 精确版本号

### 7.3 多版本共存

```
/skill-a/
├── 1.0.0/          # 稳定版本
├── 1.1.0/          # 新版本
├── 2.0.0-beta/     # 预发布版本
└── manifest.json   # 版本索引
```

---

## 8. 离线支持

### 8.1 离线模式

```java
// 离线配置
class OfflineConfig {
    boolean offlineMode = false;     // 离线模式开关
    boolean allowCacheRead = true;   // 允许读取缓存
    boolean allowCacheWrite = true;  // 允许写入缓存
    long maxCacheAge = 7 * 24 * 3600 * 1000;  // 最大缓存年龄7天
}
```

### 8.2 离线工作流程

1. **启动检测**: 检测网络状态
2. **离线切换**: 自动切换到离线模式
3. **缓存使用**: 优先使用本地缓存
4. **延迟同步**: 网络恢复后同步更新

---

## 9. 进度反馈

### 9.1 发现进度

```java
class DiscoveryProgress {
    int totalSteps = 5;           // 总步骤数
    int currentStep = 2;          // 当前步骤
    String currentPhase = "网络发现"; // 当前阶段
    int percentage = 40;          // 进度百分比
    String message = "发现中...";  // 状态消息
}
```

### 9.2 阶段定义

1. **初始化** (10%)
2. **检查缓存** (20%)
3. **本地发现** (40%)
4. **网络发现** (70%)
5. **结果合并** (90%)
6. **完成** (100%)

---

## 10. 异常处理

### 10.1 异常分类

| 异常类型 | 处理策略 | 用户反馈 |
|----------|----------|----------|
| 网络异常 | 重试3次，使用缓存 | 提示使用缓存数据 |
| 限流异常 | 指数退避，延迟重试 | 提示API限制 |
| 数据异常 | 跳过，记录日志 | 提示部分数据异常 |
| 存储异常 | 清理空间，重试 | 提示存储空间不足 |

### 10.2 重试策略

```java
// 指数退避
int retryCount = 0;
long delay = 1000;  // 初始1秒
while (retryCount < maxRetries) {
    try {
        return execute();
    } catch (RateLimitException e) {
        Thread.sleep(delay);
        delay *= 2;  // 指数增长
        retryCount++;
    }
}
```

---

## 11. 接口清单

### 11.1 核心接口

| 接口 | 位置 | 职责 |
|------|------|------|
| DiscoveryService | `api/DiscoveryService.java` | 统一发现入口 |
| CacheManager | `cache/CacheManager.java` | 缓存管理 |
| IntegrityChecker | `integrity/IntegrityChecker.java` | 完整性检查 |
| DependencyManager | `dependency/DependencyManager.java` | 依赖管理 |

### 11.2 数据类

| 类名 | 位置 | 说明 |
|------|------|------|
| DiscoveryRequest | `api/DiscoveryService.java` | 发现请求 |
| DiscoveryResult | `api/DiscoveryService.java` | 发现结果 |
| SkillInfo | `api/DiscoveryService.java` | Skill信息 |
| CacheConfig | `cache/CacheManager.java` | 缓存配置 |
| DependencyTree | `dependency/DependencyManager.java` | 依赖树 |

---

## 12. 测试策略

### 12.1 单元测试

```java
// 测试类: DiscoveryServiceTest
@Test
public void testDiscoverWithCache() { }

@Test
public void testDiscoverForceRefresh() { }

@Test
public void testCheckIntegrity() { }

@Test
public void testCheckDependencies() { }
```

### 12.2 集成测试

1. **缓存测试**: 验证多级缓存一致性
2. **网络测试**: 模拟网络异常场景
3. **并发测试**: 多线程发现测试
4. **性能测试**: 大批量发现性能

---

## 13. 后续工作

### 13.1 待实现

1. DiscoveryServiceImpl 实现类
2. CacheManagerImpl 实现类
3. IntegrityCheckerImpl 实现类
4. DependencyManagerImpl 实现类

### 13.2 优化方向

1. **性能优化**: 异步批量处理
2. **存储优化**: 压缩缓存数据
3. **安全优化**: 签名验证
4. **监控优化**: 全链路追踪

---

## 14. 附录

### 14.1 术语表

| 术语 | 说明 |
|------|------|
| Skill | 技能包，可安装的组件 |
| Discovery | 发现，从远程/本地获取Skill列表 |
| Cache | 缓存，临时存储发现结果 |
| Integrity | 完整性，Skill包的完整性校验 |
| Dependency | 依赖，Skill之间的依赖关系 |

### 14.2 参考资料

1. [语义化版本规范](https://semver.org/)
2. [GitHub API文档](https://docs.github.com/en/rest)
3. [Gitee API文档](https://gitee.com/api/v5/swagger)

---

**作者**: Ooder Team  
**更新时间**: 2026-03-02
