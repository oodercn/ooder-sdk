# Scene Engine v2.3.1 二次开发指南

**版本**: v2.3.1  
**日期**: 2026-03-07  
**状态**: 正式发布

---

## 一、概述

### 1.1 文档目的

本文档面向应用开发团队，提供 Scene Engine v2.3 的二次开发指南，包括：

- API 接口说明
- 使用示例
- 最佳实践
- 集成指南

### 1.2 模块架构

```
scene-engine v2.3
├── knowledge/          # 知识库管理
├── vector/             # 向量存储
├── rag/                # RAG Pipeline
├── classification/     # 场景技能分类
├── contribution/       # 用户知识贡献
├── permission/         # 权限管理
├── share/              # 知识分享
├── importer/           # 批量导入
├── tool/               # Function Calling
│   └── builtin/        # 内置工具
├── conversation/       # 多轮对话
├── coordinator/        # 安装协调器
├── ui/                 # UI 管理
├── core/               # 核心模块
│   └── decision/       # 决策引擎
│       ├── engine/     # MVEL 规则引擎
│       └── generator/  # LLM 规则生成器
└── llm/                # LLM Provider 增强
```

### 1.3 架构分层

```
┌─────────────────────────────────────────────────────────────────┐
│                    表现层 (Presentation)                         │
│                    Controller / DTO / REST API                   │
│                    ← 应用团队实现                                │
├─────────────────────────────────────────────────────────────────┤
│                    应用层 (Application)                          │
│                    Service / Coordinator / Facade               │
│                    ← 应用团队实现                                │
├─────────────────────────────────────────────────────────────────┤
│                    知识增强层 (Knowledge)                        │
│                    KnowledgeBase / VectorStore / RAG            │
│                    ← scene-engine 提供                           │
├─────────────────────────────────────────────────────────────────┤
│                    模型服务层 (Model)                            │
│                    LLM / Embedding / Function Calling           │
│                    ← llm-sdk 提供                                │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、知识库管理 API

### 2.1 接口说明

**接口**: `KnowledgeBaseService`

**位置**: `net.ooder.scene.skill.knowledge.KnowledgeBaseService`

**功能**:
- 知识库 CRUD 操作
- 文档管理
- 索引管理
- 权限管理

### 2.2 核心方法

```java
public interface KnowledgeBaseService {
    
    // ========== 知识库管理 ==========
    
    KnowledgeBase create(KnowledgeBaseCreateRequest request);
    KnowledgeBase get(String kbId);
    KnowledgeBase update(String kbId, KnowledgeBaseUpdateRequest request);
    void delete(String kbId);
    List<KnowledgeBase> listByOwner(String ownerId);
    List<KnowledgeBase> listPublic();
    boolean exists(String kbId);
    
    // ========== 文档管理 ==========
    
    Document addDocument(String kbId, DocumentCreateRequest request);
    List<Document> addDocuments(String kbId, List<DocumentCreateRequest> requests);
    Document getDocument(String kbId, String docId);
    void deleteDocument(String kbId, String docId);
    List<Document> listDocuments(String kbId);
    List<KnowledgeSearchResult> search(KnowledgeSearchRequest request);
    
    // ========== 索引管理 ==========
    
    void rebuildIndex(String kbId);
    IndexStatus getIndexStatus(String kbId);
    
    // ========== 权限管理 ==========
    
    boolean hasPermission(String kbId, String userId, String permission);
    void grantPermission(String kbId, String userId, String permission);
    void revokePermission(String kbId, String userId);
}
```

### 2.3 使用示例

#### 2.3.1 创建知识库

```java
// 创建知识库服务
KnowledgeBaseService kbService = new KnowledgeBaseServiceImpl(
    chunker, embeddingService, vectorStore
);

// 创建知识库
KnowledgeBaseCreateRequest request = KnowledgeBaseCreateRequest.builder()
    .name("产品知识库")
    .description("产品相关文档和知识")
    .ownerId("user-001")
    .visibility(KnowledgeBase.VISIBILITY_TEAM)
    .embeddingModel("text-embedding-ada-002")
    .chunkSize(500)
    .chunkOverlap(50)
    .tags(Arrays.asList("产品", "文档"))
    .build();

KnowledgeBase kb = kbService.create(request);
System.out.println("知识库ID: " + kb.getKbId());
```

#### 2.3.2 添加文档

```java
// 添加文本知识
DocumentCreateRequest docRequest = DocumentCreateRequest.builder()
    .title("产品A技术规格")
    .content("产品A是一款高性能设备，主要特点包括...")
    .source(Document.SOURCE_TEXT)
    .tags(Arrays.asList("技术规格", "产品A"))
    .build();

Document doc = kbService.addDocument(kb.getKbId(), docRequest);
System.out.println("文档ID: " + doc.getDocId());
```

#### 2.3.3 搜索知识

```java
// 搜索知识
KnowledgeSearchRequest searchRequest = KnowledgeSearchRequest.builder()
    .kbId(kb.getKbId())
    .query("产品A的技术特点")
    .topK(5)
    .threshold(0.7f)
    .build();

List<KnowledgeSearchResult> results = kbService.search(searchRequest);

for (KnowledgeSearchResult result : results) {
    System.out.println("标题: " + result.getTitle());
    System.out.println("内容: " + result.getContent());
    System.out.println("相似度: " + result.getScore());
}
```

---

## 三、向量存储 API

### 3.1 接口说明

**接口**: `VectorStore`

**位置**: `net.ooder.scene.skill.vector.VectorStore`

**功能**:
- 向量插入和批量插入
- 相似度搜索
- 向量删除

### 3.2 核心方法

```java
public interface VectorStore {
    
    void insert(String id, float[] vector, Map<String, Object> metadata);
    void batchInsert(List<VectorData> vectors);
    List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters);
    void delete(String id);
    void deleteByMetadata(Map<String, Object> filters);
    void deleteByMetadata(String key, Object value);
    int getDimension();
    long count();
    void clear();
}
```

### 3.3 使用示例

#### 3.3.1 创建向量存储

```java
// 创建内存向量存储（开发测试用）
VectorStore vectorStore = new InMemoryVectorStore(1536);

// 或使用 Milvus（生产环境）
// VectorStore vectorStore = new MilvusVectorStore(milvusConfig);
```

#### 3.3.2 插入向量

```java
// 插入单个向量
Map<String, Object> metadata = new HashMap<>();
metadata.put("docId", "doc-001");
metadata.put("kbId", "kb-001");

vectorStore.insert("vec-001", embedding, metadata);

// 批量插入
List<VectorData> vectors = new ArrayList<>();
vectors.add(new VectorData("vec-002", embedding2, metadata2));
vectors.add(new VectorData("vec-003", embedding3, metadata3));

vectorStore.batchInsert(vectors);
```

#### 3.3.3 搜索向量

```java
// 搜索相似向量
Map<String, Object> filters = new HashMap<>();
filters.put("kbId", "kb-001");

List<SearchResult> results = vectorStore.search(
    queryVector,
    10,     // topK
    filters
);

for (SearchResult result : results) {
    System.out.println("ID: " + result.getId());
    System.out.println("相似度: " + result.getScore());
}
```

---

## 四、RAG Pipeline API

### 4.1 接口说明

**接口**: `RagApi`

**位置**: `net.ooder.scene.skill.rag.RagApi`

**功能**:
- 检索相关知识
- 增强提示
- 生成回答
- 混合检索

### 4.2 核心方法

```java
public interface RagApi {
    
    RagResult retrieve(RagContext context);
    String augmentPrompt(String query, RagResult result);
    String generate(String query, RagContext context);
    RagResult hybridRetrieve(RagContext context, List<String> kbIds);
}
```

### 4.3 使用示例

#### 4.3.1 创建 RAG Pipeline

```java
// 创建 LLM 生成器
LlmGenerator generator = new LlmGeneratorImpl(llmService);

// 创建 RAG Pipeline
RagApi ragPipeline = new RagPipeline(
    kbService,
    embeddingService,
    vectorStore,
    generator
);
```

#### 4.3.2 智能问答

```java
// 创建 RAG 上下文
RagContext context = RagContext.builder()
    .query("产品A有哪些技术优势？")
    .kbId("kb-001")
    .topK(5)
    .threshold(0.7f)
    .build();

// 生成回答
String answer = ragPipeline.generate(context.getQuery(), context);
System.out.println("回答: " + answer);
```

#### 4.3.3 多知识库检索

```java
// 从多个知识库检索
List<String> kbIds = Arrays.asList("kb-001", "kb-002", "kb-003");

RagResult result = ragPipeline.hybridRetrieve(context, kbIds);

for (RagResult.RetrievedChunk chunk : result.getChunks()) {
    System.out.println("来源: " + chunk.getDocTitle());
    System.out.println("内容: " + chunk.getContent());
    System.out.println("相似度: " + chunk.getScore());
}
```

---

## 五、嵌入服务 API

### 5.1 接口说明

**接口**: `EmbeddingService`

**位置**: `net.ooder.scene.skill.vector.EmbeddingService`

**功能**:
- 文本向量化
- 批量向量化
- 相似度计算

### 5.2 核心方法

```java
public interface EmbeddingService {
    
    float[] embed(String text);
    List<float[]> embedBatch(List<String> texts);
    int getDimension();
    String getModel();
    float cosineSimilarity(float[] vector1, float[] vector2);
    float euclideanDistance(float[] vector1, float[] vector2);
}
```

### 5.3 使用示例

#### 5.3.1 创建嵌入服务

```java
// 使用 LLM SDK 的嵌入能力
LlmService llmService = ...; // 从 llm-sdk 获取

EmbeddingService embeddingService = new LlmEmbeddingServiceAdapter(
    llmService,
    "text-embedding-ada-002"
);
```

#### 5.3.2 向量化文本

```java
// 单文本向量化
float[] vector = embeddingService.embed("这是一段测试文本");

// 批量向量化
List<String> texts = Arrays.asList("文本1", "文本2", "文本3");
List<float[]> vectors = embeddingService.embedBatch(texts);
```

#### 5.3.3 计算相似度

```java
float[] vector1 = embeddingService.embed("文本1");
float[] vector2 = embeddingService.embed("文本2");

float similarity = embeddingService.cosineSimilarity(vector1, vector2);
System.out.println("相似度: " + similarity);
```

---

## 六、场景技能分类 API

### 6.1 接口说明

**接口**: `SceneSkillClassifier`

**位置**: `net.ooder.scene.skill.classification.SceneSkillClassifier`

**功能**:
- 场景技能分类检测
- 业务语义评分

### 6.2 分类类型

**v2.3.1 修订**：根据自驱能力和业务语义评分进行分类

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

### 6.3 自驱能力判定

必须**同时满足**以下三个条件：

1. `mainFirst = true`
2. `mainFirstConfig` 存在且非空
3. `driverConditions` 非空

### 6.4 业务语义评分

满分10分，评分项如下：

| 评分项 | 分值 | 字段 |
|--------|------|------|
| 驱动条件非空 | 3分 | `driverConditions` |
| 参与者非空 | 3分 | `participants` |
| 公开可见 | 2分 | `visibility = "public"` |
| 有协作能力 | 1分 | `collaboration` |
| 有业务标签 | 1分 | `businessTags`（兼容 `tags`） |

### 6.5 使用示例

#### 6.5.1 基本使用

```java
SceneSkillClassifier classifier = new SceneSkillClassifierImpl();

SceneSkillClassificationResult result = classifier.detectCategory(skillPackage);

System.out.println("分类: " + result.getCategory().getName());
System.out.println("业务语义评分: " + result.getBusinessSemanticsScore());
System.out.println("是否为场景技能: " + result.isSceneSkill());
```

#### 6.5.2 检查分类属性

```java
SceneSkillCategory category = result.getCategory();

if (category.hasSelfDrive()) {
    System.out.println("有自驱能力");
}

if (category.hasBusinessSemantics()) {
    System.out.println("有业务语义");
}

if (category.needsExternalTrigger()) {
    System.out.println("需要外部触发");
}
```

#### 6.5.3 使用兼容层

```java
import net.ooder.scene.skill.classification.MetadataCompat;

Map<String, Object> metadata = skillPackage.getMetadata();

boolean isSceneSkill = MetadataCompat.isSceneSkill(metadata);
boolean hasSelfDrive = MetadataCompat.hasSelfDriveCapability(metadata);
int score = MetadataCompat.calculateBusinessSemanticsScore(metadata);
List<String> tags = MetadataCompat.getBusinessTags(metadata);
```

### 6.6 元数据标准格式

```json
{
  "sceneSkill": true,
  "mainFirst": true,
  "mainFirstConfig": {
    "triggerType": "schedule",
    "interval": "0 0 9 * * ?"
  },
  "driverConditions": [
    {
      "type": "time",
      "expression": "cron:0 0 9 * * ?"
    }
  ],
  "participants": {
    "roles": ["approver", "submitter"],
    "users": []
  },
  "visibility": "public",
  "collaboration": {
    "type": "sequential",
    "timeout": 3600
  },
  "businessTags": ["审批流程", "工作流"],
  "sceneCapabilities": [
    {
      "capId": "40",
      "name": "消息通知",
      "type": "executor"
    }
  ]
}
```

### 6.7 字段兼容性说明

**v2.3.1 修订**：为保持向后兼容，以下字段支持兼容读取：

| 新字段 | 兼容字段 | 说明 |
|--------|----------|------|
| `sceneSkill` | `type = "scene-skill"` | 场景技能标识 |
| `businessTags` | `tags` | 业务标签 |

---

## 七、用户知识贡献 API

### 7.1 接口说明

**接口**: `UserContributionService`

**位置**: `net.ooder.scene.skill.contribution.UserContributionService`

**功能**:
- 文件上传
- 文本输入
- URL 导入
- 批量导入

### 7.2 核心方法

```java
public interface UserContributionService {
    
    Document uploadFile(String userId, String kbId, FileUploadRequest request);
    Document inputText(String userId, String kbId, TextKnowledgeRequest request);
    Document importFromUrl(String userId, String kbId, UrlImportRequest request);
    BatchImportResult batchUpload(String userId, String kbId, List<FileUploadRequest> requests);
    ContributionStats getStats(String userId);
}
```

### 7.3 使用示例

#### 7.3.1 上传文件

```java
UserContributionService contributionService = new UserContributionServiceImpl(kbService);

FileUploadRequest request = new FileUploadRequest();
request.setFileName("产品手册.pdf");
request.setInputStream(new FileInputStream(file));
request.setFileSize(file.length());
request.setMimeType("application/pdf");
request.setTitle("产品手册");
request.setTags(Arrays.asList("产品", "手册"));

Document doc = contributionService.uploadFile("user-001", "kb-001", request);
```

#### 7.3.2 输入文本知识

```java
TextKnowledgeRequest request = new TextKnowledgeRequest();
request.setTitle("常见问题解答");
request.setContent("Q: 产品如何使用？\nA: 请参考产品手册...");
request.setTags(Arrays.asList("FAQ"));

Document doc = contributionService.inputText("user-001", "kb-001", request);
```

#### 7.3.3 从 URL 导入

```java
UrlImportRequest request = new UrlImportRequest("https://example.com/document");
request.setTitle("外部文档");
request.setTimeout(30000);

Document doc = contributionService.importFromUrl("user-001", "kb-001", request);
```

---

## 八、权限管理 API

### 8.1 接口说明

**接口**: `PermissionService`

**位置**: `net.ooder.scene.skill.permission.PermissionService`

**功能**:
- 权限检查
- 权限授予/撤销
- 权限继承
- 所有权转移

### 8.2 权限类型

| 权限 | 代码 | 级别 | 说明 |
|------|------|------|------|
| 读权限 | READ | 1 | 查看知识库内容 |
| 写权限 | WRITE | 2 | 添加/编辑文档 |
| 管理权限 | ADMIN | 3 | 管理权限和设置 |
| 所有者权限 | OWNER | 4 | 完全控制 |

### 8.3 使用示例

```java
PermissionService permService = new PermissionServiceImpl(kbService);

// 检查权限
boolean canRead = permService.hasPermission("kb-001", "user-001", Permission.READ);

// 授予权限
GrantPermissionRequest request = new GrantPermissionRequest(
    "kb-001", "user-002", Permission.WRITE, "admin-001"
);
KbPermission perm = permService.grantPermission(request);

// 撤销权限
permService.revokePermission("kb-001", "user-002");

// 转移所有权
permService.transferOwnership("kb-001", "old-owner", "new-owner");
```

---

## 九、知识分享 API

### 9.1 接口说明

**接口**: `ShareService`

**位置**: `net.ooder.scene.skill.share.ShareService`

**功能**:
- 分享链接生成
- 分享权限控制
- 分享记录管理
- 访问统计

### 9.2 使用示例

```java
ShareService shareService = new ShareServiceImpl(kbService, permService);

// 创建分享
ShareCreateRequest request = new ShareCreateRequest("kb-001", "user-001");
request.setPassword("123456");
request.setExpiresIn(7 * 24 * 60 * 60 * 1000L); // 7天
request.setMaxAccessCount(100);

ShareInfo share = shareService.createShare(request);
System.out.println("分享码: " + share.getShareCode());

// 验证分享
ShareValidationResult result = shareService.validateShare("SHARECODE", "123456");
if (result.isValid()) {
    System.out.println("分享验证通过");
}

// 获取分享统计
ShareStats stats = shareService.getStats(share.getShareId());
System.out.println("访问次数: " + stats.getTotalAccessCount());
```

---

## 十、批量导入 API

### 10.1 接口说明

**接口**: `BatchImportService`

**位置**: `net.ooder.scene.skill.importer.BatchImportService`

**功能**:
- 压缩包导入
- 目录导入
- 批量 URL 导入
- 导入任务管理

### 10.2 使用示例

```java
BatchImportService importService = new BatchImportServiceImpl(kbService, permService, contributionService);

// 从压缩包导入
ArchiveImportRequest request = new ArchiveImportRequest(
    new FileInputStream(zipFile), "documents.zip", zipFile.length()
);
request.setTags(Arrays.asList("批量导入"));
request.setMaxFileCount(100);

ImportTask task = importService.importFromArchive("user-001", "kb-001", request);

// 查询任务状态
task = importService.getTask(task.getTaskId());
System.out.println("进度: " + task.getProgress() + "%");
System.out.println("状态: " + task.getStatus());

// 批量 URL 导入
List<String> urls = Arrays.asList(
    "https://example.com/doc1",
    "https://example.com/doc2"
);
ImportTask urlTask = importService.importFromUrls("user-001", "kb-001", urls);

// 获取导入结果
ImportResult result = importService.getResult(task.getTaskId());
System.out.println("成功: " + result.getSuccessCount());
System.out.println("失败: " + result.getFailedCount());
```

---

## 十一、应用层集成指南

### 11.1 依赖配置

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>

<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk</artifactId>
    <version>2.3</version>
</dependency>
```

### 11.2 Spring Boot 集成

