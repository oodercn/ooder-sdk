# SE SDK v2.3.1 协作说明文档

**版本**: 2.3.1  
**代号**: Context-Core  
**发布日期**: 2026-03-22  
**文档状态**: 正式发布  
**目标读者**: LLM-SDK团队、AGENT-SDK团队、Skills团队、MVP团队

---

## 一、版本概述

### 1.1 版本亮点

SE SDK v2.3.1 是一个重要的功能增强版本，主要改进包括：

| 特性 | 说明 | 状态 |
|------|------|------|
| 场景配置加载 | 从 skill.yaml 加载场景配置 | ✅ 已实现 |
| 场景配置验证 | 安装时验证配置完整性 | ✅ 已实现 |
| 激活流程引擎 | 完整的场景激活流程 | ✅ 已实现 |
| 审计服务适配 | 统一审计接口 | ✅ 已实现 |
| 状态机扩展 | 新增 DISCOVERED 状态 | ✅ 已实现 |

### 1.2 覆盖度评估

```
综合覆盖度: 81.2%

├── 实体完整性: 72.5%
├── 接口覆盖度: 79.8%
├── 流程完整性: 89.5%
├── 配置支持度: 78.6%
└── 集成就绪度: 85.4%
```

详细报告: [SCENE_LIFECYCLE_COVERAGE_V4.md](./SCENE_LIFECYCLE_COVERAGE_V4.md)

---

## 二、协作边界定义

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              MVP 应用层                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        业务场景实现                                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            SE SDK v2.3.1                                     │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐  ┌─────────────┐ │
│  │ SceneConfig   │  │ Activation    │  │ Menu          │  │ Lifecycle   │ │
│  │ Loader        │  │ Engine        │  │ Engine        │  │ Manager     │ │
│  └───────────────┘  └───────────────┘  └───────────────┘  └─────────────┘ │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐  ┌─────────────┐ │
│  │ SceneTemplate │  │ RoleConfig    │  │ ActivationStep│  │ SkillState  │ │
│  └───────────────┘  └───────────────┘  └───────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
                    │                    │                    │
                    ▼                    ▼                    ▼
        ┌───────────────────┐  ┌───────────────────┐  ┌───────────────────┐
        │     LLM-SDK       │  │    AGENT-SDK      │  │   Skills 团队     │
        │                   │  │                   │  │                   │
        │ • LlmService      │  │ • A2AService      │  │ • skill.yaml      │
        │ • Embedding       │  │ • CommandRouter   │  │ • 场景配置        │
        │ • PromptManager   │  │ • AgentRegistry   │  │ • 激活步骤        │
        └───────────────────┘  └───────────────────┘  └───────────────────┘
```

### 2.2 职责划分

| 组件 | 职责 | 边界 |
|------|------|------|
| **SE SDK** | 场景生命周期管理、配置加载验证、激活流程控制 | 不涉及具体 LLM 调用、Agent 通信 |
| **LLM-SDK** | LLM API 封装、模型路由、Token 管理 | 不涉及场景上下文组装 |
| **AGENT-SDK** | A2A 协议、Command 路由、Agent 生命周期 | 不涉及场景业务逻辑 |
| **Skills 团队** | skill.yaml 配置、场景定义、激活步骤设计 | 不涉及 SDK 内部实现 |
| **MVP 团队** | 业务场景实现、UI 交互、数据持久化 | 依赖 SE SDK SPI |

---

## 三、对 LLM-SDK 的协作要求

### 3.1 依赖接口

SE SDK 需要 LLM-SDK 提供以下接口：

```java
/**
 * LLM 服务接口
 * 由 LLM-SDK 实现，SE SDK 调用
 */
public interface LlmService {
    
    /**
     * 普通对话
     */
    LlmResponse chat(ChatRequest request);
    
    /**
     * 流式对话
     */
    void chatStream(ChatRequest request, StreamResponseHandler handler);
    
