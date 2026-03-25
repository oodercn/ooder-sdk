# NLP → MD → RAG 知识架构深度分析

**版本**: v1.0  
**日期**: 2026-03-12  
**状态**: 架构分析

---

## 一、现有实现总结抽象

### 1.1 三层架构抽象

基于代码分析，现有实现可抽象为以下三层知识处理架构：

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     知识处理三层架构抽象                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 3: 运行时上下文层 (Runtime Context Layer)                  │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  LlmRuntimeContextAssembler                                     │   │
│  │  ├── RoleContext (角色定义)                                      │   │
│  │  ├── KnowledgeContext (知识上下文)                               │   │
│  │  ├── FunctionContext (函数定义)                                  │   │
│  │  └── MemoryContext (记忆上下文)                                  │   │
│  │                                                                 │   │
│  │  职责: 运行时动态组装LLM所需完整上下文                              │   │
│  │  实现度: 85% ✅ 接近完整                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 2: 知识增强层 (Knowledge Augmentation Layer)               │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  SkillsMdLoader + RagPipeline                                   │   │
│  │  ├── KnowledgeBase (知识库管理)                                  │   │
│  │  ├── VectorStore (向量存储接口)                                  │   │
│  │  ├── EmbeddingService (向量化服务)                               │   │
│  │  └── RagPipeline (RAG管道)                                      │   │
│  │                                                                 │   │
│  │  职责: 知识文档加载、向量化、检索增强                               │   │
│  │  实现度: 60% ⚠️ 基础框架存在，关键组件缺失                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 1: 元数据定义层 (Metadata Definition Layer)                │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  SkillPackage + skill-index.yaml                                │   │
│  │  ├── Skill Metadata (Skill元数据)                                │   │
│  │  ├── Knowledge Documents (知识文档引用)                          │   │
│  │  ├── RAG Configuration (RAG配置)                                 │   │
│  │  └── Role Definitions (角色定义)                                 │   │
│  │                                                                 │   │
│  │  职责: 开发者配置阶段定义LLM所需静态数据                             │   │
│  │  实现度: 75% 🟡 基础配置完整，高级特性缺失                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1.2 数据流抽象

```
开发者配置 (YAML/Markdown)
    │
    │ 1. 解析配置
    ▼
┌─────────────────────────────────────┐
│ SkillPackage (内存对象)              │
│ ├── metadata: Map<String, Object>   │
│ ├── description: String             │
│ └── knowledgeDocuments: List<String>│
└─────────────────────────────────────┘
    │
    │ 2. 安装部署 (缺失!)
    ▼
┌─────────────────────────────────────┐
│ KnowledgeBaseInstaller (缺失)        │
│ ├── 扫描环境上下文                   │
│ ├── 构建向量索引                     │
│ └── 预编译Prompt                    │
└─────────────────────────────────────┘
    │
    │ 3. 运行时加载
    ▼
┌─────────────────────────────────────┐
│ SkillsMdLoader                       │
│ ├── extractKnowledgeFromSkill()     │
│ │   ├── Skill描述                   │
│ │   ├── knowledgeDocuments (内联)   │
│ │   └── detailedKnowledge (内联)    │
│ └── setupRagIndex()                 │
│     └── 仅读取indexId (不完整)       │
└─────────────────────────────────────┘
    │
    │ 4. RAG检索
    ▼
┌─────────────────────────────────────┐
│ RagPipeline                          │
│ ├── retrieve() - 向量检索            │
│ ├── augmentPrompt() - 提示增强       │
│ └── generate() - 生成回答            │
└─────────────────────────────────────┘
    │
    │ 5. 上下文组装
    ▼
┌─────────────────────────────────────┐
│ LlmRuntimeContextAssembler           │
│ ├── RoleContext                     │
│ ├── KnowledgeContext                │
│ ├── FunctionContext                 │
│ └── MemoryContext                   │
└─────────────────────────────────────┘
```

---

## 二、遗漏的"知识点"检查

### 2.1 关键缺失组件

