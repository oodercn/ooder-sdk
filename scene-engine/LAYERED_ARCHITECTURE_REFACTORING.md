# Scene Engine 分层架构整理建议

**整理日期**: 2026-03-06  
**当前状态**: 单体 jar 包，混合多层次职责  
**目标**: 按照分层架构原则，拆分为独立的 jar 包

---

## 一、当前架构分析

### 1.1 当前包结构

```
net.ooder.scene/
├── asset/           # 资产管理（领域层）
├── audit/           # 审计服务（服务层）
├── config/          # 配置类（配置层）
├── core/            # 核心引擎（混合：接口+实现+服务）
│   ├── driver/      # 驱动管理
│   ├── impl/        # 引擎实现
│   ├── provider/    # 提供者实现
│   ├── secure/      # 安全代理
│   ├── security/    # 安全服务
│   └── service/     # 服务接口
├── discovery/       # 发现服务（服务层）
├── engine/          # 引擎管理（领域层）
├── event/           # 事件系统（基础设施层）
├── llm/             # LLM 代理（服务层）
├── monitor/         # 监控服务（服务层）
├── protocol/        # 协议层（基础设施层）
├── provider/        # 提供者接口（接口层）
├── security/        # 安全服务（服务层）
├── service/         # 服务接口（接口层）
├── session/         # 会话管理（服务层）
├── skill/           # 技能管理（混合：领域+服务+应用）
│   ├── adapter/     # 适配器
│   ├── audit/       # 审计
│   ├── classification/  # 分类（领域层）
│   ├── coordinator/ # 协调器（应用层）
│   ├── instance/    # 实例管理
│   ├── knowledge/   # 知识库
│   ├── model/       # 领域模型
│   ├── proxy/       # 代理
│   ├── rag/         # RAG
│   ├── runtime/     # 运行时
│   ├── security/    # 安全
│   ├── session/     # 会话
│   ├── state/       # 状态
│   └── vector/      # 向量
├── ui/              # UI 管理（混合：表现层+服务层）
│   ├── NexusUiController.java    # REST Controller（表现层）
│   ├── NexusUiLoader.java        # UI 加载器（应用层）
│   ├── NexusUiRegistry.java      # 注册接口（领域层）
│   ├── NexusUiRegistryImpl.java  # 注册实现（基础设施层）
│   ├── NexusUiConfig.java        # 配置（DTO）
│   ├── MenuConfig.java           # 菜单配置（DTO）
│   └── RouteConfig.java          # 路由配置（DTO）
└── workflow/        # 工作流（服务层）
```

### 1.2 问题分析

| 问题 | 说明 |
|------|------|
| **层次混合** | 单个 jar 包含表现层、服务层、领域层、基础设施层 |
| **职责不清** | `ui/` 包混合了 Controller、Service、Repository |
| **依赖混乱** | 核心引擎依赖 Spring 注解，违反原则 |
| **难以测试** | 层次耦合，单元测试困难 |
| **难以复用** | 无法单独使用某个层次的组件 |

---

## 二、NexusUiController 分层定位

### 2.1 分层架构标准

```
┌─────────────────────────────────────────────────────────┐
│                    表现层 (Presentation)                 │
│  Controller、REST API、WebSocket、GraphQL                │
├─────────────────────────────────────────────────────────┤
│                    应用层 (Application)                  │
│  Service、Coordinator、Facade、UseCase                   │
├─────────────────────────────────────────────────────────┤
│                    领域层 (Domain)                       │
│  Entity、Value Object、Repository Interface、Domain Service │
├─────────────────────────────────────────────────────────┤
│                    基础设施层 (Infrastructure)           │
│  Repository Impl、External Service、Persistence          │
└─────────────────────────────────────────────────────────┘
```

### 2.2 NexusUiController 定位

**NexusUiController 属于表现层 (Presentation Layer)**

职责：
- 接收 HTTP 请求
- 验证请求参数
- 调用应用层服务
- 返回 HTTP 响应

**当前问题**：
- `NexusUiController` 在 `ui/` 包中，与 `NexusUiLoader`（应用层）、`NexusUiRegistry`（领域层）混合
- 违反了分层架构原则

---

## 三、建议的分层架构

### 3.1 模块拆分方案

