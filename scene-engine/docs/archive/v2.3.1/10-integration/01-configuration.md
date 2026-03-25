# 配置参考

## 完整配置示例

```yaml
se:
  # 对话服务配置
  conversation:
    enabled: true
    storage:
      type: file              # file, memory, database
      path: ${user.home}/.ooder/data/conversations
    auto-learn: true          # 自动学习
    max-history: 100          # 最大历史消息数
    audit:
      enabled: true
      include-tool-calls: true
    knowledge:
      auto-update: true
      min-content-length: 50

  # LLM配置
  llm:
    provider: openai
    api-key: ${OPENAI_API_KEY}
    model: gpt-4
    temperature: 0.7
    max-tokens: 2000

  # 知识库配置
  knowledge:
    vector-store:
      type: memory            # memory, milvus, elasticsearch
      dimension: 1536
    chunk:
      size: 500
      overlap: 50

  # RAG配置
  rag:
    enabled: true
    top-k: 5
    threshold: 0.7
    enable-rerank: true

  # 工具配置
  tool:
    enabled: true
    timeout: 30000
    max-retries: 3
```

## 配置项说明

### 对话服务 (se.conversation)

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enabled` | boolean | true | 启用对话服务 |
| `storage.type` | string | file | 存储类型 |
| `storage.path` | string | - | 文件存储路径 |
| `auto-learn` | boolean | false | 自动学习 |
| `max-history` | int | 100 | 最大历史消息数 |
| `audit.enabled` | boolean | true | 启用审计 |
| `knowledge.auto-update` | boolean | false | 自动更新知识库 |

### LLM (se.llm)

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `provider` | string | openai | 提供商 |
| `api-key` | string | - | API密钥 |
| `model` | string | gpt-4 | 模型 |
| `temperature` | float | 0.7 | 温度 |
| `max-tokens` | int | 2000 | 最大token数 |

### 知识库 (se.knowledge)

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `vector-store.type` | string | memory | 向量存储类型 |
| `vector-store.dimension` | int | 1536 | 向量维度 |
| `chunk.size` | int | 500 | 分块大小 |
| `chunk.overlap` | int | 50 | 分块重叠 |

### RAG (se.rag)

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enabled` | boolean | true | 启用RAG |
| `top-k` | int | 5 | 返回结果数 |
| `threshold` | float | 0.7 | 相似度阈值 |
| `enable-rerank` | boolean | true | 启用重排序 |
