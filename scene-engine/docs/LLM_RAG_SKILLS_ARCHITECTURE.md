# LLM + RAG + Skills 架构设计规范

**版本**: v2.3  
**日期**: 2026-03-06  
**状态**: 架构设计

---

## 一、架构概述

### 1.1 核心理念

本架构遵循**关注点分离**和**单一职责**原则，将 AI 系统划分为三个核心层次：

1. **模型服务层 (Model Service Layer)** - 提供 AI 基础能力
2. **知识增强层 (Knowledge Augmentation Layer)** - 提供领域知识支持
3. **技能执行层 (Skill Execution Layer)** - 提供业务能力封装

### 1.2 架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           应用层 (Application Layer)                     │
│                        SceneSkill / RichSkill / Agent                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                    技能执行层 (Skill Layer)                         │ │
│  │                                                                     │ │
│  │   ┌─────────────────┐                                              │ │
│  │   │  Function Call  │ ────▶ Skill Execution ────▶ Business Logic  │ │
│  │   │    Interface    │       (Direct Invoke)                        │ │
│  │   └─────────────────┘                                              │ │
│  │           │                                                         │ │
│  │           │ Tool Definition                                         │ │
│  │           ▼                                                         │ │
│  │   ┌─────────────────────────────────────────────────────────────┐  │ │
│  │   │                    Tool Registry                             │  │ │
│  │   │  - Knowledge Search Tool                                     │  │ │
│  │   │  - Database Query Tool                                       │  │ │
│  │   │  - API Call Tool                                             │  │ │
│  │   │  - Custom Business Tools                                     │  │ │
│  │   └─────────────────────────────────────────────────────────────┘  │ │
│  │                                                                     │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│                                    │ Tool Invocation                     │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                 知识增强层 (Knowledge Layer)                        │ │
│  │                                                                     │ │
│  │   ┌──────────────────────────────────────────────────────────────┐ │ │
│  │   │                  RAG Pipeline                                │ │ │
│  │   │                                                              │ │ │
│  │   │   Query ──▶ Embed ──▶ Retrieve ──▶ Rerank ──▶ Augment ──▶   │ │ │
│  │   │                                                Context        │ │ │
│  │   │                                                              │ │ │
│  │   └──────────────────────────────────────────────────────────────┘ │ │
│  │                                    │                                │ │
│  │              ┌─────────────────────┼─────────────────────┐         │ │
│  │              ▼                     ▼                     ▼         │ │
│  │   ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐ │ │
│  │   │  Knowledge Base  │  │   Vector Store   │  │  Document Store  │ │ │
│  │   │                  │  │                  │  │                  │ │ │
│  │   │  - KB Management │  │  - Indexing      │  │  - Storage       │ │ │
│  │   │  - Metadata      │  │  - Similarity    │  │  - Retrieval     │ │ │
│  │   │  - Permissions   │  │  - Filtering     │  │  - Chunking      │ │ │
│  │   └──────────────────┘  └──────────────────┘  └──────────────────┘ │ │
│  │                                                                     │ │
│  │   职责：提供领域知识存储、检索和增强能力                              │ │
│  │                                                                     │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│                                    │ Embedding API                       │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                  模型服务层 (Model Layer)                           │ │
│  │                                                                     │ │
│  │   ┌──────────────────────────────────────────────────────────────┐ │ │
│  │   │                    LLM Service                               │ │ │
│  │   │                                                              │ │ │
│  │   │   Core Capabilities:                                        │ │ │
│  │   │   - Chat Completion (对话补全)                               │ │ │
│  │   │   - Text Embedding (文本向量化)                              │ │ │
│  │   │   - Function Calling (函数调用)                              │ │ │
│  │   │   - Streaming Output (流式输出)                              │ │ │
│  │   │                                                              │ │ │
│  │   │   Multi-Model Support:                                      │ │ │
│  │   │   - Baidu Wenxin (百度文心)                                  │ │ │
│  │   │   - iFlytek Spark (讯飞星火)                                 │ │ │
│  │   │   - OpenAI Compatible (OpenAI 兼容)                          │ │ │
│  │   │                                                              │ │ │
│  │   └──────────────────────────────────────────────────────────────┘ │ │
│  │                                                                     │ │
│  │   职责：提供 AI 基础能力，不包含业务逻辑                              │ │
│  │                                                                     │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
├─────────────────────────────────────────────────────────────────────────┤
│                        基础设施层 (Infrastructure Layer)                  │
│                                                                          │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│   │ Vector DB    │  │ Relational   │  │ Object       │  │ Message    │ │
│   │ (Milvus)     │  │ DB (PG)      │  │ Storage (S3) │  │ Queue      │ │
│   └──────────────┘  └──────────────┘  └──────────────┘  └────────────┘ │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 二、层次职责定义

