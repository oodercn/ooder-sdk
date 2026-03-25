# Skill 集成指南

## 概述

本文档说明如何在 Skill 插件中使用 SE 暴露的服务。

## 前提条件

- SE 2.3.1+ 已添加到项目依赖
- Spring Boot 2.7.x 或 3.x

## Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

## 使用方式

### 方式一：直接注入服务（推荐）

Skill 可以直接通过 `@Autowired` 注入 SE 服务：

```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private TerminologyService terminologyService;
    
    @Autowired
    private InteractionFeedbackService feedbackService;

    @PostMapping("/sessions/{sessionId}/messages")
    public ResultModel<ChatMessage> sendMessage(
            @PathVariable String sessionId,
            @RequestBody ChatRequest request) {
        
        // 1. 术语预处理
        String processedQuery = terminologyService.expandAbbreviations(
            request.getMessage()
        );
        
        // 2. 发送消息
        MessageResponse response = conversationService.chat(
            sessionId,
            processedQuery
        );
        
        // 3. 记录反馈
        feedbackService.recordInteraction(
            sessionId,
            request.getMessage(),
            response.getContent(),
            Map.of("userId", request.getUserId())
        );
        
        return ResultModel.success(convertToChatMessage(response));
    }
}
```

### 方式二：通过 SPI 获取服务

如果 Skill 无法直接注入，可以通过 SPI 获取：

```java
@Service
public class ChatService {
    
    private final ConversationService conversationService;
    
    public ChatService() {
        // 通过 SPI 获取服务提供者
        SceneEngineServiceProvider provider = ServiceLoader
            .load(SceneEngineServiceProvider.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("SE Provider not found"));
        
        this.conversationService = provider.getConversationService();
    }
}
```

## 可用服务列表

| 服务 | 注入方式 | 说明 |
|------|----------|------|
| `ConversationService` | `@Autowired` | 对话服务 |
| `TerminologyService` | `@Autowired` | 术语服务 |
| `InteractionFeedbackService` | `@Autowired` | 交互反馈服务 |
| `ToolRegistry` | `@Autowired` | 工具注册表 |
| `ToolOrchestrator` | `@Autowired` | 工具编排器 |
| `ConversationStorageService` | `@Autowired` | 对话存储服务 |

## 配置示例

在 Skill 的 `application.yml` 中配置：

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

## 注意事项

1. **服务依赖**：SE 服务依赖于 `LlmService`，确保已配置 LLM 服务
2. **存储路径**：确保存储路径有写入权限
3. **版本兼容**：Skill 使用的 SE 版本应与主应用一致

## 完整示例

```java
@SpringBootApplication
public class SkillLlmChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkillLlmChatApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private TerminologyService terminologyService;

    @PostMapping("/sessions")
    public ResultModel<String> createSession(@RequestBody CreateSessionRequest request) {
        Conversation conversation = conversationService.createConversation(
            request.getUserId(),
            new ConversationCreateRequest(request.getTitle())
        );
        return ResultModel.success(conversation.getConversationId());
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ResultModel<ChatMessage> sendMessage(
            @PathVariable String sessionId,
            @RequestBody ChatRequest request) {
        
        // 术语预处理
        String expanded = terminologyService.expandAbbreviations(request.getMessage());
        
        // 发送消息
        MessageResponse response = conversationService.chat(sessionId, expanded);
        
        return ResultModel.success(ChatMessage.builder()
            .content(response.getContent())
            .role("assistant")
            .timestamp(System.currentTimeMillis())
            .build());
    }
}
```
