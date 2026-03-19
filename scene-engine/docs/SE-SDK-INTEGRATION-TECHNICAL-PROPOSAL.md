# SE/SDK 集成技术方案

## 1. 文档评审与问题分析

### 1.1 当前集成指南的问题

| 问题 | 影响 | 建议 |
|------|------|------|
| 接口定义在 MVP 项目，SE 团队无法直接修改 | 协作困难 | 接口应定义在 SDK 或共享模块 |
| SE SDK 依赖未发布到 Maven | 无法编译 | 需要先发布 SDK |
| DTO 映射逻辑复杂 | 维护成本高 | 使用 MapStruct 自动生成 |
| 缺乏错误处理和回退策略 | 系统不稳定 | 需要熔断和降级机制 |
| 没有性能指标和监控 | 无法优化 | 需要埋点和监控 |

### 1.2 架构合理性评估

**优点：**
- 分层清晰：Controller -> Service -> Adapter -> SDK
- 混合实现支持回退，保证可用性
- DTO 分离，前后端解耦

**缺点：**
- 过多层级导致性能损耗
- DTO 转换增加复杂度
- SDK 和 Memory 实现行为可能不一致

## 2. 团队职责划分

### 2.1 SE 团队职责

| 任务 | 优先级 | 工作量 | 依赖 |
|------|--------|--------|------|
| 发布 SE SDK 到 Maven 仓库 | P0 | 4h | 无 |
| 提供 SDK 连接配置文档 | P0 | 2h | 无 |
| 实现 DTO 转换工具类 | P1 | 8h | SDK 发布 |
| 实现 SceneSdkAdapterImpl | P1 | 16h | SDK 发布 |
| 提供 SDK 健康检查接口 | P1 | 4h | SDK 发布 |
| 编写 SDK 集成测试 | P2 | 8h | Adapter 实现 |
| 性能优化和调优 | P2 | 8h | 集成测试 |

### 2.2 SDK/架构团队职责

| 任务 | 优先级 | 工作量 | 依赖 |
|------|--------|--------|------|
| 定义共享接口模块 | P0 | 8h | 无 |
| 设计 DTO 映射规范 | P0 | 4h | 无 |
| 提供 MapStruct 映射配置 | P1 | 4h | DTO 规范 |
| 实现通用错误处理框架 | P1 | 8h | 无 |
| 设计熔断降级机制 | P1 | 8h | 错误处理框架 |
| 提供监控埋点规范 | P2 | 4h | 无 |
| 编写架构集成文档 | P2 | 4h | 所有设计完成 |

### 2.3 MVP 团队职责

| 任务 | 优先级 | 工作量 | 依赖 |
|------|--------|--------|------|
| 评审接口设计 | P0 | 4h | SE 团队提供初稿 |
| 调整 Service 层代码 | P1 | 8h | 接口确定 |
| 集成测试验证 | P2 | 8h | SE SDK 实现完成 |
| 性能测试 | P2 | 4h | 集成测试通过 |

## 3. 技术方案详细设计

### 3.1 架构调整建议

```
当前架构（问题）：
MVP Project
├── SceneSdkAdapter (接口定义在MVP)
├── SceneService / SceneGroupService (接口定义在MVP)
└── 各种DTO (定义在MVP)
        ↓ 依赖
SE SDK (发布到Maven)
    └── SE 实现

问题：SE 团队无法修改接口，协作效率低

建议架构（解决方案）：
Shared API Module (新模块，发布到Maven)
├── SceneSdkAdapter (接口)
├── SceneService / SceneGroupService (接口)
└── DTOs (共享)
        ↓ 依赖
MVP Project                    SE SDK (发布到Maven)
├── 实现 Service               └── 实现 Adapter
└── 使用 DTOs                    └── 使用 DTOs
```

### 3.2 模块依赖关系

```xml
<!-- Shared API Module (ooder-mvp-api) -->
<artifactId>ooder-mvp-api</artifactId>
<version>2.3.1</version>

<!-- MVP Project 依赖 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-mvp-api</artifactId>
</dependency>

<!-- SE SDK 依赖 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-mvp-api</artifactId>
</dependency>
```

### 3.3 核心接口设计

#### 3.3.1 SceneSdkAdapter（SE 团队实现）

