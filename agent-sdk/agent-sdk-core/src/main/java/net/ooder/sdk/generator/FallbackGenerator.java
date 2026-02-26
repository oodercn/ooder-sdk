package net.ooder.sdk.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import net.ooder.sdk.core.driver.model.InterfaceDefinition;
import net.ooder.sdk.core.driver.model.SchemaDefinition;

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

        // 遍历 capabilities 列表
        List<InterfaceDefinition.CapabilityDefinition> capabilities = interfaceDef.getCapabilities();
        if (capabilities != null) {
            for (InterfaceDefinition.CapabilityDefinition cap : capabilities) {
                if (cap.getName() != null) {
                    code.append("        caps.add(\"").append(cap.getName()).append("\");");
                    code.append("\n");
                }
            }
        }

        code.append("        return caps;");
        code.append("\n");
        code.append("    }");
        code.append("\n\n");

        // 生成能力方法
        if (capabilities != null) {
            for (InterfaceDefinition.CapabilityDefinition cap : capabilities) {
                generateCapabilityMethod(code, cap);
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

        GeneratedCode generatedCode = new GeneratedCode();
        generatedCode.setFileName(className + ".java");
        generatedCode.setContent(code.toString());
        generatedCode.setType("fallback");

        return generatedCode;
    }

    private void generateCapabilityMethod(StringBuilder code, InterfaceDefinition.CapabilityDefinition cap) {
        if (cap == null || cap.getName() == null) {
            return;
        }

        String methodName = toMethodName(cap.getName());

        code.append("    /**\n");
        code.append("     * ").append(cap.getDescription() != null ? cap.getDescription() : cap.getName()).append("\n");
        code.append("     */\n");
        code.append("    @Override\n");
        code.append("    public java.util.concurrent.CompletableFuture<Object> ").append(methodName).append("(Map<String, Object> params) {");
        code.append("\n");
        code.append("        // 离线降级实现：返回本地数据或默认值\n");
        code.append("        return java.util.concurrent.CompletableFuture.completedFuture(dataStore.getOrDefault(\"").append(cap.getName()).append("\", null));");
        code.append("\n");
        code.append("    }");
        code.append("\n\n");
    }

    private void generateMethod(StringBuilder code, InterfaceDefinition.MethodDefinition method) {
        if (method == null || method.getName() == null) {
            return;
        }

        code.append("    /**\n");
        code.append("     * ").append(method.getDescription() != null ? method.getDescription() : method.getName()).append("\n");
        code.append("     */\n");
        code.append("    @Override\n");

        String returnType = method.getReturnType() != null ? method.getReturnType() : "void";
        if (!returnType.equals("void") && !returnType.startsWith("CompletableFuture")) {
            returnType = "java.util.concurrent.CompletableFuture<" + returnType + ">";
        }

        code.append("    public ").append(returnType).append(" ").append(method.getName()).append("(");

        // 参数列表
        SchemaDefinition input = method.getInput();
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

        code.append(") {");
        code.append("\n");
        code.append("        // 离线降级实现\n");

        if (!returnType.equals("void")) {
            code.append("        return java.util.concurrent.CompletableFuture.completedFuture(null);");
        }

        code.append("\n");
        code.append("    }");
        code.append("\n\n");
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

    @Override
    public void generateTo(InterfaceDefinition interfaceDef, GeneratorOptions options, java.io.File outputDir) {
        GeneratedCode code = generate(interfaceDef, options);
        java.nio.file.Path filePath = java.nio.file.Paths.get(outputDir.getAbsolutePath(), code.getFilePath() != null ? code.getFilePath() : code.getFileName());

        try {
            java.nio.file.Files.createDirectories(filePath.getParent());
            java.nio.file.Files.write(filePath, code.getContent().getBytes());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to generate fallback code", e);
        }
    }
}
