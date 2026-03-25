# 技能与LLM数据生命周期深度分析报告

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **分析对象**: 业务技能与LLM的数据交互全生命周期  
> **核心问题**: 开发者配置、环境构建、用户激活、运行时数据梳理

---

## 一、核心发现总结

### 1.1 数据生命周期四阶段

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     技能与LLM数据生命周期全景图                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Phase 1: 开发者配置阶段          Phase 2: 安装/部署阶段                      │
│  ───────────────────────          ─────────────────────                      │
│  • Skill元数据配置                 • 环境感知数据收集                          │
│  • Knowledge Documents             • 动态知识库构建                           │
│  • RAG Index配置                   • 向量索引初始化                           │
│  • Role/Prompt定义                 • 系统Prompt预编译                         │
│                                                                             │
│  Phase 3: 用户激活阶段            Phase 4: 运行时阶段                         │
│  ─────────────────────            ─────────────────                           │
│  • 用户身份数据关联                 • 会话上下文组装                          │
│  • 个性化知识加载                   • RAG实时检索                             │
│  • 权限上下文构建                   • 记忆存储与召回                          │
│  • 场景状态初始化                   • 工具调用数据流                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 关键数据流

| 阶段 | 数据类型 | 来源 | 去向 | 作用 |
|------|---------|------|------|------|
| 开发配置 | Skill Metadata | 开发者 | Skill Package | 定义技能基础能力 |
| 安装部署 | Environment Context | 系统扫描 | KnowledgeContext | 构建运行时环境 |
| 用户激活 | User Profile | 用户输入 | RoleContext | 个性化配置 |
| 运行时 | Session Memory | 交互过程 | MemoryContext | 保持对话连贯性 |

---

## 二、Phase 1: 开发者配置阶段 - 应该给LLM喂什么数据？

### 2.1 代码分析: SkillsMdLoader

从 `SkillsMdLoader.java` 可以看到开发者需要配置的数据：

```java
// 从 Skill 元数据中读取的关键配置
Object knowledgeDocs = skillPackage.getMetadata().get("knowledgeDocuments");
Object detailedKnowledge = skillPackage.getMetadata().get("detailedKnowledge");
Object ragConfig = skillPackage.getMetadata().get("ragConfig");
```

### 2.2 开发者必须配置的数据清单

#### 2.2.1 基础技能元数据

```yaml
# skill.yaml - 开发者配置
metadata:
  id: skill-recruitment-assistant
  name: 智能招聘助手
  version: 1.0.0
  description: |
    这是一个智能招聘助手，可以帮助HR完成：
    - 简历筛选和分析
    - 面试问题生成
    - 候选人评估
    
  # 核心：技能能力定义
  capabilities:
    - resume_analysis
    - interview_question_generation
    - candidate_evaluation
```

**作用**: 这是LLM理解技能边界的基础数据

#### 2.2.2 知识文档配置 (Knowledge Documents)

```yaml
metadata:
  # 方式1: 内联知识文档
  knowledgeDocuments:
    - |
      ## 招聘流程规范
      1. 简历初筛：检查教育背景、工作经验匹配度
      2. 技术面试：考察专业技能和项目经验
      3. 综合评估：评估文化匹配度和职业发展潜力
      
    - |
      ## 评估维度标准
      - 技术能力 (40%): 专业技能掌握程度
      - 项目经验 (30%): 过往项目复杂度
      - 沟通能力 (20%): 表达清晰度
      - 文化匹配 (10%): 价值观契合度

  # 方式2: 详细知识配置
  detailedKnowledge: |
    # 更详细的领域知识
    本公司招聘标准：
    - 本科及以上学历
    - 3年以上相关经验
    - 熟悉Java/Python至少一种
```

**作用**: 这些文档会被 `SkillsMdLoader.extractKnowledgeFromSkill()` 加载到 `KnowledgeChunk` 中

#### 2.2.3 RAG索引配置

```yaml
metadata:
  ragConfig:
    # RAG索引ID - 指向向量知识库
    indexId: "kb-recruitment-2024"
    
    # 索引类型
    indexType: "vector"
    
    # 关联的数据源
    dataSources:
      - type: "document"
        path: "/docs/job-descriptions/"
      - type: "database"
        table: "historical_candidates"
        
    # 检索参数
    searchParams:
      topK: 5
      similarityThreshold: 0.75
      rerankEnabled: true
```

