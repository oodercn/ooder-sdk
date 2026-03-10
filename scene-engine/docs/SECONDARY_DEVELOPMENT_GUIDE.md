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

**文档维护**: Ooder Team  
**最后更新**: 2026-03-09