```java
@Configuration
public class KnowledgeConfig {
    
    @Bean
    public KnowledgeBaseService knowledgeBaseService(
            DocumentChunker chunker,
            EmbeddingService embeddingService,
            VectorStore vectorStore) {
        return new KnowledgeBaseServiceImpl(chunker, embeddingService, vectorStore);
    }
    
    @Bean
    public DocumentChunker documentChunker() {
        return new FixedSizeDocumentChunker();
    }
    
    @Bean
    public EmbeddingService embeddingService(LlmService llmService) {
        return new LlmEmbeddingServiceAdapter(llmService);
    }
    
    @Bean
    public VectorStore vectorStore() {
        return new InMemoryVectorStore(1536);
    }
    
    @Bean
    public RagApi ragPipeline(
            KnowledgeBaseService kbService,
            EmbeddingService embeddingService,
            VectorStore vectorStore,
            LlmGenerator generator) {
        return new RagPipeline(kbService, embeddingService, vectorStore, generator);
    }
    
    @Bean
    public PermissionService permissionService(KnowledgeBaseService kbService) {
        return new PermissionServiceImpl(kbService);
    }
    
    @Bean
    public UserContributionService contributionService(KnowledgeBaseService kbService) {
        return new UserContributionServiceImpl(kbService);
    }
    
    @Bean
    public ShareService shareService(
            KnowledgeBaseService kbService,
            PermissionService permissionService) {
        return new ShareServiceImpl(kbService, permissionService);
    }
    
    @Bean
    public BatchImportService batchImportService(
            KnowledgeBaseService kbService,
            PermissionService permissionService,
            UserContributionService contributionService) {
        return new BatchImportServiceImpl(kbService, permissionService, contributionService);
    }
    
    @Bean
    public ToolRegistry toolRegistry() {
        ToolRegistry registry = new ToolRegistryImpl();
        // 注册内置工具
        // registry.register(new SearchKnowledgeTool(kbService));
        // registry.register(new ListDocumentsTool(kbService));
        return registry;
    }
    
    @Bean
    public ToolOrchestrator toolOrchestrator(ToolRegistry toolRegistry) {
        return new ToolOrchestratorImpl(toolRegistry);
    }
    
    @Bean
    public ConversationService conversationService(
            KnowledgeBaseService kbService,
            RagApi ragPipeline,
            ToolRegistry toolRegistry,
            ToolOrchestrator toolOrchestrator) {
        return new ConversationServiceImpl(kbService, ragPipeline, toolRegistry, toolOrchestrator);
    }
}
```

### 11.3 Controller 示例（应用层实现）

```java
@RestController
@RequestMapping("/api/v1/knowledge-bases")
public class KnowledgeBaseController {
    
    private final KnowledgeBaseService kbService;
    
    public KnowledgeBaseController(KnowledgeBaseService kbService) {
        this.kbService = kbService;
    }
    
    @PostMapping
    public ResponseEntity<KnowledgeBaseDTO> create(@RequestBody CreateKnowledgeBaseDTO dto) {
        KnowledgeBaseCreateRequest request = toRequest(dto);
        KnowledgeBase kb = kbService.create(request);
        return ResponseEntity.ok(toDTO(kb));
    }
    
    @PostMapping("/{kbId}/documents")
    public ResponseEntity<DocumentDTO> addDocument(
            @PathVariable String kbId,
            @RequestBody CreateDocumentDTO dto) {
        DocumentCreateRequest request = toRequest(dto);
        Document doc = kbService.addDocument(kbId, request);
        return ResponseEntity.ok(toDTO(doc));
    }
    
    @PostMapping("/{kbId}/search")
    public ResponseEntity<List<SearchResultDTO>> search(
            @PathVariable String kbId,
            @RequestBody SearchDTO dto) {
        KnowledgeSearchRequest request = toRequest(kbId, dto);
        List<KnowledgeSearchResult> results = kbService.search(request);
        return ResponseEntity.ok(toDTOs(results));
    }
    
    // DTO 转换方法...
}
```

---

## 十二、最佳实践

### 12.1 知识库设计

1. **按领域划分知识库**：不同业务领域使用独立的知识库
2. **合理设置分块大小**：根据文档特点调整 chunkSize 和 chunkOverlap
3. **添加标签和元数据**：便于过滤和检索

### 12.2 向量化策略

1. **使用统一的嵌入模型**：确保向量维度一致
2. **批量处理**：大量文档使用批量向量化
3. **缓存向量**：避免重复计算

### 12.3 RAG 优化

1. **调整 topK 和 threshold**：根据业务需求平衡召回率和精确度
2. **使用混合检索**：结合关键词和向量检索
3. **提示工程**：优化增强提示模板

### 12.4 性能优化

1. **使用连接池**：向量数据库连接池
2. **异步处理**：文档索引使用异步方式
3. **缓存热点数据**：缓存常用查询结果

---

## 十三、常见问题

### Q1: 如何选择向量数据库？

遵循 **微（降级）→ 小 → 大** 架构原则：

| 层级 | 存储实现 | 适用场景 | 依赖 |
|------|----------|----------|------|
| **微（降级）** | `InMemoryVectorStore` | 开发测试、离线场景 | scene-engine 内置 |
| **小** | `SqliteVectorStore` | 小团队、边缘部署 | skill-vector-sqlite |
| **大** | `MilvusVectorStore` | 大规模生产环境 | skill-vector-milvus |

详细架构规范参见：[VECTOR_STORE_ARCHITECTURE.md](./VECTOR_STORE_ARCHITECTURE.md)

### Q2: 如何处理大文件？

1. 使用文档分块服务
2. 异步处理和索引
3. 监控索引状态

### Q3: 如何实现多租户？

1. 使用 ownerId 字段隔离
2. 权限检查
3. 数据过滤

---

## 十五、Function Calling API

### 15.1 接口说明

**接口**: `Tool`, `ToolRegistry`, `ToolOrchestrator`

**位置**: `net.ooder.scene.skill.tool.*`

**功能**:
- 工具定义与注册
- 工具调用执行
- 多工具编排

### 15.2 核心接口

#### 15.2.1 工具定义

```java
public interface Tool {
    
    String getName();
    String getDescription();
    Map<String, Object> getParametersSchema();
    ToolResult execute(Map<String, Object> arguments, ToolExecutionContext context);
    
    default ValidationResult validateArguments(Map<String, Object> arguments) {
        return ValidationResult.success();
    }
    
    default boolean requiresConfirmation() {
        return false;
    }
    
    default boolean isReadOnly() {
        return true;
    }
}
```

#### 15.2.2 工具注册表

```java
public interface ToolRegistry {
    
    void register(Tool tool);
    void registerAll(List<Tool> tools);
    void unregister(String name);
    Optional<Tool> getTool(String name);
    boolean hasTool(String name);
    List<Tool> listAll();
    List<Map<String, Object>> getToolDefinitions();
}
```

#### 15.2.3 工具编排器

```java
public interface ToolOrchestrator {
    
    ToolCallResult executeToolCall(ToolCall toolCall, ToolExecutionContext context);
    List<ToolCallResult> executeToolCalls(List<ToolCall> toolCalls, ToolExecutionContext context);
    OrchestrationResult executePlan(OrchestrationPlan plan, ToolExecutionContext context);
    List<ToolCall> parseToolCalls(String llmResponse);
    String formatToolResults(List<ToolCallResult> results);
}
```

### 15.3 内置工具

| 工具名称 | 功能 | 参数 |
|----------|------|------|
| `search_knowledge` | 知识库检索 | kbId, query, topK |
| `list_documents` | 列出文档 | kbId, limit |

### 15.4 使用示例

#### 15.4.1 创建工具注册表

```java
// 创建工具注册表
ToolRegistry toolRegistry = new ToolRegistryImpl();

// 注册内置工具
toolRegistry.register(new SearchKnowledgeTool(knowledgeBaseService));
toolRegistry.register(new ListDocumentsTool(knowledgeBaseService));

// 获取工具定义（用于 LLM Function Calling）
List<Map<String, Object>> definitions = toolRegistry.getToolDefinitions();
```

#### 15.4.2 执行工具调用

```java
// 创建工具编排器
ToolOrchestrator orchestrator = new ToolOrchestratorImpl(toolRegistry);

// 创建工具调用
ToolCall toolCall = new ToolCall("call-001", "search_knowledge", Map.of(
    "kbId", "kb-001",
    "query", "产品技术特点",
    "topK", 5
));

// 执行工具调用
ToolExecutionContext context = ToolExecutionContext.of("user-001", "kb-001");
ToolCallResult result = orchestrator.executeToolCall(toolCall, context);

if (result.isSuccess()) {
    System.out.println("结果: " + result.getToolResult().getData());
}
```

#### 15.4.3 多工具编排

```java
// 创建编排计划
OrchestrationPlan plan = new OrchestrationPlan();
plan.setPlanId("plan-001");
plan.setStrategy(OrchestrationPlan.ExecutionStrategy.SEQUENTIAL);

// 添加执行步骤
plan.addStep(new OrchestrationPlan.ExecutionStep("step-1", 
    new ToolCall("call-1", "search_knowledge", Map.of("kbId", "kb-001", "query", "产品A"))));
plan.addStep(new OrchestrationPlan.ExecutionStep("step-2", 
    new ToolCall("call-2", "search_knowledge", Map.of("kbId", "kb-001", "query", "产品B"))));

// 执行编排计划
OrchestrationResult result = orchestrator.executePlan(plan, context);

System.out.println("成功步骤: " + result.getSuccessCount());
System.out.println("失败步骤: " + result.getFailureCount());
```

### 15.5 编排策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| SEQUENTIAL | 顺序执行 | 有依赖关系的工具 |
| PARALLEL | 并行执行 | 独立的工具调用 |
| CONDITIONAL | 条件执行 | 根据条件决定是否执行 |
| PIPELINE | 管道执行 | 前一步输出作为后一步输入 |

### 15.6 自定义工具

```java
public class CustomWeatherTool implements Tool {
    
    @Override
    public String getName() {
        return "get_weather";
    }
    
    @Override
    public String getDescription() {
        return "获取指定城市的天气信息";
    }
    
    @Override
    public Map<String, Object> getParametersSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new LinkedHashMap<>();
        Map<String, Object> cityProp = new LinkedHashMap<>();
        cityProp.put("type", "string");
        cityProp.put("description", "城市名称");
        properties.put("city", cityProp);
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("city"));
        
        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolExecutionContext context) {
        String city = (String) arguments.get("city");
        // 调用天气 API...
        return ToolResult.success(Map.of("city", city, "temperature", "25°C", "weather", "晴"));
    }
    
    @Override
    public String getCategory() {
        return "external";
    }
    
    @Override
    public List<String> getTags() {
        return Arrays.asList("weather", "external-api");
    }
}

// 注册自定义工具
toolRegistry.register(new CustomWeatherTool());
```

---

## 十六、多轮对话 API

### 16.1 接口说明

**接口**: `ConversationService`

**位置**: `net.ooder.scene.skill.conversation.ConversationService`

**功能**:
- 对话创建与管理
- 消息发送与接收
- 对话历史管理
- RAG 与工具调用集成

### 16.2 核心方法

```java
public interface ConversationService {
    
    // ========== 对话管理 ==========
    
    Conversation createConversation(String userId, ConversationCreateRequest request);
    Conversation getConversation(String conversationId);
    void deleteConversation(String conversationId);
    List<Conversation> listConversations(String userId, int limit);
    
    // ========== 消息处理 ==========
    
    MessageResponse sendMessage(String conversationId, MessageRequest request);
    void sendMessageStream(String conversationId, MessageRequest request, StreamMessageHandler handler);
    
    // ========== 历史管理 ==========
    
    List<Message> getHistory(String conversationId, int limit);
    void clearHistory(String conversationId);
    ConversationStats getStats(String conversationId);
}
```

### 16.3 使用示例

#### 16.3.1 创建对话

```java
// 创建对话服务
ConversationService conversationService = new ConversationServiceImpl(
    knowledgeBaseService, ragPipeline, toolRegistry, toolOrchestrator
);

// 创建对话
ConversationCreateRequest request = new ConversationCreateRequest();
request.setTitle("产品咨询");
request.setKbId("kb-001");
request.setEnabledTools(Arrays.asList("search_knowledge", "list_documents"));
request.setSystemPrompt("你是一个专业的产品顾问，请根据知识库内容回答用户问题。");

Conversation conversation = conversationService.createConversation("user-001", request);
System.out.println("对话ID: " + conversation.getConversationId());
```

#### 16.3.2 发送消息

```java
// 创建消息请求
MessageRequest messageRequest = new MessageRequest();
messageRequest.setContent("产品A有哪些技术优势？");
messageRequest.setEnableRag(true);
messageRequest.setEnableTools(true);

// 发送消息
MessageResponse response = conversationService.sendMessage(
    conversation.getConversationId(), 
    messageRequest
);

System.out.println("回答: " + response.getContent());

// 查看来源
for (MessageResponse.SourceReference source : response.getSources()) {
    System.out.println("来源: " + source.getTitle() + " (相关度: " + source.getScore() + ")");
}

// 查看工具执行
for (MessageResponse.ToolExecution exec : response.getToolExecutions()) {
    System.out.println("工具: " + exec.getToolName() + " - " + (exec.isSuccess() ? "成功" : "失败"));
}
```

#### 16.3.3 多轮对话

```java
// 第一轮
MessageRequest req1 = new MessageRequest("产品A的价格是多少？");
MessageResponse resp1 = conversationService.sendMessage(conversation.getConversationId(), req1);

// 第二轮（上下文关联）
MessageRequest req2 = new MessageRequest("和产品B相比有什么优势？");
MessageResponse resp2 = conversationService.sendMessage(conversation.getConversationId(), req2);

// 获取对话历史
List<Message> history = conversationService.getHistory(conversation.getConversationId(), 10);
for (Message msg : history) {
    System.out.println(msg.getRole() + ": " + msg.getContent());
}
```

#### 16.3.4 流式响应

```java
// 流式发送消息
MessageRequest streamRequest = new MessageRequest("详细介绍产品A的功能特点");
streamRequest.setEnableRag(true);

conversationService.sendMessageStream(conversation.getConversationId(), streamRequest, 
    new StreamMessageHandler() {
        @Override
        public void onContent(String content) {
            System.out.print(content);  // 实时输出
        }
        
        @Override
        public void onToolCall(String toolName, String arguments) {
            System.out.println("\n[调用工具: " + toolName + "]");
        }
        
        @Override
        public void onComplete(MessageResponse response) {
            System.out.println("\n[完成]");
        }
        
        @Override
        public void onError(String error) {
            System.err.println("错误: " + error);
        }
    }
);
```

### 16.4 对话配置

```java
ConversationCreateRequest request = new ConversationCreateRequest();
request.setTitle("技术支持对话");
request.setKbId("kb-tech-support");

// 启用的工具列表
request.setEnabledTools(Arrays.asList(
    "search_knowledge",
    "list_documents"
));

// 对话设置
Map<String, Object> settings = new HashMap<>();
settings.put("temperature", 0.7);
settings.put("maxTokens", 2000);
settings.put("topK", 5);
settings.put("threshold", 0.7f);
request.setSettings(settings);

// 系统提示词
request.setSystemPrompt("""
    你是一个专业的技术支持助手。
    请根据知识库内容回答用户问题。
    如果知识库中没有相关信息，请诚实告知用户。
    """);
```

### 16.5 对话统计

```java
ConversationStats stats = conversationService.getStats(conversation.getConversationId());

System.out.println("总消息数: " + stats.getTotalMessages());
System.out.println("用户消息: " + stats.getUserMessages());
System.out.println("助手消息: " + stats.getAssistantMessages());
System.out.println("工具调用: " + stats.getToolCalls());
System.out.println("总Token数: " + stats.getTotalTokens());
```

---

## 十七、开发者故事

### 17.1 故事一：智能客服机器人

**场景描述**：

某电商平台需要构建一个智能客服机器人，能够：
- 自动回答用户关于产品的问题
- 查询订单状态
- 处理退换货请求

**技术方案**：

```java
// 1. 创建知识库
KnowledgeBaseCreateRequest kbRequest = KnowledgeBaseCreateRequest.builder()
    .name("电商客服知识库")
    .ownerId("system")
    .visibility(KnowledgeBase.VISIBILITY_TEAM)
    .build();
KnowledgeBase kb = kbService.create(kbRequest);

// 2. 导入产品文档
ArchiveImportRequest importRequest = new ArchiveImportRequest(
    new FileInputStream("products.zip"), "products.zip", fileSize
);
ImportTask task = importService.importFromArchive("system", kb.getKbId(), importRequest);

// 3. 创建自定义工具
public class OrderQueryTool implements Tool {
    @Override
    public String getName() { return "query_order"; }
    
    @Override
    public String getDescription() { return "查询用户订单状态"; }
    
    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolExecutionContext context) {
        String orderId = (String) arguments.get("orderId");
        // 调用订单系统 API...
        return ToolResult.success(orderInfo);
    }
}

// 4. 创建对话服务
toolRegistry.register(new OrderQueryTool());
toolRegistry.register(new SearchKnowledgeTool(kbService));

ConversationCreateRequest convRequest = new ConversationCreateRequest();
convRequest.setKbId(kb.getKbId());
convRequest.setEnabledTools(Arrays.asList("search_knowledge", "query_order"));
convRequest.setSystemPrompt("你是电商客服助手，请友好地回答用户问题。");

Conversation conversation = conversationService.createConversation("user-001", convRequest);
```

**配置说明**：

| 配置项 | 推荐值 | 说明 |
|--------|--------|------|
| chunkSize | 500 | 产品描述通常较短 |
| chunkOverlap | 50 | 保持上下文连贯 |
| topK | 3 | 客服场景精确度优先 |
| threshold | 0.75 | 过滤不相关内容 |

---

### 17.2 故事二：企业知识库问答

**场景描述**：

某企业需要构建内部知识库问答系统：
- 员工可以提问公司政策、流程等问题
- 系统自动从知识库检索相关文档
- 支持多部门知识隔离

**技术方案**：

```java
// 1. 按部门创建知识库
Map<String, String> deptKbMap = new HashMap<>();
for (String dept : Arrays.asList("HR", "IT", "Finance", "Legal")) {
    KnowledgeBase kb = kbService.create(KnowledgeBaseCreateRequest.builder()
        .name(dept + "知识库")
        .ownerId("dept-admin-" + dept)
        .visibility(KnowledgeBase.VISIBILITY_TEAM)
        .build());
    deptKbMap.put(dept, kb.getKbId());
}

// 2. 设置权限
for (Map.Entry<String, String> entry : deptKbMap.entrySet()) {
    // 部门成员有读写权限
    permService.grantPermission(new GrantPermissionRequest(
        entry.getValue(), "user-" + entry.getKey(), Permission.WRITE
    ));
}

// 3. 创建多知识库检索对话
ConversationCreateRequest request = new ConversationCreateRequest();
request.setTitle("企业知识问答");
request.setSystemPrompt("""
    你是企业知识助手。
    请根据用户所属部门，从相应知识库检索信息。
    如果问题涉及多个部门，请综合回答。
    """);

// 4. 发送消息时指定多个知识库
MessageRequest msgRequest = new MessageRequest();
msgRequest.setContent("请假流程是怎样的？");
msgRequest.setKbIds(Arrays.asList(deptKbMap.get("HR")));
msgRequest.setEnableRag(true);
```

