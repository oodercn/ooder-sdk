# SDK与SceneEngine分层架构设计

## 1. 设计原则

### 1.1 核心原则

| 层级 | 职责 | 模型类型 | 状态控制 | 复杂度 |
|------|------|----------|----------|--------|
| **SDK** | 单一功能实现，无状态判断 | 贫血模型 | 无状态 | 简单 |
| **SceneEngine** | 聚合、编排、状态控制 | 充血模型 | 有状态 | 复杂 |

### 1.2 设计哲学

```
SDK层：只做一件事，做好一件事
    - 不判断何时执行
    - 不管理缓存策略
    - 不控制状态流转
    - 只提供基础能力

SceneEngine层：编排一切，控制一切
    - 决定何时调用SDK
    - 管理缓存策略
    - 控制状态机流转
    - 聚合多个SDK调用
```

## 2. 功能分工矩阵

### 2.1 发现功能

| 功能点 | SDK职责 | SceneEngine职责 | 说明 |
|--------|---------|-----------------|------|
| **本地发现** | 扫描目录，读取manifest，返回原始数据 | 调用SDK，缓存结果，管理TTL | SDK只返回数据，不做缓存判断 |
| **GitHub发现** | 调用API，解析响应，返回原始数据 | 配置管理，限流控制，缓存策略 | SDK不做限流判断 |
| **UDP发现** | 发送/接收广播包，返回发现结果 | 会话管理，超时控制，结果聚合 | SDK只处理网络层 |
| **结果过滤** | 无 | 按条件过滤，去重，排序 | SceneEngine控制分支 |
| **缓存策略** | 无 | 决定何时使用缓存，何时刷新 | SDK被调用时才执行 |

### 2.2 安装功能

| 功能点 | SDK职责 | SceneEngine职责 | 说明 |
|--------|---------|-----------------|------|
| **下载Skill** | 从URL下载文件到指定路径 | 管理下载队列，并发控制 | SDK只负责下载 |
| **解压安装** | 解压到指定目录 | 决定安装路径，验证完整性 | SDK只执行解压 |
| **依赖解析** | 返回依赖列表（原始数据） | 决定安装顺序，拓扑排序 | SDK不做拓扑排序 |
| **安装进度** | 报告当前状态（被动） | 聚合多个进度，状态机管理 | SceneEngine主动查询 |
| **失败回滚** | 删除已安装文件（执行） | 决定是否回滚，回滚策略 | SDK只执行删除 |
| **安装恢复** | 无 | 管理恢复逻辑，断点续传 | SceneEngine有状态 |

### 2.3 缓存功能

| 功能点 | SDK职责 | SceneEngine职责 | 说明 |
|--------|---------|-----------------|------|
| **内存缓存** | 无 | 管理内存缓存，LRU/LFU策略 | SDK每次重新查询 |
| **文件缓存** | 读写文件（执行） | 决定何时读写，TTL检查 | SDK只执行IO |
| **缓存过期** | 无 | 检查TTL，决定刷新策略 | SDK不判断过期 |
| **缓存去重** | 无 | 合并多渠道结果，统一索引 | SceneEngine聚合 |

## 3. 数据模型设计

### 3.1 SDK贫血模型（数据+无行为）

```java
// SDK层 - SkillPackage.java
// 只包含数据和getter/setter，无业务方法
public class SkillPackage {
    private String skillId;
    private String name;
    private String version;
    private String description;
    private List<String> dependencies;
    
    // 只有getter/setter
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    // ... 其他getter/setter
}

// SDK层 - InstallResult.java
// 只包含状态，无后续操作
public class InstallResult {
    private boolean success;
    private String message;
    private String skillId;
    private String version;
    
    // 只有getter/setter
}
```

### 3.2 SceneEngine充血模型（数据+行为）

