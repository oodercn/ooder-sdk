# Driver 框架扩展：场景发现、可视化与协议支持

## 一、Driver 框架扩展架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        ooder-infra-driver (扩展后)                           │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  Core Driver Framework                                                │ │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐        │ │
│  │  │  Driver │ │ Driver  │ │ Driver  │ │Interface│ │  Health │        │ │
│  │  │Interface│ │Registry │ │Context  │ │Definition│ │ Monitor │        │ │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘        │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  Extension: Discovery Framework (场景发现)                             │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │ │
│  │  │  Discovery  │ │  Discovery  │ │  Discovery  │ │  Discovery  │    │ │
│  │  │  Provider   │ │   Registry  │ │   Engine    │ │   Group     │    │ │
│  │  │  (发现提供者)│ │  (注册中心)  │ │  (发现引擎)  │ │  (发现组)   │    │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘    │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  Extension: Visualization Framework (可视化)                           │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │ │
│  │  │  Visualizer │ │  Visualizer │ │  Visualizer │ │  Visualizer │    │ │
│  │  │  Interface  │ │   Registry  │ │   Engine    │ │   Schema    │    │ │
│  │  │  (可视化接口)│ │  (注册中心)  │ │  (渲染引擎)  │ │  (可视化模式)│    │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘    │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │  Extension: Protocol Framework (协议支持)                              │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │ │
│  │  │  Protocol   │ │  Protocol   │ │  Protocol   │ │  Protocol   │    │ │
│  │  │  Interface  │ │   Registry  │ │   Engine    │ │   Adapter   │    │ │
│  │  │  (协议接口)  │ │  (注册中心)  │ │  (协议引擎)  │ │  (协议适配器)│    │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘    │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、场景自主发现组支持

### 2.1 发现组（Discovery Group）概念

**场景：** 多个发现提供者协同工作，形成发现组

```java
// ooder-infra-driver 中定义
package net.ooder.infra.driver.discovery;

/**
 * 发现组 - 管理多个 DiscoveryProvider
 */
public interface DiscoveryGroup {
    
    String getGroupName();           // 组名称
    String getGroupType();           // 组类型：personal/department/company/public
    
    // 管理发现提供者
    void addProvider(DiscoveryProvider provider);
    void removeProvider(String providerName);
    List<DiscoveryProvider> getProviders();
    
    // 执行发现（组内所有 Provider 并行执行）
    CompletableFuture<DiscoveryResult> discover(DiscoveryQuery query);
    
    // 结果聚合策略
    void setAggregationStrategy(AggregationStrategy strategy);
}

/**
 * 发现结果聚合策略
 */
public interface AggregationStrategy {
    DiscoveryResult aggregate(List<DiscoveryResult> results);
}

// 实现类：
// - UnionAggregationStrategy (并集)
// - IntersectionAggregationStrategy (交集)
// - PriorityAggregationStrategy (优先级)
// - WeightedAggregationStrategy (加权)
```

### 2.2 自主发现组实现

```java
@Component
public class SceneDiscoveryGroup implements DiscoveryGroup {
    
    private final String groupName;
    private final List<DiscoveryProvider> providers = new CopyOnWriteArrayList<>();
    private AggregationStrategy aggregationStrategy = new UnionAggregationStrategy();
    
    @Autowired
    private DriverRegistry driverRegistry;
    
    public SceneDiscoveryGroup(String groupName) {
        this.groupName = groupName;
    }
    
    @Override
    public void addProvider(DiscoveryProvider provider) {
        providers.add(provider);
        // 按优先级排序
        providers.sort(Comparator.comparingInt(DiscoveryProvider::getPriority).reversed());
    }
    
    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryQuery query) {
        // 并行执行所有 Provider 的发现
        List<CompletableFuture<DiscoveryResult>> futures = providers.stream()
            .filter(p -> p.isApplicable(query.getScope()))
            .map(p -> p.discover(query))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                List<DiscoveryResult> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
                return aggregationStrategy.aggregate(results);
            });
    }
}
```

### 2.3 与 Driver 框架集成

