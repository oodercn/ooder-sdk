# Scene-Engine 依赖优化任务列表

> 基于 `DEPENDENCY_OPTIMIZATION_ANALYSIS.md` 分析结果制定的详细实施计划

---

## 📋 任务总览

| 阶段 | 任务数 | 预计收益 | 工作量 | 优先级 |
|------|--------|----------|--------|--------|
| 阶段1: 微小优化 | 3个 | ~19 MB | < 1天 | P0 |
| 阶段2: 小优化 | 3个 | ~78 MB | 1-3天 | P1 |
| 阶段3: 中优化 | 2个 | ~80 MB | 1周 | P2 |
| 阶段4: 大优化 | 2个 | ~150+ MB | 2-4周 | P3 |
| **总计** | **10个** | **~327 MB** | - | - |

---

## 阶段1: 微小优化 (P0 - 立即执行)

### 任务1.1: 移除Testcontainers依赖排除
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 30分钟  
**收益**: ~12 MB

**实施步骤**:
1. [ ] 修改 `e:\github\ooder-sdk\scene-engine\pom.xml`
2. [ ] 在 `milvus-sdk-java` 依赖中添加排除项
3. [ ] 验证排除生效: `mvn dependency:tree | grep testcontainers`

**代码变更**:
```xml
<dependency>
    <groupId>io.milvus</groupId>
    <artifactId>milvus-sdk-java</artifactId>
    <version>2.4.1</version>
    <exclusions>
        <exclusion>
            <groupId>org.testcontainers</groupId>
            <artifactId>*</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

**验证标准**:
- [ ] `org.testcontainers` 不再出现在依赖树中
- [ ] 项目编译成功: `mvn clean compile`

---

### 任务1.2: 统一日志框架排除log4j冲突
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 30分钟  
**收益**: ~5 MB

**实施步骤**:
1. [ ] 分析当前日志框架冲突情况
2. [ ] 排除Hadoop传递的log4j依赖
3. [ ] 排除Milvus传递的log4j-slf4j-impl
4. [ ] 验证日志输出正常

**代码变更**:
```xml
<!-- 在 milvus-sdk-java 依赖中添加 -->
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
```

**验证标准**:
- [ ] 依赖树中无 `log4j-core`
- [ ] 依赖树中无 `slf4j-reload4j`
- [ ] 应用启动日志正常输出

---

### 任务1.3: 移除重复JSON库(fastjson/gson)
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 30分钟  
**收益**: ~2 MB

**实施步骤**:
1. [ ] 检查 `ooder-annotation` 的 fastjson 依赖
2. [ ] 检查 `milvus-sdk-java` 的 gson 依赖
3. [ ] 添加排除项
4. [ ] 验证代码中使用Jackson替代

**代码变更**:
```xml
<!-- 在 ooder-annotation 依赖中添加 -->
<exclusion>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
</exclusion>

<!-- 在 milvus-sdk-java 依赖中添加 -->
<exclusion>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</exclusion>
```

**验证标准**:
- [ ] 依赖树中无 `fastjson` (1.x版本)
- [ ] 依赖树中无 `gson`
- [ ] 所有JSON操作使用Jackson完成

---

## 阶段2: 小优化 (P1 - 本周内)

### 任务2.1: SQLite改为optional依赖
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 2小时  
**收益**: ~13 MB

**实施步骤**:
1. [ ] 分析SQLite使用场景
2. [ ] 修改pom.xml标记为optional
3. [ ] 添加配置开关控制加载
4. [ ] 更新部署文档

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

**验证标准**:
- [ ] 默认打包不包含sqlite-jdbc
- [ ] 显式启用时功能正常
- [ ] 文档已更新

---

### 任务2.2: PDF/Office解析改为SPI动态加载
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 1天  
**收益**: ~15 MB

**实施步骤**:
1. [ ] 创建 `DocumentParser` SPI接口
2. [ ] 重构PDF解析为独立模块
3. [ ] 重构Office解析为独立模块
4. [ ] 修改pom.xml标记为optional
5. [ ] 创建ServiceLoader加载逻辑

**代码变更**:
```java
// 创建接口
package net.ooder.scene.spi.parser;

