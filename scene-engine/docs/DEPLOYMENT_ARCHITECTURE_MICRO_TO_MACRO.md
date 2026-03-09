# 微下大原则 - 分层部署架构设计

## 架构设计原则：微服务向下，大服务向上

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              架构分层原则说明                                             │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│   向上（大服务）：Embedding服务、LLM服务、外部API → 作为独立服务，通过接口调用              │
│   向下（微服务）：向量存储、文档存储、缓存 → 可内嵌、可独立、可集群                       │
│                                                                                         │
│   核心原则：                                                                             │
│   1. 基础设施服务（存储类）向下兼容，支持从嵌入式到分布式的平滑扩展                        │
│   2. 智能服务（AI类）向上抽象，统一接口，支持多厂商切换                                   │
│   3. Scene-Engine核心保持轻量，依赖外部服务而非包含外部服务                               │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 一、三种部署模式总览

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              三种部署模式对比                                             │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  【微型服务 - 个人客户端部署】                                                            │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  Scene-Engine (嵌入式全内置)                                                      │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │  InMemory   │  │  InMemory   │  │   SQLite    │  │   外部API    │            │   │
│  │  │ VectorStore │  │ DocumentStore│  │   元数据    │  │  Embedding  │            │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘            │   │
│  │                                                                                  │   │
│  │  特点：零配置、单进程、无外部依赖、适合个人开发测试                                    │   │
│  │  资源：内存<2GB、磁盘<10GB、无GPU要求                                               │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  【小型服务 - 局域网单机部署】                                                            │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  Docker Compose 单机部署                                                          │   │
│  │                                                                                  │   │
│  │  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐              │   │
│  │  │  Scene-Engine   │◄──►│    ChromaDB     │◄──►│    MinIO        │              │   │
│  │  │   (轻量核心)     │    │  (向量存储)      │    │  (文档存储)      │              │   │
│  │  └────────┬────────┘    └─────────────────┘    └─────────────────┘              │   │
│  │           │                                                                      │   │
│  │           └──────────────────────────────────────────────────────────┐          │   │
│  │                                                                      ▼          │   │
│  │                                                           ┌─────────────────┐   │   │
│  │                                                           │  外部Embedding  │   │   │
│  │                                                           │  (OpenAI/文心)  │   │   │
│  │                                                           └─────────────────┘   │   │
│  │                                                                                  │   │
│  │  特点：Docker Compose、单机多容器、适合小团队/局域网                                  │   │
│  │  资源：4核8GB、磁盘100GB、可共享GPU                                                  │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  【大型服务 - 分布式部署】                                                                │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │  Kubernetes 分布式集群                                                            │   │
│  │                                                                                  │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │  SE-Core    │  │  SE-Core    │  │  SE-Core    │  │  SE-Gateway │            │   │
│  │  │  (实例1)     │  │  (实例2)     │  │  (实例N)     │  │  (网关)      │            │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └─────────────┘            │   │
│  │         │                │                │                                     │   │
│  │         └────────────────┴────────────────┘                                     │   │
│  │                              │                                                   │   │
│  │                              ▼                                                   │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐    │   │
│  │  │                      基础设施服务层 (独立集群)                              │    │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │    │   │
│  │  │  │   Milvus    │  │    MinIO    │  │    Redis    │  │   Kafka     │    │    │   │
│  │  │  │   Cluster   │  │   Cluster   │  │   Cluster   │  │   Cluster   │    │    │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │    │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘    │   │
│  │                              │                                                   │   │
│  │                              ▼                                                   │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐    │   │
│  │  │                      智能服务层 (外部/独立)                                 │    │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │    │   │
│  │  │  │ OpenAI API  │  │  百度文心    │  │  讯飞星火    │  │  本地LLM    │    │    │   │
│  │  │  │  (Embedding)│  │ (Embedding) │  │ (Embedding) │  │  (vLLM)     │    │    │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │    │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘    │   │
│  │                                                                                  │   │
│  │  特点：K8s集群、微服务拆分、高可用、水平扩展、适合企业级生产环境                        │   │
│  │  资源：多节点、16核+64GB+、SSD存储、GPU集群                                          │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、微型服务 - 个人客户端部署

