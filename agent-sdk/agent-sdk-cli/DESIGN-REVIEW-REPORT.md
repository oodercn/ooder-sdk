# Agent SDK CLI 设计评审报告

## 评审概述

**评审对象**: Agent SDK CLI Integration Guide (v3.1.0)  
**评审日期**: 2026年4月  
**评审范围**: 
- 底层设计规范
- 应用端设计
- 驱动桥接设计
- 与 Skills 框架的兼容性

---

## 1. 底层设计规范评审

### 1.1 架构分层分析

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          架构分层评估                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  当前设计（Agent SDK CLI）：                                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 1: CLI 用户界面层                                             │   │
│  │  - 交互式 CLI (JLine3)                                              │   │
│  │  - 命令行参数 (Picocli)                                             │   │
│  │  - 结果输出 (Text/JSON)                                             │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                              │
│                              ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 2: 命令路由层                                                 │   │
│  │  - CliRouter (DefaultCliRouter)                                     │   │
│  │  - 命令注册/注销/路由分发                                            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                              │
│                              ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 3: 命令实现层                                                 │   │
│  │  - Skill 命令 / Scene 命令 / Task 命令                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                              │
│                              ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 4: 适配器层                                                   │   │
│  │  - CliCommandAdapter / SceneManagerAdapter / TaskStatusMonitor      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                              │                                              │
│                              ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Layer 5: Agent SDK 层                                               │   │
│  │  - SkillRegistry / CollaborativeSceneGroupMgr / SkillService        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 设计规范符合性检查

| 检查项 | 规范要求 | 当前设计 | 状态 | 说明 |
|--------|----------|----------|------|------|
| **分层清晰** | 各层职责单一 | 5层架构 | ✅ 通过 | 层次清晰，职责明确 |
| **依赖方向** | 上层依赖下层 | 单向依赖 | ✅ 通过 | 无循环依赖 |
| **接口抽象** | 面向接口编程 | CliCommand/CliRouter 接口 | ✅ 通过 | 良好抽象 |
| **无状态设计** | 协议层无状态 | CliRouter 无状态 | ✅ 通过 | 支持水平扩展 |
| **错误处理** | 统一错误码 | CommandResult | ⚠️ 警告 | 需补充错误码规范 |
| **日志规范** | SLF4J 标准 | 使用 SLF4J | ✅ 通过 | 符合规范 |

### 1.3 发现的问题

#### 问题 1: 错误码规范缺失

**现状**:
```java
// 当前实现
return CommandResult.error("Operation failed: " + e.getMessage(), e);
```

**问题**: 错误信息是字符串拼接，没有统一的错误码体系

**建议**:
```java
// 建议实现
public enum CliErrorCode {
    SKILL_NOT_FOUND("CLI-001", "Skill not found: %s"),
    PERMISSION_DENIED("CLI-002", "Permission denied for command: %s"),
    INVALID_ARGS("CLI-003", "Invalid arguments: %s"),
    EXECUTION_TIMEOUT("CLI-004", "Command execution timeout"),
    // ...
}

return CommandResult.error(CliErrorCode.SKILL_NOT_FOUND, skillId);
```

#### 问题 2: 缺少熔断降级机制

**现状**: 直接调用 Agent SDK，无熔断保护

**建议**: 添加 Circuit Breaker
```java
@Component
public class CircuitBreakerCommandProxy implements CliCommand {
    
    private final CircuitBreaker circuitBreaker;
    private final CliCommand delegate;
    
    @Override
    public CommandResult execute(CommandContext context) {
        return circuitBreaker.execute(() -> delegate.execute(context));
    }
}
```

#### 问题 3: 配置管理不够灵活

**现状**: 仅支持 YAML 配置

**建议**: 支持多源配置（环境变量、配置中心）
```java
@ConfigurationProperties(prefix = "ooder.cli")
public class CliProperties {
    // 支持 @Value 注入
    // 支持动态刷新
    // 支持配置中心集成
}
```

---

## 2. 应用端设计评审

### 2.1 Spring Boot 集成设计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Spring Boot 集成评估                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  当前设计：                                                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  1. 添加依赖                                                          │   │
│  │     <dependency>                                                     │   │
│  │       <groupId>net.ooder</groupId>                                   │   │
│  │       <artifactId>agent-sdk-cli</artifactId>                         │   │
│  │       <version>3.1.0</version>                                       │   │
│  │     </dependency>                                                    │   │
│  │                                                                      │   │
│  │  2. 配置 YAML                                                        │   │
│  │     ooder:                                                           │   │
│  │       cli:                                                           │   │
│  │         enabled: true                                                │   │
│  │         security:                                                    │   │
│  │           enabled: true                                              │   │
│  │                                                                      │   │
│  │  3. 自动配置                                                          │   │
│  │     @Configuration                                                   │   │
│  │     @ConditionalOnProperty                                           │   │
│  │     public class OoderCliConfiguration                               │   │
│  │                                                                      │   │
│  │  4. 使用 CLI                                                          │   │
│  │     @Autowired                                                       │   │
│  │     private OoderCli cli;                                            │   │
│  │     cli.run(args);                                                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  评估结果：                                                                   │
│  ✅ 符合 Spring Boot 自动配置规范                                             │
│  ✅ 条件化配置支持                                                            │
│  ✅ 依赖注入支持                                                              │
│  ⚠️ 缺少 Starter 封装（建议提供 agent-sdk-cli-starter）                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 非 Spring 集成设计

