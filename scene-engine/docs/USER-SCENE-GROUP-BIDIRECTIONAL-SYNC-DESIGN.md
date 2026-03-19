# UserSceneGroup 双向同步架构设计

## 1. 核心概念

### 1.1 UserSceneGroup 定义

**UserSceneGroup** 是 SE 层的核心业务模型，代表用户可参与的业务场景组。

**核心职责**:
- 用户可参与的操作入口
- 业务层与基础设施层的桥梁
- 双向同步的协调者

### 1.2 架构层次

```
┌─────────────────────────────────────────────────────────────────────┐
│                        应用层 (Application)                          │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    可视化界面 / 外部程序                       │   │
│  │         • Web UI • CLI • API Client                          │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
                                │
                                │ 调用
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        业务层 (Business Layer)                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    UserSceneGroup                            │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │              用户可参与的操作                          │    │   │
│  │  │  • 增加协作者 (addCollaborator)                       │    │   │
│  │  │  • 增加新 Skills (addSkill)                           │    │   │
│  │  │  • 场景协作 (startCollaboration)                      │    │   │
│  │  │  • 能力绑定 (bindCapability)                          │    │   │
│  │  │  • 知识库绑定 (bindKnowledgeBase)                     │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │                                                              │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │              代理接口 (给上层调用)                      │    │   │
│  │  │  • getAgentStatus() - 获取 Agent 状态                 │    │   │
│  │  │  • getCommunicationLinks() - 获取通讯链路              │    │   │
│  │  │  • getFailoverStatus() - 获取故障转移状态              │    │   │
│  │  │  • getHeartbeatInfo() - 获取心跳信息                   │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
                    │                              │
                    │ 同步操作                      │ 代理查询
                    ▼                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    基础设施层 (Infrastructure Layer)                  │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    SDK SceneGroup                            │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │              Agent 关系                               │    │   │
│  │  │  • SceneMember (agentId, endpoint, role)             │    │   │
│  │  │  • Agent 注册/注销                                    │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │                                                              │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │              通讯关系                                 │    │   │
│  │  │  • 心跳检测 (heartbeat)                              │    │   │
│  │  │  • 故障转移 (failover)                               │    │   │
│  │  │  • 状态共享 (sharedState)                            │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. 双向同步机制

### 2.1 业务层 → 基础设施层（同步操作）

**触发场景**: 用户在业务层执行操作时，同步到底层

```
用户操作
    │
    ▼
UserSceneGroup
    │
    ├── addCollaborator() ──────────────────┐
    │                                        │
    │   ┌────────────────────────────────┐  │
    │   │ 1. 创建 Participant             │  │
    │   │ 2. 同步到 SDK SceneGroup        │──┼──→ SDK SceneGroup.join()
    │   │    - agentId = userId           │  │
    │   │    - role = 映射后的集群角色     │  │
    │   └────────────────────────────────┘  │
    │                                        │
    ├── addSkill() ─────────────────────────┤
    │                                        │
    │   ┌────────────────────────────────┐  │
    │   │ 1. 安装 Skill                   │  │
    │   │ 2. 同步到 SDK SceneGroup        │──┼──→ SDK SceneGroup 共享状态
    │   │    - 更新 sharedState           │  │
    │   │    - 通知所有 Agent             │  │
    │   └────────────────────────────────┘  │
    │                                        │
    └── startCollaboration() ───────────────┘
```

### 2.2 基础设施层 → 业务层（代理查询）

**触发场景**: 底层 Agent 状态变化时，通过代理接口提供给上层

```
SDK SceneGroup 变化
    │
    ├── Agent 状态变化 ─────────────────────┐
    │                                        │
    │   ┌────────────────────────────────┐  │
    │   │ SceneMember.status = "offline" │  │
    │   └────────────────────────────────┘  │
    │                                        │
    ▼                                        │