### 2.1 架构特点

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           个人客户端部署架构                                              │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  【嵌入式全内置模式】                                                                    │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                         Scene-Engine 单进程                                       │   │
│  │                                                                                  │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │                      核心服务层 (Core Services)                            │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │   │   │
│  │  │  │  Lifecycle  │  │   Config    │  │    Menu     │  │  Activation │    │   │   │
│  │  │  │  Management │  │   Center    │  │  Generation │  │    Flow     │    │   │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │                              │                                                   │   │
│  │                              ▼                                                   │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │                      知识增强层 (Embedded)                                 │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │   │   │
│  │  │  │  InMemory   │  │  InMemory   │  │   SQLite    │  │   Mock      │    │   │   │
│  │  │  │ VectorStore │  │ DocStorage  │  │  Metadata   │  │  Embedding  │    │   │   │
│  │  │  │  (内存向量)  │  │  (内存文档)  │  │  (本地元数据)│  │  (模拟/本地) │    │   │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │                              │                                                   │   │
│  │                              ▼                                                   │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │                      外部服务接口 (External APIs)                          │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │   │   │
│  │  │  │  OpenAI     │  │   百度      │  │   讯飞      │  │   Ollama    │    │   │   │
│  │  │  │  (可选)     │  │  (可选)      │  │  (可选)      │  │  (本地LLM)  │    │   │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │                                                                                  │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 技术选型

| 组件 | 实现方案 | 说明 |
|------|----------|------|
| **向量存储** | `InMemoryVectorStore` | 内存存储，重启丢失，适合开发 |
| **文档存储** | `InMemoryDocumentStore` | 内存存储，Base64编码 |
| **元数据存储** | SQLite | 本地文件数据库，零配置 |
| **Embedding** | Mock / Ollama本地 | 可选外部API或本地模型 |
| **LLM服务** | Mock / Ollama本地 | 可选外部API或本地模型 |

### 2.3 配置文件

```yaml
# application-micro.yml
# 微型服务 - 个人客户端配置

server:
  port: 8080

scene-engine:
  deployment-mode: micro
  
  # 向量存储 - 嵌入式内存实现
  vector-store:
    type: in-memory
    dimension: 1536
    max-size: 10000  # 最大向量数
    
  # 文档存储 - 嵌入式内存实现
  document-store:
    type: in-memory
    max-size: 100MB
    
  # 元数据存储 - SQLite
  metadata-store:
    type: sqlite
    url: jdbc:sqlite:./data/scene-engine.db
    
  # Embedding服务 - 本地优先
  embedding:
    type: local  # local / openai / baidu / xunfei
    local:
      provider: ollama
      model: nomic-embed-text
      url: http://localhost:11434
    openai:
      api-key: ${OPENAI_API_KEY:}
      model: text-embedding-3-small
      
  # LLM服务 - 本地优先
  llm:
    type: local  # local / openai / baidu / xunfei
    local:
      provider: ollama
      model: llama3.1:8b
      url: http://localhost:11434
    openai:
      api-key: ${OPENAI_API_KEY:}
      model: gpt-4o-mini

# 日志配置
logging:
  level:
    net.ooder.scene: DEBUG
```

### 2.4 启动方式

```bash
# 方式1: 直接启动（嵌入式模式）
java -jar scene-engine.jar --spring.profiles.active=micro

# 方式2: 使用H2/SQLite内存数据库（纯内存模式）
java -jar scene-engine.jar \
  --scene-engine.vector-store.type=in-memory \
  --scene-engine.document-store.type=in-memory \
  --scene-engine.metadata-store.type=h2-memory

# 方式3: 集成Ollama本地模型
java -jar scene-engine.jar \
  --scene-engine.embedding.type=local \
  --scene-engine.llm.type=local \
  --scene-engine.local.provider=ollama
```

### 2.5 资源需求

| 资源类型 | 最低配置 | 推荐配置 |
|----------|----------|----------|
| CPU | 2核 | 4核 |
| 内存 | 2GB | 4GB |
| 磁盘 | 10GB SSD | 50GB SSD |
| 网络 | 可选外网 | 可选外网 |
| GPU | 无 | 可选（本地LLM加速） |

---

## 三、小型服务 - 局域网单机部署

