# 业务模块与知识资料库整合方案讨论

**版本**: v2.3.1  
**日期**: 2026-03-07  
**状态**: 方案讨论

---

## 一、现状分析

### 1.1 ooder-index-web 能力

**核心组件**：

| 组件 | 说明 |
|------|------|
| `IndexService` | Lucene 全文索引服务 |
| `VFSIndexService` | 虚拟文件系统索引服务 |
| `FileIndex` | 文件索引实体 |
| `JLucene` | Lucene 配置接口 |

**核心 API**：

```java
// 添加索引
ResultModel<JLucene> addIndex(T luceneBean);

// 删除索引
ResultModel<Boolean> deleteIndex(JLucene luceneBean);

// 条件查询
ListResultModel<List<V>> search(Condition<T, V> condition);
```

**FileIndex 字段**：

| 字段 | 类型 | 说明 |
|------|------|------|
| name | String | 文件名 |
| userId | String | 用户ID |
| text | StringBuffer | 文本内容（支持高亮） |
| desc | String | 描述 |
| right | String | 权限 |
| createtime | Long | 创建时间 |
| docpath | String | 文档路径 |

### 1.2 scene-engine 知识能力

**核心组件**：

| 组件 | 说明 |
|------|------|
| `KnowledgeCapability` | 知识检索能力接口 |
| `KnowledgeCapabilityImpl` | 知识检索能力实现 |
| `VectorStore` | 向量存储接口 |
| `EmbeddingService` | 嵌入服务接口 |

**核心 API**：

```java
// 单层检索
KnowledgeResult retrieve(String query, KnowledgeLayer layer, Map<String, Object> context);

// 跨层检索
KnowledgeResult crossLayerRetrieve(String query, List<KnowledgeLayer> layers, Map<String, Object> context);
```

---

## 二、整合方案讨论

### 2.1 方案一：双引擎并行

**架构**：

```
┌─────────────────────────────────────────────────────────────┐
│  业务模块                                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────┐    ┌─────────────────────┐        │
│  │  Lucene 全文检索     │    │  Vector 向量检索     │        │
│  │  (ooder-index-web)  │    │  (scene-engine)     │        │
│  └─────────────────────┘    └─────────────────────┘        │
│           │                          │                      │
│           └──────────┬───────────────┘                      │
│                      ▼                                      │
│           ┌─────────────────────┐                          │
│           │  结果融合 & 排序     │                          │
│           └─────────────────────┘                          │
│                      │                                      │
│                      ▼                                      │
│           ┌─────────────────────┐                          │
│           │  LLM 增强           │                          │
│           └─────────────────────┘                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**优点**：
- Lucene 擅长关键词精确匹配
- Vector 擅长语义相似度匹配
- 两者互补，覆盖更多场景

**缺点**：
- 需要维护两套索引
- 结果融合逻辑复杂
- 资源消耗较大

### 2.2 方案二：Lucene 作为 Vector Store 实现

**架构**：

```
┌─────────────────────────────────────────────────────────────┐
│  KnowledgeCapability                                        │
├─────────────────────────────────────────────────────────────┤
│                      │                                      │
│                      ▼                                      │
│           ┌─────────────────────┐                          │
│           │  EmbeddingService   │                          │
│           │  (向量化)            │                          │
│           └─────────────────────┘                          │
│                      │                                      │
│                      ▼                                      │
│           ┌─────────────────────┐                          │
│           │  LuceneVectorStore  │                          │
│           │  (向量存储实现)       │                          │
│           └─────────────────────┘                          │
│                      │                                      │
│                      ▼                                      │
│           ┌─────────────────────┐                          │
│           │  ooder-index-web    │                          │
│           │  (底层存储)          │                          │
│           └─────────────────────┘                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**实现思路**：

