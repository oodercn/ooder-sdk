# Skills 应用开发任务需求及技术指南

**文档版本**: v1.0  
**发布日期**: 2026-03-06  
**目标团队**: Skills 应用开发团队  
**依赖版本**: scene-engine v2.3

---

## 一、开发任务总览

基于用户知识系统技术方案中的5个用户故事，分解为以下开发任务：

| 优先级 | 任务编号 | 任务名称 | 用户故事 | 预计工时 | 依赖 |
|--------|----------|----------|----------|----------|------|
| P0 | TASK-001 | 智能文档助手 Skill | 故事1 | 5天 | scene-engine v2.3 |
| P0 | TASK-002 | 会议纪要整理 Skill | 故事2 | 4天 | TASK-001 |
| P1 | TASK-003 | 知识共享管理 Skill | 故事3 | 4天 | scene-engine v2.3 |
| P1 | TASK-004 | 新人培训助手 Skill | 故事4 | 3天 | TASK-001, TASK-003 |
| P2 | TASK-005 | 项目知识沉淀 Skill | 故事5 | 5天 | TASK-001, TASK-002 |

---

## 二、详细开发任务

### TASK-001: 智能文档助手 Skill (P0)

#### 2.1.1 任务描述
开发一个企业文档智能问答 Skill，支持员工通过自然语言查询公司制度、流程文档。

#### 2.1.2 功能需求

**核心功能**:
1. 文档上传与处理
   - 支持 PDF、Word、Excel、Markdown 格式
   - 自动文档分块（按段落/章节）
   - 自动向量化并建立索引

2. 智能问答
   - 自然语言提问理解
   - 基于 RAG 的检索增强回答
   - 答案来源标注
   - 多轮对话支持

3. 知识库管理
   - 个人/部门知识库创建
   - 文档版本管理
   - 索引重建

#### 2.1.3 技术实现

**Skill 类型**: ABS (Agent Business Skill)

**核心类设计**:
```java
/**
 * 智能文档助手 Skill
 * 
 * @sceneSkill true
 * @sceneCategory ABS
 * @capabilitySubType KNOWLEDGE_RETRIEVAL
 */
@Component
public class DocumentAssistantSkill implements RichSkill {
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    private RagPipeline ragPipeline;
    
    @Autowired
    private ConversationService conversationService;
    
    /**
     * 处理文档查询请求
     * 
     * @mainFirst true
     * @driverCondition userQuery != null
     */
    @Tool(name = "queryDocument", description = "查询文档内容")
    public QueryResult queryDocument(
            @ToolParam String kbId,
            @ToolParam String query,
            @ToolParam(required = false) Integer topK) {
        
        // 1. 检索相关知识
        RagContext context = RagContext.builder()
            .kbId(kbId)
            .query(query)
            .topK(topK != null ? topK : 5)
            .build();
        
        RagResult ragResult = ragPipeline.retrieve(context);
        
        // 2. 生成回答
        String answer = ragPipeline.generate(query, ragResult);
        
        // 3. 构建结果
        return QueryResult.builder()
            .answer(answer)
            .sources(ragResult.getSources())
            .confidence(ragResult.getConfidence())
            .build();
    }
    
    /**
     * 上传并处理文档
     * 
     * @driverCondition file != null
     */
    @Tool(name = "uploadDocument", description = "上传文档到知识库")
    public Document uploadDocument(
            @ToolParam String userId,
            @ToolParam String kbId,
            @ToolParam File file,
            @ToolParam(required = false) Map<String, Object> metadata) {
        
        // 1. 保存文档
        Document doc = knowledgeBaseService.uploadDocument(
            userId, kbId, file,
            DocumentMetadata.builder()
                .filename(file.getName())
                .customMetadata(metadata)
                .build()
        );
        
        // 2. 异步处理文档
        processDocumentAsync(doc);
        
        return doc;
    }
    
    @Async
    private void processDocumentAsync(Document doc) {
        // 文档分块、向量化、索引建立
        documentChunker.chunkAndIndex(doc);
    }
}
```

**配置要求**:
```yaml
skill:
  name: document-assistant
  version: 1.0.0
  category: ABS
  capabilities:
    - KNOWLEDGE_RETRIEVAL
    - DOCUMENT_PROCESSING
  participants:
    - role: USER
      permissions: [READ, WRITE]
    - role: ADMIN
      permissions: [READ, WRITE, DELETE]
  driverConditions:
    - userQuery != null
    - kbId != null
  visibility: PUBLIC
```

#### 2.1.4 验收标准

