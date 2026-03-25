# 技术故事：LLM 与场景技能集成设计

**版本**: v2.3.1  
**日期**: 2026-03-07  
**状态**: 设计规范  
**作者**: Ooder Team

---

## 一、背景与目标

### 1.1 背景

当前 scene-engine 已实现场景技能分类体系和基础能力框架，但 LLM 与场景技能的集成存在以下问题：

1. **概念混淆**：Tool（工具）与 Skill（技能）层次不清
2. **LLM 实现缺失**：当前 LlmSdkWrapper 是 Mock 实现
3. **架构关系模糊**：SceneGroup、SceneAgent、Skill 关系不清晰
4. **降级策略缺失**：LLM 不可用时无回退机制

### 1.2 目标

1. 明确 LLM 在技术分层中的定位
2. 定义 LLM 介入能力协作的方式
3. 设计离线支持与在线决策机制
4. 明确知识库的价值定位

---

## 二、技术分层架构

### 2.1 LLM 分层定位

LLM 应该跨两层存在：

```
┌─────────────────────────────────────────────────────────────┐
│                    LLM 分层定位                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  技能层 - LLM 作为"决策者"                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │         LLM Decision Engine                         │   │
│  │                                                     │   │
│  │  • 意图理解      → 用户想做什么？                    │   │
│  │  • 能力选择      → 该调用哪个 Capability？           │   │
│  │  • 参数提取      → 参数值是什么？                    │   │
│  │  • 结果解释      → 如何向用户呈现？                  │   │
│  └─────────────────────────────────────────────────────┘   │
│                          │                                  │
│                          ▼                                  │
│  基础层 - LLM 作为"能力提供者"                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │         LLM Capability Provider                     │   │
│  │                                                     │   │
│  │  • Chat Completion   → 对话生成                     │   │
│  │  • Text Embedding    → 向量化                       │   │
│  │  • Function Calling  → 工具调用                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 完整层次结构

| 层次 | 组件 | 职责 | LLM 角色 |
|------|------|------|----------|
| **应用层** | SceneClient, SceneGroup | 用户交互、协作管理 | 无 |
| **场景层** | SceneAgent, SceneSkill | 场景编排、能力协调 | 调用决策 |
| **技能层** | Capability, ToolRegistry | 业务逻辑、工具调用 | **决策者** |
| **基础层** | LlmProvider, KnowledgeBase | 原子能力提供 | **能力提供者** |

---

## 三、LLM 介入能力协作的方式

### 3.1 三种介入模式

#### 模式1: LLM 作为路由器

**适用场景**：用户意图不明确，需要 LLM 理解并路由到正确的 Capability

```
User Query ──▶ LLM ──▶ 意图识别 ──▶ Capability 选择
```

**示例**：
```
用户: "帮我预约面试"
LLM 分析:
  - 意图: 面试安排
  - Capability: interview_schedule
  - 参数: 需要进一步询问时间、候选人
```

#### 模式2: LLM 作为执行器

**适用场景**：需要 LLM 生成内容或处理自然语言

```
Capability 调用 ──▶ LLM ──▶ 结果生成
```

**示例**：
```
用户: "生成面试邀请邮件"
LLM 执行:
  - 输入: 候选人信息、面试时间
  - 输出: 格式化的邮件内容
```

#### 模式3: LLM 作为协调器

**适用场景**：复杂任务需要多步骤协调

```
复杂任务 ──▶ LLM ──▶ 任务拆解 ──▶ 多 Capability 协调
```

**示例**：
```
用户: "帮我完成招聘流程"
LLM 协调:
  Step 1: 收集简历 (Capability A)
  Step 2: 筛选简历 (Capability B)
  Step 3: 安排面试 (Capability C)
  Step 4: 发送通知 (Capability D)
