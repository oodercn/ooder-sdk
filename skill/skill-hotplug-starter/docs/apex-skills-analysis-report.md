# E:\apex\os\skills 工程分析报告

**报告日期**: 2026-04-01  
**报告团队**: ooder skills 团队  
**目标团队**: apex 团队  

---

## 一、执行摘要

本报告对 `E:\apex\os\skills` 工程进行了全面分析，发现以下主要问题：

| 问题类型 | 严重程度 | 问题数 | 影响 |
|---------|---------|--------|------|
| API 与 skill.yaml 不匹配 | 🔴 严重 | 15+ | 动态路由注册失败 |
| 功能重复冗余 | 🔴 严重 | 5 | 维护成本高、调用混乱 |
| API 声明缺失 | 🔴 严重 | 30+ | 文档不完整、无法自动发现 |
| 依赖配置问题 | 🟡 中等 | 3 | 启动失败风险 |
| 配置格式不一致 | 🟡 中等 | 10+ | 解析失败风险 |

---

## 二、详细分析

### 2.1 API 与 skill.yaml 匹配度问题 🔴

#### 问题 1: skill-agent 模块路径不一致

**skill.yaml 声明的路径** vs **实际 Controller 实现的路径**:

| Controller | skill.yaml 声明 | 实际实现 | 状态 |
|------------|----------------|---------|------|
| AgentController | `/api/agent/*` | `/api/agent/*` | ✅ 匹配 |
| AgentChatController | `/api/agent/chat/*` | `/api/v1/scene-groups/{sceneGroupId}/chat/*` | ❌ 完全不同 |
| AgentSessionController | `/api/agent/session/*` | `/api/v1/agents/*` | ❌ 不同前缀 |
| AgentMessageController | `/api/agent/message/*` | `/api/v1/agents/*` | ❌ 不同前缀 |

**影响**: 使用 `skill-hotplug-starter` 动态注册路由时，API 路径与声明不匹配，导致 404 错误。

#### 问题 2: 声明但实际不存在的 API

skill.yaml 中声明了以下 API，但 Controller 中不存在:

```yaml
# 以下 API 在 skill.yaml 中声明，但实际不存在
- path: /api/agent/chat              # AgentChatController 使用不同路径
- path: /api/agent/chat/stream       # 实际路径: /api/v1/scene-groups/{sceneGroupId}/chat/messages
- path: /api/agent/session/login     # 实际路径: /api/v1/agents/login
- path: /api/agent/message/send      # 实际路径: /api/v1/agents/{agentId}/messages
```

#### 问题 3: 已实现但未声明的 API (30+ 个)

AgentController 实现了以下 API，但 skill.yaml 中未声明:

| API | 方法 | 功能 |
|-----|------|------|
| `/api/agent/search` | GET | 搜索 Agent |
| `/api/agent/type/{agentType}` | GET | 按类型查询 |
| `/api/agent/status/{status}` | GET | 按状态查询 |
| `/api/agent/cluster/{clusterId}` | GET | 按集群查询 |
| `/api/agent/{agentId}/binding-count` | GET | 获取绑定数 |
| `/api/agent/cluster/{clusterId}/stats` | GET | 集群统计 |
| `/api/agent/{agentId}/config` | PUT | 更新配置 |
| `/api/agent/topology/cluster/{clusterId}` | GET | 集群拓扑 |
| `/api/agent/metrics/all` | GET | 所有指标 |
| `/api/agent/{agentId}/metrics` | POST | 更新指标 |
| `/api/agent/batch` | POST | 批量操作 |
| `/api/agent/batch/{operationId}` | GET | 批量操作状态 |
| `/api/agent/capability/{capability}` | GET | 按能力查询 |
| `/api/agent/tag` | GET | 按标签查询 |
| `/api/agent/alerts` | GET | 获取告警配置列表 |
| `/api/agent/alerts` | POST | 创建告警配置 |
| `/api/agent/alerts/{id}` | PUT | 更新告警配置 |
| `/api/agent/alerts/{id}` | DELETE | 删除告警配置 |
| `/api/agent/alerts/{id}/enable` | POST | 启用告警 |
| `/api/agent/alerts/{id}/disable` | POST | 禁用告警 |

---

### 2.2 功能重复冗余问题 🔴

#### 问题 1: 心跳功能重复

| Controller | API | HTTP 方法 |
|------------|-----|----------|
| AgentController | `/api/agent/{agentId}/heartbeat` | POST |
| AgentSessionController | `/api/v1/agents/{agentId}/heartbeat` | POST |

**建议**: 统一使用 AgentSessionController 的实现，移除 AgentController 中的重复方法。

#### 问题 2: 状态更新重复

| Controller | API | HTTP 方法 |
|------------|-----|----------|
| AgentController | `/api/agent/{agentId}/status` | PUT |
| AgentSessionController | `/api/v1/agents/{agentId}/status` | POST |

**问题**: HTTP 方法不一致 (PUT vs POST)，容易造成调用混乱。

**建议**: 统一使用 PUT 方法更新状态。

#### 问题 3: 统计信息重复

| Controller | API | 功能 |
|------------|-----|------|
| AgentController | `/api/agent/stats` | 获取统计 |
| AgentSessionController | `/api/v1/agents/stats` | 获取统计 |

**建议**: 合并为一个 API。

---

### 2.3 模块依赖问题 🟡

#### 问题 1: 依赖不存在

skill-agent 的 skill.yaml 声明:

```yaml
dependencies:
  - skillId: skill-llm-base
    version: "1.0.0"
    required: false
  - skillId: skill-storage      # ❌ 此模块不存在
    version: "1.0.0"
    required: false
```

**建议**: 移除 `skill-storage` 依赖，或创建该模块。

#### 问题 2: 安装功能重叠

