# 智能安装配置任务 - RAG、向量库、知识库设计文档

## 一、问题分析与架构设计

### 1.1 核心问题梳理

基于讨论，需要解决以下四个核心问题：

1. **RAG、向量库、知识资料库如何安装？**
2. **安装完以后如何初始化数据？数据在哪？**
3. **多级的知识库向量库是什么关系？**
4. **如何在全生命周期的过程中，提供配置、使用、更新指南？**

### 1.2 现状分析

**Scene-Engine现有能力**:
- ✅ `KnowledgeBaseService` - 知识库管理接口（CRUD、文档管理、权限）
- ✅ `RagPipeline` - RAG Pipeline实现（检索、增强、生成）
- ✅ `VectorStore` - 向量存储接口（插入、检索、删除）
- ✅ `InMemoryVectorStore` - 内存向量存储实现
- ⚠️ 仅内存实现，无持久化向量数据库集成

**Agent-SDK现有能力**:
- ✅ `VectorDriver` - 向量驱动接口
- ⚠️ `LocalVectorDriver` / `MilvusVectorDriver` - 仅模拟实现
- ✅ `MemoryStore` - 记忆存储（支持embedding）
- ❌ 无完整RAG组件

---

## 二、RAG、向量库、知识库安装方案

### 2.1 部署架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           知识增强层部署架构                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      Scene-Engine (应用层)                           │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │ Knowledge   │  │    RAG      │  │   Vector    │  │  Document   │ │   │
│  │  │ BaseService │  │   Pipeline  │  │    Store    │  │   Store     │ │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘ │   │
│  └─────────┼────────────────┼────────────────┼────────────────┼────────┘   │
│            │                │                │                │             │
│            ▼                ▼                ▼                ▼             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      向量数据库层 (可选部署)                          │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   Milvus    │  │  Pinecone   │  │  Weaviate   │  │   Chroma    │ │   │
│  │  │  (推荐)      │  │   (云端)     │  │  (开源)      │  │  (轻量级)    │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      文档存储层 (可选部署)                            │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   MinIO     │  │     S3      │  │  本地文件    │  │   MongoDB   │ │   │
│  │  │  (对象存储)  │  │  (云端存储)  │  │  (开发测试)  │  │  (文档元数据)│ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 安装方式

#### 2.2.1 方式一：嵌入式部署（开发/测试环境）

**适用场景**: 开发测试、小规模部署、快速验证

**组件**:
- `InMemoryVectorStore` - 内存向量存储
- `InMemoryDocumentStore` - 内存文档存储
- `MockEmbeddingService` - 模拟Embedding服务

**优点**:
- 零配置，开箱即用
- 无需外部依赖
- 启动速度快

**缺点**:
- 数据不持久化
- 无法横向扩展
- 仅适合开发和测试

**配置**:
```yaml
knowledge:
  vector-store:
    type: in-memory
    dimension: 1536
  document-store:
    type: in-memory
  embedding:
    type: mock
```

#### 2.2.2 方式二：Docker Compose部署（推荐生产环境）

**适用场景**: 生产环境、中小规模部署

**组件**:
- Milvus - 向量数据库
- **OODER-VFS Skills** - 虚拟文件系统（文档存储）
- Etcd - Milvus依赖的KV存储

**docker-compose.yml**:
```yaml
version: '3.5'

services:
  # Milvus向量数据库
  etcd:
    container_name: milvus-etcd
    image: quay.io/coreos/etcd:v3.5.5
    environment:
      - ETCD_AUTO_COMPACTION_MODE=revision
      - ETCD_AUTO_COMPACTION_RETENTION=1000
      - ETCD_QUOTA_BACKEND_BYTES=4294967296
    volumes:
      - ${DOCKER_VOLUME_DIRECTORY:-.}/volumes/etcd:/etcd
    command: etcd -advertise-client-urls=http://127.0.0.1:2379 -listen-client-urls http://0.0.0.0:2379 --data-dir /etcd

  minio:
    container_name: milvus-minio
    image: minio/minio:RELEASE.2023-03-20T20-16-18Z
    environment:
      MINIO_ACCESS_KEY: minioadmin
      MINIO_SECRET_KEY: minioadmin
    volumes:
      - ${DOCKER_VOLUME_DIRECTORY:-.}/volumes/minio:/minio_data
    command: minio server /minio_data
    ports:
      - "9001:9001"
      - "9000:9000"

  milvus-standalone:
    container_name: milvus-standalone
    image: milvusdb/milvus:v2.3.3
    command: ["milvus", "run", "standalone"]
    environment:
      ETCD_ENDPOINTS: etcd:2379
      MINIO_ADDRESS: minio:9000
    volumes:
      - ${DOCKER_VOLUME_DIRECTORY:-.}/volumes/milvus:/var/lib/milvus
    ports:
      - "19530:19530"
      - "9091:9091"
    depends_on:
      - etcd
      - minio

  # 文档存储MinIO
  document-minio:
    container_name: document-minio
    image: minio/minio:RELEASE.2023-03-20T20-16-18Z
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    volumes:
      - ${DOCKER_VOLUME_DIRECTORY:-.}/volumes/documents:/data
    command: server /data --console-address ":9001"
    ports:
      - "9002:9000"
      - "9003:9001"

networks:
  default:
    name: milvus
```

**配置**:
```yaml
knowledge:
  vector-store:
    type: milvus
    host: localhost
    port: 19530
    dimension: 1536
    metric-type: COSINE
  document-store:
    type: minio
    endpoint: http://localhost:9002
    access-key: minioadmin
    secret-key: minioadmin
    bucket: documents
  embedding:
    type: openai
    api-key: ${OPENAI_API_KEY}
    model: text-embedding-3-small
```

#### 2.2.3 方式三：Kubernetes部署（大规模生产环境）

**适用场景**: 大规模生产、高可用需求、云原生部署

**组件**:
- Milvus Cluster - 分布式向量数据库
- Cloud Storage - 云对象存储（S3/Azure Blob/OSS）
- External Embedding Service - 外部Embedding服务

**Helm Chart**:
```bash
# 添加Milvus Helm仓库
helm repo add milvus https://milvus-io.github.io/milvus-helm/
helm repo update

# 安装Milvus集群
helm install milvus milvus/milvus \
  --set cluster.enabled=true \
  --set etcd.replicaCount=3 \
  --set minio.mode=distributed \
  --set minio.replicas=4
```

**配置**:
```yaml
knowledge:
  vector-store:
    type: milvus
    mode: cluster
    endpoints:
      - milvus-proxy-1:19530
      - milvus-proxy-2:19530
    dimension: 1536
  document-store:
    type: s3
    region: us-east-1
    bucket: my-knowledge-documents
  embedding:
    type: openai
    api-key: ${OPENAI_API_KEY}
    model: text-embedding-3-small
```

