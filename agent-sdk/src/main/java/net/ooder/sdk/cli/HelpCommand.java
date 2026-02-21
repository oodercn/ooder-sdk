package net.ooder.sdk.cli;

public class HelpCommand implements CliCommand {
    
    @Override
    public void execute() {
        System.out.println("Scene CLI Tool");
        System.out.println("================");
        System.out.println("Commands:");
        System.out.println("  init      - Initialize a new scene project");
        System.out.println("  generate  - Generate code from interface definition");
        System.out.println("  validate  - Validate scene package");
        System.out.println("  test      - Run tests for scene");
        System.out.println("  package   - Package scene into distributable format");
        System.out.println("  publish   - Publish scene to registry");
        System.out.println("  install   - Install scene locally");
        System.out.println("  report    - Generate validation report");
        System.out.println("  docs      - Generate documentation");
        System.out.println("  help      - Show this help message");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  scene-cli <command> [options]");
    }
}