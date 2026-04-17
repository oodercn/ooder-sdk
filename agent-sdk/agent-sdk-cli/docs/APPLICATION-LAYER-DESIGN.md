# Agent SDK CLI - 应用层功能抽象设计

## 概述

本文档定义CLI应用层的功能抽象，采用分层架构设计，将业务逻辑与底层实现解耦，支持Skills框架的无缝集成。

## 1. 应用层架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        应用层 (Application Layer)                    │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐│
│  │ SkillAppService│ │ SceneAppService│ │ TaskAppService │ │ ConfigAppService││
│  │              │ │              │ │              │ │              ││
│  │ - 生命周期管理 │ │ - 场景编排    │ │ - 任务调度    │ │ - 配置管理    ││
│  │ - 能力调用    │ │ - 事件处理    │ │ - 状态监控    │ │ - 安全策略    ││
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘│
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        领域层 (Domain Layer)                         │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐│
│  │ SkillEntity  │ │ SceneEntity  │ │ TaskEntity   │ │ ConfigEntity ││
│  │ SkillStatus  │ │ SceneStatus  │ │ TaskStatus   │ │ PolicyEntity ││
│  │ Capability   │ │ Event        │ │ TaskResult   │ │ SecurityRule ││
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘│
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        驱动层 (Driver Layer)                         │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐│
│  │ SkillDriver  │ │ SceneDriver  │ │ TaskDriver   │ │ ConfigDriver ││
│  │              │ │              │ │              │ │              ││
│  │ - 注册/发现   │ │ - 创建/销毁   │ │ - 提交/取消   │ │ - 加载/保存   ││
│  │ - 启动/停止   │ │ - 调用/监听   │ │ - 查询/监控   │ │ - 验证/同步   ││
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘│
└─────────────────────────────────────────────────────────────────────┘
```

## 2. 核心领域模型

### 2.1 Skill 领域模型

```java
/**
 * Skill 实体
 */
public class SkillEntity {
    private String skillId;
    private String name;
    private String version;
    private String description;
    private SkillStatus status;
    private List<Capability> capabilities;
    private Map<String, Object> metadata;
    private LocalDateTime installTime;
    private LocalDateTime lastActiveTime;
    
    // 领域行为
    public boolean canStart() {
        return status == SkillStatus.INSTALLED || status == SkillStatus.STOPPED;
    }
    
    public boolean canStop() {
        return status == SkillStatus.RUNNING;
    }
    
    public Capability getCapability(String capabilityId) {
        return capabilities.stream()
            .filter(c -> c.getId().equals(capabilityId))
            .findFirst()
            .orElse(null);
    }
}

/**
 * Skill 状态枚举
 */
public enum SkillStatus {
    INSTALLED,      // 已安装
    INITIALIZING,   // 初始化中
    RUNNING,        // 运行中
    STOPPED,        // 已停止
    ERROR,          // 错误状态
    UNINSTALLING    // 卸载中
}

/**
 * 能力定义
 */
public class Capability {
    private String id;
    private String name;
    private String description;
    private List<Parameter> inputParams;
    private List<Parameter> outputParams;
    private boolean async;
    private long timeout;
}
```

### 2.2 Scene 领域模型

```java
/**
 * 场景实体
 */
public class SceneEntity {
    private String sceneId;
    private String groupId;
    private String name;
    private SceneStatus status;
    private List<SceneMember> members;
    private SceneDefinition definition;
    private Map<String, Object> context;
    private LocalDateTime createTime;
    
    // 领域行为
    public boolean isActive() {
        return status == SceneStatus.ACTIVE;
    }
    
    public SceneMember getMainMember() {
        return members.stream()
            .filter(SceneMember::isMain)
            .findFirst()
            .orElse(null);
    }
    
    public void addMember(SceneMember member) {
        members.add(member);
        // 触发领域事件
        registerEvent(new MemberJoinedEvent(this, member));
    }
}

/**
 * 场景状态
 */
public enum SceneStatus {
    CREATING,       // 创建中
    ACTIVE,         // 活跃状态
    PAUSED,         // 暂停
    DESTROYING,     // 销毁中
    DESTROYED       // 已销毁
}

/**
 * 场景成员
 */
public class SceneMember {
    private String memberId;
    private String skillId;
    private String capabilityId;
    private MemberRole role;
    private boolean isMain;
    private Map<String, Object> config;
}
```

### 2.3 Task 领域模型

```java
/**
 * 任务实体
 */
