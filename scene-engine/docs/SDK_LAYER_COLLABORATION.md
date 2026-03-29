# SDK 分层协作关系说明

> **文档版本**: v1.0  
> **创建日期**: 2026-03-28  
> **状态**: 待评审

---

## 一、分层架构总览

```
┌─────────────────────────────────────────────────────────────────┐
│                         MVP 层                                   │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  MVP Skill Scene Module                                      │ │
│  │  - TodoController                                            │ │
│  │  - NotificationController                                    │ │
│  │  - ProcedureController                                       │ │
│  │  - FusionController                                          │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │ 调用接口
                              │
┌─────────────────────────────────────────────────────────────────┐
│                        SE SDK 层                                 │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  SE SDK 3.0.2 (已完成)                                        │ │
│  │  - TodoService                                               │ │
│  │  - NotificationService                                       │ │
│  │  - SceneGroupPermissionService                               │ │
│  │  - SceneGroupBridge 扩展                                      │ │
│  └─────────────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  SE SDK 3.1.0 (待开发)                                        │ │
│  │  - EnterpriseProcedureService                                │ │
│  │  - FusionTemplateService                                     │ │
│  │  - CompletenessEvaluator                                     │ │
│  │  - EnhancedActivationStepExecutor                            │ │
│  │  - FlowVisualizationService                                  │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │ 依赖 API
                              │
┌─────────────────────────────────────────────────────────────────┐
│                       Agent SDK 层                               │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  Agent SDK 2.3.1 (已完成)                                     │ │
│  │  - AbstractSceneAgent                                        │ │
│  │  - CapRegistry                                               │ │
│  │  - CapabilityType                                            │ │
│  │  - SceneManager                                              │ │
│  │  - SceneGroupManager                                         │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、各层职责分工

### 2.1 Agent SDK 层 (已完成)

**版本**: 2.3.1  
**状态**: ✅ 已打包完成，可交付使用  
**职责**: 提供底层 Agent 能力和场景管理 API

| 组件 | 职责 | 文件路径 |
|------|------|----------|
| AbstractSceneAgent | 场景代理抽象基类 | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\agent\support\AbstractSceneAgent.java` |
| CapabilityType | 能力类型枚举定义 | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\capability\CapabilityType.java` |
| CapRegistry | 能力注册表 | Agent SDK Core |
| SceneManager | 场景生命周期管理 | Agent SDK Core |
| SceneGroupManager | 场景组管理 | Agent SDK Core |

**关键能力类型**:
```java
public enum CapabilityType {
    DRIVER,           // 驱动类型
    SERVICE,          // 服务类型
    MANAGEMENT,       // 管理类型
    AI,               // AI类型
    STORAGE,          // 存储类型
    COMMUNICATION,    // 通信类型
    SECURITY,         // 安全类型
    MONITORING,       // 监控类型
    SKILL,            // 技能类型
    SCENE,            // 场景类型 - 自驱型SuperAgent能力
    SCENE_GROUP,      // 场景组类型
    CAPABILITY_CHAIN, // 能力链类型
    ATOMIC,           // 原子能力
    COMPOSITE,        // 组合能力
    COLLABORATIVE,    // 协作能力
    CUSTOM            // 自定义类型
}
```

### 2.2 SE SDK 层 (当前工作)

#### 2.2.1 SE SDK 3.0.2 (已完成)

**状态**: ✅ 已完成  
**职责**: 提供待办、通知、权限等基础服务

| 组件 | 职责 | 文件路径 |
|------|------|----------|
| TodoService | 待办服务 | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\TodoService.java` |
| NotificationService | 通知服务 | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\skill\notification\NotificationService.java` |
| SceneGroupPermissionService | 场景组权限服务 | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\security\SceneGroupPermissionService.java` |
| SceneGroupBridge | 桥接扩展 | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\bridge\SceneGroupBridge.java` |
| TodoEvent | 待办事件 | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\event\todo\TodoEvent.java` |

#### 2.2.2 SE SDK 3.1.0 (待开发)

**状态**: ⏳ 待执行  
**职责**: 提供企业规范流程、融合模板等高级服务

| 组件 | 职责 | 优先级 |
|------|------|--------|
| EnterpriseProcedureService | 企业规范流程服务 | P0 |
| FusionTemplateService | 融合模板服务 | P0 |
| CompletenessEvaluator | 完善度评估器 | P1 |
| EnhancedActivationStepExecutor | 增强激活步骤执行器 | P2 |
| FlowVisualizationService | 流程可视化服务 | P2 |

### 2.3 MVP 层 (待适配)

**职责**: 调用 SE SDK 接口，实现业务逻辑

| 任务 | 依赖 | 状态 |
|------|------|------|
| 更新 pom.xml 依赖到 SE SDK 3.0.2 | SE SDK 3.0.2 发布 | 待执行 |
| 适配 TodoService 接口 | SE SDK TodoService | 待执行 |
| 适配 NotificationService 接口 | SE SDK NotificationService | 待执行 |
| 开发企业规范流程管理功能 | SE SDK 3.1.0 | 待规划 |

---

## 三、协作关系矩阵

