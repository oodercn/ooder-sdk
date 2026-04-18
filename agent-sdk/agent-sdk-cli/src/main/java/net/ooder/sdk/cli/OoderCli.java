package net.ooder.sdk.cli;

// 明确导入api包下的接口，避免与同包下的类冲突
import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CliFormatter;
import net.ooder.sdk.cli.api.CliParser;
import net.ooder.sdk.cli.api.CliRouter;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.api.ExtensionRegistry;
import net.ooder.sdk.cli.api.InteractiveCli;

import net.ooder.sdk.cli.adapter.CliCommandAdapter;
import net.ooder.sdk.cli.adapter.SceneManagerAdapter;
import net.ooder.sdk.cli.adapter.TaskStatusMonitor;
import net.ooder.sdk.cli.command.scene.SceneCreateCommand;
import net.ooder.sdk.cli.command.scene.SceneEventCommand;
import net.ooder.sdk.cli.command.scene.SceneInfoCommand;
import net.ooder.sdk.cli.command.scene.SceneInvokeCommand;
import net.ooder.sdk.cli.command.scene.SceneListCommand;
import net.ooder.sdk.cli.command.skill.*;
import net.ooder.sdk.cli.command.nlp.*;
import net.ooder.sdk.cli.command.llm.*;
import net.ooder.sdk.cli.command.system.StatusCommand;
import net.ooder.sdk.cli.command.task.TaskListCommand;
import net.ooder.sdk.cli.command.task.TaskStatusCommand;
import net.ooder.sdk.cli.core.formatter.JsonFormatter;
import net.ooder.sdk.cli.core.formatter.TableFormatter;
import net.ooder.sdk.cli.core.formatter.TextFormatter;
import net.ooder.sdk.cli.core.interactive.CompletionEngine;
import net.ooder.sdk.cli.core.interactive.JLineCli;
import net.ooder.sdk.cli.core.parser.PicocliParser;
import net.ooder.sdk.cli.core.registry.ExtensionRegistryImpl;
import net.ooder.sdk.cli.core.registry.SkillExtensionRegistry;
import net.ooder.sdk.cli.core.router.DefaultCliRouter;
import net.ooder.sdk.cli.security.CommandAuditor;
import net.ooder.sdk.cli.security.SecureCommandProxy;
import net.ooder.skills.api.CollaborativeSceneGroupManager;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillInvoker;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillService;
import net.ooder.skills.api.security.PermissionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Ooder CLI 主入口
 *
 * <p>支持 Builder 模式创建实例:</p>
 * <pre>
 * OoderCli cli = OoderCli.builder()
 *     .skillRegistry(skillRegistry)
 *     .skillInvoker(skillInvoker)
 *     .build();
 * </pre>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class OoderCli {

    private static final Logger log = LoggerFactory.getLogger(OoderCli.class);

    private CliRouter router;
    private final CliParser parser;
    private ExtensionRegistry extensionRegistry;
    private SkillExtensionRegistry skillExtensionRegistry;
    private final Map<String, CliFormatter> formatters;
    private final InteractiveCli interactiveCli;

    // SDK依赖
    private SkillRegistry skillRegistry;
    private SkillInstaller skillInstaller;
    private SkillInvoker skillInvoker;
    private CollaborativeSceneGroupManager sceneGroupManager;
    private SkillService skillService;
    private PermissionEngine permissionEngine;

    // 适配器
    private CliCommandAdapter commandAdapter;
    private TaskStatusMonitor taskMonitor;
    private SceneManagerAdapter sceneManagerAdapter;

    // 安全
    private CommandAuditor auditor;
    private Set<String> commandWhitelist;

    public OoderCli() {
        this.router = new DefaultCliRouter();
        this.parser = new PicocliParser();
        this.extensionRegistry = new ExtensionRegistryImpl();
        this.skillExtensionRegistry = new SkillExtensionRegistry();
        this.formatters = new HashMap<>();
        this.interactiveCli = new JLineCli();

        // 注册格式化器
        formatters.put("text", new TextFormatter());
        formatters.put("json", new JsonFormatter());
        formatters.put("table", new TableFormatter());

        // 初始化审计
        this.auditor = new CommandAuditor();
        this.commandWhitelist = new HashSet<>();

        // 注册内置命令
        registerBuiltinCommands();
    }

    /**
     * 创建 Builder 实例
     */
    public static OoderCliBuilder builder() {
        return new OoderCliBuilder();
    }

    /**
     * 设置SkillRegistry
     */
    public void setSkillRegistry(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }

    /**
     * 设置SkillInstaller
     */
    public void setSkillInstaller(SkillInstaller skillInstaller) {
        this.skillInstaller = skillInstaller;
    }

    /**
     * 设置SkillInvoker
     */
    public void setSkillInvoker(SkillInvoker skillInvoker) {
        this.skillInvoker = skillInvoker;
    }

    /**
     * 设置场景组管理器
     */
    public void setSceneGroupManager(CollaborativeSceneGroupManager sceneGroupManager) {
        this.sceneGroupManager = sceneGroupManager;
    }

    /**
     * 设置SkillService
     */
    public void setSkillService(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * 设置权限引擎
     */
    public void setPermissionEngine(PermissionEngine permissionEngine) {
        this.permissionEngine = permissionEngine;
    }

    /**
     * 设置ExtensionRegistry
     */
    public void setExtensionRegistry(SkillExtensionRegistry extensionRegistry) {
        this.skillExtensionRegistry = extensionRegistry;
    }

    /**
     * 设置CliRouter
     */
    public void setCliRouter(CliRouter cliRouter) {
        this.router = cliRouter;
    }

    /**
     * 初始化适配器（在所有依赖设置后调用）
     */
    public void initializeAdapters() {
        if (skillInvoker != null && skillRegistry != null) {
            this.commandAdapter = new CliCommandAdapter(skillInvoker, skillRegistry);
        }

        if (sceneGroupManager != null) {
            this.sceneManagerAdapter = new SceneManagerAdapter(sceneGroupManager, skillInvoker);
        }

        if (skillRegistry != null) {
            this.taskMonitor = new TaskStatusMonitor();
        }

        // 注册所有命令
        registerAllCommands();
    }

    /**
     * 设置命令白名单
     */
    public void setCommandWhitelist(Set<String> whitelist) {
        this.commandWhitelist = whitelist != null ? whitelist : new HashSet<>();
    }

    public static void main(String[] args) {
        OoderCli cli = new OoderCli();

        // 注意：生产环境需要通过Spring或手动注入依赖
        // cli.setSkillRegistry(...);
        // cli.setSkillInstaller(...);
        // cli.setSkillInvoker(...);
        // cli.setSceneGroupManager(...);
        // cli.initializeAdapters();

        int exitCode = cli.run(args);
        System.exit(exitCode);
    }

    public int run(String[] args) {
        if (args.length == 0) {
            // 进入交互式模式
            return runInteractive();
        }

        // 解析命令
        CliParser.ParseResult parseResult = parser.parse(args);

        if (parseResult.hasError()) {
            System.err.println("Error: " + parseResult.getErrorMessage());
            return CommandResult.INVALID_ARGS;
        }

        if (parseResult.isHelpRequested()) {
            parser.printGlobalHelp();
            return 0;
        }

        String commandName = parseResult.getCommand();
        if (commandName == null) {
            printHelp();
            return 0;
        }

        // 构建命令上下文
        CommandContext context = buildCommandContext(parseResult);

        // 执行命令
        CommandResult result = router.execute(commandName, context);

        // 格式化输出
        printResult(result, context);

        return result.getExitCode();
    }

    /**
     * 执行命令并返回结构化结果（用于嵌入式使用）
     */
    public CliExecutionResult execute(String[] args) {
        CliParser.ParseResult parseResult = parser.parse(args);

        if (parseResult.hasError()) {
            return CliExecutionResult.error(
                CommandResult.INVALID_ARGS,
                parseResult.getErrorMessage()
            );
        }

        String commandName = parseResult.getCommand();
        if (commandName == null) {
            return CliExecutionResult.error(
                CommandResult.INVALID_ARGS,
                "No command specified"
            );
        }

        CommandContext context = buildCommandContext(parseResult);
        CommandResult result = router.execute(commandName, context);

        return new CliExecutionResult(
            result.getExitCode(),
            result.getMessage(),
            result.getData(),
            result.getErrorCode()
        );
    }

    /**
     * 运行交互式模式
     */
    private int runInteractive() {
        interactiveCli.setCompleter(new CompletionEngine(router));
        interactiveCli.start();

        println("Ooder Agent SDK CLI v3.1.0");
        println("Type 'help' for available commands or 'exit' to quit.");
        println("");

        while (interactiveCli.isRunning()) {
            String line = interactiveCli.readLine("ooder> ");

            if (line == null) {
                break;
            }

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            if ("exit".equalsIgnoreCase(line) || "quit".equalsIgnoreCase(line)) {
                break;
            }

            if ("help".equalsIgnoreCase(line)) {
                printHelp();
                continue;
            }

            // 解析并执行命令
            String[] args = line.split("\\s+");
            CliParser.ParseResult parseResult = parser.parse(args);

            if (parseResult.hasError()) {
                println("Error: " + parseResult.getErrorMessage());
                continue;
            }

            String commandName = parseResult.getCommand();
            if (commandName == null) {
                println("Unknown command. Type 'help' for available commands.");
                continue;
            }

            CommandContext context = buildCommandContext(parseResult);
            CommandResult result = router.execute(commandName, context);
            printResult(result, context);
        }

        interactiveCli.stop();
        return 0;
    }

    private CommandContext buildCommandContext(CliParser.ParseResult parseResult) {
        CommandContext context = new CommandContext();
        context.setOutputFormat(parseResult.getOptions().getOrDefault("output", "text"));
        context.setVerbose(Boolean.parseBoolean(parseResult.getOptions().getOrDefault("verbose", "false")));
        context.setQuiet(Boolean.parseBoolean(parseResult.getOptions().getOrDefault("quiet", "false")));

        // 设置所有选项作为属性
        parseResult.getOptions().forEach(context::setAttribute);

        // 设置位置参数
        List<String> positionalArgs = parseResult.getPositionalArgs();
        for (int i = 0; i < positionalArgs.size(); i++) {
            context.setAttribute("arg" + i, positionalArgs.get(i));
        }

        return context;
    }

    private void printResult(CommandResult result, CommandContext context) {
        if (context.isQuiet()) {
            return;
        }

        CliFormatter formatter = formatters.getOrDefault(context.getOutputFormat(), formatters.get("text"));

        if (result.isSuccess()) {
            println(formatter.formatSuccess(result.getMessage()));
            if (result.getData() != null) {
                println(formatter.format(result.getData()));
            }
        } else {
            println(formatter.formatError(result.getMessage()));
        }
    }

    private void println(String message) {
        if (interactiveCli.isRunning()) {
            interactiveCli.println(message);
        } else {
            System.out.println(message);
        }
    }

    private void registerBuiltinCommands() {
        // 注册系统命令
        router.register(new StatusCommand());

        log.debug("Registered {} built-in commands", router.getAllCommands().size());
    }

    private void registerAllCommands() {
        registerSkillCommands();
        registerSceneCommands();
        registerTaskCommands();
        registerNlpCommands();
        registerLlmCommands();
    }

    private void registerSkillCommands() {
        if (skillRegistry == null) {
            log.warn("SkillRegistry not available, skipping skill command registration");
            return;
        }

        // 注册Skill查询命令
        registerSecureCommand(new SkillListCommand(skillRegistry));
        registerSecureCommand(new SkillInfoCommand(skillRegistry));

        // 注册Skill生命周期命令
        registerSecureCommand(new SkillStartCommand(skillRegistry));
        registerSecureCommand(new SkillStopCommand(skillRegistry));

        // 注册Skill安装命令（需要SkillInstaller）
        if (skillInstaller != null) {
            registerSecureCommand(new SkillInstallCommand(skillInstaller));
            registerSecureCommand(new SkillUninstallCommand(skillInstaller, skillRegistry));
            registerSecureCommand(new SkillUpdateCommand(skillInstaller, skillRegistry));
        } else {
            log.warn("SkillInstaller not available, skipping install/uninstall/update commands");
        }

        // 注册统一命令规范的 skill exec 命令
        if (skillExtensionRegistry != null) {
            registerSecureCommand(new SkillExecCommand(skillExtensionRegistry));
        }

        log.debug("Registered skill commands");
    }

    private void registerSceneCommands() {
        if (sceneManagerAdapter == null) {
            log.warn("SceneManagerAdapter not available, skipping scene command registration");
            return;
        }

        // 注册Scene命令
        registerSecureCommand(new SceneListCommand(sceneManagerAdapter));
        registerSecureCommand(new SceneCreateCommand(sceneManagerAdapter));
        registerSecureCommand(new SceneInfoCommand(sceneManagerAdapter));
        registerSecureCommand(new SceneInvokeCommand(sceneManagerAdapter));
        registerSecureCommand(new SceneEventCommand(sceneManagerAdapter));

        log.debug("Registered scene commands");
    }

    private void registerTaskCommands() {
        if (taskMonitor == null) {
            log.warn("TaskStatusMonitor not available, skipping task command registration");
            return;
        }

        // 注册Task命令
        registerSecureCommand(new TaskStatusCommand(taskMonitor));
        registerSecureCommand(new TaskListCommand(taskMonitor));

        log.debug("Registered task commands");
    }

    private void registerNlpCommands() {
        registerSecureCommand(new NlpConvertCommand());
        registerSecureCommand(new NlpSkillsCommand());
        registerSecureCommand(new NlpExecuteCommand());
        log.debug("Registered NLP commands");
    }

    private void registerLlmCommands() {
        registerSecureCommand(new LlmGenerateCommand());
        registerSecureCommand(new LlmIntentCommand());
        registerSecureCommand(new LlmChatCommand());
        log.debug("Registered LLM commands");
    }

    private void registerSecureCommand(CliCommand command) {
        if (permissionEngine != null || !commandWhitelist.isEmpty()) {
            SecureCommandProxy secureCommand = new SecureCommandProxy(
                    command, permissionEngine, auditor, commandWhitelist);
            router.register(secureCommand);
        } else {
            router.register(command);
        }
    }

    private void printHelp() {
        println("Ooder Agent SDK CLI v3.1.0");
        println("");
        println("Usage: ooder <command> [options]");
        println("");
        println("Commands:");
        println("  skill:list       List all installed skills");
        println("  skill:info       Show skill information");
        println("  skill:install    Install a skill");
        println("  skill:uninstall  Uninstall a skill");
        println("  skill:update     Update a skill");
        println("  skill:start      Start a skill");
        println("  skill:stop       Stop a skill");
        println("  skill exec       Execute a skill command (unified format)");
        println("  scene:list       List all scenes");
        println("  scene:create     Create a new scene group");
        println("  scene:info       Show scene group information");
        println("  scene:invoke     Invoke a capability in a scene");
        println("  scene:event      Publish an event to a scene");
        println("  task:list        List all tasks");
        println("  task:status      Query task status");
        println("  status           Show system status");
        println("");
        println("Global Options:");
        println("  -v, --verbose    Enable verbose output");
        println("  -q, --quiet      Suppress output");
        println("  -o, --output     Output format: text, json, table");
        println("  -h, --help       Show help");
        println("  --version        Show version");
    }

    // Getters
    public CliRouter getRouter() { return router; }
    public CliParser getParser() { return parser; }
    public ExtensionRegistry getExtensionRegistry() { return extensionRegistry; }
    public SkillExtensionRegistry getSkillExtensionRegistry() { return skillExtensionRegistry; }
    public InteractiveCli getInteractiveCli() { return interactiveCli; }
    public CliCommandAdapter getCommandAdapter() { return commandAdapter; }
    public TaskStatusMonitor getTaskMonitor() { return taskMonitor; }
    public SceneManagerAdapter getSceneManagerAdapter() { return sceneManagerAdapter; }
}
