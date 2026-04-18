package net.ooder.sdk.cli.command.llm;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;

import java.util.Arrays;
import java.util.List;

public class LlmGenerateCommand implements CliCommand {

    @Override
    public String getName() {
        return "llm:generate";
    }

    @Override
    public String getDescription() {
        return "使用LLM生成文本";
    }

    @Override
    public String getUsage() {
        return "llm:generate --prompt <prompt> [--model <model>] [--max-tokens <n>]";
    }

    @Override
    public String getCategory() {
        return "llm";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"llm:gen", "llm:text"};
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[]{"llm:generate"};
    }

    @Override
    public List<ParamDefinition> getParameters() {
        return Arrays.asList(
            ParamDefinition.required("prompt", "输入提示文本"),
            ParamDefinition.optional("model", "使用的模型", "default"),
            ParamDefinition.optional("max-tokens", "最大Token数", "2000"),
            ParamDefinition.optional("temperature", "温度参数", "0.7")
        );
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String prompt = context.getParameter("prompt");
        if (prompt == null || prompt.isBlank()) {
            List<String> positionalArgs = context.getPositionalArgs();
            if (!positionalArgs.isEmpty()) {
                prompt = String.join(" ", positionalArgs);
            }
        }

        if (prompt == null || prompt.isBlank()) {
            return CommandResult.invalidArgs("请提供输入提示文本 (--prompt)");
        }

        String model = context.getOption("model", "default");
        String maxTokens = context.getOption("max-tokens", "2000");

        return CommandResult.success("LLM生成结果",
            java.util.Map.of("prompt", prompt, "model", model, "maxTokens", maxTokens));
    }

    @Override
    public boolean validate(String[] args) {
        return args != null && args.length > 0;
    }
}
