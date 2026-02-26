package net.ooder.codegen.generator;

import net.ooder.codegen.cli.InterfaceDefinition;

/**
 * Skill 接口代码生成器
 */
public class SkillInterfaceGenerator {
    
    public String generate(InterfaceDefinition interfaceDef, String packageName) {
        StringBuilder code = new StringBuilder();
        
        // 包声明
        code.append("package ").append(packageName).append(".api;\n\n");
        
        // 导入
        code.append("import java.util.Map;\n\n");
        
        // 接口声明
        code.append("/**\n");
        code.append(" * ").append(interfaceDef.getName()).append(" Skill 接口\n");
        if (interfaceDef.getDescription() != null) {
            code.append(" * ").append(interfaceDef.getDescription()).append("\n");
        }
        code.append(" */\n");
        code.append("public interface ").append(interfaceDef.getSceneId()).append("Skill {\n\n");
        
        // 版本常量
        code.append("    String VERSION = \"").append(interfaceDef.getVersion()).append("\";\n\n");
        
        // 能力方法
        if (interfaceDef.getCapabilities() != null) {
            for (InterfaceDefinition.CapabilityDefinition cap : interfaceDef.getCapabilities()) {
                code.append("    /**\n");
                code.append("     * ").append(cap.getName()).append("\n");
                if (cap.getDescription() != null) {
                    code.append("     * ").append(cap.getDescription()).append("\n");
                }
                code.append("     *\n");
                code.append("     * @param params 参数\n");
                code.append("     * @return 结果\n");
                code.append("     */\n");
                code.append("    ").append(cap.getOutputType()).append(" ").append(cap.getId()).append("(Map<String, Object> params);\n\n");
            }
        }
        
        code.append("}\n");
        
        return code.toString();
    }
}
