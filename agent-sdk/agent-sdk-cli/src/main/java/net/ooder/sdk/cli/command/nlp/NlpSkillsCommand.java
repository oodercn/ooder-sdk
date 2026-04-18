package net.ooder.sdk.cli.command.nlp;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;

import java.util.Arrays;
import java.util.List;

public class NlpSkillsCommand implements CliCommand {

    @Override
    public String getName() {
        return "nlp:skills";
    }

    @Override
    public String getDescription() {
        return "通过自然语言查询匹配的技能";
    }

    @Override
    public String getUsage() {
        return "nlp:skills <query>";
    }

    @Override
    public String getCategory() {
        return "nlp";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nlp:search", "nlp:find"};
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[]{"nlp:skills"};
    }

    @Override
    public List<ParamDefinition> getParameters() {
        return Arrays.asList(
            ParamDefinition.required("query", "查询关键词或描述"),
            ParamDefinition.optional("limit", "返回数量限制", "5")
        );
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String query = context.getParameter("query");
        if (query == null || query.isBlank()) {
            List<String> positionalArgs = context.getPositionalArgs();
            if (!positionalArgs.isEmpty()) {
                query = String.join(" ", positionalArgs);
            }
        }

        if (query == null || query.isBlank()) {
            return CommandResult.invalidArgs("请提供查询关键词");
        }

        return CommandResult.success("NLP技能查询结果",
            java.util.Map.of("query", query, "status", "searched"));
    }

    @Override
    public boolean validate(String[] args) {
        return args != null && args.length > 0;
    }
}