```

### 3.2 招聘场景示例

```
┌─────────────────────────────────────────────────────────────┐
│  用户: "帮我筛选一下今天收到的简历，找出符合条件的"          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Step 1: 意图理解 (LLM Router)                              │
│  ────────────────────────────────────────                   │
│  LLM 分析:                                                  │
│  - 意图: 简历筛选                                           │
│  - 时间范围: 今天                                           │
│  - 条件: 需要进一步询问                                     │
│                                                             │
│  Step 2: 参数收集 (LLM Router)                              │
│  ────────────────────────────────────────                   │
│  LLM 反问: "请问筛选条件是什么？"                           │
│  用户: "3年以上Java经验，本科以上学历"                      │
│  LLM 提取:                                                  │
│  - 经验要求: 3年以上                                        │
│  - 技能要求: Java                                           │
│  - 学历要求: 本科及以上                                     │
│                                                             │
│  Step 3: 能力调用                              │
│  ────────────────────────────────────────                   │
│  调用 Capability: resume_screening                          │
│  参数: {                                                    │
│    "dateRange": "today",                                    │
│    "experience": "3+",                                      │
│    "skills": ["Java"],                                      │
│    "education": "bachelor+"                                 │
│  }                                                          │
│                                                             │
│  Step 4: 结果生成                              │
│  ────────────────────────────────────────                   │
│  Capability 返回: 5份符合条件的简历                         │
│  LLM 生成:                                                  │
│  "今天收到20份简历，筛选出5份符合条件的：                   │
│   1. 张三 - 5年Java经验，硕士                               │
│   2. 李四 - 4年Java经验，本科                               │
│   ..."                                                      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 四、离线支持与在线决策设计

### 4.1 设计原则

| 原则 | 说明 |
|------|------|
| **业务逻辑离线定义** | Capability 接口、业务规则、工作流在离线时定义 |
| **LLM 在线增强** | LLM 用于增强用户体验，非必需 |
| **降级策略** | LLM 不可用时回退到规则引擎 |

### 4.2 分层策略

```
┌─────────────────────────────────────────────────────────────┐
│  Layer 1: 确定性逻辑 (离线定义)                             │
│  ├── Capability 接口定义                                    │
│  ├── 业务规则配置                                           │
│  ├── 工作流定义                                             │
│  └── 数据模型                                               │
├─────────────────────────────────────────────────────────────┤
│  Layer 2: 概率性增强 (在线决策)                             │
│  ├── 意图理解                                               │
│  ├── 参数提取                                               │
│  ├── 结果生成                                               │
│  └── 异常处理                                               │
└─────────────────────────────────────────────────────────────┘
```

### 4.3 决策引擎接口

```java
/**
 * 决策引擎接口 - 支持离线/在线切换
 */
public interface DecisionEngine {
    
    /**
     * 智能决策 - 优先在线，降级离线
     * 
     * @param context 决策上下文
     * @return 决策结果
     */
    DecisionResult decide(DecisionContext context);
    
    /**
     * 检查 LLM 是否可用
     */
    boolean isLlmAvailable();
    
    /**
     * 设置决策模式
     */
    void setMode(DecisionMode mode);
    
    /**
     * 决策模式枚举
     */
    enum DecisionMode {
        ONLINE_ONLY,     // 仅在线
        OFFLINE_ONLY,    // 仅离线
        ONLINE_FIRST     // 优先在线，降级离线（默认）
    }
}
```

### 4.4 降级策略实现