**现状**: 提供手动配置方式

```java
// 当前实现
OoderCli cli = new OoderCli();
cli.setSkillRegistry(skillRegistry);
cli.setSkillInstaller(skillInstaller);
// ... 手动设置所有依赖
cli.initializeAdapters();
```

**问题**: 依赖设置繁琐，容易遗漏

**建议**: 提供 Builder 模式
```java
// 建议实现
OoderCli cli = OoderCli.builder()
    .skillRegistry(skillRegistry)
    .skillInstaller(skillInstaller)
    .skillInvoker(skillInvoker)
    .sceneGroupManager(sceneGroupManager)
    .permissionEngine(permissionEngine)  // optional
    .build();
```

### 2.3 嵌入式使用设计

**现状**: 支持程序化调用

```java
// 当前实现 - 需要捕获 System.out
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream originalOut = System.out;
System.setOut(new PrintStream(baos));
int exitCode = cli.run(args);
System.setOut(originalOut);
String output = baos.toString();
```

**问题**: 重定向 System.out 不够优雅

**建议**: 提供结构化返回
```java
// 建议实现
CliExecutionResult result = cli.execute(args);
// result.getExitCode()
// result.getOutput()  // 字符串
// result.getData()    // 结构化数据
// result.getError()   // 错误信息
```

---

## 3. 驱动桥接设计评审

### 3.1 适配器层设计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          适配器层设计评估                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  当前适配器：                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  CliCommandAdapter                                                  │   │
│  │  - invokeSkill()                                                    │   │
│  │  - getSkillInfo()                                                   │   │
│  │                                                                     │   │
│  │  SceneManagerAdapter                                                │   │
│  │  - createScene()                                                    │   │
│  │  - invokeCapability()                                               │   │
│  │                                                                     │   │
│  │  TaskStatusMonitor                                                  │   │
│  │  - submitTask()                                                     │   │
│  │  - getStatus()                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  评估结果：                                                                   │
│  ✅ 适配器职责清晰                                                           │
│  ✅ 与 Agent SDK 解耦                                                        │
│  ⚠️ 缺少适配器统一接口（建议定义 Adapter 接口）                                │
│  ⚠️ 缺少适配器健康检查机制                                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 与 Skills 框架的兼容性

| 检查项 | Skills 框架 | Agent SDK CLI | 兼容性 | 说明 |
|--------|-------------|---------------|--------|------|
| Skill 注册 | PluginManager | SkillRegistry | ⚠️ 需确认 | 是否是同一接口？ |
| 生命周期 | SkillLifecycle | SkillService | ⚠️ 需确认 | 接口是否一致？ |
| 命令扩展 | SkillCliExtension | CliExtension | ❌ 不兼容 | 接口名称不同！ |
| 配置方式 | skill.yaml | extension.properties | ❌ 不兼容 | 配置方式不同！ |

### 3.3 关键兼容性问题

#### 问题 1: CLI 扩展接口不一致

**Skills 框架**（根据我们之前的文档）:
```java
public interface SkillCliExtension {
    String getSkillId();
    String getCommand();
    String getDescription();
    CliResult execute(String[] args, SceneContext context);
}
```

**Agent SDK CLI**:
```java
public interface CliExtension {
    String getId();
    String getName();
    String getVersion();
    List<CliCommand> getCommands();
    void initialize();
    void destroy();
}
```

**冲突**: 
- 接口名称不同：`SkillCliExtension` vs `CliExtension`
- 方法不同：`getSkillId()` vs `getId()`
- 参数不同：`execute(String[] args, SceneContext)` vs `getCommands()`

**建议**: 统一接口定义，或提供适配层

#### 问题 2: 配置方式不一致

**Skills 框架**: 使用 `skill.yaml`
```yaml
skill:
  cli:
    extensions:
      - command: reindex
        handler: com.example.ReindexCommand
```

**Agent SDK CLI**: 使用 `extension.properties`
```properties
extension.class=com.example.MySkillCliExtension
extension.id=my-skill-cli
```

**冲突**: 配置方式完全不同

**建议**: 统一使用 `skill.yaml`，或提供配置转换工具

#### 问题 3: 命令命名规范不一致

**Skills 框架**: `skill exec rag-skill reindex`
**Agent SDK CLI**: `ooder skill:exec --skill-id rag-skill --capability reindex`

**冲突**: 
- 命令前缀：`skill` vs `ooder`
- 子命令格式：`exec rag-skill reindex` vs `skill:exec --skill-id rag-skill`

**建议**: 统一命令规范，或提供命令映射

---

## 4. 与 Skills 驱动层的集成分析

### 4.1 任务分解建议

