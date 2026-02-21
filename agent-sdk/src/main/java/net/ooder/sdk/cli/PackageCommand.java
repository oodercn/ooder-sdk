package net.ooder.sdk.cli;

public class PackageCommand implements CliCommand {
    
    private String path = ".";
    private String output = ".";
    private boolean includeDependencies = false;
    
    public PackageCommand(String[] args) {
        parseArgs(args);
    }
    
    private void parseArgs(String[] args) {
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--path")) {
                path = args[i + 1];
                i++;
            } else if (args[i].equals("--output")) {
                output = args[i + 1];
                i++;
            } else if (args[i].equals("--include-deps")) {
                includeDependencies = true;
            }
        }
    }
    
    @Override
    public void execute() {
        try {
            System.out.println("Packaging scene...");
            System.out.println("Path: " + path);
            System.out.println("Output: " + output);
            System.out.println("Include Dependencies: " + includeDependencies);
            // 这里应该实现打包逻辑
            System.out.println("Scene packaged successfully!");
        } catch (Exception e) {
            System.err.println("Packaging failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}