# Agent SDK CLI - 驱动层接口设计

## 概述

本文档定义CLI驱动层的标准接口，作为应用层与底层实现（Skills框架、Agent SDK）之间的桥梁。驱动层采用接口隔离原则，确保应用层不依赖具体实现。

## 1. 驱动层架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        驱动层接口 (Driver Interfaces)                 │
├─────────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐│
│  │ SkillDriver  │ │ SceneDriver  │ │ TaskDriver   │ │ ConfigDriver ││
│  │ 接口定义      │ │ 接口定义      │ │ 接口定义      │ │ 接口定义      ││
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘│
└─────────────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│ SkillsFramework │ │   Agent SDK     │ │   Mock/Test     │
│   Driver Impl   │ │   Driver Impl   │ │   Driver Impl   │
│                 │ │                 │ │                 │
│ - SkillRegistry │ │ - Core APIs     │ │ - In-Memory     │
│ - SkillInstaller│ │ - SceneManager  │ │ - Simulation    │
│ - SkillInvoker  │ │ - TaskScheduler │ │ - Stub          │
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

## 2. 核心驱动接口

### 2.1 Skill 驱动接口

```java
/**
 * Skill 驱动接口
 * 
 * 职责：封装所有 Skill 相关的底层操作，包括生命周期管理、能力调用等
 */
public interface SkillDriver {
    
    // ==================== 生命周期管理 ====================
    
    /**
     * 安装 Skill
     * 
     * @param source Skill 来源（JAR 路径、Maven 坐标、Git URL 等）
     * @param config 安装配置参数
     * @return 安装后的 Skill 实体
     * @throws SkillInstallException 安装失败时抛出
     */
    SkillEntity install(String source, Map<String, Object> config) 
        throws SkillInstallException;
    
    /**
     * 卸载 Skill
     * 
     * @param skillId Skill 唯一标识
     * @param force 是否强制卸载（即使正在运行）
     * @return 卸载结果
     * @throws SkillNotFoundException Skill 不存在时抛出
     */
    UninstallResult uninstall(String skillId, boolean force) 
        throws SkillNotFoundException;
    
    /**
     * 启动 Skill
     * 
     * @param skillId Skill 唯一标识
     * @param params 启动参数
     * @return 启动结果
     */
    StartResult start(String skillId, Map<String, Object> params);
    
    /**
     * 停止 Skill
     * 
     * @param skillId Skill 唯一标识
     * @param force 是否强制停止
     * @return 停止结果
     */
    StopResult stop(String skillId, boolean force);
    
    /**
     * 重启 Skill
     * 
     * @param skillId Skill 唯一标识
     * @param params 启动参数
     * @return 重启结果
     */
    RestartResult restart(String skillId, Map<String, Object> params);
    
    // ==================== 查询操作 ====================
    
    /**
     * 获取所有已安装的 Skills
     * 
     * @return Skill 实体列表
     */
    List<SkillEntity> getAllSkills();
    
    /**
     * 根据 ID 获取 Skill
     * 
     * @param skillId Skill 唯一标识
     * @return Skill 实体，不存在返回 null
     */
    SkillEntity getSkill(String skillId);
    
    /**
     * 根据名称模糊查询 Skills
     * 
     * @param namePattern 名称匹配模式
     * @return 匹配的 Skill 列表
     */
    List<SkillEntity> findSkillsByName(String namePattern);
    
    /**
     * 获取 Skill 状态
     * 
     * @param skillId Skill 唯一标识
     * @return 当前状态
     */
    SkillStatus getStatus(String skillId);
    
    /**
     * 检查 Skill 健康状态
     * 
     * @param skillId Skill 唯一标识
     * @return 健康检查结果
     */
    HealthStatus checkHealth(String skillId);
    
    // ==================== 能力调用 ====================
    
    /**
     * 同步调用 Skill 能力
     * 
     * @param skillId Skill 唯一标识
     * @param capabilityId 能力标识
     * @param params 调用参数
     * @return 调用结果
     * @throws CapabilityNotFoundException 能力不存在时抛出
     * @throws InvokeException 调用失败时抛出
     */
    Object invoke(String skillId, String capabilityId, Map<String, Object> params)
        throws CapabilityNotFoundException, InvokeException;
    
    /**
     * 异步调用 Skill 能力
     * 
     * @param skillId Skill 唯一标识
     * @param capabilityId 能力标识
     * @param params 调用参数
     * @param callback 回调接口
     * @return 任务ID
     */
    String invokeAsync(String skillId, String capabilityId, 
                       Map<String, Object> params, InvokeCallback callback);
    
    /**
     * 获取 Skill 支持的所有能力
     * 
     * @param skillId Skill 唯一标识
     * @return 能力定义列表
     */
    List<Capability> getCapabilities(String skillId);
    
    /**
     * 获取特定能力的详细信息
     * 
     * @param skillId Skill 唯一标识
     * @param capabilityId 能力标识
     * @return 能力定义
     */
    Capability getCapability(String skillId, String capabilityId);
    
    // ==================== 事件订阅 ====================
    
    /**
     * 订阅 Skill 状态变更事件
     * 
     * @param listener 事件监听器
     * @return 订阅句柄
     */
    Subscription subscribeStatusChange(StatusChangeListener listener);
    
    /**
     * 订阅特定 Skill 的事件
     * 
     * @param skillId Skill 唯一标识
     * @param eventType 事件类型
     * @param listener 事件监听器
     * @return 订阅句柄
     */
    Subscription subscribeSkillEvent(String skillId, String eventType, 
                                      SkillEventListener listener);
    
    // ==================== 元数据管理 ====================
    
    /**
     * 更新 Skill 元数据
     * 
     * @param skillId Skill 唯一标识
     * @param metadata 元数据
     * @return 更新结果
     */
    UpdateResult updateMetadata(String skillId, Map<String, Object> metadata);
    
    /**
     * 获取 Skill 统计信息
     * 
     * @param skillId Skill 唯一标识
     * @return 统计信息
     */
    SkillStatistics getStatistics(String skillId);
}
```

