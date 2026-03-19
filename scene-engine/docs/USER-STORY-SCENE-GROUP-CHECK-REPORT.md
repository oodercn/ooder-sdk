# 用户故事场景组接口实现检查报告

## 1. 检查概览

**检查日期**: 2026-03-19  
**检查版本**: SceneEngine 2.3.1  
**用户故事**: 周报场景 - 领导与员工激活流程

### 1.1 接口覆盖度统计

| 模块 | 原覆盖度 | 当前覆盖度 | 变化 |
|------|---------|-----------|------|
| 管理员初始化 | 100% | **100%** | - |
| 待激活场景列表 | 50% | **100%** | ✅ +50% |
| 领导激活流程 | 60% | **100%** | ✅ +40% |
| 员工激活流程 | 80% | **100%** | ✅ +20% |
| 都激活后 | 50% | **100%** | ✅ +50% |

**总体覆盖度**: **100%** ✅（原 68%）

---

## 2. 用户故事场景组需求

### 2.1 场景角色

| 角色 | 职责 |
|------|------|
| **管理员** | 系统初始化、场景配置 |
| **领导** | 激活场景、选择下属推送、添加独立 Skills |
| **员工** | 接收推送、激活场景、配置邮件/GIT Skill |

### 2.2 激活流程

```
管理员初始化
    │
    ▼
领导待激活列表 ────────────────── 员工待激活列表
    │                                │
    ▼                                ▼
领导激活流程                       员工激活流程
├── 1. 激活/初始化                 ├── 1. 激活
├── 2. 选择下属推送                 ├── 2. 配置邮件 Skill
├── 3. 添加独立 Skills              ├── 3. 配置 GIT Skill
    │                                │
    └──────────┬─────────────────────┘
               ▼
        都激活后
        ├── 1. 自动提醒入口
        ├── 3. 手工撰写入口
        └── 4. UI 界面（日志AI助手）
```

---

## 3. 接口实现检查

### 3.1 管理员初始化（100% ✅）

| 功能需求 | SE 接口 | 状态 | 实现位置 |
|----------|---------|------|----------|
| 管理员登录 | `SceneEngine.adminLogin()` | ✅ | `core/SceneEngine.java` |
| 系统初始化 | `SceneGroupInitializer.initialize()` | ✅ | `core/init/SceneGroupInitializer.java` |
| 场景配置 | `SceneGroupManager.create()` | ✅ | `group/SceneGroupManager.java` |
| 成员管理 | `SceneGroupManager.join()/leave()` | ✅ | `group/SceneGroupManager.java` |

### 3.2 待激活场景列表（100% ✅）

| 功能需求 | SE 接口 | 状态 | 实现位置 |
|----------|---------|------|----------|
| 领导待激活列表 | `PendingSceneQuery.getPendingScenes()` | ✅ | `core/PendingSceneQuery.java` |
| 员工待激活列表 | `PendingSceneQuery.getPendingScenes()` | ✅ | `core/PendingSceneQuery.java` |
| 场景状态过滤 | `PendingSceneInfo.status` | ✅ | `core/PendingSceneInfo.java` |
| **按角色过滤** | `getPendingScenes(userId, role)` | ✅ | `core/PendingSceneQuery.java` |

### 3.3 领导激活流程（100% ✅）

| 步骤 | 功能需求 | SE 接口 | 状态 | 实现位置 |
|------|----------|---------|------|----------|
| 1 | 激活场景 | `SceneGroupInitializer.initialize()` | ✅ | `core/init/` |
| 1 | 初始化场景组 | `SceneGroupManager.create()` | ✅ | `group/` |
| 2 | **选择下属推送** | `PushService.pushToSubordinates()` | ✅ | `service/push/` |
| 2 | **推送反馈** | `PushService.getPushFeedbacks()` | ✅ | `service/push/` |
| 3 | 添加独立 Skills | `SkillRuntime.install()` | ✅ | `skill/runtime/` |
| 3 | 汇总统计工具 | `KnowledgeCapability.retrieve()` | ✅ | `skill/knowledge/` |

### 3.4 员工激活流程（100% ✅）

| 步骤 | 功能需求 | SE 接口 | 状态 | 实现位置 |
|------|----------|---------|------|----------|
| 1 | 激活场景 | `SceneGroupInitializer.initialize()` | ✅ | `core/init/` |
| 2 | 配置邮件 Skill | `SkillRuntime.install()` | ✅ | `skill/runtime/` |
| 2 | 邮件 Skill 配置 | `SkillInstance.configure()` | ✅ | `skill/instance/` |
| 3 | 配置 GIT Skill | `SkillRuntime.install()` | ✅ | `skill/runtime/` |
| 3 | GIT Skill 配置 | `SkillInstance.configure()` | ✅ | `skill/instance/` |

### 3.5 都激活后（100% ✅）

| 步骤 | 功能需求 | SE 接口 | 状态 | 实现位置 |
|------|----------|---------|------|----------|
| 1 | **自动提醒入口** | `ReminderService.schedule()` | ✅ | `service/reminder/` |
| 3 | **手工撰写入口** | `JournalService.createDraft()` | ✅ | `service/journal/` |
| 4 | UI 界面 | `NexusUiController` | ✅ | `ui/` |
| 4 | 日志 AI 助手 | `DecisionEngine.decide()` | ✅ | `core/decision/` |
| 4 | **汇总邮件按钮** | `JournalService.summarizeEmails()` | ✅ | `service/journal/` |
| 4 | **汇总代码提交按钮** | `JournalService.summarizeGitCommits()` | ✅ | `service/journal/` |

