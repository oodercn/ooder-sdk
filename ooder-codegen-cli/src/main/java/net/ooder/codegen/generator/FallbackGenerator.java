package net.ooder.codegen.generator;

import net.ooder.codegen.cli.InterfaceDefinition;

/**
 * Fallback 降级实现代码生成器
 */
public class FallbackGenerator {
    
    public String generate(InterfaceDefinition interfaceDef, String packageName) {
        StringBuilder code = new StringBuilder();
        
        // 包声明
        code.append("package ").append(packageName).append(".fallback;\n\n");
        
        // 导入
        code.append("import ").append(packageName).append(".api.").append(interfaceDef.getSceneId()).append("Skill;\n");
        code.append("import java.util.Map;\n\n");
        
        // 类声明
        code.append("/**\n");
        code.append(" * ").append(interfaceDef.getName()).append(" 降级实现\n");
        code.append(" * 当远程服务不可用时使用本地数据\n");
        code.append(" */\n");
        code.append("public class ").append(interfaceDef.getSceneId()).append("Fallback implements ").append(interfaceDef.getSceneId()).append("Skill {\n\n");
        
        // 能力实现
        if (interfaceDef.getCapabilities() != null) {
            for (InterfaceDefinition.CapabilityDefinition cap : interfaceDef.getCapabilities()) {
                code.append("    @Override\n");
                code.append("    public ").append(cap.getOutputType()).append(" ").append(cap.getId()).append("(Map<String, Object> params) {\n");
                code.append("        // Fallback: Return default value or cached data\n");
                code.append("        // TODO: Implement fallback logic\n");
                code.append("        return ").append(getDefaultReturn(cap.getOutputType())).append(";\n");
                code.append("    }\n\n");
            }
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    private String getDefaultReturn(String type) {
        if (type == null) {
            return "null";
        }
        switch (type) {
            case "boolean":
            case "Boolean":
                return "false";
            case "int":
            case "Integer":
                return "0";
            case "long":
            case "Long":
                return "0L";
            case "double":
            case "Double":
                return "0.0";
            case "String":
                return "\"\"";
            default:
                return "null";
        }
    }
}