public class TaskEntity {
    private String taskId;
    private String skillId;
    private String capabilityId;
    private TaskStatus status;
    private TaskPriority priority;
    private Object input;
    private TaskResult result;
    private List<TaskLog> logs;
    private LocalDateTime submitTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    // 领域行为
    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED || 
               status == TaskStatus.FAILED ||
               status == TaskStatus.CANCELLED;
    }
    
    public Duration getExecutionTime() {
        if (startTime == null) return Duration.ZERO;
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        return Duration.between(startTime, end);
    }
    
    public void cancel() {
        if (isCompleted()) {
            throw new IllegalStateException("Cannot cancel completed task");
        }
        this.status = TaskStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
        registerEvent(new TaskCancelledEvent(this));
    }
}

/**
 * 任务状态
 */
public enum TaskStatus {
    PENDING,        // 等待中
    SCHEDULING,     // 调度中
    RUNNING,        // 运行中
    COMPLETED,      // 已完成
    FAILED,         // 失败
    CANCELLED,      // 已取消
    TIMEOUT         // 超时
}
```

## 3. 应用服务接口

### 3.1 Skill 应用服务

```java
/**
 * Skill 应用服务接口
 */
public interface SkillAppService {
    
    /**
     * 获取所有 Skill
     */
    List<SkillEntity> getAllSkills();
    
    /**
     * 根据ID获取 Skill
     */
    SkillEntity getSkill(String skillId);
    
    /**
     * 安装 Skill
     */
    InstallResult installSkill(InstallRequest request);
    
    /**
     * 卸载 Skill
     */
    UninstallResult uninstallSkill(String skillId);
    
    /**
     * 启动 Skill
     */
    StartResult startSkill(String skillId);
    
    /**
     * 停止 Skill
     */
    StopResult stopSkill(String skillId);
    
    /**
     * 调用 Skill 能力
     */
    InvokeResult invokeCapability(String skillId, String capabilityId, 
                                   Map<String, Object> params);
    
    /**
     * 异步调用 Skill 能力
     */
    String invokeCapabilityAsync(String skillId, String capabilityId,
                                  Map<String, Object> params);
    
    /**
     * 获取 Skill 能力列表
     */
    List<Capability> getCapabilities(String skillId);
    
    /**
     * 检查 Skill 健康状态
     */
    HealthStatus checkHealth(String skillId);
}
```

### 3.2 Scene 应用服务

```java
/**
 * 场景应用服务接口
 */
public interface SceneAppService {
    
    /**
     * 获取所有场景
     */
    List<SceneEntity> getAllScenes();
    
    /**
     * 根据ID获取场景
     */
    SceneEntity getScene(String sceneId);
    
    /**
     * 创建场景
     */
    CreateSceneResult createScene(CreateSceneRequest request);
    
    /**
     * 销毁场景
     */
    DestroyResult destroyScene(String sceneId);
    
    /**
     * 调用场景能力
     */
    InvokeResult invokeCapability(String sceneId, String capabilityId,
                                   Map<String, Object> params);
    
    /**
     * 发布场景事件
     */
    EventResult publishEvent(String sceneId, String eventType,
                              Map<String, Object> payload);
    
    /**
     * 添加场景成员
     */
    AddMemberResult addMember(String sceneId, AddMemberRequest request);
    
    /**
     * 移除场景成员
     */
    RemoveResult removeMember(String sceneId, String memberId);
    
    /**
     * 获取场景状态快照
     */
    SceneSnapshot getSnapshot(String sceneId);
}
```

### 3.3 Task 应用服务

```java
/**
 * 任务应用服务接口
 */
public interface TaskAppService {
    
    /**
     * 获取所有任务
     */
    List<TaskEntity> getAllTasks(TaskQuery query);
    
    /**
     * 根据ID获取任务
     */
    TaskEntity getTask(String taskId);
    
    /**
     * 提交任务
     */
    SubmitResult submitTask(SubmitTaskRequest request);
    
    /**
     * 取消任务
     */
    CancelResult cancelTask(String taskId);
    
    /**
     * 获取任务状态
     */
    TaskStatus getTaskStatus(String taskId);
    
    /**
     * 获取任务结果
     */
    TaskResult getTaskResult(String taskId);
    
    /**
     * 获取任务日志
     */
    List<TaskLog> getTaskLogs(String taskId);
    
