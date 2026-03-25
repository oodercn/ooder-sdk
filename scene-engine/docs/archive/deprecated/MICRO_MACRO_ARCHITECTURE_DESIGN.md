# 微下大原则 - 基础设施与核心服务分层架构设计

## 一、架构设计原则

### 1.1 微下大原则定义

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           微下大原则架构分层                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【大服务向上】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│   大服务 = 通用基础设施服务，向上层提供标准化接口                                 │
│   - 向量数据库 (Milvus)                                                     │
│   - 文档存储 (MinIO/S3)                                                     │
│   - Embedding服务 (OpenAI/百度文心)                                          │
│                                                                             │
│   特点:                                                                     │
│   ✓ 独立部署，可复用                                                        │
│   ✓ 标准化接口，与业务解耦                                                   │
│   ✓ 可被多个上层服务共享                                                     │
│   ✓ 专业团队运维                                                            │
│                                                                             │
│                              ▲                                              │
│                              │ 标准化接口调用                                 │
│  【中间层】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                              │                                              │
│   适配层 = 将大服务接口适配为业务接口                                          │
│   - VectorStoreAdapter                                                      │
│   - DocumentStoreAdapter                                                    │
│   - EmbeddingServiceAdapter                                                 │
│                              │                                              │
│                              ▼                                              │
│  【微服务向下】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│   微服务 = 业务核心服务，向下依赖基础设施服务                                    │
│   - Scene-Engine (场景引擎)                                                  │
│   - KnowledgeBaseService (知识库服务)                                        │
│   - RagPipeline (RAG管道)                                                    │
│                                                                             │
│   特点:                                                                     │
│   ✓ 聚焦业务逻辑                                                            │
│   ✓ 轻量级，快速迭代                                                        │
│   ✓ 依赖注入基础设施                                                        │
│   ✓ 可独立扩展                                                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 核心设计思想

| 层级 | 职责 | 设计原则 |
|------|------|----------|
| **大服务层** | 提供通用基础设施能力 | 向上暴露标准化接口，不感知业务 |
| **适配层** | 接口转换和协议适配 | 将大服务接口转换为业务友好接口 |
| **微服务层** | 实现业务逻辑 | 向下依赖基础设施，专注业务价值 |

---

## 二、大服务向上 - 基础设施服务设计

### 2.1 向量数据库服务 (Milvus)

