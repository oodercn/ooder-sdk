package net.ooder.sdk.cli.command.llm;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;

import java.util.Arrays;
import java.util.List;

public class LlmChatCommand implements CliCommand {

    @Override
    public String getName() {
        return "llm:chat";
    }

    @Override
    public String getDescription() {
        return "与LLM进行交互式对话";
    }

    @Override
    public String getUsage() {
        return "llm:chat [--model <model>] [--system <system-prompt>]";
    }

    @Override
    public String getCategory() {
        return "llm";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"llm:talk", "llm:conversation"};
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[]{"llm:chat"};
    }

    @Override
    public List<ParamDefinition> getParameters() {
        return Arrays.asList(
            ParamDefinition.optional("model", "使用的模型", "default"),
            ParamDefinition.optional("system", "系统提示词", ""),
            ParamDefinition.optional("history", "历史对话轮数", "10")
        );
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String model = context.getOption("model", "default");
        String system = context.getOption("system", "");
        String history = context.getOption("history", "10");

        return CommandResult.success("LLM对话模式已启动",
            java.util.Map.of("model", model, "system", system, "history", history, "interactive", true));
    }
}