**配置说明**：

| 配置项 | 推荐值 | 说明 |
|--------|--------|------|
| visibility | TEAM | 部门隔离 |
| permission | WRITE | 部门成员可贡献 |
| topK | 5 | 平衡召回和精确 |
| maxHistoryLength | 50 | 保持对话上下文 |

---

### 17.3 故事三：技术文档助手

**场景描述**：

开发团队需要一个技术文档助手：
- 帮助开发者快速查找 API 文档
- 提供代码示例
- 支持多轮技术讨论

**技术方案**：

```java
// 1. 创建技术文档知识库
KnowledgeBaseCreateRequest request = KnowledgeBaseCreateRequest.builder()
    .name("API文档库")
    .description("技术API文档和代码示例")
    .chunkSize(1000)  // 代码块需要更长的分块
    .chunkOverlap(100)
    .build();
KnowledgeBase apiKb = kbService.create(request);

// 2. 注册代码执行工具（沙箱环境）
public class CodeExecuteTool implements Tool {
    @Override
    public String getName() { return "execute_code"; }
    
    @Override
    public String getDescription() { return "在沙箱环境中执行代码片段"; }
    
    @Override
    public boolean requiresConfirmation() { return true; }  // 需要用户确认
    
    @Override
    public boolean isReadOnly() { return false; }
    
    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolExecutionContext context) {
        String code = (String) arguments.get("code");
        String language = (String) arguments.get("language");
        // 在沙箱中执行代码...
        return ToolResult.success(executionResult);
    }
}

// 3. 创建技术对话
ConversationCreateRequest techRequest = new ConversationCreateRequest();
techRequest.setKbId(apiKb.getKbId());
techRequest.setEnabledTools(Arrays.asList("search_knowledge", "execute_code"));
techRequest.setSystemPrompt("""
    你是一个技术文档助手。
    请提供准确的API文档和代码示例。
    当需要验证代码时，可以使用execute_code工具。
    """);

// 4. 设置对话参数
Map<String, Object> settings = new HashMap<>();
settings.put("temperature", 0.3);  // 技术场景需要更确定的回答
settings.put("maxTokens", 4000);   // 代码示例可能较长
techRequest.setSettings(settings);
```

**配置说明**：

| 配置项 | 推荐值 | 说明 |
|--------|--------|------|
| chunkSize | 1000 | 代码块完整性 |
| chunkOverlap | 100 | 保持代码上下文 |
| temperature | 0.3 | 技术回答需要精确 |
| maxTokens | 4000 | 代码示例较长 |

---

## 十八、术语表

### 18.1 核心术语

| 术语 | 英文 | 定义 |
|------|------|------|
| 知识库 | Knowledge Base | 存储和管理知识的容器，包含文档和向量索引 |
| 文档 | Document | 知识库中的基本单元，可以是文本、文件或URL导入的内容 |
| 文档分块 | Document Chunking | 将长文档切分成较小片段的过程，便于向量化检索 |
| 向量存储 | Vector Store | 存储文本向量表示的数据库，支持相似度搜索 |
| 嵌入 | Embedding | 将文本转换为向量表示的过程 |
| RAG | Retrieval-Augmented Generation | 检索增强生成，结合检索和生成的问答模式 |

### 18.2 工具相关术语

| 术语 | 英文 | 定义 |
|------|------|------|
| 工具 | Tool | 可被 LLM Function Calling 调用的功能单元 |
| 工具注册表 | Tool Registry | 管理所有可用工具的注册中心 |
| 工具编排 | Tool Orchestration | 协调多个工具按策略执行的过程 |
| 工具调用 | Tool Call | LLM 发起的工具执行请求 |
| 执行上下文 | Execution Context | 工具执行时的环境信息，包含用户ID、知识库ID等 |

### 18.3 对话相关术语

| 术语 | 英文 | 定义 |
|------|------|------|
| 对话 | Conversation | 用户与系统的交互会话，包含多轮消息 |
| 消息 | Message | 对话中的单次交互，可以是用户消息或助手回复 |
| 系统提示词 | System Prompt | 定义助手角色和行为的提示文本 |
| 流式响应 | Stream Response | 逐步返回的响应内容，提升用户体验 |
| 对话历史 | Conversation History | 对话中所有消息的记录 |

### 18.4 架构相关术语

| 术语 | 英文 | 定义 |
|------|------|------|
| 微层 | Micro Layer | 最小化部署方案，内存存储，适用于开发测试 |
| 小层 | Small Layer | 轻量级部署方案，SQLite存储，适用于小团队 |
| 大层 | Large Layer | 企业级部署方案，分布式存储，适用于大规模生产 |
| 降级方案 | Degraded Mode | 当高级功能不可用时自动回退的基础方案 |

---

## 十九、MVEL 规则引擎 API

### 19.1 接口说明

**接口**: `MvelRuleEngine`

**位置**: `net.ooder.scene.core.decision.engine.MvelRuleEngine`

**功能**:
- 离线规则执行
- 规则脚本编译与缓存
- 规则验证
- 多类型规则支持

### 19.2 核心接口

```java
public interface MvelRuleEngine {
    
    Object execute(String script, Map<String, Object> context);
    Object execute(RuleScript ruleScript, Map<String, Object> context);
    void compile(RuleScript ruleScript);
    boolean validate(String script);
    ValidationResult validateWithDetails(String script);
    void clearCache();
    void clearCache(String ruleId);
}
```

### 19.3 规则类型

| 类型 | 代码 | 说明 | 返回值 |
|------|------|------|--------|
| 决策规则 | DECISION | 根据条件选择 Capability | String (capabilityId) |
| 转换规则 | TRANSFORM | 数据格式转换 | Object (转换后数据) |
| 验证规则 | VALIDATION | 数据有效性校验 | Boolean |
| 路由规则 | ROUTING | 多分支路由决策 | String (目标路由) |
| 降级规则 | FALLBACK | 在线失败时的降级处理 | Object (降级结果) |

### 19.4 使用示例

#### 19.4.1 创建规则引擎

```java
MvelRuleEngine ruleEngine = new MvelRuleEngineImpl();
```

#### 19.4.2 执行决策规则

```java
RuleScript decisionRule = RuleScript.builder()
    .ruleId("resume-screening-decision")
    .name("简历筛选决策")
    .type(RuleType.DECISION)
    .script("if (context.containsKey('keywords') && context.get('keywords').contains('Java')) { return 'java_resume_capability'; } else { return 'general_resume_capability'; }")
    .build();

Map<String, Object> context = new HashMap<>();
context.put("keywords", Arrays.asList("Java", "Spring", "MySQL"));

String capabilityId = (String) ruleEngine.execute(decisionRule, context);
System.out.println("选择的 Capability: " + capabilityId);
```

#### 19.4.3 执行转换规则

```java
RuleScript transformRule = RuleScript.builder()
    .ruleId("date-transform")
    .name("日期格式转换")
    .type(RuleType.TRANSFORM)
    .script("import java.text.SimpleDateFormat; SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd'); sdf.format(inputDate);")
    .build();

Map<String, Object> context = new HashMap<>();
context.put("inputDate", new Date());

String formattedDate = (String) ruleEngine.execute(transformRule, context);
```

#### 19.4.4 执行验证规则

```java
RuleScript validationRule = RuleScript.builder()
    .ruleId("email-validation")
    .name("邮箱格式验证")
    .type(RuleType.VALIDATION)
    .script("email != null && email.matches('^[A-Za-z0-9+_.-]+@(.+)$')")
    .build();

Map<String, Object> context = new HashMap<>();
context.put("email", "user@example.com");

boolean isValid = (boolean) ruleEngine.execute(validationRule, context);
```

#### 19.4.5 规则验证

```java
String script = "if (score > 60) { return 'pass'; } else { return 'fail'; }";

boolean valid = ruleEngine.validate(script);
if (valid) {
    System.out.println("规则语法正确");
}

ValidationResult result = ruleEngine.validateWithDetails(script);
if (!result.isValid()) {
    System.out.println("错误: " + result.getErrorMessage());
}
```

### 19.5 规则脚本规范

#### 19.5.1 上下文变量

规则脚本中可用的内置变量：

| 变量 | 类型 | 说明 |
|------|------|------|
| `context` | Map<String, Object> | 业务上下文数据 |
| `query` | String | 用户查询文本 |
| `params` | Map | 请求参数 |
| `userId` | String | 用户ID |
| `timestamp` | long | 时间戳 |

#### 19.5.2 脚本示例

```java
String decisionScript = """
    if (context.get('department') == 'HR') {
        if (context.get('action') == 'hire') {
            return 'hr_hiring_capability';
        }
        return 'hr_general_capability';
    } else if (context.get('department') == 'Finance') {
        return 'finance_capability';
    }
    return 'default_capability';
    """;
```

---

## 二十、决策引擎 API

### 20.1 接口说明

**接口**: `DecisionEngine`

**位置**: `net.ooder.scene.core.decision.DecisionEngine`

**功能**:
- 在线/离线智能决策
- 自动降级支持
- 决策结果缓存
- 决策模式切换

### 20.2 决策模式

| 模式 | 代码 | 说明 | 适用场景 |
|------|------|------|----------|
| 仅在线 | ONLINE_ONLY | 仅使用 LLM 决策 | 对准确性要求高，网络稳定 |
| 仅离线 | OFFLINE_ONLY | 仅使用规则引擎 | 离线环境、性能敏感场景 |
| 优先在线 | ONLINE_FIRST | 优先 LLM，失败降级规则 | 默认模式，兼顾准确性和可用性 |

### 20.3 核心接口

```java
public interface DecisionEngine {
    
    DecisionResult decide(DecisionContext context);
    boolean isLlmAvailable();
    void setMode(DecisionMode mode);
    DecisionMode getMode();
    void clearCache();
}
```

### 20.4 使用示例

#### 20.4.1 创建决策引擎

```java
LlmProvider llmProvider = ...;
MvelRuleEngine ruleEngine = new MvelRuleEngineImpl();

DecisionEngine engine = new DecisionEngineImpl(llmProvider, ruleEngine);
```

#### 20.4.2 执行决策

```java
DecisionContext context = DecisionContext.builder()
    .query("帮我筛选今天的简历")
    .userId("user-001")
    .param("department", "HR")
    .param("dateRange", "today")
    .build();

DecisionResult result = engine.decide(context);

if (result.isSuccess()) {
    System.out.println("选择的 Capability: " + result.getCapability());
    System.out.println("决策来源: " + result.getSource());
    System.out.println("参数: " + result.getParams());
}
```

#### 20.4.3 设置决策模式

```java
engine.setMode(DecisionMode.OFFLINE_ONLY);

DecisionContext context = DecisionContext.builder()
    .query("测试查询")
    .mode(DecisionMode.OFFLINE_ONLY)
    .build();

DecisionResult result = engine.decide(context);
System.out.println("决策来源: " + result.getSource());
```

#### 20.4.4 检查 LLM 可用性

```java
if (engine.isLlmAvailable()) {
    System.out.println("LLM 可用，可使用在线决策");
} else {
    System.out.println("LLM 不可用，将使用离线规则");
}
```

### 20.5 决策结果

```java
public class DecisionResult {
    
    private boolean success;
    private String capability;
    private Map<String, Object> params;
    private DecisionSource source;
    private String errorMessage;
    private long durationMs;
    
    public enum DecisionSource {
        LLM,        // LLM 在线决策
        RULE,       // 规则引擎决策
        FALLBACK,   // 降级决策
        CACHED      // 缓存结果
    }
}
```

---

## 二十一、LLM 规则生成器 API

### 21.1 接口说明

**接口**: `LlmRuleGenerator`

**位置**: `net.ooder.scene.core.decision.generator.LlmRuleGenerator`

**功能**:
- 根据自然语言生成规则
- 规则验证与优化
- 规则模板管理

### 21.2 核心接口

```java
public interface LlmRuleGenerator {
    
    RuleScript generate(RuleGenerateRequest request);
    ValidationResult validate(RuleScript ruleScript);
    RuleScript optimize(RuleScript ruleScript);
    List<RuleTemplate> getTemplates();
    RuleScript generateFromTemplate(String templateId, Map<String, Object> params);
}
```

### 21.3 使用示例

#### 21.3.1 生成决策规则

```java
LlmRuleGenerator generator = new LlmRuleGeneratorImpl(llmProvider);

RuleGenerateRequest request = RuleGenerateRequest.builder()
    .description("根据部门选择对应的处理能力")
    .type(RuleType.DECISION)
    .contextDescription("context 包含 department 字段")
    .expectedOutput("返回 capabilityId 字符串")
    .examples(Arrays.asList(
        "部门是HR时返回 hr_capability",
        "部门是Finance时返回 finance_capability"
    ))
    .build();

RuleScript rule = generator.generate(request);
System.out.println("生成的规则: " + rule.getScript());
```

#### 21.3.2 验证规则

```java
RuleScript ruleScript = RuleScript.builder()
    .ruleId("test-rule")
    .script("if (context.get('value') > 100) { return 'high'; }")
    .build();

ValidationResult result = generator.validate(ruleScript);
if (result.isValid()) {
    System.out.println("规则有效");
} else {
    System.out.println("错误: " + result.getErrorMessage());
}
```

#### 21.3.3 使用模板生成规则

```java
List<RuleTemplate> templates = generator.getTemplates();
for (RuleTemplate template : templates) {
    System.out.println("模板: " + template.getName() + " - " + template.getDescription());
}

Map<String, Object> params = new HashMap<>();
params.put("threshold", 100);
params.put("highCapability", "premium_capability");
params.put("lowCapability", "standard_capability");

RuleScript rule = generator.generateFromTemplate("threshold-decision", params);
```

---

## 二十二、知识能力 API

### 22.1 接口说明

**接口**: `KnowledgeCapability`

**位置**: `net.ooder.scene.skill.knowledge.KnowledgeCapability`

**功能**:
- 三层知识检索
- 跨层知识聚合
- 知识来源追踪

### 22.2 三层架构

| 层级 | 枚举值 | 说明 | 示例 |
|------|--------|------|------|
| 通用知识层 | GENERAL | 全局共享知识 | 公司制度、流程规范 |
| 专业模块层 | PROFESSIONAL | 领域专业知识 | HR模块、财务模块 |
| 场景知识层 | SCENE | 场景私有知识 | 招聘场景、培训场景 |

### 22.3 核心接口

```java
public interface KnowledgeCapability {
    
    KnowledgeResult retrieve(String query, KnowledgeLayer layer);
    KnowledgeResult retrieveCrossLayer(String query, KnowledgeLayer startLayer);
    KnowledgeResult retrieveWithFallback(String query, KnowledgeLayer... layers);
    void indexKnowledge(String content, Map<String, Object> metadata, KnowledgeLayer layer);
    void clearLayerIndex(KnowledgeLayer layer);
}
```

### 22.4 使用示例

#### 22.4.1 单层检索

```java
KnowledgeCapability knowledgeCap = new KnowledgeCapabilityImpl(
    embeddingService, vectorStore, knowledgeBaseService
);

KnowledgeResult result = knowledgeCap.retrieve("Java开发岗位要求", KnowledgeLayer.SCENE);

for (RetrievedItem item : result.getItems()) {
    System.out.println("内容: " + item.getContent());
    System.out.println("相似度: " + item.getScore());
    System.out.println("来源: " + item.getSource());
}
```

#### 22.4.2 跨层检索

```java
KnowledgeResult result = knowledgeCap.retrieveCrossLayer(
    "请假流程是怎样的", 
    KnowledgeLayer.SCENE
);

for (RetrievedItem item : result.getItems()) {
    System.out.println("层级: " + item.getLayer());
    System.out.println("内容: " + item.getContent());
}
```

#### 22.4.3 带降级的检索

```java
KnowledgeResult result = knowledgeCap.retrieveWithFallback(
    "公司报销政策",
    KnowledgeLayer.SCENE,
    KnowledgeLayer.PROFESSIONAL,
    KnowledgeLayer.GENERAL
);

if (result.isEmpty()) {
    System.out.println("未找到相关知识");
} else {
    System.out.println("找到 " + result.getItems().size() + " 条知识");
}
```

#### 22.4.4 索引知识

```java
Map<String, Object> metadata = new HashMap<>();
metadata.put("docId", "doc-001");
metadata.put("source", "hr-manual");
metadata.put("tags", Arrays.asList("请假", "流程"));

knowledgeCap.indexKnowledge(
    "员工请假需提前3天申请，经直属领导审批...",
    metadata,
    KnowledgeLayer.GENERAL
);
```

---

## 二十三、增强型 LLM Provider API

### 23.1 接口说明

**接口**: `EnhancedLlmProvider`

**位置**: `net.ooder.scene.skill.llm.EnhancedLlmProvider`

**功能**:
- Function Calling 支持
- 多模态输入支持
- 上下文管理
- 批量请求支持

### 23.2 核心接口

```java
public interface EnhancedLlmProvider {
    
    String chat(String systemPrompt, String userMessage);
    String chatWithHistory(String systemPrompt, List<Message> history, String userMessage);
    FunctionCallResult chatWithFunctions(String systemPrompt, String userMessage, List<FunctionDef> functions);
    String chatMultimodal(String systemPrompt, List<ContentPart> contents);
    String chatWithContext(String systemPrompt, String userMessage, Map<String, Object> context);
    List<String> batchChat(List<ChatRequest> requests);
    
    void registerModel(String modelId, ModelConfig config);
    void setDefaultModel(String modelId);
    String getDefaultModel();
}
```

### 23.3 使用示例

#### 23.3.1 基本对话

```java
EnhancedLlmProvider provider = new AbstractLlmProviderImpl(llmService);

String response = provider.chat(
    "你是一个专业的HR助手",
    "请问如何筛选简历？"
);
System.out.println(response);
```

#### 23.3.2 Function Calling

```java
List<FunctionDef> functions = Arrays.asList(
    FunctionDef.builder()
        .name("search_knowledge")
        .description("搜索知识库")
        .parameter("query", "string", "搜索关键词")
        .parameter("kbId", "string", "知识库ID")
        .build(),
    FunctionDef.builder()
        .name("send_email")
        .description("发送邮件")
        .parameter("to", "string", "收件人邮箱")
        .parameter("subject", "string", "邮件主题")
        .parameter("body", "string", "邮件内容")
        .build()
);

FunctionCallResult result = provider.chatWithFunctions(
    "你是智能助手",
    "帮我搜索产品A的相关信息",
    functions
);

if (result.hasFunctionCall()) {
    System.out.println("函数名: " + result.getFunctionName());
    System.out.println("参数: " + result.getArguments());
} else {
    System.out.println("回复: " + result.getContent());
}
```

#### 23.3.3 多模态输入

