# LLM-CHAT 配置复杂度评估与多级加载方案

## 一、配置爆炸问题分析

### 1.1 当前方案配置项统计

| 配置文件 | 配置项数量 | 复杂度 |
|----------|------------|--------|
| `llm-config.yaml` | ~50项 | 高 |
| `prompts.yaml` | ~30项 | 中 |
| `tools.yaml` | ~40项 | 高 |
| **总计** | **~120项** | **极高** |

### 1.2 配置爆炸原因

```
┌─────────────────────────────────────────────────────────────────┐
│                    配置爆炸根源分析                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 单文件承载过多职责                                           │
│     └── llm-config.yaml 包含：Provider、Model、Retry、Fallback  │
│                                                                 │
│  2. 配置层级过深                                                 │
│     └── spec.providers[].models[].config.xxx                    │
│                                                                 │
│  3. 重复配置                                                     │
│     └── 多个 Provider 重复定义相同的参数结构                      │
│                                                                 │
│  4. 缺乏分层                                                     │
│     └── 系统/应用/用户配置混在一起                                │
│                                                                 │
│  5. 环境耦合                                                     │
│     └── 开发/测试/生产配置未分离                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 风险评估

| 风险 | 等级 | 说明 |
|------|------|------|
| 维护困难 | 🔴 高 | 单文件修改影响全局 |
| 环境切换复杂 | 🔴 高 | 需要手动修改多处配置 |
| 配置冲突 | 🟡 中 | 多环境配置容易冲突 |
| 加载性能 | 🟡 中 | 大配置文件解析慢 |
| 安全风险 | 🔴 高 | 敏感信息分散在多处 |

---

## 二、多级加载方案设计

### 2.1 配置分层架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    多级配置加载架构                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Level 4: 用户配置 (User Config)                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ~/.ooder/llm-config.yaml                                │   │
│  │ - 用户自定义 Provider                                    │   │
│  │ - 用户 API Key                                           │   │
│  │ - 用户偏好设置                                           │   │
│  └─────────────────────────────────────────────────────────┘   │
│                          ↓ 覆盖                                 │
│  Level 3: 应用配置 (App Config)                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ config/llm-config.yaml                                   │   │
│  │ - 应用级 Provider 配置                                    │   │
│  │ - 应用级 Prompt 模板                                      │   │
│  │ - 应用级 Tool 定义                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                          ↓ 覆盖                                 │
│  Level 2: 环境配置 (Env Config)                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ config/env/{env}/llm-config.yaml                         │   │
│  │ - 开发环境配置                                            │   │
│  │ - 测试环境配置                                            │   │
│  │ - 生产环境配置                                            │   │
│  └─────────────────────────────────────────────────────────┘   │
│                          ↓ 覆盖                                 │
│  Level 1: 系统默认配置 (System Default)                        │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ classpath:llm-config-default.yaml                        │   │
│  │ - 内置 Provider 定义                                      │   │
│  │ - 默认参数值                                              │   │
│  │ - 系统级 Prompt 模板                                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 配置文件拆分

```
config/
├── llm/                          # LLM 配置目录
│   ├── default/                  # 系统默认配置
│   │   ├── providers.yaml        # Provider 定义
│   │   ├── models.yaml           # Model 定义
│   │   ├── defaults.yaml         # 默认参数
│   │   └── retry.yaml            # 重试策略
│   │
│   ├── env/                      # 环境配置
│   │   ├── dev/
│   │   │   └── llm-config.yaml
│   │   ├── test/
│   │   │   └── llm-config.yaml
│   │   └── prod/
│   │       └── llm-config.yaml
│   │
│   └── app/                      # 应用配置
│       ├── providers.yaml        # 应用级 Provider
│       └── overrides.yaml        # 覆盖配置
│
├── prompts/                      # Prompt 模板目录
│   ├── default/                  # 系统模板
│   │   ├── assistant.yaml
│   │   └── wizard.yaml
│   └── app/                      # 应用模板
│       └── custom.yaml
│
└── tools/                        # Tool 定义目录
    ├── default/                  # 系统工具
    │   ├── discovery.yaml
    │   └── install.yaml
    └── app/                      # 应用工具
        └── custom.yaml
```

### 2.3 配置加载优先级

| 优先级 | 配置来源 | 加载时机 | 覆盖规则 |
|--------|----------|----------|----------|
| 1 (最高) | 环境变量 | 启动时 | 完全覆盖 |
| 2 | 用户配置 (~/.ooder/) | 启动时 | 深度合并 |
| 3 | 应用配置 (config/app/) | 启动时 | 深度合并 |
| 4 | 环境配置 (config/env/{env}/) | 启动时 | 深度合并 |
| 5 (最低) | 系统默认 (classpath:) | 启动时 | 基础配置 |

### 2.4 配置合并策略

```java
/**
 * 配置合并策略
 */
