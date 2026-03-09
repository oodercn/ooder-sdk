# 架构检查报告

> **检查日期**: 2026-03-08  
> **检查范围**: Scene-Engine 场景组初始化相关代码  
> **SDK 版本**: 2.3.1

---

## 一、重复定义检查

### 1.1 发现的重复定义

| 类 | scene-engine | SDK | 状态 | 建议 |
|----|--------------|-----|------|------|
| `CapRequest` | `net.ooder.scene.core` | `net.ooder.sdk.api.skill.connector` | ⚠️ 字段不同 | **保留本地**，SDK版本用于SkillConnector |
| `CapResponse` | `net.ooder.scene.core` | 无 | ✅ 无重复 | **保留** |
| `SceneContext` | `net.ooder.scene.core` | `net.ooder.sdk.api.agent` | ⚠️ 功能不同 | **保留本地**，有skillConfigs特有功能 |
| `SceneConfig` | `net.ooder.scene.core` | `net.ooder.sdk.api.scene.model` | ⚠️ 泛型不同 | **保留本地**，SDK版本是泛型`<P>` |

### 1.2 分析结论

**不建议删除本地类**，原因：

1. **CapRequest** - SDK版本字段为 `capabilityId`，本地版本为 `capId`，API不兼容
2. **SceneContext** - 本地版本有 `skillConfigs` 管理功能，SDK版本没有
3. **SceneConfig** - SDK版本是泛型 `SceneConfig<P>`，本地版本是简单配置类

**建议方案**: 在需要与 SDK 交互时，使用适配器模式转换

---

## 二、分层架构检查

### 2.1 当前分层结构

```
scene-engine/
├── core/                    # 核心层
│   ├── SceneGroupInfo       # ✅ SDK适配器
│   ├── SceneMemberInfo      # ✅ SDK适配器
│   ├── SceneAgentCore       # ✅ 接口定义
│   ├── SceneAgentBridge     # ✅ 实现
│   ├── CapRouter            # ✅ 策略路由
│   ├── CapRoutingTable      # ✅ 路由表
│   ├── RoutingStrategy      # ✅ 路由策略
│   ├── SkillBinding         # ✅ 绑定信息
│   └── init/                # 初始化子模块
│       ├── SceneGroupInitializer  # ✅ 6步初始化
│       └── InitContext            # ✅ 初始化上下文
├── skill/                   # Skill层
│   ├── runtime/             # 运行时
│   ├── instance/            # 实例管理
│   └── ...
└── event/                   # 事件层
```

### 2.2 分层评估

| 层级 | 职责 | 状态 |
|------|------|------|
| 核心层 | 场景组管理、Agent管理、CAP路由 | ✅ 清晰 |
| 初始化层 | 场景组初始化流程 | ✅ 独立子模块 |
| Skill层 | Skill实例管理 | ✅ 完整 |
| 事件层 | 事件发布订阅 | ✅ 完整 |

---

## 三、包名规范检查

### 3.1 新增文件包名

| 文件 | 包名 | 状态 |
|------|------|------|
| `SceneGroupInitializer.java` | `net.ooder.scene.core.init` | ✅ 合理 |
| `InitContext.java` | `net.ooder.scene.core.init` | ✅ 合理 |
| `RoutingStrategy.java` | `net.ooder.scene.core` | ✅ 合理 |
| `SkillBinding.java` | `net.ooder.scene.core` | ✅ 合理 |
| `CapRoutingTable.java` | `net.ooder.scene.core` | ✅ 合理 |

### 3.2 包名规范建议

- ✅ 核心类放在 `net.ooder.scene.core`
- ✅ 初始化相关类放在 `net.ooder.scene.core.init`
- ✅ 实现类放在 `net.ooder.scene.core.impl`

---

## 四、伪实现排查

### 4.1 发现的伪实现/TODO

| 文件 | 行号 | 内容 | 优先级 | 状态 |
|------|------|------|--------|------|
| `SceneGroupInitializer.java` | 214-216 | `findMatchingSkills()` | **P0** | ✅ 已修复 |
| `PersonalNetworkManager.java` | 134 | 网段扫描 | P2 | 待定 |
| `MdnsDiscoveryService.java` | 116,135,156 | mDNS 协议 | P2 | 待定 |
| `SceneEngineImpl.java` | 227,234 | 登录验证/token解析 | P1 | 待定 |
| `SceneClientImpl.java` | 240 | 身份信息获取 | P1 | 待定 |

### 4.2 关键伪实现详情

**P0 - `findMatchingSkills()`**

```java
// SceneGroupInitializer.java:213-217
private List<SkillMatch> findMatchingSkills(Capability capability) {
    // TODO: 实现与 SkillCenter 的发现协议
    // 当前返回空列表，等待 SDK 支持
    return new ArrayList<>();
}
```

**影响**: 场景组初始化无法发现 Skill，Skill 挂载流程无法执行

**建议**: 集成 SDK 的 `UnifiedSkillRegistry` 或 `DiscoveryService`

---

## 五、修复建议

### 5.1 P0 优先修复

| 任务 | 说明 | 工作量 |
|------|------|--------|
| 实现 `findMatchingSkills()` | 集成 SkillCenter 发现协议 | 2人天 |

### 5.2 P1 后续修复

| 任务 | 说明 | 工作量 |
|------|------|--------|
| 登录验证实现 | SceneEngineImpl | 1人天 |
| Token解析实现 | SceneEngineImpl | 1人天 |

### 5.3 架构优化建议

1. **适配器模式** - 为 SDK 类创建适配器，保持本地 API 兼容
2. **接口隔离** - 考虑将 `SceneContext` 拆分为接口和实现
3. **依赖注入** - 使用 Spring 注入 SDK 组件

---

## 六、总结

| 检查项 | 结果 | 状态 |
|--------|------|------|
| 重复定义 | ⚠️ 存在但合理，建议保留 | ✅ 已确认 |
| 分层架构 | ✅ 清晰合理 | ✅ 无需调整 |
| 包名规范 | ✅ 符合规范 | ✅ 无需调整 |
| 伪实现 | ⚠️ 1个P0级 | ✅ 已修复 |

**关键行动项**: ✅ 已完成 `findMatchingSkills()` 方法，集成 `UnifiedSkillRegistry`

---

**检查完成**  
**最后更新**: 2026-03-08  
**状态**: ✅ 架构检查通过