- [ ] 支持 PDF、Word、Excel、Markdown 上传
- [ ] 文档处理时间 < 30秒（10MB以内）
- [ ] 问答响应时间 < 3秒
- [ ] 答案准确率 > 85%
- [ ] 支持多轮对话上下文保持

---

### TASK-002: 会议纪要整理 Skill (P0)

#### 2.2.1 任务描述
开发会议内容智能整理 Skill，自动提取关键决策、行动项，并归档到知识库。

#### 2.2.2 功能需求

**核心功能**:
1. 会议内容输入
   - 支持语音转文字输入
   - 支持文本粘贴输入
   - 支持文件上传（录音、速记）

2. 智能整理
   - 提取会议主题、时间、参与人
   - 提取关键决策点
   - 提取行动项（任务、责任人、截止时间）
   - 生成结构化会议纪要

3. 知识归档
   - 自动归档到项目知识库
   - 关联相关项目/任务
   - 支持检索和回顾

#### 2.2.3 技术实现

**Skill 类型**: TBS (Tool Business Skill)

**核心类设计**:
```java
/**
 * 会议纪要整理 Skill
 * 
 * @sceneSkill true
 * @sceneCategory TBS
 * @capabilitySubType CONTENT_GENERATION
 */
@Component
public class MeetingMinutesSkill implements RichSkill {
    
    @Autowired
    private LlmProvider llmProvider;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    /**
     * 整理会议内容
     * 
     * @driverCondition meetingContent != null
     */
    @Tool(name = "organizeMeeting", description = "整理会议纪要")
    public MeetingMinutes organizeMeeting(
            @ToolParam String meetingContent,
            @ToolParam(required = false) String projectId,
            @ToolParam(required = false) List<String> participants) {
        
        // 1. 构建提示词
        String prompt = buildOrganizePrompt(meetingContent, participants);
        
        // 2. 调用 LLM 整理
        String structuredContent = llmProvider.complete(
            "gpt-4",
            prompt,
            CompletionOptions.builder()
                .temperature(0.3)
                .maxTokens(2000)
                .build()
        );
        
        // 3. 解析结构化内容
        MeetingMinutes minutes = parseMinutes(structuredContent);
        
        // 4. 保存到知识库
        if (projectId != null) {
            saveToProjectKb(minutes, projectId);
        }
        
        return minutes;
    }
    
    /**
     * 提取行动项
     */
    @Tool(name = "extractActionItems", description = "提取会议行动项")
    public List<ActionItem> extractActionItems(@ToolParam String meetingContent) {
        String prompt = "从以下会议内容中提取行动项，格式：任务|责任人|截止时间\n\n" + meetingContent;
        
        String result = llmProvider.complete("gpt-4", prompt, defaultOptions());
        
        return parseActionItems(result);
    }
    
    private String buildOrganizePrompt(String content, List<String> participants) {
        return String.format("""
            请将以下会议内容整理成结构化的会议纪要：
            
            参与人：%s
            
            会议内容：
            %s
            
            请按以下格式输出：
            ## 会议主题
            [主题]
            
            ## 会议信息
            - 时间：[时间]
            - 地点：[地点]
            - 参与人：[参与人列表]
            
            ## 会议内容
            [要点总结]
            
            ## 关键决策
            1. [决策1]
            2. [决策2]
            
            ## 行动项
            | 序号 | 任务 | 责任人 | 截止时间 | 状态 |
            |------|------|--------|----------|------|
            | 1 | [任务1] | [责任人] | [时间] | 待办 |
            
            ## 下次会议
            - 时间：[时间]
            - 议题：[议题]
            """, 
            participants != null ? String.join(", ", participants) : "未指定",
            content
        );
    }
}
```

#### 2.2.4 验收标准

- [ ] 支持语音/文本/文件输入
- [ ] 整理时间 < 10秒（1000字以内）
- [ ] 行动项提取准确率 > 90%
- [ ] 支持导出为 Word/PDF/Markdown
- [ ] 自动关联项目知识库

---

### TASK-003: 知识共享管理 Skill (P1)

#### 2.3.1 任务描述
开发知识库权限管理和分享 Skill，支持部门内和跨部门的知识共享。

#### 2.3.2 功能需求

**核心功能**:
1. 权限管理
   - 知识库可见性设置（私有/部门/公开）
   - 细粒度权限控制（读/写/管理）
   - 用户/部门/角色授权

2. 分享功能
   - 生成分享链接
   - 设置有效期和密码
   - 访问统计和审计

3. 协作功能
   - 多人协同编辑
   - 版本控制
   - 变更通知

#### 2.3.3 技术实现