```java
public class LuceneVectorStore implements VectorStore {
    
    private IndexService indexService;
    private EmbeddingService embeddingService;
    
    @Override
    public void insert(float[] vector, Map<String, Object> metadata) {
        // 将向量序列化存储到 Lucene
        FileIndex index = new FileIndex();
        index.setUuid(UUID.randomUUID().toString());
        index.setText(new StringBuffer(serializeVector(vector)));
        index.setMetadata(metadata);
        indexService.addIndex(index);
    }
    
    @Override
    public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
        // 方案A: 使用 Lucene 过滤 + 内存向量相似度计算
        // 方案B: 使用 Lucene 的 KNN 查询（需要扩展）
    }
}
```

**优点**：
- 复用现有 Lucene 基础设施
- 统一的知识检索接口
- 便于与现有系统集成

**缺点**：
- Lucene 原生不支持向量检索
- 需要扩展实现向量相似度计算
- 性能可能不如专业向量数据库

### 2.3 方案三：混合检索策略

**架构**：

```
┌─────────────────────────────────────────────────────────────┐
│  HybridSearchCapability                                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  检索策略选择                                         │   │
│  │  ├── 关键词查询 → Lucene 全文检索                     │   │
│  │  ├── 语义查询 → Vector 向量检索                       │   │
│  │  └── 混合查询 → 双引擎 + 结果融合                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────┐    ┌─────────────────────┐        │
│  │  LuceneIndexStore   │    │  VectorStore        │        │
│  │  (关键词索引)        │    │  (向量索引)          │        │
│  └─────────────────────┘    └─────────────────────┘        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**检索策略**：

| 场景 | 策略 | 说明 |
|------|------|------|
| 精确匹配 | Lucene | "查找包含'Java'的文档" |
| 语义相似 | Vector | "查找与招聘相关的内容" |
| 混合查询 | 融合 | "查找Java开发岗位要求" |

---

## 三、LLM 使用方式讨论

### 3.1 RAG 增强模式

**流程**：

```
用户问题
    │
    ▼
┌─────────────────────┐
│  意图理解 (LLM)      │
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│  知识检索            │
│  ├── Lucene 全文     │
│  └── Vector 向量     │
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│  上下文构建          │
│  Query + Knowledge   │
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│  LLM 生成回答        │
└─────────────────────┘
    │
    ▼
最终回答
```

**代码示例**：

```java
public class RAGEnhancedCapability implements Capability {
    
    private IndexService luceneIndex;
    private VectorStore vectorStore;
    private LlmProvider llmProvider;
    private EmbeddingService embeddingService;
    
    @Override
    public CapabilityResult execute(Map<String, Object> params) {
        String query = (String) params.get("query");
        
        // 1. Lucene 全文检索
        Condition<FileIndexEnmu, FileIndex> luceneCondition = buildLuceneCondition(query);
        ListResultModel<List<FileIndex>> luceneResults = luceneIndex.search(luceneCondition);
        
        // 2. Vector 向量检索
        float[] queryVector = embeddingService.embed(query);
        List<SearchResult> vectorResults = vectorStore.search(queryVector, 5, null);
        
        // 3. 结果融合
        String context = mergeResults(luceneResults, vectorResults);
        
        // 4. LLM 生成回答
        String prompt = buildRAGPrompt(query, context);
        String answer = llmProvider.chat("你是智能助手", prompt);
        
        return CapabilityResult.success(answer);
    }
    
    private String buildRAGPrompt(String query, String context) {
        return String.format("""
            请根据以下知识回答用户问题。
            
            相关知识：
            %s
            
            用户问题：%s
            
            请给出准确、专业的回答：
            """, context, query);
    }
}
```

### 3.2 知识库索引流程

**数据写入**：

```
业务数据
    │
    ▼
┌─────────────────────┐
│  数据处理            │
│  ├── 文本提取        │
│  ├── 分词            │
│  └── 元数据构建      │
└─────────────────────┘
    │
    ├──────────────────┐
    ▼                  ▼
┌─────────────┐  ┌─────────────┐
│ Lucene 索引 │  │ Vector 索引 │
│ (关键词)    │  │ (向量)      │
└─────────────┘  └─────────────┘
```

**代码示例**：

```java
public class KnowledgeIndexService {
    
    private IndexService luceneIndex;
    private VectorStore vectorStore;
    private EmbeddingService embeddingService;
    