    /**
     * 带工具调用的对话
     */
    LlmResponse chatWithTools(ChatRequest request);
    
    /**
     * 结构化输出
     */
    <T> T structuredOutput(ChatRequest request, Class<T> responseType);
}

/**
 * 向量服务接口
 */
public interface EmbeddingService {
    
    /**
     * 文本向量化
     */
    float[] embed(String text);
    
    /**
     * 批量向量化
     */
    List<float[]> embedBatch(List<String> texts);
}
```

### 3.2 协作任务清单

| 任务ID | 任务描述 | 优先级 | 状态 | SE SDK 依赖 |
|--------|---------|--------|------|-------------|
| LLM-SDK-001 | LLM 调用抽象接口 | P0 | 待确认 | LlmService |
| LLM-SDK-002 | 结构化输出支持 | P0 | 待确认 | structuredOutput |
| LLM-SDK-003 | 工具调用 (Function Calling) | P1 | 待确认 | chatWithTools |
| LLM-SDK-004 | 多模型路由策略 | P1 | 待确认 | 模型选择 |
| LLM-SDK-005 | Token 计算和管理 | P2 | 待确认 | 用量统计 |

### 3.3 激活引导场景

SE SDK 在场景激活时需要 LLM-SDK 提供智能引导能力：

```java
/**
 * 激活引导请求
 */
public class ActivationGuidanceRequest {
    private String sceneId;              // 场景ID
    private String userId;               // 用户ID
    private String roleId;               // 角色ID
    private String currentStepId;        // 当前步骤ID
    private Map<String, Object> context; // 上下文数据
    private List<String> availableOptions; // 可选项
}

/**
 * 激活引导响应
 */
public class ActivationGuidanceResponse {
    private String guidance;             // 引导文本
    private List<Recommendation> recommendations; // 推荐选项
    private Map<String, Object> suggestions;      // 填充建议
}
```

---

## 四、对 AGENT-SDK 的协作要求

### 4.1 依赖接口

SE SDK 需要 AGENT-SDK 提供以下接口：

```java
/**
 * A2A 服务接口
 * 由 AGENT-SDK 实现，SE SDK 调用
 */
public interface A2AService {
    
    /**
     * 发送 Command
     */
    CommandResponse sendCommand(Command command);
    
    /**
     * 异步发送 Command
     */
    void sendCommandAsync(Command command, CommandCallback callback);
    
    /**
     * 传递上下文
     */
    TransferResult transferContext(ContextTransfer transfer);
    
    /**
     * 注册 Agent
     */
    RegistrationResult registerAgent(AgentInfo agentInfo);
}

/**
 * Agent 注册信息
 */
public class AgentInfo {
    private String agentId;           // Agent ID
    private String agentType;         // Agent 类型
    private String sceneId;           // 所属场景
    private String endpoint;          // 端点地址
    private Map<String, Object> capabilities; // 能力列表
}
```

### 4.2 协作任务清单

| 任务ID | 任务描述 | 优先级 | 状态 | SE SDK 依赖 |
|--------|---------|--------|------|-------------|
| AGENT-SDK-001 | A2A 上下文传递协议 | P0 | 待确认 | transferContext |
| AGENT-SDK-002 | Command 路由增强 | P0 | 待确认 | sendCommand |
| AGENT-SDK-003 | Agent 注册与发现 | P1 | 待确认 | registerAgent |
| AGENT-SDK-004 | 消息队列支持 | P1 | 待确认 | 异步传递 |
| AGENT-SDK-005 | 负载均衡和故障转移 | P2 | 待确认 | 高可用 |

### 4.3 场景间协作场景

```java
/**
 * 场景间协作请求
 */
public class SceneCollaborationRequest {
    private String sourceSceneId;     // 源场景ID
    private String targetSceneId;     // 目标场景ID
    private String collaborationType; // 协作类型
    private Map<String, Object> payload; // 协作数据
}

/**
 * 协作类型
 */
