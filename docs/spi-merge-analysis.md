# SPI 接口合并分析报告

## 执行日期
2026-04-17

## 分析目标
检查项目中所有 SPI 接口，识别哪些需要合并到 `ooder-spi-core` 模块中

---

## 一、当前 SPI 分布概览

### 1.1 已合并到 ooder-spi-core 的 SPI (✅)

| 接口名 | 包路径 | 说明 |
|--------|--------|------|
| `ImService` | `net.ooder.spi.im` | IM 服务接口 |
| `ImDeliveryDriver` | `net.ooder.spi.im` | IM 投递驱动 |
| `InboundHandler` | `net.ooder.spi.im.handler` | 入站处理器 |
| `RagEnhanceDriver` | `net.ooder.spi.rag` | RAG 增强驱动 |
| `WorkflowDriver` | `net.ooder.spi.workflow` | 工作流驱动 |
| `LlmManager` | `net.ooder.spi.llm` | LLM 管理器 |
| `LlmProvider` | `net.ooder.spi.llm` | LLM 提供者 |
| `LlmStreamHandler` | `net.ooder.spi.llm` | LLM 流处理器 |
| `KnowledgeService` | `net.ooder.spi.knowledge` | 知识服务 |
| `KnowledgeClassifier` | `net.ooder.spi.classifier` | 知识分类器 |
| `DictService` | `net.ooder.spi.dict` | 字典服务 |
| `OrgService` | `net.ooder.spi.org` | 组织服务 |
| `UnifiedMessagingService` | `net.ooder.spi.messaging` | 统一消息服务 |
| `UnifiedSessionService` | `net.ooder.spi.messaging` | 统一会话服务 |
| `UnifiedWebSocketService` | `net.ooder.spi.messaging` | 统一 WebSocket 服务 |
| `MessageStreamHandler` | `net.ooder.spi.messaging` | 消息流处理器 |
| `SpiServices` | `net.ooder.spi.facade` | SPI 服务门面 |
| `PageResult` | `net.ooder.spi.core` | 分页结果 |

**总计**: 18 个接口/类

---

### 1.2 分散在其他模块的 SPI (⚠️ 需要合并)

#### 1.2.1 skill-common 模块中的 SPI (❌ 未合并)

**Agent 相关**:
| 接口名 | 包路径 | 优先级 | 说明 |
|--------|--------|--------|------|
| `AgentStorage` | `spi/agent/` | P2 | Agent 存储 |
| `AgentSessionStorage` | `spi/agent/` | P2 | 会话存储 |
| `AgentMessageStorage` | `spi/agent/` | P2 | 消息存储 |

**Knowledge 相关**:
| 接口名 | 包路径 | 优先级 | 说明 |
|--------|--------|--------|------|
| `KnowledgeBaseStorage` | `spi/knowledge/` | P1 | 知识库存储 |
| `VectorStoreProvider` | `spi/knowledge/` | P1 | 向量存储提供者 |
| `EmbeddingProvider` | `spi/knowledge/` | P1 | 嵌入服务提供者 |

**LLM 相关**:
| 接口名 | 包路径 | 优先级 | 说明 |
|--------|--------|--------|------|
| `LLMServiceProvider` | `spi/llm/` | P1 | LLM 服务提供者 |
| `LlmConfigStorage` | `spi/llm/` | P1 | LLM 配置存储 |
| `ConversationStorage` | `spi/llm/` | P1 | 对话存储 |

**Storage 相关**:
| 接口名 | 包路径 | 优先级 | 说明 |
|--------|--------|--------|------|
| `SceneGroupStorage` | `spi/storage/` | P0 | 场景群组存储 ✅ 已完成 |
| `SceneGroupData` | `spi/storage/` | P0 | 场景群组数据 |
| `PageResult` | `spi/storage/` | P0 | 分页结果 (重复定义) |

