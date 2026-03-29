# SE SDK 3.0.1 接口变更回复

**发件人**: SE SDK 团队  
**收件人**: Apex MVP 团队  
**日期**: 2026-03-29  
**主题**: RE: SE SDK 3.0.1 接口变更确认与兼容性问题咨询

---

## 一、概述

感谢 Apex MVP 团队的详细问询。本文档针对 SE SDK 3.0.1 接口变更进行逐一回复，并提供迁移指导。

---

## 二、接口变更回复

### 2.1 TodoDTO 字段变更

| 问询项 | SE SDK 3.0.1 实际情况 | 回复 |
|--------|----------------------|------|
| `getToUser()` → `getToUserId()` | ✅ 确认变更 | 请统一使用 `getToUserId()` |
| `getCreatedAt()` → `getCreateTime()` | ✅ 确认变更 | 请统一使用 `getCreateTime()`，返回类型为 `Long` |
| `type` 为 `TodoType` 枚举 | ✅ 确认变更 | 使用枚举类型，支持 `INVITATION`, `DELEGATION`, `REMINDER`, `APPROVAL`, `ACTIVATION`, `SCENE_NOTIFICATION` |

**迁移示例**:
```java
// 旧版本
String toUser = todo.getToUser();
Date createdAt = todo.getCreatedAt();
String type = todo.getType();

// 新版本
String toUserId = todo.getToUserId();
Long createTime = todo.getCreateTime();
TodoType type = todo.getType();
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\TodoDTO.java`

---

### 2.2 TodoService 方法签名变更

| 问询项 | SE SDK 3.0.1 实际情况 | 回复 |
|--------|----------------------|------|
| 新增 `userId` 参数 | ✅ 确认变更 | 用于权限校验和操作审计，必须传入当前操作用户ID |
| `viewTodo` 方法移除 | ✅ 确认移除 | 请使用 `getTodo(String todoId)` 获取详情 |

**新签名**:
```java
boolean acceptTodo(String userId, String todoId);
boolean rejectTodo(String userId, String todoId);
boolean completeTodo(String userId, String todoId);
boolean approveTodo(String userId, String todoId, boolean approved, String comment);
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\TodoService.java`

---

### 2.3 PageResult 方法变更

| 问询项 | SE SDK 3.0.1 实际情况 | 回复 |
|--------|----------------------|------|
| `getData()` → `getItems()` | ✅ 确认变更 | 请统一使用 `getItems()` |

**迁移示例**:
```java
// 旧版本
List<TodoDTO> data = pageResult.getData();

// 新版本
List<TodoDTO> items = pageResult.getItems();
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\PageResult.java`

---

### 2.4 OrganizationService 返回类型变更

| 问询项 | SE SDK 3.0.1 实际情况 | 回复 |
|--------|----------------------|------|
| 返回 `CompletableFuture` | ❌ 未变更 | SE SDK 3.0.1 中 `OrganizationService` 返回类型为**同步返回**，不是异步 |
| `getUserDepartment` 方法 | ❌ 不存在 | SE SDK 3.0.1 无此方法，请使用 `getDepartmentMembers()` |

**实际接口定义**:
```java
public interface OrganizationService {
    DepartmentInfo getDepartment(String departmentId);
    List<String> getDepartmentMembers(String departmentId);
    List<String> getAllDepartmentMembers(String departmentId);
    OrganizationInfo getOrganization(String organizationId);
    List<String> getOrganizationDepartments(String organizationId);
}
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\spi\org\OrganizationService.java`

---

### 2.5 A2AMessage 接口变更

| 问询项 | SE SDK 3.0.1 实际情况 | 回复 |
|--------|----------------------|------|
| 方法重命名 | ✅ 确认变更 | 语义更清晰，区分消息ID和消息类型 |
| `A2APriority` 类 | ❌ 不存在 | SE SDK 3.0.1 使用 `int priority` 字段，范围 1-10 |

**方法变更对照**:
| 原方法 | 新方法 |
|--------|--------|
| `getId()` | `getMessageId()` |
| `getFrom()` | `getFromAgentId()` |
| `getTo()` | `getToAgentId()` |
| `getType()` | `getMessageType()` |

**A2AMessageType 枚举变更**:
| 原枚举值 | 新枚举值 |
|----------|----------|
| `REQUEST` | `TASK_REQUEST` |
| `DATA` | `DATA_REQUEST` |

**文件路径**: 
- `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\a2a\A2AMessage.java`
- `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\a2a\A2AMessageType.java`

---

### 2.6 FusionTemplate 相关接口变更

| 问询项 | SE SDK 3.0.1 实际情况 | 回复 |
|--------|----------------------|------|
| 实体类改为接口 | ✅ 部分确认 | SDK API 层定义接口，SE SDK 提供实体实现类 |
| MVP 层如何创建对象 | ✅ 使用 Entity 类 | SE SDK 提供了完整的 Entity 实现类 |

**可用的 Entity 实现类**:

| 接口 | Entity 实现类 | 绝对路径 |
|------|--------------|---------|
| `FusedWorkflowTemplate` | `FusedWorkflowTemplateEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusedWorkflowTemplateEntity.java` |
| `FusionRequest` | `FusionRequestEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusionRequestEntity.java` |
| `FusedRole` | `FusedRoleEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusedRoleEntity.java` |
| `FusionStrategy` | `FusionStrategyEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusionStrategyEntity.java` |
| `FusionTemplateQueryRequest` | `FusionTemplateQueryRequestEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\FusionTemplateQueryRequestEntity.java` |
| `ConflictResolutionItem` | `ConflictResolutionItemEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\ConflictResolutionItemEntity.java` |
| `ConflictResolutionRequest` | `ConflictResolutionRequestEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\ConflictResolutionRequestEntity.java` |
| `CapabilityBindingDef` | `CapabilityBindingDefEntity` | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\fusion\CapabilityBindingDefEntity.java` |

**使用示例**:
```java
// 创建融合请求
FusionRequestEntity request = new FusionRequestEntity();
request.setEnterpriseProcedureId("proc-001");
request.setSkillId("skill-001");
request.setFusionStrategy(new FusionStrategyEntity());

// 使用 Builder 模式
FusedWorkflowTemplateEntity template = new FusedWorkflowTemplateEntity();
template.setName("融合模板");
template.setEnterpriseProcedureId("proc-001");
```

---

### 2.7 UserService 接口变更

| 问询项 | SE SDK 3.0.1 实际情况 | 回复 |
|--------|----------------------|------|
| `UserInfo` 包路径变更 | ✅ 确认变更 | 新路径为 `net.ooder.scene.core.spi.user.UserInfo` |
| `validateUsers` 返回值 | ✅ 确认 | 返回**不存在的用户ID列表**，空列表表示全部有效 |

**实际接口定义**:
```java
public interface UserService {
    Map<String, UserInfo> getUsers(List<String> userIds);
    UserInfo getUser(String userId);
    boolean userExists(String userId);
    List<String> validateUsers(List<String> userIds);  // 返回无效用户ID列表
}
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\spi\user\UserService.java`

---

### 2.8 LLM 服务接口变更

| 问询项 | SE SDK 3.0.1 实际情况 | 回复 |
|--------|----------------------|------|
| `SceneChatRequest.getPrompt()` | ❌ 从未存在 | SE SDK 使用 `getMessages()` 获取消息列表 |
| `ChatResponse` 方法移除 | ⚠️ 需确认 | SE SDK 3.0.1 无 `ChatResponse` 类，请确认使用的类名 |

**SceneChatRequest 使用方式**:
```java
SceneChatRequest request = new SceneChatRequest();
request.user("请帮我分析这段代码");
request.system("你是一个代码分析助手");

// 获取消息列表
List<SceneChatRequest.Message> messages = request.getMessages();

// 或使用快捷构造
SceneChatRequest request = SceneChatRequest.of("用户消息");
```

**文件路径**: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\llm\SceneChatRequest.java`

---

## 三、DTO 类字段缺失回复

| DTO 类 | SE SDK 3.0.1 情况 | 回复 |
|--------|------------------|------|
| `SceneGroupConfigDTO` | SE SDK 使用 `SceneGroupConfig` | 请使用 `net.ooder.scene.group.config.SceneGroupConfig` |
| `RoleDefinitionDTO` | SE SDK 无此 DTO | 请使用 `ProcedureRoleEntity` 或 `FusedRoleEntity` |
| `StepDefinitionDTO` | SE SDK 无此 DTO | 请使用 `ProcedureStepEntity` |
| `MenuItemDTO` | SE SDK 无此 DTO | 请在 MVP 层自定义 |

**建议**: MVP 层可定义自己的 DTO 类，与 SE SDK 实体类进行转换。

---

## 四、请求确认回复

### 4.1 接口稳定性

✅ **确认**: 上述变更为最终版本，后续 3.0.x 版本将保持兼容。

### 4.2 迁移指南

✅ **已提供**: 详见 `e:\github\ooder-sdk\scene-engine\docs\se-sdk-v3.0.1-change-log.md`

### 4.3 DTO 使用

✅ **建议**: 
- 使用 SE SDK 提供的 Entity 类创建对象
- MVP 层可定义内部 DTO，与 Entity 进行转换
- 使用 Builder 模式简化对象创建

### 4.4 异步处理

❌ **澄清**: `OrganizationService` 和 `UserService` 均为**同步接口**，无需异步处理。

---

## 五、附录

### 5.1 关键文件路径

| 文件 | 绝对路径 |
|------|---------|
| TodoDTO.java | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\TodoDTO.java` |
| TodoService.java | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\TodoService.java` |
| PageResult.java | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\PageResult.java` |
| OrganizationService.java | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\spi\org\OrganizationService.java` |
| A2AMessage.java | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\a2a\A2AMessage.java` |
| UserService.java | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\spi\user\UserService.java` |
| SceneChatRequest.java | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\llm\SceneChatRequest.java` |

### 5.2 Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>3.0.1</version>
</dependency>
```

**本地仓库路径**: `D:\maven\.m2\repository\net\ooder\scene-engine\3.0.1\`

---

## 六、联系方式

如有其他问题，请联系 SE SDK 开发团队。

---

**文档维护者**: SE SDK Team  
**最后更新**: 2026-03-29
