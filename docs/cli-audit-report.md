# CLI 深度审计报告

**审计日期**: 2026-04-17  
**审计范围**: agent-sdk-cli 模块全部代码与设计文档匹配度  
**设计文档**: `e:\github\ooder-sdk\skill\docs\architecture\04-cli-design\README.md`  
**代码根目录**: `e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\`

---

## 一、审计总览

### 1.1 审计结论

| 维度 | 设计目标 | 实现状态 | 匹配度 | 评级 |
|------|---------|---------|--------|------|
| 命令体系 | 5类命令(Core/NLP/LLM/Scene/Extension) | 4类命令(Core/Scene/Task/System) | 80% | ⚠️ |
| 命令接口 | CliCommand/CliContext/CliResult | 已实现，有差异 | 85% | ✅ |
| Agent SDK 集成 | CliCommandAdapter + CommandRegistry | CliCommandAdapter + SkillInvoker | 70% | ⚠️ |
| SceneEngine 集成 | SceneCommandHandler + SceneContextApi | SceneManagerAdapter + CollaborativeSceneGroupManager | 75% | ⚠️ |
| 命令透传安全 | SecureCommandProxy + 白名单 + 注入检测 | 完整实现，超出设计 | 110% | ✅✅ |
| 交互式模式 | JLine3 + 自动补全 | JLine3 + CompletionEngine | 90% | ✅ |
| 输出格式化 | 表格/JSON/交互式 | Text/JSON/Table 三种格式 | 100% | ✅ |
| 配置系统 | cli-config.yml | 完整实现 | 95% | ✅ |
| 审计日志 | AuditEvent | CommandAuditor | 90% | ✅ |
| 权限矩阵 | 4角色(installer/admin/leader/collaborator) | PermissionEngine集成 | 80% | ⚠️ |

**总体匹配度**: 87.5%  
**总体评级**: ⚠️ 基本达标，存在差距项

---

## 二、逐项详细审计

### 2.1 命令体系设计 vs 实现

#### 设计目标（5类命令）

```
skill
├── Core Commands (核心命令)
│   ├── list / info / install / uninstall / start / stop / update
├── NLP Commands (NLP 命令)          ← 设计有
│   ├── nlp convert / nlp skills / nlp execute
├── LLM Commands (LLM 命令)          ← 设计有
│   ├── llm generate / llm intent / llm chat
├── Scene Commands (场景命令)
│   ├── scene create / list / info / invoke / event
└── Extension Commands (扩展命令)
    └── exec <skill-id> <command>
```

#### 实际实现（4类命令）

```
ooder
├── Skill Commands (技能命令)         ✅ 已实现
│   ├── skill:list / skill:info / skill:install / skill:uninstall
│   ├── skill:start / skill:stop / skill:update
│   └── skill exec <skill-id>:<command>
├── Scene Commands (场景命令)         ✅ 已实现
│   ├── scene:create / scene:list / scene:info
│   ├── scene:invoke / scene:event
├── Task Commands (任务命令)          ✅ 新增（设计未定义）
│   ├── task:list / task:status
├── System Commands (系统命令)        ✅ 新增（设计未定义）
│   └── status
├── NLP Commands                      ❌ 未实现
│   └── (缺失)
└── LLM Commands                      ❌ 未实现
    └── (缺失)
```

#### 差异分析

| 命令类别 | 设计 | 实现 | 差异说明 |
|---------|------|------|---------|
| Core/Skill | 7个 | 7个 + exec | ✅ 匹配，exec为额外增强 |
| NLP | 3个 | 0个 | ❌ 完全缺失 |
| LLM | 3个 | 0个 | ❌ 完全缺失 |
| Scene | 5个 | 5个 | ✅ 完全匹配 |
| Extension | 1个(exec) | 1个(exec) | ✅ 匹配 |
| Task | 0个 | 2个 | ⚠️ 设计未定义但实现有 |
| System | 0个 | 1个 | ⚠️ 设计未定义但实现有 |

**问题1**: NLP 命令完全缺失  
**问题2**: LLM 命令完全缺失  
**问题3**: Task 和 System 命令未在设计文档中定义

---

### 2.2 命令接口设计 vs 实现

#### 设计目标

```java
public interface CliCommand {
    String getName();
    String getDescription();
    String getUsage();
    CliResult execute(CliContext context);
    default List<CliCommand> getSubCommands();
    default List<ParamDefinition> getParameters();
    default List<String> getRequiredPermissions();
}

