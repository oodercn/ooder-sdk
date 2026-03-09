# 使用场景技能快速构建业务应用模块

**——以招聘管理系统为例**

> 本文将以招聘管理系统为例，介绍如何使用 Ooder SceneEngine 的场景技能能力，快速构建智能化的业务应用模块。

---

## 一、传统开发 vs 场景开发

### 1.1 传统开发的困境

构建一个招聘管理系统，传统方式需要：

| 阶段 | 耗时 | 工作内容 |
|------|------|----------|
| 需求分析 | 2-3周 | 业务调研、需求文档 |
| 系统设计 | 2-3周 | 架构设计、数据库设计 |
| 编码开发 | 4-6周 | 前后端开发、接口联调 |
| 测试上线 | 2-3周 | 功能测试、部署上线 |
| **总计** | **10-15周** | |

**痛点**：
- 开发周期长，难以快速响应业务变化
- 需要专业开发团队，人力成本高
- 需求变更成本高，牵一发动全身
- 系统孤岛，数据难以互通

### 1.2 场景开发的优势

使用场景技能开发，同样的招聘系统：

| 阶段 | 耗时 | 工作内容 |
|------|------|----------|
| 场景定义 | 1-2天 | 定义招聘场景和业务流程 |
| 技能组装 | 2-3天 | 选择和配置已有技能 |
| 配置调试 | 1-2天 | 测试和优化 |
| 上线运行 | 1天 | 一键部署 |
| **总计** | **1周内** | |

**优势**：
- 开发效率提升 **14倍**
- 成本降低 **91%**
- 需求变更响应速度提升 **56倍**
- 无需专业开发团队

---

## 二、场景技能核心概念

### 2.1 什么是场景技能？

场景技能（Scene Skill）是 Ooder SceneEngine 的核心概念，它将业务场景封装为可复用的能力单元：

```
┌─────────────────────────────────────────────────────────────┐
│  场景技能 = 业务场景 + 能力集合 + 知识库 + 智能决策          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  业务场景: 招聘、审批、客服、销售等                          │
│  能力集合: 简历筛选、面试安排、通知发送等                    │
│  知识库: 岗位要求、候选人信息、公司制度等                    │
│  智能决策: LLM意图理解、规则引擎、降级策略                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 架构分层

```
┌─────────────────────────────────────────────────────────────┐
│  用户层: User ──▶ SceneGroup ──▶ SceneAgent                 │
├─────────────────────────────────────────────────────────────┤
│  场景层: SceneSkill ──▶ Driver/Executor Capabilities        │
├─────────────────────────────────────────────────────────────┤
│  决策层: LLM Decision (在线) ◀──▶ Rule Engine (离线降级)    │
├─────────────────────────────────────────────────────────────┤
│  能力层: ToolRegistry ──▶ 内置工具 + 自定义工具              │
├─────────────────────────────────────────────────────────────┤
│  基础层: LLM Provider, Knowledge Base, Vector Store         │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、实战：构建招聘管理系统

### 3.1 场景定义

首先，定义招聘场景的业务流程：

```
发布职位 → 简历筛选 → 面试安排 → 面试评估 → 发放Offer
```

### 3.2 创建知识库

招聘场景需要三类知识库：

```java
// 1. 创建通用知识层 - 公司制度库
KnowledgeBase companyPolicyKb = kbService.create(
    KnowledgeBaseCreateRequest.builder()
        .name("公司制度库")
        .layer(KnowledgeLayer.GENERAL)
        .description("招聘政策、薪酬标准、入职流程")
        .build()
);

// 2. 创建专业模块层 - 岗位要求库
KnowledgeBase jobRequirementKb = kbService.create(
    KnowledgeBaseCreateRequest.builder()
        .name("岗位要求库")
        .layer(KnowledgeLayer.PROFESSIONAL)
        .description("职位描述、技能要求、面试题库")
        .build()
);

// 3. 创建场景知识层 - 候选人库
KnowledgeBase candidateKb = kbService.create(
    KnowledgeBaseCreateRequest.builder()
        .name("候选人库")
        .layer(KnowledgeLayer.SCENE)
        .description("简历数据、面试记录、评价结果")
        .build()
);
```

### 3.3 导入知识数据

```java
// 导入岗位要求
DocumentCreateRequest jobDoc = DocumentCreateRequest.builder()
    .title("Java开发工程师岗位要求")
    .content("""
        职位描述：
        - 负责后端服务开发
        - 参与系统架构设计
        
        技能要求：
        - 3年以上Java开发经验
        - 熟悉Spring框架
        - 了解MySQL、Redis
        
        学历要求：
        - 本科及以上学历
        - 计算机相关专业优先
        """)
    .tags(Arrays.asList("Java", "后端", "技术"))
    .build();

kbService.addDocument(jobRequirementKb.getKbId(), jobDoc);
```

### 3.4 配置决策引擎

