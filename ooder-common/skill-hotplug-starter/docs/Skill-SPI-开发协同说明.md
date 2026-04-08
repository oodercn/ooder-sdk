# Skill SPI 接口开发协同说明

## 一、背景与目标

### 问题描述

当前 JAR 包中的 Service/Repository 依赖 Spring 容器注入，导致：
1. JAR 包加载后 Controller 无法获取依赖
2. `SceneGroupRepository` 等 JPA 接口无法在 JAR 包中使用
3. 多个 Skill 重复定义相同服务

### 解决方案

采用 **SPI (Service Provider Interface)** 模式：
- 接口定义在 `skill-common` 模块（无 Spring 依赖）
- 实现由主应用提供（JPA/内存/MongoDB 等）
- 通过 `ServiceRegistry` 自动注入

---

## 二、SPI 接口清单

### P0 - 阻塞问题（必须完成）

| 接口 | 位置 | 用途 | 状态 |
|------|------|------|------|
| `SceneGroupStorage` | `skill-common/spi/storage/` | 场景群组存储 | ✅ 已完成 |

### P1 - 多 Skill 共享

| 接口 | 位置 | 用途 | 状态 |
|------|------|------|------|
| `LLMServiceProvider` | `skill-common/spi/llm/` | LLM 服务提供者 | 待开发 |
| `LlmConfigStorage` | `skill-common/spi/llm/` | LLM 配置存储 | 待开发 |
| `ConversationStorage` | `skill-common/spi/llm/` | 对话存储 | 待开发 |
| `KnowledgeBaseStorage` | `skill-common/spi/knowledge/` | 知识库存储 | 待开发 |
| `VectorStoreProvider` | `skill-common/spi/knowledge/` | 向量存储 | 待开发 |
| `EmbeddingProvider` | `skill-common/spi/knowledge/` | 嵌入服务 | 待开发 |

### P2 - 逐步迁移

| 接口 | 位置 | 用途 | 状态 |
|------|------|------|------|
| `AgentStorage` | `skill-common/spi/agent/` | Agent 存储 | 待开发 |
| `AgentSessionStorage` | `skill-common/spi/agent/` | 会话存储 | 待开发 |
| `AgentMessageStorage` | `skill-common/spi/agent/` | 消息存储 | 待开发 |

---

## 三、开发规范

### 3.1 接口定义规范

```java
// 位置: skill-common/src/main/java/net/ooder/skill/common/spi/storage/SceneGroupStorage.java
package net.ooder.skill.common.spi.storage;

import java.util.List;
import java.util.Optional;

/**
 * 场景群组存储接口
 * 
 * 注意：
 * 1. 接口必须放在 skill-common 的 spi 包下
 * 2. 方法签名只使用 Java 标准类型或 skill-common 中的 DTO
 * 3. 不要依赖 Spring 框架
 */
public interface SceneGroupStorage {
    
    SceneGroupData save(SceneGroupData data);
    Optional<SceneGroupData> findById(String id);
    PageResult<SceneGroupData> findByOwnerId(String ownerId, int pageNum, int pageSize);
    List<SceneGroupData> findByStatus(String status);
    void deleteById(String id);
    long count();
}
```

### 3.2 DTO 定义规范

```java
// 位置: skill-common/src/main/java/net/ooder/skill/common/spi/storage/SceneGroupData.java
package net.ooder.skill.common.spi.storage;

import java.io.Serializable;
import java.util.List;

/**
 * 场景群组数据传输对象
 * 
 * 注意：
 * 1. 必须实现 Serializable
 * 2. 使用 Lombok 或手写 getter/setter
 * 3. 不要依赖 JPA 注解
 */
public class SceneGroupData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String ownerName;
    private String status;
    private List<String> capabilityIds;
    private List<String> participantIds;
    private Long createdAt;
    private Long updatedAt;
    
    // getter/setter 省略...
}
```

### 3.3 主应用实现规范

