# SceneGroup 详情页 SE SDK 接口确认

## 接口确认状态

### 7.1 SE SDK 已提供的接口

| 接口 | 方法 | 确认状态 |
|------|------|----------|
| `SceneGroup.getAllSnapshots()` | 获取快照列表 | ✅ 已实现 |
| `UserSceneGroup` | 用户场景组关联 | ✅ 已实现 |
| `SceneGroupManager.getSceneGroup()` | 获取场景组 | ✅ 已实现 |

### 7.2 需要新增的接口

| 接口 | 方法 | 优先级 |
|------|------|--------|
| `SceneGroupManager.getEventLog(sceneGroupId)` | 获取事件日志 | 高 |
| `UserSceneGroupManager.getUserSceneGroups(userId)` | 用户场景组列表 | 中 |

## 已实现接口详情

### SceneGroup.getAllSnapshots()

**文件**: `net.ooder.scene.group.SceneGroup`

```java
/**
 * 获取所有快照
 */
public List<SceneSnapshot> getAllSnapshots() {
    return new ArrayList<>(snapshots);
}
```

### UserSceneGroup

**文件**: `net.ooder.scene.bridge.UserSceneGroup`

**功能**:
- `getSceneGroupId()` - 获取场景组ID
- `getName()` / `setName()` - 名称管理
- `getDescription()` / `setDescription()` - 描述管理
- `getParticipants()` - 获取参与者列表
- `addParticipant()` / `removeParticipant()` - 参与者管理
- `getCapabilityBindings()` - 获取能力绑定
- `activate()` / `suspend()` - 状态管理
- `archive()` / `restore()` - 归档/恢复

## 需要实现的接口

### SceneGroupManager.getEventLog()

```java
/**
 * 获取场景组事件日志
 * 
 * @param sceneGroupId 场景组ID
 * @param limit 限制条数
 * @return 事件日志列表
 */
List<SceneGroupEvent> getEventLog(String sceneGroupId, int limit);
```

### UserSceneGroupManager.getUserSceneGroups()

```java
/**
 * 获取用户参与的所有场景组
 * 
 * @param userId 用户ID
 * @return 用户场景组列表
 */
List<UserSceneGroup> getUserSceneGroups(String userId);
```

## 状态

- [x] `SceneGroup.getAllSnapshots()` 已实现
- [x] `UserSceneGroup` 已实现
- [x] `SceneGroupManager.getEventLog()` 已实现
- [x] `SceneGroupManager.getUserSceneGroups()` 已实现
- [x] `SceneGroupEvent` 事件日志类已实现

## 联系方式

- MVP 团队
- SE SDK 团队