```java
// SceneEngine层 - RichSkill.java
// 包装SDK贫血模型，添加业务逻辑
public class RichSkill {
    private SkillPackage rawPackage;  // 引用SDK贫血模型
    private DiscoverySource source;
    private long discoveredTime;
    
    // 充血方法：业务逻辑
    public boolean isInstallable() {
        // 检查依赖是否满足
        // 检查版本兼容性
        // 检查权限
        return checkDependencies() && checkCompatibility() && checkPermission();
    }
    
    public List<RichSkill> getDependencies() {
        // 解析依赖，返回RichSkill列表
        // 从DiscoveryCoordinator查询
    }
    
    public InstallPlan createInstallPlan() {
        // 创建安装计划，包含拓扑排序
        // 返回InstallSession
    }
    
    public boolean isCached() {
        // 检查是否在缓存中
    }
    
    public boolean needsUpdate() {
        // 检查是否需要更新
    }
}

// SceneEngine层 - InstallSession.java
// 安装状态管理（状态机）
public class InstallSession {
    private String sessionId;
    private List<InstallTask> tasks;
    private InstallState state;  // PENDING/INSTALLING/PAUSED/FAILED/COMPLETED
    private long createTime;
    private long lastUpdateTime;
    
    // 充血方法：状态控制
    public void pause() { 
        // 暂停安装
        // 状态机转换：INSTALLING -> PAUSED
    }
    
    public void resume() { 
        // 恢复安装
        // 状态机转换：PAUSED -> INSTALLING
    }
    
    public void retry() { 
        // 重试失败任务
        // 状态机转换：FAILED -> INSTALLING
    }
    
    public InstallReport generateReport() { 
        // 生成安装报告
    }
    
    public boolean canResume() {
        // 检查是否可以恢复
        return state == InstallState.PAUSED || state == InstallState.FAILED;
    }
}
```

## 4. 调用流程

### 4.1 发现流程

```
用户调用
    ↓
SceneEngine.DiscoveryCoordinator.discover(source)
    ↓ 状态判断：使用缓存？刷新？
    ├─ 缓存有效 → 返回缓存的RichSkill列表
    ↓ 缓存无效
SDK.SkillDiscoverer.discover()
    ↓ 直接查询，无缓存判断
原始SkillPackage列表（贫血模型）
    ↓
SceneEngine.SkillEnricher.enrich(packages)
    ↓ 转换为充血模型
RichSkill列表（带业务方法）
    ↓
SceneEngine.CacheManager.save(skills)
    ↓
返回RichSkill列表
```

### 4.2 安装流程

```
用户调用
    ↓
SceneEngine.InstallCoordinator.install(skillId)
    ↓ 创建InstallSession（有状态）
InstallSession.createInstallPlan()
    ↓ 拓扑排序，状态控制
SDK.SkillInstaller.install(skillPackage)（逐个调用）
    ↓ 无状态，只执行安装
InstallSession.updateState(progress)
    ↓ 状态机转换
返回InstallReport
```

## 5. 分层架构图

```
┌─────────────────────────────────────────┐
│  应用层 (SceneClient/AdminClient)       │
│  - 用户界面交互                         │
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│  SceneEngine层（充血模型+状态控制）      │
│  ┌─────────────────────────────────────┐│
│  │ UnifiedSceneService（统一入口）      ││
│  │ - 认证、审计、权限                   ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │ DiscoveryCoordinator（发现协调器）   ││
│  │ - 缓存策略控制                       ││
│  │ - 多渠道聚合                         ││
│  │ - RichSkill（充血模型）              ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │ InstallCoordinator（安装协调器）     ││
│  │ - 状态机控制                         ││
│  │ - 安装计划编排                       ││
│  │ - InstallSession（有状态）           ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │ CacheManager（缓存管理）             ││
│  │ - 缓存策略                           ││
│  │ - TTL管理                            ││
│  └─────────────────────────────────────┘│
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│  SDK层（贫血模型+无状态）                │
│  ┌─────────────────────────────────────┐│
│  │ SkillDiscoverer（发现接口）          ││
│  │ - LocalDiscoverer                   ││
│  │ - GitHubDiscoverer                  ││
│  │ - GiteeDiscoverer                   ││
│  │ - UdpDiscoverer                     ││
│  │ - SkillPackage（贫血模型）           ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │ SkillInstaller（安装接口）           ││
│  │ - 下载/解压/安装                     ││
│  │ - InstallResult（贫血模型）          ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │ SkillManifest（清单模型）            ││
│  │ - 依赖定义                           ││
│  │ - 能力定义                           ││
│  └─────────────────────────────────────┘│
└─────────────────────────────────────────┘
```

