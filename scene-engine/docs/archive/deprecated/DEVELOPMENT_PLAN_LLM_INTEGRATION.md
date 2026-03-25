# 开发计划：LLM与场景技能集成

**版本**: v2.3.1  
**日期**: 2026-03-07  
**状态**: ✅ 已完成

---

## 一、底层架构分析

### 1.1 现有组件关系

```
┌─────────────────────────────────────────────────────────────┐
│  用户层: User                                               │
│      │                                                      │
│      ▼                                                      │
│  协作层: SceneGroup (用户协作单元)                          │
│      │                                                     │
│      │ 1:N                                                 │
│      ▼                                                      │
│  执行层: SceneAgentBridge (同时实现 SceneAgentCore + SceneAgent)│
│      │                                                      │
│      ├── CapRegistry (能力注册表)                           │
│      ├── CapRouter (能力路由器)                             │
│      └── CapAddressAllocator (地址分配器)                   │
│                                                             │
│  能力层: CapRouter.routeRequest(capId, request)             │
│      ├── SYSTEM (00-3F) - 系统能力                          │
│      ├── COMMON (40-9F) - 通用能力                          │
│      └── EXTENSION (A0-FF) - 扩展能力                       │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 需要扩展的组件

| 组件 | 现状 | 扩展内容 |
|------|------|----------|
| **CapRouter** | 静态路由 | 支持决策引擎介入 |
| **SceneAgentBridge** | 直接调用 | 支持决策模式选择 |
| **ToolRegistry** | 独立管理 | 与 CapRegistry 关联 |
| **SceneGroup** | 协作单元 | 关联 SceneAgent |

### 1.3 新增组件

| 组件 | 包路径 | 职责 |
|------|--------|------|
| DecisionEngine | core.decision | 决策引擎 |
| MvelRuleEngine | skill.rule | 规则执行 |
| KnowledgeCapability | skill.knowledge | 知识库能力 |
| LlmRouter | skill.llm | LLM 路由 |

---

## 二、开发原则

### 1.1 基本原则

| 原则 | 说明 |
|------|------|
| **阅读先行** | 每一步开发前必须阅读需求和设计文档 |
| **分层架构** | 严格遵循分层架构原则，不跨层调用 |
| **包结构规范** | 遵循现有包命名规范，保持一致性 |
| **增量开发** | 每个阶段独立可测试，逐步完善 |

### 1.2 现有包结构规范

```
net.ooder.scene
├── core/                    # 核心层 - 引擎核心功能
│   ├── impl/               # 核心实现
│   ├── provider/           # 提供者
│   ├── security/           # 安全
│   └── skill/              # 核心技能服务
│
├── skill/                   # 技能层 - 业务技能
│   ├── classification/     # 分类 (已有)
│   ├── coordinator/        # 协调器 (已有)
│   ├── knowledge/          # 知识库 (已有)
│   ├── llm/                # LLM (已有)
│   ├── tool/               # 工具 (已有)
│   ├── vector/             # 向量存储 (已有)
│   ├── rule/               # 规则引擎 (新增)
│   └── ...
│
├── discovery/              # 发现层
├── protocol/               # 协议层
├── event/                  # 事件层
└── ...
```

### 1.3 底层扩展规范变更分析

#### 1.3.1 CapRouter 扩展

**现状**：静态路由，直接根据 capId 查找处理器

**扩展需求**：支持决策引擎介入

```java
// 现有实现
public CapResponse routeRequest(String capId, CapRequest request) {
    CapHandler handler = handlers.get(capId);
    return handler != null ? handler.handle(request) : handleDefault(request);
}

// 扩展后
public CapResponse routeRequest(String capId, CapRequest request) {
    // 1. 检查是否需要决策引擎介入
    if (needDecision(capId)) {
        DecisionResult decision = decisionEngine.decide(context);
        capId = decision.getCapId();
        request = decision.getModifiedRequest();
    }
    
    // 2. 执行路由
    CapHandler handler = handlers.get(capId);
    return handler != null ? handler.handle(request) : handleDefault(request);
}
```

**变更点**：
- 新增 `DecisionEngine` 依赖
- 新增 `needDecision(capId)` 判断方法
- 支持动态修改 capId 和 request

#### 1.3.2 SceneAgentBridge 扩展

**现状**：直接调用 invokeCap

**扩展需求**：支持决策模式选择

```java
// 现有实现
@Override
public CapResponse invokeCap(String capId, CapRequest request) {
    return CapResponse.success(request.getRequestId(), capId, "Capability invoked");
}

