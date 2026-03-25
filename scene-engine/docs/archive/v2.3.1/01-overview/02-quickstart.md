# 快速开始

## 环境要求

- JDK 1.8+
- Maven 3.6+
- Spring Boot 2.7.x 或 3.x

## Maven依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

## 基础对话示例

```java
@Service
public class ChatService {
    
    @Autowired
    private ConversationService conversationService;
    
    public String chat(String userId, String message) {
        // 1. 创建对话
        Conversation conversation = conversationService.createConversation(
            userId,
            new ConversationCreateRequest("咨询")
        );
        
        // 2. 发送消息（简洁API）
        Message response = conversationService.chat(
            conversation.getConversationId(),
            message
        );
        
        return response.getContent();
    }
}
```

## 带知识库的对话

```java
@Service
public class KnowledgeChatService {
    
    @Autowired
    private ConversationService conversationService;
    
    public String chatWithKnowledge(String userId, String message) {
        // 创建对话并关联知识库
        ConversationCreateRequest request = new ConversationCreateRequest();
        request.setTitle("知识库咨询");
        request.setKbId("my-kb");
        
        Conversation conversation = conversationService.createConversation(userId, request);
        
        // 发送消息（启用RAG）
        MessageRequest msgRequest = new MessageRequest();
        msgRequest.setContent(message);
        msgRequest.setEnableRag(true);
        msgRequest.setKbIds(Arrays.asList("my-kb"));
        
        MessageResponse response = conversationService.sendMessage(
            conversation.getConversationId(),
            msgRequest
        );
        
        // 返回带引用的回复
        StringBuilder result = new StringBuilder(response.getContent());
        result.append("\n\n参考来源:\n");
        
        for (SourceReference source : response.getSources()) {
            result.append("- ").append(source.getTitle())
                  .append(" (相关度: ").append(source.getScore()).append(")\n");
        }
        
        return result.toString();
    }
}
```

## 术语解析示例

```java
@Service
public class TerminologyService {
    
    @Autowired
    private TerminologyService terminologyService;
    
    public String processQuery(String query) {
        // 预处理查询（自动扩展缩写）
        PreprocessedQuery preprocessed = terminologyService.preprocess(query);
        
        System.out.println("原始: " + preprocessed.getOriginalQuery());
        System.out.println("扩展: " + preprocessed.getExpandedQuery());
        
        // 返回扩展后的查询用于检索
        return preprocessed.getExpandedQuery();
    }
}
```

## 配置示例

```yaml
se:
  conversation:
    enabled: true
    storage:
      type: file
      path: ${user.home}/.ooder/data/conversations
    auto-learn: true
    max-history: 100
```

## 下一步

- [核心API - LLM服务](../02-core-api/01-llm-service.md)
- [对话服务 - 基础对话](../03-conversation/01-basic.md)
- [知识库管理](../04-knowledge/01-knowledge-base.md)
