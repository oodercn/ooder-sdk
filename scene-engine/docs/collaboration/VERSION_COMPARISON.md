# Scene-Engine 版本架构设计文档

## 文档概述

**面向团队**: SuperAgent 团队  
**用途**: 构建 Tiny / Small / Large / Enterprise 四个版本  
**基于**: Scene-Engine 3.0.x 模块化重构  

---

## 版本定位

| 版本 | 定位 | 目标用户 | 核心场景 |
|------|------|----------|----------|
| **Tiny** | 最小可用 | 个人开发者 | 本地测试、嵌入式 |
| **Small** | 基础功能 | 小型团队 | 简单AI应用 |
| **Large** | 完整功能 | 中型企业 | 复杂业务场景 |
| **Enterprise** | 企业级 | 大型企业 | 高可用、安全合规 |

---

## 依赖架构总览

```
Scene-Engine
├── scene-engine-core/           (所有版本都有 - 核心引擎)
├── scene-engine-llm/           (Small+ - LLM集成)
├── scene-engine-vector/        (Large+ - 向量存储)
├── scene-engine-file/          (Small+ - 文件解析)
├── scene-engine-storage/       (Large+ - 云存储)
├── scene-engine-cache/         (Enterprise - Redis缓存)
└── scene-engine-security/       (Enterprise - 安全增强)
```

---

## 一、Tiny 版本 (tiny)

### 定位
- **体积目标**: < 5 MB
- **依赖数量**: < 50 个
- **启动时间**: < 2 秒
- **内存占用**: < 128 MB

### 适用场景
- 个人开发者本地调试
- 嵌入式设备
- CI/CD 测试环境
- 最小化 Docker 镜像

### 依赖配置

```xml
<!-- scene-engine-tiny/pom.xml -->
<dependencies>
    <!-- ========== 核心层 ========== -->
    
    <!-- Spring Boot 基础 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Web 支持 (最小化) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- ========== 核心SDK ========== -->
    
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-core</artifactId>
    </dependency>
    
    <!-- ========== 精简后的 common ========== -->
    
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-client</artifactId>
        <version>3.0.3</version>
        <exclusions>
            <!-- 移除重量级依赖 -->
            <exclusion>
                <groupId>org.freemarker</groupId>
                <artifactId>freemarker</artifactId>
            </exclusion>
            <exclusion>
                <groupId>org.mvel</groupId>
                <artifactId>mvel2</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    
    <!-- ========== 基础工具 ========== -->
    
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
    </dependency>
</dependencies>
```

### 功能清单

| 功能模块 | 支持情况 | 说明 |
|----------|----------|------|
| **核心引擎** | ✅ 完整 | 场景生命周期管理 |
| **Agent 通信** | ✅ 完整 | A2A 协议基础实现 |
| **能力路由** | ✅ 完整 | 基础路由能力 |
| **配置加载** | ✅ 完整 | YAML 配置支持 |
| **日志记录** | ✅ 完整 | SLF4J 集成 |
| **健康检查** | ✅ 完整 | Actuator 端点 |
| **LLM 调用** | ❌ 不支持 | 需要 Small+ 版本 |
| **向量存储** | ❌ 不支持 | 需要 Large+ 版本 |
| **文件解析** | ❌ 不支持 | 需要 Small+ 版本 |
| **云存储** | ❌ 不支持 | 需要 Large+ 版本 |
| **Redis 缓存** | ❌ 不支持 | 需要 Enterprise 版本 |
| **安全增强** | ❌ 不支持 | 需要 Enterprise 版本 |

### 典型使用

```yaml
# scene-engine-tiny 配置示例
scene:
  mode: tiny
  engine:
    max-agents: 10
    timeout: 30000
  llm:
    enabled: false  # 不启用LLM
  storage:
    type: memory
```

---

## 二、Small 版本 (small)

### 定位
- **体积目标**: 15-25 MB
- **依赖数量**: 100-150 个
- **启动时间**: < 5 秒
- **内存占用**: < 256 MB

