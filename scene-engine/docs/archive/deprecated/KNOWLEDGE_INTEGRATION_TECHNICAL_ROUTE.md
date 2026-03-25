# 知识库整合技术路线讨论

**版本**: v2.3.1  
**日期**: 2026-03-07  
**状态**: 技术决策

---

## 一、技术决策

| 决策项 | 选择 | 说明 |
|--------|------|------|
| 向量数据库 | 专业向量数据库 | Milvus/Qdrant/Chroma |
| 检索策略 | Vector 优先 | 语义理解优先，关键词补充 |
| 配置方式 | 注解驱动 DTO | 运行期动态绑定知识库 |

---

## 二、注解驱动的 DTO 与知识库结合

### 2.1 ooder-index-web 注解模式分析

**现有注解结构**：

```java
// 类级注解 - 定义文档索引配置
@JDocumentType(
    name = "VfsFileIndex",
    fsDirectory = @FSDirectoryType(id = "VfsFileIndex"),
    vfsJson = @VFSJsonType(vfsPath = "doc/log/", fileName = "vfsLog.js"),
    indexWriter = @JIndexWriterType(id = "vfsLogIndex")
)
public class FileIndex implements VFSIndex {
    
    // 字段级注解 - 定义字段索引配置
    @JFieldType(store = Store.YES)
    String name;
    
    @JFieldType(store = Store.YES, highlighter = true)
    StringBuffer text;
}
```

**注解映射机制**：

```
@JDocumentType → @ClassMappingAnnotation(clazz=JDocumentBean.class)
                     ↓
              JDocumentBean (运行期配置)
```

### 2.2 知识库注解设计方案

**方案：扩展注解支持向量索引**

```java
// 知识文档注解
@KDocumentType(
    name = "RecruitmentKnowledge",
    layer = KnowledgeLayer.PROFESSIONAL,
    vectorStore = @VectorStoreType(
        dimension = 1536,
        metric = VectorMetric.COSINE
    ),
    luceneIndex = @JDocumentType(name = "RecruitmentLucene")
)
public class RecruitmentKnowledgeDTO {
    
    @KFieldType(
        vectorize = true,           // 是否向量化
        store = Store.YES,          // Lucene 存储
        embeddingModel = "text-embedding-ada-002"
    )
    String content;                  // 内容字段 - 需要向量化
    
    @KFieldType(store = Store.YES)
    String title;                    // 标题字段 - 仅存储
    
    @KFieldType(
        store = Store.YES,
        filterable = true            // 可过滤
    )
    String department;               // 部门 - 用于过滤
    
    @KFieldType(
        store = Store.YES,
        filterable = true
    )
    KnowledgeLayer layer;            // 知识层级
}
```

### 2.3 运行期绑定机制

**架构设计**：

