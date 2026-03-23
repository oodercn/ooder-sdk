# 能力与场景模型对比分析

## 一、对比概述

本文档对比 SE SDK 2.3.1 实现与需求/设计文档的差异，识别缺口和改进方向。

**对比范围**:
- 能力模型 (Capability Model)
- 场景模型 (Scene Model)
- 生命周期 (Lifecycle)

---

## 二、能力模型对比

### 2.1 三维分类体系

| 维度 | 需求定义 | 当前实现 | 状态 |
|------|----------|----------|:----:|
| **形态维度** | SkillForm: SCENE / STANDALONE | ✅ `SkillForm` 枚举已实现 | ✅ |
| **分类维度** | 16个能力分类 | ✅ `SkillCategory` 枚举已实现 | ✅ |
| **目的维度** | ServicePurpose 组合 | ✅ `ServicePurpose` 枚举已实现 | ✅ |

### 2.2 能力地址空间

| 需求定义 | 当前实现 | 状态 |
|----------|----------|:----:|
| 固定地址区 0x00-0x7F | ✅ `CapabilityAddress` 枚举 | ✅ |
| 16个分类，每类8个地址 | ✅ 已定义 | ✅ |
| 扩展地址区 0x80-0xFF | ⚠️ 部分实现 | ⚠️ |
| 地址与能力映射 | ✅ `CapabilityRegistry` | ✅ |

### 2.3 能力规范元数据

| 字段 | 需求定义 | 当前实现 | 状态 |
|------|----------|----------|:----:|
| specId | ✅ | ✅ `Capability.capabilityId` | ✅ |
| specName | ✅ | ✅ `Capability.name` | ✅ |
| type | SKILL/SCENE/SCENE_GROUP/CHAIN | ⚠️ 仅 SKILL/SCENE | ⚠️ |
| version | ✅ | ✅ `Capability.version` | ✅ |
| dependencies | ✅ | ✅ `Capability.dependencies` | ✅ |
| parameters | ✅ | ✅ `Capability.inputParameters` | ✅ |
| outputs | ✅ | ✅ `Capability.outputParameters` | ✅ |
| executionConfig | ✅ | ⚠️ 部分实现 | ⚠️ |
| securityConfig | ✅ | ✅ `Capability.securityLevel` | ✅ |

### 2.4 能力类型对比

| 能力类型 | 需求定义 | 当前实现 | 差异 |
|----------|----------|----------|------|
| **技能 (SKILL)** | 单一功能单元 | ✅ `Capability` 类 | 完整实现 |
| **场景 (SCENE)** | 业务上下文环境 | ✅ `SceneTemplate` | 完整实现 |
| **场景组 (SCENE_GROUP)** | 多场景协作组合 | ✅ `SceneGroup` | 完整实现 |
| **能力链 (CAPABILITY_CHAIN)** | 能力执行序列 | ❌ 未实现 | **缺失** |

---

## 三、场景模型对比

### 3.1 技能核心接口

| 方法 | 需求定义 | 当前实现 | 状态 |
|------|----------|----------|:----:|
| `getSkillId()` | ✅ | ✅ `Skill.getSkillId()` | ✅ |
| `getName()` | ✅ | ✅ `Skill.getName()` | ✅ |
| `getVersion()` | ✅ | ✅ `Skill.getVersion()` | ✅ |
| `getForm()` | SCENE/STANDALONE | ✅ `Skill.getForm()` | ✅ |
| `getSceneType()` | AUTO/TRIGGER/HYBRID | ✅ `Skill.getSceneType()` | ✅ |
| `getCategory()` | 16个分类 | ✅ `Skill.getCategory()` | ✅ |
| `getPurposes()` | 服务目的组合 | ✅ `RichSkill.getPurposes()` | ✅ |
| `getCapabilities()` | 能力列表 | ✅ `Skill.getCapabilities()` | ✅ |

### 3.2 SceneTemplate 字段对比

| 字段 | 需求定义 | 当前实现 | 状态 |
|------|----------|----------|:----:|
| templateId | ✅ | ✅ | ✅ |
| templateName | ✅ | ✅ | ✅ |
| description | ✅ | ✅ | ✅ |
| version | ✅ | ✅ | ✅ |
| sceneType | AUTO/TRIGGER/HYBRID | ✅ `SceneType` 枚举 | ✅ |
| visibility | public/internal | ✅ | ✅ |
| category | 能力分类 | ✅ | ✅ |
| capabilityCode | 能力代码 | ✅ | ✅ |
| roles | 角色列表 | ✅ `List<RoleConfig>` | ✅ |
| activationSteps | 激活步骤 | ✅ `Map<String, List<ActivationStepConfig>>` | ✅ |
| menus | 菜单配置 | ✅ `Map<String, List<MenuConfig>>` | ✅ |
| privateCapabilities | 私有能力 | ✅ | ✅ |
| dependencies | 依赖列表 | ✅ | ✅ |
| uiSkills | UI技能 | ⚠️ `UiSkillConfig` | ⚠️ |

