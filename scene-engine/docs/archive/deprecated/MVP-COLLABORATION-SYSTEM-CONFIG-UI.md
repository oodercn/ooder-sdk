# MVP 协作需求: 系统配置管理界面

## 1. 协作概述

**发起方**: SE 团队  
**接收方**: MVP 团队  
**主题**: 系统配置管理界面 - 17项系统默认配置管理  
**优先级**: P1  
**日期**: 2026-03-20  
**状态**: 🔴 待确认

---

## 2. 背景说明

### 2.1 当前问题

1. **无系统配置管理界面**：用户无法直观查看和修改系统配置
2. **17项系统 Skill 配置分散**：配置分散在多个文件中，难以统一管理
3. **配置变更不透明**：用户不清楚哪些配置是系统默认，哪些是自定义
4. **无配置历史追溯**：配置变更无历史记录

### 2.2 目标需求

1. **系统配置管理界面**：提供统一的系统配置管理入口
2. **17项系统 Skill 配置展示**：展示所有系统 Skill 的配置状态
3. **配置编辑功能**：支持在线编辑配置
4. **配置历史追溯**：记录配置变更历史

---

## 3. 功能需求

### 3.1 系统配置管理界面

**页面路径**: `/settings/system`

**界面布局**:

```
┌─────────────────────────────────────────────────────────────┐
│  系统配置管理                                    [保存] [重置] │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 系统状态                                                 ││
│  │ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐    ││
│  │ │ SQLite   │ │ 向量嵌入  │ │ 日志服务  │ │ 指标服务  │    ││
│  │ │ ✅ 运行中 │ │ ✅ 运行中 │ │ ✅ 运行中 │ │ ✅ 运行中 │    ││
│  │ └──────────┘ └──────────┘ └──────────┘ └──────────┘    ││
│  └─────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 系统服务配置 (17项)                          [筛选] [搜索]││
│  ├─────────────────────────────────────────────────────────┤│
│  │ 🔧 数据库服务                                            ││
│  │ ├── skill-db-sqlite      ✅ 运行中    [配置] [停止]      ││
│  │ ├── skill-db-mysql       ⏸️ 未启动    [配置] [启动]      ││
│  │ │                                                        ││
│  │ 🤖 LLM 服务                                              ││
│  │ ├── skill-llm-openai     ⏸️ 未启动    [配置] [启动]      ││
│  │ ├── skill-llm-deepseek   ⏸️ 未启动    [配置] [启动]      ││
│  │ ├── skill-llm-anthropic  ⏸️ 未启动    [配置] [启动]      ││
│  │ ├── skill-llm-azure      ⏸️ 未启动    [配置] [启动]      ││
│  │ │                                                        ││
│  │ 🔍 向量服务                                              ││
│  │ ├── skill-embedding-local ✅ 运行中   [配置] [停止]      ││
│  │ ├── skill-rerank         ⏸️ 未启动    [配置] [启动]      ││
│  │ ├── skill-vector-store   ⏸️ 未启动    [配置] [启动]      ││
│  │ │                                                        ││
│  │ 📦 存储服务                                              ││
│  │ ├── skill-cache-redis    ⏸️ 未启动    [配置] [启动]      ││
│  │ ├── skill-storage-oss    ⏸️ 未启动    [配置] [启动]      ││
│  │ ├── skill-storage-minio  ⏸️ 未启动    [配置] [启动]      ││
│  │ │                                                        ││
│  │ 📊 监控服务                                              ││
│  │ ├── skill-logging        ✅ 运行中    [配置] [停止]      ││
│  │ ├── skill-metrics        ✅ 运行中    [配置] [停止]      ││
│  │ ├── skill-tracing        ⏸️ 未启动    [配置] [启动]      ││
│  │ │                                                        ││
│  │ 🔔 其他服务                                              ││
│  │ ├── skill-knowledge-base ⏸️ 未启动    [配置] [启动]      ││
│  │ ├── skill-notification   ⏸️ 未启动    [配置] [启动]      ││
│  └─────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 配置历史                                    [查看全部]    ││
│  ├─────────────────────────────────────────────────────────┤│
│  │ 2026-03-20 10:30  修改 skill-embedding-local 配置       ││
│  │ 2026-03-20 09:15  启动 skill-db-sqlite                   ││
│  │ 2026-03-19 16:45  修改 skill-logging 日志级别           ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

### 3.2 Skill 配置详情弹窗

**界面布局**:

```
┌─────────────────────────────────────────────────────────────┐
│  skill-embedding-local 配置                       [关闭]    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  基本信息                                                   │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ Skill ID:    skill-embedding-local                      ││
│  │ 名称:        本地向量嵌入服务                            ││
│  │ 状态:        ✅ 运行中                                   ││
│  │ 启动类型:    自动启动                                    ││
│  └─────────────────────────────────────────────────────────┘│
│                                                             │
│  配置参数                                                   │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 模型路径    [./models/embedding          ] [浏览]       ││
│  │ 模型类型    [bge-small-zh        ▼]                     ││
│  │ 向量维度    [512        ]                               ││
│  │ 批处理大小  [32         ]                               ││
│  │ 最大序列长度 [512       ]                               ││
│  │ 归一化      [✓] 启用                                    ││
│  └─────────────────────────────────────────────────────────┘│
│                                                             │
│  运行状态                                                   │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 已加载模型:  bge-small-zh-v1.0                          ││
│  │ 内存占用:    256 MB                                     ││
│  │ 请求数量:    1,234                                      ││
│  │ 平均延迟:    15 ms                                      ││
│  └─────────────────────────────────────────────────────────┘│
│                                                             │
│                              [保存配置] [重启服务] [取消]    │
└─────────────────────────────────────────────────────────────┘
```

---

### 3.3 API 接口需求

#### 3.3.1 获取系统配置列表

**请求**:
```
GET /api/system/config/skills
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "skills": [
      {
        "skillId": "skill-db-sqlite",
        "name": "SQLite 数据库",
        "category": "database",
        "status": "running",
        "autoStart": true,
        "enabled": true,
        "config": {
          "url": "jdbc:sqlite:./data/system.db",
          "poolSize": 5
        },
        "metrics": {
          "connections": 3,
          "queries": 1234,
          "avgLatency": 5
        }
      },
      {
        "skillId": "skill-embedding-local",
        "name": "本地向量嵌入",
        "category": "embedding",
        "status": "running",
        "autoStart": true,
        "enabled": true,
        "config": {
          "modelPath": "./models/embedding",
          "modelType": "bge-small-zh",
          "dimension": 512
        },
        "metrics": {
          "memoryUsage": 256,
          "requests": 567,
          "avgLatency": 15
        }
      }
    ],
    "categories": [
      {"id": "database", "name": "数据库服务", "count": 2},
      {"id": "llm", "name": "LLM 服务", "count": 4},
      {"id": "embedding", "name": "向量服务", "count": 3},
      {"id": "storage", "name": "存储服务", "count": 3},
      {"id": "monitoring", "name": "监控服务", "count": 3},
      {"id": "other", "name": "其他服务", "count": 2}
    ]
  }
}
```

#### 3.3.2 获取 Skill 配置详情

**请求**:
```
GET /api/system/config/skills/{skillId}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "skillId": "skill-embedding-local",
    "name": "本地向量嵌入服务",
    "description": "提供本地向量嵌入能力，支持中英文文本向量化",
    "category": "embedding",
    "status": "running",
    "autoStart": true,
    "enabled": true,
    "config": {
      "modelPath": "./models/embedding",
      "modelType": "bge-small-zh",
      "dimension": 512,
      "batchSize": 32,
      "maxSequenceLength": 512,
      "normalize": true
    },
    "configSchema": {
      "modelPath": {"type": "string", "required": true, "label": "模型路径"},
      "modelType": {"type": "enum", "options": ["bge-small-zh", "bge-base-zh", "bge-large-zh"], "label": "模型类型"},
      "dimension": {"type": "integer", "min": 128, "max": 2048, "label": "向量维度"},
      "batchSize": {"type": "integer", "min": 1, "max": 128, "label": "批处理大小"},
      "normalize": {"type": "boolean", "label": "归一化"}
    },
    "runtime": {
      "loadedModel": "bge-small-zh-v1.0",
      "memoryUsage": 256,
      "requestCount": 1234,
      "avgLatency": 15,
      "startTime": "2026-03-20T08:00:00Z"
    }
  }
}
```

#### 3.3.3 更新 Skill 配置

**请求**:
```
PUT /api/system/config/skills/{skillId}
Content-Type: application/json

