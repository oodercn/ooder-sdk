package net.ooder.sdk.cli;

public class SceneCli {
    
    public static void main(String[] args) {
        CliCommand command = parseCommand(args);
        command.execute();
    }
    
    private static CliCommand parseCommand(String[] args) {
        if (args.length == 0) {
            return new HelpCommand();
        }
        
        switch (args[0]) {
            case "init":
                return new InitCommand(args);
            case "generate":
                return new GenerateCommand(args);
            case "validate":
                return new ValidateCommand(args);
            case "test":
                return new TestCommand(args);
            case "package":
                return new PackageCommand(args);
            case "publish":
                return new PublishCommand(args);
            case "install":
                return new InstallCommand(args);
            case "report":
                return new ReportCommand(args);
            case "docs":
                return new DocsCommand(args);
            default:
                return new HelpCommand();
        }
    }
}