```java
// Driver 支持发现能力
public interface DiscoverableDriver extends Driver {
    DiscoveryGroup getDiscoveryGroup();
}

// 实现示例：场景发现 Driver
@Component
public class SceneDiscoveryDriver implements DiscoverableDriver {
    
    private DiscoveryGroup discoveryGroup;
    
    @Override
    public void initialize(DriverContext context) {
        // 创建发现组
        this.discoveryGroup = new SceneDiscoveryGroup("default-scene-group");
        
        // 添加发现提供者
        discoveryGroup.addProvider(new UdpDiscoveryProvider());
        discoveryGroup.addProvider(new MdnsDiscoveryProvider());
        discoveryGroup.addProvider(new SkillCenterDiscoveryProvider());
        discoveryGroup.addProvider(new LocalFsDiscoveryProvider());
        
        // 注册到 DriverRegistry
        driverRegistry.registerDiscoveryGroup(this);
    }
    
    @Override
    public DiscoveryGroup getDiscoveryGroup() {
        return discoveryGroup;
    }
    
    @Override
    public String getCategory() {
        return "discovery";
    }
}
```

---

## 三、可视化支持

### 3.1 可视化能力定义

```java
// ooder-infra-driver 中定义
package net.ooder.infra.driver.visualization;

/**
 * 可视化能力接口
 */
public interface VisualizableDriver extends Driver {
    VisualizationSchema getVisualizationSchema();
    VisualizationRenderer getRenderer();
}

/**
 * 可视化模式定义
 */
public class VisualizationSchema {
    private String schemaId;                    // 模式ID
    private String displayName;                 // 显示名称
    private String description;                 // 描述
    private String icon;                        // 图标
    private String category;                    // 分类：form/table/chart/dashboard
    
    // 字段定义
    private List<FieldSchema> fields;
    
    // 操作定义
    private List<ActionSchema> actions;
    
    // 布局定义
    private LayoutSchema layout;
    
    // 样式定义
    private StyleSchema style;
}

/**
 * 字段模式
 */
public class FieldSchema {
    private String fieldName;           // 字段名
    private String displayName;         // 显示名
    private String fieldType;           // 类型：string/number/date/boolean/enum/object
    private boolean required;           // 是否必填
    private boolean readonly;           // 是否只读
    private Object defaultValue;        // 默认值
    private Map<String, Object> validation;  // 校验规则
    private Map<String, Object> uiConfig;    // UI配置
}

/**
 * 操作模式
 */
public class ActionSchema {
    private String actionId;            // 操作ID
    private String displayName;         // 显示名
    private String actionType;          // 类型：create/read/update/delete/execute
    private String icon;                // 图标
    private String permission;          // 所需权限
    private List<FieldSchema> inputFields;   // 输入字段
    private List<FieldSchema> outputFields;  // 输出字段
}
```

### 3.2 可视化渲染引擎

```java
/**
 * 可视化渲染引擎
 */
public interface VisualizationRenderer {
    
    // 渲染为不同格式
    String renderAsJson(VisualizationSchema schema, Object data);
    String renderAsHtml(VisualizationSchema schema, Object data);
    String renderAsReactComponent(VisualizationSchema schema, Object data);
    
    // 支持的前端框架
    boolean supportsFramework(String framework);  // react/vue/angular
}

// 实现类：
// - FormRenderer (表单渲染)
// - TableRenderer (表格渲染)
// - ChartRenderer (图表渲染)
// - DashboardRenderer (仪表盘渲染)
```

### 3.3 与 Driver 框架集成

```java
// VFS Driver 支持可视化
@Component
public class VfsVisualizableDriver implements VisualizableDriver {
    
    @Autowired
    private VfsStoreService vfsService;
    
    @Override
    public VisualizationSchema getVisualizationSchema() {
        VisualizationSchema schema = new VisualizationSchema();
        schema.setSchemaId("vfs-manager");
        schema.setDisplayName("文件管理器");
        schema.setCategory("dashboard");
        schema.setIcon("folder-open");
        
        // 定义字段
        List<FieldSchema> fields = Arrays.asList(
            createField("fileId", "文件ID", "string", true, true),
            createField("fileName", "文件名", "string", true, false),
            createField("fileSize", "文件大小", "number", true, false),
            createField("createTime", "创建时间", "date", true, false),
            createField("fileType", "文件类型", "enum", true, false)
        );
        schema.setFields(fields);
        
        // 定义操作
        List<ActionSchema> actions = Arrays.asList(
            createAction("upload", "上传文件", "create", "upload"),
            createAction("download", "下载文件", "read", "download"),
            createAction("delete", "删除文件", "delete", "trash"),
            createAction("preview", "预览文件", "read", "eye")
        );
        schema.setActions(actions);
        
        return schema;
    }
    
    @Override
    public VisualizationRenderer getRenderer() {
        return new DashboardRenderer();
    }
    
    @Override
    public String getCategory() {
        return "vfs";
    }
}
```

---

