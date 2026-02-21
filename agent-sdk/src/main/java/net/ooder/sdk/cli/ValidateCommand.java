package net.ooder.sdk.cli;

import net.ooder.sdk.validator.SceneValidator;
import net.ooder.sdk.validator.impl.SceneValidatorImpl;
import net.ooder.sdk.validator.ScenePackage;
import net.ooder.sdk.validator.ValidationResult;
import net.ooder.sdk.validator.ValidationReportGenerator;

public class ValidateCommand implements CliCommand {
    
    private int level = 4;
    private String path = ".";
    private String format = "yaml";
    
    public ValidateCommand(String[] args) {
        parseArgs(args);
    }
    
    private void parseArgs(String[] args) {
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--level")) {
                level = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("--path")) {
                path = args[i + 1];
                i++;
            } else if (args[i].equals("--format")) {
                format = args[i + 1];
                i++;
            }
        }
    }
    
    @Override
    public void execute() {
        try {
            ScenePackage scene = ScenePackage.load(path);
            SceneValidator validator = new SceneValidatorImpl();
            
            ValidationResult result = validator.validateLevel(scene, level);
            
            // 这里应该实现报告生成逻辑
            System.out.println("Validation Result:");
            System.out.println("=================");
            System.out.println("Valid: " + result.isValid());
            System.out.println("Total Checks: " + result.getTotalChecks());
            System.out.println("Passed: " + result.getPassed());
            System.out.println("Warnings: " + result.getWarnings());
            System.out.println("Errors: " + result.getErrors());
            System.out.println("Score: " + result.getScore());
            
            if (!result.isValid()) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}