### 适用场景
- 小型团队 AI 应用
- 简单对话机器人
- 基础技能执行
- 小型业务系统

### 依赖配置

```xml
<!-- scene-engine-small/pom.xml -->
<dependencies>
    <!-- ========== 继承 Tiny 版本 ========== -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-core</artifactId>
    </dependency>
    
    <!-- ========== LLM 集成 ========== -->
    
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    
    <!-- ========== 文件解析 ========== -->
    
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <optional>true</optional>
    </dependency>
    
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <optional>true</optional>
    </dependency>
    
    <dependency>
        <groupId>org.commonmark</groupId>
        <artifactId>commonmark</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
    </dependency>
    
    <!-- ========== 基础数据库 ========== -->
    
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
    
    <!-- ========== WebSocket 支持 ========== -->
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
</dependencies>
```

### 功能清单

| 功能模块 | 支持情况 | 说明 |
|----------|----------|------|
| **核心引擎** | ✅ 完整 | 继承 Tiny |
| **Agent 通信** | ✅ 完整 | A2A 协议增强 |
| **能力路由** | ✅ 完整 | 支持能力注册 |
| **LLM 调用** | ✅ 完整 | OpenAI/Claude 等 |
| **文件解析** | ✅ 基础 | PDF/Word/Markdown |
| **消息队列** | ✅ 基础 | WebSocket |
| **数据库** | ✅ MySQL | 关系型数据 |
| **向量存储** | ❌ 不支持 | 需要 Large+ |
| **云存储** | ❌ 不支持 | 需要 Large+ |
| **Redis 缓存** | ❌ 不支持 | 需要 Enterprise |
| **多租户** | ✅ 基础 | 基础隔离 |

### 典型使用

```yaml
# scene-engine-small 配置示例
scene:
  mode: small
  engine:
    max-agents: 100
    timeout: 60000
  llm:
    enabled: true
    provider: openai
    model: gpt-4o-mini
  database:
    type: mysql
    host: localhost
  storage:
    type: local
    path: /data/scene-engine
  file:
    max-size: 10MB
    allowed-types:
      - pdf
      - docx
      - md
      - txt
```

---

## 三、Large 版本 (large)

### 定位
- **体积目标**: 50-80 MB
- **依赖数量**: 200-300 个
- **启动时间**: < 10 秒
- **内存占用**: < 512 MB

### 适用场景
- 中型企业业务系统
- 复杂多 Agent 协作
- RAG 知识检索
- 大规模数据处理

### 依赖配置

```xml
<!-- scene-engine-large/pom.xml -->
<dependencies>
    <!-- ========== 继承 Small 版本 ========== -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-core</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-llm</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-file</artifactId>
    </dependency>
    
    <!-- ========== 向量存储 ========== -->
    
    <dependency>
        <groupId>io.milvus</groupId>
        <artifactId>milvus-sdk-java</artifactId>
        <version>2.4.1</version>
        <exclusions>
            <!-- 排除不必要的传递依赖 -->
            <exclusion>
                <groupId>org.apache.hadoop</groupId>
                <artifactId>*</artifactId>
            </exclusion>
            <exclusion>
                <groupId>org.testcontainers</groupId>
                <artifactId>*</artifactId>
            </exclusion>
            <exclusion>
                <groupId>com.amazonaws</groupId>
                <artifactId>*</artifactId>
            </exclusion>
            <exclusion>
                <groupId>com.azure</groupId>
                <artifactId>*</artifactId>
            </exclusion>
            <exclusion>
                <groupId>io.minio</groupId>
                <artifactId>*</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    
    <!-- ========== 云存储 ========== -->
    
    <dependency>
        <groupId>com.upyun</groupId>
        <artifactId>java-sdk</artifactId>
    </dependency>
    
    <!-- ========== 全文搜索 (精简版) ========== -->
    
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-index-web</artifactId>
    </dependency>
    
    <!-- ========== 增强工具 ========== -->
    
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-collections4</artifactId>
    </dependency>
    
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
    </dependency>
</dependencies>
```

