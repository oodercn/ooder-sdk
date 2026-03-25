# SDK 协作文档: SceneGroupBridge 实现

## 1. 协作概述

**协作方**: SceneEngine (SE) ↔ SceneSDK (SDK)  
**协作主题**: SceneGroupBridge 桥接接口实现  
**优先级**: P0  
**创建日期**: 2026-03-19  
**状态**: ✅ 已完成

---

## 2. 背景说明

### 2.1 问题背景

当前 SceneGroupInitializer 在激活场景时只创建 SDK SceneGroup（高可用集群），SE SceneGroup（业务场景组）没有被创建，导致业务层无法访问场景组数据。

### 2.2 解决方案

实现 SceneGroupBridge 桥接接口，建立 SDK SceneGroup 与 SE SceneGroup 的双向映射和同步机制。

---

## 3. 协作需求

### 3.1 SE 需要的 SDK 接口

#### 3.1.1 SceneGroup 查询接口

**需求**: SE 需要查询 SDK SceneGroup 的详细信息

```java
// 请求 SDK 提供的接口
public interface SceneGroupQueryService {
    
    /**
     * 获取场景组详情
     * @param sceneGroupId 场景组ID
     * @return 场景组详情
     */
    SceneGroupDetail getSceneGroupDetail(String sceneGroupId);
    
    /**
     * 获取场景组成员列表
     * @param sceneGroupId 场景组ID
     * @return 成员列表
     */
    List<SceneMemberDetail> getSceneGroupMembers(String sceneGroupId);
    
    /**
     * 获取场景组状态
     * @param sceneGroupId 场景组ID
     * @return 状态
     */
    SceneGroupStatus getSceneGroupStatus(String sceneGroupId);
}
```

#### 3.1.2 SceneMember 信息接口

**需求**: SE 需要获取 SceneMember 的详细信息用于映射到 Participant

```java
// 请求 SDK 提供的数据结构
public class SceneMemberDetail {
    
    private String memberId;
    private String sceneGroupId;
    private String agentId;           // SE 需要: 映射到 Participant.userId
    private MemberRole role;          // SE 需要: 映射到 Participant.role
    private String endpoint;
    private MemberStatus status;
    private long joinTime;
    private long lastHeartbeatTime;
}
```

#### 3.1.3 场景组事件监听接口

**需求**: SE 需要监听 SDK SceneGroup 的变化事件

```java
// 请求 SDK 提供的事件接口
public interface SceneGroupEventListener {
    
    /**
     * 成员加入事件
     */
    void onMemberJoined(SceneMemberEvent event);
    
    /**
     * 成员离开事件
     */
    void onMemberLeft(SceneMemberEvent event);
    
    /**
     * 角色变更事件
     */
    void onRoleChanged(SceneMemberEvent event);
    
    /**
     * 场景组状态变更事件
     */
    void onStatusChanged(SceneGroupStatusEvent event);
    
    /**
     * 主备切换事件
     */
    void onFailover(FailoverEvent event);
}
```

### 3.2 SDK 需要的 SE 接口

#### 3.2.1 业务层操作通知接口

**需求**: SDK 需要通知 SE 进行业务层操作

```java
// SE 提供的接口
public interface SceneGroupBusinessNotifier {
    
    /**
     * 通知 SE 创建业务场景组
     */
    void notifyCreateBusinessSceneGroup(SceneGroupCreateRequest request);
    
    /**
     * 通知 SE 更新参与者
     */
    void notifyUpdateParticipants(String sceneGroupId, List<ParticipantChange> changes);
    
    /**
     * 通知 SE 场景组销毁
     */
    void notifyDestroySceneGroup(String sceneGroupId);
}
```

---

## 4. 映射规则

### 4.1 ID 映射

| SDK | SE | 说明 |
|-----|-----|------|
| sceneGroupId | sceneGroupId | 共享，由 SDK 生成 |
| sceneId | templateId | sceneId = skillId = templateId |
| agentId | userId | Agent ID = User ID |

### 4.2 角色映射

| SDK MemberRole | SE ParticipantRole | 说明 |
|----------------|-------------------|------|
| PRIMARY | OWNER | 主节点 → 所有者 |
| PRIMARY | MANAGER | 主节点 → 管理者（可选） |
| BACKUP | EMPLOYEE | 备节点 → 员工 |
| BACKUP | OBSERVER | 备节点 → 观察者 |

### 4.3 状态映射