**作用**: 配置在 `SkillsMdLoader.setupRagIndex()` 中被读取，用于运行时RAG检索

#### 2.2.4 角色和Prompt配置

```yaml
spec:
  roles:
    - name: HR_ASSISTANT
      displayName: 招聘助手
      systemPrompt: |
        你是一位专业的招聘助手，具备以下能力：
        1. 深度理解岗位需求和技术栈要求
        2. 客观评估候选人技能匹配度
        3. 生成针对性的面试问题
        4. 提供结构化的评估报告
        
        评估原则：
        - 保持客观中立，避免偏见
        - 关注实际能力而非学历背景
        - 重视项目经验的具体贡献
        
      # 角色知识增强
      knowledgeEnhancement:
        - "domain:hr_recruitment"
        - "domain:technical_assessment"
```

**作用**: 在 `LlmRuntimeContextAssembler.loadRoleContext()` 中被加载

### 2.3 开发者配置数据流向

```
Developer Config (skill.yaml)
    │
    ├──► SkillPackage (打包时)
    │
    ├──► SkillsMdLoader.load()
    │       ├──► KnowledgeChunk (知识块)
    │       └──► RAG Index ID
    │
    └──► LlmRuntimeContextAssembler.assemble()
            ├──► RoleContext
            ├──► KnowledgeContext  
            └──► SystemPrompt
```

---

## 三、Phase 2: 安装/部署阶段 - 如何构建当前环境？

### 3.1 环境数据收集逻辑

从代码分析，安装阶段需要构建以下数据：

#### 3.1.1 系统环境感知

```java
// LlmRuntimeContextAssembler 中的环境加载逻辑
private KnowledgeContext loadKnowledgeContext(
        String skillId,
        KnowledgeContext.KnowledgeLoadLevel level,
        List<String> knowledgeBaseIds) {
    
    // 1. 从 skills.md 加载知识 (开发者配置)
    CompletableFuture<KnowledgeContext> skillKnowledge = skillsMdLoader.load(skillId, level);
    
    // 2. 加载额外的知识库 (环境特定)
    if (knowledgeBaseIds != null && !knowledgeBaseIds.isEmpty()) {
        // 从环境配置的知识库加载
    }
}
```

#### 3.1.2 安装时应该构建的环境数据

**1. 组织上下文数据**
```yaml
# 安装时自动收集
environment:
  organization:
    name: "某某科技公司"
    industry: "互联网"
    size: "500-1000人"
    
  # 部门结构
  departments:
    - name: "技术部"
      headcount: 150
      skills: ["Java", "Python", "Go"]
    - name: "产品部"  
      headcount: 30
      
  # 现有系统
  systems:
    - name: "OA系统"
      type: "internal"
    - name: "钉钉"
      type: "external"
```

**作用**: 这些数据会被注入到 `KnowledgeContext.searchFilters` 中，用于RAG检索时的过滤

**2. 向量知识库构建**

```java
// 安装时执行的索引构建
public class KnowledgeBaseInstaller {
    
    public void install(SkillPackage skill) {
        // 1. 解析开发者配置的RAG配置
        RagConfig ragConfig = parseRagConfig(skill);
        
        // 2. 扫描数据源
        List<Document> documents = scanDataSources(ragConfig.getDataSources());
        
        // 3. 文档切分
        List<Chunk> chunks = documentSplitter.split(documents);
        
        // 4. 生成向量嵌入
        List<Embedding> embeddings = embeddingModel.embed(chunks);
        
        // 5. 构建索引
        vectorIndex.build(ragConfig.getIndexId(), chunks, embeddings);
    }
}
```

**3. 系统Prompt预编译**

```java
// LlmRuntimeContextAssembler.assembleSystemPrompt()
private String assembleSystemPrompt(RoleContext roleContext, KnowledgeContext knowledgeContext) {
    StringBuilder sb = new StringBuilder();

    // 1. 角色定义 (开发者配置)
    if (roleContext != null) {
        sb.append(roleContext.buildPromptSection());
    }

    // 2. 知识库内容 (安装时加载的环境知识)
    if (knowledgeContext != null) {
        sb.append(knowledgeContext.buildPromptSection());
    }

    return sb.toString().trim();
}
```