```java
// 位置: os/src/main/java/net/ooder/os/spi/JpaSceneGroupStorage.java
package net.ooder.os.spi;

import net.ooder.skill.common.spi.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class JpaSceneGroupStorage implements SceneGroupStorage {
    
    @Autowired
    private SceneGroupRepository repository;
    
    @Override
    public SceneGroupData save(SceneGroupData data) {
        SceneGroup entity = toEntity(data);
        SceneGroup saved = repository.save(entity);
        return toData(saved);
    }
    
    // 其他方法实现...
}
```

---

## 四、skill.yaml 配置

### 4.1 声明服务和依赖

```yaml
# skill-scenes/skill.yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-scenes
  name: Scene Management
  version: 1.0.0

spec:
  skillForm: PROVIDER
  
  # 声明提供的服务（必须声明，否则 Controller 依赖注入失败）
  services:
    - name: sceneService
      interface: net.ooder.skill.scenes.service.SceneService
      implementation: net.ooder.skill.scenes.service.impl.SceneServiceImpl
    - name: sceneGroupService
      interface: net.ooder.skill.scenes.service.SceneGroupService
      implementation: net.ooder.skill.scenes.service.impl.SceneGroupServiceImpl
  
  # 声明需要的 SPI 服务（可选）
  requires:
    - interface: net.ooder.skill.common.spi.storage.SceneGroupStorage
      name: sceneGroupStorage
      required: true
  
  # 声明 API 端点
  endpoints:
    - path: /api/v1/scenes/list
      method: POST
      controllerClass: net.ooder.skill.scenes.controller.SceneController
      methodName: listScenes
```

### 4.2 手动依赖注入机制

由于 ClassLoader 隔离，JAR 包中的 Controller 无法通过 Spring 的 `AutowireCapableBeanFactory.createBean()` 正确创建实例。系统已实现**手动依赖注入**机制。

#### 依赖解析优先级

RouteRegistry 在创建 Controller 时，按以下优先级解析依赖：

1. Spring 容器按名称查找（`@Resource` 指定名称）
2. Spring 容器按类型查找
3. **ServiceRegistry 按接口类型查找**（当前 Skill 注册的服务）
4. **ServiceRegistry 按服务名称查找**（当前 Skill 注册的服务）
5. Spring 容器按字段名查找
6. Spring 容器按类名查找

#### 核心代码

```java
// RouteRegistry.java 中的依赖解析
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

#### 手动注入流程

```java
// RouteRegistry.java
private Object createNewBean(Class<?> controllerClass, String className) throws Exception {
    try {
        // 优先尝试 Spring 自动装配
        return applicationContext.getAutowireCapableBeanFactory().createBean(controllerClass);
    } catch (Exception e) {
        // 回退到默认构造函数 + 手动依赖注入
        Object bean = controllerClass.getDeclaredConstructor().newInstance();
        manualInjectDependencies(bean, controllerClass);
        return bean;
    }
}

private void manualInjectDependencies(Object bean, Class<?> clazz) {
    Class<?> currentClass = clazz;
    while (currentClass != null && currentClass != Object.class) {
        for (Field field : currentClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class) ||
                field.isAnnotationPresent(Resource.class)) {
                Object dependency = resolveDependency(field);
                if (dependency != null) {
                    field.setAccessible(true);
                    field.set(bean, dependency);
                }
            }
        }
        currentClass = currentClass.getSuperclass();
    }
}
```

---

## 五、Controller 开发规范

### 5.1 字段命名约定

```java
@RestController
@RequestMapping("/api/v1/scenes")
public class SceneController {

    // 推荐：字段名与 skill.yaml 中服务名称一致
    @Autowired
    private SceneService sceneService;  // 匹配 services[0].name = "sceneService"
    
    // 或者：按接口类型匹配
    @Autowired
    private SceneGroupService sceneGroupService;

    @PostMapping("/list")
    public ResultModel<PageResult<SceneDTO>> listScenes(@RequestBody Map<String, Object> request) {
        // sceneService 已被自动注入，可直接使用
        return ResultModel.success(sceneService.listScenes(...));
    }
}
```

### 5.2 Service 实现规范

```java
@Service
public class SceneServiceImpl implements SceneService {