### 2.3 安装流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           知识增强层安装流程                                   │
└─────────────────────────────────────────────────────────────────────────────┘

【阶段1: 环境检查】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1.1 系统要求检查
   ├─ CPU: 4核+ (生产环境8核+)
   ├─ 内存: 8GB+ (生产环境16GB+)
   ├─ 磁盘: 100GB+ SSD
   └─ 网络: 内网互通

1.2 依赖服务检查
   ├─ Docker / Docker Compose
   ├─ Kubernetes (可选)
   └─ 外部Embedding服务可用性

【阶段2: 向量数据库安装】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

2.1 选择部署方式
   ├─ 嵌入式: 无需安装，使用InMemoryVectorStore
   ├─ Docker: 执行docker-compose up -d
   └─ K8s: 执行helm install

2.2 验证安装
   ├─ 检查服务健康状态
   ├─ 测试向量插入/检索
   └─ 验证连接配置

【阶段3: 文档存储安装】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

3.1 选择存储后端
   ├─ 嵌入式: 无需安装
   ├─ MinIO: Docker部署或独立部署
   ├─ S3: 配置云厂商凭证
   └─ 本地文件: 配置存储路径

3.2 初始化存储桶
   ├─ 创建documents存储桶
   ├─ 配置访问策略
   └─ 测试上传/下载

【阶段4: Embedding服务配置】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

4.1 选择Embedding提供商
   ├─ OpenAI: 配置API Key
   ├─ 百度文心: 配置AK/SK
   ├─ 讯飞星火: 配置AppID/APIKey/APISecret
   └─ 本地模型: 配置模型路径

4.2 验证Embedding服务
   ├─ 测试文本向量化
   ├─ 验证向量维度
   └─ 测试批量处理

【阶段5: Scene-Engine集成配置】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

5.1 配置向量存储
   ├─ 设置VectorStore实现类
   ├─ 配置连接参数
   └─ 设置向量维度

5.2 配置文档存储
   ├─ 设置DocumentStore实现类
   ├─ 配置存储后端
   └─ 设置访问凭证

5.3 配置Embedding服务
   ├─ 设置EmbeddingService实现类
   ├─ 配置API凭证
   └─ 设置模型参数

【阶段6: 初始化验证】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

6.1 创建测试知识库
6.2 上传测试文档
6.3 执行RAG检索测试
6.4 验证端到端流程
```

---

## 三、初始化数据流程和数据来源

### 3.1 初始化数据架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           初始化数据来源架构                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         数据来源层                                    │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   系统内置   │  │   场景模板   │  │   用户上传   │  │   外部导入   │ │   │
│  │  │   数据      │  │   数据      │  │   数据      │  │   数据      │ │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘ │   │
│  └─────────┼────────────────┼────────────────┼────────────────┼────────┘   │
│            │                │                │                │             │
│            ▼                ▼                ▼                ▼             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      数据预处理层                                     │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   格式转换   │  │   文本提取   │  │   内容清洗   │  │   元数据提取 │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│            │                                                                │
│            ▼                                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      数据索引层                                       │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   文本分块   │  │   Embedding  │  │   向量存储   │  │   索引构建   │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 数据来源分类

#### 3.2.1 系统内置数据（P0）

**场景安装指南**:
```
知识库: system://installation-guides
├── 场景类型说明
│   ├── daily-report.md - 日报场景说明
│   ├── project-management.md - 项目管理场景说明
│   └── approval-workflow.md - 审批流程场景说明
├── 安装步骤指南
│   ├── leader-activation.md - 领导激活流程
│   ├── employee-activation.md - 员工激活流程
│   └── standalone-skill.md - 独立技能安装
├── 配置参考
│   ├── template-schema.md - 模板配置Schema
│   ├── dependency-check.md - 依赖检查说明
│   └── menu-generation.md - 菜单生成配置
└── 常见问题
    ├── installation-faq.md
    ├── activation-faq.md
    └── troubleshooting.md
```

**系统配置数据**:
```yaml
# 内置知识库配置
system-knowledge-bases:
  - id: system-installation-guides
    name: "场景安装指南"
    type: system
    visibility: public
    documents:
      - path: classpath:/knowledge/installation/
        pattern: "*.md"
    
  - id: system-skill-library
    name: "技能库说明"
    type: system
    visibility: public
    documents:
      - path: classpath:/knowledge/skills/
        pattern: "*.md"
```

#### 3.2.2 场景模板数据（P0）

**场景模板知识库**:
```
知识库: template://{templateId}-knowledge
├── 场景说明文档
│   ├── README.md - 场景概述
│   ├── features.md - 功能特性
│   └── use-cases.md - 使用案例
├── 角色指南
│   ├── manager-guide.md - 领导使用指南
│   ├── employee-guide.md - 员工使用指南
│   └── admin-guide.md - 管理员指南
├── 配置说明
│   ├── configuration.md - 配置项说明
│   ├── activation-steps.md - 激活步骤详解
│   └── menu-reference.md - 菜单参考
└── 最佳实践
    ├── setup-best-practices.md
    └── usage-tips.md
```

**模板知识库配置**:
```yaml
# 场景模板知识库配置
template-knowledge-base:
  enabled: true
  auto-create: true
  document-sources:
    - type: template-embedded
      path: "knowledge/"
    - type: template-docs
      path: "docs/"
  indexing:
    auto-index: true
    chunk-size: 500
    chunk-overlap: 50
```

#### 3.2.3 用户上传数据（P1）

**用户知识库**:
```
知识库: user://{userId}/knowledge-bases/{kbId}
├── 业务文档
│   ├── *.pdf
│   ├── *.docx
│   ├── *.txt
│   └── *.md
├── 配置文档
│   ├── config-*.yaml
│   └── settings-*.json
└── 历史数据
    ├── archived-*.zip
    └── backup-*.sql
```

**上传接口**:
```java
public interface DocumentUploadService {
    // 单文件上传
    Document uploadDocument(String kbId, MultipartFile file, UploadOptions options);
    
    // 批量上传
    BatchUploadResult batchUpload(String kbId, List<MultipartFile> files, UploadOptions options);
    
    // 从URL导入
    Document importFromUrl(String kbId, String url, ImportOptions options);
    
    // 从云存储导入
    Document importFromCloud(String kbId, CloudStorageConfig config, String path);
}
```

#### 3.2.4 外部导入数据（P2）

**外部数据源**:
```
数据源类型:
├── 企业Wiki
│   ├── Confluence
│   ├── Notion
│   └── 钉钉文档
├── 代码仓库
│   ├── GitHub README
│   ├── GitLab Wiki
│   └── 代码注释
├── 协作平台
│   ├── 飞书文档
│   ├── 企业微信文档
│   └── 腾讯文档
└── 专业系统
    ├── Jira问题库
    ├── ServiceNow知识库
    └── 内部KM系统
