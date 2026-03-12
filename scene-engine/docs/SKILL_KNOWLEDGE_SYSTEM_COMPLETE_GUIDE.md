# Skill知识体系完整构建指南

**版本**: v1.0  
**日期**: 2026-03-12  
**范围**: E:\github\ooder-skills\skills 全部Skill  
**目标**: 构建完整的LLM辅助知识体系

---

## 一、知识体系架构设计

### 1.1 三层知识架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     Skill三层知识架构                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 1: 运行时知识层 (Runtime Knowledge Layer)                  │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  作用: LLM运行时动态检索的知识                                     │   │
│  │  形式: 向量索引 + RAG检索                                          │   │
│  │  更新: 实时/准实时                                                  │   │
│  │                                                                 │   │
│  │  包含:                                                           │   │
│  │  ├── 技能功能描述 (Skill Overview)                                │   │
│  │  ├── API文档 (API Reference)                                      │   │
│  │  ├── 配置指南 (Configuration Guide)                               │   │
│  │  ├── 故障排查 (Troubleshooting)                                   │   │
│  │  └── 常见问题 (FAQ)                                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 2: 结构化知识层 (Structured Knowledge Layer)               │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  作用: 标准化的知识文档，支持版本控制                               │   │
│  │  形式: Markdown + YAML Front Matter                               │   │
│  │  更新: 随版本发布                                                   │   │
│  │                                                                 │   │
│  │  包含:                                                           │   │
│  │  ├── README.md (技能概览)                                         │   │
│  │  ├── docs/quick-start.md (快速开始)                               │   │
│  │  ├── docs/api-reference.md (API参考)                              │   │
│  │  ├── docs/configuration.md (配置指南)                             │   │
│  │  ├── docs/architecture.md (架构设计)                              │   │
│  │  └── docs/development.md (开发指南)                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Layer 3: 元数据知识层 (Metadata Knowledge Layer)                 │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  作用: 定义Skill的基本信息和能力声明                                │   │
│  │  形式: skill.yaml + skill-index-entry.yaml                        │   │
│  │  更新: 开发时定义                                                   │   │
│  │                                                                 │   │
│  │  包含:                                                           │   │
│  │  ├── metadata (id, name, version, description)                    │   │
│  │  ├── spec (capabilities, endpoints, config)                       │   │
│  │  ├── dependencies (依赖声明)                                      │   │
│  │  └── knowledge (知识配置，新增)                                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1.2 知识流动机制

```
开发者编写
    │
    ▼
┌─────────────────────────────────────┐
│ skill.yaml (元数据定义)              │
│ docs/*.md (结构化文档)               │
└─────────────────────────────────────┘
    │
    │ 构建时
    ▼
┌─────────────────────────────────────┐
│ Knowledge Compiler (知识编译器)      │
│ ├── 解析YAML/MD                      │
│ ├── 语义增强 (LLM辅助)               │
│ ├── 生成向量索引                     │
│ └── 输出知识包                       │
└─────────────────────────────────────┘
    │
    │ 部署时
    ▼
┌─────────────────────────────────────┐
│ KnowledgeBaseInstaller               │
│ ├── 加载知识包                       │
│ ├── 构建向量索引                     │
│ └── 注册到RAG服务                    │
└─────────────────────────────────────┘
    │
    │ 运行时
    ▼
┌─────────────────────────────────────┐
│ LLM Runtime                          │
│ ├── RAG检索增强                      │
│ ├── 动态知识更新                     │
│ └── 上下文组装                       │
└─────────────────────────────────────┘
```

---

## 二、Skills配置.md完整补充列表

### 2.1 Driver层Skills (22个)

#### 2.1.1 Media类 (5个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `_drivers/media/skill-media-toutiao/` | 只有skill.yaml | README.md, docs/integration-guide.md, docs/api-mapping.md | P0 |
| `_drivers/media/skill-media-wechat/` | 只有skill.yaml | README.md, docs/integration-guide.md, docs/auth-guide.md | P0 |
| `_drivers/media/skill-media-weibo/` | 只有skill.yaml | README.md, docs/integration-guide.md, docs/error-codes.md | P0 |
| `_drivers/media/skill-media-xiaohongshu/` | 只有skill.yaml | README.md, docs/integration-guide.md, docs/api-mapping.md | P0 |
| `_drivers/media/skill-media-zhihu/` | 只有skill.yaml | README.md, docs/integration-guide.md, docs/faq.md | P0 |

