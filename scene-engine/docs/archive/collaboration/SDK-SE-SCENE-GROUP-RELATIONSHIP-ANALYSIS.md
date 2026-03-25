# SDK SceneGroup 与 SE SceneGroup 理论关联分析

## 1. 概念设计分析

### 1.1 SDK SceneGroup（底层基础设施）

**定义**: 场景内的高可用集群

**关注点**:
- **Agent 关系**: agentId, agentName, endpoint
- **通讯关系**: 心跳检测、故障转移
- **高可用性**: 主备切换、状态共享

**核心概念**:
```
SceneGroup
├── sceneGroupId      // 场景组唯一标识
├── sceneId           // 所属场景ID
├── members           // SceneMember 列表
│   ├── agentId       // Agent 标识
│   ├── agentName     // Agent 名称
│   ├── endpoint      // Agent 通讯地址
│   ├── role          // 集群角色（PRIMARY/BACKUP）
│   ├── status        // 状态（online/offline）
│   ├── joinTime      // 加入时间
│   └── lastHeartbeat // 最后心跳时间
├── key               // 场景组密钥
├── sharedState       // 共享状态
└── autoFailover      // 自动故障转移
```

### 1.2 SE SceneGroup（上层业务）

**定义**: 业务场景组

**关注点**:
- **业务关系**: 用户、角色、权限
- **业务功能**: 推送、提醒、日志
- **业务场景**: 周报、会议、项目

**核心概念**:
```
SceneGroup
├── sceneGroupId      // 场景组唯一标识
├── templateId        // 模板ID
├── creatorId         // 创建者ID
├── participants      // Participant 列表
│   ├── participantId // 参与者ID
│   ├── userId        // 用户ID
│   ├── name          // 参与者名称
│   ├── type          // 类型（USER/AGENT/SUPER_AGENT）
│   ├── role          // 业务角色（OWNER/MANAGER/EMPLOYEE）
│   └── status        // 状态（INVITED/JOINED/ACTIVE/LEFT）
├── capabilityBindings // 能力绑定
├── knowledgeBindings  // 知识库绑定
└── snapshots          // 快照
```

---

## 2. 理论关联分析

### 2.1 分层架构

```
┌─────────────────────────────────────────────────────────────┐
│                    业务层 (Business Layer)                    │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              SE SceneGroup (业务场景组)                │   │
│  │  • Participant (业务参与者)                           │   │
│  │  • CapabilityBinding (能力绑定)                       │   │
│  │  • KnowledgeBinding (知识库绑定)                      │   │
│  │  • Snapshot (快照)                                   │   │
│  │  • 业务功能：推送、提醒、日志                          │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ 关联
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  基础设施层 (Infrastructure Layer)            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │            SDK SceneGroup (高可用集群)                 │   │
│  │  • SceneMember (集群成员)                            │   │
│  │  • SceneGroupKey (密钥管理)                          │   │
│  │  • Failover (故障转移)                               │   │
│  │  • Heartbeat (心跳检测)                              │   │
│  │  • 基础设施：通讯、高可用、安全                        │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ 关联
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Agent 层 (Agent Layer)                     │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                   Agent (代理)                        │   │
│  │  • agentId (代理标识)                                │   │
│  │  • agentType (代理类型)                              │   │
│  │  • capabilities (能力列表)                           │   │
│  │  • endpoint (通讯端点)                               │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 关联关系

| SDK 概念 | SE 概念 | 关联说明 |
|----------|---------|----------|
| `sceneGroupId` | `sceneGroupId` | **同一标识**，共享场景组 ID |
| `sceneId` | `templateId` | 场景模板 ID |
| `SceneMember.agentId` | `Participant.userId` | **Agent → 用户映射** |
| `SceneMember.role` (PRIMARY/BACKUP) | `Participant.role` (OWNER/MANAGER/EMPLOYEE) | **集群角色 → 业务角色** |
| `SceneMember.endpoint` | - | Agent 通讯地址 |
| `SceneMember.status` | `Participant.status` | **状态同步** |
| `sharedState` | `config` | **共享状态 → 业务配置** |

### 2.3 Agent 关系与业务关系

**Agent 关系（底层）**:
```
Agent
├── agentId          // 代理唯一标识
├── agentType        // 代理类型
├── capabilities     // 能力列表
├── endpoint         // 通讯端点
└── status           // 状态

