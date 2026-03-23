# SE SDK v2.3.1 与 MVP 团队协同开发文档

**版本**: 2.3.1  
**发布日期**: 2026-03-22  
**目标读者**: MVP 开发团队  
**协作方**: SE SDK 团队  
**状态**: 🔴 待确认

---

## 一、协作背景

### 1.1 版本目标

SE SDK v2.3.1 版本需要 MVP 团队实现 SPI 扩展点，使 SE SDK 能够与 MVP 的业务服务集成。

### 1.2 协作需求概述

| 需求 | 优先级 | 说明 |
|------|--------|------|
| SceneServices SPI 实现 | P0 | 核心服务接口 |
| 激活步骤执行器 | P1 | 自定义激活逻辑 |
| 数据持久化适配 | P0 | 场景数据存储 |
| 事件监听器 | P2 | 状态变更通知 |

### 1.3 当前状态

```
SE SDK SPI 扩展点:
├── SceneServices (待 MVP 实现)
│   ├── UserService
│   ├── OrganizationService
│   ├── PermissionService
│   ├── StorageService
│   └── MessageService
├── ActivationStepExecutor (可扩展)
└── SceneEngineServiceProvider (待 MVP 实现)
```

---

## 二、SPI 扩展点详解

### 2.1 SceneServices 接口

**位置**: `net.ooder.scene.spi.SceneServices`

```java
/**
 * 场景服务 SPI
 * MVP 团队必须实现此接口
 */
public interface SceneServices {
    
    /**
     * 获取用户服务
     */
    UserService getUserService();
    
    /**
     * 获取组织服务
     */
    OrganizationService getOrganizationService();
    
    /**
     * 获取权限服务
     */
    PermissionService getPermissionService();
    
    /**
     * 获取存储服务
     */
    StorageService getStorageService();
    
    /**
     * 获取消息服务
     */
    MessageService getMessageService();
    
    /**
     * 获取配置服务
     */
    ConfigService getConfigService();
    
    /**
     * 获取审计服务
     */
    AuditService getAuditService();
}
```

### 2.2 子服务接口定义

#### 2.2.1 UserService

```java
/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfo getUser(String userId);
    
    /**
     * 批量获取用户信息
     * 
     * @param userIds 用户ID列表
     * @return 用户信息映射
     */
    Map<String, UserInfo> getUsers(List<String> userIds);
    
    /**
     * 搜索用户
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 用户列表
     */
    List<UserInfo> searchUsers(String keyword, int limit);
    
    /**
     * 获取用户部门
     * 
     * @param userId 用户ID
     * @return 部门ID
     */
    String getUserDepartment(String userId);
    
    /**
     * 获取用户角色
     * 
     * @param userId 用户ID
     * @param sceneId 场景ID
     * @return 角色ID
     */
    String getUserRole(String userId, String sceneId);
}

/**
 * 用户信息
 */
@Data
public class UserInfo {
    private String userId;
    private String username;
    private String displayName;
    private String email;
    private String phone;
    private String avatar;
    private String departmentId;
    private String departmentName;
    private Map<String, Object> attributes;
}
```

#### 2.2.2 OrganizationService

```java
/**
 * 组织服务接口
 */
public interface OrganizationService {
    
    /**
     * 获取部门信息
     * 
     * @param departmentId 部门ID
     * @return 部门信息
     */
    DepartmentInfo getDepartment(String departmentId);
    
    /**
     * 获取子部门列表
     * 
     * @param parentDepartmentId 父部门ID
     * @return 子部门列表
     */
    List<DepartmentInfo> getChildDepartments(String parentDepartmentId);
    
    /**
     * 获取部门成员
     * 
     * @param departmentId 部门ID
     * @return 成员用户ID列表
     */
    List<String> getDepartmentMembers(String departmentId);
    
    /**
     * 获取部门负责人
     * 
     * @param departmentId 部门ID
     * @return 负责人用户ID
     */
    String getDepartmentManager(String departmentId);
    
    /**
     * 获取用户层级路径
     * 
     * @param userId 用户ID
     * @return 层级路径 (从根部门到用户所在部门)
     */
    List<DepartmentInfo> getUserHierarchy(String userId);
}

/**
 * 部门信息
 */
@Data
public class DepartmentInfo {
    private String departmentId;
    private String departmentName;
    private String parentId;
    private int level;
    private String managerId;
    private int memberCount;
    private Map<String, Object> attributes;
}
```