    /**
     * 等待任务完成
     */
    TaskEntity waitForCompletion(String taskId, long timeout, TimeUnit unit);
    
    /**
     * 清理已完成任务
     */
    CleanupResult cleanupCompletedTasks(Duration retention);
}
```

### 3.4 Config 应用服务

```java
/**
 * 配置应用服务接口
 */
public interface ConfigAppService {
    
    /**
     * 加载配置
     */
    <T> T loadConfig(String key, Class<T> type);
    
    /**
     * 保存配置
     */
    SaveResult saveConfig(String key, Object config);
    
    /**
     * 获取 CLI 配置
     */
    CliConfig getCliConfig();
    
    /**
     * 更新 CLI 配置
     */
    UpdateResult updateCliConfig(CliConfig config);
    
    /**
     * 获取安全策略
     */
    SecurityPolicy getSecurityPolicy();
    
    /**
     * 更新安全策略
     */
    UpdateResult updateSecurityPolicy(SecurityPolicy policy);
    
    /**
     * 验证配置
     */
    ValidationResult validateConfig(Object config);
    
    /**
     * 导出配置
     */
    ExportResult exportConfig(String format);
    
    /**
     * 导入配置
     */
    ImportResult importConfig(String content, String format);
}
```

## 4. 数据传输对象 (DTO)

### 4.1 Skill 相关 DTO

```java
/**
 * Skill 信息 DTO
 */
@Data
public class SkillInfoDTO {
    private String skillId;
    private String name;
    private String version;
    private String description;
    private String status;
    private List<CapabilityInfoDTO> capabilities;
    private LocalDateTime installTime;
    private LocalDateTime lastActiveTime;
}

/**
 * 安装请求
 */
@Data
public class InstallRequest {
    private String source;          // JAR 路径或坐标
    private String skillId;         // 可选，指定 Skill ID
    private boolean force;          // 强制重新安装
    private Map<String, Object> config; // 安装配置
}

/**
 * 安装结果
 */
@Data
public class InstallResult {
    private boolean success;
    private String skillId;
    private String message;
    private List<String> installedCapabilities;
    private LocalDateTime installTime;
}

/**
 * 调用结果
 */
@Data
public class InvokeResult {
    private boolean success;
    private String taskId;          // 异步任务ID
    private Object data;            // 同步返回数据
    private String message;
    private long executionTime;
}
```

### 4.2 Scene 相关 DTO

```java
/**
 * 场景信息 DTO
 */
@Data
public class SceneInfoDTO {
    private String sceneId;
    private String groupId;
    private String name;
    private String status;
    private List<MemberInfoDTO> members;
    private LocalDateTime createTime;
    private LocalDateTime lastActiveTime;
}

/**
 * 创建场景请求
 */
@Data
public class CreateSceneRequest {
    private String groupId;
    private String name;
    private String mainSkillId;
    private String mainCapabilityId;
    private List<MemberConfig> members;
    private Map<String, Object> context;
}

/**
 * 创建场景结果
 */
@Data
public class CreateSceneResult {
    private boolean success;
    private String sceneId;
    private String message;
    private List<String> initializedCapabilities;
}
```

### 4.3 Task 相关 DTO

```java
/**
 * 任务信息 DTO
 */
@Data
public class TaskInfoDTO {
    private String taskId;
    private String skillId;
    private String capabilityId;
    private String status;
    private String priority;
    private LocalDateTime submitTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long executionTime;
    private Object result;
}

/**
 * 提交任务请求
 */
@Data
public class SubmitTaskRequest {
    private String skillId;
    private String capabilityId;
    private Map<String, Object> params;
    private TaskPriority priority;
    private long timeout;
    private String callbackUrl;
}

/**
 * 任务查询
 */
@Data
public class TaskQuery {
    private String skillId;
    private TaskStatus status;
    private LocalDateTime startTimeFrom;
    private LocalDateTime startTimeTo;
    private int page;
    private int size;
}
```

## 5. 领域事件

```java
/**
 * 领域事件基类
 */
public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
}

/**
 * Skill 状态变更事件
 */
public class SkillStatusChangedEvent extends DomainEvent {
    private final String skillId;
    private final SkillStatus oldStatus;
    private final SkillStatus newStatus;
    private final String reason;
}

/**
 * 场景成员加入事件
 */
public class MemberJoinedEvent extends DomainEvent {
    private final String sceneId;
    private final SceneMember member;
}

/**
 * 任务完成事件
 */
