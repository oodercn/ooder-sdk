package net.ooder.sdk.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import net.ooder.sdk.skill.driver.InterfaceDefinition;

public class SkillInterfaceGenerator implements CodeGenerator {
    
    @Override
    public GeneratedCode generate(InterfaceDefinition interfaceDef, GeneratorOptions options) {
        StringBuilder code = new StringBuilder();
        
        String category = options.getCategory();
        String packageName = options.getPackageName();
        String className = category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase() + "Skill";
        
        code.append("package ").append(packageName).append(";");
        code.append("\n\n");
        
        code.append("import java.util.List;");
        code.append("\n");
        code.append("import java.util.Map;");
        code.append("\n\n");
        
        code.append("/**");
        code.append("\n");
        code.append(" * " + className + " 技能接口\n");
        code.append(" *\n");
        code.append(" * <p>自动生成，请勿手动修改</p>\n");
        code.append(" */\n");
        code.append("public interface " + className + " {");
        code.append("\n\n");
        
        code.append("    String getSkillId();");
        code.append("\n\n");
        code.append("    String getSkillName();");
        code.append("\n\n");
        code.append("    String getSkillVersion();");
        code.append("\n\n");
        code.append("    List<String> getCapabilities();");
        code.append("\n\n");
        
        for (Map.Entry<String, InterfaceDefinition.CapabilityDefinition> capEntry : interfaceDef.getCapabilities().entrySet()) {
            InterfaceDefinition.CapabilityDefinition cap = capEntry.getValue();
            code.append("    // ==================== " + cap.getDescription() + " ====================\n");
            code.append("\n");
            
            for (Map.Entry<String, InterfaceDefinition.MethodDefinition> methodEntry : cap.getMethods().entrySet()) {
                InterfaceDefinition.MethodDefinition method = methodEntry.getValue();
                code.append("    /**\n");
                code.append("     * " + method.getDescription() + "\n");
                code.append("     *\n");
                
                if (method.getInput() != null && method.getInput().getProperties() != null) {
                    for (Map.Entry<String, InterfaceDefinition.PropertyDefinition> propEntry : method.getInput().getProperties().entrySet()) {
                        String propName = propEntry.getKey();
                        String propDesc = propEntry.getValue().getDescription();
                        code.append("     * @param " + propName + " " + propDesc + "\n");
                    }
                }
                
                code.append("     * @return " + (method.getOutput() != null ? "结果" : "void") + "\n");
                code.append("     */\n");
                
                String returnType = getJavaType(method.getOutput());
                String params = generateParams(method.getInput());
                
                code.append("    " + returnType + " " + method.getName() + "(" + params + ");");
                code.append("\n\n");
            }
        }
        
        code.append("    Object invoke(String capability, Map<String, Object> params);");
        code.append("\n");
        code.append("}");
        
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
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, code.getContent().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate skill interface code", e);
        }
    }
    
    private String getJavaType(InterfaceDefinition.SchemaDefinition schema) {
        if (schema == null) return "void";
        switch (schema.getType()) {
            case "string": return "String";
            case "integer": return "Integer";
            case "long": return "Long";
            case "boolean": return "Boolean";
            case "array": return "List<?>";
            case "object": return "Map<String, Object>";
            default: return "Object";
        }
    }
    
    private String generateParams(InterfaceDefinition.SchemaDefinition input) {
        if (input == null || input.getProperties() == null) {
            return "";
        }
        
        StringBuilder params = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<String, InterfaceDefinition.PropertyDefinition> propEntry : input.getProperties().entrySet()) {
            if (!first) params.append(", ");
            first = false;
            
            String propName = propEntry.getKey();
            String type = getJavaType(propEntry.getValue().getSchema());
            params.append(type).append(" " + propName);
        }
        
        return params.toString();
    }
}