// 扩展后
@Override
public CapResponse invokeCap(String capId, CapRequest request) {
    // 根据决策模式选择执行路径
    switch (decisionMode) {
        case OFFLINE:
            return ruleEngine.execute(capId, request);
        case DEGRADED:
            return executeWithFallback(capId, request);
        case ONLINE:
        default:
            return executeWithLlm(capId, request);
    }
}
```

**变更点**：
- 新增 `decisionMode` 属性
- 新增 `ruleEngine` 依赖
- 支持三种执行模式

#### 1.3.3 ToolRegistry 与 CapRegistry 关联

**现状**：两个独立的注册表

**扩展需求**：Tool 可注册为 Capability

```java
// 扩展：Tool 注册为 Capability
public void registerToolAsCapability(Tool tool) {
    Capability capability = new ToolCapabilityAdapter(tool);
    capRegistry.register(capability);
    
    // 同时注册到 ToolRegistry
    toolRegistry.register(tool);
}
```

**变更点**：
- 新增 `ToolCapabilityAdapter` 适配器
- Tool 可通过 CapAddress 访问

#### 1.3.4 SceneGroup 与 SceneAgent 关联

**现状**：无明确关联

**扩展需求**：SceneGroup 持有 SceneAgent 引用

```java
// 扩展 SceneGroupInfo
public class SceneGroupInfo {
    // ... 现有字段
    
    private String agentId;           // 关联的 Agent ID
    private SceneAgent agent;         // Agent 引用
}
```

**变更点**：
- SceneGroup 创建时自动创建 SceneAgent
- SceneGroup 销毁时销毁 SceneAgent

---

## 三、开发阶段规划

### Phase 1: 决策引擎基础 (P0)

**目标**: 建立决策引擎核心框架

**阅读文档**:
- [TECH_STORY_LLM_INTEGRATION.md](./TECH_STORY_LLM_INTEGRATION.md) 第四章
- [SECONDARY_DEVELOPMENT_GUIDE.md](./SECONDARY_DEVELOPMENT_GUIDE.md) 19.3节

**包结构**:
```
net.ooder.scene.core.decision/
├── DecisionEngine.java          # 决策引擎接口
├── DecisionContext.java         # 决策上下文
├── DecisionResult.java          # 决策结果
├── DecisionMode.java            # 决策模式枚举
└── impl/
    └── DecisionEngineImpl.java  # 决策引擎实现
```

**任务清单**:
| 任务 | 说明 | 预估 |
|------|------|------|
| 1.1 | 创建 DecisionMode 枚举 | 0.5h |
| 1.2 | 创建 DecisionContext 类 | 1h |
| 1.3 | 创建 DecisionResult 类 | 1h |
| 1.4 | 创建 DecisionEngine 接口 | 1h |
| 1.5 | 实现 DecisionEngineImpl | 3h |
| 1.6 | 编写单元测试 | 2h |

**验收标准**:
- [ ] 决策引擎接口定义完整
- [ ] 支持三种决策模式切换
- [ ] 单元测试覆盖率 > 80%

---

### Phase 2: MVEL 规则引擎 (P0)

**目标**: 实现基于 MVEL 的规则执行引擎

**阅读文档**:
- [TECH_STORY_LLM_INTEGRATION.md](./TECH_STORY_LLM_INTEGRATION.md) 第十一章

**包结构**:
```
net.ooder.scene.skill.rule/
├── MvelRuleEngine.java          # MVEL规则引擎接口
├── RuleScript.java              # 规则脚本模型
├── RuleType.java                # 规则类型枚举
├── RuleRepository.java          # 规则仓库接口
├── ValidationResult.java        # 验证结果
└── impl/
    ├── MvelRuleEngineImpl.java  # MVEL引擎实现
    └── InMemoryRuleRepository.java # 内存规则仓库
```

**任务清单**:
| 任务 | 说明 | 预估 |
|------|------|------|
| 2.1 | 添加 MVEL 依赖 | 0.5h |
| 2.2 | 创建 RuleType 枚举 | 0.5h |
| 2.3 | 创建 RuleScript 模型 | 1h |
| 2.4 | 创建 RuleRepository 接口 | 1h |
| 2.5 | 创建 MvelRuleEngine 接口 | 1h |
| 2.6 | 实现 MvelRuleEngineImpl | 4h |
| 2.7 | 实现 InMemoryRuleRepository | 2h |
| 2.8 | 编写单元测试 | 3h |

**验收标准**:
- [ ] 规则脚本可正确解析执行
- [ ] 支持安全沙箱
- [ ] 单元测试覆盖率 > 80%

---

### Phase 3: LLM 规则生成器 (P1)

**目标**: 实现 LLM 生成规则的能力

**阅读文档**:
- [TECH_STORY_LLM_INTEGRATION.md](./TECH_STORY_LLM_INTEGRATION.md) 11.5节

**包结构**:
```
net.ooder.scene.skill.rule/
├── LlmRuleGenerator.java        # LLM规则生成器接口
└── impl/
    └── LlmRuleGeneratorImpl.java # LLM规则生成器实现
