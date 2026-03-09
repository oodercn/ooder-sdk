# Skill-Knowledge-QA 二次开发指南

> **版本**: 1.0  
> **日期**: 2026-03-03  
> **目标读者**: KG团队  
> **关联文档**: [skill-knowledge-qa依赖配置问题](file:///e:/github/ooder-skills/skill-ui-test/docs/skill-knowledge-qa-dependency-issue.md)

---

## 更新记录

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0 | 2026-03-03 | 初始版本，基于skill-knowledge-qa集成测试问题整理 |

---

## 一、概述

本文档面向KG团队，说明在开发`skill-knowledge-qa`（知识问答场景能力）时遇到的依赖配置问题，以及当前推荐的开发方式。

### 1.1 问题背景

`skill-knowledge-qa`在`skill.yaml`中声明了以下依赖：

| 依赖ID | 必需 | 当前状态 | 说明 |
|--------|------|----------|------|
| `skill-knowledge-base` | **是** | ❌ 不存在 | 知识库核心服务 |
| `skill-indexing` | **是** | ❌ 不存在 | 文档索引服务 |
| `skill-rag` | 否 | ❌ 不存在 | RAG检索增强 |
| `skill-llm-assistant` | 否 | ❌ 不存在 | LLM智能助手 |

**核心问题**: Engine尚未实现依赖自动安装机制，导致必需依赖缺失时场景能力无法启动。

### 1.2 当前状态

- ✅ `skill-knowledge-qa` UI skill已存在
- ❌ 依赖的4个skill均不存在
- ⚠️ `scene-indexing` 协作场景不存在
- ⚠️ LLM配置与Skill依赖未关联

---

## 二、Engine团队待解决问题

### 2.1 P0 - 阻塞问题

#### 问题1: 依赖自动安装机制缺失

**现状**: 
- `skill.yaml`中声明了`required: true`的依赖
- Engine没有自动检测和安装依赖的机制
- 应用启动时依赖检查失败

**需要Engine实现**:
```yaml
dependencies:
  - id: skill-knowledge-base
    version: ">=1.0.0"
    required: true
    autoInstall: true        # 需要Engine支持
    installSource: remote    # 需要Engine支持
    fallback: embedded       # 需要Engine支持
```

**关键决策点**:
1. 依赖解析的触发时机（启动时/首次访问时/懒加载）
2. `required: true`依赖缺失时的处理策略
3. 依赖版本冲突解决机制

#### 问题2: 场景能力协作机制不完善

**现状**:
```yaml
selfCheck:
  - checkCollaborative: [scene-indexing]    # scene-indexing不存在
```

**需要Engine实现**:
1. `selfCheck`阶段协作能力检查失败时的处理策略
2. `collaborativeCapabilities.autoStart`的具体执行逻辑
3. 协作接口（`interface: indexing-service`）的匹配和绑定机制

### 2.2 P1 - 高优先级问题

#### 问题3: LLM配置与Skill依赖割裂

**现状**:
- `skill-llm-assistant`依赖与`ooder.llm.*`配置无关联
- LLM配置散落在`application.yml`

**需要Engine决策**:
1. 是否将LLM作为可选Skill依赖管理
2. 如果作为Skill依赖，如何实现配置映射
3. LLM健康检查如何纳入`selfCheck`流程

#### 问题4: 能力提供者归属不清

**现状**:
- `kb-management`等能力在主skill中定义
- 不清楚能力应该由主skill提供还是依赖skill提供

**需要Engine明确**:
1. 能力定义和实现的分离规范
2. 依赖skill不存在时的fallback策略

---

## 三、当前推荐开发方式

在Engine团队完成上述问题修复前，KG团队采用以下临时方案：

### 3.1 方案: 内置实现（当前采用）

在主应用中内置服务实现，不依赖外部skill。

#### 3.1.1 服务实现

| 服务 | 文件 | 功能 |
|------|------|------|
| `DocumentIndexService` | `DocumentIndexService.java` | 文档索引和检索 |
| `LLMService` | `LLMService.java` | LLM API调用 |

#### 3.1.2 配置方式

```yaml
# application.yml
ooder:
  kb:
    storage-path: ./kb-storage
    index-path: ./kb-index
  llm:
    provider: openai
    api-key: ${OODER_LLM_API_KEY:}
    base-url: ${OODER_LLM_BASE_URL:https://api.openai.com/v1}
    model: gpt-3.5-turbo
```

#### 3.1.3 API端点

| 端点 | 方法 | 功能 |
|------|------|------|
| `/api/kb/list` | GET | 列出知识库 |
| `/api/kb/create` | POST | 创建知识库 |
| `/api/kb/upload` | POST | 上传文档 |
| `/api/kb/search` | POST | 搜索文档 |
| `/api/kb/qa` | POST | 智能问答 |
| `/api/kb/llm/status` | GET | LLM状态检查 |

### 3.2 skill.yaml 调整

```yaml
spec:
  type: scene-skill
  
  # 暂时移除required依赖，改为optional
  dependencies:
    - id: skill-knowledge-base
      version: ">=1.0.0"
      required: false        # 改为false，使用内置实现
      description: "知识库核心服务（可选，当前使用内置实现）"
      
    - id: skill-indexing
      version: ">=1.0.0"
      required: false        # 改为false
      description: "文档索引服务（可选，当前使用内置实现）"
      
    - id: skill-rag
      version: ">=1.0.0"
      required: false
      description: "RAG检索增强（可选）"
      
    - id: skill-llm-assistant
      version: ">=1.0.0"
      required: false
      description: "LLM智能助手（可选，当前使用内置实现）"

sceneCapabilities:
  - id: scene-knowledge-qa
    name: 知识问答场景能力
    type: SCENE
    mainFirst: true
    
    mainFirstConfig:
      selfCheck:
        - checkCapabilities: [kb-management, document-management, kb-search]
        - checkDriverCapabilities: [intent-receiver, event-listener]
        # 暂时移除checkCollaborative，因为scene-indexing不存在
        # - checkCollaborative: [scene-indexing]
        
      selfStart:
        - initDriverCapabilities: [intent-receiver, event-listener, capability-invoker]
        - initCapabilities: [kb-management, document-management, kb-search]
        - bindAddresses: auto
        
      # 暂时移除startCollaboration
      # startCollaboration:
      #   - startScene: scene-indexing
      #     bindInterface: indexing-service
```

---

## 四、Engine修复后的迁移方案

当Engine团队完成依赖管理机制后，按以下步骤迁移：

### 4.1 迁移步骤

1. **开发依赖Skill**
   - 开发/获取 `skill-knowledge-base`
   - 开发/获取 `skill-indexing`
   - 开发/获取 `skill-rag`（可选）
   - 开发/获取 `skill-llm-assistant`（可选）

2. **更新skill.yaml**
   ```yaml
   dependencies:
     - id: skill-knowledge-base
       version: ">=1.0.0"
       required: true        # 恢复为true
       autoInstall: true     # 启用自动安装
   ```

3. **移除内置实现**
   - 删除 `DocumentIndexService.java` 中的冗余实现
   - 删除 `LLMService.java` 中的冗余实现
   - 保留接口，改为调用依赖skill的能力

4. **恢复协作配置**
   ```yaml
   selfCheck:
     - checkCollaborative: [scene-indexing]
   
   startCollaboration:
     - startScene: scene-indexing
       bindInterface: indexing-service
   ```

### 4.2 能力接口定义

Engine团队需要定义以下能力接口：

```java
// 知识库管理
public interface KnowledgeBaseCapability {
    String createKB(String name, String description);
    void deleteKB(String kbId);
    List<KBInfo> listKBs();
}

// 文档索引
public interface DocumentIndexingCapability {
    void indexDocument(String kbId, Document doc);
    List<SearchResult> search(String kbId, String query);
}

// LLM服务
public interface LLMCapability {
    String chat(String prompt);
    List<Float> embed(String text);
    boolean isAvailable();
}
```

---

## 五、建议的Engine增强配置

### 5.1 依赖配置增强

```yaml
dependencies:
  - id: skill-knowledge-base
    version: ">=1.0.0"
    required: true
    autoInstall: true
    installSource: https://gitee.com/ooderCN/ooder-skills/releases
    fallback: embedded      # 当依赖不可用时的fallback策略
    timeout: 30s            # 安装超时
    
  - id: skill-llm-assistant
    version: ">=1.0.0"
    required: false
    autoInstall: false
    configMapping:          # 配置映射
      apiKey: "${ooder.llm.api-key}"
      baseUrl: "${ooder.llm.base-url}"
      model: "${ooder.llm.model}"
    healthCheck:            # 健康检查
      enabled: true
      endpoint: /health
      interval: 30s
```

### 5.2 场景能力配置增强

```yaml
sceneCapabilities:
  - id: scene-knowledge-qa
    mainFirst: true
    
    mainFirstConfig:
      selfCheck:
        - checkCapabilities: [kb-management, document-management, kb-search]
        - checkDependencies: [skill-knowledge-base, skill-indexing]  # 新增
        - checkConfig: [ooder.llm.api-key]                           # 新增
        
      onCheckFailed:                                           # 新增
        action: degrade                                        # degrade | fail | continue
        degradedCapabilities: [rag-retrieval]
        retry:
          maxAttempts: 3
          delay: 5s
        
      selfStart:
        - installDependencies: auto                            # 新增
        - initDriverCapabilities: [intent-receiver, event-listener]
        - initCapabilities: [kb-management, document-management, kb-search]
        - bindAddresses: auto
```

---

## 六、联系方式

- **KG团队**: 负责skill-knowledge-qa开发
- **Engine团队**: 负责依赖管理机制实现
- **文档位置**: `docs/v2.3/SKILL_KNOWLEDGE_QA_DEVELOPMENT_GUIDE.md`

---

## 附录

### A. 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 依赖问题详细说明 | `skill-ui-test/docs/skill-knowledge-qa-dependency-issue.md` | 完整问题描述 |
| SDK注入指南 | `agent-sdk/docs/SDK_INJECTION_SECONDARY_DEVELOPMENT_GUIDE.md` | SDK集成指南 |
| 集成测试反馈 | `docs/v2.3/INTEGRATION_TEST_FEEDBACK.md` | Engine集成问题 |

### B. Engine团队待办清单

| 优先级 | 任务 | 说明 |
|--------|------|------|
| P0 | 依赖解析和安装机制 | 实现`autoInstall`和`installSource`支持 |
| P0 | 场景能力协作机制 | 实现协作能力发现和`autoStart`逻辑 |
| P1 | 增强selfCheck流程 | 支持依赖检查、配置检查、失败处理 |
| P1 | LLM集成方案 | 确定LLM配置管理方式 |
| P2 | 能力提供者注册 | 实现能力定义和fallback机制 |
| P2 | 健康检查集成 | 依赖健康检查和LLM就绪检查 |
