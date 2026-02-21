# SceneEngine Core 依赖说明

## 一、依赖架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    上层工程（依赖 SEC）                                       │
│  ooder-Nexus (个人版)  │  ooder-Nexus-Enterprise (企业版)  │  agent-skillcenter │
└───────────────────────────┬─────────────────────────────────────────────────┘
                            │ 依赖
                            ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    SceneEngine Core (SEC) 0.7.3                              │
│                                                                              │
│  Client层: SceneClient │ AdminClient │ SkillClient │ SecurityClient         │
│  Session层: SessionManager │ TokenManager │ ConnectInfo                     │
│  Engine层: OrgEngine │ MsgEngine │ VfsEngine │ AgentEngine │ SkillEngine    │
│  Audit层: AuditService │ AuditLog                                          │
└───────────────────────────┬─────────────────────────────────────────────────┘
                            │ 依赖
                            ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    agentSDK 0.7.3                                            │
│                                                                              │
│  核心: OoderSDK, SDKConfiguration                                            │
│  技能: SkillPackageManager, SkillPackage, InstallRequest/Result             │
│  场景: SceneManager, SceneGroupManager, SceneDefinition, SceneGroup         │
│  安全: SecurityService, KeyPair, TokenInfo                                  │
│  发现: GitDiscoveryConfig, GitHubDiscoverer, GiteeDiscoverer                │
│  协议: CollaborationProtocol, DiscoveryProtocol, LoginProtocol              │
└─────────────────────────────────────────────────────────────────────────────┘
                            │ 依赖
                            ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    ooder-common 2.2                                          │
│                                                                              │
│  ooder-annotation, ooder-config, ooder-common-client                        │
│  ooder-server, ooder-vfs-web, ooder-org-web, ooder-msg-web                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、依赖版本统一

| 依赖 | 版本 | 说明 |
|------|------|------|
| agent-sdk | 0.7.3 | Agent SDK 核心包 |
| ooder-annotation | 2.2 | 注解支持 |
| ooder-config | 2.2 | 配置管理 |
| ooder-common-client | 2.2 | 通用客户端 |
| ooder-server | 2.2 | 服务端支持 |
| ooder-vfs-web | 2.2 | VFS Web 支持 |
| ooder-org-web | 2.2 | 组织 Web 支持 |
| ooder-msg-web | 2.2 | 消息 Web 支持 |
| spring-boot | 2.7.0 | Spring Boot 框架 |

---

## 三、agentSDK 核心类映射

### 3.1 技能管理

| agentSDK 类 | SEC 封装 | 用途 |
|-------------|---------|------|
| `SkillPackageManager` | `SkillClient` | 技能包管理 |
| `SkillPackage` | `SkillInfo` | 技能包定义 |
| `InstalledSkill` | `InstalledSkillInfo` | 已安装技能 |
| `InstallRequest` | `SkillInstallRequest` | 安装请求 |
| `InstallResult` | `SkillInstallResult` | 安装结果 |
| `Capability` | `CapabilityInfo` | 能力定义 |

### 3.2 场景管理

| agentSDK 类 | SEC 封装 | 用途 |
|-------------|---------|------|
| `SceneManager` | `SceneClient` | 场景管理 |
| `SceneGroupManager` | `SceneGroupClient` | 场景组管理 |
| `SceneDefinition` | `SceneDef` | 场景定义 |
| `SceneSnapshot` | `SceneState` | 场景快照 |
| `SceneGroup` | `SceneGroupInfo` | 场景组 |
| `SceneMember` | `SceneMemberInfo` | 场景成员 |

### 3.3 安全管理

| agentSDK 类 | SEC 封装 | 用途 |
|-------------|---------|------|
| `SecurityService` | `SecurityClient` | 安全服务 |
| `KeyPair` | `KeyPairInfo` | 密钥对 |
| `TokenInfo` | `TokenInfo` | Token 信息 |

### 3.4 发现服务

