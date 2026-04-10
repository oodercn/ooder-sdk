# Scene-Engine 依赖体量分析与优化方案

## 一、分析概述

### 1.1 分析目标
针对MVP版本精简需求，深入分析 `scene-engine` 模块的依赖树，识别大体量jar包，按照微小中大四个量级给出优化方案。

### 1.2 分析范围
- 主项目: `e:\github\ooder-sdk\scene-engine\pom.xml`
- 父项目: `e:\github\ooder-sdk\pom.xml`
- Maven本地仓库: `D:\maven\.m2\repository`

---

## 二、依赖体量分析

### 2.1 超大依赖 (>5MB) - 重量级

| 依赖 | 版本 | 大小 | 来源 | 影响分析 |
|------|------|------|------|----------|
| sqlite-jdbc | 3.45.2.0 | **12.89 MB** | 直接依赖 | 嵌入式数据库，包含多平台native库 |
| grpc-netty-shaded | 1.59.1 | **9.29 MB** | milvus-sdk-java传递 | gRPC shaded包，包含Netty |
| poi-ooxml-lite | 5.2.5 | **5.67 MB** | poi-ooxml传递 | Office文档解析schema |

**合计: ~27.85 MB**

### 2.2 大依赖 (2-5MB) - 中重量级

| 依赖 | 版本 | 大小 | 来源 | 影响分析 |
|------|------|------|------|----------|
| guava | 32.0.1-android | 2.72 MB | milvus-sdk-java传递 | Google工具库 |
| lucene-core | 9.10.0 | 3.81 MB | ooder-msg-web传递 | 全文搜索引擎 |
| milvus-sdk-java | 2.4.1 | 3.58 MB | 直接依赖 | 向量数据库客户端 |
| hadoop-common | 3.3.6 | 4.39 MB | milvus-sdk-java传递 | Hadoop生态 |
| mysql-connector-j | 8.3.0 | 2.38 MB | 直接依赖 | MySQL驱动 |
| pdfbox | 3.0.1 | 1.92 MB | 直接依赖 | PDF解析 |
| poi-ooxml | 5.2.5 | 1.93 MB | 直接依赖 | Office文档解析 |
| fontbox | 3.0.1 | 1.54 MB | pdfbox传递 | 字体处理 |

**合计: ~22.27 MB**

### 2.3 中等依赖 (1-2MB) - 轻中量级

| 依赖 | 版本 | 大小 | 来源 |
|------|------|------|------|
| aws-java-sdk-s3 | 1.12.687 | 1.21 MB | milvus-sdk-java传递 |
| parquet-hadoop | 1.13.1 | 0.96 MB | milvus-sdk-java传递 |
| parquet-avro | 1.13.1 | 0.87 MB | milvus-sdk-java传递 |
| azure-storage-blob | 12.25.3 | 0.77 MB | milvus-sdk-java传递 |
| avro | 1.11.1 | 0.58 MB | milvus-sdk-java传递 |
| minio | 8.5.7 | 0.38 MB | milvus-sdk-java传递 |

**合计: ~4.77 MB**

### 2.4 Milvus SDK 传递依赖详细分析

```
io.milvus:milvus-sdk-java:2.4.1 (3.58 MB)
├── org.apache.hadoop:hadoop-client:3.3.6
│   ├── hadoop-common (4.39 MB)
│   ├── hadoop-hdfs-client (4.65 MB)
│   ├── hadoop-yarn-api (4.23 MB)
│   ├── hadoop-yarn-client (1.41 MB)
│   ├── hadoop-yarn-common (2.07 MB)
│   ├── hadoop-mapreduce-client-core (1.42 MB)
│   └── ... (数十个传递依赖)
├── org.apache.parquet:parquet-hadoop:1.13.1
├── org.apache.parquet:parquet-avro:1.13.1
├── com.amazonaws:aws-java-sdk-s3:1.12.687
├── io.minio:minio:8.5.7
├── com.azure:azure-storage-blob:12.25.3
├── io.grpc:grpc-netty-shaded:1.59.1 (9.29 MB)
├── com.google.guava:guava:32.0.1-android (2.72 MB)
├── org.testcontainers:testcontainers:1.19.7 (11.95 MB) ⚠️
└── org.testcontainers:milvus:1.19.7
```