**其他服务**:
| 接口名 | 包路径 | 优先级 | 说明 |
|--------|--------|--------|------|
| `UserService` | `spi/` | P2 | 用户服务 |
| `StorageService` | `spi/` | P2 | 存储服务 |
| `PermissionService` | `spi/` | P2 | 权限服务 |
| `OrganizationService` | `spi/` | P2 | 组织服务 |
| `MessageService` | `spi/` | P2 | 消息服务 |
| `CalendarService` | `spi/` | P2 | 日历服务 |
| `TodoSyncService` | `spi/` | P2 | 待办同步服务 |
| `PlatformBindService` | `spi/` | P2 | 平台绑定服务 |
| `OrgSyncService` | `spi/` | P2 | 组织同步服务 |
| `ConfigService` | `spi/` | P2 | 配置服务 |
| `AuditService` | `spi/` | P2 | 审计服务 |
| `SceneServices` | `spi/` | P2 | 场景服务 |
| `ImService` | `spi/` | P2 | IM 服务 (重复定义) |

**模型类**:
| 类名 | 包路径 | 说明 |
|------|--------|------|
| `UserInfo` | `spi/user/` | 用户信息 |
| `DepartmentInfo` | `spi/org/` | 部门信息 |
| `EventInfo` | `spi/calendar/` | 事件信息 |
| `TimeSlot` | `spi/calendar/` | 时间段 |
| `TodoInfo` | `spi/todo/` | 待办信息 |
| `TodoStatus` | `spi/todo/` | 待办状态 |
| `BindInfo` | `spi/bind/` | 绑定信息 |
| `BindStatus` | `spi/bind/` | 绑定状态 |
| `QrCodeInfo` | `spi/bind/` | 二维码信息 |
| `OrgUserInfo` | `spi/orgsync/` | 组织用户信息 |
| `OrgDepartmentInfo` | `spi/orgsync/` | 组织部门信息 |
| `SyncResult` | `spi/orgsync/` | 同步结果 |
| `MessageType` | `spi/im/` | 消息类型 |
| `MessageContent` | `spi/im/` | 消息内容 |
| `SendResult` | `spi/im/` | 发送结果 |
| `Message` | `spi/message/` | 消息 |
| `SceneNotification` | `spi/message/` | 场景通知 |
| `SendMessageResult` | `spi/message/` | 发送消息结果 |
| `AuditEvent` | `spi/audit/` | 审计事件 |

**总计**: 约 45 个接口/类

#### 1.2.2 scene-engine 模块中的 SPI (❌ 未合并)

| 接口名 | 包路径 | 说明 |
|--------|--------|------|
| `ServiceLocator` | `core/spi/` | 服务定位器 |
| `MenuGenerator` | `core/spi/` | 菜单生成器 |
| `ExtensionPointRegistry` | `core/spi/` | 扩展点注册中心 |
| `DependencyChecker` | `core/spi/` | 依赖检查器 |
| `ActivationStepExecutor` | `core/spi/` | 激活步骤执行器 |
| `SceneEngineServiceProvider` | `spi/` | 场景引擎服务提供者 |
| `SceneServiceFactory` | `spi/` | 场景服务工厂 |
| `SceneServices` | `spi/` | 场景服务 |
| `StorageProvider` | `spi/` | 存储提供者 |
| `LlmProvider` | `spi/` | LLM 提供者 |
| `VectorStore` | `spi/` | 向量存储 |
| `LogStorageProvider` | `log/spi/` | 日志存储提供者 |
| `ActivationEventListener` | `core/activation/spi/` | 激活事件监听器 |
| `NetworkActionExecutor` | `core/activation/spi/` | 网络动作执行器 |
| `UserService` | `core/spi/user/` | 用户服务 (重复) |
| `UserInfo` | `core/spi/user/` | 用户信息 (重复) |
| `OrganizationService` | `core/spi/org/` | 组织服务 (重复) |
| `OrganizationInfo` | `core/spi/org/` | 组织信息 |
| `DepartmentInfo` | `core/spi/org/` | 部门信息 (重复) |