{
  "config": {
    "modelPath": "./models/embedding",
    "modelType": "bge-base-zh",
    "dimension": 768,
    "batchSize": 64
  },
  "autoStart": true,
  "enabled": true,
  "restart": true
}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "skillId": "skill-embedding-local",
    "status": "restarting",
    "message": "配置已保存，服务正在重启"
  }
}
```

#### 3.3.4 启动/停止 Skill

**请求**:
```
POST /api/system/config/skills/{skillId}/start
POST /api/system/config/skills/{skillId}/stop
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "skillId": "skill-embedding-local",
    "status": "running",
    "message": "服务已启动"
  }
}
```

#### 3.3.5 获取配置历史

**请求**:
```
GET /api/system/config/history?skillId={skillId}&limit=20
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "history": [
      {
        "id": "hist-001",
        "skillId": "skill-embedding-local",
        "action": "update_config",
        "changes": {
          "modelType": {"old": "bge-small-zh", "new": "bge-base-zh"},
          "dimension": {"old": 512, "new": 768}
        },
        "operator": "admin",
        "timestamp": "2026-03-20T10:30:00Z"
      },
      {
        "id": "hist-002",
        "skillId": "skill-db-sqlite",
        "action": "start",
        "changes": {},
        "operator": "system",
        "timestamp": "2026-03-20T09:15:00Z"
      }
    ],
    "total": 45
  }
}
```

---

## 4. SE SDK 需要提供的接口

### 4.1 SystemConfigService

```java
package net.ooder.scene.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 系统配置服务
 */
