# Scene-Engine 与 SDK 协作任务需求说明

> **文档版本**: v1.3.0  
> **编写日期**: 2026-03-08  
> **编写团队**: Scene-Engine Team  
> **目标团队**: SDK Team  
> **状态**: ✅ **协作完成 (SDK 2.3.1)**

---

## 一、背景说明

Scene-Engine 正在实现 **场景组初始化动作** 功能，根据 `SCENE-ENGINE-SPEC.md` 规范，需要 SDK 层提供以下核心能力支持。

当前 Scene-Engine 已完成：
- ✅ E-1: 重构 `SceneGroupInfo` 使用 SDK `SceneGroup`
- ✅ E-2: 重构 `SceneMemberInfo` 使用 SDK `SceneMember`
- ✅ E-3: 增强 `SceneAgentBridge` 支持 `MemberRole`
- ✅ E-4: 实现 `SceneGroupInitializer` 6步初始化
- ✅ E-5: 增强 `CapRouter` 支持策略路由
- ✅ E-6: 集成 `UnifiedSkillRegistry` 实现 Skill 发现
- ✅ E-7: 修复 SDK 2.3.1 架构变更导致的编译问题

---

## 二、SDK 2.3.1 架构变更

### 2.1 模块合并

SDK 团队已完成以下架构重构：

| 变更 | 说明 |
|------|------|
| llm-sdk-api → llm-sdk | 合并完成 |
| agent-sdk-api → agent-sdk-core | 合并完成 |
| 模块数量 | 5个 → 3个 |

### 2.2 新模块结构

```
agent-sdk (Parent POM) 2.3.1
├── llm-sdk              # LLM SDK（已合并 llm-sdk-api）
├── skills-framework     # Skills 框架（独立）
└── agent-sdk-core       # Agent SDK Core（已合并 agent-sdk-api）
```

### 2.3 已删除的类

| 类 | 替代方案 |
|----|----------|
| `LlmSdkFactory` | 使用 `MultiLlmAdapterApi` |

---

## 三、SDK 协作任务清单

### 3.1 SDK-1: SceneGroupManager 完整实现 (P0) ✅ 已完成

| 方法 | 状态 | 说明 |
|------|------|------|
| `create()` | ✅ | 创建场景组 |
| `destroy()` | ✅ | 销毁场景组 |
| `join()` | ✅ | Agent 加入 |
| `leave()` | ✅ | Agent 离开 |
| `handleFailover()` | ✅ | 故障转移逻辑 |
| `startHeartbeat()` | ✅ | 心跳机制 |
| `stopHeartbeat()` | ✅ | 心跳机制 |
| `generateKey()` | ✅ | 组密钥生成 |
| `reconstructKey()` | ✅ | 密钥重构（门限方案） |
| `distributeKeyShares()` | ✅ | 密钥分发 |

---

### 3.2 SDK-2: SceneManager 完整实现 (P0) ✅ 已完成

| 方法 | 状态 | 说明 |
|------|------|------|
| `initializeScene()` | ✅ | 场景初始化 |
| `startScene()` | ✅ | 启动场景 |
| `stopScene()` | ✅ | 停止场景 |
| `pauseScene()` | ✅ | 暂停场景 |
| `resumeScene()` | ✅ | 恢复场景 |
| `destroyScene()` | ✅ | 销毁场景 |
| `createSnapshot()` | ✅ | 创建快照 |
| `restoreSnapshot()` | ✅ | 恢复快照 |

---

### 3.3 SDK-3: SkillConnector 实现 (P0) ✅ 已完成

| 类型 | 状态 | 实现类 |
|------|------|--------|
| `HTTP` | ✅ | `HttpSkillConnector` |
| `LOCAL_JAR` | ✅ | `LocalJarSkillConnector` |
| `GRPC` | ✅ | `GrpcSkillConnector` |
| `WEBSOCKET` | ✅ | `WebSocketSkillConnector` |
| `UDP` | ✅ | `UdpSkillConnector` |

---

### 3.4 SDK-4: CAP 路由策略 (P1) ✅ 已完成

| 策略 | 状态 | 说明 |
|------|------|------|
| `priority` | ✅ | 按优先级选择 |
| `round-robin` | ✅ | 轮询选择 |
| `random` | ✅ | 随机选择 |
| `least-load` | ✅ | 最小负载选择 |

---

### 3.5 SDK-5: 离线模式支持 (P1) ✅ 已完成

| 策略 | 状态 | 说明 |
|------|------|------|
| `LOCAL_CACHE` | ✅ | 本地能力缓存 |
| `OFFLINE_FIRST` | ✅ | 离线优先 |
| `OPTIMISTIC_SYNC` | ✅ | 乐观同步 |
| `CONFLICT_RESOLUTION` | ✅ | 冲突解决 |

