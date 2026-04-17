# Agent SDK CLI - 设计文档汇总

## 概述

本文档汇总了 Agent SDK CLI 的完整设计方案，包括已完成的 Phase 1-3 实现、应用层功能抽象、驱动层接口设计和可视化展现规划。

## 文档清单

| 文档 | 路径 | 描述 |
|------|------|------|
| 迁移指南 | `../MIGRATION-GUIDE.md` | 从旧版 CLI 迁移到新版统一接口的完整指南 |
| 设计审查报告 | `../DESIGN-REVIEW-REPORT.md` | Agent SDK CLI 与 Skills 框架兼容性分析和建议 |
| 应用层设计 | `APPLICATION-LAYER-DESIGN.md` | 应用层功能抽象、领域模型、服务接口设计 |
| 驱动层接口 | `DRIVER-LAYER-INTERFACE.md` | 驱动层标准接口定义（Skill/Scene/Task/Config） |
| 可视化设计 | `VISUALIZATION-DESIGN.md` | Skills 模式可视化组件设计和 Web Dashboard 实现 |

## 架构总览

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              可视化展现层 (Presentation)                          │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ SkillPanel   │ │ SceneCanvas  │ │ TaskMonitor  │ │ LogViewer    │            │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              应用层 (Application)                                │
│  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐                │
│  │ SkillAppService  │ │ SceneAppService  │ │ TaskAppService   │                │
│  │ - install()      │ │ - create()       │ │ - submit()       │                │
│  │ - invoke()       │ │ - invoke()       │ │ - cancel()       │                │
│  │ - getStatus()    │ │ - addMember()    │ │ - getStatus()    │                │
│  └──────────────────┘ └──────────────────┘ └──────────────────┘                │
└─────────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              领域层 (Domain)                                     │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ SkillEntity  │ │ SceneEntity  │ │ TaskEntity   │ │ DomainEvent  │            │
│  │ - skillId    │ │ - sceneId    │ │ - taskId     │ │ - occurredOn │            │
│  │ - status     │ │ - members    │ │ - status     │ │ - payload    │            │
│  │ - capabilities│ │ - context   │ │ - result     │ │              │            │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              驱动层 (Driver)                                     │
│  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐                │
│  │   SkillDriver    │ │   SceneDriver    │ │   TaskDriver     │                │
│  │   Interface      │ │   Interface      │ │   Interface      │                │
│  └──────────────────┘ └──────────────────┘ └──────────────────┘                │
│           │                   │                   │                             │
│           ▼                   ▼                   ▼                             │
│  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐                │
│  │ SkillsFramework  │ │   Agent SDK      │ │   Mock/Test      │                │
│  │   Driver Impl    │ │   Driver Impl    │ │   Driver Impl    │                │
│  └──────────────────┘ └──────────────────┘ └──────────────────┘                │
└─────────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              基础设施层 (Infrastructure)                          │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐            │
│  │ SkillRegistry│ │ SceneManager │ │ TaskScheduler│ │ ConfigStore  │            │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 核心接口速查

### 1. SkillCliExtension（统一扩展接口）

```java
public interface SkillCliExtension {
    String getSkillId();
    String getCommand();
    String getDescription();
    CliResult execute(String[] args, SceneContext context);
    
    interface CliResult {
        int getExitCode();
        String getMessage();
        Object getData();
        boolean isSuccess();
    }
}
```

### 2. SkillDriver（驱动接口）

```java
public interface SkillDriver {
    // 生命周期
    SkillEntity install(String source, Map<String, Object> config);
    UninstallResult uninstall(String skillId, boolean force);
    StartResult start(String skillId, Map<String, Object> params);
    StopResult stop(String skillId, boolean force);
    
    // 查询
    List<SkillEntity> getAllSkills();
    SkillEntity getSkill(String skillId);
    SkillStatus getStatus(String skillId);
    
    // 调用
    Object invoke(String skillId, String capabilityId, Map<String, Object> params);
    String invokeAsync(String skillId, String capabilityId, 
                       Map<String, Object> params, InvokeCallback callback);
}
```