UserSceneGroup (代理接口)                     │
    │                                        │
    ├── getAgentStatus() ────────────────────┤
    │   │                                    │
    │   └── 返回 Agent 状态信息               │
    │       • agentId                        │
    │       • status (online/offline)        │
    │       • lastHeartbeat                  │
    │                                        │
    ├── getCommunicationLinks() ─────────────┤
    │   │                                    │
    │   └── 返回通讯链路信息                   │
    │       • PRIMARY → BACKUP 链路          │
    │       • endpoint 地址                  │
    │                                        │
    └── getFailoverStatus() ─────────────────┘
        │
        └── 返回故障转移状态
            • inProgress
            • failedMemberId
            • newPrimaryId
```

---

## 3. UserSceneGroup 接口设计

### 3.1 用户可参与的操作接口

```java
public interface UserSceneGroup {
    
    // ========== 协作者管理 ==========
    
    /**
     * 增加协作者
     * 
     * @param userId 用户ID
     * @param role 业务角色
     * @return 参与者信息
     */
    Participant addCollaborator(String userId, Participant.Role role);
    
    /**
     * 移除协作者
     */
    void removeCollaborator(String userId);
    
    /**
     * 变更协作者角色
     */
    void changeCollaboratorRole(String userId, Participant.Role newRole);
    
    /**
     * 获取所有协作者
     */
    List<Participant> getCollaborators();
    
    // ========== Skills 管理 ==========
    
    /**
     * 增加新 Skill
     * 
     * @param skillId Skill ID
     * @param config Skill 配置
     */
    SkillBinding addSkill(String skillId, Map<String, Object> config);
    
    /**
     * 移除 Skill
     */
    void removeSkill(String skillId);
    
    /**
     * 更新 Skill 配置
     */
    void updateSkillConfig(String skillId, Map<String, Object> config);
    
    /**
     * 获取所有 Skills
     */
    List<SkillBinding> getSkills();
    
    // ========== 场景协作 ==========
    
    /**
     * 开始场景协作
     * 
     * @param collaborationType 协作类型
     * @param participants 参与者列表
     */
    CollaborationSession startCollaboration(String collaborationType, List<String> participants);
    
    /**
     * 结束场景协作
     */
    void endCollaboration(String sessionId);
    
    /**
     * 获取协作会话
     */
    CollaborationSession getCollaborationSession(String sessionId);
    
    /**
     * 获取所有活跃协作
     */
    List<CollaborationSession> getActiveCollaborations();
    
    // ========== 能力绑定 ==========
    
    /**
     * 绑定能力
     */
    CapabilityBinding bindCapability(String capId, Map<String, Object> config);
    
    /**
     * 解绑能力
     */
    void unbindCapability(String bindingId);
    
    /**
     * 获取能力绑定列表
     */
    List<CapabilityBinding> getCapabilityBindings();
    
    // ========== 知识库绑定 ==========
    
    /**
     * 绑定知识库
     */
    KnowledgeBinding bindKnowledgeBase(String kbId, String layer);
    
    /**
     * 解绑知识库
     */
    void unbindKnowledgeBase(String kbId);
    
    /**
     * 获取知识库绑定列表
     */
    List<KnowledgeBinding> getKnowledgeBaseBindings();
}
```

### 3.2 代理接口（给上层调用）

```java
public interface UserSceneGroupAgentProxy {
    
    // ========== Agent 状态代理 ==========
    
    /**
     * 获取 Agent 状态
     */
    AgentStatusInfo getAgentStatus(String agentId);
    
    /**
     * 获取所有 Agent 状态
     */
    List<AgentStatusInfo> getAllAgentStatuses();
    
    /**
     * 获取主 Agent 信息
     */
    AgentStatusInfo getPrimaryAgent();
    
    /**
     * 获取备份 Agent 列表
     */
    List<AgentStatusInfo> getBackupAgents();
    
    // ========== 通讯链路代理 ==========
    
    /**
     * 获取通讯链路信息
     */
    CommunicationLinkInfo getCommunicationLinks();
    
    /**
     * 获取 Agent 端点地址
     */
    String getAgentEndpoint(String agentId);
    
    /**
     * 获取心跳信息
     */
    HeartbeatInfo getHeartbeatInfo(String agentId);
    
    // ========== 故障转移代理 ==========
    
    /**
     * 获取故障转移状态
     */
    FailoverStatusInfo getFailoverStatus();
    