public enum CollaborationType {
    CONTEXT_TRANSFER,    // 上下文传递
    DATA_SHARE,          // 数据共享
    TASK_DELEGATE,       // 任务委托
    NOTIFICATION         // 通知
}
```

---

## 五、对 Skills 团队的协作要求

### 5.1 skill.yaml 配置规范

场景类型技能必须在 `skill.yaml` 中定义以下配置：

```yaml
# skill.yaml 示例
name: daily-report
version: 1.0.0
type: SCENE  # 必须声明为 SCENE 类型

spec:
  # 能力配置
  capability:
    category: biz
    code: daily-report
  
  # 场景配置
  scene:
    type: TRIGGER          # AUTO/TRIGGER/HYBRID
    visibility: internal   # public/internal
  
  # 角色配置 (必需)
  roles:
    - name: MANAGER
      description: 场景管理员
      required: true
      minCount: 1
      maxCount: 1
      permissions:
        - scene:manage
        - report:view
    - name: EMPLOYEE
      description: 普通员工
      required: true
      minCount: 1
      maxCount: 100
      permissions:
        - report:submit
  
  # 激活步骤 (必需)
  activationSteps:
    MANAGER:
      - stepId: confirm-participants
        name: 确认参与者
        type: CONFIRM_PARTICIPANTS
        required: true
        autoExecute: false
      - stepId: select-push-targets
        name: 选择推送目标
        type: SELECT_PUSH_TARGETS
        required: true
      - stepId: config-conditions
        name: 配置提醒条件
        type: CONFIG_CONDITIONS
        required: false
        skippable: true
      - stepId: confirm-activation
        name: 确认激活
        type: CONFIRM_ACTIVATION
        required: true
    EMPLOYEE:
      - stepId: confirm-join
        name: 确认加入
        type: CONFIRM_JOIN
        required: true
      - stepId: config-private-capabilities
        name: 配置私有能力
        type: CONFIG_PRIVATE_CAPABILITIES
        required: false
        privateCapabilities:
          - personal-reminder
      - stepId: confirm-activation
        name: 确认激活
        type: CONFIRM_ACTIVATION
        required: true
  
  # 菜单配置 (必需)
  menus:
    MANAGER:
      - id: daily-report-dashboard
        name: 日报管理
        icon: report
        url: /daily-report/manager
        order: 1
      - id: daily-report-summary
        name: 日报汇总
        icon: summary
        url: /daily-report/summary
        order: 2
    EMPLOYEE:
      - id: daily-report-submit
        name: 提交日报
        icon: edit
        url: /daily-report/submit
        order: 1
      - id: daily-report-history
        name: 我的日报
        icon: history
        url: /daily-report/history
        order: 2
  
  # 私有能力配置 (可选)
  privateCapabilities:
    - capId: personal-reminder
      name: 个人提醒
      description: 自定义提醒时间和方式
```

### 5.2 配置验证规则

安装时 SE SDK 会进行以下验证：

| 验证项 | 验证类型 | 触发条件 | 错误级别 |
|--------|----------|----------|----------|
| 场景配置存在 | SCENE_CONFIG_MISSING | type=SCENE 但无 spec.roles | 阻断 |
| 角色定义存在 | ROLES_MISSING | spec.roles 为空 | 阻断 |
| 必需角色存在 | REQUIRED_ROLE_MISSING | 无 required=true 角色 | 阻断 |
| 激活步骤存在 | ACTIVATION_STEPS_MISSING | spec.activationSteps 为空 | 阻断 |
| 角色激活步骤 | ROLE_ACTIVATION_STEPS_MISSING | 必需角色无激活步骤 | 阻断 |
| 菜单配置存在 | MENUS_MISSING | spec.menus 为空 | 阻断 |
| 角色菜单配置 | ROLE_MENUS_MISSING | 必需角色无菜单 | 阻断 |

### 5.3 需要增强的技能列表

| 技能ID | 分类 | 安装类型 | 优先级 | 当前状态 |
|--------|------|----------|--------|----------|
| skill-recruitment-management | biz | SCENE | 高 | 待增强 |
| skill-approval-form | biz | SCENE | 高 | 待增强 |
| skill-collaboration | biz | SCENE | 高 | 待增强 |
| skill-business | biz | SCENE | 中 | 待增强 |
| skill-knowledge-management | knowledge | SCENE | 中 | 待增强 |

---

## 六、对 MVP 团队的适配说明

### 6.1 SPI 扩展点

MVP 团队需要实现以下 SPI 接口：

```java
/**
 * 场景服务 SPI
 * MVP 团队实现
 */