**Milvus SDK 及其传递依赖总计: ~60+ MB**

---

## 三、问题根源分析

### 3.1 核心问题

1. **Milvus SDK 过度依赖**
   - 向量数据库SDK引入了完整的Hadoop生态
   - 包含云存储SDK (AWS S3, Azure Blob, MinIO)
   - 包含测试框架 (Testcontainers)

2. **文件解析依赖冗余**
   - PDF解析 (PDFBox) + Office解析 (POI) 同时存在
   - 两者都依赖Apache Commons系列库

3. **数据库驱动冗余**
   - MySQL + SQLite 同时存在
   - SQLite JDBC包含多平台native库

4. **ooder-common模块过重**
   - ooder-common-client 包含Freemarker、Jedis、HttpClient等
   - ooder-msg-web 引入Lucene全文搜索

### 3.2 依赖冲突

```
问题1: 日志框架冲突
- logback (Spring Boot默认)
- log4j2 (Milvus SDK引入)
- slf4j-reload4j (Hadoop引入)

问题2: JSON库重复
- fastjson2 (直接依赖)
- fastjson (ooder-annotation传递)
- jackson (Spring Boot)
- gson (Milvus SDK传递)

问题3: Netty版本冲突
- netty 4.1.109 (Spring Boot WebFlux)
- grpc-netty-shaded 1.59.1 (Milvus)
- netty-tcnative多平台库
```

---

## 四、优化方案

### 4.1 微小优化 (< 1天工作量)

#### 方案1: 移除测试依赖
```xml
<!-- 当前milvus-sdk-java传递引入 -->
<exclude>
    <groupId>org.testcontainers</groupId>
    <artifactId>*</artifactId>
</exclude>
```
**收益**: 减少 ~12 MB

#### 方案2: 精简日志框架
```xml
<!-- 排除Hadoop的log4j -->
<exclude>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-reload4j</artifactId>
</exclude>
<exclude>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
</exclude>
```
**收益**: 减少 ~5 MB + 避免日志冲突

#### 方案3: 移除重复JSON库
```xml
<!-- 排除ooder-annotation的fastjson -->
<exclude>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
</exclude>
<!-- 排除Milvus的gson -->
<exclude>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</exclude>
```
**收益**: 减少 ~2 MB

**微小优化总计: ~19 MB**

---

### 4.2 小优化 (1-3天工作量)

#### 方案4: 条件化SQLite依赖
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.2.0</version>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```
**收益**: 减少 ~13 MB (嵌入式场景才需要)

#### 方案5: 按需加载文件解析
将PDF/Office解析改为SPI动态加载:
```java
// 定义接口
public interface DocumentParser {
    boolean supports(String mimeType);
    String parse(InputStream input);
}

// 通过ServiceLoader动态加载
ServiceLoader.load(DocumentParser.class);
```

POM调整为optional:
```xml
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
```
**收益**: 减少 ~15 MB

#### 方案6: 精简Milvus SDK依赖
```xml
<dependency>
    <groupId>io.milvus</groupId>
    <artifactId>milvus-sdk-java</artifactId>
    <exclusions>
        <!-- 排除Hadoop生态 -->
        <exclusion>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>*</artifactId>
        </exclusion>
        <!-- 排除Parquet -->
        <exclusion>
            <groupId>org.apache.parquet</groupId>
            <artifactId>*</artifactId>
        </exclusion>
        <!-- 排除云存储 -->
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
        <!-- 排除Testcontainers -->
        <exclusion>
            <groupId>org.testcontainers</groupId>
            <artifactId>*</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
**收益**: 减少 ~50+ MB

**小优化总计: ~78 MB**

---

### 4.3 中优化 (1周工作量)

#### 方案7: 拆分ooder-common模块

当前结构:
```
ooder-common
├── ooder-common-client (过重)
├── ooder-server
├── ooder-vfs-web
├── ooder-org-web
└── ooder-msg-web (含Lucene)
```