### 3.2 安装阶段数据构建流程

```
安装阶段
    │
    ├──► 1. 解析Skill元数据
    │       ├──► RAG配置
    │       ├──► 知识文档
    │       └──► 角色定义
    │
    ├──► 2. 环境数据收集
    │       ├──► 组织信息
    │       ├──► 系统拓扑
    │       └──► 用户目录
    │
    ├──► 3. 知识库构建
    │       ├──► 文档扫描
    │       ├──► 向量化
    │       └──► 索引构建
    │
    └──► 4. Prompt预编译
            ├──► 角色Prompt
            ├──► 知识注入
            └──► 环境上下文
```

---

## 四、Phase 3: 用户激活阶段 - 哪些数据要和用户关联？

### 4.1 用户激活数据流分析

从 `LlmRuntimeContextAssembler.AssemblyRequest` 可以看到用户激活时需要的数据：

```java
public static class AssemblyRequest {
    private String skillId;       // 技能ID
    private String roleId;        // 用户选择的角色
    private String sessionId;     // 会话ID (用户激活时创建)
    private KnowledgeLoadLevel knowledgeLevel;  // 知识加载级别
    private List<String> knowledgeBaseIds;      // 用户可访问的知识库
}
```

### 4.2 用户激活时需要关联的数据

#### 4.2.1 用户身份与权限数据

```yaml
# 用户激活时注入
userActivation:
  userId: "user_12345"
  
  # 用户角色 (与Skill角色映射)
  roles:
    - orgRole: "HR_MANAGER"      # 组织角色
      skillRole: "MANAGER"       # 技能内角色
    - orgRole: "TECH_LEAD"
      skillRole: "LEADER"
      
  # 权限范围
  permissions:
    - resource: "candidate_data"
      actions: ["read", "write"]
    - resource: "interview_records"  
      actions: ["read"]
      
  # 数据访问范围
  dataScope:
    departments: ["技术部", "产品部"]
    timeRange: "last_6_months"
```

**代码映射**:
```java
// RoleContext 构建时会考虑用户权限
private RoleContext loadRoleContext(String roleId, SkillPackage skillPackage) {
    // 1. 尝试从请求的角色 ID 加载 (用户选择)
    if (roleId != null && !roleId.isEmpty()) {
        RoleContext role = RoleContext.load(roleId);
        if (role != null) {
            return role;
        }
    }
    
    // 2. 尝试从 Skill 元数据中获取默认角色
    Object defaultRole = skillPackage.getMetadata().get("defaultRole");
    // ...
}
```

#### 4.2.2 个性化知识加载

```java
// KnowledgeContext 支持用户特定的知识库
public class KnowledgeContext {
    private List<String> accessibleKnowledgeBases;  // 用户可访问的知识库列表
    private Map<String, Object> searchFilters;      // 用户特定的搜索过滤条件
}

// 用户激活时设置
KnowledgeContext context = new KnowledgeContext();
context.addAccessibleKnowledgeBase("kb-user-" + userId);  // 用户个人知识库
context.addSearchFilter("department", user.getDepartment());  // 部门过滤
context.addSearchFilter("created_by", user.getId());  // 创建者过滤
```

#### 4.2.3 用户历史数据关联

```java
// MemoryContext 加载用户历史
private MemoryContext loadMemoryContext(String sessionId) {
    MemoryContext memory = new MemoryContext();
    memory.setSessionId(sessionId);
    
    // 从持久化存储加载用户历史消息
    List<Map<String, Object>> history = sessionService.getHistory(sessionId);
    memory.setHistory(history);
    
    // 加载用户偏好
    UserPreferences prefs = userService.getPreferences(userId);
    memory.setPreferences(prefs);
    
    return memory;
}
```

### 4.3 用户激活数据关联图

```
用户激活
    │
    ├──► 身份认证
    │       └──► 用户ID + 组织角色
    │
    ├──► 权限计算
    │       ├──► Skill角色映射
    │       ├──► 数据访问范围
    │       └──► 功能权限
    │
    ├──► 知识库关联
    │       ├──► 个人知识库
    │       ├──► 部门知识库
    │       └──► 公共知识库
    │
    ├──► 历史数据加载
    │       ├──► 会话历史
    │       ├──► 用户偏好
    │       └──► 行为模式
    │
    └──► 上下文组装
            ├──► RoleContext (含权限)
            ├──► KnowledgeContext (含过滤)
            └──► MemoryContext (含历史)
```