#### 2.2.3 PermissionService

```java
/**
 * 权限服务接口
 */
public interface PermissionService {
    
    /**
     * 检查权限
     * 
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean hasPermission(String userId, String permission);
    
    /**
     * 检查场景权限
     * 
     * @param userId 用户ID
     * @param sceneId 场景ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean hasScenePermission(String userId, String sceneId, String permission);
    
    /**
     * 获取用户权限列表
     * 
     * @param userId 用户ID
     * @param sceneId 场景ID (可选)
     * @return 权限列表
     */
    List<String> getUserPermissions(String userId, String sceneId);
    
    /**
     * 授予权限
     * 
     * @param userId 用户ID
     * @param sceneId 场景ID
     * @param permission 权限标识
     */
    void grantPermission(String userId, String sceneId, String permission);
    
    /**
     * 撤销权限
     * 
     * @param userId 用户ID
     * @param sceneId 场景ID
     * @param permission 权限标识
     */
    void revokePermission(String userId, String sceneId, String permission);
}
```

#### 2.2.4 StorageService

```java
/**
 * 存储服务接口
 */
public interface StorageService {
    
    /**
     * 保存场景数据
     * 
     * @param sceneId 场景ID
     * @param key 数据键
     * @param value 数据值
     */
    void save(String sceneId, String key, Object value);
    
    /**
     * 获取场景数据
     * 
     * @param sceneId 场景ID
     * @param key 数据键
     * @return 数据值
     */
    Object get(String sceneId, String key);
    
    /**
     * 获取场景数据
     * 
     * @param sceneId 场景ID
     * @param key 数据键
     * @param type 数据类型
     * @return 数据值
     */
    <T> T get(String sceneId, String key, Class<T> type);
    
    /**
     * 删除场景数据
     * 
     * @param sceneId 场景ID
     * @param key 数据键
     */
    void delete(String sceneId, String key);
    
    /**
     * 查询场景数据
     * 
     * @param sceneId 场景ID
     * @param prefix 键前缀
     * @return 数据映射
     */
    Map<String, Object> query(String sceneId, String prefix);
    
    /**
     * 批量保存
     * 
     * @param sceneId 场景ID
     * @param data 数据映射
     */
    void batchSave(String sceneId, Map<String, Object> data);
}
```

#### 2.2.5 MessageService

```java
/**
 * 消息服务接口
 */
public interface MessageService {
    
    /**
     * 发送消息
     * 
     * @param message 消息
     * @return 发送结果
     */
    SendMessageResult sendMessage(Message message);
    
    /**
     * 批量发送消息
     * 
     * @param messages 消息列表
     * @return 发送结果列表
     */
    List<SendMessageResult> batchSendMessages(List<Message> messages);
    
    /**
     * 发送场景通知
     * 
     * @param sceneId 场景ID
     * @param userIds 用户ID列表
     * @param notification 通知内容
     * @return 发送结果
     */
    SendMessageResult sendSceneNotification(
        String sceneId, 
        List<String> userIds, 
        SceneNotification notification
    );
}

/**
 * 消息
 */
@Data
@Builder
public class Message {
    private String messageId;
    private String fromUserId;
    private List<String> toUserIds;
    private String title;
    private String content;
    private MessageType type;
    private Map<String, Object> data;
    private long timestamp;
    
    enum MessageType {
        TEXT, CARD, LINK, ACTION
    }
}

/**
 * 场景通知
 */
@Data
@Builder
public class SceneNotification {
    private String title;
    private String content;
    private String actionUrl;
    private Map<String, Object> extra;
}
```

---

## 三、任务清单

### 3.1 P0 级任务 (关键)

#### 任务 MVP-001: 实现 SceneServices SPI

**优先级**: 🔴 P0  
**预计工时**: 3天  
**依赖**: 无

**任务描述**:
实现 SceneServices 接口及其所有子服务接口。

**实现模板**:

```java
/**
 * MVP 场景服务实现
 */
@Service
public class MvpSceneServicesImpl implements SceneServices {
    
    private final MvpUserService userService;
    private final MvpOrganizationService organizationService;
    private final MvpPermissionService permissionService;
    private final MvpStorageService storageService;
    private final MvpMessageService messageService;
    
    public MvpSceneServicesImpl(
            MvpUserService userService,
            MvpOrganizationService organizationService,
            MvpPermissionService permissionService,
            MvpStorageService storageService,
            MvpMessageService messageService) {
        this.userService = userService;
        this.organizationService = organizationService;
        this.permissionService = permissionService;
        this.storageService = storageService;
        this.messageService = messageService;
    }
    
    @Override
    public UserService getUserService() {
        return userService;
    }
    
    @Override
    public OrganizationService getOrganizationService() {
        return organizationService;
    }
    
    @Override
    public PermissionService getPermissionService() {
        return permissionService;
    }
    
    @Override
    public StorageService getStorageService() {
        return storageService;
    }
    
    @Override
    public MessageService getMessageService() {
        return messageService;
    }
}
```

**验收标准**:
- [ ] 所有接口方法正常返回
- [ ] Spring Bean 正确注册
- [ ] 依赖注入正常工作

---

#### 任务 MVP-002: 实现数据持久化适配

**优先级**: 🔴 P0  
**预计工时**: 2天  
**依赖**: MVP-001

**任务描述**:
实现场景数据的持久化存储，支持场景实例、激活记录等数据的存储。

**实现模板**:

```java
/**
 * 场景存储服务实现
 */
@Service
public class MvpStorageServiceImpl implements StorageService {
    
    private final SceneDataRepository sceneDataRepository;
    
    public MvpStorageServiceImpl(SceneDataRepository sceneDataRepository) {
        this.sceneDataRepository = sceneDataRepository;
    }
    
    @Override
    public void save(String sceneId, String key, Object value) {
        SceneDataEntity entity = new SceneDataEntity();
        entity.setSceneId(sceneId);
        entity.setKey(key);
        entity.setValue(serializeValue(value));
        entity.setUpdatedAt(System.currentTimeMillis());
        sceneDataRepository.save(entity);
    }
    
    @Override
    public Object get(String sceneId, String key) {
        return sceneDataRepository.findBySceneIdAndKey(sceneId, key)
            .map(this::deserializeValue)
            .orElse(null);
    }
    
    @Override
    public <T> T get(String sceneId, String key, Class<T> type) {
        Object value = get(sceneId, key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }
    
    @Override
    public void delete(String sceneId, String key) {
        sceneDataRepository.deleteBySceneIdAndKey(sceneId, key);
    }
    
    @Override
    public Map<String, Object> query(String sceneId, String prefix) {
        List<SceneDataEntity> entities = sceneDataRepository
            .findBySceneIdAndKeyStartingWith(sceneId, prefix);
        return entities.stream()
            .collect(Collectors.toMap(
                SceneDataEntity::getKey,
                e -> deserializeValue(e)
            ));
    }
    
    @Override
    public void batchSave(String sceneId, Map<String, Object> data) {
        List<SceneDataEntity> entities = data.entrySet().stream()
            .map(e -> {
                SceneDataEntity entity = new SceneDataEntity();
                entity.setSceneId(sceneId);
                entity.setKey(e.getKey());
                entity.setValue(serializeValue(e.getValue()));
                entity.setUpdatedAt(System.currentTimeMillis());
                return entity;
            })
            .collect(Collectors.toList());
        sceneDataRepository.saveAll(entities);
    }
    
    private String serializeValue(Object value) {
        // JSON 序列化
        return JsonUtils.toJson(value);
    }
    
    private Object deserializeValue(SceneDataEntity entity) {
        // JSON 反序列化
        return JsonUtils.fromJson(entity.getValue(), Object.class);
    }
}
```

---

### 3.2 P1 级任务 (重要)

#### 任务 MVP-003: 实现激活步骤执行器

**优先级**: 🟡 P1  
**预计工时**: 3天  
**依赖**: MVP-001

**任务描述**:
实现自定义激活步骤执行器，支持 MVP 特有的激活逻辑。