### 3.1 架构特点

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           局域网单机部署架构                                              │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  【Docker Compose 单机多容器】                                                           │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                         Docker Host (单机)                                        │   │
│  │                                                                                  │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Container: scene-engine (轻量核心)                                        │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │   │   │
│  │  │  │  Lifecycle  │  │   Config    │  │    Menu     │  │  Activation │    │   │   │
│  │  │  │  Management │  │   Center    │  │  Generation │  │    Flow     │    │   │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │   │   │
│  │  │                                                                                  │   │   │
│  │  │  依赖: ChromaDB Client, MinIO Client, PostgreSQL Client                      │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │           │                                                                      │   │
│  │           │ 网络互通 (Docker Network)                                              │   │
│  │           ▼                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Container: chromadb (向量存储) - 轻量级替代Milvus                            │   │   │
│  │  │  ├─ 端口: 8000                                                           │   │   │
│  │  │  ├─ 存储: /data/chroma (Docker Volume)                                    │   │   │
│  │  │  └─ 特点: 轻量、易部署、支持持久化                                         │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │           │                                                                      │   │
│  │           ▼                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Container: minio (文档存储)                                               │   │   │
│  │  │  ├─ 端口: 9000 (API), 9001 (Console)                                      │   │   │
│  │  │  ├─ 存储: /data/minio (Docker Volume)                                     │   │   │
│  │  │  └─ 桶: documents, guides, backups                                        │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │           │                                                                      │   │
│  │           ▼                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Container: postgres (元数据存储)                                          │   │   │
│  │  │  ├─ 端口: 5432                                                           │   │   │
│  │  │  ├─ 数据库: scene_engine                                                 │   │   │
│  │  │  └─ 存储: /data/postgres (Docker Volume)                                  │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │                                                                                  │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  外部服务 (通过局域网/互联网访问):                                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                                     │
│  │  OpenAI API │  │  百度文心    │  │  讯飞星火    │                                     │
│  │  (Embedding)│  │  (Embedding)│  │  (Embedding)│                                     │
│  └─────────────┘  └─────────────┘  └─────────────┘                                     │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 技术选型

| 组件 | 实现方案 | 说明 |
|------|----------|------|
| **向量存储** | ChromaDB | 轻量级，比Milvus更易部署 |
| **文档存储** | MinIO | 对象存储，兼容S3 API |
| **元数据存储** | PostgreSQL | 生产级关系数据库 |
| **Embedding** | 外部API | OpenAI/百度/讯飞 |
| **LLM服务** | 外部API | OpenAI/百度/讯飞 |

### 3.3 Docker Compose配置

```yaml
# docker-compose-small.yml
# 小型服务 - 局域网单机部署

version: '3.8'

services:
  # Scene-Engine 核心服务
  scene-engine:
    image: ooder/scene-engine:latest
    container_name: scene-engine
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=small
      - SCENE_ENGINE_VECTOR_STORE_TYPE=chromadb
      - SCENE_ENGINE_VECTOR_STORE_HOST=chromadb
      - SCENE_ENGINE_VECTOR_STORE_PORT=8000
      - SCENE_ENGINE_DOCUMENT_STORE_TYPE=minio
      - SCENE_ENGINE_MINIO_ENDPOINT=http://minio:9000
      - SCENE_ENGINE_MINIO_ACCESS_KEY=minioadmin
      - SCENE_ENGINE_MINIO_SECRET_KEY=minioadmin
      - SCENE_ENGINE_METADATA_STORE_TYPE=postgresql
      - SCENE_ENGINE_POSTGRES_HOST=postgres
      - SCENE_ENGINE_POSTGRES_PORT=5432
      - SCENE_ENGINE_POSTGRES_DB=scene_engine
      - SCENE_ENGINE_POSTGRES_USER=scene_engine
      - SCENE_ENGINE_POSTGRES_PASSWORD=scene_engine_pass
      - SCENE_ENGINE_EMBEDDING_TYPE=openai
      - OPENAI_API_KEY=${OPENAI_API_KEY}
    depends_on:
      - chromadb
      - minio
      - postgres
    networks:
      - scene-engine-network
    restart: unless-stopped

  # ChromaDB - 轻量级向量数据库
  chromadb:
    image: chromadb/chroma:latest
    container_name: scene-engine-chromadb
    ports:
      - "8000:8000"
    volumes:
      - chroma-data:/chroma/chroma
    environment:
      - IS_PERSISTENT=TRUE
      - PERSIST_DIRECTORY=/chroma/chroma
      - ANONYMIZED_TELEMETRY=FALSE
    networks:
      - scene-engine-network
    restart: unless-stopped

  # MinIO - 对象存储
  minio:
    image: minio/minio:latest
    container_name: scene-engine-minio
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio-data:/data
    environment:
      - MINIO_ROOT_USER=minioadmin
      - MINIO_ROOT_PASSWORD=minioadmin
    command: server /data --console-address ":9001"
    networks:
      - scene-engine-network
    restart: unless-stopped

  # PostgreSQL - 元数据存储
  postgres:
    image: postgres:15-alpine
    container_name: scene-engine-postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    environment:
      - POSTGRES_DB=scene_engine
      - POSTGRES_USER=scene_engine
      - POSTGRES_PASSWORD=scene_engine_pass
    networks:
      - scene-engine-network
    restart: unless-stopped

  # MinIO初始化 - 创建默认存储桶
  minio-init:
    image: minio/mc:latest
    depends_on:
      - minio
    entrypoint: >
      /bin/sh -c "
      sleep 10;
      /usr/bin/mc config host add myminio http://minio:9000 minioadmin minioadmin;
      /usr/bin/mc mb myminio/documents;
      /usr/bin/mc mb myminio/guides;
      /usr/bin/mc mb myminio/backups;
      /usr/bin/mc policy set public myminio/documents;
      exit 0;
      "
    networks:
      - scene-engine-network

volumes:
  chroma-data:
    driver: local
  minio-data:
    driver: local
  postgres-data:
    driver: local

networks:
  scene-engine-network:
    driver: bridge
```

