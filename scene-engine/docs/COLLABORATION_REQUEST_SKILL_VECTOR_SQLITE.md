# 协作请求：skill-vector-sqlite 架构规范化

## 请求方
scene-engine SDK v2.3

## 请求目标
修复 skill-vector-sqlite 中的架构违规问题，确保与 SDK 接口一致。

## 问题清单

### 问题 1：重复定义 EmbeddingService 接口

**当前状态**：
```
skill-vector-sqlite/
└── src/main/java/net/ooder/skill/vector/sqlite/
    └── EmbeddingService.java  ← 重复定义
```

**SDK 接口**：
```
scene-engine/
└── src/main/java/net/ooder/scene/skill/vector/
    └── EmbeddingService.java  ← 正确定义
```

**修复方案**：
删除 `net.ooder.skill.vector.sqlite.EmbeddingService`，改为依赖 SDK 接口。

### 问题 2：向量类型不一致

**当前**：使用 `double[]`
```java
// skill-vector-sqlite/EmbeddingService.java
double[] embed(String text);
```

**应该**：使用 `float[]`
```java
// scene-engine/EmbeddingService.java
float[] embed(String text);
```

**修复方案**：
将所有 `double[]` 改为 `float[]`。

## 修复步骤

### Step 1: 删除重复接口

```bash
# 删除文件
rm skills/skill-vector-sqlite/src/main/java/net/ooder/skill/vector/sqlite/EmbeddingService.java
```

### Step 2: 修改 MockEmbeddingService

```java
package net.ooder.skill.vector.sqlite;

import net.ooder.scene.skill.vector.EmbeddingService;
import org.springframework.stereotype.Service;

@Service
public class MockEmbeddingService implements EmbeddingService {
    
    private int dimension = 1536;
    
    @Override
    public float[] embed(String text) {
        float[] embedding = new float[dimension];
        // ... 使用 float 而非 double
        return embedding;
    }
    
    @Override
    public List<float[]> embedBatch(List<String> texts) {
        // ... 返回 List<float[]>
    }
    
    @Override
    public int getDimension() {
        return dimension;
    }
    
    @Override
    public String getModel() {
        return "mock-embedding";
    }
}
```

### Step 3: 更新 SqliteVectorStore

确保注入的是 SDK 接口：
```java
import net.ooder.scene.skill.vector.EmbeddingService;
import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.vector.VectorData;
import net.ooder.scene.skill.vector.SearchResult;
```

### Step 4: 更新 pom.xml

确认依赖：
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
    <scope>provided</scope>
</dependency>
```

## 架构规范

修复后应遵循的架构：

```
scene-engine SDK (接口定义)
├── VectorStore (interface)
├── EmbeddingService (interface) ← float[]
└── InMemoryVectorStore (微层实现)

skill-vector-sqlite (小层实现)
├── SqliteVectorStore implements VectorStore ✓
└── MockEmbeddingService implements EmbeddingService ← 需修复
```

## 验证标准

- [ ] 编译通过
- [ ] 无重复接口定义
- [ ] 统一使用 `float[]` 类型
- [ ] 正确实现 SDK 接口

## 联系方式

如有疑问，请联系 scene-engine 维护团队。

---

**创建时间**: 2026-03-06
**优先级**: 高
**状态**: 待处理