### 2.1 模型服务层 (Model Service Layer)

**定位**: AI 基础能力提供者，与具体业务解耦

**核心职责**:
- 提供文本生成能力 (Chat Completion)
- 提供文本向量化能力 (Text Embedding)
- 提供工具调用能力 (Function Calling / Tool Use)
- 提供多模型适配能力 (Model Adapter)

**接口定义**:

```java
/**
 * LLM 服务接口 - 模型服务层核心接口
 */
public interface LlmService {
    
    // ========== 对话能力 ==========
    
    /**
     * 同步对话补全
     */
    String chat(ChatRequest request);
    
    /**
     * 异步对话补全
     */
    CompletableFuture<String> chatAsync(ChatRequest request);
    
    /**
     * 流式对话补全
     */
    void chatStream(ChatRequest request, StreamHandler handler);
    
    // ========== 向量化能力 ==========
    
    /**
     * 单文本向量化
     */
    float[] embed(String text);
    
    /**
     * 批量文本向量化
     */
    List<float[]> embedBatch(List<String> texts);
    
    // ========== 工具调用能力 ==========
    
    /**
     * 带工具调用的对话
     */
    String chatWithTools(ChatRequest request, List<ToolDefinition> tools);
    
    // ========== 模型管理 ==========
    
    List<String> getAvailableModels();
    void setModel(String modelId);
    TokenUsage getTokenUsage();
}
```

**设计原则**:
- ✅ 只提供原子能力，不包含业务逻辑
- ✅ 支持多模型切换，屏蔽底层差异
- ✅ 向量化是模型能力的一部分，不是独立服务

### 2.2 知识增强层 (Knowledge Augmentation Layer)

**定位**: 领域知识管理，为 LLM 提供上下文增强

**核心职责**:
- 知识库管理 (Knowledge Base Management)
- 文档处理与索引 (Document Processing & Indexing)
- 向量存储与检索 (Vector Storage & Retrieval)
- 检索增强生成 (Retrieval-Augmented Generation)

**组件关系**:

```
┌─────────────────────────────────────────────────────────────┐
│                     RAG Pipeline                            │
│                                                             │
│  Query ──▶ Embed ──▶ Retrieve ──▶ Rerank ──▶ Augment      │
│             │           │            │           │          │
│             ▼           ▼            ▼           ▼          │
│          LLM       VectorStore   Reranker   PromptBuilder  │
│        (embed)    (search)      (optional)                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
         │                    │                    │
         ▼                    ▼                    ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  KnowledgeBase  │  │  VectorStore    │  │ DocumentStore   │
│                 │  │                 │  │                 │
│ - KB 元数据     │  │ - 向量索引      │  │ - 原始文档      │
│ - 权限管理      │  │ - 相似度检索    │  │ - 分块存储      │
│ - 文档关联      │  │ - 元数据过滤    │  │ - 内容检索      │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

**接口定义**:

```java
/**
 * 知识库接口 - 知识管理
 */