| 缺失组件 | 影响程度 | 说明 |
|---------|---------|------|
| **KnowledgeBaseInstaller** | 🔴 致命 | 向量索引无法自动构建，RAG无法在生产环境工作 |
| **EnvironmentScanner** | 🔴 致命 | 组织上下文无法自动采集，知识库缺少环境数据 |
| **DocumentChunker** | 🟡 严重 | 文档分块策略缺失，影响RAG检索质量 |
| **EmbeddingCache** | 🟡 中等 | 向量缓存缺失，重复计算影响性能 |
| **KnowledgeVersionManager** | 🟡 中等 | 知识版本管理缺失，无法追踪知识变更 |
| **PermissionEngine** | 🟡 中等 | 权限计算引擎缺失，数据范围控制不完整 |
| **SessionPersistence** | 🟢 轻微 | 会话历史持久化TODO，影响多轮对话 |

### 2.2 配置层面缺失

当前 `skill-index.yaml` 配置 vs 理想配置的对比：

```yaml
# 当前配置 (已实现)
ragConfig:
  indexId: "recruitment-kb-001"  # ✅ 仅支持indexId

# 理想配置 (缺失)
ragConfig:
  indexId: "recruitment-kb-001"
  embeddingModel: "text-embedding-3-large"  # ❌ 未支持
  chunkSize: 500                              # ❌ 未支持
  chunkOverlap: 50                            # ❌ 未支持
  searchStrategy: "hybrid"                    # ❌ 未支持
  rerankEnabled: true                         # ❌ 未支持
  dataSources:                                # ❌ 未支持
    - type: "file"
      path: "knowledge/basic.md"
    - type: "database"
      table: "hr_policies"
      filter: "status='active'"
```

### 2.3 知识类型遗漏

当前支持的知识类型：
- ✅ Skill描述 (description)
- ✅ 内联知识文档 (knowledgeDocuments)
- ✅ 详细知识 (detailedKnowledge)

遗漏的知识类型：
- ❌ 外部文件引用 (knowledge/basic.md)
- ❌ 结构化数据 (数据库表)
- ❌ API动态知识 (REST API数据)
- ❌ 用户个性化知识 (用户特定数据)
- ❌ 多模态知识 (图片、视频)

---

## 三、NLP → MD → RAG 转换关系

### 3.1 转换链路总览

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    NLP → MD → RAG 知识转换链路                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐              │
│  │   NLP输入     │───▶│   MD文档     │───▶│   RAG索引    │              │
│  │  (自然语言)   │    │  (结构化)    │    │  (向量化)    │              │
│  └──────────────┘    └──────────────┘    └──────────────┘              │
│         │                  │                  │                        │
│         ▼                  ▼                  ▼                        │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐              │
│  │ 意图识别     │    │ 文档结构     │    │ 向量检索     │              │
│  │ 实体提取     │    │ 元数据标注   │    │ 相似度计算   │              │
│  │ 关系抽取     │    │ 语义分块     │    │ 重排序       │              │
│  └──────────────┘    └──────────────┘    └──────────────┘              │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.2 详细转换映射

#### Stage 1: NLP → MD (自然语言到Markdown)

```java
/**
 * NLP到MD的转换接口
 * 将自然语言描述转换为结构化Markdown文档
 */
public interface NlpToMdConverter {
    
    /**
     * 转换Skill描述为Markdown
     */
    MarkdownDocument convertSkillDescription(String skillId, String description);
    
    /**
     * 转换业务规则为Markdown
     */
    MarkdownDocument convertBusinessRules(String skillId, List<Rule> rules);
    
    /**
     * 转换API文档为Markdown
     */
    MarkdownDocument convertApiDocs(String skillId, List<ApiDoc> apiDocs);
}

/**
 * Markdown文档结构
 */
public class MarkdownDocument {
    private String docId;
    private String title;
    private String skillId;
    
    // 标准Markdown结构
    private List<MarkdownSection> sections;
    
    // 元数据 (Front Matter)
    private Map<String, Object> frontMatter;
    
    // 语义标签
    private List<SemanticTag> semanticTags;
}

/**
 * Markdown章节
 */
public class MarkdownSection {
    private String heading;           // 标题
    private int level;                // 层级 (1-6)
    private String content;           // 内容
    private List<SemanticTag> tags;   // 语义标签
    private Map<String, Object> metadata; // 元数据
}

/**
 * 语义标签
 */
public class SemanticTag {
    private String tagType;           // 标签类型: ENTITY, INTENT, RELATION, etc.
    private String value;             // 标签值
    private int startPos;             // 起始位置
    private int endPos;               // 结束位置
    private Map<String, Object> attributes; // 属性
}
```

**转换规则：**

