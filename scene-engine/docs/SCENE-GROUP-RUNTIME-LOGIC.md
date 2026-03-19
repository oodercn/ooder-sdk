# 场景组运行期逻辑设计文档

## 1. 概述

本文档描述 SceneEngine 2.3.1 场景组运行过程中的知识库存储、能力关联日志等相关逻辑。

## 2. 知识库存储逻辑

### 2.1 知识库绑定服务

**接口**: `net.ooder.scene.skill.knowledge.KnowledgeBindingService`

**核心能力**:
- 知识库绑定/解绑
- 知识检索
- 跨层检索

```java
public interface KnowledgeBindingService {
    // 绑定知识库到场景
    void bindToScene(String sceneGroupId, String kbId, String layer);
    
    // 从场景解绑知识库
    void unbindFromScene(String sceneGroupId, String kbId);
    
    // 检索知识
    List<KnowledgeChunk> searchKnowledge(String sceneGroupId, String query, int topK);
    
    // 跨层检索知识
    List<KnowledgeChunk> crossLayerSearch(String sceneGroupId, String query, 
                                           List<String> layers, int topK);
    
    // 获取场景绑定的知识库列表
    List<KnowledgeBinding> getBindings(String sceneGroupId);
}
```

### 2.2 知识库绑定模型

**类**: `net.ooder.scene.skill.knowledge.KnowledgeBinding`

**属性**:
| 属性 | 类型 | 说明 |
|------|------|------|
| sceneGroupId | String | 场景组ID |
| kbId | String | 知识库ID |
| kbName | String | 知识库名称 |
| layer | String | 层级（global, team, personal） |
| bindTime | long | 绑定时间戳 |

### 2.3 知识库层级架构

```
┌─────────────────────────────────────────────┐
│              Global Layer (全局层)            │
│         组织级知识库，所有场景共享              │
├─────────────────────────────────────────────┤
│              Team Layer (团队层)              │
│         团队级知识库，团队内场景共享            │
├─────────────────────────────────────────────┤
│            Personal Layer (个人层)            │
│         个人知识库，仅当前场景可用              │
└─────────────────────────────────────────────┘
```

### 2.4 知识库存储流程

```
1. 场景组创建
   └─→ 初始化知识库绑定列表

2. 绑定知识库
   └─→ KnowledgeBindingService.bindToScene()
       ├─→ 记录绑定关系
       ├─→ 设置层级（layer）
       └─→ 记录绑定时间

3. 知识检索
   └─→ KnowledgeBindingService.searchKnowledge()
       ├─→ 查询场景绑定的知识库
       ├─→ 执行向量检索
       └─→ 返回知识片段

4. 跨层检索
   └─→ KnowledgeBindingService.crossLayerSearch()
       ├─→ 按层级顺序检索
       ├─→ 合并结果
       └─→ 去重排序
```

## 3. 能力关联日志逻辑

### 3.1 能力映射服务

**类**: `net.ooder.scene.capability.CapabilityMappingService`

**核心能力**:
- 能力地址映射
- 能力操作注册
- 能力查找

**默认映射**:
| 能力ID | 地址 | 操作 |
|--------|------|------|
| user.* | ORG_LOCAL | create/read/update/delete/list |
| file.* | VFS_LOCAL | upload/download/delete/list |
| llm.* | LLM_OLLAMA | chat/complete/embed |
| knowledge.* | KNOW_VECTOR | search/add |

### 3.2 审计日志系统

**接口**: `net.ooder.scene.skill.audit.AuditLogger`

**核心能力**:
- 操作日志记录
- 日志查询
- 用户统计

```java
public interface AuditLogger {
    // 记录操作日志
    CompletableFuture<Boolean> logOperation(String userId, String operation, 
                                            String resourceId, boolean result);
    
    // 记录详细操作日志
    CompletableFuture<Boolean> log(AuditEntry entry);
    
    // 查询审计日志
    CompletableFuture<AuditLogQueryResult> queryLogs(String userId, String operation,
                                                      long startTime, long endTime, int limit);
    
    // 获取用户操作统计
    CompletableFuture<AuditStats> getUserStats(String userId, long startTime, long endTime);
}
```

### 3.3 审计日志模型

**类**: `net.ooder.scene.core.AuditLog`

**属性**:
| 属性 | 类型 | 说明 |
|------|------|------|
| logId | String | 日志ID |
| eventType | String | 事件类型 |
| severity | String | 严重级别 |
| userId | String | 用户ID |
| userName | String | 用户名 |
| source | String | 来源 |
| target | String | 目标 |
| action | String | 操作 |
| description | String | 描述 |
| result | String | 结果 |
| details | String | 详情 |
| ipAddress | String | IP地址 |
| timestamp | long | 时间戳 |

### 3.4 事件监听机制

**类**: `net.ooder.scene.event.listener.AuditEventListener`

