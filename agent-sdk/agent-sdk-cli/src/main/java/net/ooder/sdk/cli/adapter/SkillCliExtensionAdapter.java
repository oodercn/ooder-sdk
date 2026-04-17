package net.ooder.sdk.cli.adapter;

import net.ooder.sdk.cli.api.*;

import java.util.HashMap;
import java.util.Map;

/**
 * SkillCliExtension 到 CliCommand 的适配器
 *
 * <p>将 Skills 框架的 SkillCliExtension 接口适配为 Agent SDK CLI 的 CliCommand 接口</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillCliExtensionAdapter implements CliCommand {

    private final SkillCliExtension extension;

    public SkillCliExtensionAdapter(SkillCliExtension extension) {
        this.extension = extension;
    }

    @Override
    public String getName() {
        return extension.getCommand();
    }

    @Override
    public String getDescription() {
        return extension.getDescription();
    }

    @Override
    public String getUsage() {
        return extension.getUsage();
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String[] args = context.getArgs();
        Map<String, Object> sceneContext = extractSceneContext(context);

        try {
            SkillCliExtension.CliResult result = extension.execute(args, sceneContext);
            return adaptResult(result);
        } catch (Exception e) {
            return CommandResult.error("Command execution failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isInteractive() {
        return extension.isInteractive();
    }

    @Override
    public String getCategory() {
        return extension.getCategory();
    }

    @Override
    public String[] getAliases() {
        return extension.getAliases();
    }

    @Override
    public boolean validate(String[] args) {
        return extension.validate(args);
    }

    /**
     * 获取原始扩展
     */
    public SkillCliExtension getExtension() {
        return extension;
    }

    /**
     * 获取 Skill ID
     */
    public String getSkillId() {
        return extension.getSkillId();
    }

    /**
     * 初始化扩展
     */
    public void initialize() {
        extension.initialize();
    }

    /**
     * 销毁扩展
     */
    public void destroy() {
        extension.destroy();
    }

    /**
     * 从 CommandContext 提取场景上下文
     */
    private Map<String, Object> extractSceneContext(CommandContext context) {
        Map<String, Object> sceneContext = new HashMap<>();
        sceneContext.put("sceneId", context.getString("sceneId", ""));
        sceneContext.put("sessionId", context.getString("sessionId", ""));
        return sceneContext;
    }

    /**
     * 适配执行结果
     */
    private CommandResult adaptResult(SkillCliExtension.CliResult result) {
        if (result.isSuccess()) {
            if (result.getData() != null) {
                return CommandResult.success(result.getMessage(), result.getData());
            }
            return CommandResult.success(result.getMessage());
        } else {
            return CommandResult.error(result.getMessage(), result.getExitCode());
        }
    }
}
