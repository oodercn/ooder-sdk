# JavaScript SDK 开发配置说明

## 概述

本文档说明前端 JavaScript 应用如何集成 ooder 动态 LLM 驱动方案，实现函数到 Capability 的自动映射执行。

## 架构设计

```
┌─────────────────────────────────────────────────────────────────────┐
│                        前端 JavaScript                              │
├─────────────────────────────────────────────────────────────────────┤
│  SkillLlmDriver SDK                                                │
│  - loadSkillConfig(skillId)      加载 Skill 配置                   │
│  - getSystemPrompt(skillId)      获取系统提示词                     │
│  - getFunctions(skillId)         获取函数定义                       │
│  - executeFunction(skillId, fn, args)  执行函数（自动映射 Capability）│
├─────────────────────────────────────────────────────────────────────┤
│                          REST API 调用                              │
│  POST /api/skill/{skillId}/llm/config    获取 LLM 配置             │
│  POST /api/skill/{skillId}/capability    调用 Capability           │
│  POST /api/llm/chat                      LLM 对话                  │
└─────────────────────────────────────────────────────────────────────┘
```

## 一、Skill 元数据配置

### 1.1 skill.json 配置结构

```json
{
  "skillId": "recruitment-skill",
  "name": "招聘助手",
  "version": "1.0.0",
  "description": "帮助HR筛选简历和安排面试",
  "metadata": {
    "llmConfig": {
      "systemPrompt": "你是招聘场景的智能助手，帮助HR筛选简历和安排面试。请用简洁专业的中文回复。",
      "temperature": 0.7,
      "maxTokens": 2000,
      "defaultModel": "deepseek-chat",
      "defaultProvider": "deepseek",
      "functions": [
        {
          "name": "scan_resume",
          "description": "扫描并解析简历",
          "parameters": {
            "resumeId": {
              "type": "string",
              "description": "简历ID"
            }
          },
          "required": ["resumeId"],
          "capability": "resume_scan"
        },
        {
          "name": "schedule_interview",
          "description": "安排面试",
          "parameters": {
            "candidateId": {
              "type": "string",
              "description": "候选人ID"
            },
            "interviewerId": {
              "type": "string",
              "description": "面试官ID"
            },
            "time": {
              "type": "string",
              "description": "面试时间"
            }
          },
          "required": ["candidateId", "time"],
          "capability": "schedule_interview"
        },
        {
          "name": "query_candidates",
          "description": "查询候选人列表",
          "parameters": {
            "keyword": {
              "type": "string",
              "description": "搜索关键词"
            },
            "status": {
              "type": "string",
              "enum": ["pending", "interviewed", "hired", "rejected"],
              "description": "候选人状态"
            },
            "limit": {
              "type": "integer",
              "description": "返回数量限制"
            }
          },
          "capability": "query_candidates"
        }
      ]
    }
  }
}
```

### 1.2 配置字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `systemPrompt` | string | 否 | 系统提示词，定义 AI 助手角色和行为 |
| `temperature` | number | 否 | 温度参数，0-1，控制回复随机性 |
| `maxTokens` | integer | 否 | 最大 Token 数 |
| `defaultModel` | string | 否 | 默认模型 |
| `defaultProvider` | string | 否 | 默认 Provider |
| `functions` | array | 否 | 函数定义列表 |

### 1.3 函数定义字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | string | 是 | 函数名称，LLM 调用时使用 |
| `description` | string | 是 | 函数描述，LLM 理解函数用途 |
| `parameters` | object | 是 | 参数定义，JSON Schema 格式 |
| `required` | array | 否 | 必填参数列表 |
| `capability` | string | 是 | 映射的 Capability ID |

## 二、JavaScript SDK 集成

### 2.1 SDK 基础类

