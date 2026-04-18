package net.ooder.sdk.cli.command.llm;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;

import java.util.Arrays;
import java.util.List;

public class LlmIntentCommand implements CliCommand {

    @Override
    public String getName() {
        return "llm:intent";
    }

    @Override
    public String getDescription() {
        return "使用LLM识别用户意图";
    }

    @Override
    public String getUsage() {
        return "llm:intent --text <text> [--context <context>]";
    }

    @Override
    public String getCategory() {
        return "llm";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"llm:recognize", "llm:understand"};
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[]{"llm:intent"};
    }

    @Override
    public List<ParamDefinition> getParameters() {
        return Arrays.asList(
            ParamDefinition.required("text", "要识别的文本"),
            ParamDefinition.optional("context", "上下文信息", "")
        );
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String text = context.getParameter("text");
        if (text == null || text.isBlank()) {
            List<String> positionalArgs = context.getPositionalArgs();
            if (!positionalArgs.isEmpty()) {
                text = String.join(" ", positionalArgs);
            }
        }

        if (text == null || text.isBlank()) {
            return CommandResult.invalidArgs("请提供要识别意图的文本 (--text)");
        }

        String ctx = context.getOption("context", "");

        return CommandResult.success("LLM意图识别结果",
            java.util.Map.of("text", text, "context", ctx, "status", "recognized"));
    }

    @Override
    public boolean validate(String[] args) {
        return args != null && args.length > 0;
    }
}
