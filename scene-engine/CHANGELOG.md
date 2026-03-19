# Changelog

## [2.3.1] - 2026-03-19

### 持久化重大更新

#### 知识库持久化
- **KnowledgeRepository** - 知识库持久化接口
- **JsonKnowledgeRepository** - JSON文件存储实现（默认方案）
- **InMemoryKnowledgeRepository** - 内存存储实现（开发测试）
- **RepositoryConfig** - 存储配置类
- **KnowledgeRepositoryFactory** - 仓库工厂，支持动态切换存储类型

#### 向量存储持久化
- **JsonVectorStore** - JSON文件向量存储实现
- 支持向量数据持久化和重启恢复

#### 自动配置
- **KnowledgePersistenceAutoConfiguration** - 知识库持久化自动配置
- **SdkSceneGroupAutoConfiguration** - SDK SceneGroupManager 自动配置

#### SDK 适配器
- **SdkSceneGroupManagerAdapter** - SDK SceneGroupManager 接口适配器
- 将 SE 原生 SceneGroupManager 适配为 SDK 接口

#### 配置支持
```yaml
se:
  knowledge:
    persistence:
      type: json  # json | memory | sql
      base-path: ~/.ooder/data/knowledge
      auto-save: true
      save-interval-ms: 5000
    vector-store:
      type: json  # json | memory
      dimension: 1536
```

#### Bug 修复
- 修复 Java 8 兼容性问题（Map.of、toList() 等）
- 修复 SceneGroupInitializer 语法错误

#### 协作文档
- `SE-SDK-COLLABORATION-RESPONSE.md` - MVP 协作响应文档

---

## [2.3.1] - 2026-03-10

### 新增功能 (v3.0 模型重构)

#### 核心模型
- **SkillForm** - 技能形态枚举（SCENE/STANDALONE）
- **SceneType** - 场景类型枚举（AUTO/TRIGGER/HYBRID）
- **SkillCategory** - 技能分类枚举（knowledge/llm/tool/workflow/data/service/ui/other）
- **ServicePurpose** - 服务目的枚举（多维度组合：范围/时效/主动性）
- **Skill** - 技能核心接口
- **SceneStructure** - 场景结构接口
- **SkillPath** - 技能路径类
- **Capability** - 能力单元接口

#### Engine Core 层
- **SceneTemplate** - 场景模板配置模型
- **RoleConfig** - 角色配置
- **ActivationStepConfig** - 激活步骤配置
- **DependenciesConfig** - 依赖配置
- **ActivationProcess** - 激活流程状态机
- **ActivationState** - 激活状态持久化模型

#### SPI 扩展机制
- **ActivationStepExecutor** - 激活步骤执行器接口
- **MenuGenerator** - 菜单生成器接口
- **DependencyChecker** - 依赖检查器接口
- **ExtensionPointRegistry** - 扩展点注册中心

### 功能完善

- **SceneClientImpl** - 完善所有 TODO 方法
  - `invokeCapability()` - 能力调用
  - `listCapabilities()` - 能力列表
  - `listMySceneGroups()` - 场景组列表
  - `getSceneGroup()` - 获取场景组
  - `updateSettings()` - 设置更新
  - `getIdentity()` - 获取身份信息
  - `stopHeartbeat()` - 停止心跳

- **RichSkill** - 适配 v3.0 模型，实现 Skill 接口
- **UserContributionServiceImpl** - 用户知识贡献服务实现
- **IdentityInfo** - 身份信息类

### 删除的代码（不兼容变更）

- `SceneSkillCategory` - 旧分类枚举（ABS/ASS/TBS）
- `SceneSkillClassifier` - 旧分类器接口
- `SceneSkillClassifierImpl` - 旧分类器实现
- `SceneSkillClassificationResult` - 旧分类结果
- `SceneSkillClassificationException` - 旧分类异常
- `MetadataCompat` - 旧兼容层
- `WaitingSubState` - 旧子状态
- `CapabilitySubType` - 旧子类型

### 协作文档

- `AGENT_SDK_V3_COLLABORATION_TASKS.md` - agent-sdk 协同任务清单
- `AGENT_SDK_V3_REQUIREMENTS.md` - 详细需求说明
- `V3_INTEGRATION_TEST_REPORT.md` - 集成测试报告

### 破坏性变更

- 分类体系从"运行时计算"改为"开发时声明"
- 场景不再是独立实体，而是技能的形态属性
- 消除 PENDING/INVALID 模糊状态

### 迁移指南

旧字段映射：
| 旧字段 | 新字段 |
|--------|--------|
| `sceneSkill: true` | `form: SCENE` |
| `mainFirst: true` | `sceneType: AUTO` |
| `category: ABS/ASS` | `sceneType: AUTO` |
| `category: TBS` | `sceneType: TRIGGER` |
| `category: NOT_SCENE_SKILL` | `form: STANDALONE` |

---

## [2.3.0] - 2026-03-01

### 新增
- 初始版本发布
- 基础场景引擎功能
- 技能发现和安装
- 知识库管理
- RAG 支持