**实现模板**:

```java
/**
 * 确认参与者执行器
 */
@Component
public class ConfirmParticipantsExecutor implements ActivationStepExecutor {
    
    private final SceneServices sceneServices;
    
    public ConfirmParticipantsExecutor(SceneServices sceneServices) {
        this.sceneServices = sceneServices;
    }
    
    @Override
    public boolean canExecute(ActivationStepConfig stepConfig) {
        return "CONFIRM_PARTICIPANTS".equals(stepConfig.getStepType());
    }
    
    @Override
    public StepResult execute(ActivationStepConfig stepConfig, 
                               ActivationProcess process, 
                               Map<String, Object> context) {
        StepResult result = new StepResult();
        result.setStepId(stepConfig.getStepId());
        
        try {
            @SuppressWarnings("unchecked")
            List<String> participantIds = (List<String>) context.get("participants");
            
            if (participantIds == null || participantIds.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage("请选择参与者");
                return result;
            }
            
            Map<String, UserInfo> users = sceneServices.getUserService()
                .getUsers(participantIds);
            
            if (users.size() != participantIds.size()) {
                List<String> notFound = participantIds.stream()
                    .filter(id -> !users.containsKey(id))
                    .collect(Collectors.toList());
                result.setSuccess(false);
                result.setErrorMessage("以下用户不存在: " + String.join(", ", notFound));
                return result;
            }
            
            result.setSuccess(true);
            result.setOutput(Map.of(
                "participantCount", participantIds.size(),
                "participants", users
            ));
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("执行失败: " + e.getMessage());
        }
        
        return result;
    }
}

/**
 * 选择推送目标执行器
 */
@Component
public class SelectPushTargetsExecutor implements ActivationStepExecutor {
    
    private final SceneServices sceneServices;
    
    public SelectPushTargetsExecutor(SceneServices sceneServices) {
        this.sceneServices = sceneServices;
    }
    
    @Override
    public boolean canExecute(ActivationStepConfig stepConfig) {
        return "SELECT_PUSH_TARGETS".equals(stepConfig.getStepType());
    }
    
    @Override
    public StepResult execute(ActivationStepConfig stepConfig, 
                               ActivationProcess process, 
                               Map<String, Object> context) {
        StepResult result = new StepResult();
        result.setStepId(stepConfig.getStepId());
        
        try {
            @SuppressWarnings("unchecked")
            List<String> targetIds = (List<String>) context.get("pushTargets");
            
            if (targetIds == null || targetIds.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage("请选择推送目标");
                return result;
            }
            
            result.setSuccess(true);
            result.setOutput(Map.of("pushTargetCount", targetIds.size()));
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("执行失败: " + e.getMessage());
        }
        
        return result;
    }
}
```

---

### 3.3 P2 级任务 (优化)

#### 任务 MVP-004: 实现事件监听器

**优先级**: 🟢 P2  
**预计工时**: 2天  
**依赖**: MVP-001

**任务描述**:
实现状态变更事件监听器，支持场景状态变更的业务处理。

**实现模板**:

```java
/**
 * 场景状态变更监听器
 */
@Component
public class SceneStateChangeListener implements SceneSkillLifecycle.StateChangeListener {
    
    private final SceneServices sceneServices;
    private final Logger log = LoggerFactory.getLogger(SceneStateChangeListener.class);
    
    public SceneStateChangeListener(SceneServices sceneServices) {
        this.sceneServices = sceneServices;
    }
    
    @Override
    public void onStateChange(StateChangeEvent event) {
        log.info("Scene state changed: {} -> {} for scene: {}, skill: {}",
            event.getOldState(), event.getNewState(),
            event.getSceneId(), event.getSkillId());
        
        switch (event.getNewState()) {
            case ACTIVATED:
                handleActivated(event);
                break;
            case DEACTIVATED:
                handleDeactivated(event);
                break;
            case ERROR:
                handleError(event);
                break;
            default:
                break;
        }
    }
    
    private void handleActivated(StateChangeEvent event) {
        // 发送激活通知
        SceneNotification notification = SceneNotification.builder()
            .title("场景激活成功")
            .content("场景 " + event.getSceneId() + " 已成功激活")
            .build();
        
        sceneServices.getMessageService().sendSceneNotification(
            event.getSceneId(),
            getSceneParticipants(event.getSceneId()),
            notification
        );
    }
    
    private void handleDeactivated(StateChangeEvent event) {
        // 清理场景数据
        sceneServices.getStorageService().delete(event.getSceneId(), "activation_data");
    }
    
    private void handleError(StateChangeEvent event) {
        // 记录错误日志
        sceneServices.getAuditService().log(AuditEvent.builder()
            .eventType("SCENE_ERROR")
            .sceneId(event.getSceneId())
            .message(event.getMessage())
            .timestamp(System.currentTimeMillis())
            .build());
    }
    
    private List<String> getSceneParticipants(String sceneId) {
        // 获取场景参与者
        return Collections.emptyList();
    }
}
```

