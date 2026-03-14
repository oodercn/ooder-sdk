# SPI 服务暴露

## 接口

**`SceneEngineServiceProvider`** - `net.ooder.scene.spi.SceneEngineServiceProvider`

## 暴露的服务

| 服务 | 接口 | 说明 |
|------|------|------|
| 对话服务 | `ConversationService` | 多轮对话管理 |
| 知识库服务 | `KnowledgeBaseService` | 知识库管理 |
| 术语服务 | `TerminologyService` | 术语解析与管理 |
| 交互反馈服务 | `InteractionFeedbackService` | 交互数据反馈 |
| 工具注册表 | `ToolRegistry` | 工具注册与发现 |
| 工具编排器 | `ToolOrchestrator` | 工具调用执行 |

## 在 Skill 中使用

### 基础用法

```java
// 加载 SPI 服务提供者
SceneEngineServiceProvider provider = ServiceLoader
    .load(SceneEngineServiceProvider.class)
    .findFirst()
    .orElseThrow(() -> new IllegalStateException("Provider not found"));

// 获取服务
ConversationService conversationService = provider.getConversationService();
KnowledgeBaseService knowledgeBaseService = provider.getKnowledgeBaseService();
TerminologyService terminologyService = provider.getTerminologyService();
```

### 完整示例

```java
@Service
public class MySkillService {
    
    private final SceneEngineServiceProvider provider;
    
    public MySkillService() {
        this.provider = ServiceLoader
            .load(SceneEngineServiceProvider.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("SceneEngineServiceProvider not found"));
    }
    
    public String chat(String userId, String message) {
        ConversationService convService = provider.getConversationService();
        
        Conversation conversation = convService.createConversation(
            userId,
            new ConversationCreateRequest("咨询")
        );
        
        Message response = convService.chat(
            conversation.getConversationId(),
            message
        );
        
        return response.getContent();
    }
    
    public String processWithTerminology(String query) {
        TerminologyService termService = provider.getTerminologyService();
        
        PreprocessedQuery preprocessed = termService.preprocess(query);
        return preprocessed.getExpandedQuery();
    }
}
```

### 检查服务可用性

```java
if (provider.isServiceAvailable(InteractionFeedbackService.class)) {
    InteractionFeedbackService feedbackService = provider.getInteractionFeedbackService();
    // 使用反馈服务
}
```

## Spring Boot 集成

### 配置类

```java
@Configuration
public class SceneEngineConfig {
    
    @Bean
    public SceneEngineServiceProvider sceneEngineServiceProvider(
            ConversationService conversationService,
            KnowledgeBaseService knowledgeBaseService,
            TerminologyService terminologyService,
            InteractionFeedbackService feedbackService,
            ToolRegistry toolRegistry,
            ToolOrchestrator toolOrchestrator) {
        
        return new DefaultSceneEngineServiceProvider(
            conversationService,
            knowledgeBaseService,
            terminologyService,
            feedbackService,
            toolRegistry,
            toolOrchestrator
        );
    }
}
```

### 注入使用

```java
@Service
public class MySkillService {
    
    @Autowired
    private SceneEngineServiceProvider provider;
    
    public void doSomething() {
        ConversationService conv = provider.getConversationService();
        // ...
    }
}
```

## 提供者信息

```java
String providerName = provider.getProviderName();      // "SceneEngine-Default"
String providerVersion = provider.getProviderVersion(); // "2.3.1"
```