### 2.2 Scene 驱动接口

```java
/**
 * 场景驱动接口
 * 
 * 职责：封装场景生命周期管理、成员管理、能力调用等操作
 */
public interface SceneDriver {
    
    // ==================== 场景生命周期 ====================
    
    /**
     * 创建场景
     * 
     * @param request 创建请求
     * @return 创建结果，包含 sceneId
     */
    CreateSceneResult create(CreateSceneRequest request);
    
    /**
     * 销毁场景
     * 
     * @param sceneId 场景ID
     * @param force 是否强制销毁
     * @return 销毁结果
     */
    DestroyResult destroy(String sceneId, boolean force);
    
    /**
     * 暂停场景
     * 
     * @param sceneId 场景ID
     * @return 暂停结果
     */
    PauseResult pause(String sceneId);
    
    /**
     * 恢复场景
     * 
     * @param sceneId 场景ID
     * @return 恢复结果
     */
    ResumeResult resume(String sceneId);
    
    // ==================== 场景查询 ====================
    
    /**
     * 获取所有场景
     * 
     * @return 场景实体列表
     */
    List<SceneEntity> getAllScenes();
    
    /**
     * 根据 ID 获取场景
     * 
     * @param sceneId 场景ID
     * @return 场景实体
     */
    SceneEntity getScene(String sceneId);
    
    /**
     * 根据 Group ID 获取场景
     * 
     * @param groupId 场景组ID
     * @return 场景实体列表
     */
    List<SceneEntity> getScenesByGroup(String groupId);
    
    /**
     * 获取场景状态
     * 
     * @param sceneId 场景ID
     * @return 场景状态
     */
    SceneStatus getStatus(String sceneId);
    
    /**
     * 获取场景快照
     * 
     * @param sceneId 场景ID
     * @return 场景快照
     */
    SceneSnapshot getSnapshot(String sceneId);
    
    // ==================== 成员管理 ====================
    
    /**
     * 添加场景成员
     * 
     * @param sceneId 场景ID
     * @param member 成员配置
     * @return 添加结果
     */
    AddMemberResult addMember(String sceneId, MemberConfig member);
    
    /**
     * 移除场景成员
     * 
     * @param sceneId 场景ID
     * @param memberId 成员ID
     * @return 移除结果
     */
    RemoveMemberResult removeMember(String sceneId, String memberId);
    
    /**
     * 更新成员配置
     * 
     * @param sceneId 场景ID
     * @param memberId 成员ID
     * @param config 新配置
     * @return 更新结果
     */
    UpdateMemberResult updateMember(String sceneId, String memberId, 
                                     Map<String, Object> config);
    
    /**
     * 获取场景成员列表
     * 
     * @param sceneId 场景ID
     * @return 成员列表
     */
    List<SceneMember> getMembers(String sceneId);
    
    /**
     * 获取主成员
     * 
     * @param sceneId 场景ID
     * @return 主成员
     */
    SceneMember getMainMember(String sceneId);
    
    // ==================== 能力调用 ====================
    
    /**
     * 调用场景能力
     * 
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @param params 调用参数
     * @return 调用结果
     */
    InvokeResult invokeCapability(String sceneId, String capabilityId,
                                   Map<String, Object> params);
    
    /**
     * 异步调用场景能力
     * 
     * @param sceneId 场景ID
     * @param capabilityId 能力ID
     * @param params 调用参数
     * @param callback 回调
     * @return 任务ID
     */
    String invokeCapabilityAsync(String sceneId, String capabilityId,
                                  Map<String, Object> params, 
                                  InvokeCallback callback);
    
    /**
     * 批量调用能力
     * 
     * @param sceneId 场景ID
     * @param invocations 调用列表
     * @return 批量调用结果
     */
    BatchInvokeResult batchInvoke(String sceneId, 
                                   List<CapabilityInvocation> invocations);
    
    // ==================== 事件处理 ====================
    
    /**
     * 发布场景事件
     * 
     * @param sceneId 场景ID
     * @param eventType 事件类型
     * @param payload 事件载荷
     * @return 发布结果
     */
    EventResult publishEvent(String sceneId, String eventType, 
                              Map<String, Object> payload);
    
    /**
     * 订阅场景事件
     * 
     * @param sceneId 场景ID
     * @param eventType 事件类型（null 表示订阅所有）
     * @param listener 监听器
     * @return 订阅句柄
     */
    Subscription subscribeEvent(String sceneId, String eventType,
                                 SceneEventListener listener);
    
    /**
     * 广播事件给所有成员
     * 
     * @param sceneId 场景ID
     * @param eventType 事件类型
     * @param payload 事件载荷
     * @return 广播结果
     */
    BroadcastResult broadcastEvent(String sceneId, String eventType,
                                    Map<String, Object> payload);
    
    // ==================== 上下文管理 ====================
    
    /**
     * 获取场景上下文
     * 
     * @param sceneId 场景ID
     * @return 上下文数据
     */
    Map<String, Object> getContext(String sceneId);
    
    /**
     * 更新场景上下文
     * 
     * @param sceneId 场景ID
     * @param context 上下文数据
     * @return 更新结果
     */
    UpdateResult updateContext(String sceneId, Map<String, Object> context);
    
    /**
     * 设置上下文值
     * 
     * @param sceneId 场景ID
     * @param key 键
     * @param value 值
     * @return 设置结果
     */
    UpdateResult setContextValue(String sceneId, String key, Object value);
}
```