```

### 3.3 初始化数据流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           初始化数据流程                                     │
└─────────────────────────────────────────────────────────────────────────────┘

【阶段1: 系统启动】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1.1 检查知识库初始化状态
   ├─ 查询system_knowledge_bases表
   ├─ 检查内置知识库是否存在
   └─ 检查索引状态

1.2 如未初始化，触发初始化流程
   └─ 发送KnowledgeBaseInitEvent

【阶段2: 内置知识库初始化】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

2.1 创建系统知识库
   ├─ 创建system-installation-guides知识库
   ├─ 创建system-skill-library知识库
   └─ 设置权限为public

2.2 加载内置文档
   ├─ 扫描classpath:/knowledge/目录
   ├─ 读取所有.md文件
   └─ 解析文档元数据

2.3 文档预处理
   ├─ 格式转换（HTML/PDF转Markdown）
   ├─ 文本提取
   ├─ 内容清洗（去除特殊字符、标准化）
   └─ 元数据提取（标题、作者、日期）

【阶段3: 文档分块】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

3.1 选择分块策略
   ├─ 固定长度分块（默认500字符）
   ├─ 语义分块（按段落/句子）
   ├─ 递归分块（按标题层级）
   └─ 自定义分块（模板指定）

3.2 执行分块
   ├─ 将文档切分为chunks
   ├─ 设置chunk重叠（默认50字符）
   ├─ 为每个chunk生成唯一ID
   └─ 记录chunk位置信息

【阶段4: Embedding生成】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

4.1 批量向量化
   ├─ 将chunks分批（每批100个）
   ├─ 调用EmbeddingService.embedBatch()
   ├─ 获取向量表示
   └─ 验证向量维度

4.2 错误处理
   ├─ 记录失败chunks
   ├─ 重试机制（最多3次）
   └─ 失败告警

【阶段5: 向量存储】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

5.1 构建向量数据
   ├─ 组合chunk内容、向量、元数据
   ├─ 添加kbId、docId、chunkId标识
   └─ 构建VectorData对象

5.2 批量插入
   ├─ 调用VectorStore.batchInsert()
   ├─ 验证插入结果
   └─ 记录存储统计

【阶段6: 索引构建】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

6.1 构建倒排索引（可选）
   ├─ 提取关键词
   ├─ 构建TF-IDF索引
   └─ 支持混合检索

6.2 更新索引状态
   ├─ 更新IndexStatus
   ├─ 记录索引时间
   └─ 计算索引覆盖率

【阶段7: 完成验证】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

7.1 验证检索功能
   ├─ 执行测试查询
   ├─ 验证返回结果
   └─ 检查相似度分数

7.2 更新系统状态
   ├─ 标记初始化完成
   ├─ 记录初始化时间
   └─ 发送KnowledgeBaseReadyEvent
```

### 3.4 数据初始化代码实现

```java
@Service
public class KnowledgeBaseInitializer {
    
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseInitializer.class);
    
    @Autowired
    private KnowledgeBaseService kbService;
    
    @Autowired
    private VectorStore vectorStore;
    
    @Autowired
    private EmbeddingService embeddingService;
    
    @Autowired
    private DocumentChunker chunker;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @PostConstruct
    public void initialize() {
        log.info("Starting knowledge base initialization...");
        
        // 1. 初始化系统知识库
        initializeSystemKnowledgeBases();
        
        // 2. 初始化场景模板知识库
        initializeTemplateKnowledgeBases();
        
        log.info("Knowledge base initialization completed");
    }
    
    private void initializeSystemKnowledgeBases() {
        // 创建系统安装指南知识库
        if (!kbService.exists("system-installation-guides")) {
            KnowledgeBaseCreateRequest request = new KnowledgeBaseCreateRequest();
            request.setName("场景安装指南");
            request.setDescription("系统内置的场景安装和配置指南");
            request.setOwnerId("system");
            request.setVisibility(KnowledgeBase.VISIBILITY_PUBLIC);
            request.setEmbeddingModel("text-embedding-3-small");
            request.setChunkSize(500);
            request.setChunkOverlap(50);
            
            KnowledgeBase kb = kbService.create(request);
            log.info("Created system knowledge base: {}", kb.getKbId());
            
            // 加载内置文档
            loadBuiltinDocuments(kb.getKbId(), "classpath:/knowledge/installation/");
        }
    }
    
    private void loadBuiltinDocuments(String kbId, String resourcePath) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources(resourcePath + "*.md");
            
            for (Resource resource : resources) {
                String content = Files.readString(resource.getFile().toPath());
                String docName = resource.getFilename();
                
                // 创建文档
                DocumentCreateRequest docRequest = new DocumentCreateRequest();
                docRequest.setTitle(docName.replace(".md", ""));
                docRequest.setContent(content);
                docRequest.setType("markdown");
                docRequest.setSource("builtin");
                
                Document doc = kbService.addDocument(kbId, docRequest);
                
                // 分块
                List<DocumentChunk> chunks = chunker.chunk(content, 
                    ChunkingConfig.builder()
                        .chunkSize(500)
                        .chunkOverlap(50)
                        .build());
                
                // 向量化并存储
                List<VectorData> vectors = new ArrayList<>();
                for (int i = 0; i < chunks.size(); i++) {
                    DocumentChunk chunk = chunks.get(i);
                    float[] embedding = embeddingService.embed(chunk.getContent());
                    
                    VectorData vectorData = new VectorData();
                    vectorData.setId(String.format("%s-chunk-%d", doc.getDocId(), i));
                    vectorData.setVector(embedding);
                    vectorData.setMetadata(Map.of(
                        "kbId", kbId,
                        "docId", doc.getDocId(),
                        "chunkId", chunk.getChunkId(),
                        "chunkIndex", i,
                        "source", "builtin"
                    ));
                    vectors.add(vectorData);
                }
                
                // 批量插入向量
                vectorStore.batchInsert(vectors);
                
                log.info("Indexed document: {} with {} chunks", docName, chunks.size());
            }
            
            // 发送初始化完成事件
            eventPublisher.publishEvent(new KnowledgeBaseInitializedEvent(kbId));
            
        } catch (IOException e) {
            log.error("Failed to load builtin documents from: {}", resourcePath, e);
        }
    }
}
```

---

## 四、多级知识库向量库关系模型