public interface CliContext {
    UserSession getUserSession();
    String getParameter(String name);
    String getOption(String name);
    String[] getRawArgs();
    OutputFormat getOutputFormat();
    boolean isInteractive();
}

public interface CliResult {
    int getExitCode();
    String getOutput();
    String getErrorOutput();
    boolean isSuccess();
    String getTaskId();
}
```

#### 实际实现

```java
public interface CliCommand extends Callable<Integer> {
    String getName();
    String getDescription();
    String getUsage();
    CommandResult execute(CommandContext context);
    default boolean isInteractive();
    default String getCategory();
    default String[] getAliases();
    default boolean validate(String[] args);
}

public class CommandContext {
    String getCurrentUser();
    String getOutputFormat();
    boolean isVerbose();
    boolean isQuiet();
    Map<String, Object> getAttributes();
    // ... 缺少 getParameter/getOption/getRawArgs
}

public class CommandResult {
    int getExitCode();
    String getMessage();
    Object getData();
    String getErrorCode();
    boolean isSuccess();
    // ... 缺少 getTaskId
}
```

#### 差异分析

| 接口方法 | 设计 | 实现 | 匹配 |
|---------|------|------|------|
| `CliCommand.getName()` | ✅ | ✅ | ✅ |
| `CliCommand.getDescription()` | ✅ | ✅ | ✅ |
| `CliCommand.getUsage()` | ✅ | ✅ | ✅ |
| `CliCommand.execute()` | ✅ | ✅ | ✅ |
| `CliCommand.getSubCommands()` | ✅ | ❌ | ❌ 缺失 |
| `CliCommand.getParameters()` | ✅ | ❌ | ❌ 缺失 |
| `CliCommand.getRequiredPermissions()` | ✅ | ❌ | ❌ 缺失 |
| `CliCommand.getCategory()` | ❌ | ✅ | ⚠️ 额外增加 |
| `CliCommand.getAliases()` | ❌ | ✅ | ⚠️ 额外增加 |
| `CliCommand.validate()` | ❌ | ✅ | ⚠️ 额外增加 |
| `CliContext.getUserSession()` | ✅ | ❌ | ❌ 简化为getCurrentUser() |
| `CliContext.getParameter()` | ✅ | ❌ | ❌ 缺失 |
| `CliContext.getOption()` | ✅ | ❌ | ❌ 缺失 |
| `CliContext.getRawArgs()` | ✅ | ❌ | ❌ 缺失 |
| `CliContext.getOutputFormat()` | ✅ | ✅ | ✅ |
| `CliContext.isInteractive()` | ✅ | ❌ | ❌ 缺失 |
| `CliResult.getTaskId()` | ✅ | ❌ | ❌ 缺失 |

**问题4**: `CliCommand` 缺少 `getSubCommands()`、`getParameters()`、`getRequiredPermissions()`  
**问题5**: `CommandContext` 缺少结构化参数访问方法  
**问题6**: `CommandResult` 缺少 `getTaskId()` 异步任务追踪

---

### 2.3 Agent SDK 集成设计 vs 实现

#### 设计目标

```
CLI → CliCommandAdapter → CommandRegistry + CommandExecutor + TaskQueue
```

- 使用 `CommandRegistry` 注册命令
- 使用 `CommandExecutor` 执行命令
- 使用 `TaskQueue` 管理异步任务
- 支持同步/异步执行模式

#### 实际实现

```
CLI → CliCommandAdapter → SkillInvoker + SkillRegistry
```

- 使用 `SkillInvoker` 调用技能
- 使用 `SkillRegistry` 查找技能
- 缺少 `CommandRegistry` / `CommandExecutor` / `TaskQueue`

#### 差异分析

| 组件 | 设计 | 实现 | 匹配 |
|------|------|------|------|
| 命令注册 | CommandRegistry | SkillRegistry | ⚠️ 名称不同 |
| 命令执行 | CommandExecutor | SkillInvoker | ⚠️ 名称不同 |
| 异步任务 | TaskQueue | TaskStatusMonitor | ⚠️ 简化实现 |
| 同步执行 | commandExecutor.execute() | skillInvoker.invoke() | ✅ 功能等价 |
| 异步执行 | commandExecutor.executeAsync() | skillService.executeAsync() | ✅ 功能等价 |
| 任务状态查询 | taskQueue.getStatus() | taskMonitor | ⚠️ 简化 |

**问题7**: 缺少独立的 `CommandRegistry`，复用了 `SkillRegistry`  
**问题8**: 缺少独立的 `CommandExecutor`，复用了 `SkillInvoker`  
**问题9**: `TaskQueue` 未实现，仅有 `TaskStatusMonitor`

---

### 2.4 SceneEngine 集成设计 vs 实现

#### 设计目标

```
CLI → SceneCommandHandler → SceneContextApi + CapabilityBindingService + SceneEventBus
```

#### 实际实现

```
CLI → SceneManagerAdapter → CollaborativeSceneGroupManager + SkillInvoker
```

#### 差异分析

| 组件 | 设计 | 实现 | 匹配 |
|------|------|------|------|
| 场景API | SceneContextApi | CollaborativeSceneGroupManager | ⚠️ 名称不同 |
| 能力绑定 | CapabilityBindingService | 内嵌于SceneManagerAdapter | ⚠️ 简化 |
| 事件总线 | SceneEventBus | ❌ 未实现 | ❌ |
| 创建场景 | sceneContextApi.createScene() | sceneGroupManager.createGroup() | ✅ 功能等价 |
| 调用能力 | scene.invokeCapability() | skillInvoker.invoke() | ✅ 功能等价 |
| 发布事件 | scene.publishEvent() | ❌ 未实现 | ❌ |

**问题10**: `SceneEventBus` 未实现，`scene:event` 命令缺少底层支持  
**问题11**: `CapabilityBindingService` 未独立实现

---

### 2.5 命令透传安全设计 vs 实现

#### 设计目标

- 白名单校验
- 危险字符过滤
- SQL注入检测
- 脚本注入检测
- 权限映射校验
- 审计记录

#### 实际实现

| 安全机制 | 设计 | 实现 | 匹配 |
|---------|------|------|------|
| 白名单校验 | ✅ | ✅ `isCommandAllowed()` | ✅ |
| 危险字符过滤 | ✅ | ✅ `DANGEROUS_CHARS` | ✅ |
| SQL注入检测 | ✅ | ✅ `SQL_INJECTION` | ✅ |
| 脚本注入检测 | ✅ | ✅ `SCRIPT_INJECTION` | ✅ |
| 路径遍历检测 | ❌ | ✅ `PATH_TRAVERSAL` | ✅✅ 超出设计 |
| 权限映射校验 | ✅ | ✅ `PermissionEngine` | ✅ |
| 敏感参数过滤 | ❌ | ✅ `filterSensitiveParams()` | ✅✅ 超出设计 |
| 审计记录 | ✅ | ✅ `CommandAuditor` | ✅ |

**评级**: ✅✅ 超出设计目标，额外实现了路径遍历检测和敏感参数过滤

---

### 2.6 交互式模式设计 vs 实现

#### 设计目标

- JLine3 交互式终端
- 命令自动补全
- 历史命令管理

#### 实际实现

| 功能 | 设计 | 实现 | 匹配 |
|------|------|------|------|
| JLine3 | ✅ | ✅ `JLineCli` | ✅ |
| 自动补全 | ✅ | ✅ `CompletionEngine` | ✅ |
| 历史管理 | ✅ | ✅ `HistoryManager` | ✅ |
| 密码输入 | ❌ | ✅ `readPassword()` | ✅✅ |
| 提示符 | `skill> ` | `ooder> ` | ⚠️ 命名变更 |

**评级**: ✅ 基本匹配

---

### 2.7 配置系统设计 vs 实现

#### 设计目标

- cli-config.yml 配置文件
- 安全配置（白名单、注入检测）
- 输出配置
- 交互式配置
- 任务配置

#### 实际实现

| 配置项 | 设计 | 实现 | 匹配 |
|--------|------|------|------|
| security.enabled | ✅ | ✅ | ✅ |
| security.whitelist | ✅ | ✅ | ✅ |
| security.injection-check | ✅ | ✅ | ✅ |
| security.dangerous-chars | ✅ | ✅ | ✅ |
| security.sensitive-keys | ✅ | ✅ | ✅ |
| security.audit-enabled | ✅ | ✅ | ✅ |
| output.default-format | ✅ | ✅ | ✅ |
| output.color-enabled | ✅ | ✅ | ✅ |
| interactive.enabled | ✅ | ✅ | ✅ |
| interactive.history-size | ✅ | ✅ | ✅ |
| interactive.prompt | ✅ | ✅ | ✅ |
| task.default-timeout | ✅ | ✅ | ✅ |
| task.max-concurrent | ✅ | ✅ | ✅ |

**评级**: ✅ 完全匹配

---

### 2.8 权限矩阵设计 vs 实现

#### 设计目标

| 角色 | 核心命令 | NLP命令 | LLM命令 | 场景命令 | 扩展命令 |
|------|----------|---------|---------|----------|----------|
| installer | ✅全部 | ❌ | ❌ | ❌ | ❌ |
| admin | ✅全部 | ✅全部 | ✅全部 | ✅全部 | ✅白名单 |
| leader | ✅view | ✅查询 | ✅生成 | ✅全部 | ✅白名单 |
| collaborator | ✅view | ✅查询 | ✅生成 | ✅view/invoke | ❌ |

#### 实际实现

- 集成了 `PermissionEngine` 接口
- `SecureCommandProxy.hasPermission()` 调用 `permissionEngine.hasPermission()`
- 但未实现角色级别的权限矩阵

**问题12**: 权限矩阵未在 CLI 层实现，依赖外部 `PermissionEngine`

---

## 三、问题汇总与优先级

### 🔴 高优先级（影响核心功能）

| 编号 | 问题 | 影响 | 建议修复 |
|------|------|------|---------|
| P1 | NLP 命令完全缺失 | 无法使用 NLP 功能 | 实现 `nlp:convert`、`nlp:skills`、`nlp:execute` |
| P2 | LLM 命令完全缺失 | 无法使用 LLM 功能 | 实现 `llm:generate`、`llm:intent`、`llm:chat` |
| P3 | `SceneEventBus` 未实现 | `scene:event` 命令无底层支持 | 实现 SceneEventBus 或集成现有事件机制 |
| P4 | `CommandResult.getTaskId()` 缺失 | 无法追踪异步任务 | 添加 taskId 字段 |

### 🟡 中优先级（影响完整性）

| 编号 | 问题 | 影响 | 建议修复 |
|------|------|------|---------|
| P5 | `CliCommand.getSubCommands()` 缺失 | 无法支持子命令 | 添加子命令支持 |
| P6 | `CliCommand.getRequiredPermissions()` 缺失 | 权限声明不完整 | 添加权限声明方法 |
| P7 | `CommandContext` 缺少结构化参数访问 | 参数获取不便 | 添加 getParameter/getOption |
| P8 | `TaskQueue` 未实现 | 异步任务管理不完整 | 实现独立 TaskQueue |
| P9 | 权限矩阵未在 CLI 层实现 | 角色权限控制不完整 | 实现 CLI 层权限矩阵 |

### 🟢 低优先级（改进项）

| 编号 | 问题 | 影响 | 建议修复 |
|------|------|------|---------|
| P10 | Task/System 命令未在设计文档中定义 | 文档不完整 | 更新设计文档 |
| P11 | 命令前缀 `skill:` vs `ooder` | 命名不一致 | 统一命令命名规范 |
| P12 | `CapabilityBindingService` 未独立实现 | 场景能力绑定耦合 | 独立实现 |

---

## 四、实现亮点（超出设计）

| 亮点 | 说明 |
|------|------|
| ✅ 路径遍历检测 | `SecureCommandProxy` 额外实现了 `PATH_TRAVERSAL` 检测 |
| ✅ 敏感参数过滤 | `filterSensitiveParams()` 自动脱敏敏感字段 |
| ✅ Task 命令 | 设计未定义但实现了 `task:list` 和 `task:status` |
| ✅ System 命令 | 设计未定义但实现了 `status` 命令 |
| ✅ 命令别名 | `CliCommand.getAliases()` 支持命令别名 |
| ✅ 参数验证 | `CliCommand.validate()` 支持参数预校验 |
| ✅ 命令分类 | `CliCommand.getCategory()` 支持命令分类 |
| ✅ Builder 模式 | `OoderCli.builder()` 支持 Builder 模式创建 |
| ✅ 嵌入式使用 | `OoderCli.execute()` 支持嵌入式调用 |
| ✅ 密码输入 | `JLineCli.readPassword()` 支持密码安全输入 |

---

## 五、文件清单与代码统计

### 5.1 实现文件清单

| 文件 | 行数 | 说明 |
|------|------|------|
| `OoderCli.java` | 507 | 主入口，命令注册，交互式模式 |
| `OoderCliBuilder.java` | ~100 | Builder 模式 |
| `api/CliCommand.java` | 89 | 命令接口 |
| `api/CommandContext.java` | ~80 | 命令上下文 |
| `api/CommandResult.java` | ~100 | 命令结果 |
| `api/CliRouter.java` | ~30 | 路由器接口 |
| `api/CliParser.java` | ~30 | 解析器接口 |
| `api/CliFormatter.java` | ~20 | 格式化器接口 |
| `api/InteractiveCli.java` | ~40 | 交互式接口 |
| `api/ExtensionRegistry.java` | ~20 | 扩展注册接口 |
| `api/SkillCliExtension.java` | ~30 | Skill CLI 扩展接口 |
| `api/CliException.java` | ~30 | CLI 异常 |
| `api/CliErrorCode.java` | ~30 | 错误码 |
| `core/router/DefaultCliRouter.java` | 138 | 默认路由器 |
| `core/parser/PicocliParser.java` | ~150 | Picocli 解析器 |
| `core/interactive/JLineCli.java` | 166 | JLine3 交互式 |
| `core/interactive/CompletionEngine.java` | ~80 | 自动补全引擎 |
| `core/formatter/TextFormatter.java` | ~50 | 文本格式化 |
| `core/formatter/JsonFormatter.java` | ~50 | JSON 格式化 |
| `core/formatter/TableFormatter.java` | ~80 | 表格格式化 |
| `core/registry/ExtensionRegistryImpl.java` | ~60 | 扩展注册实现 |
| `core/registry/SkillExtensionRegistry.java` | ~80 | Skill 扩展注册 |
| `security/SecureCommandProxy.java` | 290 | 安全命令代理 |
| `security/CommandAuditor.java` | 157 | 命令审计器 |
| `config/CliSecurityConfig.java` | ~60 | 安全配置 |
| `config/SkillCliConfigLoader.java` | ~80 | 配置加载器 |
| `config/SkillCliConfiguration.java` | ~60 | 配置类 |
| `adapter/CliCommandAdapter.java` | 110 | 命令适配器 |
| `adapter/SceneManagerAdapter.java` | 299 | 场景管理适配器 |
| `adapter/TaskStatusMonitor.java` | ~80 | 任务状态监控 |
| `adapter/SkillCliExtensionAdapter.java` | ~60 | Skill CLI 扩展适配器 |
| `adapter/LegacyCliExtensionAdapter.java` | ~60 | 旧版扩展适配器 |
| `command/skill/SkillListCommand.java` | ~80 | 技能列表命令 |
| `command/skill/SkillInfoCommand.java` | ~80 | 技能详情命令 |
| `command/skill/SkillInstallCommand.java` | ~150 | 技能安装命令 |
| `command/skill/SkillUninstallCommand.java` | ~80 | 技能卸载命令 |
| `command/skill/SkillUpdateCommand.java` | ~80 | 技能更新命令 |
| `command/skill/SkillStartCommand.java` | ~60 | 技能启动命令 |
| `command/skill/SkillStopCommand.java` | ~60 | 技能停止命令 |
| `command/skill/SkillExecCommand.java` | ~100 | 技能执行命令 |
| `command/scene/SceneCreateCommand.java` | ~100 | 场景创建命令 |
| `command/scene/SceneListCommand.java` | ~60 | 场景列表命令 |
| `command/scene/SceneInfoCommand.java` | ~80 | 场景详情命令 |
| `command/scene/SceneInvokeCommand.java` | ~100 | 场景调用命令 |
| `command/scene/SceneEventCommand.java` | ~80 | 场景事件命令 |
| `command/task/TaskListCommand.java` | ~60 | 任务列表命令 |
| `command/task/TaskStatusCommand.java` | ~80 | 任务状态命令 |
| `command/system/StatusCommand.java` | ~60 | 系统状态命令 |

**总文件数**: 48 个  
**估计总行数**: ~5,000 行

---

## 六、修复建议与实施计划

### 6.1 第一阶段：补齐核心缺失（1周）

| 任务 | 优先级 | 工作量 |
|------|--------|--------|
| 实现 NLP 命令 (nlp:convert, nlp:skills, nlp:execute) | P1 | 3天 |
| 实现 LLM 命令 (llm:generate, llm:intent, llm:chat) | P2 | 3天 |
| 实现 SceneEventBus 集成 | P3 | 2天 |
| 添加 CommandResult.getTaskId() | P4 | 0.5天 |

### 6.2 第二阶段：完善接口设计（1周）

| 任务 | 优先级 | 工作量 |
|------|--------|--------|
| 添加 CliCommand.getSubCommands() | P5 | 1天 |
| 添加 CliCommand.getRequiredPermissions() | P6 | 1天 |
| 完善 CommandContext 参数访问 | P7 | 1天 |
| 实现独立 TaskQueue | P8 | 2天 |
| 实现 CLI 层权限矩阵 | P9 | 2天 |

### 6.3 第三阶段：文档与规范（3天）

| 任务 | 优先级 | 工作量 |
|------|--------|--------|
| 更新设计文档，补充 Task/System 命令 | P10 | 1天 |
| 统一命令命名规范 | P11 | 1天 |
| 独立实现 CapabilityBindingService | P12 | 1天 |

---

## 七、审计结论

### 7.1 总体评价

CLI 模块的实现质量**整体良好**，核心功能（Skill 命令、Scene 命令、安全机制、交互式模式）已完整实现，安全机制甚至**超出设计目标**。但存在以下关键差距：

1. **NLP 和 LLM 命令完全缺失** - 这是最大的差距项
2. **异步任务追踪不完整** - 缺少 TaskId 返回
3. **接口设计与实现存在偏差** - 部分方法缺失

### 7.2 风险评估

| 风险 | 级别 | 说明 |
|------|------|------|
| NLP/LLM 功能不可用 | 🔴 高 | 用户无法通过 CLI 使用 NLP/LLM 功能 |
| 异步任务无法追踪 | 🟡 中 | 安装等异步操作无法追踪进度 |
| 接口不一致 | 🟡 中 | 后续扩展可能遇到兼容性问题 |
| 权限控制不完整 | 🟢 低 | 依赖外部 PermissionEngine |

### 7.3 建议

1. **优先补齐 NLP/LLM 命令** - 这是设计文档明确要求的核心功能
2. **统一接口设计** - 确保实现与设计文档一致
3. **更新设计文档** - 将 Task/System 命令纳入设计
4. **完善异步任务机制** - 添加 TaskId 返回和追踪

---

**审计人**: Ooder SDK Team  
**审计完成时间**: 2026-04-17