```
scene-engine/
├── scene-engine-api/           # 表现层（API Layer）
│   └── src/main/java/net/ooder/scene/
│       ├── controller/
│       │   ├── UiController.java          # UI 管理 API
│       │   ├── SkillController.java       # 技能管理 API
│       │   ├── SceneController.java       # 场景管理 API
│       │   └── DiscoveryController.java   # 发现服务 API
│       ├── dto/
│       │   ├── UiConfigDTO.java
│       │   ├── SkillInstallDTO.java
│       │   └── ...
│       └── config/
│           └── ApiConfiguration.java
│
├── scene-engine-application/   # 应用层（Application Layer）
│   └── src/main/java/net/ooder/scene/
│       ├── service/
│       │   ├── UiAppService.java          # UI 应用服务
│       │   ├── SkillAppService.java       # 技能应用服务
│       │   └── SceneAppService.java       # 场景应用服务
│       ├── coordinator/
│       │   ├── InstallCoordinator.java    # 安装协调器
│       │   └── DiscoveryCoordinator.java  # 发现协调器
│       └── facade/
│           └── SceneEngineFacade.java     # 门面服务
│
├── scene-engine-domain/        # 领域层（Domain Layer）
│   └── src/main/java/net/ooder/scene/
│       ├── model/
│       │   ├── RichSkill.java             # 技能充血模型
│       │   ├── Scene.java                 # 场景实体
│       │   └── ...
│       ├── classification/
│       │   ├── SceneSkillCategory.java    # 分类枚举
│       │   └── SceneSkillCategoryDetector.java
│       ├── repository/
│       │   ├── UiRegistry.java            # UI 注册接口
│       │   ├── SkillRepository.java       # 技能仓库接口
│       │   └── SceneRepository.java       # 场景仓库接口
│       └── service/
│           ├── UiDomainService.java       # UI 领域服务
│           └── SkillDomainService.java    # 技能领域服务
│
├── scene-engine-infrastructure/  # 基础设施层（Infrastructure Layer）
│   └── src/main/java/net/ooder/scene/
│       ├── persistence/
│       │   ├── UiRegistryImpl.java        # UI 注册实现
│       │   ├── SkillRepositoryImpl.java   # 技能仓库实现
│       │   └── ...
│       ├── external/
│       │   ├── GitHubClient.java          # GitHub 客户端
│       │   └── GiteeClient.java           # Gitee 客户端
│       ├── config/
│       │   ├── SceneEngineAutoConfiguration.java
│       │   └── ...
│       └── event/
│           ├── SceneEventPublisher.java
│           └── ...
│
└── scene-engine-core/          # 核心引擎（Core Engine）
    └── src/main/java/net/ooder/scene/
        ├── engine/
        │   ├── SceneEngine.java           # 引擎接口
        │   └── SceneEngineImpl.java       # 引擎实现
        ├── provider/
        │   └── ...                         # 提供者
        └── security/
            └── ...                         # 安全组件
```

### 3.2 模块依赖关系

```
┌─────────────────┐
│ scene-engine-api │
└────────┬────────┘
         │ depends on
         ▼
┌─────────────────────┐
│ scene-engine-application │
└────────┬────────┘
         │ depends on
         ▼
┌─────────────────┐
│ scene-engine-domain │
└────────┬────────┘
         │ depends on
         ▼
┌─────────────────────────┐
│ scene-engine-infrastructure │
└─────────────────────────┘

scene-engine-core (独立模块，被其他模块依赖)
```

---

## 四、NexusUiController 迁移方案

### 4.1 当前位置

```
scene-engine/
└── src/main/java/net/ooder/scene/ui/
    └── NexusUiController.java  ← 当前位置（混合在 ui/ 包中）
```

### 4.2 建议位置

```
scene-engine-api/
└── src/main/java/net/ooder/scene/controller/
    └── UiController.java  ← 新位置（表现层）
```

### 4.3 迁移步骤

1. **创建 scene-engine-api 模块**
2. **移动 Controller 类**
   - `NexusUiController.java` → `UiController.java`
3. **创建 DTO 类**
   - `NexusUiConfig.java` → `UiConfigDTO.java`
   - `MenuConfig.java` → `MenuConfigDTO.java`
   - `RouteConfig.java` → `RouteConfigDTO.java`
4. **创建应用服务**
   - `NexusUiLoader.java` → `UiAppService.java`（在 scene-engine-application 模块）
5. **创建领域接口和实现**
   - `NexusUiRegistry.java` → `UiRegistry.java`（在 scene-engine-domain 模块）
   - `NexusUiRegistryImpl.java` → `UiRegistryImpl.java`（在 scene-engine-infrastructure 模块）

### 4.4 迁移后代码结构

**scene-engine-api 模块**:
```java
// UiController.java
@RestController
@RequestMapping("/api/v1/ui")
public class UiController {

    private final UiAppService uiAppService;

    public UiController(UiAppService uiAppService) {
        this.uiAppService = uiAppService;
    }

    @GetMapping
    public ResponseEntity<List<UiConfigDTO>> listAllUis() {
        return ResponseEntity.ok(uiAppService.listAll());
    }

    @PostMapping("/{skillId}/load")
    public ResponseEntity<UiConfigDTO> loadUiSkill(@PathVariable String skillId) {
        return ResponseEntity.ok(uiAppService.load(skillId));
    }
    
    // ...
}
```

