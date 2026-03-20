# SDK 系统级默认配置方案

## 一、方案概述

### 1.1 目标

1. **SQLite 默认启动**：SDK 初始化时自动启动 SQLite 作为系统数据库
2. **17项系统级默认配置**：定义系统级默认 Skill 配置
3. **分层配置优先级**：推送配置 > Profile 配置 > 系统默认配置

### 1.2 配置层级

```
┌─────────────────────────────────────────────────────────────┐
│                     配置优先级（从高到低）                    │
├─────────────────────────────────────────────────────────────┤
│  1. 推送配置 (Push Config)                                   │
│     └── 外部推送的 Skill 配置，优先级最高                     │
│                                                             │
│  2. 场景配置 (Scene Config)                                  │
│     └── 场景级别的 Skill 配置                                │
│                                                             │
│  3. Profile 配置 (Profile Config)                           │
│     └── 环境配置（dev/test/prod）                            │
│                                                             │
│  4. 系统默认配置 (System Default Config)                     │
│     └── 系统级默认配置，优先级最低                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、系统级默认配置设计

### 2.1 系统默认 Skill 清单（17项）

| 序号 | Skill ID | 说明 | 默认配置 |
|------|----------|------|----------|
| 1 | `skill-db-sqlite` | SQLite 数据库 | **自动启动**，数据路径 `./data/system.db` |
| 2 | `skill-db-mysql` | MySQL 数据库 | 按需启动 |
| 3 | `skill-llm-openai` | OpenAI LLM | 按需配置 |
| 4 | `skill-llm-deepseek` | DeepSeek LLM | 按需配置 |
| 5 | `skill-llm-anthropic` | Anthropic LLM | 按需配置 |
| 6 | `skill-llm-azure` | Azure OpenAI | 按需配置 |
| 7 | `skill-embedding-local` | 本地向量嵌入 | **自动启动**，本地模型 `./models/embedding` |
| 8 | `skill-rerank` | 重排序 | 按需配置 |
| 9 | `skill-knowledge-base` | 知识库 | 按需配置 |
| 10 | `skill-vector-store` | 向量存储 | 按需配置 |
| 11 | `skill-cache-redis` | Redis 缓存 | 按需配置 |
| 12 | `skill-storage-oss` | OSS 存储 | 按需配置 |
| 13 | `skill-storage-minio` | MinIO 存储 | 按需配置 |
| 14 | `skill-notification` | 通知服务 | 按需配置 |
| 15 | `skill-logging` | 日志服务 | **自动启动** |
| 16 | `skill-metrics` | 指标服务 | **自动启动** |
| 17 | `skill-tracing` | 链路追踪 | 按需配置 |

**自动启动的系统 Skill（4项）**：
1. `skill-db-sqlite` - 系统数据库
2. `skill-embedding-local` - 本地向量嵌入
3. `skill-logging` - 日志服务
4. `skill-metrics` - 指标服务

### 2.2 系统默认配置文件

**文件路径**: `config/system-defaults.json`

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

---

## 三、SDK 初始化流程

### 3.1 初始化顺序

```
┌─────────────────────────────────────────────────────────────┐
│                     SDK 初始化流程                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 加载系统默认配置                                         │
│     └── 读取 system-defaults.json                           │
│                                                             │
│  2. 加载 Profile 配置                                        │
│     └── 根据环境变量读取 dev/test/prod 配置                  │
│                                                             │
│  3. 初始化系统 Skill                                         │
│     ├── 启动 skill-db-sqlite（必选）                        │
│     ├── 启动 skill-logging（必选）                          │
│     └── 启动 skill-metrics（必选）                          │
│                                                             │
│  4. 注册默认接口绑定                                         │
│     └── DatabaseDriver → skill-db-sqlite                    │
│                                                             │
│  5. 等待推送配置                                             │
│     └── 推送配置可覆盖默认配置                               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 代码实现

**OoderSdkImpl 初始化增强**:

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
        
        // 日志服务（必选）
        startSkill("skill-logging", config.getChild("skill-logging"));
        
        // 指标服务（必选）
        startSkill("skill-metrics", config.getChild("skill-metrics"));
    }
    
    private void startSkill(String skillId, ConfigNode config) {
        if (config != null && config.getBoolean("autoStart", false)) {
            SkillInstance skill = skillManager.loadSkill(skillId, config);
            skill.start();
            log.info("Started system skill: {}", skillId);
        }
    }
}
```

---

## 四、配置覆盖机制

### 4.1 推送配置覆盖

```java
public class ConfigMergeStrategy {
    
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

### 4.2 配置优先级示例

```yaml
# 系统默认配置
skill-db-sqlite:
  enabled: true
  autoStart: true
  config:
    url: "jdbc:sqlite:./data/system.db"
    poolSize: 5

# Profile 配置 (prod)
skill-db-sqlite:
  config:
    poolSize: 20  # 覆盖默认值

# 推送配置
skill-db-sqlite:
  config:
    url: "jdbc:sqlite:/data/production.db"  # 覆盖 Profile 配置
```

**最终合并结果**:
```yaml
skill-db-sqlite:
  enabled: true
  autoStart: true
  config:
    url: "jdbc:sqlite:/data/production.db"  # 推送配置
    poolSize: 20  # Profile 配置
```

---

## 五、实现计划

### 5.1 Phase 1: 系统默认配置文件

- [ ] 创建 `system-defaults.json` 文件
- [ ] 定义 17 项系统 Skill 默认配置
- [ ] 定义默认接口绑定

### 5.2 Phase 2: SDK 初始化增强

- [ ] 增强 `OoderSdkImpl.initialize()` 方法
- [ ] 添加 `initializeSystemSkills()` 方法
- [ ] 添加 SQLite 自动启动逻辑

### 5.3 Phase 3: 配置覆盖机制

- [ ] 实现 `ConfigMergeStrategy` 类
- [ ] 实现推送配置覆盖逻辑
- [ ] 添加配置优先级测试

---

## 六、状态

- [x] 方案设计完成
- [ ] 实现代码
- [ ] 测试验证
- [ ] 文档更新

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**作者**: SE Team