| NLP输入 | MD输出 | 示例 |
|--------|--------|------|
| "招聘Skill用于管理候选人" | `# 招聘Skill\n\n用于管理候选人的业务流程` | Skill概述 |
| "创建候选人需要填写姓名、电话" | `## 创建候选人\n\n### 必填字段\n- 姓名\n- 电话` | 功能说明 |
| "审批流程：提交→HR审核→通过/拒绝" | `## 审批流程\n\n1. 提交\n2. HR审核\n3. 通过/拒绝` | 流程图 |

#### Stage 2: MD → RAG (Markdown到向量索引)

```java
/**
 * MD到RAG的转换接口
 * 将Markdown文档转换为RAG可用的向量索引
 */
public interface MdToRagConverter {
    
    /**
     * 构建知识库
     */
    KnowledgeBase buildKnowledgeBase(String kbId, List<MarkdownDocument> documents);
    
    /**
     * 文档分块
     */
    List<KnowledgeChunk> chunkDocument(MarkdownDocument document, ChunkingStrategy strategy);
    
    /**
     * 构建向量索引
     */
    VectorIndex buildVectorIndex(String kbId, List<KnowledgeChunk> chunks);
}

/**
 * 分块策略
 */
public enum ChunkingStrategy {
    FIXED_SIZE,       // 固定大小分块
    SEMANTIC,         // 语义分块 (按段落/章节)
    HYBRID,           // 混合分块
    RECURSIVE         // 递归分块
}

/**
 * 知识块
 */
public class KnowledgeChunk {
    private String chunkId;
    private String docId;
    private String content;
    
    // 向量表示
    private float[] embedding;
    
    // 元数据
    private Map<String, Object> metadata;
    
    // 上下文信息 (用于增强检索)
    private String prevChunkId;
    private String nextChunkId;
    private List<String> relatedChunkIds;
}

/**
 * 向量索引
 */
public class VectorIndex {
    private String indexId;
    private String kbId;
    private int dimension;
    private long vectorCount;
    
    // 索引配置
    private IndexConfig config;
    
    // 统计信息
    private IndexStats stats;
}
```

**转换流程：**

```
MarkdownDocument
    │
    │ 1. 解析结构
    ▼
┌─────────────────────────────────────┐
│ DocumentParser                       │
│ ├── 提取Front Matter                │
│ ├── 解析章节结构                     │
│ └── 识别语义标签                     │
└─────────────────────────────────────┘
    │
    │ 2. 语义分块
    ▼
┌─────────────────────────────────────┐
│ SemanticChunker                      │
│ ├── 按标题层级分块                   │
│ ├── 保持上下文连贯性                 │
│ └── 生成块间关系                     │
└─────────────────────────────────────┘
    │
    │ 3. 向量化
    ▼
┌─────────────────────────────────────┐
│ EmbeddingGenerator                   │
│ ├── 调用LLM Embedding API           │
│ ├── 缓存向量结果                     │
│ └── 批量处理优化                     │
└─────────────────────────────────────┘
    │
    │ 4. 索引构建
    ▼
┌─────────────────────────────────────┐
│ VectorIndexBuilder                   │
│ ├── 创建向量索引                     │
│ ├── 插入向量数据                     │
│ └── 构建元数据索引                   │
└─────────────────────────────────────┘
```

### 3.3 NLP自然语言调整扩展方式

为了支持更优雅的NLP自然语言调整，建议引入以下扩展机制：

```java
/**
 * NLP自然语言配置接口
 * 允许开发者使用自然语言描述Skill行为
 */
public interface NlpConfiguration {
    
    /**
     * 自然语言描述Skill功能
     */
    @NlpDescription("这个Skill用于管理招聘流程，包括候选人录入、简历筛选、面试安排和录用审批")
    void describeSkill();
    
    /**
     * 自然语言定义业务规则
     */
    @NlpRule("当候选人经验超过5年时，自动标记为高级工程师")
    void seniorEngineerRule();
    
    /**
     * 自然语言定义知识边界
     */
    @NlpKnowledgeBoundary("
        这个Skill只处理招聘相关的问题。
        如果用户询问其他话题，礼貌地引导回招聘主题。
    ")
    void defineKnowledgeBoundary();
}

/**
 * NLP配置解析器
 * 将自然语言配置转换为结构化配置
 */
@Component
public class NlpConfigurationParser {
    
    @Autowired
    private LlmService llmService;
    
    /**
     * 解析NLP描述为结构化配置
     */
    public SkillConfiguration parse(Class<?> nlpConfigClass) {
        SkillConfiguration config = new SkillConfiguration();
        
        // 解析 @NlpDescription
        NlpDescription descAnnotation = nlpConfigClass.getAnnotation(NlpDescription.class);
        if (descAnnotation != null) {
            config.setDescription(descAnnotation.value());
            config.setStructuredDescription(
                llmService.extractStructuredInfo(descAnnotation.value())
            );
        }
        
        // 解析 @NlpRule
        for (Method method : nlpConfigClass.getDeclaredMethods()) {
            NlpRule ruleAnnotation = method.getAnnotation(NlpRule.class);
            if (ruleAnnotation != null) {
                BusinessRule rule = llmService.parseBusinessRule(ruleAnnotation.value());
                config.addBusinessRule(rule);
            }
        }
        
        return config;
    }
}
```

