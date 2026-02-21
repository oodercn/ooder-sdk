package net.ooder.sdk.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import net.ooder.sdk.skill.driver.InterfaceDefinition;

public class FallbackGenerator implements CodeGenerator {
    
    @Override
    public GeneratedCode generate(InterfaceDefinition interfaceDef, GeneratorOptions options) {
        StringBuilder code = new StringBuilder();
        
        String category = options.getCategory();
        String packageName = options.getPackageName();
        String className = category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase() + "Fallback";
        String skillInterface = category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase() + "Skill";
        
        code.append("package ").append(packageName).append(";");
        code.append("\n\n");
        
        code.append("import java.util.ArrayList;");
        code.append("\n");
        code.append("import java.util.HashMap;");
        code.append("\n");
        code.append("import java.util.List;");
        code.append("\n");
        code.append("import java.util.Map;");
        code.append("\n\n");
        
        code.append("/**");
        code.append("\n");
        code.append(" * " + className + " 离线降级实现\n");
        code.append(" *\n");
        code.append(" * <p>示例实现，请根据实际需求修改</p>\n");
        code.append(" */\n");
        code.append("public class " + className + " implements " + skillInterface + " {");
        code.append("\n\n");
        
        code.append("    private static final String SKILL_ID = \"").append(category.toLowerCase()).append("-fallback\";\n");
        code.append("    private static final String SKILL_NAME = \"").append(category.toUpperCase()).append(" Fallback\";\n");
        code.append("    private static final String SKILL_VERSION = \"").append(options.getVersion()).append("\";\n");
        code.append("\n");
        
        code.append("    // 本地数据存储\n");
        code.append("    private Map<String, Object> dataStore = new HashMap<String, Object>();");
        code.append("\n\n");
        
        code.append("    @Override\n");
        code.append("    public String getSkillId() { return SKILL_ID; }");
        code.append("\n\n");
        code.append("    @Override\n");
        code.append("    public String getSkillName() { return SKILL_NAME; }");
        code.append("\n\n");
        code.append("    @Override\n");
        code.append("    public String getSkillVersion() { return SKILL_VERSION; }");
        code.append("\n\n");
        
        code.append("    @Override\n");
        code.append("    public List<String> getCapabilities() {");
        code.append("\n");
        code.append("        List<String> caps = new ArrayList<String>();");
        code.append("\n");
        for (String capName : interfaceDef.getCapabilities().keySet()) {
            code.append("        caps.add(\"").append(capName).append("\");");
            code.append("\n");
        }
        code.append("        return caps;");
        code.append("\n");
        code.append("    }");
        code.append("\n\n");
        
        for (Map.Entry<String, InterfaceDefinition.CapabilityDefinition> capEntry : interfaceDef.getCapabilities().entrySet()) {
            InterfaceDefinition.CapabilityDefinition cap = capEntry.getValue();
            for (Map.Entry<String, InterfaceDefinition.MethodDefinition> methodEntry : cap.getMethods().entrySet()) {
                InterfaceDefinition.MethodDefinition method = methodEntry.getValue();
                code.append("    @Override\n");
                String returnType = getJavaType(method.getOutput());
                String params = generateParams(method.getInput());
                code.append("    public " + returnType + " " + method.getName() + "(" + params + ") {");
                code.append("\n");
                code.append("        // TODO: 实现降级逻辑\n");
                if (!"void".equals(returnType)) {
                    code.append("        return null;");
                    code.append("\n");
                }
                code.append("    }");
                code.append("\n\n");
            }
        }
        
        code.append("    @Override\n");
        code.append("    public Object invoke(String capability, Map<String, Object> params) {");
        code.append("\n");
        code.append("        // TODO: 实现通用调用\n");
        code.append("        return null;");
        code.append("\n");
        code.append("    }");
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
            throw new RuntimeException("Failed to generate fallback code", e);
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