---

## 五、Phase 4: 运行时阶段 - 哪些数据要进行梳理？

### 5.1 运行时数据流全景

```
用户输入
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│                    LLM Runtime Context                       │
├─────────────────────────────────────────────────────────────┤
│  1. 上下文组装 (LlmRuntimeContextAssembler)                  │
│     ├──► RoleContext → SystemPrompt                         │
│     ├──► KnowledgeContext → RAG检索 → Prompt增强            │
│     ├──► FunctionContext → Tools定义                        │
│     └──► MemoryContext → Message历史                        │
│                                                              │
│  2. LLM调用                                                  │
│     ├──► 组装完整Prompt                                      │
│     ├──► 调用LLM API                                         │
│     └──► 接收响应                                            │
│                                                              │
│  3. 后置处理                                                 │
│     ├──► 工具调用解析                                        │
│     ├──► 结果执行                                            │
│     └──► 响应生成                                            │
│                                                              │
│  4. 数据沉淀                                                 │
│     ├──► 会话记忆存储                                        │
│     ├──► 知识反馈 (可选)                                     │
│     └──► 用户行为记录                                        │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
响应输出
```

### 5.2 运行时关键数据梳理

#### 5.2.1 每次请求都要梳理的数据

**1. 动态上下文组装**

```java
// LlmRuntimeContextAssembler.assemble()
public CompletableFuture<LlmSceneContext> assemble(AssemblyRequest request) {
    
    // 每次请求都重新组装：
    
    // 1. 角色上下文 (可能根据用户权限动态变化)
    RoleContext roleContext = loadRoleContext(request.getRoleId(), skillPackage);
    
    // 2. 知识上下文 (根据查询动态检索)
    KnowledgeContext knowledgeContext = loadKnowledgeContext(
        request.getSkillId(), 
        request.getKnowledgeLevel(),
        request.getKnowledgeBaseIds()
    ).get();
    
    // 3. 函数上下文 (根据当前状态动态选择可用工具)
    FunctionContext functionContext = FunctionContext.loadFromSkill(
        request.getSkillId(), 
        skillPackage
    );
    
    // 4. 记忆上下文 (加载最新会话历史)
    MemoryContext memoryContext = loadMemoryContext(request.getSessionId());
}
```

**2. RAG实时检索数据**

```java
// KnowledgeContext 运行时检索
public class KnowledgeContext {
    
    // 运行时根据用户查询动态检索
    public List<KnowledgeChunk> search(String query) {
        // 1. 向量相似度检索
        List<Embedding> queryEmbedding = embeddingModel.embed(query);
        List<SearchResult> results = vectorIndex.search(
            queryEmbedding, 
            maxResults, 
            similarityThreshold
        );
        
        // 2. 应用用户特定的过滤条件
        results = applyFilters(results, searchFilters);
        
        // 3. 转换为KnowledgeChunk
        return results.stream()
            .map(r -> new KnowledgeChunk(r.getContent()))
            .collect(Collectors.toList());
    }
}
```

#### 5.2.2 需要持久化的数据

**1. 会话记忆存储**

```java
// 每次交互后保存
public void saveInteraction(String sessionId, Message message) {
    // 保存到MemoryContext
    MemoryContext memory = memoryStore.get(sessionId);
    memory.addMessage(message);
    
    // 持久化到存储
    sessionService.saveHistory(sessionId, memory.getHistory());
    
    // 更新用户画像 (用于个性化)
    userProfileService.updateInteractionPattern(sessionId, message);
}
```

**2. 知识反馈 (可选)**

```java
// 用户反馈有价值的对话可以沉淀到知识库
public void feedbackToKnowledge(String sessionId, MessagePair interaction) {
    if (interaction.isMarkedAsUseful()) {
        // 提取知识
        KnowledgeChunk chunk = extractKnowledge(interaction);
        
        // 添加到用户个人知识库
        knowledgeBaseService.addChunk("kb-user-" + userId, chunk);
        
        // 可选：添加到公共知识库 (需要审核)
        knowledgeReviewService.submit(chunk);
    }
}
```

### 5.3 运行时数据梳理清单