```
┌─────────────────────────────────────────────────────────────┐
│  DTO 类定义                                                  │
│  @KDocumentType(layer=PROFESSIONAL, vectorStore=...)        │
│  public class RecruitmentKnowledgeDTO { ... }               │
├─────────────────────────────────────────────────────────────┤
│                      │                                      │
│                      ▼                                      │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  KnowledgeAnnotationProcessor                        │   │
│  │  ├── 解析 @KDocumentType 注解                        │   │
│  │  ├── 解析 @KFieldType 注解                           │   │
│  │  └── 生成 KnowledgeDocumentConfig                    │   │
│  └─────────────────────────────────────────────────────┘   │
│                      │                                      │
│                      ▼                                      │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  KnowledgeDocumentRegistry (运行期注册表)            │   │
│  │  ├── Map<Class<?>, KnowledgeDocumentConfig>          │   │
│  │  └── 按层级组织索引配置                               │   │
│  └─────────────────────────────────────────────────────┘   │
│                      │                                      │
│                      ▼                                      │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  KnowledgeIndexService                               │   │
│  │  ├── index(Object dto)  → 双写 Vector + Lucene      │   │
│  │  └── search(query)      → Vector 优先检索            │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、优缺点分析

### 3.1 注解驱动方式

| 优点 | 缺点 |
|------|------|
| ✅ 声明式配置，代码简洁 | ❌ 编译期检查有限 |
| ✅ 与 DTO 强绑定，类型安全 | ❌ 运行时才能发现配置错误 |
| ✅ 支持继承和复用 | ❌ 调试困难 |
| ✅ 配置集中，易于维护 | ❌ 动态配置能力有限 |

### 3.2 Vector 优先策略

| 优点 | 缺点 |
|------|------|
| ✅ 语义理解能力强 | ❌ 对精确关键词匹配较弱 |
| ✅ 支持模糊查询 | ❌ 依赖 Embedding 模型质量 |
| ✅ 多语言支持 | ❌ 向量维度固定，模型升级困难 |
| ✅ 相似度排序自然 | ❌ 计算资源消耗大 |

### 3.3 专业向量数据库

| 优点 | 缺点 |
|------|------|
| ✅ 高性能向量检索 | ❌ 额外部署运维成本 |
| ✅ 支持大规模数据 | ❌ 学习成本 |
| ✅ 丰富的索引类型 | ❌ 与 Lucene 双写一致性 |
| ✅ 分布式扩展 | ❌ 数据迁移复杂 |

---

## 四、技术路线

### 4.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│  业务应用层                                                  │
│  ├── 招聘模块 (RecruitmentModule)                           │
│  ├── 审批模块 (ApprovalModule)                              │
│  └── 客服模块 (CustomerServiceModule)                       │
├─────────────────────────────────────────────────────────────┤
│                      │                                      │
│                      ▼                                      │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  KnowledgeCapability (scene-engine)                  │   │
│  │  ├── retrieve(query, layer)                          │   │
│  │  ├── crossLayerRetrieve(query, layers)               │   │
│  │  └── index(dto)                                      │   │
│  └─────────────────────────────────────────────────────┘   │
│                      │                                      │
│          ┌──────────┴──────────┐                           │
│          ▼                     ▼                           │
│  ┌─────────────┐       ┌─────────────┐                    │
│  │ VectorStore │       │ LuceneIndex │                    │
│  │ (Milvus)    │       │ (ooder-index)│                   │
│  └─────────────┘       └─────────────┘                    │
│          │                     │                           │
│          └──────────┬──────────┘                           │
│                     ▼                                       │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  KnowledgeAnnotationProcessor                        │   │
│  │  ├── 解析 DTO 注解                                   │   │
│  │  ├── 生成索引配置                                    │   │
│  │  └── 运行期绑定                                      │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 核心接口设计

```java
/**
 * 知识文档注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ClassMappingAnnotation(clazz = KDocumentConfig.class)
public @interface KDocumentType {
    
    String name();
    
    KnowledgeLayer layer();
    
    VectorStoreConfig vectorStore() default @VectorStoreConfig;
    
    LuceneConfig lucene() default @LuceneConfig;
}

/**
 * 知识字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ClassMappingAnnotation(clazz = KFieldConfig.class)
public @interface KFieldType {
    
    String name() default "";
    
    boolean vectorize() default false;
    
    boolean store() default true;
    
    boolean filterable() default false;
    
    String embeddingModel() default "default";
}

/**
 * 向量存储配置
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface VectorStoreConfig {
    
    int dimension() default 1536;
    
    String metric() default "COSINE";
    
    String collection() default "";
}

/**
 * 知识索引服务
 */
public interface KnowledgeIndexService {
    
    <T> void index(T dto);
    
    <T> void indexBatch(List<T> dtos);
    
    <T> void delete(T dto);
    
    KnowledgeResult search(KSearchRequest request);
}

/**
 * 检索请求
 */
public class KSearchRequest {
    
    String query;
    
    KnowledgeLayer layer;
    
    int topK = 10;
    
    float threshold = 0.7f;
    
    Map<String, Object> filters;
    
    SearchMode mode = SearchMode.VECTOR_FIRST;
}

/**
 * 检索模式
 */
public enum SearchMode {
    VECTOR_ONLY,      // 仅向量
    LUCENE_ONLY,      // 仅 Lucene
    VECTOR_FIRST,     // 向量优先
    HYBRID            // 混合检索
}
```

### 4.3 实现示例

**DTO 定义**：

```java
@KDocumentType(
    name = "JobRequirement",
    layer = KnowledgeLayer.PROFESSIONAL,
    vectorStore = @VectorStoreConfig(
        dimension = 1536,
        metric = "COSINE",
        collection = "job_requirements"
    )
)
public class JobRequirementDTO {
    
    @KFieldType(vectorize = true, store = true)
    private String content;
    
    @KFieldType(store = true, filterable = true)
    private String title;
    
    @KFieldType(store = true, filterable = true)
    private String department;
    
    @KFieldType(store = true, filterable = true)
    private String position;
    
    @KFieldType(store = true)
    private List<String> skills;
    
    // getters and setters
}
```

**索引操作**：

```java
@Service
public class RecruitmentKnowledgeService {
    
    @Autowired
    private KnowledgeIndexService knowledgeIndexService;
    
    public void addJobRequirement(JobRequirementDTO dto) {
        // 自动根据注解配置索引到 Vector + Lucene
        knowledgeIndexService.index(dto);
    }
    