public interface SystemConfigService {
    
    /**
     * 获取所有系统 Skill 配置
     */
    CompletableFuture<List<SystemSkillConfig>> listSystemSkills();
    
    /**
     * 获取指定 Skill 配置
     */
    CompletableFuture<SystemSkillConfig> getSkillConfig(String skillId);
    
    /**
     * 更新 Skill 配置
     */
    CompletableFuture<Void> updateSkillConfig(String skillId, Map<String, Object> config, boolean restart);
    
    /**
     * 启动 Skill
     */
    CompletableFuture<Void> startSkill(String skillId);
    
    /**
     * 停止 Skill
     */
    CompletableFuture<Void> stopSkill(String skillId);
    
    /**
     * 获取配置历史
     */
    CompletableFuture<List<ConfigHistory>> getConfigHistory(String skillId, int limit);
    
    /**
     * 获取 Skill 运行时状态
     */
    CompletableFuture<SkillRuntimeStatus> getSkillRuntimeStatus(String skillId);
}
```

### 4.2 数据模型

```java
/**
 * 系统 Skill 配置
 */
public class SystemSkillConfig {
    private String skillId;
    private String name;
    private String description;
    private String category;
    private String status;
    private boolean autoStart;
    private boolean enabled;
    private Map<String, Object> config;
    private Map<String, Object> configSchema;
}

/**
 * Skill 运行时状态
 */
public class SkillRuntimeStatus {
    private String skillId;
    private String status;
    private long startTime;
    private long uptime;
    private Map<String, Object> metrics;
}

/**
 * 配置历史
 */
public class ConfigHistory {
    private String id;
    private String skillId;
    private String action;
    private Map<String, Map<String, Object>> changes;
    private String operator;
    private long timestamp;
}
```

---

## 5. 界面交互流程

### 5.1 查看配置

```
用户访问 /settings/system
    │
    ├── 页面加载
    │   ├── 调用 GET /api/system/config/skills
    │   └── 展示系统 Skill 列表
    │
    └── 点击 Skill
        ├── 调用 GET /api/system/config/skills/{skillId}
        └── 展示配置详情弹窗
```

### 5.2 修改配置

```
用户点击 [配置] 按钮
    │
    ├── 展示配置详情弹窗
    │
    ├── 用户修改配置
    │
    ├── 点击 [保存配置]
    │   ├── 调用 PUT /api/system/config/skills/{skillId}
    │   ├── 显示保存成功提示
    │   └── 刷新 Skill 列表
    │
    └── 如果勾选 [重启服务]
        ├── 调用 POST /api/system/config/skills/{skillId}/stop
        ├── 调用 POST /api/system/config/skills/{skillId}/start
        └── 更新状态显示
```

---

## 6. 时间计划

| 阶段 | 任务 | 预计时间 | 状态 |
|------|------|----------|------|
| Phase 1 | SE SDK 实现 SystemConfigService | 2-3 天 | 待开始 |
| Phase 2 | MVP 实现系统配置管理界面 | 3-5 天 | 待开始 |
| Phase 3 | 联调测试 | 1-2 天 | 待开始 |
| **总计** | - | **6-10 天** | - |

---

## 7. 验收标准

### 7.1 功能验收

- [ ] 系统配置管理界面可访问
- [ ] 17项系统 Skill 配置可展示
- [ ] 配置修改功能正常
- [ ] Skill 启动/停止功能正常
- [ ] 配置历史可追溯

### 7.2 UI 验收

- [ ] 界面布局清晰
- [ ] 状态显示准确
- [ ] 操作反馈及时

---

## 8. 状态

- [x] 需求文档完成
- [ ] MVP 团队确认
- [ ] SE 团队实现接口
- [ ] 开始实施

---

**文档版本**: 1.0  
**创建日期**: 2026-03-20  
**SE 团队**: SceneEngine Team
