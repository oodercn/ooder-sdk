# Scene Engine v2.3 API 参考文档

**版本**: v2.3  
**日期**: 2026-03-06  
**状态**: 正式发布

---

## 目录

1. [核心服务 API](#一核心服务-api)
2. [知识库管理 API](#二知识库管理-api)
3. [RAG Pipeline API](#三rag-pipeline-api)
4. [Function Calling API](#四function-calling-api)
5. [多轮对话 API](#五多轮对话-api)
6. [权限管理 API](#六权限管理-api)
7. [场景技能分类 API](#七场景技能分类-api)
8. [安装协调 API](#八安装协调-api)

---

## 一、核心服务 API

### 1.1 KnowledgeBaseService

知识库管理核心服务接口。

```java
public interface KnowledgeBaseService {
    
    /**
     * 创建知识库
     * 
     * @param userId 用户ID
     * @param request 创建请求
     * @return 创建的知识库
     */
    KnowledgeBase create(String userId, KnowledgeBaseCreateRequest request);
    
    /**
     * 获取知识库
     * 
     * @param kbId 知识库ID
     * @return 知识库
     */
    KnowledgeBase get(String kbId);
    
    /**
     * 更新知识库
     * 
     * @param kbId 知识库ID
     * @param request 更新请求
     * @return 更新后的知识库
     */
    KnowledgeBase update(String kbId, KnowledgeBaseUpdateRequest request);
    
    /**
     * 删除知识库
     * 
     * @param kbId 知识库ID
     */
    void delete(String kbId);
    
    /**
     * 列出用户的知识库
     * 
     * @param userId 用户ID
     * @return 知识库列表
     */
    List<KnowledgeBase> listByUser(String userId);
    
    /**
     * 搜索知识库
     * 
     * @param query 搜索关键词
     * @param options 搜索选项
     * @return 搜索结果
     */
    SearchResult search(String query, SearchOptions options);
}
```

**使用示例**:
```java
@Autowired
private KnowledgeBaseService knowledgeBaseService;

// 创建知识库
KnowledgeBase kb = knowledgeBaseService.create(
    userId,
    KnowledgeBaseCreateRequest.builder()
        .name("产品文档")
        .description("产品相关文档")
        .visibility(Visibility.PRIVATE)
        .build()
);

// 搜索知识库
SearchResult result = knowledgeBaseService.search(
    "API 文档",
    SearchOptions.builder()
        .kbIds(Arrays.asList("kb-001", "kb-002"))
        .topK(10)
        .build()
);
```

---

### 1.2 DocumentService

文档管理服务接口。

```java
public interface DocumentService {
    
    /**
     * 上传文档
     * 
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param file 文件
     * @param metadata 元数据
     * @return 上传的文档
     */
    Document upload(String userId, String kbId, File file, DocumentMetadata metadata);
    
    /**
     * 添加文本知识
     * 
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param title 标题
     * @param content 内容
     * @param tags 标签
     * @return 创建的文档
     */
    Document addText(String userId, String kbId, String title, String content, List<String> tags);
    
    /**
     * 获取文档
     * 
     * @param docId 文档ID
     * @return 文档
     */
    Document get(String docId);
    
    /**
     * 删除文档
     * 
     * @param docId 文档ID
     */
    void delete(String docId);
    
    /**
     * 列出知识库文档
     * 
     * @param kbId 知识库ID
     * @param request 查询请求
     * @return 文档列表
     */
    List<Document> list(String kbId, DocumentQueryRequest request);
}
```

---

## 二、知识库管理 API

### 2.1 UserContributionService

用户知识贡献服务接口。

```java
public interface UserContributionService {
    
    /**
     * 上传文件
     * 
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param file 文件
     * @param metadata 元数据
     * @return 上传的文档
     */
    Document uploadFile(String userId, String kbId, File file, DocumentMetadata metadata);
    
    /**
     * 输入文本知识
     * 
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param title 标题
     * @param content 内容
     * @param tags 标签
     * @return 创建的文档
     */
    Document inputText(String userId, String kbId, String title, String content, List<String> tags);
    
    /**
     * 从 URL 导入
     * 
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param url URL
     * @return 导入的文档
     */
    Document importFromUrl(String userId, String kbId, String url);
    
    /**
     * 批量上传
     * 
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param files 文件列表
     * @return 批量上传结果
     */
    BatchUploadResult batchUpload(String userId, String kbId, List<File> files);
    
    /**
     * 获取用户贡献统计
     * 
     * @param userId 用户ID
     * @return 贡献统计
     */
    ContributionStats getStats(String userId);
}
```

**使用示例**:
```java
@Autowired
private UserContributionService contributionService;

// 上传文件
Document doc = contributionService.uploadFile(
    userId,
    kbId,
    new File("/path/to/document.pdf"),
    DocumentMetadata.builder()
        .type("PDF")
        .size(1024000L)
        .build()
);

// 输入文本知识
Document textDoc = contributionService.inputText(
    userId,
    kbId,
    "会议记录",
    "今天讨论了产品规划...",
    Arrays.asList("会议", "产品")
);
```

---

### 2.2 PermissionService

权限管理服务接口。

```java
public interface PermissionService {
    
    /**
     * 检查权限
     * 
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @param permission 权限
     * @return 是否有权限
     */
    boolean hasPermission(String kbId, String userId, Permission permission);
    
    /**
     * 授予权限
     * 
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @param permission 权限
     * @return 权限信息
     */
    KbPermission grantPermission(String kbId, String userId, Permission permission);
    
    /**
     * 撤销权限
     * 
     * @param kbId 知识库ID
     * @param userId 用户ID
     */
    void revokePermission(String kbId, String userId);
    
    /**
     * 获取权限
     * 
     * @param kbId 知识库ID
     * @param userId 用户ID
     * @return 权限信息
     */
    Optional<KbPermission> getPermission(String kbId, String userId);
    
    /**
     * 转移所有权
     * 
     * @param kbId 知识库ID
     * @param fromUserId 原所有者
     * @param toUserId 新所有者
     */
    void transferOwnership(String kbId, String fromUserId, String toUserId);
}
```

---

### 2.3 ShareService

知识分享服务接口。

```java
public interface ShareService {
    
    /**
     * 创建分享
     * 
     * @param kbId 知识库ID
     * @param options 分享选项
     * @return 分享结果
     */
    ShareResult createShare(String kbId, ShareOptions options);
    
    /**
     * 验证分享
     * 
     * @param shareLink 分享链接
     * @param password 密码
     * @param visitorId 访问者ID
     * @return 验证结果
     */
    ShareValidationResult validateShare(String shareLink, String password, String visitorId);
    
    /**
     * 获取分享
     * 
     * @param shareId 分享ID
     * @return 分享信息
     */
    Share getShare(String shareId);
    
    /**
     * 删除分享
     * 
     * @param shareId 分享ID
     */
    void deleteShare(String shareId);
    
    /**
     * 记录访问
     * 
     * @param shareId 分享ID
     * @param visitorId 访问者ID
     */
    void recordAccess(String shareId, String visitorId);
    
    /**
     * 获取分享统计
     * 
     * @param shareId 分享ID
     * @return 统计信息
     */
    ShareStats getStats(String shareId);
}
```

---

## 三、RAG Pipeline API

### 3.1 RagPipeline

RAG 检索增强生成 Pipeline。

```java
public class RagPipeline {
    
    /**
     * 仅检索
     * 
     * @param context 检索上下文
     * @return 检索结果
     */
    public RagResult retrieve(RagContext context);
    
    /**
     * 检索并生成
     * 
     * @param query 查询
     * @param context 上下文
     * @return 生成结果
     */
    public String retrieveAndGenerate(String query, RagContext context);
    
    /**
     * 流式生成
     * 
     * @param query 查询
     * @param context 上下文
     * @param handler 流处理回调
     */
    public void retrieveAndGenerateStream(String query, RagContext context, StreamHandler handler);
    
    /**
     * 混合检索（多知识库）
     * 
     * @param context 上下文
     * @param kbIds 知识库ID列表
     * @return 混合检索结果
     */
    public RagResult hybridRetrieve(RagContext context, List<String> kbIds);
}
```

**使用示例**:
```java
@Autowired
private RagPipeline ragPipeline;

// 检索并生成
RagContext context = RagContext.builder()
    .kbId("kb-001")
    .query("请假流程是什么？")
    .topK(5)
    .enableRerank(true)
    .build();

String answer = ragPipeline.retrieveAndGenerate(
    "请假流程是什么？",
    context
);

// 流式生成
ragPipeline.retrieveAndGenerateStream(
    "请假流程是什么？",
    context,
    new StreamHandler() {
        @Override
        public void onNext(String chunk) {
            System.out.print(chunk);
        }
        
        @Override
        public void onComplete() {
            System.out.println("\n生成完成");
        }
        
        @Override
        public void onError(Throwable error) {
            System.err.println("生成失败: " + error.getMessage());
        }
    }
);
```

---

### 3.2 EmbeddingService

嵌入服务接口。

```java
public interface EmbeddingService {
    
    /**
     * 嵌入文本
     * 
     * @param text 文本
     * @return 向量
     */
    float[] embed(String text);
    
    /**
     * 批量嵌入
     * 
     * @param texts 文本列表
     * @return 向量列表
     */
    List<float[]> embedBatch(List<String> texts);
    
    /**
     * 获取向量维度
     * 
     * @return 维度
     */
    int getDimension();
}
```

---

### 3.3 VectorStore

向量存储接口。

```java
public interface VectorStore {
    
    /**
     * 插入向量
     * 
     * @param id ID
     * @param vector 向量
     * @param metadata 元数据
     */
    void insert(String id, float[] vector, Map<String, Object> metadata);
    
    /**
     * 批量插入
     * 
     * @param vectors 向量列表
     */
    void batchInsert(List<VectorRecord> vectors);
    
    /**
     * 搜索相似向量
     * 
     * @param queryVector 查询向量
     * @param topK 返回数量
     * @return 搜索结果
     */
    List<SearchResult> search(float[] queryVector, int topK);
    
    /**
     * 删除向量
     * 
     * @param id ID
     */
    void delete(String id);
    
    /**
     * 清空存储
     */
    void clear();
}
```

---

## 四、Function Calling API

### 4.1 ToolOrchestrator

工具编排器接口。

```java
public interface ToolOrchestrator {
    
    /**
     * 执行编排计划
     * 
     * @param plan 编排计划
     * @return 执行结果
     */
    OrchestrationResult execute(OrchestrationPlan plan);
    
    /**
     * 顺序执行
     * 
     * @param tools 工具列表
     * @param context 上下文
     * @return 执行结果
     */
    OrchestrationResult executeSequential(List<Tool> tools, ExecutionContext context);
    
    /**
     * 并行执行
     * 
     * @param tools 工具列表
     * @param context 上下文
     * @return 执行结果
     */
    OrchestrationResult executeParallel(List<Tool> tools, ExecutionContext context);
    
    /**
     * 条件执行
     * 
     * @param condition 条件
     * @param trueTools 条件为真时执行的工具
     * @param falseTools 条件为假时执行的工具
     * @param context 上下文
     * @return 执行结果
     */
    OrchestrationResult executeConditional(
        Condition condition,
        List<Tool> trueTools,
        List<Tool> falseTools,
        ExecutionContext context
    );
    
    /**
     * 管道执行
     * 
     * @param tools 工具列表
     * @param context 上下文
     * @return 执行结果
     */
    OrchestrationResult executePipeline(List<Tool> tools, ExecutionContext context);
}
```

**使用示例**:
```java
@Autowired
private ToolOrchestrator toolOrchestrator;

// 创建编排计划
OrchestrationPlan plan = new OrchestrationPlan();
plan.setStrategy(ExecutionStrategy.SEQUENTIAL);
plan.setTools(Arrays.asList(
    Tool.builder()
        .name("searchKnowledge")
        .params(Map.of("query", "请假流程"))
        .build(),
    Tool.builder()
        .name("generateAnswer")
        .params(Map.of("template", "standard"))
        .build()
));

// 执行
OrchestrationResult result = toolOrchestrator.execute(plan);
```

---

### 4.2 ToolRegistry

工具注册表接口。

```java
public interface ToolRegistry {
    
    /**
     * 注册工具
     * 
     * @param tool 工具
     */
    void register(ToolDefinition tool);
    
    /**
     * 注销工具
     * 
     * @param toolName 工具名称
     */
    void unregister(String toolName);
    
    /**
     * 获取工具
     * 
     * @param toolName 工具名称
     * @return 工具定义
     */
    Optional<ToolDefinition> getTool(String toolName);
    
    /**
     * 列出所有工具
     * 
     * @return 工具列表
     */
    List<ToolDefinition> listTools();
    
    /**
     * 执行工具
     * 
     * @param toolName 工具名称
     * @param params 参数
     * @return 执行结果
     */
    ToolResult execute(String toolName, Map<String, Object> params);
}
```

---

## 五、多轮对话 API

### 5.1 ConversationService

多轮对话服务接口。

```java
public interface ConversationService {
    
    /**
     * 创建对话
     * 
     * @param userId 用户ID
     * @param options 选项
     * @return 对话
     */
    Conversation createConversation(String userId, ConversationOptions options);
    
    /**
     * 获取对话
     * 
     * @param conversationId 对话ID
     * @return 对话
     */
    Conversation getConversation(String conversationId);
    
    /**
     * 删除对话
     * 
     * @param conversationId 对话ID
     */
    void deleteConversation(String conversationId);
    
    /**
     * 列出用户对话
     * 
     * @param userId 用户ID
     * @return 对话列表
     */
    List<Conversation> listConversations(String userId);
    
    /**
     * 发送消息
     * 
     * @param conversationId 对话ID
     * @param message 消息
     * @param options 选项
     * @return 对话结果
     */
    ConversationResult sendMessage(
        String conversationId,
        String message,
        ConversationOptions options
    );
    
    /**
     * 流式发送消息
     * 
     * @param conversationId 对话ID
     * @param message 消息
     * @param options 选项
     * @param handler 流处理回调
     */
    void sendMessageStream(
        String conversationId,
        String message,
        ConversationOptions options,
        StreamHandler handler
    );
    
    /**
     * 获取历史记录
     * 
     * @param conversationId 对话ID
     * @param limit 限制数量
     * @return 消息列表
     */
    List<Message> getHistory(String conversationId, int limit);
    
    /**
     * 清空历史
     * 
     * @param conversationId 对话ID
     */
    void clearHistory(String conversationId);
}
```

**使用示例**:
```java
@Autowired
private ConversationService conversationService;

// 创建对话
Conversation conversation = conversationService.createConversation(
    userId,
    ConversationOptions.builder()
        .kbIds(Arrays.asList("kb-001"))
        .enableFunctionCalling(true)
        .build()
);

// 发送消息
ConversationResult result = conversationService.sendMessage(
    conversation.getId(),
    "请假流程是什么？",
    ConversationOptions.builder()
        .enableRag(true)
        .build()
);

// 获取历史
List<Message> history = conversationService.getHistory(conversation.getId(), 10);
```

---

## 六、权限管理 API

### 6.1 权限枚举

```java
public enum Permission {
    READ,      // 读取权限
    WRITE,     // 写入权限
    DELETE,    // 删除权限
    MANAGE,    // 管理权限
    SHARE      // 分享权限
}
```

### 6.2 可见性枚举

```java
public enum Visibility {
    PRIVATE,    // 私有
    DEPARTMENT, // 部门可见
    PUBLIC      // 公开
}
```

---

## 七、场景技能分类 API

### 7.1 SceneSkillClassifier

场景技能分类器接口。

```java
public interface SceneSkillClassifier {
    
    /**
     * 检测场景分类
     * 
     * @param skillPackage 技能包
     * @return 分类结果
     */
    SceneSkillClassificationResult detectCategory(SkillPackage skillPackage);
    
    /**
     * 获取分类详情
     * 
     * @param category 分类
     * @return 分类详情
     */
    CategoryDetails getCategoryDetails(SceneSkillCategory category);
    
    /**
     * 列出所有分类
     * 
     * @return 分类列表
     */
    List<SceneSkillCategory> listCategories();
}
```

### 7.2 场景分类枚举

**v2.3.1 修订**：根据自驱能力和业务语义评分进行分类

```java
public enum SceneSkillCategory {
    ABS,           // Auto Business Scene - 自驱业务场景
    ASS,           // Auto System Scene - 自驱系统场景
    TBS,           // Trigger Business Scene - 触发业务场景
    PENDING,       // 待定（已废弃，保留用于兼容）
    INVALID,       // 无效分类（已废弃，保留用于兼容）
    NOT_SCENE_SKILL  // 普通技能（非场景技能）
}
```

**分类判定规则**：

| 分类 | 代码 | 条件 | 说明 |
|------|------|------|------|
| 自驱业务场景 | ABS | hasSelfDrive=true + score>=8 | 自动驱动，高业务语义 |
| 自驱系统场景 | ASS | hasSelfDrive=true + score<8 | 自动驱动，业务语义不足 |
| 触发业务场景 | TBS | hasSelfDrive=false + score>=8 | 外部触发，高业务语义 |
| 普通技能 | NOT_SCENE_SKILL | 不满足基本标准 或 无自驱能力且评分<8 | 非场景技能 |

**已废弃分类**（保留用于向后兼容）：

| 分类 | 代码 | 说明 |
|------|------|------|
| 待定 | PENDING | 已废弃，统一使用 NOT_SCENE_SKILL |
| 无效分类 | INVALID | 已废弃，统一使用 NOT_SCENE_SKILL |

**自驱能力判定**：必须同时满足 `mainFirst=true`、`mainFirstConfig` 非空、`driverConditions` 非空

**使用示例**:
```java
@Autowired
private SceneSkillClassifier classifier;

// 检测分类
SceneSkillClassificationResult result = classifier.detectCategory(skillPackage);
System.out.println("分类: " + result.getCategory());
System.out.println("业务语义评分: " + result.getBusinessSemanticsScore());
System.out.println("是否场景技能: " + result.isSceneSkill());

// 检查分类属性
if (result.getCategory().hasSelfDrive()) {
    System.out.println("有自驱能力");
}

if (result.getCategory().hasBusinessSemantics()) {
    System.out.println("有业务语义");
}
```

---

## 八、安装协调 API

### 8.1 InstallCoordinator

安装协调器接口。

```java
public class InstallCoordinator {
    
    /**
     * 安装技能
     * 
     * @param skillId 技能ID
     * @param options 安装选项
     * @return 安装结果
     */
    public CompletableFuture<InstallResult> install(String skillId, InstallOptions options);
    
    /**
     * 暂停安装
     * 
     * @param installId 安装ID
     */
    public void pause(String installId);
    
    /**
     * 恢复安装
     * 
     * @param installId 安装ID
     */
    public void resume(String installId);
    
    /**
     * 取消安装
     * 
     * @param installId 安装ID
     */
    public void cancel(String installId);
    
    /**
     * 重试安装
     * 
     * @param installId 安装ID
     * @return 安装结果
     */
    public CompletableFuture<InstallResult> retry(String installId);
    
    /**
     * 获取安装进度
     * 
     * @param installId 安装ID
     * @return 进度（0-100）
     */
    public int getProgress(String installId);
    
    /**
     * 获取安装状态
     * 
     * @param installId 安装ID
     * @return 安装状态
     */
    public InstallState getState(String installId);
    
    /**
     * 获取安装报告
     * 
     * @param installId 安装ID
     * @return 安装报告
     */
    public InstallReport getReport(String installId);
}
```

**使用示例**:
```java
@Autowired
private InstallCoordinator installCoordinator;

// 安装技能
CompletableFuture<InstallCoordinator.InstallResult> future = 
    installCoordinator.install(skillId, installOptions);

// 等待安装完成
InstallCoordinator.InstallResult result = future.get();
if (result.isSuccess()) {
    System.out.println("安装成功: " + result.getInstallPath());
} else {
    System.err.println("安装失败: " + result.getErrorMessage());
}

// 获取安装进度
int progress = installCoordinator.getProgress(installId);
System.out.println("安装进度: " + progress + "%");
```

---

## 九、决策引擎 API

### 9.1 接口说明

**接口**: `DecisionEngine`

**位置**: `net.ooder.scene.decision.DecisionEngine`

**功能**:
- 智能决策（在线/离线切换）
- 意图识别
- 参数提取
- 降级策略

### 9.2 决策模式

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| `ONLINE_ONLY` | 仅在线决策 | LLM 必须可用 |
| `OFFLINE_ONLY` | 仅离线决策 | 不依赖 LLM |
| `ONLINE_FIRST` | 优先在线，降级离线 | 默认模式，高可用 |

### 9.3 使用示例

```java
// 创建决策引擎
DecisionEngine engine = new DecisionEngineImpl(llmProvider, ruleEngine);

// 设置决策模式
engine.setMode(DecisionMode.ONLINE_FIRST);

// 执行决策
DecisionContext context = new DecisionContext();
context.setQuery("帮我筛选今天的简历");
context.setUserId("user-001");
context.setSceneId("recruitment");

DecisionResult result = engine.decide(context);

// 获取决策结果
if (result.isSuccess()) {
    String capability = result.getCapability();
    Map<String, Object> params = result.getParams();
    boolean fromLlm = result.isFromLlm();  // 是否来自 LLM
    
    System.out.println("调用能力: " + capability);
    System.out.println("参数: " + params);
    System.out.println("决策方式: " + (fromLlm ? "LLM" : "规则引擎"));
}
```

### 9.4 决策上下文

```java
DecisionContext context = new DecisionContext();
context.setQuery("用户查询内容");
context.setUserId("用户ID");
context.setSceneId("场景ID");
context.setHistory(conversationHistory);  // 对话历史
context.setMetadata(additionalMetadata);  // 额外元数据
```

### 9.5 决策结果

```java
public class DecisionResult {
    private boolean success;           // 是否成功
    private String capability;         // 目标能力
    private Map<String, Object> params; // 提取的参数
    private boolean fromLlm;           // 是否来自 LLM
    private float confidence;          // 置信度
    private String errorMessage;       // 错误信息
}
```

### 9.6 规则引擎配置

离线规则通过 YAML 配置：

```yaml
rules:
  - name: resume_screening
    patterns:
      - "筛选简历"
      - "过滤简历"
      - "简历.*条件"
    capability: resume_screening
    params:
      dateRange: "today"
      
  - name: interview_schedule
    patterns:
      - "安排面试"
      - "预约面试"
      - "面试时间"
    capability: interview_schedule
```

---

## 十、知识库能力 API

### 10.1 接口说明

**接口**: `KnowledgeCapability`

**位置**: `net.ooder.scene.skill.knowledge.KnowledgeCapability`

**功能**:
- 知识检索（单层/跨层）
- 知识库层级管理
- 权限检查

### 10.2 知识库层级

| 层级 | 枚举值 | 范围 | 说明 |
|------|--------|------|------|
| 通用知识层 | `GENERAL` | 全局共享 | 公司制度、流程规范 |
| 专业模块层 | `PROFESSIONAL` | 领域共享 | HR模块、财务模块 |
| 场景知识层 | `SCENE` | 场景私有 | 招聘场景、培训场景 |

### 10.3 检索策略

| 策略 | 枚举值 | 说明 |
|------|--------|------|
| 单层检索 | `SINGLE_LAYER` | 仅检索指定层 |
| 向下扩展 | `EXPAND_DOWN` | 从场景层向下扩展 |
| 并行检索 | `PARALLEL` | 同时检索多层 |

### 10.4 使用示例

```java
// 获取知识库能力
KnowledgeCapability knowledgeCap = sceneAgent.getCapability("knowledge", KnowledgeCapability.class);

// 创建检索请求
KnowledgeSearchRequest request = new KnowledgeSearchRequest();
request.setKbId("kb-recruitment-001");
request.setQuery("Java开发岗位要求");
request.setLayer(KnowledgeLayer.SCENE);
request.setStrategy(SearchStrategy.EXPAND_DOWN);
request.setTopK(5);
request.setThreshold(0.7f);

// 执行检索
KnowledgeSearchResult result = knowledgeCap.search(request);

// 处理结果
for (KnowledgeItem item : result.getItems()) {
    System.out.println("层级: " + item.getLayer());
    System.out.println("来源: " + item.getSource());
    System.out.println("内容: " + item.getContent());
    System.out.println("相似度: " + item.getScore());
}
```

### 10.5 检索请求参数

```java
public class KnowledgeSearchRequest {
    private String kbId;                    // 知识库ID（必填）
    private String query;                   // 查询内容（必填）
    private KnowledgeLayer layer;           // 知识层级（默认SCENE）
    private SearchStrategy strategy;        // 检索策略（默认EXPAND_DOWN）
    private int topK = 5;                   // 返回数量
    private float threshold = 0.7f;         // 相似度阈值
    private boolean includeGeneral = true;  // 是否包含通用知识
    private List<String> domainFilters;     // 领域过滤
    private Map<String, Object> metadata;   // 元数据过滤
}
```

### 10.6 检索结果

```java
public class KnowledgeSearchResult {
    private String query;                   // 原始查询
    private int totalCount;                 // 总结果数
    private List<KnowledgeItem> items;      // 结果列表
    private Map<KnowledgeLayer, Integer> layerCounts;  // 各层结果数
    private long searchTime;                // 检索耗时
}

public class KnowledgeItem {
    private String docId;                   // 文档ID
    private String chunkId;                 // 分块ID
    private KnowledgeLayer layer;           // 知识层级
    private String source;                  // 来源知识库
    private String content;                 // 内容
    private float score;                    // 相似度分数
    private Map<String, Object> metadata;   // 元数据
}
```

### 10.7 权限检查

```java
// 检查用户是否有权限访问知识库
boolean hasAccess = knowledgeCap.checkAccess("user-001", "kb-recruitment-001", AccessType.READ);

// 获取用户可访问的知识库列表
List<KnowledgeBaseInfo> accessibleKbs = knowledgeCap.listAccessible("user-001", KnowledgeLayer.SCENE);
```

---

## 十一、错误码参考

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 1001 | 知识库不存在 | 检查知识库ID是否正确 |
| 1002 | 文档不存在 | 检查文档ID是否正确 |
| 1003 | 权限不足 | 检查用户权限或申请授权 |
| 1004 | 知识库已满 | 清理旧文档或扩容 |
| 2001 | 向量存储错误 | 检查向量存储服务状态 |
| 2002 | 嵌入服务错误 | 检查嵌入服务配置 |
| 3001 | LLM服务错误 | 检查LLM服务配置和配额 |
| 4001 | 工具执行失败 | 检查工具参数和依赖 |
| 5001 | 安装失败 | 检查依赖和日志 |

---

## 十二、版本历史

| 版本 | 日期 | 修改内容 |
|------|------|----------|
| v2.3.1 | 2026-03-07 | 场景技能分类体系修订；新增决策引擎 API；知识库能力 API；LLM 集成设计 |
| v2.3 | 2026-03-06 | 初始版本，包含所有核心API |

---

**相关文档**:
- [SECONDARY_DEVELOPMENT_GUIDE.md](./SECONDARY_DEVELOPMENT_GUIDE.md) - 二次开发指南
- [TECH_STORY_LLM_INTEGRATION.md](./TECH_STORY_LLM_INTEGRATION.md) - LLM与场景技能集成技术故事
- [ARCHITECTURE_DIAGRAM.md](./ARCHITECTURE_DIAGRAM.md) - 架构图
- [SKILL_DEVELOPMENT_TASKS.md](./SKILL_DEVELOPMENT_TASKS.md) - Skill开发任务