    public List<JobRequirementDTO> searchRequirements(String query, String department) {
        KSearchRequest request = KSearchRequest.builder()
            .query(query)
            .layer(KnowledgeLayer.PROFESSIONAL)
            .topK(5)
            .filter("department", department)
            .mode(SearchMode.VECTOR_FIRST)
            .build();
        
        KnowledgeResult result = knowledgeIndexService.search(request);
        return convertToDTOs(result);
    }
}
```

---

## 五、向量数据库选型

### 5.1 候选方案

| 数据库 | 优势 | 劣势 | 适用场景 |
|--------|------|------|----------|
| **Milvus** | 高性能、分布式、功能丰富 | 部署复杂、资源消耗大 | 大规模生产环境 |
| **Qdrant** | Rust实现、轻量、API友好 | 社区较小 | 中小规模应用 |
| **Chroma** | Python原生、简单易用 | 性能有限 | 开发测试 |
| **Weaviate** | GraphQL、内置向量化 | 学习曲线陡 | 复杂查询场景 |

### 5.2 推荐方案

**开发/测试环境**：SQLite + 内存向量（已实现）

**生产环境**：Milvus（支持大规模、分布式）

**边缘部署**：Qdrant（轻量级、单机部署）

### 5.3 VectorStore 接口适配

```java
public interface VectorStore {
    
    void insert(float[] vector, Map<String, Object> metadata);
    
    void insertBatch(List<float[]> vectors, List<Map<String, Object>> metadatas);
    
    List<SearchResult> search(float[] query, int topK, Map<String, Object> filters);
    
    void delete(String id);
    
    void deleteByFilter(Map<String, Object> filters);
}

// Milvus 实现
public class MilvusVectorStore implements VectorStore {
    
    private MilvusServiceClient client;
    
    @Override
    public void insert(float[] vector, Map<String, Object> metadata) {
        InsertParam param = InsertParam.newBuilder()
            .withCollectionName(collectionName)
            .withFields(convertToFields(vector, metadata))
            .build();
        client.insert(param);
    }
    
    @Override
    public List<SearchResult> search(float[] query, int topK, Map<String, Object> filters) {
        SearchParam param = SearchParam.newBuilder()
            .withCollectionName(collectionName)
            .withVectors(Collections.singletonList(query))
            .withTopK(topK)
            .withExpr(buildFilterExpr(filters))
            .build();
        R<SearchResults> result = client.search(param);
        return convertResults(result);
    }
}
```

---

## 六、双写一致性方案

### 6.1 问题分析

```
Vector Store (Milvus)  ←──?──→  Lucene Index
     │                              │
     │     如何保持一致？            │
     └──────────────────────────────┘
```

### 6.2 解决方案

**方案一：事务性双写**

```java
@Transactional
public void index(KnowledgeDocument doc) {
    try {
        // 1. 写入 Vector Store
        vectorStore.insert(doc.getVector(), doc.getMetadata());
        
        // 2. 写入 Lucene
        luceneIndex.addIndex(doc);
        
    } catch (Exception e) {
        // 回滚逻辑
        throw new KnowledgeIndexException("Index failed", e);
    }
}
```

**方案二：异步队列**

```java
public void index(KnowledgeDocument doc) {
    // 1. 写入消息队列
    kafkaTemplate.send("knowledge-index", doc);
    
    // 2. 消费者分别处理
    @KafkaListener(topics = "knowledge-index")
    public void onMessage(KnowledgeDocument doc) {
        // Vector 消费者
        vectorStore.insert(doc.getVector(), doc.getMetadata());
        
        // Lucene 消费者
        luceneIndex.addIndex(doc);
    }
}
```

**方案三：最终一致性 + 补偿**

```java
public void index(KnowledgeDocument doc) {
    String docId = doc.getId();
    
    // 1. 记录索引状态
    IndexStatus status = new IndexStatus(docId, IndexState.PENDING);
    statusRepository.save(status);
    
    try {
        // 2. 双写
        vectorStore.insert(doc.getVector(), doc.getMetadata());
        luceneIndex.addIndex(doc);
        
        // 3. 更新状态
        status.setState(IndexState.COMPLETED);
        statusRepository.save(status);
        
    } catch (Exception e) {
        status.setState(IndexState.FAILED);
        statusRepository.save(status);
        
        // 触发补偿任务
        compensationService.scheduleRetry(docId);
    }
}
```

---

## 七、实施计划

### 7.1 阶段一：注解框架（1周）

- [ ] 定义 `@KDocumentType`、`@KFieldType` 注解
- [ ] 实现 `KnowledgeAnnotationProcessor`
- [ ] 实现 `KnowledgeDocumentRegistry`

### 7.2 阶段二：VectorStore 适配（1周）

- [ ] 实现 `MilvusVectorStore`
- [ ] 实现 `QdrantVectorStore`
- [ ] 统一 `VectorStore` 接口

### 7.3 阶段三：双写机制（1周）

- [ ] 实现 `KnowledgeIndexService`
- [ ] 实现双写逻辑
- [ ] 实现一致性检查

### 7.4 阶段四：检索融合（1周）

- [ ] 实现 `KSearchRequest`
- [ ] 实现多模式检索
- [ ] 实现结果融合算法

---

## 八、风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| 向量数据库性能瓶颈 | 检索延迟高 | 引入缓存、分片 |
| 双写不一致 | 数据丢失 | 补偿机制、定期校验 |
| Embedding 模型升级 | 向量维度变化 | 版本管理、渐进迁移 |
| 注解配置错误 | 运行时异常 | 配置校验、单元测试 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
