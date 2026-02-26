package net.ooder.codegen.generator;

import net.ooder.codegen.cli.InterfaceDefinition;

/**
 * Driver 代码生成器
 */
public class DriverGenerator {
    
    public String generate(InterfaceDefinition interfaceDef, String packageName) {
        StringBuilder code = new StringBuilder();
        
        // 包声明
        code.append("package ").append(packageName).append(".driver;\n\n");
        
        // 导入
        code.append("import net.ooder.scene.core.Driver;\n");
        code.append("import net.ooder.scene.core.DriverContext;\n");
        code.append("import net.ooder.scene.core.InterfaceDefinition;\n\n");
        
        // 类声明
        code.append("public class ").append(interfaceDef.getSceneId()).append("Driver implements Driver {\n\n");
        
        // 字段
        code.append("    private InterfaceDefinition interfaceDef;\n");
        code.append("    private DriverContext context;\n\n");
        
        // 构造函数
        code.append("    public ").append(interfaceDef.getSceneId()).append("Driver(InterfaceDefinition interfaceDef) {\n");
        code.append("        this.interfaceDef = interfaceDef;\n");
        code.append("    }\n\n");
        
        // 初始化方法
        code.append("    @Override\n");
        code.append("    public void initialize(DriverContext context) {\n");
        code.append("        this.context = context;\n");
        code.append("        // TODO: Initialize driver\n");
        code.append("    }\n\n");
        
        // 调用方法
        code.append("    @Override\n");
        code.append("    public Object invoke(String capabilityId, java.util.Map<String, Object> params) {\n");
        code.append("        // TODO: Implement capability invocation\n");
        code.append("        switch (capabilityId) {\n");
        
        if (interfaceDef.getCapabilities() != null) {
            for (InterfaceDefinition.CapabilityDefinition cap : interfaceDef.getCapabilities()) {
                code.append("            case \"").append(cap.getId()).append("\":\n");
                code.append("                return invoke").append(capitalize(cap.getId())).append("(params);\n");
            }
        }
        
        code.append("            default:\n");
        code.append("                throw new UnsupportedOperationException(\"Unknown capability: \" + capabilityId);\n");
        code.append("        }\n");
        code.append("    }\n\n");
        
        // 关闭方法
        code.append("    @Override\n");
        code.append("    public void close() {\n");
        code.append("        // TODO: Cleanup resources\n");
        code.append("    }\n\n");
        
        // 能力实现方法（占位符）
        if (interfaceDef.getCapabilities() != null) {
            for (InterfaceDefinition.CapabilityDefinition cap : interfaceDef.getCapabilities()) {
                code.append("    private Object invoke").append(capitalize(cap.getId())).append("(java.util.Map<String, Object> params) {\n");
                code.append("        // TODO: Implement ").append(cap.getName()).append("\n");
                code.append("        return null;\n");
                code.append("    }\n\n");
            }
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