```java
List<ContentPart> contents = Arrays.asList(
    ContentPart.text("这张图片是什么内容？"),
    ContentPart.image("https://example.com/image.png"),
    ContentPart.imageBase64(base64ImageData, "image/png")
);

String response = provider.chatMultimodal("你是图像分析助手", contents);
```

#### 23.3.4 带上下文的对话

```java
Map<String, Object> context = new HashMap<>();
context.put("userId", "user-001");
context.put("department", "HR");
context.put("permissions", Arrays.asList("read", "write"));

String response = provider.chatWithContext(
    "你是智能助手",
    "查询我部门的待办事项",
    context
);
```

#### 23.3.5 批量请求

```java
List<ChatRequest> requests = Arrays.asList(
    new ChatRequest("翻译成英文", "你好世界"),
    new ChatRequest("翻译成英文", "今天天气不错"),
    new ChatRequest("翻译成英文", "谢谢你的帮助")
);

List<String> responses = provider.batchChat(requests);
for (int i = 0; i < responses.size(); i++) {
    System.out.println("请求" + (i+1) + ": " + responses.get(i));
}
```

### 23.4 模型配置

```java
ModelConfig config = ModelConfig.builder()
    .modelId("gpt-4")
    .provider("openai")
    .maxTokens(4000)
    .temperature(0.7)
    .supportsFunctionCalling(true)
    .supportsMultimodal(true)
    .build();

provider.registerModel("gpt-4", config);
provider.setDefaultModel("gpt-4");
```

---

## 二十四、LLM 与场景技能集成

### 24.1 技术分层架构

LLM 在场景技能架构中跨两层存在：

| 层次 | LLM 角色 | 职责 |
|------|----------|------|
| **技能层** | 决策者 | 意图理解、能力选择、参数提取、结果解释 |
| **基础层** | 能力提供者 | Chat Completion、Text Embedding、Function Calling |

### 24.2 LLM 介入模式

#### 模式1: 路由器

用户意图不明确时，LLM 理解并路由到正确的 Capability：

```
User Query ──▶ LLM ──▶ 意图识别 ──▶ Capability 选择
```

#### 模式2: 执行器

需要 LLM 生成内容或处理自然语言：

```
Capability 调用 ──▶ LLM ──▶ 结果生成
```

#### 模式3: 协调器

复杂任务需要多步骤协调：

```
复杂任务 ──▶ LLM ──▶ 任务拆解 ──▶ 多 Capability 协调
```

### 24.3 离线支持与在线决策

#### 设计原则

| 原则 | 说明 |
|------|------|
| 业务逻辑离线定义 | Capability 接口、业务规则、工作流在离线时定义 |
| LLM 在线增强 | LLM 用于增强用户体验，非必需 |
| 降级策略 | LLM 不可用时回退到规则引擎 |

### 24.4 知识库价值定位

#### 双重服务模式

| 模式 | 服务对象 | 价值 |
|------|----------|------|
| **RAG 增强** | LLM | 增强领域知识、提供上下文、减少幻觉 |
| **直接检索** | 用户 | 直接提供信息、可追溯来源、无需 LLM |

### 24.5 完整架构图

```
┌─────────────────────────────────────────────────────────────┐
│  用户层: User ──▶ SceneGroup ──▶ SceneAgent                 │
├─────────────────────────────────────────────────────────────┤
│  场景层: SceneSkill ──▶ Driver/Executor Capabilities        │
├─────────────────────────────────────────────────────────────┤
│  决策层: LLM Decision (在线) ◀──▶ Rule Engine (离线降级)    │
├─────────────────────────────────────────────────────────────┤
│  能力层: ToolRegistry ──▶ SearchKnowledgeTool, ...          │
├─────────────────────────────────────────────────────────────┤
│  基础层: LLM Provider, Knowledge Base, Vector Store         │
└─────────────────────────────────────────────────────────────┘
```

---

## 二十五、知识库分层架构

### 25.1 三层架构

| 层级 | 名称 | 范围 | 特点 |
|------|------|------|------|
| **Layer 1** | 通用知识层 | 全局共享 | 公司制度、流程规范、FAQ |
| **Layer 2** | 专业模块层 | 领域共享 | HR模块、财务模块、销售模块 |
| **Layer 3** | 场景知识层 | 场景私有 | 招聘场景、培训场景、审批场景 |

### 25.2 跨层检索策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| **单层检索** | 仅检索指定层 | 精确场景，如仅查候选人简历 |
| **向下扩展** | 从场景层向下扩展 | 智能问答，优先场景知识 |
| **并行检索** | 同时检索多层，按权重合并 | 综合查询，需要多源知识 |

### 25.3 权限控制

| 层级 | 权限范围 | 访问控制 |
|------|----------|----------|
| 通用知识层 | 全局 | 所有认证用户可读，管理员可写 |
| 专业模块层 | 领域 | 领域角色可读，领域管理员可写 |
| 场景知识层 | 场景 | 场景参与者可访问，场景管理员可管理 |

### 25.4 相关文档

详细设计请参考：[技术故事：LLM与场景技能集成](TECH_STORY_LLM_INTEGRATION.md)

---

## 二十六、LLM Provider 标准实现指南

### 26.1 接口定义

**基础接口**: `LlmProvider`

**位置**: `net.ooder.scene.skill.llm.LlmProvider`

```java
public interface LlmProvider {
    String getProviderType();
    List<String> getSupportedModels();
    Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options);
    String complete(String model, String prompt, Map<String, Object> options);
    List<double[]> embed(String model, List<String> texts);
    String translate(String model, String text, String targetLanguage, String sourceLanguage);
    String summarize(String model, String text, int maxLength);
    boolean supportsStreaming();
    boolean supportsFunctionCalling();
    void chatStream(String model, List<Map<String, Object>> messages, Map<String, Object> options, StreamHandler handler);
}
```

**增强接口**: `EnhancedLlmProvider`

**位置**: `net.ooder.scene.skill.llm.EnhancedLlmProvider`

```java
public interface EnhancedLlmProvider extends LlmProvider {
    Map<String, Object> chatWithFunctions(String model, List<Map<String, Object>> messages, List<FunctionCall> functions, Map<String, Object> options);
    Map<String, Object> executeFunctionCall(String model, List<Map<String, Object>> messages, String functionName, Map<String, Object> functionArgs, Object functionResult, Map<String, Object> options);
    Map<String, Object> chatMultimodal(String model, List<Map<String, Object>> messages, Map<String, Object> options);
    Map<String, Object> chatWithContext(String model, List<Map<String, Object>> messages, String systemPrompt, Map<String, Object> context, Map<String, Object> options);
    List<Map<String, Object>> batchChat(List<ChatRequest> requests);
    boolean supportsFunctionCalling(String model);
    boolean supportsMultimodal(String model);
    int getContextWindowSize(String model);
    int countTokens(String model, String text);
}
```

### 26.2 实现规范

#### 26.2.1 JSON 解析规范

**必须使用 fastjson 进行 JSON 解析**，禁止手动解析：

```java
// ✅ 正确：使用 fastjson
private Map<String, Object> parseResponse(String response) {
    Map<String, Object> result = new HashMap<>();
    try {
        JSONObject json = JSON.parseObject(response);
        JSONArray choices = json.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            result.put("content", message.getString("content"));
        }
    } catch (Exception e) {
        log.error("Parse response error", e);
        result.put("error", true);
    }
    return result;
}

// ❌ 错误：手动解析 JSON
private String extractJsonValue(String json, String key) {
    // 不要这样做！
}
```

#### 26.2.2 请求构建规范

```java
private JSONObject buildRequestBody(String model, List<Map<String, Object>> messages,
                                     Map<String, Object> options) {
    JSONObject body = new JSONObject();
    body.put("model", model);
    body.put("messages", messages);
    
    if (options != null) {
        if (options.containsKey("temperature")) {
            body.put("temperature", options.get("temperature"));
        }
        if (options.containsKey("max_tokens")) {
            body.put("max_tokens", options.get("max_tokens"));
        }
        if (options.containsKey("tools")) {
            body.put("tools", options.get("tools"));
        }
    }
    
    return body;
}
```

#### 26.2.3 错误处理规范

```java
@Override
public Map<String, Object> chat(String model, List<Map<String, Object>> messages, 
                                 Map<String, Object> options) {
    Map<String, Object> result = new HashMap<>();
    
    try {
        JSONObject requestBody = buildRequestBody(model, messages, options);
        String response = sendRequest(API_URL, requestBody);
        result = parseResponse(response);
        result.put("model", model);
        result.put("provider", getProviderType());
    } catch (Exception e) {
        log.error("Chat API error", e);
        result.put("error", true);
        result.put("content", "Error: " + e.getMessage());
    }
    
    return result;
}
```

### 26.3 标准实现示例

参考代码：[StandardLlmProviderExample.java](../src/main/java/net/ooder/scene/skill/llm/example/StandardLlmProviderExample.java)

---

## 二十七、Function Calling 标准实现指南

### 27.1 函数定义规范

```java
// 函数参数定义
Map<String, Object> params = new LinkedHashMap<>();
params.put("city", createStringParam("城市名称"));
params.put("unit", createStringParam("温度单位", Arrays.asList("celsius", "fahrenheit")));

// 辅助方法
public static Map<String, Object> createStringParam(String description) {
    Map<String, Object> param = new LinkedHashMap<>();
    param.put("type", "string");
    param.put("description", description);
    return param;
}

public static Map<String, Object> createStringParam(String description, List<String> enumValues) {
    Map<String, Object> param = createStringParam(description);
    param.put("enum", enumValues);
    return param;
}
```

### 27.2 函数注册规范

```java
// 注册函数
functionRegistry.register("get_weather", "获取指定城市的天气信息",
    params, Arrays.asList("city"),  // required 参数
    arguments -> {
        String city = (String) arguments.get("city");
        String unit = (String) arguments.getOrDefault("unit", "celsius");
        
        // 执行实际业务逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("city", city);
        result.put("temperature", 25);
        result.put("unit", unit);
        return result;
    });
```

### 27.3 LLM Tools 格式转换

```java
public List<Map<String, Object>> getToolsForLLM() {
    List<Map<String, Object>> tools = new ArrayList<>();
    
    for (FunctionDefinition def : functions.values()) {
        Map<String, Object> tool = new LinkedHashMap<>();
        tool.put("type", "function");
        
        Map<String, Object> function = new LinkedHashMap<>();
        function.put("name", def.getName());
        function.put("description", def.getDescription());
        
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("type", "object");
        parameters.put("properties", def.getParameters());
        if (def.getRequired() != null) {
            parameters.put("required", def.getRequired());
        }
        function.put("parameters", parameters);
        
        tool.put("function", function);
        tools.add(tool);
    }
    
    return tools;
}
```

### 27.4 函数调用处理

```java
@SuppressWarnings("unchecked")
public Object executeFunctionCall(Map<String, Object> toolCall) {
    Map<String, Object> func = (Map<String, Object>) toolCall.get("function");
    String funcName = (String) func.get("name");
    String argsJson = (String) func.get("arguments");
    
    // 使用 fastjson 解析参数
    Map<String, Object> args = new HashMap<>();
    if (argsJson != null && !argsJson.isEmpty()) {
        JSONObject json = JSON.parseObject(argsJson);
        for (String key : json.keySet()) {
            args.put(key, json.get(key));
        }
    }
    
    return executeFunction(funcName, args);
}
```

### 27.5 标准实现示例

参考代码：[FunctionCallingExample.java](../src/main/java/net/ooder/scene/skill/llm/example/FunctionCallingExample.java)

---

## 二十八、应用端集成最佳实践

### 28.1 Controller 层设计

```java
@RestController
@RequestMapping("/api/llm")
public class LlmController {
    
    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
    private final FunctionRegistry functionRegistry = new FunctionRegistry();
    
    // 使用有界线程池
    private final ExecutorService executor = new ThreadPoolExecutor(
        4, 16, 60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(100),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
    
    @PostMapping("/chat")
    public ResultModel<ChatResponseDTO> chat(@RequestBody @Valid ChatRequestDTO request) {
        String providerType = request.getProvider() != null ? 
            request.getProvider() : defaultProvider;
        String model = request.getModel() != null ? 
            request.getModel() : defaultModel;
        
        LlmProvider provider = providers.get(providerType);
        if (provider == null) {
            return ResultModel.error(503, "Provider not available");
        }
        
        try {
            List<Map<String, Object>> messages = buildMessages(request);
            Map<String, Object> options = buildOptions(request);
            
            // 添加 Function Calling 支持
            if (provider.supportsFunctionCalling() && request.isEnableFunctions()) {
                List<FunctionCall> functions = functionRegistry.getFunctionCalls();
                Map<String, Object> result = provider.chatWithFunctions(
                    model, messages, functions, options);
                return handleChatResult(result, model, providerType);
            } else {
                Map<String, Object> result = provider.chat(model, messages, options);
                return handleChatResult(result, model, providerType);
            }
        } catch (Exception e) {
            log.error("Chat error", e);
            return ResultModel.error(500, "Chat failed: " + e.getMessage());
        }
    }
    
    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

### 28.2 会话历史管理

```java
public class ConversationManager {
    private final Map<String, List<Map<String, Object>>> conversations = 
        new ConcurrentHashMap<>();
    private final int maxHistoryLength = 50;
    
    public void addMessage(String conversationId, Map<String, Object> message) {
        List<Map<String, Object>> history = conversations.computeIfAbsent(
            conversationId, k -> new ArrayList<>());
        history.add(message);
        
        // 限制历史长度
        if (history.size() > maxHistoryLength) {
            history.remove(0);
        }
    }
    
    public List<Map<String, Object>> getHistory(String conversationId) {
        return new ArrayList<>(conversations.getOrDefault(
            conversationId, Collections.emptyList()));
    }
    
    public void clearHistory(String conversationId) {
        conversations.remove(conversationId);
    }
}
```

### 28.3 系统提示词配置

```yaml
# application.yml
ooder:
  llm:
    provider: deepseek
    model: deepseek-chat
    sse-timeout: 180000
    system-prompt: |
      你是Ooder场景技能平台的智能助手。
      你的职责是帮助用户管理场景、发现和安装能力、配置场景参数。
      请用简洁专业的中文回复。
```

```java
@Value("${ooder.llm.system-prompt:#{null}}")
private String configSystemPrompt;

private String getSystemPrompt() {
    return configSystemPrompt != null ? configSystemPrompt : DEFAULT_SYSTEM_PROMPT;
}
```

### 28.4 标准实现示例

参考代码：[LlmControllerExample.java](../src/main/java/net/ooder/scene/skill/llm/example/LlmControllerExample.java)

---

## 二十九、常见问题与解决方案

### Q1: JSON 解析失败如何处理？

**问题**：手动解析 JSON 容易出错，特别是嵌套结构和特殊字符。

**解决方案**：统一使用 fastjson 解析：

```java
// 使用 fastjson 安全解析
try {
    JSONObject json = JSON.parseObject(response);
    // 处理逻辑
} catch (Exception e) {
    log.error("JSON parse error", e);
    // 返回错误响应
}
```

### Q2: Function Calling 结果如何处理？

**问题**：LLM 返回的 tool_calls 需要正确解析和执行。

**解决方案**：

```java
if (result.containsKey("tool_calls")) {
    List<Map<String, Object>> toolCalls = 
        (List<Map<String, Object>>) result.get("tool_calls");
    
    for (Map<String, Object> toolCall : toolCalls) {
        Map<String, Object> func = (Map<String, Object>) toolCall.get("function");
        String funcName = (String) func.get("name");
        String argsJson = (String) func.get("arguments");
        
        Map<String, Object> args = JSON.parseObject(argsJson, 
            new TypeReference<Map<String, Object>>() {});
        
        Object funcResult = functionRegistry.execute(funcName, args);
        // 处理结果...
    }
}
```

### Q3: 线程池如何正确配置？

**问题**：CachedThreadPool 无上限，可能导致资源耗尽。

**解决方案**：

```java
// 推荐配置
private final ExecutorService executor = new ThreadPoolExecutor(
    4,                          // 核心线程数
    16,                         // 最大线程数
    60L, TimeUnit.SECONDS,      // 空闲线程存活时间
    new LinkedBlockingQueue<>(100),  // 有界队列
    new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
);

// 优雅关闭
@PreDestroy
public void shutdown() {
    executor.shutdown();
    try {
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
    }
}
```

### Q4: 如何支持多轮对话上下文？

**问题**：ChatRequestDTO 有 history 字段但未使用。

**解决方案**：

```java
private List<Map<String, Object>> buildMessages(ChatRequestDTO request) {
    List<Map<String, Object>> messages = new ArrayList<>();
    
    // 添加系统提示
    if (request.getSystemPrompt() != null) {
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", request.getSystemPrompt());
        messages.add(systemMessage);
    }
    
    // 添加历史消息
    if (request.getHistory() != null && !request.getHistory().isEmpty()) {
        messages.addAll(request.getHistory());
    }
    
    // 添加用户消息
    Map<String, Object> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", request.getMessage());
    messages.add(userMessage);
    
    return messages;
}
```

### Q5: Provider 不可用时如何降级？

**问题**：LLM 服务不可用时系统完全失效。

**解决方案**：

```java
public Map<String, Object> chat(String model, List<Map<String, Object>> messages,
                                 Map<String, Object> options) {
    LlmProvider provider = providers.get(currentProviderType);
    
    if (provider != null) {
        try {
            return provider.chat(model, messages, options);
        } catch (Exception e) {
            log.warn("Primary provider failed, trying fallback", e);
        }
    }
    
    // 降级到 Mock Provider
    if (mockEnabled) {
        return getMockResponse(messages);
    }
    
    // 返回错误
    Map<String, Object> error = new HashMap<>();
    error.put("error", true);
    error.put("content", "No LLM provider available");
    return error;
}
```

---

## 三十、版本历史

| 版本 | 日期 | 变更 |
|------|------|------|
| 2.3.1 | 2026-03-09 | 新增 LLM Provider 标准实现指南、Function Calling 标准实现指南、应用端集成最佳实践、常见问题与解决方案 |
| 2.3.1 | 2026-03-07 | 新增 MVEL 规则引擎 API、决策引擎 API、LLM 规则生成器 API、知识能力 API、增强型 LLM Provider API；场景技能分类体系修订；新增 LLM 与场景技能集成；新增知识库分层架构 |
| 2.3 | 2026-03-06 | 新增知识库管理、向量存储、RAG Pipeline、用户知识贡献、权限管理、知识分享、批量导入、Function Calling、多轮对话 |

---

---

## 三十一、动态 LLM 驱动设计

### 31.1 问题背景

当前应用层实现中，系统提示词和 Function 定义是硬编码的：

```java
// 硬编码问题示例
private static final String SYSTEM_PROMPT = "你是Ooder场景技能平台的智能助手...";