public interface SceneServices {
    
    /**
     * 获取用户服务
     */
    UserService getUserService();
    
    /**
     * 获取组织服务
     */
    OrganizationService getOrganizationService();
    
    /**
     * 获取权限服务
     */
    PermissionService getPermissionService();
    
    /**
     * 获取存储服务
     */
    StorageService getStorageService();
    
    /**
     * 获取消息服务
     */
    MessageService getMessageService();
}

/**
 * 激活步骤执行器 SPI
 * MVP 团队可扩展实现
 */
public interface ActivationStepExecutor {
    
    /**
     * 是否可以执行
     */
    boolean canExecute(ActivationStepConfig stepConfig);
    
    /**
     * 执行步骤
     */
    StepResult execute(ActivationStepConfig stepConfig, 
                       ActivationProcess process, 
                       Map<String, Object> context);
}
```

### 6.2 必须实现的服务

| 服务 | 接口 | 说明 | 优先级 |
|------|------|------|--------|
| 用户服务 | UserService | 用户信息查询 | P0 |
| 组织服务 | OrganizationService | 组织架构查询 | P0 |
| 权限服务 | PermissionService | 权限验证 | P0 |
| 存储服务 | StorageService | 数据持久化 | P0 |
| 消息服务 | MessageService | 消息推送 | P1 |

### 6.3 Spring Boot 自动配置

```java
/**
 * MVP 服务提供者配置
 */
@Configuration
@ConditionalOnClass(SceneEngine.class)
public class MvpSceneEngineConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SceneServices sceneServices() {
        return new MvpSceneServicesImpl();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SceneEngineServiceProvider serviceProvider() {
        return new MvpServiceProviderImpl();
    }
}
```

### 6.4 激活步骤执行器注册

```java
/**
 * 自定义激活步骤执行器示例
 */
@Component
public class ConfirmParticipantsExecutor implements ActivationStepExecutor {
    
    @Override
    public boolean canExecute(ActivationStepConfig stepConfig) {
        return "CONFIRM_PARTICIPANTS".equals(stepConfig.getStepType());
    }
    