    /**
     * 手动触发故障转移
     */
    void triggerFailover(String failedAgentId);
    
    // ========== 共享状态代理 ==========
    
    /**
     * 获取共享状态
     */
    Map<String, Object> getSharedState();
    
    /**
     * 更新共享状态
     */
    void updateSharedState(String key, Object value);
}
```

---

## 4. 同步实现机制

### 4.1 业务层 → 基础设施层同步

```java
@Component
public class UserSceneGroupImpl implements UserSceneGroup, UserSceneGroupAgentProxy {
    
    private final SceneGroupManager sdkSceneGroupManager;  // SDK 的
    private final SceneGroupManager seSceneGroupManager;   // SE 的
    private final EventPublisher eventPublisher;
    
    // ========== 协作者管理实现 ==========
    
    @Override
    public Participant addCollaborator(String userId, Participant.Role role) {
        // 1. 创建 SE Participant
        Participant participant = new Participant(
            UUID.randomUUID().toString(),
            userId,
            userId,
            Participant.Type.USER
        );
        participant.setRole(role);
        
        // 2. 添加到 SE SceneGroup
        seSceneGroupManager.addParticipant(sceneGroupId, participant);
        
        // 3. 同步到 SDK SceneGroup
        MemberRole sdkRole = mapToSdkRole(role);
        sdkSceneGroupManager.join(sceneGroupId, userId, sdkRole);
        
        // 4. 发布事件
        eventPublisher.publish(new CollaboratorAddedEvent(sceneGroupId, participant));
        
        return participant;
    }
    
    private MemberRole mapToSdkRole(Participant.Role role) {
        switch (role) {
            case OWNER:
            case MANAGER:
                return MemberRole.PRIMARY;
            case EMPLOYEE:
            case COORDINATOR:
            case LLM_ASSISTANT:
                return MemberRole.BACKUP;
            default:
                return MemberRole.BACKUP;
        }
    }
    
    // ========== Skills 管理实现 ==========
    
    @Override
    public SkillBinding addSkill(String skillId, Map<String, Object> config) {
        // 1. 安装 Skill
        SkillBinding binding = skillRuntime.install(skillId, config);
        
        // 2. 同步到 SDK SceneGroup 共享状态
        Map<String, Object> sharedState = sdkSceneGroupManager.get(sceneGroupId)
            .join()
            .getSharedState();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> skills = (List<Map<String, Object>>) 
            sharedState.computeIfAbsent("skills", k -> new ArrayList<>());
        skills.add(binding.toMap());
        
        sdkSceneGroupManager.updateSharedState(sceneGroupId, sharedState);
        
        // 3. 发布事件
        eventPublisher.publish(new SkillAddedEvent(sceneGroupId, binding));
        
        return binding;
    }
    
    // ========== 代理接口实现 ==========
    
    @Override
    public AgentStatusInfo getAgentStatus(String agentId) {
        // 从 SDK SceneGroup 获取
        SceneMember member = sdkSceneGroupManager.get(sceneGroupId)
            .join()
            .getMember(agentId);
        
        if (member == null) {
            return null;
        }
        
        return AgentStatusInfo.builder()
            .agentId(member.getAgentId())
            .agentName(member.getAgentName())
            .status(member.getStatus())
            .role(member.getRole().name())
            .lastHeartbeat(member.getLastHeartbeat())
            .endpoint(member.getEndpoint())
            .build();
    }
    