## 6. 重构任务清单

### 6.1 SDK层修改（简化）

| 文件 | 修改内容 | 原因 |
|------|----------|------|
| `GitHubDiscoverer.java` | 删除内置缓存逻辑 | 缓存由SceneEngine控制 |
| `GitHubDiscoverer.java` | 删除限流判断逻辑 | 限流由SceneEngine控制 |
| `GitHubDiscoverer.java` | 简化discover()方法 | 只返回原始数据 |
| `LocalDiscoverer.java` | 保持现状 | 已符合单一职责 |
| `SkillInstaller.java` | 简化接口 | 只保留核心安装方法 |

### 6.2 SceneEngine层新增（充血模型）

| 文件 | 职责 | 说明 |
|------|------|------|
| `RichSkill.java` | Skill充血模型 | 包装SDK SkillPackage |
| `InstallSession.java` | 安装状态管理 | 状态机控制 |
| `DiscoveryCoordinator.java` | 发现协调器 | 控制缓存和分支 |
| `InstallCoordinator.java` | 安装协调器 | 控制安装状态 |
| `SkillEnricher.java` | 数据增强器 | 贫血→充血转换 |

### 6.3 SceneEngine层删除（重复）

| 文件 | 原因 | 替代方案 |
|------|------|----------|
| `LocalFsDiscoveryProvider.java` | 与SDK LocalDiscoverer重复 | 使用SDK LocalDiscoverer |
| `DiscoveryService.java` | 接口重复 | 简化为DiscoveryCoordinator |
| `InstallTaskManager.java` | 功能重复 | 合并到InstallCoordinator |
| `SkillInfo.java` | 与SDK SkillPackage重复 | 使用RichSkill |
| `DiscoveredItem.java` | 与SDK SkillPackage重复 | 使用RichSkill |

## 7. 状态控制职责

### 7.1 SDK无状态设计

```java
// SDK - GitHubDiscoverer.java
public class GitHubDiscoverer {
    // 删除缓存字段，每次重新查询
    public CompletableFuture<List<SkillPackage>> discover() {
        // 直接调用API，不做缓存判断
        // 不关心何时调用，只返回结果
        return callGitHubApi();
    }
}
```

### 7.2 SceneEngine有状态控制

```java
// SceneEngine - DiscoveryCoordinator.java
public class DiscoveryCoordinator {
    private DiscoveryState state;  // IDLE/DISCOVERING/CACHED
    private CacheManager cacheManager;
    
    public CompletableFuture<List<RichSkill>> discover(String source) {
        // 状态控制：决定是否使用缓存
        if (state == DiscoveryState.CACHED && cacheManager.isValid(source)) {
            return cacheManager.get(source);  // 使用缓存
        }
        
        // 状态控制：调用SDK
        state = DiscoveryState.DISCOVERING;
        return sdkDiscoverer.discover()
            .thenApply(this::enrichPackages)  // 转换为充血模型
            .thenApply(cacheManager::save);   // 保存缓存
    }
}
```

## 8. 总结

| 方面 | SDK | SceneEngine |
|------|-----|-------------|
| **模型** | 贫血模型（纯数据） | 充血模型（数据+行为） |
| **状态** | 无状态 | 有状态（状态机控制） |
| **分支** | 无分支判断 | 控制所有分支逻辑 |
| **缓存** | 不处理 | 统一管理策略 |
| **聚合** | 单一功能 | 聚合多个SDK调用 |
| **复杂度** | 简单 | 复杂 |
| **复用性** | 高 | 低 |

通过这种分层设计：
- SDK层保持简单、可复用、无状态
- SceneEngine层负责复杂的业务逻辑、状态管理、策略控制
- 两层职责清晰，避免重复实现