### 3.4 配置文件

```yaml
# application-small.yml
# 小型服务 - 局域网单机配置

server:
  port: 8080

scene-engine:
  deployment-mode: small
  
  # 向量存储 - ChromaDB
  vector-store:
    type: chromadb
    host: ${CHROMADB_HOST:localhost}
    port: ${CHROMADB_PORT:8000}
    dimension: 1536
    metric-type: cosine
    
  # 文档存储 - MinIO
  document-store:
    type: minio
    endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
    access-key: ${MINIO_ACCESS_KEY:minioadmin}
    secret-key: ${MINIO_SECRET_KEY:minioadmin}
    bucket: documents
    
  # 元数据存储 - PostgreSQL
  metadata-store:
    type: postgresql
    host: ${POSTGRES_HOST:localhost}
    port: ${POSTGRES_PORT:5432}
    database: ${POSTGRES_DB:scene_engine}
    username: ${POSTGRES_USER:scene_engine}
    password: ${POSTGRES_PASSWORD:}
    
  # Embedding服务 - 外部API
  embedding:
    type: ${EMBEDDING_TYPE:openai}
    openai:
      api-key: ${OPENAI_API_KEY}
      model: text-embedding-3-small
      base-url: https://api.openai.com/v1
    baidu:
      api-key: ${BAIDU_API_KEY}
      secret-key: ${BAIDU_SECRET_KEY}
      
  # LLM服务 - 外部API
  llm:
    type: ${LLM_TYPE:openai}
    openai:
      api-key: ${OPENAI_API_KEY}
      model: gpt-4o-mini
    baidu:
      api-key: ${BAIDU_API_KEY}
      secret-key: ${BAIDU_SECRET_KEY}
```

### 3.5 启动方式

```bash
# 启动所有服务
docker-compose -f docker-compose-small.yml up -d

# 查看服务状态
docker-compose -f docker-compose-small.yml ps

# 查看日志
docker-compose -f docker-compose-small.yml logs -f scene-engine

# 停止服务
docker-compose -f docker-compose-small.yml down

# 停止并删除数据卷
docker-compose -f docker-compose-small.yml down -v
```

### 3.6 资源需求

| 资源类型 | 最低配置 | 推荐配置 |
|----------|----------|----------|
| CPU | 4核 | 8核 |
| 内存 | 8GB | 16GB |
| 磁盘 | 100GB SSD | 500GB SSD |
| 网络 | 局域网/互联网 | 千兆局域网 |
| GPU | 无 | 可选 |

---

## 四、大型服务 - 分布式部署

