package net.ooder.sdk.cli;

public class PublishCommand implements CliCommand {
    
    private String path = ".";
    private String registry = "https://registry.ooder.net";
    private String apiKey = "";
    
    public PublishCommand(String[] args) {
        parseArgs(args);
    }
    
    private void parseArgs(String[] args) {
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--path")) {
                path = args[i + 1];
                i++;
            } else if (args[i].equals("--registry")) {
                registry = args[i + 1];
                i++;
            } else if (args[i].equals("--api-key")) {
                apiKey = args[i + 1];
                i++;
            }
        }
    }
    
    @Override
    public void execute() {
        try {
            System.out.println("Publishing scene...");
            System.out.println("Path: " + path);
            System.out.println("Registry: " + registry);
            // 这里应该实现发布逻辑
            System.out.println("Scene published successfully!");
        } catch (Exception e) {
            System.err.println("Publishing failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}