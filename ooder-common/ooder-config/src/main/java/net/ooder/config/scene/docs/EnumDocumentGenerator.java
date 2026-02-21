package net.ooder.config.scene.docs;

import net.ooder.config.scene.enums.CapabilityType;
import net.ooder.config.scene.enums.SceneEnvironment;
import net.ooder.config.scene.enums.ServiceType;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnumDocumentGenerator {
    
    public static String generateMarkdown(Class<?> enumClass) {
        EnumDocument doc = enumClass.getAnnotation(EnumDocument.class);
        if (doc == null) {
            return "# " + enumClass.getSimpleName() + "\n\nNo documentation available.";
        }
        
        StringWriter writer = new StringWriter();
        
        writer.write("# " + doc.name() + "\n\n");
        writer.write("**版本**: " + doc.version() + "\n\n");
        writer.write("## 描述\n\n");
        writer.write(doc.description() + "\n\n");
        
        if (!doc.installGuide().isEmpty()) {
            writer.write("## 安装指南\n\n");
            writer.write(doc.installGuide() + "\n\n");
        }
        
        if (!doc.startupGuide().isEmpty()) {
            writer.write("## 启动指南\n\n");
            writer.write(doc.startupGuide() + "\n\n");
        }
        
        if (!doc.configExample().isEmpty()) {
            writer.write("## 配置示例\n\n");
            writer.write("```yaml\n");
            writer.write(doc.configExample());
            writer.write("\n```\n\n");
        }
        
        if (enumClass.isEnum()) {
            writer.write("## 枚举值\n\n");
            writer.write("| Code | 名称 | 描述 |\n");
            writer.write("|------|------|------|\n");
            
            Object[] constants = enumClass.getEnumConstants();
            for (Object constant : constants) {
                try {
                    String code = (String) constant.getClass().getMethod("getCode").invoke(constant);
                    String displayName = (String) constant.getClass().getMethod("getDisplayName").invoke(constant);
                    String description = (String) constant.getClass().getMethod("getDescription").invoke(constant);
                    writer.write("| " + code + " | " + displayName + " | " + description + " |\n");
                } catch (Exception e) {
                    writer.write("| " + constant.toString() + " | - | - |\n");
                }
            }
        }
        
        return writer.toString();
    }
    
    public static Map<String, Object> generateJson(Class<?> enumClass) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        EnumDocument doc = enumClass.getAnnotation(EnumDocument.class);
        if (doc != null) {
            result.put("name", doc.name());
            result.put("version", doc.version());
            result.put("description", doc.description());
            result.put("installGuide", doc.installGuide());
            result.put("startupGuide", doc.startupGuide());
            result.put("configExample", doc.configExample());
        }
        
        if (enumClass.isEnum()) {
            java.util.List<Map<String, Object>> values = new java.util.ArrayList<Map<String, Object>>();
            Object[] constants = enumClass.getEnumConstants();
            
            for (Object constant : constants) {
                Map<String, Object> value = new LinkedHashMap<String, Object>();
                try {
                    value.put("code", constant.getClass().getMethod("getCode").invoke(constant));
                    value.put("displayName", constant.getClass().getMethod("getDisplayName").invoke(constant));
                    value.put("description", constant.getClass().getMethod("getDescription").invoke(constant));
                } catch (Exception e) {
                    value.put("name", constant.toString());
                }
                values.add(value);
            }
            result.put("values", values);
        }
        
        return result;
    }
    
    public static String generateAllMarkdown() {
        StringWriter writer = new StringWriter();
        
        writer.write("# Ooder 场景配置枚举文档\n\n");
        writer.write("---\n\n");
        
        writer.write(generateMarkdown(SceneEnvironment.class));
        writer.write("---\n\n");
        
        writer.write(generateMarkdown(ServiceType.class));
        writer.write("---\n\n");
        
        writer.write(generateMarkdown(CapabilityType.class));
        
        return writer.toString();
    }
    
    public static Map<String, Object> generateAllJson() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("environments", generateJson(SceneEnvironment.class));
        result.put("services", generateJson(ServiceType.class));
        result.put("capabilities", generateJson(CapabilityType.class));
        return result;
    }
}
