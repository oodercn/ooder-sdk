# 技能发现协议 - v2.3

> **版本**: 2.3  
> **发布日期**: 2026-02-23  
> **状态**: 稳定版

## 1. 协议概述

技能发现协议定义了 Skill 的发现、注册、查询和调用机制。v2.3 版本在 v0.8.0 基础上统一了发现服务抽象，简化了发现流程。

### 1.1 核心概念

| 概念 | 说明 |
|------|------|
| Scene | 场景，一组相关 Skill 的集合 |
| Capability | 能力，Skill 提供的具体功能 |
| Skill | 技能，封装了特定功能的可插拔模块 |
| Discovery | 发现，查找可用的 Scene/Capability/Skill |

### 1.2 v2.3 核心变更

- 统一 `CapabilityDiscoveryService` 接口
- 支持多种发现提供者（UDP、mDNS、SkillCenter、Local FS）
- 引入发现范围概念（PERSONAL/DEPARTMENT/COMPANY/PUBLIC）
- 标准化发现查询和结果格式

## 2. 发现服务架构

### 2.1 架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                 CapabilityDiscoveryService                      │
│                      能力发现服务层                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  UDP        │  │   mDNS      │  │ SkillCenter │             │
│  │ Provider    │  │  Provider   │  │  Provider   │             │
│  │ (优先级100) │  │ (优先级90)  │  │ (优先级80)  │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
│                                                                 │
│  ┌─────────────┐                                               │
│  │  Local FS   │                                               │
│  │  Provider   │                                               │
│  │ (优先级50)  │                                               │
│  └─────────────┘                                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 发现范围

| 范围 | 说明 | 适用发现方式 |
|------|------|-------------|
| PERSONAL | 个人设备 | Local FS, UDP |
| DEPARTMENT | 部门分享 | UDP, mDNS, SkillCenter |
| COMPANY | 公司管理 | SkillCenter API |
| PUBLIC | 公共社区 | SkillCenter API |

## 3. 核心接口

### 3.1 CapabilityDiscoveryService

```java
/**
 * 能力发现服务接口
 * @since 2.3
 */
public interface CapabilityDiscoveryService {
    
    /**
     * 同步所有索引
     */
    CompletableFuture<SyncResult> syncAllIndexes();
    
    /**
     * 列出场景
     * @param category 分类过滤，null表示所有
     */
    CompletableFuture<List<DiscoveredItem>> listScenes(String category);
    
    /**
     * 搜索场景
     * @param query 查询字符串，支持通配符 *
     */
    CompletableFuture<List<DiscoveredItem>> searchScenes(String query);
    
    /**
     * 获取场景详情
     */
    CompletableFuture<SceneDetail> getSceneDetail(String sceneId);
    
    /**
     * 获取场景下可用技能
     */
    CompletableFuture<List<DiscoveredItem>> getAvailableSkills(String sceneId);
    
    /**
     * 列出能力
     */
    CompletableFuture<List<DiscoveredItem>> listCapabilities(String category);
    
    /**
     * 搜索能力
     */
    CompletableFuture<List<DiscoveredItem>> searchCapabilities(String query);
    
    /**
     * 获取能力详情
     */
    CompletableFuture<CapabilityDetail> getCapabilityDetail(String capId);
    
    /**
     * 获取能力下可用技能
     */
    CompletableFuture<List<DiscoveredItem>> getAvailableSkillsForCapability(String capId);
    
    /**
     * 注册发现提供者
     */
    void registerProvider(DiscoveryProvider provider);
    
    /**
     * 注销发现提供者
     */
    void unregisterProvider(String providerName);
    
    /**
     * 设置发现范围
     */
    void setDiscoveryScope(DiscoveryScope scope);
    
    /**
     * 获取当前发现范围
     */
    DiscoveryScope getDiscoveryScope();
}
```

### 3.2 DiscoveryProvider

```java
/**
 * 发现提供者接口
 * @since 2.3
 */
public interface DiscoveryProvider {
    
    /**
     * 获取提供者名称
     */
    String getProviderName();
    
    /**
     * 初始化
     */
    void initialize(DiscoveryConfig config);
    
    /**
     * 启动
     */
    void start();
    
    /**
     * 停止
     */
    void stop();
    
    /**
     * 是否运行中
     */
    boolean isRunning();
    
    /**
     * 执行发现
     */
    CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query);
    
    /**
     * 获取优先级（数值越大优先级越高）
     */
    int getPriority();
    
    /**
     * 是否适用于指定范围
     */
    boolean isApplicable(DiscoveryScope scope);
}
```

## 4. 数据模型

### 4.1 DiscoveryQuery

```java
public class DiscoveryQuery {
    private DiscoveryType type;      // SCENE/CAPABILITY/SKILL
    private String query;            // 查询字符串
    private DiscoveryScope scope;    // 发现范围
    private Map<String, Object> filters; // 过滤条件
}
```

### 4.2 DiscoveredItem

