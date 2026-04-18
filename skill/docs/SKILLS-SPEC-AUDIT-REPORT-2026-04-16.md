# Ooder Skills 框架规范检测报告

**报告编号**: SKILLS-SPEC-AUDIT-2026-04-16  
**检测范围**: ooder-sdk 全仓库（agent-sdk、scene-engine、skill 三大子仓库）  
**检测日期**: 2026-04-16  
**检测基线版本**: agent-sdk v3.0.3 / scene-engine v2.3.1 / skill-common v2.3.1  

---

## 一、总体评估

| 维度 | 评分 | 说明 |
|------|------|------|
| **接口一致性** | ★★☆☆☆ (2/5) | 多处接口定义不一致，跨框架集成存在严重障碍 |
| **命名规范** | ★★★☆☆ (3/5) | 术语迁移进行中，大量 @Deprecated 字段待清理 |
| **配置规范** | ★★★☆☆ (3/5) | skill.yaml 规范已建立，但部分字段位置不统一 |
| **代码质量** | ★★★☆☆ (3/5) | 14处伪实现/占位代码，27处FIXME/TODO |
| **SPI 规范** | ★★☆☆☆ (2/5) | 9处接口重复定义，命名不一致 |
| **依赖管理** | ★★★☆☆ (3/5) | Bean 冲突、循环依赖、init()重复调用 |
| **文档完整性** | ★★★★☆ (4/5) | 架构文档完善，协作声明齐全 |

---

## 二、P0 级规范问题（阻塞级，必须修复）

### 2.1 CLI 扩展接口不一致

**问题**: Skills 框架与 Agent SDK CLI 使用完全不同的扩展接口

| 维度 | Skills 框架 | Agent SDK CLI（旧版） | 统一方向 |
|------|-------------|----------------------|----------|
| 扩展接口 | `SkillCliExtension` | `CliExtension` | → `SkillCliExtension` |
| 获取ID | `getSkillId()` | `getId()` | → `getSkillId()` |
| 获取命令 | `getCommand()`（单数） | `getCommands()`（复数） | → `getCommand()` |
| 执行方法 | `execute(args, context)` | 通过 `CliCommand` 间接执行 | → `execute(args, context)` |
| 配置方式 | `skill.yaml` | `extension.properties` | → `skill.yaml` |
| 命令格式 | `skill exec <id> <cmd>` | `ooder skill:exec --skill-id <id>` | → `skill exec <id> <cmd>` |

**影响**: 跨框架集成失败，Skill 开发者无法编写兼容两套框架的 CLI 扩展  
**当前状态**: 已通过适配器层临时解决，本质问题待废弃旧接口  
**检测文件**:
- `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\src\main\java\net\ooder\sdk\cli\api\SkillCliExtension.java`
- `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\src\main\java\net\ooder\sdk\cli\adapter\SkillCliExtensionAdapter.java`
- `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\src\main\java\net\ooder\sdk\cli\adapter\LegacyCliExtensionAdapter.java`

### 2.2 SPI 接口 9 处重复定义

**问题**: 同一接口在多个模块中重复定义，实现不一致

| 接口名 | 重复位置 | 差异说明 |
|--------|---------|---------|
| `PageResult` | ooder-spi-core, scene-engine | 字段名不同，scene-engine 版本 4 个字段 @Deprecated |
| `ImService` | ooder-spi-core, skill-common | 方法签名不同 |
| `UserService` | skill-common, scene-engine | 需要合并 |
| `OrganizationService` | skill-common, scene-engine | 需要合并 |
| `VectorStoreProvider` | skill-common, skill-menu | 包路径不同 |
| `EmbeddingProvider` | skill-common, skill-menu | 包路径不同 |
| `SceneServices` | skill-common, skill-menu | 统一使用 skill-common 版本 |
| `LlmService` | skill-llm-base, skill-spi | 同构接口，包路径不同 |
| `OrgService` | ooder-spi-core, skill-spi, skill-org-web | 三处定义，方法签名不同 |

**影响**: 编译冲突、运行时 Bean 冲突、维护成本倍增  
**检测文件**: `e:\github\ooder-sdk\docs\spi-merge-analysis.md`

### 2.3 skill.yaml 配置字段位置不统一

**问题**: `skillForm` 和 `sceneType` 字段在不同 skill.yaml 中位置不一致

| 位置 | 示例模块 | 问题 |
|------|---------|------|
| 根级别 | 部分旧模块 | `skillForm: SCENE` 放在根级别，解析器无法识别 |
| `spec` 节点内 | 新模块（推荐） | `spec.skillForm: SCENE`，符合规范 |
| `metadata` 节点内 | 部分模块 | `metadata.skillForm`，不符合规范 |

