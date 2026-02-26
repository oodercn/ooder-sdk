package net.ooder.codegen.cli;

import org.apache.commons.cli.*;

/**
 * 代码生成器 CLI 入口
 */
public class CodeGenCli {
    
    public static void main(String[] args) {
        Options options = new Options();
        
        // 输入文件
        options.addOption(Option.builder("i")
                .longOpt("input")
                .desc("Input YAML interface definition file")
                .hasArg()
                .required()
                .build());
        
        // 输出目录
        options.addOption(Option.builder("o")
                .longOpt("output")
                .desc("Output directory for generated code")
                .hasArg()
                .required()
                .build());
        
        // 包名
        options.addOption(Option.builder("p")
                .longOpt("package")
                .desc("Base package name")
                .hasArg()
                .required()
                .build());
        
        // 帮助
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Print help")
                .build());
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("h")) {
                formatter.printHelp("ooder-codegen-cli", options);
                return;
            }
            
            String inputFile = cmd.getOptionValue("i");
            String outputDir = cmd.getOptionValue("o");
            String packageName = cmd.getOptionValue("p");
            
            System.out.println("Ooder Code Generator CLI");
            System.out.println("========================");
            System.out.println("Input: " + inputFile);
            System.out.println("Output: " + outputDir);
            System.out.println("Package: " + packageName);
            
            // 执行代码生成
            CodeGeneratorRunner runner = new CodeGeneratorRunner();
            runner.run(inputFile, outputDir, packageName);
            
            System.out.println("Code generation completed successfully!");
            
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ooder-codegen-cli", options);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Code generation failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
