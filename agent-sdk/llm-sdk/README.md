# LLM SDK

## 简介

LLM SDK 是 Ooder Agent SDK 的完整 LLM 实现模块，提供多 LLM 驱动支持、Story/Will 编排、Memory 管理等完整功能。

## 模块定位

作为 LLM 功能的完整实现层，本模块：
- 提供多 LLM 驱动实现（BaiduWenxin、Spark、Mock）
- 实现 Story/Will 编排引擎
- 提供 Memory 管理功能
- 支持 NLP 处理
- 依赖 agent-sdk-api

## 核心功能

### LLM 驱动
- `BaiduWenxinDriver` - 百度文心驱动
- `SparkLlmDriver` - 讯飞星火驱动
- `MockLlmDriver` - 模拟驱动
- `AbstractLlmDriver` - 抽象驱动基类

### Story 编排
- `StoryManager` - Story 管理器
- `UserStory` - 用户故事
- `StoryStep` - 故事步骤
- `StoryContext` - 故事上下文
- `WillTransformer` - Will 转换器

### Will 引擎
- `WillManager` - Will 管理器
- `WillExpression` - Will 表达式
- `WillExecutor` - Will 执行器
- `WillParser` - Will 解析器
- `WillTransformer` - Will 转换器

### Memory 管理
- `MemoryBridge` - 记忆桥接
- `MemoryStore` - 记忆存储
- `ConversationMemory` - 对话记忆

### 多 LLM 适配
- `MultiLlmAdapterApi` - 多 LLM 适配 API
- `LlmSdk` - LLM SDK 入口

## 使用方式

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk</artifactId>
    <version>2.3</version>
</dependency>
```

## 快速开始

```java
import net.ooder.sdk.llm.LlmSdk;
import net.ooder.sdk.llm.adapter.MultiLlmAdapterApi;

public class LlmExample {
    public static void main(String[] args) {
        // 创建 LLM SDK 实例
        LlmSdk sdk = LlmSdkFactory.create();
        
        // 使用多 LLM 适配
        MultiLlmAdapterApi adapter = sdk.getMultiLlmAdapter();
        
        // 发送聊天请求
        ChatResponse response = adapter.chat(ChatRequest.builder()
            .message("Hello")
            .build());
    }
}
```

## 版本

- 当前版本: 2.3
- 兼容版本: Java 8+

## 许可证

MIT License