#### 2.1.1 服务定位

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Milvus向量数据库服务 (大服务)                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【服务边界】                                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Milvus Service                                                      │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │  Collection │  │  Partition  │  │    Index    │  │   Search    │ │   │
│  │  │   Manager   │  │   Manager   │  │   Manager   │  │   Engine    │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  │                                                                      │   │
│  │  对外接口: gRPC/REST API                                             │   │
│  │  - CreateCollection                                                  │   │
│  │  - InsertVectors                                                     │   │
│  │  - SearchVectors                                                     │   │
│  │  - DeleteVectors                                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  【部署方式】                                                                │
│  ├─ 独立部署: Docker / K8s                                                  │
│  ├─ 多租户: 支持Collection/Partition级别隔离                                 │
│  ├─ 高可用: 集群模式，自动故障转移                                            │
│  └─ 监控: Prometheus + Grafana                                              │
│                                                                             │
│  【向上暴露接口】                                                            │
│  ├─ gRPC: milvus.proto (标准接口)                                           │
│  ├─ REST: /v1/vector/*                                                      │
│  └─ SDK: Java/Python/Go SDK                                                 │
│                                                                             │
│  【不感知业务】                                                              │
│  ✗ 不知道Scene-Engine存在                                                   │
│  ✗ 不知道知识库概念                                                         │
│  ✗ 不知道RAG流程                                                            │
│  ✓ 只提供向量存储和检索能力                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 2.1.2 独立部署方案

```yaml
# milvus-deployment.yaml
# Milvus作为独立基础设施服务部署

apiVersion: apps/v1
kind: Deployment
metadata:
  name: milvus-standalone
  namespace: infrastructure
  labels:
    app: milvus
    tier: infrastructure
    service-type: vector-database
spec:
  replicas: 1
  selector:
    matchLabels:
      app: milvus
  template:
    metadata:
      labels:
        app: milvus
        tier: infrastructure
    spec:
      containers:
      - name: milvus
        image: milvusdb/milvus:v2.3.3
        command: ["milvus", "run", "standalone"]
        ports:
        - containerPort: 19530
          name: grpc
        - containerPort: 9091
          name: metrics
        env:
        - name: ETCD_ENDPOINTS
          value: "etcd:2379"
        - name: MINIO_ADDRESS
          value: "minio:9000"
        resources:
          requests:
            memory: "4Gi"
            cpu: "2"
          limits:
            memory: "8Gi"
            cpu: "4"
        volumeMounts:
        - name: milvus-data
          mountPath: /var/lib/milvus
      volumes:
      - name: milvus-data
        persistentVolumeClaim:
          claimName: milvus-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: milvus-service
  namespace: infrastructure
spec:
  selector:
    app: milvus
  ports:
  - port: 19530
    targetPort: 19530
    name: grpc
  - port: 9091
    targetPort: 9091
    name: metrics
  type: ClusterIP
```

#### 2.1.3 多租户隔离方案

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Milvus多租户隔离模型                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  租户A: Scene-Engine-Prod                                                   │
│  ├─ Collection: scene_engine_vectors                                        │
│  │   ├─ Partition: system (系统层)                                          │
│  │   ├─ Partition: template_daily_report (模板层)                           │
│  │   ├─ Partition: scene_abc123 (场景层)                                    │
│  │   └─ Partition: user_john_doe (用户层)                                   │
│  │                                                                          │
│  租户B: Scene-Engine-Test                                                   │
│  ├─ Collection: scene_engine_vectors_test                                   │
│  │   ├─ Partition: system                                                   │
│  │   └─ ...                                                                 │
│  │                                                                          │
│  租户C: Other-Service                                                       │
│  ├─ Collection: other_service_vectors                                       │
│  │   └─ ...                                                                 │
│                                                                             │
│  隔离级别:                                                                  │
│  ├─ 物理隔离: 不同Collection (最强隔离)                                      │
│  ├─ 逻辑隔离: 同一Collection不同Partition (中等隔离)                         │
│  └─ 标签隔离: 同一Partition不同metadata标签 (弱隔离)                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 文档存储服务 (MinIO)

#### 2.2.1 服务定位

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     MinIO文档存储服务 (大服务)                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【服务边界】                                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  MinIO Service                                                       │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   Bucket    │  │   Object    │  │   Access    │  │  Version    │ │   │
│  │  │   Manager   │  │   Store     │  │   Control   │  │  Control    │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  │                                                                      │   │
│  │  对外接口: S3-compatible API                                         │   │
│  │  - PutObject                                                         │   │
│  │  - GetObject                                                         │   │
│  │  - DeleteObject                                                      │   │
│  │  - ListObjects                                                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  【部署方式】                                                                │
│  ├─ 独立部署: Docker / K8s / 二进制                                         │
│  ├─ 分布式: Erasure Coding模式                                              │
│  ├─ 网关模式: 对接S3/Azure/GCS                                              │
│  └─ 高可用: 多节点集群                                                      │
│                                                                             │
│  【向上暴露接口】                                                            │
│  ├─ S3 API: 标准S3协议 (PutObject/GetObject/ListObjects)                    │
│  ├─ MinIO SDK: Java/Python/Go SDK                                           │
│  └─ Console: Web管理界面                                                    │
│                                                                             │
│  【不感知业务】                                                              │
│  ✗ 不知道知识库文档结构                                                     │
│  ✗ 不知道文档类型 (PDF/Word/Markdown)                                        │
│  ✗ 不知道文档权限模型                                                       │
│  ✓ 只提供对象存储能力                                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 2.2.2 独立部署方案

```yaml
# minio-deployment.yaml
# MinIO作为独立基础设施服务部署

apiVersion: apps/v1
kind: Deployment
metadata:
  name: minio
  namespace: infrastructure
  labels:
    app: minio
    tier: infrastructure
    service-type: object-storage
spec:
  replicas: 4  # 分布式模式
  selector:
    matchLabels:
      app: minio
  template:
    metadata:
      labels:
        app: minio
        tier: infrastructure
    spec:
      containers:
      - name: minio
        image: minio/minio:latest
        command:
        - minio
        - server
        - http://minio-{0...3}.minio.infrastructure.svc.cluster.local/data
        - --console-address
        - ":9001"
        env:
        - name: MINIO_ROOT_USER
          valueFrom:
            secretKeyRef:
              name: minio-credentials
              key: root-user
        - name: MINIO_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: minio-credentials
              key: root-password
        ports:
        - containerPort: 9000
          name: s3-api
        - containerPort: 9001
          name: console
        resources:
          requests:
            memory: "2Gi"
            cpu: "1"
          limits:
            memory: "4Gi"
            cpu: "2"
        volumeMounts:
        - name: minio-data
          mountPath: /data
      volumes:
      - name: minio-data
        persistentVolumeClaim:
          claimName: minio-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: minio-service
  namespace: infrastructure
spec:
  selector:
    app: minio
  ports:
  - port: 9000
    targetPort: 9000
    name: s3-api
  - port: 9001
    targetPort: 9001
    name: console
  type: ClusterIP
```

#### 2.2.3 Bucket设计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     MinIO Bucket设计                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Bucket: knowledge-documents                                                │
│  ├── 用途: 存储所有知识库文档                                                  │
│  ├── 访问策略: 私有 (Presigned URL访问)                                       │
│  └── 生命周期: 30天版本保留                                                   │
│                                                                             │
│  对象Key设计:                                                                │
│  {tenant}/{knowledge-base-type}/{knowledge-base-id}/{document-id}/{version} │
│                                                                             │
│  示例:                                                                      │
│  ├── scene-engine-prod/system/system-installation-guides/doc-001/v1         │
│  │   └── 系统知识库文档                                                      │
│  ├── scene-engine-prod/template/template-daily-report/doc-002/v1            │
│  │   └── 模板知识库文档                                                      │
│  ├── scene-engine-prod/scene/scene-abc123/doc-003/v1                        │
│  │   └── 场景知识库文档                                                      │
│  └── scene-engine-prod/user/user-john-doe/doc-004/v1                        │
│      └── 用户私有文档                                                        │
│                                                                             │
│  元数据标签:                                                                 │
│  ├─ x-amz-meta-kb-id: 知识库ID                                              │
│  ├─ x-amz-meta-doc-id: 文档ID                                               │
│  ├─ x-amz-meta-doc-type: 文档类型 (pdf/word/md)                              │
│  ├─ x-amz-meta-owner-id: 所有者ID                                           │
│  └─ x-amz-meta-upload-time: 上传时间                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Embedding服务 (OpenAI/百度文心)

#### 2.3.1 服务定位

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Embedding服务 (大服务)                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【服务边界】                                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Embedding Service                                                   │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   Text      │  │   Batch     │  │   Model     │  │   Cache     │ │   │
│  │  │   Embed     │  │   Embed     │  │   Manager   │  │   Layer     │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  │                                                                      │   │
│  │  对外接口: HTTP API                                                  │   │
│  │  - POST /v1/embeddings                                             │   │
│  │  - Input: text or array of texts                                   │   │
│  │  - Output: vector or array of vectors                              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  【部署方式】                                                                │
│  ├─ SaaS模式: OpenAI API / 百度文心API (推荐)                                │
│  ├─ 私有化部署: 本地Embedding模型 (如BGE/M3E)                                 │
│  └─ 混合模式: SaaS + 本地缓存                                               │
│                                                                             │
│  【向上暴露接口】                                                            │
│  ├─ OpenAI API: /v1/embeddings                                             │
│  ├─ 百度文心API: /rpc/2.0/ai_custom/v1/text_embedding                        │
│  └─ 本地模型: gRPC/REST API                                                 │
│                                                                             │
│  【不感知业务】                                                              │
│  ✗ 不知道知识库文档内容                                                     │
│  ✗ 不知道分块策略                                                           │
│  ✗ 不知道检索需求                                                           │
│  ✓ 只提供文本向量化能力                                                      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 2.3.2 服务集成方案

```yaml
# embedding-service-config.yaml
# Embedding服务配置

embedding:
  # 主服务提供商
  provider: openai
  
  openai:
    api-key: ${OPENAI_API_KEY}
    base-url: https://api.openai.com/v1
    model: text-embedding-3-small
    dimension: 1536
    max-batch-size: 100
    timeout-seconds: 30
    
  # 备用服务提供商
  fallback:
    provider: baidu
    baidu:
      api-key: ${BAIDU_API_KEY}
      secret-key: ${BAIDU_SECRET_KEY}
      model: embedding-v1
      dimension: 384
      
  # 本地缓存配置
  cache:
    enabled: true
    type: redis
    redis:
      host: redis.infrastructure.svc.cluster.local
      port: 6379
      ttl-hours: 24
      max-size: 100000
```

---

## 三、微服务向下 - Scene-Engine核心服务设计

### 3.1 服务架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Scene-Engine核心服务 (微服务)                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【业务核心层】                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     Scene-Engine                                     │   │
│  │                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │                  KnowledgeBaseService                        │   │   │
│  │  │  - 知识库CRUD                                                 │   │   │
│  │  │  - 文档管理                                                   │   │   │
│  │  │  - 权限控制                                                   │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  │                              │                                       │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │                    RagPipeline                               │   │   │
│  │  │  - 检索 (Retrieve)                                           │   │   │
│  │  │  - 增强 (Augment)                                            │   │   │
│  │  │  - 生成 (Generate)                                           │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  │                              │                                       │   │
│  │  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │  │                 DocumentProcessor                            │   │   │
│  │  │  - 文档解析                                                   │   │   │
│  │  │  - 文本分块                                                   │   │   │
│  │  │  - 元数据提取                                                 │   │   │
│  │  └─────────────────────────────────────────────────────────────┘   │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  【适配层】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      适配器层 (Adapter Layer)                        │   │
│  │                                                                      │   │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │   │
│  │  │ VectorStore     │  │ DocumentStore   │  │ Embedding       │     │   │
│  │  │ Adapter         │  │ Adapter         │  │ Service Adapter │     │   │
│  │  │                 │  │                 │  │                 │     │   │
│  │  │ - MilvusClient  │  │ - MinioClient   │  │ - OpenAIClient  │     │   │
│  │  │ - Collection    │  │ - S3Client      │  │ - BaiduClient   │     │   │
│  │  │   Manager       │  │ - AzureClient   │  │ - LocalClient   │     │   │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘     │   │
│  │                                                                      │   │
│  │  职责:                                                               │   │
│  │  ├─ 将大服务接口转换为业务接口                                         │   │
│  │  ├─ 处理连接管理、重试、熔断                                           │   │
│  │  ├─ 数据格式转换                                                      │   │
│  │  └─ 错误处理和降级策略                                                 │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  【依赖注入】━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    基础设施服务 (通过配置注入)                         │   │
│  │                                                                      │   │
│  │  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐         │   │
│  │  │   Milvus    │      │    MinIO    │      │  Embedding  │         │   │
│  │  │  Service    │      │   Service   │      │   Service   │         │   │
│  │  │  (外部)      │      │   (外部)     │      │   (外部)     │         │   │
│  │  │             │      │             │      │             │         │   │
│  │  │ host:port   │      │ endpoint    │      │ api-key     │         │   │
│  │  │             │      │             │      │             │         │   │
│  │  │ 可替换为:    │      │ 可替换为:    │      │ 可替换为:    │         │   │
│  │  │ - Pinecone  │      │ - S3        │      │ - 百度文心   │         │   │
│  │  │ - Weaviate  │      │ - Azure     │      │ - 本地模型   │         │   │
│  │  │ - Chroma    │      │ - GCS       │      │             │         │   │
│  │  └─────────────┘      └─────────────┘      └─────────────┘         │   │
│  │                                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 适配器设计

#### 3.2.1 VectorStoreAdapter

```java
/**
 * 向量存储适配器
 * 将Milvus接口适配为业务友好的VectorStore接口
 */