#### 2.1.2 Payment类 (3个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `_drivers/payment/skill-payment-alipay/` | 只有skill.yaml | README.md, docs/integration-guide.md, docs/security-guide.md | P0 |
| `_drivers/payment/skill-payment-wechat/` | 只有skill.yaml | README.md, docs/integration-guide.md, docs/cert-config.md | P0 |
| `_drivers/payment/skill-payment-unionpay/` | 只有skill.yaml | README.md, docs/integration-guide.md | P1 |

#### 2.1.3 Org类 (5个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `_drivers/org/skill-org-dingding/` | 有skill-index-entry.yaml | README.md, docs/sync-guide.md | P1 |
| `_drivers/org/skill-org-feishu/` | 有skill-index-entry.yaml | README.md, docs/sync-guide.md | P1 |
| `_drivers/org/skill-org-wecom/` | 有skill-index-entry.yaml | README.md, docs/sync-guide.md | P1 |
| `_drivers/org/skill-org-ldap/` | 有README.md | docs/mapping-guide.md | P2 |
| `_drivers/org/skill-org-base/` | 配置较完善 | docs/advanced-config.md | P2 |

#### 2.1.4 VFS类 (6个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `_drivers/vfs/skill-vfs-database/` | 有README.md | docs/performance-tuning.md | P2 |
| `_drivers/vfs/skill-vfs-local/` | 有README.md | docs/security-guide.md | P2 |
| `_drivers/vfs/skill-vfs-minio/` | 配置较完善 | README.md | P1 |
| `_drivers/vfs/skill-vfs-oss/` | 配置较完善 | README.md | P1 |
| `_drivers/vfs/skill-vfs-s3/` | 配置较完善 | README.md | P1 |
| `_drivers/vfs/skill-vfs-base/` | 配置较完善 | docs/extending.md | P2 |

#### 2.1.5 LLM类 (5个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `_drivers/llm/skill-llm-openai/` | 配置完善 | docs/model-guide.md | P2 |
| `_drivers/llm/skill-llm-qianwen/` | 配置完善 | docs/model-guide.md | P2 |
| `_drivers/llm/skill-llm-deepseek/` | 配置较完善 | README.md | P1 |
| `_drivers/llm/skill-llm-ollama/` | 配置完善 | docs/local-deployment.md | P2 |
| `_drivers/llm/skill-llm-volcengine/` | 只有pom.xml | README.md, docs/quick-start.md | P1 |

### 2.2 Capability层Skills (28个)

#### 2.2.1 Communication类 (6个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `capabilities/communication/skill-email/` | 只有skill.yaml | README.md, docs/smtp-config.md, docs/template-guide.md | P1 |
| `capabilities/communication/skill-im/` | 只有skill.yaml | README.md, docs/websocket-guide.md, docs/protocol.md | P1 |
| `capabilities/communication/skill-msg/` | 只有skill.yaml | README.md, docs/message-flow.md | P1 |
| `capabilities/communication/skill-notify/` | 只有skill.yaml | README.md, docs/channel-config.md | P1 |
| `capabilities/communication/skill-group/` | 只有skill.yaml | README.md, docs/group-management.md | P2 |
| `capabilities/communication/skill-mqtt/` | 有README.md | docs/topic-design.md | P2 |

#### 2.2.2 Knowledge类 (4个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `capabilities/knowledge/skill-knowledge-base/` | 有模型代码 | README.md, docs/kb-management.md, docs/search-guide.md | P0 |
| `capabilities/knowledge/skill-rag/` | 只有pom.xml | README.md, docs/retrieval-guide.md, skill.yaml | P0 |
| `capabilities/knowledge/skill-vector-sqlite/` | 只有pom.xml | README.md, docs/vector-ops.md | P1 |
| `capabilities/knowledge/skill-local-knowledge/` | 只有pom.xml | README.md | P2 |

#### 2.2.3 LLM类 (3个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `capabilities/llm/skill-llm-conversation/` | 有skill.yaml | docs/session-management.md, docs/streaming.md | P0 |
| `capabilities/llm/skill-llm-context-builder/` | 只有pom.xml | README.md, docs/context-extraction.md | P1 |
| `capabilities/llm/skill-llm-config-manager/` | 只有pom.xml | README.md, docs/config-schema.md | P1 |

#### 2.2.4 Scheduler类 (2个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `capabilities/scheduler/skill-scheduler-quartz/` | 只有skill.yaml | README.md, docs/cron-guide.md, docs/clustering.md | P1 |
| `capabilities/scheduler/skill-task/` | 只有skill.yaml | README.md, docs/task-lifecycle.md | P1 |