```java
package net.ooder.mvp.api.scene.sdk;

import net.ooder.mvp.api.scene.dto.*;

/**
 * SE SDK 适配器接口
 * 
 * 由 SE 团队实现，MVP 项目通过此接口调用 SE SDK 功能
 */
public interface SceneSdkAdapter {
    
    /**
     * 检查 SDK 是否可用
     */
    SdkHealthStatus healthCheck();
    
    // 场景组管理
    SceneGroupDTO createSceneGroup(SceneGroupCreateRequest request);
    SceneGroupDTO getSceneGroup(String sceneGroupId);
    PageResult<SceneGroupDTO> listSceneGroups(SceneGroupQuery query);
    SceneGroupDTO updateSceneGroup(String sceneGroupId, SceneGroupUpdateRequest request);
    void deleteSceneGroup(String sceneGroupId);
    
    // 状态管理
    void activateSceneGroup(String sceneGroupId);
    void deactivateSceneGroup(String sceneGroupId);
    
    // 参与者管理
    void joinSceneGroup(String sceneGroupId, ParticipantJoinRequest request);
    void leaveSceneGroup(String sceneGroupId, String participantId);
    PageResult<ParticipantDTO> listParticipants(String sceneGroupId, PageQuery query);
    
    // 能力绑定
    CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindRequest request);
    void unbindCapability(String sceneGroupId, String bindingId);
    PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, PageQuery query);
    
    // 知识库绑定
    void bindKnowledgeBase(String sceneGroupId, KnowledgeBaseBindRequest request);
    void unbindKnowledgeBase(String sceneGroupId, String kbId);
    List<KnowledgeBaseBindingDTO> listKnowledgeBaseBindings(String sceneGroupId);
}
```

#### 3.3.2 SceneService（MVP 团队实现）

```java
package net.ooder.mvp.api.scene.service;

import net.ooder.mvp.api.scene.dto.*;

/**
 * 场景服务接口
 * 
 * 由 MVP 团队实现，提供场景管理功能
 */
public interface SceneService {
    
    // CRUD 操作
    SceneDefinitionDTO create(SceneCreateRequest request);
    SceneDefinitionDTO get(String sceneId);
    PageResult<SceneDefinitionDTO> list(SceneQuery query);
    SceneDefinitionDTO update(String sceneId, SceneUpdateRequest request);
    void delete(String sceneId);
    
    // 状态管理
    void activate(String sceneId);
    void deactivate(String sceneId);
    
    // 批量操作
    List<SceneDefinitionDTO> batchCreate(List<SceneCreateRequest> requests);
    void batchDelete(List<String> sceneIds);
}
```

#### 3.3.3 SceneGroupService（MVP 团队实现）

```java
package net.ooder.mvp.api.scene.service;

import net.ooder.mvp.api.scene.dto.*;

/**
 * 场景组服务接口
 * 
 * 由 MVP 团队实现，提供场景组管理功能
 */
public interface SceneGroupService {
    
    // 生命周期管理
    SceneGroupDTO create(SceneGroupCreateRequest request);
    SceneGroupDTO get(String sceneGroupId);
    PageResult<SceneGroupDTO> list(SceneGroupQuery query);
    SceneGroupDTO update(String sceneGroupId, SceneGroupUpdateRequest request);
    void destroy(String sceneGroupId);
    
    // 状态管理
    void activate(String sceneGroupId);
    void deactivate(String sceneGroupId);
    
    // 参与者管理
    void join(String sceneGroupId, ParticipantJoinRequest request);
    void leave(String sceneGroupId, String participantId);
    PageResult<ParticipantDTO> listParticipants(String sceneGroupId, PageQuery query);
    void changeRole(String sceneGroupId, String participantId, String newRole);
    
    // 能力绑定
    CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindRequest request);
    void unbindCapability(String sceneGroupId, String bindingId);
    PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, PageQuery query);
    
    // 快照管理
    SceneSnapshotDTO createSnapshot(String sceneGroupId);
    List<SceneSnapshotDTO> listSnapshots(String sceneGroupId);
    void restoreSnapshot(String sceneGroupId, String snapshotId);
    
    // 知识库绑定
    void bindKnowledgeBase(String sceneGroupId, KnowledgeBaseBindRequest request);
    void unbindKnowledgeBase(String sceneGroupId, String kbId);
    List<KnowledgeBaseBindingDTO> listKnowledgeBaseBindings(String sceneGroupId);
    
    // LLM 配置
    Map<String, Object> getLlmConfig(String sceneGroupId);
    void updateLlmConfig(String sceneGroupId, Map<String, Object> config);
}
```

### 3.4 DTO 设计

```java
// 基础 DTO
public class PageQuery {
    private int pageNum = 1;
    private int pageSize = 10;
    private String sortBy;
    private String sortOrder = "DESC";
}

public class PageResult<T> {
    private List<T> list;
    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
}

// SDK 健康状态
public class SdkHealthStatus {
    private boolean available;
    private String status; // UP, DOWN, DEGRADED
    private String message;
    private long responseTime;
    private Map<String, Object> details;
}
```

### 3.5 错误处理与熔断机制