基于以上分析，建议将 CLI 升级任务分解为：

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          任务分解建议                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  任务 1: 接口统一化（高优先级）                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • 统一 CliExtension 接口定义                                        │   │
│  │  • 统一配置方式（统一使用 skill.yaml）                                │   │
│  │  • 统一命令命名规范                                                  │   │
│  │  • 提供向后兼容的适配层                                              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  任务 2: 适配器增强（中优先级）                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • 定义 Adapter 统一接口                                             │   │
│  │  • 添加适配器健康检查                                                │   │
│  │  • 添加熔断降级机制                                                  │   │
│  │  • 添加性能监控                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  任务 3: 应用端优化（中优先级）                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • 提供 agent-sdk-cli-starter                                        │   │
│  │  • 优化非 Spring 集成（Builder 模式）                                 │   │
│  │  • 优化嵌入式使用（结构化返回）                                       │   │
│  │  • 支持多源配置                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  任务 4: 规范完善（低优先级）                                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • 定义统一错误码体系                                                │   │
│  │  • 完善日志规范                                                      │   │
│  │  • 完善文档                                                          │   │
│  │  • 提供迁移指南                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 团队分工调整建议

基于任务分解，建议调整团队分工：

| 任务 | 负责人 | 协作人 | 工期 | 依赖 |
|------|--------|--------|------|------|
| 接口统一化 | 技术负责人 | 后端A、后端B | 2周 | 需与 Skills 团队对齐 |
| 适配器增强 | 后端B | 后端C | 1周 | 接口统一化完成 |
| 应用端优化 | 后端A | 前端A | 1周 | 接口统一化完成 |
| 规范完善 | 测试A | 全团队 | 持续 | - |

---

## 5. 执行摘要

### 5.1 总体评估

| 维度 | 评分 | 说明 |
|------|------|------|
| 架构设计 | ⭐⭐⭐⭐ | 分层清晰，职责明确 |
| 规范符合 | ⭐⭐⭐ | 基本符合，需完善错误码等 |
| 兼容性 | ⭐⭐ | 与 Skills 框架存在多处不兼容 |
| 可扩展性 | ⭐⭐⭐⭐ | 扩展机制完善 |
| 文档质量 | ⭐⭐⭐⭐⭐ | 文档详细，示例丰富 |

### 5.2 关键发现

1. **架构设计良好**: 5层架构清晰，职责分离合理
2. **兼容性问题严重**: 与 Skills 框架在接口、配置、命令规范上存在多处冲突
3. **缺少统一规范**: 错误码、配置方式等需要统一
4. **集成方式需优化**: 非 Spring 集成和嵌入式使用体验有待提升

### 5.3 关键决策建议

| 决策项 | 建议 | 优先级 |
|--------|------|--------|
| 接口统一 | 统一使用 `SkillCliExtension` 接口 | 🔴 P0 |
| 配置统一 | 统一使用 `skill.yaml` | 🔴 P0 |
| 命令规范 | 统一使用 `skill exec <skill-id> <command>` | 🔴 P0 |
| 错误码规范 | 定义统一错误码体系 | 🟡 P1 |
| Starter 封装 | 提供 `agent-sdk-cli-starter` | 🟡 P1 |
| 熔断机制 | 添加 Circuit Breaker | 🟢 P2 |

### 5.4 下一步行动

1. **立即行动**（本周）：
   - [ ] 召开技术评审会议，讨论接口统一方案
   - [ ] 与 Skills 团队对齐接口定义
   - [ ] 制定兼容性改造计划

2. **短期行动**（2周内）：
   - [ ] 完成接口统一化改造
   - [ ] 提供适配层实现
   - [ ] 更新集成指南

3. **中期行动**（1个月内）：
   - [ ] 完成应用端优化
   - [ ] 完善规范和文档
   - [ ] 进行全面测试

---

## 附录

### A. 接口对比表

| 维度 | Skills 框架 | Agent SDK CLI | 建议统一为 |
|------|-------------|---------------|-----------|
| 扩展接口 | `SkillCliExtension` | `CliExtension` | `SkillCliExtension` |
| 获取ID | `getSkillId()` | `getId()` | `getSkillId()` |
| 获取命令 | `getCommand()` | `getCommands()` | `getCommand()` |
| 执行方法 | `execute(args, context)` | 通过 `CliCommand` | `execute(args, context)` |
| 配置方式 | `skill.yaml` | `extension.properties` | `skill.yaml` |

### B. 命令对比表

| 操作 | Skills 框架 | Agent SDK CLI | 建议统一为 |
|------|-------------|---------------|-----------|
| 列出 Skills | `skill list` | `ooder skill:list` | `skill list` |
| 执行命令 | `skill exec <id> <cmd>` | `ooder skill:exec --skill-id <id>` | `skill exec <id> <cmd>` |
| 创建场景 | `skill scene create` | `ooder scene:create` | `skill scene create` |

### C. 参考文档

- [Skills 框架 CLI 设计](../docs/architecture/04-cli-design/README.md)
- [Agent SDK CLI 集成指南](./INTEGRATION-GUIDE.md)
- [Ooder 架构文档系列](../docs/architecture/)

---

**评审人**: Ooder 技术团队  
**评审日期**: 2026年4月  
**报告版本**: v1.0