    // 推荐：无参构造函数 + 内部数据存储
    private final Map<String, SceneDTO> dataStore = new ConcurrentHashMap<>();
    
    // 如果需要 SPI 服务
    private SceneGroupStorage storage;  // 通过构造函数注入
    
    // 无参构造函数（必须）
    public SceneServiceImpl() {
        // 初始化内部状态
    }
    
    // 带参数构造函数（可选）
    public SceneServiceImpl(SceneGroupStorage storage) {
        this.storage = storage;
    }

    @Override
    public PageResult<SceneDTO> listScenes(String status, int pageNum, int pageSize) {
        // 实现逻辑
    }
}
```

---

## 六、验证方法

### 6.1 检查日志

启动时观察以下日志，确认依赖注入成功：

```
INFO  RouteRegistry - Registering 16 routes for skill: skill-scenes
INFO  ServiceRegistry - Created service instance with default constructor: SceneServiceImpl
INFO  RouteRegistry - Manually injected dependency: sceneService -> SceneServiceImpl in SceneController
INFO  RouteRegistry - Successfully registered route: POST /api/v1/scenes/list to Spring MVC
```

### 6.2 API 测试

```powershell
# 测试场景列表接口
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/scenes/list" `
    -Method POST -ContentType "application/json" `
    -Body '{"pageNum":1,"pageSize":10}'

# 预期结果：返回正常数据，不是 NullPointerException
```

### 6.3 验证清单

- [x] `skill.yaml` 中声明 `services` 部分
- [x] Service 实现类有无参构造函数
- [x] Controller 字段名与 `services[].name` 一致
- [x] JAR 包中包含 `skill.yaml` 文件
- [x] 启动日志显示 "Manually injected dependency"
- [x] API 返回正常数据

---

## 七、常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| NullPointerException | Controller 依赖未注入 | 检查 skill.yaml 中 services 声明 |
| ClassNotFoundException | 类加载失败 | 检查 JAR 包依赖是否完整 |
| BeanCreationException | Spring 创建 Bean 失败 | 确保无参构造函数存在 |
| Service not found | ServiceRegistry 中无此服务 | 确保 services 配置正确且加载成功 |
| 字段名不匹配 | 字段名与 services[].name 不一致 | 修改字段名或 services 配置 |

---

## 八、文件路径汇总

### 8.1 skill-common 新增文件

```
skills/_system/skill-common/src/main/java/net/ooder/skill/common/spi/
├── storage/
│   ├── SceneGroupStorage.java      # P0 ✅
│   ├── SceneGroupData.java         # P0 ✅
│   └── PageResult.java             # P0 ✅
├── llm/
│   ├── LLMServiceProvider.java     # P1
│   ├── LlmConfigStorage.java       # P1
│   └── ConversationStorage.java    # P1
├── knowledge/
│   ├── KnowledgeBaseStorage.java   # P1
│   ├── VectorStoreProvider.java    # P1
│   └── EmbeddingProvider.java      # P1
└── agent/
    ├── AgentStorage.java           # P2
    ├── AgentSessionStorage.java    # P2
    └── AgentMessageStorage.java    # P2
```

### 8.2 os 主应用新增文件

```
os/src/main/java/net/ooder/os/spi/
├── JpaSceneGroupStorage.java       # P0 ✅
├── InMemorySceneGroupStorage.java  # P0 ✅
├── JpaLlmConfigStorage.java        # P1
├── JpaConversationStorage.java     # P1
├── JpaKnowledgeBaseStorage.java    # P1
├── MilvusVectorStoreProvider.java  # P1
├── DashScopeEmbeddingProvider.java # P1
├── JpaAgentStorage.java            # P2
├── JpaAgentSessionStorage.java     # P2
└── JpaAgentMessageStorage.java     # P2
```

---

## 九、版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 3.0.1 | 2026-04-03 | 新增手动依赖注入机制，解决 ClassLoader 隔离问题 |
| 1.0.0 | 2026-04-03 | 初始版本 |

---

文档创建时间: 2026-04-03
最后更新: 2026-04-03 22:00