```javascript
/**
 * Ooder Skill LLM Driver SDK
 * 
 * 动态加载 Skill 配置，实现函数到 Capability 的自动映射
 */
class SkillLlmDriver {
  
  constructor(config = {}) {
    this.baseUrl = config.baseUrl || '';
    this.apiPrefix = config.apiPrefix || '/api';
    this.configs = new Map();
    this.functionMappings = new Map();
  }

  /**
   * 加载 Skill LLM 配置
   * @param {string} skillId - Skill ID
   * @returns {Promise<Object>} LLM 配置
   */
  async loadSkillConfig(skillId) {
    if (this.configs.has(skillId)) {
      return this.configs.get(skillId);
    }

    const response = await fetch(`${this.baseUrl}${this.apiPrefix}/skill/${skillId}/llm/config`);
    if (!response.ok) {
      throw new Error(`Failed to load skill config: ${response.status}`);
    }

    const result = await response.json();
    const config = result.data || result;

    this.configs.set(skillId, config);
    
    // 构建函数映射
    if (config.functions) {
      config.functions.forEach(fn => {
        const key = `${skillId}:${fn.name}`;
        this.functionMappings.set(key, {
          skillId,
          functionName: fn.name,
          capabilityId: fn.capability || fn.name,
          parameters: fn.parameters,
          required: fn.required
        });
      });
    }

    return config;
  }

  /**
   * 获取系统提示词
   * @param {string} skillId - Skill ID
   * @returns {string|null} 系统提示词
   */
  getSystemPrompt(skillId) {
    const config = this.configs.get(skillId);
    return config?.systemPrompt || null;
  }

  /**
   * 获取函数定义列表（用于 LLM API）
   * @param {string} skillId - Skill ID
   * @returns {Array} 函数定义
   */
  getFunctions(skillId) {
    const config = this.configs.get(skillId);
    if (!config?.functions) return [];

    return config.functions.map(fn => ({
      type: 'function',
      function: {
        name: fn.name,
        description: fn.description,
        parameters: {
          type: 'object',
          properties: fn.parameters,
          required: fn.required || []
        }
      }
    }));
  }

  /**
   * 执行函数（自动映射到 Capability）
   * @param {string} skillId - Skill ID
   * @param {string} functionName - 函数名称
   * @param {Object} args - 函数参数
   * @param {Object} context - 执行上下文
   * @returns {Promise<Object>} 执行结果
   */
  async executeFunction(skillId, functionName, args, context = {}) {
    const key = `${skillId}:${functionName}`;
    const mapping = this.functionMappings.get(key);

    if (!mapping) {
      throw new Error(`Function mapping not found: ${functionName}`);
    }

    // 调用 Capability
    return await this.invokeCapability(
      skillId,
      mapping.capabilityId,
      args,
      context
    );
  }

  /**
   * 调用 Capability
   * @param {string} skillId - Skill ID
   * @param {string} capabilityId - Capability ID
   * @param {Object} params - 参数
   * @param {Object} context - 上下文
   * @returns {Promise<Object>} 执行结果
   */
  async invokeCapability(skillId, capabilityId, params, context = {}) {
    const response = await fetch(`${this.baseUrl}${this.apiPrefix}/skill/${skillId}/capability`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...this.getAuthHeaders()
      },
      body: JSON.stringify({
        capability: capabilityId,
        params: params,
        context: context
      })
    });

    if (!response.ok) {
      throw new Error(`Capability invocation failed: ${response.status}`);
    }

    const result = await response.json();
    return result.data || result;
  }

  /**
   * 获取认证头
   */
  getAuthHeaders() {
    const token = localStorage.getItem('auth_token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }

  /**
   * 清除缓存
   */
  clearCache() {
    this.configs.clear();
    this.functionMappings.clear();
  }
}
```

### 2.2 LLM 聊天客户端