### 4.1 层级架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           多级知识库架构                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Level 1: 系统层 (System Layer)                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  系统知识库: system-installation-guides                              │   │
│  │  ├─ 场景安装指南                                                      │   │
│  │  ├─ 配置参考文档                                                      │   │
│  │  └─ 常见问题解答                                                      │   │
│  │                                                                      │   │
│  │  系统知识库: system-skill-library                                    │   │
│  │  ├─ 技能说明文档                                                      │   │
│  │  ├─ API参考手册                                                       │   │
│  │  └─ 最佳实践指南                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              ▲                                              │
│                              │ 继承/引用                                     │
│  Level 2: 模板层 (Template Layer)                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  模板知识库: template-{templateId}-knowledge                         │   │
│  │  ├─ 场景说明文档 (继承系统层通用指南)                                   │   │
│  │  ├─ 角色使用指南 (模板特定)                                            │   │
│  │  ├─ 配置说明文档 (模板特定)                                            │   │
│  │  └─ 最佳实践 (继承+扩展)                                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              ▲                                              │
│                              │ 实例化/继承                                   │
│  Level 3: 场景层 (Scene Layer)                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  场景知识库: scene-{sceneId}-knowledge                               │   │
│  │  ├─ 模板知识库引用 (只读)                                              │   │
│  │  ├─ 场景特定文档 (运行时生成)                                          │   │
│  │  ├─ 用户上传文档                                                      │   │
│  │  └─ 运行时数据 (日志、配置历史)                                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              ▲                                              │
│                              │ 个性化                                        │
│  Level 4: 用户层 (User Layer)                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  用户知识库: user-{userId}-knowledge                                 │   │
│  │  ├─ 个人文档收藏                                                      │   │
│  │  ├─ 私有笔记                                                          │   │
│  │  ├─ 历史查询记录                                                      │   │
│  │  └─ 个性化配置                                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 关系模型

#### 4.2.1 继承关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           知识库继承关系                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  system-installation-guides (父)                                            │
│           │                                                                 │
│           ├──► template-daily-report-knowledge (子)                         │
│           │              │                                                  │
│           │              ├──► scene-abc123-knowledge (实例)                  │
│           │                         │                                       │
│           │                         └──► user-john-doe-knowledge (个性化)    │
│           │                                                                 │
│           ├──► template-project-mgmt-knowledge (子)                         │
│           │              │                                                  │
│           │              └──► scene-def456-knowledge (实例)                  │
│           │                                                                 │
│           └──► template-approval-knowledge (子)                             │
│                      │                                                      │
│                      └──► scene-ghi789-knowledge (实例)                      │
│                                                                             │
│  继承规则:                                                                   │
│  1. 子知识库自动包含父知识库的所有文档 (只读引用)                              │
│  2. 子知识库可以添加自己的文档                                               │
│  3. 检索时优先检索本级，再向上检索父级                                        │
│  4. 父知识库更新，子知识库自动继承更新                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 4.2.2 引用关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           知识库引用关系                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  scene-abc123-knowledge (主知识库)                                           │
│  ├─ 本地文档: doc-001, doc-002                                              │
│  │                                                                          │
│  ├─ 引用: template-daily-report-knowledge (模板知识库)                       │
│  │   ├─ 引用范围: ["getting-started.md", "manager-guide.md"]                │
│  │   ├─ 引用方式: read-only                                                 │
│  │   └─ 自动同步: true                                                      │
│  │                                                                          │
│  ├─ 引用: system-installation-guides (系统知识库)                            │
│  │   ├─ 引用范围: ["troubleshooting.md"]                                    │
│  │   ├─ 引用方式: read-only                                                 │
│  │   └─ 自动同步: true                                                      │
│  │                                                                          │
│  └─ 引用: user-john-doe-knowledge (用户私有知识库)                            │
│      ├─ 引用范围: ["personal-notes.md"]                                     │
│      ├─ 引用方式: read-write (用户可编辑)                                    │
│      └─ 自动同步: false (按需同步)                                           │
│                                                                             │
│  引用规则:                                                                   │
│  1. 引用可以是只读或读写                                                     │
│  2. 可以指定引用的文档范围 (白名单)                                           │
│  3. 支持自动同步和手动同步两种模式                                            │
│  4. 循环引用检测和阻止                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.3 向量库隔离策略

#### 4.3.1 物理隔离（推荐生产环境）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           物理隔离方案                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Milvus Collection设计:                                                      │
│                                                                             │
│  Collection: knowledge_base_vectors                                         │
│  ├─ Partition: system (系统层)                                              │
│  │   ├─ 存储所有系统知识库向量                                               │
│  │   └─ 全局共享，只读访问                                                   │
│  │                                                                          │
│  ├─ Partition: template_{templateId} (模板层)                               │
│  │   ├─ 每个场景模板一个Partition                                            │
│  │   ├─ 模板内所有场景共享                                                   │
│  │   └─ 只读访问                                                            │
│  │                                                                          │
│  ├─ Partition: scene_{sceneId} (场景层)                                     │
│  │   ├─ 每个场景一个Partition                                                │
│  │   ├─ 场景特定文档 + 运行时数据                                            │
│  │   └─ 读写访问                                                            │
│  │                                                                          │
│  └─ Partition: user_{userId} (用户层)                                       │
│      ├─ 每个用户一个Partition                                                │
│      ├─ 用户私有数据                                                        │
│      └─ 完全隔离                                                            │
│                                                                             │
│  字段设计:                                                                   │
│  ├─ id: string (向量ID)                                                     │
│  ├─ vector: float[] (1536维向量)                                            │
│  ├─ kb_id: string (知识库ID)                                                │
│  ├─ doc_id: string (文档ID)                                                 │
│  ├─ chunk_id: string (分块ID)                                               │
│  ├─ content: string (文本内容)                                              │
│  ├─ metadata: json (元数据)                                                 │
│  ├─ level: int (层级: 1=系统, 2=模板, 3=场景, 4=用户)                         │
│  ├─ parent_kb_id: string (父知识库ID)                                        │
│  └─ created_at: timestamp                                                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 4.3.2 逻辑隔离（开发/测试环境）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           逻辑隔离方案                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  InMemoryVectorStore / 单Collection方案:                                     │
│                                                                             │
│  使用metadata字段实现逻辑隔离:                                                │
│                                                                             │
│  向量数据结构:                                                               │
│  {                                                                          │
│    "id": "vec-001",                                                         │
│    "vector": [0.1, 0.2, ...],                                               │
│    "metadata": {                                                            │
│      "kb_id": "scene-abc123-knowledge",                                     │
│      "kb_level": 3,                    // 场景层                            │
│      "kb_parent": "template-daily-report-knowledge",                        │
│      "doc_id": "doc-001",                                                   │
│      "chunk_id": "chunk-001",                                               │
│      "content": "文本内容",                                                  │
│      "source": "builtin",                                                   │
│      "visibility": "public",                                                │
│      "owner_id": "system",                                                  │
│      "scene_id": "abc123",                                                  │
│      "template_id": "daily-report",                                         │
│      "created_at": "2024-01-01T00:00:00Z"                                   │
│    }                                                                        │
│  }                                                                          │
│                                                                             │
│  检索过滤:                                                                   │
│  ├─ 系统层检索: filter = {"kb_level": 1}                                    │
│  ├─ 模板层检索: filter = {"kb_level": 2, "template_id": "daily-report"}     │
│  ├─ 场景层检索: filter = {"kb_id": "scene-abc123-knowledge"}                │
│  ├─ 用户层检索: filter = {"kb_level": 4, "owner_id": "john-doe"}            │
│  └─ 跨层检索: filter = {"scene_id": "abc123"} (包含继承的文档)               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.4 多级检索策略

