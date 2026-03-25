# SceneGroup 重构任务分解

## 一、任务概览

### 1.1 总体目标

**增强 SDK 层，简化 SE 层，保留桥接层**

### 1.2 任务分级原则

| 优先级 | 说明 | 时间要求 |
|--------|------|----------|
| **P0** | 核心功能，必须完成 | 1-2 周 |
| **P1** | 重要功能，应该完成 | 2-4 周 |
| **P2** | 增强功能，可以延后 | 4-8 周 |
| **P3** | 优化功能，按需安排 | 8+ 周 |

## 二、Phase 1: SDK 层增强 (P0)

### 2.1 SDK-001: SceneManager 接口增强

**优先级**: P0  
**工作量**: 2-3 天  
**依赖**: 无

**任务描述**:
- 在 `SceneManager` 接口中添加 `getSceneGroup()` 方法
- 在 `SceneManager` 接口中添加 `getUserSceneGroup()` 方法
- 在 `SceneManager` 接口中添加 `getUserSceneGroups()` 方法

**验收标准**:
- [ ] 接口定义完成
- [ ] JavaDoc 文档完整
- [ ] 编译通过

**代码示例**:
```java
public interface SceneManager {
    // 现有方法保持不变...
    
    /**
     * 获取场景的运行时实例 (SceneGroup)
     */
    CompletableFuture<SceneGroup> getSceneGroup(String sceneId);
    
    /**
     * 获取用户场景组
     */
    CompletableFuture<UserSceneGroup> getUserSceneGroup(String sceneGroupId, String userId);
    
    /**
     * 获取用户参与的所有场景组
     */
    CompletableFuture<List<UserSceneGroup>> getUserSceneGroups(String userId);
}
```

---

### 2.2 SDK-002: SceneGroup 类增强

**优先级**: P0  
**工作量**: 2-3 天  
**依赖**: SDK-001

**任务描述**:
- 在 `SceneGroup` 类中添加用户场景组管理功能
- 添加 `getOrCreateUserSceneGroup()` 方法
- 添加 `getAllUserSceneGroups()` 方法
- 添加 `removeUserSceneGroup()` 方法

**验收标准**:
- [ ] 方法实现完成
- [ ] 单元测试通过
- [ ] 线程安全验证

**代码示例**:
```java
public class SceneGroup {
    private final Map<String, UserSceneGroup> userSceneGroups = new ConcurrentHashMap<>();
    
    public UserSceneGroup getOrCreateUserSceneGroup(String userId, Participant.Role role) {
        return userSceneGroups.computeIfAbsent(userId, 
            uid -> new UserSceneGroupImpl(this, uid, role));
    }
    
    public List<UserSceneGroup> getAllUserSceneGroups() {
        return new ArrayList<>(userSceneGroups.values());
    }
    
    public void removeUserSceneGroup(String userId) {
        userSceneGroups.remove(userId);
    }
}
```

---

### 2.3 SDK-003: UserSceneGroup 接口标准化

**优先级**: P0  
**工作量**: 3-5 天  
**依赖**: SDK-002

**任务描述**:
- 标准化 `UserSceneGroup` 接口定义
- 定义技能绑定方法
- 定义能力绑定方法
- 定义知识库绑定方法
- 定义个人上下文方法

**验收标准**:
- [ ] 接口定义完成
- [ ] JavaDoc 文档完整
- [ ] 与现有 `UserSceneGroup` 接口兼容