### 功能清单

| 功能模块 | 支持情况 | 说明 |
|----------|----------|------|
| **核心引擎** | ✅ 完整 | 继承 Small |
| **Agent 通信** | ✅ 完整 | A2A + MCP |
| **能力路由** | ✅ 完整 | 智能路由 |
| **LLM 调用** | ✅ 完整 | 多模型支持 |
| **文件解析** | ✅ 完整 | 全部格式 |
| **向量存储** | ✅ 完整 | Milvus |
| **全文搜索** | ✅ 完整 | Lucene |
| **云存储** | ✅ 完整 | Upyun 等 |
| **数据库** | ✅ 完整 | MySQL + SQLite |
| **消息队列** | ✅ 增强 | WebSocket + SSE |
| **多租户** | ✅ 完整 | 完整隔离 |
| **审计日志** | ✅ 完整 | 操作审计 |
| **Redis 缓存** | ❌ 不支持 | 需要 Enterprise |
| **安全增强** | ❌ 不支持 | 需要 Enterprise |

### 典型使用

```yaml
# scene-engine-large 配置示例
scene:
  mode: large
  engine:
    max-agents: 1000
    timeout: 120000
    cluster:
      enabled: true
  llm:
    enabled: true
    load-balancer: random
    providers:
      - name: openai
        model: gpt-4o
      - name: anthropic
        model: claude-3-5-sonnet
  vector:
    enabled: true
    provider: milvus
    connection:
      host: localhost
      port: 19530
  search:
    enabled: true
    type: lucene
    index-path: /data/search-index
  storage:
    type: cloud
    provider: upyun
    bucket: scene-engine
  database:
    type: mysql
    host: localhost
    pool:
      maximum-pool-size: 20
```

---

## 四、Enterprise 版本 (enterprise)

### 定位
- **体积目标**: 100-150 MB
- **依赖数量**: 300-500 个
- **启动时间**: < 15 秒
- **内存占用**: < 1 GB

### 适用场景
- 大型企业核心系统
- 高可用部署
- 安全合规要求
- 私有化部署

### 依赖配置

```xml
<!-- scene-engine-enterprise/pom.xml -->
<dependencies>
    <!-- ========== 继承 Large 版本 ========== -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-core</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-llm</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-vector</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-file</artifactId>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine-storage</artifactId>
    </dependency>
    
    <!-- ========== Redis 缓存 ========== -->
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
    </dependency>
    
    <!-- ========== 安全增强 ========== -->
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
    
    <!-- ========== 消息队列 ========== -->
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    
    <!-- ========== 监控运维 ========== -->
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    
    <!-- ========== 分布式事务 ========== -->
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
</dependencies>
```

### 功能清单

| 功能模块 | 支持情况 | 说明 |
|----------|----------|------|
| **核心引擎** | ✅ 完整 | 继承 Large |
| **Agent 通信** | ✅ 完整 | 全协议支持 |
| **能力路由** | ✅ 完整 | 高级路由策略 |
| **LLM 调用** | ✅ 完整 | 企业级 LLM |
| **文件解析** | ✅ 完整 | 全格式 + OCR |
| **向量存储** | ✅ 完整 | Milvus 集群 |
| **全文搜索** | ✅ 完整 | 分布式搜索 |
| **云存储** | ✅ 完整 | 多云支持 |
| **数据库** | ✅ 完整 | MySQL + 读写分离 |
| **Redis 缓存** | ✅ 完整 | 集群模式 |
| **消息队列** | ✅ 完整 | RabbitMQ |
| **多租户** | ✅ 完整 | 完全隔离 |
| **审计日志** | ✅ 完整 | 完整审计链 |
| **安全增强** | ✅ 完整 | OAuth2 + LDAP |
| **监控运维** | ✅ 完整 | Prometheus |
| **分布式事务** | ✅ 完整 | Seata |