```java
/**
 * SDK 调用异常
 */
public class SdkException extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;
    
    public SdkException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}

/**
 * 熔断器配置
 */
@Component
@ConfigurationProperties(prefix = "ooder.se.sdk.circuit-breaker")
public class CircuitBreakerConfig {
    private boolean enabled = true;
    private int failureThreshold = 5; // 失败次数阈值
    private int slowCallThreshold = 3; // 慢调用阈值
    private long slowCallDurationThreshold = 5000; // 慢调用时间阈值（毫秒）
    private long openDuration = 30000; // 熔断持续时间（毫秒）
    private long halfOpenMaxCalls = 3; // 半开状态最大调用数
}
```

### 3.6 监控与埋点

```java
/**
 * SDK 调用指标收集器
 */
@Component
public class SdkMetricsCollector {
    
    private final MeterRegistry meterRegistry;
    private final Counter callCounter;
    private final Timer responseTimer;
    private final Counter errorCounter;
    
    public SdkMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.callCounter = Counter.builder("sdk.calls.total")
            .description("Total SDK calls")
            .register(meterRegistry);
        this.responseTimer = Timer.builder("sdk.calls.duration")
            .description("SDK call duration")
            .register(meterRegistry);
        this.errorCounter = Counter.builder("sdk.errors.total")
            .description("Total SDK errors")
            .register(meterRegistry);
    }
    
    public void recordCall(String method) {
        callCounter.increment();
    }
    
    public void recordError(String method, String errorCode) {
        errorCounter.increment();
    }
    
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordDuration(Timer.Sample sample) {
        sample.stop(responseTimer);
    }
}
```

## 4. 实施计划

### 4.1 阶段一：基础设施（Week 1）

| 任务 | 负责团队 | 工作量 | 产出物 |
|------|---------|--------|--------|
| 创建共享 API 模块 | SDK/架构 | 8h | ooder-mvp-api 模块 |
| 迁移接口到共享模块 | SDK/架构 | 8h | 迁移后的接口 |
| 发布 SE SDK 到 Maven | SE | 4h | se-sdk 可用 |
| 更新 MVP 依赖 | MVP | 4h | 使用共享模块 |

### 4.2 阶段二：核心实现（Week 2-3）

| 任务 | 负责团队 | 工作量 | 产出物 |
|------|---------|--------|--------|
| 实现 SceneSdkAdapter | SE | 16h | 完整适配器实现 |
| 实现 DTO 映射 | SE | 8h | MapStruct 映射器 |
| 实现错误处理 | SE | 8h | 异常处理体系 |
| 实现熔断机制 | SDK/架构 | 8h | 熔断器组件 |
| 调整 MVP Service | MVP | 8h | 适配新接口 |

### 4.3 阶段三：测试与优化（Week 4）

| 任务 | 负责团队 | 工作量 | 产出物 |
|------|---------|--------|--------|
| 编写集成测试 | SE + MVP | 8h | 测试用例 |
| 性能测试 | SDK/架构 | 8h | 性能报告 |
| 编写使用文档 | SE | 4h | 集成文档 |
| 代码审查 | 所有团队 | 4h | 审查报告 |

## 5. 风险评估与缓解

### 5.1 风险列表

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| SE SDK 发布延迟 | 中 | 高 | 先使用本地依赖开发 |
| 接口变更导致不兼容 | 中 | 高 | 使用版本控制，向后兼容 |
| DTO 映射性能问题 | 低 | 中 | 使用对象池，缓存映射器 |
| SDK 网络不稳定 | 中 | 高 | 实现熔断降级机制 |
| 团队协作不畅 | 低 | 高 | 每日站会，及时沟通 |

### 5.2 应急预案

1. **SDK 不可用时的回退方案**
   - 保持内存实现作为备份
   - 自动切换到内存模式
   - 记录降级事件

2. **数据不一致时的处理**
   - 定期数据同步任务
   - 冲突检测和解决策略
   - 人工介入流程

## 6. 结论与建议

### 6.1 总体评估

**当前方案的合理性：7/10**

- 分层架构设计合理
- 回退机制保证可用性
- 但接口位置不当，协作效率低

### 6.2 关键建议

1. **立即执行**：创建共享 API 模块，将接口迁移到独立模块
2. **本周完成**：SE 团队发布 SDK 到 Maven 仓库
3. **分阶段实施**：按照 4 周计划分阶段交付
4. **加强协作**：建立每日站会机制，及时同步进度

### 6.3 下一步行动

1. 召集三方会议，确认技术方案
2. 创建共享 API 模块仓库
3. SE 团队准备 SDK 发布
4. 开始第一阶段开发

---

**文档版本**: 1.0  
**创建日期**: 2026-03-19  
**作者**: 技术架构组
