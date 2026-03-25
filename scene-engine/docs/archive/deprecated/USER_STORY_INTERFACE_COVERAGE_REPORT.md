# Scene Engine v2.3.1 用户故事接口覆盖度报告

**版本**: v2.3.1  
**日期**: 2026-03-08  
**用户故事**: 周报场景 - 领导与员工激活流程

---

## 一、用户故事概述

### 1.1 场景角色

| 角色 | 职责 |
|------|------|
| **管理员** | 系统初始化、场景配置 |
| **领导** | 激活场景、选择下属推送、添加独立 Skills |
| **员工** | 接收推送、激活场景、配置邮件/GIT Skill |

### 1.2 激活流程

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

## 二、接口覆盖度矩阵

### 2.1 管理员初始化

| 功能需求 | SE 接口 | 状态 | 说明 |
|----------|---------|------|------|
| 管理员登录 | `SceneEngine.adminLogin()` | ✅ 已实现 | AdminClient |
| 系统初始化 | `SceneGroupInitializer.initialize()` | ✅ 已实现 | 6步初始化 |
| 场景配置 | `SceneGroupManager.create()` | ✅ 已实现 | SDK 接口 |
| 成员管理 | `SceneGroupManager.join()/leave()` | ✅ 已实现 | SDK 接口 |

**覆盖度**: **100%**

---

### 2.2 待激活场景列表

| 功能需求 | SE 接口 | 状态 | 说明 |
|----------|---------|------|------|
| 领导待激活列表 | `SceneClient.getPendingScenes()` | ⚠️ 需确认 | 接口存在但需验证 |
| 员工待激活列表 | `SceneClient.getPendingScenes()` | ⚠️ 需确认 | 同上 |
| 场景状态过滤 | `SceneInfo.status` | ✅ 已实现 | status 字段 |
| 按角色过滤 | - | ❌ 缺失 | 需新增接口 |

**覆盖度**: **50%** - 缺少按角色过滤待激活场景的接口

---

### 2.3 领导激活流程

| 步骤 | 功能需求 | SE 接口 | 状态 | 说明 |
|------|----------|---------|------|------|
| 1 | 激活场景 | `SceneGroupInitializer.initialize()` | ✅ 已实现 | 6步初始化 |
| 1 | 初始化场景组 | `SceneGroupManager.create()` | ✅ 已实现 | SDK 接口 |
| 2 | 选择下属推送 | - | ❌ 缺失 | 需新增推送接口 |
| 2 | 推送反馈 | - | ❌ 缺失 | 需新增反馈接口 |
| 3 | 添加独立 Skills | `SkillRuntime.install()` | ✅ 已实现 | Skill 安装 |
| 3 | 汇总统计工具 | `KnowledgeCapability.retrieve()` | ✅ 已实现 | 知识检索 |

**覆盖度**: **60%** - 缺少下属推送相关接口

---

### 2.4 员工激活流程

| 步骤 | 功能需求 | SE 接口 | 状态 | 说明 |
|------|----------|---------|------|------|
| 1 | 激活场景 | `SceneGroupInitializer.initialize()` | ✅ 已实现 | 6步初始化 |
| 2 | 配置邮件 Skill | `SkillRuntime.install()` | ✅ 已实现 | Skill 安装 |
| 2 | 邮件 Skill 配置 | `SkillInstance.configure()` | ⚠️ 需确认 | 配置接口 |
| 3 | 配置 GIT Skill | `SkillRuntime.install()` | ✅ 已实现 | Skill 安装 |
| 3 | GIT Skill 配置 | `SkillInstance.configure()` | ⚠️ 需确认 | 配置接口 |

**覆盖度**: **80%** - Skill 配置接口需确认

---

### 2.5 都激活后

| 步骤 | 功能需求 | SE 接口 | 状态 | 说明 |
|------|----------|---------|------|------|
| 1 | 自动提醒入口 | - | ❌ 缺失 | 需新增提醒服务 |
| 3 | 手工撰写入口 | - | ❌ 缺失 | 需新增撰写接口 |
| 4 | UI 界面 | `NexusUiController` | ✅ 已实现 | UI 管理 |
| 4 | 日志 AI 助手 | `DecisionEngine.decide()` | ✅ 已实现 | 决策引擎 |
| 4 | 汇总邮件按钮 | - | ⚠️ 需确认 | Skill 调用 |
| 4 | 汇总代码提交按钮 | - | ⚠️ 需确认 | Skill 调用 |

**覆盖度**: **50%** - 缺少提醒和撰写接口

---

## 三、缺失接口清单

### 3.1 P0 - 核心缺失

| 接口 | 说明 | 建议 |
|------|------|------|
| `SceneClient.getPendingScenes(role)` | 按角色获取待激活场景 | 新增参数或新接口 |
| `SceneClient.pushToSubordinates(sceneId, userIds)` | 推送场景给下属 | 新增接口 |
| `SceneClient.getPushFeedback(sceneId)` | 获取推送反馈 | 新增接口 |