### 4.1 架构特点

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           分布式部署架构                                                  │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  【Kubernetes 集群部署】                                                                 │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                      Ingress / API Gateway 层                                     │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │   Nginx     │  │    Kong     │  │  Traefik    │  │  AWS ALB    │            │   │
│  │  │  (开源)     │  │  (企业)     │  │  (云原生)    │  │  (云厂商)    │            │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘            │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                                  │
│                                      ▼                                                  │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                      Scene-Engine 服务层 (多实例)                                 │   │
│  │                                                                                  │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │  SE-Core    │  │  SE-Core    │  │  SE-Core    │  │  SE-Core    │            │   │
│  │  │  (Pod 1)    │  │  (Pod 2)    │  │  (Pod 3)    │  │  (Pod N)    │            │   │
│  │  │  ├─ Core    │  │  ├─ Core    │  │  ├─ Core    │  │  ├─ Core    │            │   │
│  │  │  ├─ API     │  │  ├─ API     │  │  ├─ API     │  │  ├─ API     │            │   │
│  │  │  └─ Worker  │  │  └─ Worker  │  │  └─ Worker  │  │  └─ Worker  │            │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘            │   │
│  │                                                                                  │   │
│  │  HPA: 根据CPU/内存/QPS自动扩缩容 (min: 3, max: 20)                                │   │
│  │  PDB: 保证至少2个实例可用                                                         │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                                  │
│                                      ▼                                                  │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                      基础设施服务层 (独立集群/命名空间)                             │   │
│  │                                                                                  │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Milvus Cluster (向量数据库)                                              │   │   │
│  │  │  ├─ Milvus Proxy (2+ replicas)                                           │   │   │
│  │  │  ├─ Milvus Query Node (3+ replicas)                                      │   │   │
│  │  │  ├─ Milvus Data Node (2+ replicas)                                       │   │   │
│  │  │  ├─ Milvus Index Node (2+ replicas)                                      │   │   │
│  │  │  ├─ Etcd Cluster (3 replicas)                                            │   │   │
│  │  │  └─ MinIO Cluster (4+ replicas)                                          │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │                                                                                  │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Object Storage (文档存储)                                                │   │   │
│  │  │  ├─ MinIO Cluster (自建)                                                  │   │   │
│  │  │  ├─ AWS S3                                                                │   │   │
│  │  │  ├─ Alibaba Cloud OSS                                                     │   │   │
│  │  │  └─ Azure Blob Storage                                                    │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │                                                                                  │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  PostgreSQL Cluster (元数据存储)                                          │   │   │
│  │  │  ├─ Primary (1) + Replica (2)                                            │   │   │
│  │  │  ├─ Patroni (HA管理)                                                      │   │   │
│  │  │  └─ PgBouncer (连接池)                                                    │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │                                                                                  │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Redis Cluster (缓存/消息)                                                │   │   │
│  │  │  ├─ Master (3) + Replica (3)                                             │   │   │
│  │  │  ├─ Sentinel (HA)                                                        │   │   │
│  │  │  └─ Cluster Mode                                                          │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  │                                                                                  │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Kafka Cluster (事件流)                                                   │   │   │
│  │  │  ├─ Broker (3+ replicas)                                                 │   │   │
│  │  │  ├─ Zookeeper (3 replicas)                                               │   │   │
│  │  │  └─ Schema Registry                                                       │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                      智能服务层 (外部/独立)                                         │   │
│  │                                                                                  │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │  OpenAI     │  │   百度      │  │   讯飞      │  │   vLLM      │            │   │
│  │  │  (云端API)  │  │  (云端API)  │  │  (云端API)  │  │  (本地集群)  │            │   │
│  │  │  ├─ GPT-4   │  │  ├─ ERNIE   │  │  ├─ Spark   │  │  ├─ GPU节点  │            │   │
│  │  │  ├─ Embedding│  │  ├─ Embedding│  │  ├─ Embedding│  │  ├─ 模型服务 │            │   │
│  │  │  └─ DALL-E  │  │  └─ 文心一言  │  │  └─ 星火大模型│  │  └─ 推理引擎  │            │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘            │   │
│  │                                                                                  │   │
│  │  统一接口适配层: EmbeddingService, LlmService (多厂商切换)                         │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                      可观测性层 (Observability)                                   │   │
│  │                                                                                  │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │ Prometheus  │  │   Grafana   │  │    Jaeger   │  │    ELK      │            │   │
│  │  │  (指标采集)  │  │  (可视化)    │  │  (链路追踪)  │  │  (日志分析)  │            │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘            │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 技术选型

| 组件 | 实现方案 | 部署方式 | 高可用方案 |
|------|----------|----------|------------|
| **向量存储** | Milvus Cluster | K8s StatefulSet | 多节点+Etcd |
| **文档存储** | MinIO Cluster / S3 | K8s / 云厂商 | 多副本+纠删码 |
| **元数据存储** | PostgreSQL + Patroni | K8s StatefulSet | 主从+自动故障转移 |
| **缓存** | Redis Cluster | K8s StatefulSet | 主从+Sentinel |
| **消息队列** | Kafka | K8s StatefulSet | 多Broker+副本 |
| **Embedding** | 多厂商API | 外部服务 | 降级策略 |
| **LLM服务** | 多厂商API / vLLM | 外部/本地GPU | 多实例负载均衡 |