```javascript
/**
 * LLM 聊天客户端
 * 
 * 支持动态系统提示词和 Function Calling
 */
class LlmChatClient {

  constructor(driver, config = {}) {
    this.driver = driver;
    this.baseUrl = config.baseUrl || '';
    this.apiPrefix = config.apiPrefix || '/api';
    this.conversations = new Map();
  }

  /**
   * 发送聊天消息
   * @param {Object} request - 聊天请求
   * @returns {Promise<Object>} 聊天响应
   */
  async chat(request) {
    const { skillId, message, conversationId, history } = request;

    // 加载 Skill 配置
    const config = await this.driver.loadSkillConfig(skillId);

    // 构建消息
    const messages = this.buildMessages(request, config);

    // 构建选项
    const options = this.buildOptions(request, config);

    // 获取函数定义
    const tools = this.driver.getFunctions(skillId);

    // 调用 LLM API
    const response = await fetch(`${this.baseUrl}${this.apiPrefix}/llm/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...this.driver.getAuthHeaders()
      },
      body: JSON.stringify({
        model: config.defaultModel,
        provider: config.defaultProvider,
        messages: messages,
        tools: tools.length > 0 ? tools : undefined,
        options: options
      })
    });

    if (!response.ok) {
      throw new Error(`Chat failed: ${response.status}`);
    }

    const result = await response.json();
    const chatResult = result.data || result;

    // 处理 Function Calling
    if (chatResult.tool_calls && chatResult.tool_calls.length > 0) {
      return await this.handleToolCalls(skillId, chatResult, messages);
    }

    // 保存会话历史
    if (conversationId) {
      this.addToHistory(conversationId, 'user', message);
      this.addToHistory(conversationId, 'assistant', chatResult.content);
    }

    return chatResult;
  }

  /**
   * 构建消息列表
   */
  buildMessages(request, config) {
    const messages = [];

    // 添加系统提示词
    if (config.systemPrompt) {
      messages.push({
        role: 'system',
        content: config.systemPrompt
      });
    }

    // 添加历史消息
    if (request.history) {
      messages.push(...request.history);
    }

    // 添加会话历史
    if (request.conversationId) {
      const history = this.conversations.get(request.conversationId) || [];
      messages.push(...history);
    }

    // 添加用户消息
    messages.push({
      role: 'user',
      content: request.message
    });

    return messages;
  }

  /**
   * 构建选项
   */
  buildOptions(request, config) {
    return {
      temperature: request.temperature ?? config.temperature,
      max_tokens: request.maxTokens ?? config.maxTokens
    };
  }

  /**
   * 处理 Tool Calls
   */
  async handleToolCalls(skillId, chatResult, originalMessages) {
    const actions = [];
    const messages = [...originalMessages];

    // 添加助手消息
    messages.push({
      role: 'assistant',
      content: chatResult.content || null,
      tool_calls: chatResult.tool_calls
    });

    // 执行每个函数调用
    for (const toolCall of chatResult.tool_calls) {
      const functionName = toolCall.function.name;
      const args = JSON.parse(toolCall.function.arguments);

      try {
        // 执行函数（自动映射到 Capability）
        const result = await this.driver.executeFunction(skillId, functionName, args);

        // 添加工具结果
        messages.push({
          role: 'tool',
          tool_call_id: toolCall.id,
          content: JSON.stringify(result)
        });

        // 记录动作
        if (result.action) {
          actions.push(result);
        }
      } catch (error) {
        messages.push({
          role: 'tool',
          tool_call_id: toolCall.id,
          content: JSON.stringify({ error: error.message })
        });
      }
    }

    // 继续对话获取最终响应
    const finalResponse = await fetch(`${this.baseUrl}${this.apiPrefix}/llm/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...this.driver.getAuthHeaders()
      },
      body: JSON.stringify({
        messages: messages
      })
    });

    const finalResult = await finalResponse.json();
    const finalChatResult = finalResult.data || finalResult;

    return {
      ...finalChatResult,
      actions: actions.length > 0 ? actions : undefined
    };
  }

  /**
   * 添加到历史记录
   */
  addToHistory(conversationId, role, content) {
    if (!this.conversations.has(conversationId)) {
      this.conversations.set(conversationId, []);
    }
    this.conversations.get(conversationId).push({ role, content });
  }

  /**
   * 清除会话历史
   */
  clearHistory(conversationId) {
    if (conversationId) {
      this.conversations.delete(conversationId);
    } else {
      this.conversations.clear();
    }
  }
}
```

### 2.3 完整使用示例

```javascript
// 初始化
const driver = new SkillLlmDriver({
  baseUrl: 'https://api.example.com',
  apiPrefix: '/api'
});

const chatClient = new LlmChatClient(driver, {
  baseUrl: 'https://api.example.com',
  apiPrefix: '/api'
});

// 加载 Skill 配置
await driver.loadSkillConfig('recruitment-skill');

// 查看配置
console.log('系统提示词:', driver.getSystemPrompt('recruitment-skill'));
console.log('函数定义:', driver.getFunctions('recruitment-skill'));

// 发送聊天
const response = await chatClient.chat({
  skillId: 'recruitment-skill',
  message: '请帮我扫描简历 RESUME-001',
  conversationId: 'conv-123'
});

console.log('回复:', response.content);
if (response.actions) {
  console.log('执行的动作:', response.actions);
}
```

## 三、后端 API 要求

应用层需要实现以下 REST API：

### 3.1 获取 Skill LLM 配置

```
GET /api/skill/{skillId}/llm/config

Response:
{
  "code": 0,
  "data": {
    "systemPrompt": "你是招聘场景的智能助手...",
    "temperature": 0.7,
    "maxTokens": 2000,
    "defaultModel": "deepseek-chat",
    "defaultProvider": "deepseek",
    "functions": [...]
  }
}
```

### 3.2 调用 Capability

```
POST /api/skill/{skillId}/capability

Request:
{
  "capability": "resume_scan",
  "params": {
    "resumeId": "RESUME-001"
  },
  "context": {
    "userId": "user123"
  }
}

Response:
{
  "code": 0,
  "data": {
    "success": true,
    "action": "scanResume",
    "resumeId": "RESUME-001"
  }
}
```

### 3.3 LLM 聊天

```
POST /api/llm/chat

Request:
{
  "model": "deepseek-chat",
  "provider": "deepseek",
  "messages": [
    {"role": "system", "content": "..."},
    {"role": "user", "content": "..."}
  ],
  "tools": [...],
  "options": {
    "temperature": 0.7,
    "max_tokens": 2000
  }
}

Response:
{
  "code": 0,
  "data": {
    "content": "好的，我来帮您处理...",
    "tool_calls": [...]
  }
}
```

## 四、函数到 Capability 映射规则

### 4.1 自动映射

当函数定义中指定了 `capability` 字段时，SDK 自动将函数调用映射到对应的 Capability：

```javascript
// 函数定义
{
  "name": "scan_resume",
  "capability": "resume_scan",  // 映射到此 Capability
  "parameters": {...}
}

// 调用流程
// 1. LLM 返回 function_call: { name: "scan_resume", arguments: {...} }
// 2. SDK 查找映射: scan_resume -> resume_scan
// 3. SDK 调用: POST /api/skill/{skillId}/capability { capability: "resume_scan", params: {...} }
```

### 4.2 默认映射

如果未指定 `capability` 字段，默认使用函数名作为 Capability ID：

```javascript
{
  "name": "query_candidates",
  // 未指定 capability
  "parameters": {...}
}

// 默认映射: query_candidates -> query_candidates
```

### 4.3 参数透传

函数参数直接透传给 Capability：

```javascript
// LLM 调用函数
executeFunction("recruitment-skill", "scan_resume", { resumeId: "RESUME-001" })

// 映射到 Capability
POST /api/skill/recruitment-skill/capability
{
  "capability": "resume_scan",
  "params": { "resumeId": "RESUME-001" }
}
```

## 五、最佳实践

### 5.1 配置缓存

```javascript
// 预加载常用 Skill 配置
async function initApp() {
  const skills = ['recruitment-skill', 'calendar-skill', 'email-skill'];
  await Promise.all(skills.map(id => driver.loadSkillConfig(id)));
}
```

### 5.2 错误处理

```javascript
async function safeChat(request) {
  try {
    return await chatClient.chat(request);
  } catch (error) {
    console.error('Chat error:', error);
    return {
      error: true,
      message: error.message
    };
  }
}
```

### 5.3 会话管理

```javascript
// 创建新会话
const conversationId = `conv-${Date.now()}`;

// 发送消息
await chatClient.chat({
  skillId: 'recruitment-skill',
  message: '你好',
  conversationId: conversationId
});

// 继续对话
await chatClient.chat({
  skillId: 'recruitment-skill',
  message: '请帮我扫描简历',
  conversationId: conversationId
});

// 清除会话
chatClient.clearHistory(conversationId);
```

## 六、TypeScript 类型定义

```typescript
interface SkillLlmConfig {
  systemPrompt?: string;
  temperature?: number;
  maxTokens?: number;
  defaultModel?: string;
  defaultProvider?: string;
  functions?: FunctionDefinition[];
}

interface FunctionDefinition {
  name: string;
  description: string;
  parameters: Record<string, ParameterDefinition>;
  required?: string[];
  capability?: string;
}

interface ParameterDefinition {
  type: 'string' | 'integer' | 'number' | 'boolean' | 'array' | 'object';
  description?: string;
  enum?: string[];
}

interface ChatRequest {
  skillId: string;
  message: string;
  conversationId?: string;
  history?: Message[];
  temperature?: number;
  maxTokens?: number;
}

interface ChatResponse {
  content: string;
  model?: string;
  provider?: string;
  tool_calls?: ToolCall[];
  actions?: Record<string, any>[];
  error?: boolean;
  errorMessage?: string;
}

interface ToolCall {
  id: string;
  type: 'function';
  function: {
    name: string;
    arguments: string;
  };
}
```

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-09