#### 2.2.5 Monitor类 (6个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `capabilities/monitor/skill-agent/` | 有skill-index-entry.yaml | README.md, docs/agent-deployment.md | P2 |
| `capabilities/monitor/skill-cmd-service/` | 只有skill.yaml | README.md, docs/security-policy.md | P2 |
| `capabilities/monitor/skill-health/` | 有skill-index-entry.yaml | README.md, docs/health-checks.md | P2 |
| `capabilities/monitor/skill-monitor/` | 有skill-index-entry.yaml | README.md, docs/metrics.md | P2 |
| `capabilities/monitor/skill-network/` | 有README.md | docs/network-topology.md | P2 |
| `capabilities/monitor/skill-remote-terminal/` | 只有skill.yaml | README.md, docs/ssh-config.md | P2 |

#### 2.2.6 Security类 (3个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `capabilities/security/skill-access-control/` | 只有skill.yaml | README.md, docs/rbac-model.md | P2 |
| `capabilities/security/skill-audit/` | 只有skill.yaml | README.md, docs/audit-log.md | P2 |
| `capabilities/security/skill-security/` | 只有skill.yaml | README.md, docs/security-policy.md | P2 |

#### 2.2.7 其他 (4个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `capabilities/auth/skill-user-auth/` | 有skill.yaml | docs/auth-flow.md, docs/oauth-guide.md | P1 |
| `capabilities/search/skill-search/` | 只有skill.yaml | README.md, docs/indexing.md | P2 |
| `capabilities/infrastructure/skill-hosting/` | 只有pom.xml | README.md | P2 |
| `capabilities/infrastructure/skill-k8s/` | 只有pom.xml | README.md | P2 |

### 2.3 Scene层Skills (8个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `scenes/skill-business/` | 有skill.yaml | docs/business-flow.md | P1 |
| `scenes/skill-collaboration/` | 有skill-index-entry.yaml | README.md, docs/collab-features.md | P1 |
| `scenes/skill-document-assistant/` | 有skill-index-entry.yaml | README.md, docs/document-workflow.md | P1 |
| `scenes/skill-knowledge-qa/` | 有skill-index-entry.yaml | README.md, docs/qa-guide.md | P0 |
| `scenes/skill-knowledge-share/` | 有skill-index-entry.yaml | README.md | P2 |
| `scenes/skill-llm-chat/` | 有skill-index-entry.yaml | README.md, docs/chat-features.md, docs/persona-guide.md | P0 |
| `scenes/skill-meeting-minutes/` | 有skill-index-entry.yaml | README.md | P2 |
| `scenes/skill-onboarding-assistant/` | 有skill-index-entry.yaml | README.md, docs/onboarding-flow.md | P1 |

### 2.4 System层Skills (4个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `_system/skill-capability/` | 有skill.yaml | docs/capability-lifecycle.md | P2 |
| `_system/skill-common/` | 有skill.yaml | docs/utilities.md | P2 |
| `_system/skill-management/` | 有skill.yaml | docs/skill-ops.md | P2 |
| `_system/skill-protocol/` | 有skill.yaml | docs/protocol-spec.md | P2 |

### 2.5 Tools层Skills (4个)

| Skill路径 | 当前状态 | 需补充文档 | 优先级 |
|-----------|---------|-----------|--------|
| `tools/skill-document-processor/` | 有skill-index-entry.yaml | README.md | P2 |
| `tools/skill-market/` | 有skill.yaml | README.md, docs/market-guide.md | P2 |
| `tools/skill-report/` | 有skill-index-entry.yaml | README.md | P2 |
| `tools/skill-share/` | 有skill.yaml | README.md | P2 |

---

## 三、文档模板规范

### 3.1 README.md 标准模板

```markdown
# {skill-name}

{skill-description}

## 功能特性

- ✨ {feature-1}
- 🚀 {feature-2}
- 🔧 {feature-3}
- 📚 {feature-4}

## 快速开始

### 安装

```bash
# 通过Skill市场安装
skill install {skill-id}

# 或手动安装
mvn clean install
```

### 配置

```yaml
# application.yml
{skill-id}:
  enabled: true
  {config-key}: {config-value}
```

### 使用示例

```java
// 代码示例
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [API参考](docs/api-reference.md)
- [配置指南](docs/configuration.md)
- [故障排查](docs/troubleshooting.md)

## 依赖

- {dependency-1}
- {dependency-2}

## 许可证