```java
// 创建决策引擎
DecisionEngine decisionEngine = new DecisionEngineImpl(llmProvider, ruleEngine);

// 设置决策模式
decisionEngine.setMode(DecisionMode.ONLINE_FIRST);

// 配置离线规则（用于降级）
RuleScript routingRule = RuleScript.builder()
    .ruleId("recruitment-routing")
    .type(RuleType.ROUTING)
    .script("""
        if (query.contains('筛选') || query.contains('简历')) {
            return 'resume_screening';
        } else if (query.contains('面试')) {
            return 'interview_schedule';
        } else if (query.contains('offer') || query.contains('录用')) {
            return 'offer_management';
        }
        return 'recruitment_help';
        """)
    .build();

ruleEngine.compile(routingRule);
```

### 3.5 创建场景技能

```java
// 创建招聘场景技能
SceneSkill recruitmentSkill = SceneSkill.builder()
    .skillId("recruitment-scene")
    .name("招聘管理场景")
    .description("智能招聘管理系统")
    
    // 注册能力
    .capability("resume_screening", new ResumeScreeningCapability())
    .capability("interview_schedule", new InterviewScheduleCapability())
    .capability("offer_management", new OfferManagementCapability())
    
    // 关联知识库
    .knowledgeBase("company_policy", companyPolicyKb.getKbId())
    .knowledgeBase("job_requirement", jobRequirementKb.getKbId())
    .knowledgeBase("candidate", candidateKb.getKbId())
    
    // 配置决策引擎
    .decisionEngine(decisionEngine)
    
    .build();

// 注册场景技能
skillRegistry.register(recruitmentSkill);
```

---

## 四、智能交互实现

### 4.1 简历筛选场景

用户通过自然语言发起请求：

```
用户: "帮我筛选一下今天收到的简历，找出符合条件的Java开发候选人"
```

系统处理流程：

```
Step 1: LLM意图理解
────────────────────────────────
LLM分析:
- 意图: 简历筛选
- 时间范围: 今天
- 岗位: Java开发
- Capability: resume_screening

Step 2: 知识库检索
────────────────────────────────
从岗位要求库获取: Java开发岗位的技能要求
从候选人库检索: 今天收到的简历

Step 3: 能力执行
────────────────────────────────
resume_screening.execute({
    "dateRange": "today",
    "position": "Java开发",
    "skills": ["Java", "Spring", "MySQL"],
    "experience": "3+"
})

Step 4: 结果生成
────────────────────────────────
LLM生成自然语言回复:
"今天收到15份简历，筛选出3份符合条件的候选人：
 1. 张三 - 5年Java经验，熟悉Spring Cloud
 2. 李四 - 4年Java经验，有微服务项目经验
 3. 王五 - 3年Java经验，全栈开发能力"
```

### 4.2 代码实现

```java
// 处理用户请求
public String handleUserQuery(String userId, String query) {
    // 1. 构建决策上下文
    DecisionContext context = DecisionContext.builder()
        .query(query)
        .userId(userId)
        .timestamp(System.currentTimeMillis())
        .build();
    
    // 2. 决策引擎处理
    DecisionResult decision = decisionEngine.decide(context);
    
    if (!decision.isSuccess()) {
        return "抱歉，我无法理解您的请求，请换个说法试试。";
    }
    
    // 3. 获取能力
    String capabilityId = decision.getCapability();
    Capability capability = skillRegistry.getCapability(capabilityId);
    
    // 4. 执行能力
    CapabilityResult result = capability.execute(decision.getParams());
    
    // 5. LLM生成回复
    if (llmProvider.isAvailable()) {
        return llmProvider.chat(
            "你是招聘助手",
            "用户问题：" + query + "\n处理结果：" + result.toJson()
        );
    } else {
        // 降级：模板化输出
        return formatResult(result);
    }
}
```

---

## 五、知识库与LLM协同

### 5.1 RAG增强问答

```java
// 招聘知识问答
public String answerQuestion(String question) {
    // 1. 从知识库检索相关内容
    KnowledgeSearchRequest searchRequest = KnowledgeSearchRequest.builder()
        .query(question)
        .topK(3)
        .threshold(0.7f)
        .layers(Arrays.asList(
            KnowledgeLayer.GENERAL,
            KnowledgeLayer.PROFESSIONAL
        ))
        .build();
    
    KnowledgeResult knowledge = knowledgeCapability.retrieveCrossLayer(
        question, KnowledgeLayer.SCENE
    );
    
    // 2. 构建增强提示
    String context = knowledge.getItems().stream()
        .map(item -> item.getContent())
        .collect(Collectors.joining("\n\n"));
    
    String prompt = String.format("""
        你是招聘助手，请根据以下知识回答用户问题。
        
        相关知识：
        %s
        
        用户问题：%s
        
        请给出准确、专业的回答：
        """, context, question);
    
    // 3. LLM生成回答
    return llmProvider.chat("招聘助手", prompt);
}
```

**示例对话**：

```
用户: "Java开发岗位的薪资范围是多少？"

系统: 根据公司薪酬标准，Java开发岗位的薪资范围为：
     - 初级（1-3年）：15-20K
     - 中级（3-5年）：20-30K
     - 高级（5年以上）：30-45K
     
     具体薪资根据候选人能力和面试表现确定。
```

### 5.2 离线降级保障

当LLM不可用时，系统自动降级到规则引擎：