### 2.3 Task 驱动接口

```java
/**
 * 任务驱动接口
 * 
 * 职责：封装任务提交、调度、监控等操作
 */
public interface TaskDriver {
    
    // ==================== 任务提交 ====================
    
    /**
     * 提交任务
     * 
     * @param request 提交请求
     * @return 提交结果，包含 taskId
     */
    SubmitResult submit(SubmitTaskRequest request);
    
    /**
     * 批量提交任务
     * 
     * @param requests 请求列表
     * @return 批量提交结果
     */
    BatchSubmitResult submitBatch(List<SubmitTaskRequest> requests);
    
    /**
     * 提交定时任务
     * 
     * @param request 提交请求
     * @param trigger 触发器配置
     * @return 提交结果
     */
    SubmitResult submitScheduled(SubmitTaskRequest request, TriggerConfig trigger);
    
    // ==================== 任务控制 ====================
    
    /**
     * 取消任务
     * 
     * @param taskId 任务ID
     * @param force 是否强制取消
     * @return 取消结果
     */
    CancelResult cancel(String taskId, boolean force);
    
    /**
     * 暂停任务
     * 
     * @param taskId 任务ID
     * @return 暂停结果
     */
    PauseResult pause(String taskId);
    
    /**
     * 恢复任务
     * 
     * @param taskId 任务ID
     * @return 恢复结果
     */
    ResumeResult resume(String taskId);
    
    /**
     * 重试失败任务
     * 
     * @param taskId 任务ID
     * @return 重试结果
     */
    RetryResult retry(String taskId);
    
    // ==================== 任务查询 ====================
    
    /**
     * 获取任务信息
     * 
     * @param taskId 任务ID
     * @return 任务实体
     */
    TaskEntity getTask(String taskId);
    
    /**
     * 查询任务列表
     * 
     * @param query 查询条件
     * @return 任务列表
     */
    List<TaskEntity> queryTasks(TaskQuery query);
    
    /**
     * 获取任务状态
     * 
     * @param taskId 任务ID
     * @return 任务状态
     */
    TaskStatus getStatus(String taskId);
    
    /**
     * 获取任务结果
     * 
     * @param taskId 任务ID
     * @return 任务结果
     */
    TaskResult getResult(String taskId);
    
    /**
     * 获取任务进度
     * 
     * @param taskId 任务ID
     * @return 进度信息（0-100）
     */
    TaskProgress getProgress(String taskId);
    
    /**
     * 获取任务日志
     * 
     * @param taskId 任务ID
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 日志列表
     */
    List<TaskLog> getLogs(String taskId, int offset, int limit);
    
    /**
     * 流式获取任务日志
     * 
     * @param taskId 任务ID
     * @param consumer 日志消费者
     * @return 订阅句柄
     */
    Subscription streamLogs(String taskId, LogConsumer consumer);
    
    // ==================== 任务监控 ====================
    
    /**
     * 等待任务完成
     * 
     * @param taskId 任务ID
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 任务实体
     * @throws TimeoutException 超时时抛出
     */
    TaskEntity waitForCompletion(String taskId, long timeout, TimeUnit unit)
        throws TimeoutException;
    
    /**
     * 注册任务监听器
     * 
     * @param taskId 任务ID
     * @param listener 状态监听器
     * @return 订阅句柄
     */
    Subscription monitor(String taskId, TaskStatusListener listener);
    
    /**
     * 批量监控任务
     * 
     * @param taskIds 任务ID列表
     * @param listener 状态监听器
     * @return 订阅句柄
     */
    Subscription monitorBatch(List<String> taskIds, TaskStatusListener listener);
    
    // ==================== 任务统计 ====================
    
    /**
     * 获取任务统计
     * 
     * @param query 统计查询条件
     * @return 统计结果
     */
    TaskStatistics getStatistics(TaskStatisticsQuery query);
    
    /**
     * 获取执行历史
     * 
     * @param skillId Skill ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 历史记录
     */
    List<TaskHistory> getExecutionHistory(String skillId, 
                                           LocalDateTime startTime, 
                                           LocalDateTime endTime);
    
    // ==================== 任务清理 ====================
    
    /**
     * 清理已完成任务
     * 
     * @param retention 保留时间
     * @return 清理结果
     */
    CleanupResult cleanupCompleted(Duration retention);
    
    /**
     * 删除任务记录
     * 
     * @param taskId 任务ID
     * @return 删除结果
     */
    DeleteResult delete(String taskId);
    
    /**
     * 批量删除任务
     * 
     * @param taskIds 任务ID列表
     * @return 批量删除结果
     */
    BatchDeleteResult deleteBatch(List<String> taskIds);
}
```

