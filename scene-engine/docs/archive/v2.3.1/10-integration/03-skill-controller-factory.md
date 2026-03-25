# Skill 控制器工厂

## 概述

**`SkillControllerFactory`** - `net.ooder.scene.skill.SkillControllerFactory`

解决 Skill 控制器不是 Spring Bean 的问题，提供静态方式注册和管理 Skill 控制器，自动注入 SE 服务。

## 核心功能

| 功能 | 说明 |
|------|------|
| 控制器注册 | 注册 Skill 控制器类 |
| 服务注入 | 自动注入 SE 服务到控制器 |
| 多构造函数支持 | 支持多种构造函数模式 |
| Setter 注入 | 支持通过 setter 方法注入 |

## 使用方式

### 1. 基本使用

```java
// Skill 启动时注册控制器
SkillControllerFactory.register(ChatController.class);

// 获取控制器实例（已注入服务）
ChatController controller = SkillControllerFactory.getController(ChatController.class);
```

### 2. 构造函数注入

支持以下构造函数（按优先级）：

```java
// 方式1：双服务构造函数（推荐）
public class ChatController {
    private final ConversationService conversationService;
    private final TerminologyService terminologyService;
    
    public ChatController(ConversationService conversationService, 
                          TerminologyService terminologyService) {
        this.conversationService = conversationService;
        this.terminologyService = terminologyService;
    }
}

// 方式2：单服务构造函数
public class ChatController {
    private final ConversationService conversationService;
    
    public ChatController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }
}

// 方式3：无参构造函数 + setter 注入
public class ChatController {
    private ConversationService conversationService;
    private TerminologyService terminologyService;
    
    public ChatController() {
    }
    
    public void setConversationService(ConversationService service) {
        this.conversationService = service;
    }
    
    public void setTerminologyService(TerminologyService service) {
        this.terminologyService = service;
    }
}
```

### 3. 完整示例

```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    private final ConversationService conversationService;
    private final TerminologyService terminologyService;
    
    // 构造函数注入
    public ChatController(ConversationService conversationService,
                          TerminologyService terminologyService) {
        this.conversationService = conversationService;
        this.terminologyService = terminologyService;
    }
    
    @PostMapping("/sessions/{sessionId}/messages")
    public ResultModel<ChatMessage> sendMessage(
            @PathVariable String sessionId,
            @RequestBody ChatRequest request) {
        
        // 术语预处理
        String expanded = terminologyService.expandAbbreviations(request.getMessage());
        
        // 发送消息
        MessageResponse response = conversationService.chat(sessionId, expanded);
        
        return ResultModel.success(convertToChatMessage(response));
    }
}

// Skill 启动类
@SpringBootApplication
public class SkillLlmChatApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SkillLlmChatApplication.class, args);
        
        // 注册控制器
        SkillControllerFactory.register(ChatController.class);
    }
}
```

## 配置

```yaml
se:
  skill:
    enabled: true        # 启用 Skill 支持
    autoRegister: true   # 自动注册
```

## API 参考

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `initialize()` | 初始化工厂 | `void` |
| `register(controllerClass)` | 注册控制器 | `T` |
| `getController(controllerClass)` | 获取控制器 | `T` |
| `isInitialized()` | 检查是否初始化 | `boolean` |
| `reset()` | 重置工厂 | `void` |

## 注意事项

1. **初始化顺序**：`SkillControllerFactory.initialize()` 在 SE 启动时自动调用
2. **线程安全**：工厂是线程安全的，支持并发注册
3. **单例模式**：每个控制器类只创建一个实例
4. **空值检查**：获取服务时检查是否为 null

## 与 SceneServices 对比

| 方式 | 适用场景 | 代码复杂度 |
|------|----------|-----------|
| `SceneServices` | 简单使用，直接获取服务 | 低 |
| `SkillControllerFactory` | 需要控制器管理，依赖注入 | 中 |

推荐：复杂 Skill 使用 `SkillControllerFactory`，简单使用 `SceneServices`。
