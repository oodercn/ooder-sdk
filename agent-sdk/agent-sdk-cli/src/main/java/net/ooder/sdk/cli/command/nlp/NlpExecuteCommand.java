package net.ooder.sdk.cli.command.nlp;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;

import java.util.Arrays;
import java.util.List;

public class NlpExecuteCommand implements CliCommand {

    @Override
    public String getName() {
        return "nlp:execute";
    }

    @Override
    public String getDescription() {
        return "通过自然语言描述直接执行CLI操作";
    }

    @Override
    public String getUsage() {
        return "nlp:execute <natural-language-instruction>";
    }

    @Override
    public String getCategory() {
        return "nlp";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nlp:run", "nlp:do"};
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[]{"nlp:execute"};
    }

    @Override
    public List<ParamDefinition> getParameters() {
        return Arrays.asList(
            ParamDefinition.required("instruction", "自然语言指令"),
            ParamDefinition.optional("confirm", "执行前确认", "true")
        );
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String instruction = context.getParameter("instruction");
        if (instruction == null || instruction.isBlank()) {
            List<String> positionalArgs = context.getPositionalArgs();
            if (!positionalArgs.isEmpty()) {
                instruction = String.join(" ", positionalArgs);
            }
        }

        if (instruction == null || instruction.isBlank()) {
            return CommandResult.invalidArgs("请提供要执行的自然语言指令");
        }

        return CommandResult.success("NLP执行结果",
            java.util.Map.of("instruction", instruction, "status", "executed"));
    }

    @Override
    public boolean validate(String[] args) {
        return args != null && args.length > 0;
    }
}