```java
public class DiscoveredItem {
    private String id;               // 唯一标识
    private String name;             // 名称
    private String type;             // 类型：SCENE/CAPABILITY/SKILL
    private String description;      // 描述
    private String version;          // 版本
    private Map<String, Object> metadata; // 元数据
    private String source;           // 来源提供者
    private long discoveredAt;       // 发现时间
}
```

### 4.3 SceneDetail

```java
public class SceneDetail {
    private String sceneId;          // 场景ID
    private String name;             // 场景名称
    private String description;      // 场景描述
    private String category;         // 分类
    private String icon;             // 图标
    private List<String> tags;       // 标签
    private List<String> capabilities; // 包含的能力
    private Map<String, Object> config; // 配置
}
```

### 4.4 CapabilityDetail

```java
public class CapabilityDetail {
    private String capId;            // 能力ID
    private String name;             // 能力名称
    private String version;          // 版本
    private String category;         // 分类
    private String description;      // 描述
    private String status;           // 状态
    private List<String> dependencies; // 依赖
    private Map<String, Object> metadata; // 元数据
}
```

## 5. 发现流程

### 5.1 标准发现流程

```
1. 用户设置发现范围
   └── DiscoveryScope scope = DiscoveryScope.DEPARTMENT;
       discoveryService.setDiscoveryScope(scope);

2. 执行发现查询
   └── DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SCENE, "messaging");
       CompletableFuture<List<DiscoveredItem>> future = discoveryService.searchScenes("messaging");

3. 系统选择适用的发现提供者
   └── 根据 scope 过滤提供者
       └── LocalFsProvider (PERSONAL ✓)
       └── UdpProvider (PERSONAL ✓, DEPARTMENT ✓)
       └── MdnsProvider (DEPARTMENT ✓)
       └── SkillCenterProvider (DEPARTMENT ✓, COMPANY ✓, PUBLIC ✓)

4. 并行执行发现
   └── 按优先级排序执行
       └── UdpProvider (100) -> 发现结果 A
       └── MdnsProvider (90) -> 发现结果 B
       └── SkillCenterProvider (80) -> 发现结果 C

5. 聚合结果
   └── 去重、排序、返回
```

### 5.2 同步流程

```
syncAllIndexes()
    │
    ├── 1. 从所有提供者获取数据
    │   ├── LocalFsProvider.sync()
    │   ├── UdpProvider.sync()
    │   ├── MdnsProvider.sync()
    │   └── SkillCenterProvider.sync()
    │
    ├── 2. 合并索引
    │   └── 去重、验证
    │
    ├── 3. 更新本地缓存
    │   └── 写入本地文件系统
    │
    └── 4. 返回同步结果
        └── SyncResult { sceneCount, capabilityCount, skillCount }
```

## 6. 发现提供者实现

### 6.1 UDP Broadcast Provider

| 属性 | 值 |
|------|-----|
| 名称 | UDP-BROADCAST |
| 优先级 | 100 |
| 适用范围 | PERSONAL, DEPARTMENT |
| 端口 | 48888 |
| 协议 | UDP Broadcast |

### 6.2 mDNS Provider

| 属性 | 值 |
|------|-----|
| 名称 | MDNS |
| 优先级 | 90 |
| 适用范围 | PERSONAL, DEPARTMENT |
| 协议 | DNS-SD |
| 服务类型 | _ooder._tcp |

### 6.3 SkillCenter Provider

| 属性 | 值 |
|------|-----|
| 名称 | SKILL-CENTER |
| 优先级 | 80 |
| 适用范围 | DEPARTMENT, COMPANY, PUBLIC |
| 协议 | HTTP REST API |
| 认证 | Token |

### 6.4 Local FS Provider

| 属性 | 值 |
|------|-----|
| 名称 | LOCAL-FS |
| 优先级 | 50 |
| 适用范围 | PERSONAL, DEPARTMENT, COMPANY, PUBLIC |
| 存储 | 本地文件系统 |
| 缓存 | 是 |

## 7. 错误处理

### 7.1 错误码

| 错误码 | 说明 | 处理建议 |
|--------|------|---------|
| 4001 | 发现范围不支持 | 检查 scope 设置 |
| 4002 | 提供者未找到 | 检查提供者注册 |
| 4003 | 发现超时 | 增加超时时间或重试 |
| 4004 | 同步失败 | 检查网络连接 |
| 4005 | 无效的查询条件 | 检查 query 参数 |

## 8. 版本历史

| 版本 | 发布日期 | 主要变更 |
|------|----------|----------|
| 2.3 | 2026-02-23 | 统一发现服务抽象，标准化接口 |
| 0.8.0 | 2026-02-20 | 能力发现服务抽象层 |
| 0.7.3 | 2026-02-17 | 初始发现协议 |

## 9. 参考资料

- [Agent 协议](./agent-protocol.md)
- [协议主文档](./protocol-main.md)
- [发现实现指南](../v0.8.0/discovery-implementation-guide.md)

---

**Ooder Team | Version 2.3 | 2026-02-23**