**监听事件类型**:
| 事件类型 | 说明 | 日志级别 |
|----------|------|----------|
| LoginEvent | 登录事件 | INFO |
| LogoutEvent | 登出事件 | INFO |
| TokenEvent | Token事件 | INFO |
| OperationDeniedEvent | 操作拒绝事件 | WARN |
| SessionEvent | 会话事件 | INFO |
| SkillEvent | 技能事件 | INFO |
| CapabilityEvent | 能力事件 | INFO |
| ConfigEvent | 配置事件 | INFO |
| EngineEvent | 引擎事件 | INFO |
| SceneAgentEvent | 场景代理事件 | INFO |
| UserEvent | 用户事件 | INFO |
| PeerEvent | 节点事件 | INFO |

## 4. 场景组运行期数据流

### 4.1 场景组生命周期数据流

```
┌──────────────────────────────────────────────────────────────┐
│                    场景组生命周期                              │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  CREATING ──→ ACTIVE ⇄ SUSPENDED ──→ DESTROYING ──→ DESTROYED│
│      │           │         │              │                  │
│      │           │         │              │                  │
│      ▼           ▼         ▼              ▼                  │
│  ┌───────┐   ┌───────┐ ┌───────┐    ┌───────┐               │
│  │初始化 │   │运行中 │ │暂停中 │    │销毁中 │               │
│  │配置   │   │处理   │ │保持   │    │清理   │               │
│  └───────┘   └───────┘ └───────┘    └───────┘               │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### 4.2 知识库查询流程

```
用户查询请求
     │
     ▼
┌─────────────────┐
│ 场景组上下文     │
│ SceneGroup      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 知识库绑定服务   │
│ KnowledgeBinding│
│ Service         │
└────────┬────────┘
         │
         ├─────→ Global Layer 知识库
         │
         ├─────→ Team Layer 知识库
         │
         └─────→ Personal Layer 知识库
                   │
                   ▼
         ┌─────────────────┐
         │ 向量检索服务     │
         │ VectorStore     │
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │ 返回知识片段     │
         │ KnowledgeChunk  │
         └─────────────────┘
```

### 4.3 能力调用日志流程

```
能力调用请求
     │
     ▼
┌─────────────────┐
│ 能力映射服务     │
│ Capability      │
│ MappingService  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 能力地址解析     │
│ Capability      │
│ Address         │
└────────┬────────┘
         │
         ├─────→ 记录审计日志
         │       AuditLogger.log()
         │
         ▼
┌─────────────────┐
│ 能力执行         │
└────────┬────────┘
         │
         ├─────→ 成功 → 记录成功日志
         │
         └─────→ 失败 → 记录失败日志
                   │
                   ▼
         ┌─────────────────┐
         │ 发布能力事件     │
         │ CapabilityEvent │
         └────────┬────────┘
                  │
                  ▼
         ┌─────────────────┐
         │ 审计事件监听器   │
         │ AuditEvent      │
         │ Listener        │
         └─────────────────┘
```

## 5. 存储策略

### 5.1 知识库存储

| 数据类型 | 存储位置 | 说明 |
|----------|----------|------|
| 知识库绑定关系 | 内存 + 持久化 | SceneGroup.knowledgeBindings |
| 知识库文档 | 向量数据库 | Chroma/Milvus/Qdrant |
| 知识片段 | 向量数据库 | 向量化存储 |

### 5.2 日志存储

| 日志类型 | 存储位置 | 保留策略 |
|----------|----------|----------|
| 审计日志 | 文件/数据库 | 90天 |
| 能力调用日志 | 文件/数据库 | 30天 |
| 事件日志 | 文件 | 7天 |

## 6. 配置说明

### 6.1 知识库配置

```yaml
ooder:
  knowledge:
    # 默认层级
    default-layer: personal
    # 检索配置
    search:
      top-k: 5
      score-threshold: 0.7
    # 向量存储
    vector-store:
      type: chroma  # chroma, milvus, qdrant
      host: localhost
      port: 8000
```

### 6.2 审计日志配置

```yaml
ooder:
  audit:
    # 是否启用
    enabled: true
    # 日志级别
    level: INFO
    # 存储配置
    storage:
      type: file  # file, database
      path: ./logs/audit
      max-size: 100MB
      max-history: 90
```

## 7. 扩展点

### 7.1 自定义知识库绑定服务

实现 `KnowledgeBindingService` 接口：

```java
@Component
public class CustomKnowledgeBindingServiceImpl implements KnowledgeBindingService {
    @Override
    public void bindToScene(String sceneGroupId, String kbId, String layer) {
        // 自定义绑定逻辑
    }
    
    // ... 其他方法
}
```

### 7.2 自定义审计日志处理器

扩展 `AuditEventListener`：

```java
@Component
public class CustomAuditEventListener extends AuditEventListener {
    @Override
    @EventListener
    public void onCapabilityEvent(CapabilityEvent event) {
        super.onCapabilityEvent(event);
        // 自定义处理逻辑
    }
}
```

## 8. 注意事项

1. **知识库绑定**：场景组销毁时需要解绑所有知识库
2. **日志记录**：关键操作必须记录审计日志
3. **性能考虑**：知识检索使用异步方式
4. **安全考虑**：敏感操作记录完整上下文

---

**文档版本**: 1.0  
**创建日期**: 2026-03-19  
**适用版本**: SceneEngine 2.3.1