**Skill 类型**: ASS (Agent Semi-autonomous Skill)

**核心类设计**:
```java
/**
 * 知识共享管理 Skill
 * 
 * @sceneSkill true
 * @sceneCategory ASS
 * @capabilitySubType COLLABORATION
 */
@Component
public class KnowledgeShareSkill implements RichSkill {
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private ShareService shareService;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    /**
     * 创建分享
     * 
     * @driverCondition kbId != null && targetUser != null
     */
    @Tool(name = "createShare", description = "创建知识库分享")
    public ShareResult createShare(
            @ToolParam String kbId,
            @ToolParam String ownerId,
            @ToolParam ShareTarget target,
            @ToolParam ShareOptions options) {
        
        // 1. 检查权限
        if (!permissionService.hasPermission(kbId, ownerId, Permission.MANAGE)) {
            throw new PermissionDeniedException("无权分享此知识库");
        }
        
        // 2. 创建分享
        ShareResult result = shareService.createShare(
            kbId,
            ShareOptions.builder()
                .targetUsers(target.getUsers())
                .targetDepartments(target.getDepartments())
                .permission(options.getPermission())
                .expireDays(options.getExpireDays())
                .password(options.getPassword())
                .build()
        );
        
        // 3. 发送通知
        notifyTargetUsers(result);
        
        return result;
    }
    
    /**
     * 验证分享链接
     * 
     * @driverCondition shareLink != null
     */
    @Tool(name = "validateShare", description = "验证分享链接")
    public ShareValidationResult validateShare(
            @ToolParam String shareLink,
            @ToolParam(required = false) String password,
            @ToolParam String visitorId) {
        
        ShareValidationResult result = shareService.validateShare(
            shareLink, password, visitorId
        );
        
        if (result.isValid()) {
            // 记录访问日志
            shareService.recordAccess(shareLink, visitorId);
        }
        
        return result;
    }
    
    /**
     * 获取分享统计
     */
    @Tool(name = "getShareStats", description = "获取分享统计")
    public ShareStats getShareStats(@ToolParam String shareId) {
        return shareService.getStats(shareId);
    }
}
```

#### 2.3.4 验收标准

- [ ] 支持3种可见性设置
- [ ] 支持细粒度权限控制
- [ ] 分享链接支持有效期和密码
- [ ] 访问统计实时更新
- [ ] 支持撤销分享

---

### TASK-004: 新人培训助手 Skill (P1)

#### 2.4.1 任务描述
开发新员工入职培训助手 Skill，帮助新人快速了解公司和岗位知识。

#### 2.4.2 功能需求

**核心功能**:
1. 学习路径
   - 根据岗位推荐学习资料
   - 学习进度跟踪
   - 学习任务提醒

2. 智能问答
   - 7x24 小时问答支持
   - 常见问题自动回复
   - 复杂问题转人工

3. 考核评估
   - 在线测试
   - 学习报告生成
   - 导师反馈

#### 2.4.3 技术实现

**Skill 类型**: ABS (Agent Business Skill)

**核心类设计**:
```java
/**
 * 新人培训助手 Skill
 * 
 * @sceneSkill true
 * @sceneCategory ABS
 * @capabilitySubType LEARNING_ASSISTANT
 */
@Component
public class OnboardingAssistantSkill implements RichSkill {
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private LlmProvider llmProvider;
    
    /**
     * 初始化新人学习库
     * 
     * @mainFirst true
     * @driverCondition newEmployeeId != null
     */
    @Tool(name = "initializeLearningPath", description = "初始化学习路径")
    public LearningPath initializeLearningPath(
            @ToolParam String newEmployeeId,
            @ToolParam String position,
            @ToolParam String department) {
        
        // 1. 创建个人学习知识库
        KnowledgeBase personalKb = knowledgeBaseService.create(
            newEmployeeId,
            KnowledgeBaseCreateRequest.builder()
                .name("我的入职学习")
                .type(KnowledgeBaseType.PERSONAL)
                .build()
        );
        
        // 2. 根据岗位推荐学习资料
        List<Document> recommendedDocs = recommendLearningMaterials(position, department);
        
        // 3. 生成学习路径
        LearningPath path = LearningPath.builder()
            .employeeId(newEmployeeId)
            .position(position)
            .stages(buildLearningStages(recommendedDocs))
            .build();
        
        return path;
    }
    
    /**
     * 问答助手
     * 
     * @driverCondition question != null
     */
    @Tool(name = "askQuestion", description = "培训问答")
    public TrainingAnswer askQuestion(
            @ToolParam String employeeId,
            @ToolParam String question,
            @ToolParam(required = false) String context) {
        
        // 1. 获取员工学习知识库
        List<String> kbIds = Arrays.asList(
            getPersonalKbId(employeeId),
            "company-policy-kb",
            "hr-kb"
        );
        
        // 2. 多轮对话
        ConversationResult result = conversationService.sendMessage(
            getSessionId(employeeId),
            question,
            ConversationOptions.builder()
                .kbIds(kbIds)
                .enableRag(true)
                .build()
        );
        
        // 3. 判断是否需要转人工
        boolean needHuman = result.getConfidence() < 0.6;
        
        return TrainingAnswer.builder()
            .answer(result.getResponse())
            .sources(result.getSources())
            .needHumanSupport(needHuman)
            .build();
    }
    
    /**
     * 生成学习报告
     */
    @Tool(name = "generateLearningReport", description = "生成学习报告")
    public LearningReport generateLearningReport(@ToolParam String employeeId) {
        // 统计学习进度
        LearningProgress progress = calculateProgress(employeeId);
        
        // 生成报告
        return LearningReport.builder()
            .employeeId(employeeId)
            .progress(progress)
            .completedTasks(progress.getCompletedTasks())
            .remainingTasks(progress.getRemainingTasks())
            .assessmentScore(calculateAssessmentScore(employeeId))
            .build();
    }
}
```