### 4.3 Kubernetes部署配置

#### 4.3.1 Namespace划分

```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: scene-engine
  labels:
    app.kubernetes.io/name: scene-engine
    app.kubernetes.io/component: platform

---
apiVersion: v1
kind: Namespace
metadata:
  name: scene-engine-data
  labels:
    app.kubernetes.io/name: scene-engine-data
    app.kubernetes.io/component: infrastructure

---
apiVersion: v1
kind: Namespace
metadata:
  name: scene-engine-observability
  labels:
    app.kubernetes.io/name: scene-engine-observability
    app.kubernetes.io/component: observability
```

#### 4.3.2 Scene-Engine Core Deployment

```yaml
# scene-engine-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: scene-engine-core
  namespace: scene-engine
  labels:
    app: scene-engine-core
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: scene-engine-core
  template:
    metadata:
      labels:
        app: scene-engine-core
    spec:
      containers:
        - name: scene-engine
          image: ooder/scene-engine:latest
          ports:
            - containerPort: 8080
              name: http
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "large"
            - name: SCENE_ENGINE_VECTOR_STORE_TYPE
              value: "milvus"
            - name: SCENE_ENGINE_MILVUS_HOST
              value: "milvus-proxy.scene-engine-data.svc.cluster.local"
            - name: SCENE_ENGINE_MILVUS_PORT
              value: "19530"
            - name: SCENE_ENGINE_DOCUMENT_STORE_TYPE
              value: "minio"
            - name: SCENE_ENGINE_MINIO_ENDPOINT
              value: "http://minio.scene-engine-data.svc.cluster.local:9000"
            - name: SCENE_ENGINE_METADATA_STORE_TYPE
              value: "postgresql"
            - name: SCENE_ENGINE_POSTGRES_HOST
              value: "postgres-pgbouncer.scene-engine-data.svc.cluster.local"
            - name: SCENE_ENGINE_REDIS_HOST
              value: "redis.scene-engine-data.svc.cluster.local"
            - name: SCENE_ENGINE_KAFKA_BOOTSTRAP_SERVERS
              value: "kafka.scene-engine-data.svc.cluster.local:9092"
          resources:
            requests:
              memory: "2Gi"
              cpu: "1000m"
            limits:
              memory: "4Gi"
              cpu: "2000m"
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: scene-engine-core-hpa
  namespace: scene-engine
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: scene-engine-core
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 10
          periodSeconds: 60
---
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: scene-engine-core-pdb
  namespace: scene-engine
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: scene-engine-core
```

#### 4.3.3 Milvus Helm部署

```bash
# 添加Milvus Helm仓库
helm repo add milvus https://milvus-io.github.io/milvus-helm/
helm repo update

# 安装Milvus集群（生产配置）
helm install milvus milvus/milvus \
  --namespace scene-engine-data \
  --create-namespace \
  --set cluster.enabled=true \
  --set etcd.replicaCount=3 \
  --set etcd.persistence.enabled=true \
  --set etcd.persistence.storageClassName=fast-ssd \
  --set etcd.persistence.size=10Gi \
  --set minio.mode=distributed \
  --set minio.replicas=4 \
  --set minio.persistence.enabled=true \
  --set minio.persistence.storageClassName=fast-ssd \
  --set minio.persistence.size=100Gi \
  --set pulsar.enabled=true \
  --set pulsar.broker.replicaCount=3 \
  --set proxy.replicas=2 \
  --set queryNode.replicas=3 \
  --set queryNode.resources.requests.memory=8Gi \
  --set queryNode.resources.requests.cpu=2000m \
  --set queryNode.resources.limits.memory=16Gi \
  --set queryNode.resources.limits.cpu=4000m
```

#### 4.3.4 配置文件