@Component
public class MilvusVectorStoreAdapter implements VectorStore {
    
    private final MilvusServiceClient milvusClient;
    private final String collectionName;
    
    public MilvusVectorStoreAdapter(
            @Value("${milvus.host}") String host,
            @Value("${milvus.port}") int port,
            @Value("${milvus.collection}") String collectionName) {
        this.milvusClient = new MilvusServiceClient(
            ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .build()
        );
        this.collectionName = collectionName;
    }
    
    @Override
    public void insert(String id, float[] vector, Map<String, Object> metadata) {
        // 将业务接口转换为Milvus接口
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("id", Collections.singletonList(id)));
        fields.add(new InsertParam.Field("vector", Collections.singletonList(vector)));
        fields.add(new InsertParam.Field("metadata", Collections.singletonList(metadata)));
        
        InsertParam insertParam = InsertParam.newBuilder()
            .withCollectionName(collectionName)
            .withFields(fields)
            .build();
        
        milvusClient.insert(insertParam);
    }
    
    @Override
    public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
        // 构建Milvus搜索参数
        SearchParam searchParam = SearchParam.newBuilder()
            .withCollectionName(collectionName)
            .withVectors(Collections.singletonList(queryVector))
            .withVectorFieldName("vector")
            .withTopK(topK)
            .withExpr(buildFilterExpr(filters))
            .build();
        
        SearchResults results = milvusClient.search(searchParam).getData();
        
        // 转换为业务SearchResult
        return convertToSearchResults(results);
    }
    
    private String buildFilterExpr(Map<String, Object> filters) {
        // 将业务过滤器转换为Milvus表达式
        // 例如: {"kbId": "scene-abc123"} -> "kbId == 'scene-abc123'"
        return filters.entrySet().stream()
            .map(e -> String.format("%s == '%s'", e.getKey(), e.getValue()))
            .collect(Collectors.joining(" and "));
    }
}
```

#### 3.2.2 DocumentStoreAdapter

```java
/**
 * 文档存储适配器
 * 将MinIO/S3接口适配为业务友好的DocumentStore接口
 */