**接口定义**:
```java
public interface UserSceneGroup {
    // 基础信息
    String getSceneGroupId();
    String getSceneId();
    String getUserId();
    Participant.Role getRole();
    
    // 技能绑定
    SkillBinding addSkill(String skillId, Map<String, Object> config);
    void removeSkill(String skillId);
    List<SkillBinding> getSkills();
    
    // 能力绑定
    CapabilityBinding bindCapability(String capId, Map<String, Object> config);
    void unbindCapability(String bindingId);
    List<CapabilityBinding> getCapabilityBindings();
    
    // 知识库绑定
    KnowledgeBinding bindKnowledgeBase(String kbId, String layer);
    void unbindKnowledgeBase(String kbId);
    List<KnowledgeBinding> getKnowledgeBaseBindings();
    
    // 个人上下文
    Map<String, Object> getPersonalContext();
    void setPersonalContext(String key, Object value);
    
    // 同步
    void syncToSceneGroup();
    void syncFromSceneGroup();
}
```

---

### 2.4 SDK-004: UserSceneGroupImpl 实现

**优先级**: P0  
**工作量**: 3-5 天  
**依赖**: SDK-003

**任务描述**:
- 实现 `UserSceneGroup` 接口
- 实现技能绑定存储
- 实现能力绑定存储
- 实现知识库绑定存储
- 实现个人上下文存储

**验收标准**:
- [ ] 所有接口方法实现
- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试通过

---

### 2.5 SDK-005: SceneManagerImpl 实现

**优先级**: P0  
**工作量**: 2-3 天  
**依赖**: SDK-001, SDK-002, SDK-004

**任务描述**:
- 实现 `SceneManager` 新增方法
- 实现 SceneGroup 生命周期管理
- 实现 UserSceneGroup 查询

**验收标准**:
- [ ] 所有新增方法实现
- [ ] 单元测试通过
- [ ] 集成测试通过

---

## 三、Phase 2: SE 层简化 (P1)

### 3.1 SE-001: SceneGroup 简化设计

**优先级**: P1  
**工作量**: 2-3 天  
**依赖**: SDK-005

**任务描述**:
- 设计 SE `SceneGroup` 简化方案
- 确定保留的业务字段
- 设计与 SDK `SceneGroup` 的关联方式

**验收标准**:
- [ ] 设计文档完成
- [ ] 团队评审通过

**简化方案**:
```java
public class SceneGroup {
    // 引用 SDK SceneGroup
    private String sdkSceneGroupId;
    
    // 业务上下文 (SE 特有)
    private Map<String, Object> businessContext;
    
    // 工作流状态 (SE 特有)
    private Map<String, Object> workflowState;
    
    // 审计日志 (SE 特有)
    private List<SceneGroupEvent> auditLog;
}
```

---

### 3.2 SE-002: SceneGroupBridge 更新

**优先级**: P1  
**工作量**: 2-3 天  
**依赖**: SE-001

**任务描述**:
- 更新 `SceneGroupBridge` 桥接逻辑
- 实现 SDK-SE 双向同步
- 更新健康检查接口

**验收标准**:
- [ ] 桥接逻辑更新完成
- [ ] 双向同步测试通过
- [ ] 健康检查正常

---

### 3.3 SE-003: SE SceneGroup 数据迁移

**优先级**: P1  
**工作量**: 3-5 天  
**依赖**: SE-002

**任务描述**:
- 编写数据迁移脚本
- 迁移现有 SE SceneGroup 数据
- 验证数据完整性

**验收标准**:
- [ ] 迁移脚本完成
- [ ] 数据迁移成功
- [ ] 数据验证通过

---

## 四、Phase 3: MVP 适配 (P1)

### 4.1 MVP-001: API 适配分析

**优先级**: P1  
**工作量**: 1-2 天  
**依赖**: SDK-005

**任务描述**:
- 分析 MVP 现有 API 使用情况
- 确定需要适配的接口
- 制定适配方案

**验收标准**:
- [ ] API 使用清单完成
- [ ] 适配方案确定

---

### 4.2 MVP-002: SceneService 适配

**优先级**: P1  
**工作量**: 3-5 天  
**依赖**: MVP-001

**任务描述**:
- 适配 `SceneService` 接口
- 使用 SDK `SceneManager` 新方法
- 更新数据模型映射

