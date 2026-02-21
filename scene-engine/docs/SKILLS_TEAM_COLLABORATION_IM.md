# Skills 团队协作任务：IM 即时通讯服务

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | 1.0.0 |
| 创建日期 | 2026-02-21 |
| 发起方 | SEC Team (ENexus) |
| 接收方 | Skills Team (Nexus) |

---

## 一、协作背景

基于 ENX-IM-2026-001 协作任务，需要构建企业级即时通讯协作体系。SEC 团队负责企业端业务场景，Skills 团队负责个人端即时通讯核心能力。

---

## 二、SEC 团队已完成/待完成任务

### 2.1 已完成任务

| 任务 | 状态 | 文件位置 |
|------|------|----------|
| Skill Provider 接口定义 | ✅ 已完成 | scene-engine/src/main/java/net/ooder/scene/skill/ |
| Mock 实现 | ✅ 已完成 | scene-engine/src/main/java/net/ooder/scene/skill/ |
| Maven 0.7.3 发布 | ✅ 已完成 | 本地 Maven 仓库 |

### 2.2 待完成任务（SEC 自行实现）

| 任务 | 工作量 | 优先级 | 说明 |
|------|--------|--------|------|
| **SE-001: 扩展 SkillMsgService** | 4h | P0 | 新增消息历史、已读标记、消息撤回能力 |
| **SE-002: 扩展 SkillOrgService** | 4h | P0 | 新增组织同步、部门创建、用户创建能力 |
| **SN-001: 新建 SkillBusinessService** | 8h | P1 | 新建业务分类、业务场景管理能力 |

**扩展能力详情**：

```java
// SkillMsgService 新增方法
List<IMMessage> getMessageHistory(String conversationId, int limit, Long beforeTime);
boolean markAsRead(String conversationId, String userId, Long lastReadTime);
boolean recallMessage(String messageId, String userId);

// SkillOrgService 新增方法
Map<String, Object> syncOrganization(Map<String, Object> orgData);
Department createDepartment(Map<String, Object> params);
Map<String, Object> createUser(Map<String, Object> params);

// SkillBusinessService 完整实现
List<BusinessCategory> getCategoryList();
BusinessScene createScene(Map<String, Object> params);
Map<String, Object> invokeCapability(String sceneId, String capId, Map<String, Object> params);
```

---

## 三、Skills 团队协作任务

### 3.1 核心技能开发

| 任务 | 工作量 | 优先级 | 说明 |
|------|--------|--------|------|
| **DN-001: 新建 SkillImService** | 12h | P1 | 会话管理 + 联系人管理能力 |
| **DN-002: 新建 SkillGroupService** | 8h | P1 | 群组管理 + 成员管理能力 |

**接口文件**：

| 接口 | 说明 | 对应文档 |
|------|------|----------|
| SkillImService | 即时通讯服务 | DELEGATE-SKILL-IM-001.md |
| SkillGroupService | 群组管理服务 | DELEGATE-SKILL-GROUP-001.md |

### 3.2 前端交互开发

| 任务 | 工作量 | 优先级 | 说明 |
|------|--------|--------|------|
| **DF-001: IM 前端交互页面** | 16h | P2 | 会话列表 + 聊天页面 + 联系人页面 |

**页面文件**：

| 页面 | 文件路径 | 对应文档 |
|------|----------|----------|
| 会话列表 | /console/pages/im/conversation-list.html | DELEGATE-IM-UI-001.md |
| 聊天页面 | /console/pages/im/chat.html | DELEGATE-IM-UI-001.md |
| 联系人页面 | /console/pages/im/contact-list.html | DELEGATE-IM-UI-001.md |

---

## 四、技术规范

### 4.1 架构设计