```

**任务清单**:
| 任务 | 说明 | 预估 |
|------|------|------|
| 3.1 | 创建 LlmRuleGenerator 接口 | 1h |
| 3.2 | 实现 LlmRuleGeneratorImpl | 4h |
| 3.3 | 实现规则验证逻辑 | 2h |
| 3.4 | 编写单元测试 | 2h |

**验收标准**:
- [ ] LLM 可生成有效规则
- [ ] 规则验证机制完整
- [ ] 单元测试覆盖率 > 80%

---

### Phase 4: 知识库能力增强 (P1)

**目标**: 完善知识库分层架构

**阅读文档**:
- [TECH_STORY_LLM_INTEGRATION.md](./TECH_STORY_LLM_INTEGRATION.md) 第九章
- [SECONDARY_DEVELOPMENT_GUIDE.md](./SECONDARY_DEVELOPMENT_GUIDE.md) 第二十章

**包结构**:
```
net.ooder.scene.skill.knowledge/
├── KnowledgeLayer.java          # 知识层级枚举 (新增)
├── SearchStrategy.java          # 检索策略枚举 (新增)
├── KnowledgeCapability.java     # 知识库能力接口 (新增)
└── impl/
    └── KnowledgeCapabilityImpl.java # 知识库能力实现 (新增)
```

**任务清单**:
| 任务 | 说明 | 预估 |
|------|------|------|
| 4.1 | 创建 KnowledgeLayer 枚举 | 0.5h |
| 4.2 | 创建 SearchStrategy 枚举 | 0.5h |
| 4.3 | 创建 KnowledgeCapability 接口 | 1h |
| 4.4 | 实现 KnowledgeCapabilityImpl | 4h |
| 4.5 | 实现跨层检索逻辑 | 3h |
| 4.6 | 编写单元测试 | 2h |

**验收标准**:
- [ ] 支持三层知识架构
- [ ] 支持三种检索策略
- [ ] 权限检查完整

---

### Phase 5: LLM Provider 增强 (P1)

**目标**: 完善 LLM 提供者，支持离线模式

**阅读文档**:
- [TECH_STORY_LLM_INTEGRATION.md](./TECH_STORY_LLM_INTEGRATION.md) 第三章

**包结构**:
```
net.ooder.scene.skill.llm/
├── LlmProvider.java             # LLM提供者接口 (已有)
├── LlmRouter.java               # LLM路由器 (新增)
├── LlmRouterConfig.java         # 路由配置 (新增)
├── LlmRouteStrategy.java        # 路由策略 (新增)
└── impl/
    ├── LlmRouterImpl.java       # LLM路由器实现 (新增)
    └── MockLlmProvider.java     # Mock实现 (已有)
```

**任务清单**:
| 任务 | 说明 | 预估 |
|------|------|------|
| 5.1 | 创建 LlmRouteStrategy 枚举 | 0.5h |
| 5.2 | 创建 LlmRouterConfig 类 | 1h |
| 5.3 | 创建 LlmRouter 接口 | 1h |
| 5.4 | 实现 LlmRouterImpl | 3h |
| 5.5 | 编写单元测试 | 2h |

**验收标准**:
- [ ] 支持多种路由策略
- [ ] 支持离线降级
- [ ] 单元测试覆盖率 > 80%

---

### Phase 6: 集成测试 (P2)

**目标**: 完成整体集成测试

**任务清单**:
| 任务 | 说明 | 预估 |
|------|------|------|
| 6.1 | 决策引擎 + 规则引擎集成测试 | 2h |
| 6.2 | LLM + 规则生成集成测试 | 2h |
| 6.3 | 知识库 + LLM 集成测试 | 2h |
| 6.4 | 端到端场景测试 | 4h |

---

## 三、依赖管理

### 3.1 新增依赖

```xml
<!-- MVEL 表达式引擎 -->
<dependency>
    <groupId>org.mvel</groupId>
    <artifactId>mvel2</artifactId>
    <version>2.5.0.Final</version>
</dependency>
```

### 3.2 现有依赖

```xml
<!-- 已有依赖，无需修改 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>${agent-sdk.version}</version>
</dependency>
```

---

## 四、风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| MVEL 安全问题 | 脚本执行风险 | 实现安全沙箱，限制危险操作 |
| LLM 生成规则质量 | 规则不可用 | 多层验证，人工审核机制 |
| 性能问题 | 响应延迟 | 规则缓存，异步执行 |

---

## 五、里程碑

| 里程碑 | 目标日期 | 交付物 |
|--------|----------|--------|
| M1 | Week 1 | Phase 1-2 完成，决策引擎和规则引擎可用 |
| M2 | Week 2 | Phase 3-4 完成，LLM规则生成和知识库增强 |
| M3 | Week 3 | Phase 5-6 完成，整体集成测试通过 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