public interface KnowledgeBaseApi {
    // 知识库管理
    KnowledgeBase create(KnowledgeBaseCreateRequest request);
    KnowledgeBase get(String kbId);
    void delete(String kbId);
    List<KnowledgeBase> list(String ownerId);
    
    // 文档管理
    Document addDocument(String kbId, DocumentCreateRequest request);
    void deleteDocument(String kbId, String docId);
    List<Document> listDocuments(String kbId);
    
    // 索引管理
    void rebuildIndex(String kbId);
    IndexStatus getIndexStatus(String kbId);
}

/**
 * 向量存储接口 - 向量检索
 */
public interface VectorStore {
    // 向量操作
    void insert(String id, float[] vector, Map<String, Object> metadata);
    void batchInsert(List<VectorData> vectors);
    void delete(String id);
    
    // 检索操作
    List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters);
    
    // 元数据操作
    int getDimension();
    long count();
}

/**
 * RAG 接口 - 检索增强生成
 */
public interface RagApi {
    // 检索
    RagResult retrieve(RagContext context);
    
    // 增强
    String augmentPrompt(String query, RagResult result);
    
    // 生成
    String generate(String query, RagContext context);
    
    // 知识库管理
    void registerKnowledgeBase(String kbId, KnowledgeBaseConfig config);
    void unregisterKnowledgeBase(String kbId);
}
```

**设计原则**:
- ✅ 知识库管理文档元数据，不直接管理向量
- ✅ 向量库只负责向量存储和检索，不包含业务逻辑
- ✅ RAG 组合 LLM + VectorStore + KnowledgeBase，提供端到端能力
- ✅ 向量化由 LLM 提供，向量库只负责存储

### 2.3 技能执行层 (Skill Execution Layer)

**定位**: 业务能力封装，通过 Function Calling 调用

**核心职责**:
- 定义可调用的工具 (Tool Definition)
- 执行业务逻辑 (Business Logic Execution)
- 调用下层服务 (Service Orchestration)

**工具定义示例**:

```java
/**
 * 知识搜索工具 - 通过 Function Calling 暴露给 LLM
 */
@Tool(name = "search_knowledge", description = "搜索知识库获取相关信息")
public class KnowledgeSearchTool {
    
    private final RagApi ragApi;
    
    @ToolFunction
    public String execute(
        @Param(description = "搜索查询") String query,
        @Param(description = "知识库ID") String kbId,
        @Param(description = "返回结果数量", required = false) Integer topK
    ) {
        RagContext context = RagContext.builder()
            .query(query)
            .kbId(kbId)
            .topK(topK != null ? topK : 5)
            .build();
        
        RagResult result = ragApi.retrieve(context);
        return formatResult(result);
    }
}

/**
 * 数据库查询工具
 */
@Tool(name = "query_database", description = "查询数据库获取结构化数据")
public class DatabaseQueryTool {
    
    private final DatabaseService dbService;
    
    @ToolFunction
    public String execute(
        @Param(description = "SQL查询语句") String sql,
        @Param(description = "数据源ID") String datasourceId
    ) {
        return dbService.query(datasourceId, sql);
    }
}
```

**设计原则**:
- ✅ Skills 通过 Function Calling 暴露给 LLM
- ✅ Skills 可以调用 LLM、RAG、知识库等下层服务
- ✅ Skills 包含业务逻辑，是应用层的核心

---

## 三、关键设计决策

### 3.1 向量化能力归属

**问题**: 向量化应该放在哪一层？

**决策**: 向量化是 LLM 的核心能力，属于模型服务层

**理由**:
1. 向量化模型是 LLM 的一部分（如 text-embedding-ada-002）
2. 不同 LLM 提供者的向量化接口不同，需要适配
3. 向量化结果与 LLM 的对话模型配套使用效果更好

**实现**:

```java
// ✅ 正确：向量化在 LLM 层
public interface LlmService {
    float[] embed(String text);
}