SceneMember
├── agentId          // 关联 Agent
├── role             // 集群角色（PRIMARY/BACKUP）
└── endpoint         // 通讯地址
```

**业务关系（上层）**:
```
Participant
├── participantId    // 参与者唯一标识
├── userId           // 用户ID（可能对应 Agent.agentId）
├── type             // 类型（USER/AGENT/SUPER_AGENT）
├── role             // 业务角色（OWNER/MANAGER/EMPLOYEE）
└── status           // 状态
```

**关联映射**:
```
Agent.agentId ←→ Participant.userId
Agent.agentType ←→ Participant.type
SceneMember.role (PRIMARY) ←→ Participant.role (OWNER/MANAGER)
SceneMember.role (BACKUP) ←→ Participant.role (EMPLOYEE/OBSERVER)
```

---

## 3. 理论基础

### 3.1 分层设计原则

**关注点分离**:
- **基础设施层**: 关注通讯、高可用、安全
- **业务层**: 关注业务逻辑、用户交互、场景管理

**依赖倒置**:
- 业务层依赖基础设施层提供的通讯能力
- 基础设施层不依赖业务层

### 3.2 Agent-Participant 映射理论

**Agent 代表**:
1. **用户代理**: 代表真实用户的 Agent
2. **系统代理**: 代表系统服务的 Agent
3. **AI 代理**: 代表 AI 助手的 Agent

**Participant 代表**:
1. **用户参与者**: 真实用户（对应 USER 类型 Agent）
2. **代理参与者**: 系统代理（对应 AGENT 类型 Agent）
3. **超级代理**: AI 助手（对应 SUPER_AGENT 类型 Agent）

**映射关系**:
```
Agent.type = "USER"    ←→  Participant.type = USER
Agent.type = "AGENT"   ←→  Participant.type = AGENT
Agent.type = "AI"      ←→  Participant.type = SUPER_AGENT
```

### 3.3 角色映射理论

**集群角色（SDK）**:
- `PRIMARY`: 主节点，处理所有请求
- `BACKUP`: 备份节点，待命接管

**业务角色（SE）**:
- `OWNER`: 场景所有者
- `MANAGER`: 场景管理员
- `EMPLOYEE`: 普通员工
- `LLM_ASSISTANT`: LLM 助手
- `COORDINATOR`: 协调者
- `OBSERVER`: 观察者

**映射关系**:
```
SDK PRIMARY ←→ SE OWNER/MANAGER (主节点对应管理者)
SDK BACKUP ←→ SE EMPLOYEE/OBSERVER (备份节点对应普通参与者)
```

---

## 4. API 打通方案

### 4.1 统一场景组 ID

**原则**: SDK SceneGroup 和 SE SceneGroup 共享同一个 `sceneGroupId`

```java
// 创建时同步
String sceneGroupId = UUID.randomUUID().toString();

// SDK SceneGroup
sdkSceneGroup.setSceneGroupId(sceneGroupId);

// SE SceneGroup
seSceneGroup.setSceneGroupId(sceneGroupId);
```

### 4.2 Agent-Participant 映射接口

**新增接口**: `SceneGroupBridge`

```java
public interface SceneGroupBridge {
    
    /**
     * 从 SDK SceneMember 创建 SE Participant
     */
    Participant createParticipantFromMember(SceneMember member);
    
    /**
     * 从 SE Participant 创建 SDK SceneMember
     */
    SceneMember createMemberFromParticipant(Participant participant, String endpoint);
    
    /**
     * 同步 SDK SceneGroup 到 SE SceneGroup
     */
    void syncFromSdkToSe(String sceneGroupId);
    
    /**
     * 同步 SE SceneGroup 到 SDK SceneGroup
     */
    void syncFromSeToSdk(String sceneGroupId);
    
    /**
     * 获取关联的 SDK SceneGroup
     */
    net.ooder.sdk.api.scene.SceneGroup getSdkSceneGroup(String sceneGroupId);
    
    /**
     * 获取关联的 SE SceneGroup
     */
    net.ooder.scene.group.SceneGroup getSeSceneGroup(String sceneGroupId);
}
```

### 4.3 实现示例

```java
@Component
public class SceneGroupBridgeImpl implements SceneGroupBridge {
    
    private final net.ooder.sdk.api.scene.SceneGroupManager sdkManager;
    private final net.ooder.scene.group.SceneGroupManager seManager;
    