---

## 四、集成配置

### 4.1 Spring Boot 自动配置

```java
/**
 * MVP 场景引擎自动配置
 */
@Configuration
@ConditionalOnClass(SceneEngine.class)
@EnableConfigurationProperties(MvpSceneProperties.class)
public class MvpSceneEngineAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SceneServices sceneServices(
            MvpUserService userService,
            MvpOrganizationService organizationService,
            MvpPermissionService permissionService,
            MvpStorageService storageService,
            MvpMessageService messageService) {
        return new MvpSceneServicesImpl(
            userService, organizationService, 
            permissionService, storageService, messageService
        );
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SceneEngineServiceProvider serviceProvider(SceneServices sceneServices) {
        return new MvpServiceProviderImpl(sceneServices);
    }
    
    @Bean
    @ConditionalOnProperty(name = "ooder.scene.listeners.enabled", havingValue = "true")
    public SceneStateChangeListener sceneStateChangeListener(SceneServices sceneServices) {
        return new SceneStateChangeListener(sceneServices);
    }
}
```

### 4.2 配置属性

```yaml
# application.yml
ooder:
  scene:
    services:
      enabled: true
    listeners:
      enabled: true
    storage:
      type: database  # database, redis, file
      table-prefix: scene_
    message:
      provider: default
```

### 4.3 Maven 依赖

```xml
<!-- MVP 添加 SE SDK 依赖 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

---

## 五、验收标准

### 5.1 功能验收

| 任务ID | 验收标准 | 测试用例 |
|--------|----------|----------|
| MVP-001 | 所有 SPI 方法正常返回 | 单元测试覆盖 |
| MVP-002 | 数据正确持久化 | 增删改查测试 |
| MVP-003 | 激活步骤正确执行 | 集成测试 |
| MVP-004 | 事件正确触发 | 事件测试 |

### 5.2 集成验收

- [ ] SE SDK 正确加载 MVP SPI 实现
- [ ] 场景激活流程正常完成
- [ ] 数据正确存储和读取
- [ ] 消息正确发送

---

## 六、里程碑

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           开发里程碑                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Week 1                          Week 2                                    │
│  ┌─────────────────────────┐    ┌─────────────────────────┐               │
│  │ P0 任务完成              │    │ P1/P2 任务完成           │               │
│  │                         │    │                         │               │
│  │ • MVP-001 SceneServices │    │ • MVP-003 执行器         │               │
│  │ • MVP-002 数据持久化    │    │ • MVP-004 监听器         │               │
│  └─────────────────────────┘    └─────────────────────────┘               │
│             │                              │                                │
│             ▼                              ▼                                │
│        SPI 可用                       功能完整                              │
│                                                                             │
│  目标: 60%                        目标: 100%                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、联系人

| 角色 | 联系人 | 联系方式 |
|------|--------|----------|
| SE SDK 负责人 | - | - |
| MVP 团队负责人 | - | - |
| SPI 评审 | - | - |

---

## 八、参考文档

- [SE SDK 覆盖度报告](./SCENE_LIFECYCLE_COVERAGE_V4.md)
- [SDK 协作说明](./SDK_COLLABORATION_GUIDE.md)
- [SceneServices 接口定义](../../src/main/java/net/ooder/scene/spi/SceneServices.java)

---

*文档版本: 1.0*  
*创建日期: 2026-03-22*  
*SE SDK 团队*