---

## 4. 新增接口详情

### 4.1 PushService（推送服务）

**位置**: `net.ooder.scene.service.push.PushService`

```java
public interface PushService {
    // 推送场景给下属
    CompletableFuture<PushResult> pushToSubordinates(PushRequest request);
    
    // 获取推送详情
    PushDetail getPushDetail(String pushId);
    
    // 获取推送反馈列表
    List<PushFeedback> getPushFeedbacks(String sceneId, String leaderId);
    
    // 确认/拒绝推送
    void confirmPush(String pushId, String userId);
    void rejectPush(String pushId, String userId, String reason);
    
    // 取消/重推送
    boolean cancelPush(String pushId);
    String retryPush(String pushId);
}
```

### 4.2 ReminderService（提醒服务）

**位置**: `net.ooder.scene.service.reminder.ReminderService`

```java
public interface ReminderService {
    // 创建提醒任务
    ReminderTask createReminder(ReminderConfig config);
    
    // 取消提醒任务
    void cancelReminder(String reminderId);
    
    // 获取用户的提醒列表
    List<ReminderTask> getUserReminders(String userId);
    
    // 触发提醒
    void triggerReminder(String reminderId);
}
```

### 4.3 JournalService（日志撰写服务）

**位置**: `net.ooder.scene.service.journal.JournalService`

```java
public interface JournalService {
    // 创建草稿
    JournalDraft createDraft(String sceneId, String userId);
    
    // 保存/提交草稿
    void saveDraft(String draftId, String content);
    void submitJournal(JournalSubmitRequest request);
    
    // 获取用户日志列表
    List<JournalEntry> getUserJournals(String userId, int page, int size);
    
    // 自动汇总生成日志
    JournalDraft autoGenerate(String sceneId, String userId, AutoGenerateOptions options);
    
    // 汇总邮件/代码提交
    String summarizeEmails(String userId, Date startTime, Date endTime);
    String summarizeGitCommits(String userId, Date startTime, Date endTime);
}
```

### 4.4 PendingSceneQuery（待激活场景查询）

**位置**: `net.ooder.scene.core.PendingSceneQuery`

```java
public interface PendingSceneQuery {
    // 获取用户待激活场景列表
    List<PendingSceneInfo> getPendingScenes(String userId);
    
    // 按角色获取待激活场景列表
    List<PendingSceneInfo> getPendingScenes(String userId, MemberRole role);
    
    // 获取待激活场景数量
    int getPendingSceneCount(String userId);
    
    // 获取场景推送来源
    String getPushSource(String sceneId, String userId);
    
    // 检查场景是否待激活
    boolean isPending(String sceneId, String userId);
}
```

---

## 5. 场景组核心功能

### 5.1 SceneGroup（业务场景组）

**位置**: `net.ooder.scene.group.SceneGroup`

**核心能力**:
- 场景组生命周期管理（创建、激活、暂停、销毁）
- 参与者管理（加入、离开、角色变更）
- 能力绑定管理
- 知识库绑定管理
- 快照管理

### 5.2 SceneGroupManager（场景组管理器）

**位置**: `net.ooder.scene.group.SceneGroupManager`

**核心能力**:
- 场景组集中管理
- 心跳检测
- 状态维护

---

## 6. 总结

### 6.1 完成情况

✅ **所有用户故事中的场景组接口已全部实现**

| 原缺失接口 | 当前状态 | 实现位置 |
|-----------|---------|----------|
| `pushToSubordinates()` | ✅ 已实现 | `service/push/` |
| `getPushFeedbacks()` | ✅ 已实现 | `service/push/` |
| `getPendingScenes(role)` | ✅ 已实现 | `core/` |
| `ReminderService` | ✅ 已实现 | `service/reminder/` |
| `JournalService` | ✅ 已实现 | `service/journal/` |
| `summarizeEmails()` | ✅ 已实现 | `service/journal/` |
| `summarizeGitCommits()` | ✅ 已实现 | `service/journal/` |

### 6.2 架构分层

```
应用层 (Application)
├── SceneEngine (入口)
├── AdminClient (管理员)
└── SceneClient (用户)
      │
      ▼
服务层 (Service)
├── PushService (推送) ✅
├── ReminderService (提醒) ✅
├── JournalService (日志) ✅
└── UnifiedSceneService
      │
      ▼
核心层 (Core)
├── PendingSceneQuery ✅
├── SceneGroupManager ✅
├── DecisionEngine
└── CapRouter
      │
      ▼
技能层 (Skill)
├── SkillRuntime
├── KnowledgeCapability
└── LLM Provider
```

### 6.3 下一步建议

1. **集成测试**: 编写用户故事端到端测试用例
2. **文档更新**: 更新 API 文档和使用指南
3. **性能优化**: 对推送服务进行性能测试

---

**报告维护**: SE Team  
**最后更新**: 2026-03-19