// ❌ 错误：单独的 EmbeddingService
public interface EmbeddingService {
    float[] embed(String text);
}
```

### 3.2 知识库与向量库的关系

**问题**: 知识库和向量库是否重复？

**决策**: 两者职责不同，互补关系

| 组件 | 职责 | 数据类型 |
|------|------|----------|
| KnowledgeBase | 文档管理、元数据、权限 | 文本、元数据 |
| VectorStore | 向量索引、相似度检索 | 向量、索引 |
| DocumentStore | 原始文档存储 | 文件、分块 |

**关系图**:

```
┌─────────────────────────────────────────────────────────────┐
│                      KnowledgeBase                          │
│                                                             │
│  Document ────▶ Chunking ────▶ Embedding ────▶ VectorStore │
│     │              │               │                │       │
│     │              │               │                │       │
│     ▼              ▼               ▼                ▼       │
│  Metadata      Chunk Meta      Vector ID      Vector Index  │
│  (title,       (position,      (doc_id,       (embedding,   │
│   author,       chunk_id)       chunk_id)      metadata)    │
│   tags)                                                     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**数据流**:

```
1. 文档入库流程:
   Document ──▶ KnowledgeBase.addDocument()
                     │
                     ├──▶ DocumentStore.save() (存储原文)
                     │
                     └──▶ Chunking ──▶ LLM.embed() ──▶ VectorStore.insert()

2. 检索流程:
   Query ──▶ LLM.embed() ──▶ VectorStore.search() ──▶ KnowledgeBase.getDocuments()
```

### 3.3 RAG 与 LLM 的关系

**问题**: RAG 是否应该依赖 LLM？

**决策**: RAG 组合 LLM 能力，但不包含 LLM 本身

**依赖关系**:

```java
/**
 * RAG 实现 - 组合模式
 */
public class RagServiceImpl implements RagApi {
    
    private final LlmService llmService;      // 依赖 LLM 的 embed 能力
    private final VectorStore vectorStore;    // 依赖向量存储
    private final KnowledgeBaseApi kbApi;     // 依赖知识库
    
    @Override
    public RagResult retrieve(RagContext context) {
        // 1. 使用 LLM 向量化查询
        float[] queryVector = llmService.embed(context.getQuery());
        
        // 2. 向量检索
        List<SearchResult> results = vectorStore.search(
            queryVector, 
            context.getTopK(), 
            context.getFilters()
        );
        
        // 3. 获取完整文档
        List<Document> documents = results.stream()
            .map(r -> kbApi.getDocument(context.getKbId(), r.getId()))
            .collect(Collectors.toList());
        
        return new RagResult(documents, results);
    }
    
    @Override
    public String generate(String query, RagContext context) {
        // 1. 检索
        RagResult result = retrieve(context);
        
        // 2. 构建增强提示
        String augmentedPrompt = augmentPrompt(query, result);
        
        // 3. 使用 LLM 生成回答
        return llmService.chat(ChatRequest.of(augmentedPrompt));
    }
}
```

### 3.4 Skills 调用方式

**问题**: Skills 如何与 LLM 交互？

**决策**: 通过 Function Calling 机制

**调用流程**:

```
┌─────────────────────────────────────────────────────────────┐
│                     Function Calling Flow                    │
│                                                             │
│  1. User Query                                              │
│       │                                                     │
│       ▼                                                     │
│  2. LLM Analysis ──▶ Tool Selection                         │
│       │                                                     │
│       ▼                                                     │
│  3. Tool Execution ──▶ Skill Execution                      │
│       │                    │                                │
│       │                    ├──▶ RAG.retrieve()              │
│       │                    ├──▶ Database.query()            │
│       │                    └──▶ API.call()                  │
│       │                                                     │
│       ▼                                                     │
│  4. Result Return ──▶ LLM Generation                       │
│       │                                                     │
│       ▼                                                     │
│  5. Final Response                                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**示例**:

```java
// 1. 定义工具
List<ToolDefinition> tools = Arrays.asList(
    ToolDefinition.builder()
        .name("search_knowledge")
        .description("搜索知识库")
        .parameters(...)
        .build()
);