```java
@Service
public class HierarchicalKnowledgeRetriever {
    
    @Autowired
    private VectorStore vectorStore;
    
    /**
     * 多级检索 - 从当前层向上检索
     */
    public RagResult retrieveHierarchical(RagContext context, RetrieveOptions options) {
        String kbId = context.getKbId();
        KnowledgeBaseLevel level = determineLevel(kbId);
        
        List<RagResult.RetrievedChunk> allChunks = new ArrayList<>();
        Set<String> seenChunkIds = new HashSet<>();
        
        // 1. 检索当前层
        List<SearchResult> currentLevelResults = searchLevel(kbId, context);
        addUniqueChunks(allChunks, currentLevelResults, seenChunkIds, level);
        
        // 2. 如果结果不足，向上检索父层
        if (allChunks.size() < context.getTopK() && options.isIncludeParent()) {
            String parentKbId = getParentKbId(kbId);
            while (parentKbId != null && allChunks.size() < context.getTopK()) {
                List<SearchResult> parentResults = searchLevel(parentKbId, context);
                addUniqueChunks(allChunks, parentResults, seenChunkIds, 
                    getLevel(parentKbId));
                parentKbId = getParentKbId(parentKbId);
            }
        }
        
        // 3. 如果结果仍不足，检索引用的知识库
        if (allChunks.size() < context.getTopK() && options.isIncludeReferences()) {
            List<String> referencedKbIds = getReferencedKbIds(kbId);
            for (String refKbId : referencedKbIds) {
                if (allChunks.size() >= context.getTopK()) break;
                List<SearchResult> refResults = searchLevel(refKbId, context);
                addUniqueChunks(allChunks, refResults, seenChunkIds,
                    getLevel(refKbId));
            }
        }
        
        // 4. 按分数排序并截取topK
        allChunks.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));
        if (allChunks.size() > context.getTopK()) {
            allChunks = allChunks.subList(0, context.getTopK());
        }
        
        RagResult result = new RagResult();
        result.setQuery(context.getQuery());
        result.setChunks(allChunks);
        result.setTotalFound(allChunks.size());
        
        return result;
    }
    
    private List<SearchResult> searchLevel(String kbId, RagContext context) {
        float[] queryVector = embeddingService.embed(context.getQuery());
        
        Map<String, Object> filters = new HashMap<>();
        filters.put("kb_id", kbId);
        
        return vectorStore.search(queryVector, context.getTopK(), filters);
    }
    
    private void addUniqueChunks(List<RagResult.RetrievedChunk> chunks,
                                  List<SearchResult> results,
                                  Set<String> seenIds,
                                  KnowledgeBaseLevel level) {
        for (SearchResult result : results) {
            String chunkId = (String) result.getMetadata().get("chunk_id");
            if (seenIds.contains(chunkId)) continue;
            
            seenIds.add(chunkId);
            
            RagResult.RetrievedChunk chunk = new RagResult.RetrievedChunk();
            chunk.setChunkId(chunkId);
            chunk.setContent(result.getContent());
            chunk.setScore(result.getScore());
            chunk.setMetadata(result.getMetadata());
            chunk.setLevel(level);
            
            chunks.add(chunk);
        }
    }
}
```

---

## 五、全生命周期配置、使用、更新指南

### 5.1 生命周期阶段

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           知识库全生命周期                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【安装阶段】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  环境准备   │───▶│  服务部署   │───▶│  初始化数据  │───▶│  验证测试   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│                                                                             │
│  指南提供:                                                                   │
│  ├─ 部署文档 (Docker/K8s配置)                                               │
│  ├─ 配置参考 (yaml配置项说明)                                               │
│  ├─ 初始化脚本 (自动初始化)                                                  │
│  └─ 验证清单 (健康检查接口)                                                  │
│                                                                             │
│  【配置阶段】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  知识库创建 │───▶│  文档上传   │───▶│  索引配置   │───▶│  权限设置   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│                                                                             │
│  指南提供:                                                                   │
│  ├─ 创建向导 (LLM引导式创建)                                                 │
│  ├─ 文档上传指南 (支持格式、大小限制)                                         │
│  ├─ 分块策略建议 (根据文档类型推荐)                                           │
│  └─ 权限配置说明 (角色权限矩阵)                                               │
│                                                                             │
│  【使用阶段】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  检索查询   │───▶│  结果查看   │───▶│  反馈评价   │───▶│  持续优化   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│                                                                             │
│  指南提供:                                                                   │
│  ├─ 检索技巧 (查询语法、过滤条件)                                             │
│  ├─ 结果解读 (相似度分数、来源标注)                                           │
│  ├─ 反馈机制 (点赞/点踩、纠错)                                               │
│  └─ 优化建议 (基于使用数据)                                                   │
│                                                                             │
│  【更新阶段】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  文档更新   │───▶│  增量索引   │───▶│  版本管理   │───▶│  回滚恢复   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│                                                                             │
│  指南提供:                                                                   │
│  ├─ 更新流程 (文档替换、增量更新)                                             │
│  ├─ 索引策略 (全量重建 vs 增量更新)                                           │
│  ├─ 版本控制 (历史版本保留)                                                   │
│  └─ 回滚机制 (一键回滚)                                                       │
│                                                                             │
│  【卸载阶段】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  数据备份   │───▶│  服务停止   │───▶│  数据清理   │───▶│  资源释放   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│                                                                             │
│  指南提供:                                                                   │
│  ├─ 备份指南 (导出脚本)                                                       │
│  ├─ 清理检查清单 (防止数据残留)                                               │
│  └─ 资源释放确认 (存储、内存)                                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 指南提供机制

#### 5.2.1 上下文感知指南

