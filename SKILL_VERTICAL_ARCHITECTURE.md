# Skills 纵向贯穿架构设计

## 一、核心架构思想

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    Web 层 (UI Layer)                                     │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │  React/Vue/Angular 前端应用                                                         │  │
│  │  - 通过 Skills 获取可视化模式 (VisualizationSchema)                                  │  │
│  │  - 通过 Skills 调用后端能力                                                          │  │
│  │  - 通过 Skills 订阅实时事件                                                          │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
│                                          ▲                                               │
│                                          │ Skills 调用 (HTTP/WebSocket)                   │
│                                          ▼                                               │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                 Engine 层 (Application Layer)                            │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │  scene-engine (场景引擎)                                                            │  │
│  │  - 编排 Skills 执行业务流程                                                          │  │
│  │  - 管理场景生命周期                                                                  │  │
│  │  - 协调多个 Skills 协作                                                              │  │
│  │                                                                                     │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐               │  │
│  │  │ SceneEngine │  │WorkflowEngine│  │ EventEngine │  │  Discovery  │               │  │
│  │  │  (场景编排)  │  │  (工作流)    │  │  (事件总线)  │  │  (发现服务)  │               │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘               │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
│                                          ▲                                               │
│                                          │ Skills 调用 (Java API)                        │
│                                          ▼                                               │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                   Driver 层 (Driver Layer)                               │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │  ooder-infra-driver (驱动框架)                                                      │  │
│  │  - 统一管理 Skills 的生命周期                                                        │  │
│  │  - 提供 Skills 注册、发现、调用机制                                                  │  │
│  │  - 支持多协议 (HTTP/WebSocket/TCP/UDP/A2A)                                          │  │
│  │                                                                                     │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐               │  │
│  │  │   Driver    │  │   Driver    │  │   Driver    │  │   Driver    │               │  │
│  │  │   Registry  │  │   Context   │  │   Loader    │  │   Manager   │               │  │
│  │  │  (注册中心)  │  │  (上下文)    │  │  (加载器)    │  │  (管理器)    │               │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘               │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                          │
                                          │ Skills 纵向贯穿 (跨层调用)
                                          ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              Skills 层 (纵向贯穿)                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │  skill-ai   │  │  skill-vfs  │  │  skill-org  │  │  skill-msg  │  │ skill-...   │   │
│  │   (AI能力)   │  │  (文件能力)  │  │  (组织权限)  │  │  (消息通信)  │  │  (其他能力)  │   │
│  │                                                                                     │   │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐                 │   │
│  │  │ Driver  │  │ Engine  │  │   Web   │  │  Proto  │  │  Visual │                 │   │
│  │  │  (驱动)  │  │ (引擎)   │  │ (界面)   │  │ (协议)   │  │ (可视化) │                 │   │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘                 │   │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、Skills 三层实现模型

每个 Skill 都包含三层实现：

```java
/**
 * Skill 三层接口定义
 */
public interface Skill {
    
    String getSkillName();
    String getVersion();
    
    // ========== Driver 层实现 ==========
    SkillDriver getDriver();
    
    // ========== Engine 层实现 ==========
    SkillEngine getEngine();
    
    // ========== Web 层实现 ==========
    SkillWeb getWeb();
}

/**
 * Skill Driver 层 - 与基础设施交互
 */
public interface SkillDriver {
    
    // 初始化（加载配置、建立连接）
    void initialize(DriverContext context);
    
    // 健康检查
    HealthStatus checkHealth();
    
    // 执行底层操作
    Object execute(String operation, Map<String, Object> params);
    
    // 获取协议处理器
    ProtocolHandler getProtocolHandler();
}

/**
 * Skill Engine 层 - 业务逻辑编排
 */
public interface SkillEngine {
    
    // 获取能力定义
    List<Capability> getCapabilities();
    
    // 执行能力
    Object invokeCapability(String capabilityId, Map<String, Object> params);
    
    // 订阅事件
    void subscribe(String eventType, EventHandler handler);
    
    // 发布事件
    void publish(String eventType, Object event);
}

/**
 * Skill Web 层 - 可视化与交互
 */
public interface SkillWeb {
    
    // 获取可视化模式
    VisualizationSchema getVisualizationSchema();
    
    // 渲染界面
    String render(String viewType, Object data);
    
    // 处理前端请求
    Object handleRequest(String action, Map<String, Object> params);
}
```

---

## 三、Skills 纵向贯穿示例

### 3.1 VFS Skill 三层实现