public interface DocumentParser {
    boolean supports(String mimeType);
    String parse(InputStream input) throws IOException;
}
```

```xml
<!-- pom.xml 修改 -->
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

**验证标准**:
- [ ] SPI接口定义完成
- [ ] PDF/Office依赖标记为optional
- [ ] 动态加载逻辑工作正常
- [ ] 无依赖时优雅降级

---

### 任务2.3: 精简Milvus SDK依赖排除Hadoop/云存储
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 1天  
**收益**: ~50+ MB

**实施步骤**:
1. [ ] 分析Milvus SDK实际使用的依赖
2. [ ] 排除Hadoop生态相关依赖
3. [ ] 排除Parquet相关依赖
4. [ ] 排除云存储SDK (AWS/Azure/MinIO)
5. [ ] 验证Milvus核心功能正常

**代码变更**:
```xml
<dependency>
    <groupId>io.milvus</groupId>
    <artifactId>milvus-sdk-java</artifactId>
    <version>2.4.1</version>
    <exclusions>
        <!-- Hadoop生态 -->
        <exclusion>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>*</artifactId>
        </exclusion>
        <!-- Parquet -->
        <exclusion>
            <groupId>org.apache.parquet</groupId>
            <artifactId>*</artifactId>
        </exclusion>
        <!-- 云存储 -->
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
        <!-- Testcontainers -->
        <exclusion>
            <groupId>org.testcontainers</groupId>
            <artifactId>*</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

**验证标准**:
- [ ] Milvus向量搜索功能正常
- [ ] 依赖树中无Hadoop相关依赖
- [ ] 依赖树中无云存储SDK
- [ ] 单元测试通过

---

## 阶段3: 中优化 (P2 - 本月内)

### 任务3.1: 拆分ooder-common模块
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 3天  
**收益**: ~20 MB

**实施步骤**:
1. [ ] 分析ooder-common各模块职责
2. [ ] 创建 `ooder-common-core` (接口和基础类)
3. [ ] 创建 `ooder-common-http` (HTTP客户端)
4. [ ] 创建 `ooder-common-cache` (Redis/Jedis)
5. [ ] 创建 `ooder-common-template` (Freemarker)
6. [ ] 创建 `ooder-common-search` (Lucene，可选)
7. [ ] 更新scene-engine依赖

**模块结构**:
```
ooder-common/
├── ooder-common-core/          (核心接口，轻量)
├── ooder-common-http/          (HTTP客户端)
├── ooder-common-cache/         (Redis/Jedis)
├── ooder-common-template/      (Freemarker)
└── ooder-common-search/        (Lucene，optional)
```

**验证标准**:
- [ ] 新模块结构清晰
- [ ] scene-engine只依赖core模块
- [ ] 功能正常无缺失
- [ ] 文档已更新

---

### 任务3.2: 创建向量存储抽象层SPI接口
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 2天  
**收益**: ~60+ MB (MVP可移除Milvus)

**实施步骤**:
1. [ ] 设计 `VectorStore` SPI接口
2. [ ] 创建内存实现 `InMemoryVectorStore`
3. [ ] 将Milvus实现移到独立模块
4. [ ] 创建自动配置类
5. [ ] 更新使用方代码

**代码变更**:
```java
// SPI接口
package net.ooder.scene.spi.vector;

public interface VectorStore {
    void upsert(String collection, List<Vector> vectors);
    List<Vector> search(String collection, Vector query, int topK);
    boolean isAvailable();
}

// 内存实现 (默认)
public class InMemoryVectorStore implements VectorStore { }

