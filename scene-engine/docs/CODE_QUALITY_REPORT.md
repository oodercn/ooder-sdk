# 代码质量检查报告

**检查日期**: 2026-03-06  
**检查范围**: scene-engine/src/main/java/net/ooder/scene/skill  
**检查项**: TODO、null返回、空集合、伪实现

---

## 一、问题统计

| 问题类型 | 数量 | 严重程度 |
|----------|------|----------|
| TODO/FIXME | 3 | 中 |
| return null | 27 | 高 |
| 空集合返回 | 10 | 中 |
| 伪实现/简化实现 | 28 | 高 |
| **总计** | **68** | - |

---

## 二、关键问题清单

### 2.1 高优先级（需要立即修复）

#### 1. RichSkill.java - 依赖查询返回 null
```java
// Line 121
public List<RichSkill> getDependencies() {
    // 简化实现
    return null;  // ❌ 应该返回空列表
}
```
**影响**: 调用方可能抛出 NullPointerException  
**修复**: 返回 `Collections.emptyList()` 或实际查询

#### 2. RichSkill.java - 多个方法返回 null
```java
// Lines 329, 335, 349, 355, 369, 375, 419, 425, 450
return null;
```
**影响**: 元数据获取可能返回 null，导致调用方 NPE  
**修复**: 使用 Optional 或返回默认值

#### 3. InstallCoordinator.java - 安装方法伪实现
```java
// Lines 200-201
// 简化实现
Thread.sleep(1000); // 模拟安装耗时
```
**影响**: 无法真正安装技能  
**修复**: 集成实际的 SkillInstaller

#### 4. ToolOrchestratorImpl.java - 解析工具调用返回 null
```java
// Line 369
return null;
```
**影响**: LLM 响应解析失败时返回 null  
**修复**: 返回空列表

#### 5. PermissionServiceImpl.java - 权限检查返回 null
```java
// Lines 64, 69
return null;
```
**影响**: 权限查询返回 null  
**修复**: 返回 Optional 或默认权限

#### 6. SceneSkillClassifierImpl.java - 元数据获取返回 null
```java
// Lines 202, 208
return null;
```
**影响**: 分类检测失败  
**修复**: 返回空 Map

### 2.2 中优先级（需要优化）

#### 7. ConversationServiceImpl.java - 历史记录返回空列表
```java
// Line 233
return new ArrayList<>();
```
**影响**: 每次调用创建新对象  
**修复**: 使用 `Collections.emptyList()`

#### 8. KnowledgeBaseServiceImpl.java - 同步索引简化实现
```java
// Line 382
// 简化实现：同步索引
```
**影响**: 索引可能不一致  
**修复**: 实现异步索引机制

#### 9. 多个方法的空集合返回
- `PermissionServiceImpl.java`: Lines 150, 162
- `SkillSDKAdapter.java`: Lines 200, 214, 217
- `LlmEmbeddingServiceAdapter.java`: Line 66

**修复**: 统一使用 `Collections.emptyList()`

---

## 三、修复建议

### 3.1 立即修复项

1. **所有返回 null 的方法**改为返回 Optional 或空集合
2. **RichSkill.getDependencies()** 实现实际查询逻辑
3. **InstallCoordinator.installSkill()** 集成真实安装器
4. **ToolOrchestratorImpl.parseToolCalls()** 添加错误处理

### 3.2 优化项

1. 统一空集合返回，使用 `Collections.emptyList()`
2. 添加 `@Nullable` 注解标记可能返回 null 的方法
3. 添加参数校验，避免 null 参数

---

## 四、修复优先级

| 优先级 | 文件 | 问题 | 预计工作量 |
|--------|------|------|-----------|
| P0 | RichSkill.java | 返回 null | 2小时 |
| P0 | InstallCoordinator.java | 伪实现 | 4小时 |
| P1 | ToolOrchestratorImpl.java | 返回 null | 1小时 |
| P1 | PermissionServiceImpl.java | 返回 null | 1小时 |
| P2 | 其他文件 | 空集合优化 | 2小时 |

---

## 五、代码规范建议

1. **禁止返回 null**: 使用 Optional 或空集合
2. **统一空集合**: 使用 `Collections.emptyList()` 而非 `new ArrayList<>()`
3. **标记可空性**: 使用 `@Nullable` 和 `@NotNull`
4. **参数校验**: 公共方法入口校验参数非空

---

**报告生成**: 2026-03-06  
**建议修复时间**: 2-3天
