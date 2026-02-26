package net.ooder.codegen.cli;

import net.ooder.codegen.generator.DriverGenerator;
import net.ooder.codegen.generator.SkillInterfaceGenerator;
import net.ooder.codegen.generator.FallbackGenerator;

import java.io.File;

/**
 * 代码生成器运行器
 */
public class CodeGeneratorRunner {
    
    public void run(String inputFile, String outputDir, String packageName) throws Exception {
        // 验证输入文件
        File input = new File(inputFile);
        if (!input.exists()) {
            throw new IllegalArgumentException("Input file not found: " + inputFile);
        }
        
        // 创建输出目录
        File output = new File(outputDir);
        if (!output.exists()) {
            output.mkdirs();
        }
        
        // 解析 YAML 接口定义
        InterfaceDefinition interfaceDef = parseInterfaceDefinition(input);
        
        // 生成 Driver
        DriverGenerator driverGenerator = new DriverGenerator();
        String driverCode = driverGenerator.generate(interfaceDef, packageName);
        writeToFile(output, interfaceDef.getSceneId() + "Driver.java", driverCode);
        
        // 生成 Skill 接口
        SkillInterfaceGenerator skillGenerator = new SkillInterfaceGenerator();
        String skillCode = skillGenerator.generate(interfaceDef, packageName);
        writeToFile(output, interfaceDef.getSceneId() + "Skill.java", skillCode);
        
        // 生成 Fallback
        FallbackGenerator fallbackGenerator = new FallbackGenerator();
        String fallbackCode = fallbackGenerator.generate(interfaceDef, packageName);
        writeToFile(output, interfaceDef.getSceneId() + "Fallback.java", fallbackCode);
    }
    
    private InterfaceDefinition parseInterfaceDefinition(File input) {
        // 解析 YAML 文件
        // 这里简化实现，实际需要读取 YAML 并解析
        return new InterfaceDefinition();
    }
    
    private void writeToFile(File outputDir, String fileName, String content) throws Exception {
        File file = new File(outputDir, fileName);
        try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
            writer.write(content);
        }
        System.out.println("Generated: " + file.getAbsolutePath());
    }
}
