package net.ooder.sdk.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.ooder.sdk.skill.driver.InterfaceDefinition;

public class DriverGenerator implements CodeGenerator {
    
    @Override
    public GeneratedCode generate(InterfaceDefinition interfaceDef, GeneratorOptions options) {
        StringBuilder code = new StringBuilder();
        
        String category = options.getCategory();
        String packageName = options.getPackageName();
        String className = category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase() + "Driver";
        
        code.append("package ").append(packageName).append(";");
        code.append("\n\n");
        
        code.append("import net.ooder.scene.core.Driver;");
        code.append("\n");
        code.append("import net.ooder.scene.core.DriverContext;");
        code.append("\n");
        code.append("import net.ooder.scene.core.InterfaceDefinition;");
        code.append("\n\n");
        
        code.append("public class ").append(className).append(" implements Driver {");
        code.append("\n\n");
        
        code.append("    private static final String CATEGORY = \"").append(category.toUpperCase()).append("\";\n");
        code.append("    private static final String VERSION = \"").append(options.getVersion()).append("\";\n");
        code.append("\n");
        
        code.append("    private DriverContext context;\n");
        code.append("    private ").append(category.substring(0, 1).toUpperCase()).append(category.substring(1).toLowerCase()).append("SkillImpl skillImpl;\n");
        code.append("    private ").append(category.substring(0, 1).toUpperCase()).append(category.substring(1).toLowerCase()).append("Capabilities capabilities;\n");
        code.append("    private ").append(category.substring(0, 1).toUpperCase()).append(category.substring(1).toLowerCase()).append("Fallback fallback;\n");
        code.append("\n");
        
        code.append("    @Override\n");
        code.append("    public String getCategory() {\n");
        code.append("        return CATEGORY;\n");
        code.append("    }\n");
        code.append("\n");
        
        code.append("    @Override\n");
        code.append("    public String getVersion() {\n");
        code.append("        return VERSION;\n");
        code.append("    }\n");
        code.append("\n");
        
        code.append("    @Override\n");
        code.append("    public void initialize(DriverContext context) {\n");
        code.append("        this.context = context;\n");
        code.append("        // TODO: 实现初始化逻辑\n");
        code.append("    }\n");
        code.append("\n");
        
        for (InterfaceDefinition.CapabilityDefinition cap : interfaceDef.getCapabilities().values()) {
            code.append("    // ").append(cap.getDescription()).append("\n");
        }
        
        code.append("}\n");
        
        GeneratedCode result = new GeneratedCode();
        result.setFileName(className + ".java");
        result.setFilePath(packageName.replace('.', '/') + "/" + className + ".java");
        result.setContent(code.toString());
        result.setLanguage("java");
        
        return result;
    }
    
    @Override
    public void generateTo(InterfaceDefinition interfaceDef, GeneratorOptions options, File outputDir) {
        GeneratedCode code = generate(interfaceDef, options);
        Path filePath = Paths.get(outputDir.getAbsolutePath(), code.getFilePath());
        
        try {
            // 创建目录结构
            Files.createDirectories(filePath.getParent());
            // 写入文件
            Files.write(filePath, code.getContent().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate driver code", e);
        }
    }
}