```
SEC (scene-engine 0.7.3)
├── skill/ (Provider 接口)
│   ├── LlmProvider.java
│   ├── StorageProvider.java
│   ├── SchedulerProvider.java
│   └── HttpClientProvider.java
│
└── skill-*/ (Skills 扩展)
    ├── skill-msg/ (SEC 扩展)
    ├── skill-org/ (SEC 扩展)
    └── skill-business/ (SEC 新建)
        
Skills Team (ooder-skills)
├── skill-im/ (Skills 新建)
│   └── SkillImService.java
├── skill-group/ (Skills 新建)
│   └── SkillGroupService.java
└── frontend/im/ (Skills 新建)
    ├── conversation-list.html
    ├── chat.html
    └── contact-list.html
```

### 4.2 注册规范

**Skill 注册**：

```java
// 在 SysSkillRegistry 中注册
@Bean
public SkillImService skillImService() {
    return new SkillImService();
}

// 注册到场景组
sceneGroupManager.registerSkill("SYS", "im", skillImService);
```

**API 端点**：

```java
@PostMapping("/im/conversation/list")
public ApiResponse<Object> getConversationList(@RequestBody Map<String, Object> params) {
    SkillService imService = skillRegistry.getSkillByType(SkillImService.SKILL_TYPE_IM);
    SkillRequest request = SkillRequest.create(SkillImService.SKILL_TYPE_IM, "getConversationList")
        .param("userId", params.get("userId"));
    Object result = imService.execute(request);
    return handleSkillResponse(result);
}
```

---

## 五、时间计划

| 阶段 | 任务 | 时间 | 负责方 |
|------|------|------|--------|
| 第一阶段 | SE-001, SE-002 (Skills 扩展) | 2026-02-21 | SEC |
| 第二阶段 | SN-001 (BusinessService) | 2026-02-22 | SEC |
| 第三阶段 | DN-001, DN-002 (委托 Skills) | 2026-02-23~24 | Skills |
| 第四阶段 | DF-001 (委托前端) | 2026-02-25~26 | Skills |
| 第五阶段 | 集成测试 | 2026-02-27 | 双方 |
| 第六阶段 | 验收上线 | 2026-02-28 | 双方 |

---

## 六、依赖关系

```
SE-001 (MsgService扩展)
    │
    └──▶ DN-001 (ImService委托) ──▶ DF-001 (前端委托)
              │
SE-002 (OrgService扩展)          │
    │                           │
    └──▶ SN-001 (BusinessService)│
              │                  │
              └──────────────────┘
```

---

## 七、验收标准

### 7.1 功能验收

| 项目 | 验收项 | 负责方 |
|------|--------|--------|
| **SkillImService** | 会话列表、创建、已读标记、联系人管理 | Skills |
| **SkillGroupService** | 群组创建、成员管理、群公告、角色设置 | Skills |
| **前端页面** | 会话列表、聊天页面、联系人页面交互 | Skills |
| **SkillMsgService 扩展** | 消息历史、已读标记、消息撤回 | SEC |
| **SkillOrgService 扩展** | 组织同步、部门创建、用户创建 | SEC |
| **SkillBusinessService** | 业务分类、业务场景管理 | SEC |

### 7.2 性能验收

| 项目 | 标准 |
|------|------|
| 会话列表加载 | < 500ms |
| 联系人搜索 | < 200ms |
| 消息发送响应 | < 500ms |
| 页面加载 | < 2s |

---

## 八、交付物清单

| 交付物 | 负责方 | 状态 |
|--------|--------|------|
| SkillMsgService 扩展代码 | SEC | 待开发 |
| SkillOrgService 扩展代码 | SEC | 待开发 |
| SkillBusinessService 完整代码 | SEC | 待开发 |
| **SkillImService 完整代码** | Skills | 委托 |
| **SkillGroupService 完整代码** | Skills | 委托 |
| **IM 前端交互页面** | Skills | 委托 |
| API 集成测试报告 | 双方 | 待测试 |

---

## 九、联系方式

| 角色 | 联系方式 |
|------|----------|
| **SEC 负责人** | SEC Team |
| **Skills 负责人** | Skills Team |
| **技术支持** | SDK 0.7.3 规范 |

---

*此协作任务文档由 SEC 团队制定，请 Skills 团队确认后开始协作开发。*