**NLP配置示例：**

```java
@NlpSkill(
    id = "recruitment-skill",
    name = "招聘管理Skill",
    version = "2.3.1"
)
public class RecruitmentSkillConfig implements NlpConfiguration {
    
    @NlpDescription("""
        招聘管理Skill是一个完整的招聘流程管理系统。
        
        主要功能包括：
        1. 候选人管理：录入、编辑、查询候选人信息
        2. 简历筛选：根据职位要求自动筛选简历
        3. 面试安排：协调面试官时间，发送面试邀请
        4. 录用审批：多级审批流程，确保合规
        
        适用场景：
        - 校园招聘
        - 社会招聘
        - 内部推荐
        """)
    @Override
    public void describeSkill() {}
    
    @NlpRule("候选人必须年满18周岁")
    @NlpRule("技术岗位需要至少2年相关经验")
    @NlpRule("薪资范围必须在职位预算内")
    @Override
    public void defineBusinessRules() {}
    
    @NlpKnowledgeBoundary("""
        知识范围：
        - 公司招聘政策和流程
        - 各职位要求和薪资标准
        - 面试评估标准
        - 劳动法规和合规要求
        
        边界限制：
        - 不处理薪资谈判具体数字
        - 不提供个人求职建议
        - 不讨论其他公司招聘情况
        """)
    @Override
    public void defineKnowledgeBoundary() {}
    
    @NlpPersona("""
        角色设定：
        你是一位专业的招聘助手，熟悉人力资源管理的最佳实践。
        你的回答应该：
        - 专业、礼貌、高效
        - 基于公司政策和流程
        - 保护候选人隐私
        - 确保招聘合规
        """)
    public void definePersona() {}
}
```

---

## 四、未来规划：优雅的嵌入契合方案

### 4.1 目标架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     未来目标架构：NLP-Native RAG                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 4: NLP自然语言层 (NLP-Native Layer)                       │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  @NlpSkill, @NlpDescription, @NlpRule, @NlpKnowledgeBoundary    │   │
│  │                                                                 │   │
│  │  开发者使用自然语言描述Skill，系统自动生成配置                       │   │
│  │  示例: "这个Skill用于管理招聘流程，包括..."                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 3: 结构化配置层 (Structured Config Layer)                 │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  skill-index.yaml + skills.md                                   │   │
│  │                                                                 │   │
│  │  标准YAML配置 + Markdown知识文档                                  │   │
│  │  支持版本控制、差异对比、代码审查                                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 2: 知识编译层 (Knowledge Compilation Layer)               │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  KnowledgeCompiler                                              │   │
│  │  ├── 解析YAML/MD配置                                             │   │
│  │  ├── 语义分析与自然语言理解                                       │   │
│  │  ├── 自动生成补充知识                                             │   │
│  │  └── 输出编译后知识包                                             │   │
│  │                                                                 │   │
│  │  类似代码编译：源代码 → 编译器 → 字节码                            │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 1: 运行时执行层 (Runtime Execution Layer)                 │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  CompiledKnowledgePackage                                       │   │
│  │  ├── 优化后的向量索引                                            │   │
│  │  ├── 预编译的Prompt模板                                          │   │
│  │  ├── 函数调用映射表                                              │   │
│  │  └── 运行时上下文缓存                                            │   │
│  │                                                                 │   │
│  │  高性能执行，支持热更新                                            │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 4.2 关键改进点

#### 4.2.1 知识编译器 (Knowledge Compiler)