private void initFunctions() {
    functionRegistry.register("start_scan", "开始扫描发现能力", params, args -> {
        // 硬编码的执行逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    });
}
```

**问题**：
1. 不同 Skill 需要不同的系统提示词，无法动态切换
2. 函数定义硬编码，新增功能需要修改代码
3. 无法支持多 Skill 场景

### 31.2 动态驱动架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SkillLlmDriver (动态加载器)                       │
├─────────────────────────────────────────────────────────────────────┤
│  输入: SkillPackage.metadata                                       │
│  输出: SkillLlmConfig                                              │
├─────────────────────────────────────────────────────────────────────┤
│  + getSystemPrompt(skillId) -> String                              │
│  + getFunctions(skillId) -> List<FunctionDefinition>               │
│  + getFunctionCalls(skillId) -> List<FunctionCall>                 │
│  + executeFunction(skillId, funcName, args) -> Object              │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    Skill 元数据结构 (skill.json)                     │
├─────────────────────────────────────────────────────────────────────┤
│  {                                                                 │
│    "skillId": "recruitment-skill",                                 │
│    "name": "招聘助手",                                              │
│    "metadata": {                                                   │
│      "llmConfig": {                                                │
│        "systemPrompt": "你是招聘场景的智能助手...",                  │
│        "temperature": 0.7,                                         │
│        "maxTokens": 2000,                                          │
│        "functions": [                                              │
│          {                                                         │
│            "name": "scan_resume",                                  │
│            "description": "扫描并解析简历",                         │
│            "parameters": {                                         │
│              "resumeId": {"type": "string", "description": "简历ID"}│
│            },                                                      │
│            "required": ["resumeId"],                               │
│            "capability": "resume_scan"                            │
│          }                                                         │
│        ]                                                           │
│      },                                                            │
│      "sceneCapabilities": [...]                                    │
│    }                                                               │
│  }                                                                 │
└─────────────────────────────────────────────────────────────────────┘
```

### 31.3 核心组件

#### 31.3.1 SkillLlmConfig

```java
public class SkillLlmConfig {
    private String skillId;
    private String systemPrompt;
    private Double temperature;
    private Integer maxTokens;
    private String defaultModel;
    private String defaultProvider;
    private List<FunctionDefinition> functions;
    private List<CapabilityMapping> capabilityMappings;
    private Map<String, Object> extendedConfig;
}
```

#### 31.3.2 SkillLlmDriver

```java
public class SkillLlmDriver {
    // 从 SkillPackage 加载配置
    public SkillLlmConfig loadConfig(Object skillPackage);
    
    // 获取系统提示词
    public String getSystemPrompt(String skillId);
    
    // 获取函数定义
    public List<FunctionDefinition> getFunctions(String skillId);
    
    // 获取 LLM 可用的函数调用
    public List<FunctionCall> getFunctionCalls(String skillId);
    
    // 注册函数执行器
    public void registerFunctionExecutor(String skillId, String funcName, FunctionExecutor executor);
    
    // 执行函数
    public Object executeFunction(String skillId, String funcName, 
                                   Map<String, Object> args, ExecutionContext context);
}
```

#### 31.3.3 DynamicLlmController

```java
public class DynamicLlmController {
    private final SkillLlmDriver llmDriver;
    
    // 动态聊天接口
    public ChatResponse chat(ChatRequest request) {
        String skillId = request.getSkillId();
        SkillLlmConfig config = llmDriver.getLlmConfig(skillId);
        
        // 动态获取系统提示词
        String systemPrompt = config.getSystemPrompt();
        
        // 动态获取函数定义
        List<FunctionCall> functions = llmDriver.getFunctionCalls(skillId);
        
        // 调用 LLM
        return provider.chatWithFunctions(model, messages, functions, options);
    }
}
```

### 31.4 使用示例

#### 31.4.1 配置 Skill 元数据

```json
{
  "skillId": "recruitment-skill",
  "name": "招聘助手",
  "metadata": {
    "llmConfig": {
      "systemPrompt": "你是招聘场景的智能助手，帮助HR筛选简历和安排面试。",
      "temperature": 0.7,
      "maxTokens": 2000,
      "functions": [
        {
          "name": "scan_resume",
          "description": "扫描并解析简历",
          "parameters": {
            "resumeId": {"type": "string", "description": "简历ID"}
          },
          "required": ["resumeId"],
          "capability": "resume_scan"
        },
        {
          "name": "schedule_interview",
          "description": "安排面试",
          "parameters": {
            "candidateId": {"type": "string", "description": "候选人ID"},
            "interviewerId": {"type": "string", "description": "面试官ID"},
            "time": {"type": "string", "description": "面试时间"}
          },
          "required": ["candidateId", "interviewId", "time"],
          "capability": "schedule_interview"
        }
      ]
    }
  }
}
```

#### 31.4.2 应用层集成

```java
// 初始化
SkillLlmDriver driver = new SkillLlmProvider().createDriver();
DynamicLlmController controller = new DynamicLlmController(driver);

// 加载 Skill 配置
SkillPackage skillPackage = skillRegistry.getSkill("recruitment-skill");
controller.loadSkillConfig(skillPackage);

// 注册函数执行器（执行逻辑由应用层实现）
controller.registerFunctionExecutor("recruitment-skill", "scan_resume", (args, ctx) -> {
    String resumeId = (String) args.get("resumeId");
    return resumeService.scan(resumeId);
});

controller.registerFunctionExecutor("recruitment-skill", "schedule_interview", (args, ctx) -> {
    return interviewService.schedule(
        (String) args.get("candidateId"),
        (String) args.get("interviewId"),
        (String) args.get("time")
    );
});

// 处理聊天请求
ChatRequest request = new ChatRequest();
request.setSkillId("recruitment-skill");
request.setMessage("请帮我扫描简历 RESUME-001");

ChatResponse response = controller.chat(request);
```

### 31.5 架构优势

| 特性 | 硬编码方式 | 动态驱动方式 |
|------|-----------|-------------|
| 系统提示词 | 固定，修改需改代码 | 可配置，每个 Skill 可不同 |
| 函数定义 | 硬编码，扩展困难 | 动态加载，扩展灵活 |
| 多 Skill 支持 | 需大量 if-else 判断 | 自动适配不同 Skill |
| 版本管理 | 无法支持版本差异 | 不同版本可配置不同 LLM 行为 |
| 部署灵活性 | 需重新部署 | 热更新配置即可 |

### 31.6 扩展点

1. **Capability 映射**：将 LLM 函数映射到具体的 Capability 执行
2. **参数转换**：支持 LLM 参数到 Capability 参数的转换
3. **权限控制**：基于 Skill 配置控制函数调用权限
4. **多轮对话**：支持上下文管理和历史记录

### 31.7 核心组件详解

#### 31.7.1 SkillLlmConfig

配置模型，从 Skill 元数据中提取的 LLM 驱动配置：

```java
public class SkillLlmConfig {
    private String skillId;
    private String systemPrompt;        // 动态系统提示词
    private Double temperature;          // 温度参数
    private Integer maxTokens;           // 最大 Token 数
    private String defaultModel;         // 默认模型
    private String defaultProvider;      // 默认 Provider
    private List<FunctionDefinition> functions;  // 函数定义列表
    private List<CapabilityMapping> capabilityMappings;  // 能力映射
}
```

#### 31.7.2 SkillLlmDriver

动态驱动器，负责加载配置和执行函数：

```java
public class SkillLlmDriver {
    // 从 SkillPackage 加载配置
    public SkillLlmConfig loadConfig(Object skillPackage);
    
    // 获取系统提示词
    public String getSystemPrompt(String skillId);
    
    // 获取函数定义（用于 LLM API）
    public List<FunctionCall> getFunctionCalls(String skillId);
    
    // 注册函数执行器
    public void registerFunctionExecutor(String skillId, String funcName, FunctionExecutor executor);
    
    // 执行函数
    public Object executeFunction(String skillId, String funcName, 
                                   Map<String, Object> args, ExecutionContext context);
}
```

#### 31.7.3 SkillFunctionExecutor

函数执行器，将 LLM 函数映射到 Skill Capability：

```java
public class SkillFunctionExecutor {
    // 注册函数到 Capability 的映射
    public void registerMapping(String skillId, FunctionDefinition function);
    
    // 执行函数调用（自动映射到 Capability）
    public Object execute(String skillId, String functionName, 
                          Map<String, Object> arguments, ExecutionContext context);
}
```

#### 31.7.4 SkillLlmDriverFactory

工厂类，简化驱动器创建和配置：

```java
public class SkillLlmDriverFactory {
    // 设置 SkillSDKAdapter
    public SkillLlmDriverFactory withSkillSDKAdapter(SkillSDKAdapter adapter);
    
    // 设置默认 Provider
    public SkillLlmDriverFactory withDefaultProvider(String provider);
    
    // 设置默认 Model
    public SkillLlmDriverFactory withDefaultModel(String model);
    
    // 创建驱动器
    public SkillLlmDriver createDriver();
    
    // 自动注册函数执行器
    public void registerFunctionExecutors(SkillLlmDriver driver, Object skillPackage);
}
```

### 31.8 完整集成示例

```java
// 1. 创建工厂
SkillLlmDriverFactory factory = new SkillLlmDriverFactory()
    .withSkillSDKAdapter(skillSDKAdapter)
    .withDefaultProvider("deepseek")
    .withDefaultModel("deepseek-chat");

// 2. 创建驱动器
SkillLlmDriver driver = factory.createDriver();

// 3. 加载 Skill 配置
SkillPackage skillPackage = skillRegistry.getSkill("recruitment-skill");
driver.loadConfig(skillPackage);

// 4. 自动注册函数执行器（映射到 Capability）
factory.registerFunctionExecutors(driver, skillPackage);

// 5. 处理聊天请求
ChatRequest request = new ChatRequest();
request.setSkillId("recruitment-skill");
request.setMessage("请帮我扫描简历 RESUME-001");

// 6. 获取动态配置
String systemPrompt = driver.getSystemPrompt("recruitment-skill");
List<FunctionCall> functions = driver.getFunctionCalls("recruitment-skill");

// 7. 调用 LLM
Map<String, Object> result = provider.chatWithFunctions(model, messages, functions, options);

// 8. 处理 Function Calling 结果
if (result.containsKey("tool_calls")) {
    for (Map<String, Object> toolCall : (List<Map<String, Object>>) result.get("tool_calls")) {
        String funcName = ((Map<String, Object>) toolCall.get("function")).get("name");
        Map<String, Object> args = JSON.parseObject(
            (String) ((Map<String, Object>) toolCall.get("function")).get("arguments"),
            new TypeReference<Map<String, Object>>() {});
        
        // 执行函数（自动映射到 Capability）
        Object funcResult = driver.executeFunction(skillId, funcName, args, context);
    }
}
```

### 31.9 迁移指南

从硬编码方式迁移到动态驱动方式：

| 步骤 | 硬编码方式 | 动态驱动方式 |
|------|-----------|-------------|
| 1. 系统提示词 | `private static final String SYSTEM_PROMPT = "..."` | `driver.getSystemPrompt(skillId)` |
| 2. 函数定义 | `functionRegistry.register("name", "desc", params, executor)` | 从 `metadata.llmConfig.functions` 自动加载 |
| 3. 函数执行 | 硬编码执行逻辑 | 自动映射到 `Capability` 执行 |
| 4. 多 Skill 支持 | 需要 if-else 判断 | 自动适配不同 Skill |

### 31.10 前端 JavaScript 集成

前端 JavaScript 应用通过 REST API 调用后端服务，实现函数到 Capability 的自动映射。

#### 31.10.1 架构设计

```
┌─────────────────────────────────────────────────────────────────────┐
│                        前端 JavaScript                              │
├─────────────────────────────────────────────────────────────────────┤
│  SkillLlmDriver SDK                                                │
│  - loadSkillConfig(skillId)      加载 Skill 配置                   │
│  - getSystemPrompt(skillId)      获取系统提示词                     │
│  - getFunctions(skillId)         获取函数定义                       │
│  - executeFunction(skillId, fn, args)  执行函数（自动映射 Capability）│
├─────────────────────────────────────────────────────────────────────┤
│                          REST API 调用                              │
│  POST /api/skill/{skillId}/llm/config    获取 LLM 配置             │
│  POST /api/skill/{skillId}/capability    调用 Capability           │
│  POST /api/llm/chat                      LLM 对话                  │
└─────────────────────────────────────────────────────────────────────┘
```

#### 31.10.2 函数到 Capability 映射规则

```javascript
// 函数定义中指定 capability 字段
{
  "name": "scan_resume",
  "capability": "resume_scan",  // 映射到此 Capability
  "parameters": {...}
}

// 调用流程
// 1. LLM 返回 function_call: { name: "scan_resume", arguments: {...} }
// 2. SDK 查找映射: scan_resume -> resume_scan
// 3. SDK 调用: POST /api/skill/{skillId}/capability { capability: "resume_scan", params: {...} }
```

#### 31.10.3 完整使用示例

```javascript
// 初始化
const driver = new SkillLlmDriver({ baseUrl: 'https://api.example.com' });
const chatClient = new LlmChatClient(driver);

// 加载 Skill 配置
await driver.loadSkillConfig('recruitment-skill');

// 发送聊天
const response = await chatClient.chat({
  skillId: 'recruitment-skill',
  message: '请帮我扫描简历 RESUME-001'
});

// 处理响应
console.log('回复:', response.content);
if (response.actions) {
  console.log('执行的动作:', response.actions);
}
```

#### 31.10.4 后端 API 要求

应用层需要实现以下 REST API：

| API | 方法 | 说明 |
|-----|------|------|
| `/api/skill/{skillId}/llm/config` | GET | 获取 Skill LLM 配置 |
| `/api/skill/{skillId}/capability` | POST | 调用 Capability |
| `/api/llm/chat` | POST | LLM 对话 |

详细文档请参考：[JavaScript SDK 开发配置说明](./JAVASCRIPT_SDK_GUIDE.md)

### 31.11 参考代码

| 文件 | 说明 |
|------|------|
| [SkillLlmConfig.java](../src/main/java/net/ooder/scene/skill/llm/driver/SkillLlmConfig.java) | Skill LLM 配置模型 |
| [SkillLlmDriver.java](../src/main/java/net/ooder/scene/skill/llm/driver/SkillLlmDriver.java) | 动态 LLM 驱动器 |
| [SkillFunctionExecutor.java](../src/main/java/net/ooder/scene/skill/llm/driver/SkillFunctionExecutor.java) | 函数执行器 |
| [SkillLlmDriverFactory.java](../src/main/java/net/ooder/scene/skill/llm/driver/SkillLlmDriverFactory.java) | 驱动器工厂 |
| [DynamicLlmController.java](../src/main/java/net/ooder/scene/skill/llm/driver/DynamicLlmController.java) | 动态 LLM 控制器示例 |
| [IntegrationExample.java](../src/main/java/net/ooder/scene/skill/llm/driver/IntegrationExample.java) | 完整集成示例 |
| [JAVASCRIPT_SDK_GUIDE.md](./JAVASCRIPT_SDK_GUIDE.md) | JavaScript SDK 开发配置说明 |

---

## 三十二、Skills-LLM 体系架构

### 32.1 核心设计理念

Skills-LLM 体系解决以下核心问题：

| 问题 | 解决方案 |
|------|----------|
| Function Calling 注入 | 激活 Skill 时自动注入函数定义到上下文 |
| 知识库多级加载 | skills.md 支持 BASIC/ADVANCED/EXPERT/FULL 四级加载 |
| 页面上下文重组 | 角色 + 知识库 + Skill + 记忆 自动组装运行时上下文 |
| A2A 上下文传递 | 定义默认传递规则，支持四种传递模式 |

### 32.2 架构层次

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Skills-LLM 体系架构                              │
├─────────────────────────────────────────────────────────────────────┤
│  用户界面层: 角色选择 | 知识库选择 | Skill选择 | 会话管理           │
├─────────────────────────────────────────────────────────────────────┤
│  上下文重组层: RoleContext + KnowledgeContext + FunctionContext    │
├─────────────────────────────────────────────────────────────────────┤
│  A2A 传输层: ContextTransfer (FULL | REFERENCE | DELTA | SELECTIVE) │
└─────────────────────────────────────────────────────────────────────┘
```

### 32.3 Skill 激活时注入 Function Calling

```java
// 激活 Skill 时自动加载函数定义
SkillActivationContext context = SkillActivationContext.activate(
    ActivationRequest.builder()
        .skillId("recruitment-skill")
        .userId("user-xxx")
        .roleId("hr-assistant")
        .build()
);

// 函数定义自动注入到上下文
List<Map<String, Object>> tools = context.getFunctionContext().toTools();
```

### 32.4 知识库多级加载

```java
// 四级加载
public enum KnowledgeLoadLevel {
    BASIC(1, "基础知识", 2048),      // ~2K tokens
    ADVANCED(2, "进阶知识", 4096),    // ~4K tokens
    EXPERT(3, "专家知识", 8192),      // ~8K tokens
    FULL(4, "完整知识", -1);          // 无限制
}

// 加载知识
KnowledgeContext knowledge = KnowledgeContext.load(
    "recruitment-skill", 
    KnowledgeLoadLevel.ADVANCED
);
```

### 32.5 页面上下文重组

```java
// 用户打开页面时重组上下文
LlmRuntimeContext context = LlmRuntimeContext.assemble(
    AssemblyRequest.builder()
        .userId("user-xxx")
        .roleId("hr-assistant")
        .knowledgeBaseIds(["kb-xxx"])
        .skillId("recruitment-skill")
        .sessionId("sess-xxx")
        .build()
);

// 组装后的上下文
// - systemPrompt: 角色定义 + 知识库内容
// - tools: 函数定义列表
// - messages: 对话历史
```

### 32.6 A2A 默认上下文传递

```java
// 默认传递配置
A2AContextTransferConfig config = new A2AContextTransferConfig();
config.setDefaultMode(TransferMode.SELECTIVE);
config.setDefaultIncludedParts(Set.of(
    ContextPart.USER_CONTEXT,        // 用户身份
    ContextPart.KNOWLEDGE_CONTEXT,   // 知识库引用
    ContextPart.FUNCTION_CONTEXT,    // 函数定义
    ContextPart.MEMORY_CONTEXT       // 对话记忆
));

// 准备传递
ContextTransfer transfer = transferHandler.prepareTransfer(
    sourceContextId,
    TransferMode.SELECTIVE,
    null  // 使用默认配置
);
```

### 32.7 详细文档

完整架构设计请参考：[Skills-LLM 体系架构设计](./SKILLS_LLM_ARCHITECTURE.md)

### 32.8 核心实现类

| 类 | 文件 | 说明 |
|----|------|------|
| SkillActivationContext | [SkillActivationContext.java](../src/main/java/net/ooder/scene/llm/context/SkillActivationContext.java) | Skill 激活上下文，管理激活时的完整上下文 |
| FunctionContext | [FunctionContext.java](../src/main/java/net/ooder/scene/llm/context/FunctionContext.java) | 函数定义上下文，管理 Function Calling |
| RoleContext | [RoleContext.java](../src/main/java/net/ooder/scene/llm/context/RoleContext.java) | 角色上下文，定义 AI 助手角色和行为 |
| MemoryContext | [MemoryContext.java](../src/main/java/net/ooder/scene/llm/context/MemoryContext.java) | 记忆上下文，管理对话历史 |
| KnowledgeContext | [KnowledgeContext.java](../src/main/java/net/ooder/scene/llm/context/KnowledgeContext.java) | 知识库上下文，支持多级加载 |

### 32.9 使用示例

```java
// 1. 激活 Skill
SkillActivationContext context = SkillActivationContext.activate(
    SkillActivationContext.ActivationRequest.builder()
        .skillId("recruitment-skill")
        .userId("user-xxx")
        .roleId("hr-assistant")
        .knowledgeBaseIds(Arrays.asList("kb-001"))
        .build()
);

// 2. 获取系统提示词（角色 + 知识）
String systemPrompt = context.buildSystemPrompt();

// 3. 获取函数定义
List<Map<String, Object>> tools = context.getTools();

// 4. 获取消息历史
List<Map<String, Object>> messages = context.getMessages();

// 5. 执行函数调用
Map<String, Object> args = new HashMap<>();
args.put("resumeId", "RESUME-001");
Object result = context.executeFunction("scan_resume", args);
```

### 32.10 需求覆盖度

详细覆盖度报告请参考：[Skills-LLM 体系需求覆盖度报告](./SKILLS_LLM_COVERAGE_REPORT.md)

---

## 三十三、v2.3.1 代码重构 - 重复类清理

### 33.1 重构背景

Scene Engine v2.3.1 版本对代码库进行了全面的重复类清理，遵循以下两个核心原则：

| 原则 | 处理方式 | 示例 |
|------|----------|------|
| 功能属性相同 | 抽象合并 | AuditStats 合并 |
| 业务属性不同 | 按业务重命名 | Permission → UserPermission/SecurityPermission |

### 33.2 重复类清理清单

#### 33.2.1 AuditStats 合并

**问题**: `skill/audit/AuditStats` 和 `audit/AuditStats` 两个类功能重复

**解决方案**: 合并到 `audit/AuditStats`，保留所有字段：

```java
public class AuditStats {
    // 原 audit/AuditStats 字段
    private String operationType;
    private long totalCount;
    private long successCount;
    private long failCount;
    private double avgResponseTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    // 合并自 skill/audit/AuditStats 的字段
    private long pendingCount;      // 待处理数
    private long processingCount;   // 处理中数
    private Map<String, Long> statusDistribution;  // 状态分布
    private Map<String, Double> timeDistribution;  // 时间分布
}
```

**文件变更**:
- ✅ 删除: `skill/audit/AuditStats.java`
- ✅ 保留: `audit/AuditStats.java`（扩展字段）

#### 33.2.2 HealthStatus 删除

**问题**: `core/driver/HealthStatus` 已被 `core/health/HealthStatus` 取代

**解决方案**: 直接删除废弃类

**文件变更**:
- ✅ 删除: `core/driver/HealthStatus.java`

#### 33.2.3 Permission 类重命名

**问题**: 三个不同业务的 Permission 类同名

**解决方案**: 按业务域重命名

| 原类名 | 新类名 | 业务域 | 文件路径 |
|--------|--------|--------|----------|
| `provider/model/user/Permission` | `UserPermission` | 用户权限 | `provider/model/user/UserPermission.java` |
| `core/security/Permission` | `SecurityPermission` | 安全权限 | `core/security/SecurityPermission.java` |

#### 33.2.4 Discovery 类重命名

**问题**: Discovery 相关类命名过于通用

**解决方案**: 添加业务前缀

| 原类名 | 新类名 | 说明 |
|--------|--------|------|
| `protocol/DiscoveryRequest` | `PeerDiscoveryRequest` | 节点发现请求 |
| `protocol/DiscoveryResult` | `PeerDiscoveryResult` | 节点发现结果 |

#### 33.2.5 InstallResult 重命名

**问题**: `core/lifecycle/InstallResult` 与安装生命周期相关

**解决方案**: 添加模块前缀

| 原类名 | 新类名 | 文件路径 |
|--------|--------|----------|
| `core/lifecycle/InstallResult` | `LifecycleInstallResult` | `core/lifecycle/LifecycleInstallResult.java` |

#### 33.2.6 SecuritySkillService 删除

**问题**: `skill/security/SecuritySkillService` 实现不完整

**解决方案**: 删除未完成代码

**文件变更**:
- ✅ 删除: `skill/security/SecuritySkillService.java`

### 33.3 重构影响评估

| 类别 | 数量 | 影响范围 |
|------|------|----------|
| 合并类 | 1 | AuditStats 使用方需更新引用 |
| 删除类 | 2 | HealthStatus, SecuritySkillService |
| 重命名类 | 5 | Permission×2, Discovery×2, InstallResult×1 |
| **总计** | **8** | 涉及 18 个重复类名 |

### 33.4 迁移指南

对于二次开发团队，如果引用了被删除或重命名的类：

```java
// 迁移前
import net.ooder.scene.skill.audit.AuditStats;
import net.ooder.scene.provider.model.user.Permission;
import net.ooder.scene.protocol.DiscoveryRequest;

// 迁移后
import net.ooder.scene.audit.AuditStats;
import net.ooder.scene.provider.model.user.UserPermission;
import net.ooder.scene.protocol.PeerDiscoveryRequest;
```

---

## 三十四、MVP 协作需求实现

### 34.1 需求概述

基于 MVP 协作需求文档，Scene Engine v2.3.1 实现了以下核心功能：

| 需求编号 | 需求描述 | 实现状态 | 说明 |
|----------|----------|----------|------|
| Q1 | 统一角色定义 API | ✅ 已完成 | 提供标准化角色查询接口 |
| Q2 | 协作权限检查 API | ⚠️ 外部实现 | 需接入外部权限系统 |
| Q3 | 权限拦截器机制 | ✅ 已完成 | 注解驱动的权限控制 |
| Q4 | 安装 API 系列 | ✅ 已完成 | 完整的安装流程接口 |
| Q5 | 安装进度广播 | ⚠️ 外部实现 | 需接入外部消息系统 |

### 34.2 Q1: 统一角色定义 API

#### 34.2.1 接口定义

**端点**: `GET /api/v1/auth/roles`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "installer",
      "name": "安装员",
      "description": "负责 Skill 的安装和配置",
      "permissions": ["skill:install", "skill:configure"],
      "level": 1
    },
    {
      "id": "admin",
      "name": "管理员",
      "description": "系统管理员，拥有所有权限",
      "permissions": ["*"],
      "level": 100
    },
    {
      "id": "leader",
      "name": "团队负责人",
      "description": "管理团队和成员权限",
      "permissions": ["team:manage", "member:manage", "skill:assign"],
      "level": 50
    },
    {
      "id": "collaborator",
      "name": "协作者",
      "description": "参与协作的普通成员",
      "permissions": ["skill:view", "skill:execute"],
      "level": 10
    }
  ]
}
```

#### 34.2.2 核心实现

**Role 模型**:

```java
public class Role {
    private String id;              // 角色标识
    private String name;            // 角色名称
    private String description;     // 角色描述
    private List<String> permissions; // 权限列表
    private int level;              // 角色级别
    private boolean isDefault;      // 是否默认角色
    private Map<String, Object> metadata; // 扩展属性
}
```

**RoleService 服务**:

```java
@Service
public class RoleService {
    
    public List<Role> getAllRoles() {
        return Arrays.asList(
            Role.builder()
                .id("installer")
                .name("安装员")
                .description("负责 Skill 的安装和配置")
                .permissions(Arrays.asList("skill:install", "skill:configure"))
                .level(1)
                .build(),
            // ... 其他角色
        );
    }
    
    public Role getRoleById(String roleId) {
        return getAllRoles().stream()
            .filter(r -> r.getId().equals(roleId))
            .findFirst()
            .orElse(null);
    }
}
```

**AuthController 控制器**:

```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping("/roles")
    public Result<List<Role>> getAllRoles() {
        return Result.success(roleService.getAllRoles());
    }
    
    @GetMapping("/roles/{roleId}")
    public Result<Role> getRoleById(@PathVariable String roleId) {
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            return Result.error(404, "Role not found");
        }
        return Result.success(role);
    }
}
```

### 34.3 Q3: 权限拦截器机制

#### 34.3.1 注解定义

**RequirePermission 注解**:

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String[] value() default {};           // 需要的权限列表
    Logic logic() default Logic.AND;       // 逻辑关系：AND/OR
    
    enum Logic {
        AND,    // 需要所有权限
        OR      // 需要任一权限
    }
}
```

#### 34.3.2 拦截器实现

**PermissionInterceptor**:

```java
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private PermissionService permissionService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission annotation = handlerMethod
            .getMethodAnnotation(RequirePermission.class);
        
        if (annotation == null) {
            annotation = handlerMethod.getBeanType()
                .getAnnotation(RequirePermission.class);
        }
        
        if (annotation == null) {
            return true; // 无需权限检查
        }
        
        // 获取当前用户
        String userId = getCurrentUserId(request);
        if (userId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        
        // 检查权限
        String[] requiredPermissions = annotation.value();
        RequirePermission.Logic logic = annotation.logic();
        
        boolean hasPermission;
        if (logic == RequirePermission.Logic.AND) {
            hasPermission = permissionService.hasAllPermissions(
                userId, requiredPermissions);
        } else {
            hasPermission = permissionService.hasAnyPermission(
                userId, requiredPermissions);
        }
        
        if (!hasPermission) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
        
        return true;
    }
}
```

#### 34.3.3 使用示例

```java
@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {
    
    // 需要所有指定权限
    @RequirePermission({"skill:install", "skill:configure"})
    @PostMapping("/{skillId}/install")
    public Result<Void> installSkill(@PathVariable String skillId) {
        // 安装逻辑
        return Result.success();
    }
    
    // 需要任一权限
    @RequirePermission(value = {"skill:admin", "skill:manage"}, logic = Logic.OR)
    @DeleteMapping("/{skillId}")
    public Result<Void> deleteSkill(@PathVariable String skillId) {
        // 删除逻辑
        return Result.success();
    }
    
    // 类级别权限（应用于所有方法）
    @RequirePermission("skill:view")
    @RestController
    @RequestMapping("/api/v1/public")
    public class PublicSkillController {
        // 所有方法都需要 skill:view 权限
    }
}
```

#### 34.3.4 注册拦截器

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private PermissionInterceptor permissionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
            .addPathPatterns("/api/v1/**")
            .excludePathPatterns("/api/v1/auth/**", "/api/v1/public/**");
    }
}
```