@Component
public class MinioDocumentStoreAdapter implements DocumentStore {
    
    private final MinioClient minioClient;
    private final String bucketName;
    
    public MinioDocumentStoreAdapter(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket}") String bucketName) {
        this.minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
        this.bucketName = bucketName;
    }
    
    @Override
    public String storeDocument(String kbId, String docId, byte[] content, 
                                 String contentType, Map<String, String> metadata) {
        // 构建对象Key: {tenant}/{kb-type}/{kb-id}/{doc-id}/{version}
        String objectKey = buildObjectKey(kbId, docId);
        
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(new ByteArrayInputStream(content), content.length, -1)
                    .contentType(contentType)
                    .userMetadata(metadata)
                    .build()
            );
            return objectKey;
        } catch (Exception e) {
            throw new DocumentStoreException("Failed to store document", e);
        }
    }
    
    @Override
    public DocumentContent retrieveDocument(String objectKey) {
        try {
            GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build()
            );
            
            return DocumentContent.builder()
                .content(response.readAllBytes())
                .contentType(response.headers().get("Content-Type"))
                .metadata(response.headers())
                .build();
        } catch (Exception e) {
            throw new DocumentStoreException("Failed to retrieve document", e);
        }
    }
    
    private String buildObjectKey(String kbId, String docId) {
        // 根据kbId解析租户和类型
        String tenant = "scene-engine-prod";
        String kbType = resolveKbType(kbId);
        String version = "v1";
        
        return String.format("%s/%s/%s/%s/%s", 
            tenant, kbType, kbId, docId, version);
    }
}
```

#### 3.2.3 EmbeddingServiceAdapter

```java
/**
 * Embedding服务适配器
 * 将OpenAI/百度文心API适配为业务友好的EmbeddingService接口
 */
