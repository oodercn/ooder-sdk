# 向量存储架构规范

## 一、架构分层原则

遵循 **微（降级）→ 小 → 大** 的解耦架构：

| 层级 | 定义 | 存储容量 | 持久化 | 适用场景 |
|------|------|----------|--------|----------|
| **微** | 降级方案 | < 1万向量 | 内存 | 开发测试、离线场景 |
| **小** | 轻量方案 | < 100万向量 | SQLite/文件 | 小团队、边缘部署 |
| **大** | 企业方案 | 无限制 | 分布式数据库 | 大规模生产环境 |

## 二、接口定义（SDK 层）

### 2.1 VectorStore 接口

**位置**: `net.ooder.scene.skill.vector.VectorStore`

```java
public interface VectorStore {
    void insert(String id, float[] vector, Map<String, Object> metadata);
    void batchInsert(List<VectorData> dataList);
    List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters);
    void delete(String id);
    void deleteByMetadata(Map<String, Object> filters);
    int getDimension();
    long count();
    void clear();
}
```

### 2.2 EmbeddingService 接口

**位置**: `net.ooder.scene.skill.vector.EmbeddingService`

```java
public interface EmbeddingService {
    float[] embed(String text);
    List<float[]> embedBatch(List<String> texts);
    int getDimension();
    String getModel();
}
```

**重要**: 统一使用 `float[]` 类型，与主流向量数据库兼容。

## 三、实现层级

### 3.1 微层（降级）- scene-engine 内置

| 组件 | 类名 | 说明 |
|------|------|------|
| 向量存储 | `InMemoryVectorStore` | 纯内存实现，重启丢失 |
| 嵌入服务 | `LlmEmbeddingServiceAdapter` | 适配 LLM 层嵌入能力 |

**依赖配置**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>
```

### 3.2 小层 - skill-vector-sqlite

| 组件 | 类名 | 说明 |
|------|------|------|
| 向量存储 | `SqliteVectorStore` | SQLite 持久化 |
| 嵌入服务 | 本地模型或 Mock | 可选实现 |

**依赖配置**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-vector-sqlite</artifactId>
    <version>2.3</version>
</dependency>
```

**需要修复**: `skill-vector-sqlite` 中的 `EmbeddingService` 应删除，改为依赖 SDK 接口。

### 3.3 大层 - skill-vector-milvus（规划）

| 组件 | 类名 | 说明 |
|------|------|------|
| 向量存储 | `MilvusVectorStore` | Milvus 分布式向量库 |
| 嵌入服务 | 云端 API | OpenAI/Azure 等 |

## 四、类型规范

### 4.1 向量类型

**统一使用 `float[]`**

理由：
- 主流向量数据库（Milvus、Pinecone、Weaviate）使用 float32
- 减少类型转换开销
- 内存占用更小

### 4.2 已发现问题

| 位置 | 问题 | 修复方案 |
|------|------|----------|
| skill-vector-sqlite | `EmbeddingService` 使用 `double[]` | 改为 `float[]` |
| skill-vector-sqlite | 重复定义 `EmbeddingService` 接口 | 删除，依赖 SDK 接口 |

## 五、Skill 开发规范

### 5.1 新建向量存储 Skill

```java
// 正确：实现 SDK 接口
public class MyVectorStore implements VectorStore {
    // 使用 float[]
}

// 错误：重新定义接口
public interface MyVectorStore {  // 不要这样做
    double[] embed(String text);   // 不要使用 double[]
}
```

### 5.2 依赖声明

```xml
<!-- Skill pom.xml -->
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine</artifactId>
        <version>2.3</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## 六、迁移指南

### 6.1 skill-vector-sqlite 修复步骤

1. **删除重复接口**
   - 删除 `net.ooder.skill.vector.sqlite.EmbeddingService`

2. **修改 MockEmbeddingService**
   ```java
   // 改为实现 SDK 接口
   @Service
   public class MockEmbeddingService 
           implements net.ooder.scene.skill.vector.EmbeddingService {
       
       @Override
       public float[] embed(String text) {
           // 返回 float[] 而非 double[]
       }
   }
   ```

3. **更新 SqliteVectorStore**
   - 注入 `net.ooder.scene.skill.vector.EmbeddingService`

## 七、架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      Application Layer                       │
│                    (Controller / Service)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     scene-engine SDK                         │
│  ┌─────────────────┐  ┌─────────────────┐                   │
│  │  VectorStore    │  │ EmbeddingService│                   │
│  │  (interface)    │  │  (interface)    │                   │
│  └────────┬────────┘  └────────┬────────┘                   │
│           │                    │                            │
│  ┌────────▼────────┐  ┌────────▼────────┐                   │
│  │InMemoryVector   │  │LlmEmbedding     │                   │
│  │Store (微层)     │  │Adapter          │                   │
│  └─────────────────┘  └─────────────────┘                   │
└─────────────────────────────────────────────────────────────┘
                              │
           ┌──────────────────┼──────────────────┐
           ▼                  ▼                  ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│skill-vector-    │  │skill-vector-    │  │skill-vector-    │
│sqlite (小层)    │  │milvus (大层)    │  │custom (扩展)    │
│                 │  │                 │  │                 │
│SqliteVectorStore│  │MilvusVectorStore│  │YourVectorStore  │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

## 八、版本历史

| 版本 | 日期 | 变更 |
|------|------|------|
| 2.3 | 2026-03-06 | 初始版本，定义微/小/大分层架构 |
