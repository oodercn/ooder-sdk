package net.ooder.sdk.cli;

public class InitCommand implements CliCommand {
    
    private String path = ".";
    private String template = "default";
    
    public InitCommand(String[] args) {
        parseArgs(args);
    }
    
    private void parseArgs(String[] args) {
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--path")) {
                path = args[i + 1];
                i++;
            } else if (args[i].equals("--template")) {
                template = args[i + 1];
                i++;
            }
        }
    }
    
    @Override
    public void execute() {
        System.out.println("Initializing new scene project...");
        System.out.println("Path: " + path);
        System.out.println("Template: " + template);
        // 这里应该实现初始化逻辑
        System.out.println("Scene project initialized successfully!");
    }
}