| agentSDK 类 | SEC 封装 | 用途 |
|-------------|---------|------|
| `GitDiscoveryConfig` | `GitDiscoveryConfig` | Git 发现配置 |
| `GitHubDiscoverer` | `GitHubDiscoverer` | GitHub 技能发现 |
| `GiteeDiscoverer` | `GiteeDiscoverer` | Gitee 技能发现 |

---

## 四、上层工程使用方式

### 4.1 ooder-Nexus（个人版）

```java
// 通过 SEC 获取 SceneClient
SceneClient client = sceneEngine.login(username, password);

// 调用技能相关功能
SkillInfo skill = client.findSkill("skill-org-feishu");
SkillInstallResult result = client.installSkill(skill.getId());

// 调用场景相关功能
SceneGroupInfo group = client.joinSceneGroup("auth-scene");
```

### 4.2 ooder-Nexus-Enterprise（企业版）

```java
// 通过 SEC 获取 AdminClient
AdminClient admin = sceneEngine.adminLogin(username, password);

// 管理用户
PageResult<UserInfo> users = admin.listUsers(pageRequest);

// 管理技能
PageResult<SkillInfo> skills = admin.listAllSkills(pageRequest);
admin.approveSkill(skillId);
```

### 4.3 agent-skillcenter

```java
// 通过 SEC 获取 SkillClient
SkillClient skillClient = sceneEngine.getSkillClient();

// 技能发现
List<SkillInfo> skills = skillClient.discoverSkills(query);

// 技能安装监控
SkillInstallProgress progress = skillClient.getInstallProgress(installId);
```

---

## 五、SEC 封装原则

### 5.1 统一安全

- 所有调用必须经过 Token 验证
- Token 由 SEC 统一生成和管理
- 支持自动刷新和撤销

### 5.2 统一审计

- 所有操作记录审计日志
- 审计日志包含用户、时间、操作、结果
- 支持审计日志查询和导出

### 5.3 统一状态管理

- Session 统一管理
- ConnectInfo 统一维护
- 支持离线模式和自动恢复

### 5.4 降级机制

- 内置 Mock 实现
- 异常自动降级
- 支持配置切换模式

---

## 六、Client 层接口设计

### 6.1 SceneClient（用户客户端）

```java
public interface SceneClient {
    String getSessionId();
    String getUserId();
    
    // 技能操作
    SkillInfo findSkill(String skillId);
    List<SkillInfo> listMySkills();
    SkillInstallResult installSkill(String skillId);
    void uninstallSkill(String skillId);
    
    // 场景操作
    SceneGroupInfo joinSceneGroup(String sceneId);
    void leaveSceneGroup(String groupId);
    List<SceneGroupInfo> listMySceneGroups();
    
    // 能力调用
    Object invokeCapability(String skillId, String capability, Map<String, Object> params);
}
```

### 6.2 AdminClient（管理客户端）

```java
public interface AdminClient {
    String getSessionId();
    String getUserId();
    
    // 用户管理
    PageResult<UserInfo> listUsers(PageRequest request);
    UserInfo getUser(String userId);
    void updateUser(String userId, UserInfo user);
    void deleteUser(String userId);
    
    // 技能管理
    PageResult<SkillInfo> listAllSkills(PageRequest request);
    void approveSkill(String skillId);
    void rejectSkill(String skillId, String reason);
    void deleteSkill(String skillId);
    
    // 场景管理
    PageResult<SceneGroupInfo> listAllSceneGroups(PageRequest request);
    void createSceneGroup(SceneGroupInfo group);
    void deleteSceneGroup(String groupId);
    
    // 审计日志
    PageResult<AuditLog> listAuditLogs(PageRequest request);
}
```

### 6.3 SceneEngine（核心入口）

```java
public interface SceneEngine {
    // 用户登录
    SceneClient login(String username, String password);
    SceneClient login(String token);
    void logout(String sessionId);
    
    // 管理员登录
    AdminClient adminLogin(String username, String password);
    
    // 会话管理
    SessionInfo getSession(String sessionId);
    boolean validateSession(String sessionId);
    
    // 状态
    EngineStatus getStatus();
    void start();
    void stop();
}
```

---

**文档版本**：1.0  
**创建日期**：2026-02-20  
**维护团队**：Ooder 技术团队