```java
@Service
public class ContextAwareGuideService {
    
    @Autowired
    private KnowledgeBaseService kbService;
    
    @Autowired
    private RagPipeline ragPipeline;
    
    /**
     * 获取当前上下文的指南
     */
    public GuideResponse getGuide(GuideRequest request) {
        String context = request.getContext(); // install, configure, use, update, uninstall
        String phase = request.getPhase();     // 具体阶段
        String userRole = request.getUserRole(); // admin, manager, employee
        
        // 构建查询
        String query = String.format("%s阶段 %s 操作指南 %s角色", context, phase, userRole);
        
        // 从系统知识库检索指南
        RagContext ragContext = new RagContext(query, "system-installation-guides");
        ragContext.setTopK(3);
        
        RagResult result = ragPipeline.retrieve(ragContext);
        
        // 构建指南响应
        GuideResponse response = new GuideResponse();
        response.setContext(context);
        response.setPhase(phase);
        response.setSteps(extractSteps(result));
        response.setRelatedDocs(extractDocs(result));
        response.setNextActions(suggestNextActions(context, phase));
        
        return response;
    }
    
    /**
     * 获取操作向导
     */
    public WizardResponse startWizard(String wizardType, String userId) {
        switch (wizardType) {
            case "knowledge-base-creation":
                return startKbCreationWizard(userId);
            case "document-upload":
                return startDocumentUploadWizard(userId);
            case "rag-configuration":
                return startRagConfigWizard(userId);
            default:
                throw new IllegalArgumentException("Unknown wizard type: " + wizardType);
        }
    }
}
```

#### 5.2.2 LLM驱动的智能指南

```java
@Service
public class LLMGuideService {
    
    @Autowired
    private LlmSdk llmSdk;
    
    @Autowired
    private ContextTemplateApi contextTemplateApi;
    
    /**
     * 生成个性化操作指南
     */
    public String generatePersonalizedGuide(GuideContext context) {
        // 加载指南生成模板
        String template = contextTemplateApi.getTemplate("guide-generation");
        
        // 构建变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("userRole", context.getUserRole());
        variables.put("currentPhase", context.getCurrentPhase());
        variables.put("userQuery", context.getUserQuery());
        variables.put("systemState", context.getSystemState());
        variables.put("availableActions", context.getAvailableActions());
        
        // 渲染模板
        String prompt = contextTemplateApi.renderTemplate("guide-generation", variables);
        
        // 调用LLM生成指南
        ChatRequest request = new ChatRequest();
        request.setPrompt(prompt);
        request.setSystemMessage("你是一个专业的系统配置助手，帮助用户完成知识库的配置和使用。");
        
        ChatResponse response = llmSdk.getNlpInteractionApi().chat(request);
        
        return response.getContent();
    }
    
    /**
     * 交互式故障排查
     */
    public TroubleshootingResponse troubleshoot(String errorDescription, String context) {
        // 检索相关故障解决方案
        RagContext ragContext = new RagContext(errorDescription, "system-installation-guides");
        RagResult ragResult = ragPipeline.retrieve(ragContext);
        
        // 构建故障排查提示
        StringBuilder prompt = new StringBuilder();
        prompt.append("用户遇到了以下问题:\n");
        prompt.append(errorDescription).append("\n\n");
        prompt.append("相关上下文:\n");
        prompt.append(context).append("\n\n");
        
        if (!ragResult.getChunks().isEmpty()) {
            prompt.append("参考以下文档片段:\n");
            for (RagResult.RetrievedChunk chunk : ragResult.getChunks()) {
                prompt.append("- ").append(chunk.getContent()).append("\n");
            }
        }
        
        prompt.append("\n请提供:\n");
        prompt.append("1. 可能的原因分析\n");
        prompt.append("2. 逐步排查步骤\n");
        prompt.append("3. 解决方案\n");
        prompt.append("4. 预防措施\n");
        
        ChatRequest request = new ChatRequest();
        request.setPrompt(prompt.toString());
        
        ChatResponse response = llmSdk.getNlpInteractionApi().chat(request);
        
        return parseTroubleshootingResponse(response.getContent());
    }
}
```

### 5.3 指南内容管理

#### 5.3.1 指南文档结构

```
knowledge/
├── installation/                          # 安装阶段指南
│   ├── README.md                          # 安装概述
│   ├── prerequisites.md                   # 环境要求
│   ├── docker-deployment.md               # Docker部署
│   ├── kubernetes-deployment.md           # K8s部署
│   ├── configuration-reference.md         # 配置参考
│   └── verification.md                    # 验证测试
│
├── configuration/                         # 配置阶段指南
│   ├── README.md
│   ├── knowledge-base-creation.md         # 创建知识库
│   ├── document-upload.md                 # 文档上传
│   ├── chunking-strategies.md             # 分块策略
│   ├── embedding-configuration.md         # Embedding配置
│   └── permission-setup.md                # 权限设置
│
├── usage/                                 # 使用阶段指南
│   ├── README.md
│   ├── search-techniques.md               # 检索技巧
│   ├── result-interpretation.md           # 结果解读
│   ├── feedback-mechanism.md              # 反馈机制
│   └── best-practices.md                  # 最佳实践
│
├── maintenance/                           # 维护阶段指南
│   ├── README.md
│   ├── document-update.md                 # 文档更新
│   ├── index-rebuild.md                   # 索引重建
│   ├── version-management.md              # 版本管理
│   ├── backup-restore.md                  # 备份恢复
│   └── performance-optimization.md        # 性能优化
│
├── troubleshooting/                       # 故障排查
│   ├── README.md
│   ├── common-issues.md                   # 常见问题
│   ├── error-codes.md                     # 错误码
│   ├── debug-guide.md                     # 调试指南
│   └── support-contacts.md                # 支持渠道
│
└── api-reference/                         # API参考
    ├── README.md
    ├── knowledge-base-api.md
    ├── document-api.md
    ├── search-api.md
    └── rag-api.md
```

#### 5.3.2 指南更新机制