### 2.4 Config 驱动接口

```java
/**
 * 配置驱动接口
 * 
 * 职责：封装配置的加载、保存、验证等操作
 */
public interface ConfigDriver {
    
    // ==================== 配置加载 ====================
    
    /**
     * 加载配置
     * 
     * @param key 配置键
     * @param type 配置类型
     * @return 配置对象
     */
    <T> T load(String key, Class<T> type);
    
    /**
     * 加载配置（带默认值）
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置对象
     */
    <T> T loadOrDefault(String key, T defaultValue);
    
    /**
     * 从指定源加载配置
     * 
     * @param source 配置源（文件路径、URL等）
     * @param type 配置类型
     * @return 配置对象
     */
    <T> T loadFromSource(String source, Class<T> type);
    
    /**
     * 加载所有配置
     * 
     * @return 配置映射
     */
    Map<String, Object> loadAll();
    
    // ==================== 配置保存 ====================
    
    /**
     * 保存配置
     * 
     * @param key 配置键
     * @param config 配置对象
     * @return 保存结果
     */
    SaveResult save(String key, Object config);
    
    /**
     * 批量保存配置
     * 
     * @param configs 配置映射
     * @return 批量保存结果
     */
    BatchSaveResult saveBatch(Map<String, Object> configs);
    
    /**
     * 保存到指定目标
     * 
     * @param key 配置键
     * @param config 配置对象
     * @param target 目标路径
     * @return 保存结果
     */
    SaveResult saveTo(String key, Object config, String target);
    
    // ==================== 配置删除 ====================
    
    /**
     * 删除配置
     * 
     * @param key 配置键
     * @return 删除结果
     */
    DeleteResult delete(String key);
    
    /**
     * 批量删除配置
     * 
     * @param keys 配置键列表
     * @return 批量删除结果
     */
    BatchDeleteResult deleteBatch(List<String> keys);
    
    // ==================== 配置查询 ====================
    
    /**
     * 检查配置是否存在
     * 
     * @param key 配置键
     * @return 是否存在
     */
    boolean exists(String key);
    
    /**
     * 获取所有配置键
     * 
     * @return 配置键列表
     */
    List<String> getKeys();
    
    /**
     * 根据前缀查询配置键
     * 
     * @param prefix 前缀
     * @return 配置键列表
     */
    List<String> getKeysByPrefix(String prefix);
    
    /**
     * 获取配置元数据
     * 
     * @param key 配置键
     * @return 元数据
     */
    ConfigMetadata getMetadata(String key);
    
    // ==================== 配置验证 ====================
    
    /**
     * 验证配置
     * 
     * @param key 配置键
     * @param config 配置对象
     * @return 验证结果
     */
    ValidationResult validate(String key, Object config);
    
    /**
     * 验证配置值
     * 
     * @param key 配置键
     * @param value 配置值
     * @return 验证结果
     */
    ValidationResult validateValue(String key, Object value);
    
    // ==================== 配置同步 ====================
    
    /**
     * 同步配置
     * 
     * @param source 源配置键
     * @param target 目标配置键
     * @return 同步结果
     */
    SyncResult sync(String source, String target);
    
    /**
     * 从远程同步配置
     * 
     * @param remoteUrl 远程地址
     * @param key 配置键
     * @return 同步结果
     */
    SyncResult syncFromRemote(String remoteUrl, String key);
    
    // ==================== 配置导入导出 ====================
    
    /**
     * 导出配置
     * 
     * @param keys 配置键列表（null 表示导出所有）
     * @param format 格式（json, yaml, properties）
     * @return 导出内容
     */
    ExportResult exportConfig(List<String> keys, String format);
    
    /**
     * 导入配置
     * 
     * @param content 配置内容
     * @param format 格式
     * @param overwrite 是否覆盖
     * @return 导入结果
     */
    ImportResult importConfig(String content, String format, boolean overwrite);
    
    /**
     * 从文件导入配置
     * 
     * @param filePath 文件路径
     * @param overwrite 是否覆盖
     * @return 导入结果
     */
    ImportResult importFromFile(String filePath, boolean overwrite);
    
    // ==================== 配置监听 ====================
    
    /**
     * 监听配置变更
     * 
     * @param key 配置键
     * @param listener 变更监听器
     * @return 订阅句柄
     */
    Subscription watch(String key, ConfigChangeListener listener);
    
    /**
     * 监听多个配置变更
     * 
     * @param keys 配置键列表
     * @param listener 变更监听器
     * @return 订阅句柄
     */
    Subscription watchBatch(List<String> keys, ConfigChangeListener listener);
    
    /**
     * 监听前缀匹配的配置变更
     * 
     * @param prefix 前缀
     * @param listener 变更监听器
     * @return 订阅句柄
     */
    Subscription watchByPrefix(String prefix, ConfigChangeListener listener);
}
```