## 四、协议支持

### 4.1 协议能力定义

```java
// ooder-infra-driver 中定义
package net.ooder.infra.driver.protocol;

/**
 * 协议能力接口
 */
public interface ProtocolDriver extends Driver {
    ProtocolHandlerRegistry getProtocolHandlerRegistry();
    ProtocolEngine getProtocolEngine();
}

/**
 * 协议处理器注册表
 */
public interface ProtocolHandlerRegistry {
    
    // 注册协议处理器
    void registerHandler(ProtocolHandler handler);
    void unregisterHandler(String protocolType);
    
    // 获取处理器
    ProtocolHandler getHandler(String protocolType);
    List<ProtocolHandler> getAllHandlers();
    
    // 支持的协议类型
    List<String> getSupportedProtocols();
}

/**
 * 协议处理器
 */
public interface ProtocolHandler {
    
    String getProtocolType();           // 协议类型：http/https/ws/tcp/udp/custom
    String getVersion();                // 协议版本
    String getDescription();            // 描述
    
    // 协议能力
    boolean supportsEncryption();       // 是否支持加密
    boolean supportsCompression();      // 是否支持压缩
    boolean supportsMultiplexing();     // 是否支持多路复用
    
    // 处理请求
    ProtocolResponse handle(ProtocolRequest request);
    
    // 协议适配
    boolean canHandle(ProtocolRequest request);
}

/**
 * 协议请求/响应
 */
public class ProtocolRequest {
    private String protocolType;
    private String endpoint;
    private String method;
    private Map<String, String> headers;
    private Object payload;
    private Map<String, Object> metadata;
}

public class ProtocolResponse {
    private int statusCode;
    private Map<String, String> headers;
    private Object payload;
    private String errorMessage;
}
```

### 4.2 协议引擎

```java
/**
 * 协议引擎
 */
public interface ProtocolEngine {
    
    // 执行协议请求
    CompletableFuture<ProtocolResponse> execute(ProtocolRequest request);
    
    // 协议转换
    ProtocolResponse convert(ProtocolRequest request, String targetProtocol);
    
    // 协议协商
    String negotiateProtocol(List<String> supportedProtocols);
}

// 实现类：
// - DefaultProtocolEngine (默认引擎)
// - MultiProtocolEngine (多协议支持)
// - AdaptiveProtocolEngine (自适应选择)
```

### 4.3 与 Driver 框架集成

```java
// Agent Driver 支持协议
@Component
public class AgentProtocolDriver implements ProtocolDriver {
    
    private ProtocolHandlerRegistry handlerRegistry;
    private ProtocolEngine protocolEngine;
    
    @Override
    public void initialize(DriverContext context) {
        this.handlerRegistry = new DefaultProtocolHandlerRegistry();
        this.protocolEngine = new DefaultProtocolEngine(handlerRegistry);
        
        // 注册协议处理器
        handlerRegistry.registerHandler(new HttpProtocolHandler());
        handlerRegistry.registerHandler(new WebSocketProtocolHandler());
        handlerRegistry.registerHandler(new TcpProtocolHandler());
        handlerRegistry.registerHandler(new UdpProtocolHandler());
        handlerRegistry.registerHandler(new A2AProtocolHandler());  // A2A 协议
        
        // 注册到 DriverRegistry
        driverRegistry.registerProtocolDriver(this);
    }
    
    @Override
    public ProtocolHandlerRegistry getProtocolHandlerRegistry() {
        return handlerRegistry;
    }
    
    @Override
    public ProtocolEngine getProtocolEngine() {
        return protocolEngine;
    }
    
    @Override
    public String getCategory() {
        return "protocol";
    }
}

// A2A 协议处理器（用于 Agent 间通信）
public class A2AProtocolHandler implements ProtocolHandler {
    
    @Override
    public String getProtocolType() {
        return "a2a";
    }
    
    @Override
    public String getVersion() {
        return "1.0";
    }
    
    @Override
    public boolean supportsEncryption() {
        return true;
    }
    
    @Override
    public ProtocolResponse handle(ProtocolRequest request) {
        // 处理 A2A 协议请求
        A2AMessage message = parseA2AMessage(request.getPayload());
        A2AResponse response = processA2AMessage(message);
        return convertToProtocolResponse(response);
    }
}
```

---

## 五、统一 CapabilityProvider 整合

### 5.1 整合后的 CapabilityProvider

