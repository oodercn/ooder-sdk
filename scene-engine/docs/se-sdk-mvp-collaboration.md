# SE SDK 与 MVP 层 TodoService 集成协作说明

> **文档版本**: v1.0  
> **创建日期**: 2026-03-28  
> **状态**: 待评审

---

## 一、背景

当前 MVP Skill Scene 模块已有待办功能实现 (`TodoServiceSdkImpl`)， SE SDK 在 v3.0.1 版本中新增了完整的 `TodoService` 接口，需要协调两者的集成关系。

## 二、接口对比

### MVP 层 TodoService 接口 (简化版)
```java
// 文件: e:\apex\app\src\main\java\net\ooder\mvp\skill\scene\service\TodoService.java
public interface TodoService {
    // 查询
    PageResult<TodoDTO> listMyTodos(String userId, String status, int pageNum, int pageSize);
    PageResult<TodoDTO> listMyTodos(String userId, String status, String type, int pageNum, int pageSize);
    
    // 统计
    Map<String, Integer> countByType(String userId);
    
    // 操作
    boolean acceptTodo(String userId, String todoId);
    boolean rejectTodo(String userId, String todoId);
    boolean completeTodo(String userId, String todoId);
    boolean approveTodo(String userId, String todoId);
    
    // 创建
    boolean createInvitationTodo(String sceneGroupId, String fromUserId, String toUserId, String role);
    boolean createDelegationTodo(String sceneGroupId, String fromUserId, String toUserId, String title, Long deadline);
    boolean createReminderTodo(String sceneGroupId, String userId, String title, Long deadline);
    boolean createActivationTodo(String userId, String installId, String capabilityId, String capabilityName);
    boolean createApprovalTodo(String sceneGroupId, String fromUserId, String toUserId, String title, String description);
    boolean createSceneNotificationTodo(String sceneGroupId, String userId, String title, String description);
    
    // 删除
    boolean deleteTodo(String todoId);
}
```

### SE SDK TodoService 接口 (完整版)
```java
// 文件: e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\TodoService.java
public interface TodoService {
    // ========== 待办创建 ==========
    TodoDTO createInvitationTodo(InvitationTodoRequest request);
    TodoDTO createDelegationTodo(DelegationTodoRequest request);
    TodoDTO createApprovalTodo(ApprovalTodoRequest request);
    TodoDTO createReminderTodo(ReminderTodoRequest request);
    TodoDTO createActivationTodo(ActivationTodoRequest request);
    TodoDTO createSceneNotificationTodo(SceneNotificationRequest request);
    TodoDTO createTodo(TodoDTO todo);
    
    // ========== 待办查询 ==========
    PageResult<TodoDTO> listUserTodos(String userId, TodoQuery query);
    TodoDTO getTodo(String todoId);
    Map<String, Integer> countByType(String userId);
    PageResult<TodoDTO> listSceneGroupTodos(String sceneGroupId, TodoQuery query);
    int getPendingCount(String userId);
    
    // ========== 待办操作 ==========
    boolean acceptTodo(String userId, String todoId);
    boolean rejectTodo(String userId, String todoId);
    boolean completeTodo(String userId, String todoId);
    boolean approveTodo(String userId, String todoId, boolean approved, String comment);
    boolean cancelTodo(String todoId, String reason);
    boolean deleteTodo(String todoId);
    
    // ========== 待办订阅 ==========
    void subscribe(String userId, TodoChangeListener listener);
    void unsubscribe(String userId, TodoChangeListener listener);
    
    // ========== 过期处理 ==========
    int processExpiredTodos();
}
```

## 三、差异分析

| 功能 | MVP 层 | SE SDK | 说明 |
|------|--------|-------|------|
| 创建待办 | 简单参数 | Request 对象 | SE SDK 使用请求对象模式，更灵活 |
| 查询待办 | status + type | TodoQuery 对象 | SE SDK 支持更丰富的查询条件 |
| 订阅功能 | 无 | 有 | SE SDK 新增待办变更监听 |
| 过期处理 | 无 | 有 | SE SDK 新增过期待办处理 |
| 取消待办 | 有原因参数 | 有原因参数 | 一致 |

## 四、集成方案

### 方案 A: MVP 层适配 SE SDK 接口（推荐）
MVP 层修改 `TodoServiceSdkImpl`，实现 SE SDK 的 `TodoService` 接口。
**优点:**
- 统一接口，减少重复代码
- MVP 层可以直接使用 SE SDK 的新功能
- 便于维护和扩展

**实施步骤:**

1. **SE SDK 发布** (已完成 ✅)
   - SE SDK 3.0.2 版本已包含完整的 TodoService 接口
   - 文件位置: `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\`

2. **MVP 层更新依赖** (待执行)
   ```xml
   <!-- pom.xml 更新依赖版本 -->
   <dependency>
       <groupId>net.ooder</groupId>
       <artifactId>scene-engine</artifactId>
       <version>3.0.2</version>
   </dependency>
   ```

3. **MVP 层代码修改** (待执行)
   - 删除 `TodoServiceSdkImpl` 类
   - 修改 `TodoController` 注入 SE SDK 的 `TodoService`

### 方案 B: MVP 层继续使用自己的实现（不推荐）
MVP 层继续使用自己的 `TodoServiceSdkImpl`，但需要手动同步 SE SDK 的新功能。
**缺点:**
- 接口不一致，维护成本高
- 无法使用 SE SDK 的订阅、过期处理等新功能
- 代码重复

## 五、协作任务

### SE SDK 侧（已完成）
- [x] 发布 SE SDK 3.0.2 版本到 Maven 仓库
- [x] 提供完整的 TodoService 接口及实现
- [x] 提供相关模型类（TodoDTO, TodoQuery, TodoStatus, TodoType 等）

### MVP 层（待执行）
- [ ] 更新 pom.xml 依赖到 SE SDK 3.0.2
- [ ] 修改 `TodoServiceSdkImpl` 实现 SE SDK 的 `TodoService` 接口
  - 或者直接注入 SE SDK 的 `TodoService` bean
- [ ] 更新 `TodoController` 使用新接口
- [ ] 测试集成是否正常

## 六、时间线

| 阶段 | 任务 | 负责方 | 状态 |
|------|------|--------|------|
| 第1周 | SE SDK 发布 3.0.2 | SE Team | ✅ 已完成 |
| 第1周 | MVP 层更新依赖 | MVP Team | 待执行 |
| 第2周 | MVP 层代码适配 | MVP Team | 待执行 |
| 第2周 | 集成测试 | SE + MVP | 待执行 |

## 七、文件路径

| 文件 | 绝对路径 |
|------|---------|
| SE SDK TodoService | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\TodoService.java` |
| SE SDK TodoServiceImpl | `e:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\todo\TodoServiceImpl.java` |
| MVP TodoService (旧) | `e:\apex\app\src\main\java\net\ooder\mvp\skill\scene\service\TodoService.java` |
| MVP TodoServiceSdkImpl | `e:\apex\app\src\main\java\net\ooder\mvp\skill\scene\service\impl\TodoServiceSdkImpl.java` |
| MVP TodoController | `e:\apex\app\src\main\java\net\ooder\mvp\skill\scene\controller\TodoController.java` |

---
**文档维护者**: SE Team  
**最后更新**: 2026-03-28