    public void indexDocument(KnowledgeDocument doc) {
        // 1. Lucene 索引
        FileIndex fileIndex = new FileIndex();
        fileIndex.setUuid(doc.getId());
        fileIndex.setName(doc.getTitle());
        fileIndex.setText(new StringBuffer(doc.getContent()));
        fileIndex.setUserId(doc.getAuthor());
        fileIndex.setCreatetime(doc.getCreateTime());
        fileIndex.setDocpath(doc.getPath());
        luceneIndex.addIndex(fileIndex);
        
        // 2. Vector 索引
        float[] vector = embeddingService.embed(doc.getContent());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("docId", doc.getId());
        metadata.put("title", doc.getTitle());
        metadata.put("layer", doc.getLayer().name());
        vectorStore.insert(vector, metadata);
    }
}
```

### 3.3 三层知识架构映射

**与 ooder-index-web 整合**：

| 层级 | Lucene 索引 | Vector Store | 示例 |
|------|-------------|--------------|------|
| GENERAL | 全局索引目录 | kb-general | 公司制度、流程规范 |
| PROFESSIONAL | 领域索引目录 | kb-professional | 岗位要求、面试题库 |
| SCENE | 场景索引目录 | kb-scene | 候选人简历、面试记录 |

**实现方式**：

```java
public class LayeredKnowledgeStore {
    
    private Map<KnowledgeLayer, IndexService> luceneStores;
    private Map<KnowledgeLayer, VectorStore> vectorStores;
    
    public void index(KnowledgeDocument doc, KnowledgeLayer layer) {
        IndexService lucene = luceneStores.get(layer);
        VectorStore vector = vectorStores.get(layer);
        
        // Lucene 索引
        FileIndex fileIndex = toFileIndex(doc);
        lucene.addIndex(fileIndex);
        
        // Vector 索引
        float[] embedding = embeddingService.embed(doc.getContent());
        Map<String, Object> metadata = buildMetadata(doc, layer);
        vector.insert(embedding, metadata);
    }
    
    public List<RetrievedItem> search(String query, KnowledgeLayer layer) {
        // 双引擎检索
        List<FileIndex> luceneResults = luceneSearch(query, layer);
        List<SearchResult> vectorResults = vectorSearch(query, layer);
        
        // 结果融合
        return mergeResults(luceneResults, vectorResults);
    }
}
```

---

## 四、整合实施建议

### 4.1 第一阶段：适配层

**目标**：创建 ooder-index-web 与 scene-engine 的适配层

**工作内容**：

1. 实现 `LuceneVectorStore` 适配器
2. 实现 `LuceneKnowledgeCapability` 能力
3. 统一知识检索接口

### 4.2 第二阶段：混合检索

**目标**：实现双引擎混合检索

**工作内容**：

1. 实现检索策略选择器
2. 实现结果融合算法
3. 性能优化

### 4.3 第三阶段：LLM 深度整合

**目标**：完整的 RAG 能力

**工作内容**：

1. 实现知识索引流水线
2. 实现 RAG 增强能力
3. 实现知识库管理界面

---

## 五、待讨论问题

### 5.1 技术问题

1. **向量存储**：Lucene 是否适合存储向量？是否需要引入专业向量数据库？
2. **性能问题**：双引擎检索的性能开销如何？
3. **同步问题**：Lucene 索引和 Vector 索引如何保持同步？

### 5.2 架构问题

1. **部署方式**：知识库服务是否独立部署？
2. **扩展性**：如何支持大规模知识库？
3. **多租户**：如何支持多租户隔离？

### 5.3 业务问题

1. **知识来源**：知识从哪里来？如何导入？
2. **知识更新**：知识如何更新？增量还是全量？
3. **知识权限**：如何控制知识访问权限？

---

## 六、下一步行动

1. **确认技术方案**：选择整合方案（双引擎/适配层/混合）
2. **设计接口规范**：定义统一的检索接口
3. **原型验证**：实现核心功能原型
4. **性能测试**：验证方案可行性

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