| 提供方 | 消费方 | 提供内容 | 协作文档 |
|--------|--------|----------|----------|
| Agent SDK | SE SDK | AbstractSceneAgent, CapRegistry, CapabilityType | `e:\github\ooder-sdk\agent-sdk\SCENE_ENGINE_COLLABORATION.md` |
| SE SDK | MVP | TodoService, NotificationService, PermissionService | `e:\github\ooder-sdk\scene-engine\docs\se-sdk-mvp-collaboration.md` |
| SE SDK | MVP | EnterpriseProcedureService, FusionTemplateService | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_SDK_3.1.0_EXTENSION_COLLABORATION.md` |

---

## 四、SE SDK 任务清单确认

### 4.1 SE SDK 3.0.2 任务 (已完成)

| 任务 | 状态 | 完成日期 |
|------|------|----------|
| TodoService 接口及实现 | ✅ 已完成 | 2026-03-28 |
| TodoDTO/Query/Status/Type 模型 | ✅ 已完成 | 2026-03-28 |
| NotificationService 扩展 | ✅ 已完成 | 2026-03-28 |
| SceneGroupPermissionService | ✅ 已完成 | 2026-03-28 |
| SceneGroupBridge 扩展 | ✅ 已完成 | 2026-03-28 |
| SceneEventType 待办事件扩展 | ✅ 已完成 | 2026-03-28 |
| TodoEvent 事件类 | ✅ 已完成 | 2026-03-28 |
| 编译验证 | ✅ 已通过 | 2026-03-28 |

### 4.2 SE SDK 3.1.0 任务 (待执行)

**任务清单文件**: `e:\github\ooder-sdk\scene-engine\docs\refactoring\SE_SDK_3.1.0_TASK_LIST.md`

| Phase | 任务名称 | 优先级 | 预估工时 | 里程碑 |
|-------|----------|--------|----------|--------|
| Phase 1 | 企业规范流程数据模型 | P0 | 8d | M1 Alpha |
| Phase 2 | 融合模板机制 | P0 | 11.5d | M1 Alpha |
| Phase 3 | 完善度评估体系 | P1 | 6d | M2 Beta |
| Phase 4 | 增强激活步骤执行器 | P2 | 6d | M2 Beta |
| Phase 5 | 可视化流程配置 | P2 | 6.5d | M3 Release |
| Phase 6 | 集成测试与文档 | P1 | 5d | M3 Release |
| **总计** | | | **43d** | |

---

## 五、依赖关系图

```
Agent SDK 2.3.1 (已完成)
    │
    ├── AbstractSceneAgent ──────────────────┐
    ├── CapRegistry ─────────────────────────┤
    ├── CapabilityType ──────────────────────┤
    │                                         │
    ▼                                         ▼
SE SDK 3.0.2 (已完成)                    SE SDK 3.1.0 (待开发)
    │                                         │
    ├── TodoService                           ├── EnterpriseProcedureService
    ├── NotificationService                   ├── FusionTemplateService
    ├── SceneGroupPermissionService           ├── CompletenessEvaluator
    ├── SceneGroupBridge                      ├── EnhancedActivationStepExecutor
    └── TodoEvent                             └── FlowVisualizationService
    │                                         │
    ▼                                         ▼
MVP 层 ─────────────────────────────────────────────────────────
    │
    ├── TodoController (适配 3.0.2)
    ├── NotificationController (适配 3.0.2)
    ├── ProcedureController (待 3.1.0)
    └── FusionController (待 3.1.0)
```

---

## 六、版本发布计划

| 版本 | 发布日期 | 内容 | 状态 |
|------|----------|------|------|
| Agent SDK 2.3.1 | 2026-03-08 | 底层 Agent 能力 | ✅ 已发布 |
| SE SDK 3.0.2 | 2026-03-28 | TodoService, NotificationService | ✅ 已完成 |
| SE SDK 3.1.0 M1 Alpha | 2026-04-15 | Phase 1 + Phase 2 | ⏳ 待开始 |
| SE SDK 3.1.0 M2 Beta | 2026-05-01 | Phase 3 + Phase 4 | ⏳ 待开始 |
| SE SDK 3.1.0 M3 Release | 2026-05-15 | Phase 5 + Phase 6 | ⏳ 待开始 |

---

## 七、文件路径汇总

| 文档/文件 | 绝对路径 |
|-----------|---------|
| Agent SDK 协作文档 | `e:\github\ooder-sdk\agent-sdk\SCENE_ENGINE_COLLABORATION.md` |
| AbstractSceneAgent | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\agent\support\AbstractSceneAgent.java` |
| CapabilityType | `e:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\api\capability\CapabilityType.java` |
| SE SDK 任务清单 | `e:\github\ooder-sdk\scene-engine\docs\refactoring\SE_SDK_3.1.0_TASK_LIST.md` |
| SE SDK 协作文档 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_SDK_3.1.0_EXTENSION_COLLABORATION.md` |
| SE-MVP 协作文档 | `e:\github\ooder-sdk\scene-engine\docs\se-sdk-mvp-collaboration.md` |

---

**文档维护者**: SE SDK Team  
**最后更新**: 2026-03-28