// 2. LLM 选择工具
ChatRequest request = ChatRequest.builder()
    .message("帮我查一下产品A的技术规格")
    .tools(tools)
    .build();

String response = llmService.chatWithTools(request, tools);

// 3. LLM 返回工具调用
// response = { "tool": "search_knowledge", "args": { "query": "产品A 技术规格" } }

// 4. 执行工具
String toolResult = skillRegistry.execute("search_knowledge", args);

// 5. LLM 生成最终回答
String finalResponse = llmService.chat(ChatRequest.builder()
    .message("帮我查一下产品A的技术规格")
    .toolResult(toolResult)
    .build());
```

---

## 四、架构优势

### 4.1 职责清晰

| 层次 | 职责 | 变化频率 |
|------|------|----------|
| 模型服务层 | AI 基础能力 | 低（模型升级） |
| 知识增强层 | 领域知识支持 | 中（知识更新） |
| 技能执行层 | 业务能力封装 | 高（业务变化） |

### 4.2 灵活组合

```
场景1: 简单对话
User ──▶ LLM.chat() ──▶ Response

场景2: 知识问答
User ──▶ LLM.chat() ──▶ Tool: search_knowledge ──▶ RAG ──▶ LLM ──▶ Response

场景3: 复杂任务
User ──▶ LLM.chat() ──▶ Tool: plan_task ──▶ Tool: search_knowledge ──▶ 
        Tool: query_database ──▶ Tool: generate_report ──▶ LLM ──▶ Response
```

### 4.3 易于扩展

- 新增 LLM 提供者：实现 `LlmDriver` 接口
- 新增知识库类型：实现 `KnowledgeBaseApi` 接口
- 新增向量数据库：实现 `VectorStore` 接口
- 新增业务技能：定义 `@Tool` 注解的类

---

## 五、实施建议

### 5.1 代码调整

| 当前问题 | 建议调整 | 优先级 |
|----------|----------|--------|
| `EmbeddingService` 在 scene-engine | 删除，统一使用 `LlmService.embed()` | P0 |
| RAG 直接依赖 VectorStore | 通过 KnowledgeBase 间接依赖 | P1 |
| Skills 未通过 Function Calling | 使用 `@Tool` 注解定义 | P1 |

### 5.2 模块划分

```
agent-sdk/
├── llm-sdk/                    # 模型服务层
│   ├── api/                    # LLM 接口
│   ├── drivers/                # LLM 驱动实现
│   └── adapter/                # 多模型适配
│
├── scene-engine/               # 知识增强层 + 技能执行层
│   ├── skill/
│   │   ├── knowledge/          # 知识库管理
│   │   ├── vector/             # 向量存储
│   │   ├── rag/                # RAG 管道
│   │   └── tools/              # 工具定义
│   └── ...
│
└── skills-framework/           # 技能框架
    ├── annotation/             # @Tool 注解
    ├── registry/               # 工具注册
    └── execution/              # 工具执行
```

---

## 六、术语表

| 术语 | 英文 | 定义 |
|------|------|------|
| 模型服务层 | Model Service Layer | 提供 AI 基础能力（对话、向量化、工具调用） |
| 知识增强层 | Knowledge Augmentation Layer | 提供领域知识存储、检索和增强能力 |
| 技能执行层 | Skill Execution Layer | 提供业务能力封装，通过 Function Calling 调用 |
| 向量化 | Embedding | 将文本转换为向量表示 |
| 知识库 | Knowledge Base | 管理领域知识的文档集合 |
| 向量库 | Vector Store | 存储和检索向量的数据库 |
| RAG | Retrieval-Augmented Generation | 检索增强生成 |
| Function Calling | - | LLM 调用外部工具的机制 |
| Tool | - | 可被 LLM 调用的业务能力封装 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-06
