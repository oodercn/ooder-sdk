# Ooder SDK SPI 整合任务历史

## 概述

本文档汇总了 Ooder SDK 项目中关于 SPI (Service Provider Interface) 整合的所有历史任务、架构设计和实现细节。

---

## 一、SPI 架构演进历史

### 1.1 初始阶段 (2026-04-03)

**背景问题**:
- JAR 包中的 Service/Repository 依赖 Spring 容器注入
- `SceneGroupRepository` 等 JPA 接口无法在 JAR 包中使用
- 多个 Skill 重复定义相同服务

**解决方案**:
采用 **SPI (Service Provider Interface)** 模式：
- 接口定义在 `skill-common` 模块（无 Spring 依赖）
- 实现由主应用提供（JPA/内存/MongoDB 等）
- 通过 `ServiceRegistry` 自动注入

**关键文档**:
- [Skill SPI 接口开发协同说明](e:/github/ooder-sdk/skill/skill-hotplug-starter/docs/Skill-SPI-开发协同说明.md)

---

## 二、SPI 模块结构

### 2.1 当前 SPI 模块清单

#### 2.1.1 ooder-spi-core (核心 SPI 模块)

**位置**: `e:\github\ooder-sdk\skill\_base\ooder-spi-core`

**当前版本**: 3.0.5 (已部署到 Maven 中央仓库)

**Maven 坐标**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-spi-core</artifactId>
    <version>3.0.5</version>
</dependency>
```

**包含的 SPI 接口**:

| 接口 | 包路径 | 用途 | 状态 |
|------|--------|------|------|
| `ImService` | `net.ooder.spi.im` | IM 服务接口 | ✅ 已完成 |
| `ImDeliveryDriver` | `net.ooder.spi.im` | IM 投递驱动 | ✅ 已完成 |
| `InboundHandler` | `net.ooder.spi.im.handler` | 入站处理器 | ✅ 已完成 |
| `RagEnhanceDriver` | `net.ooder.spi.rag` | RAG 增强驱动 | ✅ 已完成 |
| `WorkflowDriver` | `net.ooder.spi.workflow` | 工作流驱动 | ✅ 已完成 |
| `SpiServices` | `net.ooder.spi.facade` | SPI 服务门面 | ✅ 已完成 |
| `PageResult` | `net.ooder.spi.core` | 分页结果 | ✅ 已完成 |

**模型类**:
- `RagRelatedDocument` - RAG 相关文档
- `RagKnowledgeConfig` - RAG 知识配置
- `SendResult` - 发送结果
- `MessageContent` - 消息内容

#### 2.1.2 skill-common (通用 SPI 模块)

**位置**: `e:\github\ooder-sdk\skill\skill-common`

**当前版本**: 3.0.5

**Maven 坐标**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-common</artifactId>
    <version>3.0.5</version>
</dependency>
```

**包含的 SPI 接口**:

| 接口 | 包路径 | 用途 | 优先级 | 状态 |
|------|--------|------|--------|------|
| `SceneGroupStorage` | `spi/storage/` | 场景群组存储 | P0 | ✅ 已完成 |
| `LLMServiceProvider` | `spi/llm/` | LLM 服务提供者 | P1 | ⬜ 待开发 |
| `LlmConfigStorage` | `spi/llm/` | LLM 配置存储 | P1 | ⬜ 待开发 |
| `ConversationStorage` | `spi/llm/` | 对话存储 | P1 | ⬜ 待开发 |
| `KnowledgeBaseStorage` | `spi/knowledge/` | 知识库存储 | P1 | ⬜ 待开发 |
| `VectorStoreProvider` | `spi/knowledge/` | 向量存储 | P1 | ⬜ 待开发 |
| `EmbeddingProvider` | `spi/knowledge/` | 嵌入服务 | P1 | ⬜ 待开发 |
| `AgentStorage` | `spi/agent/` | Agent 存储 | P2 | ⬜ 待开发 |
| `AgentSessionStorage` | `spi/agent/` | 会话存储 | P2 | ⬜ 待开发 |
| `AgentMessageStorage` | `spi/agent/` | 消息存储 | P2 | ⬜ 待开发 |

---

## 三、SPI 架构设计

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              用户交互层                                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │   CLI Tool  │  │  llm-chat   │  │   Web UI    │  │      API Clients    │ │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘ │
└─────────┼────────────────┼────────────────┼────────────────────┼────────────┘
          │                │                │                    │
          └────────────────┴────────────────┴────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           统一接入层 (API Gateway)                           │