**scene-engine-application 模块**:
```java
// UiAppService.java
public class UiAppService {

    private final UiRegistry uiRegistry;
    private final AuditService auditService;

    public UiAppService(UiRegistry uiRegistry, AuditService auditService) {
        this.uiRegistry = uiRegistry;
        this.auditService = auditService;
    }

    public List<UiConfigDTO> listAll() {
        return uiRegistry.listAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public UiConfigDTO load(String skillId) {
        // 审计日志
        auditService.log(AuditEntry.builder()
            .operation("UI_SKILL_LOAD")
            .skillId(skillId)
            .build());
        
        return toDTO(uiRegistry.load(skillId));
    }
    
    // ...
}
```

**scene-engine-domain 模块**:
```java
// UiRegistry.java
public interface UiRegistry {
    List<UiConfig> listAll();
    Optional<UiConfig> get(String skillId);
    UiConfig load(String skillId);
    void unload(String skillId);
    boolean isRegistered(String skillId);
}
```

**scene-engine-infrastructure 模块**:
```java
// UiRegistryImpl.java
public class UiRegistryImpl implements UiRegistry {

    private final Map<String, UiConfig> registry = new ConcurrentHashMap<>();

    @Override
    public List<UiConfig> listAll() {
        return new ArrayList<>(registry.values());
    }

    @Override
    public Optional<UiConfig> get(String skillId) {
        return Optional.ofNullable(registry.get(skillId));
    }

    @Override
    public UiConfig load(String skillId) {
        // 加载逻辑
    }

    @Override
    public void unload(String skillId) {
        registry.remove(skillId);
    }

    @Override
    public boolean isRegistered(String skillId) {
        return registry.containsKey(skillId);
    }
}
```

---

## 五、分层架构规范

### 5.1 命名规范

| 层次 | 包名 | 类后缀 | 示例 |
|------|------|--------|------|
| 表现层 | controller | Controller | UiController |
| 应用层 | service | AppService/Service | UiAppService |
| 领域层 | model/repository/service | 无/Repository/DomainService | RichSkill, UiRegistry |
| 基础设施层 | persistence/external/config | Impl/Client/Configuration | UiRegistryImpl |

### 5.2 依赖规则

| 层次 | 可依赖 | 不可依赖 |
|------|--------|----------|
| 表现层 | 应用层 | 领域层、基础设施层 |
| 应用层 | 领域层 | 基础设施层 |
| 领域层 | 无 | 任何其他层 |
| 基础设施层 | 领域层 | 应用层、表现层 |

### 5.3 注解使用规则

| 层次 | 允许的注解 | 禁止的注解 |
|------|-----------|-----------|
| 表现层 | @RestController, @RequestMapping, @Autowired | - |
| 应用层 | @Service（可选） | @Autowired（建议构造函数注入） |
| 领域层 | 无 | 所有 Spring 注解 |
| 基础设施层 | @Configuration, @Bean, @Repository（可选） | @Autowired（建议构造函数注入） |

---

## 六、实施计划

### 6.1 阶段一：创建模块结构

| 任务 | 优先级 | 预计时间 |
|------|--------|----------|
| 创建 scene-engine-api 模块 | P0 | 0.5 天 |
| 创建 scene-engine-application 模块 | P0 | 0.5 天 |
| 创建 scene-engine-domain 模块 | P0 | 0.5 天 |
| 创建 scene-engine-infrastructure 模块 | P0 | 0.5 天 |

### 6.2 阶段二：迁移代码

| 任务 | 优先级 | 预计时间 |
|------|--------|----------|
| 迁移 NexusUiController 到 api 模块 | P0 | 0.5 天 |
| 迁移 NexusUiLoader 到 application 模块 | P0 | 0.5 天 |
| 迁移 NexusUiRegistry 到 domain 模块 | P0 | 0.5 天 |
| 迁移 NexusUiRegistryImpl 到 infrastructure 模块 | P0 | 0.5 天 |

### 6.3 阶段三：优化调整

| 任务 | 优先级 | 预计时间 |
|------|--------|----------|
| 添加审计日志 | P0 | 1 天 |
| 添加单元测试 | P1 | 1 天 |
| 更新文档 | P1 | 0.5 天 |

---

## 七、总结

### 7.1 NexusUiController 分层定位

**NexusUiController 属于表现层 (Presentation Layer)**，应该放在 `scene-engine-api` 模块的 `controller` 包中。

### 7.2 当前问题

- `ui/` 包混合了表现层、应用层、领域层、基础设施层的职责
- 违反了分层架构原则
- 难以测试和维护

### 7.3 解决方案

- 创建独立的 Maven 模块
- 按照分层架构原则迁移代码
- 遵循依赖规则和注解使用规范

---

**整理人**: AI Assistant  
**整理时间**: 2026-03-06