public class TaskCompletedEvent extends DomainEvent {
    private final String taskId;
    private final TaskStatus finalStatus;
    private final TaskResult result;
}

/**
 * 配置变更事件
 */
public class ConfigChangedEvent extends DomainEvent {
    private final String configKey;
    private final Object oldValue;
    private final Object newValue;
}
```

## 6. 应用服务实现示例

```java
/**
 * Skill 应用服务实现
 */
@Service
public class SkillAppServiceImpl implements SkillAppService {
    
    private final SkillDriver skillDriver;
    private final DomainEventPublisher eventPublisher;
    
    @Autowired
    public SkillAppServiceImpl(SkillDriver skillDriver, 
                                DomainEventPublisher eventPublisher) {
        this.skillDriver = skillDriver;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public InstallResult installSkill(InstallRequest request) {
        // 1. 验证请求
        validateInstallRequest(request);
        
        // 2. 调用驱动层安装
        SkillEntity skill = skillDriver.install(request.getSource(), 
                                                  request.getConfig());
        
        // 3. 发布领域事件
        eventPublisher.publish(new SkillInstalledEvent(skill));
        
        // 4. 返回结果
        return InstallResult.builder()
            .success(true)
            .skillId(skill.getSkillId())
            .message("Skill installed successfully")
            .installedCapabilities(skill.getCapabilities().stream()
                .map(Capability::getId)
                .collect(Collectors.toList()))
            .installTime(LocalDateTime.now())
            .build();
    }
    
    @Override
    public InvokeResult invokeCapability(String skillId, String capabilityId,
                                          Map<String, Object> params) {
        // 1. 获取 Skill
        SkillEntity skill = getSkill(skillId);
        if (skill == null) {
            throw new SkillNotFoundException(skillId);
        }
        
        // 2. 验证能力
        Capability capability = skill.getCapability(capabilityId);
        if (capability == null) {
            throw new CapabilityNotFoundException(capabilityId);
        }
        
        // 3. 验证参数
        validateParams(capability, params);
        
        // 4. 调用驱动层
        long startTime = System.currentTimeMillis();
        Object result = skillDriver.invoke(skillId, capabilityId, params);
        long executionTime = System.currentTimeMillis() - startTime;
        
        // 5. 返回结果
        return InvokeResult.builder()
            .success(true)
            .data(result)
            .executionTime(executionTime)
            .message("Invocation successful")
            .build();
    }
    
    private void validateInstallRequest(InstallRequest request) {
        if (StringUtils.isBlank(request.getSource())) {
            throw new IllegalArgumentException("Source is required");
        }
        // 更多验证...
    }
    
    private void validateParams(Capability capability, Map<String, Object> params) {
        for (Parameter param : capability.getInputParams()) {
            if (param.isRequired() && !params.containsKey(param.getName())) {
                throw new MissingParameterException(param.getName());
            }
        }
    }
}
```

## 7. 与 Skills 框架集成

```java
/**
 * Skills 框架适配器
 * 将 Skills 框架的接口适配为应用层接口
 */
@Component
public class SkillsFrameworkAdapter implements SkillDriver {
    
    private final SkillRegistry skillRegistry;
    private final SkillInstaller skillInstaller;
    private final SkillInvoker skillInvoker;
    
    @Override
    public SkillEntity install(String source, Map<String, Object> config) {
        // 调用 Skills 框架安装
        InstallResult result = skillInstaller.install(
            InstallRequest.builder()
                .source(source)
                .config(config)
                .build()
        );
        
        // 转换为领域实体
        return convertToEntity(result.getInstalledSkill());
    }
    
    @Override
    public Object invoke(String skillId, String capabilityId, 
                         Map<String, Object> params) {
        // 调用 Skills 框架执行能力
        return skillInvoker.invoke(skillId, capabilityId, params);
    }
    
    @Override
    public List<SkillEntity> getAllSkills() {
        return skillRegistry.getAllSkills().stream()
            .map(this::convertToEntity)
            .collect(Collectors.toList());
    }
    
    private SkillEntity convertToEntity(InstalledSkill skill) {
        return SkillEntity.builder()
            .skillId(skill.getId())
            .name(skill.getName())
            .version(skill.getVersion())
            .status(convertStatus(skill.getStatus()))
            .capabilities(convertCapabilities(skill.getCapabilities()))
            .build();
    }
}
```

---

**文档版本**: 3.1.0  
**最后更新**: 2026-04-16  
**维护团队**: Agent SDK Team