└─────────────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         Agent SDK 协议层 (南向协议)                           │
└─────────────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SceneEngine 场景引擎层                                │
└─────────────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Skill 运行时层                                       │
└─────────────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           SPI 服务层                                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │UserService  │  │StorageService│  │MessageService│  │  PermissionService │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 SPI 扩展机制

| 扩展类型 | 机制 | 说明 |
|----------|------|------|
| 水平扩展 | SPI 机制 | 通过 SPI 接口扩展服务 |
| 垂直扩展 | SceneEngine | 通过 SceneEngine 编排复杂流程 |
| 插件扩展 | Skill 机制 | 通过 Skill 机制扩展功能 |

---

## 四、关键实现细节

### 4.1 手动依赖注入机制

由于 ClassLoader 隔离，JAR 包中的 Controller 无法通过 Spring 的 `AutowireCapableBeanFactory.createBean()` 正确创建实例。系统已实现**手动依赖注入**机制。

**依赖解析优先级**:

1. Spring 容器按名称查找（`@Resource` 指定名称）
2. Spring 容器按类型查找
3. **ServiceRegistry 按接口类型查找**（当前 Skill 注册的服务）
4. **ServiceRegistry 按服务名称查找**（当前 Skill 注册的服务）
5. Spring 容器按字段名查找
6. Spring 容器按类名查找

**核心代码** (RouteRegistry.java):

```java
private Object resolveDependency(java.lang.reflect.Field field) {
    Class<?> fieldType = field.getType();
    
    // 1. @Resource 指定名称
    jakarta.annotation.Resource resource = field.getAnnotation(jakarta.annotation.Resource.class);
    if (resource != null && resource.name() != null && !resource.name().isEmpty()) {
        try {
            return applicationContext.getBean(resource.name(), fieldType);
        } catch (Exception e) { }
    }
    
    // 2. Spring 容器按类型
    try {
        return applicationContext.getBean(fieldType);
    } catch (Exception e) { }
    
    // 3. ServiceRegistry 按接口类型
    if (currentSkillId != null && serviceRegistry != null) {
        List<?> services = serviceRegistry.getServicesByInterface(fieldType);
        if (services != null && !services.isEmpty()) {
            return services.get(0);
        }
    }
    
    // 4. ServiceRegistry 按服务名称
    if (currentSkillId != null && serviceRegistry != null) {
        Map<String, ServiceProxy> skillServices = serviceRegistry.getServices(currentSkillId);
        if (skillServices != null) {
            ServiceProxy proxy = skillServices.get(field.getName());
            if (proxy != null && fieldType.isInstance(proxy.getProxy())) {
                return proxy.getProxy();
            }
        }
    }
    
    return null;
}
```

### 4.2 SPI 服务门面 (SpiServices)

```java
public class SpiServices {
    private static SpiServices instance;
    
    private ImService imService;
    private RagEnhanceDriver ragEnhanceDriver;
    private WorkflowDriver workflowDriver;
    
    public static synchronized void init(SpiServices services) {
        instance = services;
    }
    
    public static SpiServices getInstance() {
        return instance;
    }
    
    public static ImService getImService() {
        return instance != null ? instance.imService : null;
    }
    
    public static RagEnhanceDriver getRagEnhanceDriver() {
        return instance != null ? instance.ragEnhanceDriver : null;
    }
    
    public static WorkflowDriver getWorkflowDriver() {
        return instance != null ? instance.workflowDriver : null;
    }
}
```

---

## 五、版本历史

### 5.1 ooder-spi-core 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 3.0.5 | 2026-04-17 | 部署到 Maven 中央仓库，添加完整部署配置 |
| 3.2.0 | 2026-04-17 | 之前的开发版本 |

### 5.2 skill-common 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 3.0.5 | 2026-04-17 | 版本号更新（已存在于中央仓库） |
| 3.0.3 | 2026-04-17 | 之前的稳定版本 |
| 3.0.1 | 2026-04-03 | 新增手动依赖注入机制 |
| 1.0.0 | 2026-04-03 | 初始版本 |

---

## 六、待办任务清单

### P0 - 阻塞问题（必须完成）

- [x] `SceneGroupStorage` - 场景群组存储接口
- [x] `SceneGroupData` - 场景群组数据传输对象
- [x] `PageResult` - 分页结果类