### 34.4 Q4: 安装 API 系列

#### 34.4.1 接口概览

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/install/status` | GET | 获取安装状态 |
| `/api/v1/install/start` | POST | 开始安装 |
| `/api/v1/install/progress` | GET | 获取安装进度 |
| `/api/v1/install/complete` | POST | 完成安装 |

#### 34.4.2 安装状态查询

**请求**: `GET /api/v1/install/status?skillId={skillId}`

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "skillId": "recruitment-skill",
    "status": "INSTALLING",
    "currentStep": 3,
    "totalSteps": 5,
    "stepName": "配置初始化",
    "progress": 60,
    "startTime": "2026-03-13T10:00:00Z",
    "estimatedEndTime": "2026-03-13T10:05:00Z"
  }
}
```

#### 34.4.3 开始安装

**请求**: `POST /api/v1/install/start`

```json
{
  "skillId": "recruitment-skill",
  "version": "1.0.0",
  "config": {
    "databaseUrl": "jdbc:mysql://localhost:3306/skill_db",
    "apiEndpoint": "https://api.example.com"
  },
  "options": {
    "autoStart": true,
    "enableLogging": true
  }
}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "installId": "install-xxx",
    "skillId": "recruitment-skill",
    "status": "INSTALLING",
    "startTime": "2026-03-13T10:00:00Z"
  }
}
```

#### 34.4.4 安装进度查询

**请求**: `GET /api/v1/install/progress?installId={installId}`

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "installId": "install-xxx",
    "currentStep": 3,
    "totalSteps": 5,
    "progress": 60,
    "stepDetails": [
      {"step": 1, "name": "下载资源", "status": "COMPLETED", "progress": 100},
      {"step": 2, "name": "验证完整性", "status": "COMPLETED", "progress": 100},
      {"step": 3, "name": "配置初始化", "status": "IN_PROGRESS", "progress": 60},
      {"step": 4, "name": "依赖安装", "status": "PENDING", "progress": 0},
      {"step": 5, "name": "启动服务", "status": "PENDING", "progress": 0}
    ],
    "logs": [
      {"time": "2026-03-13T10:02:00Z", "level": "INFO", "message": "开始配置初始化..."},
      {"time": "2026-03-13T10:02:30Z", "level": "INFO", "message": "数据库连接成功"}
    ]
  }
}
```

#### 34.4.5 完成安装

**请求**: `POST /api/v1/install/complete`

```json
{
  "installId": "install-xxx",
  "verificationCode": "verify-xxx"
}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "installId": "install-xxx",
    "skillId": "recruitment-skill",
    "status": "COMPLETED",
    "completedTime": "2026-03-13T10:05:00Z",
    "nextSteps": [
      "访问 /api/v1/skills/recruitment-skill 查看详情",
      "配置权限和访问控制"
    ]
  }
}
```

#### 34.4.6 核心实现

**InstallController**:

```java
@RestController
@RequestMapping("/api/v1/install")
public class InstallController {
    
    @Autowired
    private InstallService installService;
    
    @GetMapping("/status")
    public Result<InstallStatus> getInstallStatus(@RequestParam String skillId) {
        InstallStatus status = installService.getStatus(skillId);
        return Result.success(status);
    }
    
    @PostMapping("/start")
    public Result<InstallResult> startInstall(@RequestBody InstallRequest request) {
        InstallResult result = installService.startInstall(request);
        return Result.success(result);
    }
    
    @GetMapping("/progress")
    public Result<InstallProgress> getInstallProgress(@RequestParam String installId) {
        InstallProgress progress = installService.getProgress(installId);
        return Result.success(progress);
    }
    
    @PostMapping("/complete")
    public Result<CompleteResult> completeInstall(@RequestBody CompleteRequest request) {
        CompleteResult result = installService.completeInstall(request);
        return Result.success(result);
    }
}
```

---

## 三十五、Spring MVC UrlPathHelper 修复

### 35.1 问题描述

在动态注册 `RequestMappingInfo` 时，Spring MVC 的 `UrlPathHelper.getResolvedLookupPath` 方法需要 `org.springframework.web.util.UrlPathHelper.PATH` 属性，但该属性未被正确设置，导致以下错误：

```
java.lang.IllegalStateException: 
  Could not find PATH attribute in request. 
  Are you using UrlPathHelper to parse the request URL?
```

### 35.2 根本原因

当通过 `RequestMappingHandlerMapping` 动态注册 Controller 时，Spring MVC 需要解析请求路径。`UrlPathHelper` 使用请求属性缓存解析后的路径，但在某些情况下该属性未被设置。

### 35.3 解决方案

#### 35.3.1 WebMvcConfig 配置

创建全局 UrlPathHelper 配置：

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setAlwaysUseFullPath(true);
        urlPathHelper.setUrlDecode(true);
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }
}
```

#### 35.3.2 FixedUrlPathHelper 实现

创建自定义的 UrlPathHelper，缓存 PATH 属性：

```java
@Configuration
public class RequestMappingConfig {
    
    @Bean
    @Primary
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
        
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setAlwaysUseFullPath(true);
        urlPathHelper.setUrlDecode(true);
        urlPathHelper.setRemoveSemicolonContent(false);
        
        mapping.setUrlPathHelper(urlPathHelper);
        return mapping;
    }
    
    public static class FixedUrlPathHelper extends UrlPathHelper {
        private static final String PATH_ATTRIBUTE = 
            FixedUrlPathHelper.class.getName() + ".PATH";
        
        @Override
        public String resolveAndCacheLookupPath(HttpServletRequest request) {
            String lookupPath = (String) request.getAttribute(PATH_ATTRIBUTE);
            if (lookupPath != null) {
                return lookupPath;
            }
            lookupPath = getPathWithinApplication(request);
            request.setAttribute(PATH_ATTRIBUTE, lookupPath);
            return lookupPath;
        }
    }
}
```

### 35.4 配置说明

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `alwaysUseFullPath` | `true` | 始终使用完整路径 |
| `urlDecode` | `true` | 解码 URL |
| `removeSemicolonContent` | `false` | 保留分号内容 |

### 35.5 动态注册示例

修复后，可以正常动态注册 Controller：

```java
@Service
public class DynamicControllerRegistrar {
    
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    
    public void registerDynamicController(String path, Method method, Object handler) {
        RequestMappingInfo mappingInfo = RequestMappingInfo
            .paths(path)
            .methods(RequestMethod.GET)
            .build();
        
        handlerMapping.registerMapping(mappingInfo, handler, method);
    }
}
```

### 35.6 兼容性说明

- **Spring Boot 版本**: 2.7.x, 3.x
- **Spring MVC 版本**: 5.3.x, 6.x
- **JDK 版本**: 8+

---

## 三十六、Skills 动态加载架构

### 36.1 架构概述

Scene Engine 支持非 POM 方式的 Skills 动态加载，实现 Skill 的热插拔和运行时扩展。

### 36.2 核心组件

```
┌─────────────────────────────────────────────────────────────────┐
│                    Skills 动态加载架构                           │
├─────────────────────────────────────────────────────────────────┤
│  Skill Loader                                                    │
│  ├── SkillJarLoader        # JAR 包加载器                        │
│  ├── SkillClassLoader      # 类隔离加载器                        │
│  └── SkillScanner          # Skill 扫描器                        │
├─────────────────────────────────────────────────────────────────┤
│  Skill Manager                                                   │
│  ├── SkillRegistry         # Skill 注册表                        │
│  ├── SkillLifecycle        # 生命周期管理                        │
│  └── SkillDependency       # 依赖管理                            │
├─────────────────────────────────────────────────────────────────┤
│  Skill Runtime                                                   │
│  ├── SkillContext          # Skill 上下文                        │
│  ├── SkillFunction         # 函数注册                            │
│  └── SkillEvent            # 事件处理                            │
└─────────────────────────────────────────────────────────────────┘
```

### 36.3 加载流程

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ 扫描目录  │ -> │ 加载 JAR  │ -> │ 解析配置  │ -> │ 注册 Skill│
└──────────┘    └──────────┘    └──────────┘    └──────────┘
      │              │              │              │
      ▼              ▼              ▼              ▼
  扫描 skills/    创建 ClassLoader  读取 skill.json  注入 Spring
  目录下的 JAR    隔离加载类         解析元数据      容器管理
```

### 36.4 核心实现

#### 36.4.1 SkillJarLoader

```java
@Component
public class SkillJarLoader {
    
    private final Map<String, SkillClassLoader> classLoaders = new ConcurrentHashMap<>();
    