**总计**: 19 个接口/类

#### 1.2.3 agent-sdk 模块中的 SPI (❌ 未合并)

| 接口名 | 包路径 | 说明 |
|--------|--------|------|
| `ProtocolProvider` | `nexus/spi/` | 协议提供者 |
| `LlmProviderSpi` | `skills/spi/` | LLM 提供者 SPI |

**总计**: 2 个接口

#### 1.2.4 skill-drivers/skill-spi 模块中的 SPI (❌ 未合并)

| 接口名 | 包路径 | 说明 |
|--------|--------|------|
| `LlmService` | `spi/llm/` | LLM 服务 (重复) |
| `OrgService` | `spi/org/` | 组织服务 (重复) |
| `LlmConfigDTO` | `spi/llm/` | LLM 配置 DTO |
| `LlmModelDTO` | `spi/llm/` | LLM 模型 DTO |
| `LlmProviderDTO` | `spi/llm/` | LLM 提供者 DTO |
| `OrgUserDTO` | `spi/org/` | 组织用户 DTO |

**总计**: 6 个接口/类

#### 1.2.5 skill-system/skill-menu 模块中的 SPI (❌ 未合并 - 疑似重复)

| 接口/类名 | 说明 |
|-----------|------|
| `VectorStoreProvider` | 向量存储提供者 (重复) |
| `EmbeddingProvider` | 嵌入提供者 (重复) |
| `SceneServices` | 场景服务 (重复) |
| `TodoSyncService` | 待办同步服务 (重复) |
| `MessageContent` | 消息内容 (重复) |
| `SendResult` | 发送结果 (重复) |
| `StorageService` | 存储服务 (重复) |
| `MessageService` | 消息服务 (重复) |

**总计**: 8 个接口/类 (疑似重复文件)

---

## 二、问题分析

### 2.1 重复定义问题 ⚠️

以下接口在多个模块中重复定义：

| 接口名 | 出现位置 | 建议 |
|--------|----------|------|
| `PageResult` | ooder-spi-core, skill-common | 统一使用 ooder-spi-core 版本 |
| `ImService` | ooder-spi-core, skill-common | 统一使用 ooder-spi-core 版本 |
| `UserService` | skill-common, scene-engine | 需要合并 |
| `OrganizationService` | skill-common, scene-engine | 需要合并 |
| `VectorStoreProvider` | skill-common, skill-menu | 统一使用 skill-common 版本 |
| `EmbeddingProvider` | skill-common, skill-menu | 统一使用 skill-common 版本 |
| `SceneServices` | skill-common, skill-menu | 统一使用 skill-common 版本 |
| `LlmService` | skill-common, skill-spi | 需要合并 |
| `OrgService` | ooder-spi-core, skill-spi | 需要合并 |

### 2.2 命名不一致问题 ⚠️

| 问题 | 示例 | 建议 |
|------|------|------|
| 包路径不一致 | `spi/llm/` vs `spi/LLM/` | 统一使用小写 |
| 接口命名不一致 | `LLMServiceProvider` vs `LlmProvider` | 统一命名规范 |
| DTO 位置不一致 | 有些在 spi 包，有些在 model 包 | 统一放到 model 子包 |

### 2.3 模块依赖问题 ⚠️

当前依赖关系：
```
ooder-spi-core (基础SPI)
    ↑
skill-common (业务SPI) - 依赖 ooder-spi-core
    ↑
scene-engine (引擎SPI) - 依赖 skill-common
    ↑
agent-sdk (协议SPI) - 依赖 scene-engine
```

**问题**: scene-engine 和 agent-sdk 中定义的 SPI 可能被上层模块依赖，直接合并可能导致循环依赖。

---

## 三、合并建议

### 3.1 第一阶段：合并 skill-common 的 SPI (P0/P1)