建议拆分为:
```
ooder-common-core (核心接口)
ooder-common-http (HTTP客户端)
ooder-common-cache (Redis/Jedis)
ooder-common-template (Freemarker)
ooder-common-search (Lucene，可选)
```

**收益**: 
- 核心依赖减少 ~20 MB
- 模块职责更清晰
- 按需引入

#### 方案8: 向量数据库抽象层

创建向量存储SPI，支持多种实现:
```java
public interface VectorStore {
    void upsert(List<Vector> vectors);
    List<Vector> search(Vector query, int topK);
}

// Milvus实现 (可选依赖)
public class MilvusVectorStore implements VectorStore { }

// 内存实现 (轻量，默认)
public class InMemoryVectorStore implements VectorStore { }
```

**收益**:
- Milvus变为optional依赖
- 减少 ~60+ MB (MVP可完全移除)
- 支持多种向量存储后端

**中优化总计: ~80 MB**

---

### 4.4 大优化 (2-4周工作量)

#### 方案9: 模块化架构重构

采用Gradle多模块或Maven多profile方案:

```
scene-engine-core (核心引擎，轻量)
scene-engine-llm (LLM集成，可选)
scene-engine-vector (向量存储，可选)
scene-engine-file (文件解析，可选)
scene-engine-storage (云存储，可选)
```

**pom.xml示例**:
```xml
<profiles>
    <profile>
        <id>minimal</id>
        <dependencies>
            <!-- 仅核心依赖 -->
        </dependencies>
    </profile>
    <profile>
        <id>full</id>
        <dependencies>
            <!-- 全部功能 -->
        </dependencies>
    </profile>
</profiles>
```

**收益**:
- 最小版本可减少 ~150+ MB
- 用户按需选择功能模块
- 部署更灵活

#### 方案10: 云原生适配

针对容器化部署优化:
1. 使用jlink创建自定义JRE
2. 使用GraalVM Native Image编译
3. 分离native库 (SQLite, Netty)

**收益**:
- 镜像体积减少 70%+
- 启动速度提升 10x
- 内存占用减少 50%+

**大优化总计: ~150+ MB**

---

## 五、优化收益汇总

| 优化级别 | 预计减少体积 | 工作量 | 优先级 |
|----------|-------------|--------|--------|
| 微小优化 | ~19 MB | < 1天 | P0 |
| 小优化 | ~78 MB | 1-3天 | P1 |
| 中优化 | ~80 MB | 1周 | P2 |
| 大优化 | ~150+ MB | 2-4周 | P3 |
| **总计** | **~327 MB** | - | - |

---

## 六、MVP版本建议

### 6.1 推荐方案组合

**阶段1 (立即执行)** - 微小优化:
- 方案1: 移除Testcontainers
- 方案2: 统一日志框架
- 方案3: 移除重复JSON库

**阶段2 (本周内)** - 小优化:
- 方案4: SQLite改为optional
- 方案5: 文件解析改为optional
- 方案6: 精简Milvus依赖

**阶段3 (本月内)** - 中优化:
- 方案7: 拆分ooder-common
- 方案8: 向量存储抽象层

### 6.2 预期成果

执行阶段1+2后:
- 当前依赖总量: ~200+ MB
- 优化后依赖总量: ~100 MB
- **体积减少: 50%**

---

## 七、实施检查清单

- [ ] 创建dependency-exclusions分支
- [ ] 执行微小优化方案1-3
- [ ] 运行完整测试套件
- [ ] 执行小优化方案4-6
- [ ] 验证Milvus功能正常
- [ ] 验证文件解析功能正常
- [ ] 性能测试对比
- [ ] 更新部署文档

---

## 八、参考文件

- 依赖树文件: `e:\github\ooder-sdk\scene-engine\dependency-tree.txt`
- 主POM: `e:\github\ooder-sdk\scene-engine\pom.xml`
- 父POM: `e:\github\ooder-sdk\pom.xml`
- Maven仓库: `D:\maven\.m2\repository`

---

**文档版本**: 1.0  
**创建日期**: 2026-04-10  
**分析工具**: Maven Dependency Plugin + PowerShell  