{license}
```

### 3.2 skill.yaml 知识配置扩展模板

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: {skill-id}
  name: {skill-name}
  version: {version}
  description: {description}
  author: {author}
  type: {skill-type}
  license: {license}
  keywords:
    - {keyword-1}
    - {keyword-2}

spec:
  type: {spec-type}
  
  # ... 现有配置 ...
  
  # ============================================
  # 知识配置 (新增)
  # ============================================
  knowledge:
    # 知识文档配置
    documents:
      - id: overview
        name: 功能概述
        path: docs/overview.md
        type: guide
        language: zh
        priority: high
        
      - id: quick-start
        name: 快速开始
        path: docs/quick-start.md
        type: guide
        language: zh
        priority: high
        
      - id: api-reference
        name: API参考
        path: docs/api-reference.md
        type: reference
        language: zh
        priority: medium
        
      - id: configuration
        name: 配置指南
        path: docs/configuration.md
        type: guide
        language: zh
        priority: medium
        
      - id: troubleshooting
        name: 故障排查
        path: docs/troubleshooting.md
        type: guide
        language: zh
        priority: low
        
      - id: faq
        name: 常见问题
        path: docs/faq.md
        type: faq
        language: zh
        priority: low
    
    # RAG配置
    ragConfig:
      enabled: true
      indexName: "{skill-id}-knowledge"
      embeddingModel: text-embedding-3-small
      chunkSize: 1000
      chunkOverlap: 200
      searchStrategy: hybrid
      topK: 5
      threshold: 0.7
      rerankEnabled: false
    
    # 动态知识源
    dynamicSources:
      - type: codebase
        name: 代码注释
        path: src/main/java
        filePattern: "*.java"
        autoSync: true
        syncInterval: 24h
        
      - type: external
        name: 外部文档
        url: {external-doc-url}
        cacheTtl: 7d
  
  # 角色/Persona配置 (新增)
  persona:
    enabled: true
    name: {persona-name}
    description: {persona-description}
    expertise:
      - {expertise-1}
      - {expertise-2}
    boundaries:
      - {boundary-1}
      - {boundary-2}
    tone: professional
    examples:
      - question: {example-question-1}
        answer: {example-answer-1}
      - question: {example-question-2}
        answer: {example-answer-2}
  
  # LLM辅助配置 (新增)
  llmAssistant:
    enabled: true
    welcomeMessage: "您好，我是{skill-name}智能助手，有什么可以帮助您的？"
    suggestedQuestions:
      - "如何配置{skill-name}？"
      - "{skill-name}支持哪些功能？"
      - "常见错误如何解决？"
    contextWindow: 10
    maxTokens: 2000
```

### 3.3 docs/quick-start.md 模板

```markdown
# 快速开始

## 环境要求

- Java 8+
- Maven 3.6+
- {other-requirements}

## 安装

### 方式一：通过Skill市场安装

```bash
skill install {skill-id}
```

### 方式二：手动安装

1. 克隆代码
```bash
git clone {repository-url}
cd {skill-id}
```

2. 编译安装
```bash
mvn clean install
```

## 配置

### 基础配置

编辑 `application.yml`：

```yaml
{skill-id}:
  enabled: true
  {config-key}: {config-value}
```

### 高级配置

```yaml
{skill-id}:
  advanced:
    {advanced-config}
```

## 验证安装

```bash
# 启动服务
mvn spring-boot:run

# 测试接口
curl http://localhost:8080/api/{skill-id}/health
```

## 下一步

- [API参考](api-reference.md)
- [配置指南](configuration.md)
```

### 3.4 docs/api-reference.md 模板