```java
@Component
public class VfsSkill implements Skill {
    
    @Autowired
    private VfsDriver vfsDriver;
    
    @Autowired
    private VfsEngine vfsEngine;
    
    @Autowired
    private VfsWeb vfsWeb;
    
    @Override
    public String getSkillName() { return "vfs"; }
    
    @Override
    public String getVersion() { return "2.3.0"; }
    
    @Override
    public SkillDriver getDriver() { return vfsDriver; }
    
    @Override
    public SkillEngine getEngine() { return vfsEngine; }
    
    @Override
    public SkillWeb getWeb() { return vfsWeb; }
}

// ========== Driver 层 ==========
@Component
public class VfsDriver implements SkillDriver {
    
    @Autowired
    private VfsStoreService vfsService;
    
    @Override
    public void initialize(DriverContext context) {
        // 初始化文件存储连接
        vfsService.initialize(context.getConfig());
    }
    
    @Override
    public HealthStatus checkHealth() {
        return vfsService.ping() ? HealthStatus.HEALTHY : HealthStatus.UNHEALTHY;
    }
    
    @Override
    public Object execute(String operation, Map<String, Object> params) {
        switch (operation) {
            case "store":
                return vfsService.storeFile(
                    (InputStream) params.get("input"),
                    (String) params.get("path")
                );
            case "read":
                return vfsService.readFile((String) params.get("fileId"));
            // ...
        }
    }
    
    @Override
    public ProtocolHandler getProtocolHandler() {
        return new VfsProtocolHandler();  // 支持 vfs:// 协议
    }
}

// ========== Engine 层 ==========
@Component
public class VfsEngine implements SkillEngine {
    
    @Autowired
    private VfsDriver vfsDriver;
    
    @Autowired
    private EventBus eventBus;
    
    @Override
    public List<Capability> getCapabilities() {
        return Arrays.asList(
            Capability.of("vfs.store", "存储文件", this::storeFile),
            Capability.of("vfs.read", "读取文件", this::readFile),
            Capability.of("vfs.delete", "删除文件", this::deleteFile),
            Capability.of("vfs.list", "列出文件", this::listFiles)
        );
    }
    
    @Override
    public Object invokeCapability(String capabilityId, Map<String, Object> params) {
        // 前置处理：权限检查、日志记录
        log.info("Invoking vfs capability: {}", capabilityId);
        
        // 调用 Driver 层
        Object result = vfsDriver.execute(
            capabilityId.replace("vfs.", ""), 
            params
        );
        
        // 后置处理：发布事件
        eventBus.publish(new VfsOperationEvent(capabilityId, params, result));
        
        return result;
    }
    
    private Object storeFile(Map<String, Object> params) {
        // 业务逻辑：检查配额、生成文件ID、存储文件
        String fileId = generateFileId();
        params.put("fileId", fileId);
        return vfsDriver.execute("store", params);
    }
    
    // ... 其他能力实现
}

// ========== Web 层 ==========
@Component
public class VfsWeb implements SkillWeb {
    
    @Autowired
    private VfsEngine vfsEngine;
    
    @Override
    public VisualizationSchema getVisualizationSchema() {
        return VisualizationSchema.builder()
            .schemaId("vfs-manager")
            .displayName("文件管理器")
            .category("dashboard")
            .icon("folder-open")
            .fields(Arrays.asList(
                FieldSchema.builder()
                    .name("fileId")
                    .displayName("文件ID")
                    .type("string")
                    .readonly(true)
                    .build(),
                FieldSchema.builder()
                    .name("fileName")
                    .displayName("文件名")
                    .type("string")
                    .searchable(true)
                    .sortable(true)
                    .build(),
                FieldSchema.builder()
                    .name("fileSize")
                    .displayName("文件大小")
                    .type("number")
                    .formatter("fileSize")
                    .sortable(true)
                    .build(),
                FieldSchema.builder()
                    .name("createTime")
                    .displayName("创建时间")
                    .type("datetime")
                    .sortable(true)
                    .build()
            ))
            .actions(Arrays.asList(
                ActionSchema.builder()
                    .id("upload")
                    .displayName("上传")
                    .type("create")
                    .icon("upload")
                    .inputFields(Arrays.asList(
                        FieldSchema.builder()
                            .name("file")
                            .displayName("选择文件")
                            .type("file")
                            .required(true)
                            .build()
                    ))
                    .build(),
                ActionSchema.builder()
                    .id("download")
                    .displayName("下载")
                    .type("read")
                    .icon("download")
                    .build(),
                ActionSchema.builder()
                    .id("delete")
                    .displayName("删除")
                    .type("delete")
                    .icon("trash")
                    .confirmMessage("确定要删除此文件吗？")
                    .build(),
                ActionSchema.builder()
                    .id("preview")
                    .displayName("预览")
                    .type("read")
                    .icon("eye")
                    .modal(true)
                    .build()
            ))
            .layout(LayoutSchema.builder()
                .type("table")
                .pagination(true)
                .pageSize(20)
                .build())
            .build();
    }
    
    @Override
    public String render(String viewType, Object data) {
        // 根据 viewType 渲染不同界面
        switch (viewType) {
            case "table":
                return renderTableView(data);
            case "grid":
                return renderGridView(data);
            case "tree":
                return renderTreeView(data);
            default:
                return renderDefaultView(data);
        }
    }
    
    @Override
    public Object handleRequest(String action, Map<String, Object> params) {
        // 处理前端请求，转发到 Engine 层
        switch (action) {
            case "listFiles":
                return vfsEngine.invokeCapability("vfs.list", params);
            case "uploadFile":
                return vfsEngine.invokeCapability("vfs.store", params);
            // ...
        }
    }
}
```