### 典型使用

```yaml
# scene-engine-enterprise 配置示例
scene:
  mode: enterprise
  engine:
    max-agents: 10000
    timeout: 300000
    cluster:
      enabled: true
      nodes:
        - host: node1.cluster.local
        - host: node2.cluster.local
        - host: node3.cluster.local
  llm:
    enabled: true
    load-balancer: weighted
    providers:
      - name: openai
        model: gpt-4o
        weight: 3
      - name: anthropic
        model: claude-3-5-sonnet
        weight: 2
      - name: azure-openai
        model: gpt-4o
        weight: 1
  vector:
    enabled: true
    provider: milvus
    cluster:
      mode: cluster
      host: milvus.cluster.local
      port: 19530
  search:
    enabled: true
    type: distributed
  storage:
    type: multi-cloud
    providers:
      - name: oss
      - name: s3
      - name: upyun
  cache:
    enabled: true
    type: redis
    cluster:
      mode: cluster
      nodes:
        - host: redis1.cluster.local
        - host: redis2.cluster.local
        - host: redis3.cluster.local
  database:
    type: mysql
    datasource:
      master:
        host: db-master.cluster.local
      slave:
        - host: db-slave1.cluster.local
        - host: db-slave2.cluster.local
  security:
    enabled: true
    oauth2:
      enabled: true
      issuer-uri: https://auth.company.com
    ldap:
      enabled: true
      url: ldap://ldap.company.com:389
  audit:
    enabled: true
    storage: elasticsearch
  monitoring:
    enabled: true
    prometheus:
      enabled: true
```

---

## 版本对比矩阵

| 功能 | Tiny | Small | Large | Enterprise |
|------|:----:|:-----:|:-----:|:----------:|
| **核心引擎** | ✅ | ✅ | ✅ | ✅ |
| **Agent 通信** | ✅ | ✅ | ✅ | ✅ |
| **LLM 调用** | ❌ | ✅ | ✅ | ✅ |
| **文件解析** | ❌ | ✅ | ✅ | ✅ |
| **向量存储** | ❌ | ❌ | ✅ | ✅ |
| **全文搜索** | ❌ | ❌ | ✅ | ✅ |
| **云存储** | ❌ | ❌ | ✅ | ✅ |
| **Redis 缓存** | ❌ | ❌ | ❌ | ✅ |
| **安全增强** | ❌ | ❌ | ❌ | ✅ |
| **多租户** | ❌ | 基础 | ✅ | ✅ |
| **监控运维** | ❌ | ❌ | ❌ | ✅ |
| **分布式事务** | ❌ | ❌ | ❌ | ✅ |
| **消息队列** | ❌ | 基础 | ✅ | ✅ |
| **审计日志** | ❌ | ❌ | ✅ | ✅ |
| **OAuth2/LDAP** | ❌ | ❌ | ❌ | ✅ |

---

## 依赖体积对比

| 版本 | JAR 数量 | 总体积 | 压缩后 |
|------|----------|--------|--------|
| **Tiny** | ~30 | < 5 MB | < 2 MB |
| **Small** | ~80 | 15-25 MB | 5-8 MB |
| **Large** | ~150 | 50-80 MB | 15-25 MB |
| **Enterprise** | ~250 | 100-150 MB | 30-50 MB |

---

## 构建配置

### Maven Profile 配置