### 3.3 角色配置对比

| 字段 | 需求定义 | 当前实现 | 状态 |
|------|----------|----------|:----:|
| roleId | ✅ | ✅ `RoleConfig.roleId` | ✅ |
| roleName | ✅ | ✅ `RoleConfig.roleName` | ✅ |
| name | 兼容字段 | ✅ `RoleConfig.name` | ✅ |
| description | ✅ | ✅ | ✅ |
| priority | ✅ | ✅ | ✅ |
| required | ✅ | ✅ | ✅ |
| minCount | ✅ | ✅ | ✅ |
| maxCount | ✅ | ✅ | ✅ |
| permissions | 权限列表 | ✅ `List<String>` | ✅ |

### 3.4 激活步骤对比

| 字段 | 需求定义 | 当前实现 | 状态 |
|------|----------|----------|:----:|
| stepId | ✅ | ✅ | ✅ |
| stepName | ✅ | ✅ | ✅ |
| name | 兼容字段 | ✅ | ✅ |
| description | ✅ | ✅ | ✅ |
| stepType | ✅ | ✅ | ✅ |
| order | ✅ | ✅ | ✅ |
| skippable | ✅ | ✅ | ✅ |
| required | ✅ | ✅ | ✅ |
| autoExecute | ✅ | ✅ | ✅ |
| executorType | ✅ | ✅ | ✅ |
| privateCapabilities | ✅ | ✅ | ✅ |

---

## 四、生命周期对比

### 4.1 状态定义对比

| 状态 | 需求定义 | 当前实现 | 状态 |
|------|----------|----------|:----:|
| DISCOVERED | 已发现 | ✅ | ✅ |
| PREVIEWING | 预览中 | ❌ | **缺失** |
| CONFIGURING | 配置中 | ❌ | **缺失** |
| INSTALLING | 安装中 | ✅ | ✅ |
| INSTALLED | 已安装 | ✅ | ✅ |
| ACTIVATING | 激活中 | ✅ | ✅ |
| ACTIVATED | 已激活 | ✅ | ✅ |
| DEACTIVATING | 停用中 | ✅ | ✅ |
| DEACTIVATED | 已停用 | ✅ | ✅ |
| UPDATING | 更新中 | ❌ | **缺失** |
| UNINSTALLING | 卸载中 | ✅ | ✅ |
| UNINSTALLED | 已卸载 | ✅ | ✅ |
| ERROR | 错误 | ✅ | ✅ |

### 4.2 状态转换对比

| 转换 | 需求定义 | 当前实现 | 状态 |
|------|----------|----------|:----:|
| DISCOVERED → PREVIEWING | ✅ | ❌ | **缺失** |
| PREVIEWING → CONFIGURING | ✅ | ❌ | **缺失** |
| CONFIGURING → INSTALLING | ✅ | ❌ | **缺失** |
| DISCOVERED → INSTALLING | ✅ | ✅ | ✅ |
| INSTALLING → INSTALLED | ✅ | ✅ | ✅ |
| INSTALLED → ACTIVATING | ✅ | ✅ | ✅ |
| ACTIVATING → ACTIVATED | ✅ | ✅ | ✅ |
| ACTIVATED → DEACTIVATING | ✅ | ✅ | ✅ |
| DEACTIVATING → DEACTIVATED | ✅ | ✅ | ✅ |
| ACTIVATED → UPDATING | ✅ | ❌ | **缺失** |
| UPDATING → ACTIVATED | ✅ | ❌ | **缺失** |
| DEACTIVATED → UNINSTALLING | ✅ | ✅ | ✅ |
| UNINSTALLING → UNINSTALLED | ✅ | ✅ | ✅ |
| 任意 → ERROR | ✅ | ✅ | ✅ |

### 4.3 生命周期覆盖度