**影响**: `UnifiedDiscoveryServiceImpl.createSkillPackage()` 未解析 `spec.skillForm`，导致 `SkillInstallProcessorImpl.determineSkillForm()` 永远返回默认值 "ATOMIC"  
**检测文件**: `e:\github\ooder-sdk\scene-engine\docs\SKILLS_FRAMEWORK_SPEC_SUGGESTION.md`

---

## 三、P1 级规范问题（严重级，应尽快修复）

### 3.1 伪实现/占位代码（14处）

**问题**: 关键功能使用伪实现，生产环境不可用

| 模块 | 文件 | 行号 | 描述 |
|------|------|------|------|
| llm-sdk | ContextTransferHandlerImpl.java | 55,90,95,185,201,267 | 6处 FIXME：差异计算、上下文获取、差异应用、深度合并 |
| llm-sdk | StructuredOutputApiImpl.java | 100 | FIXME: 伪实现 - 需要集成真实LLM驱动 |
| llm-sdk | ToolCallingApiImpl.java | 229,255 | 2处 FIXME: 伪实现 - 需要集成真实LLM驱动 |
| llm-sdk | RagServiceImpl.java | 21,95 | 2处 FIXME: 伪实现 - 需要集成向量存储/LLM服务 |
| llm-sdk | LlmServiceImpl.java | 32,66,105 | 3处 FIXME: 伪实现 - 需要集成真实LLM Provider |
| llm-sdk | EmbeddingServiceImpl.java | 28,62 | 2处 FIXME: 伪实现 - 需要集成真实Embedding Provider |
| llm-sdk | TokenManagerImpl.java | 52 | FIXME: 伪实现 - 需要使用真实的 Tokenizer |
| llm-sdk | LlmRouterImpl.java | 118,149 | FIXME: 实现加权/成本选择 |
| llm-sdk | LlmConnectionPoolImpl.java | 15 | FIXME: 当前为简化实现 |
| scene-engine | SkillSDKAdapter.java | 192 | `return null; // 占位，需要根据实际情况实现` |