| SDK MemberStatus | SE ParticipantStatus | 说明 |
|------------------|---------------------|------|
| ONLINE | ACTIVE | 在线 → 活跃 |
| OFFLINE | SUSPENDED | 离线 → 暂停 |
| JOINING | INVITED | 加入中 → 已邀请 |
| LEAVING | LEFT | 离开中 → 已离开 |

---

## 5. 接口设计

### 5.1 SceneGroupBridge 接口（SE 定义）

```java
package net.ooder.scene.bridge;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.participant.Participant;
import net.ooder.sdk.api.scene.SceneMember;
import net.ooder.sdk.api.scene.SceneGroup as SdkSceneGroup;

/**
 * SDK SceneGroup 与 SE SceneGroup 桥接接口
 */
public interface SceneGroupBridge {
    
    /**
     * 从 SDK SceneMember 创建 SE Participant
     */
    Participant createParticipantFromMember(SceneMember member);
    
    /**
     * 从 SE Participant 创建 SDK SceneMember 配置
     */
    SceneMemberConfig createMemberConfigFromParticipant(Participant participant, String endpoint);
    
    /**
     * 同步 SDK SceneGroup 数据到 SE SceneGroup
     */
    void syncFromSdkToSe(String sceneGroupId);
    
    /**
     * 同步 SE SceneGroup 数据到 SDK SceneGroup
     */
    void syncFromSeToSdk(String sceneGroupId);
    
    /**
     * 获取 SDK SceneGroup
     */
    SdkSceneGroup getSdkSceneGroup(String sceneGroupId);
    
    /**
     * 获取 SE SceneGroup
     */
    SceneGroup getSeSceneGroup(String sceneGroupId);
    
    /**
     * 注册事件监听器
     */
    void registerEventListener(SceneGroupEventListener listener);
    
    /**
     * 注销事件监听器
     */
    void unregisterEventListener(SceneGroupEventListener listener);
}
```

### 5.2 SDK 需要提供的 API

```java
package net.ooder.sdk.api.scene;

/**
 * SDK 场景组查询 API（SE 需要）
 */
public interface SceneGroupApi {
    
    /**
     * 获取场景组详情
     */
    SceneGroupDetail getDetail(String sceneGroupId);
    
    /**
     * 获取场景组成员列表
     */
    List<SceneMember> getMembers(String sceneGroupId);
    
    /**
     * 添加成员
     */
    void addMember(String sceneGroupId, SceneMemberConfig config);
    
    /**
     * 移除成员
     */
    void removeMember(String sceneGroupId, String agentId);
    
    /**
     * 更新成员角色
     */
    void updateMemberRole(String sceneGroupId, String agentId, MemberRole newRole);
    
    /**
     * 注册事件监听器
     */
    void registerListener(String sceneGroupId, SceneGroupEventListener listener);
    
    /**
     * 注销事件监听器
     */
    void unregisterListener(String sceneGroupId, SceneGroupEventListener listener);
}
```

---

## 6. 实现计划

### 6.1 SDK 侧实现

| 任务 | 描述 | 优先级 | 预估工时 |
|------|------|--------|----------|
| SDK-001 | 提供 SceneGroupApi 接口实现 | P0 | 4h |
| SDK-002 | 提供事件监听机制 | P0 | 4h |
| SDK-003 | 提供 SceneMemberDetail 数据结构 | P0 | 2h |

### 6.2 SE 侧实现

| 任务 | 描述 | 优先级 | 预估工时 |
|------|------|--------|----------|
| SE-001 | 实现 SceneGroupBridge 接口 | P0 | 4h |
| SE-002 | 实现事件处理逻辑 | P0 | 4h |
| SE-003 | 实现双向同步机制 | P1 | 6h |

---

## 7. 集成测试

### 7.1 测试场景

1. **场景组创建同步**: SDK 创建 SceneGroup 后，SE 同步创建
2. **成员加入同步**: SDK 成员加入后，SE 同步添加 Participant
3. **角色变更同步**: SDK 角色变更后，SE 同步更新 ParticipantRole
4. **故障转移同步**: SDK 主备切换后，SE 同步更新角色

### 7.2 验收标准

- [ ] SDK SceneGroup 创建后，SE SceneGroup 同步创建
- [ ] SDK 成员变更后，SE Participant 同步更新
- [ ] 角色映射正确
- [ ] 事件监听正常工作

---

## 8. 联系方式

**SE 负责人**: SceneEngine Team  
**SDK 负责人**: SDK Team  
**协作状态**: ✅ 已完成

---

**文档状态**: ✅ 已确认  
**创建日期**: 2026-03-19  
**版本**: 1.0