---

## 四、跨层调用机制

### 4.1 调用链路

```
Web 层 (前端)
    │
    │ HTTP/WebSocket 请求
    ▼
Web 层 (SkillWeb)
    │ handleRequest()
    ▼
Engine 层 (SkillEngine)
    │ invokeCapability()
    ▼
Driver 层 (SkillDriver)
    │ execute()
    ▼
基础设施 (数据库/文件系统/网络)
```

### 4.2 反向调用（事件通知）

```
基础设施 (文件变更)
    │
    ▼
Driver 层 (SkillDriver)
    │ 检测到变更
    ▼
Engine 层 (SkillEngine)
    │ publishEvent()
    ▼
Web 层 (SkillWeb)
    │ WebSocket 推送
    ▼
Web 层 (前端)
    │ 更新界面
```

### 4.3 Skills 间协作

```java
// Engine 层调用其他 Skills
@Service
public class DocumentWorkflowEngine {
    
    @Autowired
    private SkillRegistry skillRegistry;
    
    public void processDocument(String docId) {
        // 1. 调用 vfs skill 读取文档
        Skill vfsSkill = skillRegistry.getSkill("vfs");
        Object doc = vfsSkill.getEngine().invokeCapability("vfs.read", 
            Map.of("fileId", docId));
        
        // 2. 调用 ai skill 分析文档
        Skill aiSkill = skillRegistry.getSkill("ai");
        Object analysis = aiSkill.getEngine().invokeCapability("ai.analyze",
            Map.of("content", doc));
        
        // 3. 调用 index skill 索引文档
        Skill indexSkill = skillRegistry.getSkill("index");
        indexSkill.getEngine().invokeCapability("index.add",
            Map.of("docId", docId, "content", analysis));
        
        // 4. 调用 msg skill 发送通知
        Skill msgSkill = skillRegistry.getSkill("msg");
        msgSkill.getEngine().invokeCapability("msg.send",
            Map.of("to", "admin", "message", "文档处理完成"));
    }
}
```

---

## 五、架构优势

| 特性 | 说明 |
|-----|------|
| **纵向贯穿** | Skills 从 Driver 到 Web 三层贯通，每层都可独立演进 |
| **横向协作** | 不同 Skills 通过 Engine 层协作，实现复杂业务流程 |
| **分层解耦** | Driver/Engine/Web 三层职责清晰，互不依赖 |
| **可视化自动生成** | Web 层通过 Schema 自动生成界面，无需手写前端代码 |
| **协议统一** | Driver 层统一处理协议，支持多协议并存 |
| **可测试性** | 每层可独立 Mock 测试，也可集成测试 |

---

## 六、与之前架构的对比

| 架构 | 特点 | 适用场景 |
|-----|------|---------|
| **Driver 框架扩展** | Driver 层能力扩展（发现/可视化/协议） | 基础设施层统一 |
| **Skills 纵向贯穿** | Skills 三层实现，纵向贯穿 | 业务能力复用 |
| **统一 CapabilityProvider** | 所有能力统一接口 | 简化调用 |

**整合方案：**
- Driver 层：使用 Driver 框架 + 扩展能力（发现/可视化/协议）
- Engine 层：通过 Skills 协作，编排业务流程
- Web 层：通过 Skills Web 层自动生成界面

**一句话总结：**
> **Skills 是纵向贯穿的业务能力单元，每个 Skill 包含 Driver/Engine/Web 三层实现，Driver 层使用统一框架（支持发现/可视化/协议），Engine 层编排业务流程，Web 层自动生成界面，实现业务能力的全栈复用。**