// Milvus实现 (optional)
public class MilvusVectorStore implements VectorStore { }
```

**验证标准**:
- [ ] SPI接口定义完成
- [ ] 内存实现功能正常
- [ ] Milvus变为optional依赖
- [ ] 自动配置工作正常

---

## 阶段4: 大优化 (P3 - 长期规划)

### 任务4.1: 设计模块化架构
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 2周  
**收益**: ~150+ MB

**实施步骤**:
1. [ ] 设计模块划分方案
2. [ ] 创建 `scene-engine-core` (核心引擎)
3. [ ] 创建 `scene-engine-llm` (LLM集成)
4. [ ] 创建 `scene-engine-vector` (向量存储)
5. [ ] 创建 `scene-engine-file` (文件解析)
6. [ ] 创建 `scene-engine-storage` (云存储)
7. [ ] 设计模块间依赖关系

**模块结构**:
```
scene-engine/
├── scene-engine-core/          (核心引擎，轻量)
├── scene-engine-llm/           (LLM集成，optional)
├── scene-engine-vector/        (向量存储，optional)
├── scene-engine-file/          (文件解析，optional)
└── scene-engine-storage/       (云存储，optional)
```

**验证标准**:
- [ ] 模块划分合理
- [ ] 核心模块无冗余依赖
- [ ] 可选模块可独立使用
- [ ] 文档和示例完整

---

### 任务4.2: Maven多profile配置
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 1周  
**收益**: 配合模块化使用

**实施步骤**:
1. [ ] 设计profile方案
2. [ ] 创建 `minimal` profile (仅核心)
3. [ ] 创建 `standard` profile (常用功能)
4. [ ] 创建 `full` profile (全部功能)
5. [ ] 编写profile使用文档

**代码变更**:
```xml
<profiles>
    <profile>
        <id>minimal</id>
        <dependencies>
            <!-- 仅核心依赖 -->
        </dependencies>
    </profile>
    <profile>
        <id>standard</id>
        <dependencies>
            <!-- 核心 + 常用功能 -->
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

**验证标准**:
- [ ] minimal profile编译成功
- [ ] standard profile编译成功
- [ ] full profile编译成功
- [ ] 文档已更新

---

## 验证与测试任务

### 任务V1: 验证优化后依赖树并对比体积
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 2小时

**检查项**:
- [ ] 生成优化前依赖树: `mvn dependency:tree > before.txt`
- [ ] 生成优化后依赖树: `mvn dependency:tree > after.txt`
- [ ] 统计jar包数量对比
- [ ] 统计总体积对比
- [ ] 生成对比报告

**成功标准**:
- 依赖数量减少 > 30%
- 总体积减少 > 40%

---

### 任务V2: 运行完整测试套件确保功能正常
**状态**: ⬜ 待开始  
**负责人**: TBD  
**预计耗时**: 4小时

**检查项**:
- [ ] 单元测试: `mvn test`
- [ ] 集成测试: `mvn integration-test`
- [ ] 启动测试: 应用正常启动
- [ ] 功能测试: 核心功能正常
- [ ] 性能测试: 无性能退化

**成功标准**:
- 所有测试通过
- 启动时间无显著增加
- 内存占用无显著增加

---

## 实施路线图

```
Week 1 (立即执行)
├── Day 1: 任务1.1 + 1.2 + 1.3 (微小优化)
├── Day 2: 任务2.1 (SQLite优化)
├── Day 3-4: 任务2.2 (文件解析SPI)
└── Day 5: 任务2.3 (Milvus精简) + V1验证

Week 2-3 (本月内)
├── Week 2: 任务3.1 (拆分ooder-common)
└── Week 3: 任务3.2 (向量存储SPI) + V2验证

Month 2-3 (长期规划)
├── Month 2: 任务4.1 (模块化架构)
└── Month 3: 任务4.2 (Maven profile)
```

---

## 风险与注意事项

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| Milvus功能异常 | 高 | 充分测试向量搜索功能 |
| 文件解析失效 | 中 | SPI加载失败时优雅降级 |
| 日志输出异常 | 中 | 验证所有日志输出正常 |
| 依赖冲突 | 中 | 每步优化后验证依赖树 |

---

## 参考文档

- [依赖优化分析报告](DEPENDENCY_OPTIMIZATION_ANALYSIS.md)
- [主POM文件](e:\github\ooder-sdk\scene-engine\pom.xml)
- [父POM文件](e:\github\ooder-sdk\pom.xml)
- [Maven依赖树](e:\github\ooder-sdk\scene-engine\dependency-tree.txt)

---

**文档版本**: 1.0  
**创建日期**: 2026-04-10  
**最后更新**: 2026-04-10