```java
// ooder-infra-driver 中定义
package net.ooder.infra.driver;

/**
 * 统一的能力提供者接口
 * 整合：基础能力 + 发现能力 + 可视化能力 + 协议能力
 */
public interface CapabilityProvider {
    
    // ========== 基础能力 (原有) ==========
    String getProviderName();
    String getVersion();
    String getCategory();
    
    void initialize(DriverContext context);
    void start();
    void stop();
    void destroy();
    
    boolean isInitialized();
    boolean isRunning();
    HealthStatus getHealthStatus();
    
    Object invoke(String capabilityId, Map<String, Object> params);
    InterfaceDefinition getInterfaceDefinition();
    
    // ========== 发现能力 (新增) ==========
    default boolean supportsDiscovery() {
        return this instanceof DiscoveryCapability;
    }
    
    default DiscoveryCapability getDiscoveryCapability() {
        if (supportsDiscovery()) {
            return (DiscoveryCapability) this;
        }
        return null;
    }
    
    // ========== 可视化能力 (新增) ==========
    default boolean supportsVisualization() {
        return this instanceof VisualizationCapability;
    }
    
    default VisualizationCapability getVisualizationCapability() {
        if (supportsVisualization()) {
            return (VisualizationCapability) this;
        }
        return null;
    }
    
    // ========== 协议能力 (新增) ==========
    default boolean supportsProtocol() {
        return this instanceof ProtocolCapability;
    }
    
    default ProtocolCapability getProtocolCapability() {
        if (supportsProtocol()) {
            return (ProtocolCapability) this;
        }
        return null;
    }
}

/**
 * 发现能力接口
 */
public interface DiscoveryCapability {
    DiscoveryGroup getDiscoveryGroup();
    CompletableFuture<DiscoveryResult> discover(DiscoveryQuery query);
}

/**
 * 可视化能力接口
 */
public interface VisualizationCapability {
    VisualizationSchema getVisualizationSchema();
    VisualizationRenderer getRenderer();
    String render(Object data, String format);  // json/html/react/vue
}

/**
 * 协议能力接口
 */
public interface ProtocolCapability {
    ProtocolHandlerRegistry getProtocolHandlerRegistry();
    ProtocolEngine getProtocolEngine();
    CompletableFuture<ProtocolResponse> executeProtocol(ProtocolRequest request);
}
```

### 5.2 完整的 Driver 实现示例

```java
// VFS Driver：支持基础能力 + 可视化能力
@Component
public class VfsCapabilityProvider implements CapabilityProvider, VisualizationCapability {
    
    @Autowired
    private VfsStoreService vfsService;
    
    // ========== 基础能力 ==========
    @Override
    public String getProviderName() { return "vfs"; }
    
    @Override
    public String getCategory() { return "storage"; }
    
    @Override
    public Object invoke(String capabilityId, Map<String, Object> params) {
        switch (capabilityId) {
            case "storeFile":
                return vfsService.storeFile(...);
            case "readFile":
                return vfsService.readFile(...);
            // ...
        }
    }
    
    // ========== 可视化能力 ==========
    @Override
    public VisualizationSchema getVisualizationSchema() {
        // 返回文件管理器的可视化模式
        return buildVfsVisualizationSchema();
    }
    
    @Override
    public VisualizationRenderer getRenderer() {
        return new DashboardRenderer();
    }
    
    @Override
    public String render(Object data, String format) {
        return getRenderer().render(getVisualizationSchema(), data, format);
    }
}

// Agent Driver：支持基础能力 + 发现能力 + 协议能力
@Component
public class AgentCapabilityProvider implements CapabilityProvider, 
                                                  DiscoveryCapability, 
                                                  ProtocolCapability {
    
    @Autowired
    private CapRegistry capRegistry;
    
    private DiscoveryGroup discoveryGroup;
    private ProtocolHandlerRegistry protocolRegistry;
    
    // ========== 基础能力 ==========
    @Override
    public String getProviderName() { return "agent"; }
    
    @Override
    public String getCategory() { return "agent"; }
    
    @Override
    public void initialize(DriverContext context) {
        // 初始化发现能力
        this.discoveryGroup = new SceneDiscoveryGroup("agent-discovery");
        discoveryGroup.addProvider(new UdpDiscoveryProvider());
        discoveryGroup.addProvider(new MdnsDiscoveryProvider());
        
        // 初始化协议能力
        this.protocolRegistry = new DefaultProtocolHandlerRegistry();
        protocolRegistry.registerHandler(new A2AProtocolHandler());
        protocolRegistry.registerHandler(new HttpProtocolHandler());
    }
    
    @Override
    public Object invoke(String capabilityId, Map<String, Object> params) {
        Capability cap = capRegistry.findById(capabilityId);
        return cap.invoke(params);
    }
    
    // ========== 发现能力 ==========
    @Override
    public DiscoveryGroup getDiscoveryGroup() {
        return discoveryGroup;
    }
    
    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryQuery query) {
        return discoveryGroup.discover(query);
    }
    
    // ========== 协议能力 ==========
    @Override
    public ProtocolHandlerRegistry getProtocolHandlerRegistry() {
        return protocolRegistry;
    }
    
    @Override
    public ProtocolEngine getProtocolEngine() {
        return new DefaultProtocolEngine(protocolRegistry);
    }
    
    @Override
    public CompletableFuture<ProtocolResponse> executeProtocol(ProtocolRequest request) {
        return getProtocolEngine().execute(request);
    }
}
```

