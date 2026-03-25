# 用户知识系统测试用例文档

**文档版本**: v1.0  
**发布日期**: 2026-03-07  
**测试范围**: 基于用户故事的真实数据读写及逻辑验证  
**依赖版本**: scene-engine v2.3

---

## 目录

1. [测试环境准备](#一测试环境准备)
2. [TASK-001: 智能文档助手测试用例](#二task-001-智能文档助手测试用例)
3. [TASK-002: 会议纪要整理测试用例](#三task-002-会议纪要整理测试用例)
4. [TASK-003: 知识共享管理测试用例](#四task-003-知识共享管理测试用例)
5. [TASK-004: 新人培训助手测试用例](#五task-004-新人培训助手测试用例)
6. [TASK-005: 项目知识沉淀测试用例](#六task-005-项目知识沉淀测试用例)
7. [集成测试用例](#七集成测试用例)
8. [性能测试用例](#八性能测试用例)

---

## 一、测试环境准备

### 1.1 测试数据准备

```sql
-- 测试用户数据
INSERT INTO users (id, username, email, department_id, role) VALUES
('user-001', '张三', 'zhangsan@company.com', 'dept-hr', 'EMPLOYEE'),
('user-002', '李四', 'lisi@company.com', 'dept-tech', 'MANAGER'),
('user-003', '王五', 'wangwu@company.com', 'dept-product', 'EMPLOYEE'),
('user-004', '赵六', 'zhaoliu@company.com', 'dept-hr', 'ADMIN'),
('user-005', '新员工', 'newbie@company.com', 'dept-tech', 'NEW_EMPLOYEE');

-- 测试部门数据
INSERT INTO departments (id, name, parent_id) VALUES
('dept-hr', '人力资源部', NULL),
('dept-tech', '技术部', NULL),
('dept-product', '产品部', NULL);
```

### 1.2 测试文档样本

**样本1: 请假制度文档 (leave_policy.pdf)**
```
《员工请假管理制度》

第一章 总则
第一条 为规范公司员工请假管理，特制定本制度。

第二章 请假类型
第二条 请假类型包括：
1. 事假：因个人事务请假，需提前1天申请
2. 病假：因病请假，需提供医院证明
3. 年假：工作满1年享受5天年假，满3年享受10天
4. 婚假：结婚享受3天婚假
5. 产假：女性员工享受98天产假

第三章 请假流程
第三条 请假流程：
1. 员工填写请假申请单
2. 直属上级审批（3天以内）
3. 部门负责人审批（3天以上）
4. HR备案

第四章 审批时限
第四条 审批时限：
- 提前申请：事假提前1天，年假提前3天
- 紧急病假：可事后补交证明
```

**样本2: 项目需求文档 (requirement_v1.0.docx)**
```
《电商平台需求规格说明书》

1. 项目概述
   1.1 项目背景
   公司计划开发B2C电商平台，支持商品展示、购物车、订单管理等功能。
   
   1.2 技术栈
   - 前端：Vue.js 3.0 + Element Plus
   - 后端：Spring Boot 2.7
   - 数据库：MySQL 8.0
   - 缓存：Redis 6.0

2. 功能需求
   2.1 用户模块
   - 用户注册/登录
   - 个人信息管理
   - 收货地址管理
   
   2.2 商品模块
   - 商品分类管理
   - 商品搜索
   - 商品详情展示

3. 非功能需求
   - 并发支持：1000 QPS
   - 响应时间：P99 < 200ms
   - 可用性：99.9%
```

---

## 二、TASK-001: 智能文档助手测试用例

### 2.1 文档上传与处理测试

#### TC-001-01: PDF文档上传与向量化

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-001-01 |
| **测试名称** | PDF文档上传与向量化处理 |
| **用户故事** | 故事1 - 智能文档助手 |
| **前置条件** | 1. 用户已登录<br>2. 知识库已创建 |
| **测试数据** | leave_policy.pdf (文件大小: 125KB, 页数: 4页) |
| **测试步骤** | 1. 调用 `uploadDocument(user-001, kb-001, leave_policy.pdf, metadata)`<br>2. 等待文档处理完成<br>3. 查询文档状态<br>4. 检查向量数据库 |
| **预期结果** | 1. 返回Document对象，status=PENDING<br>2. 异步处理后status=INDEXED<br>3. 文档分块数 >= 4<br>4. 向量数据库中存在对应向量记录 |
| **验证SQL** | `SELECT status, chunk_count FROM documents WHERE id = 'doc-xxx'` |
| **优先级** | P0 |

**实际验证代码**:
```java
@Test
void testPdfUploadAndVectorization() {
    // Given
    String userId = "user-001";
    String kbId = "kb-001";
    File pdfFile = new File("test-data/leave_policy.pdf");
    DocumentMetadata metadata = DocumentMetadata.builder()
        .filename("leave_policy.pdf")
        .type("制度文档")
        .department("人力资源部")
        .build();
    
    // When
    Document doc = knowledgeBaseService.uploadDocument(userId, kbId, pdfFile, metadata);
    
    // Then - 验证文档记录
    assertNotNull(doc.getId());
    assertEquals("leave_policy.pdf", doc.getTitle());
    assertEquals(DocumentStatus.PENDING, doc.getStatus());
    
    // 等待异步处理
    await().atMost(30, TimeUnit.SECONDS).until(() -> {
        Document updated = knowledgeBaseService.getDocument(doc.getId());
        return updated.getStatus() == DocumentStatus.INDEXED;
    });
    
    // 验证向量数据
    List<DocumentChunk> chunks = documentChunkRepository.findByDocId(doc.getId());
    assertTrue(chunks.size() >= 4, "文档应至少分4块");
    
    // 验证向量数据库
    for (DocumentChunk chunk : chunks) {
        List<Float> vector = vectorStore.getVector(chunk.getVectorId());
        assertNotNull(vector);
        assertEquals(1536, vector.size(), "向量维度应为1536");
    }
}
```

---

#### TC-001-02: Word文档分块策略验证

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-001-02 |
| **测试名称** | Word文档分块策略验证 |
| **用户故事** | 故事1 - 智能文档助手 |
| **前置条件** | 1. 知识库已创建，chunkSize=500, chunkOverlap=50 |
| **测试数据** | requirement_v1.0.docx (内容长度: 约3000字符) |
| **测试步骤** | 1. 上传Word文档<br>2. 检查分块结果<br>3. 验证分块重叠 |
| **预期结果** | 1. 分块数 = ceil(3000/500) ≈ 6块<br>2. 相邻分块有50字符重叠<br>3. 每块内容完整，不截断句子 |
| **验证点** | 分块边界不截断标题或段落 |
| **优先级** | P1 |

**实际验证代码**:
```java
@Test
void testWordDocumentChunking() {
    // Given
    String kbId = "kb-001";
    KnowledgeBase kb = knowledgeBaseService.getKnowledgeBase(kbId);
    assertEquals(500, kb.getChunkSize());
    assertEquals(50, kb.getChunkOverlap());
    
    // When
    File wordFile = new File("test-data/requirement_v1.0.docx");
    Document doc = knowledgeBaseService.uploadDocument("user-001", kbId, wordFile, 
        DocumentMetadata.builder().filename("requirement_v1.0.docx").build());
    
    // 等待处理完成
    await().atMost(30, TimeUnit.SECONDS).until(() -> 
        knowledgeBaseService.getDocument(doc.getId()).getStatus() == DocumentStatus.INDEXED
    );
    
    // Then - 验证分块
    List<DocumentChunk> chunks = documentChunkRepository.findByDocId(doc.getId());
    
    // 验证分块数
    assertTrue(chunks.size() >= 5 && chunks.size() <= 8, 
        "3000字符按500分块，应有5-8块");
    
    // 验证重叠
    for (int i = 0; i < chunks.size() - 1; i++) {
        String currentEnd = chunks.get(i).getContent().substring(
            Math.max(0, chunks.get(i).getContent().length() - 60)
        );
        String nextStart = chunks.get(i + 1).getContent().substring(0, 60);
        
        // 验证有重叠内容
        boolean hasOverlap = currentEnd.contains(nextStart.substring(0, 30));
        assertTrue(hasOverlap, "相邻分块应有重叠");
    }
    
    // 验证不截断标题
    for (DocumentChunk chunk : chunks) {
        String content = chunk.getContent();
        assertFalse(content.startsWith(" "), "分块不应以空格开头");
        assertFalse(content.matches(".*\d+\.\s*$"), "分块不应以序号结尾");
    }
}
```

---

### 2.2 智能问答测试

#### TC-001-03: 基于文档内容的准确问答

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-001-03 |
| **测试名称** | 基于文档内容的准确问答验证 |
| **用户故事** | 故事1 - 智能文档助手 |
| **前置条件** | 1. 请假制度文档已上传并索引完成<br>2. RAG Pipeline已配置 |
| **测试数据** | 问题: "年假有多少天？工作满3年呢？" |
| **测试步骤** | 1. 调用智能问答接口<br>2. 验证答案准确性<br>3. 验证来源标注 |
| **预期结果** | 1. 答案包含"5天"和"10天"<br>2. 答案来源标注为leave_policy.pdf<br>3. 置信度 > 0.85 |
| **验证SQL** | 查询conversation_logs表验证调用记录 |
| **优先级** | P0 |

**实际验证代码**:
```java
@Test
void testAccurateQABasedOnDocument() {
    // Given - 确保文档已索引
    String kbId = "kb-001";
    String question = "年假有多少天？工作满3年呢？";
    
    // When
    QAResult result = intelligentQAService.intelligentQA(
        kbId, 
        question,
        QAOptions.builder()
            .topK(3)
            .includeSources(true)
            .temperature(0.3)
            .build()
    );
    
    // Then - 验证答案
    assertNotNull(result.getAnswer());
    String answer = result.getAnswer().toLowerCase();
    
    // 验证关键信息
    assertTrue(answer.contains("5") || answer.contains("五"), 
        "答案应包含'5天'");
    assertTrue(answer.contains("10") || answer.contains("十"), 
        "答案应包含'10天'");
    assertTrue(answer.contains("3年") || answer.contains("三年"), 
        "答案应提及'3年'条件");
    
    // 验证来源
    assertNotNull(result.getSources());
    assertFalse(result.getSources().isEmpty());
    assertTrue(result.getSources().stream()
        .anyMatch(s -> s.getTitle().contains("leave_policy")),
        "来源应指向请假制度文档");
    
    // 验证置信度
    assertTrue(result.getConfidence() > 0.85, 
        "置信度应大于0.85");
    
    // 验证数据库记录
    ConversationLog log = conversationLogRepository
        .findTopByKbIdAndQuestionOrderByCreatedAtDesc(kbId, question);
    assertNotNull(log);
    assertEquals(question, log.getQuestion());
    assertTrue(log.getResponseTime() < 3000, "响应时间应小于3秒");
}
```

---

#### TC-001-04: 多轮对话上下文保持

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-001-04 |
| **测试名称** | 多轮对话上下文保持验证 |
| **用户故事** | 故事1 - 智能文档助手 |
| **前置条件** | 1. 会话已创建<br>2. 请假制度文档已索引 |
| **测试数据** | 第一轮: "请假流程是什么？"<br>第二轮: "需要提前几天申请？"<br>第三轮: "病假呢？" |
| **测试步骤** | 1. 发送第一轮问题<br>2. 发送第二轮追问<br>3. 发送第三轮追问<br>4. 验证上下文理解 |
| **预期结果** | 1. 第二轮能理解"申请"指请假申请<br>2. 第三轮能理解"病假"与前文关联<br>3. 每轮回答都基于文档内容 |
| **验证点** | 检查conversation_context表中的历史记录 |
| **优先级** | P1 |

**实际验证代码**:
```java
@Test
void testMultiTurnConversationContext() {
    // Given
    String sessionId = "session-test-001";
    String kbId = "kb-001";
    
    // 第一轮
    ConversationResult turn1 = conversationService.sendMessage(
        sessionId,
        "请假流程是什么？",
        ConversationOptions.builder()
            .kbIds(Arrays.asList(kbId))
            .enableRag(true)
            .build()
    );
    assertNotNull(turn1.getResponse());
    assertTrue(turn1.getResponse().contains("审批"));
    
    // 第二轮 - 追问，应理解上下文
    ConversationResult turn2 = conversationService.sendMessage(
        sessionId,
        "需要提前几天申请？",
        ConversationOptions.builder()
            .kbIds(Arrays.asList(kbId))
            .enableRag(true)
            .build()
    );
    assertNotNull(turn2.getResponse());
    String response2 = turn2.getResponse();
    // 应理解"申请"指请假申请
    assertTrue(response2.contains("1天") || response2.contains("3天"),
        "应回答请假提前天数");
    
    // 第三轮 - 更隐晦的追问
    ConversationResult turn3 = conversationService.sendMessage(
        sessionId,
        "病假呢？",
        ConversationOptions.builder()
            .kbIds(Arrays.asList(kbId))
            .enableRag(true)
            .build()
    );
    assertNotNull(turn3.getResponse());
    String response3 = turn3.getResponse();
    // 应理解"病假"指病假申请流程
    assertTrue(response3.contains("医院证明") || response3.contains("事后补交"),
        "应回答病假特殊规定");
    
    // 验证上下文存储
    ConversationContext context = conversationContextRepository.findBySessionId(sessionId);
    assertNotNull(context);
    assertEquals(3, context.getMessageCount());
    assertTrue(context.getMessages().stream()
        .anyMatch(m -> m.getContent().contains("请假流程")));
}
```

---

## 三、TASK-002: 会议纪要整理测试用例

### 3.1 会议内容整理测试

#### TC-002-01: 会议录音转文字并提取决策

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-002-01 |
| **测试名称** | 会议录音转文字并提取关键决策 |
| **用户故事** | 故事2 - 会议纪要智能整理 |
| **前置条件** | 1. 项目知识库已创建<br>2. LLM服务可用 |
| **测试数据** | 会议内容: "项目周会 2026-03-07 参与人: 张三、李四、王五。讨论内容: 1. 确定下周发布v1.0版本 2. 张三负责前端优化，3月15日前完成 3. 李四负责后端接口压测 4. 下次会议定在3月14日下午2点" |
| **测试步骤** | 1. 调用会议整理接口<br>2. 验证结构化输出<br>3. 验证决策提取<br>4. 验证行动项提取 |
| **预期结果** | 1. 提取会议主题"项目周会"<br>2. 提取参与人列表<br>3. 提取关键决策"确定下周发布v1.0版本"<br>4. 提取2个行动项，责任人正确 |
| **验证SQL** | 查询meeting_minutes表验证归档 |
| **优先级** | P0 |

**实际验证代码**:
```java
@Test
void testMeetingMinutesExtraction() {
    // Given
    String meetingContent = """
        项目周会 2026-03-07
        参与人: 张三、李四、王五
        
        讨论内容:
        1. 确定下周发布v1.0版本
        2. 张三负责前端优化，3月15日前完成
        3. 李四负责后端接口压测
        4. 下次会议定在3月14日下午2点
        """;
    String projectId = "project-001";
    
    // When
    MeetingMinutes minutes = meetingMinutesSkill.organizeMeeting(
        meetingContent,
        projectId,
        Arrays.asList("张三", "李四", "王五")
    );
    
    // Then - 验证基本信息
    assertNotNull(minutes);
    assertEquals("项目周会", minutes.getTheme());
    assertEquals("2026-03-07", minutes.getDate());
    assertEquals(3, minutes.getParticipants().size());
    assertTrue(minutes.getParticipants().containsAll(Arrays.asList("张三", "李四", "王五")));
    
    // 验证决策提取
    assertNotNull(minutes.getDecisions());
    assertFalse(minutes.getDecisions().isEmpty());
    assertTrue(minutes.getDecisions().stream()
        .anyMatch(d -> d.contains("v1.0") || d.contains("发布")),
        "应提取发布v1.0的决策");
    
    // 验证行动项
    List<ActionItem> actionItems = minutes.getActionItems();
    assertNotNull(actionItems);
    assertEquals(2, actionItems.size(), "应提取2个行动项");
    
    // 验证行动项详情
    ActionItem zhangsanTask = actionItems.stream()
        .filter(a -> a.getAssignee().equals("张三"))
        .findFirst()
        .orElse(null);
    assertNotNull(zhangsanTask);
    assertTrue(zhangsanTask.getTask().contains("前端优化"));
    assertEquals("2026-03-15", zhangsanTask.getDeadline());
    
    ActionItem lisiTask = actionItems.stream()
        .filter(a -> a.getAssignee().equals("李四"))
        .findFirst()
        .orElse(null);
    assertNotNull(lisiTask);
    assertTrue(lisiTask.getTask().contains("压测"));
    
    // 验证数据库归档
    Document savedDoc = documentRepository.findBySourceIdAndType(minutes.getId(), "MEETING_MINUTES");
    assertNotNull(savedDoc);
    assertEquals(projectId, savedDoc.getProjectId());
    assertEquals("INDEXED", savedDoc.getStatus());
}
```

---

#### TC-002-02: 行动项提取准确率验证

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-002-02 |
| **测试名称** | 行动项提取准确率验证 |
| **用户故事** | 故事2 - 会议纪要智能整理 |
| **前置条件** | 1. 测试数据集准备完成 |
| **测试数据** | 10份不同格式的会议纪要文本 |
| **测试步骤** | 1. 对每份纪要提取行动项<br>2. 与人工标注对比<br>3. 计算准确率 |
| **预期结果** | 行动项提取准确率 >= 90% |
| **验证点** | 精确率、召回率、F1分数 |
| **优先级** | P1 |

**实际验证代码**:
```java
@Test
void testActionItemExtractionAccuracy() {
    // Given - 测试数据集
    List<TestCase> testCases = Arrays.asList(
        new TestCase("""
            会议内容: 王五负责设计文档，周五前完成。
            预期行动项: [{assignee:"王五", task:"设计文档", deadline:"周五"}]
            """, 
            Arrays.asList(new ActionItem("王五", "设计文档", "周五"))),
        new TestCase("""
            会议内容: 1. 张三修复登录bug，今天完成 2. 李四准备演示PPT，明天上午前
            预期行动项: [{张三,修复登录bug,今天}, {李四,准备演示PPT,明天上午}]
            """,
            Arrays.asList(
                new ActionItem("张三", "修复登录bug", "今天"),
                new ActionItem("李四", "准备演示PPT", "明天上午")
            ))
        // ... 更多测试用例
    );
    
    int totalExpected = 0;
    int totalExtracted = 0;
    int correct = 0;
    
    // When
    for (TestCase testCase : testCases) {
        List<ActionItem> extracted = meetingMinutesSkill.extractActionItems(testCase.getContent());
        List<ActionItem> expected = testCase.getExpected();
        
        totalExpected += expected.size();
        totalExtracted += extracted.size();
        
        // 计算正确提取的数量
        for (ActionItem exp : expected) {
            boolean found = extracted.stream().anyMatch(ext -> 
                ext.getAssignee().equals(exp.getAssignee()) &&
                ext.getTask().contains(exp.getTask())
            );
            if (found) correct++;
        }
    }
    
    // Then - 计算指标
    double precision = (double) correct / totalExtracted;
    double recall = (double) correct / totalExpected;
    double f1 = 2 * precision * recall / (precision + recall);
    
    System.out.println("精确率: " + precision);
    System.out.println("召回率: " + recall);
    System.out.println("F1分数: " + f1);
    
    assertTrue(precision >= 0.90, "精确率应 >= 90%");
    assertTrue(recall >= 0.90, "召回率应 >= 90%");
    assertTrue(f1 >= 0.90, "F1分数应 >= 90%");
}
```

---

## 四、TASK-003: 知识共享管理测试用例

### 4.1 权限控制测试

#### TC-003-01: 知识库可见性权限验证

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-003-01 |
| **测试名称** | 知识库可见性权限验证 |
| **用户故事** | 故事3 - 跨部门知识共享 |
| **前置条件** | 1. 多个用户账号已创建<br>2. 不同部门用户已配置 |
| **测试数据** | 知识库A: visibility=PRIVATE, owner=user-001<br>知识库B: visibility=DEPARTMENT, dept=dept-hr<br>知识库C: visibility=PUBLIC |
| **测试步骤** | 1. user-001访问知识库A - 应成功<br>2. user-002访问知识库A - 应失败<br>3. user-004(HR部门)访问知识库B - 应成功<br>4. user-002(技术部)访问知识库B - 应失败<br>5. 任意用户访问知识库C - 应成功 |
| **预期结果** | 权限验证逻辑正确，越权访问被拒绝 |
| **验证SQL** | 查询kb_permissions和access_logs表 |
| **优先级** | P0 |

**实际验证代码**:
```java
@Test
void testKnowledgeBaseVisibilityPermissions() {
    // Given - 创建3个不同可见性的知识库
    String ownerId = "user-001";
    String otherUserId = "user-002";
    String hrUserId = "user-004"; // HR部门
    String techUserId = "user-002"; // 技术部
    
    // 私有知识库
    KnowledgeBase privateKb = knowledgeBaseService.create(
        ownerId,
        KnowledgeBaseCreateRequest.builder()
            .name("私有知识库")
            .visibility(Visibility.PRIVATE)
            .build()
    );
    
    // 部门知识库
    KnowledgeBase deptKb = knowledgeBaseService.create(
        hrUserId,
        KnowledgeBaseCreateRequest.builder()
            .name("HR部门知识库")
            .visibility(Visibility.DEPARTMENT)
            .departmentId("dept-hr")
            .build()
    );
    
    // 公开知识库
    KnowledgeBase publicKb = knowledgeBaseService.create(
        ownerId,
        KnowledgeBaseCreateRequest.builder()
            .name("公开知识库")
            .visibility(Visibility.PUBLIC)
            .build()
    );
    
    // When & Then - 验证私有知识库权限
    assertTrue(permissionService.hasPermission(privateKb.getId(), ownerId, Permission.READ),
        "所有者应有读权限");
    assertFalse(permissionService.hasPermission(privateKb.getId(), otherUserId, Permission.READ),
        "其他用户不应有读权限");
    
    // 验证部门知识库权限
    assertTrue(permissionService.hasPermission(deptKb.getId(), hrUserId, Permission.READ),
        "同部门用户应有读权限");
    assertFalse(permissionService.hasPermission(deptKb.getId(), techUserId, Permission.READ),
        "不同部门用户不应有读权限");
    
    // 验证公开知识库权限
    assertTrue(permissionService.hasPermission(publicKb.getId(), ownerId, Permission.READ));
    assertTrue(permissionService.hasPermission(publicKb.getId(), otherUserId, Permission.READ),
        "任意用户应有公开知识库读权限");
    
    // 验证访问日志记录
    List<AccessLog> logs = accessLogRepository.findByKbIdAndAfter(privateKb.getId(), 
        LocalDateTime.now().minusMinutes(5));
    assertFalse(logs.isEmpty(), "应记录访问尝试日志");
}
```

---

#### TC-003-02: 分享链接有效期验证

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-003-02 |
| **测试名称** | 分享链接有效期验证 |
| **用户故事** | 故事3 - 跨部门知识共享 |
| **前置条件** | 1. 知识库已创建<br>2. 分享服务可用 |
| **测试数据** | 分享配置: expireDays=7, password="123456" |
| **测试步骤** | 1. 创建分享链接<br>2. 验证链接在有效期内可访问<br>3. 模拟时间超过7天后<br>4. 验证链接已失效 |
| **预期结果** | 1. 有效期内正确密码可访问<br>2. 错误密码拒绝访问<br>3. 过期后链接失效 |
| **验证SQL** | 查询share_records表验证过期时间 |
| **优先级** | P1 |

**实际验证代码**:
```java
@Test
void testShareLinkExpiration() {
    // Given
    String kbId = "kb-001";
    String ownerId = "user-001";
    
    // When - 创建分享
    ShareResult share = shareService.createShare(
        kbId,
        ShareOptions.builder()
            .targetUsers(Arrays.asList("user-002"))
            .permission(Permission.READ)
            .expireDays(7)
            .password("123456")
            .build()
    );
    
    assertNotNull(share.getShareLink());
    String shareLink = share.getShareLink();
    
    // Then - 验证正确密码可访问
    ShareValidationResult validResult = shareService.validateShare(
        shareLink, "123456", "user-002"
    );
    assertTrue(validResult.isValid(), "正确密码应验证通过");
    assertEquals(kbId, validResult.getKbId());
    
    // 验证错误密码拒绝
    ShareValidationResult invalidResult = shareService.validateShare(
        shareLink, "wrong-password", "user-002"
    );
    assertFalse(invalidResult.isValid(), "错误密码应被拒绝");
    
    // 验证访问记录
    ShareRecord record = shareRecordRepository.findByShareLink(shareLink);
    assertNotNull(record);
    assertEquals(LocalDateTime.now().plusDays(7).toLocalDate(), 
        record.getExpireAt().toLocalDate());
    
    // 模拟过期 - 直接修改数据库
    record.setExpireAt(LocalDateTime.now().minusDays(1));
    shareRecordRepository.save(record);
    
    // 验证过期后访问失败
    ShareValidationResult expiredResult = shareService.validateShare(
        shareLink, "123456", "user-002"
    );
    assertFalse(expiredResult.isValid(), "过期链接应失效");
    assertEquals("EXPIRED", expiredResult.getErrorCode());
}
```

---

## 五、TASK-004: 新人培训助手测试用例

### 5.1 学习路径测试

#### TC-004-01: 新人学习路径初始化

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-004-01 |
| **测试名称** | 新人学习路径初始化验证 |
| **用户故事** | 故事4 - 新人培训助手 |
| **前置条件** | 1. 新员工账号已创建<br>2. 岗位模板已配置 |
| **测试数据** | 新员工: user-005, 岗位: Java开发工程师, 部门: 技术部 |
| **测试步骤** | 1. 调用初始化学习路径接口<br>2. 验证个人知识库创建<br>3. 验证推荐资料<br>4. 验证学习阶段 |
| **预期结果** | 1. 创建名为"我的入职学习"的知识库<br>2. 推荐Java相关学习资料<br>3. 生成3-5个学习阶段<br>4. 关联公司制度和HR知识库 |
| **验证SQL** | 查询learning_paths和knowledge_bases表 |
| **优先级** | P0 |

**实际验证代码**:
```java
@Test
void testNewEmployeeLearningPathInitialization() {
    // Given
    String newEmployeeId = "user-005";
    String position = "Java开发工程师";
    String department = "dept-tech";
    
    // When
    LearningPath path = onboardingAssistantSkill.initializeLearningPath(
        newEmployeeId, position, department
    );
    
    // Then - 验证学习路径
    assertNotNull(path);
    assertEquals(newEmployeeId, path.getEmployeeId());
    assertEquals(position, path.getPosition());
    
    // 验证个人知识库创建
    KnowledgeBase personalKb = knowledgeBaseService
        .listUserKnowledgeBases(newEmployeeId)
        .stream()
        .filter(kb -> kb.getName().equals("我的入职学习"))
        .findFirst()
        .orElse(null);
    assertNotNull(personalKb, "应创建个人学习知识库");
    assertEquals(KnowledgeBaseType.PERSONAL, personalKb.getType());
    
    // 验证学习阶段
    List<LearningStage> stages = path.getStages();
    assertNotNull(stages);
    assertTrue(stages.size() >= 3 && stages.size() <= 5, 
        "应有3-5个学习阶段");
    
    // 验证阶段内容
    assertTrue(stages.get(0).getName().contains("入职"),
        "第一阶段应包含入职引导");
    assertTrue(stages.stream().anyMatch(s -> s.getName().contains("Java")),
        "应有Java技术相关阶段");
    assertTrue(stages.stream().anyMatch(s -> s.getName().contains("制度")),
        "应有公司制度学习阶段");
    
    // 验证推荐资料
    List<Document> recommendedDocs = path.getRecommendedDocuments();
    assertFalse(recommendedDocs.isEmpty(), "应推荐学习资料");
    assertTrue(recommendedDocs.stream()
        .anyMatch(d -> d.getTags().contains("Java")),
        "应推荐Java相关资料");
    
    // 验证数据库记录
    LearningPathRecord record = learningPathRepository.findByEmployeeId(newEmployeeId);
    assertNotNull(record);
    assertEquals(position, record.getPosition());
    assertEquals(0, record.getCompletedStages()); // 初始完成0阶段
}
```

---

#### TC-004-02: 培训问答准确率与转人工逻辑

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-004-02 |
| **测试名称** | 培训问答准确率与转人工逻辑验证 |
| **用户故事** | 故事4 - 新人培训助手 |
| **前置条件** | 1. 公司制度知识库已建立<br>2. 问答服务已配置 |
| **测试数据** | 问题1: "公司的绩效考核周期是多久？" (高置信度)<br>问题2: "我的工资什么时候涨？" (低置信度) |
| **测试步骤** | 1. 发送高置信度问题<br>2. 验证直接回答<br>3. 发送低置信度问题<br>4. 验证转人工建议 |
| **预期结果** | 1. 高置信度问题直接回答，置信度>0.6<br>2. 低置信度问题建议转人工，置信度<0.6 |
| **验证SQL** | 查询training_qa_logs表 |
| **优先级** | P1 |

**实际验证代码**:
```java
@Test
void testTrainingQAAccuracyAndHumanHandoff() {
    // Given
    String employeeId = "user-005";
    String sessionId = "training-session-001";
    
    // When - 高置信度问题
    TrainingAnswer answer1 = onboardingAssistantSkill.askQuestion(
        employeeId,
        "公司的绩效考核周期是多久？",
        null
    );
    
    // Then - 验证直接回答
    assertNotNull(answer1.getAnswer());
    assertFalse(answer1.isNeedHumanSupport(), 
        "高置信度问题不应需要人工支持");
    assertTrue(answer1.getConfidence() > 0.6, 
        "置信度应大于0.6");
    assertTrue(answer1.getAnswer().contains("季度") || 
        answer1.getAnswer().contains("年"),
        "应回答考核周期");
    
    // When - 低置信度/敏感问题
    TrainingAnswer answer2 = onboardingAssistantSkill.askQuestion(
        employeeId,
        "我的工资什么时候涨？",
        null
    );
    
    // Then - 验证转人工建议
    assertTrue(answer2.isNeedHumanSupport() || answer2.getConfidence() < 0.6,
        "敏感问题应建议转人工或置信度低");
    
    // 验证日志记录
    List<TrainingQALog> logs = trainingQALogRepository
        .findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    assertEquals(2, logs.size());
    
    TrainingQALog log1 = logs.get(1); // 第一条记录
    assertFalse(log1.isReferredToHuman());
    assertTrue(log1.getConfidence() > 0.6);
    
    TrainingQALog log2 = logs.get(0); // 第二条记录
    assertTrue(log2.isReferredToHuman() || log2.getConfidence() < 0.6);
}
```

---

## 六、TASK-005: 项目知识沉淀测试用例

### 6.1 文档自动分类测试

#### TC-005-01: 项目文档自动分类

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-005-01 |
| **测试名称** | 项目文档自动分类验证 |
| **用户故事** | 故事5 - 项目知识沉淀 |
| **前置条件** | 1. 项目知识库已创建<br>2. 分类模型已训练 |
| **测试数据** | 包含5种类型文档的压缩包: requirement.docx, design.pdf, api.md, test.xlsx, summary.docx |
| **测试步骤** | 1. 批量导入项目文档<br>2. 等待自动分类<br>3. 验证分类结果<br>4. 验证标签提取 |
| **预期结果** | 1. 需求文档分类为"需求"<br>2. 设计文档分类为"设计"<br>3. 测试文档分类为"测试"<br>4. 准确率 >= 85% |
| **验证SQL** | 查询documents表验证type字段 |
| **优先级** | P0 |

**实际验证代码**:
```java
@Test
void testProjectDocumentAutoClassification() {
    // Given
    String projectId = "project-001";
    String kbId = "kb-project-001";
    File archiveFile = new File("test-data/project-docs.zip");
    
    // When
    ImportResult result = projectKnowledgeSkill.importProjectDocs(
        projectId,
        archiveFile,
        ImportOptions.builder()
            .autoClassify(true)
            .extractMetadata(true)
            .build()
    );
    
    // Then - 验证导入结果
    assertNotNull(result);
    assertEquals(5, result.getDocuments().size(), "应导入5个文档");
    
    // 等待分类完成
    await().atMost(60, TimeUnit.SECONDS).until(() -> {
        List<Document> docs = knowledgeBaseService.listDocuments(kbId, 
            DocumentQueryRequest.builder().build());
        return docs.stream().allMatch(d -> d.getType() != null);
    });
    
    // 验证分类结果
    List<Document> classifiedDocs = knowledgeBaseService.listDocuments(kbId,
        DocumentQueryRequest.builder().build());
    
    Map<String, String> expectedTypes = Map.of(
        "requirement.docx", "需求",
        "design.pdf", "设计",
        "api.md", "设计",
        "test.xlsx", "测试",
        "summary.docx", "总结"
    );
    
    int correctCount = 0;
    for (Document doc : classifiedDocs) {
        String filename = doc.getTitle();
        String expectedType = expectedTypes.get(filename);
        String actualType = doc.getType();
        
        if (expectedType != null && expectedType.equals(actualType)) {
            correctCount++;
        }
        
        // 验证标签
        assertFalse(doc.getTags().isEmpty(), "文档应有标签");
        assertTrue(doc.getTags().contains("project:" + projectId),
            "应包含项目标签");
        assertTrue(doc.getTags().contains("type:" + actualType),
            "应包含类型标签");
    }
    
    double accuracy = (double) correctCount / classifiedDocs.size();
    System.out.println("分类准确率: " + accuracy);
    assertTrue(accuracy >= 0.85, "分类准确率应 >= 85%");
    
    // 验证元数据提取
    Document reqDoc = classifiedDocs.stream()
        .filter(d -> d.getTitle().equals("requirement.docx"))
        .findFirst()
        .orElse(null);
    assertNotNull(reqDoc);
    assertNotNull(reqDoc.getMetadata());
    assertTrue(reqDoc.getMetadata().containsKey("techStack") ||
        reqDoc.getContent().contains("Vue.js"),
        "应提取技术栈信息");
}
```

---

#### TC-005-02: 相似项目推荐

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-005-02 |
| **测试名称** | 相似项目推荐验证 |
| **用户故事** | 故事5 - 项目知识沉淀 |
| **前置条件** | 1. 多个项目知识库已建立<br>2. 项目特征已提取 |
| **测试数据** | 当前项目: 电商平台 (技术栈: Java, Vue, MySQL)<br>历史项目: OA系统(Java, React, Oracle), 小程序( Node, Vue, MongoDB), 大数据平台(Java, Spark, Hadoop) |
| **测试步骤** | 1. 提取当前项目特征<br>2. 计算与历史项目相似度<br>3. 验证推荐结果 |
| **预期结果** | 1. OA系统相似度最高 (同为Java企业应用)<br>2. 相似度分数 > 0.7的项目被推荐<br>3. 推荐项目按相似度排序 |
| **验证SQL** | 查询project_recommendations表 |
| **优先级** | P1 |

**实际验证代码**:
```java
@Test
void testSimilarProjectRecommendation() {
    // Given - 准备历史项目数据
    String currentProjectId = "project-ecommerce";
    
    // 创建历史项目知识库并添加特征
    createProjectWithFeatures("project-oa", "OA系统", 
        Arrays.asList("Java", "React", "Oracle", "企业应用"));
    createProjectWithFeatures("project-miniapp", "小程序", 
        Arrays.asList("Node.js", "Vue.js", "MongoDB", "移动端"));
    createProjectWithFeatures("project-bigdata", "大数据平台", 
        Arrays.asList("Java", "Spark", "Hadoop", "数据分析"));
    
    // 当前项目特征
    createProjectWithFeatures(currentProjectId, "电商平台", 
        Arrays.asList("Java", "Vue.js", "MySQL", "电商"));
    
    // When
    List<ProjectRecommendation> recommendations = 
        projectKnowledgeSkill.discoverSimilarProjects(currentProjectId);
    
    // Then
    assertNotNull(recommendations);
    assertFalse(recommendations.isEmpty(), "应有推荐项目");
    
    // 验证相似度阈值
    assertTrue(recommendations.stream()
        .allMatch(r -> r.getSimilarityScore() > 0.7),
        "推荐项目相似度应 > 0.7");
    
    // 验证排序
    for (int i = 0; i < recommendations.size() - 1; i++) {
        assertTrue(recommendations.get(i).getSimilarityScore() >= 
            recommendations.get(i + 1).getSimilarityScore(),
            "应按相似度降序排列");
    }
    
    // 验证OA系统最相似 (同为Java企业应用)
    ProjectRecommendation topRecommendation = recommendations.get(0);
    assertEquals("project-oa", topRecommendation.getProjectId());
    assertTrue(topRecommendation.getSimilarityScore() > 0.8,
        "OA系统与电商平台应有较高相似度");
    
    // 验证推荐理由
    assertNotNull(topRecommendation.getReason());
    assertTrue(topRecommendation.getReason().contains("Java"),
        "推荐理由应提及共同技术栈");
    
    // 验证数据库记录
    List<ProjectRecommendationRecord> records = 
        projectRecommendationRepository.findBySourceProjectId(currentProjectId);
    assertFalse(records.isEmpty());
}

private void createProjectWithFeatures(String projectId, String name, List<String> techStack) {
    // 创建项目知识库
    KnowledgeBase kb = knowledgeBaseService.create(
        "user-001",
        KnowledgeBaseCreateRequest.builder()
            .name(name)
            .type(KnowledgeBaseType.PROJECT)
            .projectId(projectId)
            .build()
    );
    
    // 添加技术栈文档作为特征
    Document featureDoc = knowledgeBaseService.addTextKnowledge(
        "user-001",
        kb.getId(),
        name + "技术栈",
        "技术栈: " + String.join(", ", techStack),
        techStack
    );
}
```

---

## 七、集成测试用例

### 7.1 端到端流程测试

#### TC-INT-01: 完整知识贡献与问答流程

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-INT-01 |
| **测试名称** | 完整知识贡献与问答流程验证 |
| **测试范围** | 故事1 + 故事3 集成 |
| **前置条件** | 1. 完整系统环境<br>2. 所有服务可用 |
| **测试步骤** | 1. 用户创建知识库<br>2. 上传文档<br>3. 设置分享权限<br>4. 其他用户通过分享访问<br>5. 进行智能问答 |
| **预期结果** | 完整流程无错误，数据一致性正确 |
| **验证点** | 数据在各模块间正确流转 |
| **优先级** | P0 |

**实际验证代码**:
```java
@Test
void testEndToEndKnowledgeContributionAndQA() {
    // === 步骤1: 用户A创建知识库 ===
    String userA = "user-001";
    KnowledgeBase kb = knowledgeBaseService.create(
        userA,
        KnowledgeBaseCreateRequest.builder()
            .name("产品知识库")
            .visibility(Visibility.PRIVATE)
            .build()
    );
    assertNotNull(kb.getId());
    
    // === 步骤2: 上传文档 ===
    File docFile = new File("test-data/product_manual.pdf");
    Document doc = knowledgeBaseService.uploadDocument(
        userA, kb.getId(), docFile,
        DocumentMetadata.builder()
            .filename("product_manual.pdf")
            .type("产品文档")
            .build()
    );
    
    // 等待处理完成
    await().atMost(30, TimeUnit.SECONDS).until(() ->
        knowledgeBaseService.getDocument(doc.getId()).getStatus() == DocumentStatus.INDEXED
    );
    
    // === 步骤3: 设置分享权限 ===
    ShareResult share = shareService.createShare(
        kb.getId(),
        ShareOptions.builder()
            .targetUsers(Arrays.asList("user-002"))
            .permission(Permission.READ)
            .expireDays(30)
            .build()
    );
    
    // === 步骤4: 用户B通过分享访问 ===
    String userB = "user-002";
    assertTrue(permissionService.hasPermission(kb.getId(), userB, Permission.READ));
    
    List<Document> accessibleDocs = knowledgeBaseService.listDocuments(
        kb.getId(),
        DocumentQueryRequest.builder().build()
    );
    assertEquals(1, accessibleDocs.size());
    
    // === 步骤5: 用户B进行智能问答 ===
    QAResult result = intelligentQAService.intelligentQA(
        kb.getId(),
        "产品的主要功能有哪些？",
        QAOptions.builder().includeSources(true).build()
    );
    
    assertNotNull(result.getAnswer());
    assertFalse(result.getSources().isEmpty());
    assertEquals(doc.getId(), result.getSources().get(0).getDocId());
    
    // === 验证数据一致性 ===
    // 验证知识库统计
    KnowledgeBaseStats stats = knowledgeBaseService.getStats(kb.getId());
    assertEquals(1, stats.getDocumentCount());
    assertTrue(stats.getTotalChunks() > 0);
    
    // 验证访问日志
    List<AccessLog> logs = accessLogRepository.findByKbId(kb.getId());
    assertTrue(logs.stream().anyMatch(l -> l.getUserId().equals(userB)));
    
    // 验证分享统计
    ShareStats shareStats = shareService.getStats(share.getShareId());
    assertEquals(1, shareStats.getAccessCount());
}
```

---

## 八、性能测试用例

### 8.1 响应时间测试

#### TC-PERF-01: 文档处理性能

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-PERF-01 |
| **测试名称** | 文档处理性能验证 |
| **测试数据** | 10MB PDF文档 |
| **测试步骤** | 1. 上传10MB文档<br>2. 记录处理时间<br>3. 验证在30秒内完成 |
| **预期结果** | 文档处理时间 < 30秒 |
| **优先级** | P1 |

**实际验证代码**:
```java
@Test
void testDocumentProcessingPerformance() {
    // Given
    String kbId = "kb-perf-test";
    File largeFile = new File("test-data/large_document_10mb.pdf");
    assertTrue(largeFile.exists());
    assertTrue(largeFile.length() > 10 * 1024 * 1024, "文件应大于10MB");
    
    // When
    long startTime = System.currentTimeMillis();
    
    Document doc = knowledgeBaseService.uploadDocument(
        "user-001", kbId, largeFile,
        DocumentMetadata.builder().filename("large_document.pdf").build()
    );
    
    // 等待处理完成
    await().atMost(60, TimeUnit.SECONDS).until(() ->
        knowledgeBaseService.getDocument(doc.getId()).getStatus() == DocumentStatus.INDEXED
    );
    
    long processingTime = System.currentTimeMillis() - startTime;
    
    // Then
    System.out.println("文档处理时间: " + processingTime + "ms");
    assertTrue(processingTime < 30000, "10MB文档处理应小于30秒");
    
    // 验证向量已创建
    List<DocumentChunk> chunks = documentChunkRepository.findByDocId(doc.getId());
    assertFalse(chunks.isEmpty());
}
```

---

#### TC-PERF-02: 问答响应性能

| 项目 | 内容 |
|------|------|
| **测试ID** | TC-PERF-02 |
| **测试名称** | 问答响应性能验证 |
| **测试数据** | 100个并发问答请求 |
| **测试步骤** | 1. 准备测试问题<br>2. 并发发送100个请求<br>3. 统计响应时间分布 |
| **预期结果** | P99响应时间 < 3秒 |
| **优先级** | P1 |

**实际验证代码**:
```java
@Test
void testQAResponsePerformance() throws InterruptedException {
    // Given
    String kbId = "kb-perf-test";
    int concurrentRequests = 100;
    List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch latch = new CountDownLatch(concurrentRequests);
    
    String[] questions = {
        "年假有多少天？",
        "请假流程是什么？",
        "绩效考核周期是多久？",
        "加班怎么计算？",
        "报销流程是什么？"
    };
    
    // When - 并发测试
    ExecutorService executor = Executors.newFixedThreadPool(20);
    
    for (int i = 0; i < concurrentRequests; i++) {
        final String question = questions[i % questions.length];
        executor.submit(() -> {
            try {
                long start = System.currentTimeMillis();
                QAResult result = intelligentQAService.intelligentQA(
                    kbId, question, QAOptions.builder().build()
                );
                long time = System.currentTimeMillis() - start;
                responseTimes.add(time);
                assertNotNull(result.getAnswer());
            } finally {
                latch.countDown();
            }
        });
    }
    
    latch.await(5, TimeUnit.MINUTES);
    executor.shutdown();
    
    // Then - 统计分析
    Collections.sort(responseTimes);
    
    long p50 = responseTimes.get(responseTimes.size() / 2);
    long p95 = responseTimes.get((int) (responseTimes.size() * 0.95));
    long p99 = responseTimes.get((int) (responseTimes.size() * 0.99));
    long avg = responseTimes.stream().mapToLong(Long::longValue).sum() / responseTimes.size();
    
    System.out.println("P50: " + p50 + "ms");
    System.out.println("P95: " + p95 + "ms");
    System.out.println("P99: " + p99 + "ms");
    System.out.println("平均: " + avg + "ms");
    
    assertTrue(p99 < 3000, "P99响应时间应小于3秒");
    assertTrue(p95 < 2000, "P95响应时间应小于2秒");
}
```

---

## 附录

### A. 测试数据SQL脚本

```sql
-- 清理测试数据
DELETE FROM document_chunks WHERE doc_id IN (SELECT id FROM documents WHERE kb_id LIKE 'kb-test%');
DELETE FROM documents WHERE kb_id LIKE 'kb-test%';
DELETE FROM knowledge_bases WHERE id LIKE 'kb-test%';
DELETE FROM conversation_logs WHERE session_id LIKE 'session-test%';
DELETE FROM access_logs WHERE kb_id LIKE 'kb-test%';
DELETE FROM share_records WHERE kb_id LIKE 'kb-test%';
DELETE FROM learning_paths WHERE employee_id LIKE 'user-test%';

-- 创建测试用户
INSERT INTO users (id, username, email, department_id, role, created_at) VALUES
('user-test-001', '测试用户A', 'test-a@company.com', 'dept-test', 'EMPLOYEE', NOW()),
('user-test-002', '测试用户B', 'test-b@company.com', 'dept-test', 'EMPLOYEE', NOW()),
('user-test-003', '测试管理员', 'test-admin@company.com', 'dept-test', 'ADMIN', NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();
```

### B. 测试执行检查清单

- [ ] 测试环境准备完成
- [ ] 测试数据已导入
- [ ] 所有服务正常运行
- [ ] 测试用例执行通过
- [ ] 缺陷已记录并修复
- [ ] 性能指标达标
- [ ] 测试报告已生成

---

**文档维护**: Ooder QA Team  
**最后更新**: 2026-03-07
