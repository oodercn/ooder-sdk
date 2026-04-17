# Agent SDK CLI 迁移指南

## 概述

本指南帮助您从旧版 CLI 接口迁移到新的统一接口。新的接口与 Skills 框架完全兼容，提供了更简洁的 API 和更灵活的配置方式。

## 主要变更

### 1. 扩展接口变更

#### 旧接口 (已废弃)
```java
// 旧版 CliExtension 接口
public interface CliExtension {
    String getId();
    String getName();
    String getVersion();
    List<CliCommand> getCommands();
    void initialize();
    void destroy();
}
```

#### 新接口 (推荐)
```java
// 新版 SkillCliExtension 接口
public interface SkillCliExtension {
    String getSkillId();           // 改为 getSkillId
    String getCommand();           // 改为 getCommand (单数)
    String getDescription();
    CliResult execute(String[] args, SceneContext context);  // 直接执行
    void initialize();
    void destroy();
}
```

### 2. 配置方式变更

#### 旧配置 (extension.properties)
```properties
# 旧版配置方式
extension.class=com.example.MySkillCliExtension
extension.id=my-skill-cli
extension.name=My Skill CLI
```

#### 新配置 (skill.yaml)
```yaml
# 新版配置方式 - 统一使用 skill.yaml
skill:
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
    output:
      format: text
      colorEnabled: true
```

### 3. 命令格式变更

#### 旧命令格式
```bash
# 旧版命令格式
ooder skill:exec --skill-id rag-skill --capability reindex
```

#### 新命令格式 (统一规范)
```bash
# 新版命令格式 - 与 Skills 框架统一
skill exec rag-skill reindex
```

## 迁移步骤

### 步骤 1: 更新接口实现

将您的扩展类从实现 `CliExtension` 改为实现 `SkillCliExtension`：

```java
// 迁移前
public class MyOldExtension implements CliExtension {
    @Override
    public String getId() { return "my-skill"; }
    
    @Override
    public String getName() { return "My Skill"; }
    
    @Override
    public List<CliCommand> getCommands() {
        return Arrays.asList(new MyCommand());
    }
    
    // ...
}

// 迁移后
public class MyNewExtension implements SkillCliExtension {
    @Override
    public String getSkillId() { return "my-skill"; }  // 注意方法名变化
    
    @Override
    public String getCommand() { return "my-command"; }  // 返回命令名
    
    @Override
    public String getDescription() { return "My command description"; }
    
    @Override
    public CliResult execute(String[] args, SceneContext context) {
        // 直接实现执行逻辑
        try {
            // 执行命令...
            return CliResult.success("Command executed successfully");
        } catch (Exception e) {
            return CliResult.error("Execution failed: " + e.getMessage());
        }
    }
    
    // ...
}
```

### 步骤 2: 更新配置文件

将 `extension.properties` 迁移到 `skill.yaml`：

```yaml
# skill.yaml
skill:
  cli:
    extensions:
      - skillId: my-skill
        command: my-command
        handler: com.example.MyNewExtension
        description: "My command description"
        enabled: true
```

### 步骤 3: 更新命令调用

如果您在代码中调用 CLI 命令，更新调用方式：

```java
// 迁移前
String[] args = {"skill:exec", "--skill-id", "rag-skill", "--capability", "reindex"};
int exitCode = cli.run(args);

// 迁移后 - 使用统一命令格式
String[] args = {"skill", "exec", "rag-skill", "reindex"};
int exitCode = cli.run(args);

// 或使用 Builder 模式创建 CLI
OoderCli cli = OoderCli.builder()
    .skillRegistry(skillRegistry)
    .skillInvoker(skillInvoker)
    .build();

// 嵌入式使用 - 获取结构化结果
CliExecutionResult result = cli.execute(args);
if (result.isSuccess()) {
    System.out.println(result.getMessage());
    System.out.println(result.getData());
}
```

### 步骤 4: 更新错误处理

使用新的错误码体系：

```java
// 迁移前
return CommandResult.error("Skill not found: " + skillId);

// 迁移后 - 使用统一错误码
return CommandResult.error(CliErrorCode.SKILL_NOT_FOUND, skillId);

// 或在抛出异常时使用
throw new CliException(CliErrorCode.SKILL_NOT_FOUND, skillId);
```

## 向后兼容

### 适配器模式

如果您暂时无法迁移，可以使用适配器保持兼容：