    @Override
    public CommunicationLinkInfo getCommunicationLinks() {
        SceneGroup sdkGroup = sdkSceneGroupManager.get(sceneGroupId).join();
        
        return CommunicationLinkInfo.builder()
            .sceneGroupId(sceneGroupId)
            .primaryAgentId(sdkGroup.getPrimary().getAgentId())
            .primaryEndpoint(sdkGroup.getPrimary().getEndpoint())
            .backupAgents(sdkGroup.getBackups().stream()
                .map(m -> AgentInfo.builder()
                    .agentId(m.getAgentId())
                    .endpoint(m.getEndpoint())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
```

### 4.2 基础设施层 → 业务层事件监听

```java
@Component
public class SdkSceneGroupEventListener {
    
    private final UserSceneGroupManager userSceneGroupManager;
    
    /**
     * 监听 SDK SceneGroup 成员状态变化
     */
    @EventListener
    public void onMemberStatusChanged(SceneMemberStatusChangedEvent event) {
        String sceneGroupId = event.getSceneGroupId();
        String agentId = event.getAgentId();
        String newStatus = event.getNewStatus();
        
        // 更新 SE SceneGroup 中的 Participant 状态
        UserSceneGroup userGroup = userSceneGroupManager.getUserSceneGroup(sceneGroupId);
        Participant participant = userGroup.getCollaborators().stream()
            .filter(p -> agentId.equals(p.getUserId()))
            .findFirst()
            .orElse(null);
        
        if (participant != null) {
            if ("online".equals(newStatus)) {
                participant.activate();
            } else if ("offline".equals(newStatus)) {
                participant.suspend();
            }
        }
    }
    
    /**
     * 监听 SDK SceneGroup 故障转移事件
     */
    @EventListener
    public void onFailover(SceneFailoverEvent event) {
        String sceneGroupId = event.getSceneGroupId();
        String failedAgentId = event.getFailedAgentId();
        String newPrimaryId = event.getNewPrimaryId();
        
        // 更新 SE SceneGroup 中的角色
        UserSceneGroup userGroup = userSceneGroupManager.getUserSceneGroup(sceneGroupId);
        
        // 旧主节点降级
        Participant oldPrimary = userGroup.getCollaborators().stream()
            .filter(p -> failedAgentId.equals(p.getUserId()))
            .findFirst()
            .orElse(null);
        if (oldPrimary != null) {
            oldPrimary.setRole(Participant.Role.EMPLOYEE);
        }
        
        // 新主节点升级
        Participant newPrimary = userGroup.getCollaborators().stream()
            .filter(p -> newPrimaryId.equals(p.getUserId()))
            .findFirst()
            .orElse(null);
        if (newPrimary != null) {
            newPrimary.setRole(Participant.Role.OWNER);
        }
    }
}
```

---

## 5. 数据模型

### 5.1 AgentStatusInfo

```java
@Data
@Builder
public class AgentStatusInfo {
    private String agentId;
    private String agentName;
    private String status;        // online, offline, busy
    private String role;          // PRIMARY, BACKUP
    private long lastHeartbeat;
    private String endpoint;
    private int heartbeatMissed;
}
```

### 5.2 CommunicationLinkInfo

```java
@Data
@Builder
public class CommunicationLinkInfo {
    private String sceneGroupId;
    private String primaryAgentId;
    private String primaryEndpoint;
    private List<AgentInfo> backupAgents;
    private long lastUpdate;
    
    @Data
    @Builder
    public static class AgentInfo {
        private String agentId;
        private String endpoint;
    }
}
```

### 5.3 FailoverStatusInfo

```java
@Data
@Builder
public class FailoverStatusInfo {
    private String sceneGroupId;
    private boolean inProgress;
    private String failedAgentId;
    private String newPrimaryId;
    private long startTime;
    private String phase;  // detecting, electing, switching, completed
}
```

---

## 6. 总结

### 6.1 核心设计原则

1. **UserSceneGroup 作为桥梁**: 连接业务层和基础设施层
2. **双向同步**: 业务操作同步到底层，底层状态代理到上层
3. **关注点分离**: 业务层关注用户操作，基础设施层关注通讯和高可用

### 6.2 同步方向

| 方向 | 触发 | 内容 | 目的 |
|------|------|------|------|
| **业务层 → 基础设施层** | 用户操作 | 协作者、Skills、协作 | 同步业务变更 |
| **基础设施层 → 业务层** | Agent 变化 | 状态、链路、故障转移 | 提供底层信息 |

### 6.3 下一步

1. **实现 UserSceneGroup 接口**
2. **实现 SDK 事件监听器**
3. **编写集成测试**

---

**设计日期**: 2026-03-19  
**设计版本**: SceneEngine 2.3.1