```java
/**
 * 决策引擎实现
 */
public class DecisionEngineImpl implements DecisionEngine {
    
    private final LlmProvider llmProvider;
    private final RuleEngine ruleEngine;
    private DecisionMode mode = DecisionMode.ONLINE_FIRST;
    
    @Override
    public DecisionResult decide(DecisionContext context) {
        // 根据模式选择决策方式
        switch (mode) {
            case ONLINE_ONLY:
                return decideWithLlm(context);
            case OFFLINE_ONLY:
                return decideWithRules(context);
            case ONLINE_FIRST:
            default:
                return decideWithFallback(context);
        }
    }
    
    private DecisionResult decideWithFallback(DecisionContext context) {
        // 1. 尝试在线决策
        if (llmProvider != null && llmProvider.isAvailable()) {
            try {
                return decideWithLlm(context);
            } catch (Exception e) {
                log.warn("LLM decision failed, fallback to rule engine", e);
            }
        }
        
        // 2. 降级到离线规则
        return decideWithRules(context);
    }
    
    private DecisionResult decideWithLlm(DecisionContext context) {
        String prompt = buildDecisionPrompt(context);
        Map<String, Object> response = llmProvider.chat(model, messages, options);
        return parseDecisionResult(response);
    }
    
    private DecisionResult decideWithRules(DecisionContext context) {
        // 关键词匹配意图
        String intent = matchIntent(context.getQuery());
        // 正则提取参数
        Map<String, Object> params = extractParams(context.getQuery(), intent);
        return new DecisionResult(intent, params);
    }
}
```

---

## 五、知识库价值定位

### 5.1 双重服务模式

```
┌─────────────────────────────────────────────────────────────┐
│  模式1: 为 LLM 服务 (RAG 增强)                              │
│  ────────────────────────────────────────                   │
│  User Query ──▶ LLM ──▶ Knowledge Retrieval ──▶ Response    │
│                                                             │
│  价值:                                                      │
│  • 增强 LLM 的领域知识                                      │
│  • 提供上下文信息                                           │
│  • 减少幻觉                                                 │
├─────────────────────────────────────────────────────────────┤
│  模式2: 为用户服务 (直接检索)                               │
│  ────────────────────────────────────────                   │
│  User Query ──▶ Knowledge Search ──▶ Direct Results         │
│                                                             │
│  价值:                                                      │
│  • 直接提供信息                                             │
│  • 可追溯来源                                               │
│  • 无需 LLM 参与                                            │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 招聘场景知识库应用

| 知识库 | 内容 | 服务对象 | 用途 |
|--------|------|----------|------|
| **公司制度库** | 招聘政策、薪酬标准、入职流程 | LLM | 回答政策问题 |
| **岗位要求库** | 职位描述、技能要求、面试题库 | 用户 + LLM | 筛选和面试 |
| **候选人库** | 简历数据、面试记录、评价结果 | Capability | 筛选和评估 |

### 5.3 知识库与 Capability 关系

```
┌─────────────────────────────────────────────────────────────┐
│  Capability: resume_screening (简历筛选)                    │
│                                                             │
│  知识依赖:                                                  │
│  ├── KB: 岗位要求库 ──▶ 获取筛选标准                        │
│  └── KB: 候选人库 ──▶ 获取简历数据                          │
│                                                             │
│  执行流程:                                                  │
│  1. 从岗位要求库获取筛选条件                                 │
│  2. 从候选人库检索匹配简历                                   │
│  3. (可选) LLM 辅助评估                                      │
│  4. 返回筛选结果                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 六、完整架构设计

### 6.1 架构图

```
┌─────────────────────────────────────────────────────────────┐
│  用户层                                                     │
│  User ──▶ SceneGroup ──▶ SceneAgent                         │
├─────────────────────────────────────────────────────────────┤
│  场景层                                                     │
│  SceneSkill (招聘场景)                                      │
│  ├── Driver Capability (流程驱动)                           │
│  └── Executor Capabilities (能力执行)                       │
├─────────────────────────────────────────────────────────────┤
│  决策层                                                     │
│  ┌─────────────────┐    ┌─────────────────────┐            │
│  │  LLM Decision   │◀──▶│  Rule Engine        │            │
│  │  (在线优先)      │    │  (离线降级)          │            │
│  └─────────────────┘    └─────────────────────┘            │
├─────────────────────────────────────────────────────────────┤
│  能力层                                                     │
│  Tool Registry (工具注册表)                                 │
│  ├── SearchKnowledgeTool                                   │
│  ├── ListDocumentsTool                                     │
│  └── ...                                                   │
├─────────────────────────────────────────────────────────────┤
│  基础层                                                     │
│  ┌───────────────┐  ┌───────────────┐  ┌─────────┐         │
│  │ LLM Provider  │  │ Knowledge Base│  │ Vector  │         │
│  │               │  │               │  │ Store   │         │
│  └───────────────┘  └───────────────┘  └─────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 数据流设计

```
用户请求: "帮我筛选今天的简历"
    │
    ▼