| 数据类型 | 梳理时机 | 处理方式 | 存储位置 |
|---------|---------|---------|---------|
| 用户输入 | 每次请求 | 预处理、向量化 | 临时内存 |
| RAG检索结果 | 每次请求 | 相关性排序、过滤 | 临时内存 |
| LLM响应 | 每次请求 | 解析、验证 | 临时内存 |
| 工具调用 | 按需 | 执行、结果收集 | 临时内存 |
| 会话历史 | 每次交互后 | 截断、摘要 | 持久化存储 |
| 用户反馈 | 用户操作后 | 知识提取 | 知识库 |
| 行为日志 | 异步 | 批量处理 | 数据仓库 |

---

## 六、用户故事与代码对照

### 6.1 用户故事1: HR使用招聘助手筛选简历

```
用户故事:
作为HR经理，我需要上传一份简历，让AI助手帮我分析匹配度，
这样我可以快速筛选出合适的候选人。
```

**数据流对照**:

```java
// Phase 1: 开发者配置
// skill-recruitment-assistant/skill.yaml
metadata:
  knowledgeDocuments:
    - "## 简历评估标准\n技术能力40%、项目经验30%..."
  
  ragConfig:
    indexId: "kb-job-descriptions"  // 职位描述知识库

// Phase 2: 安装部署
// 系统扫描组织环境，构建索引
KnowledgeBaseInstaller.install(skill);
// - 扫描现有职位描述文档
// - 构建向量索引 kb-job-descriptions

// Phase 3: 用户激活
// HR经理激活技能
AssemblyRequest request = AssemblyRequest.builder()
    .skillId("skill-recruitment-assistant")
    .roleId("HR_MANAGER")  // 用户角色
    .sessionId("session-hr-001")
    .knowledgeLevel(KnowledgeLoadLevel.ADVANCED)
    .build();

// Phase 4: 运行时
// 用户上传简历
String resume = userUploadResume();

// 4.1 组装上下文
LlmSceneContext context = assembler.assemble(request).get();
// - RoleContext: HR_MANAGER角色定义
// - KnowledgeContext: 加载招聘知识 + RAG检索职位要求
// - FunctionContext: 简历分析工具

// 4.2 RAG检索相关职位要求
KnowledgeContext knowledge = context.getKnowledgeContext();
List<KnowledgeChunk> relevantJobs = knowledge.search(resume);

// 4.3 构建Prompt
String systemPrompt = context.getSystemPrompt() + 
    "\n\n相关职位要求:\n" + formatChunks(relevantJobs);

// 4.4 调用LLM
LlmResponse response = llmService.chat(systemPrompt, resume);

// 4.5 保存会话
memoryContext.addMessage(userMessage);
memoryContext.addMessage(response);
sessionService.saveHistory(sessionId, memoryContext.getHistory());
```

### 6.2 用户故事2: 员工查询公司政策

```
用户故事:
作为新员工，我想询问公司的请假政策，
AI助手应该基于最新的员工手册给出准确回答。
```

**数据流对照**:

```java
// Phase 1: 开发者配置
// 知识库技能
metadata:
  ragConfig:
    indexId: "kb-employee-handbook"
    dataSources:
      - type: "document"
        path: "/docs/employee-handbook/"
        autoSync: true  // 自动同步更新

// Phase 2: 安装部署
// 构建员工手册知识库
// - 扫描所有政策文档
// - 建立版本控制

// Phase 3: 用户激活
// 普通员工激活
AssemblyRequest request = AssemblyRequest.builder()
    .skillId("skill-knowledge-base")
    .roleId("EMPLOYEE")  // 普通员工角色
    .knowledgeBaseIds(["kb-employee-handbook"])
    .build();

// 权限过滤：只能访问公开政策
KnowledgeContext knowledge = new KnowledgeContext();
knowledge.addSearchFilter("visibility", "public");
knowledge.addSearchFilter("department", user.getDepartment());

// Phase 4: 运行时
// 用户提问
String query = "请问年假有多少天？";

// 4.1 RAG检索 (带权限过滤)
List<KnowledgeChunk> results = knowledge.search(query);
// 自动过滤掉：
// - 机密政策 (高管专属)
// - 其他部门政策
// - 已过期政策

// 4.2 生成回答
String answer = generateAnswer(results);

// 4.3 记录查询 (用于分析热点问题)
analyticsService.recordQuery(query, answer, user.getId());
```