```yaml
# application-large.yml
# 大型服务 - 分布式配置

server:
  port: 8080
  tomcat:
    max-threads: 200
    min-spare-threads: 20

scene-engine:
  deployment-mode: large
  
  # 向量存储 - Milvus Cluster
  vector-store:
    type: milvus
    host: ${MILVUS_HOST:milvus-proxy.scene-engine-data.svc.cluster.local}
    port: ${MILVUS_PORT:19530}
    dimension: 1536
    metric-type: cosine
    collection: knowledge_base_vectors
    consistency-level: Bounded
    
  # 文档存储 - MinIO Cluster / S3
  document-store:
    type: ${DOC_STORE_TYPE:minio}  # minio / s3 / oss
    minio:
      endpoint: ${MINIO_ENDPOINT}
      access-key: ${MINIO_ACCESS_KEY}
      secret-key: ${MINIO_SECRET_KEY}
      bucket: documents
      region: us-east-1
    s3:
      endpoint: ${S3_ENDPOINT}
      access-key: ${AWS_ACCESS_KEY_ID}
      secret-key: ${AWS_SECRET_ACCESS_KEY}
      bucket: ${S3_BUCKET}
      region: ${AWS_REGION}
    oss:
      endpoint: ${OSS_ENDPOINT}
      access-key: ${OSS_ACCESS_KEY_ID}
      secret-key: ${OSS_ACCESS_KEY_SECRET}
      bucket: ${OSS_BUCKET}
      
  # 元数据存储 - PostgreSQL Cluster
  metadata-store:
    type: postgresql
    host: ${POSTGRES_HOST}
    port: ${POSTGRES_PORT:5432}
    database: ${POSTGRES_DB:scene_engine}
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
    pool:
      min-size: 10
      max-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      
  # 缓存 - Redis Cluster
  cache:
    type: redis
    host: ${REDIS_HOST}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}
    database: 0
    cluster:
      enabled: true
      nodes: ${REDIS_CLUSTER_NODES}
      max-redirects: 3
      
  # 消息队列 - Kafka
  messaging:
    type: kafka
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    producer:
      acks: all
      retries: 3
      batch-size: 16384
    consumer:
      group-id: scene-engine
      auto-offset-reset: earliest
      enable-auto-commit: false
      
  # Embedding服务 - 多厂商支持
  embedding:
    type: ${EMBEDDING_TYPE:openai}
    providers:
      openai:
        api-key: ${OPENAI_API_KEY}
        model: text-embedding-3-small
        base-url: https://api.openai.com/v1
        timeout: 30000
        retry: 3
      baidu:
        api-key: ${BAIDU_API_KEY}
        secret-key: ${BAIDU_SECRET_KEY}
        model: bge-large-zh
        timeout: 30000
        retry: 3
      xunfei:
        app-id: ${XUNFEI_APP_ID}
        api-key: ${XUNFEI_API_KEY}
        api-secret: ${XUNFEI_API_SECRET}
        timeout: 30000
        retry: 3
    fallback:
      enabled: true
      providers: [openai, baidu]  # 降级顺序
      
  # LLM服务 - 多厂商支持
  llm:
    type: ${LLM_TYPE:openai}
    providers:
      openai:
        api-key: ${OPENAI_API_KEY}
        model: gpt-4o
        base-url: https://api.openai.com/v1
        timeout: 60000
        max-tokens: 4096
      baidu:
        api-key: ${BAIDU_API_KEY}
        secret-key: ${BAIDU_SECRET_KEY}
        model: ernie-bot-4
        timeout: 60000
      vllm:
        enabled: ${VLLM_ENABLED:false}
        endpoint: ${VLLM_ENDPOINT:http://vllm.scene-engine.svc.cluster.local:8000}
        model: ${VLLM_MODEL:llama-3.1-70b}
    fallback:
      enabled: true
      providers: [openai, baidu, vllm]

# 可观测性配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
  tracing:
    enabled: true
    sampling:
      probability: 0.1
```

### 4.4 资源需求

| 组件 | 实例数 | CPU/实例 | 内存/实例 | 存储 | 总计 |
|------|--------|----------|-----------|------|------|
| **SE-Core** | 3-20 | 2核 | 4GB | - | 6-40核 / 12-80GB |
| **Milvus Proxy** | 2 | 1核 | 2GB | - | 2核 / 4GB |
| **Milvus Query** | 3 | 4核 | 16GB | - | 12核 / 48GB |
| **Milvus Data** | 2 | 2核 | 4GB | 100GB | 4核 / 8GB / 200GB |
| **Milvus Index** | 2 | 4核 | 8GB | - | 8核 / 16GB |
| **Etcd** | 3 | 1核 | 2GB | 10GB | 3核 / 6GB / 30GB |
| **MinIO** | 4 | 2核 | 4GB | 500GB | 8核 / 16GB / 2TB |
| **PostgreSQL** | 3 | 2核 | 4GB | 100GB | 6核 / 12GB / 300GB |
| **Redis** | 6 | 1核 | 2GB | - | 6核 / 12GB |
| **Kafka** | 3 | 2核 | 4GB | 200GB | 6核 / 12GB / 600GB |
| **总计** | - | - | - | - | **61-95核 / 146-214GB / 3TB+** |