```xml
<profiles>
    <!-- Tiny 版本 -->
    <profile>
        <id>tiny</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <dependencies>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-core</artifactId>
            </dependency>
        </dependencies>
    </profile>
    
    <!-- Small 版本 -->
    <profile>
        <id>small</id>
        <dependencies>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-core</artifactId>
            </dependency>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-llm</artifactId>
            </dependency>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-file</artifactId>
            </dependency>
        </dependencies>
    </profile>
    
    <!-- Large 版本 -->
    <profile>
        <id>large</id>
        <dependencies>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-core</artifactId>
            </dependency>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-llm</artifactId>
            </dependency>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-file</artifactId>
            </dependency>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-vector</artifactId>
            </dependency>
            <dependency>
                <groupId>net.ooder</groupId>
                <artifactId>scene-engine-storage</artifactId>
            </dependency>
        </dependencies>
    </profile>
    
    <!-- Enterprise 版本 -->
    <profile>
        <id>enterprise</id>
        <dependencies>
            <!-- 全部模块 -->
        </dependencies>
    </profile>
</profiles>
```

### 构建命令

```bash
# 构建 Tiny 版本
mvn clean package -Ptiny -DskipTests

# 构建 Small 版本
mvn clean package -Psmall -DskipTests

# 构建 Large 版本
mvn clean package -Plarge -DskipTests

# 构建 Enterprise 版本
mvn clean package -Penterprise -DskipTests
```

---

## 部署方式对比

| 方面 | Tiny | Small | Large | Enterprise |
|------|------|-------|-------|------------|
| **部署模式** | 单进程 | 单进程 | 多实例 | K8s 集群 |
| **数据库** | 无 | MySQL | MySQL | MySQL 集群 |
| **缓存** | 无 | 无 | 无 | Redis 集群 |
| **向量库** | 无 | 无 | Milvus | Milvus 集群 |
| **存储** | 本地 | 本地 | 云存储 | 多云存储 |
| **负载均衡** | 无 | 无 | Nginx | K8s Ingress |
| **容器镜像** | < 50 MB | < 200 MB | < 500 MB | < 1 GB |

---

## 技术选型建议

### Tiny 版本
- **Java 版本**: 21
- **Spring Boot**: 3.2.x
- **Web 容器**: Tomcat (嵌入式)
- **日志框架**: SLF4J + Logback

### Small 版本
- **Java 版本**: 21
- **Spring Boot**: 3.2.x
- **Web 容器**: Tomcat / Netty
- **数据库**: MySQL 8.0

### Large 版本
- **Java 版本**: 21
- **Spring Boot**: 3.2.x
- **向量库**: Milvus 2.4.x
- **搜索引擎**: Elasticsearch 8.x

### Enterprise 版本
- **Java 版本**: 21
- **Spring Boot**: 3.2.x
- **容器**: Kubernetes
- **服务网格**: Istio
- **监控系统**: Prometheus + Grafana

---

## 下一步行动

### SuperAgent 团队任务

1. [ ] 设计 scene-engine-core 模块结构
2. [ ] 设计 scene-engine-llm 模块结构
3. [ ] 设计 scene-engine-vector 模块结构
4. [ ] 设计 scene-engine-file 模块结构
5. [ ] 设计 scene-engine-storage 模块结构
6. [ ] 设计 scene-engine-cache 模块结构
7. [ ] 设计 scene-engine-security 模块结构
8. [ ] 创建各版本 Maven Profile
9. [ ] 编写构建脚本
10. [ ] 编写部署文档

### 依赖精简任务

- [x] Scene-Engine 依赖优化分析
- [ ] ooder-common 依赖精简（Freemarker/MVEL 移除）
- [ ] Milvus SDK 精简（排除 Hadoop/云存储）
- [ ] SQLite 改为 optional

---

## 参考文档

- [依赖优化分析报告](DEPENDENCY_OPTIMIZATION_ANALYSIS.md)
- [依赖优化实施报告](DEPENDENCY_OPTIMIZATION_RESULT.md)
- [ooder-common 变更说明](OODER_COMMON_CHANGES.md)
- [Scene-Engine pom.xml](e:\github\ooder-sdk\scene-engine\pom.xml)

---

**文档版本**: 1.0  
**创建日期**: 2026-04-10  
**面向团队**: SuperAgent  
**状态**: 待 SuperAgent 团队评审