---

## 四、Scene-Engine 集成状态

### 4.1 已集成 SDK 功能

| 功能 | 集成状态 | 使用位置 |
|------|----------|----------|
| `SceneGroup` | ✅ 已集成 | `SceneGroupInfo` 适配器 |
| `SceneMember` | ✅ 已集成 | `SceneMemberInfo` 适配器 |
| `MemberRole` | ✅ 已集成 | `SceneAgentCore`, `SceneAgentBridge` |
| `SceneGroupManager` | ✅ 已集成 | `SceneGroupInitializer` |
| `SkillConnector` | ✅ 已集成 | `SceneGroupInitializer.mountSkills()` |
| `UnifiedSkillRegistry` | ✅ 已集成 | `SceneGroupInitializer.findMatchingSkills()` |
| `OfflineManager` | ✅ 可用 | 待集成到 CapRouter |

### 4.2 新增/修改文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `SceneGroupInfo.java` | 修改 | SDK SceneGroup 适配器 |
| `SceneMemberInfo.java` | 修改 | SDK SceneMember 适配器 |
| `SceneAgentCore.java` | 修改 | 增强，支持 MemberRole |
| `SceneAgentBridge.java` | 修改 | 完整实现 |
| `CapRouter.java` | 修改 | 增强策略路由 |
| `RoutingStrategy.java` | **新增** | 路由策略枚举 |
| `SkillBinding.java` | **新增** | Skill 绑定信息 |
| `CapRoutingTable.java` | **新增** | CAP 路由表 |
| `SceneGroupInitializer.java` | **新增** | 6步初始化器 |
| `InitContext.java` | **新增** | 初始化上下文 |
| `LlmConnectionPool.java` | 修改 | 删除废弃的 LlmSdkFactory 引用 |

---

## 五、编译修复记录

### 5.1 SDK 2.3.1 架构变更导致的编译问题

| 问题 | 文件 | 修复内容 |
|------|------|----------|
| `LlmSdkFactory` 已删除 | `LlmConnectionPool.java` | 删除无用 import |
| `SceneAgentBridge` 未导入 | `SceneGroupInitializer.java` | 添加 import |
| `SkillPackage.getEndpoint()` 不存在 | `SceneGroupInitializer.java` | 使用 metadata 获取 |
| `SkillPackage.getPriority()` 不存在 | `SceneGroupInitializer.java` | 使用 metadata 获取 |
| `SkillPackage.getRuntime()` 不存在 | `SceneGroupInitializer.java` | 使用 metadata 获取 |
| `SkillPackage.getRating()` 不存在 | `SceneGroupInitializer.java` | 使用 metadata 获取 |
| `SkillConnectorConfig.setParameter()` 不存在 | `SceneGroupInitializer.java` | 使用 `setParameters(Map)` |

---

## 六、验收结果

### 6.1 编译验证 ✅

- [x] Scene-Engine 编译通过
- [x] SDK 依赖正确引用
- [x] 无废弃类引用

### 6.2 功能验收 ✅

- [x] SceneGroupManager 所有方法可正常调用
- [x] 心跳机制可配置
- [x] 故障转移可自动触发
- [x] 5 种 SkillConnector 可正常工作
- [x] 4 种路由策略已实现

---

## 七、后续工作

| 任务 | 状态 | 说明 |
|------|------|------|
| 集成 OfflineManager 到 CapRouter | 待完成 | 离线模式支持 |
| 实现 LLM 集成层 | 待完成 | 使用 MultiLlmAdapterApi |
| 添加单元测试 | 待完成 | 测试覆盖 |

---

## 八、参考文档

### 8.1 SDK 协作文档

- [SCENE_ENGINE_COLLABORATION.md](file:///E:/github/ooder-sdk/agent-sdk/SCENE_ENGINE_COLLABORATION.md) - SDK 团队编制

### 8.2 架构检查报告

- [ARCHITECTURE_CHECK_REPORT.md](file:///e:/github/ooder-sdk/scene-engine/docs/ARCHITECTURE_CHECK_REPORT.md)

---

## 九、联系方式

- **Scene-Engine Team**: scene-engine@ooder.cn
- **SDK Team**: sdk@ooder.cn
- **文档位置**: `scene-engine/docs/SDK_COLLABORATION_REQUEST_SCENE_GROUP_INIT.md`

---

**文档结束**

**最后更新**: 2026-03-08  
**SDK 版本**: 2.3.1  
**状态**: ✅ 协作完成