| 模块 | API |
|------|-----|
| skill-install | `POST /api/v1/install/install` |
| skill-discovery | `POST /api/v1/discovery/install` |

**建议**: 统一由 skill-install 提供安装功能，skill-discovery 调用 skill-install。

---

### 2.4 配置格式不一致问题 🟡

#### 问题 1: kind 字段不一致

```yaml
# 部分使用 Skill
kind: Skill

# 部分使用 SkillPackage
kind: SkillPackage
```

**建议**: 统一使用 `kind: Skill`。

#### 问题 2: endpoints 字段名不一致

```yaml
# skill-install 使用 endpoints
spec:
  endpoints:
    - path: /api/v1/install/install

# skill-org-web 使用 capabilities (Driver 类型)
spec:
  capabilities:
    - id: user-management
```

**建议**: PROVIDER 类型使用 `endpoints`，DRIVER 类型使用 `capabilities`。

---

## 三、修复建议

### 3.1 高优先级 (立即修复)

#### 修复 1: 统一 API 路径前缀

将所有 API 统一使用 `/api/v1/` 前缀:

```yaml
# skill-agent/skill.yaml 修复示例
endpoints:
  # Agent 管理
  - path: /api/v1/agents
    method: GET
    controllerClass: net.ooder.skill.agent.controller.AgentController
    methodName: listAll
  - path: /api/v1/agents/page
    method: GET
    controllerClass: net.ooder.skill.agent.controller.AgentController
    methodName: listPage
  - path: /api/v1/agents/{agentId}
    method: GET
    controllerClass: net.ooder.skill.agent.controller.AgentController
    methodName: getAgent
  # ... 其他 API
```

#### 修复 2: 补充缺失的 API 声明

将 AgentController 中已实现但未声明的 30+ 个 API 补充到 skill.yaml。

#### 修复 3: 合并重复功能

```java
// 建议移除 AgentController 中的以下方法，统一使用 AgentSessionController
// - sendHeartbeat()
// - updateStatus()
// - getStats()
```

### 3.2 中优先级 (本周修复)

| # | 问题 | 修复方案 |
|---|------|---------|
| 1 | skill-storage 依赖不存在 | 移除该依赖声明 |
| 2 | 安装功能重叠 | skill-discovery 调用 skill-install |
| 3 | HTTP 方法不一致 | 统一使用 PUT 更新、POST 创建 |

### 3.3 低优先级 (后续迭代)

| # | 问题 | 修复方案 |
|---|------|---------|
| 1 | kind 字段不一致 | 统一使用 Skill |
| 2 | ResultModel 类型不一致 | 统一返回类型 |
| 3 | 日志格式不统一 | 统一日志格式 |

---

## 四、修改文件清单

| 文件 | 修改内容 | 优先级 |
|------|---------|--------|
| skill-agent/skill.yaml | 统一 API 路径、补充缺失声明 | 🔴 高 |
| skill-agent/AgentController.java | 移除重复方法 | 🔴 高 |
| skill-install/skill.yaml | 补充完整配置 | 🟡 中 |
| skill-discovery/skill.yaml | 移除安装 API | 🟡 中 |
| skill-knowledge/skill.yaml | 已完整，无需修改 | ✅ |
| skill-audit/skill.yaml | 已完整，无需修改 | ✅ |
| skill-menu/skill.yaml | 已完整，无需修改 | ✅ |
| skill-role/skill.yaml | 已完整，无需修改 | ✅ |

---

## 五、验证方法

### 5.1 API 路径验证

启动应用后，检查日志:

```
[RouteRegistry] Successfully registered route: GET /api/v1/agents to Spring MVC
[RouteRegistry] Successfully registered route: POST /api/v1/agents/login to Spring MVC
```

如果看到 `No routes defined in skill configuration` 警告，说明配置仍有问题。

### 5.2 功能验证

```bash
# 测试 Agent 列表
curl http://localhost:8085/api/v1/agents

# 测试 Agent 登录
curl -X POST "http://localhost:8085/api/v1/agents/login?agentId=test&secretKey=xxx"

# 测试心跳
curl -X POST http://localhost:8085/api/v1/agents/test/heartbeat
```

---

## 六、与 ooder-skills 仓库的同步建议

### 6.1 需要同步的 Skills

| os/skills 中的 Skill | ooder-skills/skill-index | 建议 |
|---------------------|-------------------------|------|
| skill-agent | 无 | 新增到 sys.yaml |
| skill-audit | 无 | 新增到 sys.yaml |
| skill-key | 无 | 新增到 sys.yaml |
| skill-llm-monitor | 无 | 新增到 llm.yaml |
| skill-notification | 无 | 新增到 msg.yaml |
| skill-im-dingding | 无 | 新增到 msg.yaml |

### 6.2 需要对齐的 Skills

| os/skills | ooder-skills | 问题 |
|-----------|-------------|------|
| skill-llm-deepseek | skill-llm-deepseek (util.yaml) | 版本可能不一致 |
| skill-org-web | skill-org (org.yaml) | 需确认是否为同一实现 |

---

## 七、总结

### 问题统计

| 类型 | 数量 | 状态 |
|------|------|------|
| API 路径不匹配 | 15+ | 待修复 |
| 功能重复 | 5 | 待修复 |
| API 声明缺失 | 30+ | 待修复 |
| 依赖问题 | 3 | 待修复 |
| 配置格式问题 | 10+ | 待修复 |

### 建议优先级

1. **立即修复**: API 路径不一致、功能重复
2. **本周修复**: 依赖问题、安装功能重叠
3. **后续迭代**: 配置格式统一、日志格式统一

---

**报告生成时间**: 2026-04-01  
**文档版本**: 1.0  
**联系人**: ooder skills 团队
