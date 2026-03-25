# 对话服务 - 基础

## 接口

**`ConversationService`** - `net.ooder.scene.skill.conversation.ConversationService`

## 核心方法

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `chat(sessionId, content)` | 简洁对话 | `Message` |
| `chatWithTools(sessionId, content, tools)` | 带工具执行 | `Message` |
| `chatStream(sessionId, content, handler)` | 流式对话 | `void` |
| `sendMessage(sessionId, request)` | 完整对话 | `MessageResponse` |
| `createConversation(userId, request)` | 创建对话 | `Conversation` |
| `getHistory(sessionId, limit)` | 获取历史 | `List<Message>` |

## 简洁API示例

### 基础对话

```java
Message response = conversationService.chat(
    "session-001",
    "你好"
);
System.out.println(response.getContent());
```

### 带工具执行

```java
List<String> tools = Arrays.asList("search_knowledge", "get_time");

Message response = conversationService.chatWithTools(
    "session-001",
    "现在几点了？",
    tools
);
```

### 流式对话

```java
conversationService.chatStream(
    "session-001",
    "讲个故事",
    new StreamMessageHandler() {
        @Override
        public void onContent(String chunk) {
            System.out.print(chunk);
        }
        
        @Override
        public void onComplete(MessageResponse response) {
            System.out.println("\n[完成]");
        }
        
        @Override
        public void onError(String error) {
            System.err.println("错误: " + error);
        }
    }
);
```

## 完整对话流程

```java
// 1. 创建对话
ConversationCreateRequest createRequest = new ConversationCreateRequest();
createRequest.setTitle("招聘咨询");
createRequest.setKbId("recruitment-kb");

Conversation conversation = conversationService.createConversation(
    "user-001",
    createRequest
);

// 2. 发送消息
MessageRequest request = new MessageRequest();
request.setContent("JD怎么写？");
request.setEnableRag(true);
request.setEnableTools(true);

MessageResponse response = conversationService.sendMessage(
    conversation.getConversationId(),
    request
);

// 3. 处理响应
System.out.println("回复: " + response.getContent());

// 4. 查看引用
for (SourceReference source : response.getSources()) {
    System.out.println("引用: " + source.getTitle());
}

// 5. 查看工具执行
for (ToolExecution exec : response.getToolExecutions()) {
    System.out.println("工具: " + exec.getToolName());
}
```

## 配置

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