| 阶段 | 需求项 | 已实现 | 覆盖度 |
|------|--------|--------|--------|
| 发现阶段 | 4 | 2 | 50% |
| 初始化阶段 | 5 | 4 | 80% |
| 激活阶段 | 5 | 5 | **100%** |
| 使用阶段 | 5 | 4 | 80% |
| 更新阶段 | 4 | 2 | 50% |
| **总计** | **23** | **17** | **74%** |

---

## 五、缺口汇总

### 5.1 高优先级缺口

| 缺口 | 影响 | 建议 |
|------|------|------|
| **能力链 (CAPABILITY_CHAIN)** | 无法定义能力执行序列 | 新增 `CapabilityChain` 类 |
| **PREVIEWING 状态** | 无法预览技能配置 | 新增状态和转换 |
| **CONFIGURING 状态** | 无法配置技能参数 | 新增状态和转换 |
| **UPDATING 状态** | 无法热更新技能 | 新增状态和转换 |

### 5.2 中优先级缺口

| 缺口 | 影响 | 建议 |
|------|------|------|
| 依赖检查机制 | 安装时无法验证依赖 | 增强 `DependencyValidator` |
| 配置驱动能力 | 配置变更无法动态生效 | 增强 `ConfigDrivenService` |
| 扩展地址区 | 扩展能力地址管理 | 完善 `ExtensionAddressRegistry` |

### 5.3 低优先级缺口

| 缺口 | 影响 | 建议 |
|------|------|------|
| UI技能配置 | UI集成不完整 | 完善 `UiSkillConfig` |
| 执行配置 | 执行参数不完整 | 完善 `ExecutionConfig` |

---

## 六、v2.3.1 新增功能

### 6.1 已实现功能

| 功能 | 实现类 | 说明 |
|------|--------|------|
| 场景配置加载 | `SceneConfigLoader` | 从 skill.yaml 加载配置 |
| 场景配置验证 | `SceneConfigLoader.validateSceneConfig()` | 验证配置完整性 |
| 场景激活服务 | `SceneActivationServiceImpl` | 执行激活流程 |
| 审计服务适配 | `AuditServiceAdapter` | 统一审计接口 |
| SceneType 枚举 | `net.ooder.scene.skill.model.SceneType` | AUTO/TRIGGER/HYBRID |

### 6.2 与需求对比

| 需求项 | v2.3.1 实现 | 状态 |
|--------|-------------|:----:|
| 从 skill.yaml 读取场景配置 | ✅ `SceneConfigLoader` | ✅ |
| 验证场景配置完整性 | ✅ `validateSceneConfig()` | ✅ |
| 执行激活步骤 | ✅ `SceneActivationServiceImpl` | ✅ |
| 注册菜单 | ✅ `registerMenus()` | ✅ |
| 应用角色配置 | ✅ `determineUserRole()` | ✅ |

---

## 七、改进建议

### 7.1 短期 (v2.3.2)

1. **新增状态**: PREVIEWING, CONFIGURING, UPDATING
2. **完善状态机**: 实现所有状态转换
3. **依赖检查**: 安装前验证依赖可用性

### 7.2 中期 (v2.4.0)

1. **能力链**: 新增 `CapabilityChain` 类型
2. **配置驱动**: 支持配置变更动态生效
3. **扩展地址**: 完善扩展地址区管理

### 7.3 长期 (v3.0.0)

1. **完整生命周期**: 实现所有生命周期阶段
2. **预览机制**: 支持技能预览和配置
3. **热更新**: 支持技能热更新

---

## 八、参考文档

- [AGENT_SDK_V3_REQUIREMENTS.md](file:///e:/github/ooder-sdk/scene-engine/docs/AGENT_SDK_V3_REQUIREMENTS.md)
- [CAPABILITY_CENTER_SPECIFICATION.md](file:///e:/github/ooder-sdk/agent-sdk/docs/manuals/CAPABILITY_CENTER_SPECIFICATION.md)
- [CAPABILITY_ADDRESS_SPACE_DESIGN.md](file:///e:/github/ooder-sdk/scene-engine/docs/CAPABILITY_ADDRESS_SPACE_DESIGN.md)
- [SCENE_LIFECYCLE_COVERAGE_ANALYSIS_V2.md](file:///e:/github/ooder-sdk/scene-engine/docs/SCENE_LIFECYCLE_COVERAGE_ANALYSIS_V2.md)
- [ooder-skills-technical-design.md](file:///e:/github/ooder-sdk/scene-engine/docs/ooder-skills-technical-design.md)

---

**创建时间**: 2026-03-22  
**版本**: v2.3.1  
**状态**: 已完成