Step 1: 进入场景层
SceneGroup.receive(userRequest)
    └──▶ SceneAgent.process(request)
        └──▶ SceneSkill.handle(request)
    │
    ▼
Step 2: 决策层处理
DecisionEngine.decide(context)
    ├──▶ LLM 可用? ──▶ 是 ──▶ LLM 意图理解
    └──▶ LLM 不可用 ──▶ 规则引擎匹配
    │
    ▼
Step 3: 能力层执行
Capability: resume_screening.execute(params)
    ├──▶ Tool: search_knowledge (获取岗位要求)
    ├──▶ Tool: database_query (查询简历数据)
    └──▶ 业务逻辑处理 (筛选匹配)
    │
    ▼
Step 4: 结果返回
LLM 可用? ──▶ 是 ──▶ LLM 生成自然语言响应
    └──▶ 否 ──▶ 直接返回结构化结果
```

---

## 七、实现计划

### 7.1 Phase 1: 基础设施（P0）

| 任务 | 说明 | 优先级 |
|------|------|--------|
| 决策引擎接口 | 定义 DecisionEngine 接口 | P0 |
| 规则引擎实现 | 实现基于关键词和正则的规则引擎 | P0 |
| LLM Provider 集成 | 集成 agent-sdk 的 LLM Driver | P0 |

### 7.2 Phase 2: 能力集成（P1）

| 任务 | 说明 | 优先级 |
|------|------|--------|
| Tool-Skill 关系打通 | Skill 可注册和使用 Tool | P1 |
| Function Calling 集成 | LLM 与 ToolRegistry 的集成 | P1 |
| 降级策略完善 | 完善降级逻辑和监控 | P1 |

### 7.3 Phase 3: 场景完善（P2）

| 任务 | 说明 | 优先级 |
|------|------|--------|
| 默认场景技能 | 提供开箱即用的场景技能模板 | P2 |
| 更多内置工具 | 计算器、时间、HTTP请求等 | P2 |
| 监控和日志 | 决策过程监控和日志记录 | P2 |

---

## 八、验收标准

### 8.1 功能验收

- [ ] LLM 在线时，意图识别准确率 > 90%
- [ ] LLM 离线时，规则引擎能处理 80% 的常见请求
- [ ] 降级切换时间 < 100ms
- [ ] 知识库检索响应时间 < 500ms

### 8.2 架构验收

- [ ] 决策引擎接口定义完整
- [ ] 支持三种决策模式切换
- [ ] 降级策略可配置
- [ ] 监控指标完整

---

## 九、知识库分层架构设计

### 9.1 三层架构

| 层级 | 名称 | 范围 | 特点 |
|------|------|------|------|
| **Layer 1** | 通用知识层 | 全局共享 | 公司制度、流程规范、FAQ |
| **Layer 2** | 专业模块层 | 领域共享 | HR模块、财务模块、销售模块 |
| **Layer 3** | 场景知识层 | 场景私有 | 招聘场景、培训场景、审批场景 |

### 9.2 层级关系

```
场景知识层 ──▶ 引用 ──▶ 专业模块层 ──▶ 引用 ──▶ 通用知识层

示例：招聘场景
├── 场景知识: 候选人简历、面试记录（场景私有）
├── 专业模块: 岗位要求、面试题库（HR领域共享）
└── 通用知识: 公司制度、招聘政策（全局共享）
```

### 9.3 跨层检索策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| **单层检索** | 仅检索指定层 | 精确场景，如仅查候选人简历 |
| **向下扩展** | 从场景层向下扩展 | 智能问答，优先场景知识 |
| **并行检索** | 同时检索多层，按权重合并 | 综合查询，需要多源知识 |

### 9.4 权限控制

| 层级 | 权限范围 | 访问控制 |
|------|----------|----------|
| 通用知识层 | 全局 | 所有认证用户可读，管理员可写 |
| 专业模块层 | 领域 | 领域角色可读，领域管理员可写 |
| 场景知识层 | 场景 | 场景参与者可访问，场景管理员可管理 |

### 9.5 知识库能力接口

```java
/**
 * 知识库能力接口
 */
