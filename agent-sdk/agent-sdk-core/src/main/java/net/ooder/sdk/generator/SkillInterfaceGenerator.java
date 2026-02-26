package net.ooder.sdk.generator;

import net.ooder.sdk.core.driver.model.InterfaceDefinition;
import net.ooder.sdk.core.driver.model.SchemaDefinition;

import java.util.List;
import java.util.Map;

/**
 * Skill 接口代码生成器
 */
public class SkillInterfaceGenerator {

    /**
     * 生成 Skill 接口代码
     */
    public String generate(InterfaceDefinition interfaceDef) {
        StringBuilder code = new StringBuilder();

        // 包声明
        code.append("package net.ooder.sdk.generated;\n\n");

        // 导入语句
        code.append("import java.util.List;\n");
        code.append("import java.util.Map;\n");
        code.append("import java.util.concurrent.CompletableFuture;\n\n");

        // 类注释
        code.append("/**\n");
        code.append(" * ").append(interfaceDef.getInterfaceName()).append(" Skill 接口\n");
        code.append(" * ").append(interfaceDef.getDescription()).append("\n");
        code.append(" */\n");

        // 接口声明
        code.append("public interface ").append(toClassName(interfaceDef.getInterfaceName())).append(" {\n\n");

        // 基本方法
        code.append("    String getSkillId();\n\n");
        code.append("    String getSkillName();\n\n");
        code.append("    String getSkillVersion();\n\n");
        code.append("    List<String> getCapabilities();\n\n");

        // 生成能力方法
        List<InterfaceDefinition.CapabilityDefinition> capabilities = interfaceDef.getCapabilities();
        if (capabilities != null) {
            for (InterfaceDefinition.CapabilityDefinition cap : capabilities) {
                generateCapabilityMethods(code, cap);
            }
        }

        // 生成普通方法
        List<InterfaceDefinition.MethodDefinition> methods = interfaceDef.getMethods();
        if (methods != null) {
            for (InterfaceDefinition.MethodDefinition method : methods) {
                generateMethod(code, method);
            }
        }

        code.append("}\n");

        return code.toString();
    }

    private void generateCapabilityMethods(StringBuilder code, InterfaceDefinition.CapabilityDefinition cap) {
        if (cap == null || cap.getName() == null) {
            return;
        }

        code.append("    // ==================== ").append(cap.getName()).append(" ====================\n\n");

        // 从 capability 的 parameters 生成方法
        Map<String, Object> params = cap.getParameters();
        if (params != null) {
            code.append("    /**\n");
            code.append("     * ").append(cap.getDescription() != null ? cap.getDescription() : cap.getName()).append("\n");
            code.append("     */\n");
            code.append("    CompletableFuture<Object> ").append(toMethodName(cap.getName())).append("(Map<String, Object> params);\n\n");
        }
    }

    private void generateMethod(StringBuilder code, InterfaceDefinition.MethodDefinition method) {
        if (method == null || method.getName() == null) {
            return;
        }

        code.append("    /**\n");
        code.append("     * ").append(method.getDescription() != null ? method.getDescription() : method.getName()).append("\n");
        code.append("     *\n");

        // 参数文档
        SchemaDefinition input = method.getInput();
        if (input != null && input.getProperties() != null) {
            for (Map.Entry<String, SchemaDefinition> propEntry : input.getProperties().entrySet()) {
                String propName = propEntry.getKey();
                SchemaDefinition propSchema = propEntry.getValue();
                String propDesc = propSchema.getDescription() != null ? propSchema.getDescription() : propName;
                code.append("     * @param ").append(propName).append(" ").append(propDesc).append("\n");
            }
        }

        code.append("     * @return ").append(method.getOutput() != null ? "结果" : "void").append("\n");
        code.append("     */\n");

        // 方法签名
        String returnType = method.getReturnType() != null ? method.getReturnType() : "void";
        if (!returnType.equals("void") && !returnType.startsWith("CompletableFuture")) {
            returnType = "CompletableFuture<" + returnType + ">";
        }

        code.append("    ").append(returnType).append(" ").append(method.getName()).append("(");

        // 参数列表
        if (input != null && input.getProperties() != null && !input.getProperties().isEmpty()) {
            boolean first = true;
            for (Map.Entry<String, SchemaDefinition> propEntry : input.getProperties().entrySet()) {
                if (!first) {
                    code.append(", ");
                }
                String propName = propEntry.getKey();
                SchemaDefinition propSchema = propEntry.getValue();
                String propType = propSchema.getType() != null ? propSchema.getType() : "Object";
                code.append(propType).append(" ").append(propName);
                first = false;
            }
        } else {
            code.append("Map<String, Object> params");
        }

        code.append(");\n\n");
    }

    private String toClassName(String name) {
        if (name == null || name.isEmpty()) {
            return "SkillInterface";
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpper = true;
        for (char c : name.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                nextUpper = true;
            } else if (nextUpper) {
                result.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String toMethodName(String name) {
        if (name == null || name.isEmpty()) {
            return "execute";
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        for (char c : name.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                nextUpper = true;
            } else if (nextUpper) {
                result.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    public void generateTo(InterfaceDefinition interfaceDef, GeneratorOptions options, java.io.File outputDir) {
        String code = generate(interfaceDef);
        String className = toClassName(interfaceDef.getInterfaceName());
        java.nio.file.Path filePath = java.nio.file.Paths.get(outputDir.getAbsolutePath(), "net/ooder/sdk/generated/", className + ".java");

        try {
            java.nio.file.Files.createDirectories(filePath.getParent());
            java.nio.file.Files.write(filePath, code.getBytes());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to generate interface code", e);
        }
    }
}