    @Override
    public Participant createParticipantFromMember(SceneMember member) {
        Participant participant = new Participant(
            UUID.randomUUID().toString(),
            member.getAgentId(),  // userId = agentId
            member.getAgentName(),
            Participant.Type.USER  // 根据 agentType 映射
        );
        
        // 角色映射
        if (member.getRole() == MemberRole.PRIMARY) {
            participant.setRole(Participant.Role.OWNER);
        } else {
            participant.setRole(Participant.Role.EMPLOYEE);
        }
        
        // 状态映射
        if ("online".equals(member.getStatus())) {
            participant.activate();
        }
        
        return participant;
    }
    
    @Override
    public void syncFromSdkToSe(String sceneGroupId) {
        // 获取 SDK SceneGroup
        net.ooder.sdk.api.scene.SceneGroup sdkGroup = 
            sdkManager.get(sceneGroupId).join();
        
        // 创建或更新 SE SceneGroup
        net.ooder.scene.group.SceneGroup seGroup = 
            seManager.getSceneGroup(sceneGroupId);
        
        if (seGroup == null) {
            seGroup = seManager.createSceneGroup(
                sceneGroupId,
                sdkGroup.getSceneId(),
                sdkGroup.getPrimary().getAgentId(),
                SceneGroup.CreatorType.USER
            );
        }
        
        // 同步成员
        for (SceneMember member : sdkGroup.getMembers()) {
            Participant participant = createParticipantFromMember(member);
            seGroup.addParticipant(participant);
        }
        
        // 激活
        seGroup.activate();
    }
}
```

### 4.4 修改 SceneGroupInitializer

```java
public class SceneGroupInitializer {
    
    private final net.ooder.sdk.api.scene.SceneGroupManager sdkManager;
    private final SceneGroupBridge bridge;
    
    private void activate(InitContext context) {
        // 1. 创建 SDK SceneGroup（集群版）
        SceneGroup sdkGroup = sdkManager.create(sceneId, config).join();
        
        // 2. 同步创建 SE SceneGroup（业务版）
        bridge.syncFromSdkToSe(sdkGroup.getSceneGroupId());
        
        // 3. 后续业务逻辑使用 SE SceneGroup
        // ...
    }
}
```

---

## 5. 总结

### 5.1 理论关联

| 层级 | 概念 | 关注点 | 关联 |
|------|------|--------|------|
| **Agent 层** | Agent | 代理标识、能力、通讯 | 基础实体 |
| **基础设施层** | SDK SceneGroup | 高可用、通讯、安全 | Agent 关系 |
| **业务层** | SE SceneGroup | 业务逻辑、用户交互 | 业务关系 |

### 5.2 关键关联点

1. **sceneGroupId**: 两者共享同一标识
2. **Agent-Participant**: agentId 映射到 userId
3. **角色映射**: PRIMARY → OWNER, BACKUP → EMPLOYEE
4. **状态同步**: online → ACTIVE

### 5.3 下一步

1. **实现 SceneGroupBridge**: 打通 SDK 和 SE 的场景组
2. **修改 SceneGroupInitializer**: 在激活时同步创建 SE SceneGroup
3. **添加事件监听**: SDK SceneGroup 变化时同步到 SE SceneGroup

---

## 6. 场景技能定义与场景组创建

### 6.1 场景技能的定义来源

**核心概念**: 场景是由技能定义的！

```
┌─────────────────────────────────────────────────────────────┐
│                    技能 (Skill) - 唯一核心实体                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  SkillForm (形态)                                    │   │
│  │  ├── SCENE (场景技能 - 容器型)                        │   │
│  │  │   ├── AUTO (自主场景)                             │   │
│  │  │   ├── TRIGGER (触发场景)                          │   │
│  │  │   └── HYBRID (混合场景)                           │   │
│  │  └── STANDALONE (独立技能 - 原子型)                   │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

**原始定义** (来自 SkillPackage 元数据):
```yaml
skill:
  skillId: "weekly-report-scene"
  name: "周报场景"
  form: SCENE                    # 形态：场景技能
  sceneType: AUTO                # 场景类型：自主场景
  category: KNOWLEDGE            # 分类：知识类
  purposes:                      # 服务目的
    - TEAM                       # 团队范围
    - PERIODIC                   # 定期时效
    - PROACTIVE                  # 主动服务
  sceneStructure:                # 场景结构
    internalCapabilities: [...]  # 内部能力
    childSkills: [...]           # 子技能
    orchestration: [...]         # 编排逻辑
```

