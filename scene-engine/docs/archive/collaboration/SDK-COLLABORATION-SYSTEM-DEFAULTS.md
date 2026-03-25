# SDK 协作需求: 系统级默认配置与自动启动

## 1. 协作概述

**发起方**: SE 团队  
**接收方**: SDK 团队  
**主题**: 系统级默认配置与自动启动机制  
**优先级**: P0  
**日期**: 2026-03-20  
**状态**: 🔴 待确认

---

## 2. 背景说明

### 2.1 当前问题

1. **SQLite 未默认启动**：需要手动配置才能使用数据库
2. **向量嵌入未默认启动**：影响 RAG 场景开箱即用体验
3. **无系统级默认配置**：每次部署都需要重复配置
4. **配置优先级不明确**：推送配置与默认配置的覆盖关系不清晰

### 2.2 目标需求

1. **SQLite 默认启动**：SDK 初始化时自动启动 SQLite 作为系统数据库
2. **向量嵌入默认启动**：本地向量嵌入模型自动启动
3. **17项系统级默认配置**：定义系统级默认 Skill 配置
4. **分层配置优先级**：推送配置 > 场景配置 > Profile 配置 > 系统默认配置

---

## 3. 需求清单

### 3.1 需求概览

| 需求ID | 需求描述 | 优先级 | 说明 |
|--------|----------|--------|------|
| SDK-REQ-001 | 系统默认配置文件 | P0 | 创建 `system-defaults.json` |
| SDK-REQ-002 | SQLite 自动启动 | P0 | SDK 初始化时自动启动 |
| SDK-REQ-003 | 向量嵌入自动启动 | P0 | 本地模型自动启动 |
| SDK-REQ-004 | 配置覆盖机制 | P0 | 推送配置覆盖默认配置 |
| SDK-REQ-005 | 默认接口绑定 | P1 | 注册默认接口实现 |

---

### 3.2 SDK-REQ-001: 系统默认配置文件

**需求描述**:

创建系统默认配置文件，定义 17 项系统 Skill 的默认配置。

**文件路径**: `config/system-defaults.json`

**配置内容**:

```json
{
  "version": "2.4.0",
  "systemSkills": {
    "skill-db-sqlite": {
      "enabled": true,
      "autoStart": true,
      "config": {
        "url": "jdbc:sqlite:./data/system.db",
        "poolSize": 5,
        "connectionTimeout": 30000
      }
    },
    "skill-embedding-local": {
      "enabled": true,
      "autoStart": true,
      "config": {
        "modelPath": "./models/embedding",
        "modelType": "bge-small-zh",
        "dimension": 512,
        "batchSize": 32
      }
    },
    "skill-logging": {
      "enabled": true,
      "autoStart": true,
      "config": {
        "level": "INFO",
        "format": "json",
        "output": ["console", "file"],
        "filePath": "./logs/sdk.log"
      }
    },
    "skill-metrics": {
      "enabled": true,
      "autoStart": true,
      "config": {
        "enabled": true,
        "interval": 60,
        "exporters": ["prometheus"]
      }
    }
  },
  "defaultInterfaceBindings": {
    "DatabaseDriver": "skill-db-sqlite",
    "EmbeddingService": "skill-embedding-local",
    "LlmService": "skill-llm-deepseek",
    "VectorStore": "skill-vector-store",
    "CacheService": "skill-cache-redis",
    "StorageService": "skill-storage-oss",
    "LoggingService": "skill-logging",
    "MetricsService": "skill-metrics"
  }
}
```

**验收标准**:
- [ ] 配置文件创建完成
- [ ] 17 项系统 Skill 默认配置定义
- [ ] 默认接口绑定定义

---

### 3.3 SDK-REQ-002: SQLite 自动启动

**需求描述**:

SDK 初始化时自动启动 SQLite 作为系统数据库，无需手动配置。

**初始化流程**:

```
SDK 初始化
    │
    ├── 1. 加载系统默认配置
    │       └── 读取 system-defaults.json
    │
    ├── 2. 启动必选系统 Skill
    │       ├── skill-db-sqlite（必选）
    │       ├── skill-embedding-local（必选）
    │       ├── skill-logging（必选）
    │       └── skill-metrics（必选）
    │
    ├── 3. 注册默认接口绑定
    │       └── DatabaseDriver → skill-db-sqlite
    │
    └── 4. 等待推送配置
            └── 推送配置可覆盖默认配置
```

**代码实现要求**:

```java
public class OoderSdkImpl implements OoderSdk {
    
    private void initializeSystemSkills() {
        // 1. 加载系统默认配置
        ConfigNode systemDefaults = loadSystemDefaults();
        
        // 2. 启动必选系统 Skill
        startRequiredSystemSkills(systemDefaults);
        
        // 3. 注册默认接口绑定
        registerDefaultInterfaceBindings(systemDefaults);
    }
    
    private void startRequiredSystemSkills(ConfigNode config) {
        // SQLite 数据库（必选）
        startSkill("skill-db-sqlite", config.getChild("skill-db-sqlite"));
        
        // 向量嵌入（必选）
        startSkill("skill-embedding-local", config.getChild("skill-embedding-local"));
        
        // 日志服务（必选）
        startSkill("skill-logging", config.getChild("skill-logging"));
        
        // 指标服务（必选）
        startSkill("skill-metrics", config.getChild("skill-metrics"));
    }
}
```

**验收标准**:
- [ ] SDK 初始化时自动创建 `./data/system.db`
- [ ] 无需手动配置即可使用数据库
- [ ] 数据库连接池正常工作

---

### 3.4 SDK-REQ-003: 向量嵌入自动启动

**需求描述**:

SDK 初始化时自动启动本地向量嵌入模型，支持 RAG 场景开箱即用。

**配置要求**:

```json
{
  "skill-embedding-local": {
    "enabled": true,
    "autoStart": true,
    "config": {
      "modelPath": "./models/embedding",
      "modelType": "bge-small-zh",
      "dimension": 512,
      "batchSize": 32,
      "maxSequenceLength": 512,
      "normalize": true
    }
  }
}
```

**模型下载**:

- 首次启动时自动下载模型到 `./models/embedding`
- 支持离线模式（模型已存在时跳过下载）
- 支持配置自定义模型路径

**验收标准**:
- [ ] SDK 初始化时自动加载向量嵌入模型
- [ ] 支持中英文向量嵌入
- [ ] 向量维度 512，支持配置

---

### 3.5 SDK-REQ-004: 配置覆盖机制

**需求描述**:

实现配置分层覆盖机制，支持推送配置覆盖默认配置。

**配置优先级**:

```
推送配置 > 场景配置 > Profile 配置 > 系统默认配置
```

**合并策略**:

```java
public class ConfigMergeStrategy {
    
    /**
     * 合并配置（深度合并）
     * 
     * @param base 基础配置
     * @param override 覆盖配置
     * @return 合并后的配置
     */
    public ConfigNode merge(ConfigNode base, ConfigNode override) {
        ConfigNode result = base.deepCopy();
        
        for (String key : override.keys()) {
            Object overrideValue = override.get(key);
            
            if (overrideValue instanceof ConfigNode) {
                ConfigNode baseChild = result.getChild(key);
                if (baseChild != null) {
                    result.setChild(key, merge(baseChild, (ConfigNode) overrideValue));
                } else {
                    result.setChild(key, (ConfigNode) overrideValue);
                }
            } else {
                result.set(key, overrideValue);
            }
        }
        
        return result;
    }
}
```

**验收标准**:
- [ ] 推送配置可覆盖默认配置
- [ ] 支持深度合并（嵌套配置）
- [ ] 配置合并日志可追溯

---

### 3.6 SDK-REQ-005: 默认接口绑定

**需求描述**:

SDK 初始化时注册默认接口绑定，简化应用层配置。

**默认绑定**:

| 接口 | 默认实现 | 说明 |
|------|----------|------|
| `DatabaseDriver` | `skill-db-sqlite` | SQLite 数据库 |
| `EmbeddingService` | `skill-embedding-local` | 本地向量嵌入 |
| `LlmService` | `skill-llm-deepseek` | DeepSeek LLM（需配置 API Key） |
| `VectorStore` | `skill-vector-store` | 向量存储（需配置） |
| `CacheService` | `skill-cache-redis` | Redis 缓存（需配置） |
| `StorageService` | `skill-storage-oss` | OSS 存储（需配置） |
| `LoggingService` | `skill-logging` | 日志服务 |
| `MetricsService` | `skill-metrics` | 指标服务 |

**验收标准**:
- [ ] 默认接口绑定注册完成
- [ ] 可通过推送配置覆盖绑定
- [ ] 接口解析正常工作

---

## 4. 系统默认 Skill 清单（17项）

| 序号 | Skill ID | 说明 | 默认启动 |
|------|----------|------|----------|
| 1 | `skill-db-sqlite` | SQLite 数据库 | ✅ 是 |
| 2 | `skill-db-mysql` | MySQL 数据库 | 否 |
| 3 | `skill-llm-openai` | OpenAI LLM | 否 |
| 4 | `skill-llm-deepseek` | DeepSeek LLM | 否 |
| 5 | `skill-llm-anthropic` | Anthropic LLM | 否 |
| 6 | `skill-llm-azure` | Azure OpenAI | 否 |
| 7 | `skill-embedding-local` | 本地向量嵌入 | ✅ 是 |
| 8 | `skill-rerank` | 重排序 | 否 |
| 9 | `skill-knowledge-base` | 知识库 | 否 |
| 10 | `skill-vector-store` | 向量存储 | 否 |
| 11 | `skill-cache-redis` | Redis 缓存 | 否 |
| 12 | `skill-storage-oss` | OSS 存储 | 否 |
| 13 | `skill-storage-minio` | MinIO 存储 | 否 |
| 14 | `skill-notification` | 通知服务 | 否 |
| 15 | `skill-logging` | 日志服务 | ✅ 是 |
| 16 | `skill-metrics` | 指标服务 | ✅ 是 |
| 17 | `skill-tracing` | 链路追踪 | 否 |

**自动启动的系统 Skill（4项）**：
1. `skill-db-sqlite` - 系统数据库
2. `skill-embedding-local` - 本地向量嵌入
3. `skill-logging` - 日志服务
4. `skill-metrics` - 指标服务

---

## 5. 时间计划

| 阶段 | 任务 | 预计时间 | 状态 |
|------|------|----------|------|
| Phase 1 | 创建系统默认配置文件 | 1-2 天 | 待开始 |
| Phase 2 | 实现 SQLite 自动启动 | 2-3 天 | 待开始 |
| Phase 3 | 实现向量嵌入自动启动 | 2-3 天 | 待开始 |
| Phase 4 | 实现配置覆盖机制 | 2-3 天 | 待开始 |
| Phase 5 | 测试验证 | 2-3 天 | 待开始 |
| **总计** | - | **9-14 天** | - |

---

## 6. 验收标准

### 6.1 功能验收

- [ ] SDK 初始化时自动启动 SQLite
- [ ] SDK 初始化时自动启动向量嵌入
- [ ] 系统默认配置文件加载正常
- [ ] 推送配置可覆盖默认配置
- [ ] 默认接口绑定正常工作

### 6.2 性能验收

- [ ] SDK 初始化时间 < 5s（无网络）
- [ ] 向量嵌入模型加载时间 < 3s
- [ ] 数据库连接池初始化时间 < 1s

---

## 7. 状态

- [x] 需求文档完成
- [ ] SDK 团队确认
- [ ] 开始实施

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**SE 团队**: SceneEngine Team
