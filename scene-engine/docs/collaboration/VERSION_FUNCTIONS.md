# Scene-Engine 版本功能对照表

> 快速查阅版 - 详细说明请参考 [VERSION_COMPARISON.md](VERSION_COMPARISON.md)

---

## 版本定位

| 版本 | 体积目标 | 内存占用 | 适用场景 |
|------|----------|----------|----------|
| **Tiny** | < 5 MB | < 128 MB | 本地测试、嵌入式 |
| **Small** | 15-25 MB | < 256 MB | 小型团队、简单AI应用 |
| **Large** | 50-80 MB | < 512 MB | 中型企业、RAG知识检索 |
| **Enterprise** | 100-150 MB | < 1 GB | 大型企业、高可用部署 |

---

## 功能矩阵

### 核心能力

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| 场景生命周期管理 | ✅ | ✅ | ✅ | ✅ |
| Agent通信(A2A协议) | ✅ | ✅ | ✅ | ✅ |
| 能力路由与注册 | ✅ | ✅ | ✅ | ✅ |
| 配置加载(YAML) | ✅ | ✅ | ✅ | ✅ |
| 日志记录 | ✅ | ✅ | ✅ | ✅ |
| 健康检查 | ✅ | ✅ | ✅ | ✅ |

### LLM 集成

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| LLM调用基础 | ❌ | ✅ | ✅ | ✅ |
| OpenAI集成 | ❌ | ✅ | ✅ | ✅ |
| Claude集成 | ❌ | ✅ | ✅ | ✅ |
| Azure OpenAI | ❌ | ❌ | ✅ | ✅ |
| 模型负载均衡 | ❌ | ❌ | ✅ | ✅ |
| 多模型路由 | ❌ | ❌ | ✅ | ✅ |

### 数据处理

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| PDF解析 | ❌ | ✅ | ✅ | ✅ |
| Word解析 | ❌ | ✅ | ✅ | ✅ |
| Excel解析 | ❌ | ✅ | ✅ | ✅ |
| Markdown解析 | ❌ | ✅ | ✅ | ✅ |
| HTML解析 | ❌ | ✅ | ✅ | ✅ |
| 文本提取 | ❌ | ✅ | ✅ | ✅ |

### 向量与搜索

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| 向量存储 | ❌ | ❌ | ✅ | ✅ |
| Milvus集成 | ❌ | ❌ | ✅ | ✅ |
| 全文搜索(Lucene) | ❌ | ❌ | ✅ | ✅ |
| 分布式搜索 | ❌ | ❌ | ❌ | ✅ |

### 存储服务

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| 本地存储 | ✅ | ✅ | ✅ | ✅ |
| MySQL数据库 | ❌ | ✅ | ✅ | ✅ |
| SQLite(嵌入式) | ❌ | ❌ | ✅ | ✅ |
| 云存储(Upyun) | ❌ | ❌ | ✅ | ✅ |
| 多云存储 | ❌ | ❌ | ❌ | ✅ |
| Redis缓存 | ❌ | ❌ | ❌ | ✅ |

### 安全与权限

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| 基础认证 | ❌ | ✅ | ✅ | ✅ |
| OAuth2认证 | ❌ | ❌ | ❌ | ✅ |
| LDAP集成 | ❌ | ❌ | ❌ | ✅ |
| 权限管理 | ❌ | ✅ | ✅ | ✅ |
| 多租户隔离 | ❌ | 基础 | ✅ | ✅ |
| 审计日志 | ❌ | ❌ | ✅ | ✅ |

### 运维监控

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| 基础监控 | ✅ | ✅ | ✅ | ✅ |
| Prometheus指标 | ❌ | ❌ | ❌ | ✅ |
| Grafana仪表盘 | ❌ | ❌ | ❌ | ✅ |
| 健康检查端点 | ✅ | ✅ | ✅ | ✅ |
| 分布式追踪 | ❌ | ❌ | ❌ | ✅ |

### 消息与通信

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| HTTP/REST | ✅ | ✅ | ✅ | ✅ |
| WebSocket | ❌ | ✅ | ✅ | ✅ |
| SSE事件流 | ❌ | ❌ | ✅ | ✅ |
| RabbitMQ | ❌ | ❌ | ❌ | ✅ |

---

## 依赖组件

### Tiny 版本依赖

```
核心框架
├── spring-boot-starter
├── spring-boot-starter-web
├── spring-boot-starter-logging
│
SDK层
├── agent-sdk-core
├── ooder-common-client (精简版)
│
工具库
├── jackson-databind
└── slf4j-api
```

### Small 版本依赖

```
Tiny版本 +
├── llm-sdk
├── spring-boot-starter-webflux
├── spring-boot-starter-websocket
│
文件解析
├── pdfbox (optional)
├── poi-ooxml (optional)
├── commonmark
└── jsoup
│
数据库
├── mysql-connector-j
└── HikariCP
```

### Large 版本依赖

```
Small版本 +
├── milvus-sdk-java (精简版)
├── ooder-index-web
│
云存储
└── upyun-sdk
│
增强工具
├── commons-lang3
├── commons-collections4
└── commons-io
```

### Enterprise 版本依赖

```
Large版本 +
├── spring-boot-starter-data-redis
├── jedis
│
安全
├── spring-boot-starter-security
├── spring-boot-starter-oauth2-resource-server
│
消息队列
├── spring-boot-starter-amqp
│
监控
├── spring-boot-starter-actuator
└── micrometer-registry-prometheus
```

---

## 部署模式

| 模式 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| 单进程 | ✅ | ✅ | ❌ | ❌ |
| 多实例 | ❌ | ❌ | ✅ | ❌ |
| Docker Compose | ❌ | ✅ | ✅ | ❌ |
| Kubernetes | ❌ | ❌ | ❌ | ✅ |
| 服务网格 | ❌ | ❌ | ❌ | ✅ |

---

## 快速选择指南

```
需要什么功能？
│
├─ 仅本地测试/开发？
│  └─ → Tiny 版本
│
├─ 需要LLM调用？
│  │
│  ├─ 仅简单对话？
│  │  └─ → Small 版本
│  │
│  └─ 需要RAG/知识库？
│     └─ → Large 版本
│
└─ 企业级部署？
   │
   ├─ 需要高可用？
   │  └─ → Enterprise 版本
   │
   └─ 需要安全合规？
      └─ → Enterprise 版本
```

---

## 版本升级路径

```
Tiny → Small → Large → Enterprise
  │        │        │
  └────────┴────────┘
         可选升级路径
```

---

**文档版本**: 1.0  
**创建日期**: 2026-04-10  
**详细文档**: [VERSION_COMPARISON.md](VERSION_COMPARISON.md)