---

## 七、关键结论与建议

### 7.1 数据生命周期核心要点

| 阶段 | 核心问题 | 答案 |
|------|---------|------|
| **开发者配置** | 给LLM喂什么数据？ | Skill元数据、知识文档、RAG配置、角色定义 |
| **安装部署** | 如何构建环境？ | 扫描组织上下文、构建向量索引、预编译Prompt |
| **用户激活** | 哪些数据要关联？ | 用户身份、权限范围、个人知识库、历史数据 |
| **运行时** | 哪些数据要梳理？ | 动态上下文组装、RAG实时检索、会话记忆持久化 |

### 7.2 架构设计建议

**1. 分层数据管理**
```
全局数据 (Global)
  └── 所有用户共享：公司政策、通用知识
  
组织数据 (Organization)  
  └── 部门共享：部门流程、项目文档
  
个人数据 (Personal)
  └── 用户私有：个人笔记、历史会话
```

**2. 数据流动原则**
- **开发时**: 定义数据结构，不填具体值
- **安装时**: 填充环境数据，构建索引
- **激活时**: 关联用户数据，计算权限
- **运行时**: 动态组装，实时检索，及时沉淀

**3. 数据安全边界**
```java
// 每个阶段都有明确的数据访问控制
public interface DataAccessControl {
    // 开发时：开发者只能访问自己Skill的数据
    boolean canAccessDevData(String developerId, String skillId);
    
    // 安装时：系统管理员可以访问组织数据
    boolean canAccessOrgData(String installerId, String orgId);
    
    // 运行时：用户只能访问授权范围内的数据
    boolean canAccessUserData(String userId, String dataId);
}
```

### 7.3 实施路线图

**短期 (1-2周)**:
- [ ] 完善Skill元数据结构，支持knowledgeDocuments和ragConfig
- [ ] 实现SkillsMdLoader的知识加载逻辑
- [ ] 构建基础的知识库安装器

**中期 (1月)**:
- [ ] 实现用户激活时的权限计算和知识库关联
- [ ] 完善RAG实时检索和过滤机制
- [ ] 建立会话记忆的持久化存储

**长期 (3月)**:
- [ ] 实现知识反馈闭环，支持用户贡献知识
- [ ] 建立数据血缘追踪，实现全链路可观测
- [ ] 优化数据分层和缓存策略

---

## 八、附录

### 8.1 核心类图

```
┌─────────────────────────────────────────────────────────────────┐
│                        Data Lifecycle Classes                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Developer Phase                    Installation Phase         │
│  ───────────────                    ─────────────────          │
│  SkillPackage                       KnowledgeBaseInstaller       │
│  ├── metadata                       ├── scanEnvironment()        │
│  │   ├── knowledgeDocuments         ├── buildVectorIndex()       │
│  │   ├── ragConfig                  └── precompilePrompts()      │
│  │   └── roles                                                    │
│  └── skills.md                                                    │
│                                                                  │
│  Activation Phase                   Runtime Phase                │
│  ───────────────                    ─────────────                │
│  LlmRuntimeContextAssembler         LlmRuntimeContext            │
│  ├── loadRoleContext()              ├── RoleContext              │
│  ├── loadKnowledgeContext()         ├── KnowledgeContext         │
│  ├── loadFunctionContext()          ├── FunctionContext          │
│  └── loadMemoryContext()            └── MemoryContext            │
│                                                                  │
│  Data Contexts                                                    │
│  ─────────────                                                    │
│  KnowledgeContext                   MemoryContext                │
│  ├── search()                       ├── getHistory()             │
│  ├── addChunk()                     ├── addMessage()             │
│  └── buildPromptSection()           └── saveToPersistent()       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 8.2 参考文档

| 文档 | 路径 |
|------|------|
| SkillsMdLoader | `scene-engine/src/main/java/.../SkillsMdLoader.java` |
| LlmRuntimeContextAssembler | `scene-engine/src/main/java/.../LlmRuntimeContextAssembler.java` |
| KnowledgeContext | `scene-engine/src/main/java/.../KnowledgeContext.java` |
| LlmRuntimeContext | `scene-engine/src/main/java/.../LlmRuntimeContext.java` |

---

**分析完成日期**: 2026-03-11  
**分析人**: Engine Team  
**状态**: ✅ 完成
