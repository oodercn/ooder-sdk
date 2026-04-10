# Scene-Engine 依赖优化实施报告

## 执行摘要

**执行时间**: 2026-04-10  
**执行人**: AI Agent  
**优化范围**: 阶段1（微小优化）+ 阶段2（小优化）

---

## 优化成果

### 依赖数量对比

| 指标 | 优化前 | 优化后 | 减少量 | 减少比例 |
|------|--------|--------|--------|----------|
| 依赖树行数 | 313 | 188 | 125 | **40.0%** |

### 已排除的关键依赖

| 依赖类别 | 优化前 | 优化后 | 状态 |
|----------|--------|--------|------|
| Testcontainers | 有 | **0处** | ✅ 已排除 |
| Hadoop生态 | 有 | **0处** | ✅ 已排除 |
| Parquet | 有 | **0处** | ✅ 已排除 |
| AWS SDK | 有 | **0处** | ✅ 已排除 |
| Azure Storage | 有 | **0处** | ✅ 已排除 |
| MinIO | 有 | **0处** | ✅ 已排除 |
| Log4j-core | 有 | **0处** | ✅ 已排除 |
| SLF4J-reload4j | 有 | **0处** | ✅ 已排除 |

### 已标记为 Optional 的依赖

| 依赖 | 大小 | 状态 |
|------|------|------|
| sqlite-jdbc | 12.89 MB | ✅ optional |
| pdfbox | 1.92 MB | ✅ optional |
| poi-ooxml | 1.93 MB | ✅ optional |

**Optional总计**: ~16.74 MB

---

## 实施的优化项

### 1. Milvus SDK 依赖精简

**修改文件**: `pom.xml`

**排除的依赖**:
- Testcontainers (测试框架)
- Log4j相关 (日志冲突)
- Gson (重复JSON库)
- Hadoop生态 (含HDFS/YARN/MapReduce)
- Parquet (列式存储)
- AWS SDK (S3/KMS)
- Azure Storage (Blob)
- MinIO (对象存储)

**代码变更**:
```xml
<dependency>
    <groupId>io.milvus</groupId>
    <artifactId>milvus-sdk-java</artifactId>
    <version>2.4.1</version>
    <exclusions>
        <!-- 排除测试框架 -->
        <exclusion>
            <groupId>org.testcontainers</groupId>
            <artifactId>*</artifactId>
        </exclusion>
        <!-- 排除日志框架冲突 -->
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-reload4j</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j-impl</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
        </exclusion>
        <!-- 排除重复JSON库 -->
        <exclusion>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
        </exclusion>
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
        <!-- 排除云存储SDK -->
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
```

**收益**: ~70+ MB

### 2. SQLite 改为 Optional

**代码变更**:
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.2.0</version>
    <optional>true</optional>
    <scope>runtime</scope>
</dependency>
```

**收益**: 12.89 MB (嵌入式场景才需要)

### 3. 文件解析依赖改为 Optional

**代码变更**:
```xml
<!-- PDF 解析 -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.1</version>
    <optional>true</optional>
</dependency>

<!-- Word/Excel 解析 -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
    <optional>true</optional>
</dependency>
```

**收益**: ~3.85 MB

---

## 验证结果

### 编译测试
```
[INFO] BUILD SUCCESS
[INFO] Total time: 42.825 s
[INFO] Compiled 1032 source files
```

✅ **编译通过** - 无编译错误

### 依赖树对比
- 优化前: 313 行
- 优化后: 188 行
- **减少: 125 行 (40%)**

---

## 剩余工作

### 仍需处理的依赖

| 依赖 | 来源 | 建议 |
|------|------|------|
| gson | jedis传递 | 如不需要可排除 |

### 阶段3-4 待执行任务

- [ ] 拆分 ooder-common 模块
- [ ] 创建向量存储抽象层 SPI
- [ ] 设计模块化架构
- [ ] Maven 多 profile 配置

---

## 文件变更清单

| 文件 | 变更类型 | 说明 |
|------|----------|------|
| `pom.xml` | 修改 | 添加依赖排除和optional标记 |
| `dependency-tree-optimized.txt` | 新增 | 优化后的依赖树 |
| `DEPENDENCY_OPTIMIZATION_RESULT.md` | 新增 | 本报告 |

---

## 参考文档

- [依赖优化分析报告](DEPENDENCY_OPTIMIZATION_ANALYSIS.md)
- [依赖优化任务列表](DEPENDENCY_OPTIMIZATION_TASKS.md)
- [优化前依赖树](dependency-tree.txt)
- [优化后依赖树](dependency-tree-optimized.txt)

---

**报告版本**: 1.0  
**生成日期**: 2026-04-10
