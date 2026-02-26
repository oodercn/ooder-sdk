package net.ooder.sdk.cli;

import net.ooder.sdk.generator.*;
import net.ooder.sdk.core.driver.model.InterfaceDefinition;

import java.io.File;

public class GenerateCommand implements CliCommand {
    
    private String interfacePath = "META-INF/scene/interface.yaml";
    private String outputPath = "./src/main/java";
    private boolean generateAll = true;
    private boolean generateDriver = false;
    private boolean generateSkill = false;
    private boolean generateFallback = false;
    private boolean generateTests = false;
    
    public GenerateCommand(String[] args) {
        parseArgs(args);
    }
    
    private void parseArgs(String[] args) {
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--interface")) {
                interfacePath = args[i + 1];
                i++;
            } else if (args[i].equals("--output")) {
                outputPath = args[i + 1];
                i++;
            } else if (args[i].equals("--driver")) {
                generateDriver = true;
                generateAll = false;
            } else if (args[i].equals("--skill")) {
                generateSkill = true;
                generateAll = false;
            } else if (args[i].equals("--fallback")) {
                generateFallback = true;
                generateAll = false;
            } else if (args[i].equals("--tests")) {
                generateTests = true;
                generateAll = false;
            }
        }
    }
    
    @Override
    public void execute() {
        try {
            InterfaceParser parser = new net.ooder.sdk.generator.impl.InterfaceParserImpl();
            InterfaceDefinition interfaceDef = parser.parse(
                new File(interfacePath)
            );
            
            GeneratorOptions options = new GeneratorOptions();
            options.setCategory(interfaceDef.getSceneId());
            options.setVersion(interfaceDef.getVersion());
            options.setPackageName("net.ooder.sdk.skills." + interfaceDef.getSceneId().toLowerCase());
            options.setLanguage("java");
            
            File outputDir = new File(outputPath);
            
            if (generateAll || generateDriver) {
                new DriverGenerator().generateTo(interfaceDef, options, outputDir);
            }
            if (generateAll || generateSkill) {
                new SkillInterfaceGenerator().generateTo(interfaceDef, options, outputDir);
            }
            if (generateAll || generateFallback) {
                new FallbackGenerator().generateTo(interfaceDef, options, outputDir);
            }
            
            System.out.println("代码生成完成: " + outputDir.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("代码生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}