**必须合并的接口** (P0):
1. ✅ `SceneGroupStorage` - 已完成
2. `SceneGroupData` - 需要合并

**建议合并的接口** (P1):
1. `KnowledgeBaseStorage`
2. `VectorStoreProvider`
3. `EmbeddingProvider`
4. `LLMServiceProvider`
5. `LlmConfigStorage`
6. `ConversationStorage`

### 3.2 第二阶段：处理重复定义

1. **删除重复文件**:
   - `skill-menu` 模块中的重复 SPI 文件
   - `skill-common` 中的 `PageResult` (使用 ooder-spi-core 版本)
   - `skill-common` 中的 `ImService` (使用 ooder-spi-core 版本)

2. **统一命名**:
   - `LLMServiceProvider` → `LlmServiceProvider`
   - 统一包路径命名规范

### 3.3 第三阶段：处理 scene-engine 和 agent-sdk 的 SPI

**不建议合并到 ooder-spi-core**:
- scene-engine 中的 SPI 属于引擎层，应该保持独立
- agent-sdk 中的 SPI 属于协议层，应该保持独立

**建议做法**:
- 保持 scene-engine 和 agent-sdk 的 SPI 独立
- 通过依赖关系引用，而不是合并

---

## 四、实施计划

### 4.1 立即执行 (今天)

1. ✅ 已合并 `SceneGroupStorage` 到 ooder-spi-core
2. ⬜ 合并 `SceneGroupData` 到 ooder-spi-core
3. ⬜ 删除 `skill-menu` 模块中的重复 SPI 文件

### 4.2 短期执行 (本周)

1. ⬜ 合并 P1 优先级的 SPI 接口
   - KnowledgeBaseStorage
   - VectorStoreProvider
   - EmbeddingProvider
   - LLMServiceProvider
   - LlmConfigStorage
   - ConversationStorage

2. ⬜ 处理重复定义问题
   - 删除重复的 PageResult
   - 删除重复的 ImService
   - 统一命名规范

### 4.3 中期执行 (本月)

1. ⬜ 合并 P2 优先级的 SPI 接口
   - AgentStorage
   - AgentSessionStorage
   - AgentMessageStorage

2. ⬜ 更新所有依赖模块的引用

---

## 五、风险评估

### 5.1 合并风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 破坏现有代码 | 高 | 保持原有接口，添加 @Deprecated |
| 循环依赖 | 中 | 分阶段合并，检查依赖关系 |
| 版本不兼容 | 中 | 统一版本号，同步发布 |

### 5.2 不合并的风险

| 风险 | 影响 | 说明 |
|------|------|------|
| 维护困难 | 高 | 同一接口多处定义 |
| 不一致性 | 高 | 不同模块实现不一致 |
| 重复代码 | 中 | 增加包体积 |

---

## 六、结论

### 6.1 当前状态

- ✅ **ooder-spi-core**: 已包含 18 个核心 SPI 接口
- ⚠️ **skill-common**: 有约 45 个 SPI 接口待合并
- ⚠️ **scene-engine**: 有 19 个 SPI 接口 (建议保持独立)
- ⚠️ **agent-sdk**: 有 2 个 SPI 接口 (建议保持独立)
- ❌ **重复定义**: 发现 9 处重复定义

### 6.2 建议

1. **立即执行**: 删除 `skill-menu` 模块中的重复 SPI 文件
2. **本周执行**: 合并 P0/P1 优先级的 SPI 接口到 ooder-spi-core
3. **保持独立**: scene-engine 和 agent-sdk 的 SPI 保持独立
4. **统一规范**: 建立 SPI 命名和包路径规范

### 6.3 预计工作量

- **删除重复文件**: 0.5 天
- **合并 P0/P1 SPI**: 2-3 天
- **更新依赖引用**: 1-2 天
- **测试验证**: 1-2 天

**总计**: 约 5-7 天

---

**报告生成时间**: 2026-04-17  
**分析人**: Ooder SDK Team