### 6.2 场景技能的三种类型

| 场景类型 | 类比 | 自驱动 | 可触发 | 生命周期控制 | 适用场景 |
|----------|------|--------|--------|--------------|----------|
| **AUTO** | 源码包 | ✅ | ❌ | 自身控制 | 智能助手、监控告警、自动化流程 |
| **TRIGGER** | 资源文件夹 | ❌ | ✅ | 调用方控制 | 审批流程、报表生成、工具服务 |
| **HYBRID** | 普通文件夹 | ✅ | ✅ | 动态调整 | 复杂业务系统、智能客服、工作流引擎 |

**类型特性详解**:

```java
public enum SceneType {
    // 自主场景 - 自驱动运行
    AUTO("自主场景", "source-folder", true, false) {
        // canSelfDrive = true   → 可设置定时任务/事件监听
        // canBeTriggered = false → 不接受外部触发
        // 生命周期：由自身控制，自动启动和停止
    },
    
    // 触发场景 - 被动响应
    TRIGGER("触发场景", "resource-folder", false, true) {
        // canSelfDrive = false   → 无自驱能力
        // canBeTriggered = true  → 等待外部触发（API/用户指令/事件）
        // 生命周期：由调用方控制
    },
    
    // 混合场景 - 灵活切换
    HYBRID("混合场景", "regular-folder", true, true) {
        // canSelfDrive = true   → 默认自主运行
        // canBeTriggered = true → 可随时接受外部触发
        // 生命周期：根据配置动态调整
    };
}
```

### 6.3 场景组创建流程差异

**安装流程概览**:
```
SceneGroupInitializer.initialize()
├── Step 1: 场景加载 (loadScene)
├── Step 2: Agent 初始化 (initializeAgents)
├── Step 3: CAP 解析 (parseCapabilities)
├── Step 4: Skill 发现 (discoverSkills)
├── Step 5: Skill 挂载 (mountSkills)
└── Step 6: 场景激活 (activate) ← 根据场景类型有差异
```

**不同场景类型在场景组创建时的差异**:

#### 6.3.1 AUTO 场景 - 自主场景组创建

```java
// AUTO 场景激活流程
private void activateAutoScene(InitContext context, Skill skill) {
    // 1. 立即创建场景组并激活
    SceneGroup group = sceneGroupManager.create(sceneId, config).join();
    
    // 2. 启动自驱动逻辑
    if (skill.canSelfDrive()) {
        // 设置定时任务
        scheduleAutoTasks(group, skill);
        // 注册事件监听
        registerEventListeners(group, skill);
    }
    
    // 3. 启动心跳（自主维护）
    sceneGroupManager.startHeartbeat(group.getSceneGroupId());
    
    // 4. 场景组状态：立即进入 ACTIVE
    seSceneGroup.activate();
}
```

**特点**:
- ✅ 安装后立即激活场景组
- ✅ 自动启动定时任务/事件监听
- ✅ 心跳由场景组自主维护
- ✅ 生命周期由场景组控制

#### 6.3.2 TRIGGER 场景 - 触发场景组创建

```java
// TRIGGER 场景激活流程
private void activateTriggerScene(InitContext context, Skill skill) {
    // 1. 创建场景组（但不激活）
    SceneGroup group = sceneGroupManager.create(sceneId, config).join();
    
    // 2. 不启动自驱动逻辑
    // skill.canSelfDrive() == false
    
    // 3. 注册触发入口
    registerTriggerEndpoint(group, skill);
    
    // 4. 场景组状态：保持 CREATED/INSTALLED
    seSceneGroup.setStatus(SceneGroup.Status.CREATED);
    
    // 等待外部触发时才激活
}

// 外部触发时激活
public void onExternalTrigger(String sceneGroupId) {
    SceneGroup group = seSceneGroupManager.getSceneGroup(sceneGroupId);
    group.activate();
    // 执行场景逻辑
    executeSceneLogic(group);
    // 完成后可停用
    group.suspend();
}
```

**特点**:
- ❌ 安装后不立即激活场景组
- ❌ 不启动定时任务/事件监听
- ✅ 注册触发入口（API/事件）
- ✅ 生命周期由调用方控制

#### 6.3.3 HYBRID 场景 - 混合场景组创建