#### 2.4.4 验收标准

- [ ] 支持按岗位推荐学习资料
- [ ] 问答响应时间 < 3秒
- [ ] 支持学习进度跟踪
- [ ] 自动生成学习报告
- [ ] 支持转人工服务

---

### TASK-005: 项目知识沉淀 Skill (P2)

#### 2.5.1 任务描述
开发项目知识自动沉淀 Skill，将项目文档自动分类整理，形成可复用知识资产。

#### 2.5.2 功能需求

**核心功能**:
1. 文档自动分类
   - 识别文档类型（需求/设计/测试/总结）
   - 提取关键信息（技术栈、难点、解决方案）
   - 自动打标签

2. 知识关联
   - 关联相关项目
   - 相似项目推荐
   - 知识图谱构建

3. 检索发现
   - 按项目阶段检索
   - 按技术栈检索
   - 智能推荐

#### 2.5.3 技术实现

**Skill 类型**: TBS (Tool Business Skill)

**核心类设计**:
```java
/**
 * 项目知识沉淀 Skill
 * 
 * @sceneSkill true
 * @sceneCategory TBS
 * @capabilitySubType KNOWLEDGE_EXTRACTION
 */
@Component
public class ProjectKnowledgeSkill implements RichSkill {
    
    @Autowired
    private BatchImportService batchImportService;
    
    @Autowired
    private SceneSkillClassifier sceneSkillClassifier;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    /**
     * 批量导入项目文档
     * 
     * @driverCondition archiveFile != null
     */
    @Tool(name = "importProjectDocs", description = "导入项目文档")
    public ImportResult importProjectDocs(
            @ToolParam String projectId,
            @ToolParam File archiveFile,
            @ToolParam ImportOptions options) {
        
        // 1. 批量导入
        ImportResult result = batchImportService.importFromArchive(
            getCurrentUserId(),
            getProjectKbId(projectId),
            archiveFile,
            ImportOptions.builder()
                .autoClassify(true)
                .extractMetadata(true)
                .build()
        );
        
        // 2. 自动分类处理
        for (Document doc : result.getDocuments()) {
            classifyAndTagDocument(doc, projectId);
        }
        
        return result;
    }
    
    /**
     * 分类和标注文档
     */
    private void classifyAndTagDocument(Document doc, String projectId) {
        // 1. 提取内容
        String content = documentExtractor.extractText(doc);
        
        // 2. 使用 LLM 分类
        String docType = classifyDocumentType(content);
        
        // 3. 提取关键信息
        Map<String, Object> metadata = extractKeyInfo(content);
        
        // 4. 更新文档元数据
        doc.setType(docType);
        doc.setMetadata(metadata);
        doc.addTag("project:" + projectId);
        doc.addTag("type:" + docType);
        
        knowledgeBaseService.updateDocument(doc);
    }
    
    /**
     * 发现相似项目
     * 
     * @driverCondition projectId != null
     */
    @Tool(name = "discoverSimilarProjects", description = "发现相似项目")
    public List<ProjectRecommendation> discoverSimilarProjects(@ToolParam String projectId) {
        // 1. 获取当前项目特征
        ProjectFeatures features = extractProjectFeatures(projectId);
        
        // 2. 相似度计算
        List<ProjectRecommendation> recommendations = projectRepository
            .findAll()
            .stream()
            .filter(p -> !p.getId().equals(projectId))
            .map(p -> calculateSimilarity(features, p))
            .filter(r -> r.getSimilarityScore() > 0.7)
            .sorted(Comparator.comparing(ProjectRecommendation::getSimilarityScore).reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        return recommendations;
    }
    
    /**
     * 生成项目知识图谱
     */
    @Tool(name = "generateKnowledgeGraph", description = "生成项目知识图谱")
    public KnowledgeGraph generateKnowledgeGraph(@ToolParam String projectId) {
        // 1. 获取项目所有文档
        List<Document> docs = knowledgeBaseService.listDocuments(
            getProjectKbId(projectId),
            DocumentQueryRequest.builder().build()
        );
        
        // 2. 提取实体和关系
        KnowledgeGraph graph = new KnowledgeGraph();
        
        for (Document doc : docs) {
            List<Entity> entities = entityExtractor.extract(doc);
            List<Relation> relations = relationExtractor.extract(doc);
            
            graph.addEntities(entities);
            graph.addRelations(relations);
        }
        
        return graph;
    }
}
```