---

## 五、三种模式对比总结

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           三种部署模式对比总结                                            │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  维度          │ 微型服务(个人)    │ 小型服务(局域网)    │ 大型服务(分布式)            │
│  ─────────────┼──────────────────┼────────────────────┼────────────────────────────│
│  部署目标      │ 个人开发测试      │ 小团队/部门        │ 企业级生产环境              │
│  部署复杂度    │ ⭐ 极低           │ ⭐⭐ 低            │ ⭐⭐⭐⭐⭐ 高              │
│  运维成本      │ ⭐ 极低           │ ⭐⭐ 低            │ ⭐⭐⭐⭐⭐ 高              │
│  扩展能力      │ ⭐ 无             │ ⭐⭐ 垂直扩展      │ ⭐⭐⭐⭐⭐ 水平扩展        │
│  高可用性      │ ⭐ 无             │ ⭐⭐ 单点故障      │ ⭐⭐⭐⭐⭐ 高可用          │
│  数据持久化    │ ⭐ 可选SQLite     │ ⭐⭐⭐ 持久化存储  │ ⭐⭐⭐⭐⭐ 分布式存储      │
│  向量存储      │ InMemory         │ ChromaDB          │ Milvus Cluster             │
│  文档存储      │ InMemory         │ MinIO             │ MinIO/S3 Cluster           │
│  元数据存储    │ SQLite/H2        │ PostgreSQL        │ PostgreSQL Cluster         │
│  缓存          │ Caffeine         │ Redis单节点       │ Redis Cluster              │
│  消息队列      │ 内置队列          │ 可选RabbitMQ      │ Kafka Cluster              │
│  Embedding     │ Mock/Ollama      │ 外部API           │ 外部API/vLLM集群           │
│  LLM服务       │ Mock/Ollama      │ 外部API           │ 外部API/vLLM集群           │
│  资源需求      │ 2核2GB           │ 4核8GB            │ 60核+150GB+               │
│  网络要求      │ 单机             │ 局域网            │ 互联网/专线                │
│  启动时间      │ <10秒            │ <2分钟            │ <10分钟                   │
│  适用场景      │ 开发调试          │ 内部测试/POC      │ 生产环境                   │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、迁移路径

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                           平滑迁移路径                                                    │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│  个人开发 ─────────────────────────────────────────────────────────────────────────────► │
│       │                                                                                 │
│       │ 数据导出: SQLite → PostgreSQL                                                   │
│       │ 向量导出: InMemory → ChromaDB/Milvus                                           │
│       │ 配置切换: application-micro.yml → application-small.yml                         │
│       ▼                                                                                 │
│  局域网部署 ────────────────────────────────────────────────────────────────────────────► │
│       │                                                                                 │
│       │ 数据迁移: PostgreSQL → PostgreSQL Cluster                                      │
│       │ 向量迁移: ChromaDB → Milvus                                                    │
│       │ 配置切换: application-small.yml → application-large.yml                         │
│       │ 架构升级: Docker Compose → Kubernetes                                          │
│       ▼                                                                                 │
│  分布式部署                                                                             │
│                                                                                         │
│  关键原则:                                                                              │
│  1. 数据格式统一，支持导出导入                                                           │
│  2. 配置分层管理，环境变量覆盖                                                           │
│  3. 接口抽象统一，底层实现可替换                                                         │
│  4. 向下兼容，支持降级运行                                                               │
│                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、总结

### 核心设计原则

1. **微服务向下**: 存储类基础设施（向量库、文档库、数据库）支持从嵌入式到分布式的平滑扩展
2. **大服务向上**: AI类服务（Embedding、LLM）统一抽象接口，支持多厂商切换和降级
3. **Scene-Engine核心保持轻量**: 不内嵌重型依赖，通过配置切换不同部署模式
4. **配置即部署**: 通过yaml配置文件和profile切换，无需修改代码即可适配不同规模

### 实施建议

- **开发阶段**: 使用微型服务模式，快速迭代
- **测试阶段**: 使用小型服务模式，验证功能
- **生产阶段**: 使用大型服务模式，保障稳定性
- **平滑迁移**: 利用数据导出导入工具，无缝升级