```java
// HYBRID 场景激活流程
private void activateHybridScene(InitContext context, Skill skill) {
    // 1. 创建场景组
    SceneGroup group = sceneGroupManager.create(sceneId, config).join();
    
    // 2. 根据配置决定初始行为
    HybridConfig config = skill.getSceneStructure()
        .map(SceneStructure::getMetadata)
        .map(m -> (HybridConfig) m.get("hybridConfig"))
        .orElse(HybridConfig.defaultAuto());
    
    if (config.startAsAuto()) {
        // 作为自主场景启动
        scheduleAutoTasks(group, skill);
        registerEventListeners(group, skill);
        sceneGroupManager.startHeartbeat(group.getSceneGroupId());
        seSceneGroup.activate();
    } else {
        // 作为触发场景等待
        registerTriggerEndpoint(group, skill);
        seSceneGroup.setStatus(SceneGroup.Status.CREATED);
    }
    
    // 3. 同时支持外部触发
    registerTriggerEndpoint(group, skill);
}
```

**特点**:
- ✅ 可选择初始行为（自主/触发）
- ✅ 同时支持自驱动和外部触发
- ✅ 可动态切换行为模式
- ✅ 生命周期灵活控制

### 6.4 场景技能与场景组的映射关系

```
┌─────────────────────────────────────────────────────────────┐
│                    场景技能 (Scene Skill)                     │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  SkillPackage                                        │   │
│  │  ├── form: SCENE                                     │   │
│  │  ├── sceneType: AUTO/TRIGGER/HYBRID                  │   │
│  │  ├── sceneStructure:                                 │   │
│  │  │   ├── internalCapabilities (内部能力)              │   │
│  │  │   ├── childSkills (子技能)                        │   │
│  │  │   └── orchestration (编排逻辑)                    │   │
│  │  └── purposes: [TEAM, PERIODIC, PROACTIVE]           │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ 激活/安装
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    场景组 (Scene Group)                       │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  SE SceneGroup                                       │   │
│  │  ├── sceneGroupId (由技能ID生成)                      │   │
│  │  ├── templateId (= skillId)                          │   │
│  │  ├── participants (参与者)                           │   │
│  │  ├── capabilityBindings (能力绑定)                    │   │
│  │  │   └── 来自 sceneStructure.internalCapabilities    │   │
│  │  ├── knowledgeBindings (知识库绑定)                   │   │
│  │  └── status (根据 sceneType 决定初始状态)             │   │
│  │      ├── AUTO → ACTIVE                               │   │
│  │      ├── TRIGGER → CREATED                           │   │
│  │      └── HYBRID → 根据配置                           │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 6.5 场景技能类型对场景组的影响

| 影响维度 | AUTO | TRIGGER | HYBRID |
|----------|------|---------|--------|
| **初始状态** | ACTIVE | CREATED | 可配置 |
| **心跳启动** | ✅ 立即启动 | ❌ 触发时启动 | ✅ 可选 |
| **定时任务** | ✅ 自动注册 | ❌ 无 | ✅ 可选 |
| **事件监听** | ✅ 自动注册 | ❌ 无 | ✅ 可选 |
| **触发入口** | ❌ 无 | ✅ 必须注册 | ✅ 必须注册 |
| **生命周期** | 自主控制 | 调用方控制 | 灵活控制 |
| **资源占用** | 持续占用 | 按需占用 | 混合模式 |

### 6.6 场景技能结构到场景组的转换

```java
public class SceneSkillToGroupConverter {
    