### P1 - 多 Skill 共享

- [ ] `LLMServiceProvider` - LLM 服务提供者
- [ ] `LlmConfigStorage` - LLM 配置存储
- [ ] `ConversationStorage` - 对话存储
- [ ] `KnowledgeBaseStorage` - 知识库存储
- [ ] `VectorStoreProvider` - 向量存储
- [ ] `EmbeddingProvider` - 嵌入服务

### P2 - 逐步迁移

- [ ] `AgentStorage` - Agent 存储
- [ ] `AgentSessionStorage` - 会话存储
- [ ] `AgentMessageStorage` - 消息存储

### 其他任务

- [ ] PDF/Office 解析改为 SPI 动态加载
- [ ] 创建向量存储抽象层 SPI 接口
- [ ] 拆分 ooder-common 模块

---

## 七、相关文档

### 7.1 架构文档

- [总体架构概述](e:/github/ooder-sdk/skill/docs/architecture/01-overview/README.md)
- [Agent SDK 深度解析](e:/github/ooder-sdk/skill/docs/architecture/02-agent-sdk/README.md)
- [SceneEngine 场景引擎](e:/github/ooder-sdk/scene-engine/docs/README.md)
- [CLI 设计实现](e:/github/ooder-sdk/skill/docs/architecture/04-cli-design/README.md)

### 7.2 开发文档

- [Skill SPI 接口开发协同说明](e:/github/ooder-sdk/skill/skill-hotplug-starter/docs/Skill-SPI-开发协同说明.md)
- [依赖优化任务](e:/github/ooder-sdk/scene-engine/docs/collaboration/DEPENDENCY_OPTIMIZATION_TASKS.md)
- [CHANGELOG](e:/github/ooder-sdk/scene-engine/docs/CHANGELOG.md)

### 7.3 代码位置

**ooder-spi-core**:
```
e:\github\ooder-sdk\skill\_base\ooder-spi-core\
├── pom.xml
└── src\main\java\net\ooder\spi\
    ├── core\PageResult.java
    ├── facade\SpiServices.java
    ├── im\
    │   ├── ImService.java
    │   ├── ImDeliveryDriver.java
    │   ├── handler\InboundHandler.java
    │   └── model\
    │       ├── MessageContent.java
    │       └── SendResult.java
    ├── rag\
    │   ├── RagEnhanceDriver.java
    │   └── model\
    │       ├── RagKnowledgeConfig.java
    │       └── RagRelatedDocument.java
    └── workflow\WorkflowDriver.java
```

**skill-common**:
```
e:\github\ooder-sdk\skill\skill-common\
├── pom.xml
└── src\main\java\net\ooder\skill\common\
    └── spi\
        ├── storage\
        │   ├── SceneGroupStorage.java
        │   └── SceneGroupData.java
        ├── llm\
        ├── knowledge\
        └── agent\
```

---

## 八、Maven 中央仓库

### 8.1 已部署构件

**ooder-spi-core 3.0.5**:
- **部署ID**: `84492e6f-f114-4d08-bc63-e8720d1d5a0a`
- **状态**: 已上传，待手动发布
- **发布页面**: https://central.sonatype.com/publishing/deployments

**skill-common 3.0.5**:
- **状态**: 已存在于中央仓库
- **说明**: 版本 3.0.5 已存在，无法重复部署

### 8.2 依赖引用

```xml
<!-- ooder-spi-core -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-spi-core</artifactId>
    <version>3.0.5</version>
</dependency>

<!-- skill-common -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-common</artifactId>
    <version>3.0.5</version>
</dependency>
```

---

## 九、总结

SPI 整合是 Ooder SDK 架构演进中的重要里程碑，主要解决了以下问题：

1. **依赖注入问题**: 通过手动依赖注入机制解决 ClassLoader 隔离问题
2. **服务复用问题**: 通过 SPI 接口实现服务的统一定义和多实现
3. **模块解耦问题**: 通过 SPI 层实现模块间的松耦合

当前状态：
- ✅ ooder-spi-core 3.0.5 已部署到 Maven 中央仓库
- ✅ skill-common 3.0.5 已存在于 Maven 中央仓库
- ⬜ 多个 P1/P2 级别的 SPI 接口待开发

---

**文档创建时间**: 2026-04-17  
**最后更新**: 2026-04-17  
**作者**: Ooder SDK Team