public interface KnowledgeCapability extends SceneCapability {
    
    /**
     * 知识检索
     */
    KnowledgeSearchResult search(KnowledgeSearchRequest request);
    
    /**
     * 获取知识库层级
     */
    KnowledgeLayer getLayer();
    
    /**
     * 检查访问权限
     */
    boolean checkAccess(String userId, AccessType accessType);
}

/**
 * 知识库层级枚举
 */
public enum KnowledgeLayer {
    GENERAL("general", "通用知识", Scope.GLOBAL),
    PROFESSIONAL("professional", "专业模块", Scope.DOMAIN),
    SCENE("scene", "场景知识", Scope.SCENE);
}
```

---

## 十、待讨论决策问题

### 10.1 知识库层级的动态扩展

**问题**：是否支持用户自定义知识层级？

| 选项 | 优点 | 缺点 |
|------|------|------|
| 固定三层 | 简单、易管理 | 灵活性不足 |
| 支持扩展 | 灵活、适应性强 | 复杂度增加、权限管理困难 |

**建议**：初期采用固定三层，后续根据需求评估扩展。

---

### 10.2 跨层检索的默认行为

**问题**：用户未指定检索策略时，默认采用哪种策略？

| 选项 | 说明 | 适用场景 |
|------|------|----------|
| 单层检索 | 仅检索当前层 | 明确知道知识位置 |
| 向下扩展 | 从当前层向下 | 不确定知识位置 |
| 并行检索 | 同时检索所有层 | 需要全面信息 |

**建议**：默认采用向下扩展策略，符合"场景优先"原则。

---

### 10.3 知识库与 LLM 的缓存策略

**问题**：LLM 调用知识库后，是否缓存结果？缓存粒度？

| 选项 | 优点 | 缺点 |
|------|------|------|
| 不缓存 | 数据实时 | 性能差、成本高 |
| 查询级缓存 | 性能提升 | 数据可能过期 |
| 文档级缓存 | 平衡性能和实时性 | 实现复杂 |

**建议**：采用文档级缓存，设置合理的过期时间（如5分钟）。

---

### 10.4 专业模块的归属管理

**问题**：专业模块由谁管理？如何避免跨领域访问？

| 选项 | 说明 | 优缺点 |
|------|------|--------|
| 集中管理 | 由系统管理员统一管理 | 简单，但灵活性差 |
| 领域自治 | 各领域管理员管理本领域模块 | 灵活，但需要完善的权限体系 |

**建议**：采用领域自治模式，配合完善的权限审计机制。

---

### 10.5 场景知识的生命周期

**问题**：场景结束后，场景知识如何处理？

| 选项 | 说明 | 适用场景 |
|------|------|----------|
| 立即删除 | 场景结束后删除所有知识 | 敏感数据场景 |
| 归档保留 | 归档到历史库，保留一定时间 | 需要审计追溯的场景 |
| 永久保留 | 不删除，转为历史知识 | 长期价值知识 |

**建议**：采用归档保留策略，保留期限可配置（默认180天）。

---

### 10.6 LLM 不可用时的知识库降级

**问题**：LLM 不可用时，知识库检索结果如何呈现？

| 选项 | 说明 | 用户体验 |
|------|------|----------|
| 直接返回原始结果 | 返回检索到的文档片段 | 信息准确，但可读性差 |
| 模板化呈现 | 使用预定义模板格式化 | 可读性好，但灵活性差 |
| 拒绝服务 | 提示用户稍后重试 | 体验差 |

**建议**：采用模板化呈现，提供基础的可读性。

---

## 十一、LLM 执行模式与规则库设计

### 11.1 LLM 三种工作模式

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| **OFFLINE** | 完全离线，仅依赖规则库 | LLM不可用、数据安全要求高 |
| **DEGRADED** | 规则库优先，LLM增强 | 成本控制、性能优化 |
| **ONLINE** | LLM主导，规则库辅助 | 智能决策、复杂任务 |

### 11.2 执行规则库架构

```
┌─────────────────────────────────────────────────────────────┐
│  规则库存储层                                                │
│  ├── 静态规则库 (预定义) - 业务流程、权限规则、系统约束      │
│  └── 动态规则库 (LLM生成) - 用户定制、场景优化、异常处理     │
│                          │                                  │
│                          ▼                                  │
│  规则执行引擎 (MVEL)                                        │
│  ├── 脚本解析                                               │
│  ├── 上下文注入                                             │
│  ├── 安全沙箱                                               │
│  └── 结果返回                                               │
└─────────────────────────────────────────────────────────────┘
```

### 11.3 规则类型

| 类型 | 说明 | 示例 |
|------|------|------|
| **DECISION** | 决策规则 - 决定下一步执行什么 | 意图路由、能力选择 |
| **TRANSFORM** | 转换规则 - 数据转换 | 参数格式化、字段映射 |
| **VALIDATION** | 验证规则 - 输入验证 | 参数校验、权限检查 |
| **ROUTING** | 路由规则 - 能力路由 | 根据意图选择Capability |
| **FALLBACK** | 降级规则 - 异常处理 | LLM不可用时的处理 |

### 11.4 MVEL 规则脚本示例

```java
// 决策规则: 简历筛选决策
rule "resume_screening_decision" {
    condition: "params.resumes != null && params.criteria != null"
    action: "
        matched = new ArrayList();
        for (r : params.resumes) {
            if (r.experience >= params.criteria.minExperience) {
                matched.add(r);
            }
        }
        result = {'matched': matched, 'count': matched.size()};
    "
}