### 3. SkillAppService（应用服务）

```java
public interface SkillAppService {
    List<SkillEntity> getAllSkills();
    SkillEntity getSkill(String skillId);
    InstallResult installSkill(InstallRequest request);
    InvokeResult invokeCapability(String skillId, String capabilityId, 
                                   Map<String, Object> params);
    String invokeCapabilityAsync(String skillId, String capabilityId,
                                  Map<String, Object> params);
}
```

## 命令规范

### 统一命令格式

```bash
# Skill 命令
skill exec <skill-id> <command> [args...]     # 执行 Skill 命令
skill list                                    # 列出所有 Skills
skill info <skill-id>                         # 查看 Skill 详情
skill install <source>                        # 安装 Skill
skill uninstall <skill-id>                    # 卸载 Skill
skill start <skill-id>                        # 启动 Skill
skill stop <skill-id>                         # 停止 Skill

# Scene 命令
scene list                                    # 列出所有场景
scene create <group-id> --main <capability>   # 创建场景
scene info <scene-id>                         # 查看场景详情
scene invoke <scene-id> <capability>          # 调用场景能力
scene destroy <scene-id>                      # 销毁场景

# Task 命令
task list                                     # 列出所有任务
task status <task-id>                         # 查看任务状态
task cancel <task-id>                         # 取消任务
task logs <task-id>                           # 查看任务日志
```

## 配置规范

### skill.yaml 配置格式

```yaml
skill:
  id: my-skill
  name: My Skill
  version: 1.0.0
  
  cli:
    extensions:
      - skillId: my-skill
        command: reindex
        handler: com.example.ReindexCommand
        description: "Rebuild search index"
        enabled: true
        
    security:
      enabled: true
      auditEnabled: true
      injectionCheckEnabled: true
      allowedCommands:
        - my-skill:reindex
      blockedCommands: []
      
    output:
      format: text  # text, json, table
      colorEnabled: true
      verbose: false
```

## 错误码体系

| 错误码 | 类别 | 说明 |
|--------|------|------|
| CLI-000 | 成功 | Success |
| CLI-002 | 通用 | Invalid argument |
| CLI-100 | Skill | Skill not found |
| CLI-103 | Skill | Skill execution failed |
| CLI-200 | Scene | Scene not found |
| CLI-203 | Scene | Scene invocation failed |
| CLI-300 | Task | Task not found |
| CLI-301 | Task | Task execution failed |
| CLI-400 | 安全 | Permission denied |
| CLI-403 | 安全 | Injection detected |
| CLI-500 | 配置 | Configuration not found |
| CLI-600 | 扩展 | Extension not found |

## 可视化组件清单

| 组件 | Ooder UI 映射 | 功能 |
|------|---------------|------|
| CLI.SkillPanel | ood.UI.Block + InfoBlock | Skill 管理面板 |
| CLI.SceneCanvas | ood.svg.SVGPaper | 场景拓扑图 |
| CLI.TaskMonitor | ood.UI.Block + TreeGrid + ECharts | 任务监控 |
| CLI.LogViewer | ood.UI.Block + List | 日志查看器 |
| CLI.CommandInput | ood.UI.FormLayout + ComboInput | 命令输入 |
| CLI.ResultPanel | ood.UI.Block + TreeGrid | 结果展示 |
| CLI.ConfigEditor | ood.UI.FormLayout | 配置编辑 |
| CLI.Dashboard | ood.UI.Block | 主仪表盘 |

## 开发规范

### 1. 扩展开发

```java
@Component
public class MySkillExtension implements SkillCliExtension {
    
    @Override
    public String getSkillId() {
        return "my-skill";
    }
    
    @Override
    public String getCommand() {
        return "my-command";
    }
    
    @Override
    public CliResult execute(String[] args, SceneContext context) {
        try {
            // 执行业务逻辑
            Object result = doSomething(args);
            return CliResult.success("Success", result);
        } catch (Exception e) {
            return CliResult.error("Failed: " + e.getMessage());
        }
    }
}
```

