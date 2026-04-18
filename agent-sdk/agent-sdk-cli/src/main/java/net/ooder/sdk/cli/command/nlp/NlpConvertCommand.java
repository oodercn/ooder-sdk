package net.ooder.sdk.cli.command.nlp;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;

import java.util.Arrays;
import java.util.List;

public class NlpConvertCommand implements CliCommand {

    @Override
    public String getName() {
        return "nlp:convert";
    }

    @Override
    public String getDescription() {
        return "将自然语言转换为CLI命令";
    }

    @Override
    public String getUsage() {
        return "nlp:convert <natural-language-text>";
    }

    @Override
    public String getCategory() {
        return "nlp";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nlp:parse", "nlp:translate"};
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[]{"nlp:convert"};
    }

    @Override
    public List<ParamDefinition> getParameters() {
        return Arrays.asList(
            ParamDefinition.required("text", "自然语言文本"),
            ParamDefinition.optional("dry-run", "仅显示不执行", "false")
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
            return CommandResult.invalidArgs("请提供要转换的自然语言文本");
        }

        return CommandResult.success("NLP转换结果", 
            java.util.Map.of("input", text, "status", "converted"));
    }

    @Override
    public boolean validate(String[] args) {
        return args != null && args.length > 0;
    }
}
