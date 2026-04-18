package net.ooder.sdk.cli.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public interface CliCommand extends Callable<Integer> {

    String getName();

    String getDescription();

    String getUsage();

    CommandResult execute(CommandContext context);

    default boolean isInteractive() {
        return false;
    }

    default String getCategory() {
        return "general";
    }

    default String[] getAliases() {
        return new String[0];
    }

    default boolean validate(String[] args) {
        return true;
    }

    default List<CliCommand> getSubCommands() {
        return Collections.emptyList();
    }

    default String[] getRequiredPermissions() {
        return new String[0];
    }

    default List<ParamDefinition> getParameters() {
        return Collections.emptyList();
    }

    @Override
    default Integer call() throws Exception {
        CommandContext context = new CommandContext();
        CommandResult result = execute(context);
        return result.getExitCode();
    }

    class ParamDefinition {
        private final String name;
        private final String description;
        private final boolean required;
        private final String defaultValue;
        private final String type;

        public ParamDefinition(String name, String description, boolean required, String defaultValue, String type) {
            this.name = name;
            this.description = description;
            this.required = required;
            this.defaultValue = defaultValue;
            this.type = type;
        }

        public static ParamDefinition required(String name, String description) {
            return new ParamDefinition(name, description, true, null, "string");
        }

        public static ParamDefinition required(String name, String description, String type) {
            return new ParamDefinition(name, description, true, null, type);
        }

        public static ParamDefinition optional(String name, String description, String defaultValue) {
            return new ParamDefinition(name, description, false, defaultValue, "string");
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isRequired() { return required; }
        public String getDefaultValue() { return defaultValue; }
        public String getType() { return type; }
    }
}