## 3. 回调与监听器接口

```java
/**
 * 调用回调接口
 */
public interface InvokeCallback {
    /**
     * 调用成功
     */
    void onSuccess(Object result);
    
    /**
     * 调用失败
     */
    void onFailure(Throwable error);
    
    /**
     * 调用进度更新
     */
    default void onProgress(int progress, String message) {}
}

/**
 * 状态变更监听器
 */
public interface StatusChangeListener {
    /**
     * 状态变更
     */
    void onStatusChanged(String id, Object oldStatus, Object newStatus, String reason);
}

/**
 * 任务状态监听器
 */
public interface TaskStatusListener {
    /**
     * 状态变更
     */
    void onStatusChanged(String taskId, TaskStatus oldStatus, TaskStatus newStatus);
    
    /**
     * 进度更新
     */
    default void onProgress(String taskId, int progress) {}
    
    /**
     * 日志输出
     */
    default void onLog(String taskId, String level, String message) {}
}

/**
 * 场景事件监听器
 */
public interface SceneEventListener {
    /**
     * 事件触发
     */
    void onEvent(String sceneId, String eventType, Map<String, Object> payload);
}

/**
 * 配置变更监听器
 */
public interface ConfigChangeListener {
    /**
     * 配置变更
     */
    void onConfigChanged(String key, Object oldValue, Object newValue);
}

/**
 * 日志消费者
 */
public interface LogConsumer {
    /**
     * 消费日志
     */
    void consume(TaskLog log);
}
```