**验收标准**:
- [ ] 接口适配完成
- [ ] 功能测试通过
- [ ] 性能测试通过

---

### 4.3 MVP-003: 页面功能验证

**优先级**: P1  
**工作量**: 2-3 天  
**依赖**: MVP-002

**任务描述**:
- 验证场景列表页面
- 验证场景详情页面
- 验证场景组管理页面

**验收标准**:
- [ ] 所有页面功能正常
- [ ] 数据显示正确
- [ ] 无性能问题

---

## 五、Phase 4: 清理和优化 (P2)

### 5.1 CLN-001: 移除冗余代码

**优先级**: P2  
**工作量**: 2-3 天  
**依赖**: MVP-003

**任务描述**:
- 移除 SE 层冗余的 SceneGroup 代码
- 移除废弃的接口
- 清理无用依赖

**验收标准**:
- [ ] 冗余代码移除
- [ ] 编译通过
- [ ] 测试通过

---

### 5.2 CLN-002: 文档更新

**优先级**: P2  
**工作量**: 2-3 天  
**依赖**: CLN-001

**任务描述**:
- 更新架构文档
- 更新 API 文档
- 更新开发指南

**验收标准**:
- [ ] 文档更新完成
- [ ] 团队评审通过

---

### 5.3 CLN-003: 性能优化

**优先级**: P2  
**工作量**: 3-5 天  
**依赖**: CLN-001

**任务描述**:
- 分析性能瓶颈
- 优化同步机制
- 优化缓存策略

**验收标准**:
- [ ] 性能基准测试完成
- [ ] 性能提升 > 20%

---

## 六、任务依赖图

```
Phase 1 (P0)
─────────────────────────────────────────────────────────────
SDK-001 ──► SDK-002 ──► SDK-003 ──► SDK-004 ──► SDK-005
                                                      │
                                                      ▼
Phase 2 (P1)                                    ┌─────────┐
────────────────────────────────────────────────│ SE-001  │
SE-001 ──► SE-002 ──► SE-003                    └─────────┘
                │                                     │
                │                                     ▼
                │                               ┌─────────┐
                └──────────────────────────────►│ MVP-001 │
                                                └─────────┘
                                                      │
                                                      ▼
Phase 3 (P1)                                    ┌─────────┐
────────────────────────────────────────────────│ MVP-002 │
MVP-001 ──► MVP-002 ──► MVP-003                 └─────────┘
                                                      │
                                                      ▼
Phase 4 (P2)                                    ┌─────────┐
────────────────────────────────────────────────│ CLN-001 │
CLN-001 ──► CLN-002                              └─────────┘
    │                                                │
    ▼                                                ▼
CLN-003                                         ┌─────────┐
                                                │ CLN-002 │
                                                └─────────┘
```

## 七、时间估算

| Phase | 任务数 | 总工作量 | 预计时间 |
|-------|--------|----------|----------|
| Phase 1 (P0) | 5 | 12-19 天 | 2-3 周 |
| Phase 2 (P1) | 3 | 7-11 天 | 1-2 周 |
| Phase 3 (P1) | 3 | 6-10 天 | 1-2 周 |
| Phase 4 (P2) | 3 | 7-11 天 | 1-2 周 |
| **总计** | **14** | **32-51 天** | **5-9 周** |

## 八、风险与缓解

| 风险 | 级别 | 缓解措施 |
|------|------|----------|
| API 不兼容 | 🔴 高 | 提供适配器模式，渐进迁移 |
| 数据丢失 | 🔴 高 | 完整备份，提供回滚机制 |
| 性能下降 | 🟡 中 | 性能基准测试，优化同步机制 |
| 学习成本 | 🟡 中 | 详细文档，培训支持 |

## 九、状态

- [x] 任务分解完成
- [ ] 团队评审
- [ ] 开始实施

## 十、联系人

- SDK 团队
- SE 团队
- MVP 团队