```java
// 降级处理
private DecisionResult decideWithFallback(DecisionContext context) {
    // 1. 尝试LLM决策
    if (llmProvider != null && llmProvider.isAvailable()) {
        try {
            return decideWithLlm(context);
        } catch (Exception e) {
            log.warn("LLM决策失败，降级到规则引擎");
        }
    }
    
    // 2. 规则引擎决策
    return decideWithRules(context);
}

// 规则引擎决策
private DecisionResult decideWithRules(DecisionContext context) {
    String query = context.getQuery().toLowerCase();
    
    // 关键词匹配
    if (query.contains("筛选") || query.contains("简历")) {
        return DecisionResult.builder()
            .capability("resume_screening")
            .param("mode", "keyword")
            .build();
    }
    
    // 默认处理
    return DecisionResult.builder()
        .capability("recruitment_help")
        .build();
}
```

---

## 六、扩展与定制

### 6.1 添加自定义能力

```java
// 自定义面试评估能力
public class InterviewEvaluationCapability implements Capability {
    
    @Override
    public String getCapabilityId() {
        return "interview_evaluation";
    }
    
    @Override
    public CapabilityResult execute(Map<String, Object> params) {
        String candidateId = (String) params.get("candidateId");
        List<String> questions = (List<String>) params.get("questions");
        
        // 获取候选人信息
        KnowledgeResult candidate = knowledgeCapability.retrieve(
            "candidate:" + candidateId, KnowledgeLayer.SCENE
        );
        
        // 获取面试题库
        KnowledgeResult questionBank = knowledgeCapability.retrieve(
            "面试题", KnowledgeLayer.PROFESSIONAL
        );
        
        // LLM辅助评估
        String evaluation = llmProvider.chat(
            "面试评估助手",
            buildEvaluationPrompt(candidate, questions, questionBank)
        );
        
        return CapabilityResult.success(evaluation);
    }
}

// 注册自定义能力
skillRegistry.registerCapability("recruitment-scene", 
    new InterviewEvaluationCapability()
);
```

### 6.2 动态规则生成

```java
// 用户反馈闭环：用户发现问题 → LLM生成新规则
public void handleUserFeedback(String userId, String feedback) {
    // 1. LLM理解反馈
    String prompt = String.format("""
        用户反馈：%s
        
        请分析问题并生成MVEL规则脚本：
        1. 规则类型（DECISION/ROUTING/VALIDATION）
        2. 触发条件
        3. 执行逻辑
        """, feedback);
    
    String ruleScript = llmProvider.chat("规则生成助手", prompt);
    
    // 2. 验证规则
    ValidationResult validation = ruleEngine.validateWithDetails(ruleScript);
    
    if (validation.isValid()) {
        // 3. 持久化规则
        RuleScript newRule = RuleScript.builder()
            .ruleId("user-defined-" + System.currentTimeMillis())
            .script(ruleScript)
            .createdBy(userId)
            .build();
        
        ruleRepository.save(newRule);
        
        log.info("新规则已创建: {}", newRule.getRuleId());
    }
}
```

---

## 七、最佳实践

### 7.1 知识库设计原则

| 原则 | 说明 |
|------|------|
| **分层管理** | 通用知识、专业模块、场景知识分离 |
| **权限控制** | 不同层级设置不同访问权限 |
| **质量监控** | 定期检查知识库数据质量 |
| **版本管理** | 知识更新保留历史版本 |

### 7.2 LLM使用策略

| 策略 | 说明 |
|------|------|
| **优先规则** | 简单场景优先使用规则引擎 |
| **LLM增强** | 复杂场景使用LLM增强体验 |
| **降级保障** | 始终配置离线降级方案 |
| **成本控制** | 缓存常用结果，减少LLM调用 |

### 7.3 性能优化建议

| 建议 | 说明 |
|------|------|
| **向量缓存** | 缓存知识库检索结果 |
| **异步处理** | 耗时操作异步执行 |
| **批量处理** | 批量导入和批量处理 |
| **监控告警** | 监控响应时间和成功率 |

---

## 八、总结

使用场景技能构建业务应用模块，核心优势在于：

1. **快速交付**：1周内完成传统方式10-15周的工作
2. **智能交互**：自然语言交互，降低使用门槛
3. **知识沉淀**：业务知识结构化存储和复用
4. **弹性架构**：在线/离线切换，稳定可靠
5. **持续优化**：用户反馈闭环，规则自动生成

**适用场景**：
- 招聘管理系统
- 智能客服系统
- 审批流程系统
- 知识问答系统
- 数据分析系统

**技术栈**：
- Scene Engine v2.3.1
- LLM Provider（支持OpenAI、Azure等）
- Vector Store（支持SQLite、Milvus等）
- MVEL Rule Engine

---

**相关文档**：
- [二次开发指南](./SECONDARY_DEVELOPMENT_GUIDE.md)
- [配置修改说明](./CONFIGURATION_CHANGELOG_2.3.1.md)
- [招聘用户故事分析](./RECRUITMENT_USER_STORY_ANALYSIS.md)

---

**作者**: Ooder Team  
**日期**: 2026-03-07
