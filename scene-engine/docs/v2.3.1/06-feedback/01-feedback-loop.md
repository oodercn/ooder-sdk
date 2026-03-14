# 交互反馈服务

## 接口

**`InteractionFeedbackService`** - `net.ooder.scene.skill.knowledge.InteractionFeedbackService`

## 反馈循环

```
用户交互 → 数据收集 → 质量评估 → 知识提取 → 知识库更新 → 模型优化
    ↑                                                              ↓
    └──────────────────── 效果验证 ← 用户反馈 ←────────────────────┘
```

## 核心方法

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `recordInteraction(sessionId, query, response, context)` | 记录交互 | `void` |
| `recordFeedback(interactionId, type, content, userId)` | 记录反馈 | `void` |
| `extractQAPairs(sessionId)` | 提取问答对 | `List<QAPair>` |
| `archiveConversation(sessionId, kbId)` | 归档对话 | `String` |
| `triggerKnowledgeBaseUpdate(kbId)` | 触发更新 | `int` |
| `learnTerminology(context, term, definition)` | 学习术语 | `void` |
| `getStats(timeRange)` | 获取统计 | `InteractionStats` |

## 使用示例

### 记录交互

```java
Map<String, Object> context = new HashMap<>();
context.put("userId", "user-001");
context.put("enableRag", true);
context.put("sourceCount", 3);

feedbackService.recordInteraction(
    sessionId,
    "JD的招聘流程是什么？",
    "招聘流程包括...",
    context
);
```

### 用户反馈

```java
// 正面反馈
feedbackService.recordFeedback(
    interactionId,
    InteractionFeedbackService.FeedbackType.POSITIVE,
    "回答很详细",
    "user-001"
);

// 纠错反馈
feedbackService.recordFeedback(
    interactionId,
    InteractionFeedbackService.FeedbackType.CORRECTION,
    "第三步应该是面试安排",
    "user-001"
);
```

### 提取问答对

```java
List<QAPair> qaPairs = feedbackService.extractQAPairs(sessionId);

for (QAPair qa : qaPairs) {
    System.out.println("Q: " + qa.getQuestion());
    System.out.println("A: " + qa.getAnswer());
    System.out.println("质量: " + qa.getQualityScore());
}
```

### 归档与更新

```java
// 归档对话
String docId = feedbackService.archiveConversation(sessionId, "recruitment-kb");

// 触发知识库更新
int updatedCount = feedbackService.triggerKnowledgeBaseUpdate("recruitment-kb");
```

### 查看统计

```java
InteractionStats stats = feedbackService.getStats(24);

System.out.println("总交互: " + stats.getTotalInteractions());
System.out.println("正面反馈: " + stats.getPositiveFeedback());
System.out.println("提取问答对: " + stats.getExtractedQAPairs());
```

## 自动学习配置

```yaml
se:
  conversation:
    auto-learn: true
    knowledge:
      auto-update: true
```

启用后，高质量问答对会自动提取并更新到知识库。