// 路由规则: 意图路由
rule "intent_routing" {
    condition: "context.query != null"
    action: "
        query = context.query.toLowerCase();
        if (query.contains('筛选')) {
            result = {'capability': 'resume_screening'};
        } else if (query.contains('面试')) {
            result = {'capability': 'interview_schedule'};
        } else {
            result = {'capability': 'unknown', 'needLLM': true};
        }
    "
}
```

### 11.5 用户反馈闭环

```
用户发现问题 → LLM接管流程 → 多轮对话 → 生成新规则 → 持久化 → 验证执行
```

**流程说明**：
1. 用户发现流程问题，通过对话反馈
2. LLM接管流程，理解用户意图
3. 多轮对话确认规则细节
4. LLM生成MVEL规则脚本
5. 持久化到动态规则库
6. 验证新规则并执行

### 11.6 包结构规划

```
net.ooder.scene
├── core/                    # 核心层
│   ├── decision/            # 决策引擎 (新增)
│   │   ├── DecisionEngine.java
│   │   ├── DecisionContext.java
│   │   ├── DecisionResult.java
│   │   └── DecisionMode.java
│   └── ...
├── skill/
│   ├── rule/                # 规则引擎 (新增)
│   │   ├── MvelRuleEngine.java
│   │   ├── RuleScript.java
│   │   ├── RuleRepository.java
│   │   ├── RuleType.java
│   │   └── LlmRuleGenerator.java
│   ├── llm/                 # LLM能力 (已有)
│   ├── knowledge/           # 知识库能力 (已有)
│   └── tool/                # 工具 (已有)
└── ...
```

---

## 十二、风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| LLM 响应延迟 | 用户体验下降 | 异步处理 + 超时降级 |
| LLM 成本过高 | 运营成本增加 | 缓存 + 规则引擎优先 |
| 规则引擎覆盖不足 | 降级后功能受限 | 持续完善规则库 |
| 知识库数据质量 | 检索结果不准确 | 数据治理 + 质量监控 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