## 4. 结果对象定义

```java
/**
 * 基础结果
 */
@Data
public abstract class BaseResult {
    private boolean success;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
}

/**
 * 安装结果
 */
@Data
public class InstallResult extends BaseResult {
    private String skillId;
    private String version;
    private List<String> installedCapabilities;
    private Map<String, Object> config;
}

/**
 * 创建场景结果
 */
@Data
public class CreateSceneResult extends BaseResult {
    private String sceneId;
    private String groupId;
    private List<String> initializedCapabilities;
}

/**
 * 提交任务结果
 */
@Data
public class SubmitResult extends BaseResult {
    private String taskId;
    private TaskStatus initialStatus;
    private Long estimatedDuration;
}

/**
 * 保存结果
 */
@Data
public class SaveResult extends BaseResult {
    private String key;
    private String version;
}

/**
 * 验证结果
 */
@Data
public class ValidationResult {
    private boolean valid;
    private List<ValidationError> errors;
    private List<ValidationWarning> warnings;
}
```

## 5. 驱动工厂

```java
/**
 * 驱动工厂
 * 
 * 用于创建和管理驱动实例
 */
public interface DriverFactory {
    
    /**
     * 创建 Skill 驱动
     */
    SkillDriver createSkillDriver(DriverConfig config);
    
    /**
     * 创建 Scene 驱动
     */
    SceneDriver createSceneDriver(DriverConfig config);
    
    /**
     * 创建 Task 驱动
     */
    TaskDriver createTaskDriver(DriverConfig config);
    
    /**
     * 创建 Config 驱动
     */
    ConfigDriver createConfigDriver(DriverConfig config);
    
    /**
     * 注册驱动实现
     */
    void registerDriver(String type, DriverProvider provider);
    
    /**
     * 获取已注册的驱动类型
     */
    List<String> getRegisteredTypes();
}

/**
 * 驱动提供者
 */
public interface DriverProvider {
    SkillDriver createSkillDriver(DriverConfig config);
    SceneDriver createSceneDriver(DriverConfig config);
    TaskDriver createTaskDriver(DriverConfig config);
    ConfigDriver createConfigDriver(DriverConfig config);
}

/**
 * 驱动配置
 */
@Data
public class DriverConfig {
    private String type;                    // 驱动类型：skills, agent-sdk, mock
    private Map<String, Object> properties; // 驱动特定配置
    private boolean cacheEnabled;           // 是否启用缓存
    private long timeout;                   // 默认超时
}
```

---

**文档版本**: 3.1.0  
**最后更新**: 2026-04-16  
**维护团队**: Agent SDK Team