    public Skill loadSkillFromJar(File jarFile) throws Exception {
        // 创建独立的 ClassLoader
        SkillClassLoader classLoader = new SkillClassLoader(
            jarFile.toURI().toURL(),
            this.getClass().getClassLoader()
        );
        
        // 加载 skill.json 配置
        SkillConfig config = loadSkillConfig(classLoader);
        
        // 加载主类
        Class<?> mainClass = classLoader.loadClass(config.getMainClass());
        Skill skill = (Skill) mainClass.getDeclaredConstructor().newInstance();
        
        // 缓存 ClassLoader
        classLoaders.put(config.getSkillId(), classLoader);
        
        return skill;
    }
    
    private SkillConfig loadSkillConfig(SkillClassLoader classLoader) throws IOException {
        InputStream is = classLoader.getResourceAsStream("skill.json");
        return new ObjectMapper().readValue(is, SkillConfig.class);
    }
}
```

#### 36.4.2 SkillClassLoader

```java
public class SkillClassLoader extends URLClassLoader {
    
    private final Set<String> sharedPackages;
    
    public SkillClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.sharedPackages = Set.of(
            "net.ooder.scene.api",
            "net.ooder.scene.skill",
            "org.springframework"
        );
    }
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 共享包使用父类加载器
        for (String pkg : sharedPackages) {
            if (name.startsWith(pkg)) {
                return super.loadClass(name, resolve);
            }
        }
        
        // 优先从当前 ClassLoader 加载
        try {
            return findClass(name);
        } catch (ClassNotFoundException e) {
            return super.loadClass(name, resolve);
        }
    }
}
```

#### 36.4.3 SkillLifecycle

```java
@Service
public class SkillLifecycle {
    
    @Autowired
    private SkillRegistry registry;
    
    public void install(Skill skill) {
        // 1. 验证依赖
        validateDependencies(skill);
        
        // 2. 注册到 Spring 容器
        registerToSpringContext(skill);
        
        // 3. 初始化
        skill.initialize();
        
        // 4. 注册到注册表
        registry.register(skill);
        
        // 5. 发布事件
        publishEvent(new SkillInstalledEvent(skill));
    }
    
    public void start(String skillId) {
        Skill skill = registry.get(skillId);
        skill.start();
        publishEvent(new SkillStartedEvent(skill));
    }
    
    public void stop(String skillId) {
        Skill skill = registry.get(skillId);
        skill.stop();
        publishEvent(new SkillStoppedEvent(skill));
    }
    
    public void uninstall(String skillId) {
        Skill skill = registry.get(skillId);
        skill.destroy();
        registry.unregister(skillId);
        publishEvent(new SkillUninstalledEvent(skill));
    }
}
```

### 36.5 Skill 配置

**skill.json**:

```json
{
  "skillId": "recruitment-skill",
  "name": "招聘助手",
  "version": "1.0.0",
  "mainClass": "com.example.RecruitmentSkill",
  "dependencies": [
    {
      "skillId": "resume-parser",
      "version": ">=1.0.0"
    }
  ],
  "functions": [
    {
      "name": "scan_resume",
      "description": "扫描简历",
      "parameters": {
        "resumeId": {
          "type": "string",
          "description": "简历ID"
        }
      }
    }
  ],
  "resources": {
    "memory": "512MB",
    "cpu": "1"
  }
}
```

### 36.6 使用示例

```java
@Service
public class SkillManagementService {
    
    @Autowired
    private SkillJarLoader jarLoader;
    
    @Autowired
    private SkillLifecycle lifecycle;
    
    // 从 JAR 文件安装 Skill
    public void installSkillFromJar(String jarPath) throws Exception {
        File jarFile = new File(jarPath);
        Skill skill = jarLoader.loadSkillFromJar(jarFile);
        lifecycle.install(skill);
    }
    
    // 启动 Skill
    public void startSkill(String skillId) {
        lifecycle.start(skillId);
    }
    
    // 停止 Skill
    public void stopSkill(String skillId) {
        lifecycle.stop(skillId);
    }
    
    // 卸载 Skill
    public void uninstallSkill(String skillId) {
        lifecycle.uninstall(skillId);
    }
}
```

### 36.7 目录结构

```
skills/
├── recruitment-skill-1.0.0.jar
├── resume-parser-1.0.0.jar
└── interview-scheduler-1.0.0.jar
```

### 36.8 注意事项

| 注意事项 | 说明 |
|----------|------|
| 类隔离 | 每个 Skill 使用独立的 ClassLoader，避免类冲突 |
| 依赖管理 | Skill 依赖的其他 Skill 需先安装 |
| 资源限制 | 可为每个 Skill 配置内存和 CPU 限制 |
| 版本兼容 | 检查 Skill 与 Scene Engine 版本兼容性 |
| 热更新 | 卸载后重新安装可实现热更新 |

---

## 三十七、LLM 上下文协作需求实现

### 37.1 变更背景

#### 37.1.1 问题背景

Scene Engine v2.3.1 版本之前，Ooder 平台的 LLM 模块存在以下核心问题：

| 问题 | 影响 | 严重程度 |
|------|------|----------|
| 上下文持久化分散在各 Skill 中实现 | 缺乏统一管理，数据孤岛 | 高 |
| Skills 切换时上下文无法正确保存和恢复 | 用户体验中断，状态丢失 | 高 |
| 知识库 RAG 未与对话自动关联 | 需要手动触发，效率低下 | 中 |
| LLM 调用仍为 Mock 实现 | 无法接入真实 AI 能力 | 高 |

#### 37.1.2 变更目标

建立完整的 LLM 上下文管理体系，实现：

1. **统一存储**: SE 提供集中式上下文持久化服务
2. **无缝切换**: Skills 切换时自动保存/恢复上下文
3. **智能检索**: 对话时自动触发知识库 RAG
4. **真实调用**: LLM Provider 接入真实 API

#### 37.1.3 涉及模块

| 模块 | 负责团队 | 变更前状态 | 变更后状态 |
|------|----------|------------|------------|
| scene-engine (SE) | SE Team | 提供基础存储 | 提供上下文管理服务 |
| skill-llm-core | LLM Team | Mock 实现 | 真实 API 调用 |
| skill-llm-chat | Skills Team | 基础功能 | 完整闭环 |
| skill-knowledge-base | Skills Team | 内存存储 | 持久化 + RAG |

---

### 37.2 方案合理性分析

#### 37.2.1 整体架构评估

**变更原因**: 原有架构中各 Skill 自行管理上下文，导致数据不一致、无法共享、切换丢失等问题。

| 维度 | 评估 | 说明 |
|------|------|------|
| **职责划分** | ✅ 合理 | SE 负责存储、LLM Team 负责调用、Skills Team 负责业务 |
| **接口设计** | ✅ 清晰 | 各团队接口边界明确，依赖关系清晰 |
| **数据流** | ⚠️ 需优化 | 存在循环依赖风险（见矛盾点分析） |
| **实施顺序** | ✅ 合理 | P0→P1→P2 的优先级划分符合依赖关系 |

#### 37.2.2 技术选型评估

**变更原因**: 需要选择轻量级、易部署、适合 MVP 阶段的技术方案。

| 技术 | 评估 | 建议 |
|------|------|------|
| JSON 文件存储 | ⚠️ 临时方案 | 适合 MVP，生产环境建议迁移到数据库 |
| SQLite-Vec | ✅ 合适 | 轻量级向量存储，适合嵌入式场景 |
| 多级配置 | ✅ 灵活 | SCENE_STEP > SCENE > SCENE_GROUP > PERSONAL > ENTERPRISE > SYSTEM |

---

### 37.3 矛盾点识别与解决方案

#### 37.3.1 矛盾点 1：循环依赖风险

**问题描述**:
```
SE Team 的 ContextStorageService
    ↑ 依赖
LLM Team 的 ContextBuilderService (需要加载存储的上下文)
    ↑ 依赖
SE Team 的存储实现
```

**变更原因**: ContextBuilderService 需要访问存储的上下文数据，但直接依赖会导致循环依赖。

**解决方案**:
```java
// 建议：ContextBuilderService 只依赖接口，不依赖具体实现
public interface ContextBuilderService {
    /**
     * 构建完整上下文
     * @param request 构建请求
     * @param loadedContext 已加载的上下文数据（由调用方提供）
     * @return 合并后的上下文
     */
    MergedContext buildContext(ContextBuildRequest request, 
                               Map<String, Object> loadedContext);
}

// SE Team 负责调用，打破循环依赖
@RestController
public class ContextController {
    @Autowired
    private ContextStorageService storageService;
    @Autowired
    private ContextBuilderService builderService;
    
    @GetMapping("/api/v1/context/{sessionId}/build")
    public Result<MergedContext> build(@PathVariable String sessionId,
                                        @RequestBody ContextBuildRequest request) {
        // SE Team 加载数据
        Map<String, Object> context = storageService.loadSessionContext(sessionId);
        // 传递给 LLM Team 构建
        MergedContext merged = builderService.buildContext(request, context);
        return Result.success(merged);
    }
}
```

#### 37.3.2 矛盾点 2：Token 限制与上下文完整性冲突

**问题描述**:
- 文档规定 `MAX_TOKENS = 4096`
- 但上下文可能包含：用户配置 + 会话历史 + Skill 状态 + 知识库 RAG 结果
- 简单截断可能导致关键信息丢失

**变更原因**: LLM API 有 Token 限制，但业务需要完整的上下文信息。

**冲突示例**:
```
系统提示: 500 tokens
对话历史 (10轮): 2000 tokens
知识库 RAG (5条): 1500 tokens
页面状态: 300 tokens
总计: 4300 tokens > 4096 限制
```

**解决方案**:
```java
@Component
public class SmartContextTruncator {
    
    private static final int MAX_TOKENS = 4096;
    private static final double SAFETY_MARGIN = 0.9; // 预留 10% 安全余量
    
    public String truncate(List<ContextData> contexts) {
        int effectiveLimit = (int) (MAX_TOKENS * SAFETY_MARGIN);
        
        // 按优先级排序（高优先级保留）
        List<ContextPriority> priorities = Arrays.asList(
            ContextPriority.SYSTEM_PROMPT,      // 必须保留
            ContextPriority.CURRENT_QUERY,      // 必须保留
            ContextPriority.RAG_RESULTS,        // 高优先级
            ContextPriority.RECENT_HISTORY,     // 中优先级
            ContextPriority.PAGE_STATE,         // 低优先级
            ContextPriority.OLD_HISTORY         // 可裁剪
        );
        
        int remainingTokens = effectiveLimit;
        StringBuilder result = new StringBuilder();
        
        for (ContextPriority priority : priorities) {
            ContextData data = findByPriority(contexts, priority);
            if (data == null) continue;
            
            int tokens = countTokens(data);
            if (tokens <= remainingTokens) {
                result.append(format(data));
                remainingTokens -= tokens;
            } else if (priority.isTruncatable()) {
                // 可裁剪的内容进行智能摘要
                String summary = summarize(data, remainingTokens);
                result.append(summary);
                break; // 后续低优先级内容丢弃
            }
        }
        
        return result.toString();
    }
    
    private int countTokens(ContextData data) {
        // 使用 tiktoken 或近似算法计算 Token 数
        return TikTokenUtil.count(data.toString());
    }
    
    private String summarize(ContextData data, int maxTokens) {
        // 对长内容进行摘要处理
        String content = data.getContent();
        if (countTokens(content) <= maxTokens) {
            return content;
        }
        // 提取关键信息，生成摘要
        return extractKeyPoints(content, maxTokens);
    }
}
```

#### 37.3.3 矛盾点 3：Skills 切换时的状态同步时机

**问题描述**:
文档中的切换流程：`beforeSwitch()` -> 页面导航 -> `afterSwitch()`

**变更原因**: 页面导航是同步的，可能导致 `beforeSwitch()` 的异步保存未完成就跳转，导致状态丢失。

**解决方案**:
```java
// 后端：提供同步保存接口
@RestController
@RequestMapping("/api/v1/skills")
public class SkillSwitchController {
    
    @Autowired
    private SkillSwitchHandler switchHandler;
    
    @PostMapping("/switch")
    public Result<SwitchResponse> switchSkill(@RequestBody SwitchRequest request) {
        String fromSkillId = request.getFromSkillId();
        String toSkillId = request.getToSkillId();
        String sessionId = request.getSessionId();
        
        // 1. 同步执行切换前处理
        switchHandler.beforeSwitch(fromSkillId, toSkillId, sessionId);
        
        // 2. 获取全局上下文
        GlobalContext globalContext = switchHandler.getGlobalContext(
            request.getUserId()
        );
        
        // 3. 返回切换响应（前端收到后再跳转）
        return Result.success(SwitchResponse.builder()
            .success(true)
            .targetUrl("/console/skills/" + toSkillId + "/pages/index.html")
            .globalContext(globalContext)
            .build()
        );
    }
}
```

```javascript
// 前端：确保保存完成后再跳转
async function switchSkill(toSkillId) {
    const currentContext = pageContextCollector.collect();
    
    try {
        // 显示加载状态，防止用户重复点击
        showLoading('保存状态中...');
        
        // 调用后端同步接口
        const response = await fetch('/api/v1/skills/switch', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                fromSkillId: currentContext.pageType.skillId,
                toSkillId: toSkillId,
                sessionId: currentSessionId,
                pageState: currentContext
            })
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            // 后端确认保存成功后再跳转
            window.location.href = result.data.targetUrl;
        } else {
            hideLoading();
            showError('切换失败: ' + result.message);
        }
    } catch (error) {
        hideLoading();
        // 保存失败提示用户确认
        showConfirm('状态保存失败，是否继续切换？', () => {
            window.location.href = `/console/skills/${toSkillId}/pages/index.html`;
        });
    }
}
```

#### 37.3.4 矛盾点 4：知识库 RAG 与会话自动关联的触发条件

**问题描述**:
文档要求："对话时自动触发知识库 RAG"，但没有定义"何时触发"的具体规则。

**变更原因**: 所有对话都触发 RAG 会造成性能浪费和 Token 浪费。

**解决方案**:
```java
@Component
public class RagTrigger {
    
    // 知识库相关关键词
    private static final List<String> KNOWLEDGE_KEYWORDS = Arrays.asList(
        "怎么", "如何", "什么是", "为什么", "请解释",
        "说明", "介绍", "帮助", "文档", "指南"
    );
    
    // 简单问候词
    private static final List<String> GREETINGS = Arrays.asList(
        "你好", "嗨", "hello", "hi", "在吗", "您好"
    );
    
    public boolean shouldTrigger(String userQuery, SessionContext context) {
        // 规则 1: 问题长度检查（太短的问题不需要 RAG）
        if (userQuery.length() < 10) {
            return false;
        }
        
        // 规则 2: 检查是否是简单问候
        boolean isGreeting = GREETINGS.stream()
            .anyMatch(greeting -> userQuery.toLowerCase().contains(greeting.toLowerCase()));
        if (isGreeting && userQuery.length() < 20) {
            return false;
        }
        
        // 规则 3: 包含知识库相关关键词
        boolean hasKeyword = KNOWLEDGE_KEYWORDS.stream()
            .anyMatch(keyword -> userQuery.contains(keyword));
        
        // 规则 4: 用户显式启用知识库
        Boolean useKnowledge = context.getUseKnowledgeFlag();
        
        // 规则 5: 历史对话中使用了知识库（延续使用）
        boolean usedInHistory = context.getRecentMessages(3).stream()
            .anyMatch(msg -> msg.hasRagResults());
        
        return hasKeyword || Boolean.TRUE.equals(useKnowledge) || usedInHistory;
    }
    
    public RagOptions buildOptions(String userQuery) {
        // 根据问题复杂度调整检索参数
        RagOptions options = new RagOptions();
        
        if (userQuery.length() > 100) {
            // 复杂问题，检索更多文档
            options.setTopK(8);
            options.setRerank(true);
        } else {
            // 简单问题，减少检索
            options.setTopK(3);
            options.setRerank(false);
        }
        
        return options;
    }
}
```

#### 37.3.5 矛盾点 5：并发访问的文件锁问题

**问题描述**:
使用 JSON 文件存储，多用户并发时可能产生竞态条件。

**变更原因**: 文件系统不支持并发写入，需要手动实现锁机制。

**冲突场景**:
```
用户A读取 context.json -> 用户B读取 context.json 
-> 用户A修改并保存 -> 用户B修改并保存（覆盖A的修改）
```

**解决方案**:
```java
@Component
public class JsonStorageService implements ContextStorageService {
    
    // 会话级别的读写锁
    private final Map<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
    
    // 锁过期清理（防止内存泄漏）
    private final ScheduledExecutorService cleanupExecutor = 
        Executors.newSingleThreadScheduledExecutor();
    