```java
// 将旧版扩展包装为新版
ExtensionRegistry.CliExtension oldExtension = ...;
SkillCliExtension adapted = new LegacyCliExtensionAdapter(oldExtension);

// 注册到新的注册表
SkillExtensionRegistry registry = new SkillExtensionRegistry();
registry.register(adapted);
```

### 混合使用

新旧接口可以同时使用：

```java
OoderCli cli = new OoderCli();

// 设置旧版依赖
cli.setSkillRegistry(skillRegistry);
cli.setSkillInstaller(skillInstaller);

// 同时设置新版扩展注册表
SkillExtensionRegistry newRegistry = new SkillExtensionRegistry();
newRegistry.register(new MyNewExtension());
cli.setExtensionRegistry(newRegistry);

cli.initializeAdapters();
```

## Spring Boot 集成

### 添加 Starter 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-cli-starter</artifactId>
    <version>3.1.0</version>
</dependency>
```

### 配置属性

```yaml
# application.yaml
ooder:
  cli:
    enabled: true
    interactive: false
    output-format: text
    color-enabled: true
    security:
      enabled: true
      audit-enabled: true
    extensions:
      - skillId: my-skill
        command: my-command
        handler: com.example.MyExtension
```

### 注入使用

```java
@Service
public class MyService {
    @Autowired
    private OoderCli cli;
    
    public void executeCommand() {
        String[] args = {"skill", "exec", "my-skill", "my-command"};
        CliExecutionResult result = cli.execute(args);
        // 处理结果...
    }
}
```

## 非 Spring 环境使用

### 使用 Builder 模式

```java
// 创建 CLI 实例
OoderCli cli = OoderCli.builder()
    .skillRegistry(skillRegistry)
    .skillInstaller(skillInstaller)
    .skillInvoker(skillInvoker)
    .sceneGroupManager(sceneGroupManager)
    .build();

// 执行命令
CliExecutionResult result = cli.execute(new String[]{"skill", "list"});
```

## 错误码参考

| 错误码 | 说明 | 使用场景 |
|--------|------|----------|
| CLI-000 | 成功 | 命令执行成功 |
| CLI-002 | 无效参数 | 参数验证失败 |
| CLI-100 | Skill 未找到 | 指定的 Skill 不存在 |
| CLI-103 | Skill 执行失败 | Skill 执行过程中出错 |
| CLI-200 | 场景未找到 | 指定的场景不存在 |
| CLI-400 | 权限拒绝 | 没有执行权限 |
| CLI-403 | 注入检测 | 检测到潜在注入攻击 |

## 常见问题

### Q: 旧版扩展还能继续使用吗？
A: 可以，使用 `LegacyCliExtensionAdapter` 进行适配。

### Q: 配置文件必须改为 YAML 吗？
A: 推荐改为 YAML 以获得完整功能支持，但旧版配置加载器仍然可用。

### Q: 命令格式必须改为新的吗？
A: 推荐统一使用 `skill exec <id> <cmd>` 格式，但旧版命令仍然支持。

### Q: 如何同时支持新旧接口？
A: 使用混合模式，同时设置新旧依赖即可。

## 完整示例

### 新版扩展实现

```java
@Component
public class ReindexCommand implements SkillCliExtension {
    
    @Autowired
    private SearchService searchService;
    
    @Override
    public String getSkillId() {
        return "search-skill";
    }
    
    @Override
    public String getCommand() {
        return "reindex";
    }
    
    @Override
    public String getDescription() {
        return "Rebuild search index";
    }
    
    @Override
    public CliResult execute(String[] args, SceneContext context) {
        try {
            String indexName = args.length > 0 ? args[0] : "default";
            searchService.rebuildIndex(indexName);
            return CliResult.success("Index rebuilt successfully: " + indexName);
        } catch (Exception e) {
            return CliResult.error("Failed to rebuild index: " + e.getMessage());
        }
    }
}
```

### 调用示例

```java
// 命令行调用
$ skill exec search-skill reindex products

// 程序化调用
CliExecutionResult result = cli.execute(
    new String[]{"skill", "exec", "search-skill", "reindex", "products"}
);
```

## 支持

如有问题，请联系：
- 技术支持: dev@ooder.cn
- 文档: https://docs.ooder.cn
- 问题反馈: https://github.com/oodercn/ooder-sdk/issues