    public SceneGroup convert(Skill skill, InitRequest request) {
        // 1. 基础信息转换
        SceneGroup group = new SceneGroup(
            generateSceneGroupId(skill),
            skill.getSkillId(),  // templateId = skillId
            request.getUserId(),
            SceneGroup.CreatorType.USER
        );
        
        // 2. 能力绑定转换
        Optional<SceneStructure> structure = skill.getSceneStructure();
        if (structure.isPresent()) {
            // 内部能力 → 能力绑定
            for (InternalCapability cap : structure.get().getInternalCapabilities()) {
                group.addCapabilityBinding(new CapabilityBinding(
                    cap.getId(),
                    cap.getName(),
                    CapabilityBinding.BindingType.INTERNAL
                ));
            }
            
            // 子技能 → 能力绑定
            for (Skill child : structure.get().getChildSkills()) {
                group.addCapabilityBinding(new CapabilityBinding(
                    child.getSkillId(),
                    child.getName(),
                    CapabilityBinding.BindingType.CHILD_SKILL
                ));
            }
        }
        
        // 3. 根据场景类型设置初始状态
        SceneType sceneType = skill.getSceneType().orElse(SceneType.TRIGGER);
        switch (sceneType) {
            case AUTO:
                group.activate();  // 立即激活
                break;
            case TRIGGER:
                group.setStatus(SceneGroup.Status.CREATED);  // 等待触发
                break;
            case HYBRID:
                // 根据配置决定
                if (shouldStartAsAuto(skill)) {
                    group.activate();
                } else {
                    group.setStatus(SceneGroup.Status.CREATED);
                }
                break;
        }
        
        return group;
    }
}
```

---

## 7. 完整知识图谱

### 7.1 三层架构全景图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         业务层 (Business Layer)                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    SE SceneGroup (业务场景组)                      │   │
│  │  • Participant (参与者: USER/AGENT/SUPER_AGENT)                  │   │
│  │  • CapabilityBinding (能力绑定: 来自场景技能结构)                   │   │
│  │  • KnowledgeBinding (知识库绑定)                                  │   │
│  │  • Status (状态: 根据 SceneType 决定初始状态)                      │   │
│  │    - AUTO → ACTIVE                                               │   │
│  │    - TRIGGER → CREATED                                           │   │
│  │    - HYBRID → 可配置                                              │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    │ 由场景技能定义                      │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    场景技能 (Scene Skill)                          │   │
│  │  • SkillForm: SCENE                                              │   │
│  │  • SceneType: AUTO/TRIGGER/HYBRID                                │   │
│  │  • SceneStructure:                                               │   │
│  │    - internalCapabilities (内部能力)                              │   │
│  │    - childSkills (子技能)                                         │   │
│  │    - orchestration (编排逻辑)                                     │   │
│  │  • Purposes: [TEAM/PERSONAL, PERIODIC/INSTANT, PROACTIVE/REACTIVE]│   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
                                     │
                                     │ 关联
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                       基础设施层 (Infrastructure Layer)                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                  SDK SceneGroup (高可用集群)                       │   │
│  │  • SceneMember (集群成员: PRIMARY/BACKUP)                        │   │
│  │  • SceneGroupKey (密钥管理)                                       │   │
│  │  • Failover (故障转移)                                            │   │
│  │  • Heartbeat (心跳检测: AUTO/HYBRID 启动, TRIGGER 按需)           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
                                     │
                                     │ 关联
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           Agent 层 (Agent Layer)                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                         Agent (代理)                              │   │
│  │  • agentId (代理标识)                                             │   │
│  │  • agentType (代理类型: USER/AGENT/AI)                           │   │
│  │  • capabilities (能力列表)                                        │   │
│  │  • endpoint (通讯端点)                                            │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

### 7.2 场景技能 → 场景组 → Agent 完整映射

```
场景技能 (Scene Skill)
│
├── skillId ──────────────────────────→ templateId (SE SceneGroup)
│
├── sceneType
│   ├── AUTO ─────────────────────────→ Status: ACTIVE, Heartbeat: 立即启动
│   ├── TRIGGER ──────────────────────→ Status: CREATED, Heartbeat: 触发时启动
│   └── HYBRID ───────────────────────→ Status: 可配置, Heartbeat: 可选
│
├── sceneStructure.internalCapabilities
│   └─────────────────────────────────→ CapabilityBinding (INTERNAL)
│
├── sceneStructure.childSkills
│   └─────────────────────────────────→ CapabilityBinding (CHILD_SKILL)
│
└── sceneStructure.orchestration
    └─────────────────────────────────→ 场景组执行逻辑

场景组 (Scene Group)
│
├── sceneGroupId ─────────────────────→ sceneGroupId (SDK SceneGroup)
│
├── participants[].userId ────────────→ SceneMember.agentId
│
├── participants[].role
│   ├── OWNER/MANAGER ────────────────→ MemberRole.PRIMARY
│   └── EMPLOYEE/OBSERVER ────────────→ MemberRole.BACKUP
│
└── participants[].type
    ├── USER ─────────────────────────→ Agent.type = "USER"
    ├── AGENT ────────────────────────→ Agent.type = "AGENT"
    └── SUPER_AGENT ──────────────────→ Agent.type = "AI"
```

---

**分析日期**: 2026-03-19  
**分析版本**: SceneEngine 2.3.1  
**更新内容**: 新增场景技能定义、类型差异、场景组创建流程差异分析