**影响**: LLM SDK 核心功能不可用，Skill SDK 适配器功能缺失  
**检测方法**: `grep -rn "FIXME\|伪实现\|占位" --include="*.java" e:\github\ooder-sdk\`

### 3.2 Bean 冲突与注入问题

**问题1**: `JsonStorageService` Bean 冲突
- `skill-common` 和 `scene-engine` 中都有 `JsonStorageService` 类带 `@Service` 注解
- 导致 Spring 启动时 Bean 冲突

**问题2**: `init()` 方法重复调用
- `OrgService.init()` 被调用两次（手动调用 + @PostConstruct）
- `JsonStorageService.init()` 被调用两次

**问题3**: 注入方式不一致
- `DiscoveryOrchestrator` 构造器参数传入但未使用，内部使用 `@Autowired` 字段注入
- `AuthApi` 使用字段注入而非构造器注入

**检测文件**:
- `e:\github\ooder-sdk\skill\skill-common\NOTICE-TEAM-COORDINATION.md`
- `e:\github\ooder-sdk\skill\skill-common\CIRCULAR_DEPENDENCY_REPORT.md`

### 3.3 缓存键缺少分支信息

**问题**: Discovery 模块缓存键使用 `repositoryUrl` 作为键，未包含分支信息
- master 和 main 分支使用相同缓存键
- 不同分支的 skill.yaml 数据混淆

**检测文件**: `e:\github\ooder-sdk\scene-engine\docs\SE_SDK_DISCOVERY_REFACTORING_ANALYSIS.md`

### 3.4 发现服务违反单一职责原则

**问题**: `UnifiedDiscoveryServiceImpl` 超过 1100 行，违反 SRP/OCP/DIP 原则
- 4 种配置方式共存，优先级不明确
- `Map<String, Object>` 缺乏类型安全
- 双层缓存增加复杂度

**检测文件**: `e:\github\ooder-sdk\scene-engine\docs\DISCOVERY_MODULE_DEEP_ANALYSIS.md`

---

## 四、P2 级规范问题（改进级，计划修复）

### 4.1 @Deprecated 字段待清理（55+处）

**问题**: 术语迁移后大量旧字段标记 @Deprecated 但未清理

| 模块 | 文件 | @Deprecated 数量 | 关键变更 |
|------|------|-----------------|---------|
| skills-framework | SkillManifest.java | 10 | `collaborativeScenes` → `collaborativeCapabilities`，`primaryScene` → `mainFirstScene` |
| skills-framework | SkillDefinition.java | 4 | `skillId`/`skillType`/`sceneId`/`groupId` 旧字段 |
| skills-framework | SkillInstaller.java | 4 | 4 个旧安装模式 |
| skills-framework | SceneTemplate.java | 3 | 旧字段 |
| scene-engine | LogStatsCriteria/LogQueryCriteria/LogCleanCriteria | 13 | 日志查询旧字段 |
| scene-engine | KnowledgeBinding.java | 6 | 知识绑定旧字段 |
| scene-engine | PageResult.java | 4 | 分页旧字段 |
| scene-engine | InMemorySceneInstanceRepository.java | 1 | 整个类 @Deprecated |
| scene-engine | DiscoveryCoordinator.java | 1 | 旧方法 |
| skill | CapabilityDTO.java | 4 | `getType()` → `getCapabilityType()`，`getCapId()` → `getCapabilityId()` |

**影响**: 代码臃肿，新开发者困惑，编译警告  
**建议**: 设定 @Deprecated 清理版本线（如 v3.2.0），到期后删除

### 4.2 术语变更未完全对齐

| 旧术语 | 新术语 | 完成状态 |
|--------|--------|---------|
| `SceneDefinition` | `SceneCapability` | skills-framework ✅ / scene-engine 部分 |
| `primaryScene` | `mainFirst` | skills-framework ✅ / scene-engine 部分 |
| `collaborativeScenes` | `collaborativeCapabilities` | skills-framework ✅ / scene-engine 部分 |
| `WorkflowDefinition` | `capabilityChains` | skills-framework ✅ / scene-engine 部分 |
| `category: ABS/ASS` | `sceneType: AUTO` | scene-engine ✅ |
| `sceneSkill: true` | `form: SCENE` | scene-engine ✅ |
| `SkillCardV3` | `SkillCard` | agent-sdk ✅ (v3.0.2) |

### 4.3 包名变更未完成

| 旧包名 | 新包名 | 状态 |
|--------|--------|------|
| `net.ooder.llm.api.*` | `net.ooder.sdk.llm.*` | 部分完成 |
| `net.ooder.sdk.api.*` (agent-sdk-api) | `net.ooder.sdk.api.*` (agent-sdk-core) | 已完成（v2.3.0 删除 agent-sdk-api） |
| `net.ooder.skill.common.spi.*` | `net.ooder.spi.*` | 进行中（约 45 个 SPI 待迁移） |

### 4.4 错误码规范不统一

**问题**: 不同模块使用不同的错误处理方式
- Agent SDK CLI: 已建立 `CliErrorCode` 枚举体系（CLI-000 ~ CLI-999）
- Scene-Engine: 使用字符串拼接错误消息
- Skills-Framework: 无统一错误码
- Skill Common: 无统一错误码

**建议**: 全仓库统一采用 `模块-编号` 格式错误码

---

## 五、规范符合性检测清单

### 5.1 skill.yaml 规范检测

| 检测项 | 规范要求 | 当前状态 | 不符合模块 |
|--------|---------|---------|-----------|
| apiVersion 字段 | 必须为 `skill.ooder.net/v1` | ✅ 已统一 | - |
| kind 字段 | 必须为 `Skill` | ✅ 已统一 | - |
| metadata.id 格式 | 必须匹配 `^[a-z0-9-]+$` | ✅ 已统一 | - |
| metadata.version 格式 | 必须为 SemVer | ✅ 已统一 | - |
| spec.skillForm 位置 | 必须在 `spec` 节点内 | ⚠️ 部分不符合 | 旧模块放在根级别 |
| spec.sceneType 位置 | 必须在 `spec` 节点内 | ⚠️ 部分不符合 | 旧模块放在根级别 |
| spec.capability.address | 必须使用标准地址枚举 | ✅ 已统一 | - |
| services 声明 | Controller 依赖注入必须声明 | ⚠️ 部分缺失 | 部分 Skill 未声明 |
| endpoints 配置 | 动态路由必须声明 | ✅ 已统一 | - |

### 5.2 接口规范检测

| 检测项 | 规范要求 | 当前状态 | 问题 |
|--------|---------|---------|------|
| CLI 扩展接口 | 统一使用 `SkillCliExtension` | ⚠️ 双接口共存 | 需废弃 `CliExtension` |
| CLI 配置方式 | 统一使用 `skill.yaml` | ⚠️ 双格式共存 | 需废弃 `extension.properties` |
| CLI 命令格式 | `skill exec <id> <cmd>` | ⚠️ 双格式共存 | 需废弃 `skill:exec` 格式 |
| SPI 接口唯一性 | 每个接口只定义一次 | ❌ 9处重复 | 需要合并 |
| 方法命名规范 | getter/setter 遵循 JavaBean 规范 | ⚠️ 部分不一致 | `getType()` vs `getCapabilityType()` |
| 返回类型规范 | 统一使用泛型/Result 模式 | ⚠️ 部分不一致 | `Object` vs `Result<T>` |

### 5.3 代码质量检测

| 检测项 | 规范要求 | 当前状态 | 数量 |
|--------|---------|---------|------|
| FIXME 标记 | 不允许生产代码中有 FIXME | ❌ 不符合 | 14处 |
| 占位实现 | 不允许 `return null` 占位 | ❌ 不符合 | 1处 |
| @Deprecated 清理 | 2个版本内清理完毕 | ⚠️ 超期 | 55+处 |
| 循环依赖 | 不允许循环依赖 | ⚠️ 存在 | 4处 |
| Bean 冲突 | 不允许同名 Bean | ❌ 存在 | 1处 |
| init() 重复调用 | 不允许重复初始化 | ❌ 存在 | 2处 |
| 注入方式 | 统一使用构造器注入 | ⚠️ 不一致 | 2处字段注入 |

---

## 六、历史修复记录

| 版本 | 修复内容 | 状态 |
|------|---------|------|
| v3.0.4 | 修复 RouteRegistry 类型兼容性问题（Integer vs int） | ✅ 已发布 |
| v3.0.3 | skill-hotplug-starter 推送 Maven 中央仓库 | ✅ 已发布 |
| v3.0.2 | SkillCard 合并重构，删除 `SkillCardV3`，统一使用 `SkillCard` | ✅ 已发布 |
| v3.0.2 | A2AService 接口更新，`discoverSkills()` 返回类型统一 | ✅ 已发布 |
| v3.0.2 | SkillMetadata 扩展，新增 `form`/`sceneType`/`purposes`/`skillCategory` | ✅ 已发布 |
| v3.0.1 | 修复 `GitRepositoryDiscovererAdapter` 占位实现 | ✅ 已发布 |
| v3.1.0 (进行中) | CLI 接口统一化（SkillCliExtension + 适配器） | 🔄 进行中 |
| v3.1.0 (进行中) | CLI 错误码体系（CliErrorCode CLI-000~CLI-999） | ✅ 已完成 |
| v3.1.0 (进行中) | CLI Starter 模块（agent-sdk-cli-starter） | ✅ 已完成 |

---

## 七、待修复问题优先级矩阵

```
                    高影响
                      │
          P0          │          P0
   CLI接口不一致      │    SPI 9处重复定义
   配置方式不一致      │    skillForm位置不统一
   命令格式不一致      │
  ───────────────────┼─────────────────── 高紧急
          P1          │          P2
   14处伪实现/占位     │    55+处@Deprecated
   Bean冲突/注入问题   │    术语变更未对齐
   缓存键缺分支信息    │    包名变更未完成
   发现服务SRP违反     │    错误码规范不统一
                      │
                    低影响
