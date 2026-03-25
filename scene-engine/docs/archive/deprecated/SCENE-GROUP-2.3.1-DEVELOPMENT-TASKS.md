# 场景组 2.3.1 开发指导文档

## 1. 文档说明

**文档类型**: 正式开发指导文档  
**版本**: 1.0  
**创建日期**: 2026-03-19  
**状态**: ✅ 已确认，开始执行

**参考文档**:
- [SCENE-GROUP-2.3.1-CLARIFICATION.md](./SCENE-GROUP-2.3.1-CLARIFICATION.md) - 需求澄清文档
- [SDK-SE-SCENE-GROUP-RELATIONSHIP-ANALYSIS.md](./SDK-SE-SCENE-GROUP-RELATIONSHIP-ANALYSIS.md) - 架构分析

---

## 2. 架构设计

### 2.1 三层架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         业务层 (Business Layer)                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    SE SceneGroup (业务场景组)                         │   │
│  │  • Participant (参与者: USER/AGENT/SUPER_AGENT)                      │   │
│  │  • CapabilityBinding (能力绑定)                                       │   │
│  │  • KnowledgeBinding (知识库绑定)                                      │   │
│  │  • SceneGroupPersistence (持久化)                                     │   │
│  │  • SceneGroupArchive (归档)                                           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    │ SceneGroupBridge                       │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                  SDK SceneGroup (高可用集群)                          │   │
│  │  • SceneMember (集群成员: PRIMARY/BACKUP)                            │   │
│  │  • Failover (故障转移)                                               │   │
│  │  • Heartbeat (心跳检测)                                              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Agent 层 (Agent Layer)                             │
│  • Agent (代理)                                                              │
│  • AgentType (USER/AGENT/AI)                                                │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心映射关系

| SDK 层 | SE 层 | 映射规则 |
|--------|-------|----------|
| sceneGroupId | sceneGroupId | 共享 ID |
| SceneMember.agentId | Participant.userId | agentId = userId |
| MemberRole.PRIMARY | ParticipantRole.OWNER/MANAGER | 主节点 → 管理者 |
| MemberRole.BACKUP | ParticipantRole.EMPLOYEE/OBSERVER | 备节点 → 员工 |

---

## 3. 开发任务

### 3.1 P0 任务（核心功能）

#### T-001: SceneGroupInitializer 同步创建 SE SceneGroup

**状态**: ✅ 已完成  
**优先级**: P0  
**预估工时**: 4h  
**依赖**: 无

**实现文件**: 
- `net.ooder.scene.core.init.SceneGroupInitializer`
- `net.ooder.scene.group.SceneGroupManager`

**实现要点**:
```java
private void activate(InitContext context) {
    // 1. 创建 SDK SceneGroup
    SceneGroup sdkGroup = sceneGroupManager.create(sceneId, config).join();
    
    // 2. 【新增】同步创建 SE SceneGroup
    net.ooder.scene.group.SceneGroup seGroup = seSceneGroupManager.createSceneGroup(
        sdkGroup.getSceneGroupId(),
        context.getRequest().getSceneId(),
        context.getRequest().getUserId(),
        SceneGroup.CreatorType.USER
    );
    
    // 3. 同步参与者
    for (SceneAgentCore agent : context.getAgents()) {
        Participant participant = createParticipantFromAgent(agent);
        seGroup.addParticipant(participant);
    }
    
    // 4. 根据 sceneType 设置初始状态
    applySceneTypeBehavior(seGroup, context.getRequest().getSceneType());
    
    // 5. 持久化
    sceneGroupPersistence.save(seGroup);
}
```

---

#### T-002: 场景类型差异化处理

**状态**: ✅ 已完成  
**优先级**: P0  
**预估工时**: 6h  
**依赖**: T-001

**实现文件**: 
- `net.ooder.scene.core.init.SceneTypeHandler`

**实现要点**:
```java
public class SceneTypeHandler {
    
    public void applyBehavior(SceneGroup group, SceneType sceneType) {
        switch (sceneType) {
            case AUTO:
                group.activate();
                startHeartbeat(group);
                registerScheduledTasks(group);
                registerEventListeners(group);
                break;
                
            case TRIGGER:
                group.setStatus(SceneGroup.Status.CREATED);
                registerTriggerEndpoint(group);
                break;
                
            case HYBRID:
                HybridConfig config = loadHybridConfig(group);
                if (config.startAsAuto()) {
                    applyAutoBehavior(group);
                }
                registerTriggerEndpoint(group);
                break;
        }
    }
}
```

---

#### T-003: SceneGroupBridge 实现

**状态**: ✅ 已完成（SDK 协作已完成）  
**优先级**: P0  
**预估工时**: 6h  
**依赖**: T-001

**SDK协作文档**: [SDK-COLLABORATION-SCENE-GROUP-BRIDGE.md](./SDK-COLLABORATION-SCENE-GROUP-BRIDGE.md)

**实现文件**: 
- `net.ooder.scene.bridge.SceneGroupBridge`
- `net.ooder.scene.bridge.SceneGroupBridgeImpl`

---

#### T-004: 场景组持久化

**状态**: ✅ 已完成  
**优先级**: P0  
**预估工时**: 8h  
**依赖**: T-001