---

## 六、scene-engine 使用示例

### 6.1 场景发现

```java
@Service
public class SceneDiscoveryService {
    
    @Autowired
    private DriverRegistry driverRegistry;
    
    public CompletableFuture<List<Scene>> discoverScenes(DiscoveryScope scope) {
        // 获取所有支持发现的 Driver
        List<CapabilityProvider> discoveryProviders = driverRegistry
            .getProvidersByCapability(DiscoveryCapability.class);
        
        // 并行执行发现
        List<CompletableFuture<DiscoveryResult>> futures = discoveryProviders.stream()
            .map(p -> p.getDiscoveryCapability().discover(
                DiscoveryQuery.builder()
                    .type(DiscoveryType.SCENE)
                    .scope(scope)
                    .build()
            ))
            .collect(Collectors.toList());
        
        // 聚合结果
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .flatMap(r -> r.getScenes().stream())
                .collect(Collectors.toList()));
    }
}
```

### 6.2 可视化渲染

```java
@Service
public class SceneVisualizationService {
    
    @Autowired
    private DriverRegistry driverRegistry;
    
    public String renderSceneManager(String format) {
        // 获取 VFS Provider 的可视化能力
        CapabilityProvider vfsProvider = driverRegistry.getProvider("vfs");
        
        if (vfsProvider.supportsVisualization()) {
            VisualizationCapability vizCap = vfsProvider.getVisualizationCapability();
            
            // 获取数据
            Object data = vfsProvider.invoke("listFiles", Collections.emptyMap());
            
            // 渲染
            return vizCap.render(data, format);  // json/html/react
        }
        
        return null;
    }
}
```

### 6.3 协议执行

```java
@Service
public class SceneProtocolService {
    
    @Autowired
    private DriverRegistry driverRegistry;
    
    public CompletableFuture<ProtocolResponse> sendA2AMessage(String targetAgentId, 
                                                               Object message) {
        // 获取 Agent Provider 的协议能力
        CapabilityProvider agentProvider = driverRegistry.getProvider("agent");
        
        if (agentProvider.supportsProtocol()) {
            ProtocolCapability protocolCap = agentProvider.getProtocolCapability();
            
            // 构建 A2A 请求
            ProtocolRequest request = ProtocolRequest.builder()
                .protocolType("a2a")
                .endpoint("agent://" + targetAgentId)
                .method("sendMessage")
                .payload(message)
                .build();
            
            // 执行协议
            return protocolCap.executeProtocol(request);
        }
        
        return CompletableFuture.completedFuture(null);
    }
}
```

---

## 七、总结

**Driver 框架扩展能力：**

| 能力 | 接口 | 使用场景 |
|-----|------|---------|
| **基础能力** | `CapabilityProvider` | 所有 Driver 必须实现 |
| **发现能力** | `DiscoveryCapability` | 场景发现、服务发现、节点发现 |
| **可视化能力** | `VisualizationCapability` | 表单、表格、图表、仪表盘 |
| **协议能力** | `ProtocolCapability` | HTTP、WebSocket、TCP、UDP、A2A |

**核心设计：**
1. **统一接口**：所有能力通过 `CapabilityProvider` 暴露
2. **可选能力**：使用 default 方法，Driver 按需实现
3. **能力组合**：一个 Driver 可实现多个能力（如 Agent Driver 同时支持发现+协议）
4. **scene-engine 简化**：通过 `DriverRegistry` 统一调用，无需关心具体实现

**预期效果：**
- 场景发现：支持自主发现组，多 Provider 协同
- 可视化：自动生成表单/表格/图表，支持多前端框架
- 协议：统一协议处理，支持 A2A 等自定义协议