```

---

## 八、修复建议与路线图

### Phase 1: P0 问题修复（2周）

| 任务 | 负责模块 | 工作量 |
|------|---------|--------|
| 废弃 `CliExtension`，统一使用 `SkillCliExtension` | agent-sdk-cli | 2天 |
| 废弃 `extension.properties`，统一使用 `skill.yaml` | agent-sdk-cli | 1天 |
| 统一命令格式为 `skill exec <id> <cmd>` | agent-sdk-cli | 1天 |
| 合并 9 处 SPI 重复接口到 ooder-spi-core | skill-common | 5天 |
| 修复 skillForm/sceneType 字段位置 | scene-engine + skill | 3天 |

### Phase 2: P1 问题修复（3周）

| 任务 | 负责模块 | 工作量 |
|------|---------|--------|
| 实现 ContextTransferHandler 6处 FIXME | llm-sdk | 5天 |
| 实现 LLM SDK 核心伪实现 | llm-sdk | 5天 |
| 修复 JsonStorageService Bean 冲突 | skill-common + scene-engine | 2天 |
| 修复 init() 重复调用 | skill-common | 1天 |
| 统一注入方式为构造器注入 | 全仓库 | 3天 |
| 修复缓存键缺少分支信息 | scene-engine | 2天 |
| 实现 SkillSDKAdapter.findSkillService() | scene-engine | 3天 |

### Phase 3: P2 问题修复（2周）

| 任务 | 负责模块 | 工作量 |
|------|---------|--------|
| 清理 55+ 处 @Deprecated 字段 | 全仓库 | 5天 |
| 完成术语变更全量对齐 | 全仓库 | 3天 |
| 完成包名变更 | 全仓库 | 3天 |
| 建立统一错误码规范 | 全仓库 | 3天 |

---

## 九、检测方法与工具建议

### 9.1 自动化检测脚本

```bash
# 1. 检测 @Deprecated 数量
grep -rn "@Deprecated" --include="*.java" e:\github\ooder-sdk\ | wc -l