#### 2.5.4 验收标准

- [ ] 支持批量导入项目文档
- [ ] 文档分类准确率 > 85%
- [ ] 支持相似项目推荐
- [ ] 支持知识图谱可视化
- [ ] 支持按多维度检索

---

## 三、技术指南

### 3.1 开发环境

**必备依赖**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```

**推荐工具**:
- IDE: IntelliJ IDEA 2023+
- JDK: 17+
- Maven: 3.8+
- Git: 2.30+

### 3.2 Skill 开发规范

**1. 注解规范**:
```java
@Component
@SkillInfo(
    name = "skill-name",
    version = "1.0.0",
    category = SceneSkillCategory.ABS,
    description = "Skill描述"
)
public class MySkill implements RichSkill {
    // ...
}
```

**2. 工具方法规范**:
```java
@Tool(name = "toolName", description = "工具描述")
public ResultType toolMethod(
    @ToolParam String requiredParam,
    @ToolParam(required = false) String optionalParam) {
    // 1. 参数校验
    // 2. 业务逻辑
    // 3. 返回结果
}
```

**3. 错误处理**:
```java
try {
    // 业务逻辑
} catch (SkillException e) {
    log.error("Skill execution failed", e);
    return Result.fail(e.getErrorCode(), e.getMessage());
} catch (Exception e) {
    log.error("Unexpected error", e);
    return Result.fail(ErrorCode.UNKNOWN_ERROR, "系统错误");
}
```

### 3.3 测试要求

**单元测试覆盖率**: > 80%

**测试示例**:
```java
@SpringBootTest
class DocumentAssistantSkillTest {
    
    @Autowired
    private DocumentAssistantSkill skill;
    
    @Test
    void testQueryDocument() {
        // Given
        String kbId = "test-kb";
        String query = "请假流程";
        
        // When
        QueryResult result = skill.queryDocument(kbId, query, 5);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getAnswer());
        assertTrue(result.getConfidence() > 0.5);
    }
}
```

### 3.4 部署指南

**1. 打包**:
```bash
mvn clean package
```

**2. 安装到引擎**:
```bash
# 使用 InstallCoordinator 安装
installCoordinator.install(skillPackage, installOptions);
```

**3. 配置**:
```yaml
skill:
  installed:
    - name: document-assistant
      version: 1.0.0
      enabled: true
    - name: meeting-minutes
      version: 1.0.0
      enabled: true
```

---

## 四、交付物清单

| 交付物 | 格式 | 说明 |
|--------|------|------|
| 源代码 | Java | 完整的 Skill 实现 |
| 单元测试 | Java | 测试覆盖率 > 80% |
| API 文档 | Markdown | 接口说明文档 |
| 部署文档 | Markdown | 部署和配置说明 |
| 使用示例 | Java | 代码示例 |

---

## 五、支持资源

**技术文档**:
- [SECONDARY_DEVELOPMENT_GUIDE.md](./SECONDARY_DEVELOPMENT_GUIDE.md)
- [API_REFERENCE.md](./API_REFERENCE.md)
- [ARCHITECTURE_DIAGRAM.md](./ARCHITECTURE_DIAGRAM.md)

**联系方式**:
- 技术负责人: [待填写]
- 产品负责人: [待填写]
- 沟通群组: [待填写]

---

**文档版本历史**:

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2026-03-06 | 初始版本 | Agent |