```java
/**
 * 知识编译器
 * 将高层配置编译为运行时高效执行的知识包
 */
@Component
public class KnowledgeCompiler {
    
    @Autowired
    private LlmService llmService;
    
    @Autowired
    private VectorStore vectorStore;
    
    /**
     * 编译知识包
     */
    public CompiledKnowledgePackage compile(KnowledgeSource source) {
        CompiledKnowledgePackage pkg = new CompiledKnowledgePackage();
        
        // 1. 解析源配置
        ParsedKnowledge parsed = parseSource(source);
        
        // 2. 语义增强
        EnhancedKnowledge enhanced = enhanceWithLlm(parsed);
        
        // 3. 生成向量索引
        VectorIndex index = buildVectorIndex(enhanced);
        
        // 4. 预编译Prompt
        Map<String, CompiledPrompt> prompts = compilePrompts(enhanced);
        
        // 5. 构建函数映射
        FunctionMap functionMap = buildFunctionMap(enhanced);
        
        pkg.setVectorIndex(index);
        pkg.setPrompts(prompts);
        pkg.setFunctionMap(functionMap);
        pkg.setMetadata(buildMetadata(enhanced));
        
        return pkg;
    }
    
    /**
     * 使用LLM进行语义增强
     */
    private EnhancedKnowledge enhanceWithLlm(ParsedKnowledge parsed) {
        EnhancedKnowledge enhanced = new EnhancedKnowledge();
        
        // 生成FAQ
        List<FAQEntry> faqs = llmService.generateFAQ(parsed.getDescription());
        enhanced.setGeneratedFaqs(faqs);
        
        // 提取关键词
        List<Keyword> keywords = llmService.extractKeywords(parsed.getContent());
        enhanced.setKeywords(keywords);
        
        // 生成示例对话
        List<ExampleConversation> examples = llmService.generateExamples(parsed);
        enhanced.setExampleConversations(examples);
        
        // 识别边界情况
        List<BoundaryCase> boundaries = llmService.identifyBoundaries(parsed);
        enhanced.setBoundaryCases(boundaries);
        
        return enhanced;
    }
}
```

#### 4.2.2 声明式RAG配置

```yaml
# 未来理想的skill-index.yaml配置
skill:
  id: recruitment-skill
  name: 招聘管理
  
  # NLP自然语言描述 (替代复杂的metadata)
  description: |
    这个Skill用于管理完整的招聘流程，包括：
    - 候选人录入和管理
    - 简历筛选和评估
    - 面试安排和反馈
    - 录用审批和入职
  
  # 声明式知识配置
  knowledge:
    # 自动从描述生成基础FAQ
    autoGenerateFaq: true
    
    # 知识文档
    documents:
      - path: knowledge/basic.md
        priority: high
        autoChunk: true
        chunkSize: 500
      
      - path: knowledge/advanced.md
        priority: medium
        
    # 动态知识源
    dynamicSources:
      - type: database
        name: 职位数据
        query: SELECT * FROM positions WHERE status='active'
        refreshInterval: 1h
        
      - type: api
        name: 组织架构
        endpoint: /api/org/structure
        cacheTtl: 24h
  
  # 声明式RAG配置
  rag:
    embeddingModel: text-embedding-3-large
    
    retrieval:
      strategy: hybrid
      topK: 5
      threshold: 0.7
      rerank: true
      rerankModel: bge-reranker-large
    
    augmentation:
      template: |
        你是专业的招聘助手。请根据以下参考资料回答问题：
        
        {retrieved_context}
        
        用户问题：{query}
        
        回答要求：
        - 基于参考资料回答
        - 如果不确定，说明不清楚
        - 保持专业和礼貌
  
  # 声明式角色配置
  persona:
    name: 招聘助手
    personality: professional, helpful, efficient
    expertise:
      - 人力资源管理
      - 招聘流程
      - 面试技巧
    boundaries:
      - 不讨论薪资具体数字
      - 不提供个人求职建议
      - 保护候选人隐私
```

#### 4.2.3 自适应RAG