```java
@Service
public class GuideUpdateService {
    
    @Autowired
    private KnowledgeBaseService kbService;
    
    @Autowired
    private DocumentImporter documentImporter;
    
    /**
     * 检查并更新指南
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点检查
    public void checkAndUpdateGuides() {
        log.info("Checking for guide updates...");
        
        // 1. 检查远程指南更新
        GuideUpdateCheckResult checkResult = checkRemoteUpdates();
        
        if (checkResult.hasUpdates()) {
            log.info("Found {} guide updates", checkResult.getUpdateCount());
            
            // 2. 下载更新
            List<Document> updatedDocs = downloadUpdates(checkResult);
            
            // 3. 导入更新
            for (Document doc : updatedDocs) {
                documentImporter.importDocument("system-installation-guides", doc);
            }
            
            // 4. 重建索引
            kbService.rebuildIndex("system-installation-guides");
            
            // 5. 发送更新通知
            notifyUsers(checkResult);
        }
    }
    
    /**
     * 用户贡献指南
     */
    public ContributionResponse contributeGuide(GuideContribution contribution) {
        // 验证贡献者权限
        validateContributor(contribution.getUserId());
        
        // 提交审核
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setContent(contribution.getContent());
        reviewRequest.setType("guide-contribution");
        reviewRequest.setSubmitterId(contribution.getUserId());
        
        String reviewId = submitForReview(reviewRequest);
        
        return ContributionResponse.builder()
            .reviewId(reviewId)
            .status("pending-review")
            .estimatedReviewTime("2-3 business days")
            .build();
    }
}
```

---

## 六、总结

### 6.1 关键设计决策

| 决策项 | 方案 | 理由 |
|--------|------|------|
| 向量数据库 | Milvus (生产) / InMemory (开发) | 高性能、分布式、开源 |
| 文档存储 | **OODER-VFS Skills** | 虚拟文件系统，统一文档存储接口 |
| Embedding | OpenAI / 百度文心 | 成熟稳定，支持中文 |
| 知识库层级 | 4层 (系统/模板/场景/用户) | 清晰的责任边界 |
| 隔离策略 | 物理隔离 (Partition) | 性能和安全兼顾 |
| 指南机制 | LLM驱动 + 上下文感知 | 个性化、智能化 |

### 6.2 实施建议

**Phase 1 (Week 1-2): 基础设施**
1. 部署Milvus向量数据库
2. 部署**OODER-VFS Skills**文档存储
3. 集成Embedding服务
4. 实现VectorStore接口

**Phase 2 (Week 3-4): 核心功能**
1. 实现KnowledgeBaseService
2. 实现RagPipeline
3. 实现文档分块和索引
4. 实现多级检索

**Phase 3 (Week 5-6): 指南系统**
1. 编写系统指南文档
2. 实现GuideService
3. 集成LLM指南生成
4. 实现故障排查助手

**Phase 4 (Week 7-8): 优化完善**
1. 性能优化
2. 监控告警
3. 用户反馈收集
4. 文档完善

---

## 七、VFS与知识资料库同步管理关系

### 7.1 架构关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    VFS 与知识资料库同步管理架构                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      OODER-VFS Skills (虚拟文件系统)                  │   │
│  │                                                                     │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │   │
│  │   │  Document   │  │   Chunk     │  │  Metadata   │  │  Version  │ │   │
│  │   │   Store     │  │   Store     │  │   Store     │  │  Control  │ │   │
│  │   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └─────┬─────┘ │   │
│  │          │                │                │               │       │   │
│  └──────────┼────────────────┼────────────────┼───────────────┼───────┘   │
│             │                │                │               │           │
│             │   ┌────────────┴────────────────┴───────────────┘           │
│             │   │                                                         │
│             ▼   ▼                                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    VFS Sync Manager (同步管理器)                      │   │
│  │                                                                     │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │   │
│  │   │   Event     │  │   Index     │  │   Change    │  │  Conflict │ │   │
│  │   │   Listener  │  │   Trigger   │  │   Detector  │  │  Resolver │ │   │
│  │   └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│             │                                                             │
│             ▼                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Knowledge Base (知识资料库)                        │   │
│  │                                                                     │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │   │
│  │   │   KB        │  │  Document   │  │   Index     │  │  Search   │ │   │
│  │   │  Manager    │  │  Manager    │  │  Manager    │  │  Engine   │ │   │
│  │   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └─────┬─────┘ │   │
│  └──────────┼────────────────┼────────────────┼───────────────┼───────┘   │
│             │                │                │               │           │
│             ▼                ▼                ▼               ▼           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Vector Store + RAG (向量库与检索增强)               │   │
│  │                                                                     │   │
│  │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │   │
│  │   │   Vector    │  │   Embedding │  │    RAG      │  │  Similarity│ │   │
│  │   │   Index     │  │   Service   │  │  Pipeline   │  │   Search   │ │   │
│  │   └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 同步机制

#### 7.2.1 事件驱动同步

```java
/**
 * VFS 事件监听器
 */
@Component
public class VfsKnowledgeSyncListener {
    
    @Autowired
    private KnowledgeBaseSyncService syncService;
    
    /**
     * 文档创建事件
     */
    @EventListener
    public void onDocumentCreated(VfsDocumentCreatedEvent event) {
        // 1. 提取文档元数据
        DocumentMetadata metadata = extractMetadata(event.getDocument());
        
        // 2. 同步到知识资料库
        syncService.syncDocumentToKnowledgeBase(event.getKbId(), metadata);
        
        // 3. 触发索引更新
        syncService.triggerIndexUpdate(event.getKbId(), event.getDocId());
    }
    
    /**
     * 文档更新事件
     */
    @EventListener
    public void onDocumentUpdated(VfsDocumentUpdatedEvent event) {
        // 1. 检测变更内容
        ChangeSet changes = detectChanges(event.getOldVersion(), event.getNewVersion());
        
        // 2. 增量同步
        syncService.syncDocumentChanges(event.getKbId(), event.getDocId(), changes);
        
        // 3. 更新向量索引（仅更新变更的chunks）
        syncService.updateVectorIndex(event.getKbId(), event.getDocId(), changes);
    }
    
    /**
     * 文档删除事件
     */
    @EventListener
    public void onDocumentDeleted(VfsDocumentDeletedEvent event) {
        // 1. 从知识资料库移除
        syncService.removeFromKnowledgeBase(event.getKbId(), event.getDocId());
        
        // 2. 从向量库删除
        syncService.removeFromVectorStore(event.getKbId(), event.getDocId());
    }
}
```

#### 7.2.2 同步策略

| 同步模式 | 触发条件 | 延迟 | 适用场景 |
|---------|---------|------|---------|
| **实时同步** | 文档CRUD操作 | <100ms | 关键业务文档 |
| **近实时同步** | 批量事件聚合 | 1-5s | 一般业务文档 |
| **定时同步** | 定时任务触发 | 5-60min | 历史文档归档 |
| **手动同步** | 用户主动触发 | 即时 | 批量导入/全量重建 |

### 7.3 知识资料库与向量库RAG关系