public enum MergeStrategy {
    DEEP_MERGE,      // 深度合并：递归合并 Map，List 追加
    SHALLOW_MERGE,   // 浅合并：直接覆盖
    REPLACE,         // 替换：完全替换
    APPEND           // 追加：仅追加新项
}

// 示例：Provider 配置合并
// Level 1 (系统默认)
providers:
  - id: mock
    type: mock
    enabled: true

// Level 3 (应用配置)
providers:
  - id: deepseek
    type: deepseek
    enabled: true
    config:
      apiKey: ${DEEPSEEK_API_KEY}

// 合并结果
providers:
  - id: mock
    type: mock
    enabled: true
  - id: deepseek
    type: deepseek
    enabled: true
    config:
      apiKey: ${DEEPSEEK_API_KEY}
```

---

## 三、简化配置方案

### 3.1 最小化配置

```yaml
# 最小配置 - 仅需配置 API Key
ooder:
  llm:
    provider: deepseek
    api-key: ${DEEPSEEK_API_KEY}
```

### 3.2 推荐配置结构

```yaml
# llm-config.yaml - 简化版
ooder:
  llm:
    # 当前使用的 Provider
    provider: deepseek
    model: deepseek-chat
    
    # Provider 配置 (按需)
    providers:
      deepseek:
        api-key: ${DEEPSEEK_API_KEY}
        base-url: https://api.deepseek.com/v1
      
      baidu:
        api-key: ${BAIDU_API_KEY}
        secret-key: ${BAIDU_SECRET_KEY}
    
    # 全局参数 (可选)
    defaults:
      temperature: 0.7
      max-tokens: 4096
```

### 3.3 环境变量优先

```bash
# 环境变量配置 - 最高优先级
export OODER_LLM_PROVIDER=deepseek
export OODER_LLM_MODEL=deepseek-chat
export OODER_LLM_API_KEY=sk-xxx
```

---

## 四、实现方案

### 4.1 多级配置加载器

```java
package net.ooder.scene.llm.config;

/**
 * 多级配置加载器
 */
public class LayeredConfigLoader {
    
    private static final String[] CONFIG_PATHS = {
        "classpath:llm-config-default.yaml",     // Level 1
        "config/env/${env}/llm-config.yaml",     // Level 2
        "config/app/llm-config.yaml",            // Level 3
        "${user.home}/.ooder/llm-config.yaml"    // Level 4
    };
    
    public LlmConfigProperties load() {
        LlmConfigProperties config = new LlmConfigProperties();
        
        for (String path : CONFIG_PATHS) {
            LlmConfigProperties layer = loadLayer(path);
            if (layer != null) {
                config = deepMerge(config, layer);
            }
        }
        
        // 环境变量覆盖
        applyEnvironmentVariables(config);
        
        return config;
    }
    
    private LlmConfigProperties deepMerge(LlmConfigProperties base, 
                                           LlmConfigProperties overlay) {
        // 深度合并逻辑
    }
}
```

### 4.2 配置热更新

```java
/**
 * 配置热更新服务
 */
public interface ConfigHotReloadService {
    
    /**
     * 监听配置文件变化
     */
    void watch(String configPath, Consumer<LlmConfigProperties> callback);
    
    /**
     * 重新加载配置
     */
    LlmConfigProperties reload();
}
```

---

## 五、方案对比

### 5.1 原方案 vs 多级加载方案

| 对比项 | 原方案 | 多级加载方案 |
|--------|--------|--------------|
| 配置文件数量 | 3个 | 10+个 (分层) |
| 单文件复杂度 | 高 | 低 |
| 环境切换 | 手动修改 | 自动切换 |
| 配置覆盖 | 不支持 | 支持 |
| 安全性 | 低 | 高 (敏感信息隔离) |
| 维护成本 | 高 | 中 |

### 5.2 推荐方案

| 场景 | 推荐方案 |
|------|----------|
| 简单应用 | 最小配置 + 环境变量 |
| 中型应用 | 简化配置 + 环境配置 |
| 大型应用 | 多级加载 + 配置中心 |

---

## 六、实施建议

### 6.1 短期 (P0)

1. **简化配置格式**
   - 减少必填项
   - 支持环境变量注入
   - 提供默认值

2. **支持最小配置**
   - 仅需配置 Provider + API Key

### 6.2 中期 (P1)

1. **实现多级加载**
   - 系统默认 → 环境配置 → 应用配置 → 用户配置

2. **配置合并策略**
   - 深度合并
   - 环境变量优先

### 6.3 长期 (P2)

1. **配置中心集成**
   - 支持 Nacos/Apollo
   - 配置热更新

2. **配置管理 UI**
   - 可视化配置编辑
   - 配置版本管理

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: SE Team