    @Override
    public StepResult execute(ActivationStepConfig stepConfig, 
                               ActivationProcess process, 
                               Map<String, Object> context) {
        StepResult result = new StepResult();
        result.setStepId(stepConfig.getStepId());
        
        try {
            List<String> participants = (List<String>) context.get("participants");
            if (participants == null || participants.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage("请选择参与者");
                return result;
            }
            
            result.setSuccess(true);
            result.setOutput(Map.of("participantCount", participants.size()));
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }
}
```

---

## 七、核心类参考

### 7.1 SceneConfigLoader

**位置**: `net.ooder.scene.skill.install.SceneConfigLoader`

**功能**: 从 skill.yaml 加载场景配置

**主要方法**:

| 方法 | 说明 |
|------|------|
| `loadSceneConfig(skillId, skillPackage)` | 加载场景配置 |
| `validateSceneConfig(skillId, template)` | 验证配置完整性 |

**使用示例**:

```java
SceneConfigLoader loader = new SceneConfigLoader();
SceneTemplate template = loader.loadSceneConfig(skillId, skillPackage);
loader.validateSceneConfig(skillId, template);
```

### 7.2 SceneActivationServiceImpl

**位置**: `net.ooder.scene.core.activation.SceneActivationServiceImpl`

**功能**: 场景激活流程引擎实现

**主要方法**:

| 方法 | 说明 |
|------|------|
| `startActivation(request)` | 启动激活流程 |
| `getActivationStatus(activationId)` | 获取激活状态 |
| `executeStep(activationId, stepId, input)` | 执行步骤 |
| `skipStep(activationId, stepId, reason)` | 跳过步骤 |
| `getProgress(activationId)` | 获取进度 |

### 7.3 SceneTemplate

**位置**: `net.ooder.scene.core.template.SceneTemplate`

**功能**: 场景模板配置

**主要字段**:

| 字段 | 类型 | 说明 |
|------|------|------|
| templateId | String | 模板ID |
| sceneType | SceneType | 场景类型 |
| roles | List<RoleConfig> | 角色配置 |
| activationSteps | Map<String, List> | 激活步骤 |
| menus | Map<String, List> | 菜单配置 |

### 7.4 枚举类型

**SceneType** (`net.ooder.scene.skill.model.SceneType`):

| 值 | 说明 | 自驱动 | 可触发 |
|----|------|--------|--------|
| AUTO | 自主场景 | ✅ | ❌ |
| TRIGGER | 触发场景 | ❌ | ✅ |
| HYBRID | 混驱动 | 可触发 |
|----|------|--------|--------|
| AUTO | 自主场景 | ✅ | ❌ |
| TRIGGER | 触发场景 | ❌ | ✅ |
| HYBRID | 混合场景 | ✅ | ✅ |

**SkillLifecycleState** (`net.ooder.scene.core.lifecycle.SceneSkillLifecycle`):

| 状态 | 说明 |
|------|------|
| DISCOVERED | 已发现 |
| INSTALLING | 安装中 |
| INSTALLED | 已安装 |
| ACTIVATING | 激活中 |
| ACTIVATED | 已激活 |
| DEACTIVATING | 停用中 |
| DEACTIVATED | 已停用 |
| UNINSTALLING | 卸载中 |
| UNINSTALLED | 已卸载 |
| ERROR | 错误 |

---

## 八、集成检查清单

### 8.1 LLM-SDK 集成检查

- [ ] LlmService 接口实现
- [ ] ChatRequest/ChatResponse 数据结构
- [ ] 流式响应处理器
- [ ] 结构化输出支持
- [ ] 工具调用支持

### 8.2 AGENT-SDK 集成检查

- [ ] A2AService 接口实现
- [ ] Command 数据结构
- [ ] 上下文传递协议
- [ ] Agent 注册机制
- [ ] 异步消息支持

### 8.3 Skills 团队配置检查

- [ ] skill.yaml 包含 spec.roles
- [ ] skill.yaml 包含 spec.activationSteps
- [ ] skill.yaml 包含 spec.menus
- [ ] 必需角色有激活步骤
- [ ] 必需角色有菜单配置

### 8.4 MVP 团队适配检查

- [ ] SceneServices SPI 实现
- [ ] UserService 实现
- [ ] OrganizationService 实现
- [ ] PermissionService 实现
- [ ] StorageService 实现
- [ ] 自定义 ActivationStepExecutor (可选)

---

## 九、联系方式

| 团队 | 联系人 | 协作文档 |
|------|--------|---------|
| SE SDK | - | 本文档 |
| LLM-SDK | - | [COLLABORATION_LLM_SDK_V2_3_1.md](../COLLABORATION_LLM_SDK_V2_3_1.md) |
| AGENT-SDK | - | [COLLABORATION_AGENT_SDK_V2_3_1.md](../COLLABORATION_AGENT_SDK_V2_3_1.md) |
| Skills 团队 | - | [SKILL_YAML_ENHANCEMENT_COLLABORATION.md](./SKILL_YAML_ENHANCEMENT_COLLABORATION.md) |

---

*文档版本: 1.0*  
*发布日期: 2026-03-22*  
*SE SDK 团队*