#### 7.3.1 数据流向

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    知识资料库 → 向量库 → RAG 数据流                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【知识资料库层】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│  Document (原始文档)                                                        │
│       │                                                                     │
│       ├──▶ Metadata (元数据: title, author, tags, permissions)              │
│       │       │                                                             │
│       │       └──▶ KnowledgeBase.documents (关系型存储)                      │
│       │                                                                     │
│       └──▶ Content (内容)                                                   │
│               │                                                             │
│               ▼                                                             │
│  【文档处理层】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│       Chunking (文本分块)                                                   │
│               │                                                             │
│               ├──▶ Chunk 1: "..."                                           │
│               ├──▶ Chunk 2: "..."                                           │
│               └──▶ Chunk N: "..."                                           │
│                                                                             │
│               │                                                             │
│               ▼                                                             │
│       Embedding (向量化 - 调用LLM.embed())                                   │
│               │                                                             │
│               ├──▶ Vector 1: [0.1, 0.2, ...] (1536维)                       │
│               ├──▶ Vector 2: [0.3, 0.4, ...]                                │
│               └──▶ Vector N: [...]                                          │
│                                                                             │
│               │                                                             │
│               ▼                                                             │
│  【向量存储层】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│       VectorStore.insert()                                                  │
│               │                                                             │
│               ├──▶ 向量索引 (HNSW/IVF)                                       │
│               ├──▶ 元数据索引 (kb_id, doc_id, chunk_id)                      │
│               └──▶ 倒排索引 (可选，用于混合检索)                              │
│                                                                             │
│  【RAG检索层】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│       User Query                                                            │
│               │                                                             │
│               ▼                                                             │
│       LLM.embed(query) ──▶ Query Vector                                     │
│               │                                                             │
│               ▼                                                             │
│       VectorStore.search(queryVector, topK=5)                               │
│               │                                                             │
│               ├──▶ Result 1: {vector, score: 0.95, metadata}                │
│               ├──▶ Result 2: {vector, score: 0.89, metadata}                │
│               └──▶ ...                                                      │
│               │                                                             │
│               ▼                                                             │
│       KnowledgeBase.getDocumentsByIds()                                     │
│               │                                                             │
│               ├──▶ Doc 1: {content, metadata, chunks}                       │
│               └──▶ Doc 2: {content, metadata, chunks}                       │
│               │                                                             │
│               ▼                                                             │
│       Reranking (可选，使用更精确的模型重排序)                                │
│               │                                                             │
│               ▼                                                             │
│       Prompt Augmentation                                                   │
│               │                                                             │
│               ├──▶ System: "基于以下文档回答问题..."                          │
│               ├──▶ Context: [Doc 1, Doc 2, ...]                             │
│               └──▶ User: "{query}"                                          │
│               │                                                             │
│               ▼                                                             │
│       LLM.chat(augmentedPrompt) ──▶ Final Answer                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 7.3.2 关系矩阵

| 组件 | 存储内容 | 索引类型 | 检索方式 | 更新频率 |
|-----|---------|---------|---------|---------|
| **VFS** | 原始文档文件 | 文件路径索引 | 文件读写 | 高（实时） |
| **知识资料库** | 文档元数据、权限 | B+树索引 | SQL查询 | 中（分钟级） |
| **向量库** | 文本向量 | HNSW/IVF索引 | 相似度检索 | 低（批量） |
| **RAG** | 检索结果组合 | 内存缓存 | 语义检索 | 动态生成 |

#### 7.3.3 一致性保障

```java
/**
 * 一致性管理器
 */
@Service
public class KnowledgeConsistencyManager {
    
    @Autowired
    private VfsService vfsService;
    
    @Autowired
    private KnowledgeBaseService kbService;
    
    @Autowired
    private VectorStore vectorStore;
    
    /**
     * 检查一致性
     */
    public ConsistencyReport checkConsistency(String kbId) {
        ConsistencyReport report = new ConsistencyReport();
        
        // 1. 检查 VFS vs 知识资料库
        List<String> vfsDocs = vfsService.listDocuments(kbId);
        List<String> kbDocs = kbService.listDocumentIds(kbId);
        
        Set<String> vfsOnly = new HashSet<>(vfsDocs);
        vfsOnly.removeAll(kbDocs);
        report.setVfsOnlyDocuments(vfsOnly);
        
        Set<String> kbOnly = new HashSet<>(kbDocs);
        kbOnly.removeAll(vfsDocs);
        report.setKbOnlyDocuments(kbOnly);
        
        // 2. 检查知识资料库 vs 向量库
        for (String docId : kbDocs) {
            long kbChunkCount = kbService.getChunkCount(kbId, docId);
            long vectorCount = vectorStore.countByDocument(kbId, docId);
            
            if (kbChunkCount != vectorCount) {
                report.addInconsistency(docId, kbChunkCount, vectorCount);
            }
        }
        
        return report;
    }
    
    /**
     * 修复一致性
     */
    public void repairConsistency(String kbId, ConsistencyReport report) {
        // 1. 同步VFS到知识资料库
        for (String docId : report.getVfsOnlyDocuments()) {
            syncService.syncDocumentFromVfs(kbId, docId);
        }
        
        // 2. 清理知识资料库中已删除的文档
        for (String docId : report.getKbOnlyDocuments()) {
            kbService.deleteDocument(kbId, docId);
            vectorStore.deleteByDocument(kbId, docId);
        }
        
        // 3. 重建不一致的向量索引
        for (Inconsistency item : report.getInconsistencies()) {
            rebuildVectorIndex(kbId, item.getDocId());
        }
    }
}
```

### 7.4 多级缓存策略

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         多级缓存架构                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Level 1: VFS Cache (文件系统缓存)                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  - 热点文档缓存                                                       │   │
│  │  - 最近访问文档                                                       │   │
│  │  - TTL: 1小时                                                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  Level 2: Knowledge Base Cache (应用缓存)                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  - 文档元数据缓存 (Caffeine)                                          │   │
│  │  - 查询结果缓存                                                       │   │
│  │  - TTL: 30分钟                                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  Level 3: Vector Cache (向量缓存)                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  - 热点查询向量缓存                                                   │   │
│  │  - 检索结果缓存                                                       │   │
│  │  - TTL: 10分钟                                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  Level 4: RAG Context Cache (上下文缓存)                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  - 增强提示缓存                                                       │   │
│  │  - 会话上下文缓存                                                     │   │
│  │  - TTL: 会话级                                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.5 监控指标

| 指标 | 说明 | 阈值 | 告警级别 |
|-----|------|------|---------|
| **sync_latency** | 同步延迟 | >5s | Warning |
| **sync_error_rate** | 同步错误率 | >1% | Critical |
| **consistency_score** | 一致性评分 | <95% | Warning |
| **index_lag** | 索引延迟 | >100 docs | Warning |
| **cache_hit_rate** | 缓存命中率 | <80% | Info |
| **rag_latency** | RAG检索延迟 | >500ms | Warning |
| **vector_search_latency** | 向量检索延迟 | >100ms | Warning |