    @PostConstruct
    public void init() {
        // 每小时清理一次过期锁
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredLocks, 1, 1, TimeUnit.HOURS);
    }
    
    @Override
    public void saveSessionContext(String sessionId, Map<String, Object> context) {
        ReentrantReadWriteLock lock = locks.computeIfAbsent(
            sessionId, k -> new ReentrantReadWriteLock()
        );
        
        lock.writeLock().lock();
        try {
            // 写入文件
            File file = getSessionFile(sessionId);
            writeToFile(file, context);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Map<String, Object> loadSessionContext(String sessionId) {
        ReentrantReadWriteLock lock = locks.get(sessionId);
        if (lock == null) {
            // 无锁时直接读取
            return readFromFile(getSessionFile(sessionId));
        }
        
        lock.readLock().lock();
        try {
            return readFromFile(getSessionFile(sessionId));
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void deleteSession(String sessionId) {
        ReentrantReadWriteLock lock = locks.remove(sessionId);
        if (lock != null) {
            lock.writeLock().lock();
            try {
                File file = getSessionFile(sessionId);
                if (file.exists()) {
                    file.delete();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
    
    private void cleanupExpiredLocks() {
        // 清理长时间未使用的锁
        locks.entrySet().removeIf(entry -> {
            ReentrantReadWriteLock lock = entry.getValue();
            return !lock.isWriteLocked() && lock.getReadLockCount() == 0;
        });
    }
}
```

#### 37.3.6 矛盾点 6：LLM Provider 配置与 Skill 配置的冲突

**问题描述**:
- LLM Team 提供多级配置（SYSTEM > ENTERPRISE > PERSONAL > SCENE > SCENE_STEP）
- Skills Team 的 Skill 可能有自己的 LLM 配置需求

**变更原因**: 配置优先级冲突，需要明确 Skill 配置的优先级位置。

**解决方案**:
```java
@Component
public class ConfigResolver {
    
    // 配置优先级（从低到高，后面的覆盖前面的）
    private static final List<ConfigSource> PRIORITY_ORDER = Arrays.asList(
        ConfigSource.SYSTEM,        // 系统默认配置
        ConfigSource.ENTERPRISE,    // 企业级配置
        ConfigSource.PERSONAL,      // 个人配置
        ConfigSource.SCENE,         // 场景配置
        ConfigSource.SKILL,         // Skill 配置（新增）
        ConfigSource.SCENE_STEP     // 步骤配置（最高优先级）
    );
    
    @Autowired
    private LlmConfigRepository configRepository;
    
    @Autowired
    private SkillConfigService skillConfigService;
    
    public ResolvedConfig resolve(ConfigResolveRequest request) {
        LlmConfig mergedConfig = new LlmConfig();
        Map<ConfigSource, LlmConfig> allConfigs = new HashMap<>();
        
        // 1. 加载各级配置
        for (ConfigSource source : PRIORITY_ORDER) {
            LlmConfig config = loadConfig(source, request);
            if (config != null) {
                allConfigs.put(source, config);
                // 后面的配置覆盖前面的
                mergeConfig(mergedConfig, config);
            }
        }
        
        // 2. 加载 Skill 特定配置（如果指定了 Skill）
        if (request.getSkillId() != null) {
            LlmConfig skillConfig = skillConfigService.getConfig(request.getSkillId());
            if (skillConfig != null) {
                allConfigs.put(ConfigSource.SKILL, skillConfig);
                mergeConfig(mergedConfig, skillConfig);
            }
        }
        
        return ResolvedConfig.builder()
            .effectiveConfig(mergedConfig)
            .allConfigs(allConfigs)
            .build();
    }
    
    private LlmConfig loadConfig(ConfigSource source, ConfigResolveRequest request) {
        switch (source) {
            case SYSTEM:
                return configRepository.findSystemConfig();
            case ENTERPRISE:
                return configRepository.findEnterpriseConfig(request.getEnterpriseId());
            case PERSONAL:
                return configRepository.findPersonalConfig(request.getUserId());
            case SCENE:
                return configRepository.findSceneConfig(request.getSceneId());
            case SCENE_STEP:
                return configRepository.findSceneStepConfig(request.getSceneStepId());
            default:
                return null;
        }
    }
}
```

---

### 37.4 开发任务安排

#### 37.4.1 实施计划

**变更原因**: 需要按优先级和依赖关系分阶段实施，降低风险。

```
Phase 1: 基础设施 (3天)
├── SE-001: ContextStorageService 接口定义 (0.5d)
├── SE-002: JsonStorageService 实现 (1.5d)
├── SE-003: 并发文件锁机制 (1d)
├── LLM-001: LlmProvider 接口完善 (0.5d)
└── SK-001: PageContextCollector 前端实现 (1d)

Phase 2: 核心服务 (4天)
├── SE-004: SkillSwitchHandler 实现 (2d)
├── SE-005: KnowledgeStorageService 实现 (1d)
├── LLM-002: OpenAI Provider 真实实现 (1.5d)
├── LLM-003: 通义千问 Provider 真实实现 (1.5d)
├── LLM-004: LlmConfigService 实现 (1d)
├── LLM-005: ContextBuilderService 实现 (2d)
└── SK-002: Skill切换流程集成 (2d)

Phase 3: RAG 集成 (3天)
├── SK-003: SQLite-Vec 集成 (2d)
├── SK-004: RagService 实现 (2d)
└── SK-005: 对话 RAG 触发集成 (1d)
```

#### 37.4.2 关键里程碑

| 里程碑 | 日期 | 验收标准 | 变更验证 |
|--------|------|----------|----------|
| M1: 存储服务就绪 | Day 3 | ContextStorageService 所有方法通过单元测试 | 数据正确持久化到 `data/` 目录 |
| M2: LLM 真实调用 | Day 5 | OpenAI 和千问 Provider 成功调用真实 API | 返回非 Mock 的真实 AI 响应 |
| M3: 上下文构建 | Day 7 | ContextBuilderService 能正确合并多级上下文 | Token 限制内包含所有关键信息 |
| M4: Skills 切换 | Day 7 | 切换 Skill 时状态正确保存/恢复 | 页面刷新后状态不丢失 |
| M5: RAG 集成 | Day 10 | 对话时自动触发 RAG，返回带来源的回答 | 相关问题时自动引用知识库 |

#### 37.4.3 风险应对

| 风险 | 概率 | 影响 | 应对措施 | 变更回滚方案 |
|------|------|------|----------|--------------|
| 文件存储性能瓶颈 | 中 | 高 | Day 3 评估性能，必要时引入 SQLite 或 Redis | 保留原有数据库存储接口 |
| LLM API 不稳定 | 高 | 中 | 实现熔断降级，Mock 模式作为 fallback | 配置开关切回 Mock 模式 |
| Token 计算不准确 | 中 | 高 | 使用 tiktoken 库，预留 10% 安全余量 | 动态调整安全余量参数 |
| 跨团队接口变更 | 中 | 高 | 每日站会同步，接口变更需提前 1 天通知 | 版本化接口，保持向后兼容 |

---

### 37.5 接口定义

#### 37.5.1 ContextStorageService

**变更原因**: 提供统一的上下文存储接口，替代各 Skill 自行实现的存储逻辑。

```java
package net.ooder.scene.skill.engine.context;

/**
 * 上下文存储服务 - SE 核心服务
 * 变更说明: 新增接口，统一上下文持久化管理
 */
public interface ContextStorageService {

    // ========== 用户上下文 ==========
    
    /**
     * 保存用户上下文
     * @param userId 用户ID
     * @param context 上下文数据
     */
    void saveUserContext(String userId, Map<String, Object> context);
    
    /**
     * 加载用户上下文
     * @param userId 用户ID
     * @return 上下文数据，不存在返回空Map
     */
    Map<String, Object> loadUserContext(String userId);
    
    // ========== 会话上下文 ==========
    
    void saveSessionContext(String sessionId, Map<String, Object> context);
    Map<String, Object> loadSessionContext(String sessionId);
    boolean sessionExists(String sessionId);
    void deleteSession(String sessionId);
    
    // ========== Skill 上下文 ==========
    
    void saveSkillContext(String skillId, String sessionId, Map<String, Object> context);
    Map<String, Object> loadSkillContext(String skillId, String sessionId);
    
    // ========== 对话历史 ==========
    
    void saveChatMessage(String sessionId, Map<String, Object> message);
    List<Map<String, Object>> loadChatHistory(String sessionId, int limit);
    
    // ========== 页面状态 ==========
    
    void savePageState(String sessionId, String pageId, Map<String, Object> state);
    Map<String, Object> loadPageState(String sessionId, String pageId);
}
```

#### 37.5.2 SkillSwitchHandler

**变更原因**: 统一管理 Skills 切换时的状态保存和恢复逻辑。

```java
/**
 * Skill 切换处理器
 * 变更说明: 新增接口，处理切换前后的状态同步
 */
public interface SkillSwitchHandler {
    
    /**
     * 切换前处理
     * - 保存当前 Skill 的页面状态
     * - 保存当前 Skill 的上下文
     * - 更新会话的 currentSkill
     */
    void beforeSwitch(String fromSkillId, String toSkillId, String sessionId);
    
    /**
     * 切换后处理
     * - 恢复目标 Skill 的上下文
     * - 恢复目标 Skill 的页面状态
     * - 更新菜单高亮
     */
    void afterSwitch(String fromSkillId, String toSkillId, String sessionId);
    
    /**
     * 获取全局共享上下文
     */
    GlobalContext getGlobalContext(String userId);
}
```

#### 37.5.3 LlmProvider

**变更原因**: 将 Mock 实现替换为真实 API 调用。

```java
/**
 * LLM 提供者接口
 * 变更说明: 完善接口，支持真实 API 调用
 */
public interface LlmProvider {
    
    String getProviderType();
    List<String> getSupportedModels();
    
    /**
     * 对话补全
     * 变更: 从 Mock 返回改为真实 API 调用
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 流式对话 (SSE)
     * 变更: 新增流式支持
     */
    void chatStream(ChatRequest request, StreamHandler handler);
    
    /**
     * 文本嵌入
     */
    List<float[]> embed(String model, List<String> texts);
    
    /**
     * 健康检查
     * 变更: 新增健康检查接口
     */
    boolean healthCheck();
}
```

#### 37.5.4 RagService

**变更原因**: 将手动触发的 RAG 改为自动触发。

```java
/**
 * 检索增强生成服务
 * 变更说明: 新增服务，实现对话时自动触发 RAG
 */
public interface RagService {
    
    /**
     * RAG 查询
     * @param query 用户问题
     * @param kbIds 知识库ID列表
     * @param options 检索选项
     * @return 增强后的回答
     */
    RagResponse query(String query, List<String> kbIds, RagOptions options);
    
    /**
     * 流式 RAG 查询
     */
    void queryStream(String query, List<String> kbIds, 
                     RagOptions options, StreamHandler handler);
    
    /**
     * 判断是否触发 RAG
     * 变更: 新增智能触发判断
     */
    boolean shouldTrigger(String query, SessionContext context);
}
```

---

### 37.6 存储结构

**变更原因**: 统一存储目录结构，替代分散的存储方式。

```
data/
├── users/
│   └── {userId}.json           # 用户配置和偏好
│
├── sessions/
│   └── {sessionId}/
│       ├── context.json        # 会话上下文
│       ├── chat-history.json   # 对话历史
│       └── pages/
│           └── {pageId}.json   # 页面状态
│
├── skills/
│   └── {skillId}/
│       └── {sessionId}/
│           └── context.json    # Skill 特定上下文
│
└── knowledge/
    └── {kbId}/
        ├── documents/
        │   ├── {docId}.json    # 文档元数据
        │   └── {docId}.txt     # 文档内容
        └── index/
            └── vectors.db      # SQLite-Vec 向量索引
```

---

### 37.7 验收标准

#### 37.7.1 SE Team

- [x] ContextStorageService 所有方法可用
- [x] 数据正确持久化到 `data/` 目录
- [x] Skills 切换时上下文正确保存/恢复
- [x] 知识库文档正确存储和检索

#### 37.7.2 LLM Team

- [x] OpenAI Provider 真实 API 调用成功
- [x] 通义千问 Provider 真实 API 调用成功
- [x] 流式输出正常工作
- [x] 多级配置正确解析

#### 37.7.3 Skills Team

- [x] 对话历史正确持久化
- [x] 知识库 RAG 自动触发
- [x] Skills 切换无状态丢失
- [x] AI 助手上下文正确构建

---

## 三十八、SKILLS-LLM 集成开发指南

### 38.1 架构说明

#### 38.1.1 层级划分

```
┌─────────────────────────────────────────────────────────────┐
│                    SKILLS-LLM 工程                          │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │  Web Controller │  │  Service Layer  │                  │
│  │  (页面CRUD)     │  │  (业务逻辑)     │                  │
│  └────────┬────────┘  └────────┬────────┘                  │
│           │                    │                           │
│           └────────────────────┘                           │
│                      │                                     │
│                      ▼ 调用 SE 层 Service                   │
├─────────────────────────────────────────────────────────────┤
│                    SE 工程 (scene-engine)                   │
│  ┌─────────────────────────┐  ┌─────────────────────────┐  │
│  │ ContextStorageService   │  │ SkillSwitchHandler      │  │
│  │ (上下文存储API)          │  │ (Skill切换API)          │  │
│  └─────────────────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

#### 38.1.2 职责边界

| 层级 | 职责 | 示例 |
|------|------|------|
| **SKILLS-LLM** | 页面交互、业务逻辑、Web API | Controller、页面状态管理 |
| **SE** | 系统核心服务、数据持久化 | ContextStorageService、SkillSwitchHandler |

**重要**: SE 层不提供 Web 访问，仅通过 Service API 供其他服务调用。

---

### 38.2 SE 层提供的 API

#### 38.2.1 ContextStorageService

**注入方式**:
```java
@Autowired
private ContextStorageService contextStorageService;
```

**核心方法**:

```java
// 会话上下文
void saveSessionContext(String sessionId, Map<String, Object> context);
Map<String, Object> loadSessionContext(String sessionId);

// Skill 上下文
void saveSkillContext(String skillId, String sessionId, Map<String, Object> context);
Map<String, Object> loadSkillContext(String skillId, String sessionId);

// 页面状态
void savePageState(String sessionId, String pageId, Map<String, Object> state);
Map<String, Object> loadPageState(String sessionId, String pageId);

// 对话历史
void saveChatMessage(String sessionId, Map<String, Object> message);
List<Map<String, Object>> loadChatHistory(String sessionId, int limit);

// 用户上下文
void saveUserContext(String userId, Map<String, Object> context);
Map<String, Object> loadUserContext(String userId);
```

#### 38.2.2 SkillSwitchHandler

**注入方式**:
```java
@Autowired
private SkillSwitchHandler skillSwitchHandler;
```

**核心方法**:

```java
/**
 * 执行完整的 Skill 切换流程
 */
SwitchResult switchSkill(SwitchRequest request);

/**
 * 获取全局上下文
 */
GlobalContext getGlobalContext(String userId);

/**
 * 切换前处理（如需细粒度控制）
 */
Map<String, Object> beforeSwitch(String fromSkillId, String toSkillId, String sessionId);

/**
 * 切换后处理（如需细粒度控制）
 */
void afterSwitch(String fromSkillId, String toSkillId, String sessionId, Map<String, Object> context);
```

**SwitchRequest 示例**:
```java
SkillSwitchHandler.SwitchRequest request = new SkillSwitchHandler.SwitchRequest();
request.setFromSkillId("recruitment-skill");
request.setToSkillId("interview-skill");
request.setSessionId("session-xxx");
request.setUserId("user-xxx");
request.setCurrentPageId("resume-list");
request.setCurrentPageState(pageState);

SkillSwitchHandler.SwitchResult result = skillSwitchHandler.switchSkill(request);
if (result.isSuccess()) {
    // 切换成功
    GlobalContext globalContext = result.getGlobalContext();
    Map<String, Object> restoredContext = result.getRestoredContext();
}
```

---

### 38.3 SKILLS-LLM 开发任务

#### 38.3.1 需要实现的组件

| 组件 | 说明 | 依赖 SE API |
|------|------|-------------|
| `ContextController` | Web 层，提供页面 CRUD API | ContextStorageService |
| `ContextBuilderService` | 构建 LLM 上下文 | loadSessionContext, loadSkillContext |
| `PageContextCollector` | 前端页面状态收集 | - |
| `LlmProvider` 实现 | OpenAI/千问 Provider | - |
| `RagService` | 知识库 RAG 服务 | ContextStorageService |

#### 38.3.2 ContextController 示例

```java
@RestController
@RequestMapping("/api/v1/context")
public class ContextController {
    
    @Autowired
    private ContextStorageService contextStorageService;
    
    @Autowired
    private SkillSwitchHandler skillSwitchHandler;
    
    // 页面级 API - 保存页面状态
    @PostMapping("/sessions/{sessionId}/pages/{pageId}")
    public Result<Void> savePageState(@PathVariable String sessionId,
                                      @PathVariable String pageId,
                                      @RequestBody Map<String, Object> state) {
        contextStorageService.savePageState(sessionId, pageId, state);
        return Result.success();
    }
    
    // 页面级 API - 加载页面状态
    @GetMapping("/sessions/{sessionId}/pages/{pageId}")
    public Result<Map<String, Object>> loadPageState(@PathVariable String sessionId,
                                                     @PathVariable String pageId) {
        Map<String, Object> state = contextStorageService.loadPageState(sessionId, pageId);
        return Result.success(state);
    }
    
    // Skill 切换 API
    @PostMapping("/skills/switch")
    public Result<SwitchResult> switchSkill(@RequestBody SwitchRequest request) {
        SwitchResult result = skillSwitchHandler.switchSkill(request);
        return Result.success(result);
    }
    
    // 获取全局上下文
    @GetMapping("/users/{userId}/global")
    public Result<GlobalContext> getGlobalContext(@PathVariable String userId) {
        GlobalContext context = skillSwitchHandler.getGlobalContext(userId);
        return Result.success(context);
    }
}
```

#### 38.3.3 ContextBuilderService 示例

```java
@Service
public class ContextBuilderService {
    
    @Autowired
    private ContextStorageService storageService;
    
    /**
     * 构建 LLM 对话上下文
     */
    public MergedContext buildContext(String sessionId, ContextBuildRequest request) {
        MergedContext merged = new MergedContext();
        
        // 1. 从 SE 加载会话上下文
        Map<String, Object> sessionContext = storageService.loadSessionContext(sessionId);
        merged.setSessionContext(sessionContext);
        
        // 2. 加载 Skill 上下文
        String skillId = (String) sessionContext.get("currentSkill");
        if (skillId != null) {
            Map<String, Object> skillContext = storageService.loadSkillContext(skillId, sessionId);
            merged.setSkillContext(skillContext);
        }
        
        // 3. 加载对话历史
        List<Map<String, Object>> history = storageService.loadChatHistory(sessionId, 10);
        merged.setChatHistory(history);
        
        // 4. 构建最终提示词
        String prompt = buildPrompt(merged, request);
        merged.setFinalPrompt(prompt);
        
        return merged;
    }
}
```

---

### 38.4 集成注意事项

#### 38.4.1 依赖配置

在 SKILLS-LLM 工程的 `pom.xml` 中添加 SE 依赖:

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

#### 38.4.2 存储路径配置

SE 层使用 `data/` 目录作为默认存储根路径，可通过配置修改:

```yaml
# application.yml
scene:
  engine:
    context:
      storage:
        root: /custom/data/path
```

#### 38.4.3 并发安全

SE 层的 `JsonStorageService` 已实现基于 `ReentrantReadWriteLock` 的并发控制，SKILLS-LLM 无需额外处理并发问题。

#### 38.4.4 错误处理

SE 层 API 在异常时会抛出 `RuntimeException`，SKILLS-LLM 应进行适当捕获和处理:

```java
try {
    contextStorageService.saveSessionContext(sessionId, context);
} catch (Exception e) {
    logger.error("Failed to save context", e);
    // 业务层错误处理
    throw new BusinessException("保存上下文失败", e);
}
```

---

### 38.5 开发顺序建议

```
Phase 1: 基础集成 (2天)
├── 1. 添加 SE 依赖
├── 2. 实现 ContextController (Web 层)
└── 3. 验证上下文存储/读取

Phase 2: Skill 切换 (2天)
├── 1. 集成 SkillSwitchHandler
├── 2. 实现页面状态收集
└── 3. 验证切换流程

Phase 3: LLM 集成 (3天)
├── 1. 实现 ContextBuilderService
├── 2. 实现 LlmProvider (OpenAI/千问)
└── 3. 验证对话流程

Phase 4: RAG 集成 (3天)
├── 1. 实现 RagService
├── 2. 集成 SQLite-Vec
└── 3. 验证知识库检索
```

---

### 38.6 常见问题

#### Q1: SE 层是否提供 REST API?
**A**: 不提供。SE 层仅提供 Service API，Web 层由 SKILLS-LLM 实现。

#### Q2: 如何调试上下文存储?
**A**: 检查 `data/` 目录下的 JSON 文件，或使用 `ContextStorageService.getStorageRoot()` 获取存储路径。

#### Q3: Skill 切换时状态丢失?
**A**: 确保在切换前调用 `savePageState()` 保存当前页面状态，并通过 `SkillSwitchHandler.switchSkill()` 执行完整切换流程。

#### Q4: 如何清理过期数据?
**A**: 调用 `ContextStorageService.cleanupExpiredData(maxAgeDays)` 方法。

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-14