### 2. 使用 Builder 模式

```java
// 非 Spring 环境
OoderCli cli = OoderCli.builder()
    .skillRegistry(skillRegistry)
    .skillInstaller(skillInstaller)
    .skillInvoker(skillInvoker)
    .sceneGroupManager(sceneGroupManager)
    .build();

CliExecutionResult result = cli.execute(new String[]{"skill", "list"});
```

### 3. Spring Boot 集成

```java
// 添加依赖
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-cli-starter</artifactId>
    <version>3.1.0</version>
</dependency>

// 配置
ooder:
  cli:
    enabled: true
    interactive: false
    output-format: text
    
// 注入使用
@Service
public class MyService {
    @Autowired
    private OoderCli cli;
    
    public void execute() {
        CliExecutionResult result = cli.execute(
            new String[]{"skill", "exec", "my-skill", "my-command"}
        );
    }
}
```

## 文件位置汇总

### 核心实现文件

```
e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\src\main\java\net\ooder\sdk\cli\
├── api\
│   ├── SkillCliExtension.java          # 统一扩展接口
│   ├── CliErrorCode.java               # 错误码枚举
│   ├── CliException.java               # 异常类
│   └── ...
├── config\
│   ├── SkillCliConfiguration.java      # 配置类
│   └── SkillCliConfigLoader.java       # 配置加载器
├── core\registry\
│   └── SkillExtensionRegistry.java     # 扩展注册表
├── adapter\
│   ├── SkillCliExtensionAdapter.java   # 新版→旧版适配器
│   └── LegacyCliExtensionAdapter.java  # 旧版→新版适配器
├── command\skill\
│   └── SkillExecCommand.java           # 统一命令执行
├── OoderCliBuilder.java                # Builder 模式
└── CliExecutionResult.java             # 执行结果
```

### Starter 模块

```
e:\github\ooder-sdk\agent-sdk\agent-sdk-cli-starter\
├── pom.xml
└── src\main\
    ├── java\net\ooder\sdk\cli\starter\
    │   ├── OoderCliAutoConfiguration.java    # 自动配置
    │   └── OoderCliProperties.java           # 配置属性
    └── resources\META-INF\
        └── spring.factories                  # SPI 配置
```

### 文档文件

```
e:\github\ooder-sdk\agent-sdk\agent-sdk-cli\
├── MIGRATION-GUIDE.md                    # 迁移指南
├── DESIGN-REVIEW-REPORT.md               # 设计审查报告
├── INTEGRATION-GUIDE.md                  # 集成指南
└── docs\
    ├── APPLICATION-LAYER-DESIGN.md       # 应用层设计
    ├── DRIVER-LAYER-INTERFACE.md         # 驱动层接口
    ├── VISUALIZATION-DESIGN.md           # 可视化设计
    └── DESIGN-SUMMARY.md                 # 本文档
```

## 后续工作建议

### Phase 4: 驱动层实现 (P1)

1. **SkillsFrameworkDriverImpl** - 基于 Skills 框架的驱动实现
2. **AgentSDKDriverImpl** - 基于 Agent SDK 的驱动实现
3. **MockDriverImpl** - 用于测试的模拟驱动

### Phase 5: 可视化实现 (P2)

1. **CLI.SkillPanel** - Skill 管理面板实现
2. **CLI.SceneCanvas** - 场景拓扑图实现
3. **CLI.TaskMonitor** - 任务监控面板实现
4. **CLI.Dashboard** - 主仪表盘实现

### Phase 6: 测试与优化 (P2)

1. 单元测试覆盖
2. 集成测试
3. 性能优化
4. 文档完善

---

**文档版本**: 3.1.0  
**最后更新**: 2026-04-16  
**维护团队**: Agent SDK Team
