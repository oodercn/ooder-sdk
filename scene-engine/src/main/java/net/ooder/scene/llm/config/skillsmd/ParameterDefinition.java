package net.ooder.scene.llm.config.skillsmd;

import java.util.List;

/**
 * 参数定义
 *
 * @author ooder
 * @since 2.4
 */
public class ParameterDefinition {

    private String name;
    private String type;
    private boolean required;
    private String description;
    private List<String> enumValues;
    private String defaultValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isEnum() {
        return enumValues != null && !enumValues.isEmpty();
    }

    public String getJsonSchemaType() {
        if (type == null) return "string";
        
        String lowerType = type.toLowerCase();
        switch (lowerType) {
            case "string":
            case "str":
                return "string";
            case "integer":
            case "int":
            case "long":
                return "integer";
            case "number":
            case "float":
            case "double":
                return "number";
            case "boolean":
            case "bool":
                return "boolean";
            case "array":
            case "list":
                return "array";
            case "object":
            case "map":
                return "object";
            default:
                if (lowerType.startsWith("enum")) {
                    return "string";
                }
                return "string";
        }
    }
}