@Component
public class OpenAiEmbeddingServiceAdapter implements EmbeddingService {
    
    private final OpenAiClient openAiClient;
    private final String model;
    private final EmbeddingCache cache;
    
    public OpenAiEmbeddingServiceAdapter(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model}") String model,
            EmbeddingCache cache) {
        this.openAiClient = OpenAiClient.builder()
            .apiKey(apiKey)
            .build();
        this.model = model;
        this.cache = cache;
    }
    
    @Override
    public float[] embed(String text) {
        // 检查缓存
        String cacheKey = generateCacheKey(text);
        float[] cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // 调用OpenAI API
        EmbeddingRequest request = EmbeddingRequest.builder()
            .model(model)
            .input(text)
            .build();
        
        EmbeddingResponse response = openAiClient.embeddings().create(request);
        float[] embedding = response.getData().get(0).getEmbedding();
        
        // 存入缓存
        cache.put(cacheKey, embedding);
        
        return embedding;
    }
    
    @Override
    public List<float[]> embedBatch(List<String> texts) {
        // 批量向量化，提高性能
        List<float[]> results = new ArrayList<>();
        
        // 分批处理 (OpenAI最大batch size为2048)
        int batchSize = 100;
        for (int i = 0; i < texts.size(); i += batchSize) {
            List<String> batch = texts.subList(i, Math.min(i + batchSize, texts.size()));
            
            EmbeddingRequest request = EmbeddingRequest.builder()
                .model(model)
                .input(batch)
                .build();
            
            EmbeddingResponse response = openAiClient.embeddings().create(request);
            
            for (EmbeddingData data : response.getData()) {
                results.add(data.getEmbedding());
            }
        }
        
        return results;
    }
    
    private String generateCacheKey(String text) {
        return "embedding:" + DigestUtils.md5Hex(text);
    }
}
```

---

## 四、完整架构图

### 4.1 部署架构

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    部署架构全景图                                        │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  【基础设施层 - Infrastructure Layer】 (大服务向上)                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                                                                                  │   │
│  │  Namespace: infrastructure                                                       │   │
│  │  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐     │   │
│  │  │   Milvus Cluster    │  │   MinIO Cluster     │  │   Redis Cluster     │     │   │
│  │  │   (向量数据库)       │  │   (对象存储)         │  │   (缓存/消息)        │     │   │
│  │  │                     │  │                     │  │                     │     │   │
│  │  │  - Collection管理   │  │  - Bucket管理       │  │  - Embedding缓存    │     │   │
│  │  │  - Partition隔离    │  │  - Object存储       │  │  - 会话缓存         │     │   │
│  │  │  - Vector索引       │  │  - 版本控制         │  │  - 分布式锁         │     │   │
│  │  │  - 相似度检索       │  │  - 生命周期管理      │  │                     │     │   │