### 3.2 P1 - 重要缺失

| 接口 | 说明 | 建议 |
|------|------|------|
| `ReminderService.schedule(sceneId, cron)` | 自动提醒服务 | 新增服务 |
| `JournalService.createDraft(sceneId)` | 手工撰写草稿 | 新增服务 |
| `JournalService.submit(draftId)` | 提交日志 | 新增服务 |

### 3.3 P2 - 增强功能

| 接口 | 说明 | 建议 |
|------|------|------|
| `SkillInstance.configure(config)` | Skill 配置接口 | 确认是否存在 |
| `SkillInstance.getStatus()` | Skill 状态查询 | 确认是否存在 |

---

## 四、现有接口详情

### 4.1 SceneEngine 接口

```java
public interface SceneEngine {
    SceneClient login(String username, String password);     // ✅ 用户登录
    SceneClient login(String token);                          // ✅ Token 登录
    AdminClient adminLogin(String username, String password); // ✅ 管理员登录
    void logout(String sessionId);                             // ✅ 登出
    SessionInfo getSession(String sessionId);                  // ✅ 获取会话
    boolean validateSession(String sessionId);                 // ✅ 验证会话
    SessionInfo refreshSession(String sessionId);              // ✅ 刷新会话
    EngineStatus getStatus();                                  // ✅ 引擎状态
    void start();                                              // ✅ 启动
    void stop();                                               // ✅ 停止
}
```

### 4.2 AdminClient 接口

```java
public interface AdminClient {
    // 管理员相关操作
    void initSystem();                    // ✅ 系统初始化
    void configureScene(SceneConfig);     // ✅ 场景配置
    List<SceneInfo> listScenes();         // ✅ 场景列表
    void assignScene(sceneId, userIds);   // ⚠️ 需确认
}
```

### 4.3 SceneClient 接口

```java
public interface SceneClient {
    // 用户相关操作
    List<SceneInfo> getScenes();           // ✅ 获取场景列表
    List<SceneInfo> getPendingScenes();    // ⚠️ 待激活列表（需确认）
    void activateScene(sceneId);           // ⚠️ 激活场景（需确认）
    void installSkill(skillId);            // ✅ 安装 Skill
    void configureSkill(skillId, config);  // ⚠️ 配置 Skill（需确认）
}
```

### 4.4 SceneGroupInitializer 接口

```java
public class SceneGroupInitializer {
    CompletableFuture<InitResult> initialize(InitRequest);  // ✅ 6步初始化
    InitContext getInitContext(initId);                      // ✅ 获取上下文
    CompletableFuture<Boolean> cancel(initId);               // ✅ 取消初始化
}
```

### 4.5 SkillRuntime 接口

```java
public class SkillRuntime {
    SkillInstallResult install(userId, skillId, config);     // ✅ 安装 Skill
    SkillUninstallResult uninstall(userId, skillId);         // ✅ 卸载 Skill
    boolean start(userId, skillId);                           // ✅ 启动 Skill
    boolean stop(userId, skillId);                            // ✅ 停止 Skill
    Object invoke(userId, skillId, capability, params);       // ✅ 调用 Skill
}
```

### 4.6 NexusUiController 接口

```java
@RestController
@RequestMapping("/api/v1/ui")
public class NexusUiController {
    GET  /                     // ✅ 获取所有 UI
    GET  /{skillId}            // ✅ 获取指定 UI
    GET  /type/{type}          // ✅ 按类型获取 UI
    GET  /menus                // ✅ 获取所有菜单
    GET  /routes               // ✅ 获取所有路由
    POST /{skillId}/load       // ✅ 加载 UI Skill
    POST /{skillId}/reload     // ✅ 重新加载 UI Skill
    POST /{skillId}/unload     // ✅ 卸载 UI Skill
    POST /scan                 // ✅ 扫描加载所有 UI
    GET  /{skillId}/status     // ✅ 获取 UI 状态
}
```

---

## 五、总结

### 5.1 覆盖度统计

| 模块 | 覆盖度 | 说明 |
|------|--------|------|
| 管理员初始化 | **100%** | 完整支持 |
| 待激活场景列表 | **50%** | 缺少角色过滤 |
| 领导激活流程 | **60%** | 缺少推送接口 |
| 员工激活流程 | **80%** | Skill 配置需确认 |
| 都激活后 | **50%** | 缺少提醒和撰写接口 |

### 5.2 总体覆盖度

**总体覆盖度: 68%**

### 5.3 优先补充建议

| 优先级 | 接口 | 工作量 |
|--------|------|--------|
| **P0** | `pushToSubordinates()` 推送接口 | 2人天 |
| **P0** | `getPendingScenes(role)` 角色过滤 | 1人天 |
| **P1** | `ReminderService` 提醒服务 | 3人天 |
| **P1** | `JournalService` 撰写服务 | 3人天 |

---

**报告维护**: Ooder Team  
**最后更新**: 2026-03-08