**实现文件**: 
- `net.ooder.scene.group.persistence.SceneGroupPersistence`
- `net.ooder.scene.group.persistence.SceneGroupMetadata`
- `net.ooder.scene.group.persistence.SceneGroupIndex`

**存储结构**:
```
~/.ooder/scene-groups/
├── {sceneGroupId}/
│   ├── metadata.yaml
│   ├── participants.yaml
│   ├── bindings/
│   │   ├── capabilities.yaml
│   │   └── knowledge.yaml
│   ├── config.yaml
│   └── archives/
└── index.yaml
```

---

#### T-005: 场景组归档功能

**状态**: ✅ 已完成  
**优先级**: P0  
**预估工时**: 6h  
**依赖**: T-004

**实现文件**: 
- `net.ooder.scene.group.archive.SceneGroupArchiver`
- `net.ooder.scene.group.archive.ArchiveMetadata`

---

#### T-006: 附属 Skill 配置集成

**状态**: ✅ 已完成  
**优先级**: P0  
**预估工时**: 8h  
**依赖**: T-001, T-004

**实现文件**: 
- `net.ooder.scene.group.config.SceneGroupConfigInitializer`
- `net.ooder.scene.group.config.LlmConfigProperties`

**配置优先级**:
```
Level 1: 系统默认配置 (classpath:)
Level 2: 环境配置 (config/env/{env}/)
Level 3: 应用配置 (config/app/)
Level 4: 用户配置 (~/.ooder/)
Level 5: 场景组配置 (实例级别)
```

---

### 3.2 P1 任务（重要功能）

| 任务ID | 任务描述 | 状态 | 工时 | 依赖 |
|--------|----------|------|------|------|
| T-007 | SceneSkillToGroupConverter 实现 | ⏳ 待开始 | 6h | T-001, T-002 |
| T-008 | UserSceneGroup 接口实现 | ✅ 已完成 | 6h | T-003 |
| T-009 | 双向同步机制 | ✅ 已完成（SDK 协作已完成） | 8h | T-003, T-008 |
| T-010 | 归档恢复功能 | ✅ 已完成 | 6h | T-005 |

---

### 3.3 P2 任务（优化项）

| 任务ID | 任务描述 | 状态 | 工时 | 依赖 |
|--------|----------|------|------|------|
| T-011 | 场景组索引优化 | ⏳ 待开始 | 4h | T-004 |
| T-012 | 性能优化 - 大规模场景组 | ⏳ 待开始 | 8h | T-004 |
| T-013 | 配置热更新 | ⏳ 待开始 | 4h | T-006 |

---

## 4. 任务依赖关系

```
T-001 (SceneGroupInitializer 同步) ─────────────────────────────────────┐
    │                                                                   │
    ├── T-002 (场景类型差异化)                                          │
    │       │                                                           │
    │       └── T-007 (SceneSkillToGroupConverter)                      │
    │                                                                   │
    ├── T-003 (SceneGroupBridge) ←── 需要SDK协作                        │
    │       │                                                           │
    │       ├── T-008 (UserSceneGroup)                                  │
    │       │       │                                                   │
    │       └── T-009 (双向同步)                                        │
    │                                                                   │
    └── T-004 (持久化) ─────────────────────────────────────────────────┤
            │                                                           │
            ├── T-005 (归档功能)                                        │
            │       │                                                   │
            │       └── T-010 (归档恢复)                                │
            │                                                           │
            ├── T-006 (配置集成)                                        │
            │       │                                                   │
            │       └── T-013 (配置热更新)                              │
            │                                                           │
            ├── T-011 (索引优化)                                        │
            │       │                                                   │
            │       └── T-012 (性能优化)                                │
            │                                                           │
            └───────────────────────────────────────────────────────────┘
```

---

## 5. 验收标准

### 5.1 功能验收

- [x] 场景组创建后 SE SceneGroup 同步创建
- [x] AUTO/TRIGGER/HYBRID 三种类型行为正确
- [x] 场景组持久化后可恢复
- [x] 归档功能正确保留参与者和绑定
- [x] 附属 Skill 配置正确集成
- [x] SceneGroupBridge 健康检查接口已实现

### 5.2 性能验收

- [ ] 场景组创建耗时 < 500ms
- [ ] 持久化写入耗时 < 200ms
- [ ] 归档操作耗时 < 1s
- [ ] 支持 1000+ 场景组并发

### 5.3 稳定性验收

- [ ] 无内存泄漏
- [ ] 无死锁
- [ ] 异常情况正确处理
- [ ] 日志完整

---

## 6. 风险与注意事项

### 6.1 技术风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| SDK SceneGroup 接口变更 | 高 | 封装适配层，隔离变化 |
| 文件存储并发问题 | 中 | 使用文件锁，支持并发读写 |
| 配置合并冲突 | 中 | 明确优先级规则，记录合并日志 |

### 6.2 注意事项

1. **配置初始化顺序**: 必须严格按照 Level 1 → Level 5 的顺序加载配置
2. **归档数据完整性**: 归档前必须确保数据一致性
3. **双向同步幂等性**: 同步操作必须支持幂等，避免重复同步
4. **类型安全**: 配置值必须符合预期类型，否则使用默认值

---

**文档状态**: ✅ 已确认  
**最后更新**: 2026-03-19  
**版本**: 1.0