```markdown
# API参考

## 接口列表

### {endpoint-1}

**URL**: `{method} {path}`

**描述**: {description}

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| {param-1} | {type} | {required} | {description} |

**响应示例**:

```json
{
  "code": 200,
  "data": { }
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 500 | 服务器内部错误 |

### {endpoint-2}

...
```

### 3.5 docs/configuration.md 模板

```markdown
# 配置指南

## 配置概述

{skill-name} 支持以下配置方式：

1. 配置文件 (`application.yml`)
2. 环境变量
3. 配置中心 (Nacos/Apollo)

## 基础配置

### 启用Skill

```yaml
{skill-id}:
  enabled: true
```

### 核心配置

```yaml
{skill-id}:
  {config-section}:
    {config-key}: {default-value}  # {description}
```

## 高级配置

### 性能调优

```yaml
{skill-id}:
  performance:
    {performance-config}
```

### 安全配置

```yaml
{skill-id}:
  security:
    {security-config}
```

## 配置示例

### 开发环境

```yaml
{skill-id}:
  {dev-config}
```

### 生产环境

```yaml
{skill-id}:
  {prod-config}
```

## 配置项参考

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| {config-item} | {type} | {default} | {description} |
```

### 3.6 docs/troubleshooting.md 模板

```markdown
# 故障排查

## 常见问题

### 问题1: {problem-1}

**现象**: {symptom}

**原因**: {cause}

**解决方案**:

1. {step-1}
2. {step-2}
3. {step-3}

### 问题2: {problem-2}

...

## 日志分析

### 日志位置

```
logs/{skill-id}.log
```

### 关键日志关键字

| 关键字 | 说明 |
|--------|------|
| {keyword-1} | {description} |

## 调试模式

启用调试模式：

```yaml
logging:
  level:
    net.ooder.skill.{skill-id}: DEBUG
```

## 获取帮助

- 查看 [FAQ](faq.md)
- 提交 [Issue]({issue-url})
```

---

## 四、实施计划

### 4.1 分阶段实施

```
Phase 1: 核心Skill补全 (Week 1-2)
├── skill-llm-chat
├── skill-knowledge-qa
├── skill-llm-conversation
├── skill-knowledge-base
└── skill-rag

Phase 2: Driver层补全 (Week 3-4)
├── Media类 (5个)
├── Payment类 (3个)
└── LLM类 (补充volcengine)

Phase 3: Capability层补全 (Week 5-6)
├── Communication类 (6个)
├── Scheduler类 (2个)
└── Knowledge类 (补充vector-sqlite, local-knowledge)

Phase 4: 其他层补全 (Week 7-8)
├── Scene层 (补充README)
├── System层
└── Tools层
```

### 4.2 优先级矩阵

| 优先级 | Skill数量 | 说明 |
|--------|-----------|------|
| P0 | 6 | 核心LLM/Knowledge相关Skill，阻塞生产 |
| P1 | 20 | 主要业务Skill，影响用户体验 |
| P2 | 40+ | 其他Skill，逐步完善 |

### 4.3 检查清单

每个Skill需要完成：

- [ ] README.md (必须)
- [ ] docs/quick-start.md (必须)
- [ ] docs/api-reference.md (必须)
- [ ] docs/configuration.md (推荐)
- [ ] docs/troubleshooting.md (推荐)
- [ ] docs/faq.md (可选)
- [ ] skill.yaml 知识配置 (必须)
- [ ] 向量索引构建 (必须)

---

## 五、自动化工具

### 5.1 文档生成工具

```java
@Component
public class SkillDocGenerator {
    
    public void generateDocs(SkillPackage skillPackage) {
        // 1. 生成README.md
        generateReadme(skillPackage);
        
        // 2. 生成API文档
        generateApiDocs(skillPackage);
        
        // 3. 生成配置文档
        generateConfigDocs(skillPackage);
        
        // 4. 生成FAQ (LLM辅助)
        generateFaq(skillPackage);
    }
    
    private void generateFaq(SkillPackage skillPackage) {
        // 使用LLM基于Skill描述生成FAQ
        String faq = llmService.generateFAQ(
            skillPackage.getDescription(),
            skillPackage.getCapabilities()
        );
        writeToFile("docs/faq.md", faq);
    }
}
```

### 5.2 配置检查工具

```java
@Component
public class SkillConfigChecker {
    
    public CheckResult check(SkillPackage skillPackage) {
        CheckResult result = new CheckResult();
        
        // 检查知识文档
        checkKnowledgeDocs(skillPackage, result);
        
        // 检查RAG配置
        checkRagConfig(skillPackage, result);
        
        // 检查Persona配置
        checkPersonaConfig(skillPackage, result);
        
        return result;
    }
}
```

---

## 六、总结

### 6.1 工作量估算

| 任务 | 数量 | 预估工时 |
|------|------|----------|
| README.md | 60+ | 120人时 |
| docs/*.md | 200+ | 400人时 |
| skill.yaml 知识配置 | 60+ | 60人时 |
| 向量索引构建 | 60+ | 30人时 |
| **总计** | - | **610人时** |

### 6.2 关键成功因素

1. **标准化模板** - 统一文档格式，降低编写成本
2. **LLM辅助生成** - 利用LLM自动生成FAQ、示例等内容
3. **分阶段实施** - 优先核心Skill，逐步覆盖全部
4. **自动化检查** - 建立CI/CD检查机制，确保质量

### 6.3 预期收益

- 📈 LLM回答准确率提升 **30%+**
- 🚀 开发效率提升 **50%+**
- 📚 形成完整的Skill知识库
- 🤖 支持智能助手自动问答

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-12