# 2. 检测 FIXME/伪实现/占位
grep -rn "FIXME\|伪实现\|占位.*实现\|return null.*占位" --include="*.java" e:\github\ooder-sdk\

# 3. 检测 SPI 接口重复
find e:\github\ooder-sdk -name "*.java" -path "*/spi/*" | xargs basename -a | sort | uniq -d

# 4. 检测 skill.yaml 规范
find e:\github\ooder-sdk -name "skill.yaml" -exec yq '.spec.skillForm' {} \;

# 5. 检测 Bean 冲突
grep -rn "@Service\|@Component" --include="*.java" e:\github\ooder-sdk\ | awk -F: '{print $1}' | xargs grep "class " | awk '{print $NF}' | sort | uniq -d
```

### 9.2 CI 集成建议

| 检测项 | 工具 | 阈值 |
|--------|------|------|
| FIXME 数量 | grep + CI | ≤ 0 |
| @Deprecated 数量 | grep + CI | 递减趋势 |
| SPI 重复 | 自定义脚本 | = 0 |
| skill.yaml 规范 | yq 验证 | 100% 通过 |
| Bean 冲突 | Spring 启动测试 | = 0 |

---

## 十、附录：关键文件索引

### 设计审查文档

| 文件 | 绝对路径 |
|------|---------|
| CLI 设计审查报告 | `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\DESIGN-REVIEW-REPORT.md` |
| CLI 迁移指南 | `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\MIGRATION-GUIDE.md` |
| SPI 合并分析 | `e:\github\ooder-sdk\docs\spi-merge-analysis.md` |
| Skills 规范建议 | `e:\github\ooder-sdk\scene-engine\docs\SKILLS_FRAMEWORK_SPEC_SUGGESTION.md` |
| Discovery 深入分析 | `e:\github\ooder-sdk\scene-engine\docs\DISCOVERY_MODULE_DEEP_ANALYSIS.md` |
| Discovery 重构分析 | `e:\github\ooder-sdk\scene-engine\docs\SE_SDK_DISCOVERY_REFACTORING_ANALYSIS.md` |
| 术语变更文档 | `e:\github\ooder-sdk\docs\v2.3\TERMINOLOGY_IMPLEMENTATION.md` |
| SE SDK 3.1.0 重构规划 | `e:\github\ooder-sdk\scene-engine\docs\refactoring\SE_SDK_3.1.0_REFACTORING_PLAN.md` |

### 协作文档

| 文件 | 绝对路径 |
|------|---------|
| Agent-SDK 与 Scene-Engine 协作 | `e:\github\ooder-sdk\agent-sdk\SCENE_ENGINE_COLLABORATION.md` |
| Skills Framework 协作声明 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SKILLS_FRAMEWORK_COLLABORATION_STATEMENT.md` |
| Bean 冲突通知 | `e:\github\ooder-sdk\skill\skill-common\NOTICE-TEAM-COORDINATION.md` |
| 循环依赖报告 | `e:\github\ooder-sdk\skill\skill-common\CIRCULAR_DEPENDENCY_REPORT.md` |
| SPI 开发协同说明 | `e:\github\ooder-sdk\skill\skill-hotplug-starter\docs\Skill-SPI-开发协同说明.md` |
| SkillCard 合并任务 | `e:\github\ooder-sdk\agent-sdk\docs\tasks\skillcard-merge-task-assignment.md` |

### 变更记录

| 文件 | 绝对路径 |
|------|---------|
| 根仓库 CHANGELOG | `e:\github\ooder-sdk\CHANGELOG.md` |
| Agent SDK CHANGELOG | `e:\github\ooder-sdk\agent-sdk\CHANGELOG.md` |
| Scene-Engine CHANGELOG | `e:\github\ooder-sdk\scene-engine\CHANGELOG.md` |
| skill-common CHANGELOG | `e:\github\ooder-sdk\skill\skill-common\CHANGELOG-2.3.1.md` |

---

**报告生成**: 2026-04-16  
**下次检测**: 建议 v3.1.0 发布前复检  
**维护团队**: Agent SDK Team
