# 用户故事接口架构分层实现方案

**版本**: v2.3.1  
**日期**: 2026-03-08  
**目标**: 根据架构分层原则，合理划分缺失接口的实现

---

## 一、当前架构分层分析

### 1.1 现有分层结构

```
net.ooder.scene/
├── core/                    # 核心层 - 接口定义、基础实现
│   ├── SceneEngine.java     # 引擎入口接口
│   ├── SceneClient.java     # 用户客户端接口
│   ├── AdminClient.java     # 管理员客户端接口
│   ├── impl/                # 核心实现
│   ├── init/                # 初始化模块
│   ├── decision/            # 决策引擎
│   └── security/            # 安全模块
│
├── service/                 # 服务层 - 业务服务
│   └── UnifiedSceneService.java
│
├── skill/                   # 技能层 - Skill 管理
│   ├── runtime/             # 运行时
│   ├── instance/            # 实例管理
│   └── ...
│
├── discovery/               # 发现层 - Skill 发现
│   └── UnifiedSkillRegistry.java
│
├── event/                   # 事件层 - 事件发布订阅
│   └── SceneEventPublisher.java
│
├── llm/                     # LLM 层 - 大模型代理
│   └── proxy/
│
├── ui/                      # UI 层 - 界面管理
│   └── NexusUiController.java
│
├── protocol/                # 协议层 - 通信协议
│   └── DiscoveryProtocolAdapter.java
│
├── provider/                # 提供者层 - 数据提供
│   └── UserProvider.java
│
├── session/                 # 会话层 - 会话管理
│   └── SessionManager.java
│
├── monitor/                 # 监控层 - 状态监控
│   └── SceneMonitor.java
│
└── asset/                   # 资产层 - 资产管理
    └── AssetGovernance.java
```

### 1.2 分层职责

| 层级 | 职责 | 依赖方向 |
|------|------|----------|
| **core** | 核心接口定义、基础实现 | 被所有层依赖 |
| **service** | 业务服务封装 | 依赖 core, skill, discovery |
| **skill** | Skill 生命周期管理 | 依赖 core, discovery |
| **discovery** | Skill 发现与注册 | 依赖 core |
| **event** | 事件发布订阅 | 依赖 core |
| **llm** | LLM 代理与上下文 | 依赖 core, skill |
| **ui** | UI 界面管理 | 依赖 core, skill |
| **protocol** | 通信协议适配 | 依赖 core |
| **provider** | 数据提供者 | 依赖 core |
| **session** | 会话管理 | 依赖 core |
| **monitor** | 状态监控 | 依赖 core, event |
| **asset** | 资产治理 | 依赖 core |

---

## 二、缺失接口分层方案

### 2.1 推送服务 - 放入 service 层

**理由**: 推送是业务逻辑，涉及场景、用户、通知等多个模块协调

```
service/
├── push/
│   ├── PushService.java              # 推送服务接口
│   ├── PushRequest.java              # 推送请求
│   ├── PushResult.java               # 推送结果
│   ├── PushFeedback.java             # 推送反馈
│   └── impl/
│       └── PushServiceImpl.java      # 推送服务实现
```

**接口设计**:

```java
package net.ooder.scene.service.push;

public interface PushService {
    
    /**
     * 推送场景给下属
     */
    CompletableFuture<PushResult> pushToSubordinates(PushRequest request);
    
    /**
     * 获取推送反馈
     */
    List<PushFeedback> getPushFeedback(String sceneId, String leaderId);
    
    /**
     * 确认推送
     */
    void confirmPush(String pushId, String userId);
}

public class PushRequest {
    private String sceneId;
    private String leaderId;
    private List<String> subordinateIds;
    private String message;
    private long expireTime;
}

public class PushResult {
    private String pushId;
    private int totalCount;
    private int successCount;
    private List<String> failedUserIds;
}
```

---

### 2.2 提醒服务 - 放入 service 层

**理由**: 提醒是业务逻辑，涉及定时任务、通知、用户偏好等

```
service/
├── reminder/
│   ├── ReminderService.java          # 提醒服务接口
│   ├── ReminderConfig.java           # 提醒配置
│   ├── ReminderTask.java             # 提醒任务
│   └── impl/
│       └── ReminderServiceImpl.java  # 提醒服务实现
```

**接口设计**:

```java
package net.ooder.scene.service.reminder;

public interface ReminderService {
    
    /**
     * 创建提醒任务
     */
    ReminderTask createReminder(ReminderConfig config);
    
    /**
     * 取消提醒任务
     */
    void cancelReminder(String reminderId);
    
    /**
     * 获取用户的提醒列表
     */
    List<ReminderTask> getUserReminders(String userId);
    
    /**
     * 触发提醒
     */
    void triggerReminder(String reminderId);
}

public class ReminderConfig {
    private String sceneId;
    private String userId;
    private String cronExpression;    // cron 表达式
    private String reminderType;       // EMAIL, PUSH, SMS
    private String messageTemplate;
}
```

---

### 2.3 日志撰写服务 - 放入 service 层

**理由**: 日志撰写是业务逻辑，涉及草稿、提交、审核等

```
service/
├── journal/
│   ├── JournalService.java           # 日志服务接口
│   ├── JournalDraft.java             # 日志草稿
│   ├── JournalEntry.java             # 日志条目
│   ├── JournalSubmitRequest.java     # 提交请求
│   └── impl/
│       └── JournalServiceImpl.java   # 日志服务实现
```

**接口设计**:

```java
package net.ooder.scene.service.journal;

public interface JournalService {
    
    /**
     * 创建草稿
     */
    JournalDraft createDraft(String sceneId, String userId);
    
    /**
     * 保存草稿
     */
    void saveDraft(String draftId, String content);
    
    /**
     * 提交日志
     */
    void submitJournal(JournalSubmitRequest request);
    
    /**
     * 获取用户日志列表
     */
    List<JournalEntry> getUserJournals(String userId, int page, int size);
    
    /**
     * 自动汇总生成日志
     */
    JournalDraft autoGenerate(String sceneId, String userId, AutoGenerateOptions options);
}

public class AutoGenerateOptions {
    private boolean includeEmail;      // 汇总邮件
    private boolean includeGitCommit;  // 汇总代码提交
    private Date startTime;
    private Date endTime;
}
```

---

### 2.4 待激活场景列表 - 放入 core 层

**理由**: 是核心接口 SceneClient 的扩展

```
core/
├── PendingSceneQuery.java            # 待激活场景查询
├── PendingSceneInfo.java             # 待激活场景信息
└── impl/
    └── PendingSceneQueryImpl.java    # 查询实现
```

**接口设计**:

```java
package net.ooder.scene.core;

public interface PendingSceneQuery {
    
    /**
     * 获取待激活场景列表
     */
    List<PendingSceneInfo> getPendingScenes(String userId);
    
    /**
     * 按角色获取待激活场景列表
     */
    List<PendingSceneInfo> getPendingScenes(String userId, MemberRole role);
    
    /**
     * 获取待激活场景数量
     */
    int getPendingSceneCount(String userId);
}

public class PendingSceneInfo {
    private String sceneId;
    private String sceneName;
    private String description;
    private MemberRole requiredRole;   // 需要的角色
    private String pushFrom;           // 推送来源（领导ID）
    private long pushTime;             // 推送时间
    private long expireTime;           // 过期时间
    private SceneActivationStatus status;
}
```

---

## 三、完整分层架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           架构分层实现方案                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         应用层 (Application)                         │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────────┐ │   │
│  │  │ SceneEngine  │ │ AdminClient  │ │ SceneClient  │ │ UI Controller│   │
│  │  │ (入口)       │ │ (管理员)     │ │ (用户)       │ │ (界面)      │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         服务层 (Service)                             │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────────┐ │   │
│  │  │ PushService  │ │ReminderService│ │JournalService│ │UnifiedScene│ │   │
│  │  │ (推送)       │ │ (提醒)       │ │ (日志)       │ │ Service    │ │   │
│  │  │ [新增]       │ │ [新增]       │ │ [新增]       │ │ (已有)     │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         核心层 (Core)                                │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────────┐ │   │
│  │  │PendingScene  │ │SceneGroup    │ │ DecisionEngine│ │ CapRouter  │ │   │
│  │  │ Query [新增] │ │ Initializer  │ │ (决策)       │ │ (路由)     │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         技能层 (Skill)                               │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────────┐ │   │
│  │  │ SkillRuntime │ │SkillInstance │ │Knowledge     │ │ LLM        │ │   │
│  │  │ (运行时)     │ │ Pool         │ │ Capability   │ │ Provider   │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         基础设施层 (Infrastructure)                  │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌────────────┐ │   │
│  │  │ Discovery    │ │ Event        │ │ Session      │ │ Monitor    │ │   │
│  │  │ (发现)       │ │ (事件)       │ │ (会话)       │ │ (监控)     │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、实现优先级

### 4.1 P0 - 核心功能

| 模块 | 接口 | 层级 | 工作量 |
|------|------|------|--------|
| **PendingSceneQuery** | 待激活场景查询 | core | 1人天 |
| **PushService** | 推送服务 | service | 2人天 |

### 4.2 P1 - 重要功能

| 模块 | 接口 | 层级 | 工作量 |
|------|------|------|--------|
| **ReminderService** | 提醒服务 | service | 3人天 |
| **JournalService** | 日志撰写服务 | service | 3人天 |

### 4.3 P2 - 增强功能

| 模块 | 接口 | 层级 | 工作量 |
|------|------|------|--------|
| **AutoGenerate** | 日志自动生成 | service | 2人天 |
| **PushFeedback** | 推送反馈管理 | service | 1人天 |

---

## 五、依赖关系

```
SceneClient (应用层)
    │
    ├──▶ PendingSceneQuery (core)
    │        │
    │        └──▶ SceneGroupManager (SDK)
    │
    └──▶ PushService (service)
             │
             ├──▶ SceneGroupManager (SDK)
             ├──▶ EventPublisher (event)
             └──▶ NotificationProvider (provider)

JournalService (service)
    │
    ├──▶ SkillRuntime (skill)
    │        │
    │        └──▶ UnifiedSkillRegistry (discovery)
    │
    ├──▶ KnowledgeCapability (skill)
    │
    └──▶ DecisionEngine (core)

ReminderService (service)
    │
    ├──▶ SchedulerProvider (skill)
    │
    └──▶ EventPublisher (event)
```

---

## 六、总结

### 6.1 分层原则

1. **单一职责**: 每个服务只负责一个业务领域
2. **依赖倒置**: 上层依赖下层接口，不依赖实现
3. **接口隔离**: 接口粒度适中，避免臃肿
4. **开闭原则**: 对扩展开放，对修改关闭

### 6.2 新增模块

| 模块 | 包路径 | 说明 |
|------|--------|------|
| PendingSceneQuery | `net.ooder.scene.core` | 待激活场景查询 |
| PushService | `net.ooder.scene.service.push` | 推送服务 |
| ReminderService | `net.ooder.scene.service.reminder` | 提醒服务 |
| JournalService | `net.ooder.scene.service.journal` | 日志撰写服务 |

### 6.3 预估总工作量

**总计: 12人天**

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-08