```java
/**
 * 自适应RAG
 * 根据查询动态调整检索策略
 */
@Component
public class AdaptiveRag {
    
    @Autowired
    private LlmService llmService;
    
    @Autowired
    private VectorStore vectorStore;
    
    /**
     * 自适应检索
     */
    public RagResult adaptiveRetrieve(String query, RagContext context) {
        // 1. 分析查询意图
        QueryAnalysis analysis = llmService.analyzeQuery(query);
        
        // 2. 根据意图选择检索策略
        RetrievalStrategy strategy = selectStrategy(analysis);
        
        switch (strategy) {
            case DIRECT_ANSWER:
                // 简单查询，直接回答，无需检索
                return directAnswer(query);
                
            case SINGLE_KB:
                // 单一知识库检索
                return retrieveFromSingleKb(query, context);
                
            case MULTI_KB:
                // 多知识库联合检索
                return retrieveFromMultipleKbs(query, context);
                
            case ITERATIVE:
                // 迭代检索 (多轮检索优化)
                return iterativeRetrieve(query, context);
                
            case HYBRID:
                // 混合检索 (向量+关键词)
                return hybridRetrieve(query, context);
                
            default:
                return standardRetrieve(query, context);
        }
    }
    
    /**
     * 迭代检索
     */
    private RagResult iterativeRetrieve(String query, RagContext context) {
        List<RetrievedChunk> allChunks = new ArrayList<>();
        String currentQuery = query;
        
        for (int i = 0; i < 3; i++) {  // 最多3轮
            // 检索
            RagResult result = standardRetrieve(currentQuery, context);
            allChunks.addAll(result.getChunks());
            
            // 判断是否足够
            if (isSufficient(allChunks, query)) {
                break;
            }
            
            // 生成下一轮查询
            currentQuery = llmService.generateFollowUpQuery(query, allChunks);
        }
        
        return mergeResults(allChunks);
    }
}
```

### 4.3 迁移路径

```
当前状态                    过渡阶段                    目标状态
─────────                  ─────────                  ─────────
                           
YAML配置                    YAML + NLP注解             NLP-Native
(静态)                      (混合)                      (动态)
  │                          │                          │
  │  Phase 1                 │  Phase 2                 │  Phase 3
  ▼                          ▼                          ▼
┌─────────┐                ┌─────────┐                ┌─────────┐
│手动配置  │───▶│注解增强  │───▶│自然语言  │
│         │                │         │                │         │
│硬编码知识│                │自动生成  │                │智能编译  │
│         │                │补充知识  │                │优化知识  │
└─────────┘                └─────────┘                └─────────┘
                           
时间线: 现在 ──────────────────────────────────────────▶ 6个月后
```

---

## 五、总结

### 5.1 核心发现

1. **现有架构基础良好**：三层架构（元数据定义→知识增强→运行时上下文）设计合理
2. **关键组件缺失**：KnowledgeBaseInstaller和EnvironmentScanner是生产部署的瓶颈
3. **配置方式待优化**：当前YAML配置过于技术化，需要更自然的NLP配置方式
4. **RAG能力不完整**：缺少自适应检索、迭代检索等高级特性

### 5.2 推荐优先级

| 优先级 | 任务 | 影响 |
|-------|------|------|
| P0 | 实现KnowledgeBaseInstaller | 解锁生产部署 |
| P0 | 实现EnvironmentScanner | 自动化环境感知 |
| P1 | 引入NLP注解配置 | 提升开发体验 |
| P1 | 实现知识编译器 | 性能优化 |
| P2 | 自适应RAG | 检索质量提升 |
| P2 | 多模态知识支持 | 功能扩展 |

### 5.3 NLP → MD → RAG 转换关系总结

```
┌─────────────────────────────────────────────────────────────────┐
│                    转换关系总结                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  NLP自然语言 ──────▶ 结构化理解 ──────▶ Markdown文档              │
│  (人类友好)          (LLM解析)          (版本可控)                │
│                                                                 │
│       │                  │                  │                   │
│       │                  │                  │                   │
│       ▼                  ▼                  ▼                   │
│                                                                 │
│  意图识别              语义标注            文档结构               │
│  实体提取              关系抽取            元数据                 │
│  规则定义              知识分类            语义标签               │
│                                                                 │
│       │                  │                  │                   │
│       └──────────────────┴──────────────────┘                   │
│                          │                                      │
│                          ▼                                      │
│                                                                 │
│  Markdown ──────────▶ 语义分块 ─────────▶ 向量索引               │
│  (结构化)             (保持语义)          (机器高效)              │
│                                                                 │
│       │                  │                  │                   │
│       ▼                  ▼                  ▼                   │
│                                                                 │
│  章节解析              上下文感知          相似度检索             │
│  元数据提取            重叠分块            元数据过滤             │
│  标签映射              关系维护            向量缓存               │
│                                                                 │
│                          │                                      │
│                          ▼                                      │
│                                                                 │
│                    RAG增强LLM                                    │
│                    (智能问答)                                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-12
